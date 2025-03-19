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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AwardStatus;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.FinishedEventPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import org.hibernate.annotations.Type;

/**
 * @author Ravi
 */

@Entity
@Table(name = "PROC_RFP_EVENTS", indexes = { @Index(columnList = "TENANT_ID", name = "IDX_RFP_TENANT_ID") })
@SqlResultSetMapping(name = "finishedEventResult", classes = { @ConstructorResult(targetClass = FinishedEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventName"), @ColumnResult(name = "createdBy"), @ColumnResult(name = "createdDate"), @ColumnResult(name = "type"), @ColumnResult(name = "eventStart"), @ColumnResult(name = "eventEnd"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "unitName"), @ColumnResult(name = "sysEventId"), @ColumnResult(name = "eventUser") }) })
public class RfpEvent extends Event implements Serializable {

	private static final long serialVersionUID = -7370899245667164560L;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfpEventSupplier> suppliers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfpEventDocument> documents;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfpEventMeeting> meetings;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfpEventContact> eventContacts;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfpEventCorrespondenceAddress> eventCorrespondenceAddress;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfpEventBq> eventBqs;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfpEnvelop> rfxEnvelop;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfpCq> cqs;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("reminderDate")
	private List<RfpReminder> reminder;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFP_EVENT_VIEWERS", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private List<User> eventViewers;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFP_EVENT_EDITORS", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private List<User> eventEditors;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfpTeamMember> teamMembers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RfpEventApproval> approvals;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFP_AWARD_SUP", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "SUPPLIER_ID"))
	private List<Supplier> awardedSuppliers;

	@DecimalMax("9999999999.999999")
	@Column(name = "AWARDED_PRICE", precision = 20, scale = 6, nullable = true)
	private BigDecimal awardedPrice;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RfpComment> comment;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFP_EVENT_INDUS_CAT", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "IND_CAT_ID"))
	private List<IndustryCategory> industryCategories;

	@Column(name = "ERP_AWARD_REF_ID", length = 500, nullable = true)
	@Size(min = 0, max = 500)
	private String erpAwardRefId;

	@Column(name = "ERP_AWARD_RESPONSE", length = 2000, nullable = true)
	@Size(min = 0, max = 2000)
	private String erpAwardResponse;

	@Column(name = "PR_PUSH_DATE", nullable = true)
	private Date pushToPr;

	@Column(name = "ADD_BILL_OF_QUANTITY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean addBillOfQuantity = Boolean.TRUE;

	@Column(name = "ADD_SCHEDULE_OF_RATE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean addScheduleOfRate = Boolean.TRUE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfpUnMaskedUser> unMaskedUsers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfpEvaluationConclusionUser> evaluationConclusionUsers;

	@Column(name = "DISABLE_TOTAL_AMOUNT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean disableTotalAmount = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RfpEventSuspensionApproval> suspensionApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RfpSuspensionComment> suspensionComment;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RfpEventAwardApproval> awardApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RfpAwardComment> awardComment;
	
	@Column(name = "IS_ENABLE_AWARD_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableAwardApproval = Boolean.FALSE;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "AWARD_STATUS")
	private AwardStatus awardStatus;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfpEventSor> eventSors;

	public Date getPushToPr() {
		return pushToPr;
	}

	public void setPushToPr(Date pushToPr) {
		this.pushToPr = pushToPr;
	}

	public RfpEvent() {
		super();
	}

	public RfpEvent(String eventId, BigDecimal budgetAmount, Date eventStart, Date eventEnd, Currency baseCurrency) {
		this.setEventId(eventId);
		this.setBudgetAmount(budgetAmount);
		this.setEventStart(eventStart);
		this.setEventEnd(eventEnd);
		this.setBaseCurrency(baseCurrency);

	}

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean uploadDocuments;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = true)
	@JoinColumn(name = "WINNING_SUPPLIER", foreignKey = @ForeignKey(name = "FK_RFP_WINNING_SUPP"))
	private Supplier winningSupplier;

	@Column(name = "WINNING_PRICE", precision = 20, scale = 4)
	private BigDecimal winningPrice;

	@Transient
	private List<RfpSupplierBq> rfpSupplierBqs;

	@Transient
	private List<RfpSupplierSor> rfpSupplierSors;

	/**
	 * @return the rfpSupplierBqs
	 */
	public List<RfpSupplierBq> getRfpSupplierBqs() {
		return rfpSupplierBqs;
	}

	/**
	 * @param rfpSupplierBqs the rfpSupplierBqs to set
	 */
	public void setRfpSupplierBqs(List<RfpSupplierBq> rfpSupplierBqs) {
		this.rfpSupplierBqs = rfpSupplierBqs;
	}

	public RfpEvent(String eventId, String eventName) {
		this.setEventId(eventId);
		this.setEventName(eventName);
	}

	public RfpEvent copyFrom(RfpEvent oldEvent) {
		RfpEvent newEvent = new RfpEvent();
		// Event Details
		copyEventDetails(oldEvent, newEvent);
		// Team
		copyTeamMemberDetails(oldEvent, newEvent);
		// Approval
		copyApprovalDetails(oldEvent, newEvent);

		newEvent.setEnableApprovalReminder(oldEvent.getEnableApprovalReminder());
		newEvent.setReminderAfterHour(oldEvent.getReminderAfterHour());
		newEvent.setReminderCount(oldEvent.getReminderCount());
		// CQ
		copyCqDetails(oldEvent, newEvent);
		// Sup
		copySupplierDetails(oldEvent, newEvent, null);
		// BQ
		copyBqDetails(oldEvent, newEvent, null, false);

		copySorDetails(oldEvent, newEvent, null, false);
		// Env
		copyEnvelopes(oldEvent, newEvent);

		copyUnmaskingDetails(oldEvent, newEvent);

		// Evaluation Conclusion
		copyEvaluationConclusionUsers(oldEvent, newEvent);

		// ..doc
		copyDocument(oldEvent, newEvent);

		return newEvent;
	}

	private void copyDocument(RfpEvent oldEvent, RfpEvent newEvent){
		if(CollectionUtil.isNotEmpty(oldEvent.getDocuments())){
			newEvent.setDocuments(new ArrayList<RfpEventDocument>());
			for(RfpEventDocument doc: oldEvent.getDocuments()){
				newEvent.getDocuments().add(doc.copyFrom(oldEvent));
			}
		}
	}


	private void copyUnmaskingDetails(RfpEvent oldEvent, RfpEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getUnMaskedUsers())) {
			newEvent.setUnMaskedUsers((new ArrayList<RfpUnMaskedUser>()));
			for (RfpUnMaskedUser app : oldEvent.getUnMaskedUsers()) {
				RfpUnMaskedUser usr = app.copyFrom();
				usr.setEvent(newEvent);
				newEvent.getUnMaskedUsers().add(usr);
			}
		}
	}

	private void copyEvaluationConclusionUsers(RfpEvent oldEvent, RfpEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getEvaluationConclusionUsers())) {
			newEvent.setEvaluationConclusionUsers(new ArrayList<RfpEvaluationConclusionUser>());
			for (RfpEvaluationConclusionUser app : oldEvent.getEvaluationConclusionUsers()) {
				RfpEvaluationConclusionUser usr = app.copyFrom();
				usr.setEvent(newEvent);
				newEvent.getEvaluationConclusionUsers().add(usr);
			}
		}
	}

	private RfpEvent copyEventDetails(RfpEvent oldEvent, RfpEvent newEvent) {
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setScheduleOfRate(oldEvent.getScheduleOfRate());
		newEvent.setAllowToSuspendEvent(oldEvent.getAllowToSuspendEvent());
		newEvent.setViewSupplerName(oldEvent.getViewSupplerName());
		newEvent.setUnMaskedUser(oldEvent.getUnMaskedUser());
		newEvent.setEnableEvaluationConclusionUsers(oldEvent.getEnableEvaluationConclusionUsers() == null ? Boolean.FALSE : oldEvent.getEnableEvaluationConclusionUsers());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setAddSupplier(oldEvent.getAddSupplier());
		newEvent.setAddBillOfQuantity(oldEvent.getAddBillOfQuantity());
		newEvent.setUrgentEvent(oldEvent.getUrgentEvent());
		newEvent.setRfxEnvelopeReadOnly(oldEvent.getRfxEnvelopeReadOnly());
		newEvent.setRfxEnvelopeOpening(oldEvent.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(oldEvent.getRfxEnvOpeningAfter());
		newEvent.setMaximumSupplierRating(oldEvent.getMaximumSupplierRating());
		newEvent.setMinimumSupplierRating(oldEvent.getMinimumSupplierRating());
		newEvent.setEnableSuspensionApproval(oldEvent.getEnableSuspensionApproval());
		newEvent.setGroupCode(oldEvent.getGroupCode());
		newEvent.setAllowDisqualifiedSupplierDownload(oldEvent.getAllowDisqualifiedSupplierDownload());

		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfpEventContact>());
			for (RfpEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyFrom(newEvent));
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfpEventCorrespondenceAddress>());
			for (RfpEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
				newEvent.getEventCorrespondenceAddress().add(correspondenceAddress.copyFrom(newEvent));
			}
		}
		newEvent.setEventDescription(oldEvent.getEventDescription());
		newEvent.setInternalRemarks(oldEvent.getInternalRemarks());
		newEvent.setEventOwner(oldEvent.getEventOwner());
		newEvent.setEventVisibility(oldEvent.getEventVisibility());
		newEvent.setEventVisibilityDates(oldEvent.getEventVisibilityDates());
		newEvent.setHistoricaAmount(oldEvent.getHistoricaAmount());
		newEvent.setEstimatedBudget(oldEvent.getEstimatedBudget());
		newEvent.setProcurementMethod(oldEvent.getProcurementMethod());
		newEvent.setProcurementCategories(oldEvent.getProcurementCategories());
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
		if (oldEvent.getEvaluationProcessDeclaration() != null) {
			newEvent.setEvaluationProcessDeclaration(oldEvent.getEvaluationProcessDeclaration());
		}
		newEvent.setEnableSupplierDeclaration(oldEvent.getEnableSupplierDeclaration());
		if (oldEvent.getSupplierAcceptanceDeclaration() != null) {
			newEvent.setSupplierAcceptanceDeclaration(oldEvent.getSupplierAcceptanceDeclaration());
		}
		newEvent.setNotifyEventOwner(oldEvent.getNotifyEventOwner());
		newEvent.setDisableTotalAmount(oldEvent.getDisableTotalAmount());

		return newEvent;
	}

	private void copyEnvelopes(RfpEvent oldEvent, RfpEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getRfxEnvelop())) {
			newEvent.setRfxEnvelop(new ArrayList<RfpEnvelop>());
			for (RfpEnvelop envelop : oldEvent.getRfxEnvelop()) {
				newEvent.getRfxEnvelop().add(envelop.copyFrom());
			}
		}
	}

	private void copySorDetails(RfpEvent oldEvent, RfpEvent newEvent, List<RfpSupplierSor> rfpSupplierSorList, boolean isSupplierInvited) {

		if (CollectionUtil.isNotEmpty(oldEvent.getEventSors())) {
			newEvent.setEventSors(new ArrayList<RfpEventSor>());
			for (RfpEventSor bq : oldEvent.getEventSors()) {
				RfpEventSor newBq = bq.copyForRfq(isSupplierInvited);
				newEvent.getEventSors().add(newBq);

				if (isSupplierInvited) {

					newEvent.setRfpSupplierSors(new ArrayList<RfpSupplierSor>());
					for (RfpSupplierSor supBq : rfpSupplierSorList) {
						newEvent.getRfpSupplierSors().add(supBq.createForRfp(newEvent, newBq));
					}
					newBq.setId(null);
					for (RfpSorItem bqItem : newBq.getSorItems()) {
						bqItem.setId(null);
					}
				}

			}
		}
	}

	private void copyCqDetails(RfpEvent oldEvent, RfpEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getCqs())) {
			newEvent.setCqs(new ArrayList<RfpCq>());
			for (RfpCq cq : oldEvent.getCqs()) {
				List<RfpCqItem> cqList = cq.getCqItems();
				for (RfpCqItem rfpCqItem : cqList) {
					if (CqType.DOCUMENT_DOWNLOAD_LINK == rfpCqItem.getCqType()) {
						newEvent.setUploadDocuments(true);
					}
				}
				newEvent.getCqs().add(cq.copyFrom());
			}
		}
	}

	private void copySupplierDetails(RfpEvent oldEvent, RfpEvent newEvent, String[] invitedSupp) {

		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RfpEventSupplier>());
			for (RfpEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfp());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRfp());
				}
			}
		}
	}

	private void copyTeamMemberDetails(RfpEvent oldEvent, RfpEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getTeamMembers())) {
			newEvent.setTeamMembers(new ArrayList<RfpTeamMember>());
			for (RfpTeamMember tm : oldEvent.getTeamMembers()) {
				newEvent.getTeamMembers().add(tm.copyFrom());
			}
		}
	}

	private void copyBqDetails(RfpEvent oldEvent, RfpEvent newEvent, List<RfpSupplierBq> rfpSupplierBqList, boolean invitedSupp) {
		if (CollectionUtil.isNotEmpty(oldEvent.getEventBqs())) {
			newEvent.setEventBqs(new ArrayList<RfpEventBq>());
			for (RfpEventBq bq : oldEvent.getEventBqs()) {
				RfpEventBq newBq = bq.copyForRfp(invitedSupp);
				newEvent.getEventBqs().add(newBq);

				if (invitedSupp) {
					newEvent.setRfpSupplierBqs(new ArrayList<RfpSupplierBq>());
					for (RfpSupplierBq rfpSupplierBq : rfpSupplierBqList) {
						newEvent.getRfpSupplierBqs().add(rfpSupplierBq.createForRfp(newEvent, newBq));
					}
					newBq.setId(null);
					for (RfpBqItem bqItem : newBq.getBqItems()) {
						bqItem.setId(null); // clear the original ID that was used temporarily
					}
				}
			}
		}
	}

	private void copyApprovalDetails(RfpEvent oldEvent, RfpEvent newEvent) {

		if (CollectionUtil.isNotEmpty(oldEvent.getApprovals())) {
			newEvent.setApprovals(new ArrayList<RfpEventApproval>());
			for (RfpEventApproval app : oldEvent.getApprovals()) {
				newEvent.getApprovals().add(app.copyFrom());
			}
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuspensionApprovals())) {
			newEvent.setSuspensionApprovals(new ArrayList<RfpEventSuspensionApproval>());
			for (RfpEventSuspensionApproval app : oldEvent.getSuspensionApprovals()) {
				newEvent.getSuspensionApprovals().add(app.copyFrom());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getAwardApprovals())) {
			newEvent.setAwardApprovals(new ArrayList<RfpEventAwardApproval>());
			for (RfpEventAwardApproval app : oldEvent.getAwardApprovals()) {
				newEvent.getAwardApprovals().add(app.copyFrom());
			}
		}

	}

	public RfpEvent createNextRfpEvent(RfpEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RfpSupplierBq> rfpSupplierBqList) {
		RfpEvent newEvent = new RfpEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFP);
		newEvent.setEventName(oldEvent.getEventName());
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());

		// Copy Event Details
		copyEventDetails(oldEvent, newEvent);
		// copy supp
		copySupplierDetails(oldEvent, newEvent, invitedSupp);
		// copy Bq

		copyBqDetails(oldEvent, newEvent, rfpSupplierBqList, (invitedSupp != null && invitedSupp.length > 0));

		return newEvent;
	}

	public RfqEvent createNextRfqEvent(RfpEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RfpSupplierBq> rfpSupplierBqList) {
		RfqEvent newEvent = new RfqEvent();
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFP);
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setAddBillOfQuantity(oldEvent.getAddBillOfQuantity());
		newEvent.setCreatedDate(new Date());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfqEventContact>());
			for (RfpEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfq());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfqEventCorrespondenceAddress>());
			for (RfpEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
			for (RfpEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfq());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRfq());
				}
			}
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getEventBqs())) {
			newEvent.setEventBqs(new ArrayList<RfqEventBq>());
			for (RfpEventBq bq : oldEvent.getEventBqs()) {

				RfqEventBq newBq = bq.copyForRfq(invitedSuppliersOnly);
				newEvent.getEventBqs().add(newBq);

				// Now copy Supplier Bq over to the new event
				if (invitedSuppliersOnly) {
					newEvent.setRfqSupplierBqs(new ArrayList<RfqSupplierBq>());
					for (RfpSupplierBq supBq : rfpSupplierBqList) {
						newEvent.getRfqSupplierBqs().add(supBq.createForRfq(newEvent, newBq));
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

	public RfiEvent createNextRfiEvent(RfpEvent oldEvent, User loggedInUser, String[] invitedSupp) {
		RfiEvent newEvent = new RfiEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(Boolean.FALSE);
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFP);
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());

		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setCreatedDate(new Date());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfiEventContact>());
			for (RfpEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfi());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfiEventCorrespondenceAddress>());
			for (RfpEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
		newEvent.setGroupCode(oldEvent.getGroupCode());

		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RfiEventSupplier>());
			for (RfpEventSupplier supp : oldEvent.getSuppliers()) {
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

	public RfaEvent createNextRfaEvent(RfpEvent oldEvent, AuctionType auctionType, String bqId, User loggedInUser, String[] invitedSupp, List<RfpSupplierBq> rfpSupplierBqList) {
		RfaEvent newEvent = new RfaEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setPreviousEventType(RfxTypes.RFP);
		newEvent.setAuctionType(auctionType);
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());

		newEvent.setCreatedDate(new Date());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfaEventContact>());
			for (RfpEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfa());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfaEventCorrespondenceAddress>());
			for (RfpEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
				newEvent.getEventCorrespondenceAddress().add(correspondenceAddress.copyForRfa());
			}
		}
		newEvent.setAddBillOfQuantity(oldEvent.getAddBillOfQuantity());
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
			newEvent.setSuppliers(new ArrayList<RfaEventSupplier>());
			for (RfpEventSupplier supp : oldEvent.getSuppliers()) {
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
			for (RfpEventBq bq : oldEvent.getEventBqs()) {
				if (bq.getId().equals(bqId)) {

					RfaEventBq newBq = bq.copyForRfa(invitedSuppliersOnly);
					newEvent.getEventBqs().add(newBq);

					// Now copy Supplier Bq over to the new event
					if (invitedSuppliersOnly) {
						newEvent.setRfaSupplierBqs(new ArrayList<RfaSupplierBq>());
						for (RfpSupplierBq supBq : rfpSupplierBqList) {
							newEvent.getRfaSupplierBqs().add(supBq.createForRfa(newEvent, newBq));
						}
						newBq.setId(null);
						for (RfaBqItem bqItem : newBq.getBqItems()) {
							bqItem.setId(null); // clear the original ID that was used temporarily
						}
					}

				}
			}
		}
		return newEvent;
	}

	public RftEvent createNextRftEvent(RfpEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RfpSupplierBq> rfpSupplierBqList) {
		RftEvent newEvent = new RftEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFA);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
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
		newEvent.setAddBillOfQuantity(oldEvent.getAddBillOfQuantity());
		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RftEventContact>());
			for (RfpEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRft());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RftEventCorrespondenceAddress>());
			for (RfpEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
		newEvent.setDisableTotalAmount(oldEvent.getDisableTotalAmount());
		newEvent.setProcurementCategories(oldEvent.getProcurementCategories());
		newEvent.setProcurementMethod(oldEvent.getProcurementMethod());
		newEvent.setGroupCode(oldEvent.getGroupCode());

		// copy suppliers
		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RftEventSupplier>());
			for (RfpEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRft());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRft());
				}
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventBqs())) {
			newEvent.setEventBqs(new ArrayList<RftEventBq>());
			for (RfpEventBq bq : oldEvent.getEventBqs()) {
				// Only required if selective BQ needs to be copied over
				RftEventBq newBq = bq.copyForRft(invitedSuppliersOnly);
				newEvent.getEventBqs().add(newBq);
				// copy supplier Bq Over new Event
				if (invitedSuppliersOnly) {
					newEvent.setRftSupplierBqs(new ArrayList<RftSupplierBq>());
					for (RfpSupplierBq supBq : rfpSupplierBqList) {
						newEvent.getRftSupplierBqs().add(supBq.createForRft(newEvent, newBq, bq));
					}
					newBq.setId(null);
					for (RftBqItem bqItem : newBq.getBqItems()) {
						bqItem.setId(null);
					}
				}
			}
		}
		return newEvent;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<RfpEventApproval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<RfpEventApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (approvals != null) {
				for (RfpEventApproval oldApproval : this.approvals) {
					for (RfpEventApproval newApproval : approvals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RfpApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RfpApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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

	public RfpEvent(String id, Date eventPublishDate, String eventName, Date eventEnd, String referanceNumber) {
		super(id, eventPublishDate, eventName, eventEnd, referanceNumber);
	}

	public RfpEvent(String id, String eventId, String eventName, String referenceNumber, BigDecimal fee, Currency feeCurrency, User user) {
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

	public RfpEvent(String eventId) {
		this.setId(eventId);
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
	public List<RfpComment> getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(List<RfpComment> comment) {
		this.comment = comment;
	}

	/**
	 * @return the reminder
	 */
	public List<RfpReminder> getReminder() {
		return reminder;
	}

	/**
	 * @param reminder the reminder to set
	 */
	public void setReminder(List<RfpReminder> reminder) {
		this.reminder = reminder;
	}

	/**
	 * @return the approvals
	 */
	public List<RfpEventApproval> getApprovals() {
		return approvals;
	}

	/**
	 * @return the suppliers
	 */
	public List<RfpEventSupplier> getSuppliers() {
		return suppliers;
	}

	/**
	 * @param suppliers the suppliers to set
	 */
	public void setSuppliers(List<RfpEventSupplier> suppliers) {
		this.suppliers = suppliers;
	}

	/**
	 * @return the documents
	 */
	public List<RfpEventDocument> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<RfpEventDocument> documents) {
		this.documents = documents;
	}

	/**
	 * @return the meetings
	 */
	public List<RfpEventMeeting> getMeetings() {
		return meetings;
	}

	/**
	 * @param meetings the meetings to set
	 */
	public void setMeetings(List<RfpEventMeeting> meetings) {
		this.meetings = meetings;
	}

	/**
	 * @return the eventContacts
	 */
	public List<RfpEventContact> getEventContacts() {
		return eventContacts;
	}

	/**
	 * @param eventContacts the eventContacts to set
	 */
	public void setEventContacts(List<RfpEventContact> eventContacts) {
		this.eventContacts = eventContacts;
	}

	/**
	 * @return the eventCorrespondenceAddress
	 */
	public List<RfpEventCorrespondenceAddress> getEventCorrespondenceAddress() {
		return eventCorrespondenceAddress;
	}

	/**
	 * @param eventCorrespondenceAddress the eventCorrespondenceAddress to set
	 */
	public void setEventCorrespondenceAddress(List<RfpEventCorrespondenceAddress> eventCorrespondenceAddress) {
		this.eventCorrespondenceAddress = eventCorrespondenceAddress;
	}

	/**
	 * @return the eventBqs
	 */
	public List<RfpEventBq> getEventBqs() {
		return eventBqs;
	}

	/**
	 * @param eventBqs the eventBqs to set
	 */
	public void setEventBqs(List<RfpEventBq> eventBqs) {
		this.eventBqs = eventBqs;
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
	 * @return the rfxEnvelop
	 */
	public List<RfpEnvelop> getRfxEnvelop() {
		return rfxEnvelop;
	}

	/**
	 * @param rfxEnvelop the rfxEnvelop to set
	 */
	public void setRfxEnvelop(List<RfpEnvelop> rfxEnvelop) {
		this.rfxEnvelop = rfxEnvelop;
	}

	/**
	 * @return the cqs
	 */
	public List<RfpCq> getCqs() {
		return cqs;
	}

	/**
	 * @param cqs the cqs to set
	 */
	public void setCqs(List<RfpCq> cqs) {
		this.cqs = cqs;
	}

	/**
	 * @return the rftReminder
	 */
	public List<RfpReminder> getRftReminder() {
		return reminder;
	}

	/**
	 * @param rftReminder the rftReminder to set
	 */
	public void setRftReminder(List<RfpReminder> rftReminder) {
		this.reminder = rftReminder;
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

	/*
	 * if (this.eventApprovers == null) { this.eventApprovers = new ArrayList<RfpApprover>(); } else {
	 * this.eventApprovers.clear(); } if (eventApprovers != null) { this.eventApprovers.addAll(eventApprovers); } }
	 */

	/**
	 * @return the teamMembers
	 */
	public List<RfpTeamMember> getTeamMembers() {
		return teamMembers;
	}

	/**
	 * @param teamMembers the teamMembers to set
	 */
	public void setTeamMembers(List<RfpTeamMember> teamMembers) {
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

	public Boolean getAddBillOfQuantity() {
		return addBillOfQuantity;
	}

	public void setAddBillOfQuantity(Boolean addBillOfQuantity) {
		this.addBillOfQuantity = addBillOfQuantity;
	}

	/**
	 * @return the unMaskedUsers
	 */
	public List<RfpUnMaskedUser> getUnMaskedUsers() {
		return unMaskedUsers;
	}

	/**
	 * @param unMaskedUsers the unMaskedUsers to set
	 */
	public void setUnMaskedUsers(List<RfpUnMaskedUser> unMaskedUsers) {
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
	public List<RfpEvaluationConclusionUser> getEvaluationConclusionUsers() {
		return evaluationConclusionUsers;
	}

	/**
	 * @param evaluationConclusionUsers the evaluationConclusionUsers to set
	 */
	public void setEvaluationConclusionUsers(List<RfpEvaluationConclusionUser> evaluationConclusionUsers) {
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
	public List<RfpEventSuspensionApproval> getSuspensionApprovals() {
		return suspensionApprovals;
	}

	/**
	 * @param suspensionApprovals the suspensionApprovals to set
	 */
	public void setSuspensionApprovals(List<RfpEventSuspensionApproval> suspensionApprovals) {
		if (this.suspensionApprovals == null) {
			this.suspensionApprovals = new ArrayList<RfpEventSuspensionApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (suspensionApprovals != null) {
				for (RfpEventSuspensionApproval oldApproval : this.suspensionApprovals) {
					for (RfpEventSuspensionApproval newApproval : suspensionApprovals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RfpSuspensionApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RfpSuspensionApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
	public List<RfpSuspensionComment> getSuspensionComment() {
		return suspensionComment;
	}

	/**
	 * @param suspensionComment the suspensionComment to set
	 */
	public void setSuspensionComment(List<RfpSuspensionComment> suspensionComment) {
		this.suspensionComment = suspensionComment;
	}
	
	/**
	 * @return the awardApprovals
	 */
	public List<RfpEventAwardApproval> get	() {
		return awardApprovals;
	}

	/**
	 * @return the awardComment
	 */
	public List<RfpAwardComment> getAwardComment() {
		return awardComment;
	}

	/**
	 * @param awardApprovals the awardApprovals to set
	 */
	public void setAwardApprovals(List<RfpEventAwardApproval> awardApprovals) {
		if (this.awardApprovals == null) {
			this.awardApprovals = new ArrayList<RfpEventAwardApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (awardApprovals != null) {
				for (RfpEventAwardApproval oldApproval : this.awardApprovals) {
					for (RfpEventAwardApproval newApproval : awardApprovals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RfpAwardApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RfpAwardApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
	public void setAwardComment(List<RfpAwardComment> awardComment) {
		this.awardComment = awardComment;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

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
		return "RfpEvent [toLogString()=" + super.toLogString() + "]";
	}

	/**
	 * @return the awardApprovals
	 */
	public List<RfpEventAwardApproval> getAwardApprovals() {
		return awardApprovals;
	}

	public List<RfpEventSor> getEventSors() {
		return eventSors;
	}

	public void setEventSors(List<RfpEventSor> eventSors) {
		this.eventSors = eventSors;
	}

	public List<RfpSupplierSor> getRfpSupplierSors() {
		return rfpSupplierSors;
	}

	public void setRfpSupplierSors(List<RfpSupplierSor> rfpSupplierSors) {
		this.rfpSupplierSors = rfpSupplierSors;
	}
}
