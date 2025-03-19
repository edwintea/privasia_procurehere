package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.ProductListMaintenanceDao;
import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.ProductItemPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class ProductListMaintenanceDaoImpl extends GenericDaoImpl<ProductItem, String> implements ProductListMaintenanceDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItemPojo> findProductListForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructProductListForTenantQuery(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredProductListForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructProductListForTenantQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalProductListForTenant(String tenantId) {

		StringBuilder hql = new StringBuilder("select count (t) from ProductItem t where t.status =:status and t.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param isCount
	 * @return
	 */
	private Query constructProductListForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(t) ";
		}
		if (!isCount) {
			hql += "select distinct new com.privasia.procurehere.core.pojo.ProductItemPojo(t.id,t.productCode,t.productName, pc.productName,t.purchaseGroupCode, s.companyName, t.productItemType, cb.loginId, mb.loginId, t.createdDate, t.modifiedDate,t.status, t.interfaceCode ) from ProductItem t ";
		} else {
			hql += " from ProductItem t ";
		}
		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join  t.createdBy cb left outer join  t.modifiedBy mb left outer join  t.favoriteSupplier fs left outer join  fs.supplier s left outer join  t.productCategory pc ";
		} else {
			hql += " left outer join  t.createdBy cb left outer join  t.modifiedBy mb left outer join  t.favoriteSupplier fs left outer join  fs.supplier s left outer join  t.productCategory pc ";
		}

		hql += " where t.buyer.id = :tenantId ";
		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and t.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("itemType")) {
					hql += " and t.productItemType in (:productItemType) ";
				} else if (cp.getData().equals("typeString")) {
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("favoriteSupplier")) {
					hql += " and upper(s.companyName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("itemName")) {
					hql += " and upper(t.productName) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("itemCode")) {
					hql += " and upper(t.productCode) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createBy")) {
					hql += " and upper(cb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					hql += " and upper(mb.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("itemCategory")) {
					hql += " and upper(pc.productName) like (:" + cp.getData() + ")";
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and t.status = :status ";
		}
		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("favoriteSupplier")) {
						hql += " s.companyName " + dir + ",";
					} else if (orderColumn.equals("createBy")) {
						hql += " cb.loginId " + dir + ",";
					} else if (orderColumn.equals("modifiedBy")) {
						hql += " mb.loginId " + dir + ",";
					} else if (orderColumn.equals("itemCategory")) {
						hql += " pc.productName " + dir + ",";
					} else if (orderColumn.equals("itemName")) {
						hql += " t.productName " + dir + ",";
					} else if (orderColumn.equals("itemCode")) {
						hql += " t.productCode " + dir + ",";
					} else if (orderColumn.equals("itemType")) {
						hql += " t.productItemType " + dir + ",";
					} else {
						hql += " t." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by t.productName, t.productCode ";
			}
		}

		LOG.info("HQL : " + hql + "  Tenant Id : " + tenantId);
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("itemType")) {
					query.setParameter("productItemType", StringUtils.checkString(cp.getSearch().getValue()).length() > 0 ? ProductItemType.fromString(cp.getSearch().getValue().toUpperCase()) : null);
				} else {
					LOG.info("Search by : " + cp.getData() + " value : " + cp.getSearch().getValue().toUpperCase());
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			query.setParameter("status", Status.ACTIVE);
		}

		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItem> findProductListForByProductCategoryId(String productCategoryId) {
		StringBuilder hsql = new StringBuilder("from ProductItem pl left outer join fetch pl.productCategory pc inner join fetch pl.uom u where pc.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", productCategoryId);
		List<ProductItem> productList = query.getResultList();
		return productList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProductItem findProductCategoryById(String id) {
		StringBuilder hql = new StringBuilder("from ProductItem as t inner join fetch t.uom where t.id = :id ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		List<ProductItem> uList = query.getResultList();
		// LOG.info("uList :"+uList);
		if (CollectionUtil.isNotEmpty(uList)) {
			return (ProductItem) uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItem> findProductsByNameAndTenantAndFavSupplier(String productItem, String tenantId, String favSupplierId) {
		StringBuilder hsql = new StringBuilder("select distinct t from ProductItem as t inner join fetch t.productCategory p left outer join fetch t.uom um where t.status = :status and t.buyer.id = :tenantId ");

		if (StringUtils.checkString(favSupplierId).length() == 0) {
			hsql.append(" AND t.favoriteSupplier is null ");
		} else {
			hsql.append(" AND t.favoriteSupplier.id = :favSupplierId ");
		}

		if (StringUtils.checkString(productItem).length() > 0) {
			hsql.append(" AND (upper(t.productName) like :productName OR  upper(t.productCode) like :productName )");
		}
		hsql.append(" order by t.productCode");
		final Query query = getEntityManager().createQuery(hsql.toString());

		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(productItem).length() > 0) {
			query.setParameter("productName", "%" + StringUtils.checkString(productItem).toUpperCase() + "%");
		}

		if (StringUtils.checkString(favSupplierId).length() > 0) {
			query.setParameter("favSupplierId", favSupplierId);
		}

		List<ProductItem> returnList = null;
		if (StringUtils.checkString(favSupplierId).length() == 0 && StringUtils.checkString(productItem).length() == 0) {
			query.setFirstResult(0);
			query.setMaxResults(10);
		}
		returnList = query.getResultList();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItem> findProductsByTenantWithOutFavSupplier(String tenantId) {
		LOG.info("teanId is ------------------------------------>" + tenantId);
		StringBuilder hsql = new StringBuilder("select distinct t from ProductItem as t inner join fetch t.productCategory p left outer join fetch t.uom um where t.status = :status and t.buyer.id = :tenantId and t.favoriteSupplier is null order by t.productCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		List<ProductItem> returnList = query.getResultList();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItem> findProductsByTenantId(String tenantId) {
		LOG.info("teanId is ------------------------------------>" + tenantId);
		StringBuilder hsql = new StringBuilder("select distinct t from ProductItem as t inner join fetch t.productCategory p   left outer join fetch t.favoriteSupplier fs   left outer join fetch t.uom um where t.status = :status and t.buyer.id = :tenantId  order by t.productCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		List<ProductItem> returnList = query.getResultList();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProductItem findProductItemByCode(String productCode, String tenantId, ProductItemType productItemType) {
		StringBuilder hsql = new StringBuilder("select distinct t from ProductItem as t left outer join fetch t.uom um left outer join fetch t.productCategory pc where t.status = :status and t.productCode = :productCode and t.buyer.id = :tenantId ");

		if (productItemType != null) {
			hsql.append(" and t.productItemType = :productItemType ");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("productCode", productCode);
		if (productItemType != null) {
			query.setParameter("productItemType", productItemType);
		}
		query.setParameter("tenantId", tenantId);
		try {
			List<ProductItem> list = query.getResultList();
			if (CollectionUtil.isNotEmpty(list)) {
				return list.get(0);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(String productCode, String buyerId, String productId) {
		LOG.info("Product Code :: " + productCode + "  Buyer  :: " + buyerId);
		StringBuilder hsql = new StringBuilder("from ProductItem as pi inner join fetch pi.buyer as b where upper(pi.productCode) = :productCode and b.id = :buyerId");
		if (StringUtils.checkString(productId).length() > 0) {
			hsql.append(" and pi.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("productCode", productCode.toUpperCase());
		query.setParameter("buyerId", buyerId);
		if (StringUtils.checkString(productId).length() > 0) {
			query.setParameter("id", productId);
		}
		List<ProductItem> scList = query.getResultList();
		if (CollectionUtil.isNotEmpty(scList)) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void setStatusInactiveForPastProducts() {
		StringBuilder hsql = new StringBuilder("update ProductItem as pi set pi.status = 'INACTIVE' where pi.validityDate < :currDate ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("currDate", new Date());
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<ProductItemPojo> getAllProductItemsByTenantIdForDownloadExcel(String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.ProductItemPojo(pi.productName, pi.productCode, pi.remarks, um.uom) from ProductItem as pi inner join pi.uom as um  where pi.buyer.id = :tenantId and pi.status =:status order by pi.productName, pi.productCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		List<ProductItemPojo> returnList = query.getResultList();
		return returnList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProductItemPojo> getAllProductItemsByTenantIdAndIdsForDownloadExcel(String tenantId, List<String> ids) {
		LOG.info("IDS : " + ids.toString());
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.ProductItemPojo(pi.productName, pi.productCode, pi.remarks, um.uom, pc.productCode, pi.productItemType) from ProductItem as pi inner join pi.uom as um inner join pi.productCategory as pc where pi.buyer.id = :tenantId and pi.status =:status and pi.id in ( :ids ) order by pi.productName, pi.productCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("ids", ids);
		List<ProductItemPojo> returnList = query.getResultList();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItem> getAllProductItemsByTenantId(String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct t from ProductItem as t inner join fetch t.productCategory p left outer join fetch t.uom um left outer join fetch t.favoriteSupplier fs where t.status = :status and t.buyer.id = :tenantId order by t.productCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		List<ProductItem> returnList = query.getResultList();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItemPojo> getAllProductItemsForDownloadByTenantId(String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.ProductItemPojo ");
		hsql.append(" (pi.interfaceCode, pi.productCode, pi.productName, um.uom, pc.productCode, s.companyName, pi.unitPrice, pi.tax, pi.productItemType, pi.glCode,pi.unspscCode,pi.status,pi.remarks, pi.historicPricingRefNo, pi.purchaseGroupCode,pi.brand, pi.startDate,pi.validityDate,pi.contractReferenceNumber )");
		hsql.append(" from ProductItem as pi inner join pi.uom as um inner join pi.productCategory as pc left outer join pi.favoriteSupplier fs left outer join  fs.supplier s");
		hsql.append(" where pi.buyer.id = :tenantId order by pi.productCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
//		query.setParameter("status", Status.ACTIVE);
		List<ProductItemPojo> returnList = query.getResultList();
		return returnList;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public ProductItem getByProductCode(String productCode, String tenantId) {
		final Query query = getEntityManager().createQuery("from ProductItem pi where pi.buyer.id = :tenantId and upper(pi.productCode) = :productCode ");
		query.setParameter("tenantId", tenantId);
		query.setParameter("productCode", productCode.toUpperCase());
		List<ProductItem> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExistsproductCode(String productCode, String buyerId) {
		LOG.info("Product Code :: " + productCode + "  Buyer  :: " + buyerId);
		StringBuilder hsql = new StringBuilder("from ProductItem as pi inner join fetch pi.buyer as b where b.id = :buyerId");
		if (StringUtils.checkString(productCode).length() > 0) {
			hsql.append(" and upper(pi.productCode) = :productCode");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("productCode", productCode.toUpperCase());
		query.setParameter("buyerId", buyerId);
		if (StringUtils.checkString(productCode).length() > 0) {
			query.setParameter("productCode", productCode.toUpperCase());
		}

		List<ProductItem> scList = query.getResultList();
		LOG.info("scList" + scList);
		if (CollectionUtil.isNotEmpty(scList)) {
			return true;
		} else {
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItemPojo> findAllActiveProductItemForTenantId(String tenantId, Status status, int pageSize, int pageNo) {
		String hql="";

		hql += "select new com.privasia.procurehere.core.pojo.ProductItemPojo(p.id, p.productCode, p.productName, pc.productName, p.productItemType, p.purchaseGroupCode, p.interfaceCode, s.companyName, p.status, um.uom, p.unitPrice, p.tax, p.remarks, p.glCode, p.unspscCode, p.brand, p.contractReferenceNumber, p.historicPricingRefNo, p.startDate, p.validityDate)";

		hql += " from ProductItem p";

		hql += " left outer join p.productCategory as pc left outer join p.favoriteSupplier fs left outer join  fs.supplier s";

		hql += " inner join p.uom as um ";

		hql += " where p.buyer.id =:tenantId ";

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		// query.setParameter("status", Status.ACTIVE);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);
		List<ProductItemPojo> returnList = query.getResultList();
		return returnList;
	}

	@Override
	public long findTotalProductItems(String tenantId) {
		Query query = getEntityManager().createQuery("select count(distinct p) from ProductItem as p where p.buyer.id = :tenantId ");
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItemPojo> fetchAllActiveProductItemForTenant(String tenantId, String search) {
		StringBuffer hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.ProductItemPojo(pi.id, pi.productName) from ProductItem pi where pi.status = :status and pi.buyer.id =:tenantId ");
		if (StringUtils.checkString(search).length() > 0) {
			hql.append(" and upper(pi.productName) like (:search) ");
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + StringUtils.checkString(search).toUpperCase() + "%");
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long countConstructQueryToFetchProductItem(String tenantId) {
		StringBuffer hql = new StringBuffer("select count(pi.id) from ProductItem pi where pi.buyer.id = :tenantId and pi.status = :status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItemPojo> fetchAllActiveProductItemNameAndCodeForTenant(String tenantId, String search) {
		StringBuffer hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.ProductItemPojo(pi.id, pi.productName, pi.productCode) from ProductItem pi where pi.status = :status and pi.buyer.id =:tenantId ");
		if (StringUtils.checkString(search).length() > 0) {
			hql.append(" and (upper(pi.productName) like (:search) or upper(pi.productCode) like (:search) )");
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + StringUtils.checkString(search).toUpperCase() + "%");
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

}