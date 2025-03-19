package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

public class DraftEventPojo implements Serializable {

	private static final long serialVersionUID = -6809939819213651830L;

	private String eventName;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date modifiedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventStart;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date publishDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date deliveryDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventEnd;

	private String costCenter;

	private User createdBy;
	private String ownerName;

	private String id;

	private RfxTypes type;
	private RfxTypes eventType;

	private String eventStartDate;

	private String eventEndDate;

	private EventStatus status;
	private EventVisibilityType visibility;
	private Integer validityDays;
	private EventStatus eventStatus;
	private String unitName;
	private String businessUnitId;
	private String referenceNumber;
	private String sysEventId;
	private String eventUser;
	private int invitedSupplierCount;
	private int acceptedSupplierCount;
	private int submittedSupplierCount;
	private String leadingSupplier;
	private BigDecimal leadingAmount;
	private String eventCategories;
	private String eventDescription;
	private BigDecimal estimatedBudget;
	private BigDecimal histricAmount;
	private BigDecimal availableAmount;
	private String templateName;
	private String assoiciateOwner;
	private String unmaskOwner;
	private String deliveryAddress;
	private String auctionType;
	private BigDecimal eventFees;
	private BigDecimal deposite;
	private String subStatus;
	private String awardedSupplier;
	private String concluded;
	private String pushToEvent;
	private String pushToPr;
	private String awardedDate;
	private String pushDate;
	private String concludedaDate;
	private String avarageBidSubmited;
	private BigDecimal participationFees;
	private int preViewSupplierCount;
	private int rejectedSupplierCount;
	private int disqualifedSuppliers;
	private String memberType;
	private String addressTitle;
	private String line1;
	private String line2;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date awardDate;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventPushDate;
	private BigDecimal avgGrandTotal;
	private Date prPushDate;
	private String currencyName;

	private String template;
	boolean templateActive;
	private String eventId;
	private String referanceNumber;
	private String industryCategory;
	private String name;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventConcludeDate;

	private String winningSupplier;
	private int selfInvitedSupplierCount;
	private int participatedSupplierCount;
	private String supplierTags;
	private AuctionType auctiontype;
	private EventVisibilityType eventVisibility;
	private BigDecimal budgetAmount;
	private BigDecimal savingsAmountBudget;
	private BigDecimal savingsAmountHistoric;
	private BigDecimal savingsPercentageBudget;
	private BigDecimal savingsPercentageHistoric;
	private String finalBid;
	private int noOfBids;
	private BigDecimal ratio;
	private String selfInvitedWinner;
	private String eventVis;
	private String auctType;
	private String savingsBudget;
	private String savingsHistoric;
	private String savingsBudgetPercentage;
	private String savingsHistoricPercentage;
	private BigDecimal leadingSuppierBid;
	private String leadingSuppier;
	private String awardedSuppliers;
	private String awardedPrice;
	private BigDecimal sumAwardedPrice;
	private int decimalValue;
	private String eventDecimal;
	private Boolean viewUnmaskSupplerName;

	private String publishDateForm;
	private String startDate;
	private String endDate;
	private String deliveryDateForm;
	private String prToPushDate;

	private String procurementMethod;
	private String procurementCategories;

	private String groupCode;

	public GroupCode getGroupCode;

	public DraftEventPojo() {

	}

