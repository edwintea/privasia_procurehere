package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqSupplierMeetingAttendance;

/**
 * @author Vipul
 * @author Ravi
 */

public interface SupplierRfqAttendanceService {

	RfqSupplierMeetingAttendance saveMeetingAttendance(RfqSupplierMeetingAttendance attendance);

	/**
	 * @param meetingId
	 * @param eventId
	 * @return
	 */
	List<RfqSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId);

	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	String findMandatoryAttendMeetingsByEventId(String supplierId, String eventId);

}
