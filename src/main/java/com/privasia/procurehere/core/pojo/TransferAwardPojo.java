package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author yogesh
 */
public class TransferAwardPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3431779256884197901L;

	private String eventId;

	private String eventName;

	private String eventOwner;

	private String awardRemark;

	private BigDecimal totalAwardPrice;

	private String businessUnitName;

	private String eventReferenceNumber;

	private Date createdDate;

	private Date startDate;

	private Date endDate;

	private Integer validityDays;

	private Date deliveryDate; // new field in BQ

	private String paymentTerm;

	private DeliveryAddressPojo deliveryAddress;

	private String erpSeqNo;

	private String currencyCode;

	List<TransferAwardBqPojo> bqList;

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
	 * @return the eventReferenceNumber
	 */
	public String getEventReferenceNumber() {
		return eventReferenceNumber;
	}

	/**
	 * @param eventReferenceNumber the eventReferenceNumber to set
	 */
	public void setEventReferenceNumber(String eventReferenceNumber) {
		this.eventReferenceNumber = eventReferenceNumber;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the validityDays
	 */
	public Integer getValidityDays() {
		return validityDays;
	}

	/**
	 * @param validityDays the validityDays to set
	 */
	public void setValidityDays(Integer validityDays) {
		this.validityDays = validityDays;
	}

	/**
	 * @return the deliveryDate
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the paymentTerm
	 */
	public String getPaymentTerm() {
		return paymentTerm;
	}

	/**
	 * @param paymentTerm the paymentTerm to set
	 */
	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	/**
	 * @return the deliveryAddress
	 */
	public DeliveryAddressPojo getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(DeliveryAddressPojo deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getErpSeqNo() {
		return erpSeqNo;
	}

	public void setErpSeqNo(String erpSeqNo) {
		this.erpSeqNo = erpSeqNo;
	}

	public List<TransferAwardBqPojo> getBqList() {
		return bqList;
	}

	public void setBqList(List<TransferAwardBqPojo> bqList) {
		this.bqList = bqList;
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
	 * @return the businessUnitName
	 */
	public String getBusinessUnitName() {
		return businessUnitName;
	}

	/**
	 * @param businessUnitName the businessUnitName to set
	 */
	public void setBusinessUnitName(String businessUnitName) {
		this.businessUnitName = businessUnitName;
	}

	/**
	 * @return the eventOwner
	 */
	public String getEventOwner() {
		return eventOwner;
	}

	/**
	 * @param eventOwner the eventOwner to set
	 */
	public void setEventOwner(String eventOwner) {
		this.eventOwner = eventOwner;
	}

	/**
	 * @return the awardRemark
	 */
	public String getAwardRemark() {
		return awardRemark;
	}

	/**
	 * @param awardRemark the awardRemark to set
	 */
	public void setAwardRemark(String awardRemark) {
		this.awardRemark = awardRemark;
	}

	/**
	 * @return the totalAwardPrice
	 */
	public BigDecimal getTotalAwardPrice() {
		return totalAwardPrice;
	}

	/**
	 * @param totalAwardPrice the totalAwardPrice to set
	 */
	public void setTotalAwardPrice(BigDecimal totalAwardPrice) {
		this.totalAwardPrice = totalAwardPrice;
	}

	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

}
