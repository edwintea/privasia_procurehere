package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.ProductContractDao;
import com.privasia.procurehere.core.dao.ProductContractNotifyUsersDao;
import com.privasia.procurehere.core.entity.AgreementType;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.ContractApproval;
import com.privasia.procurehere.core.entity.ContractApprovalUser;
import com.privasia.procurehere.core.entity.ContractAudit;
import com.privasia.procurehere.core.entity.ContractDocument;
import com.privasia.procurehere.core.entity.ContractLoaAndAgreement;
import com.privasia.procurehere.core.entity.ContractTeamMember;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductCategoryPojo;
import com.privasia.procurehere.core.entity.ProductContract;
import com.privasia.procurehere.core.entity.ProductContractItems;
import com.privasia.procurehere.core.entity.ProductContractNotifyUsers;
import com.privasia.procurehere.core.entity.ProductContractReminder;
import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.BusinessUnitPojo;
import com.privasia.procurehere.core.pojo.ContractPojo;
import com.privasia.procurehere.core.pojo.ContractProductItemPojo;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.ProductContractItemsPojo;
import com.privasia.procurehere.core.pojo.ProductContractPojo;
import com.privasia.procurehere.core.pojo.ProductItemPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.AgreementTypeService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.ContractAuditService;
import com.privasia.procurehere.service.ContractDocumentService;
import com.privasia.procurehere.service.ContractLoaAndAgreementService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.GroupCodeService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.ProcurementCategoriesService;
import com.privasia.procurehere.service.ProductCategoryMaintenanceService;
import com.privasia.procurehere.service.ProductContractItemsService;
import com.privasia.procurehere.service.ProductContractReminderService;
import com.privasia.procurehere.service.ProductContractService;
import com.privasia.procurehere.service.ProductListMaintenanceService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.impl.SnapShotAuditService;
import com.privasia.procurehere.web.editors.AgreementTypeEditor;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import com.privasia.procurehere.web.editors.ContractApprovalEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.FavouriteSupplierEditor;
import com.privasia.procurehere.web.editors.GroupCodeEditor;
import com.privasia.procurehere.web.editors.ProcurementCategoriesEditor;
import com.privasia.procurehere.web.editors.ProductCategoryMaintenanceEditor;
import com.privasia.procurehere.web.editors.ProductContractNotifyUsersEditor;
import com.privasia.procurehere.web.editors.UomEditor;

import freemarker.template.Configuration;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

/**
 * @author aishwarya
 */

@Controller
@RequestMapping(path = "/buyer")
public class ProductContractController {

	private static final Logger LOG = LogManager.getLogger(Global.CONTRACT_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	UomService uomService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	UomEditor uomEditor;

	@Autowired
	ProductCategoryMaintenanceEditor pcmEditor;

	@Autowired
	FavouriteSupplierEditor fsEditor;

	@Autowired
	BusinessUnitEditor buEditor;

	@Autowired
	ProductContractItemsService productContractItemsService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Resource
	MessageSource messageSource;

	@Autowired
	PrService prService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	ProductListMaintenanceService productListMaintenanceService;

	@Autowired
	ProductContractReminderService productContractReminderService;

	@Autowired
	UserService userService;

	@Autowired
	ProductContractNotifyUsersEditor productContractNotifyUsersEditor;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	ProductContractDao productContractDao;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	ContractDocumentService contractDocumentService;

	@Autowired
	GroupCodeService groupCodeService;

	@Autowired
	GroupCodeEditor groupCodeEditor;

	@Autowired
	ProcurementCategoriesService procurementCategoriesService;

	@Autowired
	AgreementTypeService agreementTypeService;

	@Autowired
	ProcurementCategoriesEditor pcEditor;

	@Autowired
	AgreementTypeEditor agreementTypeEditor;

	@Autowired
	ProductContractNotifyUsersDao productContractNotifyUsersDao;

	@Autowired
	ProductContractService productContractService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	ContractLoaAndAgreementService contractLoaAndAgreementService;

	@Autowired
	ContractApprovalEditor contractApprovalEditor;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ContractAuditService contractAuditService;

	@Autowired
	SnapShotAuditService snapShotAuditService;

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(Uom.class, uomEditor);
		binder.registerCustomEditor(ProductCategory.class, pcmEditor);
		binder.registerCustomEditor(FavouriteSupplier.class, fsEditor);
		binder.registerCustomEditor(BusinessUnit.class, buEditor);
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(GroupCode.class, groupCodeEditor);
		binder.registerCustomEditor(ProductContractNotifyUsers.class, productContractNotifyUsersEditor);
		binder.registerCustomEditor(AgreementType.class, agreementTypeEditor);
		binder.registerCustomEditor(ProcurementCategories.class, pcEditor);
		binder.registerCustomEditor(ContractApprovalUser.class, contractApprovalEditor);

		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(byte.class, new ByteArrayMultipartFileEditor());
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);

		// TimeZone timeZone = TimeZone.getDefault();
		// String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		// if (strTimeZone != null) {
		// timeZone = TimeZone.getTimeZone(strTimeZone);
		// }
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
		// timeFormat.setTimeZone(timeZone);

