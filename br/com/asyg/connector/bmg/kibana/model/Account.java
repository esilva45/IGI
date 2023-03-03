package br.com.asyg.connector.bmg.kibana.model;

public class Account {
	private String username;
	private String password;
	private String fullname;
	private String roles;
	private String email;
	private int status;
	private String[] ergroupid;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFullName() {
		return fullname;
	}
	public void setFullName(String fullname) {
		this.fullname = fullname;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String[] getGroupId() {
		return ergroupid;
	}
	public void setGroupId(String[] ergroupid) {
		this.ergroupid = ergroupid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
