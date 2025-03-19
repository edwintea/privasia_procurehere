/**
 * 
 */
package com.privasia.procurehere.web.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.Plan;
import com.privasia.procurehere.core.entity.Subscription;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.PaymentTransactionService;
import com.privasia.procurehere.service.PlanService;
import com.privasia.procurehere.service.SubscriptionService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.impl.SubscriptionServiceImpl;
import com.privasia.procurehere.web.editors.BuyerEditor;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;

/**
 * @author Nitin Otageri
 */
@Controller
@RequestMapping(value = "/subscription")
public class SubscriptionController {

	private static final Logger LOG = LogManager.getLogger(Global.SUBSCRIPTION_LOG);

	// private static final String CLIENT_ID =
	// "ATUbW9GJ8YbMnMrPhw35fsyhO5TQIyH9Gv-PHDe6ZrwkJNv-jvhJPHdFhQNy_4GcviqkEU6jYq5EEyqU";
	// private static final String SECRET =
	// "EL9E3nXvbMJL8QCmMoZWKd4h7X3aq4Z84p4TpVGbqudxRBocsiHxZ2l1--iiljPxoosk_WK3BK4LnG2q";
	// private static final String MODE = "sandbox";

	@Autowired
	PlanService planService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	SubscriptionService subscriptionService;

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
	
	@Value("${paypal.environment}")
	String PAYPAL_ENVIRONMENT;
	
	@Autowired
	PaymentTransactionService paymentTransactionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(Buyer.class, buyerEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(Country.class, countryEditor);
	}

	@RequestMapping(path = "/selectPlan", method = RequestMethod.GET)
	public String planList(Model model) {
		List<Plan> planList = planService.findAllPlansByStatus(PlanStatus.ACTIVE);
		model.addAttribute("planList", planList);
		return "selectPlan";
	}

	@RequestMapping(path = "/payment/{planId}", method = RequestMethod.POST)
	public String doPayment(@ModelAttribute(name = "subscription") Subscription subscription, @PathVariable(name = "planId") String planId, Model model, HttpSession session, HttpServletRequest request) {
		LOG.info("User requested to purchase Plan : " + planId);
		Plan plan = planService.getPlanForEditById(planId);
		if (plan.getPrice() > 0) {
			String returnURL = appUrl + "/subscription/confirm?planId=" + planId + "&txId=";
			String cancelURL = appUrl + "/subscription/cancel?planId=" + planId + "&txId=";
			// String returnURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			// + request.getContextPath() + "/subscription/confirm?planId=" + planId + "&txId=";
			// String cancelURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			// + request.getContextPath() + "/subscription/cancel?planId=" + planId + "&txId=";
	
			//TODO removed
			return null; //subscriptionService.initiatePaypalPayment(subscription, planId, session, returnURL, cancelURL);
		} else {
			// Trial Account
			Map<String, String> map = new HashMap<String, String>();
			try {
				//TODO removed
				User user = null; //subscriptionService.doTrialSubscription(subscription, planId, model);
				buyerService.sentBuyerCreationMail(user.getBuyer(), user);
				Owner owner = buyerService.getDefaultOwner();
				try {
					uomService.loadDefaultUomIntoBuyerAccount(user.getBuyer(), owner);
				} catch (Exception e) {
					LOG.error("Error loading default UOM for buyer : " + e.getMessage(), e);
				}
				// Free trial
				map.put("ACK", "Success");
				map.put("PAYMENTINFO_0_TRANSACTIONID", "Success");
			} catch (Exception e) {
				LOG.error("Error during trial subscription : " + e.getMessage(), e);
				PaymentTransaction paymentTransaction = new PaymentTransaction();
				paymentTransaction.setErrorCode("-1009");
				paymentTransaction.setErrorMessage(e.getMessage());
				model.addAttribute("paymentTransaction", paymentTransaction);
				return "redirect:../subscription/subscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
			}
			model.addAttribute("paypalResponse", map);
			return "subscriptionSuccess";
		}
	}

	@RequestMapping(path = "/cancel", method = RequestMethod.GET)
	public String doCancel(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, Model model) {
		Plan plan = planService.getPlanForEditById(planId);
		model.addAttribute("plan", plan);
		PaymentTransaction paymentTransaction = subscriptionService.cancelPaymentTransaction(txId);
		model.addAttribute("paymentTransaction", paymentTransaction);
		return "subscriptionError";
	}

