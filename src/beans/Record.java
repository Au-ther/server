package beans;

import java.io.Serializable;

import util.DateUtil;

/**
 * ÁÄÌì¼ÇÂ¼Àà¡£
 */
public class Record implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private Integer						id								= 1;
	private Integer						fromid						= 10000;
	private Integer						toid							= 10000;
	private String						fromName					= "";
	private String						toName						= "";
	private String						sendTime					= DateUtil.getCurrentTime(DateUtil.FULL);
	private String						readTime					= DateUtil.getCurrentTime(DateUtil.FULL);
	private boolean						isRead						= false;
	private String						content						= "";

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getFromid() {
		return fromid;
	}

	public void setFromid(Integer fromid) {
		this.fromid = fromid;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getReadTime() {
		return readTime;
	}

	public void setReadTime(String readTime) {
		this.readTime = readTime;
	}

	public Integer getToid() {
		return toid;
	}

	public void setToid(Integer toid) {
		this.toid = toid;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	@Override
	public String toString() {
		return "Record [id=" + id + ", fromid=" + fromid + ", toid=" + toid + ", fromName=" + fromName + ", toName=" + toName + ", sendTime=" + sendTime
				+ ", readTime=" + readTime + ", isRead=" + isRead + ", content=" + content + "]";
	}


}
