package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventSupplier;
import com.privasia.procurehere.core.entity.RfqSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author arc
 */
public interface RfqEventSupplierDao extends GenericEventSupplierDao<RfqEventSupplier, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliersForEvaluation(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEvent findByEventId(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfqSupplierTeamMember> getRfqSupplierTeamMembersForEvent(String eventId, String supplierId);

	/**
	 * @param userId
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId);

	long getEventSuppliersCount(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfSupplierByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId);

	List<Supplier> getEventSuppliersForEvaluation(String eventId, List<Supplier> selectedSuppliers);

	List<RfqEventSupplier> getAllRfqEventSuppliersByEventId(String eventId);

	List<Supplier> getEventSuppliersForSummary(String id);

	List<String> getAllRfaEventSuppliersIdByEventId(String eventId);

	List<FeePojo> getAllInvitedSuppliersByEventId(String eventId);

	List<RfqEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId);

	List<RfqEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId);

	List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input);

	long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input);

	List<RfqEventSupplier> getAllSubmittedSupplierByEventId(String eventId);

	List<FeePojo> getAllInvitedSuppliersByEventId(String eventId, TableDataInput input, RfxTypes eventType);

	long getAllInvitedSuppliersFilterCountByEventId(String eventId, TableDataInput input, RfxTypes eventType);

	long getAllInvitedSuppliersCountByEventId(String eventId, RfxTypes eventType);

	List<EventSupplier> getAllSubmitedSupplierByEevntId(String eventId);

	List<RfqEventSupplier> findDisqualifySupplierByEventId(String eventId);

	String getEventNameByEventId(String eventId);

	/**
	 * @param paymentId
	 * @return
	 */
	RfqEventSupplier getSupplierByStripePaymentId(String paymentId);

	List<RfqEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventSupplier> findEventSuppliersForTatReportByEventId(String eventId);

}
