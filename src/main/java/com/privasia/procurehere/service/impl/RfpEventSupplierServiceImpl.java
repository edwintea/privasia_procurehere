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

import com.privasia.procurehere.core.dao.RfpSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RfaSupplierSor;
import com.privasia.procurehere.core.entity.RfaSupplierSorItem;
import com.privasia.procurehere.core.entity.RfpSupplierSor;
import com.privasia.procurehere.core.entity.RfpSupplierSorItem;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpEventContactDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpEventMeetingDao;
import com.privasia.procurehere.core.dao.RfpEventSupplierDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfpSupplierCqDao;
import com.privasia.procurehere.core.dao.RfpSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfpSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpCqOption;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventContact;
import com.privasia.procurehere.core.entity.RfpEventMeeting;
import com.privasia.procurehere.core.entity.RfpEventMeetingContact;
import com.privasia.procurehere.core.entity.RfpEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfpEventSupplier;
import com.privasia.procurehere.core.entity.RfpReminder;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.entity.RfpSupplierCq;
import com.privasia.procurehere.core.entity.RfpSupplierCqItem;
import com.privasia.procurehere.core.entity.RfpSupplierCqOption;
import com.privasia.procurehere.core.entity.RfpSupplierTeamMember;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.CqType;
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
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.UserService;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

