package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.Budget;
import com.privasia.procurehere.core.entity.BudgetApproval;
import com.privasia.procurehere.core.entity.BudgetApprovalUser;
import com.privasia.procurehere.core.entity.BudgetDocument;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.IdSettings;
import com.privasia.procurehere.core.entity.TransactionLog;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.BudgetStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TransactionLogStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SecurityRuntimeException;
import com.privasia.procurehere.core.pojo.BudgetPermissions;
import com.privasia.procurehere.core.pojo.BudgetPojo;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BudgetService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.TransactionLogService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.BudgetApprovalEditor;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import com.privasia.procurehere.web.editors.CostCenterEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.UserEditor;

/**
 * @author Shubham
 */

@Controller
@RequestMapping("/admin/budgets")
public class BudgetController implements Serializable {

	private static final long serialVersionUID = 5441392170950896160L;

	private static final Logger LOG = LogManager.getLogger(Global.BUDGET_PLANNER);

	@Autowired
	UserService userService;
	@Autowired
	BudgetService budgetService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	TransactionLogService transactionLogService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	ApprovalService approvalService;

	@Resource
	MessageSource messageSource;

	@Autowired
	BudgetApprovalEditor budgetApprovalEditor;

	@Autowired
	CostCenterEditor costCenterEditor;

	@Autowired
	BusinessUnitEditor businessUnitEditor;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	UserEditor userEditor;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	ServletContext context;
	
	@Autowired
	EventIdSettingsService eventIdSettingsService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@InitBinder
	public void InitBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(CostCenter.class, costCenterEditor);
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		binder.registerCustomEditor(BudgetApprovalUser.class, budgetApprovalEditor);
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
		timeFormat.setTimeZone(timeZone);

