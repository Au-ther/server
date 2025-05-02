package beans;

import java.io.Serializable;

public class SendFileBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer qqnum;
	private String filename;
	private User fromUser;
	private String fromip;
	private Integer port;

	public Integer getQqnum() {
		return qqnum;
	}

	public void setQqnum(Integer qqnum) {
		this.qqnum = qqnum;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public String getFromip() {
		return fromip;
	}

	public void setFromip(String fromip) {
		this.fromip = fromip;
	}
	
	public Integer getPort() {
		return port;
	}
	
	public void setPort(Integer port) {
		this.port = port;
	}
}
