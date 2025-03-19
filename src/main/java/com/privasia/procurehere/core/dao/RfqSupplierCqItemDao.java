package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqCqItem;
import com.privasia.procurehere.core.entity.RfqSupplierCqItem;
import com.privasia.procurehere.core.entity.RfqSupplierCqOption;
import com.privasia.procurehere.core.entity.Supplier;

public interface RfqSupplierCqItemDao extends GenericDao<RfqSupplierCqItem, String> {

	/**
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	List<RfqSupplierCqItem> findSupplierCqItemListByCqId(String cqId, String supplierId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfqCqItem> getCqItemsbyId(String cqId);

	/**
	 * @param id
	 * @param name
	 * @return
	 */
	RfqSupplierCqItem findCqByEventIdAndCqName(String id, String name);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers TODO
	 * @return
	 */
	List<RfqSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventId(String cqItemId, String eventId, List<Supplier> suppliers);

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
	 * @param cqId TODO
	 * @return
	 */
	List<RfqSupplierCqItem> getSupplierCqItemsbySupplierId(String eventId, String supplierId, String cqId);

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
	List<RfqSupplierCqItem> findRequiredSupplierCqItemListByEventId(String supplierId, String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<RfqSupplierCqOption> findRequiredOptionValueByCqItemIdAndSupplierId(String id);

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
	List<RfqCq> getAllCqsBySupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 */
	void deleteSupplierCqItemsForEvent(String eventId);

	List<RfqSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfqCq> findRequiredSupplierCqCountByEventId(String supplierId, String eventId);

	List<RfqSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventId(List<String> cqid, String id, List<Supplier> suppList);

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
	List<RfqSupplierCqItem> findSupplierCqItemByCqIdandSupplierId(String cqId, String supplierId);

	/**
	 * @param supplierId
	 * @param cqId
	 * @return
	 */
	List<RfqSupplierCqItem> getSupplierCqItemsbySupplier(String supplierId, String cqId);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RfqSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(String cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RfqSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdNew(String cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	int findRfqRequiredCqCountBySupplierId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	int findRfqRequiredCqCountBySupplierIdCqId(String supplierId, String eventId, String cqId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	List<String> findRequiredCqsBySupplierId(String supplierId, String eventId, String cqId);
}
