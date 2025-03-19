package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.RfaComment;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaEventContact;
import com.privasia.procurehere.core.entity.RfaEventCorrespondenceAddress;
import com.privasia.procurehere.core.entity.RfaEventDocument;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfaReminder;

/**
 * @author sarang
 */
public class RfaEventApiPojo implements Serializable{
	
	private static final long serialVersionUID = -5823338557077649364L;

	private List<String> suppliers;
	private List<RfaEventDocument> documents;
	private List<RfaEventMeeting> meetings;
	private List<RfaEventContact> eventContacts;
	private List<RfaEventCorrespondenceAddress> eventCorrespondenceAddress;
	private List<RfaEventBq> bqs;
	private List<RfaEnvelop> envelop;
	private List<RfaCq> cqs;
	private List<RfaReminder> rfaEndReminder;
	private List<RfaReminder> rfaStartReminder;
	private String auctionType;
	private String auctionDurationType;
	private String auctionStartDelayType;
	private String auctionDuration;
	private String auctionStartDelay;
	private String timeExtensionType;
	private String timeExtensionDurationType;
	private String timeExtensionDuration;
	private String timeExtensionLeadingBidType;
	private String timeExtensionLeadingBidValue;
	private String extensionCount;
	private String bidderDisqualify;
	private boolean autoDisqualify;
	private RfaEvent previousAuction;
	private List<String> eventViewers;
	private List<String> eventEditors;
	private List<String> teamMembers;
	private List<RfaComment> comment;
	private String auctionComplitationTime;
	private String winningSupplier;
	private String winningPrice;
	private String auctionResumeDateTime;
	private String auctionSuspandAmount;
	private List<String> approvals;
	private List<String> awardedSuppliers;
	private String awardedPrice;
	private String totalExtensions;
	private List<String> industryCategories;
	private String erpAwardRefId;
	private String erpAwardResponse;

