package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.pojo.BqItemPojo;

/**
 * @author arc
 */
public interface RfqSupplierBqItemDao extends GenericDao<RfqSupplierBqItem, String> {

	/**
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	List<RfqSupplierBqItem> findSupplierBqItemByListByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfqBqItem> getBqItemsbyId(String bqId);

	/**
	 * @param itemId
	 * @param supplierId
	 * @return
	 */
	RfqSupplierBqItem getBqItemByBqItemId(String itemId, String supplierId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfqBqItem> getBqItemsToStoreInSupplierById(String bqId);

	/**
	 * @param bqItemId
	 * @param eventId
	 * @param suppliers
	 * @return
	 */
	List<RfqSupplierBqItem> findSupplierBqItemsByBqItemIdAndEventId(String bqItemId, String eventId, List<Supplier> suppliers);

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
	RfqSupplierBqItem getBqItemByBqItemIdAndSuplier(String itemId, String supplierId);

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
	List<RfqSupplierBq> getAllBqsBySupplierId(String eventId, String supplierId);

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
	 * @param start
	 * @param pageLength
	 * @param itemOrder
	 * @param itemLevel
	 * @return
	 */
	List<RfqSupplierBqItem> getBqItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer start, Integer pageLength, Integer itemLevel, Integer itemOrder);

	List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String supplierId, String searchVal);

	/**
	 * @param bqId
	 * @param supplierId
	 * @param searchVal
	 * @return
	 */
	long totalBqItemCountByBqId(String bqId, String supplierId, String searchVal);

	RfqSupplierBqItem getBqItemByRfqBqItemId(String bqItemId, String supplierId);

	List<RfqSupplierBqItem> findBqItemListByBqIdAndSupplierId(String bqId, String supplierId);

	PricingTypes getPriceTypeByBqItemId(String bqId);

	RfqSupplierBqItem getSupplierBqItemByBqItemId(String itemId, String supplierId);

	List<String> findGrandTotalsForEvaluationView(String bqId, String eventId, List<Supplier> suppliers, Object object);

	int countIncompleteBqItemByBqIdsForSupplier(String id, String id2);

	List<RfqSupplierBqItem> findSupplierBqItemListByBqIdAndSupplierIds(String bqId, String supplierIds);

	List<RfqSupplierBqItem> findBqItemListAndBqListByBqIdAndSupplierId(String bqId, String supplierId);

}
