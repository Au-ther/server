package beans;

import java.io.Serializable;

public class FileChunk implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileId;          // 文件唯一标识
    private int chunkIndex;         // 分片索引
    private int totalChunks;        // 总分片数
    private byte[] data;            // 分片数据
    private String fileName;        // 文件名
    private long fileSize;          // 文件大小
    private String md5;             // 文件MD5值
    private int userId;             // 上传用户ID

    public FileChunk() {
    }

    public FileChunk(String fileId, int chunkIndex, int totalChunks, byte[] data,
                     String fileName, long fileSize, String md5, int userId) {
        this.fileId = fileId;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
        this.data = data;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.md5 = md5;
        this.userId = userId;
    }

    // Getters and Setters
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
}