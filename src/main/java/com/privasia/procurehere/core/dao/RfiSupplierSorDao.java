package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfiSupplierSor;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersSorPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierSorPojo;

import java.util.List;

public interface RfiSupplierSorDao extends GenericDao<RfiSupplierSor, String> {
    RfiSupplierSor findRfiSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);


    RfiSupplierSor findSorByEventIdAndSorName(String id, String bqId, String supplierId);


    List<RfiSupplierSor> findRfiSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId);

    List<RfiSupplierSor> rfiSupplierSorAnswerdByEventIdAndSupplierId(String eventId, String supplierId);


    long findPendingSorsByEventIdAndEventSorId(String eventId, String supplierId);

    List<EvaluationSuppliersSorPojo> getAllSorsBySorIdsAndEventId(String bqId, String id);

    List<RfaSupplierSorPojo> getAllRfiTopEventSuppliersIdByEventId(String id, int i, String id2);

    /**
     * @param eventId
     */
    void deleteSupplierSorsForEvent(String eventId);
}
