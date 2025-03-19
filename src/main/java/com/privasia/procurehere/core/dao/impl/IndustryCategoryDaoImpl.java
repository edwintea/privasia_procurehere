package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.IndustryCategoryDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class IndustryCategoryDaoImpl extends GenericDaoImpl<IndustryCategory, String> implements IndustryCategoryDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	UserDao userDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> getAllIndustryCategory() {
		final Query query = getEntityManager().createQuery("from IndustryCategory bic left outer join fetch bic.createdBy as cb left outer join fetch bic.modifiedBy as mb order by bic.code");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> findIndustryCategoryForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructIndustryCategoryForTenantQuery(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredIndustryCategoryForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructIndustryCategoryForTenantQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalIndustryCategoryForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count(distinct ic) from IndustryCategory ic where ic.status =:status and ic.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param sortValue
	 * @return
	 */
	private Query constructIndustryCategoryForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(ic) ";
		}

		hql += " from IndustryCategory ic ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " inner join fetch ic.createdBy as cb left outer join fetch ic.modifiedBy as mb ";
		}

		hql += " where ic.buyer.id = :tenantId ";

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and ic.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(ic." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and ic.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " ic." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}

		// LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
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

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void loadNaicsCodesForTenant(List<NaicsCodes> codeList, String tenantId, String createdById) {
		if (CollectionUtil.isNotEmpty(codeList)) {
			User user = userDao.findById(createdById);
			Buyer buyer = buyerDao.findById(tenantId);
			List<IndustryCategory> icList = new ArrayList<IndustryCategory>();
			for (NaicsCodes code : codeList) {
				IndustryCategory ic = new IndustryCategory(code);
				if (!isExists(ic, tenantId)) {
					ic.setBuyer(buyer);
					ic.setCreatedBy(user);
					ic.setCreatedDate(new Date());
					icList.add(ic);
				}
			}
			this.batchInsert(icList);
		}
	}

	@Override
	public long countIndustryCategory() {
		StringBuilder hql = new StringBuilder("from IndustryCategory ");
		final Query query = getEntityManager().createQuery(hql.toString());
		return (query.getResultList() != null ? query.getResultList().size() : 0);
	}

	@Override
	public boolean isExists(IndustryCategory industryCategory, String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(*) from IndustryCategory as bic where upper(bic.code) = :code and bic.buyer.id = :buyerId ");
		if (StringUtils.checkString(industryCategory.getId()).length() > 0) {
			hsql.append(" and bic.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("code", industryCategory.getCode().toUpperCase());
		query.setParameter("buyerId", tenantId);
		if (StringUtils.checkString(industryCategory.getId()).length() > 0) {
			query.setParameter("id", industryCategory.getId());
		}
		int count = ((Number) query.getSingleResult()).intValue();
		LOG.info("*****:" + count);
		return count > 0 ? true : false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IndustryCategory findIndustryCategoryByCodeAndTenantId(String code, String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct ic from IndustryCategory ic where ic.status = :status and ic.buyer.id = :tenantId AND upper(ic.code) = :code ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		query.setParameter("code", code.toUpperCase());
		List<IndustryCategory> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> findIndustryCategoryByNameAndTenantId(String searchValue, String tenantId) {
		LOG.info("Tenant Id : " + tenantId + " Search  : " + searchValue);
		StringBuilder hsql = new StringBuilder("select distinct ic from IndustryCategory ic where ic.status = :status and ic.buyer.id = :tenantId ");
		if (StringUtils.checkString(searchValue).length() > 0) {
			hsql.append(" AND ( upper(ic.name) like :name or upper(ic.code) like :name) ");
		}
		hsql.append(" order by ic.code");
		final Query query = getEntityManager().createQuery(hsql.toString());

		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("name", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		return query.getResultList();
	}

	@Override
	public IndustryCategory getIndustryCategoryForRftById(String id) {
		StringBuilder hsql = new StringBuilder("from IndustryCategory as ic left outer join fetch ic.createdBy as cb left outer join fetch ic.modifiedBy as mb where ic.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		IndustryCategory industryCategory = (IndustryCategory) query.getSingleResult();
		return industryCategory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> getAllIndustryCategoryByIds(List<String> ids) {
		StringBuilder hsql = new StringBuilder("select ic from IndustryCategory as ic inner join fetch ic.createdBy as cb left outer join fetch ic.modifiedBy as mb where ic.id in (:ids) ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("ids", ids);
		List<IndustryCategory> returnList = query.getResultList();
		return returnList;
	}

	@Override
	public List<IndustryCategory> getAllIndustryCategoryForTenant(String loggedInUserTenantId) {

		return getIndustryCategoryForTenant(loggedInUserTenantId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> getIndustryCategoryForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("from IndustryCategory bic left outer join fetch bic.createdBy as cb left outer join fetch bic.modifiedBy as mb where  bic.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductCategory> getAllProductCategoryByIds(List<String> productCategory) {
		StringBuilder hsql = new StringBuilder("from ProductCategory where id in (:ids) ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("ids", productCategory);
		List<ProductCategory> returnList = query.getResultList();
		return returnList;
	}

	@Override
	public IndustryCategory getIndustryCategorCodeAndNameById(String id) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.entity.IndustryCategory(ic.id,ic.code,ic.name ) from IndustryCategory as ic where ic.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		IndustryCategory industryCategory = (IndustryCategory) query.getSingleResult();
		return industryCategory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> getAllIndustryCategoryOnlyByIds(List<String> ids) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.entity.IndustryCategory(ic.id,ic.code,ic.name ) from IndustryCategory as ic where ic.id in (:ids) ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("ids", ids);
		List<IndustryCategory> returnList = query.getResultList();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> getAllIndustryCategoryForCsv(String tenantId, int pAGE_SIZE, int pageNo) {
		StringBuilder hql = new StringBuilder("from IndustryCategory bic left outer join fetch bic.createdBy as cb left outer join fetch bic.modifiedBy as mb where  bic.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pAGE_SIZE * pageNo));
		query.setMaxResults(pageNo);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> findActiveIndustryCategoryByTenantId( String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.IndustryCategory(ic.id,ic.code,ic.name ) from IndustryCategory ic where ic.status = :status and ic.buyer.id = :tenantId ");
 		hsql.append(" order by ic.code");
		final Query query = getEntityManager().createQuery(hsql.toString());

		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
 		return query.getResultList();
	}

	@Override
	public IndustryCategory findIndustryCategoryByCodeAndTenantIdAndExceptStatus(String code, String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct ic from IndustryCategory ic where ic.buyer.id = :tenantId AND upper(ic.code) = :code ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("code", code.toUpperCase());
		List<IndustryCategory> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

}
