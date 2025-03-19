package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Giridhar
 */
public class EvaluationBqItemPojo implements Serializable {

	private static final long serialVersionUID = 3597002524187211427L;

	private String level;
	private String description;
	private String itemName;
	private String uom;
	private BigDecimal quantity;
	private BigDecimal unitPrice;
	private BigDecimal supplier1UnitPrice;
	private BigDecimal supplier1TotalAmt;
	private BigDecimal supplier2UnitPrice;
	private BigDecimal supplier2TotalAmt;
	private BigDecimal amount;
	private BigDecimal taxAmt;
	private BigDecimal totalAmt;
	private String totalAmtS;
	private String priceType;
	private List<EvaluationBqItemComments> review;
	private String decimal;
	private String imgPath;
	private BigDecimal grandTotal;
	private BigDecimal additionalTax;
	private BigDecimal totalAfterTax;
	private BigDecimal grandTotal1;
	private BigDecimal additionalTax1;
	private BigDecimal totalAfterTax1;
	private BigDecimal grandTotal2;
	private BigDecimal additionalTax2;
	private BigDecimal totalAfterTax2;
	private String additionalTaxDesc;
	private BigDecimal totalPrice;
	private String taxAmtS;
	private String remark;
	private String supplierName;
	private String supplier1Name;
	private String supplier2Name;
	private Boolean revisedBidSubmitted;
	private Boolean revisedBidSubmitted1;
	private Boolean revisedBidSubmitted2;
	private BigDecimal subtotal;
	private BigDecimal totalAmtTax;
	private String totalAfterTaxBq;
	private String additionalTaxBq;
	private Boolean addiTax;
	private String totalAfterTaxBqS;
	private String addiTaxS;
	private String grandTotalString;
	private String grandTotalVal;

	/**
	 * @return the subtotal
	 */
	public BigDecimal getSubtotal() {
		return subtotal;
	}

	/**
	 * @param subtotal the subtotal to set
	 */
	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	/**
	 * @return the totalAmtTax
	 */
	public BigDecimal getTotalAmtTax() {
		return totalAmtTax;
	}

	/**
	 * @param totalAmtTax the totalAmtTax to set
	 */
	public void setTotalAmtTax(BigDecimal totalAmtTax) {
		this.totalAmtTax = totalAmtTax;
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

	public String getTaxAmtS() {
		return taxAmtS;
	}

	public void setTaxAmtS(String taxAmtS) {
		this.taxAmtS = taxAmtS;
	}

	private String reviews;

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}

	/**
	 * @param uom the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}

	/**
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the unitPrice
	 */
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the taxAmt
	 */
	public BigDecimal getTaxAmt() {
		return taxAmt;
	}

	/**
	 * @param taxAmt the taxAmt to set
	 */
	public void setTaxAmt(BigDecimal taxAmt) {
		this.taxAmt = taxAmt;
	}

	/**
	 * @return the totalAmt
	 */
	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	/**
	 * @param totalAmt the totalAmt to set
	 */
	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	/**
	 * @return the review
	 */
	public List<EvaluationBqItemComments> getReview() {
		return review;
	}

	/**
	 * @param review the review to set
	 */
	public void setReview(List<EvaluationBqItemComments> review) {
		this.review = review;
	}

	/**
	 * @return the priceType
	 */
	public String getPriceType() {
		return priceType;
	}

	/**
	 * @param priceType the priceType to set
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
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
	 * @return the imgPath
	 */
	public String getImgPath() {
		return imgPath;
	}

	/**
	 * @param imgPath the imgPath to set
	 */
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
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

	/**
	 * @return the additionalTaxDesc
	 */
	public String getAdditionalTaxDesc() {
		return additionalTaxDesc;
	}

	/**
	 * @param additionalTaxDesc the additionalTaxDesc to set
	 */
	public void setAdditionalTaxDesc(String additionalTaxDesc) {
		this.additionalTaxDesc = additionalTaxDesc;
	}

