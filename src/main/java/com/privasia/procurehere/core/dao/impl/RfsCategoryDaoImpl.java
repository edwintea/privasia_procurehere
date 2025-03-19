package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfsCategoryDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.RfsCategory;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author sudesha
 */
@Repository
public class RfsCategoryDaoImpl extends GenericDaoImpl<RfsCategory, String> implements RfsCategoryDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	UserDao userDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<RfsCategory> findRfsCategoryForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructRfsForTenantQuery(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	private Query constructRfsForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(pc) ";
		}

		hql += " from RfsCategory pc ";

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
				hql += " order by pc.rfsCode ";
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

	@Override
	public long findTotalFilteredRfsCategoryForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructRfsCategoryForTenantQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructRfsCategoryForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(pc) ";
		}

		hql += " from RfsCategory pc ";

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
				hql += " order by pc.rfsCode ";
			}
		}

		// LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId); // - Rfs Category Maintanance not required tenent ID

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

	@Override
	public long findTotalRfsCategoryForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (pc) from RfsCategory pc where pc.status =:status and pc.buyer.id = :tenantId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RfsCategory rfsCategory, String buyerId) {
		StringBuilder hsql = new StringBuilder("from RfsCategory as rcm inner join fetch rcm.buyer as b where upper(rcm.rfsCode) = :rfsCode and b.id = :buyerId");
		if (StringUtils.checkString(rfsCategory.getId()).length() > 0) {
			hsql.append(" and rcm.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("rfsCode", rfsCategory.getRfsCode().toUpperCase());
		query.setParameter("buyerId", buyerId);
		if (StringUtils.checkString(rfsCategory.getId()).length() > 0) {
			query.setParameter("id", rfsCategory.getId());
		}
		LOG.info("hql :" + hsql);
		List<RfsCategory> pcmList = query.getResultList();
		return CollectionUtil.isNotEmpty(pcmList);
	}

}