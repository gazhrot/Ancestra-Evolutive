package org.ancestra.evolutive.object.action;

public class ObjectActionResult {
	
	private boolean isOk;
	private boolean send;
	
	public ObjectActionResult() {
		this.isOk = true;
		this.send = true;
	}
	
	public ObjectActionResult(boolean isOk, boolean send) {
		this.isOk = isOk;
		this.send = send;
	}
	
	public boolean isOk() {
		return isOk;
	}
	
	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}
	
	public boolean isSend() {
		return send;
	}
	
	public void setSend(boolean send) {
		this.send = send;
	}
}