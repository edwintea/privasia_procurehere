package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.RfqComment;
import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RfqEventContact;
import com.privasia.procurehere.core.entity.RfqEventDocument;
import com.privasia.procurehere.core.entity.RfqEventMeeting;
import com.privasia.procurehere.core.entity.RfqReminder;

/**
 * @author sarang
 */
public class RfqEventApiPojo implements Serializable{
	
	private static final long serialVersionUID = -7857221431684703662L;
	private List<String> suppliers;
	private List<RfqEventDocument> documents;
	private List<RfqEventMeeting> meetings;
	private List<RfqEventContact> eventContacts;
	private List<String> eventCorrespondenceAddress;
	private List<RfqEventBq> bqs;
	private List<RfqEnvelop> envelop;
	private List<RfqCq> cqs;
	private List<RfqReminder> reminder;
	private List<String> eventViewers;
	private List<String> eventEditors;
	private List<String> eventTeamMember;
	private List<String> awardedSuppliers;
	private List<String> approvals;
	private String awardedPrice;
	private List<RfqComment> comment;
	private List<String> industryCategories;
	private String erpAwardRefId;
	private String erpAwardResponse;

	public List<String> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<String> suppliers) {
		this.suppliers = suppliers;
	}

	public List<RfqEventDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<RfqEventDocument> documents) {
		this.documents = documents;
	}

	public List<RfqEventMeeting> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<RfqEventMeeting> meetings) {
		this.meetings = meetings;
	}

	public List<RfqEventContact> getEventContacts() {
		return eventContacts;
	}

	public void setEventContacts(List<RfqEventContact> eventContacts) {
		this.eventContacts = eventContacts;
	}

	public List<String> getEventCorrespondenceAddress() {
		return eventCorrespondenceAddress;
	}

	public void setEventCorrespondenceAddress(List<String> eventCorrespondenceAddress) {
		this.eventCorrespondenceAddress = eventCorrespondenceAddress;
	}

	public List<RfqEventBq> getBqs() {
		return bqs;
	}

	public void setBqs(List<RfqEventBq> bqs) {
		this.bqs = bqs;
	}

	public List<RfqEnvelop> getEnvelop() {
		return envelop;
	}

	public void setEnvelop(List<RfqEnvelop> envelop) {
		this.envelop = envelop;
	}

	public List<RfqCq> getCqs() {
		return cqs;
	}

	public void setCqs(List<RfqCq> cqs) {
		this.cqs = cqs;
	}

	public List<RfqReminder> getReminder() {
		return reminder;
	}

	public void setReminder(List<RfqReminder> reminder) {
		this.reminder = reminder;
	}

	public List<String> getEventViewers() {
		return eventViewers;
	}

	public void setEventViewers(List<String> eventViewers) {
		this.eventViewers = eventViewers;
	}

	public List<String> getEventEditors() {
		return eventEditors;
	}

	public void setEventEditors(List<String> eventEditors) {
		this.eventEditors = eventEditors;
	}

	public List<String> getEventTeamMember() {
		return eventTeamMember;
	}

	public void setEventTeamMember(List<String> eventTeamMember) {
		this.eventTeamMember = eventTeamMember;
	}

	public List<String> getAwardedSuppliers() {
		return awardedSuppliers;
	}

	public void setAwardedSuppliers(List<String> awardedSuppliers) {
		this.awardedSuppliers = awardedSuppliers;
	}

	public List<String> getApprovals() {
		return approvals;
	}

	public void setApprovals(List<String> approvals) {
		this.approvals = approvals;
	}

	public String getAwardedPrice() {
		return awardedPrice;
	}

	public void setAwardedPrice(String awardedPrice) {
		this.awardedPrice = awardedPrice;
	}

	public List<RfqComment> getComment() {
		return comment;
	}

	public void setComment(List<RfqComment> comment) {
		this.comment = comment;
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
