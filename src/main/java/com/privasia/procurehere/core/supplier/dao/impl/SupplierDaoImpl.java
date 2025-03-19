/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.privasia.procurehere.core.enums.*;
import com.privasia.procurehere.core.pojo.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.RequestedAssociatedBuyer;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
@Repository()
public class SupplierDaoImpl extends GenericDaoImpl<Supplier, String> implements SupplierDao {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> findAllactiveSuppliers() {
		final Query query = getEntityManager().createQuery("from Supplier a  join fetch  a.registrationOfCountry as rc left outer join fetch a.companyStatus cs left outer join fetch a.state st  order by a.companyName asc");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> findPendingSuppliers() {
		final Query query = getEntityManager().createQuery("from Supplier a  join fetch  a.registrationOfCountry as rc left outer join fetch a.companyStatus cs left outer join fetch  a.state st  where a.status =:status order by a.registrationDate desc");
		query.setParameter("status", SupplierStatus.PENDING);
		return query.getResultList();
	}

	@Override
	public boolean isExists(Supplier supplier) {
		final Query query = getEntityManager().createQuery("from Supplier a where (a.companyRegistrationNumber = :companyRegistrationNumber and a.registrationOfCountry = :registrationOfCountry) or (upper(a.companyName) = :companyName and a.registrationOfCountry = :registrationOfCountry)");
		query.setParameter("companyRegistrationNumber", StringUtils.checkString(supplier.getCompanyRegistrationNumber()));
		query.setParameter("registrationOfCountry", supplier.getRegistrationOfCountry());
		query.setParameter("companyName", StringUtils.checkString(supplier.getCompanyName()).toUpperCase());
		query.setParameter("registrationOfCountry", supplier.getRegistrationOfCountry());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public boolean isExistsLoginEmail(String loginEmail) {
		final Query query = getEntityManager().createQuery("from Supplier a where  upper(a.loginEmail) = :loginEmail and a.status <> :status");
		query.setParameter("loginEmail", StringUtils.checkString(loginEmail).toUpperCase());
		query.setParameter("status", SupplierStatus.REJECTED);
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public Supplier findById(String id) {
		final Query query = getEntityManager().createQuery("from Supplier a left outer join fetch a.registrationOfCountry as rc left outer join fetch a.companyStatus cs left outer join fetch  a.state st where a.id =:id");
		query.setParameter("id", id);
		return (Supplier) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> searchSupplier(String status, String order, String globalSearch) {
		StringBuilder hql = new StringBuilder(" from Supplier a  join fetch  a.registrationOfCountry rc  where 1 =1 ");
		if (StringUtils.checkString(status).length() > 0 && SupplierStatus.valueOf(StringUtils.checkString(status).toUpperCase()) != SupplierStatus.ALL) {
			hql.append("and a.status =:status ");
		}
		if (StringUtils.checkString(globalSearch).length() > 0) {
			hql.append("and (upper(a.companyName) like :companyName or upper(a.fullName) like :fullName or upper(a.supplierTrackDesc) like :supplierTrackDesc)");
		}

		if (StringUtils.checkString(order).length() > 0 && (StringUtils.checkString(order).equals("Newest"))) {
			hql.append("order by a.registrationDate desc");
		} else {
			hql.append("order by a.registrationDate asc");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(status).length() > 0 && SupplierStatus.valueOf(StringUtils.checkString(status).toUpperCase()) != SupplierStatus.ALL) {
			query.setParameter("status", SupplierStatus.valueOf(StringUtils.checkString(status).toUpperCase()));
		}
		if (StringUtils.checkString(globalSearch).length() > 0) {
			query.setParameter("companyName", "%" + globalSearch.toUpperCase() + "%");
			query.setParameter("fullName", "%" + globalSearch.toUpperCase() + "%");
			query.setParameter("supplierTrackDesc", "%" + globalSearch.toUpperCase() + "%");

		}
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Supplier update(Supplier e) {
		return getEntityManager().merge(e);
	}

	@Override
	public Supplier findSupplierSubscriptionDetailsBySupplierId(String id) {
		final Query query = getEntityManager().createQuery("from Supplier a left outer join fetch a.associatedBuyers as b left outer join fetch a.registrationOfCountry as rc left outer join fetch a.supplierSubscription s left outer join fetch s.supplierPlan p left outer join fetch a.supplierPackage as sp where a.id =:id");
		query.setParameter("id", id);
		return (Supplier) query.getSingleResult();
	}

	@Override
	@Transactional
	public Supplier findSupplierSubscriptionDetailsBySupplierIdExcludedExpiredBuyers(String id) {
		// final Query query = getEntityManager().createQuery("from Supplier s left outer join fetch s.associatedBuyers
		// as ab left outer join fetch s.registrationOfCountry as rc left outer join fetch s.supplierPackage as pkg left
		// outer join fetch s.supplierSubscription as ss left outer join fetch ss.supplierPlan as sp where s.id =:id and
		// ab.currentSubscription.subscriptionStatus != 'EXPIRED'");
		// PH-1848 - Changed by Abhinav/Nitin on 13-Jan-21
		final Query query = getEntityManager().createQuery("from Supplier s left outer join fetch s.associatedBuyers as ab left outer join fetch s.registrationOfCountry as rc left outer join fetch s.supplierPackage as pkg left outer join fetch s.supplierSubscription as ss left outer join fetch ss.supplierPlan as sp where s.id =:id");
		query.setParameter("id", id);
		try {
			Supplier supplier = (Supplier) query.getSingleResult();
			return supplier;
		} catch (NoResultException nre) {
			return null;
		}
	}

	@Override
	public Supplier findSupplierByIdForAssignedCountries(String id) {
		final Query query = getEntityManager().createQuery("from Supplier a left outer join fetch a.countries as rc  where a.id =:id");
		query.setParameter("id", id);
		return (Supplier) query.getSingleResult();
	}

	@Override
	public Supplier findSupplierByIdForAssignedStates(String id) {
		final Query query = getEntityManager().createQuery("from Supplier a left outer join fetch a.states as rc  where a.id =:id");
		query.setParameter("id", id);
		return (Supplier) query.getSingleResult();
	}

	@Override
	public Supplier findSupplierForProjectTrackById(String id) {
		final Query query = getEntityManager().createQuery("from Supplier a left outer join fetch a.supplierProjects as sp  where a.id =:id");
		query.setParameter("id", id);
		return (Supplier) query.getSingleResult();
	}

	@Override
	public boolean isExistsRegistrationNumber(Supplier supplier) {
		final Query query = getEntityManager().createQuery("from Supplier a where a.companyRegistrationNumber = :companyRegistrationNumber and a.registrationOfCountry = :registrationOfCountry ");
		query.setParameter("companyRegistrationNumber", supplier.getCompanyRegistrationNumber());
		query.setParameter("registrationOfCountry", supplier.getRegistrationOfCountry());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public boolean isExistsCompanyName(Supplier supplier) {
		final Query query = getEntityManager().createQuery("from Supplier a where upper(a.companyName) = :companyName and a.registrationOfCountry = :registrationOfCountry ");
		query.setParameter("companyName", supplier.getCompanyName());
		query.setParameter("registrationOfCountry", supplier.getRegistrationOfCountry());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public void updateSupplierCommunicationEmail(String supplierId, String oldCommunicationEmail, String newCommunicationEmail) {
		Query query = getEntityManager().createQuery("update Supplier a set a.communicationEmail = :newCommunicationEmail where a.id = :supplierId");
		query.setParameter("newCommunicationEmail", newCommunicationEmail);
		query.setParameter("supplierId", supplierId);
		query.executeUpdate();
		// Update its primary user as well
		query = getEntityManager().createQuery("update User a set a.communicationEmail = :newCommunicationEmail where a.communicationEmail = :oldCommunicationEmail and a.tenantId = :supplierId");
		query.setParameter("newCommunicationEmail", newCommunicationEmail);
		query.setParameter("oldCommunicationEmail", oldCommunicationEmail);
		query.setParameter("supplierId", supplierId);
		query.executeUpdate();
	}

	@Override
	public Supplier findSuppById(String id) {
		final Query query = getEntityManager().createQuery("from Supplier a left outer join fetch a.registrationOfCountry as rc left outer join fetch a.companyStatus cs left outer join fetch  a.state st left outer join fetch a.associatedBuyers as b where a.id =:id");
		query.setParameter("id", id);
		return (Supplier) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> findSuppliersOfNaicsCode(String ncid) {
		final Query query = getEntityManager().createQuery("from Supplier a  join fetch  a.registrationOfCountry as rc left outer join fetch a.companyStatus cs left outer join fetch  a.state st left outer join fetch a.naicsCodes nc where nc.id =:ncid");
		query.setParameter("ncid", ncid);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPojo> findListOfSupplierForDateRange(Date start, Date end, Country country) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.SupplierPojo(s.companyName, rc.countryName, s.registrationCompleteDate,s.companyRegistrationNumber) from Supplier s left outer join s.registrationOfCountry as rc where s.registrationCompleteDate between :start and :end ");
		if (country != null) {
			hsql.append(" and rc = :country");
		}
		hsql.append(" order by s.registrationCompleteDate");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("start", start, TemporalType.TIMESTAMP);
		query.setParameter("end", end, TemporalType.TIMESTAMP);
		if (country != null) {
			query.setParameter("country", country);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getAllSupplierFromGlobalSearch(String searchVal) {
		final Query query = getEntityManager().createQuery("select distinct s from Supplier s left outer join fetch s.registrationOfCountry rc where (upper(s.companyName) like :searchVal) or (upper(s.companyRegistrationNumber) like :searchVal)");
		query.setParameter("searchVal", "%" + searchVal.toUpperCase() + "%");
		List<Supplier> list = query.getResultList();
		LOG.info("  Supplier  " + list.size());
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getAllSupplierFromIds(List<String> supplierIds) {
		final Query query = getEntityManager().createQuery("select  s from Supplier s  where s.id in (:supplierIds)");
		query.setParameter("supplierIds", supplierIds);
		List<Supplier> list = query.getResultList();
		return list;
	}

	@Override
	public Supplier findSupplierAndAssocitedBuyersById(String suppId) {
		final Query query = getEntityManager().createQuery("select distinct s from Supplier s left outer join fetch s.associatedBuyers ab where s.id = :id");
		query.setParameter("id", suppId);
		return (Supplier) query.getSingleResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Supplier> findSuppliersForSubscriptionExpireOrExtend() {
		final Query query = getEntityManager().createQuery("select distinct s from Supplier s left outer join fetch s.supplierSubscription ss where s.status = :status and s.supplierPackage is not null and s.supplierSubscription is not null and ss.endDate is not null and ss.endDate < :now and ss.subscriptionStatus not in (:subscriptionStatus)");
		query.setParameter("status", SupplierStatus.APPROVED);
		query.setParameter("now", new Date());
		List<SubscriptionStatus> list = new ArrayList<SubscriptionStatus>();
		list.add(SubscriptionStatus.EXPIRED);
		list.add(SubscriptionStatus.CANCELLED);
		query.setParameter("subscriptionStatus", list);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> findSuppliersForExpiryNotificationReminder(Date remindDate) {
		final Query query = getEntityManager().createQuery("select distinct s from Supplier s left outer join fetch s.supplierSubscription ss where s.status = :status and s.supplierPackage is not null and s.supplierSubscription is not null and ss.endDate is not null and ss.endDate <= :remindDate and ss.subscriptionStatus = :subscriptionStatus and (ss.reminderSent is null or ss.reminderSent = :reminderSent)");
		query.setParameter("status", SupplierStatus.APPROVED);
		query.setParameter("remindDate", remindDate);
		query.setParameter("reminderSent", Boolean.FALSE);
		query.setParameter("subscriptionStatus", SubscriptionStatus.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoSupplierPojo> findAllSearchFilterPoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate,String status) {
		final Query query = constructPrSearchFilterForTenantQueryForSupplier(tenantId, input, false, startDate, endDate,status);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSearchFilterPoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate,String status) {
		final Query query = constructPrSearchFilterForTenantQueryForSupplier(tenantId, input, true, startDate, endDate,status);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalPoForSupplier(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct p) from Po p left outer join p.supplier as fs where fs.supplier.id = :supplierId and p.status in (:status) and p.orderedDate is not null ");

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", tenantId);

		query.setParameter("status", Arrays.asList(PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED));
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructPrSearchFilterForTenantQueryForSupplier(String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate,String status) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct p) ";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.pojo.PoSupplierPojo(p.id, p.name, p.modifiedDate, p.grandTotal, p.createdDate,p.decimal, p.referenceNumber ,p.description , p.poNumber, p.status, bu.unitName, c.currencyCode, b.companyName, p.actionDate, r, p.revised) ";
		}

		hql += " from Po p ";

		hql += " left outer join p.supplier as fs";

		hql += " left outer join p.businessUnit as bu";

		hql += " left outer join p.currency as c";

		hql += " left outer join p.buyer b";

		hql += " left outer join PoFinanceRequest r on r.po.id = p.id and r.buyer.id = p.buyer.id ";

		// hql += " where fs.supplier.id = :tenantId and p.orderedDate is not null ";
		hql += " where fs.supplier.id = :tenantId  ";

		Boolean selectStatusFilter = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					selectStatusFilter = true;
					//hql += " and (p.status in (:status) and p.oldStatus <> 'READY' )";
					hql += " and (p.status in (:status) )";
				} else if (cp.getData().equals("businessUnit")) {
					hql += " and upper(p.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("buyerCompanyName")) {
					hql += " and upper(p.buyer.companyName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("currency")) {
					hql += " and upper(p.currency.currencyCode) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}

		LOG.info("selectStatusFilter : "+selectStatusFilter);
		LOG.info("status : "+status);

		if (!status.isEmpty() & !selectStatusFilter) {
			selectStatusFilter=true;
			hql += " and (p.status in (:status) ";
			if(status.contains(","))
				hql+=" or (p.status ='"+PoStatus.READY+"' and p.oldStatus is not null))";
			else
				hql+=")";

		}

		if (!selectStatusFilter) {
			hql += " and (p.status in(:status) or (p.status='"+PoStatus.READY+"' and p.oldStatus is not null) )";
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
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						query.setParameter("status", Arrays.asList(PoStatus.CLOSED,PoStatus.PENDING,PoStatus.SUSPENDED,PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED));
					} else {
						status="";
						query.setParameter("status", PoStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		LOG.info("selectStatusFilter : "+selectStatusFilter);
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		if (!status.isEmpty() ) {
			if(status.contains(","))
				query.setParameter("status", Arrays.asList(PoStatus.PENDING,PoStatus.SUSPENDED));
			else
				query.setParameter("status", PoStatus.fromString(status.toUpperCase()));
		}
		if (!selectStatusFilter) {
			query.setParameter("status", Arrays.asList(PoStatus.CLOSED,PoStatus.PENDING,PoStatus.SUSPENDED,PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED));
		}
		return query;
	}

	@Override
	public long findCountOfAllPOForSupplier(String tenantId, String userId) {
		StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p  left outer join p.supplier as fs where fs.supplier.id = :tenantId and p.status = :status ");
		// if not admin
		// if (userId != null) {
		// hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId)");
		// }
		LOG.info("HQL : " + hsql);

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		// if (userId != null) {
		// query.setParameter("userId", userId);
		// }
		query.setParameter("status", PrStatus.APPROVED);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public boolean isExistsRegistrationNumberWithId(Supplier supplier) {
		final Query query = getEntityManager().createQuery("from Supplier a where a.companyRegistrationNumber = :companyRegistrationNumber and a.registrationOfCountry = :registrationOfCountry and a.id <> :id");
		query.setParameter("companyRegistrationNumber", supplier.getCompanyRegistrationNumber());
		query.setParameter("registrationOfCountry", supplier.getRegistrationOfCountry());
		query.setParameter("id", supplier.getId());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public boolean isExistsCompanyNameWithId(Supplier supplier) {
		final Query query = getEntityManager().createQuery("from Supplier a where upper(a.companyName) = :companyName and a.registrationOfCountry = :registrationOfCountry and a.id <> :id");
		query.setParameter("companyName", supplier.getCompanyName());
		query.setParameter("registrationOfCountry", supplier.getRegistrationOfCountry());
		query.setParameter("id", supplier.getId());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public void updateSupplierCommunicationEmailForSupplierOnly(String supplierId, String oldCommunicationEmail, String newCommunicationEmail) {
		Query query = getEntityManager().createQuery("update Supplier a set a.communicationEmail = :newCommunicationEmail where a.id = :supplierId");
		query.setParameter("newCommunicationEmail", newCommunicationEmail);
		query.setParameter("supplierId", supplierId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FinancePo> findFinanceSuppliers(String tanentid) {

		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.FinancePo(s.id, s.companyName,s.loginEmail,s.communicationEmail,s.companyRegistrationNumber,s.fullName,s.mobileNumber,s.registrationDate) from FinancePo fp left outer join  fp.supplier s where  fp.financeCompany.id = :tanentid");
		LOG.info("----" + tanentid + "----" + query.toString());
		query.setParameter("tanentid", tanentid);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FinancePo> serchFinanceSuppliers(String status, String order, String globalSreach, String tanentid) {
		StringBuilder hql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.FinancePo(s.id, s.companyName,s.loginEmail,s.communicationEmail,s.companyRegistrationNumber,s.fullName,s.mobileNumber,s.registrationDate) from FinancePo fp left outer join  fp.supplier s where  fp.financeCompany.id = :tanentid");
		/*
		 * if (StringUtils.checkString(status).length() > 0 &&
		 * SupplierStatus.valueOf(StringUtils.checkString(status).toUpperCase()) != SupplierStatus.ALL) {
		 * hql.append("and a.status =:status "); }
		 */
		if (StringUtils.checkString(globalSreach).length() > 0) {
			hql.append(" and (upper(s.companyName) like :companyName or upper(s.fullName) like :fullName or upper(s.supplierTrackDesc) like :supplierTrackDesc)");
		}

		if (StringUtils.checkString(order).length() > 0 && (StringUtils.checkString(order).equals("Descending"))) {
			hql.append(" order by s.companyName desc");
		} else {
			hql.append(" order by s.companyName asc");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		/*
		 * if (StringUtils.checkString(status).length() > 0 &&
		 * SupplierStatus.valueOf(StringUtils.checkString(status).toUpperCase()) != SupplierStatus.ALL) {
		 * query.setParameter("status", SupplierStatus.valueOf(StringUtils.checkString(status).toUpperCase())); }
		 */
		if (StringUtils.checkString(globalSreach).length() > 0) {
			query.setParameter("companyName", "%" + globalSreach.toUpperCase() + "%");
			query.setParameter("fullName", "%" + globalSreach.toUpperCase() + "%");
			query.setParameter("supplierTrackDesc", "%" + globalSreach.toUpperCase() + "%");

		}
		query.setParameter("tanentid", tanentid);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pr> findAllSearchFilterPoForFinance(String tenantId, TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier) {
		final Query query = constructPrSearchFilterForTenantQueryForSupplier(tenantId, input, false, startDate, endDate, status, selectedSupplier);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSearchFilterPoForFinance(String tenantId, TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier) {
		final Query query = constructPrSearchFilterForTenantQueryForSupplier(tenantId, input, true, startDate, endDate, status, selectedSupplier);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalPoForFinance(String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.supplier as fs where p.status = :status ");

		Query query = getEntityManager().createQuery(hsql.toString());
		// query.setParameter("supplierId", tenantId);

		query.setParameter("status", PrStatus.APPROVED);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructPrSearchFilterForTenantQueryForSupplier(String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate, PrStatus status, String selectedSupplier) {

		String hql = "";
		LOG.info("cp.getData() :" + selectedSupplier);
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct p) ";
		} else {
			hql += "select distinct NEW com.privasia.procurehere.core.entity.Pr(p.id, p.name, p.modifiedDate, p.grandTotal, p.createdBy, p.modifiedBy, p.poCreatedDate, p.createdBy.name, p.modifiedBy.name, p.decimal, p.referenceNumber, p.description, p.poNumber, p.businessUnit.unitName, s.companyName, p.supplierName) ";
		}

		hql += " from Pr p ";
		hql += " left outer join p.supplier as fs ";
		hql += " left outer join fs.supplier s ";

		hql += " where 1=1 ";

		boolean isSelectOn = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and p.status in (:status) ";
					isSelectOn = true;
				} else if (cp.getData().equalsIgnoreCase("supplier.fullName")) {
					LOG.info("cp.getData() :" + cp.getData());
					hql += " and upper(s.companyName) like (:" + cp.getData().replace(".", "") + ") ";
				}

				else {
					hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		if (StringUtils.checkString(selectedSupplier).length() > 0) {
			hql += " and s.id = '" + selectedSupplier + "' ";
		}

		if (!isSelectOn)

		{
			hql += " and p.status in (:status) ";
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  p.poCreatedDate between :startDate and :endDate ";
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
			} else {
				// by default order by created date

				hql += " order by p.poCreatedDate desc ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		// query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " + cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						query.setParameter("status", PrStatus.APPROVED);
					} else {
						query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		if (!isSelectOn) {
			query.setParameter("status", status);
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return query;
	}

	@Override
	public long findCountOfAllPOForFinance(String tenantId, String userId) {
		StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p  left outer join p.supplier as fs where p.status = :status ");
		// if not admin
		// if (userId != null) {
		// hsql.append(" and (tmu.id = :userId or p.createdBy.id = :userId or au.id = :userId)");
		// }
		LOG.info("HQL : " + hsql);

		Query query = getEntityManager().createQuery(hsql.toString());
		// query.setParameter("tenantId", tenantId);
		// if (userId != null) {
		// query.setParameter("userId", userId);
		// }
		query.setParameter("status", PrStatus.APPROVED);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pr> findAllSearchFilterPoForOwner(TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier) {
		final Query query = constructPrSearchFilterForTenantQueryForOwner(input, false, startDate, endDate, status, selectedSupplier);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	private Query constructPrSearchFilterForTenantQueryForOwner(TableDataInput tableParams, boolean isCount, Date startDate, Date endDate, PrStatus status, String selectedSupplier) {

		String hql = "";
		LOG.info("cp.getData() :" + selectedSupplier);
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct p) ";
		} else {
			hql += "select distinct NEW com.privasia.procurehere.core.entity.Pr(p.id, p.name, p.modifiedDate, p.grandTotal, p.createdBy, p.modifiedBy, p.poCreatedDate, p.createdBy.name, p.modifiedBy.name, p.decimal, p.referenceNumber, p.description, p.poNumber, p.businessUnit.unitName, s.companyName, p.supplierName) ";
		}

		hql += " from Pr p ";
		hql += " left outer join p.supplier as fs ";
		hql += " left outer join fs.supplier s ";

		hql += " where 1=1 ";

		boolean isSelectOn = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and p.status in (:status) ";
					isSelectOn = true;
				} else if (cp.getData().equalsIgnoreCase("supplier.fullName")) {
					LOG.info("cp.getData() :" + cp.getData());
					hql += " and upper(s.companyName) like (:" + cp.getData().replace(".", "") + ") ";
				}

				else {
					hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		if (StringUtils.checkString(selectedSupplier).length() > 0) {
			hql += " and s.id = '" + selectedSupplier + "' ";
		}

		if (!isSelectOn)

		{
			hql += " and p.status in (:status) ";
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  p.poCreatedDate between :startDate and :endDate ";
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
			} else {
				// by default order by created date

				hql += " order by p.poCreatedDate desc ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		// query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT 1 :: " + cp.getData() + "VALUE 1 :: " + cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						query.setParameter("status", PrStatus.APPROVED);
					} else {
						query.setParameter("status", PrStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		if (!isSelectOn) {
			query.setParameter("status", status);
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return query;
	}

	@Override
	public long findTotalSearchFilterPoForOwner(TableDataInput input, Date startDate, Date endDate, PrStatus status, String selectedSupplier) {
		final Query query = constructPrSearchFilterForTenantQueryForOwner(input, true, startDate, endDate, status, selectedSupplier);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalPoForOwner() {
		StringBuilder hsql = new StringBuilder("select count(distinct p) from Pr p left outer join p.supplier as fs where p.status = :status ");

		Query query = getEntityManager().createQuery(hsql.toString());
		// query.setParameter("supplierId", tenantId);

		query.setParameter("status", PrStatus.APPROVED);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PendingEventPojo> findAllPOForSupplierMobile(String tenantId, SearchSortFilterPojo search) {

		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.PendingEventPojo( p.id, p.name, p.createdDate, p.referenceNumber, p.businessUnit, p.status) from Po p left outer join p.supplier as fs inner join p.buyer as buy where fs.supplier.id = :tenantId and p.status in ( :status ) ");
		if (StringUtils.checkString(search.getSearchValue()).length() > 0) {
			hsql.append(" and (upper(p.poNumber) like :search or  upper(p.name) like :search )");
		}
		if (StringUtils.checkString(search.getBuyerName()).length() > 0) {
			hsql.append(" and upper(buy.companyName) like :companyName");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED));
		if (StringUtils.checkString(search.getSearchValue()).length() > 0) {
			query.setParameter("search", "%" + search.getSearchValue().toUpperCase() + "%");
		}
		if (StringUtils.checkString(search.getBuyerName()).length() > 0) {
			query.setParameter("companyName", "%" + search.getBuyerName().toUpperCase() + "%");
		}
		if (search != null && search.getStart() != null && search.getLength() != null) {
			query.setFirstResult(search.getStart());
			query.setMaxResults(search.getLength());
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPojo> searchSupplierForPagination(String status, String order, String globalSearch, String pageNo) {
		StringBuilder hql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.SupplierPojo(a.id, a.companyName, rc.countryName, a.status, a.registrationDate, a.companyRegistrationNumber, a.fullName, a.mobileNumber, a.line1, a.line2) from Supplier a  join a.registrationOfCountry rc  where 1 =1 ");
		if (StringUtils.checkString(status).length() > 0 && SupplierStatus.valueOf(StringUtils.checkString(status).toUpperCase()) != SupplierStatus.ALL) {
			hql.append("and a.status =:status ");
		}
		if (StringUtils.checkString(globalSearch).length() > 0) {
			hql.append("and (upper(a.companyName) like :companyName or upper(a.fullName) like :fullName or upper(a.supplierTrackDesc) like :supplierTrackDesc)");
		}

		if (StringUtils.checkString(order).length() > 0 && (StringUtils.checkString(order).equals("Newest"))) {
			hql.append("order by a.registrationDate desc");
		} else {
			hql.append("order by a.registrationDate asc");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(status).length() > 0 && SupplierStatus.valueOf(StringUtils.checkString(status).toUpperCase()) != SupplierStatus.ALL) {
			query.setParameter("status", SupplierStatus.valueOf(StringUtils.checkString(status).toUpperCase()));
		}
		if (StringUtils.checkString(globalSearch).length() > 0) {
			query.setParameter("companyName", "%" + globalSearch.toUpperCase() + "%");
			query.setParameter("fullName", "%" + globalSearch.toUpperCase() + "%");
			query.setParameter("supplierTrackDesc", "%" + globalSearch.toUpperCase() + "%");

		}
		query.setFirstResult(Integer.parseInt(pageNo) * 10);
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long findTotalAssocitedBuyersById(String tenantId) {
		final Query query = getEntityManager().createQuery("select count(distinct b.id) from Supplier a left outer join a.associatedBuyers as b where a.id =:tenantId");
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long getTotalSupplierCount() {
		final Query query = getEntityManager().createQuery("select count(distinct s.id) from Supplier s");
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RequestedAssociatedBuyer findSupplierRequestByIds(String id, String buyerId) {
		final Query query = getEntityManager().createQuery("from RequestedAssociatedBuyer a left outer join fetch a.supplier sp left outer join fetch a.industryCategory ic left outer join fetch a.buyer b where sp.id=:id and b.id=:buyerId");
		query.setParameter("id", id);
		query.setParameter("buyerId", buyerId);
		List<RequestedAssociatedBuyer> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Supplier findPlainSupplierById(String id) {
		final Query query = getEntityManager().createQuery("from Supplier a where a.id =:id");
		query.setParameter("id", id);
		return (Supplier) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> findSuppliersForFutureSubscriptionActivation() {
		final Query query = getEntityManager().createQuery("select distinct s from Supplier s left outer join fetch s.supplierSubscription ss left outer join fetch s.supplierPackage sp where s.status = :status and s.supplierPackage is not null and s.supplierSubscription is not null and ss.endDate is not null and ss.supplierPlan is not null and sp.supplierPlan is not null");
		query.setParameter("status", SupplierStatus.APPROVED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierReportPojo> findAllSearchFilterSupplierReportList(TableDataInput input, Date startDate, Date endDate) {
		Query query = constructAllSuppliersForOwnerQuery(input, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		List<Object[]> objects = query.getResultList();
		List<SupplierReportPojo> supplierReportPojoList = new ArrayList<>();
		objects.forEach(list -> {
			SupplierReportPojo supplierReportPojo = new SupplierReportPojo();
			supplierReportPojo.setId(list[0].toString());
			supplierReportPojo.setCompanyName(list[1] != null ? list[1].toString() : "");
			supplierReportPojo.setCompanyRegistrationNumber(list[2] != null ? list[2].toString() : "");
			supplierReportPojo.setCountry(list[3] != null ? list[3].toString() : "");
			supplierReportPojo.setCompanyType(list[4] != null ? list[4].toString() : "");
			supplierReportPojo.setSubscriptionStatus(list[5] != null ? SubscriptionStatus.valueOf( list[5].toString()) : null);
			supplierReportPojo.setStatus(list[6] != null ? SupplierStatus.fromString(list[6].toString()) : null);
			try {
				supplierReportPojo.setCompanyRegDate(list[7] != null ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(list[7].toString()) : null);
				supplierReportPojo.setApprovedDate(list[8] != null ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(list[8].toString()) : null);

			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			supplierReportPojoList.add(supplierReportPojo);
		});
		return supplierReportPojoList;
	}

	@Override
	public long findTotalSearchFilterSupplierReportCount(TableDataInput input, Date startDate, Date endDate) {
		Query query = constructAllSuppliersForOwnerQuery(input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructAllSuppliersForOwnerQuery(TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";
		String orderSql= "";
		String querySql= "";
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("subscriptionStatus")) {
						orderSql += " s.supplierSubscription.subscriptionStatus " + dir + ",";
						querySql += " s.supplierSubscription.subscriptionStatus " + ",";
					} else if (orderColumn.equalsIgnoreCase("country")) {
						orderSql += " s.registrationOfCountry.countryName " + dir + ",";
						querySql += " s.registrationOfCountry.countryName " + ",";
					} else if (orderColumn.equalsIgnoreCase("companyType")) {
						orderSql += " s.companyStatus.companystatus " + dir + ",";
						querySql += " s.companyStatus.companystatus " + ",";
					} else if (orderColumn.equalsIgnoreCase("companyRegDate")) {
						orderSql += " s.registrationDate " + dir + ",";
						querySql += " s.registrationDate " + ",";
					} else {
						orderSql += " s." + orderColumn + " " + dir + ",";
						querySql += " s." + orderColumn + " ,";
					}
				}
				if (orderSql.lastIndexOf(",") == orderSql.length() - 1) {
					orderSql = orderSql.substring(0, orderSql.length() - 1);
					querySql = querySql.substring(0, querySql.length() - 1);
				}
			} else {
				orderSql += " s.createdDate desc nulls last, s.id ";
				querySql += " s.createdDate , s.id ";
			}
		}

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct s) ";
		} else {
			hql += "select distinct s.id, s.companyName, s.companyRegistrationNumber, c.countryName, cs.companystatus, ss.subscriptionStatus, s.status, s.registrationDate, s.approvedDate, " +querySql ;
		}

		hql += " from Supplier s ";

		// If this is not a count query, only then add the join fetch. Count
		// query does not require its
		if (!isCount) {
			hql += " left outer join s.registrationOfCountry as c left outer join s.companyStatus as cs left outer join s.supplierSubscription as ss";
		}
		hql += " where 1=1";

		if (startDate != null && endDate != null) {
			hql += " and s.registrationDate between :startDate and :endDate ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and s.status in (:status) ";
				} else if (cp.getData().equals("subscriptionStatus")) {
					hql += " and s.supplierSubscription.subscriptionStatus = (:subscriptionStatus)";
				} else if (cp.getData().replace(".", "").equalsIgnoreCase("country")) {
					hql += " and upper(s.registrationOfCountry.countryName ) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equalsIgnoreCase("companyType")) {
					hql += " and upper(s.companyStatus.companystatus ) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {

				hql += " order by "+orderSql;

		}

		final Query query = getEntityManager().createQuery(hql.toString());
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);

		}
		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						List<SupplierStatus> statuss = Arrays.asList(SupplierStatus.values());
						query.setParameter("status", statuss);
					} else {
						query.setParameter("status", SupplierStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
				} else if (cp.getData().equals("subscriptionStatus")) {
					query.setParameter("subscriptionStatus", SubscriptionStatus.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		return query;

	}

	@Override
	public long findTotalSuppliersCount(Date startDate, Date endDate) {
		String hql = "select count(distinct s) from Supplier s where 1=1";
		if (startDate != null && endDate != null) {
			hql += " and s.registrationDate between :startDate and :endDate ";
		}
		Query query = getEntityManager().createQuery(hql);
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierReportPojo> getAlSupplierListForCsvReport(String[] supplierArr, SupplierSearchFilterPojo supplierSearchFilterPojo, boolean select_all, int pageSize, int pageNo, Date startDate, Date endDate) {
		String hql = "";

		hql = constructQueryForSupplierReport(supplierArr, supplierSearchFilterPojo, select_all, startDate, endDate);

		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (supplierArr != null && supplierArr.length > 0) {
				query.setParameter("supplierIds", Arrays.asList(supplierArr));
			}
		}

		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		if (supplierSearchFilterPojo != null) {
			if (StringUtils.checkString(supplierSearchFilterPojo.getCompanyname()).length() > 0) {
				query.setParameter("companyName", "%" + supplierSearchFilterPojo.getCompanyname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(supplierSearchFilterPojo.getRegistrationnumber()).length() > 0) {
				query.setParameter("registrationNumber", "%" + supplierSearchFilterPojo.getRegistrationnumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(supplierSearchFilterPojo.getCompanytype()).length() > 0) {
				query.setParameter("companyType", "%" + supplierSearchFilterPojo.getCompanytype().toUpperCase() + "%");
			}
			if (StringUtils.checkString(supplierSearchFilterPojo.getCountry()).length() > 0) {
				query.setParameter("country", "%" + supplierSearchFilterPojo.getCountry().toUpperCase() + "%");
			}
			if (supplierSearchFilterPojo.getSubscriptionstatus() != null) {
				query.setParameter("subscriptionStatus", Arrays.asList(supplierSearchFilterPojo.getSubscriptionstatus()));
			}

			if (supplierSearchFilterPojo.getAccountstatus() != null) {
				query.setParameter("status", Arrays.asList(supplierSearchFilterPojo.getAccountstatus()));
			}
		}
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);
		List<SupplierReportPojo> finalList = new ArrayList<>();
        List<Object[]> objects = query.getResultList();
		objects.stream().forEach(list -> {
			SupplierReportPojo supplierReportPojo = new SupplierReportPojo();
			supplierReportPojo.setId(list[0].toString());
			supplierReportPojo.setCompanyName(list[1].toString()!= null ? list[1].toString() : "");
			supplierReportPojo.setCompanyRegistrationNumber(list[2] != null ? list[2].toString() : "");
			supplierReportPojo.setCompanyType(list[3] != null ? list[3].toString() : "");
			supplierReportPojo.setYearOfEstablished(list[4] != null ? Integer.valueOf(list[4].toString()) : null);
			supplierReportPojo.setTaxRegistrationNumber(list[5] != null ? list[5].toString() : "");
			String companyAddress = "";
			String line1 = list[6] != null ? list[6].toString() : "";
			String line2 = list[7] != null ? list[7].toString() : "";
			String city = list[8] != null ? list[8].toString() : "";
			String postcode = list[9] != null ? list[9].toString() : "";
			if (StringUtils.checkString(line1).length() > 0) {
				companyAddress = line1;
			}
			if (StringUtils.checkString(companyAddress).length() == 0 && StringUtils.checkString(line2).length() > 0) {
				companyAddress = line2;
			}
			if (StringUtils.checkString(companyAddress).length() > 0 && StringUtils.checkString(line2).length() > 0) {
				companyAddress += " " + line2;
			}
			if (StringUtils.checkString(companyAddress).length() == 0 && StringUtils.checkString(city).length() > 0) {
				companyAddress = city;
			}
			if (StringUtils.checkString(companyAddress).length() > 0 && StringUtils.checkString(city).length() > 0) {
				companyAddress += " " + city;
			}
			if (StringUtils.checkString(companyAddress).length() == 0 && StringUtils.checkString(postcode).length() > 0) {
				companyAddress = postcode;
			}
			if (StringUtils.checkString(companyAddress).length() > 0 && StringUtils.checkString(postcode).length() > 0) {
				companyAddress += " " + postcode;
			}
			supplierReportPojo.setCompanyAddress(companyAddress);
			supplierReportPojo.setCountry(list[10] != null ? list[10].toString() : "");
			supplierReportPojo.setState(list[11] != null ? list[11].toString() : "");
			supplierReportPojo.setCompanyContactNumber(list[12] != null ? list[12].toString() : "");
			supplierReportPojo.setFaxNumber(list[13] != null ? list[13].toString() : "");
			supplierReportPojo.setCompanyWebsite(list[14] != null ? list[14].toString() : "");
			supplierReportPojo.setLoginEmail(list[15] != null ? list[15].toString() : "");
			supplierReportPojo.setCommunicationEmail(list[16] != null ? list[16].toString() : "");
            supplierReportPojo.setDesignation(list[19] != null ? list[19].toString() : "");
			supplierReportPojo.setMobileNumber(list[20] != null ? list[20].toString() : "");
			supplierReportPojo.setSubscriptionStatus(list[22] != null ? SubscriptionStatus.valueOf( list[22].toString()) : null);
			supplierReportPojo.setStatus(list[21] != null ? SupplierStatus.fromString(list[21].toString()) : null);
			supplierReportPojo.setCurrentSubPlan(list[23] != null ? list[23].toString() : "");
            supplierReportPojo.setPromoCode(list[25] != null ? list[25].toString() : "");
			supplierReportPojo.setFullName(list[26] != null ? list[26].toString() : "");
			try {
				supplierReportPojo.setCompanyRegDate(list[17] != null ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(list[17].toString()) : null);
				supplierReportPojo.setSubscriptionEndDate(list[24] != null ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(list[24].toString()) : null);
				supplierReportPojo.setRegistrationCompleteDate(list[18] != null ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(list[18].toString()) : null);
				supplierReportPojo.setApprovedDate(list[27] != null ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(list[27].toString()) : null);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			finalList.add(supplierReportPojo);
		});
		return finalList;
	}

	private String constructQueryForSupplierReport(String[] supplierIds, SupplierSearchFilterPojo supplierSearchFilterPojo, boolean select_all, Date startDate, Date endDate) {

		String hql = "";

		hql += "select distinct s.id,s.companyName, s.companyRegistrationNumber, ct.companystatus, s.yearOfEstablished, s.taxRegistrationNumber,";

		hql += " s.line1 , s.line2 , s.city, s.postalCode, rc.countryName , ss.stateName,s.companyContactNumber, s.faxNumber, s.companyWebsite, s.loginEmail, s.communicationEmail, s.registrationDate, s.registrationCompleteDate,";

		hql += " s.designation, s.mobileNumber , s.status ,cs.subscriptionStatus , p.planName , cs.endDate, cs.promoCode, s.fullName, s.approvedDate, s.createdDate";

		hql += " from Supplier s ";

		hql += " left outer join s.registrationOfCountry as rc";

		hql += " left outer join s.companyStatus ct";

		hql += " left outer join s.state ss";

		hql += " left outer join s.supplierSubscription cs left outer join cs.supplierPlan p";

		hql += " where 1=1";

		if (!(select_all)) {
			if (supplierIds != null && supplierIds.length > 0) {
				hql += " and s.id in (:supplierIds)";
			}
		}

		if (startDate != null && endDate != null) {
			hql += " and s.registrationDate between :startDate and :endDate ";
		}

		if (supplierSearchFilterPojo != null) {

			if (StringUtils.checkString(supplierSearchFilterPojo.getCompanyname()).length() > 0) {
				hql += " and upper(s.companyName) like :companyName";
			}
			if (StringUtils.checkString(supplierSearchFilterPojo.getRegistrationnumber()).length() > 0) {
				hql += " and upper(s.companyRegistrationNumber) like :registrationNumber";
			}
			if (StringUtils.checkString(supplierSearchFilterPojo.getCompanytype()).length() > 0) {
				hql += " and upper(ct.companystatus) like :companyType";
			}
			if (StringUtils.checkString(supplierSearchFilterPojo.getCountry()).length() > 0) {
				hql += " and upper(rc.countryName) like :country";
			}
			if (supplierSearchFilterPojo.getSubscriptionstatus() != null) {
				hql += " and upper(s.supplierSubscription.subscriptionStatus) like :subscriptionStatus";
			}

			if (supplierSearchFilterPojo.getAccountstatus() != null) {
				hql += " and upper(s.status) like :status";
			}
		}

		hql += " order by s.createdDate desc ";

		return hql;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String findAssociateBuyersForSupplierId(String id) throws SQLException, IOException {

        String sql = "SELECT string_agg(B.COMPANY_NAME,', ' ORDER BY B.COMPANY_NAME) AS very_long_text FROM PROC_SUPPLIER_BUYER_MAPPING M INNER JOIN PROC_BUYER B ON B.BUYER_ID = M.BUYER_ID WHERE M.SUPPLIER_ID = :supplierId";

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("supplierId", id);
//		List<Clob> list = query.getResultList();
//		if (CollectionUtil.isNotEmpty(list)) {
//			Clob clob = list.get(0);
//			if (clob != null) {
//				InputStream in = clob.getAsciiStream();
//				StringWriter w = new StringWriter();
//				IOUtils.copy(in, w);
//				return w.toString();
//			} else {
//				return "";
//			}
//		} else {
//			return "";
//		}
		List<String> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			String clob = list.get(0);
			if (clob != null) {
				StringWriter w = new StringWriter();
				w.write(clob);
				return w.toString();
			} else {
				return "";
			}
		} else {
			return "";
		}
		// final Query query = getEntityManager().createQuery("select distinct new
		// com.privasia.procurehere.core.pojo.SupplierReportPojo(a.id, sp.companyName) from Supplier a left outer join
		// a.associatedBuyers sp where a.id=:id ");
		// query.setParameter("id", id);
		// List<SupplierReportPojo> list = query.getResultList();
		// if (CollectionUtil.isNotEmpty(list)) {
		// String companyName = "";
		// for (SupplierReportPojo by : list) {
		// if (StringUtils.checkString(by.getCompanyName()).length() > 0) {
		// companyName += StringUtils.checkString(by.getCompanyName()) + ",";
		// }
		// }
		// if (StringUtils.checkString(companyName).length() > 0) {
		// return (StringUtils.checkString(companyName).substring(0, companyName.length() - 1));
		// }
		// return "";
		// } else {
		// return "";
		// }
	}

	@SuppressWarnings("unchecked")
	@Override
	public String findGeoCoverageForSupplierId(String id) throws SQLException, IOException {
        String sql = "SELECT string_agg(S.STATE_NAME ||'-'|| CO.COUNTRY_CODE ,',' ORDER BY S.STATE_NAME) AS very_long_text FROM PROC_SUPPLIER_STATES C INNER JOIN PROC_STATE S ON S.ID = C.STATE_ID INNER  JOIN PROC_COUNTRIES CO ON CO.ID = S.COUNTRY_CODE WHERE C.SUPPLIER_ID = :supplierId";

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("supplierId", id);
		List<String> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			String clob = list.get(0);
			if (clob != null) {
				StringWriter w = new StringWriter();
				w.write(clob);
				return w.toString();
			} else {
				return "";
			}
		} else {
			return "";
		}

		// final Query query = getEntityManager().createQuery("select distinct new
		// com.privasia.procurehere.core.pojo.StatePojo(a.id, sp.stateName, c.countryCode) from Supplier a left outer
		// join a.states sp left outer join sp.country c where a.id=:id ");
		// query.setParameter("id", id);
		// List<StatePojo> list = query.getResultList();
		// if (CollectionUtil.isNotEmpty(list)) {
		// String companyName = "";
		// for (StatePojo by : list) {
		// if (StringUtils.checkString(by.getStateName()).length() > 0) {
		// companyName += StringUtils.checkString(by.getStateName()) + "-" + StringUtils.checkString(by.getCountry()) +
		// ",";
		// }
		// }
		// if (StringUtils.checkString(companyName).length() > 0) {
		// return (StringUtils.checkString(companyName).substring(0, companyName.length() - 1));
		// }
		// return "";
		// } else {
		// return "";
		// }
	}

	@SuppressWarnings("unchecked")
	@Override
	public String findNaicCodesForSupplierId(String id) throws SQLException, IOException {
        String sql = "SELECT string_agg(N.CATEGORY_CODE ||'-'|| N.CATEGORY_NAME ,',' ORDER BY N.CATEGORY_NAME) AS very_long_text FROM PROC_SUPP_NAICS_CODE_MAPPING M LEFT OUTER JOIN  PROC_NAICS_CODES N ON  N.ID = M.NAICS_CODE_ID  WHERE M.SUPPLIER_ID  = :supplierId";

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("supplierId", id);
//		List<Clob> list = query.getResultList();
//		if (CollectionUtil.isNotEmpty(list)) {
//			Clob clob = list.get(0);
//			if (clob != null) {
//				InputStream in = clob.getAsciiStream();
//				StringWriter w = new StringWriter();
//				IOUtils.copy(in, w);
//				return w.toString();
//			} else {
//				return "";
//			}
//		} else {
//			return "";
//		}
		List<String> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			String clob = list.get(0);
			if (clob != null) {
				StringWriter w = new StringWriter();
				w.write(clob);
				return w.toString();
			} else {
				return "";
			}
		} else {
			return "";
		}
		// final Query query = getEntityManager().createQuery("select distinct new
		// com.privasia.procurehere.core.pojo.NaicsCodesPojo(a.id, sp.categoryCode, sp.categoryName) from Supplier a
		// left outer join a.naicsCodes sp where a.id=:id ");
		// query.setParameter("id", id);
		// List<NaicsCodesPojo> list = query.getResultList();
		// if (CollectionUtil.isNotEmpty(list)) {
		// String companyName = "";
		// for (NaicsCodesPojo by : list) {
		// if (by.getCategoryCode() != null) {
		// companyName += by.getCategoryCode() + "-" + StringUtils.checkString(by.getCategoryName()) + ",";
		// }
		// }
		// if (StringUtils.checkString(companyName).length() > 0) {
		// return (StringUtils.checkString(companyName).substring(0, companyName.length() - 1));
		// }
		// return "";
		// } else {
		// return "";
		// }
	}

	@Override
	public long findSupplierCompanyProfileById(String tenantId) {
		final Query query = getEntityManager().createQuery("select count(distinct a.id) from SupplierCompanyProfile a where a.supplier.id =:tenantId");
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findSupplierFinancialDocumentsById(String tenantId) {
		final Query query = getEntityManager().createQuery("select count(distinct a.id) from SupplierFinanicalDocuments a  where a.supplier.id =:tenantId");
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findSupplierOtherDocumentsById(String tenantId) {
		final Query query = getEntityManager().createQuery("select count(distinct a.id) from SupplierOtherDocuments a  where a.supplier.id =:tenantId");
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findSupplierBoardOfDirectorsById(String tenantId) {
		final Query query = getEntityManager().createQuery("select count(distinct a.id) from SupplierBoardOfDirectors a  where a.supplier.id =:tenantId");
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public Long findAwardCountForSupplier(String supplierId) {

		String sql = "SELECT SUM(AWARD_COUNT) AS AWARD_COUNT  FROM ( ";
		sql += " SELECT count(*) AS AWARD_COUNT  FROM PROC_RFT_AWARD_SUP S WHERE S.SUPPLIER_ID = :supplierId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS AWARD_COUNT  FROM PROC_RFA_AWARD_SUP S WHERE S.SUPPLIER_ID = :supplierId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS AWARD_COUNT  FROM PROC_RFP_AWARD_SUP S WHERE S.SUPPLIER_ID = :supplierId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS AWARD_COUNT  FROM PROC_RFQ_AWARD_SUP S WHERE S.SUPPLIER_ID = :supplierId ";
		sql += " ) a ";
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("supplierId", supplierId);
		BigDecimal value = (BigDecimal) query.getSingleResult();
		return value.longValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public SupplierPojo findInvitedAndSubmitedCountsForSupplier(String supplierId) {

		String sql = "SELECT SUM(INVITED_COUNT) AS INVITED_COUNT, sum(PARTICIPATED_COUNT) AS PARTICIPATED_COUNT FROM ( ";
		sql += " SELECT count(*) AS INVITED_COUNT , SUM(CASE WHEN S.IS_BID_SUBMITTED = 1 THEN 1 ELSE 0 END )  AS PARTICIPATED_COUNT   FROM PROC_RFA_EVENT_SUPPLIERS S WHERE S.SUPPLIER_ID = :supplierId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS INVITED_COUNT , SUM(CASE WHEN S.IS_BID_SUBMITTED = 1 THEN 1 ELSE 0 END )  AS PARTICIPATED_COUNT   FROM PROC_RFP_EVENT_SUPPLIERS S WHERE S.SUPPLIER_ID = :supplierId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS INVITED_COUNT , SUM(CASE WHEN S.IS_BID_SUBMITTED = 1 THEN 1 ELSE 0 END )  AS PARTICIPATED_COUNT   FROM PROC_RFQ_EVENT_SUPPLIERS S WHERE S.SUPPLIER_ID = :supplierId  ";
		sql += " UNION ";
		sql += " SELECT count(*) AS INVITED_COUNT , SUM(CASE WHEN S.IS_BID_SUBMITTED = 1 THEN 1 ELSE 0 END )  AS PARTICIPATED_COUNT   FROM PROC_RFI_EVENT_SUPPLIERS S WHERE S.SUPPLIER_ID = :supplierId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS INVITED_COUNT , SUM(CASE WHEN S.IS_BID_SUBMITTED = 1 THEN 1 ELSE 0 END )  AS PARTICIPATED_COUNT   FROM PROC_RFT_EVENT_SUPPLIERS S WHERE S.SUPPLIER_ID = :supplierId ";
		sql += " ) a ";
		Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("supplierId", supplierId);
		List<Object[]> records = query.getResultList();
		List<SupplierPojo> list = new ArrayList<SupplierPojo>();
		if (records != null) {
			for (Object[] result : records) {
				SupplierPojo pojo = new SupplierPojo();
				pojo.setInvited(result[0] != null ? ((BigDecimal) result[0]).longValue() : 0l);
				pojo.setSubmited(result[1] != null ? ((BigDecimal) result[1]).longValue() : 0l);
				list.add(pojo);
			}
			return list.get(0);
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public SupplierDetailsCountPojo findSupplierDetailsCounts(String supplierId) {
		String sql = "SELECT  S.SUPPLIER_ID AS SUPPLIER_ID, COUNT(CP.SUPPLIER_ID) AS COMP_PROFILE, COUNT(OD.SUPPLIER_ID) AS OTHER_DOCS, COUNT(TR.SUPPLIER_ID) AS TRACK_RECORD, COUNT(FD.SUPPLIER_ID) AS FINC_DOCS, COUNT(BD.SUPPLIER_ID) AS BORD_DIR FROM PROC_SUPPLIER S LEFT OUTER JOIN PROC_SUPPLIER_COMPANY_PROFILE CP ON  CP.SUPPLIER_ID = S.SUPPLIER_ID LEFT OUTER JOIN PROC_SUPPLIER_OTHER_DOCS OD ON  OD.SUPPLIER_ID = S.SUPPLIER_ID  LEFT OUTER JOIN PROC_SUPPLIER_PROJECTS TR ON  TR.SUPPLIER_ID = S.SUPPLIER_ID LEFT OUTER JOIN FINANICAL_DOCUMENTS FD ON  FD.SUPPLIER_ID = S.SUPPLIER_ID LEFT OUTER JOIN BOARD_OF_DIRS BD ON  BD.SUPPLIER_ID = S.SUPPLIER_ID WHERE S.SUPPLIER_ID = :supplierId  GROUP BY S.SUPPLIER_ID ";
		Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("supplierId", supplierId);
		List<Object[]> records = query.getResultList();
		List<SupplierDetailsCountPojo> list = new ArrayList<SupplierDetailsCountPojo>();
		if (records != null) {
			for (Object[] result : records) {
				SupplierDetailsCountPojo pojo = new SupplierDetailsCountPojo();
				pojo.setCompanyProfile(result[1] != null ? ((BigInteger) result[1]).longValue() : 0l);
				pojo.setOtherDocs(result[2] != null ? ((BigInteger) result[2]).longValue() : 0l);
				pojo.setTrackRecord(result[3] != null ? ((BigInteger) result[3]).longValue() : 0l);
				pojo.setFincDocs(result[4] != null ? ((BigInteger) result[4]).longValue() : 0l);
				pojo.setBordDir(result[5] != null ? ((BigInteger) result[5]).longValue() : 0l);
				list.add(pojo);
			}
			return list.get(0);
		}
		return null;
	}

	@Override
	public boolean isExistsByCompanyNameOrRegNo(Supplier supplier, String companyName, String regNumber) {

		final Query query = getEntityManager().createQuery("from Supplier a where (a.companyRegistrationNumber = :companyRegistrationNumber and a.registrationOfCountry = :registrationOfCountry) or (upper(a.companyName) = :companyName and a.registrationOfCountry = :registrationOfCountry)");
		query.setParameter("registrationOfCountry", supplier.getRegistrationOfCountry());

		query.setParameter("companyRegistrationNumber", StringUtils.checkString(regNumber));
		query.setParameter("companyName", StringUtils.checkString(companyName).toUpperCase());
		query.setParameter("registrationOfCountry", supplier.getRegistrationOfCountry());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public void updateSupplierStatus(String supplierId, User loggedUser, SupplierStatus status) {
		StringBuffer hql = new StringBuffer("update Supplier a set  a.onBoardingFromsubmitedDate =:onBoardingFromsubmitedDate, a.onBoardingFormSubmittedBy= :onBoardingFormSubmittedBy");
		if (status != null) {
			hql.append(" , a.status = :status");
		}

		hql.append(" where a.id = :supplierId");

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("onBoardingFromsubmitedDate", new Date());
		query.setParameter("onBoardingFormSubmittedBy", loggedUser);
		if (status != null) {
			query.setParameter("status", status);
		}

		query.setParameter("supplierId", supplierId);
		query.executeUpdate();
	}

	@Override
	public Supplier findPlainSupplierUsingConstructorById(String id) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.Supplier(s.id, s.companyName, s.companyContactNumber, s.loginEmail, s.fullName) from Supplier s where s.id = :id");
		query.setParameter("id", id);
		return (Supplier) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBuyerListForSupplierId(String tenantId) {
		String hql = "select DISTINCT BUYER_ID FROM PROC_SUPPLIER_BUYER_MAPPING where SUPPLIER_ID = '" + tenantId + "'";
		Query query = getEntityManager().createNativeQuery(hql);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}
	}

}
