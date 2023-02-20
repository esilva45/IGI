package br.com.asyg.connector.prudential.wellcare.model;

public class Card {
	private int cardID;	
	private String clearCode;	
	private int cardNumber;
	private int facilityCode;
	private int cardType;
	private int CHID; //CardHolder
	private boolean cardDownloadRequired;
	private int cardState;
	private int partitionID;
	private String cardStartValidityDateTime;
	private String cardEndValidityDateTime;
	private int tempCardLink;
	private int originalCardState;
	private int iPRdrUserID;
	private boolean iPRdrAlwaysEnabled;
	private boolean visitorTemporaryCard;
	private boolean automaticCard;
	
	public int getCardID() {
		return cardID;
	}
	public void setCardID(int cardID) {
		this.cardID = cardID;
	}
	public String getClearCode() {
		return clearCode;
	}
	public void setClearCode(String clearCode) {
		this.clearCode = clearCode;
	}
	public int getFacilityCode() {
		return facilityCode;
	}
	public void setFacilityCode(int facilityCode) {
		this.facilityCode = facilityCode;
	}
	public int getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(int cardNumber) {
		this.cardNumber = cardNumber;
	}
	public boolean isCardDownloadRequired() {
		return cardDownloadRequired;
	}
	public void setCardDownloadRequired(boolean cardDownloadRequired) {
		this.cardDownloadRequired = cardDownloadRequired;
	}
	public int getCardType() {
		return cardType;
	}
	public void setCardType(int cardType) {
		this.cardType = cardType;
	}
	public int getCHID() {
		return CHID;
	}
	public void setCHID(int cHID) {
		CHID = cHID;
	}
	public int getCardState() {
		return cardState;
	}
	public void setCardState(int cardState) {
		this.cardState = cardState;
	}
	public int getPartitionID() {
		return partitionID;
	}
	public void setPartitionID(int partitionID) {
		this.partitionID = partitionID;
	}
	public String getCardStartValidityDateTime() {
		return cardStartValidityDateTime;
	}
	public void setCardStartValidityDateTime(String cardStartValidityDateTime) {
		this.cardStartValidityDateTime = cardStartValidityDateTime;
	}
	public String getCardEndValidityDateTime() {
		return cardEndValidityDateTime;
	}
	public void setCardEndValidityDateTime(String cardEndValidityDateTime) {
		this.cardEndValidityDateTime = cardEndValidityDateTime;
	}
	public int getTempCardLink() {
		return tempCardLink;
	}
	public void setTempCardLink(int tempCardLink) {
		this.tempCardLink = tempCardLink;
	}
	public int getOriginalCardState() {
		return originalCardState;
	}
	public void setOriginalCardState(int originalCardState) {
		this.originalCardState = originalCardState;
	}
	public int getiPRdrUserID() {
		return iPRdrUserID;
	}
	public void setiPRdrUserID(int iPRdrUserID) {
		this.iPRdrUserID = iPRdrUserID;
	}
	public boolean isiPRdrAlwaysEnabled() {
		return iPRdrAlwaysEnabled;
	}
	public void setiPRdrAlwaysEnabled(boolean iPRdrAlwaysEnabled) {
		this.iPRdrAlwaysEnabled = iPRdrAlwaysEnabled;
	}
	public boolean isVisitorTemporaryCard() {
		return visitorTemporaryCard;
	}
	public void setVisitorTemporaryCard(boolean visitorTemporaryCard) {
		this.visitorTemporaryCard = visitorTemporaryCard;
	}
	public boolean isAutomaticCard() {
		return automaticCard;
	}
	public void setAutomaticCard(boolean automaticCard) {
		this.automaticCard = automaticCard;
	}
}
