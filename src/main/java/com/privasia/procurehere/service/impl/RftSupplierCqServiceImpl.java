package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftSupplierCqDao;
import com.privasia.procurehere.core.entity.RftSupplierCq;
import com.privasia.procurehere.service.RftSupplierCqService;

@Service
@Transactional(readOnly = true)
public class RftSupplierCqServiceImpl implements RftSupplierCqService {

	@Autowired
	RftSupplierCqDao rftSupplierCqDao;
	
	@Override
	public RftSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId) {
		return rftSupplierCqDao.findSupplierCqByEventIdAndSupplierId(supplierId, eventId, cqId);
	}

	@Override
	@Transactional(readOnly = false)
	public RftSupplierCq updateSupplierCq(RftSupplierCq rftSupplierCq) {
		return rftSupplierCqDao.update(rftSupplierCq);
	}

	@Override
	public List<RftSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId) {
		return rftSupplierCqDao.findSupplierCqStatusByEventIdAndSupplierId(supplierId, eventId);
	}

	@Override
	public Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId) {
		return rftSupplierCqDao.findCountOfSupplierCqForSupplier(cqId, eventId, supplierId);
	}

}
