package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftSupplierMeetingAttendance;

/**
 * @author Vipul
 */
public interface RftSupplierMeetingAttendanceDao extends GenericDao<RftSupplierMeetingAttendance, String> {

	/**
	 * @param meetingId
	 * @param supplierId
	 * @return
	 */
	RftSupplierMeetingAttendance getAttendanceForMeetingForSupplier(String meetingId, String supplierId);

	boolean isExists(RftSupplierMeetingAttendance rftSupplierMeetingAttendance);

	RftSupplierMeetingAttendance attendenceByEventId(String meetingId, String eventId, String tenantId);

	void deleteAttendence(String meetingId);

	/**
	 * @param meetingId
	 * @param eventId
	 * @return
	 */
	List<RftSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RftSupplierMeetingAttendance> findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param id
	 * @return
	 */
	boolean findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId, String meetingId);

}
