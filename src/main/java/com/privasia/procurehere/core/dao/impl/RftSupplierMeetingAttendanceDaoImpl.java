package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.RftSupplierMeetingAttendance;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Vipul
 */
@Repository
public class RftSupplierMeetingAttendanceDaoImpl extends GenericDaoImpl<RftSupplierMeetingAttendance, String> implements RftSupplierMeetingAttendanceDao {

	private static final Logger LOG = LogManager.getLogger(RftSupplierMeetingAttendanceDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public RftSupplierMeetingAttendance getAttendanceForMeetingForSupplier(String meetingId, String supplierId) {
		RftSupplierMeetingAttendance attendance = null;

		StringBuilder hsql = new StringBuilder("from RftSupplierMeetingAttendance a where a.rfxEventMeeting.id= :meetingId and a.supplier.id = :supplierId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("meetingId", meetingId);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierMeetingAttendance> returnList = query.getResultList();
		if (CollectionUtil.isNotEmpty(returnList)) {
			attendance = returnList.get(0);
		}
		return attendance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RftSupplierMeetingAttendance rftSupplierMeetingAttendance) {
		StringBuilder hsql = new StringBuilder("from RftSupplierMeetingAttendance as a left outer join a.rfxEventMeeting as rm where rm.id= :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", rftSupplierMeetingAttendance.getRfxEventMeeting().getId());
		List<RftSupplierMeetingAttendance> rftSupplierMeetingAttendanceList = query.getResultList();
		return CollectionUtil.isNotEmpty(rftSupplierMeetingAttendanceList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftSupplierMeetingAttendance attendenceByEventId(String meetingId, String eventId, String tenantId) {
		RftSupplierMeetingAttendance meetAttendance = null;
		StringBuilder hsql = new StringBuilder("from RftSupplierMeetingAttendance a inner join fetch a.rfxEventMeeting as rm inner join fetch a.supplier as s where rm.id= :meetingId and rm.rfxEvent.id= :eventId and s.id = :tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		LOG.info("eventId " + eventId + "meetingId " + meetingId + "tenantId" + tenantId);
		query.setParameter("eventId", eventId);
		query.setParameter("meetingId", meetingId);
		query.setParameter("tenantId", tenantId);
		List<RftSupplierMeetingAttendance> returnList = query.getResultList();
		LOG.info("meetAttendance " + returnList);
		if (CollectionUtil.isNotEmpty(returnList)) {
			meetAttendance = (RftSupplierMeetingAttendance) returnList.get(0);
		}
		LOG.info("meetAttendance " + meetAttendance);
		return meetAttendance;
	}

	@Override
	public void deleteAttendence(String meetingId) {
		LOG.info("+deleteAttendence.");
		Query query = getEntityManager().createQuery("delete from RftSupplierMeetingAttendance as a where a.rfxEventMeeting.id= :meetingId");
		query.setParameter("meetingId", meetingId);
		// query.setParameter("eventId", eventId);
		int count = query.executeUpdate();
		LOG.info("+deleteAttendence." + count);
		if (count <= 0) {
			LOG.info("Error");
		}

	}

	// @SuppressWarnings("unchecked")
	// @Override
	// public boolean isMeetingAttended(RftSupplierMeetingAttendance rftSupplierMeetingAttendance) {
	// StringBuilder hsql = new StringBuilder("from RftSupplierMeetingAttendance as a left outer join a.rfxEventMeeting
	// as rm where rm.id= :id");
	// final Query query = getEntityManager().createQuery(hsql.toString());
	// query.setParameter("id", rftSupplierMeetingAttendance.getRfxEventMeeting().getId());
	// List<RftSupplierMeetingAttendance> rftSupplierMeetingAttendanceList = query.getResultList();
	// return CollectionUtil.isNotEmpty(rftSupplierMeetingAttendanceList);
	// }

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId) {

		StringBuilder hsql = new StringBuilder("from RftSupplierMeetingAttendance a inner join fetch a.supplier s inner join fetch a.rfxEventMeeting m where m.id= :meetingId and a.rftEvent.id = :eventId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("meetingId", meetingId);
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierMeetingAttendance> findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select a from RftSupplierMeetingAttendance a  inner join fetch a.rfxEventMeeting m  where a.supplier.id =:supplierId and a.rftEvent.id = :eventId and a.attended =:attended");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("attended", Boolean.FALSE);
		List<RftSupplierMeetingAttendance> returnList = query.getResultList();
		return returnList;
	}

	@Override
	public boolean findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId, String meetingId) {
		StringBuilder hsql = new StringBuilder("select count(*) from RftSupplierMeetingAttendance a where a.supplier.id =:supplierId and a.rftEvent.id = :eventId and a.attended =:attended and a.meetingAttendanceStatus<>:meetingAttendanceStatus and  a.rfxEventMeeting.id =:meetingId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("attended", Boolean.FALSE);
		query.setParameter("meetingId", meetingId);
		query.setParameter("meetingAttendanceStatus", MeetingAttendanceStatus.Accepted);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}
}