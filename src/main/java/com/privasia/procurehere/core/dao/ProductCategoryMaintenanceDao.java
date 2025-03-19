package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductCategoryPojo;
import com.privasia.procurehere.core.entity.ProductContractItems;
import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface ProductCategoryMaintenanceDao extends GenericDao<ProductCategory, String> {

	List<ProductCategory> findAllActiveProductCategoryForTenant(String tenantId);

	/**
	 * @param productCategoryMaintenance
	 * @return
	 */
	public boolean isExists(ProductCategory productCategoryMaintenance, String buyerId);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredProductCategoryForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	List<ProductCategory> findProductCategoryForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalProductCategoryForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<ProductCategory> getAllProductCategoryByTenantId(String tenantId);

	/**
	 * @param productCode
	 * @param tenantId
	 * @return
	 */
	ProductCategory getByProductCategoryByCode(String productCode, String tenantId);

	ProductItem checkProductItemExistOrNot(String itemName, String supplierId, String tenantId);

	List<ProductCategory> findProductCategoryByTenantIDSupplierID(String loggedInUserTenantId, String supplierId);

	List<ProductItem> findProductsBySupplierIdAndCategory(String tenantId, String favSupplierId, String productCategoryId);

	List<ProductCategory> findProductsCategoryByTenantAndItemId(String loggedInUserTenantId, String itemId);

	List<ProductItem> findProductsBySupplierIdAndCategoryId(String tenantId, String favSupplierId, String productCategoryId);

	List<FavouriteSupplier> findFavSupplierByCategoryId(String productCategoryId, String supplierId);

	List<ProductCategory> findProductsBytenantAndOpenSupplier(String loggedInUserTenantId);

	List<ProductItem> searchProductsBySupplierIdAndCategoryId(String loggedInUserTenantId, String supplierId, String categoryId, String searchParam, String openSupplier);

	List<ProductCategory> serchAllActiveProductCategoryForTenant(String loggedInUserTenantId, String search);

	List<FavouriteSupplier> findFavSupplierByCategoryListId(List<String> catList);

	List<ProductItem> searchProductsBySupplierIdList(String loggedInUserTenantId, List<String> slist, String searchParam, List<String> catList);

	ProductCategory getProductCategoryCodeAndTenantId(String productCategoryCode, String id);

	boolean isExistsProductCategory(String productCode, String buyerId);

	ProductCategory findProductCategoryByCode(String productCode, String id);

	List<ProductContractItems> searchProductContractBySupplierIdList(String loggedInUserTenantId, List<String> slist, String searchParam, List<String> catList, String unitName);

	/**
	 * @param catList
	 * @return
	 */
	List<EventSupplierPojo> findFavSupplierByCategoryListForPr(List<String> catList);

	/**
	 * @param tenantId
	 * @param search
	 * @return
	 */
	List<ProductCategoryPojo> fetchAllActiveProductCategoryForTenant(String tenantId, String search);

	/**
	 * @param tenantId
	 * @return
	 */
	long countConstructQueryToFetchProductCategory(String tenantId);

	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	List<ProductCategory> getAllProductCategoryByTenantIdForCsv(String tenantId, int pageSize, int pageNo);

	/**
	 * @param tenantId
	 * @return
	 */
	long findProductCategoryCountForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @param searchValue TODO
	 * @return
	 */
	List<ProductCategory> getAllActiveProductCategoryForTenant(String tenantId, String searchValue);

	/**
	 * @param tenantId
	 * @param searchValue
	 * @return
	 */
	long findActiveProductCategoryForTenant(String tenantId, String searchValue);

}
