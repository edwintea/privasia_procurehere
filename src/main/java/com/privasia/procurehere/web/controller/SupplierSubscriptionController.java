/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.PaymentTransactionDao;
import com.privasia.procurehere.core.dao.SupplierSubscriptionDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.PasswordSettings;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.Supplier.SupplierSignup;
import com.privasia.procurehere.core.entity.SupplierPackage;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PeriodUnitType;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.PasswordSettingService;
import com.privasia.procurehere.service.PromotionalCodeService;
import com.privasia.procurehere.service.StripeSubscriptionService;
import com.privasia.procurehere.service.SubscriptionService;
import com.privasia.procurehere.service.SupplierPlanService;
import com.privasia.procurehere.service.SupplierSubscriptionStripeService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.impl.SubscriptionServiceImpl;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.stripe.exception.AuthenticationException;
import com.stripe.model.PaymentIntent;

/**
 * @author Nitin Otageri
 */
@Controller
@RequestMapping(value = "/suppliersubscription")
public class SupplierSubscriptionController {

	private static final Logger LOG = LogManager.getLogger(Global.SUBSCRIPTION_LOG);

	// private static final String CLIENT_ID =
	// "ATUbW9GJ8YbMnMrPhw35fsyhO5TQIyH9Gv-PHDe6ZrwkJNv-jvhJPHdFhQNy_4GcviqkEU6jYq5EEyqU";
	// private static final String SECRET =
	// "EL9E3nXvbMJL8QCmMoZWKd4h7X3aq4Z84p4TpVGbqudxRBocsiHxZ2l1--iiljPxoosk_WK3BK4LnG2q";
	// private static final String MODE = "sandbox";

	@Autowired
	SupplierPlanService supplierPlanService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	CountryService countryService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	UserService userService;

	@Resource
	MessageSource messageSource;

	@Value("${paypal.url}")
	String PAYPAL_URL;

	@Value("${paypal.merchant.id}")
	String MERCHANT_ID;

	@Value("${paypal.api.username}")
	String PAYPAL_API_USERNAME;

	@Value("${paypal.api.password}")
	String PAYPAL_API_PASSWORD;

	@Value("${paypal.api.signature}")
	String PAYPAL_API_SIGNATURE;

	@Value("${app.url}")
	String appUrl;

	@Value("${paypal.environment}")
	String PAYPAL_ENVIRONMENT;

	@Autowired
	PromotionalCodeService promotionalCodeService;

	@Autowired
	SupplierSubscriptionDao supplierSubscriptionDao;

	@Autowired
	PasswordSettingService passwordSettingService;

	@Autowired
	SupplierSubscriptionStripeService stripeSupplierSubscriptionService;

	@Value("${stripe.publish}")
	String stripePublicKey;

	@Autowired
	PaymentTransactionDao paymentTransactionDao;

