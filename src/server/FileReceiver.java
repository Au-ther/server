package server;

import beans.FileChunk;
import beans.QQMessage;
import beans.FastUploadResponse;
import org.apache.log4j.Logger;
import server.dao.FileDAO;
import server.dao.FileDAOByMysql;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FileReceiver {
    private static final Logger log = Logger.getLogger(FileReceiver.class);
    private static final String UPLOAD_DIR = "uploadFiles/";
    
    // 存储文件分片信息
    private static final Map<String, FileChunk[]> chunkMap = new ConcurrentHashMap<>();
    // 存储文件上传进度
    private static final Map<String, AtomicInteger> progressMap = new ConcurrentHashMap<>();
    // 存储临时文件路径
    private static final Map<String, String> tempFileMap = new ConcurrentHashMap<>();
    // 文件DAO
    private final FileDAO fileDAO = new FileDAOByMysql();
    
    static {
        // 创建上传目录
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        File tempDir = new File(UPLOAD_DIR + "temp/");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }
    
    /**
     * 处理秒传请求
     * @param message 包含MD5的消息
     * @return 秒传响应
     */
    public FastUploadResponse handleFastUploadRequest(QQMessage message) {
        if (message.getType() != 502) { // 假设502为秒传请求类型
            return null;
        }
        
        FastUploadResponse response = new FastUploadResponse();
        
        try {
            Map<String, Object> data = (Map<String, Object>) message.getObj();
            String md5 = (String) data.get("md5");
            String fileName = (String) data.get("fileName");
            int userId = (Integer) data.get("userId");
            
            // 查询文件是否已存在
            boolean exists = fileDAO.isFileExist(md5);
            response.setSuccess(exists);
            
            if (exists) {
                log.info("文件秒传成功: " + fileName + ", MD5: " + md5);
                // 如果文件存在，增加引用计数
                fileDAO.incrementReferenceCount(md5);
                // 获取文件路径供客户端记录
                String filePath = fileDAO.getFilePathByMd5(md5);
                response.setFilePath(filePath);
                response.setMessage("文件秒传成功");
            } else {
                log.info("文件不存在，需要上传: " + fileName + ", MD5: " + md5);
                response.setMessage("文件不存在，需要上传");
            }
        } catch (Exception e) {
            log.error("处理秒传请求失败: " + e.getMessage());
            response.setSuccess(false);
            response.setMessage("处理秒传请求失败: " + e.getMessage());
        }
        
        return response;
    }
    
    public void handleFileChunk(QQMessage message) {
        if (message.getType() != 501) {
            return;
        }
        
        FileChunk chunk = (FileChunk) message.getObj();
        String fileId = chunk.getFileId();
        
        try {
            // 处理文件分片
            processChunk(chunk);
            
            // 更新进度
            updateProgress(fileId);
            
            // 检查是否完成
            if (isUploadComplete(fileId)) {
                assembleFile(fileId);
                cleanup(fileId);
            }
        } catch (Exception e) {
            log.error("Error handling file chunk: " + e.getMessage());
            cleanup(fileId);
        }
    }
    
    private void processChunk(FileChunk chunk) throws IOException {
        String fileId = chunk.getFileId();
        String tempFilePath = getTempFilePath(fileId);
        
        synchronized (chunkMap) {
            // 初始化分片数组
            if (!chunkMap.containsKey(fileId)) {
                chunkMap.put(fileId, new FileChunk[chunk.getTotalChunks()]);
                progressMap.put(fileId, new AtomicInteger(0));
                tempFileMap.put(fileId, tempFilePath);
                
                // 创建临时文件
                File tempFile = new File(tempFilePath);
                if (!tempFile.getParentFile().exists()) {
                    tempFile.getParentFile().mkdirs();
                }
            }
            
            // 存储分片
            FileChunk[] chunks = chunkMap.get(fileId);
            chunks[chunk.getChunkIndex()] = chunk;
            
            // 写入临时文件
            try (RandomAccessFile raf = new RandomAccessFile(tempFilePath, "rw")) {
                raf.seek(chunk.getChunkIndex() * chunk.getData().length);
                raf.write(chunk.getData());
            }
        }
    }
    
    private void updateProgress(String fileId) {
        AtomicInteger progress = progressMap.get(fileId);
        if (progress != null) {
            progress.incrementAndGet();
            log.info("File " + fileId + " upload progress: " + progress.get() + "/" + 
                    chunkMap.get(fileId).length);
        }
    }
    
    private boolean isUploadComplete(String fileId) {
        FileChunk[] chunks = chunkMap.get(fileId);
        if (chunks == null) {
            return false;
        }
        
        for (FileChunk chunk : chunks) {
            if (chunk == null) {
                return false;
            }
        }
        return true;
    }
    
    private void assembleFile(String fileId) throws IOException {
        FileChunk[] chunks = chunkMap.get(fileId);
        if (chunks == null || chunks.length == 0) {
            return;
        }
        
        FileChunk firstChunk = chunks[0];
        String fileName = firstChunk.getFileName();
        String filePath = UPLOAD_DIR + fileName;
        String tempFilePath = tempFileMap.get(fileId);
        
        // 验证MD5
        String receivedMd5 = firstChunk.getMd5();
        String calculatedMd5 = calculateMD5(tempFilePath);
        
        if (!receivedMd5.equals(calculatedMd5)) {
            log.error("MD5 verification failed for file: " + fileName);
            throw new IOException("MD5 verification failed");
        }
        
        // 重命名临时文件
        File tempFile = new File(tempFilePath);
        File finalFile = new File(filePath);
        
        if (!tempFile.renameTo(finalFile)) {
            // 如果重命名失败，尝试复制文件
            try (FileInputStream fis = new FileInputStream(tempFile);
                 FileOutputStream fos = new FileOutputStream(finalFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            tempFile.delete();
        }
        
        // 将文件信息保存到数据库
        try {
            int userId = firstChunk.getUserId(); // 假设FileChunk添加了userId字段
            fileDAO.addFileRecord(receivedMd5, fileName, filePath, tempFile.length(), userId);
            log.info("文件记录已添加到数据库: " + fileName + ", MD5: " + receivedMd5);
        } catch (Exception e) {
            log.error("添加文件记录失败: " + e.getMessage());
        }
        
        log.info("File assembled successfully: " + fileName);
    }
    
    private String getTempFilePath(String fileId) {
        return UPLOAD_DIR + "temp/" + fileId + ".tmp";
    }
    
    private void cleanup(String fileId) {
        // 删除临时文件
        String tempFilePath = tempFileMap.remove(fileId);
        if (tempFilePath != null) {
            new File(tempFilePath).delete();
        }
        
        // 清理内存中的数据
        chunkMap.remove(fileId);
        progressMap.remove(fileId);
    }
    
    private String calculateMD5(String filePath) throws IOException {
        try (InputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[8192];
            java.security.MessageDigest complete = java.security.MessageDigest.getInstance("MD5");
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
            byte[] md5Bytes = complete.digest();
            
            StringBuilder result = new StringBuilder();
            for (byte b : md5Bytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (Exception e) {
            throw new IOException("Error calculating MD5: " + e.getMessage());
        }
    }
    
    // 获取文件上传进度
    public int getUploadProgress(String fileId) {
        AtomicInteger progress = progressMap.get(fileId);
        if (progress != null) {
            FileChunk[] chunks = chunkMap.get(fileId);
            if (chunks != null) {
                return (int) ((progress.get() * 100.0) / chunks.length);
            }
        }
        return 0;
    }
    
    // 取消文件上传
    public void cancelUpload(String fileId) {
        cleanup(fileId);
    }
} 