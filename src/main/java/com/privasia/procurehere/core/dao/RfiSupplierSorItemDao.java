package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfiSorItem;
import com.privasia.procurehere.core.entity.RfiSupplierSor;
import com.privasia.procurehere.core.entity.RfiSupplierSorItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.pojo.SorItemPojo;

import java.util.List;

public interface RfiSupplierSorItemDao extends GenericDao<RfiSupplierSorItem, String> {
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
    List<RfiSupplierSorItem> getSorItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer start, Integer pageLength, Integer itemLevel, Integer itemOrder);


    long totalSorItemCountBySorId(String eventBqId, String supplierId, String searchVal);


    List<RfiSorItem> getSorItemsbyId(String bqId);


    /**
     * @param eventId
     * @param supplierId
     * @return
     */
    List<RfiSupplierSor> getAllSorsBySupplierId(String eventId, String supplierId);

    List<RfiSupplierSorItem> findSupplierSorItemsBySorItemIdAndEventId(String bqItemId, String eventId, List<Supplier> suppliers);


    /**
     * @param bqId
     * @param supplierId
     * @return
     */
    List<RfiSupplierSorItem> findSupplierSorItemByListBySorIdAndSupplierId(String bqId, String supplierId);

    List<RfiSupplierSorItem> findSupplierBqItemListByBqIdAndSupplierIdsParentIdNotNull(String bqId, String supplierIds);


    /**
     * @param eventId
     */
    void deleteSupplierSorItemsForEvent(String eventId);


    Boolean checkAllFieldCompleted(String sorId, String supplierId);
}
