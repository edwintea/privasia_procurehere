package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import com.privasia.procurehere.core.entity.SourcingFormRequestSorItem;
import com.privasia.procurehere.core.pojo.SourcingSorItemPojo;

import java.util.List;

public interface SourcingFormRequestSorItemDao extends GenericDao<SourcingFormRequestSorItem, String> {

    /**
     * @param bqId
     * @param itemLevel
     * @param itemOrder
     * @param searchVal
     * @param start
     * @param length
     * @return
     */
    List<SourcingFormRequestSorItem> getSorItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder,
                                                               String searchVal, Integer start, Integer length);


    /**
     * @param bqId
     * @param searchVal
     * @return
     */
    List<SourcingSorItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String searchVal);


    /**
     * @param bqId
     * @param searchVal
     * @return
     */
    long getTotalSorItemCountBySorId(String bqId, String searchVal);


    /**
     * @param sourcingBqItem
     * @param bq
     * @param parent
     * @return
     */
    boolean isExist(SourcingFormRequestSorItem sourcingBqItem, String bq, String parent);


    /**
     * @param bqId
     * @return
     */
    List<SourcingFormRequestSorItem> getSorItemLevelOrder(String bqId);


    /**
     * @param bqItem
     * @param bqId
     * @param oldParent
     * @param newParent
     * @param oldOrder
     * @param newOrder
     * @param oldLevel
     * @param newLevel
     */
    void updateItemOrder(SourcingFormRequestSorItem bqItem, String bqId, String oldParent, String newParent, int oldOrder, int newOrder,
                         int oldLevel, int newLevel);

    /**
     * @param bqItemId
     * @return
     */
    SourcingFormRequestSorItem getSorItemBySorItemId(String bqItemId);


    /**
     * @param id
     * @return
     */
    SourcingFormRequestSor getAllsorItemsBySorId(String id);


    /**
     * @param id
     * @return
     */
    List<SourcingFormRequestSorItem> getAllsorItemBySorId(String id);


    /**
     * @param bqItemsIds
     * @param bqId
     * @return
     */
    String deleteSorItems(String[] bqItemsIds, String bqId);


    /**
     * @param bqId
     */
    void deleteSorItemsbySorid(String bqId);


    /**
     * @param bqId
     * @param level
     * @return
     */
    SourcingFormRequestSorItem getParentbyLevelId(String bqId, Integer level);


    void deletefieldInSorItems(String bqId, String label);
}
