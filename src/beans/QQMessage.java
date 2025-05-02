package beans;

import java.io.Serializable;


public class QQMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer type;
	private Object obj;
	
	public QQMessage() {
		
	}
	public QQMessage(Integer type,Object obj) {
		this.type = type;
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Message [type=" + type + ", obj=" + obj + "]";
	}
	
	
}
