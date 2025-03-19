package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventApproval;
import com.privasia.procurehere.core.entity.RfpEventAwardAudit;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface RfpEventDao extends GenericEventDao<RfpEvent, String> {

	/**
	 * @param eventId
	 * @return
	 */
	RfpEvent findByEventId(String eventId);

	/**
	 * @param rftEvent
	 * @return
	 */
	boolean isExists(RfpEvent rftEvent);

	/**
	 * @param eventId
	 * @return
	 */
	RfpEvent findEventForCqByEventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfpEvent findBySupplierEventId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfEnvelopByEventId(String eventId);

	/**
	 * @param tenantId
	 * @param loggedInUser TODO
	 * @param pageNo
	 * @param indCat
	 * @param serchVal
	 * @return
	 */
	List<DraftEventPojo> getAllRfpEventByTenantId(String tenantId, String loggedInUser, String pageNo, String serchVal, String indCat);

	/**
	 * @param searchValue
	 * @param industryCategory
	 * @param tenantId
	 * @param loggedInUser TODO
	 * @return
	 */
	List<RfpEvent> findByEventNameaAndRefNumAndIndCatForTenant(String searchValue, String industryCategory, String tenantId, String loggedInUser);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliers(String eventId);

	/**
	 * @param eventId
	 * @param userId
	 * @return
	 */
	RfpTeamMember getRfpTeamMemberByUserIdAndEventId(String eventId, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getTeamMembersForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfpEvent findEventForBqByEventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<RfpEventApproval> getAllApprovalsForEvent(String id);

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

	List<Supplier> getAwardedSuppliers(String eventId);

	RfpEvent getPlainEventById(String eventId);

	List<RfpEvent> getAllRfpEventByLoginId(String loginId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> getEventSuppliersId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpTeamMember> getBuyerTeamMemberByEventId(String eventId);

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
	List<EventTeamMember> getPlainTeamMembersForEvent(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfpEvent getMobileEventDetails(String id);

	List<RfpSupplierBq> getSupplierBQOfLowestItemisedPrize(String eventId, String bqId);

	RfpSupplierBqItem getMinItemisePrice(String id, String eventId);

	RfpSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId);

	RfpSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId);

	boolean isExistsRfpEventId(String tenantId, String eventId);

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

	RfpEvent findRfpEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId);

	List<RfpEvent> getEventsByIds(String tenantId, String[] eventArr, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate);

	List<RfpEvent> getSearchEventsByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate);

	List<RfpEvent> getAllRfpEventByTenantId(String tenantId);

	Event getEventByEventRefranceNo(String eventRefranceNo, String tenantID);

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

	RfpEvent getRfpEventForShortSummary(String eventId);

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
	 * @param eventId
	 * @return
	 */
	boolean isMinMaxSupplierRatingAvaliableInEvent(String eventId);

	Supplier findWinnerSupplier(String id);

	int updateEventPushedDate(String eventId);

	Double getAvarageBidPriceSubmitted(String id);

	int updatePrPushDate(String eventId);

	int updateEventAward(String eventId);

	/**
	 * @param eventId
	 * @param status
	 * @return
	 */
	int updateImmediately(String eventId, EventStatus status);

	List<RfpEvent> getAllEventByTenantIdInitial(String loggedInUserTenantId, String loggedInUser);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountByEventId(String eventId);

	/**
	 * @return
	 */
	List<RfpEvent> getAllActiveEvents();

	/**
	 * @return
	 */
	List<RfpEvent> getAllApprovedEventsforJob();

	void updateRfaForAwardPrice(String id, BigDecimal sumAwardPrice);

	/**
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	RfpEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId);

	List<RfpEventAwardAudit> getRfpEventAwardByEventId(String eventId);

	Boolean isDefaultPreSetEnvlope(String eventId);

	/**
	 * @return
	 */
	List<RfpEvent> getAllRfpEventWhereUnMaskingUserIsNotNull();

	/**
	 * @param eventId
	 */
	void updateEventUnMaskUser(String eventId);

	/**
	 * @param eventId
	 * @param evalConUserId
	 * @return
	 */
	RfpEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId);

	/**
	 * @param eventId
	 * @return
	 */
	RfpEvent getPlainEventWithOwnerById(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEvaluationConclusionUser> findEvaluationConclusionUsersByEventId(String eventId);

	/**
	 * @param userId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserEventPemissions(String userId, String eventId);

	List<EventTeamMember> findAssociateOwnerOfRfp(String id, TeamMemberType associateOwner);

	/**
	 * @param eventId
	 * @return
	 */
	RfpEvent getEventDetailsForFeePayment(String eventId);

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param userId
	 * @param industryCategory TODO
	 * @return
	 */
	long getRfpEventCountByTenantId(String searchVal, String tenantId, String userId, String industryCategory);

	List<RfpEvent> findEventsByEventNameAndRefNumAndIndCatForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, String industryCategory, RfxTypes eventType, String tenantId, String loggedInUser);

	/**
	 * @param eventId
	 * @param memberType
	 * @return
	 */
	List<User> getAssociateOwnersOfEventsByEventId(String eventId, TeamMemberType memberType);

	/**
	 * @param eventId
	 */
	void revertEventAwardStatus(String eventId);

	/**
	 * @param loginId
	 * @return
	 */
	List<Event> getAllRfpEventIdsByLoginId(String loginId);

	/**
	 * @param fromUserId
	 * @param toUserId
	 */
	void updateTransferOwnerForEvent(String fromUserId, String toUserId);


	/**
	 * @param eventId
	 * @return
	 */
	RfpEvent findEventForSapByEventId(String eventId);

	

	RfpEvent findEventSorByEventId(String id);

}
