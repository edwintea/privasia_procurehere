package com.privasia.procurehere.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NoRollBackException;
import com.privasia.procurehere.core.exceptions.WarningException;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo;
import com.privasia.procurehere.core.pojo.SearchFilterSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.pojo.SupplierActivationIntegrationPojo;
import com.privasia.procurehere.core.pojo.SupplierCountPojo;
import com.privasia.procurehere.core.pojo.SupplierIntigrationPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.SupplierSuspendIntegrationPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Javed Ahmed
 */
public interface FavoriteSupplierService {

	/**
	 * @param favouriteSupplier
	 */
	public FavouriteSupplier saveFavoriteSupplier(FavouriteSupplier favouriteSupplier, User user);

	public void deleteFavoriteSupplier(FavouriteSupplier favouriteSupplier, User user) throws ApplicationException;

	public FavouriteSupplier updateFavoriteSupplier(FavouriteSupplier favouriteSupplier, User user, boolean auditTrailFlagStatus);

	/*
	 * 
	 * 
	 */
	public List<SupplierPojo> searchSuppliers(TableDataInput input, SupplierSearchPojo searchParams, String tenantId);

	public List<FavouriteSupplier> favoriteSuppliersOfBuyer(String buyerId, List<Supplier> invitedList, BigDecimal minGrade, BigDecimal maxGrade);

	public List<Supplier> searchSupplierz(SupplierSearchPojo searchParams);

	boolean isExists(FavouriteSupplier favouriteSupplier);

	public List<FavouriteSupplier> searchFavSuppliers(SupplierSearchPojo searchParams);

	public List<FavouriteSupplier> searchFavSuppliersOfIndCat(String icId);

	public FavouriteSupplier findFavSupplierBySuppId(String sId, String buyerId);

	public List<Supplier> searchSupplierInFavByOder(SearchVo searchVo);

	public List<FavouriteSupplier> getAllFavouriteSupplier(String buyerId);

	/**
	 * @param supId
	 * @param buyerId TODO
	 * @return
	 */
	FavouriteSupplier getFavouriteSupplierBySupplierId(String supId, String buyerId);

