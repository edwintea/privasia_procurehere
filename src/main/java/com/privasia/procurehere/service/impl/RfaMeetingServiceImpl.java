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

import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventMeetingDao;
import com.privasia.procurehere.core.dao.RfaEventMeetingDocumentDao;
import com.privasia.procurehere.core.dao.RfaMeetingReminderDao;
import com.privasia.procurehere.core.dao.RfaSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfaEventMeetingContact;
import com.privasia.procurehere.core.entity.RfaEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfaEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfaSupplierMeetingAttendance;
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
import com.privasia.procurehere.service.RfaMeetingService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.supplier.SupplierService;

@Service
@Transactional(readOnly = true)
public class RfaMeetingServiceImpl implements RfaMeetingService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfaEventMeetingDao rfaEventMeetingDao;

	@Autowired
	RfaEventMeetingDocumentDao rfaEventMeetingDocumentDao;

	@Autowired
	RfaSupplierMeetingAttendanceDao rfaSupplierMeetingAttendanceDao;

	@Autowired
	RfaMeetingReminderDao rfaMeetingReminderDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfqEventDao rfqEventDao;

	@Resource
	MessageSource messageSource;

	@Autowired
	TatReportService tatReportService;

	@Override
	@Transactional(readOnly = false)
	public RfaEventMeeting saveRfaMeeting(RfaEventMeeting rfaEventMeeting, List<RfaEventMeetingDocument> meetingDocumentList, Boolean selectAllSupplier, String eventId, EventVisibilityType eventVisibilityType) throws NoSuchMessageException, ApplicationException {
		RfaEventMeeting meeting = null;
		Date appointmentDate;
		TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(eventId, SecurityLibrary.getLoggedInUserTenantId());
		if (StringUtils.checkString(rfaEventMeeting.getId()).length() == 0) {
			RfaEvent rfaEventObj = getRfaEventById(rfaEventMeeting.getRfxEvent().getId());
			rfaEventMeeting.setRfxEvent(rfaEventObj);
			rfaEventMeeting.setStatus(MeetingStatus.SCHEDULED);
			if (Boolean.TRUE.equals(selectAllSupplier)) {
				rfaEventMeeting.setInviteSuppliers(getEventSuppliers(eventId));
			} else {
				if (CollectionUtil.isNotEmpty(rfaEventMeeting.getSelectedSuppliers())) {
					if (CollectionUtil.isEmpty(rfaEventMeeting.getInviteSuppliers())) {
						rfaEventMeeting.setInviteSuppliers(new ArrayList<Supplier>());
					}
					for (String selectedSupplier : rfaEventMeeting.getSelectedSuppliers()) {
						rfaEventMeeting.getInviteSuppliers().add(new Supplier(selectedSupplier));
					}
				}
			}
			if (EventVisibilityType.PRIVATE.equals(eventVisibilityType) && CollectionUtil.isEmpty(rfaEventMeeting.getInviteSuppliers())) {
				throw new ApplicationException(messageSource.getMessage("rft.meeting.error.supplierEmpty", new Object[] {}, Global.LOCALE));
			}
			rfaEventMeeting.setRfxEventMeetingDocument(meetingDocumentList);
			meeting = rfaEventMeetingDao.saveOrUpdate(rfaEventMeeting);

			if (tatReport != null && tatReport.getEventFirstMeetingDate() == null) {
				tatReportService.updateTatReportEventFirstMeetingDate(tatReport.getId(), rfaEventMeeting.getAppointmentDateTime());
			}

		} else {
			RfaEventMeeting persistObj = getRfaMeetingById(rfaEventMeeting.getId());
			appointmentDate = persistObj.getAppointmentDateTime();
			persistObj.setTitle(rfaEventMeeting.getTitle());
			persistObj.setMeetingType(rfaEventMeeting.getMeetingType());
			persistObj.setVenue(rfaEventMeeting.getVenue());
			persistObj.setRemarks(rfaEventMeeting.getRemarks());
			persistObj.setMeetingAttendMandatory(rfaEventMeeting.getMeetingAttendMandatory());
			if (Boolean.TRUE.equals(selectAllSupplier)) {
				persistObj.setInviteSuppliers(getEventSuppliers(eventId));
			} else {
				Set<String> dbSupplier = new HashSet<String>(getMeetingSupplierIds(rfaEventMeeting.getId(), RfxTypes.RFA));
				if (CollectionUtil.isEmpty(dbSupplier)) {
					LOG.info("empty===========>");
					dbSupplier = new HashSet<String>();
				}
				if (CollectionUtil.isNotEmpty(rfaEventMeeting.getSelectedSuppliers())) {
					LOG.info("not empty===========>");
					dbSupplier.addAll(rfaEventMeeting.getSelectedSuppliers());
				}
				if (CollectionUtil.isNotEmpty(rfaEventMeeting.getRemovedSuppliers())) {
					LOG.info("not empty===========>");
					dbSupplier.removeAll(rfaEventMeeting.getRemovedSuppliers());
				}
				if (CollectionUtil.isEmpty(rfaEventMeeting.getInviteSuppliers())) {
					LOG.info("empty===========>");
					rfaEventMeeting.setInviteSuppliers(new ArrayList<Supplier>());
				}
				for (String supplierId : dbSupplier) {
					rfaEventMeeting.getInviteSuppliers().add(new Supplier(supplierId));
				}
				persistObj.setInviteSuppliers(rfaEventMeeting.getInviteSuppliers());
			}
			if (EventVisibilityType.PRIVATE.equals(eventVisibilityType) && CollectionUtil.isEmpty(persistObj.getInviteSuppliers())) {
				throw new ApplicationException(messageSource.getMessage("rft.meeting.error.supplierEmpty", new Object[] {}, Global.LOCALE));
			}
			persistObj.setRfxEventMeetingContacts(rfaEventMeeting.getRfxEventMeetingContacts());
			persistObj.setAppointmentDateTime(rfaEventMeeting.getAppointmentDateTime());
			persistObj.setRfxEventMeetingDocument(meetingDocumentList);
			persistObj.setRfxEventMeetingReminder(rfaEventMeeting.getRfxEventMeetingReminder());
			meeting = rfaEventMeetingDao.saveOrUpdate(persistObj);

			if (tatReport != null && tatReport.getEventFirstMeetingDate().equals(appointmentDate)) {
				tatReportService.updateTatReportEventFirstMeetingDate(tatReport.getId(), rfaEventMeeting.getAppointmentDateTime());
			}
		}

		return meeting;
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEventMeeting getRfaMeetingById(String id) {
		RfaEventMeeting meeting = rfaEventMeetingDao.getRfaMeetingById(id);
		// if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
		// for (RfaEventMeetingDocument document : meeting.getRfxEventMeetingDocument()) {
		// document.getFileName();
		// }
		// }
		if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
			for (RfaEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
				contact.getContactName();
			}
		}
		if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
			for (RfaEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
				reminder.getReminderDate();
			}
		}
		return meeting;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfaMeeting(RfaEventMeeting rfaEventMeeting) {
		rfaEventMeetingDao.update(rfaEventMeeting);

	}

	@Override
	@Transactional(readOnly = false)
	public List<RfaEventMeeting> getAllRfaMeetingByEventId(String eventId) {
		LOG.info("RfaEventServiceImpl getAllRfaMeetingByEventId called");
		List<RfaEventMeeting> meetingList = rfaEventMeetingDao.getRfaMeetingByEventId(eventId);
		return meetingList;
	}

	@Override
	@Transactional(readOnly = false)
	public void storeEventMettingDocs(RfaEventMeetingDocument document) {
		rfaEventMeetingDocumentDao.save(document);
	}

	@Override
	public RfaEventMeetingDocument getRfaEventMeetingDocument(String docId) {
		return rfaEventMeetingDocumentDao.findById(docId);
	}

	@Override
	public RfaEventMeetingContact getRfaMeetingContactById(String id) {
		return rfaEventMeetingDao.getRfaMeetingContactById(id);
	}

	@Override
	public RfaEventMeeting getOngoingMeeting(String id) {
		RfaEventMeeting meeting = rfaEventMeetingDao.getRfaMeetingByIdAndStatus(id);
		return meeting;
	}

	@Override
	public List<RfaEventMeetingContact> getRfaMeetContactByMeetId(String id) {
		return rfaEventMeetingDao.getRfaMeetContactByMeetId(id);
	}

	@Override
	public Date getMinMeetingDateForEvent(String eventId) {
		return rfaEventMeetingDao.getMinMeetingDateForEvent(eventId);
	}

	@Override
	public Integer getCountOfRfaMeetingByEventId(String eventId) {
		return rfaEventMeetingDao.getCountOfRfaMeetingByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteAllRfaMeetings(String eventId, String eventRequirement) {
		List<RfaEventMeeting> meetingList = rfaEventMeetingDao.getRfaMeetingByEventId(eventId);
		for (RfaEventMeeting eventMeeting : meetingList) {
			rfaEventMeetingDao.delete(eventMeeting);
		}
		// rfaEventMeetingDao.deleteAllMeetings(eventId);
		RfaEvent event = rfaEventDao.findById(eventId);
		event.setMeetingCompleted(Boolean.FALSE);
		event.setMeetingReq(Boolean.FALSE);
		rfaEventDao.update(event);

	}

	@Override
	public List<RfaEventMeeting> loadSupplierMeetingByEventIdAndMeetingStatus(String eventId, MeetingStatus meetStatus, String tenantId) {
		List<RfaEventMeeting> returnList = null;
		List<RfaEventMeeting> meetingList = rfaEventMeetingDao.findMeetByIdAndStatus(eventId, meetStatus, tenantId);

		if (CollectionUtil.isNotEmpty(meetingList)) {
			returnList = new ArrayList<RfaEventMeeting>();
			for (RfaEventMeeting meet : meetingList) {
				RfaEventMeeting clone = meet.createShallowCopy();
				RfaSupplierMeetingAttendance attendance = rfaSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meet.getId(), tenantId);
				clone.setSupplierAttendance(attendance);
				returnList.add(clone);
				List<RfaEventMeetingDocument> meetingDocumentList = rfaEventMeetingDao.getPlainMeetingDocument(meet.getId());
				if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
					clone.setEventMeetingDocument(meetingDocumentList);
				}
			}
		}
		return returnList;
	}

	@Override
	public Date getMaxMeetingDateForEvent(String eventId) {
		return rfaEventMeetingDao.getMaxMeetingDateForEvent(eventId);
	}

	@Override
	public void deleteSupplierMeetingAttendence(String meetingId) {
		rfaSupplierMeetingAttendanceDao.deleteAttendence(meetingId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfaMeetingAttendanceForSupplier(RfaSupplierMeetingAttendance persistObj) {
		rfaSupplierMeetingAttendanceDao.update(persistObj);

	}

	@Override
	public RfaEvent getRfaEventById(String id) {
		return rfaEventDao.findById(id);
	}

	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		return rfaEventDao.getEventSuppliers(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeMeetingDocs(RfaEventMeetingDocument document) {
		rfaEventMeetingDocumentDao.delete(document);
	}

	@Override
	public EventMeetingDocument getMeetingDocumentForDelete(String docId) {
		return rfaEventMeetingDocumentDao.getMeetingDocumentForDelete(docId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeMeetingDocs(EventMeetingDocument document) {
		rfaEventMeetingDocumentDao.delete((RfaEventMeetingDocument) document);
	}

	@Override
	public Integer getCountOfSupplierForMeeting(String meetingId) {
		return rfaEventMeetingDao.getCountOfSupplierForMeeting(meetingId);
	}

	@Override
	public List<RfaEventMeetingReminder> getAllRfaEventMeetingReminderForMeeting(String meetingId) {
		return rfaMeetingReminderDao.getAllRfaMeetingReminderForMeeting(meetingId);
	}

	@Override
	public List<RfaEventMeetingReminder> getMeetingRemindersForNotification() {
		return rfaMeetingReminderDao.getMeetingRemindersForNotification();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String reminderId) {
		rfaMeetingReminderDao.updateImmediately(reminderId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<Supplier> removeSupplier(String meetingId, String supplierId) {
		Supplier selectedSupplier = supplierService.findSuppById(supplierId);
		RfaEventMeeting meeting = rfaEventMeetingDao.getRfaMeetingById(meetingId);
		if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
			if (meeting.getInviteSuppliers().contains(selectedSupplier)) {
				meeting.getInviteSuppliers().remove(selectedSupplier);
			}
		}
		rfaEventMeetingDao.update(meeting);
		return meeting.getInviteSuppliers();
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEventMeeting cancelMeeting(RfaEventMeeting eventMeeting) {
		return rfaEventMeetingDao.saveOrUpdate(eventMeeting);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteMeeting(RfaEventMeeting rfaEventMeeting) {
		rfaEventMeetingDao.delete(rfaEventMeeting);
	}

	@Override
	public List<Supplier> getAllSuppliersByMeetId(String meetingId) {
		return rfaEventMeetingDao.getAllSuppliersByMeetId(meetingId);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveOrUpdateAttendance(List<RfaSupplierMeetingAttendance> supplierMeetingAttendances) {
		if (CollectionUtil.isNotEmpty(supplierMeetingAttendances)) {
			for (RfaSupplierMeetingAttendance supplierMeetingAttendance : supplierMeetingAttendances) {
				if (StringUtils.checkString(supplierMeetingAttendance.getId()).length() > 0) {
					RfaSupplierMeetingAttendance prstObj = rfaSupplierMeetingAttendanceDao.findById(supplierMeetingAttendance.getId());
					prstObj.setName(supplierMeetingAttendance.getName());
					prstObj.setDesignation(supplierMeetingAttendance.getDesignation());
					prstObj.setMobileNumber(supplierMeetingAttendance.getMobileNumber());
					prstObj.setAttended(supplierMeetingAttendance.getAttended());
					prstObj.setRemarks(supplierMeetingAttendance.getRemarks());
					if (supplierMeetingAttendance.getAttended() == Boolean.TRUE) {
						prstObj.setMeetingAttendanceStatus(MeetingAttendanceStatus.Accepted);
					}
					rfaSupplierMeetingAttendanceDao.saveOrUpdate(prstObj);
				} else {
					rfaSupplierMeetingAttendanceDao.saveOrUpdate(supplierMeetingAttendance);
				}
			}
		}
	}

	@Override
	public RfaEventMeeting getMeetingForIdAndEvent(String id, String eventId) {
		return rfaEventMeetingDao.getMeetingForIdAndEvent(id, eventId);
	}

	@Override
	public List<RfaEventMeetingDocument> getPlainMeetingDocument(String meetingId) {
		return rfaEventMeetingDao.getPlainMeetingDocument(meetingId);
	}

	@Override
	public List<RfaEventMeeting> getAllRfaMeetingWithPlainDocByEventId(String eventId) {
		List<RfaEventMeeting> meetingList = rfaEventMeetingDao.getAllRfaMeetingWithPlainDocByEventId(eventId);

		if (CollectionUtil.isNotEmpty(meetingList)) {

			for (RfaEventMeeting rfaEventMeeting : meetingList) {
				List<RfaEventMeetingDocument> meetingDocumentList = rfaEventMeetingDao.getPlainMeetingDocument(rfaEventMeeting.getId());
				if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
					rfaEventMeeting.setEventMeetingDocument(meetingDocumentList);
				}
				List<Supplier> suppList = null;
				if (CollectionUtil.isNotEmpty(rfaEventMeeting.getInviteSuppliers())) {
					suppList = new ArrayList<>();
					for (Supplier supp : rfaEventMeeting.getInviteSuppliers()) {
						suppList.add(supp.createShallowCopy());
					}
					rfaEventMeeting.setInviteSuppliers(suppList);
				}
				if (CollectionUtil.isNotEmpty(rfaEventMeeting.getRfxEventMeetingContacts())) {
					for (RfaEventMeetingContact contact : rfaEventMeeting.getRfxEventMeetingContacts()) {
						contact.getContactName();
					}
				}
			}
		}
		return meetingList;
	}

	@Override
	public boolean isSiteVisitExist(String eventId) {
		return rfaEventMeetingDao.isSiteVisitExist(eventId);
	}

	public List<String> getMeetingSupplierIds(String meetingId, RfxTypes rfxType) {

		return rfqEventDao.getMeetingSupplierIds(meetingId, rfxType);
	}

}
