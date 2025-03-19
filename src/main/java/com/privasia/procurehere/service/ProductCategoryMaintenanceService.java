package com.privasia.procurehere.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductCategoryPojo;
import com.privasia.procurehere.core.entity.ProductContractItems;
import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface ProductCategoryMaintenanceService {

	/**
	 * @param productCategoryMaintenance
	 * @return
	 */
	public String createProductCategory(ProductCategory productCategoryMaintenance);

	/**
	 * @param productCategoryMaintenance
	 */
	public void updateProductCategory(ProductCategory productCategoryMaintenance);

	/**
	 * @return
	 */
	List<ProductCategory> getAllProductCategory();

	/**
	 * @param id
	 * @return
	 */
	ProductCategory getProductCategoryById(String id);

	/**
	 * @param productCategoryMaintenance
	 * @param loggedInUser TODO
	 */
	public void deleteProductCategory(ProductCategory productCategoryMaintenance, User loggedInUser);

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
	public long findTotalFilteredProductCategoryForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	public List<ProductCategory> findProductCategoryForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return
	 */
	public long findTotalProductCategoryForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<ProductCategory> findAllActiveProductCategoryForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	public List<ProductCategory> getAllProductCategoryByTenantId(String tenantId);

	/**
	 * @param response
	 * @param loggedInUserTenantId
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void productCategoryDownloadTemplate(HttpServletResponse response, String tenantId) throws FileNotFoundException, IOException;

	/**
	 * @param file
	 * @param loggedInUserTenantId
	 * @param loggedInUser
	 * @throws Exception
	 */
	public void productCategoryUploadFile(MultipartFile file, String loggedInUserTenantId, User loggedInUser) throws Exception;

	public ProductItem checkProductItemExistOrNot(String itemName, String supplierId, String tenantId);

	ProductItem updateProductItemPrice(ProductItem productItem);

	ProductItem saveNewProductItem(ProductItem productItem);

	public List<ProductCategory> findProductCategoryByTenantIDSupplierID(String loggedInUserTenantId, String supplierId);

	List<ProductItem> findProductsBySupplierIdAndCategoryId(String loggedInUserTenantId, String supplierId, String productCategoryId);

	public List<ProductCategory> findProductsCategoryByTenantAndItemId(String loggedInUserTenantId, String itemId);

	public List<FavouriteSupplier> findFavSupplierByCategoryId(String productCategoryId, String supplierId);

	public List<ProductCategory> findProductsBytenantAndOpenSupplier(String loggedInUserTenantId);

	public List<ProductItem> searchProductsBySupplierIdAndCategoryId(String loggedInUserTenantId, String supplierId, String categoryId, String searchParam, String openSupplier);

	public List<ProductCategory> serchAllActiveProductCategoryForTenant(String loggedInUserTenantId, String search);

	/**
	 * @param code
	 * @param tenantId
	 * @return
	 */
	ProductCategory getProductCategoryByCodeAndTenantId(String code, String tenantId);

	List<FavouriteSupplier> findFavSupplierByCategoryListId(List<String> catList);

	public List<ProductItem> searchProductsBySupplierIdList(String loggedInUserTenantId, List<String> slist, String searchParam, List<String> catList);

	public ProductCategory getProductCategoryAndTenantId(String productCategory, String id);

	public boolean isExistsProductCategory(String productCode, String id);

	public ProductCategory findProductCategoryByCode(String productCode, String id);

	public String createProductCategory(ProductCategory productCatgory, Boolean true1);

	public void updateProductCategory(ProductCategory productCatgory, Boolean true1);

	public List<ProductContractItems> searchProductContractBySupplierIdList(String loggedInUserTenantId, List<String> slist, String searchParam, List<String> catList, String unitName);

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
	 * @param response
	 * @param tenantId
	 */
	public void prodCategoryDownloadTemplate(HttpServletResponse response, String tenantId);

	/**
	 * @param response
	 * @param file
	 * @param tenantId
	 */
	public void downloadProdCategoryCsvFile(HttpServletResponse response, File file, String tenantId);

	/**
	 * 
	 * @param tenantId
	 * @param searchValue TODO
	 * @return
	 */
	List<ProductCategory> getAllActiveProductCategoryForTenant(String tenantId, String searchValue);
}
