package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.FavouriteSupplierStatusAudit;

public interface FavoutireSupplierAuditService {
	public FavouriteSupplierStatusAudit saveFavouriteSupplierAudit(FavouriteSupplierStatusAudit audit);

	public List<FavouriteSupplierStatusAudit> getAuditByTenantId(String tenantId,String favSuppId);

}
