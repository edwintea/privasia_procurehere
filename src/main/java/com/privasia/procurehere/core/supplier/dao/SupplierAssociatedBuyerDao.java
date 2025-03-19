/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao;

import java.util.List;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RequestedAssociatedBuyer;
import com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo;

/**
 * @author pooja
 */
public interface SupplierAssociatedBuyerDao extends GenericDao<RequestedAssociatedBuyer, String> {
	/**
	 * @param buyerId
	 * @param supplierId
	 * @return
	 */
	RequestedAssociatedBuyerPojo getRequestedAssociatedBuyerById(String buyerId, String supplierId);

	/**
	 * @param requestId
	 * @return
	 */
	List<IndustryCategory> getIndustryCategoriesById(String requestId);
}
