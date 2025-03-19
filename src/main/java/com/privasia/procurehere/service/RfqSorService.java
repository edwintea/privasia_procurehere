package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import org.springframework.context.NoSuchMessageException;

import java.io.File;
import java.util.List;

public interface RfqSorService {

    void saveRfqSor(RfqEventSor rfqEventSor);
    void updateSor(RfqEventSor rfqEventSor);
    boolean isSorExists(SorPojo sorPojo);

    List<RfqEventSor> getAllSorListByEventIdByOrder(String eventId);

    List<Sor> findSorbyEventId(String eventId);

    RfqEventSor getSorById(String id);

    long totalSorItemCountByBqId(String sorId, String searchVal);

    List<RfqSorItem> getSorItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo);

    List<BqItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String searchVal);

    RfqSorItem getSorItemsbySorId(String id);


    boolean isSorItemExists(RfqSorItem item, String bqId, String parentId);

    RfqSorItem saveSorItem(RfqSorItem rfqSorItem);

    void reorderBqItems(BqItemPojo bqItemPojo) throws NotAllowedException;

    void updateSorItem(RfqSorItem rftBqItem);

    void deleteAllSor(String eventId, String eventRequirement);

    Integer getCountOfSorByEventId(String eventId);

    List<RfqEventSor> getEventSorForEventIdForEnvelop(String id, List<String> sorIds);

    List<RfqEventSor> getNotAssignedSorIdsByEventId(String eventId);

    List<RfqSorItem> getAllSoritemsbyBqId(String bqId);

    RfqEventSor updateRfqSorFields(RfqEventSor eventBq);

    int uploadSorFile(String bqId, String eventId, File convFile, String tenantId) throws ExcelParseException;

    RfqEventSor getRfqEventSorBySorId(String bqId);


    void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception;


    void deleteRfqSor(String id);

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
     * @param id
     * @return
     */
    List<String> getNotSectionAddedRfqSorIdsByEventId(String id);


    List<String> getNotSectionItemAddedRfqSorIdsByEventId(String id);
}
