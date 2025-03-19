/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author Nitin Otageri
 */
public class EvaluationSuppliersPojo implements Serializable {

	private static final long serialVersionUID = -2565080217855935379L;

	private String supplierName;
	private String contactName;
	private String contactNo;
	private String designation;
	private String email;
	private String status;
	private Boolean isQualify;
	private String reason;
	private String disqualifyRemarks;
	private String disqualifiedTime;
	private String disqualifiedBy;
	private String disqualifiedEnvelope;
	private String submisionTime;
	private String totalAmt;
	private String totalItemTaxAmt;
	private String sumTaxAmt;
	private String sumAmt;
	private String grandTotal;

	private String attendName;
	private String attendDesignation;
	private String attendEmail;
	private String attendContact;

	private String bqName;
	private String remark;

	private String supplierName2;
	private String remark2;
	private String disqualifiedTime2;
	private String disqualifiedBy2;
	private String disqualifiedEnvelope2;
	private Boolean isQualify2;
	private Boolean submitted;
	
	
	/**
	 * 
	 * @return
	 */
	public Boolean getIsQualify2() {
		return isQualify2;
	}

	/**
	 * 
	 * @param isQualify2
	 */
	public void setIsQualify2(Boolean isQualify2) {
		this.isQualify2 = isQualify2;
	}

	/**
	 * @return the disqualifiedTime2
	 */
	public String getDisqualifiedTime2() {
		return disqualifiedTime2;
	}

	/**
	 * @param disqualifiedTime2 the disqualifiedTime2 to set
	 */
	public void setDisqualifiedTime2(String disqualifiedTime2) {
		this.disqualifiedTime2 = disqualifiedTime2;
	}

	/**
	 * @return the disqualifiedBy2
	 */
	public String getDisqualifiedBy2() {
		return disqualifiedBy2;
	}

	/**
	 * @param disqualifiedBy2 the disqualifiedBy2 to set
	 */
	public void setDisqualifiedBy2(String disqualifiedBy2) {
		this.disqualifiedBy2 = disqualifiedBy2;
	}

	/**
	 * @return the disqualifiedEnvelope2
	 */
	public String getDisqualifiedEnvelope2() {
		return disqualifiedEnvelope2;
	}

	/**
	 * @param disqualifiedEnvelope2 the disqualifiedEnvelope2 to set
	 */
	public void setDisqualifiedEnvelope2(String disqualifiedEnvelope2) {
		this.disqualifiedEnvelope2 = disqualifiedEnvelope2;
	}

	public Boolean getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Boolean submitted) {
		this.submitted = submitted;
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
	 * @return the contactName
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
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
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the isQualify
	 */
	public Boolean getIsQualify() {
		return isQualify;
	}

	/**
	 * @param isQualify the isQualify to set
	 */
	public void setIsQualify(Boolean isQualify) {
		this.isQualify = isQualify;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the disqualifiedBy
	 */
	public String getDisqualifiedBy() {
		return disqualifiedBy;
	}

	/**
	 * @param disqualifiedBy the disqualifiedBy to set
	 */
	public void setDisqualifiedBy(String disqualifiedBy) {
		this.disqualifiedBy = disqualifiedBy;
	}

	/**
	 * @return the totalAmt
	 */
	public String getTotalAmt() {
		return totalAmt;
	}

	/**
	 * @param totalAmt the totalAmt to set
	 */
	public void setTotalAmt(String totalAmt) {
		this.totalAmt = totalAmt;
	}

	/**
	 * @return the totalItemTaxAmt
	 */
	public String getTotalItemTaxAmt() {
		return totalItemTaxAmt;
	}

	/**
	 * @param totalItemTaxAmt the totalItemTaxAmt to set
	 */
	public void setTotalItemTaxAmt(String totalItemTaxAmt) {
		this.totalItemTaxAmt = totalItemTaxAmt;
	}

	/**
	 * @return the sumTaxAmt
	 */
	public String getSumTaxAmt() {
		return sumTaxAmt;
	}

	/**
	 * @param sumTaxAmt the sumTaxAmt to set
	 */
	public void setSumTaxAmt(String sumTaxAmt) {
		this.sumTaxAmt = sumTaxAmt;
	}

	/**
	 * @return the sumAmt
	 */
	public String getSumAmt() {
		return sumAmt;
	}

	/**
	 * @param sumAmt the sumAmt to set
	 */
	public void setSumAmt(String sumAmt) {
		this.sumAmt = sumAmt;
	}

	/**
	 * @return the grandTotal
	 */
	public String getGrandTotal() {
		return grandTotal;
	}

	/**
	 * @param grandTotal the grandTotal to set
	 */
	public void setGrandTotal(String grandTotal) {
		this.grandTotal = grandTotal;
	}

	/**
	 * @return the attendName
	 */
	public String getAttendName() {
		return attendName;
	}

	/**
	 * @param attendName the attendName to set
	 */
	public void setAttendName(String attendName) {
		this.attendName = attendName;
	}

	/**
	 * @return the attendDesignation
	 */
	public String getAttendDesignation() {
		return attendDesignation;
	}

	/**
	 * @param attendDesignation the attendDesignation to set
	 */
	public void setAttendDesignation(String attendDesignation) {
		this.attendDesignation = attendDesignation;
	}

	/**
	 * @return the attendEmail
	 */
	public String getAttendEmail() {
		return attendEmail;
	}

	/**
	 * @param attendEmail the attendEmail to set
	 */
	public void setAttendEmail(String attendEmail) {
		this.attendEmail = attendEmail;
	}

	/**
	 * @return the attendContact
	 */
	public String getAttendContact() {
		return attendContact;
	}

	/**
	 * @param attendContact the attendContact to set
	 */
	public void setAttendContact(String attendContact) {
		this.attendContact = attendContact;
	}

	/**
	 * @return the bqName
	 */
	public String getBqName() {
		return bqName;
	}

	/**
	 * @param bqName the bqName to set
	 */
	public void setBqName(String bqName) {
		this.bqName = bqName;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluationSuppliersPojo [supplierName=" + supplierName + ", contactName=" + contactName + ", contactNo=" + contactNo + ", remark=" + remark + ", totalItemTaxAmt=" + totalItemTaxAmt + ", status=" + status + ", grandTotal=" + grandTotal + ", bqName=" + bqName + "]";
	}

	public String getSupplierName2() {
		return supplierName2;
	}

	public void setSupplierName2(String supplierName2) {
		this.supplierName2 = supplierName2;
	}

	public String getRemark2() {
		return remark2;
	}

	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}

	/**
	 * @return the submisionTime
	 */
	public String getSubmisionTime() {
		return submisionTime;
	}

	/**
	 * @param submisionTime the submisionTime to set
	 */
	public void setSubmisionTime(String submisionTime) {
		this.submisionTime = submisionTime;
	}

	public String getDisqualifiedTime() {
		return disqualifiedTime;
	}

	public void setDisqualifiedTime(String disqualifiedTime) {
		this.disqualifiedTime = disqualifiedTime;
	}

	public String getDisqualifyRemarks() {
		return disqualifyRemarks;
	}

	public void setDisqualifyRemarks(String disqualifyRemarks) {
		this.disqualifyRemarks = disqualifyRemarks;
	}

	public String getDisqualifiedEnvelope() {
		return disqualifiedEnvelope;
	}

	public void setDisqualifiedEnvelope(String disqualifiedEnvelope) {
		this.disqualifiedEnvelope = disqualifiedEnvelope;
	}

}