	public DraftEventPojo(String id, String eventName, User createdBy, Date createdDate, Date modifiedDate, RfxTypes type) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			createdBy.getLoginId();
		}
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.type = type;
	}

	/**
	 * @return the eventDescription
	 */
	public String getEventDescription() {
		return eventDescription;
	}

	/**
	 * @param eventDescription the eventDescription to set
	 */
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public DraftEventPojo(String id, String eventName, String createdBy, Date createdDate, Date modifiedDate, String type, Date eventStart, Date eventEnd) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.type = RfxTypes.fromString(type);
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
	}

	public DraftEventPojo(Event event) {
		this.eventName = event.getEventName();
		this.createdBy = event.getCreatedBy();
		this.createdDate = event.getCreatedDate();
		this.modifiedDate = event.getModifiedDate();
	}

	public DraftEventPojo(String id, String eventName, String createdBy, Date createdDate, String type, Date eventStart, Date eventEnd, String referenceNumber, String unitName, String sysEventId, String eventUser) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.type = RfxTypes.fromString(type);
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.unitName = unitName;
		this.referenceNumber = referenceNumber;
		this.sysEventId = sysEventId;
		this.eventUser = eventUser;
	}

	public DraftEventPojo(String id, String eventName, String createdBy, Date createdDate, Date modifiedDate, String type, Date eventStart, Date eventEnd, String referenceNumber, String unitName, String sysEventId, String eventUser, String status) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.type = RfxTypes.fromString(type);
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.unitName = unitName;
		this.referenceNumber = referenceNumber;
		this.sysEventId = sysEventId;
		this.eventUser = eventUser;
		this.status = EventStatus.fromString(status);
	}

	public DraftEventPojo(String id, String eventName, String createdBy, Date createdDate, Date modifiedDate, String type, Date eventStart, Date eventEnd, String referenceNumber, String unitName, String sysEventId, String eventUser, String status,String businessUnitId) {
		this.id = id;
		this.eventName = eventName;
		if (createdBy != null) {
			User user = new User();
			user.setName(createdBy);
			user.setLoginId(createdBy);
			this.createdBy = user;
		}
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.type = RfxTypes.fromString(type);
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.unitName = unitName;
		this.referenceNumber = referenceNumber;
		this.sysEventId = sysEventId;
		this.eventUser = eventUser;
		this.status = EventStatus.fromString(status);
		this.businessUnitId=businessUnitId;
	}

	public DraftEventPojo(String id, String eventId, String type) {
		this.id = id;
		this.sysEventId = eventId;
		this.type = RfxTypes.fromStringToRfxType(type);
	}

	public DraftEventPojo(String id, BigDecimal avgGrandTotal, Date prPushDate, String teamMember, String addressTitle, String line1, String memberType, String sysEventId, String eventName, String referenceNumber, String eventDescription, String ownerName, Date publishDate, Date eventStart, Date eventEnd, Date deliveryDate, String visibility, String validityDays, String eventType, String currencyName, String unitName, String costCenter, BigDecimal budgetAmount, BigDecimal historicAmount, BigDecimal participationFees, BigDecimal deposit, Date awardDate, Date eventpushDate, Date concludeDate, String unMaskedUser, String templateName, String status, String leadingSupplier, BigDecimal leadingAmount, String invitedSupplierCount, String submittedSupplierCount, String acceptedSupplierCount, String eventCategories, String preViewSupplierCount, String rejectedSupplierCount, String disqualifedSuppliers, String auctionType) {
		this.id = id;
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.referenceNumber = referenceNumber;
		this.sysEventId = sysEventId;
		this.eventUser = ownerName;
		this.visibility = EventVisibilityType.fromString(visibility);
		this.type = RfxTypes.valueOf(eventType);
		this.publishDate = publishDate;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.deliveryDate = deliveryDate;
		this.unitName = unitName;
		this.costCenter = costCenter;
		this.eventCategories = eventCategories;
		this.status = EventStatus.fromString(status);
		this.invitedSupplierCount = StringUtils.checkString(invitedSupplierCount).length() > 0 ? Integer.parseInt(invitedSupplierCount) : 0;
		this.submittedSupplierCount = StringUtils.checkString(submittedSupplierCount).length() > 0 ? Integer.parseInt(submittedSupplierCount) : 0;
		this.acceptedSupplierCount = StringUtils.checkString(acceptedSupplierCount).length() > 0 ? Integer.parseInt(acceptedSupplierCount) : 0;
		this.validityDays = StringUtils.checkString(validityDays).length() > 0 ? Integer.parseInt(validityDays) : 0;
		this.leadingAmount = leadingAmount;
		this.leadingSupplier = leadingSupplier;
		this.estimatedBudget = budgetAmount != null ? budgetAmount : BigDecimal.ZERO;
		this.histricAmount = historicAmount != null ? historicAmount : BigDecimal.ZERO;
		this.participationFees = participationFees != null ? participationFees : BigDecimal.ZERO;
		this.deposite = deposit;
		this.awardDate = awardDate;
		this.eventPushDate = eventpushDate;
		this.eventConcludeDate = concludeDate;
		this.unmaskOwner = unMaskedUser;
		this.preViewSupplierCount = StringUtils.checkString(preViewSupplierCount).length() > 0 ? Integer.parseInt(preViewSupplierCount) : 0;
		this.rejectedSupplierCount = StringUtils.checkString(rejectedSupplierCount).length() > 0 ? Integer.parseInt(rejectedSupplierCount) : 0;
		this.disqualifedSuppliers = StringUtils.checkString(disqualifedSuppliers).length() > 0 ? Integer.parseInt(disqualifedSuppliers) : 0;
		this.auctionType = auctionType;
		this.templateName = templateName;
		this.memberType = memberType;
		this.assoiciateOwner = teamMember;
		this.addressTitle = addressTitle;
		this.line1 = line1;
		this.avgGrandTotal = avgGrandTotal;
		this.prPushDate = prPushDate;
		this.currencyName = currencyName;
	}

	// this is used for event excel report with optimized code please confirm before change
	public DraftEventPojo(String id, BigDecimal avgGrandTotal, Date prPushDate, String teamMember, String addressTitle, String line1, String memberType, String sysEventId, String eventName, String eventDecimal, String referenceNumber, String eventDescription, String ownerName, Date publishDate, Date eventStart, Date eventEnd, Date deliveryDate, String visibility, String validityDays, String eventType, String currencyName, String unitName, String costCenter, BigDecimal budgetAmount, BigDecimal historicAmount, BigDecimal participationFees, BigDecimal deposit, Date awardDate, Date eventpushDate, Date concludeDate, String templateName, String status, String leadingSupplier, BigDecimal leadingAmount, String invitedSupplierCount, String submittedSupplierCount, String acceptedSupplierCount, String eventCategories, String preViewSupplierCount, String rejectedSupplierCount, String disqualifedSuppliers, String unMaskedUser, String auctionType) {
		this.id = id;
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.referenceNumber = referenceNumber;
		this.sysEventId = sysEventId;
		this.eventUser = ownerName;
		this.visibility = EventVisibilityType.fromString(visibility);
		this.type = RfxTypes.valueOf(eventType);
		this.publishDate = publishDate;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.deliveryDate = deliveryDate;
		this.unitName = unitName;
		this.costCenter = costCenter;
		this.eventCategories = eventCategories;
		this.status = EventStatus.fromString(status);
		this.invitedSupplierCount = StringUtils.checkString(invitedSupplierCount).length() > 0 ? Integer.parseInt(invitedSupplierCount) : 0;
		this.submittedSupplierCount = StringUtils.checkString(submittedSupplierCount).length() > 0 ? Integer.parseInt(submittedSupplierCount) : 0;
		this.acceptedSupplierCount = StringUtils.checkString(acceptedSupplierCount).length() > 0 ? Integer.parseInt(acceptedSupplierCount) : 0;
		this.validityDays = StringUtils.checkString(validityDays).length() > 0 ? Integer.parseInt(validityDays) : 0;
		this.leadingAmount = leadingAmount;
		this.leadingSupplier = leadingSupplier;
		this.estimatedBudget = budgetAmount != null ? budgetAmount : BigDecimal.ZERO;
		this.histricAmount = historicAmount != null ? historicAmount : BigDecimal.ZERO;
		this.participationFees = participationFees != null ? participationFees : BigDecimal.ZERO;
		this.deposite = deposit;
		this.awardDate = awardDate;
		this.eventPushDate = eventpushDate;
		this.eventConcludeDate = concludeDate;
		this.unmaskOwner = unMaskedUser;
		this.preViewSupplierCount = StringUtils.checkString(preViewSupplierCount).length() > 0 ? Integer.parseInt(preViewSupplierCount) : 0;
		this.rejectedSupplierCount = StringUtils.checkString(rejectedSupplierCount).length() > 0 ? Integer.parseInt(rejectedSupplierCount) : 0;
		this.disqualifedSuppliers = StringUtils.checkString(disqualifedSuppliers).length() > 0 ? Integer.parseInt(disqualifedSuppliers) : 0;
		this.auctionType = auctionType;
		this.templateName = templateName;
		this.memberType = memberType;
		this.assoiciateOwner = teamMember;
		this.addressTitle = addressTitle;
		this.line1 = line1;
		this.avgGrandTotal = avgGrandTotal;
		this.prPushDate = prPushDate;
		this.currencyName = currencyName;
		this.eventDecimal = eventDecimal;
	}

	// PH-1393
	public DraftEventPojo(String id, BigDecimal avgGrandTotal, Date prPushDate, String teamMember, String addressTitle, String line1, String memberType, String sysEventId, String eventName, String eventDecimal, Boolean viewUnmaskSupplerName, String referenceNumber, String eventDescription, String ownerName, Date publishDate, Date eventStart, Date eventEnd, Date deliveryDate, String visibility, String validityDays, String eventType, String currencyName, String unitName, String costCenter, BigDecimal budgetAmount, BigDecimal historicAmount, BigDecimal participationFees, BigDecimal deposit, Date awardDate, Date eventpushDate, Date concludeDate, String templateName, String status, String leadingSupplier, BigDecimal leadingAmount, String invitedSupplierCount, String submittedSupplierCount, String acceptedSupplierCount, String eventCategories, String preViewSupplierCount, String rejectedSupplierCount, String disqualifedSuppliers, String unMaskedUser, String auctionType) {
		this.id = id;
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.referenceNumber = referenceNumber;
		this.sysEventId = sysEventId;
		this.eventUser = ownerName;
		this.visibility = EventVisibilityType.fromString(visibility);
		this.type = RfxTypes.valueOf(eventType);
		this.publishDate = publishDate;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.deliveryDate = deliveryDate;
		this.unitName = unitName;
		this.costCenter = costCenter;
		this.eventCategories = eventCategories;
		this.status = EventStatus.fromString(status);
		this.invitedSupplierCount = StringUtils.checkString(invitedSupplierCount).length() > 0 ? Integer.parseInt(invitedSupplierCount) : 0;
		this.submittedSupplierCount = StringUtils.checkString(submittedSupplierCount).length() > 0 ? Integer.parseInt(submittedSupplierCount) : 0;
		this.acceptedSupplierCount = StringUtils.checkString(acceptedSupplierCount).length() > 0 ? Integer.parseInt(acceptedSupplierCount) : 0;
		this.validityDays = StringUtils.checkString(validityDays).length() > 0 ? Integer.parseInt(validityDays) : 0;
		this.leadingAmount = leadingAmount;
		this.leadingSupplier = leadingSupplier;
		this.estimatedBudget = budgetAmount != null ? budgetAmount : BigDecimal.ZERO;
		this.histricAmount = historicAmount != null ? historicAmount : BigDecimal.ZERO;
		this.participationFees = participationFees != null ? participationFees : BigDecimal.ZERO;
		this.deposite = deposit;
		this.awardDate = awardDate;
		this.eventPushDate = eventpushDate;
		this.eventConcludeDate = concludeDate;
		this.unmaskOwner = unMaskedUser;
		this.preViewSupplierCount = StringUtils.checkString(preViewSupplierCount).length() > 0 ? Integer.parseInt(preViewSupplierCount) : 0;
		this.rejectedSupplierCount = StringUtils.checkString(rejectedSupplierCount).length() > 0 ? Integer.parseInt(rejectedSupplierCount) : 0;
		this.disqualifedSuppliers = StringUtils.checkString(disqualifedSuppliers).length() > 0 ? Integer.parseInt(disqualifedSuppliers) : 0;
		this.auctionType = auctionType;
		this.templateName = templateName;
		this.memberType = memberType;
		this.assoiciateOwner = teamMember;
		this.addressTitle = addressTitle;
		this.line1 = line1;
		this.avgGrandTotal = avgGrandTotal;
		this.prPushDate = prPushDate;
		this.currencyName = currencyName;
		this.eventDecimal = eventDecimal;
		this.viewUnmaskSupplerName = viewUnmaskSupplerName;
	}
	// Auction Summary report
