package beans;

import java.io.Serializable;


public class UserState implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int state;
	private String SName;
	private Integer qqnum;
	
	public UserState() {
		
	}

	public UserState(String SName,int state) {
		this.state = state;
		this.SName = SName;
	}
	
	public static UserState ONLINESTATE = new UserState("在线",0);
	public static UserState HIDDENSTATE = new UserState("隐身",1);
	public static UserState DEPARTURESTATE = new UserState("离开",2);
	public static UserState BUSYSTATE = new UserState("繁忙",3);
	public static UserState OFFLIENSTATE = new UserState("离线",4);
	
	public String toString() {
		return SName;
	}

	public int getState() {
		return state;
	}
	public Integer getQQnum() {
		return qqnum;
	}

	public void setQQnum(Integer qqnum) {
		this.qqnum = qqnum;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getSName() {
		return SName;
	}

	public void setSName(String name) {
		SName = name;
	}
	
	/**
	 * 根据用户状态返回状态信息。
	 * @param state 状态。
	 * @return 状态信息。
	 */
	public static String getStateName(int state){
		String sName = state==DEPARTURESTATE.getState()?DEPARTURESTATE.getSName():
			(state==BUSYSTATE.getState()?BUSYSTATE.getSName():
			(state==HIDDENSTATE.getState()?HIDDENSTATE.getSName():
			(state==ONLINESTATE.getState()?ONLINESTATE.getSName():
			(OFFLIENSTATE.getSName()))));
		return sName;
	}
}
