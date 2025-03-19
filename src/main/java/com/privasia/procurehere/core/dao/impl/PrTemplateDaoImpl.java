package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PrTemplateDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.PrTemplatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author parveen
 */
@Repository
public class PrTemplateDaoImpl extends GenericDaoImpl<PrTemplate, String> implements PrTemplateDao {

	private static final Logger LOG = LogManager.getLogger(PrTemplateDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public List<PrTemplate> findAllActiveTemplatesForTenant(String tenantId) {
		final Query query = getEntityManager().createQuery("from PrTemplate a left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb where a.status =:status and a.buyer.id = :tenantId order by a.templateName");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrTemplate> findAllActiveTemplatesForTenantAndUser(String tenantId, String userId, TableDataInput input) {
		final Query query = getEntityManager().createQuery("select distinct a from PrTemplate a join fetch a.createdBy as cb where a.status =:status and a.buyer.id = :tenantId and a.id in (select t.id from User u inner join u.assignedPrTemplates t where u.id = :userId) order by a.templateName");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		query.setParameter("userId", userId);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(PrTemplate template) {
		if (template == null) {
			return false;
		}
		StringBuilder hsql = new StringBuilder("from PrTemplate t where t.buyer.id = :tenantId and t.templateName = :templateName ");
		if (StringUtils.checkString(template.getId()).length() > 0) {
			hsql.append(" and t.id <> :id ");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", template.getBuyer().getId());
		query.setParameter("templateName", template.getTemplateName());
		if (StringUtils.checkString(template.getId()).length() > 0) {
			query.setParameter("id", template.getId());
		}
		List<Country> scList = query.getResultList();
		return CollectionUtil.isNotEmpty(scList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrTemplate> findTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId) {
		final Query query = constructTemplatesForTenantQuery(tenantId, tableParams, false, userId);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalTemplatesForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (t) from PrTemplate t where t.status =:status and t.buyer.id = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFilteredTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId) {
		final Query query = constructTemplatesForTenantQuery(tenantId, tableParams, true, userId);
		return ((Number) query.getSingleResult()).longValue();
	}

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param userId TODO
	 * @param sortValue
	 * @return
	 */
	private Query constructTemplatesForTenantQuery(String tenantId, TableDataInput tableParams, boolean isCount, String userId) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(t) ";
		}

		hql += " from PrTemplate t ";
		if (!isCount) {
			hql += " left outer join fetch t.createdBy as cb left outer join fetch t.modifiedBy as mb ";
		}

		hql += " where t.status = :status ";

		// if not admin then only assigned templates
		if (tenantId != null) {
			hql += " and t.buyer.id = :tenantId ";
		} else {
			hql += " and t.id in (select apt.id from User u left outer join u.assignedPrTemplates as apt where u.id = :userId) ";
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

		LOG.info("HQL : " + hql);

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
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("createdBy") || cp.getData().equals("modifiedBy")) {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
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
	public PrTemplate getPrTemplateForEditById(String templateId, String tenantId) {
		final Query query = getEntityManager().createQuery("from PrTemplate a left outer join fetch a.fields f where a.buyer.id = :tenantId and a.id = :id order by a.createdDate desc");
		query.setParameter("id", templateId);
		query.setParameter("tenantId", tenantId);
		List<PrTemplate> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrTemplate> findByTemplateNameForTenant(String searchValue, String tenantId, String userId, String pageNo) {
		StringBuffer sb = new StringBuffer("select distinct t from PrTemplate t left outer join fetch t.createdBy as cb  left outer join fetch t.modifiedBy as mb where t.status =:status and t.buyer.id = :tenantId and t.id in (select au.id from User u inner join u.assignedPrTemplates au where u.id = :userId) ");

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(t.templateName) like :searchValue or upper(t.templateDescription) like :searchValue) ");
		}
		sb.append(" order by t.createdDate desc ");

		LOG.info("SQL : " + sb.toString());
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("userId", userId);
		query.setParameter("status", Status.ACTIVE);
		if (StringUtils.checkString(pageNo).length() > 0) {
			query.setFirstResult(Integer.parseInt(pageNo) * 10);
		}
		query.setMaxResults(10);

		if (StringUtils.checkString(searchValue).length() > 0) {
			LOG.info("searchValue : " + searchValue);
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
		}
		return query.getResultList();
	}

	@Override
	public Integer findAssignedTemplateCount(String templateId) {
		StringBuilder hql = new StringBuilder("select count (p) from Pr p where p.template.id =:templateId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("templateId", templateId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrTemplate> findAllPrTemplatesForTenantAndUser(String tenantId) {
		/*
		 * final Query query = getEntityManager().
		 * createQuery("select distinct a from PrTemplate a join fetch a.createdBy as cb where a.status =:status and a.buyer.id = :tenantId and a.id in (select t.id from User u inner join u.assignedPrTemplates t where u.id = :userId) order by a.templateName"
		 * );
		 */final Query query = getEntityManager().createQuery("select distinct a from PrTemplate a join fetch a.createdBy as cb where a.status =:status and a.buyer.id = :tenantId");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrTemplatePojo> findTemplatesForTenantId(String tenantId, TableDataInput tableParams, String userId) {
		final Query query = constructPrTemplatesForTenantIdQuery(tenantId, tableParams, false, userId);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	private Query constructPrTemplatesForTenantIdQuery(String tenantId, TableDataInput tableParams, boolean isCount, String userId) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(t) ";
		} else {
			hql += "select distinct NEW com.privasia.procurehere.core.pojo.PrTemplatePojo(t.id, t.templateName, t.templateDescription, t.status,cb.loginId, t.createdDate,mb.loginId,t.modifiedDate) ";
		}

		hql += " from PrTemplate t ";
		if (!isCount) {
			hql += " left outer join t.createdBy as cb left outer join t.modifiedBy as mb ";
		}

		hql += " where t.status = :status ";

		// if not admin then only assigned templates
		if (tenantId != null) {
			hql += " and t.buyer.id = :tenantId ";
		} else {
			hql += " and t.id in (select apt.id from User u left outer join u.assignedPrTemplates as apt where u.id = :userId) ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			LOG.info("CP>>>>>>>>>>>>>>>>>>" + cp.getData());
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

		LOG.info("HQL : " + hql);

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
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else if (cp.getData().equals("createdBy") || cp.getData().equals("modifiedBy")) {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
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
	public List<User> getAllUsers(String tempId) {
		String hql = "select u from User u left outer join fetch u.assignedPrTemplates temp where temp.id =:tempId";
		Query query = getEntityManager().createQuery(hql);

		try {
			return query.getResultList();

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getTemplateByUserIdAndTemplateId(String tempId, String loggedInUserTenantId) {
		String hql = "select DISTINCT USER_ID FROM  PROC_USER_PR_TEMPLATE_MAPPING where TEMPLATE_ID = '" + tempId + "'";
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
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteusersForTemplate(String prTemplateId) {
		String hql = "delete FROM  PROC_USER_PR_TEMPLATE_MAPPING where TEMPLATE_ID = '" + prTemplateId + "'";
		Query query = getEntityManager().createNativeQuery(hql);
		query.executeUpdate();

	}

	@Override
	public boolean validateContractItemSetting(String prId) {
		try {
			StringBuilder hql = new StringBuilder("select distinct pt.contractItemsOnly from Pr p inner join p.template pt where p.id =:prId");
			final Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("prId", prId);
			return ((Boolean) query.getSingleResult()).booleanValue();
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public long getTotalTemplateCountForTenant(String searchValue, String tenantId, String userId) {
		StringBuffer sb = new StringBuffer("select count(t) from PrTemplate t left outer join t.createdBy as cb left outer join t.modifiedBy as mb where t.status =:status and t.buyer.id = :tenantId and t.id in (select au.id from User u inner join u.assignedPrTemplates au where u.id = :userId) ");

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(t.templateName) like :searchValue or upper(t.templateDescription) like :searchValue) ");
		}
		LOG.info("SQL : " + sb.toString());
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("userId", userId);
		query.setParameter("status", Status.ACTIVE);
		if (StringUtils.checkString(searchValue).length() > 0) {
			LOG.info("searchValue : " + searchValue);
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
		}
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PrTemplate> findTemplatesByTemplateNameForTenantId(String searchValue, Integer pageNo, Integer pageLength, Integer start, String tenantId, String userId) {
		StringBuffer sb = new StringBuffer("select distinct t from PrTemplate t left outer join fetch t.createdBy as cb  left outer join fetch t.modifiedBy as mb where t.status =:status and t.buyer.id = :tenantId and t.id in (select au.id from User u inner join u.assignedPrTemplates au where u.id = :userId) ");

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(t.templateName) like :searchValue or upper(t.templateDescription) like :searchValue) ");
		}
		sb.append(" order by t.createdDate desc ");

		LOG.info("SQL : " + sb.toString());
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("userId", userId);
		query.setParameter("status", Status.ACTIVE);

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

}
