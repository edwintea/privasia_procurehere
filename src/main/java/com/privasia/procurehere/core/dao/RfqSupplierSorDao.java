package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.entity.RfqSupplierSor;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersSorPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierSorPojo;

import java.util.List;

public interface RfqSupplierSorDao extends GenericDao<RfqSupplierSor, String> {
    RfqSupplierSor findRfqSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);


    RfqSupplierSor findSorByEventIdAndSorName(String id, String bqId, String supplierId);

    List<RfqSupplierSor> findRfqSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId);


    List<RfqSupplierSor> rfqSupplierSorAnswerdByEventIdAndSupplierId(String eventId, String supplierId);

    long findPendingSorsByEventIdAndEventSorId(String eventId, String supplierId);

    List<EvaluationSuppliersSorPojo> getAllSorsBySorIdsAndEventId(String bqId, String id);

    List<RfaSupplierSorPojo> getAllRfqTopEventSuppliersIdByEventId(String id, int i, String id2);

    /**
     * @param eventId
     */
    void deleteSupplierSorsForEvent(String eventId);
}
