package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.BuyerSubscriptionDao;
import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
@Repository
public class BuyerSubscriptionDaoImpl extends GenericDaoImpl<BuyerSubscription, String> implements BuyerSubscriptionDao {

	private static final Logger LOG = LogManager.getLogger(BuyerSubscriptionDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public BuyerSubscription getBuyerSubscriptionById(String subscriptionId) {
		final Query query = getEntityManager().createQuery("from BuyerSubscription s left outer join fetch s.buyer b left outer join fetch s.plan p left outer join fetch s.range r left outer join fetch s.planPeriod left outer join fetch s.paymentTransactions pt where s.id = :subscriptionId ");
		query.setParameter("subscriptionId", subscriptionId);
		List<BuyerSubscription> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PlanType getBuyerSubscriptionPlanTypeByBuyerID(String buyerId) {
		final Query query = getEntityManager().createQuery("select p.planType from BuyerPackage bp left outer join bp.buyer b left outer join bp.plan p where b.id = :buyerId ");
		query.setParameter("buyerId", buyerId);
		List<PlanType> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BuyerSubscription> findBuyerSubscriptionHistoryForBuyer(String tenantId, TableDataInput input) {
		Query query = constructBuyerSubscriptionHistoryQuery(tenantId, input, false, null);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	/**
	 * @param tenantId
	 * @param input
	 * @param isCount
	 * @param status
	 * @return
	 */
	private Query constructBuyerSubscriptionHistoryQuery(String tenantId, TableDataInput input, boolean isCount, SubscriptionStatus status) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(s) ";
		}

		hql += " from BuyerSubscription s ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch s.range r left outer join fetch s.plan p left outer join fetch s.planPeriod pp ";
		}

		hql += " where s.buyer.id = :tenantId ";

		hql += " and s.subscriptionStatus is not null ";

		// if (status != null) {
		// hql += " and s.subscriptionStatus = :status ";
		// }

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("planType")) {
					hql += " and s.planType = (:planType)";
				} else if (cp.getData().equals("subscriptionStatus")) {
					hql += " and s.subscriptionStatus = (:status)";
				} else {
					hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " s." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by s.createdDate desc";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		// if (status != null) {
		// query.setParameter("status", status);
		// }

		boolean isStatusFilterOn = false;
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("subscriptionStatus")) {
					isStatusFilterOn = true;
					query.setParameter("status", SubscriptionStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("planType")) {
					query.setParameter("planType", PlanType.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			// query.setParameter("status", Status.ACTIVE);
		}
		return query;
	}

	@Override
	public long findTotalFilteredBuyerSubscriptionHistoryForBuyer(String tenantId, TableDataInput input) {
		Query query = constructBuyerSubscriptionHistoryQuery(tenantId, input, true, null);
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public long findTotalBuyerSubscriptionHistoryForBuyer(String tenantId) {
		String hql = "select count(s) from BuyerSubscription s where s.buyer.id = :tenantId ";
		Query query = getEntityManager().createQuery(hql).setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BuyerSubscription getCurrentBuyerSubscriptionForBuyer(String tenantId) {
		String hql = "select distinct s from BuyerSubscription s left outer join fetch s.range r left outer join fetch s.plan p left outer join fetch s.paymentTransactions pt left outer join fetch pt.promoCode left outer join fetch s.planPeriod pp left outer join fetch s.nextSubscription ns where s.buyer.id = :tenantId and s.subscriptionStatus = :status ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", SubscriptionStatus.ACTIVE);
		List<BuyerSubscription> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public BuyerSubscription getLastBuyerSubscriptionForBuyer(String tenantId) {
		String hql = "select distinct s from BuyerSubscription s left outer join fetch s.range r left outer join fetch s.plan p left outer join fetch s.paymentTransactions pt left outer join fetch s.planPeriod pp left outer join fetch s.nextSubscription ns where s.buyer.id = :tenantId and s.subscriptionStatus is not null order by s.startDate desc ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		List<BuyerSubscription> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public BuyerSubscription getActiveBuyerSubscriptionByPaymentProfileId(String profileId) {
		String hql = "select distinct s from BuyerSubscription s left outer join fetch s.range r left outer join fetch s.plan p left outer join fetch s.planPeriod pp left outer join fetch s.buyer bu left outer join fetch s.nextSubscription ns where s.paymentProfileId = :profileId and s.subscriptionStatus = :status ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("profileId", profileId);
		query.setParameter("status", SubscriptionStatus.ACTIVE);
		List<BuyerSubscription> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public BuyerSubscription getBuyerSubscriptionBySubscriptionId(String id) {
		String hql = "select distinct s from BuyerSubscription s left outer join fetch s.range r left outer join fetch s.plan p left outer join fetch s.paymentTransactions pt left outer join fetch pt.promoCode left outer join fetch s.planPeriod pp left outer join fetch s.nextSubscription ns where s.id = :id ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		List<BuyerSubscription> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BuyerSubscription> getAllBuyerSubscriptionForBuyerForExcel(String tenantId) {
		String hql = "select  s from BuyerSubscription s left outer join fetch s.range r left outer join fetch s.plan p left outer join fetch s.planPeriod pp " + " where s.buyer.id = :tenantId and s.subscriptionStatus is not null";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BuyerSubscription> getNonActiveBuyerSubscriptionList(String tenantId) {

		String hql = "select  s from BuyerSubscription s left outer join fetch s.range r left outer join fetch s.plan p left outer join fetch s.planPeriod pp  where s.buyer.id = :tenantId and s.subscriptionStatus <> (:subscriptionStatus)";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("subscriptionStatus", SubscriptionStatus.ACTIVE);

		return query.getResultList();

	}
}
