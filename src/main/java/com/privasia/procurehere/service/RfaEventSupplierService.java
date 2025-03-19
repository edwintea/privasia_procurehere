package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.AuctionBids;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface RfaEventSupplierService {

	/**
	 * @param rfaEventSupplier
	 * @return
	 */
	RfaEventSupplier saveRfaEventSuppliers(RfaEventSupplier rfaEventSupplier);

	/**
	 * @param rfaEventSupplier
	 * @return
	 */
	boolean isExists(RfaEventSupplier rfaEventSupplier);

	/**
	 * @param eventID
	 * @return
	 */
	List<EventSupplier> getAllSuppliersByEventId(String eventID);

	/**
	 * @param rfaEventSupplier
	 */
	void deleteRfaEventSuppliers(RfaEventSupplier rfaEventSupplier);

	/**
	 * @param id
	 * @return
	 */
	RfaEventSupplier findSupplierById(String id);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	RfaEventSupplier findSupplierByIdAndEventId(String supplierId, String eventId);

	void updateRfaEventSuppliers(RfaEventSupplier rfaEventSupplier);

	RfaEventSupplier getEventSupplierBySupplierAndEventId(String supplierId, String eventID);

	void removeTeamMemberfromList(String eventId, String userId, String supplierId);

	List<RfaSupplierTeamMember> addTeamMemberToList(String eventId, String userId, String supplierId, TeamMemberType memberType);

	List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String loggedInUserTenantId);

	RfaEvent getRfaEventByeventId(String eventId);

	List<RfaSupplierTeamMember> getRfaSupplierTeamMembersForEvent(String eventId, String loggedInUserTenantId);

	/**
	 * @param id
	 * @return
	 */
	RfaEventSupplier findSupplierBySupplierId(String id);

	/**
	 * @param eventId
	 * @param id
	 */
	void addSupplierForPublicEvent(String eventId, String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<String> getAllRfaEventSuppliersIdByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventSupplier> getAllRfaEventSuppliersByEventId(String eventId);

	/**
	 * @param userId
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId);

	List<RfaEventSupplier> getAllRfaEventSuppliersListByEventId(String eventId);

	List<Supplier> getEventSuppliersForEvaluation(String eventId);

	Integer getNumberOfBidsBySupplier(String supplierId, String eventId);

	RfaEventSupplier findEventSupplierByEventIdAndSupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return TODO
	 */
	int updatePrivewTime(String eventId, String supplierId);

	List<Supplier> getRfaEventSupplierForAuctionConsole(String eventId);

	void updateAuctionOnlineDateTime(String eventId, String supplierId);

	List<EventSupplier> getAllSuppliersByEventIdOrderByCompName(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	boolean checkAnySupplierSubmited(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfSupplierByEventId(String eventId);

	/**
	 * @param eventId
	 */
	void deleteAllSuppliersByEventId(String eventId);

	/**
	 * @param event
	 * @param session
	 * @param virtualizer TODO
	 * @param loggedInUserTenantId
	 * @return
	 */

	JasperPrint generateSupplierSummary(RfaEvent event, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer);

	Integer updateSupplierAuctionRank(String eventId, boolean isForwordAuction, String supplierId);

	List<RfaEventSupplier> getSupplierListForBidderDisqualify(String eventId, String supplierId);

	void updateEventSupplierDisqualify(String eventId, String supplierId, User disqualifiedBy);

	RfaEventSupplier findEventSupplierByEventIdAndSupplierIgnoreSubmitStatus(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param session
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint getSupplierAuctionReport(String eventId, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId);

	/**
	 * @param bids
	 */
	void saveRfaAuctionBid(AuctionBids bids);

	/**
	 * @param supplierId
	 * @return
	 */
	Supplier findSupplierForId(String supplierId);

	List<EventSupplier> getSubmittedSuppliersByEventId(String eventID);

	/**
	 * @param supplierId
	 * @param eventID
	 * @return
	 */
	RfaEventSupplier getEventSupplierForAuctionBySupplierAndEventId(String supplierId, String eventID);

	RfaEventSupplier saveRfaEventSupplier(RfaEventSupplier rfaEventSupplier);

	List<RfaEventSupplier> findSupplierByEventIdOnlyRank(String eventId);

	List<RfaEventSupplier> findDisqualifySupplierByEventId(String eventId);

	List<EventSupplier> getAllPartiallyCompleteBidsByEventId(String id);

	List<FeePojo> getAllInvitedSuppliersByEventId(String eventId);

	List<RfaEventSupplier> getAllSuppliersByFeeEventId(String eventId, String id);

	List<RfaEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String id, String supplierCode, String tenantId);

	List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input);

	long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input);

	List<EventSupplierPojo> getAllDetailsForSendInvitation(String id);

	void updateEventSuppliersNotificationFlag(String id);

	EventSupplier saveInvitedSuppliers(RfaEventSupplier eventSupplier);

	List<RfaEventSupplier> getAllSubmittedSupplierByEventId(String eventId);

	/**
	 * @param eventSupplier
	 */
	void batchInsert(List<RfaEventSupplier> eventSupplier);

	RfaEventSupplier findEventSupplierByEventIdAndSupplierRevisedSubmission(String eventId, String supplierId);

	String getEventNameByEventId(String eventId);

	List<RfaEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventSupplier> findEventSuppliersForTatReportByEventId(String eventId);

}
