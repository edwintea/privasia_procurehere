package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventSupplier;
import com.privasia.procurehere.core.entity.RfpSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author arc
 */
public interface RfpEventSupplierDao extends GenericEventSupplierDao<RfpEventSupplier, String> {

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
	RfpEvent findByEventId(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfpSupplierTeamMember> getRfpSupplierTeamMembersForEvent(String eventId, String supplierId);

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

	List<Supplier> getEventSuppliersForSummary(String id);

	List<FeePojo> getAllInvitedSuppliersByEventId(String eventId);

	List<RfpEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId);

	List<RfpEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId);

	List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input);

	long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input);

	List<RfpEventSupplier> getAllSubmittedSupplierByEventId(String eventId);

	List<RfpEventSupplier> findDisqualifySupplierByEventId(String eventId);

	String getEventNameByEventId(String eventId);

	/**
	 * @param paymentId
	 * @return
	 */
	RfpEventSupplier getSupplierByStripePaymentId(String paymentId);

	List<RfpEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventSupplier> findEventSuppliersForTatReportByEventId(String eventId);
}
