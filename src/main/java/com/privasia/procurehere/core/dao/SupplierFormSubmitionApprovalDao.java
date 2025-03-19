package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.SupplierFormSubmitionApproval;

/**
 * @author ravi
 */
public interface SupplierFormSubmitionApprovalDao extends GenericDao<SupplierFormSubmitionApproval, String> {

	/**
	 * @param id
	 * @return
	 */
	SupplierFormSubmitionApproval findSupplierFormSubmitionApproval(String id);

}
