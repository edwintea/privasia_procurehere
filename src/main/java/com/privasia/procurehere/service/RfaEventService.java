package com.privasia.procurehere.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ui.Model;

import com.privasia.procurehere.core.entity.AuctionBids;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAward;
import com.privasia.procurehere.core.entity.RfaEventAwardAudit;
import com.privasia.procurehere.core.entity.RfaEventContact;
import com.privasia.procurehere.core.entity.RfaEventCorrespondenceAddress;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaEventTimeLine;
import com.privasia.procurehere.core.entity.RfaReminder;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.DurationMinSecType;
import com.privasia.procurehere.core.enums.DurationType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.AuctionSupplierBidPojo;
import com.privasia.procurehere.core.pojo.BidHistoryChartPojo;
import com.privasia.procurehere.core.pojo.BidHistoryPojo;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.FinishedEventPojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.pojo.OngoingEventPojo;
import com.privasia.procurehere.core.pojo.RfaEventCAddressPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author Kapil
 */

public interface RfaEventService {

	RfaEvent saveRfaEvent(RfaEvent rfaEvent, User loggedInUser) throws SubscriptionException;

	RfaEvent updateRfaEvent(RfaEvent rfaEvent);

	void deleteRfaEvent(RfaEvent rfaEvent);

	RfaEvent getRfaEventById(String id);

	List<NaicsCodes> findLeafIndustryCategoryByName(String searchValue);

	void saveRfaEventContact(RfaEventContact rfaEventContact);

	void saveRfaEventCorrespondenceAddress(RfaEventCorrespondenceAddress rfaEventCorrespondenceAddress);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEvent getRfaEventByeventId(String eventId);

	IndustryCategory getIndustryCategoryForRfaById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventContact> getAllContactForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventCorrespondenceAddress> getAllCAddressForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventCAddressPojo> getAllCorrespondenceAddressPojo(String eventId);

	/**
	 * @param searchParams
	 * @return
	 */
	List<FavouriteSupplier> searchCustomSupplier(SupplierSearchPojo searchParams);

	/**
	 * @param id
	 * @return
	 */
	RfaEvent loadRfaEventById(String id);

