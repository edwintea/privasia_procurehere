package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.AuctionBidsDao;
import com.privasia.procurehere.core.entity.AuctionBids;
import com.privasia.procurehere.core.pojo.AuctionSupplierBidPojo;

/**
 * @author Arc
 */
@Repository("auctionBidsDao")
public class AuctionBidsDaoImpl extends GenericDaoImpl<AuctionBids, String> implements AuctionBidsDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<AuctionSupplierBidPojo> getAuctionBidsListBySupplierIdAndEventId(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.pojo.AuctionSupplierBidPojo(ab.amount, ab.bidSubmissionDate ) from AuctionBids as ab join ab.bidBySupplier as s join ab.event as e where s.id = :supplierId and e.id = :eventId order by ab.bidSubmissionDate ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AuctionBids> getAuctionBidsListByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("from AuctionBids as ab left outer join fetch ab.bidBySupplier as s inner join fetch ab.event as e where e.id = :eventId order by ab.bidSubmissionDate ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AuctionBids> getAuctionBidsListByEventIdForReport(String eventId) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.entity.AuctionBids(ab.id, ab.amount, ab.bidSubmissionDate, ab.rankForBid, sup.companyName, sup.id) from AuctionBids as ab join ab.bidBySupplier sup join ab.event as e where e.id = :eventId order by ab.bidSubmissionDate ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AuctionBids> getAuctionBidsForSupplier(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.entity.AuctionBids(ab.id,ab.amount, ab.bidSubmissionDate , ab.rankForBid ) from AuctionBids as ab where ab.bidBySupplier.id = :supplierId and ab.event.id = :eventId order by ab.bidSubmissionDate desc");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AuctionBids> getAuctionBidsForSupplierForReport(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.entity.AuctionBids(ab.id,ab.amount, ab.bidSubmissionDate , ab.rankForBid, sup.companyName) from AuctionBids as ab join ab.bidBySupplier sup where ab.bidBySupplier.id = :supplierId and ab.event.id = :eventId order by ab.bidSubmissionDate");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AuctionBids> getAuctionTopPreviousBidsForSupplierForReport(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.entity.AuctionBids(ab.id,ab.amount, ab.bidSubmissionDate , ab.rankForBid, sup.companyName) from AuctionBids as ab join ab.bidBySupplier sup where ab.bidBySupplier.id = :supplierId and ab.event.id = :eventId order by ab.bidSubmissionDate desc");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setMaxResults(2);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AuctionBids> getAuctionPreviousBidsForSupplierForReportOrderByDate(String supplierId, String id) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.entity.AuctionBids(ab.id,ab.amount, ab.bidSubmissionDate , ab.rankForBid, sup.companyName) from AuctionBids as ab join ab.bidBySupplier sup where ab.bidBySupplier.id = :supplierId and ab.event.id = :eventId order by ab.bidSubmissionDate desc");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", id);
		query.setParameter("supplierId", supplierId);
		query.setMaxResults(2);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AuctionBids> getRevertAuctionBidsForSupplierForReport(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select new com.privasia.procurehere.core.entity.AuctionBids(ab.id,ab.amount, ab.bidSubmissionDate , ab.rankForBid, sup.companyName,ab.isReverted,ab.remark,rb.name) from AuctionBids as ab join ab.bidBySupplier sup join ab.revertedBy rb where ab.bidBySupplier.id = :supplierId and ab.event.id = :eventId order by ab.bidSubmissionDate");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

}
