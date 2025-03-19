package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfpEnvelopDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpSorDao;
import com.privasia.procurehere.core.dao.RfpSorItemDao;
import com.privasia.procurehere.core.entity.*;
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
import com.privasia.procurehere.service.RfpSorService;
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
public class RfpSorServiceImpl implements RfpSorService {

    private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

    @Autowired
    RfpSorDao rfpSorDao;

    @Autowired
    RfpSorItemDao rfpSorItemDao;

    @Autowired
    RfpEventDao rfpEventDao;

    @Autowired
    UomService uomService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    RfpEnvelopDao rfpEnvelopDao;


    @Override
    public List<RfpEventSor> getAllSorListByEventIdByOrder(String eventId) {
        return rfpSorDao.findSorsByEventIdByOrder(eventId);
    }


    @Override
    public boolean isSorExists(SorPojo sorPojo) {
        RfpEventSor rfpEventSor = new RfpEventSor();
        rfpEventSor.setId(sorPojo.getId());
        rfpEventSor.setName(sorPojo.getSorName());
        return rfpSorDao.isExists(rfpEventSor, sorPojo.getEventId());
    }

    @Override
    @Transactional(readOnly = false)
    public void updateSor(RfpEventSor rfqEventSor) {
        rfpSorDao.update(rfqEventSor);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveRfpSor(RfpEventSor rfqEventSor) {
        rfpSorDao.saveOrUpdate(rfqEventSor);
    }

    @Override
    public List<Sor> findSorbyEventId(String eventId) {
        return rfpSorDao.findSorbyEventId(eventId);
    }

    @Override
    public List<RfpSorItem> getSorItemForSearchFilter(String sorId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo) {
        List<RfpSorItem> returnList = new ArrayList<RfpSorItem>();
        if (pageNo != null) {
            start = pageNo - 1;
        }
        if (length != null) {
            start = start * length;
        }
        LOG.info(" start  : " + start);
        List<RfpSorItem> bqList = rfpSorItemDao.getSorItemForSearchFilter(sorId, itemLevel, itemOrder, searchVal, start, length);
        if (CollectionUtil.isNotEmpty(bqList)) {
            for (RfpSorItem item : bqList) {
                RfpSorItem bqItem = item.createSearchShallowCopy();
                returnList.add(bqItem);
            }
        }
        return returnList;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public RfpEventSor getSorById(String id) {
        return rfpSorDao.findById(id);
    }

    @Override
    public long totalSorItemCountByBqId(String bqId, String searchVal) {
        return rfpSorItemDao.totalSorItemCountByBqId(bqId, searchVal);
    }

    @Override
    public List<BqItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String searchVal) {
        return rfpSorItemDao.getAllLevelOrderSorItemBySorId(bqId, searchVal);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public RfpSorItem getSorItemsbySorId(String parent) {
        return rfpSorItemDao.findById(parent);
    }

    @Override
    public boolean isSorItemExists(RfpSorItem item, String bqId, String parentId) {
        return rfpSorItemDao.isExists(item, bqId, parentId);
    }

    @Override
    @Transactional(readOnly = false)
    public RfpSorItem saveSorItem(RfpSorItem rftBqItem) {
        if (rftBqItem.getParent() == null) {
            int itemLevel = 0;
            List<RfpSorItem> list = rfpSorItemDao.getSorItemLevelOrder(rftBqItem.getSor().getId());
            if (CollectionUtil.isNotEmpty(list)) {
                itemLevel = list.size();
            }
            LOG.info("ITEM LEVEL :: " + itemLevel);
            rftBqItem.setLevel(itemLevel + 1);
            rftBqItem.setOrder(0);
        } else {
            RfpSorItem parent = getSorItemsbySorId(rftBqItem.getParent().getId());
            if (rftBqItem.getParent() != null) {
                rftBqItem.setLevel(parent.getLevel());
                rftBqItem.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
            }
        }
        return rfpSorItemDao.saveOrUpdate(rftBqItem);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateSorItem(RfpSorItem rftBqItem) {
        rfpSorItemDao.update(rftBqItem);
    }

    @Override
    public List<RfpSorItem> getAllSoritemsbySorId(String bqId) {
        return rfpSorItemDao.getAllSorItemsbysorId(bqId);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { ExcelParseException.class, Exception.class })
    public int uploadSorFile(String bqId, String eventId, File convFile, String tenantId) throws ExcelParseException {
        SorFileParser<RfpSorItem> rfqBi = new SorFileParser<RfpSorItem>(RfpSorItem.class);
        List<RfpSorItem> rfqBqList = rfqBi.parse(convFile);

        // Delete existing SOR Items by Sors.
        rfpSorItemDao.deleteSorItemsbySorid(bqId);

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
        for (RfpSorItem bi : rfqBqList) {
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
        RfpEventSor rfqBq = null;
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
            rfqBq = updateRfpSorFields(rfqBq);
        }

        int count = 1;
        List<String> columns = new ArrayList<String>();
        for (RfpSorItem bi : rfqBqList) {
            RfpSorItem rfqBqItem = new RfpSorItem();
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
            RfpEvent event = rfpEventDao.findEventForBqByEventId(eventId);
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
                RfpSorItem parentBq = rfpSorItemDao.getParentbyLevelId(bqId, bi.getLevel());
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
            rfpSorItemDao.saveOrUpdate(rfqBqItem);
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
    public RfpEventSor updateRfpSorFields(RfpEventSor eventBq) {

        rfpSorDao.update(eventBq);

        List<String> label = new ArrayList<>();
        if (eventBq.getField1FilledBy() == BqUserTypes.SUPPLIER) {
            label.add("field1");
        }

        rfpSorItemDao.resetFieldsForFilledBySupplier(eventBq.getId(), label);
        return eventBq;
    }

    @Override
    public RfpEventSor getRfpEventSorBySorId(String bqId) {
        RfpEventSor bq = rfpSorDao.getRfpEventSorBySorId(bqId);
        return bq;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void reorderBqItems(BqItemPojo rftBqItemPojo) throws NotAllowedException {
        LOG.info("SOR ITEM Object :: " + rftBqItemPojo.toString());
        int newOrder = rftBqItemPojo.getOrder();
        RfpSorItem bqItem = getSorItemsbySorId(rftBqItemPojo.getId());

        if (CollectionUtil.isNotEmpty(bqItem.getChildren()) && rftBqItemPojo.getParent() != null) {
            throw new NotAllowedException("Schedule of Rate Item cannot be made a child if it has sub items");
        }

        LOG.info("DB SOR ITEM DETAILS ::" + bqItem);
        int oldOrder = bqItem.getOrder();
        LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
        int oldLevel = bqItem.getLevel();
        int newLevel = rftBqItemPojo.getOrder(); // this will be ignored if it is made a child
        RfpSorItem newParent = null;
        if (rftBqItemPojo.getParent() != null) {
            newParent = getSorItemsbySorId(rftBqItemPojo.getParent());
        }
        RfpSorItem oldParent = bqItem.getParent();

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
        rfpSorItemDao.updateItemOrder(bqItem, bqItem.getSor().getId(), (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);
    }

    @Override
    public List<RfpEventSor> getNotAssignedSorIdsByEventId(String eventId) {
        return rfpEnvelopDao.getNotAssignedSorIdsByEventId(eventId);
    }

    @Override
    public List<RfpEventSor> getEventSorForEventIdForEnvelop(String id, List<String> bqIds) {
        return rfpSorDao.findSorsByEventIdForEnvelop(id, bqIds);
    }

    @Override
    public Integer getCountOfSorByEventId(String eventId) {
        return rfpSorDao.getCountOfSorByEventId(eventId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAllSor(String eventId, String eventRequirement) {
        // Will implement this one after the envelope issue one
        rfpEnvelopDao.removeSorsFromEnvelops(eventId);
        rfpSorItemDao.deleteSorItemsForEventId(eventId);
        rfpSorDao.deleteSorForEventId(eventId);

        RfpEvent event = rfpEventDao.findById(eventId);
        event.setBqCompleted(Boolean.FALSE);
        event.setBillOfQuantity(Boolean.FALSE);
        rfpEventDao.update(event);
    }

    @Override
    public void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception {
        rfpSorDao.isAllowtoDeleteBQ(id, eventType);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteRfpSor(String id) {
        rfpSorDao.deleteSor(id);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { Exception.class })
    public void deletefieldInSorItems(String bqId, String label) {
        rfpSorItemDao.deletefieldInSorItems(bqId, label);
        rfpSorDao.deletefieldInSor(bqId, label);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAllSorItems(String bqId) throws NoSuchMessageException, NotAllowedException {
        RfpEventSor bq = rfpSorDao.findById(bqId);

        if (bq != null) {
            if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
                throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
            }
            rfpSorItemDao.deleteSorItemsbySorid(bq.getId());
        }
    }


    @Override
    @Transactional(readOnly = false)
    public void deleteSorItems(String[] bqItemIds, String bqId) throws NotAllowedException {
        LOG.info("SOR ITEM IDs :: " + Arrays.asList(bqItemIds));
        RfpEventSor bq =  rfpSorDao.findById(bqId);
        if (bq != null) {
            if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
                throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
            }

            for (String id : bqItemIds) {
                RfpSorItem parentItm = getSorItemsbySorId(id);
                if (parentItm != null && parentItm.getParent() == null) {
                    rfpSorItemDao.deleteChildItems(id, bqId);
                }
            }

            rfpSorItemDao.deleteRemaingSorItems(bqItemIds, bq.getId());
        }
    }

    @Override
    public List<String> getNotSectionAddedRfpSorIdsByEventId(String eventId) {
        return rfpSorDao.getNotSectionAddedRfpSorIdsByEventId(eventId);
    }

    @Override
    public List<String> getNotSectionItemAddedRfpSorIdsByEventId(String eventId) {
        return rfpSorDao.getNotSectionItemAddedRfpSorIdsByEventId(eventId);
    }
}
