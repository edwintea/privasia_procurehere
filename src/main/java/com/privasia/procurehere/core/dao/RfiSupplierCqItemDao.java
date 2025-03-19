package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiCqItem;
import com.privasia.procurehere.core.entity.RfiSupplierCqItem;
import com.privasia.procurehere.core.entity.RfiSupplierCqOption;
import com.privasia.procurehere.core.entity.Supplier;

public interface RfiSupplierCqItemDao extends GenericDao<RfiSupplierCqItem, String> {

	/**
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	List<RfiSupplierCqItem> findSupplierCqItemListByCqId(String cqId, String supplierId);

	/**
	 * @param cqId
	 * @return
	 */
	List<RfiCqItem> getCqItemsbyId(String cqId);

	/**
	 * @param id
	 * @param name
	 * @return
	 */
	RfiSupplierCqItem findCqByEventIdAndCqName(String id, String name);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers TODO
	 * @return
	 */
	List<RfiSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventId(String cqItemId, String eventId, List<Supplier> suppliers);

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
	List<RfiSupplierCqItem> getSupplierCqItemsbySupplierId(String eventId, String supplierId, String cqId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	int CountAllMandatorySupplierCqItemByEventId(String supplierId, String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<RfiSupplierCqOption> findRequiredOptionValueByCqItemId(String id);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfiSupplierCqItem> findRequiredSupplierCqItemListByEventId(String supplierId, String eventId);

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
	List<RfiCq> getAllCqsBySupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 */
	void deleteSupplierCqItemsForEvent(String eventId);

	List<RfiSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfiCq> findRequiredSupplierCqCountByEventId(String supplierId, String eventId);

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
	List<RfiSupplierCqItem> findSupplierCqItemByCqIdandSupplierId(String cqId, String supplierId);

	/**
	 * @param supplierId
	 * @param cqId
	 * @return
	 */
	List<RfiSupplierCqItem> getSupplierCqItemsbySupplier(String supplierId, String cqId);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RfiSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(String cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param cqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RfiSupplierCqItem> findSupplierCqItemsByCqItemIdAndEventIdNew(String cqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	int findRfiRequiredCqCountBySupplierId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	int findRfiRequiredCqCountBySupplierIdAndCqId(String supplierId, String eventId, String cqId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	List<String> findRequiredCqsBySupplierId(String supplierId, String eventId, String cqId);

}
