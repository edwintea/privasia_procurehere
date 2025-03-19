package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductCategoryPojo;
import com.privasia.procurehere.core.entity.ProductContractItems;
import com.privasia.procurehere.core.pojo.ProductContractItemsPojo;
import com.privasia.procurehere.core.pojo.ProductItemPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface ProductContractItemsDao extends GenericDao<ProductContractItems, String> {

	void logicalDelete(String contractItemId);

	/**
	 * @param itemId
	 * @param contractItemId TODO
	 * @return
	 */

	ProductContractItems findProductContractItemByItemId(String itemId, String contractItemId);

	/**
	 * @param loggedInUserTenantId
	 * @param supplierId
	 * @return
	 */

	List<ProductCategory> findProductContractByTenantIDSupplierID(String loggedInUserTenantId, String supplierId);

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

	ProductContractItems findProductContractItemById(String itemId);

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
	 * @param contractId
	 */
	List<ProductContractItems> findProductContractItemsByProductContractId(String contractId);

	/**
	 *
	 * @param contractId
	 * @return
	 */
	List<ProductContractItems> getAllByProductContractIdAndIsDeleted(String contractId);


	/**
	 * @param contractId
	 * @param contractItemId
	 * @param itemNumber
	 */
	void deleteItemAndRenumber(String contractId, String contractItemId, String itemNumber);

	/**
	 * @param contractId
	 */
	void updateErpTransfer(String contractId);

	/**
	 *
	 * @param productContractId
	 * @param contractItemNumber
	 * @return
	 */

	List<ProductContractItems> findProductContractItemsByProductContractIdAndContractItemNumber(String productContractId, String contractItemNumber);

	/**
	 *
	 * @param contractId
	 */
	void deleteContractItemsForFailedProductContractTransaction(String contractId);
}
