package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.pojo.BqItemPojo;

/**
 * @author arc
 */
public interface RfpSupplierBqItemDao extends GenericDao<RfpSupplierBqItem, String> {

	/**
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	List<RfpSupplierBqItem> findSupplierBqItemByListByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfpBqItem> getBqItemsbyId(String bqId);

	/**
	 * @param itemId
	 * @param supplierId
	 * @return
	 */
	RfpSupplierBqItem getBqItemByBqItemId(String itemId, String supplierId);

	/**
	 * @param bqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RfpSupplierBqItem> findSupplierBqItemsByBqItemIdAndEventId(String bqItemId, String eventId, List<Supplier> suppliers);

	/**
	 * @param bqId
	 * @param eventId
	 * @param suppliers
	 * @param withOrWithoutTax TODO
	 * @return
	 */
	List<String> findSumOfTotalAmountWithTaxForSupplier(String bqId, String eventId, List<Supplier> suppliers, String withOrWithoutTax);

	/**
	 * @param bqId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<String> findSumOfTotalAmountForSupplier(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param bqId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<String> findAddtionalTax(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param bqId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<String> findGrandTotals(String bqId, String eventId, List<Supplier> suppliers);

	/**
	 * @param itemId
	 * @param supplierId
	 * @return
	 */
	RfpSupplierBqItem getBqItemByBqItemIdAndSuplier(String itemId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	Integer getCountOfAllRftSupplierBqItem(String supplierId, String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	int countNumberOfBqAnsweredByBqIdAndSupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfpSupplierBq> getAllBqsBySupplierId(String eventId, String supplierId);

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
	List<RfpSupplierBqItem> getBqItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer start, Integer pageLength, Integer itemLevel, Integer itemOrder);

	List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String supplierId, String searchVal);

	/**
	 * @param eventBqId
	 * @param supplierId
	 * @param searchVal
	 * @return
	 */
	long totalBqItemCountByBqId(String eventBqId, String supplierId, String searchVal);

	RfpSupplierBqItem getBqItemByRfpBqItemId(String bqItemId, String supplierId);

	List<RfpSupplierBqItem> findBqItemListByBqIdAndSupplierId(String bqId, String supplierId);

	PricingTypes getPriceTypeByBqItemId(String id);

	RfpSupplierBqItem getSupplierBqItemByBqItemId(String itemId, String supplierId);

	List<String> findGrandTotalsForEvaluationView(String bqId, String eventId, List<Supplier> suppliers);

	int countIncompleteBqItemByBqIdsForSupplier(String id, String id2);

	List<RfpSupplierBqItem> findSupplierBqItemListByBqIdAndSupplierIds(String bqId, String supplierIds);

	List<RfpSupplierBqItem> findBqItemListAndBqListByBqIdAndSupplierId(String bqId, String supplierId);
}
