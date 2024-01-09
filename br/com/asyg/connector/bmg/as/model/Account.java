package br.com.asyg.connector.bmg.as.model;

import java.util.Date;

import org.json.JSONArray;

public class Account {
	private String matricula;
    private String nome;
    private String email;
    private String cpf;
    private int situacao;
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
    private int indicativoUsuarioEstrangeiro;
    private long codigoEmpresaContratante;
    private Date dataExpiracao;
    private JSONArray permissiveId;
    private JSONArray restrictiveId;
	private String empresaSeqLab;
    private String cargoSeqLab;
    private String setorSeqLab;
    private String centroCustoSeqLab;
    private String cidadeSeqLab;
    private String codigoEmpresa;
    private String codigoCargo;
    private String codigoSetor;
    private String codigoCentroCusto;
    private String empresaCNPJ;
    private String empresa;
    
    public String getCodigoEmpresa() {
		return codigoEmpresa;
	}

	public void setCodigoEmpresa(String codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}

	public String getCodigoCargo() {
		return codigoCargo;
	}

	public void setCodigoCargo(String codigoCargo) {
		this.codigoCargo = codigoCargo;
	}

	public String getCodigoSetor() {
		return codigoSetor;
	}

	public void setCodigoSetor(String codigoSetor) {
		this.codigoSetor = codigoSetor;
	}

	public String getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(String codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public String getEmpresaCNPJ() {
		return empresaCNPJ;
	}

	public void setEmpresaCNPJ(String empresaCNPJ) {
		this.empresaCNPJ = empresaCNPJ;
	}

	public String getEmpresaSeqLab() {
		return empresaSeqLab;
	}

	public void setEmpresaSeqLab(String empresaSeqLab) {
		this.empresaSeqLab = empresaSeqLab;
	}

	public String getCargoSeqLab() {
		return cargoSeqLab;
	}

	public void setCargoSeqLab(String cargoSeqLab) {
		this.cargoSeqLab = cargoSeqLab;
	}

	public String getSetorSeqLab() {
		return setorSeqLab;
	}

	public void setSetorSeqLab(String setorSeqLab) {
		this.setorSeqLab = setorSeqLab;
	}

	public String getCentroCustoSeqLab() {
		return centroCustoSeqLab;
	}

	public void setCentroCustoSeqLab(String centroCustoSeqLab) {
		this.centroCustoSeqLab = centroCustoSeqLab;
	}

	public String getCidadeSeqLab() {
		return cidadeSeqLab;
	}

	public void setCidadeSeqLab(String cidadeSeqLab) {
		this.cidadeSeqLab = cidadeSeqLab;
	}
    
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
	
	public int getIndicativoUsuarioEstrangeiro() {
		return indicativoUsuarioEstrangeiro;
	}
	
	public void setIndicativoUsuarioEstrangeiro(int indicativoUsuarioEstrangeiro) {
		this.indicativoUsuarioEstrangeiro = indicativoUsuarioEstrangeiro;
	}
	
	public long getCodigoEmpresaContratante() {
		return codigoEmpresaContratante;
	}
	
	public void setCodigoEmpresaContratante(long codigoEmpresaContratante) {
		this.codigoEmpresaContratante = codigoEmpresaContratante;
	}
	
	public Date getDataExpiracao() {
		return dataExpiracao;
	}
	
	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}
	
	public JSONArray getPermissiveId() {
		return permissiveId;
	}

	public void setPermissiveId(JSONArray permissiveId) {
		this.permissiveId = permissiveId;
	}

	public JSONArray getRestrictiveId() {
		return restrictiveId;
	}

	public void setRestrictiveId(JSONArray restrictiveId) {
		this.restrictiveId = restrictiveId;
	}
	
	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
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
