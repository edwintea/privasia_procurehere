package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfpSupplierSorDao;
import com.privasia.procurehere.core.entity.RfpSupplierSor;
import com.privasia.procurehere.core.entity.RfqSupplierSor;
import com.privasia.procurehere.service.RfpSupplierSorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RfpSupplierSorServiceImpl implements RfpSupplierSorService {

    @Autowired
    RfpSupplierSorDao rfpSupplierSorDao;

    @Override
    public RfpSupplierSor findRfpSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
        return rfpSupplierSorDao.findRfpSupplierSorStatusbyEventIdAndSupplierId(supplierId, eventId, bqId);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateRfpSor(RfpSupplierSor RfpSupplierSor) {
        rfpSupplierSorDao.saveOrUpdate(RfpSupplierSor);
    }

    @Override
    public RfpSupplierSor getById(String id) {
        return rfpSupplierSorDao.findById(id);
    }

    @Override
    public List<RfpSupplierSor> findRfpSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId) {
        return rfpSupplierSorDao.findRfpSupplierSorbyEventIdAndSupplierId(eventId, supplierId);
    }
}
