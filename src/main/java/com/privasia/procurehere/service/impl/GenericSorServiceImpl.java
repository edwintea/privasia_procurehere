package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfaSupplierSorItemDao;
import com.privasia.procurehere.core.dao.RfiSupplierSorItemDao;
import com.privasia.procurehere.core.dao.RfpSupplierSorItemDao;
import com.privasia.procurehere.core.dao.RfqSupplierSorDao;
import com.privasia.procurehere.core.dao.RfqSupplierSorItemDao;
import com.privasia.procurehere.core.dao.RftSupplierSorItemDao;
import com.privasia.procurehere.core.entity.BqItem;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RfaSupplierSor;
import com.privasia.procurehere.core.entity.RfaSupplierSorItem;
import com.privasia.procurehere.core.entity.RfiSupplierSorItem;
import com.privasia.procurehere.core.entity.RfpSupplierSorItem;
import com.privasia.procurehere.core.entity.RfqSupplierSor;
import com.privasia.procurehere.core.entity.RfqSupplierSorItem;
import com.privasia.procurehere.core.entity.RftSupplierSorItem;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.entity.SorItem;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.service.GenericSorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;

@Repository
public class GenericSorServiceImpl implements GenericSorService {

    @Autowired
    RfqSupplierSorItemDao rfqSupplierSorItemDao;

    @Autowired
    RfpSupplierSorItemDao rfpSupplierSorItemDao;

    @Autowired
    RftSupplierSorItemDao rftSupplierSorItemDao;

    @Autowired
    RfiSupplierSorItemDao rfiSupplierSorItemDao;

    @Autowired
    RfaSupplierSorItemDao rfaSupplierSorItemDao;

    @Override
    @Transactional(readOnly = false)
    public SorItem updateSupplierSorItem(SorItem sorItem, RfxTypes type, String decimal) throws ApplicationException {
       switch (type) {
           case RFQ:
               RfqSupplierSorItem rfqSupplierSorItem = rfqSupplierSorItemDao.findById(sorItem.getId());
               rfqSupplierSorItem.setTotalAmount(sorItem.getTotalAmount().setScale(Integer.parseInt(decimal), RoundingMode.HALF_UP));
               rfqSupplierSorItemDao.update(rfqSupplierSorItem);
               break;
           case RFT:
               RftSupplierSorItem rftSupplierSorItem = rftSupplierSorItemDao.findById(sorItem.getId());
               rftSupplierSorItem.setTotalAmount(sorItem.getTotalAmount().setScale(Integer.parseInt(decimal), RoundingMode.HALF_UP));
               rftSupplierSorItemDao.update(rftSupplierSorItem);
               break;
           case RFI:
               RfiSupplierSorItem rfiSupplierSorItem = rfiSupplierSorItemDao.findById(sorItem.getId());
               rfiSupplierSorItem.setTotalAmount(sorItem.getTotalAmount().setScale(Integer.parseInt(decimal), RoundingMode.HALF_UP));
               rfiSupplierSorItemDao.update(rfiSupplierSorItem);
               break;
           case RFA:
               RfaSupplierSorItem rfaSupplierSorItem = rfaSupplierSorItemDao.findById(sorItem.getId());
               rfaSupplierSorItem.setTotalAmount(sorItem.getTotalAmount().setScale(Integer.parseInt(decimal), RoundingMode.HALF_UP));
               rfaSupplierSorItemDao.update(rfaSupplierSorItem);
               break;
           case RFP:
               RfpSupplierSorItem rfpSupplierSorItem = rfpSupplierSorItemDao.findById(sorItem.getId());
               rfpSupplierSorItem.setTotalAmount(sorItem.getTotalAmount().setScale(Integer.parseInt(decimal), RoundingMode.HALF_UP));
               rfpSupplierSorItemDao.update(rfpSupplierSorItem);
               break;
           default:
               break;

       }
        return sorItem;
    }


    @Override
    @Transactional(readOnly = false)
    public SorItem updateSupplierSorItemField1(SorItem sorItem, RfxTypes type) throws ApplicationException {
        switch (type) {
            case RFQ:
                RfqSupplierSorItem rfqSupplierSorItem = rfqSupplierSorItemDao.findById(sorItem.getId());
                rfqSupplierSorItem.setField1(sorItem.getField1());
                rfqSupplierSorItemDao.update(rfqSupplierSorItem);
                break;
            case RFT:
                RftSupplierSorItem rftSupplierSorItem = rftSupplierSorItemDao.findById(sorItem.getId());
                rftSupplierSorItem.setField1(sorItem.getField1());
                rftSupplierSorItemDao.update(rftSupplierSorItem);
                break;
            case RFI:
                RfiSupplierSorItem rfiSupplierSorItem = rfiSupplierSorItemDao.findById(sorItem.getId());
                rfiSupplierSorItem.setField1(sorItem.getField1());
                rfiSupplierSorItemDao.update(rfiSupplierSorItem);
                break;
            case RFA:
                RfaSupplierSorItem rfaSupplierSorItem = rfaSupplierSorItemDao.findById(sorItem.getId());
                rfaSupplierSorItem.setField1(sorItem.getField1());
                rfaSupplierSorItemDao.update(rfaSupplierSorItem);
                break;
            case RFP:
                RfpSupplierSorItem rfpSupplierSorItem = rfpSupplierSorItemDao.findById(sorItem.getId());
                rfpSupplierSorItem.setField1(sorItem.getField1());
                rfpSupplierSorItemDao.update(rfpSupplierSorItem);
                break;
            default:
                break;

        }
        return sorItem;
    }

    public Boolean isAllFilledUp(Sor sor , RfxTypes type, String supplierId) throws ApplicationException {
        switch (type) {
            case RFQ:
                Boolean isNotAllCompletedRfq = rfqSupplierSorItemDao.checkAllFieldCompleted(sor.getId(), supplierId);
                return isNotAllCompletedRfq;
            case RFT:
                Boolean isNotAllCompletedRft = rftSupplierSorItemDao.checkAllFieldCompleted(sor.getId(), supplierId);
                return isNotAllCompletedRft;
            case RFI:
                Boolean isNotAllCompletedRfi = rfiSupplierSorItemDao.checkAllFieldCompleted(sor.getId(), supplierId);
                return isNotAllCompletedRfi;
            case RFA:
                Boolean isNotAllCompletedRfa = rfaSupplierSorItemDao.checkAllFieldCompleted(sor.getId(), supplierId);
                return isNotAllCompletedRfa;
            case RFP:
                Boolean isNotAllCompletedRfp = rfpSupplierSorItemDao.checkAllFieldCompleted(sor.getId(), supplierId);
                return isNotAllCompletedRfp;
            default:
                break;
        }
        return false;
    }
}
