package com.privasia.procurehere.service.impl;

import java.io.File;
import java.math.BigDecimal;
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

import com.privasia.procurehere.core.dao.RftSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RfiSupplierSor;
import com.privasia.procurehere.core.entity.RfiSupplierSorItem;
import com.privasia.procurehere.core.entity.RftSupplierSor;
import com.privasia.procurehere.core.entity.RftSupplierSorItem;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.AuctionRulesDao;
import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftEventContactDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RftEventMeetingDao;
import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.RftSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RftSupplierCqDao;
import com.privasia.procurehere.core.dao.RftSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RftSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftCqOption;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventContact;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.entity.RftEventMeetingContact;
import com.privasia.procurehere.core.entity.RftEventMeetingDocument;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.RftReminder;
import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.entity.RftSupplierCq;
import com.privasia.procurehere.core.entity.RftSupplierCqItem;
import com.privasia.procurehere.core.entity.RftSupplierCqOption;
import com.privasia.procurehere.core.entity.RftSupplierTeamMember;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EvaluationBqItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationBqPojo;
import com.privasia.procurehere.core.pojo.EvaluationContactsPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqPojo;
import com.privasia.procurehere.core.pojo.EvaluationDocumentPojo;
import com.privasia.procurehere.core.pojo.EvaluationMeetingContactsPojo;
import com.privasia.procurehere.core.pojo.EvaluationMeetingPojo;
import com.privasia.procurehere.core.pojo.EvaluationPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.UserService;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

