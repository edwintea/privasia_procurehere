/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.GroupCodeDao;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author jayshree
 */
@Repository("groupCodeDao")
public class GroupCodeDaoImpl extends GenericDaoImpl<GroupCode, String> implements GroupCodeDao {

	private static final Logger LOG = LogManager.getLogger(GroupCodeDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(GroupCode groupCode, String tenantId) {

		StringBuilder hsql = new StringBuilder("from GroupCode as cc where upper(cc.groupCode) = upper(:code) and cc.buyer.id = :tenantId ");
		if (StringUtils.checkString(groupCode.getId()).length() > 0) {
			hsql.append(" and cc.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("code", groupCode.getGroupCode());
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(groupCode.getId()).length() > 0) {
			query.setParameter("id", groupCode.getId());
		}
		List<GroupCode> stList = query.getResultList();
		return CollectionUtil.isNotEmpty(stList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupCode> findGroupCodesForTenant(TableDataInput tableParams, String tenantId) {
		Query query = constructGroupCodeQueryForTenantId(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredGroupCodesForTenant(TableDataInput tableParams, String tenantId) {
		final Query query = constructGroupCodeQueryForTenantId(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructGroupCodeQueryForTenantId(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(c) ";
		}

		hql += " from GroupCode c ";

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

		LOG.info("HQL : " + hql);
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

		if (!isStatusFilterOn) {
			query.setParameter("status", Status.ACTIVE);
		}
		return query;
	}

	@Override
	public long findTotalGroupCodesForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder(" select count(gc) from GroupCode as gc where gc.status =:status and gc.buyer.id =:tenantId ");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findCountOfGroupCodesForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (gc) from GroupCode gc where gc.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupCode> findGroupCodeListForTenantIdForCsv(String tenantId, int pageSize, int pageNo) {
		final Query query = getEntityManager().createQuery("from GroupCode gc where gc.buyer.id = :tenantId order by gc.groupCode");
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);

		List<GroupCode> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public GroupCode getByGroupCode(String groupCode, String tenantId) {
		final Query query = getEntityManager().createQuery("from GroupCode gc where gc.buyer.id = :tenantId and upper(gc.groupCode) = :groupCode");
		query.setParameter("tenantId", tenantId);
		query.setParameter("groupCode", groupCode);

		List<GroupCode> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupCode> fetchAllGroupCodesForTenant(String tenantId, String searchVal) {
		String hql = "";
		hql += " select distinct new com.privasia.procurehere.core.entity.GroupCode(gc.id, gc.groupCode, gc.description) from GroupCode gc where gc.status = :status and gc.buyer.id =:tenantId ";

		if (StringUtils.checkString(searchVal).length() > 0) {
			hql += "and upper(gc.groupCode) like (:searchVal) ";
		}
		Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(searchVal).length() > 0) {
			query.setParameter("searchVal", searchVal);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		List<GroupCode> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupCode> getGroupCodeIdByBusinessUnitId(String buId) {
		LOG.info("Business Unit Id : " + buId);
		String hql = " select distinct new com.privasia.procurehere.core.entity.GroupCode(agc.id, agc.groupCode, agc.description, agc.status) from BusinessUnit bu join bu.assignGroupCodes agc  where agc.status =:status and bu.id =:buId ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("buId", buId);
		query.setParameter("status", Status.ACTIVE);

		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupCode> getAllGroupCodeIdByBusinessUnitId(String buId) {
		LOG.info("Business Unit Id : " + buId);
		String hql = " select distinct new com.privasia.procurehere.core.entity.GroupCode(agc.id, agc.groupCode, agc.description, agc.status) from BusinessUnit bu join bu.assignGroupCodes agc  where bu.id =:buId ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("buId", buId);

		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupCode> fetchAllCostCenterForTenantForUnit(String tenantId, String searchVal, String buId) {
		String hql = "";
		if (StringUtils.checkString(buId).length() > 0) {
			hql += " select distinct new com.privasia.procurehere.core.entity.GroupCode(gc.id, gc.groupCode, gc.description, gc.status) from GroupCode gc where gc.status =:status and gc.buyer.id =:tenantId and gc.id not in (select agc.id from BusinessUnit bu join bu.assignGroupCodes agc  where bu.id =:buId )";
		} else {
			hql += " select distinct new com.privasia.procurehere.core.entity.GroupCode(gc.id, gc.groupCode, gc.description, gc.status) from GroupCode gc where gc.status =:status and gc.buyer.id =:tenantId ";
		}

		if (StringUtils.checkString(searchVal).length() > 0) {
			hql += "and upper(gc.groupCode) like (:searchVal) ";
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

		List<GroupCode> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@Override
	public long fetchFilterCountAllCostForTenantForUnit(String tenantId, String buId) {
		String hql = "";
		if (StringUtils.checkString(buId).length() > 0) {
			hql += " select count(gc) from GroupCode gc where gc.status =:status and gc.buyer.id =:tenantId and gc.id not in (select agc.id from BusinessUnit bu join bu.assignGroupCodes agc  where bu.id =:buId)";
		} else {
			hql += " select count(gc) from GroupCode gc where gc.status =:status and gc.buyer.id =:tenantId ";
		}

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		if (StringUtils.checkString(buId).length() > 0) {
			query.setParameter("buId", buId);
		}

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public long getCountOfInactiveGroupCode(String buId) {
		StringBuffer hql = new StringBuffer("select count(cc) from GroupCode cc where cc.status = :status and cc.id in (select agc.id from BusinessUnit bu join bu.assignGroupCodes agc  where bu.id =:buId)");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.INACTIVE);
		query.setParameter("buId", buId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupCode> findGroupCodeListByTenantId(String tenantId, TableDataInput input, String id, String[] groupCodeIds, String[] removeIds) {
		final Query query = constructGroupCodeForTenantQueryForBusinessUnit(tenantId, input, id, false, groupCodeIds, removeIds);
		if (query != null) {
			query.setFirstResult(input.getStart());
			query.setMaxResults(input.getLength());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public long findTotalFilteredGroupCodeForTenant(String tenantId, TableDataInput input, String id, String[] groupCodeIds, String[] removeIds) {
		final Query query = constructGroupCodeForTenantQueryForBusinessUnit(tenantId, input, id, true, groupCodeIds, removeIds);
		if (query != null) {
			return ((Number) query.getSingleResult()).longValue();
		} else {
			return 0;
		}
	}

	private Query constructGroupCodeForTenantQueryForBusinessUnit(String tenantId, TableDataInput input, String id, boolean isCount, String[] groupCodeIds, String[] removeIds) {

		String hql = "";
		if (isCount) {
			hql += "select count(distinct gc.id) ";
		}
		if (!isCount) {
			hql += "select distinct NEW com.privasia.procurehere.core.entity.GroupCode(gc.id, gc.groupCode, gc.description, gc.status) from GroupCode gc";
		} else {
			hql += " from GroupCode gc ";
		}

		hql += " where gc.buyer.id = :tenantId and gc.id in (select agc.id from BusinessUnit bu join bu.assignGroupCodes agc where bu.id = :id  ";
		if (removeIds != null && removeIds.length > 0) {
			hql += " and agc.id not in (:removeIds) ";
		}
		hql += " )";

		if (groupCodeIds != null && groupCodeIds.length > 0 && removeIds != null && removeIds.length > 0) {
			hql += " or (gc.id in (:groupCodeIds) and gc.id not in (:removeIds)) ";
		}
		if (groupCodeIds != null && groupCodeIds.length > 0 && (removeIds == null || (removeIds != null && removeIds.length == 0))) {
			hql += " or (gc.id in (:groupCodeIds)) ";
		}

		// hql += " where gc.buyer.id = :tenantId ";
		// if (CollectionUtil.isNotEmpty(groupCodeId)) {
		// hql += " and gc.id in (:assignedGroupCodeIds)";
		// }

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				hql += " and (upper(gc.groupCode) like :" + cp.getData().replace(".", "") + " ";
				hql += " or upper(gc.description) like :" + cp.getData().replace(".", "") + " )";
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					// String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " gc.groupCode" + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by gc.groupCode";
			}
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		// query.setParameter("buId", id);
		query.setParameter("tenantId", tenantId);
		// if (CollectionUtil.isNotEmpty(groupCodeId)) {
		// query.setParameter("assignedGroupCodeIds", groupCodeId);
		// }

		query.setParameter("id", id);
		if (removeIds != null && removeIds.length > 0) {
			query.setParameter("removeIds", Arrays.asList(removeIds));
		}
		if (groupCodeIds != null && groupCodeIds.length > 0) {
			query.setParameter("groupCodeIds", Arrays.asList(groupCodeIds));
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
	public List<GroupCode> fetchAllActiveGroupCodeForTenantID(String tenantId) {
		String hql = "";
		hql += " select distinct new com.privasia.procurehere.core.entity.GroupCode(gc.id, gc.groupCode, gc.description, gc.status) from GroupCode gc where gc.status =:status and gc.buyer.id =:tenantId order by gc.groupCode ";

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);

		List<GroupCode> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getGroupCodeByBusinessId(String buId) {
		String hql = "select DISTINCT GROUP_CODE_ID FROM PROC_BUSNS_GROUP_CD_MAPPING where BUSINESS_UNIT_ID = :buId ";
		Query query = getEntityManager().createNativeQuery(hql);
		query.setParameter("buId", buId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public GroupCode getGroupCodeById(String gcId) {
		String hql = "";
		hql += " select distinct new com.privasia.procurehere.core.entity.GroupCode(gc.id, gc.groupCode, gc.description, gc.status) from GroupCode gc where gc.id =:id order by gc.groupCode ";

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", gcId);

		List<GroupCode> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GroupCode> getGroupCodedByIds(List<String> gcIds) {
		String hql = "";
		hql += " select distinct new com.privasia.procurehere.core.entity.GroupCode(gc.id, gc.groupCode, gc.description, gc.status) from GroupCode gc where gc.id  in (:gcIds) order by gc.groupCode ";
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("gcIds", gcIds);
		List<GroupCode> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}
}
