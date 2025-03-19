package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.RfpComment;
import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfpEventContact;
import com.privasia.procurehere.core.entity.RfpEventDocument;
import com.privasia.procurehere.core.entity.RfpEventMeeting;
import com.privasia.procurehere.core.entity.RfpReminder;

/**
 * @author sarang
 */
public class RfpEventApiPojo implements Serializable{
	
	private static final long serialVersionUID = -1346054291392743281L;
	private List<String> suppliers;
	private List<RfpEventDocument> documents;
	private List<RfpEventMeeting> meetings;
	private List<RfpEventContact> eventContacts;
	private List<String> eventCorrespondenceAddress;
	private List<RfpEventBq> bqs;
	private List<RfpEnvelop> envelop;
	private List<RfpCq> cqs;
	private List<RfpReminder> reminder;
	private List<String> eventViewers;
	private List<String> eventEditors;
	private List<String> eventTeamMember;
	private List<String> approvals;
	private List<String> awardedSuppliers;
	private String awardedPrice;
	private List<RfpComment> comment;
	private List<String> industryCategories;
	private String erpAwardRefId;
	private String erpAwardResponse;

	public List<String> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<String> suppliers) {
		this.suppliers = suppliers;
	}

	public List<RfpEventDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<RfpEventDocument> documents) {
		this.documents = documents;
	}

	public List<RfpEventMeeting> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<RfpEventMeeting> meetings) {
		this.meetings = meetings;
	}

	public List<RfpEventContact> getEventContacts() {
		return eventContacts;
	}

	public void setEventContacts(List<RfpEventContact> eventContacts) {
		this.eventContacts = eventContacts;
	}

	public List<String> getEventCorrespondenceAddress() {
		return eventCorrespondenceAddress;
	}

	public void setEventCorrespondenceAddress(List<String> eventCorrespondenceAddress) {
		this.eventCorrespondenceAddress = eventCorrespondenceAddress;
	}

	public List<RfpEventBq> getBqs() {
		return bqs;
	}

	public void setBqs(List<RfpEventBq> bqs) {
		this.bqs = bqs;
	}

	public List<RfpEnvelop> getEnvelop() {
		return envelop;
	}

	public void setEnvelop(List<RfpEnvelop> envelop) {
		this.envelop = envelop;
	}

	public List<RfpCq> getCqs() {
		return cqs;
	}

	public void setCqs(List<RfpCq> cqs) {
		this.cqs = cqs;
	}

	public List<RfpReminder> getReminder() {
		return reminder;
	}

	public void setReminder(List<RfpReminder> reminder) {
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

	public List<String> getApprovals() {
		return approvals;
	}

	public void setApprovals(List<String> approvals) {
		this.approvals = approvals;
	}

	public List<String> getAwardedSuppliers() {
		return awardedSuppliers;
	}

	public void setAwardedSuppliers(List<String> awardedSuppliers) {
		this.awardedSuppliers = awardedSuppliers;
	}

	public String getAwardedPrice() {
		return awardedPrice;
	}

	public void setAwardedPrice(String awardedPrice) {
		this.awardedPrice = awardedPrice;
	}

	public List<RfpComment> getComment() {
		return comment;
	}

	public void setComment(List<RfpComment> comment) {
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
