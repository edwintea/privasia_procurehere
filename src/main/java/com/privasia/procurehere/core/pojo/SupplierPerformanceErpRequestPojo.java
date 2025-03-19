package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Nitin Otageri
 */
public class SupplierPerformanceErpRequestPojo implements Serializable {

	private static final long serialVersionUID = 8903208394981717555L;

	@JsonProperty("Unit_Code")
	private String businessUnit; // Business Unit

	@JsonProperty("Supplier_Code")
	private String supplierCode; // Vendor Code

	// Default to 02 as per PH-2791 - "JAD SAP-PH Phase 2 V1.3 18 May 2022 (1).xlsx"
	@JsonProperty("ALL_SCORE")
	private String allScore = "0";

	@JsonProperty("Price")
	private String price = "0"; // Criteria 1

	@JsonProperty("Quality")
	private String quality = "0"; // Criteria 2

	@JsonProperty("Delivery")
	private String delivery = "0"; // Criteria 3

	@JsonProperty("Service")
	private String service = "0"; // Criteria 4

	@JsonProperty("Weight_Price")
	private String priceWeightage = "0"; // Criteria 1 Weightage

	@JsonProperty("Weight_Quality")
	private String qualityWeightage = "0"; // Criteria 2 Weightage

	@JsonProperty("Weight_Delivery")
	private String deliveryWeightage = "0"; // Criteria 3 Weightage

	@JsonProperty("Weight_Service")
	private String serviceWeightage = "0"; // Criteria 4 Weightage

	@JsonIgnore
	private Integer overallScore; // Used only for data migration

	@JsonIgnore
	private String refNumber; // Used only for data migration

	@JsonIgnore
	private String supplierName; // Used only for data migration

	@JsonIgnore
	private Date concludeDate; // Used only for data migration

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
	 * @return the supplierCode
	 */
	public String getSupplierCode() {
		return supplierCode;
	}

	/**
	 * @param supplierCode the supplierCode to set
	 */
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	/**
	 * @return the allScore
	 */
	public String getAllScore() {
		return allScore;
	}

	/**
	 * @param allScore the allScore to set
	 */
	public void setAllScore(String allScore) {
		this.allScore = allScore;
	}

	/**
	 * @return the priceWeightage
	 */
	public String getPriceWeightage() {
		return priceWeightage;
	}

	/**
	 * @param priceWeightage the priceWeightage to set
	 */
	public void setPriceWeightage(String priceWeightage) {
		this.priceWeightage = priceWeightage;
	}

	/**
	 * @return the qualityWeightage
	 */
	public String getQualityWeightage() {
		return qualityWeightage;
	}

	/**
	 * @param qualityWeightage the qualityWeightage to set
	 */
	public void setQualityWeightage(String qualityWeightage) {
		this.qualityWeightage = qualityWeightage;
	}

	/**
	 * @return the deliveryWeightage
	 */
	public String getDeliveryWeightage() {
		return deliveryWeightage;
	}

	/**
	 * @param deliveryWeightage the deliveryWeightage to set
	 */
	public void setDeliveryWeightage(String deliveryWeightage) {
		this.deliveryWeightage = deliveryWeightage;
	}

	/**
	 * @return the serviceWeightage
	 */
	public String getServiceWeightage() {
		return serviceWeightage;
	}

	/**
	 * @param serviceWeightage the serviceWeightage to set
	 */
	public void setServiceWeightage(String serviceWeightage) {
		this.serviceWeightage = serviceWeightage;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @return the quality
	 */
	public String getQuality() {
		return quality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(String quality) {
		this.quality = quality;
	}

	/**
	 * @return the delivery
	 */
	public String getDelivery() {
		return delivery;
	}

	/**
	 * @param delivery the delivery to set
	 */
	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @return the overallScore
	 */
	public Integer getOverallScore() {
		return overallScore;
	}

	/**
	 * @param overallScore the overallScore to set
	 */
	public void setOverallScore(Integer overallScore) {
		this.overallScore = overallScore;
	}

	/**
	 * @return the refNumber
	 */
	public String getRefNumber() {
		return refNumber;
	}

	/**
	 * @param refNumber the refNumber to set
	 */
	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the concludeDate
	 */
	public Date getConcludeDate() {
		return concludeDate;
	}

	/**
	 * @param concludeDate the concludeDate to set
	 */
	public void setConcludeDate(Date concludeDate) {
		this.concludeDate = concludeDate;
	}

	@Override
	public String toString() {
		return "SupplierPerformanceErpRequestPojo [businessUnit=" + businessUnit + ", supplierCode=" + supplierCode + ", allScore=" + allScore + ", price=" + price + ", quality=" + quality + ", delivery=" + delivery + ", service=" + service + ", priceWeightage=" + priceWeightage + ", qualityWeightage=" + qualityWeightage + ", deliveryWeightage=" + deliveryWeightage + ", serviceWeightage=" + serviceWeightage + ", overallScore=" + overallScore + ", refNumber=" + refNumber + ", supplierName=" + supplierName + ", concludeDate=" + concludeDate + "]";
	}

}
