package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ProductItemPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface ProductListMaintenanceDao extends GenericDao<ProductItem, String> {

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	List<ProductItemPojo> findProductListForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredProductListForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalProductListForTenant(String tenantId);

	/**
	 * @param productCategoryId
	 * @return
	 */
	List<ProductItem> findProductListForByProductCategoryId(String productCategoryId);

	ProductItem findProductCategoryById(String id);

	/**
	 * @param productItem
	 * @param tenantId
	 * @param favSupplierId TODO
	 * @return
	 */
	List<ProductItem> findProductsByNameAndTenantAndFavSupplier(String productItem, String tenantId, String favSupplierId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<ProductItem> findProductsByTenantWithOutFavSupplier(String tenantId);

	/**
	 * @param productCode
	 * @param tenantId TODO
	 * @param productItemType TODO
	 * @return
	 */
	ProductItem findProductItemByCode(String productCode, String tenantId, ProductItemType productItemType);

	boolean isExists(String productCode, String buyerId, String productId);

	void setStatusInactiveForPastProducts();

	/**
	 * @param tenantId
	 * @return
	 */
	public List<ProductItem> getAllProductItemsByTenantId(String tenantId);

	/**
	 * @param productCode
	 * @param tenantId
	 * @return
	 */
	ProductItem getByProductCode(String productCode, String tenantId);

	List<ProductItem> findProductsByTenantId(String tenantId);

	List<ProductItemPojo> getAllProductItemsByTenantIdForDownloadExcel(String tenantId);

	/**
	 * @param tenantId
	 * @param ids
	 * @return
	 */
	List<ProductItemPojo> getAllProductItemsByTenantIdAndIdsForDownloadExcel(String tenantId, List<String> ids);

	boolean isExistsproductCode(String productCode, String buyerId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<ProductItemPojo> getAllProductItemsForDownloadByTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @param status
	 * @param pageSize TODO
	 * @param pageNo TODO
	 * @return
	 */
	List<ProductItemPojo> findAllActiveProductItemForTenantId(String tenantId, Status status, int pageSize, int pageNo);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalProductItems(String tenantId);

	/**
	 * @param tenantId
	 * @param search
	 * @return
	 */
	List<ProductItemPojo> fetchAllActiveProductItemForTenant(String tenantId, String search);

	/**
	 * @param tenantId
	 * @return
	 */
	long countConstructQueryToFetchProductItem(String tenantId);

	/**
	 * 
	 * @param tenantId
	 * @param search
	 * @return
	 */
	List<ProductItemPojo> fetchAllActiveProductItemNameAndCodeForTenant(String tenantId, String search);

}
