package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfpSorItem;
import com.privasia.procurehere.core.entity.RfpSupplierSor;
import com.privasia.procurehere.core.entity.RfpSupplierSorItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.pojo.SorItemPojo;

import java.util.List;

public interface RfpSupplierSorItemDao extends GenericDao<RfpSupplierSorItem, String> {
    List<SorItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String supplierId, String searchVal);


    /**
     * @param eventBqId
     * @param supplierId
     * @param searchVal
     * @param start
     * @param pageLength
     * @param itemLevel
     * @param itemOrder
     * @return
     */
    List<RfpSupplierSorItem> getSorItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer start, Integer pageLength, Integer itemLevel, Integer itemOrder);


    long totalSorItemCountBySorId(String eventBqId, String supplierId, String searchVal);


    List<RfpSorItem> getSorItemsbyId(String bqId);


    /**
     * @param eventId
     * @param supplierId
     * @return
     */
    List<RfpSupplierSor> getAllSorsBySupplierId(String eventId, String supplierId);

    /**
     * @param bqItemId
     * @param eventId
     * @param suppliers
     * @return
     */

    List<RfpSupplierSorItem> findSupplierSorItemsBySorItemIdAndEventId(String bqItemId, String eventId, List<Supplier> suppliers);

    /**
     * @param bqId
     * @param supplierId
     * @return
     */
    List<RfpSupplierSorItem> findSupplierSorItemByListBySorIdAndSupplierId(String bqId, String supplierId);

    List<RfpSupplierSorItem> findSupplierBqItemListByBqIdAndSupplierIdsParentIdNotNull(String bqId, String supplierIds);


    /**
     * @param eventId
     */
    void deleteSupplierSorItemsForEvent(String eventId);

    Boolean checkAllFieldCompleted(String sorId, String supplierId);
}
