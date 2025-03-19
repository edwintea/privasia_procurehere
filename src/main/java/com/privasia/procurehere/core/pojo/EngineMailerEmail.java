/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ravi
 */
public class EngineMailerEmail implements Serializable {

	private static final long serialVersionUID = -4457107980309364937L;

	@JsonProperty("UserKey")
	private String userKey;

	@JsonProperty("CampaignName")
	private String campaignName;

	@JsonProperty("APIKey")
	private String aPIKey;

	@JsonProperty("ToEmail")
	private String toEmail;

	@JsonProperty("Subject")
	private String subject;

	@JsonProperty("SenderEmail")
	private String senderEmail;

	@JsonProperty("SubmittedContent")
	private String submittedContent;

	@JsonProperty("SenderName")
	private String senderName;

	@JsonProperty("CCEmails")
	private List<String> cCEmails;

	@JsonProperty("BCCEmails")
	private List<String> bCCEmails;

	@JsonProperty("Attachments")
	List<EngineMailerMailAttachments> attachments;

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getaPIKey() {
		return aPIKey;
	}

	public void setaPIKey(String aPIKey) {
		this.aPIKey = aPIKey;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getSubmittedContent() {
		return submittedContent;
	}

	public void setSubmittedContent(String submittedContent) {
		this.submittedContent = submittedContent;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public List<String> getcCEmails() {
		return cCEmails;
	}

	public void setcCEmails(List<String> cCEmails) {
		this.cCEmails = cCEmails;
	}

	public List<String> getbCCEmails() {
		return bCCEmails;
	}

	public void setbCCEmails(List<String> bCCEmails) {
		this.bCCEmails = bCCEmails;
	}

	public List<EngineMailerMailAttachments> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<EngineMailerMailAttachments> attachments) {
		this.attachments = attachments;
	}

}
