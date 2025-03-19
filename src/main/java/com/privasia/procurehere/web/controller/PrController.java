package com.privasia.procurehere.web.controller;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.Budget;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerPackage;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IdSettings;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.PaymentTermes;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrApproval;
import com.privasia.procurehere.core.entity.PrApprovalUser;
import com.privasia.procurehere.core.entity.PrAudit;
import com.privasia.procurehere.core.entity.PrTeamMember;
import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.BudgetStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.BudgetStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.PrAuditType;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.PrPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPrPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BudgetService;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.ErpIntegrationService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PaymentTermsService;
import com.privasia.procurehere.service.PrTemplateService;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import com.privasia.procurehere.web.editors.BuyerAddressEditor;
import com.privasia.procurehere.web.editors.CostCenterEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.PaymentTermesEditor;
import com.privasia.procurehere.web.editors.PrApprovalUserEditor;
import com.privasia.procurehere.web.editors.UserEditor;

import freemarker.template.Configuration;

/**
 * @author Parveen
 */
@Controller
@RequestMapping("/buyer")
public class PrController extends PrBaseController {

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	CostCenterEditor costCenterEditor;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	BuyerAddressEditor buyerAddressEditor;

	@Autowired
	UserEditor userEditor;

	@Autowired
	PrApprovalUserEditor prApprovalUserEditor;

	@Autowired
	PrTemplateService prTemplateService;

	@Autowired
	BusinessUnitEditor businessUnitEditor;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	BudgetService budgetService;

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	PaymentTermsService paymentTermesService;

