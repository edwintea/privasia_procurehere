package com.privasia.procurehere.core.dao.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.BusinessUnitDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.BusinessUnitPojo;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
@Repository
public class BusinessUnitDaoImpl extends GenericDaoImpl<BusinessUnit, String> implements BusinessUnitDao {

	private static final Logger LOG = LogManager.getLogger(BusinessUnitDaoImpl.class);

	@Override
	@SuppressWarnings("unchecked")
	public List<BusinessUnit> findBusinessUnitsForTenant(String tenantId, TableDataInput input) {
		final Query query = constructBusinessUnitForTenantQuery(tenantId, input, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param isCount
	 * @return
	 */
	private Query constructBusinessUnitForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(bu) ";
		} else {
			hql += "select new com.privasia.procurehere.core.entity.BusinessUnit(bu.id, bu.unitName, bu.displayName, bu.status, cb, bu.createdDate, mb, bu.modifiedDate, parent.id, parent.unitCode, parent.unitName) ";
		}

		hql += " from BusinessUnit bu left outer join bu.parent parent ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join bu.createdBy as cb left outer join bu.modifiedBy as mb ";
		}

		hql += " where bu.buyer.id = :tenantId ";
		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT ::  " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("status")) {
					hql += " and bu.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("parent.unitName")) {
					hql += " and upper(parent.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(bu." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}
		if (!isStatusFilterOn) {
			hql += " and bu.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("parent.unitName")) {
						hql += orderColumn + " " + dir + ",";
					} else {
						hql += " bu." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.debug("INPUT 1 ::  " + cp.getData() + "VALUE 1 :: " + cp.getSearch().getValue());
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
		LOG.info("query???? "+query);
		return query;
	}

	@Override
	public long findTotalFilteredBusinessUnitsForTenant(String tenantId, TableDataInput input) {
		final Query query = constructBusinessUnitForTenantQuery(tenantId, input, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalBusinessUnitsForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count(bu) from BusinessUnit bu where bu.status =:status and bu.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public boolean isExists(BusinessUnit businessUnit, String tenantId) {
		StringBuilder hsql = new StringBuilder("select count(*) from BusinessUnit bu where upper(bu.unitName)= :unitName and bu.buyer.id = :tenantId ");
		if (StringUtils.checkString(businessUnit.getId()).length() > 0) {
			hsql.append(" and bu.id <> :id");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("unitName", businessUnit.getUnitName().toUpperCase());
		if (StringUtils.checkString(businessUnit.getId()).length() > 0) {
			query.setParameter("id", businessUnit.getId());
		}
		query.setParameter("tenantId", tenantId);
		LOG.debug("hsql :" + hsql);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
/*	public List<BusinessUnit> getPlainActiveBusinessUnitForTenant(String tenantId) {
		LOG.info("entered getPlainActiveBusinessUnitForTenant " + tenantId);

		// Define the HQL query
		String hql = "select new com.privasia.procurehere.core.entity.BusinessUnit(bu.id, bu.unitName, bu.displayName, bu.status) " +
				"from BusinessUnit bu where bu.buyer.id = :tenantId and bu.status = :status";

		// Log the HQL query
		LOG.info("JPQL Query: " + hql);

		// Create the query
		final Query query = getEntityManager().createQuery(hql.toString());

		// Set the parameters
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);

		// Log the parameters
		LOG.info("Parameters: tenantId = " + tenantId + ", status = " + Status.ACTIVE);

		// Execute the query and return the result list
		return query.getResultList();
	}*/

	public List<BusinessUnit> getPlainActiveBusinessUnitForTenant(String tenantId) {
		LOG.info("entered getPlainActiveBusinessUnitForTenant " + tenantId);
		String hql = "select new com.privasia.procurehere.core.entity.BusinessUnit(bu.id, bu.unitName, bu.displayName, bu.status) " +
				"from BusinessUnit bu where bu.buyer.id = :tenantId and bu.status = :status ORDER BY display_name ASC ";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);

		List<BusinessUnit> results = query.getResultList();

		// Logging the results
/*		for (BusinessUnit bu : results) {
			LOG.info("BusinessUnit: id=" + bu.getId() + ", unitName=" + bu.getUnitName() +
					", displayName=" + bu.getDisplayName() + ", status=" + bu.getStatus());
		}*/

		return results;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<BusinessUnit> getPlainActiveBusinessUnitParentsForTenant(String tenantId) {
		String hql = "select new com.privasia.procurehere.core.entity.BusinessUnit(bu.id, bu.unitName, bu.displayName, bu.status) from BusinessUnit bu where bu.buyer.id = :tenantId and bu.status = :status and bu.parent is null ";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@Override
	public BusinessUnit getPlainBusinessUnitById(String id) {
		String hql = "select new com.privasia.procurehere.core.entity.BusinessUnit(bu.id, bu.unitName,bu.unitCode, bu.displayName, bu.status, bu.idSequence) from BusinessUnit bu where bu.id = :id ";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		return (BusinessUnit) query.getSingleResult();
	}

	@Override
	public void updateBusinessUnitSequenceNumer(String businessUnitId, String sequenceType, Integer sequence) {
		String hql = "update BusinessUnit set " + sequenceType + " = :sequenceNumber where id = :id";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", businessUnitId);
		query.setParameter("sequenceNumber", sequence);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean isEmptyUnitCode(String tenantId) {
		String hql = "select new com.privasia.procurehere.core.entity.BusinessUnit(bu.id, bu.unitName, bu.displayName, bu.status) from BusinessUnit bu where bu.buyer.id = :tenantId and bu.status = :status and (bu.unitCode is null or bu.unitCode = :unitCode )";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("unitCode", "");
		query.setParameter("status", Status.ACTIVE);
		List<BusinessUnit> businessUnits = query.getResultList();
		if (CollectionUtil.isNotEmpty(businessUnits)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@Override
	public boolean isExistsUnitCode(String unitCode, String tenantId, String id) {

		StringBuilder hsql = new StringBuilder("select count(*) from BusinessUnit bu where upper(bu.unitCode)= :unitCode and bu.buyer.id = :tenantId ");
		if (StringUtils.checkString(id).length() > 0) {
			hsql.append(" and bu.id <> :id");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("unitCode", unitCode);
		if (StringUtils.checkString(id).length() > 0) {
			query.setParameter("id", id);
		}
		return ((Number) query.getSingleResult()).intValue() > 0;

		/*
		 * String hql =
		 * "select new com.privasia.procurehere.core.entity.BusinessUnit(bu.id, bu.unitName, bu.displayName, bu.status) from BusinessUnit bu where bu.buyer.id = :tenantId and bu.unitCode = :unitName"
		 * ; final Query query = getEntityManager().createQuery(hql.toString()); query.setParameter("tenantId",
		 * tenantId); query.setParameter("unitName", unitCode); List<BusinessUnit> businessUnits =
		 * query.getResultList(); if (CollectionUtil.isNotEmpty(businessUnits)) { return Boolean.TRUE; } return
		 * Boolean.FALSE;
		 */}

	@SuppressWarnings("unchecked")
	@Override
	public List<BusinessUnit> getBusinessUnitForTenant(String tenantId) {
		String hql = "select new com.privasia.procurehere.core.entity.BusinessUnit(bu.id, bu.unitName, bu.unitCode, bu.displayName, parent.unitName, parent.unitCode, bu.status, bu.line1, bu.line2, bu.line3, bu.line4, bu.line5, bu.line6, bu.line7, bu.budgetCheck, bu.spmIntegration) from BusinessUnit bu left outer join bu.parent parent where bu.buyer.id = :tenantId";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BusinessUnit findByUnitCode(String tenantId, String unitCode) {
		StringBuilder hsql = new StringBuilder("select bu from BusinessUnit bu where bu.unitCode = :unitCode and bu.buyer.id = :tenantId");

		try {
			final Query query = getEntityManager().createQuery(hsql.toString());
			query.setParameter("unitCode", unitCode);
			query.setParameter("tenantId", tenantId);
			List<BusinessUnit> favList = query.getResultList();
			if (CollectionUtil.isNotEmpty(favList)) {
				return favList.get(0);
			} else {
				return null;
			}
		} catch (Exception nr) {
			LOG.info("Error while getting based on BusinessUnit code : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	public void updateBudgetCheckForChildRecords(BusinessUnit businessUnit) {
		String hql = "update BusinessUnit set budgetCheck = :budgetCheck where parent.id = :parent";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("budgetCheck", businessUnit.getBudgetCheck());
		query.setParameter("parent", businessUnit.getId());
		query.executeUpdate();
	}

	@Override
	public void updateSpmIntegrationForChildRecords(BusinessUnit businessUnit) {
		String hql = "update BusinessUnit set spmIntegration = :spmIntegration where parent.id = :parent";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("spmIntegration", businessUnit.getSpmIntegration());
		query.setParameter("parent", businessUnit.getId());
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BusinessUnitPojo> fetchAllActiveBusinessUnitForTenant(String tenantId, String search) {
		StringBuffer hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.BusinessUnitPojo(u.id, u.displayName, u.unitName, u.unitCode) from BusinessUnit u where u.status = :status and u.buyer.id =:tenantId ");
		if (StringUtils.checkString(search).length() > 0) {
			hql.append(" and (upper(u.displayName) like (:search) or upper(u.unitName) like (:search) or upper(u.unitCode) like (:search)) ");
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + StringUtils.checkString(search).toUpperCase() + "%");
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long countConstructQueryToFetchBusinessUnit(String tenantId) {
		StringBuffer hql = new StringBuffer("select count(u.id) from BusinessUnit u where u.buyer.id = :tenantId and u.status = :status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BusinessUnitPojo> getBusinessUnitIdByTenantId(String tenantId) {
		String hql = "select distinct new com.privasia.procurehere.core.pojo.BusinessUnitPojo(bu.id) from BusinessUnit bu where bu.buyer.id = :tenantId";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@Override
	public void removeAssignCostCenter(String businessUnitId, String costCenterId) {
		StringBuilder hsql = new StringBuilder("delete from PROC_BUSINESS_COST_MAPPING where BUSINESS_UNIT_ID= '" + businessUnitId + "' and COST_CENTER_ID= '" + costCenterId + "'");
		Query query = getEntityManager().createNativeQuery(hsql.toString());
		query.executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BusinessUnit> getBusinessUnitForCsv(String tenantId, int PAGE_SIZE, int pageNo) {
		String hql = "select new com.privasia.procurehere.core.entity.BusinessUnit(bu.id, bu.unitName, bu.unitCode, bu.displayName, parent.unitName, parent.unitCode, bu.status, bu.line1, bu.line2, bu.line3, bu.line4, bu.line5, bu.line6, bu.line7, bu.budgetCheck, bu.spmIntegration) from BusinessUnit bu left outer join bu.parent parent where bu.buyer.id = :tenantId";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((PAGE_SIZE * pageNo));
		query.setMaxResults(pageNo);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CostCenterPojo> getCostCentersByBusinessUnitId(String id, Status status) {
		String hql = "select distinct new com.privasia.procurehere.core.pojo.CostCenterPojo(cc.id,cc.costCenter,cc.description, cc.status) from BusinessUnit bu join bu.costCenter cc where bu.id = :id ";
		if (status != null) {
			hql += " and cc.status = :status ";
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		if (status != null) {
			query.setParameter("status", status);
		}
		return query.getResultList();
	}

	@Override
	public long getCountCostCentersByBusinessUnitId(String id, Status status) {
		String hql = "select size(bu.costCenter) from BusinessUnit bu join bu.costCenter cc where bu.id = :id ";
		if (status != null) {
			hql += " and cc.status = :status ";
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		if (status != null) {
			query.setParameter("status", status);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BusinessUnit> getBusinessForContractFromAwardDetails(String tenantId, BusinessUnit businessUnit) {
		Query query = getEntityManager().createQuery("select bu from BusinessUnit bu left outer join bu.parent pat where pat.id = :businessUnitId and bu.buyer.id = :tenantId");
		query.setParameter("businessUnitId", businessUnit.getId());		
		query.setParameter("tenantId", tenantId);
		return (List<BusinessUnit>) query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<BusinessUnit> getBusinessForContractFromAwardDetails(BusinessUnit businessUnit) {
		String hql = "select distinct new com.privasia.procurehere.core.entity.BusinessUnit(bu.id, bu.unitName, bu.displayName) from BusinessUnit bu where bu.parent.id = :id";
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", businessUnit.getId());
		return query.getResultList();
	}

	//4105
	@Override
	public List<String> getBusinessUnitIdByUserId(String userId) {
		String hql = "select DISTINCT business_unit_id FROM proc_user_business_unit_mapping where USER_ID = '" + userId + "'";
		Query query = getEntityManager().createNativeQuery(hql);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error("Error fetching business unit IDs for user: {}", userId, e);
			return Collections.emptyList(); // Return an empty list instead of null
		}
		}

}

