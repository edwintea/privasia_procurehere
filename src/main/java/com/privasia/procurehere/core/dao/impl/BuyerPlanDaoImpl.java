package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.BuyerPlanDao;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
@Repository
public class BuyerPlanDaoImpl extends GenericDaoImpl<BuyerPlan, String> implements BuyerPlanDao {

	private static final Logger LOG = LogManager.getLogger(BuyerPlanDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public List<BuyerPlan> findBuyerPlans(TableDataInput input) {
		final Query query = constructPlanListQuery(input, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
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

		hql += " from BuyerPlan p ";

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
				} else if (cp.getData().equals("planType")) {
					hql += " and p.planType = (:" + cp.getData() + ")";
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
				} else if (cp.getData().equals("planType")) {
					query.setParameter("planType", PlanType.valueOf(cp.getSearch().getValue()));
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
	public long findTotalFilteredBuyerPlans(TableDataInput input) {
		final Query query = constructPlanListQuery(input, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalActiveBuyerPlans() {
		StringBuilder hql = new StringBuilder("select count (p) from BuyerPlan p where p.planStatus =:planStatus ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("planStatus", PlanStatus.ACTIVE);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BuyerPlan getBuyerPlanForEditById(String planId) {
		final Query query = getEntityManager().createQuery("from BuyerPlan p inner join fetch p.currency c left outer join fetch p.rangeList where p.id = :id");
		query.setParameter("id", planId);
		List<BuyerPlan> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BuyerPlan> findAllBuyerPlansByStatus(PlanStatus planStatus) {
		StringBuilder sb = new StringBuilder("select distinct a from BuyerPlan a inner join fetch a.currency c left outer join fetch a.rangeList left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb ");
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
	public BuyerPlan findUserBasedBuyerPlansByStatus() {
		StringBuilder sb = new StringBuilder("select distinct a from BuyerPlan a inner join fetch a.currency c left outer join fetch a.rangeList left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb ");
		sb.append(" where a.planStatus = :status ");
		sb.append(" and a.planType = :planType ");
		sb.append(" order by a.planOrder");

		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("status", PlanStatus.ACTIVE);
		query.setParameter("planType", PlanType.PER_USER);
		List<BuyerPlan> planList = query.getResultList();
		if (CollectionUtil.isNotEmpty(planList)) {
			return planList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public BuyerPlan findEventBasedBuyerPlansByStatus() {
		StringBuilder sb = new StringBuilder("select distinct a from BuyerPlan a inner join fetch a.currency c left outer join fetch a.rangeList left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb ");
		sb.append(" where a.planStatus = :status ");
		sb.append(" and a.planType = :planType ");
		sb.append(" order by a.planOrder");

		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("status", PlanStatus.ACTIVE);
		query.setParameter("planType", PlanType.PER_EVENT);
		List<BuyerPlan> planList = query.getResultList();
		if (CollectionUtil.isNotEmpty(planList)) {
			return planList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public boolean checkBuyerPlanInUse(String planId) {
		boolean inUse = false;
		Query query = getEntityManager().createQuery("select distinct count(*) from BuyerSubscription bs where bs.plan.id = :planId ").setParameter("planId", planId);
		inUse = ((Number) query.getSingleResult()).intValue() > 0;

		if (!inUse) {
			query = getEntityManager().createQuery("select distinct count(*) from PaymentTransaction pt where pt.buyerPlan.id = :planId ").setParameter("planId", planId);
			inUse = ((Number) query.getSingleResult()).intValue() > 0;
		}
		return inUse;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BuyerPlan> findAllBuyerPlansByStatuses(PlanStatus[] planStatus) {
		StringBuilder sb = new StringBuilder("select distinct a from BuyerPlan a inner join fetch a.currency c left outer join fetch a.rangeList left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb ");
		if (planStatus != null) {
			sb.append(" where a.planStatus in (:status) ");
		}
		sb.append(" order by a.planOrder");

		LOG.debug(sb.toString());

		final Query query = getEntityManager().createQuery(sb.toString());
		if (planStatus != null) {
			query.setParameter("status", Arrays.asList(planStatus));
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BuyerPlan getPlanByPlanName(String planName, PlanStatus status) {
		final Query query = getEntityManager().createQuery("select distinct p from BuyerPlan p inner join fetch p.currency c where p.planName = :planName and p.planStatus = :planStatus ");
		query.setParameter("planName", planName);
		query.setParameter("planStatus", status);
		List<BuyerPlan> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
