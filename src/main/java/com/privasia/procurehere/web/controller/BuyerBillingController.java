/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.Plan;
import com.privasia.procurehere.core.entity.PlanPeriod;
import com.privasia.procurehere.core.entity.PlanRange;
import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerPlanService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSubscriptionService;
import com.privasia.procurehere.service.BuyerSubscriptionStripeService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.PaymentTransactionService;
import com.privasia.procurehere.service.PlanService;
import com.privasia.procurehere.service.PromotionalCodeService;
import com.privasia.procurehere.service.impl.SubscriptionServiceImpl;
import com.privasia.procurehere.web.editors.BuyerEditor;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.PlanEditor;

/**
 * @author Nitin Otageri
 */
@Controller
@RequestMapping(value = "/buyer/billing")
public class BuyerBillingController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	PlanService planService;

	@Autowired
	CurrencyService currencyService;

	/*
	 * @Autowired SubscriptionService subscriptionService;
	 */
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
	PlanEditor planEditor;

	@Resource
	MessageSource messageSource;

	@Value("${paypal.merchant.id}")
	String MERCHANT_ID;

	@Autowired
	PaymentTransactionService paymentTransactionService;

	@Autowired
	BuyerSubscriptionService buyerSubscriptionService;

	@Autowired
	BuyerPlanService buyerPlanService;

	@Autowired
	PromotionalCodeService promotionalCodeService;

	@Autowired
	BuyerSubscriptionStripeService buyerSubscriptionStripeService;

	@Value("${app.url}")
	String appUrl;

	@Value("${paypal.environment}")
	String PAYPAL_ENVIRONMENT;

	@Value("${stripe.publish}")
	String stripePublicKey;

	@ModelAttribute("subscriptionStatusList")
	public List<SubscriptionStatus> getStatusList() {
		return Arrays.asList(SubscriptionStatus.values());
	}

	@ModelAttribute("planTypeList")
	public List<PlanType> getPlanTypeList() {
		return Arrays.asList(PlanType.values());
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(Buyer.class, buyerEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(Country.class, countryEditor);
		binder.registerCustomEditor(Plan.class, planEditor);
	}

	@RequestMapping(path = "/accountOverview", method = RequestMethod.GET)
	public String accountOverview(Model model, @RequestParam(name = "payment_intent", required = false) String paymentStatus) {
		if (StringUtils.isNotBlank(paymentStatus)) {
			try {
				PaymentTransaction transaction = buyerSubscriptionStripeService.getPaymentTransactionByToken(paymentStatus);
				String msg = buyerSubscriptionStripeService.getPaymentStatus(paymentStatus);
				LOG.info(msg);
				if (msg.indexOf("Processing") != -1) {
					model.addAttribute("info", msg);
				} else {
					return "redirect:renewPlanSuccess/" + transaction.getId();
				}
			} catch (Exception e) {
				model.addAttribute("error", e.getMessage());
				LOG.info("Error in checking payment status " + e.getMessage());
			}
		}
		BuyerSubscription subscription = buyerSubscriptionService.getCurrentBuyerSubscriptionForBuyer(SecurityLibrary.getLoggedInUserTenantId());
		BuyerSubscription lastSubscription = buyerSubscriptionService.getLastBuyerSubscriptionForBuyer(SecurityLibrary.getLoggedInUserTenantId());
		LOG.info("Fetching subscription details for Buyer : " + SecurityLibrary.getLoggedInUserTenantId());
		Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("buyer", buyer);
		model.addAttribute("subscription", subscription);
		model.addAttribute("lastSubscription", lastSubscription);
		model.addAttribute("merchantId", this.MERCHANT_ID);
		model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
		model.addAttribute("publicKey", stripePublicKey);
		return "buyerAccountOverview";
	}

	@RequestMapping(path = "/billing", method = RequestMethod.GET)
	public String billing(Model model) {
		LOG.info("Fetching subscription details for Buyer : " + SecurityLibrary.getLoggedInUserTenantId());
		Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());

		model.addAttribute("buyer", buyer);
		model.addAttribute("merchantId", this.MERCHANT_ID);
		model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
		return "buyerBilling";
	}

	@RequestMapping(path = "/paymentTransactionData", method = RequestMethod.GET)
	public ResponseEntity<TableData<PaymentTransaction>> paymentTransactionData(TableDataInput input) {
		try {
			TableData<PaymentTransaction> data = new TableData<PaymentTransaction>(paymentTransactionService.findSuccessfulPaymentTransactionsForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input));
			long totalFilterCount = paymentTransactionService.findTotalSuccessfulFilteredPaymentTransactionsForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = paymentTransactionService.findTotalSuccessfulPaymentTransactionForBuyer(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<PaymentTransaction>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Payment Transaction list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Payment Transaction list : " + e.getMessage());
			return new ResponseEntity<TableData<PaymentTransaction>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/subscriptionHistoryData", method = RequestMethod.GET)
	public ResponseEntity<TableData<BuyerSubscription>> subscriptionHistoryData(TableDataInput input) {
		try {
			TableData<BuyerSubscription> data = new TableData<BuyerSubscription>(buyerSubscriptionService.findBuyerSubscriptionHistoryForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input));
			long totalFilterCount = buyerSubscriptionService.findTotalFilteredBuyerSubscriptionHistoryForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = buyerSubscriptionService.findTotalBuyerSubscriptionHistoryForBuyer(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<BuyerSubscription>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Buyer subscription list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Buyer Subscription list : " + e.getMessage());
			return new ResponseEntity<TableData<BuyerSubscription>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// @RequestMapping(path = "/renew/{planId}", method = RequestMethod.POST)
	// public String doRenewInitiate(@RequestParam("numberUserEvent") String numberUserEvent, @RequestParam(required =
	// false, value = "periodId") String periodId, @RequestParam(value = "rangeId") String rangeId,
	// @RequestParam(required = false, value = "promoCodeId") String promoCodeId, @RequestParam(value = "feeValue")
	// BigDecimal feeValue, @RequestParam(required = false, value = "feeDiscountValue") BigDecimal feeDiscountValue,
	// @RequestParam(required = false, value = "promoCodeDiscount") BigDecimal promoCodeDiscount, @RequestParam(value =
	// "totalFeeAmount") BigDecimal totalFeeAmount, @PathVariable(name = "planId") String planId, @RequestParam(required
	// = false, value = "autoChargeSubscription") boolean autoChargeSubscription, Model model, HttpSession session,
	// RedirectAttributes redir) {
	// LOG.info("Auto Charge Subscription :" + autoChargeSubscription);
	// LOG.info("User requested to renew Plan : " + planId);
	// // LOG.info("User requested to numberUserEvent : " + numberUserEvent);
	// // LOG.info("User requested to feeValue : " + feeValue);
	// // LOG.info("User requested to feeDiscountValue : " + feeDiscountValue);
	// // LOG.info("User requested to totalFeeAmount : " + totalFeeAmount);
	// // LOG.info("User requested to promoCodeId : " + promoCodeId);
	// // LOG.info("User requested to promoCodeDiscount : " + promoCodeDiscount);
	// // LOG.info("User requested to rangeId : " + rangeId);
	// // LOG.info("User requested to periodId : " + periodId);
	//
	// try {
	// if (totalFeeAmount.compareTo(new BigDecimal(0)) <= 0) {
	// throw new ApplicationException("Total fee should be greater then Zero");
	// }
	// BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
	//
	// BigDecimal totalPrice = BigDecimal.ZERO;
	// BigDecimal basePrice = BigDecimal.ZERO;
	// BigDecimal addtionalUserPrice = BigDecimal.ZERO;
	// BigDecimal subscriptionDiscountPrice = BigDecimal.ZERO;
	// BigDecimal promoDiscountPrice = BigDecimal.ZERO;
	// BigDecimal priceAmount = BigDecimal.ZERO;
	// PromotionalCode promoCode = null;
	// PaymentTransaction paymentTransaction = new PaymentTransaction();
	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
	// // cross checking for promo code
	// try {
	// promoCode = promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(),
	// SecurityLibrary.getLoggedInUserTenantId());
	// // LOG.info("Promo code :" + promotionalCode.toLogString());
	// } catch (Exception e) {
	// throw e;
	// }
	// paymentTransaction.setPromoCode(promotionalCode);
	// }
	//
	// if (plan.getPlanType() == PlanType.PER_USER) {
	// basePrice = getBasePrice(plan, periodId);
	// priceAmount = basePrice;
	// if (numberUserEvent != null) {
	// Integer users = new Integer(numberUserEvent);
	// LOG.info("users: " + users);
	//
	// if (users < 0) {
	// throw new ApplicationException("User quantity not be less than zero");
	// } else if (users > 3) {
	// users = users - 3;
	// } else {
	// users = 0;
	// }
	// addtionalUserPrice = getAdditionalUserprice(users, plan, basePrice, periodId);
	//
	// LOG.info("addtionalUserPrice: " + addtionalUserPrice);
	//
	// BigDecimal totalUserPrice = addtionalUserPrice.add(basePrice).setScale(2, BigDecimal.ROUND_HALF_UP);
	// totalPrice = totalUserPrice;
	// subscriptionDiscountPrice = getSubscriptionDiscountPrice(periodId, plan, totalUserPrice);
	//
	// LOG.info("subscriptionDiscountPrice: " + subscriptionDiscountPrice);
	// LOG.info("totalUserPrice: " + totalUserPrice);
	// }
	//
	// promoDiscountPrice = getPromoDiscount(promoCode, totalPrice);
	// totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
	// totalPrice = totalPrice.subtract(promoDiscountPrice);
	// LOG.info("after calculation basePrice:" + basePrice + " addtionalUserPrice: " + addtionalUserPrice +
	// "subscriptionDiscountPrice:" + subscriptionDiscountPrice + " promoDiscountPrice : " + promoDiscountPrice + "
	// totalPrice " + totalPrice);
	//
	// // totalPrice = totalPrice.add(addtionalUserPrice);
	//
	// } else {
	//
	// Integer event = new Integer(numberUserEvent);
	// priceAmount = getEventprice(plan);
	// PlanRange range = null;
	// if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
	// if (StringUtils.checkString(rangeId).length() == 0) {
	// range = plan.getRangeList().get(0);
	// } else {
	// for (PlanRange pRange : plan.getRangeList()) {
	// if (pRange.getId().equals(rangeId)) {
	// range = pRange;
	// }
	// }
	// }
	// }
	// LOG.info("============> range.getPrice():" + range.getPrice());
	// LOG.info("============>" + event);
	//
	// totalPrice = range.getPrice().multiply(new BigDecimal(event));
	//
	// LOG.info("after calculation totalPrice:" + totalPrice);
	//
	// LOG.info("after calculation totalPrice:" + priceAmount);
	// subscriptionDiscountPrice = BigDecimal.ZERO;
	// subscriptionDiscountPrice = getPromoDiscount(promoCode, totalPrice);
	//
	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// promoDiscountPrice = subscriptionDiscountPrice;
	// }
	// totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
	//
	// }
	//
	// LOG.info("User requested to totalPrice : " + totalPrice + "============>" + totalFeeAmount);
	// LOG.info("User requested to priceAmount : " + priceAmount + "===============>" + feeValue);
	// LOG.info("User requested to subscriptionDiscountPrice : " + subscriptionDiscountPrice + "=============>" +
	// feeDiscountValue);
	// LOG.info("User requested to promoCodeDiscount : " + promoDiscountPrice + "===================>" +
	// promoCodeDiscount);
	// LOG.info("User requested to rangeId : " + rangeId);
	//
	// BuyerSubscription subscription = new BuyerSubscription();
	// subscription.setPlan(plan);
	// subscription.setPlanType(plan.getPlanType());
	// subscription.setPriceAmount(priceAmount);
	// subscription.setPriceDiscount(subscriptionDiscountPrice);
	// subscription.setTotalPriceAmount(totalPrice);
	// subscription.setAutoChargeSubscription(autoChargeSubscription);
	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// subscription.setPromoCodeDiscount(promoDiscountPrice);
	// } else {
	// subscription.setPromoCodeDiscount(new BigDecimal(0));
	// }
	// if (subscription.getPlanType() == PlanType.PER_USER) {
	// subscription.setUserQuantity(new Integer(numberUserEvent));
	// subscription.setEventQuantity(9999); // set default 9999 max event on user based
	// } else if (subscription.getPlanType() == PlanType.PER_EVENT) {
	// subscription.setEventQuantity(new Integer(numberUserEvent));
	// subscription.setUserQuantity(999); // set default 999 max user on event based
	// }
	// PlanRange range = null;
	// if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
	// for (PlanRange pRange : plan.getRangeList()) {
	// if (pRange.getId().equals(rangeId)) {
	// range = pRange;
	// }
	// }
	// }
	// subscription.setRange(range);
	// PlanPeriod period = null;
	// if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
	// for (PlanPeriod prd : plan.getPlanPeriodList()) {
	// if (prd.getId().equals(periodId)) {
	// period = prd;
	// }
	// }
	// }
	// subscription.setPlanPeriod(period);
	// Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
	//
	// paymentTransaction.setCountry(buyer.getRegistrationOfCountry());
	// paymentTransaction.setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
	// paymentTransaction.setBuyer(buyer);
	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
	// // cross checking for promo code
	// try {
	// promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(),
	// SecurityLibrary.getLoggedInUserTenantId());
	// // LOG.info("Promo code :" + promotionalCode.toLogString());
	// } catch (Exception e) {
	// throw e;
	// }
	// paymentTransaction.setPromoCode(promotionalCode);
	// }
	// subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
	// subscription.setBuyer(buyer);
	// String returnURL = appUrl + "/buyer/billing/confirmSuccessRenew?planId=" + planId + "&txId=";
	// String cancelURL = appUrl + "/buyer/billing/cancel?planId=" + planId + "&txId=";
	// return buyerSubscriptionService.initiatePaypalPaymentForBuyerSubscription(subscription, planId, session,
	// returnURL, cancelURL);
	// } catch (Exception e) {
	// LOG.error("Error while renew subscription :" + e.getMessage(), e);
	// redir.addFlashAttribute("error", e.getMessage());
	// return "redirect:accountOverview";
	// }
	// }

	@RequestMapping(path = "/confirmSuccessRenew", method = RequestMethod.GET)
	public String confirmSuccessRenewPayment(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, @RequestParam(name = "token") String token, @RequestParam(name = "PayerID") String payerID, Model model, HttpServletRequest request, HttpSession session) {

		/*
		 * Build a second API request to PayPal, using the token as the ID to get the details on the payment
		 * authorization
		 */

		try {
			String subscriptionId = (String) session.getAttribute("subscriptionId");
			LOG.info("Subscription Id : " + subscriptionId);
			String nvpstr = "&TOKEN=" + token;
			LOG.info("Invoking PayPal Checkout Details ===================> ");
			/*
			 * Make the API call and store the results in an array. If the call was a success, show the authorization
			 * details, and provide an action to complete the payment. If failed, show the error
			 */
			String payerId = null;
			HashMap<String, String> nvp = buyerSubscriptionService.invokePaypalService("GetExpressCheckoutDetails", nvpstr);
			String strAck = nvp.get("ACK").toString();
			if (strAck != null && (strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS) || strAck.equalsIgnoreCase("SuccessWithWarning"))) {
				session.setAttribute("PAYERID", nvp.get("PAYERID").toString());
				payerId = nvp.get("PAYERID").toString();

			}
			// The map will contain values as documented at
			// https://developer.paypal.com/docs/classic/api/merchant/GetExpressCheckoutDetails_API_Operation_NVP/
			String serverName = request.getServerName();
			nvp = buyerSubscriptionService.confirmSubscriptionRenew(token, payerId, model, serverName, txId);
			session.setAttribute("nvp", nvp);
			request.setAttribute("renewal", "true");
		} catch (Exception e) {
			LOG.error("Error while renew Subscription : " + e.getMessage(), e);
		}
		return "redirect:renewPlanSuccess/" + txId;
	}

	@RequestMapping(path = "/cancel", method = RequestMethod.GET)
	public String doCancel(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, Model model, RedirectAttributes redir) {
		LOG.info("paln Id :" + planId + " txId :" + txId);
		PaymentTransaction paymentTransaction = buyerSubscriptionService.cancelPaymentTransaction(txId);
		LOG.info("Cancel payment :" + paymentTransaction.getErrorCode() + " - " + paymentTransaction.getErrorMessage());
		redir.addFlashAttribute("warn", paymentTransaction.getErrorCode() + " - " + paymentTransaction.getErrorMessage());
		return "redirect:/buyer/billing/accountOverview";
	}

	@RequestMapping(path = "/renewPlanSuccess/{paymentTransactionId}", method = RequestMethod.GET)
	public String showRenewSuccess(@PathVariable(name = "paymentTransactionId") String paymentTransactionId, Model model, HttpServletRequest request, HttpSession session) {

		PaymentTransaction paymentTransaction = buyerSubscriptionService.getPaymentTransactionById(paymentTransactionId);

		if (paymentTransaction != null) {
			try {
				String msg = buyerSubscriptionStripeService.getPaymentStatus(paymentTransaction.getPaymentToken());
				if (msg.indexOf("Processing") != -1) {
					LOG.info("Payment is still not completed.....");
				} else {
					// Set status as success but not updating as webhook will update the status
					paymentTransaction.setStatus(TransactionStatus.SUCCESS);
				}
			} catch (Exception e) {
				LOG.error("Error in checking payment status " + e.getMessage());
			}
		}
		BuyerSubscription subscription = buyerSubscriptionService.getBuyerSubscriptionById(paymentTransaction.getBuyerSubscription().getId());
		request.setAttribute("renewal", "true");
		model.addAttribute("subscription", subscription);
		model.addAttribute("buyer", subscription.getBuyer());
		model.addAttribute("paymentTransaction", paymentTransaction);
		return "renewalSuccess";
	}

	@RequestMapping(path = "/getSubscription/{subsId}", method = RequestMethod.GET)
	public ResponseEntity<BuyerSubscription> getSubscription(@PathVariable(name = "subsId") String subsId) {
		BuyerSubscription subscription = null;
		try {
			subscription = buyerSubscriptionService.getBuyerSubscriptionByIdForUpdateSubs(subsId);
			if (subscription != null && subscription.getPlan() != null) {
				subscription.getPlan().setCreatedBy(null);
				subscription.getPlan().setModifiedBy(null);
				subscription.getPlan().setPlanPeriodList(null);
			}

		} catch (Exception e) {
			LOG.error("Error while fetch buyer subscription :" + e.getMessage(), e);
			return new ResponseEntity<BuyerSubscription>(subscription, new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<BuyerSubscription>(subscription, new HttpHeaders(), HttpStatus.OK);
	}

	// @RequestMapping(path = "/updateSubscription/{subsId}", method = RequestMethod.POST)
	// public String doUpdateSubscriptionInitiate(@RequestParam("updateNumberUserEvent") String numberUserEvent,
	// @RequestParam("chargeMonths") String chargeMonths, @RequestParam(required = false, value = "updatePeriodId")
	// String periodId, @RequestParam(value = "updateRangeId") String rangeId, @RequestParam(required = false, value =
	// "updatePromoCodeId") String promoCodeId, @RequestParam(value = "updateFeeValue") BigDecimal feeValue,
	// @RequestParam(required = false, value = "updateFeeDiscountValue") BigDecimal feeDiscountValue,
	// @RequestParam(required = false, value = "updatePromoCodeDiscount") BigDecimal promoCodeDiscount,
	// @RequestParam(value = "updateTotalFeeAmount") BigDecimal totalFeeAmount, @PathVariable(name = "subsId") String
	// subsId, Model model, HttpSession session, RedirectAttributes redir) {
	// LOG.info("User requested to Update subsId : " + subsId);

	// try {
	// BuyerSubscription subscription = buyerSubscriptionService.getBuyerSubscriptionById(subsId);
	// subscription.setPriceAmount(feeValue);
	// subscription.setPriceDiscount(feeDiscountValue);
	// subscription.setTotalPriceAmount(totalFeeAmount);
	// if (totalFeeAmount.compareTo(new BigDecimal(0)) <= 0) {
	// throw new ApplicationException("Total fee should be greater then Zero");
	// }
	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// subscription.setPromoCodeDiscount(promoCodeDiscount);
	// } else {
	// subscription.setPromoCodeDiscount(new BigDecimal(0));
	// // subscription.setPromoCode(null);
	// }
	// if (subscription.getPlanType() == PlanType.PER_USER) {
	// subscription.setUserQuantity(subscription.getUserQuantity() + new Integer(numberUserEvent));
	// subscription.setEventQuantity(9999); // set default 9999 max event on user based
	// } else if (subscription.getPlanType() == PlanType.PER_EVENT) {
	// subscription.setEventQuantity(subscription.getEventQuantity() + new Integer(numberUserEvent));
	// subscription.setUserQuantity(999); // set default 999 max user on event based
	// }
	// PlanRange range = null;
	// if (CollectionUtil.isNotEmpty(subscription.getPlan().getRangeList())) {
	// for (PlanRange pRange : subscription.getPlan().getRangeList()) {
	// if (pRange.getId().equals(rangeId)) {
	// range = pRange;
	// }
	// }
	// }
	// subscription.setRange(range);
	// Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
	// PaymentTransaction paymentTransaction = new PaymentTransaction();
	// paymentTransaction.setCountry(buyer.getRegistrationOfCountry());
	// paymentTransaction.setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
	// paymentTransaction.setBuyer(buyer);
	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
	// // cross checking for promo code
	// try {
	// promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(),
	// SecurityLibrary.getLoggedInUserTenantId());
	// // LOG.info("Promo code :" + promotionalCode.toLogString());
	// } catch (Exception e) {
	// throw e;
	// }
	// paymentTransaction.setPromoCode(promotionalCode);
	// }

	// // setting charge months
	// if (subscription.getPlanPeriod() != null) {
	// subscription.getPlanPeriod().setPlanDuration(new Integer(chargeMonths));
	// }
	// subscription.getPaymentTransactions().add(paymentTransaction);
	// subscription.setPaymentTransactions(subscription.getPaymentTransactions());
	// String returnURL = appUrl + "/buyer/billing/confirmSuccessUpdateSubs?planId=" + subscription.getPlan().getId() +
	// "&txId=";
	// String cancelURL = appUrl + "/buyer/billing/cancel?planId=" + subscription.getPlan().getId() + "&txId=";
	// return buyerSubscriptionService.initiatePaypalPaymentForUpdateBuyerSubscription(subscription,
	// subscription.getPlan().getId(), session, returnURL, cancelURL, paymentTransaction, new Integer(numberUserEvent));
	// } catch (Exception e) {
	// LOG.error("Error while update subscription :" + e.getMessage(), e);
	// redir.addFlashAttribute("error", e.getMessage());
	// return "redirect:/buyer/billing/accountOverview";
	// }
	// }

	@RequestMapping(path = "/confirmSuccessUpdateSubs", method = RequestMethod.GET)
	public String confirmSuccessUpdateSubsPayment(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, @RequestParam(name = "token") String token, @RequestParam(name = "PayerID") String payerID, Model model, HttpServletRequest request, HttpSession session) {

		/*
		 * Build a second API request to PayPal, using the token as the ID to get the details on the payment
		 * authorization
		 */

		try {
			// String subscriptionId = (String) session.getAttribute("subscriptionId");
			BuyerSubscription subscription = (BuyerSubscription) session.getAttribute("subscription");
			LOG.info("Subscription Id : " + subscription.getId());
			String nvpstr = "&TOKEN=" + token;
			LOG.info("Invoking PayPal Checkout Details ===================> ");
			/*
			 * Make the API call and store the results in an array. If the call was a success, show the authorization
			 * details, and provide an action to complete the payment. If failed, show the error
			 */
			String payerId = null;
			HashMap<String, String> nvp = buyerSubscriptionService.invokePaypalService("GetExpressCheckoutDetails", nvpstr);
			String strAck = nvp.get("ACK").toString();
			if (strAck != null && (strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS) || strAck.equalsIgnoreCase("SuccessWithWarning"))) {
				session.setAttribute("PAYERID", nvp.get("PAYERID").toString());
				payerId = nvp.get("PAYERID").toString();

			}
			// The map will contain values as documented at
			// https://developer.paypal.com/docs/classic/api/merchant/GetExpressCheckoutDetails_API_Operation_NVP/
			String serverName = request.getServerName();

			PaymentTransaction paymentTransaction = (PaymentTransaction) session.getAttribute("paymentTransaction");

			nvp = buyerSubscriptionService.confirmSubscriptionUpdate(token, payerId, model, serverName, txId, subscription, paymentTransaction);
			session.setAttribute("nvp", nvp);
			// request.setAttribute("renewal", "true");
		} catch (Exception e) {
			LOG.error("Error while update Subscription : " + e.getMessage(), e);
		}
		return "redirect:renewPlanSuccess/" + txId;
	}

	@RequestMapping(path = "/subscriptionError/{planId}/{paymentTransactionId}", method = RequestMethod.GET)
	public String showPaymentError(@PathVariable(name = "planId") String planId, @PathVariable(name = "paymentTransactionId") String paymentTransactionId, Model model, RedirectAttributes redir) {
		PaymentTransaction paymentTransaction = buyerSubscriptionService.errorPaymentTransaction(paymentTransactionId); // cancelPaymentTransaction(paymentTransactionId);
		LOG.info("Subscription payment :" + paymentTransaction.getErrorCode() + " - " + paymentTransaction.getErrorMessage());
		redir.addFlashAttribute("error", paymentTransaction.getErrorCode() + " - " + paymentTransaction.getErrorMessage());
		return "redirect:/buyer/billing/accountOverview";
	}

	@RequestMapping(path = "/getPromoCode", method = RequestMethod.GET)
	public ResponseEntity<PromotionalCode> getPromoCode(@RequestParam("promoCode") String promoCode, @RequestParam(name = "plan", required = false) String planId, @RequestParam(name = "totalPrice", required = false) BigDecimal totalPrice) {
		LOG.info(" Promo Code : " + promoCode);
		PromotionalCode promotionalCode = null;
		try {
			if (StringUtils.isNotBlank(planId)) {
				BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
				promotionalCodeService.validatePromoCode(promoCode, null, plan, totalPrice, TenantType.BUYER, SecurityLibrary.getLoggedInUserTenantId());
			}
			promotionalCode = promotionalCodeService.checkPromotionalCodeByPromoCode(promoCode, SecurityLibrary.getLoggedInUserTenantId());
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

	@RequestMapping(path = "/changeBuyerPlan/{planId}", method = RequestMethod.GET)
	public String doChangePlan(@PathVariable(name = "planId") String planId, Model model, HttpSession session, RedirectAttributes redir, @RequestParam(name = "payment_intent", required = false) String paymentStatus, HttpServletRequest request) {
		LOG.info("User requested to change Plan existing Plan: " + planId);
		try {

			if (StringUtils.isNotBlank(paymentStatus)) {
				try {
					PaymentTransaction transaction = buyerSubscriptionStripeService.getPaymentTransactionByToken(paymentStatus);
					String msg = buyerSubscriptionStripeService.getPaymentStatus(paymentStatus);
					LOG.info(msg);
					if (msg.indexOf("Processing") != -1) {
						model.addAttribute("info", msg);
					} else {
						BuyerSubscription subscription = buyerSubscriptionService.getBuyerSubscriptionById(transaction.getBuyerSubscription().getId());
						request.setAttribute("changePlan", "true");
						model.addAttribute("subscription", subscription);
						// Not saving to DB just for display purposes. Webhook will update status
						transaction.setStatus(TransactionStatus.SUCCESS);
						model.addAttribute("paymentTransaction", transaction);
						return "changePlanSuccess";
					}
				} catch (Exception e) {
					model.addAttribute("error", e.getMessage());
					LOG.info("Error in checking payment status " + e.getMessage());
				}
			}

			List<BuyerPlan> buyerPlanList = buyerPlanService.findAllBuyerPlansByStatus(PlanStatus.ACTIVE);
			List<BuyerPlan> changePlanList = new ArrayList<BuyerPlan>();
			if (CollectionUtil.isNotEmpty(buyerPlanList)) {
				for (BuyerPlan buyerPlan : buyerPlanList) {
					if (!buyerPlan.getId().equals(planId)) {
						changePlanList.add(buyerPlan);
					}
				}
			}
			Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("buyerCountry", buyer.getRegistrationOfCountry().getCountryCode());
			model.addAttribute("planList", changePlanList);
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
			model.addAttribute("publicKey", stripePublicKey);
			return "changeBuyerPlan";
		} catch (Exception e) {
			LOG.error("Error while change subscription :" + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
			return "redirect:/buyer/billing/accountOverview";
		}
	}

	// @RequestMapping(path = "/changeBuyerPlan/{planId}", method = RequestMethod.POST)
	// public String changeBuyerPlan(@RequestParam(required = false, value = "immediateEffect") boolean immediateEffect,
	// @RequestParam("numberUserEvent") String numberUserEvent, @RequestParam(required = false, value = "periodId")
	// String periodId, @RequestParam(value = "rangeId") String rangeId, @RequestParam(required = false, value =
	// "promoCodeId") String promoCodeId, @RequestParam(value = "feeValue") BigDecimal feeValue, @RequestParam(required
	// = false, value = "feeDiscountValue") BigDecimal feeDiscountValue, @RequestParam(required = false, value =
	// "promoCodeDiscount") BigDecimal promoCodeDiscount, @RequestParam(value = "totalFeeAmount") BigDecimal
	// totalFeeAmount, @PathVariable(name = "planId") String planId, @RequestParam(required = false, value =
	// "autoChargeSubscription") boolean autoChargeSubscription, Model model, HttpSession session, RedirectAttributes
	// redir) {

	// LOG.info("User requested to numberUserEvent : " + numberUserEvent);
	// LOG.info("User requested Before priceAmount : " + feeValue);
	// LOG.info("User requested Before subscriptionDiscountPrice : " + feeDiscountValue);
	// LOG.info("User requested Before totalPrice : " + totalFeeAmount);
	// LOG.info("User requested Before promoDiscountPrice : " + promoCodeDiscount);

	// try {
	// if (totalFeeAmount.compareTo(new BigDecimal(0)) <= 0) {
	// throw new ApplicationException("Total fee should be greater then Zero");
	// }
	// BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
	// PaymentTransaction paymentTransaction = new PaymentTransaction();

	// BigDecimal totalPrice = BigDecimal.ZERO;
	// BigDecimal basePrice = BigDecimal.ZERO;
	// BigDecimal addtionalUserPrice = BigDecimal.ZERO;
	// BigDecimal subscriptionDiscountPrice = BigDecimal.ZERO;
	// BigDecimal promoDiscountPrice = BigDecimal.ZERO;
	// BigDecimal priceAmount = BigDecimal.ZERO;
	// PromotionalCode promoCode = null;

	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
	// // cross checking for promo code
	// try {
	// promoCode = promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(),
	// SecurityLibrary.getLoggedInUserTenantId());
	// // LOG.info("Promo code :" + promotionalCode.toLogString());
	// } catch (Exception e) {
	// throw e;
	// }
	// paymentTransaction.setPromoCode(promotionalCode);
	// }

	// if (plan.getPlanType() == PlanType.PER_USER) {
	// basePrice = getBasePrice(plan, periodId);

	// if (numberUserEvent != null) {
	// Integer users = new Integer(numberUserEvent);
	// if (users < 0) {
	// throw new ApplicationException("User quantity not be less than zero");
	// } else if (users > 3) {
	// users = users - 3;
	// } else {
	// users = 0;
	// }
	// addtionalUserPrice = getAdditionalUserprice(users, plan, basePrice, periodId);
	// BigDecimal totalUserPrice = addtionalUserPrice.add(basePrice).setScale(2, BigDecimal.ROUND_HALF_UP);
	// totalPrice = totalUserPrice;
	// subscriptionDiscountPrice = getSubscriptionDiscountPrice(periodId, plan, totalUserPrice);
	// LOG.info("totalUserPrice: " + totalUserPrice);
	// }

	// promoDiscountPrice = getPromoDiscount(promoCode, totalPrice);
	// totalPrice = totalPrice.subtract(subscriptionDiscountPrice);

	// LOG.info("after calculation basePrice:" + basePrice + " addtionalUserPrice: " + addtionalUserPrice +
	// "subscriptionDiscountPrice:" + subscriptionDiscountPrice + " promoDiscountPrice : " + promoDiscountPrice + "
	// totalPrice " + totalPrice);

	// priceAmount = basePrice.add(addtionalUserPrice);

	// } else {

	// basePrice = getBasePrice(plan, periodId);

	// Integer event = new Integer(numberUserEvent);
	// PlanRange range = null;
	// if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
	// // User did not change the range. Then default it to the first one.
	// if (StringUtils.checkString(rangeId).length() == 0) {
	// range = plan.getRangeList().get(0);
	// } else {
	// for (PlanRange pRange : plan.getRangeList()) {
	// if (pRange.getId().equals(rangeId)) {
	// range = pRange;
	// }
	// }
	// }
	// }
	// totalPrice = range.getPrice().multiply(new BigDecimal(event));

	// priceAmount = basePrice.add(totalPrice);
	// // if (subscriptionDiscountPrice == null) {
	// subscriptionDiscountPrice = BigDecimal.ZERO;
	// // }
	// subscriptionDiscountPrice = getPromoDiscount(promoCode, totalPrice);

	// if (totalPrice == null) {
	// totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
	// }

	// }

	// LOG.info("User requested After priceAmoun============================t : " + priceAmount);
	// LOG.info("User requested After subscriptionDiscountPrice ========================: " +
	// subscriptionDiscountPrice);
	// LOG.info("User requested After totalPrice : " + totalPrice);
	// LOG.info("User requested After promoDiscountPrice : " + promoDiscountPrice);

	// BuyerSubscription subscription = new BuyerSubscription();
	// subscription.setPlan(plan);
	// subscription.setPlanType(plan.getPlanType());
	// subscription.setPriceAmount(priceAmount);
	// subscription.setPriceDiscount(subscriptionDiscountPrice);
	// subscription.setTotalPriceAmount(totalPrice);
	// subscription.setImmediateEffect(immediateEffect);
	// subscription.setAutoChargeSubscription(autoChargeSubscription);

	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// subscription.setPromoCodeDiscount(promoDiscountPrice);
	// } else {
	// subscription.setPromoCodeDiscount(new BigDecimal(0));
	// }
	// if (subscription.getPlanType() == PlanType.PER_USER) {
	// subscription.setUserQuantity(new Integer(numberUserEvent));
	// subscription.setEventQuantity(9999); // set default 9999 max event on user based
	// } else if (subscription.getPlanType() == PlanType.PER_EVENT) {
	// subscription.setEventQuantity(new Integer(numberUserEvent));
	// subscription.setUserQuantity(999); // set default 999 max user on event based
	// }
	// PlanRange range = null;
	// if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
	// for (PlanRange pRange : plan.getRangeList()) {
	// if (pRange.getId().equals(rangeId)) {
	// range = pRange;
	// }
	// }
	// }
	// subscription.setRange(range);
	// PlanPeriod period = null;
	// if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
	// for (PlanPeriod prd : plan.getPlanPeriodList()) {
	// if (prd.getId().equals(periodId)) {
	// period = prd;
	// }
	// }
	// }
	// subscription.setPlanPeriod(period);
	// Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());

	// paymentTransaction.setCountry(buyer.getRegistrationOfCountry());
	// paymentTransaction.setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
	// paymentTransaction.setBuyer(buyer);

	// subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
	// subscription.setBuyer(buyer);
	// String returnURL = appUrl + "/buyer/billing/confirmSuccessChangePlan?planId=" + planId + "&txId=";
	// String cancelURL = appUrl + "/buyer/billing/cancel?planId=" + planId + "&txId=";
	// return buyerSubscriptionService.initiatePaypalPaymentForBuyerSubscriptionChangePlan(subscription, planId,
	// session, returnURL, cancelURL);
	// } catch (Exception e) {
	// LOG.error("Error while change subscription :" + e.getMessage(), e);
	// redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.change.subscription", new Object[] {
	// e.getMessage() }, Global.LOCALE));
	// return "redirect:/buyer/billing/changeBuyerPlan/" + planId;
	// }
	// }

	@RequestMapping(path = "/confirmSuccessChangePlan", method = RequestMethod.GET)
	public String confirmSuccessChangePlan(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, @RequestParam(name = "token") String token, @RequestParam(name = "PayerID") String payerID, Model model, HttpServletRequest request, HttpSession session) {

		/*
		 * Build a second API request to PayPal, using the token as the ID to get the details on the payment
		 * authorization
		 */

		try {
			String subscriptionId = (String) session.getAttribute("subscriptionId");
			LOG.info("Subscription Id : " + subscriptionId);
			String nvpstr = "&TOKEN=" + token;
			LOG.info("Invoking PayPal Checkout Details ===================> ");
			/*
			 * Make the API call and store the results in an array. If the call was a success, show the authorization
			 * details, and provide an action to complete the payment. If failed, show the error
			 */
			String payerId = null;
			HashMap<String, String> nvp = buyerSubscriptionService.invokePaypalService("GetExpressCheckoutDetails", nvpstr);
			String strAck = nvp.get("ACK").toString();
			if (strAck != null && (strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS) || strAck.equalsIgnoreCase("SuccessWithWarning"))) {
				session.setAttribute("PAYERID", nvp.get("PAYERID").toString());
				payerId = nvp.get("PAYERID").toString();

			}
			// The map will contain values as documented at
			// https://developer.paypal.com/docs/classic/api/merchant/GetExpressCheckoutDetails_API_Operation_NVP/
			String serverName = request.getServerName();
			nvp = buyerSubscriptionService.confirmSubscriptionChangePlan(token, payerId, model, serverName, txId);
			session.setAttribute("nvp", nvp);
			request.setAttribute("changePlan", "true");
		} catch (Exception e) {
			LOG.error("Error while Change sucess Subscription : " + e.getMessage(), e);
		}
		return "redirect:changePlanSuccess/" + txId;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/changePlanSuccess/{paymentTransactionId}", method = RequestMethod.GET)
	public String showChangePlanSuccess(@PathVariable(name = "paymentTransactionId") String paymentTransactionId, Model model, HttpServletRequest request, HttpSession session) {

		PaymentTransaction paymentTransaction = buyerSubscriptionService.getPaymentTransactionById(paymentTransactionId);

		BuyerSubscription subscription = buyerSubscriptionService.getBuyerSubscriptionById(paymentTransaction.getBuyerSubscription().getId());
		request.setAttribute("changePlan", "true");

		HashMap<String, String> nvp = (HashMap<String, String>) session.getAttribute("nvp");
		session.removeAttribute("nvp");

		model.addAttribute("paypalResponse", nvp);
		model.addAttribute("subscription", subscription);
		model.addAttribute("paymentTransaction", paymentTransaction);
		return "changePlanSuccess";
	}

	@RequestMapping(path = "/capturePayment/{subscriptionId}", method = RequestMethod.GET)
	public String capturePayment(@PathVariable(name = "subscriptionId") String subscriptionId, Model model, HttpSession session, RedirectAttributes redir) {
		LOG.info("User confirmed payment for subs id: " + subscriptionId);
		try {
			buyerSubscriptionService.doCapturePayment(subscriptionId);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.subscription.payment.confirmed", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/billing/accountOverview";
		} catch (Exception e) {
			LOG.error("Error while subscription User confirmed payment: " + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
			return "redirect:/buyer/billing/accountOverview";
		}
	}

	@RequestMapping(path = "/subscriptionHistoryExcel", method = RequestMethod.GET)
	public void downloadSubscriptionHistoryExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("downloadSubscriptionHistoryExcel this method is called here");
		try {
			buyerSubscriptionService.downloadSubscriptionHistoryExcel(response, SecurityLibrary.getLoggedInUserTenantId());
		} catch (Exception e) {
			LOG.error(" while downloading Buyer Address  template :: " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/paymentTransactionExcel", method = RequestMethod.GET)
	public void downloadPaymentTransactionExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("downloadPaymentTransactionExcel this method is called here");
		try {
			buyerSubscriptionService.downloadPaymentTransactionExcel(response, SecurityLibrary.getLoggedInUserTenantId());
		} catch (Exception e) {
			LOG.error(" while downloading Buyer Address  template :: " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/buyBuyerPlan", method = RequestMethod.GET)
	public String buyBuyerPlan(Model model, HttpSession session, RedirectAttributes redir) {
		try {
			List<BuyerPlan> buyerPlanList = buyerPlanService.findAllBuyerPlansByStatus(PlanStatus.ACTIVE);

			BuyerPlan evntPlan = buyerPlanService.findEventBasedBuyerPlansByStatus();
			BuyerPlan userPlan = buyerPlanService.findUserBasedBuyerPlansByStatus();

			model.addAttribute("evntPlan", evntPlan);
			model.addAttribute("userPlan", userPlan);

			model.addAttribute("planList", buyerPlanList);
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
			return "buyerPlanPage";

		} catch (Exception e) {
			LOG.error("Error while change subscription :" + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
			return "redirect:/buyer/billing/accountOverview";
		}
	}

	@RequestMapping(path = "/eventBasedBuyerPlan", method = RequestMethod.GET)
	public String buyerEventCheckout(Model model, RedirectAttributes redirect, @RequestParam(name = "payment_intent", required = false) String paymentStatus, HttpServletRequest request) {
		LOG.info("Event base Buyer Checkout called ");
		try {

			if (StringUtils.isNotBlank(paymentStatus)) {
				try {
					PaymentTransaction transaction = buyerSubscriptionStripeService.getPaymentTransactionByToken(paymentStatus);
					String msg = buyerSubscriptionStripeService.getPaymentStatus(paymentStatus);
					LOG.info(msg);
					if (msg.indexOf("Processing") != -1) {
						model.addAttribute("info", msg);
					} else {
						BuyerSubscription subscription = buyerSubscriptionService.getBuyerSubscriptionById(transaction.getBuyerSubscription().getId());
						request.setAttribute("changePlan", "true");
						model.addAttribute("subscription", subscription);
						// Not saving to DB just for display purposes. Webhook will update status
						transaction.setStatus(TransactionStatus.SUCCESS);
						model.addAttribute("paymentTransaction", transaction);
						model.addAttribute("buyer", buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId()));
						return "changePlanSuccess";
					}
				} catch (Exception e) {
					model.addAttribute("error", e.getMessage());
					LOG.info("Error in checking payment status " + e.getMessage());
				}
			}

			List<BuyerPlan> changePlanList = new ArrayList<BuyerPlan>();
			BuyerPlan buyerPlan = buyerPlanService.findEventBasedBuyerPlansByStatus();
			changePlanList.add(buyerPlan);
			Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("buyerCountry", buyer.getRegistrationOfCountry().getCountryCode());
			model.addAttribute("planList", changePlanList);
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
			model.addAttribute("publicKey", stripePublicKey);
		} catch (Exception e) {
			LOG.error("Error while buyer checkout : " + e.getMessage(), e);
			return "redirect:buyBuyerPlan";
		}

		/*
		 * LOG.info("Event base Buyer Checkout called "); try { BuyerPlan buyerPlan =
		 * buyerPlanService.findEventBasedBuyerPlansByStatus(); Buyer buyer = new Buyer();
		 * buyer.setRegistrationOfCountry(countryService.getCountryByCode("MY")); BuyerSubscription subscription = new
		 * BuyerSubscription(); subscription.setBuyer(buyer); subscription.setPlan(buyerPlan);
		 * model.addAttribute("buyer", subscription); model.addAttribute("countryList",
		 * countryService.getAllCountries()); model.addAttribute("buyerPlan", buyerPlan);
		 * model.addAttribute("merchantId", this.MERCHANT_ID); model.addAttribute("paypalEnvironment",
		 * this.PAYPAL_ENVIRONMENT); } catch (Exception e) { LOG.error("Error while buyer checkout : " + e.getMessage(),
		 * e); return "redirect:eventBasedBuyerCheckout"; }
		 */ // return "eventBasedBuyerCheckout";

		return "eventBasedBuyerPlan";
	}

	@RequestMapping(path = "/userBasedBuyerPlan", method = RequestMethod.GET)
	public String buyerCheckout(Model model, RedirectAttributes redirect, @RequestParam(name = "payment_intent", required = false) String paymentStatus, HttpServletRequest request) {
		LOG.info("User base Buyer Checkout called ");
		try {

			if (StringUtils.isNotBlank(paymentStatus)) {
				try {
					PaymentTransaction transaction = buyerSubscriptionStripeService.getPaymentTransactionByToken(paymentStatus);
					String msg = buyerSubscriptionStripeService.getPaymentStatus(paymentStatus);
					LOG.info(msg);
					if (msg.indexOf("Processing") != -1) {
						model.addAttribute("info", msg);
					} else {
						BuyerSubscription subscription = buyerSubscriptionService.getBuyerSubscriptionById(transaction.getBuyerSubscription().getId());
						request.setAttribute("changePlan", "true");
						model.addAttribute("subscription", subscription);
						// Not saving to DB just for display purposes. Webhook will update status
						transaction.setStatus(TransactionStatus.SUCCESS);
						model.addAttribute("paymentTransaction", transaction);
						model.addAttribute("buyer", buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId()));
						return "changePlanSuccess";
					}
				} catch (Exception e) {
					model.addAttribute("error", e.getMessage());
					LOG.info("Error in checking payment status " + e.getMessage());
				}
			}

			List<BuyerPlan> changePlanList = new ArrayList<BuyerPlan>();
			BuyerPlan buyerPlan = buyerPlanService.findUserBasedBuyerPlansByStatus();
			changePlanList.add(buyerPlan);
			Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("buyerCountry", buyer.getRegistrationOfCountry().getCountryCode());
			model.addAttribute("planList", changePlanList);
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
			model.addAttribute("publicKey", stripePublicKey);
		} catch (Exception e) {
			LOG.error("Error while buyer checkout : " + e.getMessage(), e);
			return "redirect:buyBuyerPlan";
		}
		return "userBasedBuyerPlan";
	}

	// @RequestMapping(path = "/buyBuyerPlan/{planId}", method = RequestMethod.POST)
	// public String buyBuyerPlan(@RequestParam(required = false, value = "immediateEffect") boolean immediateEffect,
	// @RequestParam("userQuantity") String numberUserEvent, @RequestParam(required = false, value = "periodId") String
	// periodId, @RequestParam(value = "range.id") String rangeId, @RequestParam(required = false, value =
	// "promoCodeId") String promoCodeId, @RequestParam(value = "eventPrice") BigDecimal feeValue,
	// @RequestParam(required = false, value = "subscriptionDiscountPrice") BigDecimal feeDiscountValue,
	// @RequestParam(required = false, value = "promoCodeDiscount") BigDecimal promoCodeDiscount, @RequestParam(value =
	// "totalFeeAmount") BigDecimal totalFeeAmount, @PathVariable(name = "planId") String planId, @RequestParam(required
	// = false, value = "autoChargeSubscription") boolean autoChargeSubscription, Model model, HttpSession session,
	// RedirectAttributes redir) {
	// // PH 150 issue
	// // public String changeBuyerPlan(@RequestParam(required = false, value = "immediateEffect") boolean
	// // immediateEffect, @RequestParam("numberUserEvent") String numberUserEvent, @RequestParam(required = false,
	// // value = "periodId") String periodId, @RequestParam(value = "rangeId") String rangeId, @RequestParam(required
	// // = false, value = "promoCodeId") String promoCodeId, @RequestParam(value = "feeValue") BigDecimal feeValue,
	// // @RequestParam(required = false, value = "feeDiscountValue") BigDecimal feeDiscountValue,
	// // @RequestParam(required = false, value = "promoCodeDiscount") BigDecimal promoCodeDiscount,
	// // @RequestParam(value = "totalFeeAmount") BigDecimal totalFeeAmount, @PathVariable(name = "planId") String
	// // planId, @RequestParam(required = false, value = "autoChargeSubscription") boolean autoChargeSubscription,
	// // Model model, HttpSession session, RedirectAttributes redir) {
	// LOG.info("Auto Charge Subscription :" + autoChargeSubscription);
	// LOG.info("User requested to change buyer Plan for plan id : " + planId);
	// LOG.info("User requested to immediateEffect : " + immediateEffect);
	// LOG.info("User requested to numberUserEvent : " + numberUserEvent);
	// LOG.info("User requested to feeValue : " + feeValue);
	// LOG.info("User requested to feeDiscountValue : " + feeDiscountValue);
	// LOG.info("User requested to totalFeeAmount : " + totalFeeAmount);
	// LOG.info("User requested to promoCodeId : " + promoCodeId);
	// LOG.info("User requested to promoCodeDiscount : " + promoCodeDiscount);
	// LOG.info("User requested to rangeId : " + rangeId);
	// // LOG.info("User requested to periodId : " + periodId);
	// try {

	// if (totalFeeAmount.compareTo(new BigDecimal(0)) <= 0) {
	// throw new ApplicationException("Total fee should be greater then Zero");
	// }
	// BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
	// PaymentTransaction paymentTransaction = new PaymentTransaction();

	// BigDecimal totalPrice = BigDecimal.ZERO;
	// BigDecimal basePrice = BigDecimal.ZERO;
	// BigDecimal addtionalUserPrice = BigDecimal.ZERO;
	// BigDecimal subscriptionDiscountPrice = BigDecimal.ZERO;
	// BigDecimal promoDiscountPrice = BigDecimal.ZERO;
	// BigDecimal priceAmount = BigDecimal.ZERO;
	// PromotionalCode promoCode = null;

	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
	// // cross checking for promo code
	// try {
	// promoCode = promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(),
	// SecurityLibrary.getLoggedInUserTenantId());
	// // LOG.info("Promo code :" + promotionalCode.toLogString());
	// } catch (Exception e) {
	// throw e;
	// }
	// paymentTransaction.setPromoCode(promotionalCode);
	// }

	// if (plan.getPlanType() == PlanType.PER_USER) {
	// basePrice = getBasePrice(plan, periodId);
	// priceAmount = basePrice;
	// if (numberUserEvent != null) {
	// Integer users = new Integer(numberUserEvent);
	// LOG.info("users: " + users);

	// if (users < 0) {
	// throw new ApplicationException("User quantity not be less than zero");
	// }
	// addtionalUserPrice = getAdditionalUserprice(users, plan, basePrice, periodId);

	// LOG.info("addtionalUserPrice: " + addtionalUserPrice);

	// BigDecimal totalUserPrice = addtionalUserPrice.add(basePrice).setScale(2, BigDecimal.ROUND_HALF_UP);
	// totalPrice = totalUserPrice;
	// subscriptionDiscountPrice = getSubscriptionDiscountPrice(periodId, plan, totalUserPrice);

	// LOG.info("subscriptionDiscountPrice: " + subscriptionDiscountPrice);
	// LOG.info("totalUserPrice: " + totalUserPrice);
	// }

	// promoDiscountPrice = getPromoDiscount(promoCode, totalPrice);
	// totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
	// totalPrice = totalPrice.subtract(promoDiscountPrice);
	// LOG.info("after calculation basePrice:" + basePrice + " addtionalUserPrice: " + addtionalUserPrice +
	// "subscriptionDiscountPrice:" + subscriptionDiscountPrice + " promoDiscountPrice : " + promoDiscountPrice + "
	// totalPrice " + totalPrice);

	// // totalPrice = totalPrice.add(addtionalUserPrice);

	// } else {

	// Integer event = new Integer(numberUserEvent);
	// priceAmount = getEventprice(plan);
	// PlanRange range = null;
	// if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
	// // User did not change the range. Then default it to the first one.
	// if (StringUtils.checkString(rangeId).length() == 0) {
	// range = plan.getRangeList().get(0);
	// } else {
	// for (PlanRange pRange : plan.getRangeList()) {
	// if (pRange.getId().equals(rangeId)) {
	// range = pRange;
	// }
	// }
	// }
	// }

	// LOG.info("============> range.getPrice():" + range.getPrice());
	// LOG.info("============>" + event);

	// totalPrice = range.getPrice().multiply(new BigDecimal(event));

	// LOG.info("after calculation totalPrice:" + totalPrice);

	// LOG.info("after calculation totalPrice:" + priceAmount);
	// // if (subscriptionDiscountPrice == null) {
	// subscriptionDiscountPrice = BigDecimal.ZERO;
	// // }
	// subscriptionDiscountPrice = getPromoDiscount(promoCode, totalPrice);

	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// promoDiscountPrice = subscriptionDiscountPrice;
	// }
	// totalPrice = totalPrice.subtract(subscriptionDiscountPrice);

	// }

	// LOG.info("User requested to totalPrice : " + totalPrice + "============>" + totalFeeAmount);
	// LOG.info("User requested to priceAmount : " + priceAmount + "===============>" + feeValue);
	// LOG.info("User requested to subscriptionDiscountPrice : " + subscriptionDiscountPrice + "=============>" +
	// feeDiscountValue);
	// LOG.info("User requested to promoCodeDiscount : " + promoCodeDiscount);
	// LOG.info("User requested to rangeId : " + rangeId);

	// BuyerSubscription subscription = new BuyerSubscription();
	// subscription.setPlan(plan);
	// subscription.setImmediateEffect(false);
	// subscription.setPlanType(plan.getPlanType());
	// subscription.setPriceAmount(priceAmount);
	// subscription.setPriceDiscount(subscriptionDiscountPrice);
	// subscription.setTotalPriceAmount(totalPrice);
	// subscription.setImmediateEffect(immediateEffect);
	// subscription.setAutoChargeSubscription(autoChargeSubscription);

	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// subscription.setPromoCodeDiscount(subscriptionDiscountPrice);
	// } else {
	// subscription.setPromoCodeDiscount(new BigDecimal(0));
	// }
	// if (subscription.getPlanType() == PlanType.PER_USER) {

	// if (StringUtils.checkString(numberUserEvent).length() == 0) {
	// subscription.setUserQuantity(3);
	// } else {
	// subscription.setUserQuantity(plan.getBaseUsers() + new Integer(numberUserEvent));
	// }

	// subscription.setEventQuantity(9999); // set default 9999 max event on user based
	// } else if (subscription.getPlanType() == PlanType.PER_EVENT) {
	// subscription.setEventQuantity(new Integer(numberUserEvent));
	// subscription.setUserQuantity(999); // set default 999 max user on event based
	// }

	// PlanRange range = null;
	// if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
	// // User did not change the range. Then default it to the first one.
	// LOG.info("User requested to rangeId ");
	// if (StringUtils.checkString(rangeId).length() == 0) {
	// range = plan.getRangeList().get(0);
	// LOG.info("User requested to rangeId ");
	// } else {
	// for (PlanRange pRange : plan.getRangeList()) {
	// LOG.info("User requested to rangeId" + pRange.getId() + "/" + rangeId);
	// if (pRange.getId().equals(rangeId)) {
	// range = pRange;
	// LOG.info("User requested to rangeId ");
	// }

	// }
	// }
	// }

	// subscription.setRange(range);
	// PlanPeriod period = null;
	// if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
	// for (PlanPeriod prd : plan.getPlanPeriodList()) {
	// if (prd.getId().equals(periodId)) {
	// period = prd;
	// }
	// }
	// }
	// subscription.setPlanPeriod(period);
	// Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());

	// paymentTransaction.setCountry(buyer.getRegistrationOfCountry());
	// paymentTransaction.setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
	// paymentTransaction.setBuyer(buyer);
	// if (StringUtils.checkString(promoCodeId).length() > 0) {
	// PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);

	// // cross checking for promo code
	// try {
	// promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(),
	// SecurityLibrary.getLoggedInUserTenantId());
	// // LOG.info("Promo code :" + promotionalCode.toLogString());
	// } catch (Exception e) {
	// throw e;
	// }
	// paymentTransaction.setPromoCode(promotionalCode);
	// }
	// subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
	// subscription.setBuyer(buyer);
	// String returnURL = appUrl + "/buyer/billing/confirmSuccessChangePlan?planId=" + planId + "&txId=";
	// String cancelURL = appUrl + "/buyer/billing/cancel?planId=" + planId + "&txId=";
	// return buyerSubscriptionService.initiatePaypalPaymentForBuyBuyerSubscription(subscription, planId, session,
	// returnURL, cancelURL);
	// } catch (Exception e) {
	// LOG.error("Error while change subscription :" + e.getMessage(), e);
	// redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.change.subscription", new Object[] {
	// e.getMessage() }, Global.LOCALE));
	// return "redirect:/buyer/billing/changeBuyerPlan/" + planId;
	// }
	// }

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

	/**
	 * @param noOfUser
	 * @param plan
	 * @param basePrice
	 * @param result TODO
	 * @return
	 */
	public BigDecimal getAdditionalUserprice(Integer noOfUser, BuyerPlan plan, BigDecimal basePrice, String periodId) {
		BigDecimal addtionalUserPrice = BigDecimal.ZERO;
		if (plan.getBaseUsers() != null) {
			if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
				for (PlanRange range : plan.getRangeList()) {

					if ((noOfUser + plan.getBaseUsers()) >= range.getRangeStart() && (noOfUser + plan.getBaseUsers()) <= range.getRangeEnd()) {
						addtionalUserPrice = range.getPrice();
						LOG.info("range Id: " + range.getId() + " ==== addtionalUserPrice: " + addtionalUserPrice);
						break;
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

	public BigDecimal getPromoDiscount(PromotionalCode promo, BigDecimal totalPrice, BuyerPlan plan) throws Exception {
		BigDecimal promoDiscountPrice = BigDecimal.ZERO;
		LOG.info("Getting discount for amount: " + totalPrice);
		if (promo != null) {
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

	private BigDecimal getEventprice(BuyerPlan plan) {
		BigDecimal eventsPrice = BigDecimal.ZERO;
		if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
			PlanRange range = plan.getRangeList().get(0);
			eventsPrice = range.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		return eventsPrice;
	}

	@RequestMapping(path = "/updateSubscription/{subsId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PaymentIntentPojo> doUpdateSubscriptionInitiate(@RequestParam("updateNumberUserEvent") String numberUserEvent, @RequestParam("chargeMonths") String chargeMonths, @RequestParam(required = false, value = "updatePeriodId") String periodId, @RequestParam(value = "updateRangeId") String rangeId, @RequestParam(required = false, value = "updatePromoCodeId") String promoCodeId, @RequestParam(value = "updateFeeValue") BigDecimal feeValue, @RequestParam(required = false, value = "updateFeeDiscountValue") BigDecimal feeDiscountValue, @RequestParam(required = false, value = "updatePromoCodeDiscount") BigDecimal promoCodeDiscount, @RequestParam(value = "updateTotalFeeAmount") BigDecimal totalFeeAmount, @PathVariable(name = "subsId") String subsId, @RequestParam(name = "promocode") String promocode, @RequestParam(name = "mode") String paymentMode) {
		LOG.info("Buyer requested to upgrade subscription with subscription ID: " + subsId);
		HttpHeaders headers = new HttpHeaders();
		try {
			BuyerSubscription subscription = buyerSubscriptionService.getBuyerSubscriptionById(subsId);
			PaymentTransaction paymentTransaction = new PaymentTransaction();

			paymentTransaction.setPriceAmount(feeValue);
			paymentTransaction.setPriceDiscount(feeDiscountValue);
			paymentTransaction.setTotalPriceAmount(totalFeeAmount);
			paymentTransaction.setAmount(totalFeeAmount);
			if (totalFeeAmount.compareTo(new BigDecimal(0)) <= 0) {
				throw new ApplicationException("Total fee should be greater then Zero");
			}
			if (StringUtils.checkString(promoCodeId).length() > 0) {
				paymentTransaction.setPromoCodeDiscount(promoCodeDiscount);
			} else {
				paymentTransaction.setPromoCodeDiscount(new BigDecimal(0));
			}
			if (subscription.getPlanType() == PlanType.PER_USER) {
				paymentTransaction.setUserQuantity(new Integer(numberUserEvent));
				paymentTransaction.setEventQuantity(9999); // set default 9999 max event on user based
			} else if (subscription.getPlanType() == PlanType.PER_EVENT) {
				paymentTransaction.setEventQuantity(new Integer(numberUserEvent));
				paymentTransaction.setUserQuantity(999); // set default 999 max user on event based
			}
			PlanRange range = null;
			if (CollectionUtil.isNotEmpty(subscription.getPlan().getRangeList())) {
				for (PlanRange pRange : subscription.getPlan().getRangeList()) {
					if (pRange.getId().equals(rangeId)) {
						range = pRange;
					}
				}
			}
			subscription.setRange(range);
			Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
			paymentTransaction.setCountry(buyer.getRegistrationOfCountry());
			paymentTransaction.setCompanyName(buyer.getCompanyName());
			paymentTransaction.setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
			paymentTransaction.setBuyer(buyer);
			if (StringUtils.checkString(promoCodeId).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
				try {
					promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(), SecurityLibrary.getLoggedInUserTenantId());
				} catch (Exception e) {
					throw e;
				}
				paymentTransaction.setPromoCode(promotionalCode);
			}

			if (subscription.getPlanPeriod() != null) {
				subscription.getPlanPeriod().setPlanDuration(new Integer(chargeMonths));
			}
			subscription.getPaymentTransactions().add(paymentTransaction);
			subscription.setPaymentTransactions(subscription.getPaymentTransactions());
			return new ResponseEntity<PaymentIntentPojo>(buyerSubscriptionStripeService.initiateStripePaymentForUpdateBuyerSubscription(subscription, subscription.getPlan().getId(), paymentTransaction, new Integer(numberUserEvent), promocode, paymentMode), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while upgrading subscription :" + e.getMessage(), e);
			headers.add("error", e.getLocalizedMessage());
			return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/renew/{planId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PaymentIntentPojo> doRenewInitiate(@RequestParam("numberUserEvent") String numberUserEvent, @RequestParam(required = false, value = "periodId") String periodId, @RequestParam(value = "rangeId") String rangeId, @RequestParam(required = false, value = "promoCodeId") String promoCodeId, @RequestParam(value = "feeValue") BigDecimal feeValue, @RequestParam(required = false, value = "feeDiscountValue") BigDecimal feeDiscountValue, @RequestParam(required = false, value = "promoCodeDiscount") BigDecimal promoCodeDiscount, @RequestParam(value = "totalFeeAmount") BigDecimal totalFeeAmount, @PathVariable(name = "planId") String planId, @RequestParam(required = false, value = "autoChargeSubscription") boolean autoChargeSubscription, @RequestParam(required = false, value = "mode") String paymentMode) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("User requested to renew plan " + planId + " with autocharge as " + autoChargeSubscription);
		try {
			if (totalFeeAmount.compareTo(new BigDecimal(0)) <= 0) {
				throw new ApplicationException("Total fee should be greater then Zero");
			}
			BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);

			BigDecimal totalPrice = BigDecimal.ZERO;
			BigDecimal basePrice = BigDecimal.ZERO;
			BigDecimal addtionalUserPrice = BigDecimal.ZERO;
			BigDecimal subscriptionDiscountPrice = BigDecimal.ZERO;
			BigDecimal promoDiscountPrice = BigDecimal.ZERO;
			BigDecimal priceAmount = BigDecimal.ZERO;
			PromotionalCode promoCode = null;
			PaymentTransaction paymentTransaction = new PaymentTransaction();
			if (StringUtils.checkString(promoCodeId).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
				try {
					promoCode = promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(), SecurityLibrary.getLoggedInUserTenantId());
				} catch (Exception e) {
					throw new ApplicationException(e.getLocalizedMessage());
				}
				paymentTransaction.setPromoCode(promotionalCode);
			}

			BigDecimal totalTax = new BigDecimal(0);
			Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
			BigDecimal tax = BigDecimal.ZERO;
			if (tax == null) {
				tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				if (buyer.getRegistrationOfCountry() != null && "MY".equals(buyer.getRegistrationOfCountry().getCountryCode())) {
					tax = plan.getTax();
					tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
				} else {
					tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
				}
			}

			if (plan.getPlanType() == PlanType.PER_USER) {
				basePrice = getBasePrice(plan, periodId);
				priceAmount = basePrice;
				if (numberUserEvent != null) {
					Integer users = new Integer(numberUserEvent);
					LOG.info("users: " + users);

					if (users < 0) {
						throw new ApplicationException("User quantity not be less than zero");
					} else if (users > 3) {
						users = users - 3;
					} else {
						users = 0;
					}
					addtionalUserPrice = getAdditionalUserprice(users, plan, basePrice, periodId);
					BigDecimal totalUserPrice = addtionalUserPrice.add(basePrice).setScale(2, BigDecimal.ROUND_HALF_UP);
					totalPrice = totalUserPrice;
					subscriptionDiscountPrice = getSubscriptionDiscountPrice(periodId, plan, totalUserPrice);
				}

				priceAmount = totalPrice;
				totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
				promoDiscountPrice = getPromoDiscount(promoCode, priceAmount, plan);
				totalPrice = totalPrice.subtract(promoDiscountPrice);
				LOG.info("totalPrice" + totalPrice);
				totalTax = (totalPrice.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				LOG.info("totalTax :" + totalTax);
				paymentTransaction.setAdditionalTax(totalTax);
				LOG.info("After calculation prices are basePrice:" + basePrice + " addtionalUserPrice: " + addtionalUserPrice + "subscriptionDiscountPrice: " + subscriptionDiscountPrice + " promoDiscountPrice: " + promoDiscountPrice + "  totalPrice: " + totalPrice);
			} else {

				Integer event = new Integer(numberUserEvent);
				priceAmount = getEventprice(plan);
				PlanRange range = null;
				if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
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
				totalPrice = range.getPrice().multiply(new BigDecimal(event));
				priceAmount = totalPrice;
				subscriptionDiscountPrice = BigDecimal.ZERO;
				subscriptionDiscountPrice = getPromoDiscount(promoCode, totalPrice, plan);
				if (StringUtils.checkString(promoCodeId).length() > 0) {
					promoDiscountPrice = subscriptionDiscountPrice;
				}
				totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
				totalTax = (totalPrice.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				LOG.info("totalTax :" + totalTax);
				paymentTransaction.setAdditionalTax(totalTax);

			}

			BuyerSubscription subscription = new BuyerSubscription();
			subscription.setPlan(plan);
			subscription.setPlanType(plan.getPlanType());

			paymentTransaction.setPriceAmount(priceAmount);
			paymentTransaction.setPriceDiscount(subscriptionDiscountPrice);
			paymentTransaction.setTotalPriceAmount(totalPrice.add(totalTax));
			paymentTransaction.setAmount(totalPrice);

			subscription.setPriceAmount(priceAmount);
			subscription.setPriceDiscount(subscriptionDiscountPrice);
			subscription.setTotalPriceAmount(totalPrice.add(totalTax));
			subscription.setAutoChargeSubscription(autoChargeSubscription);

			if (StringUtils.checkString(promoCodeId).length() > 0) {
				paymentTransaction.setPromoCodeDiscount(promoDiscountPrice);
				subscription.setPromoCodeDiscount(promoDiscountPrice);
			} else {
				paymentTransaction.setPromoCodeDiscount(new BigDecimal(0));
				subscription.setPromoCodeDiscount(new BigDecimal(0));
			}
			if (subscription.getPlanType() == PlanType.PER_USER) {
				paymentTransaction.setUserQuantity(new Integer(numberUserEvent));
				paymentTransaction.setEventQuantity(9999); // set default 9999 max event on user based
				subscription.setEventQuantity(9999); // set default 9999 max event on user based
			} else if (subscription.getPlanType() == PlanType.PER_EVENT) {
				paymentTransaction.setEventQuantity(new Integer(numberUserEvent));
				paymentTransaction.setUserQuantity(999); // set default 999 max user on event based
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
			LOG.info("Package..." + buyer.getBuyerPackage().getUserLimit() + "-" + buyer.getBuyerPackage().getEventLimit());
			paymentTransaction.setCountry(buyer.getRegistrationOfCountry());
			paymentTransaction.setCompanyName(buyer.getCompanyName());
			paymentTransaction.setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
			paymentTransaction.setBuyer(buyer);
			if (StringUtils.checkString(promoCodeId).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
				try {
					promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(), SecurityLibrary.getLoggedInUserTenantId());
				} catch (Exception e) {
					throw new ApplicationException(e.getLocalizedMessage());
				}
				paymentTransaction.setPromoCode(promotionalCode);
			}
			subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
			subscription.setBuyer(buyer);
			return new ResponseEntity<PaymentIntentPojo>(buyerSubscriptionStripeService.initiateStripePaymentForBuyerSubscription(subscription, planId, paymentMode, true), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while renew subscription :" + e.getMessage(), e);
			headers.add("error", e.getLocalizedMessage());
			return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/changeBuyerPlan/{planId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PaymentIntentPojo> changeBuyerPlan(@RequestParam(required = false, value = "immediateEffect") boolean immediateEffect, @RequestParam("numberUserEvent") String numberUserEvent, @RequestParam(required = false, value = "periodId") String periodId, @RequestParam(value = "rangeId") String rangeId, @RequestParam(required = false, value = "promoCodeId") String promoCodeId, @RequestParam(value = "feeValue") BigDecimal feeValue, @RequestParam(required = false, value = "feeDiscountValue") BigDecimal feeDiscountValue, @RequestParam(required = false, value = "promoCodeDiscount") BigDecimal promoCodeDiscount, @RequestParam(value = "totalFeeAmount") BigDecimal totalFeeAmount, @PathVariable(name = "planId") String planId, @RequestParam(required = false, value = "autoChargeSubscription") boolean autoChargeSubscription, @RequestParam(value = "mode") String mode) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("Buyer requested to change plan with plan ID: " + planId);
		try {
			if (totalFeeAmount.compareTo(new BigDecimal(0)) <= 0) {
				throw new ApplicationException("Total fee should be greater then Zero");
			}
			BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
			PaymentTransaction paymentTransaction = new PaymentTransaction();

			BigDecimal totalPrice = BigDecimal.ZERO;
			BigDecimal basePrice = BigDecimal.ZERO;
			BigDecimal addtionalUserPrice = BigDecimal.ZERO;
			BigDecimal subscriptionDiscountPrice = BigDecimal.ZERO;
			BigDecimal promoDiscountPrice = BigDecimal.ZERO;
			BigDecimal priceAmount = BigDecimal.ZERO;
			PromotionalCode promoCode = null;

			BigDecimal totalTax = new BigDecimal(0);
			Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
			BigDecimal tax = BigDecimal.ZERO;
			if (tax == null) {
				tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				if (buyer.getRegistrationOfCountry() != null && "MY".equals(buyer.getRegistrationOfCountry().getCountryCode())) {
					tax = plan.getTax();
					tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
				} else {
					tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
				}
			}

			if (StringUtils.checkString(promoCodeId).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
				try {
					promoCode = promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(), SecurityLibrary.getLoggedInUserTenantId());
				} catch (Exception e) {
					throw new ApplicationException(e.getLocalizedMessage());
				}
				paymentTransaction.setPromoCode(promotionalCode);
			}

			if (plan.getPlanType() == PlanType.PER_USER) {
				basePrice = getBasePrice(plan, periodId);

				if (numberUserEvent != null) {
					Integer users = new Integer(numberUserEvent);
					if (users < 0) {
						throw new ApplicationException("User quantity not be less than zero");
					} else if (users > 3) {
						users = users - 3;
					} else {
						users = 0;
					}
					addtionalUserPrice = getAdditionalUserprice(users, plan, basePrice, periodId);
					BigDecimal totalUserPrice = addtionalUserPrice.add(basePrice).setScale(2, BigDecimal.ROUND_HALF_UP);
					totalPrice = totalUserPrice;
					subscriptionDiscountPrice = getSubscriptionDiscountPrice(periodId, plan, totalUserPrice);
				}

				priceAmount = totalPrice;
				totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
				promoDiscountPrice = getPromoDiscount(promoCode, priceAmount, plan);
				totalPrice = totalPrice.subtract(promoDiscountPrice);
				totalTax = (totalPrice.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				LOG.info("totalTax :" + totalTax);
				paymentTransaction.setAdditionalTax(totalTax);
				LOG.info("After calculation prices are basePrice: " + basePrice + " addtionalUserPrice: " + addtionalUserPrice + "subscriptionDiscountPrice: " + subscriptionDiscountPrice + " promoDiscountPrice: " + promoDiscountPrice + "  totalPrice: " + totalPrice);
			} else {
				basePrice = getBasePrice(plan, periodId);
				Integer event = new Integer(numberUserEvent);
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
				totalPrice = range.getPrice().multiply(new BigDecimal(event));
				priceAmount = totalPrice;
				subscriptionDiscountPrice = BigDecimal.ZERO;

				promoDiscountPrice = getPromoDiscount(promoCode, totalPrice, plan);
				if (StringUtils.checkString(promoCodeId).length() > 0) {
					totalPrice = totalPrice.subtract(promoDiscountPrice);
				}
				totalTax = (totalPrice.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				LOG.info("totalTax :" + totalTax);
				paymentTransaction.setAdditionalTax(totalTax);

			}
			BuyerSubscription subscription = new BuyerSubscription();
			subscription.setPlan(plan);
			subscription.setPlanType(plan.getPlanType());

			paymentTransaction.setPriceAmount(priceAmount);
			paymentTransaction.setPriceDiscount(subscriptionDiscountPrice);
			paymentTransaction.setTotalPriceAmount(totalPrice.add(totalTax));
			paymentTransaction.setAmount(totalPrice);

			// Saving it in subscription as well as it is a new subscription
			subscription.setPriceAmount(priceAmount);
			subscription.setPriceDiscount(subscriptionDiscountPrice);
			subscription.setTotalPriceAmount(totalPrice.add(totalTax));
			subscription.setImmediateEffect(immediateEffect);
			subscription.setAutoChargeSubscription(autoChargeSubscription);

			if (StringUtils.checkString(promoCodeId).length() > 0) {
				paymentTransaction.setPromoCodeDiscount(promoDiscountPrice);
				subscription.setPromoCodeDiscount(promoDiscountPrice);
			} else {
				paymentTransaction.setPromoCodeDiscount(new BigDecimal(0));
				subscription.setPromoCodeDiscount(new BigDecimal(0));
			}
			if (subscription.getPlanType() == PlanType.PER_USER) {
				paymentTransaction.setUserQuantity(new Integer(numberUserEvent));
				paymentTransaction.setEventQuantity(9999); // set default 9999 max event on user based
			} else if (subscription.getPlanType() == PlanType.PER_EVENT) {
				paymentTransaction.setEventQuantity(new Integer(numberUserEvent));
				paymentTransaction.setUserQuantity(999); // set default 999 max user on event based
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
			paymentTransaction.setCountry(buyer.getRegistrationOfCountry());
			paymentTransaction.setCompanyName(buyer.getCompanyName());
			paymentTransaction.setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
			paymentTransaction.setBuyer(buyer);
			subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
			subscription.setBuyer(buyer);
			return new ResponseEntity<PaymentIntentPojo>(buyerSubscriptionStripeService.initiateStripePaymentForBuyerSubscriptionChangePlan(subscription, planId, mode), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while changing subscription :" + e.getMessage(), e);
			headers.add("error", e.getLocalizedMessage());
			return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/buyBuyerPlan/{planId}", method = RequestMethod.POST)
	@ResponseBody
	ResponseEntity<PaymentIntentPojo> buyBuyerPlan(@RequestParam(required = false, value = "immediateEffect") boolean immediateEffect, @RequestParam("numberUserEvent") String numberUserEvent, @RequestParam(required = false, value = "periodId") String periodId, @RequestParam(value = "rangeId") String rangeId, @RequestParam(required = false, value = "promoCodeId") String promoCodeId, @RequestParam(value = "feeValue") BigDecimal feeValue, @RequestParam(required = false, value = "feeDiscountValue") BigDecimal feeDiscountValue, @RequestParam(required = false, value = "promoCodeDiscount") BigDecimal promoCodeDiscount, @RequestParam(value = "totalFeeAmount") BigDecimal totalFeeAmount, @PathVariable(name = "planId") String planId, @RequestParam(required = false, value = "autoChargeSubscription") boolean autoChargeSubscription, @RequestParam(value = "mode") String mode) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("Buyer requested to buy plan with ID: " + planId);
		try {

			if (totalFeeAmount.compareTo(new BigDecimal(0)) <= 0) {
				throw new ApplicationException("Total fee should be greater then Zero");
			}
			BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
			PaymentTransaction paymentTransaction = new PaymentTransaction();

			BigDecimal totalPrice = BigDecimal.ZERO;
			BigDecimal basePrice = BigDecimal.ZERO;
			BigDecimal addtionalUserPrice = BigDecimal.ZERO;
			BigDecimal subscriptionDiscountPrice = BigDecimal.ZERO;
			BigDecimal promoDiscountPrice = BigDecimal.ZERO;
			BigDecimal priceAmount = BigDecimal.ZERO;
			PromotionalCode promoCode = null;

			if (StringUtils.checkString(promoCodeId).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
				try {
					promoCode = promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(), SecurityLibrary.getLoggedInUserTenantId());
				} catch (Exception e) {
					throw e;
				}
				paymentTransaction.setPromoCode(promotionalCode);
			}

			BigDecimal totalTax = new BigDecimal(0);
			Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
			BigDecimal tax = BigDecimal.ZERO;
			if (tax == null) {
				tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				if (buyer.getRegistrationOfCountry() != null && "MY".equals(buyer.getRegistrationOfCountry().getCountryCode())) {
					tax = plan.getTax();
					tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
				} else {
					tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
				}
			}

			if (plan.getPlanType() == PlanType.PER_USER) {
				basePrice = getBasePrice(plan, periodId);
				priceAmount = basePrice;
				if (numberUserEvent != null) {
					Integer users = new Integer(numberUserEvent);
					LOG.info("users: " + users);

					if (users < 0) {
						throw new ApplicationException("User quantity not be less than zero");
					}
					addtionalUserPrice = getAdditionalUserprice(users, plan, basePrice, periodId);
					BigDecimal totalUserPrice = addtionalUserPrice.add(basePrice).setScale(2, BigDecimal.ROUND_HALF_UP);
					totalPrice = totalUserPrice;
					subscriptionDiscountPrice = getSubscriptionDiscountPrice(periodId, plan, totalUserPrice);
				}

				priceAmount = totalPrice;
				LOG.info("priceAmount :" + priceAmount);
				totalPrice = totalPrice.subtract(subscriptionDiscountPrice);
				totalTax = (totalPrice.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				LOG.info("totalTax :" + totalTax);
				paymentTransaction.setAdditionalTax(totalTax);
				promoDiscountPrice = getPromoDiscount(promoCode, priceAmount, plan);
				totalPrice = totalPrice.subtract(promoDiscountPrice);
				LOG.info("After calculation prices are basePrice: " + basePrice + " addtionalUserPrice: " + addtionalUserPrice + "subscriptionDiscountPrice: " + subscriptionDiscountPrice + " promoDiscountPrice: " + promoDiscountPrice + "  totalPrice: " + totalPrice);
			} else {
				Integer event = new Integer(numberUserEvent);
				priceAmount = getEventprice(plan);
				PlanRange range = null;
				if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
					// User did not change the range. Then default it to the first one.
					if (StringUtils.isBlank(rangeId) || StringUtils.equals("null", rangeId) || StringUtils.checkString(rangeId).length() == 0) {
						range = plan.getRangeList().get(0);
					} else {
						for (PlanRange pRange : plan.getRangeList()) {
							if (pRange.getId().equals(rangeId)) {
								range = pRange;
							}
						}
					}
				}
				totalPrice = range.getPrice().multiply(new BigDecimal(event));
				priceAmount = totalPrice;
				totalTax = (totalPrice.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				LOG.info("totalTax :" + totalTax);
				paymentTransaction.setAdditionalTax(totalTax);
				promoDiscountPrice = getPromoDiscount(promoCode, priceAmount, plan);
				totalPrice = totalPrice.subtract(promoDiscountPrice);
			}

			BuyerSubscription subscription = new BuyerSubscription();
			subscription.setPlan(plan);
			subscription.setImmediateEffect(false);
			subscription.setPlanType(plan.getPlanType());
			subscription.setPriceAmount(priceAmount);
			subscription.setPriceDiscount(subscriptionDiscountPrice);
			subscription.setTotalPriceAmount(totalPrice.add(totalTax));
			paymentTransaction.setTotalPriceAmount(totalPrice.add(totalTax));
			paymentTransaction.setAmount(totalPrice);
			subscription.setImmediateEffect(immediateEffect);
			subscription.setAutoChargeSubscription(autoChargeSubscription);

			paymentTransaction.setPriceAmount(priceAmount);
			paymentTransaction.setPriceDiscount(subscriptionDiscountPrice);

			if (StringUtils.checkString(promoCodeId).length() > 0) {
				subscription.setPromoCodeDiscount(promoDiscountPrice);
				paymentTransaction.setPromoCodeDiscount(promoDiscountPrice);
			} else {
				subscription.setPromoCodeDiscount(new BigDecimal(0));
				paymentTransaction.setPromoCodeDiscount(new BigDecimal(0));
			}
			if (subscription.getPlanType() == PlanType.PER_USER) {

				if (StringUtils.checkString(numberUserEvent).length() == 0) {
					subscription.setUserQuantity(3);
					paymentTransaction.setUserQuantity(3);
				} else {
					subscription.setUserQuantity(plan.getBaseUsers() + new Integer(numberUserEvent));
					paymentTransaction.setUserQuantity(new Integer(numberUserEvent));
				}

				subscription.setEventQuantity(9999); // set default 9999 max event on user based
				paymentTransaction.setEventQuantity(9999); // set default 9999 max event on user based
			} else if (subscription.getPlanType() == PlanType.PER_EVENT) {
				subscription.setEventQuantity(new Integer(numberUserEvent));
				paymentTransaction.setEventQuantity(new Integer(numberUserEvent));
				subscription.setUserQuantity(999); // set default 999 max user on event based
				paymentTransaction.setUserQuantity(999); // set default 999 max user on event based
			}

			PlanRange range = null;
			if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
				if (StringUtils.isBlank(rangeId) || StringUtils.equals("null", rangeId) || StringUtils.checkString(rangeId).length() == 0) {
					range = plan.getRangeList().get(0);
				} else {
					for (PlanRange pRange : plan.getRangeList()) {
						LOG.info("User requested to rangeId" + pRange.getId() + "/" + rangeId);
						if (pRange.getId().equals(rangeId)) {
							range = pRange;
						}

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

			paymentTransaction.setCountry(buyer.getRegistrationOfCountry());
			paymentTransaction.setCompanyName(buyer.getCompanyName());
			paymentTransaction.setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
			paymentTransaction.setBuyer(buyer);
			if (StringUtils.checkString(promoCodeId).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(promoCodeId);
				try {
					promotionalCodeService.checkPromotionalCodeByPromoCode(promotionalCode.getPromoCode(), SecurityLibrary.getLoggedInUserTenantId());
				} catch (Exception e) {
					throw e;
				}
				paymentTransaction.setPromoCode(promotionalCode);
			}
			subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
			subscription.setBuyer(buyer);
			return new ResponseEntity<PaymentIntentPojo>(buyerSubscriptionStripeService.initiateStripePaymentForBuyerSubscription(subscription, planId, mode, false), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while change subscription :" + e.getMessage(), e);
			headers.add("error", e.getLocalizedMessage());
			return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
