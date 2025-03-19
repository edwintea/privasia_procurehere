package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PaymentTransactionDao;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.enums.TransactionType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.PaymentTransactionPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
@Repository
public class PaymentTransactionDaoImpl extends GenericDaoImpl<PaymentTransaction, String> implements PaymentTransactionDao {

	private static final Logger LOG = LogManager.getLogger(PaymentTransactionDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public PaymentTransaction getPaymentTransactionById(String paymentTransactionId) {
		final Query query = getEntityManager().createQuery("from PaymentTransaction t left outer join fetch t.buyer b left outer join fetch t.supplier sp left outer join fetch t.supplierPlan spl left outer join fetch t.buyerPlan bpl  left outer join fetch t.buyerSubscription s left outer join fetch t.promoCode where t.id = :paymentTransactionId ");
		query.setParameter("paymentTransactionId", paymentTransactionId);
		List<PaymentTransaction> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaymentTransaction> findPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams) {
		final Query query = constructPaymentTransactionForBuyerQuery(buyerId, tableParams, false, null);
		return query.getResultList();
	}

	@Override
	public long findTotalPaymentTransactionsForBuyer(String buyerId) {
		StringBuilder hql = new StringBuilder("select count (t) from PaymentTransaction t where t.buyerSubscription.buyer.id = :buyerId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("buyerId", buyerId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFilteredPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams) {
		final Query query = constructPaymentTransactionForBuyerQuery(buyerId, tableParams, true, null);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaymentTransaction> findPaymentTransactions(TableDataInput tableParams, Date startDate, Date endDate) {
		final Query query = constructPaymentTransactionQuery(tableParams, false, startDate, endDate);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalPaymentTransactions() {
		StringBuilder hql = new StringBuilder("select count (t) from PaymentTransaction t ");
		final Query query = getEntityManager().createQuery(hql.toString());
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFilteredPaymentTransactions(TableDataInput tableParams, Date startDate, Date endDate) {
		final Query query = constructPaymentTransactionQuery(tableParams, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaymentTransaction> findSuccessfulPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams) {
		final Query query = constructPaymentTransactionForBuyerQuery(buyerId, tableParams, false, TransactionStatus.SUCCESS);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSuccessfulPaymentTransactionsForBuyer(String buyerId) {
		StringBuilder hql = new StringBuilder("select count (t) from PaymentTransaction t where t.buyerSubscription.buyer.id = :buyerId and t.status = :status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("buyerId", buyerId);
		query.setParameter("status", TransactionStatus.SUCCESS);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalSuccessfulFilteredPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams) {
		final Query query = constructPaymentTransactionForBuyerQuery(buyerId, tableParams, true, TransactionStatus.SUCCESS);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tableParams
	 * @param startDate TODO
	 * @param endDate TODO
	 * @param tenantId
	 * @param sortValue
	 * @return
	 */
	private Query constructPaymentTransactionQuery(TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct t.id) ";
		} else {
			hql += " select distinct t ";
		}

		hql += " from PaymentTransaction t left outer join t.country c ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch t.supplierSubscription ss left outer join fetch t.supplierPlan sp left outer join fetch t.promoCode ";
		}

		hql += " where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					LOG.info("type " + cp.getData());
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("status")) {
					hql += " and t.status = (:status)";
				} else if (cp.getData().equals("countryCode")) {
					hql += " and upper(c.countryCode) like (:countryCode)";
				} else {
					LOG.info("cp.getData " + cp.getSearch().getValue());
					if (cp.getData().equals("plan.planName")) {
						hql += " and upper(" + "t.supplierPlan.planName" + ") like (:" + cp.getData().replace(".", "") + ")";
					} else {
						hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
					}
				}
			}
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  t.createdDate between :startDate and :endDate ";
		}
		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by  ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					LOG.info("orderColumn " + orderColumn);
					if (orderColumn.equals("plan.planName")) {
						String dir = order.getDir();
						hql += " t." + "supplierPlan.planName" + " " + dir + ",";
					} else if (orderColumn.equals("countryCode")) {
						String dir = order.getDir();
						hql += " c." + "countryCode" + " " + dir + ",";
					} else {
						String dir = order.getDir();
						hql += " t." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by t.createdDate desc";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		boolean isStatusFilterOn = false;

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", TransactionStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("type")) {
					query.setParameter("type", TransactionType.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("countryCode")) {
					query.setParameter("countryCode", "%" + cp.getSearch().getValue().toUpperCase() + "%");
				} else if (cp.getData().equals("totalPriceAmount")) {
					LOG.info("Amount value  : " + cp.getSearch().getValue());
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			// query.setParameter("status", Status.ACTIVE);
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return query;
	}

	/**
	 * @param buyerId
	 * @param tableParams
	 * @param isCount
	 * @return
	 */
	private Query constructPaymentTransactionForBuyerQuery(String buyerId, TableDataInput tableParams, boolean isCount, TransactionStatus status) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(t) ";
		}

		hql += " from PaymentTransaction t ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch t.country c left outer join fetch t.buyerPlan p left outer join fetch t.buyerSubscription s left outer join fetch t.promoCode ";
		}

		hql += " where t.buyerSubscription.buyer.id = :buyerId ";

		if (status != null) {
			hql += " and t.status = :status ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("status")) {
					hql += " and t.status = (:status)";
				} else if (cp.getData().equals("totalPriceAmount")) {
					LOG.info("Amount value  : " + cp.getSearch().getValue());
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else {
					LOG.info("dasdasdas");
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " t." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by t.confirmationDate desc";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("buyerId", buyerId);

		if (status != null) {
			query.setParameter("status", status);
		}

		boolean isStatusFilterOn = false;
		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					query.setParameter("status", TransactionStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("type")) {
					query.setParameter("type", TransactionType.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("totalPriceAmount")) {
					LOG.info("Amount value  : " + cp.getSearch().getValue());
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
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
	@SuppressWarnings("unchecked")
	public List<PaymentTransaction> findSuccessfulPaymentTransactionsForSupplier(String loggedInUserTenantId, TableDataInput input) {
		final Query query = constructPaymentTransactionForSupplierQuery(loggedInUserTenantId, input, false, TransactionStatus.SUCCESS);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSuccessfulFilteredPaymentTransactionsForSupplier(String supplierId, TableDataInput input) {
		final Query query = constructPaymentTransactionForSupplierQuery(supplierId, input, true, TransactionStatus.SUCCESS);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param supplierId
	 * @param input
	 * @param isCount
	 * @return
	 */
	private Query constructPaymentTransactionForSupplierQuery(String supplierId, TableDataInput input, boolean isCount, TransactionStatus status) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(t) ";
		}

		hql += " from PaymentTransaction t ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch t.country c left outer join fetch t.supplierPlan p left outer join fetch t.supplierSubscription s left outer join fetch t.promoCode";
		}

		hql += " where t.supplierSubscription.supplier.id = :supplierId ";

		if (status != null) {
			hql += " and t.status = :status ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					hql += " and t.type = (:type)";
				} else if (cp.getData().equals("status")) {
					hql += " and t.status = (:status)";
				} else if (cp.getData().equals("amount")) {
					LOG.info("Amount value  : " + cp.getSearch().getValue());
					try {
						new BigDecimal(cp.getSearch().getValue());
						hql += " and t." + cp.getData() + " = (:" + cp.getData().replace(".", "") + ")";
					} catch (Exception e) {
					}
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
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
					hql += " t." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by t.confirmationDate desc";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("supplierId", supplierId);

		if (status != null) {
			query.setParameter("status", status);
		}

		boolean isStatusFilterOn = false;
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					query.setParameter("status", TransactionStatus.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("type")) {
					query.setParameter("type", TransactionType.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("amount")) {
					LOG.info("Amount value  : " + cp.getSearch().getValue());
					try {
						new BigDecimal(cp.getSearch().getValue());
						query.setParameter(cp.getData().replace(".", ""), new BigDecimal(cp.getSearch().getValue()));
					} catch (Exception e) {
					}
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
	public long findTotalSuccessfulPaymentTransactionForSupplier(String supplierId) {
		StringBuilder hql = new StringBuilder("select count (t) from PaymentTransaction t where t.supplierSubscription.supplier.id = :supplierId and t.status = :status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("status", TransactionStatus.SUCCESS);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public PaymentTransaction getPaymentTransactionWithSupplierPlanByPaymentTransactionId(String paymentTransactionId) {
		final Query query = getEntityManager().createQuery("from PaymentTransaction t left outer join fetch t.supplierPlan sp where t.id = :paymentTransactionId");
		query.setParameter("paymentTransactionId", paymentTransactionId);
		return (PaymentTransaction) query.getSingleResult();
	}

	@Override
	public PaymentTransaction getPaymentTransactionWithBuyerPlanById(String paymentTransactionId) {
		final Query query = getEntityManager().createQuery("from PaymentTransaction t left outer join fetch t.buyerPlan p left outer join fetch t.promoCode where t.id = :paymentTransactionId");
		query.setParameter("paymentTransactionId", paymentTransactionId);
		return (PaymentTransaction) query.getSingleResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public PaymentTransaction findPaymentTransactionBySubscriptionId(String subscriptionId) {
		final Query query = getEntityManager().createQuery("select distinct t from PaymentTransaction t left outer join fetch t.buyerSubscription s where s.id = :subscriptionId order by t.createdDate");
		query.setParameter("subscriptionId", subscriptionId);
		List<PaymentTransaction> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PaymentTransaction> getAllPaymentTransactionForBuyerForExcel(String buyerId, TransactionStatus status) {
		String hql = "select  t from PaymentTransaction t   left outer join fetch t.country c left outer join fetch t.buyerPlan p left outer join fetch t.buyerSubscription s left outer join fetch t.promoCode  where t.buyerSubscription.buyer.id = :buyerId  and t.status = :status";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("buyerId", buyerId);
		query.setParameter("status", status);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaymentTransaction> getAllPaymentTransactionForSupplierForExcel(String suuplierID, TransactionStatus status) {
		String hql = "select  t from PaymentTransaction t   left outer join fetch t.country c left outer join fetch t.supplierPlan p left outer join fetch t.supplierSubscription s left outer join fetch t.promoCode  where t.supplierSubscription.supplier.id = :suuplierID  and t.status = :status";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("suuplierID", suuplierID);
		query.setParameter("status", status);
		return query.getResultList();
	}

	@Override
	public PaymentTransaction findPaymentTransactionByPaymentTokenId(String paymentToken) {
		final Query query = getEntityManager().createQuery("select distinct t from PaymentTransaction t left outer join fetch t.supplierSubscription s left outer join fetch t.buyerSubscription b left outer join fetch s.supplierPlan sp left outer join fetch b.plan bp where t.paymentToken = :paymentToken");
		query.setParameter("paymentToken", paymentToken);
		if (CollectionUtil.isNotEmpty(query.getResultList())) {
			return (PaymentTransaction) query.getResultList().get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PaymentTransactionPojo> getAllPaymentTransactionsForExcelReport(String[] paymentIds, PaymentTransactionPojo paymentTransactionPojo, boolean select_all, Date startDate, Date endDate, Object object) {
		String hql = "";

		hql = constructQueryForPaymentTransacationForExcelReport(paymentIds, paymentTransactionPojo, select_all, startDate, endDate);

		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (paymentIds != null && paymentIds.length > 0) {
				query.setParameter("paymentIds", Arrays.asList(paymentIds));
			}
		}

		if (paymentTransactionPojo != null) {
			if (StringUtils.checkString(paymentTransactionPojo.getCompanyname()).length() > 0) {
				query.setParameter("companyName", "%" + paymentTransactionPojo.getCompanyname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(paymentTransactionPojo.getCountry()).length() > 0) {
				query.setParameter("country", "%" + paymentTransactionPojo.getCountry().toUpperCase() + "%");
			}
			if (StringUtils.checkString(paymentTransactionPojo.getTransactionid()).length() > 0) {
				query.setParameter("transactionId", "%" + paymentTransactionPojo.getTransactionid().toUpperCase() + "%");
			}
			if (StringUtils.checkString(paymentTransactionPojo.getPlan()).length() > 0) {
				query.setParameter("planName", "%" + paymentTransactionPojo.getPlan().toUpperCase() + "%");
			}
			if (paymentTransactionPojo.getStatus() != null) {
				query.setParameter("status", Arrays.asList(paymentTransactionPojo.getStatus()));
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		List<PaymentTransactionPojo> finalList = query.getResultList();

		return finalList;
	}

	private String constructQueryForPaymentTransacationForExcelReport(String[] paymentIds, PaymentTransactionPojo paymentTransactionPojo, boolean select_all, Date startDate, Date endDate) {

		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.PaymentTransactionPojo(p.id, p.companyName, c.countryName, p.referenceTransactionId, p.totalPriceAmount, sp.planName, bp.planName, p.createdDate ,p.status, p.currencyCode )";

		hql += " from PaymentTransaction p ";

		hql += " left outer join p.country as c";

		hql += " left outer join p.supplierPlan sp";

		hql += " left outer join p.buyerPlan bp";

		hql += " where 1=1";

		if (!(select_all)) {
			hql += " and p.id in (:paymentIds)";
		}

		if (paymentTransactionPojo != null) {

			if (StringUtils.checkString(paymentTransactionPojo.getCompanyname()).length() > 0) {
				hql += " and upper(p.companyName) like :companyName";
			}
			if (StringUtils.checkString(paymentTransactionPojo.getCountry()).length() > 0) {
				hql += " and upper(c.countryCode) like :country";
			}
			if (StringUtils.checkString(paymentTransactionPojo.getTransactionid()).length() > 0) {
				hql += " and upper(p.referenceTransactionId) like :transactionId";
			}
			if (StringUtils.checkString(paymentTransactionPojo.getPlan()).length() > 0) {
				hql += " and upper(sp.planName) like :planName";
			}
			if (paymentTransactionPojo.getStatus() != null) {
				hql += " and upper(p.status) like :status";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and p.createdDate between :startDate and :endDate";
		}

		hql += " order by p.createdDate desc ";

		return hql;

	}

}
