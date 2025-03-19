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

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.ErpSetupDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.dao.RfaEventAwardDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.web.editors.RfaAwardApprovalEditor;

import freemarker.template.Configuration;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

@Controller
@RequestMapping("/buyer/RFA")
public class RfaAwardController {

	protected static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	// private RfxTypes eventType;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	PrService prService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfaAwardService eventAwardService;

	@Autowired
	RfaBqService rfaBqService;

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
	PrTemplateService prTemplateService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	UserService userService;

	@Autowired
	RfaSupplierBqItemService rfaSupplierBqItemService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	RfaEventAwardDao rfaEventAwardDao;

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
	AwardApprovalService awardApprovalService;

	@Autowired
	RfaAwardApprovalEditor rfaAwardApprovalEditor;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	RfaEventDao rfaEventDao;

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setAutoGrowCollectionLimit(2000); // this is for by default only 256 array of object allowed here
		dataBinder.registerCustomEditor(RfaAwardApprovalUser.class, rfaAwardApprovalEditor);

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

			if (eventId != null) {
				RfaEvent event = rfaEventService.getRfaEventById(eventId);
				rfaEventService.updateEventAward(eventId);
				model.addAttribute("event", event);
				model.addAttribute("eventType", RfxTypes.RFA);

				if (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID || event.getAuctionType() == AuctionType.FORWARD_DUTCH) {
					AwardCriteria[] list = new AwardCriteria[3];
					list[0] = AwardCriteria.HIGHEST_TOTAL_PRICE;
					list[1] = AwardCriteria.HIGHEST_ITEMIZED_PRICE;
					list[2] = AwardCriteria.MANUAL;
					model.addAttribute("awardCriteria", list);
				} else {
					AwardCriteria[] list = new AwardCriteria[3];
					list[0] = AwardCriteria.LOWEST_TOTAL_PRICE;
					list[1] = AwardCriteria.LOWEST_ITEMIZED_PRICE;
					list[2] = AwardCriteria.MANUAL;
					model.addAttribute("awardCriteria", list);
				}

				List<RfaEventBq> rfabq = rfaBqService.getAllBqListByEventId(eventId);
				if (StringUtils.checkString(bqId).length() == 0) {
					bqId = rfabq.get(0).getId();
				}
				List<Supplier> suppliers = rfaEventSupplierService.getEventQualifiedSuppliersForEvaluation(event.getId());
				List<RfaSupplierBq> suppBqArr = new ArrayList<RfaSupplierBq>();
				RfaSupplierBq supplierBq = new RfaSupplierBq();

				if (rfabq != null) {
					for (RfaEventBq bq : rfabq) {
						RfaSupplierBq rfaSupplierBq = rfaEventService.getSupplierBQOfLeastTotalPrice(eventId, bq.getId(), SecurityLibrary.getLoggedInUserTenantId());
						suppBqArr.add(rfaSupplierBq);
					}
				}

				LOG.info("Award Approvals " + event.getAwardApprovals().size());
				List<RfaEventAwardApproval> approval = event.getAwardApprovals();
				List<User> userAppList = new ArrayList<User>();
				if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
					for (RfaEventAwardApproval approvalLevel : event.getAwardApprovals()) {
						if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
							for (RfaAwardApprovalUser user : approvalLevel.getApprovalUsers()) {
								User u = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
								if (!userAppList.contains(u)) {
									userAppList.add(u);
								}
							}
						}
					}
				}
				List<User> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
				List<RfaAwardApprovalUser> approvalList = new ArrayList<RfaAwardApprovalUser>();
				for (User user : userList) {
					approvalList.add(new RfaAwardApprovalUser(user));
				}
				// RfaEventAward rfaEventAward = eventAwardService.rfaEventAwardByEventIdandBqId(eventId, bqId);
				// PH-1304
				RfaEventAward rfaEventAward = eventAwardService.rfaEventAwardDetailsByEventIdandBqId(eventId, bqId);

				if (rfaEventAward == null) {
					if (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID || event.getAuctionType() == AuctionType.FORWARD_DUTCH) {
						LOG.info("Event Award Null");
						RfaEventAward awd = new RfaEventAward();
						awd.setAwardCriteria(AwardCriteria.HIGHEST_TOTAL_PRICE);
						supplierBq = rfaEventService.getSupplierBQOfHighestTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
						model.addAttribute("supplierBq", supplierBq);
						if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
							awd.setAwardApprovals(approval);
						}
						awd.setRfxEvent(event);
						model.addAttribute("eventAward", awd);
					} else {
						LOG.info("Event Award Null");
						RfaEventAward awd = new RfaEventAward();
						awd.setAwardCriteria(AwardCriteria.LOWEST_TOTAL_PRICE);
						supplierBq = rfaEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
						model.addAttribute("supplierBq", supplierBq);
						if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
							awd.setAwardApprovals(approval);
						}
						awd.setRfxEvent(event);
						model.addAttribute("eventAward", awd);
					}

				} else {
					LOG.info("Event Award not Null" + rfaEventAward.getId());
					if (AwardCriteria.LOWEST_TOTAL_PRICE == rfaEventAward.getAwardCriteria()) {
						supplierBq = rfaEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
					} else if (AwardCriteria.LOWEST_ITEMIZED_PRICE == rfaEventAward.getAwardCriteria()) {
						supplierBq = rfaEventService.getSupplierBQOfLowestItemisedPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
					} else if (AwardCriteria.HIGHEST_TOTAL_PRICE == rfaEventAward.getAwardCriteria()) {
						supplierBq = rfaEventService.getSupplierBQOfHighestTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
					} else if (AwardCriteria.HIGHEST_ITEMIZED_PRICE == rfaEventAward.getAwardCriteria()) {
						supplierBq = rfaEventService.getSupplierBQOfHighestItemisedPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
					} else if (AwardCriteria.MANUAL == rfaEventAward.getAwardCriteria()) {
						// supplierBq = rfaEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId);
						if (CollectionUtil.isNotEmpty(suppliers)) {
							supplierBq = rfaEventService.getSupplierBQwithSupplierId(eventId, bqId, suppliers.get(0).getId(), SecurityLibrary.getLoggedInUserTenantId(), rfaEventAward.getId());
						} else {
							LOG.info("Qualified suppliers is empty");
						}
					}
					model.addAttribute("supplierBq", supplierBq);
					if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
						rfaEventAward.setAwardApprovals(approval);
					}
					rfaEventAward.setRfxEvent(event);
					model.addAttribute("eventAward", rfaEventAward);
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
				model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				model.addAttribute("eventSuppliers", suppliers);
				model.addAttribute("suppBqId", bqId);
				model.addAttribute("supplierBqs", suppBqArr);

				List<RfaEventAwardAudit> rfaEventAwardAuditList = eventAwardAuditService.findAllAwardAuditForTenantIdAndRfaEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				model.addAttribute("awardAuditList", rfaEventAwardAuditList);

				if(rfaEventAward != null && rfaEventAward.getFileName() != null) {
					model.addAttribute("fileId", rfaEventAward.getId());
					model.addAttribute("fileName", rfaEventAward.getFileName());
				}

				ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("erpSetup", erpSetup);
				model.addAttribute("changeCriteria", Boolean.FALSE);
				model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				model.addAttribute("showAwardTab", Boolean.TRUE);
				if (event.getTemplate() != null) {
					RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(event.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
					model.addAttribute("rfxTemplate", rfxTemplate);
				}
			}
		} catch (Exception e) {
			LOG.info("error while geting award " + e.getMessage(), e);
			model.addAttribute("error", "Error fetching award details : " + e.getMessage());
		}

		return new ModelAndView("eventAwardDetailForSAP");
	}

	@RequestMapping(path = { "/concludeEventAward" }, method = RequestMethod.POST)
	public String concludeEventAward(@ModelAttribute RfaEventAward rfaEventAward, HttpSession session, Model model, RedirectAttributes redir) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			saveProductItem(rfaEventAward);
			LOG.info(rfaEventAward.getRfxEvent().getId());

			if (rfaEventAward.getRfxEvent().getId() != null) {
				RfaEventAward eventAward = eventAwardService.saveEventAward(rfaEventAward, session, SecurityLibrary.getLoggedInUser(), false, true);
				awardApprovalService.doApproval(eventAward.getRfxEvent(), session, SecurityLibrary.getLoggedInUser(), virtualizer, eventAward.getId(), Boolean.TRUE);

				return "redirect:/buyer/RFA/eventAward/" + rfaEventAward.getRfxEvent().getId() + "/" + eventAward.getBq().getId();
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", e.getMessage());
			LOG.info(e.getMessage(), e);
			return "redirect:/buyer/RFA/eventAward/" + rfaEventAward.getRfxEvent().getId();
		}
		return "redirect:/buyer/RFA/eventAward/" + rfaEventAward.getRfxEvent().getId();
	}

	@RequestMapping(path = { "/saveEventAward", "/saveEventAward/{transfer}" }, method = RequestMethod.POST)
	public String saveEventAward(@ModelAttribute RfaEventAward rfaEventAward, @PathVariable(value = "transfer", required = false) Boolean transfer, Model model, HttpSession session, RedirectAttributes redir) {
		try {
			saveProductItem(rfaEventAward);
			LOG.info(rfaEventAward.getRfxEvent().getId());
			if (rfaEventAward.getRfxEvent().getId() != null) {
				LOG.info(rfaEventAward.getRfxEvent().getId());
				RfaEventAward eventAward = eventAwardService.saveEventAward(rfaEventAward, session, SecurityLibrary.getLoggedInUser(), false, false);
				if (eventAward != null) {
					try {
						RfaEventAudit audit = new RfaEventAudit();
						audit.setActionDate(new Date());
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setDescription("Award for Event ' " + eventAward.getRfxEvent().getEventName() + "'  is saved");
						audit.setEvent(eventAward.getRfxEvent());
						audit.setAction(AuditActionType.Award);
						try {
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.AWARD, "Award saved for event \"" + eventAward.getRfxEvent().getEventId() + "\"", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
							buyerAuditTrailDao.save(buyerAuditTrail);
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}

				return "redirect:/buyer/RFA/eventAward/" + rfaEventAward.getRfxEvent().getId() + "/" + eventAward.getBq().getId();
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", e.getMessage());
			LOG.info(e.getMessage(), e);
			return "redirect:/buyer/RFA/eventAward/" + rfaEventAward.getRfxEvent().getId();
		}
		return "redirect:/buyer/RFA/eventAward/" + rfaEventAward.getRfxEvent().getId();
	}

	@RequestMapping(path = "/criteria/{eventType}", method = RequestMethod.POST)
	public ModelAndView criteria(@PathVariable("eventType") RfxTypes eventType, @RequestParam("eventId") String eventId, @RequestParam("bqId") String bqId, @RequestParam("awardCriteria") AwardCriteria criteria, Model model) {
		LOG.info(eventId);
		LOG.info(bqId);
		LOG.info(eventType);
		LOG.info(criteria);
		try {

			if (eventId != null) {
				RfaEvent event = rfaEventService.getRfaEventById(eventId);
				model.addAttribute("event", event);
				model.addAttribute("eventType", eventType);
				if (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID || event.getAuctionType() == AuctionType.FORWARD_DUTCH) {
					AwardCriteria[] list = new AwardCriteria[3];
					list[0] = AwardCriteria.HIGHEST_TOTAL_PRICE;
					list[1] = AwardCriteria.HIGHEST_ITEMIZED_PRICE;
					list[2] = AwardCriteria.MANUAL;
					model.addAttribute("awardCriteria", list);
				} else {
					AwardCriteria[] list = new AwardCriteria[3];
					list[0] = AwardCriteria.LOWEST_TOTAL_PRICE;
					list[1] = AwardCriteria.LOWEST_ITEMIZED_PRICE;
					list[2] = AwardCriteria.MANUAL;
					model.addAttribute("awardCriteria", list);
				}
				List<Supplier> suppliers = rfaEventSupplierService.getEventQualifiedSuppliersForEvaluation(event.getId());
				List<RfaEventBq> rfabq = rfaBqService.getAllBqListByEventId(eventId);
				List<RfaSupplierBq> suppBqArr = new ArrayList<RfaSupplierBq>();

				if (rfabq != null) {
					for (RfaEventBq bq : rfabq) {
						LOG.info(bq.getId());
						RfaSupplierBq rfaSupplierBq = rfaEventService.getSupplierBQOfLeastTotalPrice(eventId, bq.getId(), SecurityLibrary.getLoggedInUserTenantId());
						suppBqArr.add(rfaSupplierBq);
					}
				}

				RfaSupplierBq rfaSupplierBq = new RfaSupplierBq();

				// RfaEventAward rfaEventAward = eventAwardService.rfaEventAwardByEventIdandBqId(eventId, bqId);
				RfaEventAward rfaEventAward = eventAwardService.rfaEventAwardDetailsByEventIdandBqId(eventId, bqId);
				if (AwardCriteria.LOWEST_TOTAL_PRICE == criteria) {
					LOG.info(eventId + "   " + bqId);
					rfaSupplierBq = rfaEventService.getSupplierBQOfLeastTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
				} else if (AwardCriteria.LOWEST_ITEMIZED_PRICE == criteria) {
					LOG.info("______________________________________________________");
					rfaSupplierBq = rfaEventService.getSupplierBQOfLowestItemisedPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
				} else if (AwardCriteria.HIGHEST_TOTAL_PRICE == criteria) {
					LOG.info(eventId + "   " + bqId);
					rfaSupplierBq = rfaEventService.getSupplierBQOfHighestTotalPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
				} else if (AwardCriteria.HIGHEST_ITEMIZED_PRICE == criteria) {
					LOG.info("______________________________________________________");
					rfaSupplierBq = rfaEventService.getSupplierBQOfHighestItemisedPrice(eventId, bqId, SecurityLibrary.getLoggedInUserTenantId());
				} else if (AwardCriteria.MANUAL == criteria) {
					if (CollectionUtil.isNotEmpty(suppliers)) {
						rfaSupplierBq = rfaEventService.getSupplierBQwithSupplierId(eventId, bqId, suppliers.get(0).getId(), SecurityLibrary.getLoggedInUserTenantId(), null);
						if (rfaEventAward != null && rfaEventAward.getAwardCriteria() != criteria) {
							if (CollectionUtil.isNotEmpty(rfaEventAward.getRfxAwardDetails())) {
								for (RfaEventAwardDetails detail : rfaEventAward.getRfxAwardDetails()) {
									detail.setSupplier(suppliers.get(0));
								}
							}
						}
					} else {
						LOG.info("Qualified suppliers is empty");
					}
				}

				if (rfaEventAward == null) {
					LOG.info("Event Award Null");
					RfaEventAward eventAward = new RfaEventAward();
					eventAward.setAwardCriteria(criteria);
					eventAward.setRfxEvent(event);
					model.addAttribute("eventAward", eventAward);
					model.addAttribute("changeCriteria", Boolean.TRUE);
				} else {
					LOG.info("Event Award not Null..." + rfaEventAward.getId());
					model.addAttribute("changeCriteria", rfaEventAward.getAwardCriteria() != criteria);
					rfaEventAward.setAwardCriteria(criteria);
					rfaEventAward.setRfxEvent(event);
					model.addAttribute("eventAward", rfaEventAward);
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
				model.addAttribute("supplierBq", rfaSupplierBq);
				model.addAttribute("supplierBqs", suppBqArr);
				model.addAttribute("suppBqId", bqId);
				model.addAttribute("showAwardTab", Boolean.TRUE);
				model.addAttribute("awardAuditList", eventAwardAuditService.findAllAwardAuditForTenantIdAndRfaEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId));
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
	public @ResponseBody ResponseEntity<RfaSupplierBqItem> criteria(@PathVariable("eventType") RfxTypes eventType, @RequestParam("eventId") String eventId, @RequestParam("bqItemId") String bqItemId, @RequestParam("supplierId") String supplierId) {

		HttpHeaders headers = new HttpHeaders();
		RfaSupplierBqItem bqItem = null;
		try {

			bqItem = eventAwardService.getBqItemByBqItemId(bqItemId, supplierId, SecurityLibrary.getLoggedInUserTenantId());
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			headers.add("error", "Cannot fetch supplier Item details : " + e.getMessage());
			return new ResponseEntity<RfaSupplierBqItem>(bqItem, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<RfaSupplierBqItem>(bqItem, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/transferAward/{eventId}/{bqId}/{rfaEventAwardId}", method = RequestMethod.POST)
	public ModelAndView transferAward(@PathVariable("eventId") String eventId, @PathVariable("bqId") String bqId, @PathVariable("rfaEventAwardId") String rfaEventAwardId, RedirectAttributes redir, HttpSession session) {
		LOG.info("eventId :" + eventId);
		try {
			if (eventId != null) {
				eventAwardService.transferRfaAward(eventId, SecurityLibrary.getLoggedInUserTenantId(), rfaEventAwardId, session, SecurityLibrary.getLoggedInUser(), true, RfxTypes.RFA);
				// RfaEvent event = rfaEventService.getRfaEventById(eventId);
				// if (event != null && event.getAwardDate() == null) {
				// event.setAwardDate(new Date());
				// rfaEventService.updateRfaEvent(event);
				// }
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.successfully.transfered", new Object[] {}, Global.LOCALE));
			} else {
				LOG.info("Event ID is Null");
			}
		} catch (Exception e) {
			LOG.info("Error while transfer Event Award :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.transfer.award", new Object[] { e.getMessage() }, Global.LOCALE));
			try {

				RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
				if (event != null) {
					sendErrorNotificationWhileCreating(SecurityLibrary.getLoggedInUser(), event.getReferanceNumber(), e.getMessage());
				}
			} catch (Exception err) {
				LOG.info("Error while sending email for transferAward", err.getMessage(), err);
			}
		}
		return new ModelAndView("redirect:/buyer/RFA/eventAward/" + eventId + "/" + bqId);
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
			LOG.info("*****itemName*******        " + itemId);
			String supplierId = itemandSupplier[1];
			LOG.info("******Supplier Id************    " + supplierId);
			RfaSupplierBqItem supplierBqItem = rfaSupplierBqItemService.getBqItemByRfaBqItemId(itemId, supplierId);
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
	public String transferEventAwardToPr(@ModelAttribute RfaEventAward rfaEventAward, @PathVariable String templateId, @PathVariable("eventId") String eventId, @PathVariable String userId, Model model) {
		LOG.info("Template Id:" + templateId);
		LOG.info("Pr Creater Id" + userId);
		try {
			saveProductItem(rfaEventAward);
			// no need to do this yogesh
			/*
			 * for (RfaEventAwardDetails rfxAward : rfaEventAward.getRfxAwardDetails()) { if
			 * (rfxAward.getBqItem().getOrder()!=0 && !(StringUtils.checkString(rfxAward.getProductCategory()).length()
			 * > 0)) { model.addAttribute("error", "Error generating PR from award : Select category for all Item.");
			 * return "redirect:/buyer/RFA/eventAward/" + eventId; } }
			 */
			String msg = rfaEventService.createPrFromAward(rfaEventAward, templateId, userId, SecurityLibrary.getLoggedInUser());
			rfaEventService.updatePrPushDate(eventId);

			RfaEventAudit audit = new RfaEventAudit();
			RfaEvent event = new RfaEvent();
			event.setId(eventId);
			audit.setEvent(event);
			audit.setAction(AuditActionType.Create);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			String prId = msg.substring(msg.indexOf(":") + 1);

			audit.setDescription("Event is pushed to PR : " + (StringUtils.checkString(prId).length() > 0 ? prId : ""));
			eventAuditService.save(audit);

			RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event is pushed to PR : " + (StringUtils.checkString(prId).length() > 0 ? prId : "") + " for event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
			buyerAuditTrailDao.save(buyerAuditTrail);
			Pr pr =prService.findPrbyprId(prId.trim());
			model.addAttribute("success", messageSource.getMessage("rfa.success.pr.created", new Object[] { msg != null ? msg : "" }, Global.LOCALE));
			return "redirect:/buyer/createPrDetails/" + pr.getId()+"?eventName="+rfaEvent.getEventName()+"&eventDescription="+rfaEvent.getEventDescription()+"&budgetAmount="+rfaEvent.getBudgetAmount();
		} catch (Exception e) {
			LOG.error("Error generating Auto PRs during Event Award : " + e.getMessage(), e);
			// model.addAttribute("error", "Error generating PR : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("rfa.award.error.generating", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return "redirect:/buyer/RFA/eventAward/" + eventId;
	}

	@RequestMapping(path = "/createContract/{eventId}", method = RequestMethod.POST)
	public String createContract(@ModelAttribute RfaEventAward rfaEventAward, @PathVariable("eventId") String eventId, @RequestParam("contractStartDateHid") String contractStartDate, @RequestParam("contractEndDateHid") String contractEndDate, @RequestParam("groupCodeHid") String groupCodeHid, @RequestParam("referenceNumberHid") String referenceNumberHid, @RequestParam("contractCreatorHid") String contractCreatorHid, Model model, HttpSession session) {
		LOG.info("StartDate " + contractStartDate + " End Date " + contractEndDate + "Event Id " + eventId);

		try {
			saveProductItem(rfaEventAward);

			String msg = rfaEventService.createContractFromAward(rfaEventAward, eventId, contractStartDate, contractEndDate, groupCodeHid, referenceNumberHid, SecurityLibrary.getLoggedInUser(), session, contractCreatorHid);
			// rfaEventService.updatePrPushDate(eventId);
			RfaEventAudit audit = new RfaEventAudit();
			RfaEvent event = rfaEventService.getPlainEventById(eventId);
			audit.setEvent(event);
			audit.setAction(AuditActionType.Create);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			String contractId = msg.substring(msg.indexOf(":") + 1);
			audit.setDescription("Event is pushed to Contract : " + (StringUtils.checkString(contractId).length() > 0 ? contractId : ""));
			eventAuditService.save(audit);

			try {
				BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, " Event is pushed to Contract : " + (StringUtils.checkString(contractId).length() > 0 ? contractId : "") + " for event " + event.getEventId(), SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
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

		return "redirect:/buyer/RFA/eventAward/" + eventId;
	}

	private void saveProductItem(RfaEventAward rfaEventAward) throws ApplicationException {
		if (CollectionUtil.isNotEmpty(rfaEventAward.getRfxAwardDetails())) {
			for (RfaEventAwardDetails rfxAward : rfaEventAward.getRfxAwardDetails()) {
				if (StringUtils.checkString(rfxAward.getSelectItem()).length() > 0) {
					String value = rfxAward.getSelectItem();
					String[] itemNameAndSupplierId = value.split("-");
					String itemId = itemNameAndSupplierId[0];
					String supplierId = itemNameAndSupplierId[1];

					RfaSupplierBqItem supplierBqItem = rfaSupplierBqItemService.getBqItemByRfaBqItemId(itemId, supplierId);

					String itemName = supplierBqItem.getItemName();
					if (StringUtils.checkString(itemName).length() > 0 && StringUtils.checkString(supplierId).length() > 0) {
						ProductItem productItem = productCategoryMaintenanceService.checkProductItemExistOrNot(itemName, supplierId, SecurityLibrary.getLoggedInUserTenantId());

						if (productItem != null) {

							BigDecimal ap = rfxAward.getAwardedPrice();
							productItem.setUnitPrice(ap.divide(supplierBqItem.getQuantity(), 6, BigDecimal.ROUND_DOWN));

							// supplierBqItem.getQuantity();

							// productItem.setUnitPrice(rfxAward.getAwardedPrice());
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
	public String pushToEvaluation(@ModelAttribute RfaEventAward rfaEventAward, @PathVariable String templateId, @PathVariable("eventId") String eventId, @PathVariable String userId, Model model) {
		LOG.info("Creating Supplier Perfromance Evaluation Form......");
		try {
			List<String> formIds = rfaEventService.createSpFormFromAward(rfaEventAward, templateId, eventId, userId, SecurityLibrary.getLoggedInUser());
			String frmIds = String.join(",", formIds);

			RfaEventAudit audit = new RfaEventAudit();
			RfaEvent event = rfaEventService.getPlainEventById(eventId);
			audit.setEvent(event);
			audit.setAction(AuditActionType.Create);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setDescription("Event is pushed to Supplier Performance Evaluation : " + (StringUtils.checkString(frmIds).length() > 0 ? frmIds : ""));
			eventAuditService.save(audit);

			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event is pushed to Supplier Performance Evaluation : \"" + (StringUtils.checkString(frmIds).length() > 0 ? frmIds : "") + "\" for event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
			buyerAuditTrailDao.save(buyerAuditTrail);

			BuyerAuditTrail buyerAuditTrail2 = new BuyerAuditTrail(AuditTypes.CREATE, "Supplier Performance Evaluation Form \"" + (StringUtils.checkString(frmIds).length() > 0 ? frmIds : "") + "\" Created.", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PerformanceEvaluation);
			buyerAuditTrailDao.save(buyerAuditTrail2);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");

			model.addAttribute("success", messageSource.getMessage("rfa.success.sp.created", new Object[] { frmIds }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error creating Supplier Performance Form : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("rfa.error.sp.created", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return "redirect:/buyer/RFA/eventAward/" + eventId;

	}

	@RequestMapping(path = "/updateEventAwardApproval", method = RequestMethod.POST)
	public String updateEventAwardApproval(@ModelAttribute RfaEventAward eventAward, @RequestParam("eventId") String eventId, RedirectAttributes redir, HttpSession session) {
		LOG.info(" Event Id with request param " + eventId);
		try {
			Boolean isUpdate = eventAwardService.updateEventAwardApproval(eventAward, SecurityLibrary.getLoggedInUser(), eventId);
			RfaEvent event = rfaEventService.getPlainEventById(eventId);
			if (AwardStatus.PENDING == event.getAwardStatus() && Boolean.TRUE == isUpdate) {
				JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
				awardApprovalService.doApproval(event, session, SecurityLibrary.getLoggedInUser(), virtualizer, eventAward.getId(), Boolean.FALSE);
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.approval.updated", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Updating Approval :" + e.getMessage(), e);

			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.approval", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/RFA/eventAward/" + eventId;
	}

	@RequestMapping(value = "/transferRfxAwardToErp/{eventId}/{rfaEventAwardId}", method = RequestMethod.POST)
	public ResponseEntity<SapResponseToTransferAward> transferAwardToERP(@PathVariable(name = "eventId") String eventId,
																		 @PathVariable(name = "rfaEventAwardId") String rfaEventAwardId,
																		 HttpSession session) throws Exception {
		ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
		SapResponseToTransferAward sapResponseToTransferAward = new SapResponseToTransferAward();
		HttpHeaders headers = new HttpHeaders();

		try {
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()) {
				sapResponseToTransferAward = erpIntegrationService.transferRfxAwardToErp(RfxTypes.RFP, eventId, erpSetup, SecurityLibrary.getLoggedInUser(),
						rfaEventAwardId, session);
			}
		}
		catch (ApplicationException applicationException) {
			headers.add("error", messageSource.getMessage("event.transfer.awrd.error", new Object[] {
					applicationException.getMessage() }, Global.LOCALE));
			RfaEvent rfaEvent = rfaEventService.getRfaEventById(eventId);
			rfaEvent.setStatus(EventStatus.COMPLETE);
			rfaEvent.setAwarded(false);
			rfaEvent.setAwardStatus(AwardStatus.DRAFT);
			rfaEventDao.saveOrUpdate(rfaEvent);
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

	@RequestMapping(value = "/deleteAwardAttachFile/{eventAwardId}", method = RequestMethod.GET)
	public ResponseEntity<?> deleteFile(@PathVariable String eventAwardId) {
		try {
			LOG.info("Event Award  Id  :: :: " + eventAwardId + "::::::");
			// We will use this to delete from the event Award previously it was for the event award audit
			eventAwardAuditService.deleteDocumentsByRfxAuditId(eventAwardId, RfxTypes.RFA);
			return new ResponseEntity<>("Document is Deleted", HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while deleting document: " + e.getMessage(), e);
			return new ResponseEntity<>("Issue in deleting document", HttpStatus.OK);
		}
	}

}