	/**
	 * @param searchParam
	 * @param buyerId TODO
	 * @param invitedList TODO
	 * @return
	 */
	List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId, List<String> invitedList);

	/**
	 * @param suppliers
	 * @return
	 */
	List<FavouriteSupplier> getFavouriteSupplierBySupplierId(List<Supplier> suppliers);

	/**
	 * @param sId
	 * @param buyerId TODO
	 * @return
	 */
	FavouriteSupplier findFavSupplierByFavSuppId(String sId, String buyerId);

	/**
	 * @param search TODO
	 * @param tenantId
	 * @return
	 */
	public List<FavouriteSupplier> favSuppliersByNameAndTenant(String search, String tenantId);

	/**
	 * @param suppId
	 * @param buyerId TODO
	 * @return
	 */
	public long totalEventInvitedSupplier(String suppId, String buyerId);

	/**
	 * @param suppId
	 * @param buyerId TODO
	 * @return
	 */
	public long totalEventParticipatedSupplier(String suppId, String buyerId);

	/**
	 * @param suppId
	 * @return
	 */
	public long totalEventAwardedSupplier(String suppId);

	Integer favoriteSuppliersOfBuyerCount(String buyerId, List<Supplier> invitedList);

	void supplierListUpload(String tenantId, File convFile, boolean isUploadNewSupplier) throws ExcelParseException, ApplicationException, NoRollBackException, WarningException;

	Supplier isSupplierInGlobalList(Country country, String registrationNo, String companyName);

	boolean isSupplierInFavouriteList(Supplier supplier, String buyerId);

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
	public List<SupplierCountPojo> getCurrentSupplierCountForTopFiveCategories(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	public List<FavouriteSupplier> getAllActiveFavouriteSupplierByTenantId(String tenantId);

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
	 * @param eventCategoryList
	 * @return
	 */
	List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId, List<String> invitedList, List<IndustryCategory> eventCategoryList);

	public FavouriteSupplier findFavSupplierByFavSupplierIdForDefault(String supplierId);

	public Supplier isSupplierInRegNoandCompany(Country country, String registrationNo, String companyName);

	public List<FavouriteSupplier> getAllFavouriteSupplierById(String[] favArr, String loggedInUserTenantId, boolean select_all, SearchFilterSupplierPojo searchFilterSupplierPojo);

	public JasperPrint getSupplierProfilePdf(String tenantId, String supplierId, HttpSession session);

	JasperPrint getSupplierProfilePdfForAll(String supplierId, HttpSession session, String tenantId, String teanantType);

	public List<String> addSupplierTo(List<SupplierIntigrationPojo> supplier, String tenantId);

	public List<EventSupplierPojo> favoriteEventSupplierPojosOfBuyerByIndusCategory(String loggedInUserTenantId, boolean includeIndustryCategories, String eventId, RfxTypes eventType, String search);

	public FavouriteSupplier findFavSupplierById(String id);

	public List<FavouriteSupplier> findAllFavouriteSupplierForSuspension();

	public Supplier getSupplierByFavSupplierId(String favSuppId);

	/**
	 * @param buyerId
	 * @return
	 */
	SupplierSearchPojo getTotalAndPendingSupplierCountForBuyer(String buyerId);

	public FavouriteSupplier saveFavoriteSuppliers(FavouriteSupplier favouriteSupplier);

	public List<FavouriteSupplier> findInvitedSupplierByIndCat(String checkString, Boolean select_all, SupplierSearchPojo supplierSearchPojo, String loggedInUserTenantId, List<IndustryCategory> industryCategories, Boolean exclusive, Boolean inclusive);

	public List<FavouriteSupplier> findInvitedSupplierBySuppId(String checkString, Boolean select_all, SupplierSearchPojo supplierSearchPojo, String loggedInUserTenantId, Boolean exclusive, Boolean inclusive);

	/**
	 * @param supplierId
	 * @param buyerId
	 * @return
	 */
	public boolean isSelfInviteSupplierInFavouriteList(String supplierId, String buyerId);

	/**
	 * @param eventId
	 * @param loggedInUserTenantId
	 * @param eventType
	 * @return
	 */
	public boolean existsEventCategoriesInSupplier(String eventId, String supplierId, RfxTypes eventType);

	/**
	 * @param minRating TODO
	 * @param maxRating TODO
	 * @param buyerId TODO
	 * @param loggedInUserTenantId
	 * @return
	 */
	public boolean isSupplierRatingMatchToEventRating(String supplierId, BigDecimal minRating, BigDecimal maxRating, String buyerId);

	List<EventSupplierPojo> favoriteEventSupplierPojosOfBuyerByIndusCategoryForAutoSave(String loggedInUserTenantId, boolean includeIndustryCategories, String eventId, RfxTypes eventType, String search);

	List<IndustryCategoryPojo> getTopTwentyFiveCategory(String tanentId);

	long getTotalAwardedSupplirForBuyer(String suppId, String tenantId);

	public long getScheduledSupplier(String loggedInUserTenantId);

	/**
	 * @param tenantId
	 * @param filterByIndustryCategory
	 * @param prId
	 * @param search
	 * @return
	 */
	List<EventSupplierPojo> searchPrSuppliers(String tenantId, boolean filterByIndustryCategory, String prId, String search);

	/**
	 * @param vendorCode
	 * @param buyerId
	 * @return
	 */
	FavouriteSupplier getFavouriteSupplierByVendorCode(String vendorCode, String buyerId);

	public FavouriteSupplier updateFavoriteSupplier(FavouriteSupplier favouriteSupplier);

	public List<FavouriteSupplier> getAllActiveFavouriteSupplierByTenantIdForAnnouncement(String buyerId);

	/**
	 * @param favouriteSupplier
	 * @return
	 */
	FavouriteSupplier saveRequestedFavoriteSupplier(FavouriteSupplier favouriteSupplier);

	/**
	 * @param status
	 * @param loggedInUserTenantId
	 * @return
	 */
	public long getSupplierCountBasedOnStatusForBuyer(FavouriteSupplierStatus status, String loggedInUserTenantId);

	/**
	 * @param input
	 * @param loggedInUserTenantId
	 * @param status TODO
	 * @return
	 */
	public List<SupplierPojo> getFavSupplierListBasedOnStatus(TableDataInput input, String loggedInUserTenantId, FavouriteSupplierStatus status);

	/**
	 * @param input
	 * @param loggedInUserTenantId
	 * @param pending
	 * @return
	 */
	public long getFavSuppliersCountBasedOnStatus(TableDataInput input, String loggedInUserTenantId, FavouriteSupplierStatus pending);

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
	 * @param searchBuyerPojo
	 * @param tenantId
	 * @return
	 */
	public List<RequestedAssociatedBuyerPojo> searchBuyers(TableDataInput input, RequestedAssociatedBuyerPojo searchBuyerPojo, String tenantId);

	/***
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
	 * @param loggedInUserTenantId
	 * @return
	 */
	public long getTotalSuppliersCountForBuyerGlobalList(String loggedInUserTenantId);

	/**
	 * @param input
	 * @param searchBuyerPojo
	 * @param loggedInUserTenantId
	 * @return
	 */
	public long searchBuyersCount(TableDataInput input, RequestedAssociatedBuyerPojo searchBuyerPojo, String loggedInUserTenantId);

	/**
	 * @param supplier
	 * @param buyer
	 * @param timeZone
	 */
	public void sendEmailToFavSupplier(Supplier supplier, Buyer buyer, TimeZone timeZone);

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
	 * @return
	 */
	public long getAssociatedBuyersCountForSupplier(String tenantId);

	/**
	 * @param input
	 * @param loggedInUserTenantId
	 * @return
	 */
	public List<RequestedAssociatedBuyerPojo> getAssociatedBuyerListForSupplier(TableDataInput input, String loggedInUserTenantId);

	/**
	 * @param input
	 * @param loggedInUserTenantId
	 * @return
	 */
	public long getAssociatedBuyerCountForSupplier(TableDataInput input, String loggedInUserTenantId);

	/**
	 * @param searchParams
	 * @param buyerId
	 * @return
	 */
	FavouriteSupplier getFavouritSupplier(SupplierSearchPojo searchParams, String buyerId);

	/**
	 * @param suppliers
	 * @param tenantId
	 * @return
	 */
	List<String> suspendSuppliers(List<SupplierSuspendIntegrationPojo> suppliers, String tenantId);

	/**
	 * @param suppliers
	 * @param tenantId
	 * @return
	 */
	List<String> blockListSuppliers(List<SupplierSuspendIntegrationPojo> suppliers, String tenantId);

	/**
	 * @param suppliers
	 * @param tenantId
	 * @return
	 */
	List<String> releaseSuppliers(List<SupplierSuspendIntegrationPojo> suppliers, String tenantId);

	/**
	 * @param suppliers
	 * @param tenantId
	 * @return
	 */
	List<String> unSuspendSuppliers(List<SupplierSuspendIntegrationPojo> suppliers, String tenantId);

	/**
	 * @param tenantId
	 * @param search
	 * @param id
	 * @return
	 */
	public List<SupplierPojo> searchFavouriteSupplier(String tenantId, String search, String id);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	public List<FavouriteSupplier> getAllActiveFavouriteSupplierByBuyerId(String loggedInUserTenantId);

	/**
	 * @param loggedInUserTenantId
	 * @param searchValue TODO
	 * @return
	 */
	public List<SupplierPojo> getAllSearchFavouriteSupplierByBuyerId(String loggedInUserTenantId, String searchValue);

	/**
	 * @param suppliers
	 * @param tenantId
	 * @return
	 */
	List<String> inActivateSuppliers(List<SupplierActivationIntegrationPojo> suppliers, String tenantId);

	/**
	 * @param suppliers
	 * @param tenantId
	 * @return
	 */
	List<String> activateSuppliers(List<SupplierActivationIntegrationPojo> suppliers, String tenantId);

	/**
	 * @param response
	 * @param file
	 * @param favArr
	 * @param searchFilterSupplierPojo
	 * @param select_all
	 * @param tenantId
	 */
	public void downloadCsvFileForSupplierList(HttpServletResponse response, File file, String[] favArr, SearchFilterSupplierPojo searchFilterSupplierPojo, boolean select_all, String tenantId, HttpSession session);

	/**
	 * @param response
	 * @param file
	 * @param favArr
	 * @param searchFilterSupplierPojo
	 * @param select_all_supp
	 * @param favStatus
	 * @param tenantId
	 */
	public void downloadCsvFileForSupplierList(HttpServletResponse response, File file, String[] favArr, SearchFilterSupplierPojo searchFilterSupplierPojo, boolean select_all, FavouriteSupplierStatus favStatus, String tenantId, HttpSession session);

	/**
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

}