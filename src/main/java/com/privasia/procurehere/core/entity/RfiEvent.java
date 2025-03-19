/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.PendingEventPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import org.hibernate.annotations.Type;

/**
 * @author Ravi
 */

@Entity
@Table(name = "PROC_RFI_EVENTS")
@SqlResultSetMapping(name = "pendingEventResult", classes = { @ConstructorResult(targetClass = PendingEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventName"), @ColumnResult(name = "createdBy"), @ColumnResult(name = "createdDate"), @ColumnResult(name = "type"), @ColumnResult(name = "eventStart"), @ColumnResult(name = "eventEnd"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "unitName"), @ColumnResult(name = "sysEventId"), @ColumnResult(name = "eventUser") }) })
public class RfiEvent extends Event implements Serializable {

	private static final long serialVersionUID = -7370899245667164560L;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfiEventSupplier> suppliers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfiEventDocument> documents;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfiEventMeeting> meetings;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfiEventContact> eventContacts;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfiEventCorrespondenceAddress> eventCorrespondenceAddress;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfiEnvelop> rfiEnvelop;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfiCq> cqs;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfiEvent", orphanRemoval = false)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@OrderBy("reminderDate")
	private List<RfiReminder> rfiReminder;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFI_EVENT_VIEWERS", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private List<User> eventViewers;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFI_EVENT_EDITORS", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private List<User> eventEditors;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfiTeamMember> teamMembers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RfiEventApproval> approvals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfiUnMaskedUser> unMaskedUsers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfiEvaluationConclusionUser> evaluationConclusionUsers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RfiComment> comment;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFI_EVENT_INDUS_CAT", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "IND_CAT_ID"))
	private List<IndustryCategory> industryCategories;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EXPECTED_TENDER_START_DATE", nullable = true)
	private Date expectedTenderStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EXPECTED_TENDER_END_DATE", nullable = true)
	private Date expectedTenderEndDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FEE_START_DATE", nullable = true)
	private Date feeStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FEE_END_DATE", nullable = true)
	private Date feeEndDate;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RfiEventSuspensionApproval> suspensionApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RfiSuspensionComment> suspensionComment;


	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfiEventSor> eventSors;

	@Column(name = "ADD_SCHEDULE_OF_RATE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean addScheduleOfRate = Boolean.TRUE;

	@Transient
	private String expectedTenderDateTimeRange;

	@Transient
	private String feeDateTimeRange;

	@Transient
	private List<RfiSupplierSor> rfiSupplierSors;

	public RfiEvent(String id, Date eventPublishDate, String eventName, Date eventEnd, String referanceNumber) {
		super(id, eventPublishDate, eventName, eventEnd, referanceNumber);
	}

	public String getExpectedTenderDateTimeRange() {
		return expectedTenderDateTimeRange;
	}

	public void setExpectedTenderDateTimeRange(String expectedTenderDateTimeRange) {
		this.expectedTenderDateTimeRange = expectedTenderDateTimeRange;
	}

	public String getFeeDateTimeRange() {
		return feeDateTimeRange;
	}

	public void setFeeDateTimeRange(String feeDateTimeRange) {
		this.feeDateTimeRange = feeDateTimeRange;
	}

	public Date getExpectedTenderStartDate() {
		return expectedTenderStartDate;
	}

	public void setExpectedTenderStartDate(Date expectedTenderStartDate) {
		this.expectedTenderStartDate = expectedTenderStartDate;
	}

	public Date getExpectedTenderEndDate() {
		return expectedTenderEndDate;
	}

	public void setExpectedTenderEndDate(Date expectedTenderEndDate) {
		this.expectedTenderEndDate = expectedTenderEndDate;
	}

	public Date getFeeStartDate() {
		return feeStartDate;
	}

	public void setFeeStartDate(Date feeStartDate) {
		this.feeStartDate = feeStartDate;
	}

	public Date getFeeEndDate() {
		return feeEndDate;
	}

	public void setFeeEndDate(Date feeEndDate) {
		this.feeEndDate = feeEndDate;
	}

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean uploadDocuments;

	public boolean isUploadDocuments() {
		return uploadDocuments;
	}

	public void setUploadDocuments(boolean uploadDocuments) {
		this.uploadDocuments = uploadDocuments;
	}

	/**
	 * @return the comment
	 */
	public List<RfiComment> getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(List<RfiComment> comment) {
		this.comment = comment;
	}

	public RfiEvent copyFrom(RfiEvent oldEvent) {
		RfiEvent newEvent = new RfiEvent();
		copyEventDetails(oldEvent, newEvent);
		// Team
		copyTeamMemberDetails(oldEvent, newEvent);
		// Approval
		copyApprovalDetails(oldEvent, newEvent);
		newEvent.setEnableApprovalReminder(oldEvent.getEnableApprovalReminder());
		newEvent.setReminderAfterHour(oldEvent.getReminderAfterHour());
		newEvent.setReminderCount(oldEvent.getReminderCount());
		// SOR
		copySorDetails(oldEvent, newEvent, null, false);
		// CQ
		copyCqDetails(oldEvent, newEvent);
		// Sup
		copySupplierDetails(oldEvent, newEvent, null);
		// Env
		copyEnvelopes(oldEvent, newEvent);
		// ..

		copyUnmaskingDetails(oldEvent, newEvent);

		// Evaluation Conclusion
		copyEvaluationConclusionUsers(oldEvent, newEvent);

		// ..
		copyDocument(oldEvent, newEvent);
		return newEvent;

	}

	private void copyDocument(RfiEvent oldEvent, RfiEvent newEvent){
		if(CollectionUtil.isNotEmpty(oldEvent.getDocuments())){
			newEvent.setDocuments(new ArrayList<RfiEventDocument>());
			for(RfiEventDocument doc: oldEvent.getDocuments()){
				newEvent.getDocuments().add(doc.copyFrom(oldEvent));
			}
		}
	}

	private void copySorDetails(RfiEvent oldEvent, RfiEvent newEvent, List<RfiSupplierSor> rfqSupplierSorList, boolean isSupplierInvited) {

		if (CollectionUtil.isNotEmpty(oldEvent.getEventSors())) {
			newEvent.setEventSors(new ArrayList<RfiEventSor>());
			for (RfiEventSor bq : oldEvent.getEventSors()) {
				RfiEventSor newBq = bq.copyForRfq(isSupplierInvited);
				newEvent.getEventSors().add(newBq);

				if (isSupplierInvited) {

					newEvent.setRfiSupplierSors(new ArrayList<RfiSupplierSor>());
					for (RfiSupplierSor supBq : rfqSupplierSorList) {
						newEvent.getRfiSupplierSors().add(supBq.createForRfi(newEvent, newBq));
					}
					newBq.setId(null);
					for (RfiSorItem bqItem : newBq.getSorItems()) {
						bqItem.setId(null);
					}
				}

			}
		}
	}

	private void copyUnmaskingDetails(RfiEvent oldEvent, RfiEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getUnMaskedUsers())) {
			newEvent.setUnMaskedUsers((new ArrayList<RfiUnMaskedUser>()));
			for (RfiUnMaskedUser app : oldEvent.getUnMaskedUsers()) {
				RfiUnMaskedUser usr = app.copyFrom();
				usr.setEvent(newEvent);
				newEvent.getUnMaskedUsers().add(usr);
			}
		}
	}

	private void copyEvaluationConclusionUsers(RfiEvent oldEvent, RfiEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getEvaluationConclusionUsers())) {
			newEvent.setEvaluationConclusionUsers(new ArrayList<RfiEvaluationConclusionUser>());
			for (RfiEvaluationConclusionUser app : oldEvent.getEvaluationConclusionUsers()) {
				RfiEvaluationConclusionUser usr = app.copyFrom();
				usr.setEvent(newEvent);
				newEvent.getEvaluationConclusionUsers().add(usr);
			}
		}
	}

	public RfiEvent() {
		super();
	}

	public RfiEvent(String eventId, BigDecimal budgetAmount, Date eventStart, Date eventEnd, Currency baseCurrency) {
		this.setEventId(eventId);
		this.setBudgetAmount(budgetAmount);
		this.setEventStart(eventStart);
		this.setEventEnd(eventEnd);
		this.setBaseCurrency(baseCurrency);

	}

	public RfiEvent(String eventId) {
		this.setId(eventId);
	}

	public RfiEvent(String eventId, String eventName) {
		this.setId(eventId);
		this.setEventName(eventName);
	}

	public RfiEvent(String id, String eventId, String eventName, String referenceNumber, BigDecimal fee, Currency feeCurrency, User user) {
		setId(id);
		setEventName(eventName);
		setReferanceNumber(referenceNumber);
		setEventId(eventId);
		setParticipationFees(fee);
		setParticipationFeeCurrency(feeCurrency);
		setCreatedBy(user);
		if (feeCurrency != null) {
			feeCurrency.getCurrencyCode();
		}
	}

	private RfiEvent copyEventDetails(RfiEvent copyEvent, RfiEvent newEvent) {
		newEvent.setBillOfQuantity(Boolean.FALSE);
		newEvent.setScheduleOfRate(copyEvent.getScheduleOfRate());
		newEvent.setAllowToSuspendEvent(copyEvent.getAllowToSuspendEvent());
		newEvent.setExpectedTenderStartDate(copyEvent.getExpectedTenderStartDate());
		newEvent.setExpectedTenderEndDate(copyEvent.getExpectedTenderEndDate());
		newEvent.setFeeStartDate(copyEvent.getFeeStartDate());
		newEvent.setFeeEndDate(copyEvent.getFeeEndDate());
		newEvent.setViewSupplerName(copyEvent.getViewSupplerName());
		newEvent.setUnMaskedUser(copyEvent.getUnMaskedUser());
		newEvent.setEnableEvaluationConclusionUsers(copyEvent.getEnableEvaluationConclusionUsers() == null ? Boolean.FALSE : copyEvent.getEnableEvaluationConclusionUsers());
		newEvent.setMeetingReq(copyEvent.getMeetingReq());
		newEvent.setQuestionnaires(copyEvent.getQuestionnaires());
		newEvent.setDocumentReq(copyEvent.getDocumentReq());
		newEvent.setReferanceNumber(copyEvent.getReferanceNumber());
		newEvent.setEventName(copyEvent.getEventName());
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setTemplate(copyEvent.getTemplate());
		newEvent.setBaseCurrency(copyEvent.getBaseCurrency());
		newEvent.setBudgetAmount(copyEvent.getBudgetAmount());
		newEvent.setCostCenter(copyEvent.getCostCenter());
		newEvent.setBusinessUnit(copyEvent.getBusinessUnit());
		newEvent.setDecimal(copyEvent.getDecimal());
		newEvent.setDocumentReq(copyEvent.getDocumentReq());
		newEvent.setCloseEnvelope(copyEvent.getCloseEnvelope());
		newEvent.setAddSupplier(copyEvent.getAddSupplier());
		newEvent.setUrgentEvent(copyEvent.getUrgentEvent());
		newEvent.setRfxEnvelopeReadOnly(copyEvent.getRfxEnvelopeReadOnly());
		newEvent.setRfxEnvelopeOpening(copyEvent.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(copyEvent.getRfxEnvOpeningAfter());
		newEvent.setMaximumSupplierRating(copyEvent.getMaximumSupplierRating());
		newEvent.setMinimumSupplierRating(copyEvent.getMinimumSupplierRating());
		newEvent.setEnableSuspensionApproval(copyEvent.getEnableSuspensionApproval());
		newEvent.setGroupCode(copyEvent.getGroupCode());
		newEvent.setAllowDisqualifiedSupplierDownload(copyEvent.getAllowDisqualifiedSupplierDownload());

		if (CollectionUtil.isNotEmpty(copyEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfiEventContact>());
			for (RfiEventContact contact : copyEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyFrom(newEvent));
			}
		}
		if (CollectionUtil.isNotEmpty(copyEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfiEventCorrespondenceAddress>());
			for (RfiEventCorrespondenceAddress correspondenceAddress : copyEvent.getEventCorrespondenceAddress()) {
				newEvent.getEventCorrespondenceAddress().add(correspondenceAddress.copyFrom(newEvent));
			}
		}
		newEvent.setEventDescription(copyEvent.getEventDescription());
		newEvent.setInternalRemarks(copyEvent.getInternalRemarks());
		newEvent.setEventOwner(copyEvent.getEventOwner());
		newEvent.setEventVisibility(copyEvent.getEventVisibility());
		newEvent.setEventVisibilityDates(copyEvent.getEventVisibilityDates());
		newEvent.setHistoricaAmount(copyEvent.getHistoricaAmount());
		newEvent.setEstimatedBudget(copyEvent.getEstimatedBudget());
		newEvent.setProcurementMethod(copyEvent.getProcurementMethod());
		newEvent.setProcurementCategories(copyEvent.getProcurementCategories());
		// newEvent.setIndustryCategory(copyEvent.getIndustryCategory());
		if (copyEvent.getIndustryCategories() == null) {
			copyEvent.setIndustryCategories(new ArrayList<>());
		}
		copyEvent.getIndustryCategories().add(copyEvent.getIndustryCategory());

		newEvent.setIndustryCategories(copyEvent.getIndustryCategories());

		newEvent.setMeetingReq(copyEvent.getMeetingReq());
		newEvent.setParticipationFeeCurrency(copyEvent.getParticipationFeeCurrency());
		newEvent.setParticipationFees(copyEvent.getParticipationFees());
		newEvent.setDepositCurrency(copyEvent.getDepositCurrency());
		newEvent.setDeposit(copyEvent.getDeposit());
		newEvent.setPaymentTerm(copyEvent.getPaymentTerm());
		newEvent.setQuestionnaires(copyEvent.getQuestionnaires());
		newEvent.setSubmissionValidityDays(copyEvent.getSubmissionValidityDays());
		newEvent.setTenantId(copyEvent.getTenantId());

		newEvent.setExpectedTenderStartDate(copyEvent.getExpectedTenderStartDate());
		newEvent.setExpectedTenderEndDate(copyEvent.getExpectedTenderEndDate());
		newEvent.setFeeStartDate(copyEvent.getFeeStartDate());
		newEvent.setFeeEndDate(copyEvent.getFeeEndDate());
		newEvent.setEnableApprovalReminder(copyEvent.getEnableApprovalReminder());
		newEvent.setReminderAfterHour(copyEvent.getReminderAfterHour());
		newEvent.setReminderCount(copyEvent.getReminderCount());
		newEvent.setEnableEvaluationDeclaration(copyEvent.getEnableEvaluationDeclaration());
		if (copyEvent.getEvaluationProcessDeclaration() != null) {
			newEvent.setEvaluationProcessDeclaration(copyEvent.getEvaluationProcessDeclaration());
		}
		newEvent.setEnableSupplierDeclaration(copyEvent.getEnableSupplierDeclaration());
		if (copyEvent.getSupplierAcceptanceDeclaration() != null) {
			newEvent.setSupplierAcceptanceDeclaration(copyEvent.getSupplierAcceptanceDeclaration());
		}
		newEvent.setNotifyEventOwner(copyEvent.getNotifyEventOwner());

		newEvent.setProcurementCategories(copyEvent.getProcurementCategories());
		newEvent.setProcurementMethod(copyEvent.getProcurementMethod());

		return newEvent;
	}

	private void copyEnvelopes(RfiEvent copyEvent, RfiEvent event) {
		if (CollectionUtil.isNotEmpty(copyEvent.getRfiEnvelop())) {
			event.setRfiEnvelop(new ArrayList<RfiEnvelop>());
			for (RfiEnvelop envelop : copyEvent.getRfiEnvelop()) {
				event.getRfiEnvelop().add(envelop.copyFrom(event));
			}
		}
	}

	private void copySupplierDetails(RfiEvent oldEvent, RfiEvent newEvent, String[] invitedSupp) {
		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {

			boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
			List<String> invitedSuppliersList = null;
			if (invitedSuppliersOnly) {
				invitedSuppliersList = Arrays.asList(invitedSupp);
			}

			newEvent.setSuppliers(new ArrayList<RfiEventSupplier>());
			for (RfiEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfi());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRfi());
				}
			}
		}
	}

	private void copyCqDetails(RfiEvent oldEvent, RfiEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getCqs())) {
			newEvent.setCqs(new ArrayList<RfiCq>());
			for (RfiCq cq : oldEvent.getCqs()) {
				List<RfiCqItem> cqList = cq.getCqItems();
				for (RfiCqItem rfiCqItem : cqList) {
					if (CqType.DOCUMENT_DOWNLOAD_LINK == rfiCqItem.getCqType()) {
						newEvent.setUploadDocuments(true);
					}
				}
				newEvent.getCqs().add(cq.copyFrom());
			}
		}
	}

	private void copyTeamMemberDetails(RfiEvent oldEvent, RfiEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getTeamMembers())) {
			newEvent.setTeamMembers(new ArrayList<RfiTeamMember>());
			for (RfiTeamMember tm : oldEvent.getTeamMembers()) {
				newEvent.getTeamMembers().add(tm.copyFrom());
			}
		}
	}

	private void copyApprovalDetails(RfiEvent oldEvent, RfiEvent newEvent) {

		if (CollectionUtil.isNotEmpty(oldEvent.getApprovals())) {
			newEvent.setApprovals(new ArrayList<RfiEventApproval>());
			for (RfiEventApproval app : oldEvent.getApprovals()) {
				newEvent.getApprovals().add(app.copyFrom());
			}
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuspensionApprovals())) {
			newEvent.setSuspensionApprovals(new ArrayList<RfiEventSuspensionApproval>());
			for (RfiEventSuspensionApproval app : oldEvent.getSuspensionApprovals()) {
				newEvent.getSuspensionApprovals().add(app.copyFrom());
			}
		}
	}

	public RfiEvent createNextRfiEvent(RfiEvent oldEvent, User loggedInUser, String[] invitedSupp) {
		RfiEvent newEvent = new RfiEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setPreviousEventType(RfxTypes.RFI);
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());

		// Copy Event Details
		copyEventDetails(oldEvent, newEvent);
		// copy supp
		copySupplierDetails(oldEvent, newEvent, invitedSupp);

		return newEvent;
	}

	public RfpEvent createNextRfpEvent(RfiEvent oldEvent, User loggedInUser, String[] invitedSupp) {
		RfpEvent newEvent = new RfpEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFI);
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setCreatedDate(new Date());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfpEventContact>());
			for (RfiEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfp());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfpEventCorrespondenceAddress>());
			for (RfiEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
				newEvent.getEventCorrespondenceAddress().add(correspondenceAddress.copyForRfp());
			}
		}
		newEvent.setEventDescription(oldEvent.getEventDescription());
		newEvent.setInternalRemarks(oldEvent.getInternalRemarks());
		newEvent.setEventOwner(oldEvent.getEventOwner());
		newEvent.setEventVisibility(oldEvent.getEventVisibility());
		newEvent.setEventVisibilityDates(oldEvent.getEventVisibilityDates());
		newEvent.setHistoricaAmount(oldEvent.getHistoricaAmount());
		// newEvent.setIndustryCategory(oldEvent.getIndustryCategory());
		if (oldEvent.getIndustryCategories() == null) {
			oldEvent.setIndustryCategories(new ArrayList<>());
		}
		oldEvent.getIndustryCategories().add(oldEvent.getIndustryCategory());

		newEvent.setIndustryCategories(oldEvent.getIndustryCategories());

		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setParticipationFeeCurrency(oldEvent.getParticipationFeeCurrency());
		newEvent.setParticipationFees(oldEvent.getParticipationFees());
		newEvent.setDepositCurrency(oldEvent.getDepositCurrency());
		newEvent.setDeposit(oldEvent.getDeposit());
		newEvent.setPaymentTerm(oldEvent.getPaymentTerm());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setSubmissionValidityDays(oldEvent.getSubmissionValidityDays());
		newEvent.setTenantId(oldEvent.getTenantId());
		newEvent.setEnableApprovalReminder(oldEvent.getEnableApprovalReminder());
		newEvent.setReminderAfterHour(oldEvent.getReminderAfterHour());
		newEvent.setReminderCount(oldEvent.getReminderCount());
		newEvent.setNotifyEventOwner(oldEvent.getNotifyEventOwner());
		newEvent.setDisableTotalAmount(Boolean.FALSE);
		newEvent.setProcurementCategories(oldEvent.getProcurementCategories());
		newEvent.setProcurementMethod(oldEvent.getProcurementMethod());
		newEvent.setGroupCode(oldEvent.getGroupCode());

		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RfpEventSupplier>());
			for (RfiEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfp());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRfp());
				}
			}
		}
		return newEvent;
	}

	public RfqEvent createNextRfqEvent(RfiEvent oldEvent, User loggedInUser, String[] invitedSupp) {
		RfqEvent newEvent = new RfqEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFI);
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setCreatedDate(new Date());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfqEventContact>());
			for (RfiEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfq());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfqEventCorrespondenceAddress>());
			for (RfiEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
				newEvent.getEventCorrespondenceAddress().add(correspondenceAddress.copyForRfq());
			}
		}
		newEvent.setEventDescription(oldEvent.getEventDescription());
		newEvent.setInternalRemarks(oldEvent.getInternalRemarks());
		newEvent.setEventOwner(oldEvent.getEventOwner());
		newEvent.setEventVisibility(oldEvent.getEventVisibility());
		newEvent.setEventVisibilityDates(oldEvent.getEventVisibilityDates());
		newEvent.setHistoricaAmount(oldEvent.getHistoricaAmount());
		// newEvent.setIndustryCategory(oldEvent.getIndustryCategory());

		if (oldEvent.getIndustryCategories() == null) {
			oldEvent.setIndustryCategories(new ArrayList<>());
		}
		oldEvent.getIndustryCategories().add(oldEvent.getIndustryCategory());

		newEvent.setIndustryCategories(oldEvent.getIndustryCategories());

		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setParticipationFeeCurrency(oldEvent.getParticipationFeeCurrency());
		newEvent.setParticipationFees(oldEvent.getParticipationFees());
		newEvent.setDepositCurrency(oldEvent.getDepositCurrency());
		newEvent.setDeposit(oldEvent.getDeposit());
		newEvent.setPaymentTerm(oldEvent.getPaymentTerm());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setSubmissionValidityDays(oldEvent.getSubmissionValidityDays());
		newEvent.setTenantId(oldEvent.getTenantId());
		newEvent.setEnableApprovalReminder(oldEvent.getEnableApprovalReminder());
		newEvent.setReminderAfterHour(oldEvent.getReminderAfterHour());
		newEvent.setReminderCount(oldEvent.getReminderCount());
		newEvent.setNotifyEventOwner(oldEvent.getNotifyEventOwner());
		newEvent.setDisableTotalAmount(Boolean.FALSE);
		newEvent.setProcurementCategories(oldEvent.getProcurementCategories());
		newEvent.setProcurementMethod(oldEvent.getProcurementMethod());
		newEvent.setGroupCode(oldEvent.getGroupCode());

		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RfqEventSupplier>());
			for (RfiEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfq());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRfq());
				}
			}
		}
		return newEvent;
	}

	public RftEvent createNextRftEvent(RfiEvent oldEvent, User loggedInUser, String[] invitedSupp) {
		RftEvent newEvent = new RftEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFI);
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setCreatedDate(new Date());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RftEventContact>());
			for (RfiEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRft());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RftEventCorrespondenceAddress>());
			for (RfiEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
				newEvent.getEventCorrespondenceAddress().add(correspondenceAddress.copyForRft());
			}
		}
		newEvent.setEventDescription(oldEvent.getEventDescription());
		newEvent.setInternalRemarks(oldEvent.getInternalRemarks());
		newEvent.setEventOwner(oldEvent.getEventOwner());
		newEvent.setEventVisibility(oldEvent.getEventVisibility());
		newEvent.setEventVisibilityDates(oldEvent.getEventVisibilityDates());
		newEvent.setHistoricaAmount(oldEvent.getHistoricaAmount());
		// newEvent.setIndustryCategory(oldEvent.getIndustryCategory());

		if (oldEvent.getIndustryCategories() == null) {
			oldEvent.setIndustryCategories(new ArrayList<>());
		}
		oldEvent.getIndustryCategories().add(oldEvent.getIndustryCategory());

		newEvent.setIndustryCategories(oldEvent.getIndustryCategories());

		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setParticipationFeeCurrency(oldEvent.getParticipationFeeCurrency());
		newEvent.setParticipationFees(oldEvent.getParticipationFees());
		newEvent.setDepositCurrency(oldEvent.getDepositCurrency());
		newEvent.setDeposit(oldEvent.getDeposit());
		newEvent.setPaymentTerm(oldEvent.getPaymentTerm());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setSubmissionValidityDays(oldEvent.getSubmissionValidityDays());
		newEvent.setTenantId(oldEvent.getTenantId());
		newEvent.setEnableApprovalReminder(oldEvent.getEnableApprovalReminder());
		newEvent.setReminderAfterHour(oldEvent.getReminderAfterHour());
		newEvent.setReminderCount(oldEvent.getReminderCount());
		newEvent.setNotifyEventOwner(oldEvent.getNotifyEventOwner());
		newEvent.setDisableTotalAmount(Boolean.FALSE);
		newEvent.setProcurementCategories(oldEvent.getProcurementCategories());
		newEvent.setProcurementMethod(oldEvent.getProcurementMethod());
		newEvent.setGroupCode(oldEvent.getGroupCode());

		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RftEventSupplier>());
			for (RfiEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRft());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRft());
				}
			}
		}
		return newEvent;
	}

	public RfaEvent createNextRfaEvent(RfiEvent oldEvent, AuctionType auctionType, User loggedInUser, String[] invitedSupp) {
		RfaEvent newEvent = new RfaEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFI);
		newEvent.setAuctionType(auctionType);
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setCreatedDate(new Date());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfaEventContact>());
			for (RfiEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfa());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfaEventCorrespondenceAddress>());
			for (RfiEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
				newEvent.getEventCorrespondenceAddress().add(correspondenceAddress.copyForRfa());
			}
		}
		newEvent.setEventDescription(oldEvent.getEventDescription());
		newEvent.setInternalRemarks(oldEvent.getInternalRemarks());
		newEvent.setEventOwner(oldEvent.getEventOwner());
		newEvent.setEventVisibility(oldEvent.getEventVisibility());
		newEvent.setEventVisibilityDates(oldEvent.getEventVisibilityDates());
		newEvent.setHistoricaAmount(oldEvent.getHistoricaAmount());
		// newEvent.setIndustryCategory(oldEvent.getIndustryCategory());
		if (oldEvent.getIndustryCategories() == null) {
			oldEvent.setIndustryCategories(new ArrayList<>());
		}
		oldEvent.getIndustryCategories().add(oldEvent.getIndustryCategory());

		newEvent.setIndustryCategories(oldEvent.getIndustryCategories());

		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setParticipationFeeCurrency(oldEvent.getParticipationFeeCurrency());
		newEvent.setParticipationFees(oldEvent.getParticipationFees());
		newEvent.setDepositCurrency(oldEvent.getDepositCurrency());
		newEvent.setDeposit(oldEvent.getDeposit());
		newEvent.setPaymentTerm(oldEvent.getPaymentTerm());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setSubmissionValidityDays(oldEvent.getSubmissionValidityDays());
		newEvent.setTenantId(oldEvent.getTenantId());
		newEvent.setEnableApprovalReminder(oldEvent.getEnableApprovalReminder());
		newEvent.setReminderAfterHour(oldEvent.getReminderAfterHour());
		newEvent.setReminderCount(oldEvent.getReminderCount());
		newEvent.setNotifyEventOwner(oldEvent.getNotifyEventOwner());
		newEvent.setDisableTotalAmount(Boolean.FALSE);
		newEvent.setProcurementCategories(oldEvent.getProcurementCategories());
		newEvent.setProcurementMethod(oldEvent.getProcurementMethod());
		newEvent.setGroupCode(oldEvent.getGroupCode());

		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RfaEventSupplier>());
			for (RfiEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfa());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRfa());
				}
			}
		}
		return newEvent;
	}

	/**
	 * @return the approvals
	 */
	public List<RfiEventApproval> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<RfiEventApproval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<RfiEventApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (approvals != null) {
				for (RfiEventApproval oldApproval : this.approvals) {
					for (RfiEventApproval newApproval : approvals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RfiApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RfiApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
									if (newApprovalUser.getUser() == null || newApprovalUser.getUser().getId() == null) {
										continue;
									}
									if (newApprovalUser.getUser().getId().equals(oldApprovalUser.getUser().getId())) {
										newApprovalUser.setActionDate(oldApprovalUser.getActionDate());
										newApprovalUser.setApprovalStatus(oldApprovalUser.getApprovalStatus());
										newApprovalUser.setRemarks(oldApprovalUser.getRemarks());
									}
								}
							}
						}
					}
				}
			}
			this.approvals.clear();
		}
		if (approvals != null) {
			this.approvals.addAll(approvals);
		}
	}

	/**
	 * @return the suppliers
	 */
	public List<RfiEventSupplier> getSuppliers() {
		return suppliers;
	}

	/**
	 * @param suppliers the suppliers to set
	 */
	public void setSuppliers(List<RfiEventSupplier> suppliers) {
		this.suppliers = suppliers;
	}

	/**
	 * @return the documents
	 */
	public List<RfiEventDocument> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<RfiEventDocument> documents) {
		this.documents = documents;
	}

	/**
	 * @return the eventContacts
	 */
	public List<RfiEventContact> getEventContacts() {
		return eventContacts;
	}

	/**
	 * @param eventContacts the eventContacts to set
	 */
	public void setEventContacts(List<RfiEventContact> eventContacts) {
		this.eventContacts = eventContacts;
	}

	/**
	 * @return the eventCorrespondenceAddress
	 */
	public List<RfiEventCorrespondenceAddress> getEventCorrespondenceAddress() {
		return eventCorrespondenceAddress;
	}

	/**
	 * @param eventCorrespondenceAddress the eventCorrespondenceAddress to set
	 */
	public void setEventCorrespondenceAddress(List<RfiEventCorrespondenceAddress> eventCorrespondenceAddress) {
		this.eventCorrespondenceAddress = eventCorrespondenceAddress;
	}

	/**
	 * @return the meetings
	 */
	public List<RfiEventMeeting> getMeetings() {
		return meetings;
	}

	/**
	 * @param meetings the meetings to set
	 */
	public void setMeetings(List<RfiEventMeeting> meetings) {
		this.meetings = meetings;
	}

	/**
	 * @return the rfiEnvelop
	 */
	public List<RfiEnvelop> getRfiEnvelop() {
		return rfiEnvelop;
	}

	/**
	 * @param rfiEnvelop the rfiEnvelop to set
	 */
	public void setRfiEnvelop(List<RfiEnvelop> rfiEnvelop) {
		this.rfiEnvelop = rfiEnvelop;
	}

	/**
	 * @return the cqs
	 */
	public List<RfiCq> getCqs() {
		return cqs;
	}

	/**
	 * @param cqs the cqs to set
	 */
	public void setCqs(List<RfiCq> cqs) {
		this.cqs = cqs;
	}

	/**
	 * @return the rfiReminder
	 */
	public List<RfiReminder> getRfiReminder() {
		return rfiReminder;
	}

	/**
	 * @param rfiReminder the rfiReminder to set
	 */
	public void setRfiReminder(List<RfiReminder> rfiReminder) {
		this.rfiReminder = rfiReminder;
	}

	/**
	 * @return the eventViewers
	 */
	public List<User> getEventViewers() {
		return eventViewers;
	}

	/**
	 * @param eventViewers the eventViewers to set
	 */
	public void setEventViewers(List<User> eventViewers) {
		this.eventViewers = eventViewers;
	}

	/**
	 * @return the eventEditors
	 */
	public List<User> getEventEditors() {
		return eventEditors;
	}

	/**
	 * @param eventEditors the eventEditors to set
	 */
	public void setEventEditors(List<User> eventEditors) {
		this.eventEditors = eventEditors;
	}

	/**
	 * @return the teamMembers
	 */
	public List<RfiTeamMember> getTeamMembers() {
		return teamMembers;
	}

	/**
	 * @param teamMembers the teamMembers to set
	 */
	public void setTeamMembers(List<RfiTeamMember> teamMembers) {
		this.teamMembers = teamMembers;
	}

	/**
	 * @return the industryCategories
	 */
	public List<IndustryCategory> getIndustryCategories() {
		return industryCategories;
	}

	/**
	 * @param industryCategories the industryCategories to set
	 */
	public void setIndustryCategories(List<IndustryCategory> industryCategories) {
		this.industryCategories = industryCategories;
	}

	/**
	 * @return the unMaskedUsers
	 */
	public List<RfiUnMaskedUser> getUnMaskedUsers() {
		return unMaskedUsers;
	}

	/**
	 * @param unMaskedUsers the unMaskedUsers to set
	 */
	public void setUnMaskedUsers(List<RfiUnMaskedUser> unMaskedUsers) {
		if (this.unMaskedUsers != null) {
			if (this.unMaskedUsers != unMaskedUsers) {
				this.unMaskedUsers.clear();
				if (CollectionUtil.isNotEmpty(unMaskedUsers)) {
					this.unMaskedUsers.addAll(unMaskedUsers);
				}
			}
		} else {
			this.unMaskedUsers = unMaskedUsers;
		}
	}

	/**
	 * @return the evaluationConclusionUsers
	 */
	public List<RfiEvaluationConclusionUser> getEvaluationConclusionUsers() {
		return evaluationConclusionUsers;
	}

	/**
	 * @param evaluationConclusionUsers the evaluationConclusionUsers to set
	 */
	public void setEvaluationConclusionUsers(List<RfiEvaluationConclusionUser> evaluationConclusionUsers) {
		if (this.evaluationConclusionUsers != null) {
			if (this.evaluationConclusionUsers != evaluationConclusionUsers) {
				this.evaluationConclusionUsers.clear();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUsers)) {
					this.evaluationConclusionUsers.addAll(evaluationConclusionUsers);
				}
			}
		} else {
			this.evaluationConclusionUsers = evaluationConclusionUsers;
		}
	}

	/**
	 * @return the suspensionApprovals
	 */
	public List<RfiEventSuspensionApproval> getSuspensionApprovals() {
		return suspensionApprovals;
	}

	/**
	 * @param suspensionApprovals the suspensionApprovals to set
	 */
	public void setSuspensionApprovals(List<RfiEventSuspensionApproval> suspensionApprovals) {

		if (this.suspensionApprovals == null) {
			this.suspensionApprovals = new ArrayList<RfiEventSuspensionApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (suspensionApprovals != null) {
				for (RfiEventSuspensionApproval oldApproval : this.suspensionApprovals) {
					for (RfiEventSuspensionApproval newApproval : suspensionApprovals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RfiSuspensionApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RfiSuspensionApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
									if (newApprovalUser.getUser() == null || newApprovalUser.getUser().getId() == null) {
										continue;
									}
									if (newApprovalUser.getUser().getId().equals(oldApprovalUser.getUser().getId())) {
										newApprovalUser.setActionDate(oldApprovalUser.getActionDate());
										newApprovalUser.setApprovalStatus(oldApprovalUser.getApprovalStatus());
										newApprovalUser.setRemarks(oldApprovalUser.getRemarks());
									}
								}
							}
						}
					}
				}
			}
			this.suspensionApprovals.clear();
		}
		if (suspensionApprovals != null) {
			this.suspensionApprovals.addAll(suspensionApprovals);
		}
	}

	/**
	 * @return the suspensionComment
	 */
	public List<RfiSuspensionComment> getSuspensionComment() {
		return suspensionComment;
	}

	/**
	 * @param suspensionComment the suspensionComment to set
	 */
	public void setSuspensionComment(List<RfiSuspensionComment> suspensionComment) {
		this.suspensionComment = suspensionComment;
	}


	public List<RfiEventSor> getEventSors() {
		return eventSors;
	}

	public void setEventSors(List<RfiEventSor> eventSors) {
		this.eventSors = eventSors;
	}

	public Boolean getAddScheduleOfRate() {
		return addScheduleOfRate;
	}

	public void setAddScheduleOfRate(Boolean addScheduleOfRate) {
		this.addScheduleOfRate = addScheduleOfRate;
	}

	public List<RfiSupplierSor> getRfiSupplierSors() {
		return rfiSupplierSors;
	}

	public void setRfiSupplierSors(List<RfiSupplierSor> rfiSupplierSors) {
		this.rfiSupplierSors = rfiSupplierSors;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfiEvent [toLogString()=" + toLogString() + "]";
	}

}
