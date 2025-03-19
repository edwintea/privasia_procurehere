package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.RfiSupplierMeetingAttendance;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Vipul
 * @author Ravi
 */
@Repository
public class RfiSupplierMeetingAttendanceDaoImpl extends GenericDaoImpl<RfiSupplierMeetingAttendance, String> implements RfiSupplierMeetingAttendanceDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(RfiSupplierMeetingAttendanceDaoImpl.class);

	@Resource
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public RfiSupplierMeetingAttendance getAttendanceForMeetingForSupplier(String meetingId, String supplierId) {
		RfiSupplierMeetingAttendance attendance = null;

		StringBuilder hsql = new StringBuilder("from RfiSupplierMeetingAttendance a where a.rfxEventMeeting.id= :meetingId and a.supplier.id = :supplierId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("meetingId", meetingId);
		query.setParameter("supplierId", supplierId);
		List<RfiSupplierMeetingAttendance> returnList = query.getResultList();
		if (CollectionUtil.isNotEmpty(returnList)) {
			attendance = returnList.get(0);
		}
		return attendance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RfiSupplierMeetingAttendance rfiSupplierMeetingAttendance) {
		StringBuilder hsql = new StringBuilder("from RfiSupplierMeetingAttendance as a left outer join a.rfxEventMeeting as rm where rm.id= :id");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", rfiSupplierMeetingAttendance.getRfxEventMeeting().getId());
		List<RfiSupplierMeetingAttendance> rftSupplierMeetingAttendanceList = query.getResultList();
		return CollectionUtil.isNotEmpty(rftSupplierMeetingAttendanceList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfiSupplierMeetingAttendance attendenceByEventId(String meetingId, String eventId) {
		RfiSupplierMeetingAttendance meetAttendance = null;
		StringBuilder hsql = new StringBuilder("from RfiSupplierMeetingAttendance a inner join fetch a.rfxEventMeeting as rm where rm.id= :meetingId and rm.rfxEvent.id= :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("meetingId", meetingId);
		List<RfiSupplierMeetingAttendance> returnList = query.getResultList();
		if (CollectionUtil.isNotEmpty(returnList)) {
			meetAttendance = (RfiSupplierMeetingAttendance) returnList.get(0);
		}
		return meetAttendance;
	}

	@SuppressWarnings("unused")
	@Override
	public void deleteAttendence(String meetingId) {
		Query query = getEntityManager().createQuery("delete from RfiSupplierMeetingAttendance as a where a.rfxEventMeeting.id= :meetingId");
		query.setParameter("meetingId", meetingId);
		int count = query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfiSupplierMeetingAttendance attendenceByEventId(String meetingId, String eventId, String tenantId) {
		RfiSupplierMeetingAttendance meetAttendance = null;
		StringBuilder hsql = new StringBuilder("from RfpSupplierMeetingAttendance a inner join fetch a.rfxEventMeeting as rm inner join fetch a.supplier as s where rm.id= :meetingId and rm.rfxEvent.id= :eventId and s.id = :tenantId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("meetingId", meetingId);
		query.setParameter("tenantId", tenantId);
		List<RfiSupplierMeetingAttendance> returnList = query.getResultList();
		if (CollectionUtil.isNotEmpty(returnList)) {
			meetAttendance = (RfiSupplierMeetingAttendance) returnList.get(0);
		}
		return meetAttendance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId) {
		StringBuilder hsql = new StringBuilder("from RfiSupplierMeetingAttendance a inner join fetch a.supplier s inner join fetch a.rfxEventMeeting m where m.id= :meetingId and a.rfiEvent.id = :eventId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("meetingId", meetingId);
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiSupplierMeetingAttendance> findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId) {
		StringBuilder hsql = new StringBuilder("select a from RfiSupplierMeetingAttendance a  inner join fetch a.rfxEventMeeting m  where a.supplier.id =:supplierId and a.rfiEvent.id = :eventId and a.attended =:attended");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("attended", Boolean.FALSE);
		return query.getResultList();
	}

	@Override
	public boolean findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId, String meetingId) {
		StringBuilder hsql = new StringBuilder("select count(*) from RfiSupplierMeetingAttendance a where a.supplier.id =:supplierId and a.rfiEvent.id = :eventId and a.attended =:attended  and a.meetingAttendanceStatus<>:meetingAttendanceStatus and a.rfxEventMeeting.id =:meetingId");
		LOG.info("hsql:" + hsql);
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("attended", Boolean.FALSE);
		query.setParameter("meetingId", meetingId);
		query.setParameter("meetingAttendanceStatus", MeetingAttendanceStatus.Accepted);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}
}