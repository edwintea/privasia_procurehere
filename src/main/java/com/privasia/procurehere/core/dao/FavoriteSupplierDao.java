package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo;
import com.privasia.procurehere.core.pojo.SearchFilterSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.pojo.SupplierCountPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Javed Ahmed
 */
public interface FavoriteSupplierDao extends GenericDao<FavouriteSupplier, Serializable> {

	public List<SupplierPojo> searchSuppliers(TableDataInput input, SupplierSearchPojo searchParams, String tenantId);

	public List<Supplier> searchSupplierz(SupplierSearchPojo searchParams);

	public List<FavouriteSupplier> favoriteSuppliersOfBuyer(String buyerId, List<Supplier> invitedList, BigDecimal minGrade, BigDecimal maxGrade);

	boolean isExists(FavouriteSupplier favouriteSupplier);

	public List<FavouriteSupplier> searchFavSuppliers(SupplierSearchPojo searchParams);

	public List<FavouriteSupplier> searchFavSuppliersOfIndCat(String icId);

	public FavouriteSupplier findFavSupplierBySuppId(String sId, String buyerId);

	public List<Supplier> searchSupplierInFavByOder(SearchVo searchVo);

	public FavouriteSupplier getFavouriteSupplierBySupplierId(String supId, String buyerId);

	public List<FavouriteSupplier> searchCustomSupplier(SupplierSearchPojo searchParams, String buyerId);

