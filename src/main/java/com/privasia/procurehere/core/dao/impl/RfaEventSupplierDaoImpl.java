package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RfaSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

@Repository
public class RfaEventSupplierDaoImpl extends GenericEventSupplierDaoImpl<RfaEventSupplier, String> implements RfaEventSupplierDao {

	public static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@Autowired
	UserDao userDao;
	@Autowired
	SupplierDao supplierDao;

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId) {
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RfaEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RfaEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaEventSupplier eventSupplier : list) {
				Supplier supplier = eventSupplier.getSupplier();
				if (eventSupplier.getDisqualify() == null || eventSupplier.getDisqualify() == Boolean.FALSE) {
					supplier.setDisqualified(false);
				} else {
					supplier.setDisqualified(true);
				}
				supplier.setDisqualifiedRemarks(eventSupplier.getDisqualifyRemarks());
				returnList.add(supplier);
			}
		}

		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId) {
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RfaEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus and e.disqualify = false  order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RfaEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaEventSupplier eventSupplier : list) {
				Supplier supplier = eventSupplier.getSupplier();
				if (eventSupplier.getDisqualify() == null || eventSupplier.getDisqualify() == Boolean.FALSE) {
					supplier.setDisqualified(false);
				} else {
					supplier.setDisqualified(true);
				}
				supplier.setDisqualifiedRemarks(eventSupplier.getDisqualifyRemarks());
				returnList.add(supplier);
			}
		}

		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaEvent findByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfaEvent r where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RfaEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId) {
		LOG.info("eventId: " + eventId + " supplierId: " + supplierId);
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.EventTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId, u.deleted)from RfaSupplierTeamMember tm join tm.user u join tm.eventSupplier es join es.supplier s where tm.event.id =:eventId and s.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaSupplierTeamMember> getRfaSupplierTeamMembersForEvent(String eventId, String supplierId) {
		LOG.info("eventId: " + eventId + " supplierId: " + supplierId);
		final Query query = getEntityManager().createQuery("from RfaSupplierTeamMember tm left outer join fetch tm.user u where tm.event.id =:eventId and tm.eventSupplier.supplier.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RfaSupplierTeamMember> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllRfaEventSuppliersIdByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select res.supplier.id  from RfaEventSupplier as res inner join res.supplier as s inner join res.rfxEvent as re where re.id = :id and res.submitted = :submitted and res.disqualify <> true");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("submitted", Boolean.TRUE);
		return query.getResultList();
	}

	@Override
	public RfaEventSupplier findEventSupplierByEventIdAndSupplierId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select res from RfaEventSupplier as res inner join fetch res.supplier as s inner join res.rfxEvent as re where re.id = :id and s.id = :supplierId and res.submitted = :submitted");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("submitted", Boolean.TRUE);
		// RfaEventSupplier returnRfaSuppplier = (RfaEventSupplier) query.getSingleResult();
		// LOG.info("result + : " + returnRfaSuppplier.getNumberOfBids());
		return (RfaEventSupplier) query.getSingleResult();
	}

	@Override
	public RfaEventSupplier findEventSupplierByEventIdAndSupplierIgnoreSubmit(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select res from RfaEventSupplier as res inner join fetch res.supplier as s inner join res.rfxEvent as re where re.id = :id and s.id = :supplierId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		return (RfaEventSupplier) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventSupplier> getAllRfaEventSuppliersByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select res from RfaEventSupplier as res inner join fetch res.supplier as s join res.rfxEvent as re where re.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId) {
		// LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();

		// Supplier supplier = new Supplier();
		// supplier.setId(supplierId);
		Supplier supplier = supplierDao.findById(supplierId);
		User adminUser = userDao.getAdminUserForSupplier(supplier);

		// Admin User
		if (adminUser != null && adminUser.getId().equals(userId)) {
			permissions.setOwner(true);
		} else {
			// Viewer Editor
			List<EventTeamMember> teamMembers = getSupplierTeamMembersForEvent(eventId, supplierId);
			for (EventTeamMember member : teamMembers) {
				if (member.getUser().getId().equals(userId)) {
					if (member.getTeamMemberType() == TeamMemberType.Viewer) {
						permissions.setViewer(true);
					}
					if (member.getTeamMemberType() == TeamMemberType.Editor) {
						permissions.setEditor(true);
						permissions.setViewer(false);
						break;
					}
				}
			}
		}
		return permissions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliersForSummary(String eventId) {
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RfaEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus and e.disqualify =:isQualify order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		query.setParameter("isQualify", Boolean.FALSE);
		List<RfaEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaEventSupplier eventSupplier : list) {
				Supplier supplier = eventSupplier.getSupplier();
				returnList.add(supplier);
			}
		}
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventSupplier> getAllRfaEventSuppliersListByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select res from RfaEventSupplier as res inner join fetch res.supplier as s join res.rfxEvent as re where re.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		// query.setParameter("submitted", Boolean.TRUE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer getNumberOfBidsBySupplier(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select res.numberOfBids from RfaEventSupplier as res inner join res.supplier as s join res.rfxEvent as re where re.id = :eventId and s.id = :supplierId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<Integer> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return 0;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Integer updateSupplierAuctionRank(String eventId, boolean isForwordAuction, String supplierId) {
		LOG.info("Rank fpr *******************************************************************: " + supplierId + " : event Id : " + eventId);
		StringBuilder hsql = new StringBuilder("select s.id from RfaSupplierBq rsb, RfaEventSupplier res,AuctionBids bids  join rsb.event e join rsb.supplier s where bids.bidSubmissionDate=(select max(b.bidSubmissionDate) from AuctionBids b where b.bidBySupplier.id = s.id and b.event.id = :eventId ) and e.id = :eventId and res.rfxEvent.id = :eventId and bids.event.id= :eventId and s.id= bids.bidBySupplier.id and s.id =res.supplier.id and res.submissionStatus = :submissionStatus order by rsb.totalAfterTax  ");
		Integer rankOfLoginSupplier = null;
		if (isForwordAuction) {
			hsql.append("desc ");
		}
		hsql.append(",bids.bidSubmissionDate desc");

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);

		List<String> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			int rank = 1;
			for (String supId : list) {
				if (supId != null && supId.length() > 0) {
					LOG.info("updating rank as " + rank + " for supplier Id =: " + supId);
					query = getEntityManager().createQuery("update RfaEventSupplier s set s.auctionRankingOfSupplier = :rank where s.supplier.id = :supplierId and s.rfxEvent.id = :eventId and s.disqualify <> true");
					query.setParameter("supplierId", supId);
					query.setParameter("eventId", eventId);
					query.setParameter("rank", rank);
					int resCount = query.executeUpdate();
					if (supplierId.equals(supId)) {
						rankOfLoginSupplier = rank;
						LOG.info("rankOfLoginSupplier" + rankOfLoginSupplier);
					}

					if (resCount > 0) {
						rank++;
					}
					LOG.info(" records updated " + rank + ": " + resCount);
				}
			}
		}
		LOG.info("rank : " + rankOfLoginSupplier);
		return rankOfLoginSupplier;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getRfaEventSupplierForAuctionConsole(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.Supplier(res.supplier.id, res.supplier.companyName) from RfaEventSupplier res inner join res.supplier s where res.rfxEvent.id = :eventId and res.submitted = :submitted ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("submitted", Boolean.TRUE);
		return query.getResultList();
	}

	@Override
	public void updateAuctionOnlineDateTime(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("update RfaEventSupplier s set s.auctionOnlineDateTime = :onlineDate where s.supplier.id = :supplierId and s.rfxEvent.id = :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.setParameter("onlineDate", new Date());
		query.executeUpdate();
		// LOG.info("Updated : auction login date and time : " + new Date());
	}

	@Override
	public void updateEventSupplierForAuction(String eventId, String supplierId, String ipAddress) {
		StringBuilder hsql = new StringBuilder("update RfaEventSupplier s set s.numberOfBids = s.numberOfBids+1 , s.submitted = :submitted , s.ipAddress = :ipAddress where s.supplier.id = :supplierId and s.rfxEvent.id = :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.setParameter("submitted", Boolean.TRUE);
		query.setParameter("ipAddress", ipAddress);
		query.executeUpdate();
	}

	@Override
	public long getEventSuppliersCount(String eventId) {
		final Query query = getEntityManager().createQuery("select count(supplier) from RfaEventSupplier e where e.rfxEvent.id =:eventId  ");
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public Integer getCountOfSupplierByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(s) from RfaEvent e inner join e.suppliers s  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	// @Override
	// public void updateRfaSupplierForAuction(String eventId, String supplierId) {
	// StringBuilder hsql = new StringBuilder("update RfaEventSupplier s set s.auctionOnlineDateTime = :onlineDate where
	// s.supplier.id = :supplierId and s.rfxEvent.id = :eventId");
	// final Query query = getEntityManager().createQuery(hsql.toString());
	// query.setParameter("supplierId", supplierId);
	// query.setParameter("eventId", eventId);
	// query.setParameter("onlineDate", new Date());
	// query.executeUpdate();
	// LOG.info("Updated : auction login date and time : " + new Date());
	// }

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaEventSupplier> getSupplierListForBidderDisqualify(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfaEventSupplier(es.disqualify,es.supplier.id, es.auctionRankingOfSupplier) from RfaEventSupplier es inner join es.supplier sup where es.supplier.id = supplierId and es.rfxEvent.id = :eventId order by es.auctionRankingOfSupplier ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@Override
	public boolean updateEventSupplierDisqualify(String eventId, String supplierId, User disqualifiedBy, String disqualifyremarks) {
		StringBuilder hsql = new StringBuilder("update RfaEventSupplier s set s.disqualify = :disqualify , s.disqualifyRemarks = :disqualifyRemarks, s.disqualifiedTime = :disqualifiedTime,s.disqualifiedBy = :disqualifiedBy,s.auctionRankingOfSupplier = :auctionRankingOfSupplier where s.supplier.id = :supplierId and s.rfxEvent.id = :eventId");
		LOG.info("event Id : " + eventId + " : supplier Id : " + supplierId + " : : ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.setParameter("disqualify", Boolean.TRUE);
		query.setParameter("disqualifyRemarks", disqualifyremarks);
		query.setParameter("disqualifiedTime", new Date());
		query.setParameter("disqualifiedBy", disqualifiedBy);
		query.setParameter("auctionRankingOfSupplier", new Integer(999));
		int result = query.executeUpdate();
		return result > 0;
	}

	@Override
	public void updateEventSupplierConfirm(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("update RfaEventSupplier s set s.confirmPriceDateAndTime = :confirmPriceDateAndTime , s.confirmPriceSubmitted = :confirmPriceSubmitted where s.supplier.id = :supplierId and s.rfxEvent.id = :eventId");
		LOG.info("event Id : " + eventId + " : supplier Id : " + supplierId + " : : ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("confirmPriceDateAndTime", new Date());
		query.setParameter("confirmPriceSubmitted", Boolean.TRUE);
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventSupplier> findSupplierByEventIdOnlyRank(String eventId) {

		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfaEventSupplier(es.supplier.id, es.auctionRankingOfSupplier) from RfaEventSupplier as es where es.rfxEvent.id = :id ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierBqItem getSupplierBqItemByBqItemId(String itemId, String supplierId) {
		try {
			final Query query = getEntityManager().createQuery("select rb from RfaSupplierBqItem rb inner join rb.bqItem as bi left outer join rb.supplier as sup where bi.id = :itemId and sup.id = :supplierId");
			query.setParameter("itemId", itemId);
			query.setParameter("supplierId", supplierId);
			List<RfaSupplierBqItem> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting SupplierBQ Items : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId, List<Supplier> selectedSuppliers) {
		// TODO Auto-generated method stub
		List<Supplier> returnList = new ArrayList<Supplier>();
		final Query query = getEntityManager().createQuery("select e from RfaEventSupplier e left outer join e.supplier ss where e.rfxEvent.id =:eventId  and e.submissionStatus =:submissionStatus and ss in (:selectedSuppliers) order by ss.companyName");
		query.setParameter("eventId", eventId);
		query.setParameter("selectedSuppliers", selectedSuppliers);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RfaEventSupplier> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaEventSupplier eventSupplier : list) {
				Supplier supplier = eventSupplier.getSupplier();
				if (eventSupplier.getDisqualify() == null || eventSupplier.getDisqualify() == Boolean.FALSE) {
					supplier.setDisqualified(false);
				} else {
					supplier.setDisqualified(true);
				}
				supplier.setDisqualifiedRemarks(eventSupplier.getDisqualifyRemarks());
				returnList.add(supplier);
			}
		}

		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FeePojo> getAllInvitedSuppliersByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.FeePojo(a.feePaid, a.depositPaid,a.feePaidDate,a.depositPaidDate,a.feeReference,a.depositReference,sup.companyName,sup.fullName,sup.communicationEmail,sup.companyContactNumber,sup.id,a.selfInvited) from RfaEventSupplier a inner join a.rfxEvent re inner join a.supplier as sup where re.id =:eventId order by sup.companyName ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select res from RfaEventSupplier as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where re.id = :id and s.id =:supplierId order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct res from RfaEventSupplier as res,FavouriteSupplier fs inner join res.supplier as s left outer join fs.supplier as fss inner join res.rfxEvent as re where re.id = :id and s.id =fss.id and fs.vendorCode =:supplierCode and fs.buyer.id =:tenantId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("supplierCode", supplierCode);
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.pojo.EventSupplierPojo(res.id, s.id, s.companyName, s.companyContactNumber, s.communicationEmail) from RfaEventSupplier as res inner join res.supplier as s  inner join res.rfxEvent as re where re.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setMaxResults(input.getLength());
		query.setFirstResult(input.getStart());
		return query.getResultList();
	}

	@Override
	public long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input) {
		StringBuilder hsql = new StringBuilder("select distinct count(res) from RfaEventSupplier as res inner join res.supplier as s inner join res.rfxEvent as re where re.id = :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventSupplier> getAllSubmittedSupplierByEventId(String eventId) {
		String hql = "select distinct sup from RfaEventSupplier sup left outer join fetch sup.supplier left outer join fetch sup.acceptedBy  where sup.submissionStatus=:submissionStatus and sup.disqualify=:disqualify and sup.rfxEvent.id=:eventId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		query.setParameter("disqualify", false);
		query.setParameter("eventId", eventId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventSupplier> findDisqualifySupplierByEventId(String eventID) {
		String hsql = new String("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re left outer join fetch res.disqualifiedBy as disuser left outer join fetch res.disqualifiedEnvelope as disevp where re.id = :id and (res.disqualify=:disqualify or (res.reQualifiedTime is not null) or  (res.submissionStatus = :status  and res.isRejectedAfterStart = :isRejectedAfterStart) ) order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", eventID);
		query.setParameter("disqualify", Boolean.TRUE);
		query.setParameter("isRejectedAfterStart", Boolean.TRUE);
		query.setParameter("status", SubmissionStatusType.REJECTED);
		return query.getResultList();
	}

	@Override
	public RfaEventSupplier findEventSupplierByEventIdAndSupplierRevisedSubmission(String eventId, String supplierId) {
		StringBuilder hsql = new StringBuilder("select res from RfaEventSupplier as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re where re.id = :id and s.id = :supplierId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		return (RfaEventSupplier) query.getSingleResult();
	}

	@Override
	public String getEventNameByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select e.eventName from RfaEvent e where e.id =:eventId");
		query.setParameter("eventId", eventId);
		return (String) query.getSingleResult();
	}

	@Override
	public RfaEventSupplier getSupplierByStripePaymentId(String paymentId) {
		Query query = getEntityManager().createQuery("select e from RfaEventSupplier e where e.feeReference =:paymentId");
		query.setParameter("paymentId", paymentId);
		try {
			return (RfaEventSupplier) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId) {
		String hsql = new String("from " + entityClass.getSimpleName() + " as res inner join fetch res.supplier as s inner join fetch res.rfxEvent as re left outer join fetch res.disqualifiedBy as disuser left outer join fetch res.disqualifiedEnvelope as disevp where re.id = :id and (res.disqualify=:disqualify or (res.reQualifiedTime is not null)) order by s.companyName");
		final Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", eventId);
		query.setParameter("disqualify", Boolean.TRUE);
		return query.getResultList();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<RfaEventSupplier> findEventSuppliersForTatReportByEventId(String eventId) {
		String hsql = new String("select distinct new com.privasia.procurehere.core.entity.RfaEventSupplier(res.id, res.submissionStatus) from RfaEventSupplier as res inner join res.supplier as s inner join res.rfxEvent as re where res.rfxEvent.id =:id ");
		final Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", eventId);
		return query.getResultList();
	}

}
