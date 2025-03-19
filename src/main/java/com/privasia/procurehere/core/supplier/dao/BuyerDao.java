/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.pojo.BuyerReportPojo;
import com.privasia.procurehere.core.pojo.BuyerSearchFilterPojo;
import com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Arc
 */
public interface BuyerDao extends GenericDao<Buyer, String> {

	/**
	 * @return
	 */
	List<Buyer> findAllBuyers();

	/**
	 * @param status
	 * @param order TODO
	 * @param globalSearch TODO
	 * @return
	 */
	List<Buyer> searchBuyer(String status, String order, String globalSearch);

	/**
	 * @param buyer
	 * @return
	 */
	boolean isExists(Buyer buyer);

	/**
	 * @param loginEmail
	 * @return
	 */
	boolean isExistsLoginEmail(String loginEmail);

	/**
	 * @param buyer
	 * @return
	 */
	boolean isExistsRegistrationNumber(Buyer buyer);

	/**
	 * @param buyer
	 * @return
	 */
	boolean isExistsCompanyName(Buyer buyer);

	List<Buyer> findAll1();

	List<Buyer> findActiveBuyers();

	List<Buyer> findPendingBuyers();

	List<Buyer> findListOfBuyerForDateRange(Date start, Date end, Country country, TableDataInput input);

	List<Buyer> getAllBuyerFromGlobalSearch(String searchVal);

	/**
	 * @param buyerId
	 */
	void decreaseEventLimitCountByBuyerId(String buyerId);

	/**
	 * @param tenantId
	 * @return
	 */
	Buyer findBuyerGeneralDetailsById(String tenantId);

	/**
	 * @param id
	 * @return
	 */
	Buyer findPlainBuyerById(String id);

	/**
	 * @return
	 */
	List<Buyer> findAllActiveBuyers();

	/**
	 * @return
	 */
	List<Buyer> findAllBuyersWithActiveSubscription();

	/**
	 * @return
	 */
	List<Buyer> findBuyersForExpiryNotificationReminderBefore30Days();

	/**
	 * @return
	 */
	List<Buyer> findBuyersForExpiryNotificationReminderBefore15Days();

	/**
	 * @return
	 */
	List<Buyer> findBuyersForExpiryNotificationReminderBefore7Days();

	/**
	 * @param buyerId
	 * @return
	 */
	Buyer findBuyerByIdWithBuyerPackage(String buyerId);

	/**
	 * @return
	 */
	List<Buyer> findAllBuyersFor2DaysBeforeSubscriptionExpire();

	/**
	 * @return
	 */
	List<Buyer> findBuyersForExpiryNotificationReminderBefore6Months();

	/**
	 * @return
	 */
	List<Buyer> findBuyersForExpiryNotificationReminderBefore3Months();

	/**
	 * @return
	 */
	List<Buyer> findAllBuyersFor7DaysBeforeSubscriptionExpire();

	/**
	 * @return
	 */
	List<Buyer> findAllBuyersFor15DaysBeforeSubscriptionExpire();

	/**
	 * @return
	 */
	List<Buyer> findAllBuyersFor30DaysBeforeSubscriptionExpire();

	void deleteIndustryCategoryByTanent(String tenantId);

	void deleteFavouriteSupplierByTanent(String tenantId);

	void deleteAuditTrail(String tenantId);

	void deleteUOM(String tenantId);

	void deleteBuyerSettings(String tenantId);

	void deleteCostCenter(String tenantId);

	void deleteBusinessUnit(String tenantId);

	void deleteProductItem(String tenantId);

	void deleteProductCategory(String tenantId);

	void deleteBuyerAddress(String tenantId);

	void deleteIdSettings(String tenantId);

	void deleteBuyerById(String tenantId);

	void deleteBuyerPackage(String tenantId);

	void setNullBuyerPackageBuyerSubscription(String tenantId);

	void deleteBuyerSubscription(String tenantId, boolean isNextsub);

	void deleteBuyerPaymentTransaction(String tenantId);

	void setUserNullInBuyer(String tenantId);

	List<Buyer> searchBuyerForPagination(String status, String order, String globalSearch, String pageNo);

	List<Buyer> findAllActiveMailBoxBuyers();

	List<Buyer> findAllActiveBuyersWithActiveSubscription();

	/**
	 * @param publicContextPath
	 * @param buyerId
	 * @return
	 */
	Integer isExistPublicContextPathForBuyer(String publicContextPath, String buyerId);

	/**
	 * @param buyerId
	 * @return
	 */
	String getTenantIdByPublicContextPath(String buyerId);

	String getTenantId(String buyerId);

	String getContextPathByBuyerId(String buyerId);

	/**
	 * @param buyerId
	 * @return
	 */
	RequestedAssociatedBuyerPojo getPublishedBuyerDetailsById(String buyerId);

	/**
	 * @param input
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	List<BuyerReportPojo> findAllSearchFilterBuyerReportList(TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param input
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	long findTotalSearchFilterBuyerReportCount(TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	long findTotalBuyerReportCount(Date startDate, Date endDate);

	List<BuyerReportPojo> getAllBuyerListForCsvReport(String[] buyerIds, BuyerSearchFilterPojo buyerSearchFilterPojo, boolean select_all, int pageSize, int pageNo, Date startDate, Date endDate);

	/**
	 * @param buyerId
	 * @return
	 */
	Buyer getContextPathForRegistration(String buyerId);

	/**
	 * @param buyerId
	 * @return
	 */
	Buyer findBuyerById(String buyerId);

}
