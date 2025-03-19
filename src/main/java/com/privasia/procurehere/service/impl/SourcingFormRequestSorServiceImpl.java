package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestSorDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestSorItemDao;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import com.privasia.procurehere.core.entity.SourcingFormRequestSorItem;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.parsers.RFSSorFileParser;
import com.privasia.procurehere.core.pojo.SourcingReqSorPojo;
import com.privasia.procurehere.core.pojo.SourcingSorItemPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.SourcingFormRequestSorService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class SourcingFormRequestSorServiceImpl implements SourcingFormRequestSorService {

    private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);


    @Autowired
    SourcingFormRequestSorDao sourcingFormRequestSorDao;

    @Autowired
    SourcingFormRequestService sourcingFormRequestService;

    @Autowired
    SourcingFormRequestDao sourcingFormRequestDao;

    @Autowired
    SourcingFormRequestSorItemDao sourcingFormRequestSorItemDao;

    @Autowired
    MessageSource messageSource;

    @Autowired
    UomService uomService;

    @Override
    public List<SourcingFormRequestSor> findSorByFormIdByOrder(String formId) {
        List<SourcingFormRequestSor> list = sourcingFormRequestSorDao.findSorByFormIdByOrder(formId);
        if (CollectionUtil.isNotEmpty(list)) {
            for (SourcingFormRequestSor sourcingFormRequestBq : list) {
                if (CollectionUtil.isNotEmpty(sourcingFormRequestBq.getSorItems())) {
                    for (SourcingFormRequestSorItem item : sourcingFormRequestBq.getSorItems()) {
                        item.getParent();
                        item.getChildren();
                    }
                }
            }
        }
        return list;
    }

    @Override
    public boolean isSorExists(String formId, String bqId, String name) {
        return sourcingFormRequestSorDao.isSorExists(formId, bqId, name);
    }


    @Override
    @Transactional(readOnly = false)
    public SourcingFormRequestSor saveSourcingSor(SourcingReqSorPojo sourcingReqBqPojo) {
        List<SourcingFormRequestSor> bqList = findSorByFormIdByOrder(sourcingReqBqPojo.getFormId());
        Integer count = 1;
        if (CollectionUtil.isNotEmpty(bqList)) {
            for (SourcingFormRequestSor bq : bqList) {
                if (bq.getSorOrder() == null) {
                    bq.setSorOrder(count);
                    sourcingFormRequestSorDao.update(bq);
                    count++;
                }
            }
            count = bqList.size();
            count++;
        }
        SourcingFormRequestSor bq = new SourcingFormRequestSor();
        bq.setSourcingFormRequest(sourcingFormRequestService.getSourcingRequestById(sourcingReqBqPojo.getFormId()));
        bq.setId(sourcingReqBqPojo.getId());
        bq.setName(sourcingReqBqPojo.getSorName());
        bq.setDescription(sourcingReqBqPojo.getSorDesc());
        bq.setCreatedDate(new Date());
        bq.setSorOrder(count);
        return sourcingFormRequestSorDao.saveOrUpdate(bq);
    }

    @Override
    public SourcingFormRequestSor getSorById(String sorId) {
        return sourcingFormRequestSorDao.findById(sorId);
    }


    @Override
    @Transactional(readOnly = false)
    public SourcingFormRequestSor updateSourcingSor(SourcingFormRequestSor bq) {
        return sourcingFormRequestSorDao.update(bq);
    }


    @Override
    public SourcingFormRequest getSourcingRequestSorByFormId(String formId) {
        SourcingFormRequest sourcingFormRequest = sourcingFormRequestDao.getSourcingSorByFormId(formId);
        if (sourcingFormRequest.getFormOwner() != null) {
            sourcingFormRequest.getBusinessUnit();
            sourcingFormRequest.getFormOwner().getName();
            sourcingFormRequest.getFormOwner().getCommunicationEmail();
            sourcingFormRequest.getFormOwner().getPhoneNumber();
            if (sourcingFormRequest.getFormOwner().getOwner() != null) {
                Owner usr = sourcingFormRequest.getFormOwner().getOwner();
                usr.getCompanyContactNumber();
            }
        }
        return sourcingFormRequest;
    }


    @Override
    public SourcingFormRequestSor findSorById(String bqId) {
        return sourcingFormRequestSorDao.findById(bqId);
    }


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
    @Override
    public List<SourcingFormRequestSorItem> getSorItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder,
                                                                      String searchVal, Integer start, Integer length, Integer pageNo) {
        List<SourcingFormRequestSorItem> returnList = new ArrayList<SourcingFormRequestSorItem>();
        if (pageNo != null) {
            start = pageNo - 1;
        }
        if (length != null) {
            start = start * length;
        }
        List<SourcingFormRequestSorItem> bqList = sourcingFormRequestSorItemDao.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length);
        if (CollectionUtil.isNotEmpty(bqList)) {
            for (SourcingFormRequestSorItem item : bqList) {
                SourcingFormRequestSorItem bqItem = item.createSearchShallowCopy();
                returnList.add(bqItem);
            }
        }
        return returnList;
    }


    @Override
    public List<SourcingSorItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String searchVal) {
        return sourcingFormRequestSorItemDao.getAllLevelOrderSorItemBySorId(bqId, searchVal);
    }


    @Override
    public long getTotalSortemCountBySorId(String bqId, String searchVal) {
        return sourcingFormRequestSorItemDao.getTotalSorItemCountBySorId(bqId, searchVal);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteSourcingSor(String id) {
        sourcingFormRequestSorDao.deleteSor(id);
    }

    @Override
    public SourcingFormRequestSorItem getSorItemsbyBqId(String bqParent) {
        return sourcingFormRequestSorItemDao.findById(bqParent);
    }

    @Override
    public boolean isSorItemExists(SourcingFormRequestSorItem sourcingBqItem, String bq, String parent) {
        return sourcingFormRequestSorItemDao.isExist(sourcingBqItem, bq, parent);
    }

    @Override
    @Transactional(readOnly = false)
    public SourcingFormRequestSorItem saveSourcingSorItem(SourcingFormRequestSorItem sourcingBqItem) {
        if (sourcingBqItem.getLevel() == 0) {
            if (sourcingBqItem.getParent() == null) {
                int itemLevel = 0;
                List<SourcingFormRequestSorItem> list = sourcingFormRequestSorItemDao.getSorItemLevelOrder(sourcingBqItem.getSor().getId());
                if (CollectionUtil.isNotEmpty(list)) {
                    itemLevel = list.size();
                }
                LOG.info("ITEM LEVEL :: " + itemLevel);
                sourcingBqItem.setLevel(itemLevel + 1);
                sourcingBqItem.setOrder(0);
            } else {
                SourcingFormRequestSorItem parent = sourcingFormRequestSorItemDao.findById(sourcingBqItem.getParent().getId());
                if (sourcingBqItem.getParent() != null) {
                    sourcingBqItem.setLevel(parent.getLevel());
                    sourcingBqItem.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
                }
            }
        }
        return sourcingFormRequestSorItemDao.saveOrUpdate(sourcingBqItem);
    }

    @Override
    @Transactional(readOnly = false)
    public SourcingFormRequestSorItem updateSourcingSorItem(SourcingFormRequestSorItem sourcingReqBqItem) {
        return sourcingFormRequestSorItemDao.update(sourcingReqBqItem);
    }


    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void reorderSorItems(SourcingSorItemPojo sourcingBqItemPojo) throws NotAllowedException {
        int newOrder = sourcingBqItemPojo.getOrder();
        SourcingFormRequestSorItem bqItem = getSorItemsbyBqId(sourcingBqItemPojo.getId());

        if (CollectionUtil.isNotEmpty(bqItem.getChildren()) && sourcingBqItemPojo.getParent() != null) {
            throw new NotAllowedException("Schedule of Rate Item cannot be made a child if it has sub items");
        }

        int oldOrder = bqItem.getOrder();
        LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
        int oldLevel = bqItem.getLevel();
        int newLevel = sourcingBqItemPojo.getOrder(); // this will be ignored if
        // it is made a child
        SourcingFormRequestSorItem newParent = null;
        if (sourcingBqItemPojo.getParent() != null && StringUtils.checkString(sourcingBqItemPojo.getParent()).length() > 0) {
            newParent = getSorItemsbyBqId(sourcingBqItemPojo.getParent());
        }
        SourcingFormRequestSorItem oldParent = bqItem.getParent();

        // If these are not child, their order should be reset to 0
        if (oldParent == null) {
            oldOrder = 0;
        }
        if (newParent == null) {
            newOrder = 0;
        }

        // Update it to new position.
        bqItem.setOrder(newOrder);
        bqItem.setLevel(newParent == null ? sourcingBqItemPojo.getOrder() : 0);
        bqItem.setParent(newParent);
        sourcingFormRequestSorItemDao.updateItemOrder(bqItem, bqItem.getSor().getId(), (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);

    }


    @Override
    public SourcingFormRequestSor getAllsorItemsBySorId(String id) {
        return sourcingFormRequestSorItemDao.getAllsorItemsBySorId(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteSourcingSorItems(String[] bqItemsIds, String bqId) throws NotAllowedException {
        SourcingFormRequestSor bq = sourcingFormRequestSorDao.findById(bqId);
        if (bq != null) {
            if (SourcingFormStatus.DRAFT != bq.getSourcingFormRequest().getStatus()) {
                throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
            }
            bqId = sourcingFormRequestSorItemDao.deleteSorItems(bqItemsIds, bq.getId());
        }
    }


    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { ExcelParseException.class, Exception.class })
    public int uploadSorFile(String bqId, String rfsId, File convFile, String tenantId) throws ExcelParseException {
        RFSSorFileParser<SourcingFormRequestSorItem> rfaBi = new RFSSorFileParser<SourcingFormRequestSorItem>(SourcingFormRequestSorItem.class);
        List<SourcingFormRequestSorItem> rfaBqList = rfaBi.parse(convFile);

        Workbook workbook = null;
        Sheet sheet = null;
        try {
            workbook = WorkbookFactory.create(convFile);
            sheet = workbook.getSheetAt(0);
        } catch (InvalidFormatException | IOException e) {
            LOG.info("Error while uploading RFS SOR : " + e.getMessage(), e);
        }

        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (SourcingFormRequestSorItem bi : rfaBqList) {
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

        SourcingFormRequestSor rfsBq = null;
        if (sheet != null) {
            rfsBq = sourcingFormRequestSorDao.findById(bqId);
            int startingRow = 0;
            List<String> columnsTitle = new ArrayList<String>();
            Row rowData = sheet.getRow(startingRow);
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
            buildAddNewColumnsToUpload(rfsBq, columnsTitle);
            rfsBq = sourcingFormRequestSorDao.update(rfsBq);
        }

        // Delete existing SOR Items by Bqs.
        sourcingFormRequestSorItemDao.deleteSorItemsbySorid(bqId);
        int count = 1;
        List<String> columns = new ArrayList<String>();

        // If successfully Deleted then only inserting records into DB.
        for (SourcingFormRequestSorItem bi : rfaBqList) {
            SourcingFormRequestSorItem rfaBqItem = new SourcingFormRequestSorItem();

            // checking Extra column in Excel match with bq columns
            if (count == 1 && CollectionUtil.isNotEmpty(bi.getColumnTitles())) {

                if (StringUtils.checkString(rfsBq.getField1Label()).length() > 0) {
                    columns.add(rfsBq.getField1Label());
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
            rfaBqItem.setSor(rfsBq);
            rfaBqItem.setSourcingFormRequest(sourcingFormRequestDao.findById(rfsId));

            if (bi.getItemName().length() > 250) {
                throw new ExcelParseException("Item Name length must be between 1 to 250. ", convFile.getName());
            }
            rfaBqItem.setItemName(bi.getItemName());
            if (bi.getItemDescription().length() > 2000) {
                throw new ExcelParseException("Item Description length must be between 0 to 2000. ", convFile.getName());
            }
            rfaBqItem.setItemDescription(bi.getItemDescription());
            rfaBqItem.setLevel(bi.getLevel());
            rfaBqItem.setOrder(bi.getOrder());

            // For Parent below fields set to null
            if (bi.getOrder() == 0) {
                rfaBqItem.setUom(null);
                rfaBqItem.setField1(null);
            } else {
                SourcingFormRequestSorItem parentBq = sourcingFormRequestSorItemDao.getParentbyLevelId(bqId, bi.getLevel());
                rfaBqItem.setParent(parentBq);

                if (bi.getUom() != null) {
                    rfaBqItem.setUom(uomService.getUombyCode(bi.getUom().getUom(), tenantId));
                }
                // if invalid UOM
                if (rfaBqItem.getUom() == null) {
                    throw new ExcelParseException(messageSource.getMessage("file.parse.uom.invalid.found", new Object[] { bi.getUom().getUom() }, Global.LOCALE), convFile.getName());
                }
                if (StringUtils.checkString(rfsBq.getField1Label()).length() > 0) {
                    rfaBqItem.setField1(bi.getField1());
                }
            }
            sourcingFormRequestSorItemDao.save(rfaBqItem);
        }
        return rfaBqList.size();
    }

    protected void buildAddNewColumnsToUpload(SourcingFormRequestSor rfsBq, List<String> columnsTitle) {
        if (columnsTitle.size() >= 1) {
            rfsBq.setField1Label(columnsTitle.get(0) != null ? columnsTitle.get(0) : null);
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { Exception.class })
    public void deletefieldInSorItems(String bqId, String label) {
        sourcingFormRequestSorItemDao.deletefieldInSorItems(bqId, label);
        sourcingFormRequestSorDao.deletefieldInSor(bqId, label);
    }
}
