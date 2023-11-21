package br.com.asyg.connector.bmg.voll.model;

public class Account {
	private String id;
	private String name;
	private String email;
	private String groupid;
	private String profile;
	private String phone;
	private String worspaceId;
	private String tags;
	private String preferredName;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getProfile() {
		return profile;
	}
	
	public void setProfile(String profile) {
		this.profile = profile;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getWorspaceId() {
		return worspaceId;
	}
	
	public void setWorspaceId(String worspaceId) {
		this.worspaceId = worspaceId;
	}
	
	public String getTags() {
		return tags;
	}
	
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public String getPreferredName() {
		return preferredName;
	}
	
	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}
	
	public String getGroupid() {
		return groupid;
	}
	
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
}
