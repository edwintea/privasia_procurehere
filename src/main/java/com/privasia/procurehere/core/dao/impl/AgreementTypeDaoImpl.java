package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.AgreementTypeDao;
import com.privasia.procurehere.core.entity.AgreementType;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("agreementTypeDao")
public class AgreementTypeDaoImpl extends GenericDaoImpl<AgreementType, String> implements AgreementTypeDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<AgreementType> getAllActiveAgreementTypeForTenant(String tenantId) {
		String sql = "from AgreementType at left outer join fetch at.createdBy as cb left outer join fetch at.modifiedBy as mb where at.status='ACTIVE' and at.tenantId =:tenantId ";
		sql += " order by at.agreementType";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(AgreementType agreementType) {
		String sql = "from AgreementType at where upper(at.agreementType)= upper(:agreementType) and at.tenantId = :tenantId ";

		StringBuilder hsql = new StringBuilder(sql);
		if (StringUtils.checkString(agreementType.getId()).length() > 0) {
			hsql.append(" and at.id <> :id ");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("agreementType", agreementType.getAgreementType());
		query.setParameter("tenantId", agreementType.getTenantId());
		if (StringUtils.checkString(agreementType.getId()).length() > 0) {
			query.setParameter("id", agreementType.getId());
		}
		List<AgreementType> atList = query.getResultList();
		return CollectionUtil.isNotEmpty(atList);
	}

	@Override
	public AgreementType getAgreementTypebyCode(String agreementType, String tenantId) {
		try {
			final Query query = getEntityManager().createQuery("from AgreementType at where at.agreementType= :agreementType and at.tenantId =:tenantId");
			query.setParameter("tenantId", tenantId);
			query.setParameter("agreementType", agreementType);
			@SuppressWarnings("unchecked")
			List<AgreementType> atList = query.getResultList();
			if (CollectionUtil.isNotEmpty(atList)) {
				return atList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AgreementType> findAgreementTypeForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructAgreementTypeForTenantQuery(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredAgreementTypeForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructAgreementTypeForTenantQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalActiveAgreementTypeForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (at) from AgreementType at where at.status =:status and at.tenantId =:tenantId ");

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
	private Query constructAgreementTypeForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {
		LOG.info("TENANT ID........" + tenantId);

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(at) ";
		}

		hql += " from AgreementType at ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch at.createdBy as cb left outer join fetch at.modifiedBy as mb ";
		}
		
		hql += " where at.tenantId =:tenantId ";

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					hql += " and at.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(at." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and at.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " at." + orderColumn + " " + dir + ",";
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
				if (cp.getData().equals("status")) {
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
	@Transactional(readOnly = false)
	public void loadAgreementTypeMasterData(List<AgreementType> list, User createdBy) {
		for (AgreementType at : list) {
			if (!isExists(at)) {
				at.setCreatedBy(createdBy);
				at.setCreatedDate(new Date());
				at.setStatus(Status.ACTIVE);
				save(at);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public AgreementType getAgreementTypeByAgreementTypeAndTenantId(String agreementType, String tenantId) {
		try {
			final Query query = getEntityManager().createQuery("from AgreementType at where upper(at.agreementType) = :agreementType and at.tenantId = :tenantId");
			LOG.info("getAgreementTypeByAgreementTypeAndTenantId(agreementType,tenantId) called");
			query.setParameter("agreementType", agreementType.toUpperCase());
			query.setParameter("tenantId", tenantId);
			List<AgreementType> atList = query.getResultList();
			if (CollectionUtil.isNotEmpty(atList)) {
				return atList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AgreementType> getAllAgreementTypeForTenant(String loggedInUserTenantId) {
		String sql = "from AgreementType at left outer join fetch at.createdBy as cb left outer join fetch at.modifiedBy as mb where at.tenantId =:tenantId ";
		sql += " order by at.agreementType";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();
	}

	@Override
	public boolean isExists(String agreementType) {
		String sql = "select count(as) from AgreementType as at where upper(at.agreementType) = :agreementType";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("agreementType", agreementType);
		return (((Number) query.getSingleResult()).longValue() > 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AgreementType> fetchAllActiveAgreementTypeForTenant(String tenantId, String search) {
		StringBuffer hql = new StringBuffer("select distinct new com.privasia.procurehere.core.entity.AgreementType(at.id, at.agreementType, at.description) from AgreementType at where at.status = :status and at.tenantId =:tenantId");
		if (StringUtils.checkString(search).length() > 0) {
			hql.append(" and upper(at.agreementType) like (:search) ");
		}
		hql.append(" order by at.agreementType");
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
	public long countConstructQueryToFetchAgreementType(String tenantId) {
		StringBuffer hql = new StringBuffer("select count(u) from AgreementType at where at.tenantId = :tenantId and u.status = :status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AgreementType> findAllActiveAgreementTypeForTenantIdForCsv(String tenantId, int pAGE_SIZE, int pageNo, String[] eventArr, AgreementType agreementType, boolean select_all, Date startDate, Date endDate) {
		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.entity.AgreementType(at.id, at.agreementType, at.description, at.status) from AgreementType at where at.tenantId =:tenantId";

		if (!(select_all) || (eventArr != null && eventArr.length > 0)) {
			if (eventArr != null && eventArr.length > 0) {
				hql += "and at.id in (:eventIds)";
			}
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		if (agreementType != null) {
			if (StringUtils.checkString(agreementType.getAgreementType()).length() > 0) {
				query.setParameter("agreementType", "%" + agreementType.getAgreementType().toUpperCase() + "%");
			}
			if (StringUtils.checkString(agreementType.getDescription()).length() > 0) {
				query.setParameter("description", "%" + agreementType.getDescription().toUpperCase() + "%");
			}
			if (agreementType.getStatus() != null) {
				query.setParameter("status", "%" + agreementType.getAgreementType().toUpperCase() + "%");
			}
		}

		query.setParameter("tenantId", tenantId);

		if (!(select_all) || (eventArr != null && eventArr.length > 0)) {
			if (eventArr != null && eventArr.length > 0) {
				query.setParameter("eventIds", Arrays.asList(eventArr));
			}
		}

		LOG.info("HQL : " + hql);
		query.setFirstResult((pAGE_SIZE * pageNo));
		query.setMaxResults(pAGE_SIZE);

		return query.getResultList();
	}

	@Override
	public long findTotalAgreementTypeCountForCsv(String tenantId) {
		LOG.info("Tenant in DaoImpl for count........." + tenantId);
		String hql = "select count(at) from AgreementType at where at.tenantId = :tenantId";

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AgreementType> getAllAgreementTypeForCsv(String tenantId, int pAGE_SIZE, int pageNo) {
		String sql = "select at from AgreementType at left outer join fetch at.createdBy as cb left outer join fetch at.modifiedBy as mb";
		sql += " where at.tenantId = :tenantId order by at.agreementType";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pAGE_SIZE * pageNo));
		query.setMaxResults(pageNo);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AgreementType> fetchAllAgreementTypeForTenant(String tenantId, String searchVal) {
		String hql = "";
		hql += " select distinct new com.privasia.procurehere.core.entity.AgreementType(at.id, at.agreementType, at.description) from AgreementType at where at.status = :status and at.tenantId =:tenantId ";

		if (StringUtils.checkString(searchVal).length() > 0) {
			hql += "and upper(at.agreementType) like (:searchVal) ";
		}
		Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(searchVal).length() > 0) {
			query.setParameter("searchVal", searchVal);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		List<AgreementType> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@Override
	public long findTotalAgreementTypeForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder(" select count(at) from AgreementType as at where at.status =:status and at.tenantId = :tenantId");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AgreementType> getAllAgreementTypeIdByBusinessUnitId(String buId) {
		LOG.info("Business Unit Id : " + buId);
		String hql = " select distinct new com.privasia.procurehere.core.entity.AgreementType(agt.id, agt.agreementType, agt.description, agt.status) from BusinessUnit bu join bu.agreementType agt  where bu.id =:buId ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("buId", buId);

		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public long getCountOfInactiveAgreementType(String buId) {
		StringBuffer hql = new StringBuffer("select count(at) from AgreementType at where at.status = :status and at.id in (select bat.id from BusinessUnit bu join bu.agreementType bat  where bu.id =:buId)");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.INACTIVE);
		query.setParameter("buId", buId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AgreementType> fetchAllAgreementTypeForTenantForUnit(String tenantId, String searchVal, String buId) {
		String hql = "";
		if (StringUtils.checkString(buId).length() > 0) {
			hql += " select distinct new com.privasia.procurehere.core.entity.AgreementType(gc.id, gc.agreementType, gc.description, gc.status) from AgreementType gc where gc.status =:status and gc.tenantId =:tenantId and gc.id not in (select agc.id from BusinessUnit bu join bu.agreementType agc  where bu.id =:buId )";
		} else {
			hql += " select distinct new com.privasia.procurehere.core.entity.AgreementType(gc.id, gc.agreementType, gc.description, gc.status) from AgreementType gc where gc.status =:status and gc.tenantId =:tenantId ";
		}

		if (StringUtils.checkString(searchVal).length() > 0) {
			hql += "and upper(gc.agreementType) like (:searchVal) ";
		}
		Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(searchVal).length() > 0) {
			query.setParameter("searchVal", searchVal);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		if (StringUtils.checkString(buId).length() > 0) {
			query.setParameter("buId", buId);
		}

		List<AgreementType> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
		
	}
	
	@Override
	public long fetchFilterCountAllAgreementTypeForTenantForUnit(String tenantId, String buId) {
		String hql = "";
		if (StringUtils.checkString(buId).length() > 0) {
			hql += " select count(at) from AgreementType at where at.status =:status and at.tenantId =:tenantId and at.id not in (select agt.id from BusinessUnit bu join bu.agreementType agt  where bu.id =:buId)";
		} else {
			hql += " select count(at) from AgreementType at where at.status =:status and at.tenantId =:tenantId ";
		}

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		if (StringUtils.checkString(buId).length() > 0) {
			query.setParameter("buId", buId);
		}

		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AgreementType> findAgreementTypeListByTenantId(String tenantId, TableDataInput input, String id, String[] agreementTypeIds, String[] removeIds) {
		final Query query = constructAgreementTypeForTenantQueryForBusinessUnit(tenantId, input, id, false, agreementTypeIds, removeIds);
		if (query != null) {
			query.setFirstResult(input.getStart());
			query.setMaxResults(input.getLength());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public long findTotalFilteredAgreementTypeForTenant(String tenantId, TableDataInput input, String id, String[] agreementTypeIds, String[] removeIds) {
		final Query query = constructAgreementTypeForTenantQueryForBusinessUnit(tenantId, input, id, true, agreementTypeIds, removeIds);
		if (query != null) {
			return ((Number) query.getSingleResult()).longValue();
		} else {
			return 0;
		}
	}

	private Query constructAgreementTypeForTenantQueryForBusinessUnit(String tenantId, TableDataInput input, String id, boolean isCount, String[] agreementTypeIds, String[] removeIds) {

		String hql = "";
		if (isCount) {
			hql += "select count(distinct at.id) ";
		}
		if (!isCount) {
			hql += "select distinct NEW com.privasia.procurehere.core.entity.AgreementType(at.id, at.agreementType, at.description, at.status) from AgreementType at";
		} else {
			hql += " from AgreementType at ";
		}

		hql += " where at.tenantId = :tenantId and at.id in (select agt.id from BusinessUnit bu join bu.agreementType agt where bu.id = :id  ";
		if (removeIds != null && removeIds.length > 0) {
			hql += " and agt.id not in (:removeIds) ";
		}
		hql += " )";

		if (agreementTypeIds != null && agreementTypeIds.length > 0 && removeIds != null && removeIds.length > 0) {
			hql += " or (at.id in (:agreementTypeIds) and at.id not in (:removeIds)) ";
		}
		if (agreementTypeIds != null && agreementTypeIds.length > 0 && (removeIds == null || (removeIds != null && removeIds.length == 0))) {
			hql += " or (at.id in (:agreementTypeIds)) ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				hql += " and (upper(at.agreementType) like :" + cp.getData().replace(".", "") + " ";
				hql += " or upper(at.description) like :" + cp.getData().replace(".", "") + " )";
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String dir = order.getDir();
					hql += " at.agreementType" + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by at.agreementType";
			}
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);

		query.setParameter("id", id);
		if (removeIds != null && removeIds.length > 0) {
			query.setParameter("removeIds", Arrays.asList(removeIds));
		}
		if (agreementTypeIds != null && agreementTypeIds.length > 0) {
			query.setParameter("agreementTypeIds", Arrays.asList(agreementTypeIds));
		}
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}
		return query;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AgreementType> getAllActiveAgreementType(String tenantId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.AgreementType(at.id, at.agreementType, at.description) from AgreementType at where at.status = :status and at.tenantId = :tenantId");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

}
