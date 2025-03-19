package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RftSupplierBq;

public interface RftSupplierBqService {

	RftSupplierBq getSupplierBqByBqAndSupplierId(String id, String supplierId);

	RftSupplierBq updateSupplierBq(RftSupplierBq persistSupplier);

	List<RftSupplierBq> findRftSupplierBqbyEventId(String eventId);

	List<RftSupplierBq> findRftSupplierBqbyEventIdAndSupplierId(String eventId, String SupplierId);

	List<RftSupplierBq> findRftSummarySupplierBqbyEventId(String id);

	RftSupplierBq findRftSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);

}
