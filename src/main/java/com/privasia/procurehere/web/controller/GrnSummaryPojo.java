package com.privasia.procurehere.web.controller;

import java.awt.Image;
import java.io.Serializable;
import java.util.List;

/**
 * @author pooja
 */
public class GrnSummaryPojo implements Serializable {

	private static final long serialVersionUID = -6115341428058695551L;

	private String grnTitle;
	private String paymentTerm;
	private String paymentNote;
	private String poNumber;
	private String referenceNumber;

	// Delivery Address
	private String correspondAddress;
	private String deliveryAddress;

	private String grnId;
	private String owner;
	private String baseCurrency;
	private String decimal;
	private String costCenter;
	private String totalAmount;
	private String receiver;
	private String taxnumber;

	private String supplierName;
	private String supplierTaxNumber;
	private String supplierContact;
	private String grnDescription;

	// GRN Items
	private List<GrnItemsSummaryPojo> grnItems;

	// Buyer Address
	private String buyerAddress;
	private String comanyName;
	private Image logo;
	private String displayName;

	// Supplier Address
	private String supplierAddress;

	private String businesUnit;
	private String createdDate;
	private String goodsReceiptDate;
	private String grnReceivedDate;

	/**
	 * @return the grnTitle
	 */
	public String getGrnTitle() {
		return grnTitle;
	}

	/**
	 * @param grnTitle the grnTitle to set
	 */
	public void setGrnTitle(String grnTitle) {
		this.grnTitle = grnTitle;
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
	 * @return the paymentNote
	 */
	public String getPaymentNote() {
		return paymentNote;
	}

	/**
	 * @param paymentNote the paymentNote to set
	 */
	public void setPaymentNote(String paymentNote) {
		this.paymentNote = paymentNote;
	}

	/**
	 * @return the poNumber
	 */
	public String getPoNumber() {
		return poNumber;
	}

	/**
	 * @param poNumber the poNumber to set
	 */
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * @return the correspondAddress
	 */
	public String getCorrespondAddress() {
		return correspondAddress;
	}

	/**
	 * @param correspondAddress the correspondAddress to set
	 */
	public void setCorrespondAddress(String correspondAddress) {
		this.correspondAddress = correspondAddress;
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
	 * @return the grnId
	 */
	public String getGrnId() {
		return grnId;
	}

	/**
	 * @param grnId the grnId to set
	 */
	public void setGrnId(String grnId) {
		this.grnId = grnId;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the baseCurrency
	 */
	public String getBaseCurrency() {
		return baseCurrency;
	}

	/**
	 * @param baseCurrency the baseCurrency to set
	 */
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	/**
	 * @return the decimal
	 */
	public String getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return the costCenter
	 */
	public String getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the totalAmount
	 */
	public String getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the receiver
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	/**
	 * @return the taxnumber
	 */
	public String getTaxnumber() {
		return taxnumber;
	}

	/**
	 * @param taxnumber the taxnumber to set
	 */
	public void setTaxnumber(String taxnumber) {
		this.taxnumber = taxnumber;
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
	 * @return the supplierTaxNumber
	 */
	public String getSupplierTaxNumber() {
		return supplierTaxNumber;
	}

	/**
	 * @param supplierTaxNumber the supplierTaxNumber to set
	 */
	public void setSupplierTaxNumber(String supplierTaxNumber) {
		this.supplierTaxNumber = supplierTaxNumber;
	}

	/**
	 * @return the supplierContact
	 */
	public String getSupplierContact() {
		return supplierContact;
	}

	/**
	 * @param supplierContact the supplierContact to set
	 */
	public void setSupplierContact(String supplierContact) {
		this.supplierContact = supplierContact;
	}

	/**
	 * @return the grnDescription
	 */
	public String getGrnDescription() {
		return grnDescription;
	}

	/**
	 * @param grnDescription the grnDescription to set
	 */
	public void setGrnDescription(String grnDescription) {
		this.grnDescription = grnDescription;
	}

	/**
	 * @return the grnItems
	 */
	public List<GrnItemsSummaryPojo> getGrnItems() {
		return grnItems;
	}

	/**
	 * @param grnItems the grnItems to set
	 */
	public void setGrnItems(List<GrnItemsSummaryPojo> grnItems) {
		this.grnItems = grnItems;
	}

	/**
	 * @return the buyerAddress
	 */
	public String getBuyerAddress() {
		return buyerAddress;
	}

	/**
	 * @param buyerAddress the buyerAddress to set
	 */
	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}

	/**
	 * @return the comanyName
	 */
	public String getComanyName() {
		return comanyName;
	}

	/**
	 * @param comanyName the comanyName to set
	 */
	public void setComanyName(String comanyName) {
		this.comanyName = comanyName;
	}

	/**
	 * @return the logo
	 */
	public Image getLogo() {
		return logo;
	}

	/**
	 * @param logo the logo to set
	 */
	public void setLogo(Image logo) {
		this.logo = logo;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * @return the businesUnit
	 */
	public String getBusinesUnit() {
		return businesUnit;
	}

	/**
	 * @param businesUnit the businesUnit to set
	 */
	public void setBusinesUnit(String businesUnit) {
		this.businesUnit = businesUnit;
	}

	/**
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the goodsReceiptDate
	 */
	public String getGoodsReceiptDate() {
		return goodsReceiptDate;
	}

	/**
	 * @param goodsReceiptDate the goodsReceiptDate to set
	 */
	public void setGoodsReceiptDate(String goodsReceiptDate) {
		this.goodsReceiptDate = goodsReceiptDate;
	}

	/**
	 * @return the grnReceivedDate
	 */
	public String getGrnReceivedDate() {
		return grnReceivedDate;
	}

	/**
	 * @param grnReceivedDate the grnReceivedDate to set
	 */
	public void setGrnReceivedDate(String grnReceivedDate) {
		this.grnReceivedDate = grnReceivedDate;
	}

}
