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
import javax.persistence.Convert;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AwardStatus;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.DurationType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TimeExtensionType;
import com.privasia.procurehere.core.enums.converter.AuctionTypeCoverter;
import com.privasia.procurehere.core.pojo.ActiveEventPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author RT-Kapil
 */
@Entity
@Table(name = "PROC_RFA_EVENTS", indexes = { @Index(columnList = "TENANT_ID", name = "IDX_RFA_TENANT_ID") })
@SqlResultSetMapping(name = "activeEventResult", classes = { @ConstructorResult(targetClass = ActiveEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventName"), @ColumnResult(name = "createdBy"), @ColumnResult(name = "createdDate"), @ColumnResult(name = "type"), @ColumnResult(name = "eventStart"), @ColumnResult(name = "eventEnd"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "unitName"), @ColumnResult(name = "sysEventId"), @ColumnResult(name = "eventUser") }) })
public class RfaEvent extends Event implements Serializable {

	private static final long serialVersionUID = -3413479654437139568L;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfaEventSupplier> suppliers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfaEventDocument> documents;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfaEventMeeting> meetings;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfaEvent")
	private List<RfaEventContact> eventContacts;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfaEvent")
	private List<RfaEventCorrespondenceAddress> eventCorrespondenceAddress;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfaEventBq> eventBqs;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfaEnvelop> rfaEnvelop;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfaCq> cqs;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfaEvent", orphanRemoval = false)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@Where(clause = "START_REMINDER = 0")
	@OrderBy("reminderDate")
	private List<RfaReminder> rfaEndReminder;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfaUnMaskedUser> unMaskedUsers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfaEvaluationConclusionUser> evaluationConclusionUsers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfaEvent", orphanRemoval = false)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@Where(clause = "START_REMINDER = 1")
	@OrderBy("reminderDate")
	private List<RfaReminder> rfaStartReminder;

	// @NotNull(message = "{auction.type.empty}")
	@Convert(converter = AuctionTypeCoverter.class)
	@Column(name = "AUCTION_TYPE", length = 50)
	private AuctionType auctionType;

	@Enumerated(EnumType.STRING)
	@Column(name = "AUCTION_DURATION_TYPE")
	private DurationType auctionDurationType;

	@Enumerated(EnumType.STRING)
	@Column(name = "AUCTION_START_DELAY_TYPE")
	private DurationType auctionStartDelayType;

	@Column(name = "AUCTION_DURATION", length = 10)
	@Digits(integer = 10, fraction = 0)
	private Integer auctionDuration;

	@Column(name = "AUCTION_START_DELAY", length = 3)
	@Digits(integer = 3, fraction = 0)
	private Integer auctionStartDelay;

	@Column(name = "AUCTION_START_RELATIVE", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean auctionStartRelative = Boolean.FALSE;

	@Enumerated(EnumType.STRING)
	@Column(name = "TIME_EXTENSION_TYPE")
	private TimeExtensionType timeExtensionType;

	@Enumerated(EnumType.STRING)
	@Column(name = "TIMEEXTENSION_DURATION_TYPE")
	private DurationType timeExtensionDurationType;

	@Column(name = "TIMEEXTENSION_DURATION", length = 3)
	@Digits(integer = 3, fraction = 0)
	private Integer timeExtensionDuration;

	@Enumerated(EnumType.STRING)
	@Column(name = "EXTN_LEADING_BID_TYPE")
	private DurationType timeExtensionLeadingBidType;

	@Column(name = "EXTN_LEADING_BID_VALUE", length = 3)
	@Digits(integer = 3, fraction = 0)
	private Integer timeExtensionLeadingBidValue;

	@Column(name = "EXTENSION_COUNT", length = 3)
	@Digits(integer = 3, fraction = 0)
	private Integer extensionCount;

	@Column(name = "BIDDER_DISQUALIFY", length = 3)
	@Digits(integer = 3, fraction = 0)
	private Integer bidderDisqualify;

	@Column(name = "AUTO_DISQUALIFY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean autoDisqualify = Boolean.FALSE;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PREVIOUS_AUCTION", foreignKey = @ForeignKey(name = "FK_PREVIOUS_AUCTION"))
	private RfaEvent previousAuction;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFA_EVENT_VIEWERS", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private List<User> eventViewers;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFA_EVENT_EDITORS", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private List<User> eventEditors;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<RfaTeamMember> teamMembers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RfaComment> comment;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent")
	private List<RfaEventSor> eventSors;

	@Transient
	private Date eventEndTime;

	@Transient
	private Date eventStartTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "AUCTION_COMPLETE_TIME")
	private Date auctionComplitationTime;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = true)
	@JoinColumn(name = "WINNING_SUPPLIER", foreignKey = @ForeignKey(name = "FK_AUCTION_WINNING_SUPP"))
	private Supplier winningSupplier;

	@Column(name = "WINNING_PRICE", precision = 22, scale = 6)
	private BigDecimal winningPrice;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "AUCTION_RESUME_TIME")
	private Date auctionResumeDateTime;

	@Transient
	private Date auctionResumeTime;

	@Column(name = "AUCTION_SUSPEND_AMOUNT", precision = 22, scale = 6)
	private BigDecimal auctionSuspandAmount;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RfaEventApproval> approvals;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFA_AWARD_SUP", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "SUPPLIER_ID"))
	private List<Supplier> awardedSuppliers;

	@Column(name = "AWARDED_PRICE", precision = 22, scale = 4, nullable = true)
	@DecimalMax("9999999999.999999")
	private BigDecimal awardedPrice;

	@Column(name = "TOTAL_EXTENSION", length = 4)
	@Digits(integer = 4, fraction = 0)
	private Integer totalExtensions = 0;

	// @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "PROC_RFA_EVENT_INDUS_CAT", joinColumns = @JoinColumn(name = "EVENT_ID"), inverseJoinColumns = @JoinColumn(name = "IND_CAT_ID"))
	private List<IndustryCategory> industryCategories;

	@Column(name = "ERP_AWARD_REF_ID", length = 500, nullable = true)
	@Size(min = 0, max = 500)
	private String erpAwardRefId;

	@Column(name = "ERP_AWARD_RESPONSE", length = 2000, nullable = true)
	@Size(min = 0, max = 2000)
	private String erpAwardResponse;

	@Column(name = "ALLOW_VIEW_AUCTION_HALL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean viewAuctionHall = Boolean.FALSE;

	@Column(name = "PR_PUSH_DATE", nullable = true)
	private Date pushToPr;

	@Column(name = "ALLOW_REVERT_LAST_BID", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean revertLastBid = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "REVERT_BID_BY_USER", nullable = true, foreignKey = @ForeignKey(name = "FK_REVERT_BID_BY_USER"))
	private User revertBidUser;

	@Column(name = "ADD_BILL_OF_QUANTITY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean addBillOfQuantity = Boolean.TRUE;

	// PH-1701
	@Column(name = "DISABLE_TOTAL_AMOUNT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean disableTotalAmount = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RfaEventSuspensionApproval> suspensionApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "event", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<RfaEventAwardApproval> awardApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RfaSuspensionComment> suspensionComment;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rfxEvent", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RfaAwardComment> awardComment;

	@Column(name = "IS_ENABLE_AWARD_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableAwardApproval = Boolean.FALSE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean uploadDocuments;

	@Transient
	private List<RfaSupplierBq> rfaSupplierBqs;

	@Transient
	private List<RfaSupplierSor> rfaSupplierSors;

	@Enumerated(EnumType.STRING)
	@Column(name = "AWARD_STATUS")
	private AwardStatus awardStatus;

	@Column(name = "ADD_SCHEDULE_OF_RATE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean addScheduleOfRate = Boolean.TRUE;

	private void copyEventDetails(RfaEvent oldEvent, RfaEvent newEvent) {
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setScheduleOfRate(oldEvent.getScheduleOfRate());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setRevertBidUser(oldEvent.getRevertBidUser());
		newEvent.setAddBillOfQuantity(oldEvent.getAddBillOfQuantity());
		newEvent.setRevertLastBid(oldEvent.getRevertLastBid());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());

		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfaEventContact>());
			for (RfaEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfa());
			}
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfaEventCorrespondenceAddress>());
			for (RfaEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
		newEvent.setPaymentTerm(oldEvent.getPaymentTerm());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setSubmissionValidityDays(oldEvent.getSubmissionValidityDays());
		newEvent.setTenantId(oldEvent.getTenantId());
		newEvent.setDepositCurrency(oldEvent.getDepositCurrency());
		newEvent.setDeposit(oldEvent.getDeposit());
		newEvent.setEnableApprovalReminder(oldEvent.getEnableApprovalReminder());
		newEvent.setReminderAfterHour(oldEvent.getReminderAfterHour());
		newEvent.setReminderCount(oldEvent.getReminderCount());
		newEvent.setNotifyEventOwner(oldEvent.getNotifyEventOwner());
		newEvent.setDisableTotalAmount(oldEvent.getDisableTotalAmount());

		newEvent.setProcurementCategories(oldEvent.getProcurementCategories());
		newEvent.setProcurementMethod(oldEvent.getProcurementMethod());
		newEvent.setGroupCode(oldEvent.getGroupCode());
		newEvent.setEnableAwardApproval(oldEvent.getEnableAwardApproval());

	}

	/*
	 * public RfaEvent createNextRfaEvent(RfaEvent oldEvent, AuctionType auctionType, String bqId, User loggedInUser,
	 * boolean copySuppliers, List<RfaSupplierBq> rfaSupplierBqs) { RfaEvent newEvent = new RfaEvent();
	 * newEvent.setStatus(EventStatus.DRAFT); newEvent.setPreviousEventId(oldEvent.getId());
	 * newEvent.setPreviousEventType(RfxTypes.RFA); newEvent.setAuctionType(auctionType); // copy Delivery Address
	 * newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress()); // copy Temlate //
	 * newEvent.setTemplate(oldEvent.getTemplate()); newEvent.setConcludeBy(loggedInUser); newEvent.setConcludeDate(new
	 * Date()); newEvent.setEventName(oldEvent.getEventName()); // Copy Event Details copyEventDetails(oldEvent,
	 * newEvent); // copy supp if (copySuppliers) { copySupplierDetails(oldEvent, newEvent, null); } // copy Bq
	 * copyBqDetails(oldEvent, newEvent, bqId, null); return newEvent; }
	 */

	public RfaEvent createNextRfaEvent(RfaEvent oldEvent, AuctionType auctionType, String bqId, User loggedInUser, String[] invitedSupp, List<RfaSupplierBq> rfaSupplierBqs) {
		RfaEvent newEvent = new RfaEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFA);
		newEvent.setAuctionType(auctionType);
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setEventName(oldEvent.getEventName());
		// Copy Event Details
		copyEventDetails(oldEvent, newEvent);
		// copy supp
		copySupplierDetails(oldEvent, newEvent, invitedSupp);
		// copy Bq
		copyBqDetailsAndSupplierBq(oldEvent, newEvent, bqId, rfaSupplierBqs, invitedSupp);

		return newEvent;
	}

	private void copyCqDetails(RfaEvent oldEvent, RfaEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getCqs())) {
			newEvent.setCqs(new ArrayList<RfaCq>());
			for (RfaCq cq : oldEvent.getCqs()) {
				List<RfaCqItem> cqList = cq.getCqItems();
				for (RfaCqItem rfaCqItem : cqList) {
					if (CqType.DOCUMENT_DOWNLOAD_LINK == rfaCqItem.getCqType()) {
						newEvent.setUploadDocuments(true);
					}
				}
				newEvent.getCqs().add(cq.copyFrom());
			}
		}
	}

	private void copySupplierDetails(RfaEvent oldEvent, RfaEvent newEvent, String[] invitedSupp) {

		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RfaEventSupplier>());
			for (RfaEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfa());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRfa());
				}
			}
		}
	}

	private void copyBqDetailsAndSupplierBq(RfaEvent oldEvent, RfaEvent newEvent, String bqId, List<RfaSupplierBq> rfaSupplierBqs, String[] invitedSupp) {
		if (CollectionUtil.isNotEmpty(oldEvent.getEventBqs())) {

			boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);

			newEvent.setEventBqs(new ArrayList<RfaEventBq>());
			for (RfaEventBq bq : oldEvent.getEventBqs()) {
				if (bq.getId().equals(bqId)) {

					RfaEventBq newBq = bq.copyForRfa(invitedSuppliersOnly);
					newEvent.getEventBqs().add(newBq);

					// Now copy Supplier Bq over to the new event
					if (invitedSuppliersOnly) {
						newEvent.setRfaSupplierBqs(new ArrayList<RfaSupplierBq>());
						for (RfaSupplierBq supBq : rfaSupplierBqs) {
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
	}

	private void copyBq(RfaEvent oldEvent, RfaEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getEventBqs())) {
			newEvent.setEventBqs(new ArrayList<RfaEventBq>());
			for (RfaEventBq bq : oldEvent.getEventBqs()) {
				newEvent.getEventBqs().add(bq.copyForRfa(false));
			}
		}
	}

	private void copyDocument(RfaEvent oldEvent, RfaEvent newEvent){
		if(CollectionUtil.isNotEmpty(oldEvent.getDocuments())){
			newEvent.setDocuments(new ArrayList<RfaEventDocument>());
			for(RfaEventDocument doc: oldEvent.getDocuments()){
				newEvent.getDocuments().add(doc.copyFrom(oldEvent));
			}
		}
	}

	public RfiEvent createNextRfiEvent(RfaEvent oldEvent, User loggedInUser, String[] invitedSupp) {
		RfiEvent newEvent = new RfiEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFA);
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Temlate
		// newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBillOfQuantity(Boolean.FALSE);
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
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
			for (RfaEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfi());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfiEventCorrespondenceAddress>());
			for (RfaEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
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
			for (RfaEventSupplier supp : oldEvent.getSuppliers()) {
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

	public RfpEvent createNextRfpEvent(RfaEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RfaSupplierBq> rfaSupplierBqs, String bqId) {
		RfpEvent newEvent = new RfpEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFA);
		// copy Delivery Address
		newEvent.setDeliveryAddress(oldEvent.getDeliveryAddress());
		// copy Template
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		// newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
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
			newEvent.setEventContacts(new ArrayList<RfpEventContact>());
			for (RfaEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfp());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfpEventCorrespondenceAddress>());
			for (RfaEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
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
		newEvent.setEnableAwardApproval(oldEvent.getEnableAwardApproval());

		// copy suppliers
		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RfpEventSupplier>());
			for (RfaEventSupplier supp : oldEvent.getSuppliers()) {
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
			for (RfaEventBq bq : oldEvent.getEventBqs()) {

				// Only required if selective BQ needs to be copied over
				RfpEventBq newBq = bq.copyForRfp(invitedSuppliersOnly);
				newEvent.getEventBqs().add(newBq);

				// copy supplier Bq Over new Event
				if (invitedSuppliersOnly) {

					newEvent.setRfpSupplierBqs(new ArrayList<RfpSupplierBq>());
					for (RfaSupplierBq supBq : rfaSupplierBqs) {
						newEvent.getRfpSupplierBqs().add(supBq.createForRfp(newEvent, newBq, bq));
					}
					newBq.setId(null);
					for (RfpBqItem bqItem : newBq.getBqItems()) {
						bqItem.setId(null);
					}
				}
			}
		}
		return newEvent;
	}

	public RfqEvent createNextRfqEvent(RfaEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RfaSupplierBq> rfaSupplierBqs) {
		RfqEvent newEvent = new RfqEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setPreviousEventId(oldEvent.getId());
		newEvent.setPreviousEventType(RfxTypes.RFA);
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
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
			newEvent.setEventContacts(new ArrayList<RfqEventContact>());
			for (RfaEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRfq());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfqEventCorrespondenceAddress>());
			for (RfaEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
				newEvent.getEventCorrespondenceAddress().add(correspondenceAddress.copyForRfq());
			}
		}
		newEvent.setEventDescription(oldEvent.getEventDescription());
		newEvent.setInternalRemarks(oldEvent.getInternalRemarks());
		newEvent.setEventOwner(oldEvent.getEventOwner());
		newEvent.setEventVisibility(oldEvent.getEventVisibility());
		newEvent.setEventVisibilityDates(oldEvent.getEventVisibilityDates());
		newEvent.setHistoricaAmount(oldEvent.getHistoricaAmount());
		newEvent.setEstimatedBudget(oldEvent.getEstimatedBudget());
		// newEvent.setIndustryCategory(oldEvent.getIndustryCategory());
		if (oldEvent.getIndustryCategories() == null) {
			oldEvent.setIndustryCategories(new ArrayList<>());
		}
		oldEvent.getIndustryCategories().add(oldEvent.getIndustryCategory());
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
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
		newEvent.setEnableAwardApproval(oldEvent.getEnableAwardApproval());

		// copy suppliers
		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RfqEventSupplier>());
			for (RfaEventSupplier supp : oldEvent.getSuppliers()) {
				if (invitedSuppliersOnly) {
					if (invitedSuppliersList.contains(supp.getSupplier().getId())) {
						newEvent.getSuppliers().add(supp.copyForRfq());
					}
				} else {
					newEvent.getSuppliers().add(supp.copyForRfq());
				}
			}
		}

		// copy Bq's And Suppliers Bq's

		if (CollectionUtil.isNotEmpty(oldEvent.getEventBqs())) {
			newEvent.setEventBqs(new ArrayList<RfqEventBq>());
			for (RfaEventBq bq : oldEvent.getEventBqs()) {
				// Only required if selective BQ needs to be copied over
				RfqEventBq newBq = bq.copyForRfq(invitedSuppliersOnly);
				newEvent.getEventBqs().add(newBq);
				// copy supplier Bq Over new Event
				if (invitedSuppliersOnly) {
					newEvent.setRfqSupplierBqs(new ArrayList<RfqSupplierBq>());
					for (RfaSupplierBq supBq : rfaSupplierBqs) {
						newEvent.getRfqSupplierBqs().add(supBq.createForRfq(newEvent, newBq, bq));
					}
					newBq.setId(null);
					for (RfqBqItem bqItem : newBq.getBqItems()) {
						bqItem.setId(null);
					}
				}

			}
		}
		return newEvent;
	}

	public RftEvent createNextRftEvent(RfaEvent oldEvent, User loggedInUser, String[] invitedSupp, List<RfaSupplierBq> rfaSupplierBqs) {
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
			for (RfaEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyForRft());
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RftEventCorrespondenceAddress>());
			for (RfaEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
				newEvent.getEventCorrespondenceAddress().add(correspondenceAddress.copyForRft());
			}
		}
		newEvent.setEventDescription(oldEvent.getEventDescription());
		newEvent.setInternalRemarks(oldEvent.getInternalRemarks());
		newEvent.setEventOwner(oldEvent.getEventOwner());
		newEvent.setEventVisibility(oldEvent.getEventVisibility());
		newEvent.setEventVisibilityDates(oldEvent.getEventVisibilityDates());
		newEvent.setHistoricaAmount(oldEvent.getHistoricaAmount());
		newEvent.setEstimatedBudget(oldEvent.getEstimatedBudget());
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
		newEvent.setEnableAwardApproval(oldEvent.getEnableAwardApproval());

		// copy suppliers
		boolean invitedSuppliersOnly = (invitedSupp != null && invitedSupp.length > 0);
		List<String> invitedSuppliersList = null;
		if (invitedSuppliersOnly) {
			invitedSuppliersList = Arrays.asList(invitedSupp);
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuppliers())) {
			newEvent.setSuppliers(new ArrayList<RftEventSupplier>());
			for (RfaEventSupplier supp : oldEvent.getSuppliers()) {
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
			for (RfaEventBq bq : oldEvent.getEventBqs()) {
				// Only required if selective BQ needs to be copied over
				RftEventBq newBq = bq.copyForRft(invitedSuppliersOnly);
				newEvent.getEventBqs().add(newBq);
				// copy supplier Bq Over new Event

				if (invitedSuppliersOnly) {
					newEvent.setRftSupplierBqs(new ArrayList<RftSupplierBq>());
					for (RfaSupplierBq supBq : rfaSupplierBqs) {
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
	 * @param eventStart
	 * @param eventEnd
	 * @param status
	 */
	public RfaEvent(EventStatus status, Date eventStart, Date eventEnd) {
		super(status, eventStart, eventEnd);
	}

	/**
	 * @param id
	 * @param eventName
	 * @param status
	 */
	public RfaEvent(String id, String eventName, EventStatus status) {
		super(id, eventName, status);
	}

	public RfaEvent(String id, String eventId, String eventName, String referenceNumber, BigDecimal fee, Currency feeCurrency, User user) {
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

	public RfaEvent(String id, String eventName, String referanceNumber) {
		setId(id);
		setEventName(eventName);
		setReferanceNumber(referanceNumber);
	}

	public RfaEvent(String id, Date eventPublishDate, String eventName, Date eventEnd, String referanceNumber, Date auctionComplitationTime, Boolean billOfQuantity, Boolean questionnaires) {
		super(id, eventPublishDate, eventName, eventEnd, referanceNumber);
		this.auctionComplitationTime = auctionComplitationTime;
		setBillOfQuantity(billOfQuantity);
		setQuestionnaires(questionnaires);
	}

	/**
	 * Override this for Auction as the event is considered as Ongoing as soon as the status is ACTIVE.
	 */
	@Override
	public boolean getIsOnGoing() {
		if (getStatus() == EventStatus.ACTIVE) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param auctionType
	 * @param auctionDuration
	 * @param auctionStartRelative
	 * @param timeExtensionType
	 * @param timeExtensionDurationType
	 * @param timeExtensionDuration
	 * @param timeExtensionLeadingBidType
	 * @param timeExtensionLeadingBidValue
	 * @param extensionCount
	 * @param bidderDisqualify
	 * @param autoDisqualify
	 */
	public RfaEvent(String id, String tenantId, Date eventStart, Date eventEnd, EventStatus status, AuctionType auctionType, Integer auctionDuration, Boolean auctionStartRelative, TimeExtensionType timeExtensionType, DurationType timeExtensionDurationType, Integer timeExtensionDuration, DurationType timeExtensionLeadingBidType, Integer timeExtensionLeadingBidValue, Integer extensionCount, Integer bidderDisqualify, Boolean autoDisqualify, String createdBy, String buyer, String decimal, Integer totalExtensions) {
		setId(id);
		setStatus(status);
		setEventStart(eventStart);
		setEventEnd(eventEnd);
		setDecimal(decimal);
		setCreatedBy(new User());
		getCreatedBy().setId(createdBy);
		getCreatedBy().setBuyer(new Buyer());
		getCreatedBy().getBuyer().setId(buyer);
		this.setTenantId(tenantId);

		this.auctionType = auctionType;
		this.auctionDuration = auctionDuration;
		this.auctionStartRelative = auctionStartRelative;
		this.timeExtensionType = timeExtensionType;
		this.timeExtensionDurationType = timeExtensionDurationType;
		this.timeExtensionDuration = timeExtensionDuration;
		this.timeExtensionLeadingBidType = timeExtensionLeadingBidType;
		this.timeExtensionLeadingBidValue = timeExtensionLeadingBidValue;
		this.extensionCount = extensionCount;
		this.bidderDisqualify = bidderDisqualify;
		this.autoDisqualify = autoDisqualify;
		this.totalExtensions = totalExtensions;
	}

	/**
	 * @return the approvals
	 */
	public List<RfaEventApproval> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<RfaEventApproval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<RfaEventApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (approvals != null) {
				for (RfaEventApproval oldApproval : this.approvals) {
					for (RfaEventApproval newApproval : approvals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RfaApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RfaApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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

	public RfaEvent copyFrom(RfaEvent oldEvent) {
		RfaEvent newEvent = new RfaEvent();
		newEvent.setViewSupplerName(oldEvent.getViewSupplerName());
		newEvent.setUnMaskedUser(oldEvent.getUnMaskedUser());
		newEvent.setEnableEvaluationConclusionUsers(oldEvent.getEnableEvaluationConclusionUsers() == null ? Boolean.FALSE : oldEvent.getEnableEvaluationConclusionUsers());
		newEvent.setAllowToSuspendEvent(oldEvent.getAllowToSuspendEvent());
		newEvent.setBaseCurrency(oldEvent.getBaseCurrency());
		newEvent.setTemplate(oldEvent.getTemplate());
		newEvent.setEventName(oldEvent.getEventName());
		newEvent.setBudgetAmount(oldEvent.getBudgetAmount());
		newEvent.setViewAuctionHall(oldEvent.getViewAuctionHall());
		newEvent.setCostCenter(oldEvent.getCostCenter());
		newEvent.setBusinessUnit(oldEvent.getBusinessUnit());
		newEvent.setDecimal(oldEvent.getDecimal());
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());
		newEvent.setScheduleOfRate(oldEvent.getScheduleOfRate());
		newEvent.setMeetingReq(oldEvent.getMeetingReq());
		newEvent.setQuestionnaires(oldEvent.getQuestionnaires());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setAuctionType(oldEvent.getAuctionType());
		newEvent.setDocumentReq(oldEvent.getDocumentReq());
		newEvent.setCloseEnvelope(oldEvent.getCloseEnvelope());
		newEvent.setRevertLastBid(oldEvent.getRevertLastBid());
		newEvent.setRevertBidUser(oldEvent.getRevertBidUser());
		newEvent.setAddBillOfQuantity(oldEvent.getAddBillOfQuantity());
		newEvent.setUrgentEvent(oldEvent.getUrgentEvent());
		newEvent.setRfxEnvelopeReadOnly(oldEvent.getRfxEnvelopeReadOnly());
		newEvent.setRfxEnvelopeOpening(oldEvent.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(oldEvent.getRfxEnvOpeningAfter());
		newEvent.setMaximumSupplierRating(oldEvent.getMaximumSupplierRating());
		newEvent.setMinimumSupplierRating(oldEvent.getMinimumSupplierRating());
		newEvent.setEnableSuspensionApproval(oldEvent.getEnableSuspensionApproval());
		newEvent.setAllowDisqualifiedSupplierDownload(oldEvent.getAllowDisqualifiedSupplierDownload());

		if (CollectionUtil.isNotEmpty(oldEvent.getEventContacts())) {
			newEvent.setEventContacts(new ArrayList<RfaEventContact>());
			for (RfaEventContact contact : oldEvent.getEventContacts()) {
				newEvent.getEventContacts().add(contact.copyFrom(newEvent));
			}
		}
		if (CollectionUtil.isNotEmpty(oldEvent.getEventCorrespondenceAddress())) {
			newEvent.setEventCorrespondenceAddress(new ArrayList<RfaEventCorrespondenceAddress>());
			for (RfaEventCorrespondenceAddress correspondenceAddress : oldEvent.getEventCorrespondenceAddress()) {
				newEvent.getEventCorrespondenceAddress().add(correspondenceAddress.copyFrom(newEvent));
			}
		}
		if (getAuctionType() == AuctionType.REVERSE_ENGISH || getAuctionType() == AuctionType.FORWARD_ENGISH) {
			newEvent.setTimeExtensionType(oldEvent.getTimeExtensionType());
			newEvent.setTimeExtensionDurationType(oldEvent.getTimeExtensionDurationType());
			newEvent.setTimeExtensionDuration(oldEvent.getTimeExtensionDuration());
			newEvent.setTimeExtensionLeadingBidType(oldEvent.getTimeExtensionLeadingBidType());
			newEvent.setTimeExtensionLeadingBidValue(oldEvent.getTimeExtensionLeadingBidValue());
			newEvent.setBidderDisqualify(oldEvent.getBidderDisqualify());
			newEvent.setExtensionCount(oldEvent.getExtensionCount());
		}
		newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
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
		newEvent.setBillOfQuantity(oldEvent.getBillOfQuantity());

		newEvent.setEnableApprovalReminder(oldEvent.getEnableApprovalReminder());
		newEvent.setReminderAfterHour(oldEvent.getReminderAfterHour());
		newEvent.setReminderCount(oldEvent.getReminderCount());
		newEvent.setNotifyEventOwner(oldEvent.getNotifyEventOwner());
		newEvent.setGroupCode(oldEvent.getGroupCode());

		newEvent.setEnableEvaluationDeclaration(oldEvent.getEnableEvaluationDeclaration());
		if (oldEvent.getEvaluationProcessDeclaration() != null) {
			newEvent.setEvaluationProcessDeclaration(oldEvent.getEvaluationProcessDeclaration());
		}
		newEvent.setEnableSupplierDeclaration(oldEvent.getEnableSupplierDeclaration());
		if (oldEvent.getSupplierAcceptanceDeclaration() != null) {
			newEvent.setSupplierAcceptanceDeclaration(oldEvent.getSupplierAcceptanceDeclaration());
		}
		newEvent.setDisableTotalAmount(oldEvent.getDisableTotalAmount());

		// copy Envelope
		copyEnvelopes(oldEvent, newEvent);
		// copy Cq
		copyCqDetails(oldEvent, newEvent);
		// Copy Bq
		copyBq(oldEvent, newEvent);
		// copy Suppliers
		copySupplierDetails(oldEvent, newEvent, null);

		//Copy SOR
		copySorDetails(oldEvent, newEvent, null, false);

		// copy TeamMembers
		copyTeamMemberDetails(oldEvent, newEvent);
		// copy Approvals
		copyApprovalDetails(oldEvent, newEvent);

		copyUnmaskingDetails(oldEvent, newEvent);

		// Evaluation Conclusion
		copyEvaluationConclusionUsers(oldEvent, newEvent);

		// Copy Document
		copyDocument(oldEvent, newEvent);

		return newEvent;
	}

	private void copySorDetails(RfaEvent oldEvent, RfaEvent newEvent, List<RfaSupplierSor> rfpSupplierSorList, boolean isSupplierInvited) {

		if (CollectionUtil.isNotEmpty(oldEvent.getEventSors())) {
			newEvent.setEventSors(new ArrayList<RfaEventSor>());
			for (RfaEventSor bq : oldEvent.getEventSors()) {
				RfaEventSor newBq = bq.copyForRfq(isSupplierInvited);
				newEvent.getEventSors().add(newBq);

				if (isSupplierInvited) {

					newEvent.setRfaSupplierSors(new ArrayList<RfaSupplierSor>());
					for (RfaSupplierSor supBq : rfpSupplierSorList) {
						newEvent.getRfaSupplierSors().add(supBq.createForRfp(newEvent, newBq));
					}
					newBq.setId(null);
					for (RfaSorItem bqItem : newBq.getSorItems()) {
						bqItem.setId(null);
					}
				}

			}
		}
	}

	private void copyEnvelopes(RfaEvent copyEvent, RfaEvent event) {
		if (CollectionUtil.isNotEmpty(copyEvent.getRfaEnvelop())) {
			event.setRfaEnvelop(new ArrayList<RfaEnvelop>());
			for (RfaEnvelop envelop : copyEvent.getRfaEnvelop()) {
				event.getRfaEnvelop().add(envelop.copyFrom(event));
			}
		}
	}

	private void copyTeamMemberDetails(RfaEvent oldEvent, RfaEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getTeamMembers())) {
			newEvent.setTeamMembers(new ArrayList<RfaTeamMember>());
			for (RfaTeamMember tm : oldEvent.getTeamMembers()) {
				newEvent.getTeamMembers().add(tm.copyFrom());
			}
		}
	}

	private void copyApprovalDetails(RfaEvent oldEvent, RfaEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getApprovals())) {
			newEvent.setApprovals(new ArrayList<RfaEventApproval>());
			for (RfaEventApproval app : oldEvent.getApprovals()) {
				newEvent.getApprovals().add(app.copyFrom());
			}
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getSuspensionApprovals())) {
			newEvent.setSuspensionApprovals(new ArrayList<RfaEventSuspensionApproval>());
			for (RfaEventSuspensionApproval app : oldEvent.getSuspensionApprovals()) {
				newEvent.getSuspensionApprovals().add(app.copyFrom());
			}
		}

		if (CollectionUtil.isNotEmpty(oldEvent.getAwardApprovals())) {
			newEvent.setAwardApprovals(new ArrayList<RfaEventAwardApproval>());
			for (RfaEventAwardApproval app : oldEvent.getAwardApprovals()) {
				newEvent.getAwardApprovals().add(app.copyFrom());
			}
		}
	}

	private void copyUnmaskingDetails(RfaEvent oldEvent, RfaEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getUnMaskedUsers())) {
			newEvent.setUnMaskedUsers((new ArrayList<RfaUnMaskedUser>()));
			for (RfaUnMaskedUser app : oldEvent.getUnMaskedUsers()) {
				RfaUnMaskedUser usr = app.copyFrom();
				usr.setEvent(newEvent);
				newEvent.getUnMaskedUsers().add(usr);
			}
		}

	}

	private void copyEvaluationConclusionUsers(RfaEvent oldEvent, RfaEvent newEvent) {
		if (CollectionUtil.isNotEmpty(oldEvent.getEvaluationConclusionUsers())) {
			newEvent.setEvaluationConclusionUsers(new ArrayList<RfaEvaluationConclusionUser>());
			for (RfaEvaluationConclusionUser app : oldEvent.getEvaluationConclusionUsers()) {
				RfaEvaluationConclusionUser usr = app.copyFrom();
				usr.setEvent(newEvent);
				newEvent.getEvaluationConclusionUsers().add(usr);
			}
		}
	}

	public RfaEvent() {
		this.auctionStartRelative = Boolean.FALSE;
		this.autoDisqualify = Boolean.FALSE;
		this.totalExtensions = 0;
	}

	/**
	 * @param id
	 * @param budgetAmount
	 * @param auctionType
	 * @param eventStart
	 * @param eventEnd
	 */

	public RfaEvent(String eventId, BigDecimal budgetAmount, AuctionType auctionType, Date eventStart, Date eventEnd, Currency baseCurrency) {
		this.setEventId(eventId);
		this.setBudgetAmount(budgetAmount);
		this.auctionType = auctionType;
		this.setEventStart(eventStart);
		this.setEventEnd(eventEnd);
		this.setBaseCurrency(baseCurrency);

	}

	public RfaEvent(String eventId) {
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
	 * @return the suppliers
	 */
	public List<RfaEventSupplier> getSuppliers() {
		return suppliers;
	}

	/**
	 * @param suppliers the suppliers to set
	 */
	public void setSuppliers(List<RfaEventSupplier> suppliers) {
		this.suppliers = suppliers;
	}

	/**
	 * @return the documents
	 */
	public List<RfaEventDocument> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<RfaEventDocument> documents) {
		this.documents = documents;
	}

	/**
	 * @return the meetings
	 */
	public List<RfaEventMeeting> getMeetings() {
		return meetings;
	}

	/**
	 * @param meetings the meetings to set
	 */
	public void setMeetings(List<RfaEventMeeting> meetings) {
		this.meetings = meetings;
	}

	/**
	 * @return the eventContacts
	 */
	public List<RfaEventContact> getEventContacts() {
		return eventContacts;
	}

	/**
	 * @param eventContacts the eventContacts to set
	 */
	public void setEventContacts(List<RfaEventContact> eventContacts) {
		this.eventContacts = eventContacts;
	}

	/**
	 * @return the eventCorrespondenceAddress
	 */
	public List<RfaEventCorrespondenceAddress> getEventCorrespondenceAddress() {
		return eventCorrespondenceAddress;
	}

	/**
	 * @param eventCorrespondenceAddress the eventCorrespondenceAddress to set
	 */
	public void setEventCorrespondenceAddress(List<RfaEventCorrespondenceAddress> eventCorrespondenceAddress) {
		this.eventCorrespondenceAddress = eventCorrespondenceAddress;
	}

	/**
	 * @return the eventBqs
	 */
	public List<RfaEventBq> getEventBqs() {
		return eventBqs;
	}

	/**
	 * @param eventBqs the eventBqs to set
	 */
	public void setEventBqs(List<RfaEventBq> eventBqs) {
		this.eventBqs = eventBqs;
	}

	/**
	 * @return the rfaEnvelop
	 */
	public List<RfaEnvelop> getRfaEnvelop() {
		return rfaEnvelop;
	}

	/**
	 * @param rfaEnvelop the rfaEnvelop to set
	 */
	public void setRfaEnvelop(List<RfaEnvelop> rfaEnvelop) {
		this.rfaEnvelop = rfaEnvelop;
	}

	/**
	 * @return the cqs
	 */
	public List<RfaCq> getCqs() {
		return cqs;
	}

	/**
	 * @param cqs the cqs to set
	 */
	public void setCqs(List<RfaCq> cqs) {
		this.cqs = cqs;
	}

	/**
	 * @return the rfaEndReminder
	 */
	public List<RfaReminder> getRfaEndReminder() {
		return rfaEndReminder;
	}

	/**
	 * @param rfaEndReminder the rfaEndReminder to set
	 */
	public void setRfaEndReminder(List<RfaReminder> rfaEndReminder) {
		this.rfaEndReminder = rfaEndReminder;
	}

	/**
	 * @return the rfaStartReminder
	 */
	public List<RfaReminder> getRfaStartReminder() {
		return rfaStartReminder;
	}

	/**
	 * @param rfaStartReminder the rfaStartReminder to set
	 */
	public void setRfaStartReminder(List<RfaReminder> rfaStartReminder) {
		this.rfaStartReminder = rfaStartReminder;
	}

	/**
	 * @return the auctionType
	 */
	public AuctionType getAuctionType() {
		return auctionType;
	}

	/**
	 * @param auctionType the auctionType to set
	 */
	public void setAuctionType(AuctionType auctionType) {
		this.auctionType = auctionType;
	}

	/**
	 * @return the auctionDurationType
	 */
	public DurationType getAuctionDurationType() {
		return auctionDurationType;
	}

	/**
	 * @param auctionDurationType the auctionDurationType to set
	 */
	public void setAuctionDurationType(DurationType auctionDurationType) {
		this.auctionDurationType = auctionDurationType;
	}

	/**
	 * @return the auctionStartDelayType
	 */
	public DurationType getAuctionStartDelayType() {
		return auctionStartDelayType;
	}

	/**
	 * @param auctionStartDelayType the auctionStartDelayType to set
	 */
	public void setAuctionStartDelayType(DurationType auctionStartDelayType) {
		this.auctionStartDelayType = auctionStartDelayType;
	}

	/**
	 * @return the auctionStartDelay
	 */
	public Integer getAuctionStartDelay() {
		return auctionStartDelay;
	}

	/**
	 * @param auctionStartDelay the auctionStartDelay to set
	 */
	public void setAuctionStartDelay(Integer auctionStartDelay) {
		this.auctionStartDelay = auctionStartDelay;
	}

	/**
	 * @return the auctionDuration
	 */
	public Integer getAuctionDuration() {
		return auctionDuration;
	}

	/**
	 * @param auctionDuration the auctionDuration to set
	 */
	public void setAuctionDuration(Integer auctionDuration) {
		this.auctionDuration = auctionDuration;
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
	 * @return the auctionStartRelative
	 */
	public Boolean getAuctionStartRelative() {
		return auctionStartRelative;
	}

	/**
	 * @param auctionStartRelative the auctionStartRelative to set
	 */
	public void setAuctionStartRelative(Boolean auctionStartRelative) {
		this.auctionStartRelative = auctionStartRelative;
	}

	/**
	 * @return the timeExtensionType
	 */
	public TimeExtensionType getTimeExtensionType() {
		return timeExtensionType;
	}

	/**
	 * @param timeExtensionType the timeExtensionType to set
	 */
	public void setTimeExtensionType(TimeExtensionType timeExtensionType) {
		this.timeExtensionType = timeExtensionType;
	}

	/**
	 * @return the timeExtensionDurationType
	 */
	public DurationType getTimeExtensionDurationType() {
		return timeExtensionDurationType;
	}

	/**
	 * @param timeExtensionDurationType the timeExtensionDurationType to set
	 */
	public void setTimeExtensionDurationType(DurationType timeExtensionDurationType) {
		this.timeExtensionDurationType = timeExtensionDurationType;
	}

	/**
	 * @return the timeExtensionDuration
	 */
	public Integer getTimeExtensionDuration() {
		return timeExtensionDuration;
	}

	/**
	 * @param timeExtensionDuration the timeExtensionDuration to set
	 */
	public void setTimeExtensionDuration(Integer timeExtensionDuration) {
		this.timeExtensionDuration = timeExtensionDuration;
	}

	/**
	 * @return the timeExtensionLeadingBidType
	 */
	public DurationType getTimeExtensionLeadingBidType() {
		return timeExtensionLeadingBidType;
	}

	/**
	 * @param timeExtensionLeadingBidType the timeExtensionLeadingBidType to set
	 */
	public void setTimeExtensionLeadingBidType(DurationType timeExtensionLeadingBidType) {
		this.timeExtensionLeadingBidType = timeExtensionLeadingBidType;
	}

	/**
	 * @return the timeExtensionLeadingBidValue
	 */
	public Integer getTimeExtensionLeadingBidValue() {
		return timeExtensionLeadingBidValue;
	}

	/**
	 * @param timeExtensionLeadingBidValue the timeExtensionLeadingBidValue to set
	 */
	public void setTimeExtensionLeadingBidValue(Integer timeExtensionLeadingBidValue) {
		this.timeExtensionLeadingBidValue = timeExtensionLeadingBidValue;
	}

	/**
	 * @return the extensionCount
	 */
	public Integer getExtensionCount() {
		return extensionCount;
	}

	/**
	 * @param extensionCount the extensionCount to set
	 */
	public void setExtensionCount(Integer extensionCount) {
		this.extensionCount = extensionCount;
	}

	/**
	 * @return the bidderDisqualify
	 */
	public Integer getBidderDisqualify() {
		return bidderDisqualify;
	}

	/**
	 * @param bidderDisqualify the bidderDisqualify to set
	 */
	public void setBidderDisqualify(Integer bidderDisqualify) {
		this.bidderDisqualify = bidderDisqualify;
	}

	/**
	 * @return the autoDisqualify
	 */
	public Boolean getAutoDisqualify() {
		return autoDisqualify;
	}

	/**
	 * @param autoDisqualify the autoDisqualify to set
	 */
	public void setAutoDisqualify(Boolean autoDisqualify) {
		this.autoDisqualify = autoDisqualify;
	}

	/**
	 * @return the previousAuction
	 */
	public RfaEvent getPreviousAuction() {
		return previousAuction;
	}

	/**
	 * @param previousAuction the previousAuction to set
	 */
	public void setPreviousAuction(RfaEvent previousAuction) {
		this.previousAuction = previousAuction;
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
	public List<RfaTeamMember> getTeamMembers() {
		return teamMembers;
	}

	/**
	 * @param teamMembers the teamMembers to set
	 */
	public void setTeamMembers(List<RfaTeamMember> teamMembers) {
		this.teamMembers = teamMembers;
	}

	/**
	 * @return the auctionComplitationTime
	 */
	public Date getAuctionComplitationTime() {
		return auctionComplitationTime;
	}

	/**
	 * @param auctionComplitationTime the auctionComplitationTime to set
	 */
	public void setAuctionComplitationTime(Date auctionComplitationTime) {
		this.auctionComplitationTime = auctionComplitationTime;
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
	 * @return the auctionResumeDateTime
	 */
	public Date getAuctionResumeDateTime() {
		return auctionResumeDateTime;
	}

	/**
	 * @param auctionResumeDateTime the auctionResumeDateTime to set
	 */
	public void setAuctionResumeDateTime(Date auctionResumeDateTime) {
		this.auctionResumeDateTime = auctionResumeDateTime;
	}

	/**
	 * @return the auctionResumeTime
	 */
	public Date getAuctionResumeTime() {
		return auctionResumeTime;
	}

	/**
	 * @param auctionResumeTime the auctionResumeTime to set
	 */
	public void setAuctionResumeTime(Date auctionResumeTime) {
		this.auctionResumeTime = auctionResumeTime;
	}

	/**
	 * @return the auctionSuspandAmount
	 */
	public BigDecimal getAuctionSuspandAmount() {
		return auctionSuspandAmount;
	}

	/**
	 * @param auctionSuspandAmount the auctionSuspandAmount to set
	 */
	public void setAuctionSuspandAmount(BigDecimal auctionSuspandAmount) {
		this.auctionSuspandAmount = auctionSuspandAmount;
	}

	/**
	 * @return the eventEndTime
	 */
	public Date getEventEndTime() {
		return eventEndTime;
	}

	/**
	 * @param eventEndTime the eventEndTime to set
	 */
	public void setEventEndTime(Date eventEndTime) {
		this.eventEndTime = eventEndTime;
	}

	/**
	 * @return the eventStartTime
	 */
	public Date getEventStartTime() {
		return eventStartTime;
	}

	/**
	 * @param eventStartTime the eventStartTime to set
	 */
	public void setEventStartTime(Date eventStartTime) {
		this.eventStartTime = eventStartTime;
	}

	/**
	 * @return the comment
	 */
	public List<RfaComment> getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(List<RfaComment> comment) {
		this.comment = comment;
	}

	/**
	 * @return the totalExtensions
	 */
	public Integer getTotalExtensions() {
		return totalExtensions;
	}

	/**
	 * @param totalExtensions the totalExtensions to set
	 */
	public void setTotalExtensions(Integer totalExtensions) {
		this.totalExtensions = totalExtensions;
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

	/**
	 * @return the viewAuctionHall
	 */
	public Boolean getViewAuctionHall() {
		return viewAuctionHall;
	}

	/**
	 * @param viewAuctionHall the viewAuctionHall to set
	 */
	public void setViewAuctionHall(Boolean viewAuctionHall) {
		this.viewAuctionHall = viewAuctionHall;
	}

	public boolean isUploadDocuments() {
		return uploadDocuments;
	}

	public void setUploadDocuments(boolean uploadDocuments) {
		this.uploadDocuments = uploadDocuments;
	}

	/**
	 * @return the rfaSupplierBqs
	 */
	public List<RfaSupplierBq> getRfaSupplierBqs() {
		return rfaSupplierBqs;
	}

	/**
	 * @param rfaSupplierBqs the rfaSupplierBqs to set
	 */
	public void setRfaSupplierBqs(List<RfaSupplierBq> rfaSupplierBqs) {
		this.rfaSupplierBqs = rfaSupplierBqs;
	}

	public Date getPushToPr() {
		return pushToPr;
	}

	public void setPushToPr(Date pushToPr) {
		this.pushToPr = pushToPr;
	}

	public Boolean getRevertLastBid() {
		return revertLastBid;
	}

	public void setRevertLastBid(Boolean revertLastBid) {
		this.revertLastBid = revertLastBid;
	}

	public User getRevertBidUser() {
		return revertBidUser;
	}

	public void setRevertBidUser(User revertBidUser) {
		this.revertBidUser = revertBidUser;
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
	public List<RfaUnMaskedUser> getUnMaskedUsers() {
		return unMaskedUsers;
	}

	/**
	 * @param unMaskedUsers the unMaskedUsers to set
	 */
	public void setUnMaskedUsers(List<RfaUnMaskedUser> unMaskedUsers) {
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
	public List<RfaEvaluationConclusionUser> getEvaluationConclusionUsers() {
		return evaluationConclusionUsers;
	}

	/**
	 * @param evaluationConclusionUsers the evaluationConclusionUsers to set
	 */
	public void setEvaluationConclusionUsers(List<RfaEvaluationConclusionUser> evaluationConclusionUsers) {
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
	public List<RfaEventSuspensionApproval> getSuspensionApprovals() {
		return suspensionApprovals;
	}

	/**
	 * @param suspensionApprovals the suspensionApprovals to set
	 */
	public void setSuspensionApprovals(List<RfaEventSuspensionApproval> suspensionApprovals) {
		if (this.suspensionApprovals == null) {
			this.suspensionApprovals = new ArrayList<RfaEventSuspensionApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (suspensionApprovals != null) {
				for (RfaEventSuspensionApproval oldApproval : this.suspensionApprovals) {
					for (RfaEventSuspensionApproval newApproval : suspensionApprovals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RfaSuspensionApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RfaSuspensionApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
	public List<RfaSuspensionComment> getSuspensionComment() {
		return suspensionComment;
	}

	/**
	 * @param suspensionComment the suspensionComment to set
	 */
	public void setSuspensionComment(List<RfaSuspensionComment> suspensionComment) {
		this.suspensionComment = suspensionComment;
	}

	/**
	 * @return the awardApprovals
	 */
	public List<RfaEventAwardApproval> getAwardApprovals() {
		return awardApprovals;
	}

	/**
	 * @param awardApprovals the awardApprovals to set
	 */
	public void setAwardApprovals(List<RfaEventAwardApproval> awardApprovals) {
		if (this.awardApprovals == null) {
			this.awardApprovals = new ArrayList<RfaEventAwardApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (awardApprovals != null) {
				for (RfaEventAwardApproval oldApproval : this.awardApprovals) {
					for (RfaEventAwardApproval newApproval : awardApprovals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (RfaAwardApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (RfaAwardApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
	 * @return the awardComment
	 */
	public List<RfaAwardComment> getAwardComment() {
		return awardComment;
	}

	/**
	 * @param awardComment the awardComment to set
	 */
	public void setAwardComment(List<RfaAwardComment> awardComment) {
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


	public List<RfaEventSor> getEventSors() {
		return eventSors;
	}

	public void setEventSors(List<RfaEventSor> eventSors) {
		this.eventSors = eventSors;
	}

	public Boolean getAddScheduleOfRate() {
		return addScheduleOfRate;
	}

	public void setAddScheduleOfRate(Boolean addScheduleOfRate) {
		this.addScheduleOfRate = addScheduleOfRate;
	}

	public List<RfaSupplierSor> getRfaSupplierSors() {
		return rfaSupplierSors;
	}

	public void setRfaSupplierSors(List<RfaSupplierSor> rfaSupplierSors) {
		this.rfaSupplierSors = rfaSupplierSors;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfaEvent [toLogString()=" + super.toLogString() + "]";
	}

}
