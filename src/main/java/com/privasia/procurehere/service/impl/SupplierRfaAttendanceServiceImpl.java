package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaEventMeetingDao;
import com.privasia.procurehere.core.dao.RfaSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfaSupplierMeetingAttendance;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SupplierRfaAttendanceService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class SupplierRfaAttendanceServiceImpl implements SupplierRfaAttendanceService {

	Logger LOG = LogManager.getLogger(SupplierRfaAttendanceServiceImpl.class);

	@Autowired
	RfaSupplierMeetingAttendanceDao rfaSupplierMeetingAttendanceDao;

	@Autowired
	RfaEventMeetingDao rfaEventMeetingDao;

	@Override
	@Transactional(readOnly = false)
	public RfaSupplierMeetingAttendance saveMeetingAttendance(RfaSupplierMeetingAttendance attendance) {
		return rfaSupplierMeetingAttendanceDao.saveOrUpdate(attendance);
	}

	@Override
	public List<RfaSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId) {
		return rfaSupplierMeetingAttendanceDao.getAllSupplierAttendance(meetingId, eventId);
	}

	@Override
	public String findMandatoryAttendMeetingsByEventId(String supplierId, String eventId) {
		String meetingNames = "";
		List<RfaEventMeeting> meetingNamesList = rfaEventMeetingDao.findMandatoryAttendMeetingsByEventId(eventId, supplierId);
		if (CollectionUtil.isNotEmpty(meetingNamesList)) {
			for (RfaEventMeeting meeting : meetingNamesList) {
				RfaSupplierMeetingAttendance attendance = rfaSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meeting.getId(), supplierId);
				if (attendance == null || (attendance != null && attendance.getAttended() == Boolean.FALSE && attendance.getMeetingAttendanceStatus() != MeetingAttendanceStatus.Accepted)) {
					meetingNames += meeting.getTitle() + ",";
				}

			}
		}
		if (StringUtils.checkString(meetingNames).length() > 0) {
			meetingNames = StringUtils.checkString(meetingNames).substring(0, meetingNames.length() - 1);
		}
		return meetingNames;
	}

}
