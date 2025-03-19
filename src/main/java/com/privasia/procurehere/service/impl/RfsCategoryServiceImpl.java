package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.RfsCategoryDao;
import com.privasia.procurehere.core.entity.RfsCategory;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.service.RfsCategoryService;

@Service
@Transactional(readOnly = true)
public class RfsCategoryServiceImpl implements RfsCategoryService {

	@Autowired
	private RfsCategoryDao rfsCategoryDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	public List<RfsCategory> findRfsCategoryForTenant(String tenantId, TableDataInput tableParams) {
		return rfsCategoryDao.findRfsCategoryForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalFilteredRfsCategoryForTenant(String tenantId, TableDataInput tableParams) {
		return rfsCategoryDao.findTotalFilteredRfsCategoryForTenant(tenantId, tableParams);
	}

	@Override
	public long findTotalRfsCategoryForTenant(String tenantId) {
		return rfsCategoryDao.findTotalRfsCategoryForTenant(tenantId);
	}

	@Override
	public RfsCategory getRfsCategoryById(String id) {
		return rfsCategoryDao.findById(id);
	}

	@Override
	public boolean isExists(RfsCategory rfsCategory, String buyerId) {
		return rfsCategoryDao.isExists(rfsCategory, buyerId);
	}

}