@Service
@Transactional(readOnly = true)
public class RfpEventSupplierServiceImpl implements RfpEventSupplierService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	UserService userService;

	@Autowired
	RfpEventSupplierDao rfpEventSupplierDao;

	@Autowired
	RfpSupplierTeamMemberDao rfpSupplierTeamMemberDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RfpEventContactDao rfpEventContactDao;

	@Autowired
	ServletContext context;

	@Autowired
	RfpSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RfpSupplierBqItemDao supplierBqItemDao;

	@Autowired
	RfpEventMeetingDao rfpEventMeetingDao;

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Autowired
	RfpEventDao rfpEventDao;
	
	@Autowired
	RfpSupplierCqDao rfpSupplierCqDao;

	@Autowired
	RfpSupplierSorItemDao supplierSorItemDao;

	@Override
	@Transactional(readOnly = false)
	public String saveRfpEventSuppliers(RfpEventSupplier rftEventSupplier) {
		rfpEventSupplierDao.saveOrUpdate(rftEventSupplier);
		return (rftEventSupplier != null ? rftEventSupplier.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public void batchInsert(List<RfpEventSupplier> eventSupplier) {
		rfpEventSupplierDao.batchInsert(eventSupplier);
	}

	@Override
	public boolean isExists(RfpEventSupplier rftEventSupplier) {
		return rfpEventSupplierDao.isExists(rftEventSupplier, rftEventSupplier.getRfxEvent().getId());
	}

	@Override
	public List<EventSupplier> getAllSuppliersByEventId(String eventID) {
		return rfpEventSupplierDao.getAllSuppliersByEventId(eventID);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfpEventSuppliers(RfpEventSupplier rftEventSupplier) {
		rfpEventSupplierDao.delete(rftEventSupplier);
	}

	@Override
	public RfpEventSupplier findSupplierById(String id) {
		return rfpEventSupplierDao.getSupplierById(id);
	}

	@Override
	public RfpEventSupplier findSupplierBySupplierId(String id) {
		return rfpEventSupplierDao.getSupplierBySupplierId(id);
	}

	@Override
	public RfpEventSupplier findSupplierByIdAndEventId(String supplierId, String eventId) {
		return rfpEventSupplierDao.getEventSupplierBySupplierAndEventId(supplierId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventSuppliers(RfpEventSupplier eventSupplier) {
		rfpEventSupplierDao.update(eventSupplier);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeTeamMemberfromList(String eventId, String userId, String supplierId) {
		LOG.info("ServiceImpl........." + "removeTeamMemberfromList----TeamMember" + " eventId: " + eventId + " userId: " + userId + " supplierId: " + supplierId);
		RfpEventSupplier rfpEventSupplier = findSupplierByIdAndEventId(supplierId, eventId);
		rfpSupplierTeamMemberDao.deleteSupplierTeamMemberBySupplierIdForEvent(rfpEventSupplier.getId(), userId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfpSupplierTeamMember> addTeamMemberToList(String eventId, String userId, String supplierId, TeamMemberType memberType) {
		LOG.info("ServiceImpl........." + "addTeamMemberToList----TeamMember" + " eventId: " + eventId + " userId: " + userId + " TeamMember_Type: " + memberType);
		RfpEvent rfpEvent = getRfpEventByeventId(eventId);
		RfpEventSupplier rfpEventSupplier = findSupplierByIdAndEventId(supplierId, eventId);
		LOG.info("RfpEventSupplier *****:" + rfpEventSupplier.getSupplierCompanyName());
		List<RfpSupplierTeamMember> teamMembers = rfpEventSupplierDao.getRfpSupplierTeamMembersForEvent(eventId, supplierId);
		if (teamMembers == null) {
			teamMembers = new ArrayList<RfpSupplierTeamMember>();
		}
		LOG.info("teamMembers : *******" + teamMembers);
		RfpSupplierTeamMember rfpTeamMember = new RfpSupplierTeamMember();
		rfpTeamMember.setEventSupplier(rfpEventSupplier);
		rfpTeamMember.setEvent(rfpEvent);
		User user = new User();// userService.getUsersById(userId);
		user.setId(userId);
		rfpTeamMember.setUser(user);

		boolean exists = false;
		for (RfpSupplierTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(user.getId())) {
				rfpTeamMember = member;
				exists = true;
				break;
			}
		}
		rfpTeamMember.setTeamMemberType(memberType);

		if (!exists) {
			teamMembers.add(rfpTeamMember);
		}

		rfpEventSupplier.setTeamMembers(teamMembers);
		rfpEventSupplierDao.update(rfpEventSupplier);
		return teamMembers;
	}

	@Override
	public RfpEvent getRfpEventByeventId(String eventId) {
		return rfpEventSupplierDao.findByEventId(eventId);
	}

	@Override
	public List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId) {
		return rfpEventSupplierDao.getSupplierTeamMembersForEvent(eventId, supplierId);
	}

	@Override
	public List<RfpSupplierTeamMember> getRfpSupplierTeamMembersForEvent(String eventId, String supplierId) {
		return rfpEventSupplierDao.getRfpSupplierTeamMembersForEvent(eventId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public void addSupplierForPublicEvent(String eventId, String supplierId) {
		LOG.info("supplierId : " + supplierId + " eventId: " + eventId);
		RfpEvent rfpEvent = getRfpEventByeventId(eventId);
		List<RfpEventSupplier> rfpEventSupplier = rfpEvent.getSuppliers();
		if (rfpEventSupplier == null) {
			rfpEventSupplier = new ArrayList<RfpEventSupplier>();
		}

		boolean exists = false;
		for (RfpEventSupplier eventSupplier : rfpEventSupplier) {
			if (eventSupplier.getSupplier().getId().equals(supplierId)) {
				exists = true;
				break;
			}
		}

		if (!exists) {
			RfpEventSupplier rfpSupplier = new RfpEventSupplier();
			Supplier supplier = new Supplier();
			supplier.setId(supplierId);
			rfpSupplier.setSupplier(supplier);
			rfpSupplier.setRfxEvent(rfpEvent);
			rfpSupplier.setSupplierInvitedTime(new Date());
			rfpSupplier.setSelfInvited(true);
			rfpEventSupplierDao.saveOrUpdate(rfpSupplier);

			/**
			 * Add site visit meeting for self invited supplier
			 */
			RfpEventMeeting siteMeeting = rfpEventMeetingDao.findMinMandatorySiteVisitMeetingsByEventId(eventId);
			if (siteMeeting != null) {
				List<Supplier> suppList = siteMeeting.getInviteSuppliers();
				if (suppList == null) {
					suppList = new ArrayList<Supplier>();
				}
				suppList.add(supplier);
				siteMeeting.setInviteSuppliers(suppList);
				rfpEventMeetingDao.saveOrUpdate(siteMeeting);
			}
		}
		LOG.info("supplierId : " + supplierId + " eventId: " + eventId + "RfpEventSupplier Updated... and Supplier added.");
	}

	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId) {
		return rfpEventSupplierDao.getEventSuppliersForEvaluation(eventId);
	}

	@Override
	public List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId) {
		return rfpEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId) {
		return rfpEventSupplierDao.getUserPemissionsForEvent(userId, supplierId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public int updatePrivewTime(String eventId, String supplierId) {
		return rfpEventSupplierDao.updatePrivewTime(eventId, supplierId);
	}

	@Override
	public List<EventSupplier> getAllSuppliersByEventIdOrderByCompName(String eventId) {
		return rfpEventSupplierDao.getAllSuppliersByEventIdOrderByCompName(eventId);
	}

	@Override
	public Integer getCountOfSupplierByEventId(String eventId) {
		return rfpEventSupplierDao.getCountOfSupplierByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllSuppliersByEventId(String eventId) {
		rfpEventSupplierDao.deleteAllSuppliersByEventId(eventId);
		// List<EventSupplier> supplierList = rfpEventSupplierDao.getAllSuppliersByEventId(eventId);
		// for (EventSupplier eventSupplier : supplierList) {
		// rfpEventSupplierDao.delete((RfpEventSupplier) eventSupplier);
		// }
	}

	@Override
	public JasperPrint generateSupplierSummary(RfpEvent event, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer) {
		event = rfpEventDao.getPlainEventById(event.getId());
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
			eventDetails.setType("RFP");
			eventDetails.setTeanantType("Supplier");
			eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");

			List<EvaluationPojo> eventRemider = new ArrayList<EvaluationPojo>();
			if (CollectionUtil.isNotEmpty(event.getReminder())) {
				for (RfpReminder item : event.getReminder()) {
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
			List<RfpEventContact> eventContacts = rfpEventContactDao.findAllEventContactById(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RfpEventContact contact : eventContacts) {
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
			List<RfpSupplierCqItem> supplierCqItem = supplierCqItemDao.getSupplierCqItemsbySupplierIdAndEventId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			Map<RfpCq, List<RfpSupplierCqItem>> cqList = new LinkedHashMap<RfpCq, List<RfpSupplierCqItem>>();
			for (RfpSupplierCqItem item : supplierCqItem) {
				List<RfpSupplierCqItem> itemList = cqList.get(item.getCq());
				if (itemList == null) {
					itemList = new ArrayList<RfpSupplierCqItem>();
					itemList.add(item);
					cqList.put(item.getCq(), itemList);
				} else {
					itemList.add(item);
				}
			}
			for (Map.Entry<RfpCq, List<RfpSupplierCqItem>> entry : cqList.entrySet()) {
				EvaluationCqPojo cqDetails = new EvaluationCqPojo();
				cqDetails.setName(entry.getKey().getName());
				cqDetails.setDescription(entry.getKey().getDescription());
				List<EvaluationCqItemPojo> cqItemList = new ArrayList<>();
				
				RfpSupplierCq cqStatus = rfpSupplierCqDao.findCqByEventIdAndEventCqId(event.getId(), entry.getKey().getId(), supplierId);
				for (RfpSupplierCqItem cqItem : entry.getValue()) {
					String answer = "";
					EvaluationCqItemPojo suppCqItem = new EvaluationCqItemPojo();
					String level = "";
					if (cqItem.getCqItem().getOptional() == Boolean.TRUE) {
						level = "*";
					}

					if (cqItem.getCqItem().getCqType() == CqType.CHOICE || cqItem.getCqItem().getCqType() == CqType.LIST || cqItem.getCqItem().getCqType() == CqType.CHOICE_WITH_SCORE || cqItem.getCqItem().getCqType() == CqType.CHECKBOX) {

						List<String> selectedOptions = new ArrayList<>();
						for (RfpSupplierCqOption supCqOpt : cqItem.getListAnswers()) {
							if (!selectedOptions.contains(supCqOpt.getValue())) {
								selectedOptions.add(supCqOpt.getValue());
							}
						}

						for (RfpCqOption cqOption : cqItem.getCqItem().getCqOptions()) {
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
			List<RfpSupplierBq> supplierBqItem = supplierBqItemDao.getAllBqsBySupplierId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (CollectionUtil.isNotEmpty(supplierBqItem)) {
				DecimalFormat numFormat = new DecimalFormat(event.getDecimal().equals("1") ? "#,###,###,##0.0" : event.getDecimal().equals("2") ? "#,###,###,##0.00" : event.getDecimal().equals("3") ? "#,###,###,##0.000" : event.getDecimal().equals("4") ? "#,###,###,##0.0000" : event.getDecimal().equals("5") ? "#,###,###,##0.00000" : event.getDecimal().equals("6") ? "#,###,###,##0.000000" : "#,###,###,##0.00");
				for (RfpSupplierBq item : supplierBqItem) {
					EvaluationBqPojo bqList = new EvaluationBqPojo();

					bqList.setName(item.getName());
					bqList.setTitle(item.getName());
					bqList.setRemark(StringUtils.checkString(item.getRemark()));

					List<EvaluationBqItemPojo> bqItemList = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getSupplierBqItems())) {
						for (RfpSupplierBqItem bqItem : item.getSupplierBqItems()) {
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
			List<RfpSupplierSor> supplierSorItem = supplierSorItemDao.getAllSorsBySupplierId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (CollectionUtil.isNotEmpty(supplierSorItem)) {
				for (RfpSupplierSor item : supplierSorItem) {
					EvaluationSorPojo bqList = new EvaluationSorPojo();

					bqList.setName(item.getName());
					bqList.setTitle(item.getName());
					bqList.setRemark(StringUtils.checkString(item.getRemark()));
					List<EvaluationSorItemPojo> bqItemList = new ArrayList<EvaluationSorItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getSupplierSorItems())) {
						for (RfpSupplierSorItem bqItem : item.getSupplierSorItems()) {
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
	private List<EvaluationMeetingPojo> summaryMeetingDetails(RfpEvent event, String imgPath, SimpleDateFormat sdf, String supplierId) {
		List<RfpEventMeeting> meetingList = event.getMeetings();
		List<EvaluationMeetingPojo> meetings = new ArrayList<EvaluationMeetingPojo>();
		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RfpEventMeeting meeting : meetingList) {
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
					for (RfpEventMeetingContact mc : meeting.getRfxEventMeetingContacts()) {
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
					for (RfpEventMeetingDocument docs : meeting.getRfxEventMeetingDocument()) {
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
	public RfpEventSupplier saveRfpEventSupplier(RfpEventSupplier rfpEventSupplier) {
		rfpEventSupplier = rfpEventSupplierDao.saveOrUpdate(rfpEventSupplier);
		if (rfpEventSupplier.getSupplier() != null) {
			rfpEventSupplier.getSupplier().getCompanyName();
			rfpEventSupplier.getSupplier().getId();
		}

		if (rfpEventSupplier.getRfxEvent() != null && rfpEventSupplier.getRfxEvent().getCreatedBy() != null && rfpEventSupplier.getRfxEvent().getCreatedBy().getBuyer() != null) {
			rfpEventSupplier.getRfxEvent().getCreatedBy().getBuyer().getCommunicationEmail();
			rfpEventSupplier.getRfxEvent().getTenantId();
			rfpEventSupplier.getRfxEvent().getCreatedBy().getTenantId();
		}

		if (rfpEventSupplier.getRfxEvent() != null && rfpEventSupplier.getRfxEvent().getParticipationFeeCurrency() != null) {
			rfpEventSupplier.getRfxEvent().getParticipationFeeCurrency().getCurrencyCode();
		}

		if (rfpEventSupplier.getRfxEvent() != null && rfpEventSupplier.getRfxEvent().getEventOwner() != null) {
			rfpEventSupplier.getRfxEvent().getEventOwner().getCommunicationEmail();
			rfpEventSupplier.getRfxEvent().getEventOwner().getTenantId();
		}

		if (rfpEventSupplier.getRfxEvent() != null && rfpEventSupplier.getRfxEvent().getTeamMembers() != null) {
			for (RfpTeamMember member : rfpEventSupplier.getRfxEvent().getTeamMembers()) {
				member.getUser().getCommunicationEmail();
				member.getUser().getTenantId();
			}
		}

		return rfpEventSupplier;
	}

	@Override
	public List<RfpEventSupplier> findDisqualifySupplierByEventId(String eventId) {
		return rfpEventSupplierDao.findDisqualifySupplierByEventId(eventId);
	}

	@Override
	public List<FeePojo> getAllInvitedSuppliersByEventId(String eventId) {
		return rfpEventSupplierDao.getAllInvitedSuppliersByEventId(eventId);
	}

	@Override
	public List<RfpEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId) {
		return rfpEventSupplierDao.getAllSuppliersByFeeEventId(eventId, supplierId);
	}

	@Override
	public List<RfpEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId) {
		return rfpEventSupplierDao.getAllSuppliersByEventIdAndSupplierCode(eventId, supplierCode, tenantId);
	}

	@Override
	public List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input) {
		return rfpEventSupplierDao.getAllEventsSupplierPojoByEventId(eventId, input);
	}

	@Override
	public long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input) {
		return rfpEventSupplierDao.getAllEventsSupplierPojoCountByEventId(eventId, input);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventSuppliersNotificationFlag(String id) {
		rfpEventSupplierDao.updateEventSuppliersNotificationFlag(id);

	}

	@Override
	public List<EventSupplierPojo> getAllDetailsForSendInvitation(String eventId) {
		List<EventSupplierPojo> list = rfpEventSupplierDao.getAllDetailsForSendInvitation(eventId);
		String timeZone = "GMT+8:00";
		for (EventSupplierPojo eventSupplierPojo : list) {
			String supplierTimeZone = supplierSettingsDao.getSupplierTimeZoneByTenantId(eventSupplierPojo.getSupplierId());
			eventSupplierPojo.setTimeZone(StringUtils.checkString(supplierTimeZone).length() > 0 ? supplierTimeZone : timeZone);
		}
		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public EventSupplier saveInvitedSuppliers(RfpEventSupplier eventSupplier) {
		EventSupplier rfpEventSupplier = rfpEventSupplierDao.saveOrUpdate(eventSupplier);
		if (rfpEventSupplier.getSupplier() != null) {
			rfpEventSupplier.getSupplier().getCompanyName();
			rfpEventSupplier.getSupplier().setIndustryCategory(null);
			rfpEventSupplier.getSupplier().setProductCategory(null);
			rfpEventSupplier.getSupplier().setCreatedBy(null);
		}
		return rfpEventSupplier;
	}

	@Override
	public List<RfpEventSupplier> getAllSubmittedSupplierByEventId(String eventId) {
		return rfpEventSupplierDao.getAllSubmittedSupplierByEventId(eventId);
	}

	@Override
	public String getEventNameByEventId(String eventId) {
		return rfpEventSupplierDao.getEventNameByEventId(eventId);
	}
	
	@Override
	public List<RfpEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId) {
		return rfpEventSupplierDao.findDisqualifySupplierForEvaluationReportByEventId(eventId);
	}
	
	@Override
	public List<RfpEventSupplier> findEventSuppliersForTatReportByEventId(String eventId) {
		return rfpEventSupplierDao.findEventSuppliersForTatReportByEventId(eventId);
	}
}
