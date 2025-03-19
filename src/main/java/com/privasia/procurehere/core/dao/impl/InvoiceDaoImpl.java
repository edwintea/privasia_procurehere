package com.privasia.procurehere.core.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.InvoiceDao;
import com.privasia.procurehere.core.entity.Invoice;
import com.privasia.procurehere.core.enums.InvoiceStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.InvoiceSupplierPojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("invoiceDao")
public class InvoiceDaoImpl extends GenericDaoImpl<Invoice, String> implements InvoiceDao {

	private static final Logger LOG = LogManager.getLogger(Global.DO_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceSupplierPojo> findAllSearchFilterInvoiceForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQueryForSupplier(tenantId, input, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSearchFilterInvoiceForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQueryForSupplier(tenantId, input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalInvoiceForSupplier(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct i) from Invoice i where i.supplier.id = :supplierId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructInvoiceSearchFilterQueryForSupplier(String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct i) ";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.InvoiceSupplierPojo(i.id, i.invoiceId, i.name, cb.name, i.createdDate, i.grandTotal, i.decimal, i.referenceNumber, p.poNumber, b.companyName, i.status, bu.unitName, c.currencyCode, ab.name, i.actionDate, i.invoiceSendDate) ";
		}

		hql += " from Invoice i";

		hql += " left outer join i.createdBy as cb";

		hql += " left outer join i.businessUnit as bu";

		hql += " left outer join i.currency as c";

		hql += " left outer join i.buyer b";

		hql += " left outer join i.po p";

		hql += " left outer join i.actionBy ab";

		hql += " where i.supplier.id = :tenantId ";

		Boolean selectStatusFilter = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					selectStatusFilter = true;
					hql += " and i.status in (:status) ";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(i.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("buyerCompanyName")) {
					hql += " and upper(i.buyer.companyName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("currency")) {
					hql += " and upper(i.currency.currencyCode) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("poNumber")) {
					hql += " and upper(i.po.poNumber) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(i.createdBy.name) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(i." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}

		if (!selectStatusFilter) {
			hql += " and i.status in(:status)";
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  i.createdDate between :startDate and :endDate ";
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
					} else if (orderColumn.equalsIgnoreCase("buyerCompanyName")) {
						hql += " i.buyer.companyName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("poNumber")) {
						hql += " i.po.poNumber " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("sendDate")) {
						hql += " i.invoiceSendDate " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("createdBy")) {
						hql += " i.createdBy.name " + dir + ",";
					} else {
						hql += " i." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// by default order by created date

				hql += " order by i.createdDate desc ";
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
						query.setParameter("status", Arrays.asList(InvoiceStatus.values()));
					} else {
						query.setParameter("status", InvoiceStatus.fromString(cp.getSearch().getValue().toUpperCase()));
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
			query.setParameter("status", Arrays.asList(InvoiceStatus.values()));
		}
		return query;
	}

	@Override
	public Invoice findByInvoiceId(String invoiceId) {
		StringBuilder hsql = new StringBuilder("from Invoice i left outer join fetch i.supplier sp join fetch i.createdBy cb left outer join fetch i.modifiedBy mb left outer join fetch i.currency c left outer join fetch i.po p where i.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", invoiceId);
		Invoice invoice = (Invoice) query.getSingleResult();
		return invoice;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceSupplierPojo> findAllSearchFilterInvoiceForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQueryForBuyer(tenantId, input, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSearchFilterInvoiceForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructInvoiceSearchFilterQueryForBuyer(tenantId, input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalInvoiceForBuyer(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct i) from Invoice i where i.buyer.id = :buyerId and i.status in (:status)");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("buyerId", tenantId);
		query.setParameter("status", Arrays.asList(InvoiceStatus.ACCEPTED, InvoiceStatus.DECLINED, InvoiceStatus.INVOICED));
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructInvoiceSearchFilterQueryForBuyer(String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct i) ";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.InvoiceSupplierPojo(i.id, i.invoiceId, i.name, i.createdDate, i.grandTotal, i.decimal,  p.poNumber, s.companyName, i.status, bu.unitName, c.currencyCode, ab.name, i.actionDate, i.referenceNumber, i.invoiceSendDate) ";
		}

		hql += " from Invoice i";

		hql += " left outer join i.businessUnit as bu";

		hql += " left outer join i.currency as c";

		hql += " left outer join i.supplier s";

		hql += " left outer join i.po p";

		hql += " left outer join i.actionBy ab";

		hql += " where i.buyer.id = :tenantId ";

		Boolean selectStatusFilter = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					selectStatusFilter = true;
					hql += " and i.status in (:status) ";
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
			hql += " and i.status in(:status)";
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  i.invoiceSendDate between :startDate and :endDate ";
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

				hql += " order by i.invoiceSendDate desc ";
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
						query.setParameter("status", Arrays.asList(InvoiceStatus.ACCEPTED, InvoiceStatus.DECLINED, InvoiceStatus.INVOICED));
					} else {
						query.setParameter("status", InvoiceStatus.fromString(cp.getSearch().getValue().toUpperCase()));
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
			query.setParameter("status", Arrays.asList(InvoiceStatus.ACCEPTED, InvoiceStatus.DECLINED, InvoiceStatus.INVOICED));
		}
		return query;
	}

	@Override
	public long findTotalInvoiceForPo(String poId) {
		StringBuilder hsql = new StringBuilder("select count(distinct d) from Invoice d where d.po.id = :poId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<InvoiceSupplierPojo> getAllInvoiceDetailsForExcelReport(String tenantId, String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate) {

		String hql = "";

		hql = constructQuerySupplierForInvoiceDetailsForExcelReport(tenantId, invoiceIds, invoiceSupplierPojo, select_all, startDate, endDate);

		LOG.info("Query---------" + hql);
		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (invoiceIds != null && invoiceIds.length > 0) {
				query.setParameter("invoiceIds", Arrays.asList(invoiceIds));
			}
		}

		if (invoiceSupplierPojo != null) {
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicename()).length() > 0) {
				query.setParameter("invoicename", "%" + invoiceSupplierPojo.getInvoicename().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referencenumber", "%" + invoiceSupplierPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicenumber()).length() > 0) {
				query.setParameter("invoicenumber", "%" + invoiceSupplierPojo.getInvoicenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getPonumber()).length() > 0) {
				query.setParameter("ponumber", "%" + invoiceSupplierPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBuyer()).length() > 0) {
				query.setParameter("buyer", "%" + invoiceSupplierPojo.getBuyer().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + invoiceSupplierPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getCreatedby()).length() > 0) {
				query.setParameter("createdby", "%" + invoiceSupplierPojo.getCreatedby().toUpperCase() + "%");
			}
			if (invoiceSupplierPojo.getStatus() != null) {
				query.setParameter("status", invoiceSupplierPojo.getStatus());
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);

		List<InvoiceSupplierPojo> finalList = query.getResultList();

		return finalList;
	}

	private String constructQuerySupplierForInvoiceDetailsForExcelReport(String loggedInUserTenantId, String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate) {

		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.InvoiceSupplierPojo(i.id, i.invoiceId, i.name, cb.name, i.createdDate, i.grandTotal, i.decimal, i.referenceNumber, p.poNumber, b.companyName, i.status, bu.unitName, c.currencyCode, ab.name, i.actionDate, i.invoiceSendDate) ";

		hql += "from Invoice i";

		hql += " left outer join i.createdBy as cb";

		hql += " left outer join i.businessUnit as bu";

		hql += " left outer join i.currency as c";

		hql += " left outer join i.buyer b";

		hql += " left outer join i.po p";

		hql += " left outer join i.actionBy as ab";

		hql += " where i.supplier.id = :tenantId";

		if (!(select_all)) {
			hql += " and i.id in (:invoiceIds)";
		}

		if (invoiceSupplierPojo != null) {
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicename()).length() > 0) {
				hql += " and upper(i.name) like :invoicename";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getReferencenumber()).length() > 0) {
				hql += " and upper(i.referenceNumber) like :referencenumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicenumber()).length() > 0) {
				hql += " and upper(i.invoiceId) like :invoicenumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :ponumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBuyer()).length() > 0) {
				hql += " and upper(b.companyName) like :buyer";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getCreatedby()).length() > 0) {
				hql += " and upper(cb.name) like :createdby";
			}
			if (invoiceSupplierPojo.getStatus() != null ) {
				hql += " and upper(i.status) like :status";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and i.createdDate between :startDate and :endDate";
		}

		hql += " order by i.createdDate desc ";

		return hql;

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<InvoiceSupplierPojo> getAllBuyerInvoiceDetailsForExcelReport(String tenantId, String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		String hql = "";

		hql = constructBuyerQueryForInvoiceDetailsForExcelReport(tenantId, invoiceIds, invoiceSupplierPojo, select_all, startDate, endDate);

		LOG.info("Query ----------> " + hql);
		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (invoiceIds != null && invoiceIds.length > 0) {
				query.setParameter("invoiceIds", Arrays.asList(invoiceIds));
			}
		}

		if (invoiceSupplierPojo != null) {
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicename()).length() > 0) {
				query.setParameter("invoicename", "%" + invoiceSupplierPojo.getInvoicename().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getReferenceNumber()).length() > 0) {
				query.setParameter("referenceNumber", "%" + invoiceSupplierPojo.getReferenceNumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicenumber()).length() > 0) {
				query.setParameter("invoicenumber", "%" + invoiceSupplierPojo.getInvoicenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getPonumber()).length() > 0) {
				query.setParameter("ponumber", "%" + invoiceSupplierPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getSupplier()).length() > 0) {
				query.setParameter("supplier", "%" + invoiceSupplierPojo.getSupplier().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + invoiceSupplierPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (invoiceSupplierPojo.getStatus() != null) {
				query.setParameter("status", invoiceSupplierPojo.getStatus());
			} else {
				query.setParameter("status", Arrays.asList(InvoiceStatus.ACCEPTED, InvoiceStatus.DECLINED, InvoiceStatus.INVOICED));
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);

		List<InvoiceSupplierPojo> finalList = query.getResultList();

		return finalList;
	}

	private String constructBuyerQueryForInvoiceDetailsForExcelReport(String loggedInUserTenantId, String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate) {

		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.InvoiceSupplierPojo(i.id, i.invoiceId, i.name, i.createdDate, i.grandTotal, i.decimal,  p.poNumber, s.companyName, i.status, bu.unitName, c.currencyCode, ab.name, i.actionDate, i.referenceNumber, i.invoiceSendDate) ";

		hql += " from Invoice i";

		hql += " left outer join i.businessUnit as bu";

		hql += " left outer join i.currency as c";

		hql += " left outer join i.supplier s";

		hql += " left outer join i.po p";

		hql += " left outer join i.actionBy ab";

		hql += " where i.buyer.id = :tenantId ";

		if (!(select_all)) {
			hql += " and i.id in (:invoiceIds)";
		}

		if (invoiceSupplierPojo != null) {
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicename()).length() > 0) {
				hql += " and upper(i.name) like :invoicename";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getReferenceNumber()).length() > 0) {
				hql += " and upper(i.referenceNumber) like :referenceNumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicenumber()).length() > 0) {
				hql += " and upper(i.invoiceId) like :invoicenumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :ponumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getSupplier()).length() > 0) {
				hql += " and upper(s.companyName) like :supplier";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (invoiceSupplierPojo.getStatus() != null) {
				hql += " and upper(i.status) like :status";
			} else {
				hql += " and i.status in (:status)";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and i.invoiceSendDate between :startDate and :endDate";
		}

		hql += " order by i.invoiceSendDate desc ";

		return hql;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceSupplierPojo> getInvoicesByPoId(String poId) {
		String hql = "select distinct new com.privasia.procurehere.core.pojo.InvoiceSupplierPojo(i.id, i.invoiceId, i.name, i.createdDate, i.grandTotal,p.poNumber,i.status, i.referenceNumber, i.decimal, cb.name, i.actionDate, i.invoiceSendDate) from Invoice i  left outer join i.po p left outer join i.createdBy cb where p.id =:poId order by i.createdDate desc";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("poId", poId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceSupplierPojo> getInvoicesByPoIdForBuyer(String poId) {
		String hql = "select distinct new com.privasia.procurehere.core.pojo.InvoiceSupplierPojo(i.id, i.invoiceId, i.name, i.createdDate, i.grandTotal,p.poNumber,i.status, i.referenceNumber, i.decimal, cb.name, i.actionDate, i.invoiceSendDate) from Invoice i  left outer join i.po p left outer join i.createdBy cb where p.id =:poId and i.status not in (:status) order by i.createdDate desc";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("poId", poId);
		query.setParameter("status", Arrays.asList(InvoiceStatus.CANCELLED, InvoiceStatus.DRAFT));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public long findTotalBuyerInvoiceForPo(String poId) {
		StringBuilder hsql = new StringBuilder("select count(distinct i) from Invoice i where i.po.id = :poId and i.status not in (:status)");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		query.setParameter("status", Arrays.asList(InvoiceStatus.CANCELLED, InvoiceStatus.DRAFT));
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceSupplierPojo> findBuyerInvoicesForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate) {
		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.InvoiceSupplierPojo(i.id, i.actionDate, i.invoiceId, i.name, i.createdDate, i.grandTotal, i.decimal, i.invoiceSendDate,  p.poNumber, s.companyName, i.status, bu.unitName, c.currencyCode, ab.name, i.referenceNumber) ";

		hql += " from Invoice i";

		hql += " left outer join i.businessUnit as bu left outer join i.currency as c";

		hql += " left outer join i.supplier s left outer join i.po p left outer join i.actionBy ab";

		hql += " where i.buyer.id = :tenantId ";

		if (!(select_all)) {
			hql += " and i.id in (:invoiceIds)";
		}

		if (invoiceSupplierPojo != null) {
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicename()).length() > 0) {
				hql += " and upper(i.name) like :invoicename";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getReferenceNumber()).length() > 0) {
				hql += " and upper(i.referenceNumber) like :referenceNumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicenumber()).length() > 0) {
				hql += " and upper(i.invoiceId) like :invoicenumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :ponumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getSupplier()).length() > 0) {
				hql += " and upper(s.companyName) like :supplier";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (invoiceSupplierPojo.getStatus() != null) {
				hql += " and upper(i.status) like :status";
			} else {
				hql += " and i.status in (:status)";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and i.invoiceSendDate between :startDate and :endDate";
		}

		hql += " order by i.invoiceSendDate desc ";

		LOG.info("Query ======================>>>>>>>> " + hql);
		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (invoiceIds != null && invoiceIds.length > 0) {
				query.setParameter("invoiceIds", Arrays.asList(invoiceIds));
			}
		}

		if (invoiceSupplierPojo != null) {
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicename()).length() > 0) {
				query.setParameter("invoicename", "%" + invoiceSupplierPojo.getInvoicename().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getReferenceNumber()).length() > 0) {
				query.setParameter("referenceNumber", "%" + invoiceSupplierPojo.getReferenceNumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicenumber()).length() > 0) {
				query.setParameter("invoicenumber", "%" + invoiceSupplierPojo.getInvoicenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getPonumber()).length() > 0) {
				query.setParameter("ponumber", "%" + invoiceSupplierPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getSupplier()).length() > 0) {
				query.setParameter("supplier", "%" + invoiceSupplierPojo.getSupplier().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + invoiceSupplierPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (invoiceSupplierPojo.getStatus() != null) {
				query.setParameter("status", invoiceSupplierPojo.getStatus());
			} else {
				query.setParameter("status", Arrays.asList(InvoiceStatus.ACCEPTED, InvoiceStatus.DECLINED, InvoiceStatus.INVOICED));
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);

		List<InvoiceSupplierPojo> finalList = query.getResultList();
		
		if(CollectionUtil.isNotEmpty(finalList)) {
			return finalList;
		}else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceSupplierPojo> findSupplierInvoicesForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate) {
		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.InvoiceSupplierPojo(i.id, i.actionDate, i.invoiceSendDate, i.invoiceId, i.name, cb.name, i.createdDate, i.decimal, i.referenceNumber, p.poNumber, b.companyName, i.grandTotal, i.status, bu.unitName, c.currencyCode, ab.name) ";

		hql += "from Invoice i";

		hql += " left outer join i.createdBy as cb left outer join i.businessUnit as bu";

		hql += " left outer join i.currency as c left outer join i.buyer b";

		hql += " left outer join i.po p left outer join i.actionBy as ab";

		hql += " where i.supplier.id = :tenantId";

		if (!(select_all)) {
			hql += " and i.id in (:invoiceIds)";
		}

		if (invoiceSupplierPojo != null) {
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicename()).length() > 0) {
				hql += " and upper(i.name) like :invoicename";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getReferencenumber()).length() > 0) {
				hql += " and upper(i.referenceNumber) like :referencenumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicenumber()).length() > 0) {
				hql += " and upper(i.invoiceId) like :invoicenumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :ponumber";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBuyer()).length() > 0) {
				hql += " and upper(b.companyName) like :buyer";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getCreatedby()).length() > 0) {
				hql += " and upper(cb.name) like :createdby";
			}
			if (invoiceSupplierPojo.getStatus() != null ) {
				hql += " and upper(i.status) like :status";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and i.createdDate between :startDate and :endDate";
		}

		hql += " order by i.createdDate desc ";

		LOG.info("Query--------->> : " + hql);
		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (invoiceIds != null && invoiceIds.length > 0) {
				query.setParameter("invoiceIds", Arrays.asList(invoiceIds));
			}
		}

		if (invoiceSupplierPojo != null) {
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicename()).length() > 0) {
				query.setParameter("invoicename", "%" + invoiceSupplierPojo.getInvoicename().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referencenumber", "%" + invoiceSupplierPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoicenumber()).length() > 0) {
				query.setParameter("invoicenumber", "%" + invoiceSupplierPojo.getInvoicenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getPonumber()).length() > 0) {
				query.setParameter("ponumber", "%" + invoiceSupplierPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBuyer()).length() > 0) {
				query.setParameter("buyer", "%" + invoiceSupplierPojo.getBuyer().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + invoiceSupplierPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(invoiceSupplierPojo.getCreatedby()).length() > 0) {
				query.setParameter("createdby", "%" + invoiceSupplierPojo.getCreatedby().toUpperCase() + "%");
			}
			if (invoiceSupplierPojo.getStatus() != null) {
				query.setParameter("status", invoiceSupplierPojo.getStatus());
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);

		List<InvoiceSupplierPojo> finalList = query.getResultList();
		
		if(CollectionUtil.isNotEmpty(finalList)) {
			return finalList;
		}else {
			return null;
		}
	}

	@Override
	public long findTotalBuyerInvoiceByPoId(String poId) {
		StringBuilder hsql = new StringBuilder("select count(distinct i) from Invoice i where i.po.id = :poId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		return ((Number) query.getSingleResult()).longValue();
	}

}
