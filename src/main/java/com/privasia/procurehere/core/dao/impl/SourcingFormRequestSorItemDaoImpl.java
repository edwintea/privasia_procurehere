package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.SourcingFormRequestSorItemDao;
import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import com.privasia.procurehere.core.entity.SourcingFormRequestSorItem;
import com.privasia.procurehere.core.pojo.SourcingSorItemPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;

@Repository
public class SourcingFormRequestSorItemDaoImpl extends GenericDaoImpl<SourcingFormRequestSorItem, String> implements SourcingFormRequestSorItemDao {


    private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

    @Override
    public List<SourcingFormRequestSorItem> getSorItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder,
                                                                      String searchVal, Integer start, Integer length) {
        try {
            StringBuilder hql = new StringBuilder("select distinct a from SourcingFormRequestSorItem a inner join fetch a.sor bq left outer join fetch a.children c left outer join fetch a.parent p left outer join fetch a.uom uom where bq.id =:id ");

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

            List<SourcingFormRequestSorItem> uList = query.getResultList();

            return uList;
        } catch (NoResultException nr) {
            LOG.info("Error while getting sourcing SOR Items : " + nr.getMessage(), nr);
            return null;
        }
    }


    @Override
    public List<SourcingSorItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String searchVal) {
        StringBuilder hql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.SourcingSorItemPojo(a.level, a.order) from SourcingFormRequestSorItem a where a.sor.id =:bqId ");
        if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
            hql.append(" and (upper(a.itemName) like :searchValue or upper(a.itemDescription) like :searchValue) ");
        }
        hql.append(" order by a.level, a.order ");
        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("bqId", bqId);
        if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
            query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
        }
        List<SourcingSorItemPojo> list = query.getResultList();
        return list;
    }


    @Override
    public long getTotalSorItemCountBySorId(String bqId, String searchVal) {
        StringBuilder hql = new StringBuilder("select count(item) from SourcingFormRequestSorItem item where item.sor.id =:bqId ");
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
    public boolean isExist(SourcingFormRequestSorItem sourcingBqItem, String bqId, String parentId) {
        String hql = "select count(*) from SourcingFormRequestSorItem b where b.sor.id = :bqId and upper(b.itemName) = :itemName";
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


    @Override
    public List<SourcingFormRequestSorItem> getSorItemLevelOrder(String bqId) {
        final Query query = getEntityManager().createQuery("from SourcingFormRequestSorItem  a inner join fetch a.sor c where a.parent is null and c.id = :bqId");
        query.setParameter("bqId", bqId);
        return query.getResultList();
    }


    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateItemOrder(final SourcingFormRequestSorItem bqItem, String bqId, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel) {
        // Top Level
        // Rearrange its new place - Push down
        if (newOrder == 0) {
            StringBuilder hql = new StringBuilder("update SourcingFormRequestSorItem i set i.level = (i.level + 1) where i.sor.id = :bqId");

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
            Query query = getEntityManager().createQuery("update SourcingFormRequestSorItem i set i.level = (i.level - 1) where i.level > :level and i.sor.id = :bqId");
            query.setParameter("bqId", bqId);
            query.setParameter("level", oldLevel);
            query.executeUpdate();
        }

        // Child Level
        // Push down
        if (newParent != null && oldParent != null) {

            StringBuilder hql = new StringBuilder("update SourcingFormRequestSorItem i set i.order = (i.order + 1) where i.parent.id = :parent and i.sor.id = :bqId");

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
            Query query = getEntityManager().createQuery("update SourcingFormRequestSorItem i set i.order = (i.order - 1) where i.parent.id = :parent and i.order > :order and i.sor.id = :bqId");
            query.setParameter("bqId", bqId);
            query.setParameter("parent", oldParent);
            query.setParameter("order", oldOrder);
            query.executeUpdate();
        }

        // Fetch the parent object again as its position might have changed during above updates.
        if (newParent != null) {
            SourcingFormRequestSorItem newDbParent = getSorItemBySorItemId(newParent);
            bqItem.setLevel(newDbParent.getLevel());
        }

        Query query = getEntityManager().createQuery("update SourcingFormRequestSorItem i set i.order = :order, i.level = :level, i.parent.id = :newParent where i.id = :bqItemId and i.sor.id = :bqId");
        query.setParameter("bqId", bqId);
        query.setParameter("bqItemId", bqItem.getId());
        query.setParameter("newParent", newParent);
        query.setParameter("order", bqItem.getOrder());
        query.setParameter("level", bqItem.getLevel());
        query.executeUpdate();

        // bqItem = update(bqItem);

    }

    @Override
    public SourcingFormRequestSorItem getSorItemBySorItemId(String bqItemId) {
        final Query query = getEntityManager().createQuery("from  SourcingFormRequestSorItem b inner join fetch b.sor bq where b.id=:bqItemId");
        query.setParameter("bqItemId", bqItemId);
        List<SourcingFormRequestSorItem> list = query.getResultList();
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public SourcingFormRequestSor getAllsorItemsBySorId(String id) {
        String hql = "from SourcingFormRequestSor s left outer join fetch s.sorItems item where s.id=:id";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("id", id);
        return (SourcingFormRequestSor) query.getSingleResult();
    }


    @Override
    public List<SourcingFormRequestSorItem> getAllsorItemBySorId(String id) {
        String hql = "select distinct item from SourcingFormRequestSor s inner join  s.sorItems item  where  s.id=:id order by item.level, item.order";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("id", id);
        return query.getResultList();
    }


    @Override
    public String deleteSorItems(String[] bqItemsIds, String bqId) {
        Query query = getEntityManager().createQuery("from SourcingFormRequestSorItem b inner join fetch b.sor bq where b.id in(:id )");
        query.setParameter("id", Arrays.asList(bqItemsIds));
        List<SourcingFormRequestSorItem> bqItemist = query.getResultList();
        if (CollectionUtil.isNotEmpty(bqItemist)) {
            for (String id : bqItemsIds) {
                query = getEntityManager().createQuery("select a.level, a.order, p.id from SourcingFormRequestSorItem  a left outer join a.parent p where a.id = :id");
                query.setParameter("id", id);
                List<Object[]> result = query.getResultList();
                if (CollectionUtil.isNotEmpty(result) && result.get(0).length > 1) {
                    int level = ((Number) result.get(0)[0]).intValue();
                    int order = ((Number) result.get(0)[1]).intValue();
                    String parentId = ((String) result.get(0)[2]);

                    // If Parent
                    if (StringUtils.checkString(parentId).length() == 0) {
                        StringBuilder hql = new StringBuilder("update SourcingFormRequestSorItem i set i.level = (i.level - 1) where i.level > :level and i.sor.id = :bqId");
                        query = getEntityManager().createQuery(hql.toString());
                        query.setParameter("bqId", bqId);
                        query.setParameter("level", level);
                        query.executeUpdate();
                    } else {
                        // If Child
                        StringBuilder hql = new StringBuilder("update SourcingFormRequestSorItem i set i.order = (i.order - 1) where i.level = :level and i.order >= :order and i.parent.id = :parentId and i.sor.id = :bqId");
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
        query = getEntityManager().createQuery("delete from SourcingFormRequestSorItem item where item.id in(:id) and item.parent is not null");
        query.setParameter("id", Arrays.asList(bqItemsIds));
        query.executeUpdate();

        query = getEntityManager().createQuery("delete from SourcingFormRequestSorItem item where item.id in(:id) and item.parent is null");
        query.setParameter("id", Arrays.asList(bqItemsIds));
        query.executeUpdate();
        return bqId;
    }

    @Override
    public void deleteSorItemsbySorid(String bqId) {
        Query query = getEntityManager().createQuery("delete from SourcingFormRequestSorItem item where item.sor.id = :id and item.parent is not null");
        query.setParameter("id", bqId);
        query.executeUpdate();

        query = getEntityManager().createQuery("delete from SourcingFormRequestSorItem item where item.sor.id = :id and item.parent is null");
        query.setParameter("id", bqId);
        query.executeUpdate();
    }

    @Override
    public SourcingFormRequestSorItem getParentbyLevelId(String bqId, Integer level) {
        try {
            final Query query = getEntityManager().createQuery("from SourcingFormRequestSorItem a inner join fetch a.sor as b where b.id = :bqId and a.level = :level and a.order = 0 ");
            query.setParameter("bqId", bqId);
            query.setParameter("level", level);
            List<SourcingFormRequestSorItem> uList = query.getResultList();
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
    public void deletefieldInSorItems(String bqId, String label) {
        try {
            LOG.info("SOR ID :: " + bqId + " label :: " + label);
            StringBuilder hql = new StringBuilder("update SourcingFormRequestSorItem i set i." + label + " = null where i.sor.id = :bqId");
            Query query = getEntityManager().createQuery(hql.toString());
            query.setParameter("bqId", bqId);
            query.executeUpdate();

        } catch (NoResultException nr) {
            LOG.info("Error while deleting new Fields: " + nr.getMessage(), nr);
        }
    }
}
