package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierNoteDocument;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface SuppNotesDocUploadDao extends GenericDao<SupplierNoteDocument, String> {

	/**
	 * @param suppId
	 * @param loggedInUserTenantId
	 * @param tenantType
	 * @param input
	 * @return
	 */

	List<SupplierNoteDocument> findSuppNotesDocBySuppId(String suppId, String loggedInUserTenantId, TenantType tenantType, TableDataInput input);

	/**
	 * @param suppId
	 * @param loggedInUserTenantId
	 * @param tenantType
	 * @param input
	 * @return
	 */
	long findTotalFilteredSupNotesDocList(String suppId, String loggedInUserTenantId, TenantType tenantType, TableDataInput input);

	/**
	 * @param suppId
	 * @param loggedInUserTenantId
	 * @param tenantType
	 * @return
	 */

	long findTotalSupNotesDocList(String suppId, String loggedInUserTenantId, TenantType tenantType);
	

	List<SupplierNoteDocument> findSuppNotesDocBySuppIdWithFile(String suppId, String tenentId);
}