	@Autowired
	StripeSubscriptionService stripeService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Currency.class, currencyEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(Country.class, countryEditor);
	}

	@RequestMapping(path = "/selectPlan", method = RequestMethod.GET)
	public String planList(Model model) {
		List<SupplierPlan> planList = supplierPlanService.findAllPlansByStatus(PlanStatus.ACTIVE);
		model.addAttribute("planList", planList);
		return "selectSupplierPlan";
	}

	@RequestMapping(path = "/payment/{planId}", method = RequestMethod.POST)
	public String doPayment(@Validated(value = { SupplierSignup.class }) @ModelAttribute("supplier") Supplier supplier, @PathVariable(name = "planId") String planId, BindingResult result, Model model, HttpSession session, HttpServletRequest request) {
		LOG.info("User requested to purchase Plan : " + planId);

		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			List<String> errMessages = new ArrayList<String>();
			for (ObjectError field : result.getAllErrors()) {
				LOG.info("ERROR : " + field.getObjectName() + " : " + field.getDefaultMessage());
				errMessages.add(field.getDefaultMessage());
			}
			model.addAttribute("errors", errMessages);
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
			model.addAttribute("plan", plan);
			model.addAttribute("countryList", countryService.getAllCountries());
			model.addAttribute("supplier", supplier);
			List<SupplierPlan> planList = supplierPlanService.findAllPlansByStatus(PlanStatus.ACTIVE);
			model.addAttribute("planList", planList);
			return "selectSupplierPlan";
		} else {
			LOG.info("Page submitted....................................... " + supplier.toString());
			try {
				if (validate(supplier, model)) {
					model.addAttribute("merchantId", this.MERCHANT_ID);
					model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
					model.addAttribute("plan", plan);
					model.addAttribute("countryList", countryService.getAllCountries());
					model.addAttribute("supplier", supplier);
					List<SupplierPlan> planList = supplierPlanService.findAllPlansByStatus(PlanStatus.ACTIVE);
					model.addAttribute("planList", planList);
					return "selectSupplierPlan";
				}

				if (plan.getPrice() > 0) {

					// supplier.setStatus(SupplierStatus.PENDING);
					// Supplier dbSupplier = supplierService.saveSupplier(supplier, false);

					SupplierSubscription subscription = new SupplierSubscription();
					subscription.setSupplierPlan(plan);
					subscription.setPaymentTransaction(new PaymentTransaction());
					subscription.getPaymentTransaction().setCountry(supplier.getRegistrationOfCountry());
					subscription.setSupplier(supplier);

					String returnURL = appUrl + "/suppliersubscription/confirm?planId=" + planId + "&txId=";
					String cancelURL = appUrl + "/suppliersubscription/cancel?planId=" + planId + "&txId=";
					// String returnURL = request.getScheme() + "://" + request.getServerName() + ":" +
					// request.getServerPort()
					// + request.getContextPath() + "/subscription/confirm?planId=" + planId + "&txId=";
					// String cancelURL = request.getScheme() + "://" + request.getServerName() + ":" +
					// request.getServerPort()
					// + request.getContextPath() + "/subscription/cancel?planId=" + planId + "&txId=";
					return subscriptionService.initiateSupplierPaypalPayment(subscription, planId, session, returnURL, cancelURL);
				} else {
					// Trial Account
					Map<String, String> map = new HashMap<String, String>();
					try {
						// User user = subscriptionService.doTrialSubscription(subscription, planId, model);
						// buyerService.sentBuyerCreationMail(user.getBuyer(), user);
						// Owner owner = buyerService.getDefaultOwner();
						// try {
						// uomService.loadDefaultUomIntoBuyerAccount(user.getBuyer(), owner);
						// } catch (Exception e) {
						// LOG.error("Error loading default UOM for buyer : " + e.getMessage(), e);
						// }
						// Free trial
						map.put("ACK", "Success");
						map.put("PAYMENTINFO_0_TRANSACTIONID", "Success");
					} catch (Exception e) {
						LOG.error("Error during trial subscription : " + e.getMessage(), e);
						PaymentTransaction paymentTransaction = new PaymentTransaction();
						paymentTransaction.setErrorCode("-1009");
						paymentTransaction.setErrorMessage(e.getMessage());
						model.addAttribute("paymentTransaction", paymentTransaction);
						return "redirect:../supplierSubscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
					}
					model.addAttribute("paypalResponse", map);
					return "supplierSubscriptionSuccess";
				}

			} catch (Exception e) {
				LOG.error("Error while storing Supplier : " + e.getMessage(), e);
				model.addAttribute("errors", messageSource.getMessage("supplier.error.detail", new Object[] { supplier.getCompanyName(), e.getMessage() }, Global.LOCALE));
				return "supplierSignup";
			}
			// return "redirect:/confirmation";
		}
	}

	private boolean validate(Supplier supplier, Model model) {
		boolean isError = false;

		// if (StringUtils.checkString(supplier.getRecaptchaResponse()).length() == 0) {
		// model.addAttribute("errors", messageSource.getMessage("supplier.recaptcha.empty", new Object[] {},
		// Global.LOCALE));
		// isError = true;
		// }

		User user = userService.getPlainUserByLoginId(supplier.getLoginEmail());
		String regex = null;
		String msg = null;
		if (user != null) {
			PasswordSettings settings = passwordSettingService.getPasswordRegex(user.getTenantId());
			if (settings != null) {
				regex = settings.getRegx();
				msg = null;
			}
		}
		if (supplierService.isExistsLoginEmail(supplier.getLoginEmail())) {
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

	@RequestMapping(path = "/cancel", method = RequestMethod.GET)
	public String doCancel(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, Model model) {
		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);
		model.addAttribute("plan", plan);
		PaymentTransaction paymentTransaction = subscriptionService.cancelPaymentTransaction(txId);
		model.addAttribute("paymentTransaction", paymentTransaction);
		return "subscriptionError";
	}

	@RequestMapping(path = "/supplierSubscriptionError/{planId}/{paymentTransactionId}", method = RequestMethod.GET)
	public String showPaymentError(@PathVariable(name = "planId") String planId, @PathVariable(name = "paymentTransactionId") String paymentTransactionId, Model model) {
		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);
		PaymentTransaction paymentTransaction = subscriptionService.getPaymentTransactionById(paymentTransactionId);
		Supplier supplier = subscriptionService.getSupplierWithSupplierPackagePlanByTenantIdforBilling(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("supplier", supplier);
		if (paymentTransaction == null) {
			try {
				paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentTransactionId);
				SupplierSubscription subscription = subscriptionService.getSupplierSubscriptionById(paymentTransaction.getSupplierSubscription().getId());
				model.addAttribute("subscription", subscription);
				PaymentIntent status = stripeService.getPaymentStatusData(paymentTransactionId);
				LOG.info("Got payment status as " + status.getStatus());
				if (Global.STRIPE_STATUS_REQUIRES_ATTENTION.equals(status.getStatus())) {
					if (status.getLastPaymentError() != null) {
						paymentTransaction.setStatus(TransactionStatus.FAILURE);
						paymentTransaction.setErrorCode(status.getLastPaymentError().getCode());
						paymentTransaction.setErrorMessage(status.getLastPaymentError().getMessage());
					}
				}
			} catch (Exception e) {
				LOG.error("Error in getting payment object " + e.getMessage());
			}
		}
		model.addAttribute("paymentTransaction", paymentTransaction);
		model.addAttribute("plan", plan);
		return "supplierSubscriptionError";
	}

	/*********************************************************************************
	 * GetShippingDetails: Function to perform the GetExpressCheckoutDetails API call Inputs: None Output: Returns a
	 * HashMap object containing the response from the server.
	 *********************************************************************************/
	@RequestMapping(path = "/confirm", method = RequestMethod.GET)
	public String showConfirmPayment(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, @RequestParam(name = "token") String token, @RequestParam(name = "PayerID") String payerID, Model model, HttpServletRequest request, HttpSession session) {

		// Not required in model as the Subscription object already has the Plan details
		// String planId = (String) session.getAttribute("planId");
		// Plan plan = planService.getPlanForEditById(planId);
		// model.addAttribute("plan", plan);

		String subscriptionId = (String) session.getAttribute("subscriptionId");
		LOG.info("Subscription Id : " + subscriptionId);
		SupplierSubscription subscription = subscriptionService.getSupplierSubscriptionById(subscriptionId);
		if (subscription != null) {
			Supplier supplier = (Supplier) session.getAttribute("supplier");
			subscription.setSupplier(supplier);
		}
		model.addAttribute("subscription", subscription);

		PaymentTransaction paymentTransaction = subscriptionService.getPaymentTransactionById(txId);
		model.addAttribute("paymentTransaction", paymentTransaction);

		/*
		 * Build a second API request to PayPal, using the token as the ID to get the details on the payment
		 * authorization
		 */
		String nvpstr = "&TOKEN=" + token;

		LOG.info("Invoking PayPal Checkout Details ===================> ");
		/*
		 * Make the API call and store the results in an array. If the call was a success, show the authorization
		 * details, and provide an action to complete the payment. If failed, show the error
		 */
		HashMap<String, String> nvp = subscriptionService.invokePaypalService("GetExpressCheckoutDetails", nvpstr);
		String strAck = nvp.get("ACK").toString();
		if (strAck != null && (strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS) || strAck.equalsIgnoreCase("SuccessWithWarning"))) {
			session.setAttribute("PAYERID", nvp.get("PAYERID").toString());
		}

		// The map will contain values as documented at
		// https://developer.paypal.com/docs/classic/api/merchant/GetExpressCheckoutDetails_API_Operation_NVP/
		model.addAttribute("paypalResponse", nvp);
		return "supplierConfirmSubscription";
	}

	@RequestMapping(path = "/confirm", method = RequestMethod.POST)
	public String doConfirmPayment(@RequestParam(name = "paymentTransactionId") String paymentTransactionId, @RequestParam(name = "token") String token, @RequestParam(name = "payerId") String payerId, Model model, HttpServletRequest request, HttpSession session) {
		try {
			String serverName = request.getServerName();
			subscriptionService.confirmSupplierSubscription(token, payerId, model, serverName, paymentTransactionId, false);
			// buyerService.sentBuyerCreationMail(user.getBuyer(), user);

		} catch (Exception e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);
		}
		return "supplierSubscriptionSuccess";
	}

	@RequestMapping(path = "/get/{planId}", method = RequestMethod.GET)
	public String buyPlan(@PathVariable(name = "planId") String planId, Model model, HttpSession session) {
		try {
			LOG.info("User requested to purchase Plan : " + planId);
			SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);

			SupplierSubscription subscription = new SupplierSubscription();
			subscription.setSupplierPlan(plan);
			subscription.setSupplier(new Supplier());
			subscription.setPaymentTransaction(new PaymentTransaction());
			subscription.getPaymentTransaction().setCountry(countryService.getCountryByCode("MY"));

			model.addAttribute("subscription", subscription);
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
			model.addAttribute("plan", plan);
			model.addAttribute("countryList", countryService.getAllCountries());
			return "buyPlan";
		} catch (Exception e) {
			LOG.error("Error fetching Buy Screen : " + e.getMessage(), e);
			List<SupplierPlan> planList = supplierPlanService.findAllPlansByStatus(PlanStatus.ACTIVE);
			model.addAttribute("planList", planList);
			return "selectPlan";
		}
	}

	@RequestMapping(path = "/supplierCheckout", method = RequestMethod.GET)
	public String supplierCheckout(Model model, HttpSession session, @RequestParam(name = "payment_intent", required = false) String paymentStatus) {
		LOG.info("supplier Checkout GET called");
		try {

			if (StringUtils.checkString(paymentStatus).length() > 0) {
				try {
					String msg = stripeSupplierSubscriptionService.getPaymentStatus(paymentStatus);
					LOG.info(msg);
					if (msg.indexOf("Processing") != -1) {
						model.addAttribute("info", msg);
					} else {
						return "supplierCheckoutThankyou";
					}
				} catch (Exception e) {
					LOG.info("Error in checking payment status " + e.getMessage());
				}
			}

			Supplier supplier = new Supplier();
			Country my = countryService.getCountryByCode("MY");
			supplier.setRegistrationOfCountry(my);
			model.addAttribute("countryList", countryService.getAllCountries());
			model.addAttribute("supplier", supplier);
			model.addAttribute("supplier", supplier);
			model.addAttribute("publicKey", stripePublicKey);

			PlanStatus[] status = { PlanStatus.ACTIVE };
			List<SupplierPlan> dbplanList = supplierPlanService.findAllPlansByStatuses(status);
			for (SupplierPlan supplierPlan : dbplanList) {
				model.addAttribute("plan", supplierPlan);
				break;
			}

			model.addAttribute("merchantId", this.MERCHANT_ID);
			String tenantId = userService.getAdminUser().getTenantId();
			PasswordSettings passwordSetting = passwordSettingService.getPasswordRegex(tenantId);
			model.addAttribute("regex", passwordSetting);

			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
		} catch (Exception e) {
			LOG.error("Error: " + e.getMessage(), e);
		}
		return "supplierCheckout";
	}

	@RequestMapping(value = "/doInitiateTrialPayment", method = RequestMethod.POST)
	public String saveRegistration(@Validated(value = { SupplierSignup.class }) @ModelAttribute("supplier") Supplier supplier, @RequestParam(required = false, name = "promoCodeId") String promoCodeId, @RequestParam(name = "supplerPlan") String supplerPlan, @RequestParam(name = "supplierPrice") BigDecimal supplierPrice, @RequestParam(required = false, name = "promoDiscountPrice") BigDecimal promoDiscountPrice, @RequestParam(name = "totalPrice") BigDecimal totalPrice, HttpSession session, BindingResult result, Model model) {
		LOG.info("promoCodeId: " + promoCodeId);
		LOG.info("supplerPlan: " + supplerPlan);
		LOG.info("supplierPrice: " + supplierPrice);
		LOG.info("promoDiscountPrice: " + promoDiscountPrice);
		LOG.info("totalPrice: " + totalPrice);

		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			List<String> errMessages = new ArrayList<String>();
			for (ObjectError field : result.getAllErrors()) {
				LOG.info("ERROR : " + field.getObjectName() + " : " + field.getDefaultMessage());
				errMessages.add(field.getDefaultMessage());
			}
			model.addAttribute("countryList", countryService.getAllCountries());
			model.addAttribute("supplier", supplier);
			model.addAttribute("errors", errMessages);

			return "supplierCheckout";
		} else {
			LOG.info("Page submitted....................................... " + supplier.toString());
			try {
				if (validate(supplier, model)) {
					return "supplierCheckout";
				}

				SupplierSubscription subscription = new SupplierSubscription();
				// subscription.setSupplierPlan(plan);
				subscription.setPriceAmount(supplierPrice);
				subscription.setPromoCodeDiscount(promoDiscountPrice);
				subscription.setTotalPriceAmount(totalPrice);

				subscription.setPaymentTransaction(new PaymentTransaction());
				subscription.getPaymentTransaction().setCountry(supplier.getRegistrationOfCountry());
				subscription.setSupplier(supplier);

				String returnURL = appUrl + "/suppliersubscription/doPaymentConfirm?type=Order&&txId=";
				String cancelURL = appUrl + "/suppliersubscription/supplierCheckout?cancelled=true";

				return subscriptionService.initiateSupplierOrderPaypalPayment(subscription, null, session, returnURL, cancelURL);
				// supplier.setStatus(SupplierStatus.PENDING);
				// supplierService.saveSupplier(supplier, true);

			} catch (Exception e) {
				LOG.error("Error while storing Supplier : " + e.getMessage(), e);
				model.addAttribute("errors", messageSource.getMessage("supplier.error.detail", new Object[] { supplier.getCompanyName(), e.getMessage() }, Global.LOCALE));
				model.addAttribute("countryList", countryService.getAllCountries());
				model.addAttribute("supplier", supplier);
				return "supplierCheckout";
			}
			// return "redirect:supplierCheckoutThankyou";
		}

	}

	// @RequestMapping(value = "/doInitiateSupplierPayment", method = RequestMethod.POST)
	// public String saveSupplierRegistration(@Validated(value = { SupplierSignup.class }) @ModelAttribute("supplier")
	// Supplier supplier, @RequestParam(required = false, name = "promoCodeId") String promoCodeId, @RequestParam(name =
	// "supplerPlan") String supplerPlan, @RequestParam(name = "supplierPrice") BigDecimal supplierPrice,
	// @RequestParam(required = false, name = "promoDiscountPrice") BigDecimal promoDiscountPrice, @RequestParam(name =
	// "totalPrice") BigDecimal totalPrice, HttpSession session, BindingResult result, Model model) {
	// LOG.info("promoCodeId: " + promoCodeId);
	// LOG.info("supplerPlan: " + supplerPlan);
	// LOG.info("supplierPrice: " + supplierPrice);
	// LOG.info("promoDiscountPrice: " + promoDiscountPrice);
	// LOG.info("totalPrice: " + totalPrice);
	//
	// promoDiscountPrice = BigDecimal.ZERO;
	//
	// if (result.hasErrors()) {
	// LOG.error("Page submitted With Errors ............................. ");
	// List<String> errMessages = new ArrayList<String>();
	// for (ObjectError field : result.getAllErrors()) {
	// LOG.info("ERROR : " + field.getObjectName() + " : " + field.getDefaultMessage());
	// errMessages.add(field.getDefaultMessage());
	// }
	// model.addAttribute("countryList", countryService.getAllCountries());
	// model.addAttribute("supplier", supplier);
	// model.addAttribute("errors", errMessages);
	//
	// return "supplierCheckout";
	// } else {
	// LOG.info("Page submitted....................................... " + supplier.toString());
	// try {
	// if (validate(supplier, model)) {
	// return "supplierCheckout";
	// }
	//
	// SupplierSubscription subscription = new SupplierSubscription();
	// SupplierPlan plan = supplierPlanService.getAllBuyerSupplierPlan();
	// subscription.setSupplierPlan(plan);
	// subscription.setPriceAmount(new BigDecimal(plan.getPrice()));
	//
	// PaymentTransaction paymentTransaction = new PaymentTransaction();
	//
	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// PromotionalCode promoCode = promotionalCodeService.getPromotionalCodeById(promoCodeId);
	// if (promoCode != null) {
	// paymentTransaction.setPromoCode(promoCode);
	// subscription.setPromoCode(promoCode.getPromoCode());
	// promoDiscountPrice = getPromoDiscount(promoCode.getPromoCode(), supplierPrice, null);
	// subscription.setPromoCodeDiscount(promoDiscountPrice);
	// } else {
	// subscription.setPromoCodeDiscount(BigDecimal.ZERO);
	// }
	// }
	// subscription.setTotalPriceAmount(new BigDecimal(plan.getPrice()).subtract(promoDiscountPrice));
	//
	// if (totalPrice.compareTo(subscription.getTotalPriceAmount()) != 0) {
	// model.addAttribute("countryList", countryService.getAllCountries());
	// model.addAttribute("supplier", supplier);
	// model.addAttribute("errors", "Price Not Matched please enter correct value");
	// return "supplierCheckout";
	// }
	//
	// paymentTransaction.setCountry(supplier.getRegistrationOfCountry());
	// subscription.setPaymentTransaction(paymentTransaction);
	//
	// subscription.setSupplier(supplier);
	//
	// SupplierPackage sp = new SupplierPackage(subscription);
	// supplier.setSupplierPackage(sp);
	//
	// String returnURL = appUrl + "/suppliersubscription/doSupplierPaymentConfirm?type=Sale&&txId=";
	// String cancelURL = appUrl + "/suppliersubscription/supplierCheckout?cancelled=true";
	//
	// return subscriptionService.initiateSupplierRegistrationPaypalPayment(subscription, null, session, returnURL,
	// cancelURL);
	// // supplier.setStatus(SupplierStatus.PENDING);
	// // supplierService.saveSupplier(supplier, true);
	//
	// } catch (Exception e) {
	// LOG.error("Error while storing Supplier : " + e.getMessage(), e);
	// model.addAttribute("errors", messageSource.getMessage("supplier.error.detail", new Object[] {
	// supplier.getCompanyName(), e.getMessage() }, Global.LOCALE));
	// model.addAttribute("countryList", countryService.getAllCountries());
	// model.addAttribute("supplier", supplier);
	// return "supplierCheckout";
	// }
	// // return "redirect:supplierCheckoutThankyou";
	// }
	//
	// }

	@RequestMapping(path = "/doSupplierPaymentConfirm", method = RequestMethod.GET)
	public String doSupplierPaymentConfirm(@RequestParam(name = "type", required = false) String type, @RequestParam(name = "txId") String txId, @RequestParam(name = "token") String token, @RequestParam(name = "PayerID") String payerID, Model model, HttpServletRequest request, HttpSession session) {
		boolean isSale = true;
		if (StringUtils.checkString(type).equalsIgnoreCase("order")) {
			isSale = false;
		}

		try {

			String subscriptionId = (String) session.getAttribute("subscriptionId");
			LOG.info("Subscription Id : " + subscriptionId);
			SupplierSubscription subscription = subscriptionService.getSupplierSubscriptionById(subscriptionId);
			if (subscription != null) {
				Supplier supplier = (Supplier) session.getAttribute("supplier");
				subscription.setSupplier(supplier);
			}
			model.addAttribute("subscription", subscription);

			PaymentTransaction paymentTransaction = subscriptionService.getPaymentTransactionById(txId);
			model.addAttribute("paymentTransaction", paymentTransaction);

			LOG.info(" >>>>>>>>>>>>>>  TOKEN : " + token);

			/*
			 * Build a second API request to PayPal, using the token as the ID to get the details on the payment
			 * authorization
			 */
			String nvpstr = "&TOKEN=" + token;

			LOG.info("Invoking PayPal Checkout Details ===================> ");
			/*
			 * Make the API call and store the results in an array. If the call was a success, show the authorization
			 * details, and provide an action to complete the payment. If failed, show the error
			 */
			HashMap<String, String> nvp = subscriptionService.invokePaypalService("GetExpressCheckoutDetails", nvpstr);
			String strAck = nvp.get("ACK").toString();
			if (strAck != null && (strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS) || strAck.equalsIgnoreCase("SuccessWithWarning"))) {
				session.setAttribute("PAYERID", nvp.get("PAYERID").toString());
			}

			LOG.info("Payment Checkout completed...!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

			String serverName = request.getServerName();
			LOG.info("Server Name :" + serverName);
			subscriptionService.confirmSupplierPaymentSubscription(token, nvp.get("PAYERID").toString(), model, serverName, txId, type, isSale);
		} catch (Exception e) {
			LOG.error("Error while trial confirm : " + e.getMessage(), e);
		}
		return "redirect:supplierCheckoutThankyou";
	}

	@RequestMapping(path = "/doPaymentConfirm", method = RequestMethod.GET)
	public String doInitiateTrialConfirm(@RequestParam(name = "type", required = false) String type, @RequestParam(name = "txId") String txId, @RequestParam(name = "token") String token, @RequestParam(name = "PayerID") String payerID, Model model, HttpServletRequest request, HttpSession session) {
		boolean isSale = true;
		if (StringUtils.checkString(type).equalsIgnoreCase("order")) {
			isSale = false;
		}

		try {

			String subscriptionId = (String) session.getAttribute("subscriptionId");
			LOG.info("Subscription Id : " + subscriptionId);
			SupplierSubscription subscription = subscriptionService.getSupplierSubscriptionById(subscriptionId);
			if (subscription != null) {
				Supplier supplier = (Supplier) session.getAttribute("supplier");
				subscription.setSupplier(supplier);
			}
			model.addAttribute("subscription", subscription);

			PaymentTransaction paymentTransaction = subscriptionService.getPaymentTransactionById(txId);
			model.addAttribute("paymentTransaction", paymentTransaction);

			LOG.info(" >>>>>>>>>>>>>>  TOKEN : " + token);

			/*
			 * Build a second API request to PayPal, using the token as the ID to get the details on the payment
			 * authorization
			 */
			String nvpstr = "&TOKEN=" + token;

			LOG.info("Invoking PayPal Checkout Details ===================> ");
			/*
			 * Make the API call and store the results in an array. If the call was a success, show the authorization
			 * details, and provide an action to complete the payment. If failed, show the error
			 */
			HashMap<String, String> nvp = subscriptionService.invokePaypalService("GetExpressCheckoutDetails", nvpstr);
			String strAck = nvp.get("ACK").toString();
			if (strAck != null && (strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS) || strAck.equalsIgnoreCase("SuccessWithWarning"))) {
				session.setAttribute("PAYERID", nvp.get("PAYERID").toString());
			}

			LOG.info("Payment Checkout completed...!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

			String serverName = request.getServerName();
			LOG.info("Server Name :" + serverName);
			subscriptionService.confirmSupplierPaymentSubscription(token, nvp.get("PAYERID").toString(), model, serverName, txId, type, isSale);
		} catch (Exception e) {
			LOG.error("Error while trial confirm : " + e.getMessage(), e);
		}
		return "redirect:supplierCheckoutThankyou";
	}

	@RequestMapping(path = "/supplierCheckoutThankyou", method = RequestMethod.GET)
	public String supplierCheckoutThankyou(Model model, HttpSession session) {
		LOG.info("supplier Checkout Thank you GET called");
		return "supplierCheckoutThankyou";
	}

	@RequestMapping(path = "/getSupplierPrice", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> getSupplierPrice(@RequestParam(name = "selectedPlan") String selectedPlan, @RequestParam(name = "promoCode") String promoCode, @RequestParam(name = "countryId", required = false) String countryId) throws JsonProcessingException {
		LOG.info("get Supplier Price called ...selectedPlan: " + selectedPlan);
		LOG.info("promoCode: " + promoCode);
		Map<String, Object> result = new HashMap<>();
		try {
			BigDecimal promoDiscountPrice = BigDecimal.ZERO;
			BigDecimal supplierPrice = BigDecimal.ZERO;
			BigDecimal tax = BigDecimal.ZERO;
			SupplierPlan sp = null;
			if (selectedPlan != null && selectedPlan.equalsIgnoreCase("SINGLEBUYER")) {
				// TODO change from supplier plan
				PlanStatus[] planStatuses = { PlanStatus.HIDDEN };
				List<SupplierPlan> plans = supplierPlanService.findAllPlansByStatuses(planStatuses);
				SupplierPlan singlePlan = null;
				if (CollectionUtil.isNotEmpty(plans)) {
					for (SupplierPlan plan : plans) {
						if (plan.getBuyerLimit() == 1) {
							singlePlan = plan;
							sp = plan;
							break;
						}
					}
				}
				if (singlePlan != null) {
					supplierPrice = new BigDecimal(singlePlan.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
				} else {
					supplierPrice = new BigDecimal(30).setScale(2, BigDecimal.ROUND_HALF_UP);
				}

				if (StringUtils.isNotBlank(countryId)) {
					Country country = countryService.getCountryById(countryId);
					if (country == null) {
						country = countryService.getCountryByCode(countryId);
					}
					if (country != null) {
						if ("MY".equals(country.getCountryCode())) {
							tax = new BigDecimal(singlePlan.getPrice()).multiply((singlePlan.getTax().divide(new BigDecimal(100))));
							result.put("tax", singlePlan.getTax());
						} else {
							result.put("tax", 0);
						}
					}
				}
				result.put("tax", singlePlan.getTax());
			} else {
				// TODO change from supplier plan
				SupplierPlan plan = supplierPlanService.getAllBuyerSupplierPlan();
				sp = plan;
				if (plan != null) {
					supplierPrice = new BigDecimal(plan.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
				} else {

					supplierPrice = new BigDecimal(199).setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				if (StringUtils.isNotBlank(countryId)) {
					Country country = countryService.getCountryById(countryId);
					if (country == null) {
						country = countryService.getCountryByCode(countryId);
					}
					if (country != null) {
						if ("MY".equals(country.getCountryCode())) {
							tax = new BigDecimal(plan.getPrice()).multiply((plan.getTax().divide(new BigDecimal(100))));
							result.put("tax", plan.getTax());
						} else {
							result.put("tax", 0);
						}
					}
				} else {
					result.put("tax", 0);
				}

			}
			BigDecimal totalPrice = supplierPrice.add(tax);
			result.put("totalPrice", totalPrice);
			result.put("supplierPrice", supplierPrice);
			result.put("promoDiscountPrice", promoDiscountPrice);
			try {
				promotionalCodeService.validatePromoCode(promoCode, sp, null, totalPrice, TenantType.SUPPLIER, null);
				promoDiscountPrice = getPromoDiscount(promoCode, totalPrice, result, sp);
				if (totalPrice.compareTo(BigDecimal.ZERO) == -1) {
					throw new ApplicationException("Invalid Promo Code");
				}
				result.put("promoDiscountPrice", promoDiscountPrice);
				totalPrice = totalPrice.subtract(promoDiscountPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
				result.put("totalPrice", totalPrice);
			} catch (ApplicationException e) {
				result.put("error", e.getMessage());
				LOG.error(e.getMessage());
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while calculate supplier price : " + e.getMessage(), e);
			return new ResponseEntity<Map<String, Object>>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @param promoCode
	 * @param totalPrice
	 * @param result
	 * @throws Exception
	 */
	public BigDecimal getPromoDiscount(String promoCode, BigDecimal totalPrice, Map<String, Object> result, SupplierPlan plan) throws Exception {
		BigDecimal promoDiscountPrice = BigDecimal.ZERO;
		if (StringUtils.checkString(promoCode).length() > 0) {
			PromotionalCode promo = promotionalCodeService.getPromotionalCodeByPromoCode(promoCode);
			if (result != null) {
				result.put("promoCodeId", promo.getId());
			}
			if (promo.getDiscountType() == ValueType.VALUE) {
				promoDiscountPrice = new BigDecimal(promo.getPromoDiscount());
				LOG.info("promoDiscountPrice: " + promoDiscountPrice);
			} else {
				Integer discountPer = promo.getPromoDiscount();
				if (discountPer != null) {
					promoDiscountPrice = totalPrice.multiply(new BigDecimal(discountPer)).setScale(2, BigDecimal.ROUND_HALF_UP);
					promoDiscountPrice = promoDiscountPrice.divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
					LOG.info("promoDiscountPrice: " + promoDiscountPrice);
				}
			}

			if (promoDiscountPrice.compareTo(totalPrice) == 1) {
				throw new ApplicationException("This promo code is invalid");
			}

			if (promo != null && promo.getSupplier() != null) {
				if (SecurityLibrary.getLoggedInUserTenantId() != null) {
					if (!(StringUtils.equals(promo.getSupplier().getId(), SecurityLibrary.getLoggedInUserTenantId()))) {
						throw new ApplicationException("Promo code is not valid for this supplier.");
					}
				} else {
					throw new ApplicationException("Promo code is not valid for this supplier.");
				}
			}

			if (promo != null && promo.getSupplierPlan() != null) {
				if (!StringUtils.equals(promo.getSupplierPlan().getId(), plan.getId())) {
					throw new ApplicationException("Promo code is not valid for this plan.");
				}
			}

		}
		return promoDiscountPrice;
	}

	@RequestMapping(value = "/supplierCheckout", method = RequestMethod.POST)
	public String saveRegistration(@Validated(value = { SupplierSignup.class }) @ModelAttribute("supplier") Supplier supplier, BindingResult result, Model model) throws AuthenticationException {
		LOG.warn("***** INVALID SUPPLIER SUBSCRIPTION ******");
		throw new AuthenticationException("Invalid Subscription", null, "401", 401);
	}

	/**
	 * @param supplier
	 * @return
	 */
	private Supplier ActiveSingleBuyerSubcription(Supplier supplier) {
		SupplierSubscription subscription = new SupplierSubscription();
		// PH 211 & PH 224 issue fixed
		subscription.setBuyerLimit(999);
		subscription.setCreatedDate(new Date());
		subscription.setStartDate(new Date());
		subscription.setActivatedDate(new Date());
		subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
		subscription.setPromoCode(supplier.getPromocode());

		PlanStatus[] planStatuses = { PlanStatus.ACTIVE };
		List<SupplierPlan> plans = supplierPlanService.findAllPlansByStatuses(planStatuses);

		SupplierPlan singlePlan = null;
		if (CollectionUtil.isNotEmpty(plans)) {
			for (SupplierPlan plan : plans) {
				if (plan.getBuyerLimit() == 1) {
					singlePlan = plan;
					break;
				}
			}
		}
		if (supplier.getSupplierSubscription() != null) {
			subscription.setPromoCode(supplier.getSupplierSubscription().getPromoCode());
			LOG.info("######" + subscription.getPromoCode());
		}
		if (singlePlan != null) {
			Calendar endDate = Calendar.getInstance();
			if (singlePlan.getPeriodUnit() == PeriodUnitType.MONTH) {
				endDate.add(Calendar.MONTH, singlePlan.getPeriod());
			} else if (singlePlan.getPeriodUnit() == PeriodUnitType.YEAR) {
				endDate.add(Calendar.YEAR, singlePlan.getPeriod());
			}
			subscription.setEndDate(endDate.getTime());

			// TODO add supplier used promo code
			subscription.setPromoCodeDiscount(new BigDecimal(singlePlan.getPrice()));

			subscription.setSupplierPlan(null);
			subscription.setPriceAmount(new BigDecimal(singlePlan.getPrice()));
			subscription.setPromoCodeDiscount(new BigDecimal(singlePlan.getPrice()));
			subscription.setTotalPriceAmount(BigDecimal.ZERO);
			subscription.setCurrencyCode(singlePlan.getCurrency().getCurrencyCode());
		} else {
			// This block will not be executed as for unlimited buyers, the user will be taken trough the paypal
			// checkout flow

			// TODO add supplier used promo code
			subscription.setPromoCodeDiscount(BigDecimal.ZERO);

			subscription.setSupplierPlan(null);
			subscription.setPriceAmount(BigDecimal.ZERO);
			subscription.setPromoCodeDiscount(BigDecimal.ZERO);
			subscription.setTotalPriceAmount(BigDecimal.ZERO);
		}

		try {

			subscription.setSupplier(supplier);
			supplierSubscriptionDao.save(subscription);

			SupplierPackage sp = new SupplierPackage(subscription);
			supplier.setSupplierSubscription(subscription);
			supplier.setSupplierPackage(sp);
			supplier = supplierService.updateSupplier(supplier);
		} catch (Exception e) {
			LOG.error("Error storing supplier subscription : " + e.getMessage(), e);
		}
		return supplier;
	}

	@RequestMapping(value = "/doInitiateSupplierPayment", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PaymentIntentPojo> saveSupplierRegistration(@RequestBody Supplier supplier, @RequestParam(required = false, name = "promoCodeId") String promoCodeId, @RequestParam(name = "supplerPlan") String supplerPlan, @RequestParam(name = "supplierPrice") BigDecimal supplierPrice, @RequestParam(required = false, name = "promoDiscountPrice") BigDecimal promoDiscountPrice, @RequestParam(name = "totalPrice") BigDecimal totalPrice, @RequestParam(name = "paymentMode") String paymentMode, @RequestParam(name = "country") String country) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("Initiating supplier payment with promo code: " + promoCodeId + " and supplier plan " + supplerPlan + " with plan price " + supplierPrice + " and discount price " + promoDiscountPrice + " and total price " + totalPrice);
		promoDiscountPrice = BigDecimal.ZERO;
		try {

			supplier.setRegistrationOfCountry(countryService.searchCountryById(country));
			User user = userService.getPlainUserByLoginId(supplier.getLoginEmail());
			String regex = null;
			String msg = null;
			if (user != null) {
				PasswordSettings settings = passwordSettingService.getPasswordRegex(user.getTenantId());
				if (settings != null) {
					regex = settings.getRegx();
					msg = null;
				}
			}
			if (supplierService.isExistsLoginEmail(supplier.getLoginEmail())) {
				headers.add("error", messageSource.getMessage("supplier.login.email.exisit", new Object[] { supplier.getLoginEmail() }, Global.LOCALE));
				return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			} else if (userService.getUserByLoginName(supplier.getLoginEmail()) != null) {
				headers.add("error", messageSource.getMessage("supplier.login.email.exisit", new Object[] { supplier.getLoginEmail() }, Global.LOCALE));
				return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			if (!StringUtils.validatePasswordWithRegx(supplier.getPassword(), regex)) {
				headers.add("error", StringUtils.checkString(msg).length() > 0 ? msg : messageSource.getMessage("user.password.week", new Object[] { supplier.getLoginEmail() }, Global.LOCALE));
				return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			if (supplierService.isExists(supplier)) {
				headers.add("error", messageSource.getMessage("supplier.error.duplicate", new Object[] { supplier.getCompanyName(), supplier.getCompanyRegistrationNumber(), supplier.getRegistrationOfCountry() }, Global.LOCALE));
				return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			SupplierSubscription subscription = new SupplierSubscription();
			SupplierPlan plan = supplierPlanService.getAllBuyerSupplierPlan();
			subscription.setSupplierPlan(plan);
			subscription.setPriceAmount(new BigDecimal(plan.getPrice()));

			PaymentTransaction paymentTransaction = new PaymentTransaction();

			BigDecimal tax = BigDecimal.ZERO;
			BigDecimal taxAmount = BigDecimal.ZERO;

			if (tax == null) {
				tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				if (supplier.getRegistrationOfCountry() != null && "MY".equalsIgnoreCase(supplier.getRegistrationOfCountry().getCountryCode())) {
					tax = plan.getTax();
					tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
				} else {
					tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
				}
			}

			taxAmount = (supplierPrice.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

			if (StringUtils.checkString(promoCodeId).length() > 0) {
				PromotionalCode promoCode = promotionalCodeService.getPromotionalCodeById(promoCodeId);
				if (promoCode != null) {
					promotionalCodeService.validatePromoCode(promoCode.getPromoCode(), plan, null, supplierPrice.add(taxAmount), TenantType.SUPPLIER, null);
					paymentTransaction.setPromoCode(promoCode);
					subscription.setPromoCode(promoCode.getPromoCode());
					promoDiscountPrice = getPromoDiscount(promoCode.getPromoCode(), supplierPrice.add(taxAmount), null, plan);
					subscription.setPromoCodeDiscount(promoDiscountPrice);
					paymentTransaction.setPromoCodeDiscount(promoDiscountPrice);
				} else {
					subscription.setPromoCodeDiscount(BigDecimal.ZERO);
					paymentTransaction.setPromoCodeDiscount(BigDecimal.ZERO);
				}
			} else {
				subscription.setPromoCodeDiscount(BigDecimal.ZERO);
				paymentTransaction.setPromoCodeDiscount(BigDecimal.ZERO);
			}

			subscription.setTotalPriceAmount(supplierPrice);
			paymentTransaction.setAdditionalTax(taxAmount);
			paymentTransaction.setCountry(supplier.getRegistrationOfCountry());
			paymentTransaction.setDesignation(supplier.getDesignation());
			subscription.setPaymentTransaction(paymentTransaction);

			subscription.setSupplier(supplier);

			SupplierPackage sp = new SupplierPackage(subscription);
			supplier.setSupplierPackage(sp);
			return new ResponseEntity<PaymentIntentPojo>(stripeSupplierSubscriptionService.initiateSupplierRegistrationStripePayment(subscription, paymentMode), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while Supplier subscription : " + e.getMessage(), e);
			headers.add("error", e.getLocalizedMessage());
			return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
