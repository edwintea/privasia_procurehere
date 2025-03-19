package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfqSupplierSor;
import com.privasia.procurehere.core.enums.SupplierBqStatus;

import java.util.List;

public interface RfqSupplierSorService {
    RfqSupplierSor findRfqSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);

    void updateRfqSor(RfqSupplierSor rfqSupplierSor);


    RfqSupplierSor getById(String id);


    List<RfqSupplierSor> findRfqSupplierSorbyEventIdAndSupplierId(String eventId, String loggedInUserTenantId);
}
