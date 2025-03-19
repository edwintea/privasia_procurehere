package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.persistence.Query;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.PrService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PoDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.PoSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPoPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
@Repository
public class PoDaoImpl extends GenericDaoImpl<Po, String> implements PoDao {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	UserDao userDao;

	@Override
	public Po findByPoId(String poId) {
		StringBuilder hsql = new StringBuilder("from Po p left outer join fetch p.supplier sp join fetch p.createdBy cb left outer join fetch p.paymentTermes pt left outer join fetch p.modifiedBy mb left outer join fetch p.currency c left outer join fetch p.costCenter cc left outer join fetch p.deliveryAddress as da left outer join fetch da.state as dst left outer join fetch dst.country left outer join fetch p.pr pi where p.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", poId);
		Po po = (Po) query.getSingleResult();
		return po;
	}

	@Override
	public Po getPoItemByPoNumberAndBuyerId(String poNumber, String buyerId) {
		StringBuilder hsql = new StringBuilder("from Po pi where pi.poNumber = :poNumber and pi.buyer.id =:buyerId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poNumber", poNumber);
		query.setParameter("buyerId", buyerId);
		Po po = (Po) query.getSingleResult();
		return po;
	}

	@Override
	public Po findPoSupplierByPoId(String poId) {
		StringBuilder hsql = new StringBuilder("select distinct p from Po p left outer join fetch p.supplier sp left outer join fetch p.modifiedBy mb left outer join fetch p.poItems where p.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", poId);
		Po po = (Po) query.getSingleResult();
		po = (Po) getEntityManager().unwrap(SessionImplementor.class).getPersistenceContext().unproxy(po);
		return po;
	}

