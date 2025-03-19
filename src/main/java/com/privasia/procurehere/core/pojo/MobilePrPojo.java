package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoItem;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrApproval;
import com.privasia.procurehere.core.entity.PrComment;
import com.privasia.procurehere.core.entity.PrDocument;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
public class MobilePrPojo implements Serializable {

	private static final long serialVersionUID = -7175607611589273195L;

	private String id;

	private String prId;

	private String name;

	private String referenceNumber;

	private String requester;

	private String description;

	private String currency;

	private String currencyName;

	private String creatorName;

	private String unitName;

	private String paymentTerm;

	private String supplierName;

	private List<PrItem> prItems;

	private String remarks;

	private String termsAndConditions;

	private BigDecimal total;

	private String taxDescription;

	private BigDecimal additionalTax;

	private BigDecimal grandTotal;

	private List<PrDocument> documents;

	private String decimal;

	private List<PrApproval> approvers;

	private List<Comments> comments;

	private String templateName;

	private String businessUnit;

	private String costCenter;

	private List<PoItem> poItems;

	private String receiver;

	public MobilePrPojo() {

	}

	public MobilePrPojo(Pr pr) {
		this.id = pr.getId();
		this.prId = pr.getPrId();
		this.name = pr.getName();
		this.referenceNumber = pr.getReferenceNumber();
		this.requester = pr.getRequester();
		this.description = pr.getDescription();
		this.decimal = pr.getDecimal();
		this.currency = pr.getCurrency() != null ? pr.getCurrency().getCurrencyCode() : null;
		this.currencyName = pr.getCurrency() != null ? pr.getCurrency().getCurrencyName() : null;
		this.creatorName = pr.getCreatedBy() != null ? pr.getCreatedBy().getName() : null;

		this.paymentTerm = pr.getPaymentTerm();
		if (StringUtils.checkString(pr.getSupplierName()).length() > 0) {
			this.supplierName = pr.getSupplierName();
		} else if (pr.getSupplier() != null) {
			this.supplierName = pr.getSupplier().getSupplier() != null ? pr.getSupplier().getSupplier().getCompanyName() : "";
		}

		if (CollectionUtil.isNotEmpty(pr.getPrItems())) {
			this.prItems = new ArrayList<>();
			for (PrItem prItem : pr.getPrItems()) {
				this.prItems.add(prItem.createMobileShallowCopy());
			}
		}
		if (CollectionUtil.isNotEmpty(pr.getPrDocuments())) {
			this.documents = new ArrayList<>();
			for (PrDocument prDocument : pr.getPrDocuments()) {
				this.documents.add(prDocument.createMobileShallowCopy());
			}
		}

		this.remarks = pr.getRemarks();
		this.termsAndConditions = pr.getTermsAndConditions();
		this.total = pr.getTotal();
		this.taxDescription = pr.getTaxDescription();
		this.additionalTax = pr.getAdditionalTax();
		this.grandTotal = pr.getGrandTotal();
		if (CollectionUtil.isNotEmpty(pr.getPrComments())) {
			this.comments = new ArrayList<>();
			for (PrComment comment : pr.getPrComments()) {
				comment.setTransientIsApproved(comment.isApproved());
				this.comments.add(comment.createMobileShallowCopy());
			}
		}
	}

	public MobilePrPojo(Po po) {
		this.id = po.getId();
		this.prId = po.getPoId();
		this.name = po.getName();
		this.referenceNumber = po.getReferenceNumber();
//		this.requester = po.getRequester();
		this.receiver = po.getDeliveryReceiver();
		this.description = po.getDescription();
		this.decimal = po.getDecimal();
		this.currency = po.getCurrency() != null ? po.getCurrency().getCurrencyCode() : null;
		this.currencyName = po.getCurrency() != null ? po.getCurrency().getCurrencyName() : null;
		this.creatorName = po.getCreatedBy() != null ? po.getCreatedBy().getName() : null;

		this.paymentTerm = po.getPaymentTerm();
		if (StringUtils.checkString(po.getSupplierName()).length() > 0) {
			this.supplierName = po.getSupplierName();
		} else if (po.getSupplier() != null) {
			this.supplierName = po.getSupplier().getSupplier() != null ? po.getSupplier().getSupplier().getCompanyName() : "";
		}

		if (CollectionUtil.isNotEmpty(po.getPoItems())) {
			this.poItems = new ArrayList<>();
			for (PoItem prItem : po.getPoItems()) {
				this.poItems.add(prItem.createMobileShallowCopy());
			}
		}

		this.remarks = po.getRemarks();
		this.termsAndConditions = po.getTermsAndConditions();
		this.total = po.getTotal();
		this.taxDescription = po.getTaxDescription();
		this.additionalTax = po.getAdditionalTax();
		this.grandTotal = po.getGrandTotal();

	}

	/**
	 * @return businessUnit
	 */
	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return costCenter
	 */
	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
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
	 * @return the prId
	 */
	public String getPrId() {
		return prId;
	}

	/**
	 * @param prId the prId to set
	 */
	public void setPrId(String prId) {
		this.prId = prId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the unitName
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * @param unitName the unitName to set
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
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
	 * @return the prItems
	 */
	public List<PrItem> getPrItems() {
		return prItems;
	}

	/**
	 * @param prItems the prItems to set
	 */
	public void setPrItems(List<PrItem> prItems) {
		this.prItems = prItems;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the termsAndConditions
	 */
	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	/**
	 * @param termsAndConditions the termsAndConditions to set
	 */
	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
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

	/**
	 * @return the creatorName
	 */
	public String getCreatorName() {
		return creatorName;
	}

	/**
	 * @param creatorName the creatorName to set
	 */
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	/**
	 * @return the documents
	 */
	public List<PrDocument> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<PrDocument> documents) {
		this.documents = documents;
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
	 * @return the approvers
	 */
	public List<PrApproval> getApprovers() {
		return approvers;
	}

	/**
	 * @param approvers the approvers to set
	 */
	public void setApprovers(List<PrApproval> approvers) {
		this.approvers = approvers;
	}

	/**
	 * @return the comments
	 */
	public List<Comments> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}

	/**
	 * @return the currencyName
	 */
	public String getCurrencyName() {
		return currencyName;
	}

	/**
	 * @param currencyName the currencyName to set
	 */
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return the poItems
	 */
	public List<PoItem> getPoItems() {
		return poItems;
	}

	/**
	 * @param poItems the poItems to set
	 */
	public void setPoItems(List<PoItem> poItems) {
		this.poItems = poItems;
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

	public String toLogString() {
		return "MobilePrPojo [id=" + id + ", prId=" + prId + ", name=" + name + ", referenceNumber=" + referenceNumber + ", requester=" + requester + ", description=" + description + ", unitName=" + unitName + ", paymentTerm=" + paymentTerm + ", supplierName=" + supplierName + ", remarks=" + remarks + ", termsAndConditions=" + termsAndConditions + ", total=" + total + ", taxDescription=" + taxDescription + ", additionalTax=" + additionalTax + ", grandTotal=" + grandTotal + ", creatorName=" + creatorName + "]";
	}
}