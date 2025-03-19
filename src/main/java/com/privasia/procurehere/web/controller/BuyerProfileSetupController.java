/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.SecurityTokenDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FreeTrialEnquiry;
import com.privasia.procurehere.core.entity.PasswordSettings;
import com.privasia.procurehere.core.entity.SecurityToken;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.SupplierForm;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.exceptions.SecurityTokenException;
import com.privasia.procurehere.core.exceptions.SecurityTokenExpiredException;
import com.privasia.procurehere.core.exceptions.SecurityTokenInvalidException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.EncryptionUtils;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.FreeTrialEnquiryService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PasswordSettingService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.service.SupplierFormService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.StateEditor;
import com.privasia.procurehere.web.editors.SupplierFormEditor;

/**
 * @author Arc
 */
@Controller
@RequestMapping(path = "/buyer")
public class BuyerProfileSetupController implements Serializable {

	private static final long serialVersionUID = -1374121918315067331L;

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Resource
	MessageSource messageSource;

	@Autowired
	BuyerService buyerService;

	@Autowired
	CountryService countryService;

	@Autowired
	StateService stateService;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	StateEditor stateEditor;

	@Autowired
	UserService userService;

	@Autowired
	SecurityTokenDao securityTokenDao;

	@Value("${app.url}")
	String APP_URL;

	@Value("${app.root.url}")
	String APP_ROOT_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	FreeTrialEnquiryService freeTrialInquiryService;

	@Autowired
	SupplierFormService supplierFormService;

	@ModelAttribute("appUrl")
	public String getAppUrl() {
		return APP_URL;
	}

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	PasswordSettingService passwordSettingService;

