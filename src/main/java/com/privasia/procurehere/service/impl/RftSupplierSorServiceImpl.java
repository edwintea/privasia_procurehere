package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RftSupplierSorDao;
import com.privasia.procurehere.core.entity.RfaSupplierSor;
import com.privasia.procurehere.core.entity.RftSupplierSor;
import com.privasia.procurehere.service.RftSupplierSorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RftSupplierSorServiceImpl implements RftSupplierSorService {

    @Autowired
    RftSupplierSorDao rftSupplierSorDao;

    @Override
    public RftSupplierSor findRftSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
        return rftSupplierSorDao.findRftSupplierSorStatusbyEventIdAndSupplierId(supplierId, eventId, bqId);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateRftSor(RftSupplierSor RftSupplierSor) {
        rftSupplierSorDao.saveOrUpdate(RftSupplierSor);
    }

    @Override
    public RftSupplierSor getById(String id) {
        return rftSupplierSorDao.findById(id);
    }

    @Override
    public List<RftSupplierSor> findRftSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId) {
        return rftSupplierSorDao.findRftSupplierSorbyEventIdAndSupplierId(eventId, supplierId);
    }
}
