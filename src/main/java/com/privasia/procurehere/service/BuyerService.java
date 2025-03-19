/**
 * 
 */
package com.privasia.procurehere.service;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Notes;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.BuyerReportPojo;
import com.privasia.procurehere.core.pojo.BuyerSearchFilterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Arc
 */
public interface BuyerService {

	/**
	 * @param buyer
	 * @return TODO
	 * @throws ApplicationException TODO
	 */
	User saveBuyer(Buyer buyer) throws ApplicationException;

	/**
	 * @param buyer
	 * @return
	 */
	Buyer updateBuyer(Buyer buyer);

	/**
	 * @param buyer
	 */
	void deleteBuyer(Buyer buyer);

	/**
	 * @return
	 */
	List<Buyer> findAllBuyers();

	/**
	 * @param buyerId
	 * @return
	 */
	Buyer findBuyerById(String buyerId);

	/**
	 * @param buyer
	 * @throws ApplicationException
	 */
	void confirmBuyer(Buyer buyer) throws ApplicationException;

	/**
	 * @param status
	 * @param order
	 * @param globalSearch
	 * @return
	 */
	List<Buyer> searchBuyers(String status, String order, String globalSearch);

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

	Notes saveByerNotes(Notes notes);

	/**
	 * @param id
	 * @return
	 */
	List<Notes> findAllNotesById(String id);

	/**
	 * @param buyer
	 * @param user
	 */
	void sentBuyerCreationMail(Buyer buyer, User user) throws ApplicationException;

	/**
	 * @return
	 */
	@Deprecated
	List<Buyer> findAllBuyers1();

	/**
	 * @param buyerObj
	 * @param createdByLoginId
	 * @return TODO
	 * @throws Exception
	 */
	User saveManualBuyer(Buyer buyerObj, String createdByLoginId) throws Exception;

	Owner getDefaultOwner();

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
	 * @param buyerId
	 * @param startDate TODO
	 * @param endDate TODO
	 * @param userLimit TODO
	 * @param eventLimit TODO
	 * @param subscription
	 * @return
	 */
	Buyer updateManualSubscription(String buyerId, String startDate, String endDate, Integer userLimit, Integer eventLimit) throws Exception;

	/**
	 * @param buyerId
	 * @return
	 */
	Buyer findBuyerByIdWithBuyerPackage(String buyerId);

	/**
	 * @return
	 */
	List<Buyer> findBuyersForExpiryNotificationReminderBefore15Days();

	/**
	 * @return
	 */
	List<Buyer> findBuyersForExpiryNotificationReminderBefore7Days();

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

	/**
	 * @param buyer
	 * @param user
	 * @throws ApplicationException
	 */
	void sendTrialBuyerCreationMail(Buyer buyer, User user) throws ApplicationException;

	/**
	 * @param buyer
	 * @param createdDate
	 * @param createdBy
	 */
	void createBuyerDefaultData(Buyer buyer, Date createdDate, User createdBy);

	List<Buyer> searchBuyersForPagination(String status, String order, String globalSearch, String pageNo);

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
	 * @param buyer
	 * @return
	 */
	Buyer updateBuyerOnly(Buyer buyer);

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

	long findTotalBuyerReportCount(Date startDate, Date endDate);

	/**
	 * @param response
	 * @param file
	 * @param buyerSearchFilterPojo
	 * @param select_all
	 * @param buyerIds
	 * @param formatter
	 * @param startDate TODO
	 * @param endDate TODO
	 */
	void downloadCsvFileForBuyerList(HttpServletResponse response, File file, BuyerSearchFilterPojo buyerSearchFilterPojo, boolean select_all, String[] buyerIds, DateFormat formatter, Date startDate, Date endDate);

	/**
	 * @param buyerId
	 * @return
	 */
	Buyer getContextPathForRegistration(String buyerId);
}
