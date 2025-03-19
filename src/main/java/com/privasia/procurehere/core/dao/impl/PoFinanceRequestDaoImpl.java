package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PoFinanceRequestDao;
import com.privasia.procurehere.core.entity.PoFinanceRequest;
import com.privasia.procurehere.core.entity.PoFinanceRequestDocuments;
import com.privasia.procurehere.core.enums.FinanceRequestStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.PoFinanceRequestDocumentsPojo;
import com.privasia.procurehere.core.pojo.PoFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author nitin
 */
@Repository("poFinanceRequestDao")
public class PoFinanceRequestDaoImpl extends GenericDaoImpl<PoFinanceRequest, String> implements PoFinanceRequestDao {

	private static final Logger LOG = LogManager.getLogger(PoFinanceRequestDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public PoFinanceRequest findPoFinanceRequestByPoId(String poId) {
		StringBuilder hsql = new StringBuilder("from PoFinanceRequest i where i.po.id = :poId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		List<PoFinanceRequest> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PoFinanceRequestPojo getPoFinanceRequestPojoByPoId(String poId) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.pojo.PoFinanceRequestPojo(i.id, i.po.id, i.poNumber, i.poName, i.requestedDate, i.acceptedDate, i.declinedDate, i.approvedDate, i.poAmount, i.decimal, s.companyName, b.companyName, i.requestStatus, c.currencyCode, r.loginId, a.loginId, d.loginId, app.loginId, i.poDate) from PoFinanceRequest i join i.requestedBy r left outer join i.acceptedBy a left outer join i.declinedBy d left outer join i.approvedBy app join i.buyer b join i.supplier s join i.currency c where i.po.id = :poId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		List<PoFinanceRequestPojo> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoFinanceRequestDocumentsPojo> getPoFinanceRequestDocumentsForRequestForSupplier(String requestId) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.pojo.PoFinanceRequestDocumentsPojo(d.id, d.documentName, d.documentDescription, d.fileName, d.fileSize, u.loginId, d.uploadDate, dt.documentType) from PoFinanceRequestDocuments d join d.uploadedBy u join d.documentType dt where d.poFinanceRequest.id = :requestId and dt.visibleToSupplier = true");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("requestId", requestId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PoFinanceRequestDocuments getPoFinanceRequestDocumentById(String documentId) {
		StringBuilder hsql = new StringBuilder("from PoFinanceRequestDocuments d where d.id = :docmentId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("documentId", documentId);
		List<PoFinanceRequestDocuments> list = query.getResultList();
		return CollectionUtil.isNotEmpty(list) ? list.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoFinanceRequestPojo> findAllPoFinanceRequestsForBuyer(TableDataInput input, String tenantId, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQuery(input, tenantId, false, startDate, endDate, true);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoFinanceRequestPojo> findAllPoFinanceRequestsForSupplier(TableDataInput input, String tenantId, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQuery(input, tenantId, false, startDate, endDate, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredPoFinanceRequestsForBuyer(TableDataInput input, String tenantId, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQuery(input, tenantId, true, startDate, endDate, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFilteredPoFinanceRequestsForSupplier(TableDataInput input, String tenantId, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQuery(input, tenantId, true, startDate, endDate, false);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalPoFinanceRequestsForBuyer(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct i) from PoFinanceRequest i where i.buyer.id = :buyerId and i.requestStatus in (:status)");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("buyerId", tenantId);
		query.setParameter("status", Arrays.asList(FinanceRequestStatus.values()));
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalPoFinanceRequestsForSupplier(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct i) from PoFinanceRequest i where i.supplier.id = :tenantId and i.requestStatus in (:status)");
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
			hql += "select distinct new com.privasia.procurehere.core.pojo.PoFinanceRequestPojo(r.id, i.id, r.poNumber, r.poName, r.requestedDate, r.poAmount, r.decimal, s.companyName, b.companyName, r.requestStatus, bu.unitName, c.currencyCode, rb.name, i.referenceNumber, r.poDate) ";
		}

		hql += " from PoFinanceRequest r";

		hql += " left outer join r.po as i ";

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
					} else {
						hql += " i." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// by default order by created date
				hql += " order by r.poDate desc ";
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
