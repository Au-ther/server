package beans;

import java.io.Serializable;
import java.util.Vector;

public class User implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private Integer						id;
	private Integer						qqnum;
	private String						realname;
	private String						nickname;
	private Integer						sex								= 0;
	private Integer						age								= 0;
	private String						password;
	private String						signature					= "点击编辑个性签名";
	private String						email;
	private Integer						photo							= 1;
	private Integer						state							= 4;
	private Vector<Integer>		listFriend				= new Vector<Integer>();
	private String						registerTime;
	private Long							onhours;
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

	public Long getOnhours() {
		return onhours;
	}

	public void setOnhours(Long onhours) {
		this.onhours = onhours;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getQQnum() {
		return qqnum;
	}

	public void setQQnum(Integer qqnum) {
		this.qqnum = qqnum;
	}

	public Vector<Integer> getListFriend() {
		return listFriend;
	}

	public void setListFriend(Vector<Integer> listFriend) {
		this.listFriend = listFriend;
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

	public Integer getPhoto() {
		return photo;
	}

	public void setPhoto(Integer photo) {
		this.photo = photo;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
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

	@Override
	public String toString() {
		return "User [id=" + id + ", qqnum=" + qqnum + ", realname=" + realname + ", nickname=" + nickname + ", sex=" + sex + ", age=" + age
				+ ", password=" + password + ", signature=" + signature + ", email=" + email + ", photo=" + photo + ", state=" + state + ", listFriend="
				+ listFriend + ", registerTime=" + registerTime + "]";
	}

}