		binder.registerCustomEditor(Date.class, "validTo", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "validFrom", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "createdDate", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "modifiedDate", new CustomDateEditor(timeFormat, true));
	}

	@RequestMapping(path = "/budgetSummary", method = RequestMethod.GET)
	public String budgetSummary(Model model) throws JsonProcessingException {
		return "budgetSummary";
	}

	@ModelAttribute("budgetStatus")
	public List<BudgetStatus> budgetStatus() {
		List<BudgetStatus> budgetStatus = new ArrayList<>(Arrays.asList(BudgetStatus.values()));
		budgetStatus.remove(BudgetStatus.NEW);
		return budgetStatus;
	}

	@RequestMapping(path = "/budgetDashboard", method = RequestMethod.GET)
	public String budgetDashboard(Model model) throws JsonProcessingException {
		model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		return "budgetDashboard";
	}

	/*
	 * @RequestMapping(path = "/budgetDashboard/{businessUnitId}", method = RequestMethod.GET) public String
	 * springMVC(@PathVariable String businessUnitId, ModelMap modelMap) { Budget budget =
	 * budgetService.findBudgetByBusinessUnit(businessUnitId); List<Map<Object, Object>> budgetDataList=new
	 * ArrayList<Map<Object,Object>>(); budgetDataList.add("","") modelMap.addAttribute("dataPointsList",
	 * budgetDataList); return "chart"; }
	 */

	@RequestMapping(path = "/listBudget", method = RequestMethod.GET)
	public String manageBudget(Model model, RedirectAttributes redirectAttributes) throws JsonProcessingException {
		List<BudgetStatus> list = new ArrayList<BudgetStatus>();
		for (BudgetStatus bs : BudgetStatus.values()) {
			if (BudgetStatus.NEW != bs) {
				list.add(bs);
			}
		}
		model.addAttribute("budgetStatusList", list);
		return "manageBudget";
	}

	@ModelAttribute("txStatusList")
	public List<TransactionLogStatus> TransactionLogStatus() {
		return Arrays.asList(TransactionLogStatus.values());
	}

	@RequestMapping(path = "/transactionLogs", method = RequestMethod.GET)
	public String getTransactionLogs(Model model) throws JsonProcessingException {
		return "transactionLogs";
	}

	@RequestMapping(value = "/downloadBudgetDocument/{docId}", method = RequestMethod.GET)
	public void downloadBudgetFile(@PathVariable String docId, HttpServletResponse response) throws IOException {
		try {
			BudgetDocument docs = budgetService.findBudgetDocById(docId);
			response.setContentType(docs.getContentType());
			response.setContentLength(docs.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
			FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while downloading Budget Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/createBudget", method = RequestMethod.GET)
	public ModelAndView createBudget(Model model) throws JsonProcessingException {
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

		BudgetPojo budgetPojo = new BudgetPojo();
		try {
			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (null != buyerSettings.getCurrency()) {
				budgetPojo.setBaseCurrency(buyerSettings.getCurrency());
			}
			model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());

			BudgetPermissions permissions = new BudgetPermissions();
			model.addAttribute("permissions", permissions);

		} catch (SecurityRuntimeException e) {
			LOG.error(e.getMessage(), e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		model.addAttribute("changeBudget", new BudgetPojo());
		model.addAttribute("userList", approvalUserList);
		model.addAttribute("budgetApprovalUserList", approvalUserList);
		model.addAttribute("baseCurrencyList", currencyService.getAllCurrency());
		model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isEnableUnitAndCostCorrelation", enableCorrelation);
	
		return new ModelAndView("createBudget", "budgetPojo", budgetPojo);
	}

	@RequestMapping(path = "/editBudget/{id}", method = RequestMethod.GET)
	public ModelAndView editBudget(@PathVariable("id") String id, Model model, HttpSession session) throws JsonProcessingException {
		List<User> approvalUserList = new ArrayList<User>();
		List<User> allUserList = new ArrayList<User>();
		List<UserPojo> allAppUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
		if (CollectionUtil.isNotEmpty(allAppUserList)) {
			for (UserPojo user : allAppUserList) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!allUserList.contains(u)) {
					allUserList.add(u);
				}
			}
		}

		BudgetPojo budgetPojo = null;
		BudgetPermissions permissions = new BudgetPermissions();
		try {
			budgetPojo = budgetService.findBudgetById(id);

			if (budgetPojo != null) {
				if (CollectionUtil.isNotEmpty(budgetPojo.getApprovals())) {
					for (BudgetApproval approval : budgetPojo.getApprovals()) {
						if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
							for (BudgetApprovalUser user : approval.getApprovalUsers()) {
								if (!approvalUserList.contains(user.getUser())) {
									approvalUserList.add(user.getUser());
								}
							}
						}
					}

				}
				List<UserPojo> appuserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
				if (CollectionUtil.isNotEmpty(appuserList)) {
					for (UserPojo user : appuserList) {
						User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
						if (!approvalUserList.contains(u)) {
							approvalUserList.add(u);
						}
					}
				}
			}
			if (SecurityLibrary.getLoggedInUser().getId().equals(budgetPojo.getCreatedBy().getId())) {
				permissions.setCreator(true);
			}
			if (BudgetStatus.APPROVED.equals(budgetPojo.getBudgetStatus()) || BudgetStatus.ACTIVE.equals(budgetPojo.getBudgetStatus())) {
				permissions.setModifier(true);
				permissions.setDisabled(true);
				permissions.setChangeBudget(true);
			} else if (BudgetStatus.PENDING.equals(budgetPojo.getBudgetStatus())) {
				permissions.setDisabled(true);
			} else if (BudgetStatus.EXPIRED.equals(budgetPojo.getBudgetStatus())) {
				permissions.setDisabled(true);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		model.addAttribute("permissions", permissions);

		model.addAttribute("userList", approvalUserList);
		model.addAttribute("budgetApprovalUserList", allUserList);
		model.addAttribute("baseCurrencyList", currencyService.getAllCurrency());
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isEnableUnitAndCostCorrelation", enableCorrelation);
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		
		if (budgetPojo.getBusinessUnit() != null && enableCorrelation == Boolean.TRUE) {
			if (budgetPojo.getBusinessUnit().getStatus() == Status.ACTIVE) {
				List<CostCenterPojo> assignedCostList = businessUnitService.getCostCentersByBusinessUnitId(budgetPojo.getBusinessUnit().getId(), Status.ACTIVE);
//				List<String> assignedCostId = costCenterService.getCostCenterByBusinessId(budgetPojo.getBusinessUnit().getId());
//				if (CollectionUtil.isNotEmpty(assignedCostId)) {
//					for (String assignedCost : assignedCostId) {
//						CostCenterPojo cost = costCenterService.getCostCenterByCostId(assignedCost);
//						if (cost.getStatus() == Status.ACTIVE) {
//							assignedCostList.add(cost);
//						}
//					}
//				}
				model.addAttribute("costCenterList", assignedCostList);
			} else {
				model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			}
		} else {
			model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		}
		
//		model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		IdSettings idSettings = eventIdSettingsService.getIdSettingsByIdTypeForTenanatId(SecurityLibrary.getLoggedInUserTenantId(), "BG");
		model.addAttribute("idSettings", idSettings);
		
		return new ModelAndView("createBudget", "budgetPojo", budgetPojo);
	}

	@RequestMapping(path = "/budgetSummary/{id}", method = RequestMethod.GET)
	public ModelAndView viewBudget(@PathVariable("id") String id, Model model, HttpSession session) throws JsonProcessingException {
		List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<BudgetApprovalUser> budgetApprovalUserList = new ArrayList<BudgetApprovalUser>();
		LOG.info("budgetApprovalUserList............." + budgetApprovalUserList.size());
		for (User user : userList) {
			budgetApprovalUserList.add(new BudgetApprovalUser(user));
		}
		BudgetPojo budgetPojo = budgetService.findBudgetById(id);

		if (null != budgetPojo.getConversionRate()) {
			Budget budget = budgetService.findTransferToBudgetById(id);
			budgetPojo.setToBusinessUnit(budget.getToBusinessUnit());
			budgetPojo.setToCostCenter(budget.getToCostCenter());
		}

		model.addAttribute("userList", userList);
		model.addAttribute("budgetApprovalUserList", budgetApprovalUserList);
		model.addAttribute("baseCurrencyList", currencyService.getAllCurrency());
		model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		return new ModelAndView("budgetSummary", "budget", budgetPojo);
	}

	@RequestMapping(path = "/viewBudget/{id}", method = RequestMethod.GET)
	public ModelAndView viewBudgetSummary(@PathVariable("id") String id, Model model, HttpSession session) throws JsonProcessingException {
		List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<User> allUserList = new ArrayList<User>();
		List<UserPojo> allAppUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
		if (CollectionUtil.isNotEmpty(allAppUserList)) {
			for (UserPojo user : allAppUserList) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!allUserList.contains(u)) {
					allUserList.add(u);
				}
			}
		}

		BudgetPojo budgetPojo = budgetService.findBudgetById(id);

		BudgetPermissions permissions = new BudgetPermissions();

		if (SecurityLibrary.getLoggedInUser().getId().equals(budgetPojo.getCreatedBy().getId())) {
			permissions.setCreator(true);
		}
		if (BudgetStatus.APPROVED.equals(budgetPojo.getBudgetStatus()) || BudgetStatus.ACTIVE.equals(budgetPojo.getBudgetStatus())) {
			permissions.setModifier(true);
			permissions.setDisabled(true);
			permissions.setChangeBudget(true);
		}
		if (BudgetStatus.EXPIRED.equals(budgetPojo.getBudgetStatus())) {
			permissions.setDisabled(true);
		}

		BudgetPojo changeBudgetPojo = new BudgetPojo();
		changeBudgetPojo.setId(budgetPojo.getId());
		model.addAttribute("userList", userList);
		model.addAttribute("budgetApprovalUserList", allUserList);
		model.addAttribute("baseCurrencyList", currencyService.getAllCurrency());
		model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("changeBudget", changeBudgetPojo);
		model.addAttribute("permissions", permissions);
		return new ModelAndView("viewBudget", "budget", budgetPojo);
	}

	@RequestMapping(path = "/budget", method = RequestMethod.POST)
	public String saveBudget(@ModelAttribute("budgetPojo") BudgetPojo budgetPojo, BindingResult result, Model model, HttpSession session, RedirectAttributes redir) {

		if (result.hasErrors()) {

			LOG.error("budget files binding ....................." + result.getFieldError());
			model.addAttribute("error", messageSource.getMessage("budget.create.error", new Object[] {}, Global.LOCALE));
			return "createBudget";
		}

		if (null != budgetPojo.getBudgetFilesArr()) {
			budgetPojo.setBudgetFiles(Arrays.asList(budgetPojo.getBudgetFilesArr()));
		}

		Boolean exists = Boolean.FALSE;
		// only for new budget check already exists or not
		if (StringUtils.checkString(budgetPojo.getId()).length() == 0) {
			exists = budgetService.isCombinationOfBuAndCcExists(budgetPojo);
		} /*
			 * else if(BudgetStatus.DRAFT != budgetPojo.getBudgetStatus() && BudgetStatus.NEW !=
			 * budgetPojo.getBudgetStatus()) { return "redirect:/admin/budgets/listBudget"; }
			 */
		if (exists) {
			LOG.error("budget already exists....");
			model.addAttribute("error", messageSource.getMessage("budget.already.exists", new Object[] { budgetPojo.getBusinessUnit().getUnitName(), budgetPojo.getCostCenter().getCostCenter() }, Global.LOCALE));
			List<User> approvalUserList = new ArrayList<User>();
			List<User> allUserList = new ArrayList<User>();
			List<UserPojo> allAppUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
			if (CollectionUtil.isNotEmpty(allAppUserList)) {
				for (UserPojo user : allAppUserList) {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					if (!allUserList.contains(u)) {
						allUserList.add(u);
					}
				}
			}

			if (budgetPojo != null) {
				if (CollectionUtil.isNotEmpty(budgetPojo.getApprovals())) {
					for (BudgetApproval approval : budgetPojo.getApprovals()) {
						for (BudgetApprovalUser user : approval.getApprovalUsers()) {
							if (!approvalUserList.contains(user.getUser())) {
								approvalUserList.add(user.getUser());
							}
						}
					}
				}

			}
			List<UserPojo> appuserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
			if (CollectionUtil.isNotEmpty(appuserList)) {
				for (UserPojo user : appuserList) {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					if (!approvalUserList.contains(u)) {
						approvalUserList.add(u);
					}
				}
			}

			try {
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (null != buyerSettings.getCurrency()) {
					budgetPojo.setBaseCurrency(buyerSettings.getCurrency());
				}
				model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());

				BudgetPermissions permissions = new BudgetPermissions();
				model.addAttribute("permissions", permissions);

			} catch (SecurityRuntimeException e) {
				LOG.error(e.getMessage(), e);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			model.addAttribute("userList", approvalUserList);
			model.addAttribute("budgetApprovalUserList", allUserList);
			model.addAttribute("baseCurrencyList", currencyService.getAllCurrency());
			model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			model.addAttribute("budgetPojo", budgetPojo);
			return "createBudget";
		}

		try

		{

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy");
			timeFormat.setTimeZone(timeZone);
			Date validFromDate = null;
			Date validToDate = null;
			Date budgetDuration = null;

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			if (budgetPojo.getValidFrom() != null) {
				try {
					String validFrom = timeFormat.format(budgetPojo.getValidFrom());
					validFromDate = sdf.parse(validFrom);
				} catch (Exception e) {
					LOG.error("Error while parsing:" + e.getMessage());

				}
				validFromDate = getDate(validFromDate);
				budgetDuration = getOneYearDate(validFromDate);
			}

			if (budgetPojo.getValidTo() != null) {
				try {
					String validTo = timeFormat.format(budgetPojo.getValidTo());
					validToDate = sdf.parse(validTo);
				} catch (Exception e) {
					LOG.error("Error while parsing:" + e.getMessage());

				}
				validToDate = getDate(validToDate);
			}

			Date now = new Date();
			now = getDate(now);

			if (StringUtils.checkString(budgetPojo.getId()).length() == 0 || (budgetPojo.getBudgetStatus() != null || budgetPojo.getBudgetStatus() == BudgetStatus.NEW || budgetPojo.getBudgetStatus() == BudgetStatus.DRAFT || budgetPojo.getBudgetStatus() == BudgetStatus.REJECTED)) {
				LOG.info("checking budget dates:" + budgetDuration);
				if (validFromDate != null && validFromDate.before(now) && validToDate != null && validToDate.before(now)) {
					LOG.info("checking budget dates:");
					throw new ApplicationException(messageSource.getMessage("budget.error.validFrom.validTo", new Object[] { timeFormat.format(budgetPojo.getValidFrom()), timeFormat.format(budgetPojo.getValidTo()) }, Global.LOCALE));
				}
				if (validFromDate != null && validFromDate.before(now)) {
					LOG.info("checking budget dates:");
					throw new ApplicationException(messageSource.getMessage("budget.error.validFrom", new Object[] { timeFormat.format(budgetPojo.getValidFrom()) }, Global.LOCALE));
				}
				if (validToDate != null && validToDate.before(validFromDate) || validToDate.equals(validFromDate)) {
					LOG.info("checking budget dates:");
					throw new ApplicationException(messageSource.getMessage("budget.error.validTo", new Object[] { timeFormat.format(budgetPojo.getValidTo()) }, Global.LOCALE));
				}
				if (validToDate != null && validToDate.before(now)) {
					LOG.info("checking budget dates:");
					throw new ApplicationException(messageSource.getMessage("budget.error.past.validTo", new Object[] { timeFormat.format(budgetPojo.getValidTo()) }, Global.LOCALE));
				}
				if (validToDate != null && budgetDuration != null && validToDate.after(budgetDuration)) {
					throw new ApplicationException(messageSource.getMessage("budget.error.budgetDuration", new Object[] {}, Global.LOCALE));

				}
			}
			Budget budget = new Budget();
			budget.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
			budget.setBudgetStatus(BudgetStatus.NEW);
			budget.setBudgetOwner(SecurityLibrary.getLoggedInUser());
			budget.setCreatedDate(new Date());
			budget.setCreatedBy(SecurityLibrary.getLoggedInUser());
			budget.setModifiedDate(new Date());
			budget.setModifiedBy(SecurityLibrary.getLoggedInUser());
			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (null != buyerSettings.getCurrency()) {
				budgetPojo.setBaseCurrency(buyerSettings.getCurrency());
			}
			Budget savedBudget = budgetService.saveBudget(budget, budgetPojo);

			try {
				// save audit trail
				BuyerAuditTrail audit = new BuyerAuditTrail();
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
				audit.setModuleType(ModuleType.BudgetPlanner);
				if (StringUtils.checkString(budgetPojo.getId()).length() > 0) {
					audit.setActivity(AuditTypes.UPDATE);
					audit.setDescription("'" + savedBudget.getBudgetId() + "' Budget is updated");
				} else {
					audit.setActivity(AuditTypes.CREATE);
					audit.setDescription("'" + savedBudget.getBudgetId() + "' Budget is created");
				}
				budgetService.saveBuyerAuditTrail(audit);
			} catch (Exception e) {
				LOG.error("error while saving audit for budget...." + e.getMessage(), e);
			}

			if (null != savedBudget && StringUtils.checkString(budgetPojo.getId()).length() == 0) {
				redir.addFlashAttribute("success", messageSource.getMessage("budget.create.success", new Object[] { savedBudget.getBudgetName() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("success", messageSource.getMessage("budget.update.success", new Object[] { savedBudget.getBudgetName() }, Global.LOCALE));
			}
			TransactionLog transactionLog = new TransactionLog();
			Budget newBudget = new Budget();
			newBudget.setId(savedBudget.getId());
			transactionLog.setBudget(newBudget);
			if (StringUtils.checkString(budgetPojo.getId()).length() == 0) {
				transactionLog.setNewAmount(savedBudget.getTotalAmount());
			}
			transactionLog.setReferanceNumber(savedBudget.getBudgetId());
			transactionLog.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
			transactionLog.setTransactionTimeStamp(new Date());
			transactionLogService.saveTransactionLog(transactionLog);
		} catch (ApplicationException e) {
			LOG.error("error while saving...." + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			List<User> approvalUserList = new ArrayList<User>();
			List<User> allUserList = new ArrayList<User>();
			List<UserPojo> allAppUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
			if (CollectionUtil.isNotEmpty(allAppUserList)) {
				for (UserPojo user : allAppUserList) {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					if (!allUserList.contains(u)) {
						allUserList.add(u);
					}
				}
			}
			if (budgetPojo != null) {
				if (CollectionUtil.isNotEmpty(budgetPojo.getApprovals())) {
					for (BudgetApproval approval : budgetPojo.getApprovals()) {
						for (BudgetApprovalUser user : approval.getApprovalUsers()) {
							if (!approvalUserList.contains(user.getUser())) {
								approvalUserList.add(user.getUser());
							}
						}
					}
				}

			}
			List<UserPojo> appuserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
			if (CollectionUtil.isNotEmpty(appuserList)) {
				for (UserPojo user : appuserList) {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					if (!approvalUserList.contains(u)) {
						approvalUserList.add(u);
					}
				}
			}

			try {
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (null != buyerSettings.getCurrency()) {
					budgetPojo.setBaseCurrency(buyerSettings.getCurrency());
				}
				model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
				BudgetPermissions permissions = new BudgetPermissions();
				model.addAttribute("permissions", permissions);

			} catch (SecurityRuntimeException e1) {
				LOG.error(e.getMessage(), e1);
			} catch (Exception e2) {
				LOG.error(e.getMessage(), e2);
			}
			model.addAttribute("userList", approvalUserList);
			model.addAttribute("budgetApprovalUserList", allUserList);
			model.addAttribute("baseCurrencyList", currencyService.getAllCurrency());
			model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			if (StringUtils.checkString(budgetPojo.getId()).length() == 0) {
				model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			} else {
				model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
			}
			model.addAttribute("budgetPojo", budgetPojo);
			return "createBudget";

		} catch (Exception e) {
			LOG.error("error while saving...." + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());

			List<User> allUserList = new ArrayList<User>();
			List<User> approvalUserList = new ArrayList<User>();

			List<UserPojo> allAppUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
			if (CollectionUtil.isNotEmpty(allAppUserList)) {
				for (UserPojo user : allAppUserList) {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					if (!allUserList.contains(u)) {
						allUserList.add(u);
					}
				}
			}
			if (budgetPojo != null) {
				if (CollectionUtil.isNotEmpty(budgetPojo.getApprovals())) {
					for (BudgetApproval approval : budgetPojo.getApprovals()) {
						for (BudgetApprovalUser user : approval.getApprovalUsers()) {
							if (!approvalUserList.contains(user.getUser())) {
								approvalUserList.add(user.getUser());
							}
						}
					}
				}

			}
			List<UserPojo> appuserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
			if (CollectionUtil.isNotEmpty(appuserList)) {
				for (UserPojo user : appuserList) {
					User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
					if (!approvalUserList.contains(u)) {
						approvalUserList.add(u);
					}
				}
			}

			try {
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (null != buyerSettings.getCurrency()) {
					budgetPojo.setBaseCurrency(buyerSettings.getCurrency());
				}
				model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
				BudgetPermissions permissions = new BudgetPermissions();
				model.addAttribute("permissions", permissions);

			} catch (SecurityRuntimeException e1) {
				LOG.error(e.getMessage(), e1);
			} catch (Exception e2) {
				LOG.error(e.getMessage(), e2);
			}
			model.addAttribute("userList", approvalUserList);
			model.addAttribute("budgetApprovalUserList", allUserList);
			model.addAttribute("baseCurrencyList", currencyService.getAllCurrency());
			model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			if (StringUtils.checkString(budgetPojo.getId()).length() == 0) {
				model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			} else {
				model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
			}
			model.addAttribute("budgetPojo", budgetPojo);
			return "createBudget";

		}
		return "redirect:/admin/budgets/listBudget";
	}

	private Date getOneYearDate(Date validFromDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(validFromDate);
		int year = cal.get(Calendar.YEAR);
		cal.set(Calendar.YEAR, year + 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private Date getDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	@RequestMapping(path = "/changeBudget", method = RequestMethod.POST)
	public String changeBudget(@ModelAttribute("changeBudget") BudgetPojo budgetPojo, BindingResult result, Model model, HttpSession session, RedirectAttributes redir) {

		LOG.info("***************budget id  " + budgetPojo.getId());
		Budget budget = budgetService.findById(budgetPojo.getId());
		budget.setModifiedBy(SecurityLibrary.getLoggedInUser());
		budget.setModifiedDate(new Date());
		budget.setRevisionDate(new Date());
		try {
			// Add amount
			if (budgetPojo.getAddAmount() != null) {
				// if budget has additional approvers
				if (CollectionUtil.isNotEmpty(budgetPojo.getApprovals())) {
					// Additional approvers
					budgetService.addAdditionalApprovers(budgetPojo, budget.getId());

					budget.setRevisionAmount(budget.getTotalAmount().add(budgetPojo.getAddAmount()));
					budget.setRevisionJustification(budgetPojo.getAddRevisionJustification());
					budget.setBudgetStatus(BudgetStatus.PENDING);
					Budget savedBudget = budgetService.updateBudget(budget);
					redir.addFlashAttribute("success", messageSource.getMessage("budget.update.success", new Object[] { savedBudget.getBudgetName() }, Global.LOCALE));
				}
			} else if (budgetPojo.getDeductAmount() != null && budgetPojo.getToBusinessUnit() == null) {
				// deduct amount
				if (((1 == (budget.getTotalAmount().compareTo(budgetPojo.getDeductAmount()))) || (0 == (budget.getTotalAmount().compareTo(budgetPojo.getDeductAmount()))))) {
					LOG.info("deduct amount inside ***** ===========> " + budgetPojo.getDeductAmount());
					// if budget has additional approvers
					if (CollectionUtil.isNotEmpty(budgetPojo.getApprovals())) {
						// Additional approvers
						budgetService.addAdditionalApprovers(budgetPojo, budget.getId());
						budget.setRevisionAmount(budget.getTotalAmount().subtract(budgetPojo.getDeductAmount()));
						budget.setRevisionJustification(budgetPojo.getDeductRevisionJustification());
						budget.setBudgetStatus(BudgetStatus.PENDING);
						Budget savedBudget = budgetService.updateBudget(budget);
						LOG.info("budget Status inside additional approval after save============> " + savedBudget.getBudgetStatus());
						redir.addFlashAttribute("success", messageSource.getMessage("budget.update.success", new Object[] { savedBudget.getBudgetName() }, Global.LOCALE));
					}
				} else {
					LOG.info("Not able to deduct");
					redir.addFlashAttribute("error", messageSource.getMessage("budget.update.deduct.error", new Object[] { budget.getBudgetName() }, Global.LOCALE));
					return "redirect:/admin/budgets/listBudget";

				}

			} else if (budgetPojo.getTransferAmount() != null && budgetPojo.getToBusinessUnit() != null && budgetPojo.getToCostCenter() != null) {
				// transfer amount
				if ((1 == budget.getRemainingAmount().compareTo(budgetPojo.getTransferAmount())) || (0 == budget.getRemainingAmount().compareTo(budgetPojo.getTransferAmount()))) {
					Budget toBudget = budgetService.findBudgetByBusinessUnitAndCostCenter(budgetPojo.getToBusinessUnit().getId(), budgetPojo.getToCostCenter().getId());
					if (toBudget == null) {
						redir.addFlashAttribute("error", "Error in transaction, Budget is not Active.");
						return "redirect:/admin/budgets/listBudget";
					}

					if (BudgetStatus.ACTIVE != toBudget.getBudgetStatus()) {
						redir.addFlashAttribute("error", "Error in transaction, Budget is not Active.");
						return "redirect:/admin/budgets/listBudget";
					}
					if (budgetPojo.getConversionRate() == null && (!budget.getBaseCurrency().getCurrencyCode().equals(toBudget.getBaseCurrency().getCurrencyCode()))) {
						redir.addFlashAttribute("error", "Error in transaction, Conversion rate is required as to budget currency is different than from budget currency.");
						return "redirect:/admin/budgets/listBudget";

					}
					if (budgetPojo.getConversionRate() != null) {
						if (1 == BigDecimal.ZERO.compareTo(budgetPojo.getConversionRate())) {
							redir.addFlashAttribute("error", "Error in transaction, Conversion rate should not be equal to zero or less than zero.");
							return "redirect:/admin/budgets/listBudget";
						}
					}

					// if budget has additional approvers
					if (CollectionUtil.isNotEmpty(budgetPojo.getApprovals())) {
						// Additional approvers
						budgetService.addAdditionalApprovers(budgetPojo, budget.getId());
						budget.setRevisionAmount(budget.getTotalAmount().subtract(budgetPojo.getTransferAmount()));
						budget.setRevisionJustification(budgetPojo.getTransferRevisionJustification());
						budget.setBudgetStatus(BudgetStatus.PENDING);
						if (budgetPojo.getConversionRate() != null) {
							budget.setConversionRate(budgetPojo.getConversionRate());
							LOG.info("**************************budget conversionRate " + budget.getConversionRate());
						}
						budget.setToBusinessUnit(budgetPojo.getToBusinessUnit());
						budget.setToCostCenter(budgetPojo.getToCostCenter());
						Budget savedBudget = budgetService.updateBudget(budget);
						LOG.info("budget Status inside additional approval after save============> " + savedBudget.getBudgetStatus());
						redir.addFlashAttribute("success", messageSource.getMessage("budget.update.success", new Object[] { savedBudget.getBudgetName() }, Global.LOCALE));
						return "redirect:/admin/budgets/listBudget";

					}
				} else {
					// error in transfer
					if (budgetPojo.getTransferAmount() != null) {
						redir.addFlashAttribute("error", "Error in transaction, Transfer amount should be less than Remaining amount of Budget.");
						return "redirect:/admin/budgets/listBudget";
					}
				}
			}
		} catch (NoResultException e) {
			LOG.error("error while transferring amount to budget which is not created yet...." + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error in transaction, Budget is not available for given Business unit.");
			return "redirect:/admin/budgets/listBudget";
		} catch (Exception e) {
			LOG.error("error while saving...." + e.getMessage(), e);
			redir.addAttribute("error", "Error in Budget transaction");
		}
		return "redirect:/admin/budgets/listBudget";
	}

	@RequestMapping(path = "/budgetData", method = RequestMethod.GET)
	public ResponseEntity<TableData<BudgetPojo>> budgetTemplateData(TableDataInput input) {
		TableData<BudgetPojo> data = null;
		try {
			data = new TableData<BudgetPojo>(budgetService.getAllBudgetForTenantId(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			LOG.info("***************budget query data" + data.getRecordsTotal());
			long totalFilteredCount = budgetService.findTotalBudgetForTenantId(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilteredCount);
			long totalCount = budgetService.findTotalCountBudgetForTenantId(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsTotal(totalCount);
			LOG.info(" totalCount :" + totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading budget list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching budget list :" + e.getMessage());
			return new ResponseEntity<TableData<BudgetPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		return new ResponseEntity<TableData<BudgetPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/approve", method = RequestMethod.POST)
	public String requestApproved(@RequestParam String id, @RequestParam String remarks, @RequestParam String reject, RedirectAttributes redir, HttpSession session) {
		try {
			Budget budget = new Budget();
			budget.setId(id);
			if (!reject.equals("reject")) {
				// approve
				budget = approvalService.doBudgetApproval(budget, SecurityLibrary.getLoggedInUser(), remarks, true, session);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.budget.approved", new Object[] { (budget.getBudgetName() != null ? budget.getBudgetName() : "") }, Global.LOCALE));
			} else {
				// reject
				budget = approvalService.doBudgetApproval(budget, SecurityLibrary.getLoggedInUser(), remarks, false, session);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.budget.rejected", new Object[] { (budget.getBudgetName() != null ? budget.getBudgetName() : "") }, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while approving request :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.approving.request", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/deleteDocument/{budgetId}/{docId}", method = RequestMethod.GET)
	public ModelAndView deleteDocument(@PathVariable("budgetId") String budgetId, @PathVariable("docId") String docId, Model model, HttpSession session) throws JsonProcessingException {
		budgetService.deleteDocumentById(budgetId, docId);
		return new ModelAndView("redirect:/admin/budgets/editBudget/" + budgetId);
	}

	@RequestMapping(path = "/checkBudgetSummary/{businessUnitId}/{costCenterId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, BigDecimal>> checkBudgetSummary(@PathVariable("businessUnitId") String businessUnitId, @PathVariable("costCenterId") String costCenterId, Model model, HttpSession session) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			Budget budget = budgetService.findBudgetByBusinessUnitAndCostCenter(businessUnitId, costCenterId);
			if (budget == null) {
				headers.add("error", "Budget is not created for selected Business Unit and Cost Center.");
				return new ResponseEntity<Map<String, BigDecimal>>(null, headers, HttpStatus.OK);
			}
			Map<String, BigDecimal> data = new LinkedHashMap<String, BigDecimal>();
			data.put("total", budget.getTotalAmount());
			data.put("remaining", budget.getRemainingAmount());
			data.put("pending", budget.getPendingAmount());
			data.put("approved", budget.getApprovedAmount());
			data.put("locked", budget.getLockedAmount());
			data.put("paid", budget.getPaidAmount());
			headers.add("currencyCode", budget.getBaseCurrency().getCurrencyCode());
			return new ResponseEntity<Map<String, BigDecimal>>(data, headers, HttpStatus.OK);
		} catch (Exception exception) {
			headers.add("error", "Budget is not created for selected Business Unit and Cost Center.");
			return new ResponseEntity<Map<String, BigDecimal>>(null, headers, HttpStatus.OK);
		}

	}

	@RequestMapping(path = "/cancelBudget/{id}", method = RequestMethod.GET)
	public ModelAndView cancelBudget(@PathVariable("id") String id, Model model, RedirectAttributes redir, HttpSession session) throws JsonProcessingException {
		Budget budget = budgetService.findById(id);
		budget.setBudgetStatus(BudgetStatus.CANCELED);
		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CANCELLED, "Budget '"+budget.getBudgetId()+"' is Cancelled", SecurityLibrary.getLoggedInUserTenantId(),SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.BudgetPlanner);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (SecurityRuntimeException e1) {
			LOG.info("Error in creating Audit");
		}
		budgetService.updateBudget(budget);
		redir.addFlashAttribute("success", messageSource.getMessage("budget.cancel.success", new Object[] { budget.getBudgetName() }, Global.LOCALE));
		return new ModelAndView("redirect:/admin/budgets/listBudget");
	}

}
