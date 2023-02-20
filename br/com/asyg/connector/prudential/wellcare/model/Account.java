package br.com.asyg.connector.prudential.wellcare.model;

import java.util.ArrayList;
import java.util.List;

public class Account {
	private String FirstName; // Nome
	private String IdNumber; //LOGIN
	private Integer CHID; //ID
	private Integer CHType; //TIPO
	private String CHTypeDescription;
	private String AuxText01; // Documento RG
	private Integer CompanyID; //Empresa
	private String CompanyDescription; //Empresa
	private String CHState; //Status
	private String LastName;
	private String VisitorCompany;
	private String EMail;
	private Boolean IsUndesirable;
	private String IsUndesirableReason1;
	private String IsUndesirableReason2;
	private Integer PartitionID;
	private Integer LastModifOnLocality;
	private String LastModifDateTime;
	private String LastModifBy;
	private String CHStartValidityDateTime;
	private String CHEndValidityDateTime;
	private Boolean CHDownloadRequired;
	private Boolean TraceCH;
	private Integer Trace_AlmP;
	private Integer Trace_Act;
	private String TrustedLogin;
	private Integer DefFrontCardLayout;
	private Integer DefBackCardLayout;
	private Integer MaxTransits;
	private Integer MaxMeals;
	private Boolean IgnoreTransitsCount;
	private Boolean IgnoreMealsCount;
	private Boolean IgnoreAntiPassback;
	private Boolean IgnoreZoneCount;
	private String PIN;
	private Boolean RequiresEscort;
	private Boolean CanEscort;
	private Boolean CanReceiveVisits;
	private Integer SubZoneID;
	private Boolean IgnoreRandomInspection;
	private String CHFloor;
	private Integer BdccState;
	private Boolean BdccIgnore;
	private String BdccCompanies;
	private Integer IdNumberType;
	private String AuxText02;
	private String AuxText03;
	private String AuxText04;
	private String AuxText05;
	private String AuxText06;
	private String AuxText07;
	private String AuxText08;
	private String AuxText09;
	private String AuxText10;
	private String AuxText11;
	private String AuxText12;
	private String AuxText13;
	private String AuxText14;
	private String AuxText15;
	private String AuxTextA01;
	private String AuxTextA02;
	private String AuxTextA03;
	private String AuxTextA04;
	private String AuxTextA05;
	private Integer AuxLst01;
	private Integer AuxLst02;
	private Integer AuxLst03;
	private Integer AuxLst04;
	private Integer AuxLst05;
	private Integer AuxLst06;
	private Integer AuxLst07;
	private Integer AuxLst08;
	private Integer AuxLst09;
	private Integer AuxLst10;
	private Integer AuxLst11;
	private Integer AuxLst12;
	private Integer AuxLst13;
	private Integer AuxLst14;
	private Integer AuxLst15;
	private Boolean AuxChk01;
	private Boolean AuxChk02;
	private Boolean AuxChk03;
	private Boolean AuxChk04;
	private Boolean AuxChk05;
	private Boolean AuxChk06;
	private Boolean AuxChk07;
	private Boolean AuxChk08;
	private Boolean AuxChk09;
	private Boolean AuxChk10;
	private String AuxDte01;
	private String AuxDte02;
	private String AuxDte03;
	private String AuxDte04;
	private String AuxDte05;
	private String AuxDte06;
	private String AuxDte07;
	private String AuxDte08;
	private String AuxDte09;
	private String AuxDte10;
	private Card cartao;
	private List<AccessLevel> accessLevels = new ArrayList<AccessLevel>();

