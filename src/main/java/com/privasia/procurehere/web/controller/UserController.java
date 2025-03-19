/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.privasia.procurehere.core.entity.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerPlanDao;
import com.privasia.procurehere.core.dao.PasswordHistoryDao;
import com.privasia.procurehere.core.dao.SecurityTokenDao;
import com.privasia.procurehere.core.dao.SupplierSubscriptionDao;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.RfxTemplateFieldName;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.SecurityTokenException;
import com.privasia.procurehere.core.exceptions.SecurityTokenExpiredException;
import com.privasia.procurehere.core.exceptions.SecurityTokenInvalidException;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.pojo.PasswordPojo;
import com.privasia.procurehere.core.pojo.RecaptchaForm;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.EncryptionUtils;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.PasswordGenerator;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerPlanService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.PasswordSettingService;
import com.privasia.procurehere.service.SourcingTemplateService;
import com.privasia.procurehere.service.UserRoleService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.PrTemplateEditor;
import com.privasia.procurehere.web.editors.RequestTemplateEditor;
import com.privasia.procurehere.web.editors.RfxTemplateEditor;
import com.privasia.procurehere.web.editors.SpTemplateEditor;
import com.privasia.procurehere.web.editors.UserRoleEditor;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import com.privasia.procurehere.web.validator.RecaptchaFormValidator;

/**
 * @author Ravi
 */
@Controller
@RequestMapping(path = "/admin")
@SessionAttributes("roles")
public class UserController implements Serializable {

	private static final long serialVersionUID = 4418186650642482316L;

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	UserService userService;

	@Autowired
	PasswordHistoryDao passwordHistoryDao;

	@Autowired
	private UserRoleEditor userRoleEditor;

	@Autowired
	UserRoleService userRoleService;

	@Autowired
	RecaptchaFormValidator recaptchaFormValidator;

	@Autowired
	SecurityTokenDao securityTokenDao;

	@Autowired
	private RfxTemplateEditor rfxTemplateEditor;

	@Autowired
	PrTemplateEditor prTemplateEditor;

	@Autowired
	RequestTemplateEditor requestTemplateEditor;

	@Autowired
	private BusinessUnitEditor businessUnitEditor;

	@Autowired
	BuyerService buyerService;

	@Autowired
	BuyerPlanDao buyerPlanDao;

	@Autowired
	BuyerPlanService buyerPlanService;

	@Autowired
	SupplierSubscriptionDao supplierSubscriptionDao;

	@Autowired
	SourcingTemplateService sourcingTemplateService;

	@Autowired
	AccessRightsDao accessRightsDao;

	@Autowired
	PasswordSettingService passwordSettingService;
	
	@Autowired
	SupplierService supplierService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	SpTemplateEditor spTemplateEditor;

	/**
	 * reCaptcha related.
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(UserRole.class, userRoleEditor);
		binder.registerCustomEditor(RfxTemplate.class, rfxTemplateEditor);
		binder.registerCustomEditor(PrTemplate.class, prTemplateEditor);
		binder.registerCustomEditor(SourcingFormTemplate.class, requestTemplateEditor);
		binder.registerCustomEditor(SupplierPerformanceTemplate.class, spTemplateEditor);
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@ModelAttribute("userTypeList")
	public List<UserType> getUserTypeList() {
		List<UserType> userTypeList = Arrays.asList(UserType.values());
		return userTypeList;
	}

	/*
	 * @RequestMapping(value = "/supplierSignup", method = RequestMethod.GET) public ModelAndView getCreateUserView() {
	 * LOG.info("Supplier Signup"); return new ModelAndView("supplier-signup", "form", new UserCreateForm()); }
	 */

