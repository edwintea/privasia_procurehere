/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.SupplierCompanyProfile;

/**
 * @author Arc
 */
public interface SupplierCompanyProfileDao extends GenericDao<SupplierCompanyProfile, String> {

	/**
	 * @param id
	 */
	void deleteById(String id);

	long findCompanyDocumentCountBySupplierId(String id);

}
