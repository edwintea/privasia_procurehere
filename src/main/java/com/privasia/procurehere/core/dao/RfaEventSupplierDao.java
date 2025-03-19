package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RfaSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface RfaEventSupplierDao extends GenericEventSupplierDao<RfaEventSupplier, String> {

	RfaEvent findByEventId(String eventId);

	List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId);

	List<RfaSupplierTeamMember> getRfaSupplierTeamMembersForEvent(String eventId, String supplierId);

	List<Supplier> getEventSuppliersForEvaluation(String eventId);

	List<String> getAllRfaEventSuppliersIdByEventId(String eventId);

	RfaEventSupplier findEventSupplierByEventIdAndSupplierId(String eventId, String supplierId);

	List<RfaEventSupplier> getAllRfaEventSuppliersByEventId(String eventId);

	/**
	 * @param userId
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliersForSummary(String eventId);

	List<RfaEventSupplier> getAllRfaEventSuppliersListByEventId(String eventId);

	Integer getNumberOfBidsBySupplier(String supplierId, String eventId);

	Integer updateSupplierAuctionRank(String eventId, boolean isForwordAuction, String supplierId);

	List<Supplier> getRfaEventSupplierForAuctionConsole(String eventId);

	void updateAuctionOnlineDateTime(String eventId, String supplierId);

	void updateEventSupplierForAuction(String eventId, String supplierId, String ipAddress);

	long getEventSuppliersCount(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfSupplierByEventId(String eventId);

	List<RfaEventSupplier> getSupplierListForBidderDisqualify(String eventId, String supplierId);

	boolean updateEventSupplierDisqualify(String eventId, String supplierId, User disqualifiedBy, String disqualifyremarks);

	RfaEventSupplier findEventSupplierByEventIdAndSupplierIgnoreSubmit(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 */
	void updateEventSupplierConfirm(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId);

	List<RfaEventSupplier> findSupplierByEventIdOnlyRank(String eventId);

	RfaSupplierBqItem getSupplierBqItemByBqItemId(String itemId, String supplierId);

	List<Supplier> getEventSuppliersForEvaluation(String eventId, List<Supplier> selectedSuppliers);

	List<FeePojo> getAllInvitedSuppliersByEventId(String eventId);

	List<RfaEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId);

	List<RfaEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId);

	List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input);

	long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input);

	List<RfaEventSupplier> getAllSubmittedSupplierByEventId(String eventId);

	List<RfaEventSupplier> findDisqualifySupplierByEventId(String eventID);

	RfaEventSupplier findEventSupplierByEventIdAndSupplierRevisedSubmission(String eventId, String supplierId);

	String getEventNameByEventId(String eventId);

	/**
	 * @param paymentId
	 * @return
	 */
	RfaEventSupplier getSupplierByStripePaymentId(String paymentId);

	List<RfaEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventSupplier> findEventSuppliersForTatReportByEventId(String eventId);

}
