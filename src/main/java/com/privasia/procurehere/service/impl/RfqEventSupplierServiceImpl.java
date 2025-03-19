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

import com.privasia.procurehere.core.dao.RfqSupplierSorItemDao;
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

import com.privasia.procurehere.core.dao.RfqEventContactDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqEventMeetingDao;
import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfqSupplierCqDao;
import com.privasia.procurehere.core.dao.RfqSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfqSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqCqOption;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventContact;
import com.privasia.procurehere.core.entity.RfqEventMeeting;
import com.privasia.procurehere.core.entity.RfqEventMeetingContact;
import com.privasia.procurehere.core.entity.RfqEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfqEventSupplier;
import com.privasia.procurehere.core.entity.RfqReminder;
import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;
import com.privasia.procurehere.core.entity.RfqSupplierCq;
import com.privasia.procurehere.core.entity.RfqSupplierCqItem;
import com.privasia.procurehere.core.entity.RfqSupplierCqOption;
import com.privasia.procurehere.core.entity.RfqSupplierTeamMember;
import com.privasia.procurehere.core.entity.RfqTeamMember;
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
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.UserService;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

@Service
@Transactional(readOnly = true)
public class RfqEventSupplierServiceImpl implements RfqEventSupplierService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	RfqEventSupplierDao rfqEventSupplierDao;

	@Autowired
	UserService userService;

	@Autowired
	RfqSupplierTeamMemberDao rfqSupplierTeamMemberDao;

	@Autowired
	RfqEventContactDao rfqEventContactDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	ServletContext context;

	@Autowired
	RfqSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RfqSupplierBqItemDao supplierBqItemDao;

	@Autowired
	RfqSupplierSorItemDao supplierSorItemDao;

	@Autowired
	RfqEventMeetingDao rfqEventMeetingDao;

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Autowired
	RfqEventDao rfqEventDao;
	
	@Autowired
	RfqSupplierCqDao rfqSupplierCqDao;

	@Override
	@Transactional(readOnly = false)
	public String saveEventSuppliers(RfqEventSupplier rftEventSupplier) {
		rfqEventSupplierDao.saveOrUpdate(rftEventSupplier);
		return (rftEventSupplier != null ? rftEventSupplier.getId() : null);
	}

	@Override
	public boolean isExists(RfqEventSupplier rftEventSupplier) {
		return rfqEventSupplierDao.isExists(rftEventSupplier, rftEventSupplier.getRfxEvent().getId());
	}

	@Override
	public List<EventSupplier> getAllSuppliersByEventId(String eventID) {
		return rfqEventSupplierDao.getAllSuppliersByEventId(eventID);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfqEventSuppliers(RfqEventSupplier rftEventSupplier) {
		rfqEventSupplierDao.delete(rftEventSupplier);
	}

	@Override
	public RfqEventSupplier findSupplierById(String id) {
		return rfqEventSupplierDao.getSupplierById(id);
	}

	@Override
	public RfqEventSupplier findSupplierBySupplierId(String id) {
		return rfqEventSupplierDao.getSupplierBySupplierId(id);
	}

	@Override
	public RfqEventSupplier findSupplierByIdAndEventId(String supplierId, String eventId) {
		return rfqEventSupplierDao.getEventSupplierBySupplierAndEventId(supplierId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventSuppliers(RfqEventSupplier eventSupplier) {
		rfqEventSupplierDao.update(eventSupplier);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeTeamMemberfromList(String eventId, String userId, String supplierId) {
		RfqEventSupplier rfqEventSupplier = findSupplierByIdAndEventId(supplierId, eventId);
		rfqSupplierTeamMemberDao.deleteSupplierTeamMemberBySupplierIdForEvent(rfqEventSupplier.getId(), userId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfqSupplierTeamMember> addTeamMemberToList(String eventId, String userId, String supplierId, TeamMemberType memberType) {
		RfqEvent rfqEvent = getRfqEventByeventId(eventId);
		RfqEventSupplier rfqEventSupplier = findSupplierByIdAndEventId(supplierId, eventId);
		List<RfqSupplierTeamMember> teamMembers = rfqEventSupplierDao.getRfqSupplierTeamMembersForEvent(eventId, supplierId);
		if (teamMembers == null) {
			teamMembers = new ArrayList<RfqSupplierTeamMember>();
		}
		RfqSupplierTeamMember rfqTeamMember = new RfqSupplierTeamMember();
		rfqTeamMember.setEventSupplier(rfqEventSupplier);
		rfqTeamMember.setEvent(rfqEvent);
		User user = new User(); // userService.getUsersById(userId);
		user.setId(userId);
		rfqTeamMember.setUser(user);

		boolean exists = false;
		for (RfqSupplierTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(user.getId())) {
				rfqTeamMember = member;
				exists = true;
				break;
			}
		}
		rfqTeamMember.setTeamMemberType(memberType);

		if (!exists) {
			teamMembers.add(rfqTeamMember);
		}

		rfqEventSupplier.setTeamMembers(teamMembers);
		rfqEventSupplierDao.update(rfqEventSupplier);
		return teamMembers;
	}

	@Override
	public RfqEvent getRfqEventByeventId(String eventId) {
		return rfqEventSupplierDao.findByEventId(eventId);
	}

	@Override
	public List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId) {
		return rfqEventSupplierDao.getSupplierTeamMembersForEvent(eventId, supplierId);
	}

	@Override
	public List<RfqSupplierTeamMember> getRfqSupplierTeamMembersForEvent(String eventId, String supplierId) {

		return rfqEventSupplierDao.getRfqSupplierTeamMembersForEvent(eventId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public void addSupplierForPublicEvent(String eventId, String supplierId) {
		LOG.info("supplierId : " + supplierId + " eventId: " + eventId);
		RfqEvent rfqEvent = getRfqEventByeventId(eventId);
		List<RfqEventSupplier> rfqEventSupplier = rfqEvent.getSuppliers();
		if (rfqEventSupplier == null) {
			rfqEventSupplier = new ArrayList<RfqEventSupplier>();
		}

		boolean exists = false;
		for (RfqEventSupplier eventSupplier : rfqEventSupplier) {
			if (eventSupplier.getSupplier().getId().equals(supplierId)) {
				exists = true;
				break;
			}
		}

		if (!exists) {
			RfqEventSupplier rfqSupplier = new RfqEventSupplier();
			Supplier supplier = new Supplier();
			supplier.setId(supplierId);
			rfqSupplier.setSupplier(supplier);
			rfqSupplier.setRfxEvent(rfqEvent);
			rfqSupplier.setSupplierInvitedTime(new Date());
			rfqSupplier.setSelfInvited(true);
			rfqEventSupplierDao.saveOrUpdate(rfqSupplier);

			/**
			 * Add site visit meeting for self invited supplier
			 */
			RfqEventMeeting siteMeeting = rfqEventMeetingDao.findMinMandatorySiteVisitMeetingsByEventId(eventId);
			if (siteMeeting != null) {
				List<Supplier> suppList = siteMeeting.getInviteSuppliers();
				if (suppList == null) {
					suppList = new ArrayList<Supplier>();
				}
				suppList.add(supplier);
				siteMeeting.setInviteSuppliers(suppList);
				rfqEventMeetingDao.saveOrUpdate(siteMeeting);
			}
		}
		LOG.info("supplierId : " + supplierId + " eventId: " + eventId + "RfqEventSupplier Updated... and Supplier added.");
	}

	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId) {
		return rfqEventSupplierDao.getEventSuppliersForEvaluation(eventId);
	}

	@Override
	public List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId) {
		return rfqEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId) {
		return rfqEventSupplierDao.getUserPemissionsForEvent(userId, supplierId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public int updatePrivewTime(String eventId, String supplierId) {
		return rfqEventSupplierDao.updatePrivewTime(eventId, supplierId);
	}

	@Override
	public List<EventSupplier> getAllSuppliersByEventIdOrderByCompName(String eventId) {
		return rfqEventSupplierDao.getAllSuppliersByEventIdOrderByCompName(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllSuppliersByEventId(String eventId) {
		rfqEventSupplierDao.deleteAllSuppliersByEventId(eventId);
		//
		// List<EventSupplier> supplierList = rfqEventSupplierDao.getAllSuppliersByEventId(eventId);
		// for (EventSupplier eventSupplier : supplierList) {
		// rfqEventSupplierDao.delete((RfqEventSupplier) eventSupplier);
		// }
	}

	@Override
	public Integer getCountOfSupplierByEventId(String eventId) {
		return rfqEventSupplierDao.getCountOfSupplierByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public JasperPrint generateSupplierSummary(RfqEvent event, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer) {
		event = rfqEventDao.getPlainEventById(event.getId());
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
			eventDetails.setType("RFQ");
			eventDetails.setTeanantType("Supplier");
			eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");

			List<EvaluationPojo> eventRemider = new ArrayList<EvaluationPojo>();
			if (CollectionUtil.isNotEmpty(event.getRftReminder())) {
				for (RfqReminder item : event.getRftReminder()) {
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

			// eventDetails.setCategory(event.getIndustryCategory().getName());

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
			List<RfqEventContact> eventContacts = rfqEventContactDao.findAllEventContactById(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RfqEventContact contact : eventContacts) {
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
			List<RfqSupplierCqItem> supplierCqItem = supplierCqItemDao.getSupplierCqItemsbySupplierIdAndEventId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			Map<RfqCq, List<RfqSupplierCqItem>> cqList = new LinkedHashMap<RfqCq, List<RfqSupplierCqItem>>();
			for (RfqSupplierCqItem item : supplierCqItem) {
				List<RfqSupplierCqItem> itemList = cqList.get(item.getCq());
				if (itemList == null) {
					itemList = new ArrayList<RfqSupplierCqItem>();
					itemList.add(item);
					cqList.put(item.getCq(), itemList);
				} else {
					itemList.add(item);
				}
			}
			for (Map.Entry<RfqCq, List<RfqSupplierCqItem>> entry : cqList.entrySet()) {
				EvaluationCqPojo cqDetails = new EvaluationCqPojo();
				cqDetails.setName(entry.getKey().getName());
				cqDetails.setDescription(entry.getKey().getDescription());
				List<EvaluationCqItemPojo> cqItemList = new ArrayList<>();
				
				RfqSupplierCq cqStatus = rfqSupplierCqDao.findCqByEventIdAndEventCqId(event.getId(), entry.getKey().getId(), supplierId);
				for (RfqSupplierCqItem cqItem : entry.getValue()) {
					String answer = "";
					EvaluationCqItemPojo suppCqItem = new EvaluationCqItemPojo();
					String level = "";
					if (cqItem.getCqItem().getOptional() == Boolean.TRUE) {
						level = "*";
					}

					if (cqItem.getCqItem().getCqType() == CqType.CHOICE || cqItem.getCqItem().getCqType() == CqType.LIST || cqItem.getCqItem().getCqType() == CqType.CHOICE_WITH_SCORE || cqItem.getCqItem().getCqType() == CqType.CHECKBOX) {

						List<String> selectedOptions = new ArrayList<>();
						for (RfqSupplierCqOption supCqOpt : cqItem.getListAnswers()) {
							if (!selectedOptions.contains(supCqOpt.getValue())) {
								selectedOptions.add(supCqOpt.getValue());
							}
						}

						for (RfqCqOption cqOption : cqItem.getCqItem().getCqOptions()) {
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
			List<RfqSupplierBq> supplierBqItem = supplierBqItemDao.getAllBqsBySupplierId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (CollectionUtil.isNotEmpty(supplierBqItem)) {
				DecimalFormat numFormat = new DecimalFormat(event.getDecimal().equals("1") ? "#,###,###,##0.0" : event.getDecimal().equals("2") ? "#,###,###,##0.00" : event.getDecimal().equals("3") ? "#,###,###,##0.000" : event.getDecimal().equals("4") ? "#,###,###,##0.0000" : event.getDecimal().equals("5") ? "#,###,###,##0.00000" : event.getDecimal().equals("6") ? "#,###,###,##0.000000" : "#,###,###,##0.00");
				for (RfqSupplierBq item : supplierBqItem) {
					EvaluationBqPojo bqList = new EvaluationBqPojo();

					bqList.setName(item.getName());
					bqList.setTitle(item.getName());
					bqList.setRemark(StringUtils.checkString(item.getRemark()));
					List<EvaluationBqItemPojo> bqItemList = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getSupplierBqItems())) {
						for (RfqSupplierBqItem bqItem : item.getSupplierBqItems()) {
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


			// Schedule Of rate
			List<EvaluationSorPojo> supplierSorList = new ArrayList<EvaluationSorPojo>();
			List<RfqSupplierSor> supplierSorItem = supplierSorItemDao.getAllSorsBySupplierId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (CollectionUtil.isNotEmpty(supplierSorItem)) {
				for (RfqSupplierSor item : supplierSorItem) {
					EvaluationSorPojo bqList = new EvaluationSorPojo();

					bqList.setName(item.getName());
					bqList.setTitle(item.getName());
					bqList.setRemark(StringUtils.checkString(item.getRemark()));
					List<EvaluationSorItemPojo> bqItemList = new ArrayList<EvaluationSorItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getSupplierSorItems())) {
						for (RfqSupplierSorItem bqItem : item.getSupplierSorItems()) {
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
			parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Error generating Supplier Summary Report : " + e.getMessage(), e);
		}
		LOG.info("Rfq Supplier summary report retun");
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
	private List<EvaluationMeetingPojo> summaryMeetingDetails(RfqEvent event, String imgPath, SimpleDateFormat sdf, String supplierId) {
		List<RfqEventMeeting> meetingList = event.getMeetings();
		List<EvaluationMeetingPojo> meetings = new ArrayList<EvaluationMeetingPojo>();
		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RfqEventMeeting meeting : meetingList) {
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
					for (RfqEventMeetingContact mc : meeting.getRfxEventMeetingContacts()) {
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
					for (RfqEventMeetingDocument docs : meeting.getRfxEventMeetingDocument()) {
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
	public RfqEventSupplier saveRfqEventSupplier(RfqEventSupplier eventSupplier) {
		eventSupplier = rfqEventSupplierDao.saveOrUpdate(eventSupplier);
		if (eventSupplier.getSupplier() != null) {
			eventSupplier.getSupplier().getCompanyName();
			eventSupplier.getSupplier().getId();
		}

		if (eventSupplier.getRfxEvent() != null && eventSupplier.getRfxEvent().getCreatedBy() != null && eventSupplier.getRfxEvent().getCreatedBy().getBuyer() != null) {
			eventSupplier.getRfxEvent().getCreatedBy().getBuyer().getCommunicationEmail();
			eventSupplier.getRfxEvent().getTenantId();
			eventSupplier.getRfxEvent().getCreatedBy().getTenantId();
		}

		if (eventSupplier.getRfxEvent() != null && eventSupplier.getRfxEvent().getParticipationFeeCurrency() != null) {
			eventSupplier.getRfxEvent().getParticipationFeeCurrency().getCurrencyCode();
		}

		if (eventSupplier.getRfxEvent() != null && eventSupplier.getRfxEvent().getEventOwner() != null) {
			eventSupplier.getRfxEvent().getEventOwner().getCommunicationEmail();
			eventSupplier.getRfxEvent().getEventOwner().getTenantId();
		}

		if (eventSupplier.getRfxEvent() != null && eventSupplier.getRfxEvent().getTeamMembers() != null) {
			for (RfqTeamMember member : eventSupplier.getRfxEvent().getTeamMembers()) {
				member.getUser().getCommunicationEmail();
				member.getUser().getTenantId();
			}
		}

		return eventSupplier;
	}

	@Override
	@Transactional(readOnly = false)
	public void batchInsert(List<RfqEventSupplier> eventSupplier) {
		rfqEventSupplierDao.batchInsert(eventSupplier);
	}

	public static void main(String[] args) {

		DecimalFormat numFormat;

		String number;

		numFormat = new DecimalFormat("#,###,###.00");
		BigDecimal a = new BigDecimal(12454545.100);

		number = numFormat.format(a);

		System.out.println(number);

	}

	@Override
	public List<RfqEventSupplier> findDisqualifySupplierByEventId(String eventId) {
		return rfqEventSupplierDao.findDisqualifySupplierByEventId(eventId);
	}

	@Override
	public List<String> getAllRfaEventSuppliersIdByEventId(String eventId) {
		return rfqEventSupplierDao.getAllRfaEventSuppliersIdByEventId(eventId);
	}

	@Override
	public List<EventSupplier> getAllPartiallyCompleteBidsByEventId(String eventId) {
		return rfqEventSupplierDao.getAllPartiallyCompleteBidsByEventId(eventId);
	}

	@Override
	public List<FeePojo> getAllInvitedSuppliersByEventId(String eventId) {
		return rfqEventSupplierDao.getAllInvitedSuppliersByEventId(eventId);
	}

	@Override
	public List<RfqEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId) {
		return rfqEventSupplierDao.getAllSuppliersByFeeEventId(eventId, supplierId);
	}

	@Override
	public List<RfqEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId) {
		return rfqEventSupplierDao.getAllSuppliersByEventIdAndSupplierCode(eventId, supplierCode, tenantId);
	}

	@Override
	public List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input) {
		return rfqEventSupplierDao.getAllEventsSupplierPojoByEventId(eventId, input);
	}

	@Override
	public long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input) {
		return rfqEventSupplierDao.getAllEventsSupplierPojoCountByEventId(eventId, input);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventSuppliersNotificationFlag(String id) {
		rfqEventSupplierDao.updateEventSuppliersNotificationFlag(id);

	}

	@Override
	public List<EventSupplierPojo> getAllDetailsForSendInvitation(String eventId) {
		List<EventSupplierPojo> list = rfqEventSupplierDao.getAllDetailsForSendInvitation(eventId);
		String timeZone = "GMT+8:00";
		LOG.info("listlistlistlist" + list.size());
		for (EventSupplierPojo eventSupplierPojo : list) {
			String supplierTimeZone = supplierSettingsDao.getSupplierTimeZoneByTenantId(eventSupplierPojo.getSupplierId());
			eventSupplierPojo.setTimeZone(StringUtils.checkString(supplierTimeZone).length() > 0 ? supplierTimeZone : timeZone);
			LOG.info("listlistlistlist" + eventSupplierPojo);
			LOG.info("listlistlistlist" + eventSupplierPojo.getCompanyName());
		}
		return list;

	}

	@Override
	@Transactional(readOnly = false)
	public EventSupplier saveInvitedSuppliers(RfqEventSupplier eventSupplier) {
		EventSupplier rfqEventSupplier = rfqEventSupplierDao.saveOrUpdate(eventSupplier);
		if (rfqEventSupplier.getSupplier() != null) {
			rfqEventSupplier.getSupplier().getCompanyName();
			rfqEventSupplier.getSupplier().setIndustryCategory(null);
			rfqEventSupplier.getSupplier().setProductCategory(null);
			rfqEventSupplier.getSupplier().setCreatedBy(null);
		}
		return rfqEventSupplier;
	}

	@Override
	public List<RfqEventSupplier> getAllSubmittedSupplierByEventId(String eventId) {
		return rfqEventSupplierDao.getAllSubmittedSupplierByEventId(eventId);
	}

	@Override
	public List<FeePojo> getAllInvitedSuppliersByEventId(String eventId, TableDataInput input, RfxTypes eventType) {
		return rfqEventSupplierDao.getAllInvitedSuppliersByEventId(eventId, input, eventType);
	}

	@Override
	public long getAllInvitedSuppliersFilterCountByEventId(String eventId, TableDataInput input, RfxTypes eventType) {
		return rfqEventSupplierDao.getAllInvitedSuppliersFilterCountByEventId(eventId, input, eventType);
	}

	@Override
	public long getAllInvitedSuppliersCountByEventId(String eventId, RfxTypes eventType) {
		return rfqEventSupplierDao.getAllInvitedSuppliersCountByEventId(eventId, eventType);
	}

	@Override
	public List<EventSupplier> getAllSubmitedSupplierByEevntId(String eventId) {
		return rfqEventSupplierDao.getAllSubmitedSupplierByEevntId(eventId);
	}

	@Override
	public String getEventNameByEventId(String eventId) {
		return rfqEventSupplierDao.getEventNameByEventId(eventId);
	}

	@Override
	public List<RfqEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId) {
		return rfqEventSupplierDao.findDisqualifySupplierForEvaluationReportByEventId(eventId);
	}

	@Override
	public List<RfqEventSupplier> findEventSuppliersForTatReportByEventId(String eventId) {
		return rfqEventSupplierDao.findEventSuppliersForTatReportByEventId(eventId);
	}
}
