package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PlanDao;
import com.privasia.procurehere.core.entity.Plan;
import com.privasia.procurehere.core.enums.PeriodUnitType;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
@Repository
public class PlanDaoImpl extends GenericDaoImpl<Plan, String> implements PlanDao {

	private static final Logger LOG = LogManager.getLogger(PlanDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public List<Plan> findAllPlansByStatus(PlanStatus planStatus) {
		StringBuilder sb = new StringBuilder("from Plan a left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb ");
		if (planStatus != null) {
			sb.append(" where a.planStatus = :status ");
		} else {
			sb.append(" where a.planStatus <> :status ");
		}
		sb.append(" order by a.planOrder");

		LOG.debug(sb.toString());

		final Query query = getEntityManager().createQuery(sb.toString());
		if (planStatus != null) {
			query.setParameter("status", planStatus);
		} else {
			query.setParameter("status", PlanStatus.DELETED);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Plan> findAllPlansByStatuses(PlanStatus[] planStatuses) {
		StringBuilder sb = new StringBuilder("from Plan a left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb join fetch a.currency c ");
		sb.append(" where a.planStatus in (:status) ");
		sb.append(" order by a.planGroup, a.planOrder");

		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("status", Arrays.asList(planStatuses));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Plan> findPlans(TableDataInput tableParams) {
		final Query query = constructPlanListQuery(tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalPlans() {
		StringBuilder hql = new StringBuilder("select count (p) from Plan p where p.planStatus =:planStatus ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("planStatus", PlanStatus.ACTIVE);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFilteredPlans(TableDataInput tableParams) {
		final Query query = constructPlanListQuery(tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param sortValue
	 * @return
	 */
	private Query constructPlanListQuery(TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(p) ";
		}

		hql += " from Plan p ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " inner join fetch p.createdBy as cb left outer join fetch p.modifiedBy as mb ";
		}

		hql += " where 1 = 1 ";

		boolean isStatusFilterOn = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("planStatus")) {
					isStatusFilterOn = true;
					hql += " and p.planStatus = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("periodUnit")) {
					hql += " and p.periodUnit = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("planOrder")) {
					hql += " and str(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and p.planStatus = :planStatus ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " p." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}

		LOG.debug("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("planStatus")) {
					query.setParameter("planStatus", PlanStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("periodUnit")) {
					query.setParameter("periodUnit", PeriodUnitType.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			query.setParameter("planStatus", PlanStatus.ACTIVE);
		}
		return query;
	}

	@Override
	public boolean isExists(Plan plan) {
		if (plan == null) {
			return false;
		}
		StringBuilder hsql = new StringBuilder("select count(p) from Plan p where p.planName = :planName and p.planStatus = :planStatus");
		if (plan.getId() != null) {
			hsql.append(" and p.id <> :id ");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("planName", plan.getPlanName());
		query.setParameter("planStatus", plan.getPlanStatus());
		if (plan.getId() != null) {
			query.setParameter("id", plan.getId());
		}
		int count = ((Number) query.getSingleResult()).intValue();
		return (count > 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Plan getPlanForEditById(String planId) {
		final Query query = getEntityManager().createQuery("from Plan p inner join fetch p.currency c where p.id = :id");
		query.setParameter("id", planId);
		List<Plan> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
