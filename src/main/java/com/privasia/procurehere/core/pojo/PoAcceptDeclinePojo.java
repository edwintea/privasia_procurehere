/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ravi
 */
public class PoAcceptDeclinePojo implements Serializable {

	private static final long serialVersionUID = -4823174842147080937L;

	private String id;

	private String poDocNo;

	private String referenceNumber;

	private Date actionDate;

	private Boolean accepted;

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
	 * @return the poDocNo
	 */
	public String getPoDocNo() {
		return poDocNo;
	}

	/**
	 * @param poDocNo the poDocNo to set
	 */
	public void setPoDocNo(String poDocNo) {
		this.poDocNo = poDocNo;
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
	 * @return the actionDate
	 */
	public Date getActionDate() {
		return actionDate;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	/**
	 * @return the accepted
	 */
	public Boolean getAccepted() {
		return accepted;
	}

	/**
	 * @param accepted the accepted to set
	 */
	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

}