@Service
@Transactional(readOnly = true)
public class RftEventSupplierServiceImpl implements RftEventSupplierService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	RftEventSupplierDao rftEventSupplierDao;

	@Autowired
	RftSupplierTeamMemberDao rftSupplierTeamMemberDao;

	@Autowired
	UserService userService;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RftEventContactDao rftEventContactDao;

	@Autowired
	ServletContext context;

	@Autowired
	RftSupplierCqItemDao supplierCqItemDao;
	@Autowired
	RftSupplierBqItemDao supplierBqItemDao;

	@Autowired
	RftEventMeetingDao rftEventMeetingDao;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Autowired
	AuctionRulesDao auctionRulesDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfaEventDao rfaEventDao;
	
	@Autowired
	RftSupplierCqDao rftSupplierCqDao;

	@Autowired
	RftSupplierSorItemDao supplierSorItemDao;

	@Override
	@Transactional(readOnly = false)
	public String saveRftEventSuppliers(RftEventSupplier rftEventSupplier) {
		rftEventSupplier = rftEventSupplierDao.saveOrUpdate(rftEventSupplier);
		return (rftEventSupplier != null ? rftEventSupplier.getId() : null);
	}

	@Override
	public boolean isExists(RftEventSupplier rftEventSupplier) {
		return rftEventSupplierDao.isExists(rftEventSupplier, rftEventSupplier.getRfxEvent().getId());
	}

	@Override
	public List<EventSupplier> getAllSuppliersByEventId(String eventID) {
		return rftEventSupplierDao.getAllSuppliersByEventId(eventID);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRftEventSuppliers(RftEventSupplier rftEventSupplier) {
		rftEventSupplierDao.delete(rftEventSupplier);
	}

	@Override
	public RftEventSupplier findSupplierById(String id) {
		return rftEventSupplierDao.getSupplierById(id);
	}

	@Override
	public RftEventSupplier findSupplierBySupplierId(String id) {
		return rftEventSupplierDao.getSupplierBySupplierId(id);
	}

	@Override
	public RftEventSupplier findSupplierByIdAndEventId(String supplierId, String eventId) {
		return rftEventSupplierDao.getEventSupplierBySupplierAndEventId(supplierId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventSuppliers(RftEventSupplier eventSupplier) {
		rftEventSupplierDao.update(eventSupplier);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RftSupplierTeamMember> addTeamMemberToList(String eventId, String userId, String supplierId, TeamMemberType memberType) {
		LOG.info("ServiceImpl........." + "addTeamMemberToList----TeamMember" + " eventId: " + eventId + " userId: " + userId + " TeamMember_Type: " + memberType);
		RftEvent rftEvent = getRftEventByeventId(eventId);
		RftEventSupplier rftEventSupplier = findSupplierByIdAndEventId(supplierId, eventId);
		LOG.info("RftEventSupplier *****:" + rftEventSupplier.getSupplierCompanyName());
		List<RftSupplierTeamMember> teamMembers = rftEventSupplierDao.getRftSupplierTeamMembersForEvent(eventId, supplierId);
		if (teamMembers == null) {
			teamMembers = new ArrayList<RftSupplierTeamMember>();
		}

		RftSupplierTeamMember rftTeamMember = new RftSupplierTeamMember();
		rftTeamMember.setEventSupplier(rftEventSupplier);
		rftTeamMember.setEvent(rftEvent);
		User user = new User(); // userService.getUsersById(userId);
		user.setId(userId);
		rftTeamMember.setUser(user);

		boolean exists = false;
		for (RftSupplierTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(userId)) {
				rftTeamMember = member;
				exists = true;
				break;
			}
		}
		rftTeamMember.setTeamMemberType(memberType);

		if (!exists) {
			teamMembers.add(rftTeamMember);
		}

		rftEventSupplier.setTeamMembers(teamMembers);
		rftEventSupplierDao.update(rftEventSupplier);
		return teamMembers;
	}

	@Override
	@Transactional(readOnly = false)
	public void removeTeamMemberfromList(String eventId, String userId, String supplierId) {
		LOG.info("ServiceImpl........." + "removeTeamMemberfromList----TeamMember" + " eventId: " + eventId + " userId: " + userId + " supplierId: " + supplierId);
		RftEventSupplier rftEventSupplier = findSupplierByIdAndEventId(supplierId, eventId);
		rftSupplierTeamMemberDao.deleteSupplierTeamMemberBySupplierIdForEvent(rftEventSupplier.getId(), userId);
	}

	@Override
	public List<RftSupplierTeamMember> getRftSupplierTeamMembersForEvent(String eventId, String supplierId) {
		return rftEventSupplierDao.getRftSupplierTeamMembersForEvent(eventId, supplierId);
	}

	@Override
	public List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId) {
		return rftEventSupplierDao.getSupplierTeamMembersForEvent(eventId, supplierId);
	}

	@Override
	public RftEvent getRftEventByeventId(String eventId) {

		return rftEventSupplierDao.findByEventId(eventId);
	}

	@Override
	public RftSupplierTeamMember getRftTeamMemberByUserIdAndEventId(String eventId, String userId) {
		return rftEventSupplierDao.getRftTeamMemberByUserIdAndEventId(eventId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void addSupplierForPublicEvent(String eventId, String supplierId) {
		LOG.info("supplierId : " + supplierId + " eventId: " + eventId);
		RftEvent rftEvent = rftEventSupplierDao.findByEventId(eventId);
		List<RftEventSupplier> rftEventSupplier = rftEvent.getSuppliers();
		if (rftEventSupplier == null) {
			rftEventSupplier = new ArrayList<RftEventSupplier>();
		}

		boolean exists = false;
		for (RftEventSupplier eventSupplier : rftEventSupplier) {
			if (eventSupplier.getSupplier().getId().equals(supplierId)) {
				exists = true;
				break;
			}
		}

		if (!exists) {
			RftEventSupplier rftSupplier = new RftEventSupplier();
			Supplier supplier = new Supplier();
			supplier.setId(supplierId);
			rftSupplier.setSupplier(supplier);
			rftSupplier.setRfxEvent(rftEvent);
			rftSupplier.setSupplierInvitedTime(new Date());
			rftSupplier.setSelfInvited(true);
			rftEventSupplierDao.saveOrUpdate(rftSupplier);

			/**
			 * Add site visit meeting for self invited supplier
			 */
			RftEventMeeting siteMeeting = rftEventMeetingDao.findMinMandatorySiteVisitMeetingsByEventId(eventId);
			if (siteMeeting != null) {
				List<Supplier> suppList = siteMeeting.getInviteSuppliers();
				if (suppList == null) {
					suppList = new ArrayList<Supplier>();
				}
				suppList.add(supplier);
				siteMeeting.setInviteSuppliers(suppList);
				rftEventMeetingDao.saveOrUpdate(siteMeeting);
			}
			/*
			 * rftEventSupplier.add(rftSupplier); rftEvent.setSuppliers(rftEventSupplier); rftEventDao.update(rftEvent);
			 */
		}
		LOG.info("supplierId : " + supplierId + " eventId: " + eventId + "RftEventSupplier Updated... and Supplier added.");
	}

	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId) {
		return rftEventSupplierDao.getEventSuppliersForEvaluation(eventId);
	}

	@Override
	public List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId) {
		return rftEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId) {
		return rftEventSupplierDao.getUserPemissionsForEvent(userId, supplierId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public int updatePrivewTime(String eventId, String supplierId) {
		return rftEventSupplierDao.updatePrivewTime(eventId, supplierId);
	}

	@Override
	public List<EventSupplier> getAllSuppliersByEventIdOrderByCompName(String eventId) {
		return rftEventSupplierDao.getAllSuppliersByEventIdOrderByCompName(eventId);
	}

	@Override
	public Integer getCountOfSupplierByEventId(String eventId) {
		return rftEventSupplierDao.getCountOfSupplierByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllSuppliersByEventId(String eventId) {
		rftEventSupplierDao.deleteAllSuppliersByEventId(eventId);
		// List<EventSupplier> supplierList = rftEventSupplierDao.getAllSuppliersByEventId(eventId);
		// for (EventSupplier eventSupplier : supplierList) {
		// rftEventSupplierDao.delete((RftEventSupplier) eventSupplier);
		// }
	}

	@Override
	@Transactional(readOnly = true)
	public JasperPrint generateSupplierSummary(RftEvent event, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer) {
		event = rftEventDao.getPlainEventById(event.getId());
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

		try {
			// Virtualizar - To increase the performance
			parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

			Resource resource = applicationContext.getResource("classpath:reports/GenerateSupplierSummaryReport.jasper");
			String imgPath = context.getRealPath("resources/images");
			File jasperfile = resource.getFile();

			EvaluationPojo eventDetails = new EvaluationPojo();
			eventDetails.setReferenceId(event.getEventId());
			eventDetails.setReferenceNo(event.getReferanceNumber());
			eventDetails.setEventName(event.getEventName());
			String owner = "";
			if (event.getEventOwner() != null) {
				owner += event.getEventOwner().getName() + "\r\n" + event.getEventOwner().getCommunicationEmail() + "\r\n" + (event.getEventOwner().getPhoneNumber() != null ? event.getEventOwner().getPhoneNumber() : "");
			}
			eventDetails.setOwner(owner);
			eventDetails.setEventStart(sdf.format(event.getEventStart()));
			eventDetails.setEmail(event.getEventOwner().getCommunicationEmail());
			eventDetails.setType("RFT");
			eventDetails.setTeanantType("Supplier");
			eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");

			List<EvaluationPojo> eventRemider = new ArrayList<EvaluationPojo>();
			if (CollectionUtil.isNotEmpty(event.getRftReminder())) {
				for (RftReminder item : event.getRftReminder()) {
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
				deliveryAddress += event.getDeliveryAddress().getTitle() + "\r\n" + event.getDeliveryAddress().getLine1() + ", \r\n";
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
			List<RftEventContact> eventContacts = rftEventContactDao.findAllEventContactById(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RftEventContact contact : eventContacts) {
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
			List<RftSupplierCqItem> supplierCqItem = supplierCqItemDao.getSupplierCqItemsbySupplierIdAndEventId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			Map<RftCq, List<RftSupplierCqItem>> cqList = new LinkedHashMap<RftCq, List<RftSupplierCqItem>>();
			for (RftSupplierCqItem item : supplierCqItem) {
				LOG.info(">>> " + item.getId());
				List<RftSupplierCqItem> itemList = cqList.get(item.getCq());
				if (itemList == null) {
					itemList = new ArrayList<RftSupplierCqItem>();
					itemList.add(item);
					cqList.put(item.getCq(), itemList);
				} else {
					itemList.add(item);
				}
			}
			for (Map.Entry<RftCq, List<RftSupplierCqItem>> entry : cqList.entrySet()) {
				EvaluationCqPojo cqDetails = new EvaluationCqPojo();
				cqDetails.setName(entry.getKey().getName());
				cqDetails.setDescription(entry.getKey().getDescription());
				List<EvaluationCqItemPojo> cqItemList = new ArrayList<>();

				RftSupplierCq cqStatus = rftSupplierCqDao.findCqByEventIdAndEventCqId(event.getId(), entry.getKey().getId(), supplierId);
				for (RftSupplierCqItem cqItem : entry.getValue()) {
					String answer = "";
					EvaluationCqItemPojo suppCqItem = new EvaluationCqItemPojo();
					String level = "";
					if (cqItem.getCqItem().getOptional() == Boolean.TRUE) {
						level = "*";
					}

					if (cqItem.getCqItem().getCqType() == CqType.CHOICE || cqItem.getCqItem().getCqType() == CqType.LIST || cqItem.getCqItem().getCqType() == CqType.CHOICE_WITH_SCORE || cqItem.getCqItem().getCqType() == CqType.CHECKBOX) {

						List<String> selectedOptions = new ArrayList<>();
						for (RftSupplierCqOption supCqOpt : cqItem.getListAnswers()) {
							if (!selectedOptions.contains(supCqOpt.getValue())) {
								selectedOptions.add(supCqOpt.getValue());
							}
						}

						for (RftCqOption cqOption : cqItem.getCqItem().getCqOptions()) {
							if(cqStatus != null && SupplierCqStatus.DRAFT == cqStatus.getSupplierCqStatus()) {
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
						if(cqStatus != null && SupplierCqStatus.DRAFT == cqStatus.getSupplierCqStatus()) {
							answer += "Ans: \n";
						}else {
							if (cqItem.getTextAnswers() == null) {
								answer += "Ans: \n";
							} else {
								answer += "Ans: " + cqItem.getTextAnswers() + "\n";
							}
						}
					} else if (cqItem.getCqItem().getCqType() == CqType.DATE) {
						if(cqStatus != null && SupplierCqStatus.DRAFT == cqStatus.getSupplierCqStatus()) {
							answer += "Date: \n";
						}else {
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
			List<RftSupplierBq> supplierBqItem = supplierBqItemDao.getAllBqsBySupplierId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (CollectionUtil.isNotEmpty(supplierBqItem)) {
				DecimalFormat numFormat = new DecimalFormat(event.getDecimal().equals("1") ? "#,###,###,##0.0" : event.getDecimal().equals("2") ? "#,###,###,##0.00" : event.getDecimal().equals("3") ? "#,###,###,##0.000" : event.getDecimal().equals("4") ? "#,###,###,##0.0000" : event.getDecimal().equals("5") ? "#,###,###,##0.00000" : event.getDecimal().equals("6") ? "#,###,###,##0.000000" : "#,###,###,##0.00");
				for (RftSupplierBq item : supplierBqItem) {
					EvaluationBqPojo bqList = new EvaluationBqPojo();

					bqList.setName(item.getName());
					bqList.setTitle(item.getName());
					bqList.setRemark(StringUtils.checkString(item.getRemark()));
					List<EvaluationBqItemPojo> bqItemList = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getSupplierBqItems())) {
						for (RftSupplierBqItem bqItem : item.getSupplierBqItems()) {
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
						// bqItems.setTaxAmtS("Grand Total " + "(" + (event.getBaseCurrency() != null ?
						// event.getBaseCurrency().getCurrencyCode() : "") + ")" + " :");
						bqItems.setTotalAmt(item.getTotalAfterTax());
						// bqItems.setTotalAmtS(item.getGrandTotal() != null ? numFormat.format(item.getGrandTotal()) :
						// "");

						boolean addiTax = false;

						if (!(event.getErpEnable() != null ? event.getErpEnable() : Boolean.FALSE)) {
							addiTax = true;
							bqItems.setGrandTotalString("Grand Total " + "(" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + ")" + " :");
							bqItems.setGrandTotalVal(item.getGrandTotal() != null ? numFormat.format(item.getGrandTotal()) : "");
							bqItems.setAddiTax(addiTax);
							bqItems.setAdditionalTaxBq(item.getAdditionalTax() != null ? numFormat.format(item.getAdditionalTax()) : numFormat.format(new BigDecimal(0)));
							bqItems.setAddiTaxS("Additional Tax :");
						}

						if (!(event.getErpEnable() != null ? event.getErpEnable() : Boolean.FALSE)) {
							bqItems.setTotalAfterTaxBqS("Total After Tax :");
							bqItems.setTotalAfterTaxBq(item.getTotalAfterTax() != null ? numFormat.format(item.getTotalAfterTax()) : numFormat.format(new BigDecimal(0)));
						}
						if ((event.getErpEnable() != null ? event.getErpEnable() : Boolean.FALSE)) {
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
			List<RftSupplierSor> supplierSorItem = supplierSorItemDao.getAllSorsBySupplierId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (CollectionUtil.isNotEmpty(supplierSorItem)) {
				for (RftSupplierSor item : supplierSorItem) {
					EvaluationSorPojo bqList = new EvaluationSorPojo();

					bqList.setName(item.getName());
					bqList.setTitle(item.getName());
					bqList.setRemark(StringUtils.checkString(item.getRemark()));
					List<EvaluationSorItemPojo> bqItemList = new ArrayList<EvaluationSorItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getSupplierSorItems())) {
						for (RftSupplierSorItem bqItem : item.getSupplierSorItems()) {
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

			summary.add(eventDetails);
			parameters.put("SUPPLIER_SUMMARY", summary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Error generating Supplier Summary Report : " + e.getMessage(), e);
		}
		LOG.info("Return report");
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
	private List<EvaluationMeetingPojo> summaryMeetingDetails(RftEvent event, String imgPath, SimpleDateFormat sdf, String supplierId) {
		List<RftEventMeeting> meetingList = event.getMeetings();
		List<EvaluationMeetingPojo> meetings = new ArrayList<EvaluationMeetingPojo>();
		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RftEventMeeting meeting : meetingList) {
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
					for (RftEventMeetingContact mc : meeting.getRfxEventMeetingContacts()) {
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
					for (RftEventMeetingDocument docs : meeting.getRfxEventMeetingDocument()) {
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

	@Override
	@Transactional(readOnly = false)
	public RftEventSupplier saveRftEventSupplier(RftEventSupplier rftEventSupplier) {
		rftEventSupplier = rftEventSupplierDao.saveOrUpdate(rftEventSupplier);
		if (rftEventSupplier.getSupplier() != null) {
			rftEventSupplier.getSupplier().getCompanyName();
			rftEventSupplier.getSupplier().getId();
		}

		if (rftEventSupplier.getRfxEvent() != null && rftEventSupplier.getRfxEvent().getCreatedBy() != null && rftEventSupplier.getRfxEvent().getCreatedBy().getBuyer() != null) {
			rftEventSupplier.getRfxEvent().getCreatedBy().getBuyer().getCommunicationEmail();
			rftEventSupplier.getRfxEvent().getTenantId();
			rftEventSupplier.getRfxEvent().getCreatedBy().getTenantId();
		}

		if (rftEventSupplier.getRfxEvent() != null && rftEventSupplier.getRfxEvent().getEventOwner() != null) {
			rftEventSupplier.getRfxEvent().getEventOwner().getCommunicationEmail();
			rftEventSupplier.getRfxEvent().getEventOwner().getTenantId();
		}

		if (rftEventSupplier.getRfxEvent() != null && rftEventSupplier.getRfxEvent().getParticipationFeeCurrency() != null) {
			rftEventSupplier.getRfxEvent().getParticipationFeeCurrency().getCurrencyCode();
		}

		if (rftEventSupplier.getRfxEvent() != null && rftEventSupplier.getRfxEvent().getTeamMembers() != null) {
			for (RftTeamMember member : rftEventSupplier.getRfxEvent().getTeamMembers()) {
				member.getUser().getCommunicationEmail();
				member.getUser().getTenantId();
			}
		}

		return rftEventSupplier;
	}

	@Override
	public List<RftEventSupplier> findDisqualifySupplierByEventId(String eventId) {
		return rftEventSupplierDao.findDisqualifySupplierByEventId(eventId);
	}

	@Override
	public List<FeePojo> getAllInvitedSuppliersByEventId(String eventId) {
		return rftEventSupplierDao.getAllInvitedSuppliersByEventId(eventId);
	}

	@Override
	public List<RftEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId) {
		return rftEventSupplierDao.getAllSuppliersByFeeEventId(eventId, supplierId);
	}

	@Override
	public List<RftEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId) {
		return rftEventSupplierDao.getAllSuppliersByEventIdAndSupplierCode(eventId, supplierCode, tenantId);
	}

	@Override
	public List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input) {
		return rftEventSupplierDao.getAllEventsSupplierPojoByEventId(eventId, input);
	}

	@Override
	public long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input) {
		return rftEventSupplierDao.getAllEventsSupplierPojoCountByEventId(eventId, input);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventSuppliersNotificationFlag(String id) {
		rftEventSupplierDao.updateEventSuppliersNotificationFlag(id);

	}

	@Override
	public List<EventSupplierPojo> getAllDetailsForSendInvitation(String eventId) {
		List<EventSupplierPojo> list = rftEventSupplierDao.getAllDetailsForSendInvitation(eventId);
		String timeZone = "GMT+8:00";
		for (EventSupplierPojo eventSupplierPojo : list) {
			String supplierTimeZone = supplierSettingsDao.getSupplierTimeZoneByTenantId(eventSupplierPojo.getSupplierId());
			eventSupplierPojo.setTimeZone(StringUtils.checkString(supplierTimeZone).length() > 0 ? supplierTimeZone : timeZone);
		}
		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public EventSupplier saveInvitedSuppliers(RftEventSupplier eventSupplier) {
		EventSupplier rftEventSupplier = rftEventSupplierDao.saveOrUpdate(eventSupplier);
		if (rftEventSupplier.getSupplier() != null) {
			rftEventSupplier.getSupplier().getCompanyName();
			rftEventSupplier.getSupplier().setIndustryCategory(null);
			rftEventSupplier.getSupplier().setProductCategory(null);
			rftEventSupplier.getSupplier().setCreatedBy(null);
		}
		return rftEventSupplier;
	}

	@Override

	public List<SupplierSearchPojo> favoriteSuppliersOfBuyerByState(String buyerId, SupplierSearchPojo supplierSearchPojo, List<IndustryCategory> industryCategories, Boolean exclusive, Boolean inclusive, String eventType, String eventId) {
		boolean isMinMaxPresent = false;
		switch (RfxTypes.valueOf(eventType)) {
		case RFT:
			isMinMaxPresent = rftEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFA:
			isMinMaxPresent = rfaEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFP:
			isMinMaxPresent = rfpEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFQ:
			isMinMaxPresent = rfqEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFI:
			isMinMaxPresent = rfiEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		}
		return rftEventSupplierDao.favoriteSuppliersOfBuyerByState(buyerId, supplierSearchPojo, industryCategories, exclusive, inclusive, eventType, eventId, isMinMaxPresent);
	}

	@Override
	public List<SupplierSearchPojo> favoriteSuppliersOfBuyer(String buyerId, SupplierSearchPojo supplierSearchPojo, Boolean exclusive, Boolean inclusive, String eventType, String eventId) {
		boolean isMinMaxPresent = false;
		switch (RfxTypes.valueOf(eventType)) {
		case RFT:
			isMinMaxPresent = rftEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFA:
			isMinMaxPresent = rfaEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFP:
			isMinMaxPresent = rfpEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFQ:
			isMinMaxPresent = rfqEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		case RFI:
			isMinMaxPresent = rfiEventDao.isMinMaxSupplierRatingAvaliableInEvent(eventId);
			break;
		}
		return rftEventSupplierDao.favoriteSuppliersOfBuyer(buyerId, supplierSearchPojo, exclusive, inclusive, isMinMaxPresent, eventType, eventId);
	}

	@Override
	public boolean isSupplierExistsForPublicEvent(String supplierId, String eventId, RfxTypes eventType) {
		return rftEventSupplierDao.isSupplierExistsForPublicEvent(supplierId, eventId, eventType);
	}

	@Override
	public List<RftEventSupplier> getAllSubmittedSupplierByEventId(String eventId) {
		return rftEventSupplierDao.getAllSubmittedSupplierByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void batchInsert(List<RftEventSupplier> eventSupplier) {
		rftEventSupplierDao.batchInsert(eventSupplier);
	}

	@Override
	public String getEventNameByEventId(String eventId) {
		return rftEventSupplierDao.getEventNameByEventId(eventId);
	}

	@Override
	public List<RftEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId) {
		return rftEventSupplierDao.findDisqualifySupplierForEvaluationReportByEventId(eventId);
	}

	@Override
	public List<RftEventSupplier> findEventSuppliersForTatReportByEventId(String eventId) {
		return rftEventSupplierDao.findEventSuppliersForTatReportByEventId(eventId);
	}
}