	@Autowired
	PaymentTermesEditor paymentTermesEditor;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(CostCenter.class, costCenterEditor);
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		binder.registerCustomEditor(BuyerAddress.class, buyerAddressEditor);
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(PrApprovalUser.class, prApprovalUserEditor);
		binder.registerCustomEditor(List.class, "approvalUsers", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					User user = userService.findUserById(id);
					return new PrApprovalUser(user);
				}
				return null;
			}
		});
		binder.registerCustomEditor(PaymentTermes.class, paymentTermesEditor);

	}

	@ModelAttribute("step")
	public String getStep() {
		return "1";
	}

	@ModelAttribute("poStatusList")
	public List<PrStatus> getPoStatusList() {
		List<PrStatus> poStatusList = Arrays.asList(PrStatus.APPROVED, PrStatus.DELIVERED, PrStatus.TRANSFERRED, PrStatus.CANCELED);
		return poStatusList;
	}

	// This Method is not using anymore because PR can't create from blank
	@RequestMapping(path = "/prCreate", method = RequestMethod.GET)
	public String prCreate(Model model) {
		LOG.info("create Pr GET called ");
		Pr pr = null;
		try {
			pr = prService.savePr(SecurityLibrary.getLoggedInUser());
		} catch (Exception e) {
			LOG.error("Error in saving Pr " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("pr.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("pr", pr);
			super.constructPrAttributes(model);
			model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			return "createPrDetails";
		}
		return "redirect:createPrDetails/" + pr.getId();
	}

	@RequestMapping(path = "/createPrDetails/{prId}", method = RequestMethod.GET)
	public String createPrDetails(@PathVariable String prId,
								  @RequestParam(name = "eventName",required = false)String eventName,
								  @RequestParam(name = "eventDescription",required = false)String eventDescription,
								  @RequestParam(name = "budgetAmount", required = false) BigDecimal budgetAmount,

								  String planId, Model model, RedirectAttributes redir) {
		LOG.info("create Pr Details GET called ");

		HttpHeaders headers = new HttpHeaders();



		// Check subscription limit
		try {
			Buyer buyer = buyerService.findBuyerByIdWithBuyerPackage(SecurityLibrary.getLoggedInUser().getBuyer().getId());

			if (buyer != null && buyer.getBuyerPackage() != null) {
				BuyerPackage bp = buyer.getBuyerPackage();
				if (bp.getPlan() != null && bp.getPlan().getPlanType() == PlanType.PER_USER) {
					if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
						throw new SubscriptionException("Your Subscription has Expired.");
					}
				}
			}
		} catch (SubscriptionException e) {
			LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/buyerDashboard";
		}
		Pr pr = prService.getLoadedPrById(prId);
		PrTemplateField prTemplateField = new PrTemplateField();

		if (prTemplateField.getReadOnly()){
			if (pr.getName() != null) {
				eventName = pr.getName();
			}
			if (pr.getDescription() != null) {
				eventDescription = pr.getDescription();
			}

			if (pr.getAvailableBudget() != null) {
				budgetAmount = pr.getAvailableBudget();
			}
		}

		pr.setDescription(eventDescription);
		pr.setName(eventName);
		pr.setAvailableBudget(budgetAmount);

		if (Boolean.TRUE == pr.getLockBudget()) {
			try {
				// autoPopulate currency for budget if diff. conversion rate required
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (!(pr.getCurrency().getCurrencyCode().equals(buyerSettings.getCurrency().getCurrencyCode()))) {
					pr.setConversionRateRequired(Boolean.TRUE);
				}
				if (null != pr.getBusinessUnit() && null != pr.getCostCenter()) {
					Budget budget = budgetService.findBudgetByBusinessUnitAndCostCenter(pr.getBusinessUnit().getId(), pr.getCostCenter().getId());
					if (budget == null && pr.getLockBudget()) {
						redir.addFlashAttribute("error", messageSource.getMessage("err.msg.budget.not.create", new Object[] {}, Global.LOCALE));
						return "redirect:/buyer/createPr";
					}
					if (budget != null && pr.getLockBudget()) {
						LOG.info("budget.getRemainingAmount()" + budget.getRemainingAmount());
						pr.setAvailableBudget(budget.getRemainingAmount());
						pr.setBudgetCurrencyCode(budget.getBaseCurrency().getCurrencyCode());
						DecimalFormat df = new DecimalFormat("#,###.######");
						headers.add("remainingAmt", "Remaining Amount : " + df.format(budget.getRemainingAmount()) + " " + budget.getBaseCurrency().getCurrencyCode());
						redir.addFlashAttribute("remainingAmt", "Remaining Amount :" + budget.getRemainingAmount());

					} else {
						pr.setAvailableBudget(null);
						pr.setBudgetCurrencyCode(null);
					}
				}

			} catch (Exception exception) {
				LOG.error("error budget is not created and trying to create PR >>>>>>>>" + exception.getMessage());
				redir.addFlashAttribute("error", messageSource.getMessage("err.msg.budget.not.create", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/createPr";
			}
		}

		// set available budget with two decimal places
		pr.setAvailableBudget(pr.getDecimal() != null ? pr.getAvailableBudget() != null ?
				pr.getAvailableBudget().setScale(Integer.parseInt(pr.getDecimal()), RoundingMode.HALF_UP) : null : pr.getAvailableBudget());

		if (pr.getStatus() != PrStatus.DRAFT) {
			return "redirect:/buyer/prView/" + pr.getId();
		}

		super.constructPrAttributes(model);
		super.constructPrTeamAttributes(model, pr);
		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isEnableUnitAndCostCorrelation", enableCorrelation);
		if (pr.getBusinessUnit() != null && enableCorrelation == Boolean.TRUE) {
			if (pr.getBusinessUnit().getStatus() == Status.ACTIVE) {
				List<CostCenterPojo> assignedCostList = businessUnitService.getCostCentersByBusinessUnitId(pr.getBusinessUnit().getId(), Status.ACTIVE);
				model.addAttribute("costCenterList", assignedCostList);
				// List<String> assignedCostId =
				// costCenterService.getCostCenterByBusinessId(pr.getBusinessUnit().getId());
				// if (CollectionUtil.isNotEmpty(assignedCostId)) {
				// for (String assignedCost : assignedCostId) {
				// CostCenterPojo cost = costCenterService.getCostCenterByCostId(assignedCost);
				// if (cost.getStatus() == Status.ACTIVE) {
				// assignedCostList.add(cost);
				// }
				// }
				// model.addAttribute("costCenterList", assignedCostList);
				// }
			} else {
				model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			}
		} else {
			model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		}
		model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId));
		model.addAttribute("pr", pr);
		IdSettings idSetting = eventIdSettingsService.getIdSettingsByIdTypeForTenanatId(SecurityLibrary.getLoggedInUserTenantId(), "PR");
		model.addAttribute("idSetting", idSetting);

		if (pr.getTemplate() != null && pr.getTemplate().getId() != null) {
			PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(pr.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("templateFields", prTemplate.getFields());
			model.addAttribute("prTemplate", prTemplate);
			model.addAttribute("minimumApprovalCount", prTemplate.getMinimumApprovalCount());
		}

		// Restore PR name and description from session if available
		if (model.containsAttribute("prName")) {
			pr.setName((String)model.asMap().get("prName"));
		}


		return "createPrDetails";
	}

	@RequestMapping(path = "/createPrDetails", method = RequestMethod.POST)
	public ModelAndView savePr(@ModelAttribute Pr pr, Model model, RedirectAttributes redir, boolean isDraft) {
		LOG.info("create Pr Details POST called and getId :" + pr.getId() + "isDraft " + isDraft);

		LOG.info("*************************************** lock budget " + pr.getLockBudget());


		try {

			if (pr.getTemplateId() != null) {
				PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(pr.getTemplateId(), SecurityLibrary.getLoggedInUserTenantId());
				Integer minimumApprovalCount = prTemplate.getMinimumApprovalCount();
				minimumApprovalCount = (minimumApprovalCount != null) ? minimumApprovalCount : 0;


				if (minimumApprovalCount > 0 && (CollectionUtil.isEmpty(pr.getPrApprovals()) || pr.getPrApprovals().size() < minimumApprovalCount)) {
					throw new ApplicationException(
							messageSource.getMessage(
									"pr.error.minimum.approval.count",
									new Object[]{minimumApprovalCount},
									Global.LOCALE
							)
					);
				}


			}

			model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), pr.getId()));
			if (!isDraft && pr.getCorrespondenceAddress() == null) {
				LOG.error("Error in saving Pr correspondence address required ");
				model.addAttribute("error", messageSource.getMessage("pr.correspondenceAddress.error", new Object[] {}, Global.LOCALE));
				super.constructPrAttributes(model);
				model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
				super.constructPrTeamAttributes(model, pr);
				return new ModelAndView("createPrDetails", "pr", pr);
			}

			if (pr.getPaymentTermes() != null) {
				pr.setPaymentTerm(pr.getPaymentTermes().getPaymentTermCode() + " - " + pr.getPaymentTermes().getDescription());
				pr.setPaymentTermDays(pr.getPaymentTermes().getPaymentDays());
				pr.setPaymentTermes(pr.getPaymentTermes());
			}

			if (!isDraft && super.validatePr(pr, model, Pr.PrCreate.class)) {
				Pr persistObj = prService.getLoadedPrById(pr.getId());
				super.constructPrAttributes(model);
				super.constructPrTeamAttributes(model, pr);
				if (pr.getTemplateId() != null) {
					PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(pr.getTemplateId(), SecurityLibrary.getLoggedInUserTenantId());
					model.addAttribute("templateFields", prTemplate.getFields());
					model.addAttribute("prTemplate", prTemplate);
				}
				return new ModelAndView("createPrDetails", "pr", persistObj);
			}
			if (StringUtils.checkString(pr.getId()).length() > 0) {
				// budget checking
				if (Boolean.TRUE == pr.getLockBudget()) {
					if (null != pr.getBusinessUnit() && null != pr.getCostCenter()) {
						LOG.info("business Unit:" + pr.getBusinessUnit().getId() + "cost Center:" + pr.getCostCenter().getId());
						Budget budget = budgetService.findBudgetByBusinessUnitAndCostCenter(StringUtils.checkString(pr.getBusinessUnit().getId()), StringUtils.checkString(pr.getCostCenter().getId()));
						if (budget != null) {
							if (budget.getBudgetStatus() == BudgetStatus.APPROVED || budget.getBudgetStatus() == BudgetStatus.ACTIVE) {
								LOG.info("Budget is approved or active");
							} else {

								pr.setAvailableBudget(null);
								pr.setBudgetCurrencyCode(null);
								// PR cant be made if budget is not ACTIVE or APPROVED
								LOG.info("Budget is not  approved or active");
								throw new ApplicationException(messageSource.getMessage("budget.pr.create.bu.notActive.error", new Object[] {}, Global.LOCALE));
							}
						} else {
							LOG.info("Budget not created");
							pr.setAvailableBudget(null);
							pr.setBudgetCurrencyCode(null);
							throw new ApplicationException(messageSource.getMessage("budget.pr.create.bu.error", new Object[] {}, Global.LOCALE));
						}

					}
				}

				//PH-3261

				pr.setName(prService.replaceSmartQuotes(new String(pr.getName().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)));
				pr.setRequester(prService.replaceSmartQuotes(new String(pr.getRequester().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)));
				pr.setDescription(pr.getDescription() != null ? prService.replaceSmartQuotes(new String(pr.getDescription().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)) : pr.getDescription());

				prService.prCreate(pr, isDraft, SecurityLibrary.getLoggedInUser());
				// save As Draft
				if (isDraft) {
					redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[] { pr.getPrId() }, Global.LOCALE));
					return new ModelAndView("redirect:/buyer/buyerDashboard");
				}
				LOG.info("pr Updated succesfully");
			}
		} catch (ApplicationException e) {
			LOG.error("Error in saving Pr " + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			Pr newPr = prService.getLoadedPrById(pr.getId());
			// autoPopulate currency for budget if diff. conversion rate required
			if (newPr != null) {
				newPr.setBusinessUnit(pr.getBusinessUnit());
				newPr.setCostCenter(pr.getCostCenter());
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (!(newPr.getCurrency().getCurrencyCode().equals(buyerSettings.getCurrency().getCurrencyCode()))) {
					newPr.setConversionRateRequired(Boolean.TRUE);
				}
				super.constructPrAttributes(model);
				super.constructPrTeamAttributes(model, newPr);

				model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), pr.getId()));
				if (pr.getTemplateId() != null) {
					PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(pr.getTemplateId(), SecurityLibrary.getLoggedInUserTenantId());
					model.addAttribute("templateFields", prTemplate.getFields());
					model.addAttribute("prTemplate", prTemplate);
				}
			}
			model.addAttribute("pr", newPr);
			return new ModelAndView("createPrDetails", "pr", newPr);
		}

		catch (Exception e) {
			LOG.error("Error in saving Pr " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("pr.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			Pr newPr = prService.getLoadedPrById(pr.getId());
			// autoPopulate currency for budget if diff. conversion rate required
			if (newPr != null) {
				newPr.setBusinessUnit(pr.getBusinessUnit());
				newPr.setCostCenter(pr.getCostCenter());
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (!(newPr.getCurrency().getCurrencyCode().equals(buyerSettings.getCurrency().getCurrencyCode()))) {
					newPr.setConversionRateRequired(Boolean.TRUE);
				}
				super.constructPrAttributes(model);
				super.constructPrTeamAttributes(model, newPr);

				model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), pr.getId()));
				if (pr.getTemplateId() != null) {
					PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(pr.getTemplateId(), SecurityLibrary.getLoggedInUserTenantId());
					model.addAttribute("templateFields", prTemplate.getFields());
					model.addAttribute("prTemplate", prTemplate);
				}
			}
			model.addAttribute("pr", newPr);
		}
		return new ModelAndView("redirect:prDocument/" + pr.getId());
	}

	@RequestMapping(path = "/savePrDraft", method = RequestMethod.POST)
	public ModelAndView savePrDraft(@ModelAttribute Pr pr, Model model, RedirectAttributes redir) {
		// Save the PR name and description in session before saving draft
		redir.addFlashAttribute("prName", pr.getName());
		redir.addFlashAttribute("prDescription", pr.getDescription());
		
		savePr(pr, model, redir, Boolean.TRUE);
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(path = "/addTeamMemberToList", method = RequestMethod.POST)
	public ResponseEntity<List<EventTeamMember>> addTeamMemberToList(@RequestParam("prId") String prId, @RequestParam("userId") String userId, @RequestParam("memberType") TeamMemberType memberType) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("addTeamMemberToList:  " + " prId: " + prId + " userId: " + userId);
		List<EventTeamMember> teamMembers = null;
		try {
			if (userId != null) {
				prService.addTeamMemberToList(prId, userId, memberType);
				teamMembers = prService.getPlainTeamMembersForPr(prId);
				sendAddTeamMemberEmailNotificationEmail(prId, userId, memberType);

			} else {
				headers.add("error", "Please Select TeamMember Users");
				LOG.error("Please Select TeamMember Users");
				return new ResponseEntity<List<EventTeamMember>>(null, headers, HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			LOG.error("Error While adding TeamMember users : " + e.getMessage(), e);
			headers.add("error", "Please Select TeamMember Users");
			return new ResponseEntity<List<EventTeamMember>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOG.info(teamMembers.size() + "..................." + teamMembers);
		return new ResponseEntity<List<EventTeamMember>>(teamMembers, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/removeTeamMemberfromList", method = RequestMethod.POST)
	public ResponseEntity<List<User>> removeTeamMemberfromList(@RequestParam(value = "prId") String prId, @RequestParam(value = "userId") String userId, Model model) {
		LOG.info("addTeamMemberToList:  " + " prId: " + prId + " userId: " + userId);
		HttpHeaders headers = new HttpHeaders();
		LOG.info("userId Call");
		List<User> teamMembers = null;
		List<User> userList = new ArrayList<User>();
		try {
			PrTeamMember prTeamMember = prService.getPrTeamMemberByUserIdAndPrId(prId, userId);

			sendRemoveTeamMemberEmailNotificationEmail(prId, userId, prTeamMember.getTeamMemberType());
			teamMembers = prService.removeTeamMemberfromList(prId, userId, prTeamMember);

			// List<User> activeUserList =
			// userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			List<User> activeUserList = userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			for (User user : activeUserList) {
				try {
					userList.add((User) user.clone());
				} catch (Exception e) {
					LOG.error("Error while cloning user List: " + e.getMessage(), e);
					headers.add("error", "Error While removing Team Member users : " + e.getMessage());
					return new ResponseEntity<List<User>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			// Remove all users that are already added as editors.
			userList.removeAll(teamMembers);
		} catch (Exception e) {
			LOG.error("Error While removing Team Member users : " + e.getMessage(), e);
			headers.add("error", "Error While removing Team Member users : " + e.getMessage());
			return new ResponseEntity<List<User>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<User>>(userList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/prDraftList", method = RequestMethod.GET)
	public String prDraftList() {
		return "prDraftList";
	}

	@RequestMapping(path = "/prDraftData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Pr>> prDraftData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.debug(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<Pr> data = new TableData<Pr>(prService.findAllDraftPr(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long recordFiltered = prService.findTotalFilteredDraftPr(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input);
			long totalCount = prService.findTotalDraftPr(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), input,null);
			LOG.info("**********PrDrafts Count*********** filtered " + recordFiltered);
			LOG.info("**********PrDrafts Count*********** totalCount " + totalCount);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			LOG.info("total draft count :" + totalCount);
			return new ResponseEntity<TableData<Pr>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching pr draft list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Pr list : " + e.getMessage());
			return new ResponseEntity<TableData<Pr>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/prDashboardData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Pr>> poDashboardData(TableDataInput input, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.debug(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			List<Pr> poList = prService.findAllPo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, null, null);
			TableData<Pr> data = new TableData<Pr>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = prService.findTotalFilteredPo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, null, null);
			long totalCount = prService.findTotalApprovedPrs(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(),null);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<Pr>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching po list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching PO list : " + e.getMessage());
			return new ResponseEntity<TableData<Pr>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/prTransferDashboardData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Pr>> poTransferDashboardData(TableDataInput input, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.debug(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			List<Pr> poList = prService.findAllPoTransfer(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, null, null);

			TableData<Pr> data = new TableData<Pr>(poList);

			data.setDraw(input.getDraw());
			long recordFiltered = prService.findTotalFilteredPoTransfer(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, null, null);
			long totalCount = prService.findTotalPrTransfer(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), null);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<Pr>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching po list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching PO list : " + e.getMessage());
			return new ResponseEntity<TableData<Pr>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/cancelPr", method = RequestMethod.GET)
	public String cancelPr(@RequestParam("prId") String prId, @RequestParam(required = false) String cancelReason, RedirectAttributes redir) {
		try {
			Pr pr = prService.getPrById(prId);
			pr.setStatus(PrStatus.CANCELED);
			pr.setCancelReason(cancelReason);
			prService.updatePr(pr);
			redir.addFlashAttribute("success", messageSource.getMessage("success.pr.cancel", new Object[] { StringUtils.checkString(pr.getName()).length() == 0 ? pr.getPrId() : pr.getName() }, Global.LOCALE));
		
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CANCELLED, "PR '"+pr.getPrId()+"' is Cancelled", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PR);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error While Cnacel Pr :" + e.getMessage(), e);
		}
		return "redirect:prDraftList";
	}

	@RequestMapping(path = "/transferPo", method = RequestMethod.POST)
	public String transferPo(@RequestParam("prId") String prId, @RequestParam(required = false) String poRemarks, RedirectAttributes redir) {
		try {
			LOG.info("Transfer PO prId :" + prId + " ==User Name :" + SecurityLibrary.getLoggedInUser().getName() + " ==poRemarks : " + poRemarks);
			Pr pr = prService.getPrById(prId);
			pr.setStatus(PrStatus.TRANSFERRED);

			PrAudit audit = new PrAudit();
			audit.setAction(PrAuditType.TRANSFERRED);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			audit.setDescription(poRemarks);
			audit.setPr(pr);
			prService.updatePoStatus(pr, audit);
			
			redir.addFlashAttribute("success", messageSource.getMessage("success.po.transfer", new Object[] { StringUtils.checkString(pr.getName()).length() == 0 ? pr.getPrId() : pr.getName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error While transfer PO :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.transfer.po", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:prView/" + prId;
	}

	@RequestMapping(path = "/deliverPo", method = RequestMethod.POST)
	public String deliverPo(@RequestParam("prId") String prId, @RequestParam(required = false) String poRemarks, RedirectAttributes redir) {
		try {
			LOG.info("Deliver PO prId :" + prId + " ==User Name :" + SecurityLibrary.getLoggedInUser().getName() + " ==poRemarks : " + poRemarks);
			Pr pr = prService.getPrById(prId);
			pr.setStatus(PrStatus.DELIVERED);

			PrAudit audit = new PrAudit();
			audit.setAction(PrAuditType.DELIVERED);
			audit.setActionBy(SecurityLibrary.getLoggedInUser());
			audit.setActionDate(new Date());
			audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			audit.setDescription(poRemarks);
			audit.setPr(pr);
			prService.updatePoStatus(pr, audit);
			redir.addFlashAttribute("success", messageSource.getMessage("success.po.deliver", new Object[] { StringUtils.checkString(pr.getName()).length() == 0 ? pr.getPrId() : pr.getName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error While deliver PO :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.deliver.po", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:prView/" + prId;
	}

	@RequestMapping(path = "/poReportList", method = RequestMethod.GET)
	public String poReportList(Model model) {
		List<Pr> poList = new ArrayList<>();
		try {
			TableDataInput input = new TableDataInput();
			input.setStart(0);
			input.setLength(10);
			List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED, PrStatus.DELIVERED);
			poList = prService.findAllSearchFilterPo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, null, null, statuses);
		} catch (Exception e) {
			LOG.error("Error While fetching PO list.." + e.getMessage(), e);
		}
		model.addAttribute("poList", poList);
		return "poReportList";
	}

	@RequestMapping(path = "/poReportList", method = RequestMethod.POST)
	public ResponseEntity<List<Pr>> getPoReportList(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "dateTimeRange", required = false) String dateTimeRange) {
		try {
			LOG.info("dateTimeRange :" + dateTimeRange);
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
					LOG.info("Start date : " + startDate + " End Date : " + endDate);
				}
			}
			TableDataInput input = new TableDataInput();
			input.setStart(0);
			input.setLength(5000);
			List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED, PrStatus.DELIVERED);
			List<Pr> poList = prService.findAllSearchFilterPo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, statuses);
			return new ResponseEntity<List<Pr>>(poList, HttpStatus.OK);
		} catch (Exception e) {
			HttpHeaders headers = new HttpHeaders();
			LOG.error("Error While Filter po list :" + e.getMessage(), e);
			return new ResponseEntity<List<Pr>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/transferPoToErp/{prId}", method = RequestMethod.GET)
	public String transferPoToErp(@PathVariable("prId") String prId, RedirectAttributes redir) {
		try {
			LOG.info("Transfer PO prId :" + prId + " ==User Name :" + SecurityLibrary.getLoggedInUser().getName());
			erpIntegrationService.transferPrToErp(prId);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.po.transfered", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error While deliver PO :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.transfer.po", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/prView/" + prId;
	}

	private void sendAddTeamMemberEmailNotificationEmail(String prId, String userId, TeamMemberType memberType) {
		// TODO Auto-generated method stub
		try {
			String subject = "You have been Invited as TEAM MEMBER In PR";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();
			User user = userService.getUsersById(userId);
			Pr pr = prService.getPrById(prId);

			map.put("userName", user.getName());
			map.put("memberType", memberType.getValue());

			if (memberType == TeamMemberType.Editor)
				map.put("memberMessage", " Allows you to edit the entire draft stage of the PR but not finish the PR");
			else if (memberType == TeamMemberType.Viewer)
				map.put("memberMessage", "Allows you to view entire draft stage of the PR without the ability to edit");
			else
				map.put("memberMessage", "Allows you to perform the same actions as the PR Owner.");
			String eventName = StringUtils.checkString(pr.getName()).length() > 0 ? pr.getName() : " ";
			map.put("eventName", StringUtils.checkString(pr.getName()).length() > 0 ? pr.getName() : " ");
			map.put("createdBy", pr.getCreatedBy().getName());
			map.put("eventId", pr.getPrId());
			map.put("eventRefNum", StringUtils.checkString(pr.getReferenceNumber()).length() > 0 ? pr.getReferenceNumber() : " ");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);

			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.TEAM_MEMBER_TEMPLATE_PR), map);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(user.getCommunicationEmail(), subject, message);
			}
			url = APP_URL + "/buyer/createPrDetails/" + pr.getId();

			String notificationMessage = messageSource.getMessage("team.pr.add", new Object[] { memberType, eventName }, Global.LOCALE);
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

	private void sendRemoveTeamMemberEmailNotificationEmail(String prId, String userId, TeamMemberType memberType) {
		// TODO Auto-generated method stub
		try {
			String subject = "You have been Removed as TEAM MEMBER from PR";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();
			User user = userService.getUsersById(userId);
			map.put("userName", user.getName());
			map.put("memberType", memberType.getValue());

			Pr pr = prService.getPrById(prId);

			if (memberType == TeamMemberType.Editor)
				map.put("memberMessage", " Allows you to edit the entire draft stage of the PR but not finish the PR");
			else if (memberType == TeamMemberType.Viewer)
				map.put("memberMessage", "Allows you to view entire draft stage of the PR without the ability to edit");
			else
				map.put("memberMessage", "Allows you to perform the same actions as the PR Owner.");
			String eventName = StringUtils.checkString(pr.getName()).length() > 0 ? pr.getName() : " ";
			map.put("eventName", StringUtils.checkString(pr.getName()).length() > 0 ? pr.getName() : " ");
			map.put("createdBy", pr.getCreatedBy().getName());
			map.put("createrEmail", pr.getCreatedBy().getCommunicationEmail());
			map.put("eventId", pr.getPrId());
			map.put("eventRefNum", StringUtils.checkString(pr.getReferenceNumber()).length() > 0 ? pr.getReferenceNumber() : " ");

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);

			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.REMOVE_TEAM_MEMBER_TEMPLATE_PR), map);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(user.getCommunicationEmail(), subject, message);
			}
			url = APP_URL + "/buyer/createPrDetails/" + pr.getId();

			String notificationMessage = messageSource.getMessage("team.pr.remove", new Object[] { memberType, eventName }, Global.LOCALE);
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

	@RequestMapping(path = "/prReports", method = RequestMethod.POST)
	public void downloadprReports(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam String dateTimeRange, @RequestParam String prIds, boolean select_all, @ModelAttribute("searchFilterPrPojo") SearchFilterPrPojo searchFilterPrPojo) {
		try {

			LOG.info("-----------prIds--------------" + prIds);
			String prArr[] = prIds.split(",");
			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String[] array = dateTimeRange.split("-");

				if (array.length > 0) {
					startDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[0]);
					endDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[1]);
				}
			}
			LOG.info("uc 1.1 5 nov test case 3  WHERE IT STARTED prReportList >>>>  ");
			List<Pr> prList = prService.findAllSearchFilterPrReport(SecurityLibrary.getLoggedInUserTenantId(), prArr, select_all, searchFilterPrPojo, startDate, endDate, SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId());

			List<PrPojo> prPojos = new ArrayList<>();
			for (Pr pr : prList) {

				PrPojo prpojo = new PrPojo();

				prpojo.setId(pr.getId());
				prpojo.setCreatedBy(pr.getCreatedBy());
				prpojo.setPrCreatedDate(pr.getPrCreatedDate());
				prpojo.setBusinessUnit(pr.getBusinessUnit());
				prpojo.setDescription(pr.getDescription());
				prpojo.setName(pr.getName());
				prpojo.setGrandTotal(pr.getGrandTotal());
				prpojo.setStatus(pr.getStatus());
				prpojo.setPrId(pr.getPrId());
				prpojo.setDecimal(pr.getDecimal());
				prpojo.setReferenceNumber(pr.getReferenceNumber());
				prpojo.setSupplierName(pr.getSupplierName());
				prpojo.setSupplier(pr.getSupplier());
				prpojo.setCurrency(pr.getCurrency());
				for (PrApproval prAprovar : prService.getAllPrApprovalsByPrId(pr.getId())) {
					LOG.info("------------------------" + prAprovar.toString());
					List<PrApprovalUser> prApprovalUsers = prAprovar.getApprovalUsers();
					LOG.info("------------------------" + prApprovalUsers.size());
					if (CollectionUtil.isNotEmpty(prApprovalUsers)) {

						// Collections.sort(prApprovalUsers, new Comparator<PrApprovalUser>() {
						// public int compare(PrApprovalUser m1, PrApprovalUser m2) {
						//
						// return m1.getActionDate().compareTo(m2.getActionDate());
						// }
						// });

						PrApprovalUser prApprovalUser = prApprovalUsers.get(0);
						if (prApprovalUser.getApprovalStatus() == ApprovalStatus.APPROVED) {

							prpojo.setApprovedBy(prApprovalUser.getUser());
							LOG.info("------------------------" + prApprovalUser.getUser().getName());
							prpojo.setPrApprovedDate(prApprovalUser.getActionDate());
						}

					}
				}

				prPojos.add(prpojo);

			}

			prService.downloadPRReports(prPojos, response, session);
		} catch (

		Exception e) {
			LOG.error("Error While Filter po list :" + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/prReportList", method = RequestMethod.GET)
	public String rfxTemplateList(Model model) {
		LOG.info("status data "+PrStatus.DRAFT.toString());
		String userId=SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId();
		List<String> bizUnitIds =null;
		bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
		long draftPrCount =  prService.findTotalDraftPr(SecurityLibrary.getLoggedInUserTenantId(), userId,null, bizUnitIds);
		model.addAttribute("draftPrCount", draftPrCount);

		long pendingPrCount = prService.findTotalPendingPr(SecurityLibrary.getLoggedInUserTenantId(), userId,bizUnitIds);
		model.addAttribute("pendingPrCount", pendingPrCount);

		long prApprovalsCount = prService.findTotalApprovedPrs(SecurityLibrary.getLoggedInUserTenantId(),userId,bizUnitIds);
		model.addAttribute("prApprovalsCount", prApprovalsCount);

		long prTransferCount = prService.findTotalPrTransfer(SecurityLibrary.getLoggedInUserTenantId(), userId,bizUnitIds);
		model.addAttribute("prTransferCount", prTransferCount);

		long completedPrCount = prService.findTotalCompletePr(SecurityLibrary.getLoggedInUserTenantId(),userId,bizUnitIds);
		model.addAttribute("completedPrCount", completedPrCount);

		long cancelledPrCount = prService.findTotalCancelledPr(SecurityLibrary.getLoggedInUserTenantId(), userId,bizUnitIds);
		model.addAttribute("cancelledPrCount", cancelledPrCount);

		return "prReportList";
	}



	@RequestMapping(path = "/prReportListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<PrPojo>> prReportListData(TableDataInput input, String dateTimeRange,@RequestParam(required = false) String prType,@RequestParam(required = false) String status) {
		LOG.info("-------------------------" + status);
		try {

			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String[] array = dateTimeRange.split("-");

				if (array.length > 0) {
					startDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[0]);
					endDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[1]);

					LOG.info("=====start===" + startDate);
					LOG.info("=====end===" + endDate);

				}

			}
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED, PrStatus.DELIVERED, PrStatus.PENDING);


			String userId=SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") && !prType.equals("ME") ? null : SecurityLibrary.getLoggedInUser().getId();
			List<String> bizUnitIds =null;
			bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			LOG.info(" prReportList RETRIEVE UNIT ID??? >>>>>>>>>>>>>>>   "+bizUnitIds);

			List<Pr> prList = prService.findAllSearchFilterPrBizUnit(userId, SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, statuses,status,prType,bizUnitIds);
			long recordFiltered = prService.findTotalFilteredPr(userId, SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, statuses,status,prType,bizUnitIds);
//
			long totalCount = recordFiltered;

			List<PrPojo> prPojos = new ArrayList<>();
			for (Pr pr : prList) {

				PrPojo prpojo = new PrPojo();

				prpojo.setId(pr.getId());
				prpojo.setCreatedBy(pr.getCreatedBy());
				prpojo.setPrCreatedDate(pr.getPrCreatedDate());
				prpojo.setBusinessUnit(pr.getBusinessUnit());
				prpojo.setDescription(pr.getDescription());
				prpojo.setName(pr.getName());
				prpojo.setGrandTotal(pr.getGrandTotal());
				prpojo.setStatus(pr.getStatus());
				prpojo.setDecimal(pr.getDecimal());
				prpojo.setPrId(pr.getPrId());
				prpojo.setReferenceNumber(pr.getReferenceNumber());
				prpojo.setSupplierName(pr.getSupplierName());
				prpojo.setSupplier(pr.getSupplier());
				prpojo.setCurrency(pr.getCurrency());
				prpojo.setPoNumber(pr.getPoNumber());
				List<PrApproval> prAprovars = prService.getAllPrApprovalsByPrId(pr.getId());
				for (PrApproval prAprovar : prAprovars) {
					if (prAprovar != null) {
						List<PrApprovalUser> prApprovalUsers = prAprovar.getApprovalUsers();
						if (CollectionUtil.isNotEmpty(prApprovalUsers)) {

							/*
							 * LOG.info("Pr Approval Users........................"); Collections.sort(prApprovalUsers,
							 * new Comparator<PrApprovalUser>() { public int compare(PrApprovalUser m1, PrApprovalUser
							 * m2) { return m1.getActionDate().compareTo(m2.getActionDate()); } });
							 */

							PrApprovalUser prApprovalUser = prApprovalUsers.get(0);
							if (prApprovalUser.getApprovalStatus() == ApprovalStatus.APPROVED) {

								prpojo.setApprovedBy(prApprovalUser.getUser());
								prpojo.setPrApprovedDate(prApprovalUser.getActionDate());
							}

						}
					}
				}

				prPojos.add(prpojo);

			}

			TableData<PrPojo> data = new TableData<PrPojo>(prPojos);
			data.setDraw(input.getDraw());
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<PrPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching pr list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching PR list : " + e.getMessage());
			return new ResponseEntity<TableData<PrPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@ModelAttribute("prStatusList")
	public List<PrStatus> getPrStatusList() {
		List<PrStatus> prStatusList = Arrays.asList(PrStatus.TRANSFERRED, PrStatus.APPROVED, PrStatus.CANCELED, PrStatus.PENDING);
		return prStatusList;
	}

	@RequestMapping(path = "/poReportListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Pr>> poReportListData(TableDataInput input, String dateTimeRange) {
		LOG.info("-------------------------" + dateTimeRange);
		try {

			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String[] array = dateTimeRange.split("-");

				if (array.length > 0) {
					startDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[0]);
					endDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[1]);

					LOG.info("=====start===" + startDate);
					LOG.info("=====end===" + endDate);

				}

			}
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());

			List<PrStatus> statuses = Arrays.asList(PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED, PrStatus.DELIVERED);
			List<Pr> poList = prService.findAllSearchFilterPo(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, statuses);
			LOG.info("poList" + poList.size());

			TableData<Pr> data = new TableData<Pr>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = prService.findTotalFilteredPoReportList(SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, statuses);
			long totalCount = prService.findTotalPoReportList(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), statuses);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<Pr>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching po list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching PO list : " + e.getMessage());
			return new ResponseEntity<TableData<Pr>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(path = "/prReportCsv", method = RequestMethod.POST)
	public void downloadPrCsv(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir,@RequestParam(required = false) String prType,@RequestParam(required = false) String status, @RequestParam String dateTimeRange, @RequestParam String prIds, boolean select_all, @ModelAttribute("searchFilterPrPojo") SearchFilterPrPojo searchFilterPrPojo) {
		LOG.info("-----------prIds--------------" + prIds);
		try {
			File file = File.createTempFile("prReports", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());
			String userId=SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") && !prType.equals("ME") ? null : SecurityLibrary.getLoggedInUser().getId();
			String prArr[] = prIds.split(",");
			Date startDate = null;
			Date endDate = null;
			List<PrStatus> statuses = Arrays.asList(PrStatus.DRAFT,PrStatus.APPROVED, PrStatus.TRANSFERRED, PrStatus.CANCELED,PrStatus.COMPLETE, PrStatus.DELIVERED, PrStatus.PENDING);

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String[] array = dateTimeRange.split("-");

				if (array.length > 0) {
					startDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[0]);
					endDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[1]);
				}
			}
			prService.downloadCsvFileForPrList(response, file, prArr, startDate, endDate, searchFilterPrPojo, select_all, SecurityLibrary.getLoggedInUserTenantId(), userId, statuses, session,prType,status);

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "PR Report is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PR);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}
			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=prReports.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
		}

	}

}
