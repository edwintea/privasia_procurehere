package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpEventMeetingDao;
import com.privasia.procurehere.core.dao.RfpEventMeetingDocumentDao;
import com.privasia.procurehere.core.dao.RfpMeetingReminderDao;
import com.privasia.procurehere.core.dao.RfpSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventMeeting;
import com.privasia.procurehere.core.entity.RfpEventMeetingContact;
import com.privasia.procurehere.core.entity.RfpEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfpEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfpSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfpMeetingService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.supplier.SupplierService;

@Service
@Transactional(readOnly = true)
public class RfpMeetingServiceImpl implements RfpMeetingService {

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfpEventMeetingDao rfpEventMeetingDao;

	@Autowired
	RfpEventMeetingDocumentDao rfpEventMeetingDocumentDao;

	@Autowired
	RfpSupplierMeetingAttendanceDao rfpSupplierMeetingAttendanceDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfpMeetingReminderDao rfpMeetingReminderDao;
	
	@Autowired
	TatReportService tatReportService;

	protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RfqEventDao rfqEventDao;

	@Resource
	MessageSource messageSource;

	@Override
	@Transactional(readOnly = false)
	public RfpEventMeeting cancelMeeting(RfpEventMeeting eventMeeting) {
		RfpEventMeeting persistObj = getRfpMeetingById(eventMeeting.getId());
		persistObj.setCancelReason(eventMeeting.getCancelReason());
		persistObj.setStatus(MeetingStatus.CANCELLED);
		persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
		persistObj.setModifiedDate(new Date());
		return rfpEventMeetingDao.saveOrUpdate(persistObj);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEventMeeting saveRfpMeeting(RfpEventMeeting rfpEventMeeting, List<RfpEventMeetingDocument> meetingDocumentList, Boolean selectAllSupplier, String eventId, EventVisibilityType eventVisibility) throws NoSuchMessageException, ApplicationException {
		RfpEventMeeting eventMeeting = null;
		Date appointmentDate;
		TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(eventId, SecurityLibrary.getLoggedInUserTenantId());
		if (StringUtils.checkString(rfpEventMeeting.getId()).length() == 0) {
			RfpEvent rftEventObj = getRfpEventById(rfpEventMeeting.getRfxEvent().getId());
			rfpEventMeeting.setRfxEvent(rftEventObj);
			if (Boolean.TRUE.equals(selectAllSupplier)) {
				rfpEventMeeting.setInviteSuppliers(getEventSuppliers(eventId));
			} else {
				if (CollectionUtil.isNotEmpty(rfpEventMeeting.getSelectedSuppliers())) {
					if (CollectionUtil.isEmpty(rfpEventMeeting.getInviteSuppliers())) {
						rfpEventMeeting.setInviteSuppliers(new ArrayList<Supplier>());
					}
					for (String selectedSupplier : rfpEventMeeting.getSelectedSuppliers()) {
						rfpEventMeeting.getInviteSuppliers().add(new Supplier(selectedSupplier));
					}
				}
			}
			if (EventVisibilityType.PRIVATE.equals(eventVisibility) && CollectionUtil.isEmpty(rfpEventMeeting.getInviteSuppliers())) {
				throw new ApplicationException(messageSource.getMessage("rft.meeting.error.supplierEmpty", new Object[] {}, Global.LOCALE));
			}
			rfpEventMeeting.setRfxEventMeetingDocument(meetingDocumentList);
			rfpEventMeeting.setStatus(MeetingStatus.SCHEDULED);
			eventMeeting = rfpEventMeetingDao.saveOrUpdate(rfpEventMeeting);
			if(tatReport != null && tatReport.getEventFirstMeetingDate() == null) {
				tatReportService.updateTatReportEventFirstMeetingDate(tatReport.getId(), rfpEventMeeting.getAppointmentDateTime());
			}
		} else {
			RfpEventMeeting persistObj = getRfpMeetingById(rfpEventMeeting.getId());
			appointmentDate = persistObj.getAppointmentDateTime();
			persistObj.setTitle(rfpEventMeeting.getTitle());
			persistObj.setMeetingType(rfpEventMeeting.getMeetingType());
			persistObj.setVenue(rfpEventMeeting.getVenue());
			persistObj.setRemarks(rfpEventMeeting.getRemarks());
			if (Boolean.TRUE.equals(selectAllSupplier)) {
				persistObj.setInviteSuppliers(getEventSuppliers(eventId));
			} else {
				Set<String> dbSupplier = new HashSet<String>(getMeetingSupplierIds(rfpEventMeeting.getId(), RfxTypes.RFP));
				if (CollectionUtil.isEmpty(dbSupplier)) {
					LOG.info("empty===========>");
					dbSupplier = new HashSet<String>();
				}
				if (CollectionUtil.isNotEmpty(rfpEventMeeting.getSelectedSuppliers())) {
					LOG.info("not empty===========>");
					dbSupplier.addAll(rfpEventMeeting.getSelectedSuppliers());
				}
				if (CollectionUtil.isNotEmpty(rfpEventMeeting.getRemovedSuppliers())) {
					LOG.info("not empty===========>");
					dbSupplier.removeAll(rfpEventMeeting.getRemovedSuppliers());
				}
				if (CollectionUtil.isEmpty(rfpEventMeeting.getInviteSuppliers())) {
					LOG.info("empty===========>");
					rfpEventMeeting.setInviteSuppliers(new ArrayList<Supplier>());
				}
				for (String supplierId : dbSupplier) {
					rfpEventMeeting.getInviteSuppliers().add(new Supplier(supplierId));
				}
				persistObj.setInviteSuppliers(rfpEventMeeting.getInviteSuppliers());
			}
			if (EventVisibilityType.PRIVATE.equals(eventVisibility) && CollectionUtil.isEmpty(persistObj.getInviteSuppliers())) {
				throw new ApplicationException(messageSource.getMessage("rft.meeting.error.supplierEmpty", new Object[] {}, Global.LOCALE));
			}
			persistObj.setMeetingAttendMandatory(rfpEventMeeting.getMeetingAttendMandatory());
			persistObj.setRfxEventMeetingContacts(rfpEventMeeting.getRfxEventMeetingContacts());
			persistObj.setAppointmentDateTime(rfpEventMeeting.getAppointmentDateTime());
			persistObj.setRfxEventMeetingDocument(meetingDocumentList);
			persistObj.setRfxEventMeetingReminder(rfpEventMeeting.getRfxEventMeetingReminder());
			eventMeeting = rfpEventMeetingDao.saveOrUpdate(persistObj);
			
			if(tatReport != null && tatReport.getEventFirstMeetingDate().equals(appointmentDate)) {
				tatReportService.updateTatReportEventFirstMeetingDate(tatReport.getId(), rfpEventMeeting.getAppointmentDateTime());
			}
		}
		
		return eventMeeting;
		
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEventMeeting getRfpMeetingById(String id) {
		RfpEventMeeting meeting = rfpEventMeetingDao.getRfpMeetingById(id);
		if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
			for (RfpEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
				contact.getContactName();
			}
		}
		if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
			for (RfpEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
				reminder.getReminderDate();
			}
		}
		return meeting;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfpMeeting(RfpEventMeeting rftEventMeeting) {
		rfpEventMeetingDao.update(rftEventMeeting);

	}

	@Override
	@Transactional(readOnly = false)
	public List<RfpEventMeeting> getAllRfpMeetingByEventId(String eventId) {
		List<RfpEventMeeting> meetingList = rfpEventMeetingDao.getRfpMeetingByEventId(eventId);
		// if (CollectionUtil.isNotEmpty(meetingList)) {
		// for (RfpEventMeeting meeting : meetingList) {
		// if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
		// for (RfpEventMeetingDocument document : meeting.getRfxEventMeetingDocument()) {
		// document.getFileName();
		// }
		// }
		// if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
		// for (Supplier supplier : meeting.getInviteSuppliers()) {
		// supplier.getCompanyName();
		// }
		// }
		//
		// }
		//
		// }
		return meetingList;
	}

	@Override
	@Transactional(readOnly = false)
	public void storeEventMettingDocs(RfpEventMeetingDocument document) {
		rfpEventMeetingDocumentDao.save(document);
	}

	@Override
	public RfpEventMeetingDocument getRfpEventMeetingDocument(String docId) {
		return rfpEventMeetingDocumentDao.findById(docId);
	}

	@Override
	public RfpEventMeetingContact getRfpMeetingContactById(String id) {
		return rfpEventMeetingDao.getRfpMeetingContactById(id);
	}

	@Override
	public RfpEventMeeting getOngoingMeeting(String id) {
		RfpEventMeeting meeting = rfpEventMeetingDao.getRfpMeetingByIdAndStatus(id);
		return meeting;
	}

	@Override
	public List<RfpEventMeetingContact> getRfpMeetContactByMeetId(String id) {
		return rfpEventMeetingDao.getRfpMeetContactByMeetId(id);
	}

	@Override
	public Date getMinMeetingDateForEvent(String eventId) {
		return rfpEventMeetingDao.getMinMeetingDateForEvent(eventId);
	}

	@Override
	public Integer getCountOfRfpMeetingByEventId(String eventId) {
		return rfpEventMeetingDao.getCountOfRfpMeetingByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteAllRfpMeetings(String eventId, String eventRequirement) {
		List<RfpEventMeeting> meetingList = rfpEventMeetingDao.getRfpMeetingByEventId(eventId);
		for (RfpEventMeeting eventMeeting : meetingList) {
			rfpEventMeetingDao.delete(eventMeeting);
		}
		// rfpEventMeetingDao.deleteAllMeetings(eventId);
		RfpEvent event = rfpEventDao.findById(eventId);
		event.setMeetingCompleted(Boolean.FALSE);
		event.setMeetingReq(Boolean.FALSE);
		rfpEventDao.update(event);
	}

	@Override
	public List<RfpEventMeeting> loadSupplierMeetingByEventIdAndMeetingStatus(String eventId, MeetingStatus meetStatus, String tenantId) {
		List<RfpEventMeeting> returnList = null;
		List<RfpEventMeeting> meetingList = rfpEventMeetingDao.findMeetByIdAndStatus(eventId, meetStatus, tenantId);

		if (CollectionUtil.isNotEmpty(meetingList)) {
			returnList = new ArrayList<RfpEventMeeting>();
			for (RfpEventMeeting meet : meetingList) {
				RfpEventMeeting clone = meet.createShallowCopy();
				RfpSupplierMeetingAttendance attendance = rfpSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meet.getId(), tenantId);
				clone.setSupplierAttendance(attendance);
				returnList.add(clone);
				List<RfpEventMeetingDocument> meetingDocumentList = rfpEventMeetingDao.getPlainMeetingDocument(meet.getId());
				if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
					clone.setEventMeetingDocument(meetingDocumentList);
				}
			}
		}
		return returnList;
	}

