package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfaSupplierSor;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersSorPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierSorPojo;

import java.util.List;

public interface RfaSupplierSorDao extends GenericDao<RfaSupplierSor, String> {
    RfaSupplierSor findRfaSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);


    RfaSupplierSor findSorByEventIdAndSorName(String id, String bqId, String supplierId);

    List<RfaSupplierSor> findRfaSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId);

    List<RfaSupplierSor> rfaSupplierSorAnswerdByEventIdAndSupplierId(String eventId, String supplierId);


    long findPendingSorsByEventIdAndEventSorId(String eventId, String supplierId);

    List<EvaluationSuppliersSorPojo> getAllSorsBySorIdsAndEventId(String bqId, String id);

    List<RfaSupplierSorPojo> getAllRfqTopEventSuppliersIdByEventId(String id, int i, String id2);

    /**
     * @param eventId
     */
    void deleteSupplierSorsForEvent(String eventId);
}
