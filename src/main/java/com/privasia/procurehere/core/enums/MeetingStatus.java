package com.privasia.procurehere.core.enums;

/**
 * @author RT-Parveen
 */

public enum MeetingStatus {
	SCHEDULED, ONGOING, CANCELLED,COMPLETED;
	
	public static MeetingStatus convertToString(String type) {

		switch (type) {
		case "SCHEDULED":
			return MeetingStatus.SCHEDULED;
		case "ONGOING":
			return MeetingStatus.ONGOING;
		case "CANCELLED":
			return MeetingStatus.CANCELLED;
		case "COMPLETED":
			return MeetingStatus.COMPLETED;
		default:
			return null;
		}
	}
}
