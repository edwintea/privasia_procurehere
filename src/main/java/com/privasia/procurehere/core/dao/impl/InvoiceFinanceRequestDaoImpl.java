package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.InvoiceFinanceRequestDao;
import com.privasia.procurehere.core.entity.InvoiceFinanceRequest;
import com.privasia.procurehere.core.enums.FinanceRequestStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.InvoiceFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author nitin
 */
@Repository("invoiceFinanceRequestDao")
public class InvoiceFinanceRequestDaoImpl extends GenericDaoImpl<InvoiceFinanceRequest, String> implements InvoiceFinanceRequestDao {

	private static final Logger LOG = LogManager.getLogger(InvoiceFinanceRequestDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public InvoiceFinanceRequest findInvoiceFinanceRequestByInvoiceId(String invoiceId) {
		StringBuilder hsql = new StringBuilder("from InvoiceFinanceRequest i where i.invoice.id = :invoiceId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("invoiceId", invoiceId);
		List<InvoiceFinanceRequest> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceFinanceRequestPojo> findAllInvoiceFinanceRequestsForBuyer(TableDataInput input, String tenantId, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQuery(input, tenantId, false, startDate, endDate, true);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceFinanceRequestPojo> findAllInvoiceFinanceRequestsForSupplier(TableDataInput input, String tenantId, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQuery(input, tenantId, false, startDate, endDate, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredInvoiceFinanceRequestsForBuyer(TableDataInput input, String tenantId, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQuery(input, tenantId, true, startDate, endDate, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFilteredInvoiceFinanceRequestsForSupplier(TableDataInput input, String tenantId, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQuery(input, tenantId, true, startDate, endDate, false);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalInvoiceFinanceRequestsForBuyer(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct i) from InvoiceFinanceRequest i where i.buyer.id = :buyerId and i.requestStatus in (:status)");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("buyerId", tenantId);
		query.setParameter("status", Arrays.asList(FinanceRequestStatus.values()));
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalInvoiceFinanceRequestsForSupplier(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct i) from InvoiceFinanceRequest i where i.supplier.id = :tenantId and i.requestStatus in (:status)");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(FinanceRequestStatus.values()));
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructInvoiceSearchFilterQuery(TableDataInput tableParams, String tenantId, boolean isCount, Date startDate, Date endDate, boolean isBuyer) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct r) ";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.InvoiceFinanceRequestPojo(r.id, i.id, r.invoiceNumber, r.invoiceTitle, r.requestedDate, r.invoiceAmount, r.decimal,  p.poNumber, s.companyName, b.companyName, r.requestStatus, bu.unitName, c.currencyCode, rb.name, i.referenceNumber, i.invoiceSendDate) ";
		}

		hql += " from InvoiceFinanceRequest r";

		hql += " left outer join r.invoice as i ";

		hql += " left outer join i.businessUnit as bu";

		hql += " left outer join r.currency as c";

		hql += " left outer join r.supplier s";

		hql += " left outer join r.buyer b";

		hql += " left outer join i.po p";

		hql += " join r.requesteBy rb";

		if (isBuyer) {
			hql += " where r.buyer.id = :tenantId ";
		} else {
			hql += " where r.supplier.id = :tenantId ";
		}

		Boolean selectStatusFilter = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					selectStatusFilter = true;
					hql += " and r.requestStatus in (:status) ";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(i.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("supplierCompanyName")) {
					hql += " and upper(i.supplier.companyName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("currency")) {
					hql += " and upper(i.currency.currencyCode) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("poNumber")) {
					hql += " and upper(i.po.poNumber) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(i." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		if (!selectStatusFilter) {
			hql += " and r.requestStatus in(:status)";
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and r.requestedDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("businessUnit")) {
						hql += " i.businessUnit.unitName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("currency")) {
						hql += " i.currency.currencyCode " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("supplierCompanyName")) {
						hql += " i.supplier.companyName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("poNumber")) {
						hql += " i.po.poNumber " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("sendDate")) {
						hql += " i.invoiceSendDate " + dir + ",";
					} else {
						hql += " i." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// by default order by created date
				hql += " order by r.requestedDate desc ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " + cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						if (isBuyer) {
							query.setParameter("status", Arrays.asList(FinanceRequestStatus.ACCEPTED, FinanceRequestStatus.DECLINED, FinanceRequestStatus.REQUESTED, FinanceRequestStatus.APPROVED, FinanceRequestStatus.REJECTED, FinanceRequestStatus.DISBURSED, FinanceRequestStatus.CLOSED));
						} else {
							query.setParameter("status", Arrays.asList(FinanceRequestStatus.values()));
						}
					} else {
						query.setParameter("status", FinanceRequestStatus.valueOf(cp.getSearch().getValue().toUpperCase()));
					}
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

		if (!selectStatusFilter) {
			if (isBuyer) {
				query.setParameter("status", Arrays.asList(FinanceRequestStatus.ACCEPTED, FinanceRequestStatus.DECLINED, FinanceRequestStatus.REQUESTED, FinanceRequestStatus.APPROVED, FinanceRequestStatus.REJECTED, FinanceRequestStatus.DISBURSED, FinanceRequestStatus.CLOSED));
			} else {
				query.setParameter("status", Arrays.asList(FinanceRequestStatus.values()));
			}
		}
		return query;
	}

}
