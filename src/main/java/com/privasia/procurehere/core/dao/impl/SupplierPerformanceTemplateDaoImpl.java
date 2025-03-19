/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierPerformanceTemplateDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplate;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;
import org.apache.logging.log4j.*;
/**
 * @author Jayshree
 */
@Repository
public class SupplierPerformanceTemplateDaoImpl extends GenericDaoImpl<SupplierPerformanceTemplate, String> implements SupplierPerformanceTemplateDao {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceTemplateDaoImpl.class);

	@Override
	public void deleteUsersForSPTemplate(String templateId) {
		String hql = "delete FROM PROC_USER_SP_TEMP_MAPPING where SUPP_PRFMC_TEMPLATE_ID = '" + templateId + "'";
		Query query = getEntityManager().createNativeQuery(hql);
		query.executeUpdate();
	}

	@Override
	public void updateSPTemplateStatus(String templateId, SourcingStatus status) {
		LOG.info("Template Id : " + templateId + " Status : " + status);
		String hql = "update SupplierPerformanceTemplate t set t.status = :status where t.id = :templateId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", status);
		query.setParameter("templateId", templateId);
		query.executeUpdate();
	}

	@Override
	public void updateSupplierPerformanceTemplateStatusAndComplete(String templateId, Boolean performanceCriteriaCompleted, SourcingStatus status) {
		String hql = "update SupplierPerformanceTemplate set performanceCriteriaCompleted = :performanceCriteriaCompleted ";
		if (status != null) {
			hql += ", status = :status";
		}
		hql += " where id = :templateId";

		LOG.info("HQL : " + hql);
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("templateId", templateId);
		query.setParameter("performanceCriteriaCompleted", performanceCriteriaCompleted);
		if (status != null) {
			query.setParameter("status", status);
		}
		query.executeUpdate();
	}

	@Override
	public boolean isTemplateExistsByTemplateName(String templateName, String tenantId) {
		final Query query = getEntityManager().createQuery("select count(sc) from SupplierPerformanceTemplate sc where sc.templateName =:templateName and sc.buyer.id =:tenantId");
		query.setParameter("templateName", templateName);
		query.setParameter("tenantId", tenantId);
		return ((Long) query.getSingleResult() > 0);
	}

	@Override
	public long findTotalActiveSPTemplateForTenant(String loggedInUserTenantId, TenantType tenantType) {
		StringBuilder hql = new StringBuilder("select count (sp) from SupplierPerformanceTemplate sp where sp.status =:status ");

		if (TenantType.BUYER == tenantType) {
			hql.append(" and sp.buyer.id =:tenantId ");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", SourcingStatus.ACTIVE);
		query.setParameter("tenantId", loggedInUserTenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFilteredSPTemplateForTenant(String loggedInUserTenantId, TableDataInput input, TenantType tenantType) {
		final Query query = constructSPTemplateForTenantQuery(loggedInUserTenantId, input, true, tenantType);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceTemplate> findSPTemplateForTenant(String loggedInUserTenantId, TableDataInput input, TenantType tenantType) {
		final Query query = constructSPTemplateForTenantQuery(loggedInUserTenantId, input, false, tenantType);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	private Query constructSPTemplateForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount, TenantType tenantType) {
		LOG.info("TENANT ID........" + tenantId);

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(sp) ";
		}

		hql += " from SupplierPerformanceTemplate sp ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch sp.createdBy as cb left outer join fetch sp.modifiedBy as mb ";
		}
		if (tenantType == TenantType.BUYER) {
			hql += " where sp.buyer.id = :tenantId ";
		}

		boolean isStatusFilterOn = false;

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;
					hql += " and sp.status = (:" + cp.getData() + ")";
				} else {
					hql += " and upper(sp." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
				}
			}
		}

		if (!isStatusFilterOn) {
			hql += " and sp.status = :status ";
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			hql += " order by  ";
			if (CollectionUtil.isNotEmpty(orderList)) {
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " sp." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " sp.createdDate desc ";
			}
		}
//		else {
//			hql += " order by sp.createdDate desc ";
//		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", SourcingStatus.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			query.setParameter("status", SourcingStatus.ACTIVE);
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUserIdListByTemplateId(String templateId) {
		String hql = "select DISTINCT USER_ID FROM PROC_USER_SP_TEMP_MAPPING where SUPP_PRFMC_TEMPLATE_ID = '" + templateId + "'";
		Query query = getEntityManager().createNativeQuery(hql);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceTemplate> getAllAssignedSpTemplateIdsForUser(String userId) {
		// String hql = "select DISTINCT SUPP_PRFMC_TEMPLATE_ID FROM PROC_USER_SP_TEMP_MAPPING where USER_ID = '" +
		// userId + "'";
		// String hql = "SELECT spt.ID FROM PROC_SUP_PERFORM_TEMPLATE spt " + "LEFT OUTER JOIN PROC_USER_SP_TEMP_MAPPING
		// spm ON spt.id = spm.SUPP_PRFMC_TEMPLATE_ID " + "WHERE spt.TEMPLATE_STATUS = 'ACTIVE' AND spm.USER_ID = '" +
		// userId + "'";

		String hql = "select new com.privasia.procurehere.core.entity.SupplierPerformanceTemplate(t.id, t.templateName, t.templateDescription) from User u join u.assignedSupplierPerformanceTemplates t where u.id = :id and t.status =:status";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", userId);
		query.setParameter("status", SourcingStatus.ACTIVE);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public boolean isExists(String templateId, String templateName) {
		final Query query = getEntityManager().createQuery("select count(sc) from SupplierPerformanceTemplate sc where sc.id=:templateId and sc.templateName =:templateName ");
		query.setParameter("templateId", templateId);
		query.setParameter("templateName", templateName);
		LOG.info((Long) query.getSingleResult());
		return ((Long) query.getSingleResult() > 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceTemplate> getAllSpTemplatesOfBuyer(String tenantId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SupplierPerformanceTemplate(a.id, a.templateName, a.templateDescription) from SupplierPerformanceTemplate a where a.status =:status and a.buyer.id = :tenantId order by a.templateName");
		query.setParameter("status", SourcingStatus.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

}
