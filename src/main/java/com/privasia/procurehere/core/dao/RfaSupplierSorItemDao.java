package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfaSorItem;
import com.privasia.procurehere.core.entity.RfaSupplierSor;
import com.privasia.procurehere.core.entity.RfaSupplierSorItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.pojo.SorItemPojo;

import java.util.List;

public interface RfaSupplierSorItemDao extends GenericDao<RfaSupplierSorItem, String> {
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
    List<RfaSupplierSorItem> getSorItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer start, Integer pageLength, Integer itemLevel, Integer itemOrder);


    long totalSorItemCountBySorId(String eventBqId, String supplierId, String searchVal);


    List<RfaSorItem> getSorItemsbyId(String bqId);

    /**
     * @param eventId
     * @param supplierId
     * @return
     */
    List<RfaSupplierSor> getAllSorsBySupplierId(String eventId, String supplierId);


    List<RfaSupplierSorItem> findSupplierSorItemsBySorItemIdAndEventId(String bqItemId, String eventId, List<Supplier> suppliers);


    /**
     * @param bqId
     * @param supplierId
     * @return
     */
    List<RfaSupplierSorItem> findSupplierSorItemByListBySorIdAndSupplierId(String bqId, String supplierId);


    List<RfaSupplierSorItem> findSupplierBqItemListByBqIdAndSupplierIdsParentIdNotNull(String bqId, String supplierIds);

    /**
     * @param eventId
     */
    void deleteSupplierSorItemsForEvent(String eventId);

    Boolean checkAllFieldCompleted(String sorId, String supplierId);
}
