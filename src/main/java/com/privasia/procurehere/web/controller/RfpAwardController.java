package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.dao.*;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import com.privasia.procurehere.core.pojo.SapResponseToTransferAward;
import com.privasia.procurehere.service.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.web.editors.RfpAwardApprovalEditor;

import freemarker.template.Configuration;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

@Controller
@RequestMapping("/buyer/RFP")
public class RfpAwardController {

	protected static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfpAwardService eventAwardService;

	@Autowired
	RfpBqService rfpBqService;

	@Autowired
	EventAwardAuditService eventAwardAuditService;

	@Autowired
	ErpSetupDao erpSetupDao;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	MessageSource messageSource;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	PrTemplateService prTemplateService;

	@Autowired
	PrService prService;

	@Autowired
	UserService userService;

	@Autowired
	RfpSupplierBqItemService rfpSupplierBqItemService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	RfpEventAwardDao rfpEventAwardDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	GroupCodeService groupCodeService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	SupplierPerformanceTemplateService spTemplateService;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	SupplierPerformanceFormService spFormService;

	@Autowired
	RfpAwardApprovalEditor rfpAwardApprovalEditor;

	@Autowired
	AwardApprovalService awardApprovalService;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	RfpEventDao rfpEventDao;

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setAutoGrowCollectionLimit(2000); // this is for by default only 256 array of object allowed here
		dataBinder.registerCustomEditor(RfpAwardApprovalUser.class, rfpAwardApprovalEditor);
	}

	@ModelAttribute("productCategory")
	public List<ProductCategory> getAllProductCategory() {
		List<ProductCategory> productList = productCategoryMaintenanceService.getAllProductCategoryByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		if (CollectionUtil.isNotEmpty(productList)) {
			return productList;
		} else {
			return null;
		}
	}

	@ModelAttribute("prTemplate")
	public List<PrTemplate> getAllPrTemplate() {
		List<PrTemplate> allPrTemplate = prTemplateService.findAllPrTemplatesForTenantAndUser(SecurityLibrary.getLoggedInUserTenantId());
		LOG.info("================" + allPrTemplate.size());
		if (CollectionUtil.isNotEmpty(allPrTemplate)) {
			return allPrTemplate;
		} else {
			return null;
		}
	}

	@ModelAttribute("prCreator")
	public List<User> getPrCreator() {
		List<User> prCreatorUser = userService.getPrCreatorUser(SecurityLibrary.getLoggedInUserTenantId());
		if (CollectionUtil.isNotEmpty(prCreatorUser)) {
			return prCreatorUser;
		} else {
			return null;
		}
	}

	@ModelAttribute("formOwner")
	public List<User> getFormOwner() {
		List<User> formOwnerUsers = userService.fetchAllActiveUserForTenantId(SecurityLibrary.getLoggedInUserTenantId());
		if (CollectionUtil.isNotEmpty(formOwnerUsers)) {
			return formOwnerUsers;
		} else {
			return null;
		}
	}

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	@ModelAttribute("groupCodeList")
	public List<GroupCode> getGroupCode() {
		List<GroupCode> groupCodeList = groupCodeService.fetchAllGroupCodesForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		if (CollectionUtil.isNotEmpty(groupCodeList)) {
			return groupCodeList;
		} else {
			return null;
		}
	}

	@ModelAttribute("itemTypeList")
	public List<ProductItemType> getProductItemType() {
		return Arrays.asList(ProductItemType.values());
	}

	@RequestMapping(path = { "/eventAward/{eventId}/{bqId}", "/eventAward/{eventId}" }, method = RequestMethod.GET)
	public ModelAndView getEventAward(@PathVariable("eventId") String eventId, @PathVariable(value = "bqId", required = false) String bqId, Model model) {
		LOG.info("Hi this is event award Controller eventId  :" + eventId + "=== bqId :" + bqId);
		try {
			List<User> awardApprovalUsers = new ArrayList<User>();
			if (eventId != null) {
				RfpEvent event = rfpEventService.getRfpEventByeventId(eventId);
				rfpEventService.updateEventAward(eventId);
				model.addAttribute("event", event);
				model.addAttribute("eventType", RfxTypes.RFP);
				AwardCriteria[] list = new AwardCriteria[3];
				list[0] = AwardCriteria.LOWEST_ITEMIZED_PRICE;
				list[1] = AwardCriteria.LOWEST_TOTAL_PRICE;
				list[2] = AwardCriteria.MANUAL;
				model.addAttribute("awardCriteria", list);

				// model.addAttribute("awardCriteria", AwardCriteria.values());

				List<Supplier> suppliers = rfpEventSupplierService.getEventQualifiedSuppliersForEvaluation(event.getId());
				List<RfpEventBq> rfpbq = rfpBqService.getAllBqListByEventId(eventId);
				if (StringUtils.checkString(bqId).length() == 0) {
					bqId = rfpbq.get(0).getId();
				}
				LOG.info("Award Approvals " + event.getAwardApprovals().size());
				List<RfpEventAwardApproval> approval = event.getAwardApprovals();
				List<User> userAppList = new ArrayList<User>();
				if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
					for (RfpEventAwardApproval approvalLevel : event.getAwardApprovals()) {
						if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
							for (RfpAwardApprovalUser user : approvalLevel.getApprovalUsers()) {
								User u = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
								if (!userAppList.contains(u)) {
									userAppList.add(u);
								}
							}
						}
					}
				}

				List<User> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
				List<RfpAwardApprovalUser> approvalList = new ArrayList<RfpAwardApprovalUser>();
				for (User user : userList) {
					approvalList.add(new RfpAwardApprovalUser(user));
				}

				List<RfpSupplierBq> suppBqArr = new ArrayList<RfpSupplierBq>();
				RfpSupplierBq supplierBq = new RfpSupplierBq();

				if (rfpbq != null) {
					for (RfpEventBq bq : rfpbq) {
						RfpSupplierBq rfpSupplierBq = rfpEventService.getSupplierBQOfLeastTotalPrice(eventId, bq.getId(), SecurityLibrary.getLoggedInUserTenantId());
						suppBqArr.add(rfpSupplierBq);
					}
				}
				// RfpEventAward rfpEventAward = eventAwardService.rfpEventAwardByEventIdandBqId(eventId, bqId);
				RfpEventAward rfpEventAward = eventAwardService.rfpEventAwardDetailsByEventIdandBqId(eventId, bqId);
				if (rfpEventAward == null) {
					RfpEventAward awd = new RfpEventAward();
					awd.setAwardCriteria(AwardCriteria.LOWEST_TOTAL_PRICE);
					supplierBq = rfpEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
					model.addAttribute("supplierBq", supplierBq);
					if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
						awd.setAwardApprovals(approval);
					}
					awd.setRfxEvent(event);
					model.addAttribute("eventAward", awd);
				} else {
					LOG.info("Event Award not Null" + rfpEventAward.getId());
					if (AwardCriteria.LOWEST_TOTAL_PRICE == rfpEventAward.getAwardCriteria()) {
						supplierBq = rfpEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
					} else if (AwardCriteria.LOWEST_ITEMIZED_PRICE == rfpEventAward.getAwardCriteria()) {
						supplierBq = rfpEventService.getSupplierBQOfLowestItemisedPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
					} else if (AwardCriteria.MANUAL == rfpEventAward.getAwardCriteria()) {
						if (CollectionUtil.isNotEmpty(suppliers)) {
							supplierBq = rfpEventService.getSupplierBQwithSupplierId(eventId, bqId, suppliers.get(0).getId(), SecurityLibrary.getLoggedInUserTenantId(), rfpEventAward.getId());
						} else {
							LOG.info("no qualified supplier in list");
						}
					}
					model.addAttribute("supplierBq", supplierBq);
					if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
						rfpEventAward.setAwardApprovals(approval);
					}
					rfpEventAward.setRfxEvent(event);
					model.addAttribute("eventAward", rfpEventAward);
				}

				List<BusinessUnit> businessUnitData = businessUnitService.getBusinessForContractFromAwardDetails(SecurityLibrary.getLoggedInUserTenantId(), event.getBusinessUnit());
				if (!businessUnitData.contains(event.getBusinessUnit())) {
					businessUnitData.add(0, event.getBusinessUnit());
				}
				model.addAttribute("businessUnitData", businessUnitData);
				Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());

				if (enableCorrelation != null && enableCorrelation == Boolean.TRUE) {
					List<CostCenter> costCenterList = costCenterService.getCostCentersByBusinessUnitIdForAwardScreen(event.getBusinessUnit().getId());
					if (event.getCostCenter() != null) {
						costCenterList.add(0, event.getCostCenter());
					}
					model.addAttribute("costCenterList", costCenterList);
				} else {
					List<CostCenter> costCenterList = costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId());
					model.addAttribute("costCenterList", costCenterList);
				}
				model.addAttribute("userList", approvalList);
				model.addAttribute("userList1", userAppList);
				model.addAttribute("awardUserList", awardApprovalUsers);
				model.addAttribute("eventSuppliers", suppliers);
				model.addAttribute("suppBqId", bqId);
				model.addAttribute("supplierBqs", suppBqArr);

				List<RfpEventAwardAudit> rfpEventAwardAuditList = eventAwardAuditService.findAllAwardAuditForTenantIdAndRfpEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				model.addAttribute("awardAuditList", rfpEventAwardAuditList);

				if(rfpEventAward != null && rfpEventAward.getFileName() != null) {
					model.addAttribute("fileId", rfpEventAward.getId());
					model.addAttribute("fileName", rfpEventAward.getFileName());
				}

				ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("erpSetup", erpSetup);
				model.addAttribute("changeCriteria", Boolean.FALSE);
				model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				model.addAttribute("showAwardTab", Boolean.TRUE);
				model.addAttribute("event", event);
				if (event.getTemplate() != null) {
					RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(event.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
					model.addAttribute("rfxTemplate", rfxTemplate);
				}
			}
		} catch (Exception e) {
			LOG.error("Error getting award details : " + e.getMessage(), e);
			model.addAttribute("error", "Error fetching award details : " + e.getMessage());
		}

		return new ModelAndView("eventAwardDetailForSAP");
	}

	@RequestMapping(path = { "/concludeEventAward" }, method = RequestMethod.POST)
	public String concludeEventAward(@ModelAttribute RfpEventAward rfpEventAward, HttpSession session, Model model, RedirectAttributes redir) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			saveProductItem(rfpEventAward);
			LOG.info(rfpEventAward.getRfxEvent().getId());

			if (rfpEventAward.getRfxEvent().getId() != null) {
				RfpEventAward eventAward = eventAwardService.saveEventAward(rfpEventAward, session, SecurityLibrary.getLoggedInUser(), false, true);
				awardApprovalService.doApproval(eventAward.getRfxEvent(), session, SecurityLibrary.getLoggedInUser(), virtualizer, eventAward.getId(), Boolean.TRUE);
				return "redirect:/buyer/RFP/eventAward/" + rfpEventAward.getRfxEvent().getId() + "/" + eventAward.getBq().getId();
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", e.getMessage());
			LOG.info(e.getMessage(), e);
			return "redirect:/buyer/RFP/eventAward/" + rfpEventAward.getRfxEvent().getId();
		}
		return "redirect:/buyer/RFP/eventAward/" + rfpEventAward.getRfxEvent().getId();
	}

	@RequestMapping(path = { "/saveEventAward", "/saveEventAward/{transfer}" }, method = RequestMethod.POST)
	public String saveEventAward(@ModelAttribute RfpEventAward rfpEventAward, @PathVariable(value = "transfer", required = false) Boolean transfer, HttpSession session, Model model, RedirectAttributes redir) {
		try {
			saveProductItem(rfpEventAward);
			LOG.info(rfpEventAward.getRfxEvent().getId());

			if (rfpEventAward.getRfxEvent().getId() != null) {
				RfpEventAward eventAward = eventAwardService.saveEventAward(rfpEventAward, session, SecurityLibrary.getLoggedInUser(), false, false);

				try {
					RfpEventAudit audit = new RfpEventAudit();
					audit.setActionDate(new Date());
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setDescription("Award for Event ' " + eventAward.getRfxEvent().getEventName() + "'  is saved");
					audit.setEvent(eventAward.getRfxEvent());
					audit.setAction(AuditActionType.Award);
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.AWARD, "Award saved for event \"" + eventAward.getRfxEvent().getEventId() + "\"", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				return "redirect:/buyer/RFP/eventAward/" + rfpEventAward.getRfxEvent().getId() + "/" + eventAward.getBq().getId();
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", e.getMessage());
			LOG.info(e.getMessage(), e);
			return "redirect:/buyer/RFP/eventAward/" + rfpEventAward.getRfxEvent().getId();
		}
		return "redirect:/buyer/RFP/eventAward/" + rfpEventAward.getRfxEvent().getId();
	}

	@RequestMapping(path = "/criteria/{eventType}", method = RequestMethod.POST)
	public ModelAndView criteria(@PathVariable("eventType") RfxTypes eventType, @RequestParam("eventId") String eventId, @RequestParam("bqId") String bqId, @RequestParam("awardCriteria") AwardCriteria criteria, Model model) {
		LOG.info(eventId);
		LOG.info(bqId);
		LOG.info(eventType);
		LOG.info(criteria);
		try {

			if (eventId != null) {
				RfpEvent event = rfpEventService.getRfpEventByeventId(eventId);
				model.addAttribute("event", event);
				model.addAttribute("eventType", eventType);
				AwardCriteria[] list = new AwardCriteria[3];
				list[0] = AwardCriteria.LOWEST_ITEMIZED_PRICE;
				list[1] = AwardCriteria.LOWEST_TOTAL_PRICE;
				list[2] = AwardCriteria.MANUAL;
				model.addAttribute("awardCriteria", list);

				// model.addAttribute("awardCriteria", AwardCriteria.values());
				List<Supplier> suppliers = rfpEventSupplierService.getEventQualifiedSuppliersForEvaluation(event.getId());
				List<RfpEventBq> rfpbq = rfpBqService.getAllBqListByEventId(eventId);
				List<RfpSupplierBq> suppBqArr = new ArrayList<RfpSupplierBq>();

				if (rfpbq != null) {
					for (RfpEventBq bq : rfpbq) {
						LOG.info(bq.getId());
						RfpSupplierBq rfpSupplierBq = rfpEventService.getSupplierBQOfLeastTotalPrice(eventId, bq.getId(), SecurityLibrary.getLoggedInUserTenantId());
						suppBqArr.add(rfpSupplierBq);
					}
				}

				RfpSupplierBq rfpSupplierBq = new RfpSupplierBq();

				// RfpEventAward rfpEventAward = eventAwardService.rfpEventAwardByEventIdandBqId(eventId, bqId);
				RfpEventAward rfpEventAward = eventAwardService.rfpEventAwardDetailsByEventIdandBqId(eventId, bqId);
				if (AwardCriteria.LOWEST_TOTAL_PRICE == criteria) {
					LOG.info(eventId + "   " + bqId);
					rfpSupplierBq = rfpEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
				} else if (AwardCriteria.LOWEST_ITEMIZED_PRICE == criteria) {
					rfpSupplierBq = rfpEventService.getSupplierBQOfLowestItemisedPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
				} else if (AwardCriteria.MANUAL == criteria) {
					// rfpSupplierBq = rfpEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId);
					if (CollectionUtil.isNotEmpty(suppliers)) {
						rfpSupplierBq = rfpEventService.getSupplierBQwithSupplierId(eventId, bqId, suppliers.get(0).getId(), SecurityLibrary.getLoggedInUserTenantId(), null);
						if (rfpEventAward != null && rfpEventAward.getAwardCriteria() != criteria) {
							if (CollectionUtil.isNotEmpty(rfpEventAward.getRfxAwardDetails())) {
								for (RfpEventAwardDetails detail : rfpEventAward.getRfxAwardDetails()) {
									detail.setSupplier(suppliers.get(0));
								}
							}
						}
					} else {
						LOG.info("No qualifed supplier in list....");
					}
				}

				if (rfpEventAward == null) {
					LOG.info("Event Award Null");
					model.addAttribute("changeCriteria", Boolean.TRUE);
					RfpEventAward eventAward = new RfpEventAward();
					eventAward.setAwardCriteria(criteria);
					eventAward.setRfxEvent(event);
					model.addAttribute("eventAward", eventAward);
				} else {
					model.addAttribute("changeCriteria", rfpEventAward.getAwardCriteria() != criteria);
					LOG.info("Event Award not Null..." + rfpEventAward.getId());
					rfpEventAward.setAwardCriteria(criteria);
					rfpEventAward.setRfxEvent(event);
					model.addAttribute("eventAward", rfpEventAward);
				}
				List<BusinessUnit> businessUnitData = businessUnitService.getBusinessForContractFromAwardDetails(SecurityLibrary.getLoggedInUserTenantId(), event.getBusinessUnit());
				if (!businessUnitData.contains(event.getBusinessUnit())) {
					businessUnitData.add(0, event.getBusinessUnit());
				}
				model.addAttribute("businessUnitData", businessUnitData);

				Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());

				if (enableCorrelation != null && enableCorrelation == Boolean.TRUE) {
					List<CostCenter> costCenterList = costCenterService.getCostCentersByBusinessUnitIdForAwardScreen(event.getBusinessUnit().getId());
					if (event.getCostCenter() != null) {
						costCenterList.add(0, event.getCostCenter());
					}
					model.addAttribute("costCenterList", costCenterList);
				} else {
					List<CostCenter> costCenterList = costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId());
					model.addAttribute("costCenterList", costCenterList);
				}
				model.addAttribute("eventSuppliers", suppliers);
				model.addAttribute("supplierBq", rfpSupplierBq);
				model.addAttribute("supplierBqs", suppBqArr);
				model.addAttribute("suppBqId", bqId);
				model.addAttribute("showAwardTab", Boolean.TRUE);
				model.addAttribute("awardAuditList", eventAwardAuditService.findAllAwardAuditForTenantIdAndRfpEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId));
				ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("erpSetup", erpSetup);
				if (event.getTemplate() != null) {
					RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(event.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
					model.addAttribute("rfxTemplate", rfxTemplate);
				}
			}
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
		}
		return new ModelAndView("eventAwardDetailForSAP");
	}

	@RequestMapping(path = "/getSupplierData/{eventType}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RfpSupplierBqItem> criteria(@PathVariable("eventType") RfxTypes eventType, @RequestParam("eventId") String eventId, @RequestParam("bqItemId") String bqItemId, @RequestParam("supplierId") String supplierId) {

		HttpHeaders headers = new HttpHeaders();
		RfpSupplierBqItem bqItem = null;
		try {

			bqItem = eventAwardService.getBqItemByBqItemId(bqItemId, supplierId, SecurityLibrary.getLoggedInUserTenantId());

		} catch (Exception e) {
			headers.add("error", "Cannot fetch supplier Item details : " + e.getMessage());
			return new ResponseEntity<RfpSupplierBqItem>(bqItem, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<RfpSupplierBqItem>(bqItem, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/transferAward/{eventId}/{bqId}/{rfpEventAwardId}", method = RequestMethod.POST)
	public ModelAndView transferAward(@PathVariable("eventId") String eventId, @PathVariable("bqId") String bqId, @PathVariable("rfpEventAwardId") String rfpEventAwardId, RedirectAttributes redir, HttpSession session) {
		LOG.info("eventId :" + eventId);
		try {
			if (eventId != null) {
				eventAwardService.transferRfpAward(eventId, SecurityLibrary.getLoggedInUserTenantId(), session, SecurityLibrary.getLoggedInUser(), rfpEventAwardId, true, RfxTypes.RFP);
				// RfpEvent event = rfpEventService.getEventById(eventId);
				// if (event != null && event.getAwardDate() == null) {
				// event.setAwardDate(new Date());
				// rfpEventService.updateRfpEvent(event);
				// }

				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.successfully.transfered", new Object[] {}, Global.LOCALE));
			} else {
				LOG.info("Event ID is Null");
			}
		} catch (Exception e) {
			LOG.info("Error while transfer Event Award :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.transfer.award", new Object[] { e.getMessage() }, Global.LOCALE));
			try {

				RfpEvent event = rfpEventService.getRfpEventByeventId(eventId);
				if (event != null) {
					sendErrorNotificationWhileCreating(SecurityLibrary.getLoggedInUser(), event.getReferanceNumber(), e.getMessage());
				}
			} catch (Exception err) {
				LOG.info("Error while sending email for transferAward", err.getMessage(), err);
			}
		}
		return new ModelAndView("redirect:/buyer/RFP/eventAward/" + eventId + "/" + bqId);
	}

	@RequestMapping(value = "/downloadAwardSnapShot/{id}", method = RequestMethod.GET)
	public void downloadAwardSnapShot(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Award Audid Id  :: :: " + id + "::::::");
			eventAwardService.downloadAwardAuditSnapshot(id, response);
		} catch (Exception e) {
			LOG.error("Error while downloading Award Audit snapshot: " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/downloadAwardExcelSnapShot/{id}", method = RequestMethod.GET)
	public void downloadAwardExcelSnapShot(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Award Audid Id  :: :: " + id + "::::::");
			eventAwardService.downloadAwardAuditExcelSnapShot(id, response);
		} catch (Exception e) {
			LOG.error("Error while downloading Award Audit snapshot: " + e.getMessage(), e);
		}

	}

	@RequestMapping(value = "/downloadAwardAttachFile/{id}", method = RequestMethod.GET)
	public void downloadAwardAttachFile(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Event Award  Id  :: :: " + id + "::::::");
			eventAwardService.downloadAwardAttachFileSnapShot(id, response);
		} catch (Exception e) {
			LOG.error("Error while downloading Award Audit snapshot: " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/downloadAwardFile/{id}", method = RequestMethod.GET)
	public void downloadAwardFile(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Event Award  Id  :: :: " + id + "::::::");
			eventAwardService.downloadAwardAttachFile(id, response);
		} catch (Exception e) {
			LOG.error("Error while downloading Award Audit snapshot: " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/checkProductItemExistOrNot", method = RequestMethod.POST)
	public ResponseEntity<String> checkProductItemExistOrNot(HttpServletResponse response, RedirectAttributes redir, @RequestParam(required = false) String itemAndSupplierId) {
		try {
			LOG.info("itemVal " + itemAndSupplierId);
			String[] itemandSupplier = itemAndSupplierId.split("-");
			String itemId = itemandSupplier[0];
			LOG.info("*****itemId*******        " + itemId);
			String supplierId = itemandSupplier[1];

			RfpSupplierBqItem supplierBqItem = rfpSupplierBqItemService.getBqItemByRfpBqItemId(itemId, supplierId);

			LOG.info("*****supplierBqItem.getItemName()***********    " + supplierBqItem.getItemName());
			if ((StringUtils.checkString(supplierBqItem.getItemName()).length() > 0) && (StringUtils.checkString(supplierId).length() > 0)) {
				ProductItem productItem = productCategoryMaintenanceService.checkProductItemExistOrNot(supplierBqItem.getItemName(), supplierId, SecurityLibrary.getLoggedInUserTenantId());
				if (productItem != null) {
					return new ResponseEntity<String>("", HttpStatus.OK);

				}
			}

		} catch (Exception e) {
			LOG.error("Error while checkng item already exist or not:  " + e.getMessage(), e);
		}
		return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	private String getErpNotifiactionEmailsByBuyerSettings(String tenantId) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				LOG.info("fetching buyer setting-------------------");
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(tenantId);
				if (buyerSettings != null) {
					return buyerSettings.getErpNotificationEmails();
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer setting :" + e.getMessage(), e);
		}
		return null;
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

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				LOG.info("Sending request email to : " + mailTo);
				String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(template), map);
				notificationService.sendEmail(mailTo, subject, message);
			} catch (Exception e) {
				LOG.error("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	private void sendErrorNotificationWhileCreating(User user, String prNo, String error) {
		String mailTo = "";
		String subject = "Error ERP event added in ERP Event List";
		String url = APP_URL + "/buyer/erpManualList";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {

			mailTo = getErpNotifiactionEmailsByBuyerSettings(user.getTenantId());
			map.put("userName", "");
			map.put("prNo", prNo);
			map.put("error", error);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("appUrl", url);
			map.put("loginUrl", APP_URL + "/login");
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.ERP_EVENT_ERROR_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending pending mail For adding Erp audit into manual list :" + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("erp.added.notification.message", new Object[] { prNo }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending notification For Event CREATION :" + e.getMessage(), e);
		}

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

	@RequestMapping(path = "/transferEventAwardToPr/{templateId}/{userId}/{eventId}", method = RequestMethod.POST)
	public String transferEventAwardToPr(@ModelAttribute RfpEventAward rfpEventAward, @PathVariable String templateId, @PathVariable("eventId") String eventId, @PathVariable String userId, Model model) {
		LOG.info("Template Id:" + templateId);
		LOG.info("Pr Creater Id" + userId);
		try {
			saveProductItem(rfpEventAward);

			// no need to do this yogesh
			/*
			 * for (RfpEventAwardDetails rfxAward : rfpEventAward.getRfxAwardDetails()) { if
			 * (rfxAward.getBqItem().getOrder()!=0 && !(StringUtils.checkString(rfxAward.getProductCategory()).length()
			 * > 0)) { model.addAttribute("error", "Error generating PR from award : Select category for all Item.");
			 * return "redirect:/buyer/RFP/eventAward/" + eventId; } }
			 */
			String msg = rfpEventService.createPrFromAward(rfpEventAward, templateId, userId, SecurityLibrary.getLoggedInUser());
			rfpEventService.updatePrPushDate(eventId);
			RfpEventAudit audit = new RfpEventAudit();
			RfpEvent event = new RfpEvent();
			event.setId(eventId);
			audit.setEvent(event);
			audit.setAction(AuditActionType.Create);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			String prId = msg.substring(msg.indexOf(":") + 1);
			audit.setDescription("Event is pushed to PR : " + (StringUtils.checkString(prId).length() > 0 ? prId : ""));
			eventAuditService.save(audit);

			RfpEvent rfpEvent = rfpEventService.getPlainEventById(eventId);
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event is pushed to PR : " + (StringUtils.checkString(prId).length() > 0 ? prId : "") + " for event '" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
			buyerAuditTrailDao.save(buyerAuditTrail);

			Pr pr =prService.findPrbyprId(prId.trim());
			model.addAttribute("success", messageSource.getMessage("rfa.success.pr.created", new Object[] { msg != null ? msg : "" }, Global.LOCALE));
			return "redirect:/buyer/createPrDetails/" + pr.getId()+"?eventName="+rfpEvent.getEventName()+"&eventDescription="+rfpEvent.getEventDescription();
		} catch (Exception e) {
			LOG.error("Error generating Auto PRs during Event Award : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("rfa.award.error.generating", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return "redirect:/buyer/RFP/eventAward/" + eventId;
	}

	@RequestMapping(value = "/transferRfxAwardToErp/{eventId}/{rfpEventAwardId}", method = RequestMethod.POST)
	public ResponseEntity<SapResponseToTransferAward> transferAwardToERP(@PathVariable(name = "eventId") String eventId,
																		 @PathVariable(name = "rfpEventAwardId") String rfpEventAwardId,
																		 HttpSession session) throws Exception {
		ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
		SapResponseToTransferAward sapResponseToTransferAward = new SapResponseToTransferAward();
		HttpHeaders headers = new HttpHeaders();

		try {
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()) {
				sapResponseToTransferAward = erpIntegrationService.transferRfxAwardToErp(RfxTypes.RFP, eventId, erpSetup, SecurityLibrary.getLoggedInUser(),
						rfpEventAwardId, session);
			}
		}
		catch (ApplicationException applicationException) {
			headers.add("error", messageSource.getMessage("event.transfer.awrd.error", new Object[] {
					applicationException.getMessage() }, Global.LOCALE));
			RfpEvent rfpEvent = rfpEventService.getEventById(eventId);
			rfpEvent.setStatus(EventStatus.COMPLETE);
			rfpEvent.setStatus(EventStatus.COMPLETE);
			rfpEvent.setAwarded(false);
			rfpEvent.setAwardStatus(AwardStatus.DRAFT);
			rfpEventDao.saveOrUpdate(rfpEvent);
			return new ResponseEntity<SapResponseToTransferAward>(null , headers, HttpStatus.OK);
		}

		if(sapResponseToTransferAward == null || !sapResponseToTransferAward.getType().equals(SapToPrResponse.S)) {
			headers.add("error", messageSource.getMessage("event.transfer.awrd.error", new Object[] {
					sapResponseToTransferAward == null ? "" : sapResponseToTransferAward.getMessage() }, Global.LOCALE));
		}
		else {
			headers.add("success", messageSource.getMessage("event.transfer.awrd.success", new Object[] {
					sapResponseToTransferAward.getEventId() }, Global.LOCALE));
		}

		return new ResponseEntity<SapResponseToTransferAward>(null , headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/createContract/{eventId}", method = RequestMethod.POST)
	public String createContract(@ModelAttribute RfpEventAward rfpEventAward, @PathVariable("eventId") String eventId, @RequestParam("contractStartDateHid") String contractStartDate, @RequestParam("contractEndDateHid") String contractEndDate, @RequestParam("groupCodeHid") String groupCodeHid, @RequestParam("referenceNumberHid") String referenceNumberHid, @RequestParam("contractCreatorHid") String contractCreatorHid, Model model, HttpSession session) {
		LOG.info("Creating Contracct for Event :" + eventId);

		try {
			saveProductItem(rfpEventAward);

			String msg = rfpEventService.createContractFromAward(rfpEventAward, eventId, contractStartDate, contractEndDate, groupCodeHid, referenceNumberHid, SecurityLibrary.getLoggedInUser(), session, contractCreatorHid);
			// rfpEventService.updatePrPushDate(eventId);
			RfpEventAudit audit = new RfpEventAudit();
			RfpEvent event = rfpEventService.getPlainEventById(eventId);
			audit.setEvent(event);
			audit.setAction(AuditActionType.Create);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			String contractId = msg.substring(msg.indexOf(":") + 1);
			audit.setDescription("Event is pushed to Contract : " + (StringUtils.checkString(contractId).length() > 0 ? contractId : ""));
			eventAuditService.save(audit);

			try {
				BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event is pushed to Contract " + (StringUtils.checkString(contractId).length() > 0 ? contractId : "") + " for event " + event.getEventId(), SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(ownerAuditTrail);

				ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, " Contract : " + (StringUtils.checkString(contractId).length() > 0 ? contractId : "") + " Created ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ContractList);
				buyerAuditTrailDao.save(ownerAuditTrail);

			} catch (Exception e) {
				LOG.error("ERROR while Buyer Setting storing .. " + e.getMessage(), e);
			}

			model.addAttribute("success", messageSource.getMessage("event.success.contract.created", new Object[] { msg != null ? msg : "" }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error generating Auto Contract during Event Award : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("event.award.error.generating.contract", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return "redirect:/buyer/RFP/eventAward/" + eventId;
	}

	private void saveProductItem(RfpEventAward rfpEventAward) throws ApplicationException {
		if (CollectionUtil.isNotEmpty(rfpEventAward.getRfxAwardDetails())) {
			for (RfpEventAwardDetails rfxAward : rfpEventAward.getRfxAwardDetails()) {
				LOG.info("UOM----" + rfxAward.getBqItem().getUom() + "----" + rfxAward.getAwardedPrice());

				if (StringUtils.checkString(rfxAward.getSelectItem()).length() > 0) {
					String value = rfxAward.getSelectItem();
					String[] itemNameAndSupplierId = value.split("-");
					String itemId = itemNameAndSupplierId[0];
					LOG.info("****Item Name in Save****" + itemId);
					String supplierId = itemNameAndSupplierId[1];

					RfpSupplierBqItem supplierBqItem = rfpSupplierBqItemService.getBqItemByRfpBqItemId(itemId, supplierId);

					String itemName = supplierBqItem.getItemName();
					LOG.info("****Supplier Id in Save******" + supplierId);
					LOG.info("UOM----" + rfxAward.getBqItem().getUom() + "----" + rfxAward.getAwardedPrice());
					if (StringUtils.checkString(itemName).length() > 0 && StringUtils.checkString(supplierId).length() > 0) {
						ProductItem productItem = productCategoryMaintenanceService.checkProductItemExistOrNot(itemName, supplierId, SecurityLibrary.getLoggedInUserTenantId());

						if (productItem != null) {
							LOG.info("Item Name Update Price--------" + productItem.getProductName());
							LOG.info("****Supplier Id Update Price******" + rfxAward.getSupplier().getId());

							BigDecimal ap = rfxAward.getAwardedPrice();
							productItem.setUnitPrice(ap.divide(supplierBqItem.getQuantity(), 6, BigDecimal.ROUND_DOWN));

							// supplierBqItem.getQuantity();

							// productItem.setUnitPrice(rfxAward.getAwardedPrice());
							LOG.info("UNIT PRICE------" + productItem.getUnitPrice());
							productCategoryMaintenanceService.updateProductItemPrice(productItem);
						} else {
							productItem = new ProductItem();
							productItem.setProductName(itemName);
							LOG.info("Item Name New-----" + productItem.getProductName());

							if (StringUtils.checkString(rfxAward.getProductCode()).length() > 0) {
								productItem.setProductCode(rfxAward.getProductCode());
							} else {
								throw new ApplicationException("Please Select Product Code for: " + itemName);
							}

							if (supplierBqItem.getUom() != null) {
								LOG.info("Uom New" + supplierBqItem.getUom());
								productItem.setUom(supplierBqItem.getUom());
							}
							productItem.setTax(rfxAward.getTax());
							productItem.setCreatedBy(SecurityLibrary.getLoggedInUser());
							productItem.setCreatedDate(new Date());

							BigDecimal ap = rfxAward.getAwardedPrice();
							productItem.setUnitPrice(ap.divide(supplierBqItem.getQuantity(), 6, BigDecimal.ROUND_DOWN));

							productItem.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
							String productCategory = rfxAward.getProductCategory();
							if (StringUtils.checkString(productCategory).length() > 0) {
								LOG.info("Product Category-----------" + productCategory);
								ProductCategory category = new ProductCategory();
								category.setId(productCategory);
								productItem.setProductCategory(category);
							} else {
								throw new ApplicationException("Please Select Product Category for: " + itemName);
							}
							productItem.setBrand(rfxAward.getBrand());
							productItem.setProductItemType(rfxAward.getProductType());
							FavouriteSupplier favouriteSupplier = favoriteSupplierService.getFavouriteSupplierBySupplierId(supplierId, SecurityLibrary.getLoggedInUserTenantId());
							productItem.setFavoriteSupplier(favouriteSupplier);
							productItem.setStatus(Status.ACTIVE);
							productCategoryMaintenanceService.saveNewProductItem(productItem);
						}
					}
				}
			}
		}
	}

	@RequestMapping(path = "/getCostCenterByBusinessUnit", method = RequestMethod.GET)
	public ResponseEntity<List<CostCenter>> getCostCenterByBusinessUnit(@RequestParam("businessUnitId") String businessUnitId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("Business Unit " + businessUnitId);
		try {
			Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());

			if (enableCorrelation != null && enableCorrelation == Boolean.TRUE) {
				List<CostCenter> data = costCenterService.getCostCentersByBusinessUnitIdForAwardScreen(businessUnitId);
				return new ResponseEntity<List<CostCenter>>(data, headers, HttpStatus.OK);
			} else {
				List<CostCenter> data = costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId());
				return new ResponseEntity<List<CostCenter>>(data, headers, HttpStatus.OK);
			}

		} catch (Exception e) {
			LOG.error("Error fetching productList Contract list : " + e.getMessage(), e);
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<List<CostCenter>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/pushToEvaluation/{templateId}/{userId}/{eventId}", method = RequestMethod.POST)
	public String pushToEvaluation(@ModelAttribute RfpEventAward rfpEventAward, @PathVariable String templateId, @PathVariable("eventId") String eventId, @PathVariable String userId, Model model) {
		LOG.info("Creating Supplier Perfromance Evaluation Form......");
		try {
			List<String> formIds = rfpEventService.createSpFormFromAward(rfpEventAward, templateId, eventId, userId, SecurityLibrary.getLoggedInUser());
			String frmIds = String.join(",", formIds);

			RfpEventAudit audit = new RfpEventAudit();
			RfpEvent event = rfpEventService.getPlainEventById(eventId);
			audit.setEvent(event);
			audit.setAction(AuditActionType.Create);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setDescription("Event is pushed to Supplier Performance Evaluation : " + (StringUtils.checkString(frmIds).length() > 0 ? frmIds : ""));
			eventAuditService.save(audit);

			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event is pushed to Supplier Performance Evaluation : \"" + (StringUtils.checkString(frmIds).length() > 0 ? frmIds : "") + "\" for event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
			buyerAuditTrailDao.save(buyerAuditTrail);

			BuyerAuditTrail buyerAuditTrail2 = new BuyerAuditTrail(AuditTypes.CREATE, "Supplier Performance Evaluation Form : \"" + (StringUtils.checkString(frmIds).length() > 0 ? frmIds : "") + "\" Created.", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PerformanceEvaluation);
			buyerAuditTrailDao.save(buyerAuditTrail2);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");

			model.addAttribute("success", messageSource.getMessage("rfa.success.sp.created", new Object[] { frmIds }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error creating Supplier Performance Form : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("rfa.error.sp.created", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return "redirect:/buyer/RFP/eventAward/" + eventId;

	}

	@RequestMapping(path = "/approveAward", method = RequestMethod.POST)
	public ModelAndView approveAwardEvent(@RequestParam(name = "eventId") String eventId, @RequestParam("eventType") String eventType, @RequestParam("bqId") String bqId, @RequestParam String remarks, RedirectAttributes redir, HttpSession session, Model model) {
		LOG.info(SecurityLibrary.getLoggedInUserLoginId() + " is Approving Award " + eventType + " Event : " + eventId + " with remarks : " + remarks);
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
		try {
			Event theEvent = null;
			switch (eventType) {
			case "RFA": {
				RfaEvent event = new RfaEvent();
				event.setId(eventId);
				theEvent = awardApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, model, bqId);
				event.setEventName(theEvent.getEventName());
				break;
			}
			case "RFP": {
				RfpEvent event = new RfpEvent();
				event.setId(eventId);
				theEvent = awardApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, model, bqId);
				event.setEventName(theEvent.getEventName());
				break;
			}
			case "RFQ": {
				RfqEvent event = new RfqEvent();
				event.setId(eventId);
				theEvent = awardApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, model, bqId);
				event.setEventName(theEvent.getEventName());
				break;
			}
			case "RFT": {
				RftEvent event = new RftEvent();
				event.setId(eventId);
				theEvent = awardApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, model, bqId);
				event.setEventName(theEvent.getEventName());
				break;
			}
			default:
				break;
			}
			redir.addFlashAttribute("success", messageSource.getMessage("award.event.approved", new Object[] { theEvent.getEventId() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Approving Event :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.approving", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("redirect:/buyer/" + eventType + "/eventAward/" + eventId);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(path = "/rejectAward", method = RequestMethod.POST)
	public String rejectAward(@RequestParam(name = "eventId") String eventId, @RequestParam("eventType") String eventType, @RequestParam("bqId") String bqId, @RequestParam String remarks, RedirectAttributes redir, HttpSession session, Model model) {
		LOG.info(SecurityLibrary.getLoggedInUserLoginId() + " is Rejecting " + eventType + " Event : " + eventId + " with remarks : " + remarks);
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			if (StringUtils.checkString(remarks).length() == 0) {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.remark.required.rejection", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/" + eventType + "/eventAward/" + eventId;
			}
			Event theEvent = null;
			switch (eventType) {
			case "RFA": {
				RfaEvent event = new RfaEvent();
				event.setId(eventId);
				theEvent = awardApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, model, bqId);
				break;
			}
			case "RFP": {
				RfpEvent event = new RfpEvent();
				event.setId(eventId);
				theEvent = awardApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, model, bqId);
				break;
			}
			case "RFQ": {
				RfqEvent event = new RfqEvent();
				event.setId(eventId);
				theEvent = awardApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, model, bqId);
				break;
			}
			case "RFT": {
				RftEvent event = new RftEvent();
				event.setId(eventId);
				theEvent = awardApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, model, bqId);
				break;
			}
			default:
				break;
			}
			redir.addFlashAttribute("success", messageSource.getMessage("award.event.rejected", new Object[] { theEvent.getEventId() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Rejecting Event :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.rejecting", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/" + eventType + "/eventAward/" + eventId;
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/updateEventAwardApproval", method = RequestMethod.POST)
	public String updateEventAwardApproval(@ModelAttribute RfpEventAward eventAward, @RequestParam("eventId") String eventId, RedirectAttributes redir, HttpSession session) {
		LOG.info(" Event Id with request param " + eventId);
		try {
			Boolean isUpdated = eventAwardService.updateEventAwardApproval(eventAward, SecurityLibrary.getLoggedInUser(), eventId);
			RfpEvent event = rfpEventService.getPlainEventById(eventId);
			if (AwardStatus.PENDING == event.getAwardStatus() && Boolean.TRUE == isUpdated) {
				JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
				awardApprovalService.doApproval(event, session, SecurityLibrary.getLoggedInUser(), virtualizer, eventAward.getId(), Boolean.FALSE);
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.approval.updated", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Updating Approval :" + e.getMessage(), e);

			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.approval", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/RFP/eventAward/" + eventId;
	}

	@RequestMapping(value = "/deleteAwardAttachFile/{eventAwardId}", method = RequestMethod.GET)
	public ResponseEntity<?> deleteFile(@PathVariable String eventAwardId) {
		try {
			LOG.info("Event Award  Id  :: :: " + eventAwardId + "::::::");
			// We will use this to delete from the event Award previously it was for the event award audit
			eventAwardAuditService.deleteDocumentsByRfxAuditId(eventAwardId, RfxTypes.RFP);
			return new ResponseEntity<>("Document is Deleted", HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while deleting document: " + e.getMessage(), e);
			return new ResponseEntity<>("Issue in deleting document", HttpStatus.OK);
		}
	}

}
