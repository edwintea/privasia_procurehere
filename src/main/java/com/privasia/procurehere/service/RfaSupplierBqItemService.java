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

public interface RfaSupplierBqItemService {

	/**
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	List<RfaSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfaSupplierBqItem> saveSupplierEventBq(String bqId);

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
	RfaSupplierBqItem getSupplierBqItemByBqItemAndSupplierId(String itemId, String supplierId);

	/**
	 * @param persistObject
	 * @return
	 */
	RfaSupplierBqItem updateSupplierBqItems(RfaSupplierBqItem persistObject);

	/**
	 * @param items
	 * @param supplierID TODO
	 */
	void updateBqItems(List<SupplierBqItem> items, String supplierID);

	/**
	 * @param persistObject
	 * @return
	 */
	RfaSupplierBqItem updateSupplierBqItem(RfaSupplierBqItem persistObject);

	/**
	 * @param eventId
	 * @param envelopId
	 * @param logedUser
	 * @param withOrWithoutTax
	 * @param selectedSuppliers
	 * @param itemLevel TODO
	 * @param itemOrder TODO
	 * @param searchVal TODO
	 * @param start TODO
	 * @param length TODO
	 * @return
	 */
	List<EventEvaluationPojo> getBqEvaluationData(String eventId, String envelopId, User logedUser, String withOrWithoutTax, List<Supplier> selectedSuppliers, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length);

	/**
	 * @param eventId
	 * @param envelopId
	 * @return
	 */
	List<EventEvaluationPojo> getEvaluationDataForBqComparison(String eventId, String envelopId);

	/**
	 * @param itemId
	 * @param supplierId
	 * @return
	 */
	RfaSupplierBqItem getBqItemByBqItemId(String itemId, String supplierId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfaSupplierBqItem> findAllSupplierBqItemListByBqId(String bqId);

	/**
	 * @param id
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	RfaSupplierBq findBqByEventIdAndSupplierId(String id, String bqId, String supplierId);

	/**
	 * @param bqId
	 * @param supplierId
	 * @param isPresetPreBidForAllSuppliers TODO
	 * @return
	 */
	List<RfaSupplierBqItem> saveSupplierEventBqByBuyer(String bqId, String supplierId);

	/**
	 * @param bq
	 * @param supplierId
	 * @param eventId TODO
	 * @param string
	 */
	void updateBqForSupplier(RfaSupplierBq bq, String supplierId, String decimal, String eventId);

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
	 * @param evelopId
	 * @return
	 */
	List<EventEvaluationPojo> getEvaluationDataForBqComparisonReport(String eventId, String evelopId);

	RfaSupplierBqItem getBqItemByRfaBqItemId(String itemId, String supplierId);

	List<RfaSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierIds(String bqId, String supplierIds);

	List<RfaSupplierBqItem> getAllSupplierBqItemForReportByBqIdAndSupplierId(String bqId, String id);

	void updateBqItems(List<SupplierBqItem> supplierBqItem, String loggedInUserTenantId, SupplierBqStatus status);

	void saveSupplierBqDetails(String bqId, List<RfaEventSupplier> eventSuppliers);

	RfxSupplierBqItemAndRfxEvent getTotalRfaSupplierBqItem(String eventId);
}