//	 public DraftEventPojo(String id, String eventName,String currencyName, String referanceNumber, String sysEventId,
//	 String auctiontype, String ownerName, Date eventStart, Date eventEnd, String eventVisibility, String unitName,
//	 String templateName, BigDecimal leadingSuppierBid, String leadingSuppier, String awardedSupplier, BigDecimal awardedPrice, String winningSupplier, String selfInvitedWinner, BigDecimal leadingAmount, String
//	 invitedSupplierCount, String participatedSupplierCount, String selfInvitedSupplierCount, String
//	 submittedSupplierCount, String eventCategories, BigDecimal budgetAmount, BigDecimal historicAmount, BigDecimal
//	 sumAwardedPrice, String supplierTags, String finalBid, String noOfBids, BigDecimal ratio) {
//
//	 }

	public DraftEventPojo(String id, String eventName, String decimalValue, String currencyName, BigDecimal leadingSuppierBid, String leadingSuppier, String awardedSupplier, String referanceNumber, String sysEventId, String auctiontype, String ownerName, Date eventStart, Date eventEnd, String eventVisibility, String unitName, String templateName, String winningSupplier, String selfInvitedWinner, BigDecimal leadingAmount, String invitedSupplierCount, String participatedSupplierCount, String selfInvitedSupplierCount, BigInteger submittedSupplierCount, String eventCategories, BigDecimal budgetAmount, BigDecimal historicAmount, BigDecimal sumAwardedPrice, String supplierTags, String noOfBids, BigDecimal ratio) {
		this.id = id;
		this.eventName = eventName;
		this.currencyName = currencyName;
		this.leadingSuppierBid = leadingSuppierBid != null ? leadingSuppierBid : BigDecimal.ZERO;
		this.leadingSuppier = leadingSuppier;
		this.awardedSuppliers = awardedSupplier;
		this.referanceNumber = referanceNumber;
		this.sysEventId = sysEventId;
		this.auctionType = auctiontype;
		this.eventUser = ownerName;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventVisibility = EventVisibilityType.fromStringToEventVisibilityType(eventVisibility);
		this.unitName = unitName;
		this.templateName = templateName;
		this.winningSupplier = winningSupplier;
		this.selfInvitedWinner = selfInvitedWinner;
		this.leadingAmount = leadingAmount;
		this.invitedSupplierCount = StringUtils.checkString(invitedSupplierCount).length() > 0 ? Integer.parseInt(invitedSupplierCount) : 0;
		this.participatedSupplierCount = StringUtils.checkString(participatedSupplierCount).length() > 0 ? Integer.parseInt(participatedSupplierCount) : 0;
		this.selfInvitedSupplierCount = StringUtils.checkString(selfInvitedSupplierCount).length() > 0 ? Integer.parseInt(selfInvitedSupplierCount) : 0;
//		this.submittedSupplierCount = StringUtils.checkString(submittedSupplierCount).length() > 0 ? Integer.parseInt(submittedSupplierCount) : 0;
		this.submittedSupplierCount = submittedSupplierCount.intValueExact();
		this.eventCategories = eventCategories;
		this.budgetAmount = budgetAmount != null ? budgetAmount : BigDecimal.ZERO;
		this.histricAmount = historicAmount != null ? historicAmount : BigDecimal.ZERO;
		this.sumAwardedPrice = sumAwardedPrice != null ? sumAwardedPrice : BigDecimal.ZERO;
		this.supplierTags = supplierTags;
		this.noOfBids = StringUtils.checkString(noOfBids).length() > 0 ? Integer.parseInt(noOfBids) : 0;
		this.ratio = ratio;
		this.decimalValue = StringUtils.checkString(decimalValue).length() > 0 ? Integer.parseInt(decimalValue) : 0;
		// this.savingsBudget = savingsBudget;
		// this.savingsHistoric = savingsHistoric;
		// this.savingsBudgetPercentage = savingsBudgetPercentage;
		// this.savingsHistoricPercentage = savingsHistoricPercentage;
	}

	public DraftEventPojo(String id, String eventName, String auctionType, String eventStatus, String eventDescription, Number templateActive, String referenceNumber, String eventId, Date startDate, Date endDate, String eventCategories) {
		this.id = id;
		this.eventName = eventName;
		this.auctionType = auctionType;
		this.status = EventStatus.fromString(eventStatus);
		this.eventDescription = eventDescription;
		this.referanceNumber = referenceNumber;
		this.eventId = eventId;
		this.eventStart = startDate;
		this.eventEnd = endDate;
		this.industryCategory = eventCategories;
		this.templateActive = templateActive != null ? (templateActive.equals(1) ? true : false) : false;

	}

	// eventReport
	public DraftEventPojo(String id, String eventName, Date eventStart, Date eventEnd, RfxTypes type, EventStatus status, String unitName, String referenceNumber, String sysEventId, String eventUser, Date createdDate) {
		this.eventName = eventName;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.id = id;
		this.type = type;
		this.status = status;
		this.unitName = unitName;
		this.referenceNumber = referenceNumber;
		this.sysEventId = sysEventId;
		this.eventUser = eventUser;
		this.createdDate = createdDate;
	}

	// PH-521 Screen Data
	public DraftEventPojo(String id, String eventName, String referanceNumber, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, String unitName, String templateName, String sysEventId, String eventUser) {
		this.id = id;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		// this.auctionType = AuctionType.getValue(auctionType);
		this.referanceNumber = referanceNumber;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.unitName = unitName;
		this.sysEventId = sysEventId;
		this.templateName = templateName;
		this.eventUser = eventUser;

	}

	public DraftEventPojo(String id, String eventName, String referanceNumber, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, String unitName, String templateName, String sysEventId, String eventUser, Date createdDate) {
		this.id = id;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.referanceNumber = referanceNumber;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.unitName = unitName;
		this.sysEventId = sysEventId;
		this.templateName = templateName;
		this.eventUser = eventUser;
		this.createdDate = createdDate;
	}

	// PH-1889 eventreport
	public DraftEventPojo(BigDecimal avgGrandTotal, String id, Date prPushDate, String teamMember, String addressTitle, String line1, String memberType, String sysEventId, String eventName, String eventDecimal, Boolean viewUnmaskSupplerName, String referenceNumber, String eventDescription, String ownerName, Date publishDate, Date eventStart, Date eventEnd, Date deliveryDate, String visibility, String validityDays, String eventType, String currencyName, String unitName, String costCenter, BigDecimal budgetAmount, BigDecimal historicAmount, BigDecimal participationFees, BigDecimal deposit, Date awardDate, Date eventpushDate, Date concludeDate, String templateName, String status, String leadingSupplier, BigDecimal leadingAmount, String invitedSupplierCount, String submittedSupplierCount, String acceptedSupplierCount, String eventCategories, String preViewSupplierCount, String rejectedSupplierCount, String disqualifedSuppliers, String unMaskedUser, String auctionType) {
		this.id = id;
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.referenceNumber = referenceNumber;
		this.sysEventId = sysEventId;
		this.eventUser = ownerName;
		this.visibility = EventVisibilityType.fromString(visibility);
		this.type = RfxTypes.valueOf(eventType);
		this.publishDate = publishDate;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.deliveryDate = deliveryDate;
		this.unitName = unitName;
		this.costCenter = costCenter;
		this.eventCategories = eventCategories;
		this.status = EventStatus.fromString(status);
		this.invitedSupplierCount = StringUtils.checkString(invitedSupplierCount).length() > 0 ? Integer.parseInt(invitedSupplierCount) : 0;
		this.submittedSupplierCount = StringUtils.checkString(submittedSupplierCount).length() > 0 ? Integer.parseInt(submittedSupplierCount) : 0;
		this.acceptedSupplierCount = StringUtils.checkString(acceptedSupplierCount).length() > 0 ? Integer.parseInt(acceptedSupplierCount) : 0;
		this.validityDays = StringUtils.checkString(validityDays).length() > 0 ? Integer.parseInt(validityDays) : 0;
		this.leadingAmount = leadingAmount;
		this.leadingSupplier = leadingSupplier;
		this.estimatedBudget = budgetAmount != null ? budgetAmount : BigDecimal.ZERO;
		this.histricAmount = historicAmount != null ? historicAmount : BigDecimal.ZERO;
		this.participationFees = participationFees != null ? participationFees : BigDecimal.ZERO;
		this.deposite = deposit;
		this.awardDate = awardDate;
		this.eventPushDate = eventpushDate;
		this.eventConcludeDate = concludeDate;
		this.unmaskOwner = unMaskedUser;
		this.preViewSupplierCount = StringUtils.checkString(preViewSupplierCount).length() > 0 ? Integer.parseInt(preViewSupplierCount) : 0;
		this.rejectedSupplierCount = StringUtils.checkString(rejectedSupplierCount).length() > 0 ? Integer.parseInt(rejectedSupplierCount) : 0;
		this.disqualifedSuppliers = StringUtils.checkString(disqualifedSuppliers).length() > 0 ? Integer.parseInt(disqualifedSuppliers) : 0;
		this.auctionType = auctionType;
		this.templateName = templateName;
		this.memberType = memberType;
		this.assoiciateOwner = teamMember;
		this.addressTitle = (addressTitle != null ? addressTitle : "") + line1 != null ? line1 : "";
		// this.line1 = line1;
		this.avgGrandTotal = avgGrandTotal;
		this.prPushDate = prPushDate;
		this.currencyName = currencyName;
		this.eventDecimal = eventDecimal;
		this.viewUnmaskSupplerName = viewUnmaskSupplerName;
		this.concluded = concludeDate != null ? "YES" : "NO";
		this.pushToEvent = eventpushDate != null ? "YES" : "NO";
		this.pushToPr = prPushDate != null ? "YES" : "NO";
	}

	// PH-2168
	public DraftEventPojo(String id, BigDecimal avgGrandTotal, Date prPushDate, String teamMember, String addressTitle, String line1, String memberType, String sysEventId, String eventName, String eventDecimal, Boolean viewUnmaskSupplerName, String referenceNumber, String eventDescription, String ownerName, Date publishDate, Date eventStart, Date eventEnd, Date deliveryDate, String visibility, String validityDays, String eventType, String currencyName, String unitName, String costCenter, BigDecimal budgetAmount, BigDecimal estimatedBudget, BigDecimal historicAmount, BigDecimal participationFees, BigDecimal deposit, Date awardDate, Date eventpushDate, Date concludeDate, String templateName, String procurementMethod, String procurementCategories, String status, String leadingSupplier, BigDecimal leadingAmount, String invitedSupplierCount, String submittedSupplierCount, String acceptedSupplierCount, String eventCategories, String preViewSupplierCount, String rejectedSupplierCount, String disqualifedSuppliers, String unMaskedUser, String groupCode) {
		this.id = id;
		this.avgGrandTotal = avgGrandTotal;
		this.prPushDate = prPushDate;
		this.assoiciateOwner = teamMember;
		this.addressTitle = addressTitle;
		this.line1 = line1;
		this.memberType = memberType;
		this.sysEventId = sysEventId;
		this.eventName = eventName;
		this.eventDecimal = eventDecimal;
		this.viewUnmaskSupplerName = viewUnmaskSupplerName;
		this.referenceNumber = referenceNumber;
		this.eventDescription = eventDescription;
		this.eventUser = ownerName;
		this.publishDate = publishDate;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.deliveryDate = deliveryDate;
		this.visibility = EventVisibilityType.fromString(visibility);
		this.validityDays = StringUtils.checkString(validityDays).length() > 0 ? Integer.parseInt(validityDays) : 0;
		this.type = RfxTypes.valueOf(eventType);
		this.currencyName = currencyName;
		this.unitName = unitName;
		this.costCenter = costCenter;
		this.budgetAmount = budgetAmount != null ? budgetAmount : BigDecimal.ZERO;
		this.estimatedBudget = estimatedBudget != null ? estimatedBudget : BigDecimal.ZERO;
		this.histricAmount = historicAmount != null ? historicAmount : BigDecimal.ZERO;
		this.participationFees = participationFees != null ? participationFees : BigDecimal.ZERO;
		this.deposite = deposit;
		this.awardDate = awardDate;
		this.eventPushDate = eventpushDate;
		this.eventConcludeDate = concludeDate;
		this.templateName = templateName;
		this.procurementMethod = procurementMethod;
		this.procurementCategories = procurementCategories;
		this.status = EventStatus.fromString(status);
		this.leadingSupplier = leadingSupplier;
		this.leadingAmount = leadingAmount;
		this.invitedSupplierCount = StringUtils.checkString(invitedSupplierCount).length() > 0 ? Integer.parseInt(invitedSupplierCount) : 0;
		this.submittedSupplierCount = StringUtils.checkString(submittedSupplierCount).length() > 0 ? Integer.parseInt(submittedSupplierCount) : 0;
		this.acceptedSupplierCount = StringUtils.checkString(acceptedSupplierCount).length() > 0 ? Integer.parseInt(acceptedSupplierCount) : 0;
		this.eventCategories = eventCategories;
		this.preViewSupplierCount = StringUtils.checkString(preViewSupplierCount).length() > 0 ? Integer.parseInt(preViewSupplierCount) : 0;
		this.rejectedSupplierCount = StringUtils.checkString(rejectedSupplierCount).length() > 0 ? Integer.parseInt(rejectedSupplierCount) : 0;
		this.disqualifedSuppliers = StringUtils.checkString(disqualifedSuppliers).length() > 0 ? Integer.parseInt(disqualifedSuppliers) : 0;
		this.unmaskOwner = unMaskedUser;
		this.groupCode = groupCode;
	}

	// PH-2168 AuctionType
	public DraftEventPojo(BigDecimal avgGrandTotal, String id, Date prPushDate, String teamMember, String addressTitle, String line1, String memberType, String sysEventId, String eventName, String eventDecimal, Boolean viewUnmaskSupplerName, String referenceNumber, String eventDescription, String ownerName, Date publishDate, Date eventStart, Date eventEnd, Date deliveryDate, String visibility, String validityDays, String eventType, String currencyName, String unitName, String costCenter, BigDecimal budgetAmount, BigDecimal estimatedBudget, BigDecimal historicAmount, BigDecimal participationFees, BigDecimal deposit, Date awardDate, Date eventpushDate, Date concludeDate, String templateName, String procurementMethod, String procurementCategories, String status, String leadingSupplier, BigDecimal leadingAmount, String invitedSupplierCount, String submittedSupplierCount, String acceptedSupplierCount, String eventCategories, String preViewSupplierCount, String rejectedSupplierCount, String disqualifedSuppliers, String unMaskedUser, String auctionType, String groupCode) {
		this.avgGrandTotal = avgGrandTotal;
		this.id = id;
		this.prPushDate = prPushDate;
		this.assoiciateOwner = teamMember;
		this.addressTitle = addressTitle;
		this.line1 = line1;
		this.memberType = memberType;
		this.sysEventId = sysEventId;
		this.eventName = eventName;
		this.eventDecimal = eventDecimal;
		this.viewUnmaskSupplerName = viewUnmaskSupplerName;
		this.referenceNumber = referenceNumber;
		this.eventDescription = eventDescription;
		this.eventUser = ownerName;
		this.publishDate = publishDate;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.deliveryDate = deliveryDate;
		this.visibility = EventVisibilityType.fromString(visibility);
		this.validityDays = StringUtils.checkString(validityDays).length() > 0 ? Integer.parseInt(validityDays) : 0;
		this.type = RfxTypes.valueOf(eventType);
		this.currencyName = currencyName;
		this.unitName = unitName;
		this.costCenter = costCenter;
		this.budgetAmount = budgetAmount != null ? budgetAmount : BigDecimal.ZERO;
		this.estimatedBudget = estimatedBudget != null ? estimatedBudget : BigDecimal.ZERO;
		this.histricAmount = historicAmount != null ? historicAmount : BigDecimal.ZERO;
		this.participationFees = participationFees != null ? participationFees : BigDecimal.ZERO;
		this.deposite = deposit;
		this.awardDate = awardDate;
		this.eventPushDate = eventpushDate;
		this.eventConcludeDate = concludeDate;
		this.templateName = templateName;
		this.procurementMethod = procurementMethod;
		this.procurementCategories = procurementCategories;
		this.status = EventStatus.fromString(status);
		this.leadingSupplier = leadingSupplier;
		this.leadingAmount = leadingAmount;
		this.invitedSupplierCount = StringUtils.checkString(invitedSupplierCount).length() > 0 ? Integer.parseInt(invitedSupplierCount) : 0;
		this.submittedSupplierCount = StringUtils.checkString(submittedSupplierCount).length() > 0 ? Integer.parseInt(submittedSupplierCount) : 0;
		this.acceptedSupplierCount = StringUtils.checkString(acceptedSupplierCount).length() > 0 ? Integer.parseInt(acceptedSupplierCount) : 0;
		this.eventCategories = eventCategories;
		this.preViewSupplierCount = StringUtils.checkString(preViewSupplierCount).length() > 0 ? Integer.parseInt(preViewSupplierCount) : 0;
		this.rejectedSupplierCount = StringUtils.checkString(rejectedSupplierCount).length() > 0 ? Integer.parseInt(rejectedSupplierCount) : 0;
		this.disqualifedSuppliers = StringUtils.checkString(disqualifedSuppliers).length() > 0 ? Integer.parseInt(disqualifedSuppliers) : 0;
		this.unmaskOwner = unMaskedUser;
		this.auctionType = auctionType;
		this.groupCode = groupCode;
	}

	public String getPublishDateForm() {
		return publishDateForm;
	}

	public void setPublishDateForm(String publishDateForm) {
		this.publishDateForm = publishDateForm;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getDeliveryDateForm() {
		return deliveryDateForm;
	}

	public void setDeliveryDateForm(String deliveryDateForm) {
		this.deliveryDateForm = deliveryDateForm;
	}

	public String getPrToPushDate() {
		return prToPushDate;
	}

	public void setPrToPushDate(String prToPushDate) {
		this.prToPushDate = prToPushDate;
	}

	/**
	 * @return the decimalValue
	 */
	public int getDecimalValue() {
		return decimalValue;
	}

	/**
	 * @param decimalValue the decimalValue to set
	 */
	public void setDecimalValue(int decimalValue) {
		this.decimalValue = decimalValue;
	}

	/**
	 * @return the awardedPrice
	 */
	public String getAwardedPrice() {
		return awardedPrice;
	}

	/**
	 * @param awardedPrice the awardedPrice to set
	 */
	public void setAwardedPrice(String awardedPrice) {
		this.awardedPrice = awardedPrice;
	}

	/**
	 * @return the awardedSuppliers
	 */
	public String getAwardedSuppliers() {
		return awardedSuppliers;
	}

	/**
	 * @param awardedSuppliers the awardedSuppliers to set
	 */
	public void setAwardedSuppliers(String awardedSuppliers) {
		this.awardedSuppliers = awardedSuppliers;
	}

	/**
	 * @return the sumAwardedPrice
	 */
	public BigDecimal getSumAwardedPrice() {
		return sumAwardedPrice;
	}

	/**
	 * @param sumAwardedPrice the sumAwardedPrice to set
	 */
	public void setSumAwardedPrice(BigDecimal sumAwardedPrice) {
		this.sumAwardedPrice = sumAwardedPrice;
	}

	/**
	 * @return the leadingSuppierBid
	 */
	public BigDecimal getLeadingSuppierBid() {
		return leadingSuppierBid;
	}

	/**
	 * @param leadingSuppierBid the leadingSuppierBid to set
	 */
	public void setLeadingSuppierBid(BigDecimal leadingSuppierBid) {
		this.leadingSuppierBid = leadingSuppierBid;
	}

	/**
	 * @return the leadingSuppier
	 */
	public String getLeadingSuppier() {
		return leadingSuppier;
	}

	/**
	 * @param leadingSuppier the leadingSuppier to set
	 */
	public void setLeadingSuppier(String leadingSuppier) {
		this.leadingSuppier = leadingSuppier;
	}

	/**
	 * @return the ratio
	 */
	public BigDecimal getRatio() {
		return ratio;
	}

	/**
	 * @param ratio the ratio to set
	 */
	public void setRatio(BigDecimal ratio) {
		this.ratio = ratio;
	}

	/**
	 * @return the finalBid
	 */
	public String getFinalBid() {
		return finalBid;
	}

	/**
	 * @param finalBid the finalBid to set
	 */
	public void setFinalBid(String finalBid) {
		this.finalBid = finalBid;
	}

	/**
	 * @return the eventVis
	 */
	public String getEventVis() {
		return eventVis;
	}

	/**
	 * @param eventVis the eventVis to set
	 */
	public void setEventVis(String eventVis) {
		this.eventVis = eventVis;
	}

	/**
	 * @return the auctType
	 */
	public String getAuctType() {
		return auctType;
	}

	/**
	 * @param auctType the auctType to set
	 */
	public void setAuctType(String auctType) {
		this.auctType = auctType;
	}

	/**
	 * @return the savingsBudget
	 */
	public String getSavingsBudget() {
		return savingsBudget;
	}

	/**
	 * @param savingsBudget the savingsBudget to set
	 */
	public void setSavingsBudget(String savingsBudget) {
		this.savingsBudget = savingsBudget;
	}

	/**
	 * @return the savingsHistoric
	 */
	public String getSavingsHistoric() {
		return savingsHistoric;
	}

	/**
	 * @param savingsHistoric the savingsHistoric to set
	 */
	public void setSavingsHistoric(String savingsHistoric) {
		this.savingsHistoric = savingsHistoric;
	}

	/**
	 * @return the savingsBudgetPercentage
	 */
	public String getSavingsBudgetPercentage() {
		return savingsBudgetPercentage;
	}

	/**
	 * @param savingsBudgetPercentage the savingsBudgetPercentage to set
	 */
	public void setSavingsBudgetPercentage(String savingsBudgetPercentage) {
		this.savingsBudgetPercentage = savingsBudgetPercentage;
	}

	/**
	 * @return the savingsHistoricPercentage
	 */
	public String getSavingsHistoricPercentage() {
		return savingsHistoricPercentage;
	}

	/**
	 * @param savingsHistoricPercentage the savingsHistoricPercentage to set
	 */
	public void setSavingsHistoricPercentage(String savingsHistoricPercentage) {
		this.savingsHistoricPercentage = savingsHistoricPercentage;
	}

	/**
	 * @return the selfInvitedWinner
	 */
	public String getSelfInvitedWinner() {
		return selfInvitedWinner;
	}

	/**
	 * @param selfInvitedWinner the selfInvitedWinner to set
	 */
	public void setSelfInvitedWinner(String selfInvitedWinner) {
		this.selfInvitedWinner = selfInvitedWinner;
	}

	/**
	 * @return the noOfBids
	 */
	public int getNoOfBids() {
		return noOfBids;
	}

	/**
	 * @param noOfBids the noOfBids to set
	 */
	public void setNoOfBids(int noOfBids) {
		this.noOfBids = noOfBids;
	}

	/**
	 * @return the budgetAmount
	 */
	public BigDecimal getBudgetAmount() {
		return budgetAmount;
	}

	/**
	 * @param budgetAmount the budgetAmount to set
	 */
	public void setBudgetAmount(BigDecimal budgetAmount) {
		this.budgetAmount = budgetAmount;
	}

	/**
	 * @return the savingsAmountBudget
	 */
	public BigDecimal getSavingsAmountBudget() {
		return savingsAmountBudget;
	}

	/**
	 * @param savingsAmountBudget the savingsAmountBudget to set
	 */
	public void setSavingsAmountBudget(BigDecimal savingsAmountBudget) {
		this.savingsAmountBudget = savingsAmountBudget;
	}

	/**
	 * @return the savingsAmountHistoric
	 */
	public BigDecimal getSavingsAmountHistoric() {
		return savingsAmountHistoric;
	}

	/**
	 * @param savingsAmountHistoric the savingsAmountHistoric to set
	 */
	public void setSavingsAmountHistoric(BigDecimal savingsAmountHistoric) {
		this.savingsAmountHistoric = savingsAmountHistoric;
	}

	/**
	 * @return the savingsPercentageBudget
	 */
	public BigDecimal getSavingsPercentageBudget() {
		return savingsPercentageBudget;
	}

	/**
	 * @param savingsPercentageBudget the savingsPercentageBudget to set
	 */
	public void setSavingsPercentageBudget(BigDecimal savingsPercentageBudget) {
		this.savingsPercentageBudget = savingsPercentageBudget;
	}

	/**
	 * @return the savingsPercentageHistoric
	 */
	public BigDecimal getSavingsPercentageHistoric() {
		return savingsPercentageHistoric;
	}

	/**
	 * @param savingsPercentageHistoric the savingsPercentageHistoric to set
	 */
	public void setSavingsPercentageHistoric(BigDecimal savingsPercentageHistoric) {
		this.savingsPercentageHistoric = savingsPercentageHistoric;
	}

	/**
	 * @return the auctiontype
	 */
	public AuctionType getAuctiontype() {
		return auctiontype;
	}

	/**
	 * @param auctiontype the auctiontype to set
	 */
	public void setAuctiontype(AuctionType auctiontype) {
		this.auctiontype = auctiontype;
	}

	/**
	 * @return the eventVisibility
	 */
	public EventVisibilityType getEventVisibility() {
		return eventVisibility;
	}

	/**
	 * @param eventVisibility the eventVisibility to set
	 */
	public void setEventVisibility(EventVisibilityType eventVisibility) {
		this.eventVisibility = eventVisibility;
	}

	/**
	 * @return the winningSupplier
	 */
	public String getWinningSupplier() {
		return winningSupplier;
	}

	/**
	 * @param winningSupplier the winningSupplier to set
	 */
	public void setWinningSupplier(String winningSupplier) {
		this.winningSupplier = winningSupplier;
	}

	/**
	 * @return the selfInvitedSupplierCount
	 */
	public int getSelfInvitedSupplierCount() {
		return selfInvitedSupplierCount;
	}

	/**
	 * @param selfInvitedSupplierCount the selfInvitedSupplierCount to set
	 */
	public void setSelfInvitedSupplierCount(int selfInvitedSupplierCount) {
		this.selfInvitedSupplierCount = selfInvitedSupplierCount;
	}

	/**
	 * @return the participatedSupplierCount
	 */
	public int getParticipatedSupplierCount() {
		return participatedSupplierCount;
	}

	/**
	 * @param participatedSupplierCount the participatedSupplierCount to set
	 */
	public void setParticipatedSupplierCount(int participatedSupplierCount) {
		this.participatedSupplierCount = participatedSupplierCount;
	}

	/**
	 * @return the supplierTags
	 */
	public String getSupplierTags() {
		return supplierTags;
	}

	/**
	 * @param supplierTags the supplierTags to set
	 */
	public void setSupplierTags(String supplierTags) {
		this.supplierTags = supplierTags;
	}

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public RfxTypes getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RfxTypes type) {
		this.type = type;
	}

	/**
	 * @return the eventStart
	 */
	public Date getEventStart() {
		return eventStart;
	}

	/**
	 * @param eventStart the eventStart to set
	 */
	public void setEventStart(Date eventStart) {
		this.eventStart = eventStart;
	}

	/**
	 * @return the eventEnd
	 */
	public Date getEventEnd() {
		return eventEnd;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getSysEventId() {
		return sysEventId;
	}

	public void setSysEventId(String sysEventId) {
		this.sysEventId = sysEventId;
	}

	public String getEventUser() {
		return eventUser;
	}

	public void setEventUser(String eventUser) {
		this.eventUser = eventUser;
	}

	/**
	 * @return the status
	 */
	public EventStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(EventStatus status) {
		this.status = status;
	}

	/**
	 * @return the invitedSupplierCount
	 */
	public int getInvitedSupplierCount() {
		return invitedSupplierCount;
	}

	/**
	 * @param invitedSupplierCount the invitedSupplierCount to set
	 */
	public void setInvitedSupplierCount(int invitedSupplierCount) {
		this.invitedSupplierCount = invitedSupplierCount;
	}

	/**
	 * @return the submittedSupplierCount
	 */
	public int getSubmittedSupplierCount() {
		return submittedSupplierCount;
	}

	/**
	 * @param submittedSupplierCount the submittedSupplierCount to set
	 */
	public void setSubmittedSupplierCount(int submittedSupplierCount) {
		this.submittedSupplierCount = submittedSupplierCount;
	}

	/**
	 * @return the leadingSupplier
	 */
	public String getLeadingSupplier() {
		return leadingSupplier;
	}

	/**
	 * @param leadingSupplier the leadingSupplier to set
	 */
	public void setLeadingSupplier(String leadingSupplier) {
		this.leadingSupplier = leadingSupplier;
	}

	/**
	 * @return the leadingAmount
	 */
	public BigDecimal getLeadingAmount() {
		return leadingAmount;
	}

	/**
	 * @param leadingAmount the leadingAmount to set
	 */
	public void setLeadingAmount(BigDecimal leadingAmount) {
		this.leadingAmount = leadingAmount;
	}

	/**
	 * @return the eventCategories
	 */
	public String getEventCategories() {
		return eventCategories;
	}

	/**
	 * @param eventCategories the eventCategories to set
	 */
	public void setEventCategories(String eventCategories) {
		this.eventCategories = eventCategories;
	}

	/**
	 * @param eventEnd the eventEnd to set
	 */
	public void setEventEnd(Date eventEnd) {
		this.eventEnd = eventEnd;
	}

	/**
	 * @return
	 */
	public int getAcceptedSupplierCount() {
		return acceptedSupplierCount;
	}

	/**
	 * @param acceptedSupplierCount
	 */
	public void setAcceptedSupplierCount(int acceptedSupplierCount) {
		this.acceptedSupplierCount = acceptedSupplierCount;
	}

	/**
	 * @return the publishDate
	 */
	public Date getPublishDate() {
		return publishDate;
	}

	/**
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	/**
	 * @return the deliveryDate
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the visibility
	 */
	public EventVisibilityType getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(EventVisibilityType visibility) {
		this.visibility = visibility;
	}

	/**
	 * @return the validityDays
	 */
	public Integer getValidityDays() {
		return validityDays;
	}

	/**
	 * @param validityDays the validityDays to set
	 */
	public void setValidityDays(Integer validityDays) {
		this.validityDays = validityDays;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public RfxTypes getEventType() {
		return eventType;
	}

	public void setEventType(RfxTypes eventType) {
		this.eventType = eventType;
	}

	public BigDecimal getEstimatedBudget() {
		return estimatedBudget;
	}

	public void setEstimatedBudget(BigDecimal estimatedBudget) {
		this.estimatedBudget = estimatedBudget;
	}

	public BigDecimal getHistricAmount() {
		return histricAmount;
	}

	public void setHistricAmount(BigDecimal histricAmount) {
		this.histricAmount = histricAmount;
	}

	public BigDecimal getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(BigDecimal availableAmount) {
		this.availableAmount = availableAmount;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getAssoiciateOwner() {
		return assoiciateOwner;
	}

	public void setAssoiciateOwner(String assoiciateOwner) {
		this.assoiciateOwner = assoiciateOwner;
	}

	public String getUnmaskOwner() {
		return unmaskOwner;
	}

	public void setUnmaskOwner(String unmaskOwner) {
		this.unmaskOwner = unmaskOwner;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getAuctionType() {
		return auctionType;
	}

	public void setAuctionType(String auctionType) {
		this.auctionType = auctionType;
	}

	public BigDecimal getEventFees() {
		return eventFees;
	}

	public void setEventFees(BigDecimal eventFees) {
		this.eventFees = eventFees;
	}

	public BigDecimal getDeposite() {
		return deposite;
	}

	public void setDeposite(BigDecimal deposite) {
		this.deposite = deposite;
	}

	public String getSubStatus() {
		return subStatus;
	}

	public void setSubStatus(String subStatus) {
		this.subStatus = subStatus;
	}

	public String getAwardedSupplier() {
		return awardedSupplier;
	}

	public void setAwardedSupplier(String awardedSupplier) {
		this.awardedSupplier = awardedSupplier;
	}

	public String getConcluded() {
		return concluded;
	}

	public void setConcluded(String concluded) {
		this.concluded = concluded;
	}

	public String getPushToEvent() {
		return pushToEvent;
	}

	public void setPushToEvent(String pushToEvent) {
		this.pushToEvent = pushToEvent;
	}

	public String getPushToPr() {
		return pushToPr;
	}

	public void setPushToPr(String pushToPr) {
		this.pushToPr = pushToPr;
	}

	public String getAwardedDate() {
		return awardedDate;
	}

	public void setAwardedDate(String awardedDate) {
		this.awardedDate = awardedDate;
	}

	public String getPushDate() {
		return pushDate;
	}

	public void setPushDate(String pushDate) {
		this.pushDate = pushDate;
	}

	public String getConcludedaDate() {
		return concludedaDate;
	}

	public void setConcludedaDate(String concludedaDate) {
		this.concludedaDate = concludedaDate;
	}

	public String getAvarageBidSubmited() {
		return avarageBidSubmited;
	}

	public void setAvarageBidSubmited(String avarageBidSubmited) {
		this.avarageBidSubmited = avarageBidSubmited;
	}

	public BigDecimal getParticipationFees() {
		return participationFees;
	}

	public void setParticipationFees(BigDecimal participationFees) {
		this.participationFees = participationFees;
	}

	public Date getAwardDate() {
		return awardDate;
	}

	public void setAwardDate(Date awardDate) {
		this.awardDate = awardDate;
	}

	public Date getEventPushDate() {
		return eventPushDate;
	}

	public void setEventPushDate(Date eventPushDate) {
		this.eventPushDate = eventPushDate;
	}

	public Date getEventConcludeDate() {
		return eventConcludeDate;
	}

	public void setEventConcludeDate(Date eventConcludeDate) {
		this.eventConcludeDate = eventConcludeDate;
	}

	public int getPreViewSupplierCount() {
		return preViewSupplierCount;
	}

	public void setPreViewSupplierCount(int preViewSupplierCount) {
		this.preViewSupplierCount = preViewSupplierCount;
	}

	public int getRejectedSupplierCount() {
		return rejectedSupplierCount;
	}

	public void setRejectedSupplierCount(int rejectedSupplierCount) {
		this.rejectedSupplierCount = rejectedSupplierCount;
	}

	public int getDisqualifedSuppliers() {
		return disqualifedSuppliers;
	}

	public void setDisqualifedSuppliers(int disqualifedSuppliers) {
		this.disqualifedSuppliers = disqualifedSuppliers;
	}

	/**
	 * @return the memberType
	 */
	public String getMemberType() {
		return memberType;
	}

	/**
	 * @param memberType the memberType to set
	 */
	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	public String getAddressTitle() {
		return addressTitle;
	}

	public void setAddressTitle(String addressTitle) {
		this.addressTitle = addressTitle;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public BigDecimal getAvgGrandTotal() {
		return avgGrandTotal;
	}

	public void setAvgGrandTotal(BigDecimal avgGrandTotal) {
		this.avgGrandTotal = avgGrandTotal;
	}

	public Date getPrPushDate() {
		return prPushDate;
	}

	public void setPrPushDate(Date prPushDate) {
		this.prPushDate = prPushDate;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndustryCategory() {
		return industryCategory;
	}

	public void setIndustryCategory(String industryCategory) {
		this.industryCategory = industryCategory;
	}

	public String getReferanceNumber() {
		return referanceNumber;
	}

	public void setReferanceNumber(String referanceNumber) {
		this.referanceNumber = referanceNumber;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public boolean isTemplateActive() {
		return templateActive;
	}

	public void setTemplateActive(boolean templateActive) {
		this.templateActive = templateActive;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getEventDecimal() {
		return eventDecimal;
	}

	public void setEventDecimal(String eventDecimal) {
		this.eventDecimal = eventDecimal;
	}

	public Boolean getViewUnmaskSupplerName() {
		return viewUnmaskSupplerName;
	}

	public void setViewUnmaskSupplerName(Boolean viewUnmaskSupplerName) {
		this.viewUnmaskSupplerName = viewUnmaskSupplerName;
	}

	public String getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(String eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	public String getEventEndDate() {
		return eventEndDate;
	}

	public void setEventEndDate(String eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	public String getProcurementMethod() {
		return procurementMethod;
	}

	public void setProcurementMethod(String procurementMethod) {
		this.procurementMethod = procurementMethod;
	}

	public String getProcurementCategories() {
		return procurementCategories;
	}

	public void setProcurementCategories(String procurementCategories) {
		this.procurementCategories = procurementCategories;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	
	public GroupCode getGetGroupCode() {
		return getGroupCode;
	}

	public void setGetGroupCode(GroupCode getGroupCode) {
		this.getGroupCode = getGroupCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DraftEventPojo other = (DraftEventPojo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
