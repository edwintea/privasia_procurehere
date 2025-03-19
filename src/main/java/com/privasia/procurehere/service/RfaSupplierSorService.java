package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfaSupplierSor;

import java.util.List;

public interface RfaSupplierSorService {
    RfaSupplierSor findRfaSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);

    void updateRfaSor(RfaSupplierSor RfaSupplierSor);


    RfaSupplierSor getById(String id);

    List<RfaSupplierSor> findRfaSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId);
}
