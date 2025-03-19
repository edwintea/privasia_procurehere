package com.privasia.procurehere.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAward;
import com.privasia.procurehere.core.entity.RfpEventAwardAudit;
import com.privasia.procurehere.core.entity.RfpEventContact;
import com.privasia.procurehere.core.entity.RfpEventCorrespondenceAddress;
import com.privasia.procurehere.core.entity.RfpEventTimeLine;
import com.privasia.procurehere.core.entity.RfpReminder;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.entity.RfpSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.pojo.RfpEventCAddressPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface RfpEventService {

	/**
	 * @param rfpEvent
	 * @param loggedInUser TODO
	 * @return
	 * @throws SubscriptionException
	 */
	RfpEvent saveEvent(RfpEvent rfpEvent, User loggedInUser) throws SubscriptionException;

	/**
	 * @param rfiEvent
	 * @return
	 */
	RfpEvent updateEvent(RfpEvent rfpEvent);

	/**
	 * @param rfiEvent
	 */
	void deleteEvent(RfpEvent rfpEvent);

	/**
	 * @param id
	 * @return
	 */
	RfpEvent getEventById(String id);

	/**
	 * @param searchValue
	 * @return
	 */
	List<NaicsCodes> findLeafIndustryCategoryByName(String searchValue);

	/**
	 * @param rfpEventContact
	 */
	void saveEventContact(RfpEventContact rfpEventContact);

	/**
	 * @param rfiEventCorrespondenceAddress
	 */
	void saveEventCorrespondenceAddress(RfpEventCorrespondenceAddress rfiEventCorrespondenceAddress);

	/**
	 * @param id
	 * @return
	 */
	IndustryCategory getIndustryCategoryForRftById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventContact> getAllContactForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventCorrespondenceAddress> getAllCAddressForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventCAddressPojo> getAllCorrespondenceAddressPojo(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfpEvent loadRfpEventById(String id);

	/**
	 * @param rfiEvent
	 * @return
	 */
	boolean isExists(RfpEvent rfiEvent);

	/**
	 * @param id
	 * @return
	 */
	RfpEvent loadRfpEventForSummeryById(String id);

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
	Integer getCountOfRfpCqByEventId(String eventId);

	/**
	 * @param tenantId
	 * @param loggedInUser TODO
	 * @param pageNo
	 * @param industryCategory
	 * @param searchValue
	 * @return
	 */
	List<DraftEventPojo> getAllEventByTenantId(String tenantId, String loggedInUser, String pageNo, String searchValue, String industryCategory) throws SubscriptionException;;

	/**
	 * @param rfiSupplierMeetingAttendance
	 * @return
	 */
	boolean isExists(RfpSupplierMeetingAttendance rfiSupplierMeetingAttendance);

	/**
	 * @param meetingId
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	RfpSupplierMeetingAttendance loadSupplierMeetingAttendenceByEventId(String meetingId, String eventId, String tenantId);

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
	RfpEvent copyFromTemplate(String templateId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	RfpEventContact getEventContactById(String id);

	/**
	 * @param eventContact
	 */
	void deleteContact(RfpEventContact eventContact);

	/**
	 * @param rfpEventContact
	 */
	void updateEventContact(RfpEventContact rfpEventContact);

	/**
	 * @param rfpEventContact
	 */
	void updateRfpEventContact(RfpEventContact rfpEventContact);

	/**
	 * @param rfpReminder
	 */
	void saveRfpEventReminder(RfpReminder rfpReminder);

	/**
	 * @param rfpReminder
	 */
	void updateRfpEventReminder(RfpReminder rfpReminder);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpReminder> getAllRfpEventReminderForEvent(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfpReminder getRfpEventReminderById(String id);

	/**
	 * @param rfpReminder
	 */
	void deleteRfpReminder(RfpReminder rfpReminder);

	/**
	 * @param id
	 * @return
	 */
	RfpEvent loadRfpBqsForSupplierByEventId(String id);

	List<User> addEditorUser(String eventId, String userId);

	void updateRfpEvent(RfpEvent rfpEvent);

	List<User> removeEditorUser(String eventId, String userId);

	RfpEvent getRfpEventByeventId(String eventId);

	List<User> addViwersToList(String eventId, String userId);

	List<User> removeViewersfromList(String eventId, String userId);

	List<RfpTeamMember> addTeamMemberToList(String eventId, String userId, TeamMemberType memberType);

	List<User> removeTeamMemberfromList(String eventId, String userId, RfpTeamMember dbTeamMember);

	RfpTeamMember getRfpTeamMemberByUserIdAndEventId(String eventId, String userId);

	/**
	 * @param eventId
	 * @param createdBy
	 * @param businessUnit
	 * @throws SubscriptionException
	 * @throws ApplicationException
	 */
	RfpEvent copyFrom(String eventId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException;

	void updateEventReminder(RfpReminder rfpReminder);

	/**
	 * @param rfpEventService
	 * @return
	 */
	boolean isExists(RfpEventContact rfpEventContact);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getTeamMembersForEvent(String eventId);

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
	RfpEvent updateEventApproval(RfpEvent event, User LoggedInUser);

	/**
	 * @param id
	 * @throws ApplicationException
	 */
	void insertTimeLine(String id) throws ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	List<RfpEventTimeLine> getRfpEventTimeline(String id);

	/**
	 * @param userId
	 * @param eventId
	 * @param envelopeId
	 * @return
	 */
	EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId);

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
	JasperPrint getEvaluationSummaryPdf(RfpEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param event
	 * @param session
	 * @param virtualizer TODO
	 * @return
	 * @throws Exception
	 */
	RfpEvent cancelEvent(RfpEvent event, HttpSession session, JRSwapFileVirtualizer virtualizer) throws Exception;

	/**
	 * @param eventId
	 * @param session
	 * @return
	 */
	JasperPrint getMeetingAttendanceReport(String eventId, String meetingId, HttpSession session);

	RfpEvent concludeRfpEvent(RfpEvent event, User loggedInUser);

	String createNextEvent(RfpEvent event, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, String concludeRemarks, String[] invitedSupp) throws Exception;

	List<Supplier> getAwardedSuppliers(String id);

	RfpEvent getPlainEventById(String eventId);

	/**
	 * @param event
	 */
	void suspendEvent(RfpEvent event);

	/**
	 * @param event
	 */
	void resumeEvent(RfpEvent event);

	long getEventSuppliersCount(String eventId);

	List<Date> getAllMeetingDateByEventId(String eventId);

	Integer findAssignedTemplateCount(String id);

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
	 * @param loggedInUser TODO
	 * @param virtualizer TODO
	 */
	void downloadEventSummary(String eventId, HttpServletResponse response, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param eventId
	 * @return
	 */
	RfpEvent getRfpEventByeventIdForSummaryReport(String eventId);

	RfpSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String id, String tenantId);

	RfpSupplierBq getSupplierBQOfLowestItemisedPrice(String eventId, String id, String tenantId);

	RfpSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String id, String tenantId, String awardId);

	/**
	 * @param eventId
	 * @return
	 */
	RfpEvent getRfpEventWithIndustryCategoriesByEventId(String eventId);

	boolean isExistsRfpEventId(String tenantId, String eventId);

	void createRfpFromTemplate(RfpEvent newEvent, RfxTemplate rfxTemplate, Object object, BusinessUnit selectedbusinessUnit, User loggedInUser, boolean isFormRfs) throws ApplicationException;

	List<RfpTeamMember> addAssociateOwners(User createdBy, RfpEvent newEvent);

	void setDefaultEventContactDetail(String loggedInuserId, String eventId);

	String createPrFromAward(RfpEventAward rfpEventAward, String templateId, String userId, User loggedInUser) throws ApplicationException;

	RfpEvent findRfpEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId);

	List<DraftEventPojo> getAllExcelEventReportForBuyer(String loggedInUserTenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate);

	JasperPrint getEvaluationReport(String eventId, String evenvelopId, String attribute, JRSwapFileVirtualizer virtualizer);

	JasperPrint generatePdfforEvaluationSummary(String eventId, String envelopeId, JRSwapFileVirtualizer virtualizer);

	JasperPrint generateSubmissionReport(String evenvelopId, String eventId, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	void autoSaveRfpDetails(RfpEvent rfaEvent, String[] industryCategory, BindingResult result, String strTimeZone);

	RfpEvent getRfpEventWithBuByeventId(String eventId);

	EventTimerPojo getTimeEventByeventId(String eventId);

	List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	/**
	 * @param id
	 * @param userId
	 * @param tenantId TODO
	 * @return
	 * @throws ApplicationException
	 */
	MobileEventPojo getMobileEventDetailsForSupplier(String id, String userId, String tenantId) throws ApplicationException;

	List<RfpEvent> getAllRfpEventByTenantId(String tenantId);

	Event getEventByEventRefranceNo(String eventRefranceNo, String string);

	EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId);

	EventPojo loadSupplierEventPojoForSummeryById(String eventId);

	long getInvitedSupplierCount(String eventId);

	long getParticepatedSupplierCount(String eventId);

	long getSubmitedSupplierCount(String eventId);

	List<RfpSupplierBq> getLowestSubmissionPrice(String eventId);

	List<EventSupplierPojo> getSuppliersByStatus(String eventId);

	// RfpSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId);

	RfpEnvelop getBqForEnvelope(String envelopeId);

	/**
	 * @param eventId
	 * @return
	 */
	EventPojo getRfpForPublicEventByeventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<IndustryCategory> getIndustryCategoriesForRfpById(String eventId);

	/**
	 * @param event
	 * @return
	 */
	JasperPrint getPublicEventSummaryPdf(EventPojo event);

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

	List<RfpEvent> getAllEventByTenantIdInitial(String loggedInUserTenantId, String object) throws SubscriptionException;

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountByEventId(String eventId);

	Event loadRfpEventModalById(String eventId, Model model, SimpleDateFormat format, List<User> assignedTeamMembers, List<User> approvalUsers, List<User> suspApprovalUsers) throws CloneNotSupportedException;

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
	RfpEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String id);

	List<RfpEventAwardAudit> getRfpEventAwardByEventId(String eventId);

	/**
	 * @param event
	 * @param loggedInUser
	 * @param attribute
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint getEventAuditPdf(RfpEvent event, User loggedInUser, String attribute, JRSwapFileVirtualizer virtualizer);

	Boolean isDefaultPreSetEnvlope(String eventId);

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
	RfpEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId);

	/**
	 * @param eventId
	 * @param timeZone
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generatePdfforEvaluationConclusion(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param recipientEmail
	 * @param subject
	 * @param description
	 * @param event
	 * @param name
	 * @param feeReference
	 */
	void sendEmailAfterParticipationFees(String recipientEmail, String subject, String description, RfpEvent event, String name, String feeReference, String timezone);

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long getRfpEventCountByTenantId(String searchVal, String tenantId, String userId);

	/**
	 * @param event
	 * @param user
	 * @return
	 */
	RfpEvent updateEventSuspensionApproval(RfpEvent event, User user);

	void downloadRfpEvaluatorDocument(String envelopId, HttpServletResponse response);

	void downloadRfpLeadEvaluatorDocument(String envelopId, HttpServletResponse response);

	String createContractFromAward(RfpEventAward rfpEventAward, String eventId, String contractStartDate, String contractEndDate, String groupCodeHid, String referenceNumberHid, User loggedInUser, HttpSession session, String contractCreator) throws ApplicationException;

	/**
	 * 
	 * @param eventId
	 */
	void revertEventAwardStatus(String eventId);
	
	List<String> createSpFormFromAward(RfpEventAward rfpEventAward, String templateId, String eventId, String userId, User loggedInUser) throws ApplicationException;
}
