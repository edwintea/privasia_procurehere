package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftSupplierBqDao;
import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.service.RftSupplierBqService;

@Service
@Transactional(readOnly = true)
public class RftSupplierBqServiceImpl implements RftSupplierBqService {

	@Autowired
	RftSupplierBqDao rftSupplierBqDao;

	@Override
	@Transactional(readOnly = false)
	public RftSupplierBq getSupplierBqByBqAndSupplierId(String id, String supplierId) {
		return rftSupplierBqDao.findBqByBqId(id, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public RftSupplierBq updateSupplierBq(RftSupplierBq persistSupplier) {
		return rftSupplierBqDao.update(persistSupplier);
	}

	@Override
	public List<RftSupplierBq> findRftSupplierBqbyEventId(String eventId) {
		return rftSupplierBqDao.findRftSupplierBqbyEventId(eventId);
	}

	@Override
	public List<RftSupplierBq> findRftSupplierBqbyEventIdAndSupplierId(String eventId, String supplierId) {
		return rftSupplierBqDao.findRftSupplierBqbyEventIdAndSupplierId(eventId, supplierId);
	}

	@Override
	public List<RftSupplierBq> findRftSummarySupplierBqbyEventId(String id) {
		return rftSupplierBqDao.findRftSummarySupplierBqbyEventId(id);
	}

	@Override
	public RftSupplierBq findRftSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
		return rftSupplierBqDao.findRftSupplierBqStatusbyEventIdAndSupplierId(supplierId, eventId, bqId);
	}

}
