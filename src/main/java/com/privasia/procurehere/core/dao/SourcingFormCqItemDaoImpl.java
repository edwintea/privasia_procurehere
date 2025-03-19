package com.privasia.procurehere.core.dao;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.impl.GenericCqItemDaoImpl;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author sarang
 */
@Repository
public class SourcingFormCqItemDaoImpl extends GenericCqItemDaoImpl<SourcingTemplateCqItem, String> implements SourcingFormCqItemDao {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Override
	public boolean isCqExists(String name, String name2) {

		return false;
	}

	/**
	 * @param name
	 * @param name2
	 * @return
	 */

	/**
	 * @param formId
	 * @return
	 */

	@Override
	public SourcingTemplateCqItem getCqItembyItemId(String itemId) {

		final Query query = getEntityManager().createQuery("select s from SourcingTemplateCqItem s inner join fetch s.cq cq inner join fetch s.sourcingForm sf left outer join fetch s.children eo where s.id=:itemId");
		query.setParameter("itemId", itemId);
		return (SourcingTemplateCqItem) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestCqItem> getSourcingFormRequestCqItemByFormId(String id) {
		String sql = "from SourcingFormRequestCqItem s where s.sourcingForm.id=:id";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { NotAllowedException.class, Exception.class })
	public void deleteCqItemss(String[] cqItemIds, String cqId) throws NotAllowedException {

		Query query = getEntityManager().createQuery("from SourcingTemplateCqItem a where  a.id in (:id)");
		query.setParameter("id", Arrays.asList(cqItemIds));
		List<SourcingTemplateCqItem> cqItems = query.getResultList();
		if (CollectionUtil.isNotEmpty(cqItems)) {
			for (String id : cqItemIds) {
				LOG.info("ID : " + id + " CqId : " + cqId);
				query = getEntityManager().createQuery("select a.level, a.order, p.id from SourcingTemplateCqItem a left outer join a.parent p where a.id = :id");
				query.setParameter("id", id);
				List<Object[]> result = query.getResultList();
				if (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) {
					int level = ((Number) result.get(0)[0]).intValue();
					int order = ((Number) result.get(0)[1]).intValue();
					String parentId = ((String) result.get(0)[2]);
					LOG.info("Reordering Level :" + level + " order :" + order + " parentId : " + parentId);

					// If Parent
					if (StringUtils.checkString(parentId).length() == 0) {
						StringBuilder hql = new StringBuilder("update SourcingTemplateCqItem i set i.level = (i.level - 1) where i.level > :level and i.cq.id = :cqId");
						query = getEntityManager().createQuery(hql.toString());
						query.setParameter("cqId", cqId);
						query.setParameter("level", level);
						query.executeUpdate();
					} else {
						// If Child
						StringBuilder hql = new StringBuilder("update SourcingTemplateCqItem i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.cq.id = :cqId");
						query = getEntityManager().createQuery(hql.toString());
						query.setParameter("cqId", cqId);
						query.setParameter("level", level);
						query.setParameter("order", order);
						query.setParameter("parentId", parentId);
						query.executeUpdate();
					}
				}
			}
		}

		query = getEntityManager().createQuery("from SourcingTemplateCqItem item  where item.id in (:id) and item.parent is not null");
		query.setParameter("id", Arrays.asList(cqItemIds));
		List<SourcingTemplateCqItem> result = query.getResultList();
		for (SourcingTemplateCqItem t : result) {
			getEntityManager().remove(t);
		}
		query = getEntityManager().createQuery("from SourcingTemplateCqItem item  where item.id in (:id) and item.parent is null");
		query.setParameter("id", Arrays.asList(cqItemIds));
		result = query.getResultList();
		for (SourcingTemplateCqItem t : result) {
			getEntityManager().remove(t);
		}
	}

	@Override
	public boolean isExistsItem(SourcingTemplateCqItem cqItem, String cqId, String parentId) {
		String hql = "select count(*) from SourcingTemplateCqItem c where c.cq.id = :cqId and upper(c.itemName) = :itemName";
		if (StringUtils.checkString(cqItem.getId()).length() > 0) {
			hql += " and c.id <> :id";
		}
		if (StringUtils.checkString(parentId).length() > 0) {
			hql += " and c.parent.id = :parentId ";
		} else {
			hql += " and c.parent is null";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("itemName", cqItem.getItemName().toUpperCase());
		query.setParameter("cqId", cqId);
		if (StringUtils.checkString(cqItem.getId()).length() > 0) {
			query.setParameter("id", cqItem.getId());
		}
		if (StringUtils.checkString(parentId).length() > 0) {
			query.setParameter("parentId", parentId);
		}
	
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateRequestItemOrder(String cqId, SourcingTemplateCqItem cqItem, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel) {
	
		LOG.info("UPdateing ITEM ORDERE ==> oldParent " + oldParent + " newParent " + newParent + " oldOrder " + oldOrder + " newOrder " + newOrder + " oldLevel " + oldLevel + " newLevel" + newLevel + " Id : " + cqItem.getId());
		LOG.info("After set CQ Level :: " + cqItem.getLevel());
		// Top Level
		// Rearrange its new place - Push down
		if (newOrder == 0) {
			LOG.info("Enter 1 :: cqId :: " + cqId + " newLevel :: " + newLevel);
			StringBuilder hql = new StringBuilder("update SourcingTemplateCqItem i set i.level = (i.level + 1) where i.cq.id = :cqId");

			if (newLevel > oldLevel && oldOrder == 0) {
				hql.append(" and i.level > :level");
			} else {
				hql.append(" and i.level >= :level");
			}

			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("cqId", cqId);
			query.setParameter("level", newLevel);
			query.executeUpdate();
		}

		// Top Level
		// Rearrange its new place - Pull up
		if (oldOrder == 0) {
			LOG.info("Enter 2 :: cqId :: " + cqId + " oldLevel :: " + oldLevel);
			StringBuilder hql = new StringBuilder("update SourcingTemplateCqItem i set i.level = (i.level - 1) where i.level > :level and i.cq.id = :cqId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("cqId", cqId);
			query.setParameter("level", oldLevel);
			query.executeUpdate();
		}

		// Child Level
		// Push down
		if (newParent != null) {
			LOG.info("Enter 3 :: cqId :: " + cqId + " newParent :: " + newParent + " newOrder " + newOrder);
			StringBuilder hql = new StringBuilder("update SourcingTemplateCqItem i set i.order = (i.order + 1) where i.parent.id = :parent and i.cq.id = :cqId");

			if (newOrder > oldOrder && oldParent.equals(newParent)) {
				hql.append(" and i.order > :order");
			} else {
				hql.append("  and i.order >= :order ");
			}
			// Rearrange its new place
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("cqId", cqId);
			query.setParameter("parent", newParent);
			query.setParameter("order", newOrder);
			query.executeUpdate();
			LOG.info(" child push down hql :" + hql);
		}

		// Child Level
		// Pull Up
		if (oldParent != null) {
			LOG.info("Enter 4 :: cqId :: " + cqId + " oldParent :: " + oldParent + " oldOrder " + oldOrder);
			StringBuilder hql = new StringBuilder("update SourcingTemplateCqItem i set i.order = (i.order - 1) where i.parent.id = :parent and i.order > :order and i.cq.id = :cqId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("cqId", cqId);
			query.setParameter("parent", oldParent);
			query.setParameter("order", oldOrder);
			query.executeUpdate();
		}

		// Fetch the parent object again as its position might have changed during above updates.
		if (newParent != null) {
			SourcingTemplateCqItem newDbParent = findById(newParent);
			LOG.info("newDbParent.getLevel() :" + newDbParent.getLevel());
			cqItem.setLevel(newDbParent.getLevel());
		}

		StringBuilder hql = new StringBuilder("update " + entityClass.getSimpleName() + " i set i.order = :order, i.level = :level, i.parent.id = :newParent where i.id = :id");
		LOG.info("HQL : " + hql.toString());
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", cqItem.getId());
		query.setParameter("newParent", newParent);
		query.setParameter("level", cqItem.getLevel());
		query.setParameter("order", cqItem.getOrder());
		query.executeUpdate();

		LOG.info("After Update All CQ Orders CQITEM Data :: " + cqItem.toLogString());
		LOG.info("Updated successfully");
		LOG.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingTemplateCqItem> getAllSourcingCqItemsbycqId(String cqId) {
		final Query query = getEntityManager().createQuery("select distinct a from SourcingTemplateCqItem a inner join fetch a.cq sp left outer join fetch a.children c where sp.id =:id  and a.parent is null order by a.level, a.order");
		query.setParameter("id", cqId);
		return query.getResultList();
	}

}
