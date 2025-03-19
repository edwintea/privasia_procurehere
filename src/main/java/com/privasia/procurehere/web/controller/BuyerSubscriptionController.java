/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Validation;
import javax.validation.Validator;

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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.NotificationMessageDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerPackage;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.FreeTrialEnquiry;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.PlanPeriod;
import com.privasia.procurehere.core.entity.PlanRange;
import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.enums.TransactionType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.BuyerSubscriptionStripeRequestPojo;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerPlanService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSubscriptionService;
import com.privasia.procurehere.service.BuyerSubscriptionStripeService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.FreeTrialEnquiryService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.PaymentTransactionService;
import com.privasia.procurehere.service.PlanService;
import com.privasia.procurehere.service.PromotionalCodeService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.impl.SubscriptionServiceImpl;
import com.privasia.procurehere.web.editors.BuyerEditor;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;

/**
 * @author Nitin Otageri
 */
@Controller
@RequestMapping(value = "/buyerSubscription")
public class BuyerSubscriptionController {

	private static final Logger LOG = LogManager.getLogger(Global.SUBSCRIPTION_LOG);

	@Autowired
	PlanService planService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	BuyerEditor buyerEditor;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	CountryService countryService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	UomService uomService;

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

	@Value("${paypal.ipn.url}")
	String PAYPAL_IPN_URL;

	@Autowired
	PaymentTransactionService paymentTransactionService;

	@Autowired
	BuyerPlanService buyerPlanService;

	@Autowired
	PromotionalCodeService promotionalCodeService;

	@Autowired
	BuyerSubscriptionService buyerSubscriptionService;

	@Autowired
	NotificationMessageDao notificationMessageDao;

	@Autowired
	NotificationService notificationService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	UserService userService;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	UserDao userDao;

	@Value("${paypal.environment}")
	String PAYPAL_ENVIRONMENT;

	@Autowired
	FreeTrialEnquiryService freeTrialInquiryService;

	@Autowired
	BuyerSubscriptionStripeService stripeBuyerSubscriptionService;

	@Value("${stripe.publish}")
	String stripePublicKey;

	@SuppressWarnings("unused")
	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(Buyer.class, buyerEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(Country.class, countryEditor);
	}

	// User methods for buyer Package

	@RequestMapping(path = "/selectPlan", method = RequestMethod.GET)
	public String planList(Model model) {
		List<BuyerPlan> buyerPlanList = buyerPlanService.findAllBuyerPlansByStatus(PlanStatus.ACTIVE);
		model.addAttribute("planList", buyerPlanList);
		return "selectPlan";
	}

	@RequestMapping(path = "/getPromoCode", method = RequestMethod.GET)
	public ResponseEntity<PromotionalCode> getPromoCode(@RequestParam("promoCode") String promoCode, @RequestParam(name = "plan", required = false) String planId, @RequestParam(name = "totalPrice", required = false) BigDecimal totalPrice) {
		LOG.info(" Promo Code : " + promoCode);
		PromotionalCode promotionalCode = null;
		try {
			if (StringUtils.isNotBlank(planId)) {
				BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
				promotionalCodeService.validatePromoCode(promoCode, null, plan, totalPrice, TenantType.BUYER, null);
			}
			promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCode(promoCode);
			// LOG.info("Promo code :" + promotionalCode.toLogString());
		} catch (ApplicationException e) {
			LOG.error("Error while fetching promo code :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", e.getMessage());
			return new ResponseEntity<PromotionalCode>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			LOG.error("Error while fetching promo code :" + e.getMessage(), e);
			return new ResponseEntity<PromotionalCode>(null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<PromotionalCode>(promotionalCode, HttpStatus.OK);
	}

	@RequestMapping(path = "/get/{planId}", method = RequestMethod.POST)
	public String buyPlan(@RequestParam("numberUserEvent") String numberUserEvent, @RequestParam(required = false, value = "periodId") String periodId, @RequestParam(value = "rangeId") String rangeId, @RequestParam(required = false, value = "promoCodeId") String promoCodeId, @RequestParam(value = "feeValue") BigDecimal feeValue, @RequestParam(required = false, value = "feeDiscountValue") BigDecimal feeDiscountValue, @RequestParam(required = false, value = "promoCodeDiscount") BigDecimal promoCodeDiscount, @RequestParam(value = "totalFeeAmount") BigDecimal totalFeeAmount, @PathVariable(name = "planId") String planId, @RequestParam(required = false, value = "autoChargeSubscription") boolean autoChargeSubscription, Model model, HttpSession session) {
		try {
			LOG.info("Auto Charge Subscription :" + autoChargeSubscription);
			LOG.info("User requested to totalFeeAmount : " + totalFeeAmount);
			if (totalFeeAmount.compareTo(new BigDecimal(0)) <= 0) {
				throw new ApplicationException("Total fee should be greater then Zero");
			}
			BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
			BuyerSubscription subscription = new BuyerSubscription();
			subscription.setPlan(plan);
			subscription.setPlanType(plan.getPlanType());
			subscription.setPriceAmount(feeValue);
			subscription.setPriceDiscount(feeDiscountValue);
			subscription.setTotalPriceAmount(totalFeeAmount);
			subscription.setAutoChargeSubscription(autoChargeSubscription);

			if (promoCodeDiscount != null) {
				subscription.setPromoCodeDiscount(promoCodeDiscount);
			} else {
				subscription.setPromoCodeDiscount(new BigDecimal(0));
			}

			if (subscription.getPlanType() == PlanType.PER_USER) {
				subscription.setUserQuantity(new Integer(numberUserEvent));
				subscription.setEventQuantity(9999); // set default 9999 max event on user based
			} else if (subscription.getPlanType() == PlanType.PER_EVENT) {
				subscription.setEventQuantity(new Integer(numberUserEvent));
				subscription.setUserQuantity(999); // set default 999 max user on event based
			}
			PlanRange range = null;
			if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
				for (PlanRange pRange : plan.getRangeList()) {
					if (pRange.getId().equals(rangeId)) {
						range = pRange;
					}
				}
			}
			subscription.setRange(range);

			PlanPeriod period = null;
			if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
				for (PlanPeriod prd : plan.getPlanPeriodList()) {
					if (prd.getId().equals(periodId)) {
						period = prd;
					}
				}
			}

			subscription.setPlanPeriod(period);

			subscription.setBuyer(new Buyer());

			PaymentTransaction pt = new PaymentTransaction();
			if (StringUtils.checkString(promoCodeId).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
				// cross checking for promocode
				try {
					promotionalCodeService.getPromotionalCodeByPromoCode(promotionalCode.getPromoCode());
				} catch (Exception e) {
					throw e;
				}
				pt.setPromoCode(promotionalCode);
			}
			subscription.setPaymentTransactions(Arrays.asList(pt));
			model.addAttribute("subscription", subscription);
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
			model.addAttribute("plan", plan);
			model.addAttribute("countryList", countryService.getAllCountries());
			return "buyPlan";
		} catch (Exception e) {
			LOG.error("Error fetching Buy Screen : " + e.getMessage(), e);
			List<BuyerPlan> buyerPlanList = buyerPlanService.findAllBuyerPlansByStatus(PlanStatus.ACTIVE);
			model.addAttribute("error", e.getMessage());
			model.addAttribute("planList", buyerPlanList);
			return "selectPlan";
		}
	}

	@RequestMapping(path = "/payment/{planId}", method = RequestMethod.POST)
	public String doPayment(@ModelAttribute(name = "subscription") BuyerSubscription subscription, @PathVariable(name = "planId") String planId, Model model, HttpSession session, HttpServletRequest request) {
		LOG.info("User requested to purchase Plan : " + planId);
		try {
			if (StringUtils.checkString(planId).length() > 0) {
				String returnURL = appUrl + "/buyerSubscription/confirm?planId=" + planId + "&txId=";
				String cancelURL = appUrl + "/buyerSubscription/cancel?planId=" + planId + "&txId=";
				return buyerSubscriptionService.initiatePaypalPaymentForBuyerSubscription(subscription, planId, session, returnURL, cancelURL);
			} else {
				LOG.error("Plan is null");
				return "redirect:/buyerSubscription/selectPlan";
			}
		} catch (Exception e) {
			LOG.error("Error while payment : " + e.getMessage(), e);
			return "redirect:/buyerSubscription/selectPlan";
		}
	}

	@RequestMapping(path = "/cancel", method = RequestMethod.GET)
	public String doCancel(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, Model model, RedirectAttributes redir) {
		LOG.info("paln Id :" + planId + " txId :" + txId);
		PaymentTransaction paymentTransaction = buyerSubscriptionService.cancelPaymentTransaction(txId);
		LOG.info("Cancel payment :" + paymentTransaction.getErrorCode() + " - " + paymentTransaction.getErrorMessage());
		redir.addFlashAttribute("warn", paymentTransaction.getErrorCode() + " - " + paymentTransaction.getErrorMessage());
		return "redirect:/buyerSubscription/selectPlan";
	}

	@RequestMapping(path = "/subscriptionError/{planId}/{paymentTransactionId}", method = RequestMethod.GET)
	public String showPaymentError(@PathVariable(name = "planId") String planId, @PathVariable(name = "paymentTransactionId") String paymentTransactionId, Model model, RedirectAttributes redir) {
		PaymentTransaction paymentTransaction = buyerSubscriptionService.errorPaymentTransaction(paymentTransactionId);// buyerSubscriptionService.cancelPaymentTransaction(paymentTransactionId);
		LOG.info("Subscription Error payment :" + paymentTransaction.getErrorCode() + " - " + paymentTransaction.getErrorMessage());
		redir.addFlashAttribute("error", paymentTransaction.getErrorCode() + " - " + paymentTransaction.getErrorMessage());
		return "redirect:/buyerSubscription/selectPlan";
	}

	/*********************************************************************************
	 * GetShippingDetails: Function to perform the GetExpressCheckoutDetails API call Inputs: None Output: Returns a
	 * HashMap object containing the response from the server.
	 *********************************************************************************/
	@RequestMapping(path = "/confirm", method = RequestMethod.GET)
	public String showConfirmPayment(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, @RequestParam(name = "token") String token, @RequestParam(name = "PayerID") String payerID, Model model, HttpServletRequest request, HttpSession session) {

		String subscriptionId = (String) session.getAttribute("subscriptionId");
		LOG.info("Subscription Id : " + subscriptionId);
		BuyerSubscription subscription = buyerSubscriptionService.getBuyerSubscriptionById(subscriptionId);
		if (subscription != null) {
			Buyer buyer = (Buyer) session.getAttribute("buyer");
			subscription.setBuyer(buyer);
		}
		model.addAttribute("subscription", subscription);

		PaymentTransaction paymentTransaction = buyerSubscriptionService.getPaymentTransactionById(txId);
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
		HashMap<String, String> nvp = buyerSubscriptionService.invokePaypalService("GetExpressCheckoutDetails", nvpstr);
		String strAck = nvp.get("ACK").toString();
		if (strAck != null && (strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS) || strAck.equalsIgnoreCase("SuccessWithWarning"))) {
			session.setAttribute("PAYERID", nvp.get("PAYERID").toString());
		}

