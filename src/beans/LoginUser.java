package beans;

import java.io.Serializable;

public class LoginUser implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private Integer						qqnum;
	private String						password;
	private Integer						state;

	public Integer getQQnum() {
		return qqnum;
	}

	public void setQQnum(Integer qqnum) {
		this.qqnum = qqnum;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
}
