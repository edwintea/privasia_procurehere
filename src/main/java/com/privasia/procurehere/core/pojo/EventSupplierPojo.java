package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.privasia.procurehere.core.utils.StringUtils;
import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * @author yogesh
 */

public class EventSupplierPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5657756405260157239L;

	private String id;

	private String supplierId;

	private String favSupplierId;

	private String companyName;

	private String companyContactNumber;

	private String companyFax;

	private String communicationEmail;

	private String timeZone;

	private String eventId;

	private String eventName;

	private String eventRefranceNo;

	private String tenantId;

	private String businessUnit;

	private String userName;

	private String diviceId;

	private String userId;

	private BigDecimal totalBqPrice;

	private String disqualifiedEnvelopId;

	private Boolean disqualify;

	private Integer disqualifiedEnvelopSeq;

	private Boolean emailNotifications;

	public EventSupplierPojo() {

	}

	// here id = eventSupplierID
	public EventSupplierPojo(String id, String supplierId, String companyName, String companyContactNumber, String communicationEmail) {
		this.id = id;
		this.supplierId = supplierId;
		this.companyName = companyName;
		this.companyContactNumber = companyContactNumber;
		this.communicationEmail = communicationEmail;
	}

	// here id = supplierId
	public EventSupplierPojo(String id, String companyName) {
		this.id = id;
		this.companyName = companyName;
	}

	public EventSupplierPojo(String id, String companyName, String timeZone) {
		this.id = id;
		this.companyName = companyName;
		this.timeZone = timeZone;
	}

	public EventSupplierPojo(String id, String supplierId, String timeZone, String tenantId, String userName, String communicationEmail, String diviceId, String businessUnit, String userId) {
		this.id = id;
		this.supplierId = supplierId;
		this.timeZone = timeZone;
		this.tenantId = tenantId;
		this.userName = userName;
		this.communicationEmail = communicationEmail;
		this.diviceId = diviceId;
		this.businessUnit = businessUnit;
		this.userId = userId;
	}

	public EventSupplierPojo(String id, String supplierId, String companyName, BigDecimal totalBqPrice) {
		this.id = id;
		this.supplierId = supplierId;
		this.companyName = companyName;
		this.totalBqPrice = totalBqPrice;
	}

	public EventSupplierPojo(String id, String supplierId, String tenantId, String userName, String communicationEmail, String diviceId, String businessUnit, String userId) {
		this.id = id;
		this.supplierId = supplierId;
		this.tenantId = tenantId;
		this.userName = userName;
		this.communicationEmail = communicationEmail;
		this.diviceId = diviceId;
		this.businessUnit = businessUnit;
		this.userId = userId;
	}

	public EventSupplierPojo(String id, String supplierId, String timeZone, String tenantId, String userName, String communicationEmail, boolean emailNotifications, String diviceId, String businessUnit, String userId, String companyName) {
		this.id = id;
		this.supplierId = supplierId;
		this.timeZone = timeZone;
		this.tenantId = tenantId;
		this.userName = userName;
		this.communicationEmail = communicationEmail;
		this.emailNotifications = emailNotifications;
		this.diviceId = diviceId;
		this.businessUnit = businessUnit;
		this.userId = userId;
		this.companyName = companyName;
	}

	public EventSupplierPojo(String id, String supplierId, String timeZone, String tenantId, String userName, String communicationEmail, String diviceId, String businessUnit, String userId, String companyName, String companyFax, String companyContactNumber, Boolean emailNotifications) {
		this.id = id;
		this.supplierId = supplierId;
		this.timeZone = timeZone;
		this.tenantId = tenantId;
		this.userName = userName;
		this.communicationEmail = communicationEmail;
		this.diviceId = diviceId;
		this.businessUnit = businessUnit;
		this.userId = userId;
		this.companyName = companyName;
		this.companyContactNumber = companyContactNumber;
		this.companyFax = companyFax;
		this.emailNotifications = emailNotifications;
	}

	public EventSupplierPojo(String id, String supplierId, String timeZone, String tenantId, String userName, String communicationEmail, boolean emailNotifications, String diviceId, String businessUnit, String userId, String companyName, String companyFax, String companyContactNumber, String favSupContact, String favSupFax) {
		this.id = id;
		this.supplierId = supplierId;
		this.timeZone = timeZone;
		this.tenantId = tenantId;
		this.userName = userName;
		this.communicationEmail = communicationEmail;
		this.diviceId = diviceId;
		this.businessUnit = businessUnit;
		this.userId = userId;
		this.companyName = companyName;
		this.companyContactNumber = StringUtils.checkString(favSupContact).isEmpty() ? companyContactNumber : favSupContact; // companyContactNumber
																																// //
																																// ==
																																// //
																																// supplier
																																// //
																																// mobile
																																// //
																																// number
		this.companyFax = StringUtils.checkString(favSupFax).isEmpty() ? companyFax : favSupFax;
		this.emailNotifications = emailNotifications;
	}

	public EventSupplierPojo(String id, String companyName, Boolean disqualify, String disqualifiedEnvelopId, Integer disqualifiedEnvelopSeq) {
		this.id = id;
		this.companyName = companyName;
		this.disqualify = disqualify;
		this.disqualifiedEnvelopId = disqualifiedEnvelopId;
		this.disqualifiedEnvelopSeq = disqualifiedEnvelopSeq;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the supplierId
	 */
	public String getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the favSupplierId
	 */
	public String getFavSupplierId() {
		return favSupplierId;
	}

	/**
	 * @param favSupplierId the favSupplierId to set
	 */
	public void setFavSupplierId(String favSupplierId) {
		this.favSupplierId = favSupplierId;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the companyContactNumber
	 */
	public String getCompanyContactNumber() {
		return companyContactNumber;
	}

	/**
	 * @param companyContactNumber the companyContactNumber to set
	 */
	public void setCompanyContactNumber(String companyContactNumber) {
		this.companyContactNumber = companyContactNumber;
	}

	/**
	 * @return the communicationEmail
	 */
	public String getCommunicationEmail() {
		return communicationEmail;
	}

	/**
	 * @param communicationEmail the communicationEmail to set
	 */
	public void setCommunicationEmail(String communicationEmail) {
		this.communicationEmail = communicationEmail;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

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
	 * @return the eventRefranceNo
	 */
	public String getEventRefranceNo() {
		return eventRefranceNo;
	}

	/**
	 * @param eventRefranceNo the eventRefranceNo to set
	 */
	public void setEventRefranceNo(String eventRefranceNo) {
		this.eventRefranceNo = eventRefranceNo;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
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
	 * @return the diviceId
	 */
	public String getDiviceId() {
		return diviceId;
	}

	/**
	 * @param diviceId the diviceId to set
	 */
	public void setDiviceId(String diviceId) {
		this.diviceId = diviceId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BigDecimal getTotalBqPrice() {
		return totalBqPrice;
	}

	public void setTotalBqPrice(BigDecimal totalBqPrice) {
		this.totalBqPrice = totalBqPrice;
	}

	/**
	 * @return the companyFax
	 */
	public String getCompanyFax() {
		return companyFax;
	}

	/**
	 * @param companyFax the companyFax to set
	 */
	public void setCompanyFax(String companyFax) {
		this.companyFax = companyFax;
	}

	public String getDisqualifiedEnvelopId() {
		return disqualifiedEnvelopId;
	}

	public void setDisqualifiedEnvelopId(String disqualifiedEnvelopId) {
		this.disqualifiedEnvelopId = disqualifiedEnvelopId;
	}

	public Boolean getDisqualify() {
		return disqualify;
	}

	public void setDisqualify(Boolean disqualify) {
		this.disqualify = disqualify;
	}

	public Boolean getEmailNotifications() {
		return emailNotifications;
	}

	public void setEmailNotifications(Boolean emailNotifications) {
		this.emailNotifications = emailNotifications;
	}

	/**
	 * @return the disqualifiedEnvelopSeq
	 */
	public Integer getDisqualifiedEnvelopSeq() {
		return disqualifiedEnvelopSeq;
	}

	/**
	 * @param disqualifiedEnvelopSeq the disqualifiedEnvelopSeq to set
	 */
	public void setDisqualifiedEnvelopSeq(Integer disqualifiedEnvelopSeq) {
		this.disqualifiedEnvelopSeq = disqualifiedEnvelopSeq;
	}

}
