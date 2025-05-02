package server.dao;

/**
 * 文件数据访问接口
 */
public interface FileDAO {
    /**
     * 根据MD5值查询文件信息
     * @param md5 文件MD5值
     * @return 文件存储路径，如果不存在则返回null
     */
    String getFilePathByMd5(String md5);
    
    /**
     * 添加文件记录
     * @param md5 文件MD5值
     * @param fileName 文件名
     * @param filePath 文件存储路径
     * @param fileSize 文件大小
     * @param userId 上传用户ID
     * @return 是否添加成功
     */
    boolean addFileRecord(String md5, String fileName, String filePath, long fileSize, int userId);
    
    /**
     * 增加文件引用计数
     * @param md5 文件MD5值
     * @return 是否成功
     */
    boolean incrementReferenceCount(String md5);
    
    /**
     * 查询文件是否已存在
     * @param md5 文件MD5值
     * @return 是否存在
     */
    boolean isFileExist(String md5);
} 