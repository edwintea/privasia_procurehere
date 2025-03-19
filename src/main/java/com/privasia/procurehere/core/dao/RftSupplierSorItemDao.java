package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RftSorItem;
import com.privasia.procurehere.core.entity.RftSupplierSor;
import com.privasia.procurehere.core.entity.RftSupplierSorItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.pojo.SorItemPojo;

import java.util.List;

public interface RftSupplierSorItemDao extends GenericDao<RftSupplierSorItem, String> {
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
    List<RftSupplierSorItem> getSorItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer start, Integer pageLength, Integer itemLevel, Integer itemOrder);


    long totalSorItemCountBySorId(String eventBqId, String supplierId, String searchVal);


    List<RftSorItem> getSorItemsbyId(String bqId);


    /**
     * @param eventId
     * @param supplierId
     * @return
     */
    List<RftSupplierSor> getAllSorsBySupplierId(String eventId, String supplierId);

    List<RftSupplierSorItem> findSupplierSorItemsBySorItemIdAndEventId(String bqItemId, String eventId, List<Supplier> suppliers);


    /**
     * @param bqId
     * @param supplierId
     * @return
     */
    List<RftSupplierSorItem> findSupplierSorItemByListBySorIdAndSupplierId(String bqId, String supplierId);

    List<RftSupplierSorItem> findSupplierBqItemListByBqIdAndSupplierIdsParentIdNotNull(String bqId, String supplierIds);

    /**
     * @param eventId
     */
    void deleteSupplierSorItemsForEvent(String eventId);


    Boolean checkAllFieldCompleted(String sorId, String supplierId);
}
