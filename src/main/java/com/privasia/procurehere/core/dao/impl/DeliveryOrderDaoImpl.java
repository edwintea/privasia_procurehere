package com.privasia.procurehere.core.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.DeliveryOrderDao;
import com.privasia.procurehere.core.entity.DeliveryOrder;
import com.privasia.procurehere.core.enums.DoStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.DoSupplierPojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("deliveryOrderDao")
public class DeliveryOrderDaoImpl extends GenericDaoImpl<DeliveryOrder, String> implements DeliveryOrderDao {

	private static final Logger LOG = LogManager.getLogger(Global.DO_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<DoSupplierPojo> findAllSearchFilterDoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructDoSearchFilterForTenantQueryForSupplier(tenantId, input, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSearchFilterDoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructDoSearchFilterForTenantQueryForSupplier(tenantId, input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalDoForSupplier(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct d) from DeliveryOrder d where d.supplier.id = :supplierId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructDoSearchFilterForTenantQueryForSupplier(String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct d) ";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.DoSupplierPojo(d.id, d.deliveryId, d.name, cb.name, d.createdDate, d.grandTotal, d.decimal, d.referenceNumber, p.poNumber, b.companyName, d.status, bu.unitName, c.currencyCode, ab.name, d.actionDate, d.doSendDate) ";
		}

		hql += " from DeliveryOrder d ";

		hql += " left outer join d.createdBy as cb";

		hql += " left outer join d.businessUnit as bu";

		hql += " left outer join d.currency as c";

		hql += " left outer join d.buyer b";

		hql += " left outer join d.po p";

		hql += " left outer join d.actionBy as ab";

		hql += " where d.supplier.id = :tenantId ";

		Boolean selectStatusFilter = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					selectStatusFilter = true;
					hql += " and d.status in (:status) ";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(d.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("buyerCompanyName")) {
					hql += " and upper(d.buyer.companyName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("currency")) {
					hql += " and upper(d.currency.currencyCode) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("poNumber")) {
					hql += " and upper(d.po.poNumber) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("createdBy")) {
					hql += " and upper(d.createdBy.name) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(d." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}

		if (!selectStatusFilter) {
			hql += " and d.status in(:status)";
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  d.createdDate between :startDate and :endDate ";
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
						hql += " d.businessUnit.unitName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("currency")) {
						hql += " d.currency.currencyCode " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("buyerCompanyName")) {
						hql += " d.buyer.companyName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("poNumber")) {
						hql += " d.po.poNumber " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("sendDate")) {
						hql += " d.doSendDate " + dir + ",";
					}else if (orderColumn.equalsIgnoreCase("createdBy")) {
						hql += " d.createdBy.name " + dir + ",";
					} else {
						hql += " d." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// by default order by created date

				hql += " order by d.createdDate desc ";
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
						query.setParameter("status", Arrays.asList(DoStatus.values()));
					} else {
						query.setParameter("status", DoStatus.fromString(cp.getSearch().getValue().toUpperCase()));
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
			query.setParameter("status", Arrays.asList(DoStatus.values()));
		}
		return query;
	}

	@Override
	public DeliveryOrder findByDoId(String doId) {
		StringBuilder hsql = new StringBuilder("from DeliveryOrder d left outer join fetch d.supplier sp left outer join fetch d.buyer bu join fetch d.createdBy cb left outer join fetch d.modifiedBy mb left outer join fetch d.currency c left outer join fetch d.po p left outer join fetch d.footer f where d.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", doId);
		DeliveryOrder deliveryOrder = (DeliveryOrder) query.getSingleResult();
		return deliveryOrder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DoSupplierPojo> findAllSearchFilterDoForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructDoSearchFilterForTenantQueryForBuyer(tenantId, input, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	@Override
	public long findTotalSearchFilterDoForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructDoSearchFilterForTenantQueryForBuyer(tenantId, input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalDoForBuyer(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct d) from DeliveryOrder d where d.buyer.id = :buyerId and d.status in (:status) ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("buyerId", tenantId);
		query.setParameter("status", Arrays.asList(DoStatus.ACCEPTED, DoStatus.DECLINED, DoStatus.DELIVERED));
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructDoSearchFilterForTenantQueryForBuyer(String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct d) ";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.DoSupplierPojo(d.id, d.deliveryId, d.name, d.createdDate, d.grandTotal, d.decimal, p.poNumber, s.companyName, d.status, bu.unitName, c.currencyCode, d.referenceNumber, ab.name, d.actionDate, d.doSendDate) ";
		}

		hql += " from DeliveryOrder d ";

		hql += " left outer join d.businessUnit as bu";

		hql += " left outer join d.currency as c";

		hql += " left outer join d.supplier s";

		hql += " left outer join d.po p";

		hql += " left outer join d.actionBy ab";

		hql += " where d.buyer.id = :tenantId ";

		Boolean selectStatusFilter = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					selectStatusFilter = true;
					hql += " and d.status in (:status) ";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(d.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("supplierCompanyName")) {
					hql += " and upper(d.supplier.companyName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("currency")) {
					hql += " and upper(d.currency.currencyCode) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("poNumber")) {
					hql += " and upper(d.po.poNumber) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(d." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}

		if (!selectStatusFilter) {
			hql += " and d.status in(:status)";
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  d.doSendDate between :startDate and :endDate ";
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
						hql += " d.businessUnit.unitName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("currency")) {
						hql += " d.currency.currencyCode " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("supplierCompanyName")) {
						hql += " d.supplier.companyName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("poNumber")) {
						hql += " d.po.poNumber " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("sendDate")) {
						hql += " d.doSendDate " + dir + ",";
					} else {
						hql += " d." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// by default order by created date

				hql += " order by d.doSendDate desc ";
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
					if (StringUtils.checkString(cp.getSearch().getValue()).length() == 0) {
						query.setParameter("status", Arrays.asList(DoStatus.ACCEPTED, DoStatus.DECLINED, DoStatus.DELIVERED));
					} else {
						query.setParameter("status", DoStatus.fromString(cp.getSearch().getValue().toUpperCase()));
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
			query.setParameter("status", Arrays.asList(DoStatus.ACCEPTED, DoStatus.DECLINED, DoStatus.DELIVERED));
		}
		return query;
	}

	@Override
	public long findTotalDoForPo(String poId) {
		StringBuilder hsql = new StringBuilder("select count(distinct d) from DeliveryOrder d where d.po.id = :poId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DoSupplierPojo> getAllBuyerDoDetailsForExcelReport(String tenantId, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate) {
		String hql = "";

		hql = constructQueryForDoDetailsForExcelReport(tenantId, doIds, doSupplierPojo, select_all, startDate, endDate);

		final Query query = getEntityManager().createQuery(hql.toString());

		LOG.info("Query ----------> " + hql);

		if (!(select_all)) {
			if (doIds != null && doIds.length > 0) {
				query.setParameter("doIds", Arrays.asList(doIds));
			}
		}

		if (doSupplierPojo != null) {
			if (StringUtils.checkString(doSupplierPojo.getDoname()).length() > 0) {
				query.setParameter("doname", "%" + doSupplierPojo.getDoname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referencenumber", "%" + doSupplierPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getDonumber()).length() > 0) {
				query.setParameter("donumber", "%" + doSupplierPojo.getDonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getPonumber()).length() > 0) {
				query.setParameter("ponumber", "%" + doSupplierPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getSupplier()).length() > 0) {
				query.setParameter("supplier", "%" + doSupplierPojo.getSupplier().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + doSupplierPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (doSupplierPojo.getStatus() != null) {
				query.setParameter("status", Arrays.asList(doSupplierPojo.getStatus()));
			} else {
				query.setParameter("status", Arrays.asList(DoStatus.ACCEPTED, DoStatus.DECLINED, DoStatus.DELIVERED));
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);

		List<DoSupplierPojo> finalList = query.getResultList();

		return finalList;
	}

	private String constructQueryForDoDetailsForExcelReport(String tenantId, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate) {

		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.DoSupplierPojo(d.id, d.deliveryId, d.name, d.createdDate, d.grandTotal, d.decimal, p.poNumber, s.companyName, d.status, bu.unitName, c.currencyCode, d.referenceNumber, ab.name, d.actionDate, d.doSendDate) ";

		hql += " from DeliveryOrder d ";

		hql += " left outer join d.businessUnit as bu";

		hql += " left outer join d.currency as c";

		hql += " left outer join d.supplier s";

		hql += " left outer join d.po p";

		hql += " left outer join d.actionBy ab";

		hql += " where d.buyer.id = :tenantId ";

		if (!(select_all)) {
			hql += " and d.id in (:doIds)";
		}

		if (doSupplierPojo != null) {
			if (StringUtils.checkString(doSupplierPojo.getDoname()).length() > 0) {
				hql += " and upper(d.name) like :doname";
			}
			if (StringUtils.checkString(doSupplierPojo.getReferencenumber()).length() > 0) {
				hql += " and upper(d.referenceNumber) like :referencenumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getDonumber()).length() > 0) {
				hql += " and upper(d.deliveryId) like :donumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :ponumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getSupplier()).length() > 0) {
				hql += " and upper(s.companyName) like :supplier";
			}
			if (StringUtils.checkString(doSupplierPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (doSupplierPojo.getStatus() != null) {
				hql += " and upper(d.status) like :status";
			} else {
				hql += " and d.status in(:status)";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and d.createdDate between :startDate and :endDate";
		}

		hql += " order by d.createdDate desc ";

		return hql;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DoSupplierPojo> getAllSupplierDoDetailsForExcelReport(String tenantId, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		String hql = "";

		hql = constructSupplierQueryForDoDetailsForExcelReport(tenantId, doIds, doSupplierPojo, select_all, startDate, endDate);

		LOG.info("Query ---------- >" + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (doIds != null && doIds.length > 0) {
				query.setParameter("doIds", Arrays.asList(doIds));
			}
		}

		if (doSupplierPojo != null) {
			if (StringUtils.checkString(doSupplierPojo.getDoname()).length() > 0) {
				query.setParameter("doname", "%" + doSupplierPojo.getDoname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referencenumber", "%" + doSupplierPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getDonumber()).length() > 0) {
				query.setParameter("donumber", "%" + doSupplierPojo.getDonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getPonumber()).length() > 0) {
				query.setParameter("ponumber", "%" + doSupplierPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getBuyer()).length() > 0) {
				query.setParameter("buyer", "%" + doSupplierPojo.getBuyer().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + doSupplierPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getDocreatedby()).length() > 0) {
				query.setParameter("docreatedby", "%" + doSupplierPojo.getDocreatedby().toUpperCase() + "%");
			}
			if (doSupplierPojo.getStatus() != null) {
				query.setParameter("status", Arrays.asList(doSupplierPojo.getStatus()));
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);

		List<DoSupplierPojo> finalList = query.getResultList();

		return finalList;
	}

	private String constructSupplierQueryForDoDetailsForExcelReport(String tenantId, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate) {

		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.DoSupplierPojo(d.id, d.deliveryId, d.name, cb.name, d.createdDate, d.grandTotal, d.decimal, d.referenceNumber, p.poNumber, b.companyName, d.status, bu.unitName, c.currencyCode, ab.name, d.actionDate, d.doSendDate) ";

		hql += " from DeliveryOrder d ";

		hql += " left outer join d.createdBy as cb";

		hql += " left outer join d.businessUnit as bu";

		hql += " left outer join d.currency as c";

		hql += " left outer join d.buyer b";

		hql += " left outer join d.po p";

		hql += " left outer join d.actionBy as ab";

		hql += " where d.supplier.id = :tenantId ";

		if (!(select_all)) {
			hql += " and d.id in (:doIds)";
		}

		if (doSupplierPojo != null) {
			if (StringUtils.checkString(doSupplierPojo.getDoname()).length() > 0) {
				hql += " and upper(d.name) like :doname";
			}
			if (StringUtils.checkString(doSupplierPojo.getReferencenumber()).length() > 0) {
				hql += " and upper(d.referenceNumber) like :referencenumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getDonumber()).length() > 0) {
				hql += " and upper(d.deliveryId) like :donumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :ponumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getBuyer()).length() > 0) {
				hql += " and upper(b.companyName) like :buyer";
			}
			if (StringUtils.checkString(doSupplierPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(doSupplierPojo.getDocreatedby()).length() > 0) {
				hql += " and upper(cb.name) like :docreatedby";
			}
			if (doSupplierPojo.getStatus() != null) {
				hql += " and upper(d.status) like :status";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and d.createdDate between :startDate and :endDate";
		}

		hql += " order by d.createdDate desc ";

		return hql;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DoSupplierPojo> getDosByPoId(String poId) {

		String hql = "select distinct new com.privasia.procurehere.core.pojo.DoSupplierPojo(d.id, d.deliveryId, d.name, d.createdDate, d.grandTotal, p.poNumber,  d.status,  d.referenceNumber, cb.name, d.decimal, d.actionDate, d.doSendDate) from DeliveryOrder d  left outer join d.po p left outer join d.createdBy cb  where p.id =:poId order by d.createdDate desc";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("poId", poId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DoSupplierPojo> getDosByPoIdForBuyer(String poId) {
		String hql = "select distinct new com.privasia.procurehere.core.pojo.DoSupplierPojo(d.id, d.deliveryId, d.name, d.createdDate, d.grandTotal, p.poNumber,  d.status,  d.referenceNumber, cb.name, d.decimal, d.actionDate, d.doSendDate) from DeliveryOrder d  left outer join d.po p left outer join d.createdBy cb  where p.id =:poId  and d.status not in (:status) order by d.createdDate desc";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("poId", poId);
		query.setParameter("status", Arrays.asList(DoStatus.CANCELLED, DoStatus.DRAFT));
		return query.getResultList();
	}

	@Override
	public long findTotalDoForBuyerPo(String poId) {
		StringBuilder hsql = new StringBuilder("select count(distinct d) from DeliveryOrder d where d.po.id = :poId and d.status not in (:status)");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		query.setParameter("status", Arrays.asList(DoStatus.CANCELLED, DoStatus.DRAFT));
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DoSupplierPojo> findDoForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate) {
		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.DoSupplierPojo(d.id, d.actionDate, d.deliveryId, d.createdDate, d.name, d.grandTotal, d.decimal, p.poNumber, s.companyName, d.status, bu.unitName, c.currencyCode, d.doSendDate, d.referenceNumber, ab.name) ";

		hql += " from DeliveryOrder d ";

		hql += " left outer join d.businessUnit as bu left outer join d.currency as c";

		hql += " left outer join d.supplier s left outer join d.po p left outer join d.actionBy ab";

		hql += " where d.buyer.id = :tenantId ";

		if (!(select_all)) {
			hql += " and d.id in (:doIds)";
		}

		if (doSupplierPojo != null) {
			if (StringUtils.checkString(doSupplierPojo.getDoname()).length() > 0) {
				hql += " and upper(d.name) like :doname";
			}
			if (StringUtils.checkString(doSupplierPojo.getReferencenumber()).length() > 0) {
				hql += " and upper(d.referenceNumber) like :referencenumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getDonumber()).length() > 0) {
				hql += " and upper(d.deliveryId) like :donumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :ponumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getSupplier()).length() > 0) {
				hql += " and upper(s.companyName) like :supplier";
			}
			if (StringUtils.checkString(doSupplierPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (doSupplierPojo.getStatus() != null) {
				hql += " and upper(d.status) like :status";
			} else {
				hql += " and d.status in (:status)";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and d.createdDate between :startDate and :endDate";
		}

		hql += " order by d.createdDate desc ";
		
		LOG.info("HQL >>>>>>>>>>>>>>>>> : "+hql);
		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (doIds != null && doIds.length > 0) {
				query.setParameter("doIds", Arrays.asList(doIds));
			}
		}

		if (doSupplierPojo != null) {
			if (StringUtils.checkString(doSupplierPojo.getDoname()).length() > 0) {
				query.setParameter("doname", "%" + doSupplierPojo.getDoname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referencenumber", "%" + doSupplierPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getDonumber()).length() > 0) {
				query.setParameter("donumber", "%" + doSupplierPojo.getDonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getPonumber()).length() > 0) {
				query.setParameter("ponumber", "%" + doSupplierPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getSupplier()).length() > 0) {
				query.setParameter("supplier", "%" + doSupplierPojo.getSupplier().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + doSupplierPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (doSupplierPojo.getStatus() != null) {
				query.setParameter("status", Arrays.asList(doSupplierPojo.getStatus()));
			} else {
				query.setParameter("status", Arrays.asList(DoStatus.ACCEPTED, DoStatus.DECLINED, DoStatus.DELIVERED));
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);

		List<DoSupplierPojo> finalList = query.getResultList();

		if(CollectionUtil.isNotEmpty(finalList)) {
			return finalList;
		}else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DoSupplierPojo> findDoForTenantIdForSupplierCsv(String tenantId, int pageSize, int pageNo, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate) {

		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.DoSupplierPojo(d.id, d.actionDate, d.doSendDate, d.createdDate, d.grandTotal, d.deliveryId, d.name, cb.name, d.decimal, d.referenceNumber, p.poNumber, b.companyName, d.status, bu.unitName, c.currencyCode, ab.name) ";

		hql += " from DeliveryOrder d ";

		hql += " left outer join d.createdBy as cb left outer join d.businessUnit as bu";

		hql += " left outer join d.currency as c left outer join d.buyer b";

		hql += " left outer join d.po p left outer join d.actionBy as ab";

		hql += " where d.supplier.id = :tenantId ";

		if (!(select_all)) {
			hql += " and d.id in (:doIds)";
		}

		if (doSupplierPojo != null) {
			if (StringUtils.checkString(doSupplierPojo.getDoname()).length() > 0) {
				hql += " and upper(d.name) like :doname";
			}
			if (StringUtils.checkString(doSupplierPojo.getReferencenumber()).length() > 0) {
				hql += " and upper(d.referenceNumber) like :referencenumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getDonumber()).length() > 0) {
				hql += " and upper(d.deliveryId) like :donumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :ponumber";
			}
			if (StringUtils.checkString(doSupplierPojo.getBuyer()).length() > 0) {
				hql += " and upper(b.companyName) like :buyer";
			}
			if (StringUtils.checkString(doSupplierPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(doSupplierPojo.getCreatedby()).length() > 0) {
				hql += " and upper(cb.name) like :docreatedby";
			}
			if (doSupplierPojo.getStatus() != null) {
				hql += " and upper(d.status) like :status";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and d.createdDate between :startDate and :endDate";
		}

		hql += " order by d.createdDate desc ";

		LOG.info("Query ---------- >" + hql);
		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (doIds != null && doIds.length > 0) {
				query.setParameter("doIds", Arrays.asList(doIds));
			}
		}

		if (doSupplierPojo != null) {
			if (StringUtils.checkString(doSupplierPojo.getDoname()).length() > 0) {
				query.setParameter("doname", "%" + doSupplierPojo.getDoname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referencenumber", "%" + doSupplierPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getDonumber()).length() > 0) {
				query.setParameter("donumber", "%" + doSupplierPojo.getDonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getPonumber()).length() > 0) {
				query.setParameter("ponumber", "%" + doSupplierPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getBuyer()).length() > 0) {
				query.setParameter("buyer", "%" + doSupplierPojo.getBuyer().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + doSupplierPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(doSupplierPojo.getCreatedby()).length() > 0) {
				query.setParameter("docreatedby", "%" + doSupplierPojo.getCreatedby().toUpperCase() + "%");
			}
			if (doSupplierPojo.getStatus() != null) {
				query.setParameter("status", Arrays.asList(doSupplierPojo.getStatus()));
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);

		List<DoSupplierPojo> finalList = query.getResultList();

		if(CollectionUtil.isNotEmpty(finalList)) {
			return finalList;
		}else {
			return null;
		}
	}

	@Override
	public long findTotalDoForBuyerPoById(String poId) {
		StringBuilder hsql = new StringBuilder("select count(distinct d) from DeliveryOrder d where d.po.id = :poId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		return ((Number) query.getSingleResult()).longValue();
	}

}
