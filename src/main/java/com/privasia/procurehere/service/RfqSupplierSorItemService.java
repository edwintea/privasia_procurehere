package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.entity.RfqSupplierSorItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.pojo.SorItemPojo;
import com.privasia.procurehere.core.utils.SecurityLibrary;

import java.util.List;

public interface RfqSupplierSorItemService {
    List<SorItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String supplierId, String searchVal);


    /**
     * @param eventBqId
     * @param supplierId
     * @param searchVal
     * @param pageNo
     * @param pageLength
     * @param itemLevel
     * @param itemOrder
     * @return
     */
    List<?> getSorItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer pageNo, Integer pageLength, Integer itemLevel, Integer itemOrder);



    long totalSorItemCountBySorId(String eventBqId, String supplierId, String searchVal);


    List<RfqSupplierSorItem> saveSupplierEventSor(String bqId);


    /**
     * @param eventId
     * @param envelopId
     * @param selectedSuppliers
     * @param logedUser
     * @param withOrWithoutTax
     * @param itemLevel
     * @param itemOrder
     * @param searchVal
     * @param start
     * @param length
     * @return
     */
    List<EventEvaluationPojo> getSorEvaluationData(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser, String withOrWithoutTax, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length);


    /**
     * @param eventId
     * @param envelopId
     * @return
     */
    List<EventEvaluationPojo> getEvaluationDataForSorComparisonReport(String eventId, String envelopId);


    List<RfqSupplierSorItem> getAllSupplierSorItemBySorIdAndSupplierId(String bqId, String supplierId);


    List<RfqSupplierSorItem> getAllSupplierSorItemForReportBySorIdAndSupplierId(String sorId, String id);


    List<RfqSupplierSorItem> getAllSupplierSorItemForReportBySorIdAndSupplierIdParentIdNotNull(String sorId, String id);
}
