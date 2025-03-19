package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfpSupplierSor;
import com.privasia.procurehere.core.pojo.SorItemPojo;

import java.util.List;

public interface RfpSupplierSorService {
    RfpSupplierSor findRfpSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);

    void updateRfpSor(RfpSupplierSor RfpSupplierSor);


    RfpSupplierSor getById(String id);

    List<RfpSupplierSor> findRfpSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId);

    /*List<SorItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String supplierId, String searchVal);*/
}
