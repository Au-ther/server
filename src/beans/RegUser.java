package beans;

import java.io.Serializable;


public class RegUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer qqnum;
	private String realname;
	private String nickname;
	private String password;
	public Integer getQQnum() {
		return qqnum;
	}
	public void setQQnum(Integer qqnum) {
		this.qqnum = qqnum;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	
	
}
