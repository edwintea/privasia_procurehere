package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfiEnvelopDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfiSorDao;
import com.privasia.procurehere.core.dao.RfiSorItemDao;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.RfiSorItem;
import com.privasia.procurehere.core.entity.RftSorItem;
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
import com.privasia.procurehere.service.RfiSorService;
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
public class RfiSorServiceImpl implements RfiSorService {

    private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);


    @Autowired
    RfiSorDao rfiSorDao;

    @Autowired
    RfiSorItemDao rfiSorItemDao;

    @Autowired
    RfiEventDao rfiEventDao;

    @Autowired
    UomService uomService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    RfiEnvelopDao rfiEnvelopDao;

    @Override
    public List<RfiEventSor> getAllSorListByEventIdByOrder(String eventId) {
        return rfiSorDao.findSorsByEventIdByOrder(eventId);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateSor(RfiEventSor rfqEventSor) {
        rfiSorDao.update(rfqEventSor);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveRfiSor(RfiEventSor rfqEventSor) {
        rfiSorDao.saveOrUpdate(rfqEventSor);
    }

    @Override
    public List<Sor> findSorbyEventId(String eventId) {
        return rfiSorDao.findSorbyEventId(eventId);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public RfiEventSor getSorById(String id) {
        return rfiSorDao.findById(id);
    }

    @Override
    public void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception {
        rfiSorDao.isAllowtoDeleteBQ(id, eventType);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteRfiSor(String id) {
        rfiSorDao.deleteSor(id);
    }

    @Override
    public List<RfiSorItem> getSorItemForSearchFilter(String sorId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo) {
        List<RfiSorItem> returnList = new ArrayList<RfiSorItem>();
        if (pageNo != null) {
            start = pageNo - 1;
        }
        if (length != null) {
            start = start * length;
        }
        LOG.info(" start  : " + start);
        List<RfiSorItem> bqList = rfiSorItemDao.getSorItemForSearchFilter(sorId, itemLevel, itemOrder, searchVal, start, length);
        if (CollectionUtil.isNotEmpty(bqList)) {
            for (RfiSorItem item : bqList) {
                RfiSorItem bqItem = item.createSearchShallowCopy();
                returnList.add(bqItem);
            }
        }
        return returnList;
    }

    @Override
    public List<BqItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String searchVal) {
        return rfiSorItemDao.getAllLevelOrderSorItemBySorId(bqId, searchVal);
    }

    @Override
    public long totalSorItemCountByBqId(String bqId, String searchVal) {
        return rfiSorItemDao.totalSorItemCountByBqId(bqId, searchVal);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public RfiSorItem getSorItemsbySorId(String parent) {
        return rfiSorItemDao.findById(parent);
    }

    @Override
    public boolean isSorItemExists(RfiSorItem item, String bqId, String parentId) {
        return rfiSorItemDao.isExists(item, bqId, parentId);
    }

    @Override
    @Transactional(readOnly = false)
    public RfiSorItem saveSorItem(RfiSorItem rftBqItem) {
        if (rftBqItem.getParent() == null) {
            int itemLevel = 0;
            List<RfiSorItem> list = rfiSorItemDao.getSorItemLevelOrder(rftBqItem.getSor().getId());
            if (CollectionUtil.isNotEmpty(list)) {
                itemLevel = list.size();
            }
            LOG.info("ITEM LEVEL :: " + itemLevel);
            rftBqItem.setLevel(itemLevel + 1);
            rftBqItem.setOrder(0);
        } else {
            RfiSorItem parent = getSorItemsbySorId(rftBqItem.getParent().getId());
            if (rftBqItem.getParent() != null) {
                rftBqItem.setLevel(parent.getLevel());
                rftBqItem.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
            }
        }
        return rfiSorItemDao.saveOrUpdate(rftBqItem);
    }

    /**
     * @param rftBqItem
     */
    @Override
    @Transactional(readOnly = false)
    public void updateSorItem(RfiSorItem rftBqItem) {
        rfiSorItemDao.update(rftBqItem);
    }

    @Override
    public RfiEventSor getRfiEventSorBySorId(String bqId) {
        RfiEventSor bq = rfiSorDao.getRfiEventSorBySorId(bqId);
        return bq;
    }

    @Override
    public boolean isSorExists(SorPojo sorPojo) {
        RfiEventSor rfqEventSor = new RfiEventSor();
        rfqEventSor.setId(sorPojo.getId());
        rfqEventSor.setName(sorPojo.getSorName());
        return rfiSorDao.isExists(rfqEventSor, sorPojo.getEventId());
    }


    @Override
    public List<RfiSorItem> getAllSoritemsbySorId(String bqId) {
        return rfiSorItemDao.getAllSorItemsbysorId(bqId);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void reorderBqItems(BqItemPojo rftBqItemPojo) throws NotAllowedException {
        LOG.info("SOR ITEM Object :: " + rftBqItemPojo.toString());
        int newOrder = rftBqItemPojo.getOrder();
        RfiSorItem bqItem = getSorItemsbySorId(rftBqItemPojo.getId());

        if (CollectionUtil.isNotEmpty(bqItem.getChildren()) && rftBqItemPojo.getParent() != null) {
            throw new NotAllowedException("Schedule of Rate Item cannot be made a child if it has sub items");
        }

        LOG.info("DB SOR ITEM DETAILS ::" + bqItem);
        int oldOrder = bqItem.getOrder();
        LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
        int oldLevel = bqItem.getLevel();
        int newLevel = rftBqItemPojo.getOrder(); // this will be ignored if it is made a child
        RfiSorItem newParent = null;
        if (rftBqItemPojo.getParent() != null) {
            newParent = getSorItemsbySorId(rftBqItemPojo.getParent());
        }
        RfiSorItem oldParent = bqItem.getParent();

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
        rfiSorItemDao.updateItemOrder(bqItem, bqItem.getSor().getId(), (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = {ExcelParseException.class, Exception.class})
    public int uploadSorFile(String bqId, String eventId, File convFile, String tenantId) throws ExcelParseException {
        SorFileParser<RfiSorItem> rfqBi = new SorFileParser<RfiSorItem>(RfiSorItem.class);
        List<RfiSorItem> rfqBqList = rfqBi.parse(convFile);

        // Delete existing SOR Items by Sors.
        rfiSorItemDao.deleteSorItemsbySorid(bqId);

        // adding columns while uploading SORs
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
        for (RfiSorItem bi : rfqBqList) {
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
        RfiEventSor rfqBq = null;
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
            rfqBq = updateRfiSorFields(rfqBq);
        }

        int count = 1;
        List<String> columns = new ArrayList<String>();
        for (RfiSorItem bi : rfqBqList) {
            RfiSorItem rfqBqItem = new RfiSorItem();
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
            RfiEvent event = rfiEventDao.findByEventId(eventId);
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
                RfiSorItem parentBq = rfiSorItemDao.getParentbyLevelId(bqId, bi.getLevel());
                rfqBqItem.setParent(parentBq);

                if (bi.getUom() != null)
                    rfqBqItem.setUom(uomService.getUombyCode(bi.getUom().getUom(), tenantId));
                // if invalid UOM
                if (rfqBqItem.getUom() == null) {
                    throw new ExcelParseException(messageSource.getMessage("file.parse.uom.invalid.found", new Object[]{bi.getUom().getUom()}, Global.LOCALE), convFile.getName());
                }
                if (StringUtils.checkString(rfqBq.getField1Label()).length() > 0) {
                    if (Boolean.TRUE == rfqBq.getField1Required() && StringUtils.checkString(bi.getField1()).length() == 0) {
                        throw new ExcelParseException(rfqBq.getField1Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
                    }
                    rfqBqItem.setField1(bi.getField1());
                }
            }
            rfiSorItemDao.saveOrUpdate(rfqBqItem);
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
    public RfiEventSor updateRfiSorFields(RfiEventSor eventBq) {

        rfiSorDao.update(eventBq);

        List<String> label = new ArrayList<>();
        if (eventBq.getField1FilledBy() == BqUserTypes.SUPPLIER) {
            label.add("field1");
        }
        rfiSorItemDao.resetFieldsForFilledBySupplier(eventBq.getId(), label);
        return eventBq;
    }

    @Override
    public List<RfiEventSor> getEventSorForEventIdForEnvelop(String id, List<String> bqIds) {
        return rfiSorDao.findSorsByEventIdForEnvelop(id, bqIds);
    }

    @Override
    public List<RfiEventSor> getNotAssignedSorIdsByEventId(String eventId) {
        return rfiEnvelopDao.getNotAssignedSorIdsByEventId(eventId);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { Exception.class })
    public void deletefieldInSorItems(String bqId, String label) {
        rfiSorItemDao.deletefieldInSorItems(bqId, label);
        rfiSorDao.deletefieldInSor(bqId, label);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAllSorItems(String bqId) throws NoSuchMessageException, NotAllowedException {
        RfiEventSor bq = rfiSorDao.findById(bqId);

        if (bq != null) {
            if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
                throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
            }
            rfiSorItemDao.deleteSorItemsbySorid(bq.getId());
        }
    }


    @Override
    @Transactional(readOnly = false)
    public void deleteSorItems(String[] bqItemIds, String bqId) throws NotAllowedException {
        LOG.info("SOR ITEM IDs :: " + Arrays.asList(bqItemIds));
        RfiEventSor bq =  rfiSorDao.findById(bqId);
        if (bq != null) {
            if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
                throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
            }

            for (String id : bqItemIds) {
                RfiSorItem parentItm = getSorItemsbySorId(id);
                if (parentItm != null && parentItm.getParent() == null) {
                    rfiSorItemDao.deleteChildItems(id, bqId);
                }
            }

            rfiSorItemDao.deleteRemaingSorItems(bqItemIds, bq.getId());
        }
    }

    @Override
    public Integer getCountOfSorByEventId(String eventId) {
        return rfiSorDao.getCountOfSorByEventId(eventId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAllSor(String eventId, String eventRequirement) {
        // Will implement this one after the envelope issue one
        rfiEnvelopDao.removeSorsFromEnvelops(eventId);
        rfiSorItemDao.deleteSorItemsForEventId(eventId);
        rfiSorDao.deleteFromEnvSor(eventId, RfxTypes.RFI);
        rfiSorDao.deleteSorForEventId(eventId);

        RfiEvent event = rfiEventDao.findById(eventId);
        event.setSorCompleted(Boolean.FALSE);
        event.setScheduleOfRate(Boolean.FALSE);
        rfiEventDao.update(event);
    }

    @Override
    public List<String> getNotSectionAddedRfiSorIdsByEventId(String eventId) {
        return rfiSorDao.getNotSectionAddedRfiSorIdsByEventId(eventId);
    }

    @Override
    public List<String> getNotSectionItemAddedRfiSorIdsByEventId(String eventId) {
        return rfiSorDao.getNotSectionItemAddedRfiSorIdsByEventId(eventId);
    }
}
