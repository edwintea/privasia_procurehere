package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.enums.SourcingFormStatus;

/**
 * @author yogesh
 */
public class MobileRequestPojo implements Serializable {

	private static final long serialVersionUID = -5633294462530701915L;

	private String id;

	private String requestId;

	private String referenceNumber;

	private String requestName;

	private String requestDescription;

	private String requestOwner;

	private SourcingFormStatus status;

	private String decimal;

	private List<SourcingFormRequestBq> bqs;

	private List<SourcingFormApprovalRequest> approvers;

	private List<Comments> comments;

	private String templateName;

	
	
	
	public MobileRequestPojo() {

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
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
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
	 * @return the requestName
	 */
	public String getRequestName() {
		return requestName;
	}

	/**
	 * @param requestName the requestName to set
	 */
	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	/**
	 * @return the requestDescription
	 */
	public String getRequestDescription() {
		return requestDescription;
	}

	/**
	 * @param requestDescription the requestDescription to set
	 */
	public void setRequestDescription(String requestDescription) {
		this.requestDescription = requestDescription;
	}

	/**
	 * @return the requestOwner
	 */
	public String getRequestOwner() {
		return requestOwner;
	}

	/**
	 * @param requestOwner the requestOwner to set
	 */
	public void setRequestOwner(String requestOwner) {
		this.requestOwner = requestOwner;
	}

	/**
	 * @return the status
	 */
	public SourcingFormStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(SourcingFormStatus status) {
		this.status = status;
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
	 * @return the bqs
	 */
	public List<SourcingFormRequestBq> getBqs() {
		return bqs;
	}

	/**
	 * @param bqs the bqs to set
	 */
	public void setBqs(List<SourcingFormRequestBq> bqs) {
		this.bqs = bqs;
	}

	/**
	 * @return the approvers
	 */
	public List<SourcingFormApprovalRequest> getApprovers() {
		return approvers;
	}

	/**
	 * @param approvers the approvers to set
	 */
	public void setApprovers(List<SourcingFormApprovalRequest> approvers) {
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

}