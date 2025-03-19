package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Giridhar
 */
public class EvaluationEnvelopSuppliersPojo implements Serializable {

	private static final long serialVersionUID = -8707982595221258745L;

	private String supplierName;
	private String submittedBy;
	private Date submissionDate;
	private String remarks;

	private List<EvaluationBqPojo> bqs;
	private List<EvaluationCqPojo> cqs;

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
	 * @return the submittedBy
	 */
	public String getSubmittedBy() {
		return submittedBy;
	}

	/**
	 * @param submittedBy the submittedBy to set
	 */
	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}

	/**
	 * @return the submissionDate
	 */
	public Date getSubmissionDate() {
		return submissionDate;
	}

	/**
	 * @param submissionDate the submissionDate to set
	 */
	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
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
	 * @return the bqs
	 */
	public List<EvaluationBqPojo> getBqs() {
		return bqs;
	}

	/**
	 * @param bqs the bqs to set
	 */
	public void setBqs(List<EvaluationBqPojo> bqs) {
		this.bqs = bqs;
	}

	/**
	 * @return the cqs
	 */
	public List<EvaluationCqPojo> getCqs() {
		return cqs;
	}

	/**
	 * @param cqs the cqs to set
	 */
	public void setCqs(List<EvaluationCqPojo> cqs) {
		this.cqs = cqs;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluationEnvelopSuppliersPojo [supplierName=" + supplierName + ", submittedBy=" + submittedBy + ", submissionDate=" + submissionDate + ", Remarks=" + remarks + ", bqs=" + bqs + ", cqs=" + cqs + "]";
	}

}
