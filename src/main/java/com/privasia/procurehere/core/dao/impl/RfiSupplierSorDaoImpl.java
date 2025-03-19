package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.RfiSupplierSorDao;
import com.privasia.procurehere.core.entity.RfiSupplierSor;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SupplierBqStatus;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersSorPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierSorPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class RfiSupplierSorDaoImpl extends GenericDaoImpl<RfiSupplierSor, String> implements RfiSupplierSorDao {


    @Override
    public RfiSupplierSor findRfiSupplierSorStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
        Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfiSupplierSor(a.supplierSorStatus, b.id, a.id) from RfiSupplierSor a inner join a.event as e inner join a.bq as b where a.event.id = :eventId and a.supplier.id = :supplierId and a.bq.id = :bqId order by a.sorOrder ");
        query.setParameter("eventId", eventId);
        query.setParameter("supplierId", supplierId);
        query.setParameter("bqId", bqId);
        List<RfiSupplierSor> bqList = query.getResultList();
        if (CollectionUtil.isNotEmpty(bqList)) {
            return bqList.get(0);
        } else {
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public RfiSupplierSor findSorByEventIdAndSorName(String id, String bqId, String supplierId) {
        final Query query = getEntityManager().createQuery("from RfiSupplierSor rb inner join fetch rb.event as r where r.id = :id and rb.bq.id = :bqId and rb.supplier.id=:supplierId");
        query.setParameter("id", id);
        query.setParameter("bqId", bqId);
        query.setParameter("supplierId", supplierId);
        List<RfiSupplierSor> uList = query.getResultList();
        if (CollectionUtil.isNotEmpty(uList)) {
            return uList.get(0);
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RfiSupplierSor> findRfiSupplierSorbyEventIdAndSupplierId(String eventId, String supplierId) {
        // TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
        //final Query query = getEntityManager().createQuery("select distinct a from RfqSupplierBq a inner join fetch a.supplierBqItems item inner join a.event ev  inner join fetch a.bq b where ev.id = :id and a.supplier.id = :supplierId order by item.level, item.order");
        final Query query = getEntityManager().createQuery("select distinct a from RfiSupplierSor a inner join fetch a.supplierSorItems item inner join a.event ev  inner join fetch a.bq b where ev.id = :id and a.supplier.id = :supplierId order by a.sorOrder");
        query.setParameter("id", eventId);
        query.setParameter("supplierId", supplierId);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RfiSupplierSor> rfiSupplierSorAnswerdByEventIdAndSupplierId(String eventId, String supplierId) {
        List<RfiSupplierSor> supplierBqList = new ArrayList<>();
        try {
            StringBuilder hsql = new StringBuilder("select new RfiSupplierSor(r.name) from RfiSupplierSor r where r.event.id =:eventId and r.supplier.id =:supplierId and r.supplierSorStatus = 'COMPLETED' ");
            Query query = getEntityManager().createQuery(hsql.toString());
            query.setParameter("eventId", eventId);
            query.setParameter("supplierId", supplierId);
            supplierBqList = (List<RfiSupplierSor>) query.getResultList();
            return supplierBqList;
        } catch (Exception e) {
            return supplierBqList;
        }
    }

    @Override
    public long findPendingSorsByEventIdAndEventSorId(String eventId, String supplierId) {
        Query query =getEntityManager().createQuery("select count (pc.id) from RfiSupplierSor pc inner join pc.event as e where pc.supplier.id =:supplierId and e.id =:eventId and pc.supplierSorStatus in (:supplierBqStatus) ");
        query.setParameter("supplierId", supplierId);
        query.setParameter("eventId", eventId);
        query.setParameter("supplierBqStatus", Arrays.asList(SupplierBqStatus.DRAFT, SupplierBqStatus.PENDING));
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public List<EvaluationSuppliersSorPojo> getAllSorsBySorIdsAndEventId(String bqid, String id) {
        String hql = "select distinct new com.privasia.procurehere.core.pojo.EvaluationSuppliersSorPojo(sup.id, sup.companyName, bq.id, bq.name,a.remark) from RfiSupplierSor a inner join a.event sp inner join a.bq bq inner join a.supplier sup,  RfiEventSupplier es inner join es.rfxEvent e inner join es.supplier esup where sp.id = :id and e.id = sp.id and sup.id = esup.id and bq.id =:bqIds and es.submissionStatus =:status";
        final Query query = getEntityManager().createQuery(hql);
        query.setParameter("id", id);
        query.setParameter("bqIds", bqid);
        query.setParameter("status", SubmissionStatusType.COMPLETED);
        return query.getResultList();

    }


    @Override
    public List<RfaSupplierSorPojo> getAllRfiTopEventSuppliersIdByEventId(String eventId, int limit, String bqId) {
        StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierSorPojo(sup.companyName,sup.id,bq.name) from RfiSupplierSor a inner join a.event e inner join a.bq bq inner join a.event e inner join a.supplier as sup, RfiEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and e.id = :eventId and es.disqualify=:disqualify and es.submissionStatus = :status ");
        if (StringUtils.checkString(bqId).length() > 0) {
            hsql.append(" and bq.id =:bqId ");
        }
        // need to think later
//        hsql.append(" order by a.totalAfterTax ");
        final Query query = getEntityManager().createQuery(hsql.toString());
        //query.setMaxResults(limit);
        query.setParameter("eventId", eventId);
        if (StringUtils.checkString(bqId).length() > 0) {
            query.setParameter("bqId", bqId);
        }
        query.setParameter("status", SubmissionStatusType.COMPLETED);
        query.setParameter("disqualify", Boolean.FALSE);

        return query.getResultList();
    }

    @Override
    public void deleteSupplierSorsForEvent(String eventId) {
        final Query query = getEntityManager().createQuery("delete from RfiSupplierSor a where a.event.id = :eventId");
        query.setParameter("eventId", eventId);
        query.executeUpdate();
    }
}
