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

import com.privasia.procurehere.core.dao.RfaEventMeetingDao;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfaEventMeetingContact;
import com.privasia.procurehere.core.entity.RfaEventMeetingDocument;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.MeetingType;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RfaEventMeetingDaoImpl extends GenericDaoImpl<RfaEventMeeting, String> implements RfaEventMeetingDao {

	private static final Logger LOG = LogManager.getLogger(RfaEventMeetingDaoImpl.class);

	@Override
	public int activateScheduledMeetings() {
		StringBuilder hsql = new StringBuilder("update RfaEventMeeting rm set rm.status = :newStatus where rm.status = :oldStatus and rm.appointmentDateTime <= :now and rm.rfxEvent.id not in ( select e.id from RfaEvent e where e.status in (:eventStatus))");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("newStatus", MeetingStatus.ONGOING);
		query.setParameter("oldStatus", MeetingStatus.SCHEDULED);
		query.setParameter("eventStatus", Arrays.asList(EventStatus.DRAFT, EventStatus.CANCELED));
		query.setParameter("now", new Date());
		return query.executeUpdate();
	}

	@Override
	public RfaEventMeeting getRfaMeetingById(String id) {
		StringBuilder hsql = new StringBuilder("from RfaEventMeeting rm  inner join fetch rm.rfxEvent as re left outer join fetch rm.inviteSuppliers as sup where rm.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		RfaEventMeeting returnList = (RfaEventMeeting) query.getSingleResult();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventMeeting> getRfaMeetingByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct rm from RfaEventMeeting as rm inner join fetch rm.rfxEvent as re where re.id= :id order by rm.appointmentDateTime");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		List<RfaEventMeeting> returnList = (List<RfaEventMeeting>) query.getResultList();
		return returnList;
	}

	@Override
	public RfaEventMeetingContact getRfaMeetingContactById(String contactId) {
		try {
			StringBuilder hsql = new StringBuilder("from RfaEventMeetingContact as rc where rc.id= :id");
			LOG.info(hsql);
			Query query = getEntityManager().createQuery(hsql.toString());
			query.setParameter("id", contactId);
			@SuppressWarnings("unchecked")
			List<RfaEventMeetingContact> uList = query.getResultList();
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
	public RfaEventMeeting getRfaMeetingByIdAndStatus(String id) {
		StringBuilder hsql = new StringBuilder("from RfaEventMeeting rm  inner join fetch rm.rfxEvent as re left outer join fetch rm.inviteSuppliers as sup where rm.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		RfaEventMeeting returnList = (RfaEventMeeting) query.getSingleResult();
		return returnList;
	}

	@Override
	public List<RfaEventMeetingContact> getRfaMeetContactByMeetId(String id) {
		StringBuilder hsql = new StringBuilder("from RfaEventMeetingContact  as rc left outer join fetch rc.rfaEventMeeting as rm where rm.id= :id");
		LOG.info(hsql);
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<RfaEventMeetingContact> mList = query.getResultList();
		return mList;
	}

	public Date getMinMeetingDateForEvent(String eventId) {
		Date retVal = null;
		StringBuilder hsql = new StringBuilder("select min(rm.appointmentDateTime) from RfaEventMeeting as rm left outer join rm.rfxEvent e where e.id = :id and rm.pastMeeting = false");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		retVal = (Date) query.getSingleResult();
		LOG.info("ret val : " + retVal);
		return retVal;
	}

	@Override
	public Integer getCountOfRfaMeetingByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(rm) from RfaEvent e inner join e.meetings rm  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllMeetings(String id) {
		Query query = getEntityManager().createQuery("delete from RfaEventMeeting em where em.rfxEvent.id = :id ");
		query.setParameter("id", id);
		int count = query.executeUpdate();

		if (count <= 0) {
			LOG.info("Error");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventMeeting> findMeetByIdAndStatus(String eventId, MeetingStatus meetStatus, String tenantId) {
		StringBuilder hsql = new StringBuilder("select distinct rm from RfaEventMeeting rm left outer join fetch rm.rfxEventMeetingContacts con inner join fetch rm.rfxEvent as re join rm.inviteSuppliers sup where re.id= :id and sup.id = :tenantId  ");
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
		List<RfaEventMeeting> returnList = query.getResultList();
		LOG.info("returnList :" + returnList.size());

		return returnList;
	}

	@Override
	public Date getMaxMeetingDateForEvent(String eventId) {
		Date retVal = null;
		StringBuilder hsql = new StringBuilder("select max(rm.appointmentDateTime) from RfaEventMeeting as rm left outer join rm.rfxEvent e where e.id = :id and rm.pastMeeting = false");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		retVal = (Date) query.getSingleResult();
		LOG.info("ret val : " + retVal);
		return retVal;
	}

	@Override
	public Integer getCountOfSupplierForMeeting(String meetingId) {
		final Query query = getEntityManager().createQuery("select count(rm) from RfaEventMeeting em inner join em.inviteSuppliers rm  where em.id =:id");
		query.setParameter("id", meetingId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	public Integer countMeetingsForSupplier(String supplierId, String eventId) {
		final Query query = getEntityManager().createQuery("select count(em) from RfaEventMeeting em inner join em.inviteSuppliers rm  where rm.id =:id  and em.rfxEvent.id = :eventId and  em.status not in(:status)");
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
		StringBuilder hsql = new StringBuilder("from RfaEventMeeting  as rc where rc.id= :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", meetingId);
		RfaEventMeeting meeting = (RfaEventMeeting) query.getSingleResult();
		if (meeting != null && meeting.getInviteSuppliers() != null) {
			for (Supplier supplier : meeting.getInviteSuppliers()) {
				list.add(supplier);
			}
		}
		return list;
	}

	@Override
	public RfaEventMeeting getMeetingForIdAndEvent(String id, String eventId) {
		StringBuilder hsql = new StringBuilder("from RfaEventMeeting rm  inner join fetch rm.rfxEvent as re where rm.id= :id and re.id =:eventId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", id);
		query.setParameter("eventId", eventId);
		RfaEventMeeting returnList = (RfaEventMeeting) query.getSingleResult();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Date> getAllMeetingDateByEventId(String eventId) {
		LOG.info("event Id : " + eventId);
		StringBuilder hsql = new StringBuilder("select distinct rm.appointmentDateTime from RfaEventMeeting rm where rm.rfxEvent.id = :eventId and rm.status = :status");
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
		StringBuilder hsql = new StringBuilder("select distinct s.id, tz.timeZone  from RfaEventMeeting rm join rm.inviteSuppliers as s, SupplierSettings sett join sett.timeZone tz where s.id = sett.supplier.id and  rm.id= :id and s not in (select supplier from RfaSupplierMeetingAttendance a where a.rfaEventMeeting = rm and a.meetingAttendanceStatus = :meetingAttendanceStatus )");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", meetingId);
		query.setParameter("meetingAttendanceStatus", MeetingAttendanceStatus.Rejected);
		return (List<Object []>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventMeetingDocument> getPlainMeetingDocument(String meetingId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfaEventMeetingDocument(md.id, md.fileName, md.credContentType, md.tenantId, md.fileSizeInKb) from RfaEventMeetingDocument md inner join md.rfaEventMeeting m where m.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", meetingId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventMeeting> getAllRfaMeetingWithPlainDocByEventId(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct rm from RfaEventMeeting as rm left outer join fetch rm.inviteSuppliers inner join fetch rm.rfxEvent as re where re.id= :id order by rm.appointmentDateTime");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		List<RfaEventMeeting> returnList = (List<RfaEventMeeting>) query.getResultList();
		return returnList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventMeeting> findMandatoryAttendMeetingsByEventId(String eventId, String supplierId) {
		LOG.info("event Id : " + eventId + " Supplier ID:" + supplierId);
		StringBuilder hsql = new StringBuilder("select distinct rm from RfaEventMeeting rm join fetch rm.inviteSuppliers as s where rm.rfxEvent.id= :eventId and rm.meetingAttendMandatory = :meetingAttendMandatory and s.id = :supplierId and rm.status <> :status");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("meetingAttendMandatory", Boolean.TRUE);
		query.setParameter("status", MeetingStatus.CANCELLED);
		return query.getResultList();
	}

	@Override
	public boolean isSiteVisitExist(String eventId) {
		StringBuilder hql = new StringBuilder("select count(rm) from RfaEventMeeting as rm where rm.rfxEvent.id= :eventId and rm.status <> :status and rm.meetingType = :meetingType");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", MeetingStatus.CANCELLED);
		query.setParameter("meetingType", MeetingType.SITE_VISIT);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaEventMeeting findMinMandatorySiteVisitMeetingsByEventId(String eventId) {
		StringBuilder hql = new StringBuilder("select distinct rm from RfaEventMeeting as rm where rm.rfxEvent.id= :eventId and rm.meetingAttendMandatory = :meetingAttendMandatory and rm.status =:status and rm.meetingType = :meetingType order by rm.appointmentDateTime");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("meetingAttendMandatory", Boolean.TRUE);
		query.setParameter("status", Arrays.asList(MeetingStatus.SCHEDULED));
		query.setParameter("meetingType", MeetingType.SITE_VISIT);
		List<RfaEventMeeting> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override	
	public List<RfaEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId) {
		StringBuilder hql = new StringBuilder("select parent from RfaEventMeeting parent where  (select count(*) from parent.inviteSuppliers ) = 0  and parent.rfxEvent.id= :eventId  ");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("eventId", eventId);
		List<RfaEventMeeting> list = query.getResultList();
 		return list;
	}

}
