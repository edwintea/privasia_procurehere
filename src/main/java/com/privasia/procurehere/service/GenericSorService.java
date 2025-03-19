package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.BqItem;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.entity.SorItem;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;

public interface GenericSorService {
    SorItem updateSupplierSorItem(SorItem bqItem, RfxTypes type, String decimal) throws ApplicationException;


    SorItem updateSupplierSorItemField1(SorItem bqItem, RfxTypes type) throws ApplicationException;

    Boolean isAllFilledUp(Sor sor , RfxTypes type, String supplierId) throws ApplicationException;
}
