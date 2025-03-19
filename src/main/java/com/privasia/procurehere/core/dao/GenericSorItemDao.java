package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.SorItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;

import java.io.Serializable;
import java.util.List;

public interface GenericSorItemDao<T extends SorItem, PK extends Serializable> extends GenericDao<T, PK> {
    List<T> getSorItemForSearchFilter(String sorId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length);

    long totalSorItemCountByBqId(String sorId, String searchVal);

    List<BqItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String searchVal);

    boolean isExists(final T bqItem, String bqId, String parentId);

    List<T> getSorItemLevelOrder(String bqId);

    void updateItemOrder(T bqItem, String bqId, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel);

    void deleteSorItemsForEventId(String eventId);

    List<T> findSorItemsForSor(String sorId);

    List<T> getAllSorItemsbysorId(String bqId);

    /**
     * @param bqId
     */
    void deleteSorItemsbySorid(String bqId);

    /**
     * @param bqId
     * @param level
     * @return
     */
    T getParentbyLevelId(String bqId, Integer level);


    /**
     * @param id
     * @param label
     */
    void resetFieldsForFilledBySupplier(String id, List<String> label);


    /**
     * @param bqId
     * @param label
     */
    void deletefieldInSorItems(String bqId, String label);


    /**
     * @param parentId
     * @param bqId
     * @throws NotAllowedException
     */
    void deleteChildItems(String parentId, String bqId) throws NotAllowedException;


    /**
     * @param bqItemIds
     * @param bqId
     * @throws NotAllowedException
     */
    void deleteRemaingSorItems(String[] bqItemIds, String bqId) throws NotAllowedException;


    T getSorItembySorItemId(String id);
}
