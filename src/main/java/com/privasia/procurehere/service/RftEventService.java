package com.privasia.procurehere.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.ActiveEventPojo;
import com.privasia.procurehere.core.pojo.ApprovedRejectEventPojo;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.FinishedEventPojo;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.pojo.OngoingEventPojo;
import com.privasia.procurehere.core.pojo.PendingEventPojo;
import com.privasia.procurehere.core.pojo.PublicEventPojo;
import com.privasia.procurehere.core.pojo.RequestParamPojo;
import com.privasia.procurehere.core.pojo.RftEventCAddressPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.SearchSortFilterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author Kapil
 */

public interface RftEventService {

	/**
	 * @param rftEvent
	 * @param loggedInUser
	 * @return
	 * @throws SubscriptionException
	 */
	RftEvent saveRftEvent(RftEvent rftEvent, User loggedInUser) throws SubscriptionException;

	/**
	 * @param rftEvent
	 * @return
	 */
	RftEvent updateRftEvent(RftEvent rftEvent);

	/**
	 * @param rftEvent
	 */
	void deleteRftEvent(RftEvent rftEvent);

	/**
	 * @param id
	 * @return
	 */
	RftEvent getEventById(String id);

	/**
	 * @param id
	 * @return
	 */
	RftEvent getRftEventById(String id);

	/**
	 * @param searchValue
	 * @return
	 */
	List<NaicsCodes> findLeafIndustryCategoryByName(String searchValue);

	/**
	 * @param rftEventContact
	 */
	void saveRftEventContact(RftEventContact rftEventContact);

	/**
	 * @param rftEventCorrespondenceAddress
	 */
	void saveRftEventCorrespondenceAddress(RftEventCorrespondenceAddress rftEventCorrespondenceAddress);

	/**
	 * @param eventId
	 * @return
	 */
	RftEvent getRftEventByeventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	IndustryCategory getIndustryCategoryForRftById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventContact> getAllContactForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventCorrespondenceAddress> getAllCAddressForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventCAddressPojo> getAllCorrespondenceAddressPojo(String eventId);

	// /**
	// * @param searchParams
	// * @return
	// */
	// List<FavouriteSupplier> searchCustomSupplier(SupplierSearchPojo searchParams);

	/**
	 * @param id
	 * @return
	 */
	RftEvent loadRftEventById(String id);

