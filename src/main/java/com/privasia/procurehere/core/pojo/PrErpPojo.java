package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrComment;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
public class PrErpPojo implements Serializable {

	private static final long serialVersionUID = 3254946964591682885L;

	private String id;

	private String referenceNumber;

	private Date deliveryDate;

	private String requester;

	private String costCenter;

	private String vendorCode;

	private String prId;

	private String generalRemarks;

	private String approverName;

	private String approvalRemarks;

	private Date prCreatedDate;

	private Date approvedDate;

	private String erpSeqNo;

	private List<PrItemErpPojo> prItems;

	private String supplierName;

	private String supplierAddress;

	private String supplierTelNumber;

	public PrErpPojo() {

	}

	public PrErpPojo(Pr pr) {
		this.id = pr.getId();
		this.referenceNumber = pr.getReferenceNumber();
		this.deliveryDate = pr.getDeliveryDate();
		this.requester = pr.getRequester();
		this.costCenter = pr.getCostCenter() != null ? pr.getCostCenter().getCostCenter() : "";
		this.vendorCode = pr.getSupplier() != null ? pr.getSupplier().getVendorCode() : "";
		if (pr.getSupplier() == null) {
			this.supplierName = StringUtils.checkString(pr.getSupplierName());
			this.supplierAddress = StringUtils.checkString(pr.getSupplierAddress());
			this.supplierTelNumber = StringUtils.checkString(pr.getSupplierTelNumber());
		}
		this.prId = pr.getPrId();
		this.generalRemarks = pr.getRemarks();

		if (CollectionUtil.isNotEmpty(pr.getPrComments())) {
			PrComment prComment = pr.getPrComments().get(pr.getPrComments().size() - 1);
			this.approverName = prComment.getCreatedBy() != null ? prComment.getCreatedBy().getName() : "";
			this.approvalRemarks = prComment.getComment();
			this.approvedDate = prComment.getCreatedDate();
		} else {
			this.approvedDate = pr.getPrCreatedDate();
		}

		this.prCreatedDate = pr.getPrCreatedDate();

		if (CollectionUtil.isNotEmpty(pr.getPrItems())) {
			this.prItems = new ArrayList<>();
			for (PrItem prItem : pr.getPrItems()) {
				PrItemErpPojo prItemErpPojo = new PrItemErpPojo(prItem);
				this.prItems.add(prItemErpPojo);
			}
		}

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
	 * @return the vendorCode
	 */
	public String getVendorCode() {
		return vendorCode;
	}

	/**
	 * @param vendorCode the vendorCode to set
	 */
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
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
	 * @return the generalRemarks
	 */
	public String getGeneralRemarks() {
		return generalRemarks;
	}

	/**
	 * @param generalRemarks the generalRemarks to set
	 */
	public void setGeneralRemarks(String generalRemarks) {
		this.generalRemarks = generalRemarks;
	}

	/**
	 * @return the approverName
	 */
	public String getApproverName() {
		return approverName;
	}

	/**
	 * @param approverName the approverName to set
	 */
	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}

	/**
	 * @return the approvalRemarks
	 */
	public String getApprovalRemarks() {
		return approvalRemarks;
	}

	/**
	 * @param approvalRemarks the approvalRemarks to set
	 */
	public void setApprovalRemarks(String approvalRemarks) {
		this.approvalRemarks = approvalRemarks;
	}

	/**
	 * @return the prCreatedDate
	 */
	public Date getPrCreatedDate() {
		return prCreatedDate;
	}

	/**
	 * @param prCreatedDate the prCreatedDate to set
	 */
	public void setPrCreatedDate(Date prCreatedDate) {
		this.prCreatedDate = prCreatedDate;
	}

	/**
	 * @return the approvedDate
	 */
	public Date getApprovedDate() {
		return approvedDate;
	}

	/**
	 * @param approvedDate the approvedDate to set
	 */
	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	/**
	 * @return the prItems
	 */
	public List<PrItemErpPojo> getPrItems() {
		return prItems;
	}

	/**
	 * @param prItems the prItems to set
	 */
	public void setPrItems(List<PrItemErpPojo> prItems) {
		this.prItems = prItems;
	}

	public String getErpSeqNo() {
		return erpSeqNo;
	}

	public void setErpSeqNo(String erpSeqNo) {
		this.erpSeqNo = erpSeqNo;
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
	 * @return the supplierTelNumber
	 */
	public String getSupplierTelNumber() {
		return supplierTelNumber;
	}

	/**
	 * @param supplierTelNumber the supplierTelNumber to set
	 */
	public void setSupplierTelNumber(String supplierTelNumber) {
		this.supplierTelNumber = supplierTelNumber;
	}

	public String toLogString() {
		return "PrErpPojo [id=" + id + ", referenceNumber=" + referenceNumber + ", deliveryDate=" + deliveryDate + ", requester=" + requester + ", costCenter=" + costCenter + ", vendorCode=" + vendorCode + ", prId=" + prId + ", generalRemarks=" + generalRemarks + ", approverName=" + approverName + ", approvalRemarks=" + approvalRemarks + ", prCreatedDate=" + prCreatedDate + ", approvedDate=" + approvedDate + "]";
	}

}
