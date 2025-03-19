package com.privasia.procurehere.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.dao.RfaSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RfaSupplierSor;
import com.privasia.procurehere.core.entity.RfaSupplierSorItem;
import com.privasia.procurehere.core.entity.RfqSupplierSor;
import com.privasia.procurehere.core.entity.RfqSupplierSorItem;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.AuctionBidsDao;
import com.privasia.procurehere.core.dao.AuctionRulesDao;
import com.privasia.procurehere.core.dao.RfaEventContactDao;
import com.privasia.procurehere.core.dao.RfaEventMeetingDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.entity.AuctionBids;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaCqOption;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventContact;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfaEventMeetingContact;
import com.privasia.procurehere.core.entity.RfaEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaReminder;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RfaSupplierCq;
import com.privasia.procurehere.core.entity.RfaSupplierCqItem;
import com.privasia.procurehere.core.entity.RfaSupplierCqOption;
import com.privasia.procurehere.core.entity.RfaSupplierTeamMember;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.PreBidByType;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.TimeExtensionType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.pojo.EvaluationAuctionPojo;
import com.privasia.procurehere.core.pojo.EvaluationAuctionRulePojo;
import com.privasia.procurehere.core.pojo.EvaluationBiddingPricePojo;
import com.privasia.procurehere.core.pojo.EvaluationBqItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationBqPojo;
import com.privasia.procurehere.core.pojo.EvaluationContactsPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqPojo;
import com.privasia.procurehere.core.pojo.EvaluationDocumentPojo;
import com.privasia.procurehere.core.pojo.EvaluationMeetingContactsPojo;
import com.privasia.procurehere.core.pojo.EvaluationMeetingPojo;
import com.privasia.procurehere.core.pojo.EvaluationPojo;
import com.privasia.procurehere.core.pojo.EvaluationSupplierBidsPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.UserService;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

@Service
@Transactional(readOnly = true)
public class RfaEventSupplierServiceImpl implements RfaEventSupplierService {

	private static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@Autowired
	RfaSupplierTeamMemberDao rfaSupplierTeamMemberDao;

	@Autowired
	RfaEventSupplierDao rfaEventSupplierDao;

	@Autowired
	RfaEventContactDao rfaEventContactDao;

	@Autowired
	UserService userService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	ServletContext context;

	@Autowired
	AuctionRulesDao auctionRulesDao;

	@Autowired
	AuctionBidsDao auctionBidsDao;

	@Autowired
	RfaSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RfaSupplierBqItemDao supplierBqItemDao;

	@Autowired
	RfaSupplierBqDao rfaSupplierBqDao;;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	RfaEventMeetingDao rfaEventMeetingDao;

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Autowired
	RfaSupplierCqDao rfaSupplierCqDao;

	@Autowired
	RfaSupplierSorItemDao supplierSorItemDao;

	@Override
	@Transactional(readOnly = false)
	public RfaEventSupplier saveRfaEventSuppliers(RfaEventSupplier rfaEventSupplier) {
		rfaEventSupplier = rfaEventSupplierDao.saveOrUpdate(rfaEventSupplier);
		return rfaEventSupplier;
	}

	// @Override
	// @Transactional(readOnly = false)
	// public String saveRfaEventSuppliers(RfaEventSupplier rfaEventSupplier) {
	// rfaEventSupplierDao.saveOrUpdate(rfaEventSupplier);
	// return (rfaEventSupplier != null ? rfaEventSupplier.getId() : null);
	// }

	@Override
	public boolean isExists(RfaEventSupplier rfaEventSupplier) {
		return rfaEventSupplierDao.isExists(rfaEventSupplier, rfaEventSupplier.getRfxEvent().getId());
	}

	@Override
	public List<EventSupplier> getAllSuppliersByEventId(String eventID) {
		return rfaEventSupplierDao.getAllSuppliersByEventId(eventID);
	}

