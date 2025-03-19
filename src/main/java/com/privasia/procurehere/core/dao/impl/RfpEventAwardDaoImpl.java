 package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpEventAwardDao;
import com.privasia.procurehere.core.entity.RfpEventAward;
import com.privasia.procurehere.core.pojo.AwardReferenceNumberPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RfpEventAwardDaoImpl extends GenericDaoImpl<RfpEventAward, String> implements RfpEventAwardDao {

	private static final Logger LOG = LogManager.getLogger(RfpEventAwardDaoImpl.class);

	@Override
	@SuppressWarnings("unchecked")
	public RfpEventAward rfpEventAwardByEventIdandBqId(String eventId, String bqId) {
		LOG.info("EventId......" + eventId + "         BQID.........." + bqId);
		final Query query = getEntityManager().createQuery("from RfpEventAward a left outer join fetch a.bq b left outer join fetch a.rfxAwardDetails ad where a.rfxEvent.id =:eventId and a.bq.id =:bqId");
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		List<RfpEventAward> uList = query.getResultList();
		// LOG.info(uList);
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfpEventAward> getRfpEventAwardsByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct a from RfpEventAward a inner join fetch a.bq b left outer join fetch a.rfxEvent re left outer join fetch re.deliveryAddress da left outer join fetch a.rfxAwardDetails ad where a.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	public Double getSumOfAwardedPrice(String eventId) {
		final Query query = getEntityManager().createQuery(" select sum(award.totalAwardPrice) from RfpEventAward award  where award.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		try {
			return ((Number) query.getSingleResult()).doubleValue();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0.0;
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public RfpEventAward rfpEventAwardDetailsByEventIdandBqId(String eventId, String bqId) {
		final Query query = getEntityManager().createQuery("from RfpEventAward a left outer join fetch a.bq b left outer join fetch a.rfxAwardDetails ad left outer join fetch ad.bqItem item where a.rfxEvent.id =:eventId and a.bq.id =:bqId order by item.level, item.order");
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		List<RfpEventAward> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<AwardReferenceNumberPojo> getRfpEventAwardDetailsByEventIdandBqId(String eventAwardId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.AwardReferenceNumberPojo(s.companyName, sum(ad.totalPrice)) from RfpEventAwardDetails ad join ad.bqItem bi join ad.supplier s where ad.eventAward.id =:eventAwardId and bi.order > 0 group by s.companyName order by s.companyName ");
		query.setParameter("eventAwardId", eventAwardId);
		List<AwardReferenceNumberPojo> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
	}
	
	@Override
	public int removeAwardDetails(String eventId) {
		String sql = "delete from RfpEventAwardDetails a where a.eventAward.id = (select id from RfpEventAward e where e.rfxEvent.id = :eventId)";
		
		Query query = getEntityManager().createQuery(sql);
		query.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("delete from RfpEventAward a where a.rfxEvent.id = :eventId");
		query.setParameter("eventId", eventId);
		return query.executeUpdate();
	}


}