		binder.registerCustomEditor(Date.class, "contractStartDate", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "contractEndDate", new CustomDateEditor(timeFormat, true));

	}

	@ModelAttribute("statusList")
	public List<ContractStatus> getStatusList() {
		return Arrays.asList(ContractStatus.values());
	}

	@ModelAttribute("itemTypeList")
	public List<ProductItemType> getProductItemType() {
		return Arrays.asList(ProductItemType.values());
	}

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	@RequestMapping(path = "/productContractList", method = RequestMethod.GET)
	public String productContractList(Model model) throws JsonProcessingException {
		LOG.info(">>>>>>>>>>>>>>>> DASHBOARD COUNTER productContractList uc 1.1    ");
		long draftContractCount = productContractService.findDraftContractByTenantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId());
		long pendingContractCount = productContractService.findPendingContractByTenantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId());
		long newContractCount = productContractService.findNewUpcomingContractByTeanantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId());
		long oneMonthExpiredCount = productContractService.findContractBefore30DayExpireByTeanantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId());
		long oneToThreeMonthExpiredCount = productContractService.findContractBefore90DayExpireByTeanantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId());
		long threeToSixMonthExpiredCount = productContractService.findContractBefore180DayExpireByTeanantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId());
		long greaterThanSixMonthExpiredCount = productContractService.findContractGreaterThanSixMonthExpireByTeanantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId());
		long expiredContractCount = productContractService.findContractByStatusForTeanant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), ContractStatus.EXPIRED);
		long terminatedContractCount = productContractService.findContractByStatusForTeanant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), ContractStatus.TERMINATED);
		long activeContractCount = productContractService.findContractByStatusForTeanant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), ContractStatus.ACTIVE);
		long suspendedContractCount = productContractService.findContractByStatusForTeanant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), ContractStatus.SUSPENDED);
		model.addAttribute("draftContractCount", draftContractCount);
		model.addAttribute("pendingContractCount", pendingContractCount);
		model.addAttribute("newContractCount", newContractCount);
		model.addAttribute("oneMonthExpiredCount", oneMonthExpiredCount);
		model.addAttribute("oneToThreeMonthExpiredCount", oneToThreeMonthExpiredCount);
		model.addAttribute("threeToSixMonthExpiredCount", threeToSixMonthExpiredCount);
		model.addAttribute("greaterThanSixMonthExpiredCount", greaterThanSixMonthExpiredCount);
		model.addAttribute("expiredContractCount", expiredContractCount);
		model.addAttribute("terminatedContractCount", terminatedContractCount);
		model.addAttribute("activeContractCount", activeContractCount);
		model.addAttribute("suspendedContractCount", suspendedContractCount);
		// get ID settings for Contract
		model.addAttribute("idSettingsBasedOnBusinessUnit", eventIdSettingsService.isBusinessSettingEnable(SecurityLibrary.getLoggedInUserTenantId(), "CTR"));
		List<BusinessUnitPojo> businessUnitData = businessUnitService.fetchBusinessUnitByTenantId(SecurityLibrary.getLoggedInUserTenantId(), null);
		model.addAttribute("businessUnit", businessUnitData);

		return "productContractList";
	}

	/*
	 * productListMaintenanceData
	 */
	@RequestMapping(path = "/productContractListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ContractPojo>> productContractListData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info(" SCENARIO 4 Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.debug(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			Date startDate = null;
			Date endDate = null;
			List<String> bizUnitIds =null;
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			LOG.info("RETRIEVE UNIT ID??? >>>>>>>>>>>>>>>   "+bizUnitIds);
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			TableData<ContractPojo> data = new TableData<ContractPojo>(productContractService.findProductContractListForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate));
			//TableData<ContractPojo> data = new TableData<ContractPojo>(productContractService.findProductContractListForBizUnit(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate,bizUnitIds));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredProductListForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractService.findTotalProductListForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Product Contract list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/productContractListItem", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractItemsPojo>> productContractListItem(@RequestParam String id, TableDataInput input) {
		try {
			LOG.info(" LIST ITEM Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort() + "id :  " + id);
			LOG.debug(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<ProductContractItemsPojo> data = new TableData<ProductContractItemsPojo>(productContractItemsService.findProductContractItemListForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, id));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractItemsService.findTotalFilteredProductItemListForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, id);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractItemsService.findTotalProductItemListForTenant(SecurityLibrary.getLoggedInUserTenantId(), id);
			data.setRecordsTotal(totalCount);

			return new ResponseEntity<TableData<ProductContractItemsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Product Contract Item list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productList Item    list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractItemsPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/createBlankContract", method = RequestMethod.GET)
	public ModelAndView createBlankContract(@RequestParam(required = false, name = "businessUnitId") String businessUnitId, Model model, HttpSession session, RedirectAttributes redir) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			LOG.info(" yes I am here with BU ID : " + businessUnitId);
			ProductContract contract = new ProductContract();

			// If ID settings i based on business unit then pass the BU else pass null
			BusinessUnit bu = new BusinessUnit();
			bu.setId(StringUtils.checkString(businessUnitId));
			String id = eventIdSettingsService.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "CTR", StringUtils.checkString(businessUnitId).length() > 0 ? bu : null);
			contract.setContractId(id);

			if (StringUtils.checkString(businessUnitId).length() > 0) {
				contract.setBusinessUnit(bu);
				contract.setIdBasedOnBusinessUnit(Boolean.TRUE);
			}

			contract.setStatus(ContractStatus.DRAFT);
			contract.setContractCreator(SecurityLibrary.getLoggedInUser());
			contract.setCreatedBy(SecurityLibrary.getLoggedInUser());
			contract.setCreatedDate(new Date());
			contract.setDecimal("2");
			contract.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			List<ProductContractNotifyUsers> notifyUserList = new ArrayList<ProductContractNotifyUsers>();
			ProductContractNotifyUsers users = new ProductContractNotifyUsers();
			users.setUser(SecurityLibrary.getLoggedInUser());
			users.setProductContract(contract);
			notifyUserList.add(users);
			contract.setNotifyUsers(notifyUserList);

			Calendar endDate = Calendar.getInstance();
			endDate.add(Calendar.DATE, 181);
			contract.setContractEndDate(endDate.getTime());
			contract.setContractStartDate(new Date());
			List<ProductContractReminder> productRemindersList = new ArrayList<ProductContractReminder>();
			createDefaultReminderList(productRemindersList, new Date(), endDate.getTime());
			for (ProductContractReminder r : productRemindersList) {
				r.setProductContract(contract);
			}
			contract.setContractReminders(productRemindersList);
			contract = productContractService.createContract(contract, session, SecurityLibrary.getLoggedInUser(), virtualizer);

			return new ModelAndView("redirect:productContractListEdit?id=" + contract.getId());
		} catch (Exception e) {
			LOG.error("Error to redirect page " + e.getMessage(), e);
			return new ModelAndView("productContractList");
		}
	}

	@GetMapping("/productContract")
	public ModelAndView productContract(@ModelAttribute ProductItem productContract, Model model) {
		LOG.info("logged in user ::  " + SecurityLibrary.getLoggedInUser().getBuyer().getId());
		BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUser().getBuyer().getId());
		String decimal = buyerSettings.getDecimal();
		int z = Integer.parseInt(decimal);
		StringBuffer decimals = new StringBuffer(0);
		for (int i = 0; i < z; i++) {
			decimals.append(0);
		}
		model.addAttribute("decimals", decimals);
		model.addAttribute("decimal", decimal);
		model.addAttribute("btnValue2", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		List<BusinessUnitPojo> businessUnitData = businessUnitService.fetchBusinessUnitByTenantId(SecurityLibrary.getLoggedInUserTenantId(), null);
		model.addAttribute("businessUnit", businessUnitData);
		List<SupplierPojo> supplierListData = favoriteSupplierService.searchFavouriteSupplier(SecurityLibrary.getLoggedInUserTenantId(), null, null);
		model.addAttribute("favSupp", supplierListData);
		List<GroupCode> groupCodeList = groupCodeService.fetchAllGroupCodesForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		model.addAttribute("groupCodeList", groupCodeList);
		model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("agreementTypeList", agreementTypeService.getAllActiveAgreementType(SecurityLibrary.getLoggedInUserTenantId()));
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		UserPojo userpojo = new UserPojo(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUser().getLoginId(), SecurityLibrary.getLoggedInUser().getName(), SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser().isDeleted(), SecurityLibrary.getLoggedInUser().getCommunicationEmail(), SecurityLibrary.getLoggedInUser().getEmailNotifications());
		if (!userList.contains(userpojo)) {
			userList.add(0, userpojo);
		}
		model.addAttribute("notifyUserList", userList);
		List<User> userTeamMember = userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		LOG.info("userTeamMemberList.size() :" + userTeamMember.size());
		model.addAttribute("userTeamMemberList", userTeamMember);

		model.addAttribute("loggedInUserId", SecurityLibrary.getLoggedInUser().getId());
		model.addAttribute("currencyList", currencyService.getAllCurrency());

		ProductContract pc = new ProductContract();
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.DATE, 181);
		pc.setContractEndDate(endDate.getTime());
		pc.setContractStartDate(new Date());

		List<ProductContractReminder> productRemindersList = new ArrayList<ProductContractReminder>();
		createDefaultReminderList(productRemindersList, new Date(), endDate.getTime());
		model.addAttribute("reminderList", productRemindersList);

		List<User> approvalUserList = new ArrayList<User>();
		List<UserPojo> appuserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
		if (CollectionUtil.isNotEmpty(appuserList)) {
			for (UserPojo user : appuserList) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!approvalUserList.contains(u)) {
					approvalUserList.add(u);
				}
			}
		}
		model.addAttribute("userList", approvalUserList);
		model.addAttribute("contractApprovalUserList", approvalUserList);

		return new ModelAndView("productContractUpdate", "productContract", pc);
	}

	@RequestMapping(path = "/suspendProductContract", method = RequestMethod.GET)
	public String suspendProductContract(@RequestParam String id, HttpSession session, RedirectAttributes redir) {
		LOG.info("Changing status of Contract to SUSPENDED : " + id);

		EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), id);
		if (!eventPermissions.isOwner()) {
			redir.addFlashAttribute("error", "You are not authorized to suspend this contract");
			return "redirect:/buyer/productContractList";
		}

		ProductContract contractObj = productContractDao.findProductContractById(id, SecurityLibrary.getLoggedInUserTenantId());
		// If the contract owner - only then suspend the contract
		contractObj.setOldStatus(contractObj.getStatus());
		contractObj.setStatus(ContractStatus.SUSPENDED);
		contractObj = productContractService.update(contractObj);

		// Suspend Audit
		try {
			snapShotAuditService.doContractAudit(contractObj, session, contractObj, SecurityLibrary.getLoggedInUser(), AuditActionType.Suspend, messageSource.getMessage("contract.suspend.request", new Object[] { contractObj.getContractId() }, Global.LOCALE), null);
		} catch (Exception e) {
			LOG.error("Error while recording contract Suspend Audit : " + e.getMessage(), e);
		}

		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND, messageSource.getMessage("contract.is.suspend.request", new Object[] { contractObj.getContractId() }, Global.LOCALE), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ContractList);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error while recording contract Suspend Audit : " + e.getMessage(), e);
		}
		return "redirect:/buyer/productContractListEdit?id=" + id;
	}

	@RequestMapping(path = "/productContractListEdit", method = RequestMethod.GET)
	public ModelAndView productContractListEdit(@RequestParam String id, ModelMap model, HttpSession session) {
		ProductContract productContractObj = productContractService.findProductContractById(id, SecurityLibrary.getLoggedInUserTenantId());
		LOG.info("contract Refrence number  : " + productContractObj.getContractReferenceNumber() + ", Start Date : " + productContractObj.getContractStartDate() + ", End Date : " + productContractObj.getContractEndDate());

		BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUser().getBuyer().getId());
		String decimal = buyerSettings.getDecimal();
		int z = Integer.parseInt(decimal);
		StringBuffer decimals = new StringBuffer(0);
		for (int i = 0; i < z; i++) {
			decimals.append(0);
		}
		model.addAttribute("decimals", decimals);
		model.addAttribute("decimal", decimal);

		EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), id);
		if (!eventPermissions.isOwner() && (productContractObj.getStatus() == ContractStatus.ACTIVE || productContractObj.getStatus() == ContractStatus.SUSPENDED)) {
			return new ModelAndView("redirect:contractSummary/" + productContractObj.getId());
		}

		model.addAttribute("eventPermissions", eventPermissions);

		// model.addAttribute("data", productListObj);
		List<SupplierPojo> supplierListData = favoriteSupplierService.searchFavouriteSupplier(SecurityLibrary.getLoggedInUserTenantId(), null, null);
		List<SupplierPojo> supplierList = new ArrayList<SupplierPojo>();
		if (productContractObj.getSupplier() != null) {
			supplierList.add(new SupplierPojo(productContractObj.getSupplier().getId(), productContractObj.getSupplierName()));
		}
		for (SupplierPojo supplier : supplierListData) {
			boolean isSupplierExist = false;
			if (productContractObj.getSupplier() != null && productContractObj.getSupplier().getId().equals(supplier.getId())) {
				isSupplierExist = true;
			}
			if (!isSupplierExist) {
				supplierList.add(supplier);
			}
		}

		model.addAttribute("favSupp", supplierList);

		List<BusinessUnitPojo> businessUnitData = businessUnitService.fetchBusinessUnitByTenantId(SecurityLibrary.getLoggedInUserTenantId(), null);
		List<BusinessUnitPojo> businessUnitList = new ArrayList<BusinessUnitPojo>();
		if (productContractObj.getBusinessUnit() != null) {
			businessUnitList.add(new BusinessUnitPojo(productContractObj.getBusinessUnit().getId(), productContractObj.getBusinessUnit().getDisplayName(), productContractObj.getBusinessUnit().getUnitName(), productContractObj.getBusinessUnit().getUnitCode()));
		}
		for (BusinessUnitPojo unit : businessUnitData) {
			boolean isUnitExist = false;
			if (productContractObj.getBusinessUnit() != null && productContractObj.getBusinessUnit().getId().equals(unit.getId())) {
				isUnitExist = true;
			}
			if (!isUnitExist) {
				businessUnitList.add(unit);
			}
		}
		model.addAttribute("businessUnit", businessUnitList);

		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		if (enableCorrelation != null && enableCorrelation == Boolean.TRUE) {
			List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, productContractObj.getBusinessUnit() != null ? productContractObj.getBusinessUnit().getId() :  null);
			model.addAttribute("costCenterList", costCenterList);
		} else {
			List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, null);
			model.addAttribute("costCenterList", costCenterList);
		}

		List<ProductContractReminder> productReminders = productContractReminderService.getAllContractRemindersByContractId(productContractObj.getId());
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		List<User> notifyUserList = new ArrayList<User>();
		if (CollectionUtil.isNotEmpty(productContractObj.getNotifyUsers())) {
			for (ProductContractNotifyUsers notifyUser : productContractObj.getNotifyUsers()) {
				notifyUserList.add(new User(notifyUser.getUser().getId(), notifyUser.getUser().getLoginId(), notifyUser.getUser().getName(), notifyUser.getUser().getCommunicationEmail(), notifyUser.getUser().getEmailNotifications(), notifyUser.getUser().getTenantId(), notifyUser.getUser().isDeleted()));
			}
		}
		UserPojo loggedInUserPojo = new UserPojo();
		loggedInUserPojo.setId(productContractObj.getCreatedBy().getId());
		loggedInUserPojo.setLoginId(productContractObj.getCreatedBy().getLoginId());

		for (UserPojo user : userList) {
			try {
				User userObj = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!notifyUserList.contains(userObj)) {
					notifyUserList.add(userObj);
				}
			} catch (Exception e) {
				LOG.info("Error while cloning the user List :" + e.getMessage());
			}
		}

		List<ContractDocument> contractDocument = contractDocumentService.findAllContractdocsbyContractId(id);
		model.addAttribute("contractDocument", contractDocument);
		ContractLoaAndAgreement loaAndAgrcontractDocument = productContractService.findContractLoaAndAgreementByContractId(id);
		model.addAttribute("loaAndAgrcontractDocument", loaAndAgrcontractDocument);

		for (User us : notifyUserList) {
			LOG.info("Notify User in Controller : " + us.getName());
		}

		model.addAttribute("notifyUserList", notifyUserList);
		model.addAttribute("reminderList", productReminders);
		model.addAttribute("btnValue2", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		List<ProductItemPojo> productItemList = productContractItemsService.findProductContractItems(id);
		List<ProductItemPojo> list = productListMaintenanceService.fetchAllActiveProductItemNameAndCodeForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		if (CollectionUtil.isNotEmpty(list)) {
			for (ProductItemPojo pojo : list) {
				if (!productItemList.contains(pojo)) {
					productItemList.add(pojo);
				}
			}
		}

		model.addAttribute("productItemList", productItemList);
		List<Uom> uomList = uomService.getAllActiveUomForTenant(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("uomList", uomList);
		List<GroupCode> groupCodeList = groupCodeService.fetchAllGroupCodesForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		model.addAttribute("groupCodeList", groupCodeList);
		model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("agreementTypeList", agreementTypeService.getAllActiveAgreementType(SecurityLibrary.getLoggedInUserTenantId()));

		List<ProductCategoryPojo> itemCategories = productContractItemsService.findProductCategories(id);
		List<ProductCategoryPojo> catList = productCategoryMaintenanceService.fetchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		if (CollectionUtil.isNotEmpty(itemCategories)) {
			for (ProductCategoryPojo pojo : itemCategories) {
				if (!catList.contains(pojo)) {
					catList.add(pojo);
				}
			}
		}
		model.addAttribute("productCategoryList", catList);
		model.addAttribute("currencyList", currencyService.getAllCurrency());

		Date newDate = DateUtil.formatDateToStartTime(new Date());
		Boolean contractExpire = false;
		if (productContractObj.getContractEndDate() != null) {
			if (productContractObj.getContractEndDate().before(newDate)) {
				contractExpire = true;
			}
		}

		model.addAttribute("contractExpire", contractExpire);

		// Contract users for Approval

		List<ContractApprovalUser> approvalUserList = new ArrayList<ContractApprovalUser>();

		if (CollectionUtil.isNotEmpty(productContractObj.getApprovals())) {
			for (ContractApproval approval : productContractObj.getApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (ContractApprovalUser approvalUser : approval.getApprovalUsers()) {
						User user = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(), approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(), approvalUser.getUser().getEmailNotifications(), approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());
						if (!approvalUserList.contains(new ContractApprovalUser(user))) {
							approvalUserList.add(new ContractApprovalUser(user));
						}
					}
				}
			}
		}
		for (UserPojo userPojo : userList) {
			if (userPojo.equals(loggedInUserPojo)) {
				continue;
			}
			User user = new User(userPojo.getId(), userPojo.getLoginId(), userPojo.getName(), userPojo.getCommunicationEmail(), userPojo.isEmailNotifications(), userPojo.getTenantId(), userPojo.isDeleted());
			if (!approvalUserList.contains(new ContractApprovalUser(user))) {
				approvalUserList.add(new ContractApprovalUser(user));
			}
		}

		List<UserPojo> userTeamMemberList = new ArrayList<UserPojo>();
		for (UserPojo userPojo : userList) {
			boolean found = false;
			for (ContractTeamMember tm : productContractObj.getTeamMembers()) {
				if (tm.getUser().getId().equals(userPojo.getId())) {
					found = true;
					break;
				}
			}
			if (!found) {
				userTeamMemberList.add(userPojo);
			}
		}

		model.addAttribute("userTeamMemberList", userTeamMemberList);
		model.addAttribute("userList", approvalUserList);
		model.addAttribute("userList1", userList);

		return new ModelAndView("productContractUpdate", "productContract", productContractObj);
	}

	@GetMapping("/editContractItem/{itemId}/{contractId}")
	public @ResponseBody ResponseEntity<ProductContractItemsPojo> editContractItem(@PathVariable("itemId") String itemId, @PathVariable("contractId") String contractId) {
		LOG.info("itemId  :" + itemId + " contractId :" + contractId);

		HttpHeaders headers = new HttpHeaders();
		ProductContractItemsPojo pojo = new ProductContractItemsPojo();
		try {
			ProductContract productListObj = productContractService.findProductContractById(contractId, SecurityLibrary.getLoggedInUserTenantId());
			ProductContractItems item = productContractItemsService.findProductContractItemById(itemId);
			pojo.setContractItemNumber(item.getContractItemNumber());
			if (item.getProductItem() != null) {
				pojo.setProductItem(item.getProductItem().getId());
			}
			pojo.setItemName(item.getItemName());
			pojo.setItemCode(item.getItemCode());
			pojo.setQuantity(item.getQuantity());
			pojo.setBalanceQuantity(item.getBalanceQuantity());
			pojo.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice().setScale(Integer.parseInt(productListObj.getDecimal()), RoundingMode.HALF_UP) : null);
			pojo.setPricePerUnit(item.getPricePerUnit() != null ? item.getPricePerUnit().setScale(Integer.parseInt(productListObj.getDecimal()), RoundingMode.HALF_UP) : null);
			pojo.setStorageLoc(item.getStorageLocation());
			pojo.setUom(item.getUom() != null ? item.getUom().getId() : null);
			pojo.setBusinessUnit(item.getBusinessUnit() != null ? item.getBusinessUnit().getId() : null);
			pojo.setBusinessUnitCode(item.getBusinessUnit() != null ? item.getBusinessUnit().getUnitCode() : null);
			pojo.setBusinessUnitName(item.getBusinessUnit() != null ? item.getBusinessUnit().getUnitName() : null);
			pojo.setCostCenter(item.getCostCenter() != null ? item.getCostCenter().getId() : null);
			pojo.setCostCenterName(item.getCostCenter() != null ? item.getCostCenter().getCostCenter() : null);
			pojo.setCostCenterDescription(item.getCostCenter() != null ? item.getCostCenter().getDescription() : null);
			pojo.setTax(item.getTax());
			pojo.setProductCategory(item.getProductCategory() != null ? item.getProductCategory().getId() : null);
			pojo.setProductCategoryCode(item.getProductCategory() != null ? item.getProductCategory().getProductCode() : null);
			pojo.setProductCategoryName(item.getProductCategory() != null ? item.getProductCategory().getProductName() : null);
			pojo.setBrand(item.getBrand());
			pojo.setItemType(item.getProductItemType());
			pojo.setFreeTextItemEntered(item.getFreeTextItemEntered());
			pojo.setErpTransferred(item.getErpTransferred());

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			headers.add("error", "Error while getting contract item details : " + e.getMessage());
			return new ResponseEntity<ProductContractItemsPojo>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<ProductContractItemsPojo>(pojo, headers, HttpStatus.OK);
	}

	private List<ProductContractReminder> createDefaultReminderList(List<ProductContractReminder> productRemindersList, Date contractStartDate, Date contractEndDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(contractEndDate);
		cal.add(Calendar.DATE, -180);
		Date reminderDate = cal.getTime();
		if (reminderDate.after(contractStartDate)) {
			LOG.info("Six month default reminder");
			ProductContractReminder newReminder = new ProductContractReminder();
			newReminder.setReminderDate(reminderDate);
			newReminder.setInterval(180);
			productRemindersList.add(newReminder);
		}
		cal = Calendar.getInstance();
		cal.setTime(contractEndDate);
		cal.add(Calendar.DATE, -90);
		reminderDate = cal.getTime();
		if (reminderDate.after(contractStartDate)) {
			LOG.info("three month default reminder");
			ProductContractReminder newReminder = new ProductContractReminder();
			newReminder.setInterval(90);
			newReminder.setReminderDate(reminderDate);
			productRemindersList.add(newReminder);
		}
		cal = Calendar.getInstance();
		cal.setTime(contractEndDate);
		cal.add(Calendar.DATE, -30);
		reminderDate = cal.getTime();
		if (reminderDate.after(contractStartDate)) {
			LOG.info("one month default reminder");
			ProductContractReminder newReminder = new ProductContractReminder();
			newReminder.setReminderDate(reminderDate);
			newReminder.setInterval(30);
			productRemindersList.add(newReminder);
		}
		return productRemindersList;
	}

	@PostMapping("/saveProductContract")
	public String saveProductContract(@ModelAttribute("productContract") ProductContract productContract, BindingResult result, Model model, HttpSession session, RedirectAttributes redir, @RequestParam(required = false, name = "remindMeDays") String[] remindMeDays, @RequestParam(name = "docs", required = false) MultipartFile[] file, @RequestParam(name = "loaDocs", required = false) MultipartFile loaFile, @RequestParam(name = "agrDocs", required = false) MultipartFile agrFile, @RequestParam(name = "loaDate", required = false) String loaDate, @RequestParam(name = "agrDate", required = false) String agrDate, @RequestParam(name = "docDesc", required = false) String[] docDesc, @RequestParam(name = "loaDesc", required = false) String loaDesc, @RequestParam(name = "agrDesc", required = false) String agrDesc) {
		LOG.info("Contract ID : " + productContract.getId());
		if (remindMeDays != null) {
			LOG.info("Remind me Days for reminder : " + remindMeDays.length + " Document Size " + file.length + " File Name " + file + " LOA Document " + loaFile + " Agreement Document " + agrFile);
		} else {
			LOG.info("Remind me Days are empty : ");
		}

		LOG.info("Start Date : " + productContract.getContractStartDate() + ", End Date : " + productContract.getContractEndDate());

		if (productContract.getSupplier() == null && productContract.getSupplierName() == null) {
			redir.addFlashAttribute("error", messageSource.getMessage("pr.supplier.required", null, Global.LOCALE));
			return "redirect:/buyer/productContractListEdit?id=" + productContract.getId();
		}

		if (productContract.getGroupCode() != null) {
			productContract.setGroupCodeStr(productContract.getGroupCode().getGroupCode());
		}
		if(productContract.getRemark() != null){
			productContract.setRemark(productContract.getRemark());
		}
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
			dateFormatter.setTimeZone(timeZone);
			SimpleDateFormat dateFormatterNoTimeZone = new SimpleDateFormat("dd/MM/yyyy");

			List<ProductContractReminder> remindMeDaysList = new ArrayList<>();
			if (remindMeDays != null) {
				for (String remindMe : remindMeDays) {
					ProductContractReminder contractReminder = new ProductContractReminder();
					contractReminder.setProductContract(productContract);
					contractReminder.setInterval(Integer.parseInt(remindMe));
					Calendar cal = Calendar.getInstance(timeZone);
					cal.setTime(productContract.getContractEndDate());
					cal.add(Calendar.DATE, -Integer.parseInt(remindMe));
					contractReminder.setReminderDate(cal.getTime());
					remindMeDaysList.add(contractReminder);
				}
			}

			remindMeDaysList.sort(Comparator.comparing(ProductContractReminder::getReminderDate));

			model.addAttribute("reminderList", remindMeDaysList);
			productContract.setContractReminders(remindMeDaysList);

			// Reminder Dates String
			String reminderDates = "";
			for (ProductContractReminder reminder : remindMeDaysList) {
				reminderDates += dateFormatter.format(reminder.getReminderDate()) + ",";
			}
			LOG.info("----------------" + reminderDates);
			if (StringUtils.checkString(reminderDates).length() > 2) {
				reminderDates = reminderDates.substring(0, reminderDates.length() - 1);
			}

			productContract.setContractReminderDates(reminderDates);

			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUser().getBuyer().getId());
			String decimal = buyerSettings.getDecimal();
			int z = Integer.parseInt(decimal);
			StringBuffer decimals = new StringBuffer(0);
			for (int i = 0; i < z; i++) {
				decimals.append(0);
			}
			model.addAttribute("decimals", decimals);
			model.addAttribute("decimal", decimal);

			List<SupplierPojo> supplierListData = favoriteSupplierService.searchFavouriteSupplier(SecurityLibrary.getLoggedInUserTenantId(), null, null);
			List<SupplierPojo> supplierList = new ArrayList<SupplierPojo>();

			String supplierName = null;
			for (SupplierPojo supplierObj : supplierListData) {
				if (productContract.getSupplier() != null && productContract.getSupplier().getId().equals(supplierObj.getId())) {
					supplierName = supplierObj.getCompanyName();
				}
			}

			LOG.info("productContract.getSupplier()  : " + productContract.getSupplierName());
			if (productContract.getSupplier() != null) {
				supplierList.add(new SupplierPojo(productContract.getSupplier().getId(), supplierName));
			}

			for (SupplierPojo supplier : supplierListData) {
				boolean isSupplierExist = false;
				if (productContract.getSupplier() != null && productContract.getSupplier().getId().equals(supplier.getId())) {
					isSupplierExist = true;
					supplierName = supplier.getCompanyName();
				}
				if (!isSupplierExist) {
					supplierList.add(supplier);
				}
			}

			model.addAttribute("favSupp", supplierList);
			model.addAttribute("currencyList", currencyService.getAllActiveCurrencies());
			List<GroupCode> groupCodeList = groupCodeService.fetchAllGroupCodesForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
			model.addAttribute("groupCodeList", groupCodeList);
			model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("agreementTypeList", agreementTypeService.getAllActiveAgreementType(SecurityLibrary.getLoggedInUserTenantId()));

			List<BusinessUnitPojo> businessUnitData = businessUnitService.fetchBusinessUnitByTenantId(SecurityLibrary.getLoggedInUserTenantId(), null);
			List<BusinessUnitPojo> businessUnitList = new ArrayList<BusinessUnitPojo>();
			if (productContract.getBusinessUnit() != null) {
				businessUnitList.add(new BusinessUnitPojo(productContract.getBusinessUnit().getId(), productContract.getBusinessUnit().getDisplayName()));
			}
			for (BusinessUnitPojo unit : businessUnitData) {
				boolean isUnitExist = false;
				if (productContract.getBusinessUnit() != null && productContract.getBusinessUnit().getId().equals(unit.getId())) {
					isUnitExist = true;
				}
				if (!isUnitExist) {
					businessUnitList.add(unit);
				}
			}
			model.addAttribute("businessUnit", businessUnitList);

			List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
			List<UserPojo> notifyUserList = new ArrayList<UserPojo>();
			if (CollectionUtil.isNotEmpty(productContract.getNotifyUsers())) {
				for (ProductContractNotifyUsers notifyUser : productContract.getNotifyUsers()) {
					notifyUserList.add(new UserPojo(notifyUser.getUser().getId(), notifyUser.getUser().getLoginId(), notifyUser.getUser().getName(), notifyUser.getUser().getTenantId(), notifyUser.getUser().isDeleted(), notifyUser.getUser().getCommunicationEmail(), notifyUser.getUser().getEmailNotifications()));
				}
			}
			for (UserPojo user : userList) {
				try {
					if (!notifyUserList.contains(user)) {
						notifyUserList.add(user);
					}
				} catch (Exception e) {
					LOG.info("Error while cloning the user List :" + e.getMessage());
				}
			}
			model.addAttribute("notifyUserList", notifyUserList);

			if (StringUtils.checkString(productContract.getId()).length() == 0) {
				LOG.info("inside save");
				model.addAttribute("btnValue2", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			} else {
				model.addAttribute("btnValue2", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
			}

			List<String> errMessages = new ArrayList<String>();
			if ((productContract != null && StringUtils.checkString(productContract.getId()).length() != 0) || doValidate(productContract)) {
				if (result.hasErrors()) {
					for (ObjectError err : result.getAllErrors()) {
						errMessages.add(err.getDefaultMessage());
					}
					model.addAttribute("error", errMessages);
					model.addAttribute("currencyList", currencyService.getAllActiveCurrencies());
					model.addAttribute("productContract", productContract);
					return "productContractUpdate";
				} else if (StringUtils.checkString(productContract.getId()).length() == 0) {
					// NO use
				} else if (StringUtils.checkString(productContract.getId()).length() > 0) {// Update Contract
					LOG.info("Product contract In update::");

					try {
						ProductContract productContractObj = productContractService.findProductContractById(productContract.getId(), SecurityLibrary.getLoggedInUserTenantId());

						if (productContract.getContractStartDate() != null && productContract.getContractEndDate() != null && productContract.getContractStartDate().after(productContract.getContractEndDate())) {
							model.addAttribute("error", messageSource.getMessage("contract.error.enddate", new Object[] { dateFormatter.format(productContract.getContractEndDate()), dateFormatter.format(productContract.getContractStartDate()) }, Global.LOCALE));
							productContract.setStatus(productContractObj.getStatus());
							model.addAttribute("productContract", productContract);
							buildModel(productContract, model);
							return "productContractUpdate";
						}

						if (CollectionUtil.isNotEmpty(productContract.getNotifyUsers())) {
							for (ProductContractNotifyUsers notifyUsers : productContract.getNotifyUsers()) {
								notifyUsers.setProductContract(productContractObj);
							}
						}

						if (ContractStatus.SUSPENDED == productContractObj.getStatus()) {
							SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
							if (productContractObj.getContractValue().doubleValue() != productContract.getContractValue().doubleValue()) {
								productContractObj.setOldContractValue(productContractObj.getContractValue());
							}
							String oldStartDate = df.format(productContractObj.getContractStartDate());
							String newStartDate = df.format(productContract.getContractStartDate());
							if (!StringUtils.checkString(oldStartDate).equals(StringUtils.checkString(newStartDate))) {
								productContractObj.setOldContractStartDate(productContractObj.getContractStartDate());
							}
							String oldEndDate = df.format(productContractObj.getContractEndDate());
							String newEndDate = df.format(productContract.getContractEndDate());
							if (!StringUtils.checkString(oldEndDate).equals(StringUtils.checkString(newEndDate))) {
								productContractObj.setOldContractEndDate(productContractObj.getContractEndDate());
							}
						}

						productContractObj.setContractStartDate(productContract.getContractStartDate());
						productContractObj.setContractEndDate(productContract.getContractEndDate());
						productContractObj.setContractValue(productContract.getContractValue() != null && productContract.getDecimal() != null ? productContract.getContractValue().setScale(Integer.valueOf(productContract.getDecimal()), RoundingMode.HALF_UP) : productContract.getContractValue());
						productContractObj.setContractReminders(remindMeDaysList);
						productContractObj.setNotifyUsers(productContract.getNotifyUsers());
						productContractObj.setContractId(productContract.getContractId());
						productContractObj.setContractName(productContract.getContractName());
						// productContractObj.setSapContractNumber(productContract.getSapContractNumber());
						productContractObj.setPreviousContractNo(productContract.getPreviousContractNo());
						productContractObj.setContractReferenceNumber(productContract.getContractReferenceNumber());
						productContractObj.setGroupCodeStr(productContract.getGroupCode().getGroupCode());
						productContractObj.setGroupCode(productContract.getGroupCode());
						productContractObj.setBusinessUnit(productContract.getBusinessUnit());
						productContractObj.setSupplier(productContract.getSupplier());
						productContractObj.setCurrency(productContract.getCurrency());
						productContractObj.setRenewalContract(productContract.getRenewalContract());
						productContractObj.setContractDetailCompleted(Boolean.TRUE);
						productContractObj.setRemark(productContract.getRemark());
						productContractObj.setSupplierName(productContract.getSupplierName());
						boolean decimalChanged = false;
						if (!productContract.getDecimal().equals(productContractObj.getDecimal())) {
							decimalChanged = true;
						}

						productContractObj.setDecimal(productContract.getDecimal());
						productContractObj.setProcurementCategory(productContract.getProcurementCategory());
						productContractObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						productContractObj.setModifiedDate(new Date());
						productContractObj.setAgreementType(productContract.getAgreementType());
						productContractObj.setEnableApproval(productContract.getEnableApproval());
						productContractObj.setEnableApprovalReminder(productContract.getEnableApprovalReminder());
						productContractObj.setReminderAfterHour(productContract.getReminderAfterHour());
						productContractObj.setReminderCount(productContract.getReminderCount());
						productContractObj.setContractReminderDates(productContract.getContractReminderDates());
						productContractObj.setNotifyEventOwner(productContract.getNotifyEventOwner());

						if (productContractObj.getContractId() == null) {
							String id = eventIdSettingsService.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "CTR", productContractObj.getBusinessUnit());
							productContractObj.setContractId(id);
						}

						// If approval is not enabled then clear the approval levels
						if (Boolean.FALSE == productContractObj.getEnableApproval()) {
							LOG.info("Approval is disabled.....");
							if (productContractObj.getApprovals() != null) {
								for (ContractApproval app : productContractObj.getApprovals()) {
									app.setProductContract(null);
								}
								productContractObj.getApprovals().clear();
							}
						} else {
							List<ContractApproval> approvals = new ArrayList<ContractApproval>();
							try {
								if (CollectionUtil.isNotEmpty(productContract.getApprovals())) {
									LOG.info("Total Approval Levels : " + productContract.getApprovals().size());
									int level = 1;
									for (ContractApproval app : productContract.getApprovals()) {
										LOG.info("app Level : " + app.getLevel());
										app.setProductContract(productContractObj);
										app.setLevel(level++);
										LOG.info("type :" + app.getApprovalType());
										for (ContractApprovalUser approvalUser : app.getApprovalUsers()) {
											approvalUser.setApproval(app);
											LOG.info("user id : " + approvalUser.getUser().getId() + " username : " + approvalUser.getUser().getName());
										}
										approvals.add(app);
									}
									productContractObj.setApprovals(approvals);
								} else {
									LOG.info("No Approvals...");
								}
							} catch (Exception e) {
								LOG.error("Error While saving the Approvers" + e.getMessage(), e);
								return "redirect:productContract";
							}
						}

						productContractObj = productContractService.updateContract(productContractObj, session, SecurityLibrary.getLoggedInUser(), virtualizer, decimalChanged);

						LOG.info("AFTER UPDATE -> Start Date : " + productContractObj.getContractStartDate() + ", End Date : " + productContractObj.getContractEndDate());

						for (int i = 0; i < file.length; i++) {
							if (file[i] != null) {
								if (!file[i].isEmpty()) {
									String fileName = file[i].getOriginalFilename();
									try {
										byte[] bytes = file[i].getBytes();
										if (bytes != null) {
											LOG.info("FILE CONTENT Size : " + bytes.length + " Doc Desc : " + docDesc[i]);
										}
										ContractDocument contractDocument = new ContractDocument();
										contractDocument.setFileName(fileName);
										contractDocument.setFileData(bytes);
										contractDocument.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
										contractDocument.setUploadDate(new Date());
										contractDocument.setContentType(file[i].getContentType());
										contractDocument.setProductContract(productContractObj);
										contractDocument.setDescription(docDesc[i]);
										contractDocument.setUploadedBy(SecurityLibrary.getLoggedInUser());
										contractDocument.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
										contractDocumentService.saveContractDocument(contractDocument);
									} catch (Exception e) {
										LOG.error("Failed to upload " + fileName + ": " + e.getMessage(), e);
									}
								}
							}
						}

						ContractLoaAndAgreement contractLoaAndAgrDocument = contractLoaAndAgreementService.findContractLoaAndAgreementByContractId(productContract.getId());
						if (contractLoaAndAgrDocument == null) {
							contractLoaAndAgrDocument = new ContractLoaAndAgreement();
						}
						contractLoaAndAgrDocument.setProductContract(productContractObj);
						contractLoaAndAgrDocument.setTenantId(SecurityLibrary.getLoggedInUserTenantId());

						if (agrDate != null) {
							Date agrDates = dateFormatterNoTimeZone.parse(agrDate);
							contractLoaAndAgrDocument.setAgreementDate(agrDates);
							LOG.info("Agreement Date : " + agrDate + " After Convert : " + agrDates);
						} else {
							contractLoaAndAgrDocument.setAgreementDate(null);
						}
						if (loaDate != null) {
							Date loaDates = dateFormatterNoTimeZone.parse(loaDate);
							contractLoaAndAgrDocument.setLoaDate(loaDates);
							LOG.info("LOA Date : " + loaDate + " After Convert : " + loaDates);
						} else {
							contractLoaAndAgrDocument.setLoaDate(null);
						}

						contractLoaAndAgrDocument.setLoaDescription(loaDesc);
						contractLoaAndAgrDocument.setAgreementDescription(agrDesc);

						if ((loaFile != null && !loaFile.isEmpty()) || (agrFile != null && !agrFile.isEmpty())) {

							try {
								if ((loaFile != null && !loaFile.isEmpty())) {
									LOG.info("ORIGINALFILENAME is : " + loaFile.getOriginalFilename());
									LOG.info("NAME is : " + loaFile.getName());
									LOG.info("BYTES is : " + loaFile.getBytes());
									contractLoaAndAgrDocument.setLoaFileName(loaFile.getOriginalFilename() != null ? loaFile.getOriginalFilename() : "");
									if (loaFile.getBytes() != null) {
										contractLoaAndAgrDocument.setLoaFileData(loaFile.getBytes());
										contractLoaAndAgrDocument.setLoaContentType(loaFile.getContentType());
									} else {
										model.addAttribute("error", messageSource.getMessage("contract.invalid.loa.file", new Object[] {}, Global.LOCALE));
										return "";
									}
									contractLoaAndAgrDocument.setLoaUploadDate(new Date());
									contractLoaAndAgrDocument.setLoaFileSizeInKb(loaFile.getBytes().length > 0 ? loaFile.getBytes().length / 1024 : 0);
								}

								if (agrFile != null && !agrFile.isEmpty()) {
									LOG.info("ORIGINALFILENAME is : " + agrFile.getOriginalFilename());
									LOG.info("NAME is : " + agrFile.getName());
									LOG.info("BYTES is : " + agrFile.getBytes());

									contractLoaAndAgrDocument.setAgreementFileName(agrFile.getOriginalFilename() != null ? agrFile.getOriginalFilename() : "");
									if (agrFile.getBytes() != null) {
										contractLoaAndAgrDocument.setAgreementFileData(agrFile.getBytes());
										contractLoaAndAgrDocument.setAgreementContentType(agrFile.getContentType());
									} else {
										model.addAttribute("error", messageSource.getMessage("contract.invalid.agre.file", new Object[] {}, Global.LOCALE));
										return "";
									}
									contractLoaAndAgrDocument.setAgreementUploadDate(new Date());
									contractLoaAndAgrDocument.setAgreementFileSizeInKb(agrFile.getBytes().length > 0 ? agrFile.getBytes().length / 1024 : 0);
									contractLoaAndAgrDocument.setLoaUploadedBy(SecurityLibrary.getLoggedInUser());
									contractLoaAndAgrDocument.setAgreementUploadedBy(SecurityLibrary.getLoggedInUser());
									// contractLoaAndAgrDocument.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
									// contractLoaAndAgrDocument.setProductContract(productContract);
								}
							} catch (Exception e) {
								LOG.error("Failed to upload LOA/Agreement : " + e.getMessage(), e);
							}
						}
						contractLoaAndAgreementService.saveLoaAndAgrContractDocument(contractLoaAndAgrDocument);

						redir.addFlashAttribute("success", messageSource.getMessage("contract.update.success", new Object[] { productContractObj.getContractReferenceNumber() }, Global.LOCALE));
						return "redirect:productContractItemList/" + productContract.getId();
					} catch (Exception e) {
						LOG.error("Error While saving the contract : " + e.getMessage(), e);
						model.addAttribute("error", messageSource.getMessage("product.contract.error.update", new Object[] { e.getMessage() }, Global.LOCALE));
						model.addAttribute("productContract", productContract);
						return "productContractUpdate";
					}
				}
			} else {
				if (StringUtils.checkString(productContract.getId()).length() == 0) {
					model.addAttribute("btnValue2", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				} else {
					model.addAttribute("btnValue2", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
				}
				LOG.error("Contract Reference Number " + productContract.getContractReferenceNumber() + " already exists.");
				redir.addFlashAttribute("error", messageSource.getMessage("contract.error.duplicate", new Object[] { productContract.getContractReferenceNumber() }, Global.LOCALE));
				return "redirect:productContractListEdit?id=" + productContract.getId();

			}
			model.addAttribute("productContract", productContract);
			return "productContractUpdate";
		} catch (Exception e) {
			LOG.error("Error While saving the productList" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.contract.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractListEdit?id=" + productContract.getId();
		}
	}

	private void buildModel(ProductContract productContractObj, Model model) {

		String id = productContractObj.getId();
		// TODO Auto-generated method stub
		BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUser().getBuyer().getId());
		String decimal = buyerSettings.getDecimal();
		int z = Integer.parseInt(decimal);
		StringBuffer decimals = new StringBuffer(0);
		for (int i = 0; i < z; i++) {
			decimals.append(0);
		}
		model.addAttribute("decimals", decimals);
		model.addAttribute("decimal", decimal);

		EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), id);
		model.addAttribute("eventPermissions", eventPermissions);

		// model.addAttribute("data", productListObj);
		List<SupplierPojo> supplierListData = favoriteSupplierService.searchFavouriteSupplier(SecurityLibrary.getLoggedInUserTenantId(), null, null);
		List<SupplierPojo> supplierList = new ArrayList<SupplierPojo>();
		if (productContractObj.getSupplier() != null) {
			Supplier favouriteSupplier = favoriteSupplierService.getSupplierByFavSupplierId(productContractObj.getSupplier().getId());
			supplierList.add(new SupplierPojo(productContractObj.getSupplier().getId(), favouriteSupplier.getCompanyName()));
		}
		for (SupplierPojo supplier : supplierListData) {
			boolean isSupplierExist = false;
			if (productContractObj.getSupplier() != null && productContractObj.getSupplier().getId().equals(supplier.getId())) {
				isSupplierExist = true;
			}
			if (!isSupplierExist) {
				supplierList.add(supplier);
			}
		}
		model.addAttribute("favSupp", supplierList);

		List<BusinessUnitPojo> businessUnitData = businessUnitService.fetchBusinessUnitByTenantId(SecurityLibrary.getLoggedInUserTenantId(), null);
		List<BusinessUnitPojo> businessUnitList = new ArrayList<BusinessUnitPojo>();
		if (productContractObj.getBusinessUnit() != null) {
			businessUnitList.add(new BusinessUnitPojo(productContractObj.getBusinessUnit().getId(), productContractObj.getBusinessUnit().getDisplayName()));
		}
		for (BusinessUnitPojo unit : businessUnitData) {
			boolean isUnitExist = false;
			if (productContractObj.getBusinessUnit() != null && productContractObj.getBusinessUnit().getId().equals(unit.getId())) {
				isUnitExist = true;
			}
			if (!isUnitExist) {
				businessUnitList.add(unit);
			}
		}
		model.addAttribute("businessUnit", businessUnitList);

		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		if (enableCorrelation != null && enableCorrelation == Boolean.TRUE) {
			List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, productContractObj.getBusinessUnit().getId());
			model.addAttribute("costCenterList", costCenterList);
		} else {
			List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, null);
			model.addAttribute("costCenterList", costCenterList);
		}

		List<ProductContractReminder> productReminders = productContractReminderService.getAllContractRemindersByContractId(productContractObj.getId());
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		List<User> notifyUserList = new ArrayList<User>();
		if (CollectionUtil.isNotEmpty(productContractObj.getNotifyUsers())) {
			for (ProductContractNotifyUsers notifyUser : productContractObj.getNotifyUsers()) {
				notifyUserList.add(new User(notifyUser.getUser().getId(), notifyUser.getUser().getLoginId(), notifyUser.getUser().getName(), notifyUser.getUser().getCommunicationEmail(), notifyUser.getUser().getEmailNotifications(), notifyUser.getUser().getTenantId(), notifyUser.getUser().isDeleted()));
			}
		}
		// UserPojo loggedInUserPojo = new UserPojo();
		// loggedInUserPojo.setId(productContractObj.getCreatedBy().getId());
		// loggedInUserPojo.setLoginId(productContractObj.getCreatedBy().getLoginId());

		for (UserPojo user : userList) {
			try {
				User userObj = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!notifyUserList.contains(userObj)) {
					notifyUserList.add(userObj);
				}
			} catch (Exception e) {
				LOG.info("Error while cloning the user List :" + e.getMessage());
			}
		}

		List<ContractDocument> contractDocument = contractDocumentService.findAllContractdocsbyContractId(id);
		model.addAttribute("contractDocument", contractDocument);
		ContractLoaAndAgreement loaAndAgrcontractDocument = productContractService.findContractLoaAndAgreementByContractId(id);
		model.addAttribute("loaAndAgrcontractDocument", loaAndAgrcontractDocument);

		for (User us : notifyUserList) {
			LOG.info("Notify User in Controller : " + us.getName());
		}

		model.addAttribute("notifyUserList", notifyUserList);
		model.addAttribute("reminderList", productReminders);
		model.addAttribute("btnValue2", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		List<ProductItemPojo> productItemList = productContractItemsService.findProductContractItems(id);
		List<ProductItemPojo> list = productListMaintenanceService.fetchAllActiveProductItemNameAndCodeForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		if (CollectionUtil.isNotEmpty(list)) {
			for (ProductItemPojo pojo : list) {
				if (!productItemList.contains(pojo)) {
					productItemList.add(pojo);
				}
			}
		}

		model.addAttribute("productItemList", productItemList);
		List<Uom> uomList = uomService.getAllActiveUomForTenant(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("uomList", uomList);
		List<GroupCode> groupCodeList = groupCodeService.fetchAllGroupCodesForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		model.addAttribute("groupCodeList", groupCodeList);
		model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("agreementTypeList", agreementTypeService.getAllActiveAgreementType(SecurityLibrary.getLoggedInUserTenantId()));

		List<ProductCategoryPojo> itemCategories = productContractItemsService.findProductCategories(id);
		List<ProductCategoryPojo> catList = productCategoryMaintenanceService.fetchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		if (CollectionUtil.isNotEmpty(itemCategories)) {
			for (ProductCategoryPojo pojo : itemCategories) {
				if (!catList.contains(pojo)) {
					catList.add(pojo);
				}
			}
		}
		model.addAttribute("productCategoryList", catList);
		model.addAttribute("currencyList", currencyService.getAllCurrency());

		Date newDate = DateUtil.formatDateToStartTime(new Date());
		Boolean contractExpire = false;
		if (productContractObj.getContractEndDate() != null) {
			if (productContractObj.getContractEndDate().before(newDate)) {
				contractExpire = true;
			}
		}

		model.addAttribute("contractExpire", contractExpire);

		// Contract users for Approval

		List<ContractApprovalUser> approvalUserList = new ArrayList<ContractApprovalUser>();

		if (CollectionUtil.isNotEmpty(productContractObj.getApprovals())) {
			for (ContractApproval approval : productContractObj.getApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (ContractApprovalUser approvalUser : approval.getApprovalUsers()) {
						User user = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(), approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(), approvalUser.getUser().getEmailNotifications(), approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());
						if (!approvalUserList.contains(new ContractApprovalUser(user))) {
							approvalUserList.add(new ContractApprovalUser(user));
						}
					}
				}
			}
		}
		for (UserPojo userPojo : userList) {
			// if (userPojo.equals(loggedInUserPojo)) {
			// continue;
			// }
			User user = new User(userPojo.getId(), userPojo.getLoginId(), userPojo.getName(), userPojo.getCommunicationEmail(), userPojo.isEmailNotifications(), userPojo.getTenantId(), userPojo.isDeleted());
			if (!approvalUserList.contains(new ContractApprovalUser(user))) {
				approvalUserList.add(new ContractApprovalUser(user));
			}
		}

		List<UserPojo> userTeamMemberList = new ArrayList<UserPojo>();
		for (UserPojo userPojo : userList) {
			boolean found = false;
			if (productContractObj.getTeamMembers() != null) {
				for (ContractTeamMember tm : productContractObj.getTeamMembers()) {
					if (tm.getUser().getId().equals(userPojo.getId())) {
						found = true;
						break;
					}
				}
			}
			if (!found) {
				userTeamMemberList.add(userPojo);
			}
		}

		model.addAttribute("userTeamMemberList", userTeamMemberList);
		model.addAttribute("userList", approvalUserList);
		model.addAttribute("userList1", userList);

	}

	@PostMapping("/saveContractItem")
	public @ResponseBody ResponseEntity<String> saveContractItem(@RequestParam String itemId, @RequestParam String contractId, @RequestParam String productItem, //
			@RequestParam String itemName, @RequestParam String itemCode, @RequestParam Boolean freeTextItemEntered, @RequestParam BigDecimal quantity, @RequestParam BigDecimal unitPrice, //
			@RequestParam BigDecimal pricePerUnit, //
			@RequestParam String storageLocation, @RequestParam String uom, @RequestParam String businessUnit, @RequestParam String costCenterId, @RequestParam String itemBrand, //
			@RequestParam BigDecimal itemTax, @RequestParam String itemProductCategory, @RequestParam ProductItemType productItemType) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("Save contract Item: " + contractId + " itemId: " + itemId + "unit Price " + unitPrice);
		try {
			ProductContract contractObj = productContractService.findProductContractById(contractId, SecurityLibrary.getLoggedInUserTenantId());
			ProductContractItems item = null;
			if (StringUtils.checkString(itemId).length() > 0) {
				item = productContractItemsService.findProductContractItemById(itemId);
				if (ContractStatus.SUSPENDED == contractObj.getStatus() && ContractStatus.ACTIVE == contractObj.getOldStatus()) {
					if (item != null && item.getQuantity().doubleValue() != quantity.doubleValue()) {
						item.setOldQuantity(item.getQuantity());
					}
				}
			} else {
				item = new ProductContractItems();
				long productItemList = productContractItemsService.findTotalProductItemListForTenant(SecurityLibrary.getLoggedInUserTenantId(), contractId);
				LOG.info("productItemList : " + productItemList);
				int itemNumber = (int) (productItemList + 1);
				item.setContractItemNumber(String.valueOf(itemNumber));
			}

			if (uom != null) {
				Uom uomObj = new Uom();
				uomObj.setId(uom);
				item.setUom(uomObj);
			}
			if (StringUtils.checkString(businessUnit).length() > 0) {
				BusinessUnit unit = new BusinessUnit();
				unit.setId(businessUnit);
				item.setBusinessUnit(unit);
			}
			if (StringUtils.checkString(costCenterId).length() > 0) {
				CostCenter cost = new CostCenter();
				cost.setId(costCenterId);
				item.setCostCenter(cost);
			}
			if (itemProductCategory != null) {
				ProductCategory category = new ProductCategory();
				category.setId(itemProductCategory);
				item.setProductCategory(category);
			}
			item.setProductContract(contractObj);
			item.setFreeTextItemEntered(freeTextItemEntered);
			if (Boolean.TRUE == freeTextItemEntered) {
				item.setItemName(itemName);
				item.setItemCode(itemCode);
				item.setProductItem(null);
			} else {
				ProductItem productItemObj = productListMaintenanceService.findProductItembyId(productItem);
				item.setProductItem(productItemObj);
				item.setItemName(productItemObj.getProductName());
				item.setItemCode(productItemObj.getProductCode());
			}
			item.setQuantity(quantity);
			item.setBalanceQuantity(quantity);
			item.setUnitPrice(unitPrice != null ? unitPrice.setScale(Integer.parseInt(contractObj.getDecimal()), RoundingMode.HALF_UP) : null);
			item.setPricePerUnit(pricePerUnit != null ? pricePerUnit.setScale(Integer.parseInt(contractObj.getDecimal()), RoundingMode.HALF_UP) : null);
			item.setStorageLocation(storageLocation);
			item.setTax(itemTax);
			if (item.getTax() == null) {
				item.setTax(BigDecimal.ZERO);
			}
			if (item.getTaxAmount() == null) {
				item.setTaxAmount(BigDecimal.ZERO);
			}

			item.setBrand(itemBrand);
			// item.setBalanceQuantity(balanceQuantity);
			item.setProductItemType(productItemType);
			productContractItemsService.saveContractItem(item);

			if (StringUtils.checkString(itemId).length() > 0) {
				headers.add("success", messageSource.getMessage("productContract.update.success", new Object[] { item.getContractItemNumber() }, Global.LOCALE));
			} else {
				headers.add("success", messageSource.getMessage("product.contract.item.create.success", new Object[] { item.getContractItemNumber() }, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while SaveContractItem " + e.getMessage(), e);
			headers.add("error", "Error while SaveContractItem");
			return new ResponseEntity<String>("error", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>("success", headers, HttpStatus.OK);
	}

	@PostMapping("/deleteContractItem/{itemId}")
	public ResponseEntity<String> deleteContractItem(@PathVariable String itemId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("delete contract Item: " + itemId);
		try {
			productContractItemsService.delete(itemId);
			headers.add("success", messageSource.getMessage("product.contrac.item.delete.success", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while delete contract Item " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("product.contrac.item.delete.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/saveProductContract1", method = RequestMethod.POST)
	public String saveProductContract1(@ModelAttribute("productContract") ProductContract productContract, Model model, HttpSession session, RedirectAttributes redir) {
		try {
			LOG.info("inside update " + productContract.getId());
			ProductContract productListObj = productContractService.findProductContractById(productContract.getId(), SecurityLibrary.getLoggedInUserTenantId());
			productListObj.setStatus(productContract.getStatus());
			if (CollectionUtil.isNotEmpty(productContract.getNotifyUsers())) {
				for (ProductContractNotifyUsers notifyUsers : productContract.getNotifyUsers()) {
					notifyUsers.setProductContract(productListObj);
				}
			}
			productListObj.setNotifyUsers(productContract.getNotifyUsers());
			List<ProductContractReminder> reminderList = productContractReminderService.getAllContractRemindersByContractId(productContract.getId());
			if (CollectionUtil.isNotEmpty(reminderList)) {
				for (ProductContractReminder productContractReminder : reminderList) {
					productContractReminder.setReminderDate(getReminderDate(productContract.getContractEndDate(), productContractReminder.getInterval()));
					if (productContractReminder.getReminderDate().before(productContract.getContractStartDate())) {
						throw new ApplicationException(messageSource.getMessage("product.reminder.before.start", new Object[] {}, Global.LOCALE));
					}
				}
			}
			productListObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
			productListObj.setModifiedDate(new Date());
			productContractService.createProductContract(productListObj);
			redir.addFlashAttribute("success", messageSource.getMessage("productContract.update.success", new Object[] { productListObj.getContractReferenceNumber() }, Global.LOCALE));
			return "redirect:productContractList";
		} catch (Exception e) {
			LOG.error("Error While updating the product Item " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
	}

	private boolean doValidate(ProductContract productContract) {
		boolean validate = true;
		if (productContractService.isExists(productContract.getContractReferenceNumber(), SecurityLibrary.getLoggedInUserTenantId(), productContract.getId())) {
			validate = false;
		}
		return validate;
	}

	@PostMapping("/searchFavouriteuppliers")
	public @ResponseBody ResponseEntity<List<SupplierPojo>> searchFavouriteuppliers(@RequestParam("searchSupplier") String searchSupplier) {
		List<SupplierPojo> favouriteSupplierList = favoriteSupplierService.searchFavouriteSupplier(SecurityLibrary.getLoggedInUserTenantId(), searchSupplier, null);
		return new ResponseEntity<List<SupplierPojo>>(favouriteSupplierList, HttpStatus.OK);
	}

	@PostMapping("/searchBusinessUnit")
	public @ResponseBody ResponseEntity<List<BusinessUnitPojo>> searchBusinessUnit(@RequestParam("searchUnit") String searchUnit) {
		List<BusinessUnitPojo> businessUnitList = businessUnitService.fetchBusinessUnitByTenantId(SecurityLibrary.getLoggedInUserTenantId(), searchUnit);
		return new ResponseEntity<List<BusinessUnitPojo>>(businessUnitList, HttpStatus.OK);
	}

	@PostMapping("/searchCostCenter")
	public @ResponseBody ResponseEntity<List<CostCenterPojo>> searchCostCenter(@RequestParam("searchCost") String searchCost, @RequestParam(name = "businessUnitId", required = false) String businessUnitId) {
		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		if (enableCorrelation != null && enableCorrelation == Boolean.TRUE) {
			List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), searchCost, businessUnitId);
			return new ResponseEntity<List<CostCenterPojo>>(costCenterList, HttpStatus.OK);
		} else {
			List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), searchCost, null);
			return new ResponseEntity<List<CostCenterPojo>>(costCenterList, HttpStatus.OK);
		}
	}

	@PostMapping("/searchProductCategory")
	public @ResponseBody ResponseEntity<List<ProductCategoryPojo>> searchProductCategory(@RequestParam("searchStr") String searchStr) {
		List<ProductCategoryPojo> list = productCategoryMaintenanceService.fetchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), searchStr);
		return new ResponseEntity<List<ProductCategoryPojo>>(list, HttpStatus.OK);
	}

	@PostMapping("/searchProductItemName")
	public @ResponseBody ResponseEntity<List<ProductItemPojo>> searchProductItemName(@RequestParam("searchProductItem") String searchProductItem) {
		List<ProductItemPojo> productItemList = productListMaintenanceService.fetchAllProductItemForTenant(SecurityLibrary.getLoggedInUserTenantId(), searchProductItem);
		return new ResponseEntity<List<ProductItemPojo>>(productItemList, HttpStatus.OK);
	}

	public static Date getReminderDate(Date contractEndDate, int reminderDays) {
		Calendar c = Calendar.getInstance();
		LOG.info("time:" + contractEndDate);
		c.setTime(contractEndDate);
		c.add(Calendar.DAY_OF_MONTH, -reminderDays);
		LOG.info("time after:" + c.getTime());

		return c.getTime();
	}

	@RequestMapping(path = "/contractUpcomingNewData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> contractUpcomingNewData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			Date currentDate = new Date();
			TableData<ProductContractPojo> data = new TableData<ProductContractPojo>(productContractService.findContractListByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, null, true, false, false, false, startDate, endDate, null));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, null, true, false, false, false, startDate, endDate, null);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, null, true, false, false, false, startDate, endDate, null);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Upcoming Product Contract list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/activeContractData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> activeContractData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			TableData<ProductContractPojo> data = new TableData<ProductContractPojo>(productContractService.findContractListByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, null, null, false, false, false, false, startDate, endDate, ContractStatus.ACTIVE));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, null, null, false, false, false, false, startDate, endDate, ContractStatus.ACTIVE);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractService.findContractByStatusForTeanant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), ContractStatus.ACTIVE);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Active Product Contract list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Active Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/suspendedContractData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> suspendedContractData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			TableData<ProductContractPojo> data = new TableData<ProductContractPojo>(productContractService.findContractListByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, null, null, false, false, false, false, startDate, endDate, ContractStatus.SUSPENDED));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, null, null, false, false, false, false, startDate, endDate, ContractStatus.SUSPENDED);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractService.findContractByStatusForTeanant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), ContractStatus.SUSPENDED);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Suspended Product Contract list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Suspended Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/oneMonthContractExpiredData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> oneMonthContractExpiredData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			Date currentDate = new Date();
			Date expiredDate = getExpiredDate(30);
			TableData<ProductContractPojo> data = new TableData<ProductContractPojo>(productContractService.findContractListByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, expiredDate, false, true, false, false, startDate, endDate, null));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, expiredDate, false, true, false, false, startDate, endDate, null);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, expiredDate, false, true, false, false, startDate, endDate, null);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Expired Product Contract list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/oneToThreeMonthContractExpiredData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> oneToThreeMonthContractExpiredData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			Date currentDate = getDate(30);
			Date expiredDate = getExpiredDate(90);
			TableData<ProductContractPojo> data = new TableData<ProductContractPojo>(productContractService.findContractListByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, expiredDate, false, true, false, false, startDate, endDate, null));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, expiredDate, false, true, false, false, startDate, endDate, null);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, expiredDate, false, true, false, false, startDate, endDate, null);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Product Contracts expiring in 3 months : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/threeToSixMonthContractExpiredData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> threeToSixMonthContractExpiredData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			Date currentDate = getDate(90);
			Date expiredDate = getExpiredDate(180);
			TableData<ProductContractPojo> data = new TableData<ProductContractPojo>(productContractService.findContractListByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, expiredDate, false, true, false, false, startDate, endDate, null));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, expiredDate, false, true, false, false, startDate, endDate, null);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, expiredDate, false, true, false, false, startDate, endDate, null);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Product Contracts expiring in 6 months : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/greaterSixMonthContractExpiredData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> greaterSixMonthContractExpiredData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			Date currentDate = getDate(180);
			TableData<ProductContractPojo> data = new TableData<ProductContractPojo>(productContractService.findContractListByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, null, false, false, true, false, startDate, endDate, null));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, null, false, false, true, false, startDate, endDate, null);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, null, false, false, true, false, startDate, endDate, null);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Product Contracts Expiring after 6 months : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/contractExpiredData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> contractExpiredData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			Date currentDate = new Date();
			TableData<ProductContractPojo> data = new TableData<ProductContractPojo>(productContractService.findContractListByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, null, false, false, false, true, startDate, endDate, null));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, currentDate, null, false, false, false, true, startDate, endDate, null);
			data.setRecordsFiltered(totalFilterCount);

			// long totalCount =
			// productContractService.findTotalFilteredContractByExpiredDaysBetweenForTenant(SecurityLibrary.getLoggedInUserTenantId(),
			// SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null :
			// SecurityLibrary.getLoggedInUser().getId(), input, currentDate, null, false, false, false, true,
			// startDate, endDate);
			long totalCount = productContractService.findContractByStatusForTeanant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), ContractStatus.EXPIRED);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Expired Product Contracts list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/contractDraftData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> contractDraftData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}
			TableData<ProductContractPojo> data = new TableData<ProductContractPojo>(productContractService.findDraftContractListForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredDraftContractByTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractService.findTotalFilteredDraftContractByTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Draft Product Contract list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/contractPendingData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> contractPendingData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}

			TableData<ProductContractPojo> data = new TableData<ProductContractPojo>(productContractService.findPendingContractListByTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredPendingContractByTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractService.findTotalFilteredPendingContractByTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Pending Product Contract list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/contractTerminatedData", method = RequestMethod.GET)
	public ResponseEntity<TableData<ProductContractPojo>> contractTerminatedData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
				}
			}

			TableData<ProductContractPojo> data = new TableData<ProductContractPojo>(productContractService.findTerminatedContractListByTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate));
			data.setDraw(input.getDraw());
			long totalFilterCount = productContractService.findTotalFilteredTerminatedContractByTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = productContractService.findTotalFilteredTerminatedContractByTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProductContractPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Terminated Product Contract list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<TableData<ProductContractPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private Date getDate(int current) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, current);
		return cal.getTime();

	}

	private Date getExpiredDate(int expired) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_MONTH, expired);
		return cal.getTime();
	}

	@RequestMapping(path = "/exportContractCsvReport", method = RequestMethod.POST)
	public String exportContractCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("productContractPojo") ContractPojo productContractPojo, boolean select_all, @RequestParam(name = "dateRange", required = false) String dateRange) throws IOException {
		LOG.info("Event Ids	" + productContractPojo.toLogString());
		try {
			File file = File.createTempFile("Contract Report-", ".csv");

			String eventArr[] = null;
			if (StringUtils.checkString(productContractPojo.getEventIds()).length() > 0) {
				eventArr = productContractPojo.getEventIds().split(",");
			}

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateRange).length() > 0 && select_all) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}

			productContractService.downloadCsvFileForContract(response, file, eventArr, productContractPojo, select_all, SecurityLibrary.getLoggedInUserTenantId(), formatter, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Contract Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(path = "/exportContractCsvReportForUpComingData", method = RequestMethod.POST)
	public String downloadContractCsvReportForUpcomingData(HttpSession session, HttpServletResponse response, RedirectAttributes redir, @RequestParam(name = "dateRange", required = false) String dateRange) {
		try {
			File file = File.createTempFile("Contract Report", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateRange).length() > 0) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}
			productContractService.downloadCsvFileForContractList(response, file, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, false, false, SecurityLibrary.getLoggedInUserTenantId(), formatter, ContractStatus.APPROVED, null, null);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Upcoming-Contract-Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(path = "/exportContractCsvReportFor1Month", method = RequestMethod.POST)
	public String downloadContractCsvReportFor1Month(HttpSession session, HttpServletResponse response, RedirectAttributes redir, @RequestParam(name = "dateRange", required = false) String dateRange) {
		try {
			File file = File.createTempFile("Contract Report", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			Date currentDate = new Date();
			Date expiredDate = getExpiredDate(30);

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateRange).length() > 0) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}
			productContractService.downloadCsvFileForContractList(response, file, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, true, false, SecurityLibrary.getLoggedInUserTenantId(), formatter, ContractStatus.ACTIVE, currentDate, expiredDate);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Contract-Report-Expiring-In-1-Month.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(path = "/exportContractCsvReportActive", method = RequestMethod.POST)
	public String exportContractCsvReportActive(HttpSession session, HttpServletResponse response, RedirectAttributes redir, @RequestParam(name = "dateRange", required = false) String dateRange) {
		try {
			File file = File.createTempFile("Contract Report-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			Date currentDate = getDate(30); // oneToThreeMonthContractExpiredData
			Date expiredDate = getExpiredDate(90); // oneToThreeMonthContractExpiredData
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateRange).length() > 0) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}
			productContractService.downloadCsvFileForContractList(response, file, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, false, false, SecurityLibrary.getLoggedInUserTenantId(), formatter, ContractStatus.ACTIVE, currentDate, expiredDate);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Active-Contract-Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(path = "/exportContractCsvReportSuspended", method = RequestMethod.POST)
	public String exportContractCsvReportSuspended(HttpSession session, HttpServletResponse response, RedirectAttributes redir, @RequestParam(name = "dateRange", required = false) String dateRange) {
		try {
			File file = File.createTempFile("Contract Report-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			Date currentDate = getDate(30); // oneToThreeMonthContractExpiredData
			Date expiredDate = getExpiredDate(90); // oneToThreeMonthContractExpiredData
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateRange).length() > 0) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}
			productContractService.downloadCsvFileForContractList(response, file, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, false, false, SecurityLibrary.getLoggedInUserTenantId(), formatter, ContractStatus.SUSPENDED, currentDate, expiredDate);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Suspended-Contract-Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(path = "/exportContractCsvReportFor3Month", method = RequestMethod.POST)
	public String exportContractCsvReportFor3Month(HttpSession session, HttpServletResponse response, RedirectAttributes redir, @RequestParam(name = "dateRange", required = false) String dateRange) {
		try {
			File file = File.createTempFile("Contract Report-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			Date currentDate = getDate(30); // oneToThreeMonthContractExpiredData
			Date expiredDate = getExpiredDate(90); // oneToThreeMonthContractExpiredData
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateRange).length() > 0) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}
			productContractService.downloadCsvFileForContractList(response, file, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, true, false, SecurityLibrary.getLoggedInUserTenantId(), formatter, ContractStatus.ACTIVE, currentDate, expiredDate);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Contract-Report-Expiring-1-3-Month.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(path = "/exportContractCsvReport6Month", method = RequestMethod.POST)
	public String downloadContractCsvReport6Month(HttpSession session, HttpServletResponse response, RedirectAttributes redir, @RequestParam(name = "dateRange", required = false) String dateRange) throws IOException {
		try {
			File file = File.createTempFile("Contract Report-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			Date currentDate = getDate(90); // threeToSixMonthContractExpiredData
			Date expiredDate = getExpiredDate(180); // threeToSixMonthContractExpiredData

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateRange).length() > 0) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}
			productContractService.downloadCsvFileForContractList(response, file, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, true, false, SecurityLibrary.getLoggedInUserTenantId(), formatter, ContractStatus.ACTIVE, currentDate, expiredDate);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Contract-Report-Expiring-3-6-Months.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(path = "/exportContractCsvReportFor10Month", method = RequestMethod.POST)
	public String downloadContractCsvReportFor10Month(HttpSession session, HttpServletResponse response, RedirectAttributes redir, @RequestParam(name = "dateRange", required = false) String dateRange) {
		try {
			File file = File.createTempFile("Contract Report-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			Date currentDate = getDate(180); // greaterSixMonthContractExpiredData

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateRange).length() > 0) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}
			productContractService.downloadCsvFileForContractList(response, file, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, false, true, SecurityLibrary.getLoggedInUserTenantId(), formatter, ContractStatus.ACTIVE, currentDate, null);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Contract-Report-Expiring-More-Than-6-Months.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(path = "/exportContractCsvReportForDraft", method = RequestMethod.POST)
	public String exportContractCsvReportForDraft(HttpSession session, HttpServletResponse response, @RequestParam(name = "dateRange", required = false) String dateRange, RedirectAttributes redir) {
		try {
			File file = File.createTempFile("Draft Contract Report-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath() + " For Draft Contract List : " + dateRange);

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateRange).length() > 0) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}
			productContractService.downloadCsvFileForContractList(response, file, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, false, false, SecurityLibrary.getLoggedInUserTenantId(), formatter, ContractStatus.DRAFT, null, null);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Draft-Contract-Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(path = "/exportContractCsvReportForExpired", method = RequestMethod.POST)
	public String exportContractCsvReportForExpired(HttpSession session, HttpServletResponse response, @RequestParam(name = "dateRange", required = false) String dateRange, RedirectAttributes redir) {
		try {
			File file = File.createTempFile("Draft Contract Report-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath() + " For Draft Contract List : " + dateRange);

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateRange).length() > 0) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}
			productContractService.downloadCsvFileForContractList(response, file, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, false, false, SecurityLibrary.getLoggedInUserTenantId(), formatter, ContractStatus.EXPIRED, null, null);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Expired-Contract-Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(path = "/exportContractCsvReportForTerminated", method = RequestMethod.POST)
	public String exportContractCsvReportForTerminated(HttpSession session, HttpServletResponse response, @RequestParam(name = "dateRange", required = false) String dateRange, RedirectAttributes redir) {
		try {
			File file = File.createTempFile("Draft Contract Report-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath() + " For Draft Contract List : " + dateRange);

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateRange).length() > 0) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}
			productContractService.downloadCsvFileForContractList(response, file, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, false, false, SecurityLibrary.getLoggedInUserTenantId(), formatter, ContractStatus.TERMINATED, null, null);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Terminated-Contract-Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(path = "/exportContractCsvReportForPending", method = RequestMethod.POST)
	public String exportContractCsvReportForPending(HttpSession session, HttpServletResponse response, @RequestParam(name = "dateRange", required = false) String dateRange, RedirectAttributes redir, @ModelAttribute("productContractPojo") ProductContractPojo productContractPojo, boolean select_all) throws IOException {
		LOG.info("Event Ids	" + productContractPojo.getEventIds());
		try {
			File file = File.createTempFile("Pending Contract Report-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath() + " For Pending Contract List : " + dateRange);

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateRange).length() > 0) {
				String dateTimeArr[] = dateRange.split("-");
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
			}
			productContractService.downloadCsvFileForContractList(response, file, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), startDate, endDate, false, false, SecurityLibrary.getLoggedInUserTenantId(), formatter, ContractStatus.PENDING, null, null);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Pending-Contract-Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:productContractList";
		}
		return null;
	}

	@RequestMapping(value = "/downloadContractDocument/{id}", method = RequestMethod.GET)
	public void downloadContractDocument(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Contract Document Download  :: :: " + id + "::::::");
			contractDocumentService.downloadContractDocument(id, response);
		} catch (Exception e) {
			LOG.error("Error while downloaded Contract Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/deleteContractDocument", method = RequestMethod.POST)
	public ResponseEntity<String> deleteContractDocument(@RequestParam("documentId") String documentId, @RequestParam("contractId") String contractId) {
		try {
			HttpHeaders headers = new HttpHeaders();
			if (StringUtils.checkString(documentId).length() > 0) {
				contractDocumentService.removeContractDocument(documentId);
				headers.add("success", messageSource.getMessage("rft.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<String>("{ \"status\" : \"SUCCESS\" }", headers, HttpStatus.OK);
			}
			headers.add("error", "Document Id is required");
			return new ResponseEntity<String>("{ \"status\" : \"ERROR\" }", headers, HttpStatus.EXPECTATION_FAILED);
		} catch (Exception e) {
			LOG.error("Error while Error while removing Other Credential : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("{ \"status\" : \"ERROR\" }", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/itemDetailsOnProductBase", method = RequestMethod.GET)
	public ResponseEntity<ContractProductItemPojo> itemDetailsOnProductBase(@RequestParam("productItemId") String productItemId) {
		HttpHeaders headers = new HttpHeaders();
		try {
			ContractProductItemPojo data = productContractService.getProductItemListByProductItemId(productItemId);
			// ContractProductItemPojo pojo = new ContractProductItemPojo();
			// if (data != null) {
			// pojo.setBrand(data.getBrand());
			// pojo.setTax(data.getTax());
			// pojo.setUnitPrice(data.getUnitPrice());
			// pojo.setProductItemType(data.getProductItemType());
			// pojo.setUom(data.getUom());
			// pojo.setProductCategory(data.getProductCategory());
			// pojo.setProductItemType(data.getProductItemType());
			// }

			return new ResponseEntity<ContractProductItemPojo>(data, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error fetching Product Items list : " + e.getMessage(), e);
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<ContractProductItemPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getCostCenterByBusinessUnit", method = RequestMethod.GET)
	public ResponseEntity<List<CostCenterPojo>> getCostCenterByBusinessUnit(@RequestParam("businessUnitId") String businessUnitId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("Business Unit " + businessUnitId);
		try {
			Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (enableCorrelation != null && enableCorrelation == Boolean.TRUE) {
				List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, businessUnitId);
				return new ResponseEntity<List<CostCenterPojo>>(costCenterList, HttpStatus.OK);
			} else {
				List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, null);
				return new ResponseEntity<List<CostCenterPojo>>(costCenterList, HttpStatus.OK);
			}

		} catch (Exception e) {
			LOG.error("Error fetching Cost Center by Business Unit : " + e.getMessage(), e);
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<List<CostCenterPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getBusinessUnitByBusinessUnit", method = RequestMethod.GET)
	public ResponseEntity<List<BusinessUnit>> getBusinessUnitByBusinessUnit(@RequestParam("businessUnitId") String businessUnitId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("BU ID : " + businessUnitId);
		BusinessUnit businessUnit = businessUnitService.getPlainBusinessUnitById(businessUnitId);
		try {

			List<BusinessUnit> data = businessUnitService.getBusinessUnitForContractFromAward(businessUnit);

			if (!data.contains(businessUnit)) {
				data.add(0, businessUnit);
			}
			return new ResponseEntity<List<BusinessUnit>>(data, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error fetching Business Units : " + e.getMessage(), e);
			headers.add("error", "Error fetching productList Contract  list : " + e.getMessage());
			return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/productContractItemList/{id}", method = RequestMethod.GET)
	public ModelAndView productContractItemList(@PathVariable("id") String id, Model model) {
		ProductContract productContract = productContractService.getProductContractById(id);

		List<BusinessUnitPojo> businessUnitData = businessUnitService.fetchBusinessUnitByTenantId(SecurityLibrary.getLoggedInUserTenantId(), null);

		List<BusinessUnitPojo> businessUnitList = new ArrayList<BusinessUnitPojo>();
		if (productContract.getBusinessUnit() != null) {
			businessUnitList.add(new BusinessUnitPojo(productContract.getBusinessUnit().getId(), productContract.getBusinessUnit().getDisplayName(), productContract.getBusinessUnit().getUnitName(), productContract.getBusinessUnit().getUnitCode()));
		}
		for (BusinessUnitPojo unit : businessUnitData) {
			boolean isUnitExist = false;
			if (productContract.getBusinessUnit() != null && productContract.getBusinessUnit().getId().equals(unit.getId())) {
				isUnitExist = true;
			}
			if (!isUnitExist) {
				businessUnitList.add(unit);
			}
		}
		model.addAttribute("businessUnit", businessUnitList);

		List<SupplierPojo> supplierListData = favoriteSupplierService.searchFavouriteSupplier(SecurityLibrary.getLoggedInUserTenantId(), null, null);
		model.addAttribute("favSupp", supplierListData);
		List<GroupCode> groupCodeList = groupCodeService.fetchAllGroupCodesForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		model.addAttribute("groupCodeList", groupCodeList);
		model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("agreementTypeList", agreementTypeService.getAllActiveAgreementType(SecurityLibrary.getLoggedInUserTenantId()));

		EventPermissions eventPermissions = productContractService.getUserPemissionsForContract(SecurityLibrary.getLoggedInUser().getId(), id);
		model.addAttribute("eventPermissions", eventPermissions);

		List<ProductItemPojo> productItemList = productContractItemsService.findProductContractItems(id);
		List<ProductItemPojo> list = productListMaintenanceService.fetchAllProductItemForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		if (CollectionUtil.isNotEmpty(list)) {
			for (ProductItemPojo pojo : list) {
				if (!productItemList.contains(pojo)) {
					productItemList.add(pojo);
				}
			}
		}
		model.addAttribute("productItemList", productItemList);
		List<ProductCategoryPojo> itemCategories = productContractItemsService.findProductCategories(id);
		List<ProductCategoryPojo> catList = productCategoryMaintenanceService.fetchAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), null);
		if (CollectionUtil.isNotEmpty(itemCategories)) {
			for (ProductCategoryPojo pojo : itemCategories) {
				if (!catList.contains(pojo)) {
					catList.add(pojo);
				}
			}
		}
		model.addAttribute("productCategoryList", catList);
		List<Uom> uomList = uomService.getAllActiveUomForTenant(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("uomList", uomList);

		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		if (enableCorrelation != null && enableCorrelation == Boolean.TRUE) {
			List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, productContract.getBusinessUnit().getId());
			model.addAttribute("costCenterList", costCenterList);
		} else {
			List<CostCenterPojo> costCenterList = costCenterService.fetchAllCostCenterForTenant(SecurityLibrary.getLoggedInUserTenantId(), null, null);
			model.addAttribute("costCenterList", costCenterList);
		}

		Date newDate = DateUtil.formatDateToStartTime(new Date());
		Boolean contractExpire = false;
		if (productContract.getStatus() == ContractStatus.ACTIVE && productContract.getContractEndDate().before(newDate)) {
			contractExpire = true;
		}
		model.addAttribute("contractExpire", contractExpire);

		return new ModelAndView("productContractItemList", "productContract", productContract);
	}

	@RequestMapping(path = "/addContractTeamMember", method = RequestMethod.POST)
	public ResponseEntity<List<ContractTeamMember>> addContractTeamMember(@RequestParam("contractId") String contractId, @RequestParam("userId") String userId, @RequestParam("memberType") TeamMemberType memberType, HttpSession session) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("addTeamMemberToList:  " + " contractId: " + contractId + " userId: " + userId);
		List<ContractTeamMember> teamMembers = null;
		try {
			if (userId != null) {
				productContractService.addTeamMemberToList(contractId, userId, memberType, session, SecurityLibrary.getLoggedInUser(), null);
				teamMembers = productContractService.getPlainTeamMembersForContract(contractId);
			} else {
				headers.add("error", "Please Select TeamMember Users");
				LOG.error("Please Select TeamMember Users");
				return new ResponseEntity<List<ContractTeamMember>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error While adding TeamMember users : " + e.getMessage(), e);
			headers.add("error", "Please Select TeamMember Users");
			return new ResponseEntity<List<ContractTeamMember>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOG.info(teamMembers.size() + "..................." + teamMembers);
		return new ResponseEntity<List<ContractTeamMember>>(teamMembers, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/removeContractTeamMember", method = RequestMethod.POST)
	public ResponseEntity<List<ContractTeamMember>> removeContractTeamMember(@RequestParam("contractId") String contractId, @RequestParam("userId") String userId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("removeContractTeamMember:  " + " contractId: " + contractId + " userId: " + userId);
		List<ContractTeamMember> teamMembers = null;
		try {
			if (userId != null) {
				productContractService.removeTeamMemberfromList(contractId, userId, SecurityLibrary.getLoggedInUser());
				teamMembers = productContractService.getPlainTeamMembersForContract(contractId);
			} else {
				headers.add("error", "Please Select TeamMember Users");
				LOG.error("Please Select TeamMember Users");
				return new ResponseEntity<List<ContractTeamMember>>(teamMembers, headers, HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			LOG.error("Error While removing Team Member user : " + e.getMessage(), e);
			headers.add("error", "Error While removing Team Member user : " + e.getMessage());
			return new ResponseEntity<List<ContractTeamMember>>(teamMembers, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOG.info(teamMembers.size() + "..................." + teamMembers);
		return new ResponseEntity<List<ContractTeamMember>>(teamMembers, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/cancelContract", method = RequestMethod.GET)
	public String cancelContract(@RequestParam("contractId") String contractId, @RequestParam(required = false) String cancelReason, RedirectAttributes redir) {
		try {
			ProductContract contract = productContractService.getProductContractById(contractId);
			contract.setStatus(ContractStatus.CANCELLED);
			contract.setCancelReason(cancelReason);
			LOG.info("Contract Id : " + contract.getContractId());
			contract = productContractService.update(contract);
			LOG.info("Contract Id After Update : " + contract.getContractId());
			if (StringUtils.checkString(contract.getContractId()).length() > 0) {
				redir.addFlashAttribute("success", messageSource.getMessage("success.contract.cancel", new Object[] { StringUtils.checkString(contract.getContractId()).length() > 0 ? contract.getContractId() : StringUtils.checkString(contract.getContractName()) }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("success", messageSource.getMessage("success.contract.cancel.without.id", new Object[] {}, Global.LOCALE));
			}
			try {
				String remark = "Contract \"" + (StringUtils.checkString(contract.getContractId()).length() > 0 ? contract.getContractId() : StringUtils.checkString(contract.getContractName())) + "\" is Cancelled";
				ContractAudit audit = new ContractAudit(contract, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Cancel, remark);
				contractAuditService.save(audit);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CANCELLED, "Contract \"" + contract.getContractId() + "\" is Cancelled", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ContractList);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while Store Audit : " + e.getMessage(), e);
			}

		} catch (Exception e) {
			LOG.error("Error While Cancel Contract :" + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error during Cancel Contract operation : " + e.getMessage());
		}
		return "redirect:productContractList";
	}
}
