package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.pojo.BqItemPojo;

public interface RfaSupplierBqItemDao extends GenericDao<RfaSupplierBqItem, String> {

	/**
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	List<RfaSupplierBqItem> findSupplierBqItemListByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfaBqItem> getBqItemsbyId(String bqId);

	/**
	 * @param itemId
	 * @param supplierId
	 * @return
	 */
	RfaSupplierBqItem getBqItemByBqItemId(String itemId, String supplierId);

	List<RfaBqItem> getBqItemsToStoreInSupplierById(String bqId);

	List<RfaSupplierBqItem> findSupplierBqItemsByBqItemIdAndEventId(String bqItemId, String eventId, List<Supplier> suppliers);

	List<String> findAddtionalTax(String bqId, String eventId, List<Supplier> suppliers);

	List<String> findGrandTotals(String bqId, String eventId, List<Supplier> suppliers, Boolean withTax);

	RftSupplierBqItem getBqItemByBqItemIdAndSuplier(String itemId, String supplierId);

	List<String> findSumOfTotalAmountForSupplier(String bqId, String eventId, List<Supplier> suppliers, Boolean withTax);

	List<String> findSumOfTotalAmountWithTaxForSupplier(String bqId, String eventId, List<Supplier> suppliers);

	List<RfaSupplierBqItem> findAllSupplierBqItemListByBqId(String bqId);

	Integer getCountOfAllRfaSupplierBqItem(String supplierId, String eventId);

	int countNumberOfBqAnsweredByBqIdAndSupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfaSupplierBq> getAllBqsBySupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfSupplierBqItem(String eventId);

	/**
	 * @param bqId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<String> findAddtionalTaxForView(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param bqId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<String> findGrandTotalsForView(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param eventBqId
	 * @param supplierId
	 * @param searchVal
	 * @param start
	 * @param pageLength
	 * @param itemOrder
	 * @param itemLevel
	 * @return
	 */
	List<RfaSupplierBqItem> getBqItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer start, Integer pageLength, Integer itemLevel, Integer itemOrder);

	List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String supplierId, String searchVal);

	/**
	 * @param eventBqId
	 * @param supplierId
	 * @param searchVal
	 * @return
	 */
	long totalBqItemCountByBqId(String eventBqId, String supplierId, String searchVal);

	RfaSupplierBqItem getBqItemByRfaBqItemId(String bqItemId, String supplierId);

	List<RfaSupplierBqItem> findBqItemListByBqIdAndSupplierId(String bqId, String supplierId);

	PricingTypes getPriceTypeByBqItemId(String id);

	RfaSupplierBqItem getSupplierBqItemByBqItemId(String itemId, String supplierId);

	List<String> findGrandTotalsForEvaluationView(String bqId, String eventId, List<Supplier> suppliers);

	int countIncompleteBqItemByBqIdsForSupplier(String id, String id2);

	List<RfaSupplierBqItem> findSupplierBqItemListByBqIdAndSupplierIds(String bqId, String supplierIds);

	List<RfaSupplierBqItem> findBqItemListAndBqListByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param itemId
	 * @param supplierId
	 * @return
	 */
	RfaSupplierBqItem getSupplierBqItemByEventBqItemIdAndSupplierId(String itemId, String supplierId);

}
