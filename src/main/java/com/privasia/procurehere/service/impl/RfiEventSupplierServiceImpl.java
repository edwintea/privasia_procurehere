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

import com.privasia.procurehere.core.dao.RfiSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RfiSupplierSor;
import com.privasia.procurehere.core.entity.RfiSupplierSorItem;
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

import com.privasia.procurehere.core.dao.RfiEventContactDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfiEventMeetingDao;
import com.privasia.procurehere.core.dao.RfiEventSupplierDao;
import com.privasia.procurehere.core.dao.RfiSupplierCqDao;
import com.privasia.procurehere.core.dao.RfiSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfiSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.SupplierSettingsDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiCqOption;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventContact;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfiEventMeetingContact;
import com.privasia.procurehere.core.entity.RfiEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfiEventSupplier;
import com.privasia.procurehere.core.entity.RfiReminder;
import com.privasia.procurehere.core.entity.RfiSupplierCq;
import com.privasia.procurehere.core.entity.RfiSupplierCqItem;
import com.privasia.procurehere.core.entity.RfiSupplierCqOption;
import com.privasia.procurehere.core.entity.RfiSupplierTeamMember;
import com.privasia.procurehere.core.entity.RfiTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
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
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.UserService;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

@Service
@Transactional(readOnly = true)
public class RfiEventSupplierServiceImpl implements RfiEventSupplierService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	UserService userService;

	@Autowired
	RfiSupplierTeamMemberDao rfiSupplierTeamMemberDao;

	@Autowired
	RfiEventSupplierDao rfiEventSupplierDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RfiEventContactDao rfiEventContactDao;

	@Autowired
	RfiSupplierCqItemDao supplierCqItemDao;

	@Autowired
	ServletContext context;

	@Autowired
	RfiEventMeetingDao rfiEventMeetingDao;

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Autowired
	RfiEventDao rfiEventDao;
	
	@Autowired
	RfiSupplierCqDao rfiSupplierCqDao;

	@Autowired
	RfiSupplierSorItemDao supplierSorItemDao;

	@Override
	@Transactional(readOnly = false)
	public String saveRfiEventSuppliers(RfiEventSupplier rfiEventSupplier) {
		rfiEventSupplierDao.saveOrUpdate(rfiEventSupplier);
		return (rfiEventSupplier != null ? rfiEventSupplier.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public void batchInsert(List<RfiEventSupplier> eventSupplier) {
		rfiEventSupplierDao.batchInsert(eventSupplier);
	}

	@Override
	public boolean isExists(RfiEventSupplier rftEventSupplier) {
		return rfiEventSupplierDao.isExists(rftEventSupplier, rftEventSupplier.getRfxEvent().getId());
	}

	@Override
	public List<EventSupplier> getAllSuppliersByEventId(String eventID) {
		return rfiEventSupplierDao.getAllSuppliersByEventId(eventID);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfiEventSuppliers(RfiEventSupplier rftEventSupplier) {
		rfiEventSupplierDao.delete(rftEventSupplier);
	}

	@Override
	public RfiEventSupplier findSupplierById(String id) {
		return rfiEventSupplierDao.getSupplierById(id);
	}

	@Override
	public RfiEventSupplier findSupplierBySupplierId(String id) {
		return rfiEventSupplierDao.getSupplierBySupplierId(id);
	}

	@Override
	public RfiEventSupplier findSupplierByIdAndEventId(String supplierId, String eventId) {
		return rfiEventSupplierDao.getEventSupplierBySupplierAndEventId(supplierId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeTeamMembersfromList(String eventId, String userId, String supplierId) {
		LOG.info("ServiceImpl........." + "removeTeamMemberfromList----TeamMember" + " eventId: " + eventId + " userId: " + userId + " supplierId: " + supplierId);
		RfiEventSupplier rfiEventSupplier = findSupplierByIdAndEventId(supplierId, eventId);
		rfiSupplierTeamMemberDao.deleteSupplierTeamMemberBySupplierIdForEvent(rfiEventSupplier.getId(), userId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventSuppliers(RfiEventSupplier eventSupplier) {
		rfiEventSupplierDao.update(eventSupplier);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfiSupplierTeamMember> addTeamMemberToList(String eventId, String userId, String supplierId, TeamMemberType memberType) {
		LOG.info("ServiceImpl........." + "addTeamMemberToList----TeamMember" + " eventId: " + eventId + " userId: " + userId + " TeamMember_Type: " + memberType);
		RfiEvent rfiEvent = getRfiEventByeventId(eventId);
		RfiEventSupplier rfiEventSupplier = findSupplierByIdAndEventId(supplierId, eventId);
		LOG.info("RfiEventSupplier *****:" + rfiEventSupplier.getSupplierCompanyName());
		List<RfiSupplierTeamMember> teamMembers = rfiEventSupplierDao.getRfiSupplierTeamMembersForEvent(eventId, supplierId);
		if (teamMembers == null) {
			teamMembers = new ArrayList<RfiSupplierTeamMember>();
		}
		LOG.info("teamMembers : *******" + teamMembers);
		RfiSupplierTeamMember rfiTeamMember = new RfiSupplierTeamMember();
		rfiTeamMember.setEventSupplier(rfiEventSupplier);
		rfiTeamMember.setEvent(rfiEvent);
		User user = new User();// userService.getUsersById(userId);
		user.setId(userId);
		rfiTeamMember.setUser(user);

		boolean exists = false;
		for (RfiSupplierTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(user.getId())) {
				rfiTeamMember = member;
				exists = true;
				break;
			}
		}
		rfiTeamMember.setTeamMemberType(memberType);

		if (!exists) {
			teamMembers.add(rfiTeamMember);
		}

		rfiEventSupplier.setTeamMembers(teamMembers);
		rfiEventSupplierDao.update(rfiEventSupplier);
		return teamMembers;
	}

	private RfiEvent getRfiEventByeventId(String eventId) {
		return rfiEventSupplierDao.findByEventId(eventId);
	}

	@Override
	public List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId) {
		return rfiEventSupplierDao.getSupplierTeamMembersForEvent(eventId, supplierId);
	}

	@Override
	public List<RfiSupplierTeamMember> getRfiSupplierTeamMembersForEvent(String eventId, String supplierId) {
		return rfiEventSupplierDao.getRfiSupplierTeamMembersForEvent(eventId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public void addSupplierForPublicEvent(String eventId, String supplierId) {
		LOG.info("supplierId : " + supplierId + " eventId: " + eventId);
		RfiEvent rfiEvent = getRfiEventByeventId(eventId);
		List<RfiEventSupplier> rfiEventSupplier = rfiEvent.getSuppliers();
		if (rfiEventSupplier == null) {
			rfiEventSupplier = new ArrayList<RfiEventSupplier>();
		}

		boolean exists = false;
		for (RfiEventSupplier eventSupplier : rfiEventSupplier) {
			if (eventSupplier.getSupplier().getId().equals(supplierId)) {
				exists = true;
				break;
			}
		}

		if (!exists) {
			RfiEventSupplier rfiSupplier = new RfiEventSupplier();
			Supplier supplier = new Supplier();
			supplier.setId(supplierId);
			rfiSupplier.setSupplier(supplier);
			rfiSupplier.setRfxEvent(rfiEvent);
			rfiSupplier.setSupplierInvitedTime(new Date());
			rfiSupplier.setSelfInvited(true);
			rfiEventSupplierDao.saveOrUpdate(rfiSupplier);

			/**
			 * Add site visit meeting for self invited supplier
			 */
			RfiEventMeeting siteMeeting = rfiEventMeetingDao.findMinMandatorySiteVisitMeetingsByEventId(eventId);
			if (siteMeeting != null) {
				List<Supplier> suppList = siteMeeting.getInviteSuppliers();
				if (suppList == null) {
					suppList = new ArrayList<Supplier>();
				}
				suppList.add(supplier);
				siteMeeting.setInviteSuppliers(suppList);
				rfiEventMeetingDao.saveOrUpdate(siteMeeting);
			}
		}
		LOG.info("supplierId : " + supplierId + " eventId: " + eventId + "RfiEventSupplier Updated... and Supplier added.");
	}

	@Override
	public List<Supplier> getEventSuppliersForEvaluation(String eventId) {
		return rfiEventSupplierDao.getEventSuppliersForEvaluation(eventId);
	}

	@Override
	public List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId) {
		return rfiEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId) {
		return rfiEventSupplierDao.getUserPemissionsForEvent(userId, supplierId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public int updatePrivewTime(String eventId, String supplierId) {
		return rfiEventSupplierDao.updatePrivewTime(eventId, supplierId);
	}

	@Override
	public List<EventSupplier> getAllSuppliersByEventIdOrderByCompName(String eventId) {
		return rfiEventSupplierDao.getAllSuppliersByEventIdOrderByCompName(eventId);
	}

	@Override
	public Integer getCountOfSupplierByEventId(String eventId) {
		return rfiEventSupplierDao.getCountOfSupplierByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllSuppliersByEventId(String eventId) {
		rfiEventSupplierDao.deleteAllSuppliersByEventId(eventId);
		// List<EventSupplier> supplierList = rfiEventSupplierDao.getAllSuppliersByEventId(eventId);
		// for (EventSupplier eventSupplier : supplierList) {
		// rfiEventSupplierDao.delete((RfiEventSupplier) eventSupplier);
		// }
	}

	@Override
	public JasperPrint generateSupplierSummary(RfiEvent event, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer) {
		event = rfiEventDao.getPlainEventById(event.getId());
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
			eventDetails.setType("RFI");
			eventDetails.setTeanantType("Supplier");
			eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");

			List<EvaluationPojo> eventRemider = new ArrayList<EvaluationPojo>();
			if (CollectionUtil.isNotEmpty(event.getRfiReminder())) {
				for (RfiReminder item : event.getRfiReminder()) {
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
			List<IndustryCategoryPojo> ics = new ArrayList<IndustryCategoryPojo>();
			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				LOG.info(" Industry Categories " + event.getIndustryCategories().size());
				for (IndustryCategory category : event.getIndustryCategories()) {
					LOG.info(" 1 ");
					IndustryCategoryPojo ic = new IndustryCategoryPojo();
					ic.setName(category.getName());
					ics.add(ic);
				}
			}
			eventDetails.setCategory(ics);


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
			List<RfiEventContact> eventContacts = rfiEventContactDao.findAllEventContactById(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RfiEventContact contact : eventContacts) {
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
			List<RfiSupplierCqItem> supplierCqItem = supplierCqItemDao.getSupplierCqItemsbySupplierIdAndEventId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			Map<RfiCq, List<RfiSupplierCqItem>> cqList = new LinkedHashMap<RfiCq, List<RfiSupplierCqItem>>();
			for (RfiSupplierCqItem item : supplierCqItem) {
				List<RfiSupplierCqItem> itemList = cqList.get(item.getCq());
				if (itemList == null) {
					itemList = new ArrayList<RfiSupplierCqItem>();
					itemList.add(item);
					cqList.put(item.getCq(), itemList);
				} else {
					itemList.add(item);
				}
			}
			for (Map.Entry<RfiCq, List<RfiSupplierCqItem>> entry : cqList.entrySet()) {
				EvaluationCqPojo cqDetails = new EvaluationCqPojo();
				cqDetails.setName(entry.getKey().getName());
				cqDetails.setDescription(entry.getKey().getDescription());
				List<EvaluationCqItemPojo> cqItemList = new ArrayList<>();
				
				RfiSupplierCq cqStatus = rfiSupplierCqDao.findCqByEventIdAndEventCqId(event.getId(), entry.getKey().getId(), supplierId);
				
				for (RfiSupplierCqItem cqItem : entry.getValue()) {
					String answer = "";
					EvaluationCqItemPojo suppCqItem = new EvaluationCqItemPojo();
					String level = "";
					if (cqItem.getCqItem().getOptional() == Boolean.TRUE) {
						level = "*";
					}

					if (cqItem.getCqItem().getCqType() == CqType.CHOICE || cqItem.getCqItem().getCqType() == CqType.LIST || cqItem.getCqItem().getCqType() == CqType.CHOICE_WITH_SCORE || cqItem.getCqItem().getCqType() == CqType.CHECKBOX) {

						List<String> selectedOptions = new ArrayList<>();
						for (RfiSupplierCqOption supCqOpt : cqItem.getListAnswers()) {
							if (!selectedOptions.contains(supCqOpt.getValue())) {
								selectedOptions.add(supCqOpt.getValue());
							}
						}

						for (RfiCqOption cqOption : cqItem.getCqItem().getCqOptions()) {
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

			// Schedule Of Rate
			List<EvaluationSorPojo> supplierSorList = new ArrayList<EvaluationSorPojo>();
			List<RfiSupplierSor> supplierSorItem = supplierSorItemDao.getAllSorsBySupplierId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (CollectionUtil.isNotEmpty(supplierSorItem)) {
				for (RfiSupplierSor item : supplierSorItem) {
					EvaluationSorPojo bqList = new EvaluationSorPojo();

					bqList.setName(item.getName());
					bqList.setTitle(item.getName());
					bqList.setRemark(StringUtils.checkString(item.getRemark()));
					List<EvaluationSorItemPojo> bqItemList = new ArrayList<EvaluationSorItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getSupplierSorItems())) {
						for (RfiSupplierSorItem bqItem : item.getSupplierSorItems()) {
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
	private List<EvaluationMeetingPojo> summaryMeetingDetails(RfiEvent event, String imgPath, SimpleDateFormat sdf, String supplierId) {
		List<RfiEventMeeting> meetingList = event.getMeetings();
		List<EvaluationMeetingPojo> meetings = new ArrayList<EvaluationMeetingPojo>();
		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RfiEventMeeting meeting : meetingList) {
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
					for (RfiEventMeetingContact mc : meeting.getRfxEventMeetingContacts()) {
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
					for (RfiEventMeetingDocument docs : meeting.getRfxEventMeetingDocument()) {
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
	public RfiEventSupplier saveRfiEventSupplier(RfiEventSupplier rfiEventSupplier) {
		rfiEventSupplier = rfiEventSupplierDao.saveOrUpdate(rfiEventSupplier);
		if (rfiEventSupplier.getSupplier() != null) {
			rfiEventSupplier.getSupplier().getCompanyName();
			rfiEventSupplier.getSupplier().getId();
		}

		if (rfiEventSupplier.getRfxEvent() != null && rfiEventSupplier.getRfxEvent().getCreatedBy() != null && rfiEventSupplier.getRfxEvent().getCreatedBy().getBuyer() != null) {
			rfiEventSupplier.getRfxEvent().getCreatedBy().getBuyer().getCommunicationEmail();
			rfiEventSupplier.getRfxEvent().getTenantId();
			rfiEventSupplier.getRfxEvent().getCreatedBy().getTenantId();
		}

		if (rfiEventSupplier.getRfxEvent() != null && rfiEventSupplier.getRfxEvent().getParticipationFeeCurrency() != null) {
			rfiEventSupplier.getRfxEvent().getParticipationFeeCurrency().getCurrencyCode();
		}

		if (rfiEventSupplier.getRfxEvent() != null && rfiEventSupplier.getRfxEvent().getEventOwner() != null) {
			rfiEventSupplier.getRfxEvent().getEventOwner().getCommunicationEmail();
			rfiEventSupplier.getRfxEvent().getEventOwner().getTenantId();

		}

		if (rfiEventSupplier.getRfxEvent() != null && rfiEventSupplier.getRfxEvent().getTeamMembers() != null) {
			for (RfiTeamMember member : rfiEventSupplier.getRfxEvent().getTeamMembers()) {
				member.getUser().getCommunicationEmail();
				member.getUser().getTenantId();
			}
		}

		return rfiEventSupplier;
	}

	@Override
	public List<RfiEventSupplier> findDisqualifySupplierByEventId(String eventId) {
		return rfiEventSupplierDao.findDisqualifySupplierByEventId(eventId);
	}

	@Override
	public List<FeePojo> getAllInvitedSuppliersByEventId(String eventId) {
		return rfiEventSupplierDao.getAllInvitedSuppliersByEventId(eventId);
	}

	@Override
	public List<RfiEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId) {
		return rfiEventSupplierDao.getAllSuppliersByFeeEventId(eventId, supplierId);
	}

	@Override
	public List<RfiEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String eventId, String supplierCode, String tenantId) {
		return rfiEventSupplierDao.getAllSuppliersByEventIdAndSupplierCode(eventId, supplierCode, tenantId);
	}

	@Override
	public List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input) {
		return rfiEventSupplierDao.getAllEventsSupplierPojoByEventId(eventId, input);
	}

	@Override
	public long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input) {
		return rfiEventSupplierDao.getAllEventsSupplierPojoCountByEventId(eventId, input);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventSuppliersNotificationFlag(String id) {
		rfiEventSupplierDao.updateEventSuppliersNotificationFlag(id);

	}

	@Override
	public List<EventSupplierPojo> getAllDetailsForSendInvitation(String eventId) {
		List<EventSupplierPojo> list = rfiEventSupplierDao.getAllDetailsForSendInvitation(eventId);
		String timeZone = "GMT+8:00";
		for (EventSupplierPojo eventSupplierPojo : list) {
			String supplierTimeZone = supplierSettingsDao.getSupplierTimeZoneByTenantId(eventSupplierPojo.getSupplierId());
			eventSupplierPojo.setTimeZone(StringUtils.checkString(supplierTimeZone).length() > 0 ? supplierTimeZone : timeZone);
		}
		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public EventSupplier saveInvitedSuppliers(RfiEventSupplier eventSupplier) {
		EventSupplier rfiEventSupplier = rfiEventSupplierDao.saveOrUpdate(eventSupplier);
		if (rfiEventSupplier.getSupplier() != null) {
			rfiEventSupplier.getSupplier().getCompanyName();
			rfiEventSupplier.getSupplier().setIndustryCategory(null);
			rfiEventSupplier.getSupplier().setProductCategory(null);
			rfiEventSupplier.getSupplier().setCreatedBy(null);
		}
		return rfiEventSupplier;
	}

	@Override
	public EventDocument findEventDocumentById(String docId) {
		return rfiEventSupplierDao.findeventDocumentById(docId);
	}

	@Override
	public List<RfiEventSupplier> getAllSubmittedSupplierByEventId(String eventId) {
		return rfiEventSupplierDao.getAllSubmittedSupplierByEventId(eventId);
	}

	@Override
	public String getEventNameByEventId(String eventId) {
		return rfiEventSupplierDao.getEventNameByEventId(eventId);
	}
	
	@Override
	public List<RfiEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId) {
		return rfiEventSupplierDao.findDisqualifySupplierForEvaluationReportByEventId(eventId);
	}
	
	@Override
	public List<RfiEventSupplier> findEventSuppliersForTatReportByEventId(String eventId) {
		return rfiEventSupplierDao.findEventSuppliersForTatReportByEventId(eventId);
	}
}