	/**
	 * @return the totalAfterTax
	 */
	public BigDecimal getTotalAfterTax() {
		return totalAfterTax;
	}

	/**
	 * @param totalAfterTax the totalAfterTax to set
	 */
	public void setTotalAfterTax(BigDecimal totalAfterTax) {
		this.totalAfterTax = totalAfterTax;
	}

	/**
	 * @return the reviews
	 */
	public String getReviews() {
		return reviews;
	}

	/**
	 * @param reviews the reviews to set
	 */
	public void setReviews(String reviews) {
		this.reviews = reviews;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluationBqItemPojo [level=" + level + ", description=" + description + ", uom=" + uom + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", amount=" + amount + ", taxAmt=" + taxAmt + ", totalAmt=" + totalAmt + ", review=" + review + "]";
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getTotalAmtS() {
		return totalAmtS;
	}

	public void setTotalAmtS(String totalAmtS) {
		this.totalAmtS = totalAmtS;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getSupplier1UnitPrice() {
		return supplier1UnitPrice;
	}

	public void setSupplier1UnitPrice(BigDecimal supplier1UnitPrice) {
		this.supplier1UnitPrice = supplier1UnitPrice;
	}

	public BigDecimal getSupplier1TotalAmt() {
		return supplier1TotalAmt;
	}

	public void setSupplier1TotalAmt(BigDecimal supplier1TotalAmt) {
		this.supplier1TotalAmt = supplier1TotalAmt;
	}

	public BigDecimal getSupplier2UnitPrice() {
		return supplier2UnitPrice;
	}

	public void setSupplier2UnitPrice(BigDecimal supplier2UnitPrice) {
		this.supplier2UnitPrice = supplier2UnitPrice;
	}

	public BigDecimal getSupplier2TotalAmt() {
		return supplier2TotalAmt;
	}

	public void setSupplier2TotalAmt(BigDecimal supplier2TotalAmt) {
		this.supplier2TotalAmt = supplier2TotalAmt;
	}

	/**
	 * @return the supplier1Name
	 */
	public String getSupplier1Name() {
		return supplier1Name;
	}

	/**
	 * @param supplier1Name the supplier1Name to set
	 */
	public void setSupplier1Name(String supplier1Name) {
		this.supplier1Name = supplier1Name;
	}

	/**
	 * @return the supplier2Name
	 */
	public String getSupplier2Name() {
		return supplier2Name;
	}

	/**
	 * @param supplier2Name the supplier2Name to set
	 */
	public void setSupplier2Name(String supplier2Name) {
		this.supplier2Name = supplier2Name;
	}

	/**
	 * @return the grandTotal1
	 */
	public BigDecimal getGrandTotal1() {
		return grandTotal1;
	}

	/**
	 * @param grandTotal1 the grandTotal1 to set
	 */
	public void setGrandTotal1(BigDecimal grandTotal1) {
		this.grandTotal1 = grandTotal1;
	}

	/**
	 * @return the additionalTax1
	 */
	public BigDecimal getAdditionalTax1() {
		return additionalTax1;
	}

	/**
	 * @param additionalTax1 the additionalTax1 to set
	 */
	public void setAdditionalTax1(BigDecimal additionalTax1) {
		this.additionalTax1 = additionalTax1;
	}

	/**
	 * @return the totalAfterTax1
	 */
	public BigDecimal getTotalAfterTax1() {
		return totalAfterTax1;
	}

	/**
	 * @param totalAfterTax1 the totalAfterTax1 to set
	 */
	public void setTotalAfterTax1(BigDecimal totalAfterTax1) {
		this.totalAfterTax1 = totalAfterTax1;
	}

	/**
	 * @return the grandTotal2
	 */
	public BigDecimal getGrandTotal2() {
		return grandTotal2;
	}

	/**
	 * @param grandTotal2 the grandTotal2 to set
	 */
	public void setGrandTotal2(BigDecimal grandTotal2) {
		this.grandTotal2 = grandTotal2;
	}

	/**
	 * @return the additionalTax2
	 */
	public BigDecimal getAdditionalTax2() {
		return additionalTax2;
	}

	/**
	 * @param additionalTax2 the additionalTax2 to set
	 */
	public void setAdditionalTax2(BigDecimal additionalTax2) {
		this.additionalTax2 = additionalTax2;
	}

	/**
	 * @return the totalAfterTax2
	 */
	public BigDecimal getTotalAfterTax2() {
		return totalAfterTax2;
	}

	/**
	 * @param totalAfterTax2 the totalAfterTax2 to set
	 */
	public void setTotalAfterTax2(BigDecimal totalAfterTax2) {
		this.totalAfterTax2 = totalAfterTax2;
	}

	/**
	 * @return the revisedBidSubmitted
	 */
	public Boolean getRevisedBidSubmitted() {
		return revisedBidSubmitted;
	}

	/**
	 * @param revisedBidSubmitted the revisedBidSubmitted to set
	 */
	public void setRevisedBidSubmitted(Boolean revisedBidSubmitted) {
		this.revisedBidSubmitted = revisedBidSubmitted;
	}

	/**
	 * @return the revisedBidSubmitted1
	 */
	public Boolean getRevisedBidSubmitted1() {
		return revisedBidSubmitted1;
	}

	/**
	 * @param revisedBidSubmitted1 the revisedBidSubmitted1 to set
	 */
	public void setRevisedBidSubmitted1(Boolean revisedBidSubmitted1) {
		this.revisedBidSubmitted1 = revisedBidSubmitted1;
	}

	/**
	 * @return the revisedBidSubmitted2
	 */
	public Boolean getRevisedBidSubmitted2() {
		return revisedBidSubmitted2;
	}

	/**
	 * @param revisedBidSubmitted2 the revisedBidSubmitted2 to set
	 */
	public void setRevisedBidSubmitted2(Boolean revisedBidSubmitted2) {
		this.revisedBidSubmitted2 = revisedBidSubmitted2;
	}

	/**
	 * @return the totalAfterTaxBq
	 */
	public String getTotalAfterTaxBq() {
		return totalAfterTaxBq;
	}

	/**
	 * @param totalAfterTaxBq the totalAfterTaxBq to set
	 */
	public void setTotalAfterTaxBq(String totalAfterTaxBq) {
		this.totalAfterTaxBq = totalAfterTaxBq;
	}

	/**
	 * @return the additionalTaxBq
	 */
	public String getAdditionalTaxBq() {
		return additionalTaxBq;
	}

	/**
	 * @param additionalTaxBq the additionalTaxBq to set
	 */
	public void setAdditionalTaxBq(String additionalTaxBq) {
		this.additionalTaxBq = additionalTaxBq;
	}

	/**
	 * @return the addiTax
	 */
	public Boolean getAddiTax() {
		return addiTax;
	}

	/**
	 * @param addiTax the addiTax to set
	 */
	public void setAddiTax(Boolean addiTax) {
		this.addiTax = addiTax;
	}

	/**
	 * @return the totalAfterTaxBqS
	 */
	public String getTotalAfterTaxBqS() {
		return totalAfterTaxBqS;
	}

	/**
	 * @param totalAfterTaxBqS the totalAfterTaxBqS to set
	 */
	public void setTotalAfterTaxBqS(String totalAfterTaxBqS) {
		this.totalAfterTaxBqS = totalAfterTaxBqS;
	}

	/**
	 * @return the addiTaxS
	 */
	public String getAddiTaxS() {
		return addiTaxS;
	}

	/**
	 * @param addiTaxS the addiTaxS to set
	 */
	public void setAddiTaxS(String addiTaxS) {
		this.addiTaxS = addiTaxS;
	}

	public String getGrandTotalString() {
		return grandTotalString;
	}

	public void setGrandTotalString(String grandTotalString) {
		this.grandTotalString = grandTotalString;
	}

	public String getGrandTotalVal() {
		return grandTotalVal;
	}

	public void setGrandTotalVal(String grandTotalVal) {
		this.grandTotalVal = grandTotalVal;
	}

}