	@Override
	public long findCountOfPoForSupplierBasedOnStatus(String tenantId, PoStatus status) {
		StringBuilder hsql = new StringBuilder("select count(distinct p) from Po p  left outer join p.supplier as fs where fs.supplier.id = :tenantId ");
		if(status.equals(PoStatus.PENDING) || status.equals(PoStatus.SUSPENDED) || status.equals(PoStatus.READY))
			hsql.append("and (p.status in (:status) or (p.status= '"+PoStatus.READY+"' and p.oldStatus is not null) )");
		else
			hsql.append("and p.status = :status");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		if(status.equals(PoStatus.PENDING) || status.equals(PoStatus.SUSPENDED)|| status.equals(PoStatus.READY)){

			query.setParameter("status", Arrays.asList(PoStatus.PENDING,PoStatus.SUSPENDED));
		}
		else
			query.setParameter("status", status);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoSupplierPojo> findAllSearchFilterPoForSupplierByStatus(String tenantId, TableDataInput input, PoStatus status) {
		final Query query = constructPoForTenantQueryForSupplierByStatus(tenantId, input, false, status);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	@Override
	public long findTotalSearchFilterPoForSupplierByStatus(String tenantId, TableDataInput input, PoStatus status) {
		final Query query = constructPoForTenantQueryForSupplierByStatus(tenantId, input, true, status);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructPoForTenantQueryForSupplierByStatus(String tenantId, TableDataInput tableParams, boolean isCount, PoStatus status) {
		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct p) ";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.PoSupplierPojo(p.id, p.name, p.modifiedDate, p.grandTotal, p.createdDate,p.decimal, p.referenceNumber, p.description, p.poNumber, p.status, bu.unitName, c.currencyCode, b.companyName, p.actionDate, r) ";
		}

		hql += " from Po p ";

		hql += " left outer join p.supplier as fs";

		hql += " left outer join p.businessUnit as bu";

		hql += " left outer join p.currency as c";

		hql += " left outer join p.buyer b";

		hql += " left outer join PoFinanceRequest r on r.po.id = p.id and r.buyer.id = p.buyer.id ";

		hql += " where fs.supplier.id = :tenantId";

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("businessUnit")) {
					hql += " and upper(p.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("currency")) {
					hql += " and upper(p.currency.currencyCode) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("buyerCompanyName")) {
					hql += " and upper(p.buyer.companyName) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		hql += " and p.status = :status ";

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("businessUnit")) {
						hql += " p.businessUnit.unitName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("currency")) {
						hql += " p.currency.currencyCode " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("buyerCompanyName")) {
						hql += " p.buyer.companyName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("acceptRejectDate")) {
						hql += " p.actionDate " + dir + ",";
					} else {
						hql += " p." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// by default order by created date

				hql += " order by p.createdDate desc ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " + cp.getSearch().getValue());
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}
		query.setParameter("status", status);
		return query;

	}

	@Override
	public long findTotalPo(String tenantId) {
		// StringBuilder hsql = new StringBuilder("select count(distinct p) from Po p where p.buyer.id = :buyerId and
		// p.status = :status and p.erpPrTransferred = :erpPrTransferred");
		StringBuilder hsql = new StringBuilder("select count(distinct p) from Po p where p.buyer.id = :buyerId and p.erpPrTransferred = :erpPrTransferred");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("buyerId", tenantId);
		// query.setParameter("status", PrStatus.APPROVED);
		query.setParameter("erpPrTransferred", Boolean.FALSE);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Po> findAllPo(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructPoForTenantQuery(tenantId, input, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	private Query constructPoForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct p) ";
		} else {
			hql += "select distinct NEW Po(p.id, p.name, p.modifiedDate, p.grandTotal, cb, mb, p.createdDate, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.poNumber,  bu.unitName, sup.companyName, p.supplierName, p.status,p.isPoReportSent,o,p.orderedDate,c.currencyCode,p.actionDate) ";
		}

		hql += " from Po p ";

		// If this is not a count query, only then add the join fetch. Count
		// query does not require its
		if (!isCount) {
			hql += " join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b left outer join p.orderedBy as o ";
		}

		hql += " left outer join p.businessUnit as bu left outer join p.supplier as fs  left outer join fs.supplier as sup left outer join p.currency as c ";

		hql += " where p.buyer.id = :tenantId and p.erpPrTransferred = :erpPrTransferred ";

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					hql += " and p.status in (:status) ";
				} else {
					if (cp.getData().replace(".", "").equalsIgnoreCase("supplierfullName")) {
						hql += " and (upper(p.supplierName) like (:" + cp.getData().replace(".", "") + ") or  upper(sup.companyName) like (:" + cp.getData().replace(".", "") + ")) ";
					} else {
						hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

					}
				}
			}
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  p.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();

					if (orderColumn.equalsIgnoreCase("supplier.fullName")) {
						hql += " sup.companyName " + dir + ", p.supplierName " + dir;
					} else {

						hql += " p." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// by default order by created date
				hql += " order by p.createdDate desc ";
			}
		}

		LOG.info("*********prdraftsQuery*********** " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						List<PoStatus> statuses = Arrays.asList(PoStatus.values());
						query.setParameter("status", statuses);
					} else {
						query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));
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
		query.setParameter("erpPrTransferred", Boolean.FALSE);
		return query;
	}

	@Override
	public long findTotalFilteredPo(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructPoForTenantQuery(tenantId, input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoSupplierPojo> findSearchPoByIds(String tenantId, String[] poArr, boolean select_all, Date startDate, Date endDate, SearchFilterPoPojo searchFilterPoPojo) {
		String hql = "select distinct new com.privasia.procurehere.core.pojo.PoSupplierPojo(p.id, p.name, p.modifiedDate, p.grandTotal, p.createdDate,p.decimal, p.referenceNumber ,p.description , p.poNumber, p.status, bu.unitName, c.currencyCode, b.companyName, p.actionDate, r) ";

		hql += " from Po p ";

		hql += " left outer join p.supplier as fs";

		hql += " left outer join p.businessUnit as bu";

		hql += " left outer join p.currency as c";

		hql += " left outer join p.buyer b";

		hql += " left outer join PoFinanceRequest r on r.po.id = p.id and r.buyer.id = p.buyer.id ";

		hql += " where fs.supplier.id = :tenantId ";

		// hql += "and p.status in(:status) ";

		if (!(select_all)) {
			if (poArr != null && poArr.length > 0) {
				hql += " and p.id in (:poArr)";
				LOG.info("select_all" + select_all);
			}
		}
		if (searchFilterPoPojo != null) {
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				hql += " and upper(p.name) like :name";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBuyer()).length() > 0) {
				hql += " and upper(b.companyName) like :buyerCompanyName";
				LOG.info("buyerCompanyName" + searchFilterPoPojo.getBuyer());
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :poNumber";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				hql += " and upper(c.currencyCode) like :currency";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getDescription()).length() > 0) {
				hql += " and upper(p.description) like :description";
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				hql += " and upper(p.status) like :postatus";

			} else {
				hql += " and upper(p.status) in :postatus)";
			}
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and p.createdDate between :startDate and :endDate ";
		}

		hql += " order by p.createdDate desc";

		final Query query = getEntityManager().createQuery(hql);

		if (!(select_all)) {
			if (poArr != null && poArr.length > 0) {
				query.setParameter("poArr", Arrays.asList(poArr));
			}
		}

		if (searchFilterPoPojo != null) {
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				query.setParameter("name", "%" + searchFilterPoPojo.getPoname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBuyer()).length() > 0) {
				query.setParameter("buyerCompanyName", "%" + searchFilterPoPojo.getBuyer().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPonumber()).length() > 0) {
				query.setParameter("poNumber", "%" + searchFilterPoPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				query.setParameter("currency", "%" + searchFilterPoPojo.getCurrency().toUpperCase() + "%");
			}

			if (StringUtils.checkString(searchFilterPoPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + searchFilterPoPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getDescription()).length() > 0) {
				query.setParameter("description", "%" + searchFilterPoPojo.getDescription().toUpperCase() + "%");
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				LOG.info("getPostatus" + searchFilterPoPojo.getPostatus());
				query.setParameter("postatus", searchFilterPoPojo.getPostatus());
			} else {
				query.setParameter("postatus", Arrays.asList(PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED));
			}

		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		// query.setParameter("status", Arrays.asList(PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED,
		// PoStatus.CANCELLED));
		return query.getResultList();

	}

	@Override
	public long findPoCountBasedOnStatusAndTenant(String loggedInUser, String tenantId, PoStatus status) {
		String hql = "select count(distinct p) from Po p where p.buyer.id = :tenantId and p.erpPrTransferred = :erpPrTransferred and p.status =:status ";
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			hql += " and p.createdBy.id =:createdBy ";
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("erpPrTransferred", Boolean.FALSE);
		query.setParameter("status", status);
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("createdBy", loggedInUser);
		}

		LOG.info("SQL String for PO Dashboard : "+hql.toString());
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findPoCountBasedOnStatusAndTenantAndBusinessUnit(String loggedInUser, String tenantId, PoStatus status,List<String> businessUnitIds) {
		String hql = "select count(distinct p) from Po p where p.buyer.id = :tenantId and p.status =:status ";
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			//hql += " and p.createdBy.id =:createdBy ";
		}

		if(!businessUnitIds.isEmpty()){
			hql += " and p.businessUnit.id in (:businessUnitIds) ";
		}

		if(PoStatus.PENDING.equals(status)){
			hql += " and p.revised = :revised ";
			hql += " and p.cancelled = :cancelled ";
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		//query.setParameter("erpPrTransferred", Boolean.FALSE);
		query.setParameter("status", status);
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			//query.setParameter("createdBy", loggedInUser);
		}

		if(PoStatus.PENDING.equals(status)){
			query.setParameter("revised", Boolean.FALSE);
			query.setParameter("cancelled", Boolean.FALSE);
		}

		if(!businessUnitIds.isEmpty()){
			query.setParameter("businessUnitIds", businessUnitIds);
		}

		LOG.info("SQL String for PO Dashboard : "+status+" >>> "+hql.toString());
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findPoPendingCountBaseOnTenantAndBuisnessUnit(String loggedInUser, String tenantId,PoStatus status,List<String> businessUnitIds) {
		// HQL query to count distinct purchase orders with revised set to FALSE and cancelled to FALSE

		// HQL query to count distinct purchase orders with revised set to TRUE
		String hql = "select count(distinct p) from Po p where p.buyer.id = :tenantId " +
				"and p.revised = :revised and p.cancelled = :cancelled and p.status= :status ";

		if(!businessUnitIds.isEmpty()){
			hql += " and p.businessUnit.id in (:businessUnitIds) ";
		}

		if (StringUtils.checkString(loggedInUser).length() > 0) {
			//hql += " and p.createdBy.id = :createdBy ";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", status);
		query.setParameter("revised", Boolean.FALSE);
		query.setParameter("cancelled", Boolean.FALSE);

		if (StringUtils.checkString(loggedInUser).length() > 0) {
			//query.setParameter("createdBy", loggedInUser);
		}

		if(!businessUnitIds.isEmpty()){
			query.setParameter("businessUnitIds", businessUnitIds);
		}

		LOG.info("SQL String for PO Dashboard Pending >>> Creation : "+hql.toString());

		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findPoRevisedCountBaseOnTenantAndBuisnessUnit(String loggedInUser, String tenantId,List<String> businessUnitIds) {
		// HQL query to count distinct purchase orders with revised set to TRUE
		String hql = "select count(distinct p) from Po p where p.buyer.id = :tenantId " +
				"and p.revised = :revised";

		if(!businessUnitIds.isEmpty()){
			hql += " and p.businessUnit.id in (:businessUnitIds) ";
		}

		if (StringUtils.checkString(loggedInUser).length() > 0) {
			//hql += " and p.createdBy.id = :createdBy ";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("revised", Boolean.TRUE);

		if (StringUtils.checkString(loggedInUser).length() > 0) {
			//query.setParameter("createdBy", loggedInUser);
		}

		if(!businessUnitIds.isEmpty()){
			query.setParameter("businessUnitIds", businessUnitIds);
		}

		LOG.info("SQL String for PO Dashboard Pending >>> Revision : "+hql.toString());

		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findPoOnCancellationCountBaseOnTenantAndBuisnessUnit(String loggedInUser, String tenantId,List<String> businessUnitIds) {
		// HQL query to count distinct purchase orders with revised set to TRUE
		String hql = "select count(distinct p) from Po p where p.buyer.id = :tenantId " +
				"and p.cancelled = :cancelled";

		if(!businessUnitIds.isEmpty()){
			hql += " and p.businessUnit.id in (:businessUnitIds) ";
		}

		if (StringUtils.checkString(loggedInUser).length() > 0) {
			//hql += " and p.createdBy.id =:createdBy ";
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("cancelled", Boolean.TRUE);
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			//query.setParameter("createdBy", loggedInUser);
		}

		if(!businessUnitIds.isEmpty()){
			query.setParameter("businessUnitIds", businessUnitIds);
		}

		LOG.info("SQL String for PO Dashboard Pending >>> Cancellation : "+hql.toString());

		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Po> findAllPoByStatus(String loggedInUser, String tenantId, TableDataInput input, PoStatus status) {
		final Query query = constructPoForTenantQueryByStatus(loggedInUser, tenantId, input, false, status);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	private Query constructPoForTenantQueryByStatus(String loggedInUser, String tenantId, TableDataInput tableParams, boolean isCount, PoStatus status) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct p) ";
		} else {
			hql += "select distinct NEW Po(p.id, p.name, p.modifiedDate, p.grandTotal, cb, mb, p.createdDate, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.poNumber,  bu.unitName, sup.companyName, p.supplierName, p.status,p.isPoReportSent,o,p.orderedDate,c.currencyCode,p.actionDate) ";
		}

		hql += " from Po p ";

		// If this is not a count query, only then add the join fetch. Count
		// query does not require its
		if (!isCount) {
			hql += " join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b  left outer join p.orderedBy as o";
		}

		hql += " left outer join p.businessUnit as bu left outer join p.supplier as fs  left outer join fs.supplier as sup left outer join p.currency as c ";

		hql += " where p.buyer.id = :tenantId and p.erpPrTransferred = :erpPrTransferred and p.status =:status ";
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			hql += " and p.createdBy.id =:createdBy ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().replace(".", "").equalsIgnoreCase("supplierfullName")) {
					hql += " and (upper(p.supplierName) like (:" + cp.getData().replace(".", "") + ") or  upper(sup.companyName) like (:" + cp.getData().replace(".", "") + ")) ";
				} else {
					hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

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

					if (orderColumn.equalsIgnoreCase("supplier.fullName")) {
						hql += " sup.companyName " + dir + ", p.supplierName " + dir;
					} else {

						hql += " p." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				// by default order by created date
				hql += " order by p.createdDate desc ";
			}
		}

		LOG.info("*********prdraftsQuery*********** " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}

		query.setParameter("erpPrTransferred", Boolean.FALSE);
		query.setParameter("status", status);
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("createdBy", loggedInUser);
		}
		return query;
	}

	@Override
	public long findTotalFilteredPoByStatus(String loggedInUserId, String tenantId, TableDataInput input, PoStatus status) {
		final Query query = constructPoForTenantQueryByStatus(loggedInUserId, tenantId, input, true, status);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Po> findAllSearchFilterPo(String loggedInUserId, String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate,String viewType,String status,List<String> businessUnitIds) {
		Query query = constructAllPoForTenantQuery(loggedInUserId, loggedInUserTenantId, input, false, startDate, endDate,viewType,status,businessUnitIds);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSearchFilterPoCount(String loggedInUserId, String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate,String viewType,String status,List<String> businessUnitIds) {
		final Query query = constructAllPoForTenantQuery(loggedInUserId, loggedInUserTenantId, input, true, startDate, endDate,viewType,status,businessUnitIds);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructAllPoForPoDashboardQuery(String loggedInUserId, String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate, String viewType,String status,List<String> businessUnitIds) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct p) ";
		} else {
			hql += "select distinct NEW Po(p.id, p.name, p.modifiedDate, p.grandTotal, cb, mb, p.createdDate, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.poNumber,  bu.unitName, sup.companyName, p.supplierName, p.status,p.isPoReportSent,o,p.orderedDate,c.currencyCode,p.actionDate, pr, p.poRevisedDate, o.name, pr.prId) ";
		}

		hql += " from Po p ";

		// If this is not a count query, only then add the join fetch. Count
		// query does not require its
		if (!isCount) {
			hql += " join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b  left outer join p.orderedBy as o";
		}

		hql += " left outer join p.businessUnit as bu left outer join p.supplier as fs  left outer join fs.supplier as sup left outer join p.currency as c ";

		hql += " left outer join p.pr as pr";


		//PH-4113
		if (viewType.equals("me")) {
			if (StringUtils.checkString(loggedInUserId).length() > 0) {
				hql += " where p.createdBy.id =:createdBy";
			}
		}else{
			hql += " where p.buyer.id = :tenantId ";

			if(!businessUnitIds.isEmpty()){
				hql += " and p.businessUnit.id in (:businessUnitIds) ";
			}

		}

		boolean isSelectFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and p.status in (:status) ";
					isSelectFilterOn = true;
				} else if (cp.getData().replace(".", "").equalsIgnoreCase("supplierfullName")) {
					hql += " and (upper(p.supplierName) like (:" + cp.getData().replace(".", "") + ") or  upper(sup.companyName) like (:" + cp.getData().replace(".", "") + ")) ";
				} else {
					hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}

		if(status !=null){
			isSelectFilterOn=true;
			if(status.equals("ONCANCELLATION")) {
				hql += " and p.cancelled =:cancelled ";
			}else if(status.equals("REVISION")){
				hql += " and p.revised =:revised ";
			}else{
				hql += " and p.status =:status ";
			}


		}

		if (!isSelectFilterOn ) {
			hql += " and p.status in (:statuses) ";
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  p.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();

					if (orderColumn.equalsIgnoreCase("supplier.fullName")) {
						hql += " sup.companyName " + dir + ", p.supplierName " + dir;
					} else {

						hql += " p." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by p.createdDate desc ";
			}
		}

		LOG.info("*********viewType*********** " + viewType);
		LOG.info("*********userLoggedIn*********** " + loggedInUserId);
		LOG.info("*********posQuery*********** " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		if(!businessUnitIds.isEmpty() && !viewType.equals("me")){
			query.setParameter("tenantId", tenantId);
			query.setParameter("businessUnitIds", businessUnitIds);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						List<PoStatus> statuss = Arrays.asList(PoStatus.DRAFT,PoStatus.SUSPENDED,PoStatus.CLOSED,PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE, PoStatus.PENDING);
						query.setParameter("status", statuss);
					} else {
						query.setParameter("status", PoStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		if(status !=null){
			if(status.equals("ONCANCELLATION")) {
				query.setParameter("cancelled",Boolean.TRUE);
			}else if(status.equals("REVISION")){
				query.setParameter("revised",Boolean.TRUE);
			}else{
				query.setParameter("status",PoStatus.fromString(status));
			}

		}
		if (!isSelectFilterOn ) {
			query.setParameter("statuses", Arrays.asList(PoStatus.DRAFT,PoStatus.SUSPENDED,PoStatus.CLOSED,PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE, PoStatus.PENDING));
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		if (viewType.equals("me")) {
			if (StringUtils.checkString(loggedInUserId).length() > 0) {
				query.setParameter("createdBy", loggedInUserId);
			}
		}
		return query;
	}

	private Query constructAllPoForTenantQuery(String loggedInUserId, String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate, String viewType,String status,List<String> businessUnitIds) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct p) ";
		} else {
			hql += "select distinct NEW Po(p.id, p.name, p.modifiedDate, p.grandTotal, cb, mb, p.createdDate, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.poNumber,  bu.unitName, sup.companyName, p.supplierName, p.status,p.isPoReportSent,o,p.orderedDate,c.currencyCode,p.actionDate, pr, p.poRevisedDate, o.name, pr.prId, p.fromIntegration) ";
		}

		hql += " from Po p ";

		// If this is not a count query, only then add the join fetch. Count
		// query does not require its
		if (!isCount) {
			hql += " join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b  left outer join p.orderedBy as o";
		}

		hql += " left outer join p.businessUnit as bu left outer join p.supplier as fs  left outer join fs.supplier as sup left outer join p.currency as c left outer join p.poTeamMembers as ptm left outer join p.approvals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au ";

		hql += " left outer join p.pr as pr";

		hql += " where p.buyer.id = :tenantId ";


		if(!businessUnitIds.isEmpty() && viewType.equals("all")){
			hql += " and p.businessUnit.id in (:businessUnitIds) ";
		}

		//PH-4113
		if (viewType.equals("me")) {
			if (StringUtils.checkString(loggedInUserId).length() > 0) {
				hql += " and (p.createdBy.id =:createdBy or ptm.user.id=:createdBy or au.id=:createdBy)";
			}
		}

		boolean isSelectFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and p.status in (:status) ";
					isSelectFilterOn = true;
				} else if (cp.getData().replace(".", "").equalsIgnoreCase("supplierfullName")) {
					hql += " and (upper(p.supplierName) like (:" + cp.getData().replace(".", "") + ") or  upper(sup.companyName) like (:" + cp.getData().replace(".", "") + ")) ";
				} else {
					hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}

		if(status !=null){
			isSelectFilterOn=true;

			if(status.equals("PENDING")) {
				hql += " and p.revised =:revised ";
				hql += " and p.cancelled =:cancelled ";
			}

			if(status.equals("ONCANCELLATION")) {
				hql += " and p.cancelled =:cancelled ";
			}else if(status.equals("REVISION")){
				hql += " and p.revised =:revised ";
			}else{
				hql += " and p.status =:status ";
			}
		}

		if (!isSelectFilterOn ) {
			hql += " and p.status in (:statuses) ";
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  p.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();

					if (orderColumn.equalsIgnoreCase("supplier.fullName")) {
						hql += " sup.companyName " + dir + ", p.supplierName " + dir;
					} else {

						hql += " p." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by p.createdDate desc ";
			}
		}

		LOG.info("*********viewType*********** " + viewType);
		LOG.info("*********userLoggedIn*********** " + loggedInUserId);
		LOG.info("*********posQuery*********** " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		if(!businessUnitIds.isEmpty() && viewType.equals("all")){
			query.setParameter("businessUnitIds", businessUnitIds);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						List<PoStatus> statuss = Arrays.asList(PoStatus.DRAFT,PoStatus.SUSPENDED,PoStatus.CLOSED,PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE, PoStatus.PENDING);
						query.setParameter("status", statuss);
					} else {
						query.setParameter("status", PoStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		if(status !=null){
			if(status.equals("PENDING")) {
				query.setParameter("cancelled",Boolean.FALSE);
				query.setParameter("revised",Boolean.FALSE);
			}
			if(status.equals("ONCANCELLATION")) {
				query.setParameter("cancelled",Boolean.TRUE);
			}else if(status.equals("REVISION")){
				query.setParameter("revised",Boolean.TRUE);
			}else{
				query.setParameter("status",PoStatus.fromString(status));
			}

		}
		if (!isSelectFilterOn ) {
			query.setParameter("statuses", Arrays.asList(PoStatus.DRAFT,PoStatus.SUSPENDED,PoStatus.CLOSED,PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE, PoStatus.PENDING));
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		if (viewType.equals("me")) {
			if (StringUtils.checkString(loggedInUserId).length() > 0) {
				query.setParameter("createdBy", loggedInUserId);
			}
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Po> findSearchBuyerPoByIds(String tenantId, String[] poArr, boolean select_all, Date startDate, Date endDate, SearchFilterPoPojo searchFilterPoPojo, String loggedInUser) {
		String hql = "select p from Po p inner join p.createdBy cr left outer join p.orderedBy as o left outer join p.businessUnit as bu left outer join p.supplier as fs left outer join fs.supplier as sup left outer join p.currency c left outer join p.pr as pr";

		// hql += "where p.status in (:status) and"; //commented bcoz we r setting status in searchFilterPoPojo for both
		// conditions

		hql += " where p.buyer.id = :tenantId";

		if (!(select_all)) {
			if (poArr != null && poArr.length > 0) {
				hql += " and p.id in (:poArr)";
				LOG.info("select_all" + select_all);
			}
		}
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			hql += " and p.createdBy.id = :createdBy ";
		}

		if (searchFilterPoPojo != null) {
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				hql += " and upper(p.name) like :poName";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getSupplier()).length() > 0) {
				hql += " and upper(sup.companyName) like :suppliername";
				LOG.info("getSuppliername" + searchFilterPoPojo.getSupplier());
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :ponumber";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPocreatedby()).length() > 0) {
				hql += " and upper(cr.name) like :pocreatedby";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				hql += " and upper(c.currencyCode) like :currency";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPoorderedby()).length() > 0) {
				hql += " and upper(o.name) like :orderedby";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPrnumber()).length() > 0) {
				hql += " and upper(pr.prId) like :prnumber";
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				hql += " and upper(p.status) like :postatus";
			} else {
				hql += " and upper(p.status) in(:postatus)";
			}
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and p.createdDate between :startDate and :endDate ";
		}

		hql += " order by p.createdDate desc ";

		final Query query = getEntityManager().createQuery(hql);

		if (!(select_all)) {
			if (poArr != null && poArr.length > 0) {
				query.setParameter("poArr", Arrays.asList(poArr));
			}
		}
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("createdBy", loggedInUser);
		}

		if (searchFilterPoPojo != null) {
			LOG.info("---------name -----" + searchFilterPoPojo.getPoname());
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				query.setParameter("poName", "%" + searchFilterPoPojo.getPoname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getSupplier()).length() > 0) {
				query.setParameter("suppliername", "%" + searchFilterPoPojo.getSupplier().toUpperCase() + "%");
				LOG.info("getSuppliername" + searchFilterPoPojo.getSupplier().toUpperCase());
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPonumber()).length() > 0) {
				query.setParameter("ponumber", "%" + searchFilterPoPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPocreatedby()).length() > 0) {
				query.setParameter("pocreatedby", "%" + searchFilterPoPojo.getPocreatedby().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + searchFilterPoPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				query.setParameter("currency", searchFilterPoPojo.getCurrency());
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPoorderedby()).length() > 0) {
				query.setParameter("orderedby", "%" + searchFilterPoPojo.getPoorderedby().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPrnumber()).length() > 0) {
				query.setParameter("prnumber", "%" + searchFilterPoPojo.getPrnumber().toUpperCase() + "%");
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				LOG.info("getPostatus" + searchFilterPoPojo.getPostatus());
				query.setParameter("postatus", searchFilterPoPojo.getPostatus());
			} else {
				query.setParameter("postatus", Arrays.asList(PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED));
			}

		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		// List<PoStatus> statuses = Arrays.asList(PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED,
		// PoStatus.DECLINED, PoStatus.CANCELLED);
		query.setParameter("tenantId", tenantId);
		// query.setParameter("status", statuses);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Po> findSupplierAllPo(String tenantId) {

		String hql = new String("select distinct p from Po p left outer join fetch p.createdBy cb left outer join fetch p.costCenter cc left outer join fetch p.currency pc left outer join fetch p.supplier fs left outer join fetch p.poItems pi left outer join fetch p.buyer b ");
		hql += " where fs.supplier.id = :tenantId and p.status not in (:status)  and p.isPo = true and p.createdDate is not null and  p.poNumber is not null";
		List<PrStatus> statuses = Arrays.asList(PrStatus.CANCELED, PrStatus.DRAFT);
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", statuses);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Po> findAllPoforSharingAll(String supplierId, String buyerId) {

		String hql = "select p from Po p left outer join p.supplier as fs  where fs.supplier.id = :tenantId and p.status= :status ";

		if (StringUtils.checkString(buyerId).length() > 0) {
			hql += " and p.buyer.id =:buyerId";
		}
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", supplierId);
		query.setParameter("status", PoStatus.ACCEPTED);
		if (StringUtils.checkString(buyerId).length() > 0) {
			query.setParameter("buyerId", buyerId);
		}

		return query.getResultList();
	}

	@Override
	public String getBusineessUnitname(String poId) {
		StringBuilder hsql = new StringBuilder("select bu.displayName from Po p left outer join p.businessUnit as bu where p.id =:poId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		return (String) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Po> getAllPoFromGlobalSearch(String searchVal, String tenantId, String status, String type, Date startDate, Date endDate) {
		LOG.info("searchValue:" + searchVal + " tenantId : " + tenantId);

		List<PoStatus> statusList = new ArrayList<PoStatus>();

		if (StringUtils.checkString(status).length() > 0) {
			String[] statusArray = status.split(",");
			if (statusArray != null && statusArray.length > 0) {
				for (String ss : statusArray) {
					statusList.add(PoStatus.valueOf(ss));
				}
			}
		}
		StringBuilder hsql;
		hsql = new StringBuilder("select p from Po p left outer join fetch p.businessUnit as bu left outer join fetch p.supplier as fs where fs.supplier.id = :tenantId ");

		if (StringUtils.checkString(searchVal).length() > 0) {
			LOG.info(searchVal);
			hsql.append(" and ((upper(p.poNumber) like :searchVal) or (upper(p.referenceNumber) like :searchVal) or (upper(p.name) like :searchVal) or (upper(p.description) like :searchVal) or (upper(bu.unitName) like :searchVal)) ");
		}
		hsql.append(" and (p.status in (:status)) ");
		if (startDate != null && endDate != null) {
			hsql.append(" and (p.createdDate between :startDate and :endDate) ");
		}
		LOG.info("HSQL :" + hsql);
		Query query = getEntityManager().createQuery(hsql.toString());

		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(searchVal).length() > 0) {
			query.setParameter("searchVal", "%" + searchVal.toUpperCase() + "%");
		}
		if (StringUtils.checkString(status).length() > 0) {
			query.setParameter("status", statusList);
		} else {
			query.setParameter("status", Arrays.asList(PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED));
		}
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		List<Po> list = query.getResultList();
		LinkedHashSet<Po> resultList = new LinkedHashSet<Po>();
		for (Po po : list) {
			resultList.add(po);
		}
		return new ArrayList<Po>(resultList);
	}

	@Override
	public Po findByPrId(String prId) {
		StringBuilder hsql = new StringBuilder("from Po p where p.pr.id = :prId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("prId", prId);
		Po po = (Po) query.getSingleResult();
		return po;
	}

	@Override
	public Po findPoById(String prId) {
		StringBuilder hsql = new StringBuilder("from Po p where p.id = :prId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("prId", prId);
		Po po = (Po) query.getSingleResult();
		return po;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Po findSupplierByFavSupplierId(String id) {
		StringBuilder hsql = new StringBuilder("from Po p left outer join fetch p.supplier as fs where fs.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		List<Po> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Po findByPoNomber(String poNumber, String tenantId) {
		StringBuilder hsql = new StringBuilder("from Po p where p.poNumber = :poNumber and p.buyer.id = :tenantId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poNumber", poNumber);
		query.setParameter("tenantId", tenantId);
		List<Po> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		}
		return null;
	}

	@Override
	public Po findByPoReferenceNomber(String poReferenceNumber, String tenantId) {
		StringBuilder hsql = new StringBuilder("from Po p where p.referenceNumber = :poReferenceNumber and p.buyer.id = :tenantId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poReferenceNumber", poReferenceNumber);
		query.setParameter("tenantId", tenantId);
		List<Po> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		}
		return null;
	}

	@Override
	public Po getMobilePoDetails(String id) {
		StringBuilder hsql = new StringBuilder("select p from Po p left outer join fetch p.createdBy cb left outer join fetch p.currency pc left outer join fetch p.poItems pi where p.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		return (Po) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Po> findAllPoByTenantId(String tenantId) {
		StringBuilder hsql = new StringBuilder("select p from Po p where p.buyer.id = :tenantId and p.status = :status");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", PoStatus.ORDERED);
		List<Po> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
	}

	@Override
	public long findTotalPoSummaryCountForCsv(String tenantId, Date startDate, Date endDate) {
		LOG.info("Tenant in DaoImpl for count........." + tenantId);
		String hql = "select count(p) from Po p";
		hql += " left outer join p.supplier as fs";
		hql += " where p.buyer.id = :tenantId";

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Po>  findPoForTenantIdForCsv(String tenantId, int pAGE_SIZE, int pageNo, String[] poIds, SearchFilterPoPojo searchFilterPoPojo, boolean select_all, Date startDate, Date endDate, String loggedInUser,String poType, List<String> businessUnitIds,String status) {
		LOG.info("PoIds in Dao............ " + poIds + " Tenant Id" + tenantId + " Logged In User " +loggedInUser);

		String hql = "";
		hql += "select distinct NEW com.privasia.procurehere.core.entity.Po(p.id, p.name, p.modifiedDate, p.grandTotal, cb, mb, p.createdDate, cb.name, mb.name, p.decimal, p.referenceNumber, p.description, p.poNumber, bu.unitName, sup.companyName, p.supplierName, p.status, o, p.orderedDate, c.currencyCode, p.actionDate, pr, p.poRevisedDate) from Po p ";
		hql += " join p.createdBy as cb left outer join p.modifiedBy mb left outer join p.buyer b left outer join p.orderedBy as o";

		hql += " left outer join p.businessUnit as bu left outer join p.supplier as fs  left outer join fs.supplier as sup left outer join p.currency as c ";

		hql += " left outer join p.pr as pr left outer join p.poTeamMembers as ptm left outer join p.approvals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au";

		//hql += " where p.buyer.id = :tenantId and p.status in (:status)";
		hql += " where p.buyer.id = :tenantId ";
		if (!(select_all)) {
			if (poIds != null && poIds.length > 0) {
				hql += " and p.id in(:poArr)";
				LOG.info("select_all " + select_all);
			}
			if(status.equals("REVISION")){
				hql += " and p.revised =:revised ";
			}else{
				hql += " and p.status =:status ";
			}
		}
		else{
			if(status !=null){

				if(status.equals("PENDING")) {
					hql += " and p.revised =:revised ";
					hql += " and p.cancelled =:cancelled ";
				}

				if(status.equals("ONCANCELLATION")) {
					hql += " and p.cancelled =:cancelled ";
				}else if(status.equals("REVISION")){
					hql += " and p.revised =:revised ";
				}else{
					hql += " and p.status =:status ";
				}
			}
		}

		if (poType.equals("me")) {
			if (StringUtils.checkString(loggedInUser).length() > 0) {
				hql += " and (p.createdBy.id =:userId or ptm.user.id=:userId or au.id=:userId)";
			}
		}else{
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				hql+= "  AND bu.id IN (:businessUnitIds) ";
			}
		}

		List<PoStatus> list = new ArrayList<PoStatus>();
		if (searchFilterPoPojo != null) {
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				hql += " and upper(p.name) like :poName";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getSupplier()).length() > 0) {
				hql += " and upper(sup.companyName) like :suppliername";
				LOG.info("getSuppliername" + searchFilterPoPojo.getSupplier());
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPonumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :ponumber";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPocreatedby()).length() > 0) {
				hql += " and upper(cb.name) like :pocreatedby";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				hql += " and upper(c.currencyCode) like :currency";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPoorderedby()).length() > 0) {
				hql += " and upper(o.name) like :orderedby";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPrnumber()).length() > 0) {
				hql += " and upper(pr.prId) like :prnumber";
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				hql += " and upper(p.status) like :postatus";
			} else {
				hql += " and upper(p.status) in(:postatus)";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and p.createdDate between :startDate and :endDate ";
		}

		hql += " order by p.createdDate desc ";

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);

		if (!(select_all)) {
			if (poIds != null && poIds.length > 0) {
				query.setParameter("poArr", Arrays.asList(poIds));
			}
		}
		if(poType.equals("me")){
			if (StringUtils.checkString(loggedInUser).length() > 0)
				query.setParameter("userId", loggedInUser);
		}else {
			if (businessUnitIds != null && !businessUnitIds.isEmpty())
				query.setParameter("businessUnitIds", businessUnitIds);
		}


		if (searchFilterPoPojo != null) {
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				query.setParameter("poName", "%" + searchFilterPoPojo.getPoname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getSupplier()).length() > 0) {
				query.setParameter("suppliername", "%" + searchFilterPoPojo.getSupplier().toUpperCase() + "%");
				LOG.info("getSuppliername" + searchFilterPoPojo.getSupplier().toUpperCase());
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPonumber()).length() > 0) {
				query.setParameter("ponumber", "%" + searchFilterPoPojo.getPonumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPocreatedby()).length() > 0) {
				query.setParameter("pocreatedby", "%" + searchFilterPoPojo.getPocreatedby().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessunit", "%" + searchFilterPoPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				query.setParameter("currency", searchFilterPoPojo.getCurrency());
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPoorderedby()).length() > 0) {
				query.setParameter("orderedby", "%" + searchFilterPoPojo.getPoorderedby().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPrnumber()).length() > 0) {
				query.setParameter("prnumber", "%" + searchFilterPoPojo.getPrnumber().toUpperCase() + "%");
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				LOG.info("getPostatus" + searchFilterPoPojo.getPostatus());
				query.setParameter("postatus", searchFilterPoPojo.getPostatus());
			} else {
				query.setParameter("postatus", Arrays.asList(PoStatus.DRAFT,PoStatus.SUSPENDED,PoStatus.CLOSED,PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE, PoStatus.PENDING));
			}
		}
		LOG.info("STATUS EXTRACTED WHEN EXPORT REPORT PO LIST >>>>>    "+status);
