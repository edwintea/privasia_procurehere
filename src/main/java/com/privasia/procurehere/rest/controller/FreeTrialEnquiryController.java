/**
 * 
 */
package com.privasia.procurehere.rest.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FreeTrialEnquiry;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSubscriptionService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.FreeTrialEnquiryService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;

/**
 * @author yogesh
 */
@CrossOrigin
@RequestMapping("/registration")
@RestController
public class FreeTrialEnquiryController {

	private static Logger LOG = LogManager.getLogger(FreeTrialEnquiryController.class);

	@Autowired
	FreeTrialEnquiryService freeTrialInquiryService;

	@Resource
	MessageSource messageSource;

	@Autowired
	NotificationService notificationService;

	@Autowired
	BuyerSubscriptionService buyerSubscriptionService;

	@Autowired
	CountryService countryService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	UomService uomService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	UserService userService;

	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@RequestMapping(value = "/saveEnquiryOld", method = RequestMethod.POST)
	public @ResponseBody String freeTrialInquiryOld(@RequestBody FreeTrialEnquiry freeTrialEnquiry) {
		LOG.info("...............Free Enquiry called................................................" + freeTrialEnquiry.getEmailId());
		try {
			String message = "";
			if (CollectionUtil.isNotEmpty(validateFreeTrialEnquiry(freeTrialEnquiry, FreeTrialEnquiry.FreeTrialEnquiryInfo.class))) {
				for (String errMessage : validateFreeTrialEnquiry(freeTrialEnquiry, FreeTrialEnquiry.FreeTrialEnquiryInfo.class)) {
					message += errMessage + ", ";
				}
				return message;
			}

			freeTrialEnquiry.setCreatedDate(new Date());
			freeTrialEnquiry = freeTrialInquiryService.savefreeTrialEnquiry(freeTrialEnquiry);

			/*
			 * Send Email
			 */
			HashMap<String, Object> map = new HashMap<String, Object>();
			// String mailTo = freeTrialEnquiry.getEmailId();
			// String subject = "Signup Email";
			String appUrl = APP_URL + "/buyerSubscription/freeTrialSignup?u=" + freeTrialEnquiry.getUserName() + "&e=" + freeTrialEnquiry.getEmailId() + "&id=" + freeTrialEnquiry.getId();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			map.put("date", df.format(new Date()));
			map.put("userName", freeTrialEnquiry.getUserName());
			map.put("appUrl", appUrl);
			map.put("logoUrl", APP_URL + "/resources/images/public/procuhereLogo.png");
			// notificationService.sendEmailWithBcc(mailTo, messageSource.getMessage("trial.bcc.mail", new Object[] {},
			// Global.LOCALE) ,subject, map, Global.TRIAL_SUBSCRIPTION_SIGNUP_TEMPLATE);

			return "{\"status\" : \"Success\", \"id\" : \"" + freeTrialEnquiry.getId() + "\"}";

		} catch (Exception e) {
			LOG.error("error........" + e.getMessage(), e);
			return "{\"status\" : \"Failure\"}";
		}

	}

	@RequestMapping(value = "/saveEnquiry", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> freeTrialInquiry(@RequestBody FreeTrialEnquiry freeTrialEnquiry) {
		LOG.info("...............Free Enquiry called................................................" + freeTrialEnquiry.getEmailId());
		try {

			/*
			 * Send Email
			 */

			if (userService.isExistsLoginEmailGlobal(StringUtils.checkString(freeTrialEnquiry.getEmailId()))) {
				LOG.error("email has already been registered:" + freeTrialEnquiry.getEmailId());
				return new ResponseEntity<String>("{\"status\" : \"This email has already been registered. Kindly login to proceed or Chat with us for further assistance.\"}", HttpStatus.CONFLICT);
			}

			String message = "";
			if (CollectionUtil.isNotEmpty(validateFreeTrialEnquiry(freeTrialEnquiry, FreeTrialEnquiry.FreeTrialEnquiryInfo.class))) {
				for (String errMessage : validateFreeTrialEnquiry(freeTrialEnquiry, FreeTrialEnquiry.FreeTrialEnquiryInfo.class)) {
					message += errMessage + ", ";
				}
				return new ResponseEntity<String>("{\"status\" : \"Failure. " + message + "\"}", HttpStatus.CONFLICT);

			}

			freeTrialEnquiry.setCreatedDate(new Date());
			freeTrialEnquiry = freeTrialInquiryService.savefreeTrialEnquiry(freeTrialEnquiry);
			try {
				Buyer buyer = new Buyer();
				buyer.setSuspendedRemarks(freeTrialEnquiry.getId());
				buyer.setLoginEmail(StringUtils.checkString(freeTrialEnquiry.getEmailId()));
				buyer.setCommunicationEmail(StringUtils.checkString(freeTrialEnquiry.getEmailId()));
				LOG.info("Email---------------" + buyer.getCommunicationEmail());
				buyer.setFullName(freeTrialEnquiry.getUserName());
				LOG.info("Name-------------------" + buyer.getFullName());
				UUID uuid = UUID.randomUUID();
				buyer.setCompanyName("DUMMY-" + uuid.toString().replace("-", ""));
				buyer.setCompanyRegistrationNumber("DUMMY-" + uuid.toString().replace("-", ""));

				Country country = countryService.getCountryByCode("MY");
				buyer.setRegistrationOfCountry(country);

				User user = buyerSubscriptionService.saveTrialBuyer(buyer);
				if (user != null) {
					buyerService.sendTrialBuyerCreationMail(user.getBuyer(), user);
					Owner owner = buyerService.getDefaultOwner();
					try {
						uomService.loadDefaultUomIntoBuyerAccount(user.getBuyer(), owner);
					} catch (Exception e) {
						LOG.error("Error loading default UOM for buyer : " + e.getMessage(), e);
					}

				} else {
					LOG.error("User is null. Buyer creation failed due to some reason.");
				}
			} catch (Exception e) {
				LOG.error("Error while free trial sign up: " + e.getMessage(), e);
			}
			return new ResponseEntity<String>("{\"status\" : \"Success\", \"id\" : \"" + freeTrialEnquiry.getId() + "\"}", HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("error........" + e.getMessage(), e);
			return new ResponseEntity<String>("{\"status\" : \"Failure: " + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public List<String> validateFreeTrialEnquiry(FreeTrialEnquiry freeTrialEnquiry, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<FreeTrialEnquiry>> constraintViolations = validator.validate(freeTrialEnquiry, validations);
		for (ConstraintViolation<FreeTrialEnquiry> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}

}
