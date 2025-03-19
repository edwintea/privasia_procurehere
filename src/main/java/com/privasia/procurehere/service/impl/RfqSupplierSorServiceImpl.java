package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfqSupplierSorDao;
import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.entity.RfqSupplierSor;
import com.privasia.procurehere.core.enums.SupplierBqStatus;
import com.privasia.procurehere.service.RfqSupplierSorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RfqSupplierSorServiceImpl implements RfqSupplierSorService {

    @Autowired
    RfqSupplierSorDao rfqSupplierSorDao;

    @Override
    public RfqSupplierSor findRfqSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
        return rfqSupplierSorDao.findRfqSupplierSorStatusbyEventIdAndSupplierId(supplierId, eventId, bqId);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateRfqSor(RfqSupplierSor rfqSupplierSor) {
        rfqSupplierSorDao.update(rfqSupplierSor);
    }

    @Override
    public RfqSupplierSor getById(String id) {
        return rfqSupplierSorDao.findById(id);
    }

    @Override
    public List<RfqSupplierSor> findRfqSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId) {
        return rfqSupplierSorDao.findRfqSupplierSorbyEventIdAndSupplierId(eventId, supplierId);
    }

}
