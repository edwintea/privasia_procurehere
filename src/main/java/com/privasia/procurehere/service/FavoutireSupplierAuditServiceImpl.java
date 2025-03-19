package com.privasia.procurehere.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.FavoutireSupplierAuditDao;
import com.privasia.procurehere.core.entity.FavouriteSupplierStatusAudit;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Service
public class FavoutireSupplierAuditServiceImpl implements FavoutireSupplierAuditService {

	@Autowired
	FavoutireSupplierAuditDao auditDao;

	@Transactional(readOnly = false)
	@Override
	public FavouriteSupplierStatusAudit saveFavouriteSupplierAudit(FavouriteSupplierStatusAudit audit) {
		return auditDao.saveOrUpdate(audit);
	}

	@Override
	@Transactional(readOnly = false)
	public List<FavouriteSupplierStatusAudit> getAuditByTenantId(String tenantId, String favSuppId) {
		List<FavouriteSupplierStatusAudit> auditList = auditDao.findAuditTrailForTenant(tenantId, favSuppId);
		if (CollectionUtil.isNotEmpty(auditList)) {
			for (FavouriteSupplierStatusAudit favouriteSupplierStatusAudit : auditList) {
				if (favouriteSupplierStatusAudit.getActionBy() != null) {
					favouriteSupplierStatusAudit.getActionBy().getName();
				}
				if (favouriteSupplierStatusAudit.getFavSupp() != null) {
					favouriteSupplierStatusAudit.getFavSupp().getActionDate();
				}
			}
		}
		return auditList;
	}

}
