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
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RftEventMeetingDao;
import com.privasia.procurehere.core.dao.RftEventMeetingDocumentDao;
import com.privasia.procurehere.core.dao.RftMeetingReminderDao;
import com.privasia.procurehere.core.dao.RftSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.entity.RftEventMeetingContact;
import com.privasia.procurehere.core.entity.RftEventMeetingDocument;
import com.privasia.procurehere.core.entity.RftEventMeetingReminder;
import com.privasia.procurehere.core.entity.RftSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RftMeetingService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.supplier.SupplierService;

@Service
@Transactional(readOnly = true)
public class RftMeetingServiceImpl implements RftMeetingService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RftEventMeetingDao rftEventMeetingDao;

	@Autowired
	RftEventMeetingDocumentDao rftEventMeetingDocumentDao;

	@Autowired
	RftSupplierMeetingAttendanceDao rftSupplierMeetingAttendanceDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RftMeetingReminderDao rftMeetingReminderDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Resource
	MessageSource messageSource;
	
	@Autowired
	TatReportService tatReportService;

	@Override
	@Transactional(readOnly = false)
	public RftEventMeeting cancelMeeting(RftEventMeeting eventMeeting) {
		RftEventMeeting persistObj = getRftMeetingById(eventMeeting.getId());
		persistObj.setCancelReason(eventMeeting.getCancelReason());
		persistObj.setStatus(MeetingStatus.CANCELLED);
		persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
		persistObj.setModifiedDate(new Date());
		return rftEventMeetingDao.saveOrUpdate(persistObj);
	}

	@Override
	@Transactional(readOnly = false)
	public RftEventMeeting saveRftMeeting(RftEventMeeting rftEventMeeting, List<RftEventMeetingDocument> meetingDocumentList, Boolean selectAllSupplier, String eventId, EventVisibilityType eventVisibility) throws NoSuchMessageException, ApplicationException {
		
		RftEventMeeting eventMeeting = null;
		Date appointmentDate;
		TatReportPojo tatReport = tatReportService.geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(eventId, SecurityLibrary.getLoggedInUserTenantId());
		if (StringUtils.checkString(rftEventMeeting.getId()).length() == 0) {
			RftEvent rftEventObj = getRftEventById(rftEventMeeting.getRfxEvent().getId());
			rftEventMeeting.setRfxEvent(rftEventObj);
			rftEventMeeting.setStatus(MeetingStatus.SCHEDULED);
			if (Boolean.TRUE.equals(selectAllSupplier)) {
				rftEventMeeting.setInviteSuppliers(getEventSuppliers(eventId));
			} else {
				if (CollectionUtil.isNotEmpty(rftEventMeeting.getSelectedSuppliers())) {
					if (CollectionUtil.isEmpty(rftEventMeeting.getInviteSuppliers())) {
						rftEventMeeting.setInviteSuppliers(new ArrayList<Supplier>());
					}
					for (String selectedSupplier : rftEventMeeting.getSelectedSuppliers()) {
						rftEventMeeting.getInviteSuppliers().add(new Supplier(selectedSupplier));
					}
				}
			}
			if (EventVisibilityType.PRIVATE.equals(eventVisibility) && CollectionUtil.isEmpty(rftEventMeeting.getInviteSuppliers())) {
				throw new ApplicationException(messageSource.getMessage("rft.meeting.error.supplierEmpty", new Object[] {}, Global.LOCALE));
			}
			rftEventMeeting.setCreatedBy(SecurityLibrary.getLoggedInUser());
			rftEventMeeting.setCreatedDate(new Date());
			rftEventMeeting.setRfxEventMeetingDocument(meetingDocumentList);
			eventMeeting = rftEventMeetingDao.saveOrUpdate(rftEventMeeting);
			
			if(tatReport != null && tatReport.getEventFirstMeetingDate() == null) {
				tatReportService.updateTatReportEventFirstMeetingDate(tatReport.getId(), rftEventMeeting.getAppointmentDateTime());
			}
		} else {
			RftEventMeeting persistObj = getRftMeetingById(rftEventMeeting.getId());
			appointmentDate = persistObj.getAppointmentDateTime();
			persistObj.setTitle(rftEventMeeting.getTitle());
			persistObj.setMeetingType(rftEventMeeting.getMeetingType());
			persistObj.setVenue(rftEventMeeting.getVenue());
			persistObj.setRemarks(rftEventMeeting.getRemarks());
			persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
			persistObj.setModifiedDate(new Date());
			if (Boolean.TRUE.equals(selectAllSupplier)) {
				persistObj.setInviteSuppliers(getEventSuppliers(eventId));
			} else {
				Set<String> dbSupplier = new HashSet<String>(getMeetingSupplierIds(rftEventMeeting.getId(), RfxTypes.RFT));
				if (CollectionUtil.isEmpty(dbSupplier)) {
					LOG.info("empty===========>");
					dbSupplier = new HashSet<String>();
				}
				if (CollectionUtil.isNotEmpty(rftEventMeeting.getSelectedSuppliers())) {
					LOG.info("not empty===========>");
					dbSupplier.addAll(rftEventMeeting.getSelectedSuppliers());
				}
				if (CollectionUtil.isNotEmpty(rftEventMeeting.getRemovedSuppliers())) {
					LOG.info("not empty===========>");
					dbSupplier.removeAll(rftEventMeeting.getRemovedSuppliers());
				}
				if (CollectionUtil.isEmpty(rftEventMeeting.getInviteSuppliers())) {
					LOG.info("empty===========>");
					rftEventMeeting.setInviteSuppliers(new ArrayList<Supplier>());
				}
				for (String supplierId : dbSupplier) {
					rftEventMeeting.getInviteSuppliers().add(new Supplier(supplierId));
				}
				persistObj.setInviteSuppliers(rftEventMeeting.getInviteSuppliers());
			}
			if (EventVisibilityType.PRIVATE.equals(eventVisibility) && CollectionUtil.isEmpty(persistObj.getInviteSuppliers())) {
				throw new ApplicationException(messageSource.getMessage("rft.meeting.error.supplierEmpty", new Object[] {}, Global.LOCALE));
			}
			persistObj.setMeetingAttendMandatory(rftEventMeeting.getMeetingAttendMandatory());
			persistObj.setRfxEventMeetingContacts(rftEventMeeting.getRfxEventMeetingContacts());
			persistObj.setAppointmentDateTime(rftEventMeeting.getAppointmentDateTime());
			persistObj.setRfxEventMeetingDocument(meetingDocumentList);
			if (rftEventMeeting.getRfxEventMeetingReminder() != null) {
				persistObj.setRfxEventMeetingReminder(rftEventMeeting.getRfxEventMeetingReminder());
			}
			eventMeeting = rftEventMeetingDao.saveOrUpdate(persistObj);
			
			if(tatReport != null && tatReport.getEventFirstMeetingDate().equals(appointmentDate)) {
				tatReportService.updateTatReportEventFirstMeetingDate(tatReport.getId(), rftEventMeeting.getAppointmentDateTime());
			}
		}
		return eventMeeting;
	}

	@Override
	@Transactional(readOnly = false)
	public RftEventMeeting getRftMeetingById(String id) {
		RftEventMeeting meeting = rftEventMeetingDao.getRftMeetingById(id);
		// if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
		// for (RftEventMeetingDocument document : meeting.getRfxEventMeetingDocument()) {
		// document.getFileName();
		// }
		// }
		if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
			for (RftEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
				contact.getContactName();
			}
		}
		if (meeting != null && CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
			for (RftEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
				reminder.getReminderDate();
			}
		}
		return meeting;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRftMeeting(RftEventMeeting rftEventMeeting) {
		rftEventMeetingDao.update(rftEventMeeting);

	}

	@Override
	@Transactional(readOnly = false)
	public List<RftEventMeeting> getRftMeetingByEventId(String eventId) {
		LOG.info("RftEventServiceImpl getAllRftMeetingByEventId called");
		List<RftEventMeeting> meetingList = rftEventMeetingDao.getRftMeetingByEventId(eventId);
		return meetingList;
	}

	@Override
	public RftEventMeetingDocument getRftEventMeetingDocument(String docId) {
		return rftEventMeetingDocumentDao.findById(docId);
	}

	@Override
	public RftEventMeetingContact getRftMeetingContactById(String id) {
		return rftEventMeetingDao.getRftMeetingContactById(id);
	}

	@Override
	public RftEventMeeting getOngoingMeeting(String id) {
		RftEventMeeting meeting = rftEventMeetingDao.getRftMeetingByIdAndStatus(id);
		return meeting;
	}

	@Override
	public List<RftEventMeetingContact> getRftMeetContactByMeetId(String id) {
		return rftEventMeetingDao.getRftMeetContactByMeetId(id);
	}

	@Override
	public Date getMinMeetingDateForEvent(String eventId) {
		return rftEventMeetingDao.getMinMeetingDateForEvent(eventId);
	}

	@Override
	public Integer getCountOfRftMeetingByEventId(String eventId) {
		return rftEventMeetingDao.getCountOfRftMeetingByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteAllRftMeetings(String eventId, String eventRequirement) {
		List<RftEventMeeting> meetingList = rftEventMeetingDao.getRftMeetingByEventId(eventId);
		for (RftEventMeeting eventMeeting : meetingList) {
			rftEventMeetingDao.delete(eventMeeting);
		}

		// TODO:: ??????????????????????
		// rftEventMeetingDao.deleteAllMeetings(eventId);

		/**
		 * RftEvent event = rftEventDao.findById(eventId); event.setMeetingCompleted(Boolean.FALSE);
		 * event.setMeetingReq(Boolean.FALSE); rftEventDao.update(event);
		 */

		rftEventDao.updateEventMeetingFlagsToFalse(eventId);
	}

	@Override
	public List<RftEventMeeting> loadSupplierMeetingByEventIdAndMeetingStatus(String eventId, MeetingStatus meetStatus, String tenantId) {
		List<RftEventMeeting> returnList = null;
		List<RftEventMeeting> meetingList = rftEventMeetingDao.findMeetByIdAndStatus(eventId, meetStatus, tenantId);

		if (CollectionUtil.isNotEmpty(meetingList)) {
			returnList = new ArrayList<RftEventMeeting>();
			for (RftEventMeeting meet : meetingList) {
				RftEventMeeting clone = meet.createShallowCopy();
				RftSupplierMeetingAttendance attendance = rftSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meet.getId(), tenantId);
				clone.setSupplierAttendance(attendance);
				List<RftEventMeetingDocument> meetingDocumentList = rftEventMeetingDao.getPlainMeetingDocument(meet.getId());
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
		return rftEventMeetingDao.getMaxMeetingDateForEvent(eventId);
	}

	@Override
	public void deleteSupplierMeetingAttendence(String meetingId) {
		rftSupplierMeetingAttendanceDao.deleteAttendence(meetingId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRftMeetingAttendanceForSupplier(RftSupplierMeetingAttendance persistObj) {
		rftSupplierMeetingAttendanceDao.update(persistObj);

	}

	@Override
	public RftEvent getRftEventById(String id) {
		return rftEventDao.findById(id);
	}

	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		return rftEventDao.getEventSuppliers(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeMeetingDocs(EventMeetingDocument document) {
		rftEventMeetingDocumentDao.delete((RftEventMeetingDocument) document);
	}

	@Override
	public EventMeetingDocument getMeetingDocumentForDelete(String docId) {
		return rftEventMeetingDocumentDao.getMeetingDocumentForDelete(docId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<Supplier> removeSupplier(String meetingId, String supplierId) {
		Supplier selectedSupplier = supplierService.findSuppById(supplierId);
		RftEventMeeting meeting = rftEventMeetingDao.getRftMeetingById(meetingId);
		if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
			if (meeting.getInviteSuppliers().contains(selectedSupplier)) {
				meeting.getInviteSuppliers().remove(selectedSupplier);
			}
		}
		rftEventMeetingDao.update(meeting);
		return meeting.getInviteSuppliers();
	}

	@Override
	public Integer getCountOfSupplierForMeeting(String meetingId) {
		return rftEventMeetingDao.getCountOfSupplierForMeeting(meetingId);
	}

	@Override
	public List<RftEventMeetingReminder> getAllRftEventMeetingReminderForMeeting(String meetingId) {
		return rftMeetingReminderDao.getAllRftMeetingReminderForMeeting(meetingId);
	}

	@Override
	public List<RftEventMeetingReminder> getMeetingRemindersForNotification() {
		return rftMeetingReminderDao.getMeetingRemindersForNotification();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String reminderId) {
		rftMeetingReminderDao.updateImmediately(reminderId);
	}

	@Override
	public Integer countMeetingsForSupplier(String supplierId) {
		return rftEventMeetingDao.countMeetingsForSupplier(supplierId, null);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteMeeting(RftEventMeeting rftEventMeeting) {
		rftEventMeetingDao.delete(rftEventMeeting);
	}

	@Override
	public List<Supplier> getAllSuppliersByMeetId(String meetingId) {
		return rftEventMeetingDao.getAllSuppliersByMeetId(meetingId);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveOrUpdateAttendance(List<RftSupplierMeetingAttendance> supplierMeetingAttendances) {
		if (CollectionUtil.isNotEmpty(supplierMeetingAttendances)) {
			for (RftSupplierMeetingAttendance supplierMeetingAttendance : supplierMeetingAttendances) {
				if (StringUtils.checkString(supplierMeetingAttendance.getId()).length() > 0) {
					RftSupplierMeetingAttendance prstObj = rftSupplierMeetingAttendanceDao.findById(supplierMeetingAttendance.getId());
					prstObj.setName(supplierMeetingAttendance.getName());
					prstObj.setDesignation(supplierMeetingAttendance.getDesignation());
					prstObj.setMobileNumber(supplierMeetingAttendance.getMobileNumber());
					prstObj.setAttended(supplierMeetingAttendance.getAttended());
					prstObj.setRemarks(supplierMeetingAttendance.getRemarks());
					prstObj.setMeetingAttendanceStatus(supplierMeetingAttendance.getMeetingAttendanceStatus());
					rftSupplierMeetingAttendanceDao.saveOrUpdate(prstObj);
				} else {
					rftSupplierMeetingAttendanceDao.saveOrUpdate(supplierMeetingAttendance);
				}
			}
		}
	}

	@Override
	public RftEventMeeting getMeetingForIdAndEvent(String id, String eventId) {
		return rftEventMeetingDao.getMeetingForIdAndEvent(id, eventId);
	}

	@Override
	public List<RftEventMeetingDocument> getPlainMeetingDocument(String meetingId) {
		return rftEventMeetingDao.getPlainMeetingDocument(meetingId);
	}

	@Override
	public List<RftEventMeeting> getAllRftMeetingByEventId(String eventId) {
		List<RftEventMeeting> meetingList = rftEventMeetingDao.getAllRftMeetingByEventId(eventId);

		if (CollectionUtil.isNotEmpty(meetingList)) {

			for (RftEventMeeting rftEventMeeting : meetingList) {
				List<RftEventMeetingDocument> meetingDocumentList = rftEventMeetingDao.getPlainMeetingDocument(rftEventMeeting.getId());
				if (CollectionUtil.isNotEmpty(meetingDocumentList)) {
					rftEventMeeting.setEventMeetingDocument(meetingDocumentList);
				}
				List<Supplier> suppList = null;
				if (CollectionUtil.isNotEmpty(rftEventMeeting.getInviteSuppliers())) {
					suppList = new ArrayList<>();
					for (Supplier supp : rftEventMeeting.getInviteSuppliers()) {
						suppList.add(supp.createShallowCopy());
					}
					rftEventMeeting.setInviteSuppliers(suppList);
				}
				if (CollectionUtil.isNotEmpty(rftEventMeeting.getRfxEventMeetingContacts())) {
					for (RftEventMeetingContact contact : rftEventMeeting.getRfxEventMeetingContacts()) {
						contact.getContactName();
					}
				}
			}
		}
		return meetingList;
	}

	@Override
	public Date findMandatorySiteVisitMeetingsByEventId(String eventId, RfxTypes eventType) {
		return rftEventMeetingDao.findMandatorySiteVisitMeetingsByEventId(eventId, eventType);
	}

	@Override
	public boolean isSiteVisitExist(String eventId) {
		return rftEventMeetingDao.isSiteVisitExist(eventId);
	}

	public List<String> getMeetingSupplierIds(String meetingId, RfxTypes rfxType) {

		return rfqEventDao.getMeetingSupplierIds(meetingId, rfxType);
	}

}
