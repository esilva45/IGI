package br.com.asyg.connector.bmg.fasap.model;

public class PerPersonVO {
	private int id;
	private String personId;
	private String areaCode;
    private String phoneNumber;
    private Integer integrou;
    private String dtAlteracao;
    
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Integer getIntegrou() {
		return integrou;
	}
	public void setIntegrou(Integer integrou) {
		this.integrou = integrou;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDtAlteracao() {
		return dtAlteracao;
	}
	public void setDtAlteracao(String dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}
}
