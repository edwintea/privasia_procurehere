package com.privasia.procurehere.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAward;
import com.privasia.procurehere.core.entity.RfqEventAwardAudit;
import com.privasia.procurehere.core.entity.RfqEventContact;
import com.privasia.procurehere.core.entity.RfqEventCorrespondenceAddress;
import com.privasia.procurehere.core.entity.RfqEventTimeLine;
import com.privasia.procurehere.core.entity.RfqReminder;
import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.entity.RfqSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RfqTeamMember;
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
import com.privasia.procurehere.core.pojo.EventCAddressPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface RfqEventService {

	/**
	 * @param rfpEvent
	 * @param loggedInUser TODO
	 * @return
	 * @throws SubscriptionException
	 */
	RfqEvent saveEvent(RfqEvent rfpEvent, User loggedInUser) throws SubscriptionException;

	/**
	 * @param rfiEvent
	 * @return
	 */
	RfqEvent updateEvent(RfqEvent rfpEvent);

	/**
	 * @param rfiEvent
	 */
	void deleteEvent(RfqEvent rfpEvent);

	/**
	 * @param id
	 * @return
	 */
	RfqEvent getEventById(String id);

	/**
	 * @param searchValue
	 * @return
	 */
	List<NaicsCodes> findLeafIndustryCategoryByName(String searchValue);

	/**
	 * @param rfpEventContact
	 */
	void saveEventContact(RfqEventContact rfpEventContact);

	/**
	 * @param eventCorrespondenceAddress
	 */
	void saveEventCorrespondenceAddress(RfqEventCorrespondenceAddress eventCorrespondenceAddress);

	/**
	 * @param id
	 * @return
	 */
	IndustryCategory getIndustryCategoryForRftById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventContact> getAllContactForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventCorrespondenceAddress> getAllCAddressForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventCAddressPojo> getAllCorrespondenceAddressPojo(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfqEvent loadRfqEventById(String id);

	/**
	 * @param rfiEvent
	 * @return
	 */
	boolean isExists(RfqEvent rfiEvent);

	/**
	 * @param id
	 * @return
	 */
	RfqEvent loadEventForSummeryById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfEnvelopByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	/**
	 * @param rftReminder
	 */

	Integer getCountOfDocumentByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfqCqByEventId(String eventId);

	/**
	 * @param tenantId
	 * @param loggedInUser TODO
	 * @param industryCategory
	 * @param searchValue
	 * @param pageNO
	 * @return
	 */
	List<DraftEventPojo> getAllEventByTenantId(String tenantId, String loggedInUser, String pageNo, String searchValue, String industryCategory) throws SubscriptionException;;

	/**
	 * @param supplierMeetingAttendance
	 * @return
	 */
	boolean isExists(RfqSupplierMeetingAttendance supplierMeetingAttendance);

	/**
	 * @param meetingId
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	RfqSupplierMeetingAttendance loadSupplierMeetingAttendenceByEventId(String meetingId, String eventId, String tenantId);

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
	RfqEvent copyFromTemplate(String templateId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	RfqEventContact getEventContactById(String id);

	/**
	 * @param eventContact
	 */
	void deleteContact(RfqEventContact eventContact);

	/**
	 * @param eventContact
	 */
	void updateEventContact(RfqEventContact eventContact);

	/**
	 * @param id
	 * @return
	 */
	RfqEvent loadBqsForSupplierByEventId(String id);

	List<User> addEditorUser(String eventId, String userId);

	List<User> removeEditorUser(String eventId, String userId);

	RfqEvent getRfqEventByeventId(String eventId);

	/**
	 * @param rftReminder
	 */
	void saveEventReminder(RfqReminder rftReminder);

	/**
	 * @param rftReminder
	 */
	void updateEventReminder(RfqReminder rftReminder);

	List<RfqReminder> getAllEventReminderForEvent(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfqReminder getRfqEventReminderById(String id);

	/**
	 * @param rfqReminder
	 */
	void deleteRfqReminder(RfqReminder rfqReminder);

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
	List<User> removeTeamMemberfromList(String eventId, String userId, RfqTeamMember dbTeamMember);

	/**
	 * @param eventId
	 * @param userId
	 * @param memberType TODO
	 * @return
	 */
	List<RfqTeamMember> addTeamMemberToList(String eventId, String userId, TeamMemberType memberType);

	/**
	 * @param eventId
	 * @param userId
	 * @return
	 */
	List<User> addViwersToList(String eventId, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqReminder> getAllRfqEventReminderForEvent(String eventId);

	/**
	 * @param eventId
	 * @param createdBy
	 * @param businessUnit
	 * @return
	 * @throws SubscriptionException
	 * @throws ApplicationException
	 */
	RfqEvent copyFrom(String eventId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException;

	/**
	 * @param rfqEventContact
	 * @return
	 */
	boolean isExists(RfqEventContact rfqEventContact);

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
	RfqEvent updateEventApproval(RfqEvent event, User loggedInUser);

	/**
	 * @param id
	 * @throws ApplicationException
	 */
	void insertTimeLine(String id) throws ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	List<RfqEventTimeLine> getRfqEventTimeline(String id);

	/**
	 * @param userId
	 * @param eventId
	 * @param envelopeId
	 * @return
	 */
	EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId);

	/**
	 * @param eventId
	 * @param envelopeId
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generatePdfforEvaluationSummary(String eventId, String envelopeId, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param event
	 * @param loggedInUser TODO
	 * @param strTimeZone TODO
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint getEvaluationSummaryPdf(RfqEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param event
	 * @param virtualizer TODO
	 * @param loggedInUser TODO
	 * @return
	 * @throws Exception
	 */
	RfqEvent cancelEvent(RfqEvent event, HttpSession session, JRSwapFileVirtualizer virtualizer, User loggedInUser) throws Exception;

	/**
	 * @param eventId
	 * @param meetingId
	 * @param session
	 * @return
	 */
	JasperPrint getMeetingAttendanceReport(String eventId, String meetingId, HttpSession session);

	RfqEvent concludeRfqEvent(RfqEvent event, User loggedInUser);

	String createNextEvent(RfqEvent event, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, String concludeRemarks, String[] invitedSupp) throws Exception;

	List<Supplier> getAwardedSuppliers(String id);

	RfqEvent getPlainEventById(String eventId);

	/**
	 * @param event
	 */
	void suspendEvent(RfqEvent event);

	/**
	 * @param event
	 */
	void resumeEvent(RfqEvent event);

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
	RfqEvent getRfqEventByeventIdForSummaryReport(String eventId);

	/**
	 * @param eventId
	 * @param bqId
	 * @param tenantId TODO
	 * @return
	 */
	RfqSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId, String tenantId);

	/**
	 * @param eventId
	 * @param bqId
	 * @param tenantId TODO
	 * @return
	 */
	RfqSupplierBq getSupplierBQOfLowestItemisedPrice(String eventId, String bqId, String tenantId);

	RfqSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String id, String tenantId, String awardId);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEvent getRfqEventWithIndustryCategoriesByEventId(String eventId);

	boolean isExistsRfqEventId(String tenantId, String eventId);

	void createRfqFromTemplate(RfqEvent newEvent, RfxTemplate rfxTemplate, BusinessUnit selectedbusinessUnit, User loggedInUser, boolean isFromRequest) throws ApplicationException;

	List<RfqTeamMember> addAssociateOwners(User createdBy, RfqEvent newEvent);

	void setDefaultEventContactDetail(String id, String id2);

	RfqTeamMember getRfqTeamMemberByUserIdAndEventId(String eventId, String userId);

	RfqEvent findRfqEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId);

	String createPrFromAward(RfqEventAward rfqEventAward, String templateId, String userId, User loggedInUser) throws ApplicationException;

	List<DraftEventPojo> getAllExcelEventReportForBuyer(String loggedInUserTenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate);

	JasperPrint getEvaluationReport(String eventId, String evenvelopId, String attribute, JRSwapFileVirtualizer virtualizer);

	JasperPrint generatePdfforEvaluationSummary(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer);

	JasperPrint generateSubmissionReport(String evenvelopId, String eventId, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	void autoSaveRfqDetails(RfqEvent rfaEvent, String[] industryCategory, BindingResult result, String strTimeZone1);

	RfqEvent getRfqEventWithBuByEventId(String eventId);

	List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	EventTimerPojo getTimeEventByeventId(String eventId);

	/**
	 * @param id
	 * @param userId
	 * @param tenantId TODO
	 * @return
	 * @throws ApplicationException
	 */
	MobileEventPojo getMobileEventDetailsForSupplier(String id, String userId, String tenantId) throws ApplicationException;

	RfqEvent loadEventForSummeryPageById(String id);

	RfqEvent loadEventForSummeryPageForSupplierById(String id);

	List<RfqEvent> getAllRfqEventByTenantId(String tenantId);

	Event getEventByEventRefranceNo(String eventRefranceNo, String string);

	EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId);

	EventPojo loadSupplierEventPojoForSummeryById(String eventId);

	long getInvitedSupplierCount(String eventId);

	long getParticepatedSupplierCount(String eventId);

	long getSubmitedSupplierCount(String eventId);

	List<RfqSupplierBq> getLowestSubmissionPrice(String eventId);

	List<EventSupplierPojo> getSuppliersByStatus(String eventId);

	// RfqSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId);

	RfqEnvelop getBqForEnvelope(String envelopeId);

	/**
	 * @param eventId
	 * @return
	 */
	EventPojo getRfqForPublicEventByeventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<IndustryCategory> getIndustryCategoriesForRfqById(String eventId);

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

	List<RfqEvent> getAllEventByTenantIdInitial(String loggedInUserTenantId, String string) throws SubscriptionException;

	List<RfqEvent> getAllEventByTenantIdAndRfqTemplateId(String loggedInUserTenantId, String string,String rfqTemplateId) throws SubscriptionException;

	Event loadRfqEventModalById(String eventId, Model model, SimpleDateFormat format, List<User> assignedTeamMembers, List<User> approvalUser, List<User> suspApprovalUsers) throws CloneNotSupportedException;

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountByEventId(String eventId);

	/**
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	Event getSimpleEventDetailsById(String eventId);

	RfqEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId);

	/**
	 * @param event
	 * @param loggedInUser TODO
	 * @param strTimeZone TODO
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint getEventAuditPdf(RfqEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	List<RfqEventAwardAudit> getRfqEventAwardByEventId(String eventId);

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
	RfqEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId);

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
	void sendEmailAfterParticipationFees(String recipientEmail, String subject, String description, RfqEvent event, String name, String feeReference, String timezone);

	long getRfqEventCountByTenantId(String searchVal, String tenantId, String userId);

	/**
	 * @param event
	 * @param user
	 * @return
	 */
	RfqEvent updateEventSuspensionApproval(RfqEvent event, User user);

	void downloadRfqEvaluatorDocument(String id, HttpServletResponse response);

	void downloadRfqLeadEvaluatorDocument(String envelopId, HttpServletResponse response);

	String createContractFromAward(RfqEventAward rfqEventAward, String eventId, String contractStartDate, String contractEndDate, String groupCodeHid, String referenceNumberHid, User loggedInUser, HttpSession session, String contractCreatorHid) throws ApplicationException;

	/**
	 * @param eventId
	 */
	void revertEventAwardStatus(String eventId);

	/**
	 * @param rfqEventAward
	 * @param templateId
	 * @param eventId
	 * @param userId
	 * @param loggedInUser
	 * @return
	 * @throws ApplicationException
	 */
	List<String> createSpFormFromAward(RfqEventAward rfqEventAward, String templateId, String eventId, String userId, User loggedInUser) throws ApplicationException;

}
