package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpSupplierMeetingAttendance;

/**
 * @author Ravi
 */
public interface RfpSupplierMeetingAttendanceDao extends GenericDao<RfpSupplierMeetingAttendance, String> {

	/**
	 * @param meetingId
	 * @param supplierId
	 * @return
	 */
	RfpSupplierMeetingAttendance getAttendanceForMeetingForSupplier(String meetingId, String supplierId);

	/**
	 * @param rfpSupplierMeetingAttendance
	 * @return
	 */
	boolean isExists(RfpSupplierMeetingAttendance rfpSupplierMeetingAttendance);

	/**
	 * @param meetingId
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	RfpSupplierMeetingAttendance attendenceByEventId(String meetingId, String eventId, String tenantId);

	/**
	 * @param meetingId
	 */
	void deleteAttendence(String meetingId);

	/**
	 * @param meetingId
	 * @param eventId
	 * @return
	 */
	List<RfpSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfpSupplierMeetingAttendance> findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param id
	 * @return
	 */
	boolean findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId, String meetingId);

}
