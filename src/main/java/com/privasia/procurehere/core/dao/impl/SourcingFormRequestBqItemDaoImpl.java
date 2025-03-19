
package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SourcingFormRequestBqItemDao;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;
import com.privasia.procurehere.core.pojo.SourcingBqItemPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Pooja
 */
@Repository
public class SourcingFormRequestBqItemDaoImpl extends GenericDaoImpl<SourcingFormRequestBqItem, String> implements SourcingFormRequestBqItemDao {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length) {
		try {
			StringBuilder hql = new StringBuilder("select distinct a from SourcingFormRequestBqItem a inner join fetch a.bq bq left outer join fetch a.children c left outer join fetch a.parent p left outer join fetch a.uom uom where bq.id =:id ");

			if (itemLevel != null && itemOrder != null) {
				hql.append(" and a.level =:itemLevel and a.order =:itemOrder ");
			}
			if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
				hql.append(" and (upper(a.itemName) like :searchValue or upper(a.itemDescription) like :searchValue) ");
			}
			hql.append(" order by a.level, a.order ");

			final Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("id", bqId);
			if (itemLevel != null && itemOrder != null) {
				query.setParameter("itemLevel", itemLevel);
				query.setParameter("itemOrder", itemOrder);
			}
			if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
				query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
			}

			if (start != null && length != null) {
				query.setFirstResult(start);
				query.setMaxResults(length);
			}

			List<SourcingFormRequestBqItem> uList = query.getResultList();

			return uList;
		} catch (NoResultException nr) {
			LOG.info("Error while getting sourcing BQ Items : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingBqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String searchVal) {
		StringBuilder hql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.SourcingBqItemPojo(a.level, a.order) from SourcingFormRequestBqItem a where a.bq.id =:bqId ");
		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			hql.append(" and (upper(a.itemName) like :searchValue or upper(a.itemDescription) like :searchValue) ");
		}
		hql.append(" order by a.level, a.order ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("bqId", bqId);
		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
		}
		List<SourcingBqItemPojo> list = query.getResultList();
		return list;

	}

	@Override
	public long getTotalBqItemCountByBqId(String bqId, String searchVal) {
		StringBuilder hql = new StringBuilder("select count(item) from SourcingFormRequestBqItem item where item.bq.id =:bqId ");
		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			hql.append(" and (upper(item.itemName) like :searchValue or upper(item.itemDescription) like :searchValue) ");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("bqId", bqId);
		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
		}
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public boolean isExist(SourcingFormRequestBqItem sourcingBqItem, String bqId, String parentId) {
		String hql = "select count(*) from SourcingFormRequestBqItem b where b.bq.id = :bqId and upper(b.itemName) = :itemName";
		if (StringUtils.checkString(sourcingBqItem.getId()).length() > 0) {
			hql += " and b.id <> :id";
		}
		if (StringUtils.checkString(parentId).length() > 0) {
			hql += " and b.parent.id = :parentId ";
		} else {
			hql += " and b.parent is null";
		}
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("itemName", StringUtils.checkString(sourcingBqItem.getItemName()).toUpperCase());
		query.setParameter("bqId", bqId);
		if (StringUtils.checkString(sourcingBqItem.getId()).length() > 0) {
			query.setParameter("id", sourcingBqItem.getId());
		}
		if (StringUtils.checkString(parentId).length() > 0) {
			query.setParameter("parentId", parentId);
		}
		LOG.info("hql :" + hql);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String deleteBqItems(String[] bqItemsIds, String bqId) {
		Query query = getEntityManager().createQuery("from SourcingFormRequestBqItem b inner join fetch b.bq bq where b.id in(:id )");
		query.setParameter("id", Arrays.asList(bqItemsIds));
		List<SourcingFormRequestBqItem> bqItemist = query.getResultList();
		if (CollectionUtil.isNotEmpty(bqItemist)) {
			for (String id : bqItemsIds) {
				query = getEntityManager().createQuery("select a.level, a.order, p.id from SourcingFormRequestBqItem  a left outer join a.parent p where a.id = :id");
				query.setParameter("id", id);
				List<Object[]> result = query.getResultList();
				if (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) {
					int level = ((Number) result.get(0)[0]).intValue();
					int order = ((Number) result.get(0)[1]).intValue();
					String parentId = ((String) result.get(0)[2]);

					// If Parent
					if (StringUtils.checkString(parentId).length() == 0) {
						StringBuilder hql = new StringBuilder("update SourcingFormRequestBqItem i set i.level = (i.level - 1) where i.level > :level and i.bq.id = :bqId");
						query = getEntityManager().createQuery(hql.toString());
						query.setParameter("bqId", bqId);
						query.setParameter("level", level);
						query.executeUpdate();
					} else {
						// If Child
						StringBuilder hql = new StringBuilder("update SourcingFormRequestBqItem i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.bq.id = :bqId");
						query = getEntityManager().createQuery(hql.toString());
						query.setParameter("bqId", bqId);
						query.setParameter("level", level);
						query.setParameter("order", order);
						query.setParameter("parentId", parentId);
						query.executeUpdate();
					}
				}
			}
		}
		query = getEntityManager().createQuery("delete from SourcingFormRequestBqItem item where item.id in(:id) and item.parent is not null");
		query.setParameter("id", Arrays.asList(bqItemsIds));
		query.executeUpdate();

		query = getEntityManager().createQuery("delete from SourcingFormRequestBqItem item where item.id in(:id) and item.parent is null");
		query.setParameter("id", Arrays.asList(bqItemsIds));
		query.executeUpdate();
		return bqId;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateItemOrder(final SourcingFormRequestBqItem bqItem, String bqId, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel) {
		// Top Level
		// Rearrange its new place - Push down
		if (newOrder == 0) {
			StringBuilder hql = new StringBuilder("update SourcingFormRequestBqItem i set i.level = (i.level + 1) where i.bq.id = :bqId");

			if (newLevel > oldLevel && oldOrder == 0) {
				hql.append(" and i.level > :level");
			} else {
				hql.append(" and i.level >= :level");
			}

			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("bqId", bqId);
			query.setParameter("level", newLevel);
			query.executeUpdate();
		}

		// Top Level
		// Rearrange its new place - Pull up
		if (oldOrder == 0) {
			LOG.info("Enter 2 :: bqId :: " + bqId + " oldLevel :: " + oldLevel);
			StringBuilder hql = new StringBuilder("update SourcingFormRequestBqItem i set i.level = (i.level - 1) where i.level > :level and i.bq.id = :bqId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("bqId", bqId);
			query.setParameter("level", oldLevel);
			query.executeUpdate();
		}

		// Child Level
		// Push down
		if (newParent != null && oldParent != null) {

			StringBuilder hql = new StringBuilder("update SourcingFormRequestBqItem i set i.order = (i.order + 1) where i.parent.id = :parent and i.bq.id = :bqId");

			if (newOrder > oldOrder && oldParent.equals(newParent)) {
				hql.append(" and i.order > :order");
			} else {
				hql.append("  and i.order >= :order ");
			}
			// Rearrange its new place
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("bqId", bqId);
			query.setParameter("parent", newParent);
			query.setParameter("order", newOrder);
			query.executeUpdate();
		}

		// Child Level
		// Pull Up
		if (oldParent != null) {
			StringBuilder hql = new StringBuilder("update SourcingFormRequestBqItem i set i.order = (i.order - 1) where i.parent.id = :parent and i.order > :order and i.bq.id = :bqId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("bqId", bqId);
			query.setParameter("parent", oldParent);
			query.setParameter("order", oldOrder);
			query.executeUpdate();
		}

		// Fetch the parent object again as its position might have changed during above updates.
		if (newParent != null) {
			SourcingFormRequestBqItem newDbParent = getBqItemByBqItemId(newParent);
			bqItem.setLevel(newDbParent.getLevel());
		}

		StringBuilder hql = new StringBuilder("update SourcingFormRequestBqItem i set i.order = :order, i.level = :level, i.parent.id = :newParent where i.id = :bqItemId and i.bq.id = :bqId");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("bqId", bqId);
		query.setParameter("bqItemId", bqItem.getId());
		query.setParameter("newParent", newParent);
		query.setParameter("order", bqItem.getOrder());
		query.setParameter("level", bqItem.getLevel());
		query.executeUpdate();

		// bqItem = update(bqItem);

	}

	@SuppressWarnings("unchecked")
	@Override
	public SourcingFormRequestBqItem getBqItemByBqItemId(String bqItemId) {
		final Query query = getEntityManager().createQuery("from  SourcingFormRequestBqItem b inner join fetch b.bq bq where b.id=:bqItemId");
		query.setParameter("bqItemId", bqItemId);
		List<SourcingFormRequestBqItem> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestBqItem> getBqItemLevelOrder(String bqId) {
		final Query query = getEntityManager().createQuery("from SourcingFormRequestBqItem  a inner join fetch a.bq c where a.parent is null and c.id = :bqId");
		query.setParameter("bqId", bqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestBqItem> getAllbqItemByBqId(String id) {
		String hql = "select distinct item from SourcingFormRequestBq s inner join  s.bqItems item  where  s.id=:id order by item.level, item.order";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SourcingFormRequestBq getAllbqItemsByBqId(String id) {
		String hql = "from SourcingFormRequestBq s left outer join fetch s.bqItems item where s.id=:id";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		return (SourcingFormRequestBq) query.getSingleResult();
	}

	@Override
	public void deleteBqItemsbyBqid(String bqId) {
		Query query = getEntityManager().createQuery("delete from SourcingFormRequestBqItem item where item.bq.id = :id and item.parent is not null");
		query.setParameter("id", bqId);
		query.executeUpdate();

		query = getEntityManager().createQuery("delete from SourcingFormRequestBqItem item where item.bq.id = :id and item.parent is null");
		query.setParameter("id", bqId);
		query.executeUpdate();
	}

	@Override
	public SourcingFormRequestBqItem getParentbyLevelId(String bqId, Integer level) {
		try {
			final Query query = getEntityManager().createQuery("from SourcingFormRequestBqItem a inner join fetch a.bq as b where b.id = :bqId and a.level = :level and a.order = 0 ");
			query.setParameter("bqId", bqId);
			query.setParameter("level", level);
			List<SourcingFormRequestBqItem> uList = query.getResultList();
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

}