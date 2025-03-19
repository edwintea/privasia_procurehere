/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfiSupplierCqDao;
import com.privasia.procurehere.core.entity.RfiSupplierCq;
import com.privasia.procurehere.service.RfiSupplierCqService;

/**
 * @author jayshree
 *
 */
@Service
@Transactional(readOnly = true)
public class RfiSupplierCqServiceImpl implements RfiSupplierCqService {

	@Autowired
	RfiSupplierCqDao rfiSupplierCqDao; 
	
	@Override
	public RfiSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId) {
		return rfiSupplierCqDao.findSupplierCqByEventIdAndSupplierId(supplierId, eventId, cqId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfiSupplierCq updateSupplierCq(RfiSupplierCq persistSupplierCq) {
		return rfiSupplierCqDao.update(persistSupplierCq);
	}

	@Override
	public List<RfiSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId) {
		return rfiSupplierCqDao.findSupplierCqStatusByEventIdAndSupplierId(supplierId, eventId);
	}

	@Override
	public Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId) {
		return rfiSupplierCqDao.findCountOfSupplierCqForSupplier(cqId, eventId, supplierId);
	}

}
