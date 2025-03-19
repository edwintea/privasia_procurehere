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

import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfiEventMeetingDao;
import com.privasia.procurehere.core.dao.RfiEventMeetingDocumentDao;
import com.privasia.procurehere.core.dao.RfiMeetingReminderDao;
import com.privasia.procurehere.core.dao.RfiSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfiEventMeetingContact;
import com.privasia.procurehere.core.entity.RfiEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfiEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfiSupplierMeetingAttendance;
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
import com.privasia.procurehere.service.RfiMeetingService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.supplier.SupplierService;

@Service
@Transactional(readOnly = true)
public class RfiMeetingServiceImpl implements RfiMeetingService {

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfiEventMeetingDao rfiEventMeetingDao;

	@Autowired
	RfiEventMeetingDocumentDao rfiEventMeetingDocumentDao;

	@Autowired
	RfiSupplierMeetingAttendanceDao rfiSupplierMeetingAttendanceDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfiMeetingReminderDao rfiMeetingReminderDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Resource
	MessageSource messageSource;
	
	@Autowired
	TatReportService tatReportService;

	protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Override
	@Transactional(readOnly = false)
	public RfiEventMeeting cancelMeeting(RfiEventMeeting eventMeeting) {
		RfiEventMeeting persistObj = getRfiMeetingById(eventMeeting.getId());
		persistObj.setCancelReason(eventMeeting.getCancelReason());
		persistObj.setStatus(MeetingStatus.CANCELLED);
		persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
		persistObj.setModifiedDate(new Date());
		return rfiEventMeetingDao.saveOrUpdate(persistObj);
	}

