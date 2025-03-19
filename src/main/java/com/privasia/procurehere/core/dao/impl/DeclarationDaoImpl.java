package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.DeclarationDao;
import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.enums.DeclarationType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.DeclarationTemplatePojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("declarationDao")
public class DeclarationDaoImpl extends GenericDaoImpl<Declaration, String> implements DeclarationDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<DeclarationTemplatePojo> findDeclarationsByTeantId(String tenantId, TableDataInput input) {
		final Query query = constructeDeclarationForTenantQuery(tenantId, input, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredDeclarationsForTenant(String tenantId, TableDataInput input) {
		final Query query = constructeDeclarationForTenantQuery(tenantId, input, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalActiveDeclarationsForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count(d) from Declaration d where d.status =:status and d.buyer.id =:tenantId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructeDeclarationForTenantQuery(String tenantId, TableDataInput input, boolean isCount) {
		String hql = "";
		if (isCount) {
			hql += "select count(distinct d.id) ";
		}
		if (!isCount) {
			hql += "select distinct NEW com.privasia.procurehere.core.pojo.DeclarationTemplatePojo(d.id, d.title, d.declarationType, d.status,cb.loginId, d.createdDate,mb.loginId,d.modifiedDate)";
		}
		hql += " from Declaration d";

		if (!isCount) {
			hql += " left outer join d.createdBy cb left outer join  d.modifiedBy as mb ";
		}
		hql += " where d.buyer.id = :tenantId";

		boolean isStatusFilterOn = false;
		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					hql += " and d.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("declarationType")) {
					hql += " and d.declarationType = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					LOG.info(cp.getData());
					hql += " and upper(d.createdBy.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					LOG.info(cp.getData());
					LOG.info(cp.getSearch().getValue());
					hql += " and upper(d.modifiedBy.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy") || cp.getData().equals("modifiedBy")) {
					hql += " and upper(d." + cp.getData() + ".loginId" + ") like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(d." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and d.status = :status";

		}
		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " d." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by d.createdDate desc";
			}
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("declarationType")) {
					query.setParameter("declarationType", DeclarationType.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("createdBy") || cp.getData().equals("modifiedBy")) {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Declaration> getDeclarationsByTypeForTenant(DeclarationType declarationType, String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.Declaration(d.id,d.title) from Declaration d where d.buyer.id = :tenantId and d.declarationType = :declarationType and d.status =:status");
		query.setParameter("tenantId", tenantId);
		query.setParameter("declarationType", declarationType);
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

}