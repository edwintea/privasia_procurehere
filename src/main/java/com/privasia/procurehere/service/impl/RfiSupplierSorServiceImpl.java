package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfiSupplierSorDao;
import com.privasia.procurehere.core.entity.RfiSupplierSor;
import com.privasia.procurehere.core.entity.RftSupplierSor;
import com.privasia.procurehere.service.RfiSupplierSorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RfiSupplierSorServiceImpl implements RfiSupplierSorService {

    @Autowired
    RfiSupplierSorDao rfiSupplierSorDao;

    @Override
    public RfiSupplierSor findRfiSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
        return rfiSupplierSorDao.findRfiSupplierSorStatusbyEventIdAndSupplierId(supplierId, eventId, bqId);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateRfiSor(RfiSupplierSor RfiSupplierSor) {
        rfiSupplierSorDao.saveOrUpdate(RfiSupplierSor);
    }

    @Override
    public RfiSupplierSor getById(String id) {
        return rfiSupplierSorDao.findById(id);
    }

    @Override
    public List<RfiSupplierSor> findRfiSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId) {
        return rfiSupplierSorDao.findRfiSupplierSorbyEventIdAndSupplierId(eventId, supplierId);
    }
}
