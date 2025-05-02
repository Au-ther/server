package beans;

import java.io.Serializable;

public class FriendUser implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private Integer						qqnum;
	private String						nickName;
	private String						signature;
	private String						txtRealName;
	private String						email;
	private Integer						age;
	private Integer						sex;
	private Integer						photo;
	private Integer						state;
	private String						rsapublic;
	private String						rsaprivate;

	public Integer getQqnum() {
		return qqnum;
	}

	public void setQqnum(Integer qqnum) {
		this.qqnum = qqnum;
	}

	public String getRsapublic() {
		return rsapublic;
	}

	public void setRsapublic(String rsapublic) {
		this.rsapublic = rsapublic;
	}

	public String getRsaprivate() {
		return rsaprivate;
	}

	public void setRsaprivate(String rsaprivate) {
		this.rsaprivate = rsaprivate;
	}

	public Integer getQQnum() {
		return qqnum;
	}

	public void setQQnum(Integer qqnum) {
		this.qqnum = qqnum;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getPhoto() {
		return photo;
	}

	public void setPhoto(Integer photo) {
		this.photo = photo;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getTxtRealName() {
		return txtRealName;
	}

	public void setTxtRealName(String txtRealName) {
		this.txtRealName = txtRealName;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String toString() {
		return nickName;
	}

	public boolean equals(Object obj) {
		if (obj instanceof FriendUser) {
			FriendUser user = (FriendUser) obj;
			return user.getQQnum().equals(qqnum);
		} else
			return false;
	}
}