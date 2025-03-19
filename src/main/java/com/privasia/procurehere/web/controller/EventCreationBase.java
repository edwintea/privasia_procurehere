package com.privasia.procurehere.web.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.entity.BuyerPackage;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RfaApprovalUser;
import com.privasia.procurehere.core.entity.RfaEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventContact;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfaSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.RfaUnMaskedUser;
import com.privasia.procurehere.core.entity.RfiEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventContact;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfiEventSupplier;
import com.privasia.procurehere.core.entity.RfiEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfiSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfiTeamMember;
import com.privasia.procurehere.core.entity.RfiUnMaskedUser;
import com.privasia.procurehere.core.entity.RfpApprovalUser;
import com.privasia.procurehere.core.entity.RfpEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventContact;
import com.privasia.procurehere.core.entity.RfpEventMeeting;
import com.privasia.procurehere.core.entity.RfpEventSupplier;
import com.privasia.procurehere.core.entity.RfpEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfpSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.RfpUnMaskedUser;
import com.privasia.procurehere.core.entity.RfqApprovalUser;
import com.privasia.procurehere.core.entity.RfqEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventContact;
import com.privasia.procurehere.core.entity.RfqEventMeeting;
import com.privasia.procurehere.core.entity.RfqEventSupplier;
import com.privasia.procurehere.core.entity.RfqEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfqSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfqTeamMember;
import com.privasia.procurehere.core.entity.RfqUnMaskedUser;
import com.privasia.procurehere.core.entity.RftApprovalUser;
import com.privasia.procurehere.core.entity.RftEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventContact;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.RftEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RftSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.RftUnMaskedUser;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.DeclarationType;
import com.privasia.procurehere.core.enums.DurationType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.IntervalType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.BuyerAddressPojo;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.DeclarationService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.GroupCodeService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.ProcurementCategoriesService;
import com.privasia.procurehere.service.ProcurementMethodService;
import com.privasia.procurehere.service.RfaEventMeetingService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfiEventMeetingService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfpEventMeetingService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfqEventMeetingService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RftEventMeetingService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import com.privasia.procurehere.web.editors.BuyerAddressEditor;
import com.privasia.procurehere.web.editors.CostCenterEditor;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.DeclarationEditor;
import com.privasia.procurehere.web.editors.GroupCodeEditor;
import com.privasia.procurehere.web.editors.IndustryCategoryEditor;
import com.privasia.procurehere.web.editors.RfaEvaluationConclusionUserEditor;
import com.privasia.procurehere.web.editors.RfaUnMaskedUserEditor;
import com.privasia.procurehere.web.editors.RfiEvaluationConclusionUserEditor;
import com.privasia.procurehere.web.editors.RfiUnMaskedUserEditor;
import com.privasia.procurehere.web.editors.RfpEvaluationConclusionUserEditor;
import com.privasia.procurehere.web.editors.RfpUnMaskedUserEditor;
import com.privasia.procurehere.web.editors.RfqEvaluationConclusionUserEditor;
import com.privasia.procurehere.web.editors.RfqUnMaskedUserEditor;
import com.privasia.procurehere.web.editors.RftEvaluationConclusionUserEditor;
import com.privasia.procurehere.web.editors.RftEventEditor;
import com.privasia.procurehere.web.editors.RftUnMaskedUserEditor;
import com.privasia.procurehere.web.editors.StateEditor;
import com.privasia.procurehere.web.editors.TemplateEditor;
import com.privasia.procurehere.web.editors.UserEditor;

import freemarker.template.Configuration;

public class EventCreationBase {

	protected static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	IndustryCategoryEditor industryCategoryEditor;

	@Autowired
	RftEventEditor rftEventEditor;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	CostCenterEditor costCenterEditor;

	@Autowired
	StateEditor stateEditor;

	@Autowired
	UserEditor userEditor;

	@Autowired
	TemplateEditor templateEditor;

	@Autowired
	BuyerAddressEditor buyerAddressEditor;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	EventIdSettingsDao eventIdSettingDao;

	@Resource
	MessageSource messageSource;

	@Autowired
	CountryService countryService;

	@Autowired
	StateService stateService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	UserService userService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	BusinessUnitEditor businessUnitEditor;
	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	RfiUnMaskedUserEditor rfiUnMaskedUserEditor;

	@Autowired
	RfqUnMaskedUserEditor rfqUnMaskedUserEditor;

	@Autowired
	RfpUnMaskedUserEditor rfpUnMaskedUserEditor;

	@Autowired
	RftUnMaskedUserEditor rftUnMaskedUserEditor;

	@Autowired
	RfaUnMaskedUserEditor rfaUnMaskedUserEditor;

	@Autowired
	RfiEvaluationConclusionUserEditor rfiEvaluationConclusionUserEditor;

	@Autowired
	RfqEvaluationConclusionUserEditor rfqEvaluationConclusionUserEditor;

	@Autowired
	RfpEvaluationConclusionUserEditor rfpEvaluationConclusionUserEditor;

	@Autowired
	RftEvaluationConclusionUserEditor rftEvaluationConclusionUserEditor;

	@Autowired
	RfaEvaluationConclusionUserEditor rfaEvaluationConclusionUserEditor;

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
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	RftEventMeetingService rftEventMeetingService;

	@Autowired
	RfaEventMeetingService rfaEventMeetingService;

	@Autowired
	RfpEventMeetingService rfpEventMeetingService;

	@Autowired
	RfqEventMeetingService rfqEventMeetingService;

	@Autowired
	RfiEventMeetingService rfiEventMeetingService;

	@Autowired
	DeclarationService declarationService;

	@Autowired
	DeclarationEditor declarationEditor;

	@Autowired
	ProcurementMethodService procurementMethodService;

	@Autowired
	ProcurementCategoriesService procurementCategoriesService;

	@Autowired
	GroupCodeService groupCodeService;

	@Autowired
	GroupCodeEditor groupCodeEditor;
	
	@Autowired
	TatReportService tatReportService;

	private RfxTypes eventType;

