package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfxTemplateDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.RfxTemplatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Nitin Otageri
 */
@Repository
public class RfxTemplateDaoImpl extends GenericDaoImpl<RfxTemplate, String> implements RfxTemplateDao {

	private static final Logger LOG = LogManager.getLogger(RfxTemplateDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxTemplate> findAllActiveTemplatesForTenant(String tenantId) {
		// Prepare the JPQL query string
		String jpqlQuery = "from RfxTemplate a left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb where a.status =:status and a.buyer.id = :tenantId order by a.templateName";

		// Log the JPQL query and parameters
		LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>findAllActiveTemplatesForTenant");
		LOG.info("JPQL Query: " + jpqlQuery);
		LOG.info("Parameters: status = " + Status.ACTIVE + ", tenantId = " + tenantId);

		// Create and configure the query
		final Query query = getEntityManager().createQuery(jpqlQuery);
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);

		// Execute the query and return the results
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RfxTemplate template) {
		if (template == null) {
			return false;
		}
		StringBuilder hsql = new StringBuilder("from RfxTemplate t where t.buyer.id = :tenantId and t.templateName = :templateName ");
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
	public List<RfxTemplate> findTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId) {
		final Query query = constructTemplatesForTenantQuery(tenantId, tableParams, false, userId);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalTemplatesForTenant(String tenantId) {
		StringBuilder hql = new StringBuilder("select count (t) from RfxTemplate t where t.status =:status and t.buyer.id = :tenantId ");
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

		hql += " from RfxTemplate t ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join fetch t.createdBy as cb left outer join fetch t.modifiedBy as mb ";
		}

		hql += " where t.status = :status ";

		// if not admin then only assigned templates
		if (tenantId != null) {
			hql += " and t.buyer.id = :tenantId ";
		} else {
			hql += " and t.id in (select at.id from User u left outer join u.assignedTemplates as at where u.id = :userId) ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and t.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					LOG.info(cp.getData());
					hql += " and upper(t.createdBy.name) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					LOG.info(cp.getData());
					LOG.info(cp.getSearch().getValue());
					hql += " and upper(t.modifiedBy.name) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("type")) {
					hql += " and t.type = (:type)";
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
				} else if (cp.getData().equals("type")) {
					query.setParameter("type", RfxTypes.valueOf(cp.getSearch().getValue()));
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
	public RfxTemplate getRfxTemplateForEditById(String templateId, String tenantId) {
		final Query query = getEntityManager().createQuery("from RfxTemplate a left outer join fetch a.fields f where a.buyer.id = :tenantId and a.id = :id");
		query.setParameter("id", templateId);
		query.setParameter("tenantId", tenantId);
		List<RfxTemplate> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenant(String tenantId, RfxTypes rfxTypes, String userId) {
		final Query query = getEntityManager().createQuery("from RfxTemplate a left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb where a.status =:status and a.buyer.id = :tenantId and a.type = :rfxType and a.id in (select t.id from User u inner join u.assignedTemplates t where u.id = :userId) order by a.templateName");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		query.setParameter("rfxType", rfxTypes);
		query.setParameter("userId", userId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxTemplate> findByTemplateNameForTenant(String searchValue, String tenantId, RfxTypes eventType, String userId) {
		StringBuffer sb = new StringBuffer("select distinct t from RfxTemplate t left outer join fetch t.createdBy as cb  left outer join fetch t.modifiedBy as mb where t.status =:status and t.buyer.id = :tenantId and t.type = :eventType and t.id in (select au.id from User u inner join u.assignedTemplates au where u.id = :userId) ");

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(t.templateName) like :searchValue or upper(t.templateDescription) like :searchValue) ");
		}
		sb.append(" order by t.templateName");

		LOG.info("SQL : " + sb.toString());
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventType", eventType);
		query.setParameter("userId", userId);
		query.setParameter("status", Status.ACTIVE);
		query.setMaxResults(10);

		if (StringUtils.checkString(searchValue).length() > 0) {
			LOG.info("template name or desc : " + searchValue);
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
		}
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenantId(String tenantId, RfxTypes rfxTypes) {
		final Query query = getEntityManager().createQuery("from RfxTemplate a where a.status =:status and a.buyer.id = :tenantId and a.type = :rfxType");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		query.setParameter("rfxType", rfxTypes);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxTemplate> findAllActiveTemplatesByAuctionTypeForTenantId(String tenantId, AuctionType auctionType, String userId) {
		final Query query = getEntityManager().createQuery("from RfxTemplate a where a.status =:status and a.buyer.id = :tenantId and a.type = :rfxType and a.templateAuctionType = :auctionType and  a.id in (select t.id from User u inner join u.assignedTemplates t where u.id = :userId) order by a.templateName");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		query.setParameter("auctionType", auctionType);
		query.setParameter("userId", userId);
		query.setParameter("rfxType", RfxTypes.RFA);
		return query.getResultList();
	}

	@Override
	public Boolean findTemplateIndustryCategoryFlagByEventId(String eventId, RfxTypes rfxTypes) {
		try {
			String hql = "select t.supplierBasedOnCategory from R" + rfxTypes.name().toLowerCase().substring(1) + "Event r left outer join r.template t where r.id = :eventId";
			final Query query = getEntityManager().createQuery(hql);
			query.setParameter("eventId", eventId);
			return (Boolean) query.getSingleResult();
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenant(String loggedInUserTenantId, RfxTypes rfxTypes, String userId, String pageNo) {
		final Query query = getEntityManager().createQuery("from RfxTemplate a left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb where a.status =:status and a.buyer.id = :tenantId and a.type = :rfxType and a.id in (select t.id from User u inner join u.assignedTemplates t where u.id = :userId) order by a.createdDate desc");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setParameter("rfxType", rfxTypes);
		query.setParameter("userId", userId);
		query.setFirstResult(Integer.parseInt(pageNo) * 10);
		query.setMaxResults(10);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenantInitial(String loggedInUserTenantId, RfxTypes rfxTypes, String userId) {
		final Query query = getEntityManager().createQuery("from RfxTemplate a left outer join fetch a.createdBy as cb left outer join fetch a.modifiedBy as mb where a.status =:status and a.buyer.id = :tenantId and a.type = :rfxType and a.id in (select t.id from User u inner join u.assignedTemplates t where u.id = :userId) order by a.createdDate desc");
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setParameter("rfxType", rfxTypes);
		query.setParameter("userId", userId);
		query.setFirstResult(0);
		query.setMaxResults(10);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxTemplatePojo> findTemplatesForTenantId(String tenantId, TableDataInput tableParams, String userId) {
		final Query query = constructRfxTemplatesForTenantIdQuery(tenantId, tableParams, false, userId);
		query.setFirstResult(tableParams.getStart());
		query.setMaxResults(tableParams.getLength());
		return query.getResultList();
	}

	private Query constructRfxTemplatesForTenantIdQuery(String tenantId, TableDataInput tableParams, boolean isCount, String userId) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause

		if (isCount) {
			hql += "select count(distinct t) ";
		} else {

			hql += "select distinct NEW com.privasia.procurehere.core.pojo.RfxTemplatePojo(t.id, t.templateName, t.templateDescription, t.type, t.status,cb.loginId, t.createdDate,mb.loginId,t.modifiedDate) ";
		}

		hql += " from RfxTemplate t";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join  t.createdBy as cb left outer join  t.modifiedBy as mb ";
		}

		hql += " where t.status = :status ";

		// if not admin then only assigned templates
		if (tenantId != null) {
			hql += " and t.buyer.id = :tenantId ";
		} else {
			hql += " and t.id in (select at.id from User u left outer join u.assignedTemplates as at where u.id = :userId) ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {

			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and t.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("createdBy")) {
					LOG.info(cp.getData());
					hql += " and upper(t.createdBy.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("modifiedBy")) {
					LOG.info(cp.getData());
					LOG.info(cp.getSearch().getValue());
					hql += " and upper(t.modifiedBy.loginId) like (:" + cp.getData() + ")";
				} else if (cp.getData().equals("type")) {
					hql += " and t.type = (:type)";
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
//					LOG.info("@@@@---------- 372-------" + dir);
					hql += " t." + orderColumn + " " + dir + ",";
					if (hql.lastIndexOf(",") == hql.length() - 1) {
						hql = hql.substring(0, hql.length() - 1);
					}
				}
			} else {
				hql += " order by t.createdDate desc";
			}
		}

//		LOG.info("HQL : " + hql);

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
				} else if (cp.getData().equals("type")) {
					query.setParameter("type", RfxTypes.valueOf(cp.getSearch().getValue()));
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
		String hql = "select u from User u left outer join fetch u.assignedTemplates temp where temp.id =:tempId";
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
	public RfxTemplate getTemplateByTenantIdAndTemplateId(String id, String tenantId) {
		String hql = "select temp from RfxTemplate temp left outer join fetch temp.buyer where temp.id = :tempId and temp.buyer.id = :tenantId ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tempId", id);
		query.setParameter("tenantId", tenantId);
		try {
			return (RfxTemplate) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getTemplateByUserIdAndTemplateId(String tempId, String loggedInUserTenantId) {
		String hql = "select DISTINCT USER_ID FROM PROC_USER_TEMPLATE_MAPPING where TEMPLATE_ID = '" + tempId + "'";
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
	@Transactional(readOnly = false,  propagation = Propagation.REQUIRES_NEW)
	public void deleteAssociatedUserForTemplate(String rfxTemplateId) {
		String hql = "DELETE FROM PROC_USER_TEMPLATE_MAPPING where TEMPLATE_ID = '" + rfxTemplateId + "'";
		Query query = getEntityManager().createNativeQuery(hql);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxTemplate> findTemplatesByRfxForUnMaskingUserIsNotNull() {
		final Query query = getEntityManager().createQuery("from RfxTemplate a  where a.unMaskedUser is not null");
		return query.getResultList();
	}

	@Override
	public void updateTemplateUnMaskUser(String tempId) {
		Query query = getEntityManager().createQuery("update RfxTemplate e set e.unMaskedUser = null where e.id = :tempId").setParameter("tempId", tempId);
		query.executeUpdate();
	}

	@Override
	public boolean isAssignedDeclarationToTemplate(String declarationId) {
		StringBuilder hql = new StringBuilder("select count(tf) from TemplateField tf where tf.defaultValue = :declarationId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("declarationId", declarationId);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	public long findAssignedCountOfDeclaration(String declarationId) {
		StringBuilder hql = new StringBuilder("select count(tf) from TemplateField tf  where tf.defaultValue = :declarationId and tf.template.status =:status");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("declarationId", declarationId);
		query.setParameter("status", Status.ACTIVE);
		return ((Number) query.getSingleResult()).longValue();

	}

	@Override
	public long findActiveTemplateCountByRfxTypeForTenantId(String searchValue, String tenantId, RfxTypes rfxType, String userId) {
		StringBuilder hql = new StringBuilder("select count(a) from RfxTemplate a left outer join a.createdBy as cb left outer join a.modifiedBy as mb where a.status =:status and a.buyer.id = :tenantId and a.type = :rfxType and a.id in (select t.id from User u inner join u.assignedTemplates t where u.id = :userId)");
		if(StringUtils.checkString(searchValue).length() > 0) {
			hql.append(" and (upper(a.templateName) like :searchValue or upper(a.templateDescription) like :searchValue)");
		}
		
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", Status.ACTIVE);
		query.setParameter("tenantId", tenantId);
		query.setParameter("rfxType", rfxType);
		query.setParameter("userId", userId);
		
		if (StringUtils.checkString(searchValue).length() > 0) {
			LOG.info("template name or desc : " + searchValue);
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
		}
		
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxTemplate> findTemplateByTemplateNameForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, RfxTypes rfxType, String tenantId, String userId) {
		StringBuffer sb = new StringBuffer("select distinct t from RfxTemplate t left outer join fetch t.createdBy as cb left outer join fetch t.modifiedBy as mb where t.status =:status and t.buyer.id = :tenantId and t.type = :eventType and t.id in (select au.id from User u inner join u.assignedTemplates au where u.id = :userId) ");

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(t.templateName) like :searchValue or upper(t.templateDescription) like :searchValue) ");
		}
		sb.append(" order by t.createdDate desc");

		LOG.info("SQL : " + sb.toString());
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventType", rfxType);
		query.setParameter("userId", userId);
		query.setParameter("status", Status.ACTIVE);
		
		if (start != null && pageLength != null) {
			query.setFirstResult(start);
			query.setMaxResults(pageLength);
		}

		if (StringUtils.checkString(searchValue).length() > 0) {
			LOG.info("template name or desc : " + searchValue);
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
		}
		return query.getResultList();
	}

}
