package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiSupplierMeetingAttendance;

/**
 * @author Vipul
 * @author Ravi
 */
public interface RfiSupplierMeetingAttendanceDao extends GenericDao<RfiSupplierMeetingAttendance, String> {

	/**
	 * @param meetingId
	 * @param supplierId
	 * @return
	 */
	RfiSupplierMeetingAttendance getAttendanceForMeetingForSupplier(String meetingId, String supplierId);

	boolean isExists(RfiSupplierMeetingAttendance rfiSupplierMeetingAttendance);

	RfiSupplierMeetingAttendance attendenceByEventId(String meetingId, String eventId);

	void deleteAttendence(String meetingId);

	/**
	 * @param meetingId
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	RfiSupplierMeetingAttendance attendenceByEventId(String meetingId, String eventId, String tenantId);

	/**
	 * @param meetingId
	 * @param eventId
	 * @return
	 */
	List<RfiSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfiSupplierMeetingAttendance> findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param id
	 * @return
	 */
	boolean findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId, String meetingId);

}