		// The map will contain values as documented at
		// https://developer.paypal.com/docs/classic/api/merchant/GetExpressCheckoutDetails_API_Operation_NVP/
		model.addAttribute("paypalResponse", nvp);
		return "confirmSubscription";
	}

	@RequestMapping(path = "/confirm", method = RequestMethod.POST)
	public String doConfirmPayment(@RequestParam(name = "paymentTransactionId") String paymentTransactionId, @RequestParam(name = "token") String token, @RequestParam(name = "payerId") String payerId, Model model, HttpServletRequest request, HttpSession session) {
		// PaymentTransaction paymentTransaction;
		// Subscription subscription;
		try {
			String serverName = request.getServerName();
			LOG.info("Server Name :" + serverName);
			User user = buyerSubscriptionService.confirmSubscription(token, payerId, model, serverName, paymentTransactionId, true);

			buyerService.sentBuyerCreationMail(user.getBuyer(), user);
			Owner owner = buyerService.getDefaultOwner();
			try {
				uomService.loadDefaultUomIntoBuyerAccount(user.getBuyer(), owner);
			} catch (Exception e) {
				LOG.error("Error loading default UOM for buyer : " + e.getMessage(), e);
			}
		} catch (ApplicationException e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);
			PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionWithBuyerPlanById(paymentTransactionId);
			return "redirect:../buyerSubscription/subscriptionError/" + paymentTransaction.getBuyerPlan().getId() + "/" + paymentTransactionId;
		} catch (Exception e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);
		}
		return "subscriptionSuccess";
	}

	@RequestMapping(path = "/instantPaymentNotification", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> getPaymentNotification(@RequestBody String paypalNotificationResponse) throws JsonProcessingException {
		LOG.info("getPaymentNotification called ..." + paypalNotificationResponse);

		try {
			String responeText = sendPaypalValidateRequest(paypalNotificationResponse);
			HashMap<String, String> nvp = deformatNVP(paypalNotificationResponse);
			if (StringUtils.checkString(responeText).length() > 0 && "VERIFIED".equals(responeText)) {
				// Your business code comes here

				LOG.info("TXN_TYPE : " + nvp.get("TXN_TYPE"));

				if (StringUtils.checkString(nvp.get("TXN_TYPE")).length() > 0) {
					// Check TXN_TYPE = recurring_payment
					if (nvp.get("TXN_TYPE").equalsIgnoreCase("recurring_payment")) {
						LOG.info("payment profile Id : " + nvp.get("RECURRING_PAYMENT_ID"));
						BuyerSubscription buyerSubscription = buyerSubscriptionService.getActiveBuyerSubscriptionByPaymentProfileId(nvp.get("RECURRING_PAYMENT_ID"));
						if (buyerSubscription != null) {
							if (buyerSubscription.getNextSubscription() == null) {
								saveFutureSubscriptionAfterRecurringPayment(buyerSubscription, nvp);

							} else {
								LOG.info("Subscription already contains next subscription for profile id : " + nvp.get("RECURRING_PAYMENT_ID"));

								// duplicate possibility email to procurehere subscription Admin
								String superAdminsubsMail = ownerSettingsService.getBuyerSubsMailFromOwnersettings();
								if (StringUtils.checkString(superAdminsubsMail).length() > 0) {
									sendBuyerAutoSubscriptionDuplicatePossibleMail(superAdminsubsMail, nvp, buyerSubscription.getBuyer());
								}
							}

						} else {
							LOG.error("Subscription not found for profile id : " + nvp.get("RECURRING_PAYMENT_ID"));
						}

					} else if (nvp.get("TXN_TYPE").equalsIgnoreCase("recurring_payment_profile_cancel")) {

						BuyerSubscription buyerSubscription = buyerSubscriptionService.getActiveBuyerSubscriptionByPaymentProfileId(nvp.get("RECURRING_PAYMENT_ID"));
						if (buyerSubscription != null) {
							if (buyerSubscription.getRecurringCancelDate() == null) {
								buyerSubscription.setRecurringCancelDate(new Date());
								buyerSubscription.setRecurringCancelRemarks("Profile '" + nvp.get("RECURRING_PAYMENT_ID") + "' cancel by user from paypal account.");
								buyerSubscriptionService.updateSubscription(buyerSubscription);
							} else {
								LOG.info("cancel by procurehere on change or renew plan");
							}

							try {
								Buyer buyer = buyerSubscription.getBuyer();
								// sending
								String timeZone = "GMT+8:00";
								User buyerUser = userService.getPlainUserByLoginId(buyer.getLoginEmail());
								timeZone = getTimeZoneByBuyerSettings(buyer.getId(), timeZone);
								sendBuyerAutoSubscriptionCancelMail(buyerUser, buyerSubscription, timeZone, buyer);
								// send successful email and dashboard notification to Application Admin
								String superAdminsubsMail = ownerSettingsService.getBuyerSubsMailFromOwnersettings();
								if (StringUtils.checkString(superAdminsubsMail).length() > 0) {
									User appAdminUser = userDao.getAdminUser();
									appAdminUser.setCommunicationEmail(superAdminsubsMail);
									timeZone = "GMT+8:00";
									sendBuyerAutoSubscriptionCancelMail(appAdminUser, buyerSubscription, timeZone, buyer);
								}
							} catch (Exception e) {
								LOG.error("Error while sending cancel subs notification :" + e.getMessage(), e);
							}
						} else {
							LOG.error("Subscription not found for profile id : " + nvp.get("RECURRING_PAYMENT_ID"));
						}

					} else {
						LOG.info("TXN_TYPE '" + nvp.get("TXN_TYPE") + "' is other then recurring_payment and recurring_payment_profile_cancel");
					}
				} else {
					LOG.info("TXN_TYPE is null");
				}
				LOG.info("Response from PayPal ===================> ");
				for (Map.Entry<String, String> entry : nvp.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					LOG.info(key + " = " + value);
				}

			} else {
				LOG.info("Invalid response after validation");
				LOG.info("Response from PayPal ===================> ");
				for (Map.Entry<String, String> entry : nvp.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					LOG.info(key + " = " + value);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while getting Instant payment notification :" + e.getMessage(), e);
		}

		return new ResponseEntity<String>("", HttpStatus.OK);
	}

	public String sendPaypalValidateRequest(String nvpStr) {

		// String PAYPAL_IPN_URL = "https://www.sandbox.paypal.com/cgi-bin/webscr"; // (for Sandbox IPNs)
		// String PAYPAL_IPN_URL = "https://www.paypal.com/cgi-bin/webscr"; // (for live IPNs)

		String gv_IPN = PAYPAL_IPN_URL;

		LOG.info(" gv_APIEndpoint : " + gv_IPN);

		String agent = "ProcurehereInstPayVerfy";
		String respText = "";

		String encodedData = "?cmd=_notify-validate&" + nvpStr;

		try {
			URL postURL = new URL(gv_IPN + encodedData);
			HttpURLConnection conn = (HttpURLConnection) postURL.openConnection();

			// Set connection parameters. We need to perform input and output so set both as true.
			conn.setDoInput(true);
			conn.setDoOutput(true);

			// Set the content type we are POSTing. We impersonate it as encoded form data
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent", agent);

			// conn.setRequestProperty( "Content-Type", type );
			conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
			conn.setRequestMethod("GET");

			// get the output stream to POST to.
			DataOutputStream output = new DataOutputStream(conn.getOutputStream());
			output.writeBytes(encodedData);
			output.flush();
			output.close();

			// Read input from the input stream.
			// DataInputStream in = new DataInputStream(conn.getInputStream());
			int rc = conn.getResponseCode();
			if (rc != -1) {
				BufferedReader is = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String _line = null;
				while (((_line = is.readLine()) != null)) {
					respText = respText + _line;
				}
				LOG.info("respText :" + respText);
			}

			return respText;
		} catch (IOException e) {
			// handle the error here
			LOG.info("Error during Paypal instant notification Call : " + e.getMessage(), e);
			return null;
		}
	}

	/*********************************************************************************
	 * deformatNVP: Function to break the NVP string into a HashMap pPayLoad is the NVP string. returns a HashMap object
	 * containing all the name value pairs of the string.
	 *********************************************************************************/
	@SuppressWarnings("deprecation")
	private HashMap<String, String> deformatNVP(String pPayload) {
		HashMap<String, String> nvp = new HashMap<String, String>();
		StringTokenizer stTok = new StringTokenizer(pPayload, "&");
		while (stTok.hasMoreTokens()) {
			StringTokenizer stInternalTokenizer = new StringTokenizer(stTok.nextToken(), "=");
			if (stInternalTokenizer.countTokens() == 2) {
				String key = URLDecoder.decode(stInternalTokenizer.nextToken());
				String value = URLDecoder.decode(stInternalTokenizer.nextToken());
				nvp.put(key.toUpperCase(), value);
			}
		}
		return nvp;
	}

	private void saveFutureSubscriptionAfterRecurringPayment(BuyerSubscription buyerSubscription, HashMap<String, String> nvp) {
		BuyerSubscription newSubscription = new BuyerSubscription();
		newSubscription.setPlan(buyerSubscription.getPlan());
		newSubscription.setPlanType(buyerSubscription.getPlanType());
		newSubscription.setPriceAmount(buyerSubscription.getPriceAmount());
		newSubscription.setPriceDiscount(buyerSubscription.getPriceDiscount());
		newSubscription.setTotalPriceAmount(buyerSubscription.getTotalPriceAmount());
		newSubscription.setAutoChargeSubscription(buyerSubscription.getAutoChargeSubscription());
		newSubscription.setPaymentProfileId(nvp.get("RECURRING_PAYMENT_ID"));
		newSubscription.setPromoCodeDiscount(buyerSubscription.getPromoCodeDiscount());
		newSubscription.setUserQuantity(buyerSubscription.getUserQuantity());
		newSubscription.setEventQuantity(buyerSubscription.getEventQuantity());
		newSubscription.setRange(buyerSubscription.getRange());
		newSubscription.setPlanPeriod(buyerSubscription.getPlanPeriod());
		Buyer buyer = buyerService.findBuyerById(buyerSubscription.getBuyer().getId());

		PaymentTransaction paymentTransaction = new PaymentTransaction();
		paymentTransaction.setCountry(buyer.getRegistrationOfCountry());
		paymentTransaction.setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
		paymentTransaction.setBuyer(buyer);
		// paymentTransaction.setPromoCode(promotionalCode);
		paymentTransaction.setCurrencyCode(buyerSubscription.getCurrencyCode());
		// paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
		paymentTransaction.setStatus(TransactionStatus.SUCCESS);
		paymentTransaction.setType(TransactionType.PAYMENT);
		paymentTransaction.setAmount(new BigDecimal(nvp.get("AMOUNT")));
		paymentTransaction.setPriceAmount(new BigDecimal(nvp.get("AMOUNT")));
		// paymentTransaction.setPriceDiscount(buyerSubscription.getPriceDiscount());
		// paymentTransaction.setPromoCodeDiscount(buyerSubscription.getPromoCodeDiscount());
		paymentTransaction.setTotalPriceAmount(new BigDecimal(nvp.get("AMOUNT")));
		paymentTransaction.setCommunicationEmail(buyer.getCommunicationEmail());
		paymentTransaction.setCompanyContactNumber(buyer.getCompanyContactNumber());
		paymentTransaction.setCompanyName(buyer.getCompanyName());
		paymentTransaction.setFullName(buyer.getFullName());
		paymentTransaction.setCreatedDate(new Date());
		paymentTransaction.setLoginEmail(buyer.getLoginEmail());
		paymentTransaction.setBuyerPlan(buyerSubscription.getPlan());
		paymentTransaction = buyerSubscriptionService.savePaymentTransaction(paymentTransaction);

		newSubscription.setCreatedDate(new Date());
		newSubscription.setCurrencyCode(buyerSubscription.getCurrencyCode());
		newSubscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
		newSubscription.setBuyer(buyer);
		newSubscription = buyerSubscriptionService.saveBuyerSubscription(newSubscription);

		// Save the TOKEN in DB
		paymentTransaction.setBuyerSubscription(newSubscription);
		// paymentTransaction.setPaymentToken(nvp.get(BuyerSubscriptionServiceImpl.TOKEN));
		paymentTransaction.setRemarks("Auto subscription " + nvp.get("TRANSACTION_SUBJECT"));
		paymentTransaction.setConfirmationDate(new Date());
		paymentTransaction.setStatus(TransactionStatus.SUCCESS);
		paymentTransaction.setReferenceTransactionId(nvp.get("TXN_ID"));
		paymentTransaction.setPaymentFee(new BigDecimal(nvp.get("AMOUNT")));

		buyerSubscription.setNextSubscription(newSubscription);
		BuyerPackage bp = buyer.getBuyerPackage();
		buyerSubscriptionService.updateSubscription(buyerSubscription);

		Calendar cal = Calendar.getInstance();
		cal.setTime(buyerSubscription.getEndDate());
		cal.set(Calendar.AM_PM, Calendar.AM);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, 1);
		Date perviousendDate = cal.getTime();
		LOG.info("perviousendDate + 1 :" + perviousendDate);
		newSubscription.setStartDate(perviousendDate);
		newSubscription.setSubscriptionStatus(SubscriptionStatus.FUTURE);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(newSubscription.getStartDate());
		endCal.set(Calendar.AM_PM, Calendar.PM);
		endCal.set(Calendar.HOUR, 11);
		endCal.set(Calendar.MINUTE, 59);
		endCal.set(Calendar.SECOND, 59);
		endCal.set(Calendar.MILLISECOND, 59);
		endCal.add(Calendar.MONTH, buyerSubscription.getPlanPeriod().getPlanDuration());
		endCal.add(Calendar.DATE, -1);
		newSubscription.setEndDate(endCal.getTime());

		buyerSubscriptionService.updateSubscription(newSubscription);

		bp.setEndDate(newSubscription.getEndDate());
		buyer.setBuyerPackage(bp);
		LOG.info("Buyer Id : " + buyer.getId());
		buyer = buyerService.updateBuyer(buyer);

		// Update transaction details with buyer info
		paymentTransaction.setBuyer(buyer);

		buyerSubscriptionService.updatePaymentTransaction(paymentTransaction);

		try {
			// send successful email and dashboard notification
			String timeZone = "GMT+8:00";
			User buyerUser = userService.getPlainUserByLoginId(buyer.getLoginEmail());
			timeZone = getTimeZoneByBuyerSettings(buyer.getId(), timeZone);
			newSubscription.setPlan(buyerSubscription.getPlan());
			sendBuyerAutoSubscriptionSuccessMail(buyerUser, newSubscription, timeZone, buyer);

			// send successful email and dashboard notification to Application Admin
			String superAdminsubsMail = ownerSettingsService.getBuyerSubsMailFromOwnersettings();
			if (StringUtils.checkString(superAdminsubsMail).length() > 0) {
				User appAdminUser = userDao.getAdminUser();
				appAdminUser.setCommunicationEmail(superAdminsubsMail);
				timeZone = "GMT+8:00";
				sendBuyerAutoSubscriptionSuccessMail(appAdminUser, newSubscription, timeZone, buyer);
			}
		} catch (Exception e) {
			LOG.error("Error while sending auto subs notification for '" + buyer.getLoginEmail() + "' :" + e.getMessage(), e);
		}
	}

	/**
	 * @param user
	 * @param subscription
	 * @param timeZone
	 * @param buyer
	 */
	private void sendBuyerAutoSubscriptionSuccessMail(User user, BuyerSubscription subscription, String timeZone, Buyer buyer) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String mailTo = user.getCommunicationEmail();
			String subject = "Auto Subscription Success";

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("loginEmail", StringUtils.checkString(buyer.getLoginEmail()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("plan", subscription.getPlan() != null ? subscription.getPlan().getPlanName() : "");
			map.put("planDesc", subscription.getPlan() != null ? subscription.getPlan().getShortDescription() : "");
			map.put("startDate", subscription.getStartDate() != null ? date.format(subscription.getStartDate()) : "");
			map.put("endDate", subscription.getEndDate() != null ? date.format(subscription.getEndDate()) : "");
			map.put("currencyCode", subscription.getCurrencyCode() != null ? subscription.getCurrencyCode() : "Not Available");
			map.put("amount", subscription.getTotalPriceAmount() != null ? subscription.getTotalPriceAmount() : "");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.BUYER_AUTO_SUBSCRIPTION_SUCCESS);
			}
			try {
				String notificationMessage = messageSource.getMessage("buyer.auto.subscription.success.notification", new Object[] {}, Global.LOCALE);
				sendDashboardNotification(user, "", subject, notificationMessage);
			} catch (Exception e) {
				LOG.error("Error While Sending Dashboard auto Subscription renew notification :" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending auto Subscription renew mail :" + e.getMessage(), e);
		}
	}

	/**
	 * @param user
	 * @param subscription
	 * @param timeZone
	 * @param buyer
	 */
	private void sendBuyerAutoSubscriptionCancelMail(User user, BuyerSubscription subscription, String timeZone, Buyer buyer) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String mailTo = user.getCommunicationEmail();
			String subject = "Auto Subscription Cancel";

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("loginEmail", StringUtils.checkString(buyer.getLoginEmail()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("plan", subscription.getPlan() != null ? subscription.getPlan().getPlanName() : "");
			map.put("planDesc", subscription.getPlan() != null ? subscription.getPlan().getShortDescription() : "");
			map.put("cancelRemarks", StringUtils.checkString(subscription.getRecurringCancelRemarks()));
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.BUYER_AUTO_SUBSCRIPTION_CANCEL);
			}
			try {
				String notificationMessage = messageSource.getMessage("buyer.auto.subscription.cancel.notification", new Object[] {}, Global.LOCALE);
				sendDashboardNotification(user, "", subject, notificationMessage);
			} catch (Exception e) {
				LOG.error("Error While Sending Dashboard auto Subscription renew notification :" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending auto Subscription renew mail :" + e.getMessage(), e);
		}
	}

	/**
	 * @param mailTo
	 * @param buyer
	 * @param subscription
	 * @param timeZone
	 */
	private void sendBuyerAutoSubscriptionDuplicatePossibleMail(String mailTo, HashMap<String, String> nvp, Buyer buyer) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String subject = "Auto Subscription duplication";

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			map.put("date", df.format(new Date()));
			map.put("loginEmail", StringUtils.checkString(buyer.getLoginEmail()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("payerEmail", StringUtils.checkString(nvp.get("PAYER_EMAIL")));
			map.put("recurringPaymentId", StringUtils.checkString(nvp.get("RECURRING_PAYMENT_ID")));
			map.put("txnId", StringUtils.checkString(nvp.get("TXN_ID")));
			notificationService.sendEmail(mailTo, subject, map, Global.BUYER_AUTO_SUBSCRIPTION_DUPLICATE);
		} catch (Exception e) {
			LOG.error("Error While Sending auto Subscription duplicate posiblitiy mail :" + e.getMessage(), e);
		}
	}

	/**
	 * @param messageTo
	 * @param url
	 * @param subject
	 * @param notificationMessage
	 */
	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
		try {
			NotificationMessage message = new NotificationMessage();
			message.setCreatedBy(null);
			message.setCreatedDate(new Date());
			message.setMessage(notificationMessage);
			message.setNotificationType(NotificationType.GENERAL);
			message.setMessageTo(messageTo);
			message.setSubject(subject);
			message.setTenantId(messageTo.getTenantId());
			message.setUrl(url);
			// dashboardNotificationService.save(message);
			notificationMessageDao.saveOrUpdate(message);
		} catch (Exception e) {
			LOG.error("Error while saving dashboard notification :" + e.getMessage(), e);
		}
	}

	/**
	 * @param user
	 * @param timeZone
	 * @return timeZone
	 */
	private String getTimeZoneByBuyerSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = buyerSettingsDao.getBuyerTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer setting time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	// @RequestMapping(path = "/freeTrialPlan", method = RequestMethod.GET)
	// public String freeTrialPlanList(Model model) {
	// List<BuyerPlan> buyerPlanList = buyerPlanService.findAllBuyerPlansByStatus(PlanStatus.ACTIVE);
	// model.addAttribute("planList", buyerPlanList);
	// return "freeTrialPlan";
	// }

	@RequestMapping(path = "/freeTrialPlan", method = RequestMethod.POST)
	public String freeTrialPlan(@ModelAttribute(name = "buyer") Buyer buyer, Model model, RedirectAttributes redirect, @RequestParam(name = "signupId", required = false) String signupId) {
		LOG.info("Free Trial Signup post called ");
		try {
			List<BuyerPlan> buyerPlanList = buyerPlanService.findAllBuyerPlansByStatus(PlanStatus.ACTIVE);
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
			model.addAttribute("planList", buyerPlanList);
			model.addAttribute("buyer", buyer);
			model.addAttribute("signupId", signupId);
		} catch (Exception e) {
			LOG.error("Error while free trial sign up with payment : " + e.getMessage(), e);
			return "redirect:freeTrialSignup";
		}
		return "freeTrialPlan";
	}

	@RequestMapping(path = "/freeTrialSignup", method = RequestMethod.GET)
	public String freeTrialSignup(@RequestParam(name = "u", required = false) String userName, @RequestParam(name = "e", required = false) String email, @RequestParam(name = "id", required = false) String id, Model model) {
		LOG.info("Free Trial Signup get called user name: " + userName + "  user email address: " + email);
		try {
			Buyer buyer = new Buyer();
			buyer.setFullName(userName);
			buyer.setCommunicationEmail(email);
			buyer.setLoginEmail(email);
			model.addAttribute("buyer", buyer);
			buyer.setRegistrationOfCountry(countryService.getCountryByCode("MY"));
			model.addAttribute("countryList", countryService.getAllCountries());
			model.addAttribute("signupId", id);
			String companyRegistrationNumber = "DUMMY-" + UUID.randomUUID().toString().replace("-", "");
			buyer.setCompanyRegistrationNumber(companyRegistrationNumber);

		} catch (Exception e) {
			LOG.error("Error while free trial sign up: " + e.getMessage(), e);
		}
		return "freeTrialSignup";
	}

	@RequestMapping(path = "/freeTrialSignup", method = RequestMethod.POST)
	public String freeTrialSignup(@ModelAttribute(name = "buyer") Buyer buyer, Model model, RedirectAttributes redirect, @RequestParam(name = "signupId", required = false) String signupId) {
		LOG.info("Free Trial Signup post called ");
		try {
			User user = buyerSubscriptionService.saveTrialBuyer(buyer);
			buyerService.sendTrialBuyerCreationMail(user.getBuyer(), user);
			Owner owner = buyerService.getDefaultOwner();
			try {
				uomService.loadDefaultUomIntoBuyerAccount(user.getBuyer(), owner);
			} catch (Exception e) {
				LOG.error("Error loading default UOM for buyer : " + e.getMessage(), e);
			}
			if (StringUtils.checkString(signupId).length() > 0) {
				try {
					FreeTrialEnquiry freeTrialInquiry = freeTrialInquiryService.findById(signupId);
					freeTrialInquiry.setSignupDate(new Date());
					freeTrialInquiryService.updateFreeTrialEnquiry(freeTrialInquiry);
					LOG.info(">>>> Updated Signup complete for ID : " + signupId);
				} catch (Exception e) {
					LOG.error("Did not find reference signup id.... " + e.getMessage(), e);
				}
			}

		} catch (Exception e) {
			LOG.error("Error while free trial sign up: " + e.getMessage(), e);
			return "redirect:freeTrialSignup";
		}
		return "redirect:freeTrialThankyou";
	}

	@RequestMapping(value = { "/freeTrialThankyou" }, method = RequestMethod.GET)
	public String freeTrialThankyouMethod() {
		LOG.info("Thankyou successfully");
		return "freeTrialThankyou";
	}

	@RequestMapping(path = "/getUserBasedPrice", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> getUserBasedPrice(@RequestParam(name = "planId", required = false) String planId, @RequestParam(name = "noOfUser") Integer noOfUser, @RequestParam(name = "promoCode") String promoCode, @RequestParam(name = "periodId") String periodId, @RequestParam(name = "countryId") String countryId) throws JsonProcessingException {
		LOG.info("getUserBasedPrice called ...noOfUser: " + noOfUser);
		LOG.info("planId: " + planId);
		LOG.info("promoCode: " + promoCode);
		LOG.info("periodId: " + periodId);
		Map<String, Object> result = new HashMap<>();
		try {
			BuyerPlan plan = getBuyerPlan(planId);
			if (plan != null) {
				BigDecimal totalPrice = BigDecimal.ZERO;
				BigDecimal basePrice = getBasePrice(plan, periodId);
				BigDecimal addtionalUserPrice = getAdditionalUserprice(noOfUser, plan, basePrice, periodId, result);
				BigDecimal totalUserPrice = addtionalUserPrice.add(basePrice).setScale(2, BigDecimal.ROUND_HALF_UP);
				LOG.info("totalUserPrice: " + totalUserPrice);
				BigDecimal tax = BigDecimal.ZERO;

				BigDecimal subscriptionDiscountPrice = BigDecimal.ZERO;
				subscriptionDiscountPrice = getSubscriptionDiscountPrice(periodId, plan, totalUserPrice);

				if (StringUtils.isNotBlank(countryId)) {
					Country country = countryService.getCountryById(countryId);
					if (country == null) {
						country = countryService.getCountryByCode(countryId);
					}
					if (country != null) {
						if ("MY".equals(country.getCountryCode())) {
							tax = totalUserPrice.subtract(subscriptionDiscountPrice).multiply((plan.getTax().divide(new BigDecimal(100))));
							result.put("tax", plan.getTax());
						} else {
							result.put("tax", 0);
						}
					}
				} else {
					result.put("tax", plan.getTax());
				}
				totalPrice = totalUserPrice;
				BigDecimal promoDiscountPrice = BigDecimal.ZERO;
				totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
				Integer planDuration = getSubscriptionDuration(plan, periodId);

				result.put("planDuration", planDuration);
				result.put("taxAmount", tax);
				result.put("basePrice", basePrice);
				result.put("addtionalUserPrice", addtionalUserPrice);
				result.put("subscriptionDiscountPrice", subscriptionDiscountPrice);
				result.put("promoDiscountPrice", promoDiscountPrice);
				result.put("totalPrice", totalPrice);

				try {
					promoDiscountPrice = getPromoDiscount(promoCode, totalUserPrice, result, plan);
					result.put("promoDiscountPrice", promoDiscountPrice);
					totalPrice = totalPrice.subtract(promoDiscountPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
					result.put("totalPrice", totalPrice);

				} catch (ApplicationException e) {
					result.put("error", e.getMessage());
					LOG.error(e.getMessage());
					return new ResponseEntity<Map<String, Object>>(result, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				LOG.error("Plan is null");
			}
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while calculate User Based Price : " + e.getMessage(), e);
			return new ResponseEntity<Map<String, Object>>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @param promoCode
	 * @param totalPrice
	 * @param result TODO
	 * @throws Exception
	 */
	public BigDecimal getPromoDiscount(String promoCode, BigDecimal totalPrice, Map<String, Object> result, BuyerPlan plan) throws Exception {
		BigDecimal promoDiscountPrice = BigDecimal.ZERO;
		if (StringUtils.checkString(promoCode).length() > 0) {
			PromotionalCode promo = promotionalCodeService.getPromotionalCodeByPromoCode(promoCode);
			promotionalCodeService.validatePromoCode(promo.getPromoCode(), null, plan, totalPrice, TenantType.BUYER, null);
			result.put("promoCodeId", promo.getId());
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

		}
		return promoDiscountPrice;
	}

	/**
	 * @param periodId
	 * @param plan
	 * @param totalUserPrice
	 */
	public BigDecimal getSubscriptionDiscountPrice(String periodId, BuyerPlan plan, BigDecimal totalUserPrice) {
		BigDecimal subscriptionDiscountPrice = BigDecimal.ZERO;
		if (StringUtils.checkString(periodId).length() > 0) {
			if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
				for (PlanPeriod period : plan.getPlanPeriodList()) {
					if (period.getId().equalsIgnoreCase(periodId)) {
						LOG.info("Period Id: " + period.getId());
						Integer discountPer = period.getPlanDiscount();
						if (discountPer != null) {
							subscriptionDiscountPrice = totalUserPrice.multiply(new BigDecimal(discountPer)).setScale(2, BigDecimal.ROUND_HALF_UP);
							subscriptionDiscountPrice = subscriptionDiscountPrice.divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
						}
						LOG.info("subscriptionDiscountPrice: " + subscriptionDiscountPrice);
						break;
					}

				}
			}
		}
		return subscriptionDiscountPrice;
	}

	/**
	 * @param noOfUser
	 * @param plan
	 * @param basePrice
	 * @param result TODO
	 * @return
	 */
	public BigDecimal getAdditionalUserprice(Integer noOfUser, BuyerPlan plan, BigDecimal basePrice, String periodId, Map<String, Object> result) {
		BigDecimal addtionalUserPrice = BigDecimal.ZERO;
		if (plan.getBaseUsers() != null) {
			if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
				for (PlanRange range : plan.getRangeList()) {

					if ((noOfUser + plan.getBaseUsers()) >= range.getRangeStart() && (noOfUser + plan.getBaseUsers()) <= range.getRangeEnd()) {
						addtionalUserPrice = range.getPrice();
						LOG.info("range Id: " + range.getId() + " ==== addtionalUserPrice: " + addtionalUserPrice);
						result.put("rangeId", range.getId());
						break;
					} else if (1 >= range.getRangeStart() && plan.getBaseUsers() <= range.getRangeEnd()) {
						result.put("rangeId", range.getId());
					}
				}
			}
			// noOfUser = noOfUser - plan.getBaseUsers();
			LOG.info("noOfUser : " + noOfUser);
			addtionalUserPrice = addtionalUserPrice.multiply(new BigDecimal(noOfUser)).setScale(2, BigDecimal.ROUND_HALF_UP);
			Integer duration = getSubscriptionDuration(plan, periodId);
			addtionalUserPrice = addtionalUserPrice.multiply(new BigDecimal(duration)).setScale(2, BigDecimal.ROUND_HALF_UP);

			LOG.info("addtionalUserPrice: " + addtionalUserPrice);
		}
		return addtionalUserPrice;
	}

	/**
	 * @param planId
	 */
	public BuyerPlan getBuyerPlan(String planId) {
		if (StringUtils.checkString(planId).length() > 0) {
			BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
			return plan;
		} else {
			LOG.error("Plan Id is null");
			return null;
		}
	}

	/**
	 * @param plan
	 */
	public BigDecimal getBasePrice(BuyerPlan plan, String periodId) {
		BigDecimal basePrice = BigDecimal.ZERO;
		if (plan.getBasePrice() != null) {
			basePrice = plan.getBasePrice().setScale(2, BigDecimal.ROUND_HALF_UP);
			Integer duration = getSubscriptionDuration(plan, periodId);
			basePrice = basePrice.multiply(new BigDecimal(duration)).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		return basePrice;
	}

	/**
	 * @param plan
	 * @param periodId
	 */
	public Integer getSubscriptionDuration(BuyerPlan plan, String periodId) {
		Integer duration = 0;
		if (StringUtils.checkString(periodId).length() > 0) {
			if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
				for (PlanPeriod period : plan.getPlanPeriodList()) {
					if (period.getId().equalsIgnoreCase(periodId)) {
						duration = period.getPlanDuration();
						LOG.info("Plan Duration: " + duration);
						break;
					}

				}
			}
		}
		return duration;
	}

	@RequestMapping(path = "/doInitiateTrialPayment", method = RequestMethod.POST)
	public String doInitiateTrialPayment(@ModelAttribute(name = "buyer") Buyer buyer, @RequestParam("numberUserEvent") String numberUserEvent, @RequestParam(required = false, value = "periodId") String periodId, @RequestParam(value = "rangeId") String rangeId, @RequestParam(required = false, value = "promoCodeId") String promoCodeId, @RequestParam(required = false, value = "basePrice") BigDecimal basePrice, @RequestParam(required = false, value = "addtionalUserPrice") BigDecimal addtionalUserPrice, @RequestParam(required = false, value = "eventPrice") BigDecimal eventPrice, @RequestParam(required = false, value = "subscriptionDiscountPrice") BigDecimal subscriptionDiscountPrice, @RequestParam(required = false, value = "promoDiscountPrice") BigDecimal promoDiscountPrice, @RequestParam(value = "totalPrice") BigDecimal totalPrice, @RequestParam(name = "planId") String planId, @RequestParam(name = "countryId") String countryId, @RequestParam(name = "signupId", required = false) String signupId, Model model, HttpSession session) {
		LOG.info("User requested to purchase Plan : " + planId + ", Range Id : " + rangeId + ", NumberUser : " + numberUserEvent + ", periodId : " + periodId + ", promoCodeId : " + promoCodeId + ", Base Price : " + basePrice);
		try {
			if (StringUtils.checkString(planId).length() > 0) {
				String returnURL = appUrl + "/buyerSubscription/doPaymentConfirm?type=Order&planId=" + planId + "&signupId=" + signupId + "&txId=";
				String cancelURL = appUrl + "/buyerSubscription/cancel?planId=" + planId + "&signupId=" + signupId + "&txId=";

				if (StringUtils.checkString(numberUserEvent).length() == 0) {
					numberUserEvent = "0";
				}

				int baseUsers = 3;

				LOG.info("buyer country: " + countryId);
				Country country = new Country();
				country.setId(countryId);
				buyer.setRegistrationOfCountry(country);

				BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
				BuyerSubscription newSubscription = new BuyerSubscription();
				newSubscription.setPlan(plan);
				newSubscription.setPlanType(plan.getPlanType());
				BigDecimal priceAmount = BigDecimal.ZERO;
				if (newSubscription.getPlanType() == PlanType.PER_USER) {
					priceAmount = basePrice.add(addtionalUserPrice);
				} else if (newSubscription.getPlanType() == PlanType.PER_EVENT) {
					priceAmount = eventPrice;
				}

				newSubscription.setPriceAmount(priceAmount);
				newSubscription.setPriceDiscount(subscriptionDiscountPrice);
				newSubscription.setTotalPriceAmount(totalPrice);

				if (promoDiscountPrice != null) {
					newSubscription.setPromoCodeDiscount(promoDiscountPrice);
				} else {
					newSubscription.setPromoCodeDiscount(new BigDecimal(0));
				}

				if (newSubscription.getPlanType() == PlanType.PER_USER) {
					newSubscription.setUserQuantity(new Integer(numberUserEvent) + baseUsers);
					newSubscription.setEventQuantity(9999); // set default 9999 max event on user based
				} else if (newSubscription.getPlanType() == PlanType.PER_EVENT) {
					newSubscription.setEventQuantity(new Integer(numberUserEvent));
					newSubscription.setUserQuantity(999); // set default 999 max user on event based
				}
				PlanRange range = null;
				if (CollectionUtil.isNotEmpty(plan.getRangeList())) {

					// User did not change the range. Then default it to the first one.
					if (StringUtils.checkString(rangeId).length() == 0) {
						range = plan.getRangeList().get(0);
					} else {
						for (PlanRange pRange : plan.getRangeList()) {
							if (pRange.getId().equals(rangeId)) {
								range = pRange;
							}
						}
					}
				}
				newSubscription.setRange(range);

				PlanPeriod period = null;
				if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
					for (PlanPeriod prd : plan.getPlanPeriodList()) {
						if (prd.getId().equals(periodId)) {
							period = prd;
						}
					}
				}

				newSubscription.setPlanPeriod(period);

				newSubscription.setBuyer(buyer);

				PaymentTransaction paymentTransaction = new PaymentTransaction();

				if (StringUtils.checkString(promoCodeId).length() > 0) {
					PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
					// cross checking for promocode
					try {
						promotionalCodeService.getPromotionalCodeByPromoCode(promotionalCode.getPromoCode());
					} catch (Exception e) {
						throw e;
					}
					paymentTransaction.setPromoCode(promotionalCode);
				}
				paymentTransaction.setCompanyName(buyer.getCompanyName());
				paymentTransaction.setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
				paymentTransaction.setFullName(buyer.getFullName());
				paymentTransaction.setCompanyContactNumber(buyer.getCompanyContactNumber());
				paymentTransaction.setLoginEmail(buyer.getLoginEmail());
				paymentTransaction.setCommunicationEmail(buyer.getCommunicationEmail());
				paymentTransaction.setCountry(buyer.getRegistrationOfCountry());

				newSubscription.setPaymentTransactions(Arrays.asList(paymentTransaction));

				return buyerSubscriptionService.initiatePaypalPaymentForBuyerTrialSubscription(newSubscription, session, returnURL, cancelURL);
			} else {
				LOG.error("Plan is null");
				return "redirect:/buyerSubscription/selectPlan";
			}
		} catch (Exception e) {
			LOG.error("Error while payment : " + e.getMessage(), e);
			return "redirect:/buyerSubscription/freeTrialSignup?u=" + buyer.getFullName() + "&e=" + buyer.getCommunicationEmail() + "&id=" + signupId;
		}
	}

	@RequestMapping(path = "/doPaymentConfirm", method = RequestMethod.GET)
	public String doInitiateTrialConfirm(@RequestParam(name = "type", required = false) String type, @RequestParam(name = "signupId", required = false) String signupId, @RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, @RequestParam(name = "token") String token, @RequestParam(name = "PayerID") String payerID, Model model, HttpServletRequest request, HttpSession session) {
		boolean isSale = true;
		if (StringUtils.checkString(type).equalsIgnoreCase("order")) {
			isSale = false;
		}

		try {

			String subscriptionId = (String) session.getAttribute("subscriptionId");
			LOG.info("Subscription Id : " + subscriptionId);
			BuyerSubscription subscription = buyerSubscriptionService.getBuyerSubscriptionById(subscriptionId);
			if (subscription != null) {
				Buyer buyer = (Buyer) session.getAttribute("buyer");
				subscription.setBuyer(buyer);
			}
			model.addAttribute("subscription", subscription);

			PaymentTransaction paymentTransaction = buyerSubscriptionService.getPaymentTransactionById(txId);
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
			HashMap<String, String> nvp = buyerSubscriptionService.invokePaypalService("GetExpressCheckoutDetails", nvpstr);
			String strAck = nvp.get("ACK").toString();
			if (strAck != null && (strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS) || strAck.equalsIgnoreCase("SuccessWithWarning"))) {
				session.setAttribute("PAYERID", nvp.get("PAYERID").toString());
			}

			LOG.info("Payment Checkout completed...!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

			String serverName = request.getServerName();
			LOG.info("Server Name :" + serverName);
			User user = buyerSubscriptionService.confirmSubscription(token, nvp.get("PAYERID").toString(), model, serverName, txId, isSale);

			try {
				buyerService.sentBuyerCreationMail(user.getBuyer(), user);
			} catch (Exception e) {
				LOG.error("Error Sending Buyer Creation Email : " + e.getMessage(), e);
			}
			if (StringUtils.checkString(signupId).length() > 0) {
				try {
					FreeTrialEnquiry freeTrialInquiry = freeTrialInquiryService.findById(signupId);
					freeTrialInquiry.setSignupDate(new Date());
					freeTrialInquiryService.updateFreeTrialEnquiry(freeTrialInquiry);
					LOG.info(">>>> Updated Signup complete for ID : " + signupId);
				} catch (Exception e) {
					LOG.error("Did not find reference signup id.... " + e.getMessage(), e);
				}
			}
			Owner owner = buyerService.getDefaultOwner();
			try {
				uomService.loadDefaultUomIntoBuyerAccount(user.getBuyer(), owner);
			} catch (Exception e) {
				LOG.error("Error loading default UOM for buyer : " + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error while trial confirm : " + e.getMessage(), e);
		}
		if (isSale) {
			return "redirect:buyerSubscriptionThankyou";
		} else {
			return "redirect:freeTrialThankyou";
		}
	}

	@RequestMapping(path = "/userBasedBuyerCheckout", method = RequestMethod.GET)
	public String buyerCheckout(Model model, RedirectAttributes redirect, @RequestParam(name = "payment_intent", required = false) String paymentStatus) {
		LOG.info("User base Buyer Checkout called ");
		try {

			if (StringUtils.checkString(paymentStatus).length() > 0) {
				try {
					String msg = stripeBuyerSubscriptionService.getPaymentStatus(paymentStatus);
					LOG.info(msg);
					if (msg.indexOf("Processing") != -1) {
						model.addAttribute("info", msg);
					} else {
						return "buyerSubscriptionThankyou";
					}
				} catch (Exception e) {
					model.addAttribute("error", e.getMessage());
					LOG.info("Error in checking payment status " + e.getMessage());
				}
			}

			BuyerPlan buyerPlan = buyerPlanService.findUserBasedBuyerPlansByStatus();
			Buyer buyer = new Buyer();
			buyer.setRegistrationOfCountry(countryService.getCountryByCode("MY"));
			BuyerSubscription subscription = new BuyerSubscription();
			subscription.setBuyer(buyer);
			subscription.setPlan(buyerPlan);
			model.addAttribute("buyer", subscription);
			model.addAttribute("countryList", countryService.getAllCountries());
			model.addAttribute("buyerPlan", buyerPlan);
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
			model.addAttribute("publicKey", stripePublicKey);

		} catch (Exception e) {
			LOG.error("Error while buyer checkout : " + e.getMessage(), e);
			return "redirect:userBasedBuyerCheckout";
		}
		return "userBasedBuyerCheckout";
	}

	// @RequestMapping(path = "/userBasedBuyerCheckout", method = RequestMethod.POST)
	// public String buyerUserBaseCheckout(@ModelAttribute(name = "buyer") BuyerSubscription buyer,
	// @RequestParam(required = false, value = "periodId") String periodId, @RequestParam("promoCodeId") String
	// promoCode, @RequestParam(value = "basePrice") BigDecimal basePrice, @RequestParam(value = "addtionalUserPrice")
	// BigDecimal addtionalUserPrice, @RequestParam(required = false, value = "subscriptionDiscountPrice") BigDecimal
	// subscriptionDiscountPrice, @RequestParam(required = false, value = "promoDiscountPrice") BigDecimal
	// promoDiscountPrice, @RequestParam(value = "totalPrice") BigDecimal totalPrice, HttpSession session,
	// RedirectAttributes redirectAttributes) {
	// LOG.info(" promoCode : " + promoCode + " basePrice " + basePrice + " addtionalUserPrice : " + addtionalUserPrice
	// + " subscriptionDiscountPrice : " + subscriptionDiscountPrice + " promoDiscountPrice : " + promoDiscountPrice + "
	// totalPrice " + totalPrice);
	// LOG.info("User base Buyer Checkout submitted : >>>>> " + buyer.getBuyer().getLoginEmail() + " UserQuantity " +
	// buyer.getUserQuantity());
	// try {
	// Map<String, Object> result = new HashMap<>();
	// String returnURL = appUrl + "/buyerSubscription/doPaymentConfirm?type=Sale&planId=" + buyer.getPlan().getId() +
	// "&txId=";
	// String cancelURL = appUrl + "/buyerSubscription/userBasedBuyerCheckout?cancelled=true";
	//
	// BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(buyer.getPlan().getId());
	// totalPrice = BigDecimal.ZERO;
	// basePrice = BigDecimal.ZERO;
	// addtionalUserPrice = BigDecimal.ZERO;
	// subscriptionDiscountPrice = BigDecimal.ZERO;
	//
	// basePrice = getBasePrice(plan, periodId);
	//
	// if (buyer.getUserQuantity() != null) {
	// if (buyer.getUserQuantity() < 0) {
	// throw new ApplicationException("User quantity not be less than zero");
	// }
	// addtionalUserPrice = getAdditionalUserprice(buyer.getUserQuantity(), plan, basePrice, periodId, result);
	// BigDecimal totalUserPrice = addtionalUserPrice.add(basePrice).setScale(2, BigDecimal.ROUND_HALF_UP);
	// totalPrice = totalUserPrice;
	// subscriptionDiscountPrice = getSubscriptionDiscountPrice(periodId, plan, totalUserPrice);
	// LOG.info("totalUserPrice: " + totalUserPrice);
	// }
	// promoDiscountPrice = BigDecimal.ZERO;
	// promoDiscountPrice = getPromoDiscount(promoCode, totalPrice, result);
	// totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
	//
	// LOG.info("after calculation basePrice:" + basePrice + " addtionalUserPrice: " + addtionalUserPrice +
	// "subscriptionDiscountPrice:" + subscriptionDiscountPrice + " promoDiscountPrice : " + promoDiscountPrice + "
	// totalPrice " + totalPrice);
	// LOG.info("User base Buyer Checkout submitted : >>>>> " + buyer.getBuyer().getLoginEmail() + " UserQuantity " +
	// buyer.getUserQuantity());
	//
	// buyer.setPlan(plan);
	// buyer.setPlanType(plan.getPlanType());
	// BigDecimal priceAmount = basePrice.add(addtionalUserPrice);
	// buyer.setPriceAmount(priceAmount);
	// buyer.setPriceDiscount(subscriptionDiscountPrice);
	// buyer.setTotalPriceAmount(totalPrice);
	//
	// if (promoDiscountPrice != null) {
	// buyer.setPromoCodeDiscount(promoDiscountPrice);
	// } else {
	// buyer.setPromoCodeDiscount(new BigDecimal(0));
	// }
	//
	// if (buyer.getUserQuantity() == null || (buyer.getUserQuantity() != null && buyer.getUserQuantity() == 0)) {
	// buyer.setUserQuantity(3);
	// buyer.setEventQuantity(9999); // set default 9999 max event on user based
	// } else {
	// buyer.setUserQuantity(plan.getBaseUsers() + buyer.getUserQuantity());
	// buyer.setEventQuantity(9999); // set default 9999 max event on user based
	// }
	//
	// PlanRange range = null;
	// if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
	// // User did not change the range. Then default it to the first one.
	// if (StringUtils.checkString(buyer.getRange().getId()).length() == 0) {
	// range = plan.getRangeList().get(0);
	// } else {
	// for (PlanRange pRange : plan.getRangeList()) {
	// if (pRange.getId().equals(buyer.getRange().getId())) {
	// range = pRange;
	// }
	// }
	// }
	// }
	// buyer.setRange(range);
	//
	// PlanPeriod period = null;
	// if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
	// for (PlanPeriod prd : plan.getPlanPeriodList()) {
	// if (prd.getId().equals(periodId)) {
	// period = prd;
	// }
	// }
	// }
	// buyer.setPlanPeriod(period);
	//
	// PaymentTransaction paymentTransaction = new PaymentTransaction();
	//
	// if (StringUtils.checkString(promoCode).length() > 0) {
	// PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCode);
	// // cross checking for promocode
	// try {
	// promotionalCodeService.getPromotionalCodeByPromoCode(promotionalCode.getPromoCode());
	// } catch (Exception e) {
	// throw e;
	// }
	// paymentTransaction.setPromoCode(promotionalCode);
	// }
	// paymentTransaction.setCompanyName(buyer.getBuyer().getCompanyName());
	// paymentTransaction.setCompanyRegistrationNumber(buyer.getBuyer().getCompanyRegistrationNumber());
	// paymentTransaction.setFullName(buyer.getBuyer().getFullName());
	// paymentTransaction.setCompanyContactNumber(buyer.getBuyer().getCompanyContactNumber());
	// paymentTransaction.setLoginEmail(buyer.getBuyer().getLoginEmail());
	// paymentTransaction.setCommunicationEmail(buyer.getBuyer().getCommunicationEmail());
	// paymentTransaction.setCountry(buyer.getBuyer().getRegistrationOfCountry());
	//
	// buyer.setPaymentTransactions(Arrays.asList(paymentTransaction));
	//
	// return buyerSubscriptionService.initiatePaypalPaymentForBuyerSubscription(buyer, plan.getId(), session,
	// returnURL, cancelURL);
	// } catch (Exception e) {
	// LOG.error("Error while payment : " + e.getMessage(), e);
	// return "redirect:/buyerSubscription/userBasedBuyerCheckout";
	// }
	// }

	@RequestMapping(value = { "/buyerSubscriptionThankyou" }, method = RequestMethod.GET)
	public String buyerSubscriptionThankyou() {
		return "buyerSubscriptionThankyou";
	}

	@RequestMapping(path = "/eventBasedBuyerCheckout", method = RequestMethod.GET)
	public String buyerEventCheckout(Model model, RedirectAttributes redirect, @RequestParam(name = "payment_intent", required = false) String paymentStatus) {
		LOG.info("Event base Buyer Checkout called ");
		try {

			if (StringUtils.checkString(paymentStatus).length() > 0) {
				try {
					String msg = stripeBuyerSubscriptionService.getPaymentStatus(paymentStatus);
					LOG.info(msg);
					if (msg.indexOf("Processing") != -1) {
						model.addAttribute("info", msg);
					} else {
						return "buyerSubscriptionThankyou";
					}
				} catch (Exception e) {
					LOG.info("Error in checking payment status " + e.getMessage());
				}
			}

			BuyerPlan buyerPlan = buyerPlanService.findEventBasedBuyerPlansByStatus();
			Buyer buyer = new Buyer();
			buyer.setRegistrationOfCountry(countryService.getCountryByCode("MY"));
			BuyerSubscription subscription = new BuyerSubscription();
			subscription.setBuyer(buyer);
			subscription.setPlan(buyerPlan);
			model.addAttribute("buyer", subscription);
			model.addAttribute("countryList", countryService.getAllCountries());
			model.addAttribute("buyerPlan", buyerPlan);
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
			model.addAttribute("publicKey", stripePublicKey);

		} catch (Exception e) {
			LOG.error("Error while buyer checkout : " + e.getMessage(), e);
			return "redirect:eventBasedBuyerCheckout";
		}
		return "eventBasedBuyerCheckout";
	}

	// @RequestMapping(path = "/eventBasedBuyerCheckout", method = RequestMethod.POST)
	// public String buyerEventBaseCheckout(@ModelAttribute(name = "buyer") BuyerSubscription buyer,
	// @RequestParam(required = false, value = "periodId") String periodId, @RequestParam("promoCodeId") String
	// promoCode, @RequestParam(required = false, value = "basePrice") BigDecimal basePrice, @RequestParam(required =
	// false, value = "addtionalUserPrice") BigDecimal addtionalUserPrice, @RequestParam(required = false, value =
	// "subscriptionDiscountPrice") BigDecimal subscriptionDiscountPrice, @RequestParam(required = false, value =
	// "promoDiscountPrice") BigDecimal promoDiscountPrice, @RequestParam(required = false, value = "totalPrice")
	// BigDecimal totalPrice, HttpSession session) {
	// try {
	// LOG.info(" promoCode : " + promoCode + " basePrice " + basePrice + " addtionalUserPrice : " + addtionalUserPrice
	// + " subscriptionDiscountPrice : " + subscriptionDiscountPrice + " promoDiscountPrice : " + promoDiscountPrice + "
	// totalPrice " + totalPrice);
	// LOG.info("Event base Buyer Checkout submitted : >>>>> " + buyer.getBuyer().getLoginEmail() + " UserQuantity " +
	// buyer.getUserQuantity());
	// String returnURL = appUrl + "/buyerSubscription/doPaymentConfirm?type=Sale&planId=" + buyer.getPlan().getId() +
	// "&txId=";
	// String cancelURL = appUrl + "/buyerSubscription/eventBasedBuyerCheckout?cancelled=true";
	// Map<String, Object> result = new HashMap<>();
	// BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(buyer.getPlan().getId());
	// buyer.setPlan(plan);
	// buyer.setPlanType(plan.getPlanType());
	//
	// if (promoDiscountPrice != null) {
	// buyer.setPromoCodeDiscount(promoDiscountPrice);
	// } else {
	// buyer.setPromoCodeDiscount(new BigDecimal(0));
	// }
	//
	// if (buyer.getUserQuantity() == null || (buyer.getUserQuantity() != null && buyer.getUserQuantity() == 0)) {
	// buyer.setEventQuantity(1); // set default 9999 max event on user based
	// } else {
	// buyer.setEventQuantity(buyer.getUserQuantity());
	// }
	// buyer.setUserQuantity(9999);
	//
	// PlanRange range = null;
	// if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
	// // User did not change the range. Then default it to the first one.
	// if (StringUtils.checkString(buyer.getRange().getId()).length() == 0) {
	// range = plan.getRangeList().get(0);
	// } else {
	// for (PlanRange pRange : plan.getRangeList()) {
	// if (pRange.getId().equals(buyer.getRange().getId())) {
	// range = pRange;
	// }
	// }
	// }
	// }
	// buyer.setRange(range);
	// totalPrice = range.getPrice().multiply(new BigDecimal(buyer.getEventQuantity()));
	// buyer.setPriceAmount(range.getPrice().multiply(new BigDecimal(buyer.getEventQuantity())));
	// // if (subscriptionDiscountPrice == null) {
	// subscriptionDiscountPrice = BigDecimal.ZERO;
	// // }
	// subscriptionDiscountPrice = getPromoDiscount(promoCode, totalPrice, result);
	// buyer.setPriceDiscount(subscriptionDiscountPrice);
	//
	// if (totalPrice == null) {
	// totalPrice = buyer.getPriceAmount().subtract(subscriptionDiscountPrice);
	// }
	// buyer.setTotalPriceAmount(totalPrice);
	//
	// PlanPeriod period = null;
	// if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
	// for (PlanPeriod prd : plan.getPlanPeriodList()) {
	// if (prd.getId().equals(periodId)) {
	// period = prd;
	// }
	// }
	// }
	// buyer.setPlanPeriod(period);
	//
	// LOG.info("after promoCode : " + promoCode + " basePrice " + basePrice + " addtionalUserPrice : " +
	// addtionalUserPrice + " subscriptionDiscountPrice : " + subscriptionDiscountPrice + " promoDiscountPrice : " +
	// promoDiscountPrice + " totalPrice " + totalPrice);
	// LOG.info("after Event base Buyer Checkout submitted : >>>>> " + buyer.getBuyer().getLoginEmail() + " UserQuantity
	// " + buyer.getUserQuantity());
	//
	// PaymentTransaction paymentTransaction = new PaymentTransaction();
	//
	// if (StringUtils.checkString(promoCode).length() > 0 && !StringUtils.checkString(promoCode).equals(",")) {
	// PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCode);
	// // cross checking for promocode
	// try {
	// promotionalCodeService.getPromotionalCodeByPromoCode(promotionalCode.getPromoCode());
	// } catch (Exception e) {
	// throw e;
	// }
	// paymentTransaction.setPromoCode(promotionalCode);
	// }
	// paymentTransaction.setCompanyName(buyer.getBuyer().getCompanyName());
	// paymentTransaction.setCompanyRegistrationNumber(buyer.getBuyer().getCompanyRegistrationNumber());
	// paymentTransaction.setFullName(buyer.getBuyer().getFullName());
	// paymentTransaction.setCompanyContactNumber(buyer.getBuyer().getCompanyContactNumber());
	// paymentTransaction.setLoginEmail(buyer.getBuyer().getLoginEmail());
	// paymentTransaction.setCommunicationEmail(buyer.getBuyer().getCommunicationEmail());
	// paymentTransaction.setCountry(buyer.getBuyer().getRegistrationOfCountry());
	//
	// buyer.setPaymentTransactions(Arrays.asList(paymentTransaction));
	//
	// return buyerSubscriptionService.initiatePaypalPaymentForBuyerSubscription(buyer, plan.getId(), session,
	// returnURL, cancelURL);
	// } catch (Exception e) {
	// LOG.error("Error while payment : " + e.getMessage(), e);
	// return "redirect:/buyerSubscription/eventBasedBuyerCheckout";
	// }
	// }

	@RequestMapping(path = "/getEventBasedPrice", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> getEventBasedPrice(@RequestParam(name = "planId") String planId, @RequestParam(name = "noOfEvent") Integer noOfEvent, @RequestParam(name = "promoCode", required = false) String promoCode, @RequestParam(name = "countryId") String countryId) throws JsonProcessingException {
		LOG.info("getEventBasedPrice called ...noOfEvent: " + noOfEvent);
		LOG.info("planId: " + planId);
		LOG.info("promoCode: " + promoCode);
		Map<String, Object> result = new HashMap<>();
		BuyerPlan plan = null;
		try {
			plan = getBuyerPlan(planId);
			if (plan != null) {
				BigDecimal tax = BigDecimal.ZERO;
				BigDecimal eventPrice = getEventprice(noOfEvent, plan, result);

				if (StringUtils.isNotBlank(countryId)) {
					Country country = countryService.getCountryById(countryId);
					if (country == null) {
						country = countryService.getCountryByCode(countryId);
					}
					if (country != null) {
						if ("MY".equals(country.getCountryCode())) {
							tax = eventPrice.multiply((plan.getTax().divide(new BigDecimal(100))));
							result.put("tax", plan.getTax());
						} else {
							result.put("tax", 0);
						}
					}
				} else {
					result.put("tax", plan.getTax());
				}

				BigDecimal totalPrice = eventPrice;
				BigDecimal promoDiscountPrice = BigDecimal.ZERO;
				result.put("taxAmount", tax);
				result.put("eventPrice", eventPrice);
				result.put("promoDiscountPrice", promoDiscountPrice);
				result.put("totalPrice", totalPrice);
				result.put("noOfEvent", noOfEvent);
				try {
					promoDiscountPrice = getPromoDiscount(promoCode, totalPrice, result, plan);
					result.put("promoDiscountPrice", promoDiscountPrice);
					totalPrice = totalPrice.subtract(promoDiscountPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
					result.put("totalPrice", totalPrice);

				} catch (ApplicationException e) {
					result.put("error", e.getMessage());
					LOG.error(e.getMessage());
					return new ResponseEntity<Map<String, Object>>(result, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				LOG.error("Plan is null");
			}
			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while calculate Event Based Price : " + e.getMessage(), e);
			return new ResponseEntity<Map<String, Object>>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private BigDecimal getEventprice(Integer noOfEvent, BuyerPlan plan, Map<String, Object> result) {
		BigDecimal eventsPrice = BigDecimal.ZERO;
		if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
			PlanRange range = plan.getRangeList().get(0);
			eventsPrice = range.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP);
			eventsPrice = eventsPrice.multiply(new BigDecimal(noOfEvent)).setScale(2, BigDecimal.ROUND_HALF_UP);
			result.put("rangeId", range.getId());
		}
		return eventsPrice;
	}

	@RequestMapping(value = "/userBasedBuyerCheckout", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PaymentIntentPojo> buyerUserBaseCheckout(@RequestBody BuyerSubscriptionStripeRequestPojo buyerSubscriptionPojo, @RequestParam(required = false, value = "periodId") String periodId, @RequestParam("promoCodeId") String promoCode, @RequestParam(value = "basePrice") BigDecimal basePrice, @RequestParam(value = "addtionalUserPrice") BigDecimal addtionalUserPrice, @RequestParam(required = false, value = "subscriptionDiscountPrice") BigDecimal subscriptionDiscountPrice, @RequestParam(required = false, value = "promoDiscountPrice") BigDecimal promoDiscountPrice, @RequestParam(value = "totalPrice") BigDecimal totalPrice, @RequestParam(name = "paymentMode") String paymentMode, @RequestParam(name = "country") String country) {
		HttpHeaders headers = new HttpHeaders();
		try {

			BuyerSubscription buyerSubscription = new BuyerSubscription();
			PaymentTransaction paymentTransaction = new PaymentTransaction();
			buyerSubscription.setBuyer(buyerSubscriptionPojo.getBuyer());
			buyerSubscription.setPlan(buyerSubscriptionPojo.getPlan());
			paymentTransaction.setUserQuantity(buyerSubscriptionPojo.getUserQuantity());
			buyerSubscription.setUserQuantity(buyerSubscriptionPojo.getUserQuantity());
			buyerSubscription.setRange(buyerSubscriptionPojo.getRange());

			buyerSubscription.getBuyer().setRegistrationOfCountry(countryService.searchCountryById(country));
			Map<String, Object> result = new HashMap<>();
			BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(buyerSubscription.getPlan().getId());
			totalPrice = BigDecimal.ZERO;
			basePrice = BigDecimal.ZERO;
			addtionalUserPrice = BigDecimal.ZERO;
			subscriptionDiscountPrice = BigDecimal.ZERO;

			basePrice = getBasePrice(plan, periodId);

			if (paymentTransaction.getUserQuantity() != null) {
				if (paymentTransaction.getUserQuantity() < 0) {
					throw new ApplicationException("User quantity not be less than zero");
				}
				addtionalUserPrice = getAdditionalUserprice(paymentTransaction.getUserQuantity(), plan, basePrice, periodId, result);
				BigDecimal totalUserPrice = addtionalUserPrice.add(basePrice).setScale(2, BigDecimal.ROUND_HALF_UP);
				totalPrice = totalUserPrice;
				subscriptionDiscountPrice = getSubscriptionDiscountPrice(periodId, plan, totalUserPrice);
				LOG.info("totalUserPrice: " + totalUserPrice);
			}
			promoDiscountPrice = BigDecimal.ZERO;
			BigDecimal totalTax = new BigDecimal(0);
			BigDecimal tax = BigDecimal.ZERO;
			if (tax == null) {
				tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				if (buyerSubscription.getBuyer().getRegistrationOfCountry() != null && "MY".equals(buyerSubscription.getBuyer().getRegistrationOfCountry().getCountryCode())) {
					tax = plan.getTax();
					tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
				} else {
					tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
				}
			}

			totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
			totalTax = (totalPrice.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
			LOG.info("totalTax :" + totalTax);
			paymentTransaction.setAdditionalTax(totalTax);

			if (StringUtils.checkString(promoCode).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCode);
				promoDiscountPrice = getPromoDiscount(promotionalCode.getPromoCode(), totalPrice, result, plan);
				totalPrice = totalPrice.subtract(promoDiscountPrice);
				try {
					promotionalCodeService.getPromotionalCodeByPromoCode(promotionalCode.getPromoCode());
				} catch (Exception e) {
					LOG.error(e);
					throw e;
				}
				paymentTransaction.setPromoCode(promotionalCode);
			}

			LOG.info("After calculation prices are basePrice:" + basePrice + " addtionalUserPrice: " + addtionalUserPrice + "subscriptionDiscountPrice: " + subscriptionDiscountPrice + " promoDiscountPrice: " + promoDiscountPrice + "  totalPrice: " + totalPrice + " and login email: " + buyerSubscription.getBuyer().getLoginEmail() + " with user quantity: " + buyerSubscription.getUserQuantity());
			buyerSubscription.setPlan(plan);
			buyerSubscription.setPlanType(plan.getPlanType());
			BigDecimal priceAmount = basePrice.add(addtionalUserPrice);
			paymentTransaction.setPriceAmount(priceAmount);
			paymentTransaction.setPriceDiscount(subscriptionDiscountPrice);
			paymentTransaction.setTotalPriceAmount(totalPrice.add(totalTax));
			paymentTransaction.setAmount(totalPrice);
			buyerSubscription.setPriceAmount(priceAmount);
			buyerSubscription.setPriceDiscount(subscriptionDiscountPrice);
			buyerSubscription.setTotalPriceAmount(totalPrice.add(totalTax));

			if (promoDiscountPrice != null) {
				paymentTransaction.setPromoCodeDiscount(promoDiscountPrice);
				buyerSubscription.setPromoCodeDiscount(promoDiscountPrice);
			} else {
				paymentTransaction.setPromoCodeDiscount(new BigDecimal(0));
				buyerSubscription.setPromoCodeDiscount(new BigDecimal(0));
			}

			if (buyerSubscription.getUserQuantity() == null || (buyerSubscription.getUserQuantity() != null && buyerSubscription.getUserQuantity() == 0)) {
				paymentTransaction.setUserQuantity(3);
				paymentTransaction.setEventQuantity(9999); // set default 9999 max event on user based
				buyerSubscription.setUserQuantity(3);
				buyerSubscription.setEventQuantity(9999); // set default 9999 max event on user based
			} else {
				paymentTransaction.setUserQuantity(plan.getBaseUsers() + buyerSubscription.getUserQuantity());
				paymentTransaction.setEventQuantity(9999); // set default 9999 max event on user based
				buyerSubscription.setUserQuantity(plan.getBaseUsers() + buyerSubscription.getUserQuantity());
				buyerSubscription.setEventQuantity(9999); // set default 9999 max event on user based
			}

			PlanRange range = null;
			if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
				// User did not change the range. Then default it to the first one.
				if (StringUtils.checkString(buyerSubscription.getRange().getId()).length() == 0) {
					range = plan.getRangeList().get(0);
				} else {
					for (PlanRange pRange : plan.getRangeList()) {
						if (pRange.getId().equals(buyerSubscription.getRange().getId())) {
							range = pRange;
						}
					}
				}
			}
			buyerSubscription.setRange(range);

			PlanPeriod period = null;
			if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
				for (PlanPeriod prd : plan.getPlanPeriodList()) {
					if (prd.getId().equals(periodId)) {
						period = prd;
					}
				}
			}
			buyerSubscription.setPlanPeriod(period);

			paymentTransaction.setCompanyName(buyerSubscription.getBuyer().getCompanyName());
			paymentTransaction.setCompanyRegistrationNumber(buyerSubscription.getBuyer().getCompanyRegistrationNumber());
			paymentTransaction.setFullName(buyerSubscription.getBuyer().getFullName());
			paymentTransaction.setCompanyContactNumber(buyerSubscription.getBuyer().getCompanyContactNumber());
			paymentTransaction.setLoginEmail(buyerSubscription.getBuyer().getLoginEmail());
			paymentTransaction.setCommunicationEmail(buyerSubscription.getBuyer().getCommunicationEmail());
			paymentTransaction.setCountry(buyerSubscription.getBuyer().getRegistrationOfCountry());

			buyerSubscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
			return new ResponseEntity<PaymentIntentPojo>(stripeBuyerSubscriptionService.initiateStripePaymentForBuyerSubscription(buyerSubscription, plan.getId(), paymentMode, false), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while storing buyer : " + e.getMessage(), e);
			headers.set("error", e.getLocalizedMessage());
			return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(path = "/eventBasedBuyerCheckout", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PaymentIntentPojo> buyerEventBaseCheckout(@RequestBody BuyerSubscriptionStripeRequestPojo buyerSubscriptionPojo, @RequestParam(required = false, value = "periodId") String periodId, @RequestParam("promoCodeId") String promoCode, @RequestParam(required = false, value = "basePrice") BigDecimal basePrice, @RequestParam(required = false, value = "addtionalUserPrice") BigDecimal addtionalUserPrice, @RequestParam(required = false, value = "subscriptionDiscountPrice") BigDecimal subscriptionDiscountPrice, @RequestParam(required = false, value = "promoDiscountPrice") BigDecimal promoDiscountPrice, @RequestParam(required = false, value = "totalPrice") BigDecimal totalPrice, @RequestParam(name = "paymentMode") String paymentMode, @RequestParam(name = "country") String country) {
		HttpHeaders headers = new HttpHeaders();
		try {
			PaymentTransaction paymentTransaction = new PaymentTransaction();
			BuyerSubscription buyerSubscription = new BuyerSubscription();
			buyerSubscription.setBuyer(buyerSubscriptionPojo.getBuyer());
			buyerSubscription.setPlan(buyerSubscriptionPojo.getPlan());
			paymentTransaction.setUserQuantity(buyerSubscriptionPojo.getUserQuantity());
			buyerSubscription.setUserQuantity(buyerSubscriptionPojo.getUserQuantity());
			buyerSubscription.setRange(buyerSubscriptionPojo.getRange());

			LOG.info(" promoCode : " + promoCode + " basePrice " + basePrice + " addtionalUserPrice :  " + addtionalUserPrice + "  subscriptionDiscountPrice : " + subscriptionDiscountPrice + "  promoDiscountPrice :  " + promoDiscountPrice + "  totalPrice " + totalPrice);
			LOG.info("Event base Buyer Checkout submitted :   >>>>>   " + buyerSubscription.getBuyer().getLoginEmail() + " UserQuantity " + paymentTransaction.getUserQuantity());
			buyerSubscription.getBuyer().setRegistrationOfCountry(countryService.searchCountryById(country));
			Map<String, Object> result = new HashMap<>();
			BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(buyerSubscription.getPlan().getId());
			buyerSubscription.setPlan(plan);
			buyerSubscription.setPlanType(plan.getPlanType());

			if (promoDiscountPrice != null) {
				paymentTransaction.setPromoCodeDiscount(promoDiscountPrice);
				buyerSubscription.setPromoCodeDiscount(promoDiscountPrice);
			} else {
				paymentTransaction.setPromoCodeDiscount(new BigDecimal(0));
				buyerSubscription.setPromoCodeDiscount(new BigDecimal(0));
			}

			if (paymentTransaction.getUserQuantity() == null || (paymentTransaction.getUserQuantity() != null && paymentTransaction.getUserQuantity() == 0)) {
				paymentTransaction.setEventQuantity(1); // set default 9999 max event on user based
			} else {
				paymentTransaction.setEventQuantity(buyerSubscription.getUserQuantity());
			}
			paymentTransaction.setUserQuantity(9999);

			PlanRange range = null;
			if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
				// User did not change the range. Then default it to the first one.
				if (StringUtils.checkString(buyerSubscription.getRange().getId()).length() == 0) {
					range = plan.getRangeList().get(0);
				} else {
					for (PlanRange pRange : plan.getRangeList()) {
						if (pRange.getId().equals(buyerSubscription.getRange().getId())) {
							range = pRange;
						}
					}
				}
			}
			buyerSubscription.setRange(range);
			totalPrice = range.getPrice().multiply(new BigDecimal(paymentTransaction.getEventQuantity()));
			paymentTransaction.setPriceAmount(range.getPrice().multiply(new BigDecimal(paymentTransaction.getEventQuantity())));
			buyerSubscription.setPriceAmount(range.getPrice().multiply(new BigDecimal(paymentTransaction.getEventQuantity())));
			// if (subscriptionDiscountPrice == null) {
			subscriptionDiscountPrice = BigDecimal.ZERO;
			BigDecimal totalTax = new BigDecimal(0);
			// }

			BigDecimal tax = BigDecimal.ZERO;
			if (tax == null) {
				tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				if (buyerSubscription.getBuyer().getRegistrationOfCountry() != null && "MY".equals(buyerSubscription.getBuyer().getRegistrationOfCountry().getCountryCode())) {
					tax = plan.getTax();
					tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
				} else {
					tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
				}
			}

			totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
			totalTax = (totalPrice.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
			LOG.info("totalTax :" + totalTax);
			paymentTransaction.setAdditionalTax(totalTax);

			if (StringUtils.checkString(promoCode).length() > 0 && !StringUtils.checkString(promoCode).equals(",")) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCode);
				subscriptionDiscountPrice = getPromoDiscount(promotionalCode.getPromoCode(), totalPrice, result, plan);
				totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
				try {
					promotionalCodeService.getPromotionalCodeByPromoCode(promotionalCode.getPromoCode());
				} catch (Exception e) {
					LOG.error(e);
					throw e;
				}
				paymentTransaction.setPromoCode(promotionalCode);
			}

			paymentTransaction.setPriceDiscount(subscriptionDiscountPrice);
			buyerSubscription.setPriceDiscount(subscriptionDiscountPrice);
			// if (totalPrice == null) {
			// totalPrice = paymentTransaction.getPriceAmount().subtract(subscriptionDiscountPrice);
			// }
			paymentTransaction.setTotalPriceAmount(totalPrice.add(totalTax));
			paymentTransaction.setAmount(totalPrice);
			buyerSubscription.setTotalPriceAmount(totalPrice.add(totalTax));

			PlanPeriod period = null;
			if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
				for (PlanPeriod prd : plan.getPlanPeriodList()) {
					if (prd.getId().equals(periodId)) {
						period = prd;
					}
				}
			}
			buyerSubscription.setPlanPeriod(period);

			LOG.info("after promoCode : " + promoCode + " basePrice " + basePrice + " addtionalUserPrice :  " + addtionalUserPrice + "  subscriptionDiscountPrice : " + subscriptionDiscountPrice + "  promoDiscountPrice :  " + promoDiscountPrice + "  totalPrice " + totalPrice);
			LOG.info("after Event base Buyer Checkout submitted :   >>>>>   " + buyerSubscription.getBuyer().getLoginEmail() + " UserQuantity " + paymentTransaction.getUserQuantity());

			paymentTransaction.setCompanyName(buyerSubscription.getBuyer().getCompanyName());
			paymentTransaction.setCompanyRegistrationNumber(buyerSubscription.getBuyer().getCompanyRegistrationNumber());
			paymentTransaction.setFullName(buyerSubscription.getBuyer().getFullName());
			paymentTransaction.setCompanyContactNumber(buyerSubscription.getBuyer().getCompanyContactNumber());
			paymentTransaction.setLoginEmail(buyerSubscription.getBuyer().getLoginEmail());
			paymentTransaction.setCommunicationEmail(buyerSubscription.getBuyer().getCommunicationEmail());
			paymentTransaction.setCountry(buyerSubscription.getBuyer().getRegistrationOfCountry());
			buyerSubscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
			return new ResponseEntity<PaymentIntentPojo>(stripeBuyerSubscriptionService.initiateStripePaymentForBuyerSubscription(buyerSubscription, plan.getId(), paymentMode, false), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while storing buyer : " + e.getMessage(), e);
			headers.add("error", e.getLocalizedMessage());
			return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
