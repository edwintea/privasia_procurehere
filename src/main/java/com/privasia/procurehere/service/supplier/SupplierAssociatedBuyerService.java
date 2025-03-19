/**
 * 
 */
package com.privasia.procurehere.service.supplier;

import java.util.List;
import java.util.TimeZone;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RequestedAssociatedBuyer;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo;

/**
 * @author pooja
 */
public interface SupplierAssociatedBuyerService {

	void saveOrUpdateAssociateRequest(RequestedAssociatedBuyer requestBuyerObj, User loggedInUser);

	/**
	 * @param supplier
	 * @param supplierRemark
	 * @param buyer
	 * @param timeZone
	 */
	void sendEmailToAssociatedBuyer(Supplier supplier, String supplierRemark, Buyer buyer, TimeZone timeZone);

	/**
	 * @param buyerId
	 * @param supplierId
	 * @return
	 */
	RequestedAssociatedBuyerPojo getRequestedAssociatedBuyerById(String buyerId, String supplierId);

	/**
	 * @param buyerId TODO
	 * @return
	 */
	RequestedAssociatedBuyerPojo getPublishedBuyerDetailsById(String buyerId);

	/**
	 * @param buyerId
	 * @return
	 */
	List<IndustryCategory> getIndustryCategoryForTenant(String buyerId);

	/**
	 * @param requestId
	 * @return
	 */
	List<IndustryCategory> getIndustryCategoriesById(String requestId);

	void sendEmailToAssociatedSupplier(Supplier supplier, Buyer buyer, TimeZone timeZone, boolean flag, RequestedAssociatedBuyer associatedBuyer, String buyerRemark);

	/**
	 * @param associatedBuyer
	 * @param data
	 * @param loggedInUser TODO
	 * @return
	 */
	RequestedAssociatedBuyer rejectSupplierRequest(RequestedAssociatedBuyer associatedBuyer, RequestedAssociatedBuyerPojo data, User loggedInUser);

	/**
	 * @param associatedBuyer
	 * @param data
	 * @param loggedInUser
	 * @return
	 */
	RequestedAssociatedBuyer acceptSupplierRequest(RequestedAssociatedBuyer associatedBuyer, RequestedAssociatedBuyerPojo data, User loggedInUser);

}
