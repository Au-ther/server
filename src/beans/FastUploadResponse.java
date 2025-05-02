package beans;

import java.io.Serializable;

/**
 * 文件秒传响应类
 */
public class FastUploadResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;    // 是否成功
    private String message;     // 消息
    private String filePath;    // 文件路径
    
    public FastUploadResponse() {
        this.success = false;
        this.message = "";
        this.filePath = "";
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
} 