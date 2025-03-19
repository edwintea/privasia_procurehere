/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.entity.EventMeeting;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaMeetingService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiMeetingService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpMeetingService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqMeetingService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftMeetingService;
import com.privasia.procurehere.service.SupplierPerformanceFormService;

/**
 * @author Ravi
 */
@Controller
@RequestMapping("/timeRemaining")
public class TimeRemainingController {

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfaMeetingService rfaMeetingService;

	@Autowired
	RftMeetingService rftMeetingService;

	@Autowired
	RfpMeetingService rfpMeetingService;

	@Autowired
	RfqMeetingService rfqMeetingService;

	@Autowired
	RfiMeetingService rfiMeetingService;
	
	@Autowired
	SupplierPerformanceFormService supplierPerformanceFormService;

	@RequestMapping(path = "event/{eventType}/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<String> getEventRemainingTime(@PathVariable("eventType") RfxTypes eventType, @PathVariable String eventId, Model model, HttpSession session) {
		long remainingTime = 0;

		Date now = new Date();
		Date startDate = null;
		Date endDate = null;
		boolean isClosed = false;
		switch (eventType) {
		case RFA: {
			EventTimerPojo event = rfaEventService.getTimeRfaEventByeventId(eventId);
			startDate = event.getEventStart();
			if (event.getAuctionResumeDateTime() != null) {
				startDate = event.getAuctionResumeDateTime();
			}
			endDate = event.getEventEnd();
			isClosed = (event.getStatus() == EventStatus.CLOSED || event.getStatus() == EventStatus.COMPLETE);
			break;
		}
		case RFI: {
			EventTimerPojo event = rfiEventService.getTimeEventByeventId(eventId);
			startDate = event.getEventStart();
			endDate = event.getEventEnd();
			isClosed = (event.getStatus() == EventStatus.CLOSED || event.getStatus() == EventStatus.COMPLETE);
			break;
		}
		case RFP: {
			EventTimerPojo event = rfpEventService.getTimeEventByeventId(eventId);
			startDate = event.getEventStart();
			endDate = event.getEventEnd();
			isClosed = (event.getStatus() == EventStatus.CLOSED || event.getStatus() == EventStatus.COMPLETE);
			break;
		}
		case RFQ: {
			EventTimerPojo event = rfqEventService.getTimeEventByeventId(eventId);
			startDate = event.getEventStart();
			endDate = event.getEventEnd();
			isClosed = (event.getStatus() == EventStatus.CLOSED || event.getStatus() == EventStatus.COMPLETE);
			break;
		}
		case RFT: {
			EventTimerPojo event = rftEventService.getTimeEventByeventId(eventId);
			startDate = event.getEventStart();
			endDate = event.getEventEnd();
			break;
		}
		default:
			break;

		}
		// If not yet started, get the remaining time to start
		if (now.before(startDate)) {
			remainingTime = startDate.getTime() - now.getTime();
		} else if (now.before(endDate)) {
			// If started, get the remaining time to End time
			if (!isClosed) {
				remainingTime = endDate.getTime() - now.getTime();
			} else {
				remainingTime = 0;
			}
		}

		if (remainingTime < 0) {
			remainingTime = 0;
		}

		return new ResponseEntity<String>("{\"remainingTime\" : " + String.valueOf(remainingTime) + "}", HttpStatus.OK);
	}

	@RequestMapping(path = "meeting/{eventType}/{meetingId}", method = RequestMethod.GET)
	public ResponseEntity<String> getMettingRemainingTime(@PathVariable("eventType") RfxTypes eventType, @PathVariable("meetingId") String meetingId, Model model, HttpSession session) {
		long remainingTime = 0;
		EventMeeting eventMeeting = null;
		Date now = new Date();
		Date startDate = null;
		switch (eventType) {
		case RFA:
			eventMeeting = rfaMeetingService.getRfaMeetingById(meetingId);
			break;
		case RFI:
			eventMeeting = rfiMeetingService.getRfiMeetingById(meetingId);
			break;
		case RFP:
			eventMeeting = rfpMeetingService.getRfpMeetingById(meetingId);
			break;
		case RFQ:
			eventMeeting = rfqMeetingService.getMeetingById(meetingId);
			break;
		case RFT:
			eventMeeting = rftMeetingService.getRftMeetingById(meetingId);
			break;
		default:
			break;
		}

		startDate = eventMeeting.getAppointmentDateTime();
		// If not yet started, get the remaining time to start
		if (now.before(startDate)) {
			remainingTime = startDate.getTime() - now.getTime();
		}
		return new ResponseEntity<String>("{\"remainingTime\" : " + String.valueOf(remainingTime) + "}", HttpStatus.OK);
	}

	@RequestMapping(path = "spForm/{formId}", method = RequestMethod.GET)
	public ResponseEntity<String> getSPFormRemainingTime(@PathVariable String formId, Model model, HttpSession session) {
		long remainingTime = 0;

		Date now = new Date();
		Date startDate = null;
		Date endDate = null;
		boolean isClosed = false;
		
		EventTimerPojo event = supplierPerformanceFormService.getTimeByFormId(formId);
		startDate = event.getEventStart();
		endDate = event.getEventEnd();
		isClosed = (event.getFormStatus() == SupplierPerformanceFormStatus.CLOSED);
		
		// If not yet started, get the remaining time to start
		if (now.before(startDate)) {
			remainingTime = startDate.getTime() - now.getTime();
		} else if (now.before(endDate)) {
			// If started, get the remaining time to End time
			if (!isClosed) {
				remainingTime = endDate.getTime() - now.getTime();
			} else {
				remainingTime = 0;
			}
		}

		if (remainingTime < 0) {
			remainingTime = 0;
		}

		return new ResponseEntity<String>("{\"remainingTime\" : " + String.valueOf(remainingTime) + "}", HttpStatus.OK);
	}
}
