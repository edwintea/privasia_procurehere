package com.privasia.procurehere.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.FinanceCompany.FinanceCompanyBasicProfile;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.FinanceCompanyService;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.StateEditor;

@Controller
@RequestMapping(value = "/owner")
public class FinanceComapanyCreationcontroller {

	private static final Logger LOG = LogManager.getLogger(Global.FINANCE_COMPANY_LOG);

	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Resource
	MessageSource messageSource;

	@Autowired
	FinanceCompanyService financeCompanyService;
	@Autowired
	CountryService countryService;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	StateEditor stateEditor;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Country.class, countryEditor);
		binder.registerCustomEditor(State.class, stateEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@RequestMapping(value = "/financeCompanyCreation", method = RequestMethod.GET)
	public String createFinanceCompany(Model model) {
		LOG.info("financeCompanyCreation call.............");

		FinanceCompany financeCompany = new FinanceCompany();
		Country country = countryService.getCountryWithStatesByCode("MY");
		financeCompany.setRegistrationOfCountry(country);
		model.addAttribute("countryStates", country != null ? country.getStates() : null);

		model.addAttribute("countryList", countryService.getAllCountries());
		model.addAttribute("financeCompanyObj", financeCompany);

		return "financeCompanyCreation";
	}

	@RequestMapping(value = "/checkFinanceCompanyRegistrationNumber", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> checkFinanceCompanyRegistrationNumber(@RequestParam("countryID") String countryID, @RequestParam("crnNum") String crnNum) {
		Boolean isExist = Boolean.FALSE;
		LOG.info("checkFinanceCompanyRegistrationNumber call.............");
		LOG.info("countryID call............." + countryID);
		LOG.info("crnNum call............." + crnNum);

		try {
			FinanceCompany financeCompany = new FinanceCompany();
			financeCompany.setCompanyRegistrationNumber(crnNum);
			Country country = new Country();
			country.setId(countryID);
			financeCompany.setRegistrationOfCountry(country);
			isExist = financeCompanyService.isExistsRegistrationNumber(financeCompany);

		} catch (Exception e) {
			LOG.error("Error while checking Company registration number  is exist " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("finance.registration.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("Duplicate", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>((isExist ? "Duplicate" : "Success"), (isExist ? HttpStatus.CONFLICT : HttpStatus.OK));
	}

	@RequestMapping(value = "/checkFinanceCompanyLoginEmail", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> checkFinanceCompanyEmail(@RequestParam("loginEmail") String loginEmail) {
		LOG.info("checkFinanceCompanyLoginEmail call.............");

		Boolean isExist = Boolean.FALSE;
		try {

			if (financeCompanyService.isExistsLoginEmail(loginEmail)) {
				LOG.info("Duplicate login EMail ....." + loginEmail);
				isExist = Boolean.TRUE;
			}
		} catch (Exception e) {
			LOG.error("Error while checking Company name  is exist " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while checking Company name is exist " + e.getMessage());
			headers.add("error", "error checking login email duplicate : " + e.getMessage());
			return new ResponseEntity<String>("Duplicate", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>((isExist ? "Duplicate" : "Success"), (isExist ? HttpStatus.CONFLICT : HttpStatus.OK));
	}

	@RequestMapping(value = "/checkFinanceCompanyName", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> checkFinanceCompanyName(@RequestParam("countryID") String countryID, @RequestParam("companyName") String companyName) {
		Boolean isExist = Boolean.FALSE;
		LOG.info("checkFinanceCompanyName call.............");
		try {
			FinanceCompany financeCompany = new FinanceCompany();
			financeCompany.setCompanyName(companyName);
			Country country = new Country();
			country.setId(countryID);
			financeCompany.setRegistrationOfCountry(country);
			isExist = financeCompanyService.isExistsCompanyName(financeCompany);

		} catch (Exception e) {
			LOG.error("Error while checking Company name  is exist " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while checking Company name is exist " + e.getMessage());
			headers.add("error", messageSource.getMessage("finance.companyname.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("Duplicate", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>((isExist ? "Duplicate" : "Success"), (isExist ? HttpStatus.CONFLICT : HttpStatus.OK));
	}

	@RequestMapping(value = "/financeCompanyCreation", method = RequestMethod.POST)
	public String saveFinanceCompany(@Validated(value = { FinanceCompanyBasicProfile.class }) @ModelAttribute("financeCompanyObj") FinanceCompany financeCompanyObj, BindingResult result, Model model, RedirectAttributes redir) throws ParseException {

		List<String> errMessages = new ArrayList<String>();
		HttpHeaders headers = new HttpHeaders();

		model.addAttribute("countryList", countryService.getAllCountries());
		try {
			Set<ConstraintViolation<FinanceCompany>> constraintViolations = validator.validate(financeCompanyObj, FinanceCompany.FinanceCompanyBasicProfile.class);
			for (ConstraintViolation<FinanceCompany> cv : constraintViolations) {
				LOG.info("Message : " + cv.getMessage());
				headers.add("error", cv.getMessage());
			}

			if (result.hasErrors()) {
				for (ObjectError field : result.getAllErrors()) {
					LOG.info("ERROR : " + field.getObjectName() + " : " + field.getDefaultMessage());
					errMessages.add(field.getDefaultMessage());
				}
				model.addAttribute("financeCompanyObj", financeCompanyObj);
				model.addAttribute("errors", errMessages);
				return "financeCompanyCreation";
			} else {
				if (validate(financeCompanyObj, model)) {
					model.addAttribute("financeObj", financeCompanyObj);
					return "financeCompanyCreation";
				}

				User user = financeCompanyService.saveFinanceCompany(financeCompanyObj, SecurityLibrary.getLoggedInUserLoginId());
				financeCompanyService.sentFinanaceCompanyCreationMail(user.getFinanceCompany(), user);

				LOG.info("success.....");
				redir.addFlashAttribute("success", messageSource.getMessage("finance.create.success", new Object[] { financeCompanyObj.getCompanyName() }, Global.LOCALE));

				return "redirect:financeCompanyList";
			}
		} catch (Exception e) {
			LOG.error("Error while saving Finance Comapany details : " + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("finance.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("financeCompanyObj", financeCompanyObj);
			return "financeCompanyCreation";
		}
	}

	private boolean validate(FinanceCompany financeCompanyObj, Model model) {
		boolean isError = false;

		if (financeCompanyService.isExistsLoginEmail(financeCompanyObj.getLoginEmail())) {
			LOG.info("validation getLoginEmail....." + financeCompanyObj.getLoginEmail());
			model.addAttribute("errors", messageSource.getMessage("finance.login.email.exisit", new Object[] { financeCompanyObj.getLoginEmail() }, Global.LOCALE));
			isError = true;
		}
		if (financeCompanyService.isExists(financeCompanyObj)) {
			LOG.info("validation inside isExits()....." + financeCompanyObj.getCompanyName());
			LOG.info("validation inside isExits()....---------------------." + financeCompanyObj.getRegistrationOfCountry().getCountryName());
			model.addAttribute("errors", messageSource.getMessage("finance.error.duplicate", new Object[] { financeCompanyObj.getCompanyName(), financeCompanyObj.getRegistrationOfCountry().getCountryName() }, Global.LOCALE));
			isError = true;
		}

		return isError;
	}

}
