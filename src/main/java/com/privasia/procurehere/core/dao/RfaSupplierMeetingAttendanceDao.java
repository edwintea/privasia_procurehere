package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaSupplierMeetingAttendance;

/**
 * @author RT-Kapil
 */
public interface RfaSupplierMeetingAttendanceDao extends GenericDao<RfaSupplierMeetingAttendance, String> {

	/**
	 * @param meetingId
	 * @param supplierId
	 * @return
	 */
	RfaSupplierMeetingAttendance getAttendanceForMeetingForSupplier(String meetingId, String supplierId);

	boolean isExists(RfaSupplierMeetingAttendance rfaSupplierMeetingAttendance);

	RfaSupplierMeetingAttendance attendenceByEventId(String meetingId, String eventId, String tenantId);

	void deleteAttendence(String meetingId);

	/**
	 * @param meetingId
	 * @param eventId
	 * @return
	 */
	List<RfaSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfaSupplierMeetingAttendance> findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param id
	 * @return
	 */
	boolean findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId, String meetingId);

}
