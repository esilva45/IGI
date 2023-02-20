package br.com.asyg.connector.brk.hrfeed.model;

public class User {
	private String pernr;
	private String cpf;
	private String uid;

	public String getPernr() {
		return this.pernr;
	}

	public String getUid() {
		return this.uid;
	}
	
	public String getCpf() {
		return this.cpf;
	}
	
	public void setPernr(String pernr) {
		this.pernr = pernr;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
}
