package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.entity.RfaSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RfiSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RfpSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RfqSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RftSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.SupplierMeetingAttendance;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;

public class SupplierMeetingAttendancePojo extends SupplierMeetingAttendance implements Serializable {

	private static final long serialVersionUID = 9161436333347819079L;

	public SupplierMeetingAttendancePojo(String id, String name, String designation, String mobileNumber, String remarks, MeetingAttendanceStatus meetingAttendanceStatus, Boolean attended, String companyName, String supplierId, String meetingId, String rejectReason) {
		setId(id);
		setName(name);
		setDesignation(designation);
		setMobileNumber(mobileNumber);
		setRemarks(remarks);
		setMeetingAttendanceStatus(meetingAttendanceStatus);
		setAttended(attended);
		setCompanyName(companyName);
		setSupplierId(supplierId);
		setMeetingId(meetingId);
		setRejectReason(rejectReason);
	}

	public SupplierMeetingAttendancePojo() {
	}

	public SupplierMeetingAttendancePojo(RftSupplierMeetingAttendance atta) {
		setId(atta.getId());
		setName(atta.getName());
		setDesignation(atta.getDesignation());
		setMobileNumber(atta.getMobileNumber());
		setRemarks(atta.getRemarks());
		setMeetingAttendanceStatus(atta.getMeetingAttendanceStatus());
		setAttended(atta.getAttended());
		setCompanyName(atta.getSupplier().getCompanyName());
		setSupplierId(atta.getSupplier().getId());
		setMeetingId(atta.getRfxEventMeeting().getId());
		setRejectReason(atta.getRejectReason());
	}

	public SupplierMeetingAttendancePojo(RfpSupplierMeetingAttendance atta) {
		setId(atta.getId());
		setName(atta.getName());
		setDesignation(atta.getDesignation());
		setMobileNumber(atta.getMobileNumber());
		setRemarks(atta.getRemarks());
		setMeetingAttendanceStatus(atta.getMeetingAttendanceStatus());
		setAttended(atta.getAttended());
		setCompanyName(atta.getSupplier().getCompanyName());
		setSupplierId(atta.getSupplier().getId());
		setMeetingId(atta.getRfxEventMeeting().getId());
		setRejectReason(atta.getRejectReason());
	}

	public SupplierMeetingAttendancePojo(RfqSupplierMeetingAttendance atta) {
		setId(atta.getId());
		setName(atta.getName());
		setDesignation(atta.getDesignation());
		setMobileNumber(atta.getMobileNumber());
		setRemarks(atta.getRemarks());
		setMeetingAttendanceStatus(atta.getMeetingAttendanceStatus());
		setAttended(atta.getAttended());
		setCompanyName(atta.getSupplier().getCompanyName());
		setSupplierId(atta.getSupplier().getId());
		setMeetingId(atta.getRfxEventMeeting().getId());
		setRejectReason(atta.getRejectReason());
	}

	public SupplierMeetingAttendancePojo(RfiSupplierMeetingAttendance atta) {
		setId(atta.getId());
		setName(atta.getName());
		setDesignation(atta.getDesignation());
		setMobileNumber(atta.getMobileNumber());
		setRemarks(atta.getRemarks());
		setMeetingAttendanceStatus(atta.getMeetingAttendanceStatus());
		setAttended(atta.getAttended());
		setCompanyName(atta.getSupplier().getCompanyName());
		setSupplierId(atta.getSupplier().getId());
		setMeetingId(atta.getRfxEventMeeting().getId());
		setRejectReason(atta.getRejectReason());
	}

	public SupplierMeetingAttendancePojo(RfaSupplierMeetingAttendance atta) {
		setId(atta.getId());
		setName(atta.getName());
		setDesignation(atta.getDesignation());
		setMobileNumber(atta.getMobileNumber());
		setRemarks(atta.getRemarks());
		setMeetingAttendanceStatus(atta.getMeetingAttendanceStatus());
		setAttended(atta.getAttended());
		setCompanyName(atta.getSupplier().getCompanyName());
		setSupplierId(atta.getSupplier().getId());
		setMeetingId(atta.getRfaEventMeeting().getId());
		setRejectReason(atta.getRejectReason());
	}

	
	@Override
	public String toLogString() {
		return super.toLogString();
	}
}