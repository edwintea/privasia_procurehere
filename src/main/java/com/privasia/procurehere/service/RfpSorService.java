package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfpEventSor;
import com.privasia.procurehere.core.entity.RfpSorItem;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import org.springframework.context.NoSuchMessageException;

import java.io.File;
import java.util.List;

public interface RfpSorService {
    List<RfpEventSor> getAllSorListByEventIdByOrder(String eventId);

    boolean isSorExists(SorPojo sorPojo);

    void updateSor(RfpEventSor rfqEventSor);


    void saveRfpSor(RfpEventSor rfqEventSor);

    List<Sor> findSorbyEventId(String eventId);

    List<RfpSorItem> getSorItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo);

    RfpEventSor getSorById(String id);

    long totalSorItemCountByBqId(String sorId, String searchVal);

    List<BqItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String searchVal);

    RfpSorItem getSorItemsbySorId(String id);

    boolean isSorItemExists(RfpSorItem item, String bqId, String parentId);

    RfpSorItem saveSorItem(RfpSorItem rfqSorItem);

    void updateSorItem(RfpSorItem rftBqItem);

    List<RfpSorItem> getAllSoritemsbySorId(String bqId);

    int uploadSorFile(String bqId, String eventId, File convFile, String tenantId) throws ExcelParseException;

    RfpEventSor updateRfpSorFields(RfpEventSor eventBq);

    RfpEventSor getRfpEventSorBySorId(String bqId);

    void reorderBqItems(BqItemPojo bqItemPojo) throws NotAllowedException;

    List<RfpEventSor> getNotAssignedSorIdsByEventId(String eventId);

    List<RfpEventSor> getEventSorForEventIdForEnvelop(String id, List<String> sorIds);

    Integer getCountOfSorByEventId(String eventId);

    void deleteAllSor(String eventId, String eventRequirement);

    void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception;


    void deleteRfpSor(String id);

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
     * @param id
     * @return
     */
    List<String> getNotSectionAddedRfpSorIdsByEventId(String id);


    List<String> getNotSectionItemAddedRfpSorIdsByEventId(String id);
}
