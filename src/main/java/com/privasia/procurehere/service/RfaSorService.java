package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.core.entity.RfaSorItem;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import org.springframework.context.NoSuchMessageException;

import java.io.File;
import java.util.List;

public interface RfaSorService {
    /**
     * @param eventId
     * @return
     */
    List<RfaEventSor> getAllSorListByEventIdByOrder(String eventId);

    /**
     * @param rfqEventSor
     */
    void updateSor(RfaEventSor rfqEventSor);

    /**
     * @param sorPojo
     * @return
     */
    boolean isSorExists(SorPojo sorPojo);

    /**
     * @param eventId
     * @return
     */
    List<Sor> findSorbyEventId(String eventId);

    /**
     * @param rfqEventSor
     */
    void saveRfaSor(RfaEventSor rfqEventSor);

    /**
     * @param id
     * @return
     */
    RfaEventSor getSorById(String id);

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
    List<RfaSorItem> getSorItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo);

    /**
     * @param bqId
     * @param searchVal
     * @return
     */
    List<BqItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String searchVal);

    /**
     * @param sorId
     * @param searchVal
     * @return
     */
    long totalSorItemCountByBqId(String sorId, String searchVal);

    /**
     * @param id
     * @return
     */
    RfaSorItem getSorItemsbySorId(String id);

    /**
     * @param item
     * @param bqId
     * @param parentId
     * @return
     */
    boolean isSorItemExists(RfaSorItem item, String bqId, String parentId);

    /**
     * @param rfqSorItem
     * @return
     */
    RfaSorItem saveSorItem(RfaSorItem rfqSorItem);

    /**
     * @param rftBqItem
     */
    void updateSorItem(RfaSorItem rftBqItem);

    /**
     * @param bqItemPojo
     * @throws NotAllowedException
     */
    void reorderBqItems(BqItemPojo bqItemPojo) throws NotAllowedException;

    /**
     * @param bqId
     * @return
     */
    List<RfaSorItem> getAllSoritemsbySorId(String bqId);

    /**
     * @param bqId
     * @param eventId
     * @param convFile
     * @param tenantId
     * @return
     * @throws ExcelParseException
     */
    int uploadSorFile(String bqId, String eventId, File convFile, String tenantId) throws ExcelParseException;

    /**
     * @param eventBq
     * @return
     */
    RfaEventSor updateRfaSorFields(RfaEventSor eventBq);

    /**
     * @param sorId
     * @return
     */
    RfaEventSor getRfaEventSorBySorId(String sorId);

    /**
     * @param id
     * @param sorIds
     * @return
     */
    List<RfaEventSor> getEventSorForEventIdForEnvelop(String id, List<String> sorIds);

    /**
     * @param eventId
     * @return
     */
    List<RfaEventSor> getNotAssignedSorIdsByEventId(String eventId);

    /**
     * @param eventId
     * @return
     */
    Integer getCountOfSorByEventId(String eventId);

    /**
     * @param id
     * @param eventType
     * @throws Exception
     */
    void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception;

    /**
     * @param id
     */
    void deleteRfaSor(String id);


    /**
     * @param bqId
     * @param label
     */
    void deletefieldInSorItems(String bqId, String label);


    /**
     * @param bqId
     * @throws NoSuchMessageException
     * @throws NotAllowedException
     */
    void deleteAllSorItems(String bqId) throws NoSuchMessageException, NotAllowedException;


    /**
     * @param bqItemIds
     * @param bqId
     * @throws NotAllowedException
     */
    void deleteSorItems(String[] bqItemIds, String bqId) throws NotAllowedException;

    /**
     * @param eventId
     * @param eventRequirement
     */
    void deleteAllSor(String eventId, String eventRequirement);


    /**
     * @param id
     * @return
     */
    List<String> getNotSectionAddedRfaSorIdsByEventId(String id);


    List<String> getNotSectionItemAddedRfaSorIdsByEventId(String id);
}
