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
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfiEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventContact;
import com.privasia.procurehere.core.entity.RfiEventCorrespondenceAddress;
import com.privasia.procurehere.core.entity.RfiEventTimeLine;
import com.privasia.procurehere.core.entity.RfiReminder;
import com.privasia.procurehere.core.entity.RfiSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RfiTeamMember;
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
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author arc
 */
public interface RfiEventService {

	/**
	 * @param rfiEvent
	 * @return
	 * @throws SubscriptionException
	 */
	RfiEvent saveRfiEvent(RfiEvent rfiEvent) throws SubscriptionException;

	/**
	 * @param rfiEvent
	 * @return
	 */
	RfiEvent updateRfiEvent(RfiEvent rfiEvent);

	/**
	 * @param rfiEvent
	 */
	void deleteRfiEvent(RfiEvent rfiEvent);

	/**
	 * @param id
	 * @return
	 */
	RfiEvent getRfiEventById(String id);

	/**
	 * @param rfiEventContact
	 * @return TODO
	 */
	RfiEventContact saveRfiEventContact(RfiEventContact rfiEventContact);

	/**
	 * @param rfiEventCorrespondenceAddress
	 */
	void saveRfiEventCorrespondenceAddress(RfiEventCorrespondenceAddress rfiEventCorrespondenceAddress);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventContact> getAllContactForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventCorrespondenceAddress> getAllCAddressForEvent(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfiEvent loadRfiEventById(String id);

	/**
	 * @param rftEvent
	 * @return
	 */
	boolean isExists(RfiEvent rfiEvent);

	/**
	 * @param id
	 * @return
	 */
	RfiEventContact getEventContactById(String id);

	/**
	 * @param tenantId
	 * @param loggedInUser TODO
	 * @param industryCategory
	 * @param searchValue
	 * @return
	 */
	List<DraftEventPojo> getAllRfiEventByTenantId(String tenantId, String loggedInUser, String pageNo, String searchValue, String industryCategory) throws SubscriptionException;;

	/**
	 * @param eventName
	 * @param referenceNumber
	 * @param industryCategory
	 * @param tenantId
	 * @return
	 */
	List<RfiEvent> findByEventNameaAndRefNumAndIndCatForTenant(String eventName, String referenceNumber, String industryCategory, String tenantId);

	/**
	 * @param eventId
	 * @param createdBy
	 * @param businessUnit
	 * @return
	 * @throws SubscriptionException
	 * @throws ApplicationException
	 */
	RfiEvent copyFrom(String eventId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException;

	/**
	 * @param templateId
	 * @param createdBy
	 * @param businessUnit
	 * @return
	 * @throws SubscriptionException
	 * @throws ApplicationException
	 */
	RfiEvent copyFromTemplate(String templateId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException;

	/**
	 * @param searchParam
	 * @param buyerId TODO
	 * @return
	 */
	List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId);

	/**
	 * @param searchParams
	 * @param buyerId TODO
	 * @return
	 */
	List<FavouriteSupplier> searchCustomSupplier(SupplierSearchPojo searchParams, String buyerId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfEnvelopByEventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfiEvent loadRfiEventForSummeryById(String id);

	/**
	 * @param rfiReminder
	 */
	void saveRfiEventReminder(RfiReminder rfiReminder);

	/**
	 * @param rfiReminder
	 */
	void updateRfiEventReminder(RfiReminder rfiReminder);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiReminder> getAllRfiEventReminderForEvent(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfiReminder getRfiEventReminderById(String id);

	/**
	 * @param rfiReminder
	 */
	void deleteRfiReminder(RfiReminder rfiReminder);

	List<User> addEditorUser(String eventId, String userId);

	List<User> removeEditorUser(String eventId, String userId);

	RfiEvent getRfiEventByeventId(String eventId);

	List<User> addViwersToList(String eventId, String userId);

	List<User> removeViewersfromList(String eventId, String userId);

	List<User> removeTeamMembersfromList(String eventId, String userId, RfiTeamMember dbTeamMember);

	RfiTeamMember getRfiTeamMemberByUserIdAndEventId(String eventId, String userId);

	void updateEventReminder(RfiReminder rfiReminder);

	void deleteContact(RfiEventContact eventContact);

	boolean isExists(RfiEventContact rfiEventContact);

	List<RfiTeamMember> addTeamMemberToList(String eventId, String userId, TeamMemberType memberType);

	List<EventTeamMember> getTeamMembersForEvent(String eventId);

	/**
	 * @param meetingId
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	RfiSupplierMeetingAttendance loadSupplierMeetingAttendenceByEventId(String meetingId, String eventId, String tenantId);

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
	RfiEvent updateEventApproval(RfiEvent event, User loggedInUser);

	/**
	 * @param id
	 * @throws ApplicationException
	 */
	void insertTimeLine(String id) throws ApplicationException;

	/**
	 * @param id
	 * @return
	 */
	List<RfiEventTimeLine> getRfiEventTimeline(String id);

	/**
	 * @param userId
	 * @param eventId
	 * @param envelopeId
	 * @return
	 */
	EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId);

	/**
	 * @param eventId
	 * @param timeZone
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
	JasperPrint getEvaluationSummaryPdf(RfiEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param event
	 * @param virtualizer TODO
	 * @param loggedInUser TODO
	 * @return
	 */
	RfiEvent cancelEvent(RfiEvent event, HttpSession session, JRSwapFileVirtualizer virtualizer, User loggedInUser);

	/**
	 * @param eventId
	 * @param meetingId
	 * @param session
	 * @return
	 */
	JasperPrint getMeetingAttendanceReport(String eventId, String meetingId, HttpSession session);

	String createNextEvent(RfiEvent event, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, String concludeRemarks, String[] invitedSupp) throws Exception;

	RfiEvent getPreviousEventById(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEvent getPlainEventById(String eventId);

	/**
	 * @param event
	 */
	void suspendEvent(RfiEvent event);

	/**
	 * @param event
	 */
	void resumeEvent(RfiEvent event);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliers(String eventId);

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
	RfiEvent getRfiEventByeventIdForSummaryReport(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEvent getRfiEventWithIndustryCategoriesByEventId(String eventId);

	boolean isExistsRfiEventId(String tenantId, String eventId);

	void createRfiFromTemplate(RfiEvent newEvent, RfxTemplate rfxTemplate, BusinessUnit selectedbusinessUnit, User loggedInUser, boolean isFromRfs) throws ApplicationException;

	List<RfiTeamMember> addAssociateOwners(User createdBy, RfiEvent newEvent);

	void setDefaultEventContactDetail(String id, String id2);

	List<DraftEventPojo> getAllExcelEventReportForBuyer(String loggedInUserTenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate);

	JasperPrint getEvaluationReport(String eventId, String evenvelopId, String attribute, JRSwapFileVirtualizer virtualizer);

	JasperPrint generatePdfforEvaluationSummary(String eventId, String envelopeId, JRSwapFileVirtualizer virtualizer);

	JasperPrint generateSubmissionReport(String evenvelopId, String eventId, String strTimeZone, JRSwapFileVirtualizer virtualizer);

	void autoSaveRfaDetails(RfiEvent rfiEvent, String[] industryCategory, BindingResult result, String strTimeZone);

	List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	EventTimerPojo getTimeEventByeventId(String eventId);

	MobileEventPojo getMobileEventDetailsForSupplier(String id, String userId, String tenantId) throws ApplicationException;

	List<RfiEvent> getAllRfiEventByTenantId(String tenantId);

	Event getEventByEventRefranceNo(String eventRefranceNo, String string);

	RfiEvent getPlainEventWithTemplateById(String id);

	EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId);

	EventPojo loadSupplierEventPojoForSummeryById(String eventId);

	long getInvitedSupplierCount(String eventId);

	long getParticepatedSupplierCount(String eventId);

	long getSubmitedSupplierCount(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	EventPojo getRfiForPublicEventByeventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<IndustryCategory> getIndustryCategoriesForRfiById(String eventId);

	/**
	 * @param event
	 * @return
	 */
	JasperPrint getPublicEventSummaryPdf(EventPojo event);

	int updateEventPushedDate(String id);

	List<String> getEventTeamMember(String string);

	/**
	 * @param eventId
	 * @param status
	 */
	void updateImmediately(String eventId, EventStatus status);

	/**
	 * @param eventId
	 */
	void updateEventStartMessageFlagImmediately(String eventId);

	List<RfiEvent> getAllRfiEventByTenantIdInitial(String loggedInUserTenantId, String string) throws SubscriptionException;

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountByEventId(String eventId);

	Event loadRfiEventModalById(String eventId, Model model, SimpleDateFormat format, List<User> assignedTeamMembers, List<User> approvalUsers, List<User> suspApprovalUsers) throws CloneNotSupportedException;

	/**
	 * @param eventId
	 * @return
	 */
	Event getSimpleEventDetailsById(String eventId);

	/**
	 * @param event
	 * @param loggedInUser
	 * @param attribute
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint getEventAuditPdf(RfiEvent event, User loggedInUser, String attribute, JRSwapFileVirtualizer virtualizer);

	Boolean isDefaultPreSetEnvlope(String eventId, User loggedInUser);

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
	RfiEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId);

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
	void sendEmailAfterParticipationFees(String recipientEmail, String subject, String description, RfiEvent event, String name, String feeReference, String timezone);

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long getRfiEventCountByTenantId(String searchVal, String tenantId, String userId);

	/**
	 * @param event
	 * @param user
	 * @return
	 */
	RfiEvent updateEventSuspensionApproval(RfiEvent event, User user);

	void downloadRfiEvaluatorDocument(String id, HttpServletResponse response);

	void downloadRfiLeadEvaluatorDocument(String envelopId, HttpServletResponse response);

	/**
	 * @param event
	 * @param loggedInUser
	 * @return
	 */
	RfiEvent concludeRfiEvent(RfiEvent event, User loggedInUser);

}
