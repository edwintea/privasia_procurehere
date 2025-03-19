package com.privasia.procurehere.service;


import com.privasia.procurehere.core.entity.RftEventSor;
import com.privasia.procurehere.core.entity.RftSorItem;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import org.springframework.context.NoSuchMessageException;

import java.io.File;
import java.util.List;

public interface RftSorService {
    List<RftEventSor> getAllSorListByEventIdByOrder(String eventId);

    Integer getCountOfSorByEventId(String eventId);

    boolean isSorExists(SorPojo sorPojo);

    void updateSor(RftEventSor rfqEventSor);

    void saveRftSor(RftEventSor rfqEventSor);

    List<Sor> findSorbyEventId(String eventId);

    RftEventSor getSorById(String id);

    void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception;


    void deleteRftSor(String id);

    List<RftSorItem> getSorItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo);

    List<BqItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String searchVal);

    long totalSorItemCountByBqId(String sorId, String searchVal);

    RftSorItem getSorItemsbySorId(String id);

    boolean isSorItemExists(RftSorItem item, String bqId, String parentId);

    RftSorItem saveSorItem(RftSorItem rfqSorItem);

    void updateSorItem(RftSorItem rftBqItem);

    void reorderBqItems(BqItemPojo bqItemPojo) throws NotAllowedException;

    List<RftSorItem> getAllSoritemsbySorId(String bqId);

    int uploadSorFile(String bqId, String eventId, File convFile, String tenantId) throws ExcelParseException;

    RftEventSor updateRftSorFields(RftEventSor eventBq);

    List<RftEventSor> getEventSorForEventIdForEnvelop(String id, List<String> sorIds);

    

    List<RftEventSor> getNotAssignedSorIdsByEventId(String eventId);


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


    List<String> getNotSectionAddedRftSorIdsByEventId(String id);

    List<String> getNotSectionItemAddedRftSorIdsByEventId(String id);
}
