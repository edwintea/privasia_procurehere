/**
 * 
 */
package com.privasia.procurehere.service.supplier;

import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.exceptions.ApplicationException;

/**
 * @author ravi
 */
public interface BuyerSupplierRegistrationService {

	/**
	 * @param supplier
	 * @param tenantId TODO
	 * @return
	 * @throws ApplicationException 
	 */
	Supplier registerSupplier(Supplier supplier, String tenantId) throws ApplicationException;

	/**
	 * @param supplierId
	 * @return
	 */
	SupplierFormSubmition findSupplierFormBySupplierId(String supplierId);
}
