package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqSupplierCqDao;
import com.privasia.procurehere.core.entity.RfqSupplierCq;
import com.privasia.procurehere.service.RfqSupplierCqService;

@Service
@Transactional(readOnly = true)
public class RfqSupplierCqServiceImpl implements RfqSupplierCqService {

	@Autowired
	RfqSupplierCqDao rfqSupplierCqDao;
	
	@Override
	public RfqSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId) {
		return rfqSupplierCqDao.findSupplierCqByEventIdAndSupplierId(supplierId, eventId, cqId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqSupplierCq updateSupplierCq(RfqSupplierCq rfqSupplierCq) {
		return rfqSupplierCqDao.update(rfqSupplierCq);
	}

	@Override
	public List<RfqSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId) {
		return rfqSupplierCqDao.findSupplierCqStatusByEventIdAndSupplierId(supplierId, eventId);
	}

	@Override
	public Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId) {
		return rfqSupplierCqDao.findCountOfSupplierCqForSupplier(cqId, eventId, supplierId);
	}

}
