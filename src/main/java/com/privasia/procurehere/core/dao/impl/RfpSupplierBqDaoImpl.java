package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpSupplierBqDao;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SupplierBqStatus;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class RfpSupplierBqDaoImpl extends GenericDaoImpl<RfpSupplierBq, String> implements RfpSupplierBqDao {

	@SuppressWarnings("unchecked")
	@Override
	public RfpSupplierBq findBqByEventIdAndBqName(String id, String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("from RfpSupplierBq rb inner join fetch rb.event as r where r.id = :id and rb.bq.id = :bqId and rb.supplier.id=:supplierId");
		query.setParameter("id", id);
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		List<RfpSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpSupplierBq findBqByBqId(String id, String supplierId) {
		final Query query = getEntityManager().createQuery("from RfpSupplierBq as r inner join fetch r.bq as rb where rb.id = :id and r.supplier.id =:supplierId");
		query.setParameter("id", id);
		query.setParameter("supplierId", supplierId);
		List<RfpSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void deleteSupplierBqsForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("delete from RfpSupplierBq a where a.event.id = :eventId");
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfpSupplierBq> findRfpSupplierBqbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfpSupplierBq a inner join fetch a.supplierBqItems item inner join a.event ev  where ev.id = :id order by item.level, item.order");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfpSupplierBq> findRfpSupplierBqbyEventIdAndSupplierId(String eventId, String supplierId) {
		// TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
		//final Query query = getEntityManager().createQuery("select distinct a from RfpSupplierBq a inner join fetch a.supplierBqItems item inner join a.event ev inner join fetch a.bq b where ev.id = :id and a.supplier.id = :supplierId order by item.level, item.order");
		final Query query = getEntityManager().createQuery("select distinct a from RfpSupplierBq a inner join fetch a.supplierBqItems item inner join a.event ev inner join fetch a.bq b where ev.id = :id and a.supplier.id = :supplierId order by a.bqOrder");
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BigDecimal> grandTotalOfBqByEventIdAndSupplierId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select r.grandTotal from RfpSupplierBq r where r.event.id =:eventId and r.supplier.id =:supplierId ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<BigDecimal> grandTotalList = (List<BigDecimal>) query.getResultList();
		return grandTotalList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpSupplierBq> rfpSupplierBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select new RfpSupplierBq(r.name, r.grandTotal) from RfpSupplierBq r where r.event.id =:eventId and r.supplier.id =:supplierId and r.grandTotal > 0 ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RfpSupplierBq> rfpSupplierBqList = (List<RfpSupplierBq>) query.getResultList();
		return rfpSupplierBqList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public long findPendingBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select count(r.id) from RfpSupplierBq r where r.event.id =:eventId and r.supplier.id =:supplierId and r.supplierBqStatus in (:status) ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("status", Arrays.asList(SupplierBqStatus.DRAFT, SupplierBqStatus.PENDING));
		return ((Number) query.getSingleResult()).longValue();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RfpSupplierBq findBqByEventIdAndSupplierId(String eventId, String bqId, String supId) {
		final Query query = getEntityManager().createQuery("from RfpSupplierBq rb inner join fetch rb.event as r where r.id = :id and rb.bq.id = :bqId and rb.supplier.id=:supplierId");
		query.setParameter("id", eventId);
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supId);
		List<RfpSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqPojo> getAllRfaTopEventSuppliersIdByEventId(String eventId, int limit, String bqId) {

		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(sup.companyName,sup.id,a.grandTotal,a.additionalTax,a.totalAfterTax,bq.name) from RfpSupplierBq a inner join a.event e inner join a.bq bq inner join a.supplier as sup, RfpEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and e.id = :eventId and es.disqualify=:disqualify and es.submissionStatus = :status ");
		if (StringUtils.checkString(bqId).length() > 0) {
			hsql.append(" and bq.id =:bqId ");
		}
		hsql.append(" order by a.totalAfterTax");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setMaxResults(limit);
		query.setParameter("eventId", eventId);
		if (StringUtils.checkString(bqId).length() > 0) {
			query.setParameter("bqId", bqId);
		}
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		query.setParameter("disqualify", Boolean.FALSE);

		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqPojo> findRfpSupplierBqBySupplierIdsOdrByRank(String eventId, Integer limitSupplier) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(sup.companyName, sup.id , a.totalAfterTax) from RfpSupplierBq a inner join a.event e inner join a.supplier as sup, RfpEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.submissionStatus = :status and e.id = :eventId order by a.totalAfterTax ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (limitSupplier != null) {
			query.setMaxResults(limitSupplier);
		}
		query.setParameter("eventId", eventId);

		query.setParameter("status", SubmissionStatusType.COMPLETED);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqPojo> findRfpSupplierBqCompleteNessBySupplierIdsOdrByRank(String eventId, Integer limitSupplier) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(es.ipAddress,a.id,sup.id, sup.companyName, es.numberOfBids,es.rejectedTime,es.supplierEventReadTime,es.submissionStatus,es.disqualify,a.totalAfterTax,count(case when supBqitm.unitPrice  > 0 then 1 else null end ),count(distinct supBqitm.id),es.supplierSubmittedTime, a.bqOrder) from RfpSupplierBq a inner join a.event e inner join a.supplier as sup left outer join a.supplierBqItems supBqitm, RfpEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.submissionStatus = :status and e.id = :eventId and supBqitm.parent is not null group by es.ipAddress,a.id,sup.id, sup.companyName, es.numberOfBids,es.rejectedTime,es.supplierEventReadTime,es.submissionStatus,es.disqualify,a.totalAfterTax,es.supplierSubmittedTime, a.bqOrder order by a.bqOrder, a.totalAfterTax ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (limitSupplier != null) {
			query.setMaxResults(limitSupplier);
		}
		query.setParameter("eventId", eventId);
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EvaluationSuppliersBqPojo> getAllBqsByBqIdsAndEventId(String bqid, String id) {
		String hql = "select distinct new com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo(sup.id, sup.companyName, a.totalAfterTax, a.additionalTax, a.grandTotal,bq.id, bq.name,a.remark) from RfpSupplierBq a inner join a.event sp inner join a.bq bq inner join a.supplier sup,  RfpEventSupplier es inner join es.rfxEvent e inner join es.supplier esup where sp.id = :id and e.id = sp.id and sup.id = esup.id and bq.id =:bqIds and es.submissionStatus =:status  order by a.totalAfterTax ";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		query.setParameter("bqIds", bqid);
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqPojo> findRfaSupplierBqCompleteNessBySupplierIds(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(es.ipAddress,a.id,sup.id, sup.companyName, es.numberOfBids,es.rejectedTime,es.supplierEventReadTime,es.submissionStatus,es.disqualify,a.totalAfterTax,count(case when supBqitm.unitPrice  > 0 then 1 else null end ),count(distinct supBqitm.id),es.supplierSubmittedTime) from RfpSupplierBq a inner join a.event e inner join a.supplier as sup left outer join a.supplierBqItems supBqitm, RfpEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.submissionStatus <> :status and e.id = :eventId group by es.ipAddress,a.id,sup.id,sup.companyName,es.numberOfBids,es.rejectedTime,es.supplierEventReadTime,es.submissionStatus,es.disqualify,a.totalAfterTax,es.supplierSubmittedTime order by a.totalAfterTax  ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", SubmissionStatusType.INVITED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpSupplierBq findBqByEventIdAndSupplierIdOfQualifiedSupplier(String id, String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("from RfpSupplierBq rb inner join fetch rb.event as r inner join fetch r.suppliers as s where r.id = :id and rb.bq.id = :bqId and rb.supplier.id=:supplierId and s.disqualify =:disqualify");
		query.setParameter("id", id);
		query.setParameter("bqId", bqId);
		query.setParameter("disqualify", Boolean.FALSE);
		query.setParameter("supplierId", supplierId);
		List<RfpSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqPojo> getAllRfpTopCompletedEventSuppliersIdByEventId(String eventId, Integer limit, String bqId) {

		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(sup.companyName,sup.id,a.grandTotal,a.additionalTax,a.totalAfterTax,bq.name,count(case when supBqitm.unitPrice  > 0 then 1 else null end ),count(distinct supBqitm.id)) from RfpSupplierBq a inner join a.event e left outer join a.supplierBqItems supBqitm inner join a.bq bq inner join a.event e inner join a.supplier as sup, RfpEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and e.id = :eventId and es.disqualify=:disqualify and es.submissionStatus = :status and supBqitm.parent is not null ");
		if (StringUtils.checkString(bqId).length() > 0) {
			hsql.append(" and bq.id =:bqId ");
		}
		hsql.append(" group by sup.companyName,sup.id,a.grandTotal,a.additionalTax,a.totalAfterTax,bq.name  order by a.totalAfterTax ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setMaxResults(limit);
		query.setParameter("eventId", eventId);
		if (StringUtils.checkString(bqId).length() > 0) {
			query.setParameter("bqId", bqId);
		}
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		query.setParameter("disqualify", Boolean.FALSE);

		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqPojo> findRfpSupplierParticipation(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(es.numberOfBids,sup.companyName,sup.id,es.disqualify,es.ipAddress,es.submissionStatus,es.rejectedTime,es.supplierEventReadTime,es.supplierSubmittedTime) from  RfpEventSupplier es inner join es.rfxEvent e inner join es.supplier sup where es.submissionStatus not in (:status) and e.id = :eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		List<SubmissionStatusType> list = new ArrayList<SubmissionStatusType>();
		list.add(SubmissionStatusType.INVITED);
		query.setParameter("status", list);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpSupplierBq> findRfpSummarySupplierBqbyEventId(String eventId) {
		// TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
		//final Query query = getEntityManager().createQuery("select distinct a from RfpSupplierBq a inner join fetch a.supplierBqItems item inner join a.event ev  where ev.id = :id order by item.level, item.order");
		final Query query = getEntityManager().createQuery("select distinct a from RfpSupplierBq a inner join fetch a.supplierBqItems item inner join a.event ev  where ev.id = :id");
		query.setParameter("id", eventId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfpSupplierBq findRfpSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfpSupplierBq(a.supplierBqStatus, b.id) from RfpSupplierBq a inner join a.event ev inner join a.bq b where ev.id = :id and a.supplier.id = :supplierId and a.bq.id = :bqId order by a.bqOrder");
		
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("bqId", bqId);
		List<RfpSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}
}
