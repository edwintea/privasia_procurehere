package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaSorItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierSorDao;
import com.privasia.procurehere.core.dao.RfaSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.core.entity.RfaSorEvaluationComments;
import com.privasia.procurehere.core.entity.RfaSorItem;
import com.privasia.procurehere.core.entity.RfaSupplierSor;
import com.privasia.procurehere.core.entity.RfaSupplierSorItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.pojo.BqNonPriceComprision;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.pojo.SorItemPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.MaskUtils;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaSupplierSorItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class RfaSupplierSorItemServiceImpl implements RfaSupplierSorItemService {

    private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

    @Autowired
    RfaSupplierSorItemDao supplierSorItemDao;

    @Autowired
    RfaSupplierSorDao supplierSorDao;


    @Autowired
    RfaEventDao eventDao;

    @Autowired
    RfaEventSupplierDao eventSupplierDao;

    @Autowired
    RfaEnvelopDao envelopDao;

    @Autowired
    RfaSorItemDao sorItemDao;

    @Override
    public List<SorItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String supplierId, String searchVal) {
        return supplierSorItemDao.getAllLevelOrderSorItemBySorId(bqId, supplierId, searchVal);
    }


    @Override
    public List<?> getSorItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer pageNo, Integer pageLength, Integer itemLevel, Integer itemOrder) {
        List<RfaSupplierSorItem> returnList = new ArrayList<RfaSupplierSorItem>();
        Integer start = null;
        if (pageNo != null) {
            start = pageNo - 1;
        }
        if (pageLength != null) {
            start = start * pageLength;
        }
        // LOG.info(" start : "+start);
        List<RfaSupplierSorItem> bqList = supplierSorItemDao.getSorItemForSearchFilterForSupplier(eventBqId, supplierId, searchVal, start, pageLength, itemLevel, itemOrder);
        if (CollectionUtil.isNotEmpty(bqList)) {
            for (RfaSupplierSorItem item : bqList) {
                RfaSupplierSorItem bqItem = item.createSearchShallowCopy();
                returnList.add(bqItem);
            }
        }
        return returnList;
    }

    @Override
    public long totalSorItemCountBySorId(String eventBqId, String supplierId, String searchVal) {
        return supplierSorItemDao.totalSorItemCountBySorId(eventBqId, supplierId, searchVal);
    }


    @Override
    @Transactional(readOnly = false)
    public List<RfaSupplierSorItem> saveSupplierEventSor(String bqId) {
        List<RfaSupplierSorItem> rftSupplierBqItem = new ArrayList<>();
        List<RfaSorItem> rftEventBq = supplierSorItemDao.getSorItemsbyId(bqId);
        for (RfaSorItem item : rftEventBq) {
            RfaEventSor bq = item.getSor();
            RfaSupplierSor supplierBq = supplierSorDao.findSorByEventIdAndSorName(bq.getRfxEvent().getId(), bq.getId(), SecurityLibrary.getLoggedInUserTenantId());
            if (supplierBq == null) {
                supplierBq = new RfaSupplierSor(item.getSor());
                RfaEvent rfxEvent = eventDao.findByEventId(bq.getRfxEvent().getId());
                supplierBq.setEvent(rfxEvent);
                supplierBq.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
                supplierBq = supplierSorDao.saveOrUpdate(supplierBq);
            }
            RfaSupplierSorItem supplierBqItem = new RfaSupplierSorItem(item, SecurityLibrary.getLoggedInUser().getSupplier(), supplierBq);
            RfaSupplierSorItem obj = supplierSorItemDao.saveOrUpdate(supplierBqItem);
            if (CollectionUtil.isNotEmpty(supplierBqItem.getChildren())) {
                for (RfaSupplierSorItem child : supplierBqItem.getChildren()) {
                    child.setParent(obj);
                    supplierSorItemDao.saveOrUpdate(child);
                }
            }
            rftSupplierBqItem.add(supplierBqItem.createShallowCopy());
        }
        return rftSupplierBqItem;
    }


    @Override
    public List<EventEvaluationPojo> getSorEvaluationData(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser, String withOrWithoutTax, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length) {
        List<EventEvaluationPojo> returnList = new ArrayList<>();
        List<Supplier> suppliers = null;
        if (CollectionUtil.isEmpty(selectedSuppliers)) {
            suppliers = eventSupplierDao.getEventSuppliersForEvaluation(eventId);
        } else {
            suppliers = eventSupplierDao.getEventSuppliersForEvaluation(eventId, selectedSuppliers);
        }

        if (CollectionUtil.isNotEmpty(suppliers)) {
            RfaEvent event = eventDao.findById(eventId);

            List<String> columns = new ArrayList<String>();
            for (Supplier supplier : suppliers) {
                columns.add(supplier.getCompanyName());
            }
            List<String> envelopIds = new ArrayList<String>();
            envelopIds.add(envelopId);
            List<SorPojo> bqPojo = envelopDao.getSorsIdListByEnvelopIdByOrder(envelopIds);

            RfaEnvelop envelop = envelopDao.findById(envelopId);
            if (envelop.getRfxEvent() != null) {
                if (envelop.getRfxEvent().getViewSupplerName() != null && !envelop.getRfxEvent().getViewSupplerName() && !envelop.getRfxEvent().getDisableMasking()) {
                    for (Supplier supplier : suppliers) {
                        supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
                    }
                }
            }
            int sorItemlistSize = 0;
            List<String> levelOrderList = new ArrayList<>();
            for (SorPojo bqObj : bqPojo) {
                EventEvaluationPojo response = new EventEvaluationPojo();
                LinkedList<List<String>> data = new LinkedList<List<String>>();
                List<RfaSorItem> sorItems = sorItemDao.getSorItemForSearchFilter(bqObj.getId(), itemLevel, itemOrder, searchVal, start, length);

                List<RfaSorItem> sorItemlevelOrders = sorItemDao.getSorItemForSearchFilter(bqObj.getId(), null, null, null, null, null);
                LOG.info("sorItemlistSize :" + sorItemlistSize);
                if (CollectionUtil.isNotEmpty(sorItemlevelOrders) && sorItemlevelOrders.size() > sorItemlistSize) {
                    levelOrderList = new ArrayList<>();
                }
                for (RfaSorItem sorItemLevelOrder : sorItemlevelOrders) {
                    if (CollectionUtil.isNotEmpty(sorItemlevelOrders) && sorItemlevelOrders.size() > sorItemlistSize) {
                        levelOrderList.add(sorItemLevelOrder.getLevel() + "." + sorItemLevelOrder.getOrder());

                        LOG.info("TotalAmount :--->" + sorItemLevelOrder.getTotalAmount());
                    }
                }

                if (CollectionUtil.isNotEmpty(sorItemlevelOrders) && sorItemlevelOrders.size() > sorItemlistSize) {
                    sorItemlistSize = sorItemlevelOrders.size();
                }

                Map<String, BigDecimal> map = new LinkedHashMap<String, BigDecimal>();
                Integer j = 1;
                Integer k = 0;
                Set<String> set = new HashSet<>();
                List<String> supplierRemarks = new ArrayList<>();
                for (RfaSorItem sorItem : sorItems) {
                    boolean commentsExist = false;
                    List<String> answers = new ArrayList<String>();
                    List<String> sectionTotalRow = new ArrayList<String>();
                    sectionTotalRow.add("");
                    sectionTotalRow.add("");
                    sectionTotalRow.add("");
                    sectionTotalRow.add("Section Sub total");
                    sectionTotalRow.add("");
                    response.setItemId(sorItem.getId());
                    response.setName(bqObj.getSorName());
                    answers.add(sorItem.getLevel() + "." + sorItem.getOrder());
                    answers.add(sorItem.getItemName());
                    answers.add(sorItem.getUom() != null ? sorItem.getUom().getUom() : "");
                    answers.add("");

                    List<RfaSupplierSorItem> responseList = supplierSorItemDao.findSupplierSorItemsBySorItemIdAndEventId(sorItem.getId(), eventId, suppliers);

                    for (RfaSupplierSorItem item : responseList) {

                        if(!set.contains(item.getSupplier().getId())) {
                            set.add(item.getSupplier().getId());
                            supplierRemarks.add(item.getSupplierBq().getRemark());
                        }

                        commentsExist = false;
                        if (item.getSorItem().getEvaluationComments() != null && item.getSorItem().getEvaluationComments().size() > 0) {
                            for (RfaSorEvaluationComments com : item.getSorItem().getEvaluationComments()) {
                                if (((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId()))) {
                                    commentsExist = true;
                                    continue;
                                }
                            }
                        }

                        if (item.getTotalAmount() != null) {
                            BigDecimal amount = new BigDecimal(String.valueOf(item.getTotalAmount())).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.DOWN);
                            map.put(item.getSupplier().getId() + "-" + item.getLevel(), null);

                            answers.add(commentsExist + "-" + item.getSupplier().getId() + "-" + item.getSorItem().getId() + "-" + amount);
                        } else {
                            answers.add("");
                        }
                    }

                    data.add(answers);
                }
                response.setData(data);
                response.setColumns(suppliers);
                response.setSupplierRemarks(supplierRemarks);

                response.setEnvelopId(envelopId);
                response.setEventId(eventId);
                response.setLevelOrderList(levelOrderList);
                returnList.add(response);
            }
            if (CollectionUtil.isNotEmpty(levelOrderList)) {
                LOG.info("levelOrderList :" + levelOrderList);
            }
        }
        return returnList;

    }

    @Override
    public List<RfaSupplierSorItem> getAllSupplierSorItemBySorIdAndSupplierId(String bqId, String supplierId) {
        List<RfaSupplierSorItem> returnList = new ArrayList<RfaSupplierSorItem>();
        List<RfaSupplierSorItem> list = supplierSorItemDao.findSupplierSorItemByListBySorIdAndSupplierId(bqId, supplierId);
        LOG.info("ITEM LIST SIZE : " + list.size());
        bulidSupplierSorItemList(returnList, list);
        return returnList;
    }

    @Override
    public List<EventEvaluationPojo> getEvaluationDataForSorComparisonReport(String eventId, String envelopId) {
        List<EventEvaluationPojo> returnList = new ArrayList<>();
        List<Supplier> qualifiedSuppliers = eventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);

        RfaEvent event = eventDao.findById(eventId);
        if (CollectionUtil.isNotEmpty(qualifiedSuppliers)) {
            List<String> envelopIds = new ArrayList<String>();
            envelopIds.add(envelopId);
            List<SorPojo> bqList = envelopDao.getSorsIdListByEnvelopIdByOrder(envelopIds);
            for (SorPojo bq : bqList) {
                EventEvaluationPojo response = new EventEvaluationPojo();
                List<List<String>> data = new ArrayList<List<String>>();
                List<RfaSorItem> bqItems = sorItemDao.findSorItemsForSor(bq.getId());

                List<String> buyerHeading = new ArrayList<>();
                List<String> supplierHeading = new ArrayList<>();
                Map<String, List<String>> finalSupplierDataMap = new LinkedHashMap<>();
                List<List<String>> buyerDataList = new ArrayList<>();
                int bqItemLoopCount = 1;
                for (RfaSorItem bqItem : bqItems) {
                    BqNonPriceComprision bqNonPriceComprision = new BqNonPriceComprision();
                    List<String> answers = new ArrayList<String>();
                    response.setItemId(bqItem.getId());
                    response.setName(bqItem.getSor().getName());

                    answers.add(bqItem.getLevel() + "." + bqItem.getOrder());
                    answers.add(bqItem.getItemName());
                    answers.add(bqItem.getItemDescription());
                    answers.add(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");

                    setBuyerData(buyerDataList, bqItem);
                    bqNonPriceComprision.setBuyerFeildData(buyerDataList);

                    if (bqItemLoopCount == 1) {
                        setBuyerSupplierHeadings(buyerHeading, supplierHeading, bqItem);
                        bqItemLoopCount++;
                    }

                    if (CollectionUtil.isNotEmpty(buyerHeading)) {
                        bqNonPriceComprision.setBuyerHeading(buyerHeading);
                    }
                    if (CollectionUtil.isNotEmpty(supplierHeading)) {
                        bqNonPriceComprision.setSupplierHeading(supplierHeading);
                    }

                    List<String> supplierName = new ArrayList<>();
                    List<RfaSupplierSorItem> responseList = supplierSorItemDao.findSupplierSorItemsBySorItemIdAndEventId(bqItem.getId(), eventId, qualifiedSuppliers);
                    for (RfaSupplierSorItem item : responseList) {
                        response.setSupplierId(item.getSupplier().getId());
                        supplierName.add(item.getSupplier().getCompanyName());
                        answers.add(String.valueOf(item.getTotalAmount() != null ? item.getTotalAmount() : ""));

                        setSupplierData(finalSupplierDataMap, bqItem, item);
                    }

                    if (CollectionUtil.isNotEmpty(supplierName)) {
                        bqNonPriceComprision.setSupplierName(supplierName);
                        bqNonPriceComprision.setSupplierData(finalSupplierDataMap);
                        data.add(answers);
                        response.setBqNonPriceComprision(bqNonPriceComprision);
                    }
                    response.setData(data);
                    response.setColumns(qualifiedSuppliers);
                }
                returnList.add(response);
            }
        }
        return returnList;
    }


    private void bulidSupplierSorItemList(List<RfaSupplierSorItem> returnList, List<RfaSupplierSorItem> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            for (RfaSupplierSorItem item : list) {
                RfaSupplierSorItem parent = item.createShallowCopy();
                returnList.add(parent);
                if (CollectionUtil.isNotEmpty(item.getChildren())) {
                    for (RfaSupplierSorItem child : item.getChildren()) {
                        if (parent.getChildren() == null) {
                            parent.setChildren(new ArrayList<RfaSupplierSorItem>());
                        }
                        parent.getChildren().add(child.createShallowCopy());
                    }
                }
            }
        }
    }

    private void setBuyerData(List<List<String>> buyerDataList, RfaSorItem bqItem) {
        List<String> buyerData = new ArrayList<>();
        if (StringUtils.checkString(bqItem.getSor().getField1Label()).length() > 0) {
            if (bqItem.getSor().getField1FilledBy() == BqUserTypes.BUYER) {
                buyerData.add(bqItem.getField1());
            }
        }
        buyerDataList.add(buyerData);
    }

    private void setBuyerSupplierHeadings(List<String> buyerHeading, List<String> supplierHeading, RfaSorItem bqItem) {
        if (StringUtils.checkString(bqItem.getSor().getField1Label()).length() > 0) {
            if (bqItem.getSor().getField1FilledBy() == BqUserTypes.BUYER) {
                buyerHeading.add(bqItem.getSor().getField1Label());
            } else {
                supplierHeading.add(bqItem.getSor().getField1Label());
            }
        }
    }

    private void setSupplierData(Map<String, List<String>> finalSupplierDataMap, RfaSorItem bqItem, RfaSupplierSorItem item) {
        List<String> dataList = finalSupplierDataMap.get(item.getSorItem().getId());
        if (dataList != null) {
            List<String> dataList1 = finalSupplierDataMap.get(item.getSorItem().getId());
            if (StringUtils.checkString(bqItem.getSor().getField1Label()).length() > 0) {
                if (bqItem.getSor().getField1FilledBy() != BqUserTypes.BUYER) {
                    dataList1.add(item.getField1());
                }
            }

            finalSupplierDataMap.put(item.getSorItem().getId(), dataList1);
        } else {
            List<String> dataList1 = new ArrayList<>();
            if (StringUtils.checkString(bqItem.getSor().getField1Label()).length() > 0) {
                if (bqItem.getSor().getField1FilledBy() != BqUserTypes.BUYER) {
                    dataList1.add(item.getField1());
                }

            }
            finalSupplierDataMap.put(item.getSorItem().getId(), dataList1);
        }
    }


    @Override
    public List<RfaSupplierSorItem> getAllSupplierSorItemForReportBySorIdAndSupplierId(String sorId, String supplierId) {

        List<RfaSupplierSorItem> list = supplierSorItemDao.findSupplierSorItemByListBySorIdAndSupplierId(sorId, supplierId);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RfaSupplierSorItem item : list) {

                if (item.getSupplier() != null) {
                    item.getSupplier().getCompanyName();
                }
                if (CollectionUtil.isNotEmpty(item.getChildren())) {
                    item.getChildren().get(0).getId();
                }
            }
        }
        return list;
    }


    @Override
    public List<RfaSupplierSorItem> getAllSupplierSorItemForReportBySorIdAndSupplierIdParentIdNotNull(String bqId, String supplierIds) {
        return supplierSorItemDao.findSupplierBqItemListByBqIdAndSupplierIdsParentIdNotNull(bqId, supplierIds);
    }

}