	@Override
	public List<EventSupplier> getSubmittedSuppliersByEventId(String eventID) {
		return rfaEventSupplierDao.getSubmittedSuppliersByEventId(eventID);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfaEventSuppliers(RfaEventSupplier rfaEventSupplier) {
		rfaSupplierBqDao.discardSupplierBqforSupplierId(rfaEventSupplier.getRfxEvent().getId(), rfaEventSupplier.getSupplier().getId());
		rfaEventSupplierDao.delete(rfaEventSupplier);
	}

	@Override
	public RfaEventSupplier findSupplierById(String id) {
		return rfaEventSupplierDao.getSupplierById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfaEventSuppliers(RfaEventSupplier rfaEventSupplier) {
		rfaEventSupplierDao.update(rfaEventSupplier);
	}

	@Override
	public RfaEventSupplier getEventSupplierBySupplierAndEventId(String supplierId, String eventID) {
		return rfaEventSupplierDao.getEventSupplierBySupplierAndEventId(supplierId, eventID);
	}

	@Override
	public RfaEventSupplier findSupplierByIdAndEventId(String supplierId, String eventId) {
		return rfaEventSupplierDao.getEventSupplierBySupplierAndEventId(supplierId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfaSupplierTeamMember> addTeamMemberToList(String eventId, String userId, String supplierId, TeamMemberType memberType) {
		LOG.info("ServiceImpl........." + "addTeamMemberToList----TeamMember" + " eventId: " + eventId + " userId: " + userId + " TeamMember_Type: " + memberType);
		RfaEvent rfaEvent = getRfaEventByeventId(eventId);
		RfaEventSupplier rfaEventSupplier = findSupplierByIdAndEventId(supplierId, eventId);
		LOG.info("RfaEventSupplier *****:" + rfaEventSupplier.getSupplierCompanyName());
		List<RfaSupplierTeamMember> teamMembers = rfaEventSupplierDao.getRfaSupplierTeamMembersForEvent(eventId, supplierId);
		if (teamMembers == null) {
			teamMembers = new ArrayList<RfaSupplierTeamMember>();
		}
		LOG.info("teamMembers : *******" + teamMembers.size());
		RfaSupplierTeamMember rfaTeamMember = new RfaSupplierTeamMember();
		rfaTeamMember.setEventSupplier(rfaEventSupplier);
		rfaTeamMember.setEvent(rfaEvent);
		User user = new User();
		user.setId(userId);
		// User user = userService.getUsersById(userId);
		rfaTeamMember.setUser(user);

		boolean exists = false;
		for (RfaSupplierTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(user.getId())) {
				rfaTeamMember = member;
				exists = true;
				break;
			}
		}
		rfaTeamMember.setTeamMemberType(memberType);

		LOG.info("rfaTeamMember : " + rfaTeamMember.toLogString());

		if (!exists) {
			teamMembers.add(rfaTeamMember);
		}

		rfaEventSupplier.setTeamMembers(teamMembers);
		rfaEventSupplierDao.update(rfaEventSupplier);
		LOG.info("rfaEventSupplier.getTeamMembers().size() :" + rfaEventSupplier.getTeamMembers().size());
		return teamMembers;
	}

	@Override
	public RfaEvent getRfaEventByeventId(String eventId) {
		return rfaEventSupplierDao.findByEventId(eventId);
	}

	@Override
	public List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId) {
		return rfaEventSupplierDao.getSupplierTeamMembersForEvent(eventId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeTeamMemberfromList(String eventId, String userId, String supplierId) {
		LOG.info("ServiceImpl........." + "removeTeamMemberfromList----TeamMember" + " eventId: " + eventId + " userId: " + userId + " supplierId: " + supplierId);
		RfaEventSupplier rfaEventSupplier = findSupplierByIdAndEventId(supplierId, eventId);
		rfaSupplierTeamMemberDao.deleteSupplierTeamMemberBySupplierIdForEvent(rfaEventSupplier.getId(), userId);
	}

	@Override
	public List<RfaSupplierTeamMember> getRfaSupplierTeamMembersForEvent(String eventId, String supplierId) {
		return rfaEventSupplierDao.getRfaSupplierTeamMembersForEvent(eventId, supplierId);
	}

	@Override
	public RfaEventSupplier findSupplierBySupplierId(String id) {
		return rfaEventSupplierDao.getSupplierBySupplierId(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void addSupplierForPublicEvent(String eventId, String supplierId) {
		LOG.info("supplierId : " + supplierId + " eventId: " + eventId);
		RfaEvent rfaEvent = getRfaEventByeventId(eventId);
		List<RfaEventSupplier> rfaEventSupplier = rfaEvent.getSuppliers();
		if (rfaEventSupplier == null) {
			rfaEventSupplier = new ArrayList<RfaEventSupplier>();
		}
		boolean exists = false;
		for (RfaEventSupplier eventSupplier : rfaEventSupplier) {
			if (eventSupplier.getSupplier().getId().equals(supplierId)) {
				exists = true;
				break;
			}
		}

		if (!exists) {
			RfaEventSupplier rfaSupplier = new RfaEventSupplier();
			Supplier supplier = new Supplier();
			supplier.setId(supplierId);
			rfaSupplier.setSupplier(supplier);
			rfaSupplier.setRfxEvent(rfaEvent);
			rfaSupplier.setSupplierInvitedTime(new Date());
			rfaSupplier.setSelfInvited(true);
			rfaEventSupplierDao.saveOrUpdate(rfaSupplier);

			/**
			 * Add site visit meeting for self invited supplier
			 */
			RfaEventMeeting siteMeeting = rfaEventMeetingDao.findMinMandatorySiteVisitMeetingsByEventId(eventId);
			if (siteMeeting != null) {
				List<Supplier> suppList = siteMeeting.getInviteSuppliers();
				if (suppList == null) {
					suppList = new ArrayList<Supplier>();
				}
				suppList.add(supplier);
				siteMeeting.setInviteSuppliers(suppList);
				rfaEventMeetingDao.saveOrUpdate(siteMeeting);
			}
		}
		LOG.info("supplierId : " + supplierId + " eventId: " + eventId + "RftEventSupplier Updated... and Supplier added.");

	}

	@Override
	public List<String> getAllRfaEventSuppliersIdByEventId(String eventId) {
		return rfaEventSupplierDao.getAllRfaEventSuppliersIdByEventId(eventId);
	}

	@Override
	public List<RfaEventSupplier> getAllRfaEventSuppliersByEventId(String eventId) {
		return rfaEventSupplierDao.getAllRfaEventSuppliersByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId) {
		return rfaEventSupplierDao.getUserPemissionsForEvent(userId, supplierId, eventId);
	}

	@Override
	public List<RfaEventSupplier> getAllRfaEventSuppliersListByEventId(String eventId) {
		return rfaEventSupplierDao.getAllRfaEventSuppliersListByEventId(eventId);
	}

	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId) {
		return rfaEventSupplierDao.getEventSuppliersForEvaluation(eventId);
	}

	@Override
	public List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId) {
		return rfaEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);
	}

	@Override
	public Integer getNumberOfBidsBySupplier(String supplierId, String eventId) {
		return rfaEventSupplierDao.getNumberOfBidsBySupplier(supplierId, eventId);
	}

	@Override
	public RfaEventSupplier findEventSupplierByEventIdAndSupplierId(String eventId, String supplierId) {
		return rfaEventSupplierDao.findEventSupplierByEventIdAndSupplierId(eventId, supplierId);
	}

	@Override
	public RfaEventSupplier findEventSupplierByEventIdAndSupplierIgnoreSubmitStatus(String eventId, String supplierId) {
		return rfaEventSupplierDao.findEventSupplierByEventIdAndSupplierIgnoreSubmit(eventId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public int updatePrivewTime(String eventId, String supplierId) {
		return rfaEventSupplierDao.updatePrivewTime(eventId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<Supplier> getRfaEventSupplierForAuctionConsole(String eventId) {
		return rfaEventSupplierDao.getRfaEventSupplierForAuctionConsole(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateAuctionOnlineDateTime(String eventId, String supplierId) {
		// LOG.info("Update the auction login date and time : For Supplier Id:"+supplierId);
		rfaEventSupplierDao.updateAuctionOnlineDateTime(eventId, supplierId);
	}

	@Override
	public List<EventSupplier> getAllSuppliersByEventIdOrderByCompName(String eventId) {
		return rfaEventSupplierDao.getAllSuppliersByEventIdOrderByCompName(eventId);
	}

	@Override
	public boolean checkAnySupplierSubmited(String eventId) {
		return rfaEventSupplierDao.checkAnySupplierSubmited(eventId);
	}

	@Override
	public Integer getCountOfSupplierByEventId(String eventId) {
		return rfaEventSupplierDao.getCountOfSupplierByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllSuppliersByEventId(String eventId) {
		rfaEventSupplierDao.deleteAllSuppliersByEventId(eventId);
		// List<EventSupplier> supplierList = rfaEventSupplierDao.getAllSuppliersByEventId(eventId);
		// for (EventSupplier eventSupplier : supplierList) {
		// rfaEventSupplierDao.delete((RfaEventSupplier) eventSupplier);
		// }
	}

	@Override
	public JasperPrint generateSupplierSummary(RfaEvent event, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer) {
		event = rfaEventService.getPlainEventById(event.getId());
		List<EvaluationPojo> summary = new ArrayList<EvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperPrint jasperPrint = null;

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

		try {
			Resource resource = applicationContext.getResource("classpath:reports/GenerateSupplierSummaryReport.jasper");
			String imgPath = context.getRealPath("resources/images");
			File jasperfile = resource.getFile();

			EvaluationPojo eventDetails = new EvaluationPojo();
			eventDetails.setReferenceId(event.getEventId());
			eventDetails.setReferenceNo(event.getReferanceNumber());
			eventDetails.setEventName(event.getEventName());
			String owner = "";
			if (event.getEventOwner() != null) {
				owner += event.getEventOwner().getName() + "\r\n" + event.getEventOwner().getCommunicationEmail() + "\r\n" + StringUtils.checkString(event.getEventOwner().getPhoneNumber());
			}
			eventDetails.setOwner(owner);
			eventDetails.setEventStart(sdf.format(event.getEventStart()));
			eventDetails.setEmail(event.getEventOwner().getCommunicationEmail());
			eventDetails.setType("RFA");
			eventDetails.setTeanantType("Supplier");
			eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");

			List<EvaluationPojo> eventStartRemider = new ArrayList<EvaluationPojo>();
			if (CollectionUtil.isNotEmpty(event.getRfaEndReminder())) {
				for (RfaReminder item : event.getRfaEndReminder()) {
					EvaluationPojo startRemider = new EvaluationPojo();
					startRemider.setEventStart(item.getReminderDate() != null ? sdf.format(item.getReminderDate()) : "");
					eventStartRemider.add(startRemider);
				}
			}
			eventDetails.setReminderDate(eventStartRemider);

			List<EvaluationPojo> eventRemider = new ArrayList<EvaluationPojo>();
			if (CollectionUtil.isNotEmpty(event.getRfaEndReminder())) {
				for (RfaReminder item : event.getRfaEndReminder()) {
					EvaluationPojo remider = new EvaluationPojo();
					remider.setEventEnd(item.getReminderDate() != null ? sdf.format(item.getReminderDate()) : "");
					eventRemider.add(remider);
				}
			}
			eventDetails.setReminderDate(eventRemider);

			eventDetails.setVisibility(event.getEventVisibility().name());

			eventDetails.setPublishDate(sdf.format(event.getEventPublishDate()));
			eventDetails.setValidityDays(event.getSubmissionValidityDays());
			String participationFeeCurrency = event.getParticipationFeeCurrency() != null ? event.getParticipationFeeCurrency().getCurrencyCode() : "";
			eventDetails.setParticipationFeeAndCurrency(participationFeeCurrency + " " + (event.getParticipationFees() != null ? formatedDecimalNumber("2", event.getParticipationFees()) : "-"));
			String depositCurrency = event.getDepositCurrency() != null ? event.getDepositCurrency().getCurrencyCode() : "";
			eventDetails.setDepositAndCurrency(depositCurrency + " " + (event.getDeposit() != null ? formatedDecimalNumber("2", event.getDeposit()) : "-"));

			// Solving issue PH-2916
			List<IndustryCategoryPojo> industryCategories = new ArrayList<IndustryCategoryPojo>();
			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				LOG.info(" Industry Categories " + event.getIndustryCategories().size());
				for (IndustryCategory category : event.getIndustryCategories()) {
					LOG.info(" 1 ");
					IndustryCategoryPojo ic = new IndustryCategoryPojo();
					ic.setName(category.getName());
					industryCategories.add(ic);
				}
			}
			eventDetails.setCategory(industryCategories);

			// Time Extension
			String extensionDuration = "", autoDisqualify = "", extensionTrigger = "";
			eventDetails.setAuctionType(event.getAuctionType() != null ? event.getAuctionType().getValue() : "");
			eventDetails.setExtentionType(event.getTimeExtensionType() != null ? event.getTimeExtensionType().name() : "");
			if (event.getTimeExtensionType() != null && event.getTimeExtensionType() == TimeExtensionType.AUTOMATIC) {
				extensionDuration = event.getTimeExtensionDuration() + "-" + event.getTimeExtensionDurationType().name();
				if (event.getAutoDisqualify() == Boolean.TRUE) {
					autoDisqualify = "Yes";
				} else {
					autoDisqualify = "No";
				}
				extensionTrigger = event.getTimeExtensionLeadingBidValue() + "-" + event.getTimeExtensionLeadingBidType().name();
				eventDetails.setExtensionDuration(extensionDuration);
				eventDetails.setExtensionRound(event.getExtensionCount());
				eventDetails.setExtensionTrigger(extensionTrigger);
				eventDetails.setAutoDisqualify(autoDisqualify);

			}
			// Correspond Address
			String correspondAddress = "";
			correspondAddress += event.getEventOwner().getBuyer().getLine1() + ", \r\n";
			if (event.getEventOwner().getBuyer().getLine2() != null) {
				correspondAddress += event.getEventOwner().getBuyer().getLine2() + ", \r\n";
			}
			correspondAddress += event.getEventOwner().getBuyer().getCity() + ", \r\n";
			if (event.getEventOwner().getBuyer().getState() != null) {
				correspondAddress += event.getEventOwner().getBuyer().getState().getStateName() + ", \r\n";
				correspondAddress += event.getEventOwner().getBuyer().getState().getCountry().getCountryName() + "\r\n";
			}
			eventDetails.setCorrespondAddress(correspondAddress);

			// Delivery Address
			String deliveryAddress = "";
			if (event.getDeliveryAddress() != null) {
				deliveryAddress += event.getDeliveryAddress().getLine1() + ", \r\n";
				if (event.getDeliveryAddress().getLine2() != null) {
					deliveryAddress += event.getDeliveryAddress().getLine2() + ", \r\n";
				}
				deliveryAddress += event.getDeliveryAddress().getCity() + ", \r\n";
				if (event.getDeliveryAddress().getState() != null) {
					deliveryAddress += event.getDeliveryAddress().getState().getStateName() + ", \r\n";
					deliveryAddress += event.getDeliveryAddress().getState().getCountry().getCountryName() + "\r\n";
				}
			}
			eventDetails.setDeliveryAddress(deliveryAddress);
			// Event Contact Details.
			List<RfaEventContact> eventContacts = rfaEventContactDao.findAllEventContactById(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RfaEventContact contact : eventContacts) {
					EvaluationContactsPojo contactPojo = new EvaluationContactsPojo();
					contactPojo.setTitle(contact.getTitle());
					contactPojo.setContactName(contact.getContactName());
					contactPojo.setDesignation(contact.getDesignation());
					contactPojo.setContactNumber(contact.getContactNumber());
					contactPojo.setMobileNumber(contact.getMobileNumber());
					contactPojo.setComunicationEmail(contact.getComunicationEmail());
					contactList.add(contactPojo);
				}
			}
			eventDetails.setContacts(contactList);

			// Commercial Information.

			eventDetails.setBaseCurrency(event.getBaseCurrency() != null ? (event.getBaseCurrency().getCurrencyCode() + " - " + event.getBaseCurrency().getCurrencyName()) : "");
			eventDetails.setPaymentTerm(event.getPaymentTerm());

			eventDetails.setBusinesUnit(event.getBusinessUnit() != null ? event.getBusinessUnit().getDisplayName() : "");
			// eventDetails.setCostCenter(event.getCostCenter() != null ? event.getCostCenter().getCostCenter() : "");
			// eventDetails.setHistoricAmt(event.getHistoricaAmount());
			// eventDetails.setBudgetAmt(event.getBudgetAmount());

			eventDetails.setCostCenter(null);
			eventDetails.setHistoricAmt(null);
			eventDetails.setBudgetAmt(null);

			eventDetails.setDecimal(event.getDecimal());
			eventDetails.setDescription(event.getEventDescription());

			// Meeting Details.
			List<EvaluationMeetingPojo> meetings = summaryMeetingDetails(event, imgPath, sdf, supplierId);
			eventDetails.setMeetings(meetings);

			// Questionnaire
			List<EvaluationCqPojo> supplierCqList = new ArrayList<EvaluationCqPojo>();
			List<RfaSupplierCqItem> supplierCqItem = supplierCqItemDao.getSupplierCqItemsbySupplierIdAndEventId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			Map<RfaCq, List<RfaSupplierCqItem>> cqList = new LinkedHashMap<RfaCq, List<RfaSupplierCqItem>>();
			for (RfaSupplierCqItem item : supplierCqItem) {
				List<RfaSupplierCqItem> itemList = cqList.get(item.getCq());
				if (itemList == null) {
					itemList = new ArrayList<RfaSupplierCqItem>();
					itemList.add(item);
					cqList.put(item.getCq(), itemList);
				} else {
					itemList.add(item);
				}
			}
			for (Map.Entry<RfaCq, List<RfaSupplierCqItem>> entry : cqList.entrySet()) {
				EvaluationCqPojo cqDetails = new EvaluationCqPojo();
				cqDetails.setName(entry.getKey().getName());
				cqDetails.setDescription(entry.getKey().getDescription());
				List<EvaluationCqItemPojo> cqItemList = new ArrayList<>();

				RfaSupplierCq cqStatus = rfaSupplierCqDao.findCqByEventIdAndCqId(event.getId(), entry.getKey().getId(), supplierId);

				for (RfaSupplierCqItem cqItem : entry.getValue()) {
					String answer = "";
					EvaluationCqItemPojo suppCqItem = new EvaluationCqItemPojo();
					String level = "";
					if (cqItem.getCqItem().getOptional() == Boolean.TRUE) {
						level = "*";
					}

					if (cqItem.getCqItem().getCqType() == CqType.CHOICE || cqItem.getCqItem().getCqType() == CqType.LIST || cqItem.getCqItem().getCqType() == CqType.CHOICE_WITH_SCORE || cqItem.getCqItem().getCqType() == CqType.CHECKBOX) {

						List<String> selectedOptions = new ArrayList<>();
						for (RfaSupplierCqOption supCqOpt : cqItem.getListAnswers()) {
							if (!selectedOptions.contains(supCqOpt.getValue())) {
								selectedOptions.add(supCqOpt.getValue());
							}
						}

						for (RfaCqOption cqOption : cqItem.getCqItem().getCqOptions()) {
							if (cqStatus != null && SupplierCqStatus.DRAFT == cqStatus.getSupplierCqStatus()) {
								answer += "  " + cqOption.getValue() + "\n";
							} else {
								if (selectedOptions.contains(cqOption.getValue())) {
									answer += "\u2022 " + cqOption.getValue() + "\n";
								} else {
									answer += "  " + cqOption.getValue() + "\n";
								}
							}

						}
					} else if (cqItem.getCqItem().getCqType() == CqType.TEXT || cqItem.getCqItem().getCqType() == CqType.NUMBER || cqItem.getCqItem().getCqType() == CqType.PARAGRAPH) {
						if (cqStatus != null && SupplierCqStatus.DRAFT == cqStatus.getSupplierCqStatus()) {
							answer += "Ans: \n";
						} else {
							if (cqItem.getTextAnswers() == null) {
								answer += "Ans: \n";
							} else {
								answer += "Ans: " + cqItem.getTextAnswers() + "\n";
							}
						}
					} else if (cqItem.getCqItem().getCqType() == CqType.DATE) {
						if (cqStatus != null && SupplierCqStatus.DRAFT == cqStatus.getSupplierCqStatus()) {
							answer += "Date: \n";
						} else {
							if (cqItem.getTextAnswers() == null) {
								answer += "Date: \n";
							} else {
								answer += "Date: " + cqItem.getTextAnswers() + "\n";
							}
						}
					}
					suppCqItem.setLevel(level + " " + cqItem.getCqItem().getLevel() + "." + cqItem.getCqItem().getOrder());
					suppCqItem.setItemName(cqItem.getCqItem().getItemName());
					suppCqItem.setItemDescription(cqItem.getCqItem().getItemDescription());

					suppCqItem.setOptionType(cqItem.getCqItem().getCqType() != null ? cqItem.getCqItem().getCqType().getValue() : "");

					suppCqItem.setAnswer(answer);
					cqItemList.add(suppCqItem);
				}
				cqDetails.setCqItem(cqItemList);
				supplierCqList.add(cqDetails);
			}

			eventDetails.setCqs(supplierCqList);

			// Bill of Quantity
			List<EvaluationBqPojo> supplierBqList = new ArrayList<EvaluationBqPojo>();
			List<RfaSupplierBq> supplierBqItem = supplierBqItemDao.getAllBqsBySupplierId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (CollectionUtil.isNotEmpty(supplierBqItem)) {
				DecimalFormat numFormat = new DecimalFormat(event.getDecimal().equals("1") ? "#,###,###,##0.0" : event.getDecimal().equals("2") ? "#,###,###,##0.00" : event.getDecimal().equals("3") ? "#,###,###,##0.000" : event.getDecimal().equals("4") ? "#,###,###,##0.0000" : event.getDecimal().equals("5") ? "#,###,###,##0.00000" : event.getDecimal().equals("6") ? "#,###,###,##0.000000" : "#,###,###,##0.00");

				for (RfaSupplierBq item : supplierBqItem) {
					EvaluationBqPojo bqList = new EvaluationBqPojo();

					bqList.setName(item.getName());
					bqList.setTitle(item.getName());
					bqList.setRemark(StringUtils.checkString(item.getRemark()));
					List<EvaluationBqItemPojo> bqItemList = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getSupplierBqItems())) {
						for (RfaSupplierBqItem bqItem : item.getSupplierBqItems()) {
							EvaluationBqItemPojo bqItems = new EvaluationBqItemPojo();

							bqItems.setDecimal(event.getDecimal());
							bqItems.setLevel(bqItem.getLevel() + "." + bqItem.getOrder());
							bqItems.setItemName(bqItem.getItemName());
							bqItems.setDescription(bqItem.getItemDescription());
							bqItems.setQuantity(bqItem.getQuantity());
							bqItems.setPriceType(bqItem.getPriceType() != null ? bqItem.getPriceType().getValue() : "");
							bqItems.setUnitPrice(bqItem.getUnitPrice());
							bqItems.setUom(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
							bqItems.setAmount(bqItem.getParent() != null ? bqItem.getTotalAmount() : null);
							bqItems.setTaxAmt(bqItem.getTax());
							bqItems.setTaxAmtS((bqItem.getTax() != null && bqItem.getParent() != null) ? numFormat.format(bqItem.getTax()).toString() : "");
							bqItems.setTotalAmt(bqItem.getTotalAmountWithTax());
							bqItems.setTotalAmtS((bqItem.getTotalAmountWithTax() != null && bqItem.getParent() != null) ? numFormat.format(bqItem.getTotalAmountWithTax()) : "");

							bqItemList.add(bqItems);

						}

						EvaluationBqItemPojo bqItems = new EvaluationBqItemPojo();
						AuctionRules auctionRules = new AuctionRules();

						auctionRules = auctionRulesDao.findAuctionRulesByEventId(event.getId());

						// bqItems.setTaxAmtS("Grand Total " + "(" + (event.getBaseCurrency() != null ?
						// event.getBaseCurrency().getCurrencyCode() : "") + ")" + " :");
						// bqItems.setTotalAmt(item.getTotalAfterTax());
						// bqItems.setTotalAmtS(item.getGrandTotal() != null ? numFormat.format(item.getGrandTotal()) :
						// "");
						boolean addiTax = false;

						boolean lumsumBiddingWithTax = auctionRules.getLumsumBiddingWithTax() != null ? auctionRules.getLumsumBiddingWithTax() : Boolean.FALSE;
						boolean itemizedBiddingWithTax = auctionRules.getItemizedBiddingWithTax() != null ? auctionRules.getItemizedBiddingWithTax() : Boolean.FALSE;
						if ((itemizedBiddingWithTax == true || lumsumBiddingWithTax == true) && !(event.getErpEnable() != null ? event.getErpEnable() : Boolean.FALSE)) {
							addiTax = true;
							bqItems.setGrandTotalString("Grand Total " + "(" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + ")" + " :");
							bqItems.setGrandTotalVal(item.getGrandTotal() != null ? numFormat.format(item.getGrandTotal()) : "");
							bqItems.setAddiTax(addiTax);
							bqItems.setAdditionalTaxBq(item.getAdditionalTax() != null ? numFormat.format(item.getAdditionalTax()) : numFormat.format(new BigDecimal(0)));
							bqItems.setAddiTaxS("Additional Tax :");
						}
						if ((itemizedBiddingWithTax == true || lumsumBiddingWithTax == true)) {
							bqItems.setTotalAfterTaxBqS("TotaLl After Tax :");
							bqItems.setTotalAfterTaxBq(item.getTotalAfterTax() != null ? numFormat.format(item.getTotalAfterTax()) : numFormat.format(new BigDecimal(0)));
						}

						if ((itemizedBiddingWithTax == false || lumsumBiddingWithTax == false)) {
							bqItems.setTotalAfterTaxBqS("Grand Total " + "(" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + ")" + " :");
							bqItems.setTotalAfterTaxBq(item.getTotalAfterTax() != null ? numFormat.format(item.getTotalAfterTax()) : numFormat.format(new BigDecimal(0)));
						}

						bqItemList.add(bqItems);
					}
					bqList.setBqItems(bqItemList);
					supplierBqList.add(bqList);

				}
			}
			eventDetails.setBqs(supplierBqList);

			// Schedule Of Rate
			List<EvaluationSorPojo> supplierSorList = new ArrayList<EvaluationSorPojo>();
			List<RfaSupplierSor> supplierSorItem = supplierSorItemDao.getAllSorsBySupplierId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (CollectionUtil.isNotEmpty(supplierSorItem)) {
				for (RfaSupplierSor item : supplierSorItem) {
					EvaluationSorPojo bqList = new EvaluationSorPojo();

					bqList.setName(item.getName());
					bqList.setTitle(item.getName());
					bqList.setRemark(StringUtils.checkString(item.getRemark()));
					List<EvaluationSorItemPojo> bqItemList = new ArrayList<EvaluationSorItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getSupplierSorItems())) {
						for (RfaSupplierSorItem bqItem : item.getSupplierSorItems()) {
							EvaluationSorItemPojo bqItems = new EvaluationSorItemPojo();

							bqItems.setDecimal(event.getDecimal());
							bqItems.setLevel(bqItem.getLevel() + "." + bqItem.getOrder());
							bqItems.setItemName(bqItem.getItemName());
							bqItems.setDescription(bqItem.getItemDescription());
							bqItems.setUom(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
							bqItems.setAmount(bqItem.getParent() != null ? bqItem.getTotalAmount() : null);
							bqItemList.add(bqItems);
						}
					}
					bqList.setBqItems(bqItemList);
					supplierSorList.add(bqList);

				}
			}
			eventDetails.setSors(supplierSorList);

			// RFA AuctionDetails.
			List<EvaluationAuctionRulePojo> auctionRuleList = auctionRuleDetails(event, sdf);
			eventDetails.setAuctionRules(auctionRuleList);

			summary.add(eventDetails);
			parameters.put("SUPPLIER_SUMMARY", summary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Error generating Supplier Summary Report : " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	private String formatedDecimalNumber(String decimalPoint, BigDecimal value) {
		String decimal = StringUtils.checkString(decimalPoint).length() > 0 ? decimalPoint : "1";
		if (value != null) {
			DecimalFormat df = new DecimalFormat(decimal.equals("1") ? "#,###,###,##0.0" : decimal.equals("2") ? "#,###,###,##0.00" : decimal.equals("3") ? "#,###,###,##0.000" : decimal.equals("4") ? "#,###,###,##0.0000" : decimal.equals("5") ? "#,###,###,##0.00000" : decimal.equals("6") ? "#,###,###,##0.000000" : "#,###,###,##0.00");
			return df.format(value);
		}
		return "";
	}

	@SuppressWarnings("deprecation")
	private List<EvaluationMeetingPojo> summaryMeetingDetails(RfaEvent event, String imgPath, SimpleDateFormat sdf, String supplierId) {
		List<RfaEventMeeting> meetingList = event.getMeetings();
		List<EvaluationMeetingPojo> meetings = new ArrayList<EvaluationMeetingPojo>();
		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RfaEventMeeting meeting : meetingList) {
				if(CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
					if(meeting.getInviteSuppliers().stream().filter(supplier -> supplier.getId().equals(supplierId)).count() == 0) {
						continue;
					}
				}
				EvaluationMeetingPojo em = new EvaluationMeetingPojo();
				em.setAppointmentDateTime(new Date(sdf.format(meeting.getAppointmentDateTime())));
				em.setRemarks(meeting.getRemarks());
				em.setStatus(meeting.getStatus().toString());
				em.setVenue(meeting.getVenue());
				em.setTitle(meeting.getTitle());
				em.setMandatoryMeeting(meeting.getMeetingAttendMandatory() ? "Yes" : "No");
				em.setResponse(meeting.getSupplierAttendance() != null ? meeting.getSupplierAttendance().getMeetingAttendanceStatus().name() : "");
				// Fetch Contact Details -Start
				List<EvaluationMeetingContactsPojo> contacts = new ArrayList<EvaluationMeetingContactsPojo>();
				if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
					for (RfaEventMeetingContact mc : meeting.getRfxEventMeetingContacts()) {
						EvaluationMeetingContactsPojo contact = new EvaluationMeetingContactsPojo();
						contact.setContactEmail(mc.getContactEmail());
						contact.setContactName(mc.getContactName());
						contact.setContactNumber(mc.getContactNumber());
						contact.setImagePath(imgPath);
						contacts.add(contact);
					}
				}
				em.setMeetingContacts(contacts);

				// Documents
				List<EvaluationDocumentPojo> meetingDocs = new ArrayList<EvaluationDocumentPojo>();
				if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
					for (RfaEventMeetingDocument docs : meeting.getRfxEventMeetingDocument()) {
						EvaluationDocumentPojo item = new EvaluationDocumentPojo();
						item.setFileName(docs.getFileName());
						item.setSize((double) ((docs.getFileData().length) / 1024));
						meetingDocs.add(item);
					}
				}
				em.setMeetingDocuments(meetingDocs);
				meetings.add(em);
			}
		}
		return meetings;
	}

	private List<EvaluationAuctionRulePojo> auctionRuleDetails(RfaEvent event, SimpleDateFormat sdf) {
		List<EvaluationAuctionRulePojo> auctionRule = new ArrayList<EvaluationAuctionRulePojo>();
		String supplierValue = "", biddingType = "", ownPrevious = "", leadBid = "", startGate = "", bidMinValue = "", heigherLeadBid = "", prebid = "";
		DecimalFormat df = null;
		if (event.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (event.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (event.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (event.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (event.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (event.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		}
		try {
			AuctionRules rules = auctionRulesDao.findAuctionRulesByEventId(event.getId());
			if (rules != null) {
				LOG.info("Auction Rules: " + rules.getEvent().getEventName());
				EvaluationAuctionRulePojo ar = new EvaluationAuctionRulePojo();
				ar.setEventName(rules.getEvent().getEventName());
				ar.setAuctionType(rules.getAuctionType().getValue());
				ar.setDecimal(event.getDecimal());
				if (rules.getAuctionType() == AuctionType.FORWARD_DUTCH || rules.getAuctionType() == AuctionType.REVERSE_DUTCH) {
					ar.setDutchStartPrice(rules.getDutchStartPrice());
					ar.setFowardAuction(rules.getFowardAuction());
					ar.setMinPrice(rules.getDutchMinimumPrice());
					ar.setAmountPerIncrementDecrement(rules.getAmountPerIncrementDecrement());
					ar.setInterval(rules.getInterval());
					ar.setIntervalType(rules.getIntervalType().name());
				}
				if (rules.getAuctionType() == AuctionType.FORWARD_ENGISH || rules.getAuctionType() == AuctionType.REVERSE_ENGISH || rules.getAuctionType() == AuctionType.FORWARD_SEALED_BID || rules.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
					ar.setPrebidByTitle("Initial Price key-in by");
					ar.setSupplierAuctionSetting("Auction Console Settings :");
					ar.setPreBidBy(rules.getPreBidBy().name());
					ar.setFowardAuction(rules.getFowardAuction());

					if (rules.getPreBidBy() != null && rules.getPreBidBy() == PreBidByType.BUYER) {
						if (rules.getFowardAuction() != null && rules.getIsPreBidHigherPrice() != null) {
							if (rules.getFowardAuction() == Boolean.TRUE) {
								supplierValue = "Supplier must provide Higher price or same price";
							} else if (rules.getFowardAuction() == Boolean.FALSE) {
								supplierValue = "Supplier must provide Lower price or same price";
							}
						}
					}

					ar.setSupplierMustProvide(supplierValue);
					ar.setPreBidSameBidPriceValue("Allow supplier to have same pre bid price");

					if (rules.getItemizedBiddingWithTax() != null) {
						if (rules.getItemizedBiddingWithTax() == Boolean.TRUE) {
							biddingType = "Itemized Bidding with tax";
						} else {
							biddingType = "Itemized Bidding without tax";
						}
					}
					if (rules.getLumsumBiddingWithTax() != null) {
						if (rules.getLumsumBiddingWithTax() == Boolean.TRUE) {
							biddingType = "Lumsum Bidding with tax";
						} else {
							biddingType = "Lumsum Bidding without tax";
						}
					}
					if (rules.getIsBiddingMinValueFromPrevious()) {
						if (rules.getFowardAuction()) {
							ownPrevious = "Minimum increment from own previous";
						} else {
							ownPrevious = "Minimum decrement from own previous";
						}
					}
					if (rules.getIsBiddingPriceHigherLeadingBid()) {
						if (rules.getFowardAuction()) {
							leadBid = "Price must be higher than leading bid";
						} else {
							leadBid = "Price must be lower than leading bid";
						}
					}

					ar.setBiddingType(biddingType);
					ar.setOwnPrevious(ownPrevious);
					ar.setLeadBid(leadBid);
					ar.setIsPreBidSameBidPrice(rules.getIsPreBidSameBidPrice());
					ar.setIsPreBidHigherPrice(rules.getIsPreBidHigherPrice());
					ar.setItemizedBiddingWithTax(rules.getItemizedBiddingWithTax());
					ar.setLumsumBiddingWithTax(rules.getLumsumBiddingWithTax());
					ar.setIsBiddingMinValueFromPrevious(rules.getIsBiddingMinValueFromPrevious());
					ar.setBiddingMinValueType(rules.getBiddingMinValueType().name());
					if (rules.getBiddingMinValueType() == ValueType.PERCENTAGE) {
						bidMinValue = df.format(rules.getBiddingMinValue()) + " %";
						if (rules.getBiddingPriceHigherLeadingBidValue() != null) {
							heigherLeadBid = df.format(rules.getBiddingPriceHigherLeadingBidValue());
						}
					} else {
						if (rules.getBiddingMinValue() != null) {
							bidMinValue = df.format(rules.getBiddingMinValue());
						}
						if (rules.getBiddingPriceHigherLeadingBidValue() != null) {
							heigherLeadBid = df.format(rules.getBiddingPriceHigherLeadingBidValue());
						}
					}
					ar.setBiddingMinValue(bidMinValue);
					if (rules.getIsStartGate() != null) {
						if (rules.getIsStartGate() == Boolean.TRUE) {
							startGate = "YES";
						} else {
							startGate = "NO";
						}
						ar.setIsStartGate(startGate);
					}
					ar.setIsBiddingPriceHigherLeadingBid(rules.getIsBiddingPriceHigherLeadingBid());
					ar.setBiddingPriceHigherLeadingBidType(rules.getBiddingPriceHigherLeadingBidType().name());
					ar.setBiddingPriceHigherLeadingBidValue(heigherLeadBid);
					ar.setIsBiddingAllowSupplierSameBid(rules.getIsBiddingAllowSupplierSameBid());
					ar.setAuctionConsolePriceType(rules.getAuctionConsolePriceType().getValue());
					ar.setAuctionConsoleVenderType(rules.getAuctionConsoleVenderType().getValue());
					ar.setAuctionConsoleRankType(rules.getAuctionConsoleRankType().getValue());
					if (rules.getPrebidAsFirstBid() != null) {
						if (rules.getPrebidAsFirstBid() == Boolean.TRUE) {
							prebid = "YES";
						} else {
							prebid = "NO";
						}
						ar.setPrebidAsFirstBid(prebid);
					}
				}
				auctionRule.add(ar);
			}
		} catch (Exception e) {
			LOG.error("Could not Auction Rules Values " + e.getMessage(), e);
		}
		return auctionRule;
	}

	@Override
	@Transactional(readOnly = false)
	public Integer updateSupplierAuctionRank(String eventId, boolean isForwordAuction, String supplierId) {
		return rfaEventSupplierDao.updateSupplierAuctionRank(eventId, isForwordAuction, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfaEventSupplier> getSupplierListForBidderDisqualify(String eventId, String supplierId) {
		return rfaEventSupplierDao.getSupplierListForBidderDisqualify(eventId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventSupplierDisqualify(String eventId, String supplierId, User disqualifiedBy) {
		rfaEventSupplierDao.updateEventSupplierDisqualify(eventId, supplierId, disqualifiedBy, "Auto disqualify due to auction rules");

	}

	@Override
	public JasperPrint getSupplierAuctionReport(String eventId, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer) {
		JasperPrint jasperPrint = null;
		List<EvaluationAuctionPojo> auctionSummary = new ArrayList<EvaluationAuctionPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/SupplierAuctionReport.jasper");
			// String imgPath = context.getRealPath("resources/images");
			File jasperfile = resource.getFile();
			RfaEvent event = rfaEventSupplierDao.findByEventId(eventId);
			int supplierCount = 0, submittedCnt = 0, totalBids = 0;

			List<AuctionBids> supplierBids = auctionBidsDao.getAuctionBidsForSupplier(supplierId, eventId);
			if (CollectionUtil.isNotEmpty(supplierBids)) {
				totalBids = supplierBids.size();
			}

			if (event != null) {
				EvaluationAuctionPojo auction = new EvaluationAuctionPojo();
				String auctionDate = event.getEventStart() != null ? sdf.format(event.getEventStart()) : "" + "-" + event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "";
				String auctionTitle = "PRE & POST AUCTION PRICE (" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : " ") + ")";
				String netSavingTitle = "Saving based on Budged(%)";
				auction.setAuctionId(event.getEventId());
				auction.setReferenceNo(event.getReferanceNumber());
				auction.setAuctionName(event.getEventName());
				auction.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				auction.setAuctionType(event.getAuctionType().getValue());
				auction.setDateTime(auctionDate);
				auction.setAuctionTitle(auctionTitle);
				auction.setNetSavingTitle(netSavingTitle);
				auction.setIsBuyer(Boolean.FALSE);
				// Bid Details.
				auction.setBuyerName(event.getEventOwner().getBuyer().getCompanyName());
				auction.setAuctionPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
				auction.setAuctionStartDate(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
				auction.setAuctionEndDate(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
				auction.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
				auction.setAuctionExtension(event.getTimeExtensionType() != null ? event.getTimeExtensionType().name() : "");
				auction.setTotalExtension(event.getTotalExtensions());
				auction.setSupplierInvited(supplierCount);
				auction.setSupplierParticipated(submittedCnt);
				auction.setAuctionStatus(event.getStatus().name());
				auction.setTotalBilds(totalBids);
				auction.setDecimal(event.getDecimal());
				if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.REVERSE_DUTCH) {
					AuctionRules auctionRules = auctionRulesDao.findAuctionRulesByEventId(event.getId());

					auction.setAuctionCompletionDate(event.getAuctionComplitationTime() != null ? sdf.format(event.getAuctionComplitationTime()) : "");
					if (auctionRules != null) {
						auction.setStartPrice(auctionRules.getDutchStartPrice());
						auction.setDuctchPrice(auctionRules.getDutchMinimumPrice());
						auction.setIntervalType(auctionRules.getIntervalType().name());
						auction.setInterval(auctionRules.getInterval());

					}
					auction.setWinner(event.getWinningSupplier() != null ? event.getWinningSupplier().getCompanyName() : "");
					auction.setWinningPrice(event.getWinningPrice());
					auction.setWinningDate(event.getAuctionComplitationTime() != null ? sdf.format(event.getAuctionComplitationTime()) : "");
				}

				List<EvaluationSupplierBidsPojo> supplierBidHistory = new ArrayList<EvaluationSupplierBidsPojo>();

				// Supplier Bid Price Details

				List<RfaSupplierBq> bqList = rfaSupplierBqDao.findSupplierBqbyEventId(eventId);
				String bqId = null;
				if (CollectionUtil.isNotEmpty(bqList)) {
					for (RfaSupplierBq rfaSupplierEventBq : bqList) {
						bqId = rfaSupplierEventBq.getBq().getId();
					}
				}
				RfaSupplierBq supBq = rfaSupplierBqDao.findBqByBqId(bqId, supplierId);
				if (supBq != null) {
					EvaluationSupplierBidsPojo bidderPriceHistory = new EvaluationSupplierBidsPojo();
					bidderPriceHistory.setSupplierName(supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : "");
					bidderPriceHistory.setBqDescription(supBq.getName());
					bidderPriceHistory.setInitialPrice(supBq.getInitialPrice());
					bidderPriceHistory.setDecimals(event.getDecimal());
					bidderPriceHistory.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
					List<EvaluationBiddingPricePojo> BidderPriceList = new ArrayList<EvaluationBiddingPricePojo>();
					List<AuctionBids> supplierBidsList = auctionBidsDao.getAuctionBidsForSupplierForReport(supplierId, event.getId());
					if (CollectionUtil.isNotEmpty(supplierBidsList)) {
						BigDecimal reductionPrice = BigDecimal.ZERO, temp = BigDecimal.ZERO, percentage = BigDecimal.ZERO;
						temp = supBq.getInitialPrice();
						int bidNumber = 1;
						for (AuctionBids suppBids : supplierBidsList) {
							EvaluationBiddingPricePojo bidderPrice = new EvaluationBiddingPricePojo();
							if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
								reductionPrice = temp.subtract(suppBids.getAmount());
								percentage = (reductionPrice.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(supBq.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
							}
							if (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
								reductionPrice = suppBids.getAmount().subtract(temp);
								percentage = (reductionPrice.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(supBq.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
							}
							// temp = suppBids.getAmount();
							bidderPrice.setBidNumber(bidNumber);
							bidderPrice.setDecimal(event.getDecimal());
							bidderPrice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
							bidderPrice.setPriceSubmission(suppBids.getAmount());
							bidderPrice.setPriceReduction(reductionPrice);
							bidderPrice.setPercentage(percentage);
							bidderPrice.setBidderName(supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : "");
							bidderPrice.setSubmitedDate(suppBids.getBidSubmissionDate() != null ? sdf.format(suppBids.getBidSubmissionDate()) : "");
							BidderPriceList.add(bidderPrice);
							bidNumber += 1;
						}
					}
					bidderPriceHistory.setPriceSubmissionList(BidderPriceList);
					supplierBidHistory.add(bidderPriceHistory);
				}

				auction.setSupplierBidsList(supplierBidHistory);
				auctionSummary.add(auction);
			}
			parameters.put("AUCTION_SUMMARY", auctionSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auctionSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Supplier Auction PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	@Transactional(readOnly = false)
	public void saveRfaAuctionBid(AuctionBids bids) {
		auctionBidsDao.save(bids);
	}

	@Override
	@Transactional(readOnly = true)
	public Supplier findSupplierForId(String supplierId) {
		return supplierDao.findById(supplierId);
	}

	@Override
	public RfaEventSupplier getEventSupplierForAuctionBySupplierAndEventId(String supplierId, String eventID) {
		return rfaEventSupplierDao.getEventSupplierForAuctionBySupplierAndEventId(supplierId, eventID);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEventSupplier saveRfaEventSupplier(RfaEventSupplier rfaEventSupplier) {
		rfaEventSupplier = rfaEventSupplierDao.saveOrUpdate(rfaEventSupplier);
		if (rfaEventSupplier.getSupplier() != null) {
			rfaEventSupplier.getSupplier().getCompanyName();
			rfaEventSupplier.getSupplier().getId();
		}

		if (rfaEventSupplier.getRfxEvent() != null && rfaEventSupplier.getRfxEvent().getCreatedBy() != null && rfaEventSupplier.getRfxEvent().getCreatedBy().getBuyer() != null) {
			rfaEventSupplier.getRfxEvent().getCreatedBy().getBuyer().getCommunicationEmail();
			rfaEventSupplier.getRfxEvent().getTenantId();
			rfaEventSupplier.getRfxEvent().getCreatedBy().getTenantId();
		}

		if (rfaEventSupplier.getRfxEvent() != null && rfaEventSupplier.getRfxEvent().getParticipationFeeCurrency() != null) {
			rfaEventSupplier.getRfxEvent().getParticipationFeeCurrency().getCurrencyCode();
		}

		if (rfaEventSupplier.getRfxEvent() != null && rfaEventSupplier.getRfxEvent().getEventOwner() != null) {
			rfaEventSupplier.getRfxEvent().getEventOwner().getCommunicationEmail();
			rfaEventSupplier.getRfxEvent().getEventOwner().getTenantId();
		}

		if (rfaEventSupplier.getRfxEvent() != null && rfaEventSupplier.getRfxEvent().getTeamMembers() != null) {
			for (RfaTeamMember member : rfaEventSupplier.getRfxEvent().getTeamMembers()) {
				member.getUser().getCommunicationEmail();
				member.getUser().getTenantId();
			}
		}

		return rfaEventSupplier;
	}

	@Override
	public List<RfaEventSupplier> findSupplierByEventIdOnlyRank(String eventId) {
		return rfaEventSupplierDao.findSupplierByEventIdOnlyRank(eventId);
	}

	@Override
	public List<RfaEventSupplier> findDisqualifySupplierByEventId(String eventId) {
		return rfaEventSupplierDao.findDisqualifySupplierByEventId(eventId);
	}

	@Override
	public List<EventSupplier> getAllPartiallyCompleteBidsByEventId(String eventId) {
		return rfaEventSupplierDao.getAllPartiallyCompleteBidsByEventId(eventId);
	}

	@Override
	public List<FeePojo> getAllInvitedSuppliersByEventId(String eventId) {
		return rfaEventSupplierDao.getAllInvitedSuppliersByEventId(eventId);
	}

	@Override
	public List<RfaEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId) {
		return rfaEventSupplierDao.getAllSuppliersByFeeEventId(eventId, supplierId);
	}

	@Override
	public List<RfaEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId) {
		return rfaEventSupplierDao.getAllSuppliersByEventIdAndSupplierCode(eventId, supplierCode, tenantId);
	}

	@Override
	public List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input) {
		return rfaEventSupplierDao.getAllEventsSupplierPojoByEventId(eventId, input);
	}

	@Override
	public long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input) {
		return rfaEventSupplierDao.getAllEventsSupplierPojoCountByEventId(eventId, input);
	}

	@Override
	public List<EventSupplierPojo> getAllDetailsForSendInvitation(String eventId) {
		List<EventSupplierPojo> list = rfaEventSupplierDao.getAllDetailsForSendInvitation(eventId);
		String timeZone = "GMT+8:00";
		for (EventSupplierPojo eventSupplierPojo : list) {
			String supplierTimeZone = supplierSettingsDao.getSupplierTimeZoneByTenantId(eventSupplierPojo.getSupplierId());
			eventSupplierPojo.setTimeZone(StringUtils.checkString(supplierTimeZone).length() > 0 ? supplierTimeZone : timeZone);
		}
		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventSuppliersNotificationFlag(String supplierId) {
		rfaEventSupplierDao.updateEventSuppliersNotificationFlag(supplierId);

	}

	@Override
	@Transactional(readOnly = false)
	public EventSupplier saveInvitedSuppliers(RfaEventSupplier eventSupplier) {
		EventSupplier rfaEventSupplier = rfaEventSupplierDao.saveOrUpdate(eventSupplier);
		if (rfaEventSupplier.getSupplier() != null) {
			rfaEventSupplier.getSupplier().getCompanyName();
			rfaEventSupplier.getSupplier().setIndustryCategory(null);
			rfaEventSupplier.getSupplier().setProductCategory(null);
			rfaEventSupplier.getSupplier().setCreatedBy(null);
		}
		return rfaEventSupplier;
	}

	@Override
	public List<RfaEventSupplier> getAllSubmittedSupplierByEventId(String eventId) {
		return rfaEventSupplierDao.getAllSubmittedSupplierByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void batchInsert(List<RfaEventSupplier> eventSupplier) {
		rfaEventSupplierDao.batchInsert(eventSupplier);
	}

	@Override
	public RfaEventSupplier findEventSupplierByEventIdAndSupplierRevisedSubmission(String eventId, String supplierId) {
		return rfaEventSupplierDao.findEventSupplierByEventIdAndSupplierRevisedSubmission(eventId, supplierId);

	}

	@Override
	public String getEventNameByEventId(String eventId) {
		return rfaEventSupplierDao.getEventNameByEventId(eventId);
	}

	@Override
	public List<RfaEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId) {
		return rfaEventSupplierDao.findDisqualifySupplierForEvaluationReportByEventId(eventId);
	}
	
	@Override
	public List<RfaEventSupplier> findEventSuppliersForTatReportByEventId(String eventId) {
		return rfaEventSupplierDao.findEventSuppliersForTatReportByEventId(eventId);
	}

}