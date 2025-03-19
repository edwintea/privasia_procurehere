package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfiSupplierSor;

import java.util.List;

public interface RfiSupplierSorService {
    RfiSupplierSor findRfiSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);

    void updateRfiSor(RfiSupplierSor RfiSupplierSor);


    RfiSupplierSor getById(String id);

    List<RfiSupplierSor> findRfiSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId);
}
