package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftSupplierBqDao;
import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SupplierBqStatus;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class RftSupplierBqDaoImpl extends GenericDaoImpl<RftSupplierBq, String> implements RftSupplierBqDao {

	@SuppressWarnings("unchecked")
	@Override
	public RftSupplierBq findBqByEventIdAndSupplierId(String id, String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("from RftSupplierBq rb inner join fetch rb.rfxEvent as r where r.id = :id and rb.bq.id = :bqId and rb.supplier.id=:supplierId");
		query.setParameter("id", id);
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftSupplierBq findBqByBqId(String id, String supplierId) {
		final Query query = getEntityManager().createQuery("from RftSupplierBq as r inner join fetch r.bq as rb where rb.id = :id and r.supplier.id=:supplierId");
		query.setParameter("id", id);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftSupplierBq findSupplierBqByEventIdSupplierId(String eventId, String supplierId) {
		final Query query = getEntityManager().createQuery("from RftSupplierBq rb inner join fetch rb.rfxEvent as r where r.id = :id and rb.supplier.id =:supplierId");
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void deleteSupplierBqsForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("delete from RftSupplierBq a where a.rfxEvent.id = :eventId");
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftSupplierBq> findRftSupplierBqbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct a from RftSupplierBq a inner join fetch a.supplierBqItems item inner join a.rfxEvent ev  where ev.id = :id order by item.level, item.order");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftSupplierBq> findRftSupplierBqbyEventIdAndSupplierId(String eventId, String supplierId) {
		// TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
		//final Query query = getEntityManager().createQuery("select distinct a from RftSupplierBq a inner join fetch a.supplierBqItems item inner join a.rfxEvent ev  inner join fetch a.bq b where ev.id = :id and a.supplier.id = :supplierId order by item.level, item.order");
		final Query query = getEntityManager().createQuery("select distinct a from RftSupplierBq a inner join fetch a.supplierBqItems item inner join a.rfxEvent ev  inner join fetch a.bq b where ev.id = :id and a.supplier.id = :supplierId order by a.bqOrder");
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BigDecimal> grandTotalOfBqByEventIdAndSupplierId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select r.grandTotal from RftSupplierBq r where r.rfxEvent.id =:eventId and r.supplier.id =:supplierId ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<BigDecimal> grandTotalList = (List<BigDecimal>) query.getResultList();
		return grandTotalList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierBq> rftSupplierBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select new RftSupplierBq(r.name, r.grandTotal) from RftSupplierBq r where r.rfxEvent.id = :eventId and r.supplier.id = :supplierId and r.grandTotal > 0 ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierBq> rftSupplierBqList = (List<RftSupplierBq>) query.getResultList();
		return rftSupplierBqList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqPojo> getAllRfaTopEventSuppliersIdByEventId(String eventId, int limit, String bqId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(sup.companyName,sup.id,a.grandTotal,a.additionalTax,a.totalAfterTax,bq.name) from RftSupplierBq a inner join a.rfxEvent e inner join a.bq bq inner join a.supplier as sup, RftEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and e.id = :eventId and es.disqualify=:disqualify and es.submissionStatus = :status ");
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
	public List<RfaSupplierBqPojo> findRftSupplierBqBySupplierIdsOdrByRank(String eventId, Integer limitSupplier) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(sup.companyName, sup.id , a.totalAfterTax) from RftSupplierBq a inner join a.rfxEvent e inner join a.supplier as sup, RftEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.submissionStatus = :status and e.id = :eventId order by a.totalAfterTax ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (limitSupplier != null) {
			query.setMaxResults(limitSupplier);
		}
		query.setParameter("eventId", eventId);

		query.setParameter("status", SubmissionStatusType.COMPLETED);

		return query.getResultList();
	}

	@Override
	@SuppressWarnings({ "unchecked", "unused" })
	public List<RfaSupplierBqPojo> findRftSupplierBqCompleteNessBySupplierIdsOdrByRank(String eventId, Integer limitSupplier) {

		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(es.ipAddress,a.id,sup.id, sup.companyName, es.numberOfBids,es.rejectedTime,es.supplierEventReadTime,es.submissionStatus,es.disqualify,a.totalAfterTax,count(case when supBqitm.unitPrice  > 0 then 1 else null end ),count(distinct supBqitm.id),es.supplierSubmittedTime, a.bqOrder) from RftSupplierBq a inner join a.rfxEvent e inner join a.supplier as sup left outer join a.supplierBqItems supBqitm, RftEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.submissionStatus = :status and e.id = :eventId and supBqitm.parent is not null group by es.ipAddress,a.id,sup.id, sup.companyName, es.numberOfBids,es.rejectedTime,es.supplierEventReadTime,es.submissionStatus,es.disqualify,a.totalAfterTax,es.supplierSubmittedTime, a.bqOrder order by a.bqOrder, a.totalAfterTax ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (limitSupplier != null) {
			query.setMaxResults(limitSupplier);
		}
		query.setParameter("eventId", eventId);
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		List<RfaSupplierBqPojo> suuLisyt = query.getResultList();
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EvaluationSuppliersBqPojo> getAllBqsByBqIdsAndEventId(String bqid, String id) {
		String hql = "select distinct new com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo(sup.id, sup.companyName, a.totalAfterTax, a.additionalTax, a.grandTotal,bq.id, bq.name,a.remark) from RftSupplierBq a inner join a.rfxEvent sp inner join a.bq bq inner join a.supplier sup,  RftEventSupplier es inner join es.rfxEvent e inner join es.supplier esup where sp.id = :id and e.id = sp.id and sup.id = esup.id and bq.id =:bqIds and es.submissionStatus =:status  order by a.totalAfterTax ";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		query.setParameter("bqIds", bqid);
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqPojo> findRftSupplierBqCompleteNessBySupplierIds(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(es.ipAddress,a.id,sup.id, sup.companyName, es.numberOfBids,es.rejectedTime,es.supplierEventReadTime,es.submissionStatus,es.disqualify,a.totalAfterTax,count(case when supBqitm.unitPrice  > 0 then 1 else null end ),count(distinct supBqitm.id),es.supplierSubmittedTime) from RftSupplierBq a inner join a.rfxEvent e inner join a.supplier as sup left outer join a.supplierBqItems supBqitm, RftEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.submissionStatus <> :status and e.id = :eventId group by es.ipAddress,a.id,sup.id,sup.companyName,es.numberOfBids,es.rejectedTime,es.supplierEventReadTime,es.submissionStatus,es.disqualify,a.totalAfterTax,es.supplierSubmittedTime order by a.totalAfterTax  ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", SubmissionStatusType.INVITED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftSupplierBq findBqByEventIdAndSupplierIdOfQualifiedSupplier(String id, String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("from RftSupplierBq rb inner join fetch rb.rfxEvent as r inner join fetch r.suppliers as s where r.id = :id and rb.bq.id = :bqId and rb.supplier.id=:supplierId and s.disqualify =:disqualify");
		query.setParameter("id", id);
		query.setParameter("bqId", bqId);
		query.setParameter("disqualify", Boolean.FALSE);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqPojo> getAllRftTopCompletedEventSuppliersIdByEventId(String eventId, Integer limit, String bqId) {

		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(sup.companyName,sup.id,a.grandTotal,a.additionalTax,a.totalAfterTax,bq.name,count(case when supBqitm.unitPrice  > 0 then 1 else null end ),count(distinct supBqitm.id)) from RftSupplierBq a inner join a.rfxEvent e left outer join a.supplierBqItems supBqitm inner join a.bq bq inner join a.supplier as sup, RftEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and e.id = :eventId and es.disqualify=:disqualify and supBqitm.parent is not null and es.submissionStatus = :status ");
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
	public List<RfaSupplierBqPojo> findRftSupplierParticipation(String eventId) {

		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(es.numberOfBids,sup.companyName,sup.id,es.disqualify,es.ipAddress,es.submissionStatus,es.rejectedTime,es.supplierEventReadTime,es.supplierSubmittedTime) from  RftEventSupplier es inner join es.rfxEvent e inner join es.supplier sup where es.submissionStatus not in (:status) and e.id = :eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		List<SubmissionStatusType> list = new ArrayList<SubmissionStatusType>();
		list.add(SubmissionStatusType.INVITED);
		query.setParameter("status", list);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierBq> findRftSummarySupplierBqbyEventId(String eventId) {
		// TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
		//final Query query = getEntityManager().createQuery("select distinct a from RftSupplierBq a inner join fetch a.supplierBqItems item inner join a.rfxEvent ev  where ev.id = :id order by item.level, item.order");
		final Query query = getEntityManager().createQuery("select distinct a from RftSupplierBq a inner join fetch a.supplierBqItems item inner join a.rfxEvent ev  where ev.id = :id");
		query.setParameter("id", eventId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public RftSupplierBq findRftSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
		Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RftSupplierBq(a.supplierBqStatus, b.id) from RftSupplierBq a inner join a.rfxEvent as e inner join a.bq as b where a.rfxEvent.id = :eventId and a.supplier.id = :supplierId and a.bq.id = :bqId order by a.bqOrder ");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("bqId", bqId);
		List <RftSupplierBq> bqList =query.getResultList();
		if (CollectionUtil.isNotEmpty(bqList)) {
			return bqList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public long findPendingBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select count(r.id) from RftSupplierBq r where r.rfxEvent.id =:eventId and r.supplier.id =:supplierId and r.supplierBqStatus in (:status) ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("status", Arrays.asList(SupplierBqStatus.DRAFT, SupplierBqStatus.PENDING));
		return ((Number) query.getSingleResult()).longValue();
	}

}