	public Account(){
		this.CHTypeDescription = "";
		this.AuxText01 = "";
		this.CompanyDescription = "";
		this.LastName = "";
		this.VisitorCompany = "";
		this.IsUndesirable = false;
		this.IsUndesirableReason1 = "";
		this.IsUndesirableReason2 = "";
		this.PartitionID = 0;
		this.LastModifOnLocality=0;
		this.LastModifDateTime ="";
		this.LastModifBy ="";
		this.CHStartValidityDateTime = "";
		this.CHEndValidityDateTime = "";
		this.CHDownloadRequired = false;
		this.TraceCH = false;
		this.Trace_AlmP = 0;
		this.Trace_Act = 0;
		this.TrustedLogin = "";
		this.DefFrontCardLayout = 0;
		this.DefBackCardLayout = 0;
		this.MaxTransits = 0;
		this.MaxMeals = 0;
		this.IgnoreTransitsCount = false;
		this.IgnoreMealsCount = false;
		this.IgnoreAntiPassback = false;
		this.IgnoreZoneCount = false;
		this.PIN = "";
		this.RequiresEscort = false;
		this.CanEscort = false;
		this.CanReceiveVisits = false;
		this.SubZoneID = 0;
		this.IgnoreRandomInspection = false;
		this.CHFloor = "";
		this.BdccState = 0;
		this.BdccIgnore = false;
		this.BdccCompanies = "";
		this.IdNumberType = 0;
		this.AuxText02 = "";
		this.AuxText03 = "";
		this.AuxText04 = "";
		this.AuxText05 = "";
		this.AuxText06 = "";
		this.AuxText07 = "";
		this.AuxText08 = "";
		this.AuxText09 = "";
		this.AuxText10 = "";
		this.AuxText11 = "";
		this.AuxText12 = "";
		this.AuxText13 = "";
		this.AuxText14 = "";
		this.AuxText15 = "";
		this.AuxTextA01 = "";
		this.AuxTextA02 = "";
		this.AuxTextA03 = "";
		this.AuxTextA04 = "";
		this.AuxTextA05 = "";
		this.AuxLst01 = 0;
		this.AuxLst02 = 0;
		this.AuxLst03 = 0;
		this.AuxLst04 = 0;
		this.AuxLst05 = 0;
		this.AuxLst06 = 0;
		this.AuxLst07 = 0;
		this.AuxLst08 = 0;
		this.AuxLst09 = 0;
		this.AuxLst10 = 0;
		this.AuxLst11 = 0;
		this.AuxLst12 = 0;
		this.AuxLst13 = 0;
		this.AuxLst14 = 0;
		this.AuxLst15 = 0;
		this.AuxChk01 = false;
		this.AuxChk02 = false;
		this.AuxChk03 = false;
		this.AuxChk04 = false;
		this.AuxChk05 = false;
		this.AuxChk06 = false;
		this.AuxChk07 = false;
		this.AuxChk08 = false;
		this.AuxChk09 = false;
		this.AuxChk10 = false;
		this.AuxDte01 = "";
		this.AuxDte02 = "";
		this.AuxDte03 = "";
		this.AuxDte04 = "";
		this.AuxDte05 = "";
		this.AuxDte06 = "";
		this.AuxDte07 = "";
		this.AuxDte08 = "";
		this.AuxDte09 = "";
		this.AuxDte10 = "";
	}
		
