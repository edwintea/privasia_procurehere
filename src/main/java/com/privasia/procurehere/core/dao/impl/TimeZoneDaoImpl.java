package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.CountryDao;
import com.privasia.procurehere.core.dao.TimeZoneDao;
import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("timeZoneDao")
public class TimeZoneDaoImpl extends GenericDaoImpl<TimeZone, String> implements TimeZoneDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	CountryDao countryDao;

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(TimeZone timeZone) {
		StringBuilder hsql = new StringBuilder("from TimeZone t where t.timeZone= :timeZone and t.timeZoneDescription = :timeZoneDescription");
		if (StringUtils.checkString(timeZone.getId()).length() > 0) {
			hsql.append(" and t.id <> :id order by t.timeZone");
		} else {
			hsql.append(" order by t.timeZone");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("timeZone", timeZone.getTimeZone());
		query.setParameter("timeZoneDescription", timeZone.getTimeZoneDescription());
		if (StringUtils.checkString(timeZone.getId()).length() > 0) {
			query.setParameter("id", timeZone.getId());
		}
		List<TimeZone> scList = query.getResultList();
		return CollectionUtil.isNotEmpty(scList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TimeZone> getAllTimeZone() {
		final Query query = getEntityManager().createQuery("from TimeZone as t left outer join fetch t.country as c left outer join fetch t.createdBy as cb left outer join fetch t.modifiedBy as mb where t.status =:status order by t.timeZone");
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false)
	public void loadTimeZoneMasterData(List<TimeZone> list, User createdBy) {
		for (TimeZone t : list) {
			if (!isExists(t)) {
				LOG.info("Loading timezone : " + t.toLogString());
				t.setStatus(Status.ACTIVE);
				t.setCreatedBy(createdBy);
				t.setCreatedDate(new Date());
				t.setCountry(t.getCountry());
				save(t);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TimeZone> findAllTimezones(TableDataInput tableParams) {
		final Query query = constructTimeZoneQuery(tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredTimeZones(TableDataInput tableParams) {
		final Query query = constructTimeZoneQuery(tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalTimeZones() {
		StringBuilder hql = new StringBuilder("select count (t) from TimeZone t where t.status =:status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tableParams
	 * @param isCount
	 * @return
	 */
	private Query constructTimeZoneQuery(TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(t) ";
		}

		hql += " from TimeZone t ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch t.country as c left outer join fetch t.createdBy as cb left outer join fetch t.modifiedBy as mb ";
		}
		hql += " where 1 = 1";

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					hql += " and t.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and t.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " t." + orderColumn + " " + dir + ",";
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
	public List<TimeZone> findTimeZonesForTenantId(String tenantId, String search) {
		StringBuffer hql = new StringBuffer("from TimeZone as t left outer join fetch t.country as c left outer join fetch t.createdBy as cb left outer join fetch t.modifiedBy as mb where t.status =:status");
		if (StringUtils.checkString(search).length() > 0) {
			hql.append(" and (upper(t.timeZone) like (:search) or upper(t.timeZoneDescription) like (:search))");
		}
		hql.append(" order by t.timeZone");

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + StringUtils.checkString(search).toUpperCase() + "%");
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long countConstructQueryToFetchTimeZones(String tenantId) {
		StringBuffer hql = new StringBuffer("select count(t) from TimeZone t where t.status = :status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).intValue();

	}

	@SuppressWarnings("unchecked")
	@Override
	public TimeZone fetchTimeZoneForCountry(String country) {
		StringBuffer hql = new StringBuffer("select t from TimeZone t where t.country.id = :country and t.status = :status");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("country", country);
		List<TimeZone> list = query.getResultList();

		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