	public EventCreationBase(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("TeamMemberTypeList")
	public List<TeamMemberType> getTeamMemberTypeList() {
		return Arrays.asList(TeamMemberType.values());
	}

	@ModelAttribute("step")
	public String getStep() {
		return "1";
	}

	@ModelAttribute("eventType")
	public RfxTypes getEventType() {
		return eventType;
	}

	@ModelAttribute("intervalType")
	public List<IntervalType> getIntervalType() {
		return Arrays.asList(IntervalType.values());
	}

	@ModelAttribute("durationType")
	public List<DurationType> getDurationType() {
		return Arrays.asList(DurationType.values());
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(IndustryCategory.class, "industryCategory", industryCategoryEditor);
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(CostCenter.class, costCenterEditor);
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		binder.registerCustomEditor(State.class, stateEditor);
		binder.registerCustomEditor(Country.class, countryEditor);
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(RfiUnMaskedUser.class, rfiUnMaskedUserEditor);
		binder.registerCustomEditor(RfqUnMaskedUser.class, rfqUnMaskedUserEditor);
		binder.registerCustomEditor(RfpUnMaskedUser.class, rfpUnMaskedUserEditor);
		binder.registerCustomEditor(RftUnMaskedUser.class, rftUnMaskedUserEditor);
		binder.registerCustomEditor(RfaUnMaskedUser.class, rfaUnMaskedUserEditor);

		binder.registerCustomEditor(RfiEvaluationConclusionUser.class, rfiEvaluationConclusionUserEditor);
		binder.registerCustomEditor(RfqEvaluationConclusionUser.class, rfqEvaluationConclusionUserEditor);
		binder.registerCustomEditor(RfpEvaluationConclusionUser.class, rfpEvaluationConclusionUserEditor);
		binder.registerCustomEditor(RftEvaluationConclusionUser.class, rftEvaluationConclusionUserEditor);
		binder.registerCustomEditor(RfaEvaluationConclusionUser.class, rfaEvaluationConclusionUserEditor);

		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);

		// Formatter for Big Decimal
		NumberStyleFormatter numberFormatter = new NumberStyleFormatter();
		numberFormatter.setPattern("#,###,###,###.####");
		binder.addCustomFormatter(numberFormatter, BigDecimal.class);

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}

		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		timeFormat.setTimeZone(timeZone);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		format.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "eventPublishTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "eventPublishDate", new CustomDateEditor(format, true));
		binder.registerCustomEditor(Date.class, "deliveryDate", new CustomDateEditor(format, true));
		// binder.registerCustomEditor(Date.class, "eventStart", new
		// CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy
		// hh:mm a"), true));
		binder.registerCustomEditor(BuyerAddress.class, buyerAddressEditor);
		binder.registerCustomEditor(RfxTemplate.class, templateEditor);
		binder.registerCustomEditor(Declaration.class, declarationEditor);
		binder.registerCustomEditor(GroupCode.class, groupCodeEditor);

	}

	/**
	 * @param model
	 * @param event
	 * @param eventType
	 * @param settings 
	 * @throws JsonProcessingException
	 */
	public void constructDefaultModel(Model model, Event event, RfxTypes eventType, BuyerSettings settings) throws JsonProcessingException {
		if (event != null && (null == event.getEventDetailCompleted() || (event.getEventDetailCompleted() != null && event.getEventDetailCompleted() == Boolean.FALSE))) {
			if (settings == null) {
				settings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (settings != null) {
					if (event.getBaseCurrency() == null) {
						event.setBaseCurrency(settings.getCurrency());
					}
					if (event.getDecimal() == null) {
						event.setDecimal(settings.getDecimal());
					}
				}
			}
		}

		model.addAttribute("currency", currencyService.getAllActiveCurrencyCode());

		LOG.info("View Supplier  : " + event.getViewSupplerName());
		model.addAttribute("event", event);
		model.addAttribute("intervalType", IntervalType.values());
		TableDataInput filter = new TableDataInput();
		filter.setStart(1);
		filter.setLength(10);
		model.addAttribute("industryCategoryList", industryCategoryService.findIndustryCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), filter));
		// delivery address
		if (event != null && event.getDeliveryAddress() != null) {
			BuyerAddress buyerAddress = buyerAddressService.getBuyerAddress(event.getDeliveryAddress().getId());
			model.addAttribute("buyerAddress", buyerAddress);
		}
		List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("addressList", addressList);
		// model.addAttribute("costCenter",
		// costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
		List<User> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("userList1", userList);
		List<User> userList2 = userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("userList2", userList2);
		model.addAttribute("isIdSettingOn", eventIdSettingsDao.isBusinessSettingEnable(SecurityLibrary.getLoggedInUserTenantId(), getEventType().name()));

		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isEnableUnitAndCostCorrelation", enableCorrelation);
		if (event.getBusinessUnit() != null && enableCorrelation == Boolean.TRUE) {
			if (event.getBusinessUnit().getStatus() == Status.ACTIVE) {
				List<CostCenterPojo> assignedCostList =  businessUnitService.getCostCentersByBusinessUnitId(event.getBusinessUnit().getId(), Status.ACTIVE);
				
//				List<String> assignedCostId = costCenterService.getCostCenterByBusinessId(event.getBusinessUnit().getId());
//				if (CollectionUtil.isNotEmpty(assignedCostId)) {
//					for (String assignedCost : assignedCostId) {
//						CostCenterPojo cost = costCenterService.getCostCenterByCostId(assignedCost);
//						if (cost.getStatus() == Status.ACTIVE) {
//							assignedCostList.add(cost);
//						}
//					}
//				}
				model.addAttribute("costCenter", assignedCostList);
			} else {
				model.addAttribute("costCenter", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			}
		} else {
			model.addAttribute("costCenter", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		}

		Boolean enableBUAndGroupCodeCorrelation = buyerSettingsService.isEnableUnitAndGroupCodeCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isEnableBUAndGPCCorr", enableBUAndGroupCodeCorrelation);
		List<GroupCode> groupCodeList = groupCodeService.fetchAllActiveGroupCodeForTenantID(SecurityLibrary.getLoggedInUserTenantId());
		if (event.getBusinessUnit() != null && enableBUAndGroupCodeCorrelation == Boolean.TRUE) {
			groupCodeList = groupCodeService.getGroupCodeIdByBusinessId(event.getBusinessUnit().getId());
			if (event.getGroupCode() != null) {
				if (Status.INACTIVE == event.getGroupCode().getStatus() && !groupCodeList.contains(event.getGroupCode())) {
					groupCodeList.add(event.getGroupCode());
				}
			}

		}
		model.addAttribute("groupCodeList", groupCodeList);

		if (event.getUnMaskedUser() != null) {
			model.addAttribute("unMaskedUser", event.getUnMaskedUser());
		}
		constructDatabaseEventCategories(model, event, eventType);
	}

	/**
	 * @param search
	 * @return
	 */
	@RequestMapping(value = "searchCategory", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<IndustryCategory>> searchCategory(@RequestParam("search") String search) {
		String tenantId = SecurityLibrary.getLoggedInUserTenantId();
		List<IndustryCategory> categoryList = industryCategoryService.findIndustryCategoryByNameAndTenantId(search, tenantId);
		return new ResponseEntity<List<IndustryCategory>>(categoryList, HttpStatus.OK);
	}

	/**
	 * @param persistObj
	 * @return
	 */
	protected String doNavigation(Event persistObj) {
		if (Boolean.TRUE == persistObj.getDocumentReq()) {
			return "redirect:createEventDocuments/" + persistObj.getId();
		} else if (persistObj.getEventVisibility() != EventVisibilityType.PUBLIC) {
			return "redirect:addSupplier/" + persistObj.getId();
		} else if (Boolean.TRUE == persistObj.getMeetingReq()) {
			return "redirect:meetingList/" + persistObj.getId();
		} else if (Boolean.TRUE == persistObj.getQuestionnaires()) {
			return "redirect:eventCqList/" + persistObj.getId();
		} else if (Boolean.TRUE == persistObj.getBillOfQuantity()) {
			return "redirect:createBQList/" + persistObj.getId();
		} else {
			return "redirect:envelopList/" + persistObj.getId();
		}
	}

	/**
	 * @param event
	 * @return
	 */
	public String eventCreationPrevious(Event event) {

		if (event == null || (event != null && event.getId().length() == 0)) {
			LOG.info("Error : No event found for given id  - " + event.getId());
			return "redirect:/400_error";
		}
		return "redirect:createEventDetails/" + event.getId();
	}

	/**
	 * @param eventId
	 * @param
	 * @return
	 * @throws SubscriptionException
	 */
	public String editEventDetails(@RequestParam String eventId) throws SubscriptionException {
		LOG.info("Event edit Request started");
		// Check subscription limit
		Buyer buyer = buyerService.findBuyerByIdWithBuyerPackage(SecurityLibrary.getLoggedInUser().getBuyer().getId());

		if (buyer != null && buyer.getBuyerPackage() != null) {
			BuyerPackage bp = buyer.getBuyerPackage();
			if (bp.getPlan() != null && bp.getPlan().getPlanType() == PlanType.PER_USER) {
				if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
					throw new SubscriptionException("Your Subscription has Expired.");
				}
			}
		}

		Integer count = null;
		switch (getEventType()) {
		case RFA:
			count = rfaEventService.getCountByEventId(eventId);
			break;
		case RFI:
			count = rfiEventService.getCountByEventId(eventId);
			break;
		case RFP:
			count = rfpEventService.getCountByEventId(eventId);
			break;
		case RFQ:
			count = rfqEventService.getCountByEventId(eventId);
			break;
		case RFT:
			count = rftEventService.getCountByEventId(eventId);
			break;
		default:
			break;
		}

		if (count == null || (count != null && count == 0)) {
			LOG.info("Error : No event found for given id  - " + eventId);
			return "redirect:/400_error";
		}
		LOG.info("Event edit Request end");
		return "redirect:createEventDetails/" + eventId;
	}

	/**
	 * @param model
	 * @param eventId
	 * @return
	 */
	public String createEventDetails(Model model, String eventId, HttpSession session) {
		LOG.info("Getting Event details.....................");
		// List<IndustryCategory> industryCategoryList =
		// userService.getEventContactByIdByTenantId("");
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}

		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}

		Event event = null;
		List<User> assignedTeamMembers = new ArrayList<>();

		try {
			List<User> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("Got User List .........................");
			model.addAttribute("userList1", userList);
			List<User> userList2 = userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("userList2", userList2);
			switch (getEventType()) {
			case RFA:

				event = rfaEventService.loadRfaEventById(eventId);
				if (!(event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED)) {
					LOG.info("==========event status not in draft======================");
					return "redirect:/buyer/RFA/eventSummary/" + event.getId();
				}

				/*
				 * assignedEditors = ((RfaEvent) event).getEventEditors(); assignedViewers = ((RfaEvent)
				 * event).getEventViewers();
				 */
				for (RfaTeamMember rfaTeamMember : ((RfaEvent) event).getTeamMembers()) {
					assignedTeamMembers.add((User) rfaTeamMember.getUser().clone());
				}

				List<RfaApprovalUser> rfaApprovalUserList = new ArrayList<RfaApprovalUser>();
				for (User user : userList) {
					rfaApprovalUserList.add(new RfaApprovalUser(user));
				}
				model.addAttribute("userList", rfaApprovalUserList);

				Date eventStartDate = event.getEventStart();
				Date eventEndDate = event.getEventEnd();

				Date eventPublishDate = event.getEventPublishDate();
				((RfaEvent) (event)).setEventStartTime(eventStartDate);
				((RfaEvent) (event)).setEventEndTime(eventEndDate);
				((RfaEvent) (event)).setEventPublishTime(eventPublishDate);
				if (((RfaEvent) (event)).getMinimumSupplierRating() != null) {
					((RfaEvent) (event)).setMinimumSupplierRating(((RfaEvent) (event)).getMinimumSupplierRating());
				}
				if (((RfaEvent) (event)).getMaximumSupplierRating() != null) {
					((RfaEvent) (event)).setMaximumSupplierRating(((RfaEvent) (event)).getMaximumSupplierRating());
				}
				RfaEventContact eventAContact = new RfaEventContact();
				eventAContact.setRfaEvent((RfaEvent) event);
				eventAContact.setEventId(event.getId());
				model.addAttribute("eventContact", eventAContact);
				model.addAttribute("eventContactsList", ((RfaEvent) event).getEventContacts());
				model.addAttribute("reminderList", rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.FALSE));
				model.addAttribute("startReminderList", rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.TRUE));
				model.addAttribute("auctionType", ((RfaEvent) event).getAuctionType());
				break;
			case RFI:
				event = rfiEventService.loadRfiEventById(eventId);
				/*
				 * assignedEditors = ((RfiEvent) event).getEventEditors(); assignedViewers = ((RfiEvent)
				 * event).getEventViewers();
				 */

				if (!(event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED)) {
					LOG.info("==========event status not in draft======================");
					return "redirect:/buyer/RFI/eventSummary/" + event.getId();
				}
				for (RfiTeamMember rfiTeamMember : ((RfiEvent) event).getTeamMembers()) {
					assignedTeamMembers.add((User) rfiTeamMember.getUser().clone());
				}

				// List<RfiApprovalUser> rfiApprovalUserList = new ArrayList<RfiApprovalUser>();
				/*
				 * for (User user : userList) { rfiApprovalUserList.add(new RfiApprovalUser(user)); }
				 */
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				format.setTimeZone(timeZone);
				((RfiEvent) (event)).setEventPublishTime(event.getEventPublishDate());
				if (((RfiEvent) (event)).getExpectedTenderEndDate() != null && ((RfiEvent) (event)).getExpectedTenderStartDate() != null) {
					((RfiEvent) (event)).setExpectedTenderDateTimeRange(format.format(((RfiEvent) (event)).getExpectedTenderStartDate()) + " - " + format.format(((RfiEvent) (event)).getExpectedTenderEndDate()));
				}
				if (((RfiEvent) (event)).getFeeEndDate() != null && ((RfiEvent) (event)).getFeeStartDate() != null) {
					((RfiEvent) (event)).setFeeDateTimeRange(format.format(((RfiEvent) (event)).getFeeStartDate()) + " - " + format.format(((RfiEvent) (event)).getFeeEndDate()));
				}
				if (((RfiEvent) (event)).getMinimumSupplierRating() != null) {
					((RfiEvent) (event)).setMinimumSupplierRating(((RfiEvent) (event)).getMinimumSupplierRating());
				}
				if (((RfiEvent) (event)).getMaximumSupplierRating() != null) {
					((RfiEvent) (event)).setMaximumSupplierRating(((RfiEvent) (event)).getMaximumSupplierRating());
				}
				// model.addAttribute("userList", rfiApprovalUserList);
				RfiEventContact eventContact = new RfiEventContact();
				eventContact.setEventId(event.getId());
				model.addAttribute("eventContact", eventContact);
				model.addAttribute("eventContactsList", ((RfiEvent) event).getEventContacts());
				model.addAttribute("reminderList", rfiEventService.getAllRfiEventReminderForEvent(eventId));
				break;
			case RFP:
				event = rfpEventService.loadRfpEventById(eventId);
				/*
				 * assignedEditors = ((RfpEvent) event).getEventEditors(); assignedViewers = ((RfpEvent)
				 * event).getEventViewers();
				 */

				if (!(event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED)) {
					LOG.info("==========event status not in draft======================");
					return "redirect:/buyer/RFP/eventSummary/" + event.getId();
				}
				for (RfpTeamMember rfpTeamMember : ((RfpEvent) event).getTeamMembers()) {
					assignedTeamMembers.add((User) rfpTeamMember.getUser().clone());
				}

				List<RfpApprovalUser> rfpApprovalUserList = new ArrayList<RfpApprovalUser>();
				for (User user : userList) {
					rfpApprovalUserList.add(new RfpApprovalUser(user));
				}
				((RfpEvent) (event)).setEventPublishTime(event.getEventPublishDate());

				if (((RfpEvent) (event)).getMinimumSupplierRating() != null) {
					((RfpEvent) (event)).setMinimumSupplierRating(((RfpEvent) (event)).getMinimumSupplierRating());
				}
				if (((RfpEvent) (event)).getMaximumSupplierRating() != null) {
					((RfpEvent) (event)).setMaximumSupplierRating(((RfpEvent) (event)).getMaximumSupplierRating());
				}

				model.addAttribute("userList", rfpApprovalUserList);

				RfpEventContact eventPContact = new RfpEventContact();
				eventPContact.setEventId(event.getId());
				model.addAttribute("eventContact", eventPContact);
				model.addAttribute("eventContactsList", ((RfpEvent) event).getEventContacts());
				model.addAttribute("reminderList", rfpEventService.getAllRfpEventReminderForEvent(eventId));
				break;
			case RFQ: {
				event = rfqEventService.loadRfqEventById(eventId);
				LOG.info(" Event Id : " + event.getId());

				if (!(event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED)) {
					LOG.info("==========event status not in draft======================");
					return "redirect:/buyer/RFQ/eventSummary/" + event.getId();
				}
				/*
				 * assignedEditors = ((RfqEvent) event).getEventEditors(); assignedViewers = ((RfqEvent)
				 * event).getEventViewers();
				 */
				for (RfqTeamMember rfpTeamMember : ((RfqEvent) event).getTeamMembers()) {
					assignedTeamMembers.add((User) rfpTeamMember.getUser().clone());
				}
				List<RfqApprovalUser> rfqApprovalUserList = new ArrayList<RfqApprovalUser>();
				for (User user : userList) {
					rfqApprovalUserList.add(new RfqApprovalUser(user));
				}
				((RfqEvent) (event)).setEventPublishTime(event.getEventPublishDate());
				if (((RfqEvent) (event)).getMinimumSupplierRating() != null) {
					((RfqEvent) (event)).setMinimumSupplierRating(((RfqEvent) (event)).getMinimumSupplierRating());
				}
				if (((RfqEvent) (event)).getMaximumSupplierRating() != null) {
					((RfqEvent) (event)).setMaximumSupplierRating(((RfqEvent) (event)).getMaximumSupplierRating());
				}
				LOG.info("..................................................");
				model.addAttribute("userList", rfqApprovalUserList);
				RfqEventContact eventPContact1 = new RfqEventContact();
				eventPContact1.setEventId(event.getId());
				model.addAttribute("eventContact", eventPContact1);
				model.addAttribute("eventContactsList", ((RfqEvent) event).getEventContacts());
				model.addAttribute("reminderList", rfqEventService.getAllRfqEventReminderForEvent(eventId));
				break;
			}
			case RFT:
				event = rftEventService.loadRftEventById(eventId);

				if (!(event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED)) {
					LOG.info("==========event status not in draft======================");
					return "redirect:/buyer/RFT/eventSummary/" + event.getId();
				}
				for (RftTeamMember rftTeamMember : ((RftEvent) event).getTeamMembers()) {
					assignedTeamMembers.add((User) rftTeamMember.getUser().clone());
				}

				List<RftApprovalUser> rftapprovalUserList = new ArrayList<RftApprovalUser>();
				for (User user : userList) {
					rftapprovalUserList.add(new RftApprovalUser(user));
				}
				((RftEvent) (event)).setEventPublishTime(event.getEventPublishDate());
				if (((RftEvent) (event)).getMinimumSupplierRating() != null) {
					((RftEvent) (event)).setMinimumSupplierRating(((RftEvent) (event)).getMinimumSupplierRating());
				}
				if (((RftEvent) (event)).getMaximumSupplierRating() != null) {
					((RftEvent) (event)).setMaximumSupplierRating(((RftEvent) (event)).getMaximumSupplierRating());
				}
				model.addAttribute("userList", rftapprovalUserList);

				RftEventContact eventTContact = new RftEventContact();
				eventTContact.setEventId(event.getId());
				model.addAttribute("eventContact", eventTContact);
				model.addAttribute("eventContactsList", ((RftEvent) event).getEventContacts());
				model.addAttribute("reminderList", rftEventService.getAllRftEventReminderForEvent(eventId));
				break;
			default:
				break;
			}

			constructEventCategories(model, event, getEventType());
			if (event.getTemplate() != null && event.getTemplate().getId() != null) {
				RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(event.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("templateFields", rfxTemplate.getFields());
				model.addAttribute("rfxTemplate", rfxTemplate);
			}
			LOG.info("Consturt.....................");
			if (event.getEventStart() != null && event.getEventEnd() != null) {
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				format.setTimeZone(timeZone);
				event.setEventVisibilityDates(format.format(event.getEventStart()) + " - " + format.format(event.getEventEnd()));
			}
			List<User> userTeamMemberList = new ArrayList<User>();
			// List<User> userTeamMember =
			// userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			List<User> userTeamMember = userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			for (User user : userTeamMember) {
				userTeamMemberList.add((User) user.clone());
			}

			LOG.info("assignedTeamMembers.size() :" + assignedTeamMembers.size());
			userTeamMemberList.removeAll(assignedTeamMembers);
			LOG.info("userTeamMemberList.size() :" + userTeamMemberList.size());
			model.addAttribute("userTeamMemberList", userTeamMemberList);

			model.addAttribute("isIdSettingOn", eventIdSettingsDao.isBusinessSettingEnable(SecurityLibrary.getLoggedInUserTenantId(), getEventType().name()));

			// model.addAttribute("userList1", userList);

			LOG.info("Event : " + event.getId());
			constructDefaultModel(model, event, eventType, null);
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
		}
		if (event.getViewSupplerName()) {
			event.setViewSupplerName(false);
		} else {
			event.setViewSupplerName(true);
		}

		model.addAttribute("event", event);
		LOG.info("Got Event details.................................................");
		return "createEventDetails";
	}

	/**
	 * @param model
	 * @param event
	 * @param rfxType
	 * @throws JsonProcessingException
	 */
	public void constructEventCategories(Model model, Event event, RfxTypes rfxType) throws JsonProcessingException {
		/*
		 * ObjectMapper mapperObj = new ObjectMapper(); String jsonIndusList = "";
		 */
		String tenantId = SecurityLibrary.getLoggedInUserTenantId();
		Set<IndustryCategoryPojo> industryCat = new HashSet<IndustryCategoryPojo>();

		List<IndustryCategoryPojo> list = rfaEventService.getTopFiveCategory(tenantId);
		for (IndustryCategoryPojo industryCategory : list) {
			IndustryCategoryPojo pojo = new IndustryCategoryPojo();
			pojo.setName(industryCategory.getName());
			pojo.setCode(industryCategory.getCode());
			pojo.setId(industryCategory.getId());
			industryCat.add(pojo);
		}

		if (CollectionUtil.isEmpty(industryCat)) {

			TableDataInput filter = new TableDataInput();
			filter.setStart(1);
			filter.setLength(20);
			List<IndustryCategory> industryCatList = industryCategoryService.findIndustryCategoryForTenant(tenantId, filter);
			for (IndustryCategory industryCategory : industryCatList) {
				IndustryCategoryPojo pojo = new IndustryCategoryPojo();
				pojo.setName(industryCategory.getName());
				pojo.setCode(industryCategory.getCode());
				pojo.setId(industryCategory.getId());
				industryCat.add(pojo);
			}

		}

		switch (rfxType) {
		case RFA:
			if (CollectionUtil.isNotEmpty(((RfaEvent) (event)).getIndustryCategories())) {
				List<IndustryCategory> icList = ((RfaEvent) (event)).getIndustryCategories();
				for (IndustryCategory industryCategory : icList) {
					IndustryCategoryPojo pojo = new IndustryCategoryPojo();
					pojo.setName(industryCategory.getName());
					pojo.setCode(industryCategory.getCode());
					pojo.setId(industryCategory.getId());
					industryCat.add(pojo);
				}
			}
			break;
		case RFI:
			if (CollectionUtil.isNotEmpty(((RfiEvent) (event)).getIndustryCategories())) {
				List<IndustryCategory> icList = ((RfiEvent) (event)).getIndustryCategories();
				for (IndustryCategory industryCategory : icList) {
					IndustryCategoryPojo pojo = new IndustryCategoryPojo();
					pojo.setName(industryCategory.getName());
					pojo.setCode(industryCategory.getCode());
					pojo.setId(industryCategory.getId());
					industryCat.add(pojo);
				}
			}
			break;
		case RFP:
			if (CollectionUtil.isNotEmpty(((RfpEvent) (event)).getIndustryCategories())) {
				List<IndustryCategory> icList = ((RfpEvent) (event)).getIndustryCategories();
				for (IndustryCategory industryCategory : icList) {
					IndustryCategoryPojo pojo = new IndustryCategoryPojo();
					pojo.setName(industryCategory.getName());
					pojo.setCode(industryCategory.getCode());
					pojo.setId(industryCategory.getId());
					industryCat.add(pojo);
				}
			}
			break;
		case RFQ:
			if (CollectionUtil.isNotEmpty(((RfqEvent) (event)).getIndustryCategories())) {
				List<IndustryCategory> icList = ((RfqEvent) (event)).getIndustryCategories();
				for (IndustryCategory industryCategory : icList) {
					IndustryCategoryPojo pojo = new IndustryCategoryPojo();
					pojo.setName(industryCategory.getName());
					pojo.setCode(industryCategory.getCode());
					pojo.setId(industryCategory.getId());
					industryCat.add(pojo);
				}
			}
			break;
		case RFT:
			if (CollectionUtil.isNotEmpty(((RftEvent) (event)).getIndustryCategories())) {
				List<IndustryCategory> icList = ((RftEvent) (event)).getIndustryCategories();
				for (IndustryCategory industryCategory : icList) {
					IndustryCategoryPojo pojo = new IndustryCategoryPojo();
					pojo.setName(industryCategory.getName());
					pojo.setCode(industryCategory.getCode());
					pojo.setId(industryCategory.getId());
					industryCat.add(pojo);
				}
			}
			break;
		default:
			break;
		}
		model.addAttribute("industryCat", industryCat);

		long indusCatListCount = industryCategoryService.findTotalIndustryCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId());
		if (indusCatListCount == 0) {
			model.addAttribute("indusCatListFlag", true);
		} else {
			model.addAttribute("indusCatListFlag", false);
		}

	}

	/**
	 * @param model
	 * @param event
	 * @param rfxType
	 * @throws JsonProcessingException
	 */
	public void constructDatabaseEventCategories(Model model, Event event, RfxTypes rfxType) throws JsonProcessingException {

		String tenantId = SecurityLibrary.getLoggedInUserTenantId();
		Set<IndustryCategoryPojo> industryCat = new HashSet<IndustryCategoryPojo>();

		List<IndustryCategoryPojo> list = rfaEventService.getTopFiveCategory(tenantId);
		for (IndustryCategoryPojo industryCategory : list) {
			IndustryCategoryPojo pojo = new IndustryCategoryPojo();
			pojo.setName(industryCategory.getName());
			pojo.setCode(industryCategory.getCode());
			pojo.setId(industryCategory.getId());
			industryCat.add(pojo);
		}

		if (CollectionUtil.isEmpty(industryCat)) {

			TableDataInput filter = new TableDataInput();
			filter.setStart(1);
			filter.setLength(20);
			List<IndustryCategory> industryCatList = industryCategoryService.findIndustryCategoryForTenant(tenantId, filter);
			for (IndustryCategory industryCategory : industryCatList) {
				IndustryCategoryPojo pojo = new IndustryCategoryPojo();
				pojo.setName(industryCategory.getName());
				pojo.setCode(industryCategory.getCode());
				pojo.setId(industryCategory.getId());
				industryCat.add(pojo);
			}

		}

		switch (rfxType) {
		case RFA:
			if (CollectionUtil.isNotEmpty(((RfaEvent) (event)).getIndustryCategories())) {
				List<IndustryCategory> icList = ((RfaEvent) (event)).getIndustryCategories();
				for (IndustryCategory industryCategory : icList) {
					IndustryCategory db = industryCategoryService.getIndustryCategoryById(industryCategory.getId());

					IndustryCategoryPojo pojo = new IndustryCategoryPojo();
					pojo.setName(db.getName());
					pojo.setCode(db.getCode());
					pojo.setId(db.getId());
					industryCat.add(pojo);

				}
			}
			break;
		case RFI:
			if (CollectionUtil.isNotEmpty(((RfiEvent) (event)).getIndustryCategories())) {
				List<IndustryCategory> icList = ((RfiEvent) (event)).getIndustryCategories();
				for (IndustryCategory industryCategory : icList) {
					IndustryCategory db = industryCategoryService.getIndustryCategoryById(industryCategory.getId());
					IndustryCategoryPojo pojo = new IndustryCategoryPojo();
					pojo.setName(db.getName());
					pojo.setCode(db.getCode());
					pojo.setId(db.getId());
					industryCat.add(pojo);
				}
			}
			break;
		case RFP:
			if (CollectionUtil.isNotEmpty(((RfpEvent) (event)).getIndustryCategories())) {
				List<IndustryCategory> icList = ((RfpEvent) (event)).getIndustryCategories();
				for (IndustryCategory industryCategory : icList) {
					IndustryCategory db = industryCategoryService.getIndustryCategoryById(industryCategory.getId());
					IndustryCategoryPojo pojo = new IndustryCategoryPojo();
					pojo.setName(db.getName());
					pojo.setCode(db.getCode());
					pojo.setId(db.getId());
					industryCat.add(pojo);
				}
			}
			break;
		case RFQ:
			if (CollectionUtil.isNotEmpty(((RfqEvent) (event)).getIndustryCategories())) {
				List<IndustryCategory> icList = ((RfqEvent) (event)).getIndustryCategories();
				for (IndustryCategory industryCategory : icList) {
					IndustryCategory db = industryCategoryService.getIndustryCategoryById(industryCategory.getId());
					IndustryCategoryPojo pojo = new IndustryCategoryPojo();
					pojo.setName(db.getName());
					pojo.setCode(db.getCode());
					pojo.setId(db.getId());
					industryCat.add(pojo);
				}
			}
			break;
		case RFT:
			if (CollectionUtil.isNotEmpty(((RftEvent) (event)).getIndustryCategories())) {
				List<IndustryCategory> icList = ((RftEvent) (event)).getIndustryCategories();
				for (IndustryCategory industryCategory : icList) {
					IndustryCategory db = industryCategoryService.getIndustryCategoryById(industryCategory.getId());
					IndustryCategoryPojo pojo = new IndustryCategoryPojo();
					pojo.setName(db.getName());
					pojo.setCode(db.getCode());
					pojo.setId(db.getId());
					industryCat.add(pojo);
				}
			}
			break;
		default:
			break;
		}
		model.addAttribute("industryCat", industryCat);

		long indusCatListCount = industryCategoryService.findTotalIndustryCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId());
		if (indusCatListCount == 0) {
			model.addAttribute("indusCatListFlag", true);
		} else {
			model.addAttribute("indusCatListFlag", false);
		}

	}

	protected boolean doValidate(Event event, Model model, boolean isDraft) {
		boolean isvalid = false;

		// TV requested to allow duplicate reference numbers.

		/*
		 * switch (getEventType()) { case RFA: isvalid = rfaEventService.isExists((RfaEvent) event); break; case RFI:
		 * isvalid = rfiEventService.isExists((RfiEvent) event); break; case RFP: isvalid =
		 * rfpEventService.isExists((RfpEvent) event); break; case RFQ: isvalid = rfqEventService.isExists((RfqEvent)
		 * event); break; case RFT: isvalid = rftEventService.isExists((RftEvent) event); break; default: break; }
		 */

		if (isvalid) {
			model.addAttribute("error", messageSource.getMessage("rftEvent.error.duplicate", new Object[] { event.getReferanceNumber() }, Global.LOCALE));
			LOG.error("Error:   " + messageSource.getMessage("rftEvent.error.duplicate", new Object[] { event.getReferanceNumber() }, Global.LOCALE));
			return isvalid;
		}
		// if (!isDraft && event.getIndustryCategory() == null) {
		// model.addAttribute("error",
		// messageSource.getMessage("supplier.industy.required", new Object[]
		// {},
		// Global.LOCALE));
		// // LOG.error("Error: " +
		// messageSource.getMessage("supplier.industy.required", new Object[]
		// {},
		// // Global.LOCALE));
		// return true;
		// }
		LOG.info("START DATE : " + event.getEventStart());
		return isvalid;
	}

	public ResponseEntity<List<EventTeamMember>> addTeamMemberToList(String eventId, String userId, TeamMemberType memberType) {
		HttpHeaders headers = new HttpHeaders();
		List<EventTeamMember> teamMembers = null;
		try {
			if (StringUtils.checkString(userId).length() > 0) {
				switch (getEventType()) {
				case RFA:
					rfaEventService.addTeamMemberToList(eventId, userId, memberType);
					teamMembers = rfaEventService.getPlainTeamMembersForEvent(eventId);
					break;
				case RFI:
					rfiEventService.addTeamMemberToList(eventId, userId, memberType);
					teamMembers = rfiEventService.getPlainTeamMembersForEvent(eventId);
					break;
				case RFP:
					rfpEventService.addTeamMemberToList(eventId, userId, memberType);
					teamMembers = rfpEventService.getPlainTeamMembersForEvent(eventId);
					break;
				case RFQ:
					rfqEventService.addTeamMemberToList(eventId, userId, memberType);
					teamMembers = rfqEventService.getPlainTeamMembersForEvent(eventId);
					break;
				case RFT:
					boolean isUserControl = Boolean.TRUE.equals(SecurityLibrary.getLoggedInUser().getBuyer().getEnableEventUserControle());
					if (isUserControl) {
						String ownerID = rftEventService.getEventOwnerId(eventId);
						if (userId.equals(ownerID)) {
							throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Event Team Member" }, Global.LOCALE));
						}
					}
					rftEventService.addTeamMemberToList(eventId, userId, memberType);
					teamMembers = rftEventService.getPlainTeamMembersForEvent(eventId);
					break;
				default:
					break;
				}

				sendAddTeamMemberEmailNotificationEmail(eventId, userId, memberType, getEventType());

			} else {
				headers.add("error", "Please Select TeamMember Users");
				LOG.error("Please Select TeamMember Users");
				return new ResponseEntity<List<EventTeamMember>>(null, headers, HttpStatus.BAD_REQUEST);

			}
		} catch (ApplicationException e) {
			LOG.error("Error While adding TeamMember users : " + e.getMessage(), e);
			headers.add("error", e.getMessage());
			return new ResponseEntity<List<EventTeamMember>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			LOG.error("Error While adding TeamMember users : " + e.getMessage(), e);
			headers.add("error", "Please Select TeamMember Users");
			return new ResponseEntity<List<EventTeamMember>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<EventTeamMember>>(teamMembers, headers, HttpStatus.OK);
	}

	private void sendAddTeamMemberEmailNotificationEmail(String eventId, String userId, TeamMemberType memberType, RfxTypes eventType2) {
		// Auto-generated method stub
		try {
			String subject = "You have been Invited as TEAM MEMBER in Event";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();
			User user = userService.getUsersById(userId);

			map.put("userName", user.getName());
			map.put("memberType", memberType.getValue());
			map.put("eventType", getEventType().toString());
			if (memberType == TeamMemberType.Editor)
				map.put("memberMessage", " Allows you to edit the entire draft stage of the Event but not finish the Event");
			else if (memberType == TeamMemberType.Viewer)
				map.put("memberMessage", "Allows you to view entire draft stage of the Event without the ability to edit");
			else
				map.put("memberMessage", "Allows you to perform the same actions as the Event Owner.");

			String id = null;
			// String eventIdForNotificatonMsg = "";
			String eventName = "";
			switch (getEventType()) {
			case RFA:
				Event rfaevent = rfaEventService.getSimpleEventDetailsById(eventId);
				map.put("eventName", StringUtils.checkString(rfaevent.getEventName()).length() > 0 ? rfaevent.getEventName() : " ");
				map.put("createdBy", rfaevent.getCreatedBy().getName());
				id = rfaevent.getId();
				// eventIdForNotificatonMsg = rfaevent.getEventId();
				eventName = StringUtils.checkString(rfaevent.getEventName()).length() > 0 ? rfaevent.getEventName() : " ";
				map.put("eventId", rfaevent.getEventId());
				map.put("eventRefNum", StringUtils.checkString(rfaevent.getReferanceNumber()).length() > 0 ? rfaevent.getReferanceNumber() : " ");
				break;
			case RFI:
				Event rfievent = rfiEventService.getSimpleEventDetailsById(eventId);
				id = rfievent.getId();
				// eventIdForNotificatonMsg = rfievent.getEventId();
				eventName = StringUtils.checkString(rfievent.getEventName()).length() > 0 ? rfievent.getEventName() : " ";
				map.put("eventName", StringUtils.checkString(rfievent.getEventName()).length() > 0 ? rfievent.getEventName() : " ");
				map.put("createdBy", rfievent.getCreatedBy().getName());
				map.put("eventId", rfievent.getEventId());
				map.put("eventRefNum", StringUtils.checkString(rfievent.getReferanceNumber()).length() > 0 ? rfievent.getReferanceNumber() : " ");
				break;
			case RFP:
				Event rfpevent = rfpEventService.getSimpleEventDetailsById(eventId);
				id = rfpevent.getId();
				// eventIdForNotificatonMsg = rfpevent.getEventId();
				eventName = StringUtils.checkString(rfpevent.getEventName()).length() > 0 ? rfpevent.getEventName() : " ";
				map.put("eventName", StringUtils.checkString(rfpevent.getEventName()).length() > 0 ? rfpevent.getEventName() : " ");
				map.put("createdBy", rfpevent.getCreatedBy().getName());
				map.put("eventId", rfpevent.getEventId());
				map.put("eventRefNum", StringUtils.checkString(rfpevent.getReferanceNumber()).length() > 0 ? rfpevent.getReferanceNumber() : " ");
				break;
			case RFQ:
				Event rfqevent = rfqEventService.getSimpleEventDetailsById(eventId);
				id = rfqevent.getId();
				// eventIdForNotificatonMsg = rfqevent.getEventId();
				eventName = StringUtils.checkString(rfqevent.getEventName()).length() > 0 ? rfqevent.getEventName() : " ";
				map.put("eventName", StringUtils.checkString(rfqevent.getEventName()).length() > 0 ? rfqevent.getEventName() : " ");
				map.put("createdBy", rfqevent.getCreatedBy().getName());
				map.put("eventId", rfqevent.getEventId());
				map.put("eventRefNum", StringUtils.checkString(rfqevent.getReferanceNumber()).length() > 0 ? rfqevent.getReferanceNumber() : " ");
				break;
			case RFT:
				Event rftevent = rftEventService.getSimpleEventDetailsById(eventId);
				id = rftevent.getId();
				// eventIdForNotificatonMsg = rftevent.getEventId();
				eventName = StringUtils.checkString(rftevent.getEventName()).length() > 0 ? rftevent.getEventName() : " ";
				map.put("eventName", StringUtils.checkString(rftevent.getEventName()).length() > 0 ? rftevent.getEventName() : " ");
				map.put("createdBy", rftevent.getCreatedBy().getName());
				map.put("eventId", rftevent.getEventId());
				map.put("eventRefNum", StringUtils.checkString(rftevent.getReferanceNumber()).length() > 0 ? rftevent.getReferanceNumber() : " ");
				break;
			default:
				break;
			}

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);

			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.TEAM_MEMBER_TEMPLATE), map);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(user.getCommunicationEmail(), subject, message);
			}
			url = APP_URL + "/buyer/" + eventType2.name() + "/createEventDetails/" + id;
			String notificationMessage = messageSource.getMessage("team.event.add", new Object[] { memberType, eventName }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);

		} catch (Exception e) {
			LOG.error("error in sending team member email " + e.getMessage(), e);
		}
		/*
		 * String notificationMessage = messageSource.getMessage("po.create.notification.message", new Object[] {
		 * pr.getName() }, Global.LOCALE); sendDashboardNotification(user, url, subject, notificationMessage,
		 * NotificationType.CREATED_MESSAGE);
		 */

	}

	private void sendRemoveTeamMemberEmailNotificationEmail(String eventId, String userId, TeamMemberType memberType, RfxTypes eventType2) {
		LOG.info("--------------EVENT eventId =---------------" + eventId);

		try {
			String subject = "You have been Removed as TEAM MEMBER from Event";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();
			User user = userService.getUsersById(userId);
			map.put("userName", user.getName());
			map.put("memberType", memberType.getValue());
			map.put("eventType", eventType2);

			if (memberType == TeamMemberType.Editor)
				map.put("memberMessage", " Allows you to edit the entire draft stage of the Event but not finish the Event");
			else if (memberType == TeamMemberType.Viewer)
				map.put("memberMessage", "Allows you to view entire draft stage of the Event without the ability to edit");
			else
				map.put("memberMessage", "Allows you to perform the same actions as the Event Owner.");

			String id = null;
			// String eventIdForNotificatonMsg = "";
			String eventName = "";
			switch (getEventType()) {
			case RFA:
				Event rfaevent = rfaEventService.getSimpleEventDetailsById(eventId);
				map.put("eventName", StringUtils.checkString(rfaevent.getEventName()).length() > 0 ? rfaevent.getEventName() : " ");
				map.put("createdBy", rfaevent.getCreatedBy().getName());
				id = rfaevent.getId();
				// eventIdForNotificatonMsg = rfaevent.getEventId();
				eventName = StringUtils.checkString(rfaevent.getEventName()).length() > 0 ? rfaevent.getEventName() : " ";
				map.put("eventId", rfaevent.getEventId());
				map.put("createrEmail", rfaevent.getCreatedBy().getCommunicationEmail());
				map.put("eventRefNum", StringUtils.checkString(rfaevent.getReferanceNumber()).length() > 0 ? rfaevent.getReferanceNumber() : " ");
				break;
			case RFI:
				Event rfievent = rfiEventService.getSimpleEventDetailsById(eventId);
				id = rfievent.getId();
				// eventIdForNotificatonMsg = rfievent.getEventId();
				eventName = StringUtils.checkString(rfievent.getEventName()).length() > 0 ? rfievent.getEventName() : " ";
				map.put("eventName", StringUtils.checkString(rfievent.getEventName()).length() > 0 ? rfievent.getEventName() : " ");
				map.put("createdBy", rfievent.getCreatedBy().getName());
				map.put("createrEmail", rfievent.getCreatedBy().getCommunicationEmail());
				map.put("eventId", rfievent.getEventId());
				map.put("eventRefNum", StringUtils.checkString(rfievent.getReferanceNumber()).length() > 0 ? rfievent.getReferanceNumber() : " ");
				break;
			case RFP:
				Event rfpevent = rfpEventService.getSimpleEventDetailsById(eventId);
				id = rfpevent.getId();
				// eventIdForNotificatonMsg = rfpevent.getEventId();
				eventName = StringUtils.checkString(rfpevent.getEventName()).length() > 0 ? rfpevent.getEventName() : " ";
				map.put("eventName", StringUtils.checkString(rfpevent.getEventName()).length() > 0 ? rfpevent.getEventName() : " ");
				map.put("createdBy", rfpevent.getCreatedBy().getName());
				map.put("createrEmail", rfpevent.getCreatedBy().getCommunicationEmail());
				map.put("eventId", rfpevent.getEventId());
				map.put("eventRefNum", StringUtils.checkString(rfpevent.getReferanceNumber()).length() > 0 ? rfpevent.getReferanceNumber() : " ");
				break;
			case RFQ:
				Event rfqevent = rfqEventService.getSimpleEventDetailsById(eventId);
				id = rfqevent.getId();
				// eventIdForNotificatonMsg = rfqevent.getEventId();
				eventName = StringUtils.checkString(rfqevent.getEventName()).length() > 0 ? rfqevent.getEventName() : " ";
				map.put("eventName", StringUtils.checkString(rfqevent.getEventName()).length() > 0 ? rfqevent.getEventName() : " ");
				map.put("createdBy", rfqevent.getCreatedBy().getName());
				map.put("eventId", rfqevent.getEventId());
				map.put("createrEmail", rfqevent.getCreatedBy().getCommunicationEmail());
				map.put("eventRefNum", StringUtils.checkString(rfqevent.getReferanceNumber()).length() > 0 ? rfqevent.getReferanceNumber() : " ");
				break;
			case RFT:
				Event rftevent = rftEventService.getSimpleEventDetailsById(eventId);
				id = rftevent.getId();
				// eventIdForNotificatonMsg = rftevent.getEventId();
				eventName = StringUtils.checkString(rftevent.getEventName()).length() > 0 ? rftevent.getEventName() : " ";
				map.put("eventName", StringUtils.checkString(rftevent.getEventName()).length() > 0 ? rftevent.getEventName() : " ");
				map.put("createdBy", rftevent.getCreatedBy().getName());
				map.put("eventId", rftevent.getEventId());
				map.put("createrEmail", rftevent.getCreatedBy().getCommunicationEmail());
				map.put("eventRefNum", StringUtils.checkString(rftevent.getReferanceNumber()).length() > 0 ? rftevent.getReferanceNumber() : " ");
				break;
			default:
				break;
			}
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);

			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.REMOVE_TEAM_MEMBER_TEMPLATE), map);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(user.getCommunicationEmail(), subject, message);
			}
			url = APP_URL + "/buyer/" + eventType2.name() + "/createEventDetails/" + id;

			String notificationMessage = messageSource.getMessage("team.event.remove", new Object[] { memberType, eventName }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);

		} catch (Exception e) {
			LOG.error("error in sending team member email " + e.getMessage(), e);
		}
		/*
		 * String notificationMessage = messageSource.getMessage("po.create.notification.message", new Object[] {
		 * pr.getName() }, Global.LOCALE); sendDashboardNotification(user, url, subject, notificationMessage,
		 * NotificationType.CREATED_MESSAGE);
		 */

	}

	public ResponseEntity<List<User>> removeTeamMemberfromList(String eventId, String userId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("userId Call");
		List<User> teamMembers = null;
		try {
			TeamMemberType memberType = null;
			switch (getEventType()) {
			case RFA:

				RfaTeamMember rfaTeamMember = rfaEventService.getRfaTeamMemberByUserIdAndEventId(eventId, userId);
				memberType = rfaTeamMember.getTeamMemberType();
				teamMembers = rfaEventService.removeTeamMemberfromList(eventId, userId, rfaTeamMember);
				break;
			case RFI:
				RfiTeamMember rfimember = rfiEventService.getRfiTeamMemberByUserIdAndEventId(eventId, userId);
				memberType = rfimember.getTeamMemberType();
				teamMembers = rfiEventService.removeTeamMembersfromList(eventId, userId, rfimember);
				break;
			case RFP:
				RfpTeamMember rfpmember = rfpEventService.getRfpTeamMemberByUserIdAndEventId(eventId, userId);
				memberType = rfpmember.getTeamMemberType();
				teamMembers = rfpEventService.removeTeamMemberfromList(eventId, userId, rfpmember);
				break;
			case RFQ:
				RfqTeamMember rfqmember = rfqEventService.getRfqTeamMemberByUserIdAndEventId(eventId, userId);
				memberType = rfqmember.getTeamMemberType();
				teamMembers = rfqEventService.removeTeamMemberfromList(eventId, userId, rfqmember);
				break;
			case RFT:
				RftTeamMember rftmember = rftEventService.getRftTeamMemberByUserIdAndEventId(eventId, userId);
				memberType = rftmember.getTeamMemberType();
				teamMembers = rftEventService.removeTeamMemberfromList(eventId, userId, rftmember);
				break;
			default:
				break;
			}

			sendRemoveTeamMemberEmailNotificationEmail(eventId, userId, memberType, getEventType());
		} catch (Exception e) {
			LOG.error("Error While removing Team Member users : " + e.getMessage(), e);
			headers.add("error", "Error While removing Team Member users : " + e.getMessage());
			return new ResponseEntity<List<User>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<User> userTeamMember = new ArrayList<User>();

		// List<User> userList =
		// userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, UserType.NORMAL_USER);
		for (UserPojo user : userList) {
			try {
				userTeamMember.add(new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted()));
			} catch (Exception e) {
				LOG.info("Error while cloning the user List :" + e.getMessage());
			}
		}

		userTeamMember.removeAll(teamMembers);
		return new ResponseEntity<List<User>>(userTeamMember, headers, HttpStatus.OK);
	}

	private String getTimeZoneByBuyerSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = buyerSettingsService.getBuyerTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(NotificationType.EVENT_MESSAGE);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	public String createEventDetailsPage(Model model, String eventId, HttpSession session, EventPermissions eventPermissions) {
		LOG.info("Getting Event details....................." + eventPermissions.toLogString());
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);

		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		format.setTimeZone(timeZone);

		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		Event event = null;
		List<User> assignedTeamMembers = new ArrayList<>();
		List<User> approvalUsers = new ArrayList<User>();
		List<UserPojo> maskingUserList = new ArrayList<UserPojo>();
		List<UserPojo> evaluationConclusionUserList = new ArrayList<UserPojo>();
		List<User> suspApprovalUsers = new ArrayList<User>();
		boolean sendForSuspApproval = false;
		
		try {
			List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);

			switch (getEventType()) {
			case RFA: {
				event = rfaEventService.loadRfaEventModalById(eventId, model, format, assignedTeamMembers, approvalUsers, suspApprovalUsers);

				if (SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") && event.getStatus() == EventStatus.DRAFT && !eventPermissions.isOwner()) {
					return "redirect:/403_error";
				}
				if (event.getStatus() == EventStatus.DRAFT && !(eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer())) {
					return "redirect:/403_error";
				}

				if (event.getStatus() == EventStatus.PENDING && !(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover())) {
					return "redirect:/403_error";
				}

				if (!(event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED)) {
					LOG.info("Event status not in draft redirected to ->EventSummary page");
					return "redirect:/buyer/RFA/eventSummary/" + event.getId();
				}
				List<User> userList2 = new ArrayList<User>();

				User ru = ((RfaEvent) event).getRevertBidUser();
				if (ru != null) {
					userList2.add(new User(ru.getId(), ru.getLoginId(), ru.getName(), ru.getCommunicationEmail(), ru.getEmailNotifications(), ru.getTenantId(), ru.isDeleted()));
				}

				List<RfaUnMaskedUser> rfaUnMaskedUsers = ((RfaEvent) event).getUnMaskedUsers();
				if (CollectionUtil.isNotEmpty(rfaUnMaskedUsers)) {
					for (RfaUnMaskedUser unMaskedUser : rfaUnMaskedUsers) {
						maskingUserList.add(new UserPojo(unMaskedUser.getUser().getId(), unMaskedUser.getUser().getLoginId(), unMaskedUser.getUser().getName(), unMaskedUser.getUser().getTenantId(), unMaskedUser.getUser().isDeleted(), unMaskedUser.getUser().getCommunicationEmail(), unMaskedUser.getUser().getEmailNotifications()));
					}
				}

				List<RfaEvaluationConclusionUser> evaluationConclusionUsers = ((RfaEvent) event).getEvaluationConclusionUsers();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUsers)) {
					for (RfaEvaluationConclusionUser user : evaluationConclusionUsers) {
						evaluationConclusionUserList.add(new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications()));
					}
				}

				for (UserPojo user : userListSumm) {
					try {
						User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
						if (!userList2.contains(u)) {
							userList2.add(u);
						}
					} catch (Exception e) {
						LOG.info("Error while cloning the user List :" + e.getMessage());
					}
				}

				model.addAttribute("userList2", userList2);
				model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
				model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
				model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
				Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("isEnableUnitAndCostCorrelation", enableCorrelation);
				if (event.getBusinessUnit() != null && enableCorrelation == Boolean.TRUE) {
					if (event.getBusinessUnit().getStatus() == Status.ACTIVE) {
						List<CostCenterPojo> assignedCostList = businessUnitService.getCostCentersByBusinessUnitId(event.getBusinessUnit().getId(), Status.ACTIVE);
//						List<String> assignedCostId = costCenterService.getCostCenterByBusinessId(event.getBusinessUnit().getId());
//						if (CollectionUtil.isNotEmpty(assignedCostId)) {
//							for (String assignedCost : assignedCostId) {
//								CostCenterPojo cost = costCenterService.getCostCenterByCostId(assignedCost);
//								if (cost.getStatus() == Status.ACTIVE) {
//									assignedCostList.add(cost);
//								}
//							}
//						}
						model.addAttribute("costCenter", assignedCostList);
					} else {
						model.addAttribute("costCenter", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
					}
				} else {
					model.addAttribute("costCenter", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
				}
				
				Boolean enableBUAndGroupCodeCorrelation = buyerSettingsService.isEnableUnitAndGroupCodeCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("isEnableBUAndGPCCorr", enableBUAndGroupCodeCorrelation);
				List<GroupCode> groupCodeList = groupCodeService.fetchAllActiveGroupCodeForTenantID(SecurityLibrary.getLoggedInUserTenantId());
				if (event.getBusinessUnit() != null && enableBUAndGroupCodeCorrelation == Boolean.TRUE) {
					groupCodeList = groupCodeService.getGroupCodeIdByBusinessId(event.getBusinessUnit().getId());
					if (event.getGroupCode() != null) {
						if (Status.INACTIVE == event.getGroupCode().getStatus() && !groupCodeList.contains(event.getGroupCode())) {
							groupCodeList.add(event.getGroupCode());
						}
					}

				}
				model.addAttribute("groupCodeList", groupCodeList);

				if(event.getStatus() == EventStatus.SUSPENDED && CollectionUtil.isNotEmpty(((RfaEvent) event).getSuspensionApprovals())) {
					for(RfaEventSuspensionApproval app :  ((RfaEvent) event).getSuspensionApprovals()) {
						if(app.isActive()) {
							LOG.info(">>>>>>>>>>>>>>>>>>>>>> : " + app.getLevel() + " - " + app.isActive());
							sendForSuspApproval = true;
							break;
						}
					}
				}
				model.addAttribute("sendForSuspApproval", sendForSuspApproval);
				
				List<RfaSuspensionApprovalUser> suspUserList = new ArrayList<RfaSuspensionApprovalUser>();
				LOG.info("suspUserList ............." + suspUserList.size());
				for (UserPojo user : userListSumm) {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					suspUserList.add(new RfaSuspensionApprovalUser(u));
				}
				model.addAttribute("suspUserList", suspUserList);
				break;
			}
			case RFI: {
				event = rfiEventService.loadRfiEventModalById(eventId, model, format, assignedTeamMembers, approvalUsers, suspApprovalUsers);
				if (SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") && event.getStatus() == EventStatus.DRAFT && !eventPermissions.isOwner()) {
					return "redirect:/403_error";
				}
				if (event.getStatus() == EventStatus.DRAFT && !(eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer())) {
					return "redirect:/403_error";
				}
				if (event.getStatus() == EventStatus.PENDING && !(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover())) {
					return "redirect:/403_error";
				}

				if (!(event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED)) {
					LOG.info("Event status not in draft redirected to ->EventSummary page");
					return "redirect:/buyer/RFI/eventSummary/" + event.getId();
				}
				List<RfiUnMaskedUser> rfiUnMaskedUsers = ((RfiEvent) event).getUnMaskedUsers();
				if (CollectionUtil.isNotEmpty(rfiUnMaskedUsers)) {
					for (RfiUnMaskedUser unMaskedUser : rfiUnMaskedUsers) {
						maskingUserList.add(new UserPojo(unMaskedUser.getUser().getId(), unMaskedUser.getUser().getLoginId(), unMaskedUser.getUser().getName(), unMaskedUser.getUser().getTenantId(), unMaskedUser.getUser().isDeleted(), unMaskedUser.getUser().getCommunicationEmail(), unMaskedUser.getUser().getEmailNotifications()));
					}
				}

				List<RfiEvaluationConclusionUser> evaluationConclusionUsers = ((RfiEvent) event).getEvaluationConclusionUsers();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUsers)) {
					for (RfiEvaluationConclusionUser user : evaluationConclusionUsers) {
						evaluationConclusionUserList.add(new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications()));
					}
				}
				
				if(event.getStatus() == EventStatus.SUSPENDED && CollectionUtil.isNotEmpty(((RfiEvent) event).getSuspensionApprovals())) {
					for(RfiEventSuspensionApproval app :  ((RfiEvent) event).getSuspensionApprovals()) {
						if(app.isActive()) {
							LOG.info(">>>>>>>>>>>>>>>>>>>>>> : " + app.getLevel() + " - " + app.isActive());
							sendForSuspApproval = true;
							break;
						}
					}
				}
				model.addAttribute("sendForSuspApproval", sendForSuspApproval);
				
				List<RfiSuspensionApprovalUser> suspUserList = new ArrayList<RfiSuspensionApprovalUser>();
				LOG.info("suspUserList ............." + suspUserList.size());
				for (UserPojo user : userListSumm) {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					suspUserList.add(new RfiSuspensionApprovalUser(u));
				}
				model.addAttribute("suspUserList", suspUserList);
				break;
			}
			case RFP: {
				event = rfpEventService.loadRfpEventModalById(eventId, model, format, assignedTeamMembers, approvalUsers, suspApprovalUsers);
				if (SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") && event.getStatus() == EventStatus.DRAFT && !eventPermissions.isOwner()) {
					return "redirect:/403_error";
				}
				if (event.getStatus() == EventStatus.DRAFT && !(eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer())) {
					return "redirect:/403_error";
				}
				if (event.getStatus() == EventStatus.PENDING && !(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover())) {
					return "redirect:/403_error";
				}

				if (!(event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED)) {
					LOG.info("Event status not in draft redirected to ->EventSummary page");
					return "redirect:/buyer/RFP/eventSummary/" + event.getId();
				}
				List<RfpUnMaskedUser> rfpUnMaskedUsers = ((RfpEvent) event).getUnMaskedUsers();
				if (CollectionUtil.isNotEmpty(rfpUnMaskedUsers)) {
					for (RfpUnMaskedUser unMaskedUser : rfpUnMaskedUsers) {
						maskingUserList.add(new UserPojo(unMaskedUser.getUser().getId(), unMaskedUser.getUser().getLoginId(), unMaskedUser.getUser().getName(), unMaskedUser.getUser().getTenantId(), unMaskedUser.getUser().isDeleted(), unMaskedUser.getUser().getCommunicationEmail(), unMaskedUser.getUser().getEmailNotifications()));
					}
				}

				List<RfpEvaluationConclusionUser> evaluationConclusionUsers = ((RfpEvent) event).getEvaluationConclusionUsers();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUsers)) {
					for (RfpEvaluationConclusionUser user : evaluationConclusionUsers) {
						evaluationConclusionUserList.add(new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications()));
					}
				}
				
				if(event.getStatus() == EventStatus.SUSPENDED && CollectionUtil.isNotEmpty(((RfpEvent) event).getSuspensionApprovals())) {
					for(RfpEventSuspensionApproval app :  ((RfpEvent) event).getSuspensionApprovals()) {
						if(app.isActive()) {
							LOG.info(">>>>>>>>>>>>>>>>>>>>>> : " + app.getLevel() + " - " + app.isActive());
							sendForSuspApproval = true;
							break;
						}
					}
				}
				model.addAttribute("sendForSuspApproval", sendForSuspApproval);
				List<RfpSuspensionApprovalUser> suspUserList = new ArrayList<RfpSuspensionApprovalUser>();
				LOG.info("suspUserList ............." + suspUserList.size());
				for (UserPojo user : userListSumm) {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					suspUserList.add(new RfpSuspensionApprovalUser(u));
				}
				model.addAttribute("suspUserList", suspUserList);
				break;
			}
			case RFQ: {
				event = rfqEventService.loadRfqEventModalById(eventId, model, format, assignedTeamMembers, approvalUsers, suspApprovalUsers);
				if (SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") && event.getStatus() == EventStatus.DRAFT && !eventPermissions.isOwner()) {
					return "redirect:/403_error";
				}
				if (event.getStatus() == EventStatus.DRAFT && !(eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer())) {
					return "redirect:/403_error";
				}
				if (event.getStatus() == EventStatus.PENDING && !(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover())) {
					return "redirect:/403_error";
				}

				if (!(event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED)) {
					LOG.info("Event status not in draft redirected to ->EventSummary page");
					return "redirect:/buyer/RFQ/eventSummary/" + event.getId();
				}
				List<RfqUnMaskedUser> rfqUnMaskedUsers = ((RfqEvent) event).getUnMaskedUsers();
				if (CollectionUtil.isNotEmpty(rfqUnMaskedUsers)) {
					for (RfqUnMaskedUser unMaskedUser : rfqUnMaskedUsers) {
						maskingUserList.add(new UserPojo(unMaskedUser.getUser().getId(), unMaskedUser.getUser().getLoginId(), unMaskedUser.getUser().getName(), unMaskedUser.getUser().getTenantId(), unMaskedUser.getUser().isDeleted(), unMaskedUser.getUser().getCommunicationEmail(), unMaskedUser.getUser().getEmailNotifications()));
					}
				}

				List<RfqEvaluationConclusionUser> evaluationConclusionUsers = ((RfqEvent) event).getEvaluationConclusionUsers();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUsers)) {
					for (RfqEvaluationConclusionUser user : evaluationConclusionUsers) {
						evaluationConclusionUserList.add(new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications()));
					}
				}

				if(event.getStatus() == EventStatus.SUSPENDED && CollectionUtil.isNotEmpty(((RfqEvent) event).getSuspensionApprovals())) {
					for(RfqEventSuspensionApproval app :  ((RfqEvent) event).getSuspensionApprovals()) {
						if(app.isActive()) {
							LOG.info(">>>>>>>>>>>>>>>>>>>>>> : " + app.getLevel() + " - " + app.isActive());
							sendForSuspApproval = true;
							break;
						}
					}
				}
				model.addAttribute("sendForSuspApproval", sendForSuspApproval);
				
				List<RfqSuspensionApprovalUser> suspUserList = new ArrayList<RfqSuspensionApprovalUser>();
				LOG.info("suspUserList ............." + suspUserList.size());
				for (UserPojo user : userListSumm) {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					suspUserList.add(new RfqSuspensionApprovalUser(u));
				}
				model.addAttribute("suspUserList", suspUserList);
				break;
			}
			case RFT: {
				event = rftEventService.loadRftEventModalById(eventId, model, format, assignedTeamMembers, approvalUsers, suspApprovalUsers);
				if (SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") && event.getStatus() == EventStatus.DRAFT && !eventPermissions.isOwner()) {
					return "redirect:/403_error";
				}
				if (event.getStatus() == EventStatus.DRAFT && !(eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer())) {
					return "redirect:/403_error";
				}
				if (event.getStatus() == EventStatus.PENDING && !(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isViewer() || eventPermissions.isApprover())) {
					return "redirect:/403_error";
				}

				if (!(event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED)) {
					LOG.info("Event status not in draft redirected to ->EventSummary page");
					return "redirect:/buyer/RFT/eventSummary/" + event.getId();
				}
				List<RftUnMaskedUser> rftUnMaskedUsers = ((RftEvent) event).getUnMaskedUsers();
				if (CollectionUtil.isNotEmpty(rftUnMaskedUsers)) {
					for (RftUnMaskedUser unMaskedUser : rftUnMaskedUsers) {
						maskingUserList.add(new UserPojo(unMaskedUser.getUser().getId(), unMaskedUser.getUser().getLoginId(), unMaskedUser.getUser().getName(), unMaskedUser.getUser().getTenantId(), unMaskedUser.getUser().isDeleted(), unMaskedUser.getUser().getCommunicationEmail(), unMaskedUser.getUser().getEmailNotifications()));
					}
				}

				List<RftEvaluationConclusionUser> evaluationConclusionUsers = ((RftEvent) event).getEvaluationConclusionUsers();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUsers)) {
					for (RftEvaluationConclusionUser user : evaluationConclusionUsers) {
						evaluationConclusionUserList.add(new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications()));
					}
				}

				if(event.getStatus() == EventStatus.SUSPENDED && CollectionUtil.isNotEmpty(((RftEvent) event).getSuspensionApprovals())) {
					for(RftEventSuspensionApproval app :  ((RftEvent) event).getSuspensionApprovals()) {
						if(app.isActive()) {
							LOG.info(">>>>>>>>>>>>>>>>>>>>>> : " + app.getLevel() + " - " + app.isActive());
							sendForSuspApproval = true;
							break;
						}
					}
				}
				model.addAttribute("sendForSuspApproval", sendForSuspApproval);
				
				List<RftSuspensionApprovalUser> suspUserList = new ArrayList<RftSuspensionApprovalUser>();
				LOG.info("suspUserList ............." + suspUserList.size());
				for (UserPojo user : userListSumm) {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					suspUserList.add(new RftSuspensionApprovalUser(u));
				}
				model.addAttribute("suspUserList", suspUserList);
				break;
			}
			default:
				break;

			}

			model.addAttribute("evaluationDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.EVALUATION_PROCESS, SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("supplierDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.SUPPLIER_ACCEPTANCE, SecurityLibrary.getLoggedInUserTenantId()));

			constructEventCategories(model, event, getEventType());
			if (event.getTemplate() != null && event.getTemplate().getId() != null) {
				RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(event.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("templateFields", rfxTemplate.getFields());
				model.addAttribute("rfxTemplate", rfxTemplate);
			}
			LOG.info("Consturt.....................");
			if (event.getEventStart() != null && event.getEventEnd() != null) {
				event.setEventVisibilityDates(format.format(event.getEventStart()) + " - " + format.format(event.getEventEnd()));
			}

			List<User> userTeamMemberList = new ArrayList<User>();

			for (UserPojo user : userListSumm) {
				try {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					userTeamMemberList.add(u);
					if (!maskingUserList.contains(user)) {
						maskingUserList.add(user);
					}

					if (!evaluationConclusionUserList.contains(user)) {
						evaluationConclusionUserList.add(user);
					}
				} catch (Exception e) {
					LOG.info("Error while cloning the user List :" + e.getMessage());
				}
			}

			userTeamMemberList.removeAll(assignedTeamMembers);

			// Event users for Approval
			List<UserPojo> approvalAllUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);

			for (UserPojo userPojo : approvalAllUserList) {
				User user = new User(userPojo.getId(), userPojo.getLoginId(), userPojo.getName(), userPojo.getCommunicationEmail(), userPojo.isEmailNotifications(), userPojo.getTenantId(), userPojo.isDeleted());
				if (!approvalUsers.contains(user)) {
					approvalUsers.add(user);
				}
			}
			
			for (UserPojo userPojo : approvalAllUserList) {
				User user = new User(userPojo.getId(), userPojo.getLoginId(), userPojo.getName(), userPojo.getCommunicationEmail(), userPojo.isEmailNotifications(), userPojo.getTenantId(), userPojo.isDeleted());
				if (!suspApprovalUsers.contains(user)) {
					suspApprovalUsers.add(user);
				}
			}
			
			model.addAttribute("userTeamMemberList", userTeamMemberList);
			model.addAttribute("userList1", approvalUsers);
			model.addAttribute("suspApprvlUserList", suspApprovalUsers);
			model.addAttribute("maskingUserList", maskingUserList);
			model.addAttribute("evaluationConclusionUsers", evaluationConclusionUserList);
			model.addAttribute("isIdSettingOn", eventIdSettingsDao.isBusinessSettingEnable(SecurityLibrary.getLoggedInUserTenantId(), getEventType().name()));

			LOG.info("Event : " + event.getId());
			constructDefaultCreatePageModel(model, event, eventType, null);
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
		}
		if (event.getViewSupplerName()) {
			event.setViewSupplerName(false);
		} else {
			event.setViewSupplerName(true);
		}
		model.addAttribute("event", event);
		LOG.info("Got Event details.................................................");
		return "createEventDetails";

	}

	private void constructDefaultCreatePageModel(Model model, Event event, RfxTypes eventType, BuyerSettings settings) throws JsonProcessingException {
		if (event != null && (null == event.getEventDetailCompleted() || (event.getEventDetailCompleted() != null && event.getEventDetailCompleted() == Boolean.FALSE))) {
			if (settings == null) {
				settings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (settings != null) {
					if (event.getBaseCurrency() == null) {
						event.setBaseCurrency(settings.getCurrency());
					}
					if (event.getDecimal() == null) {
						event.setDecimal(settings.getDecimal());
					}
				}
			}
		}

		model.addAttribute("currency", currencyService.getAllActiveCurrencies());

		model.addAttribute("event", event);
		model.addAttribute("intervalType", IntervalType.values());
		TableDataInput filter = new TableDataInput();
		filter.setStart(1);
		filter.setLength(10);
		model.addAttribute("industryCategoryList", industryCategoryService.findIndustryCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), filter));
		if (event != null && event.getDeliveryAddress() != null) {
			BuyerAddress buyerAddress = buyerAddressService.getBuyerAddress(event.getDeliveryAddress().getId());
			model.addAttribute("buyerAddress", buyerAddress);
		}
		List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("addressList", addressList);
		model.addAttribute("isIdSettingOn", eventIdSettingsDao.isBusinessSettingEnable(SecurityLibrary.getLoggedInUserTenantId(), getEventType().name()));

		// constructDatabaseEventCategories(model, event, eventType);

	}

	public boolean checkToAddSupplier(String eventId, String[] industryCat, List<IndustryCategory> industryCategories, String templateId) {
		List<String> mismatchIds = new ArrayList<String>();
		RfxTemplate rfxTemplate = null;
		Boolean filterByIndustryCategory = rfxTemplateService.findTemplateIndustryCategoryFlagByEventId(eventId, getEventType());
		if (filterByIndustryCategory == null) {
			filterByIndustryCategory = Boolean.FALSE;
		}
		if (StringUtils.checkString(templateId).length() > 0) {
			rfxTemplate = rfxTemplateService.getRfxTemplateById(templateId);
		}

		LOG.info("================================" + filterByIndustryCategory);
		if (filterByIndustryCategory) {
			switch (getEventType()) {
			case RFA: {
				List<String> dbCategory = rftEventService.getIndustryCategoriesIdForRftById(eventId, getEventType());
				if (CollectionUtil.isNotEmpty(dbCategory) && industryCat != null) {
					if (industryCat.length == dbCategory.size()) {
						for (String string : industryCat) {
							if (dbCategory.contains(string)) {
								continue;
							}
							mismatchIds.add(string);
						}
					} else {
						mismatchIds.addAll(dbCategory);
					}
				} else {
					mismatchIds.addAll(dbCategory);
				}
				if (CollectionUtil.isNotEmpty(mismatchIds) && (rfxTemplate != null && Boolean.TRUE == rfxTemplate.getAutoPopulateSupplier())) {
					rfaEventSupplierService.deleteAllSuppliersByEventId(eventId);
					List<FavouriteSupplier> favouriteSupplier = favoriteSupplierService.findInvitedSupplierByIndCat(null, Boolean.TRUE, null, SecurityLibrary.getLoggedInUserTenantId(), industryCategories, null, null);
					if (CollectionUtil.isNotEmpty(favouriteSupplier)) {
						Set<FavouriteSupplier> favSupp1 = new HashSet<>(favouriteSupplier);
						List<RfaEventSupplier> eventSuppliers = new ArrayList<RfaEventSupplier>();
						for (FavouriteSupplier favSupp : favSupp1) {
							RfaEventSupplier eventSupplier = new RfaEventSupplier();
							eventSupplier.setSupplier(favSupp.getSupplier());
							eventSupplier.setSupplierInvitedTime(new Date());
							eventSupplier.setRfxEvent(new RfaEvent(eventId));
							eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
							eventSuppliers.add(eventSupplier);
						}
						rfaEventSupplierService.batchInsert(eventSuppliers);
					}
					List<RfaEventMeeting> meetings = rfaEventMeetingService.getRfaMeetingByEventId(eventId);
					for (RfaEventMeeting meeting : meetings) {
						meeting.setInviteSuppliers(new ArrayList<Supplier>());
						rfaEventMeetingService.updateMeeting(meeting);
					}
				}
				break;
			}
			case RFI: {
				List<String> dbCategory = rftEventService.getIndustryCategoriesIdForRftById(eventId, getEventType());
				if (CollectionUtil.isNotEmpty(dbCategory) && industryCat != null) {
					if (industryCat.length == dbCategory.size()) {
						for (String string : industryCat) {
							if (dbCategory.contains(string)) {
								continue;
							}
							mismatchIds.add(string);
						}
					}
				} else {
					mismatchIds.addAll(dbCategory);
				}
				if (CollectionUtil.isNotEmpty(mismatchIds) && (rfxTemplate != null && Boolean.TRUE == rfxTemplate.getAutoPopulateSupplier())) {
					rfiEventSupplierService.deleteAllSuppliersByEventId(eventId);
					List<FavouriteSupplier> favouriteSupplier = favoriteSupplierService.findInvitedSupplierByIndCat(null, Boolean.TRUE, null, SecurityLibrary.getLoggedInUserTenantId(), industryCategories, null, null);
					if (CollectionUtil.isNotEmpty(favouriteSupplier)) {
						Set<FavouriteSupplier> favSupp1 = new HashSet<>(favouriteSupplier);
						List<RfiEventSupplier> eventSuppliers = new ArrayList<RfiEventSupplier>();
						for (FavouriteSupplier favSupp : favSupp1) {
							RfiEventSupplier eventSupplier = new RfiEventSupplier();
							eventSupplier.setSupplier(favSupp.getSupplier());
							eventSupplier.setSupplierInvitedTime(new Date());
							eventSupplier.setRfxEvent(new RfiEvent(eventId));
							eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
							eventSuppliers.add(eventSupplier);
						}
						rfiEventSupplierService.batchInsert(eventSuppliers);
					}

					List<RfiEventMeeting> meetings = rfiEventMeetingService.getMeetingsByEventId(eventId);
					for (RfiEventMeeting meeting : meetings) {
						meeting.setInviteSuppliers(new ArrayList<Supplier>());
						rfiEventMeetingService.updateMeeting(meeting);
					}
				}
				break;
			}
			case RFP: {
				List<String> dbCategory = rftEventService.getIndustryCategoriesIdForRftById(eventId, getEventType());
				if (CollectionUtil.isNotEmpty(dbCategory) && industryCat != null) {
					if (industryCat.length == dbCategory.size()) {
						for (String string : industryCat) {
							if (dbCategory.contains(string)) {
								continue;
							}
							mismatchIds.add(string);
						}
					}
				} else {
					mismatchIds.addAll(dbCategory);
				}
				if (CollectionUtil.isNotEmpty(mismatchIds) && (rfxTemplate != null && Boolean.TRUE == rfxTemplate.getAutoPopulateSupplier())) {
					rfpEventSupplierService.deleteAllSuppliersByEventId(eventId);
					List<FavouriteSupplier> favouriteSupplier = favoriteSupplierService.findInvitedSupplierByIndCat(null, Boolean.TRUE, null, SecurityLibrary.getLoggedInUserTenantId(), industryCategories, null, null);
					if (CollectionUtil.isNotEmpty(favouriteSupplier)) {
						Set<FavouriteSupplier> favSupp1 = new HashSet<>(favouriteSupplier);
						List<RfpEventSupplier> eventSuppliers = new ArrayList<RfpEventSupplier>();
						for (FavouriteSupplier favSupp : favSupp1) {
							RfpEventSupplier eventSupplier = new RfpEventSupplier();
							eventSupplier.setSupplier(favSupp.getSupplier());
							eventSupplier.setSupplierInvitedTime(new Date());
							eventSupplier.setRfxEvent(new RfpEvent(eventId));
							eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
							eventSuppliers.add(eventSupplier);
						}
						rfpEventSupplierService.batchInsert(eventSuppliers);
					}
					List<RfpEventMeeting> meetings = rfpEventMeetingService.getMeetingsByEventId(eventId);
					for (RfpEventMeeting meeting : meetings) {
						meeting.setInviteSuppliers(new ArrayList<Supplier>());
						rfpEventMeetingService.updateMeeting(meeting);
					}

				}
				break;
			}
			case RFQ: {
				List<String> dbCategory = rftEventService.getIndustryCategoriesIdForRftById(eventId, getEventType());
				if (CollectionUtil.isNotEmpty(dbCategory) && industryCat != null) {
					if (industryCat.length == dbCategory.size()) {
						for (String string : industryCat) {
							if (dbCategory.contains(string)) {
								continue;
							}
							mismatchIds.add(string);
						}
					}
				} else {
					mismatchIds.addAll(dbCategory);
				}
				if (CollectionUtil.isNotEmpty(mismatchIds) && (rfxTemplate != null && Boolean.TRUE == rfxTemplate.getAutoPopulateSupplier())) {
					rfqEventSupplierService.deleteAllSuppliersByEventId(eventId);
					List<FavouriteSupplier> favouriteSupplier = favoriteSupplierService.findInvitedSupplierByIndCat(null, Boolean.TRUE, null, SecurityLibrary.getLoggedInUserTenantId(), industryCategories, null, null);
					if (CollectionUtil.isNotEmpty(favouriteSupplier)) {
						Set<FavouriteSupplier> favSupp1 = new HashSet<>(favouriteSupplier);
						List<RfqEventSupplier> eventSuppliers = new ArrayList<RfqEventSupplier>();
						for (FavouriteSupplier favSupp : favSupp1) {
							RfqEventSupplier eventSupplier = new RfqEventSupplier();
							eventSupplier.setSupplier(favSupp.getSupplier());
							eventSupplier.setSupplierInvitedTime(new Date());
							eventSupplier.setRfxEvent(new RfqEvent(eventId));
							eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
							eventSuppliers.add(eventSupplier);
						}
						rfqEventSupplierService.batchInsert(eventSuppliers);
					}
					List<RfqEventMeeting> meetings = rfqEventMeetingService.getMeetingsByEventId(eventId);
					for (RfqEventMeeting meeting : meetings) {
						meeting.setInviteSuppliers(new ArrayList<Supplier>());
						rfqEventMeetingService.updateMeeting(meeting);
					}

				}
				break;
			}
			case RFT:
				List<String> dbCategory = rftEventService.getIndustryCategoriesIdForRftById(eventId, getEventType());
				if (CollectionUtil.isNotEmpty(dbCategory) && industryCat != null) {
					if (industryCat.length == dbCategory.size()) {
						for (String string : industryCat) {
							if (dbCategory.contains(string)) {
								continue;
							}
							mismatchIds.add(string);
						}
					} else {
						mismatchIds.addAll(dbCategory);
					}
				} else {
					mismatchIds.addAll(dbCategory);
				}
				if (CollectionUtil.isNotEmpty(mismatchIds) && (rfxTemplate != null && Boolean.TRUE == rfxTemplate.getAutoPopulateSupplier())) {
					rftEventSupplierService.deleteAllSuppliersByEventId(eventId);
					List<FavouriteSupplier> favouriteSupplier = favoriteSupplierService.findInvitedSupplierByIndCat(null, Boolean.TRUE, null, SecurityLibrary.getLoggedInUserTenantId(), industryCategories, null, null);
					if (CollectionUtil.isNotEmpty(favouriteSupplier)) {
						Set<FavouriteSupplier> favSupp1 = new HashSet<>(favouriteSupplier);
						List<RftEventSupplier> eventSuppliers = new ArrayList<RftEventSupplier>();
						for (FavouriteSupplier favSupp : favSupp1) {
							RftEventSupplier eventSupplier = new RftEventSupplier();
							eventSupplier.setSupplier(favSupp.getSupplier());
							eventSupplier.setSupplierInvitedTime(new Date());
							eventSupplier.setRfxEvent(new RftEvent(eventId));
							eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
							eventSuppliers.add(eventSupplier);
						}
						rftEventSupplierService.batchInsert(eventSuppliers);
					}
					List<RftEventMeeting> meetings = rftEventMeetingService.getMeetingsByEventId(eventId);
					for (RftEventMeeting meeting : meetings) {
						meeting.setInviteSuppliers(new ArrayList<Supplier>());
						rftEventMeetingService.updateMeeting(meeting);
					}

				}
				break;
			default:
				break;
			}
		}
		return false;
	}

}