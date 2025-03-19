package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqSupplierBqDao;
import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.service.RfqSupplierBqService;

@Service
@Transactional(readOnly = true)
public class RfqSupplierBqServiceImpl implements RfqSupplierBqService {

	@Autowired
	RfqSupplierBqDao rfqSupplierBqDao;

	@Override
	@Transactional(readOnly = false)
	public RfqSupplierBq getSupplierBqByBqAndSupplierId(String id, String supplierId) {
		return rfqSupplierBqDao.findBqByBqId(id, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqSupplierBq updateSupplierBq(RfqSupplierBq persistSupplier) {
		return rfqSupplierBqDao.update(persistSupplier);
	}

	@Override
	public List<RfqSupplierBq> findRfqSupplierBqbyEventId(String eventId) {
		return rfqSupplierBqDao.findRfqSupplierBqbyEventId(eventId);
	}

	@Override
	public List<RfqSupplierBq> findRfqSupplierBqbyEventIdAndSupplierId(String eventId, String supplierId) {
		return rfqSupplierBqDao.findRfqSupplierBqbyEventIdAndSupplierId(eventId, supplierId);
	}

	@Override
	public List<RfqSupplierBq> findRfqSummarySupplierBqbyEventId(String eventId) {
		return rfqSupplierBqDao.findRfqSummarySupplierBqbyEventId(eventId);
	}

	@Override
	public RfqSupplierBq findRfqSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
		return rfqSupplierBqDao.findRfqSupplierBqStatusbyEventIdAndSupplierId(supplierId, eventId, bqId);
	}

}
