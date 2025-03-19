/**
 * 
 */
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
import com.privasia.procurehere.core.dao.StateDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author jav3d
 */
@Repository
public class StateDaoImpl extends GenericDaoImpl<State, String> implements StateDao {

	private static final Logger LOG = LogManager.getLogger(StateDaoImpl.class);

	@Autowired
	CountryDao countryDao;

	@Override
	public void updateState(State state) {
		getSession().update(state);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<State> findAll() {
		StringBuilder hsql = new StringBuilder("from State  as s inner join fetch s.country as c order by s.stateCode  ");
		final Query query = getEntityManager().createQuery(hsql.toString());

		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(State state) {
		StringBuilder hsql = new StringBuilder("from State as s where s.stateCode = :stateCode  ");
		if (StringUtils.checkString(state.getId()).length() > 0) {
			hsql.append(" and s.id <> :id");
		}

		if (StringUtils.checkString(state.getCountry().getId()).length() > 0) {
			hsql.append(" and s.country.id = :countryId ");
		}

		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("stateCode", state.getStateCode());

		if (StringUtils.checkString(state.getId()).length() > 0) {
			query.setParameter("id", state.getId());
		}

		if (StringUtils.checkString(state.getCountry().getId()).length() > 0) {
			query.setParameter("countryId", state.getCountry().getId());
		}

		List<State> stList = query.getResultList();
		return CollectionUtil.isNotEmpty(stList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<State> statesForCountry(String countryId) {
		StringBuilder hsql = new StringBuilder("from State  as s inner join fetch s.country as c where c.id =:countryId  and s.status =:status order by s.stateCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("countryId", countryId);
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<State> getAllStatesPojo() {
		StringBuilder hsql = new StringBuilder("from State  as s inner join fetch s.country as c left outer join fetch s.createdBy as cb left outer join fetch s.modifiedBy as mb order by s.stateCode  ");
		final Query query = getEntityManager().createQuery(hsql.toString());

		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false)
	public void loadStateMasterData(List<State> list, User createdBy) {
		for (State s : list) {
			if (!isExists(s)) {
				LOG.info("Loading State : " + s.getStateCode() + " - " + s.getStateName() + " For Country : " + s.getCountry().getCountryCode());
				s.setCreatedBy(createdBy);
				s.setCreatedDate(new Date());
				s.setStatus(Status.ACTIVE);

				Country c = countryDao.findByProperty("countryCode", s.getCountry().getCountryCode());
				if (c == null) {
					LOG.warn("Cannot load state : " + s.getStateCode() + " - " + s.getStateName() + ", due to country not found - " + s.getCountry().getCountryCode());
				} else {
					s.setCountry(c);
					save(s);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false)
	public List<State> searchStatesByNameOrCode(String searchValue) {
		final Query query = getEntityManager().createQuery("from State s inner join fetch s.country as c where ((upper(s.stateName) like :stateName) or (upper(s.stateCode) like :stateCode)) and s.status=:status");
		query.setParameter("stateName", "%" + searchValue.toUpperCase() + "%");
		query.setParameter("stateCode", "%" + searchValue.toUpperCase() + "%");
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<State> findAllStateList(TableDataInput tableParams) {
		final Query query = constructStateQuery(tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredStateList(TableDataInput tableParams) {
		final Query query = constructStateQuery(tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalStateList() {
		StringBuilder hql = new StringBuilder("select count (s) from State s where s.status =:status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tableParams
	 * @param isCount
	 * @return
	 */
	private Query constructStateQuery(TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(s) ";
		}

		hql += " from State s ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch s.country as c left outer join fetch s.createdBy as cb left outer join fetch s.modifiedBy as mb ";
		}
		hql += " where 1 = 1";

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					hql += " and s.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(s." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and s.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " s." + orderColumn + " " + dir + ",";
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
	public List<State> findAllActiveStatesForCountry(String countryId) {
		final Query query = getEntityManager().createQuery("from State  as s inner join fetch s.country as c where c.id =:countryId and s.status = :status");
		query.setParameter("countryId", countryId);
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();

	}
}
