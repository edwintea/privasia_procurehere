package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.UserRoleDao;
import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Ravi
 */
@Repository("userRole")
public class UserRoleDaoImpl extends GenericDaoImpl<UserRole, String> implements UserRoleDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExist(UserRole userRole, String tenantId) {
		StringBuilder hsql = new StringBuilder("from UserRole u where u.roleName= :roleName and u.tenantId= :tenantId");
		if (StringUtils.checkString(userRole.getId()).length() > 0) {
			hsql.append(" and u.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("roleName", userRole.getRoleName());
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(userRole.getId()).length() > 0) {
			query.setParameter("id", userRole.getId());
		}
		List<UserRole> scList = query.getResultList();
		LOG.info("isExistDaoImpl Called");
		return CollectionUtil.isNotEmpty(scList);
	}

	@Override
	public UserRole loadById(String id) {
		final Query query = getEntityManager().createQuery("from UserRole a where a.id =:id");
		query.setParameter("id", id);
		UserRole userRole = (UserRole) query.getSingleResult();
		for (AccessRights al : userRole.getAccessControlList()) {
			al.getAclName();
		}

		return userRole;
	}

	// @Override
	// public void delete(UserRole userRole) {
	// super.update(userRole);
	// }

	@Override
	public UserRole save(UserRole role) {
		role.setRoleName(role.getRoleName().toUpperCase());

		UserRole dbRole = findByUserRoleAndTenantId(role.getRoleName(), role.getTenantId());
		if (dbRole != null) {
			ConstraintViolationException cause = new ConstraintViolationException("Active Role already exists by Role Name : " + role.getRoleName() + " and Tenant Id : " + role.getTenantId(), null, "Unique Constraint");
			throw new DataIntegrityViolationException("Duplicate Entry", cause);
		}
		return super.save(role);
	}

	@Override
	public UserRole findByUserRoleAndTenantId(String value, String tenantId) {
		try {
			final Query query = getEntityManager().createQuery("from UserRole a where a.roleName =:roleName and a.tenantId =:tenantId and a.deleted = false ");
			query.setParameter("roleName", value);
			query.setParameter("tenantId", tenantId);
			return (UserRole) query.getSingleResult();
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRole> findAllActiveRoles(String tenantId) {
		LOG.info("find to user Role");
		try {
			final Query query = getEntityManager().createQuery("from UserRole ur left outer join fetch ur.createdBy as cb left outer join fetch ur.modifiedBy as mb where ur.deleted = false and ur.tenantId = :tenantId and ur.status = :status");
			query.setParameter("tenantId", tenantId);
			query.setParameter("status", Status.ACTIVE);
			return query.getResultList();
		} catch (NoResultException nr) {
			LOG.info("Error while getting " + " userrole : " + nr.getMessage());
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRole> findAllUserRoleListForTenant(TableDataInput tableParams, String tenantId) {
		final Query query = constructCountryQuery(tableParams, tenantId, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredUserRoleListForTenant(TableDataInput tableParams, String tenantId) {
		final Query query = constructCountryQuery(tableParams, tenantId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalUserRoleListForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (ur) from UserRole ur where ur.tenantId =:tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructCountryQuery(TableDataInput tableParams, String tenantId, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(ur) ";
		}

		hql += " from UserRole ur ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch ur.createdBy as cb left outer join fetch ur.modifiedBy as mb ";
		}
		hql += " where ur.tenantId = :tenantId ";

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					hql += " and ur.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdByName")) {
					LOG.info(cp.getData());
					hql += " and upper(ur.createdBy.name) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedByName")) {
					LOG.info(cp.getData());
					LOG.info(cp.getSearch().getValue());
					hql += " and upper(ur.modifiedBy.name) like (:" + cp.getData() + ")";
				} else {
					hql += " and upper(ur." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and ur.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equals("createdByName")) {
						hql += " ur.createdBy.name " + dir + ",";
					} else if (orderColumn.equals("modifiedByName")) {
						hql += " ur.modifiedBy.name " + dir + ",";
					} else {
						hql += " ur." + orderColumn + " " + dir + ",";
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
				LOG.info(cp.getSearch().getValue() + " " + cp.getData());

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

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRole> findAllUserRolesForTenant(String tenantId) {
		LOG.info("find to user Role");
		try {
			final Query query = getEntityManager().createQuery("from UserRole ur left outer join fetch ur.accessControlList as ar where ur.tenantId = :tenantId");
			query.setParameter("tenantId", tenantId);
			return query.getResultList();
		} catch (NoResultException nr) {
			LOG.info("Error while getting " + " userrole : " + nr.getMessage());
			return null;
		}

	}
}
