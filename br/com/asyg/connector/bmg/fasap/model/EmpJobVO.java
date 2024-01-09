package br.com.asyg.connector.bmg.fasap.model;

public class EmpJobVO implements Comparable<EmpJobVO> {
    private int id;
    private String userId;
    private String matricula;
    private String dataNascimento;
    private String CPF;
    private String dataValidadeDoc;
    private String dataEmissaoDoc;
    private String numRG;
    private String dataFim;
    private String dataInicio;
    private String motivoEvento;
    private String statusColaborador;
    private String ultModifEm;
    private String login;
    private String dataFimAtriOrg;
    private String dataInicioAtriOrg;
    private String empresa;
    private String grupoEmpregado;
    private String subGrupoEmpregado;
    private String centroCusto;
    private String estabelecimento;
    private String cargo;
    private String departamento;
    private String ultModifEmAtriOrg;
    private String matriculaGestor;
    private Integer integrou;
    private String empresaDesc;
    private String departamentoDesc;
    private String cargoDesc;
    private String estabelecimentoDesc;
    private String statusColaboradorDesc;
    private String motivoEventoDesc;
    private String grupoEmpregadoDesc;
    private String subGrupoEmpregadoDesc;
    private String centroCustoDesc;

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCPF() {
        return CPF;
    }

    public void setCPF(String cPF) {
        CPF = this.checkCpf(cPF);
    }

    public String getDataValidadeDoc() {
        return dataValidadeDoc;
    }

    public void setDataValidadeDoc(String dataValidadeDoc) {
        this.dataValidadeDoc = dataValidadeDoc;
    }

    public String getDataEmissaoDoc() {
        return dataEmissaoDoc;
    }

    public void setDataEmissaoDoc(String dataEmissaoDoc) {
        this.dataEmissaoDoc = dataEmissaoDoc;
    }

    public String getNumRG() {
        return numRG;
    }

    public void setNumRG(String numRG) {
        this.numRG = numRG;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getMotivoEvento() {
        return motivoEvento;
    }

    public void setMotivoEvento(String motivoEvento) {
        this.motivoEvento = motivoEvento;
    }

    public String getStatusColaborador() {
        return statusColaborador;
    }

    public void setStatusColaborador(String statusColaborador) {
        this.statusColaborador = statusColaborador;
    }

    public String getUltModifEm() {
        return ultModifEm;
    }

    public void setUltModifEm(String ultModifEm) {
        this.ultModifEm = ultModifEm;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDataFimAtriOrg() {
        return dataFimAtriOrg;
    }

    public void setDataFimAtriOrg(String dataFimAtriOrg) {
        this.dataFimAtriOrg = dataFimAtriOrg;
    }

    public String getDataInicioAtriOrg() {
        return dataInicioAtriOrg;
    }

    public void setDataInicioAtriOrg(String dataInicioAtriOrg) {
        this.dataInicioAtriOrg = dataInicioAtriOrg;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getGrupoEmpregado() {
        return grupoEmpregado;
    }

    public void setGrupoEmpregado(String grupoEmpregado) {
        this.grupoEmpregado = grupoEmpregado;
    }

    public String getSubGrupoEmpregado() {
        return subGrupoEmpregado;
    }

    public void setSubGrupoEmpregado(String subGrupoEmpregado) {
        this.subGrupoEmpregado = subGrupoEmpregado;
    }

    public String getCentroCusto() {
        return centroCusto;
    }

    public void setCentroCusto(String centroCusto) {
        this.centroCusto = centroCusto;
    }

    public String getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(String estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getUltModifEmAtriOrg() {
        return ultModifEmAtriOrg;
    }

    public void setUltModifEmAtriOrg(String ultModifEmAtriOrg) {
        this.ultModifEmAtriOrg = ultModifEmAtriOrg;
    }

    public String getMatriculaGestor() {
        return matriculaGestor;
    }

    public void setMatriculaGestor(String matriculaGestor) {
        this.matriculaGestor = matriculaGestor;
    }

    public Integer getIntegrou() {
        return integrou;
    }

    public void setIntegrou(Integer integrou) {
        this.integrou = integrou;
    }

    public String getEmpresaDesc() {
        return empresaDesc;
    }

    public void setEmpresaDesc(String empresaDesc) {
        this.empresaDesc = empresaDesc;
    }

    public String getDepartamentoDesc() {
        return departamentoDesc;
    }

    public void setDepartamentoDesc(String departamentoDesc) {
        this.departamentoDesc = departamentoDesc;
    }

    public String getCargoDesc() {
        return cargoDesc;
    }

    public void setCargoDesc(String cargoDesc) {
        this.cargoDesc = cargoDesc;
    }

    public String getEstabelecimentoDesc() {
        return estabelecimentoDesc;
    }

    public void setEstabelecimentoDesc(String estabelecimentoDesc) {
        this.estabelecimentoDesc = estabelecimentoDesc;
    }

    public String getStatusColaboradorDesc() {
        return statusColaboradorDesc;
    }

    public void setStatusColaboradorDesc(String statusColaboradorDesc) {
        this.statusColaboradorDesc = statusColaboradorDesc;
    }

    public String getMotivoEventoDesc() {
        return motivoEventoDesc;
    }

    public void setMotivoEventoDesc(String motivoEventoDesc) {
        this.motivoEventoDesc = motivoEventoDesc;
    }

    public String getGrupoEmpregadoDesc() {
        return grupoEmpregadoDesc;
    }

    public void setGrupoEmpregadoDesc(String grupoEmpregadoDesc) {
        this.grupoEmpregadoDesc = grupoEmpregadoDesc;
    }

    public String getSubGrupoEmpregadoDesc() {
        return subGrupoEmpregadoDesc;
    }

    public void setSubGrupoEmpregadoDesc(String subGrupoEmpregadoDesc) {
        this.subGrupoEmpregadoDesc = subGrupoEmpregadoDesc;
    }

    public String getCentroCustoDesc() {
        return centroCustoDesc;
    }

    public void setCentroCustoDesc(String centroCustoDesc) {
        this.centroCustoDesc = centroCustoDesc;
    }

    private String checkCpf(String cpf) {
        if (cpf != null) {
            cpf = cpf.replaceAll("[.-]", "");

            if (cpf.length() < 11) {
                while (cpf.length() < 11) {
                    cpf = "0" + cpf;
                }
            }
        }
        
        return cpf;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        
        if (obj == null)
            return false;
        
        if (getClass() != obj.getClass())
            return false;
        
        EmpJobVO other = (EmpJobVO) obj;
        
        if (userId == null && matricula == null) {
            if (other.userId != null && other.matricula != null)
                return false;
        } else if (userId.equals(other.userId) && !matricula.equals(other.matricula))
            return false;
        
        return true;
    }

    @Override
    public int compareTo(EmpJobVO empJobVO) {
        try {
            if (CPF != null || !CPF.isEmpty()) {
                String cpf = this.CPF.replace(".", "").replace("-", "");
                String cpfAnt = empJobVO.getCPF().replace(".", "").replace("-", "");
                long cpfInt = Long.parseLong(cpf);
                long cpfAntInt = Long.parseLong(cpfAnt);

                if (cpfInt > cpfAntInt) {
                    return -1;
                }
                
                if (cpfInt < cpfAntInt) {
                    return 1;
                }

                return 0;
            } else {
                return -1;
            }
        }
        catch (Exception e) {
            return 0;
        }
    }
}
