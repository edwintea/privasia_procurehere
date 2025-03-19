package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpEventMeetingDao;
import com.privasia.procurehere.core.dao.RfpSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.RfpEventMeeting;
import com.privasia.procurehere.core.entity.RfpSupplierMeetingAttendance;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SupplierRfpAttendanceService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class SupplierRfpAttendanceServiceImpl implements SupplierRfpAttendanceService {

	Logger LOG = LogManager.getLogger(SupplierRfpAttendanceServiceImpl.class);

	@Autowired
	RfpSupplierMeetingAttendanceDao rfpSupplierMeetingAttendanceDao;

	@Autowired
	RfpEventMeetingDao rfpEventMeetingDao;

	@Override
	@Transactional(readOnly = false)
	public RfpSupplierMeetingAttendance saveMeetingAttendance(RfpSupplierMeetingAttendance attendance) {
		return rfpSupplierMeetingAttendanceDao.saveOrUpdate(attendance);

	}

	@Override
	public List<RfpSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId) {
		return rfpSupplierMeetingAttendanceDao.getAllSupplierAttendance(meetingId, eventId);
	}

	@Override
	public String findMandatoryAttendMeetingsByEventId(String supplierId, String eventId) {
		String meetingNames = "";
		List<RfpEventMeeting> meetingNamesList = rfpEventMeetingDao.findMandatoryAttendMeetingsByEventId(eventId, supplierId);
		if (CollectionUtil.isNotEmpty(meetingNamesList)) {
			for (RfpEventMeeting meeting : meetingNamesList) {
				RfpSupplierMeetingAttendance attendance = rfpSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meeting.getId(), supplierId);
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
