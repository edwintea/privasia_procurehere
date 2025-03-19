/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.PasswordSettings;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.Supplier.SupplierSignup;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PoShare;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.PasswordSettingService;
import com.privasia.procurehere.service.SupplierPlanService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.TimeZoneService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.validator.RecaptchaFormValidator;

/**
 * @author Arc
 */
@Controller
@RequestMapping(value = "/")
public class SupplierSignupController implements Serializable {

	private static final long serialVersionUID = -3511236085076614718L;

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	SupplierService supplierService;

	@Autowired
	CountryService countryService;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	UserService userService;

	@Autowired
	RecaptchaFormValidator recaptchaFormValidator;

	@Autowired
	SupplierPlanService supplierPlanService;

	@Value("${paypal.merchant.id}")
	String MERCHANT_ID;

	@Value("${paypal.environment}")
	String PAYPAL_ENVIRONMENT;

	@Autowired
	TimeZoneService timeZoneService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	PasswordSettingService passswordSettingService;

	@ModelAttribute("recaptchaSiteKey")
	public String getRecaptchaSiteKey(@Value("${recaptcha.site-key}") String recaptchaSiteKey) {
		return recaptchaSiteKey;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Country.class, countryEditor);
		binder.addValidators(recaptchaFormValidator);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@RequestMapping(value = "/supplierSignup/{planId}", method = RequestMethod.GET)
	public String supplierSubscriptionSignupView(Model model, @PathVariable(name = "planId") String planId) {

		Supplier supplier = new Supplier();
		Country my = countryService.getCountryByCode("MY");
		supplier.setRegistrationOfCountry(my);

		LOG.info("User requested to purchase Plan : " + planId);
		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);

		if (plan == null) {

		}

		SupplierSubscription subscription = new SupplierSubscription();
		subscription.setSupplierPlan(plan);
		subscription.setSupplier(supplier);
		subscription.setPaymentTransaction(new PaymentTransaction());
		subscription.getPaymentTransaction().setCountry(countryService.getCountryByCode("MY"));

		model.addAttribute("subscription", subscription);
		model.addAttribute("merchantId", this.MERCHANT_ID);
		model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
		model.addAttribute("plan", plan);
		model.addAttribute("countryList", countryService.getAllCountries());

