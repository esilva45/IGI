package br.com.asyg.connector.bmg.as.model;

import java.util.Date;

import org.json.JSONArray;

public class Account {
	private String matricula;
    private String nome;
    private String email;
    private String cpf;
    private int situacao;
    private int codigoCentroCusto;
    private int codigoEmpresa;
    private String nivelCargo;
    private String cargo;
    private String cidade;
    private String setor;
    private String cpfSuperior;
    private Date dtNascimento;
    private Date dtAdmissao;
    private Date dtDesligamento;
    private Date dtInclusao;
    private Date dtModificacao;
    private String loginWindows;
    private String indicativoUsuarioEstrangeiro;
    private String codigoCargo;
    private String codigoEmpresaContratante;
    private String codigoSetor;
    private Date dataExpiracao;
    private JSONArray entitlementId;
    
	public String getMatricula() {
		return matricula;
	}
	
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getCpf() {
		return cpf;
	}
	
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	public int getSituacao() {
		return situacao;
	}
	
	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}
	
	public int getCodigoCentroCusto() {
		return codigoCentroCusto;
	}
	
	public void setCodigoCentroCusto(int codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}
	
	public int getCodigoEmpresa() {
		return codigoEmpresa;
	}
	
	public void setCodigoEmpresa(int codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}
	
	public String getNivelCargo() {
		return nivelCargo;
	}
	
	public void setNivelCargo(String nivelCargo) {
		this.nivelCargo = nivelCargo;
	}
	
	public String getCargo() {
		return cargo;
	}
	
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	
	public String getCidade() {
		return cidade;
	}
	
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	
	public String getSetor() {
		return setor;
	}
	
	public void setSetor(String setor) {
		this.setor = setor;
	}
	
	public String getCpfSuperior() {
		return cpfSuperior;
	}
	
	public void setCpfSuperior(String cpfSuperior) {
		this.cpfSuperior = cpfSuperior;
	}
	
	public Date getDtNascimento() {
		return dtNascimento;
	}
	
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	
	public Date getDtAdmissao() {
		return dtAdmissao;
	}
	
	public void setDtAdmissao(Date dtAdmissao) {
		this.dtAdmissao = dtAdmissao;
	}
	
	public Date getDtDesligamento() {
		return dtDesligamento;
	}
	
	public void setDtDesligamento(Date dtDesligamento) {
		this.dtDesligamento = dtDesligamento;
	}
	
	public Date getDtInclusao() {
		return dtInclusao;
	}
	
	public void setDtInclusao(Date dtInclusao) {
		this.dtInclusao = dtInclusao;
	}
	
	public Date getDtModificacao() {
		return dtModificacao;
	}
	
	public void setDtModificacao(Date dtModificacao) {
		this.dtModificacao = dtModificacao;
	}
	
	public String getLoginWindows() {
		return loginWindows;
	}
	
	public void setLoginWindows(String loginWindows) {
		this.loginWindows = loginWindows;
	}
	
	public String getIndicativoUsuarioEstrangeiro() {
		return indicativoUsuarioEstrangeiro;
	}
	
	public void setIndicativoUsuarioEstrangeiro(String indicativoUsuarioEstrangeiro) {
		this.indicativoUsuarioEstrangeiro = indicativoUsuarioEstrangeiro;
	}
	
	public String getCodigoCargo() {
		return codigoCargo;
	}
	
	public void setCodigoCargo(String codigoCargo) {
		this.codigoCargo = codigoCargo;
	}
	
	public String getCodigoEmpresaContratante() {
		return codigoEmpresaContratante;
	}
	
	public void setCodigoEmpresaContratante(String codigoEmpresaContratante) {
		this.codigoEmpresaContratante = codigoEmpresaContratante;
	}
	
	public String getCodigoSetor() {
		return codigoSetor;
	}
	
	public void setCodigoSetor(String codigoSetor) {
		this.codigoSetor = codigoSetor;
	}
	
	public Date getDataExpiracao() {
		return dataExpiracao;
	}
	
	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}
	
	public JSONArray getEntitlementId() {
		return entitlementId;
	}

	public void setEntitlementId(JSONArray entitlementId) {
		this.entitlementId = entitlementId;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (getClass() != obj.getClass())
            return false;
        
        Account other = (Account) obj;
        
        if (cpf == null) {
            if (other.cpf != null)
                return false;
        } else if (!cpf.equals(other.cpf))
            return false;
        return true;
    }
}