/*		if(null!=status)
			query.setParameter("status",PoStatus.fromString(status));
		else
			query.setParameter("status", Arrays.asList(PoStatus.DRAFT,PoStatus.SUSPENDED,PoStatus.CLOSED,PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE, PoStatus.PENDING));*/

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		if (select_all==true) {
			if (null!=status) {
				if (status.equals("PENDING")) {
					query.setParameter("cancelled", Boolean.FALSE);
					query.setParameter("revised", Boolean.FALSE);
				}
				if (status.equals("ONCANCELLATION")) {
					query.setParameter("cancelled", Boolean.TRUE);
				} else if (status.equals("REVISION")) {
					query.setParameter("revised", Boolean.TRUE);
				} else {
					query.setParameter("status", PoStatus.fromString(status));
				}

			}
			else{
				query.setParameter("status", Arrays.asList(PoStatus.DRAFT,PoStatus.SUSPENDED,PoStatus.CLOSED,PoStatus.READY, PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE, PoStatus.PENDING));

			}
		}
		else{
			if (status.equals("REVISION")) {
				query.setParameter("revised", Boolean.TRUE);
			} else {
				query.setParameter("status", PoStatus.fromString(status));
			}

		}


		LOG.info("HQL excel P2 function issue..........." + hql);
		query.setFirstResult((pAGE_SIZE * pageNo));
		query.setMaxResults(pAGE_SIZE);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoSupplierPojo> findPoSummaryForSupplierCsvReport(String tenantId, int pageSize, int pageNo, String[] poIds, SearchFilterPoPojo searchFilterPoPojo, boolean select_all, Date startDate, Date endDate) {
		String hql = "";
		hql += "select distinct new com.privasia.procurehere.core.pojo.PoSupplierPojo(p.id, p.name, p.modifiedDate, p.grandTotal, p.createdDate,p.decimal, p.referenceNumber ,p.description , p.poNumber, p.status, bu.unitName, c.currencyCode,b.companyName,p.actionDate, r) ";
		hql += " from Po p ";

		hql += " left outer join p.supplier as fs";

		hql += " left outer join p.businessUnit as bu";

		hql += " left outer join p.currency as c";

		hql += " left outer join p.buyer b";

		hql += " left outer join PoFinanceRequest r on r.po.id = p.id and r.buyer.id = b.id ";

		hql += " where fs.supplier.id = :tenantId";

		if (!(select_all)) {
			if (poIds != null && poIds.length > 0) {
				hql += " and p.id in (:poArr)";
			}
		}
		if (searchFilterPoPojo != null) {
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				hql += " and upper(p.name) like :name";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBuyer()).length() > 0) {
				hql += " and upper(b.companyName) like :buyerCompanyName";
				LOG.info("buyerCompanyName" + searchFilterPoPojo.getBuyer());
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPoNumber()).length() > 0) {
				hql += " and upper(p.poNumber) like :poNumber";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				hql += " and upper(c.currencyCode) like :currency";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBusinessUnit()).length() > 0) {
				hql += " and upper(bu.unitName) like :businessunit";
			}
			if (StringUtils.checkString(searchFilterPoPojo.getDescription()).length() > 0) {
				hql += " and upper(p.description) like :description";
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				hql += " and upper(p.status) like :postatus";

			} else {
				hql += " and upper(p.status) in :postatus)";
			}
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and p.createdDate between :startDate and :endDate ";
		}

		hql += " order by p.createdDate desc";

		final Query query = getEntityManager().createQuery(hql);

		if (!(select_all)) {
			if (poIds != null && poIds.length > 0) {
				query.setParameter("poArr", Arrays.asList(poIds));
			}
		}

		if (searchFilterPoPojo != null) {
			if (StringUtils.checkString(searchFilterPoPojo.getPoname()).length() > 0) {
				query.setParameter("name", "%" + searchFilterPoPojo.getPoname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getBuyer()).length() > 0) {
				query.setParameter("buyerCompanyName", "%" + searchFilterPoPojo.getBuyer().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getPoNumber()).length() > 0) {
				query.setParameter("poNumber", "%" + searchFilterPoPojo.getPoNumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getCurrency()).length() > 0) {
				query.setParameter("currency", "%" + searchFilterPoPojo.getCurrency().toUpperCase() + "%");
			}

			if (StringUtils.checkString(searchFilterPoPojo.getBusinessUnit()).length() > 0) {
				query.setParameter("businessunit", "%" + searchFilterPoPojo.getBusinessUnit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterPoPojo.getDescription()).length() > 0) {
				query.setParameter("description", "%" + searchFilterPoPojo.getDescription().toUpperCase() + "%");
			}
			if (searchFilterPoPojo.getPostatus() != null) {
				LOG.info("getPostatus" + searchFilterPoPojo.getPostatus());
				query.setParameter("postatus", searchFilterPoPojo.getPostatus());
			} else {
				query.setParameter("postatus", Arrays.asList(PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE));
			}

		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);
		// query.setParameter("status", Arrays.asList(PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED,
		// PoStatus.CANCELLED));
		return query.getResultList();

	}

	@Override
	public long findTotalPoSupplierSummaryCountForCsv(String tenantId, Date startDate, Date endDate) {
		LOG.info("Tenant in DaoImpl for count in supplier........." + tenantId);
		String hql = "select count(p) from Po p";
		hql += " left outer join p.supplier as fs";
		hql += " left outer join p.businessUnit as bu";
		hql += " left outer join p.currency as c";
		hql += " left outer join p.buyer b";
		hql += " where fs.supplier.id = :tenantId";

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public Po findPoForApprovalById(String poId) {
		StringBuilder hsql = new StringBuilder("from Po p left outer join fetch p.approvals ap join fetch p.createdBy cb left outer join fetch p.modifiedBy mb where p.id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", poId);
		Po po = (Po) query.getSingleResult();
		return po;
	}

	@Override
	public String getBusineessUnitnameByPoId(String poId) {
		StringBuilder hsql = new StringBuilder("select bu.displayName from Po p left outer join p.businessUnit as bu where p.id =:poId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		return (String) query.getSingleResult();
	}

	@Override
	public EventPermissions getUserPemissionsForPo(String userId, String poId) {
		LOG.debug("userId :" + userId + " poId: " + poId);
		EventPermissions permissions = new EventPermissions();

		User loggedInUser = userDao.findById(userId);
		LOG.info("Login id********:" + loggedInUser.getLoginId());
		LOG.info("Login type********:" + loggedInUser.getUserType());
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}


		// Event Creator
		Po po = findById(poId);
		if(po != null) {
			//set po creator as owner
			if(po.getCreatedBy().equals(SecurityLibrary.getLoggedInUser().getId())){
				permissions.setOwner(true);
			}
			// Po Team Member
			if(!po.getFromIntegration()) {
				Pr pr = po.getPr();
				if (po.getCreatedBy().getId().equals(userId)) {
					permissions.setOwner(true);
				}

				List<PoTeamMember> teamMembers = po.getPoTeamMembers();
				if (teamMembers != null) { // Check if the list is not null
					for (PoTeamMember member : teamMembers) {
						if (member != null && member.getUser() != null && member.getUser().getId() != null) { // Check for null values
							if (member.getUser().getId().equals(userId)) {
								if (member.getTeamMemberType() == TeamMemberType.Viewer) {
									permissions.setViewer(true);
								} else if (member.getTeamMemberType() == TeamMemberType.Editor) {
									permissions.setEditor(true);
									permissions.setViewer(false);
								} else if (member.getTeamMemberType() == TeamMemberType.Associate_Owner) {
									permissions.setEditor(false);
									permissions.setViewer(false);
									permissions.setOwner(true);
									break; // Exit the loop if the user is an owner
								}
							}
						}
					}
				}


				// Approver
				List<PoApproval> approvals = po.getApprovals();
				if (approvals != null) { // Check if the list is not null
					for (PoApproval approval : approvals) {
						if (approval != null && CollectionUtil.isNotEmpty(approval.getApprovalUsers())) { // Check if approval is not null and users list is not empty
							List<PoApprovalUser > users = approval.getApprovalUsers();
							for (PoApprovalUser  user : users) {
								if (user != null && user.getUser () != null && user.getUser ().getId() != null) { // Check for null values
									if (user.getUser ().getId().equals(userId)) {
										permissions.setApprover(true);
										if (approval.isActive() && ApprovalStatus.PENDING == user.getApprovalStatus()) {
											permissions.setActiveApproval(true);
											break; // Exit the inner loop if active approval is found
										}
									}
								}
							}
						}
					}
				}
			}else{
				permissions.setOwner(true);
			}
		}
		LOG.debug("permissions : " + permissions.toLogString());
		return permissions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Po> findAllPendingPo(String userId, String tenantId, TableDataInput input) {
		final Query query = constructPoForTenantQuery(userId, tenantId, input, false, PoStatus.PENDING);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredCancelPo(String tenantId, String userId, TableDataInput input){
		final Query query = constructPoForTenantQuery(userId, tenantId, input, true, PoStatus.CANCELLED);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public List<Po> findAllCancelPo(String tenantId, String userId, TableDataInput input){
		final Query query = constructPoForTenantQuery(userId, tenantId, input, false, PoStatus.CANCELLED);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalCancelPo(String tenantId, String userId){
		StringBuilder totalCancelPo = new StringBuilder("select count(distinct p) from Po p left outer join p.approvals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au where p.buyer.id = :tenantId and p.status = :status and p.erpPrTransferred= false");
		// if not admin
		if (userId != null) {
			totalCancelPo.append(" and (p.createdBy.id = :userId or au.id = :userId)");
		}

		Query query = getEntityManager().createQuery(totalCancelPo.toString());
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("status", PoStatus.CANCELLED);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Po> findPoByParams(String userId, String tenantId, TableDataInput input, Po param) {
		final Query query = queryForFindPoByParams(userId, tenantId, input, false, param);
		setQueryParameter(query, userId, tenantId, input, param);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long countPoByParams(String userId, String tenantId, TableDataInput input, Po param) {
		final Query query = queryForFindPoByParams(userId, tenantId, input, true, param);
		setQueryParameter(query, userId, tenantId, input, param);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query queryForFindPoByParams(String userId, String tenantId, TableDataInput input, boolean isCount, Po param) {
		StringBuilder hql = new StringBuilder();
		StringBuilder tableQuery = tableQuery();
		StringBuilder whereQuery = whereQuery(userId, tenantId, input, param);
		StringBuilder orderQuery = orderQuery(input);

		if(isCount) {
			hql.append("select count(distinct p) ")
					.append(tableQuery)
					.append(whereQuery);
		} else {
			hql.append("select distinct new Po(p.id, p.poNumber, p.name,cb, p.createdDate, c.currencyCode, p.grandTotal, p.decimal, bu.unitName,pr) ")
					.append(tableQuery)
					.append(whereQuery)
					.append(orderQuery);
		}
		LOG.info("queryForFindPoByParams for userID {}, tenantId {}, status {}, isRevised {}, isCancelled {}, isCount {}",
				userId, tenantId, param.getStatus(), param.getRevised(), param.getCancelled(), isCount);
		return getEntityManager().createQuery(hql.toString());
	}

	private void setQueryParameter(
			Query query, String userId, String tenantId,
			TableDataInput input, Po param) {
		if (userId != null) {
			query.setParameter("userId", userId);
			query.setParameter("approvalActive", true);
			query.setParameter("approvalDone", false);
		}
		if (tenantId != null) {
			query.setParameter("tenantId", tenantId);
		}
		for (ColumnParameter cp : input.getColumns()) {
			if (isFilterApply(cp)) {
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						List<PoStatus> statuses = Arrays.asList(PoStatus.PENDING);
						query.setParameter("status", statuses);
					} else {
						query.setParameter("status", PoStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		if (param != null) {
			if(param.getStatus() != null) {
				query.setParameter("status", param.getStatus());
			}
		}
		query.setParameter("erpPrTransferred", Boolean.FALSE);
	}

	private boolean isFilterApply(ColumnParameter cp) {
		return Boolean.TRUE == cp.getSearchable()
				&& cp.getSearch() != null
				&& StringUtils.checkString(cp.getSearch().getValue()).length() > 0;
	}

	private StringBuilder tableQuery() {
		return new StringBuilder(" from Po p " +
				"left outer join p.pr as pr " +
				"join p.createdBy as cb " +
				"left outer join p.currency as c " +
				"left outer join p.businessUnit as bu " +
				"left outer join p.approvals as pa " +
				"left outer join pa.approvalUsers as pau " +
				"left outer join pau.user as au ");
	}

	private StringBuilder whereQuery(String userId, String tenantId, TableDataInput input, Po param) {
		StringBuilder whereQuery = new StringBuilder();
		whereQuery.append(" where p.erpPrTransferred = :erpPrTransferred ");
		whereQuery.append(tenantId == null ? "" : "and p.buyer.id = :tenantId ");
		whereQuery.append(userId == null ? "" : "and (p.createdBy.id = :userId or au.id = :userId or (au.id =:userId and pa.active =:approvalActive and pa.done =:approvalDone)) ");
		if (param != null) {
			if(param.getStatus() != null) {
				whereQuery.append(" and p.status = :status ");
			}
			if(param.getRevised()) {
				whereQuery.append(" and p.revised = true ");
			}
			if(param.getCancelled()) {
				whereQuery.append(" and p.cancelled = true ");
			}
		}
		for (ColumnParameter cp : input.getColumns()) {
			if (isFilterApply(cp)) {
				switch (cp.getData()) {
					case "name":
						whereQuery.append(" and upper(p.name) like upper(:name)");
						break;
					case "poNumber":
						whereQuery.append(" and upper(p.poNumber) like upper(:poNumber) ");
						break;
					case "createdBy.name":
						whereQuery.append(" and upper(cb.name) like (:createdByname)");
						break;
					case "currency.currencyCode":
						whereQuery.append(" and upper(c.currencyCode) like (:currencycurrencyCode)");
						break;
					case "businessUnit.unitName":
						whereQuery.append(" and upper(bu.unitName) like (:businessUnit.unitName)");
						break;
					default:
						String paramName = cp.getData().replace(".", "");
						whereQuery.append(" and p.").append(paramName)
								.append(" like (:").append(paramName)
								.append(") ");
				}
			}
		}
		return whereQuery;
	}

	private StringBuilder orderQuery(TableDataInput input) {
		StringBuilder orderQuery = new StringBuilder();
		if (CollectionUtil.isNotEmpty(input.getOrder())) {
			orderQuery.append(" order by ");
			for (Iterator<OrderParameter> iterator = input.getOrder().iterator(); iterator.hasNext(); ) {
				OrderParameter order = iterator.next();
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				switch (orderColumn) {
					case "createdBy.name":
						orderQuery.append(" cb.name ")
								.append(dir);
						break;
					case "currency.currencyCode":
						orderQuery.append(" c.currencyCode ")
								.append(dir);
						break;
					case "businessUnit.unitName":
						orderQuery.append(" bu.unitName ")
								.append(dir);
						break;
					default:
						orderQuery.append(" p.").append(orderColumn)
								.append(" ").append(dir);
				}
				if (iterator.hasNext()) {
					orderQuery.append(",");
				}
			}
		} else {
			orderQuery.append(" order by p.poCreatedDate desc ");
		}
		return orderQuery;
	}

	/**
	 * @param userId
	 * @param tenantId
	 * @param tableParams
	 * @param isCount
	 * @param status
	 * @param
	 * @return
	 */
	private Query constructPoForTenantQuery(String userId, String tenantId, TableDataInput tableParams, boolean isCount, PoStatus status) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct p) ";
		} else {
			hql += "select distinct new Po(p.id, p.poNumber, p.name,cb, p.createdDate, c.currencyCode, p.grandTotal, p.decimal, bu.unitName,pr) ";
		}

		hql += " from Po p left outer join  p.pr as pr";

		hql += " join p.createdBy as cb left outer join p.currency as c left outer join p.businessUnit as bu ";

		hql += " left outer join p.approvals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au ";

		hql += " where p.buyer.id = :tenantId and p.erpPrTransferred = :erpPrTransferred ";

		if (userId != null) {
			hql += " and (p.createdBy.id = :userId or au.id = :userId) ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("name")) {
					hql += " and p.name like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("poNumber")) {
					hql += " and p.poNumber like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("createdBy.name")) {
					hql += " and upper(cb.name) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("currency.currencyCode")) {
					hql += " and upper(c.currencyCode) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("businessUnit.unitName")) {
					hql += " and upper(bu.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and p." + cp.getData().replace(".", "") + " like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		hql += " and p.status = :status ";

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();

					if (orderColumn.equalsIgnoreCase("createdBy.name")) {
						hql += " cb.name " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("currency.currencyCode")) {
						hql += " c.currencyCode " + dir;
					} else if (orderColumn.equalsIgnoreCase("businessUnit.unitName")) {
						hql += " bu.unitName " + dir + ",";
					} else {
						hql += " p." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by p.poCreatedDate desc ";
			}
		}

		if(PoStatus.CANCELLED.equals("CANCELLED")){
			LOG.info("*********PO Query STATUS CANCEL*********** : " + hql);
		}


		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		if (userId != null) {
			query.setParameter("userId", userId);
		}
		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						List<PoStatus> statuses = Arrays.asList(PoStatus.PENDING);
						query.setParameter("status", statuses);
					} else {
						query.setParameter("status", PoStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		query.setParameter("status", status);

		query.setParameter("erpPrTransferred", Boolean.FALSE);

		return query;
	}

	@Override
	public long findTotalPendingPo(String tenantId, String userId) {
		StringBuilder hsql = new StringBuilder("select count(distinct p) from Po p left outer join p.approvals as pa left outer join pa.approvalUsers as pau left outer join pau.user as au where p.buyer.id = :tenantId and p.status = :status and p.erpPrTransferred= false");
		// if not admin
		if (userId != null) {
			hsql.append(" and (p.createdBy.id = :userId or au.id = :userId)");
		}

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("status", PoStatus.PENDING);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFilteredPendingPo(String tenantId, String userId, TableDataInput input) {
		final Query query = constructPoForTenantQuery(userId, tenantId, input, true, PoStatus.PENDING);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public void updatePoRevisedSnapshot(String poId, String revisePoDetails) {
		String hql = "update Po p set p.revisePoDetails = :revisePoDetails where p.id  = :poId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("revisePoDetails", revisePoDetails);
		query.setParameter("poId", poId);
		int updateRevisedPoDetails = query.executeUpdate();
		LOG.info("updateSentPoReport :" + updateRevisedPoDetails);
	}

	@Override
	public void updateTransferOwnerForPo(String fromUserId, String toUserId) {

		User sourceUser = getEntityManager().find(User.class, fromUserId);
		User targetUser = getEntityManager().find(User.class, toUserId);

		Query query = getEntityManager().createNativeQuery("UPDATE PROC_PO SET CREATED_BY = :toUserId where CREATED_BY =:fromUserId");
		query.setParameter("toUserId", toUserId);
		query.setParameter("fromUserId", fromUserId);
		int recordsUpdated = query.executeUpdate();
		LOG.info("Creators transferred: {}", recordsUpdated);

		//Note: PO team members are from the PR. That will be managed in the PrDao for ownership transfer

		//transfer ownership of approvals only for RFS with status of DRAFT and PENDING
		Query query5 = getEntityManager().createQuery(
				"UPDATE PoApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query5.setParameter("sourceUser", sourceUser);
		query5.setParameter("targetUser", targetUser);
		recordsUpdated = query5.executeUpdate();
		LOG.info("Approval user transferred: {}", recordsUpdated);
		//4105 P2 functional issue 46
		Query query6 = getEntityManager().createQuery(
				"UPDATE PoTeamMember sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query6.setParameter("sourceUser", sourceUser);
		query6.setParameter("targetUser", targetUser);
		recordsUpdated = query6.executeUpdate();
		LOG.info("PO TEAM MEMBERS transferred: {}", recordsUpdated);
	}

	@Override
	public List<EventTeamMember> getPlainTeamMembersForPo(String poId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.EventTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId, u.deleted)from PoTeamMember tm left outer join tm.user u where tm.po.id =:poId");
		query.setParameter("poId", poId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@Override
	public PoTeamMember getPoTeamMemberByUserIdAndPoId(String poId, String userId) {
		StringBuilder hsql = new StringBuilder("from PoTeamMember tm where tm.po.id =:poId and tm.user.id =:userId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("poId", poId);
		query.setParameter("userId", userId);
		List<PoTeamMember> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<PoTeamMember> findAssociateOwnerOfPo(String id, TeamMemberType associateOwner) {
		String hql = " from PoTeamMember ptm where ptm.po.id = :poId and ptm.teamMemberType =:associateOwner";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("poId", id);
		query.setParameter("associateOwner", associateOwner);

		List<PoTeamMember> poTeamList = query.getResultList();
		return poTeamList;
	}


	@Override
	public List<PoTeamMember> getPlainTeamMembersForPoSummary(String poId) {
		final Query query = getEntityManager().createQuery("select distinct tm from PoTeamMember tm left outer join fetch tm.user u left outer join fetch u.buyer where tm.po.id =:poId");
		query.setParameter("poId", poId);
		List<PoTeamMember> list = query.getResultList();
		return list;
	}

}
