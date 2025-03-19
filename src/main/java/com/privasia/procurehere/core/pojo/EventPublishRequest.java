/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author pooja
 */
public class EventPublishRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7650862409660190519L;

	@JsonProperty("TenderID")
	private String eventId;

	@JsonProperty("Title0")
	private String eventName;

	@JsonProperty("TenderDescription")
	private String eventDescription;

	@JsonProperty("KodBidang")
	private String categoryCode;

	@JsonProperty("Lokasi_x002f_Ladang")
	private String deliveryAddress;

	@JsonProperty("GradeMin")
	private String minimumRating;

	@JsonProperty("GradeMax")
	private String maximumRating;

	@JsonProperty("LawatanTapakTaklimat")
	private String yesNo;

	@JsonProperty("TarikhLawatanTapak")
	private String siteVisitDate;

	@JsonProperty("LokasiLawatanTapakTaklimat")
	private String siteVisitLocation;

	@JsonProperty("HargaJualanNaskahTender")
	private String fee;

	@JsonProperty("TenderDeposit")
	private String tenderDeposit;

	@JsonProperty("CompanyCode")
	private String businessUnit;

	@JsonProperty("PegawaiUntukDihubungi")
	private String contactPerson;

	@JsonProperty("EmelPegawaiUntukDihubungi")
	private String emailAddress;

	@JsonProperty("NoTelefon")
	private String contactNo;

	@JsonProperty("JenisIklan")
	private String eventType;

	@JsonProperty("UntukDiterbitkan")
	private String publish;

	@JsonProperty("Attachment")
	private String supplierInviteUrl;

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return the eventDescription
	 */
	public String getEventDescription() {
		return eventDescription;
	}

	/**
	 * @param eventDescription the eventDescription to set
	 */
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	/**
	 * @return the categoryCode
	 */
	public String getCategoryCode() {
		return categoryCode;
	}

	/**
	 * @param categoryCode the categoryCode to set
	 */
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	/**
	 * @return the deliveryAddress
	 */
	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the minimumRating
	 */
	public String getMinimumRating() {
		return minimumRating;
	}

	/**
	 * @param minimumRating the minimumRating to set
	 */
	public void setMinimumRating(String minimumRating) {
		this.minimumRating = minimumRating;
	}

	/**
	 * @return the maximumRating
	 */
	public String getMaximumRating() {
		return maximumRating;
	}

	/**
	 * @param maximumRating the maximumRating to set
	 */
	public void setMaximumRating(String maximumRating) {
		this.maximumRating = maximumRating;
	}

	/**
	 * @return the yesNo
	 */
	public String getYesNo() {
		return yesNo;
	}

	/**
	 * @param yesNo the yesNo to set
	 */
	public void setYesNo(String yesNo) {
		this.yesNo = yesNo;
	}

	/**
	 * @return the siteVisitDate
	 */
	public String getSiteVisitDate() {
		return siteVisitDate;
	}

	/**
	 * @param siteVisitDate the siteVisitDate to set
	 */
	public void setSiteVisitDate(String siteVisitDate) {
		this.siteVisitDate = siteVisitDate;
	}

	/**
	 * @return the siteVisitLocation
	 */
	public String getSiteVisitLocation() {
		return siteVisitLocation;
	}

	/**
	 * @param siteVisitLocation the siteVisitLocation to set
	 */
	public void setSiteVisitLocation(String siteVisitLocation) {
		this.siteVisitLocation = siteVisitLocation;
	}

	/**
	 * @return the fee
	 */
	public String getFee() {
		return fee;
	}

	/**
	 * @param fee the fee to set
	 */
	public void setFee(String fee) {
		this.fee = fee;
	}

	/**
	 * @return the tenderDeposit
	 */
	public String getTenderDeposit() {
		return tenderDeposit;
	}

	/**
	 * @param tenderDeposit the tenderDeposit to set
	 */
	public void setTenderDeposit(String tenderDeposit) {
		this.tenderDeposit = tenderDeposit;
	}

	/**
	 * @return the businessUnit
	 */
	public String getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return the contactPerson
	 */
	public String getContactPerson() {
		return contactPerson;
	}

	/**
	 * @param contactPerson the contactPerson to set
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the contactNo
	 */
	public String getContactNo() {
		return contactNo;
	}

	/**
	 * @param contactNo the contactNo to set
	 */
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the publish
	 */
	public String getPublish() {
		return publish;
	}

	/**
	 * @param publish the publish to set
	 */
	public void setPublish(String publish) {
		this.publish = publish;
	}

	/**
	 * @return the supplierInviteUrl
	 */
	public String getSupplierInviteUrl() {
		return supplierInviteUrl;
	}

	/**
	 * @param supplierInviteUrl the supplierInviteUrl to set
	 */
	public void setSupplierInviteUrl(String supplierInviteUrl) {
		this.supplierInviteUrl = supplierInviteUrl;
	}

}