	public Card getCartao() {
		return cartao;
	}
	public void setCartao(Card cartao) {
		this.cartao = cartao;
	}
	public String getFirstName() {
		return FirstName;
	}
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	public String getCompanyDescription() {
		return CompanyDescription;
	}
	public void setCompanyDescription(String companyDescription) {
		CompanyDescription = companyDescription;
	}
	public void setCompanyID(Integer companyID) {
		CompanyID = companyID;
	}
	public Integer getCompanyID() {
		return CompanyID;
	}
	public String getIdNumber() {
		return IdNumber;
	}
	public void setIdNumber(String idNumber) {
		IdNumber = idNumber;
	}
	public Integer getCHID() {
		return CHID;
	}
	public void setCHID(Integer cHID) {
		CHID = cHID;
	}
	public Integer getCHType() {
		return CHType;
	}
	public void setCHType(Integer cHType) {
		CHType = cHType;
	}
	public String getAuxText01() {
		return AuxText01;
	}
	public void setAuxText01(String auxText01) {
		AuxText01 = auxText01;
	}
	public String getCHState() {
		return CHState;
	}
	public void setCHState(String cHState) {
		CHState = cHState;
	}
	public String getLastName() {
		return LastName;
	}
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	public String getVisitorCompany() {
		return VisitorCompany;
	}
	public void setVisitorCompany(String visitorCompany) {
		VisitorCompany = visitorCompany;
	}
	public String getEMail() {
		return EMail;
	}
	public void setEMail(String eMail) {
		EMail = eMail;
	}
	public Boolean getIsUndesirable() {
		return IsUndesirable;
	}
	public void setIsUndesirable(Boolean isUndesirable) {
		IsUndesirable = isUndesirable;
	}
	public String getIsUndesirableReason1() {
		return IsUndesirableReason1;
	}
	public void setIsUndesirableReason1(String isUndesirableReason1) {
		IsUndesirableReason1 = isUndesirableReason1;
	}
	public String getIsUndesirableReason2() {
		return IsUndesirableReason2;
	}
	public void setIsUndesirableReason2(String isUndesirableReason2) {
		IsUndesirableReason2 = isUndesirableReason2;
	}
	public Integer getPartitionID() {
		return PartitionID;
	}
	public void setPartitionID(Integer partitionID) {
		PartitionID = partitionID;
	}
	public Integer getLastModifOnLocality() {
		return LastModifOnLocality;
	}
	public void setLastModifOnLocality(Integer lastModifOnLocality) {
		LastModifOnLocality = lastModifOnLocality;
	}
	public String getLastModifDateTime() {
		return LastModifDateTime;
	}
	public void setLastModifDateTime(String lastModifDateTime) {
		LastModifDateTime = lastModifDateTime;
	}
	public String getLastModifBy() {
		return LastModifBy;
	}
	public void setLastModifBy(String lastModifBy) {
		LastModifBy = lastModifBy;
	}
	public String getCHStartValidityDateTime() {
		return CHStartValidityDateTime;
	}
	public void setCHStartValidityDateTime(String cHStartValidityDateTime) {
		CHStartValidityDateTime = cHStartValidityDateTime;
	}
	public String getCHEndValidityDateTime() {
		return CHEndValidityDateTime;
	}
	public void setCHEndValidityDateTime(String cHEndValidityDateTime) {
		CHEndValidityDateTime = cHEndValidityDateTime;
	}
	public Boolean getCHDownloadRequired() {
		return CHDownloadRequired;
	}
	public void setCHDownloadRequired(Boolean cHDownloadRequired) {
		CHDownloadRequired = cHDownloadRequired;
	}
	public Boolean getTraceCH() {
		return TraceCH;
	}
	public void setTraceCH(Boolean traceCH) {
		TraceCH = traceCH;
	}
	public Integer getTrace_AlmP() {
		return Trace_AlmP;
	}
	public void setTrace_AlmP(Integer trace_AlmP) {
		Trace_AlmP = trace_AlmP;
	}
	public Integer getTrace_Act() {
		return Trace_Act;
	}
	public void setTrace_Act(Integer trace_Act) {
		Trace_Act = trace_Act;
	}
	public String getTrustedLogin() {
		return TrustedLogin;
	}
	public void setTrustedLogin(String trustedLogin) {
		TrustedLogin = trustedLogin;
	}
	public Integer getDefFrontCardLayout() {
		return DefFrontCardLayout;
	}
	public void setDefFrontCardLayout(Integer defFrontCardLayout) {
		DefFrontCardLayout = defFrontCardLayout;
	}
	public Integer getDefBackCardLayout() {
		return DefBackCardLayout;
	}
	public void setDefBackCardLayout(Integer defBackCardLayout) {
		DefBackCardLayout = defBackCardLayout;
	}
	public Integer getMaxTransits() {
		return MaxTransits;
	}
	public void setMaxTransits(Integer maxTransits) {
		MaxTransits = maxTransits;
	}
	public Integer getMaxMeals() {
		return MaxMeals;
	}
	public void setMaxMeals(Integer maxMeals) {
		MaxMeals = maxMeals;
	}
	public Boolean getIgnoreTransitsCount() {
		return IgnoreTransitsCount;
	}
	public void setIgnoreTransitsCount(Boolean ignoreTransitsCount) {
		IgnoreTransitsCount = ignoreTransitsCount;
	}
	public Boolean getIgnoreMealsCount() {
		return IgnoreMealsCount;
	}
	public void setIgnoreMealsCount(Boolean ignoreMealsCount) {
		IgnoreMealsCount = ignoreMealsCount;
	}
	public Boolean getIgnoreAntiPassback() {
		return IgnoreAntiPassback;
	}
	public void setIgnoreAntiPassback(Boolean ignoreAntiPassback) {
		IgnoreAntiPassback = ignoreAntiPassback;
	}
	public Boolean getIgnoreZoneCount() {
		return IgnoreZoneCount;
	}
	public void setIgnoreZoneCount(Boolean ignoreZoneCount) {
		IgnoreZoneCount = ignoreZoneCount;
	}
	public String getPIN() {
		return PIN;
	}
	public void setPIN(String pIN) {
		PIN = pIN;
	}
	public Boolean getRequiresEscort() {
		return RequiresEscort;
	}
	public void setRequiresEscort(Boolean requiresEscort) {
		RequiresEscort = requiresEscort;
	}
	public Boolean getCanEscort() {
		return CanEscort;
	}
	public void setCanEscort(Boolean canEscort) {
		CanEscort = canEscort;
	}
	public Boolean getCanReceiveVisits() {
		return CanReceiveVisits;
	}
	public void setCanReceiveVisits(Boolean canReceiveVisits) {
		CanReceiveVisits = canReceiveVisits;
	}
	public Integer getSubZoneID() {
		return SubZoneID;
	}
	public void setSubZoneID(Integer subZoneID) {
		SubZoneID = subZoneID;
	}
	public Boolean getIgnoreRandomInspection() {
		return IgnoreRandomInspection;
	}
	public void setIgnoreRandomInspection(Boolean ignoreRandomInspection) {
		IgnoreRandomInspection = ignoreRandomInspection;
	}
	public String getCHFloor() {
		return CHFloor;
	}
	public void setCHFloor(String cHFloor) {
		CHFloor = cHFloor;
	}
	public Integer getBdccState() {
		return BdccState;
	}
	public void setBdccState(Integer bdccState) {
		BdccState = bdccState;
	}
	public Boolean getBdccIgnore() {
		return BdccIgnore;
	}
	public void setBdccIgnore(Boolean bdccIgnore) {
		BdccIgnore = bdccIgnore;
	}
	public String getBdccCompanies() {
		return BdccCompanies;
	}
	public void setBdccCompanies(String bdccCompanies) {
		BdccCompanies = bdccCompanies;
	}
	public Integer getIdNumberType() {
		return IdNumberType;
	}
	public void setIdNumberType(Integer idNumberType) {
		IdNumberType = idNumberType;
	}
	public String getAuxText02() {
		return AuxText02;
	}
	public void setAuxText02(String auxText02) {
		AuxText02 = auxText02;
	}
	public String getAuxText03() {
		return AuxText03;
	}
	public void setAuxText03(String auxText03) {
		AuxText03 = auxText03;
	}
	public String getAuxText04() {
		return AuxText04;
	}
	public void setAuxText04(String auxText04) {
		AuxText04 = auxText04;
	}
	public String getAuxText05() {
		return AuxText05;
	}
	public void setAuxText05(String auxText05) {
		AuxText05 = auxText05;
	}
	public String getAuxText06() {
		return AuxText06;
	}
	public void setAuxText06(String auxText06) {
		AuxText06 = auxText06;
	}
	public String getAuxText07() {
		return AuxText07;
	}
	public void setAuxText07(String auxText07) {
		AuxText07 = auxText07;
	}
	public String getAuxText08() {
		return AuxText08;
	}
	public void setAuxText08(String auxText08) {
		AuxText08 = auxText08;
	}
	public String getAuxText09() {
		return AuxText09;
	}
	public void setAuxText09(String auxText09) {
		AuxText09 = auxText09;
	}
	public String getAuxText10() {
		return AuxText10;
	}
	public void setAuxText10(String auxText10) {
		AuxText10 = auxText10;
	}
	public String getAuxText11() {
		return AuxText11;
	}
	public void setAuxText11(String auxText11) {
		AuxText11 = auxText11;
	}
	public String getAuxText12() {
		return AuxText12;
	}
	public void setAuxText12(String auxText12) {
		AuxText12 = auxText12;
	}
	public String getAuxText13() {
		return AuxText13;
	}
	public void setAuxText13(String auxText13) {
		AuxText13 = auxText13;
	}
	public String getAuxText14() {
		return AuxText14;
	}
	public void setAuxText14(String auxText14) {
		AuxText14 = auxText14;
	}
	public String getAuxText15() {
		return AuxText15;
	}
	public void setAuxText15(String auxText15) {
		AuxText15 = auxText15;
	}
	public String getAuxTextA01() {
		return AuxTextA01;
	}
	public void setAuxTextA01(String auxTextA01) {
		AuxTextA01 = auxTextA01;
	}
	public String getAuxTextA02() {
		return AuxTextA02;
	}
	public void setAuxTextA02(String auxTextA02) {
		AuxTextA02 = auxTextA02;
	}
	public String getAuxTextA03() {
		return AuxTextA03;
	}
	public void setAuxTextA03(String auxTextA03) {
		AuxTextA03 = auxTextA03;
	}
	public String getAuxTextA04() {
		return AuxTextA04;
	}
	public void setAuxTextA04(String auxTextA04) {
		AuxTextA04 = auxTextA04;
	}
	public String getAuxTextA05() {
		return AuxTextA05;
	}
	public void setAuxTextA05(String auxTextA05) {
		AuxTextA05 = auxTextA05;
	}
	public Integer getAuxLst01() {
		return AuxLst01;
	}
	public void setAuxLst01(Integer auxLst01) {
		AuxLst01 = auxLst01;
	}
	public Integer getAuxLst02() {
		return AuxLst02;
	}
	public void setAuxLst02(Integer auxLst02) {
		AuxLst02 = auxLst02;
	}
	public Integer getAuxLst03() {
		return AuxLst03;
	}
	public void setAuxLst03(Integer auxLst03) {
		AuxLst03 = auxLst03;
	}
	public Integer getAuxLst04() {
		return AuxLst04;
	}
	public void setAuxLst04(Integer auxLst04) {
		AuxLst04 = auxLst04;
	}
	public Integer getAuxLst05() {
		return AuxLst05;
	}
	public void setAuxLst05(Integer auxLst05) {
		AuxLst05 = auxLst05;
	}
	public Integer getAuxLst06() {
		return AuxLst06;
	}
	public void setAuxLst06(Integer auxLst06) {
		AuxLst06 = auxLst06;
	}
	public Integer getAuxLst07() {
		return AuxLst07;
	}
	public void setAuxLst07(Integer auxLst07) {
		AuxLst07 = auxLst07;
	}
	public Integer getAuxLst08() {
		return AuxLst08;
	}
	public void setAuxLst08(Integer auxLst08) {
		AuxLst08 = auxLst08;
	}
	public Integer getAuxLst09() {
		return AuxLst09;
	}
	public void setAuxLst09(Integer auxLst09) {
		AuxLst09 = auxLst09;
	}
	public Integer getAuxLst10() {
		return AuxLst10;
	}
	public void setAuxLst10(Integer auxLst10) {
		AuxLst10 = auxLst10;
	}
	public Integer getAuxLst11() {
		return AuxLst11;
	}
	public void setAuxLst11(Integer auxLst11) {
		AuxLst11 = auxLst11;
	}
	public Integer getAuxLst12() {
		return AuxLst12;
	}
	public void setAuxLst12(Integer auxLst12) {
		AuxLst12 = auxLst12;
	}
	public Integer getAuxLst13() {
		return AuxLst13;
	}
	public void setAuxLst13(Integer auxLst13) {
		AuxLst13 = auxLst13;
	}
	public Integer getAuxLst14() {
		return AuxLst14;
	}
	public void setAuxLst14(Integer auxLst14) {
		AuxLst14 = auxLst14;
	}
	public Integer getAuxLst15() {
		return AuxLst15;
	}
	public void setAuxLst15(Integer auxLst15) {
		AuxLst15 = auxLst15;
	}
	public Boolean getAuxChk01() {
		return AuxChk01;
	}
	public void setAuxChk01(Boolean auxChk01) {
		AuxChk01 = auxChk01;
	}
	public Boolean getAuxChk02() {
		return AuxChk02;
	}
	public void setAuxChk02(Boolean auxChk02) {
		AuxChk02 = auxChk02;
	}
	public Boolean getAuxChk03() {
		return AuxChk03;
	}
	public void setAuxChk03(Boolean auxChk03) {
		AuxChk03 = auxChk03;
	}
	public Boolean getAuxChk04() {
		return AuxChk04;
	}
	public void setAuxChk04(Boolean auxChk04) {
		AuxChk04 = auxChk04;
	}
	public Boolean getAuxChk05() {
		return AuxChk05;
	}
	public void setAuxChk05(Boolean auxChk05) {
		AuxChk05 = auxChk05;
	}
	public Boolean getAuxChk06() {
		return AuxChk06;
	}
	public void setAuxChk06(Boolean auxChk06) {
		AuxChk06 = auxChk06;
	}
	public Boolean getAuxChk07() {
		return AuxChk07;
	}
	public void setAuxChk07(Boolean auxChk07) {
		AuxChk07 = auxChk07;
	}
	public Boolean getAuxChk08() {
		return AuxChk08;
	}
	public void setAuxChk08(Boolean auxChk08) {
		AuxChk08 = auxChk08;
	}
	public Boolean getAuxChk09() {
		return AuxChk09;
	}
	public void setAuxChk09(Boolean auxChk09) {
		AuxChk09 = auxChk09;
	}
	public Boolean getAuxChk10() {
		return AuxChk10;
	}
	public void setAuxChk10(Boolean auxChk10) {
		AuxChk10 = auxChk10;
	}
	public String getAuxDte01() {
		return AuxDte01;
	}
	public void setAuxDte01(String auxDte01) {
		AuxDte01 = auxDte01;
	}
	public String getAuxDte02() {
		return AuxDte02;
	}
	public void setAuxDte02(String auxDte02) {
		AuxDte02 = auxDte02;
	}
	public String getAuxDte03() {
		return AuxDte03;
	}
	public void setAuxDte03(String auxDte03) {
		AuxDte03 = auxDte03;
	}
	public String getAuxDte04() {
		return AuxDte04;
	}
	public void setAuxDte04(String auxDte04) {
		AuxDte04 = auxDte04;
	}
	public String getAuxDte05() {
		return AuxDte05;
	}
	public void setAuxDte05(String auxDte05) {
		AuxDte05 = auxDte05;
	}
	public String getAuxDte06() {
		return AuxDte06;
	}
	public void setAuxDte06(String auxDte06) {
		AuxDte06 = auxDte06;
	}
	public String getAuxDte07() {
		return AuxDte07;
	}
	public void setAuxDte07(String auxDte07) {
		AuxDte07 = auxDte07;
	}
	public String getAuxDte08() {
		return AuxDte08;
	}
	public void setAuxDte08(String auxDte08) {
		AuxDte08 = auxDte08;
	}
	public String getAuxDte09() {
		return AuxDte09;
	}
	public void setAuxDte09(String auxDte09) {
		AuxDte09 = auxDte09;
	}
	public String getAuxDte10() {
		return AuxDte10;
	}
	public void setAuxDte10(String auxDte10) {
		AuxDte10 = auxDte10;
	}
	public List<AccessLevel> getAccessLevels() {
		return accessLevels;
	}
	public void setAccessLevels(List<AccessLevel> accessLevels) {
		this.accessLevels = accessLevels;
	}
	public String getCHTypeDescription() {
		return CHTypeDescription;
	}
	public void setCHTypeDescription(String cHTypeDescription) {
		CHTypeDescription = cHTypeDescription;
	}
}
