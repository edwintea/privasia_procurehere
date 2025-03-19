package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.DurationType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SuspensionType;
import com.privasia.procurehere.core.enums.TimeExtensionType;
import com.privasia.procurehere.core.utils.CustomDateSerializer;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

public class
EventPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7754069987497100448L;

	private String id;
	private String eventId;
	private String eventOwner;
	private String companyName;
	private String ownerLine1;
	private String ownerLine2;
	private String ownerCity;
	private String ownerState;
	private String ownerCountry;
	private String eventOwnerEmail;
	private String ownerPhoneNumber;
	private String referanceNumber;
	private String eventName;
	private EventVisibilityType eventVisibility;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventStart;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date eventEnd;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date eventPublishDate;
	private Integer submissionValidityDays;
	private BigDecimal participationFees;
	private String participationFeeCurrency;

	private String deliveryTitle;

	private String deliveryLine1;

	private String deliveryLine2;

	private String deliveryCity;

	private String deliveryZip;

	private String deliveryState;

	private String deliveryCountry;

	private String tenantId;

	private String createdBy;
	private String modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date modifiedDate;

	private EventStatus status;

	private String eventDescription;

	private String baseCurrency;

	private String decimal;

	private String costCenter;

	private BigDecimal budgetAmount;

	private BigDecimal historicaAmount;

	private String paymentTerm;

	private String template;

	private SuspensionType suspensionType;

	private String cancelReason;

	private String suspendRemarks;

	String previousEventId;

	String nextEventId;

	private RfxTypes previousEventType;

	private RfxTypes nextEventType;

	private String businessUnit;

	private String deliveryDate;

	private String concludeRemarks;

	private Date concludeDate;

	private String concludeBy;

	private Boolean disableMasking = Boolean.FALSE;

	private String unMaskedUser;

	private BigDecimal deposit;

	private String depositCurrency;

	private AuctionType auctionType;

	private Boolean documentReq;

	private Boolean meetingReq;

	private Boolean questionnaires;

	private Boolean billOfQuantity;

	private Boolean scheduleOfRate;

	private Boolean erpEnable;

	private BuyerAddress deliveryAddress;

	private String currencyCode;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date auctionResumeDateTime;

	private DurationType auctionDurationType;

	private DurationType auctionStartDelayType;

	private Integer auctionDuration;

	private Integer auctionStartDelay;

	private Boolean auctionStartRelative;
	private TimeExtensionType timeExtensionType;

	private DurationType timeExtensionDurationType;

	private Integer timeExtensionDuration;

	private DurationType timeExtensionLeadingBidType;

	private Integer timeExtensionLeadingBidValue;

	private Integer extensionCount;

	private Integer bidderDisqualify;

	private Boolean autoDisqualify;
	private String deliveryAddressState;
	private String deliveryAddressCountry;

	private Date eventDeliveryDate;

	private BigDecimal minimumSupplierRating;

	private BigDecimal maximumSupplierRating;

	private Boolean enableSupplierDeclaration = Boolean.FALSE;

	private Boolean disableTotalAmount = Boolean.FALSE;

	EventPojo() {

	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * This is for Self Invite Supplier validation
	 */
	public EventPojo(BigDecimal minimumSupplierRating, BigDecimal maximumSupplierRating) {
		super();
		this.minimumSupplierRating = minimumSupplierRating;
		this.maximumSupplierRating = maximumSupplierRating;
	}

	/**
	 * This is used for Supplier Invite Summary Page be careful before change
	 */
	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Boolean erpEnable, String eventBuyerName, Boolean enableSupplierDeclaration) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.enableSupplierDeclaration = enableSupplierDeclaration;
	}

	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Boolean erpEnable, String eventBuyerName, Boolean enableSupplierDeclaration, SuspensionType suspensionType) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.enableSupplierDeclaration = enableSupplierDeclaration;
		this.suspensionType = suspensionType;
	}


	// This is the updated constructor with schedule of rate

	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity,Boolean scheduleOfRate ,Boolean erpEnable, String eventBuyerName, Boolean enableSupplierDeclaration, SuspensionType suspensionType) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.enableSupplierDeclaration = enableSupplierDeclaration;
		this.suspensionType = suspensionType;
		this.scheduleOfRate = scheduleOfRate;
	}

	/**
	 * This is used for Rfa Supplier Invite Summary Page be careful before change
	 */
	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, AuctionType rfaAuctionType, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Date auctionResumeDateTime, Boolean erpEnable, String eventBuyerName, Boolean enableSupplierDeclaration) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.auctionType = rfaAuctionType;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.auctionResumeDateTime = auctionResumeDateTime;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.enableSupplierDeclaration = enableSupplierDeclaration;
	}

	/**
	 * This is used for Supplier last Summary Page be careful before change
	 */
	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Boolean erpEnable, String eventBuyerName, BuyerAddress deliveryAddress, String currencyCode, String businessUnit, String deliveryAddressState, String deliveryAddressCountry, BigDecimal deposit, String depositCurrency) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;

		this.currencyCode = currencyCode;
		this.businessUnit = businessUnit;
		this.deliveryAddressState = deliveryAddressState;
		this.deliveryAddressCountry = deliveryAddressCountry;
		this.deposit = deposit;
		this.depositCurrency = depositCurrency;

		if (deliveryAddress != null) {
			this.deliveryAddress = new BuyerAddress();
			this.deliveryAddress.setTitle(deliveryAddress.getTitle());
			this.deliveryAddress.setLine1(deliveryAddress.getLine1());
			this.deliveryAddress.setLine2(deliveryAddress.getLine2());
			this.deliveryAddress.setCity(deliveryAddress.getCity());
			this.deliveryAddress.setZip(deliveryAddress.getZip());

		}

	}

	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Boolean erpEnable, String eventBuyerName, BuyerAddress deliveryAddress, String currencyCode, String businessUnit, String deliveryAddressState, String deliveryAddressCountry, BigDecimal deposit, String depositCurrency, SuspensionType suspensionType) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;

		this.currencyCode = currencyCode;
		this.businessUnit = businessUnit;
		this.deliveryAddressState = deliveryAddressState;
		this.deliveryAddressCountry = deliveryAddressCountry;
		this.deposit = deposit;
		this.depositCurrency = depositCurrency;

		if (deliveryAddress != null) {
			this.deliveryAddress = new BuyerAddress();
			this.deliveryAddress.setTitle(deliveryAddress.getTitle());
			this.deliveryAddress.setLine1(deliveryAddress.getLine1());
			this.deliveryAddress.setLine2(deliveryAddress.getLine2());
			this.deliveryAddress.setCity(deliveryAddress.getCity());
			this.deliveryAddress.setZip(deliveryAddress.getZip());

		}
		this.suspensionType = suspensionType;
	}

	/**
	 * This is used for Rfa Supplier last Summary Page be careful before change
	 */
	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, AuctionType rfaAuctionType, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Date auctionResumeDateTime, Boolean erpEnable, String eventBuyerName, BuyerAddress deliveryAddress, String currencyCode, String businessUnit, String deliveryAddressState, String deliveryAddressCountry, DurationType auctionDurationType, DurationType auctionStartDelayType, Integer auctionDuration, Integer auctionStartDelay, Boolean auctionStartRelative, TimeExtensionType timeExtensionType, DurationType timeExtensionDurationType, Integer timeExtensionDuration, DurationType timeExtensionLeadingBidType, Integer timeExtensionLeadingBidValue, Integer extensionCount, Integer bidderDisqualify, Boolean autoDisqualify, BigDecimal deposit, String depositCurrency) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.auctionType = rfaAuctionType;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.auctionResumeDateTime = auctionResumeDateTime;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.deliveryAddress = deliveryAddress;
		this.currencyCode = currencyCode;
		this.businessUnit = businessUnit;

		this.auctionDurationType = auctionDurationType;

		this.auctionStartDelayType = auctionStartDelayType;

		this.auctionDuration = auctionDuration;

		this.auctionStartDelay = auctionStartDelay;

		this.auctionStartRelative = auctionStartRelative;

		this.timeExtensionType = timeExtensionType;

		this.timeExtensionDurationType = timeExtensionDurationType;

		this.timeExtensionDuration = timeExtensionDuration;

		this.timeExtensionLeadingBidType = timeExtensionLeadingBidType;

		this.timeExtensionLeadingBidValue = timeExtensionLeadingBidValue;

		this.extensionCount = extensionCount;

		this.bidderDisqualify = bidderDisqualify;

		this.autoDisqualify = autoDisqualify;
		this.deliveryAddressState = deliveryAddressState;
		this.deliveryAddressCountry = deliveryAddressCountry;
		this.deposit = deposit;
		this.depositCurrency = depositCurrency;

		if (deliveryAddress != null) {
			this.deliveryAddress = new BuyerAddress();
			this.deliveryAddress.setTitle(deliveryAddress.getTitle());
			this.deliveryAddress.setLine1(deliveryAddress.getLine1());
			this.deliveryAddress.setLine2(deliveryAddress.getLine2());
			this.deliveryAddress.setCity(deliveryAddress.getCity());
			this.deliveryAddress.setZip(deliveryAddress.getZip());

		}

	}

	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, AuctionType rfaAuctionType, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Date auctionResumeDateTime, Boolean erpEnable, String eventBuyerName, BuyerAddress deliveryAddress, String currencyCode, String businessUnit, String deliveryAddressState, String deliveryAddressCountry, DurationType auctionDurationType, DurationType auctionStartDelayType, Integer auctionDuration, Integer auctionStartDelay, Boolean auctionStartRelative, TimeExtensionType timeExtensionType, DurationType timeExtensionDurationType, Integer timeExtensionDuration, DurationType timeExtensionLeadingBidType, Integer timeExtensionLeadingBidValue, Integer extensionCount, Integer bidderDisqualify, Boolean autoDisqualify, BigDecimal deposit, String depositCurrency, SuspensionType suspensionType) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.auctionType = rfaAuctionType;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.auctionResumeDateTime = auctionResumeDateTime;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.deliveryAddress = deliveryAddress;
		this.currencyCode = currencyCode;
		this.businessUnit = businessUnit;

		this.auctionDurationType = auctionDurationType;

		this.auctionStartDelayType = auctionStartDelayType;

		this.auctionDuration = auctionDuration;

		this.auctionStartDelay = auctionStartDelay;

		this.auctionStartRelative = auctionStartRelative;

		this.timeExtensionType = timeExtensionType;

		this.timeExtensionDurationType = timeExtensionDurationType;

		this.timeExtensionDuration = timeExtensionDuration;

		this.timeExtensionLeadingBidType = timeExtensionLeadingBidType;

		this.timeExtensionLeadingBidValue = timeExtensionLeadingBidValue;

		this.extensionCount = extensionCount;

		this.bidderDisqualify = bidderDisqualify;

		this.autoDisqualify = autoDisqualify;
		this.deliveryAddressState = deliveryAddressState;
		this.deliveryAddressCountry = deliveryAddressCountry;
		this.deposit = deposit;
		this.depositCurrency = depositCurrency;

		if (deliveryAddress != null) {
			this.deliveryAddress = new BuyerAddress();
			this.deliveryAddress.setTitle(deliveryAddress.getTitle());
			this.deliveryAddress.setLine1(deliveryAddress.getLine1());
			this.deliveryAddress.setLine2(deliveryAddress.getLine2());
			this.deliveryAddress.setCity(deliveryAddress.getCity());
			this.deliveryAddress.setZip(deliveryAddress.getZip());

		}
		this.suspensionType = suspensionType;
	}

	/**
	 * This is used for public event summary page
	 */
	public EventPojo(String id, String eventId, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Date eventDeliveryDate, String tenantId, EventStatus status, String paymentTerm, String baseCurrency, String currencyCode, String eventBuyerName, BigDecimal participationFees, String participationFeeCurrency) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.eventDeliveryDate = eventDeliveryDate;
		this.tenantId = tenantId;
		this.status = status;
		this.paymentTerm = paymentTerm;
		this.baseCurrency = baseCurrency;
		this.currencyCode = currencyCode;
		this.companyName = eventBuyerName;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
	}

	/**
	 * This is used for rfa public event summary page
	 */
	public EventPojo(String id, String eventId, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Date eventDeliveryDate, String tenantId, EventStatus status, String paymentTerm, String baseCurrency, String currencyCode, String eventBuyerName, AuctionType auctionType, TimeExtensionType timeExtensionType) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.eventDeliveryDate = eventDeliveryDate;
		this.tenantId = tenantId;
		this.status = status;
		this.paymentTerm = paymentTerm;
		this.baseCurrency = baseCurrency;
		this.currencyCode = currencyCode;
		this.companyName = eventBuyerName;
		this.auctionType = auctionType;
		this.timeExtensionType = timeExtensionType;
	}

	/**
	 * This is used for RFA supplier side Bill of quantity Page be careful before change PH-1701
	 */
	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, AuctionType rfaAuctionType, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Date auctionResumeDateTime, Boolean erpEnable, String eventBuyerName, Boolean enableSupplierDeclaration, Boolean disableTotalAmount) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.auctionType = rfaAuctionType;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.auctionResumeDateTime = auctionResumeDateTime;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.enableSupplierDeclaration = enableSupplierDeclaration;
		this.disableTotalAmount = disableTotalAmount;
	}

	/**
	 * This is used for RFP,RFQ, RFT supplier side Bill of quantity Page be careful before change PH-1701
	 */
	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Boolean erpEnable, String eventBuyerName, Boolean enableSupplierDeclaration, Boolean disableTotalAmount) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.enableSupplierDeclaration = enableSupplierDeclaration;
		this.disableTotalAmount = disableTotalAmount;
	}

	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Boolean erpEnable, String eventBuyerName, Boolean enableSupplierDeclaration, Boolean disableTotalAmount, SuspensionType suspensionType) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.enableSupplierDeclaration = enableSupplierDeclaration;
		this.disableTotalAmount = disableTotalAmount;
		this.suspensionType = suspensionType;
	}

	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, AuctionType rfaAuctionType, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Date auctionResumeDateTime, Boolean erpEnable, String eventBuyerName, Boolean enableSupplierDeclaration, Boolean disableTotalAmount, SuspensionType suspensionType) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.auctionType = rfaAuctionType;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.auctionResumeDateTime = auctionResumeDateTime;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.enableSupplierDeclaration = enableSupplierDeclaration;
		this.disableTotalAmount = disableTotalAmount;
		this.suspensionType = suspensionType;
	}

	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, AuctionType rfaAuctionType, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Boolean scheduleOfRate,Date auctionResumeDateTime, Boolean erpEnable, String eventBuyerName, Boolean enableSupplierDeclaration, Boolean disableTotalAmount, SuspensionType suspensionType) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.auctionType = rfaAuctionType;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.auctionResumeDateTime = auctionResumeDateTime;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.enableSupplierDeclaration = enableSupplierDeclaration;
		this.disableTotalAmount = disableTotalAmount;
		this.suspensionType = suspensionType;
		this.scheduleOfRate = scheduleOfRate;
	}


	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Boolean scheduleOfRate,Boolean erpEnable, String eventBuyerName, Boolean enableSupplierDeclaration, Boolean disableTotalAmount, SuspensionType suspensionType) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.enableSupplierDeclaration = enableSupplierDeclaration;
		this.disableTotalAmount = disableTotalAmount;
		this.suspensionType = suspensionType;
		this.scheduleOfRate = scheduleOfRate;
	}

	/**
	 * @param id
	 * @param eventId
	 * @param eventOwner
	 * @param ownerLine1
	 * @param ownerLine2
	 * @param ownerCity
	 * @param ownerState
	 * @param ownerCountry
	 * @param eventOwnerEmail
	 * @param phoneNumber
	 * @param referanceNumber
	 * @param eventName
	 * @param eventVisibility
	 * @param eventStart
	 * @param eventEnd
	 * @param eventPublishDate
	 * @param submissionValidityDays
	 * @param participationFees
	 * @param participationFeeCurrency
	 * @param tenantId
	 * @param status
	 * @param eventDescription
	 * @param baseCurrency
	 * @param decimal
	 * @param paymentTerm
	 * @param documentReq
	 * @param meetingReq
	 * @param questionnaires
	 * @param billOfQuantity
	 * @param erpEnable
	 * @param eventBuyerName
	 * @param deliveryAddress
	 * @param currencyCode
	 * @param businessUnit
	 * @param deliveryAddressState
	 * @param deliveryAddressCountry
	 * @param deposit
	 * @param depositCurrency
	 * @param suspensionType
	 * This is used for supplier submission summary page
	 */
	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2,
					 String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber,
					 String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd,
					 Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency,
					 String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm,
					 Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Boolean scheduleOfRate,
					 Boolean erpEnable, String eventBuyerName, BuyerAddress deliveryAddress, String currencyCode, String businessUnit,
					 String deliveryAddressState, String deliveryAddressCountry, BigDecimal deposit, String depositCurrency, SuspensionType suspensionType) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;

		this.currencyCode = currencyCode;
		this.businessUnit = businessUnit;
		this.deliveryAddressState = deliveryAddressState;
		this.deliveryAddressCountry = deliveryAddressCountry;
		this.deposit = deposit;
		this.depositCurrency = depositCurrency;

		if (deliveryAddress != null) {
			this.deliveryAddress = new BuyerAddress();
			this.deliveryAddress.setTitle(deliveryAddress.getTitle());
			this.deliveryAddress.setLine1(deliveryAddress.getLine1());
			this.deliveryAddress.setLine2(deliveryAddress.getLine2());
			this.deliveryAddress.setCity(deliveryAddress.getCity());
			this.deliveryAddress.setZip(deliveryAddress.getZip());

		}
		this.suspensionType = suspensionType;
		this.scheduleOfRate = scheduleOfRate;
	}


	// This is the changed FOR RFA supplier submission , be careful to change this
	public EventPojo(String id, String eventId, String eventOwner, String ownerLine1, String ownerLine2, String ownerCity, String ownerState, String ownerCountry, String eventOwnerEmail, String phoneNumber, String referanceNumber, String eventName, EventVisibilityType eventVisibility, Date eventStart, Date eventEnd, Date eventPublishDate, Integer submissionValidityDays, BigDecimal participationFees, String participationFeeCurrency, String tenantId, EventStatus status, String eventDescription, String baseCurrency, String decimal, String paymentTerm, AuctionType rfaAuctionType, Boolean documentReq, Boolean meetingReq, Boolean questionnaires, Boolean billOfQuantity, Boolean scheduleOfRate,Date auctionResumeDateTime, Boolean erpEnable, String eventBuyerName, BuyerAddress deliveryAddress, String currencyCode, String businessUnit, String deliveryAddressState, String deliveryAddressCountry, DurationType auctionDurationType, DurationType auctionStartDelayType, Integer auctionDuration, Integer auctionStartDelay, Boolean auctionStartRelative, TimeExtensionType timeExtensionType, DurationType timeExtensionDurationType, Integer timeExtensionDuration, DurationType timeExtensionLeadingBidType, Integer timeExtensionLeadingBidValue, Integer extensionCount, Integer bidderDisqualify, Boolean autoDisqualify, BigDecimal deposit, String depositCurrency) {
		super();
		this.id = id;
		this.eventId = eventId;
		this.eventOwner = eventOwner;
		this.ownerLine1 = ownerLine1;
		this.ownerLine2 = ownerLine2;
		this.ownerCity = ownerCity;
		this.ownerState = ownerState;
		this.ownerCountry = ownerCountry;
		this.eventOwnerEmail = eventOwnerEmail;
		this.ownerPhoneNumber = phoneNumber;
		this.referanceNumber = referanceNumber;
		this.eventName = eventName;
		this.eventVisibility = eventVisibility;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.eventPublishDate = eventPublishDate;
		this.submissionValidityDays = submissionValidityDays;
		this.participationFees = participationFees;
		this.participationFeeCurrency = participationFeeCurrency;
		this.auctionType = rfaAuctionType;
		this.tenantId = tenantId;
		this.status = status;
		this.eventDescription = eventDescription;
		this.baseCurrency = baseCurrency;
		this.decimal = decimal;
		this.paymentTerm = paymentTerm;
		this.documentReq = documentReq;
		this.meetingReq = meetingReq;
		this.questionnaires = questionnaires;
		this.billOfQuantity = billOfQuantity;
		this.auctionResumeDateTime = auctionResumeDateTime;
		this.erpEnable = erpEnable;
		this.companyName = eventBuyerName;
		this.deliveryAddress = deliveryAddress;
		this.currencyCode = currencyCode;
		this.businessUnit = businessUnit;

		this.auctionDurationType = auctionDurationType;

		this.auctionStartDelayType = auctionStartDelayType;

		this.auctionDuration = auctionDuration;

		this.auctionStartDelay = auctionStartDelay;

		this.auctionStartRelative = auctionStartRelative;

		this.timeExtensionType = timeExtensionType;

		this.timeExtensionDurationType = timeExtensionDurationType;

		this.timeExtensionDuration = timeExtensionDuration;

		this.timeExtensionLeadingBidType = timeExtensionLeadingBidType;

		this.timeExtensionLeadingBidValue = timeExtensionLeadingBidValue;

		this.extensionCount = extensionCount;

		this.bidderDisqualify = bidderDisqualify;

		this.autoDisqualify = autoDisqualify;
		this.deliveryAddressState = deliveryAddressState;
		this.deliveryAddressCountry = deliveryAddressCountry;
		this.deposit = deposit;
		this.depositCurrency = depositCurrency;

		if (deliveryAddress != null) {
			this.deliveryAddress = new BuyerAddress();
			this.deliveryAddress.setTitle(deliveryAddress.getTitle());
			this.deliveryAddress.setLine1(deliveryAddress.getLine1());
			this.deliveryAddress.setLine2(deliveryAddress.getLine2());
			this.deliveryAddress.setCity(deliveryAddress.getCity());
			this.deliveryAddress.setZip(deliveryAddress.getZip());

		}
		this.scheduleOfRate = scheduleOfRate;

	}

	public EventPojo(String id, String eventName) {
		super();
		this.id = id;
		this.eventName = eventName;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the eventOwner
	 */
	public String getEventOwner() {
		return eventOwner;
	}

	/**
	 * @param eventOwner the eventOwner to set
	 */
	public void setEventOwner(String eventOwner) {
		this.eventOwner = eventOwner;
	}

	/**
	 * @return the ownerLine1
	 */
	public String getOwnerLine1() {
		return ownerLine1;
	}

	/**
	 * @param ownerLine1 the ownerLine1 to set
	 */
	public void setOwnerLine1(String ownerLine1) {
		this.ownerLine1 = ownerLine1;
	}

	/**
	 * @return the ownerLine2
	 */
	public String getOwnerLine2() {
		return ownerLine2;
	}

	/**
	 * @param ownerLine2 the ownerLine2 to set
	 */
	public void setOwnerLine2(String ownerLine2) {
		this.ownerLine2 = ownerLine2;
	}

	/**
	 * @return the ownerCity
	 */
	public String getOwnerCity() {
		return ownerCity;
	}

	/**
	 * @param ownerCity the ownerCity to set
	 */
	public void setOwnerCity(String ownerCity) {
		this.ownerCity = ownerCity;
	}

	/**
	 * @return the ownerState
	 */
	public String getOwnerState() {
		return ownerState;
	}

	/**
	 * @param ownerState the ownerState to set
	 */
	public void setOwnerState(String ownerState) {
		this.ownerState = ownerState;
	}

	/**
	 * @return the ownerCountry
	 */
	public String getOwnerCountry() {
		return ownerCountry;
	}

	/**
	 * @param ownerCountry the ownerCountry to set
	 */
	public void setOwnerCountry(String ownerCountry) {
		this.ownerCountry = ownerCountry;
	}

	/**
	 * @return the eventOwnerEmail
	 */
	public String getEventOwnerEmail() {
		return eventOwnerEmail;
	}

	/**
	 * @param eventOwnerEmail the eventOwnerEmail to set
	 */
	public void setEventOwnerEmail(String eventOwnerEmail) {
		this.eventOwnerEmail = eventOwnerEmail;
	}

	/**
	 * @return the referanceNumber
	 */
	public String getReferanceNumber() {
		return referanceNumber;
	}

	/**
	 * @param referanceNumber the referanceNumber to set
	 */
	public void setReferanceNumber(String referanceNumber) {
		this.referanceNumber = referanceNumber;
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

	/**
	 * @param eventEnd the eventEnd to set
	 */
	public void setEventEnd(Date eventEnd) {
		this.eventEnd = eventEnd;
	}

	/**
	 * @return the eventPublishDate
	 */
	public Date getEventPublishDate() {
		return eventPublishDate;
	}

	/**
	 * @param eventPublishDate the eventPublishDate to set
	 */
	public void setEventPublishDate(Date eventPublishDate) {
		this.eventPublishDate = eventPublishDate;
	}

	/**
	 * @return the submissionValidityDays
	 */
	public Integer getSubmissionValidityDays() {
		return submissionValidityDays;
	}

	/**
	 * @param submissionValidityDays the submissionValidityDays to set
	 */
	public void setSubmissionValidityDays(Integer submissionValidityDays) {
		this.submissionValidityDays = submissionValidityDays;
	}

	/**
	 * @return the participationFees
	 */
	public BigDecimal getParticipationFees() {
		return participationFees;
	}

	/**
	 * @param participationFees the participationFees to set
	 */
	public void setParticipationFees(BigDecimal participationFees) {
		this.participationFees = participationFees;
	}

	/**
	 * @return the participationFeeCurrency
	 */
	public String getParticipationFeeCurrency() {
		return participationFeeCurrency;
	}

	/**
	 * @param participationFeeCurrency the participationFeeCurrency to set
	 */
	public void setParticipationFeeCurrency(String participationFeeCurrency) {
		this.participationFeeCurrency = participationFeeCurrency;
	}

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
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

	/**
	 * @return the baseCurrency
	 */
	public String getBaseCurrency() {
		return baseCurrency;
	}

	/**
	 * @param baseCurrency the baseCurrency to set
	 */
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	/**
	 * @return the decimal
	 */
	public String getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return the costCenter
	 */
	public String getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
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
	 * @return the historicaAmount
	 */
	public BigDecimal getHistoricaAmount() {
		return historicaAmount;
	}

	/**
	 * @param historicaAmount the historicaAmount to set
	 */
	public void setHistoricaAmount(BigDecimal historicaAmount) {
		this.historicaAmount = historicaAmount;
	}

	/**
	 * @return the paymentTerm
	 */
	public String getPaymentTerm() {
		return paymentTerm;
	}

	/**
	 * @param paymentTerm the paymentTerm to set
	 */
	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @return the suspensionType
	 */
	public SuspensionType getSuspensionType() {
		return suspensionType;
	}

	/**
	 * @param suspensionType the suspensionType to set
	 */
	public void setSuspensionType(SuspensionType suspensionType) {
		this.suspensionType = suspensionType;
	}

	/**
	 * @return the cancelReason
	 */
	public String getCancelReason() {
		return cancelReason;
	}

	/**
	 * @param cancelReason the cancelReason to set
	 */
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	/**
	 * @return the suspendRemarks
	 */
	public String getSuspendRemarks() {
		return suspendRemarks;
	}

	/**
	 * @param suspendRemarks the suspendRemarks to set
	 */
	public void setSuspendRemarks(String suspendRemarks) {
		this.suspendRemarks = suspendRemarks;
	}

	/**
	 * @return the previousEventId
	 */
	public String getPreviousEventId() {
		return previousEventId;
	}

	/**
	 * @param previousEventId the previousEventId to set
	 */
	public void setPreviousEventId(String previousEventId) {
		this.previousEventId = previousEventId;
	}

	/**
	 * @return the nextEventId
	 */
	public String getNextEventId() {
		return nextEventId;
	}

	/**
	 * @param nextEventId the nextEventId to set
	 */
	public void setNextEventId(String nextEventId) {
		this.nextEventId = nextEventId;
	}

	/**
	 * @return the previousEventType
	 */
	public RfxTypes getPreviousEventType() {
		return previousEventType;
	}

	/**
	 * @param previousEventType the previousEventType to set
	 */
	public void setPreviousEventType(RfxTypes previousEventType) {
		this.previousEventType = previousEventType;
	}

	/**
	 * @return the nextEventType
	 */
	public RfxTypes getNextEventType() {
		return nextEventType;
	}

	/**
	 * @param nextEventType the nextEventType to set
	 */
	public void setNextEventType(RfxTypes nextEventType) {
		this.nextEventType = nextEventType;
	}

	/**
	 * @return the businessUnit
	 */
	public String getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return the deliveryDate
	 */
	public String getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the concludeRemarks
	 */
	public String getConcludeRemarks() {
		return concludeRemarks;
	}

	/**
	 * @param concludeRemarks the concludeRemarks to set
	 */
	public void setConcludeRemarks(String concludeRemarks) {
		this.concludeRemarks = concludeRemarks;
	}

	/**
	 * @return the concludeDate
	 */
	public Date getConcludeDate() {
		return concludeDate;
	}

	/**
	 * @param concludeDate the concludeDate to set
	 */
	public void setConcludeDate(Date concludeDate) {
		this.concludeDate = concludeDate;
	}

	/**
	 * @return the concludeBy
	 */
	public String getConcludeBy() {
		return concludeBy;
	}

	/**
	 * @param concludeBy the concludeBy to set
	 */
	public void setConcludeBy(String concludeBy) {
		this.concludeBy = concludeBy;
	}

	/**
	 * @return the disableMasking
	 */
	public Boolean getDisableMasking() {
		return disableMasking;
	}

	/**
	 * @param disableMasking the disableMasking to set
	 */
	public void setDisableMasking(Boolean disableMasking) {
		this.disableMasking = disableMasking;
	}

	/**
	 * @return the unMaskedUser
	 */
	public String getUnMaskedUser() {
		return unMaskedUser;
	}

	/**
	 * @param unMaskedUser the unMaskedUser to set
	 */
	public void setUnMaskedUser(String unMaskedUser) {
		this.unMaskedUser = unMaskedUser;
	}

	/**
	 * @return the deposit
	 */
	public BigDecimal getDeposit() {
		return deposit;
	}

	/**
	 * @param deposit the deposit to set
	 */
	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	/**
	 * @return the depositCurrency
	 */

	/**
	 * @return the depositCurrency
	 */
	public String getDepositCurrency() {
		return depositCurrency;
	}

	/**
	 * @param depositCurrency the depositCurrency to set
	 */
	public void setDepositCurrency(String depositCurrency) {
		this.depositCurrency = depositCurrency;
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
	 * @return the deliveryTitle
	 */
	public String getDeliveryTitle() {
		return deliveryTitle;
	}

	/**
	 * @param deliveryTitle the deliveryTitle to set
	 */
	public void setDeliveryTitle(String deliveryTitle) {
		this.deliveryTitle = deliveryTitle;
	}

	/**
	 * @return the deliveryLine1
	 */
	public String getDeliveryLine1() {
		return deliveryLine1;
	}

	/**
	 * @param deliveryLine1 the deliveryLine1 to set
	 */
	public void setDeliveryLine1(String deliveryLine1) {
		this.deliveryLine1 = deliveryLine1;
	}

	/**
	 * @return the deliveryLine2
	 */
	public String getDeliveryLine2() {
		return deliveryLine2;
	}

	/**
	 * @param deliveryLine2 the deliveryLine2 to set
	 */
	public void setDeliveryLine2(String deliveryLine2) {
		this.deliveryLine2 = deliveryLine2;
	}

	/**
	 * @return the deliveryCity
	 */
	public String getDeliveryCity() {
		return deliveryCity;
	}

	/**
	 * @param deliveryCity the deliveryCity to set
	 */
	public void setDeliveryCity(String deliveryCity) {
		this.deliveryCity = deliveryCity;
	}

	/**
	 * @return the deliveryZip
	 */
	public String getDeliveryZip() {
		return deliveryZip;
	}

	/**
	 * @param deliveryZip the deliveryZip to set
	 */
	public void setDeliveryZip(String deliveryZip) {
		this.deliveryZip = deliveryZip;
	}

	/**
	 * @return the deliveryState
	 */
	public String getDeliveryState() {
		return deliveryState;
	}

	/**
	 * @param deliveryState the deliveryState to set
	 */
	public void setDeliveryState(String deliveryState) {
		this.deliveryState = deliveryState;
	}

	/**
	 * @return the deliveryCountry
	 */
	public String getDeliveryCountry() {
		return deliveryCountry;
	}

	/**
	 * @param deliveryCountry the deliveryCountry to set
	 */
	public void setDeliveryCountry(String deliveryCountry) {
		this.deliveryCountry = deliveryCountry;
	}

	/**
	 * @return the ownerPhoneNumber
	 */
	public String getOwnerPhoneNumber() {
		return ownerPhoneNumber;
	}

	/**
	 * @param ownerPhoneNumber the ownerPhoneNumber to set
	 */
	public void setOwnerPhoneNumber(String ownerPhoneNumber) {
		this.ownerPhoneNumber = ownerPhoneNumber;
	}

	/**
	 * @return the documentReq
	 */
	public Boolean getDocumentReq() {
		return documentReq;
	}

	/**
	 * @param documentReq the documentReq to set
	 */
	public void setDocumentReq(Boolean documentReq) {
		this.documentReq = documentReq;
	}

	/**
	 * @return the meetingReq
	 */
	public Boolean getMeetingReq() {
		return meetingReq;
	}

	/**
	 * @param meetingReq the meetingReq to set
	 */
	public void setMeetingReq(Boolean meetingReq) {
		this.meetingReq = meetingReq;
	}

	/**
	 * @return the questionnaires
	 */
	public Boolean getQuestionnaires() {
		return questionnaires;
	}

	/**
	 * @param questionnaires the questionnaires to set
	 */
	public void setQuestionnaires(Boolean questionnaires) {
		this.questionnaires = questionnaires;
	}

	/**
	 * @return the billOfQuantity
	 */
	public Boolean getBillOfQuantity() {
		return billOfQuantity;
	}

	/**
	 * @param billOfQuantity the billOfQuantity to set
	 */
	public void setBillOfQuantity(Boolean billOfQuantity) {
		this.billOfQuantity = billOfQuantity;
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
	 * @return the erpEnable
	 */
	public Boolean getErpEnable() {
		return erpEnable;
	}

	/**
	 * @param erpEnable the erpEnable to set
	 */
	public void setErpEnable(Boolean erpEnable) {
		this.erpEnable = erpEnable;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the deliveryAddress
	 */
	public BuyerAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(BuyerAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
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
	 * @return the deliveryAddressState
	 */
	public String getDeliveryAddressState() {
		return deliveryAddressState;
	}

	/**
	 * @param deliveryAddressState the deliveryAddressState to set
	 */
	public void setDeliveryAddressState(String deliveryAddressState) {
		this.deliveryAddressState = deliveryAddressState;
	}

	/**
	 * @return the deliveryAddressCountry
	 */
	public String getDeliveryAddressCountry() {
		return deliveryAddressCountry;
	}

	/**
	 * @param deliveryAddressCountry the deliveryAddressCountry to set
	 */
	public void setDeliveryAddressCountry(String deliveryAddressCountry) {
		this.deliveryAddressCountry = deliveryAddressCountry;
	}

	/**
	 * @return the eventDeliveryDate
	 */
	public Date getEventDeliveryDate() {
		return eventDeliveryDate;
	}

	/**
	 * @param eventDeliveryDate the eventDeliveryDate to set
	 */
	public void setEventDeliveryDate(Date eventDeliveryDate) {
		this.eventDeliveryDate = eventDeliveryDate;
	}

	/**
	 * @return the minimumSupplierRating
	 */
	public BigDecimal getMinimumSupplierRating() {
		return minimumSupplierRating;
	}

	/**
	 * @param minimumSupplierRating the minimumSupplierRating to set
	 */
	public void setMinimumSupplierRating(BigDecimal minimumSupplierRating) {
		this.minimumSupplierRating = minimumSupplierRating;
	}

	/**
	 * @return the maximumSupplierRating
	 */
	public BigDecimal getMaximumSupplierRating() {
		return maximumSupplierRating;
	}

	/**
	 * @param maximumSupplierRating the maximumSupplierRating to set
	 */
	public void setMaximumSupplierRating(BigDecimal maximumSupplierRating) {
		this.maximumSupplierRating = maximumSupplierRating;
	}

	/**
	 * @return the enableSupplierDeclaration
	 */
	public Boolean getEnableSupplierDeclaration() {
		return enableSupplierDeclaration;
	}

	/**
	 * @param enableSupplierDeclaration the enableSupplierDeclaration to set
	 */
	public void setEnableSupplierDeclaration(Boolean enableSupplierDeclaration) {
		this.enableSupplierDeclaration = enableSupplierDeclaration;
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


	public Boolean getScheduleOfRate() {
		return scheduleOfRate;
	}

	public void setScheduleOfRate(Boolean scheduleOfRate) {
		this.scheduleOfRate = scheduleOfRate;
	}
}