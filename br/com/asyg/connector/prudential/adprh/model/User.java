package br.com.asyg.connector.prudential.adprh.model;

public class User {
	private String uid;
	private String status;
	private String startdate;
	private String enddate;

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStartDate() {
		return startdate;
	}

	public void setStartDate(String startdate) {
		this.startdate = startdate;
	}

	public String getEndDate() {
		return enddate;
	}

	public void setEndDate(String enddate) {
		this.enddate = enddate;
	}

}
