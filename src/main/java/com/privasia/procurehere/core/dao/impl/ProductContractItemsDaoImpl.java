package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ProductContractItemsDao;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductCategoryPojo;
import com.privasia.procurehere.core.entity.ProductContractItems;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.ProductContractItemsPojo;
import com.privasia.procurehere.core.pojo.ProductItemPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ProductContractItemsDaoImpl extends GenericDaoImpl<ProductContractItems, String> implements ProductContractItemsDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Override
	public void logicalDelete(String contractItemId) {
		StringBuilder hsql = new StringBuilder("update ProductContractItems pc set pc.deleted = :deleted where pc.id = :contractItemId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("contractItemId", contractItemId);
		query.setParameter("deleted", Boolean.TRUE);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProductContractItems findProductContractItemByItemId(String itemId, String contractItemId) {

		StringBuilder hql = new StringBuilder(" select distinct(t) from ProductContractItems as t inner join t.productContract pc left outer join pc.supplier as fs left outer join fs.supplier as sss where ");

		if (StringUtils.checkString(itemId).length() != 0) {
			hql.append(" t.productItem.id = :productItem ");
		}
		if (StringUtils.checkString(contractItemId).length() != 0) {
			hql.append(" and t.id = :contractItemId ");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(itemId).length() != 0) {
			query.setParameter("productItem", itemId);
		}
		if (StringUtils.checkString(contractItemId).length() != 0) {
			query.setParameter("contractItemId", contractItemId);
		}
		List<ProductContractItems> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProductContractItems findProductContractItemById(String itemId) {

		StringBuilder hql = new StringBuilder(" select distinct(t) from ProductContractItems as t inner join fetch t.productContract pc left outer join fetch pc.supplier as fs left outer join fetch fs.supplier as sss left outer join fetch t.uom uoms left outer join fetch t.productCategory pct where ");

		if (StringUtils.checkString(itemId).length() != 0) {
			hql.append(" t.id = :productItem ");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(itemId).length() != 0) {
			query.setParameter("productItem", itemId);

		}
		List<ProductContractItems> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategory> findProductContractByTenantIDSupplierID(String tenantId, String favSupplierId) {

		String hql = "select  distinct(t.productCategory) from FavouriteSupplier as t inner join t.productCategory where t.buyer.id = :tenantId ";
		if (StringUtils.checkString(favSupplierId).length() > 0) {
			hql += " AND t.id = :favSupplierId ";
		} else {
			hql = "select distinct(t.productCategory) from ProductContractItems as t inner join t.productContract pc left outer join t.productCategory where pc.buyer.id = :tenantId ";
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(favSupplierId).length() > 0) {
			query.setParameter("favSupplierId", favSupplierId);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductContractItemsPojo> findProductContractItemListForTenant(String loggedInUserTenantId, TableDataInput tableParams, String id) {
		final Query query = constructProductListForTenantQuery(loggedInUserTenantId, tableParams, false, id);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		List<Object[]> objects = query.getResultList();
		List<ProductContractItemsPojo> productContractItemsPojos = new ArrayList<>();
		objects.forEach(list -> {
			ProductContractItemsPojo productContractItemsPojo = new ProductContractItemsPojo();
			productContractItemsPojo.setId(list[0].toString());
			productContractItemsPojo.setContractItemNumber(list[1].toString());
			productContractItemsPojo.setProductName(list[2] != null ? list[2].toString() : "");
			productContractItemsPojo.setProductCode(list[3] != null ? list[3].toString() : "");
			productContractItemsPojo.setQuantity(list[4] != null ? BigDecimal.valueOf(Double.valueOf(list[4].toString())) : BigDecimal.ZERO);
			productContractItemsPojo.setBalanceQuantity(list[5] != null ? BigDecimal.valueOf(Double.valueOf(list[5].toString())) : BigDecimal.ZERO);
			productContractItemsPojo.setUnitPrice(BigDecimal.valueOf(Double.valueOf(list[6].toString())));
			productContractItemsPojo.setStorageLoc(list[7] != null ? list[7].toString() : "");
			productContractItemsPojo.setUom(list[8] != null ? list[8].toString() : "");
			productContractItemsPojo.setBusinessUnitName(list[9] != null ? list[9].toString(): "");
			productContractItemsPojo.setCostCenter(list[10] != null ? list[10].toString() : "");
			try {
				productContractItemsPojo.setContractStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(list[11].toString()) );
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			productContractItemsPojo.setTax(list[12] != null ? BigDecimal.valueOf(Double.valueOf(list[12].toString())) : BigDecimal.ZERO);
			productContractItemsPojo.setBrand(list[13] != null ? list[13].toString() : "");
			productContractItemsPojo.setProductCode(list[14] != null ? list[14].toString() : "");
			productContractItemsPojo.setProductName(list[15] != null ? list[15].toString() : "");
			productContractItemsPojo.setItemType((ProductItemType) list[16]);
			productContractItemsPojo.setDecimal(list[17] != null ? list[17].toString() : "");
			productContractItemsPojo.setItemName(list[18] != null ? list[18].toString() : "");
			productContractItemsPojo.setItemCode(list[19] != null ? list[19].toString() : "");
			productContractItemsPojo.setFreeTextItemEntered(list[20].toString().equals("1") ? Boolean.TRUE : Boolean.FALSE);
			productContractItemsPojo.setCostCenterDescription(list[21] != null ? list[21].toString() : "");
			productContractItemsPojo.setDeleted(list[22].toString().equals("1") ? Boolean.TRUE : Boolean.FALSE);
			productContractItemsPojos.add(productContractItemsPojo);
		});
		return productContractItemsPojos;
	}

	private Query constructProductListForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount, String id) {

		List<OrderParameter> orderList = tableParams.getOrder();
		String orderSql="";
		String querySql = "";
		if(!isCount ){
			if( CollectionUtil.isNotEmpty(orderList)){
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("uom")) {
						orderSql += " um.uom " + dir + ",";
						querySql += " um.uom " + ",";
					} else if (orderColumn.equals("itemName")) {
						orderSql += " t.itemName " + dir + ",";
						querySql += " t.itemName " + ",";
					} else if (orderColumn.equals("contractItemNumber")) {
						orderSql += " cast(t.contractItemNumber as int) " + dir + ",";
						querySql += " cast(t.contractItemNumber as int) " + ",";
					} else if (orderColumn.equals("itemCode")) {
						orderSql += " i.productCode " + dir + ",";
						querySql += " i.productCode " + ",";
					} else if (orderColumn.equals("storageLoc")) {
						orderSql += " t.storageLocation " + dir + ",";
						querySql += " t.storageLocation " + ",";
					} else if (orderColumn.equals("itemType")) {
						orderSql += " t.productItemType " + dir + ",";
						querySql += " t.productItemType " + ",";
					} else {
						orderSql += " t." + orderColumn + " " + dir + ",";
						querySql += " t." + orderColumn + ",";
					}
				}
				if (orderSql.lastIndexOf(",") == orderSql.length() - 1) {
					orderSql = orderSql.substring(0, orderSql.length() - 1);
					querySql = querySql.substring(0, querySql.length() - 1);
				}
			}else {
				orderSql ="cast(t.contractItemNumber as int) ";
				querySql ="cast(t.contractItemNumber as int) ";
			}
		}
		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct t) ";
		}
		if (!isCount) {
			hql += "select distinct t.id, t.contractItemNumber, i.productName, i.productCode, t.quantity, t.balanceQuantity, t.unitPrice, t.storageLocation, um.uom, bu.unitName, c.costCenter, pc.contractStartDate, t.tax, t.brand, pct.productCode, pct.productName, t.productItemType, pc.decimal, t.itemName, t.itemCode, t.freeTextItemEntered, c.description, t.deleted , " +querySql+ " from ProductContractItems t left outer join t.productContract pc ";
		} else {
			hql += " from ProductContractItems t left outer join t.productContract pc";
		}
		// If this is not a count query, only then add the join fetch. Count query does not require its
		hql += " left outer join t.uom um left outer join t.businessUnit bu left outer join t.costCenter c left outer join t.productItem i left outer join t.productCategory pct ";
		hql += " where  pc.id = :id ";

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("typeString")) {
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("uom")) {
					hql += " and upper(um.uom) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("storageLoc")) {
					hql += " and upper(t.storageLocation) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(bu.unitName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("itemName")) {
					hql += " and upper(t.itemName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("itemCode")) {
					hql += " and upper(i.productCode) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("costCenter")) {
					hql += " and (upper(c.costCenter) like (:" + cp.getData().replace(".", "") + ") or upper(c.description) like(:" + cp.getData().replace(".", "") + ")) ";
				} else if (cp.getData().equals("productCategory")) {
					hql += " and (upper(pct.productCode) like (:" + cp.getData().replace(".", "") + ") or  upper(pct.productName) like (:" + cp.getData().replace(".", "") + ")) ";
				} else if (cp.getData().equals("storageLoc")) {
					hql += " and upper(t.storageLocation) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("itemType")) {
					hql += " and t.productItemType = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("quantity")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else if (cp.getData().equals("balanceQuantity")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else if (cp.getData().equals("unitPrice")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else if (cp.getData().equals("tax")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {

//			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by " +orderSql;
//			} else {
//				hql += " order by orderValue ";
//			}
		}
		System.out.println(hql);
		LOG.info("HQL : " + hql + "  Tenant Id : " + tenantId);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// String columnName = cp.getData().equals("status");
				if (cp.getData().equals("quantity")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else if (cp.getData().equals("balanceQuantity")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else if (cp.getData().equals("unitPrice")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else if (cp.getData().equals("tax")) {
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else if (cp.getData().equals("itemType")) {
					query.setParameter("itemType", ProductItemType.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return query;
	}

	@Override
	public long findTotalFilteredProductItemListForTenant(String loggedInUserTenantId, TableDataInput input, String id) {
		final Query query = constructProductListForTenantQuery(loggedInUserTenantId, input, true, id);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalProductItemListForTenant(String tenantId, String id) {
		StringBuilder hql = new StringBuilder("select count(t) from ProductContractItems t where t.productContract.id = :id");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItemPojo> findProductContractItems(String id) {
		String hql = "";
		hql += "select distinct new com.privasia.procurehere.core.pojo.ProductItemPojo(i.id, i.productName, i.productCode) ";
		hql += " from ProductContractItems t left outer join t.productContract pc ";
		hql += " left join t.productItem i ";
		hql += " where  pc.id = :id ";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategoryPojo> findProductCategories(String id) {
		String hql = "";
		hql += "select distinct new com.privasia.procurehere.core.entity.ProductCategoryPojo(i.id, i.productName, i.productCode) ";
		hql += " from ProductContractItems t left outer join t.productContract pc ";
		hql += " left join t.productCategory i ";
		hql += " where  pc.id = :id ";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductContractItems> findProductContractItemsByProductContractId(String contractId) {
//		StringBuilder hql = new StringBuilder("select distinct pi from ProductContractItems pi left outer join fetch pi.productItem pd left outer join fetch pd.uom left outer join fetch pi.productCategory left outer join fetch pi.businessUnit left outer join fetch pi.costCenter left outer join fetch pi.productContract p where p.id = :id order by cast(pi.contractItemNumber as int)");
		StringBuilder hql = new StringBuilder("SELECT DISTINCT pi , CAST(pi.contractItemNumber AS int) as orderValue FROM ProductContractItems pi left outer join fetch pi.productItem pd left outer join fetch pd.uom left outer join fetch pi.productCategory left outer join fetch pi.businessUnit left outer join fetch pi.costCenter left outer join fetch pi.productContract p WHERE p.id = :id ORDER BY orderValue");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", contractId);
		List<ProductContractItems> productContractItems = new ArrayList<>();
		List<Object[]> objects = query.getResultList();
		objects.forEach(e -> {
			productContractItems.add((ProductContractItems) e[0]);
		});
		return productContractItems;
	}

	@Override
	@Transactional
	public void deleteItemAndRenumber(String contractId, String contractItemId, String itemNumber) {
		try {
			StringBuilder hsql = new StringBuilder("delete from ProductContractItems pc where pc.id = :contractItemId");
			Query query = getEntityManager().createQuery(hsql.toString());
			query.setParameter("contractItemId", contractItemId);
			query.executeUpdate();

			hsql = new StringBuilder("update PROC_PRODUCT_CONTRACT_ITEM set CONTRACT_ITEM_NUMBER = CAST((CAST(CONTRACT_ITEM_NUMBER AS numeric) - 1) AS character varying) where CONTRACT_ITEM_NUMBER > :itemNumber and CONTRACT_ID = :contractId");
			query = getEntityManager().createNativeQuery(hsql.toString());
			query.setParameter("itemNumber", itemNumber);
			query.setParameter("contractId", contractId);
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateErpTransfer(String contractId) {
		StringBuilder hsql = new StringBuilder("update ProductContractItems pc set pc.erpTransferred = :erpTransferred where pc.productContract.id = :contractId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("contractId", contractId);
		query.setParameter("erpTransferred", Boolean.TRUE);
		query.executeUpdate();
	}


	@Override
	public List<ProductContractItems> findProductContractItemsByProductContractIdAndContractItemNumber(String productContractId, String contractItemNumber) {
		StringBuilder hql = new StringBuilder("select distinct pi from ProductContractItems pi  where pi.productContract.id = :productContractId" +
				" and pi.contractItemNumber =:contractItemNumber");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("productContractId", productContractId);
		query.setParameter("contractItemNumber", contractItemNumber);
		return query.getResultList();
	}

	@Override
	public List<ProductContractItems> getAllByProductContractIdAndIsDeleted(String contractId) {
		StringBuilder hql = new StringBuilder("select distinct pi from ProductContractItems pi " +
				 " where pi.productContract.id =:contractId and pi.deleted = false ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("contractId", contractId);
		return query.getResultList();
	}

	@Override
	@Transactional
	public void deleteContractItemsForFailedProductContractTransaction(String contractId) {
		StringBuilder hql = new StringBuilder("delete from ProductContractItems pi " +
				" where pi.productContract.id =:contractId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("contractId", contractId);
		query.executeUpdate();
	}

}