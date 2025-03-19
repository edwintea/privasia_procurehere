package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpSupplierBqDao;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.service.RfpSupplierBqService;

@Service
@Transactional(readOnly = true)
public class RfpSupplierBqServiceImpl implements RfpSupplierBqService {

	@Autowired
	RfpSupplierBqDao rfpSupplierBqDao;

	@Override
	@Transactional(readOnly = false)
	public RfpSupplierBq getSupplierBqByBqAndSupplierId(String id, String supplierId) {
		return rfpSupplierBqDao.findBqByBqId(id, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpSupplierBq updateSupplierBq(RfpSupplierBq persistSupplier) {
		return rfpSupplierBqDao.update(persistSupplier);
	}

	@Override
	public List<RfpSupplierBq> findRfpSupplierBqbyEventId(String eventId) {
		return rfpSupplierBqDao.findRfpSupplierBqbyEventId(eventId);
	}

	@Override
	public List<RfpSupplierBq> findRfpSupplierBqbyEventIdAndSupplierId(String eventId, String supplierId) {
		return rfpSupplierBqDao.findRfpSupplierBqbyEventIdAndSupplierId(eventId, supplierId);
	}

	@Override
	public List<RfpSupplierBq> findRfpSummarySupplierBqbyEventId(String eventId) {
		return rfpSupplierBqDao.findRfpSummarySupplierBqbyEventId(eventId);
	}

	@Override
	public RfpSupplierBq findRfpSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
		return rfpSupplierBqDao.findRfpSupplierBqStatusbyEventIdAndSupplierId(supplierId, eventId, bqId);
	}

}
