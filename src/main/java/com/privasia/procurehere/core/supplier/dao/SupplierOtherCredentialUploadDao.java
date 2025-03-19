package com.privasia.procurehere.core.supplier.dao;

import java.util.List;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.SupplierOtherCredentials;

/**
 * @author Giridhar
 */

public interface SupplierOtherCredentialUploadDao  extends GenericDao<SupplierOtherCredentials, String>  {

	/**
	 * 
	 * @return
	 */
	List<SupplierOtherCredentials> findOtherCredentialAll();

	/**
	 * 
	 * @param supplierId
	 * @return
	 */
	SupplierOtherCredentials findOtherCredentialById(String id);

	/**
	 * 
	 * @param supplierId
	 * @return
	 */
	List<SupplierOtherCredentials> findAllOtherCredentialBySupplierId(String supplierId);

	/**
	 * 
	 * @param id
	 */
	void deleteById(String id);

}
