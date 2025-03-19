package com.privasia.procurehere.web.controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.TemplateApprovalUser;
import com.privasia.procurehere.core.entity.TemplateAwardApproval;
import com.privasia.procurehere.core.entity.TemplateAwardApprovalUser;
import com.privasia.procurehere.core.entity.TemplateEventApproval;
import com.privasia.procurehere.core.entity.TemplateEventTeamMembers;
import com.privasia.procurehere.core.entity.TemplateField;
import com.privasia.procurehere.core.entity.TemplateSuspensionApproval;
import com.privasia.procurehere.core.entity.TemplateSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.TemplateUnmaskUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionConsolePriceVenderType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.DeclarationType;
import com.privasia.procurehere.core.enums.DurationType;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PreBidByType;
import com.privasia.procurehere.core.enums.RfxTemplateFieldName;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TimeExtensionType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.RfxEnvelopPojo;
import com.privasia.procurehere.core.pojo.RfxTemplatePojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TemplateFieldPojo;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.DeclarationService;
import com.privasia.procurehere.service.GroupCodeService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.ProcurementCategoriesService;
import com.privasia.procurehere.service.ProcurementMethodService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import com.privasia.procurehere.web.editors.CostCenterEditor;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.DeclarationEditor;
import com.privasia.procurehere.web.editors.GroupCodeEditor;
import com.privasia.procurehere.web.editors.IndustryCategoryEditor;
import com.privasia.procurehere.web.editors.ProcurementCategoriesEditor;
import com.privasia.procurehere.web.editors.ProcurementMethodEditor;
import com.privasia.procurehere.web.editors.TemplateApprovalEditor;
import com.privasia.procurehere.web.editors.TemplateAwardApprovalUserEditor;
import com.privasia.procurehere.web.editors.TemplateSuspensionApprovalEditor;
import com.privasia.procurehere.web.editors.TemplateUnmaskUserEditor;

/**
 * @author Nitin Otageri
 */
