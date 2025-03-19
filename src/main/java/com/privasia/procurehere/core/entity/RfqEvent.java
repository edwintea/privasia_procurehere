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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AwardStatus;
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
@Table(name = "PROC_RFQ_EVENTS")
@SqlResultSetMappings({ @SqlResultSetMapping(name = "pendingPrResult", classes = { @ConstructorResult(targetClass = PendingEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventId"), @ColumnResult(name = "eventName"), @ColumnResult(name = "createdBy"), @ColumnResult(name = "createdDate"), @ColumnResult(name = "modifiedDate"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "grandTotal"), @ColumnResult(name = "unitName"), @ColumnResult(name = "prUserName"), @ColumnResult(name = "prDecimal"), @ColumnResult(name = "urgentPr", type = Boolean.class) }) }),

		@SqlResultSetMapping(name = "pendingRevisePoResult", classes = { @ConstructorResult(targetClass = PendingEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventId"), @ColumnResult(name = "eventName"), @ColumnResult(name = "createdBy"), @ColumnResult(name = "createdDate"), @ColumnResult(name = "modifiedDate"), @ColumnResult(name = "grandTotal"), @ColumnResult(name = "unitName"), @ColumnResult(name = "currency"), @ColumnResult(name = "prUserName"), @ColumnResult(name = "prDecimal") }) }) })

public class RfqEvent extends Event implements Serializable {

	private static final long serialVersionUID = -2880440183722120972L;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfqEventSupplier> suppliers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfqEventDocument> documents;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfqEventMeeting> meetings;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfqEventContact> eventContacts;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfqEventCorrespondenceAddress> eventCorrespondenceAddress;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfqEventBq> eventBqs;


	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfqEventSor> eventSors;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfqEnvelop> rfxEnvelop;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfqCq> cqs;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", orphanRemoval = false, cascade = CascadeType.ALL)
	@OrderBy("reminderDate")
	private List<RfqReminder> rftReminder;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfqUnMaskedUser> unMaskedUsers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfqEvaluationConclusionUser> evaluationConclusionUsers;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFQ_EVENT_VIEWERS", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private List<User> eventViewers;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFQ_EVENT_EDITORS", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private List<User> eventEditors;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfqTeamMember> teamMembers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RfqEventApproval> approvals;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFQ_AWARD_SUP", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "SUPPLIER_ID"))
	private List<Supplier> awardedSuppliers;

	@DecimalMax("9999999999.999999")
	@Column(name = "AWARDED_PRICE", precision = 22, scale = 6, nullable = true)
	private BigDecimal awardedPrice;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RfqComment> comment;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFQ_EVENT_INDUS_CAT", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "IND_CAT_ID"))
	private List<IndustryCategory> industryCategories;

	@Column(name = "ERP_AWARD_REF_ID", length = 500, nullable = true)
	@Size(min = 0, max = 2000)
	private String erpAwardRefId;

	@Column(name = "ERP_AWARD_RESPONSE", length = 500, nullable = true)
	@Size(min = 0, max = 2000)
	private String erpAwardResponse;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean uploadDocuments;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = true)
	@JoinColumn(name = "WINNING_SUPPLIER", foreignKey = @ForeignKey(name = "FK_RFQ_WINNING_SUPP"))
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

	@Column(name = "DISABLE_TOTAL_AMOUNT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean disableTotalAmount = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RfqEventSuspensionApproval> suspensionApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RfqSuspensionComment> suspensionComment;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RfqEventAwardApproval> awardApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RfqAwardComment> awardComment;
	
	@Column(name = "IS_ENABLE_AWARD_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableAwardApproval = Boolean.FALSE;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "AWARD_STATUS")
	private AwardStatus awardStatus;

	public Date getPushToPr() {
		return pushToPr;
	}

	public void setPushToPr(Date pushToPr) {
		this.pushToPr = pushToPr;
	}

	@Transient
	private List<RfqSupplierBq> rfqSupplierBqs;

	@Transient
	private List<RfqSupplierSor> rfqSupplierSors;

	public RfqEvent(String eventId, String eventName) {
		this.setEventId(eventId);
		this.setEventName(eventName);
	}

	/**
	 * @return the approvals
	 */
	public List<RfqEventApproval> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<RfqEventApproval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<RfqEventApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (approvals != null) {
				for (RfqEventApproval oldApproval : this.approvals) {
					for (RfqEventApproval newApproval : approvals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RfqApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RfqApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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

	public RfqEvent copyFrom(RfqEvent oldEvent) {
		RfqEvent newEvent = new RfqEvent();
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

		// SOR
		copySorDetails(oldEvent, newEvent, null, false);
		// Env
		copyEnvelopes(oldEvent, newEvent);

		// unmasking
		copyUnmaskingDetails(oldEvent, newEvent);

		// Evaluation Conclusion
		copyEvaluationConclusionUsers(oldEvent, newEvent);

		//copy doc
		copyDocument(oldEvent, newEvent);

		// ..
		return newEvent;
	}

	private void copyUnmaskingDetails(RfqEvent oldEvent, RfqEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getUnMaskedUsers())) {
			newEvent.setUnMaskedUsers((new ArrayList<RfqUnMaskedUser>()));
			for (RfqUnMaskedUser app : oldEvent.getUnMaskedUsers()) {
				RfqUnMaskedUser usr = app.copyFrom();
				usr.setEvent(newEvent);
				newEvent.getUnMaskedUsers().add(usr);
			}
		}
	}

	private void copyEvaluationConclusionUsers(RfqEvent oldEvent, RfqEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getEvaluationConclusionUsers())) {
			newEvent.setEvaluationConclusionUsers(new ArrayList<RfqEvaluationConclusionUser>());
			for (RfqEvaluationConclusionUser app : oldEvent.getEvaluationConclusionUsers()) {
				RfqEvaluationConclusionUser usr = app.copyFrom();
				usr.setEvent(newEvent);
				newEvent.getEvaluationConclusionUsers().add(usr);
			}
		}
	}

	private RfqEvent copyEventDetails(RfqEvent oldEvent, RfqEvent newEvent) {
		newEvent.setViewSupplerName(oldEvent.getViewSupplerName());
		newEvent.setUnMaskedUser(oldEvent.getUnMaskedUser());
		newEvent.setEnableEvaluationConclusionUsers(oldEvent.getEnableEvaluationConclusionUsers() == null ? Boolean.FALSE : oldEvent.getEnableEvaluationConclusionUsers());
		newEvent.setAllowToSuspendEvent(oldEvent.getAllowToSuspendEvent());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setScheduleOfRate(oldEvent.getScheduleOfRate());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
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
			newEvent.setEventContacts(new ArrayList<RfqEventContact>());
			for (RfqEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyFrom(newEvent));
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfqEventCorrespondenceAddress>());
			for (RfqEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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

	private void copyEnvelopes(RfqEvent oldEvent, RfqEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getRfxEnvelop())) {
			newEvent.setRfxEnvelop(new ArrayList<RfqEnvelop>());
			for (RfqEnvelop envelop : oldEvent.getRfxEnvelop()) {
				newEvent.getRfxEnvelop().add(envelop.copyFrom(newEvent));
			}
		}
	}

	private void copyCqDetails(RfqEvent oldEvent, RfqEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getCqs())) {
			newEvent.setCqs(new ArrayList<RfqCq>());
			for (RfqCq cq : oldEvent.getCqs()) {
				List<RfqCqItem> cqList = cq.getCqItems();
				for (RfqCqItem rfqCqItem : cqList) {
					if (CqType.DOCUMENT_DOWNLOAD_LINK == rfqCqItem.getCqType()) {
						newEvent.setUploadDocuments(true);
					}
				}
				newEvent.getCqs().add(cq.copyFrom());
			}
		}
	}

	private void copySupplierDetails(RfqEvent oldEvent, RfqEvent newEvent, String[] invitedSupp) {

		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RfqEventSupplier>());
			for (RfqEventSupplier supp : oldEvent.getSuppliers()) {

				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfq());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRfq());
				}
			}
		}
	}

	private void copyBqDetails(RfqEvent oldEvent, RfqEvent newEvent, List<RfqSupplierBq> rfqSupplierBqList, boolean isSupplierInvited) {
		if (CollectionUtil.isNotEmpty(oldEvent.getEventBqs())) {
			newEvent.setEventBqs(new ArrayList<RfqEventBq>());
			for (RfqEventBq bq : oldEvent.getEventBqs()) {
				RfqEventBq newBq = bq.copyForRfq(isSupplierInvited);
				newEvent.getEventBqs().add(newBq);

				// Now copy Supplier Bq over to the new event
				if (isSupplierInvited) {

					newEvent.setRfqSupplierBqs(new ArrayList<RfqSupplierBq>());
					for (RfqSupplierBq supBq : rfqSupplierBqList) {
						newEvent.getRfqSupplierBqs().add(supBq.createForRfq(newEvent, newBq));
					}

					newBq.setId(null); // clear the original ID that was used temporarily
					for (RfqBqItem bqItem : newBq.getBqItems()) {
						bqItem.setId(null); // clear the original ID that was used temporarily
					}
				}

			}
		}
	}


	private void copySorDetails(RfqEvent oldEvent, RfqEvent newEvent, List<RfqSupplierSor> rfqSupplierSorList, boolean isSupplierInvited) {

		if(oldEvent.getScheduleOfRate() == Boolean.TRUE) {
			newEvent.setScheduleOfRate(Boolean.TRUE);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getEventSors())) {
			newEvent.setEventSors(new ArrayList<RfqEventSor>());
			for (RfqEventSor bq : oldEvent.getEventSors()) {
				RfqEventSor newBq = bq.copyForRfq(isSupplierInvited);
				newEvent.getEventSors().add(newBq);

				if (isSupplierInvited) {

					newEvent.setRfqSupplierSors(new ArrayList<RfqSupplierSor>());
					for (RfqSupplierSor supBq : rfqSupplierSorList) {
						newEvent.getRfqSupplierSors().add(supBq.createForRfq(newEvent, newBq));
					}
					newBq.setId(null);
					for (RfqSorItem bqItem : newBq.getSorItems()) {
						bqItem.setId(null);
					}
				}

			}
		}
	}

	private void copyTeamMemberDetails(RfqEvent oldEvent, RfqEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getTeamMembers())) {
			newEvent.setTeamMembers(new ArrayList<RfqTeamMember>());
			for (RfqTeamMember tm : oldEvent.getTeamMembers()) {
				newEvent.getTeamMembers().add(tm.copyFrom());
			}
		}
	}

	private void copyApprovalDetails(RfqEvent oldEvent, RfqEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getApprovals())) {
			newEvent.setApprovals(new ArrayList<RfqEventApproval>());
			for (RfqEventApproval app : oldEvent.getApprovals()) {
				newEvent.getApprovals().add(app.copyFrom());
			}
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuspensionApprovals())) {
			newEvent.setSuspensionApprovals(new ArrayList<RfqEventSuspensionApproval>());
			for (RfqEventSuspensionApproval app : oldEvent.getSuspensionApprovals()) {
				newEvent.getSuspensionApprovals().add(app.copyFrom());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getAwardApprovals())) {
			newEvent.setAwardApprovals(new ArrayList<RfqEventAwardApproval>());
			for (RfqEventAwardApproval app : oldEvent.getAwardApprovals()) {
				newEvent.getAwardApprovals().add(app.copyFrom());
			}
		}

	}

	private void copyDocument(RfqEvent oldEvent, RfqEvent newEvent){
		if(CollectionUtil.isNotEmpty(oldEvent.getDocuments())){
			newEvent.setDocuments(new ArrayList<RfqEventDocument>());
			for(RfqEventDocument doc: oldEvent.getDocuments()){
				newEvent.getDocuments().add(doc.copyFrom(oldEvent));
			}
		}
	}

	public RfqEvent createNextRfqEvent(RfqEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RfqSupplierBq> rfqSupplierBqList) {
		RfqEvent newEvent = new RfqEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setPreviousEventType(RfxTypes.RFQ);
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());

		// Copy Event Details
		copyEventDetails(oldEvent, newEvent);
		// copy supp
		copySupplierDetails(oldEvent, newEvent, invitedSupp);
		// copy Bq
		copyBqDetails(oldEvent, newEvent, rfqSupplierBqList, (invitedSupp != null && invitedSupp.length > 0));

		return newEvent;
	}

	public RfpEvent createNextRfpEvent(RfqEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RfqSupplierBq> rfqSupplierBqList) {
		RfpEvent newEvent = new RfpEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setPreviousEventType(RfxTypes.RFQ);
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
			newEvent.setEventContacts(new ArrayList<RfpEventContact>());
			for (RfqEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfp());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfpEventCorrespondenceAddress>());
			for (RfqEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
			for (RfqEventSupplier supp : oldEvent.getSuppliers()) {
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
			for (RfqEventBq bq : oldEvent.getEventBqs()) {
				RfpEventBq newBq = bq.copyForRfp(invitedSuppliersOnly);
				newEvent.getEventBqs().add(newBq);
				// Now copy Supplier Bq over to the new event
				if (invitedSuppliersOnly) {
					newEvent.setRfpSupplierBqs(new ArrayList<RfpSupplierBq>());
					for (RfqSupplierBq supBq : rfqSupplierBqList) {
						newEvent.getRfpSupplierBqs().add(supBq.createForRfp(newEvent, newBq));
					}
					newBq.setId(null); // clear the original ID that was used temporarily
					for (RfpBqItem bqItem : newBq.getBqItems()) {
						bqItem.setId(null); // clear the original ID that was used temporarily
					}
				}
			}
		}
		return newEvent;
	}

	public RfiEvent createNextRfiEvent(RfqEvent oldEvent, User loggedInUser, String[] invitedSupp) {
		RfiEvent newEvent = new RfiEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(Boolean.FALSE);
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFQ);
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
			newEvent.setEventContacts(new ArrayList<RfiEventContact>());
			for (RfqEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfi());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfiEventCorrespondenceAddress>());
			for (RfqEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
			for (RfqEventSupplier supp : oldEvent.getSuppliers()) {
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

	public RfaEvent createNextRfaEvent(RfqEvent oldEvent, AuctionType auctionType, String bqId, User loggedInUser, String[] invitedSupp, List<RfqSupplierBq> rfqSupplierBqList) {
		RfaEvent newEvent = new RfaEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setPreviousEventType(RfxTypes.RFQ);
		newEvent.setAuctionType(auctionType);
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
			for (RfqEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfa());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfaEventCorrespondenceAddress>());
			for (RfqEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
			for (RfqEventSupplier supp : oldEvent.getSuppliers()) {
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
			for (RfqEventBq bq : oldEvent.getEventBqs()) {
				if (bq.getId().equals(bqId)) {

					RfaEventBq newBq = bq.copyForRfa(invitedSuppliersOnly);
					newEvent.getEventBqs().add(newBq);

					// Now copy Supplier Bq over to the new event
					if (invitedSuppliersOnly) {
						newEvent.setRfaSupplierBqs(new ArrayList<RfaSupplierBq>());
						for (RfqSupplierBq supBq : rfqSupplierBqList) {
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

	public RftEvent createNextRftEvent(RfqEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RfqSupplierBq> rfqSupplierBqList) {
		RftEvent newEvent = new RftEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFQ);
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
			for (RfqEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRft());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RftEventCorrespondenceAddress>());
			for (RfqEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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

		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RftEventSupplier>());
			for (RfqEventSupplier supp : oldEvent.getSuppliers()) {

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
			for (RfqEventBq bq : oldEvent.getEventBqs()) {
				RftEventBq newBq = bq.copyForRft(invitedSuppliersOnly);
				newEvent.getEventBqs().add(newBq);

				// Now copy Supplier Bq over to the new event
				if (invitedSuppliersOnly) {
					newEvent.setRftSupplierBqs(new ArrayList<RftSupplierBq>());
					for (RfqSupplierBq supBq : rfqSupplierBqList) {
						newEvent.getRftSupplierBqs().add(supBq.createForRft(newEvent, newBq));
					}

					newBq.setId(null); // clear the original ID that was used temporarily
					for (RftBqItem bqItem : newBq.getBqItems()) {
						bqItem.setId(null); // clear the original ID that was used temporarily
					}
				}
			}
		}
		return newEvent;
	}

	public RfqEvent() {
		super();
	}

	public RfqEvent(String eventId, BigDecimal budgetAmount, Date eventStart, Date eventEnd, Currency baseCurrency) {
		this.setEventId(eventId);
		this.setBudgetAmount(budgetAmount);
		this.setEventStart(eventStart);
		this.setEventEnd(eventEnd);
		this.setBaseCurrency(baseCurrency);

	}

	public RfqEvent(String id, Date eventPublishDate, String eventName, Date eventEnd, String referanceNumber) {
		super(id, eventPublishDate, eventName, eventEnd, referanceNumber);
	}

	public RfqEvent(String eventId) {

		this.setId(eventId);
	}

	public RfqEvent(String id, String eventId, String eventName, String referenceNumber, BigDecimal fee, Currency feeCurrency, User user) {
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
	 * @return the suppliers
	 */
	public List<RfqEventSupplier> getSuppliers() {
		return suppliers;
	}

	/**
	 * @param suppliers the suppliers to set
	 */
	public void setSuppliers(List<RfqEventSupplier> suppliers) {
		this.suppliers = suppliers;
	}

	/**
	 * @return the documents
	 */
	public List<RfqEventDocument> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<RfqEventDocument> documents) {
		this.documents = documents;
	}

	/**
	 * @return the meetings
	 */
	public List<RfqEventMeeting> getMeetings() {
		return meetings;
	}

	/**
	 * @param meetings the meetings to set
	 */
	public void setMeetings(List<RfqEventMeeting> meetings) {
		this.meetings = meetings;
	}

	/**
	 * @return the eventContacts
	 */
	public List<RfqEventContact> getEventContacts() {
		return eventContacts;
	}

	/**
	 * @param eventContacts the eventContacts to set
	 */
	public void setEventContacts(List<RfqEventContact> eventContacts) {
		this.eventContacts = eventContacts;
	}

	/**
	 * @return the eventCorrespondenceAddress
	 */
	public List<RfqEventCorrespondenceAddress> getEventCorrespondenceAddress() {
		return eventCorrespondenceAddress;
	}

	/**
	 * @param eventCorrespondenceAddress the eventCorrespondenceAddress to set
	 */
	public void setEventCorrespondenceAddress(List<RfqEventCorrespondenceAddress> eventCorrespondenceAddress) {
		this.eventCorrespondenceAddress = eventCorrespondenceAddress;
	}

	/**
	 * @return the eventBqs
	 */
	public List<RfqEventBq> getEventBqs() {
		return eventBqs;
	}

	/**
	 * @param eventBqs the eventBqs to set
	 */
	public void setEventBqs(List<RfqEventBq> eventBqs) {
		this.eventBqs = eventBqs;
	}

	/**
	 * @return the rfxEnvelop
	 */
	public List<RfqEnvelop> getRfxEnvelop() {
		return rfxEnvelop;
	}

	/**
	 * @param rfxEnvelop the rfxEnvelop to set
	 */
	public void setRfxEnvelop(List<RfqEnvelop> rfxEnvelop) {
		this.rfxEnvelop = rfxEnvelop;
	}

	/**
	 * @return the cqs
	 */
	public List<RfqCq> getCqs() {
		return cqs;
	}

	/**
	 * @param cqs the cqs to set
	 */
	public void setCqs(List<RfqCq> cqs) {
		this.cqs = cqs;
	}

	/**
	 * @return the rftReminder
	 */
	public List<RfqReminder> getRftReminder() {
		return rftReminder;
	}

	/**
	 * @param rftReminder the rftReminder to set
	 */
	public void setRftReminder(List<RfqReminder> rftReminder) {
		this.rftReminder = rftReminder;
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
	public List<RfqTeamMember> getTeamMembers() {
		return teamMembers;
	}

	/**
	 * @param teamMembers the teamMembers to set
	 */
	public void setTeamMembers(List<RfqTeamMember> teamMembers) {
		this.teamMembers = teamMembers;
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
	 * @return the comment
	 */
	public List<RfqComment> getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(List<RfqComment> comment) {
		this.comment = comment;
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

	public boolean isUploadDocuments() {
		return uploadDocuments;
	}

	public void setUploadDocuments(boolean uploadDocuments) {
		this.uploadDocuments = uploadDocuments;
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

	/**
	 * @return the rfqSupplierBqs
	 */
	public List<RfqSupplierBq> getRfqSupplierBqs() {
		return rfqSupplierBqs;
	}

	/**
	 * @param rfqSupplierBqs the rfqSupplierBqs to set
	 */
	public void setRfqSupplierBqs(List<RfqSupplierBq> rfqSupplierBqs) {
		this.rfqSupplierBqs = rfqSupplierBqs;
	}


	public List<RfqSupplierSor> getRfqSupplierSors() {
		return rfqSupplierSors;
	}

	public void setRfqSupplierSors(List<RfqSupplierSor> rfqSupplierSors) {
		this.rfqSupplierSors = rfqSupplierSors;
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
	public List<RfqUnMaskedUser> getUnMaskedUsers() {
		return unMaskedUsers;
	}

	/**
	 * @param unMaskedUsers the unMaskedUsers to set
	 */
	public void setUnMaskedUsers(List<RfqUnMaskedUser> unMaskedUsers) {
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
	public List<RfqEvaluationConclusionUser> getEvaluationConclusionUsers() {
		return evaluationConclusionUsers;
	}

	/**
	 * @param evaluationConclusionUsers the evaluationConclusionUsers to set
	 */
	public void setEvaluationConclusionUsers(List<RfqEvaluationConclusionUser> evaluationConclusionUsers) {
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
	public List<RfqEventSuspensionApproval> getSuspensionApprovals() {
		return suspensionApprovals;
	}

	/**
	 * @param suspensionApprovals the suspensionApprovals to set
	 */
	public void setSuspensionApprovals(List<RfqEventSuspensionApproval> suspensionApprovals) {
		if (this.suspensionApprovals == null) {
			this.suspensionApprovals = new ArrayList<RfqEventSuspensionApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (suspensionApprovals != null) {
				for (RfqEventSuspensionApproval oldApproval : this.suspensionApprovals) {
					for (RfqEventSuspensionApproval newApproval : suspensionApprovals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RfqSuspensionApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RfqSuspensionApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
	public List<RfqSuspensionComment> getSuspensionComment() {
		return suspensionComment;
	}

	/**
	 * @param suspensionComment the suspensionComment to set
	 */
	public void setSuspensionComment(List<RfqSuspensionComment> suspensionComment) {
		this.suspensionComment = suspensionComment;
	}

	/**
	 * @return the awardApprovals
	 */
	public List<RfqEventAwardApproval> getAwardApprovals() {
		return awardApprovals;
	}

	/**
	 * @return the awardComment
	 */
	public List<RfqAwardComment> getAwardComment() {
		return awardComment;
	}

	/**
	 * @param awardApprovals the awardApprovals to set
	 */
	public void setAwardApprovals(List<RfqEventAwardApproval> awardApprovals) {
		if (this.awardApprovals == null) {
			this.awardApprovals = new ArrayList<RfqEventAwardApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (awardApprovals != null) {
				for (RfqEventAwardApproval oldApproval : this.awardApprovals) {
					for (RfqEventAwardApproval newApproval : awardApprovals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RfqAwardApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RfqAwardApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
	public void setAwardComment(List<RfqAwardComment> awardComment) {
		this.awardComment = awardComment;
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

	public List<RfqEventSor> getEventSors() {
		return eventSors;
	}

	public void setEventSors(List<RfqEventSor> eventSors) {
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
		return "RfqEvent [toLogString()=" + super.toLogString() + "]";
	}



}
