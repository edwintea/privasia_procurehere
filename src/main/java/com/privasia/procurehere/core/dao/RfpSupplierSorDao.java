package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfpSupplierSor;
import com.privasia.procurehere.core.entity.RfqSupplierSor;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersSorPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierSorPojo;

import java.util.List;

public interface RfpSupplierSorDao extends GenericDao<RfpSupplierSor, String> {
    RfpSupplierSor findRfpSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);


    RfpSupplierSor findSorByEventIdAndSorName(String id, String bqId, String supplierId);

    List<RfpSupplierSor> findRfpSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId);

    List<RfpSupplierSor> rfpSupplierSorAnswerdByEventIdAndSupplierId(String eventId, String supplierId);

    long findPendingSorsByEventIdAndEventSorId(String eventId, String supplierId);

    List<EvaluationSuppliersSorPojo> getAllSorsBySorIdsAndEventId(String bqId, String id);

    List<RfaSupplierSorPojo> getAllRfpTopEventSuppliersIdByEventId(String id, int i, String id2);

    /**
     * @param eventId
     */
    void deleteSupplierSorsForEvent(String eventId);
}
