package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.RfqSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RfqSorItem;
import com.privasia.procurehere.core.entity.RfqSupplierSor;
import com.privasia.procurehere.core.entity.RfqSupplierSorItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.pojo.SorItemPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class RfqSupplierSorItemDaoImpl extends GenericDaoImpl<RfqSupplierSorItem, String> implements RfqSupplierSorItemDao {

    private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

    @Override
    public List<SorItemPojo> getAllLevelOrderSorItemBySorId(String bqId, String supplierId, String searchVal) {
        LOG.info(" sorId  :" + bqId);
        StringBuilder hql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.SorItemPojo(a.level, a.order) from RfqSupplierSorItem a where a.supplier.id= :supplierId and a.supplierBq.bq.id= :bqId ");

        if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
            hql.append(" and (upper(a.itemName) like :searchValue or upper(a.itemDescription) like :searchValue) ");
        }
        hql.append(" order by a.level, a.order ");

        final Query query = getEntityManager().createQuery(hql.toString());

        if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
            query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
        }
        query.setParameter("bqId", bqId);
        query.setParameter("supplierId", supplierId);
        List<SorItemPojo> list = query.getResultList();
        return list;
    }


    @Override
    public List<RfqSupplierSorItem> getSorItemForSearchFilterForSupplier(String eventBqId, String supplierId, String searchVal, Integer start, Integer pageLength, Integer itemLevel, Integer itemOrder) {
        StringBuilder hql = new StringBuilder("select distinct a from RfqSupplierSorItem a inner join fetch a.supplier as s inner join fetch a.supplierBq as b inner join fetch b.bq bq left outer join fetch a.children as ch left outer join fetch a.parent p where s.id= :supplierId and bq.id= :bqId ");

        if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
            hql.append(" and (upper(a.itemName) like :searchValue or upper(a.itemDescription) like :searchValue) ");
        }
        if (itemLevel != null && itemOrder != null) {
            hql.append(" and a.level =:itemLevel and a.order =:itemOrder ");
        }
        hql.append(" order by a.level, a.order ");

        final Query query = getEntityManager().createQuery(hql.toString());

        if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
            query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
        }
        if (itemLevel != null && itemOrder != null) {
            query.setParameter("itemLevel", itemLevel);
            query.setParameter("itemOrder", itemOrder);
        }
        query.setParameter("bqId", eventBqId);
        query.setParameter("supplierId", supplierId);

        if (start != null && pageLength != null) {
            query.setFirstResult(start);
            query.setMaxResults(pageLength);
        }
        return query.getResultList();
    }


    @Override
    public long totalSorItemCountBySorId(String bqId, String supplierId, String searchVal) {
        StringBuilder hql = new StringBuilder("select count(a) from RfqSupplierSorItem a where a.supplier.id= :supplierId and a.supplierBq.bq.id= :bqId ");

        if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
            hql.append(" and (upper(a.itemName) like :searchValue or upper(a.itemDescription) like :searchValue) ");
        }

        final Query query = getEntityManager().createQuery(hql.toString());

        if (searchVal != null && StringUtils.checkString(searchVal).length() > 0) {
            query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
        }
        query.setParameter("bqId", bqId);
        query.setParameter("supplierId", supplierId);

        return ((Number) query.getSingleResult()).intValue();
    }


    @Override
    public List<RfqSorItem> getSorItemsbyId(String bqId) {
        final Query query = getEntityManager().createQuery("select distinct a from RfqSorItem a inner join fetch a.sor sp left outer join fetch a.children c left outer join fetch a.uom uom where sp.id =:id and a.parent is null order by a.level, a.order");
        query.setParameter("id", bqId);
        return query.getResultList();
    }


    @Override
    public List<RfqSupplierSor> getAllSorsBySupplierId(String eventId, String supplierId) {
        final Query query = getEntityManager().createQuery("select distinct a.supplierBq from RfqSupplierSorItem a where a.event.id = :eventId and a.supplier.id =:supplierId order by a.supplierBq.sorOrder");
        query.setParameter("eventId", eventId);
        query.setParameter("supplierId", supplierId);
        List<RfqSupplierSor> list = query.getResultList();
        return list;
    }


    @Override
    public List<RfqSupplierSorItem> findSupplierSorItemsBySorItemIdAndEventId(String bqItemId, String eventId, List<Supplier> suppliers) {
        for (Supplier supplier : suppliers) {
            LOG.info("Suppliers >>>>>>>>>>>>>>>>>>>  " + supplier.getId());
        }
        final Query query = getEntityManager().createQuery("select distinct a from RfqSupplierSorItem a inner join fetch a.supplier s inner join a.event re left join fetch a.supplierBq hn where a.sorItem.id = :bqItemId and re.id =:eventId and s in (:suppliers) order by s.companyName");
        query.setParameter("bqItemId", bqItemId);
        query.setParameter("eventId", eventId);
        query.setParameter("suppliers", suppliers);
        return query.getResultList();
    }

    @Override
    public List<RfqSupplierSorItem> findSupplierSorItemByListBySorIdAndSupplierId(String bqId, String supplierId) {
        LOG.info("Supplier Id : " + supplierId + " Sor Id : " + bqId);
        final Query query = getEntityManager().createQuery("select distinct a from RfqSupplierSorItem a inner join fetch a.supplier as s inner join fetch a.event as ev inner join fetch a.supplierBq as b inner join fetch b.bq bq left outer join fetch a.children as ch inner join fetch a.sorItem as bqi where s.id= :supplierId and bq.id= :bqId and a.parent is null order by a.level, a.order");
        query.setParameter("bqId", bqId);
        query.setParameter("supplierId", supplierId);
        return query.getResultList();
    }


    @Override
    public List<RfqSupplierSorItem> findSupplierBqItemListByBqIdAndSupplierIdsParentIdNotNull(String bqId, String supplierIds) {
        final Query query = getEntityManager().createQuery("select distinct a from RfqSupplierSorItem a inner join fetch a.supplier as s inner join fetch a.supplierBq as b inner join fetch b.bq bq where s.id in(:supplierId) and bq.id= :bqId order by a.level, a.order");
        query.setParameter("bqId", bqId);
        query.setParameter("supplierId", supplierIds);
        return query.getResultList();
    }

    @Override
    public void deleteSupplierSorItemsForEvent(String eventId) {
        final Query query = getEntityManager().createQuery("delete from RfqSupplierSorItem a where a.event.id = :eventId");
        query.setParameter("eventId", eventId);
        query.executeUpdate();
    }

    @Override
    public Boolean checkAllFieldCompleted(String sorId, String supplierId) {
        // check all the items if any of them null , not able to complete the event

        StringBuilder hql = new StringBuilder("select count(*) from RfqSupplierSorItem ea where ea.supplier.id = :supplierId and ea.supplierBq.id = :sorId and ea.totalAmount is null and ea.parent is not null");
        final Query query = getEntityManager().createQuery(hql.toString());
        query.setParameter("supplierId", supplierId);
        query.setParameter("sorId", sorId);
        return ((Number) query.getSingleResult()).intValue() > 0;
    }

}
