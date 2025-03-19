package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftCqItem;
import com.privasia.procurehere.core.entity.RftSupplierCqItem;
import com.privasia.procurehere.core.entity.RftSupplierCqOption;
import com.privasia.procurehere.core.entity.Supplier;

public interface RftSupplierCqItemDao extends GenericDao<RftSupplierCqItem, String> {

	List<RftSupplierCqItem> findSupplierCqItemListByCqId(String cqId, String supplierId);

	List<RftCqItem> getCqItemsbyId(String cqId);

	RftSupplierCqItem findCqByEventIdAndCqName(String id, String name);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers TODO
	 * @return
	 */
	List<RftSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventId(String cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param cqId
	 * @param eventId TODO
	 * @param suppliers TODO
	 * @return
	 */
	List<String> findSumOfScoringForSupplier(String cqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param cqId TODO
	 * @return
	 */
	List<RftSupplierCqItem> getSupplierCqItemsbySupplierId(String eventId, String supplierId, String cqId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RftSupplierCqItem> findRequiredSupplierCqItemListByEventId(String supplierId, String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<RftSupplierCqOption> findRequiredOptionValueByCqItemIdAndSupplierId(String id);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	int CountAllMandatorySupplierCqItemByEventId(String supplierId, String eventId);

	/**
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	boolean resetAttachment(String eventId, String cqId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RftCq> getAllCqsBySupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 */
	void deleteSupplierCqItemsForEvent(String eventId);

	List<RftSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RftCq> findRequiredSupplierCqCountByEventId(String supplierId, String eventId);

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
	List<RftSupplierCqItem> findSupplierCqItemByCqIdandSupplierId(String cqId, String supplierId);

	/**
	 * @param supplierId
	 * @param cqId
	 * @return
	 */
	List<RftSupplierCqItem> getSupplierCqItemsbySupplier(String supplierId, String cqId);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RftSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(String cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RftSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdNew(String cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	int findRftRequiredCqCountBySupplierId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	int findRftRequiredCqCountBySupplierIdAndCqId(String supplierId, String eventId, String cqId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId TODO
	 * @return
	 */
	List<String> findRequiredCqsBySupplierId(String supplierId, String eventId, String cqId);

}
