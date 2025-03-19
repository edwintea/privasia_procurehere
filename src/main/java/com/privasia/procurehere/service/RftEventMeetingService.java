/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RftEventMeeting;

/**
 * @author ravi
 */
public interface RftEventMeetingService {

	/**
	 * @param meeting
	 */
	void updateMeeting(RftEventMeeting meeting);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventMeeting> getMeetingsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId);

}
