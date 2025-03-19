package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RftSupplierSor;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersSorPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierSorPojo;

import java.util.List;

public interface RftSupplierSorDao extends GenericDao<RftSupplierSor, String> {
    RftSupplierSor findRftSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);


    RftSupplierSor findSorByEventIdAndSorName(String id, String bqId, String supplierId);

    List<RftSupplierSor> findRftSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId);

    List<RftSupplierSor> rftSupplierSorAnswerdByEventIdAndSupplierId(String eventId, String supplierId);

    long findPendingSorsByEventIdAndEventSorId(String eventId, String supplierId);

    List<EvaluationSuppliersSorPojo> getAllSorsBySorIdsAndEventId(String bqId, String id);

    List<RfaSupplierSorPojo> getAllRftTopEventSuppliersIdByEventId(String id, int i, String id2);

    /**
     * @param eventId
     */
    void deleteSupplierSorsForEvent(String eventId);
}
