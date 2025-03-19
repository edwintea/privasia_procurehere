package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.RfaSupplierMeetingAttendance;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author RT-Kapil
 */
@Repository
public class RfaSupplierMeetingAttendanceDaoImpl extends GenericDaoImpl<RfaSupplierMeetingAttendance, String> implements RfaSupplierMeetingAttendanceDao {

	private static final Logger LOG = LogManager.getLogger(RfaSupplierMeetingAttendanceDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierMeetingAttendance getAttendanceForMeetingForSupplier(String meetingId, String supplierId) {
		RfaSupplierMeetingAttendance attendance = null;

		StringBuilder hsql = new StringBuilder("from RfaSupplierMeetingAttendance a where a.rfaEventMeeting.id= :meetingId and a.supplier.id = :supplierId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("meetingId", meetingId);
		query.setParameter("supplierId", supplierId);
		List<RfaSupplierMeetingAttendance> returnList = query.getResultList();
		if (CollectionUtil.isNotEmpty(returnList)) {
			attendance = returnList.get(0);
		}
		return attendance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RfaSupplierMeetingAttendance rfaSupplierMeetingAttendance) {
		StringBuilder hsql = new StringBuilder("from RfaSupplierMeetingAttendance as a left outer join a.rfaEventMeeting as rm where rm.id= :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		LOG.info("meetingId :" + rfaSupplierMeetingAttendance.getRfaEventMeeting().getId());
		query.setParameter("id", rfaSupplierMeetingAttendance.getRfaEventMeeting().getId());
		List<RfaSupplierMeetingAttendance> rfaSupplierMeetingAttendanceList = query.getResultList();
		return CollectionUtil.isNotEmpty(rfaSupplierMeetingAttendanceList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierMeetingAttendance attendenceByEventId(String meetingId, String eventId, String tenantId) {
		RfaSupplierMeetingAttendance meetAttendance = null;
		StringBuilder hsql = new StringBuilder("from RfaSupplierMeetingAttendance a inner join fetch a.rfaEventMeeting as rm inner join fetch a.supplier as s where rm.id= :meetingId and rm.rfxEvent.id= :eventId and s.id = :tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		LOG.info("eventId " + eventId + "meetingId " + meetingId);
		query.setParameter("eventId", eventId);
		query.setParameter("meetingId", meetingId);
		query.setParameter("tenantId", tenantId);
		List<RfaSupplierMeetingAttendance> returnList = query.getResultList();
		if (CollectionUtil.isNotEmpty(returnList)) {
			meetAttendance = (RfaSupplierMeetingAttendance) returnList.get(0);
		}
		LOG.info("meetAttendance " + meetAttendance);
		return meetAttendance;
	}

	@Override
	public void deleteAttendence(String meetingId) {
		LOG.info("+deleteAttendence.");
		Query query = getEntityManager().createQuery("delete from RfaSupplierMeetingAttendance as a where a.rfaEventMeeting.id= :meetingId");
		query.setParameter("meetingId", meetingId);
		// query.setParameter("eventId", eventId);
		int count = query.executeUpdate();
		LOG.info("+deleteAttendence." + count);
		if (count <= 0) {
			LOG.info("Error");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId) {
		StringBuilder hsql = new StringBuilder("from RfaSupplierMeetingAttendance a inner join fetch a.supplier s inner join fetch a.rfaEventMeeting m where m.id= :meetingId and a.rfaEvent.id = :eventId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("meetingId", meetingId);
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierMeetingAttendance> findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select a from RfaSupplierMeetingAttendance a  inner join fetch a.rfaEventMeeting m  where a.supplier.id =:supplierId and a.rfaEvent.id = :eventId and a.attended =:attended");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("attended", Boolean.FALSE);
		return query.getResultList();
	}

	@Override
	public boolean findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId, String meetingId) {
		StringBuilder hsql = new StringBuilder("select count(*) from RfaSupplierMeetingAttendance a where a.supplier.id =:supplierId and a.rfaEvent.id = :eventId and a.attended =:attended  and a.meetingAttendanceStatus<>:meetingAttendanceStatus and a.rfaEventMeeting.id =:meetingId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("attended", Boolean.FALSE);
		query.setParameter("meetingId", meetingId);
		query.setParameter("meetingAttendanceStatus", MeetingAttendanceStatus.Accepted);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}
}