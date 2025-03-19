package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import com.privasia.procurehere.core.entity.CostCenter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ProcurementMethodDao;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author sana
 */
@Repository
public class ProcurementMethodDaoImpl extends GenericDaoImpl<ProcurementMethod, String> implements ProcurementMethodDao {

	private static final Logger LOG = LogManager.getLogger(ProcurementMethodDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcurementMethod> findProcurementMethodsForTenant(String tenantId, TableDataInput input) {
		final Query query = constructProcurementMethodTenantQuery(tenantId, input, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		List<ProcurementMethod> stList = query.getResultList();
		if (CollectionUtil.isNotEmpty(stList)) {
			return stList;
		} else {
			return null;
		}
	}

	@Override
	public long findTotalFilteredProcurementMethodsForTenant(String tenantId, TableDataInput input) {
		final Query query = constructProcurementMethodTenantQuery(tenantId, input, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructProcurementMethodTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {
		String hql = "";
		if (isCount) {
			hql += "select count(pm) ";
		}
		hql += " from ProcurementMethod pm ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch pm.createdBy as cb left outer join fetch pm.modifiedBy as mb ";
		}

		hql += " where pm.buyer.id = :tenantId ";
		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT ::  " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					hql += " and pm.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(pm." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}
		if (!isStatusFilterOn) {
			hql += " and pm.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " pm." + orderColumn + " " + dir + ",";
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
	public long findCountOfProcurementMethodsForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (pm) from ProcurementMethod pm where pm.status =:status and pm.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(ProcurementMethod procurementMethod, String tenantId) {
		StringBuilder hsql = new StringBuilder("from ProcurementMethod as pm where upper(pm.procurementMethod) = upper(:procurementMethod) and pm.buyer.id = :tenantId ");
		if (StringUtils.checkString(procurementMethod.getId()).length() > 0) {
			hsql.append(" and pm.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("procurementMethod", procurementMethod.getProcurementMethod());
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(procurementMethod.getId()).length() > 0) {
			query.setParameter("id", procurementMethod.getId());
		}
		List<ProcurementMethod> stList = query.getResultList();
		return CollectionUtil.isNotEmpty(stList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcurementMethod> getAllProcurementMethodByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from ProcurementMethod pm where pm.buyer.id = :tenantId order by pm.procurementMethod");
		LOG.info("Query ======>>>>>" + query);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcurementMethod> getAllActiveProcurementMethod(String tenantId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.ProcurementMethod(p.id, p.procurementMethodCode, p.procurementMethod , p.description ) from ProcurementMethod p where p.status = :status and p.buyer.id = :tenantId ");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProcurementMethod getByProcurementMethod(String procurementMethod, String tenantId) {
		final Query query = getEntityManager().createQuery("from ProcurementMethod pp where pp.buyer.id = :tenantId and upper(pp.procurementMethod) = :procurementMethod ");
		query.setParameter("tenantId", tenantId);
		query.setParameter("procurementMethod", procurementMethod.toUpperCase());
		List<ProcurementMethod> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

}
