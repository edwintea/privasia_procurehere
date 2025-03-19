package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PromotionalCodeDao;
import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author vipul
 */
@Repository
public class PromotionalCodeDaoImpl extends GenericDaoImpl<PromotionalCode, String> implements PromotionalCodeDao {

	private static final Logger LOG = LogManager.getLogger(PromotionalCodeDaoImpl.class);

	@Override
	public PromotionalCode getPromotionalCodeByPromoCode(String promoCode) throws Exception {
		try {
			// removed where clause and pc.buyer is null
			Query query = getEntityManager().createQuery("select distinct pc from PromotionalCode pc where pc.promoCode = :promoCode and pc.status = :status and (:now between pc.effectiveDate and pc.expiryDate) ");
			query.setParameter("promoCode", promoCode);
			query.setParameter("status", Status.ACTIVE);
			query.setParameter("now", new Date());
			return (PromotionalCode) query.getSingleResult();
		} catch (NoResultException e) {
			LOG.error("Invalid Promo Code '" + promoCode + "' :" + e.getMessage(), e);
			throw new ApplicationException("Invalid Promo Code '" + promoCode + "'");
		}
	}

	@Override
	public boolean isExists(PromotionalCode promotionalCode) {
		final Query query = getEntityManager().createQuery("from PromotionalCode pc where upper(pc.promoCode) = upper(:promoCode) ");
		query.setParameter("promoCode", promotionalCode.getPromoCode());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PromotionalCode> findAllPromotionCodeForTenant(TableDataInput tableParams) {
		final Query query = constructPromotionCodeForTenantQuery(tableParams, false);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public PromotionalCode findPromotionalCodeById(String id) {
		final Query query = getEntityManager().createQuery("select distinct pc from PromotionalCode pc left outer join fetch pc.buyer as b left outer join fetch pc.supplier as s left outer join fetch pc.buyerPlan as bp left outer join fetch pc.supplierPlan as sp where pc.id = :id and pc.status = :status and (:now between pc.effectiveDate and pc.expiryDate)");
		query.setParameter("id", id);
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("now", new Date());
		return (PromotionalCode) query.getSingleResult();
	}

	@Override
	public long findTotalFilteredPromotionalCodeList(TableDataInput tableParams) {
		final Query query = constructPromotionCodeForTenantQuery(tableParams, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalPromotionalCodeList() {
		StringBuilder hql = new StringBuilder("select count(pc) from PromotionalCode pc where pc.status =:status ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tableParams
	 * @param isCount
	 * @return
	 */
	private Query constructPromotionCodeForTenantQuery(TableDataInput tableParams, boolean isCount) {
		String hql = "";
		if (isCount) {
			hql += "select count(pc) ";
		}

		hql += " from PromotionalCode pc ";
		if (!isCount) {
			hql += " left outer join fetch pc.createdBy as cb ";

		}
		hql += " where 1 = 1";

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					hql += " and pc.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(pc." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and pc.status = :status ";
		}

		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " pc." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}
		LOG.info(" Hql :" + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		// Apply Search values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (cp.getSearchable() != null && Boolean.TRUE == cp.getSearchable() && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
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
	public void updatePromoCode(String promoCodeId) {
		Query query = getEntityManager().createQuery("update PromotionalCode pc set pc.usageCount = (pc.usageCount + 1), pc.inUse = :use where pc.id = :promoCodeId ");
		query.setParameter("use", true);
		query.setParameter("promoCodeId", promoCodeId);
		int updatePromoResult = query.executeUpdate();
		LOG.info("updatePromoResult :" + updatePromoResult);
	}

	@Override
	public PromotionalCode checkPromotionalCodeByPromoCode(String promoCode) throws Exception {
		try {
			Query query = getEntityManager().createQuery("select distinct pc from PromotionalCode pc left outer join fetch pc.buyer b where pc.promoCode = :promoCode and pc.status = :status and (:now between pc.effectiveDate and pc.expiryDate) ");
			query.setParameter("promoCode", promoCode);
			query.setParameter("status", Status.ACTIVE);
			query.setParameter("now", new Date());
			return (PromotionalCode) query.getSingleResult();
		} catch (NoResultException e) {
			LOG.error("Invalid Promo Code '" + promoCode + "' :" + e.getMessage(), e);
			throw new ApplicationException("Invalid Promo Code '" + promoCode + "'");
		}
	}

	@Override
	public boolean checkPromoCodeUseByBuyer(String promoId, String buyerId) throws Exception {
		Query query = getEntityManager().createQuery("select count(*) from PaymentTransaction t where t.buyer.id = :buyerId and t.promoCode.id = :promoId ");
		query.setParameter("promoId", promoId);
		query.setParameter("buyerId", buyerId);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	public PromotionalCode findPromoCodeByName(String promoCode) throws Exception {
		try {
			Query query = getEntityManager().createQuery("select distinct pc from PromotionalCode pc left outer join fetch pc.buyer as b left outer join fetch pc.supplier as s left outer join fetch pc.buyerPlan as bp left outer join fetch pc.supplierPlan as sp where pc.promoCode = :promoCode and pc.status = :status and (:now between pc.effectiveDate and pc.expiryDate)");
			query.setParameter("promoCode", promoCode);
			query.setParameter("status", Status.ACTIVE);
			query.setParameter("now", new Date());
			return (PromotionalCode) query.getSingleResult();
		} catch (NoResultException e) {
			LOG.error("Invalid Promo Code '" + promoCode + "' :" + e.getMessage(), e);
			throw new ApplicationException("Invalid Promo Code '" + promoCode + "'");
		}
	}

	@Override
	public long findCountOfSupplierPromoCodeById(String supplierId) {
		StringBuilder hsql = new StringBuilder("select count( distinct sp.id)  from PromotionalCode sp where sp.supplier.id = :supplierId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		return ((Number) query.getSingleResult()).longValue();
	}

}