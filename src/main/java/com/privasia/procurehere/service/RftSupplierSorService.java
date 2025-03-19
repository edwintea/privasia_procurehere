package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RftSupplierSor;

import java.util.List;

public interface RftSupplierSorService {
    RftSupplierSor findRftSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);

    void updateRftSor(RftSupplierSor RftSupplierSor);


    RftSupplierSor getById(String id);

    List<RftSupplierSor> findRftSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId);
}
