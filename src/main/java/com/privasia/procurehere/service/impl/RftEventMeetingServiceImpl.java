/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftEventMeetingDao;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.service.RftEventMeetingService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class RftEventMeetingServiceImpl implements RftEventMeetingService {

	@Autowired
	RftEventMeetingDao rftEventMeetingDao;

	@Override
	@Transactional(readOnly = false)
	public void updateMeeting(RftEventMeeting meeting) {
		rftEventMeetingDao.saveOrUpdate(meeting);
	}

	@Override
	public List<RftEventMeeting> getMeetingsByEventId(String eventId) {
		return rftEventMeetingDao.getRftMeetingByEventId(eventId);
	}

	@Override
	public List<RftEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId) {
		return rftEventMeetingDao.findMeetingWithoutSuplliersByEventId(eventId);
	}

}