	@RequestMapping(path = "/recoverPassword", method = RequestMethod.POST)
	public String recoverPassword(Model model, @RequestParam String loginEmail, @RequestParam(name = "g-recaptcha-response", required = true) String recaptchaResponse) {
		LOG.info("*****forgetPassword called ****");
		LOG.info("Recover password requested..." + recaptchaResponse);
		try {
			MapBindingResult result = new MapBindingResult(new HashMap<String, String>(), "recaptcha");
			RecaptchaForm rf = new RecaptchaForm();
			rf.setRecaptchaResponse(recaptchaResponse);
			recaptchaFormValidator.validate(rf, result);

			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					model.addAttribute("error", err.getDefaultMessage());
					LOG.error("Recaptcha Validation Error  " + err.getObjectName() + "  - " + err.getCode() + " -- " + err.getDefaultMessage());
				}
				return "forgetPasswordPage";
			} else {
				User user = userService.getUserByLoginIdNoTouch(loginEmail);
				if (user == null) {
					// Specified login email not found in our system. Please
					// enter correct login email to reset its
					// account password.
					LOG.warn("Invalid login email: No account is registered by the provided login email : " + loginEmail);

					model.addAttribute("error", messageSource.getMessage("user.error.login.email", new Object[] {}, Global.LOCALE));

					return "forgetPasswordPage";
				}
				if (user.isLocked()) {
					LOG.warn("User is Locked: can't able to change password" + loginEmail);
					model.addAttribute("error", messageSource.getMessage("AbstractUserDetailsAuthenticationProvider.locked", new Object[] {}, Global.LOCALE));
					return "forgetPasswordPage";
				}

				userService.sendPasswordResetEmail(user);
			}
		} catch (Exception e) {
			LOG.error("Error during reset of password : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("user.error.reset.password", new Object[] { e.getMessage() }, Global.LOCALE));
			return "forgetPasswordPage";
		}
		return "forgetPasswordThankYou";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/resetPassword", method = { RequestMethod.GET })
	public String resetPassword(Model model, @RequestParam String d, @RequestParam String v) {
		LOG.info("Reset password requested via reset email link...");

		Map<String, String> params = null;
		String email = null;
		String token = null;
		model.addAttribute("tokenInvalidFlag", false);
		try {
			params = (Map<String, String>) EncryptionUtils.decryptObject(d, v);
			email = params.get("email");
			token = params.get("token");
			LOG.info("Token : " + token + ", Email : " + email);
		} catch (Exception e) {
			LOG.error("Error in request : " + e.getMessage(), e);
			model.addAttribute("password", new PasswordPojo());
			model.addAttribute("tokenInvalidFlag", true);
			model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));

			return "resetPassword";
		}

		if (StringUtils.checkString(token).length() == 0) {
			LOG.error("Invalid security token");
			model.addAttribute("tokenInvalidFlag", true);
			model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
			return "resetPassword";
		}
		if (StringUtils.checkString(email).length() == 0) {
			LOG.error("Invalid login email");
			model.addAttribute("password", new PasswordPojo());
			model.addAttribute("tokenInvalidFlag", true);
			model.addAttribute("errors", messageSource.getMessage("user.error.security.login", new Object[] {}, Global.LOCALE));

			return "resetPassword";
		}
		User user = userService.getUserByLoginId(email);
		if (user == null) {
			LOG.error("Invalid security token");
			model.addAttribute("tokenInvalidFlag", true);
			model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
			return "resetPassword";
		}

		SecurityToken securityToken = new SecurityToken();
		securityToken.setToken(token);
		securityToken.setUser(user);
		try {
			securityTokenDao.validateToken(securityToken);
		} catch (SecurityTokenException | SecurityTokenExpiredException | SecurityTokenInvalidException e) {
			LOG.error("Invalid security token : " + e.getMessage());
			model.addAttribute("tokenInvalidFlag", true);
			model.addAttribute("errors", messageSource.getMessage("user.error.Invalid", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		model.addAttribute("password", new PasswordPojo(user.getName(), user.getLoginId()));
		model.addAttribute("regex", passwordSettingService.getPasswordRegex(user.getTenantId()));
		return "resetPassword";
	}

	@RequestMapping(value = "/resetPassword", method = { RequestMethod.POST })
	public String resetPassword(@ModelAttribute("password") PasswordPojo password, Model model, BindingResult result) {
		LOG.info("Reset password attempted...");

		HttpHeaders headers = new HttpHeaders();
		try {
			List<String> errMessages = new ArrayList<String>();
			BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
			User dbUser = validatePassword(password, errMessages, enc, Boolean.TRUE);
			// UPDATE PASSWORD INTO DB
			if (CollectionUtil.isEmpty(errMessages)) {
				// All good.
				LOG.info("password save");
				String newPassword = enc.encode(password.getNewPassword());
				dbUser.setLastPasswordChangedDate(new Date());
				dbUser.setPassword(newPassword);
				dbUser.setFailedAttempts(0);
				userService.updateUserPlain(dbUser);

				if (Global.PASSWORD_HISTORY_ENABLED) {
					PasswordHistory passwordHistory = new PasswordHistory();
					passwordHistory.setPassword(dbUser.getPassword());
					passwordHistory.setPasswordChangedDate(new Date());
					passwordHistory.setUser(dbUser);
					passwordHistoryDao.save(passwordHistory);
				}
			} else {
				model.addAttribute("errors", errMessages);
				model.addAttribute("tokenInvalidFlag", false);
				LOG.info("Reset password else error..." + errMessages);
				return "resetPassword";
			}
		} catch (Exception e) {
			LOG.info("Reset password error..." + e.getMessage());

			headers.add("error", messageSource.getMessage("user.error.reset.password", new Object[] { e.getMessage() }, Global.LOCALE));

			return "resetPassword";
		}
		return "redirect:/logout?pchange=success";
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String loginSuccess() {
		LOG.info("After Login " + SecurityLibrary.getLoggedInUserLoginId());
		try {
			if (SecurityLibrary.getLoggedInUser() != null) {
				if (SecurityLibrary.getLoggedInUser().getOwner() != null) {
					return "supplierSignupList";
				} else if (SecurityLibrary.getLoggedInUser().getSupplier() != null) {
					return "supplierDashboard";
				} else if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
					return "buyerDashboard";
				}
			}
		} catch (Exception e) {
			LOG.error("Error during dashboard access / : " + e.getMessage(), e);
		}
		return "dashboard";
	}

	@RequestMapping(path = "/user", method = RequestMethod.GET)
	public ModelAndView createUsers(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
		LOG.info("Create User Called");
		model.addAttribute("user", new User());
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("userRole", userRoleService.getUserRoles());
		model.addAttribute("editMode", false);

		if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {

			Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
			BuyerPackage buyerPackage = buyer.getBuyerPackage();

			if (buyerPackage != null && buyerPackage.getNoOfUsers() != null && buyerPackage.getUserLimit() != null && (buyerPackage.getNoOfUsers() >= buyerPackage.getUserLimit() && buyerPackage.getNoOfApprovers() >= buyerPackage.getUserLimit())) {
				LOG.info("*****Both condition are true give the error message here");
				redirectAttributes.addFlashAttribute("error", messageSource.getMessage("user.limit.exceeded", new Object[] {}, Global.LOCALE));
				return new ModelAndView("redirect:listUser");
			}

			//cr 4105

			List<BusinessUnit> bizList = userService.getAllBizUnitOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("bizList", bizList);
			LOG.info("bizList.size() "+ bizList.size());
			List<RfxTemplate> tempList = userService.getAllTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("templateList", tempList);
			LOG.info("tempList.size "+ tempList.size());
		}
		if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
			List<PrTemplate> prTempList = userService.getAllPrTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("prTempList", prTempList);
		}
		
		if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
			List<SourcingFormTemplate> sourcingTempList = userService.getAllSourcingTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("sourcingTemplateList", sourcingTempList);
		}
		
		if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
			List<SupplierPerformanceTemplate> spTempList = userService.getAllSpTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("spTempList", spTempList);
		}
		
		model.addAttribute("regex", passwordSettingService.getPasswordRegex(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("isAdmnstrLck", false);
		return new ModelAndView("user", "userObj", new User());
	}

	@RequestMapping(path = "/user", method = RequestMethod.POST)
	public ModelAndView saveUsers(@Valid @ModelAttribute("userObj") User user, BindingResult result, @RequestParam(required = false) String userPassword, Model model, RedirectAttributes redir, RfxTemplate rt) {
		LOG.info("Save User called template size" + (user.getAssignedTemplates() != null ? user.getAssignedTemplates().size() : 0));
		List<String> errMessages = new ArrayList<String>();
		model.addAttribute("userObj", user);

		if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
			List<RfxTemplate> tempList = userService.getAllTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("templateList", tempList);
			List<PrTemplate> prTempList = userService.getAllPrTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("prTempList", prTempList);
			List<SourcingFormTemplate> sourcingTemplateList = userService.getAllSourcingTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("sourcingTemplateList", sourcingTemplateList);
			List<SupplierPerformanceTemplate> spTempList = userService.getAllSpTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("spTempList", spTempList);
			List<BusinessUnit> bizList = userService.getAllBizUnitOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("bizList", bizList);

		}

		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
					LOG.error("Error   " + err.getDefaultMessage());
				}
				model.addAttribute("errors", errMessages);
				return new ModelAndView("user", "userObj", user);
			} else {
				if (doValidate(user, model)) {
					// New mode
					if (StringUtils.checkString(user.getId()).length() == 0) {
						if (StringUtils.checkString(user.getCommunicationEmail()).length() == 0) {
							user.setCommunicationEmail(user.getLoginId().toUpperCase());
						}
						user.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
						user.setTenantType(SecurityLibrary.getLoggedInUser().getTenantType());
						user.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
						user.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
						user.setFinanceCompany(SecurityLibrary.getLoggedInUser().getFinanceCompany());
						user.setOwner(SecurityLibrary.getLoggedInUser().getOwner());
						user.setCreatedBy(SecurityLibrary.getLoggedInUser());
						user.setCreatedDate(new Date());
						user.setAssignedTemplates(user.getAssignedTemplates());
						user.setAssignedSourcingTemplates(user.getAssignedSourcingTemplates());
						user.setAssignedSupplierPerformanceTemplates(user.getAssignedSupplierPerformanceTemplates());
//						if (StringUtils.checkString(userPassword).length() > 0) {
//							BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
//							String password = enc.encode(userPassword);
//							user.setPassword(password);
//						}
						String generatedPassword = PasswordGenerator.generateStrongPassword();
						if (StringUtils.checkString(generatedPassword).length() > 0) {
							BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
							String password = enc.encode(generatedPassword);
							user.setPassword(password);
						}		
						try {
							if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
								Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
								BuyerPackage buyerPackage = buyer.getBuyerPackage();

								if (buyerPackage != null) {
									if (UserType.NORMAL_USER == user.getUserType() && (buyerPackage.getNoOfUsers() >= buyerPackage.getUserLimit()) && user.isActive()) {
										LOG.info("****************Normal User limit exceeded");
										model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
										model.addAttribute("userRole", getUserRoleByUserType(user.getUserType().toString()));
										// model.addAttribute("error", "User limit exceeded");
										model.addAttribute("error", messageSource.getMessage("user.limit.exceeded", new Object[] {}, Global.LOCALE));
										return new ModelAndView("user", "userObj", user);
									} else if (UserType.APPROVER_USER == user.getUserType() && buyerPackage.getNoOfApprovers() >= buyerPackage.getUserLimit() && user.isActive()) {
										model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
										model.addAttribute("userRole", getUserRoleByUserType(user.getUserType().toString()));
										// model.addAttribute("error", "Approver User limit exceeded");
										model.addAttribute("error", messageSource.getMessage("approver.user.limit.exceeded", new Object[] {}, Global.LOCALE));
										return new ModelAndView("user", "userObj", user);
									}
								}
								BuyerSubscription subscription = buyer.getCurrentSubscription();

								if (subscription != null) {
									if (subscription.getPlan().getPlanName().equalsIgnoreCase("FREETRIAL")) {

										user.setShowWizardTutorial(Boolean.TRUE);
									} else {

										user.setShowWizardTutorial(Boolean.FALSE);
									}
								}
							}
							if (SecurityLibrary.getLoggedInUser().getSupplier() != null) {
								SupplierSubscription supplierSubscription = supplierSubscriptionDao.getCurrentSubscriptionForSupplier(SecurityLibrary.getLoggedInUserTenantId());
								if (supplierSubscription != null) {
									if (supplierSubscription.getSupplierPlan() != null) {
										user.setShowWizardTutorial(Boolean.FALSE);
									} else {
										user.setShowWizardTutorial(Boolean.TRUE);
									}

								}
							}
							if(user.isActive()){
								user.setEmailNotifications(Boolean.TRUE);
							}else{
								user.setEmailNotifications(Boolean.FALSE);
							}
							userService.saveUsers(user, generatedPassword);
							redir.addFlashAttribute("success", messageSource.getMessage("user.save.success", new Object[] { user.getName() }, Global.LOCALE));
							LOG.info("create user Called" + SecurityLibrary.getLoggedInUser());
						} catch (Exception e) {
							LOG.info("in create catch: " + e.getMessage(), e);
							model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
							if (user.getUserType() != null) {
								model.addAttribute("userRole", getUserRoleByUserType(user.getUserType().toString()));
							} else {
								model.addAttribute("userRole", userRoleService.getUserRoles());
							}
							model.addAttribute("error", messageSource.getMessage("user.save.error", new Object[] { e.getMessage() }, Global.LOCALE));
							return new ModelAndView("user", "userObj", user);
						}
					} else {
						User persistObj = userService.findUserWithRoleById(user.getId());

						if ((user.isActive() && !persistObj.isActive()) || (persistObj.getUserType() != user.getUserType())) {

							if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
								Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
								BuyerPackage buyerPackage = buyer.getBuyerPackage();
								if (buyerPackage != null && buyerPackage.getNoOfUsers() != null && buyerPackage.getUserLimit() != null && UserType.NORMAL_USER == user.getUserType() && (buyerPackage.getNoOfUsers() >= buyerPackage.getUserLimit())) {
									// buyerPackage.getNoOfApprovers();

									model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
									model.addAttribute("userRole", getUserRoleByUserType(user.getUserType().toString()));
									// model.addAttribute("error", "Normal User limit exceeded");
									model.addAttribute("error", messageSource.getMessage("normal.user.limit.exceeded", new Object[] {}, Global.LOCALE));
									return new ModelAndView("user", "userObj", user);
								} else if (buyerPackage != null && buyerPackage.getNoOfUsers() != null && buyerPackage.getUserLimit() != null && UserType.APPROVER_USER == user.getUserType() && (buyerPackage.getNoOfApprovers() >= buyerPackage.getUserLimit())) {
									model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
									model.addAttribute("userRole", getUserRoleByUserType(user.getUserType().toString()));
									// model.addAttribute("error", "Approver User limit exceeded");
									model.addAttribute("error", messageSource.getMessage("approver.user.limit.exceeded", new Object[] {}, Global.LOCALE));
									return new ModelAndView("user", "userObj", user);
								}

							}
						}
						if (StringUtils.checkString(user.getCommunicationEmail()).length() == 0) {
							persistObj.setCommunicationEmail(user.getLoginId().toUpperCase());
						} else {
							persistObj.setCommunicationEmail(user.getCommunicationEmail().toUpperCase());
						}
						persistObj.setDesignation(user.getDesignation());
						persistObj.setName(user.getName());
						if (persistObj.isLocked() && !user.isLocked()) {
							persistObj.setFailedAttempts(0); // reset failed
																// attempts
						}
						if (user != null && persistObj.getUserRole() != null && user.getUserRole() != null) {
							if ((!user.getUserRole().getId().equals(persistObj.getUserRole().getId())) && persistObj.getUserRole().getRoleName().equalsIgnoreCase("ADMINISTRATOR")) {
								if (!userService.findAvalableAdminUser(persistObj.getId(), persistObj.getTenantId())) {
									redir.addFlashAttribute("error", messageSource.getMessage("no.other.admin.found", new Object[] {}, Global.LOCALE));
									return new ModelAndView("redirect:editUser?id=" + user.getId());
								}
							}
						}

						persistObj.setLocked(user.isLocked());
						persistObj.setPhoneNumber(user.getPhoneNumber());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setUserType(user.getUserType());
						if (CollectionUtil.isNotEmpty(user.getAssignedTemplates())) {
							persistObj.setAssignedTemplates(user.getAssignedTemplates());
						} else {
							persistObj.setAssignedTemplates(null);
						}
						persistObj.setAssignedPrTemplates(user.getAssignedPrTemplates());
						persistObj.setAssignedSourcingTemplates(user.getAssignedSourcingTemplates());
						persistObj.setAssignedSupplierPerformanceTemplates(user.getAssignedSupplierPerformanceTemplates());
						//PH-4105
						persistObj.setAssignedBusinessUnits(user.getAssignedBusinessUnits());
						
						// For suppliers check if the user account is primary account
						boolean isAdmin = false;
						if (TenantType.SUPPLIER == persistObj.getTenantType()) {
							Supplier supplier = supplierService.findSupplierById(persistObj.getTenantId());
							if (StringUtils.equalsIgnoreCase(supplier.getLoginEmail(), persistObj.getLoginId())) {
								isAdmin = true;
							}
						}
						// Skip update of role if it is supplier primary account
						if(!isAdmin) {
							persistObj.setUserRole(user.getUserRole());
							persistObj.setActive(user.isActive());
						}
						
						if (StringUtils.checkString(userPassword).length() > 0) {
							BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
							String password = enc.encode(userPassword);
							persistObj.setPassword(password);
						}

						if(!user.isActive()){
							persistObj.setEmailNotifications(Boolean.FALSE);
						}

						userService.updateUsers(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("user.update.success", new Object[] { user.getName() }, Global.LOCALE));
						LOG.info("update user Called");
					}
					if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
						Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
						if (buyer.getBuyerPackage() != null) {
							BuyerPackage buyerPackage = buyer.getBuyerPackage();

							List<User> normalUserList = userService.fetchAllActiveUsersForTenantAndUserType(SecurityLibrary.getLoggedInUserTenantId(), UserType.NORMAL_USER);
							buyerPackage.setNoOfUsers(CollectionUtil.isNotEmpty(normalUserList) ? normalUserList.size() : 0);

							List<User> approvedUserList = userService.fetchAllActiveUsersForTenantAndUserType(SecurityLibrary.getLoggedInUserTenantId(), UserType.APPROVER_USER);
							buyerPackage.setNoOfApprovers(CollectionUtil.isNotEmpty(approvedUserList) ? approvedUserList.size() : 0);

							buyer.setBuyerPackage(buyerPackage);
							buyerService.updateBuyer(buyer);
						}
					}
				} else {
					model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					if (user.getUserType() != null) {
						model.addAttribute("userRole", getUserRoleByUserType(user.getUserType().toString()));
					} else {
						model.addAttribute("userRole", userRoleService.getUserRoles());
					}
					if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
						// LOG.info("the templates of buyer ::::" +
						// userService.getAllTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId()).size());

						// List<RfxTemplate> tempList =
						// userService.getAllTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
						// model.addAttribute("templateList", tempList);
					}
					return new ModelAndView("user", "userObj", user);
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the User" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("user.save.error", new Object[] { e.getMessage() }, Global.LOCALE));
			if (user.getUserType() != null) {
				model.addAttribute("userRole", getUserRoleByUserType(user.getUserType().toString()));
			} else {
				model.addAttribute("userRole", userRoleService.getUserRoles());
			}
			return new ModelAndView("user", "userObj", user);
		}

		return new ModelAndView("redirect:listUser");
	}

	private boolean doValidate(User user, Model model) {
		PasswordSettings settings = passwordSettingService.getPasswordRegex(user.getTenantId());
		String regex = null;
		String msg = null;
		if (settings != null) {
			regex = settings.getRegx();
			msg = settings.getMessage();
		}
		boolean validate = true;
		if (userService.isExists(user)) {
			model.addAttribute("error", messageSource.getMessage("user.error.duplicate", new Object[] { user.getLoginId() }, Global.LOCALE));
			validate = false;
		}

		if (StringUtils.checkString(user.getPassword()).length() > 0) {

			if (!StringUtils.validatePasswordWithRegx(user.getPassword(), regex)) {
				LOG.info("password is weak " + StringUtils.validatePasswordWithRegx(user.getPassword(), regex));
				model.addAttribute("error", StringUtils.checkString(msg).length() > 0 ? msg : messageSource.getMessage("user.password.week", new Object[] {}, Global.LOCALE));

			}
		}

		return validate;
	}

	@RequestMapping(path = "/listUser", method = RequestMethod.GET)
	public String listUsers(Model model) throws JsonProcessingException {
		List<UserPojo> userList = userService.getAllUserPojo();
		List<TemplateField> templateFields = new ArrayList<TemplateField>();
		templateFields.add(new TemplateField(RfxTemplateFieldName.EVENT_NAME, "Default Name", true, true, false, null, null));
		templateFields.add(new TemplateField(RfxTemplateFieldName.DECIMAL, "Default Phone", true, false, false, null, null));
		templateFields.add(new TemplateField(RfxTemplateFieldName.COST_CENTER, "Default Phone", false, false, false, null, null));
		model.addAttribute("templateFields", templateFields);

		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("createdBy", SecurityLibrary.getLoggedInUserLoginId());
		model.addAttribute("userList", mapper.writeValueAsString(userList));

		return "listUser";
	}

	@RequestMapping(path = "/editUser", method = RequestMethod.GET)
	public ModelAndView editUser(@RequestParam String id, @ModelAttribute User user, Model model) {
		User user1 = userService.getDetailsOfLoggedinBuyerWithTemplates(SecurityLibrary.getLoggedInUserTenantId(), id);
		User user2 = userService.getDetailsOfLoggedinBuyerWithBizUnits(SecurityLibrary.getLoggedInUserTenantId(), id);

		// Merge business units from user2 into user1
		if (CollectionUtil.isNotEmpty(user2.getAssignedBusinessUnits())) {
			user1.setAssignedBusinessUnits(user2.getAssignedBusinessUnits());
			List<BusinessUnit> bizList = userService.getAllBizUnitOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("bizList", bizList);
		}
		else{
			LOG.info("error to display edit page for old users where the user didnt asign business units using checkbox when creating the user ");
			user1.setAssignedBusinessUnits(new ArrayList<>());
			List<BusinessUnit> bizList = userService.getAllBizUnitOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("bizList "+bizList.size());
			model.addAttribute("bizList", bizList);
		}

		// Populate model attributes
		List<RfxTemplate> tempList = userService.getAllTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("templateList", tempList);

		List<PrTemplate> prTempList = userService.getAllPrTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("prTempList", prTempList);

		List<SourcingFormTemplate> sourcingTemplateList = userService.getAllSourcingTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("sourcingTemplateList", sourcingTemplateList);

		List<SupplierPerformanceTemplate> spTempList = userService.getAllSpTemplatesOfBuyer(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("spTempList", spTempList);



		// Add additional user role and other attributes to the model
		if (user1.getUserType() != null) {
			model.addAttribute("userRole", getUserRoleByUserType(user1.getUserType().toString()));
		} else {
			model.addAttribute("userRole", userRoleService.getUserRoles());
		}

		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		model.addAttribute("editMode", true);

		if (user1.isLocked() == true) {
			model.addAttribute("checked", "checked");
		}

		model.addAttribute("regex", passwordSettingService.getPasswordRegex(SecurityLibrary.getLoggedInUserTenantId()));

		boolean isAdmnstrLck = false;
		if (TenantType.SUPPLIER == user1.getTenantType()) {
			Supplier supplier = supplierService.findSupplierById(user1.getTenantId());
			if (StringUtils.equalsIgnoreCase(supplier.getLoginEmail(), user1.getLoginId())) {
				isAdmnstrLck = true;
			}
		}
		model.addAttribute("isAdmnstrLck", isAdmnstrLck);

		// Return the updated user object
		return new ModelAndView("user", "userObj", user1);
	}

	@RequestMapping(path = "/deleteUser", method = RequestMethod.GET)
	public String deleteUser(@RequestParam String id, Model model, RedirectAttributes reAttributes) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String userName = "";
		User user = userService.getUsersById(id);
		LOG.info("Delete User  " + userService.getUsersById(id));
		try {
			userName = user.getName();
			userService.deleteUsers(user);
			reAttributes.addFlashAttribute("success", messageSource.getMessage("user.success.delete", new Object[] { user.getName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting UserRole [ " + userName + " ]" + e.getMessage(), e);
			reAttributes.addFlashAttribute("error", messageSource.getMessage("user.error.delete", new Object[] { user.getName() }, Global.LOCALE));
			return null;
		}
		List<UserPojo> userList = userService.getAllUserPojo();
		model.addAttribute("userList", mapper.writeValueAsString(userList));
		return "listUser";
	}

	@RequestMapping(path = "/changePassword", method = RequestMethod.GET)
	public String changePassword(@ModelAttribute PasswordPojo passwordPojo, Model model) {
		model.addAttribute("regex", passwordSettingService.getPasswordRegex(SecurityLibrary.getLoggedInUserTenantId()));
		LOG.info("regex " + passwordSettingService.getPasswordRegex(SecurityLibrary.getLoggedInUserTenantId()).getRegx() + " regex message " + passwordSettingService.getPasswordRegex(SecurityLibrary.getLoggedInUserTenantId()).getMessage());
		model.addAttribute("regexMessage", passwordSettingService.buildPasswordRegexMessage(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("passwordObj", new PasswordPojo());
		return "changePassword";
	}

	@RequestMapping(path = "/changePassword", method = RequestMethod.POST)
	public String savePassword(@Valid @ModelAttribute("passwordObj") PasswordPojo password, BindingResult result, Model model) {
		model.addAttribute("regex", passwordSettingService.getPasswordRegex(SecurityLibrary.getLoggedInUserTenantId()));
		List<String> errMessages = new ArrayList<String>();
		try {

			if (result.hasErrors()) {
				for (ObjectError oe : result.getAllErrors()) {
					errMessages.add(oe.getDefaultMessage());
				}
			}
			BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
			User dbUser = validatePassword(password, errMessages, enc, Boolean.FALSE);

			// UPDATE PASSWORD INTO DB
			if (CollectionUtil.isEmpty(errMessages)) {
				// All good.
				LOG.info("password save");
				String newPassword = enc.encode(password.getNewPassword());
				dbUser.setLastPasswordChangedDate(new Date());
				dbUser.setPassword(newPassword);
				dbUser.setFailedAttempts(0);
				userService.updateUserPlain(dbUser);

				if (Global.PASSWORD_HISTORY_ENABLED) {
					PasswordHistory passwordHistory = new PasswordHistory();
					passwordHistory.setPassword(dbUser.getPassword());
					passwordHistory.setPasswordChangedDate(new Date());
					passwordHistory.setUser(dbUser);
					passwordHistoryDao.save(passwordHistory);
				}

				/*
				 * UPDATE THE SECURITY CONTEXT AS THE PASSWORD IS NOW CHANGED.
				 */
				// gonna need this to get user from Acegi
				SecurityContext ctx = SecurityContextHolder.getContext();
				Authentication auth = ctx.getAuthentication();
				// get user obj
				AuthenticatedUser authUser = (AuthenticatedUser) auth.getPrincipal();
				// update the password on the user obj
				authUser.setPassword(dbUser.getPassword());
				authUser.setPasswordExpired(false);
				// Tell Acegi about the changes: update the
				// SecurityContextHolder
				// (see
				// org.acegisecurity.providers.dao.AbstractUserDetailsAuthenticationProvider.createSuccessAuthentication())
				UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(authUser, auth.getCredentials(), authUser.getAuthorities());
				upat.setDetails(auth.getDetails());
				ctx.setAuthentication(upat);

			}

		} catch (Exception e) {
			LOG.error("Error While Save the User" + e.getMessage(), e);
			errMessages.add(messageSource.getMessage("user.password.change", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		model.addAttribute("error", errMessages);
		if (CollectionUtil.isNotEmpty(errMessages)) {
			return "changePassword";
		} else {
			return "redirect:/logout?pchange=true";
		}

	}

	/**
	 * @param password
	 * @param errMessages
	 * @param enc
	 * @param bypassOldPasswordValidation
	 * @return
	 */
	private User validatePassword(PasswordPojo password, List<String> errMessages, BCryptPasswordEncoder enc, Boolean bypassOldPasswordValidation) {
		LOG.info("validatePassword");
		User dbUser = userService.getUserByLoginIdNoTouch((Boolean.TRUE == bypassOldPasswordValidation ? password.getLoginEmail() : SecurityLibrary.getLoggedInUser().getLoginId()));
		String regex = null;
		String msg = null;
		if (dbUser != null) {
			PasswordSettings setting = passwordSettingService.getPasswordRegex(dbUser.getTenantId());
			if (setting != null) {
				regex = setting.getRegx();
				msg = setting.getMessage();
			}
		}
		if (!password.getNewPassword().equals(password.getConfirmPassword())) {
			// new and confirm do not match
			errMessages.add(messageSource.getMessage("user.changepassword.equalpasswords", new Object[] {}, Global.LOCALE));
		}
		if (!StringUtils.validatePasswordWithRegx(password.getNewPassword(), regex)) {
			// weak password...
			errMessages.add(StringUtils.checkString(msg).length() > 0 ? msg : messageSource.getMessage("user.password.week", new Object[] {}, Global.LOCALE));
		}

		if (dbUser == null) {
			errMessages.add(messageSource.getMessage("user.error.login.email", new Object[] {}, Global.LOCALE));
			return dbUser;
		}

		if (Boolean.FALSE == bypassOldPasswordValidation && !enc.matches(password.getOldPassword(), dbUser.getPassword())) {
			// old password does not match
			errMessages.add(messageSource.getMessage("user.changepassword.old.password.wrong", new Object[] {}, Global.LOCALE));
		}

		if (CollectionUtil.isEmpty(errMessages)) {

			PasswordSettings passwordSetting = passwordSettingService.getPasswordSettingsByTenantId(dbUser.getTenantId());
			boolean enablePasswordReuse = false;
			int numberOfPasswordRemember = Global.PASSWORD_HISTORY_COUNT;
			if (passwordSetting != null) {
				enablePasswordReuse = passwordSetting.getEnableReusePassword() != null ? (!passwordSetting.getEnableReusePassword()) : Global.PASSWORD_HISTORY_ENABLED;
				numberOfPasswordRemember = passwordSetting.getNumberOfPasswordRemember() != null ? passwordSetting.getNumberOfPasswordRemember() : Global.PASSWORD_HISTORY_COUNT;
			}
			if (enablePasswordReuse) {
				List<PasswordHistory> pasList = new ArrayList<>();
				List<PasswordHistory> passwordHistories = passwordHistoryDao.findPasswordHistoryByUserId(dbUser.getId());
				if (passwordHistories != null && passwordHistories.size() > 0) {
					int count = 1;
					for (PasswordHistory passwordHistory : passwordHistories) {
						if (count <= numberOfPasswordRemember && enc.matches(password.getNewPassword(), passwordHistory.getPassword())) {
							errMessages.add(messageSource.getMessage("user.changepassword.password.exist.inhistory", new Object[] {}, Global.LOCALE));
							return dbUser;
						}
						count++;
						/**
						 * If the history count greater than 6 system will identify those records for deletion
						 **/
						if (count > numberOfPasswordRemember)
							pasList.add(passwordHistory);
					}

					if (!pasList.isEmpty() && pasList.size() > 0) {
						for (PasswordHistory pasHistory : pasList) {
							passwordHistoryDao.delete(pasHistory);
							LOG.info("Password history deleted for : " + dbUser.getLoginId());
						}
					}
				}
			}
		}
		return dbUser;
	}

	/*
	 * End
	 */

	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	public String getAllUsers(ModelMap model) {
		List<User> users = userService.getUsers();
		model.addAttribute("users", users);
		model.addAttribute("loggedinuser", SecurityLibrary.getLoggedInUserLoginId());
		return "userslist";
	}

	@RequestMapping(value = { "/userslist" }, method = RequestMethod.GET)
	public String getUserlist(ModelMap model) {
		List<User> users = userService.getUsers();
		model.addAttribute("users", users);
		model.addAttribute("loggedinuser", SecurityLibrary.getLoggedInUserLoginId());
		return "userslist";
	}

	@RequestMapping(path = "/userListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<UserPojo>> userListData(TableDataInput input) throws JsonProcessingException {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			// LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<UserPojo> data = new TableData<UserPojo>(userService.findUserForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = userService.findTotalFilteredUserForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = userService.findTotalUserForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<UserPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching user list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching template list : " + e.getMessage());
			return new ResponseEntity<TableData<UserPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/getUserRole", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<UserRole>> getUserRole(@RequestParam("userType") String userType) {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(userType).length() > 0) {
				List<UserRole> list = getUserRoleByUserType(userType);
				return new ResponseEntity<List<UserRole>>(list, HttpStatus.OK);
			} else {
				return new ResponseEntity<List<UserRole>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception e) {
			LOG.error("Error while get user role :" + e.getMessage(), e);
			headers.add("error", "Error get user role : " + e.getMessage());
			return new ResponseEntity<List<UserRole>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private List<UserRole> getUserRoleByUserType(String userType) {
		List<UserRole> list = new ArrayList<>();
		if (StringUtils.checkString(userType).length() > 0) {
			if (UserType.NORMAL_USER == UserType.fromString(userType.trim())) {
				list = userRoleService.getUserRoles();
			} else if (UserType.REQUESTOR_USER == UserType.fromString(userType.trim())) {
				// sending BUYER APPROVER USER role on changing user type from
				// NORMAL USER to APPROVER USER
				UserRole userRole = userRoleService.findByUserRoleAndTenantId("BUYER REQUESTER USER".toUpperCase(), SecurityLibrary.getLoggedInUserTenantId());

				if (userRole != null) {
					list.add(userRole);
				} else {
					LOG.info("***************************response is : " + userType);
					// This is added because create default BUYER REQUESTER USER role for older buyer implemented for PH
					// 72
					try {
						if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
							UserRole role = new UserRole();
							role.setRoleName("Buyer Requester User".toUpperCase());
							role.setRoleDescription("Buyer Requester User Role only for Requester user type can't be modify or delete");
							role.setAccessControlList(accessRightsDao.findCustomeAccessForBuyer(Global.BUYER_DEFAULT_REQUESTER_USER_ACL_VALUES));
							role.setReadOnly(Boolean.TRUE);
							role.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
							role.setCreatedBy(SecurityLibrary.getLoggedInUser());
							role.setCreatedDate(new Date());
							role = userRoleService.saveUserRole(role, SecurityLibrary.getLoggedInUser());
							list.add(role);
							LOG.info("***************************response is : " + role.getId());
						}
					} catch (Exception e) {
						LOG.info("error while create REQUESTER USER" + e.getMessage(), e);
					}
				}
				// if (CollectionUtil.isNotEmpty(list)) {
				// for (UserRole role : list) {
				// LOG.info("approver UserRole:" + role.getRoleName());
				// }
				// }
			} else {
				// sending BUYER APPROVER USER role on changing user type from NORMAL USER to APPROVER USER
				UserRole userRole = userRoleService.findByUserRoleAndTenantId("BUYER APPROVER USER".toUpperCase(), SecurityLibrary.getLoggedInUserTenantId());
				LOG.info("***************************response is : " + userType);
				if (userRole != null) {
					list.add(userRole);
				}
				if (CollectionUtil.isNotEmpty(list)) {
					for (UserRole role : list) {
						LOG.info("approver UserRole:" + role.getRoleName());
					}
				}
			}
		}
		return list;
	}

	@RequestMapping(path = "/forgetPassword", method = RequestMethod.GET)
	public String forgetPassword(Model model) {
		LOG.info("*****forgetPassword called ****");
		return "forgetPasswordPage";
	}

	@RequestMapping(path = "/forgetPasswordThankYou", method = RequestMethod.GET)
	public String forgetPasswordThankYou(Model model) {
		LOG.info("*****forgetPasswordThankYou called****");
		return "forgetPasswordThankYou";
	}

	@RequestMapping(path = "/userTemplate", method = RequestMethod.GET)
	public void downloadUserListExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			userService.userDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());
		} catch (Exception e) {
			LOG.error("Error while downloading Buyer Address  template :: " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/downloadZip", method = RequestMethod.POST)
	public void downloadUserZip(HttpServletResponse response, HttpSession session) throws Exception {
		String fileName = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {

			fileName = userService.generateUserZip(zos, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), SecurityLibrary.getLoggedInUser());
			zos.close();
			response.setContentType("application/zip,application/octet-stream");
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.getOutputStream().write(baos.toByteArray());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			LOG.error("Error zipping event evaluation report for download : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/showWizardTutorial/{show}", method = RequestMethod.GET)
	public @ResponseBody void showWizardTutorial(@PathVariable(name = "show") Boolean show, HttpSession session) {
		LOG.info("******show Wizard Tutorial method call********");

		User user = userService.getUsersById(SecurityLibrary.getLoggedInUser().getId());

		if (user != null) {
			user.setShowWizardTutorial(show);
			userService.updateUserPlain(user);
			session.setAttribute("showWizardTutorial", show);
			updateSecurityLibraryUser(show);
		}
	}

	@RequestMapping(path = "/unsubscribeEmailNotifications/{show}", method = RequestMethod.GET)
	public @ResponseBody void showUnsubscribeEmailNotifications(@PathVariable(name = "show") Boolean show, HttpSession session) {
		LOG.info("******show Unsubscribe method call********");

		User user = userService.getUsersById(SecurityLibrary.getLoggedInUser().getId());

		if (user != null) {
			user.setEmailNotifications(show);
			userService.updateUserPlain(user);
			session.setAttribute("unsubscribeEmailNotifications", show);
			updateSecurityLibraryUser1(show);
		}
	}

	private void updateSecurityLibraryUser(Boolean showWizardTutorial) {
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		AuthenticatedUser authUser = (AuthenticatedUser) auth.getPrincipal();
		authUser.setIsFreeTrial(showWizardTutorial);
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(authUser, auth.getCredentials(), authUser.getAuthorities());
		upat.setDetails(auth.getDetails());
		ctx.setAuthentication(upat);
	}

	private void updateSecurityLibraryUser1(Boolean unsubscribeEmailNotifications) {
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		AuthenticatedUser authUser = (AuthenticatedUser) auth.getPrincipal();
		authUser.setIsUnSubscribeEmail(unsubscribeEmailNotifications);
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(authUser, auth.getCredentials(), authUser.getAuthorities());
		upat.setDetails(auth.getDetails());
		ctx.setAuthentication(upat);
	}

	@RequestMapping(path = "/changeLocal", method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> changeLocal(@RequestParam("lang") String lang) {

		Map<String, String> map = new HashMap<String, String>();

		try {
			User user = SecurityLibrary.getLoggedInUser();
			userService.updateLangCodeForUser(user.getId(), lang);
			updateSecurityLibraryLanguageCode(lang);
		} catch (Exception e) {
			map.put("error", "Failed to change language to " + lang);
			return new ResponseEntity<Map<String, String>>(map, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// if (user.getBuyer() != null) {
		// return "redirect:/buyer/buyerDashboard";
		// } else if (user.getSupplier() != null) {
		// return "redirect:/supplier/supplierDashboard";
		// } else if (user.getFinanceCompany() != null) {
		// return "redirect:/finance/financeDashboard";
		// } else {
		// return "redirect:/owner/ownerDashboard";
		// }

		map.put("success", "Language changed to " + lang);
		return new ResponseEntity<Map<String, String>>(map, HttpStatus.OK);

	}

	private void updateSecurityLibraryLanguageCode(String lang) {
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		AuthenticatedUser authUser = (AuthenticatedUser) auth.getPrincipal();
		authUser.setLanguageCode(lang);
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(authUser, auth.getCredentials(), authUser.getAuthorities());
		upat.setDetails(auth.getDetails());
		ctx.setAuthentication(upat);
	}

	/**
	 * @param searchSupplier
	 * @return
	 */
	@RequestMapping(value = "searchUserName/{userType}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<UserPojo>> searchUser(@RequestParam("search") String searchValue, @PathVariable("userType") String userType) {
		LOG.info("User Type : " + userType);
		UserType type = null;
		if (StringUtils.checkString(userType).equals(UserType.NORMAL_USER.name())) {
			type = UserType.NORMAL_USER;
		}
		if (StringUtils.checkString(userType).equals(UserType.APPROVER_USER.name())) {
			type = UserType.APPROVER_USER;
		}
		// TODO: Confirm with privasia if user type should be activated especially for Approval Type users. If yes, pass
		// the userType in below method.
		return new ResponseEntity<List<UserPojo>>(userService.fetchAllUsersForPoApproval(SecurityLibrary.getLoggedInUserLoginId(),SecurityLibrary.getLoggedInUserTenantId(), searchValue, type), HttpStatus.OK);
	}

	/**
	 * @param searchSupplier
	 * @return
	 */
	@RequestMapping(value = "searchUserNameForRfxTemplate/{userType}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<UserPojo>> searchUser(@RequestParam("search") String searchValue, @RequestParam("templateId") String templateId, @PathVariable("userType") String userType) {
		LOG.info("User Type : " + userType);
		UserType type = null;
		if (StringUtils.checkString(userType).equals(UserType.NORMAL_USER.name())) {
			type = UserType.NORMAL_USER;
		}
		if (StringUtils.checkString(userType).equals(UserType.APPROVER_USER.name())) {
			type = UserType.APPROVER_USER;
		}
		return new ResponseEntity<List<UserPojo>>(userService.fetchAllUsersForTenantForRfxTemplate(SecurityLibrary.getLoggedInUserTenantId(), searchValue, type, templateId), HttpStatus.OK);
	}

	/**
	 * @param searchUser
	 * @return
	 */
	@RequestMapping(value = "searchUserNameForPoApproval/{userType}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<UserPojo>> searchUserNameForApproval(@RequestParam("search") String searchValue, @PathVariable("userType") String userType) {
		LOG.info("User Type : " + userType);
		UserType type = null;
		if (StringUtils.checkString(userType).equals(UserType.NORMAL_USER.name())) {
			type = UserType.NORMAL_USER;
		}
		if (StringUtils.checkString(userType).equals(UserType.APPROVER_USER.name())) {
			type = UserType.APPROVER_USER;
		}
		return new ResponseEntity<List<UserPojo>>(userService.fetchAllUsersForPoApproval(SecurityLibrary.getLoggedInUserLoginId(),SecurityLibrary.getLoggedInUserTenantId(), searchValue, type), HttpStatus.OK);
	}

	/**
	 * @param searchSupplier
	 * @return
	 */
	@RequestMapping(value = "searchPrTemplateUserName/{userType}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<UserPojo>> searchPrUser(@RequestParam("search") String searchValue, @RequestParam("templateId") String templateId, @PathVariable("userType") String userType) {
		LOG.info("User Type : " + userType);
		UserType type = null;
		if (StringUtils.checkString(userType).equals(UserType.NORMAL_USER.name())) {
			type = UserType.NORMAL_USER;
		}
		if (StringUtils.checkString(userType).equals(UserType.APPROVER_USER.name())) {
			type = UserType.APPROVER_USER;
		}
		return new ResponseEntity<List<UserPojo>>(userService.fetchAllUsersForTenantForPrTemplate(SecurityLibrary.getLoggedInUserTenantId(), searchValue, type, templateId), HttpStatus.OK);
	}

	@RequestMapping(value = "/getUserProfile", method = RequestMethod.GET)
	public void getUserProfile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = userService.getUserByLoginId(SecurityLibrary.getLoggedInUserLoginId());
		InputStream in = request.getServletContext().getResourceAsStream("/resources/images/profile.png");
		if (user.getPrfPicAttatchment() != null) {
			in = new ByteArrayInputStream(user.getPrfPicAttatchment());
		}
		response.setContentType(StringUtils.checkString(user.getPrfPiccontentType()).length() > 0 ? user.getPrfPiccontentType() : "image/png");
		IOUtils.copy(in, response.getOutputStream());
	}
	
	@RequestMapping(path = "/userTemplateCsv", method = RequestMethod.GET)
	public void downloadUserListCsv(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
		try {
			File file = File.createTempFile("User", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());
			
			userService.downloadUserCsvFile(response, file,  SecurityLibrary.getLoggedInUserTenantId(), session);
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "User Report is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.User);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=User.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading User template :: " + e.getMessage(), e);
		}

	}
	
	@RequestMapping(value = "searchSourcingTemplateUserName/{userType}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<UserPojo>> searchSorcingTemplateUser(@RequestParam("search") String searchValue, @RequestParam("templateId") String templateId, @PathVariable("userType") String userType) {
		LOG.info("User Type : " + userType);
		UserType type = null;
		if (StringUtils.checkString(userType).equals(UserType.NORMAL_USER.name())) {
			type = UserType.NORMAL_USER;
		}
		if (StringUtils.checkString(userType).equals(UserType.APPROVER_USER.name())) {
			type = UserType.APPROVER_USER;
		}
		return new ResponseEntity<List<UserPojo>>(userService.fetchAllUsersForTenantForSourcingTemplate(SecurityLibrary.getLoggedInUserTenantId(), searchValue, type, templateId), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "searchSPTemplateUserName/{userType}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<UserPojo>> searchSpTemplateUser(@RequestParam("search") String searchValue, @RequestParam("templateId") String templateId, @PathVariable("userType") String userType) {
		LOG.info("User Type : " + userType);
		UserType type = null;
		if (StringUtils.checkString(userType).equals(UserType.NORMAL_USER.name())) {
			type = UserType.NORMAL_USER;
		}
		if (StringUtils.checkString(userType).equals(UserType.APPROVER_USER.name())) {
			type = UserType.APPROVER_USER;
		}
		return new ResponseEntity<List<UserPojo>>(userService.fetchAllUsersForTenantForSPTemplate(SecurityLibrary.getLoggedInUserTenantId(), searchValue, type, templateId), HttpStatus.OK);
	}
}
