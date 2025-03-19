package com.privasia.procurehere.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfiEventMeetingDao;
import com.privasia.procurehere.core.dao.RfiSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfiSupplierMeetingAttendance;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SupplierRfiAttendanceService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class SupplierRfiMeetingAttendanceServiceImpl implements SupplierRfiAttendanceService {

	Logger LOG = LogManager.getLogger(SupplierRfiMeetingAttendanceServiceImpl.class);

	@Autowired
	RfiSupplierMeetingAttendanceDao rfiSupplierMeetingAttendanceDao;

	@Autowired
	RfiEventMeetingDao rfiEventMeetingDao;

	@Override
	@Transactional(readOnly = false)
	public RfiSupplierMeetingAttendance saveMeetingAttendance(RfiSupplierMeetingAttendance attendance) {
		return rfiSupplierMeetingAttendanceDao.saveOrUpdate(attendance);

	}

	@Override
	public List<RfiSupplierMeetingAttendance> getAllSupplierAttendance(String meetingId, String eventId) {
		return rfiSupplierMeetingAttendanceDao.getAllSupplierAttendance(meetingId, eventId);
	}

	@Override
	public String findMandatoryAttendMeetingsByEventId(String supplierId, String eventId) {
		String meetingNames = "";
		List<RfiEventMeeting> meetingNamesList = rfiEventMeetingDao.findMandatoryAttendMeetingsByEventId(eventId, supplierId);
		if (CollectionUtil.isNotEmpty(meetingNamesList)) {
			for (RfiEventMeeting meeting : meetingNamesList) {
				RfiSupplierMeetingAttendance attendance = rfiSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meeting.getId(), supplierId);
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
