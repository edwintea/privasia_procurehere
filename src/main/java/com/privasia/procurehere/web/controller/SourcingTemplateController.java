package com.privasia.procurehere.web.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;


import com.privasia.procurehere.core.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.enums.SourcingTemplateFieldName;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.InactiveTemplateException;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.SourcingFormTemplatePojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TemplateFieldPojo;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.GroupCodeService;
import com.privasia.procurehere.service.ProcurementCategoriesService;
import com.privasia.procurehere.service.ProcurementMethodService;
import com.privasia.procurehere.service.SourcingFormCqService;
import com.privasia.procurehere.service.SourcingTemplateService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import com.privasia.procurehere.web.editors.CostCenterEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.GroupCodeEditor;
import com.privasia.procurehere.web.editors.ProcurementCategoriesEditor;
import com.privasia.procurehere.web.editors.ProcurementMethodEditor;
import com.privasia.procurehere.web.editors.SourcingFormApprovalEditor;
import com.privasia.procurehere.web.editors.UserEditor;

/**
 * @author sarang
 */
@Controller
@RequestMapping("/buyer")
public class SourcingTemplateController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	UserService userService;

	@Autowired
	SourcingFormApprovalEditor sourcingFormApprovalEditor;

	@Autowired
	SourcingTemplateService sourcingTemplateService;

	@Autowired
	UserEditor userEditor;

	@Autowired

	MessageSource messageSource;

	@Autowired
	SourcingFormCqService cqService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	CostCenterEditor costCenterEditor;

	@Autowired
	BusinessUnitEditor businessUnitEditor;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	ProcurementMethodService procurementMethodService;

	@Autowired
	ProcurementCategoriesService procurementCategoriesService;

	@Autowired
	ProcurementMethodEditor procurementMethodEditor;

	@Autowired
	ProcurementCategoriesEditor procurementCategoriesEditor;

	@Autowired
	GroupCodeService groupCodeService;

	@Autowired
	GroupCodeEditor groupCodeEditor;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@InitBinder
	public void InitBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(SourcingFormApprovalUser.class, sourcingFormApprovalEditor);
		binder.registerCustomEditor(CostCenter.class, costCenterEditor);
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(ProcurementMethod.class, procurementMethodEditor);
		binder.registerCustomEditor(ProcurementCategories.class, procurementCategoriesEditor);
		binder.registerCustomEditor(GroupCode.class, groupCodeEditor);
	}

	@RequestMapping(path = "/createSourcingFormTemplate", method = RequestMethod.GET)
	public String createSourcingFormTemplate(Model model) {
		// List<User> userList = userService.fetchAllActiveUserForTenantId(SecurityLibrary.getLoggedInUserTenantId());
		LOG.info("/createSourcingFormTemplate ");
		SourcingFormTemplate sourceFormobj = new SourcingFormTemplate();
		sourceFormobj.setCreatedBy(SecurityLibrary.getLoggedInUser());
		sourceFormobj.setCreatedDate(new Date());
		sourceFormobj.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
		sourceFormobj.setStatus(SourcingStatus.ACTIVE);
		sourceFormobj = sourcingTemplateService.saveSourcingTemplate(sourceFormobj);
		model.addAttribute("button", messageSource.getMessage("save.next.button", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("sourceForm", sourceFormobj);
		// add approval User
		List<SourcingFormApprovalUser> approvalUserList = new ArrayList<SourcingFormApprovalUser>();
		List<UserPojo> approvalMember = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
		for (UserPojo user : approvalMember) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			approvalUserList.add(new SourcingFormApprovalUser(u));
		}

		LOG.info("Create Sourcing Form Template called and Approval Users Are " + approvalUserList);

		model.addAttribute("userList", approvalUserList);
		return "sourcingTemplate";
	}

	@RequestMapping(path = "/saveSourcingFormTemplate", method = RequestMethod.POST)
	public String saveSourcingFormTemplate(@ModelAttribute("sourceForm") SourcingFormTemplate sourceForm, @RequestParam("userId") String[] userId, Model model, RedirectAttributes redirect) {
		SourcingFormTemplate persistObject = null;
		boolean flag = false;
		boolean isDuplicateName = true;
		LOG.info("Template id id " + sourceForm.getId() + "  " + sourceForm.getFormName());
		model.addAttribute("sourceForm", sourceForm);
		model.addAttribute("baseCurrencyList", currencyService.getlActiveCurrencies());
		model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));

		try {
			constructFieldsForSaving(sourceForm);
			if (StringUtils.checkString(sourceForm.getId()).length() > 0) {
				LOG.info("updating sourcing form Status and decimal is---------" + sourceForm.getDecimal() + " " + sourceForm.getStatus());
				persistObject = sourcingTemplateService.getSourcingFormbyId(sourceForm.getId());
				boolean doAuditStatusChange = sourceForm.getStatus() != persistObject.getStatus();
				if (!persistObject.getIsTemplateUsed()) {
					persistObject.setFormName(sourceForm.getFormName());
					persistObject.setDescription(sourceForm.getDescription());
					persistObject.setModifiedDate(new Date());
					persistObject.setModifiedBy(SecurityLibrary.getLoggedInUser());
					persistObject.setDecimal(sourceForm.getDecimal());
					persistObject.setApprovalsCount(sourceForm.getApprovalsCount() != null ? sourceForm.getApprovalsCount() : 0);
					persistObject.setFields(sourceForm.getFields());
					persistObject.setReadOnlyTeamMember(sourceForm.getReadOnlyTeamMember());
					if (sourceForm.getAddAdditionalApprovals() != null) {
						persistObject.setAddAdditionalApprovals(sourceForm.getAddAdditionalApprovals());
					}

					model.addAttribute("isTemplateUsed", persistObject.getIsTemplateUsed());
					List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
					List<SourcingFormApprovalUser> templateApprovalUserList = new ArrayList<SourcingFormApprovalUser>();
					for (User user : userList) {
						templateApprovalUserList.add(new SourcingFormApprovalUser(user));
					}
					List<SourcingTemplateCq> cqList = sourcingTemplateService.getAllQuestionnarie(sourceForm.getId());
					model.addAttribute("cqList", cqList);
					model.addAttribute("userList", userList);
					// update Approvals
					if (CollectionUtil.isNotEmpty(sourceForm.getSourcingFormApproval())) {
						int level = 1;
						for (SourcingTemplateApproval app : sourceForm.getSourcingFormApproval()) {
							LOG.info("get sourcing form approval---------");
							app.setSourcingForm(persistObject);
							app.setLevel(level++);
							if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
								for (SourcingFormApprovalUser approvalUser : app.getApprovalUsers()) {
									approvalUser.setApproval(app);
								}
							}
						}
					}

					List<TemplateSourcingTeamMembers> teamMembers = new ArrayList<TemplateSourcingTeamMembers>();
					if (CollectionUtil.isNotEmpty(sourceForm.getTeamMembers())) {
						for (TemplateSourcingTeamMembers team : sourceForm.getTeamMembers()) {
							if (team != null && team.getUser() != null) {
								if (StringUtils.checkString(team.getUser().getId()).length() > 0 && team.getTeamMemberType() != null) {
									team.setSourcingForm(persistObject);
									teamMembers.add(team);
								}
							}

						}
					}
					sourceForm.setTeamMembers(teamMembers);
					persistObject.setTeamMembers(sourceForm.getTeamMembers());
					sourcingTemplateService.deleteusersForRfsTemplate(sourceForm.getId());
					LOG.info("assignedSourcingTemplates 16");
					for (String userID : userId) {
						User user = userService.getUsersForSourcingFormById(userID);
						List<SourcingFormTemplate> assignedTemplateList = user.getAssignedSourcingTemplates();
						if (assignedTemplateList == null) {
							assignedTemplateList = new ArrayList<SourcingFormTemplate>();
						}
						assignedTemplateList.add(sourceForm);
						user.setAssignedSourcingTemplates(assignedTemplateList);
						userService.updateUser(user);
					}

					// update Sourcing Form
					persistObject = sourcingTemplateService.SaveOrupdateSourcingTemplate(persistObject);
					
					if(doAuditStatusChange) {
						try {
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(sourceForm.getStatus() == SourcingStatus.ACTIVE ? AuditTypes.ACTIVE : AuditTypes.INACTIVE, " Sourcing Form Template '" + sourceForm.getFormName() + "' is changed  to " + persistObject.getStatus(), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SourcingTemplate);
							buyerAuditTrailDao.save(buyerAuditTrail);
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
					} else {
						try {
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Sourcing Form Template '"+persistObject.getFormName()+"' updated ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SourcingTemplate);
							buyerAuditTrailDao.save(buyerAuditTrail);
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
					}
					
					LOG.info("Template Name and description  " + persistObject.getFormName() + " " + persistObject.getDescription());
					model.addAttribute("tempId", persistObject.getId());
					model.addAttribute("status", persistObject.getStatus().toString() == "INACTIVE" ? true : false);
				}
			} else {
				persistObject = new SourcingFormTemplate();
				persistObject.setStatus(SourcingStatus.DRAFT);
				persistObject.setFormName(sourceForm.getFormName());
				persistObject.setDescription(sourceForm.getDescription());
				persistObject.setDecimal(sourceForm.getDecimal());
				persistObject.setCreatedBy(SecurityLibrary.getLoggedInUser());
				persistObject.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
				persistObject.setCreatedDate(new Date());
				LOG.info("sourceForm.getApprovalsCount() : " + sourceForm.getApprovalsCount());
				persistObject.setApprovalsCount(sourceForm.getApprovalsCount() != null ? sourceForm.getApprovalsCount() : 0);
				persistObject.setFields(sourceForm.getFields());
				persistObject.setReadOnlyTeamMember(sourceForm.getReadOnlyTeamMember());
				if (sourceForm.getAddAdditionalApprovals() != null) {
					persistObject.setAddAdditionalApprovals(sourceForm.getAddAdditionalApprovals());
				}
				model.addAttribute("isTemplateUsed", persistObject.getIsTemplateUsed());

				if (sourcingTemplateService.isTemplateExists(sourceForm.getFormName(), SecurityLibrary.getLoggedInUserTenantId())) {
					LOG.info("Duplicate name " + sourceForm.getFormName());
					model.addAttribute("button", messageSource.getMessage("save.next.button", new Object[] {}, LocaleContextHolder.getLocale()));
					model.addAttribute("sourceForm", sourceForm);
					isDuplicateName = true;
					List<SourcingFormApprovalUser> approvalUserList = new ArrayList<SourcingFormApprovalUser>();
					List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
					for (UserPojo user : userListSumm) {
						User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
						approvalUserList.add(new SourcingFormApprovalUser(u));
					}

					model.addAttribute("userList", approvalUserList);
					List<User> userTeamMemberList = new ArrayList<User>();
					List<TemplateSourcingTeamMembers> assignedTeamMembers = new ArrayList<TemplateSourcingTeamMembers>();
					List<UserPojo> userTeamMember = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
					for (UserPojo user : userTeamMember) {
						User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
						userTeamMemberList.add(u);
					}
					model.addAttribute("userTeamMembers", userTeamMemberList);
					model.addAttribute("assignedTeamMembers", assignedTeamMembers);
					model.addAttribute("error", "Duplicate Sourcing template Name,template with this name is already available ");
					return "sourcingTemplateDetails";
				}

				persistObject = sourcingTemplateService.saveSourcingTemplate(persistObject);
				LOG.info("assignedSourcingTemplates 17");
				for (String usersId : userId) {
					User user = userService.getUsersForSourcingFormById(usersId);
					List<SourcingFormTemplate> assignedTemplateList = user.getAssignedSourcingTemplates();
					if (CollectionUtil.isNotEmpty(assignedTemplateList)) {
						assignedTemplateList.add(persistObject);
						user.setAssignedSourcingTemplates(assignedTemplateList);
					} else {
						assignedTemplateList = new ArrayList<>();
						assignedTemplateList.add(persistObject);
						user.setAssignedSourcingTemplates(assignedTemplateList);
					}
					userService.updateUser(user);
				}

			}

			if (persistObject != null) {
				persistObject = sourcingTemplateService.getSourcingFormbyId(persistObject.getId());
				for (RfsTemplateDocument document : persistObject.getRfsTemplateDocuments()) {
					String uploadedBy = (document.getUploadBy() != null ? document.getUploadBy().getId() : "");
				}
				if (CollectionUtil.isNotEmpty(sourceForm.getSourcingFormApproval())) {
					int level = 1;
					for (SourcingTemplateApproval app : sourceForm.getSourcingFormApproval()) {
						LOG.info("get sourcing form approval---------");
						app.setSourcingForm(persistObject);
						app.setLevel(level++);
						if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
							for (SourcingFormApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
							}
						}
					}
				}

				List<TemplateSourcingTeamMembers> teamMembers = new ArrayList<TemplateSourcingTeamMembers>();
				if (CollectionUtil.isNotEmpty(sourceForm.getTeamMembers())) {
					for (TemplateSourcingTeamMembers team : sourceForm.getTeamMembers()) {
						if (team != null && team.getUser() != null) {
							if (StringUtils.checkString(team.getUser().getId()).length() > 0 && team.getTeamMemberType() != null) {
								team.setSourcingForm(persistObject);
								teamMembers.add(team);
							}
						}

					}
				}

				sourceForm.setTeamMembers(teamMembers);
				persistObject.setTeamMembers(sourceForm.getTeamMembers());

				if (CollectionUtil.isNotEmpty(sourceForm.getSourcingFormApproval())) {
					persistObject.setSourcingFormApproval(sourceForm.getSourcingFormApproval());
				} else {
					persistObject.setSourcingFormApproval(new ArrayList<SourcingTemplateApproval>());
				}
				persistObject.setEventDetailCompleted(true);

				SourcingFormTemplate template = sourcingTemplateService.updateSourcingTemplate(persistObject);
				
				try {
					BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Sourcing Form Template '" + sourceForm.getFormName() + "' is updated", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SourcingTemplate);
					buyerAuditTrailDao.save(ownerAuditTrail);
				} catch (Exception e) {
					LOG.info("Error to create audit trails message");
				}
				
				model.addAttribute("event", template);
				LOG.info(template.getEventDetailCompleted());
				redirect.addFlashAttribute("formId", persistObject.getId());
				model.addAttribute("tempId", persistObject.getId());
				model.addAttribute("status", persistObject.getStatus());
				return "redirect:/buyer/sourcingTemplateDocument/" + persistObject.getId();

			} else {
				model.addAttribute("button", messageSource.getMessage("save.next.button", new Object[] {}, LocaleContextHolder.getLocale()));
				model.addAttribute("sourceForm", sourceForm);
				LOG.info("Duplicate Template Name ");
				redirect.addFlashAttribute("error", messageSource.getMessage("error.while.saving.sourcingtemplate1", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/sourcingTemplateDocument";
			}

		} catch (Exception e) {
			model.addAttribute("sourceForm", sourceForm);
			model.addAttribute("sourcingForm", sourceForm);
			List<User> userList = userService.fetchAllActiveUserForTenantId(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("userList", userList);
			// LOG.info("userlist--------------------------- " + userList);
			flag = true;
			model.addAttribute("flag", flag);
			model.addAttribute("isDuplicateName", isDuplicateName);
			// redirect.addFlashAttribute("sourceForm", sourceForm);
			LOG.info("sourceForm Description " + sourceForm.getDescription() + " sourceForm Decimal  " + sourceForm.getDecimal());
			LOG.error("error while saving sourcing form template " + e.getMessage(), e);
			// model.addAttribute("error", "Error While saving Sourcing template:" + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("error.while.saving.sourcingtemplate", new Object[] { e.getMessage() }, Global.LOCALE));
			return "sourcingTemplateDetails";
		}
		// return "redirect:/buyer/sourcingTemplateDetails";

	}

	private void constructFieldsForSaving(SourcingFormTemplate sourceForm) {

		TemplateFieldPojo tfp = sourceForm.getTemplateFieldBinding();
		if (tfp != null) {
			Buyer buyer = new Buyer();
			buyer.setId(SecurityLibrary.getLoggedInUserTenantId());
			sourceForm.setFields(new ArrayList<SourcingTemplateField>());

			if (tfp.getCostCenter() != null || tfp.getCostCenterVisible() != null || tfp.getCostCenterDisabled() != null || tfp.getCostCenterOptional() != null) {
				SourcingTemplateField tf = new SourcingTemplateField(SourcingTemplateFieldName.COST_CENTER, (tfp.getCostCenter() != null ? tfp.getCostCenter().getId() : null), tfp.getCostCenterVisible(), tfp.getCostCenterDisabled(), tfp.getCostCenterOptional(), buyer, sourceForm);
				sourceForm.getFields().add(tf);
			}
			if (tfp.getBusinessUnit() != null || tfp.getBusinessUnitDisabled() != null) {
				SourcingTemplateField tf = new SourcingTemplateField(SourcingTemplateFieldName.BUSINESS_UNIT, (tfp.getBusinessUnit() != null ? tfp.getBusinessUnit().getId() : null), tfp.getBusinessUnitDisabled(), buyer, sourceForm);
				sourceForm.getFields().add(tf);
			}
			if (tfp.getBudgetAmount() != null || tfp.getBudgetAmountVisible() != null || tfp.getBudgetAmountDisabled() != null || tfp.getBudgetAmountOptional() != null) {
				SourcingTemplateField tf = new SourcingTemplateField(SourcingTemplateFieldName.BUDGET_AMOUNT, (tfp.getBudgetAmount() != null ? String.valueOf(tfp.getBudgetAmount()) : null), tfp.getBudgetAmountVisible(), tfp.getBudgetAmountDisabled(), tfp.getBudgetAmountOptional(), buyer, sourceForm);
				sourceForm.getFields().add(tf);
			}
			if (tfp.getHistoricAmount() != null || tfp.getHistoricAmountVisible() != null || tfp.getHistoricAmountDisabled() != null || tfp.getHistoricAmountOptional() != null) {
				SourcingTemplateField tf = new SourcingTemplateField(SourcingTemplateFieldName.HISTORIC_AMOUNT, (tfp.getHistoricAmount() != null ? String.valueOf(tfp.getHistoricAmount()) : null), tfp.getHistoricAmountVisible(), tfp.getHistoricAmountDisabled(), tfp.getHistoricAmountOptional(), buyer, sourceForm);
				sourceForm.getFields().add(tf);
			}
			if (tfp.getMinimumSupplierRating() != null || tfp.getMinimumSupplierRatingVisible() != null || tfp.getMinimumSupplierRatingDisabled() != null || tfp.getMinimumSupplierRatingOptional() != null) {
				SourcingTemplateField tf = new SourcingTemplateField(SourcingTemplateFieldName.MINIMUM_SUPPLIER_RATING, (tfp.getMinimumSupplierRating() != null ? String.valueOf(tfp.getMinimumSupplierRating()) : null), tfp.getMinimumSupplierRatingVisible(), tfp.getMinimumSupplierRatingDisabled(), tfp.getMinimumSupplierRatingOptional(), buyer, sourceForm);
				sourceForm.getFields().add(tf);
			}
			if (tfp.getMaximumSupplierRating() != null || tfp.getMaximumSupplierRatingVisible() != null || tfp.getMaximumSupplierRatingDisabled() != null || tfp.getMaximumSupplierRatingOptional() != null) {
				SourcingTemplateField tf = new SourcingTemplateField(SourcingTemplateFieldName.MAXIMUM_SUPPLIER_RATING, (tfp.getMaximumSupplierRating() != null ? String.valueOf(tfp.getMaximumSupplierRating()) : null), tfp.getMaximumSupplierRatingVisible(), tfp.getMaximumSupplierRatingDisabled(), tfp.getMaximumSupplierRatingOptional(), buyer, sourceForm);
				sourceForm.getFields().add(tf);
			}
			// Giving optional as false because optional option is not present in FE. User cannot make this field
			// optional.
			if (tfp.getBaseCurrency() != null || tfp.getBaseCurrencyVisible() != null || tfp.getBaseCurrencyDisabled() != null) {
				SourcingTemplateField tf = new SourcingTemplateField(SourcingTemplateFieldName.BASE_CURRENCY, (tfp.getBaseCurrency() != null ? tfp.getBaseCurrency().getId() : null), tfp.getBaseCurrencyVisible(), tfp.getBaseCurrencyDisabled(), false, buyer, sourceForm);
				sourceForm.getFields().add(tf);
			}
			if (tfp.getEstimatedBudget() != null || tfp.getEstimatedBudgetVisible() != null || tfp.getEstimatedBudgetDisabled() != null || tfp.getEstimatedBudgetOptional() != null) {
				SourcingTemplateField tf = new SourcingTemplateField(SourcingTemplateFieldName.ESTIMATED_BUDGET, (tfp.getEstimatedBudget() != null ? String.valueOf(tfp.getEstimatedBudget()) : null), tfp.getEstimatedBudgetVisible(), tfp.getEstimatedBudgetDisabled(), tfp.getEstimatedBudgetOptional(), buyer, sourceForm);
				sourceForm.getFields().add(tf);
			}
			if (tfp.getProcurementMethod() != null || tfp.getProcurementMethodVisible() != null || tfp.getProcurementMethodDisabled() != null || tfp.getProcurementMethodOptional() != null) {
				SourcingTemplateField tf = new SourcingTemplateField(SourcingTemplateFieldName.PROCUREMENT_METHOD, (tfp.getProcurementMethod() != null ? tfp.getProcurementMethod().getId() : null), tfp.getProcurementMethodVisible(), tfp.getProcurementMethodDisabled(), tfp.getProcurementMethodOptional(), buyer, sourceForm);
				sourceForm.getFields().add(tf);
			}
			if (tfp.getProcurementCategory() != null || tfp.getProcurementCategoryVisible() != null || tfp.getProcurementCategoryDisabled() != null || tfp.getProcurementCategoryOptional() != null) {
				SourcingTemplateField tf = new SourcingTemplateField(SourcingTemplateFieldName.PROCUREMENT_CATEGORY, (tfp.getProcurementCategory() != null ? tfp.getProcurementCategory().getId() : null), tfp.getProcurementCategoryVisible(), tfp.getProcurementCategoryDisabled(), tfp.getProcurementCategoryOptional(), buyer, sourceForm);
				sourceForm.getFields().add(tf);
			}
			if (tfp.getGroupCode() != null || tfp.getGroupCodeVisible() != null || tfp.getGroupCodeDisabled() != null || tfp.getGroupCodeOptional() != null) {
				SourcingTemplateField tf = new SourcingTemplateField(SourcingTemplateFieldName.GROUP_CODE, (tfp.getGroupCode() != null ? tfp.getGroupCode().getId() : null), tfp.getGroupCodeVisible(), tfp.getGroupCodeDisabled(), tfp.getGroupCodeOptional(), buyer, sourceForm);
				sourceForm.getFields().add(tf);
			}

		}

		for (SourcingTemplateField tf : sourceForm.getFields()) {
			LOG.info("Field : " + tf.toLogString());
		}

	}

	@RequestMapping(path = "/sourceTemplateList", method = RequestMethod.GET)
	public String sourceTemplateList(Model model) {
		return "sourceTemplateList";
	}

	@RequestMapping(path = "/sourcingTemplateDetails", method = RequestMethod.GET)
	public String sourceTemplatedetails(Model model) {
		try {
			SourcingFormTemplate sourceForm = new SourcingFormTemplate();
			sourceForm.setStatus(SourcingStatus.DRAFT);
			model.addAttribute("sourceForm", sourceForm);
			model.addAttribute("baseCurrencyList", currencyService.getlActiveCurrencies());
			model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
			List<SourcingFormApprovalUser> approvalUserList = new ArrayList<SourcingFormApprovalUser>();
			List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
			for (UserPojo user : userListSumm) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				approvalUserList.add(new SourcingFormApprovalUser(u));
			}

			model.addAttribute("userList", approvalUserList);
		} catch (Exception e) {
			LOG.info("Error while fetching userList " + e.getMessage());

		}
		List<User> userTeamMemberList = new ArrayList<User>();
		List<TemplateSourcingTeamMembers> assignedTeamMembers = new ArrayList<TemplateSourcingTeamMembers>();
		List<UserPojo> userTeamMember = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		for (UserPojo user : userTeamMember) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			userTeamMemberList.add(u);
		}
		model.addAttribute("userTeamMembers", userTeamMemberList);
		model.addAttribute("assignedTeamMembers", assignedTeamMembers);
		model.addAttribute("teamMember", true);
		List<String> statusList = new ArrayList<>();
		statusList.add("ACTIVE");
		statusList.add("INACTIVE");
		model.addAttribute("statusList", statusList);
		model.addAttribute("createTemplate", messageSource.getMessage("sourcingtemplate.create.template", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("button", messageSource.getMessage("save.next.button", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("isEnableUnitAndCostCorrelation", buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId()));
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		model.addAttribute("assignedUserListDropDown", userList);

		List<User> assignedUserList = new ArrayList<>();
		model.addAttribute("assignedUserList", assignedUserList);

		model.addAttribute("isEnableUnitAndGroupCodeCorr", buyerSettingsService.isEnableUnitAndGroupCodeCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("groupCodeList", groupCodeService.fetchAllActiveGroupCodeForTenantID(SecurityLibrary.getLoggedInUserTenantId()));

		return "sourcingTemplateDetails";
	}

	@ModelAttribute("statusList")
	public List<SourcingStatus> getStatusList() {
		return Arrays.asList(SourcingStatus.values());
	}

	@RequestMapping(path = "/sourcingTemplateData", method = RequestMethod.GET)
	public ResponseEntity<TableData<SourcingFormTemplatePojo>> sourcingTemplateData(TableDataInput input, Model model) {
		try {
			List<SourcingFormTemplate> sourceList1 = sourcingTemplateService.findAllActiveSourcTemplateForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			List<SourcingFormTemplatePojo> pojoList = new ArrayList<SourcingFormTemplatePojo>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			String timeZone = "GMT+8:00";
			for (SourcingFormTemplate sourcingForm : sourceList1) {
				model.addAttribute("isTemplateUsed", sourcingForm.getIsTemplateUsed());
				SourcingFormTemplatePojo pojo = new SourcingFormTemplatePojo();
				pojo.setId(sourcingForm.getId());
				timeZone = buyerSettingsService.getBuyerTimeZoneByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (TimeZone.getTimeZone(timeZone) != null) {
					sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
				}

				String createdDate = sdf.format(sourcingForm.getCreatedDate());
				pojo.setCreatedDate(createdDate);
				pojo.setCreatedBy(sourcingForm.getCreatedBy().getLoginId());
				pojo.setDescription(sourcingForm.getDescription());
				pojo.setFormName(sourcingForm.getFormName());
				if (sourcingForm.getModifiedDate() != null) {
					String modifiedDate = sdf.format(sourcingForm.getModifiedDate());
					pojo.setModifiedDate(modifiedDate);
				}
				if (sourcingForm.getModifiedBy() != null) {
					pojo.setModifiedBy(sourcingForm.getModifiedBy().getLoginId());
				}
				pojo.setStatus(sourcingForm.getStatus().toString());
				pojoList.add(pojo);
			}
			TableData<SourcingFormTemplatePojo> data = new TableData<SourcingFormTemplatePojo>(pojoList);
			long totalFilterCount = sourcingTemplateService.findTotalFilteredTemplatesForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			long totalCount = sourcingTemplateService.findTotalTemplatesForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsFiltered(totalFilterCount);
			data.setRecordsTotal(totalCount);
			List<String> statusList = new ArrayList<>();
			statusList.add("ACTIVE");
			statusList.add("INACTIVE");
			model.addAttribute("statusList", statusList);
			return new ResponseEntity<TableData<SourcingFormTemplatePojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching template list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching template list : " + e.getMessage());
			return new ResponseEntity<TableData<SourcingFormTemplatePojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/deleteSourcingTemplate", method = RequestMethod.GET)
	public String deleteTemplate(@RequestParam String id, Model model) {
		LOG.info("delete Sourcing Form method SourcingForm Id is " + id);
		SourcingFormTemplate sourceForm = null;
		if (id != null) {
			sourceForm = sourcingTemplateService.getSourcingFormbyId(id);
		}
		LOG.info("Check if template is Used " + sourceForm.getIsTemplateUsed());
		LOG.info(sourceForm.getId());
		if (sourceForm != null) {
			try {
				LOG.info(sourceForm.getIsTemplateUsed());
				if (sourceForm.getIsTemplateUsed()) {
					throw new ApplicationException();
				}
				sourcingTemplateService.deleteSourcingForm(sourceForm);
				model.addAttribute("success", messageSource.getMessage("template.delete.success", new Object[] {}, Global.LOCALE));
				LOG.info("deleted Sorcing Form  " + sourcingTemplateService.getSourcingFormbyId(id));

			} catch (DataIntegrityViolationException e) {
				LOG.error("Error while deleting Template , " + e.getMessage(), e);
				model.addAttribute("error", messageSource.getMessage("template.error.delete.child.exist", new Object[] { sourceForm.getFormName() }, Global.LOCALE));
			} catch (ApplicationException e) {
				LOG.error("Error while deleting SourcingTemplate  [ " + sourceForm.getFormName() + " ]" + e.getMessage(), e);
				model.addAttribute("error", messageSource.getMessage("used.template.delete", new Object[] { sourceForm.getFormName() }, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error while deleting SourcingTemplate  [ " + sourceForm.getFormName() + " ]" + e.getMessage(), e);
				model.addAttribute("error", messageSource.getMessage("template.error.delete", new Object[] { sourceForm.getFormName() }, Global.LOCALE));
			}

		}
		return "sourceTemplateList";
	}

	@RequestMapping(path = "/editSourcingTemplate", method = RequestMethod.GET)
	public String editsourcingTemplate(@RequestParam String id, Model model) {
		LOG.info("Sourcing Form id ::" + id);
		SourcingFormTemplate sourceForm = sourcingTemplateService.getSourcingFormbyId(id);
		LOG.info("SourceForm Decimal  " + sourceForm.getDecimal());
		LOG.info("Name " + sourceForm.getFormName() + " TeanatId" + sourceForm.getTenantId() + " " + sourceForm.getSourcingFormApproval());
		constructFieldsForDisplay(sourceForm);
		model.addAttribute("sourceForm", sourceForm);
		model.addAttribute("event", sourceForm);
		model.addAttribute("sourcingForm", sourceForm);
		model.addAttribute("prTemplate", sourceForm);
		model.addAttribute("templateId", sourceForm.getId());
		model.addAttribute("baseCurrencyList", currencyService.getlActiveCurrencies());

		List<UserPojo> userList = userService.fetchAllUsersForTenantForSourcingTemplate(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER, id);

		model.addAttribute("assignedUserListDropDown", userList);
		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isEnableUnitAndCostCorrelation", enableCorrelation);
		if (CollectionUtil.isNotEmpty(sourceForm.getFields())) {
			for (SourcingTemplateField tf : sourceForm.getFields()) {
				if (tf.getFieldName() == SourcingTemplateFieldName.BUSINESS_UNIT) {
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
							// model.addAttribute("costCenterList", assignedCostList);
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
		// model.addAttribute("costCenterList",
		// costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));

		Boolean enableGPCCorrelation = buyerSettingsService.isEnableUnitAndGroupCodeCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isEnableUnitAndGroupCodeCorr", enableGPCCorrelation);
		List<GroupCode> groupCodeList = groupCodeService.fetchAllActiveGroupCodeForTenantID(SecurityLibrary.getLoggedInUserTenantId());
		if (CollectionUtil.isNotEmpty(sourceForm.getFields())) {

			for (SourcingTemplateField tf : sourceForm.getFields()) {
				if (SourcingTemplateFieldName.BUSINESS_UNIT == tf.getFieldName() && tf.getDefaultValue() != null && Boolean.TRUE == enableGPCCorrelation) {
					groupCodeList = groupCodeService.getGroupCodeIdByBusinessId(tf.getDefaultValue());

				}
				// ::
				if (SourcingStatus.DRAFT != sourceForm.getStatus()) {
					if (SourcingTemplateFieldName.GROUP_CODE == tf.getFieldName() && tf.getDefaultValue() != null) {
						GroupCode gc = groupCodeService.getGroupCodeByGCId(tf.getDefaultValue());
						if (Status.INACTIVE == gc.getStatus() && !groupCodeList.contains(gc)) {
							groupCodeList.add(gc);
						}
					}
				}
			}
		}
		model.addAttribute("groupCodeList", groupCodeList);

		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("button", messageSource.getMessage("update.next.button", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("templateFields", sourceForm.getFields());
		model.addAttribute("isTemplateUsed", sourceForm.getIsTemplateUsed());
		model.addAttribute("createTemplate", messageSource.getMessage("sourcingtemplate.update.template", new Object[] {}, LocaleContextHolder.getLocale()));
		LOG.info("check if teplate id used " + sourceForm.getIsTemplateUsed());
		// List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);

		List<SourcingFormApprovalUser> templateApprovalUserList = new ArrayList<SourcingFormApprovalUser>();
		if (sourceForm.getSourcingFormApproval() != null) {
			for (SourcingTemplateApproval approval : sourceForm.getSourcingFormApproval()) {
				for (SourcingFormApprovalUser approvalUser : approval.getApprovalUsers()) {
					User u = new User(approvalUser.getUser().getId(), approvalUser.getUser().getLoginId(), approvalUser.getUser().getName(), approvalUser.getUser().getCommunicationEmail(),
							approvalUser.getUser().getEmailNotifications(), approvalUser.getUser().getTenantId(), approvalUser.getUser().isDeleted());
					SourcingFormApprovalUser obj = new SourcingFormApprovalUser(u);
					if (!templateApprovalUserList.contains(obj)) {
						templateApprovalUserList.add(obj);
					}
				}
			}
		}

		for (UserPojo user : userListSumm) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			SourcingFormApprovalUser obj = new SourcingFormApprovalUser(u);
			if (!templateApprovalUserList.contains(obj)) {
				templateApprovalUserList.add(obj);
			}
		}
		List<String> assignedUserId = sourcingTemplateService.getTemplateByUserIdAndTemplateId(id, SecurityLibrary.getLoggedInUserTenantId());
		List<User> assignedUserList = new ArrayList<>();
		List<String> selectedOnes = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(assignedUserId)) {
			for (String assgnedUser : assignedUserId) {
				User user = userService.getUsersNameAndId(assgnedUser);
				assignedUserList.add(user);
				selectedOnes.add(user.getId());
			}
		}
		model.addAttribute("assignedUserList", assignedUserList);

		List<User> userTeamMemberList = new ArrayList<User>();
		List<User> remainingUser = new ArrayList<User>();
		List<TemplateSourcingTeamMembers> assignedTeamMembers = new ArrayList<TemplateSourcingTeamMembers>();
		List<UserPojo> userTeamMember = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		try {

			for (UserPojo user : userTeamMember) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				userTeamMemberList.add(u);
			}

			for (TemplateSourcingTeamMembers team : sourceForm.getTeamMembers()) {
				assignedTeamMembers.add(team);
				remainingUser.add((User) team.getUser().clone());
				LOG.info("(User)team.getUser().clone()..........." + team.getUser().getLoginId());
			}
			userTeamMemberList.removeAll(remainingUser);
			model.addAttribute("userTeamMembers", userTeamMemberList);
			model.addAttribute("assignedTeamMembers", assignedTeamMembers);
			model.addAttribute("teamMember", true);
			model.addAttribute("userList", templateApprovalUserList);

		} catch (Exception e) {
			LOG.info("userTeamMemberList.............." + e.getMessage(), e);
		}
		return "sourcingTemplateDetails";
	}

	private void constructFieldsForDisplay(SourcingFormTemplate sourceForm) {

		TemplateFieldPojo tfp = sourceForm.getTemplateFieldBinding();
		if (CollectionUtil.isNotEmpty(sourceForm.getFields())) {
			tfp = new TemplateFieldPojo();
			for (SourcingTemplateField tf : sourceForm.getFields()) {
				LOG.info("tf.getFieldName() : " + tf.getFieldName());
				switch (tf.getFieldName()) {

				case BUDGET_AMOUNT:
					if (tf.getDefaultValue() != null) {
						tfp.setBudgetAmount(new BigDecimal(tf.getDefaultValue()));
					}
					tfp.setBudgetAmountVisible(tf.getVisible());
					tfp.setBudgetAmountOptional(tf.getOptional());
					tfp.setBudgetAmountDisabled(tf.getReadOnly());
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
				case HISTORIC_AMOUNT:
					if (tf.getDefaultValue() != null) {
						tfp.setHistoricAmount(new BigDecimal(tf.getDefaultValue()));
					}
					tfp.setHistoricAmountVisible(tf.getVisible());
					tfp.setHistoricAmountOptional(tf.getOptional());
					tfp.setHistoricAmountDisabled(tf.getReadOnly());
					break;
				case MINIMUM_SUPPLIER_RATING:
					if (tf.getDefaultValue() != null) {
						tfp.setMinimumSupplierRating(tf.getDefaultValue());
					}
					tfp.setMinimumSupplierRatingVisible(tf.getVisible());
					tfp.setMinimumSupplierRatingDisabled(tf.getReadOnly());
					tfp.setMinimumSupplierRatingOptional(tf.getOptional());
					break;
				case MAXIMUM_SUPPLIER_RATING:
					if (tf.getDefaultValue() != null) {
						tfp.setMaximumSupplierRating(tf.getDefaultValue());
					}
					tfp.setMaximumSupplierRatingVisible(tf.getVisible());
					tfp.setMaximumSupplierRatingDisabled(tf.getReadOnly());
					tfp.setMaximumSupplierRatingOptional(tf.getOptional());
					break;
				case BASE_CURRENCY:
					if (tf.getDefaultValue() != null) {
						tfp.setBaseCurrency(currencyService.getCurrency(tf.getDefaultValue()));
					}
					tfp.setBaseCurrencyVisible(tf.getVisible());
					tfp.setBaseCurrencyDisabled(tf.getReadOnly());
					break;
				case ESTIMATED_BUDGET:
					if (tf.getDefaultValue() != null) {
						tfp.setEstimatedBudget(new BigDecimal(tf.getDefaultValue()));
					}
					tfp.setEstimatedBudgetVisible(tf.getVisible());
					tfp.setEstimatedBudgetOptional(tf.getOptional());
					tfp.setEstimatedBudgetDisabled(tf.getReadOnly());
					break;
				case PROCUREMENT_METHOD:
					if (tf.getDefaultValue() != null) {
						tfp.setProcurementMethod(procurementMethodService.getProcurementMethodById(tf.getDefaultValue()));
					}
					tfp.setProcurementMethodVisible(tf.getVisible());
					tfp.setProcurementMethodOptional(tf.getOptional());
					tfp.setProcurementMethodDisabled(tf.getReadOnly());
					break;
				case PROCUREMENT_CATEGORY:
					if (tf.getDefaultValue() != null) {
						tfp.setProcurementCategory(procurementCategoriesService.getProcurementCategoriesById(tf.getDefaultValue()));
					}
					tfp.setProcurementCategoryVisible(tf.getVisible());
					tfp.setProcurementCategoryOptional(tf.getOptional());
					tfp.setProcurementCategoryDisabled(tf.getReadOnly());
					break;
				case GROUP_CODE:
					if (tf.getDefaultValue() != null) {
						tfp.setGroupCode(groupCodeService.getGroupCodeById(tf.getDefaultValue()));
					}
					tfp.setGroupCodeVisible(tf.getVisible());
					tfp.setGroupCodeOptional(tf.getOptional());
					tfp.setGroupCodeDisabled(tf.getReadOnly());
					break;
				default:
					break;
				}
			}

		}
		sourceForm.setTemplateFieldBinding(tfp);

	}

	@RequestMapping(path = "/updateSourcingForm", method = RequestMethod.POST)
	public String updateSourcingForm(@ModelAttribute SourcingFormTemplate sf, Model model) {
		LOG.info("Sourcing Form Id  is " + sf.getId());
		try {
			SourcingFormTemplate persistObj = sourcingTemplateService.getSourcingFormbyId(sf.getId());
			persistObj.setFormName(sf.getFormName());
			persistObj.setDescription(sf.getDescription());
			persistObj.setApprovalsCount(sf.getApprovalsCount());
			persistObj.setFields(sf.getFields());
			List<SourcingTemplateApproval> app = persistObj.getSourcingFormApproval();
			LOG.info("size of approval list " + app.size());
			// update List of approvals
			if (app != null && sf.getSourcingFormApproval() != null) {
				app.addAll(sf.getSourcingFormApproval());
			}
			persistObj.setModifiedDate(new Date());
			persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
			// update SourcingForm
			SourcingFormTemplate sourceForm = sourcingTemplateService.SaveOrupdateSourcingTemplate(persistObj);
			LOG.info(sourceForm.getClass().getName());
			model.addAttribute("sourceForm", sourceForm);
			LOG.info("Sourcing Form Id  is " + sourceForm.getId() + "Name " + sourceForm.getFormName() + " Desc " + sourceForm.getDescription() + "Modified by " + sourceForm.getModifiedBy() + " modified Date " + sourceForm.getModifiedDate());
		} catch (Exception e) {
			LOG.error("error while update Template " + e.getMessage(), e);
		}

		return "updateSourcingForm";
	}

	@RequestMapping(value = "/copySourcingTemplate", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> copySourcingTemplate(@RequestParam(value = "templateId") String templateId, @RequestParam(value = "templateName") String templateName, @RequestParam(value = "templateDesc") String templateDesc) {
		HttpHeaders headers = new HttpHeaders();
		String newTemplateId = "";
		LOG.info("copy Sourcing template method is called  and SourcingForm id " + templateId);
		try {
			SourcingFormTemplate sf = new SourcingFormTemplate();
			sf.setFormName(templateName);
			sf.setDescription(templateDesc);

			if (!sourcingTemplateService.isExists(templateId, sf.getFormName())) {

				SourcingFormTemplate form = sourcingTemplateService.copyTemplate(templateId, sf, SecurityLibrary.getLoggedInUser());

				newTemplateId = sf.getId();
				LOG.info("Copied Template Created and Saved Sucessfully  :" + form.getFormName());
				headers.add("success", messageSource.getMessage("template.save.success", new Object[] { form.getFormName() }, Global.LOCALE));
			} else {

				headers.add("error", messageSource.getMessage("template.error.duplicate", new Object[] { sf.getFormName() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while save as : " + e.getMessage(), e);
			headers.add("error", "Error while Copying Pr template : " + e.getMessage());
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<String>(newTemplateId, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/finishTemplate/{formId}", method = RequestMethod.GET)
	public String finishTemplate(@PathVariable("formId") String formId, RedirectAttributes model) {
		LOG.info("finish template method is called with form id " + formId);
		try {
			SourcingFormTemplate template = sourcingTemplateService.getSourcingFormbyId(formId);
			if ((template.getStatus() == SourcingStatus.INACTIVE) && (template.getIsTemplateUsed() == Boolean.TRUE)) {
				LOG.info("Template is inactiveted");
				throw new InactiveTemplateException("Can not finish inactive template");
			}
			List<SourcingTemplateCq> cqList = sourcingTemplateService.getAllQuestionnarie(formId);
			long cqCount = sourcingTemplateService.getBqCount(formId);
			if (!(cqCount > 0)) {
				throw new ApplicationException();
			}
			// Validate Cq items inside section
			List<String> notItemSectionAddedCqs = cqService.getNotSectionItemAddedCqIdsByFormId(formId);
			if (CollectionUtil.isNotEmpty(notItemSectionAddedCqs)) {
				String names = org.apache.commons.lang.StringUtils.join(notItemSectionAddedCqs, ",");
				model.addAttribute("error", messageSource.getMessage("request.cq.item.not.sectionAdded", new Object[] { names }, Global.LOCALE));
				return "redirect:/buyer/sourcingFormCqList/" + formId;
			}

			if (CollectionUtil.isNotEmpty(cqList)) {
				for (SourcingTemplateCq cq : cqList) {
					List<SourcingTemplateCqItem> cqItemList = cqService.findAllCqItembyCqId(cq.getId());
					if (CollectionUtil.isNotEmpty(cqItemList)) {
						template.setStatus(SourcingStatus.ACTIVE);
						template.setCqCompleted(true);
						template = sourcingTemplateService.updateSourcingTemplate(template);
						LOG.info("Status after finishing the  Form " + template.getStatus());
						LOG.info("Cq List is not null ");
						model.addFlashAttribute("success", messageSource.getMessage("flashsuccess.template.created", new Object[] { template.getFormName() }, Global.LOCALE));
						return "redirect:/buyer/sourceTemplateList";
					} else {
						throw new ApplicationException();
					}
				}
			} else {
				throw new ApplicationException();
			}
		} catch (InactiveTemplateException e) {
			model.addFlashAttribute("error", messageSource.getMessage("inactice.template.finish", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/sourcingFormCqList/" + formId;
		} catch (ApplicationException e) {
			model.addFlashAttribute("error", messageSource.getMessage("cq.cqItem.mandatory", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/sourcingFormCqList/" + formId;
		} catch (Exception e) {
			model.addFlashAttribute("error", messageSource.getMessage("error.finishing.template", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/sourcingFormCqList/" + formId;
		}

		return "redirect:/buyer/sourceTemplateList";
	}

	@RequestMapping(path = "/activeSourcingFormTemplate", method = RequestMethod.POST)
	public String activeSourcingFormTemplate(@RequestParam("templateId") String templateId, RedirectAttributes redirect) {
		LOG.info("Sourcing Form Id  is " + templateId);

		try {
			LOG.info("Copied Template Created ");
			SourcingFormTemplate persistObj = sourcingTemplateService.getSourcingFormbyId(templateId);

			if (persistObj.getStatus() == SourcingStatus.DRAFT) {
				LOG.info("Copied Template Created ");
				persistObj.setStatus(SourcingStatus.ACTIVE);
				redirect.addFlashAttribute("success", messageSource.getMessage("flashsuccess.template.activated", new Object[] {}, Global.LOCALE));
			} else if (persistObj.getStatus() == SourcingStatus.ACTIVE) {
				persistObj.setStatus(SourcingStatus.INACTIVE);
				
				BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.INACTIVE, "Sourcing Form Template '" +persistObj.getFormName()+ "' is '"+persistObj.getStatus()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SourcingTemplate);
				buyerAuditTrailDao.save(ownerAuditTrail);
				
				redirect.addFlashAttribute("success", messageSource.getMessage("flashsuccess.template.uploaded", new Object[] {}, Global.LOCALE));
			} else if (persistObj.getStatus() == SourcingStatus.INACTIVE) {
				persistObj.setStatus(SourcingStatus.ACTIVE);
				
				BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.ACTIVE, "Sourcing Form Template '" +persistObj.getFormName()+ "' is '"+persistObj.getStatus()+"' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SourcingTemplate);
				buyerAuditTrailDao.save(ownerAuditTrail);
				
				redirect.addFlashAttribute("success", messageSource.getMessage("flashsuccess.template.uploaded", new Object[] {}, Global.LOCALE));
			}
			persistObj.setModifiedDate(new Date());
			persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
			sourcingTemplateService.updateSourcingTemplate(persistObj);

			return "redirect:/buyer/sourceTemplateList";
		} catch (Exception e) {
			redirect.addFlashAttribute("error", messageSource.getMessage("flasherror.changing.status.template", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while update Template " + e.getMessage(), e);

			return "redirect:/buyer/editSourcingTemplate/" + templateId;
		}
	}

}
