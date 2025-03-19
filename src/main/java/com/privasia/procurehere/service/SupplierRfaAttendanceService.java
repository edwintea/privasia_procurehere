package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaSupplierMeetingAttendance;

/**
 * @author Vipul
 */

public interface SupplierRfaAttendanceService {

	RfaSupplierMeetingAttendance saveMeetingAttendance(RfaSupplierMeetingAttendance attendance);

	/**
	 * @param meetingId
	 * @param eventId
	 * @return
	 */
	List<RfaSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId);

	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @return
	 */
	String findMandatoryAttendMeetingsByEventId(String supplierId, String eventId);
}
