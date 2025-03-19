package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SupplierFormItemDao;
import com.privasia.procurehere.core.entity.SupplierFormItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository("supplierFormItemDao")
public class SupplierFormItemDaoImpl extends GenericDaoImpl<SupplierFormItem, String> implements SupplierFormItemDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormItem> getFormItemLevelOrder(String id) {
		final Query query = getEntityManager().createQuery("from SupplierFormItem a inner join fetch a.supplierForm sf where a.parent is null and sf.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public boolean isExists(SupplierFormItem supplierFormItem, String formId, String parentId) {
		String hql = "select count(*) from SupplierFormItem s where s.supplierForm.id = :formId and upper(s.itemName) = :itemName";
		if (StringUtils.checkString(supplierFormItem.getId()).length() > 0) {
			hql += " and s.id <> :id";
		}
		if (StringUtils.checkString(parentId).length() > 0) {
			hql += " and s.parent.id = :parentId ";
		} else {
			hql += " and s.parent is null";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("itemName", supplierFormItem.getItemName().toUpperCase());
		query.setParameter("formId", formId);
		if (StringUtils.checkString(supplierFormItem.getId()).length() > 0) {
			query.setParameter("id", supplierFormItem.getId());
		}
		if (StringUtils.checkString(parentId).length() > 0) {
			query.setParameter("parentId", parentId);
		}

		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierFormItem getFormItembyFormItemId(String itemId) {
		final Query query = getEntityManager().createQuery("from SupplierFormItem a inner join fetch a.supplierForm c  left outer join fetch a.children ch where a.id = :id");
		query.setParameter("id", itemId);
		List<SupplierFormItem> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormItem> getFormItemsbyFormId(String formId) {
		final Query query = getEntityManager().createQuery("select distinct a from SupplierFormItem  a inner join fetch a.supplierForm sp left outer join fetch a.children c where sp.id =:id and a.parent is null order by a.level, a.order");
		query.setParameter("id", formId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { NotAllowedException.class, Exception.class })
	public void deleteFormItems(String[] formItemIds, String formId) throws NotAllowedException {
		Query query = getEntityManager().createQuery("from SupplierFormItem a where a.id in (:id)");
		query.setParameter("id", Arrays.asList(formItemIds));
		List<SupplierFormItem> formItems = query.getResultList();
		if (CollectionUtil.isNotEmpty(formItems)) {
			for (String id : formItemIds) {
				LOG.info("ID : " + id + " formId : " + formId);
				query = getEntityManager().createQuery("select a.level, a.order, p.id from SupplierFormItem a left outer join a.parent p where a.id = :id");
				query.setParameter("id", id);
				List<Object[]> result = query.getResultList();
				if (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) {
					int level = ((Number) result.get(0)[0]).intValue();
					int order = ((Number) result.get(0)[1]).intValue();
					String parentId = ((String) result.get(0)[2]);
					LOG.info("Reordering Level :" + level + " order :" + order + " parentId : " + parentId);

					// If Parent
					if (StringUtils.checkString(parentId).length() == 0) {

						StringBuilder hql = new StringBuilder("update SupplierFormItem i set i.level = (i.level - 1) where i.level > :level and i.supplierForm.id = :formId");
						query = getEntityManager().createQuery(hql.toString());
						query.setParameter("formId", formId);
						query.setParameter("level", level);
						query.executeUpdate();
					} else {
						StringBuilder hql = new StringBuilder("update SupplierFormItem i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.supplierForm.id = :formId ");
						query = getEntityManager().createQuery(hql.toString());
						query.setParameter("formId", formId);
						query.setParameter("level", level);
						query.setParameter("order", order);
						query.setParameter("parentId", parentId);
						query.executeUpdate();
					}
				}
			}
		}

		query = getEntityManager().createQuery(" from SupplierFormItem item  where item.id in (:id) and item.parent is not null");
		query.setParameter("id", Arrays.asList(formItemIds));
		List<SupplierFormItem> result = query.getResultList();
		for (SupplierFormItem t : result) {
			getEntityManager().remove(t);
		}
		query = getEntityManager().createQuery(" from SupplierFormItem item  where item.id in (:id) and item.parent is null");
		query.setParameter("id", Arrays.asList(formItemIds));
		result = query.getResultList();
		for (SupplierFormItem t : result) {
			getEntityManager().remove(t);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormItem> getAllFormitemsbyFormId(String formId) {
		final Query query = getEntityManager().createQuery("select distinct a from SupplierFormItem a inner join fetch a.supplierForm sp left outer join fetch a.children c where sp.id =:formId  order by a.level, a.order");
		query.setParameter("formId", formId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateItemOrder(String formId, SupplierFormItem formItem, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel) {
		if (newOrder == 0) {
			StringBuilder hql = new StringBuilder("update SupplierFormItem  i set i.level = (i.level + 1) where i.supplierForm.id= :formId");
			if (newLevel > oldLevel && oldOrder == 0) {
				hql.append(" and i.level > :level");
			} else {
				hql.append(" and i.level >= :level");
			}

			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("formId", formId);
			query.setParameter("level", newLevel);
			query.executeUpdate();
		}
		if (oldOrder == 0) {
			StringBuilder hql = new StringBuilder("update SupplierFormItem i set i.level = (i.level - 1) where i.level > :level and i.supplierForm.id = :formId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("formId", formId);
			query.setParameter("level", oldLevel);
			query.executeUpdate();
		}
		if (newParent != null) {
			StringBuilder hql = new StringBuilder("update SupplierFormItem i set i.order = (i.order + 1) where i.parent.id = :parent and i.supplierForm.id = :formId");

			if (newOrder > oldOrder && oldParent.equals(newParent)) {
				hql.append(" and i.order > :order");
			} else {
				hql.append("  and i.order >= :order ");
			}
			// Rearrange its new place
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("formId", formId);
			query.setParameter("parent", newParent);
			query.setParameter("order", newOrder);
			query.executeUpdate();
		}
		if (oldParent != null) {
			StringBuilder hql = new StringBuilder("update SupplierFormItem i set i.order = (i.order - 1) where i.parent.id = :parent and i.order > :order and i.supplierForm.id = :formId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("formId", formId);
			query.setParameter("parent", oldParent);
			query.setParameter("order", oldOrder);
			query.executeUpdate();
		}

		// Fetch the parent object again as its position might have changed during above updates.
		if (newParent != null) {
			SupplierFormItem newDbParent = findById(newParent);
			LOG.info("newDbParent.getLevel() :" + newDbParent.getLevel());
			formItem.setLevel(newDbParent.getLevel());
		}

		StringBuilder hql = new StringBuilder("update " + entityClass.getSimpleName() + " i set i.order = :order, i.level = :level, i.parent.id = :newParent where i.id = :id");
		LOG.info("HQL : " + hql.toString());
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", formItem.getId());
		query.setParameter("newParent", newParent);
		query.setParameter("level", formItem.getLevel());
		query.setParameter("order", formItem.getOrder());
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getNotSectionItemAddedByFormId(String formId) {
		final Query query = getEntityManager().createQuery("select si.itemName from SupplierFormItem si inner join si.supplierForm s where s.id = :formId and si.parent is null and  (select count(child.id) from  SupplierFormItem child where child.parent.id = si.id ) = 0");
		query.setParameter("formId", formId);
		return query.getResultList();
	}
}