@Controller
@RequestMapping("/buyer/rfxTemplate")
public class RfxTemplateController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	IndustryCategoryEditor industryCategoryEditor;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	CountryService countryService;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	CostCenterEditor costCenterEditor;

	@Resource
	MessageSource messageSource;

	@Autowired
	UserService userService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	TemplateApprovalEditor templateApprovalEditor;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	BusinessUnitEditor businessUnitEditor;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	TemplateUnmaskUserEditor templateUnmaskUserEditor;

	@Autowired
	DeclarationService declarationService;

	@Autowired
	DeclarationEditor declarationEditor;

	@Autowired
	TemplateSuspensionApprovalEditor templateSuspensionApprovalEditor;

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

	@Autowired
	TemplateAwardApprovalUserEditor templateAwardApprovalEditor;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(IndustryCategory.class, industryCategoryEditor);
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(CostCenter.class, costCenterEditor);
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		binder.registerCustomEditor(Country.class, countryEditor);
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(TemplateApprovalUser.class, templateApprovalEditor);
		binder.registerCustomEditor(TemplateUnmaskUser.class, templateUnmaskUserEditor);
		binder.registerCustomEditor(Declaration.class, declarationEditor);
		binder.registerCustomEditor(ProcurementMethod.class, procurementMethodEditor);
		binder.registerCustomEditor(ProcurementCategories.class, procurementCategoriesEditor);
		binder.registerCustomEditor(TemplateSuspensionApprovalUser.class, templateSuspensionApprovalEditor);
		binder.registerCustomEditor(TemplateAwardApprovalUser.class, templateAwardApprovalEditor);
		binder.registerCustomEditor(GroupCode.class, groupCodeEditor);
	}

	@ModelAttribute("rfxTypeList")
	public List<RfxTypes> getRfxTypeList() {
		return Arrays.asList(RfxTypes.values());
	}

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(value = "searchCategory", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<IndustryCategory>> searchCategory(@RequestParam("search") String search) {
		List<IndustryCategory> bicList = industryCategoryService.findIndustryCategoryByNameAndTenantId(search, SecurityLibrary.getLoggedInUserTenantId());
		return new ResponseEntity<List<IndustryCategory>>(bicList, HttpStatus.OK);
	}

	@RequestMapping(path = "/rfxTemplate", method = RequestMethod.GET)
	public ModelAndView createRfxTemplate(Model model) {
		LOG.info("Create RfxTemplate called");
		try {
			// List<User> userList =
			// userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null); // Aprovars
			List<TemplateApprovalUser> templateApprovalUserList = new ArrayList<TemplateApprovalUser>();
			List<User> userTeamMemberList = new ArrayList<User>();
			LOG.info("templateApprovalUserList............." + templateApprovalUserList.size());
			for (UserPojo user : userListSumm) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				templateApprovalUserList.add(new TemplateApprovalUser(u));
			}

			List<TemplateSuspensionApprovalUser> templSuspApprlUserList = new ArrayList<TemplateSuspensionApprovalUser>();
			LOG.info("templSuspApprlUserList ............." + templSuspApprlUserList.size());
			for (UserPojo user : userListSumm) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				templSuspApprlUserList.add(new TemplateSuspensionApprovalUser(u));
			}

			List<TemplateAwardApprovalUser> templAwardApprlUserList = new ArrayList<TemplateAwardApprovalUser>();
			LOG.info("templAwardApprlUserList ............." + templAwardApprlUserList.size());
			for (UserPojo user : userListSumm) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				templAwardApprlUserList.add(new TemplateAwardApprovalUser(u));
			}

			List<TemplateEventTeamMembers> assignedTeamMembers = new ArrayList<TemplateEventTeamMembers>();
			List<User> revertBidUser = new ArrayList<User>(); // userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			List<User> evaluationConclusionUsers = new ArrayList<User>();
			// team members (Normal)
			List<UserPojo> userTeamMember = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
			for (UserPojo user : userTeamMember) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				userTeamMemberList.add(u);
				revertBidUser.add(u);
				evaluationConclusionUsers.add(u);
			}

			model.addAttribute("userTeamMembers", userTeamMemberList);
			model.addAttribute("assignedTeamMembers", assignedTeamMembers);
			model.addAttribute("teamMember", true);
			model.addAttribute("userList", userListSumm);
			model.addAttribute("revertBidUser", revertBidUser);
			model.addAttribute("evaluationConclusionUsers", evaluationConclusionUsers);
			model.addAttribute("templateApprovalUserList", templateApprovalUserList);
			model.addAttribute("templSuspApprlUserList", templSuspApprlUserList);
			model.addAttribute("templAwardApprlUserList", templAwardApprlUserList);

			model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
			RfxTemplate template = new RfxTemplate();
			Buyer buyer = new Buyer();
			buyer.setId(SecurityLibrary.getLoggedInUserTenantId());
			template.setBuyer(buyer);
			model.addAttribute("decimal", buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId()).getDecimal());
			loadDefaultDataInModel(model, template);
			List<UserPojo> usersList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
			model.addAttribute("userlistForAssigned", usersList);
		} catch (Exception e) {
			LOG.info("error while createRfxTemplate......... " + e.getMessage(), e);
		}
		return new ModelAndView("rfxTemplate");
	}

	@RequestMapping(path = "/editRfxTemplate", method = {RequestMethod.GET, RequestMethod.POST})
	public String editRfxTemplate(@RequestParam String id, Model model) {
		LOG.info("Getting the RfxTemplate. : " + id);
		RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(id, SecurityLibrary.getLoggedInUserTenantId());

		// Error condition. Send the user back to listing screen.
		// List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<String> assignedUserId = rfxTemplateService.getTemplateByUserIdAndTemplateId(id, SecurityLibrary.getLoggedInUserTenantId());
		List<User> assignedUserList = new ArrayList<>();
		List<String> selectedOnes = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(assignedUserId)) {
			for (String assgnedUser : assignedUserId) {
				User user = userService.getUsersNameAndId(assgnedUser);
				assignedUserList.add(user);
				selectedOnes.add(user.getId());
			}
		}

		List<User> revertBidUser = new ArrayList<User>(); // .fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<User> evaluationConclusionUsers = new ArrayList<User>(); // .fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());

		model.addAttribute("assignedUserList", assignedUserList);
		if (rfxTemplate == null) {
			model.addAttribute("error", messageSource.getMessage("common.no.records.found", new Object[] { id }, Global.LOCALE));
			return "redirect:rfxTemplateList";
		}
		// Fix for old templates that do not have Event Publish Type values as this was introduced in Sept 2019
		if (rfxTemplate.getPrivateEvent() == null && rfxTemplate.getPublicEvent() == null && rfxTemplate.getPartialEvent() == null) {
			rfxTemplate.setPrivateEvent(true);
		}
		if (rfxTemplate.getPrivateEvent() == Boolean.FALSE && rfxTemplate.getPublicEvent() == Boolean.FALSE && rfxTemplate.getPartialEvent() == Boolean.FALSE) {
			rfxTemplate.setPrivateEvent(true);
		}

		// List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<User> userTeamMemberList = new ArrayList<User>();
		List<User> remainingUser = new ArrayList<User>();
		List<TemplateEventTeamMembers> assignedTeamMembers = new ArrayList<TemplateEventTeamMembers>();
		// List<User> userTeamMember =
		// userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> userTeamMember = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		try {

			for (UserPojo user : userTeamMember) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				userTeamMemberList.add(u);
				revertBidUser.add(u);
				evaluationConclusionUsers.add(u);
			}

			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
				assignedTeamMembers.add(team);
				remainingUser.add((User) team.getUser().clone());
			}

			for (TemplateUnmaskUser us : rfxTemplate.getUnMaskedUsers()) {
				User user = us.getUser();
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.getEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!revertBidUser.contains(u)) {
					revertBidUser.add(u);
				}
			}

			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.getEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!evaluationConclusionUsers.contains(u)) {
					evaluationConclusionUsers.add(u);
				}
			}

			userTeamMemberList.removeAll(remainingUser);
			model.addAttribute("userTeamMembers", userTeamMemberList);
			model.addAttribute("assignedTeamMembers", assignedTeamMembers);

			model.addAttribute("revertBidUser", revertBidUser);
			model.addAttribute("evaluationConclusionUsers", evaluationConclusionUsers);
			model.addAttribute("teamMember", true);
		} catch (Exception e) {
			LOG.info("userTeamMemberList.............." + e.getMessage(), e);
		}

		switch (rfxTemplate.getType()) {
		case RFA: {
			Integer assignedCount = rfaEventService.findAssignedTemplateCount(id);
			model.addAttribute("assignedCount", assignedCount);
			break;
		}
		case RFI: {
			Integer assignedCount = rfiEventService.findAssignedTemplateCount(id);
			model.addAttribute("assignedCount", assignedCount);

			break;
		}
		case RFP: {
			Integer assignedCount = rfpEventService.findAssignedTemplateCount(id);
			model.addAttribute("assignedCount", assignedCount);

			break;
		}
		case RFQ: {
			Integer assignedCount = rfqEventService.findAssignedTemplateCount(id);
			model.addAttribute("assignedCount", assignedCount);

			break;
		}
		case RFT: {
			Integer assignedCount = rftEventService.findAssignedTemplateCount(id);

			model.addAttribute("assignedCount", assignedCount);
			break;
		}
		default:
			break;
		}

		// List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<TemplateApprovalUser> templateApprovalUserList = new ArrayList<TemplateApprovalUser>();
		List<UserPojo> approvalMember = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
		List<User> userAppList = new ArrayList<User>();
		List<User> suspApprovalUsers = new ArrayList<User>();
		List<User> awardApprovalUsers = new ArrayList<User>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			for (TemplateEventApproval approval : rfxTemplate.getApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (TemplateApprovalUser user : approval.getApprovalUsers()) {
						User u = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
						if (!userAppList.contains(u)) {
							userAppList.add(u);
						}
					}
				}
			}
		}

		List<TemplateSuspensionApprovalUser> templSuspApprlUserList = new ArrayList<TemplateSuspensionApprovalUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getSuspensionApprovals())) {
			for (TemplateSuspensionApproval approval : rfxTemplate.getSuspensionApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (TemplateSuspensionApprovalUser user : approval.getApprovalUsers()) {
						User u = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(),user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
						if (!suspApprovalUsers.contains(u)) {
							suspApprovalUsers.add(u);
						}
					}
				}
			}
		}

		List<TemplateAwardApprovalUser> templAwardApprlUserList = new ArrayList<TemplateAwardApprovalUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getAwardApprovals())) {
			for (TemplateAwardApproval approval : rfxTemplate.getAwardApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (TemplateAwardApprovalUser user : approval.getApprovalUsers()) {
						User u = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
						if (!awardApprovalUsers.contains(u)) {
							awardApprovalUsers.add(u);
						}
					}
				}
			}
		}

		for (UserPojo user : approvalMember) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			if (!userAppList.contains(u)) {
				userAppList.add(u);
			}
		}

		model.addAttribute("userList", userAppList);
		model.addAttribute("templateApprovalUserList", templateApprovalUserList);
		model.addAttribute("templSuspApprlUserList", templSuspApprlUserList);
		model.addAttribute("templAwardApprlUserList", templAwardApprlUserList);
		model.addAttribute("suspTemplUserList", suspApprovalUsers);
		model.addAttribute("awardTemplUserList", awardApprovalUsers);

		List<UserPojo> finalUserList = userService.fetchAllUsersForTenantForRfxTemplate(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER, id);
		model.addAttribute("userlistForAssigned", finalUserList);
		if(rfxTemplate != null){
			model.addAttribute("template", rfxTemplate);
		}
		constructFieldsForDisplay(rfxTemplate);

		// This attachment is required for Buyer.id only. It wont be updated
		// into DB so no worries.

		List<RfxEnvelopPojo> envlopeList = new ArrayList<RfxEnvelopPojo>();

		if (StringUtils.checkString(rfxTemplate.getRfxEnvelope1()).length() > 0) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope1());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence1());
			envlopeList.add(pojo);
		}

		if (StringUtils.checkString(rfxTemplate.getRfxEnvelope2()).length() > 0) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope2());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence2());
			envlopeList.add(pojo);
		}
		if (StringUtils.checkString(rfxTemplate.getRfxEnvelope3()).length() > 0) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope3());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence3());
			envlopeList.add(pojo);
		}

		if (StringUtils.checkString(rfxTemplate.getRfxEnvelope4()).length() > 0) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope4());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence4());
			envlopeList.add(pojo);
		}

		if (StringUtils.checkString(rfxTemplate.getRfxEnvelope5()).length() > 0) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope5());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence5());
			envlopeList.add(pojo);
		}

		if (StringUtils.checkString(rfxTemplate.getRfxEnvelope6()).length() > 0) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope6());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence6());
			envlopeList.add(pojo);
		}

		model.addAttribute("envlopeList", envlopeList);

		Buyer buyer = new Buyer();
		buyer.setId(SecurityLibrary.getLoggedInUserTenantId());
		rfxTemplate.setBuyer(buyer);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("decimal", buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId()).getDecimal());

		loadDefaultDataInModel(model, rfxTemplate);

		return "rfxTemplate";
	}

	/**
	 * @param model
	 * @param rfxTemplate
	 */
	private void loadDefaultDataInModel(Model model, RfxTemplate rfxTemplate) {
		List<Declaration> evalDeclarationList = declarationService.getDeclarationsByTypeForTenant(DeclarationType.EVALUATION_PROCESS, SecurityLibrary.getLoggedInUserTenantId());
		List<Declaration> supDeclarationList = declarationService.getDeclarationsByTypeForTenant(DeclarationType.SUPPLIER_ACCEPTANCE, SecurityLibrary.getLoggedInUserTenantId());

		Declaration selectedEvalDecl = null;
		Declaration selectedSupDecl = null;

		if (CollectionUtil.isNotEmpty(rfxTemplate.getFields())) {
			for (TemplateField tf : rfxTemplate.getFields()) {
				if (tf.getFieldName() == RfxTemplateFieldName.EVALUATION_PROCESS_DECLARATION && StringUtils.checkString(tf.getDefaultValue()).length() > 0) {
					selectedEvalDecl = declarationService.getDeclarationById(tf.getDefaultValue());
				}
				if (tf.getFieldName() == RfxTemplateFieldName.SUPPLIER_ACCEPTANCE_DECLARATION && StringUtils.checkString(tf.getDefaultValue()).length() > 0) {
					selectedSupDecl = declarationService.getDeclarationById(tf.getDefaultValue());
				}
			}
		}

		if (selectedEvalDecl != null && !evalDeclarationList.contains(selectedEvalDecl)) {
			evalDeclarationList.add(selectedEvalDecl);
		}

		if (selectedSupDecl != null && !supDeclarationList.contains(selectedSupDecl)) {
			supDeclarationList.add(selectedSupDecl);
		}

		model.addAttribute("currencyList", currencyService.getlActiveCurrencies());

		Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("isEnableUnitAndCostCorrelation", enableCorrelation);
		if (rfxTemplate.getId() != null) {
			if (CollectionUtil.isNotEmpty(rfxTemplate.getFields())) {
				for (TemplateField tf : rfxTemplate.getFields()) {
					if (tf.getFieldName() == RfxTemplateFieldName.BUSINESS_UNIT) {
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
								model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
							}
						} else {
							model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
						}
					}
				}
			}
		} else {
			model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		}
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("evaluationDeclaratonList", evalDeclarationList);
		model.addAttribute("supplierDeclaratonList", supDeclarationList);
		model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));

		ObjectMapper mapperObj = new ObjectMapper();
		String jsonIndusList = "";
		List<IndustryCategory> icList = new ArrayList<IndustryCategory>();
		try {
			if (rfxTemplate.getId() != null) {
				if (CollectionUtil.isNotEmpty(rfxTemplate.getFields())) {
					for (TemplateField tf : rfxTemplate.getFields()) {
						if (tf.getFieldName() == RfxTemplateFieldName.EVENT_CATEGORY) {
							if (tf.getDefaultValue() != null) {
								String[] icArr = tf.getDefaultValue().split(",");
								if (icArr != null) {
									for (String icId : icArr) {
										IndustryCategory ic = industryCategoryService.getIndustryCategoryById(icId);
										icList.add(ic.createShallowCopy());
									}
									jsonIndusList = mapperObj.writeValueAsString(icList);
								}

							}
						}
					}
				}
			}
		} catch (JsonProcessingException e) {
			LOG.error("Error while converting industry category to json object :" + e.getMessage(), e);
		}
		model.addAttribute("indusCatList", jsonIndusList);

		List<GroupCode> groupCodeList = groupCodeService.fetchAllActiveGroupCodeForTenantID(SecurityLibrary.getLoggedInUserTenantId());
		Boolean enableGPCodeCorrelation = buyerSettingsService.isEnableUnitAndGroupCodeCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());

		model.addAttribute("isEnableUnitAndGroupCodeCorrelation", enableGPCodeCorrelation);
		if (rfxTemplate.getId() != null) {
			if (CollectionUtil.isNotEmpty(rfxTemplate.getFields())) {
				for (TemplateField tf : rfxTemplate.getFields()) {
					if (RfxTemplateFieldName.BUSINESS_UNIT == tf.getFieldName() && tf.getDefaultValue() != null && Boolean.TRUE == enableGPCodeCorrelation) {
						groupCodeList = groupCodeService.getGroupCodeIdByBusinessId(tf.getDefaultValue());

					}
					// ::
					if (RfxTemplateFieldName.GROUP_CODE == tf.getFieldName() && tf.getDefaultValue() != null) {
						GroupCode gc = groupCodeService.getGroupCodeByGCId(tf.getDefaultValue());
						if (Status.INACTIVE == gc.getStatus() && !groupCodeList.contains(gc)) {
							groupCodeList.add(gc);
						}
					}

				}
			}
		}

		model.addAttribute("groupCodeList", groupCodeList);

		long indusCatListCount = industryCategoryService.findTotalIndustryCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId());
		if (indusCatListCount == 0) {
			model.addAttribute("indusCatListFlag", true);
		} else {
			model.addAttribute("indusCatListFlag", false);
		}
		if (rfxTemplate.getViewSupplerName()) {
			rfxTemplate.setViewSupplerName(false);
		} else {
			rfxTemplate.setViewSupplerName(true);
		}
		model.addAttribute("rfxTemplate", rfxTemplate);
	}

	@RequestMapping(path = "/saveRfxTemplate", method = RequestMethod.POST)
	public ModelAndView saveRfxTemplate(@Valid @ModelAttribute("rfxTemplate") RfxTemplate rfxTemplate, @RequestParam("userId") String[] userId, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("saving rfx");
		if (rfxTemplate.getViewSupplerName()) {
			rfxTemplate.setViewSupplerName(false);
		} else {
			rfxTemplate.setViewSupplerName(true);
		}
		List<String> errMessages = new ArrayList<String>();
		List<User> approvalUsers = new ArrayList<User>();
		List<User> suspApprovalUsers = new ArrayList<User>();
		List<User> awardApprovalUsers = new ArrayList<User>();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				model.addAttribute("error", errMessages);
				return new ModelAndView("rfxTemplate", "rfxTemplate", rfxTemplate);

			} else {
				/*
				 * if(CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())){ for (TemplateEventTeamMembers string :
				 * rfxTemplate.getTeamMembers()) { LOG.info("Team Members..........."+string.getUser().getId());
				 * LOG.info("Team Members...getTeamMemberType........"+string.getTeamMemberType()); } }
				 */

				if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
					for (TemplateEventApproval app : rfxTemplate.getApprovals()) {
						for (TemplateApprovalUser appU : app.getApprovalUsers()) {
							if (!approvalUsers.contains(appU.getUser())) {
								approvalUsers.add(appU.getUser());
							}
						}
					}
				}

				if (CollectionUtil.isNotEmpty(rfxTemplate.getSuspensionApprovals())) {
					for (TemplateSuspensionApproval suspApp : rfxTemplate.getSuspensionApprovals()) {
						for (TemplateSuspensionApprovalUser suspAppU : suspApp.getApprovalUsers()) {
							if (!suspApprovalUsers.contains(suspAppU.getUser())) {
								suspApprovalUsers.add(suspAppU.getUser());
							}
						}
					}
				}

				if (CollectionUtil.isNotEmpty(rfxTemplate.getAwardApprovals())) {
					for (TemplateAwardApproval awardApp : rfxTemplate.getAwardApprovals()) {
						for (TemplateAwardApprovalUser awardAppU : awardApp.getApprovalUsers()) {
							if (!awardApprovalUsers.contains(awardAppU.getUser())) {
								awardApprovalUsers.add(awardAppU.getUser());
							}
						}
					}
				}

				if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
					int level = 1;
					for (TemplateEventApproval app : rfxTemplate.getApprovals()) {
						app.setRfxTemplate(rfxTemplate);
						app.setLevel(level++);
						if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
							for (TemplateApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
								approvalUser.setId(null);
							}
						}
					}
				}

				if (CollectionUtil.isNotEmpty(rfxTemplate.getSuspensionApprovals())) {
					int level = 1;
					for (TemplateSuspensionApproval app : rfxTemplate.getSuspensionApprovals()) {
						app.setRfxTemplate(rfxTemplate);
						app.setLevel(level++);
						if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
							for (TemplateSuspensionApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setSuspensionApproval(app);
								approvalUser.setId(null);
							}
						}
					}
				}

				if (CollectionUtil.isNotEmpty(rfxTemplate.getAwardApprovals())) {
					int level = 1;
					for (TemplateAwardApproval app : rfxTemplate.getAwardApprovals()) {
						app.setRfxTemplate(rfxTemplate);
						app.setLevel(level++);
						if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
							for (TemplateAwardApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setAwardApproval(app);
								approvalUser.setId(null);
							}
						}
					}
				}

				if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
					for (TemplateUnmaskUser unmask : rfxTemplate.getUnMaskedUsers()) {
						unmask.setRfxTemplate(rfxTemplate);
						unmask.setId(null);
					}
				}

				List<TemplateEventTeamMembers> teamMembers = new ArrayList<TemplateEventTeamMembers>();
				if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
					for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
						if (team != null && team.getUser() != null) {
							if (StringUtils.checkString(team.getUser().getId()).length() > 0 && team.getTeamMemberType() != null) {
								team.setRfxTemplate(rfxTemplate);
								teamMembers.add(team);
							}
						}
					}
				}
				rfxTemplate.setTeamMembers(teamMembers);

				if (doValidate(rfxTemplate)) {
					constructFieldsForSaving(rfxTemplate);
					if (StringUtils.checkString(rfxTemplate.getId()).length() == 0) {
						rfxTemplate.setCreatedBy(SecurityLibrary.getLoggedInUser());
						rfxTemplate.setCreatedDate(new Date());
						rfxTemplate.setRfxEnvelope1(rfxTemplate.getRfxEnvelope1());
						rfxTemplate.setRfxEnvelope2(rfxTemplate.getRfxEnvelope2());
						rfxTemplate.setRfxEnvelope3(rfxTemplate.getRfxEnvelope3());
						rfxTemplate.setRfxEnvelope4(rfxTemplate.getRfxEnvelope4());
						rfxTemplate.setRfxEnvelope5(rfxTemplate.getRfxEnvelope5());
						rfxTemplate.setRfxEnvelope6(rfxTemplate.getRfxEnvelope6());
						rfxTemplate.setRfxSequence1(rfxTemplate.getRfxSequence1());
						rfxTemplate.setRfxSequence2(rfxTemplate.getRfxSequence2());
						rfxTemplate.setRfxSequence3(rfxTemplate.getRfxSequence3());
						rfxTemplate.setRfxSequence4(rfxTemplate.getRfxSequence4());
						rfxTemplate.setRfxSequence5(rfxTemplate.getRfxSequence5());
						rfxTemplate.setRfxSequence6(rfxTemplate.getRfxSequence6());
						rfxTemplate.setRfxEnvelopeOpening(rfxTemplate.getRfxEnvelopeOpening());
						rfxTemplate.setRfxEnvelopeReadOnly(rfxTemplate.getRfxEnvelopeReadOnly());
						rfxTemplate.setRfxEnvOpeningAfter(rfxTemplate.getRfxEnvOpeningAfter());
						rfxTemplate.setEnableSuspendApproval(rfxTemplate.getEnableSuspendApproval());
						rfxTemplate.setVisibleSuspendApproval(rfxTemplate.getVisibleSuspendApproval());
						rfxTemplate.setOptionalSuspendApproval(rfxTemplate.getOptionalSuspendApproval());
						rfxTemplate.setReadOnlySuspendApproval(rfxTemplate.getReadOnlySuspendApproval());
						rfxTemplate.setEnableAwardApproval(rfxTemplate.getEnableAwardApproval());
						rfxTemplate.setVisibleAwardApproval(rfxTemplate.getVisibleAwardApproval());
						rfxTemplate.setOptionalAwardApproval(rfxTemplate.getOptionalAwardApproval());
						rfxTemplate.setReadOnlyAwardApproval(rfxTemplate.getReadOnlyAwardApproval());
						rfxTemplate.setAllowDisqualifiedSupplierDownload(rfxTemplate.getAllowDisqualifiedSupplierDownload());
						LOG.info("rfxTemplate.getRevertBidUser()  " + rfxTemplate.getRevertBidUser().getId());
						rfxTemplate.setCompleteTemplateDetails(Boolean.TRUE);
						if (rfxTemplate.getType() != RfxTypes.RFA) {
							LOG.info("TYPE : " + rfxTemplate.getType() + " Revise Bid User : " + rfxTemplate.getRevertBidUser());
							rfxTemplate.setRevertBidUser(null);
						} else {
							if (StringUtils.checkString(rfxTemplate.getRevertBidUser().getId()).isEmpty()) {
								rfxTemplate.setRevertBidUser(null);
							}
						}
						rfxTemplate = rfxTemplateService.save(rfxTemplate);

						for (String usersId : userId) {
							User user = userService.getUsersForRfxById(usersId);
							List<RfxTemplate> assignedTemplateList = user.getAssignedTemplates();
							if (CollectionUtil.isNotEmpty(assignedTemplateList)) {
								assignedTemplateList.add(rfxTemplate);
								user.setAssignedTemplates(assignedTemplateList);
							} else {
								assignedTemplateList = new ArrayList<>();
								assignedTemplateList.add(rfxTemplate);
								user.setAssignedTemplates(assignedTemplateList);
							}
							userService.updateUser(user);
						}
						// List<User> userList =
						// rfxTemplateService.getAllUserForTemplate(SecurityLibrary.getLoggedInUserTenantId());

						/*redir.addFlashAttribute("success", messageSource.getMessage("rfxTemplate.save.success", new Object[] { rfxTemplate.getTemplateName() }, Global.LOCALE));*/
						LOG.info("create rfxTemplate Called by : " + SecurityLibrary.getLoggedInUser());
					} else {
						Integer assignedCount = 0;
						if (rfxTemplate.getType() == RfxTypes.RFA) {
							assignedCount = rfaEventService.findAssignedTemplateCount(rfxTemplate.getId());
						}
						if (rfxTemplate.getType() == RfxTypes.RFI) {
							assignedCount = rfiEventService.findAssignedTemplateCount(rfxTemplate.getId());
						}
						if (rfxTemplate.getType() == RfxTypes.RFP) {
							assignedCount = rfpEventService.findAssignedTemplateCount(rfxTemplate.getId());
						}
						if (rfxTemplate.getType() == RfxTypes.RFQ) {
							assignedCount = rfpEventService.findAssignedTemplateCount(rfxTemplate.getId());
						}
						if (rfxTemplate.getType() == RfxTypes.RFT) {
							assignedCount = rftEventService.findAssignedTemplateCount(rfxTemplate.getId());
						}
						RfxTemplate persistObj = rfxTemplateService.getRfxTemplateById(rfxTemplate.getId());
						boolean doAuditStatusChange = rfxTemplate.getStatus() != persistObj.getStatus();

						if (assignedCount == 0) {
							persistObj.setFields(rfxTemplate.getFields());
							persistObj.setType(rfxTemplate.getType());
							if (rfxTemplate.getType() == RfxTypes.RFA) {
								persistObj.setTemplateAuctionType(rfxTemplate.getTemplateAuctionType());
								persistObj.setRevertLastBid(rfxTemplate.getRevertLastBid());
								persistObj.setVisibleRevertLastBid(rfxTemplate.getVisibleRevertLastBid());
								persistObj.setReadOnlyRevertLastBid(rfxTemplate.getReadOnlyRevertLastBid());
								LOG.info(">>>>RevertBidUser>>>>>>" + rfxTemplate.getRevertBidUser().getId());
								if (rfxTemplate.getRevertBidUser() != null && StringUtils.checkString(rfxTemplate.getRevertBidUser().getId()).length() > 0) {
									persistObj.setRevertBidUser(rfxTemplate.getRevertBidUser());
								}
							}

							persistObj.setEnableEvaluationDeclaration(rfxTemplate.getEnableEvaluationDeclaration());
							persistObj.setEnableSupplierDeclaration(rfxTemplate.getEnableSupplierDeclaration());
							persistObj.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
							persistObj.setVisibleEvaluationConclusionUsers(rfxTemplate.getVisibleEvaluationConclusionUsers());
							persistObj.setReadOnlyEvaluationConclusionUsers(rfxTemplate.getReadOnlyEvaluationConclusionUsers());
							persistObj.setEvaluationConclusionUsers(rfxTemplate.getEvaluationConclusionUsers());

							persistObj.setRfxEnvelopeOpening(rfxTemplate.getRfxEnvelopeOpening());
							persistObj.setAllowDisqualifiedSupplierDownload(rfxTemplate.getAllowDisqualifiedSupplierDownload());

							persistObj.setRfxEnvOpeningAfter(rfxTemplate.getRfxEnvOpeningAfter());
							persistObj.setRfxEnvelopeReadOnly(rfxTemplate.getRfxEnvelopeReadOnly());
							persistObj.setRfxEnvelope1(rfxTemplate.getRfxEnvelope1());
							persistObj.setRfxEnvelope2(rfxTemplate.getRfxEnvelope2());
							persistObj.setRfxEnvelope3(rfxTemplate.getRfxEnvelope3());
							persistObj.setRfxEnvelope4(rfxTemplate.getRfxEnvelope4());
							persistObj.setRfxEnvelope5(rfxTemplate.getRfxEnvelope5());
							persistObj.setRfxEnvelope6(rfxTemplate.getRfxEnvelope6());
							persistObj.setUnMaskedUsers(rfxTemplate.getUnMaskedUsers());
							persistObj.setRfxSequence1(rfxTemplate.getRfxSequence1());
							persistObj.setRfxSequence2(rfxTemplate.getRfxSequence2());
							persistObj.setRfxSequence3(rfxTemplate.getRfxSequence3());
							persistObj.setRfxSequence4(rfxTemplate.getRfxSequence4());
							persistObj.setRfxSequence5(rfxTemplate.getRfxSequence5());
							persistObj.setRfxSequence6(rfxTemplate.getRfxSequence6());
							persistObj.setAddBillOfQuantity(rfxTemplate.getAddBillOfQuantity());
							persistObj.setUnMaskedUser(rfxTemplate.getUnMaskedUser());
							persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
							persistObj.setModifiedDate(new Date());
							persistObj.setApprovals(rfxTemplate.getApprovals());
							persistObj.setTeamMembers(rfxTemplate.getTeamMembers());
							persistObj.setApprovalOptional(rfxTemplate.getApprovalOptional());
							persistObj.setApprovalReadOnly(rfxTemplate.getApprovalReadOnly());
							persistObj.setApprovalVisible(rfxTemplate.getApprovalVisible());
							persistObj.setSupplierBasedOnCategory(rfxTemplate.getSupplierBasedOnCategory());
							persistObj.setAutoPopulateSupplier(rfxTemplate.getAutoPopulateSupplier());
							persistObj.setReadOnlySupplier(rfxTemplate.getReadOnlySupplier());
							persistObj.setVisibleViewSupplierName(rfxTemplate.getVisibleViewSupplierName());
							persistObj.setReadOnlyViewSupplierName(rfxTemplate.getReadOnlyViewSupplierName());
							persistObj.setVisibleCloseEnvelope(rfxTemplate.getVisibleCloseEnvelope());
							persistObj.setReadOnlyCloseEnvelope(rfxTemplate.getReadOnlyCloseEnvelope());
							persistObj.setViewSupplerName(rfxTemplate.getViewSupplerName());
							persistObj.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
							persistObj.setAddSupplier(rfxTemplate.getAddSupplier());
							persistObj.setVisibleAddSupplier(rfxTemplate.getVisibleAddSupplier());
							persistObj.setReadOnlyAddSupplier(rfxTemplate.getReadOnlyAddSupplier());
							persistObj.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
							persistObj.setReadOnlyAllowToSuspendEvent(rfxTemplate.getReadOnlyAllowToSuspendEvent());
							persistObj.setVisibleAllowToSuspendEvent(rfxTemplate.getVisibleAllowToSuspendEvent());
							persistObj.setViewAuctionHall(rfxTemplate.getViewAuctionHall());
							persistObj.setVisibleViewAuctionHall(rfxTemplate.getVisibleViewAuctionHall());
							persistObj.setReadOnlyViewAuctionHall(rfxTemplate.getReadOnlyViewAuctionHall());
							persistObj.setPrivateEvent(rfxTemplate.getPrivateEvent());
							persistObj.setPartialEvent(rfxTemplate.getPartialEvent());
							persistObj.setPublicEvent(rfxTemplate.getPublicEvent());
							persistObj.setSupplierBasedOnState(rfxTemplate.getSupplierBasedOnState());
							persistObj.setRestrictSupplierByState(rfxTemplate.getRestrictSupplierByState());
							persistObj.setVisibleSupplierTags(rfxTemplate.getVisibleSupplierTags());
							persistObj.setOptionalSupplierTags(rfxTemplate.getOptionalSupplierTags());
							persistObj.setVisibleGeographicalCoverage(rfxTemplate.getVisibleGeographicalCoverage());
							persistObj.setOptionalGeographicalCoverage(rfxTemplate.getOptionalGeographicalCoverage());
							persistObj.setReadOnlyTeamMember(rfxTemplate.getReadOnlyTeamMember());
							persistObj.setEnableSuspendApproval(rfxTemplate.getEnableSuspendApproval());
							persistObj.setVisibleSuspendApproval(rfxTemplate.getVisibleSuspendApproval());
							persistObj.setOptionalSuspendApproval(rfxTemplate.getOptionalSuspendApproval());
							persistObj.setReadOnlySuspendApproval(rfxTemplate.getReadOnlySuspendApproval());
							persistObj.setSuspensionApprovals(rfxTemplate.getSuspensionApprovals());
							persistObj.setEnableAwardApproval(rfxTemplate.getEnableAwardApproval());
							persistObj.setVisibleAwardApproval(rfxTemplate.getVisibleAwardApproval());
							persistObj.setOptionalAwardApproval(rfxTemplate.getOptionalAwardApproval());
							persistObj.setReadOnlyAwardApproval(rfxTemplate.getReadOnlyAwardApproval());
							persistObj.setCompleteTemplateDetails(rfxTemplate.getCompleteTemplateDetails());
							LOG.info(" ******************* Hello ***************** " + rfxTemplate.getReadOnlyAwardApproval());
							persistObj.setAwardApprovals(rfxTemplate.getAwardApprovals());

						}
						// Fix for old templates that do not have Event Publish Type values as this was introduced in
						// Sept 2019
						if (rfxTemplate.getPrivateEvent() == null && rfxTemplate.getPublicEvent() == null && rfxTemplate.getPartialEvent() == null) {
							rfxTemplate.setPrivateEvent(true);
						}
						if (rfxTemplate.getPrivateEvent() == Boolean.FALSE && rfxTemplate.getPublicEvent() == Boolean.FALSE && rfxTemplate.getPartialEvent() == Boolean.FALSE) {
							rfxTemplate.setPrivateEvent(true);
						}
						persistObj.setTemplateName(rfxTemplate.getTemplateName());
						persistObj.setTemplateDescription(rfxTemplate.getTemplateDescription());
						persistObj.setStatus(rfxTemplate.getStatus());
						// List<String> assignedUserId =
						// rfxTemplateService.getTemplateByUserIdAndTemplateId(rfxTemplate.getId(),
						// SecurityLibrary.getLoggedInUserTenantId());

						rfxTemplateService.deleteAssociatedUserForTemplate(rfxTemplate.getId());

						for (String usersId : userId) {
							User user = userService.getUsersForRfxById(usersId);
							List<RfxTemplate> assignedTemplateList = user.getAssignedTemplates();
							assignedTemplateList.add(rfxTemplate);
							user.setAssignedTemplates(assignedTemplateList);
							userService.updateUser(user);
						}

						rfxTemplateService.update(persistObj);
						if (doAuditStatusChange) {
							try {
								BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(rfxTemplate.getStatus() == Status.ACTIVE ? AuditTypes.ACTIVE : AuditTypes.INACTIVE, rfxTemplate.getType() + " Template '" + rfxTemplate.getTemplateName() + "' status is changed to '" + rfxTemplate.getStatus() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFXAuctionTemplate);
								buyerAuditTrailDao.save(buyerAuditTrail);
							} catch (Exception e) {
								LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
							}
						} else {
							try {
								BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, persistObj.getType() + " Template '" + persistObj.getTemplateName() + "' updated ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFXAuctionTemplate);
								buyerAuditTrailDao.save(buyerAuditTrail);
							} catch (Exception e) {
								LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
							}
						}
						//redir.addFlashAttribute("success", messageSource.getMessage("rfxTemplate.update.success", new Object[] { rfxTemplate.getTemplateName() }, Global.LOCALE));
						LOG.info("update RfxTemplate Called");
					}
				} else {
					LOG.info("Validation error ...............");
					if (rfxTemplate.getViewSupplerName()) {
						rfxTemplate.setViewSupplerName(false);
					} else {
						rfxTemplate.setViewSupplerName(true);
					}
					model.addAttribute("evaluationDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.EVALUATION_PROCESS, SecurityLibrary.getLoggedInUserTenantId()));
					model.addAttribute("supplierDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.SUPPLIER_ACCEPTANCE, SecurityLibrary.getLoggedInUserTenantId()));
					model.addAttribute("currencyList", currencyService.getAllCurrency());
					model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
					model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
					model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
					model.addAttribute("template", rfxTemplate);
					// List<User> revertBidUser =
					// userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());

					List<User> userTeamMemberList = new ArrayList<User>();
					List<User> revertBidUser = new ArrayList<User>(); // userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
					List<User> evaluationConclusionUsers = new ArrayList<User>();
					// team members (Normal)
					List<UserPojo> userTeamMember = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
					for (UserPojo user : userTeamMember) {
						User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
						userTeamMemberList.add(u);
						revertBidUser.add(u);
						evaluationConclusionUsers.add(u);
					}

					if (rfxTemplate.getUnMaskedUsers() != null) {
						for (TemplateUnmaskUser us : rfxTemplate.getUnMaskedUsers()) {
							if (us != null && us.getUser() != null) {
								User user = us.getUser();
								User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.getEmailNotifications(), user.getTenantId(), user.isDeleted());
								if (!revertBidUser.contains(u)) {
									revertBidUser.add(u);
								}
							}
						}
					}

					if (rfxTemplate.getEvaluationConclusionUsers() != null) {
						for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
							if (user != null) {
								User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.getEmailNotifications(), user.getTenantId(), user.isDeleted());
								if (!evaluationConclusionUsers.contains(u)) {
									evaluationConclusionUsers.add(u);
								}
							}
						}
					}

					model.addAttribute("revertBidUser", revertBidUser);
					model.addAttribute("evaluationConclusionUsers", evaluationConclusionUsers);

					model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
					model.addAttribute("teamMember", true);
					List<TemplateEventTeamMembers> assignedTeamMembers = new ArrayList<TemplateEventTeamMembers>();
					List<User> remainingUser = new ArrayList<User>();
					for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
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

					List<UserPojo> usersList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
					List<User> assignedUserList = new ArrayList<>();
					List<User> selectedOnes = new ArrayList<User>();
					for (UserPojo user : usersList) {
						User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
						selectedOnes.add(u);
					}
					for (String usersId : userId) {
						User user = userService.getUsersNameAndId(usersId);
						User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.getEmailNotifications(), user.getTenantId(), user.isDeleted());
						assignedUserList.add(u);
						remainingUser.add((User) u.clone());
					}
					selectedOnes.removeAll(remainingUser);
					model.addAttribute("assignedUserList", assignedUserList);
					model.addAttribute("userlistForAssigned", selectedOnes);
					model.addAttribute("userList", approvalUsers);
					model.addAttribute("suspTemplUserList", suspApprovalUsers);
					model.addAttribute("awardTemplUserList", awardApprovalUsers);

					if (StringUtils.checkString(rfxTemplate.getId()).length() == 0) {
						model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));

						model.addAttribute("error", messageSource.getMessage("rfxTemplate.error.duplicate", new Object[] { rfxTemplate.getTemplateName() }, Global.LOCALE));
						return new ModelAndView("rfxTemplate", "rfxTemplate", rfxTemplate);

					} else {
						model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
						LOG.info("<<<<<<<<<<<<<<<<<<<" + rfxTemplate.getId());
						redir.addFlashAttribute("error", messageSource.getMessage("error.while.saving.rfxtemplate", new Object[] { rfxTemplate.getTemplateName() }, Global.LOCALE));
						return new ModelAndView("redirect:editRfxTemplate?id=" + rfxTemplate.getId());
					}

				}
			}
		} catch (Exception e) {
			LOG.error("Error while saving the RfxTemplate : " + e.getMessage(), e);
			// model.addAttribute("error", "Error while saving the RfxTemplate : " + e.getMessage());

			if (StringUtils.checkString(rfxTemplate.getId()).length() > 0) {
				redir.addFlashAttribute("error", messageSource.getMessage("error.while.saving.rfxtemplate", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ModelAndView("redirect:editRfxTemplate?id=" + rfxTemplate.getId());
			} else {
				model.addAttribute("error", messageSource.getMessage("error.while.saving.rfxtemplate", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ModelAndView("rfxTemplate", "rfxTemplate", rfxTemplate);
			}
		}
		/*return new ModelAndView("redirect:rfxTemplateList");*/
		return new ModelAndView("redirect:rfxTemplateDocument/" + rfxTemplate.getId());
	}

	@RequestMapping (path="/finishTemplate/{templateId}", method = RequestMethod.GET)
	public String finishTemplate(@PathVariable("templateId") String templateId, Model model){
		LOG.info("finish template method is called with Template id " + templateId);
		RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateById(templateId);
		if(CollectionUtil.isNotEmpty(rfxTemplate.getDocuments())){
			rfxTemplate.setDocumentCompleted(true);
		}
		rfxTemplateService.update(rfxTemplate);
		if(doValidate(rfxTemplate)){
			model.addAttribute("success", messageSource.getMessage("rfxTemplate.update.success", new Object[] { rfxTemplate.getTemplateName() }, Global.LOCALE));
		}else {
			model.addAttribute("success", messageSource.getMessage("rfxTemplate.save.success", new Object[]{rfxTemplate.getTemplateName()}, Global.LOCALE));
		}
		return "rfxTemplateList";
	}
	private void constructFieldsForSaving(RfxTemplate rfxTemplate) {

		TemplateFieldPojo tfp = rfxTemplate.getTemplateFieldBinding();
		if (tfp != null) {
			Buyer buyer = new Buyer();
			buyer.setId(SecurityLibrary.getLoggedInUserTenantId());
			rfxTemplate.setFields(new ArrayList<TemplateField>());

			if (tfp.getEventName() != null || tfp.getEventNameVisible() != null || tfp.getEventNameDisabled() != null || tfp.getEventNameOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.EVENT_NAME, tfp.getEventName(), tfp.getEventNameVisible(), tfp.getEventNameDisabled(), tfp.getEventNameOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}

			/*
			 * if (tfp.getIndustryCategory() != null || tfp.getIndustryCategoryVisible() != null ||
			 * tfp.getIndustryCategoryDisabled() != null || tfp.getIndustryCategoryOptional() != null) { LOG.info(
			 * "............................................................."+( tfp.getIndustryCategory() != null ?
			 * tfp.getIndustryCategory().getId() : "somthing going wrong")); TemplateField tf = new
			 * TemplateField(RfxTemplateFieldName.EVENT_CATEGORY, (tfp.getIndustryCategory() != null ?
			 * tfp.getIndustryCategory().getId() : null), tfp.getIndustryCategoryVisible(),
			 * tfp.getIndustryCategoryDisabled(), tfp.getIndustryCategoryOptional(), buyer, rfxTemplate);
			 * rfxTemplate.getFields().add(tf); }
			 */

			if (tfp.getIndustryCatArr() != null || tfp.getIndustryCategoryVisible() != null || tfp.getIndustryCategoryDisabled() != null || tfp.getIndustryCategoryOptional() != null) {
				LOG.info("............................................................." + tfp.getIndustryCatArr());
				TemplateField tf = new TemplateField(RfxTemplateFieldName.EVENT_CATEGORY, (tfp.getIndustryCatArr() != null ? tfp.getIndustryCatArr() : null), tfp.getIndustryCategoryVisible(), tfp.getIndustryCategoryDisabled(), tfp.getIndustryCategoryOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getBaseCurrency() != null || tfp.getBaseCurrencyVisible() != null || tfp.getBaseCurrencyDisabled() != null || tfp.getBaseCurrencyOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.BASE_CURRENCY, (tfp.getBaseCurrency() != null ? tfp.getBaseCurrency().getId() : null), tfp.getBaseCurrencyVisible(), tfp.getBaseCurrencyDisabled(), tfp.getBaseCurrencyOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getDecimal() != null || tfp.getDecimalVisible() != null || tfp.getDecimalDisabled() != null || tfp.getDecimalOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.DECIMAL, (tfp.getDecimal() != null ? String.valueOf(tfp.getDecimal()) : null), tfp.getDecimalVisible(), tfp.getDecimalDisabled(), tfp.getDecimalOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getCostCenter() != null || tfp.getCostCenterVisible() != null || tfp.getCostCenterDisabled() != null || tfp.getCostCenterOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.COST_CENTER, (tfp.getCostCenter() != null ? tfp.getCostCenter().getId() : null), tfp.getCostCenterVisible(), tfp.getCostCenterDisabled(), tfp.getCostCenterOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getBusinessUnit() != null || tfp.getBusinessUnitDisabled() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.BUSINESS_UNIT, (tfp.getBusinessUnit() != null ? tfp.getBusinessUnit().getId() : null), tfp.getBusinessUnitDisabled(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getBudgetAmount() != null || tfp.getBudgetAmountVisible() != null || tfp.getBudgetAmountDisabled() != null || tfp.getBudgetAmountOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.BUDGET_AMOUNT, (tfp.getBudgetAmount() != null ? String.valueOf(tfp.getBudgetAmount()) : null), tfp.getBudgetAmountVisible(), tfp.getBudgetAmountDisabled(), tfp.getBudgetAmountOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getHistoricAmount() != null || tfp.getHistoricAmountVisible() != null || tfp.getHistoricAmountDisabled() != null || tfp.getHistoricAmountOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.HISTORIC_AMOUNT, (tfp.getHistoricAmount() != null ? String.valueOf(tfp.getHistoricAmount()) : null), tfp.getHistoricAmountVisible(), tfp.getHistoricAmountDisabled(), tfp.getHistoricAmountOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getPaymentTerms() != null || tfp.getPaymentTermsVisible() != null || tfp.getPaymentTermsDisabled() != null || tfp.getPaymentTermsOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.PAYMENT_TERM, tfp.getPaymentTerms(), tfp.getPaymentTermsVisible(), tfp.getPaymentTermsDisabled(), tfp.getPaymentTermsOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getParticipationFees() != null || tfp.getParticipationFeesVisible() != null || tfp.getParticipationFeesDisabled() != null || tfp.getParticipationFeesOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.PARTICIPATION_FEES, (tfp.getParticipationFees() != null ? String.valueOf(tfp.getParticipationFees()) : null), tfp.getParticipationFeesVisible(), tfp.getParticipationFeesDisabled(), tfp.getParticipationFeesOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getDeposit() != null || tfp.getAddDepositVisible() != null || tfp.getAddDepositDisabled() != null || tfp.getAddDepositOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.DEPOSIT, (tfp.getDeposit() != null ? String.valueOf(tfp.getDeposit()) : null), tfp.getAddDepositVisible(), tfp.getAddDepositDisabled(), tfp.getAddDepositOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getParticipationFeeCurrency() != null || tfp.getParticipationFeesVisible() != null || tfp.getParticipationFeesDisabled() != null || tfp.getParticipationFeesOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.PARTICIPATION_FEE_CURRENCY, (tfp.getParticipationFeeCurrency() != null ? tfp.getParticipationFeeCurrency().getId() : null), tfp.getParticipationFeesVisible(), tfp.getParticipationFeesDisabled(), tfp.getParticipationFeesOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getDepositCurrency() != null || tfp.getAddDepositVisible() != null || tfp.getAddDepositDisabled() != null || tfp.getAddDepositOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.DEPOSIT_CURRENCY, (tfp.getDepositCurrency() != null ? tfp.getDepositCurrency().getId() : null), tfp.getAddDepositVisible(), tfp.getAddDepositDisabled(), tfp.getAddDepositOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			// if (tfp.getPaymentTerms() != null || tfp.getPaymentTermsVisible() != null ||
			// tfp.getPaymentTermsDisabled() != null || tfp.getPaymentTermsOptional() != null) {
			// TemplateField tf = new TemplateField(RfxTemplateFieldName.PAYMENT_TERM, tfp.getPaymentTerms(),
			// tfp.getPaymentTermsVisible(), tfp.getPaymentTermsDisabled(), tfp.getPaymentTermsOptional(), buyer,
			// rfxTemplate);
			// rfxTemplate.getFields().add(tf);
			// }

			if (tfp.getPreBidBy() != null || tfp.getPaymentTermsDisabled() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.PRE_BID_BY, (String.valueOf(tfp.getPreBidBy())), true, tfp.getPreBidByDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getIsPreBidSameBidPrice() != null || tfp.getIsPreBidSameBidPriceVisible() != null || tfp.getIsPreBidSameBidPriceDisabled() != null || tfp.getIsPreBidSameBidPriceOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.PRE_SUPPLIER_SAME_BID, String.valueOf(tfp.getIsPreBidSameBidPrice()), true, tfp.getIsPreBidSameBidPriceDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getPreBidBy() == PreBidByType.SUPPLIER) {
				tfp.setIsPreSetSamePreBidForAllSuppliers(Boolean.FALSE);
				tfp.setIsPreSetSamePreBidForAllSuppliersDisabled(Boolean.FALSE);
			}

			if (tfp.getIsPreSetSamePreBidForAllSuppliers() != null || tfp.getIsPreSetSamePreBidForAllSuppliersVisible() != null || tfp.getIsPreSetSamePreBidForAllSuppliersDisabled() != null || tfp.getIsPreSetSamePreBidForAllSuppliersOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.PRE_SET_SAME_PRE_BID_ALL_SUPPLIER, String.valueOf(tfp.getIsPreSetSamePreBidForAllSuppliers()), true, tfp.getIsPreSetSamePreBidForAllSuppliersDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			// if (tfp.getIsPreBidSameBidPrice() != null || tfp.getIsPreBidSameBidPriceVisible() != null ||
			// tfp.getIsPreBidSameBidPriceDisabled() != null || tfp.getIsPreBidSameBidPriceOptional() != null) {
			// TemplateField tf = new TemplateField(RfxTemplateFieldName.PRE_SUPPLIER_SAME_BID,
			// (tfp.getIsPreBidSameBidPrice() != null ? String.valueOf(tfp.getIsPreBidSameBidPrice()) : null),
			// tfp.getIsPreBidSameBidPriceVisible(), tfp.getIsPreBidSameBidPriceDisabled(),
			// tfp.getIsPreBidSameBidPriceOptional(), buyer, rfxTemplate);
			// rfxTemplate.getFields().add(tf);
			// }
			if (tfp.getIsPreBidHigherPrice() != null || tfp.getIsPreBidHigherPriceVisible() != null || tfp.getIsPreBidHigherPriceDisabled() != null || tfp.getIsPreBidHigherPriceOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.PRE_SUPPLIER_PROVIDE_HIGHER, (tfp.getIsPreBidHigherPrice() != null ? String.valueOf(tfp.getIsPreBidHigherPrice()) : null), true, tfp.getIsPreBidHigherPriceDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}

			if (tfp.getBiddingType() != null || tfp.getBiddingTypeDisabled() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.BIDDING_TYPE, (tfp.getBiddingType() != null ? String.valueOf(tfp.getBiddingType()) : null), true, tfp.getBiddingTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}

			if (tfp.getIsBiddingMinValueFromPrevious() != null || tfp.getIsBiddingAllowSupplierSameBidVisible() != null || tfp.getIsBiddingAllowSupplierSameBidDisabled() != null || tfp.getIsBiddingAllowSupplierSameBidOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.IS_BID_INCR_OWN_PREVIOUS, (tfp.getIsBiddingMinValueFromPrevious() != null ? String.valueOf(tfp.getIsBiddingMinValueFromPrevious()) : null), true, tfp.getIsBiddingMinValueFromPreviousDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getBiddingMinValue() != null || tfp.getIsPreBidHigherPriceVisible() != null || tfp.getIsPreBidHigherPriceDisabled() != null || tfp.getIsPreBidHigherPriceVisible() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.BID_INCR_OWN_PRE_VAL, (tfp.getBiddingMinValue() != null ? String.valueOf(tfp.getBiddingMinValue()) : null), true, tfp.getIsBiddingMinValueFromPreviousDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getBiddingMinValueType() != null || tfp.getIsPreBidHigherPriceVisible() != null || tfp.getIsPreBidHigherPriceDisabled() != null || tfp.getIsPreBidHigherPriceVisible() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.BID_INCR_OWN_PRE_TYPE, (tfp.getBiddingMinValueType() != null ? String.valueOf(tfp.getBiddingMinValueType()) : null), true, tfp.getIsBiddingMinValueFromPreviousDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}

			if (tfp.getIsStartGate() != null || tfp.getIsStartGateVisible() != null || tfp.getIsStartGateDisabled() != null || tfp.getIsStartGateOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.START_GATE, (tfp.getIsStartGate() != null ? String.valueOf(tfp.getIsStartGate()) : null), true, tfp.getIsStartGateDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}

			if (tfp.getIsBiddingPriceHigherLeadingBid() != null || tfp.getIsBiddingPriceHigherLeadingBidVisible() != null || tfp.getIsBiddingPriceHigherLeadingBidDisabled() != null || tfp.getIsBiddingPriceHigherLeadingBidOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.IS_BID_HIGHER_LEADING, (tfp.getIsBiddingPriceHigherLeadingBid() != null ? String.valueOf(tfp.getIsBiddingPriceHigherLeadingBid()) : null), true, tfp.getIsBiddingPriceHigherLeadingBidDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getBiddingPriceHigherLeadingBidType() != null || tfp.getIsBiddingPriceHigherLeadingBidVisible() != null || tfp.getIsBiddingPriceHigherLeadingBidDisabled() != null || tfp.getIsBiddingPriceHigherLeadingBidOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.BID_HIGHER_LEAD_TYPE, (tfp.getBiddingPriceHigherLeadingBidType() != null ? String.valueOf(tfp.getBiddingPriceHigherLeadingBidType()) : null), true, tfp.getIsBiddingPriceHigherLeadingBidDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getBiddingPriceHigherLeadingBidValue() != null || tfp.getIsBiddingPriceHigherLeadingBidVisible() != null || tfp.getIsBiddingPriceHigherLeadingBidDisabled() != null || tfp.getIsBiddingPriceHigherLeadingBidOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.BID_HIGHER_LEAD_VAL, (tfp.getBiddingPriceHigherLeadingBidValue() != null ? String.valueOf(tfp.getBiddingPriceHigherLeadingBidValue()) : null), true, tfp.getIsBiddingPriceHigherLeadingBidDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getIsBiddingAllowSupplierSameBid() != null || tfp.getIsBiddingAllowSupplierSameBidVisible() != null || tfp.getIsBiddingAllowSupplierSameBidDisabled() != null || tfp.getIsBiddingAllowSupplierSameBidOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.IS_BIDDING_SAMEPRICE_SUPPLIER, (tfp.getIsBiddingAllowSupplierSameBid() != null ? String.valueOf(tfp.getIsBiddingAllowSupplierSameBid()) : null), true, tfp.getIsBiddingAllowSupplierSameBidDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getAuctionConsolePriceType() != null || tfp.getAuctionConsolePriceTypeVisible() != null || tfp.getAuctionConsolePriceTypeDisabled() != null || tfp.getAuctionConsolePriceTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.AUCTION_PRICE_TYPE, (tfp.getAuctionConsolePriceType() != null ? String.valueOf(tfp.getAuctionConsolePriceType()) : null), true, tfp.getAuctionConsolePriceTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getAuctionConsoleRankType() != null || tfp.getAuctionConsoleRankTypeVisible() != null || tfp.getAuctionConsoleRankTypeDisabled() != null || tfp.getAuctionConsoleRankTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.AUCTION_RANK_TYPE, (tfp.getAuctionConsoleRankType() != null ? String.valueOf(tfp.getAuctionConsoleRankType()) : null), true, tfp.getAuctionConsoleRankTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getAuctionConsoleVenderType() != null || tfp.getAuctionConsoleVenderTypeVisible() != null || tfp.getAuctionConsoleVenderTypeDisabled() != null || tfp.getAuctionConsoleVenderTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.AUCTION_SUPPLIER_TYPE, (tfp.getAuctionConsoleVenderType() != null ? String.valueOf(tfp.getAuctionConsoleVenderType()) : null), true, tfp.getAuctionConsoleVenderTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}

			if (tfp.getBuyerAuctionConsolePriceType() != null || tfp.getBuyerAuctionConsolePriceTypeVisible() != null || tfp.getBuyerAuctionConsolePriceTypeDisabled() != null || tfp.getBuyerAuctionConsolePriceTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.AUCTION_BUY_PRICE_TYPE, (tfp.getBuyerAuctionConsolePriceType() != null ? String.valueOf(tfp.getBuyerAuctionConsolePriceType()) : null), true, tfp.getBuyerAuctionConsolePriceTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getBuyerAuctionConsoleRankType() != null || tfp.getBuyerAuctionConsoleRankTypeVisible() != null || tfp.getBuyerAuctionConsoleRankTypeDisabled() != null || tfp.getBuyerAuctionConsoleRankTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.AUCTION_BUY_RANK_TYPE, (tfp.getBuyerAuctionConsoleRankType() != null ? String.valueOf(tfp.getBuyerAuctionConsoleRankType()) : null), true, tfp.getBuyerAuctionConsoleRankTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getBuyerAuctionConsoleVenderType() != null || tfp.getBuyerAuctionConsoleVenderTypeVisible() != null || tfp.getBuyerAuctionConsoleVenderTypeDisabled() != null || tfp.getBuyerAuctionConsoleVenderTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.AUCTION_BUY_SUPPLIER_TYPE, (tfp.getBuyerAuctionConsoleVenderType() != null ? String.valueOf(tfp.getBuyerAuctionConsoleVenderType()) : null), true, tfp.getBuyerAuctionConsoleVenderTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}

			if (tfp.getTimeExtensionType() != null || tfp.getTimeExtensionTypeVisible() != null || tfp.getTimeExtensionTypeDisabled() != null || tfp.getTimeExtensionTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.TIME_EXT_TYPE, (tfp.getTimeExtensionType() != null ? String.valueOf(tfp.getTimeExtensionType()) : null), true, tfp.getTimeExtensionTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getTimeExtensionDuration() != null || tfp.getTimeExtensionTypeVisible() != null || tfp.getTimeExtensionTypeDisabled() != null || tfp.getTimeExtensionTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.TIME_EXT_DURATION, (tfp.getTimeExtensionDuration() != null ? String.valueOf(tfp.getTimeExtensionDuration()) : null), true, tfp.getTimeExtensionTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getTimeExtensionDurationType() != null || tfp.getTimeExtensionTypeVisible() != null || tfp.getTimeExtensionTypeDisabled() != null || tfp.getTimeExtensionTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.TIME_EXT_DURATION_TYPE, (tfp.getTimeExtensionDurationType() != null ? String.valueOf(tfp.getTimeExtensionDurationType()) : null), true, tfp.getTimeExtensionTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getTimeExtensionLeadingBidType() != null || tfp.getTimeExtensionTypeVisible() != null || tfp.getTimeExtensionTypeDisabled() != null || tfp.getTimeExtensionTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.TIME_TRIGGER_TYPE, (tfp.getTimeExtensionLeadingBidType() != null ? String.valueOf(tfp.getTimeExtensionLeadingBidType()) : null), true, tfp.getTimeExtensionTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getTimeExtensionLeadingBidValue() != null || tfp.getTimeExtensionTypeVisible() != null || tfp.getTimeExtensionTypeDisabled() != null || tfp.getTimeExtensionTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.TIME_TRIGGER_VAL, (tfp.getTimeExtensionLeadingBidValue() != null ? String.valueOf(tfp.getTimeExtensionLeadingBidValue()) : null), true, tfp.getTimeExtensionTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getExtensionCount() != null || tfp.getTimeExtensionTypeVisible() != null || tfp.getTimeExtensionTypeDisabled() != null || tfp.getTimeExtensionTypeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.EXTENSION_COUNT, (tfp.getExtensionCount() != null ? String.valueOf(tfp.getExtensionCount()) : null), true, tfp.getTimeExtensionTypeDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getAutoDisqualify() != null || tfp.getAutoDisqualifyVisible() != null || tfp.getAutoDisqualifyDisabled() != null || tfp.getAutoDisqualifyOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.IS_AUTO_BIDDER_DISQUALIFY, (tfp.getAutoDisqualify() != null ? String.valueOf(tfp.getAutoDisqualify()) : null), true, tfp.getAutoDisqualifyDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getBidderDisqualify() != null || tfp.getAutoDisqualifyVisible() != null || tfp.getAutoDisqualifyDisabled() != null || tfp.getAutoDisqualifyOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.BIDDER_DISQUALIFY_COUNT, (tfp.getBidderDisqualify() != null ? String.valueOf(tfp.getBidderDisqualify()) : null), true, tfp.getAutoDisqualifyDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			// PH-334
			if (tfp.getExpectedTenderVisible() != null || tfp.getExpectedTenderDisabled() != null || tfp.getExpectedTenderOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.EXPECTED_TENDER_STARTEND_DATTIME, tfp.getExpectedTenderDateTimeRange(), tfp.getExpectedTenderVisible(), tfp.getExpectedTenderDisabled(), tfp.getExpectedTenderOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getExpectedTenderVisible() != null || tfp.getExpectedTenderDisabled() != null || tfp.getExpectedTenderOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.FEE_STARTEND_DATETIME, tfp.getFeeDateTimeRange(), tfp.getExpectedTenderVisible(), tfp.getFeeDisabled(), tfp.getFeeOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getMinimumSupplierRating() != null || tfp.getMinimumSupplierRatingVisible() != null || tfp.getMinimumSupplierRatingDisabled() != null || tfp.getMinimumSupplierRatingOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.MINIMUM_SUPPLIER_RATING, (tfp.getMinimumSupplierRating() != null ? String.valueOf(tfp.getMinimumSupplierRating()) : null), tfp.getMinimumSupplierRatingVisible(), tfp.getMinimumSupplierRatingDisabled(), tfp.getMinimumSupplierRatingOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getMaximumSupplierRating() != null || tfp.getMaximumSupplierRatingVisible() != null || tfp.getMaximumSupplierRatingDisabled() != null || tfp.getMaximumSupplierRatingOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.MAXIMUM_SUPPLIER_RATING, (tfp.getMaximumSupplierRating() != null ? String.valueOf(tfp.getMaximumSupplierRating()) : null), tfp.getMaximumSupplierRatingVisible(), tfp.getMaximumSupplierRatingDisabled(), tfp.getMaximumSupplierRatingOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getEvaluationProcessDeclaration() != null || tfp.getEvaluationDeclarationVisible() != null || tfp.getEvaluationDeclarationDisabled() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.EVALUATION_PROCESS_DECLARATION, (tfp.getEvaluationProcessDeclaration() != null ? tfp.getEvaluationProcessDeclaration().getId() : null), tfp.getEvaluationDeclarationVisible(), tfp.getEvaluationDeclarationDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getSupplierAcceptanceDeclaration() != null || tfp.getSupplierDeclarationVisible() != null || tfp.getSupplierDeclarationDisabled() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.SUPPLIER_ACCEPTANCE_DECLARATION, (tfp.getSupplierAcceptanceDeclaration() != null ? tfp.getSupplierAcceptanceDeclaration().getId() : null), tfp.getSupplierDeclarationVisible(), tfp.getSupplierDeclarationDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getPrebidAsFirstBid() != null || tfp.getPrebidAsFirstBidVisible() != null || tfp.getPrebidAsFirstBidDisabled() != null || tfp.getPrebidAsFirstBidOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.PRE_AS_FIRSTBID, (tfp.getPrebidAsFirstBid() != null ? String.valueOf(tfp.getPrebidAsFirstBid()) : null), true, tfp.getPrebidAsFirstBidDisabled(), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getSubmissionValidityDays() != null || tfp.getSubmissionValidityDaysDisabled() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.SUB_VALIDITY_DAYS, (tfp.getSubmissionValidityDays() != null ? String.valueOf(tfp.getSubmissionValidityDays()) : null), tfp.getSubmissionValidityDaysDisabled(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getDisableTotalAmount() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.DISABLE_TOTAL_AMOUNT, (tfp.getDisableTotalAmount() != null ? String.valueOf(tfp.getDisableTotalAmount()) : null), false, buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getEstimatedBudget() != null || tfp.getEstimatedBudgetVisible() != null || tfp.getEstimatedBudgetDisabled() != null || tfp.getEstimatedBudgetOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.ESTIMATED_BUDGET, (tfp.getEstimatedBudget() != null ? String.valueOf(tfp.getEstimatedBudget()) : null), tfp.getEstimatedBudgetVisible(), tfp.getEstimatedBudgetDisabled(), tfp.getEstimatedBudgetOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getProcurementMethod() != null || tfp.getProcurementMethodVisible() != null || tfp.getProcurementMethodDisabled() != null || tfp.getProcurementMethodOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.PROCUREMENT_METHOD, (tfp.getProcurementMethod() != null ? tfp.getProcurementMethod().getId() : null), tfp.getProcurementMethodVisible(), tfp.getProcurementMethodDisabled(), tfp.getProcurementMethodOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getProcurementCategory() != null || tfp.getProcurementCategoryVisible() != null || tfp.getProcurementCategoryDisabled() != null || tfp.getProcurementCategoryOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.PROCUREMENT_CATEGORY, (tfp.getProcurementCategory() != null ? tfp.getProcurementCategory().getId() : null), tfp.getProcurementCategoryVisible(), tfp.getProcurementCategoryDisabled(), tfp.getProcurementCategoryOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}
			if (tfp.getGroupCode() != null || tfp.getGroupCodeVisible() != null || tfp.getGroupCodeDisabled() != null || tfp.getGroupCodeOptional() != null) {
				TemplateField tf = new TemplateField(RfxTemplateFieldName.GROUP_CODE, (tfp.getGroupCode() != null ? tfp.getGroupCode().getId() : null), tfp.getGroupCodeVisible(), tfp.getGroupCodeDisabled(), tfp.getGroupCodeOptional(), buyer, rfxTemplate);
				rfxTemplate.getFields().add(tf);
			}

		}

		for (TemplateField tf : rfxTemplate.getFields()) {
			LOG.info("Field : " + tf.toLogString());
		}

	}

	private void constructFieldsForDisplay(RfxTemplate rfxTemplate) {

		TemplateFieldPojo tfp = rfxTemplate.getTemplateFieldBinding();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getFields())) {
			tfp = new TemplateFieldPojo();
			for (TemplateField tf : rfxTemplate.getFields()) {
				switch (tf.getFieldName()) {
				case BASE_CURRENCY:
					if (tf.getDefaultValue() != null) {
						tfp.setBaseCurrency(currencyService.getCurrency(tf.getDefaultValue()));
					}
					tfp.setBaseCurrencyVisible(tf.getVisible());
					tfp.setBaseCurrencyOptional(tf.getOptional());
					tfp.setBaseCurrencyDisabled(tf.getReadOnly());
					break;
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
				case DECIMAL:
					if (tf.getDefaultValue() != null) {
						tfp.setDecimal(Integer.valueOf(tf.getDefaultValue()));
					}
					tfp.setDecimalVisible(tf.getVisible());
					tfp.setDecimalOptional(tf.getOptional());
					tfp.setDecimalDisabled(tf.getReadOnly());
					break;
				case EVENT_CATEGORY:
					if (tf.getDefaultValue() != null) {
						tfp.setIndustryCategory(industryCategoryService.getIndustryCategoryById(tf.getDefaultValue()));
					}
					tfp.setIndustryCategoryVisible(tf.getVisible());
					tfp.setIndustryCategoryOptional(tf.getOptional());
					tfp.setIndustryCategoryDisabled(tf.getReadOnly());
					break;
				case EVENT_NAME:
					tfp.setEventName(tf.getDefaultValue());
					tfp.setEventNameVisible(tf.getVisible());
					tfp.setEventNameOptional(tf.getOptional());
					tfp.setEventNameDisabled(tf.getReadOnly());
					break;
				case HISTORIC_AMOUNT:
					if (tf.getDefaultValue() != null) {
						tfp.setHistoricAmount(new BigDecimal(tf.getDefaultValue()));
					}
					tfp.setHistoricAmountVisible(tf.getVisible());
					tfp.setHistoricAmountOptional(tf.getOptional());
					tfp.setHistoricAmountDisabled(tf.getReadOnly());
					break;
				case PAYMENT_TERM:
					tfp.setPaymentTerms(tf.getDefaultValue());
					tfp.setPaymentTermsVisible(tf.getVisible());
					tfp.setPaymentTermsOptional(tf.getOptional());
					tfp.setPaymentTermsDisabled(tf.getReadOnly());
					break;
				case PARTICIPATION_FEES:
					if (tf.getDefaultValue() != null) {
						tfp.setParticipationFees(new BigDecimal(tf.getDefaultValue()));
					}
					tfp.setParticipationFeesVisible(tf.getVisible());
					tfp.setParticipationFeesOptional(tf.getOptional());
					tfp.setParticipationFeesDisabled(tf.getReadOnly());
					break;
				case DEPOSIT:
					if (tf.getDefaultValue() != null) {
						tfp.setDeposit(new BigDecimal(tf.getDefaultValue()));
					}
					tfp.setAddDepositVisible(tf.getVisible());
					tfp.setAddDepositOptional(tf.getOptional());
					tfp.setAddDepositDisabled(tf.getReadOnly());
					break;

				case PARTICIPATION_FEE_CURRENCY:
					if (tf.getDefaultValue() != null) {
						tfp.setParticipationFeeCurrency(currencyService.getCurrency(tf.getDefaultValue()));
					}
					tfp.setParticipationFeesVisible(tf.getVisible());
					tfp.setParticipationFeesOptional(tf.getOptional());
					tfp.setParticipationFeesDisabled(tf.getReadOnly());
					break;

				case DEPOSIT_CURRENCY:
					if (tf.getDefaultValue() != null) {
						tfp.setDepositCurrency(currencyService.getCurrency(tf.getDefaultValue()));
					}
					tfp.setAddDepositVisible(tf.getVisible());
					tfp.setAddDepositOptional(tf.getOptional());
					tfp.setAddDepositDisabled(tf.getReadOnly());
					break;
				case AUCTION_PRICE_TYPE:
					tfp.setAuctionConsolePriceType(AuctionConsolePriceVenderType.valueOf(tf.getDefaultValue()));
					tfp.setAuctionConsolePriceTypeDisabled(tf.getReadOnly());
					break;
				case AUCTION_RANK_TYPE:
					tfp.setAuctionConsoleRankType(AuctionConsolePriceVenderType.valueOf(tf.getDefaultValue()));
					tfp.setAuctionConsoleRankTypeDisabled(tf.getReadOnly());
					break;
				case AUCTION_SUPPLIER_TYPE:
					tfp.setAuctionConsoleVenderType(AuctionConsolePriceVenderType.valueOf(tf.getDefaultValue()));
					tfp.setAuctionConsoleVenderTypeDisabled(tf.getReadOnly());
					break;

				case AUCTION_BUY_PRICE_TYPE:
					tfp.setBuyerAuctionConsolePriceType(AuctionConsolePriceVenderType.valueOf(tf.getDefaultValue()));
					tfp.setBuyerAuctionConsolePriceTypeDisabled(tf.getReadOnly());
					break;
				case AUCTION_BUY_RANK_TYPE:
					tfp.setBuyerAuctionConsoleRankType(AuctionConsolePriceVenderType.valueOf(tf.getDefaultValue()));
					tfp.setBuyerAuctionConsoleRankTypeDisabled(tf.getReadOnly());
					break;
				case AUCTION_BUY_SUPPLIER_TYPE:
					tfp.setBuyerAuctionConsoleVenderType(AuctionConsolePriceVenderType.valueOf(tf.getDefaultValue()));
					tfp.setBuyerAuctionConsoleVenderTypeDisabled(tf.getReadOnly());
					break;

				case BIDDER_DISQUALIFY_COUNT:
					if (tf.getDefaultValue() != null) {
						tfp.setBidderDisqualify(Integer.parseInt(tf.getDefaultValue()));
					}
					break;
				case BIDDING_TYPE:
					if (tf.getDefaultValue() != null) {
						tfp.setBiddingType(tf.getDefaultValue());
					}
					tfp.setBiddingTypeDisabled(tf.getReadOnly());
					break;
				case BID_HIGHER_LEAD_TYPE:
					if (tf.getDefaultValue() != null) {
						tfp.setBiddingPriceHigherLeadingBidType(ValueType.valueOf(tf.getDefaultValue()));
					}
					break;
				case BID_HIGHER_LEAD_VAL:
					if (tf.getDefaultValue() != null) {
						tfp.setBiddingPriceHigherLeadingBidValue(new BigDecimal(tf.getDefaultValue()));
					}
					break;
				case BID_INCR_OWN_PRE_TYPE:
					if (tf.getDefaultValue() != null) {
						tfp.setBiddingMinValueType(ValueType.valueOf(tf.getDefaultValue()));
					}
					break;
				case BID_INCR_OWN_PRE_VAL:
					if (tf.getDefaultValue() != null) {
						tfp.setBiddingMinValue(new BigDecimal(tf.getDefaultValue()));
					}
					break;
				case EXTENSION_COUNT:
					if (tf.getDefaultValue() != null) {
						tfp.setExtensionCount(Integer.parseInt(tf.getDefaultValue()));
					}
					break;
				case IS_AUTO_BIDDER_DISQUALIFY:
					if (tf.getDefaultValue() != null) {
						tfp.setAutoDisqualify(Boolean.valueOf(tf.getDefaultValue()));
					}
					tfp.setAutoDisqualifyVisible(tf.getVisible());
					tfp.setAutoDisqualifyOptional(tf.getOptional());
					tfp.setAutoDisqualifyDisabled(tf.getReadOnly());
					break;
				case IS_BIDDING_SAMEPRICE_SUPPLIER:
					if (tf.getDefaultValue() != null) {
						tfp.setIsBiddingAllowSupplierSameBid(Boolean.valueOf(tf.getDefaultValue()));
					}
					tfp.setIsBiddingAllowSupplierSameBidVisible(tf.getVisible());
					tfp.setIsBiddingAllowSupplierSameBidOptional(tf.getOptional());
					tfp.setIsBiddingAllowSupplierSameBidDisabled(tf.getReadOnly());
					break;
				case IS_BID_HIGHER_LEADING:
					if (tf.getDefaultValue() != null) {
						tfp.setIsBiddingPriceHigherLeadingBid(Boolean.valueOf(tf.getDefaultValue()));
					}
					tfp.setIsBiddingPriceHigherLeadingBidVisible(tf.getVisible());
					tfp.setIsBiddingPriceHigherLeadingBidOptional(tf.getOptional());
					tfp.setIsBiddingPriceHigherLeadingBidDisabled(tf.getReadOnly());
					break;
				case IS_BID_INCR_OWN_PREVIOUS:
					if (tf.getDefaultValue() != null) {
						tfp.setIsBiddingMinValueFromPrevious(Boolean.valueOf(tf.getDefaultValue()));
					}
					tfp.setIsBiddingMinValueFromPreviousVisible(tf.getVisible());
					tfp.setIsBiddingMinValueFromPreviousOptional(tf.getOptional());
					tfp.setIsBiddingMinValueFromPreviousDisabled(tf.getReadOnly());
					break;
				case PRE_BID_BY:
					if (tf.getDefaultValue() != null) {
						tfp.setPreBidBy(PreBidByType.valueOf(tf.getDefaultValue()));
					}
					tfp.setPreBidByDisabled(tf.getReadOnly());
					break;
				case PRE_SUPPLIER_PROVIDE_HIGHER:
					if (tf.getDefaultValue() != null) {
						tfp.setIsPreBidHigherPrice(Boolean.valueOf(tf.getDefaultValue()));
					}
					tfp.setIsPreBidHigherPriceVisible(tf.getVisible());
					tfp.setIsPreBidHigherPriceOptional(tf.getOptional());
					tfp.setIsPreBidHigherPriceDisabled(tf.getReadOnly());
					break;
				case PRE_SUPPLIER_SAME_BID:
					if (tf.getDefaultValue() != null) {
						tfp.setIsPreBidSameBidPrice(Boolean.valueOf(tf.getDefaultValue()));
					}
					tfp.setIsPreBidSameBidPriceVisible(tf.getVisible());
					tfp.setIsPreBidSameBidPriceOptional(tf.getOptional());
					tfp.setIsPreBidSameBidPriceDisabled(tf.getReadOnly());
					break;
				case START_GATE:
					if (tf.getDefaultValue() != null) {
						tfp.setIsStartGate(Boolean.valueOf(tf.getDefaultValue()));
					}
					tfp.setIsStartGateVisible(tf.getVisible());
					tfp.setIsStartGateOptional(tf.getOptional());
					tfp.setIsStartGateDisabled(tf.getReadOnly());
					break;
				case TIME_EXT_DURATION:
					if (tf.getDefaultValue() != null) {
						tfp.setTimeExtensionDuration(Integer.parseInt(tf.getDefaultValue()));
					}
					break;
				case TIME_EXT_DURATION_TYPE:
					if (tf.getDefaultValue() != null) {
						tfp.setTimeExtensionDurationType(DurationType.valueOf(tf.getDefaultValue()));
					}
					break;
				case TIME_EXT_TYPE:
					if (tf.getDefaultValue() != null) {
						tfp.setTimeExtensionType(TimeExtensionType.valueOf(tf.getDefaultValue()));
					}
					tfp.setTimeExtensionTypeVisible(tf.getVisible());
					tfp.setTimeExtensionTypeOptional(tf.getOptional());
					tfp.setTimeExtensionTypeDisabled(tf.getReadOnly());
					break;
				case TIME_TRIGGER_TYPE:
					if (tf.getDefaultValue() != null) {
						tfp.setTimeExtensionLeadingBidType(DurationType.valueOf(tf.getDefaultValue()));
					}
					break;
				case TIME_TRIGGER_VAL:
					if (tf.getDefaultValue() != null) {
						tfp.setTimeExtensionLeadingBidValue(Integer.parseInt(tf.getDefaultValue()));
					}
					break;
				case EXPECTED_TENDER_STARTEND_DATTIME:
					if (tf.getDefaultValue() != null) {
						tfp.setExpectedTenderDateTimeRange(tf.getDefaultValue());
					}
					tfp.setExpectedTenderVisible(tf.getVisible());
					tfp.setExpectedTenderDisabled(tf.getReadOnly());
					tfp.setExpectedTenderOptional(tf.getOptional());
					break;
				case FEE_STARTEND_DATETIME:
					if (tf.getDefaultValue() != null) {
						tfp.setFeeDateTimeRange(tf.getDefaultValue());
					}
					tfp.setFeeVisible(tf.getVisible());
					tfp.setFeeDisabled(tf.getReadOnly());
					tfp.setFeeOptional(tf.getOptional());
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
				case EVALUATION_PROCESS_DECLARATION:
					if (tf.getDefaultValue() != null) {
						tfp.setEvaluationProcessDeclaration(declarationService.getDeclarationById(tf.getDefaultValue()));
					}
					tfp.setEvaluationDeclarationVisible(tf.getVisible());
					tfp.setEvaluationDeclarationDisabled(tf.getReadOnly());
					break;
				case SUPPLIER_ACCEPTANCE_DECLARATION:
					if (tf.getDefaultValue() != null) {
						tfp.setSupplierAcceptanceDeclaration(declarationService.getDeclarationById(tf.getDefaultValue()));
					}
					tfp.setSupplierDeclarationVisible(tf.getVisible());
					tfp.setSupplierDeclarationDisabled(tf.getReadOnly());
					break;

				case PRE_AS_FIRSTBID:
					if (tf.getDefaultValue() != null) {
						tfp.setPrebidAsFirstBid(Boolean.valueOf(tf.getDefaultValue()));
					}
					tfp.setPrebidAsFirstBidVisible(tf.getVisible());
					tfp.setPrebidAsFirstBidOptional(tf.getOptional());
					tfp.setPrebidAsFirstBidDisabled(tf.getReadOnly());
					break;
				case SUB_VALIDITY_DAYS:
					if (tf.getDefaultValue() != null) {
						tfp.setSubmissionValidityDays(Integer.parseInt(tf.getDefaultValue()));
					}
					tfp.setSubmissionValidityDaysDisabled(tf.getReadOnly());
					break;
				case DISABLE_TOTAL_AMOUNT:
					if (tf.getDefaultValue() != null) {
						tfp.setDisableTotalAmount(Boolean.valueOf(tf.getDefaultValue()));
					}
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
				case PRE_SET_SAME_PRE_BID_ALL_SUPPLIER:
					if (tf.getDefaultValue() != null) {
						tfp.setIsPreSetSamePreBidForAllSuppliers(Boolean.valueOf(tf.getDefaultValue()));
					}
					tfp.setIsPreSetSamePreBidForAllSuppliersVisible(tf.getVisible());
					tfp.setIsPreSetSamePreBidForAllSuppliersOptional(tf.getOptional());
					tfp.setIsPreSetSamePreBidForAllSuppliersDisabled(tf.getReadOnly());
					break;
				default:
					break;
				}
			}

		}
		rfxTemplate.setTemplateFieldBinding(tfp);

	}

	private boolean doValidate(RfxTemplate rfxTemplate) {
		boolean validate = true;
		if (rfxTemplateService.isExists(rfxTemplate)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/deleteRfxTemplate", method = RequestMethod.GET)
	public String deleteRfxTemplate(@RequestParam String id, Model model) {
		LOG.info("Delete RfxTemplate called ");
		RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateById(id);
		// Error condition. Send the user back to listing screen.
		if (rfxTemplate == null) {
			model.addAttribute("error", messageSource.getMessage("common.no.records.found", new Object[] { id }, Global.LOCALE));
			return "redirect:rfxTemplateList";
		}
		try {
			rfxTemplateService.delete(rfxTemplate);
			model.addAttribute("success", messageSource.getMessage("rfxTemplate.success.delete", new Object[] { rfxTemplate.getTemplateName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting RfxTemplate [ " + rfxTemplate.getTemplateName() + " ]" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("rfxTemplate.error.delete", new Object[] { rfxTemplate.getTemplateName() }, Global.LOCALE));
		}
		return "rfxTemplateList";
	}

	@RequestMapping(path = "/rfxTemplateList", method = RequestMethod.GET)
	public String rfxTemplateList(Model model) {
		return "rfxTemplateList";
	}

	@RequestMapping(path = "/rfxTemplateData", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfxTemplatePojo>> rfxTemplateData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			TableData<RfxTemplatePojo> data = new TableData<RfxTemplatePojo>(rfxTemplateService.findTemplatesForTenantId(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());
			long totalFilterCount = rfxTemplateService.findTotalFilteredTemplatesForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = rfxTemplateService.findTotalTemplatesForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<RfxTemplatePojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching template list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching template list : " + e.getMessage());
			return new ResponseEntity<TableData<RfxTemplatePojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "copyTemplate", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> copyTemplate(@RequestParam(value = "templateId") String templateId, @RequestParam(value = "templateName") String templateName, @RequestParam(value = "templateDesc") String templateDesc) {
		HttpHeaders headers = new HttpHeaders();
		String newTemplateId = "";
		try {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(templateId, SecurityLibrary.getLoggedInUserTenantId());
			RfxTemplate newTemplate = new RfxTemplate();
			Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
			newTemplate.setBuyer(buyer);
			newTemplate.setType(rfxTemplate.getType());
			newTemplate.setTemplateName(templateName);
			newTemplate.setTemplateDescription(templateDesc);
			newTemplate.setStatus(Status.ACTIVE);

			if (doValidate(newTemplate)) {
				RfxTemplate temp = rfxTemplateService.copyTemplate(rfxTemplate, newTemplate, SecurityLibrary.getLoggedInUser());
				newTemplateId = temp.getId();
				LOG.info("Copied Template Created and Saved Sucessfully  :" + temp.getTemplateName());
				headers.add("success", messageSource.getMessage("rfxTemplate.save.success", new Object[] { URLEncoder.encode(temp.getTemplateName(), StandardCharsets.UTF_8.toString()) }, Global.LOCALE));
			} else {
				headers.add("error", messageSource.getMessage("rfxTemplate.error.duplicate", new Object[] { newTemplate.getTemplateName() }, Global.LOCALE));
				return new ResponseEntity<String>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while save as : " + e.getMessage(), e);
			headers.add("error", "Error while Copying template : " + e.getMessage());
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<String>(newTemplateId, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/addCurrentUsers", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<User>> addUsersToTemplate(String tempId, String userId) {
		LOG.info("tempId " + tempId + " userId " + userId);
		HttpHeaders headers = new HttpHeaders();
		try {
			RfxTemplate template = rfxTemplateService.getRfxTemplateById(tempId);
			User user = userService.getUsersForRfxById(userId);
			List<RfxTemplate> templatesList = user.getAssignedTemplates();

			if (CollectionUtil.isNotEmpty(templatesList)) {
				templatesList.add(template);
			} else {
				templatesList = new ArrayList<>();
				templatesList.add(template);
			}
			headers.add("success", messageSource.getMessage("rfxTemplate.usersave.success", new Object[] {}, Global.LOCALE));
			user.setAssignedTemplates(templatesList);
			userService.updateUser(user);
			List<User> users = rfxTemplateService.getAllUsers(tempId);
			LOG.info("-----------------------" + users.size());
			return new ResponseEntity<List<User>>(users, headers, HttpStatus.OK);
		} catch (Exception e) {
			headers.add("error", messageSource.getMessage("rfxTemplate.usersave.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<User>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
