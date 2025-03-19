package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.pojo.BqItemPojo;

public interface RftSupplierBqItemDao extends GenericDao<RftSupplierBqItem, String> {

	// RftSupplierBqItem getBqItemByBqIItemId(String itemId, String supplierId);

	List<RftSupplierBqItem> findSupplierBqItemByListByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RftBqItem> getBqItemsbyId(String bqId);

	RftSupplierBqItem getBqItemByBqItemId(String itemId, String supplierId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RftBqItem> getBqItemsToStoreInSupplierById(String bqId);

	/**
	 * @param bqItemId
	 * @param eventId
	 * @param suppliers TODO
	 * @return
	 */
	List<RftSupplierBqItem> findSupplierBqItemsByBqItemIdAndEventId(String bqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param bqId
	 * @param eventId TODO
	 * @param suppliers TODO
	 * @return
	 */
	List<String> findSumOfTotalAmountForSupplier(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param bqId
	 * @param eventId TODO
	 * @param suppliers TODO
	 * @return
	 */
	List<String> findAddtionalTax(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param bqId
	 * @param eventId TODO
	 * @param suppliers TODO
	 * @return
	 */
	List<String> findGrandTotals(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param bqId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<String> findSumOfTotalAmountWithTaxForSupplier(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param itemId
	 * @param supplierId
	 * @return
	 */
	RftSupplierBqItem getBqItemByBqItemIdAndSuplier(String itemId, String supplierId);

	/**
	 * @param bqId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<String> findSumOfTotalAmountWithOutTaxForSupplier(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfAllRftSupplierBqItem(String supplierId, String eventId);

	/**
	 * @param id
	 * @param supplierId
	 * @return
	 */
	int countNumberOfBqAnsweredByBqIdAndSupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RftSupplierBq> getAllBqsBySupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 */
	void deleteSupplierBqItemsForEvent(String eventId);

	/**
	 * @param bqId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<String> findGrandTotalsForView(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param bqId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<String> findAddtionalTaxForView(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param eventBqId
	 * @param supplierId
	 * @param searchVal
	 * @param pageLength
	 * @param start
	 * @param itemOrder
	 * @param itemLevel
	 * @return
	 */
	List<RftSupplierBqItem> getBqItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer start, Integer pageLength, Integer itemLevel, Integer itemOrder);

	/**
	 * @param bqId
	 * @param supplierId TODO
	 * @param searchVal TODO
	 * @return
	 */
	List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String supplierId, String searchVal);

	/**
	 * @param bqId
	 * @param supplierId TODO
	 * @param searchVal
	 * @return
	 */
	long totalBqItemCountByBqId(String bqId, String supplierId, String searchVal);

	/**
	 * @param itemId
	 * @param supplierId
	 * @return
	 */
	RftSupplierBqItem getBqItemByRftBqItemId(String itemId, String supplierId);

	List<RftSupplierBqItem> findBqItemListByBqIdAndSupplierId(String bqId, String supplierId);

	PricingTypes getPriceTypeByBqItemId(String id);

	/**
	 * @param itemId
	 * @param supplierId
	 * @return
	 */
	RftSupplierBqItem getSupplierBqItemByBqItemId(String itemId, String supplierId);

	List<String> findGrandTotalsForEvaluationView(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	int countIncompleteBqItemByBqIdsForSupplier(String bqId, String supplierId);

	List<RftSupplierBqItem> findSupplierBqItemListByBqIdAndSupplierIds(String bqId, String supplierIds);

	List<RftSupplierBqItem> findBqItemListAndBqListByBqIdAndSupplierId(String bqId, String supplierId);
}
