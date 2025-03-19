package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventApproval;
import com.privasia.procurehere.core.entity.RfqEventAwardAudit;
import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;
import com.privasia.procurehere.core.entity.RfqTeamMember;
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

public interface RfqEventDao extends GenericEventDao<RfqEvent, String> {

	/**
	 * @param eventId
	 * @return
	 */
	RfqEvent findByEventId(String eventId);

	/**
	 * @param rftEvent
	 * @return
	 */
	boolean isExists(RfqEvent rftEvent);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEvent findEventForCqByEventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfqEvent findBySupplierEventId(String id);

	/**
	 * @param id
	 * @return
	 */
	RfqEvent findEventSorByEventId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfEnvelopByEventId(String eventId);

	/**
	 * @param tenantId
	 * @param loggedInUser TODO
	 * @param indCat
	 * @param searchVal
	 * @return
	 */
	List<DraftEventPojo> getAllRfqEventByTenantId(String tenantId, String loggedInUser, String pageNo, String searchVal, String indCat);

	/**
	 * @param searchValue
	 * @param industryCategory
	 * @param tenantId
	 * @param loggedInUser TODO
	 * @return
	 */
	List<RfqEvent> findByEventNameaAndRefNumAndIndCatForTenant(String searchValue, String industryCategory, String tenantId, String loggedInUser);

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
	RfqTeamMember getTeamMemberByUserIdAndEventId(String eventId, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getTeamMembersForEvent(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfqEvent findEventForBqByEventId(String id);

	/**
	 * @param id
	 * @return
	 */
	List<RfqEventApproval> getAllApprovalsForEvent(String id);

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

	RfqEvent getPlainEventById(String eventId);

	List<RfqEvent> getAllRfqEventByLoginId(String loginId);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> getEventSuppliersId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqTeamMember> getBuyerTeamMemberByEventId(String eventId);

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
	RfqEvent getMobileEventDetails(String id);

	List<RfqSupplierBq> getSupplierBQOfLowestItemisedPrize(String eventId, String bqId);

	RfqSupplierBqItem getMinItemisePrice(String id, String eventId);

	RfqSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId);

	RfqSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId);

	boolean isExistsRfqEventId(String tenantId, String eventId);

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

	RfqEvent findRfqEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId);

	List<RfqEvent> getEventsByIds(String tenantId, String[] eventArr, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate);

	List<RfqEvent> getSearchEventsByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate);

	List<RfqEvent> getAllRfqEventByTenantId(String tenantId);

	Event getEventByEventRefranceNo(String eventRefranceNo, String tenantId);

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

	RfqEvent getRfqEventForShortSummary(String eventId);

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
	 * @param eventId
	 * @return
	 */
	boolean isMinMaxSupplierRatingAvaliableInEvent(String eventId);

	Supplier findWinnerSupplier(String id);

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

	List<RfqEvent> getAllRfqEventByTenantIdInitial(String loggedInUserTenantId, String loggedInUser);

	List<RfqEvent> getAllEventByTenantIdAndRfqTemplateId(String loggedInUserTenantId, String loggedInUser,String rfqTemplateId);
	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliersWithIdAndName(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountByEventId(String eventId);

	List<Supplier> getEventSuppliersWithIdAndName(String eventId, TableDataInput input, RfxTypes rfxTypes);

	long getEventSuppliersCountWithIdAndName(String eventId, RfxTypes rfxType);

	long getEventSuppliersFilterCount(String eventId, TableDataInput input, RfxTypes rfxType);

	List<String> getMeetingSupplierIds(String meetingId, RfxTypes rfxType);

	/**
	 * @return
	 */
	List<RfqEvent> getAllActiveEvents();

	/**
	 * @return
	 */
	List<RfqEvent> getAllApprovedEventsforJob();

	void updateRfaForAwardPrice(String id, BigDecimal sumAwardPrice);

	/**
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	RfqEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId);

	List<RfqEventAwardAudit> getRfqEventAwardByEventId(String eventId);

	Boolean isDefaultPreSetEnvlope(String eventId);

	/**
	 * @return
	 */
	List<RfqEvent> getAllRfqEventWhereUnMaskingUserIsNotNull();

	/**
	 * @param eventId
	 */
	void updateEventUnMaskUser(String eventId);

	/**
	 * @param eventId
	 * @param evalConUserId
	 * @return
	 */
	RfqEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEvent getPlainEventWithOwnerById(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEvaluationConclusionUser> findEvaluationConclusionUsersByEventId(String eventId);

	/**
	 * @param userId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserEventPemissions(String userId, String eventId);

	List<EventTeamMember> findAssociateOwnerOfRfq(String id, TeamMemberType associateOwner);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEvent getEventDetailsForFeePayment(String eventId);

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param userId
	 * @param industryCategory TODO
	 * @return
	 */
	long getRfqEventCountByTenantId(String searchVal, String tenantId, String userId, String industryCategory);

	List<RfqEvent> findEventsByEventNameAndRefNumAndIndCatForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, String industryCategory, RfxTypes eventType, String tenantId, String loggedInUser);

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
	List<Event> getAllRfqEventIdsByLoginId(String loginId);

	/**
	 * @param fromUserId
	 * @param toUserId
	 */
	void updateTransferOwnerForEvent(String fromUserId, String toUserId);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEvent findEventForSapByEventId(String eventId);

}
