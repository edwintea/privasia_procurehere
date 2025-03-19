package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfqSorItem;
import com.privasia.procurehere.core.entity.RfqSupplierSor;
import com.privasia.procurehere.core.entity.RfqSupplierSorItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.pojo.SorItemPojo;

import java.util.List;

public interface RfqSupplierSorItemDao extends GenericDao<RfqSupplierSorItem, String> {
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
    List<RfqSupplierSorItem> getSorItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer start, Integer pageLength, Integer itemLevel, Integer itemOrder);


    long totalSorItemCountBySorId(String eventBqId, String supplierId, String searchVal);


    List<RfqSorItem> getSorItemsbyId(String bqId);


    /**
     * @param eventId
     * @param supplierId
     * @return
     */
    List<RfqSupplierSor> getAllSorsBySupplierId(String eventId, String supplierId);


    /**
     * @param bqItemId
     * @param eventId
     * @param suppliers
     * @return
     */
    List<RfqSupplierSorItem> findSupplierSorItemsBySorItemIdAndEventId(String bqItemId, String eventId, List<Supplier> suppliers);


    /**
     * @param bqId
     * @param supplierId
     * @return
     */
    List<RfqSupplierSorItem> findSupplierSorItemByListBySorIdAndSupplierId(String bqId, String supplierId);



    List<RfqSupplierSorItem> findSupplierBqItemListByBqIdAndSupplierIdsParentIdNotNull(String bqId, String supplierIds);


    /**
     * @param eventId
     */
    void deleteSupplierSorItemsForEvent(String eventId);

    Boolean checkAllFieldCompleted(String sorId, String supplierId);
}
