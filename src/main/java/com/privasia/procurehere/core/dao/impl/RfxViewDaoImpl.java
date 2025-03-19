package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.privasia.procurehere.core.dao.RfxViewDao;
import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.pojo.GlobalSearchPojo;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class RfxViewDaoImpl extends GenericDaoImpl<RfxView, String> implements RfxViewDao {

	private static final Logger LOG = LogManager.getLogger(RfxViewDaoImpl.class);

	@Override
	@SuppressWarnings("unchecked")
	public List<RfxView> getAllApprovedEvents() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfxView(v.id, v.eventPublishDate, v.eventName, v.type, v.eventEnd) from RfxView v where v.status = :eventStatus");
		query.setParameter("eventStatus", EventStatus.APPROVED);
		List<RfxView> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfxView> getAllActiveEvents() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfxView(v.id, v.eventPublishDate, v.eventName, v.type, v.eventEnd, v.referanceNumber) from RfxView v where v.status = :eventStatus");
		query.setParameter("eventStatus", EventStatus.ACTIVE);
		List<RfxView> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<GlobalSearchPojo> getAllRfxEventFromGlobalSearch(String searchVal, String tenantId, boolean isSupplier, String userId) {
		LOG.info("searchValue:" + searchVal + " isSupplier " + isSupplier + " tenantId : " + tenantId + " userId : " + userId);

		String sql = "";
		if (isSupplier) {
			if (StringUtils.checkString(userId).length() == 0) {
				sql = getNativeRfxQueryForSupplierAdminGlobalSearch();
			} else {
				sql = getNativeRfxQueryForSupplierGlobalSearch();
			}
		} else {
			if (StringUtils.checkString(userId).length() == 0) {
				sql = getNativeRfxQueryForBuyerAdminGlobalSearch();
			} else {
				sql = getNativeRfxQueryForBuyerGlobalSearch();
			}
		}

 
//		 LOG.info("SQL : " + sql);
		final Query query = getEntityManager().createNativeQuery(sql, "globalSearchResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		if (isSupplier) {
			query.setParameter("status", Arrays.asList(EventStatus.ACTIVE.toString(), EventStatus.SUSPENDED.toString(), EventStatus.FINISHED.toString(), EventStatus.CLOSED.toString(), EventStatus.COMPLETE.toString()));
		} else {
			query.setParameter("status", Arrays.asList(EventStatus.DRAFT.toString(), EventStatus.PENDING.toString(), EventStatus.ACTIVE.toString(), EventStatus.SUSPENDED.toString(), EventStatus.FINISHED.toString(), EventStatus.CLOSED.toString(), EventStatus.COMPLETE.toString(), EventStatus.APPROVED.toString(), EventStatus.CANCELED.toString()));
		}
		query.setParameter("searchVal", "%" + searchVal.toUpperCase() + "%");

		List<GlobalSearchPojo> list = query.getResultList();
		// LOG.info("Returned list size : " + list.size());

		// Comment this return because it always return the list before filtering so it always show all the events
		// Kapil
		return list;
 	}

	@Override
	public long totalEventInvitedSupplier(String suppId, String buyerId) {
		StringBuffer sql = new StringBuffer("select count(distinct v) from RfxView v where v.supplier.id = :suppId ");
		if(StringUtils.checkString(buyerId).length() > 0) {
			sql.append(" and v.tenantId = :tenantId ");
		}
		
		final Query query = getEntityManager().createQuery(sql.toString());
		query.setParameter("suppId", suppId);
		if(StringUtils.checkString(buyerId).length() > 0) {
			query.setParameter("tenantId", buyerId);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long totalEventParticipatedSupplier(String suppId, String buyerId) {
		StringBuffer sql = new StringBuffer("select count(distinct v) from RfxView v where v.supplier.id = :suppId and v.submitted is true ");
		if(StringUtils.checkString(buyerId).length() > 0) {
			sql.append(" and v.tenantId = :tenantId ");
		}
		
		final Query query = getEntityManager().createQuery(sql.toString());
		query.setParameter("suppId", suppId);
		if(StringUtils.checkString(buyerId).length() > 0) {
			query.setParameter("tenantId", buyerId);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public long totalEventAwardedSupplier(String suppId) {
		long rftEventAwardedSupplier = ((Number) getEntityManager().createQuery("select count(distinct t) from RftEvent t left outer join t.awardedSuppliers s where s.id = :suppId").setParameter("suppId", suppId).getSingleResult()).longValue();
		long rfpEventAwardedSupplier = ((Number) getEntityManager().createQuery("select count(distinct p) from RfpEvent p left outer join p.awardedSuppliers s where s.id = :suppId").setParameter("suppId", suppId).getSingleResult()).longValue();
		long rfqEventAwardedSupplier = ((Number) getEntityManager().createQuery("select count(distinct q) from RfqEvent q left outer join q.awardedSuppliers s where s.id = :suppId").setParameter("suppId", suppId).getSingleResult()).longValue();
		long rfaEventAwardedSupplier = ((Number) getEntityManager().createQuery("select count(distinct a) from RfaEvent a left outer join a.awardedSuppliers s where s.id = :suppId").setParameter("suppId", suppId).getSingleResult()).longValue();

		return rftEventAwardedSupplier + rfpEventAwardedSupplier + rfqEventAwardedSupplier + rfaEventAwardedSupplier;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public long totalEventAwardedSupplierAndBuyer(String suppId, String buyerId) {
		long rftEventAwardedSupplier = ((Number) getEntityManager().createQuery("select count(distinct t) from RftEvent t left outer join t.awardedSuppliers s where s.id = :suppId and t.tenantId = :buyerId").setParameter("suppId", suppId).setParameter("buyerId", buyerId).getSingleResult()).longValue();
		long rfpEventAwardedSupplier = ((Number) getEntityManager().createQuery("select count(distinct p) from RfpEvent p left outer join p.awardedSuppliers s where s.id = :suppId and p.tenantId = :buyerId ").setParameter("suppId", suppId).setParameter("buyerId", buyerId).getSingleResult()).longValue();
		long rfqEventAwardedSupplier = ((Number) getEntityManager().createQuery("select count(distinct q) from RfqEvent q left outer join q.awardedSuppliers s where s.id = :suppId and q.tenantId = :buyerId").setParameter("suppId", suppId).setParameter("buyerId", buyerId).getSingleResult()).longValue();
		long rfaEventAwardedSupplier = ((Number) getEntityManager().createQuery("select count(distinct a) from RfaEvent a left outer join a.awardedSuppliers s where s.id = :suppId and a.tenantId = :buyerId").setParameter("suppId", suppId).setParameter("buyerId", buyerId).getSingleResult()).longValue();

		return rftEventAwardedSupplier + rfpEventAwardedSupplier + rfqEventAwardedSupplier + rfaEventAwardedSupplier;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfxView> getEventRemindersForNotification() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfxView(v.id, v.eventStart , v.eventName, v.referanceNumber, v.type, v.eventOwner) from RfxView v  where v.status = :eventStatus and v.startMessageSent = false and v.eventStart <= :now order by v.eventStart");
		query.setParameter("eventStatus", EventStatus.ACTIVE);
		query.setParameter("now", new Date());
		List<RfxView> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfxView> getAllApprovedEventsforJob() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfxView(v.id, v.eventPublishDate, v.eventName,v.referanceNumber, v.type, v.eventEnd,cb.communicationEmail,cb.emailNotifications,v.tenantId,cb.name,cb.id,bu.unitName ) from RfxView v left outer join v.eventOwner cb left outer join v.businessUnit bu where v.status = :eventStatus");
		query.setParameter("eventStatus", EventStatus.APPROVED);
		List<RfxView> list = query.getResultList();
		return list;
	}


	@Override
	@SuppressWarnings("unchecked")
	public RfxView getRfxViewById(String id) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfxView(v.id, v.eventPublishDate, " +
				"v.eventName,v.referanceNumber, v.type, v.eventEnd, v.eventStart," +
				"cb.communicationEmail,v.tenantId,cb.name,cb.id,bu.unitName, v.eventVisibility, v.eventId, cb.emailNotifications) from RfxView v " +
				"left outer join v.eventOwner cb left outer join v.businessUnit bu where v.id = :id");
		query.setParameter("id", id);
		List<RfxView> list = query.getResultList();
		if (!CollectionUtils.isEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
	@Override
	public long getTotalAwardedSupplirForBuyer(String supplierId, String tenantId) {
		Query queryRft = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM PROC_RFT_AWARD_SUP  WHERE  SUPPLIER_ID =:supplierId AND EVENT_ID IN  (SELECT ID  FROM PROC_RFT_EVENTS e WHERE e.TENANT_ID =:tenantId)");
		queryRft.setParameter("tenantId", tenantId);
		queryRft.setParameter("supplierId", supplierId);
		long rftAwrdSupplierCount = ((Number) queryRft.getSingleResult()).longValue();

		Query queryRfp = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM PROC_RFP_AWARD_SUP  WHERE  SUPPLIER_ID =:supplierId AND EVENT_ID IN  (SELECT ID  FROM PROC_RFP_EVENTS e WHERE e.TENANT_ID =:tenantId)");
		queryRfp.setParameter("tenantId", tenantId);
		queryRfp.setParameter("supplierId", supplierId);
		long rfpAwrdSupplierCount = ((Number) queryRfp.getSingleResult()).longValue();

		Query queryRfq = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM PROC_RFQ_AWARD_SUP  WHERE  SUPPLIER_ID =:supplierId AND EVENT_ID IN  (SELECT ID  FROM PROC_RFQ_EVENTS e WHERE e.TENANT_ID =:tenantId)");
		queryRfq.setParameter("tenantId", tenantId);
		queryRfq.setParameter("supplierId", supplierId);
		long rfqAwrdSupplierCount = ((Number) queryRfq.getSingleResult()).longValue();

		Query queryRfa = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM PROC_RFA_AWARD_SUP  WHERE  SUPPLIER_ID =:supplierId AND EVENT_ID IN  (SELECT ID  FROM PROC_RFA_EVENTS e WHERE e.TENANT_ID =:tenantId)");
		queryRfa.setParameter("tenantId", tenantId);
		queryRfa.setParameter("supplierId", supplierId);
		long rfaAwrdSupplierCount = ((Number) queryRfa.getSingleResult()).longValue();
		return (rftAwrdSupplierCount + rfpAwrdSupplierCount + rfqAwrdSupplierCount + rfaAwrdSupplierCount);
	}
}
