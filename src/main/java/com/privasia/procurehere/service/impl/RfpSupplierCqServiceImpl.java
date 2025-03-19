package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpSupplierCqDao;
import com.privasia.procurehere.core.entity.RfpSupplierCq;
import com.privasia.procurehere.service.RfpSupplierCqService;

@Service
@Transactional(readOnly = true)
public class RfpSupplierCqServiceImpl implements RfpSupplierCqService{

	@Autowired
	RfpSupplierCqDao rfpSupplierCqDao;
	
	@Override
	public RfpSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId) {
		return rfpSupplierCqDao.findSupplierCqByEventIdAndSupplierId(supplierId, eventId, cqId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpSupplierCq updateSupplierCq(RfpSupplierCq rfpSupplierCq) {
		return rfpSupplierCqDao.update(rfpSupplierCq);
	}

	@Override
	public List<RfpSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId) {
		return rfpSupplierCqDao.findSupplierCqStatusByEventIdAndSupplierId(supplierId, eventId);
	}

	@Override
	public Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId) {
		return rfpSupplierCqDao.findCountOfSupplierCqForSupplier(cqId, eventId, supplierId);
	}

}
