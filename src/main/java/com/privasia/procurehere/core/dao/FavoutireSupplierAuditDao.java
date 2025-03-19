package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.FavouriteSupplierStatusAudit;

/**
 * @author Sarang
 */
public interface FavoutireSupplierAuditDao extends GenericDao<FavouriteSupplierStatusAudit, String> {

	List<FavouriteSupplierStatusAudit> findAuditTrailForTenant(String tenantId, String favSuppId);

}
