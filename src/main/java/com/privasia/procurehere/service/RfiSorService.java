package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.RfiSorItem;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import org.springframework.context.NoSuchMessageException;

import java.io.File;
import java.util.List;

public interface RfiSorService {
    /**
     * @param eventId
     * @return
     */
    List<RfiEventSor> getAllSorListByEventIdByOrder(String eventId);

    /**
     * @param rfqEventSor
     */
    void updateSor(RfiEventSor rfqEventSor);

    /**
     * @param rfqEventSor
     */
    void saveRfiSor(RfiEventSor rfqEventSor);

    /**
     * @param eventId
     * @return
     */
    List<Sor> findSorbyEventId(String eventId);

    /**
     * @param id
     * @return
     */
    RfiEventSor getSorById(String id);

    /**
     * @param id
     * @param eventType
     * @throws Exception
     */
    void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception;

    /**
     * @param id
     */
    void deleteRfiSor(String id);

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
    List<RfiSorItem> getSorItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo);

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
    RfiSorItem getSorItemsbySorId(String id);

    /**
     * @param item
     * @param bqId
     * @param parentId
     * @return
     */
    boolean isSorItemExists(RfiSorItem item, String bqId, String parentId);

    /**
     * @param rfqSorItem
     * @return
     */
    RfiSorItem saveSorItem(RfiSorItem rfqSorItem);


    /**
     * @param rftBqItem
     */
    void updateSorItem(RfiSorItem rftBqItem);


    /**
     *
     * @param bqId
     * @return
     */
    RfiEventSor getRfiEventSorBySorId(String bqId);

    /**
     * @param sorPojo
     * @return
     */
    boolean isSorExists(SorPojo sorPojo);

    /**
     * @param bqId
     * @return
     */
    List<RfiSorItem> getAllSoritemsbySorId(String bqId);


    /**
     * @param bqItemPojo
     * @throws NotAllowedException
     */
    void reorderBqItems(BqItemPojo bqItemPojo) throws NotAllowedException;


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
    RfiEventSor updateRfiSorFields(RfiEventSor eventBq);


    /**
     * @param id
     * @param sorIds
     * @return
     */
    List<RfiEventSor> getEventSorForEventIdForEnvelop(String id, List<String> sorIds);


    /**
     * @param eventId
     * @return
     */
    List<RfiEventSor> getNotAssignedSorIdsByEventId(String eventId);


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
     * @return
     */
    Integer getCountOfSorByEventId(String eventId);


    /**
     * @param eventId
     * @param eventRequirement
     */
    void deleteAllSor(String eventId, String eventRequirement);


    /**
     * @param id
     * @return
     */
    List<String> getNotSectionAddedRfiSorIdsByEventId(String id);


    List<String> getNotSectionItemAddedRfiSorIdsByEventId(String id);
}
