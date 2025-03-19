package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.CompanyStatusDao;
import com.privasia.procurehere.core.entity.CompanyStatus;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/*
 * Author 
 * 
 * */

@Repository("companyStatusDao")
public class CompanyStatusDaoImpl extends GenericDaoImpl<CompanyStatus, String> implements CompanyStatusDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);
	
	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public List<CompanyStatus> findAllCompanyStatus() {
		final Query query = getEntityManager().createQuery("from CompanyStatus cs left outer join fetch cs.createdBy as cb left outer join fetch cs.modifiedBy as mb  where cs.active =:active order by cs.companystatus");
		query.setParameter("active", true);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(CompanyStatus companyStatus) {
		StringBuilder hsql = new StringBuilder("from CompanyStatus c where c.companystatus = :companystatus");
		if (StringUtils.checkString(companyStatus.getId()).length() > 0) {
			hsql.append(" and c.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("companystatus", companyStatus.getCompanystatus());
		if (StringUtils.checkString(companyStatus.getId()).length() > 0) {
			query.setParameter("id", companyStatus.getId());
		}
		List<CompanyStatus> scList = query.getResultList();
		return CollectionUtil.isNotEmpty(scList);
	}

	@Override
	public void loadCompanyStatusMasterData(List<CompanyStatus> list, User createdBy) {
		for (CompanyStatus c : list) {
			if (!isExists(c)) {
				c.setCreatedBy(createdBy);
				c.setCreatedDate(new Date());
				save(c);
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CompanyStatus> findAllCompanyStatusList(TableDataInput tableParams) {
		final Query query = constructCompanyStatusQuery(tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredCompanyStatusList(TableDataInput tableParams) {
		final Query query = constructCompanyStatusQuery(tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
		}

	@Override
	public long findTotalCompanyStatusList() {
		StringBuilder hql = new StringBuilder("select count (cs) from CompanyStatus cs where cs.active =:status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Boolean.TRUE);
		return ((Number) query.getSingleResult()).longValue();
	}
	private Query constructCompanyStatusQuery(TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(cs) ";
		}

		hql += " from CompanyStatus cs ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch cs.createdBy as cb left outer join fetch cs.modifiedBy as mb ";
		}
		hql += " where 1 = 1";

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					hql += " and cs.active = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(cs." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if(!isStatusFilterOn) {
			hql += " and cs.active = :status ";
		}
		
		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " cs." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}
		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("active")) {
					query.setParameter("status", Boolean.TRUE);
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			query.setParameter("status", Boolean.TRUE);
		}
		return query;		
 }
}
