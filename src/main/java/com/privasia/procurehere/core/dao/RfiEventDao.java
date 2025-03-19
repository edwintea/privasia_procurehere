package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Ravi
 */
public interface RfiEventDao extends GenericEventDao<RfiEvent, String> {

	/**
	 * @param rfiEvent
	 * @return
	 */
	boolean isExists(RfiEvent rfiEvent);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliers(String eventId);

	/**
	 * @param tenantId
	 * @param loggedInUser TODO
	 * @param pageNo
	 * @param indCat
	 * @param serchVal
	 * @return
	 */
	List<DraftEventPojo> getAllRfiEventByTenantId(String tenantId, String loggedInUser, String pageNo, String serchVal, String indCat);

	/**
	 * @param searchValue
	 * @param industryCategory
	 * @param tenantId
	 * @param loggedInUser TODO
	 * @return
	 */
	List<RfiEvent> findByEventNameaAndRefNumAndIndCatForTenant(String searchValue, String industryCategory, String tenantId, String loggedInUser);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfEnvelopByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEvent findByEventId(String eventId);

	/**
	 * @param eventId
	 * @param userId
	 * @return
	 */
	RfiTeamMember getRfiTeamMemberByUserIdAndEventId(String eventId, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getTeamMembersForEvent(String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<RfiEventApproval> getAllApprovalsForEvent(String id);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEvent findEventForCqByEventId(String eventId);

	/**
	 * @param userId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserPemissionsForEvent(String userId, String eventId);

	/**
	 * @param userId
	 * @param eventId
	 * @param envelopeId
	 * @return
	 */
	EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId);

	RfiEvent getPreviousEventById(String eventId);

	List<RfiEvent> getAllRfiEventByLoginId(String loginId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> getEventSuppliersId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiTeamMember> getBuyerTeamMemberByEventId(String eventId);

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

	Integer findAssignedTemplateCount(String templateId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEvent getPlainEventById(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getPlainTeamMembersForEvent(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfiEvent getMobileEventDetails(String id);

	boolean isExistsRfiEventId(String tenantId, String eventId);

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

	List<String> getMeetingIdList(String eventId);

	void deleteMeetingContact(String mid);

	void deleteMeetingDoc(String tenantId);

	void deleteMeetingReminder(String mid);

	void deleteSupplierMeetingAtt(String eventId);

	void deleteSupplierTeam(String eventId);

	void deleteEventSupplier(String eventId);

	void deleteTimeLine(String tenantId);

	void deleteEventReminder(String eventId);

	void deleteSupplierCqOption(String cqId);

	void deleteEvent(String eventId);

	void deleteMeeting(String eventId);

	void deleteMettingContact(String meetingId);

	List<String> getEventMeettingIds(String eventId);

	void deleteEventMessage(String eventId, boolean isparent);

	void deleteBuyerTeam(String eventId);

	List<RfiEvent> getEventsByIds(String tenantId, String[] eventArr, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate);

	List<RfiEvent> getSearchEventsByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date endDate, Date startDate);

	List<RfiEvent> getAllRfiEventByTenantId(String tenantId);

	Event getEventByEventRefranceNo(String eventRefranceNo, String string);

	User getUnMaskedUserNameAndMailByEventId(String eventId);

	EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId);

	EventPojo loadSupplierEventPojoForSummeryById(String eventId);

	List<IndustryCategory> getIndustryCategoryListForRfaById(String id);

	/**
	 * @param id
	 * @return
	 */
	List<EventSupplierPojo> getEventSuppliersAndTimeZone(String id);

	void updateEventStatus(EventStatus status, String id);

	RfiEvent getRfiEventForShortSummary(String eventId);

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
	 * @param eventId
	 * @return
	 */
	boolean isMinMaxSupplierRatingAvaliableInEvent(String eventId);

	int updateEventPushedDate(String id);

	List<String> getEventTeamMember(String eventId);

	/**
	 * @param eventId
	 * @param status
	 * @return
	 */
	int updateImmediately(String eventId, EventStatus status);

	List<RfiEvent> getAllRfiEventByTenantIdInitial(String loggedInUserTenantId, String string);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountByEventId(String eventId);

	/**
	 * @return
	 */
	List<RfiEvent> getAllActiveEvents();

	/**
	 * @return
	 */
	List<RfiEvent> getAllApprovedEventsforJob();

	Boolean isDefaultPreSetEnvlope(String eventId, User loggedInUser);

	/**
	 * @return
	 */
	List<RfiEvent> getAllRfiEventWhereUnMaskingUserIsNotNull();

	/**
	 * @param eventId
	 */
	void updateEventUnMaskUser(String eventId);

	/**
	 * @param eventId
	 * @param evalConUserId
	 * @return
	 */
	RfiEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEvent getPlainEventWithOwnerById(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEvaluationConclusionUser> findEvaluationConclusionUsersByEventId(String eventId);

	/**
	 * @param userId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserEventPemissions(String userId, String eventId);

	List<EventTeamMember> findAssociateOwnerOfRfi(String id, TeamMemberType associateOwner);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEvent getEventDetailsForFeePayment(String eventId);

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param userId
	 * @param industryCategory TODO
	 * @return
	 */
	long getRfiEventCountByTenantId(String searchVal, String tenantId, String userId, String industryCategory);

	List<RfiEvent> findEventsByEventNameAndRefNumAndIndCatForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, String industryCategory, RfxTypes eventType, String tenantId, String loggedInUser);

	/**
	 * @param eventId
	 * @param memberType
	 * @return
	 */
	List<User> getAssociateOwnersOfEventsByEventId(String eventId, TeamMemberType memberType);

	/**
	 * @param loginId
	 * @return
	 */
	List<Event> getAllRfiEventIdsByLoginId(String loginId);

	/**
	 * @param fromUserId
	 * @param toUserId
	 */
	void updateTransferOwnerForEvent(String fromUserId, String toUserId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEvent findEventForSapByEventId(String eventId);
}
