package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import com.privasia.procurehere.core.entity.SourcingFormRequestSorItem;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.SourcingReqSorPojo;
import com.privasia.procurehere.core.pojo.SourcingSorItemPojo;

import java.io.File;
import java.util.List;

public interface SourcingFormRequestSorService {

    /**
     * @param formId
     * @return
     */
    List<SourcingFormRequestSor> findSorByFormIdByOrder(String formId);

    /**
     * @param formId
     * @param bqId
     * @param name
     * @return
     */
    boolean isSorExists(String formId, String bqId, String name);


    /**
     * @param sourcingReqBqPojo
     * @return
     */
    SourcingFormRequestSor saveSourcingSor(SourcingReqSorPojo sourcingReqBqPojo);


    /**
     * @param sorId
     * @return
     */
    SourcingFormRequestSor getSorById(String sorId);


    /**
     * @param bq
     * @return
     */
    SourcingFormRequestSor updateSourcingSor(SourcingFormRequestSor bq);


    /**
     * @param formId
     * @return
     */
    SourcingFormRequest getSourcingRequestSorByFormId(String formId);


    /**
     * @param bqId
     * @return
     */
    SourcingFormRequestSor findSorById(String bqId);


    /**
     * @param bqId
     * @param itemLevel
     * @param itemOrder
     * @param searchVal
     * @param start
     * @param length
     * @param pageNo
     * @return
     */
    List<SourcingFormRequestSorItem> getSorItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal,
                                                               Integer start, Integer length, Integer pageNo);


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
    long getTotalSortemCountBySorId(String bqId, String searchVal);


    /**
     * @param id
     */
    void deleteSourcingSor(String id);


    /**
     * @param bqParent
     * @return
     */
    SourcingFormRequestSorItem getSorItemsbyBqId(String bqParent);


    /**
     * @param sourcingBqItem
     * @param bq
     * @param parent
     * @return
     */
    boolean isSorItemExists(SourcingFormRequestSorItem sourcingBqItem, String bq, String parent);

    /**
     * @param sourcingBqItem
     * @return
     */
    SourcingFormRequestSorItem saveSourcingSorItem(SourcingFormRequestSorItem sourcingBqItem);


    /**
     * @param sourcingReqBqItem
     * @return
     */
    SourcingFormRequestSorItem updateSourcingSorItem(SourcingFormRequestSorItem sourcingReqBqItem);

    /**
     * @param sourcingSorItemPojo
     * @throws NotAllowedException
     */
    void reorderSorItems(SourcingSorItemPojo sourcingSorItemPojo) throws NotAllowedException;


    /**
     * @param id
     * @return
     */
    SourcingFormRequestSor getAllsorItemsBySorId(String id);


    /**
     * @param bqItemsIds
     * @param bqId
     * @throws NotAllowedException
     */
    void deleteSourcingSorItems(String[] bqItemsIds, String bqId) throws NotAllowedException;


    /**
     * @param bqId
     * @param rfsId
     * @param convFile
     * @param tenantId
     * @return
     * @throws ExcelParseException
     */
    int uploadSorFile(String bqId, String rfsId, File convFile, String tenantId) throws ExcelParseException;


    /**
     * @param bqId
     * @param label
     */
    void deletefieldInSorItems(String bqId, String label);
}
