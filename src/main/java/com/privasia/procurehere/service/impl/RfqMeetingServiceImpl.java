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

import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqEventMeetingDao;
import com.privasia.procurehere.core.dao.RfqEventMeetingDocumentDao;
import com.privasia.procurehere.core.dao.RfqMeetingReminderDao;
import com.privasia.procurehere.core.dao.RfqSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventMeeting;
import com.privasia.procurehere.core.entity.RfqEventMeetingContact;
import com.privasia.procurehere.core.entity.RfqEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfqEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfqSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfqMeetingService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.supplier.SupplierService;

@Service
@Transactional(readOnly = true)
public class RfqMeetingServiceImpl implements RfqMeetingService {

	protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RfqEventDao rfqEventDao;

	@Resource
	MessageSource messageSource;

	@Autowired
	RfqEventMeetingDao rfqEventMeetingDao;

	@Autowired
	RfqEventMeetingDocumentDao rfqEventMeetingDocumentDao;

	@Autowired
	RfqSupplierMeetingAttendanceDao rfqSupplierMeetingAttendanceDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfqMeetingReminderDao rfqMeetingReminderDao;
	
	@Autowired
	TatReportService tatReportService;

	@Override
	@Transactional(readOnly = false)
	public RfqEventMeeting cancelMeeting(RfqEventMeeting eventMeeting) {
		RfqEventMeeting persistObj = getMeetingById(eventMeeting.getId());
		persistObj.setCancelReason(eventMeeting.getCancelReason());
		persistObj.setStatus(MeetingStatus.CANCELLED);
		persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
		persistObj.setModifiedDate(new Date());
		return rfqEventMeetingDao.saveOrUpdate(persistObj);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEventMeeting saveMeeting(RfqEventMeeting rfqEventMeeting, List<RfqEventMeetingDocument> meetingDocumentList, Boolean selectAllSupplier, String eventId, EventVisibilityType eventVisibilityType) throws NoSuchMessageException, ApplicationException {
		
		RfqEventMeeting eventMeeting = null;
		Date appointmentDate;
		TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(eventId, SecurityLibrary.getLoggedInUserTenantId());
		if (StringUtils.checkString(rfqEventMeeting.getId()).length() == 0) {
			RfqEvent rftEventObj = getEventById(rfqEventMeeting.getRfxEvent().getId());
			rfqEventMeeting.setRfxEvent(rftEventObj);
			rfqEventMeeting.setRfxEventMeetingDocument(meetingDocumentList);
			rfqEventMeeting.setStatus(MeetingStatus.SCHEDULED);
			if (Boolean.TRUE.equals(selectAllSupplier)) {
				rfqEventMeeting.setInviteSuppliers(getEventSuppliers(eventId));
			} else {
				if (CollectionUtil.isNotEmpty(rfqEventMeeting.getSelectedSuppliers())) {
					if (CollectionUtil.isEmpty(rfqEventMeeting.getInviteSuppliers())) {
						rfqEventMeeting.setInviteSuppliers(new ArrayList<Supplier>());
					}
					for (String selectedSupplier : rfqEventMeeting.getSelectedSuppliers()) {
						rfqEventMeeting.getInviteSuppliers().add(new Supplier(selectedSupplier));
					}
				}
			}
			if (EventVisibilityType.PRIVATE.equals(eventVisibilityType) && CollectionUtil.isEmpty(rfqEventMeeting.getInviteSuppliers())) {
				throw new ApplicationException(messageSource.getMessage("rft.meeting.error.supplierEmpty", new Object[] {}, Global.LOCALE));
			}
			eventMeeting = rfqEventMeetingDao.saveOrUpdate(rfqEventMeeting);
			
			if(tatReport != null && tatReport.getEventFirstMeetingDate() == null) {
				tatReportService.updateTatReportEventFirstMeetingDate(tatReport.getId(), rfqEventMeeting.getAppointmentDateTime());
			}
		} else {
			RfqEventMeeting persistObj = getMeetingById(rfqEventMeeting.getId());
			appointmentDate = persistObj.getAppointmentDateTime();
			persistObj.setTitle(rfqEventMeeting.getTitle());
			persistObj.setMeetingType(rfqEventMeeting.getMeetingType());
			persistObj.setVenue(rfqEventMeeting.getVenue());
			persistObj.setRemarks(rfqEventMeeting.getRemarks());
			if (Boolean.TRUE.equals(selectAllSupplier)) {
				persistObj.setInviteSuppliers(getEventSuppliers(eventId));
			} else {
				Set<String> dbSupplier = new HashSet<String>(getMeetingSupplierIds(rfqEventMeeting.getId(), RfxTypes.RFQ));
				if (CollectionUtil.isEmpty(dbSupplier)) {
					LOG.info("empty===========>");
					dbSupplier = new HashSet<String>();
				}
				if (CollectionUtil.isNotEmpty(rfqEventMeeting.getSelectedSuppliers())) {
					LOG.info("not empty===========>");
					dbSupplier.addAll(rfqEventMeeting.getSelectedSuppliers());
				}
				if (CollectionUtil.isNotEmpty(rfqEventMeeting.getRemovedSuppliers())) {
					LOG.info("not empty===========>");
					dbSupplier.removeAll(rfqEventMeeting.getRemovedSuppliers());
				}
				if (CollectionUtil.isEmpty(rfqEventMeeting.getInviteSuppliers())) {
					LOG.info("empty===========>");
					rfqEventMeeting.setInviteSuppliers(new ArrayList<Supplier>());
				}
				for (String supplierId : dbSupplier) {
					rfqEventMeeting.getInviteSuppliers().add(new Supplier(supplierId));
				}
				persistObj.setInviteSuppliers(rfqEventMeeting.getInviteSuppliers());
			}
			if (EventVisibilityType.PRIVATE.equals(eventVisibilityType) && CollectionUtil.isEmpty(persistObj.getInviteSuppliers())) {
				throw new ApplicationException(messageSource.getMessage("rft.meeting.error.supplierEmpty", new Object[] {}, Global.LOCALE));
			}
			persistObj.setMeetingAttendMandatory(rfqEventMeeting.getMeetingAttendMandatory());
			persistObj.setRfxEventMeetingContacts(rfqEventMeeting.getRfxEventMeetingContacts());
			persistObj.setAppointmentDateTime(rfqEventMeeting.getAppointmentDateTime());
			persistObj.setRfxEventMeetingDocument(meetingDocumentList);
			persistObj.setRfxEventMeetingReminder(rfqEventMeeting.getRfxEventMeetingReminder());
			eventMeeting = rfqEventMeetingDao.saveOrUpdate(persistObj);
			
			if(tatReport != null && tatReport.getEventFirstMeetingDate().equals(appointmentDate)) {
				tatReportService.updateTatReportEventFirstMeetingDate(tatReport.getId(), rfqEventMeeting.getAppointmentDateTime());
			}
		}
		return eventMeeting;
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEventMeeting getMeetingById(String id) {
		RfqEventMeeting meeting = rfqEventMeetingDao.getMeetingById(id);
		if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
			for (RfqEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
				contact.getContactName();
			}
		}
		if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
			for (RfqEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
				reminder.getReminderDate();
			}
		}
		return meeting;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateMeeting(RfqEventMeeting rftEventMeeting) {
		rfqEventMeetingDao.update(rftEventMeeting);

	}

	@Override
	@Transactional(readOnly = false)
	public List<RfqEventMeeting> getAllMeetingByEventId(String eventId) {
		List<RfqEventMeeting> meetingList = rfqEventMeetingDao.getMeetingByEventId(eventId);
		// if (CollectionUtil.isNotEmpty(meetingList)) {
		// for (RfqEventMeeting meeting : meetingList) {
		// if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
		// for (RfqEventMeetingDocument document : meeting.getRfxEventMeetingDocument()) {
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
	public void storeEventMettingDocs(RfqEventMeetingDocument document) {
		rfqEventMeetingDocumentDao.save(document);
	}

	@Override
	public RfqEventMeetingDocument getEventMeetingDocument(String docId) {
		return rfqEventMeetingDocumentDao.findById(docId);
	}

	@Override
	public RfqEventMeetingContact getMeetingContactById(String id) {
		return rfqEventMeetingDao.getMeetingContactById(id);
	}

	@Override
	public RfqEventMeeting getOngoingMeeting(String id) {
		RfqEventMeeting meeting = rfqEventMeetingDao.getMeetingByIdAndStatus(id);
		return meeting;
	}

	@Override
	public List<RfqEventMeetingContact> getMeetContactByMeetId(String id) {
		return rfqEventMeetingDao.getMeetContactByMeetId(id);
	}

	@Override
	public Date getMinMeetingDateForEvent(String eventId) {
		return rfqEventMeetingDao.getMinMeetingDateForEvent(eventId);
	}

	@Override
	public Integer getCountOfMeetingByEventId(String eventId) {
		return rfqEventMeetingDao.getCountOfMeetingByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteAllMeetings(String eventId, String eventRequirement) {
		List<RfqEventMeeting> meetingList = rfqEventMeetingDao.getMeetingByEventId(eventId);
		for (RfqEventMeeting rfqEventMeeting : meetingList) {
			rfqEventMeetingDao.delete(rfqEventMeeting);
		}
		// rfqEventMeetingDao.deleteAllMeetings(eventId);
		RfqEvent event = rfqEventDao.findById(eventId);
		event.setMeetingCompleted(Boolean.FALSE);
		event.setMeetingReq(Boolean.FALSE);
		rfqEventDao.update(event);
	}

	@Override
	public List<RfqEventMeeting> loadSupplierMeetingByEventIdAndMeetingStatus(String eventId, MeetingStatus meetStatus, String tenantId) {
		List<RfqEventMeeting> returnList = null;
		List<RfqEventMeeting> meetingList = rfqEventMeetingDao.findMeetByIdAndStatus(eventId, meetStatus, tenantId);

		if (CollectionUtil.isNotEmpty(meetingList)) {
			returnList = new ArrayList<RfqEventMeeting>();
			for (RfqEventMeeting meet : meetingList) {
				RfqEventMeeting clone = meet.createShallowCopy();
				RfqSupplierMeetingAttendance attendance = rfqSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meet.getId(), tenantId);
				clone.setSupplierAttendance(attendance);
				List<RfqEventMeetingDocument> meetingDocumentList = rfqEventMeetingDao.getPlainMeetingDocument(meet.getId());
				if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
					clone.setEventMeetingDocument(meetingDocumentList);
				}
				returnList.add(clone);
			}
		}
		return returnList;
	}

	@Override
	public Date getMaxMeetingDateForEvent(String eventId) {
		return rfqEventMeetingDao.getMaxMeetingDateForEvent(eventId);
	}

	@Override
	public void deleteSupplierMeetingAttendence(String meetingId) {
		rfqSupplierMeetingAttendanceDao.deleteAttendence(meetingId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateMeetingAttendanceForSupplier(RfqSupplierMeetingAttendance persistObj) {
		rfqSupplierMeetingAttendanceDao.update(persistObj);

	}

	@Override
	public RfqEvent getEventById(String id) {
		return rfqEventDao.findById(id);
	}

	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		return rfqEventDao.getEventSuppliers(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<Supplier> removeSupplier(String meetingId, String supplierId) {
		Supplier selectedSupplier = supplierService.findSuppById(supplierId);
		RfqEventMeeting meeting = rfqEventMeetingDao.getMeetingById(meetingId);
		if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
			if (meeting.getInviteSuppliers().contains(selectedSupplier)) {
				meeting.getInviteSuppliers().remove(selectedSupplier);
			}
		}
		rfqEventMeetingDao.update(meeting);
		return meeting.getInviteSuppliers();
	}

	@Override
	public EventMeetingDocument getMeetingDocumentForDelete(String docId) {
		return rfqEventMeetingDocumentDao.getMeetingDocumentForDelete(docId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeMeetingDocs(EventMeetingDocument document) {
		rfqEventMeetingDocumentDao.delete((RfqEventMeetingDocument) document);
	}

	@Override
	public Integer getCountOfSupplierForMeeting(String meetingId) {
		return rfqEventMeetingDao.getCountOfSupplierForMeeting(meetingId);
	}

	@Override
	public List<RfqEventMeetingReminder> getAllRfqEventMeetingReminderForMeeting(String meetingId) {
		return rfqMeetingReminderDao.getAllRfqMeetingReminderForMeeting(meetingId);

	}

	@Override
	public List<RfqEventMeetingReminder> getMeetingRemindersForNotification() {
		return rfqMeetingReminderDao.getMeetingRemindersForNotification();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String reminderId) {
		rfqMeetingReminderDao.updateImmediately(reminderId);
	}

	@Override
	public RfqEventMeetingDocument getRfqEventMeetingDocument(String docId) {
		return rfqEventMeetingDocumentDao.findById(docId);
	}

	@Override
	public RfqEvent getRfqEventById(String id) {
		return rfqEventDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteMeeting(RfqEventMeeting rfqEventMeeting) {
		rfqEventMeetingDao.delete(rfqEventMeeting);
	}

	@Override
	public List<Supplier> getAllSuppliersByMeetId(String meetingId) {
		return rfqEventMeetingDao.getAllSuppliersByMeetId(meetingId);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveOrUpdateAttendance(List<RfqSupplierMeetingAttendance> supplierMeetingAttendances) {
		if (CollectionUtil.isNotEmpty(supplierMeetingAttendances)) {
			for (RfqSupplierMeetingAttendance supplierMeetingAttendance : supplierMeetingAttendances) {
				if (StringUtils.checkString(supplierMeetingAttendance.getId()).length() > 0) {
					RfqSupplierMeetingAttendance prstObj = rfqSupplierMeetingAttendanceDao.findById(supplierMeetingAttendance.getId());
					prstObj.setName(supplierMeetingAttendance.getName());
					prstObj.setDesignation(supplierMeetingAttendance.getDesignation());
					prstObj.setMobileNumber(supplierMeetingAttendance.getMobileNumber());
					prstObj.setAttended(supplierMeetingAttendance.getAttended());
					prstObj.setRemarks(supplierMeetingAttendance.getRemarks());
					prstObj.setMeetingAttendanceStatus(supplierMeetingAttendance.getMeetingAttendanceStatus());
					rfqSupplierMeetingAttendanceDao.saveOrUpdate(prstObj);
				} else {
					rfqSupplierMeetingAttendanceDao.saveOrUpdate(supplierMeetingAttendance);
				}
			}
		}
	}

	@Override
	public RfqEventMeeting getMeetingForIdAndEvent(String id, String eventId) {
		return rfqEventMeetingDao.getMeetingForIdAndEvent(id, eventId);
	}

	@Override
	public List<RfqEventMeetingDocument> getPlainMeetingDocument(String meetingId) {
		return rfqEventMeetingDao.getPlainMeetingDocument(meetingId);
	}

	@Override
	public List<RfqEventMeeting> getAllRfqMeetingWithPlainDocByEventId(String eventId) {
		List<RfqEventMeeting> meetingList = rfqEventMeetingDao.getAllRfqMeetingWithPlainDocByEventId(eventId);

		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RfqEventMeeting rfqEventMeeting : meetingList) {
				List<RfqEventMeetingDocument> meetingDocumentList = rfqEventMeetingDao.getPlainMeetingDocument(rfqEventMeeting.getId());
				if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
					rfqEventMeeting.setEventMeetingDocument(meetingDocumentList);
				}
				List<Supplier> suppList = null;
				if (CollectionUtil.isNotEmpty(rfqEventMeeting.getInviteSuppliers())) {
					suppList = new ArrayList<>();
					for (Supplier supp : rfqEventMeeting.getInviteSuppliers()) {
						suppList.add(supp.createShallowCopy());
					}
					rfqEventMeeting.setInviteSuppliers(suppList);
				}
				if (CollectionUtil.isNotEmpty(rfqEventMeeting.getRfxEventMeetingContacts())) {
					for (RfqEventMeetingContact contact : rfqEventMeeting.getRfxEventMeetingContacts()) {
						contact.getContactName();
					}
				}
			}
		}
		return meetingList;
	}

	@Override
	public boolean isSiteVisitExist(String eventId) {
		return rfqEventMeetingDao.isSiteVisitExist(eventId);
	}

	@Override
	public List<Supplier> getEventSuppliersWithIdAndName(String eventId) {
		return rfqEventDao.getEventSuppliersWithIdAndName(eventId);
	}

	@Override
	public List<Supplier> getEventSuppliersWithIdAndName(String eventId, TableDataInput input, RfxTypes rfxTypes) {
		return rfqEventDao.getEventSuppliersWithIdAndName(eventId, input, rfxTypes);
	}

	@Override
	public long getEventSuppliersCountWithIdAndName(String eventId, RfxTypes rfxType) {
		return rfqEventDao.getEventSuppliersCountWithIdAndName(eventId, rfxType);
	}

	@Override
	public long getEventSuppliersFilterCount(String eventId, TableDataInput input, RfxTypes rfxType) {
		return rfqEventDao.getEventSuppliersFilterCount(eventId, input, rfxType);
	}

	@Override
	public List<String> getMeetingSupplierIds(String meetingId, RfxTypes rfxType) {

		return rfqEventDao.getMeetingSupplierIds(meetingId, rfxType);
	}

}
