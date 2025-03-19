package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.RfqSupplierMeetingAttendance;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */
@Repository
public class RfqSupplierMeetingAttendanceDaoImpl extends GenericDaoImpl<RfqSupplierMeetingAttendance, String> implements RfqSupplierMeetingAttendanceDao {

	private static final Logger LOG = LogManager.getLogger(RfqSupplierMeetingAttendanceDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public RfqSupplierMeetingAttendance getAttendanceForMeetingForSupplier(String meetingId, String supplierId) {
		RfqSupplierMeetingAttendance attendance = null;

		StringBuilder hsql = new StringBuilder("from RfqSupplierMeetingAttendance a where a.rfxEventMeeting.id= :meetingId and a.supplier.id = :supplierId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("meetingId", meetingId);
		query.setParameter("supplierId", supplierId);
		List<RfqSupplierMeetingAttendance> returnList = query.getResultList();
		if (CollectionUtil.isNotEmpty(returnList)) {
			attendance = returnList.get(0);
		}
		return attendance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RfqSupplierMeetingAttendance rftSupplierMeetingAttendance) {
		StringBuilder hsql = new StringBuilder("from RfqSupplierMeetingAttendance as a left outer join a.rfxEventMeeting as rm where rm.id= :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", rftSupplierMeetingAttendance.getRfxEventMeeting().getId());
		List<RfqSupplierMeetingAttendance> rftSupplierMeetingAttendanceList = query.getResultList();
		return CollectionUtil.isNotEmpty(rftSupplierMeetingAttendanceList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfqSupplierMeetingAttendance attendenceByEventId(String meetingId, String eventId, String tenantId) {
		RfqSupplierMeetingAttendance meetAttendance = null;
		StringBuilder hsql = new StringBuilder("from RfqSupplierMeetingAttendance a inner join fetch a.rfxEventMeeting as rm inner join fetch a.supplier as s where rm.id= :meetingId and rm.rfxEvent.id= :eventId and s.id = :tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("meetingId", meetingId);
		query.setParameter("tenantId", tenantId);
		List<RfqSupplierMeetingAttendance> returnList = query.getResultList();
		if (CollectionUtil.isNotEmpty(returnList)) {
			meetAttendance = (RfqSupplierMeetingAttendance) returnList.get(0);
		}
		return meetAttendance;
	}

	@Override
	public void deleteAttendence(String meetingId) {
		Query query = getEntityManager().createQuery("delete from RfqSupplierMeetingAttendance as a where a.rfxEventMeeting.id= :meetingId");
		query.setParameter("meetingId", meetingId);
		int count = query.executeUpdate();
		if (count <= 0) {
			LOG.info("Error");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId) {
		StringBuilder hsql = new StringBuilder("from RfqSupplierMeetingAttendance a inner join fetch a.supplier s inner join fetch a.rfxEventMeeting m where m.id= :meetingId and a.rfxEvent.id = :eventId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("meetingId", meetingId);
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqSupplierMeetingAttendance> findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select a from RfqSupplierMeetingAttendance a  inner join fetch a.rfxEventMeeting m  where a.supplier.id =:supplierId and a.rfxEvent.id = :eventId and a.attended =:attended");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("attended", Boolean.FALSE);
		return query.getResultList();
	}

	@Override
	public boolean findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId, String meetingId) {
		StringBuilder hsql = new StringBuilder("select count(*) from RfqSupplierMeetingAttendance a where a.supplier.id =:supplierId and a.rfxEvent.id = :eventId and a.attended =:attended and a.meetingAttendanceStatus<>:meetingAttendanceStatus and  a.rfxEventMeeting.id =:meetingId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("attended", Boolean.FALSE);
		query.setParameter("meetingId", meetingId);
		query.setParameter("meetingAttendanceStatus", MeetingAttendanceStatus.Accepted);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}
}