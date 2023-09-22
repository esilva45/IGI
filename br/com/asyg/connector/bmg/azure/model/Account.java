package br.com.asyg.connector.bmg.azure.model;

import org.json.JSONArray;

public class Account {
	private String mail;
	private String phoneNumber;
	private String smsSignInState;
	private JSONArray roleid;
	private int status;
	
	public JSONArray getRoleid() {
		return roleid;
	}
	public void setRoleid(JSONArray roleid) {
		this.roleid = roleid;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getSmsSignInState() {
		return smsSignInState;
	}
	public void setSmsSignInState(String smsSignInState) {
		this.smsSignInState = smsSignInState;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
