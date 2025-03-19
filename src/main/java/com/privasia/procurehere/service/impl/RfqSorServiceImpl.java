package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfqEnvelopDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqSorDao;
import com.privasia.procurehere.core.dao.RfqSorItemDao;
import com.privasia.procurehere.core.entity.RfaSorItem;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventSor;
import com.privasia.procurehere.core.entity.RfqSorItem;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.parsers.SorFileParser;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfqSorService;
import com.privasia.procurehere.service.UomService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(readOnly = true)
public class RfqSorServiceImpl implements RfqSorService {

    private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

    @Autowired
    RfqSorDao rfqSorDao;

    @Autowired
    RfqSorItemDao rfqSorItemDao;

    @Autowired
    RfqEventDao rfqEventDao;

    @Autowired
    RfqEnvelopDao rfqEnvelopDao;

    @Autowired
    UomService uomService;

    @Autowired
    MessageSource messageSource;

    @Override
    @Transactional(readOnly = false)
    public void saveRfqSor(RfqEventSor rfqEventSor) {
        rfqSorDao.saveOrUpdate(rfqEventSor);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateSor(RfqEventSor rfqEventSor) {
        rfqSorDao.update(rfqEventSor);
    }

    @Override
    public boolean isSorExists(SorPojo sorPojo) {
        RfqEventSor rfqEventSor = new RfqEventSor();
        rfqEventSor.setId(sorPojo.getId());
        rfqEventSor.setName(sorPojo.getSorName());
        return rfqSorDao.isExists(rfqEventSor, sorPojo.getEventId());
    }

    @Override
    public List<RfqEventSor> getAllSorListByEventIdByOrder(String eventId) {
        return rfqSorDao.findSorsByEventIdByOrder(eventId);
    }

    @Override
    public List<Sor> findSorbyEventId(String eventId) {
        return rfqSorDao.findSorbyEventId(eventId);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public RfqEventSor getSorById(String id) {
        return rfqSorDao.findById(id);
    }

    @Override
    public long totalSorItemCountByBqId(String bqId, String searchVal) {
        return rfqSorItemDao.totalSorItemCountByBqId(bqId, searchVal);
    }

    @Override
    public List<RfqSorItem> getSorItemForSearchFilter(String sorId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo) {
        List<RfqSorItem> returnList = new ArrayList<RfqSorItem>();
        if (pageNo != null) {
            start = pageNo - 1;
        }
        if (length != null) {
            start = start * length;
        }
        LOG.info(" start  : " + start);
        List<RfqSorItem> bqList = rfqSorItemDao.getSorItemForSearchFilter(sorId, itemLevel, itemOrder, searchVal, start, length);
        if (CollectionUtil.isNotEmpty(bqList)) {
            for (RfqSorItem item : bqList) {
                RfqSorItem bqItem = item.createSearchShallowCopy();
                returnList.add(bqItem);
            }
        }
        return returnList;
    }

    @Override
    public List<BqItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String searchVal) {
        return rfqSorItemDao.getAllLevelOrderSorItemBySorId(bqId, searchVal);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public RfqSorItem getSorItemsbySorId(String parent) {
        return rfqSorItemDao.findById(parent);
    }

    @Override
    public boolean isSorItemExists(RfqSorItem item, String bqId, String parentId) {
        return rfqSorItemDao.isExists(item, bqId, parentId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void reorderBqItems(BqItemPojo rftBqItemPojo) throws NotAllowedException {
        LOG.info("SOR ITEM Object :: " + rftBqItemPojo.toString());
        int newOrder = rftBqItemPojo.getOrder();
        RfqSorItem bqItem = getSorItemsbySorId(rftBqItemPojo.getId());

        if (CollectionUtil.isNotEmpty(bqItem.getChildren()) && rftBqItemPojo.getParent() != null) {
            throw new NotAllowedException("Schedule of Rate Item cannot be made a child if it has sub items");
        }

        LOG.info("DB SOR ITEM DETAILS ::" + bqItem);
        int oldOrder = bqItem.getOrder();
        LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
        int oldLevel = bqItem.getLevel();
        int newLevel = rftBqItemPojo.getOrder(); // this will be ignored if it is made a child
        RfqSorItem newParent = null;
        if (rftBqItemPojo.getParent() != null) {
            newParent = getSorItemsbySorId(rftBqItemPojo.getParent());
        }
        RfqSorItem oldParent = bqItem.getParent();

        // If these are not child, their order should be reset to 0
        if (oldParent == null) {
            oldOrder = 0;
        }
        if (newParent == null) {
            newOrder = 0;
        }

        // Update it to new position.
        bqItem.setOrder(newOrder);
        bqItem.setLevel(newParent == null ? rftBqItemPojo.getOrder() : newParent.getLevel());
        bqItem.setParent(newParent);
        rfqSorItemDao.updateItemOrder(bqItem, bqItem.getSor().getId(), (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateSorItem(RfqSorItem rftBqItem) {
        rfqSorItemDao.update(rftBqItem);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAllSor(String eventId, String eventRequirement) {
        // Will implement this one after the envelope issue one
        rfqEnvelopDao.removeSorsFromEnvelops(eventId);
        rfqSorItemDao.deleteSorItemsForEventId(eventId);
        rfqSorDao.deleteFromEnvSor(eventId, RfxTypes.RFQ);
        rfqSorDao.deleteSorForEventId(eventId);

        RfqEvent event = rfqEventDao.findById(eventId);
        event.setSorCompleted(Boolean.FALSE);
        event.setScheduleOfRate(Boolean.FALSE);
        rfqEventDao.update(event);
    }

    @Override
    @Transactional(readOnly = false)
    public RfqSorItem saveSorItem(RfqSorItem rftBqItem) {
        if (rftBqItem.getParent() == null) {
            int itemLevel = 0;
            List<RfqSorItem> list = rfqSorItemDao.getSorItemLevelOrder(rftBqItem.getSor().getId());
            if (CollectionUtil.isNotEmpty(list)) {
                itemLevel = list.size();
            }
            LOG.info("ITEM LEVEL :: " + itemLevel);
            rftBqItem.setLevel(itemLevel + 1);
            rftBqItem.setOrder(0);
        } else {
            RfqSorItem parent = getSorItemsbySorId(rftBqItem.getParent().getId());
            if (rftBqItem.getParent() != null) {
                rftBqItem.setLevel(parent.getLevel());
                rftBqItem.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
            }
        }
        return rfqSorItemDao.saveOrUpdate(rftBqItem);
    }

    @Override
    public Integer getCountOfSorByEventId(String eventId) {
        return rfqSorDao.getCountOfSorByEventId(eventId);
    }

    @Override
    public List<RfqEventSor> getEventSorForEventIdForEnvelop(String id, List<String> bqIds) {
        return rfqSorDao.findSorsByEventIdForEnvelop(id, bqIds);
    }

    @Override
    public List<RfqEventSor> getNotAssignedSorIdsByEventId(String eventId) {
        return rfqEnvelopDao.getNotAssignedSorIdsByEventId(eventId);
    }

    @Override
    public List<RfqSorItem> getAllSoritemsbyBqId(String bqId) {
        return rfqSorItemDao.getAllSorItemsbysorId(bqId);
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { ExcelParseException.class, Exception.class })
    public int uploadSorFile(String bqId, String eventId, File convFile, String tenantId) throws ExcelParseException {
        SorFileParser<RfqSorItem> rfqBi = new SorFileParser<RfqSorItem>(RfqSorItem.class);
        List<RfqSorItem> rfqBqList = rfqBi.parse(convFile);

        // Delete existing BQ Items by Bqs.
        rfqSorItemDao.deleteSorItemsbySorid(bqId);

        // adding columns while uploading BQs
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            workbook = WorkbookFactory.create(convFile);
            sheet = workbook.getSheetAt(0);
        } catch (InvalidFormatException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (RfqSorItem bi : rfqBqList) {
            Integer order = map.get(bi.getLevel());
            if (order == null) {
                map.put(bi.getLevel(), 1);
            } else {
                map.put(bi.getLevel(), order + 1);
            }
        }

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            Integer level = entry.getKey();
            Integer order = entry.getValue();
            LOG.info("Level : " + level + " Order : " + order);
            if (order < 2 && order != 1) {
                throw new ExcelParseException("Upload failed: \"Section is mandatory\"", convFile.getName());
            }
        }
        RfqEventSor rfqBq = null;
        if (sheet != null) {
            rfqBq = getSorById(bqId);
            int startingRow = 0;
            List<String> columnsTitle = new ArrayList<String>();
            Row rowData = sheet.getRow(startingRow);
            // columnCount = rowData.length;
            int columnCount = rowData.getLastCellNum();
            if (columnCount > 0 && rowData.getCell(0).getStringCellValue().trim().length() > 0) {
                for (int j = 4; j < columnCount; j++) {
                    if (j >= 4) {
                        String columnName = rowData.getCell(j).getStringCellValue().trim();
                        if (StringUtils.checkString(columnName).length() > 0) {
                            columnsTitle.add(columnName);
                        }
                    }
                }
            }
            buildAddNewColumnsToUpload(rfqBq, columnsTitle);
            rfqBq = updateRfqSorFields(rfqBq);
        }

        int count = 1;
        List<String> columns = new ArrayList<String>();
        for (RfqSorItem bi : rfqBqList) {
            RfqSorItem rfqBqItem = new RfqSorItem();
            // RfqEventBq rfqBq = rfqBqDao.findBqItemById(bqId);

            // checking Extra column in Excel match with bq columns
            if (count == 1 && CollectionUtil.isNotEmpty(bi.getColumnTitles())) {

                if (StringUtils.checkString(rfqBq.getField1Label()).length() > 0) {
                    columns.add(rfqBq.getField1Label());
                }

                if (columns.size() != bi.getColumnTitles().size()) {
                    LOG.info("Invalid Excel format size");
                    throw new ExcelParseException("Invalid Excel format. There should be '" + (columns.size() + 4) + "' Columns in excel.", convFile.getName());
                }

                for (int i = 0; i < columns.size(); i++) {
                    if (!columns.get(i).equalsIgnoreCase(bi.getColumnTitles().get(i))) {
                        LOG.info("Invalid Excel format column '" + columns.get(i) + "' in excel does not match with '" + bi.getColumnTitles().get(i) + "' in Bill of Quantity.");
                        throw new ExcelParseException("Invalid Excel format column '" + columns.get(i) + "' in excel does not match with '" + bi.getColumnTitles().get(i) + "' in Bill of Quantity.", convFile.getName());
                    }
                }

                count++;
            }
            rfqBqItem.setSor(rfqBq);
            RfqEvent event = rfqEventDao.findEventForBqByEventId(eventId);
            rfqBqItem.setRfxEvent(event);

            rfqBqItem.setItemName(bi.getItemName());

            rfqBqItem.setItemDescription(bi.getItemDescription());
            rfqBqItem.setLevel(bi.getLevel());
            rfqBqItem.setOrder(bi.getOrder());

            // For Parent below fields set to null
            if (bi.getOrder() == 0) {
                rfqBqItem.setUom(null);
                rfqBqItem.setField1(null);
            } else {
                RfqSorItem parentBq = rfqSorItemDao.getParentbyLevelId(bqId, bi.getLevel());
                rfqBqItem.setParent(parentBq);

                if (bi.getUom() != null)
                    rfqBqItem.setUom(uomService.getUombyCode(bi.getUom().getUom(), tenantId));
                // if invalid UOM
                if (rfqBqItem.getUom() == null) {
                    throw new ExcelParseException(messageSource.getMessage("file.parse.uom.invalid.found", new Object[] { bi.getUom().getUom() }, Global.LOCALE), convFile.getName());
                }
                if (StringUtils.checkString(rfqBq.getField1Label()).length() > 0) {
                    if (Boolean.TRUE == rfqBq.getField1Required() && StringUtils.checkString(bi.getField1()).length() == 0) {
                        throw new ExcelParseException(rfqBq.getField1Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
                    }
                    rfqBqItem.setField1(bi.getField1());
                }
            }
            rfqSorItemDao.saveOrUpdate(rfqBqItem);
        }
        return rfqBqList.size();
    }

    protected void buildAddNewColumnsToUpload(Sor eventBq, List<String> columnsTitle) {
        if (columnsTitle.size() >= 1) {
            eventBq.setField1Label(columnsTitle.get(0) != null ? columnsTitle.get(0) : null);
            if (eventBq.getField1FilledBy() == null) {
                eventBq.setField1FilledBy(BqUserTypes.BUYER);
            }
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public RfqEventSor updateRfqSorFields(RfqEventSor eventBq) {

        rfqSorDao.update(eventBq);

        List<String> label = new ArrayList<>();
        if (eventBq.getField1FilledBy() == BqUserTypes.SUPPLIER) {
            label.add("field1");
        }
        rfqSorItemDao.resetFieldsForFilledBySupplier(eventBq.getId(), label);
        return eventBq;
    }

    @Override
    public RfqEventSor getRfqEventSorBySorId(String bqId) {
        RfqEventSor bq = rfqSorDao.getRfqEventSorBySorId(bqId);
        return bq;
    }

    @Override
    public void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception {
        rfqSorDao.isAllowtoDeleteBQ(id, eventType);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteRfqSor(String id) {
        rfqSorDao.deleteSor(id);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { Exception.class })
    public void deletefieldInSorItems(String bqId, String label) {
        rfqSorItemDao.deletefieldInSorItems(bqId, label);
        rfqSorDao.deletefieldInSor(bqId, label);
    }


    @Override
    @Transactional(readOnly = false)
    public void deleteAllSorItems(String bqId) throws NoSuchMessageException, NotAllowedException {
        RfqEventSor bq = rfqSorDao.findById(bqId);

        if (bq != null) {
            if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
                throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
            }
            rfqSorItemDao.deleteSorItemsbySorid(bq.getId());
        }
    }


    @Override
    @Transactional(readOnly = false)
    public void deleteSorItems(String[] bqItemIds, String bqId) throws NotAllowedException {
        LOG.info("SOR ITEM IDs :: " + Arrays.asList(bqItemIds));
        RfqEventSor bq =  rfqSorDao.findById(bqId);
        if (bq != null) {
            if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
                throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
            }

            for (String id : bqItemIds) {
                RfqSorItem parentItm = getSorItemsbySorId(id);
                if (parentItm != null && parentItm.getParent() == null) {
                    rfqSorItemDao.deleteChildItems(id, bqId);
                }
            }

            rfqSorItemDao.deleteRemaingSorItems(bqItemIds, bq.getId());
        }
    }

    @Override
    public List<String> getNotSectionAddedRfqSorIdsByEventId(String eventId) {
        return rfqSorDao.getNotSectionAddedRfqSorIdsByEventId(eventId);
    }

    @Override
    public List<String> getNotSectionItemAddedRfqSorIdsByEventId(String eventId) {
        return rfqSorDao.getNotSectionItemAddedRfqSorIdsByEventId(eventId);
    }
}
