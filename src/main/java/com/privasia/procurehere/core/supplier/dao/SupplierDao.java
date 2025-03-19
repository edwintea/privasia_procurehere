/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.RequestedAssociatedBuyer;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.pojo.PendingEventPojo;
import com.privasia.procurehere.core.pojo.PoSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchSortFilterPojo;
import com.privasia.procurehere.core.pojo.SupplierDetailsCountPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierReportPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchFilterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Arc
 */
public interface SupplierDao extends GenericDao<Supplier, String> {

	/**
	 * @return
	 */
	List<Supplier> findAllactiveSuppliers();

	/**
	 * @return
	 */
	List<Supplier> findPendingSuppliers();

	/**
	 * @param status
	 * @param order TODO
	 * @param globalSearch TODO
	 * @return
	 */
	List<Supplier> searchSupplier(String status, String order, String globalSearch);

	/**
	 * @param supplier
	 * @return
	 */
	boolean isExists(Supplier supplier);

	/**
	 * @param loginEmail
	 * @return
	 */
	boolean isExistsLoginEmail(String loginEmail);

	/**
	 * @param id
	 * @return
	 */
	Supplier findSupplierByIdForAssignedCountries(String id);

	/**
	 * @param id
	 * @return
	 */
	Supplier findSupplierForProjectTrackById(String id);

	/**
	 * @param id
	 * @return
	 */
	Supplier findSupplierByIdForAssignedStates(String id);

	/**
	 * @param supplier
	 * @return
	 */
	boolean isExistsRegistrationNumber(Supplier supplier);

	/**
	 * @param supplier
	 * @return
	 */
	boolean isExistsCompanyName(Supplier supplier);

	Supplier findSuppById(String id);

	List<Supplier> findSuppliersOfNaicsCode(String ncid);

	List<SupplierPojo> findListOfSupplierForDateRange(Date start, Date end, Country country);

	List<Supplier> getAllSupplierFromGlobalSearch(String searchVal);

	/**
	 * @param supplierIds
	 * @return
	 */
	List<Supplier> getAllSupplierFromIds(List<String> supplierIds);

	/**
	 * @param id
	 * @return
	 */
	Supplier findSupplierSubscriptionDetailsBySupplierId(String id);

	/**
	 * @param supplierId
	 * @param oldCommunicationEmail
	 */
	void updateSupplierCommunicationEmail(String supplierId, String oldCommunicationEmail, String newCommunicationEmail);

	/**
	 * @param suppId
	 * @return
	 */
	Supplier findSupplierAndAssocitedBuyersById(String suppId);

	/**
	 * @return
	 */
	List<Supplier> findSuppliersForSubscriptionExpireOrExtend();

	/**
	 * @param remindDate
	 * @return
	 */
	List<Supplier> findSuppliersForExpiryNotificationReminder(Date remindDate);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<PoSupplierPojo> findAllSearchFilterPoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate,String status);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalSearchFilterPoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate,String status);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalPoForSupplier(String tenantId);

	/**
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	long findCountOfAllPOForSupplier(String tenantId, String userId);

	boolean isExistsRegistrationNumberWithId(Supplier supplier);

	boolean isExistsCompanyNameWithId(Supplier supplier);

	void updateSupplierCommunicationEmailForSupplierOnly(String supplierId, String oldCommunicationEmail, String newCommunicationEmail);

	List<FinancePo> findFinanceSuppliers(String id);

	List<FinancePo> serchFinanceSuppliers(String status, String order, String globalSreach, String id);

	List<Pr> findAllSearchFilterPoForFinance(String tenantId, TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier);

	long findTotalSearchFilterPoForFinance(String tenantId, TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier);

	long findTotalPoForFinance(String tenantId);

	long findCountOfAllPOForFinance(String tenantId, String userId);

	List<Pr> findAllSearchFilterPoForOwner(TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier);

	long findTotalSearchFilterPoForOwner(TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier);

	long findTotalPoForOwner();

	List<SupplierPojo> searchSupplierForPagination(String status, String order, String globalSearch, String pageNo);

	List<PendingEventPojo> findAllPOForSupplierMobile(String tenantId, SearchSortFilterPojo search);

	/**
	 * @param teanantId
	 * @return
	 */
	long findTotalAssocitedBuyersById(String teanantId);

	/**
	 * @return
	 */
	long getTotalSupplierCount();

	RequestedAssociatedBuyer findSupplierRequestByIds(String id, String buyerId);

	Supplier findSupplierSubscriptionDetailsBySupplierIdExcludedExpiredBuyers(String id);

	Supplier findPlainSupplierById(String supplierId);

	/**
	 * @return
	 */
	List<Supplier> findSuppliersForFutureSubscriptionActivation();

	List<SupplierReportPojo> findAllSearchFilterSupplierReportList(TableDataInput input, Date startDate, Date endDate);

	long findTotalSearchFilterSupplierReportCount(TableDataInput input, Date startDate, Date endDate);

	long findTotalSuppliersCount(Date startDate, Date endDate);

	/**
	 * @param supplierIds
	 * @param supplierSearchFilterPojo
	 * @param select_all
	 * @param PAGE_SIZE TODO
	 * @param pageNo TODO
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	List<SupplierReportPojo> getAlSupplierListForCsvReport(String[] supplierIds, SupplierSearchFilterPojo supplierSearchFilterPojo, boolean select_all, int PAGE_SIZE, int pageNo, Date startDate, Date endDate);

	/**
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	String findAssociateBuyersForSupplierId(String id) throws SQLException, IOException;

	/**
	 * @param tenantId
	 * @return
	 */
	long findSupplierCompanyProfileById(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long findSupplierFinancialDocumentsById(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long findSupplierOtherDocumentsById(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long findSupplierBoardOfDirectorsById(String tenantId);

	/**
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	String findGeoCoverageForSupplierId(String id) throws SQLException, IOException;

	/**
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	String findNaicCodesForSupplierId(String id) throws SQLException, IOException;

	Long findAwardCountForSupplier(String supplierId);

	/**
	 * @param supplierId
	 * @return
	 */
	SupplierPojo findInvitedAndSubmitedCountsForSupplier(String supplierId);

	/**
	 * @param supplierId
	 * @return
	 */
	SupplierDetailsCountPojo findSupplierDetailsCounts(String supplierId);

	/**
	 * @param supplier
	 * @param companyName
	 * @param regNumber
	 * @return
	 */
	boolean isExistsByCompanyNameOrRegNo(Supplier supplier, String companyName, String regNumber);

	/**
	 * @param id
	 * @param loggedInUser
	 * @param object
	 */
	void updateSupplierStatus(String id, User loggedInUser, SupplierStatus status);

	/**
	 * @param id
	 * @return
	 */
	Supplier findPlainSupplierUsingConstructorById(String id);

	/**
	 * @param tenantId
	 * @return
	 */
	List<String> getBuyerListForSupplierId(String tenantId);
}
