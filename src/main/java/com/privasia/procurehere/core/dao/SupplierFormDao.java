package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierForm;
import com.privasia.procurehere.core.pojo.SupplierFormPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */
public interface SupplierFormDao extends GenericDao<SupplierForm, String> {
	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	List<SupplierFormPojo> findSupplierFormsByTeantId(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredSupplierFormsForTenant(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalFilteredSupplierFormsForTenant(String loggedInUserTenantId);

	/**
	 * @param id
	 * @return
	 */
	SupplierForm getFormById(String id);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<SupplierForm> getSupplierFormListByTenantId(String loggedInUserTenantId);

	/**
	 * @param supplierFormObj
	 * @param loggedInUserTenantId
	 * @return
	 */
	boolean isFormNameExists(SupplierForm supplierFormObj, String loggedInUserTenantId);

	/**
	 * @param formId
	 * @param loggedInUserTenantId
	 * @return
	 */
	SupplierForm getSupplierFormByTenantAndId(String formId, String loggedInUserTenantId);

    void transferOwnership(String fromUserId, String toUserId);
}
