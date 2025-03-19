package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierTagsDao;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.SupplierTags;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("supplierTagsDao")
public class SupplierTagsDaoImpl extends GenericDaoImpl<SupplierTags, String> implements SupplierTagsDao {

	private static final Logger LOG = LogManager.getLogger(SupplierTagsDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(SupplierTags supplierTags, String tenantId) {
		StringBuilder hsql = new StringBuilder("from SupplierTags as st where upper(st.supplierTags) = upper(:supplierTags) and st.buyer.id = :tenantId ");
		if (StringUtils.checkString(supplierTags.getId()).length() > 0) {
			hsql.append(" and st.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierTags", supplierTags.getSupplierTags());
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(supplierTags.getId()).length() > 0) {
			query.setParameter("id", supplierTags.getId());
		}
		List<CostCenter> stList = query.getResultList();
		return CollectionUtil.isNotEmpty(stList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierTags> findSupplierTagsForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructSupplierTagsForTenantQuery(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredSupplierTagsForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructSupplierTagsForTenantQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalSupplierTagsForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (st) from SupplierTags st where st.status =:status and st.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructSupplierTagsForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(c) ";
		}

		hql += " from SupplierTags c ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch c.createdBy as cb left outer join fetch c.modifiedBy as mb ";
		}

		hql += " where c.buyer.id = :tenantId ";
		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT ::  " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					hql += " and c.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(c." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}
		if (!isStatusFilterOn) {
			hql += " and c.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " c." + orderColumn + " " + dir + ",";
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
				LOG.info("INPUT 1 ::  " + cp.getData() + "VALUE 1 :: " + cp.getSearch().getValue());
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

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierTags> searchAllActiveSupplierTagsForTenant(String loggedInUserTenantId) {
		StringBuilder hql = new StringBuilder("select distinct(st) from SupplierTags st where st.status =:status and st.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierTags> getAllSupplierTagsOnlyByIds(List<String> supplierTags) {
		LOG.info("supplierTags"+supplierTags.size());
		StringBuilder hsql = new StringBuilder("from SupplierTags where id in (:ids) ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("ids", supplierTags);
		List<SupplierTags> returnList = query.getResultList();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierTags getSuppliertagsAndTenantId(String suppliertags, String tenantId) {
		final Query query = getEntityManager().createQuery("from SupplierTags st where st.buyer.id = :tenantId and upper(st.supplierTags) = :supplierTags");
		query.setParameter("tenantId", tenantId);
		query.setParameter("supplierTags", suppliertags.toUpperCase());
		List<SupplierTags> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	public SupplierTags getSuppliertagsDescriptionAndTenantId(String description, String tenantId) {
		final Query query = getEntityManager().createQuery("from SupplierTags st where st.buyer.id = :tenantId and upper(st.description) = :description");
		query.setParameter("tenantId", tenantId);
		query.setParameter("description", description.toUpperCase());
		List<SupplierTags> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

}
