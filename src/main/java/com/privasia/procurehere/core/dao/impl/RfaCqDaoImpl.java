package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaCqDao;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author RT-Kapil
 */
@Repository
public class RfaCqDaoImpl extends GenericCqDaoImpl<RfaCq, String> implements RfaCqDao {

	@SuppressWarnings("unchecked")
	@Override
	public void isAllowToDeleteCq(String cqId) throws NotAllowedException {
		LOG.info("isAllowToDeleteCq for Id : " + cqId);
		Query query = getEntityManager().createQuery("from RfaCq a where a.id = :id");
		query.setParameter("id", cqId);
		List<RfaCq> cqList = query.getResultList();
		if (CollectionUtil.isNotEmpty(cqList)) {
			RfaCq cq = cqList.get(0);
			LOG.info("NAME : " + cq.getName() + " Event Status : " + cq.getRfxEvent().getStatus());
			// if (EventStatus.DRAFT != cq.getRfxEvent().getStatus()) {
			// throw new NotAllowedException(messageSource.getMessage("rft.cq.info.draft", new Object[] { cq.getName()
			// }, Global.LOCALE));
			// }
			// if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
			// throw new NotAllowedException(messageSource.getMessage("rft.cq.error.delete", new Object[] { cq.getName()
			// }, Global.LOCALE));
			// }

			query = getEntityManager().createQuery("select distinct cq from RfaEnvelop ev left outer join ev.cqList cq where cq.id =:id");
			query.setParameter("id", cqId);
			List<RfaCq> assignedCqList = query.getResultList();
			if (CollectionUtil.isNotEmpty(assignedCqList)) {
				throw new NotAllowedException(messageSource.getMessage("rfa.cq.info.envelope", new Object[] { cq.getName() }, Global.LOCALE));
			}

		}

	}

	public Integer getCountOfRfaCqByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(cq) from RfaEvent e inner join e.cqs cq  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getNotSectionAddedRfaCqIdsByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select cq1.name from RfaCq cq1 where cq1.rfxEvent.id = :eventid and cq1.id not in (select cq.id from RfaCq cq inner join cq.cqItems item where cq.rfxEvent.id = :eventid and item.order > 0 group by cq.id having count(item) > 0)");
		query.setParameter("eventid", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getNotSectionItemAddedRfaCqIdsByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select cq1.name from RfaCqItem i inner join i.cq cq1 where i.rfxEvent.id = :eventid and i.parent is null and  (select count(child.id) from  RfaCqItem child where child.parent.id = i.id ) = 0");
		query.setParameter("eventid", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaCq> findCqsForEventEnvelopeId(String eventId, String envelopeId) {
		final Query query = getEntityManager().createQuery("select distinct r from RfaCq r left outer join fetch r.cqItems ci  left outer join r.rfxEvent re  left outer join re.rfaEnvelop ee where re.id =:eventId and ee.id =:envelopeId  order by r.createdDate");
		query.setParameter("eventId", eventId);
		query.setParameter("envelopeId", envelopeId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaCq> findCqsForEventEnvelopeId(List<String> cqid, String id) {
		final Query query = getEntityManager().createQuery("select distinct r from RfaCq r left outer join fetch r.cqItems ci left outer join ci.parent p left outer join r.rfxEvent re  where re.id =:eventId and r.id in (:cqIds)  order by r.cqOrder, r.createdDate ");
		query.setParameter("eventId", id);
		query.setParameter("cqIds", cqid);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CqPojo> findEventForCqPojoByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.pojo.CqPojo(r.id,r.name,r.createdDate,r.cqOrder) from RfaCq r left outer join r.rfxEvent re  where re.id =:eventId order by r.cqOrder, r.createdDate");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

}
