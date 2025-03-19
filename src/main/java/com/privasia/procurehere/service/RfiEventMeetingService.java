/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiEventMeeting;

/**
 * @author ravi
 */
public interface RfiEventMeetingService {

	/**
	 * @param meeting
	 */
	void updateMeeting(RfiEventMeeting meeting);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventMeeting> getMeetingsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId);

}
