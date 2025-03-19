package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.SupplierBqStatus;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.utils.RfxSupplierBqItemAndRfxEvent;

/**
 * @author Vipul
 * @author Ravi
 */

public interface RfpSupplierBqItemService {

	/**
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	List<RfpSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfpSupplierBqItem> saveSupplierEventBq(String bqId);

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
	RfpSupplierBqItem getSupplierBqItemByBqItemAndSupplierId(String itemId, String supplierId);

	/**
	 * @param persistObject
	 * @return
	 */
	RfpSupplierBqItem updateSupplierBqItems(RfpSupplierBqItem persistObject);

	/**
	 * @param items
	 * @param supplierId TODO
	 */
	void updateBqItems(List<SupplierBqItem> items, String supplierId);

	/**
	 * @param persistObject
	 * @return
	 */
	RfpSupplierBqItem updateSupplierBqItem(RfpSupplierBqItem persistObject);

	/**
	 * @param eventId
	 * @param envelopId
	 * @param selectedSuppliers TODO
	 * @param logedUser TODO
	 * @param withOrWithoutTax TODO
	 * @param itemLevel TODO
	 * @param itemOrder TODO
	 * @param searchVal TODO
	 * @param start TODO
	 * @param length TODO
	 * @return
	 */
	List<EventEvaluationPojo> getBqEvaluationData(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser, String withOrWithoutTax, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length);

	/**
	 * @param eventId
	 * @param envelopId
	 * @return
	 */
	List<EventEvaluationPojo> getEvaluationDataForBqComparison(String eventId, String envelopId);

	/**
	 * @param eventBqId
	 * @param loggedInUserTenantId
	 * @param searchVal
	 * @param pageNo
	 * @param pageLength
	 * @param itemOrder
	 * @param itemLevel
	 * @return
	 */
	List<?> getBqItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer pageNo, Integer pageLength, Integer itemLevel, Integer itemOrder);

	List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String supplierId, String searchVal);

	/**
	 * @param eventId
	 * @param envelopId
	 * @param logedUser
	 * @param withOrWithoutTax
	 * @param itemLevel
	 * @param itemOrder
	 * @param searchVal
	 * @param start
	 * @param length
	 * @param supplierList 
	 * @return
	 */
	List<EventEvaluationPojo> getBqSearchFilterEvaluationData(String eventId, String envelopId, User logedUser, String withOrWithoutTax, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, String[] supplierList);

	/**
	 * @param eventBqId
	 * @param loggedInUserTenantId
	 * @param searchVal
	 * @return
	 */
	long totalBqItemCountByBqId(String eventBqId, String supplierId, String searchVal);

	/**
	 * @param eventId
	 * @param envelopId
	 * @return
	 */
	List<EventEvaluationPojo> getEvaluationDataForBqComparisonReport(String eventId, String envelopId);

	RfpSupplierBqItem getBqItemByRfpBqItemId(String itemId, String supplierId);

	List<RfpSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierIds(String bqId, String string);

	List<RfpSupplierBqItem> getAllSupplierBqItemForReportByBqIdAndSupplierId(String bqId, String id);

	void updateBqItems(List<SupplierBqItem> supplierBqItem, String loggedInUserTenantId, SupplierBqStatus status);

	RfxSupplierBqItemAndRfxEvent getTotalRfpSupplierBqItem(String eventId);

}
