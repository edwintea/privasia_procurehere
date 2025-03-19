package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierBqItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupplierBqStatus;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.utils.RfxSupplierBqItemAndRfxEvent;

/**
 * @author Vipul
 */

public interface RftSupplierBqItemService {

	List<RftSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierId(String bqId, String supplierId);

	void saveSupplierEventBq(String bqId, String tenantId);

	List<RftBqItem> getBqItemsbyId(String bqId);

	RftSupplierBqItem getSupplierBqItemByBqItemAndSupplierId(String itemId, String supplierId);

	RftSupplierBqItem updateSupplierBqItems(RftSupplierBqItem persistObject);

	void updateBqItems(List<SupplierBqItem> items, String supplierId);

	/**
	 * @param persistObject
	 * @return
	 */
	RftSupplierBqItem updateSupplierBqItem(RftSupplierBqItem persistObject);

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
	 * @param bqId
	 * @param tenantId
	 * @return
	 */
	List<RftSupplierBqItem> saveAndGetSupplierEventBq(String bqId, String tenantId);

	/**
	 * @param eventId
	 * @param envelopId
	 * @param selectedSuppliers
	 * @param logedUser TODO
	 * @return
	 */
	List<EventEvaluationPojo> getBqEvaluationDataForWithOutTax(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser);

	/**
	 * @param eventId
	 * @return
	 */
	Integer countAllRftSupplierBqItemByEventId(String supplierId, String eventId);

	/**
	 * @param eventBqId
	 * @param loggedInUserTenantId
	 * @param searchVal
	 * @param pageLength
	 * @param pageNo
	 * @param itemOrder
	 * @param itemLevel
	 * @return
	 */
	List<?> getBqItemForSearchFilterForSupplier(String eventBqId, String loggedInUserTenantId, String searchVal, Integer pageNo, Integer pageLength, Integer itemLevel, Integer itemOrder);

	List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String supplierId, String searchVal);

	/**
	 * @param eventId
	 * @param evelopId
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
	List<EventEvaluationPojo> getBqSearchFilterEvaluationData(String eventId, String evelopId, User logedUser, String withOrWithoutTax, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, String[] supplierList);

	/**
	 * @param bqId
	 * @param supplierId TODO
	 * @param searchVal
	 * @return
	 */
	long totalBqItemCountByBqId(String bqId, String supplierId, String searchVal);

	/**
	 * @param eventId
	 * @param envelopId
	 * @return
	 */
	List<EventEvaluationPojo> getEvaluationDataForBqComparisonReport(String eventId, String envelopId);

	RftSupplierBqItem getBqItemByRftBqItemId(String itemId, String supplierId);

	List<RftSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierIds(String bqId, String supplierIds);

	List<RftSupplierBqItem> getAllSupplierBqItemForReportByBqIdAndSupplierId(String bqId, String supplierId);

	void updateBqItems(List<SupplierBqItem> supplierBqItem, String supplierId, SupplierBqStatus status);

	RfxSupplierBqItemAndRfxEvent getTotalRftSupplierBqItem(String eventId);

}
