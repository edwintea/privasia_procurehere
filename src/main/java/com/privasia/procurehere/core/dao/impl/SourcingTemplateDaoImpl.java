package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SourcingTemplateDao;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */
@Repository
public class SourcingTemplateDaoImpl extends GenericDaoImpl<SourcingFormTemplate, String> implements SourcingTemplateDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormTemplate> findAllActiveSourcingTemplateForTenant(String tenantId, TableDataInput input, String userId) {
		final Query query = constructTemplatesForTenantQuery(tenantId, input, false, userId);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormTemplate> findAllActiveSourcTemplateForTenant(String tenantId, TableDataInput input, String userId) {
		final Query query = constructTemplatesForTenantQuery(tenantId, input, false, userId);
		query.setParameter("tenantId", tenantId);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId) {
		final Query query = constructTemplatesForTenantQuery(tenantId, tableParams, true, userId);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructTemplatesForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount, String userId) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(t) ";
		}

		hql += " from SourcingFormTemplate t ";
		if (!isCount) {
			hql += " left outer join fetch t.createdBy as cb left outer join fetch t.modifiedBy as mb ";
		}

		hql += " where t.status = :status ";

		if (tenantId != null) {
			hql += " and t.tenantId = :tenantId ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and t.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy") || cp.getData().equals("modifiedBy")) {
					hql += " and upper(t." + cp.getData() + ".loginId" + ") like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(t." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";

				}
			}
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

		final Query query = getEntityManager().createQuery(hql.toString());
		if (tenantId != null) {
			query.setParameter("tenantId", tenantId);
		} else {
			query.setParameter("userId", userId);
		}

		boolean isStatusFilterOn = false;

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					isStatusFilterOn = true;

					query.setParameter("status", SourcingStatus.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					LOG.info("cp.getData() " + cp.getData());
				}
			}
		}
		// If status search filter is not ON then by default return only active records.
		if (!isStatusFilterOn) {
			query.setParameter("status", SourcingStatus.ACTIVE);
		}
		return query;
	}

	@Override
	public long findTotalTemplatesForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (t) from SourcingFormTemplate t where   t.tenantId = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public boolean isExists(String formId, String formName) {
		final Query query = getEntityManager().createQuery("select count(sc) from SourcingFormTemplate sc where sc.id=:id and formName=:name");
		query.setParameter("id", formId);
		query.setParameter("name", formName);
		LOG.info((Long) query.getSingleResult());
		return ((Long) query.getSingleResult() > 0);
	}

	@Override
	public boolean isTemplateExists(String formName, String tenantId) {
		final Query query = getEntityManager().createQuery("select count(sc) from SourcingFormTemplate sc where  UPPER(TRIM(sc.formName))=:formName and sc.tenantId=:tenantId");
		query.setParameter("formName", formName != null ? formName.trim().toUpperCase() : null);
		query.setParameter("tenantId", tenantId);
		LOG.info((Long) query.getSingleResult());
		return ((Long) query.getSingleResult() > 0);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormTemplate> getSourcingFormbyteantId(String loggedInUserTenantId) {
		Query query = getEntityManager().createQuery("from SourcingFormTemplate  sc where sc.tenantId=:tenantId");
		query.setParameter("tenantId", loggedInUserTenantId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormTemplate> getAllTemplate(String loggedInUserTenantId, TableDataInput input, String userId) {
		Query query = constructTemplatesForTenantQuery(loggedInUserTenantId, input, false, userId);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormTemplate> findByTemplateNameForTenant(String searchValue, String tenantId, String pageNo, String userId) {
		LOG.info("assignedSourcingTemplates 1");
		StringBuffer sb = new StringBuffer("select distinct t from SourcingFormTemplate t left outer join fetch t.createdBy  cb left outer join fetch t.modifiedBy as mb where t.tenantId=:tenantId and t.status=:status and t.id in (select au.id from User u inner join u.assignedSourcingTemplates au where u.id = :userId) ");
		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(t.formName) like :searchValue or upper(t.description) like :searchValue) ");
		}
		sb.append(" order by t.createdDate desc ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("userId", userId);
		query.setParameter("status", SourcingStatus.ACTIVE);
		if (StringUtils.checkString(pageNo).length() > 0) {
			query.setFirstResult(Integer.parseInt(pageNo) * 20);
		}
		query.setMaxResults(10);
		if (StringUtils.checkString(searchValue).length() > 0) {
			LOG.info("searchValue : " + searchValue);
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormTemplate> findAllActiveSourcingTemplateForTenantId(String tenantId) {
		final String hql = " from SourcingFormTemplate t  where t.status = :status and t.tenantId = :tenantId ";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", SourcingStatus.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormTemplate> findAllAssignActiveSourcingTemplateForTenant(String tenantId, TableDataInput input, String userId) {
		final String hql = "select distinct t from SourcingFormTemplate t  left outer join fetch t.createdBy  cb where t.status = :status and t.tenantId = :tenantId  order by t.createdDate desc";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", SourcingStatus.ACTIVE);
		query.setParameter("tenantId", tenantId);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long getBqCount(String formId) {
		String hql = "select count(a) from SourcingTemplateCqItem a where a.sourcingForm.id = :formId and a.parent is not null";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("formId", formId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getTemplateByUserIdAndTemplateId(String tempId, String loggedInUserTenantId) {
		String hql = "select DISTINCT USER_ID FROM PROC_USER_RFS_TEMP_MAPPING where SOURCING_TEMPLATE_ID = '" + tempId + "'";
		Query query = getEntityManager().createNativeQuery(hql);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public long getTotalTemplateCountForTenant(String tenantId, String userId) {
		LOG.info("assignedSourcingTemplates 5");
		StringBuffer sb = new StringBuffer("select count(t) from SourcingFormTemplate t left outer join t.createdBy cb left outer join t.modifiedBy as mb where t.tenantId=:tenantId and t.status=:status and t.id in (select au.id from User u inner join u.assignedSourcingTemplates au where u.id = :userId) ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("userId", userId);
		query.setParameter("status", SourcingStatus.ACTIVE);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormTemplate> findTemplatesBySearchValForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, String tenantId, String userId) {
		LOG.info("assignedSourcingTemplates 6");
		StringBuffer sb = new StringBuffer("select distinct t from SourcingFormTemplate t left outer join fetch t.createdBy  cb left outer join fetch t.modifiedBy as mb where t.tenantId=:tenantId and t.status=:status and t.id in (select au.id from User u inner join u.assignedSourcingTemplates au where u.id = :userId) ");
		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(t.formName) like :searchValue or upper(t.description) like :searchValue) ");
		}
		sb.append(" order by t.createdDate desc ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("userId", userId);
		query.setParameter("status", SourcingStatus.ACTIVE);

		if (start != null && pageLength != null) {
			query.setFirstResult(start);
			query.setMaxResults(pageLength);
		}

		if (StringUtils.checkString(searchValue).length() > 0) {
			LOG.info("searchValue : " + searchValue);
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
		}
		return query.getResultList();
	}

	@Override
	public long findTemplatesCountBySearchValForTenant(String searchValue, String tenantId, String userId) {
		LOG.info("assignedSourcingTemplates 7");
		StringBuffer sb = new StringBuffer("select count(t) from SourcingFormTemplate t left outer join t.createdBy cb left outer join t.modifiedBy as mb where t.tenantId=:tenantId and t.status=:status and t.id in (select au.id from User u inner join u.assignedSourcingTemplates au where u.id = :userId) ");
		LOG.info("searchValue>>>>>>>>>>>>>>>>>> : " + searchValue);
		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(t.formName) like :searchValue or upper(t.description) like :searchValue) ");
		}

		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("userId", userId);
		query.setParameter("status", SourcingStatus.ACTIVE);

		if (StringUtils.checkString(searchValue).length() > 0) {
			LOG.info("searchValue : " + searchValue);
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormTemplate> getAllSourcingTemplatesOfBuyer(String tenantId) {
		LOG.info("getAllSourcingTemplatesOfBuyer >>> ");
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SourcingFormTemplate(a.id, a.formName, a.description) from SourcingFormTemplate a where a.status =:status and a.tenantId = :tenantId order by a.formName");
		query.setParameter("status", SourcingStatus.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSourcingTemplateByUserIdAndTemplateId(String templateId, String tenantId) {
		String hql = "select DISTINCT USER_ID FROM PROC_USER_RFS_TEMP_MAPPING where SOURCING_TEMPLATE_ID = '" + templateId + "'";
		Query query = getEntityManager().createNativeQuery(hql);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void deleteusersForRfsTemplate(String id) {
		String hql = "delete FROM PROC_USER_RFS_TEMP_MAPPING where SOURCING_TEMPLATE_ID = '" + id + "'";
		Query query = getEntityManager().createNativeQuery(hql);
		query.executeUpdate();
	}

}