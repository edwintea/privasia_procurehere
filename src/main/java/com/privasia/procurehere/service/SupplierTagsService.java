package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierTags;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author sudesha
 */
public interface SupplierTagsService {

	/**
	 * @param supplierTags
	 */
	public void saveSupplierTags(SupplierTags supplierTags);

	/**
	 * @param supplierTags
	 */
	public void updateSupplierTags(SupplierTags supplierTags);

	/**
	 * @param id
	 * @return
	 */
	SupplierTags getSupplierTagsById(String id);

	/**
	 * @param costCenter
	 * @param tenantId
	 * @return
	 */
	boolean isExists(SupplierTags supplierTags, String tenantId);

	public long findTotalFilteredSupplierTagsForTenant(String loggedInUserTenantId, TableDataInput input);

	public long findTotalSupplierTagsForTenant(String loggedInUserTenantId);

	public List<SupplierTags> findSupplierTagsForTenant(String loggedInUserTenantId, TableDataInput input);

	public void deleteSupplierTags(SupplierTags supplierTags);

	public List<SupplierTags> searchAllActiveSupplierTagsForTenant(String loggedInUserTenantId);

	public List<SupplierTags> getAllSupplierTagsOnlyByIds(List<String> supplierTags);

	public SupplierTags getSuppliertagsAndTenantId(String string, String tenantId);
	public SupplierTags getSuppliertagsDescriptionAndTenantId(String string, String tenantId);

}
