package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.IndustryCategory;

public class EvaluationPojo implements Serializable {

	private static final long serialVersionUID = 4852650543746786532L;

	private String id;
	private String eventName;
	private String eventId;
	private String referenceNo;
	private String createdDate;
	private String type;
	private String eventOwner;
	private String owner;
	private String email;
	private String contactno;
	private String mobileno;
	private String companyName;

	private String referenceId;
	private String eventStart;
	private String diliveryDate;
	private String eventEnd;
	private List<EvaluationPojo> reminderDate;
	private List<EvaluationPojo> reminderStartDate;
	private String publishDate;
	private String visibility;
	private Integer validityDays;
	private BigDecimal participationFee;
	private List<IndustryCategoryPojo> category;

	private String correspondAddress;
	private String deliveryAddress;

	private String baseCurrency;
	private String paymentTerm;
	private String costCenter;
	private BigDecimal historicAmt;
	private String decimal;
	private String description;
	private BigDecimal budgetAmt;

	// EnvlopeDetails
	private String envlopName;
	private String envDescription;
	private String openType;
	private String envelopOwner;
	private String envelopOpener;
	private String location;
	private String openDate;

	// Time Extension
	private String auctionType;
	private String extentionType;
	private String extensionDuration;
	private String extensionTrigger;
	private Integer extensionRound;
	private String autoDisqualify;
	private Integer disqualifyCount;

	private String buyerName;

	private List<EvaluationContactsPojo> contacts;

	private List<EvaluationSuppliersPojo> suppliers;

	private List<EvaluationMeetingPojo> meetings;

	private List<EvaluationCqPojo> cqs;

	private List<EvaluationBqPojo> bqs;

	private List<EvaluationSorPojo> sors;

	private List<EvaluationEnvelopPojo> envelops;

	private List<EvaluationSuppliersBqPojo> bqSuppliers;

	private List<EvaluationSuppliersSorPojo> sorSuppliers;

	private List<EvaluationBqPojo> bqLeadCommentsList;

	private List<EvaluationTeamsPojo> evaluationTeam;

	private List<EvaluationApprovalsPojo> approvals;

	private List<EvaluationTimelinePojo> timelines;

	private List<EvaluationDocumentPojo> documents;

	private List<EvaluationEnvelopSuppliersPojo> envlopSuppliers;

	private List<EvaluationCommentsPojo> comments;

	private List<EvaluationAuctionRulePojo> auctionRules;

	private List<EvaluationAuditPojo> auditDetails;

	private String businesUnit;

	private List<SupplierMaskingPojo> supplierMaskingList;

	private Boolean isMask = true;

	private String internalRemarks;

	private Boolean siteVisit;

	List<IndustryCategoryPojo> industryCategoryNames;

	private BigDecimal minimumSupplierRating;

	private BigDecimal maximumSupplierRating;

	private String teanantType;

	private List<EvaluationSuppliersPojo> suppliersForLeadEvaluators;

	private String generatedOn;

	private String evalutionDeclaration;

	private String supplierDeclaration;

	private Boolean urgentEvent;

	private Boolean allowToSuspendEvent;

	private Boolean closeEnvelope;

	private Boolean viewAuctionHall;

	private Boolean addSupplier;

	private String revertBidUser;

	private String rfxEnvOpeningAfter;

	private String evalConclusionOwners;

	private String unmaskingOwners;

	private Integer envelopeEvaluatedCount;

	private Integer envelopeNonEvaluatedCount;

	private Integer disqualifiedSupplierCount;

	private Integer remainingSupplierCount;

	private String evaluationConclusionPermaturly;

	private List<EvaluationConclusionUsersPojo> evaluationConclusionUsersList;

	private boolean haveUserConclusionPermaturly;

	private Boolean enableApprovalReminder = Boolean.FALSE;

	private Integer reminderAfterHour;

	private Integer reminderCount;

	private Boolean notifyEventOwner = Boolean.FALSE;
	
	private List<EvaluationApprovalsPojo> suspensionApprovals;
	
	private List<EvaluationCommentsPojo> suspensionComments;

	private BigDecimal estimatedBudget;

	private String procurementMethod;

	private String procurementCategories;
	
	private String groupCode;

	private Boolean allowEvaluationForDisqualifySupplier;

	private String fileName;

	private String title;
	
	private List<EvaluationApprovalsPojo> awardApprovals;
	
	private List<EvaluationCommentsPojo> awardComments;

