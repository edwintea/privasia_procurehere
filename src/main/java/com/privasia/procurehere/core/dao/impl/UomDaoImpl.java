package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.UomDao;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UomPojo;
import com.privasia.procurehere.core.supplier.dao.OwnerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("uomDao")
public class UomDaoImpl extends GenericDaoImpl<Uom, String> implements UomDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<Uom> getAllActiveUomForTenant(String tenantId, TenantType tenantType) {
		String sql = "from Uom c left outer join fetch c.createdBy as cb left outer join fetch c.modifiedBy as mb where c.status='ACTIVE' ";
		if (tenantType == TenantType.BUYER) {
			sql += " and c.buyer.id =:tenantId ";
		} else if (tenantType == TenantType.OWNER) {
			sql += " and c.owner.id =:tenantId ";
		}
		sql += " order by c.uom";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(Uom uom, TenantType tenantType) {
		String sql = "from Uom c where upper(c.uom)= upper(:uom) ";
		if (tenantType == TenantType.BUYER) {
			sql += " and c.buyer.id = :tenantId ";
		} else if (tenantType == TenantType.OWNER) {
			sql += " and c.owner.id = :tenantId ";
		}
		StringBuilder hsql = new StringBuilder(sql);
		if (StringUtils.checkString(uom.getId()).length() > 0) {
			hsql.append(" and c.id <> :id ");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("uom", uom.getUom());
		if (tenantType == TenantType.BUYER) {
			query.setParameter("tenantId", uom.getBuyer().getId());
		} else if (tenantType == TenantType.OWNER) {
			query.setParameter("tenantId", uom.getOwner().getId());
		}
		if (StringUtils.checkString(uom.getId()).length() > 0) {
			query.setParameter("id", uom.getId());
		}
		List<Uom> umList = query.getResultList();
		return CollectionUtil.isNotEmpty(umList);
	}

	@Override
	public Uom getUombyCode(String uom, String tenantId) {
		try {
			final Query query = getEntityManager().createQuery("from Uom c where c.uom= :uom and c.buyer.id =:tenantId");
			query.setParameter("tenantId", tenantId);
			query.setParameter("uom", uom);
			@SuppressWarnings("unchecked")
			List<Uom> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Uom> findUomForTenant(String tenantId, TableDataInput tableParams, TenantType tenantType) {
		final Query query = constructUomForTenantQuery(tenantId, tableParams, false, tenantType);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredUomForTenant(String tenantId, TableDataInput tableParams, TenantType tenantType) {
		final Query query = constructUomForTenantQuery(tenantId, tableParams, true, tenantType);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalActiveUomForTenant(String tenantId, TenantType tenantType) {
		StringBuilder hql = new StringBuilder("select count (u) from Uom u where u.status =:status ");

		if (tenantType == TenantType.BUYER) {
			hql.append(" and u.buyer.id =:tenantId ");
		} else if (tenantType == TenantType.OWNER) {
			hql.append(" and u.owner.id =:tenantId ");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param isCount
	 * @param tenantType TODO
	 * @return
	 */
	private Query constructUomForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount, TenantType tenantType) {
		LOG.info("TENANT ID........" + tenantId);

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(u) ";
		}

		hql += " from Uom u ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch u.createdBy as cb left outer join fetch u.modifiedBy as mb ";
		}
		if (tenantType == TenantType.BUYER) {
			hql += " where u.buyer.id = :tenantId ";
		} else if (tenantType == TenantType.OWNER) {
			hql += " where u.owner.id = :tenantId ";
		}

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					hql += " and u.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(u." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and u.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " u." + orderColumn + " " + dir + ",";
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

	@Autowired
	OwnerDao ownerDao;

	@Override
	@Transactional(readOnly = false)
	public void loadUomMasterData(List<Uom> list, User createdBy) {
		for (Uom c : list) {
			if (!isExists(c)) {
				c.setCreatedBy(createdBy);
				c.setCreatedDate(new Date());
				c.setStatus(Status.ACTIVE);
				c.setOwner(ownerDao.getDefaultOwner());
				save(c);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(Uom uom) {
		StringBuilder hsql = new StringBuilder("from Uom u where u.uom= :uom");
		if (StringUtils.checkString(uom.getId()).length() > 0) {
			hsql.append(" and u.id <> :id order by u.uom");
		} else {
			hsql.append(" order by u.uom");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("uom", uom.getUom());
		if (StringUtils.checkString(uom.getId()).length() > 0) {
			query.setParameter("id", uom.getId());
		}
		List<Uom> scList = query.getResultList();
		return CollectionUtil.isNotEmpty(scList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Uom getUomByUomAndTenantId(String uom, String tenantId) {
		try {
			final Query query = getEntityManager().createQuery("from Uom c where upper(c.uom) = :uom and c.buyer.id = :tenantId");
			LOG.info("getUombyPcodeandTenantId(uom,tenantId) called");
			query.setParameter("uom", uom.toUpperCase());
			query.setParameter("tenantId", tenantId);
			List<Uom> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Uom> getAllUomForTenant(String loggedInUserTenantId, TenantType tenantType) {
		// TODO Auto-generated method stub
		String sql = "from Uom c left outer join fetch c.createdBy as cb left outer join fetch c.modifiedBy as mb where ";
		if (tenantType == TenantType.BUYER) {
			sql += "c.buyer.id =:tenantId ";
		} else {
			sql += "c.owner.id =:tenantId ";
		}
		sql += " order by c.uom";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();
	}

	@Override
	public boolean isExists(String uom, String buyerId) {
		String sql = "select count(as) from Uom as um inner join um.buyer as b where upper(um.uom) = :uom and b.id = :id";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("uom", uom);
		query.setParameter("id", buyerId);
		return (((Number) query.getSingleResult()).longValue() > 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UomPojo> fetchAllActiveUomForTenant(String tenantId, String search) {
		StringBuffer hql = new StringBuffer("select distinct new com.privasia.procurehere.core.pojo.UomPojo(u.id, u.uom, u.uomDescription) from Uom u where u.status = :status and u.buyer.id =:tenantId");
		if (StringUtils.checkString(search).length() > 0) {
			hql.append(" and upper(u.uom) like (:search) ");
		}
		hql.append(" order by u.uom");
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
	public long countConstructQueryToFetchUom(String tenantId) {
		StringBuffer hql = new StringBuffer("select count(u) from Uom u where u.buyer.id = :tenantId and u.status = :status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UomPojo> findAllActiveUomForTenantIdForCsv(String tenantId, TenantType tenantType, int pAGE_SIZE, int pageNo, String[] eventArr, UomPojo uomPojo, boolean select_all, Date startDate, Date endDate) {
		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.UomPojo(u.id, u.uom, u.uomDescription, u.status) from Uom u ";

		if (tenantType == TenantType.BUYER) {
			hql += "where u.buyer.id =:tenantId ";
		} else if (tenantType == TenantType.OWNER) {
			hql += "where u.owner.id =:tenantId ";
		}

		if (!(select_all) || (eventArr != null && eventArr.length > 0)) {
			if (eventArr != null && eventArr.length > 0) {
				hql += "and u.id in (:eventIds)";
			}
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		if (uomPojo != null) {
			if (StringUtils.checkString(uomPojo.getUom()).length() > 0) {
				query.setParameter("uom", "%" + uomPojo.getUom().toUpperCase() + "%");
			}
			if (StringUtils.checkString(uomPojo.getUomDescription()).length() > 0) {
				query.setParameter("uomDescription", "%" + uomPojo.getUomDescription().toUpperCase() + "%");
			}
			if (uomPojo.getStatus() != null) {
				query.setParameter("status", "%" + uomPojo.getUom().toUpperCase() + "%");
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
	public long findTotalUomCountForCsv(String tenantId, TenantType tenantType) {
		LOG.info("Tenant in DaoImpl for count........." + tenantId);
		String hql = "select count(u) from Uom u ";

		if (tenantType == TenantType.BUYER) {
			hql += "where u.buyer.id = :tenantId ";
		} else if (tenantType == TenantType.OWNER) {
			hql += " where u.owner.id = :tenantId ";
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Uom> getAllUomForCsv(String tenantId, TenantType tenantType, int pAGE_SIZE, int pageNo) {
		String sql = "from Uom c left outer join fetch c.createdBy as cb left outer join fetch c.modifiedBy as mb where c.status='ACTIVE' ";
		if (tenantType == TenantType.BUYER) {
			sql += " and c.buyer.id =:tenantId ";
		} else if (tenantType == TenantType.OWNER) {
			sql += " and c.owner.id =:tenantId ";
		}
		sql += " order by c.uom";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pAGE_SIZE * pageNo));
		query.setMaxResults(pageNo);
		return query.getResultList();
	}
}
