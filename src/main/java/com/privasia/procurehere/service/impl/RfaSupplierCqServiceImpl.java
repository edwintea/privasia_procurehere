/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaSupplierCqDao;
import com.privasia.procurehere.core.entity.RfaSupplierCq;
import com.privasia.procurehere.service.RfaSupplierCqService;

/**
 * @author jayshree
 *
 */
@Service
@Transactional(readOnly = true)
public class RfaSupplierCqServiceImpl implements RfaSupplierCqService {

	@Autowired
	RfaSupplierCqDao rfaSupplierCqDao; 
	
	@Override
	public RfaSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId) {
		return rfaSupplierCqDao.findSupplierCqByEventIdAndSupplierId(supplierId, eventId, cqId);
	}
	
	@Override
	@Transactional(readOnly = false)
	public RfaSupplierCq updateSupplierCq(RfaSupplierCq persistSupplierCq) {
		return rfaSupplierCqDao.update(persistSupplierCq);
	}

	@Override
	public List<RfaSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId) {
		return rfaSupplierCqDao.findSupplierCqStatusByEventIdAndSupplierId(supplierId, eventId);
	}

	@Override
	public Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId) {
		return rfaSupplierCqDao.findCountOfSupplierCqForSupplier(cqId, eventId, supplierId);
	}

}
