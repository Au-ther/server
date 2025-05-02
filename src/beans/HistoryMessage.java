package beans;

import java.io.Serializable;
import java.util.List;

public class HistoryMessage implements Serializable {
	private String friendName;
	private List<Record> list;

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public List<Record> getList() {
		return list;
	}

	public void setList(List<Record> list) {
		this.list = list;
	}

}
