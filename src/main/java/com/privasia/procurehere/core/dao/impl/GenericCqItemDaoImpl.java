/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

/**
 * @author Nitin Otageri
 *
 */
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.GenericCqItemDao;
import com.privasia.procurehere.core.entity.CqItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Transactional(propagation = Propagation.REQUIRED)
public class GenericCqItemDaoImpl<T extends CqItem, PK extends Serializable> extends GenericDaoImpl<T, PK> implements GenericCqItemDao<T, PK> {

	protected static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Override
	public boolean isExists(final T cqItem, String cqId, String parentId) {
		String hql = "select count(*) from " + entityClass.getName() + " c where c.cq.id = :cqId and upper(c.itemName) = :itemName";
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

	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public List<T> getCqItemsForEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " r inner join fetch r.rfxEvent re  where re.id = :id");
		query.setParameter("id", eventId);
		List<T> cqItemList = query.getResultList();
		return cqItemList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findCqItemsForCq(String cqId) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " r where r.cq.id =:cqId order by r.level, r.order");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findCqItemsForCqIds(List<String> cqIds) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " r where r.cq.id  in (:cqId) order by r.level, r.order");
		query.setParameter("cqId", cqIds);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public List<T> getCqItemLevelOrder(String cqId) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.cq c where a.parent is null and c.id = :id");
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> getCqItemsbyId(String cqId) {
		final Query query = getEntityManager().createQuery("select distinct a from " + entityClass.getName() + " a inner join fetch a.cq sp left outer join fetch a.children c where sp.id =:id and a.parent is null order by a.level, a.order");
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@Override
	public boolean updateLeadEvaluatorComment(String cqItemId, String leadComment) {
		boolean updated = false;
		Query query = getEntityManager().createQuery("update " + entityClass.getName() + " i set i.leadEvaluationComment = :leadComment where i.id = :id");
		query.setParameter("id", cqItemId);
		query.setParameter("leadComment", leadComment);
		int updateCount = query.executeUpdate();
		if (updateCount > 0) {
			updated = true;
		}
		return updated;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getCqItembyCqItemId(String id) {
		try {
			final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.cq c inner join fetch a.rfxEvent re left outer join fetch a.children ch where a.id = :id");
			query.setParameter("id", id);
			List<T> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting CQ Items : " + nr.getMessage(), nr);
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { NotAllowedException.class, Exception.class })
	public void deleteCqItems(String[] cqItemIds, String cqId) throws NotAllowedException {

		Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.rfxEvent re where a.id in (:id)");
		query.setParameter("id", Arrays.asList(cqItemIds));
		List<T> cqItems = query.getResultList();
		if (CollectionUtil.isNotEmpty(cqItems)) {
			for (String id : cqItemIds) {
				LOG.info("ID : " + id + " CqId : " + cqId);
				query = getEntityManager().createQuery("select a.level, a.order, p.id from " + entityClass.getName() + " a left outer join a.parent p where a.id = :id");
				query.setParameter("id", id);
				List<Object[]> result = query.getResultList();
				if (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) {
					int level = ((Number) result.get(0)[0]).intValue();
					int order = ((Number) result.get(0)[1]).intValue();
					String parentId = ((String) result.get(0)[2]);
					LOG.info("Reordering Level :" + level + " order :" + order + " parentId : " + parentId);

					// If Parent
					if (StringUtils.checkString(parentId).length() == 0) {
						StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.level = (i.level - 1) where i.level > :level and i.cq.id = :cqId");
						query = getEntityManager().createQuery(hql.toString());
						query.setParameter("cqId", cqId);
						query.setParameter("level", level);
						query.executeUpdate();
					} else {
						// If Child
						StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.cq.id = :cqId");
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

		query = getEntityManager().createQuery("from " + entityClass.getName() + " item  where item.id in (:id) and item.parent is not null");
		query.setParameter("id", Arrays.asList(cqItemIds));
		List<T> result = query.getResultList();
		for (T t : result) {
			getEntityManager().remove(t);
		}
		query = getEntityManager().createQuery("from " + entityClass.getName() + " item  where item.id in (:id) and item.parent is null");
		query.setParameter("id", Arrays.asList(cqItemIds));
		result = query.getResultList();
		for (T t : result) {
			getEntityManager().remove(t);
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateItemOrder(String cqId, T cqItem, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel) {
		LOG.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		LOG.info("UPdateing ITEM ORDERE ==> oldParent " + oldParent + " newParent " + newParent + " oldOrder " + oldOrder + " newOrder " + newOrder + " oldLevel " + oldLevel + " newLevel" + newLevel + " Id : " + cqItem.getId());
		LOG.info("After set CQ Level :: " + cqItem.getLevel());
		// Top Level
		// Rearrange its new place - Push down
		if (newOrder == 0) {
			LOG.info("Enter 1 :: cqId :: " + cqId + " newLevel :: " + newLevel);
			StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.level = (i.level + 1) where i.cq.id = :cqId");

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
			StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.level = (i.level - 1) where i.level > :level and i.cq.id = :cqId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("cqId", cqId);
			query.setParameter("level", oldLevel);
			query.executeUpdate();
		}

		// Child Level
		// Push down
		if (newParent != null) {
			LOG.info("Enter 3 :: cqId :: " + cqId + " newParent :: " + newParent + " newOrder " + newOrder);
			StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.order = (i.order + 1) where i.parent.id = :parent and i.cq.id = :cqId");

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
			StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.order = (i.order - 1) where i.parent.id = :parent and i.order > :order and i.cq.id = :cqId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("cqId", cqId);
			query.setParameter("parent", oldParent);
			query.setParameter("order", oldOrder);
			query.executeUpdate();
		}

		// Fetch the parent object again as its position might have changed during above updates.
		if (newParent != null) {
			T newDbParent = getCqItembyCqItemId(newParent);
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
	public T getCqItemByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.cq c inner join fetch a.rfxEvent re where re.id = :id");
			query.setParameter("id", eventId);
			List<T> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting CQ Items : " + nr.getMessage(), nr);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAllCqItemsbycqId(String cqId) {
		final Query query = getEntityManager().createQuery("select distinct a from " + entityClass.getName() + " a inner join fetch a.cq sp left outer join fetch a.children c where sp.id =:id  order by a.level, a.order");
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getParentbyLevelId(String cqId, Integer level) {
		try {
			final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.cq as b where b.id = :cqId and a.level = :level and a.order = 0 ");
			query.setParameter("cqId", cqId);
			query.setParameter("level", level);
			List<T> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting BQ Items : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	public String getLeadEvaluatorComment(String cqItemId) {
		final Query query = getEntityManager().createQuery("select i.leadEvaluationComment from " + entityClass.getName() + " i where i.id = :id ");
		query.setParameter("id", cqItemId);
		return (String) query.getSingleResult();
	}

}
