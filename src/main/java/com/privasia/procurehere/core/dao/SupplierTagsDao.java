package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierTags;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface SupplierTagsDao extends GenericDao<SupplierTags, String> {

	/**
	 * @param costCenter
	 * @param tenantId TODO
	 * @return
	 */
	boolean isExists(SupplierTags costCenter, String tenantId);

	List<SupplierTags> findSupplierTagsForTenant(String tenantId, TableDataInput tableParams);

	long findTotalFilteredSupplierTagsForTenant(String tenantId, TableDataInput tableParams);

	long findTotalSupplierTagsForTenant(String tenantId);

	List<SupplierTags> searchAllActiveSupplierTagsForTenant(String loggedInUserTenantId);

	List<SupplierTags> getAllSupplierTagsOnlyByIds(List<String> supplierTags);

	SupplierTags getSuppliertagsAndTenantId(String suppliertags, String tenantId);

	SupplierTags getSuppliertagsDescriptionAndTenantId(String description, String tenantId);

}