	@Autowired
	SupplierFormEditor supplierFormEditor;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Country.class, countryEditor);
		binder.registerCustomEditor(State.class, stateEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		simpleDateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(simpleDateFormat, false));
		binder.registerCustomEditor(SupplierForm.class, supplierFormEditor);

	}

	public List<String> validate(Buyer buyer, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<Buyer>> constraintViolations = validator.validate(buyer, validations);
		for (ConstraintViolation<Buyer> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}

	@ModelAttribute("registeredCountry")
	public List<Country> populateCountries() {
		return countryService.findAllActiveCountries();
	}

	@RequestMapping(value = "/countryStates", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<State>> findStates(@RequestParam("countryId") String countryId) {
		List<State> stateList = null;
		try {
			LOG.info("Country  " + countryId);
			stateList = stateService.statesForCountry(countryId);
			for (State state : stateList) {
				state.setCreatedBy(null);
				state.setModifiedBy(null);
				state.getCountry().setCreatedBy(null);
				state.getCountry().setModifiedBy(null);
			}
		} catch (Exception e) {
			LOG.error("Error while getting states for Country " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while getting states for Country " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.error.state", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<State>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<State>>(stateList, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/buyerTermsOfUse", method = RequestMethod.GET)
	public String buyerTermsOfUse(Model model, @RequestParam(required = false) String d, @RequestParam(required = false) String v) {
		Map<String, String> params = null;
		String email = null;
		String token = null;
		try {
			if (StringUtils.checkString(d).length() == 0 || StringUtils.checkString(v).length() == 0) {
				model.addAttribute("errors", "Your account needs activation. Please click on the link received in the email or contact the support.");
				model.addAttribute("d", "");
				model.addAttribute("v", "");
				model.addAttribute("buyer", new Buyer());
				return "buyerTermsOfUse";
			}
			params = (Map<String, String>) EncryptionUtils.decryptObject(d, v);
			email = params.get("email");
			token = params.get("token");
			LOG.info("Token : " + token + ", Email : " + email);
		} catch (Exception e) {
			LOG.error("Error in request : " + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
			model.addAttribute("d", "");
			model.addAttribute("v", "");
			model.addAttribute("buyer", new Buyer());
			return "buyerTermsOfUse";
		}

		if (StringUtils.checkString(token).length() == 0) {
			LOG.error("Invalid security token");
			model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
			return "buyerProfileSetup";
		}
		if (StringUtils.checkString(email).length() == 0) {
			model.addAttribute("errors", messageSource.getMessage("user.error.security.login", new Object[] {}, Global.LOCALE));
			model.addAttribute("d", "");
			model.addAttribute("v", "");
			model.addAttribute("buyer", new Buyer());
			return "buyerTermsOfUse";
		}
		User user = userService.getUserByLoginId(email);
		if (user == null) {
			LOG.error("Invalid security token");
			model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
			return "resetPassword";
		}

		SecurityToken securityToken = new SecurityToken();
		securityToken.setToken(token);
		securityToken.setUser(user);
		try {
			Buyer buyer = buyerService.findBuyerById(user.getBuyer().getId());
			model.addAttribute("buyer", buyer);
			securityTokenDao.validateToken(securityToken);
			model.addAttribute("d", d);
			model.addAttribute("v", v);
		} catch (SecurityTokenException | SecurityTokenExpiredException | SecurityTokenInvalidException e) {
			LOG.error("Invalid security token : " + e.getMessage());
			model.addAttribute("errors", messageSource.getMessage("user.error.Invalid", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return "buyerTermsOfUse";
	}

	@RequestMapping(value = "/buyerTermsOfUse", method = RequestMethod.POST)
	public ModelAndView buyerTermsOfUseSave(@ModelAttribute("buyer") Buyer buyer, @RequestParam("id") String buyerId, @RequestParam("d") String d, @RequestParam String v, Model model, RedirectAttributes redir) {
		LOG.info("Enter the POST Method of Terms of User");
		List<String> errMessages = new ArrayList<String>();
		try {
			Buyer dbBuyer = buyerService.findBuyerById(buyerId);
			dbBuyer.setTermsOfUseAccepted(Boolean.TRUE);
			dbBuyer.setTermsOfUseAcceptedDate(new Date());
			dbBuyer = buyerService.updateBuyer(dbBuyer);
			if (StringUtils.checkString(buyer.getCompanyRegistrationNumber()).startsWith("DUMMY-")) {
				buyer.setCompanyRegistrationNumber(null);
			}
			if (StringUtils.checkString(buyer.getCompanyName()).startsWith("DUMMY-")) {
				buyer.setCompanyName(null);
			}
			redir.addFlashAttribute("d", d);
			redir.addFlashAttribute("v", v);
			redir.addFlashAttribute("buyer", dbBuyer);
			LOG.info("End the POST Method of Terms of User");
			return new ModelAndView("redirect:buyerProfileSetup");
		} catch (Exception e) {
			LOG.error("Error while storing Buyer Terms of Use : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("buyer.not.accepted.terms", new Object[] {}, Global.LOCALE));
			return new ModelAndView("buyerTermsOfUse");

		}

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/buyerProfileSetup", method = RequestMethod.GET)
	public String buyerProfileView(@ModelAttribute("buyer") Buyer buyer, @RequestParam("d") String d, @RequestParam String v, Model model) {
		LOG.info("Enter the POST Method of Profile setup " + model.asMap().get("d"));
		Map<String, String> params = null;
		String email = null;
		String token = null;
		String tenantId = userService.getAdminUser().getTenantId();
		PasswordSettings passwordSetting = passwordSettingService.getPasswordRegex(tenantId);
		model.addAttribute("regex", passwordSetting);
		LOG.info("REGEX " + passwordSetting.getRegx() + " VAl MESSAGE " + passwordSetting.getMessage());
		// String d = null;
		// String v = null;
		try {
			try {
				// d = (String) model.asMap().get("d");
				// v = (String) model.asMap().get("v");
				if (d == null || v == null) {
					LOG.error("Invalid security token d : " + d + ", v : " + v);
					model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
					return "buyerProfileSetup";
				}
				params = (Map<String, String>) EncryptionUtils.decryptObject(d, v);
				email = params.get("email");
				token = params.get("token");
				LOG.info("Token : " + token + ", Email : " + email);
			} catch (Exception e) {
				LOG.error("Error in request : " + e.getMessage(), e);
				model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
				return "buyerProfileSetup";
			}

			if (StringUtils.checkString(token).length() == 0) {
				LOG.error("Invalid security token");
				model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
				return "buyerProfileSetup";
			}
			if (StringUtils.checkString(email).length() == 0) {
				model.addAttribute("errors", messageSource.getMessage("user.error.security.login", new Object[] {}, Global.LOCALE));

				return "buyerProfileSetup";
			}
			User user = userService.getUserByLoginId(email);
			if (user == null) {
				LOG.error("Invalid security token");
				model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
				return "buyerProfileSetup";
			}

			SecurityToken securityToken = new SecurityToken();
			securityToken.setToken(token);
			securityToken.setUser(user);
			try {
				buyer = buyerService.findBuyerById(user.getBuyer().getId());
				securityTokenDao.validateToken(securityToken);
				if (StringUtils.checkString(buyer.getCompanyRegistrationNumber()).startsWith("DUMMY-")) {
					buyer.setCompanyRegistrationNumber(null);
				}
				if (StringUtils.checkString(buyer.getCompanyName()).startsWith("DUMMY-")) {
					buyer.setCompanyName(null);
				}
				model.addAttribute("buyer", buyer);
				model.addAttribute("countryStates", buyer.getRegistrationOfCountry() != null ? stateService.statesForCountry(buyer.getRegistrationOfCountry().getId()) : null);
				model.addAttribute("d", d);
				model.addAttribute("v", v);
			} catch (SecurityTokenException | SecurityTokenExpiredException | SecurityTokenInvalidException e) {
				LOG.error("Invalid security token : " + e.getMessage());
				model.addAttribute("errors", messageSource.getMessage("user.error.Invalid", new Object[] { e.getMessage() }, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while buyer profile setup : " + e.getMessage(), e);
		}

		return "buyerProfileSetup";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/buyerProfileSetup", method = RequestMethod.POST)
	public String saveBuyerProfile(@ModelAttribute("buyer") Buyer buyer, @RequestParam("id") String id, BindingResult result, Model model, RedirectAttributes redir, @RequestParam String d, @RequestParam String v) {
		Map<String, String> params = null;
		String email = null;
		String tokenId = null;

		List<String> errMessages = validate(buyer, Buyer.BuyerCompleteProfile.class);

		if (CollectionUtil.isNotEmpty(errMessages)) {
			for (ObjectError field : result.getAllErrors()) {
				errMessages.add(field.getDefaultMessage());
			}
			model.addAttribute("allowTocheckTandC", true);
			model.addAttribute("errors", errMessages);
			model.addAttribute("buyer", buyer);
			model.addAttribute("countryStates", buyer.getRegistrationOfCountry() != null ? stateService.statesForCountry(buyer.getRegistrationOfCountry().getId()) : null);
			model.addAttribute("d", d);
			model.addAttribute("v", v);
			return "buyerProfileSetup";
		} else {
			Buyer dbBuyer = buyerService.findBuyerById(id);
			LOG.info("Page submitted......... ");

			try {

				try {
					params = (Map<String, String>) EncryptionUtils.decryptObject(d, v);
					email = params.get("email");
					tokenId = params.get("token");
					LOG.info("Token : " + tokenId + ", Email : " + email);
				} catch (Exception e) {
					LOG.error("Error in request : " + e.getMessage(), e);
					model.addAttribute("allowTocheckTandC", false);
					model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
					return "buyerProfileSetup";
				}

				if (StringUtils.checkString(tokenId).length() == 0) {
					LOG.error("Invalid security token");
					model.addAttribute("allowTocheckTandC", false);
					model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
					return "buyerProfileSetup";
				}
				if (StringUtils.checkString(email).length() == 0) {
					model.addAttribute("allowTocheckTandC", false);
					model.addAttribute("errors", messageSource.getMessage("user.error.security.login", new Object[] {}, Global.LOCALE));
					return "buyerProfileSetup";
				}
				User user = userService.getUserByLoginId(email);
				if (user == null) {
					LOG.error("Invalid security token");
					model.addAttribute("allowTocheckTandC", false);
					model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
					return "buyerProfileSetup";
				}

				SecurityToken securityToken = new SecurityToken();
				securityToken.setToken(tokenId);
				securityToken.setUser(user);
				try {
					securityTokenDao.validateToken(securityToken);
				} catch (SecurityTokenException | SecurityTokenExpiredException | SecurityTokenInvalidException e) {
					LOG.error("Invalid security token : " + e.getMessage());
					model.addAttribute("allowTocheckTandC", false);
					model.addAttribute("errors", messageSource.getMessage("user.error.Invalid", new Object[] { e.getMessage() }, Global.LOCALE));
					return "buyerProfileSetup";
				}
				// now user can not change the company name from front end
				buyer.setId(id);
				if (validate(buyer, model)) {
					model.addAttribute("countryStates", buyer.getRegistrationOfCountry() != null ? stateService.statesForCountry(buyer.getRegistrationOfCountry().getId()) : null);
					return "buyerProfileSetup";
				}

				LOG.info("LOG :  " + buyer.toLogString());
				BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
				String password = enc.encode(buyer.getPassword());

				if (StringUtils.checkString(dbBuyer.getSuspendedRemarks()).length() > 0) {
					String signupRefId = dbBuyer.getSuspendedRemarks();
					try {
						FreeTrialEnquiry freeTrialInquiry = freeTrialInquiryService.findById(signupRefId);
						freeTrialInquiry.setSignupDate(new Date());
						freeTrialInquiryService.updateFreeTrialEnquiry(freeTrialInquiry);
						LOG.info(">>>> Updated Signup complete for ID : " + signupRefId);
					} catch (Exception e) {
						LOG.error("Did not find reference signup id.... " + e.getMessage(), e);
					}
					dbBuyer.setSuspendedRemarks(null);
				}

				if (StringUtils.checkString(dbBuyer.getCompanyName()).isEmpty()) {
					dbBuyer.setCompanyName(buyer.getCompanyName());
				}
				if (StringUtils.checkString(dbBuyer.getCompanyRegistrationNumber()).isEmpty()) {
					dbBuyer.setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
				}

				dbBuyer.setStatus(BuyerStatus.ACTIVE);
				dbBuyer.setMobileNumber(buyer.getMobileNumber());
				dbBuyer.setCompanyContactNumber(buyer.getCompanyContactNumber());
				dbBuyer.setCommunicationEmail(buyer.getCommunicationEmail());
				dbBuyer.setPassword(password);
				dbBuyer.setLine1(buyer.getLine1());
				dbBuyer.setLine2(buyer.getLine2());
				dbBuyer.setCity(buyer.getCity());
				dbBuyer.setPostcode(buyer.getPostcode());
				dbBuyer.setRegistrationOfCountry(buyer.getRegistrationOfCountry());
				dbBuyer.setState(buyer.getState());
				dbBuyer.setRegistrationComplete(Boolean.TRUE);
				dbBuyer.setRegistrationCompleteDate(new Date());

				dbBuyer.setTermsOfUseAccepted(Boolean.TRUE);
				dbBuyer.setTermsOfUseAcceptedDate(new Date());

				LOG.info(dbBuyer.toLogString());
				buyerService.updateBuyerOnly(dbBuyer);

				user.setPassword(password);
				userService.updateUserPlain(user);

				errMessages.add("Buyer Profile Setup has saved successfully");
				redir.addFlashAttribute("info", errMessages);

				SecurityToken token = securityTokenDao.findById(tokenId);
				token.setUsedDate(new Date());
				securityTokenDao.update(token);
				/*
				 * Create default data for Buyer PH-76
				 */
				try {
					buyerService.createBuyerDefaultData(dbBuyer, new Date(), user);
				} catch (Exception e) {
					LOG.error("Error while creating default data for [ " + dbBuyer.getCompanyName() + " ] " + e.getMessage(), e);
				}
				// sending email to user
				sendProfileSetupMail(dbBuyer);
				return "redirect:/login?bsuccess=success";
			} catch (Exception e) {
				LOG.error("Error while storing Buyer : " + e.getMessage(), e);
				model.addAttribute("allowTocheckTandC", false);
				model.addAttribute("errors", messageSource.getMessage("buyer.error.store", new Object[] { e.getMessage() }, Global.LOCALE));
				return "buyerProfileSetup";
			}
		}

	}

	private boolean validate(Buyer buyer, Model model) {
		boolean isError = false;
		if (buyerService.isExists(buyer)) {
			model.addAttribute("errors", messageSource.getMessage("buyer.error.duplicate", new Object[] { buyer.getCompanyName(), buyer.getCompanyRegistrationNumber(), buyer.getRegistrationOfCountry() }, Global.LOCALE));
			isError = true;
		}
		return isError;
	}

	@RequestMapping(value = "/buyerProfileForm", method = RequestMethod.GET)
	public String buyerProfileFormView(@ModelAttribute("buyer") Buyer buyer, Model model) {
		LOG.info("Enter the POST Method of Profile setup ");

		try {
			buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUser().getBuyer().getId());

			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(buyer.getId());
			// LOG.info( " name "+ buyerSettings.getFileName());

			try {

				if (buyerSettings != null && buyerSettings.getFileAttatchment() != null) {
					byte[] encodeBase64 = Base64.encodeBase64(buyerSettings.getFileAttatchment());
					String base64Encoded = new String(encodeBase64, "UTF-8");
					model.addAttribute("logoImg", base64Encoded);

				}
			} catch (Exception e) {
				LOG.error("Error encoding Image" + e.getMessage(), e);
			}

			model.addAttribute("buyer", buyer);
			model.addAttribute("countryStates", buyer.getRegistrationOfCountry() != null ? stateService.statesForCountry(buyer.getRegistrationOfCountry().getId()) : null);
			model.addAttribute("supplierFormList", supplierFormService.getSupplierFormListByTenantId(SecurityLibrary.getLoggedInUserTenantId()));

		} catch (Exception e) {
			LOG.error("Error while buyer profile setup : " + e.getMessage(), e);
		}

		return "buyerProfileForm";
	}

	@RequestMapping(value = "/buyerProfileForm", method = RequestMethod.POST)
	public String saveBuyerProfileForm(@ModelAttribute("buyer") Buyer buyer, @RequestParam(value = "logoImg", required = false) MultipartFile logoImg, @RequestParam(value = "removeFile") boolean removeFile, @RequestParam("id") String id, BindingResult result, Model model, RedirectAttributes redir, @RequestParam String d, @RequestParam String v) {
		String fileName = null;
		List<String> errMessages = new ArrayList<String>();
		if (result.hasErrors()) {
			for (ObjectError field : result.getAllErrors()) {
				errMessages.add(field.getDefaultMessage());
			}
			model.addAttribute("errors", errMessages);

			return "buyerProfileForm";
		} else {

			LOG.info("Page submitted......... " + buyer.toLogString());

			try {
				Buyer dbBuyer = buyerService.findBuyerById(id);
				userService.getUserByLoginId(SecurityLibrary.getLoggedInUser().getId());
				if (StringUtils.checkString(SecurityLibrary.getLoggedInUser().getLoginId()).length() == 0) {
					model.addAttribute("errors", messageSource.getMessage("user.error.security.login", new Object[] {}, Global.LOCALE));

					return "buyerProfileForm";
				}
				User user = userService.getUserByLoginId(SecurityLibrary.getLoggedInUser().getLoginId());
				if (user == null) {
					LOG.error("Invalid security token");
					model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
					return "buyerProfileForm";
				}

				if (validate(buyer, model)) {
					return "buyerProfileForm";
				}

				LOG.info("LOG :  " + buyer.toLogString());

				dbBuyer.setStatus(BuyerStatus.ACTIVE);
				dbBuyer.setMobileNumber(buyer.getMobileNumber());
				dbBuyer.setCompanyContactNumber(buyer.getCompanyContactNumber());
				dbBuyer.setCommunicationEmail(buyer.getCommunicationEmail());
				dbBuyer.setLine1(buyer.getLine1());
				dbBuyer.setLine2(buyer.getLine2());
				dbBuyer.setCity(buyer.getCity());
				dbBuyer.setPostcode(buyer.getPostcode());
				dbBuyer.setRegistrationOfCountry(buyer.getRegistrationOfCountry());
				dbBuyer.setState(buyer.getState());
				dbBuyer.setRegistrationComplete(Boolean.TRUE);
				if (dbBuyer.getRegistrationCompleteDate() == null) {
					dbBuyer.setRegistrationCompleteDate(new Date());
				}

				LOG.info(dbBuyer.toLogString());

				// LOG.info(" **************** 1");
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(buyer.getId());
				if (logoImg != null && !logoImg.isEmpty()) {
					// LOG.info(" *** IF CONDTN CALLED *** ");
					fileName = logoImg.getOriginalFilename();
					LOG.info(logoImg.getContentType() + ": contentType & fileName: " + fileName);
					byte[] bytes = logoImg.getBytes();

					try {
						bytes = resizeLogoImage(logoImg);
					} catch (Exception e) {
						LOG.error("Error while resizing logo image :" + e.getMessage(), e);
					}

					buyerSettings.setContentType(logoImg.getContentType());
					buyerSettings.setFileName(fileName);
					buyerSettings.setFileAttatchment(bytes);
					buyerSettings.setFileSizeKb(bytes.length > 0 ? bytes.length / 1024 : 0);
				}
				if (removeFile) {
					LOG.info(" ****Remove File  ELSE CONDTN CALLED *** ");
					buyerSettings.setContentType(null);
					buyerSettings.setFileName(null);
					buyerSettings.setFileAttatchment(null);
					buyerSettings.setFileSizeKb(null);
				}
				buyerSettingsService.updateBuyerSettings(buyerSettings, user);

				dbBuyer = buyerService.updateBuyer(dbBuyer);

				// errMessages.add("Your Profile Updated Successfully");
				// redir.addFlashAttribute("success", errMessages);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.profile.updated", new Object[] {}, Global.LOCALE));

				model.addAttribute("countryStates", buyer.getRegistrationOfCountry() != null ? stateService.statesForCountry(buyer.getRegistrationOfCountry().getId()) : null);
				return "redirect:/buyer/buyerDashboard";
			} catch (Exception e) {
				LOG.error("Error while storing Buyer : " + e.getMessage(), e);
				model.addAttribute("errors", messageSource.getMessage("buyer.error.store", new Object[] { e.getMessage() }, Global.LOCALE));
				return "buyerProfileForm";
			}
		}
	}

	private void sendProfileSetupMail(Buyer buyer) {
		// Send user creation email
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", buyer.getFullName());
		map.put("loginId", buyer.getLoginEmail());
		map.put("appUrl", APP_URL + "/login");
		map.put("blogUrl", APP_ROOT_URL + "/blog");
		map.put("logoUrl", APP_URL + "/resources/images/public/procuhereLogo.png");
		User user = userService.getDetailsOfLoggedinUser(buyer.getLoginEmail());
		if (user.getEmailNotifications()) {
			notificationService.sendEmail(buyer.getCommunicationEmail(), "Your Account has been Activated", map, Global.BUYER_PROFILE_SETUP);
		}
	}

	private byte[] resizeLogoImage(MultipartFile logoImg) throws IOException {
		byte[] bytes;
		// resizing logo image into 100 X aspect ratio
		InputStream in = new ByteArrayInputStream(logoImg.getBytes());
		BufferedImage originalImage = ImageIO.read(in);
		LOG.info(originalImage.getHeight() + ":h & w:" + originalImage.getWidth());
		int IMG_WIDTH = 270;
		int IMG_HEIGHT = 100;
		double ratioPer = 0;
		ratioPer = originalImage.getWidth() - originalImage.getHeight();
		LOG.info(ratioPer + "===ratioPer 0");
		ratioPer = (ratioPer / originalImage.getHeight()) * 100;
		LOG.info(ratioPer + "===ratioPer 1");
		ratioPer = (IMG_HEIGHT * (ratioPer / 100));
		IMG_WIDTH = IMG_HEIGHT + (int) ratioPer;
		LOG.info(ratioPer + "===ratioPer 2");
		if (IMG_WIDTH > 200) {
			IMG_WIDTH = 200;
		}

		BufferedImage resizedImage = resizeImage(originalImage, IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		LOG.info(resizedImage.getHeight() + ":h & w:" + resizedImage.getWidth());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resizedImage, "png", baos);
		bytes = baos.toByteArray();
		return bytes;
	}

	private BufferedImage resizeImage(BufferedImage originalImage, int IMG_WIDTH, int IMG_HEIGHT, int imageType) {
		LOG.info(" resizeImage method called ");
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, imageType);
		Graphics2D g = resizedImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();
		return resizedImage;
	}

	@RequestMapping(value = "/buyerPublishedProfileForm", method = RequestMethod.POST)
	public String saveBuyerPublishedProfileForm(@ModelAttribute("buyer") Buyer buyer, @RequestParam("id") String id, BindingResult result, Model model, RedirectAttributes redir) {
		try {
			LOG.info("***** User " + SecurityLibrary.getLoggedInUser().getLoginId() + " is updating buyer publish profile.");
			Buyer dbBuyer = buyerService.findBuyerById(id);
			userService.getUserByLoginId(SecurityLibrary.getLoggedInUser().getId());
			if (StringUtils.checkString(SecurityLibrary.getLoggedInUser().getLoginId()).length() == 0) {
				model.addAttribute("errors", messageSource.getMessage("user.error.security.login", new Object[] {}, Global.LOCALE));
				return "buyerProfileForm";
			}
			User user = userService.getUserByLoginId(SecurityLibrary.getLoggedInUser().getLoginId());
			if (user == null) {
				LOG.error("Invalid security token");
				model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
				return "buyerProfileForm";
			}
			String description = null;
			if (buyer.getPublishedProfile() == dbBuyer.getPublishedProfile()) {
				description = " Published Profile updated";
			}
			if (Boolean.TRUE == buyer.getPublishedProfile() && Boolean.FALSE == dbBuyer.getPublishedProfile()) {
				description = " Published Profile updated and published ";
			}
			if (Boolean.FALSE == buyer.getPublishedProfile() && Boolean.TRUE == dbBuyer.getPublishedProfile()) {
				description = " Published Profile updated and unpublished ";
			}

			dbBuyer.setPublishedProfile(buyer.getPublishedProfile());
			dbBuyer.setPublishedProfileCommunicationEmail(buyer.getPublishedProfileCommunicationEmail());
			dbBuyer.setPublishedProfileContactNumber(buyer.getPublishedProfileContactNumber());
			dbBuyer.setPublishedProfileContactPerson(buyer.getPublishedProfileContactPerson());
			dbBuyer.setPublishedProfileWebsite(buyer.getPublishedProfileWebsite());
			dbBuyer.setPublishedProfileInfoToSuppliers(buyer.getPublishedProfileInfoToSuppliers());
			dbBuyer.setPublishedProfileIsAllowIndustryCat(buyer.getPublishedProfileIsAllowIndustryCat());
			dbBuyer.setPublishedProfileMaximumCategories(buyer.getPublishedProfileIsAllowIndustryCat() == false ? null : buyer.getPublishedProfileMaximumCategories());
			dbBuyer.setPublishedProfileMinimumCategories(buyer.getPublishedProfileIsAllowIndustryCat() == false ? null : buyer.getPublishedProfileMinimumCategories());
			dbBuyer.setIsEnablePrequalificationForm(buyer.getIsEnablePrequalificationForm());
			if (Boolean.TRUE == buyer.getIsEnablePrequalificationForm()) {
				dbBuyer.setSupplierForm(buyer.getSupplierForm());
			} else {
				dbBuyer.setSupplierForm(null);
			}
			dbBuyer = buyerService.updateBuyer(dbBuyer);
			try {
				buyerSettingsService.updateBuyerSettingsForPublishedProfile(user, description);
			} catch (Exception e) {
				LOG.error("Error while updating audit" + e.getMessage(), e);
			}
			model.addAttribute("countryStates", buyer.getRegistrationOfCountry() != null ? stateService.statesForCountry(buyer.getRegistrationOfCountry().getId()) : null);
			model.addAttribute("profileMaintenanceFlag", true);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.profile.updated", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/buyerDashboard";
		} catch (Exception e) {
			LOG.error("Error while updating Buyer Publish Profile : " + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("buyer.error.store", new Object[] { e.getMessage() }, Global.LOCALE));
			return "buyerProfileForm";
		}
	}
}