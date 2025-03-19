/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PaymentTermsDao;
import com.privasia.procurehere.core.entity.PaymentTermes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author jayshree
 */
@Repository
public class PaymentTermsDaoImpl extends GenericDaoImpl<PaymentTermes, String> implements PaymentTermsDao {

	private static final Logger LOG = LogManager.getLogger(PaymentTermsDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(PaymentTermes paymentTermes, String tenantId) {
		StringBuilder hsql = new StringBuilder("from PaymentTermes as cc where upper(cc.paymentTermCode) = upper(:paymentTermCode) and cc.buyer.id = :tenantId ");
		if (StringUtils.checkString(paymentTermes.getId()).length() > 0) {
			hsql.append(" and cc.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("paymentTermCode", paymentTermes.getPaymentTermCode().toUpperCase());
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(paymentTermes.getId()).length() > 0) {
			query.setParameter("id", paymentTermes.getId());
		}
		List<PaymentTermes> stList = query.getResultList();
		return CollectionUtil.isNotEmpty(stList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaymentTermes> getAllActivePaymentTermesForTenant(String tenantId) {
		final Query query = getEntityManager().createQuery("from PaymentTermes cc left outer join fetch cc.createdBy as cb left outer join fetch cc.modifiedBy as mb where cc.buyer.id = :tenantId and cc.status = :status order by cc.paymentTermCode");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaymentTermes> findPaymentTermesForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructPaymentTermesForTenantQuery(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		List<PaymentTermes> stList = query.getResultList();
		if (CollectionUtil.isNotEmpty(stList)) {
			return stList;
		} else {
			return null;
		}
	}

	@Override
	public long findTotalFilteredPaymentTermesForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructPaymentTermesForTenantQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructPaymentTermesForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {
		String hql = "";
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(c) ";
		}
		hql += " from PaymentTermes c ";

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
				}else if (cp.getData().equals("paymentDays")) {
					hql += " and upper(c.paymentDays) like (:paymentDays)";
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

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT 1 ::  " + cp.getData() + "VALUE 1 :: " + cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				}else if (cp.getData().equals("paymentDays")) {
					query.setParameter("paymentDays", + Integer.parseInt(cp.getSearch().getValue().toUpperCase()));
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
	public long findTotalPaymentTermesForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (c) from PaymentTermes c where c.status =:status and c.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaymentTermes> getAllPaymentTermesByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from PaymentTermes cc where cc.buyer.id = :tenantId order by cc.paymentTermCode");
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PaymentTermes getByPaymentTermes(String paymentTermes, String tenantId) {
		final Query query = getEntityManager().createQuery("from PaymentTermes cc where cc.buyer.id = :tenantId and upper(cc.paymentTermCode) = :paymentTermes ");
		query.setParameter("tenantId", tenantId);
		query.setParameter("paymentTermes", paymentTermes.toUpperCase());
		List<PaymentTermes> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PaymentTermes getByPaymentTermsByDesc(String description, String tenantId) {
		final Query query = getEntityManager().createQuery("from PaymentTermes cc where cc.buyer.id = :tenantId and upper(cc.description) = :description ");
		query.setParameter("tenantId", tenantId);
		query.setParameter("description", description.toUpperCase());
		List<PaymentTermes> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PaymentTermes> getActivePaymentTermesByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from PaymentTermes cc where cc.buyer.id = :tenantId and cc.status =:status order by cc.paymentTermCode");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

}
