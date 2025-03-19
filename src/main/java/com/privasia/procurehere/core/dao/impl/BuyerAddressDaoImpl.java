package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.BuyerAddressDao;
import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Vipul
 */

@Repository
public class BuyerAddressDaoImpl extends GenericDaoImpl<BuyerAddress, String> implements BuyerAddressDao {

	private static final Logger LOG = LogManager.getLogger(BuyerAddressDaoImpl.class);

	@Override
	public void updateBuyerAddress(BuyerAddress buyerAddress) {
		getSession().update(buyerAddress);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BuyerAddress> findAllAddressesForTenant(String tenantId) {
		StringBuilder hsql = new StringBuilder("from BuyerAddress b inner join fetch b.state as s inner join fetch s.country c where b.buyer.id = :buyerId and b.status = :status");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("buyerId", tenantId);
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BuyerAddress> findBuyerAddressForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructBuyerAddressForTenantQuery(tenantId, tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredBuyerAddressForTenant(String tenantId, TableDataInput tableParams) {
		final Query query = constructBuyerAddressForTenantQuery(tenantId, tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalBuyerAddressForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (b) from BuyerAddress b where  b.status = :status and b.buyer.id = :tenantId ");
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
	private Query constructBuyerAddressForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(u) ";
		}

		hql += " from BuyerAddress u ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " inner join fetch u.state as s inner join fetch s.country c left outer join fetch u.createdBy as cb left outer join fetch u.modifiedBy as mb ";
		}

		hql += " where u.buyer.id = :tenantId ";

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

	@Override
	public boolean isExists(BuyerAddress buyerAddress, String tenantId) {
		final Query query = getEntityManager().createQuery("from BuyerAddress a where upper(a.title) = upper(:title) and a.buyer.id = :id and a.status = :status");
		query.setParameter("title", buyerAddress.getTitle());
		query.setParameter("id", tenantId);
		query.setParameter("status", buyerAddress.getStatus());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BuyerAddress> getBuyerAddressForTenant(String loggedInUserTenantId) {
		// TODO Auto-generated method stub
		StringBuilder hsql = new StringBuilder("from BuyerAddress b inner join fetch b.state as s inner join fetch s.country c where b.buyer.id = :buyerId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("buyerId", loggedInUserTenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BuyerAddress getBuyerAddressAndStateAndCountryById(String addressId) {
		// TODO Auto-generated method stub
		StringBuilder hsql = new StringBuilder("from BuyerAddress b inner join fetch b.state as s inner join fetch s.country c where b.id = :addressId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("addressId", addressId);
		List<BuyerAddress>  list = query.getResultList();
		if(CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public BuyerAddress getBuyerAddressForTenantByTitle(String title, String tenantId) {
		StringBuilder hsql = new StringBuilder("from BuyerAddress a left join fetch a.state as st inner join fetch st.country where upper(a.title) = upper(:title) and a.buyer.id = :id and a.status = :status");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("title", title);
		query.setParameter("id", tenantId);
		query.setParameter("status", Status.ACTIVE);
		List<BuyerAddress> list = query.getResultList();
		if(CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public long findBuyerAddressCountForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (b) from BuyerAddress b where b.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BuyerAddress> findBuyerAddressListForTenantIdForCsv(String tenantId, int pageSize, int pageNo) {
		StringBuilder hsql = new StringBuilder("from BuyerAddress b inner join fetch b.state as s inner join fetch s.country c where b.buyer.id = :tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);
		
		List<BuyerAddress> list =  query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

}
