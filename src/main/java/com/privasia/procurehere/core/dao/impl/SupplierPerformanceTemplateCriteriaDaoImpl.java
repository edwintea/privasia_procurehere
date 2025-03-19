/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierPerformanceTemplateCriteriaDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplateCriteria;
import com.privasia.procurehere.core.pojo.SupplierPerformanceTemplateCriteriaPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Jayshree
 */
@Repository
public class SupplierPerformanceTemplateCriteriaDaoImpl extends GenericDaoImpl<SupplierPerformanceTemplateCriteria, String> implements SupplierPerformanceTemplateCriteriaDao {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceTemplateCriteriaDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceTemplateCriteria> getAllCriteriaByOrder(String templateId) {
		final Query query = getEntityManager().createQuery("select distinct(s) from SupplierPerformanceTemplateCriteria s left outer join fetch s.template t left outer join fetch s.parent p left outer join fetch s.children c where s.template.id =:templateId order by s.level, s.order");
		query.setParameter("templateId", templateId);
		return query.getResultList();
	}

	@Override
	public boolean isCriteriaExistsByName(SupplierPerformanceTemplateCriteria criteria) {
		String hql = "select count(sc) from SupplierPerformanceTemplateCriteria sc where sc.template.id = :templateId and sc.name = :criteriaName";
		if (StringUtils.checkString(criteria.getId()).length() > 0) {
			hql += " and sc.id <> :id";
		}
		
		if(criteria.getParent() != null && StringUtils.checkString(criteria.getParent().getId()).length() > 0) {
			hql += " and sc.parent.id = :parentId";
		} else {
			hql += " and sc.parent is null";
		}
		
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("templateId", criteria.getTemplate().getId());
		query.setParameter("criteriaName", criteria.getName());
		if (StringUtils.checkString(criteria.getId()).length() > 0) {
			query.setParameter("id", criteria.getId());
		}
		if(criteria.getParent() != null && StringUtils.checkString(criteria.getParent().getId()).length() > 0) {
			query.setParameter("parentId", criteria.getParent().getId());
		}
		return ((Long) query.getSingleResult() > 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceTemplateCriteria> getCriteriaLevelOrder(String templateId) {
		final Query query = getEntityManager().createQuery("from SupplierPerformanceTemplateCriteria a where a.parent is null and a.template.id = :templateId");
		query.setParameter("templateId", templateId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceTemplateCriteria> findAllCriteriasByTemplateId(String templateId) {
		final Query query = getEntityManager().createQuery("select distinct a from SupplierPerformanceTemplateCriteria a where a.template.id =:templateId order by a.level, a.order");
		query.setParameter("templateId", templateId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceTemplateCriteriaPojo> findAllCriteriaPojoByTemplateId(String templateId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.SupplierPerformanceTemplateCriteriaPojo(a.id, a.template.id, a.order, a.level, a.name, a.description, a.maximumScore, a.weightage, a.parent.id) from SupplierPerformanceTemplateCriteria a where a.template.id = :templateId order by a.level, a.order");
		query.setParameter("templateId", templateId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierPerformanceTemplateCriteria getSPTCriteriaByCriteriaIdAndTemplateId(String templateId, String criteriaId) {
		final Query query = getEntityManager().createQuery("select distinct a from SupplierPerformanceTemplateCriteria a left outer join fetch a.template t left outer join fetch a.parent p where t.id = :templateId and a.id = :criteriaId");
		query.setParameter("templateId", templateId);
		query.setParameter("criteriaId", criteriaId);
		List<SupplierPerformanceTemplateCriteria> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String deleteCriteriaByCreteriaId(String criteriaId, String templateId) {

		Query query = getEntityManager().createQuery("select a.level, a.order, p.id from SupplierPerformanceTemplateCriteria a left outer join a.parent p where a.id = :criteriaId");
		query.setParameter("criteriaId", criteriaId);
		List<Object[]> result = query.getResultList();
		if (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) {
			int level = ((Number) result.get(0)[0]).intValue();
			int order = ((Number) result.get(0)[1]).intValue();
			String parentId = ((String) result.get(0)[2]);
			LOG.info("Reordering Level :" + level + " order :" + order + " parentId : " + parentId);

			// If Parent
			if (StringUtils.checkString(parentId).length() == 0) {
				StringBuilder hql = new StringBuilder("update SupplierPerformanceTemplateCriteria i set i.level = (i.level - 1) where i.level > :level and i.template.id = :templateId");
				query = getEntityManager().createQuery(hql.toString());
				query.setParameter("templateId", templateId);
				query.setParameter("level", level);
				query.executeUpdate();
			} else {
				// If Child
				StringBuilder hql = new StringBuilder("update SupplierPerformanceTemplateCriteria i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.template.id = :templateId");
				query = getEntityManager().createQuery(hql.toString());
				query.setParameter("templateId", templateId);
				query.setParameter("level", level);
				query.setParameter("order", order);
				query.setParameter("parentId", parentId);
				query.executeUpdate();
			}
		}

		LOG.info("Going to Delete the selected Criteria ");
		Query query11 = getEntityManager().createQuery("delete from SupplierPerformanceTemplateCriteria i where i.parent.id = :criteriaId and i.template.id = :templateId");
		query11.setParameter("criteriaId", criteriaId);
		query11.setParameter("templateId", templateId);
		query11.executeUpdate();

		Query query1 = getEntityManager().createQuery("delete from SupplierPerformanceTemplateCriteria i where i.id = :criteriaId and i.parent is not null and i.template.id = :templateId");
		query1.setParameter("criteriaId", criteriaId);
		query1.setParameter("templateId", templateId);
		query1.executeUpdate();

		query = getEntityManager().createQuery("delete from SupplierPerformanceTemplateCriteria i where i.id = :criteriaId and i.parent is null and i.template.id = :templateId");
		query.setParameter("criteriaId", criteriaId);
		query.setParameter("templateId", templateId);
		query.executeUpdate();
		LOG.info("COMPLETED TO DELETE the Criteria");

		return criteriaId;
	}

	@Override
	public long getCriteriaCount(String templateId) {
		String hql = "select count(a) from SupplierPerformanceTemplateCriteria a where a.template.id = :templateId ";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("templateId", templateId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSubCriteriaNotAddedCriteriaIdsByTemplateId(String templateId) {
		final Query query = getEntityManager().createQuery("select i.name from SupplierPerformanceTemplateCriteria i where i.template.id = :templateId and i.parent is null and (select count(child.id) from SupplierPerformanceTemplateCriteria child where child.parent.id = i.id ) = 0 order by i.level, i.order ");
		query.setParameter("templateId", templateId);
		return query.getResultList();
	}

	@Override
	public BigDecimal getSumOfWeightageOfAllCriteriaByTemplateId(String templateId, String criteriaId) {
		String hql = "select sum(a.weightage) from SupplierPerformanceTemplateCriteria a where a.template.id = :templateId and a.parent is null ";

		if (StringUtils.checkString(criteriaId).length() > 0) {
			// Update
			hql += " and a.id <> :criteriaId";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("templateId", templateId);
		if (StringUtils.checkString(criteriaId).length() > 0) {
			// Update
			query.setParameter("criteriaId", criteriaId);
		}
		BigDecimal result = ((BigDecimal) query.getSingleResult());
		if (result == null) {
			return new BigDecimal(0);
		} else {
			return ((BigDecimal) query.getSingleResult());
		}
	}

	@Override
	public BigDecimal getSumOfWeightageOfAllSubCriteriaForCriteriaByCriteriaId(String subCriteriaId, String criteriaId, String templateId) {
		String hql = "select sum(a.weightage) from SupplierPerformanceTemplateCriteria a where a.template.id = :templateId and a.parent.id = :parentId and a.parent is not null ";

		if (StringUtils.checkString(subCriteriaId).length() > 0) {
			// Update
			hql += "and a.id <> :subCriteriaId";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("templateId", templateId);
		query.setParameter("parentId", criteriaId);
		if (StringUtils.checkString(subCriteriaId).length() > 0) {
			query.setParameter("subCriteriaId", subCriteriaId);
		}
		BigDecimal result = ((BigDecimal) query.getSingleResult());
		if (result == null) {
			return new BigDecimal(0);
		} else {
			return ((BigDecimal) query.getSingleResult());
		}
	}

	@Override
	public boolean isCriteriaExistsByName(String name, String templateId, String parentId) {
		LOG.info("parentId :" + parentId + ", Name : " + name + ", Template Id : " + templateId);
		System.out.println("SQLSQLSQLSQLSQLSQLSQLSQLSQLSQLSQLSQLSQLSQLSQLSQL");
		String hql = "select count(b) from SupplierPerformanceTemplateCriteria b where b.template.id = :templateId and upper(b.name) = :name";
		if (StringUtils.checkString(parentId).length() > 0) {
			hql += " and b.parent.id = :parentId ";
		} else {
			hql += " and b.parent is null";
		}
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("name", StringUtils.checkString(name).toUpperCase());
		query.setParameter("templateId", templateId);
		if (StringUtils.checkString(parentId).length() > 0) {
			query.setParameter("parentId", parentId);
		}
		LOG.info("hql :" + hql);
		return ((Long) query.getSingleResult()).intValue() > 0;

	}

	@Override
	public void updateCriteriaOrder(SupplierPerformanceTemplateCriteria criteria, String templateId, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel) {
		LOG.info("UPdateing ITEM ORDERE ==> oldParent " + oldParent + " newParent " + newParent + " oldOrder " + oldOrder + " newOrder " + newOrder + " oldLevel " + oldLevel + " newLevel" + newLevel);

		// Top Level
		// Rearrange its new place - Push down
		if (newOrder == 0) {
			LOG.info("Enter 1 :: templateId :: " + templateId + " newLevel :: " + newLevel);
			StringBuilder hql = new StringBuilder("update SupplierPerformanceTemplateCriteria i set i.level = (i.level + 1) where i.template.id = :templateId");

			if (newLevel > oldLevel && oldOrder == 0) {
				hql.append(" and i.level > :level");
			} else {
				hql.append(" and i.level >= :level");
			}

			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("templateId", templateId);
			query.setParameter("level", newLevel);
			query.executeUpdate();
		}

		// Top Level
		// Rearrange its new place - Pull up
		if (oldOrder == 0) {
			LOG.info("Enter 2 :: templateId :: " + templateId + " oldLevel :: " + oldLevel);
			StringBuilder hql = new StringBuilder("update SupplierPerformanceTemplateCriteria i set i.level = (i.level - 1) where i.level > :level and i.template.id = :templateId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("templateId", templateId);
			query.setParameter("level", oldLevel);
			query.executeUpdate();
		}

		// Child Level
		// Push down
		if (newParent != null) {
			LOG.info("Enter 3 :: templateId :: " + templateId + " newParent :: " + newParent + " newOrder " + newOrder);
			StringBuilder hql = new StringBuilder("update SupplierPerformanceTemplateCriteria i set i.order = (i.order + 1) where i.parent.id = :parent and i.template.id = :templateId");

			if (newOrder > oldOrder && oldParent.equals(newParent)) {
				hql.append(" and i.order > :order");
			} else {
				hql.append("  and i.order >= :order ");
			}
			// Rearrange its new place
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("templateId", templateId);
			query.setParameter("parent", newParent);
			query.setParameter("order", newOrder);
			query.executeUpdate();
		}

		// Child Level
		// Pull Up
		if (oldParent != null) {
			LOG.info("Enter 4 :: templateId :: " + templateId + " oldParent :: " + oldParent + " oldOrder " + oldOrder);
			StringBuilder hql = new StringBuilder("update SupplierPerformanceTemplateCriteria i set i.order = (i.order - 1) where i.parent.id = :parent and i.order > :order and i.template.id = :templateId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("templateId", templateId);
			query.setParameter("parent", oldParent);
			query.setParameter("order", oldOrder);
			query.executeUpdate();
		}

		// Fetch the parent object again as its position might have changed during above updates.
		if (newParent != null) {
			SupplierPerformanceTemplateCriteria newDbParent = findById(newParent);
			criteria.setLevel(newDbParent.getLevel());
		}

		StringBuilder hql = new StringBuilder("update SupplierPerformanceTemplateCriteria i set i.order = :order, i.level = :level, i.parent.id = :newParent where i.id = :id and i.template.id = :templateId");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("templateId", templateId);
		query.setParameter("id", criteria.getId());
		query.setParameter("newParent", newParent);
		query.setParameter("order", criteria.getOrder());
		query.setParameter("level", criteria.getLevel());
		query.executeUpdate();		
	}

}