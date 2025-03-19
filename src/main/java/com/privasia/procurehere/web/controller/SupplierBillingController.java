package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.PaymentTransactionService;
import com.privasia.procurehere.service.PromotionalCodeService;
import com.privasia.procurehere.service.SubscriptionService;
import com.privasia.procurehere.service.SupplierPlanService;
import com.privasia.procurehere.service.SupplierSubscriptionStripeService;
import com.privasia.procurehere.service.impl.SubscriptionServiceImpl;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author parveen
 */
@Controller
@RequestMapping(value = "/supplier/billing")
public class SupplierBillingController {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);
	//
	// @Autowired
	// PlanService planService;
	//
	// @Autowired
	// CurrencyService currencyService;
	//
	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	SupplierSubscriptionStripeService supplierSubscriptionStripeService;

	@Autowired
	SupplierPlanService supplierPlanService;

	@Autowired
	CountryService countryService;
	//
	// @Autowired
	// CurrencyEditor currencyEditor;
	//
	// @Autowired
	// BuyerEditor buyerEditor;
	//
	// @Autowired
	// CountryEditor countryEditor;
	//
	// @Autowired
	// CountryService countryService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierService supplierService;
	//
	// @Autowired
	// PlanEditor planEditor;
	//
	// @Resource
	// MessageSource messageSource;
	//
	@Value("${paypal.merchant.id}")
	String MERCHANT_ID;

	@Value("${app.url}")
	String appUrl;

	@Autowired
	PaymentTransactionService paymentTransactionService;

	@Autowired
	SupplierSubscriptionDao supplierSubscriptionDao;

	@Resource
	MessageSource messageSource;

	@Value("${paypal.environment}")
	String PAYPAL_ENVIRONMENT;

	@Autowired
	PromotionalCodeService promotionalCodeService;

	@Value("${stripe.publish}")
	String stripePublicKey;

	@Autowired
	PaymentTransactionDao paymentTransactionDao;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// binder.registerCustomEditor(Currency.class, currencyEditor);
		// binder.registerCustomEditor(Buyer.class, buyerEditor);
		// StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		// binder.registerCustomEditor(String.class, stringtrimmer);
		// binder.registerCustomEditor(Country.class, countryEditor);
		// binder.registerCustomEditor(Plan.class, planEditor);
	}

	@RequestMapping(path = "/accountOverview", method = RequestMethod.GET)
	public String accountOverview(Model model, @RequestParam(name = "payment_intent", required = false) String paymentStatus) {
		try {
			LOG.info("Fetching supplier package plan details for Supplier : " + SecurityLibrary.getLoggedInUserTenantId());
			Supplier supplier = subscriptionService.getSupplierWithSupplierPackagePlanByTenantIdforBilling(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("supplier", supplier);
			model.addAttribute("supplierCountry", (supplier.getRegistrationOfCountry() != null ? supplier.getRegistrationOfCountry().getCountryCode() : ""));
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
			model.addAttribute("subscription", supplier.getSupplierSubscription() != null ? supplier.getSupplierSubscription() : new SupplierSubscription());
			model.addAttribute("publicKey", stripePublicKey);

			if (supplier.getSupplierSubscription() != null) {
				if (supplier.getRegistrationOfCountry() != null && supplier.getRegistrationOfCountry().getCountryCode().equals("MY")) {
					BigDecimal price = BigDecimal.ZERO;
					if (supplier.getSupplierSubscription().getSupplierPlan() != null) {
						price = new BigDecimal(supplier.getSupplierSubscription().getSupplierPlan().getPrice());
						BigDecimal tax = supplier.getSupplierSubscription().getSupplierPlan().getTax();
						price = price.add((price.multiply(tax.divide(new BigDecimal(100)))));
						model.addAttribute("price", price);
					} else {
						model.addAttribute("price", price);
					}
				} else {
					if (supplier.getSupplierSubscription().getSupplierPlan() != null) {
						model.addAttribute("price", supplier.getSupplierSubscription().getSupplierPlan().getPrice());
					}
				}
			}

			if (StringUtils.checkString(paymentStatus).length() > 0) {
				PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentStatus);
				try {
					String msg = supplierSubscriptionStripeService.getPaymentStatus(paymentStatus);
					if (msg.indexOf("Processing") != -1) {
						model.addAttribute("info", msg);
					} else {
						paymentTransaction.setStatus(TransactionStatus.SUCCESS);
						model.addAttribute("subscription", paymentTransaction.getSupplierSubscription());
						model.addAttribute("paymentTransaction", paymentTransaction);
						model.addAttribute("renewal", true);
						return "showSupplierSubscriptionSuccess";
					}
				} catch (Exception e) {
					LOG.error("Error in payment " + e.getLocalizedMessage());
					return "supplierSubscriptionError/" + supplier.getSupplierSubscription().getSupplierPlan().getId() + "/" + paymentTransaction.getId();
				}

			}

		} catch (Exception e) {
			LOG.error("Error while fetching supplier package plan details :" + e.getMessage(), e);
		}
		return "supplierAccountOverview";
	}

	@RequestMapping(path = "/billing", method = RequestMethod.GET)
	public String billing(Model model) {
		try {
			// SupplierSubscription subscription =
			// subscriptionService.getCurrentSubscriptionForSupplier(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("Fetching subscription details for supplier : " + SecurityLibrary.getLoggedInUserTenantId());
			Supplier supplier = subscriptionService.getSupplierWithSupplierPackagePlanByTenantIdforBilling(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("supplier", supplier);
			model.addAttribute("merchantId", this.MERCHANT_ID);
			model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);

			model.addAttribute("subscription", supplier.getSupplierSubscription() != null ? supplier.getSupplierSubscription() : new SupplierSubscription());
			// model.addAttribute("subscription", new SupplierSubscription());
		} catch (Exception e) {
			LOG.error("Error while fetching billing view :" + e.getMessage(), e);
		}
		return "supplierBilling";
	}

	//
	// @RequestMapping(path = "/buycredits/{planId}/{buyerId}", method =
	// RequestMethod.POST)
	// public String doTopup(@ModelAttribute(name = "subscription") Subscription
	// subscription, @PathVariable(name =
	// "planId") String planId, @PathVariable(name = "buyerId") String buyerId,
	// Model model, HttpSession session,
	// HttpServletRequest request) {
	// LOG.info("User requested to purchase Plan : " + planId);
	// String returnURL = request.getScheme() + "://" + request.getServerName()
	// + ":" + request.getServerPort() +
	// request.getContextPath() + "/buyer/billing/confirm?planId=" + planId +
	// "&txId=";
	// String cancelURL = request.getScheme() + "://" + request.getServerName()
	// + ":" + request.getServerPort() +
	// request.getContextPath() + "/buyer/billing/cancel?planId=" + planId +
	// "&txId=";
	// return subscriptionService.initiatePaypalPaymentForTopup(subscription,
	// planId, buyerId, session, returnURL,
	// cancelURL);
	// }
	//
	// @RequestMapping(path = "/renew/{planId}", method = RequestMethod.GET)
	// public String doRenew(@PathVariable(name = "planId") String planId, Model
	// model, HttpSession session) {
	// LOG.info("User requested to renew Plan : " + planId);
	// Buyer buyer = null;
	// // try {
	// // buyer =
	// buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
	// // Plan plan = planService.getPlanForEditById(planId);
	// //
	// // Subscription subscription = new Subscription();
	// // subscription.setPlan(plan);
	// // subscription.setBuyer(buyer);
	// // subscription.setPaymentTransaction(new PaymentTransaction());
	// //
	// subscription.getPaymentTransaction().setCountry(buyer.getRegistrationOfCountry());
	// //
	// // model.addAttribute("subscription", subscription);
	// // model.addAttribute("merchantId", this.MERCHANT_ID);
	// // model.addAttribute("plan", plan);
	// // model.addAttribute("buyer", buyer);
	// // return "renewPlan";
	// // } catch (Exception e) {
	// // LOG.error("Error during renewal : " + e.getMessage(), e);
	// // model.addAttribute("buyer", buyer);
	// // model.addAttribute("merchantId", this.MERCHANT_ID);
	// // Subscription subscription =
	// //
	// subscriptionService.getCurrentSubscriptionForBuyer(SecurityLibrary.getLoggedInUserTenantId());
	// // model.addAttribute("subscription", subscription != null ? subscription
	// : new Subscription());
	// // model.addAttribute("error", "Error during plan renewal : " +
	// e.getMessage());
	// // return "buyerBilling";
	// // }
	// return null;
	// }

	// @RequestMapping(path = "/renew/{planId}", method = RequestMethod.POST)
	// public String doRenewInitiate(@ModelAttribute(name = "subscription") SupplierSubscription subscription,
	// @PathVariable(name = "planId") String planId, Model model, HttpSession session, HttpServletRequest request) {
	// LOG.info("User requested to renew Plan : " + planId);
	//
	// LOG.info("User requested to renew Plan : " + subscription);
	// Supplier supplier = supplierService.findSupplierOnDashbordById(SecurityLibrary.getLoggedInUserTenantId());
	// subscription.setPaymentTransaction(new PaymentTransaction());
	// subscription.getPaymentTransaction().setCountry(supplier.getRegistrationOfCountry());
	// subscription.getPaymentTransaction().setCompanyRegistrationNumber(supplier.getCompanyRegistrationNumber());
	// subscription.getPaymentTransaction().setSupplier(supplier);
	// subscription.setSupplier(supplier);
	// subscription.setSupplierPlan(new SupplierPlan());
	// subscription.getSupplierPlan().setId(planId);
	//
	// String returnURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
	// request.getContextPath() + "/supplier/billing/confirmRenew?planId=" + planId + "&txId=";
	// String cancelURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
	// request.getContextPath() + "/supplier/billing/cancel?planId=" + planId + "&txId=";
	// return subscriptionService.initiatePaypalPaymentForRenewSupplier(subscription, planId,
	// SecurityLibrary.getLoggedInUserTenantId(), session, returnURL, cancelURL, null);
	// }

	// @RequestMapping(path = "/doBuyPlanInitiate/{planId}", method = RequestMethod.POST)
	// public String doBuyPlanInitiate(@ModelAttribute(name = "subscription") SupplierSubscription subscription,
	// @PathVariable(name = "planId") String planId, @RequestParam(name = "promocode") String promocode, Model model,
	// HttpSession session, HttpServletRequest request) {
	// LOG.info("User requested to renew Plan : " + planId);
	// LOG.info("User requested to promocode : " + promocode);
	// Supplier supplier = supplierService.findSupplierOnDashbordById(SecurityLibrary.getLoggedInUserTenantId());
	// subscription.setPaymentTransaction(new PaymentTransaction());
	// subscription.getPaymentTransaction().setCountry(supplier.getRegistrationOfCountry());
	// subscription.getPaymentTransaction().setCompanyRegistrationNumber(supplier.getCompanyRegistrationNumber());
	// subscription.getPaymentTransaction().setSupplier(supplier);
	// subscription.setSupplier(supplier);
	// subscription.setSupplierPlan(new SupplierPlan());
	// subscription.getSupplierPlan().setId(planId);

	// String returnURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
	// request.getContextPath() + "/supplier/billing/confirmRenew?planId=" + planId + "&txId=";
	// String cancelURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
	// request.getContextPath() + "/supplier/billing/buyPlan";
	// return subscriptionService.initiatePaypalPaymentForRenewSupplier(subscription, planId,
	// SecurityLibrary.getLoggedInUserTenantId(), session, returnURL, cancelURL, promocode);
	// }

	// @RequestMapping(path = "/payment/{planId}", method = RequestMethod.POST)
	// public String doPayment(@ModelAttribute(name = "subscription")
	// Subscription subscription, @PathVariable(name =
	// "planId") String planId, Model model, HttpSession session,
	// HttpServletRequest request) {
	// LOG.info("User requested to purchase Plan : " + planId);
	// String returnURL = request.getScheme() + "://" + request.getServerName()
	// + ":" + request.getServerPort() +
	// request.getContextPath() + "/buyer/billing/confirmRenew?planId=" + planId
	// + "&txId=";
	// String cancelURL = request.getScheme() + "://" + request.getServerName()
	// + ":" + request.getServerPort() +
	// request.getContextPath() + "/buyer/billing/cancel?planId=" + planId +
	// "&txId=";
	// return subscriptionService.initiatePaypalPayment(subscription, planId,
	// session, returnURL, cancelURL);
	// }

	@RequestMapping(path = "/cancel", method = RequestMethod.GET)
	public String doCancel(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, Model model) {
		List<SupplierPlan> planList = supplierPlanService.findAllPlansByStatus(PlanStatus.ACTIVE);
		model.addAttribute("planList", planList);
		model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
		subscriptionService.cancelPaymentTransaction(txId);
		model.addAttribute("merchantId", this.MERCHANT_ID);
		model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
		Supplier supplier = supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("supplier", supplier);
		return "subscribePlan";
	}

	@RequestMapping(path = "/confirmRenew", method = RequestMethod.GET)
	public String showConfirmPayment(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, @RequestParam(name = "token") String token, @RequestParam(name = "PayerID") String payerID, Model model, HttpServletRequest request, HttpSession session) {

		// String subscriptionId = (String)
		// session.getAttribute("subscriptionId");
		// LOG.info("Subscription Id : " + subscriptionId);
		// SupplierSubscription subscription =
		// subscriptionService.getSupplierSubscriptionById(subscriptionId);
		// // Buyer buyer =
		// buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
		// Supplier supplier =
		// supplierService.findSupplierOnDashbordById(SecurityLibrary.getLoggedInUserTenantId());
		// //model.addAttribute("buyer", buyer);
		// model.addAttribute("supplier", supplier);
		// model.addAttribute("subscription", subscription);
		//
		// PaymentTransaction paymentTransaction =
		// subscriptionService.getPaymentTransactionById(txId);
		// model.addAttribute("paymentTransaction", paymentTransaction);
		// /*
		// * Build a second API request to PayPal, using the token as the ID to
		// get the details on the payment
		// * authorization
		// */
		//
		// String nvpstr = "&TOKEN=" + token;
		//
		// LOG.info("Invoking PayPal Checkout Details ===================> ");
		// /*
		// * Make the API call and store the results in an array. If the call
		// was a success, show the authorization
		// * details, and provide an action to complete the payment. If failed,
		// show the error
		// */
		// HashMap<String, String> nvp =
		// subscriptionService.invokePaypalService("GetExpressCheckoutDetails",
		// nvpstr);
		// String strAck = nvp.get("ACK").toString();
		// if (strAck != null &&
		// (strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS) ||
		// strAck.equalsIgnoreCase("SuccessWithWarning"))) {
		// session.setAttribute("PAYERID", nvp.get("PAYERID").toString());
		// }
		//
		// // The map will contain values as documented at
		// //
		// https://developer.paypal.com/docs/classic/api/merchant/GetExpressCheckoutDetails_API_Operation_NVP/
		// model.addAttribute("paypalResponse", nvp);
		// dsfdsf return "renewPlanConfirm";
		//
		// Not required in model as the Subscription object already has the Plan
		// details
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
		model.addAttribute("renewal", true);
		return "showSupplierConfirmSubscription";

	}

	@RequestMapping(path = "/confirmRenew", method = RequestMethod.POST)
	public String doConfirmRenewPayment(@RequestParam(name = "paymentTransactionId") String paymentTransactionId, @RequestParam(name = "token") String token, @RequestParam(name = "payerId") String payerId, Model model, HttpServletRequest request, HttpSession session) {
		LOG.info("confirmRenew post called");
		try {
			String serverName = request.getServerName();
			subscriptionService.confirmRenewSupplierSubscription(token, payerId, model, serverName, paymentTransactionId, true);
			model.addAttribute("renewal", true);
		} catch (ApplicationException e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);
			PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionWithSupplierPlanByPaymentTransactionId(paymentTransactionId);
			return "redirect:/supplier/supplierSubscriptionError/" + paymentTransaction.getSupplierPlan().getId() + "/" + paymentTransactionId;
		} catch (Exception e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);
		}
		return "showSupplierSubscriptionSuccess";
	}

	// @SuppressWarnings("unchecked")
	// @RequestMapping(path = "/renewPlanSuccess/{paymentTransactionId}", method
	// = RequestMethod.GET)
	// public String showRenewSuccess(@PathVariable(name =
	// "paymentTransactionId") String paymentTransactionId, Model
	// model, HttpServletRequest request, HttpSession session) {
	//
	// PaymentTransaction paymentTransaction =
	// subscriptionService.getPaymentTransactionById(paymentTransactionId);
	//
	// SupplierSubscription subscription =
	// subscriptionService.getSupplierSubscriptionById(paymentTransaction.getSubscription().getId());
	// request.setAttribute("renewal", "true");
	//
	// HashMap<String, String> nvp = (HashMap<String, String>)
	// session.getAttribute("nvp");
	// session.removeAttribute("nvp");
	//
	// model.addAttribute("paypalResponse", nvp);
	// model.addAttribute("subscription", subscription);
	// model.addAttribute("paymentTransaction", paymentTransaction);
	// return "renewalSuccess";
	// }

	// @RequestMapping(path = "/changePlan/{planId}", method =
	// RequestMethod.GET)
	// public String doChangePlan(@PathVariable(name = "planId") String planId,
	// Model model, HttpSession session) {
	// LOG.info("User requested to change Plan : " + planId);
	// Buyer buyer = null;
	// try {
	// buyer =
	// buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
	// Plan plan = planService.getPlanForEditById(planId);
	//
	// Subscription subscription = new Subscription();
	// subscription.setPlan(plan);
	// subscription.setBuyer(buyer);
	// subscription.setPaymentTransaction(new PaymentTransaction());
	// subscription.getPaymentTransaction().setCountry(buyer.getRegistrationOfCountry());
	//
	// List<Plan> planList = planService.findAllPlansByStatuses(new PlanStatus[]
	// { PlanStatus.ACTIVE });
	// model.addAttribute("planList", planList);
	// model.addAttribute("subscription", subscription);
	// model.addAttribute("merchantId", this.MERCHANT_ID);
	// model.addAttribute("plan", plan);
	// model.addAttribute("buyer", buyer);
	// return "changePlan";
	// } catch (Exception e) {
	// LOG.error("Error during renewal : " + e.getMessage(), e);
	// model.addAttribute("buyer", buyer);
	// model.addAttribute("merchantId", this.MERCHANT_ID);
	// Subscription subscription =
	// subscriptionService.getCurrentSubscriptionForBuyer(SecurityLibrary.getLoggedInUserTenantId());
	// model.addAttribute("subscription", subscription != null ? subscription :
	// new Subscription());
	// model.addAttribute("error", "Error during plan renewal : " +
	// e.getMessage());
	// return "buyerBilling";
	// }
	// }

	// @RequestMapping(path = "/changePlan/{planId}", method =
	// RequestMethod.POST)
	// public String doChangePlanInitiate(@ModelAttribute(name = "subscription")
	// Subscription subscription,
	// @PathVariable(name = "planId") String planId, Model model, HttpSession
	// session, HttpServletRequest request) {
	// LOG.info("User requested to change Plan from : " + planId + " to Plan : "
	// + subscription.getPlan().getId());
	// Buyer buyer =
	// buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
	// subscription.setPaymentTransaction(new PaymentTransaction());
	// subscription.getPaymentTransaction().setCountry(buyer.getRegistrationOfCountry());
	// subscription.getPaymentTransaction().setCompanyRegistrationNumber(buyer.getCompanyRegistrationNumber());
	// subscription.getPaymentTransaction().setBuyer(buyer);
	// subscription.setBuyer(buyer);
	// // subscription.setPlan(new Plan());
	// // subscription.getPlan().setId(planId);
	// String returnURL = request.getScheme() + "://" + request.getServerName()
	// + ":" + request.getServerPort() +
	// request.getContextPath() + "/buyer/billing/confirmChangePlan?planId=" +
	// planId + "&txId=";
	// String cancelURL = request.getScheme() + "://" + request.getServerName()
	// + ":" + request.getServerPort() +
	// request.getContextPath() + "/buyer/billing/cancel?planId=" + planId +
	// "&txId=";
	// return
	// subscriptionService.initiatePaypalPaymentForChangeOfPlan(subscription,
	// subscription.getPlan().getId(),
	// SecurityLibrary.getLoggedInUserTenantId(), session, returnURL,
	// cancelURL);
	// }
	//
	// @RequestMapping(path = "/confirmChangePlan", method = RequestMethod.GET)
	// public String showConfirmChangePlanPayment(@RequestParam(name = "planId")
	// String planId, @RequestParam(name =
	// "txId") String txId, @RequestParam(name = "token") String token,
	// @RequestParam(name = "PayerID") String payerID,
	// Model model, HttpServletRequest request, HttpSession session) {
	//
	// String subscriptionId = (String) session.getAttribute("subscriptionId");
	// LOG.info("Subscription Id : " + subscriptionId);
	// Subscription subscription =
	// subscriptionService.getSubscriptionById(subscriptionId);
	// Buyer buyer =
	// buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
	// // subscription.setBuyer(buyer);
	// model.addAttribute("buyer", buyer);
	// model.addAttribute("subscription", subscription);
	//
	// PaymentTransaction paymentTransaction =
	// subscriptionService.getPaymentTransactionById(txId);
	// model.addAttribute("paymentTransaction", paymentTransaction);
	// /*
	// * Build a second API request to PayPal, using the token as the ID to get
	// the details on the payment
	// * authorization
	// */
	//
	// String nvpstr = "&TOKEN=" + token;
	//
	// LOG.info("Invoking PayPal Checkout Details ===================> ");
	// /*
	// * Make the API call and store the results in an array. If the call was a
	// success, show the authorization
	// * details, and provide an action to complete the payment. If failed, show
	// the error
	// */
	// HashMap<String, String> nvp =
	// subscriptionService.invokePaypalService("GetExpressCheckoutDetails",
	// nvpstr);
	// String strAck = nvp.get("ACK").toString();
	// if (strAck != null &&
	// (strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS) ||
	// strAck.equalsIgnoreCase("SuccessWithWarning"))) {
	// session.setAttribute("PAYERID", nvp.get("PAYERID").toString());
	// }
	//
	// // The map will contain values as documented at
	// //
	// https://developer.paypal.com/docs/classic/api/merchant/GetExpressCheckoutDetails_API_Operation_NVP/
	// model.addAttribute("paypalResponse", nvp);
	// return "changePlanConfirm";
	// }
	//
	// @RequestMapping(path = "/confirmChangePlan", method = RequestMethod.POST)
	// public String doConfirmChangePlanPayment(@RequestParam(name =
	// "paymentTransactionId") String
	// paymentTransactionId, @RequestParam(name = "token") String token,
	// @RequestParam(name = "payerId") String payerId,
	// Model model, HttpServletRequest request, HttpSession session) {
	// String serverName = request.getServerName();
	// HashMap<String, String> nvp =
	// subscriptionService.confirmSubscriptionChange(token, payerId, model,
	// serverName,
	// paymentTransactionId);
	// session.setAttribute("nvp", nvp);
	// request.setAttribute("renewal", "true");
	// return "redirect:changePlanSuccess/" + paymentTransactionId +
	// "?changePlan=true";
	// }
	//
	// @SuppressWarnings("unchecked")
	// @RequestMapping(path = "/changePlanSuccess/{paymentTransactionId}",
	// method = RequestMethod.GET)
	// public String showChangePlanSuccess(@PathVariable(name =
	// "paymentTransactionId") String paymentTransactionId,
	// Model model, HttpServletRequest request, HttpSession session) {
	//
	// PaymentTransaction paymentTransaction =
	// subscriptionService.getPaymentTransactionById(paymentTransactionId);
	//
	// Subscription subscription =
	// subscriptionService.getSubscriptionById(paymentTransaction.getSubscription().getId());
	// request.setAttribute("changePlan", "true");
	//
	// HashMap<String, String> nvp = (HashMap<String, String>)
	// session.getAttribute("nvp");
	// session.removeAttribute("nvp");
	//
	// model.addAttribute("paypalResponse", nvp);
	// model.addAttribute("subscription", subscription);
	// model.addAttribute("paymentTransaction", paymentTransaction);
	// return "changePlanSuccess";
	// }

	@RequestMapping(path = "/paymentTransactionData", method = RequestMethod.GET)
	public ResponseEntity<TableData<PaymentTransaction>> paymentTransactionData(TableDataInput input) {
		try {
			// TableData<PaymentTransaction> data = new
			// TableData<PaymentTransaction>(paymentTransactionService.findSuccessfulPaymentTransactionsForBuyer(SecurityLibrary.getLoggedInUserTenantId(),
			// input));
			TableData<PaymentTransaction> data = new TableData<PaymentTransaction>(paymentTransactionService.findSuccessfulPaymentTransactionsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input));
			long totalFilterCount = paymentTransactionService.findTotalSuccessfulFilteredPaymentTransactionsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = paymentTransactionService.findTotalSuccessfulPaymentTransactionForSupplier(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<PaymentTransaction>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Payment Transaction list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Payment Transaction list : " + e.getMessage());
			return new ResponseEntity<TableData<PaymentTransaction>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/paymentTransactionExcel", method = RequestMethod.GET)
	public void downloadPaymentTransactionExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("downloadPaymentTransactionExcel this method is called here");
		try {
			supplierPlanService.downloadPaymentTransactionExcel(response, SecurityLibrary.getLoggedInUserTenantId());
		} catch (Exception e) {
			LOG.error(" while downloading Buyer Address  template :: " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/buyPlan", method = RequestMethod.GET)
	public String upgradePlanList(Model model, @RequestParam(name = "payment_intent", required = false) String paymentStatus) {
		PlanStatus[] status = { PlanStatus.ACTIVE };
		List<SupplierPlan> planList = new ArrayList<SupplierPlan>();

		List<SupplierPlan> dbplanList = supplierPlanService.findAllPlansByStatuses(status);
		for (SupplierPlan supplierPlan : dbplanList) {
			planList.add(supplierPlan);
			break;
		}
		model.addAttribute("planList", planList);
		model.addAttribute("buyerList", buyerService.findAllActiveBuyersWithActiveSubscription());
		model.addAttribute("merchantId", this.MERCHANT_ID);
		model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
		model.addAttribute("publicKey", stripePublicKey);
		Supplier supplier = supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("supplier", supplier);

		if (StringUtils.checkString(paymentStatus).length() > 0) {
			PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentStatus);
			try {
				String msg = supplierSubscriptionStripeService.getPaymentStatus(paymentStatus);
				if (msg.indexOf("Processing") != -1) {
					model.addAttribute("info", msg);
				} else {
					paymentTransaction.setStatus(TransactionStatus.SUCCESS);
					model.addAttribute("subscription", paymentTransaction.getSupplierSubscription());
					model.addAttribute("paymentTransaction", paymentTransaction);
					model.addAttribute("renewal", true);
					return "showSupplierSubscriptionSuccess";
				}
			} catch (Exception e) {
				LOG.error("Error in payment " + e.getLocalizedMessage());
				return "supplierSubscriptionError/" + supplier.getSupplierSubscription().getSupplierPlan().getId() + "/" + paymentTransaction.getId();
			}
		}

		return "buyPlan";
	}

	@RequestMapping(path = "/getSupplierPrice", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> getSupplierPrice(@RequestParam(name = "selectedPlan") String selectedPlan, @RequestParam(name = "promoCode") String promoCode, @RequestParam(name = "countryId", required = false) String countryId) throws JsonProcessingException {
		LOG.info("get Supplier Price called ...selectedPlan: " + selectedPlan);
		LOG.info("promoCode: " + promoCode);
		Map<String, Object> result = new HashMap<>();
		try {

			SupplierPlan plan = supplierPlanService.getPlanById(selectedPlan);
			BigDecimal promoDiscountPrice = BigDecimal.ZERO;
			BigDecimal supplierPrice = BigDecimal.ZERO;
			if (plan != null) {

				if (plan.getPrice() != null && plan.getPrice() != 0) {
					supplierPrice = new BigDecimal(plan.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
				}

				BigDecimal tax = BigDecimal.ZERO;
				if (StringUtils.isNotBlank(countryId)) {
					Country country = countryService.getCountryById(countryId);
					if (country == null) {
						country = countryService.getCountryByCode(countryId);
					}
					if (country != null) {
						if ("MY".equals(country.getCountryCode())) {
							tax = new BigDecimal(plan.getPrice()).multiply((plan.getTax().divide(new BigDecimal(100))));
						}
					}
				}

				BigDecimal totalPrice = supplierPrice.add(tax);
				promotionalCodeService.validatePromoCode(promoCode, plan, null, totalPrice, TenantType.SUPPLIER, SecurityLibrary.getLoggedInUserTenantId());
				result.put("totalPrice", (totalPrice));
				result.put("supplierPrice", supplierPrice);
				result.put("promoDiscountPrice", promoDiscountPrice);
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
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while calculate supplier price : " + e.getMessage(), e);
			return new ResponseEntity<Map<String, Object>>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public BigDecimal getPromoDiscount(String promoCode, BigDecimal totalPrice, Map<String, Object> result, SupplierPlan plan) throws Exception {
		BigDecimal promoDiscountPrice = BigDecimal.ZERO;
		if (StringUtils.checkString(promoCode).length() > 0) {
			PromotionalCode promo = promotionalCodeService.getPromotionalCodeByPromoCode(promoCode);
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

	@RequestMapping(path = "/doBuyPlanInitiate/{planId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PaymentIntentPojo> doBuyPlanInitiate(@RequestBody SupplierSubscription subscription, @PathVariable(name = "planId") String planId, @RequestParam(name = "paymentMode") String paymentMode, @RequestParam(name = "promocode") String promocode) {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("User requested to renew Plan : " + planId);
			LOG.info("User requested to promocode : " + promocode);
			Supplier supplier = supplierService.findSupplierOnDashbordById(SecurityLibrary.getLoggedInUserTenantId());
			subscription.setPaymentTransaction(new PaymentTransaction());
			subscription.getPaymentTransaction().setCountry(supplier.getRegistrationOfCountry());
			subscription.getPaymentTransaction().setCompanyRegistrationNumber(supplier.getCompanyRegistrationNumber());
			subscription.getPaymentTransaction().setSupplier(supplier);
			subscription.setSupplier(supplier);
			subscription.setSupplierPlan(new SupplierPlan());
			subscription.getSupplierPlan().setId(planId);
			return new ResponseEntity<PaymentIntentPojo>(supplierSubscriptionStripeService.initiateStripePaymentForRenewSupplier(subscription, planId, SecurityLibrary.getLoggedInUserTenantId(), promocode, paymentMode, false), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while storing Supplier : " + e.getMessage(), e);
			headers.add("error", e.getLocalizedMessage());
			return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(path = "/renew/{planId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PaymentIntentPojo> doRenewInitiate(@RequestBody SupplierSubscription existingSubscription, @PathVariable(name = "planId") String planId, @RequestParam(name = "paymentMode") String paymentMode) {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("User requested to renew Plan with id " + planId + " and subscription id " + existingSubscription.getId());
			Supplier supplier = supplierService.findSupplierOnDashbordById(SecurityLibrary.getLoggedInUserTenantId());
			SupplierSubscription subscription = new SupplierSubscription();
			subscription.setPaymentTransaction(new PaymentTransaction());
			subscription.getPaymentTransaction().setCountry(supplier.getRegistrationOfCountry());
			subscription.getPaymentTransaction().setCompanyRegistrationNumber(supplier.getCompanyRegistrationNumber());
			subscription.getPaymentTransaction().setSupplier(supplier);
			subscription.setSupplier(supplier);
			subscription.setSupplierPlan(new SupplierPlan());
			subscription.getSupplierPlan().setId(planId);
			return new ResponseEntity<PaymentIntentPojo>(supplierSubscriptionStripeService.initiateStripePaymentForRenewSupplier(subscription, planId, supplier.getId(), null, paymentMode, true), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while storing Supplier : " + e.getMessage(), e);
			headers.add("error", e.getLocalizedMessage());
			return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
