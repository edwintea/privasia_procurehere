package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;


import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.web.editors.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PrTemplateFieldName;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.pojo.BuyerAddressPojo;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.PrTemplateFieldPojo;
import com.privasia.procurehere.core.pojo.PrTemplatePojo;
import com.privasia.procurehere.core.pojo.TableData;
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
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.GroupCodeService;
import com.privasia.procurehere.service.PaymentTermsService;
import com.privasia.procurehere.service.PrTemplateService;
import com.privasia.procurehere.service.UserService;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PrTemplateController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	CostCenterEditor costCenterEditor;

	@Resource
	MessageSource messageSource;

	@Autowired
	UserService userService;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	PrTemplateService prTemplateService;

	@Autowired
	PrTemplateApprovalEditor prTemplateApprovalEditor;

	@Autowired
	PoTemplateApprovalEditor poTemplateApprovalEditor;

	@Autowired
	GrTemplateApprovalEditor grTemplateApprovalEditor;

	@Autowired
	InvoiceTemplateApprovalEditor invoiceTemplateApprovalEditor;

	@Autowired
	BuyerAddressEditor buyerAddressEditor;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	BusinessUnitEditor businessUnitEditor;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	PaymentTermesEditor paymentTermesEditor;

	@Autowired
	PaymentTermsService paymentTermesService;

	@Autowired
	GroupCodeService groupCodeService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(CostCenter.class, costCenterEditor);
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(PrTemplateApprovalUser.class, prTemplateApprovalEditor);
		binder.registerCustomEditor(PoTemplateApprovalUser.class, poTemplateApprovalEditor);
		binder.registerCustomEditor(GrTemplateApprovalUser.class, grTemplateApprovalEditor);
		binder.registerCustomEditor(InvoiceTemplateApprovalUser.class, invoiceTemplateApprovalEditor);
		binder.registerCustomEditor(BuyerAddress.class, buyerAddressEditor);
		binder.registerCustomEditor(PaymentTermes.class, paymentTermesEditor);

	}

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@ModelAttribute(name = "addressList")
	public List<BuyerAddressPojo> getBuyerAddressList() {
		return buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
	}

	@RequestMapping(path = "/prTemplate", method = RequestMethod.GET)
	public ModelAndView createPrTemplate(Model model) {
		LOG.info("Create PrTemplate called");
		try {
			model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			PrTemplate prTemplate = new PrTemplate();
			Buyer buyer = new Buyer();
			buyer.setId(SecurityLibrary.getLoggedInUserTenantId());
			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			PrTemplateFieldPojo prTemplateFieldPojo = new PrTemplateFieldPojo(buyerSettings.getCurrency(), buyerSettings.getDecimal());
			prTemplate.setTemplateFieldBinding(prTemplateFieldPojo);
			prTemplate.setBuyer(buyer);
			constructPrTemplateAttributes(model, prTemplate, false, new ArrayList<String>());

			List<User> userTeamMemberList = new ArrayList<User>();
			List<TemplatePrTeamMembers> assignedTeamMembers = new ArrayList<TemplatePrTeamMembers>();

			prTemplate.setApprovalOptional(false);



			List<UserPojo> userTeamMember = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
			// team members (Normal)
			// userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			for (UserPojo user : userTeamMember) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				userTeamMemberList.add(u);
			}
			LOG.info("Get the PR Template: " + prTemplate);
			model.addAttribute("teamMember", false);
			model.addAttribute("userTeamMembers", userTeamMemberList);
			model.addAttribute("assignedTeamMembers", assignedTeamMembers);
			model.addAttribute("prTemplate", prTemplate);
			List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
			List<UserPojo> approvalUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
			model.addAttribute("userlist", approvalUserList);
			model.addAttribute("assignedUserListDropDown", userList);

			List<PaymentTermes> paymentsTerms = paymentTermesService.getActivePaymentTermesByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("paymentTermsList", paymentsTerms);

		} catch (Exception e) {
			LOG.info("Error while creating prTemplate.........." + e.getMessage(), e);
		}
		return new ModelAndView("prTemplate");
	}

	@RequestMapping(path = "/savePrTemplate", method = RequestMethod.POST)
	public ModelAndView savePrTemplate(@Valid @ModelAttribute("prTemplate") PrTemplate prTemplate, @RequestParam("userId") String[] userId, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("Save PrTemplate Data called : " + prTemplate.toLogString());
		List<String> errMessages = new ArrayList<String>();

		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				model.addAttribute("error", errMessages);
				constructPrTemplateAttributes(model, prTemplate, false, Arrays.asList(userId));
				model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				return new ModelAndView("prTemplate", "prTemplate", prTemplate);

			} else {

				if (CollectionUtil.isNotEmpty(prTemplate.getApprovals())) {
					LOG.info("Total Approval Levels : " + prTemplate.getApprovals().size());
					int level = 1;
					for (PrTemplateApproval app : prTemplate.getApprovals()) {
						LOG.info("app Level : " + app.getLevel());
						app.setPrTemplate(prTemplate);
						app.setLevel(level++);
						LOG.info("type :" + app.getApprovalType());
						for (PrTemplateApprovalUser approvalUser : app.getApprovalUsers()) {
							approvalUser.setApproval(app);
							LOG.info("user id : " + approvalUser.getUser().getId() + " username : " + approvalUser.getUser().getName());
							approvalUser.setId(null);
						}
					}
				}

				if (CollectionUtil.isNotEmpty(prTemplate.getPoApprovals())) {
					LOG.info("Total Approval Levels : " + prTemplate.getPoApprovals().size());
					int level = 1;
					for (PoTemplateApproval poApp : prTemplate.getPoApprovals()) {
						LOG.info("app Level : " + poApp.getLevel());
						poApp.setPrTemplate(prTemplate);
						poApp.setLevel(level++);
						LOG.info("type :" + poApp.getApprovalType());
						for (PoTemplateApprovalUser approvalUser : poApp.getApprovalUsers()) {
							approvalUser.setPoApproval(poApp);
							LOG.info("user id : " + approvalUser.getUser().getId() + " username : " + approvalUser.getUser().getName());
							approvalUser.setId(null);
						}
					}
				}

				if (CollectionUtil.isNotEmpty(prTemplate.getGrApprovals())) {
					LOG.info("Total Approval Levels : " + prTemplate.getGrApprovals().size());
					int level = 1;
					for (GrTemplateApproval grApp : prTemplate.getGrApprovals()) {
						LOG.info("app Level : " + grApp.getLevel());
						grApp.setPrTemplate(prTemplate);
						grApp.setLevel(level++);
						LOG.info("type :" + grApp.getApprovalType());
						for (GrTemplateApprovalUser approvalUser : grApp.getApprovalUsers()) {
							approvalUser.setGrApproval(grApp);
							LOG.info("user id : " + approvalUser.getUser().getId() + " username : " + approvalUser.getUser().getName());
							approvalUser.setId(null);
						}
					}
				}

				if (CollectionUtil.isNotEmpty(prTemplate.getInvoiceApprovals())) {
					LOG.info("Total Approval Levels : " + prTemplate.getInvoiceApprovals().size());
					int level = 1;
					for (InvoiceTemplateApproval invoiceApp : prTemplate.getInvoiceApprovals()) {
						LOG.info("app Level : " + invoiceApp.getLevel());
						invoiceApp.setPrTemplate(prTemplate);
						invoiceApp.setLevel(level++);
						LOG.info("type :" + invoiceApp.getApprovalType());
						for (InvoiceTemplateApprovalUser approvalUser : invoiceApp.getApprovalUsers()) {
							approvalUser.setInvoiceApproval(invoiceApp);
							LOG.info("user id : " + approvalUser.getUser().getId() + " username : " + approvalUser.getUser().getName());
							approvalUser.setId(null);
						}
					}
				}

				else {
					LOG.warn("Approval levels is empty.");
				}

				List<TemplatePrTeamMembers> teamMembers = new ArrayList<TemplatePrTeamMembers>();
				if (CollectionUtil.isNotEmpty(prTemplate.getTeamMembers())) {
					for (TemplatePrTeamMembers team : prTemplate.getTeamMembers()) {
						if (team != null && team.getUser() != null) {
							if (StringUtils.checkString(team.getUser().getId()).length() > 0 && team.getTeamMemberType() != null) {
								team.setPrTemplate(prTemplate);
								teamMembers.add(team);
							}
						}

					}
				}
				prTemplate.setTeamMembers(teamMembers);

				if (!prTemplateService.isExists(prTemplate)) {
					constructFieldsForSaving(prTemplate);
					LOG.info("prTemplate iD" + prTemplate.getId());
					if (StringUtils.checkString(prTemplate.getId()).length() == 0) {
						prTemplate.setCreatedBy(SecurityLibrary.getLoggedInUser());
						prTemplate.setCreatedDate(new Date());
						prTemplate = prTemplateService.save(prTemplate);
						for (String usersId : userId) {
							User user = userService.getUsersForPrById(usersId);
							List<PrTemplate> assignedTemplateList = user.getAssignedPrTemplates();
							if (CollectionUtil.isNotEmpty(assignedTemplateList)) {
								assignedTemplateList.add(prTemplate);
								user.setAssignedPrTemplates(assignedTemplateList);
							} else {
								assignedTemplateList = new ArrayList<>();
								assignedTemplateList.add(prTemplate);
								user.setAssignedPrTemplates(assignedTemplateList);
							}
							userService.updateUser(user);
						}

						redir.addFlashAttribute("success", messageSource.getMessage("template.save.success", new Object[] { prTemplate.getTemplateName() }, Global.LOCALE));
						LOG.info("create prTemplate Called by : " + SecurityLibrary.getLoggedInUser());
					} else {
						Integer assignedCount = prTemplateService.findAssignedTemplateCount(prTemplate.getId());

						PrTemplate persistObj = prTemplateService.getPrTemplateById(prTemplate.getId());
						boolean doAuditStatusChange = prTemplate.getStatus() != persistObj.getStatus();

						LOG.info("Assigned Count: {}", assignedCount);

						if (assignedCount == 0) {
							persistObj.setFields(prTemplate.getFields());
							persistObj.setTeamMembers(prTemplate.getTeamMembers());

							persistObj.setApprovals(prTemplate.getApprovals());
							persistObj.setApprovalOptional(prTemplate.getApprovalOptional());
							persistObj.setApprovalReadOnly(prTemplate.getApprovalReadOnly());
							persistObj.setApprovalVisible(prTemplate.getApprovalVisible());
							persistObj.setMinimumApprovalCount(prTemplate.getMinimumApprovalCount());

							persistObj.setPoApprovals(prTemplate.getPoApprovals());
							persistObj.setApprovalPoOptional(prTemplate.getApprovalPoOptional());
							persistObj.setApprovalPoReadOnly(prTemplate.getApprovalPoReadOnly());
							persistObj.setApprovalPoVisible(prTemplate.getApprovalPoVisible());
							persistObj.setMinimumPoApprovalCount(prTemplate.getMinimumPoApprovalCount());

							persistObj.setGrApprovals(prTemplate.getGrApprovals());
							persistObj.setApprovalGrOptional(prTemplate.getApprovalGrOptional());
							persistObj.setApprovalGrReadOnly(prTemplate.getApprovalGrReadOnly());
							persistObj.setApprovalGrVisible(prTemplate.getApprovalGrVisible());
							persistObj.setMinimumGrApprovalCount(prTemplate.getMinimumGrApprovalCount());

							persistObj.setInvoiceApprovals(prTemplate.getInvoiceApprovals());
							persistObj.setApprovalInvoiceOptional(prTemplate.getApprovalInvoiceOptional());
							persistObj.setApprovalInvoiceReadOnly(prTemplate.getApprovalInvoiceReadOnly());
							persistObj.setApprovalInvoiceVisible(prTemplate.getApprovalInvoiceVisible());
							persistObj.setMinimumInvoiceApprovalCount(prTemplate.getMinimumInvoiceApprovalCount());

							persistObj.setContractItemsOnly(prTemplate.getContractItemsOnly());
							persistObj.setLockBudget(prTemplate.getLockBudget());
							persistObj.setPaymentTermes(prTemplate.getPaymentTermes());
						}
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setTemplateName(prTemplate.getTemplateName());
						persistObj.setTemplateDescription(prTemplate.getTemplateDescription());
						persistObj.setStatus(prTemplate.getStatus());

						prTemplateService.deleteusersForTemplate(prTemplate.getId());
						// List<String> assignedUserId =
						// prTemplateService.getTemplateByUserIdAndTemplateId(prTemplate.getId(),
						// SecurityLibrary.getLoggedInUserTenantId());
						for (String usersId : userId) {
							// for (String dbAssignedUser : assignedUserId) {
							// if (!dbAssignedUser.equals(userId)) {
							User user = userService.getUsersForPrById(usersId);
							List<PrTemplate> assignedTemplateList = user.getAssignedPrTemplates();
							assignedTemplateList.add(prTemplate);
							user.setAssignedPrTemplates(assignedTemplateList);
							userService.updateUser(user);
							// }
							// }
						}
						prTemplateService.update(persistObj);

						if (doAuditStatusChange) {
							try {
								LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
								BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(prTemplate.getStatus() == Status.ACTIVE ? AuditTypes.ACTIVE : AuditTypes.INACTIVE, "PR Template '" + prTemplate.getTemplateName() + "' is " + prTemplate.getStatus(), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PRTemplate);
								buyerAuditTrailDao.save(buyerAuditTrail);
								LOG.info("--------------------------AFTER AUDIT---------------------------------------");
							} catch (Exception e) {
								LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
							}
						}
						redir.addFlashAttribute("success", messageSource.getMessage("template.update.success", new Object[] { prTemplate.getTemplateName() }, Global.LOCALE));
						LOG.info("update prTemplate Called");
					}

				} else {
					LOG.info("Validation error ...............");
					model.addAttribute("error", messageSource.getMessage("template.error.duplicate", new Object[] { prTemplate.getTemplateName() }, Global.LOCALE));
					if (StringUtils.checkString(prTemplate.getId()).length() == 0) {
						model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					} else {
						model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
					}
					constructPrTemplateAttributes(model, prTemplate, false, Arrays.asList(userId));

					try {
						List<User> userTeamMemberList = new ArrayList<User>();
						List<TemplatePrTeamMembers> assignedTeamMembers = new ArrayList<TemplatePrTeamMembers>();
						List<User> userTeamMember = userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
						List<User> remainingUser = new ArrayList<User>();
						for (User user : userTeamMember) {
							LOG.info("user............." + user.getName());
							userTeamMemberList.add((User) user.clone());
						}
						for (TemplatePrTeamMembers team : prTemplate.getTeamMembers()) {
							User user = userService.findById(team.getUser().getId());
							User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.getEmailNotifications(), user.getTenantId(), user.isDeleted());
							if (!remainingUser.contains(u)) {
								team.setUser(u);
							}
							assignedTeamMembers.add(team);
							remainingUser.add((User) team.getUser().clone());
						}
						userTeamMemberList.removeAll(remainingUser);
						model.addAttribute("userTeamMembers", userTeamMemberList);
						model.addAttribute("assignedTeamMembers", assignedTeamMembers);

					} catch (Exception ee) {
						LOG.info(ee.getMessage(), ee);
					}
					return new ModelAndView("prTemplate", "prTemplate", prTemplate);
				}

			}
		} catch (Exception e) {
			LOG.error("Error while saving the PrTemplate : " + e.getMessage(), e);
			// model.addAttribute("error", "Error while saving the PrTemplate : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("prtemplate.error.while.saving", new Object[] { e.getMessage() }, Global.LOCALE));
			if (StringUtils.checkString(prTemplate.getId()).length() == 0) {
				model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			} else {
				model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
			}
			constructPrTemplateAttributes(model, prTemplate, false, Arrays.asList(userId));

			return new ModelAndView("prTemplate", "prTemplate", prTemplate);
		}
		return new ModelAndView("redirect:prTemplateList");
	}

	/**
	 * @param model
	 * @param prTemplate
	 * @param isEditMode
	 * @param userId
	 */
	private void constructPrTemplateAttributes(Model model, PrTemplate prTemplate, boolean isEditMode, List<String> userId) {
		model.addAttribute("currencyList", currencyService.getAllActiveCurrencyCode());
		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isEnableUnitAndCostCorrelation", enableCorrelation);
		if (prTemplate.getId() != null) {
			if (CollectionUtil.isNotEmpty(prTemplate.getFields())) {
				for (PrTemplateField tf : prTemplate.getFields()) {
					if (tf.getFieldName() == PrTemplateFieldName.BUSINESS_UNIT) {
						if (tf.getDefaultValue() != null && enableCorrelation == Boolean.TRUE) {
							BusinessUnit unit = businessUnitService.getPlainBusinessUnitById(tf.getDefaultValue());
							if (unit.getStatus() == Status.ACTIVE) {
								List<CostCenterPojo> assignedCostList = businessUnitService.getCostCentersByBusinessUnitId(tf.getDefaultValue(), Status.ACTIVE);
								// List<String> assignedCostId =
								// costCenterService.getCostCenterByBusinessId(tf.getDefaultValue());
								// if (CollectionUtil.isNotEmpty(assignedCostId)) {
								// for (String assignedCost : assignedCostId) {
								// CostCenterPojo cost = costCenterService.getCostCenterByCostId(assignedCost);
								// if (cost.getStatus() == Status.ACTIVE) {
								// assignedCostList.add(cost);
								// }
								// }
								// }
								model.addAttribute("costCenterList", assignedCostList);
							} else {
								model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
							}
						} else {
							model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
						}
					}
				}
			}
		} else {
			model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		}
		List<BusinessUnit> businessUnitList = businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId());

		model.addAttribute("businessUnitList", businessUnitList);
		// List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> appUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
		List<PrTemplateApprovalUser> templateApprovalUserList = new ArrayList<PrTemplateApprovalUser>();
		List<PoTemplateApprovalUser> templatePoApprovalUserList = new ArrayList<PoTemplateApprovalUser>();
		List<GrTemplateApprovalUser> templateGrApprovalUserList = new ArrayList<GrTemplateApprovalUser>();
		List<InvoiceTemplateApprovalUser> templateInvoiceApprovalUserList = new ArrayList<InvoiceTemplateApprovalUser>();



		if (CollectionUtil.isNotEmpty(prTemplate.getApprovals())) {
			for (PrTemplateApproval approval : prTemplate.getApprovals()) {
				for (PrTemplateApprovalUser approvalUser : approval.getApprovalUsers()) {
					User u = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(), approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(), approvalUser.getUser().getEmailNotifications(), approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());
					PrTemplateApprovalUser obj = new PrTemplateApprovalUser(u);
					if (!templateApprovalUserList.contains(obj)) {
						templateApprovalUserList.add(obj);
					}
				}
			}
		}

		if (CollectionUtil.isNotEmpty(prTemplate.getPoApprovals())) {
			for (PoTemplateApproval poApproval : prTemplate.getPoApprovals()) {
				for (PoTemplateApprovalUser approvalUser : poApproval.getApprovalUsers()) {
					User u = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(), approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(), approvalUser.getUser().getEmailNotifications(), approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());
					PoTemplateApprovalUser obj = new PoTemplateApprovalUser(u);
					if (!templatePoApprovalUserList.contains(obj)) {
						templatePoApprovalUserList.add(obj);
					}
				}
			}
		}

		if (CollectionUtil.isNotEmpty(prTemplate.getGrApprovals())) {
			for (GrTemplateApproval grApproval : prTemplate.getGrApprovals()) {
				for (GrTemplateApprovalUser approvalUser : grApproval.getApprovalUsers()) {
					User u = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(), approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(), approvalUser.getUser().getEmailNotifications(), approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());
					GrTemplateApprovalUser obj = new GrTemplateApprovalUser(u);
					if (!templateGrApprovalUserList.contains(obj)) {
						templateGrApprovalUserList.add(obj);
					}
				}
			}
		}

		if (CollectionUtil.isNotEmpty(prTemplate.getInvoiceApprovals())) {
			for (InvoiceTemplateApproval invoiceApproval : prTemplate.getInvoiceApprovals()) {
				for (InvoiceTemplateApprovalUser approvalUser : invoiceApproval.getApprovalUsers()) {
					User u = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(), approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(), approvalUser.getUser().getEmailNotifications(), approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());
					InvoiceTemplateApprovalUser obj = new InvoiceTemplateApprovalUser(u);
					if (!templateInvoiceApprovalUserList.contains(obj)) {
						templateInvoiceApprovalUserList.add(obj);
					}
				}
			}
		}


		for (UserPojo user : appUserList) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			PrTemplateApprovalUser obj = new PrTemplateApprovalUser(u);
			if (!templateApprovalUserList.contains(obj)) {
				templateApprovalUserList.add(obj);
			}
		}
		model.addAttribute("userList", templateApprovalUserList);

		for (UserPojo user : appUserList) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			PoTemplateApprovalUser obj = new PoTemplateApprovalUser(u);
			if (!templatePoApprovalUserList.contains(obj)) {
				templatePoApprovalUserList.add(obj);
			}
		}
		model.addAttribute("userList", templatePoApprovalUserList);

		for (UserPojo user : appUserList) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			GrTemplateApprovalUser obj = new GrTemplateApprovalUser(u);
			if (!templateGrApprovalUserList.contains(obj)) {
				templateGrApprovalUserList.add(obj);
			}
		}
		model.addAttribute("userList", templateGrApprovalUserList);

		for (UserPojo user : appUserList) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			InvoiceTemplateApprovalUser obj = new InvoiceTemplateApprovalUser(u);
			if (!templateInvoiceApprovalUserList.contains(obj)) {
				templateInvoiceApprovalUserList.add(obj);
			}
		}
		model.addAttribute("userList", templateInvoiceApprovalUserList);

		List<User> assignedUserList = new ArrayList<>();
		List<String> assignedUserIds = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(userId)) {
			for (String assgnedUser : userId) {
				User user = userService.getUsersNameAndId(assgnedUser);
				assignedUserList.add(user);
				assignedUserIds.add(user.getId());
			}
		}

		List<UserPojo> finalUserList = new ArrayList<UserPojo>();
		for (UserPojo user : appUserList) {
			if (!assignedUserIds.contains(user.getId())) {
				finalUserList.add(user);
			}
		}
		if (CollectionUtil.isNotEmpty(finalUserList)) {
			model.addAttribute("userlist", finalUserList);
		} else {
			model.addAttribute("userlist", appUserList);
		}

		model.addAttribute("assignedUserList", assignedUserList);
		model.addAttribute("templateApprovalUserList", templateApprovalUserList);
		model.addAttribute("templatePoApprovalUserList", templatePoApprovalUserList);
		model.addAttribute("templateGrApprovalUserList", templateGrApprovalUserList);
		model.addAttribute("templateInvoiceApprovalUserList", templateInvoiceApprovalUserList);
		if (prTemplate != null && prTemplate.getTemplateFieldBinding() != null && prTemplate.getTemplateFieldBinding().getCorrespondenceAddress() != null) {
			BuyerAddress buyerAddress = buyerAddressService.getBuyerAddress(prTemplate.getTemplateFieldBinding().getCorrespondenceAddress().getId());
			model.addAttribute("buyerAddress", buyerAddress);
		}
	}

	private void constructFieldsForSaving(PrTemplate prTemplate) {

		PrTemplateFieldPojo tfp = prTemplate.getTemplateFieldBinding();
		if (tfp != null) {
			Buyer buyer = new Buyer();
			buyer.setId(SecurityLibrary.getLoggedInUserTenantId());
			prTemplate.setFields(new ArrayList<PrTemplateField>());

			if (tfp.getPrName() != null || tfp.getPrNameDisabled() != null) {
				PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.PR_NAME, tfp.getPrName(), tfp.getPrNameDisabled(), buyer, prTemplate);
				prTemplate.getFields().add(tf);
			}
			if (tfp.getRequester() != null || tfp.getRequesterDisabled() != null) {
				PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.REQUESTER, tfp.getRequester(), tfp.getRequesterDisabled(), buyer, prTemplate);
				prTemplate.getFields().add(tf);
			}
			if (tfp.getCorrespondenceAddress() != null || tfp.getCorrespondenceAddressDisabled() != null) {
				PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.CORRESPONDENCE_ADDRESS, (tfp.getCorrespondenceAddress() != null ? tfp.getCorrespondenceAddress().getId() : null), tfp.getCorrespondenceAddressDisabled(), buyer, prTemplate);
				prTemplate.getFields().add(tf);
			}
			if (tfp.getBaseCurrency() != null || tfp.getBaseCurrencyDisabled() != null) {
				PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.BASE_CURRENCY, (tfp.getBaseCurrency() != null ? tfp.getBaseCurrency().getId() : null), tfp.getBaseCurrencyDisabled(), buyer, prTemplate);
				prTemplate.getFields().add(tf);
			}
			if (Boolean.FALSE == prTemplate.getLockBudget()) {
				if (tfp.getAvailableBudget() != null || tfp.getAvailableBudgetDisabled() != null) {
					PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.AVAILABLE_BUDGET, (tfp.getAvailableBudget() != null ? tfp.getAvailableBudget().toString() : null), tfp.getAvailableBudgetVisible(), tfp.getAvailableBudgetDisabled(), tfp.getAvailableBudgetOptional(), buyer, prTemplate);
					prTemplate.getFields().add(tf);
				}
			}
			if (tfp.getHideOpenSupplier() != null) {
				PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.HIDE_OPEN_SUPPLIER, tfp.getHideOpenSupplier(), buyer, prTemplate);
				prTemplate.getFields().add(tf);
			}
			if (tfp.getDecimal() != null || tfp.getDecimalDisabled() != null) {
				PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.DECIMAL, (tfp.getDecimal() != null ? String.valueOf(tfp.getDecimal()) : null), tfp.getDecimalDisabled(), buyer, prTemplate);
				prTemplate.getFields().add(tf);
			}
			if (tfp.getCostCenter() != null || tfp.getCostCenterVisible() != null || tfp.getCostCenterDisabled() != null || tfp.getCostCenterOptional() != null) {
				PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.COST_CENTER, (tfp.getCostCenter() != null ? tfp.getCostCenter().getId() : null), tfp.getCostCenterVisible(), tfp.getCostCenterDisabled(), tfp.getCostCenterOptional(), buyer, prTemplate);
				prTemplate.getFields().add(tf);
			}
			if (tfp.getBusinessUnit() != null || tfp.getBusinessUnitDisabled() != null) {
				PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.BUSINESS_UNIT, (tfp.getBusinessUnit() != null ? tfp.getBusinessUnit().getId() : null), tfp.getBusinessUnitDisabled(), buyer, prTemplate);
				prTemplate.getFields().add(tf);
			}
			if (tfp.getPaymentTerms() != null || tfp.getPaymentTermsDisabled() != null) {
				PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.PAYMENT_TERM, (tfp.getPaymentTerms() != null ? tfp.getPaymentTerms().getPaymentTermCode() + " - " + tfp.getPaymentTerms().getDescription() : null), tfp.getPaymentTermsDisabled(), buyer, prTemplate);
				prTemplate.getFields().add(tf);
			}
			if (tfp.getTermAndCondition() != null || tfp.getTermAndConditionDisabled() != null) {
				PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.TERM_AND_CONDITION, tfp.getTermAndCondition(), tfp.getTermAndConditionDisabled(), buyer, prTemplate);
				prTemplate.getFields().add(tf);
			}
			if (tfp.getConversionRate() != null || tfp.getConversionRateDisabled() != null) {
				PrTemplateField tf = new PrTemplateField(PrTemplateFieldName.CONVERSION_RATE, (tfp.getConversionRate() != null ? tfp.getConversionRate().toString() : null), tfp.getConversionRateDisabled(), buyer, prTemplate);
				prTemplate.getFields().add(tf);
			}
		}
		for (PrTemplateField tf : prTemplate.getFields()) {
			LOG.info("Field : " + tf.toLogString());
		}
	}

	@RequestMapping(path = "/prTemplateList", method = RequestMethod.GET)
	public String prTemplateList(Model model) {
		return "prTemplateList";
	}

	@RequestMapping(path = "/prTemplateData", method = RequestMethod.GET)
	public ResponseEntity<TableData<PrTemplatePojo>> prTemplateData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TableData<PrTemplatePojo> data = new TableData<PrTemplatePojo>(prTemplateService.findTemplatesForTenantId(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());
			long totalFilterCount = prTemplateService.findTotalFilteredTemplatesForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = prTemplateService.findTotalTemplatesForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<PrTemplatePojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching template list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching template list : " + e.getMessage());
			return new ResponseEntity<TableData<PrTemplatePojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/editPrTemplate", method = RequestMethod.GET)
	public String editPrTemplate(@RequestParam String id, Model model) {
		LOG.info("Getting the editPrTemplate. : " + id);
		PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(id, SecurityLibrary.getLoggedInUserTenantId());
		if (prTemplate == null) {
			model.addAttribute("error", messageSource.getMessage("common.no.records.found", new Object[] { id }, Global.LOCALE));
			return "redirect:prTemplateList";
		}

		// Error condition. Send the user back to listing screen.
		List<UserPojo> userList = userService.fetchAllUsersForTenantForPrTemplate(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER, id);
		List<String> assignedUserId = prTemplateService.getTemplateByUserIdAndTemplateId(id, SecurityLibrary.getLoggedInUserTenantId());

		List<User> assignedUserList = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(assignedUserId)) {
			for (String assgnedUser : assignedUserId) {
				User user = userService.getUsersNameAndId(assgnedUser);
				assignedUserList.add(user);
			}
		}
		model.addAttribute("assignedUserList", assignedUserList);
		model.addAttribute("assignedUserListDropDown", userList);

		List<User> userTeamMemberList = new ArrayList<User>();
		List<User> remainingUser = new ArrayList<User>();
		List<TemplatePrTeamMembers> assignedTeamMembers = new ArrayList<TemplatePrTeamMembers>();

		// List<UserPojo> userTeamMember = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(),
		// "", UserType.NORMAL_USER);
		try {

			for (UserPojo user : userList) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				userTeamMemberList.add(u);
			}

			for (TemplatePrTeamMembers team : prTemplate.getTeamMembers()) {
				assignedTeamMembers.add(team);
				remainingUser.add((User) team.getUser().clone());
				LOG.info("(User)team.getUser().clone()..........." + team.getUser().getLoginId());
			}
			model.addAttribute("userTeamMembers", userTeamMemberList);
			model.addAttribute("assignedTeamMembers", assignedTeamMembers);
		} catch (Exception e) {
			LOG.info("userTeamMemberList.............." + e.getMessage(), e);
		}

		Integer assignedCount = prTemplateService.findAssignedTemplateCount(id);
		// List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<PrTemplateApprovalUser> templateApprovalUserList = new ArrayList<PrTemplateApprovalUser>();
		List<PoTemplateApprovalUser> templatePoApprovalUserList = new ArrayList<PoTemplateApprovalUser>();
		List<GrTemplateApprovalUser> templateGrApprovalUserList = new ArrayList<GrTemplateApprovalUser>();
		List<InvoiceTemplateApprovalUser> templateInvoiceApprovalUserList = new ArrayList<InvoiceTemplateApprovalUser>();
		model.addAttribute("assignedCount", assignedCount);
		model.addAttribute("teamMember", false);
		model.addAttribute("templateApprovalUserList", templateApprovalUserList);
		model.addAttribute("templatePoApprovalUserList", templatePoApprovalUserList);
		model.addAttribute("templateGrApprovalUserList", templateGrApprovalUserList);
		model.addAttribute("templateInvoiceApprovalUserList", templateInvoiceApprovalUserList);
		List<PaymentTermes> paymentsTerms = paymentTermesService.getActivePaymentTermesByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("paymentTermsList", paymentsTerms);

		if (prTemplate.getFields() != null) {
			for (PrTemplateField field: prTemplate.getFields()) {
				if (PrTemplateFieldName.PAYMENT_TERM.equals(field.getFieldName())) {
					model.addAttribute("selectedPaymentTerms", field.getDefaultValue());
					break;
				}
			}
		}

		constructFieldsForDisplay(prTemplate);

		// This attachment is required for Buyer.id only. It wont be updated into DB so no worries.
		Buyer buyer = new Buyer();
		buyer.setId(SecurityLibrary.getLoggedInUserTenantId());
		prTemplate.setBuyer(buyer);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		constructPrTemplateAttributes(model, prTemplate, true, assignedUserId);
		model.addAttribute("prTemplate", prTemplate);
		return "prTemplate";
	}

	@RequestMapping(path = "/deletePrTemplate", method = RequestMethod.GET)
	public String deletePrTemplate(@RequestParam String id, Model model) {
		LOG.info("Delete deletePrTemplate called ");
		PrTemplate prTemplate = prTemplateService.getPrTemplateById(id);
		// Error condition. Send the user back to listing screen.
		if (prTemplate == null) {
			model.addAttribute("error", messageSource.getMessage("common.no.records.found", new Object[] { id }, Global.LOCALE));
			return "redirect:prTemplateList";
		}
		try {
			prTemplateService.delete(prTemplate);
			model.addAttribute("success", messageSource.getMessage("template.success.delete", new Object[] { prTemplate.getTemplateName() }, Global.LOCALE));
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting Template , " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("prtemplate.error.delete.child.exist", new Object[] { prTemplate.getTemplateName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting prTemplate [ " + prTemplate.getTemplateName() + " ]" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("template.error.delete", new Object[] { prTemplate.getTemplateName() }, Global.LOCALE));
		}
		return "prTemplateList";
	}

	private void constructFieldsForDisplay(PrTemplate prTemplate) {

		PrTemplateFieldPojo tfp = prTemplate.getTemplateFieldBinding();
		if (CollectionUtil.isNotEmpty(prTemplate.getFields())) {
			tfp = new PrTemplateFieldPojo();
			for (PrTemplateField tf : prTemplate.getFields()) {
				switch (tf.getFieldName()) {
				case BASE_CURRENCY:
					if (tf.getDefaultValue() != null) {
						tfp.setBaseCurrency(currencyService.getCurrency(tf.getDefaultValue()));
					}
					tfp.setBaseCurrencyDisabled(tf.getReadOnly());
					break;
				case COST_CENTER:
					if (tf.getDefaultValue() != null) {
						tfp.setCostCenter(costCenterService.getCostCenterById(tf.getDefaultValue()));
					}
					tfp.setCostCenterVisible(tf.getVisible());
					tfp.setCostCenterOptional(tf.getOptional());
					tfp.setCostCenterDisabled(tf.getReadOnly());
					break;
				case BUSINESS_UNIT:
					if (tf.getDefaultValue() != null) {
						tfp.setBusinessUnit(businessUnitService.getPlainBusinessUnitById(tf.getDefaultValue()));
					}
					tfp.setBusinessUnitDisabled(tf.getReadOnly());
					break;
				case DECIMAL:
					if (tf.getDefaultValue() != null) {
						tfp.setDecimal(tf.getDefaultValue());
					}
					tfp.setDecimalDisabled(tf.getReadOnly());
					break;
				case CORRESPONDENCE_ADDRESS:
					if (tf.getDefaultValue() != null) {
						tfp.setCorrespondenceAddress(buyerAddressService.getBuyerAddress(tf.getDefaultValue()));
					}
					tfp.setCorrespondenceAddressDisabled(tf.getReadOnly());
					break;
				case PR_NAME:
					if (tf.getDefaultValue() != null) {
						tfp.setPrName(tf.getDefaultValue());
					}
					tfp.setPrNameDisabled(tf.getReadOnly());
					break;
				case PAYMENT_TERM:
					// if (tf.getDefaultValue() != null) {
					// tfp.setPaymentTerms(tf.getDefaultValue());
					// }

					if (tf.getDefaultValue() != null) {
						tfp.setPaymentTermes(paymentTermesService.getPaymentTermesById(tf.getDefaultValue()));
					}
					tfp.setPaymentTermsDisabled(tf.getReadOnly());
					break;
				case REQUESTER:
					if (tf.getDefaultValue() != null) {
						tfp.setRequester(tf.getDefaultValue());
					}
					tfp.setRequesterDisabled(tf.getReadOnly());
					break;
				case TERM_AND_CONDITION:
					if (tf.getDefaultValue() != null) {
						tfp.setTermAndCondition(tf.getDefaultValue());
					}
					tfp.setTermAndConditionDisabled(tf.getReadOnly());
					break;

				case HIDE_OPEN_SUPPLIER:
					tfp.setHideOpenSupplier(tf.getVisible());
					break;

				case AVAILABLE_BUDGET:
					if (tf.getDefaultValue() != null) {
						tfp.setAvailableBudget(new java.math.BigDecimal(tf.getDefaultValue()));
					}
					tfp.setAvailableBudgetDisabled(tf.getReadOnly());
					tfp.setAvailableBudgetVisible(tf.getVisible());
					tfp.setAvailableBudgetOptional(tf.getOptional());
					break;
				case CONVERSION_RATE:
					if (tf.getDefaultValue() != null) {
						tfp.setConversionRate(new java.math.BigDecimal(tf.getDefaultValue()));
					}
					tfp.setConversionRateDisabled(tf.getReadOnly());
					break;
				default:
					break;
				}
			}

		}
		prTemplate.setTemplateFieldBinding(tfp);

	}

	@RequestMapping(value = "copyPrTemplate", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> copyPrTemplate(@RequestParam(value = "templateId") String templateId, @RequestParam(value = "templateName") String templateName, @RequestParam(value = "templateDesc") String templateDesc) {
		HttpHeaders headers = new HttpHeaders();
		String newTemplateId = "";
		try {
			PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(templateId, SecurityLibrary.getLoggedInUserTenantId());
			PrTemplate newTemplate = new PrTemplate();
			Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
			newTemplate.setBuyer(buyer);
			newTemplate.setTemplateName(templateName);
			newTemplate.setTemplateDescription(templateDesc);
			newTemplate.setContractItemsOnly(prTemplate.getContractItemsOnly());
			newTemplate.setStatus(Status.ACTIVE);
			if (!prTemplateService.isExists(newTemplate)) {
				PrTemplate temp = prTemplateService.copyTemplate(prTemplate, newTemplate, SecurityLibrary.getLoggedInUser());
				newTemplateId = temp.getId();
				LOG.info("Copied Template Created and Saved Sucessfully  :" + prTemplate.getTemplateName());
				headers.add("success", messageSource.getMessage("template.save.success", new Object[] { newTemplate.getTemplateName() }, Global.LOCALE));
			} else {

				headers.add("error", messageSource.getMessage("template.error.duplicate", new Object[] { newTemplate.getTemplateName() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while save as : " + e.getMessage(), e);
			headers.add("error", "Error while Copying Pr template : " + e.getMessage());
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<String>(newTemplateId, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/addCurrentUsers", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<User>> addUsersToTemplate(String tempId, String userId) {
		LOG.info("tempId " + tempId + " userId " + userId);
		HttpHeaders headers = new HttpHeaders();
		try {
			PrTemplate template = prTemplateService.getPrTemplateById(tempId);
			User user = userService.getUsersForPrById(userId);
			List<PrTemplate> templatesList = user.getAssignedPrTemplates();

			if (CollectionUtil.isNotEmpty(templatesList)) {
				templatesList.add(template);
			} else {
				templatesList = new ArrayList<>();
				templatesList.add(template);
			}
			headers.add("success", messageSource.getMessage("prTemplate.usersave.success", new Object[] {}, Global.LOCALE));
			user.setAssignedPrTemplates(templatesList);
			User newUser = userService.updateUser(user);
			LOG.info("Users " + newUser.getName());
			List<User> users = prTemplateService.getAllUsers(tempId);
			return new ResponseEntity<List<User>>(users, headers, HttpStatus.OK);
		} catch (Exception e) {
			headers.add("error", messageSource.getMessage("prTemplate.usersave.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<User>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/getAssingedCostCenterList", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<CostCenterPojo>> getAssingedCostCenterList(@RequestParam("unitId") String unitId) {
		try {
			LOG.info("Business Unit Id : " + unitId);
			List<CostCenterPojo> assignedCostList = businessUnitService.getCostCentersByBusinessUnitId(unitId, Status.ACTIVE);
			// List<String> assignedCostId = costCenterService.getCostCenterByBusinessId(unitId);
			// if (CollectionUtil.isNotEmpty(assignedCostId)) {
			// for (String assignedCost : assignedCostId) {
			// CostCenterPojo cost = costCenterService.getCostCenterByCostId(assignedCost);
			// if (cost.getStatus() == Status.ACTIVE) {
			// assignedCostList.add(cost);
			// }
			// }
			// }
			return new ResponseEntity<List<CostCenterPojo>>(assignedCostList, HttpStatus.OK);
		} catch (Exception e) {
			HttpHeaders headers = new HttpHeaders();
			LOG.error("Error fetching Cost Center list : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("assign.cost.center.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<CostCenterPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/getAssingedGroupCodeList", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<GroupCode>> getAssingedGroupCodeList(@RequestParam("buId") String unitId) {
		try {
			LOG.info("Business Unit Id : " + unitId);
			return new ResponseEntity<List<GroupCode>>(groupCodeService.getGroupCodeIdByBusinessId(unitId), HttpStatus.OK);
		} catch (Exception e) {
			HttpHeaders headers = new HttpHeaders();
			LOG.error("Error fetching Group Code list : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("assign.groupCode.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<GroupCode>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
