package br.com.asyg.connector.bmg.fasap.model;

public class EmpEmploymentVO {
	private String personId;
	private String nivelCargo;
	private Integer integrou;
	private int id;
	private String dtAlteracao;
	
    public String getNivelCargo() {
		return nivelCargo;
	}
	public void setNivelCargo(String nivelCargo) {
		this.nivelCargo = nivelCargo;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
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
