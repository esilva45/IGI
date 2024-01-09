package br.com.asyg.connector.bmg.fasap.model;

public class DadosConexaoVO {

    private String urlIGI;
    private String userIGI;
    private String passIGI;

    private String urlSAP;
    private String clientIdSAP;
    private String userIdSAP;
    private String tokenSAP;
    private String privateKeySAP;
    private String grantTypeSAP;
    private String companyIdSAP;
    private String assertion;
    private String pagination;

    private String allowMsg;
    private String codificacaoJSON;

    public String getUrlIGI() {
        return urlIGI;
    }

    public void setUrlIGI(String urlIGI) {
        this.urlIGI = urlIGI;
    }

    public String getUserIGI() {
        return userIGI;
    }

    public void setUserIGI(String userIGI) {
        this.userIGI = userIGI;
    }

    public String getPassIGI() {
        return passIGI;
    }

    public void setPassIGI(String passIGI) {
        this.passIGI = passIGI;
    }

    public String getUrlSAP() {
        return urlSAP;
    }

    public void setUrlSAP(String urlSAP) {
        this.urlSAP = urlSAP;
    }

    public String getClientIdSAP() {
        return clientIdSAP;
    }

    public void setClientIdSAP(String clientIdSAP) {
        this.clientIdSAP = clientIdSAP;
    }

    public String getUserIdSAP() {
        return userIdSAP;
    }

    public void setUserIdSAP(String userIdSAP) {
        this.userIdSAP = userIdSAP;
    }

    public String getPrivateKeySAP() {
        return privateKeySAP;
    }

    public void setPrivateKeySAP(String privateKeySAP) {
        this.privateKeySAP = privateKeySAP;
    }

    public String getGrantTypeSAP() {
        return grantTypeSAP;
    }

    public void setGrantTypeSAP(String grantTypeSAP) {
        this.grantTypeSAP = grantTypeSAP;
    }

    public String getCompanyIdSAP() {
        return companyIdSAP;
    }

    public void setCompanyIdSAP(String companyIdSAP) {
        this.companyIdSAP = companyIdSAP;
    }

    public String getAllowMsg() {
        return allowMsg;
    }

    public void setAllowMsg(String allowMsg) {
        this.allowMsg = allowMsg;
    }

    public String getCodificacaoJSON() {
        return codificacaoJSON;
    }

    public void setCodificacaoJSON(String codificacaoJSON) {
        this.codificacaoJSON = codificacaoJSON;
    }

    public String getAssertion() {
        return assertion;
    }

    public void setAssertion(String assertion) {
        this.assertion = assertion;
    }

    public String getTokenSAP() {
        return tokenSAP;
    }

    public void setTokenSAP(String tokenSAP) {
        this.tokenSAP = tokenSAP;
    }

    public String getPagination() {
        return pagination;
    }

    public void setPagination(String pagination) {
        this.pagination = pagination;
    }

}
