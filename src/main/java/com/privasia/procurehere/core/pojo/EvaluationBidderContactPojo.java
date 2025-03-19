package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

public class EvaluationBidderContactPojo implements Serializable {

	private static final long serialVersionUID = -2151189026956740675L;

	private String companyName;
	private String contactName;
	private String phno;
	private String mobileNo;
	private String email;
	private String ipnumber;
	private String designation;
	private String status;
	private String actionStatus;
	private String actionDate;
	private String remark;
	private Integer numberOfBids = 0;
	private String compleAndTotalItem;
	private BigDecimal totalAfterTax;

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
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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
	 * @return the phno
	 */
	public String getPhno() {
		return phno;
	}

	/**
	 * @param phno the phno to set
	 */
	public void setPhno(String phno) {
		this.phno = phno;
	}

	/**
	 * @return the mobileNo
	 */
	public String getMobileNo() {
		return mobileNo;
	}

	/**
	 * @param mobileNo the mobileNo to set
	 */
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
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

	public String getIpnumber() {
		return ipnumber;
	}

	public void setIpnumber(String ipnumber) {
		this.ipnumber = ipnumber;
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
	 * @return the actionStatus
	 */
	public String getActionStatus() {
		return actionStatus;
	}

	/**
	 * @param actionStatus the actionStatus to set
	 */
	public void setActionStatus(String actionStatus) {
		this.actionStatus = actionStatus;
	}

	/**
	 * @return the numberOfBids
	 */
	public Integer getNumberOfBids() {
		return numberOfBids;
	}

	/**
	 * @param numberOfBids the numberOfBids to set
	 */
	public void setNumberOfBids(Integer numberOfBids) {
		this.numberOfBids = numberOfBids;
	}

	/**
	 * @return the actionDate
	 */
	public String getActionDate() {
		return actionDate;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
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

	/**
	 * @return the compleAndTotalItem
	 */
	public String getCompleAndTotalItem() {
		return compleAndTotalItem;
	}

	/**
	 * @param compleAndTotalItem the compleAndTotalItem to set
	 */
	public void setCompleAndTotalItem(String compleAndTotalItem) {
		this.compleAndTotalItem = compleAndTotalItem;
	}

	/**
	 * @return the totalAfterTax
	 */
	public BigDecimal getTotalAfterTax() {
		return totalAfterTax;
	}
	
	/**
	 * 
	 * @param totalAfterTax
	 */
	public void setTotalAfterTax(BigDecimal totalAfterTax) {
		this.totalAfterTax = totalAfterTax;
	}
}
