package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.IndustryCategoryDao;
import com.privasia.procurehere.core.dao.RfaEventMeetingDao;
import com.privasia.procurehere.core.dao.RfiEventMeetingDao;
import com.privasia.procurehere.core.dao.RfpEventMeetingDao;
import com.privasia.procurehere.core.dao.RfqEventMeetingDao;
import com.privasia.procurehere.core.dao.RftEventMeetingDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventSupplier;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventSupplier;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventSupplier;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierTags;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.service.SupplierTagsService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.SupplierEditor;

public class EventInviteSupplierBase {

	protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	SupplierEditor supplierEditor;

	@Autowired
	RftEventMeetingDao rftEventMeetingDao;

	@Autowired
	RfpEventMeetingDao rfpEventMeetingDao;

	@Autowired
	RfqEventMeetingDao rfqEventMeetingDao;

	@Autowired
	RfiEventMeetingDao rfiEventMeetingDao;

	@Autowired
	RfaEventMeetingDao rfaEventMeetingDao;

	@Autowired
	RftEventService eventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	StateService stateService;

	@Autowired
	CountryService countryService;

	@Autowired
	IndustryCategoryDao industryCategoryDao;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	SupplierTagsService supplierTagsService;

	private RfxTypes eventType;

	public EventInviteSupplierBase(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("eventType")
	public RfxTypes getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("step")
	public String getStep() {
		return "3";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(List.class, "inviteSuppliers", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					Supplier group = supplierService.findSuppById(id);
					return group;
				}
				return null;
			}
		});

	}

	/**
	 * @param event
	 * @return
	 */
	protected String doNavigation(Event event) {
		if (Boolean.TRUE == event.getMeetingReq()) {
			return "redirect:meetingList/" + event.getId();
		} else if (Boolean.TRUE == event.getQuestionnaires()) {
			return "redirect:eventCqList/" + event.getId();
		} else if (Boolean.TRUE == event.getBillOfQuantity()) {
			return "redirect:createBQList/" + event.getId();
		} else if (Boolean.TRUE == event.getScheduleOfRate()) {
			return "redirect:createSorList/" + event.getId();
		} else {
			return "redirect:envelopList/" + event.getId();
		}
	}

	/**
	 * @param eventt
	 * @return
	 */
	protected String doNavigationPrevious(Event event) {
		if (event.getDocumentReq()) {
			return "redirect:createEventDocuments/" + event.getId();
		} else {
			return "redirect:eventDescription/" + event.getId();
		}
	}

	/**
	 * @param event
	 * @return
	 */
	protected boolean doValidate(EventSupplier event) {
		boolean isvalid = false;
		switch (getEventType()) {
		case RFA:
			isvalid = rfaEventSupplierService.isExists((RfaEventSupplier) event);
			break;
		case RFI:
			isvalid = rfiEventSupplierService.isExists((RfiEventSupplier) event);
			break;
		case RFP:
			isvalid = rfpEventSupplierService.isExists((RfpEventSupplier) event);
			break;
		case RFQ:
			isvalid = rfqEventSupplierService.isExists((RfqEventSupplier) event);
			break;
		case RFT:
			isvalid = rftEventSupplierService.isExists((RftEventSupplier) event);
			break;
		default:
			break;
		}

		return isvalid;
	}

	/**
	 * @param search
	 * @param eventId
	 * @return
	 */
	protected List<EventSupplier> removeSupplier(String search, String eventId) throws ApplicationException {
		List<EventSupplier> supplierList = null;
		EventSupplier eventsupplier = null;
		switch (getEventType()) {
		case RFA: {
			eventsupplier = rfaEventSupplierService.findSupplierById(search);
			Integer count = rfaEventMeetingDao.countMeetingsForSupplier(eventsupplier.getSupplier().getId(), ((RfaEventSupplier) eventsupplier).getRfxEvent().getId());
			if (count == 0) {
				rfaEventSupplierService.deleteRfaEventSuppliers((RfaEventSupplier) eventsupplier);
				// supplierList = rfaEventSupplierService.getAllSuppliersByEventId(((RfaEventSupplier)
				// eventsupplier).getRfxEvent().getId());
			} else {
				LOG.error("Please remove this supplier from meeting invitation, before removing supplier from Event");
				throw new ApplicationException("Please remove supplier " + eventsupplier.getSupplier().getCompanyName() + " from meeting invitation, before removing from Event");
			}
			break;
		}
		case RFI: {
			eventsupplier = rfiEventSupplierService.findSupplierById(search);
			Integer count = rfiEventMeetingDao.countMeetingsForSupplier(eventsupplier.getSupplier().getId(), ((RfiEventSupplier) eventsupplier).getRfxEvent().getId());
			if (count == 0) {
				rfiEventSupplierService.deleteRfiEventSuppliers((RfiEventSupplier) eventsupplier);
				// supplierList = rfiEventSupplierService.getAllSuppliersByEventId(((RfiEventSupplier)
				// eventsupplier).getRfxEvent().getId());
			} else {
				LOG.error("Please remove this supplier from meeting invitation, before removing supplier from Event");
				throw new ApplicationException("Please remove supplier " + eventsupplier.getSupplier().getCompanyName() + " from meeting invitation, before removing from Event");
			}
			break;
		}
		case RFP: {
			eventsupplier = rfpEventSupplierService.findSupplierById(search);
			Integer count = rfpEventMeetingDao.countMeetingsForSupplier(eventsupplier.getSupplier().getId(), ((RfpEventSupplier) eventsupplier).getRfxEvent().getId());
			if (count == 0) {
				rfpEventSupplierService.deleteRfpEventSuppliers((RfpEventSupplier) eventsupplier);
				// supplierList = rfpEventSupplierService.getAllSuppliersByEventId(((RfpEventSupplier)
				// eventsupplier).getRfxEvent().getId());
			} else {
				LOG.error("Please remove this supplier from meeting invitation, before removing supplier from Event");
				throw new ApplicationException("Please remove supplier " + eventsupplier.getSupplier().getCompanyName() + " from meeting invitation, before removing from Event");
			}
			break;
		}
		case RFQ: {
			eventsupplier = rfqEventSupplierService.findSupplierById(search);
			Integer count = rfqEventMeetingDao.countMeetingsForSupplier(eventsupplier.getSupplier().getId(), ((RfqEventSupplier) eventsupplier).getRfxEvent().getId());
			if (count == 0) {
				rfqEventSupplierService.deleteRfqEventSuppliers((RfqEventSupplier) eventsupplier);
				// supplierList = rfqEventSupplierService.getAllSuppliersByEventId(((RfqEventSupplier)
				// eventsupplier).getRfxEvent().getId());
			} else {
				LOG.error("Please remove this supplier from meeting invitation, before removing supplier from Event");
				throw new ApplicationException("Please remove supplier " + eventsupplier.getSupplier().getCompanyName() + " from meeting invitation, before removing from Event");
			}
			break;
		}
		case RFT: {
			eventsupplier = rftEventSupplierService.findSupplierById(search);
			Integer count = rftEventMeetingDao.countMeetingsForSupplier(eventsupplier.getSupplier().getId(), ((RftEventSupplier) eventsupplier).getRfxEvent().getId());
			if (count == 0) {
				rftEventSupplierService.deleteRftEventSuppliers((RftEventSupplier) eventsupplier);
				// supplierList = rftEventSupplierService.getAllSuppliersByEventId(((RftEventSupplier)
				// eventsupplier).getRfxEvent().getId());
			} else {
				LOG.error("Please remove this supplier from meeting invitation, before removing supplier from Event");
				throw new ApplicationException("Please remove supplier " + eventsupplier.getSupplier().getCompanyName() + " from meeting invitation, before removing from Event");
			}
			break;
		}
		default:
			break;
		}

		return supplierList;
	}

	/**
	 * @param search
	 * @param eventId
	 * @return
	 */
	protected List<EventSupplierPojo> searchSuppliers(String search, String eventId) {
		Boolean filterByIndustryCategory = rfxTemplateService.findTemplateIndustryCategoryFlagByEventId(eventId, getEventType());
		if (filterByIndustryCategory == null) {
			filterByIndustryCategory = Boolean.FALSE;
		}

		return favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategory(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, eventId, getEventType(), search);
	}

	/**
	 * @param model
	 * @param eventId
	 * @param modelsupplierList
	 * @param eventSupplierList
	 * @param eventType
	 * @param rfxEvent
	 * @param eventId
	 * @throws JsonProcessingException
	 */
	protected void buildModel(Model model, List<EventSupplier> eventSupplierList, RfxTypes eventType, Event rfxEvent) throws JsonProcessingException {
		List<EventSupplierPojo> supplierList = new ArrayList<EventSupplierPojo>();
		Boolean filterByIndustryCategory = rfxTemplateService.findTemplateIndustryCategoryFlagByEventId(rfxEvent.getId(), getEventType());

		if (filterByIndustryCategory == null) {
			filterByIndustryCategory = Boolean.FALSE;
		}

		switch (eventType) {
		case RFA:
			RfaEvent rfaEvent = (RfaEvent) rfxEvent;
			if (rfaEvent.getTemplate() != null && Boolean.TRUE == rfaEvent.getTemplate().getAutoPopulateSupplier()) {
				List<EventSupplierPojo> supplierAllList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategoryForAutoSave(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, rfaEvent.getId(), eventType, null);
				if (CollectionUtil.isNotEmpty(supplierAllList)) {
					List<RfaEventSupplier> eventSuppliers = new ArrayList<RfaEventSupplier>();
					for (EventSupplierPojo supplier : supplierAllList) {
						RfaEventSupplier rfqEventSupplier = new RfaEventSupplier();
						rfqEventSupplier.setSupplier(new Supplier(supplier.getId()));
						rfqEventSupplier.setSupplierInvitedTime(new Date());
						rfqEventSupplier.setRfxEvent((RfaEvent) rfxEvent);
						rfqEventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
						eventSuppliers.add(rfqEventSupplier);
						// addAutoSupplier(supplier, rfaEvent, eventType);
					}
					rfaEventSupplierService.batchInsert(eventSuppliers);
				}
			} else {
				supplierList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategory(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, rfaEvent.getId(), eventType, null);
			}
			break;
		case RFI:
			RfiEvent rfiEvent = (RfiEvent) rfxEvent;
			if (rfiEvent.getTemplate() != null && Boolean.TRUE == rfiEvent.getTemplate().getAutoPopulateSupplier()) {
				List<EventSupplierPojo> supplierAllList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategoryForAutoSave(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, rfiEvent.getId(), eventType, null);
				if (CollectionUtil.isNotEmpty(supplierAllList)) {
					List<RfiEventSupplier> eventSuppliers = new ArrayList<RfiEventSupplier>();
					for (EventSupplierPojo supplier : supplierAllList) {
						RfiEventSupplier eventSupplier = new RfiEventSupplier();
						eventSupplier.setSupplier(new Supplier(supplier.getId()));
						eventSupplier.setSupplierInvitedTime(new Date());
						eventSupplier.setRfxEvent((RfiEvent) rfxEvent);
						eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
						eventSuppliers.add(eventSupplier);
					}
					rfiEventSupplierService.batchInsert(eventSuppliers);

					// for (EventSupplierPojo supplier : supplierAllList) {
					// addAutoSupplier(supplier, rfiEvent, eventType);
					// }
				}
			} else {
				supplierList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategory(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, rfiEvent.getId(), eventType, null);
			}
			break;
		case RFP:
			RfpEvent rfpEvent = (RfpEvent) rfxEvent;
			if (rfpEvent.getTemplate() != null && Boolean.TRUE == rfpEvent.getTemplate().getAutoPopulateSupplier()) {
				List<EventSupplierPojo> supplierAllList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategoryForAutoSave(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, rfpEvent.getId(), eventType, null);
				if (CollectionUtil.isNotEmpty(supplierAllList)) {
					List<RfpEventSupplier> eventSuppliers = new ArrayList<RfpEventSupplier>();
					for (EventSupplierPojo supplier : supplierAllList) {
						RfpEventSupplier eventSupplier = new RfpEventSupplier();
						eventSupplier.setSupplier(new Supplier(supplier.getId()));
						eventSupplier.setSupplierInvitedTime(new Date());
						eventSupplier.setRfxEvent((RfpEvent) rfxEvent);
						eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
						eventSuppliers.add(eventSupplier);
					}
					rfpEventSupplierService.batchInsert(eventSuppliers);

					// for (EventSupplierPojo supplier : supplierAllList) {
					// addAutoSupplier(supplier, rfpEvent, eventType);
					// }
				}
			} else {
				supplierList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategory(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, rfpEvent.getId(), eventType, null);
			}
			break;
		case RFQ:
			RfqEvent rfqEvent = (RfqEvent) rfxEvent;
			if (rfqEvent.getTemplate() != null && Boolean.TRUE == rfqEvent.getTemplate().getAutoPopulateSupplier()) {
				List<EventSupplierPojo> supplierAllList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategoryForAutoSave(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, rfqEvent.getId(), eventType, null);
				if (CollectionUtil.isNotEmpty(supplierAllList)) {
					List<RfqEventSupplier> eventSuppliers = new ArrayList<RfqEventSupplier>();
					for (EventSupplierPojo supplier : supplierAllList) {
						RfqEventSupplier rfqEventSupplier = new RfqEventSupplier();
						rfqEventSupplier.setSupplier(new Supplier(supplier.getId()));
						rfqEventSupplier.setSupplierInvitedTime(new Date());
						rfqEventSupplier.setRfxEvent((RfqEvent) rfxEvent);
						rfqEventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
						eventSuppliers.add(rfqEventSupplier);
					}
					rfqEventSupplierService.batchInsert(eventSuppliers);
				}
			} else {
				supplierList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategory(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, rfqEvent.getId(), eventType, null);
			}
			break;
		case RFT:
			RftEvent rftEvent = (RftEvent) rfxEvent;
			if (rftEvent.getTemplate() != null && Boolean.TRUE == rftEvent.getTemplate().getAutoPopulateSupplier()) {
				List<EventSupplierPojo> supplierAllList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategoryForAutoSave(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, rftEvent.getId(), eventType, null);
				if (CollectionUtil.isNotEmpty(supplierAllList)) {
					List<RftEventSupplier> eventSuppliers = new ArrayList<RftEventSupplier>();
					for (EventSupplierPojo supplier : supplierAllList) {
						RftEventSupplier eventSupplier = new RftEventSupplier();
						eventSupplier.setSupplier(new Supplier(supplier.getId()));
						eventSupplier.setSupplierInvitedTime(new Date());
						eventSupplier.setRfxEvent((RftEvent) rfxEvent);
						eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
						eventSuppliers.add(eventSupplier);
					}
					rftEventSupplierService.batchInsert(eventSuppliers);

					// for (EventSupplierPojo supplier : supplierAllList) {
					// addAutoSupplier(supplier, rftEvent, eventType);
					// }
				}
			} else {
				supplierList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategory(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, rftEvent.getId(), eventType, null);
			}
			break;
		default:
			break;
		}
		List<IndustryCategory> industryCategoryList = industryCategoryDao.getAllIndustryCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("industryCategory", industryCategoryList);
		Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("countryStates", buyer.getRegistrationOfCountry() != null ? stateService.statesForCountry(buyer.getRegistrationOfCountry().getId()) : null);
		model.addAttribute("buyer", buyer);
		model.addAttribute("sppliers", supplierList);
		model.addAttribute("supplierSearchPojo", new SupplierSearchPojo());
		List<SupplierTags> supplierTagsList = supplierTagsService.searchAllActiveSupplierTagsForTenant(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("supplierTagsList", supplierTagsList);
		model.addAttribute("coverageList", stateService.retrieveAllStatesByCountry());
	}

	@ModelAttribute("registeredCountry")
	public List<Country> populateCountries() {
		return countryService.findAllActiveCountries();
	}

	@SuppressWarnings("unused")
	private void addAutoSupplier(EventSupplierPojo supplier, Event rfxEvent, RfxTypes type) {
		if (StringUtils.checkString(supplier.getId()).length() > 0) {
			switch (type) {
			case RFA: {
				RfaEventSupplier rfaEventSupplier = new RfaEventSupplier();
				rfaEventSupplier.setSupplier(new Supplier(supplier.getId()));
				rfaEventSupplier.setSupplierInvitedTime(new Date());
				rfaEventSupplier.setRfxEvent((RfaEvent) rfxEvent);
				// if (!doValidate(rfaEventSupplier)) {
				rfaEventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
				rfaEventSupplier = rfaEventSupplierService.saveRfaEventSupplier(rfaEventSupplier);
				// } else {
				// LOG.info("Event Supplier already exists");
				// }
				break;
			}
			case RFI: {
				RfiEventSupplier rfqEventSupplier = new RfiEventSupplier();
				rfqEventSupplier.setSupplier(new Supplier(supplier.getId()));
				rfqEventSupplier.setSupplierInvitedTime(new Date());
				rfqEventSupplier.setRfxEvent((RfiEvent) rfxEvent);
				if (!doValidate(rfqEventSupplier)) {
					rfqEventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
					rfqEventSupplier = rfiEventSupplierService.saveRfiEventSupplier(rfqEventSupplier);
				} else {
					LOG.info("Event Supplier already exists");
				}
				break;
			}
			case RFP: {
				RfpEventSupplier rfpEventSupplier = new RfpEventSupplier();
				rfpEventSupplier.setSupplier(new Supplier(supplier.getId()));
				rfpEventSupplier.setSupplierInvitedTime(new Date());
				rfpEventSupplier.setRfxEvent((RfpEvent) rfxEvent);
				if (!doValidate(rfpEventSupplier)) {
					rfpEventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
					rfpEventSupplier = rfpEventSupplierService.saveRfpEventSupplier(rfpEventSupplier);
				} else {
					LOG.info("Event Supplier already exists");
				}

				break;
			}
			case RFQ: {
				RfqEventSupplier rfqEventSupplier = new RfqEventSupplier();
				rfqEventSupplier.setSupplier(new Supplier(supplier.getId()));
				rfqEventSupplier.setSupplierInvitedTime(new Date());
				rfqEventSupplier.setRfxEvent((RfqEvent) rfxEvent);
				if (!doValidate(rfqEventSupplier)) {
					rfqEventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
					rfqEventSupplier = rfqEventSupplierService.saveRfqEventSupplier(rfqEventSupplier);
				} else {
					LOG.info("Event Supplier already exists");
				}
				break;
			}
			case RFT: {
				RftEventSupplier rftEventSupplier = new RftEventSupplier();
				rftEventSupplier.setSupplier(new Supplier(supplier.getId()));
				rftEventSupplier.setSupplierInvitedTime(new Date());
				rftEventSupplier.setRfxEvent((RftEvent) rfxEvent);
				if (!doValidate(rftEventSupplier)) {
					rftEventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
					rftEventSupplier = rftEventSupplierService.saveRftEventSupplier(rftEventSupplier);
				} else {
					LOG.info("Event Supplier already exists");
				}
				break;
			}
			default:
				break;

			}
		}

	}

	protected List<EventSupplier> setNullSupplierObject(List<EventSupplier> eventSupplierList) {
		List<EventSupplier> returnList = null;
		if (CollectionUtil.isNotEmpty(eventSupplierList)) {
			returnList = new ArrayList<EventSupplier>();
			for (EventSupplier supplier : eventSupplierList) {
				returnList.add(supplier.createShallowCopy());
			}
		}
		return returnList;
	}

	@RequestMapping(value = "/getEventSuppliers/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<EventSupplierPojo>> buyerSuspendedList(TableDataInput input, @PathVariable("eventId") String eventId) {
		TableData<EventSupplierPojo> data = null;
		long totalCount = 0;
		try {
			switch (getEventType()) {
			case RFA:
				data = new TableData<EventSupplierPojo>(rfaEventSupplierService.getAllEventsSupplierPojoByEventId(eventId, input));
				totalCount = rfaEventSupplierService.getAllEventsSupplierPojoCountByEventId(eventId, input);
				break;
			case RFI:
				data = new TableData<EventSupplierPojo>(rfiEventSupplierService.getAllEventsSupplierPojoByEventId(eventId, input));
				totalCount = rfiEventSupplierService.getAllEventsSupplierPojoCountByEventId(eventId, input);
				break;
			case RFP:
				data = new TableData<EventSupplierPojo>(rfpEventSupplierService.getAllEventsSupplierPojoByEventId(eventId, input));
				totalCount = rfpEventSupplierService.getAllEventsSupplierPojoCountByEventId(eventId, input);
				break;
			case RFQ:
				data = new TableData<EventSupplierPojo>(rfqEventSupplierService.getAllEventsSupplierPojoByEventId(eventId, input));
				totalCount = rfqEventSupplierService.getAllEventsSupplierPojoCountByEventId(eventId, input);
				break;
			case RFT:
				data = new TableData<EventSupplierPojo>(rftEventSupplierService.getAllEventsSupplierPojoByEventId(eventId, input));
				totalCount = rftEventSupplierService.getAllEventsSupplierPojoCountByEventId(eventId, input);
				break;
			default:
				break;

			}
			data.setDraw(input.getDraw());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading supplier list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<EventSupplierPojo>>(data, HttpStatus.OK);
	}

	// PH-891
	@RequestMapping(value = "/removeSupplier/{eventId}", method = RequestMethod.POST)
	public ResponseEntity<Void> removeSelectedSupplier(@PathVariable String eventId, @RequestParam("selectedSuppIds[]") String[] selectedSuppIds, Model model) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		if (selectedSuppIds != null && selectedSuppIds.length > 0) {
			for (int i = 0; i < selectedSuppIds.length; i++) {
				if(StringUtils.checkString(selectedSuppIds[i]).length() == 0) {
					continue;
				}
				try {
					removeSupplier(selectedSuppIds[i], eventId);
					headers.add("success", "Event Supplier removed successfully");
				} catch (ApplicationException e) {
					LOG.error("Error while removing Supplier from event . " + e.getMessage(), e);
					headers.add("error", e.getMessage());
					return new ResponseEntity<Void>(null, headers, HttpStatus.BAD_REQUEST);
				} catch (Exception e) {
					LOG.error("Error while removing Supplier from event . " + e.getMessage(), e);
					headers.add("error", "Event Supplier removed unsuccessfull");
					return new ResponseEntity<Void>(null, headers, HttpStatus.BAD_REQUEST);
				}
			}
			return new ResponseEntity<Void>(null, headers, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(null, headers, HttpStatus.OK);

	}

}
