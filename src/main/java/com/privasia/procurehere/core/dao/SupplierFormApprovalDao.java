package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierFormApproval;

/**
 * @author pooja
 */
public interface SupplierFormApprovalDao extends GenericDao<SupplierFormApproval, String> {
	/**
	 * 
	 * @param formId
	 * @return
	 */
	List<SupplierFormApproval> getAllApprovalListByFormId(String formId);
}