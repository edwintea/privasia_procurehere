package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqSupplierMeetingAttendance;

/**
 * @author Ravi
 */
public interface RfqSupplierMeetingAttendanceDao extends GenericDao<RfqSupplierMeetingAttendance, String> {

	/**
	 * @param meetingId
	 * @param supplierId
	 * @return
	 */
	RfqSupplierMeetingAttendance getAttendanceForMeetingForSupplier(String meetingId, String supplierId);

	/**
	 * @param supplierMeetingAttendance
	 * @return
	 */
	boolean isExists(RfqSupplierMeetingAttendance supplierMeetingAttendance);

	/**
	 * @param meetingId
	 * @param eventId
	 * @param tenantId
	 * @return
	 */
	RfqSupplierMeetingAttendance attendenceByEventId(String meetingId, String eventId, String tenantId);

	/**
	 * @param meetingId
	 */
	void deleteAttendence(String meetingId);

	/**
	 * @param meetingId
	 * @param eventId
	 * @return
	 */
	List<RfqSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfqSupplierMeetingAttendance> findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param id
	 * @return
	 */
	boolean findRejectedMeetingByEventIdAndSupplierId(String supplierId, String eventId, String id);

}
