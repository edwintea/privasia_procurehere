package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.CostCenterDao;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("costCenterDao")
public class CostCenterDaoImpl extends GenericDaoImpl<CostCenter, String> implements CostCenterDao {

	private static final Logger LOG = LogManager.getLogger(CostCenterDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<CostCenter> getAllActiveCostCentersForTenant(String tenantId) {
		final Query query = getEntityManager().createQuery("from CostCenter cc left outer join fetch cc.createdBy as cb left outer join fetch cc.modifiedBy as mb where cc.buyer.id = :tenantId and cc.status = :status order by cc.costCenter");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CostCenter> getActiveCostCentersForTenant(String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.CostCenter(cc.id, cc.costCenter,cc.description,cc.status) from CostCenter cc where cc.buyer.id = :tenantId and cc.status = :status order by cc.costCenter");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public CostCenter getActiveCostCenterForTenantByCostCenterName(String costCenterName, String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.CostCenter(cc.id, cc.costCenter, cc.description, cc.status) from CostCenter cc where cc.buyer.id = :tenantId and cc.status = :status and cc.costCenter = :costCenterName ");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("costCenterName", costCenterName);
		List<CostCenter> costList = query.getResultList();
		if (CollectionUtil.isNotEmpty(costList)) {
			return costList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(CostCenter costCenter, String tenantId) {
		StringBuilder hsql = new StringBuilder("from CostCenter as cc where upper(cc.costCenter) = upper(:costCenter) and cc.buyer.id = :tenantId ");
		if (StringUtils.checkString(costCenter.getId()).length() > 0) {
			hsql.append(" and cc.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("costCenter", costCenter.getCostCenter());
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(costCenter.getId()).length() > 0) {
			query.setParameter("id", costCenter.getId());
		}
		List<CostCenter> stList = query.getResultList();
		return CollectionUtil.isNotEmpty(stList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CostCenter> findCostCentersForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructCostCenterForTenantQuery(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredCostCentersForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructCostCenterForTenantQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalCostCentersForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (c) from CostCenter c where c.status =:status and c.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param isCount
	 * @return
	 */
	private Query constructCostCenterForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(c) ";
		}

		hql += " from CostCenter c ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch c.createdBy as cb left outer join fetch c.modifiedBy as mb ";
		}

		hql += " where c.buyer.id = :tenantId ";
		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT ::  " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					hql += " and c.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(c." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}
		if (!isStatusFilterOn) {
			hql += " and c.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " c." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}

		// LOG.info("HQL : " + hql);

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

	@SuppressWarnings("unchecked")
	@Override
	public List<CostCenter> getAllCostCentersByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from CostCenter cc where cc.buyer.id = :tenantId order by cc.costCenter");
		LOG.info("Query ======>>>>>" + query);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public CostCenter getByCostCenter(String costCenter, String tenantId) {
		final Query query = getEntityManager().createQuery("from CostCenter cc where cc.buyer.id = :tenantId and upper(cc.costCenter) = :costCenter ");
		query.setParameter("tenantId", tenantId);
		query.setParameter("costCenter", costCenter.toUpperCase());
		List<CostCenter> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CostCenterPojo> fetchAllCostCenterForTenant(String tenantId, String searchValue, String businessUnitId) {
		StringBuffer hql = new StringBuffer("");
		if (StringUtils.checkString(businessUnitId).length() > 0) {
			hql.append("select distinct new com.privasia.procurehere.core.pojo.CostCenterPojo(cc.id, cc.costCenter, cc.description) from BusinessUnit bu join bu.costCenter cc where bu.id = :businessUnitId and cc.status = :status and cc.buyer.id = :tenantId ");
		} else {
			hql.append("select distinct new com.privasia.procurehere.core.pojo.CostCenterPojo(cc.id, cc.costCenter, cc.description) from CostCenter cc where cc.status = :status and cc.buyer.id = :tenantId ");
		}

		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and (upper(cc.costCenter) like (:searchValue)  or upper(cc.description) like (:searchValue))");
		}
		// hql.append(" order by cc.costCenter");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);

		if (StringUtils.checkString(businessUnitId).length() > 0) {
			query.setParameter("businessUnitId", businessUnitId);
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long fetchFilterCountAllCostForTenant(String tenantId, String businessUnitId) {
		StringBuffer hql = new StringBuffer("select count(cc) from CostCenter cc where cc.buyer.id = :tenantId and cc.status = :status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCostCenterByBusinessId(String id) {
		String hql = "select DISTINCT COST_CENTER_ID FROM PROC_BUSINESS_COST_MAPPING where BUSINESS_UNIT_ID = :id ";
		Query query = getEntityManager().createNativeQuery(hql);
		query.setParameter("id", id);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CostCenterPojo> fetchAllCostCenterForTenantForUnit(String tenantId, String searchValue, List<String> assignedCostId, String buId) {
		StringBuffer hql = null;
		if (CollectionUtil.isNotEmpty(assignedCostId)) {
			hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.CostCenterPojo(cc.id, cc.costCenter, cc.description) from CostCenter cc where cc.status = :status and cc.buyer.id =:tenantId and cc.id not in (select bc.id from BusinessUnit bu join bu.costCenter bc where bu.id = :id)");
		} else {
			hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.CostCenterPojo(cc.id, cc.costCenter, cc.description) from CostCenter cc where cc.status = :status and cc.buyer.id =:tenantId");
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and (upper(cc.costCenter) like (:searchValue) or upper(cc.description) like (:searchValue))");
		}
		// hql.append(" order by cc.costCenter");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);

		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		if (CollectionUtil.isNotEmpty(assignedCostId)) {
			// query.setParameter("assignedCostId", assignedCostId);
			query.setParameter("id", buId);
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long fetchFilterCountAllCostForTenantForUnit(String tenantId, List<String> assignedCostId, String buId) {
		StringBuffer hql = null;
		if (CollectionUtil.isNotEmpty(assignedCostId)) {
			hql = new StringBuffer("select count(cc) from CostCenter cc where cc.buyer.id = :tenantId and cc.status = :status and cc.id not in (select bc.id from BusinessUnit bu join bu.costCenter bc where bu.id = :id)");
		} else {
			hql = new StringBuffer("select count(cc) from CostCenter cc where cc.buyer.id = :tenantId and cc.status = :status ");
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		if (CollectionUtil.isNotEmpty(assignedCostId)) {
			// query.setParameter("assignedCostId", assignedCostId);
			query.setParameter("id", buId);
		}
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CostCenterPojo> findCostCenterListByTenantId(String tenantId, TableDataInput input, String id, String[] costCenterIds, String[] removeIds) {
		final Query query = constructCostCenterForTenantQuery(tenantId, input, id, false, costCenterIds, removeIds);
		if (query != null) {
			query.setFirstResult(input.getStart());
			query.setMaxResults(input.getLength());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public long findTotalFilteredCostCenterForTenant(String tenantId, TableDataInput input, String id, String[] costCenterIds, String[] removeIds) {
		final Query query = constructCostCenterForTenantQuery(tenantId, input, id, true, costCenterIds, removeIds);
		if (query != null) {
			return ((Number) query.getSingleResult()).longValue();
		} else {
			return 0;
		}
	}

	private Query constructCostCenterForTenantQuery(String tenantId, TableDataInput input, String id, boolean isCount, String[] costCenterIds, String[] removeIds) {
		String hql = "";

		if (isCount) {
			hql += "select count(distinct cc.id) ";
		}
		if (!isCount) {
			hql += "select distinct NEW com.privasia.procurehere.core.pojo.CostCenterPojo(cc.id, cc.costCenter, cc.description, cc.status) ";
		}
		hql += " from CostCenter cc ";

		hql += " where cc.buyer.id = :tenantId and cc.id in (select bc.id from BusinessUnit bu join bu.costCenter bc where bu.id = :id  ";
		if (removeIds != null && removeIds.length > 0) {
			hql += " and bc.id not in (:removeIds) ";
		}
		hql += " )";

		if (costCenterIds != null && costCenterIds.length > 0 && removeIds != null && removeIds.length > 0) {
			hql += " or (cc.id in (:costCenterIds) and cc.id not in (:removeIds)) ";
		}
		if (costCenterIds != null && costCenterIds.length > 0 && (removeIds == null || (removeIds != null && removeIds.length == 0))) {
			hql += " or (cc.id in (:costCenterIds)) ";
		}
		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and cc.status = (:" + cp.getData() + ")";
				} else {
					hql += " and (upper(cc.costCenter) like :" + cp.getData().replace(".", "") + " ";
					hql += " or upper(cc.description) like :" + cp.getData().replace(".", "") + " )";
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
					hql += " cc." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by cc.costCenter";
			}
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("id", id);
		if (removeIds != null && removeIds.length > 0) {
			query.setParameter("removeIds", Arrays.asList(removeIds));
		}
		if (costCenterIds != null && costCenterIds.length > 0) {
			query.setParameter("costCenterIds", Arrays.asList(costCenterIds));
		}
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CostCenterPojo getCostCenterByCostId(String id) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.CostCenterPojo(cc.id, cc.costCenter, cc.description, cc.status) from CostCenter cc where cc.id = :id");
		query.setParameter("id", id);
		List<CostCenterPojo> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	@Override
	public void deleteAssignedCostCenter(String id) {
		StringBuilder hsql = new StringBuilder("delete from PROC_BUSINESS_COST_MAPPING where BUSINESS_UNIT_ID= :id ");
		Query query = getEntityManager().createNativeQuery(hsql.toString());
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public long getCountOfInactiveCostCenter(List<String> assignedCostId) {
		StringBuffer hql = new StringBuffer("select count(cc) from CostCenter cc where cc.status = :status and cc.id in (:assignedCostId)");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.INACTIVE);
		query.setParameter("assignedCostId", assignedCostId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CostCenter> findCostCenterListForTenantIdForCsv(String tenantId, int pageSize, int pageNo) {
		final Query query = getEntityManager().createQuery("from CostCenter cc where cc.buyer.id = :tenantId order by cc.costCenter");
		LOG.info("Query ======>>>>>" + query);
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);

		List<CostCenter> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@Override
	public long findAllCostCentersForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (c) from CostCenter c where c.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CostCenter> getCostCentersByBusinessUnitIdForAwardScreen(String id) {
		String hql = "select distinct new com.privasia.procurehere.core.pojo.CostCenterPojo(cc.id,cc.costCenter,cc.description, cc.status) from BusinessUnit bu join bu.costCenter cc where bu.id = :id ";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		return query.getResultList();
	}

}