	@Override
	@Transactional(readOnly = false)
	public RfiEventMeeting saveRfiMeeting(RfiEventMeeting rfiEventMeeting, List<RfiEventMeetingDocument> meetingDocumentList, Boolean selectAllSupplier, String eventId, EventVisibilityType eventVisibility) throws NoSuchMessageException, ApplicationException {

		RfiEventMeeting eventMeeting = null;
		Date appointmentDate;
		TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(eventId, SecurityLibrary.getLoggedInUserTenantId());
		if (StringUtils.checkString(rfiEventMeeting.getId()).length() == 0) {
			RfiEvent rftEventObj = getRfiEventById(rfiEventMeeting.getRfxEvent().getId());
			rfiEventMeeting.setRfxEvent(rftEventObj);
			if (Boolean.TRUE.equals(selectAllSupplier)) {
				rfiEventMeeting.setInviteSuppliers(getEventSuppliers(eventId));
			} else {
				if (CollectionUtil.isNotEmpty(rfiEventMeeting.getSelectedSuppliers())) {
					if (CollectionUtil.isEmpty(rfiEventMeeting.getInviteSuppliers())) {
						rfiEventMeeting.setInviteSuppliers(new ArrayList<Supplier>());
					}
					for (String selectedSupplier : rfiEventMeeting.getSelectedSuppliers()) {
						rfiEventMeeting.getInviteSuppliers().add(new Supplier(selectedSupplier));
					}
				}
			}
			if (EventVisibilityType.PRIVATE.equals(eventVisibility) && CollectionUtil.isEmpty(rfiEventMeeting.getInviteSuppliers())) {
				throw new ApplicationException(messageSource.getMessage("rft.meeting.error.supplierEmpty", new Object[] {}, Global.LOCALE));
			}
			rfiEventMeeting.setStatus(MeetingStatus.SCHEDULED);
			rfiEventMeeting.setRfxEventMeetingDocument(meetingDocumentList);
			eventMeeting = rfiEventMeetingDao.saveOrUpdate(rfiEventMeeting);
			
			if(tatReport != null && tatReport.getEventFirstMeetingDate() == null) {
				LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				tatReportService.updateTatReportEventFirstMeetingDate(tatReport.getId(), rfiEventMeeting.getAppointmentDateTime());
			}
		} else {
			RfiEventMeeting persistObj = getRfiMeetingById(rfiEventMeeting.getId());
			appointmentDate = persistObj.getAppointmentDateTime();
			persistObj.setTitle(rfiEventMeeting.getTitle());
			persistObj.setMeetingType(rfiEventMeeting.getMeetingType());
			persistObj.setVenue(rfiEventMeeting.getVenue());
			persistObj.setMeetingAttendMandatory(rfiEventMeeting.getMeetingAttendMandatory());
			persistObj.setRemarks(rfiEventMeeting.getRemarks());
			if (Boolean.TRUE.equals(selectAllSupplier)) {
				persistObj.setInviteSuppliers(getEventSuppliers(eventId));
			} else {
				Set<String> dbSupplier = new HashSet<String>(getMeetingSupplierIds(rfiEventMeeting.getId(), RfxTypes.RFI));
				if (CollectionUtil.isEmpty(dbSupplier)) {
					LOG.info("empty===========>");
					dbSupplier = new HashSet<String>();
				}
				if (CollectionUtil.isNotEmpty(rfiEventMeeting.getSelectedSuppliers())) {
					LOG.info("not empty===========>");
					dbSupplier.addAll(rfiEventMeeting.getSelectedSuppliers());
				}
				if (CollectionUtil.isNotEmpty(rfiEventMeeting.getRemovedSuppliers())) {
					LOG.info("not empty===========>");
					dbSupplier.removeAll(rfiEventMeeting.getRemovedSuppliers());
				}
				if (CollectionUtil.isEmpty(rfiEventMeeting.getInviteSuppliers())) {
					LOG.info("empty===========>");
					rfiEventMeeting.setInviteSuppliers(new ArrayList<Supplier>());
				}
				for (String supplierId : dbSupplier) {
					rfiEventMeeting.getInviteSuppliers().add(new Supplier(supplierId));
				}
				persistObj.setInviteSuppliers(rfiEventMeeting.getInviteSuppliers());
			}
			if (EventVisibilityType.PRIVATE.equals(eventVisibility) && CollectionUtil.isEmpty(persistObj.getInviteSuppliers())) {
				throw new ApplicationException(messageSource.getMessage("rft.meeting.error.supplierEmpty", new Object[] {}, Global.LOCALE));
			}
			persistObj.setRfxEventMeetingContacts(rfiEventMeeting.getRfxEventMeetingContacts());
			persistObj.setAppointmentDateTime(rfiEventMeeting.getAppointmentDateTime());
			persistObj.setRfxEventMeetingDocument(meetingDocumentList);
			persistObj.setRfxEventMeetingReminder(rfiEventMeeting.getRfxEventMeetingReminder());
			eventMeeting = rfiEventMeetingDao.saveOrUpdate(persistObj);
			
			if(tatReport != null && tatReport.getEventFirstMeetingDate().equals(appointmentDate)) {
				tatReportService.updateTatReportEventFirstMeetingDate(tatReport.getId(), rfiEventMeeting.getAppointmentDateTime());
			}
		}
		return eventMeeting;
	}

