package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.BqItem;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;

public interface GenericBqService {

	Bq updateBq(Bq bq, RfxTypes type);

	BqItem updateBqItem(BqItem bq, RfxTypes type);

	BqItem getSupplierBqItem(String bqItemId, String supplierId, RfxTypes type);

	BqItem updateSupplierBqItem(BqItem bqItem, RfxTypes type, String decimal) throws ApplicationException;

}