	/**
	 * @param searchParam
	 * @param buyerId TODO
	 * @return
	 */
	List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId);

	/**
	 * @param rftEvent
	 * @return
	 */
	boolean isExists(RftEvent rftEvent);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @return
	 */
	List<DraftEventPojo> getAllDraftEventForBuyer(String tenantId, TableDataInput input, String userId);

	/**
	 * @param tenantId
	 * @param lastLoginTime
	 * @param input
	 * @param userId TODO
	 * @return
	 */
	List<OngoingEventPojo> getAllOngoingEventsForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId);

	/**
	 * @param tenantId
	 * @param lastLoginTime
	 * @param days
	 * @param userId TODO
	 * @return
	 */
	List<FinishedEventPojo> getAllFinishedEventsForBuyer(String tenantId, Date lastLoginTime, int days, TableDataInput input, String userId);

	/**
	 * @param suppliers
	 * @return
	 */
	List<FavouriteSupplier> getFavouriteSupplierBySupplierId(List<Supplier> suppliers);

	/**
	 * @param supplierId
	 * @param userId TODO
	 * @return
	 */
	List<RfxView> getAllEventForSupplier(String supplierId, String userId);

	/**
	 * @param id
	 * @return
	 */
	RftEvent loadRftEventForSummeryById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfEnvelopByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRftDocumentByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRftCqByEventId(String eventId);

	/**
	 * @param tenantId
	 * @param loggedInUser
	 * @param industryCategory
	 * @param searchValue
	 * @param pageNO
	 * @return
	 * @throws SubscriptionException
	 */
	List<DraftEventPojo> getAllRftEventByTenantId(String tenantId, String loggedInUser, String pageNo, String searchValue, String industryCategory) throws SubscriptionException;

	/**
	 * @param searchValue
	 * @param industryCategory
	 * @param tenantId
	 * @param eventType
	 * @param loggedInUser
	 * @return
	 */
	List<Event> findByEventNameaAndRefNumAndIndCatForTenant(String searchValue, String industryCategory, String tenantId, String eventType, String loggedInUser);

	/**
	 * @param eventId
	 * @param createdBy
	 * @return
	 * @throws SubscriptionException
	 */
	RftEvent copyFrom(String eventId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException;

	/**
	 * @param rftSupplierMeetingAttendance
	 * @return
	 */
	boolean isExists(RftSupplierMeetingAttendance rftSupplierMeetingAttendance);

	/**
	 * @param meetingId
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	RftSupplierMeetingAttendance loadSupplierMeetingAttendenceByEventId(String meetingId, String eventId, String tenantId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliers(String eventId);

	/**
	 * @param templateId
	 * @param createdBy
	 * @param businessUnit
	 * @return
	 * @throws SubscriptionException
	 * @throws ApplicationException
	 */
	RftEvent copyFromTemplate(String templateId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	RftEventContact getEventContactById(String id);

	/**
	 * @param eventContact
	 */
	void deleteRftContact(RftEventContact eventContact);

	/**
	 * @param rftEventContact
	 */
	void updateRftEventContact(RftEventContact rftEventContact);

	/**
	 * @param rftReminder
	 */
	void saveEventReminder(RftReminder rftReminder);

	/**
	 * @param rftReminder
	 */
	void updateEventReminder(RftReminder rftReminder);

	List<RftReminder> getAllRftEventReminderForEvent(String eventId);

	RftReminder getEventReminderById(String id);

	void deleteRftReminder(RftReminder rftReminder);

	/**
	 * @param eventId
	 * @param userId
	 * @param dbTeamMember TODO
	 * @return
	 */

	List<User> removeTeamMemberfromList(String eventId, String userId, RftTeamMember dbTeamMember);

	RftTeamMember getRftTeamMemberByUserIdAndEventId(String eventId, String userId);

	List<Buyer> getAllActivePublicEventBuyers();

	List<PublicEventPojo> getActivePublicEvents(Country country, Buyer buyer);

	boolean isExists(RftEventContact rftEventContact);

	List<RftTeamMember> addTeamMemberToList(String eventId, String userId, TeamMemberType teamMemberType);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getTeamMembersForEvent(String eventId);

	long findTotalDraftEventForBuyer(String tenantId, String userId, TableDataInput input);

	List<RftEventApproval> getApprovalsForEvent(String id);

	List<ActiveEventPojo> getAllActiveEventForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId);

	long findTotalActiveEventForBuyer(String tenantId, String userId, TableDataInput input);

	long findTotalPendingEventForBuyer(String tenantId, String userId, TableDataInput input);

	List<PendingEventPojo> getAllPendingEventForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId);

	/**
	 * @param userId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserPemissionsForEvent(String userId, String eventId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalOngoingEventForBuyer(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @param days
	 * @param input TODO
	 * @return
	 */
	long findTotalFinishedEventForBuyer(String tenantId, String userId, int days, TableDataInput input);

	/**
	 * @param event
	 * @return TODO
	 */
	RftEvent updateEventApproval(RftEvent event, User loggedInUser);

	/**
	 * @param eventId
	 * @throws ApplicationException
	 */
	void insertTimeLine(String eventId) throws ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	List<RftEventTimeLine> getRftEventTimeline(String id);

	/**
	 * @param eventId
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
	JasperPrint getEvaluationSummaryPdf(RftEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param userId
	 * @param eventId
	 * @param envelopeId
	 * @return
	 */
	EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId);

	/**
	 * @param event
	 * @param session
	 * @param virtualizer TODO
	 * @param loggedInUser TODO
	 * @return
	 * @throws Exception
	 */
	RftEvent cancelEvent(RftEvent event, HttpSession session, JRSwapFileVirtualizer virtualizer, User loggedInUser) throws Exception;

	/**
	 * @param eventId
	 * @param meetingId
	 * @param session
	 * @return
	 */
	JasperPrint getMeetingAttendanceReport(String eventId, String meetingId, HttpSession session);

	/**
	 * @param event
	 * @param loggedInUser TODO
	 * @return
	 */
	RftEvent concludeRftEvent(RftEvent event, User loggedInUser);

	/**
	 * @param rftevent
	 * @param selectedRfxType
	 * @param auctionType
	 * @param bqId
	 * @param loggedInUser
	 * @param idRfxTemplate
	 * @param businessUnitId
	 * @return
	 * @throws Exception
	 */
	String createNextEvent(RftEvent rftevent, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, String concludeRemarks, String[] invitedSuppliers) throws Exception;

	/**
	 * @param loggedInUserTenantId
	 * @param opVal
	 * @param status
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @param userId
	 * @return
	 */
	List<RfxView> getAllEventBasedOnSearchvalue(String loggedInUserTenantId, String opVal, String status, String type, Date startDate, Date endDate, String userId);

	/**
	 * @return
	 */
	List<Buyer> getAllActiveBuyersForIntegration();

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getAwardedSuppliers(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RftEvent getPlainEventById(String eventId);

	/**
	 * @param event
	 */
	void suspendEvent(RftEvent event);

	/**
	 * @param event
	 */
	void resumeEvent(RftEvent event);

	/**
	 * @param eventId
	 * @return
	 */
	public List<Supplier> getEventSuppliersForSummary(String eventId);

	/**
	 * @param country
	 * @param buyer
	 * @return
	 */
	List<PublicEventPojo> getActivePublicEventsForIntegration(Country country, Buyer buyer);

	/**
	 * @param eventId
	 * @return
	 */
	public long getEventSuppliersCount(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Date> getAllMeetingDateByEventId(String eventId);

	/**
	 * @param tenantId
	 * @param input
	 * @param userId
	 * @return
	 */
	List<DraftEventPojo> getAllCancelledEventForBuyer(String tenantId, TableDataInput input, String userId);

	/**
	 * @param tenantId
	 * @param input
	 * @param userId
	 * @return
	 */
	List<DraftEventPojo> getAllSuspendedEventsForBuyer(String tenantId, TableDataInput input, String userId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalSuspendedEventForBuyer(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalCancelledEventForBuyer(String tenantId, String userId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param userId
	 * @return
	 */
	long findTotalMyPendingApprovals(String loggedInUserTenantId, String userId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input
	 * @return
	 */
	List<PendingEventPojo> findMyPendingRfxApprovals(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input
	 * @return
	 */
	List<PendingEventPojo> findMyPendingEvaluation(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalMyPendingEvaluation(String tenantId, String userId, TableDataInput input);
	long findTotalMyPendingEvaluationBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input
	 * @return
	 */
	List<PendingEventPojo> findMyPendingPrApprovals(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalClosedEventForBuyer(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param input
	 * @param userId
	 * @return
	 */
	List<DraftEventPojo> getAllClosedEventForBuyer(String tenantId, TableDataInput input, String userId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalCompletedEventForBuyer(String tenantId, String userId, TableDataInput input);

	/**
	 * @param id
	 * @return
	 */
	Integer findAssignedTemplateCount(String id);

	/**
	 * @param eventId
	 * @return
	 */
	User getPlainEventOwnerByEventId(String eventId);

	/**
	 * @param loggedInUserTenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalMyPendingPrApprovals(String loggedInUserTenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param input
	 * @param userId
	 * @return
	 */
	List<DraftEventPojo> getAllCompletedEventsForBuyer(String tenantId, TableDataInput input, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getPlainTeamMembersForEvent(String eventId);

	/**
	 * @param eventId
	 * @param eventType
	 * @return
	 */
	boolean checkTemplateStatusForEvent(String eventId, String eventType);

	/**
	 * @param eventId
	 * @return
	 */
	String findBusinessUnitName(String eventId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param search
	 * @return
	 */
	List<ApprovedRejectEventPojo> findMyAprrovedRejectList(String tenantId, String userId, SearchSortFilterPojo search);

	/**
	 * @param tenantId
	 * @param userId
	 * @param search
	 * @return
	 */
	List<PendingEventPojo> findMyToDoList(String tenantId, String userId, SearchSortFilterPojo search);

	/**
	 * @param id
	 * @param userId
	 * @return
	 * @throws ApplicationException
	 */
	MobileEventPojo getMobileEventDetails(String id, String userId) throws ApplicationException;

	/**
	 * @param tenantId
	 * @param userId
	 * @param search
	 * @return
	 */
	List<ApprovedRejectEventPojo> findMyEventList(String tenantId, String userId, SearchSortFilterPojo search);

	/**
	 * @param tenantId
	 * @param status
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	long findAggregateEventCountForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception;

	/**
	 * @param tenantId
	 * @param status
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	BigDecimal findAggregateEventBudgetAmountValueForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception;

	/**
	 * @param tenantId
	 * @param status
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	BigDecimal findAggregateEventAwardedPriceValueForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception;

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
	RftEvent getRftEventByeventIdForSummaryReport(String eventId);

	/**
	 * @param tenantId
	 * @param asList
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	BigDecimal findAggregateClosedCompletedEventValueForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception;

	/**
	 * @param eventId
	 * @param bqId
	 * @param tenantId TODO
	 * @return
	 */
	RftSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId, String tenantId);

	/**
	 * @param eventId
	 * @param bqId
	 * @param tenantId TODO
	 * @return
	 */
	RftSupplierBq getSupplierBQOfLowestItemisedPrice(String eventId, String bqId, String tenantId);

	/**
	 * @param eventId
	 * @param bqId
	 * @param supplierId
	 * @param tenantId TODO
	 * @param awardId TODO
	 * @return
	 */
	RftSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId, String tenantId, String awardId);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */
	long findCountOfAllActiveEventForSupplier(String supplierId, String userId);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */
	long findCountOfAllSuspendedEventForSupplier(String supplierId, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */
	long findCountOfAllClosedEventForSupplier(String supplierId, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */
	long findCountOfAllRejectedEventForSupplier(String supplierId, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */
	long findCountOfAllPendingEventForSupplier(String supplierId, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */
	long findCountOfAllCompletedEventForSupplier(String supplierId, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */
	List<RfxView> getOnlyAllSuspendedEventsForSupplier(String supplierId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */
	List<RfxView> getOnlyAllActiveEventsForSupplier(String supplierId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */
	List<RfxView> getOnlyAllClosedEventsForSupplier(String supplierId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */
	List<RfxView> getOnlyAllCompletedEventsForSupplier(String supplierId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */
	List<RfxView> getOnlyAllRejectedEventsForSupplier(String supplierId, TableDataInput input, String id);

	/**
	 * @param supplierId
	 * @param input
	 * @param id
	 * @return
	 */
	List<RfxView> getOnlyAllPendingEventsForSupplier(String supplierId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */
	long findTotalPendingEventForForSupplier(String supplierId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */
	long findTotalOnlyAllSuspendedEventsForSupplier(String supplierId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */
	long findTotalOnlyAllActiveEventsForSupplier(String supplierId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */
	long findTotalOnlyAllClosedEventsForSupplier(String supplierId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */
	long findTotalOnlyAllCompletedEventsForSupplier(String supplierId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */
	long findTotalOnlyAllRejectedEventsForSupplier(String loggedInUserTenantId, TableDataInput input, String id);

	/**
	 * @param eventId
	 * @return
	 */
	RftEvent getRftEventWithIndustryCategoriesByEventId(String eventId);

	boolean isExistsRftEventId(String tenantId, String eventId);

	void createRftFromTemplate(RftEvent newEvent, RfxTemplate rfxTemplate, BusinessUnit selectedbusinessUnit, User loggedInUser, boolean isFromRequest) throws ApplicationException;

	List<RftTeamMember> addAssociateOwners(User createdBy, RftEvent newEvent);

	void setDefaultEventContactDetail(String loggedInUserId, String eventId);

	List<DraftEventPojo> getAllEventForBuyer(String tenantId, TableDataInput input, String userid, Date startDate, Date endDate);

	List<DraftEventPojo> getAllExcelEventReportForBuyer(String tenantId, String[] eventArr, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate);

	List<RfxView> getOnlyAllAcceptedEventsForSupplier(String loggedInUserTenantId, TableDataInput input, String id);

	long findTotalAcceptedEventForForSupplier(String loggedInUserTenantId, TableDataInput input, String id);

	long findCountOfAllAcceptedEventForSupplier(String loggedInUserTenantId, String id);

	RftEvent findRftEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId);

	String createPrFromAward(RftEventAward rftEventAward, String templateId, String userId, User loggedInUser) throws ApplicationException;

	JasperPrint getEvaluationReport(String eventId, String evenvelopId, String attribute, JRSwapFileVirtualizer virtualizer);

	JasperPrint generatePdfforEvaluationSummary(String eventId, String envelopeId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer);

	JasperPrint generateSubmissionReport(String evenvelopId, String eventId, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	// void autoSaveRftDetails(RftEvent rfaEvent, String[] industryCategory, BindingResult result, HttpSession session,
	// boolean b);
	void autoSaveRfaDetails(RftEvent rfaEvent, String[] industryCategory, BindingResult result, String strTimeZone);

	RftEvent getRftEventWithBuByeventId(String eventId);

	List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	EventTimerPojo getTimeEventByeventId(String eventId);

	/**
	 * @param tenantId
	 * @param id
	 * @param search
	 * @return
	 */
	List<PendingEventPojo> findMyToDoListForSupplier(String tenantId, String id, SearchSortFilterPojo search);

	/**
	 * @param tenantId
	 * @param userId
	 * @param search
	 * @return
	 */
	List<ApprovedRejectEventPojo> findMyEventListForSupplier(String tenantId, String userId, SearchSortFilterPojo search);

	/**
	 * @param id
	 * @param userId
	 * @param tenantId TODO
	 * @return
	 * @throws ApplicationException
	 */
	MobileEventPojo getMobileEventDetailsForSupplier(String id, String userId, String tenantId) throws ApplicationException;

	RftEvent loadRftEventForSummeryPageById(String id);

	Event loadEventForSummeryPageForSupplierById(String eventId);

	List<RftEvent> getAllRftEventByTenantId(String tenantId);

	Event getEventByEventRefranceNo(String eventRefranceNo, String string);

	EventPojo loadEventPojoForSummeryPageForSupplierById(String eventId);

	EventPojo loadSupplierEventPojoForSummeryById(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RftEvent getPlainEventWithOwnerById(String eventId);

	List<DraftEventPojo> getAllEventsForBuyerEventReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate);

	long findTotalAllEventsForBuyerEventReport(String loggedInUserTenantId);

	long getInvitedSupplierCount(String eventId);

	long getParticepatedSupplierCount(String eventId);

	long getSubmitedSupplierCount(String eventId);

	List<RftSupplierBq> getLowestSubmissionPrice(String eventId);

	List<EventSupplierPojo> getSuppliersByStatus(String eventId);

	// RftSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId);

	RftEnvelop getBqForEnvelope(String envelopeId);

	/**
	 * @param buyerId
	 * @return
	 */
	List<PublicEventPojo> getActivePublicEventsByTenantId(String buyerId);

	/**
	 * @param buyerId
	 * @param input
	 * @return
	 */
	long findTotalFilteredActivePublicEventsList(String buyerId, TableDataInput input);

	/**
	 * @param buyerId
	 * @return
	 */
	long findTotalActivePublicEventsList(String buyerId);

	/**
	 * @param buyerId
	 * @param input
	 * @return
	 */
	List<PublicEventPojo> findActivePublicEventsByTenantId(String buyerId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<PublicEventPojo> findActivePublicEventsListByTenantId(String tenantId, TableDataInput input);

	/**
	 * @param eventId
	 * @param eventType TODO
	 * @return
	 */
	String findTenantIdBasedOnEventIdAndEventType(String eventId, RfxTypes eventType);

	/**
	 * @param event
	 * @return
	 */
	JasperPrint getPublicEventSummaryPdf(EventPojo event);

	/**
	 * @param eventId
	 * @return
	 */
	EventPojo getRftForPublicEventByeventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<IndustryCategory> getIndustryCategoriesForRftById(String eventId);

	/**
	 * @param eventId
	 * @param eventType TODO
	 * @return
	 */
	EventPojo findMinMaxRatingsByEventId(String eventId, RfxTypes eventType);

	/**
	 * @param eventId
	 * @param eventType TODO
	 * @return
	 */
	boolean isIndustryCategoryMandatoryInEvent(String eventId, RfxTypes eventType);

	long getAllEventsCountForBuyerEventReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate);

	int updateEventPushedDate(String eventId);

	Double getAvarageBidPriceSubmitted(String id);

	List<String> getEventTeamMember(String string);

	int updatePrPushDate(String eventId);

	int updateEventAward(String eventId);

	/**
	 * @param eventId
	 * @param status
	 */
	void updateImmediately(String eventId, EventStatus status);

	/**
	 * @param eventId
	 */
	void updateEventStartMessageFlagImmediately(String eventId);

	long findTotalEventForBuyer(String loggedInUserTenantId, String userId);

	List<RftEvent> getAllRftEventbyTenantidInitial(String loggedInUserTenantId, String id) throws SubscriptionException;

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountByEventId(String eventId);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalAdminEventByTenantId(String tenantId);

	Event loadRftEventModalById(String eventId, Model model, SimpleDateFormat format, List<User> assignedTeamMembers, List<User> approvalUsers, List<User> suspApprovalUsers) throws CloneNotSupportedException;

	/**
	 * @param eventId
	 * @return
	 */
	Event getSimpleEventDetailsById(String eventId);

	String getEventOwnerId(String eventId);

	boolean doValidateOwnerUserEnvople(String id);

	boolean doValidateOwnerUserUnmaskUser(String eventId);

	boolean doValidateOwnerUserApprover(String eventId);

	boolean doValidateOwnerUserTeamMember(String eventId);

	/**
	 * @param eventId
	 * @param id
	 * @return
	 */
	RftEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String id);

	/**
	 * @param eventType
	 * @param eventId
	 * @param envelopeId
	 * @param timeZone
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generateShortEvaluationSummaryReport(String eventType, String eventId, String envelopeId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer);

	List<RftEventAwardAudit> getRftEventAwardByEventId(String eventId);

	/**
	 * @param event
	 * @param loggedInUser
	 * @param attribute
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint getEventAuditPdf(RftEvent event, User loggedInUser, String attribute, JRSwapFileVirtualizer virtualizer);

	Boolean isDefaultPreSetEnvlope(String eventId);

	byte[] buildFaxForSupplierPdf(String supplierName, String buyerName, String eventName, BusinessUnit businessUnit, RfxTypes rfxTypes, String refrance, EventStatus eventStatus);

	List<String> getIndustryCategoriesIdForRftById(String eventId, RfxTypes eventType);

	/**
	 * @param eventId
	 * @param eventType TODO
	 * @return
	 */
	Declaration getDeclarationForSupplierByEventId(String eventId, RfxTypes eventType);

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
	RftEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId);

	/**
	 * @param eventId
	 * @param timeZone
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generatePdfforEvaluationConclusion(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param rftEvent
	 * @return
	 */
	RftEvent updateEventSimple(RftEvent rftEvent);

	/**
	 * @param userId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserEventPemissions(String userId, String eventId);

	/**
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long findTotalEventMyPendingApprovals(String tenantId, String userId);

	/**
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long findTotalPrMyPendingApprovals(String tenantId, String userId);

	/**
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long findTotalRfsMyPendingApprovals(String tenantId, String userId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input
	 * @return
	 */
	long findTotalMyPendingRfxApprovals(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long findTotalSupplierFormMyPendingApprovals(String tenantId, String userId);

	/**
	 * @param recipientEmail
	 * @param subject
	 * @param description
	 * @param event
	 * @param name
	 * @param feeReference
	 */
	void sendEmailAfterParticipationFees(String recipientEmail, String subject, String description, RftEvent event, String name, String feeReference, String timezone);

	/**
	 * @param supplierId
	 * @param userId
	 * @return
	 */
	long findCountOfAllActivePendingEventCountForSupplier(String supplierId, String userId);

	/**
	 * @param supplierId
	 * @param userId
	 * @return
	 */
	long findCountOfAllActiveSubmittedEventCountForSupplier(String supplierId, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param userId
	 * @return
	 */
	List<RfxView> getOnlyActivePendingEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param userId
	 * @return
	 */
	long findTotalCountOfActivePendingEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param userId
	 * @return
	 */
	List<RfxView> getOnlyActiveSubmittedEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param userId
	 * @return
	 */
	long findTotalCountOfActiveSubmittedEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param response
	 * @param file
	 * @param eventIds
	 * @param searchFilterEventPojo
	 * @param startDate
	 * @param endDate
	 * @param select_all
	 * @param tenantId
	 */
	void downloadCsvFileForEvents(HttpServletResponse response, File file, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, Date startDate, Date endDate, boolean select_all, String tenantId, HttpSession session);

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long getRftEventCountByTenantId(String searchVal, String tenantId, String userId);

	List<Event> findEventsByEventNameAndRefNumAndIndCatForTenant(String searchValue, Integer pageNo, Integer pageLength, String industryCategory, RfxTypes eventType, String tenantId, String userId) throws SubscriptionException;

	long findActiveEventCountByRfxTypeForTenantId(String searchValue, String tenantId, RfxTypes eventType, String userId, String industryCategory);

	List<PendingEventPojo> findSuspendedEventsPendingApprovals(String tenantId, String userId, TableDataInput input);

	long findCountOfSuspendedEventsPendingApprovals(String tenantId, String userId, TableDataInput input);

	long findCountOfSuspendedEventPendingApprovals(String tenantId, String userId);

	/**
	 * @param event
	 * @param user
	 * @return
	 */
	RftEvent updateEventSuspensionApproval(RftEvent event, User user);

	void downloadRftEvaluatorDocument(String envelopId, HttpServletResponse response);

	void downloadRftLeadEvaluatorDocument(String envelopId, HttpServletResponse response);

	String createContractFromAward(RftEventAward rftEventAward, String eventId, String contractStartDate, String contractEndDate, String groupCodeHid, String referenceNumberHid, User loggedInUser, HttpSession session, String contractCreatorHid) throws ApplicationException;

	/**
	 * @param tenantId
	 * @param userId
	 * @param input
	 * @return
	 */
	List<PendingEventPojo> findMyPendingPoApprovals(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long findTotalRevisePendingPoApprovals(String tenantId, String userId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input
	 * @return
	 */
	long findTotalRevisePendingPoApprovals(String tenantId, String userId, TableDataInput input);

	/**
	 * @param eventId
	 */
	void revertEventAwardStatus(String eventId);
	
	List<String> createSpFormFromAward(RftEventAward rftEventAward, String templateId, String eventId, String userId, User loggedInUser) throws ApplicationException;

	List<PendingEventPojo> findMyPendingRfxAwardApprovals(String loggedInUserTenantId, String id, TableDataInput input);

	long findTotalMyPendingRfxAwardApprovals(String loggedInUserTenantId, String id, TableDataInput input);

	long findTotalMyPendingAwardApprovals(String loggedInUserTenantId, String id);

	//4105

	List<DraftEventPojo> getAllFinishedEventsForBuyerBizUnit(String tenantId, Date lastLoginTime, int days, TableDataInput input, String userId,List<String> businessUnitIds);
	long findTotalFinishedEventForBizUnit(String tenantId, String userId, int days, TableDataInput input,List<String> businessUnitIds);
	List<DraftEventPojo> getAllClosedEventForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds);
	long findTotalClosedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds);
	List<DraftEventPojo> getAllSuspendedEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds);
	long findTotalSuspendedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds);
	List<DraftEventPojo> getAllCompletedEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds);
	long findTotalCompletedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds);
	List<DraftEventPojo> getAllCancelledEventForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds);
	long findTotalCancelledEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds);
	long findTotalPendingEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds);
	List<PendingEventPojo> getAllPendingEventForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds);
	List<ActiveEventPojo> getAllActiveEventForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds);
	long findTotalActiveEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds);
	List<OngoingEventPojo> getAllOngoingEventsForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds);
	long findTotalOngoingEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds);

	RftEvent loadEventForSummeryPageById(String id);
}
