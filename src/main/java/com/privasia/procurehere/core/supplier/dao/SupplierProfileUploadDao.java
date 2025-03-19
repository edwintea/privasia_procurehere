package com.privasia.procurehere.core.supplier.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierCompanyProfile;

/**
 * @author Giridhar
 */

public interface SupplierProfileUploadDao {
	/**
	 * 
	 * @return
	 */
	List<SupplierCompanyProfile> findCompanyProfileAll();

	/**
	 * 
	 * @param supplierId
	 * @return
	 */
	SupplierCompanyProfile findCompanyProfileById(String supplierId);

	/**
	 * 
	 * @param supplierId
	 * @return
	 */
	List<SupplierCompanyProfile> findAllCompanyProfileBySupplierId(String supplierId);

}
