package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpCqItem;
import com.privasia.procurehere.core.entity.RfpSupplierCqItem;
import com.privasia.procurehere.core.entity.RfpSupplierCqOption;
import com.privasia.procurehere.core.entity.Supplier;

public interface RfpSupplierCqItemDao extends GenericDao<RfpSupplierCqItem, String> {

	/**
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	List<RfpSupplierCqItem> findSupplierCqItemListByCqId(String cqId, String supplierId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfpCqItem> getCqItemsbyId(String cqId);

	/**
	 * @param id
	 * @param name
	 * @return
	 */
	RfpSupplierCqItem findCqByEventIdAndCqName(String id, String name);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers TODO
	 * @return
	 */
	List<RfpSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventId(String cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param cqId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<String> findSumOfScoringForSupplier(String cqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param cqId
	 * @return
	 */
	List<RfpSupplierCqItem> getSupplierCqItemsbySupplierId(String eventId, String supplierId, String cqId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfpSupplierCqItem> findRequiredSupplierCqItemListByEventId(String supplierId, String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<RfpSupplierCqOption> findRequiredOptionValueByCqItemIdAndSupplierId(String id);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	int CountAllMandatorySupplierCqItemByEventId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfpSupplierCqItem> findOptionalSupplierCqItemByEventId(String supplierId, String eventId);

	/**
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	boolean resetAttachment(String eventId, String cqId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfpCq> getAllCqsBySupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 */
	void deleteSupplierCqItemsForEvent(String eventId);

	List<RfpSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfpCq> findRequiredSupplierCqCountByEventId(String supplierId, String eventId);

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
	List<RfpSupplierCqItem> findSupplierCqItemByCqIdandSupplierId(String cqId, String supplierId);

	/**
	 * @param supplierId
	 * @param cqId
	 * @return
	 */
	List<RfpSupplierCqItem> getSupplierCqItemsbySupplier(String supplierId, String cqId);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RfpSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(String cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RfpSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdNew(String cqItemId, String eventId, List<Supplier> suppliers);

	int findRfpRequiredCqCountBySupplierId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	int findRfpRequiredCqCountBySupplierIdAndCqId(String supplierId, String eventId, String cqId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	List<String> findRequiredCqsBySupplierId(String supplierId, String eventId, String cqId);
}
