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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.jfree.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AwardStatus;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.OngoingEventPojo;
import com.privasia.procurehere.core.pojo.PublicEventPojo;
import com.privasia.procurehere.core.pojo.SourcingFormRequestPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */

@Entity
@Table(name = "PROC_RFT_EVENTS", indexes = { @Index(columnList = "TENANT_ID", name = "IDX_RFT_TENANT_ID") })
@SqlResultSetMappings({ @SqlResultSetMapping(name = "ongoingEventResult", classes = { @ConstructorResult(targetClass = OngoingEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventName"), @ColumnResult(name = "createdBy"), @ColumnResult(name = "createdDate"), @ColumnResult(name = "modifiedDate"), @ColumnResult(name = "type"), @ColumnResult(name = "eventStart"), @ColumnResult(name = "eventEnd"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "unitName"), @ColumnResult(name = "sysEventId"), @ColumnResult(name = "eventUser") }) }), @SqlResultSetMapping(name = "publicEventsResult", classes = { @ConstructorResult(targetClass = PublicEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventName"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "eventStart"), @ColumnResult(name = "eventEnd"), @ColumnResult(name = "unitName"), @ColumnResult(name = "name"), @ColumnResult(name = "code"), @ColumnResult(name = "siteVisitDate"), @ColumnResult(name = "contactName"), @ColumnResult(name = "meetingType"), @ColumnResult(name = "type"), @ColumnResult(name = "contactNumber"), @ColumnResult(name = "buyerId"), @ColumnResult(name = "participationFees"), @ColumnResult(name = "currencyCode") }) }), @SqlResultSetMapping(name = "auctionExcelReportData", classes = { @ConstructorResult(targetClass = DraftEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventName"), @ColumnResult(name = "decimalValue"), @ColumnResult(name = "currencyName"), @ColumnResult(name = "leadingSuppierBid"), @ColumnResult(name = "leadingSuppier"), @ColumnResult(name = "awardedSupplier"), @ColumnResult(name = "referanceNumber"), @ColumnResult(name = "sysEventId"), @ColumnResult(name = "auctiontype"), @ColumnResult(name = "ownerName"), @ColumnResult(name = "eventStart"), @ColumnResult(name = "eventEnd"), @ColumnResult(name = "eventVisibility"), @ColumnResult(name = "unitName"), @ColumnResult(name = "templateName"), @ColumnResult(name = "winningSupplier"), @ColumnResult(name = "selfInvitedWinner"), @ColumnResult(name = "leadingAmount"), @ColumnResult(name = "invitedSupplierCount"), @ColumnResult(name = "participatedSupplierCount"), @ColumnResult(name = "selfInvitedSupplierCount"), @ColumnResult(name = "submittedSupplierCount"), @ColumnResult(name = "eventCategories"), @ColumnResult(name = "budgetAmount"), @ColumnResult(name = "historicAmount"), @ColumnResult(name = "sumAwardedPrice"), @ColumnResult(name = "supplierTags"), @ColumnResult(name = "noOfBids"), @ColumnResult(name = "ratio") }) }), @SqlResultSetMapping(name = "sourcingReportExcelData", classes = { @ConstructorResult(targetClass = SourcingFormRequestPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "formId"), @ColumnResult(name = "sourcingFormName"), @ColumnResult(name = "referanceNumber"), @ColumnResult(name = "createdBy"), @ColumnResult(name = "createdDate"), @ColumnResult(name = "formOwner"), @ColumnResult(name = "businessUnit"), @ColumnResult(name = "costCenter"), @ColumnResult(name = "baseCurrency"), @ColumnResult(name = "status"), @ColumnResult(name = "groupCode"), @ColumnResult(name = "templateName"), @ColumnResult(name = "description"), @ColumnResult(name = "availableBudget"), @ColumnResult(name = "estimatedBudget"), @ColumnResult(name = "submittedDate"), @ColumnResult(name = "approvedDate"), @ColumnResult(name = "approvalDaysHours"), @ColumnResult(name = "approvalTotalLevels"), @ColumnResult(name = "approvalTotalUsers") }) }), @SqlResultSetMapping(name = "sourcingReportCsvData", classes = { @ConstructorResult(targetClass = SourcingFormRequestPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "createdDate"), @ColumnResult(name = "formId"), @ColumnResult(name = "sourcingFormName"), @ColumnResult(name = "referanceNumber"), @ColumnResult(name = "createdBy"), @ColumnResult(name = "formOwner"), @ColumnResult(name = "businessUnit"), @ColumnResult(name = "costCenter"), @ColumnResult(name = "baseCurrency"), @ColumnResult(name = "status"), @ColumnResult(name = "groupCode"), @ColumnResult(name = "templateName"), @ColumnResult(name = "description"), @ColumnResult(name = "availableBudget"), @ColumnResult(name = "estimatedBudget"), @ColumnResult(name = "submittedDate"), @ColumnResult(name = "approvedDate"), @ColumnResult(name = "approvalDaysHours"), @ColumnResult(name = "approvalTotalLevels"), @ColumnResult(name = "approvalTotalUsers") }) }) })
public class RftEvent extends Event implements Serializable {

	private static final long serialVersionUID = -1835870053481809460L;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RftEventSupplier> suppliers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RftEventDocument> documents;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RftEventMeeting> meetings;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RftEventContact> eventContacts;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RftEventCorrespondenceAddress> eventCorrespondenceAddress;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RftEventBq> eventBqs;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RftEventSor> eventSors;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RftEnvelop> rftEnvelop;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RftCq> cqs;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rftEvent", orphanRemoval = true)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@OrderBy("reminderDate")
	private List<RftReminder> rftReminder;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RftComment> comment;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RftUnMaskedUser> unMaskedUsers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RftEvaluationConclusionUser> evaluationConclusionUsers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RftTeamMember> teamMembers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RftEventApproval> approvals;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFT_AWARD_SUP", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "SUPPLIER_ID"))
	private List<Supplier> awardedSuppliers;

	@DecimalMax("9999999999.999999")
	@Column(name = "AWARDED_PRICE", precision = 22, scale = 6, nullable = true)
	private BigDecimal awardedPrice;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFT_EVENT_INDUS_CAT", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "IND_CAT_ID"))
	private List<IndustryCategory> industryCategories;

	@Column(name = "ERP_AWARD_REF_ID", length = 500, nullable = true)
	@Size(min = 0, max = 500)
	private String erpAwardRefId;

	@Column(name = "ERP_AWARD_RESPONSE", length = 2000, nullable = true)
	@Size(min = 0, max = 2000)
	private String erpAwardResponse;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean uploadDocuments;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = true)
	@JoinColumn(name = "WINNING_SUPPLIER", foreignKey = @ForeignKey(name = "FK_RFT_WINNING_SUPP"))
	private Supplier winningSupplier;

	@Column(name = "WINNING_PRICE", precision = 20, scale = 4)
	private BigDecimal winningPrice;

	@Column(name = "PR_PUSH_DATE", nullable = true)
	private Date pushToPr;

	@Column(name = "ADD_BILL_OF_QUANTITY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean addBillOfQuantity = Boolean.TRUE;


	@Column(name = "ADD_SCHEDULE_OF_RATE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean addScheduleOfRate = Boolean.TRUE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rftEvent")
	private List<AdditionalDocument> additionalDocument;

	// PH-1701
	@Column(name = "DISABLE_TOTAL_AMOUNT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean disableTotalAmount = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RftEventSuspensionApproval> suspensionApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RftSuspensionComment> suspensionComment;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RftEventAwardApproval> awardApprovals;

	@Column(name = "IS_ENABLE_AWARD_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableAwardApproval = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RftAwardComment> awardComment;

	@Transient
	private List<RftSupplierBq> rftSupplierBqs;

	@Transient
	private List<RftSupplierSor> rftSupplierSors;

	@Enumerated(EnumType.STRING)
	@Column(name = "AWARD_STATUS")
	private AwardStatus awardStatus;

	public RftEvent() {
		super();
	}

	public RftEvent(String eventId) {
		this.setId(eventId);
	}

	public RftEvent(String eventId, String eventName) {
		this.setEventId(eventId);
		this.setEventName(eventName);
	}

	public RftEvent(String eventId, BigDecimal budgetAmount, Date eventStart, Date eventEnd, Currency baseCurrency) {
		this.setEventId(eventId);
		this.setBudgetAmount(budgetAmount);
		this.setEventStart(eventStart);
		this.setEventEnd(eventEnd);
		this.setBaseCurrency(baseCurrency);
	}

	public RftEvent(String id, Date eventPublishDate, String eventName, Date eventEnd, String referanceNumber) {
		super(id, eventPublishDate, eventName, eventEnd, referanceNumber);
	}

	public RftEvent(String id, String eventId, String eventName, String referenceNumber, BigDecimal fee, Currency feeCurrency, User user) {
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

	public RftEvent copyFrom(RftEvent oldEvent) {
		RftEvent newEvent = new RftEvent();
		// copy Event Detail
		copyEventDetails(oldEvent, newEvent);

		// Team
		copyTeamMemberDetails(oldEvent, newEvent);

		// Approval
		copyApprovalDetails(oldEvent, newEvent);
		newEvent.setEnableApprovalReminder(oldEvent.getEnableApprovalReminder());
		newEvent.setReminderAfterHour(oldEvent.getReminderAfterHour());
		newEvent.setReminderCount(oldEvent.getReminderCount());

		// Docs
		// copyDocs(oldEvent, newEvent);

		// CQ
		copyCqDetails(oldEvent, newEvent);

		// Sup
		copySupplierDetails(oldEvent, newEvent, null);

		// BQ
		copyBqDetails(oldEvent, newEvent, null, false);

		copySorDetails(oldEvent, newEvent, null, false);

		// Env
		copyEnvelopes(oldEvent, newEvent);

		// Unmasking
		copyUnmaskingDetails(oldEvent, newEvent);

		// Evaluation Conclusion
		copyEvaluationConclusionUsers(oldEvent, newEvent);

		// ..copy doc
		copyDocument(oldEvent, newEvent);

		return newEvent;
	}

	private void copyDocument(RftEvent oldEvent, RftEvent newEvent){
		if(CollectionUtil.isNotEmpty(oldEvent.getDocuments())){
			newEvent.setDocuments(new ArrayList<RftEventDocument>());
			for(RftEventDocument doc: oldEvent.getDocuments()){
				newEvent.getDocuments().add(doc.copyFrom(oldEvent));
			}
		}
	}


	private void copySorDetails(RftEvent oldEvent, RftEvent newEvent, List<RftSupplierSor> rfpSupplierSorList, boolean isSupplierInvited) {

		if(oldEvent.getScheduleOfRate() == Boolean.TRUE) {
			newEvent.setScheduleOfRate(Boolean.TRUE);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getEventSors())) {
			newEvent.setEventSors(new ArrayList<RftEventSor>());
			for (RftEventSor bq : oldEvent.getEventSors()) {
				RftEventSor newBq = bq.copyForRfq(isSupplierInvited);
				newEvent.getEventSors().add(newBq);

				if (isSupplierInvited) {

					newEvent.setRftSupplierSors(new ArrayList<RftSupplierSor>());
					for (RftSupplierSor supBq : rfpSupplierSorList) {
						newEvent.getRftSupplierSors().add(supBq.createForRft(newEvent, newBq));
					}
					newBq.setId(null);
					for (RftSorItem bqItem : newBq.getSorItems()) {
						bqItem.setId(null);
					}
				}

			}
		}
	}

	private void copyUnmaskingDetails(RftEvent oldEvent, RftEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getUnMaskedUsers())) {
			newEvent.setUnMaskedUsers((new ArrayList<RftUnMaskedUser>()));
			for (RftUnMaskedUser app : oldEvent.getUnMaskedUsers()) {
				RftUnMaskedUser usr = app.copyFrom();
				usr.setEvent(newEvent);
				newEvent.getUnMaskedUsers().add(usr);
			}
		}
	}

	private void copyEvaluationConclusionUsers(RftEvent oldEvent, RftEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getEvaluationConclusionUsers())) {
			newEvent.setEvaluationConclusionUsers(new ArrayList<RftEvaluationConclusionUser>());
			for (RftEvaluationConclusionUser app : oldEvent.getEvaluationConclusionUsers()) {
				RftEvaluationConclusionUser usr = app.copyFrom();
				usr.setEvent(newEvent);
				newEvent.getEvaluationConclusionUsers().add(usr);
			}
		}
	}

	private void copyEnvelopes(RftEvent oldEvent, RftEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getRftEnvelop())) {
			newEvent.setRftEnvelop(new ArrayList<RftEnvelop>());
			for (RftEnvelop envelop : oldEvent.getRftEnvelop()) {
				newEvent.getRftEnvelop().add(envelop.copyFrom());
			}
		}
	}

	private void copyCqDetails(RftEvent oldEvent, RftEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getCqs())) {
			newEvent.setCqs(new ArrayList<RftCq>());
			for (RftCq cq : oldEvent.getCqs()) {
				List<RftCqItem> cqList = cq.getCqItems();
				for (RftCqItem rftCqItem : cqList) {
					if (CqType.DOCUMENT_DOWNLOAD_LINK == rftCqItem.getCqType()) {
						newEvent.setUploadDocuments(true);
					}
				}
				newEvent.getCqs().add(cq.copyFrom());
			}
		}
	}

	private void copyEventDetails(RftEvent oldEvent, RftEvent newEvent) {
		Log.info("Old View Supplier Name " + oldEvent.getViewSupplerName());
		newEvent.setViewSupplerName(oldEvent.getViewSupplerName());
		newEvent.setEnableEvaluationConclusionUsers(oldEvent.getEnableEvaluationConclusionUsers() == null ? Boolean.FALSE : oldEvent.getEnableEvaluationConclusionUsers());
		newEvent.setUnMaskedUser(oldEvent.getUnMaskedUser());
		newEvent.setAllowToSuspendEvent(oldEvent.getAllowToSuspendEvent());
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setScheduleOfRate(oldEvent.getScheduleOfRate());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setReferanceNumber(getReferanceNumber());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
		newEvent.setAddBillOfQuantity(oldEvent.getAddBillOfQuantity());
		newEvent.setRfxEnvelopeReadOnly(oldEvent.getRfxEnvelopeReadOnly());
		newEvent.setUrgentEvent(oldEvent.getUrgentEvent());
		newEvent.setRfxEnvelopeOpening(oldEvent.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(oldEvent.getRfxEnvOpeningAfter());
		newEvent.setMaximumSupplierRating(oldEvent.getMaximumSupplierRating());
		newEvent.setMinimumSupplierRating(oldEvent.getMinimumSupplierRating());
		newEvent.setEnableSuspensionApproval(oldEvent.getEnableSuspensionApproval());
		newEvent.setGroupCode(oldEvent.getGroupCode());
		newEvent.setAllowDisqualifiedSupplierDownload(oldEvent.getAllowDisqualifiedSupplierDownload());

		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RftEventContact>());
			for (RftEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRft());
			}
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RftEventCorrespondenceAddress>());
			for (RftEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
		newEvent.setEnableEvaluationDeclaration(oldEvent.getEnableEvaluationDeclaration());
		newEvent.setEstimatedBudget(oldEvent.getEstimatedBudget());
		newEvent.setProcurementMethod(oldEvent.getProcurementMethod());
		newEvent.setProcurementCategories(oldEvent.getProcurementCategories());
		newEvent.setGroupCode(oldEvent.getGroupCode());
		if (oldEvent.getEvaluationProcessDeclaration() != null) {
			newEvent.setEvaluationProcessDeclaration(oldEvent.getEvaluationProcessDeclaration());
		}
		newEvent.setEnableSupplierDeclaration(oldEvent.getEnableSupplierDeclaration());
		if (oldEvent.getSupplierAcceptanceDeclaration() != null) {
			newEvent.setSupplierAcceptanceDeclaration(oldEvent.getSupplierAcceptanceDeclaration());
		}
		newEvent.setNotifyEventOwner(oldEvent.getNotifyEventOwner());
		newEvent.setDisableTotalAmount(oldEvent.getDisableTotalAmount());
	}

	public RftEvent createNextRftEvent(RftEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RftSupplierBq> rftSupplierBqList) {
		RftEvent newEvent = new RftEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFT);
		// copy Delivery Address
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());

		// Copy Event Details
		copyEventDetails(oldEvent, newEvent);
		// copy supp
		copySupplierDetails(oldEvent, newEvent, invitedSupp);
		// copy Bq
		copyBqDetails(oldEvent, newEvent, rftSupplierBqList, (invitedSupp != null && invitedSupp.length > 0));

		return newEvent;
	}

	private void copySupplierDetails(RftEvent oldEvent, RftEvent newEvent, String[] invitedSupp) {
		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RftEventSupplier>());
			for (RftEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRft());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRft());
				}
			}
		}
	}

	private void copyTeamMemberDetails(RftEvent oldEvent, RftEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getTeamMembers())) {
			newEvent.setTeamMembers(new ArrayList<RftTeamMember>());
			for (RftTeamMember tm : oldEvent.getTeamMembers()) {
				newEvent.getTeamMembers().add(tm.copyFrom());
			}
		}
	}

	private void copyApprovalDetails(RftEvent oldEvent, RftEvent newEvent) {

		if (CollectionUtil.isNotEmpty(oldEvent.getApprovals())) {
			newEvent.setApprovals(new ArrayList<RftEventApproval>());
			for (RftEventApproval app : oldEvent.getApprovals()) {
				newEvent.getApprovals().add(app.copyFrom());
			}
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuspensionApprovals())) {
			newEvent.setSuspensionApprovals(new ArrayList<RftEventSuspensionApproval>());
			for (RftEventSuspensionApproval app : oldEvent.getSuspensionApprovals()) {
				newEvent.getSuspensionApprovals().add(app.copyFrom());
			}
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getAwardApprovals())) {
			newEvent.setAwardApprovals(new ArrayList<RftEventAwardApproval>());
			for (RftEventAwardApproval app : oldEvent.getAwardApprovals()) {
				newEvent.getAwardApprovals().add(app.copyFrom());
			}
		}

	}

	private void copyBqDetails(RftEvent oldEvent, RftEvent newEvent, List<RftSupplierBq> rftSupplierBqs, boolean isSupplierInvited) {

		if (CollectionUtil.isNotEmpty(oldEvent.getEventBqs())) {
			newEvent.setEventBqs(new ArrayList<RftEventBq>());
			for (RftEventBq bq : oldEvent.getEventBqs()) {
				RftEventBq newBq = bq.copyForRft(isSupplierInvited);
				newEvent.getEventBqs().add(newBq);
				// Now copy Supplier Bq over to the new event
				if (isSupplierInvited) {
					newEvent.setRftSupplierBqs(new ArrayList<RftSupplierBq>());
					for (RftSupplierBq rftSupplierBq : rftSupplierBqs) {
						newEvent.getRftSupplierBqs().add(rftSupplierBq.createForRft(newEvent, newBq, bq));
					}
					newBq.setId(null);
					for (RftBqItem bqItem : newBq.getBqItems()) {
						bqItem.setId(null); // clear the original ID that was used temporarily
					}
				}
			}
		}
	}

	public RfpEvent createNextRfpEvent(RftEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RftSupplierBq> rftSupplierBqList) {
		RfpEvent newEvent = new RfpEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFT);
		newEvent.setEventName(oldEvent.getEventName());
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());
		newEvent.setCreatedDate(new Date());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
		newEvent.setAddBillOfQuantity(oldEvent.getAddBillOfQuantity());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfpEventContact>());
			for (RftEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfp());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfpEventCorrespondenceAddress>());
			for (RftEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
		newEvent.setDisableTotalAmount(oldEvent.getDisableTotalAmount());
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
			for (RftEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfp());
					}

				} else {
					newEvent.getSuppliers().add(supp.copyForRfp());
				}
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventBqs())) {
			newEvent.setEventBqs(new ArrayList<RfpEventBq>());
			for (RftEventBq bq : oldEvent.getEventBqs()) {
				RfpEventBq newBq = bq.copyForRfp(invitedSuppliersOnly);
				newEvent.getEventBqs().add(newBq);
				if (invitedSuppliersOnly) {
					newEvent.setRfpSupplierBqs(new ArrayList<RfpSupplierBq>());
					for (RftSupplierBq rfpSupplierBq : rftSupplierBqList) {
						newEvent.getRfpSupplierBqs().add(rfpSupplierBq.createForRfp(newEvent, newBq));
					}
					newBq.setId(null);
					for (RfpBqItem bqItem : newBq.getBqItems()) {
						bqItem.setId(null); // clear the original ID that was used temporarily
					}
				}
			}
		}
		return newEvent;
	}

	public RfqEvent createNextRfqEvent(RftEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RftSupplierBq> rftSupplierBqList) {
		RfqEvent newEvent = new RfqEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFT);
		newEvent.setEventName(oldEvent.getEventName());
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());
		newEvent.setCreatedDate(new Date());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setAddBillOfQuantity(oldEvent.getAddBillOfQuantity());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfqEventContact>());
			for (RftEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfq());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfqEventCorrespondenceAddress>());
			for (RftEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
		newEvent.setDisableTotalAmount(oldEvent.getDisableTotalAmount());
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
			for (RftEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfq());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRfq());
				}
			}
		}

		// copy Bq's and supplier Bq's
		if (CollectionUtil.isNotEmpty(oldEvent.getEventBqs())) {
			newEvent.setEventBqs(new ArrayList<RfqEventBq>());
			for (RftEventBq bq : oldEvent.getEventBqs()) {
				RfqEventBq newBq = bq.copyForRfq(invitedSuppliersOnly);
				newEvent.getEventBqs().add(newBq);

				// Now copy Supplier Bq over to the new event
				if (invitedSuppliersOnly) {
					newEvent.setRfqSupplierBqs(new ArrayList<RfqSupplierBq>());
					for (RftSupplierBq rftSupplierBq : rftSupplierBqList) {
						newEvent.getRfqSupplierBqs().add(rftSupplierBq.createForRfq(newEvent, newBq));
					}
					newBq.setId(null);
					for (RfqBqItem bqItem : newBq.getBqItems()) {
						bqItem.setId(null); // clear the original ID that was used temporarily
					}
				}

			}
		}
		return newEvent;
	}

	public RfiEvent createNextRfiEvent(RftEvent oldEvent, User loggedInUser, String[] invitedSupp) {
		RfiEvent newEvent = new RfiEvent();
		newEvent.setBillOfQuantity(Boolean.FALSE);
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setScheduleOfRate(oldEvent.getScheduleOfRate());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFT);
		newEvent.setEventName(oldEvent.getEventName());
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
		// copy Temlate
		newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());
		newEvent.setCreatedDate(new Date());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfiEventContact>());
			for (RftEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfi());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfiEventCorrespondenceAddress>());
			for (RftEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
				newEvent.getEventCorrespondenceAddress().add(correspondenceAddress.copyForRfi());
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
		newEvent.setProcurementCategories(oldEvent.getProcurementCategories());
		newEvent.setProcurementMethod(oldEvent.getProcurementMethod());
		newEvent.setGroupCode(oldEvent.getGroupCode());

		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RfiEventSupplier>());
			for (RftEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfi());
					}

				} else {
					newEvent.getSuppliers().add(supp.copyForRfi());
				}
			}
		}
		return newEvent;
	}

	public RfaEvent createNextRfaEvent(RftEvent oldEvent, AuctionType auctionType, String bqId, User loggedInUser, String[] invitedSupp, List<RftSupplierBq> rftSupplierBqList) {
		RfaEvent newEvent = new RfaEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setScheduleOfRate(oldEvent.getScheduleOfRate());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setPreviousEventType(RfxTypes.RFT);
		newEvent.setAuctionType(auctionType);
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());

		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());
		newEvent.setCreatedDate(new Date());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setAddBillOfQuantity(oldEvent.getAddBillOfQuantity());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfaEventContact>());
			for (RftEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfa());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfaEventCorrespondenceAddress>());
			for (RftEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
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
		newEvent.setDisableTotalAmount(oldEvent.getDisableTotalAmount());

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
			for (RftEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfa());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRfa());
				}
			}
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getEventBqs())) {
			newEvent.setEventBqs(new ArrayList<RfaEventBq>());
			for (RftEventBq bq : oldEvent.getEventBqs()) {
				if (bq.getId().equals(bqId)) {
					RfaEventBq newBq = bq.copyForRfa(invitedSuppliersOnly);
					newEvent.getEventBqs().add(newBq);

					// Now copy Supplier Bq over to the new event
					if (invitedSuppliersOnly) {
						newEvent.setRfaSupplierBqs(new ArrayList<RfaSupplierBq>());
						for (RftSupplierBq supBq : rftSupplierBqList) {
							newEvent.getRfaSupplierBqs().add(supBq.createForRfa(newEvent, newBq));
						}
						newBq.setId(null); // clear the original ID that was used temporarily
						for (RfaBqItem bqItem : newBq.getBqItems()) {
							bqItem.setId(null); // clear the original ID that was used temporarily
						}
					}
				}
			}
		}
		return newEvent;
	}

	/**
	 * @return the disableTotalAmount
	 */
	public Boolean getDisableTotalAmount() {
		return disableTotalAmount;
	}

	/**
	 * @param disableTotalAmount the disableTotalAmount to set
	 */
	public void setDisableTotalAmount(Boolean disableTotalAmount) {
		this.disableTotalAmount = disableTotalAmount;
	}

	/**
	 * @return the comment
	 */
	public List<RftComment> getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(List<RftComment> comment) {
		this.comment = comment;
	}

	public List<RftEventSupplier> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<RftEventSupplier> suppliers) {
		this.suppliers = suppliers;
	}

	/**
	 * @return the documents
	 */
	public List<RftEventDocument> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<RftEventDocument> documents) {
		this.documents = documents;
	}

	/**
	 * @return the meetings
	 */
	public List<RftEventMeeting> getMeetings() {
		return meetings;
	}

	/**
	 * @param meetings the meetings to set
	 */
	public void setMeetings(List<RftEventMeeting> meetings) {
		this.meetings = meetings;
	}

	/**
	 * @return the eventContacts
	 */
	public List<RftEventContact> getEventContacts() {
		return eventContacts;
	}

	/**
	 * @param eventContacts the eventContacts to set
	 */
	public void setEventContacts(List<RftEventContact> eventContacts) {
		this.eventContacts = eventContacts;
	}

	/**
	 * @return the eventCorrespondenceAddress
	 */
	public List<RftEventCorrespondenceAddress> getEventCorrespondenceAddress() {
		return eventCorrespondenceAddress;
	}

	/**
	 * @param eventCorrespondenceAddress the eventCorrespondenceAddress to set
	 */
	public void setEventCorrespondenceAddress(List<RftEventCorrespondenceAddress> eventCorrespondenceAddress) {
		this.eventCorrespondenceAddress = eventCorrespondenceAddress;
	}

	/**
	 * @return the eventBqs
	 */
	public List<RftEventBq> getEventBqs() {
		return eventBqs;
	}

	/**
	 * @param eventBqs the eventBqs to set
	 */
	public void setEventBqs(List<RftEventBq> eventBqs) {
		this.eventBqs = eventBqs;
	}

	/**
	 * @return the rftEnvelop
	 */
	public List<RftEnvelop> getRftEnvelop() {
		return rftEnvelop;
	}

	/**
	 * @param rftEnvelop the rftEnvelop to set
	 */
	public void setRftEnvelop(List<RftEnvelop> rftEnvelop) {
		this.rftEnvelop = rftEnvelop;
	}

	/**
	 * @return the cqs
	 */
	public List<RftCq> getCqs() {
		return cqs;
	}

	/**
	 * @param cqs the cqs to set
	 */
	public void setCqs(List<RftCq> cqs) {
		this.cqs = cqs;
	}

	/**
	 * @return the rftReminder
	 */
	public List<RftReminder> getRftReminder() {
		return rftReminder;
	}

	/**
	 * @param rftReminder the rftReminder to set
	 */
	public void setRftReminder(List<RftReminder> rftReminder) {
		this.rftReminder = rftReminder;
	}

	/**
	 * @return the teamMembers
	 */
	public List<RftTeamMember> getTeamMembers() {
		return teamMembers;
	}

	/**
	 * @param teamMembers the teamMembers to set
	 */
	public void setTeamMembers(List<RftTeamMember> teamMembers) {
		this.teamMembers = teamMembers;
	}

	/**
	 * @return the approvals
	 */
	public List<RftEventApproval> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<RftEventApproval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<RftEventApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (approvals != null) {
				for (RftEventApproval oldApproval : this.approvals) {
					for (RftEventApproval newApproval : approvals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);

							// Preserve individual approval user old state
							for (RftApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RftApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
	 * @return the awardedSuppliers
	 */
	public List<Supplier> getAwardedSuppliers() {
		return awardedSuppliers;
	}

	/**
	 * @param awardedSuppliers the awardedSuppliers to set
	 */
	public void setAwardedSuppliers(List<Supplier> awardedSuppliers) {
		this.awardedSuppliers = awardedSuppliers;
	}

	/**
	 * @return the awardedPrice
	 */
	public BigDecimal getAwardedPrice() {
		return awardedPrice;
	}

	/**
	 * @param awardedPrice the awardedPrice to set
	 */
	public void setAwardedPrice(BigDecimal awardedPrice) {
		this.awardedPrice = awardedPrice;
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
	 * @return the erpAwardRefId
	 */
	public String getErpAwardRefId() {
		return erpAwardRefId;
	}

	/**
	 * @param erpAwardRefId the erpAwardRefId to set
	 */
	public void setErpAwardRefId(String erpAwardRefId) {
		this.erpAwardRefId = erpAwardRefId;
	}

	/**
	 * @return the erpAwardResponse
	 */
	public String getErpAwardResponse() {
		return erpAwardResponse;
	}

	/**
	 * @param erpAwardResponse the erpAwardResponse to set
	 */
	public void setErpAwardResponse(String erpAwardResponse) {
		this.erpAwardResponse = erpAwardResponse;
	}

	public boolean isUploadDocuments() {
		return uploadDocuments;
	}

	public void setUploadDocuments(boolean uploadDocuments) {
		this.uploadDocuments = uploadDocuments;
	}

	/**
	 * @return the winningSupplier
	 */
	public Supplier getWinningSupplier() {
		return winningSupplier;
	}

	/**
	 * @param winningSupplier the winningSupplier to set
	 */
	public void setWinningSupplier(Supplier winningSupplier) {
		this.winningSupplier = winningSupplier;
	}

	/**
	 * @return the winningPrice
	 */
	public BigDecimal getWinningPrice() {
		return winningPrice;
	}

	/**
	 * @param winningPrice the winningPrice to set
	 */
	public void setWinningPrice(BigDecimal winningPrice) {
		this.winningPrice = winningPrice;
	}

	/**
	 * @return the rftSupplierBqs
	 */
	public List<RftSupplierBq> getRftSupplierBqs() {
		return rftSupplierBqs;
	}

	/**
	 * @param rftSupplierBqs the rftSupplierBqs to set
	 */
	public void setRftSupplierBqs(List<RftSupplierBq> rftSupplierBqs) {
		this.rftSupplierBqs = rftSupplierBqs;
	}

	public Date getPushToPr() {
		return pushToPr;
	}

	public void setPushToPr(Date pushToPr) {
		this.pushToPr = pushToPr;
	}

	public Boolean getAddBillOfQuantity() {
		return addBillOfQuantity;
	}

	public void setAddBillOfQuantity(Boolean addBillOfQuantity) {
		this.addBillOfQuantity = addBillOfQuantity;
	}

	public List<AdditionalDocument> getAdditionalDocument() {
		return additionalDocument;
	}

	public void setAdditionalDocument(List<AdditionalDocument> additionalDocument) {
		this.additionalDocument = additionalDocument;
	}

	/**
	 * @return the unMaskedUsers
	 */
	public List<RftUnMaskedUser> getUnMaskedUsers() {
		return unMaskedUsers;
	}

	/**
	 * @param unMaskedUsers the unMaskedUsers to set
	 */
	public void setUnMaskedUsers(List<RftUnMaskedUser> unMaskedUsers) {
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
	public List<RftEvaluationConclusionUser> getEvaluationConclusionUsers() {
		return evaluationConclusionUsers;
	}

	/**
	 * @param evaluationConclusionUsers the evaluationConclusionUsers to set
	 */
	public void setEvaluationConclusionUsers(List<RftEvaluationConclusionUser> evaluationConclusionUsers) {
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
	public List<RftEventSuspensionApproval> getSuspensionApprovals() {
		return suspensionApprovals;
	}

	/**
	 * @param suspensionApprovals the suspensionApprovals to set
	 */
	public void setSuspensionApprovals(List<RftEventSuspensionApproval> suspensionApprovals) {
		if (this.suspensionApprovals == null) {
			this.suspensionApprovals = new ArrayList<RftEventSuspensionApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (suspensionApprovals != null) {
				for (RftEventSuspensionApproval oldApproval : this.suspensionApprovals) {
					for (RftEventSuspensionApproval newApproval : suspensionApprovals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);

							// Preserve individual approval user old state
							for (RftSuspensionApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RftSuspensionApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
	public List<RftSuspensionComment> getSuspensionComment() {
		return suspensionComment;
	}

	/**
	 * @param suspensionComment the suspensionComment to set
	 */
	public void setSuspensionComment(List<RftSuspensionComment> suspensionComment) {
		this.suspensionComment = suspensionComment;
	}

	/**
	 * @return the awardApprovals
	 */
	public List<RftEventAwardApproval> getAwardApprovals() {
		return awardApprovals;
	}

	/**
	 * @return the awardComment
	 */
	public List<RftAwardComment> getAwardComment() {
		return awardComment;
	}

	/**
	 * @param awardApprovals the awardApprovals to set
	 */
	public void setAwardApprovals(List<RftEventAwardApproval> awardApprovals) {
		if (this.awardApprovals == null) {
			this.awardApprovals = new ArrayList<RftEventAwardApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (awardApprovals != null) {
				for (RftEventAwardApproval oldApproval : this.awardApprovals) {
					for (RftEventAwardApproval newApproval : awardApprovals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RftAwardApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RftAwardApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
			this.awardApprovals.clear();
		}
		if (awardApprovals != null) {
			this.awardApprovals.addAll(awardApprovals);
		}
	}

	/**
	 * @param awardComment the awardComment to set
	 */
	public void setAwardComment(List<RftAwardComment> awardComment) {
		this.awardComment = awardComment;
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

	/**
	 * @return the enableAwardApproval
	 */
	public Boolean getEnableAwardApproval() {
		return enableAwardApproval;
	}

	/**
	 * @param enableAwardApproval the enableAwardApproval to set
	 */
	public void setEnableAwardApproval(Boolean enableAwardApproval) {
		this.enableAwardApproval = enableAwardApproval;
	}

	/**
	 * @return the awardStatus
	 */
	public AwardStatus getAwardStatus() {
		return awardStatus;
	}

	/**
	 * @param awardStatus the awardStatus to set
	 */
	public void setAwardStatus(AwardStatus awardStatus) {
		this.awardStatus = awardStatus;
	}


	public List<RftEventSor> getEventSors() {
		return eventSors;
	}

	public void setEventSors(List<RftEventSor> eventSors) {
		this.eventSors = eventSors;
	}

	public Boolean getAddScheduleOfRate() {
		return addScheduleOfRate;
	}

	public void setAddScheduleOfRate(Boolean addScheduleOfRate) {
		this.addScheduleOfRate = addScheduleOfRate;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RftEvent [toLogString()=" + super.toLogString() + "]";
	}

	public List<RftSupplierSor> getRftSupplierSors() {
		return rftSupplierSors;
	}

	public void setRftSupplierSors(List<RftSupplierSor> rftSupplierSors) {
		this.rftSupplierSors = rftSupplierSors;
	}
}
