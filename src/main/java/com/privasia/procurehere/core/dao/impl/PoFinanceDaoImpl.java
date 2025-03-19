package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PoFinanceDao;
import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.FinancePoStatus;
import com.privasia.procurehere.core.enums.FinancePoType;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class PoFinanceDaoImpl extends GenericDaoImpl<FinancePo, String> implements PoFinanceDao {

	private static final Logger LOG = LogManager.getLogger(Global.FINANCE_COMPANY_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public FinancePo getPoFinanceByPrIdAndSupID(String prId, String supId, String loggedInUserTenantId) {

		StringBuilder hsql = new StringBuilder("from FinancePo p left outer join fetch p.supplier sp left outer join fetch p.financeCompany fc where p.po.id= :id and p.supplier.id = :supId and p.financeCompany.id = :financeCompanyId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", prId);
		query.setParameter("supId", supId);
		query.setParameter("financeCompanyId", loggedInUserTenantId);
		List<FinancePo> polist = query.getResultList();
		if (CollectionUtil.isNotEmpty(polist))
			return polist.get(0);
		else
			return new FinancePo();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FinancePo> findAllSearchFilterPoForFinance(String tenantId, TableDataInput input, Date startDate, Date endDate, FinancePoStatus status, String selectedSupplier, FinancePoType financePoType) {
		final Query query = constructPrSearchFilterForTenantQueryForDashboard(tenantId, input, false, startDate, endDate, status, selectedSupplier, financePoType);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSearchFilterPoForFinance(String tenantId, TableDataInput input, Date startDate, Date endDate, FinancePoStatus status, String selectedSupplier, FinancePoType financePoType) {
		final Query query = constructPrSearchFilterForTenantQueryForDashboard(tenantId, input, true, startDate, endDate, status, selectedSupplier, financePoType);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalPoForFinance(String tenantId, FinancePoType financePoType) {

		StringBuilder hsql = new StringBuilder("select count(p) from FinancePo p left outer join p.po as po left outer join p.financeCompany as fc left outer join p.supplier as s where fc.id = :tenantId and p.financePoType = :financePoType and po.status <> :poStatus");

		Query query = getEntityManager().createQuery(hsql.toString());
		// query.setParameter("supplierId", tenantId);

		query.setParameter("tenantId", tenantId);
		query.setParameter("financePoType", financePoType);
		query.setParameter("poStatus", PoStatus.CANCELLED);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructPrSearchFilterForTenantQueryForDashboard(String tenantId, TableDataInput tableParams, boolean isCount, Date startDate, Date endDate, FinancePoStatus status, String selectedSupplier, FinancePoType financePoType) {

		String hql = "";
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(p) ";
		} else {
			hql += "select distinct new com.privasia.procurehere.core.entity.FinancePo(p.id, po.id, s.companyName,  po.name ,  po.description, po.poNumber, p.financePoStatus, po.createdDate, po.grandTotal, po.decimal)";
		}

		hql += " from FinancePo p left outer join p.po as po left outer join p.financeCompany as fc left outer join p.supplier as s ";

		hql += " where fc.id = :tenantId and p.financePoType = :financePoType and po.status <> :poStatus";

		boolean isSelectOn = false;
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("statusValue")) {
					hql += " and p.financePoStatus in (:status) ";
					isSelectOn = true;
				} else if (cp.getData().equalsIgnoreCase("supplier.companyName")) {
					hql += " and upper(s.companyName) like (:" + cp.getData().replace(".", "") + ") ";
				}

				else {
					hql += " and upper(p." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		if (StringUtils.checkString(selectedSupplier).length() > 0) {
			LOG.info("-----------------------------" + selectedSupplier);
			hql += " and s.id = '" + selectedSupplier + "' ";
		}

		if (status != null) {
			hql += " and p.financePoStatus in (:status) ";
		}

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and  p.sharedDate between :startDate and :endDate ";
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
				hql += " order by po.createdDate desc ";
			}
		}

		LOG.info("HQL=" + hql);
		final Query query = getEntityManager().createQuery(hql.toString());

		// query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("statusValue")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						query.setParameter("status", Arrays.asList(FinancePoStatus.values()));
					} else {
						query.setParameter("status", FinancePoStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		if (status != null) {
			query.setParameter("status", status);
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("financePoType", financePoType);
		query.setParameter("poStatus", PoStatus.CANCELLED);
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> findAllSupplierGloballyForFinance(String searchVal, String tenantId) {
		final Query query = getEntityManager().createQuery("select distinct s from Supplier s, FinancePo fp left outer join fp.supplier fs left outer join fp.financeCompany fc left outer join fetch s.registrationOfCountry rc where fp.supplier.id = s.id and fp.financeCompany.id= :tenantId and (upper(s.companyName) like :searchVal) or (upper(s.companyRegistrationNumber) like :searchVal)");
		query.setParameter("searchVal", "%" + searchVal.toUpperCase() + "%");
		query.setParameter("tenantId", tenantId);

		List<Supplier> list = query.getResultList();
		LOG.info("  Finance.....  " + list.size());
		return list;
	}

	@Override
	public long findTotalCountPoForFinanceByStatus(String tenantId, FinancePoStatus status, FinancePoType financePoType) {

		StringBuilder hsql = new StringBuilder("select count(p) from FinancePo p left outer join p.po as po left outer join p.financeCompany as fc left outer join p.supplier as s where fc.id = :tenantId  and p.financePoStatus = :status and p.financePoType = :financePoType and po.status <> :poStatus");

		Query query = getEntityManager().createQuery(hsql.toString());

		query.setParameter("tenantId", tenantId);
		query.setParameter("financePoType", financePoType);
		query.setParameter("status", status);
		query.setParameter("poStatus", PoStatus.CANCELLED);
		return ((Number) query.getSingleResult()).longValue();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Po> findAllPrPoGloballyForFinance(String searchVal, String tenantId) {
		final Query query = getEntityManager().createQuery("select po from Po po, FinancePo fp left outer join fetch po.businessUnit as bu left outer join fetch po.supplier as fs left outer join fp.po fppr left outer join fp.financeCompany fc where fp.po.id = po.id and fp.financeCompany.id= :tenantId and ((upper(po.poNumber) like :searchVal) or (upper(po.referenceNumber) like :searchVal) or (upper(po.name) like :searchVal) or (upper(po.description) like :searchVal))");
		query.setParameter("searchVal", "%" + searchVal.toUpperCase() + "%");
		query.setParameter("tenantId", tenantId);
		List<Po> list = query.getResultList();
		return list;

	}

	@SuppressWarnings("unchecked")
	@Override
	public FinancePo getPoFinanceForSupplier(String poId, String tenantId) {
		StringBuilder hsql = new StringBuilder("from FinancePo p left outer join fetch p.supplier sp left outer join fetch p.financeCompany fc where p.po.id= :id and p.supplier.id = :supId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", poId);
		query.setParameter("supId", tenantId);
		List<FinancePo> polist = query.getResultList();
		if (CollectionUtil.isNotEmpty(polist))
			return polist.get(0);
		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FinancePo> findFinancePoByIds(String[] poArr) {
		String sql = "select distinct fpo from FinancePo fpo left outer join fetch fpo.po p inner join p.createdBy cr left outer join  p.businessUnit as bu left outer join fetch p.supplier as fs where p.id in :poIds order by p.createdDate desc ";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("poIds", Arrays.asList(poArr));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FinancePo> findFinancePoByIdsAndStatus(String poId) {
		String sql = "select distinct fpo from FinancePo fpo left outer join fetch fpo.po p where p.id = :poId and fpo.financePoStatus = :financePoStatus ";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("poId", poId);
		query.setParameter("financePoStatus", FinancePoStatus.NEW);
		return query.getResultList();
	}
	
	@Override
	public void deletePo(String id) {
		Query query = getEntityManager().createQuery("delete from FinancePo po  where po.id = :id ").setParameter("id", id);
		query.executeUpdate();
	}
}
