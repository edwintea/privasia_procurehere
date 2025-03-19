package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.FooterDao;
import com.privasia.procurehere.core.entity.Footer;
import com.privasia.procurehere.core.enums.FooterType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("footerDao")
public class FooterDaoImpl extends GenericDaoImpl<Footer, String> implements FooterDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Footer> findFootersByTeantId(String tenantId, TableDataInput input) {
		final Query query = constructeFootersForTenantQuery(tenantId, input, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredFootersForTenant(String tenantId, TableDataInput input) {
		final Query query = constructeFootersForTenantQuery(tenantId, input, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalActiveFootersForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count(f) from Footer f where f.status =:status and f.supplier.id =:tenantId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructeFootersForTenantQuery(String tenantId, TableDataInput input, boolean isCount) {
		String hql = "";
		if (isCount) {
			hql += "select count(distinct f.id) ";
		}
		if (!isCount) {
			hql += "select distinct NEW com.privasia.procurehere.core.entity.Footer(f.id, f.title, f.footerType, f.status, cb.loginId, f.createdDate, mb.loginId, f.modifiedDate)";
		}
		hql += " from Footer f";

		if (!isCount) {
			hql += " left outer join f.createdBy cb left outer join  f.modifiedBy as mb ";
		}
		hql += " where f.supplier.id = :tenantId";

		boolean isStatusFilterOn = false;
		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("createdBy")) {
					hql += " and upper(f.createdBy.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("footerTypeValue")) {
					hql += " and f.footerType = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					hql += " and upper(f.modifiedBy.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy") || cp.getData().equals("modifiedBy")) {
					hql += " and upper(f." + cp.getData() + ".loginId" + ") like (:" + cp.getData().replace(".", "") + ")";
				} else if(cp.getData().equals("status")) {
					hql += " and f.status = (:" + cp.getData() + ")";
			    } else {
					hql += " and upper(f." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and f.status = :status";

		}
		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("footerTypeValue")) {
						hql += " f.footerType" + " " + dir + ",";
					} else {
						hql += " f." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by f.createdDate desc";
			}
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn=true;
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("footerTypeValue")) {
					query.setParameter("footerTypeValue", FooterType.valueOf(cp.getSearch().getValue()));
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
	public List<Footer> getFootersByTypeForTenant(FooterType footerType, String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.Footer(f.id,f.title) from Footer f where f.supplier.id = :tenantId and f.footerType = :footerType and f.status =:status");
		query.setParameter("tenantId", tenantId);
		query.setParameter("footerType", footerType);
		query.setParameter("status", Status.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getFooterContentById(String footerId) {
		final Query query = getEntityManager().createQuery("select f.content from Footer f where f.id = :footerId");
		query.setParameter("footerId", footerId);
		List<String> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

}