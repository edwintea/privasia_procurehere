package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class CurrencyDaoImpl extends GenericDaoImpl<Currency, String> implements CurrencyDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@SuppressWarnings("unchecked")
	public List<Currency> getAllCurrencies() {
		final Query query = getEntityManager().createQuery("from Currency c left outer join fetch c.createdBy as cb left outer join fetch c.modifiedBy as mb order by c.currencyName");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public boolean isExists(Currency currency) {
		StringBuilder hsql = new StringBuilder("from Currency as c where c.currencyCode = :currencyCode");
		if (StringUtils.checkString(currency.getId()).length() > 0) {
			hsql.append(" and c.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("currencyCode", currency.getCurrencyCode());
		if (StringUtils.checkString(currency.getId()).length() > 0) {
			query.setParameter("id", currency.getId());
		}
		List<State> stList = query.getResultList();
		return CollectionUtil.isNotEmpty(stList);
	}

	@Override
	@Transactional(readOnly = false)
	public void loadCurrencyMasterData(List<Currency> list, User createdBy) {
		for (Currency cur : list) {
			if (!isExists(cur)) {
				LOG.info("Loading Currency : " + cur.toLogString());
				cur.setStatus(Status.ACTIVE);
				cur.setCreatedBy(createdBy);
				cur.setCreatedDate(new Date());
				save(cur);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Currency> findCurrency(int start, int length, String order) {
		String[] sortValue = null;
		if (StringUtils.checkString(order).length() > 0 && StringUtils.checkString(order).contains(",")) {
			sortValue = order.split(",");
		}
		StringBuilder hql = new StringBuilder("from Currency c left outer join fetch c.createdBy as cb left outer join fetch c.modifiedBy as mb where c.status =:status ");
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
	public List<Currency> findAllCurrencyList(TableDataInput tableParams) {
		final Query query = constructBaseCurrencyQuery(tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredCurrencyList(TableDataInput tableParams) {
		final Query query = constructBaseCurrencyQuery(tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalCurrencyList() {
		StringBuilder hql = new StringBuilder("select count (c) from Currency c where c.status =:status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tableParams
	 * @param isCount
	 * @return
	 */
	private Query constructBaseCurrencyQuery(TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(c) ";
		}

		hql += " from Currency c ";

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

	@SuppressWarnings("unchecked")
	public List<Currency> getAllActiveCurrencies() {
		final Query query = getEntityManager().createQuery("from Currency c where c.status = :status order by c.currencyName");
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Currency> getAllActiveCurrenciesForMobileApi() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.Currency(c.currencyCode , c.currencyName, c.status ) from Currency c where c.status = :status");
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Currency findByCurrencyCode(String currCode) {
		StringBuilder hsql = new StringBuilder("from Currency as c where upper(c.currencyCode) = :currencyCode");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("currencyCode", currCode.toUpperCase());
		List<Currency> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public List<Currency> getAllActiveCurrencyCode() {
		final Query query = getEntityManager().createQuery("from Currency c where c.status = :status order by c.currencyCode");
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Currency> getlActiveCurrencies() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.Currency(c.id, c.currencyCode , c.currencyName, c.status ) from Currency c where c.status = :status  order by c.currencyCode ");
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Currency> fetchAllActiveCurrencies(String search) {
		StringBuffer hql = new StringBuffer("from Currency as t left outer join fetch t.createdBy as cb left outer join fetch t.modifiedBy as mb where t.status =:status");
		if (StringUtils.checkString(search).length() > 0) {
			hql.append(" and upper(t.currencyName) like (:search) ");
		}
		hql.append(" order by t.currencyName");

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + StringUtils.checkString(search).toUpperCase() + "%");
		}
		query.setMaxResults(10);
		return query.getResultList();
	}

	@Override
	public long countConstructQueryToFetchCurrencies() {
		StringBuffer hql = new StringBuffer("select count(t) from Currency t where t.status = :status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).intValue();
	}
}
