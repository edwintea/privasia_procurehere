package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductCategoryPojo;
import com.privasia.procurehere.core.entity.ProductContractItems;
import com.privasia.procurehere.core.pojo.ProductContractItemsPojo;
import com.privasia.procurehere.core.pojo.ProductItemPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface ProductContractItemsService {

	/**
	 * @param itemId
	 * @return
	 */

	ProductContractItems findProductContractItemById(String itemId);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */

	List<ProductCategory> findProductContractByTenantIDSupplierID(String loggedInUserTenantId, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */

	List<ProductContractItemsPojo> findProductContractItemListForTenant(String loggedInUserTenantId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id TODO
	 * @return
	 */
	long findTotalFilteredProductItemListForTenant(String loggedInUserTenantId, TableDataInput input, String id);

	/**
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */
	long findTotalProductItemListForTenant(String loggedInUserTenantId, String id);

	ProductContractItems findProductContractItemByItemId(String itemId);

	/**
	 * @param item
	 */
	void saveContractItem(ProductContractItems item);

	/**
	 * @param contractItemId TODO
	 */
	void delete(String contractItemId);

	/**
	 * @param id
	 * @return
	 */
	List<ProductItemPojo> findProductContractItems(String id);

	/**
	 * @param id
	 * @return
	 */
	List<ProductCategoryPojo> findProductCategories(String id);
	
	/**
	 * 
	 * @param contractId
	 * @return
	 */
	List<ProductContractItems> findProductContractItemsByProductContractId(String contractId);

	/**
	 *
	 * @param productContractId
	 * @param contractItemNumber
	 * @return
	 */

	ProductContractItems findProductContractItemsByProductContractIdAndContractItemNumber(String productContractId, String contractItemNumber);

	/**
	 * @param productContractItems
	 */
	public void updateProductContractItems(ProductContractItems productContractItems);


	/**
	 *
	 * @param contractId
	 * @return
	 */
	List<ProductContractItems> findProductContractItemsByProductContractIdWithoutCondition(String contractId);

	/**
	 *
	 * @param contractId
	 * @return
	 */
	List<ProductContractItems> findProductContractItemsByProductContractIdIsDeleted(String contractId);

	/**
	 *
	 * @param contractId
	 */
	void deleteContractItemsForFailedProductContractTransaction(String contractId);
}