	@RequestMapping(path = "/subscriptionError/{planId}/{paymentTransactionId}", method = RequestMethod.GET)
	public String showPaymentError(@PathVariable(name = "planId") String planId, @PathVariable(name = "paymentTransactionId") String paymentTransactionId, Model model) {
		Plan plan = planService.getPlanForEditById(planId);
		PaymentTransaction paymentTransaction = subscriptionService.getPaymentTransactionById(paymentTransactionId);
		model.addAttribute("paymentTransaction", paymentTransaction);
		model.addAttribute("plan", plan);
		return "subscriptionError";
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
		Subscription subscription = subscriptionService.getSubscriptionById(subscriptionId);
		if (subscription != null) {
			Buyer buyer = (Buyer) session.getAttribute("buyer");
			subscription.setBuyer(buyer);
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
		return "confirmSubscription";
	}
/*
	@RequestMapping(path = "/confirm", method = RequestMethod.POST)
	public String doConfirmPayment(@RequestParam(name = "paymentTransactionId") String paymentTransactionId, @RequestParam(name = "token") String token, @RequestParam(name = "payerId") String payerId, Model model, HttpServletRequest request, HttpSession session) {
		// PaymentTransaction paymentTransaction;
		// Subscription subscription;
		try {
			String serverName = request.getServerName();
			User user = subscriptionService.confirmSubscription(token, payerId, model, serverName, paymentTransactionId);
			buyerService.sentBuyerCreationMail(user.getBuyer(), user);
			Owner owner = buyerService.getDefaultOwner();
			try {
				uomService.loadDefaultUomIntoBuyerAccount(user.getBuyer(), owner);
			} catch (Exception e) {
				LOG.error("Error loading default UOM for buyer : " + e.getMessage(), e);
			}

			// paymentTransaction = subscriptionService.getPaymentTransactionById(paymentTransactionId);
			// subscription = subscriptionService.getSubscriptionById(paymentTransaction.getSubscription().getId());
			//
			// if (user != null) {
			// LOG.info("User ID : " + user.getId() + " Tenant : " + user.getTenantId());
			// // Update the subscription details
			// Buyer buyer = buyerService.findBuyerById(user.getTenantId());
			// subscription.setBuyer(buyer);
			// // Calculate Start/End, Trial Start Trial End etc...
			// subscriptionService.doComputeSubscription(subscription);
			//
			// BuyerPackage bp = new BuyerPackage(subscription);
			// buyer.setBuyerPackage(bp);
			// subscription = subscriptionService.updateSubscription(subscription);
			// buyer = buyerService.updateBuyer(buyer);
			//
			// // Update transaction details with buyer info
			// paymentTransaction.setBuyer(buyer);
			// paymentTransaction = subscriptionService.updatePaymentTransaction(paymentTransaction);
			//
			// buyerService.sentBuyerCreationMail(buyer, user);
			// // Reload the subscription data along with plan details to render success page.
			//
			// }
			// model.addAttribute("subscription", subscription);
			// model.addAttribute("paymentTransaction", paymentTransaction);
		} catch (ApplicationException e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);
			PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionWithBuyerPlanById(paymentTransactionId);
			return "redirect:../subscription/subscriptionError/" + paymentTransaction.getPlan().getId() + "/" + paymentTransactionId;
		} catch (Exception e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);
		}
		return "subscriptionSuccess";
	}*/

	@RequestMapping(path = "/get/{planId}", method = RequestMethod.GET)
	public String buyPlan(@PathVariable(name = "planId") String planId, Model model, HttpSession session) {
		try {
			LOG.info("User requested to purchase Plan : " + planId);
			Plan plan = planService.getPlanForEditById(planId);

			Subscription subscription = new Subscription();
			subscription.setPlan(plan);
			subscription.setBuyer(new Buyer());
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
			List<Plan> planList = planService.findAllPlansByStatus(PlanStatus.ACTIVE);
			// Map<String, List<Plan>> planMap = constructPlanMap(planList);
			// model.addAttribute("planMap", planMap);
			model.addAttribute("planList", planList);
			return "selectPlan";
		}
	}

}
