/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Buyer.BuyerBasicProfile;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.Subscription;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.BuyerPlanService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.SubscriptionService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.BuyerPlanEditor;
import com.privasia.procurehere.web.editors.CountryEditor;

/**
 * @author Arc
 */
@Controller
@RequestMapping(value = "/owner")
public class BuyerCreationController implements Serializable {

	private static final long serialVersionUID = -6367432581209698965L;

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	BuyerService buyerService;

	@Autowired
	CountryService countryService;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	BuyerPlanEditor planEditor;

	@Autowired
	BuyerPlanService buyerPlanService;

	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	UserService userService;

	@Autowired
	UomService uomService;

	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(Country.class, countryEditor);
		binder.registerCustomEditor(BuyerPlan.class, planEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@RequestMapping(value = "/buyerCreation", method = RequestMethod.GET)
	public String createBuyer(Model model) {
		LOG.info("Create Buyer.......");
		Buyer buyer = new Buyer();
		buyer.setCurrentSubscription(new BuyerSubscription());
		buyer.setRegistrationOfCountry(countryService.getCountryByCode("MY"));
		model.addAttribute("buyerObj", buyer);
		modelAttributesForBuyerPlanList(model);
		return "buyerCreation";
	}

	private void modelAttributesForBuyerPlanList(Model model) {
		List<BuyerPlan> planList = buyerPlanService.findAllBuyerPlansByStatuses(new PlanStatus[] { PlanStatus.ACTIVE, PlanStatus.HIDDEN });
		model.addAttribute("planList", planList);
		model.addAttribute("countryList", countryService.getAllCountries());
	}

	@RequestMapping(value = "/checkBuyerRegistrationNumber", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> checkRegistrationNumber(@RequestParam("countryID") String countryID, @RequestParam("crnNum") String crnNum) {
		Boolean isExist = Boolean.FALSE;
		try {
			Buyer buyer = new Buyer();
			buyer.setCompanyRegistrationNumber(crnNum);
			Country country = new Country();
			country.setId(countryID);
			buyer.setRegistrationOfCountry(country);
			isExist = buyerService.isExistsRegistrationNumber(buyer);

		} catch (Exception e) {
			LOG.error("Error while checking Company registration number  is exist " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.registration.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("Duplicate", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>((isExist ? "Duplicate" : "Success"), (isExist ? HttpStatus.CONFLICT : HttpStatus.OK));
	}

	@RequestMapping(value = "/checkLoginEmail", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> checkEmail(@RequestParam("loginEmail") String loginEmail) {
		Boolean isExist = Boolean.FALSE;
		try {

			if (userService.isExistsLoginEmailGlobal(loginEmail)) {
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

	@RequestMapping(value = "/checkBuyerCompanyName", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> checkBuyerCompanyName(@RequestParam("countryID") String countryID, @RequestParam("companyName") String companyName,@RequestParam(name="buyerID",required=false) String buyerID) {
		Boolean isExist = Boolean.FALSE;
		try {
			Buyer buyer = new Buyer();
			buyer.setCompanyName(companyName);
			Country country = new Country();
			country.setId(countryID);
			buyer.setRegistrationOfCountry(country);
			//this is optional in case of free trail user .
			buyer.setId(buyerID);
			isExist = buyerService.isExistsCompanyName(buyer);

		} catch (Exception e) {
			LOG.error("Error while checking Company name  is exist " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while checking Company name is exist " + e.getMessage());
			headers.add("error", messageSource.getMessage("supplier.companyname.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<String>("Duplicate", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>((isExist ? "Duplicate" : "Success"), (isExist ? HttpStatus.CONFLICT : HttpStatus.OK));
	}

	@RequestMapping(value = "/buyerCreation", method = RequestMethod.POST)
	public String saveBuyer(@Validated(value = { BuyerBasicProfile.class }) @ModelAttribute("buyerObj") Buyer buyerObj, BindingResult result, Model model, RedirectAttributes redir) throws ParseException {
		List<String> errMessages = new ArrayList<String>();
		HttpHeaders headers = new HttpHeaders();

		modelAttributesForBuyerPlanList(model);
		try {
			Set<ConstraintViolation<Buyer>> constraintViolations = validator.validate(buyerObj, Buyer.BuyerBasicProfile.class);
			for (ConstraintViolation<Buyer> cv : constraintViolations) {
				LOG.info("Message : " + cv.getMessage());
				headers.add("error", cv.getMessage());
			}

			if (result.hasErrors()) {
				for (ObjectError field : result.getAllErrors()) {
					LOG.info("ERROR : " + field.getObjectName() + " : " + field.getDefaultMessage());
					errMessages.add(field.getDefaultMessage());
				}
				model.addAttribute("buyerObj", buyerObj);
				model.addAttribute("errors", errMessages);
				return "buyerCreation";
			} else {
				if (validate(buyerObj, model)) {
					model.addAttribute("buyerObj", buyerObj);
					return "buyerCreation";
				}

				User user = buyerService.saveManualBuyer(buyerObj, SecurityLibrary.getLoggedInUserLoginId());
				buyerService.sentBuyerCreationMail(user.getBuyer(), user);

				try {
					Owner owner = buyerService.getDefaultOwner();
					uomService.loadDefaultUomIntoBuyerAccount(buyerObj, owner);
				} catch (Exception e) {
					LOG.error("Error loading default UOM for buyer : " + e.getMessage(), e);
				}

				LOG.info("success.....");
				redir.addFlashAttribute("success", messageSource.getMessage("buyer.create.success", new Object[] { buyerObj.getCompanyName() }, Global.LOCALE));

				return "redirect:buyerList";
			}
		} catch (Exception e) {
			LOG.error("Error while saving buyer details : " + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("buyerCreation.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("buyerObj", buyerObj);
			return "buyerCreation";
		}
	}

	@RequestMapping(value = "/calculateSubscription", method = RequestMethod.POST)
	public ResponseEntity<Subscription> calculateSubscription(@RequestBody Subscription subscription) {
		try {
			// TODO removed
			// subscriptionService.doComputeSubscription(subscription, null);
		} catch (Exception e) {
			LOG.error("Error during subscription computing : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error during subscription computing : " + e.getMessage());
			return new ResponseEntity<Subscription>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Subscription>(subscription, HttpStatus.OK);
	}

	private boolean validate(Buyer buyerObj, Model model) {
		boolean isError = false;

		if (userService.isExistsLoginEmailGlobal(buyerObj.getLoginEmail())) {
			LOG.info("validation getLoginEmail....." + buyerObj.getLoginEmail());
			model.addAttribute("errors", messageSource.getMessage("buyer.login.email.exisit", new Object[] { buyerObj.getLoginEmail() }, Global.LOCALE));
			isError = true;
		}
		if (buyerService.isExists(buyerObj)) {
			LOG.info("validation inside isExits()....." + buyerObj.getCompanyName());
			LOG.info("validation inside isExits()....---------------------." + buyerObj.getRegistrationOfCountry().getCountryName());
			model.addAttribute("errors", messageSource.getMessage("buyer.error.duplicate", new Object[] { buyerObj.getCompanyName(), buyerObj.getRegistrationOfCountry().getCountryName() }, Global.LOCALE));
			isError = true;
		}

		return isError;
	}

}
