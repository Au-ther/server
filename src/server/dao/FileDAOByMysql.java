package server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 文件DAO的MySQL实现
 */
public class FileDAOByMysql implements FileDAO {
    private static final Logger log = Logger.getLogger(FileDAOByMysql.class);
    
    @Override
    public String getFilePathByMd5(String md5) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String filePath = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT file_path FROM file_md5 WHERE file_md5 = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, md5);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                filePath = rs.getString("file_path");
            }
        } catch (SQLException e) {
            log.error("查询文件路径失败: " + e.getMessage());
        } finally {
            DB.close(conn, ps, rs);
        }
        
        return filePath;
    }
    
    @Override
    public boolean addFileRecord(String md5, String fileName, String filePath, long fileSize, int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DB.getConnection();
            String sql = "INSERT INTO file_md5 (file_md5, file_name, file_path, file_size, upload_user_id, upload_time, reference_count) " +
                         "VALUES (?, ?, ?, ?, ?, ?, 1)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, md5);
            ps.setString(2, fileName);
            ps.setString(3, filePath);
            ps.setLong(4, fileSize);
            ps.setInt(5, userId);
            ps.setTimestamp(6, new Timestamp(new Date().getTime()));
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            log.error("添加文件记录失败: " + e.getMessage());
            return false;
        } finally {
            DB.close(conn, ps, null);
        }
    }
    
    @Override
    public boolean incrementReferenceCount(String md5) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DB.getConnection();
            String sql = "UPDATE file_md5 SET reference_count = reference_count + 1 WHERE file_md5 = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, md5);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            log.error("增加文件引用计数失败: " + e.getMessage());
            return false;
        } finally {
            DB.close(conn, ps, null);
        }
    }
    
    @Override
    public boolean isFileExist(String md5) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT COUNT(*) as count FROM file_md5 WHERE file_md5 = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, md5);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
            return false;
        } catch (SQLException e) {
            log.error("查询文件是否存在失败: " + e.getMessage());
            return false;
        } finally {
            DB.close(conn, ps, rs);
        }
    }
} 