		model.addAttribute("supplier", supplier);
		return "supplierSignup";
	}

	@RequestMapping(value = "/supplierSignup", method = RequestMethod.GET)
	public String supplierSignupView(Model model) {

		Supplier supplier = new Supplier();
		Country my = countryService.getCountryByCode("MY");
		supplier.setRegistrationOfCountry(my);

		model.addAttribute("countryList", countryService.getAllCountries());

		model.addAttribute("supplier", supplier);
		return "supplierSignup";
	}

	@RequestMapping(value = "/supplierSignup", method = RequestMethod.POST)
	public String saveRegistration(@Validated(value = { SupplierSignup.class }) @ModelAttribute("supplier") Supplier supplier, BindingResult result, Model model) {
		User createdBy = null;
		try {
			createdBy = SecurityLibrary.getLoggedInUser();
		} catch (Exception e) {
			createdBy = userService.getAdminUserForBuyer(SecurityLibrary.getLoggedInUserTenantId());
		}

		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			List<String> errMessages = new ArrayList<String>();
			for (ObjectError field : result.getAllErrors()) {
				LOG.info("ERROR : " + field.getObjectName() + " : " + field.getDefaultMessage());
				errMessages.add(field.getDefaultMessage());
			}
			model.addAttribute("errors", errMessages);

			return "supplierSignup";
		} else {
			LOG.info("Page submitted....................................... " + supplier.toString());
			try {
				if (validate(supplier, model)) {
					return "supplierSignup";
				}
				supplier.setStatus(SupplierStatus.PENDING);
				supplier = supplierService.saveSupplier(supplier, true);
				createDefaultSupplierSettings(supplier,createdBy);

			} catch (Exception e) {
				LOG.error("Error while storing Supplier : " + e.getMessage(), e);
				model.addAttribute("errors", messageSource.getMessage("supplier.error.detail", new Object[] { supplier.getCompanyName(), e.getMessage() }, Global.LOCALE));
				return "supplierSignup";
			}
			return "redirect:/confirmation";
		}

	}

	private void createDefaultSupplierSettings(Supplier supplier, User createdBy) {
		if (supplier != null) {
			List<com.privasia.procurehere.core.entity.TimeZone> timeZones = timeZoneService.findAllActiveTimeZone();
			SupplierSettings supplierSettings = new SupplierSettings();
			supplierSettings.setSupplier(supplier);
			supplierSettings.setPoShare(PoShare.NONE);
			for (com.privasia.procurehere.core.entity.TimeZone timeZone : timeZones) {
				if (timeZone.getTimeZone().equalsIgnoreCase("GMT+8:00")) {
					supplierSettings.setTimeZone(timeZone);
					break;
				}
			}
			supplierSettings.setModifiedDate(new Date());
			// supplierSettings.setModifiedBy(createdBy);
			supplierSettingsService.saveSettings(supplierSettings);
		}
	}

	private boolean validate(Supplier supplier, Model model) {
		boolean isError = false;
		PasswordSettings settings = passswordSettingService.getPasswordRegex(userService.getAdminUser().getTenantId());
		String regex = null;
		String msg = null;
		if (settings != null) {
			regex = settings.getRegx();
			msg = settings.getMessage();
		}
		if (StringUtils.checkString(supplier.getRecaptchaResponse()).length() == 0) {
			model.addAttribute("errors", messageSource.getMessage("supplier.recaptcha.empty", new Object[] {}, Global.LOCALE));
			isError = true;
		}

		if (userService.isExistsLoginEmailGlobal(supplier.getLoginEmail())) {
			model.addAttribute("errors", messageSource.getMessage("supplier.login.email.exisit", new Object[] { supplier.getLoginEmail() }, Global.LOCALE));
			isError = true;
		} else if (userService.getUserByLoginName(supplier.getLoginEmail()) != null) {
			model.addAttribute("errors", messageSource.getMessage("supplier.login.email.exisit", new Object[] { supplier.getLoginEmail() }, Global.LOCALE));
			isError = true;
		}

		if (!StringUtils.validatePasswordWithRegx(supplier.getPassword(), regex)) {
			model.addAttribute("errors", StringUtils.checkString(msg).length() > 0 ? msg : messageSource.getMessage("user.password.week", new Object[] { supplier.getLoginEmail() }, Global.LOCALE));
			isError = true;
		}

		if (supplierService.isExists(supplier)) {
			model.addAttribute("errors", messageSource.getMessage("supplier.error.duplicate", new Object[] { supplier.getCompanyName(), supplier.getCompanyRegistrationNumber(), supplier.getRegistrationOfCountry() }, Global.LOCALE));
			isError = true;
		}

		return isError;
	}

	@RequestMapping(value = "/confirmation", method = RequestMethod.GET)
	public String supplierConformationView() {
		return "confirmation";
	}

	@ModelAttribute("registeredCountry")
	public List<Country> populateCountries() {
		return countryService.findAllActiveCountries();
	}

	@RequestMapping(value = "/checkRegistrationNumber", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> checkRegistrationNumber(@RequestParam("countryID") String countryID, @RequestParam("crnNum") String crnNum) {
		Boolean isExist = Boolean.FALSE;
		try {
			Supplier supplier = new Supplier();
			supplier.setCompanyRegistrationNumber(crnNum);
			Country country = new Country();
			country.setId(countryID);
			supplier.setRegistrationOfCountry(country);
			isExist = supplierService.isExistsRegistrationNumber(supplier);

		} catch (Exception e) {
			LOG.error("Error while checking Company registration number  is exist " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.registration.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("Duplicate", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>((isExist ? "Duplicate" : "Success"), (isExist ? HttpStatus.CONFLICT : HttpStatus.OK));
	}

	@RequestMapping(value = "/checkCompanyNameExis", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> checkCompanyNameExis(@RequestParam("countryID") String countryID, @RequestParam("companyName") String companyName) {
		Boolean isExist = Boolean.FALSE;
		try {
			Supplier supplier = new Supplier();
			supplier.setCompanyName(companyName);
			Country country = new Country();
			country.setId(countryID);
			supplier.setRegistrationOfCountry(country);
			isExist = supplierService.isExistsCompanyName(supplier);

		} catch (Exception e) {
			LOG.error("Error while checking Company name  is exist " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while checking Company name is exist " + e.getMessage());
			headers.add("error", messageSource.getMessage("supplier.companyname.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("Duplicate", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>((isExist ? "Duplicate" : "Success"), (isExist ? HttpStatus.CONFLICT : HttpStatus.OK));
	}

	@RequestMapping(value = "/checkLoginEmail", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> checkLoginEmail(@RequestParam("loginEmailId") String loginEmailId) {
		Boolean isExist = Boolean.FALSE;
		LOG.info("LOGIN EMAIL ID : " + loginEmailId);
		try {
			isExist = userService.isExistsLoginEmailGlobal(loginEmailId);
		} catch (Exception e) {
			LOG.error("Error while checking Login Email is exist " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.loginemail.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("Duplicate", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOG.info("isExist : " + isExist);
		return new ResponseEntity<String>((isExist ? "Duplicate" : "Success"), (isExist ? HttpStatus.CONFLICT : HttpStatus.OK));
	}

	@RequestMapping(value = "/checkRegistrationNumberWithId", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> checkRegistrationNumberWithId(@RequestParam("countryID") String countryID, @RequestParam("crnNum") String crnNum, @RequestParam("id") String id) {
		Boolean isExist = Boolean.FALSE;
		try {
			Supplier supplier = new Supplier();
			supplier.setId(id);
			supplier.setCompanyRegistrationNumber(crnNum);
			Country country = new Country();
			country.setId(countryID);
			supplier.setRegistrationOfCountry(country);
			isExist = supplierService.isExistsRegistrationNumberWithId(supplier);

		} catch (Exception e) {
			LOG.error("Error while checking Company registration number  is exist " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.registration.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("Duplicate", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>((isExist ? "Duplicate" : "Success"), (isExist ? HttpStatus.CONFLICT : HttpStatus.OK));
	}

	@RequestMapping(value = "/checkCompanyNameExistWithId", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> checkCompanyNameExistWithId(@RequestParam("countryID") String countryID, @RequestParam("companyName") String companyName, @RequestParam("id") String id) {
		Boolean isExist = Boolean.FALSE;
		try {
			Supplier supplier = new Supplier();
			supplier.setId(id);
			supplier.setCompanyName(companyName);
			Country country = new Country();
			country.setId(countryID);
			supplier.setRegistrationOfCountry(country);
			isExist = supplierService.isExistsCompanyNameWithId(supplier);

		} catch (Exception e) {
			LOG.error("Error while checking Company name  is exist " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while checking Company name is exist " + e.getMessage());
			headers.add("error", messageSource.getMessage("supplier.companyname.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("Duplicate", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>((isExist ? "Duplicate" : "Success"), (isExist ? HttpStatus.CONFLICT : HttpStatus.OK));
	}
}