	public List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId, List<String> invitedList);

	/**
	 * @param suppliers
	 * @return
	 */
	List<FavouriteSupplier> findAllFavouriteSuppliersForSuppliers(List<Supplier> suppliers);

	/**
	 * @param fsId
	 * @param buyerId TODO
	 * @return
	 */
	FavouriteSupplier findFavSupplierByFavSuppId(String fsId, String buyerId);

	/**
	 * @param search TODO
	 * @param tenantId
	 * @return
	 */
	public List<FavouriteSupplier> favSuppliersByNameAndTenant(String search, String tenantId);

	public List<FavouriteSupplier> getAllFavouriteSupplierFromGlobalSearch(String searchVal, String tenantId);

	Integer favoriteSuppliersOfBuyerCount(String buyerId, List<Supplier> invitedList);

	public Supplier isSupplierInGlobalList(Country country, String registrationNo, String companyName);

	public boolean isSupplierInFavouriteList(Supplier supplier, String buyerId);

	Integer searchSuppliersCount(TableDataInput input, SupplierSearchPojo searchParams, String tenantId);

	List<FavouriteSupplier> getAllFavouriteSupplierByTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long countForFavSupplier(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<SupplierCountPojo> getCurrentSupplierCountForTopFiveCategories(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	public List<FavouriteSupplier> getAllActiveFavouriteSupplierByTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @param companyName
	 * @return
	 */

	public FavouriteSupplier getFavouriteSupplierByCompanyName(String tenantId, String companyName);

	/**
	 * @param buyerId
	 * @param invitedList
	 * @param eventCategoryList
	 * @param minGrade TODO
	 * @param maxGrade TODO
	 * @return
	 */
	List<FavouriteSupplier> favoriteSuppliersOfBuyerByIndusCategory(String buyerId, List<Supplier> invitedList, List<IndustryCategory> eventCategoryList, BigDecimal minGrade, BigDecimal maxGrade);

	/**
	 * @param searchParam
	 * @param buyerId
	 * @param invitedList
	 * @param categoryList
	 * @return
	 */
	List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNoByIndusCategory(String searchParam, String buyerId, List<String> invitedList, List<IndustryCategory> categoryList);

	public Supplier isSupplierInRegNoandCompany(Country country, String registrationNo, String companyName);

	// public List<FavouriteSupplier> getAllFavouriteSupplierById(String[] favArr, String loggedInUserTenantId, boolean
	// select_all);
	public List<FavouriteSupplier> getAllFavouriteSupplierById(String[] favArr, String loggedInUserTenantId, boolean select_all, SearchFilterSupplierPojo searchFilterSupplierPojo);

	List<FavouriteSupplier> getAllFavouriteSupplierByIdForExport(String loggedInUserTenantId);

	FavouriteSupplier getFavouriteSupplierBySupplierIdForReport(String supId, String buyerId);

	FavouriteSupplier getFavouriteSupplierBySupplierIdReport(String supId);

	public List<ProductCategory> getSupplierProductCategoryBySupIdORTenantId(String tenantId, String id);

	public List<FavouriteSupplier> getAllFavouriteSupplier(String buyerId);

	public FavouriteSupplier getFavouriteSupplierBySupplierId(String id);

	public List<FavouriteSupplier> getAllFavSupplierToInActive(String FavoriteSupplierDao);

	public List<EventSupplierPojo> favoriteEventSupplierPojosOfBuyerByIndusCategory(String loggedInUserTenantId, boolean includeIndustryCategories, String eventId, RfxTypes eventType, String search, boolean isMinMaxPresent);

	public long countFavoriteEventSupplierPojosOfBuyerByIndusCategory(String loggedInUserTenantId, boolean includeIndustryCategories, String eventId, RfxTypes eventType, String search, boolean isMinMaxPresent);

	public FavouriteSupplier findFavSupplierById(String id);

	public List<FavouriteSupplier> findAllFavouriteSupplierForSuspension();

	public List<FavouriteSupplier> findAllFavouriteSupplierToRemoveSuspension();

	public Supplier getSupplierByFavSupplierId(String favSupplierId);

	/**
	 * @param buyerId
	 * @return
	 */
	SupplierSearchPojo getTotalAndPendingSupplierCountForBuyer(String buyerId);

	public List<FavouriteSupplier> findInvitedSupplierByIndCat(String sId, Boolean select_all, SupplierSearchPojo supplierSearchPojo, String buyerId, List<IndustryCategory> industryCategories, Boolean exclusive, Boolean inclusive);

	public List<FavouriteSupplier> findInvitedSupplierBySuppId(String sId, Boolean select_all, SupplierSearchPojo supplierSearchPojo, String buyerId, Boolean exclusive, Boolean inclusive);

	/**
	 * @param supplierId
	 * @param buyerId
	 * @return
	 */
	public boolean isSelfInviteSupplierInFavouriteList(String supplierId, String buyerId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	public boolean existsEventCategoriesInSupplier(String eventId, String supplierId, RfxTypes eventType);

	/**
	 * @param supplierId
	 * @param minRating TODO
	 * @param maxRating TODO
	 * @param buyerId TODO
	 * @return
	 */
	public boolean isSupplierRatingMatchToEventRating(String supplierId, BigDecimal minRating, BigDecimal maxRating, String buyerId);

	List<EventSupplierPojo> favoriteEventSupplierPojosOfBuyerByIndusCategoryForAutoSave(String tenantId, boolean includeIndustryCategories, String eventId, RfxTypes eventType, String search, boolean isMinMaxPresent);

	List<IndustryCategoryPojo> getTopTwentyFiveCategory(String tanentId);

	public long getScheduledSupplier(String loggedInUserTenantId);

	/**
	 * @param tenantId
	 * @param includeIndustryCategories
	 * @param prId
	 * @param search
	 * @return
	 */
	long countConstructQueryToFetchSuppliersForPrSelection(String tenantId, boolean includeIndustryCategories, String prId, String search);

	/**
	 * @param tenantId
	 * @param includeIndustryCategories
	 * @param prId
	 * @param search
	 * @return
	 */
	List<EventSupplierPojo> favoritePrSupplierPojosOfBuyerByIndusCategory(String tenantId, boolean includeIndustryCategories, String prId, String search);

	/**
	 * @param vendorCode
	 * @param buyerId
	 * @return
	 */
	FavouriteSupplier getFavouriteSupplierByVendorCode(String vendorCode, String buyerId);

	/**
	 * @param supplierId
	 * @param tenantId
	 * @return
	 */
	String getFavouriteSupplierVendorCodeBySupplierIdAndTenant(String supplierId, String tenantId);

	List<FavouriteSupplier> getAllActiveFavouriteSupplierByTenantIdForAnnouncement(String tenantId);

	List<EventSupplierPojo> getAllFavSupplierToSendRfaEventInvitation(String tenantId, String eventId);

	/**
	 * @param buyerId
	 * @return
	 */
	public long getSupplierCountBasedOnStatusForBuyer(FavouriteSupplierStatus status, String buyerId);

	/**
	 * @param input
	 * @param tenantId
	 * @param status TODO
	 * @return
	 */
	public List<SupplierPojo> getFavSupplierListBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status);

	/**
	 * @param input
	 * @param tenantId
	 * @param status
	 * @return
	 */
	public long getFavSuppliersCountBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status);

	/**
	 * @param input
	 * @param tenantId
	 * @param status
	 * @return
	 */
	public List<RequestedAssociatedBuyerPojo> getAssociatedBuyerListBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status);

	/**
	 * @param input
	 * @param tenantId
	 * @param status
	 * @return
	 */
	public long getAssociatedBuyersCountBasedOnStatus(TableDataInput input, String tenantId, FavouriteSupplierStatus status);

	/**
	 * @param tenantId
	 * @param status
	 * @return
	 */
	public long getAssociatedBuyersCountForSupplierBasedOnStatus(String tenantId, FavouriteSupplierStatus status);

	/**
	 * @param input
	 * @param loggedInUserTenantId
	 * @return
	 */
	public List<RequestedAssociatedBuyerPojo> getAvailableBuyerList(TableDataInput input, String loggedInUserTenantId);

	/**
	 * @param input
	 * @param loggedInUserTenantId
	 * @return
	 */
	public long getAvailableBuyerListCount(TableDataInput input, String loggedInUserTenantId);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	public long getTotalPublishedAvailableBuyerList(String loggedInUserTenantId);

	/**
	 * @param buyerId
	 * @param loggedInUserLoginId
	 * @return
	 */
	public long isSupplierInBuyerFavList(String buyerId, String loggedInUserLoginId);

	/**
	 * @param input
	 * @param loggedInUserTenantId
	 * @return
	 */
	public List<SupplierPojo> getTotalSuppliersFromGlobalList(TableDataInput input, String loggedInUserTenantId);

	/**
	 * @param input
	 * @param loggedInUserTenantId
	 * @return
	 */
	public long getTotalSuppliersCountFromGlobalList(TableDataInput input, String loggedInUserTenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long getTotalSuppliersCountForBuyerGlobalList(String tenantId);

	/**
	 * @param input
	 * @param searchBuyerPojo
	 * @param loggedInUserTenantId
	 * @return
	 */
	public long searchBuyersCount(TableDataInput input, RequestedAssociatedBuyerPojo searchBuyerPojo, String loggedInUserTenantId);

	/**
	 * @param input
	 * @param searchBuyerPojo
	 * @param tenantId
	 * @return
	 */
	public List<RequestedAssociatedBuyerPojo> searchBuyers(TableDataInput input, RequestedAssociatedBuyerPojo searchBuyerPojo, String tenantId);

	/**
	 * @param supplierId
	 * @param buyerId
	 * @return
	 */
	public FavouriteSupplier getFavSupplierDetailsByBuyerAndSupplierId(String supplierId, String buyerId);

	/**
	 * @param favArr
	 * @param loggedInUserTenantId
	 * @param select_all
	 * @param searchFilterSupplierPojo
	 * @param status TODO
	 * @return
	 */
	public List<FavouriteSupplier> getAllFavouriteSupplierByIdAndStatus(String[] favArr, String loggedInUserTenantId, boolean select_all, SearchFilterSupplierPojo searchFilterSupplierPojo, FavouriteSupplierStatus status);

	/**
	 * @param tenantId
	 * @param status
	 * @return
	 */
	public long getAssociatedBuyersCountForSupplier(String tenantId);

	/**
	 * @param input
	 * @param tenantId
	 * @return
	 */
	public List<RequestedAssociatedBuyerPojo> getAssociatedBuyerListForSupplier(TableDataInput input, String tenantId);

	/**
	 * @param input
	 * @param tenantId
	 * @return
	 */
	public long getAssociatedBuyerCountForSupplier(TableDataInput input, String tenantId);

	/**
	 * @param searchParams
	 * @param buyerId
	 * @return
	 */
	FavouriteSupplier getFavouritSupplier(SupplierSearchPojo searchParams, String buyerId);

	/**
	 * @param tenantId
	 * @param search
	 * @param id
	 * @return
	 */
	public List<SupplierPojo> searchFavouriteSupplier(String tenantId, String search, String id);

	/**
	 * @param tenantId
	 * @param id
	 * @return
	 */
	public long countConstructQueryToFetchFavouriteSupplier(String tenantId, String id);

	/**
	 * @param supplierIds
	 * @return
	 */
	public List<FavouriteSupplier> getAllActiveFavouriteSupplierBySuppIds(String[] supplierIds);

	/**
	 * @param tenantId
	 * @return
	 */
	List<FavouriteSupplier> getAllActiveFavouriteSupplierByBuyerId(String tenantId);

	/**
	 * @param loggedInUserTenantId
	 * @param searchValue TODO
	 * @return
	 */
	public List<SupplierPojo> getAllSearchFavouriteSupplierByBuyerId(String loggedInUserTenantId, String searchValue);

	/**
	 * @param id
	 */
	public void deleteIndustryCatAndProductCat(String id);

	/**
	 * @param tenantId
	 * @return
	 */
	public long getTotalSupplierCountForList(String tenantId, String status);

	/**
	 * @param tenantId
	 * @param pAGE_SIZE
	 * @param pageNo
	 * @param favArr
	 * @param searchFilterSupplierPojo
	 * @param select_all
	 * @return
	 */
	public List<FavouriteSupplier> findAllActiveSupplierForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] favArr, SearchFilterSupplierPojo searchFilterSupplierPojo, boolean select_all);

	/**
	 * @param tenantId
	 * @param favStatus
	 * @return
	 */
	public long getTotalSupplierCountByIdAndStatusForList(String tenantId, FavouriteSupplierStatus favStatus);

	/**
	 * @param pageSize
	 * @param pageNo
	 * @param favArr
	 * @param searchFilterSupplierPojo
	 * @param select_all
	 * @param favStatus
	 * @param tenantId
	 * @return
	 */
	public List<FavouriteSupplier> findAllActiveSupplierByTenantIdAndStatusForCsv(int pageSize, int pageNo, String[] favArr, SearchFilterSupplierPojo searchFilterSupplierPojo, boolean select_all, FavouriteSupplierStatus favStatus, String tenantId);

	/**
	 * @param supplierId
	 * @return
	 */
	List<Buyer> findFavSupplierBySuppId(String supplierId);

	/**
	 * 
	 * @param vendorCode
	 * @param buyerId
	 * @return
	 */
	String getSupplierNameByVendorCode(String vendorCode, String buyerId);

	/**
	 * @param tenantId
	 * @param search
	 * @param id
	 * @return
	 */
	List<Supplier> searchBuyerSuppliers(String tenantId, String search, String id);

	/**
	 * @param id
	 * @param tenantId
	 * @return
	 */
	public FavouriteSupplier getSupplierDetailsBySupplierId(String id, String tenantId);

	/**
	 * @param id
	 * @param buyerId
	 * @return
	 */
	FavouriteSupplier getActiveFavouriteSupplierBySupplierIdForBuyer(String id, String buyerId);

	/**
	 * @param vendorCode
	 * @param buyerId
	 * @return
	 */
	Supplier getSupplierByVendorCodeAndBuyerId(String vendorCode, String buyerId);

	/**
	 * @param supplierId
	 * @param buyerId
	 * @return
	 */
	FavouriteSupplier getFavouriteSupplierBySupplierIdAndBuyerId(String supplierId, String buyerId);
}