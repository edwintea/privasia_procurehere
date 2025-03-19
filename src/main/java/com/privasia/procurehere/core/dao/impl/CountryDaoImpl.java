package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.CountryDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
@Repository("countryDao")
public class CountryDaoImpl extends GenericDaoImpl<Country, String> implements CountryDao {

	private static final Logger LOG = LogManager.getLogger(CountryDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public List<Country> findAllActiveCountries() {
		final Query query = getEntityManager().createQuery("from Country a left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb where a.status =:status order by a.countryName");
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Country> searchCountiesByNameOrCode(String serachValue) {
		final Query query = getEntityManager().createQuery("from Country c left outer join fetch c.states as s where ((upper(c.countryName) like :countryName) or (upper(c.countryCode) like :countryCode)) and c.status =:status and s.status =:stateActive order by c.countryName");
		query.setParameter("countryName", "%" + serachValue.toUpperCase() + "%");
		query.setParameter("countryCode", "%" + serachValue.toUpperCase() + "%");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("stateActive", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(Country country) {
		StringBuilder hsql = new StringBuilder("from Country c where (upper(c.countryCode)= upper(:countryCode))");
		if (StringUtils.checkString(country.getId()).length() > 0) {
			hsql.append(" and c.id <> :id order by c.countryName");
		} else {
			hsql.append(" order by c.countryName");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("countryCode", country.getCountryCode());
		if (StringUtils.checkString(country.getId()).length() > 0) {
			query.setParameter("id", country.getId());
		}
		List<Country> scList = query.getResultList();
		return CollectionUtil.isNotEmpty(scList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Country> getAllCountries() {
		final Query query = getEntityManager().createQuery("from Country c left outer join fetch c.createdBy as cb left outer join fetch c.modifiedBy as mb where c.status =:status order by c.countryName");
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Country> getAllCountriesOrderByCountryCode() {
		final Query query = getEntityManager().createQuery("from Country c left outer join fetch c.createdBy as cb left outer join fetch c.modifiedBy as mb where c.status =:status order by c.countryCode");
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Country> loadById(String id) {
		final Query query = getEntityManager().createQuery("from Country c where c.id like :id order by c.countryName");
		query.setParameter("id", "%" + id + "%");
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false)
	public void loadCountryMasterData(List<Country> list, User createdBy) {
		for (Country c : list) {
			if (!isExists(c)) {
				// LOG.info("Loading country : " + c.getCountryCode() + " - " + c.getCountryName());
				c.setCreatedBy(createdBy);
				c.setCreatedDate(new Date());
				c.setStatus(Status.ACTIVE);
				save(c);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Country> findCountries(int start, int length, String order) {
		String[] sortValue = null;
		if (StringUtils.checkString(order).length() > 0 && StringUtils.checkString(order).contains(",")) {
			sortValue = order.split(",");
		}
		StringBuilder hql = new StringBuilder("from Country c left outer join fetch c.createdBy as cb left outer join fetch c.modifiedBy as mb where c.status =:status ");
		if (sortValue != null && sortValue.length >= 2) {
			hql.append(" order by c." + sortValue[0] + " " + sortValue[1]);
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);

		query.setFirstResult(start);
		query.setMaxResults(length);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Country> findAllCountryList(TableDataInput tableParams) {
		final Query query = constructCountryQuery(tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredCountryList(TableDataInput tableParams) {
		final Query query = constructCountryQuery(tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalCountryList() {
		StringBuilder hql = new StringBuilder("select count (c) from Country c where c.status =:status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructCountryQuery(TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(c) ";
		}

		hql += " from Country c ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch c.createdBy as cb left outer join fetch c.modifiedBy as mb ";
		}
		hql += " where 1 = 1";

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
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
	@SuppressWarnings("unchecked")
	public Country getCountryWithStatesByCode(String code) {
		final Query query = getEntityManager().createQuery("Select distinct c from Country c left outer join fetch c.states as s where c.status =:status and upper(c.countryCode) = :code ");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("code", code.toUpperCase());
		List<Country> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	
	@Override
	@SuppressWarnings("unchecked")
	public List<Country> getActiveCountries(){
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.Country(c.id,c.countryCode, c.countryName, c.status) from Country c where c.status =:status ");
		query.setParameter("status", Status.ACTIVE);
		List<Country> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}
}
