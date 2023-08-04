package br.com.asyg.connector.bmg.as.model;

public class Entitlements {
    private String description;
    private String permissonType;
    private String entitlementId;
    private String entitlementName;
    
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPermissonType() {
		return permissonType;
	}

	public void setPermissonType(String permissonType) {
		this.permissonType = permissonType;
	}

	public String getEntitlementName() {
		return entitlementName;
	}

	public void setEntitlementName(String entitlementName) {
		this.entitlementName = entitlementName;
	}

	public String getEntitlementId() {
		return entitlementId;
	}

	public void setEntitlementId(String entitlementId) {
		this.entitlementId = entitlementId;
	}
}