	@Override
	public Date getMaxMeetingDateForEvent(String eventId) {
		return rfpEventMeetingDao.getMaxMeetingDateForEvent(eventId);
	}

	@Override
	public void deleteSupplierMeetingAttendence(String meetingId) {
		rfpSupplierMeetingAttendanceDao.deleteAttendence(meetingId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfpMeetingAttendanceForSupplier(RfpSupplierMeetingAttendance persistObj) {
		rfpSupplierMeetingAttendanceDao.update(persistObj);

	}

	@Override
	public RfpEvent getRfpEventById(String id) {
		return rfpEventDao.findById(id);
	}

	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		return rfpEventDao.getEventSuppliers(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<Supplier> removeSupplier(String meetingId, String supplierId) {
		Supplier selectedSupplier = supplierService.findSuppById(supplierId);
		RfpEventMeeting meeting = rfpEventMeetingDao.getRfpMeetingById(meetingId);
		if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
			if (meeting.getInviteSuppliers().contains(selectedSupplier)) {
				meeting.getInviteSuppliers().remove(selectedSupplier);
			}
		}
		rfpEventMeetingDao.update(meeting);
		return meeting.getInviteSuppliers();
	}

	@Override
	public EventMeetingDocument getMeetingDocumentForDelete(String docId) {
		return rfpEventMeetingDocumentDao.getMeetingDocumentForDelete(docId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeMeetingDocs(EventMeetingDocument document) {
		rfpEventMeetingDocumentDao.delete((RfpEventMeetingDocument) document);
	}

	@Override
	public Integer getCountOfSupplierForMeeting(String meetingId) {
		return rfpEventMeetingDao.getCountOfSupplierForMeeting(meetingId);
	}

	@Override
	public List<RfpEventMeetingReminder> getAllRfpEventMeetingReminderForMeeting(String meetingId) {
		return rfpMeetingReminderDao.getAllRfpMeetingReminderForMeeting(meetingId);
	}

	@Override
	public List<RfpEventMeetingReminder> getMeetingRemindersForNotification() {
		return rfpMeetingReminderDao.getMeetingRemindersForNotification();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String reminderId) {
		rfpMeetingReminderDao.updateImmediately(reminderId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteMeeting(RfpEventMeeting rfpEventMeeting) {
		rfpEventMeetingDao.delete(rfpEventMeeting);
	}

	@Override
	public List<Supplier> getAllSuppliersByMeetId(String meetingId) {
		return rfpEventMeetingDao.getAllSuppliersByMeetId(meetingId);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveOrUpdateAttendance(List<RfpSupplierMeetingAttendance> supplierMeetingAttendances) {
		if (CollectionUtil.isNotEmpty(supplierMeetingAttendances)) {
			for (RfpSupplierMeetingAttendance supplierMeetingAttendance : supplierMeetingAttendances) {
				if (StringUtils.checkString(supplierMeetingAttendance.getId()).length() > 0) {
					RfpSupplierMeetingAttendance prstObj = rfpSupplierMeetingAttendanceDao.findById(supplierMeetingAttendance.getId());
					prstObj.setName(supplierMeetingAttendance.getName());
					prstObj.setDesignation(supplierMeetingAttendance.getDesignation());
					prstObj.setMobileNumber(supplierMeetingAttendance.getMobileNumber());
					prstObj.setAttended(supplierMeetingAttendance.getAttended());
					prstObj.setRemarks(supplierMeetingAttendance.getRemarks());
					if (supplierMeetingAttendance.getAttended() == Boolean.TRUE) {
						prstObj.setMeetingAttendanceStatus(MeetingAttendanceStatus.Accepted);
					}
					rfpSupplierMeetingAttendanceDao.saveOrUpdate(prstObj);
				} else {
					rfpSupplierMeetingAttendanceDao.saveOrUpdate(supplierMeetingAttendance);
				}
			}
		}
	}

	@Override
	public RfpEventMeeting getMeetingForIdAndEvent(String id, String eventId) {
		return rfpEventMeetingDao.getMeetingForIdAndEvent(id, eventId);
	}

	@Override
	public List<Date> getAllMeetingDateByEventId(String eventId) {
		return rfpEventMeetingDao.getAllMeetingDateByEventId(eventId);
	}

	@Override
	public List<RfpEventMeetingDocument> getPlainMeetingDocument(String meetingId) {
		return rfpEventMeetingDao.getPlainMeetingDocument(meetingId);
	}

	@Override
	public List<RfpEventMeeting> getAllRfpMeetingWithPlainDocByEventId(String eventId) {
		List<RfpEventMeeting> meetingList = rfpEventMeetingDao.getAllRfpMeetingWithPlainDocByEventId(eventId);

		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RfpEventMeeting rfpEventMeeting : meetingList) {
				List<RfpEventMeetingDocument> meetingDocumentList = rfpEventMeetingDao.getPlainMeetingDocument(rfpEventMeeting.getId());
				if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
					rfpEventMeeting.setEventMeetingDocument(meetingDocumentList);
				}
				List<Supplier> suppList = null;
				if (CollectionUtil.isNotEmpty(rfpEventMeeting.getInviteSuppliers())) {
					suppList = new ArrayList<>();
					for (Supplier supp : rfpEventMeeting.getInviteSuppliers()) {
						suppList.add(supp.createShallowCopy());
					}
					rfpEventMeeting.setInviteSuppliers(suppList);
				}
				if (CollectionUtil.isNotEmpty(rfpEventMeeting.getRfxEventMeetingContacts())) {
					for (RfpEventMeetingContact contact : rfpEventMeeting.getRfxEventMeetingContacts()) {
						contact.getContactName();
					}
				}
			}
		}
		return meetingList;
	}

	@Override
	public boolean isSiteVisitExist(String eventId) {
		return rfpEventMeetingDao.isSiteVisitExist(eventId);
	}

	public List<String> getMeetingSupplierIds(String meetingId, RfxTypes rfxType) {

		return rfqEventDao.getMeetingSupplierIds(meetingId, rfxType);
	}
}
