package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventApproval;
import com.privasia.procurehere.core.entity.RfaEventAwardAudit;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.BidHistoryPojo;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.FinishedEventPojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.OngoingEventPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface RfaEventDao extends GenericDao<RfaEvent, String> {

	RfaEvent findByEventId(String eventId);

	@Deprecated
	List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam);

	boolean isExists(RfaEvent rfaEvent);

	List<DraftEventPojo> getAllDraftEventsForBuyer(String tenantId);

	List<OngoingEventPojo> getAllOngoingEventsForBuyer(String tenantId, Date lastLoginTime);

	List<FinishedEventPojo> getAllFinishedEventsForBuyer(String tenantId, Date lastLoginTime, int days);

	/*
	 * @Deprecated FavouriteSupplier getFavouriteSupplierBySupplierId(String id);
	 */
	List<RfxView> findAllEventForSupplier(String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEvent findEventForCqByEventId(String eventId);

	RfaEvent findBySupplierEventId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfEnvelopByEventId(String eventId);

	/**
	 * @param tenantId
	 * @param loggedInUser
	 * @param indCat
	 * @param searchVal
	 * @return
	 */
	List<DraftEventPojo> getAllRfaEventByTenantId(String tenantId, String loggedInUser, String pageNo, String searchVal, String indCat);

	/**
	 * @param searchValue
	 * @param industryCategory
	 * @param tenantId
	 * @param loggedInUser
	 * @return
	 */
	List<RfaEvent> findByEventNameaAndRefNumAndIndCatForTenant(String searchValue, String industryCategory, String tenantId, String loggedInUser);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliers(String eventId);

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
	RfaTeamMember getRfaTeamMemberByUserIdAndEventId(String eventId, String userId);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getTeamMembersForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEvent findEventForBqByEventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<RfaEventApproval> getAllApprovalsForEvent(String id);

	/**
	 * @param userId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserPemissionsForEvent(String userId, String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	BidHistoryPojo getMinMaxBidPriceForEvent(String eventId);

	/**
	 * @param userId
	 * @param eventId
	 * @param envelopeId
	 * @return
	 */
	EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId);

	RfaEvent getLeanEventbyEventId(String eventId);

	List<Supplier> getAwardedSuppliers(String eventId);

	RfaEvent getPlainEventById(String eventId);

	List<RfaEvent> getAllRfaEventByLoginId(String loginId);

	RfaEvent getRfaEventForBidHistory(String eventId);

	EventStatus checkRelativeEventStatus(String relativeEventId);

	List<RfaEvent> getAllAssosiateAuction(String eventId);

	/**
	 * @param eventId
	 * @param winningSupplier
	 * @param winningPrice
	 * @param status
	 */
	void updateDuctionAuctionWinningSupplier(String eventId, Supplier winningSupplier, BigDecimal winningPrice, EventStatus status);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> getEventSuppliersId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaTeamMember> getBuyerTeamMemberByEventId(String eventId);

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

	List<RfaEvent> getAllAssosiateAuctionForReschdule(String eventId);

	Integer findAssignedTemplateCount(String templateId);

	RfaEvent getRfaEventForTimeExtensionAndBidSubmission(String eventId);

	void updateTimeExtension(String eventId, Integer totalExtensions, Date eventEnd);

	/**
	 * @param id
	 * @return
	 */
	RfaEvent getEventNameAndReferenceNumberById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventTeamMember> getPlainTeamMembersForEvent(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	String findBusinessUnitName(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfaEvent getMobileEventDetails(String id);

	List<RfaSupplierBq> getSupplierBQOfLowestItemisedPrize(String eventId, String bqId);

	RfaSupplierBqItem getMinItemisePrice(String id, String eventId);

	RfaSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId);

	RfaSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId);

	boolean isExistsRfaEventId(String tenantId, String id);

	List<String> getEventIdList(String tenantId);

	void deleteAuctionBids(String eventId);

	void deleteAuctionRules(String eventId);

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

	void deleteEventTemplateFieldByTanent(String tenantId);

	List<String> getEventTemplateIdList(String tenantId);

	List<String> getEventTemplateApprovalIdList(String prTemplateId);

	void deleteEventTemplateApprovalUserByAproval(String apid);

	void deleteEventTemplateApprovalById(String apid);

	void deleteEventTemplateByTanent(String tenantId);

	void deleteEventTemplateByID(String prTemplateId);

	void deleteRfxrecord(String id);

	void deleteTeamMemberByTemplateId(String rfxTemplateId);

	RfaEvent findRfaEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId);

	List<RfaEvent> getEventsByIds(String tenantId, String[] eventArr, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate);

	List<BigDecimal> getEventsGrandTotalByIds(String tenantId, String[] eventArr);

	List<IndustryCategoryPojo> getTopFiveCategory(String tanentId);

	List<RfaEvent> getSearchEventByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date endDate, Date startDate);

	EventTimerPojo getTimeRfaEventByeventId(String eventId);

	List<RfaEvent> getAllRfaEventByTenantId(String tenantId);

	Event getEventByEventRefranceNo(String eventRefranceNo, String tenantId);

	User getUnMaskedUserNameAndMailByEventId(String eventId);

	EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId);

	EventPojo loadSupplierEventPojoForSummeryById(String eventId);

	List<IndustryCategory> getIndustryCategoryListForRfaById(String id);

	List<EventSupplierPojo> getEventSuppliersAndTimeZone(String id);

	void updateEventStatus(EventStatus status, String id);

	long getParticipatedSupplierCount(String eventId);

	long getInvitedSupplierCount(String eventId);

	long getSubmitedSupplierCount(String eventId);

	List<RfaSupplierBq> getLowestSubmissionPrice(String eventId, Boolean prebidFlag);

	RfaEvent getRfaEventForShortSummary(String eventId);

	List<RfaSupplierBq> getHighestSubmissionPrice(String eventId, Boolean prebidFlag);

	List<RfaEventSupplier> getSuppliersByStatus(String eventId);

	RfaSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId);

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
	 * @param auctionComplitationTime
	 * @return
	 */
	int updateImmediately(String eventId, EventStatus status, Date auctionComplitationTime);

	List<RfaEvent> getAllRfaEventByTenantIdInitial(String loggedInUserTenantId, String string);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountByEventId(String eventId);

	/**
	 * @return
	 */
	List<RfaEvent> getAllActiveEvents();

	/**
	 * @return
	 */
	List<RfaEvent> getAllApprovedEventsforJob();

	void updateRfaForAwardPrice(String id, BigDecimal sumOfAwardedPriceForEvent);

	/**
	 * @param eventId
	 * @return
	 */
	Event getSimpleEventDetailsById(String eventId);

	User getRevertLastBidUserNameAndMailByEventId(String id);

	/**
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	RfaEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId);

	List<RfaEventAwardAudit> getRfaEventAwardByEventId(String eventId);

	Boolean isDefaultPreSetEnvlope(String eventId);

	RfaSupplierBq getSupplierBQOfHighestTotalPrice(String eventId, String bqId);

	List<RfaSupplierBq> getSupplierBQOfHighestItemisedPrize(String eventId, String bqId);

	RfaSupplierBqItem getMaxItemisePrice(String id, String eventId);

	List<DraftEventPojo> getAllAuctionEventsForAuctionSummaryReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, List<AuctionType> auctionTypeList, String auctionTypeS);

	long getAuctionEventsCountForBuyerEventReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, List<AuctionType> auctionTypeList, String auctionTypeS);

	long findTotalEventForBuyer(String tenantId, Object userId, List<AuctionType> auctionTypeList, String auctionTypeS);

	List<DraftEventPojo> getAllAuctionEventwithSearchFilter(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, String auctionTypeS);

	/**
	 * @return
	 */
	List<RfaEvent> getAllRfaEventWhereUnMaskingUserIsNotNull();

	/**
	 * @param eventId
	 */
	void updateEventUnMaskUser(String eventId);

	void deleteAllUnmaskedUsers(String eventId, String eventType);

	void deleteBqItemComments(String eventId, String eventType);

	void deleteCreatedByRecordUserID(String userId);

	/**
	 * @param eventId
	 * @param evalConUserId
	 * @return
	 */
	RfaEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEvent getPlainEventWithOwnerById(String eventId);

	/***
	 * @param eventId
	 * @return
	 */
	List<RfaEvaluationConclusionUser> findEvaluationConclusionUsersByEventId(String eventId);

	/**
	 * @param userId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserEventPemissions(String userId, String eventId);

	/**
	 * @param id
	 * @param associateOwner
	 * @return
	 */
	List<EventTeamMember> findAssociateOwnerOfRfa(String id, TeamMemberType associateOwner);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEvent getEventDetailsForFeePayment(String eventId);

	/**
	 * @param tenantId
	 * @param auctionTypeList
	 * @return
	 */
	long findTotalEventsCountForCsv(String tenantId, List<AuctionType> auctionTypeList);

	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @param eventIds
	 * @param searchFilterEventPojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param auctionTypeS
	 * @return
	 */
	List<DraftEventPojo> findAllActiveEventsForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, String auctionTypeS);

	/**
	 * @param searchValue
	 * @param tenantId
	 * @param userId
	 * @param industryCategory TODO
	 * @return
	 */
	long getRfaEventCountByTenantId(String searchValue, String tenantId, String userId, String industryCategory);

	List<RfaEvent> findEventsByEventNameAndRefNumAndIndCatForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, String industryCategory, RfxTypes eventType, String tenantId, String loggedInUser);

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
	List<Event> getAllRfaEventIdsByLoginId(String loginId);

	/**
	 * @param fromUserId
	 * @param toUserId
	 */
	void updateTransferOwnerForEvent(String fromUserId, String toUserId);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEvent findEventForSapByEventId(String eventId);

}
