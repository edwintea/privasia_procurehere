/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpEventMeeting;

/**
 * @author ravi
 */
public interface RfpEventMeetingService {

	/**
	 * @param meeting
	 */
	void updateMeeting(RfpEventMeeting meeting);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventMeeting> getMeetingsByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventMeeting> findMeetingWithoutSuplliersByEventId(String eventId);

}