	/**
	 * @param searchParam
	 * @param buyerId TODO
	 * @return
	 */
	List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId);

	/**
	 * @param rfaEvent
	 * @return
	 */
	boolean isExists(RfaEvent rfaEvent);

	/**
	 * @param tenantId
	 * @return
	 */
	List<DraftEventPojo> getAllDraftEventForBuyer(String tenantId);

	/**
	 * @param tenantId
	 * @param lastLoginTime
	 * @return
	 */
	List<OngoingEventPojo> getAllOngoingEventsForBuyer(String tenantId, Date lastLoginTime);

	/**
	 * @param tenantId
	 * @param lastLoginTime
	 * @param days
	 * @return
	 */
	List<FinishedEventPojo> getAllFinishedEventsForBuyer(String tenantId, Date lastLoginTime, int days);

	/**
	 * @param suppliers
	 * @return
	 */
	List<FavouriteSupplier> getFavouriteSupplierBySupplierId(List<Supplier> suppliers);

	/**
	 * @param supplierId
	 * @return
	 */
	List<RfxView> getAllEventForSupplier(String supplierId);

	/**
	 * @param id
	 * @return
	 */
	RfaEvent loadRfaEventForSummeryById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfEnvelopByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfaDocumentByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfaCqByEventId(String eventId);

	List<DraftEventPojo> getAllRfaEventByTenantId(String tenantId, String loggedInUser, String pageNo, String searchValue, String industryCategory) throws SubscriptionException;;

	List<RfaEvent> findByEventNameAndRefNumAndIndCatForTenant(String eventName, String referenceNumber, String industryCategory, String tenantId);

	RfaEvent copyFrom(String eventId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException;

	boolean isExists(RfaSupplierMeetingAttendance rfaSupplierMeetingAttendance);

	RfaSupplierMeetingAttendance loadSupplierMeetingAttendenceByEventId(String meetingId, String eventId, String tenantId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliers(String eventId);

	/**
	 * @param templateId
	 * @param createdBy
	 * @return
	 * @throws SubscriptionException
	 */
	RfaEvent copyFromTemplate(String templateId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException;

	RfaEventContact getEventContactById(String id);

	void deleteRfaContact(RfaEventContact eventContact);

	void updateRfaEventContact(RfaEventContact rfaEventContact);

	/**
	 * @param rftReminder
	 */
	void saveEventReminder(RfaReminder rfaReminder);

	/**
	 * @param rftReminder
	 */
	void updateEventReminder(RfaReminder rfaReminder);

	List<RfaReminder> getAllRfaEventReminderForEvent(String eventId, Boolean startReminder) throws ApplicationException;

	RfaReminder getEventReminderById(String id);

	void deleteRfaReminder(RfaReminder rfaReminder);

	/**
	 * @param tenantId
	 * @return
	 */
	List<RfaEvent> getAllFinishAndApprovedRfaEventByTenantId(String tenantId);

	/**
	 * @param searchValue
	 * @param tenantId
	 * @return
	 */
	List<RfaEvent> findRfaEventByNameAndTenantId(String searchValue, String tenantId);

	/**
	 * @param eventId
	 * @param userId
	 * @return
	 */
	List<User> addEditorUser(String eventId, String userId);

	/**
	 * @param eventId
	 * @param userId
	 * @return
	 */
	List<User> addViewersToList(String eventId, String userId);

	/**
	 * @param eventId
	 * @param userId
	 * @param memberType TODO
	 * @return
	 */
	List<RfaTeamMember> addTeamMemberToList(String eventId, String userId, TeamMemberType memberType);

	/**
	 * @param eventId
	 * @param userId
	 * @return
	 */
	List<User> removeEditorUser(String eventId, String userId);

	/**
	 * @param eventId
	 * @param userId
	 * @return
	 */
	List<User> removeViewersfromList(String eventId, String userId);

	/**
	 * @param eventId
	 * @param userId
	 * @param dbTeamMember TODO
	 * @return
	 */
	List<User> removeTeamMemberfromList(String eventId, String userId, RfaTeamMember dbTeamMember);

	/**
	 * @param auctionRules
	 * @returns
	 */
	AuctionRules saveAuctionRules(AuctionRules auctionRules);

	/**
	 * @param auctionRules
	 * @return
	 */
	AuctionRules updateAuctionRules(AuctionRules auctionRules);

	/**
	 * @param eventId
	 * @return
	 */
	AuctionRules getAuctionRulesByEventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	AuctionRules getAuctionRulesById(String id);

	/**
	 * @param rfaEventContact
	 * @return
	 */
	boolean isExists(RfaEventContact rfaEventContact);

	/**
	 * @param id
	 * @return
	 */
	AuctionRules getAuctionRulesWithEventById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getTeamMembersForEvent(String eventId);

	/**
	 * @param currentStep
	 * @param auctionId
	 * @return
	 */
	BigDecimal findAuctionRulesCurrentPriceForStepNo(Integer currentStep, String auctionId);

	/**
	 * @param currentStep
	 * @param auctionId
	 * @param event
	 * @param loggedInUser
	 * @param loggedInTenantId
	 * @param ipAddress TODO
	 * @throws ApplicationException
	 */
	void submitDutchAuction(Integer currentStep, String auctionId, RfaEvent event, User loggedInUser, String loggedInTenantId, String ipAddress) throws ApplicationException;

	/**
	 * @param event
	 * @return
	 */
	RfaEvent suspendDutchAuction(RfaEvent event);

	/**
	 * @param userId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserPemissionsForEvent(String userId, String eventId);

	/**
	 * @param event
	 * @return TODO
	 */
	RfaEvent updateEventApproval(RfaEvent event, User loggedInUser);

	/**
	 * @param eventId
	 * @return
	 */
	BidHistoryPojo getMinMaxBidPriceForEvent(String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<AuctionSupplierBidPojo> getAuctionBidsListBySupplierIdAndEventId(String supplierId, String eventId);

	/**
	 * @param id
	 * @throws ApplicationException
	 */
	void insertTimeLine(String id) throws ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	List<RfaEventTimeLine> getRfaEventTimeline(String id);

	/**
	 * @param userId
	 * @param eventId
	 * @param envelopeId
	 * @return
	 */
	EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId);

	/**
	 * @param event
	 * @param virtualizer TODO
	 * @param logedUser TODO
	 * @return
	 * @throws Exception
	 */
	RfaEvent cancelEvent(RfaEvent event, HttpSession session, JRSwapFileVirtualizer virtualizer, User logedUser) throws Exception;

	/**
	 * @param eventId
	 * @param timeZone TODO
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generatePdfforEvaluationSummary(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param event
	 * @param loggedInUser TODO
	 * @param strTimeZone TODO
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint getEvaluationSummaryPdf(RfaEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	AuctionBids saveAuctionBids(AuctionBids auctionBids);

	/**
	 * @param eventId
	 * @param meetingId
	 * @param session
	 * @return
	 */
	JasperPrint getMeetingAttendanceReport(String eventId, String meetingId, HttpSession session);

	RfaEvent concludeRfaEvent(RfaEvent event, User loggedInUser);

	/**
	 * @param eventId
	 * @param strTimeZone
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint getBuyerAuctionReport(String eventId, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	boolean automaticTimeExtension(RfaEvent event, TimeZone timeZone, HttpSession session);

	String createNextEvent(RfaEvent rfaevent, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, String[] invitedSupp, String concludeRemarks) throws Exception;

	AuctionRules getAuctionRulesForAuctionConsole(String eventId);

	RfaEvent getLeanEventbyEventId(String eventId);

	List<Supplier> getAwardedSuppliers(String id);

	RfaEvent getPlainEventById(String eventId);

	AuctionRules getAuctionRulesForBidSumission(String eventId);

	void updateLumsumAuction(String eventId, String supplierId, String bqId, BigDecimal totalAfterTax, BigDecimal differencePercentage);

	AuctionRules getLeanAuctionRulesByEventId(String eventId);

	void updateEventSupplierForAuction(String eventId, String supplierId, String ipAddress);

	List<AuctionBids> getAuctionBidsForSupplier(String supplierId, String eventId);

	/**
	 * @param bidId
	 * @return
	 */
	AuctionBids getAuctionBidForBidId(String bidId);

	RfaEvent suspendEnglishAuction(RfaEvent event);

	long getEventSuppliersCount(String eventId);

	RfaEvent getRfaEventForBidHistory(String eventId);

	List<Date> getAllMeetingDateByEventId(String eventId);

	EventStatus checkRelativeEventStatus(String relativeEventId);

	List<RfaEvent> getAllAssosiateAuction(String eventId);

	void suspendAllRelativeEvents(String eventId);

	void scheduleAuction(AuctionRules auctionRulesData) throws ApplicationException;

	void manageRelativeEventOnTimeExt(RfaEvent event, TimeZone timeZone, Integer duration, DurationType durationType);

	Date calculateEndDateForDutchAuction(Integer interval, DurationMinSecType intervalType, Integer totalSteps, Date startDate);

	Integer findAssignedTemplateCount(String id);

	List<RfaEvent> getAllAssosiateAuctionForReschdule(String eventId);

	RfaEvent getRfaEventForTimeExtensionAndBidSubmission(String eventId);

	void updateTimeExtension(String eventId, Integer totalExtensions, Date eventEnd);

	/**
	 * @param eventId
	 * @return
	 */
	User getPlainEventOwnerByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getPlainTeamMembersForEvent(String eventId);

	/**
	 * @param id
	 * @param userId
	 * @return
	 * @throws ApplicationException
	 */
	MobileEventPojo getMobileEventDetails(String id, String userId) throws ApplicationException;

	BidHistoryPojo getBidHistoryOfOwnSupplierBid(String eventId, String supplierId);

	/**
	 * @param docId
	 * @param response
	 * @throws Exception
	 */
	void downloadEventDocument(String docId, HttpServletResponse response) throws Exception;

	/**
	 * @param eventId
	 * @param response
	 * @param session
	 * @param loggedInUser
	 * @param virtualizer TODO
	 */
	void downloadEventSummary(String eventId, HttpServletResponse response, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEvent getRfaEventByeventIdForSummaryReport(String eventId);

	/**
	 * @param eventId
	 * @param timeZone TODO
	 * @param arrangeBidBy TODO
	 * @return
	 */
	BidHistoryChartPojo getBidHistoryChartData(String eventId, String timeZone, String arrangeBidBy);

	BidHistoryChartPojo getBidHistoryChartDataForSupplier(String eventId, String supplierId, String timeZone, String arrangeBidBy);

	RfaSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String id, String tenantId);

	RfaSupplierBq getSupplierBQOfLowestItemisedPrice(String eventId, String id, String tenantId);

	RfaSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String id, String tenantId, String awardId);

	boolean disqualifySupplier(String supplierId, String remark, String eventId, User loggedInUser);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEvent getRfaEventWithIndustryCategoriesByEventId(String eventId);

	boolean isExistsRfaEventId(String tenantId, String eventId);

	void createRfaFromTemplate(RfaEvent newEvent, RfxTemplate template, AuctionRules auctionRules, BusinessUnit selectedbusinessUnit, User loggedInUser, boolean isFromRequest) throws Exception;

	List<RfaTeamMember> addAssociateOwners(User createdBy, RfaEvent newEvent);

	void setDefaultEventContactDetail(String id, String id2);

	void sendTeamMemberEmailNotificationEmail(User user, TeamMemberType memberType, User cretedBy, String eventName, String eventId, String referanceNumber, RfxTypes eventType, String id);

	RfaTeamMember getRfaTeamMemberByUserIdAndEventId(String eventId, String userId);

	/**
	 * @param erpAwardRefId
	 * @param tenantId
	 * @return
	 */
	RfaEvent findRfaEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId);

	String createPrFromAward(RfaEventAward rfaEventAward, String templateId, String userId, User loggedInUser) throws ApplicationException;

	public List<IndustryCategoryPojo> getTopFiveCategory(String tanentId);

	JasperPrint getBiddingReportEnglish(String eventId, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	List<DraftEventPojo> getAllExcelEventReportForBuyer(String loggedInUserTenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate);

	JasperPrint getEvaluationReport(String eventId, String evenvelopId, String attribute, JRSwapFileVirtualizer virtualizer);

	JasperPrint generateSubmissionReport(String evenvelopId, String eventId, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	JasperPrint generatePdfforEvaluationSummary(String eventId, String envelopeId, JRSwapFileVirtualizer virtualizer);

	List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate);

	EventTimerPojo getTimeRfaEventByeventId(String eventId);

	/**
	 * @param id
	 * @param userId
	 * @param tenantId TODO
	 * @return
	 * @throws ApplicationException
	 */
	MobileEventPojo getMobileEventDetailsForSupplier(String id, String userId, String tenantId) throws ApplicationException;

	List<RfaEvent> getAllRfaEventByTenantId(String tenantId);

	Event getEventByEventRefranceNo(String eventRefranceNo, String string);

	EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId);

	EventPojo loadSupplierEventPojoForSummeryById(String eventId);

	long getInvitedSupplierCount(String eventId);

	long getParticepatedSupplierCount(String eventId);

	long getSubmitedSupplierCount(String eventId);

	List<RfaSupplierBq> getLowestSubmissionPrice(String eventId, Boolean prebidFlag);

	List<RfaSupplierBq> getHighestSubmissionPrice(String eventId, Boolean prebidFlag);

	List<RfaEventSupplier> getSuppliersByStatus(String eventId);

	RfaSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId);

	Boolean findAuctionRulesWithEventId(String eventId);

	long getNumberOfBidForRfa(String eventId);

	RfaEnvelop getBqForEnvelope(String envelopeId);

	// String getSuppliersByRevisedBidTime(String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	EventPojo getRfaForPublicEventByeventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<IndustryCategory> getIndustryCategoriesForRfaById(String eventId);

	/**
	 * @param event
	 * @return
	 */
	JasperPrint getPublicEventSummaryPdf(EventPojo event);

	int updateEventPushedDate(String eventId);

	Double getAvarageBidPriceSubmitted(String id);

	List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	List<String> getEventTeamMember(String string);

	int updatePrPushDate(String eventId);

	int updateEventAward(String eventId);

	/**
	 * @param eventId
	 * @param status
	 * @param auctionComplitationTime
	 */
	void updateImmediately(String eventId, EventStatus status, Date auctionComplitationTime);

	/**
	 * @param eventId
	 */
	void updateEventStartMessageFlagImmediately(String eventId);

	List<RfaEvent> getAllRfaEventByTenantIdInitial(String loggedInUserTenantId, String string) throws SubscriptionException;

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountByEventId(String eventId);

	List<DraftEventPojo> getAuctionEventsForAuctionSummaryReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, List<AuctionType> auctionTypeList, String auctionTypeS);

	long getAuctionEventsCountForBuyerEventReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, List<AuctionType> auctionTypeList, String auctionTypeS);

	long findTotalEventForBuyer(String loggedInUserTenantId, Object object, List<AuctionType> auctionTypeList, String auctionTypeS);

	List<DraftEventPojo> getAllSearchAuctionEventReportForBuyer(String loggedInUserTenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf, String auctionTypeS);

	Event loadRfaEventModalById(String eventId, Model model, SimpleDateFormat format, List<User> assignedTeamMembers, List<User> approvalUsers, List<User> suspApprovalUsers) throws CloneNotSupportedException, ApplicationException;

	/**
	 * @param eventId
	 * @return
	 */
	Event getSimpleEventDetailsById(String eventId);

	/**
	 * @param eventId
	 * @param id
	 * @return
	 */
	RfaEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String id);

	XSSFWorkbook buildBqComparisionFile(XSSFWorkbook workbook, List<EventEvaluationPojo> list, HttpServletResponse response, String eventId, String evelopId, RfxTypes eventType) throws IOException;

	XSSFWorkbook buildCqComparisionFile(XSSFWorkbook workbook, List<EventEvaluationPojo> list, HttpServletResponse response) throws IOException;


	XSSFWorkbook buildSorComparisionFile(XSSFWorkbook workbook, List<EventEvaluationPojo> list, HttpServletResponse response, String eventId, String evelopId, RfxTypes eventType) throws IOException;


	List<RfaEventAwardAudit> getRfaEventAwardByEventId(String eventId);

	/**
	 * @param event
	 * @param loggedInUser
	 * @param attribute
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint getEventAuditPdf(RfaEvent event, User loggedInUser, String attribute, JRSwapFileVirtualizer virtualizer);

	Boolean isDefaultPreSetEnvlope(String eventId);

	RfaSupplierBq getSupplierBQOfHighestTotalPrice(String eventId, String bqId, String loggedInUserTenantId);

	RfaSupplierBq getSupplierBQOfHighestItemisedPrice(String eventId, String bqId, String loggedInUserTenantId);

	/**
	 * @param actionBy
	 * @param event
	 */
	void sendEvaluationCompletedPrematurelyNotification(User actionBy, Event event);

	/**
	 * @param eventId
	 * @param evalConUserId
	 * @return
	 */
	RfaEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId);

	/**
	 * @param virtualizer TODO
	 */
	JasperPrint generatePdfforEvaluationConclusion(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param userId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserEventPemissions(String userId, String eventId);

	/**
	 * @param recipientEmail
	 * @param subject
	 * @param description
	 * @param event
	 * @param name
	 * @param feeReference
	 */
	void sendEmailAfterParticipationFees(String recipientEmail, String subject, String description, RfaEvent event, String name, String feeReference, String timezone);

	/**
	 * @param response
	 * @param file
	 * @param eventIds
	 * @param searchFilterEventPojo
	 * @param startDate
	 * @param endDate
	 * @param select_all
	 * @param auctionTypeS
	 * @param tenantId
	 */
	void downloadCsvFileForEvents(HttpServletResponse response, File file, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, Date startDate, Date endDate, boolean select_all, String auctionTypeS, String tenantId, HttpSession session);

	long getRfaEventCountByTenantId(String searchValue, String tenantId, String userId);

	/**
	 * @param event
	 * @param user
	 * @return
	 */
	RfaEvent updateEventSuspensionApproval(RfaEvent event, User user);

	/**
	 * @param eventId
	 * @param memberType
	 * @return
	 */
	List<User> getAssociateOwnersOfEventsByEventId(String eventId, TeamMemberType memberType);

	void downloadRfaEvaluatorDocument(String id, HttpServletResponse response);

	void downloadRfaLeadEvaluatorDocument(String envelopId, HttpServletResponse response);

	String createContractFromAward(RfaEventAward rfaEventAward, String eventId, String contractStartDate, String contractEndDate, String groupCodeHid, String referenceNumberHid, User loggedInUser, HttpSession session, String contractCreator) throws ApplicationException;

	/**
	 * 
	 * @param eventId
	 */
 	void revertEventAwardStatus(String eventId);
 
 	List<String> createSpFormFromAward(RfaEventAward rfaEventAward, String templateId, String eventId, String userId, User loggedInUser) throws ApplicationException;


	List<RfaReminder> getAllRfaEventReminderForEvent(String eventId);
}
