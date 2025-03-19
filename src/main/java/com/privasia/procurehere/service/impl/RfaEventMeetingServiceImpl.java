/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaEventMeetingDao;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.service.RfaEventMeetingService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class RfaEventMeetingServiceImpl implements RfaEventMeetingService {

	@Autowired
	RfaEventMeetingDao rfaEventMeetingDao;

	@Override
	@Transactional(readOnly = false)
	public void updateMeeting(RfaEventMeeting meeting) {
		rfaEventMeetingDao.saveOrUpdate(meeting);
	}

	@Override
	public List<RfaEventMeeting> getRfaMeetingByEventId(String eventId) {
		return rfaEventMeetingDao.getRfaMeetingByEventId(eventId);
	}

	@Override
	public List<RfaEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId) {
		return rfaEventMeetingDao.findMeetingWithoutSuplliersByEventId(eventId);
	}

}