	@Override
	@Transactional(readOnly = false)
	public RfiEventMeeting getRfiMeetingById(String id) {
		RfiEventMeeting meeting = rfiEventMeetingDao.getRfiMeetingById(id);
		if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
			for (RfiEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
				contact.getContactName();
			}
		}
		if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
			for (RfiEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
				reminder.getReminderDate();
			}
		}
		return meeting;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfiMeeting(RfiEventMeeting rfiEventMeeting) {
		rfiEventMeetingDao.update(rfiEventMeeting);

	}

	@Override
	@Transactional(readOnly = false)
	public List<RfiEventMeeting> getAllRfiMeetingByEventId(String eventId) {
		List<RfiEventMeeting> meetingList = rfiEventMeetingDao.getRfiMeetingByEventId(eventId);
		// if (CollectionUtil.isNotEmpty(meetingList)) {
		// for (RfiEventMeeting meeting : meetingList) {
		// if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
		// for (RfiEventMeetingDocument document : meeting.getRfxEventMeetingDocument()) {
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
	public void storeEventMettingDocs(RfiEventMeetingDocument document) {
		rfiEventMeetingDocumentDao.save(document);
	}

	@Override
	public RfiEventMeetingDocument getRfiEventMeetingDocument(String docId) {
		return rfiEventMeetingDocumentDao.findById(docId);
	}

	@Override
	public RfiEventMeetingContact getRfiMeetingContactById(String id) {
		return rfiEventMeetingDao.getRfiMeetingContactById(id);
	}

	@Override
	public RfiEventMeeting getOngoingMeeting(String id) {
		RfiEventMeeting meeting = rfiEventMeetingDao.getRfiMeetingByIdAndStatus(id);
		return meeting;
	}

	@Override
	public List<RfiEventMeetingContact> getRfiMeetContactByMeetId(String id) {
		return rfiEventMeetingDao.getRfiMeetContactByMeetId(id);
	}

	@Override
	public Date getMinMeetingDateForEvent(String eventId) {
		return rfiEventMeetingDao.getMinMeetingDateForEvent(eventId);
	}

	@Override
	public Integer getCountOfRfiMeetingByEventId(String eventId) {
		return rfiEventMeetingDao.getCountOfRfiMeetingByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteAllRfiMeetings(String eventId, String eventRequirement) {
		List<RfiEventMeeting> meetingList = rfiEventMeetingDao.getRfiMeetingByEventId(eventId);
		for (RfiEventMeeting eventMeeting : meetingList) {
			rfiEventMeetingDao.delete(eventMeeting);
		}
		// rfiEventMeetingDao.deleteAllMeetings(eventId);
		RfiEvent event = rfiEventDao.findById(eventId);
		event.setMeetingCompleted(Boolean.FALSE);
		event.setMeetingReq(Boolean.FALSE);
		rfiEventDao.update(event);

	}

	@Override
	public List<RfiEventMeeting> loadSupplierMeetingByEventIdAndMeetingStatus(String eventId, MeetingStatus meetStatus, String tenantId) {
		List<RfiEventMeeting> returnList = null;
		List<RfiEventMeeting> meetingList = rfiEventMeetingDao.findMeetByIdAndStatus(eventId, meetStatus, tenantId);

		if (CollectionUtil.isNotEmpty(meetingList)) {
			returnList = new ArrayList<RfiEventMeeting>();
			for (RfiEventMeeting meet : meetingList) {
				RfiEventMeeting clone = meet.createShallowCopy();
				RfiSupplierMeetingAttendance attendance = rfiSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meet.getId(), tenantId);
				clone.setSupplierAttendance(attendance);
				returnList.add(clone);
				List<RfiEventMeetingDocument> meetingDocumentList = rfiEventMeetingDao.getPlainMeetingDocument(meet.getId());
				if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
					clone.setEventMeetingDocument(meetingDocumentList);
				}
			}
		}
		return returnList;
	}

	@Override
	public Date getMaxMeetingDateForEvent(String eventId) {
		return rfiEventMeetingDao.getMaxMeetingDateForEvent(eventId);
	}

	@Override
	public void deleteSupplierMeetingAttendence(String meetingId) {
		rfiSupplierMeetingAttendanceDao.deleteAttendence(meetingId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfiMeetingAttendanceForSupplier(RfiSupplierMeetingAttendance persistObj) {
		rfiSupplierMeetingAttendanceDao.update(persistObj);

	}

	@Override
	public RfiEvent getRfiEventById(String id) {
		return rfiEventDao.findById(id);
	}

	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		return rfiEventDao.getEventSuppliers(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<Supplier> removeSupplier(String meetingId, String supplierId) {
		Supplier selectedSupplier = supplierService.findSuppById(supplierId);
		RfiEventMeeting meeting = rfiEventMeetingDao.getRfiMeetingById(meetingId);
		if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
			if (meeting.getInviteSuppliers().contains(selectedSupplier)) {
				meeting.getInviteSuppliers().remove(selectedSupplier);
			}
		}
		rfiEventMeetingDao.update(meeting);
		return meeting.getInviteSuppliers();
	}

	@Override
	@Transactional(readOnly = false)
	public void removeMeetingDocs(EventMeetingDocument document) {
		rfiEventMeetingDocumentDao.delete((RfiEventMeetingDocument) document);
	}

	@Override
	public EventMeetingDocument getMeetingDocumentForDelete(String docId) {
		return rfiEventMeetingDocumentDao.getMeetingDocumentForDelete(docId);
	}

	@Override
	public Integer getCountOfSupplierForMeeting(String meetingId) {
		return rfiEventMeetingDao.getCountOfSupplierForMeeting(meetingId);
	}

	@Override
	public List<RfiEventMeetingReminder> getAllRfiEventMeetingReminderForMeeting(String meetingId) {
		return rfiMeetingReminderDao.getAllRfiMeetingReminderForMeeting(meetingId);

	}

	@Override
	public List<RfiEventMeetingReminder> getMeetingRemindersForNotification() {
		return rfiMeetingReminderDao.getMeetingRemindersForNotification();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String reminderId) {
		rfiMeetingReminderDao.updateImmediately(reminderId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteMeeting(RfiEventMeeting rfiEventMeeting) {
		rfiEventMeetingDao.delete(rfiEventMeeting);
	}

	@Override
	public List<Supplier> getAllSuppliersByMeetId(String meetingId) {
		return rfiEventMeetingDao.getAllSuppliersByMeetId(meetingId);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveOrUpdateAttendance(List<RfiSupplierMeetingAttendance> supplierMeetingAttendances) {
		if (CollectionUtil.isNotEmpty(supplierMeetingAttendances)) {
			for (RfiSupplierMeetingAttendance supplierMeetingAttendance : supplierMeetingAttendances) {
				if (StringUtils.checkString(supplierMeetingAttendance.getId()).length() > 0) {
					RfiSupplierMeetingAttendance prstObj = rfiSupplierMeetingAttendanceDao.findById(supplierMeetingAttendance.getId());
					prstObj.setName(supplierMeetingAttendance.getName());
					prstObj.setDesignation(supplierMeetingAttendance.getDesignation());
					prstObj.setMobileNumber(supplierMeetingAttendance.getMobileNumber());
					prstObj.setAttended(supplierMeetingAttendance.getAttended());
					prstObj.setRemarks(supplierMeetingAttendance.getRemarks());
					if (supplierMeetingAttendance.getAttended() == Boolean.TRUE) {
						prstObj.setMeetingAttendanceStatus(MeetingAttendanceStatus.Accepted);
					}
					rfiSupplierMeetingAttendanceDao.saveOrUpdate(prstObj);
				} else {
					rfiSupplierMeetingAttendanceDao.saveOrUpdate(supplierMeetingAttendance);
				}
			}
		}
	}

	@Override
	public RfiEventMeeting getMeetingForIdAndEvent(String id, String eventId) {
		return rfiEventMeetingDao.getMeetingForIdAndEvent(id, eventId);
	}

	@Override
	public List<RfiEventMeetingDocument> getPlainMeetingDocument(String meetingId) {
		return rfiEventMeetingDao.getPlainMeetingDocument(meetingId);
	}

	@Override
	public List<RfiEventMeeting> getAllRfiMeetingWithPlainDocByEventId(String eventId) {
		List<RfiEventMeeting> meetingList = rfiEventMeetingDao.getAllRfiMeetingWithPlainDocByEventId(eventId);

		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RfiEventMeeting rfiEventMeeting : meetingList) {
				List<RfiEventMeetingDocument> meetingDocumentList = rfiEventMeetingDao.getPlainMeetingDocument(rfiEventMeeting.getId());
				if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
					rfiEventMeeting.setEventMeetingDocument(meetingDocumentList);
				}
				List<Supplier> suppList = null;
				if (CollectionUtil.isNotEmpty(rfiEventMeeting.getInviteSuppliers())) {
					suppList = new ArrayList<>();
					for (Supplier supp : rfiEventMeeting.getInviteSuppliers()) {
						suppList.add(supp.createShallowCopy());
					}
					rfiEventMeeting.setInviteSuppliers(suppList);
				}
				if (CollectionUtil.isNotEmpty(rfiEventMeeting.getRfxEventMeetingContacts())) {
					for (RfiEventMeetingContact contact : rfiEventMeeting.getRfxEventMeetingContacts()) {
						contact.getContactName();
					}
				}
			}
		}
		return meetingList;
	}

	@Override
	public boolean isSiteVisitExist(String eventId) {
		return rfiEventMeetingDao.isSiteVisitExist(eventId);
	}

	public List<String> getMeetingSupplierIds(String meetingId, RfxTypes rfxType) {

		return rfqEventDao.getMeetingSupplierIds(meetingId, rfxType);
	}
}
