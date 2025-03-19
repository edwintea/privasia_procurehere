package com.privasia.procurehere.core.dao.impl;

import java.util.Calendar;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ProductCategoryMaintenanceDao;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductCategoryPojo;
import com.privasia.procurehere.core.entity.ProductContractItems;
import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class ProductCategoryMaintenanceDaoImpl extends GenericDaoImpl<ProductCategory, String> implements ProductCategoryMaintenanceDao {

	private static final Logger LOG = LogManager.getLogger(ProductCategoryMaintenanceDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategory> findAllActiveProductCategoryForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("from ProductCategory pc where pc.status =:status and pc.buyer.id = :tenantId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(ProductCategory productCategoryMaintenance, String buyerId) {
		StringBuilder hsql = new StringBuilder("from ProductCategory as pcm inner join fetch pcm.buyer as b where upper(pcm.productCode) = :productCode and b.id = :buyerId");
		if (StringUtils.checkString(productCategoryMaintenance.getId()).length() > 0) {
			hsql.append(" and pcm.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("productCode", productCategoryMaintenance.getProductCode().toUpperCase());
		query.setParameter("buyerId", buyerId);
		if (StringUtils.checkString(productCategoryMaintenance.getId()).length() > 0) {
			query.setParameter("id", productCategoryMaintenance.getId());
		}
		LOG.info("hql :" + hsql);
		List<ProductCategory> pcmList = query.getResultList();
		return CollectionUtil.isNotEmpty(pcmList);
	}

	@Override
	public long findTotalFilteredProductCategoryForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructProductCategoryForTenantQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategory> findProductCategoryForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructProductCategoryForTenantQuery(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalProductCategoryForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (pc) from ProductCategory pc where pc.status =:status and pc.buyer.id = :tenantId");
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
	private Query constructProductCategoryForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(pc) ";
		}

		hql += " from ProductCategory pc ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " join fetch pc.createdBy as cb left outer join fetch pc.modifiedBy as mb ";
		}
		boolean isStatusFilterOn = false;

		hql += " where pc.buyer.id = :tenantId ";

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and pc.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("typeString")) {
					hql += " and pc.type = (:type)";
				} else {
					hql += " and upper(pc." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and pc.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " pc." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by pc.productCode ";
			}
		}

		// LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId); // - Product Category Maintanance not required tenent ID

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("typeString")) {
					query.setParameter("type", RfxTypes.valueOf(cp.getSearch().getValue()));
				} else {
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
	public List<ProductCategory> getAllProductCategoryByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from ProductCategory pc where pc.buyer.id = :tenantId order by pc.productCode");
 		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProductCategory getByProductCategoryByCode(String productCode, String tenantId) {
		final Query query = getEntityManager().createQuery("from ProductCategory pc where pc.buyer.id = :tenantId and upper(pc.productCode) = :productCode");
		query.setParameter("tenantId", tenantId);
		query.setParameter("productCode", productCode.toUpperCase());
		List<ProductCategory> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public ProductItem checkProductItemExistOrNot(String itemName, String supplierId, String tenantId) {
		String sql = "from ProductItem pc inner join fetch pc.favoriteSupplier as fs where pc.buyer.id = :tenantId and upper(pc.productName) = :itemName and fs.supplier.id = :supplierId ";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("itemName", itemName.toUpperCase());
		query.setParameter("supplierId", supplierId);
		List<ProductItem> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategory> findProductCategoryByTenantIDSupplierID(String tenantId, String favSupplierId) {

		String hql = "select  distinct(t.productCategory) from FavouriteSupplier as t left outer join  t.productCategory where t.buyer.id = :tenantId ";
		if (StringUtils.checkString(favSupplierId).length() > 0) {
			hql += " AND t.id = :favSupplierId ";
		} else {
			hql = "select  distinct(t.productCategory) from ProductItem as t left outer join  t.productCategory where t.buyer.id = :tenantId";
		}
		LOG.info("Query ======>>>>>" + hql.toString());

		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(favSupplierId).length() > 0) {
			query.setParameter("favSupplierId", favSupplierId);
		}
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItem> findProductsBySupplierIdAndCategory(String tenantId, String favSupplierId, String productCategoryId) {

		StringBuilder hql = new StringBuilder(" select t from ProductItem as t left outer join  t.productCategory where t.buyer.id = :tenantId  and t.productCategory.id=:productCategory ");

		if (StringUtils.checkString(favSupplierId).length() == 0) {
			hql.append(" AND t.favoriteSupplier is null ");
		} else {
			hql.append(" AND t.favoriteSupplier.id = :favSupplierId ");
		}

		LOG.info("Query ======>>>>>" + hql.toString());

		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(favSupplierId).length() > 0) {
			query.setParameter("favSupplierId", favSupplierId);

		}
		query.setParameter("productCategory", productCategoryId);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategory> findProductsCategoryByTenantAndItemId(String tenantId, String itemId) {
		StringBuilder hql = new StringBuilder("select distinct(t.productCategory) from ProductItem as t left outer join  t.productCategory where t.buyer.id = :tenantId and t.id=:itemId");

		LOG.info("Query ======>>>>>" + hql.toString());

		final Query query = getEntityManager().createQuery(hql.toString());

		query.setParameter("tenantId", tenantId);
		query.setParameter("itemId", itemId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItem> findProductsBySupplierIdAndCategoryId(String tenantId, String favSupplierId, String productCategoryId) {

		StringBuilder hql = new StringBuilder(" select distinct(t) from ProductItem as t left outer join fetch t.productCategory left outer join fetch t.favoriteSupplier  as fs  left outer join fetch fs.supplier  as sss  left outer join fetch  fs.supplier as ss where t.buyer.id = :tenantId ");

		if (StringUtils.checkString(favSupplierId).length() != 0) {
			hql.append(" AND t.favoriteSupplier.id = :favSupplierId ");
		}
		if (StringUtils.checkString(productCategoryId).length() != 0) {
			hql.append(" AND t.productCategory.id = :productCategoryId ");
		}

		LOG.info("Query ======>>>>>" + hql.toString());

		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(favSupplierId).length() != 0) {
			query.setParameter("favSupplierId", favSupplierId);

		}
		if (StringUtils.checkString(productCategoryId).length() != 0) {
			query.setParameter("productCategoryId", productCategoryId);
		}
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> findFavSupplierByCategoryId(String productCategoryId, String supplierId) {
		String hql = "select distinct(t) from  FavouriteSupplier t  left outer join fetch t.supplier left outer join t.productCategory  as pc where pc.id = :productCategoryId ";
		if (StringUtils.checkString(supplierId).length() > 0) {
			hql += " and t.id = :supplierId";
		}

		Query query = getEntityManager().createQuery(hql);
		query.setParameter("productCategoryId", productCategoryId);

		if (StringUtils.checkString(supplierId).length() > 0) {
			query.setParameter("supplierId", supplierId);
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategory> findProductsBytenantAndOpenSupplier(String tenantIdd) {

		StringBuilder hql = new StringBuilder("select  distinct(t.productCategory) from ProductItem as t left outer join  t.productCategory where t.buyer.id = :tenantId AND t.favoriteSupplier is null ");

		final Query query = getEntityManager().createQuery(hql.toString());

		query.setParameter("tenantId", tenantIdd);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItem> searchProductsBySupplierIdAndCategoryId(String tenantId, String favSupplierId, String productCategoryId, String searchParm, String openSupplier) {

		StringBuilder hql = new StringBuilder(" select distinct new com.privasia.procurehere.core.entity.ProductItem(t.id,t.productCode,t.productName,t.uom,t.unitPrice,t.tax,pc,fs,t.status) from ProductItem as t inner join  t.productCategory pc left outer join  t.favoriteSupplier  as fs left outer join fs.productCategory as fcp where t.buyer.id = :tenantId ");

		if (StringUtils.checkString(favSupplierId).length() > 0) {
			hql.append(" AND t.favoriteSupplier.id = :favSupplierId or ( fcp.id = pc.id or fs.id is null ) ");
		}
		if (StringUtils.checkString(productCategoryId).length() > 0) {
			hql.append(" AND t.productCategory.id = :productCategoryId ");
		}
		// it is checked when item select first
		if (StringUtils.checkString(productCategoryId).length() <= 0 && StringUtils.checkString(favSupplierId).length() <= 0) {
			LOG.info("first======>>>>>");

			// if (StringUtils.checkString(openSupplier).length() > 0)
			hql.append(" AND (fcp.id = pc.id or fs.id is null )");
			// else
			// hql.append(" AND fcp.id = pc.id ");

		}
		if (StringUtils.checkString(searchParm).length() > 0) {
			hql.append(" AND upper(t.productName) like :searchParm");
		}

		LOG.info("Query ======>>>>>" + hql.toString());

		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(favSupplierId).length() > 0) {
			query.setParameter("favSupplierId", favSupplierId);

		}
		if (StringUtils.checkString(productCategoryId).length() > 0) {
			query.setParameter("productCategoryId", productCategoryId);
		}

		if (StringUtils.checkString(searchParm).length() > 0) {
			String search = searchParm.toUpperCase();
			query.setParameter("searchParm", "%" + search + "%");
		}

		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategory> serchAllActiveProductCategoryForTenant(String loggedInUserTenantId, String search) {
		StringBuilder hql = new StringBuilder("select distinct(pc) from ProductCategory pc where pc.status =:status and pc.buyer.id = :tenantId ");

		if (StringUtils.checkString(search).length() > 0) {
			hql.append(" and (upper(pc.productName) like :searchParm or upper(pc.productCode) like :searchParm ) ");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", loggedInUserTenantId);

		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("searchParm", "%" + search.toUpperCase() + "%");
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FavouriteSupplier> findFavSupplierByCategoryListId(List<String> catList) {

		long count = catList.size();
		LOG.info("==============" + count);
		final String hql = "select distinct(t) from  FavouriteSupplier t  left outer join fetch t.supplier as s inner join t.productCategory  as pc where " + " :catcount = ( select count(ifspc) from FavouriteSupplier fs inner join fs.productCategory  as ifspc  where  ifspc.id in (:productCategoryId) and  fs.id = t.id ) ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("productCategoryId", catList);
		query.setParameter("catcount", count);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductItem> searchProductsBySupplierIdList(String tenantId, List<String> slist, String searchParam, List<String> catList) {

		StringBuilder hql = new StringBuilder("select new com.privasia.procurehere.core.entity.ProductItem(t.id, t.productCode,t.productName,t.uom,t.unitPrice,t.tax,pc,fs,t.status) ");
		hql.append(" from ProductItem as t inner join t.productCategory pc left outer join t.favoriteSupplier as fs  ");
		hql.append(" where t.buyer.id = :tenantId and t.status = :status ");
		if (CollectionUtil.isNotEmpty(catList)) {
			hql.append(" AND pc.id in (:catList)");
		}

		if (CollectionUtil.isNotEmpty(slist)) {
			hql.append(" AND ( fs.id is null or fs.id in (:favSupplierId) )");
		}
		if (StringUtils.checkString(searchParam).length() > 0) {
			hql.append(" AND upper(t.productName) like :searchParm");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		if (CollectionUtil.isNotEmpty(slist)) {
			query.setParameter("favSupplierId", slist);
		}
		if (CollectionUtil.isNotEmpty(catList)) {
			query.setParameter("catList", catList);
		}

		if (StringUtils.checkString(searchParam).length() > 0) {
			String search = searchParam.toUpperCase();
			query.setParameter("searchParm", "%" + search + "%");
		}

		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);

		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public ProductCategory getProductCategoryCodeAndTenantId(String productCategoryCode, String tenantId) {
		try {
			final Query query = getEntityManager().createQuery("from ProductCategory pc where upper(pc.productCode) = :productCategoryCode and pc.buyer.id = :tenantId");
			LOG.info("getUombyPcodeandTenantId(uom,tenantId) called");
			query.setParameter("productCategoryCode", productCategoryCode.toUpperCase());
			query.setParameter("tenantId", tenantId);
			List<ProductCategory> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExistsProductCategory(String productCode, String buyerId) {
		StringBuilder hsql = new StringBuilder("from ProductCategory as pcm inner join fetch pcm.buyer as b where upper(pcm.productCode) = :productCode and b.id = :buyerId");
		if (StringUtils.checkString(productCode).length() > 0) {
			hsql.append(" and pcm.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("productCode", productCode.toUpperCase());
		query.setParameter("buyerId", buyerId);
		if (StringUtils.checkString(buyerId).length() > 0) {
			query.setParameter("id", buyerId);
		}
		LOG.info("hql :" + hsql);
		List<ProductCategory> pcmList = query.getResultList();
		if (CollectionUtil.isNotEmpty(pcmList)) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProductCategory findProductCategoryByCode(String productCode, String tenantId) {
		StringBuilder hsql = new StringBuilder("from ProductCategory pc where upper(pc.productCode) = :productCode and pc.buyer.id = :tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());

		query.setParameter("productCode", productCode);
		query.setParameter("tenantId", tenantId);
		List<ProductCategory> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductContractItems> searchProductContractBySupplierIdList(String tenantId, List<String> slist, String searchParam, List<String> catList, String unitName) {

		StringBuilder hql = new StringBuilder();
		hql.append("select new com.privasia.procurehere.core.entity.ProductContractItems(ci.id,ci.productItem.id,ci.productItem.productName,ci.unitPrice,ci.uom,ci.balanceQuantity,ci.storageLocation,sup,cat,ci.businessUnit) ");
		hql.append(" from ProductContractItems ci inner join ci.productContract p inner join ci.productCategory cat left outer join ci.businessUnit bu left outer join ci.uom u left outer join p.supplier sup where p.buyer.id =:tenantId and ci.productContract.status =:productStatus and (:nowDate between p.contractStartDate and p.contractEndDate )");

		if (CollectionUtil.isNotEmpty(catList)) {
			hql.append(" AND cat.id in (:catList)");
		}

		if (CollectionUtil.isNotEmpty(slist)) {
			hql.append(" AND ( sup.id is null or sup.id in (:favSupplierId) )");
		}

		if (StringUtils.checkString(unitName).length() > 0) {
			hql.append(" AND upper(ci.businessUnit.unitName) like :unitName");
		}

		if (StringUtils.checkString(searchParam).length() > 0) {
			hql.append(" AND upper(ci.productItem.productName) like :searchParm");
		}

		final Query query = getEntityManager().createQuery(hql.toString());

		if (CollectionUtil.isNotEmpty(slist)) {
			query.setParameter("favSupplierId", slist);
		}

		if (CollectionUtil.isNotEmpty(catList)) {
			query.setParameter("catList", catList);
		}

		if (StringUtils.checkString(searchParam).length() > 0) {
			String search = searchParam.toUpperCase();
			query.setParameter("searchParm", "%" + search + "%");
		}
		if (StringUtils.checkString(unitName).length() > 0) {
			String search = unitName.toUpperCase();
			query.setParameter("unitName", "%" + search + "%");
		}
		LOG.info(hql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("nowDate", Calendar.getInstance().getTime());
		query.setParameter("productStatus", ContractStatus.ACTIVE);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> findFavSupplierByCategoryListForPr(List<String> catList) {
		long count = catList.size();
		final String hql = "select distinct new com.privasia.procurehere.core.pojo.EventSupplierPojo(t.id, t.supplier.companyName) from  FavouriteSupplier t  left outer join t.supplier as s inner join t.productCategory  as pc where " + " :catcount = ( select count(ifspc) from FavouriteSupplier fs inner join fs.productCategory  as ifspc  where  ifspc.id in (:productCategoryId) and  fs.id = t.id ) ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("productCategoryId", catList);
		query.setParameter("catcount", count);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategoryPojo> fetchAllActiveProductCategoryForTenant(String tenantId, String search) {
		StringBuffer hql = new StringBuffer("select distinct new com.privasia.procurehere.core.entity.ProductCategoryPojo(pc.id, pc.productName, pc.productCode) from ProductCategory pc where pc.status = :status and pc.buyer.id = :tenantId");
		if (StringUtils.checkString(search).length() > 0) {
			hql.append(" and (upper(pc.productName) like (:search) or upper(pc.productCode) like (:search)) ");
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + StringUtils.checkString(search).toUpperCase() + "%");
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long countConstructQueryToFetchProductCategory(String tenantId) {
		StringBuffer hql = new StringBuffer("select count(pc) from ProductCategory pc where pc.status = :status and pc.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategory> getAllProductCategoryByTenantIdForCsv(String tenantId, int pageSize, int pageNo) {
		final Query query = getEntityManager().createQuery("from ProductCategory pc where pc.buyer.id = :tenantId order by pc.productCode");
		LOG.info("Query ======>>>>>" + query);
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

	@Override
	public long findProductCategoryCountForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (pc) from ProductCategory pc where pc.buyer.id = :tenantId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategory> getAllActiveProductCategoryForTenant(String tenantId, String searchValue) {
		StringBuilder hql = new StringBuilder("select new com.privasia.procurehere.core.entity.ProductCategory(pc.id, pc.productCode, pc.productName, pc.status) from ProductCategory pc where pc.status =:status and pc.buyer.id = :tenantId ");
		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and (upper(pc.productName) like :searchParm or upper(pc.productCode) like :searchParm ) ");
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchParm", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
//		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long findActiveProductCategoryForTenant(String tenantId, String searchValue) {
		StringBuilder hql = new StringBuilder("select count(pc.id) from ProductCategory pc where pc.status =:status and pc.buyer.id = :tenantId ");
		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and (upper(pc.productName) like :searchParm or upper(pc.productCode) like :searchParm ) ");
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchParm", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		return ((Number) query.getSingleResult()).intValue();
	}

}
