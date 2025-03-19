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

import com.privasia.procurehere.core.dao.GenericBqItemDao;
import com.privasia.procurehere.core.entity.BqItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Transactional(propagation = Propagation.REQUIRED)
public class GenericBqItemDaoImpl<T extends BqItem, PK extends Serializable> extends GenericDaoImpl<T, String> implements GenericBqItemDao<T, String> {

	private static final Logger LOG = LogManager.getLogger(GenericDaoImpl.class);

	@Autowired
	MessageSource messageSource;

	@Override
	public boolean isExists(final T bqItem, String bqId, String parentId) {
		LOG.info("parentId :" + parentId + ", Name : " + bqItem.getItemName() + ", bqId : " + bqId);
		String hql = "select count(*) from " + entityClass.getName() + " b where b.bq.id = :bqId and upper(b.itemName) = :itemName";
		if (StringUtils.checkString(bqItem.getId()).length() > 0) {
			hql += " and b.id <> :id";
		}
		if (StringUtils.checkString(parentId).length() > 0) {
			hql += " and b.parent.id = :parentId ";
		} else {
			hql += " and b.parent is null";
		}
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("itemName", StringUtils.checkString(bqItem.getItemName()).toUpperCase());
		query.setParameter("bqId", bqId);
		if (StringUtils.checkString(bqItem.getId()).length() > 0) {
			query.setParameter("id", bqItem.getId());
		}
		if (StringUtils.checkString(parentId).length() > 0) {
			query.setParameter("parentId", parentId);
		}
		LOG.info("hql :" + hql);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { NotAllowedException.class, Exception.class })
	public String deleteBqItems(String[] bqItemIds, String bqId) throws NotAllowedException {
		Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.bq re where a.id in (:id)");
		query.setParameter("id", Arrays.asList(bqItemIds));
		List<T> bqItems = query.getResultList();

		if (CollectionUtil.isNotEmpty(bqItems)) {
			for (String id : bqItemIds) {
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
						StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.level = (i.level - 1) where i.level > :level and i.bq.id = :bqId");
						query = getEntityManager().createQuery(hql.toString());
						query.setParameter("bqId", bqId);
						query.setParameter("level", level);
						query.executeUpdate();
					} else {
						// If Child
						StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.bq.id = :bqId");
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

		query = getEntityManager().createQuery("delete from " + entityClass.getName() + " item where item.id in (:id) and item.parent is not null");
		query.setParameter("id", Arrays.asList(bqItemIds));
		query.executeUpdate();

		query = getEntityManager().createQuery("delete from " + entityClass.getName() + " item where item.id in (:id) and item.parent is null");
		query.setParameter("id", Arrays.asList(bqItemIds));
		query.executeUpdate();

		return bqId;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateItemOrder(final T bqItem, String bqId, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel) {
		LOG.info("UPdateing ITEM ORDERE ==> oldParent " + oldParent + " newParent " + newParent + " oldOrder " + oldOrder + " newOrder " + newOrder + " oldLevel " + oldLevel + " newLevel" + newLevel);

		// Top Level
		// Rearrange its new place - Push down
		if (newOrder == 0) {
			LOG.info("Enter 1 :: bqId :: " + bqId + " newLevel :: " + newLevel);
			StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.level = (i.level + 1) where i.bq.id = :bqId");

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
			StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.level = (i.level - 1) where i.level > :level and i.bq.id = :bqId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("bqId", bqId);
			query.setParameter("level", oldLevel);
			query.executeUpdate();
		}

		// Child Level
		// Push down
		if (newParent != null) {
			LOG.info("Enter 3 :: bqId :: " + bqId + " newParent :: " + newParent + " newOrder " + newOrder);
			StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.order = (i.order + 1) where i.parent.id = :parent and i.bq.id = :bqId");

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
			LOG.info("Enter 4 :: bqId :: " + bqId + " oldParent :: " + oldParent + " oldOrder " + oldOrder);
			StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.order = (i.order - 1) where i.parent.id = :parent and i.order > :order and i.bq.id = :bqId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("bqId", bqId);
			query.setParameter("parent", oldParent);
			query.setParameter("order", oldOrder);
			query.executeUpdate();
		}

		// Fetch the parent object again as its position might have changed during above updates.
		if (newParent != null) {
			T newDbParent = getBqItembyBqItemId(newParent);
			bqItem.setLevel(newDbParent.getLevel());
		}

		StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.order = :order, i.level = :level, i.parent.id = :newParent where i.id = :bqItemId and i.bq.id = :bqId");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("bqId", bqId);
		query.setParameter("bqItemId", bqItem.getId());
		query.setParameter("newParent", newParent);
		query.setParameter("order", bqItem.getOrder());
		query.setParameter("level", bqItem.getLevel());
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getBqItemLevelOrder(String bqId) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.bq c where a.parent is null and c.id = :id");
		query.setParameter("id", bqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getBqItemByBqIdAndBqItemId(String bqItemId) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.bq c where a.id = :bqItemId");
		query.setParameter("bqItemId", bqItemId);
		List<T> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getBqItembyBqItemId(String id) {
		try {
			final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.bq c inner join fetch a.rfxEvent re left outer join fetch a.children ch where a.id = :id");
			query.setParameter("id", id);
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
	public void deletefieldInBqItems(String bqId, String label) {
		try {
			LOG.info("BQ ID :: " + bqId + " label :: " + label);
			StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i." + label + " = null where i.bq.id = :bqId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("bqId", bqId);
			query.executeUpdate();

		} catch (NoResultException nr) {
			LOG.info("Error while deleting new Fields: " + nr.getMessage(), nr);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findBqItemListByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.rftEvent as re where re.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getListBqItemsbyId(String bqId) {
		final Query query = getEntityManager().createQuery("select distinct a from " + entityClass.getName() + " a inner join fetch a.bq as b left outer join fetch a.children c left outer join fetch a.uom uom where a.parent is null and b.id =:id order by a.level, a.order");
		query.setParameter("id", bqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getParentbyLevelId(String bqId, Integer level) {
		try {
			final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.bq as b where b.id = :bqId and a.level = :level and a.order = 0 ");
			query.setParameter("bqId", bqId);
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

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAllBqItemsbybqId(String bqId) {
		final Query query = getEntityManager().createQuery("select distinct a from " + entityClass.getName() + " a inner join fetch a.bq bq left outer join fetch a.children c left outer join fetch a.uom uom where bq.id =:id order by a.level, a.order");
		query.setParameter("id", bqId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { NotAllowedException.class, Exception.class })
	public void deleteBqItemsbyBqid(String bqId) {
		Query query = getEntityManager().createQuery("delete from " + entityClass.getName() + " item where item.bq.id = :id and item.parent is not null");
		query.setParameter("id", bqId);
		query.executeUpdate();

		query = getEntityManager().createQuery("delete from " + entityClass.getName() + " item where item.bq.id = :id and item.parent is null");
		query.setParameter("id", bqId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findBqItemsForBq(String bqId) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " r where r.bq.id =:bqId order by r.level, r.order");
		query.setParameter("bqId", bqId);
		return query.getResultList();
	}

	@Override
	public void resetFieldsForFilledBySupplier(String id, List<String> label) {

		if (label == null || (label != null && label.size() == 0)) {
			return;
		}

		String cols = "";
		for (String string : label) {
			cols += " i." + string + " = null,";
		}
		if (cols.length() > 0) {
			cols = cols.substring(0, cols.length() - 1);
		}

		StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set " + cols + " where i.bq.id = :id");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", id);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { NotAllowedException.class, Exception.class })
	public void deleteBqItemsForEventId(String eventId) {
		Query query = getEntityManager().createQuery("delete from " + entityClass.getName() + " item where item.rfxEvent.id = :id");
		query.setParameter("id", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length) {
		LOG.info(" id :" + bqId + "  itemLevel  : " + itemLevel + " itemOrder : " + itemOrder + " searchVal :" + searchVal + " start :" + start + " length :" + length);
		try {
			StringBuilder hql = new StringBuilder("select distinct a from " + entityClass.getName() + " a inner join fetch a.bq bq left outer join fetch a.children c left outer join fetch a.parent p left outer join fetch a.uom uom where bq.id =:id ");

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

			List<T> uList = query.getResultList();

			return uList;
		} catch (NoResultException nr) {
			LOG.info("Error while getting BQ Items : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String searchVal) {
		LOG.info(" bqId  :" + bqId);
		StringBuilder hql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.BqItemPojo(a.level, a.order) from " + entityClass.getName() + " a where a.bq.id =:bqId ");
		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			hql.append(" and (upper(a.itemName) like :searchValue or upper(a.itemDescription) like :searchValue) ");
		}
		hql.append(" order by a.level, a.order ");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("bqId", bqId);
		if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
			query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
		}
		List<BqItemPojo> list = query.getResultList();
		return list;
	}

	@Override
	public long totalBqItemCountByBqId(String bqId, String searchVal) {
		StringBuilder hql = new StringBuilder("select count(item) from " + entityClass.getName() + " item where item.bq.id =:bqId ");
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
	@Transactional(readOnly = false, rollbackFor = { NotAllowedException.class, Exception.class })
	public void deleteBqItemsForErpForEventId(String eventId) {
		Query query = getEntityManager().createQuery("delete from " + entityClass.getName() + " item where item.rfxEvent.id = :id and item.parent is not null ");
		query.setParameter("id", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("delete from " + entityClass.getName() + " item where item.rfxEvent.id = :id and item.parent is null ");
		query.setParameter("id", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteChildItems(String parentItemId, String bqId) throws NotAllowedException {
		Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.bq re where a.id in (:id)");

		query = getEntityManager().createQuery("select a.level, a.order, p.id from " + entityClass.getName() + " a left outer join a.parent p where a.id = :id");
		query.setParameter("id", parentItemId);
		List<Object[]> result = query.getResultList();
		if (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) {
			int level = ((Number) result.get(0)[0]).intValue();
			int order = ((Number) result.get(0)[1]).intValue();
			String parentId = ((String) result.get(0)[2]);
			LOG.info("Reordering Level :" + level + " order :" + order + " parentId : " + parentId);

			// If Parent
			if (StringUtils.checkString(parentId).length() == 0) {
				StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.level = (i.level - 1) where i.level > :level and i.bq.id = :bqId");
				query = getEntityManager().createQuery(hql.toString());
				query.setParameter("bqId", bqId);
				query.setParameter("level", level);
				query.executeUpdate();
			} else {
				// If Child
				StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.bq.id = :bqId");
				query = getEntityManager().createQuery(hql.toString());
				query.setParameter("bqId", bqId);
				query.setParameter("level", level);
				query.setParameter("order", order);
				query.setParameter("parentId", parentId);
				query.executeUpdate();
			}
		}

		LOG.info("Going to Delete the selected BQ Items");
		query = getEntityManager().createQuery("delete from " + entityClass.getName() + " item where item.parent.id = :id and item.parent is not null");
		query.setParameter("id", parentItemId);
		query.executeUpdate();

		query = getEntityManager().createQuery("delete from " + entityClass.getName() + " item where item.id = :id and item.parent is null");
		query.setParameter("id", parentItemId);
		query.executeUpdate();

		LOG.info("COMPLETED TO DELETE the Selected BQ ITems");
	}

	@Override
	public void deleteRemaingBqItems(String[] bqItemIds, String bqId) throws NotAllowedException {

		Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.bq re where a.id in (:id)");
		query.setParameter("id", Arrays.asList(bqItemIds));
		List<T> bqItems = query.getResultList();

		if (CollectionUtil.isNotEmpty(bqItems)) {
			for (String id : bqItemIds) {
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
						StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.level = (i.level - 1) where i.level > :level and i.bq.id = :bqId");
						query = getEntityManager().createQuery(hql.toString());
						query.setParameter("bqId", bqId);
						query.setParameter("level", level);
						query.executeUpdate();
					} else {
						// If Child
						StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.bq.id = :bqId");
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

		query = getEntityManager().createQuery("delete from " + entityClass.getName() + " item where item.id in (:id)");
		query.setParameter("id", Arrays.asList(bqItemIds));
		query.executeUpdate();

	}
}
