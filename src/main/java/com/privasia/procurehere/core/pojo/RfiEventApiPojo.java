package com.privasia.procurehere.core.pojo;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiComment;
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEventContact;
import com.privasia.procurehere.core.entity.RfiEventDocument;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfiReminder;

/**
 * @author sarang
 */
public class RfiEventApiPojo extends EventApiPojo {
	 
	private static final long serialVersionUID = -5241784561787737304L;
	private List<String> suppliers;
	private List<RfiEventDocument> documents;
	private List<RfiEventMeeting> meetings;
	private List<RfiEventContact> eventContacts;
	private List<String> eventCorrespondenceAddress;
	private List<RfiEnvelop> envelop;
	private List<RfiCq> cqs;
	private List<RfiReminder> reminder;
	private List<String> eventViewers;
	private List<String> eventEditors;
	private List<String> eventTeamMember;
	private List<String> approvals;
	private List<RfiComment> comment;
	private List<String> industryCategories;

	public List<String> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<String> suppliers) {
		this.suppliers = suppliers;
	}

	public List<RfiEventDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<RfiEventDocument> documents) {
		this.documents = documents;
	}

	public List<RfiEventMeeting> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<RfiEventMeeting> meetings) {
		this.meetings = meetings;
	}

	public List<RfiEventContact> getEventContacts() {
		return eventContacts;
	}

	public void setEventContacts(List<RfiEventContact> eventContacts) {
		this.eventContacts = eventContacts;
	}

	public List<String> getEventCorrespondenceAddress() {
		return eventCorrespondenceAddress;
	}

	public void setEventCorrespondenceAddress(List<String> eventCorrespondenceAddress) {
		this.eventCorrespondenceAddress = eventCorrespondenceAddress;
	}

	public List<RfiEnvelop> getEnvelop() {
		return envelop;
	}

	public void setEnvelop(List<RfiEnvelop> envelop) {
		this.envelop = envelop;
	}

	public List<RfiCq> getCqs() {
		return cqs;
	}

	public void setCqs(List<RfiCq> cqs) {
		this.cqs = cqs;
	}

	public List<RfiReminder> getReminder() {
		return reminder;
	}

	public void setReminder(List<RfiReminder> reminder) {
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

	public List<RfiComment> getComment() {
		return comment;
	}

	public void setComment(List<RfiComment> comment) {
		this.comment = comment;
	}

	public List<String> getIndustryCategories() {
		return industryCategories;
	}

	public void setIndustryCategories(List<String> industryCategories) {
		this.industryCategories = industryCategories;
	}

}
