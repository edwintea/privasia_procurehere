package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Nitin Otageri
 */
public class PoReviseSnapshot implements Serializable {

	private static final long serialVersionUID = -2311906720698865972L;

	private String id;

	private String requester;

	private Date deliveryDate;

	private String deliveryAddress;

	private String deliveryReceiver;

	private List<PoRevisedSnapshotItem> poItems;

	private String deliveryAddressTitle;

	private String deliveryAddressLine1;

	private String deliveryAddressLine2;

	private String deliveryAddressCity;

	private String deliveryAddressState;

	private String deliveryAddressZip;

	private String deliveryAddressCountry;

	private BigDecimal total = new BigDecimal(0);

	private String taxDescription;

	private BigDecimal additionalTax = new BigDecimal(0);

	private BigDecimal grandTotal = new BigDecimal(0);

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
	 * @return the requester
	 */
	public String getRequester() {
		return requester;
	}

	/**
	 * @param requester the requester to set
	 */
	public void setRequester(String requester) {
		this.requester = requester;
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
	 * @return the deliveryReceiver
	 */
	public String getDeliveryReceiver() {
		return deliveryReceiver;
	}

	/**
	 * @param deliveryReceiver the deliveryReceiver to set
	 */
	public void setDeliveryReceiver(String deliveryReceiver) {
		this.deliveryReceiver = deliveryReceiver;
	}

	/**
	 * @return the poItems
	 */
	public List<PoRevisedSnapshotItem> getPoItems() {
		return poItems;
	}

	/**
	 * @param poItems the poItems to set
	 */
	public void setPoItems(List<PoRevisedSnapshotItem> poItems) {
		this.poItems = poItems;
	}

	/**
	 * @return the deliveryAddressTitle
	 */
	public String getDeliveryAddressTitle() {
		return deliveryAddressTitle;
	}

	/**
	 * @param deliveryAddressTitle the deliveryAddressTitle to set
	 */
	public void setDeliveryAddressTitle(String deliveryAddressTitle) {
		this.deliveryAddressTitle = deliveryAddressTitle;
	}

	/**
	 * @return the deliveryAddressLine1
	 */
	public String getDeliveryAddressLine1() {
		return deliveryAddressLine1;
	}

	/**
	 * @param deliveryAddressLine1 the deliveryAddressLine1 to set
	 */
	public void setDeliveryAddressLine1(String deliveryAddressLine1) {
		this.deliveryAddressLine1 = deliveryAddressLine1;
	}

	/**
	 * @return the deliveryAddressLine2
	 */
	public String getDeliveryAddressLine2() {
		return deliveryAddressLine2;
	}

	/**
	 * @param deliveryAddressLine2 the deliveryAddressLine2 to set
	 */
	public void setDeliveryAddressLine2(String deliveryAddressLine2) {
		this.deliveryAddressLine2 = deliveryAddressLine2;
	}

	/**
	 * @return the deliveryAddressCity
	 */
	public String getDeliveryAddressCity() {
		return deliveryAddressCity;
	}

	/**
	 * @param deliveryAddressCity the deliveryAddressCity to set
	 */
	public void setDeliveryAddressCity(String deliveryAddressCity) {
		this.deliveryAddressCity = deliveryAddressCity;
	}

	/**
	 * @return the deliveryAddressState
	 */
	public String getDeliveryAddressState() {
		return deliveryAddressState;
	}

	/**
	 * @param deliveryAddressState the deliveryAddressState to set
	 */
	public void setDeliveryAddressState(String deliveryAddressState) {
		this.deliveryAddressState = deliveryAddressState;
	}

	/**
	 * @return the deliveryAddressZip
	 */
	public String getDeliveryAddressZip() {
		return deliveryAddressZip;
	}

	/**
	 * @param deliveryAddressZip the deliveryAddressZip to set
	 */
	public void setDeliveryAddressZip(String deliveryAddressZip) {
		this.deliveryAddressZip = deliveryAddressZip;
	}

	/**
	 * @return the deliveryAddressCountry
	 */
	public String getDeliveryAddressCountry() {
		return deliveryAddressCountry;
	}

	/**
	 * @param deliveryAddressCountry the deliveryAddressCountry to set
	 */
	public void setDeliveryAddressCountry(String deliveryAddressCountry) {
		this.deliveryAddressCountry = deliveryAddressCountry;
	}

	/**
	 * @return the total
	 */
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	/**
	 * @return the taxDescription
	 */
	public String getTaxDescription() {
		return taxDescription;
	}

	/**
	 * @param taxDescription the taxDescription to set
	 */
	public void setTaxDescription(String taxDescription) {
		this.taxDescription = taxDescription;
	}

	/**
	 * @return the additionalTax
	 */
	public BigDecimal getAdditionalTax() {
		return additionalTax;
	}

	/**
	 * @param additionalTax the additionalTax to set
	 */
	public void setAdditionalTax(BigDecimal additionalTax) {
		this.additionalTax = additionalTax;
	}

	/**
	 * @return the grandTotal
	 */
	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	/**
	 * @param grandTotal the grandTotal to set
	 */
	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

}
