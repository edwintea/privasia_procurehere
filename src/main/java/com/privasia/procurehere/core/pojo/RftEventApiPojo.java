package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.RftComment;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEventApproval;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.RftEventContact;
import com.privasia.procurehere.core.entity.RftEventDocument;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.entity.RftReminder;
import com.privasia.procurehere.core.entity.Supplier;

/**
 * @author sarang
 */
public class RftEventApiPojo implements Serializable{
	
	private static final long serialVersionUID = -614960938046491309L;

	private List<String> suppliers;
	private List<RftEventDocument> documents;
	private List<RftEventMeeting> meetings;
	private List<RftEventContact> eventContacts;
	private List<String> eventCorrespondenceAddress;
	private List<RftEventBq> bqs;
	private List<RftEnvelop> envelop;
	private List<RftCq> cqs;;
	private List<RftReminder> reminder;
	private List<RftComment> comment;
	private List<String> eventTeamMember;
	private List<RftEventApproval> approvals;
	private List<Supplier> awardedSuppliers;
	private BigDecimal awardedPrice;
	private List<String> industryCategories;
	private String erpAwardRefId;
	private String erpAwardResponse;

	public List<String> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<String> suppliers) {
		this.suppliers = suppliers;
	}

	public List<RftEventDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<RftEventDocument> documents) {
		this.documents = documents;
	}

	public List<RftEventMeeting> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<RftEventMeeting> meetings) {
		this.meetings = meetings;
	}

	public List<RftEventContact> getEventContacts() {
		return eventContacts;
	}

	public void setEventContacts(List<RftEventContact> eventContacts) {
		this.eventContacts = eventContacts;
	}

	public List<String> getEventCorrespondenceAddress() {
		return eventCorrespondenceAddress;
	}

	public void setEventCorrespondenceAddress(List<String> eventCorrespondenceAddress) {
		this.eventCorrespondenceAddress = eventCorrespondenceAddress;
	}

	public List<RftEventBq> getBqs() {
		return bqs;
	}

	public void setBqs(List<RftEventBq> bqs) {
		this.bqs = bqs;
	}

	public List<RftEnvelop> getEnvelop() {
		return envelop;
	}

	public void setEnvelop(List<RftEnvelop> envelop) {
		this.envelop = envelop;
	}

	public List<RftCq> getCqs() {
		return cqs;
	}

	public void setCqs(List<RftCq> cqs) {
		this.cqs = cqs;
	}

	public List<RftReminder> getReminder() {
		return reminder;
	}

	public void setReminder(List<RftReminder> reminder) {
		this.reminder = reminder;
	}

	public List<RftComment> getComment() {
		return comment;
	}

	public void setComment(List<RftComment> comment) {
		this.comment = comment;
	}

	public List<String> getEventTeamMember() {
		return eventTeamMember;
	}

	public void setEventTeamMember(List<String> eventTeamMember) {
		this.eventTeamMember = eventTeamMember;
	}

	public List<RftEventApproval> getApprovals() {
		return approvals;
	}

	public void setApprovals(List<RftEventApproval> approvals) {
		this.approvals = approvals;
	}

	public List<Supplier> getAwardedSuppliers() {
		return awardedSuppliers;
	}

	public void setAwardedSuppliers(List<Supplier> awardedSuppliers) {
		this.awardedSuppliers = awardedSuppliers;
	}

	public BigDecimal getAwardedPrice() {
		return awardedPrice;
	}

	public void setAwardedPrice(BigDecimal awardedPrice) {
		this.awardedPrice = awardedPrice;
	}

	public List<String> getIndustryCategories() {
		return industryCategories;
	}

	public void setIndustryCategories(List<String> industryCategories) {
		this.industryCategories = industryCategories;
	}

	public String getErpAwardRefId() {
		return erpAwardRefId;
	}

	public void setErpAwardRefId(String erpAwardRefId) {
		this.erpAwardRefId = erpAwardRefId;
	}

	public String getErpAwardResponse() {
		return erpAwardResponse;
	}

	public void setErpAwardResponse(String erpAwardResponse) {
		this.erpAwardResponse = erpAwardResponse;
	}

}
