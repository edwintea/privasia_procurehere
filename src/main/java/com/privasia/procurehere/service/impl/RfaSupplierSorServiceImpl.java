package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfaSupplierSorDao;
import com.privasia.procurehere.core.entity.RfaSupplierSor;
import com.privasia.procurehere.core.entity.RfpSupplierSor;
import com.privasia.procurehere.service.RfaSupplierSorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RfaSupplierSorServiceImpl implements RfaSupplierSorService {

    @Autowired
    RfaSupplierSorDao rfaSupplierSorDao;

    @Override
    public RfaSupplierSor findRfaSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
        return rfaSupplierSorDao.findRfaSupplierSorStatusbyEventIdAndSupplierId(supplierId, eventId, bqId);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateRfaSor(RfaSupplierSor RfaSupplierSor) {
        rfaSupplierSorDao.saveOrUpdate(RfaSupplierSor);
    }

    @Override
    public RfaSupplierSor getById(String id) {
        return rfaSupplierSorDao.findById(id);
    }

    @Override
    public List<RfaSupplierSor> findRfaSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId) {
        return rfaSupplierSorDao.findRfaSupplierSorbyEventIdAndSupplierId(eventId, supplierId);
    }
}