	public String getGeneratedOn() {
		return generatedOn;
	}

	public void setGeneratedOn(String generatedOn) {
		this.generatedOn = generatedOn;
	}

	public List<EvaluationSuppliersPojo> getSuppliersForLeadEvaluators() {
		return suppliersForLeadEvaluators;
	}

	public void setSuppliersForLeadEvaluators(List<EvaluationSuppliersPojo> suppliersForLeadEvaluators) {
		this.suppliersForLeadEvaluators = suppliersForLeadEvaluators;
	}

	private String participationFeeAndCurrency;

	private String depositAndCurrency;

	public Boolean getIsMask() {
		return isMask;
	}

	public void setIsMask(Boolean isMask) {
		this.isMask = isMask;
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
	 * @return the referenceNo
	 */
	public String getReferenceNo() {
		return referenceNo;
	}

	/**
	 * @param referenceNo the referenceNo to set
	 */
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	/**
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the envelopOpener
	 */
	public String getEnvelopOpener() {
		return envelopOpener;
	}

	/**
	 * @param envelopOpener the envelopOpener to set
	 */
	public void setEnvelopOpener(String envelopOpener) {
		this.envelopOpener = envelopOpener;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the contactno
	 */
	public String getContactno() {
		return contactno;
	}

	/**
	 * @param contactno the contactno to set
	 */
	public void setContactno(String contactno) {
		this.contactno = contactno;
	}

	/**
	 * @return the mobileno
	 */
	public String getMobileno() {
		return mobileno;
	}

	/**
	 * @param mobileno the mobileno to set
	 */
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
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
	 * @return the referenceId
	 */
	public String getReferenceId() {
		return referenceId;
	}

	/**
	 * @param referenceId the referenceId to set
	 */
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	/**
	 * @return the eventStart
	 */
	public String getEventStart() {
		return eventStart;
	}

	/**
	 * @param eventStart the eventStart to set
	 */
	public void setEventStart(String eventStart) {
		this.eventStart = eventStart;
	}

	/**
	 * @return the eventEnd
	 */
	public String getEventEnd() {
		return eventEnd;
	}

	/**
	 * @param eventEnd the eventEnd to set
	 */
	public void setEventEnd(String eventEnd) {
		this.eventEnd = eventEnd;
	}

	/**
	 * @return the reminderDate
	 */
	public List<EvaluationPojo> getReminderDate() {
		return reminderDate;
	}

	/**
	 * @param reminderDate the reminderDate to set
	 */
	public void setReminderDate(List<EvaluationPojo> reminderDate) {
		this.reminderDate = reminderDate;
	}

	/**
	 * @return the reminderStartDate
	 */
	public List<EvaluationPojo> getReminderStartDate() {
		return reminderStartDate;
	}

	/**
	 * @param reminderStartDate the reminderStartDate to set
	 */
	public void setReminderStartDate(List<EvaluationPojo> reminderStartDate) {
		this.reminderStartDate = reminderStartDate;
	}

	/**
	 * @return the publishDate
	 */
	public String getPublishDate() {
		return publishDate;
	}

	/**
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	/**
	 * @return the visibility
	 */
	public String getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(String visibility) {
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

	/**
	 * @return the participationFee
	 */
	public BigDecimal getParticipationFee() {
		return participationFee;
	}

	/**
	 * @param participationFee the participationFee to set
	 */
	public void setParticipationFee(BigDecimal participationFee) {
		this.participationFee = participationFee;
	}

	/**
	 * @return the category
	 */
	public List<IndustryCategoryPojo> getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(List<IndustryCategoryPojo> category) {
		this.category = category;
	}

	/**
	 * @return the correspondAddress
	 */
	public String getCorrespondAddress() {
		return correspondAddress;
	}

	/**
	 * @param correspondAddress the correspondAddress to set
	 */
	public void setCorrespondAddress(String correspondAddress) {
		this.correspondAddress = correspondAddress;
	}

	/**
	 * @return the deliveryAddress
	 */
	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
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
	 * @return the historicAmt
	 */
	public BigDecimal getHistoricAmt() {
		return historicAmt;
	}

	/**
	 * @param historicAmt the historicAmt to set
	 */
	public void setHistoricAmt(BigDecimal historicAmt) {
		this.historicAmt = historicAmt;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the budgetAmt
	 */
	public BigDecimal getBudgetAmt() {
		return budgetAmt;
	}

	/**
	 * @param budgetAmt the budgetAmt to set
	 */
	public void setBudgetAmt(BigDecimal budgetAmt) {
		this.budgetAmt = budgetAmt;
	}

	/**
	 * @return the envlopName
	 */
	public String getEnvlopName() {
		return envlopName;
	}

	/**
	 * @param envlopName the envlopName to set
	 */
	public void setEnvlopName(String envlopName) {
		this.envlopName = envlopName;
	}

	/**
	 * @return the envDescription
	 */
	public String getEnvDescription() {
		return envDescription;
	}

	/**
	 * @param envDescription the envDescription to set
	 */
	public void setEnvDescription(String envDescription) {
		this.envDescription = envDescription;
	}

	/**
	 * @return the openType
	 */
	public String getOpenType() {
		return openType;
	}

	/**
	 * @param openType the openType to set
	 */
	public void setOpenType(String openType) {
		this.openType = openType;
	}

	/**
	 * @return the envelopOwner
	 */
	public String getEnvelopOwner() {
		return envelopOwner;
	}

	/**
	 * @param envelopOwner the envelopOwner to set
	 */
	public void setEnvelopOwner(String envelopOwner) {
		this.envelopOwner = envelopOwner;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the openDate
	 */
	public String getOpenDate() {
		return openDate;
	}

	/**
	 * @param openDate the openDate to set
	 */
	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}

	/**
	 * @return the auctionType
	 */
	public String getAuctionType() {
		return auctionType;
	}

	/**
	 * @param auctionType the auctionType to set
	 */
	public void setAuctionType(String auctionType) {
		this.auctionType = auctionType;
	}

	/**
	 * @return the extentionType
	 */
	public String getExtentionType() {
		return extentionType;
	}

	/**
	 * @param extentionType the extentionType to set
	 */
	public void setExtentionType(String extentionType) {
		this.extentionType = extentionType;
	}

	/**
	 * @return the extensionDuration
	 */
	public String getExtensionDuration() {
		return extensionDuration;
	}

	/**
	 * @param extensionDuration the extensionDuration to set
	 */
	public void setExtensionDuration(String extensionDuration) {
		this.extensionDuration = extensionDuration;
	}

	/**
	 * @return the extensionTrigger
	 */
	public String getExtensionTrigger() {
		return extensionTrigger;
	}

	/**
	 * @param extensionTrigger the extensionTrigger to set
	 */
	public void setExtensionTrigger(String extensionTrigger) {
		this.extensionTrigger = extensionTrigger;
	}

	/**
	 * @return the extensionRound
	 */
	public Integer getExtensionRound() {
		return extensionRound;
	}

	/**
	 * @param extensionRound the extensionRound to set
	 */
	public void setExtensionRound(Integer extensionRound) {
		this.extensionRound = extensionRound;
	}

	/**
	 * @return the autoDisqualify
	 */
	public String getAutoDisqualify() {
		return autoDisqualify;
	}

	/**
	 * @param autoDisqualify the autoDisqualify to set
	 */
	public void setAutoDisqualify(String autoDisqualify) {
		this.autoDisqualify = autoDisqualify;
	}

	/**
	 * @return the disqualifyCount
	 */
	public Integer getDisqualifyCount() {
		return disqualifyCount;
	}

	/**
	 * @param disqualifyCount the disqualifyCount to set
	 */
	public void setDisqualifyCount(Integer disqualifyCount) {
		this.disqualifyCount = disqualifyCount;
	}

	/**
	 * @return the contacts
	 */
	public List<EvaluationContactsPojo> getContacts() {
		return contacts;
	}

	/**
	 * @param contacts the contacts to set
	 */
	public void setContacts(List<EvaluationContactsPojo> contacts) {
		this.contacts = contacts;
	}

	/**
	 * @return the suppliers
	 */
	public List<EvaluationSuppliersPojo> getSuppliers() {
		return suppliers;
	}

	/**
	 * @param suppliers the suppliers to set
	 */
	public void setSuppliers(List<EvaluationSuppliersPojo> suppliers) {
		this.suppliers = suppliers;
	}

	/**
	 * @return the meetings
	 */
	public List<EvaluationMeetingPojo> getMeetings() {
		return meetings;
	}

	/**
	 * @param meetings the meetings to set
	 */
	public void setMeetings(List<EvaluationMeetingPojo> meetings) {
		this.meetings = meetings;
	}

	/**
	 * @return the cqs
	 */
	public List<EvaluationCqPojo> getCqs() {
		return cqs;
	}

	/**
	 * @param cqs the cqs to set
	 */
	public void setCqs(List<EvaluationCqPojo> cqs) {
		this.cqs = cqs;
	}

	/**
	 * @return the bqs
	 */
	public List<EvaluationBqPojo> getBqs() {
		return bqs;
	}

	/**
	 * @param bqs the bqs to set
	 */
	public void setBqs(List<EvaluationBqPojo> bqs) {
		this.bqs = bqs;
	}

	/**
	 * @return the envelops
	 */
	public List<EvaluationEnvelopPojo> getEnvelops() {
		return envelops;
	}

	/**
	 * @param envelops the envelops to set
	 */
	public void setEnvelops(List<EvaluationEnvelopPojo> envelops) {
		this.envelops = envelops;
	}

	/**
	 * @return the bqSuppliers
	 */
	public List<EvaluationSuppliersBqPojo> getBqSuppliers() {
		return bqSuppliers;
	}

	/**
	 * @param bqSuppliers the bqSuppliers to set
	 */
	public void setBqSuppliers(List<EvaluationSuppliersBqPojo> bqSuppliers) {
		this.bqSuppliers = bqSuppliers;
	}

	/**
	 * @return the bqLeadCommentsList
	 */
	public List<EvaluationBqPojo> getBqLeadCommentsList() {
		return bqLeadCommentsList;
	}

	/**
	 * @param bqLeadCommentsList the bqLeadCommentsList to set
	 */
	public void setBqLeadCommentsList(List<EvaluationBqPojo> bqLeadCommentsList) {
		this.bqLeadCommentsList = bqLeadCommentsList;
	}

	/**
	 * @return the evaluationTeam
	 */
	public List<EvaluationTeamsPojo> getEvaluationTeam() {
		return evaluationTeam;
	}

	/**
	 * @param evaluationTeam the evaluationTeam to set
	 */
	public void setEvaluationTeam(List<EvaluationTeamsPojo> evaluationTeam) {
		this.evaluationTeam = evaluationTeam;
	}

	/**
	 * @return the approvals
	 */
	public List<EvaluationApprovalsPojo> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<EvaluationApprovalsPojo> approvals) {
		this.approvals = approvals;
	}

	/**
	 * @return the timelines
	 */
	public List<EvaluationTimelinePojo> getTimelines() {
		return timelines;
	}

	/**
	 * @param timelines the timelines to set
	 */
	public void setTimelines(List<EvaluationTimelinePojo> timelines) {
		this.timelines = timelines;
	}

	/**
	 * @return the documents
	 */
	public List<EvaluationDocumentPojo> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<EvaluationDocumentPojo> documents) {
		this.documents = documents;
	}

	/**
	 * @return the envlopSuppliers
	 */
	public List<EvaluationEnvelopSuppliersPojo> getEnvlopSuppliers() {
		return envlopSuppliers;
	}

	/**
	 * @param envlopSuppliers the envlopSuppliers to set
	 */
	public void setEnvlopSuppliers(List<EvaluationEnvelopSuppliersPojo> envlopSuppliers) {
		this.envlopSuppliers = envlopSuppliers;
	}

	/**
	 * @return the comments
	 */
	public List<EvaluationCommentsPojo> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(List<EvaluationCommentsPojo> comments) {
		this.comments = comments;
	}

	/**
	 * @return the buyerName
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * @param buyerName the buyerName to set
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * @return the auctionRules
	 */
	public List<EvaluationAuctionRulePojo> getAuctionRules() {
		return auctionRules;
	}

	/**
	 * @param auctionRules the auctionRules to set
	 */
	public void setAuctionRules(List<EvaluationAuctionRulePojo> auctionRules) {
		this.auctionRules = auctionRules;
	}

	/**
	 * @return the auditDetails
	 */
	public List<EvaluationAuditPojo> getAuditDetails() {
		return auditDetails;
	}

	/**
	 * @param auditDetails the auditDetails to set
	 */
	public void setAuditDetails(List<EvaluationAuditPojo> auditDetails) {
		this.auditDetails = auditDetails;
	}

	/**
	 * @return the businesUnit
	 */
	public String getBusinesUnit() {
		return businesUnit;
	}

	/**
	 * @param businesUnit the businesUnit to set
	 */
	public void setBusinesUnit(String businesUnit) {
		this.businesUnit = businesUnit;
	}

	public String getDiliveryDate() {
		return diliveryDate;
	}

	public void setDiliveryDate(String diliveryDate) {
		this.diliveryDate = diliveryDate;
	}

	/**
	 * @return the supplierMaskingList
	 */
	public List<SupplierMaskingPojo> getSupplierMaskingList() {
		return supplierMaskingList;
	}

	/**
	 * @param supplierMaskingList the supplierMaskingList to set
	 */
	public void setSupplierMaskingList(List<SupplierMaskingPojo> supplierMaskingList) {
		this.supplierMaskingList = supplierMaskingList;
	}

	/**
	 * @return the internalRemarks
	 */
	public String getInternalRemarks() {
		return internalRemarks;
	}

	/**
	 * @param internalRemarks the internalRemarks to set
	 */
	public void setInternalRemarks(String internalRemarks) {
		this.internalRemarks = internalRemarks;
	}

	/**
	 * @return the siteVisit
	 */
	public Boolean getSiteVisit() {
		return siteVisit;
	}

	/**
	 * @param siteVisit the siteVisit to set
	 */
	public void setSiteVisit(Boolean siteVisit) {
		this.siteVisit = siteVisit;
	}

	public List<IndustryCategoryPojo> getIndustryCategoryNames() {
		return industryCategoryNames;
	}

	public void setIndustryCategoryNames(List<IndustryCategoryPojo> industryCategoryNames) {
		this.industryCategoryNames = industryCategoryNames;
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
	 * @return the teanantType
	 */
	public String getTeanantType() {
		return teanantType;
	}

	/**
	 * @param teanantType the teanantType to set
	 */
	public void setTeanantType(String teanantType) {
		this.teanantType = teanantType;
	}

	/**
	 * @return the participationFeeAndCurrency
	 */
	public String getParticipationFeeAndCurrency() {
		return participationFeeAndCurrency;
	}

	/**
	 * @param participationFeeAndCurrency the participationFeeAndCurrency to set
	 */
	public void setParticipationFeeAndCurrency(String participationFeeAndCurrency) {
		this.participationFeeAndCurrency = participationFeeAndCurrency;
	}

	/**
	 * @return the depositAndCurrency
	 */
	public String getDepositAndCurrency() {
		return depositAndCurrency;
	}

	/**
	 * @param depositAndCurrency the depositAndCurrency to set
	 */
	public void setDepositAndCurrency(String depositAndCurrency) {
		this.depositAndCurrency = depositAndCurrency;
	}

	/**
	 * @return the evalutionDeclaration
	 */
	public String getEvalutionDeclaration() {
		return evalutionDeclaration;
	}

	/**
	 * @param evalutionDeclaration the evalutionDeclaration to set
	 */
	public void setEvalutionDeclaration(String evalutionDeclaration) {
		this.evalutionDeclaration = evalutionDeclaration;
	}

	/**
	 * @return the supplierDeclaration
	 */
	public String getSupplierDeclaration() {
		return supplierDeclaration;
	}

	/**
	 * @param supplierDeclaration the supplierDeclaration to set
	 */
	public void setSupplierDeclaration(String supplierDeclaration) {
		this.supplierDeclaration = supplierDeclaration;
	}

	/**
	 * @return the urgentEvent
	 */
	public Boolean getUrgentEvent() {
		return urgentEvent;
	}

	/**
	 * @param urgentEvent the urgentEvent to set
	 */
	public void setUrgentEvent(Boolean urgentEvent) {
		this.urgentEvent = urgentEvent;
	}

	/**
	 * @return the allowToSuspendEvent
	 */
	public Boolean getAllowToSuspendEvent() {
		return allowToSuspendEvent;
	}

	/**
	 * @param allowToSuspendEvent the allowToSuspendEvent to set
	 */
	public void setAllowToSuspendEvent(Boolean allowToSuspendEvent) {
		this.allowToSuspendEvent = allowToSuspendEvent;
	}

	/**
	 * @return the closeEnvelope
	 */
	public Boolean getCloseEnvelope() {
		return closeEnvelope;
	}

	/**
	 * @param closeEnvelope the closeEnvelope to set
	 */
	public void setCloseEnvelope(Boolean closeEnvelope) {
		this.closeEnvelope = closeEnvelope;
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

	/**
	 * @return the addSupplier
	 */
	public Boolean getAddSupplier() {
		return addSupplier;
	}

	/**
	 * @param addSupplier the addSupplier to set
	 */
	public void setAddSupplier(Boolean addSupplier) {
		this.addSupplier = addSupplier;
	}

	/**
	 * @return the revertBidUser
	 */
	public String getRevertBidUser() {
		return revertBidUser;
	}

	/**
	 * @param revertBidUser the revertBidUser to set
	 */
	public void setRevertBidUser(String revertBidUser) {
		this.revertBidUser = revertBidUser;
	}

	/**
	 * @return the rfxEnvOpeningAfter
	 */
	public String getRfxEnvOpeningAfter() {
		return rfxEnvOpeningAfter;
	}

	/**
	 * @param rfxEnvOpeningAfter the rfxEnvOpeningAfter to set
	 */
	public void setRfxEnvOpeningAfter(String rfxEnvOpeningAfter) {
		this.rfxEnvOpeningAfter = rfxEnvOpeningAfter;
	}

	/**
	 * @return the evalConclusionOwners
	 */
	public String getEvalConclusionOwners() {
		return evalConclusionOwners;
	}

	/**
	 * @param evalConclusionOwners the evalConclusionOwners to set
	 */
	public void setEvalConclusionOwners(String evalConclusionOwners) {
		this.evalConclusionOwners = evalConclusionOwners;
	}

	/**
	 * @return the unmaskingOwners
	 */
	public String getUnmaskingOwners() {
		return unmaskingOwners;
	}

	/**
	 * @param unmaskingOwners the unmaskingOwners to set
	 */
	public void setUnmaskingOwners(String unmaskingOwners) {
		this.unmaskingOwners = unmaskingOwners;
	}

	/**
	 * @return the envelopeEvaluatedCount
	 */
	public Integer getEnvelopeEvaluatedCount() {
		return envelopeEvaluatedCount;
	}

	/**
	 * @param envelopeEvaluatedCount the envelopeEvaluatedCount to set
	 */
	public void setEnvelopeEvaluatedCount(Integer envelopeEvaluatedCount) {
		this.envelopeEvaluatedCount = envelopeEvaluatedCount;
	}

	/**
	 * @return the envelopeNonEvaluatedCount
	 */
	public Integer getEnvelopeNonEvaluatedCount() {
		return envelopeNonEvaluatedCount;
	}

	/**
	 * @param envelopeNonEvaluatedCount the envelopeNonEvaluatedCount to set
	 */
	public void setEnvelopeNonEvaluatedCount(Integer envelopeNonEvaluatedCount) {
		this.envelopeNonEvaluatedCount = envelopeNonEvaluatedCount;
	}

	/**
	 * @return the disqualifiedSupplierCount
	 */
	public Integer getDisqualifiedSupplierCount() {
		return disqualifiedSupplierCount;
	}

	/**
	 * @param disqualifiedSupplierCount the disqualifiedSupplierCount to set
	 */
	public void setDisqualifiedSupplierCount(Integer disqualifiedSupplierCount) {
		this.disqualifiedSupplierCount = disqualifiedSupplierCount;
	}

	/**
	 * @return the remainingSupplierCount
	 */
	public Integer getRemainingSupplierCount() {
		return remainingSupplierCount;
	}

	/**
	 * @param remainingSupplierCount the remainingSupplierCount to set
	 */
	public void setRemainingSupplierCount(Integer remainingSupplierCount) {
		this.remainingSupplierCount = remainingSupplierCount;
	}

	/**
	 * @return the evaluationConclusionPermaturly
	 */
	public String getEvaluationConclusionPermaturly() {
		return evaluationConclusionPermaturly;
	}

	/**
	 * @param evaluationConclusionPermaturly the evaluationConclusionPermaturly to set
	 */
	public void setEvaluationConclusionPermaturly(String evaluationConclusionPermaturly) {
		this.evaluationConclusionPermaturly = evaluationConclusionPermaturly;
	}

	/**
	 * @return the evaluationConclusionUsersList
	 */
	public List<EvaluationConclusionUsersPojo> getEvaluationConclusionUsersList() {
		return evaluationConclusionUsersList;
	}

	/**
	 * @param evaluationConclusionUsersList the evaluationConclusionUsersList to set
	 */
	public void setEvaluationConclusionUsersList(List<EvaluationConclusionUsersPojo> evaluationConclusionUsersList) {
		this.evaluationConclusionUsersList = evaluationConclusionUsersList;
	}

	/**
	 * @return the haveUserConclusionPermaturly
	 */
	public boolean isHaveUserConclusionPermaturly() {
		return haveUserConclusionPermaturly;
	}

	/**
	 * @param haveUserConclusionPermaturly the haveUserConclusionPermaturly to set
	 */
	public void setHaveUserConclusionPermaturly(boolean haveUserConclusionPermaturly) {
		this.haveUserConclusionPermaturly = haveUserConclusionPermaturly;
	}

	/**
	 * @return the enableApprovalReminder
	 */
	public Boolean getEnableApprovalReminder() {
		return enableApprovalReminder;
	}

	/**
	 * @param enableApprovalReminder the enableApprovalReminder to set
	 */
	public void setEnableApprovalReminder(Boolean enableApprovalReminder) {
		this.enableApprovalReminder = enableApprovalReminder;
	}

	/**
	 * @return the reminderAfterHour
	 */
	public Integer getReminderAfterHour() {
		return reminderAfterHour;
	}

	/**
	 * @param reminderAfterHour the reminderAfterHour to set
	 */
	public void setReminderAfterHour(Integer reminderAfterHour) {
		this.reminderAfterHour = reminderAfterHour;
	}

	/**
	 * @return the reminderCount
	 */
	public Integer getReminderCount() {
		return reminderCount;
	}

	/**
	 * @param reminderCount the reminderCount to set
	 */
	public void setReminderCount(Integer reminderCount) {
		this.reminderCount = reminderCount;
	}

	/**
	 * @return the notifyEventOwner
	 */
	public Boolean getNotifyEventOwner() {
		return notifyEventOwner;
	}

	/**
	 * @param notifyEventOwner the notifyEventOwner to set
	 */
	public void setNotifyEventOwner(Boolean notifyEventOwner) {
		this.notifyEventOwner = notifyEventOwner;
	}

	public BigDecimal getEstimatedBudget() {
		return estimatedBudget;
	}

	public void setEstimatedBudget(BigDecimal estimatedBudget) {
		this.estimatedBudget = estimatedBudget;
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


	/**
	 * @return the suspensionApprovals
	 */
	public List<EvaluationApprovalsPojo> getSuspensionApprovals() {
		return suspensionApprovals;
	}

	/**
	 * @param suspensionApprovals the suspensionApprovals to set
	 */
	public void setSuspensionApprovals(List<EvaluationApprovalsPojo> suspensionApprovals) {
		this.suspensionApprovals = suspensionApprovals;
	}

	/**
	 * @return the suspensionComments
	 */
	public List<EvaluationCommentsPojo> getSuspensionComments() {
		return suspensionComments;
	}

	/**
	 * @param suspensionComments the suspensionComments to set
	 */
	public void setSuspensionComments(List<EvaluationCommentsPojo> suspensionComments) {
		this.suspensionComments = suspensionComments;
	}

	/**
	 * @return the groupCode
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	
	
	public Boolean getAllowEvaluationForDisqualifySupplier() {
		return allowEvaluationForDisqualifySupplier;
	}

	public void setAllowEvaluationForDisqualifySupplier(Boolean allowEvaluationForDisqualifySupplier) {
		this.allowEvaluationForDisqualifySupplier = allowEvaluationForDisqualifySupplier;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the awardApprovals
	 */
	public List<EvaluationApprovalsPojo> getAwardApprovals() {
		return awardApprovals;
	}

	/**
	 * @return the awardComments
	 */
	public List<EvaluationCommentsPojo> getAwardComments() {
		return awardComments;
	}

	/**
	 * @param awardApprovals the awardApprovals to set
	 */
	public void setAwardApprovals(List<EvaluationApprovalsPojo> awardApprovals) {
		this.awardApprovals = awardApprovals;
	}

	/**
	 * @param awardComments the awardComments to set
	 */
	public void setAwardComments(List<EvaluationCommentsPojo> awardComments) {
		this.awardComments = awardComments;
	}


	public List<EvaluationSorPojo> getSors() {
		return sors;
	}

	public void setSors(List<EvaluationSorPojo> sors) {
		this.sors = sors;
	}

	public List<EvaluationSuppliersSorPojo> getSorSuppliers() {
		return sorSuppliers;
	}

	public void setSorSuppliers(List<EvaluationSuppliersSorPojo> sorSuppliers) {
		this.sorSuppliers = sorSuppliers;
	}
}
