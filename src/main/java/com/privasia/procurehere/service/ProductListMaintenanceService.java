package com.privasia.procurehere.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.pojo.ProductItemPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface ProductListMaintenanceService {

	/**
	 * @param productListMaintenance
	 * @param actionByErp TODO
	 * @return
	 */
	public String createProductItem(ProductItem productListMaintenance, Boolean actionByErp);

	/**
	 * @param productListMaintenance
	 * @param actionByErp TODO
	 */
	public void updateProductItem(ProductItem productListMaintenance, Boolean actionByErp);

	/**
	 * @param id
	 * @return
	 */
	ProductItem getProductCategoryById(String id);

	ProductItem getProductItemByPrItemId(String id);

	/**
	 * @param productListMaintenance
	 */
	public void deleteProductCategory(ProductItem productListMaintenance);

	/**
	 * @param buyerId TODO
	 * @param productId TODO
	 * @param ProductCode
	 * @return
	 */
	public boolean isExists(String productCode, String buyerId, String productId);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	public List<ProductItemPojo> findProductListForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	public long findTotalFilteredProductListForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return
	 */
	public long findTotalProductListForTenant(String tenantId);

	/**
	 * @param productCategoryId
	 * @return
	 */
	List<ProductItem> findProductListForByProductCategoryId(String productCategoryId);

	ProductItem getProductCategoryForPrById(String id);

	/**
	 * @param productItem
	 * @param tenantId
	 * @param supplierId TODO
	 * @return
	 */
	public List<ProductItem> findProductsByNameAndTenantAndFavSupplier(String productItem, String tenantId, String supplierId);

	/**
	 * @param tenantId
	 * @return
	 */
	public List<ProductItem> findProductsByTenantWithOutFavSupplier(String tenantId);

	/**
	 * @param productrCode
	 * @param tenantId TODO
	 * @param productItemType TODO
	 * @return
	 */
	public ProductItem findProductItemByCode(String productrCode, String tenantId, ProductItemType productItemType);

	/**
	 * @param id
	 * @return
	 */
	ProductItem getProductCategoryForDownloadAttachmentById(String id);

	/**
	 * @param tenantId
	 * @return
	 */
	public List<ProductItem> getAllProductItemsByTenantId(String tenantId);

	/**
	 * @param response
	 * @param loggedInUserTenantId
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void productDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId) throws FileNotFoundException, IOException;

	/**
	 * @param file
	 * @param loggedInUserTenantId
	 * @param loggedInUser
	 * @throws Exception
	 */
	public void productItemUploadFile(MultipartFile file, String loggedInUserTenantId, User loggedInUser) throws Exception;

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	public List<ProductItem> findProductsByTenantId(String loggedInUserTenantId);

	/**
	 * @param itemId
	 * @return
	 */
	public ProductItem findProductItembyId(String itemId);

	/**
	 * @param response
	 * @param loggedInUserTenantId
	 * @param type TODO
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void productListDownload(HttpServletResponse response, String loggedInUserTenantId, Integer type) throws FileNotFoundException, IOException;

	/**
	 * @param tenantId
	 * @param ids
	 * @return
	 */
	List<ProductItemPojo> getAllProductItemsByTenantIdAndIdsForDownloadExcel(String tenantId, List<String> ids);

	/**
	 * @param response
	 * @param tenantId
	 * @param ids
	 * @param type TODO
	 * @return TODO
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	HttpServletResponse productListDownload(HttpServletResponse response, String tenantId, List<String> ids, Integer type) throws FileNotFoundException, IOException;

	/**
	 * @param productCode
	 * @param id
	 * @return
	 */
	public boolean isExistsproductCode(String productCode, String id);

	/**
	 * @param response
	 * @param file
	 * @param tenantId TODO
	 */
	public void downloadCsvFileForProductList(HttpServletResponse response, File file, String tenantId);

	/**
	 * @param response
	 * @param tenantId
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void downloadProductItemTemplate(HttpServletResponse response, String tenantId) throws FileNotFoundException, IOException;

	/**
	 * @param loggedInUserTenantId
	 * @param search
	 * @return
	 */
	public List<ProductItemPojo> fetchAllProductItemForTenant(String loggedInUserTenantId, String search);

	/**
	 * @param tenantId
	 * @param search
	 * @return
	 */
	List<ProductItemPojo> fetchAllActiveProductItemNameAndCodeForTenant(String tenantId, String search);

}
