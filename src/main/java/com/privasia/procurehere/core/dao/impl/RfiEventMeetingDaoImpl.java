package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfiEventMeetingDao;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfiEventMeetingContact;
import com.privasia.procurehere.core.entity.RfiEventMeetingDocument;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.MeetingType;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RfiEventMeetingDaoImpl extends GenericDaoImpl<RfiEventMeeting, String> implements RfiEventMeetingDao {

	private static final Logger LOG = LogManager.getLogger(RfiEventMeetingDaoImpl.class);

	@Override
	public int activateScheduledMeetings() {
		StringBuilder hsql = new StringBuilder("update RfiEventMeeting rm set rm.status = :newStatus where rm.status = :oldStatus and rm.appointmentDateTime <= :now and rm.rfxEvent.id not in ( select e.id from RfiEvent e where e.status in (:eventStatus))");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("newStatus", MeetingStatus.ONGOING);
		query.setParameter("oldStatus", MeetingStatus.SCHEDULED);
		query.setParameter("eventStatus", Arrays.asList(EventStatus.DRAFT, EventStatus.CANCELED));
		query.setParameter("now", new Date());
		return query.executeUpdate();
	}

	@Override
	public RfiEventMeeting getRfiMeetingById(String id) {
		StringBuilder hsql = new StringBuilder("from RfiEventMeeting rm  inner join fetch rm.rfxEvent as re left outer join fetch rm.inviteSuppliers as sup where rm.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		RfiEventMeeting returnList = (RfiEventMeeting) query.getSingleResult();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventMeeting> getRfiMeetingByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct rm from RfiEventMeeting as rm inner join fetch rm.rfxEvent as re where re.id= :id order by rm.appointmentDateTime");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		List<RfiEventMeeting> returnList = (List<RfiEventMeeting>) query.getResultList();
		return returnList;
	}

	@Override
	public RfiEventMeetingContact getRfiMeetingContactById(String contactId) {
		try {
			StringBuilder hsql = new StringBuilder("from RfiEventMeetingContact as rc where rc.id= :id");
			Query query = getEntityManager().createQuery(hsql.toString());
			query.setParameter("id", contactId);
			@SuppressWarnings("unchecked")
			List<RfiEventMeetingContact> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting Contact : " + nr.getMessage(), nr);
			return null;
		}

	}

	@Override
	public RfiEventMeeting getRfiMeetingByIdAndStatus(String id) {
		StringBuilder hsql = new StringBuilder("from RfiEventMeeting rm inner join fetch rm.rfxEvent as re left outer join fetch rm.inviteSuppliers as sup where rm.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		RfiEventMeeting returnList = (RfiEventMeeting) query.getSingleResult();
		return returnList;
	}

	@Override
	public List<RfiEventMeetingContact> getRfiMeetContactByMeetId(String id) {
		StringBuilder hsql = new StringBuilder("from RfiEventMeetingContact  as rc left outer join fetch rc.rfiEventMeeting as rm where rm.id= :id");
		LOG.info(hsql);
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<RfiEventMeetingContact> mList = query.getResultList();
		return mList;
	}

	public Date getMinMeetingDateForEvent(String eventId) {
		Date retVal = null;
		StringBuilder hsql = new StringBuilder("select min(rm.appointmentDateTime) from RfiEventMeeting as rm left outer join rm.rfxEvent e where e.id = :id and rm.pastMeeting = false");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		retVal = (Date) query.getSingleResult();
		return retVal;
	}

	@Override
	public Integer getCountOfRfiMeetingByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(rm) from RfiEvent e inner join e.meetings rm  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@SuppressWarnings("unused")
	@Override
	@Transactional(readOnly = false)
	public void deleteAllMeetings(String id) {
		Query query = getEntityManager().createQuery("delete from RfiEventMeeting em where em.rfxEvent.id = :id ");
		query.setParameter("id", id);
		int count = query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventMeeting> findMeetByIdAndStatus(String eventId, MeetingStatus meetStatus, String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct rm from RfiEventMeeting rm left outer join fetch rm.rfxEventMeetingContacts con inner join fetch rm.rfxEvent as re join rm.inviteSuppliers sup where re.id= :id and sup.id = :tenantId ");
		if (meetStatus != null) {
			hsql.append(" and rm.status= :status");
		}
		hsql.append(" order by rm.appointmentDateTime ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.setParameter("tenantId", tenantId);
		if (meetStatus != null) {
			query.setParameter("status", meetStatus);
			LOG.info(" MeetingStatus.convertToString(meetStatus) :" + meetStatus);
		}
		List<RfiEventMeeting> returnList = query.getResultList();
		LOG.info("returnList :" + returnList.size());

		return returnList;
	}

	@Override
	public Date getMaxMeetingDateForEvent(String eventId) {
		Date retVal = null;
		StringBuilder hsql = new StringBuilder("select max(rm.appointmentDateTime) from RfiEventMeeting as rm left outer join rm.rfxEvent e where e.id = :id and rm.pastMeeting = false");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		retVal = (Date) query.getSingleResult();
		return retVal;
	}

	@Override
	public Integer getCountOfSupplierForMeeting(String meetingId) {
		final Query query = getEntityManager().createQuery("select count(rm) from RfiEventMeeting em inner join em.inviteSuppliers rm  where em.id =:id");
		query.setParameter("id", meetingId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventMeeting> findMeetByIdAndStatus(String eventId, MeetingStatus meetStatus) {
		StringBuilder hsql = new StringBuilder("from RfiEventMeeting rm left outer join fetch rm.rfxEventMeetingContacts con inner join fetch rm.rfxEvent as re where re.id= :id ");
		if (meetStatus != null) {
			hsql.append(" and rm.status= :status");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		if (meetStatus != null) {
			query.setParameter("status", meetStatus);
			LOG.info(" MeetingStatus.convertToString(meetStatus) :" + meetStatus);
		}
		List<RfiEventMeeting> returnList = query.getResultList();
		LOG.info("returnList :" + returnList.size());

		return returnList;
	}

	@Override
	public Integer countMeetingsForSupplier(String supplierId, String eventId) {
		final Query query = getEntityManager().createQuery("select count(em) from RfiEventMeeting em inner join em.inviteSuppliers rm  where rm.id =:id  and em.rfxEvent.id = :eventId and  em.status not in(:status)");
		query.setParameter("id", supplierId);
		query.setParameter("eventId", eventId);
		List<MeetingStatus> list = new ArrayList<MeetingStatus>();
		list.add(MeetingStatus.CANCELLED);
		query.setParameter("status", list);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	public List<Supplier> getAllSuppliersByMeetId(String meetingId) {
		List<Supplier> list = new ArrayList<Supplier>();
		StringBuilder hsql = new StringBuilder("from RfiEventMeeting  as rc where rc.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", meetingId);
		RfiEventMeeting meeting = (RfiEventMeeting) query.getSingleResult();
		if (meeting != null && meeting.getInviteSuppliers() != null) {
			for (Supplier supplier : meeting.getInviteSuppliers()) {
				list.add(supplier);
			}
		}
		return list;
	}

	@Override
	public RfiEventMeeting getMeetingForIdAndEvent(String id, String eventId) {
		StringBuilder hsql = new StringBuilder("from RfiEventMeeting rm  inner join fetch rm.rfxEvent as re where rm.id= :id and re.id =:eventId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		query.setParameter("eventId", eventId);
		RfiEventMeeting returnList = (RfiEventMeeting) query.getSingleResult();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Date> getAllMeetingDateByEventId(String eventId) {
		LOG.info("event Id : " + eventId);
		StringBuilder hsql = new StringBuilder("select distinct rm.appointmentDateTime from RfiEventMeeting rm where rm.rfxEvent.id= :eventId and rm.status = :status");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", MeetingStatus.SCHEDULED);
		List<Date> returnList = query.getResultList();
		LOG.info("returnList :" + returnList.size());
		return returnList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> getNotRejectedMeetingByMeetingId(String meetingId) {
		StringBuilder hsql = new StringBuilder("select distinct s.id, tz.timeZone from RfiEventMeeting rm join rm.inviteSuppliers as s, SupplierSettings sett join sett.timeZone tz where s.id = sett.supplier.id and rm.id= :id and s not in (select supplier from RfiSupplierMeetingAttendance a where a.rfxEventMeeting = rm and a.meetingAttendanceStatus = :meetingAttendanceStatus )");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", meetingId);
		query.setParameter("meetingAttendanceStatus", MeetingAttendanceStatus.Rejected);
		return (List<Object[]>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventMeetingDocument> getPlainMeetingDocument(String meetingId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfiEventMeetingDocument(md.id, md.fileName, md.credContentType, md.tenantId, md.fileSizeInKb) from RfiEventMeetingDocument md inner join md.rfxEventMeeting m where m.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", meetingId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventMeeting> getAllRfiMeetingWithPlainDocByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct rm from RfiEventMeeting as rm left outer join fetch rm.inviteSuppliers inner join fetch rm.rfxEvent as re where re.id= :id order by rm.appointmentDateTime");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		List<RfiEventMeeting> returnList = (List<RfiEventMeeting>) query.getResultList();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventMeeting> findMandatoryAttendMeetingsByEventId(String eventId, String supplierId) {
		LOG.info("event Id : " + eventId + " Supplier ID:" + supplierId);
		StringBuilder hsql = new StringBuilder("select distinct rm from RfiEventMeeting rm join fetch rm.inviteSuppliers as s where rm.rfxEvent.id= :eventId and rm.meetingAttendMandatory = :meetingAttendMandatory and s.id = :supplierId and rm.status <> :status");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("meetingAttendMandatory", Boolean.TRUE);
		query.setParameter("status", MeetingStatus.CANCELLED);
		return query.getResultList();
	}

	@Override
	public boolean isSiteVisitExist(String eventId) {
		StringBuilder hql = new StringBuilder("select count(rm) from RfiEventMeeting as rm where rm.rfxEvent.id= :eventId and rm.status <> :status and rm.meetingType = :meetingType");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", MeetingStatus.CANCELLED);
		query.setParameter("meetingType", MeetingType.SITE_VISIT);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfiEventMeeting findMinMandatorySiteVisitMeetingsByEventId(String eventId) {
		StringBuilder hql = new StringBuilder("select distinct rm from RfiEventMeeting as rm where rm.rfxEvent.id= :eventId and rm.meetingAttendMandatory = :meetingAttendMandatory and rm.status =:status and rm.meetingType = :meetingType order by rm.appointmentDateTime");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("meetingAttendMandatory", Boolean.TRUE);
		query.setParameter("status", Arrays.asList(MeetingStatus.SCHEDULED));
		query.setParameter("meetingType", MeetingType.SITE_VISIT);
		List<RfiEventMeeting> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId) {
		StringBuilder hql = new StringBuilder("select parent from RfiEventMeeting parent where  (select count(*) from parent.inviteSuppliers ) = 0  and parent.rfxEvent.id= :eventId  ");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("eventId", eventId);
		List<RfiEventMeeting> list = query.getResultList();
		return list;
	}

}
