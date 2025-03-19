package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ProductContractItemsDao;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductCategoryPojo;
import com.privasia.procurehere.core.entity.ProductContractItems;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.pojo.ProductContractItemsPojo;
import com.privasia.procurehere.core.pojo.ProductItemPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.service.ProductContractItemsService;

@Service
@Transactional(readOnly = true)
public class ProductContractItemsServiceImpl implements ProductContractItemsService {

	@Autowired
	ProductContractItemsDao productContractItemsDao;

	@Override
	@SuppressWarnings("unused")
	public ProductContractItems findProductContractItemByItemId(String itemId) {
		ProductContractItems item = productContractItemsDao.findProductContractItemByItemId(itemId, null);
		if (item.getProductContract() != null) {
			item.getProductContract().getSupplier().getSupplier().getCompanyName();
		}
		if (item.getProductCategory() != null)
			item.getProductCategory().getProductCode();
		if (item.getUom() != null)
			item.getUom().getUom();

		return item;

	}

	@Override
	public ProductContractItems findProductContractItemById(String itemId) {
		ProductContractItems item = productContractItemsDao.findProductContractItemById(itemId);

		if (item.getProductItem() != null) {
			item.getProductItem().getId();
		}
		if (item.getUom() != null) {
			item.getUom().getId();
		}
		if (item.getBusinessUnit() != null) {
			item.getBusinessUnit().getId();
		}
		if (item.getCostCenter() != null) {
			item.getCostCenter().getId();
		}
		return item;
	}

	@Override
	public List<ProductCategory> findProductContractByTenantIDSupplierID(String loggedInUserTenantId, String supplierId) {
		List<ProductCategory> pcList = productContractItemsDao.findProductContractByTenantIDSupplierID(loggedInUserTenantId, supplierId);
		if (pcList != null) {
			for (ProductCategory bpc : pcList) {
				if (bpc != null) {
					bpc.setCreatedBy(null);
					bpc.setModifiedBy(null);
				}
			}
		}
		return pcList;
	}

	@Override
	public List<ProductContractItemsPojo> findProductContractItemListForTenant(String loggedInUserTenantId, TableDataInput input, String id) {
		return productContractItemsDao.findProductContractItemListForTenant(loggedInUserTenantId, input, id);
	}

	@Override
	public long findTotalFilteredProductItemListForTenant(String loggedInUserTenantId, TableDataInput input, String id) {
		return productContractItemsDao.findTotalFilteredProductItemListForTenant(loggedInUserTenantId, input, id);
	}

	@Override
	public long findTotalProductItemListForTenant(String loggedInUserTenantId, String id) {
		return productContractItemsDao.findTotalProductItemListForTenant(loggedInUserTenantId, id);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveContractItem(ProductContractItems item) {
		productContractItemsDao.saveOrUpdate(item);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(String contractItemId) {
		ProductContractItems item = findProductContractItemById(contractItemId);
		if (item.getProductContract().getStatus() == ContractStatus.DRAFT || (Boolean.TRUE == item.getProductContract().getErpTransferred() && Boolean.FALSE == item.getErpTransferred())) {
			// Physical delete in DRAFT mode
			String itemNumber = item.getContractItemNumber();
			String contractId = item.getProductContract().getId();
			productContractItemsDao.deleteItemAndRenumber(contractId, contractItemId, itemNumber);
		} else {
			// DO NOT DO PHYSICAL DELETE OF CONTRACT ITEM. DO A LOGICAL DELETE
			// PH-2939 - FGV I Delete Indicator in Contract Line Item
			productContractItemsDao.logicalDelete(contractItemId);
		}
	}

	@Override
	public List<ProductItemPojo> findProductContractItems(String id) {
		return productContractItemsDao.findProductContractItems(id);
	}

	@Override
	public List<ProductCategoryPojo> findProductCategories(String id) {
		return productContractItemsDao.findProductCategories(id);
	}

	@Override
	public List<ProductContractItems> findProductContractItemsByProductContractId(String contractId) {
		List<ProductContractItems> itemList = productContractItemsDao.findProductContractItemsByProductContractId(contractId);
		for (ProductContractItems item : itemList) {
			if (item.getUom() != null) {
				item.getUom().getUom();
			}
			if (item.getProductCategory() != null) {
				item.getProductCategory().getProductCode();
			}
		}
		return itemList;
	}

	@Override
	public ProductContractItems findProductContractItemsByProductContractIdAndContractItemNumber(String productContractId, String contractItemNumber) {
		List<ProductContractItems> productContractItemsList = productContractItemsDao.
				findProductContractItemsByProductContractIdAndContractItemNumber(productContractId, contractItemNumber);
		if (productContractItemsList != null && productContractItemsList.size() > 0) {
			return productContractItemsList.get(0);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateProductContractItems(ProductContractItems productContractItems) {
		productContractItemsDao.update(productContractItems);
	}

	@Override
	public List<ProductContractItems> findProductContractItemsByProductContractIdWithoutCondition(String contractId) {
		List<ProductContractItems> itemList = productContractItemsDao.findProductContractItemsByProductContractId(contractId);
		return itemList;
	}

	@Override
	public List<ProductContractItems> findProductContractItemsByProductContractIdIsDeleted(String contractId) {
		List<ProductContractItems> itemList = productContractItemsDao.getAllByProductContractIdAndIsDeleted(contractId);
		return itemList;
	}

	@Override
	public void deleteContractItemsForFailedProductContractTransaction(String contractId) {
		productContractItemsDao.deleteContractItemsForFailedProductContractTransaction(contractId);
	}

}