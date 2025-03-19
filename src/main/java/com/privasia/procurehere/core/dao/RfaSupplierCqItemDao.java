package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaCqItem;
import com.privasia.procurehere.core.entity.RfaSupplierCqItem;
import com.privasia.procurehere.core.entity.RfaSupplierCqOption;
import com.privasia.procurehere.core.entity.Supplier;

public interface RfaSupplierCqItemDao extends GenericDao<RfaSupplierCqItem, String> {

	/**
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	List<RfaSupplierCqItem> findSupplierCqItemListByCqId(String cqId, String supplierId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfaCqItem> getCqItemsbyId(String cqId);

	/**
	 * @param id
	 * @param name
	 * @return
	 */
	RfaSupplierCqItem findCqByEventIdAndCqName(String id, String name);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @return
	 */
	List<RfaSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventId(String cqItemId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */

	List<RfaSupplierCqItem> findRequiredSupplierCqItemListByEventId(String supplierId, String eventId);

	/**
	 * @param id
	 * @param supplierId2
	 * @return
	 */

	List<RfaSupplierCqOption> findRequiredOptionValueByCqItemIdAndSupplierId(String id);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param cqId TODO
	 * @return
	 */
	List<RfaSupplierCqItem> getSupplierCqItemsbySupplierId(String eventId, String supplierId, String cqId);

	/**
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	boolean resetAttachment(String eventId, String cqId);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RfaSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventId(String cqItemId, String eventId, List<Supplier> suppliers);

	List<String> findSumOfScoringForSupplier(String cqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfaCq> getAllCqsBySupplierId(String eventId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	int CountAllMandatorySupplierCqItemByEventId(String supplierId, String eventId);

	List<RfaSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfaCq> findRequiredSupplierCqCountByEventId(String supplierId, String eventId);

	List<RfaSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventId(List<String> cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param attachId
	 * @param supplierId
	 * @return
	 */
	String findFileNameById(String attachId, String supplierId);

	/**
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	List<RfaSupplierCqItem> findSupplierCqItemByCqIdandSupplierId(String cqId, String supplierId);

	/**
	 * @param supplierId
	 * @param cqId
	 * @return
	 */
	List<RfaSupplierCqItem> getSupplierCqItemsbySupplier(String supplierId, String cqId);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RfaSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(String cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RfaSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdNew(String cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	int findRfaRequiredCqCountBySupplierId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	int findRfaRequiredCqCountBySupplierIdAndCqId(String supplierId, String eventId, String cqId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	List<String> findRequiredCqsBySupplierId(String supplierId, String eventId, String cqId);
}
