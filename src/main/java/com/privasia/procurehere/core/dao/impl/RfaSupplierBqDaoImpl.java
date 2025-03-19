package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaSupplierBqDao;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SupplierBqStatus;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class RfaSupplierBqDaoImpl extends GenericDaoImpl<RfaSupplierBq, String> implements RfaSupplierBqDao {

	protected static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierBq findBqByEventIdAndSupplierId(String id, String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("from RfaSupplierBq rb inner join fetch rb.event as r where r.id = :id and rb.bq.id = :bqId and rb.supplier.id=:supplierId");
		query.setParameter("id", id);
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		List<RfaSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierBq findBqByBqId(String id, String supplierId) {
		LOG.info("Here we are to complete the trasection : supp " + supplierId + " : bq : " + id);
		// TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
		// final Query query = getEntityManager().createQuery("from RfaSupplierBq as r inner join fetch r.bq as rb inner
		// join fetch r.supplierBqItems as bi where rb.id = :id and r.supplier.id =:supplierId order by bi.level,
		// bi.order");
		final Query query = getEntityManager().createQuery("from RfaSupplierBq as r inner join fetch r.bq as rb inner join fetch r.supplierBqItems as bi where rb.id = :id and r.supplier.id =:supplierId");
		query.setParameter("id", id);
		query.setParameter("supplierId", supplierId);
		List<RfaSupplierBq> uList = query.getResultList();
		LOG.info("ulist Size : " + uList.size());
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaSupplierBq> findSupplierBqbyEventId(String eventId) {
		// TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
		// final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBq a inner join fetch
		// a.event sp inner join fetch a.bq left outer join fetch a.supplierBqItems bi where sp.id = :id order by
		// bi.level, bi.order");
		final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBq a inner join fetch a.event sp inner join fetch a.bq left outer join fetch a.supplierBqItems bi where sp.id = :id");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaSupplierBq> findRfaSupplierBqBySupplierIds(List<String> supplierIds, String eventId, Integer limitSupplier) {
		StringBuilder hsql = new StringBuilder("select distinct a from RfaSupplierBq a inner join fetch a.event e  inner join fetch a.supplier as sup inner join fetch a.bq left outer join fetch a.supplierBqItems bi where e.id = :eventId and sup.id in (:supplierIds) order by a.totalAfterTax");
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (limitSupplier != null) {
			query.setMaxResults(limitSupplier);
		}
		query.setParameter("supplierIds", supplierIds);
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings({ "unchecked", "unused" })
	public List<RfaSupplierBqPojo> getSupplierListForAuctionConsole(String eventId, Integer limitSupplier) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(a.initialPrice, a.totalAfterTax, es.numberOfBids, a.differncePerToInitial, sup.companyName, sup.id, es.auctionRankingOfSupplier, e.status, e.eventStart, e.eventEnd, e.auctionResumeDateTime, es.auctionOnlineDateTime, e.auctionType,es.disqualify,es.disqualifyRemarks) from RfaSupplierBq a inner join a.event e inner join a.supplier as sup, RfaEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.submissionStatus = :status and e.id = :eventId order by es.auctionRankingOfSupplier ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (limitSupplier != null) {
			query.setMaxResults(limitSupplier);
		}
		query.setParameter("eventId", eventId);
		query.setParameter("status", SubmissionStatusType.COMPLETED);

		List<RfaSupplierBqPojo> suuLisyt = query.getResultList();
		// for (RfaSupplierBqPojo suppPojo : suuLisyt) {
		// LOG.debug("supp : " + suppPojo.getSupplierCompanyName() + " : rank : " + suppPojo.getRankOfSupplier());
		// }
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierBq findSupplierBqByBqId(String bqId) {
		final Query query = getEntityManager().createQuery("from RfaSupplierBq as r inner join fetch r.bq as bq inner join fetch r.supplierBqItems where bq.id = :bqId");
		query.setParameter("bqId", bqId);
		List<RfaSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public BigDecimal getMaxBidFromAllBidsOfSuppliers(String bqId) {
		// final Query query = getEntityManager().createQuery("select max(r.totalAfterTax) from RfaSupplierBq as r inner
		// join r.bq as bq where bq.id = :bqId");
		final Query query = getEntityManager().createQuery("select max(r.totalAfterTax) from RfaSupplierBq as r where r.bq.id = :bqId and r.supplier.id in ( select distinct s.supplier.id from RfaEventSupplier s where s.rfxEvent = r.event and s.submissionStatus = :submissionStatus and s.disqualify = false )");

		query.setParameter("bqId", bqId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<BigDecimal> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Integer getCountsOfSameBidBySupliers(String bqId, String eventId, BigDecimal bidAmount) {
		final Query query = getEntityManager().createQuery("select count(r.totalAfterTax) from RfaSupplierBq as r inner join r.bq as bq inner join r.event e where r.totalAfterTax = :bidAmount and bq.id = :bqId and e.id = :eventId");
		query.setParameter("bqId", bqId);
		query.setParameter("eventId", eventId);
		query.setParameter("bidAmount", bidAmount);
		LOG.info("bid amount : " + bidAmount + " : Bq Id : " + bqId + " : event id : " + eventId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public Integer getCountsOfRfaSupplierBqBySuplierIdAndBqId(String eventId, String supplierId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfaSupplierBq as r inner join r.bq as bq inner join r.supplier as sup inner join r.event e where e.id = :eventId and sup.id = :supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBq> getAllBqsBySupplierId(String eventId, String supplierId) {
		final Query query = getEntityManager().createQuery("select distinct a.supplierBq from RfaSupplierBqItem a where a.event.id = :eventId and a.supplier.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);

		return query.getResultList();
	}

	// @Override
	// public void updateDuctionAuctionBidForSupplier(String eventId, String supplierId, BigDecimal bidPrice) {
	// StringBuilder hsql = new StringBuilder("update RfaSupplierBq rsb set rsb.totalAfterTax = :totalAfterTax,
	// rsb.revisedGrandTotal = :revisedGrandTotal where rsb.supplier.id = :supplierId and rsb.event.id = :eventId ");
	// final Query query = getEntityManager().createQuery(hsql.toString());
	// query.setParameter("supplierId", supplierId);
	// query.setParameter("eventId", eventId);
	// query.setParameter("totalAfterTax", bidPrice);
	// query.setParameter("revisedGrandTotal", bidPrice);
	// query.executeUpdate();
	// }

	@Override
	public void updateDuctionAuctionBidForSupplier(String eventId, String supplierId, BigDecimal bidPrice) {
		StringBuilder hsql = new StringBuilder("update RfaSupplierBq rsb set rsb.totalAfterTax = :totalAfterTax where rsb.supplier.id = :supplierId and rsb.event.id = :eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.setParameter("totalAfterTax", bidPrice);
		// query.setParameter("revisedGrandTotal", bidPrice);
		query.executeUpdate();
	}

	/*
	 * @Override public void updateLumsumAuction(String eventId, String supplierId, String bqId, BigDecimal
	 * totalAfterTax, BigDecimal differencePercentage) { StringBuilder hsql = new
	 * StringBuilder("update RfaSupplierBq rsb set rsb.totalAfterTax = :totalAfterTax, rsb.revisedGrandTotal = :revisedGrandTotal, rsb.differncePerToInitial = :differencePercentage where rsb.supplier.id = :supplierId and rsb.event.id = :eventId and rsb.bq.id = :bqId"
	 * ); final Query query = getEntityManager().createQuery(hsql.toString()); query.setParameter("supplierId",
	 * supplierId); query.setParameter("eventId", eventId); query.setParameter("bqId", bqId);
	 * query.setParameter("totalAfterTax", totalAfterTax); query.setParameter("revisedGrandTotal", totalAfterTax);
	 * query.setParameter("differencePercentage", differencePercentage); query.executeUpdate(); }
	 */

	@Override
	public void updateLumsumAuction(String eventId, String supplierId, String bqId, BigDecimal totalAfterTax, BigDecimal differencePercentage) {
		StringBuilder hsql = new StringBuilder("update RfaSupplierBq rsb set rsb.totalAfterTax = :totalAfterTax, rsb.differncePerToInitial = :differencePercentage where rsb.supplier.id = :supplierId and rsb.event.id = :eventId and rsb.bq.id = :bqId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		query.setParameter("totalAfterTax", totalAfterTax);
		// query.setParameter("revisedGrandTotal", totalAfterTax);
		query.setParameter("differencePercentage", differencePercentage);
		query.executeUpdate();
	}

	@Override
	public RfaSupplierBq getRfaSupplierBqForLumsumBid(String bqId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfaSupplierBq(rsb.totalAfterTax, rsb.initialPrice, rsb.differncePerToInitial) from RfaSupplierBq rsb where rsb.supplier.id = :supplierId and rsb.bq.id = :bqId");
		LOG.debug("Supplier ID : " + supplierId + " : Bq Id : " + bqId);
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("bqId", bqId);
		return (RfaSupplierBq) query.getSingleResult();
	}

	@Override
	@Transactional(readOnly = false)
	public void discardSupplierBqforEventId(String eventId) {

		Query query = getEntityManager().createQuery("delete RfaSupplierBqItem as r  where r.event.id = :eventId");
		query.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("delete  RfaSupplierBq as r  where r.event.id = :eventId");
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaSupplierBq> findRfaSummarySupplierBqbyEventId(String eventId) {
		// TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
		// final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBq a inner join fetch
		// a.supplierBqItems item inner join a.event ev where ev.id = :id order by item.level, item.order");
		final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBq a inner join fetch a.supplierBqItems item inner join a.event ev  where ev.id = :id");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaSupplierBq> findRfaSupplierBqbyEventIdAndSupplierId(String eventId, String supplierId) {
		// TODO: this Query is commented for as its not working in SQL Server, Verify the methods where it is use.
		// final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBq a inner join fetch
		// a.supplierBqItems item inner join a.event ev inner join fetch a.bq b where ev.id = :id and a.supplier.id =
		// :supplierId order by item.level, item.order");
		final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBq a inner join fetch a.supplierBqItems item inner join a.event ev inner join fetch a.bq b where ev.id = :id and a.supplier.id = :supplierId order by a.bqOrder");
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BigDecimal> grandTotalOfBqByEventIdAndSupplierId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select r.grandTotal from RfaSupplierBq r where r.event.id =:eventId and r.supplier.id =:supplierId ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<BigDecimal> grandTotalList = (List<BigDecimal>) query.getResultList();
		return grandTotalList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BigDecimal getMinBidFromAllBidsOfSuppliers(String bqId) {
		final Query query = getEntityManager().createQuery("select min(r.totalAfterTax) from RfaSupplierBq as r where r.bq.id = :bqId and r.supplier.id in ( select distinct s.supplier.id from RfaEventSupplier s where s.rfxEvent = r.event and s.submissionStatus = :submissionStatus and s.disqualify = false)");
		query.setParameter("bqId", bqId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<BigDecimal> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaSupplierBq> rfaSupplierBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId) {
		// TODO Condition should be r.grandTotal <> 0 In all RFX because grandtotal can be negative
		StringBuilder hsql = new StringBuilder("select new RfaSupplierBq(r.name, r.grandTotal,r.buyerSubmited) from RfaSupplierBq r where r.event.id =:eventId and r.supplier.id =:supplierId and r.grandTotal > 0 ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RfaSupplierBq> supplierBqList = (List<RfaSupplierBq>) query.getResultList();
		return supplierBqList;
	}

	@Override
	public RfaSupplierBq getRfaSupplierBqByEventIdAndSupplierId(String eventId, String supplierId) {
		LOG.info("Supplier Id : " + supplierId);
		final Query query = getEntityManager().createQuery("from RfaSupplierBq a  where a.event.id = :id and a.supplier.id = :supplierId");
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		return (RfaSupplierBq) query.getSingleResult();
	}

	@Override
	public void discardSupplierBqforSupplierId(String eventId, String supplierId) {

		Query query = getEntityManager().createQuery("update RfaSupplierBqItem as r set r.parent = null where r.event.id = :eventId and r.supplier.id = :supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.executeUpdate();

		query = getEntityManager().createQuery("delete RfaSupplierBqItem as r  where r.event.id = :eventId and r.supplier.id = :supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.executeUpdate();

		query = getEntityManager().createQuery("delete  RfaSupplierBq as r  where r.event.id = :eventId and r.supplier.id = :supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BigDecimal getLastTotalBqAmountBySupplierId(String eventId, String supplierId) {
		final Query query = getEntityManager().createQuery("select r.totalAfterTax from RfaSupplierBq as r where r.event.id = :eventId and r.supplier.id = :supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<BigDecimal> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Integer getCountsOfSamePreBidBySupliers(String eventId, BigDecimal bidAmount, String supplierId) {
		LOG.info("Supplier ID  : " + supplierId);
		StringBuilder hsql = new StringBuilder("select count(r.initialPrice) from RfaSupplierBq as r inner join r.event e where r.initialPrice = :bidAmount and e.id = :eventId");
		if (supplierId != null) {
			hsql.append(" and r.supplier.id <> :supplierId");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("bidAmount", bidAmount);
		if (supplierId != null) {
			query.setParameter("supplierId", supplierId);
		}

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	@SuppressWarnings({ "unchecked", "unused" })
	public List<RfaSupplierBqPojo> getSupplierListForDutchAuctionConsole(String eventId, Integer limitSupplier) {
		LOG.info("event Id : " + eventId + " limit :  " + limitSupplier);
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(sup.companyName, sup.id, e.status, e.auctionType,es.disqualify,es.auctionOnlineDateTime) from RfaEventSupplier es inner join es.rfxEvent e inner join es.supplier as sup where es.supplier.id = sup.id and e.id = :eventId and es.submitted = :submitted");
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (limitSupplier != null) {
			query.setMaxResults(limitSupplier);
		}
		query.setParameter("eventId", eventId);
		query.setParameter("submitted", Boolean.TRUE);

		List<RfaSupplierBqPojo> suuLisyt = query.getResultList();
		// for (RfaSupplierBqPojo suppPojo : suuLisyt) {
		// LOG.debug("supp : " + suppPojo.getSupplierCompanyName() + " : rank : " + suppPojo.getRankOfSupplier());
		// }
		return query.getResultList();
	}

	@Override
	@SuppressWarnings({ "unchecked", "unused" })
	public List<RfaSupplierBqPojo> findRfaSupplierBqBySupplierIdsOdrByRank(String eventId, Integer limitSupplier) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(a.initialPrice, sup.companyName, es.auctionRankingOfSupplier, a.revisedGrandTotal) from RfaSupplierBq a inner join a.event e inner join a.supplier as sup, RfaEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.submissionStatus = :status and e.id = :eventId order by es.auctionRankingOfSupplier ");
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
	public List<RfaSupplierBqPojo> getAllRfaTopEventSuppliersIdByEventId(String eventId, Integer limit, String bqId) {
		LOG.info("event Id : " + eventId + " limit :  " + limit);
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(es.auctionRankingOfSupplier,sup.companyName, a.totalAfterTax,sup.id,a.initialPrice,a.grandTotal,a.additionalTax,a.totalAfterTax,bq.name) from RfaSupplierBq a inner join a.event e inner join a.bq bq inner join a.event e inner join a.supplier as sup, RfaEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and e.id = :eventId and es.disqualify=:disqualify  and es.submissionStatus = :status");
		if (StringUtils.checkString(bqId).length() > 0) {
			hsql.append(" and bq.id =:bqId ");
		}
		hsql.append(" and es.auctionRankingOfSupplier is not null order by es.auctionRankingOfSupplier");
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

	@Override
	@SuppressWarnings({ "unchecked", "unused" })
	public List<RfaSupplierBqPojo> findRfaSupplierBqCompleteNessBySupplierIdsOdrByRank(String eventId, Integer limitSupplier) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(es.ipAddress  ,a.id,sup.id,a.initialPrice, sup.companyName, es.auctionRankingOfSupplier, a.totalAfterTax ,count(case when supBqitm.unitPrice  > 0 then 1 else null end ),count(distinct supBqitm.id)) from RfaSupplierBq a inner join a.event e inner join a.supplier as sup left outer join a.supplierBqItems supBqitm, RfaEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.submissionStatus = :status and e.id = :eventId and supBqitm.parent is not null group by es.ipAddress,a.id,sup.id,a.initialPrice, sup.companyName, es.auctionRankingOfSupplier, a.totalAfterTax order by es.auctionRankingOfSupplier  ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (limitSupplier != null) {
			query.setMaxResults(limitSupplier);
		}
		query.setParameter("eventId", eventId);
		query.setParameter("status", SubmissionStatusType.COMPLETED);

		List<RfaSupplierBqPojo> suuLisyt = query.getResultList();
		return query.getResultList();
	}

	@Override
	@SuppressWarnings({ "unchecked", "unused" })
	public List<RfaSupplierBqPojo> findRfaSupplierBqByDisqualifiedSupplierIdsOdrByRank(String eventId, Integer limitSupplier) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(ds.name,es.disqualifyRemarks,es.disqualifiedTime,a.id,sup.id,a.initialPrice, sup.companyName, es.auctionRankingOfSupplier, a.revisedGrandTotal ,count(case when supBqitm.unitPrice  > 0 then 1 else null end ),count(distinct supBqitm.id)) from RfaSupplierBq a inner join a.event e inner join a.supplier as sup left outer join a.supplierBqItems supBqitm, RfaEventSupplier es left outer join es.disqualifiedBy as ds where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.submissionStatus = :status and e.id = :eventId and es.disqualify=:disqualify and supBqitm.parent is not null  group by ds.name,es.disqualifyRemarks,es.disqualifiedTime,a.id,sup.id,a.initialPrice, sup.companyName, es.auctionRankingOfSupplier, a.revisedGrandTotal order by es.auctionRankingOfSupplier");
		final Query query = getEntityManager().createQuery(hsql.toString());
		if (limitSupplier != null) {
			query.setMaxResults(limitSupplier);
		}
		query.setParameter("eventId", eventId);
		query.setParameter("disqualify", Boolean.TRUE);
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		List<RfaSupplierBqPojo> suuLisyt = query.getResultList();
		return query.getResultList();
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public List<RfaSupplierBqPojo> findRfaTopSupplierBqBySupplierIdsOdrByRank(String eventId, Integer limitSupplier) {
		// Initailly we calculate the revised grand total now we are taking from total after tax because we have change
		// the logic, please check updateItemDetails method for RFA
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(a.initialPrice, sup.companyName, es.auctionRankingOfSupplier, a.totalAfterTax) from RfaSupplierBq a inner join a.event e inner join a.supplier as sup, RfaEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.disqualify=:disqualify and es.submissionStatus = :status and e.id = :eventId order by es.auctionRankingOfSupplier ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setMaxResults(limitSupplier);
		query.setParameter("eventId", eventId);
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		query.setParameter("disqualify", Boolean.FALSE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqPojo> getAllRfaSuppliersIdByEventId(String eventId) {
		// revised grand total is in no use
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(es.auctionRankingOfSupplier,sup.companyName, a.totalAfterTax,sup.id,a.initialPrice) from RfaSupplierBq a inner join a.event e inner join a.event e inner join a.supplier as sup, RfaEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and e.id = :eventId and es.disqualify=:disqualify and es.submissionStatus = :status order by es.auctionRankingOfSupplier");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		query.setParameter("disqualify", Boolean.FALSE);

		return query.getResultList();
	}

	@Override
	public BigDecimal findMinLeadingPrice(String eventId, String bqId, String suppId) {

		final Query query = getEntityManager().createQuery("select min(rb.totalAfterTax) from RfaSupplierBq rb  where rb.event.id = :id and rb.bq.id = :bqId and rb.supplier.id=:supplierId");
		query.setParameter("id", eventId);
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", suppId);
		long minLead = ((BigDecimal) query.getSingleResult()).intValue();
		BigDecimal minLeading = new BigDecimal(minLead);
		return minLeading;
	}

	@Override
	public BigDecimal findMaxLeadingPrice(String eventId, String bqId, String suppId) {
		final Query query = getEntityManager().createQuery("select max(rb.totalAfterTax) from RfaSupplierBq rb  where rb.event.id = :id and rb.bq.id = :bqId and rb.supplier.id=:supplierId");
		query.setParameter("id", eventId);
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", suppId);
		long minLead = 0;
		if (query.getSingleResult() != null) {
			minLead = ((BigDecimal) query.getSingleResult()).intValue();
		}
		BigDecimal minLeading = new BigDecimal(minLead);
		return minLeading;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBq> findBqListByBqIds(List<String> bqIdlist) {
		final Query query = getEntityManager().createQuery("select distinct a from RfaSupplierBq a where a.id in (:ids)");
		query.setParameter("ids", bqIdlist);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EvaluationSuppliersBqPojo> getAllBqsByBqIdsAndEventId(String bqid, String id, AuctionType auctionType) {

		String hql = "select distinct new com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo(sup.id, sup.companyName, a.totalAfterTax, a.additionalTax, a.grandTotal,bq.id, bq.name,a.remark ,es.revisedBidSubmitted) from RfaSupplierBq a inner join a.event sp inner join a.bq bq inner join a.supplier sup,  RfaEventSupplier es inner join es.rfxEvent e inner join es.supplier esup where sp.id = :id and e.id = sp.id and sup.id = esup.id and bq.id =:bqIds and es.submissionStatus =:status  order by a.totalAfterTax ";

		if (AuctionType.FORWARD_DUTCH == auctionType || AuctionType.FORWARD_ENGISH == auctionType || AuctionType.FORWARD_SEALED_BID == auctionType) {
			hql += " desc";
		} else {
			hql += " asc";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		query.setParameter("bqIds", bqid);
		query.setParameter("status", SubmissionStatusType.COMPLETED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBqPojo> findRfaSupplierBqCompleteNessBySupplierIds(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.RfaSupplierBqPojo(es.ipAddress,a.id,sup.id, sup.companyName, es.auctionRankingOfSupplier,es.numberOfBids,es.rejectedTime,es.supplierEventReadTime,es.submissionStatus,es.disqualify,a.totalAfterTax,count(case when supBqitm.unitPrice  > 0 then 1 else null end ),count(distinct supBqitm.id)) from RfaSupplierBq a inner join a.event e inner join a.supplier as sup left outer join a.supplierBqItems supBqitm, RfaEventSupplier es where es.supplier.id = sup.id and es.rfxEvent.id = e.id and es.submissionStatus <> :status and supBqitm.parent is not null and e.id = :eventId group by es.ipAddress,a.id,sup.id,sup.companyName, es.auctionRankingOfSupplier,es.numberOfBids,es.rejectedTime,es.supplierEventReadTime,es.submissionStatus,es.disqualify,a.totalAfterTax order by es.auctionRankingOfSupplier  ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", SubmissionStatusType.INVITED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierBq findBqByEventIdAndSupplierIdOfQualifiedSupplier(String id, String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("from RfaSupplierBq rb inner join fetch rb.event as r inner join fetch r.suppliers as s where r.id = :id and rb.bq.id = :bqId and rb.supplier.id=:supplierId and s.disqualify =:disqualify");
		query.setParameter("id", id);
		query.setParameter("bqId", bqId);
		query.setParameter("disqualify", Boolean.FALSE);
		query.setParameter("supplierId", supplierId);
		List<RfaSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public BigDecimal findInitialMinPrice(String eventId, AuctionType auctionType) {
		String hql = "select ";
		if (AuctionType.FORWARD_DUTCH == auctionType || AuctionType.FORWARD_ENGISH == auctionType || AuctionType.FORWARD_SEALED_BID == auctionType) {
			hql += " max(rb.initialPrice)";
		} else {
			hql += " min(rb.initialPrice)";
		}
		hql += " from RfaSupplierBq rb inner join  rb.event as r inner join  r.suppliers as s where r.id = :id and s.disqualify =:disqualify";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", eventId);
		query.setParameter("disqualify", Boolean.FALSE);

		return (BigDecimal) query.getSingleResult();
	}

	@Override
	public BigDecimal findPostMinPrice(String eventId, AuctionType auctionType) {
		String hql = "select ";
		if (AuctionType.FORWARD_DUTCH == auctionType || AuctionType.FORWARD_ENGISH == auctionType || AuctionType.FORWARD_SEALED_BID == auctionType) {
			hql += " max(rb.totalAfterTax)";
		} else {
			hql += " min(rb.totalAfterTax)";
		}
		hql += " from RfaSupplierBq rb inner join  rb.event as r inner join  r.suppliers as s where r.id = :id and s.disqualify =:disqualify";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", eventId);
		query.setParameter("disqualify", Boolean.FALSE);

		return (BigDecimal) query.getSingleResult();
	}

	@Override
	public Long getRfaSupplierBqCountForEvent(String eventId) {
		String hql = "select count(*) FROM RfaSupplierBq bq  WHERE bq.event.id=:eventId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("eventId", eventId);
		try {
			return (Long) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0l;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierBq findRfaSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId) {
		Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfaSupplierBq(a.supplierBqStatus, b.id) from RfaSupplierBq a inner join a.event as e inner join a.bq as b where a.event.id = :eventId and a.supplier.id = :supplierId and a.bq.id = :bqId order by a.bqOrder ");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("bqId", bqId);
		List<RfaSupplierBq> bqList = query.getResultList();
		if (CollectionUtil.isNotEmpty(bqList)) {
			return bqList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public long findPendingBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select count(r.id) from RfaSupplierBq r where r.event.id =:eventId and r.supplier.id =:supplierId and r.supplierBqStatus in (:status) ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("status", Arrays.asList(SupplierBqStatus.DRAFT, SupplierBqStatus.PENDING));
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public Boolean isBqPreBidPricingExistForSuppliers(String eventId) {
		StringBuilder hsql = new StringBuilder("select count(r.id) from RfaSupplierBq r where r.event.id =:eventId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).longValue() > 0;
	}

}
