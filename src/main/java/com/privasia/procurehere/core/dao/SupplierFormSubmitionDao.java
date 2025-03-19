/**
 * 
 */
package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.SupplierFormSubmition;

/**
 * @author ravi
 *
 */
public interface SupplierFormSubmitionDao extends GenericDao<SupplierFormSubmition, String> {

	/**
	 * @param supplierId
	 * @return
	 */
	SupplierFormSubmition findSupplierFormBySupplierId(String supplierId);

}
