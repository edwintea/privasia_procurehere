package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.FinanceAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.FinanceAuditTrail;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Ravi
 */
@Repository
public class FinanceAuditTrailDaoImpl extends GenericDaoImpl<FinanceAuditTrail, String> implements FinanceAuditTrailDao {

	private static final Logger LOG = LogManager.getLogger(OwnerAuditTrailDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<FinanceAuditTrail> findAuditTrailForTenant(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructAuditTrailForTenantQuery(tenantId, input, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredAuditTrailForTenant(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructAuditTrailForTenantQuery(loggedInUserTenantId, input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalAuditTrailForTenant(String loggedInUserTenantId) {
		StringBuilder hql = new StringBuilder("select count (at) from FinanceAuditTrail at where at.tenantId = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", loggedInUserTenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tenantId
	 * @param input
	 * @param isCount
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private Query constructAuditTrailForTenantQuery(String tenantId, TableDataInput input, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(at) ";
		}

		hql += " from FinanceAuditTrail at ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch at.actionBy as ab ";
		}

		hql += " where at.tenantId = :tenantId ";
		boolean orderBy = true;
		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT ::  " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("activity")) {
					hql += " and at.activity = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(at." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  at.actionDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				orderBy = false;
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " at." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}

		if (orderBy && !isCount) {
			hql += " order by at.actionDate desc";
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT 1 ::  " + cp.getData() + "VALUE 1 :: " + cp.getSearch().getValue());
				if (cp.getData().equals("activity")) {
					query.setParameter("activity", AuditTypes.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return query;
	}
}
