package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.RftSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface RftEventSupplierDao extends GenericEventSupplierDao<RftEventSupplier, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliersForEvaluation(String eventId);

	List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId);

	RftEvent findByEventId(String eventId);

	RftSupplierTeamMember getRftTeamMemberByUserIdAndEventId(String eventId, String userId);

	List<RftSupplierTeamMember> getRftSupplierTeamMembersForEvent(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliersForSummary(String eventId);

	/**
	 * @param userId
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId);

	public long getEventSuppliersCount(String eventId);

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

	List<FeePojo> getAllInvitedSuppliersByEventId(String eventId);

	List<RftEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId);

	List<RftEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId);

	List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input);

	long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input);

	List<SupplierSearchPojo> favoriteSuppliersOfBuyerByState(String buyerId, SupplierSearchPojo supplierSearchPojo, List<IndustryCategory> industryCategories, Boolean exclusive, Boolean inclusive, String eventType, String eventId, boolean isMinMaxPresent);

	List<SupplierSearchPojo> favoriteSuppliersOfBuyer(String buyerId, SupplierSearchPojo supplierSearchPojo, Boolean exclusive, Boolean inclusive, boolean isMinMaxPresent, String eventType, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param eventType
	 * @return
	 */
	boolean isSupplierExistsForPublicEvent(String supplierId, String eventId, RfxTypes eventType);

	List<RftEventSupplier> getAllSubmittedSupplierByEventId(String eventId);

	List<RftEventSupplier> findDisqualifySupplierByEventId(String eventId);

	String getEventNameByEventId(String eventId);

	/**
	 * @param paymentId
	 * @return
	 */
	RftEventSupplier getSupplierByStripePaymentId(String paymentId);

	List<RftEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventSupplier> findEventSuppliersForTatReportByEventId(String eventId);

}