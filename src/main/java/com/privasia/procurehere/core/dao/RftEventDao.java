package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventApproval;
import com.privasia.procurehere.core.entity.RftEventAwardAudit;
import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.ActiveEventPojo;
import com.privasia.procurehere.core.pojo.ApprovedRejectEventPojo;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FinishedEventPojo;
import com.privasia.procurehere.core.pojo.OngoingEventPojo;
import com.privasia.procurehere.core.pojo.PendingEventPojo;
import com.privasia.procurehere.core.pojo.PublicEventPojo;
import com.privasia.procurehere.core.pojo.RequestParamPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.SearchSortFilterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface RftEventDao extends GenericEventDao<RftEvent, String> {

	RftEvent findByEventId(String eventId);

	/**
	 * @deprecated something is funny about this method. Please validate
	 * @param searchParam
	 * @return
	 */
	@Deprecated
	List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam);

	/**
	 * @param rftEvent
	 * @return
	 */
	boolean isExists(RftEvent rftEvent);

	/**
	 * @param tenantId
	 * @param input
	 * @param userId
	 * @return
	 */
	List<DraftEventPojo> getAllDraftEventsForBuyer(String tenantId, TableDataInput input, String userId);

	/**
	 * @param tenantId
	 * @param lastLoginTime
	 * @param input
	 * @param userId
	 * @return
	 */
	List<OngoingEventPojo> getAllOngoingEventsForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId);

	/**
	 * @param tenantId
	 * @param lastLoginTime
	 * @param days
	 * @param input
	 * @param userId
	 * @return
	 */
	List<FinishedEventPojo> getAllFinishedEventsForBuyer(String tenantId, Date lastLoginTime, int days, TableDataInput input, String userId);

	/**
	 * @deprecated This method should be in Favourite Supplier DAO/Service
	 * @param id
	 * @return
	 */
	/*
	 * @Deprecated FavouriteSupplier getFavouriteSupplierBySupplierId(String id);
	 */
	/**
	 * @param supplierId
	 * @param userId TODO
	 * @return
	 */
	List<RfxView> findAllEventForSupplier(String supplierId, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	RftEvent findEventForCqByEventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RftEvent findBySupplierEventId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfEnvelopByEventId(String eventId);

	/**
	 * @param tenantId
	 * @param loggedInUser TODO
	 * @param indCat
	 * @param serchVal
	 * @return
	 */
	List<DraftEventPojo> getAllRftEventByTenantId(String tenantId, String loggedInUser, String pageNo, String serchVal, String indCat);

	/**
	 * @param searchValue
	 * @param industryCategory
	 * @param tenantId
	 * @param loggedInUser TODO
	 * @return
	 */
	List<RftEvent> findByEventNameaAndRefNumAndIndCatForTenant(String searchValue, String industryCategory, String tenantId, String loggedInUser);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliers(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RftEvent findEventForBqByEventId(String eventId);

	/**
	 * @param eventId
	 * @param userId
	 * @return
	 */
	RftTeamMember getRftTeamMemberByUserIdAndEventId(String eventId, String userId);

	/**
	 * @return
	 */
	List<Buyer> getAllActivePublicEventBuyers();

	/**
	 * @param country
	 * @param buyer
	 * @return
	 */
	List<PublicEventPojo> getActivePublicEvents(Country country, Buyer buyer);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getTeamMembersForEvent(String eventId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalDraftEventForBuyer(String tenantId, String userId, TableDataInput input);

	/**
	 * @param id
	 * @return
	 */
	List<RftEventApproval> getApprovalsForEvent(String id);

	/**
	 * @param tenantId
	 * @param lastLoginTime
	 * @param input
	 * @param userId
	 * @return
	 */
	List<ActiveEventPojo> getAllActiveEventsForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalActiveEventForBuyer(String tenantId, String userId, TableDataInput input);

	/**
	 * @param id
	 * @return
	 */
	List<RftEventApproval> getAllApprovalsForEvent(String id);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @param input TODO
	 * @return
	 */
	long findTotalPendingEventForBuyer(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param lastLoginTime
	 * @param input
	 * @param userId
	 * @return
	 */
	List<PendingEventPojo> getAllPendingEventsForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId);

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
	 * @param userId
	 * @param eventId
	 * @param envelopeId
	 * @return
	 */
	EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId);

	/**
	 * @param tenantId
	 * @param searchValue
	 * @param status
	 * @param type
	 * @param userId TODO
	 * @return
	 */
	List<RfxView> findAllEventForSearchvalue(String tenantId, String searchValue, String status, String type, Date startDate, Date endDate, String userId);

	List<Supplier> getAwardedSuppliers(String eventId);

	RftEvent getPlainEventById(String eventId);

	List<RftEvent> getAllRftEventByLoginId(String loginId);

	List<DraftEventPojo> getAllCancelledEventsForBuyer(String tenantId, TableDataInput input, String userId);

	List<DraftEventPojo> getAllSuspendedEventsForBuyer(String tenantId, TableDataInput input, String userId);

	long findTotalSuspendedEventForBuyer(String tenantId, String userId, TableDataInput input);

	long findTotalCancelledEventForBuyer(String tenantId, String userId, TableDataInput input);

	long findTotalMyPendingApprovals(String tenantId, String userId);

	/**
	 * @param id
	 * @return
	 */
	List<String> getEventSuppliersId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftTeamMember> getBuyerTeamMemberByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	User getPlainEventOwnerByEventId(String eventId);

	/**
	 * @param eventId
	 */
	void updateEventStartMessageFlag(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getUserBuyerTeamMemberByEventId(String eventId);

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
	List<DraftEventPojo> getAllClosedEventsForBuyer(String tenantId, TableDataInput input, String userId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalCompletedEventForBuyer(String tenantId, String userId, TableDataInput input);

	Integer findAssignedTemplateCount(String templateId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	long findTotalMyPendingPrApprovals(String tenantId, String userId, TableDataInput input);

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
	 * @return
	 */
	RftEvent getMobileEventDetails(String id);

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
	 * @param tenantId
	 * @param status
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	BigDecimal findAggregateClosedCompletedEventValueForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception;

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
	 * @param eventId
	 * @param bqId
	 * @return
	 */
	RftSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId);

	/**
	 * @param eventId
	 * @param bqId
	 * @return
	 */
	List<RftSupplierBq> getSupplierBQOfLowestItemisedPrize(String eventId, String bqId);

	/**
	 * @param id
	 * @param eventId
	 * @return
	 */
	RftSupplierBqItem getMinItemisePrice(String id, String eventId);

	/**
	 * @param eventId
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	RftSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId);

	/**
	 * @param supplierId
	 * @param object
	 * @return
	 */
	long findCountOfAllActiveEventForSupplier(String supplierId, String userId);

	/**
	 * @param supplierId
	 * @param userId
	 * @return
	 */
	long findCountOfAllSuspendedEventForSupplier(String supplierId, String userId);

	/**
	 * @param supplierId
	 * @param object
	 * @return
	 */
	long findCountOfAllClosedEventForSupplier(String supplierId, String userId);

	/**
	 * @param supplierId
	 * @param object
	 * @return
	 */
	long findCountOfAllRejectedEventForSupplier(String supplierId, String userId);

	/**
	 * @param supplierId
	 * @param userId
	 * @return
	 */
	long findCountOfAllPendingEventForSupplier(String supplierId, String userId);

	/**
	 * @param supplierId
	 * @param userId
	 * @return
	 */
	long findCountOfAllCompletedEventForSupplier(String supplierId, String userId);

	/**
	 * @param tenantId
	 * @param input
	 * @param userId
	 * @return
	 */
	List<RfxView> getOnlyAllSuspendedEventsForSupplier(String tenantId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param object
	 * @return
	 */
	List<RfxView> getOnlyAllActiveEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param object
	 * @return
	 */
	List<RfxView> getOnlyAllClosedEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param object
	 * @return
	 */
	List<RfxView> getOnlyAllCompletedEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param object
	 * @return
	 */
	List<RfxView> getOnlyAllRejectedEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param object
	 * @return
	 */
	List<RfxView> getOnlyAllPendingEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param userId
	 * @return
	 */
	long findTotalPendingEventForForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param object
	 * @return
	 */
	long findTotalOnlyAllSuspendedEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param object
	 * @return
	 */
	long findTotalOnlyAllActiveEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param userId
	 * @return
	 */
	long findTotalOnlyAllClosedEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param object
	 * @return
	 */
	long findTotalOnlyAllCompletedEventsForSupplier(String supplierId, TableDataInput input, String userId);

	/**
	 * @param supplierId
	 * @param input
	 * @param object
	 * @return
	 */
	long findTotalOnlyAllRejectedEventsForSupplier(String supplierId, TableDataInput input, String userId);

	boolean isExistsRftEventId(String tenantId, String eventId);

	List<DraftEventPojo> getAllEventsForBuyer(String tenantId, EventStatus status);

	List<RfxView> getAllEventsForSupplier(String tenantId, SubmissionStatusType status);

	List<String> getEventIdList(String tenantId);

	void deleteAllComments(String eventId);

	void deleteAllDocument(String eventId);

	List<String> getenvelopIdList(String eventId);

	void deleteEvaluatorUser(String envelopID);

	void deleteEnvelop(String eventId);

	void deleteEventAddress(String eventId);

	List<String> getApprovalIdList(String eventId);

	void deleteApprovalUser(String aprovalID);

	void deleteAproval(String eventId);

	void deleteAudit(String tenantId);

	void deleteAwardAudit(String tenantId);

	void deleteEventAward(String eventId);

	void deleteBqEvaluationComments(String eventId);

	List<String> getBqIdList(String eventId);

	void deleteAwardDetail(String bqId);

	void deleteBq(String eventId);

	void deleteSupplierComments(String bqId);

	void deleteBqSupplierItem(String bqId, boolean isParent);

	List<String> getSupplierBqIdList(String eventId);

	void deleteSupplierBq(String eventId);

	void deleteEventContact(String eventId);

	List<String> getCqIdList(String eventId);

	void deleteEvalutionCqComts(String eventId);

	List<String> getCqItemIdList(String cqId);

	void deletCqOption(String cqItemId);

	void deletSuppCqOption(String cqItemId);

	void deleteCq(String eventId);

	List<String> getSupplierCqIdList(String eventId);

	List<String> getSupplierCqItemIdList(String cqId);

	void deleteCqItem(String eventId);

	void deleteCqParentItem(String eventId);

	void deleteSupplierCqItem(String cqId);

	void deleteSupplierCqParentItem(String cqId);

	void deleteBqItem(String bqId, boolean isParent);

	List<String> getMeetingIdList(String eventId);

	void deleteMeetingContact(String mid);

	void deleteMeetingDoc(String tenantId);

	void deleteMeetingReminder(String mid);

	void deleteSupplierMeetingAtt(String eventId);

	void deleteSupplierTeam(String eventId);

	void deleteEventSupplier(String eventId);

	void deleteTimeLine(String tenantId);

	void deleteEventReminder(String eventId);

	List<String> getBqItemList(String bqId);

	List<String> getSppBqItemList(String bqId);

	void deleteBqItems(String eventId);

	void deleteeventBqItems(String eventId, boolean isParent);

	void deleteSupplierBqItems(String eventId, boolean isParent);

	void deleteSupplierCqOption(String cqId);

	void deleteAwardDetails(String eventId);

	void deleteEventAwardByBq(String bqId);

	void deleteAwardDetailbyItem(String bqItemId);

	void deleteEvent(String eventId);

	void deleteMeeting(String eventId);

	void deleteMettingContact(String meetingId);

	List<String> getEventMeettingIds(String eventId);

	void deleteEventMessage(String eventId, boolean isparent);

	void deleteBuyerTeam(String eventId);

	List<DraftEventPojo> getAllEventsForBuyer(String tenantId, TableDataInput input, String userid, Date startDate, Date endDate);

	List<DraftEventPojo> getAllExcelEventReportForBuyer(String tenantId, String[] eventArr);

	List<RfxView> getOnlyAllAcceptedEventsForSupplier(String supplierId, TableDataInput input, String object);

	long findTotalAcceptedEventForForSupplier(String supplierId, TableDataInput input, String object);

	long findCountOfAllAcceptedEventForSupplier(String supplierId, String userId);

	RftEvent findRftEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId);

	/**
	 * @param tenantId
	 * @param eventIds
	 * @param searchFilterEventPojo
	 * @param select_all
	 * @param endDate
	 * @param startDate
	 * @param input
	 * @param eventtype
	 * @return
	 */
	List<RftEvent> getEventsByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate);

	List<RftEvent> getSearchEventReportssByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate);

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
	List<ApprovedRejectEventPojo> findMyAllEventListForSupplier(String tenantId, String userId, SearchSortFilterPojo search);

	List<RftEvent> getAllRftEventByTenantId(String tenantId);

	Event getEventByEventRefranceNo(String eventRefranceNo, String tenantId);

	User getUnMaskedUserNameAndMailByEventId(String eventId);

	EventPojo findEventPojoById(String id);

	EventPojo loadSupplierEventPojoForSummeryById(String eventId);

	/**
	 * @param eventId
	 */
	void updateEventMeetingFlagsToFalse(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RftEvent getPlainEventWithOwnerById(String eventId);

	List<DraftEventPojo> getAllEventsForBuyerEventReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate);

	long findTotalAllEventsForBuyerEventReport(String loggedInUserTenantId);

	List<IndustryCategory> getIndustryCategoryListForRfaById(String id);

	/**
	 * @param id
	 * @return
	 */
	List<EventSupplierPojo> getEventSuppliersAndTimeZone(String id);

	void updateEventStatus(EventStatus status, String id);

	RftEvent getRftEventForShortSummary(String eventId);

	long getInvitedSupplierCount(String eventId);

	long getParticepatedSupplierCount(String eventId);

	long getSubmitedSupplierCount(String eventId);

	List<RftSupplierBq> getLowestSubmissionPrice(String eventId);

	List<EventSupplierPojo> getSuppliersByStatus(String eventId);

	// RftSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId);

	RftEnvelop getBqForEnvelope(String envelopeId);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<PublicEventPojo> findActivePublicEventsListByTenantId(String tenantId, TableDataInput input);

	/**
	 * @param eventId
	 * @param eventType
	 * @return
	 */
	String findTenantIdBasedOnEventIdAndEventType(String eventId, RfxTypes eventType);

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
	 * @param eventType
	 * @return
	 */
	EventPojo findMinMaxRatingsByEventId(String eventId, RfxTypes eventType);

	/**
	 * @param eventId
	 * @return
	 */
	boolean isMinMaxSupplierRatingAvaliableInEvent(String eventId);

	/**
	 * @param eventId
	 * @param eventType TODO
	 * @return
	 */
	boolean isIndustryCategoryMandatoryInEvent(String eventId, RfxTypes eventType);

	Supplier findWinnerSupplier(String id);

	List<DraftEventPojo> getAllEventwithSearchFilter(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate);

	long getAllEventsCountForBuyerEventReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate);

	int updateEventPushedDate(String eventId);

	Double getAvarageBidPriceSubmitted(String id);

	List<String> getEventTeamMember(String eventId);

	int updatePrPushDate(String eventId);

	int updateEventAward(String eventId);

	/**
	 * @param eventId
	 * @param status
	 * @return
	 */
	int updateImmediately(String eventId, EventStatus status);

	long findTotalEventForBuyer(String tenantId, String userId);

	List<RftEvent> getAllRftEventbyTenantidInitial(String tenantId, String loggedInUser) throws SubscriptionException;

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountByEventId(String eventId);

	/**
	 * @return
	 */
	List<RftEvent> getAllActiveEvents();

	/**
	 * @return
	 */
	List<RftEvent> getAllApprovedEventsforJob();

	void updateRfaForAwardPrice(String id, BigDecimal sumAwardPrice);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalAdminEventByTenantId(String tenantId);

	String getEventOwnerId(String eventId);

	boolean doValidateOwnerUserEnvelope(String eventId);

	boolean doValidateOwnerUserUnmaskUser(String eventId);

	boolean doValidateOwnerUserApprover(String eventId);

	boolean doValidateOwnerUserTeamMember(String eventId);

	/**
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	RftEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId);

	List<RftEventAwardAudit> getRftEventAwardByEventId(String eventId);

	Boolean isDefaultPreSetEnvlope(String eventId);

	/**
	 * @return
	 */
	List<RftEvent> getAllRftEventWhereUnMaskingUserIsNotNull();

	/**
	 * @param eventId
	 */
	void updateEventUnMaskUser(String eventId);

	List<String> getIndustryCategoriesIdForRftById(String eventId, RfxTypes eventType);

	/**
	 * @param eventId
	 * @param eventType TODO
	 * @return
	 */
	Declaration getDeclarationForSupplierByEventId(String eventId, RfxTypes eventType);

	/**
	 * @param eventId
	 * @param evalConUserId
	 * @return
	 */
	RftEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEvaluationConclusionUser> findEvaluationConclusionUsersByEventId(String eventId);

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

	List<EventTeamMember> findAssociateOwnerOfRft(String id, TeamMemberType associateOwner);

	/**
	 * @param eventId
	 * @return
	 */
	RftEvent getEventDetailsForFeePayment(String eventId);

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
	 * @param object
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
	 * @param tenantId
	 * @return
	 */
	long findTotalEventsCountForCsv(String tenantId);

	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @param eventIds
	 * @param searchFilterEventPojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<DraftEventPojo> findAllActiveEventsForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate);

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param userId
	 * @param industryCategory TODO
	 * @return
	 */
	long getRftEventCountByTenantId(String searchVal, String tenantId, String userId, String industryCategory);

	List<RftEvent> findEventsByEventNameAndRefNumAndIndCatForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, String industryCategory, RfxTypes eventType, String tenantId, String loggedInUser);

	List<PendingEventPojo> findSuspendedEventsPendingApprovals(String tenantId, String userId, TableDataInput input);

	long findCountOfSuspendedEventsPendingApprovals(String tenantId, String userId, TableDataInput input);

	long findCountOfSuspendedEventPendingApprovals(String tenantId, String userId);

	/**
	 * @param eventId
	 * @param memberType
	 * @return
	 */
	List<User> getAssociateOwnersOfEventsByEventId(String eventId, TeamMemberType memberType);

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

	/**
	 * @param loginId
	 * @return
	 */
	List<Event> getAllRftEventIdsByLoginId(String loginId);

	/**
	 * @param fromUserId
	 * @param toUserId
	 */
	void updateTransferOwnerForEvent(String fromUserId, String toUserId);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input
	 * @return
	 */
	List<PendingEventPojo> findMyPendingRfxAwardApprovals(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input
	 * @return
	 */
	long findTotalMyPendingRfxAwardApprovals(String tenantId, String userId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long findTotalMyPendingAwardApprovals(String tenantId, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	RftEvent findEventForSapByEventId(String eventId);

	/**
	 * @param tenantId
	 * @param lastLoginTime
	 * @param days
	 * @param input
	 * @param userId
	 * @return
	 */
	List<DraftEventPojo> getAllFinishedEventsForBizUnit(String tenantId, Date lastLoginTime, int days, TableDataInput input, String userId,List<String> businessUnitIds);
	long findTotalFinishedEventForBizUnit(String tenantId, String userId, int days, TableDataInput input,List<String> businessUnitIds);

	List<DraftEventPojo> getAllClosedEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds);
	long findTotalClosedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds);

	List<DraftEventPojo> getAllSuspendedEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds);
	long findTotalSuspendedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds);
	List<DraftEventPojo> getAllCompletedEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds);
	long findTotalCompletedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds);
	List<DraftEventPojo> getAllCancelledEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds);
	long findTotalCancelledEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds);
	List<PendingEventPojo> getAllPendingEventsForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds);
	long findTotalPendingEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds);
	List<ActiveEventPojo> getAllActiveEventsForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds);
	long findTotalActiveEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds);
	List<OngoingEventPojo> getAllOngoingEventsForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds);
	long findTotalOngoingEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds);


	RftEvent findEventSorByEventId(String id);
}
