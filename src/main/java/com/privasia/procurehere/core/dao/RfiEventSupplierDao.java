package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventSupplier;
import com.privasia.procurehere.core.entity.RfiSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface RfiEventSupplierDao extends GenericEventSupplierDao<RfiEventSupplier, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliersForEvaluation(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEvent findByEventId(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfiSupplierTeamMember> getRfiSupplierTeamMembersForEvent(String eventId, String supplierId);

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

	List<RfaSupplierBqPojo> findRfiSupplierParticipation(String eventId);

	List<FeePojo> getAllInvitedSuppliersByEventId(String eventId);

	List<RfiEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId);

	List<RfiEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId);

	List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input);

	long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input);

	EventDocument findeventDocumentById(String docId);

	List<RfiEventSupplier> getAllSubmittedSupplierByEventId(String eventId);

	List<RfiEventSupplier> findDisqualifySupplierByEventId(String eventId);

	String getEventNameByEventId(String eventId);

	/**
	 * @param paymentId
	 * @return
	 */
	RfiEventSupplier getSupplierByStripePaymentId(String paymentId);

	List<RfiEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventSupplier> findEventSuppliersForTatReportByEventId(String eventId);

}