	public List<String> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<String> suppliers) {
		this.suppliers = suppliers;
	}

	public List<RfaEventDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<RfaEventDocument> documents) {
		this.documents = documents;
	}

	public List<RfaEventMeeting> getMeetings() {
		return meetings;
	}

	public void setMeetings(List<RfaEventMeeting> meetings) {
		this.meetings = meetings;
	}

	public List<RfaEventContact> getEventContacts() {
		return eventContacts;
	}

	public void setEventContacts(List<RfaEventContact> eventContacts) {
		this.eventContacts = eventContacts;
	}

	public List<RfaEventCorrespondenceAddress> getEventCorrespondenceAddress() {
		return eventCorrespondenceAddress;
	}

	public void setEventCorrespondenceAddress(List<RfaEventCorrespondenceAddress> eventCorrespondenceAddress) {
		this.eventCorrespondenceAddress = eventCorrespondenceAddress;
	}

	public List<RfaEventBq> getBqs() {
		return bqs;
	}

	public void setBqs(List<RfaEventBq> bqs) {
		this.bqs = bqs;
	}

	public List<RfaEnvelop> getEnvelop() {
		return envelop;
	}

	public void setEnvelop(List<RfaEnvelop> envelop) {
		this.envelop = envelop;
	}

	public List<RfaCq> getCqs() {
		return cqs;
	}

	public void setCqs(List<RfaCq> cqs) {
		this.cqs = cqs;
	}

	public List<RfaReminder> getRfaEndReminder() {
		return rfaEndReminder;
	}

	public void setRfaEndReminder(List<RfaReminder> rfaEndReminder) {
		this.rfaEndReminder = rfaEndReminder;
	}

	public List<RfaReminder> getRfaStartReminder() {
		return rfaStartReminder;
	}

	public void setRfaStartReminder(List<RfaReminder> rfaStartReminder) {
		this.rfaStartReminder = rfaStartReminder;
	}

	public String getAuctionType() {
		return auctionType;
	}

	public void setAuctionType(String auctionType) {
		this.auctionType = auctionType;
	}

	public String getAuctionDurationType() {
		return auctionDurationType;
	}

	public void setAuctionDurationType(String auctionDurationType) {
		this.auctionDurationType = auctionDurationType;
	}

	public String getAuctionStartDelayType() {
		return auctionStartDelayType;
	}

	public void setAuctionStartDelayType(String auctionStartDelayType) {
		this.auctionStartDelayType = auctionStartDelayType;
	}

	public String getAuctionDuration() {
		return auctionDuration;
	}

	public void setAuctionDuration(String auctionDuration) {
		this.auctionDuration = auctionDuration;
	}

	public String getAuctionStartDelay() {
		return auctionStartDelay;
	}

	public void setAuctionStartDelay(String auctionStartDelay) {
		this.auctionStartDelay = auctionStartDelay;
	}

	public String getTimeExtensionType() {
		return timeExtensionType;
	}

	public void setTimeExtensionType(String timeExtensionType) {
		this.timeExtensionType = timeExtensionType;
	}

	public String getTimeExtensionDurationType() {
		return timeExtensionDurationType;
	}

	public void setTimeExtensionDurationType(String timeExtensionDurationType) {
		this.timeExtensionDurationType = timeExtensionDurationType;
	}

	public String getTimeExtensionDuration() {
		return timeExtensionDuration;
	}

	public void setTimeExtensionDuration(String timeExtensionDuration) {
		this.timeExtensionDuration = timeExtensionDuration;
	}

	public String getTimeExtensionLeadingBidType() {
		return timeExtensionLeadingBidType;
	}

	public void setTimeExtensionLeadingBidType(String timeExtensionLeadingBidType) {
		this.timeExtensionLeadingBidType = timeExtensionLeadingBidType;
	}

	public String getTimeExtensionLeadingBidValue() {
		return timeExtensionLeadingBidValue;
	}

	public void setTimeExtensionLeadingBidValue(String timeExtensionLeadingBidValue) {
		this.timeExtensionLeadingBidValue = timeExtensionLeadingBidValue;
	}

	public String getExtensionCount() {
		return extensionCount;
	}

	public void setExtensionCount(String extensionCount) {
		this.extensionCount = extensionCount;
	}

	public String getBidderDisqualify() {
		return bidderDisqualify;
	}

	public void setBidderDisqualify(String bidderDisqualify) {
		this.bidderDisqualify = bidderDisqualify;
	}

	public boolean isAutoDisqualify() {
		return autoDisqualify;
	}

	public void setAutoDisqualify(boolean autoDisqualify) {
		this.autoDisqualify = autoDisqualify;
	}

	public RfaEvent getPreviousAuction() {
		return previousAuction;
	}

	public void setPreviousAuction(RfaEvent previousAuction) {
		this.previousAuction = previousAuction;
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

	public List<String> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<String> teamMembers) {
		this.teamMembers = teamMembers;
	}

	public List<RfaComment> getComment() {
		return comment;
	}

	public void setComment(List<RfaComment> comment) {
		this.comment = comment;
	}

	public String getAuctionComplitationTime() {
		return auctionComplitationTime;
	}

	public void setAuctionComplitationTime(String auctionComplitationTime) {
		this.auctionComplitationTime = auctionComplitationTime;
	}

	public String getWinningSupplier() {
		return winningSupplier;
	}

	public void setWinningSupplier(String winningSupplier) {
		this.winningSupplier = winningSupplier;
	}

	public String getWinningPrice() {
		return winningPrice;
	}

	public void setWinningPrice(String winningPrice) {
		this.winningPrice = winningPrice;
	}

	public String getAuctionResumeDateTime() {
		return auctionResumeDateTime;
	}

	public void setAuctionResumeDateTime(String auctionResumeDateTime) {
		this.auctionResumeDateTime = auctionResumeDateTime;
	}

	public String getAuctionSuspandAmount() {
		return auctionSuspandAmount;
	}

	public void setAuctionSuspandAmount(String auctionSuspandAmount) {
		this.auctionSuspandAmount = auctionSuspandAmount;
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

	public String getTotalExtensions() {
		return totalExtensions;
	}

	public void setTotalExtensions(String totalExtensions) {
		this.totalExtensions = totalExtensions;
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
