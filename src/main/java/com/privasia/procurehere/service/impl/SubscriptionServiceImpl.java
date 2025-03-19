/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.PaymentTransactionDao;
import com.privasia.procurehere.core.dao.SubscriptionDao;
import com.privasia.procurehere.core.dao.SupplierPackageDao;
import com.privasia.procurehere.core.dao.SupplierSubscriptionDao;
import com.privasia.procurehere.core.enums.PaymentMethod;
import com.privasia.procurehere.core.enums.PeriodUnitType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.enums.TransactionType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author Nitin Otageri
 */
@Service
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {

	private static Logger LOG = LogManager.getLogger(Global.SUBSCRIPTION_LOG);

	public static final String TOKEN = "TOKEN";
	public static final String SUCCESS = "Success";

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	SubscriptionDao subscriptionDao;

	@Autowired
	SupplierSubscriptionDao supplierSubscriptionDao;

	@Autowired
	PaymentTransactionDao paymentTransactionDao;

	@Autowired
	PlanService planService;

	@Autowired
	SupplierPlanService supplierPlanService;

	@Autowired
	UserService userService;

	@Value("${app.url}")
	String APP_URL;

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

	@Value("${paypal.api.endpoint.url}")
	String PAYPAL_API_ENDPOINT_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	PromotionalCodeService promotionalCodeService;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	SupplierPackageDao supplierPackageDao;

	@Override
	@Transactional(readOnly = false)
	public Subscription saveSubscription(Subscription subscription) {
		if (subscription.getBuyer() != null && subscription.getBuyer().getId() == null) {
			// Buyer buyer = buyerService.saveBuyer(subscription.getBuyer());
			// subscription.setBuyer(buyer);
		}
		return subscriptionDao.saveOrUpdate(subscription);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierSubscription saveSupplierSubscription(SupplierSubscription subscription) {
		if (subscription.getSupplier() != null && subscription.getSupplier().getId() == null) {
			// Buyer buyer = buyerService.saveBuyer(subscription.getBuyer());
			// subscription.setBuyer(buyer);
		}
		return supplierSubscriptionDao.saveOrUpdate(subscription);
	}

	/*
	 * @Override
	 * @Transactional(readOnly = true) public void doComputeSubscription(Subscription subscription, Subscription
	 * previousSubscription) { if (subscription.getPlan() == null){
	 * subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE); return; } // Go to the end of subscription chain
	 * if (previousSubscription != null) { while (previousSubscription.getNextSubscription() != null &&
	 * previousSubscription.getNextSubscription().getStartDate() != null) { previousSubscription =
	 * previousSubscription.getNextSubscription(); } } Plan plan =
	 * planService.getPlanForEditById(subscription.getPlan().getId()); if (plan != null) { if
	 * (subscription.getUserLimit() == null) { subscription.setUserLimit(plan.getUserLimit()); } if
	 * (subscription.getEventLimit() == null) { subscription.setEventLimit(plan.getEventLimit()); } // Set the start
	 * date as Today if its null (this method is also called from manual Buyer Creation and from // Buy Subscription) if
	 * (subscription.getStartDate() == null) { if (previousSubscription != null) { // Set the start date as Today if
	 * previous subscription has expired if (previousSubscription.getSubscriptionStatus() == SubscriptionStatus.EXPIRED)
	 * { subscription.setStartDate(new Date()); subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE); Buyer
	 * buyer = previousSubscription.getBuyer(); if(buyer != null){ // buyer.setCurrentSubscription(subscription);
	 * buyerService.updateBuyer(buyer); } } else { subscription.setStartDate(previousSubscription.getEndDate());
	 * subscription.setSubscriptionStatus(SubscriptionStatus.FUTURE); } } else { subscription.setStartDate(new Date());
	 * subscription.setActivatedDate(new Date()); subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE); } }
	 * boolean autoSubscription = false; // Trial period only if its a new subscription. For renewal, no trial period if
	 * (subscription.getTrialQuantity() == null && previousSubscription == null && plan.getTrialPeriod() != null &&
	 * plan.getTrialPeriod() > 0) { subscription.setTrialQuantity(plan.getTrialPeriod()); autoSubscription = true; LOG.
	 * info
	 * ("Trial will be taken from Plan as looks like this is auto subscription (Buyer not being manually created)..." );
	 * } // Compute the durations of trial and paid ones if (subscription.getTrialQuantity() != null &&
	 * subscription.getTrialQuantity() > 0) { LOG.info("Subscription trial : " + subscription.getTrialQuantity());
	 * Calendar trialEnd = Calendar.getInstance(); trialEnd.setTime(subscription.getStartDate()); if (autoSubscription)
	 * { switch (plan.getTrialPeriodUnit()) { case DAY: trialEnd.add(Calendar.DATE, subscription.getTrialQuantity());
	 * break; case MONTH: trialEnd.add(Calendar.MONTH, subscription.getTrialQuantity()); break; case WEEK:
	 * trialEnd.add(Calendar.DATE, (7 * subscription.getTrialQuantity())); break; default: break; } } else {
	 * trialEnd.add(Calendar.DATE, subscription.getTrialQuantity()); }
	 * subscription.setTrialStartDate(subscription.getStartDate()); subscription.setTrialEndDate(trialEnd.getTime()); //
	 * In case of trial plans, there is no plan price. if (plan.getPrice() > 0) { // Now calculate the Paid Start and
	 * End Dates. trialEnd.add(Calendar.DATE, 1); // Next day after trial end date
	 * subscription.setStartDate(trialEnd.getTime()); if (plan.getPeriodUnit() == PeriodUnitType.MONTH) {
	 * trialEnd.add(Calendar.MONTH, subscription.getPlanQuantity()); } else if (plan.getPeriodUnit() ==
	 * PeriodUnitType.YEAR) { trialEnd.add(Calendar.YEAR, subscription.getPlanQuantity()); } }
	 * subscription.setEndDate(trialEnd.getTime()); } else { subscription.setTrialQuantity(0);
	 * LOG.info("Buyer probably extending/changing subscription"); // This will happen during renewal // No need to set
	 * paid Start as it already comes from the UI. Calendar paidEnd = Calendar.getInstance();
	 * paidEnd.setTime(subscription.getStartDate()); if (plan.getPeriodUnit() == PeriodUnitType.MONTH) {
	 * paidEnd.add(Calendar.MONTH, subscription.getPlanQuantity()); } else if (plan.getPeriodUnit() ==
	 * PeriodUnitType.YEAR) { paidEnd.add(Calendar.YEAR, subscription.getPlanQuantity()); }
	 * subscription.setEndDate(paidEnd.getTime()); } if (subscription.getUserLimit() == null) {
	 * subscription.setUserLimit(subscription.getPlan().getUserLimit()); } if (subscription.getEventLimit() == null) {
	 * subscription.setEventLimit(subscription.getPlan().getUserLimit()); } LOG.info("Computed subscription : " +
	 * subscription.toLogString()); } }
	 */

	@Override
	@Transactional(readOnly = false)
	public Subscription updateSubscription(Subscription subscription) {
		return subscriptionDao.update(subscription);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierSubscription updateSupplierSubscription(SupplierSubscription subscription) {
		return supplierSubscriptionDao.update(subscription);
	}

	@Override
	public Subscription getSubscriptionById(String subscriptionId) {
		return subscriptionDao.getSubscriptionById(subscriptionId);
	}

	@Override
	public SupplierSubscription getSupplierSubscriptionById(String subscriptionId) {
		return supplierSubscriptionDao.getSubscriptionById(subscriptionId);
	}

	@Override
	@Transactional(readOnly = false)
	public PaymentTransaction savePaymentTransaction(PaymentTransaction paymentTransaction) {
		return paymentTransactionDao.saveOrUpdate(paymentTransaction);
	}

	@Override
	@Transactional(readOnly = false)
	public PaymentTransaction updatePaymentTransaction(PaymentTransaction paymentTransaction) {
		return paymentTransactionDao.update(paymentTransaction);
	}

	@Override
	public PaymentTransaction getPaymentTransactionById(String paymentTransactionId) {
		return paymentTransactionDao.getPaymentTransactionById(paymentTransactionId);
	}

	@Override
	public Subscription getCurrentSubscriptionForBuyer(String buyerId) {
		Subscription subs = subscriptionDao.getCurrentSubscriptionForBuyer(buyerId);
		if (subs != null && subs.getNextSubscription() != null) {
			Subscription nxt = subs.getNextSubscription();
			nxt.getActivatedDate();
			nxt.getPlan().getCurrency();
			nxt.getPaymentTransaction().getAmount();
			if (nxt.getNextSubscription() != null) {
				Subscription nxt1 = nxt.getNextSubscription();
				nxt1.getActivatedDate();
				nxt1.getPlan().getCurrency();
				nxt1.getPaymentTransaction().getAmount();
				if (nxt1.getNextSubscription() != null) {
					Subscription nxt2 = nxt1.getNextSubscription();
					nxt2.getActivatedDate();
					nxt2.getPlan().getCurrency();
					nxt2.getPaymentTransaction().getAmount();
					if (nxt2.getNextSubscription() != null) {
						Subscription nxt3 = nxt2.getNextSubscription();
						nxt3.getActivatedDate();
						nxt3.getPlan().getCurrency();
						nxt3.getPaymentTransaction().getAmount();
					}
				}
			}
		}
		return subs;
	}

	/*
	 * @Override
	 * @Transactional(readOnly = false) public String initiatePaypalPaymentForTopup(Subscription subscription, String
	 * planId, String buyerId, HttpSession session, String returnURL, String cancelURL) { Plan plan =
	 * planService.getPlanForEditById(planId); // Buyer buyer = buyerService.findBuyerById(buyerId); // The
	 * paymentAmount is the total value of the shopping cart int paymentAmount = plan.getPrice() *
	 * subscription.getPlanQuantity(); if (plan.getChargeModel() == ChargeModel.PER_UNIT && subscription.getEventLimit()
	 * != null) { paymentAmount *= subscription.getEventLimit(); } // The currencyCodeType and paymentType are set to
	 * the selections made on the Integration Assistant String currencyCodeType = plan.getCurrency().getCurrencyCode();
	 * String paymentType = "Sale"; // boolean bSandbox = true; // if (bSandbox == true) { // PAYPAL_URL =
	 * "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token="; // } else { // PAYPAL_URL =
	 * "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="; // } //
	 * session.setAttribute("currencyCodeType", currencyCodeType); // session.setAttribute("paymentAmount",
	 * paymentAmount); // session.setAttribute("paymentType", paymentType); // session.setAttribute("quantity",
	 * subscription.getPlanQuantity()); // Create an instance of PaymentTransaction at the beginning of payment flow.
	 * PaymentTransaction paymentTransaction = subscription.getPaymentTransaction(); paymentTransaction.setPlan(plan);
	 * paymentTransaction.setCreatedDate(new Date()); paymentTransaction.setCurrencyCode(currencyCodeType);
	 * paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
	 * paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS); paymentTransaction.setType(TransactionType.PAYMENT);
	 * paymentTransaction.setAmount(new BigDecimal(paymentAmount)); paymentTransaction =
	 * savePaymentTransaction(paymentTransaction); // The returnURL is the location where buyers return to when a
	 * payment has been succesfully authorized. returnURL += paymentTransaction.getId(); // The cancelURL is the
	 * location buyers are sent to when they hit the cancel button during authorization of // payment during the PayPal
	 * flow cancelURL += paymentTransaction.getId(); // Construct the parameter string that describes the PayPal
	 * payment. String nvpstr = "&PAYMENTREQUEST_0_AMT=" + paymentAmount + "&PAYMENTREQUEST_0_PAYMENTACTION=" +
	 * paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType +
	 * "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan."; // No Shipping required nvpstr +=
	 * "&NOSHIPPING=1"; // Local code // nvpstr += "&LOCALECODE=MY"; // Business Name Image // nvpstr += "&HDRIMG="; //
	 * Background Color of checkout page nvpstr += "&PAYFLOWCOLOR=0095D5"; // SOLUTIONTYPE nvpstr +=
	 * "&SOLUTIONTYPE=Sole"; // or Mark // Cart Border Color nvpstr += "&CARTBORDERCOLOR=0095D5"; // Logo Image //
	 * nvpstr += "&LOGOIMG=" + URLEncoder.encode(request.getScheme() + "://" + request.getServerName() + ":" + //
	 * request.getServerPort() + request.getContextPath() + "/resources/images/pro_logo.png"); // Buyer Email nvpstr +=
	 * "&EMAIL=" + subscription.getPaymentTransaction().getCommunicationEmail(); // Landing Page - Type of PayPal page
	 * to display nvpstr += "&LANDINGPAGE=Billing"; // or LANDINGPAGE=Login String note = "You are subscribing for "; if
	 * (plan.getChargeModel() == ChargeModel.PER_UNIT) { note += (subscription.getEventLimit() == null ? 0 :
	 * subscription.getEventLimit()) + " Credits each for " + subscription.getPlanQuantity() + " " +
	 * plan.getPeriodUnit() + "S"; } else { note += subscription.getPlanQuantity() + " " + plan.getPeriodUnit() + "S"; }
	 * // Note to Buyer nvpstr += "&NOTETOBUYER=" + note;
	 *//**
		 * Item Details
		 */
	/*
	 * // Item Amount nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + paymentAmount; // Total Tax nvpstr +=
	 * "&PAYMENTREQUEST_0_TAXAMT=0"; // Total Shipping nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0"; // Item Name nvpstr
	 * += "&L_PAYMENTREQUEST_0_NAME0=" + plan.getPlanName(); // Item Description nvpstr += "&L_PAYMENTREQUEST_0_DESC0="
	 * + plan.getShortDescription(); // Item Amount nvpstr += "&L_PAYMENTREQUEST_0_AMT0=" + (plan.getPrice() *
	 * (plan.getChargeModel() == ChargeModel.PER_UNIT && subscription.getEventLimit() != null ?
	 * subscription.getEventLimit() : 1)); // Item Qty nvpstr += "&L_PAYMENTREQUEST_0_QTY0=" +
	 * subscription.getPlanQuantity(); // Item Tax nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT0=0"; // Item Url // nvpstr +=
	 * "&L_PAYMENTREQUEST_0_ITEMURL0=" + URLEncoder.encode(request.getScheme() + "://" + // request.getServerName() +
	 * ":" + request.getServerPort() + request.getContextPath() + // "/subscription/selectPlan/" + planId); nvpstr +=
	 * "&ReturnUrl=" + URLEncoder.encode(returnURL) + "&CANCELURL=" + URLEncoder.encode(cancelURL); // Make the call to
	 * PayPal to get the Express Checkout token If the API call succeded, then redirect the buyer // to PayPal to begin
	 * to authorize payment. If an error occured, show the resulting errors
	 * LOG.info("Sending to payment gateway with data : " + nvpstr); // Calls the SetExpressCheckout API call
	 * LOG.info("Invoking Paypal Service Express Checkout ============ >"); HashMap<String, String> nvp =
	 * invokePaypalService("SetExpressCheckout", nvpstr); LOG.info("Payment gateway response : " + nvp.get("ACK"));
	 * String strAck = nvp.get("ACK").toString(); if (strAck != null &&
	 * strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) { session.setAttribute(SubscriptionServiceImpl.TOKEN,
	 * nvp.get(SubscriptionServiceImpl.TOKEN).toString()); LOG.info(" TOKEN : " +
	 * nvp.get(SubscriptionServiceImpl.TOKEN)); // We dont need these for saving the subscription Buyer buyer =
	 * subscription.getBuyer(); subscription.setBuyer(null); subscription.setPlan(plan); subscription.setCreatedDate(new
	 * Date()); subscription.setCurrencyCode(currencyCodeType); subscription.setPaymentTransaction(paymentTransaction);
	 * if (subscription.getEventLimit() == null) { subscription.setEventLimit(plan.getEventLimit()); } if
	 * (subscription.getUserLimit() == null) { subscription.setUserLimit(plan.getUserLimit()); } subscription =
	 * saveSubscription(subscription); // Save the TOKEN in DB paymentTransaction.setSubscription(subscription);
	 * paymentTransaction.setPaymentToken(nvp.get(SubscriptionServiceImpl.TOKEN)); paymentTransaction =
	 * updatePaymentTransaction(paymentTransaction); // Reassign the values subscription.setBuyer(buyer);
	 * session.setAttribute("subscriptionId", subscription.getId()); session.setAttribute("paymentTransactionId",
	 * paymentTransaction.getId()); session.setAttribute("buyer", buyer); String payPalURL = PAYPAL_URL +
	 * nvp.get(SubscriptionServiceImpl.TOKEN); return "redirect:" + payPalURL; } else { paymentTransaction =
	 * markTransactionFailed(paymentTransaction, nvp); } return "redirect:subscriptionError/" + plan.getId() + "/" +
	 * paymentTransaction.getId(); }
	 */

	/*
	 * @Override
	 * @Transactional(readOnly = false) public String initiatePaypalPaymentForRenew(Subscription subscription, String
	 * planId, String buyerId, HttpSession session, String returnURL, String cancelURL) { Plan plan =
	 * planService.getPlanForEditById(planId); // Buyer buyer = buyerService.findBuyerById(buyerId); // The
	 * paymentAmount is the total value of the shopping cart int paymentAmount = plan.getPrice() *
	 * subscription.getPlanQuantity(); if (plan.getChargeModel() == ChargeModel.PER_UNIT && subscription.getEventLimit()
	 * != null) { paymentAmount *= subscription.getEventLimit(); } // The currencyCodeType and paymentType are set to
	 * the selections made on the Integration Assistant String currencyCodeType = plan.getCurrency().getCurrencyCode();
	 * String paymentType = "Sale"; // boolean bSandbox = true; // if (bSandbox == true) { // PAYPAL_URL =
	 * "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token="; // } else { // PAYPAL_URL =
	 * "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="; // } //
	 * session.setAttribute("currencyCodeType", currencyCodeType); // session.setAttribute("paymentAmount",
	 * paymentAmount); // session.setAttribute("paymentType", paymentType); // session.setAttribute("quantity",
	 * subscription.getPlanQuantity()); // Create an instance of PaymentTransaction at the beginning of payment flow.
	 * PaymentTransaction paymentTransaction = subscription.getPaymentTransaction(); paymentTransaction.setPlan(plan);
	 * paymentTransaction.setCreatedDate(new Date()); paymentTransaction.setCurrencyCode(currencyCodeType);
	 * paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
	 * paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS); paymentTransaction.setType(TransactionType.PAYMENT);
	 * paymentTransaction.setAmount(new BigDecimal(paymentAmount)); paymentTransaction =
	 * savePaymentTransaction(paymentTransaction); // The returnURL is the location where buyers return to when a
	 * payment has been succesfully authorized. returnURL += paymentTransaction.getId(); // The cancelURL is the
	 * location buyers are sent to when they hit the cancel button during authorization of // payment during the PayPal
	 * flow cancelURL += paymentTransaction.getId(); // Construct the parameter string that describes the PayPal
	 * payment. String nvpstr = "&PAYMENTREQUEST_0_AMT=" + paymentAmount + "&PAYMENTREQUEST_0_PAYMENTACTION=" +
	 * paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType +
	 * "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan."; // No Shipping required nvpstr +=
	 * "&NOSHIPPING=1"; // Local code // nvpstr += "&LOCALECODE=MY"; // Business Name Image // nvpstr += "&HDRIMG="; //
	 * Background Color of checkout page nvpstr += "&PAYFLOWCOLOR=0095D5"; // SOLUTIONTYPE nvpstr +=
	 * "&SOLUTIONTYPE=Sole"; // or Mark // Cart Border Color nvpstr += "&CARTBORDERCOLOR=0095D5"; // Logo Image //
	 * nvpstr += "&LOGOIMG=" + URLEncoder.encode(request.getScheme() + "://" + request.getServerName() + ":" + //
	 * request.getServerPort() + request.getContextPath() + "/resources/images/pro_logo.png"); // Buyer Email nvpstr +=
	 * "&EMAIL=" + subscription.getPaymentTransaction().getCommunicationEmail(); // Landing Page - Type of PayPal page
	 * to display nvpstr += "&LANDINGPAGE=Billing"; // or LANDINGPAGE=Login String note =
	 * "You are renewing subscription for "; if (plan.getChargeModel() == ChargeModel.PER_UNIT) { note +=
	 * (subscription.getEventLimit() == null ? 0 : subscription.getEventLimit()) + " Credits each for " +
	 * subscription.getPlanQuantity() + " " + plan.getPeriodUnit() + "S"; } else { note +=
	 * subscription.getPlanQuantity() + " " + plan.getPeriodUnit() + "S"; } // Note to Buyer nvpstr += "&NOTETOBUYER=" +
	 * note;
	 *//**
		 * Item Details
		 */
	/*
	 * // Item Amount nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + paymentAmount; // Total Tax nvpstr +=
	 * "&PAYMENTREQUEST_0_TAXAMT=0"; // Total Shipping nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0"; // Item Name nvpstr
	 * += "&L_PAYMENTREQUEST_0_NAME0=" + plan.getPlanName(); // Item Description nvpstr += "&L_PAYMENTREQUEST_0_DESC0="
	 * + plan.getShortDescription(); // Item Amount nvpstr += "&L_PAYMENTREQUEST_0_AMT0=" + (plan.getPrice() *
	 * (plan.getChargeModel() == ChargeModel.PER_UNIT && subscription.getEventLimit() != null ?
	 * subscription.getEventLimit() : 1)); // Item Qty nvpstr += "&L_PAYMENTREQUEST_0_QTY0=" +
	 * subscription.getPlanQuantity(); // Item Tax nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT0=0"; // Item Url // nvpstr +=
	 * "&L_PAYMENTREQUEST_0_ITEMURL0=" + URLEncoder.encode(request.getScheme() + "://" + // request.getServerName() +
	 * ":" + request.getServerPort() + request.getContextPath() + // "/subscription/selectPlan/" + planId); nvpstr +=
	 * "&ReturnUrl=" + URLEncoder.encode(returnURL) + "&CANCELURL=" + URLEncoder.encode(cancelURL); // Make the call to
	 * PayPal to get the Express Checkout token If the API call succeded, then redirect the buyer // to PayPal to begin
	 * to authorize payment. If an error occured, show the resulting errors
	 * LOG.info("Sending to payment gateway with data : " + nvpstr); // Calls the SetExpressCheckout API call
	 * LOG.info("Invoking Paypal Service Express Checkout ============ >"); HashMap<String, String> nvp =
	 * invokePaypalService("SetExpressCheckout", nvpstr); LOG.info("Payment gateway response : " + nvp.get("ACK"));
	 * String strAck = nvp.get("ACK").toString(); if (strAck != null &&
	 * strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) { session.setAttribute(SubscriptionServiceImpl.TOKEN,
	 * nvp.get(SubscriptionServiceImpl.TOKEN).toString()); LOG.info(" TOKEN : " +
	 * nvp.get(SubscriptionServiceImpl.TOKEN)); subscription.setPlan(plan); subscription.setCreatedDate(new Date());
	 * subscription.setCurrencyCode(currencyCodeType); subscription.setPaymentTransaction(paymentTransaction); if
	 * (subscription.getEventLimit() == null) { subscription.setEventLimit(plan.getEventLimit()); } if
	 * (subscription.getUserLimit() == null) { subscription.setUserLimit(plan.getUserLimit()); } subscription =
	 * saveSubscription(subscription); // Save the TOKEN in DB paymentTransaction.setSubscription(subscription);
	 * paymentTransaction.setPaymentToken(nvp.get(SubscriptionServiceImpl.TOKEN)); paymentTransaction =
	 * updatePaymentTransaction(paymentTransaction); Buyer buyer = buyerService.findBuyerById(buyerId);
	 * session.setAttribute("subscriptionId", subscription.getId()); session.setAttribute("paymentTransactionId",
	 * paymentTransaction.getId()); session.setAttribute("buyer", buyer); String payPalURL = PAYPAL_URL +
	 * nvp.get(SubscriptionServiceImpl.TOKEN); return "redirect:" + payPalURL; } else { paymentTransaction =
	 * markTransactionFailed(paymentTransaction, nvp); } return "redirect:subscriptionError/" + plan.getId() + "/" +
	 * paymentTransaction.getId(); }
	 */

	/*
	 * @Override
	 * @Transactional(readOnly = false) public String initiatePaypalPaymentForChangeOfPlan(Subscription subscription,
	 * String planId, String buyerId, HttpSession session, String returnURL, String cancelURL) { Plan plan =
	 * planService.getPlanForEditById(planId); // Buyer buyer = buyerService.findBuyerById(buyerId); // The
	 * paymentAmount is the total value of the shopping cart int paymentAmount = plan.getPrice() *
	 * subscription.getPlanQuantity(); if (plan.getChargeModel() == ChargeModel.PER_UNIT && subscription.getEventLimit()
	 * != null) { paymentAmount *= subscription.getEventLimit(); } // The currencyCodeType and paymentType are set to
	 * the selections made on the Integration Assistant String currencyCodeType = plan.getCurrency().getCurrencyCode();
	 * String paymentType = "Sale"; // boolean bSandbox = true; // if (bSandbox == true) { // PAYPAL_URL =
	 * "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token="; // } else { // PAYPAL_URL =
	 * "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="; // } //
	 * session.setAttribute("currencyCodeType", currencyCodeType); // session.setAttribute("paymentAmount",
	 * paymentAmount); // session.setAttribute("paymentType", paymentType); // session.setAttribute("quantity",
	 * subscription.getPlanQuantity()); // Create an instance of PaymentTransaction at the beginning of payment flow.
	 * PaymentTransaction paymentTransaction = subscription.getPaymentTransaction(); paymentTransaction.setPlan(plan);
	 * paymentTransaction.setCreatedDate(new Date()); paymentTransaction.setCurrencyCode(currencyCodeType);
	 * paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
	 * paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS); paymentTransaction.setType(TransactionType.PAYMENT);
	 * paymentTransaction.setAmount(new BigDecimal(paymentAmount)); paymentTransaction =
	 * savePaymentTransaction(paymentTransaction); // The returnURL is the location where buyers return to when a
	 * payment has been succesfully authorized. returnURL += paymentTransaction.getId(); // The cancelURL is the
	 * location buyers are sent to when they hit the cancel button during authorization of // payment during the PayPal
	 * flow cancelURL += paymentTransaction.getId(); // Construct the parameter string that describes the PayPal
	 * payment. String nvpstr = "&PAYMENTREQUEST_0_AMT=" + paymentAmount + "&PAYMENTREQUEST_0_PAYMENTACTION=" +
	 * paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType +
	 * "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan."; // No Shipping required nvpstr +=
	 * "&NOSHIPPING=1"; // Local code // nvpstr += "&LOCALECODE=MY"; // Business Name Image // nvpstr += "&HDRIMG="; //
	 * Background Color of checkout page nvpstr += "&PAYFLOWCOLOR=0095D5"; // SOLUTIONTYPE nvpstr +=
	 * "&SOLUTIONTYPE=Sole"; // or Mark // Cart Border Color nvpstr += "&CARTBORDERCOLOR=0095D5"; // Logo Image //
	 * nvpstr += "&LOGOIMG=" + URLEncoder.encode(request.getScheme() + "://" + request.getServerName() + ":" + //
	 * request.getServerPort() + request.getContextPath() + "/resources/images/pro_logo.png"); // Buyer Email nvpstr +=
	 * "&EMAIL=" + subscription.getPaymentTransaction().getCommunicationEmail(); // Landing Page - Type of PayPal page
	 * to display nvpstr += "&LANDINGPAGE=Billing"; // or LANDINGPAGE=Login String note = "You are changing plan to " +
	 * plan.getPlanName() + " for "; if (plan.getChargeModel() == ChargeModel.PER_UNIT) { note +=
	 * (subscription.getEventLimit() == null ? 0 : subscription.getEventLimit()) + " Credits each for " +
	 * subscription.getPlanQuantity() + " " + plan.getPeriodUnit() + "S"; } else { note +=
	 * subscription.getPlanQuantity() + " " + plan.getPeriodUnit() + "S"; } // Note to Buyer nvpstr += "&NOTETOBUYER=" +
	 * note;
	 *//**
		 * Item Details
		 */
	/*
	 * // Item Amount nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + paymentAmount; // Total Tax nvpstr +=
	 * "&PAYMENTREQUEST_0_TAXAMT=0"; // Total Shipping nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0"; // Item Name nvpstr
	 * += "&L_PAYMENTREQUEST_0_NAME0=" + plan.getPlanName(); // Item Description nvpstr += "&L_PAYMENTREQUEST_0_DESC0="
	 * + plan.getShortDescription(); // Item Amount nvpstr += "&L_PAYMENTREQUEST_0_AMT0=" + (plan.getPrice() *
	 * (plan.getChargeModel() == ChargeModel.PER_UNIT && subscription.getEventLimit() != null ?
	 * subscription.getEventLimit() : 1)); // Item Qty nvpstr += "&L_PAYMENTREQUEST_0_QTY0=" +
	 * subscription.getPlanQuantity(); // Item Tax nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT0=0"; // Item Url // nvpstr +=
	 * "&L_PAYMENTREQUEST_0_ITEMURL0=" + URLEncoder.encode(request.getScheme() + "://" + // request.getServerName() +
	 * ":" + request.getServerPort() + request.getContextPath() + // "/subscription/selectPlan/" + planId); nvpstr +=
	 * "&ReturnUrl=" + URLEncoder.encode(returnURL) + "&CANCELURL=" + URLEncoder.encode(cancelURL); // Make the call to
	 * PayPal to get the Express Checkout token If the API call succeded, then redirect the buyer // to PayPal to begin
	 * to authorize payment. If an error occured, show the resulting errors
	 * LOG.info("Sending to payment gateway with data : " + nvpstr); // Calls the SetExpressCheckout API call
	 * LOG.info("Invoking Paypal Service Express Checkout ============ >"); HashMap<String, String> nvp =
	 * invokePaypalService("SetExpressCheckout", nvpstr); LOG.info("Payment gateway response : " + nvp.get("ACK"));
	 * String strAck = nvp.get("ACK").toString(); if (strAck != null &&
	 * strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) { session.setAttribute(SubscriptionServiceImpl.TOKEN,
	 * nvp.get(SubscriptionServiceImpl.TOKEN).toString()); LOG.info(" TOKEN : " +
	 * nvp.get(SubscriptionServiceImpl.TOKEN)); subscription.setPlan(plan); subscription.setCreatedDate(new Date());
	 * subscription.setCurrencyCode(currencyCodeType); subscription.setPaymentTransaction(paymentTransaction); if
	 * (subscription.getEventLimit() == null) { subscription.setEventLimit(plan.getEventLimit()); } if
	 * (subscription.getUserLimit() == null) { subscription.setUserLimit(plan.getUserLimit()); } subscription =
	 * saveSubscription(subscription); // Save the TOKEN in DB paymentTransaction.setSubscription(subscription);
	 * paymentTransaction.setPaymentToken(nvp.get(SubscriptionServiceImpl.TOKEN)); paymentTransaction =
	 * updatePaymentTransaction(paymentTransaction); Buyer buyer = buyerService.findBuyerById(buyerId);
	 * session.setAttribute("subscriptionId", subscription.getId()); session.setAttribute("paymentTransactionId",
	 * paymentTransaction.getId()); session.setAttribute("buyer", buyer); String payPalURL = PAYPAL_URL +
	 * nvp.get(SubscriptionServiceImpl.TOKEN); return "redirect:" + payPalURL; } else { paymentTransaction =
	 * markTransactionFailed(paymentTransaction, nvp); } return "redirect:subscriptionError/" + plan.getId() + "/" +
	 * paymentTransaction.getId(); }
	 */

	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	/*
	 * @Override
	 * @Transactional(readOnly = false, rollbackFor = Exception.class) public User doTrialSubscription(Subscription
	 * subscription, String planId, Model model) throws Exception { try { Plan plan =
	 * planService.getPlanForEditById(planId); int paymentAmount = plan.getPrice(); String currencyCodeType =
	 * plan.getCurrency().getCurrencyCode(); // Create an instance of PaymentTransaction at the beginning of payment
	 * flow. PaymentTransaction paymentTransaction = subscription.getPaymentTransaction();
	 * paymentTransaction.setPlan(plan); paymentTransaction.setCreatedDate(new Date());
	 * paymentTransaction.setCurrencyCode(currencyCodeType); paymentTransaction.setPaymentMethod(PaymentMethod.OTHER);
	 * paymentTransaction.setStatus(TransactionStatus.SUCCESS); paymentTransaction.setType(TransactionType.PAYMENT);
	 * paymentTransaction.setAmount(new BigDecimal(paymentAmount)); paymentTransaction.setConfirmationDate(new Date());
	 * paymentTransaction.setPaymentFee(BigDecimal.ZERO); paymentTransaction =
	 * savePaymentTransaction(paymentTransaction); // We dont need these for saving the subscription // Buyer buyer =
	 * subscription.getBuyer(); subscription.setBuyer(null); subscription.setTrialQuantity(null); // else it will be
	 * reset to zero. Better set it to null subscription.setPlan(plan); subscription.setCreatedDate(new Date());
	 * subscription.setCurrencyCode(currencyCodeType); subscription.setPaymentTransaction(paymentTransaction); if
	 * (subscription.getEventLimit() == null) { subscription.setEventLimit(plan.getEventLimit()); } if
	 * (subscription.getUserLimit() == null) { subscription.setUserLimit(plan.getUserLimit()); } subscription =
	 * saveSubscription(subscription); // Save the TOKEN in DB paymentTransaction.setSubscription(subscription);
	 * paymentTransaction = updatePaymentTransaction(paymentTransaction); // Create the Buyer account and send out email
	 * notification for account activiation. Buyer buyer = new Buyer();
	 * buyer.setLoginEmail(paymentTransaction.getLoginEmail());
	 * buyer.setCommunicationEmail(paymentTransaction.getCommunicationEmail());
	 * buyer.setCompanyContactNumber(paymentTransaction.getCompanyContactNumber());
	 * buyer.setCompanyName(paymentTransaction.getCompanyName());
	 * buyer.setCompanyRegistrationNumber(paymentTransaction.getCompanyRegistrationNumber()); //
	 * buyer.setCurrentSubscription(subscription); buyer.setFullName(paymentTransaction.getFullName());
	 * //buyer.setPlan(paymentTransaction.getPlan()); buyer.setRegistrationOfCountry(paymentTransaction.getCountry());
	 * buyer.setStatus(BuyerStatus.PENDING); buyer.setSubscriptionDate(new Date()); User user =
	 * buyerService.saveBuyer(buyer); if (user != null) { user.getBuyer().getCommunicationEmail(); }
	 * subscription.setBuyer(user.getBuyer()); doComputeSubscription(subscription, null); BuyerPackage bp = new
	 * BuyerPackage(); //new BuyerPackage(subscription); subscription = updateSubscription(subscription);
	 * buyer.setBuyerPackage(bp); LOG.info("Buyer Id : " + buyer.getId()); buyer = buyerService.updateBuyer(buyer); //
	 * Update transaction details with buyer info paymentTransaction.setBuyer(buyer); paymentTransaction =
	 * updatePaymentTransaction(paymentTransaction); model.addAttribute("subscription", subscription);
	 * model.addAttribute("paymentTransaction", paymentTransaction); return user; } catch (Exception e) {
	 * LOG.error("Error during trial subscription : " + e.getMessage()); throw e; } }
	 */
	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param request
	 * @return
	 */
	/*
	 * @Override
	 * @Transactional(readOnly = false) public String initiatePaypalPayment(Subscription subscription, String planId,
	 * HttpSession session, String returnURL, String cancelURL) { Plan plan = planService.getPlanForEditById(planId); //
	 * The paymentAmount is the total value of the shopping cart int paymentAmount = plan.getPrice() *
	 * subscription.getPlanQuantity(); if (plan.getChargeModel() == ChargeModel.PER_UNIT && subscription.getEventLimit()
	 * != null) { paymentAmount *= subscription.getEventLimit(); } // The currencyCodeType and paymentType are set to
	 * the selections made on the Integration Assistant String currencyCodeType = plan.getCurrency().getCurrencyCode();
	 * String paymentType = "Sale"; // boolean bSandbox = true; // if (bSandbox == true) { // PAYPAL_URL =
	 * "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token="; // } else { // PAYPAL_URL =
	 * "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="; // } //
	 * session.setAttribute("currencyCodeType", currencyCodeType); // session.setAttribute("paymentAmount",
	 * paymentAmount); // session.setAttribute("paymentType", paymentType); // session.setAttribute("quantity",
	 * subscription.getPlanQuantity()); // Create an instance of PaymentTransaction at the beginning of payment flow.
	 * PaymentTransaction paymentTransaction = subscription.getPaymentTransaction(); paymentTransaction.setPlan(plan);
	 * paymentTransaction.setCreatedDate(new Date()); paymentTransaction.setCurrencyCode(currencyCodeType);
	 * paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
	 * paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS); paymentTransaction.setType(TransactionType.PAYMENT);
	 * paymentTransaction.setAmount(new BigDecimal(paymentAmount)); paymentTransaction =
	 * savePaymentTransaction(paymentTransaction); // The returnURL is the location where buyers return to when a
	 * payment has been succesfully authorized. returnURL += paymentTransaction.getId(); // The cancelURL is the
	 * location buyers are sent to when they hit the cancel button during authorization of // payment during the PayPal
	 * flow cancelURL += paymentTransaction.getId(); // Construct the parameter string that describes the PayPal
	 * payment. String nvpstr = "&PAYMENTREQUEST_0_AMT=" + paymentAmount + "&PAYMENTREQUEST_0_PAYMENTACTION=" +
	 * paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType +
	 * "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan."; // No Shipping required nvpstr +=
	 * "&NOSHIPPING=1"; // Local code // nvpstr += "&LOCALECODE=MY"; // Business Name Image // nvpstr += "&HDRIMG="; //
	 * Background Color of checkout page nvpstr += "&PAYFLOWCOLOR=0095D5"; // SOLUTIONTYPE nvpstr +=
	 * "&SOLUTIONTYPE=Sole"; // or Mark // Cart Border Color nvpstr += "&CARTBORDERCOLOR=0095D5"; // Logo Image //
	 * nvpstr += "&LOGOIMG=" + URLEncoder.encode(request.getScheme() + "://" + request.getServerName() + ":" + //
	 * request.getServerPort() + request.getContextPath() + "/resources/images/pro_logo.png"); // Buyer Email nvpstr +=
	 * "&EMAIL=" + subscription.getPaymentTransaction().getCommunicationEmail(); // Landing Page - Type of PayPal page
	 * to display nvpstr += "&LANDINGPAGE=Billing"; // or LANDINGPAGE=Login String note = "You are subscribing for "; if
	 * (plan.getChargeModel() == ChargeModel.PER_UNIT) { note += (subscription.getEventLimit() == null ? 0 :
	 * subscription.getEventLimit()) + " Credits each for " + subscription.getPlanQuantity() + " " +
	 * plan.getPeriodUnit() + "S"; } else { note += subscription.getPlanQuantity() + " " + plan.getPeriodUnit() + "S"; }
	 * // Note to Buyer nvpstr += "&NOTETOBUYER=" + note;
	 *//**
		 * Item Details
		 */
	/*
	 * // Item Amount nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + paymentAmount; // Total Tax nvpstr +=
	 * "&PAYMENTREQUEST_0_TAXAMT=0"; // Total Shipping nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0"; // Item Name nvpstr
	 * += "&L_PAYMENTREQUEST_0_NAME0=" + plan.getPlanName(); // Item Description nvpstr += "&L_PAYMENTREQUEST_0_DESC0="
	 * + plan.getShortDescription(); // Item Amount nvpstr += "&L_PAYMENTREQUEST_0_AMT0=" + (plan.getPrice() *
	 * (plan.getChargeModel() == ChargeModel.PER_UNIT && subscription.getEventLimit() != null ?
	 * subscription.getEventLimit() : 1)); // Item Qty nvpstr += "&L_PAYMENTREQUEST_0_QTY0=" +
	 * subscription.getPlanQuantity(); // Item Tax nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT0=0"; // Item Url // nvpstr +=
	 * "&L_PAYMENTREQUEST_0_ITEMURL0=" + URLEncoder.encode(request.getScheme() + "://" + // request.getServerName() +
	 * ":" + request.getServerPort() + request.getContextPath() + // "/subscription/selectPlan/" + planId); nvpstr +=
	 * "&ReturnUrl=" + URLEncoder.encode(returnURL) + "&CANCELURL=" + URLEncoder.encode(cancelURL); // Make the call to
	 * PayPal to get the Express Checkout token If the API call succeded, then redirect the buyer // to PayPal to begin
	 * to authorize payment. If an error occured, show the resulting errors
	 * LOG.info("Sending to payment gateway with data : " + nvpstr); // Calls the SetExpressCheckout API call
	 * LOG.info("Invoking Paypal Service Express Checkout ============ >"); HashMap<String, String> nvp =
	 * invokePaypalService("SetExpressCheckout", nvpstr); LOG.info("Payment gateway response : " + nvp.get("ACK"));
	 * String strAck = nvp.get("ACK").toString(); if (strAck != null &&
	 * strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) { session.setAttribute(SubscriptionServiceImpl.TOKEN,
	 * nvp.get(SubscriptionServiceImpl.TOKEN).toString()); LOG.info(" TOKEN : " +
	 * nvp.get(SubscriptionServiceImpl.TOKEN)); // We dont need these for saving the subscription Buyer buyer =
	 * subscription.getBuyer(); subscription.setBuyer(null); subscription.setPlan(plan); subscription.setCreatedDate(new
	 * Date()); subscription.setCurrencyCode(currencyCodeType); subscription.setPaymentTransaction(paymentTransaction);
	 * if (subscription.getEventLimit() == null) { subscription.setEventLimit(plan.getEventLimit()); } if
	 * (subscription.getUserLimit() == null) { subscription.setUserLimit(plan.getUserLimit()); } subscription =
	 * saveSubscription(subscription); // Save the TOKEN in DB paymentTransaction.setSubscription(subscription);
	 * paymentTransaction.setPaymentToken(nvp.get(SubscriptionServiceImpl.TOKEN)); paymentTransaction =
	 * updatePaymentTransaction(paymentTransaction); // Reassign the values subscription.setBuyer(buyer);
	 * session.setAttribute("subscriptionId", subscription.getId()); session.setAttribute("paymentTransactionId",
	 * paymentTransaction.getId()); session.setAttribute("buyer", buyer); String payPalURL = PAYPAL_URL +
	 * nvp.get(SubscriptionServiceImpl.TOKEN); return "redirect:" + payPalURL; } else { paymentTransaction =
	 * markTransactionFailed(paymentTransaction, nvp); } return "redirect:../subscriptionError/" + plan.getId() + "/" +
	 * paymentTransaction.getId(); }
	 */

	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param request
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	public String initiateSupplierPaypalPayment(SupplierSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL) {
		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);

		// The paymentAmount is the total value of the shopping cart
		int paymentAmount = plan.getPrice();

		Supplier supplier = subscription.getSupplier();
		BigDecimal tax = BigDecimal.ZERO;
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

		// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
		String currencyCodeType = plan.getCurrency().getCurrencyCode();
		String paymentType = "Sale";

		// boolean bSandbox = true;
		// if (bSandbox == true) {
		// PAYPAL_URL = "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=";
		// } else {
		// PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
		// }

		// session.setAttribute("currencyCodeType", currencyCodeType);
		// session.setAttribute("paymentAmount", paymentAmount);
		// session.setAttribute("paymentType", paymentType);
		// session.setAttribute("quantity", subscription.getPlanQuantity());

		// Create an instance of PaymentTransaction at the beginning of payment flow.
		PaymentTransaction paymentTransaction = subscription.getPaymentTransaction();
		paymentTransaction.setSupplierPlan(plan);
		paymentTransaction.setCreatedDate(new Date());
		paymentTransaction.setCurrencyCode(currencyCodeType);
		paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
		paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
		paymentTransaction.setType(TransactionType.PAYMENT);
		paymentTransaction.setAmount(new BigDecimal(paymentAmount));
		paymentTransaction.setCompanyRegistrationNumber(subscription.getSupplier().getCompanyRegistrationNumber());
		paymentTransaction.setCompanyName(subscription.getSupplier().getCompanyName());
		paymentTransaction.setCommunicationEmail(subscription.getSupplier().getCommunicationEmail());
		paymentTransaction = savePaymentTransaction(paymentTransaction);

		// The returnURL is the location where buyers return to when a payment has been succesfully authorized.
		returnURL += paymentTransaction.getId();

		// The cancelURL is the location buyers are sent to when they hit the cancel button during authorization of
		// payment during the PayPal flow
		cancelURL += paymentTransaction.getId();
		BigDecimal amount = new BigDecimal(paymentAmount);
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal totalTax = (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

		// LOG.info("amount: " + amount );
		// LOG.info("tax: " + tax );
		// LOG.info("amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP) : " + amount.multiply(tax).setScale(2,
		// BigDecimal.ROUND_HALF_UP) );
		// LOG.info("(amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)): " +
		// (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)) );

		// Construct the parameter string that describes the PayPal payment.
		String nvpstr = "&PAYMENTREQUEST_0_AMT=" + amount.add(totalTax) + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType + "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan.";

		// No Shipping required
		nvpstr += "&NOSHIPPING=1";

		// Local code
		// nvpstr += "&LOCALECODE=MY";

		// Business Name Image
		// nvpstr += "&HDRIMG=";

		// Background Color of checkout page
		nvpstr += "&PAYFLOWCOLOR=0095D5";

		// SOLUTIONTYPE
		nvpstr += "&SOLUTIONTYPE=Sole"; // or Mark

		// Cart Border Color
		nvpstr += "&CARTBORDERCOLOR=0095D5";

		// Logo Image
		// nvpstr += "&LOGOIMG=" + URLEncoder.encode(request.getScheme() + "://" + request.getServerName() + ":" +
		// request.getServerPort() + request.getContextPath() + "/resources/images/pro_logo.png");

		// Buyer Email
		nvpstr += "&EMAIL=" + subscription.getPaymentTransaction().getCommunicationEmail();

		// Landing Page - Type of PayPal page to display
		nvpstr += "&LANDINGPAGE=Billing"; // or LANDINGPAGE=Login
		String gstDesc = " (Inclusive of " + tax + "%25 Tax)";
		String note = "You are subscribing for " + plan.getPeriod() + " " + plan.getPeriodUnit() + "S" + gstDesc;

		// Note to Buyer
		nvpstr += "&NOTETOBUYER=" + note;

		/**
		 * Item Details
		 */
		// Item Amount
		nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + amount;
		// Total Tax
		nvpstr += "&PAYMENTREQUEST_0_TAXAMT=" + totalTax;
		// Total Shipping
		nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0";
		// Item Name
		nvpstr += "&L_PAYMENTREQUEST_0_NAME0=" + plan.getPlanName();
		// Item Description
		nvpstr += "&L_PAYMENTREQUEST_0_DESC0=" + plan.getShortDescription();
		// Item Amount
		nvpstr += "&L_PAYMENTREQUEST_0_AMT0=" + amount;
		// Item Qty
		nvpstr += "&L_PAYMENTREQUEST_0_QTY0=1";
		// Item Tax
		nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT0=0";
		// Item Url
		// nvpstr += "&L_PAYMENTREQUEST_0_ITEMURL0=" + URLEncoder.encode(request.getScheme() + "://" +
		// request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +
		// "/subscription/selectPlan/" + planId);

		nvpstr += "&ReturnUrl=" + URLEncoder.encode(returnURL) + "&CANCELURL=" + URLEncoder.encode(cancelURL);

		// Make the call to PayPal to get the Express Checkout token If the API call succeded, then redirect the buyer
		// to PayPal to begin to authorize payment. If an error occured, show the resulting errors
		LOG.info("Sending to payment gateway with data : " + nvpstr);

		// Calls the SetExpressCheckout API call
		LOG.info("Invoking Paypal Service Express Checkout ============ >");
		HashMap<String, String> nvp = invokePaypalService("SetExpressCheckout", nvpstr);
		if (nvp == null) {
			paymentTransaction.setStatus(TransactionStatus.FAILURE);
			paymentTransaction.setErrorCode("-1");
			paymentTransaction.setErrorMessage("Error invoking paypal service");
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			return "redirect:/supplier/supplierSubscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
		}
		LOG.info("Payment gateway response : " + nvp.get("ACK"));

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) {
			session.setAttribute(SubscriptionServiceImpl.TOKEN, nvp.get(SubscriptionServiceImpl.TOKEN).toString());
			LOG.info(" TOKEN : " + nvp.get(SubscriptionServiceImpl.TOKEN));

			// We dont need these for saving the subscription
			if (supplier.getId() == null) {
				subscription.setSupplier(null);
			}

			// subscription.setSupplierPlan(plan);
			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);
			subscription.setPaymentTransaction(paymentTransaction);
			if (subscription.getBuyerLimit() == null) {
				subscription.setBuyerLimit(plan.getBuyerLimit());
			}

			subscription = saveSupplierSubscription(subscription);

			// Save the TOKEN in DB
			paymentTransaction.setSupplierSubscription(subscription);
			paymentTransaction.setPaymentToken(nvp.get(SubscriptionServiceImpl.TOKEN));
			paymentTransaction.setAdditionalTaxDesc("Tax " + tax + "%");
			paymentTransaction.setAdditionalTax(totalTax);
			paymentTransaction.setPriceAmount(amount);
			paymentTransaction.setAmount(amount.add(totalTax));
			paymentTransaction.setTotalPriceAmount(amount.add(totalTax));
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			// Reassign the values
			subscription.setSupplier(supplier);

			session.setAttribute("subscriptionId", subscription.getId());
			session.setAttribute("paymentTransactionId", paymentTransaction.getId());
			session.setAttribute("supplier", supplier);

			String payPalURL = PAYPAL_URL + nvp.get(SubscriptionServiceImpl.TOKEN);
			return "redirect:" + payPalURL;

		} else {
			paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
		}

		return "redirect:/supplier/supplierSubscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
	}

	private PaymentTransaction markTransactionFailed(PaymentTransaction paymentTransaction, HashMap<String, String> nvp) {
		// Display a user friendly Error on the page using any of the following error information returned by PayPal
		String errorCode = nvp.get("L_ERRORCODE0").toString();
		String errorShortMsg = nvp.get("L_SHORTMESSAGE0").toString();
		String errorLongMsg = nvp.get("L_LONGMESSAGE0").toString();
		String errorSeverityCode = nvp.get("L_SEVERITYCODE0").toString();

		LOG.error("Error during payment. Error code : " + errorCode + ", Short Message : " + errorShortMsg + ", Severity : " + errorSeverityCode + ", Error Message : " + errorLongMsg);

		// Save the TOKEN in DB
		paymentTransaction.setStatus(TransactionStatus.FAILURE);
		paymentTransaction.setErrorCode(errorCode);
		paymentTransaction.setErrorMessage(errorLongMsg);
		paymentTransaction = updatePaymentTransaction(paymentTransaction);
		return paymentTransaction;
	}

	/**
	 * @param txId
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	public PaymentTransaction cancelPaymentTransaction(String txId) {
		PaymentTransaction paymentTransaction = getPaymentTransactionById(txId);
		paymentTransaction.setStatus(TransactionStatus.FAILURE);
		paymentTransaction.setErrorCode("-1001");
		paymentTransaction.setErrorMessage("User requested payment cancellation");
		paymentTransaction = updatePaymentTransaction(paymentTransaction);
		try {
			sendCancelSubscriptionMail(paymentTransaction);
		} catch (Exception e) {
			LOG.error("Error while sending mail on cancel subscription :" + e.getMessage(), e);
		}
		return paymentTransaction;
	}

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param paymentType
	 * @param paymentTransaction
	 * @throws ApplicationException
	 */
	/*
	 * @Override
	 * @Transactional(readOnly = false) public User confirmSubscription(String token, String payerId, Model model,
	 * String serverName, String paymentTransactionId) throws ApplicationException { User user = null; String
	 * paymentType = "Sale"; PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId);
	 * String nvpstr = "&TOKEN=" + token + "&PAYERID=" + payerId + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType +
	 * "&PAYMENTREQUEST_0_AMT=" + paymentTransaction.getAmount(); nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" +
	 * paymentTransaction.getPlan().getCurrency().getCurrencyCode() + "&IPADDRESS=" + serverName;
	 * LOG.info("Invoking PayPal Confirm Payment ===================> "); Make the call to PayPal to finalize payment If
	 * an error occured, show the resulting errors HashMap<String, String> nvp =
	 * invokePaypalService("DoExpressCheckoutPayment", nvpstr); model.addAttribute("paypalResponse", nvp); Subscription
	 * subscription = null; String strAck = nvp.get("ACK").toString(); try { subscription =
	 * getSubscriptionById(paymentTransaction.getSubscription().getId()); if (strAck != null &&
	 * strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) { // Update the Payment Transaction Details
	 * LOG.info("Subscription Id : " + paymentTransaction.getSubscription().getId());
	 * paymentTransaction.setConfirmationDate(new Date()); paymentTransaction.setStatus(TransactionStatus.SUCCESS);
	 * paymentTransaction.setReferenceTransactionId(nvp.get("PAYMENTINFO_0_TRANSACTIONID"));
	 * paymentTransaction.setPaymentFee(new BigDecimal(nvp.get("PAYMENTINFO_0_FEEAMT"))); paymentTransaction =
	 * updatePaymentTransaction(paymentTransaction); // Create the Buyer account and send out email notification for
	 * account activiation. Buyer buyer = new Buyer(); buyer.setLoginEmail(paymentTransaction.getLoginEmail());
	 * buyer.setCommunicationEmail(paymentTransaction.getCommunicationEmail());
	 * buyer.setCompanyContactNumber(paymentTransaction.getCompanyContactNumber());
	 * buyer.setCompanyName(paymentTransaction.getCompanyName());
	 * buyer.setCompanyRegistrationNumber(paymentTransaction.getCompanyRegistrationNumber()); //
	 * buyer.setCurrentSubscription(subscription); buyer.setFullName(paymentTransaction.getFullName()); //
	 * buyer.setPlan(paymentTransaction.getPlan()); buyer.setRegistrationOfCountry(paymentTransaction.getCountry());
	 * buyer.setStatus(BuyerStatus.PENDING); buyer.setSubscriptionDate(new Date()); buyer.setCreatedDate(new Date());
	 * try { user = buyerService.saveBuyer(buyer); user.getBuyer().getCommunicationEmail(); } catch (Exception e) {
	 * LOG.error("Error creating Buyer instance : " + e.getMessage(), e); } // Update the subscription details if
	 * (subscription != null) { subscription.setBuyer(user.getBuyer()); // Calculate Start/End, Trial Start Trial End
	 * etc... doComputeSubscription(subscription, null); BuyerPackage bp =new BuyerPackage();// new
	 * BuyerPackage(subscription); subscription = updateSubscription(subscription); buyer.setBuyerPackage(bp); // buyer
	 * = buyerService.findBuyerById(user.getTenantId()); LOG.info("Buyer Id : " + buyer.getId()); buyer =
	 * buyerService.updateBuyer(buyer); } // Update transaction details with buyer info
	 * paymentTransaction.setBuyer(buyer); paymentTransaction = updatePaymentTransaction(paymentTransaction); // // Send
	 * email to buyer of payment receipt // try { // String msg = "Your payment transaction has successfully done"; //
	 * String subject = "Subscribed Successfully"; // sendBuyerSubscriptionMail(buyer.getCommunicationEmail(),
	 * buyer.getFullName(), subscription, msg, // subject, true); // } catch (Exception e) { //
	 * LOG.error("Error While sending success subscription mail to buyer :" + e.getMessage(), e); // } } else {
	 * paymentTransaction = markTransactionFailed(paymentTransaction, nvp); // sending mail to buyer on payment failure
	 * try { String msg = "Your payment transaction has failed during subscription"; String subject =
	 * "Subscription Failure"; sendBuyerSubscriptionMail(paymentTransaction.getCommunicationEmail(),
	 * paymentTransaction.getFullName(), subscription, msg, subject, false); } catch (Exception e) {
	 * LOG.error("Error While sending failure subscription mail to buyer :" + e.getMessage(), e); } throw new
	 * ApplicationException("Payment transaction has failed"); } } catch (ApplicationException e) { throw e; } catch
	 * (Exception e) { LOG.error("Error during payment confirmation : " + e.getMessage(), e);
	 * paymentTransaction.setStatus(TransactionStatus.NEEDS_ATTENTION);
	 * paymentTransaction.setErrorMessage("Error during payment confirmation : " + e.getMessage()); paymentTransaction =
	 * updatePaymentTransaction(paymentTransaction); } model.addAttribute("subscription", subscription);
	 * model.addAttribute("paymentTransaction", paymentTransaction); return user; }
	 */

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param paymentType
	 * @param paymentTransaction
	 * @throws ApplicationException
	 */
	@Override
	@Transactional(readOnly = false)
	public void confirmSupplierSubscription(String token, String payerId, Model model, String serverName, String paymentTransactionId, boolean existingSupplier) throws ApplicationException {

		String paymentType = "Sale";

		LOG.info("Confirming Payment Transaction Id : " + paymentTransactionId);
		PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId);

		String nvpstr = "&TOKEN=" + token + "&PAYERID=" + payerId + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_AMT=" + paymentTransaction.getAmount();

		if (paymentTransaction.getSupplierPlan() != null) {
			nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" + paymentTransaction.getSupplierPlan().getCurrency().getCurrencyCode() + "&IPADDRESS=" + serverName;
		}

		LOG.info("Invoking PayPal Confirm Payment ===================> ");

		/* Make the call to PayPal to finalize payment If an error occured, show the resulting errors */

		HashMap<String, String> nvp = invokePaypalService("DoExpressCheckoutPayment", nvpstr);
		model.addAttribute("paypalResponse", nvp);

		SupplierSubscription subscription = null;
		String strAck = nvp.get("ACK").toString();
		try {
			subscription = getSupplierSubscriptionById(paymentTransaction.getSupplierSubscription().getId());
			if (strAck != null && strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) {

				// Update the Payment Transaction Details
				LOG.info("Subscription Id : " + paymentTransaction.getSupplierSubscription().getId());
				paymentTransaction.setConfirmationDate(new Date());
				paymentTransaction.setStatus(TransactionStatus.SUCCESS);
				paymentTransaction.setReferenceTransactionId(nvp.get("PAYMENTINFO_0_TRANSACTIONID"));
				paymentTransaction.setPaymentFee(new BigDecimal(nvp.get("PAYMENTINFO_0_FEEAMT")));
				paymentTransaction = updatePaymentTransaction(paymentTransaction);

				Supplier supplier = new Supplier();
				if (existingSupplier) {
					supplier = subscription.getSupplier();
					cancelOldSupplierSubscription(supplier.getId());
					supplier.setSupplierSubscription(subscription);
					if (paymentTransaction.getBuyer() != null) {
						if (supplier.getAssociatedBuyers() == null) {
							supplier.setAssociatedBuyers(new ArrayList<Buyer>());
						}
						LOG.info("Associating buyer : " + paymentTransaction.getBuyer().getId() + " with supplier : " + supplier.getCompanyName());
						supplier.getAssociatedBuyers().add(paymentTransaction.getBuyer());
					}

					if (subscription != null) {
						// Calculate Start/End, Trial Start Trial End etc...
						subscription.setSubscriptionStatus(SubscriptionStatus.FUTURE);

						SupplierPackage sp = supplier.getSupplierPackage();

						Calendar previousEndDate = Calendar.getInstance();
						if (sp == null || (sp != null && sp.getEndDate() == null)) {
							previousEndDate.setTime(new Date());
							subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
						} else {
							// if already expired, then the start date after renewal will be current date
							if (sp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED) {
								subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
								previousEndDate.setTime(new Date());
							} else {
								previousEndDate.setTime(sp.getEndDate());
								previousEndDate.add(Calendar.DATE, 1); // 1 day to add
							}
						}
						subscription.setStartDate(previousEndDate.getTime());
						subscription.setActivatedDate(previousEndDate.getTime());

						Calendar endDate = Calendar.getInstance();
						endDate.setTime(subscription.getStartDate());
						if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.MONTH) {
							endDate.add(Calendar.MONTH, paymentTransaction.getSupplierPlan().getPeriod());
						} else if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.YEAR) {
							endDate.add(Calendar.YEAR, paymentTransaction.getSupplierPlan().getPeriod());
						}
						subscription.setEndDate(endDate.getTime());

						if (sp == null) {
							sp = new SupplierPackage(subscription);
							supplier.setSupplierPackage(sp);
						} else {
							if (sp.getStartDate() == null) {
								sp.setStartDate(subscription.getStartDate());
							}
							sp.setEndDate(endDate.getTime());
						}
						subscription = updateSupplierSubscription(subscription);

						LOG.info("Supplier Id : " + supplier.getId());
						supplierService.updateSupplier(supplier);

					}
					// sending payment receipt email to supplier on successful
					String timeZone = "GMT+8:00";
					try {
						timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
						String msg = "Your payment transaction has successfully done";
						String subject = "Subscribed Successfully";
//					   User user = userService.getDetailsOfLoggedinUser(supplier.getLoginEmail());
//					   if(user.getEmailNotifications()) {
						   sendSupplierSubscriptionMail(supplier.getCommunicationEmail(), supplier.getFullName(), subscription, timeZone, msg, subject, true);
					  // }
					} catch (Exception e) {
						LOG.error("Error While sending success subscription mail to supplier :" + e.getMessage(), e);
					}

					// sending email to buyer on supplier successful subscribed
					if (paymentTransaction.getBuyer() != null) {
						try {
							timeZone = getTimeZoneByBuyerSettings(paymentTransaction.getBuyer().getId(), timeZone);
//							User user = userService.getDetailsOfLoggedinUser(paymentTransaction.getBuyer().getLoginEmail());
//							if(user.getEmailNotifications()) {
								sendSupplierSubscriptionSuccessMailForBuyer(paymentTransaction.getBuyer().getCommunicationEmail(), paymentTransaction.getBuyer().getFullName(), timeZone, supplier.getCompanyName());
							//}
							} catch (Exception e) {
							LOG.error("Error While sending success supplier subscription mail to buyer :" + e.getMessage(), e);
						}
					}

				} else {
					// Create the Supplier account and send out email notification for account activiation.
					supplier.setLoginEmail(paymentTransaction.getLoginEmail());
					supplier.setCommunicationEmail(paymentTransaction.getCommunicationEmail());
					supplier.setCompanyContactNumber(paymentTransaction.getCompanyContactNumber());
					supplier.setCompanyName(paymentTransaction.getCompanyName());
					supplier.setCompanyRegistrationNumber(paymentTransaction.getCompanyRegistrationNumber());
					supplier.setSupplierSubscription(subscription);
					supplier.setFullName(paymentTransaction.getFullName());
					supplier.setPassword(paymentTransaction.getPassword());
					supplier.setRemarks(paymentTransaction.getRemarks());
					supplier.setMobileNumber(paymentTransaction.getMobileNumber());
					// supplier.setPlan(paymentTransaction.getSupplierPlan());
					supplier.setRegistrationOfCountry(paymentTransaction.getCountry());
					supplier.setStatus(SupplierStatus.PENDING);
					supplier.setSubscriptionDate(new Date());
					supplier.setCreatedDate(new Date());
					supplier.setDesignation(paymentTransaction.getDesignation());
					try {
						supplier = supplierService.saveSupplier(supplier, true);
					} catch (Exception e) {
						LOG.error("Error creating Supplier instance : " + e.getMessage(), e);
					}

					// Update the subscription details
					if (subscription != null) {
						subscription.setSupplier(supplier);
						// Calculate Start/End, Trial Start Trial End etc...
						// doComputeSubscription(subscription, null);

						// BuyerPackage bp = new BuyerPackage(subscription);

						subscription = updateSupplierSubscription(subscription);
						// buyer.setBuyerPackage(bp);
						// buyer = buyerService.findBuyerById(user.getTenantId());
						LOG.info("Supplier Id : " + supplier.getId());
						// buyer = buyerService.updateBuyer(buyer);
					}
					// Update transaction details with buyer info
					paymentTransaction.setSupplier(supplier);
					paymentTransaction = updatePaymentTransaction(paymentTransaction);

				}
			} else {
				paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
				// sending payment receipt email to supplier on failure
				String timeZone = "GMT+8:00";
				try {
					timeZone = getTimeZoneBySupplierSettings(subscription.getSupplier().getId(), timeZone);
					String msg = "Your payment transaction has failed during subscription";
					String subject = "Subscription Failure";
//					User user = userService.getDetailsOfLoggedinUser(subscription.getSupplier().getLoginEmail());
//					if(user.getEmailNotifications())
					sendSupplierSubscriptionMail(subscription.getSupplier().getCommunicationEmail(), subscription.getSupplier().getFullName(), subscription, timeZone, msg, subject, false);
				} catch (Exception e) {
					LOG.error("Error While sending failure subscription mail to supplier :" + e.getMessage(), e);
				}
				throw new ApplicationException("Payment transaction has failed");
			}
		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);

			paymentTransaction.setStatus(TransactionStatus.NEEDS_ATTENTION);
			paymentTransaction.setErrorMessage("Error during payment confirmation : " + e.getMessage());
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
		}

		model.addAttribute("subscription", subscription);
		model.addAttribute("paymentTransaction", paymentTransaction);
	}

	/*
	 * @Override
	 * @Transactional(readOnly = false) public HashMap<String, String> confirmSubscriptionRenew(String token, String
	 * payerId, Model model, String serverName, String paymentTransactionId) { User user = null; String paymentType =
	 * "Sale"; PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId); String nvpstr =
	 * "&TOKEN=" + token + "&PAYERID=" + payerId + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType +
	 * "&PAYMENTREQUEST_0_AMT=" + paymentTransaction.getAmount(); nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" +
	 * paymentTransaction.getPlan().getCurrency().getCurrencyCode() + "&IPADDRESS=" + serverName;
	 * LOG.info("Invoking PayPal Confirm Payment ===================> "); Make the call to PayPal to finalize payment If
	 * an error occured, show the resulting errors HashMap<String, String> nvp =
	 * invokePaypalService("DoExpressCheckoutPayment", nvpstr); Subscription subscription = null; try { // Update the
	 * Payment Transaction Details LOG.info("Subscription Id : " + paymentTransaction.getSubscription().getId());
	 * paymentTransaction.setConfirmationDate(new Date()); paymentTransaction.setStatus(TransactionStatus.SUCCESS);
	 * paymentTransaction.setReferenceTransactionId(nvp.get("PAYMENTINFO_0_TRANSACTIONID"));
	 * paymentTransaction.setPaymentFee(new BigDecimal(nvp.get("PAYMENTINFO_0_FEEAMT"))); paymentTransaction =
	 * updatePaymentTransaction(paymentTransaction); subscription =
	 * getSubscriptionById(paymentTransaction.getSubscription().getId()); // Chain the Subscriptions to know which one
	 * is next. Buyer buyer = buyerService.findBuyerById(subscription.getBuyer().getId()); Subscription currentChainEnd
	 * = null ;buyer.getCurrentSubscription(); if (currentChainEnd != null) { while
	 * (currentChainEnd.getNextSubscription() != null) { currentChainEnd = currentChainEnd.getNextSubscription(); }
	 * currentChainEnd.setNextSubscription(subscription); updateSubscription(currentChainEnd); } // Buyer buyer =
	 * subscription.getBuyer(); // Update the subscription details if (subscription != null) { // Calculate Start/End,
	 * Trial Start Trial End etc... // doComputeSubscription(subscription, buyer.getCurrentSubscription()); subscription
	 * = updateSubscription(subscription); BuyerPackage bp = buyer.getBuyerPackage();
	 * bp.setEndDate(subscription.getEndDate()); buyer.setBuyerPackage(bp); // buyer =
	 * buyerService.findBuyerById(user.getTenantId()); LOG.info("Buyer Id : " + buyer.getId()); buyer =
	 * buyerService.updateBuyer(buyer); } } catch (Exception e) { LOG.error("Error during payment confirmation : " +
	 * e.getMessage(), e); paymentTransaction.setStatus(TransactionStatus.NEEDS_ATTENTION);
	 * paymentTransaction.setErrorMessage("Error during payment confirmation : " + e.getMessage()); paymentTransaction =
	 * updatePaymentTransaction(paymentTransaction); } model.addAttribute("subscription", subscription);
	 * model.addAttribute("paymentTransaction", paymentTransaction); return nvp; }
	 */

	/*
	 * @Override
	 * @Transactional(readOnly = false) public HashMap<String, String> confirmSubscriptionChange(String token, String
	 * payerId, Model model, String serverName, String paymentTransactionId) { User user = null; String paymentType =
	 * "Sale"; PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId); String nvpstr =
	 * "&TOKEN=" + token + "&PAYERID=" + payerId + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType +
	 * "&PAYMENTREQUEST_0_AMT=" + paymentTransaction.getAmount(); nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" +
	 * paymentTransaction.getPlan().getCurrency().getCurrencyCode() + "&IPADDRESS=" + serverName;
	 * LOG.info("Invoking PayPal Confirm Payment ===================> "); Make the call to PayPal to finalize payment If
	 * an error occured, show the resulting errors HashMap<String, String> nvp =
	 * invokePaypalService("DoExpressCheckoutPayment", nvpstr); Subscription subscription = null; try { // Update the
	 * Payment Transaction Details LOG.info("Subscription Id : " + paymentTransaction.getSubscription().getId());
	 * paymentTransaction.setConfirmationDate(new Date()); paymentTransaction.setStatus(TransactionStatus.SUCCESS);
	 * paymentTransaction.setReferenceTransactionId(nvp.get("PAYMENTINFO_0_TRANSACTIONID"));
	 * paymentTransaction.setPaymentFee(new BigDecimal(nvp.get("PAYMENTINFO_0_FEEAMT"))); paymentTransaction =
	 * updatePaymentTransaction(paymentTransaction); subscription =
	 * getSubscriptionById(paymentTransaction.getSubscription().getId()); // Chain the Subscriptions to know which one
	 * is next. Buyer buyer = buyerService.findBuyerById(subscription.getBuyer().getId()); Subscription currentChainEnd
	 * = null ; //buyer.getCurrentSubscription(); if (currentChainEnd != null) { while
	 * (currentChainEnd.getNextSubscription() != null) { currentChainEnd = currentChainEnd.getNextSubscription(); }
	 * currentChainEnd.setNextSubscription(subscription); updateSubscription(currentChainEnd); } // Buyer buyer =
	 * subscription.getBuyer(); // Update the subscription details if (subscription != null) { // Calculate Start/End,
	 * Trial Start Trial End etc... // doComputeSubscription(subscription, buyer.getCurrentSubscription()); // Set this
	 * one to null as it was used during change of plan (Immediate or after current subscription // end)
	 * subscription.setConvertedToPaid(null); subscription = updateSubscription(subscription); BuyerPackage bp =
	 * buyer.getBuyerPackage(); bp.setEndDate(subscription.getEndDate()); buyer.setBuyerPackage(bp); // buyer =
	 * buyerService.findBuyerById(user.getTenantId()); LOG.info("Buyer Id : " + buyer.getId()); buyer =
	 * buyerService.updateBuyer(buyer); } } catch (Exception e) { LOG.error("Error during payment confirmation : " +
	 * e.getMessage(), e); paymentTransaction.setStatus(TransactionStatus.NEEDS_ATTENTION);
	 * paymentTransaction.setErrorMessage("Error during payment confirmation : " + e.getMessage()); paymentTransaction =
	 * updatePaymentTransaction(paymentTransaction); } model.addAttribute("subscription", subscription);
	 * model.addAttribute("paymentTransaction", paymentTransaction); return nvp; }
	 */

	/*********************************************************************************
	 * httpcall: Function to perform the API call to PayPal using API signature
	 * 
	 * @methodName is name of API method.
	 * @nvpStr is nvp string. returns a NVP string containing the response from the server.
	 *********************************************************************************/
	@Override
	public HashMap<String, String> invokePaypalService(String methodName, String nvpStr) {

		// String gv_BNCode = "PP-ECWizard";
		String gv_Version = "93";
		// String gv_APIUserName = "nitin_api1.recstech.com";
		// String gv_APIPassword = "89F5J7Q4D6EB9ABV";
		// String gv_APISignature = "AFcWxV21C7fd0v3bYYYRCpSSRl31A-VQG5gdOnSLEGQmpV9JToK74qvZ";

		// String HTTPREQUEST_PROXYSETTING_SERVER = "";
		// String HTTPREQUEST_PROXYSETTING_PORT = "";
		// boolean USE_PROXY = false;

		String gv_APIEndpoint = PAYPAL_API_ENDPOINT_URL;

		/*
		 * boolean bSandbox = true; if (bSandbox == true) { gv_APIEndpoint = "https://api-3t.sandbox.paypal.com/nvp"; }
		 * else { gv_APIEndpoint = "https://api-3t.paypal.com/nvp"; }
		 */

		LOG.info(" gv_APIEndpoint : " + gv_APIEndpoint);

		// WinObjHttp Request proxy settings.
		// String gv_ProxyServer = HTTPREQUEST_PROXYSETTING_SERVER;
		// String gv_ProxyServerPort = HTTPREQUEST_PROXYSETTING_PORT;
		// int gv_Proxy = 2; // 'setting for proxy activation
		// boolean gv_UseProxy = USE_PROXY;

		// String version = "2.3";
		String agent = "Mozilla/4.0";
		String respText = "";
		HashMap<String, String> nvp = null;

		// deformatNVP( nvpStr );
		String encodedData = "METHOD=" + methodName + "&VERSION=" + gv_Version + "&PWD=" + PAYPAL_API_PASSWORD + "&USER=" + PAYPAL_API_USERNAME + "&SIGNATURE=" + PAYPAL_API_SIGNATURE + nvpStr;
		// + "&BUTTONSOURCE=" + gv_BNCode;

		try {
			URL postURL = new URL(gv_APIEndpoint);
			HttpURLConnection conn = (HttpURLConnection) postURL.openConnection();

			// Set connection parameters. We need to perform input and output so set both as true.
			conn.setDoInput(true);
			conn.setDoOutput(true);

			// Set the content type we are POSTing. We impersonate it as encoded form data
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent", agent);

			// conn.setRequestProperty( "Content-Type", type );
			conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
			conn.setRequestMethod("POST");

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
				nvp = deformatNVP(respText);
			}

			LOG.info("Response from PayPal ===================> ");
			for (Map.Entry<String, String> entry : nvp.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				LOG.info(key + " = " + value);
			}

			return nvp;
		} catch (IOException e) {
			// handle the error here
			LOG.info("Error during Paypal Service Call : " + e.getMessage(), e);
			return null;
		}
	}

	/*********************************************************************************
	 * deformatNVP: Function to break the NVP string into a HashMap pPayLoad is the NVP string. returns a HashMap object
	 * containing all the name value pairs of the string.
	 *********************************************************************************/
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

	@Override
	public boolean isPlanInUse(String planId) {
		return subscriptionDao.isPlanInUse(planId);
	}

	@Override
	public boolean isSupplierPlanInUse(String planId) {
		return supplierSubscriptionDao.isPlanInUse(planId);
	}

	@Override
	public SupplierSubscription getCurrentSubscriptionForSupplier(String loggedInUserTenantId) {
		return supplierSubscriptionDao.getCurrentSubscriptionForSupplier(loggedInUserTenantId);
	}

	@Override
	public Supplier getSupplierWithSupplierPackagePlanByTenantId(String loggedInUserTenantId) {
		return supplierSubscriptionDao.getSupplierWithSupplierPackagePlanByTenantId(loggedInUserTenantId);
	}

	@Override
	public Supplier getSupplierWithSupplierPackagePlanByTenantIdforBilling(String loggedInUserTenantId) {
		Supplier s = supplierSubscriptionDao.getSupplierWithSupplierPackagePlanByTenantId(loggedInUserTenantId);
		if (s != null) {
			if (s.getSupplierSubscription() != null) {
				if (s.getSupplierSubscription().getSupplierPlan() != null) {

					s.getSupplierSubscription().getSupplierPlan().getBuyerLimit();
					s.getSupplierSubscription().getSupplierPlan().getId();
					s.getSupplierSubscription().getSupplierPlan().getPlanName();
					s.getSupplierSubscription().getSupplierPlan().getPeriod();
					s.getSupplierSubscription().getSupplierPlan().getPrice();
					s.getSupplierSubscription().getSupplierPlan().getTax();

					if (s.getSupplierSubscription().getSupplierPlan().getCurrency() != null) {
						s.getSupplierSubscription().getSupplierPlan().getCurrency().getCurrencyCode();
					}

				}
				if (s.getSupplierSubscription().getPaymentTransaction() != null) {
					s.getSupplierSubscription().getPaymentTransaction().getPaymentFee();
					s.getSupplierSubscription().getPaymentTransaction().getPaymentToken();
				}

				if (s.getRegistrationOfCountry() != null) {
					s.getRegistrationOfCountry().getCountryCode();
				}
			}
			if (s.getSupplierPackage() != null) {
				s.getSupplierPackage().getSupplierPlan();
			}

		}
		return s;
	}

	private void sendSupplierSubscriptionMail(String mailTo, String name, SupplierSubscription subscription, String timeZone, String msg, String subject, boolean isSuccess) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("msg", msg);
			map.put("userName", name);
			map.put("loginUrl", APP_URL + "/login");

			SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
			date.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("plan", subscription.getSupplierPlan() != null ? subscription.getSupplierPlan().getPlanName() : "Not Available");
			if (isSuccess) {
				map.put("startDate", subscription.getStartDate() != null ? date.format(subscription.getStartDate()) : "");
				map.put("endDate", subscription.getEndDate() != null ? date.format(subscription.getEndDate()) : "Not Available");
			}
			map.put("currencyCode", subscription.getPaymentTransaction() != null ? subscription.getPaymentTransaction().getCurrencyCode() : "Not Available");
			map.put("amount", subscription.getPaymentTransaction() != null ? subscription.getPaymentTransaction().getAmount() : "");
			if (isSuccess) {
				notificationService.sendEmail(mailTo, subject, map, Global.SUPPLIER_SUCCESS_SUBSCRIPTION);
			} else {
				map.put("errorCode", subscription.getPaymentTransaction() != null ? subscription.getPaymentTransaction().getErrorCode() : "");
				map.put("errorMsg", subscription.getPaymentTransaction() != null ? subscription.getPaymentTransaction().getErrorMessage() : "");
				notificationService.sendEmail(mailTo, subject, map, Global.SUPPLIER_FAILURE_SUBSCRIPTION);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending Subscription mail :" + e.getMessage(), e);
		}
	}

	private void sendBuyerSubscriptionMail(String mailTo, String name, Subscription subscription, String msg, String subject, boolean isSuccess) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			map.put("date", df.format(new Date()) + " (UTC)");
			map.put("msg", msg);
			map.put("userName", name);
			map.put("loginUrl", APP_URL + "/login");

			SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
			map.put("plan", subscription.getPlan() != null ? subscription.getPlan().getPlanName() : "Not Available");
			if (isSuccess) {
				map.put("startDate", subscription.getStartDate() != null ? date.format(subscription.getStartDate()) : "");
				map.put("endDate", subscription.getEndDate() != null ? date.format(subscription.getEndDate()) + " (UTC)" : "Not Available");
			}
			map.put("currencyCode", subscription.getPaymentTransaction() != null ? subscription.getPaymentTransaction().getCurrencyCode() : "Not Available");
			map.put("amount", subscription.getPaymentTransaction() != null ? subscription.getPaymentTransaction().getAmount() : "");
			if (isSuccess) {
				notificationService.sendEmail(mailTo, subject, map, Global.BUYER_SUBSCRIPTION);
			} else {
				map.put("errorCode", subscription.getPaymentTransaction() != null ? subscription.getPaymentTransaction().getErrorCode() : "");
				map.put("errorMsg", subscription.getPaymentTransaction() != null ? subscription.getPaymentTransaction().getErrorMessage() : "");
				notificationService.sendEmail(mailTo, subject, map, Global.BUYER_FAILURE_SUBSCRIPTION);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending Subscription mail :" + e.getMessage(), e);
		}
	}

	private void sendSupplierSubscriptionSuccessMailForBuyer(String mailTo, String name, String timeZone, String supplierName) {
		try {
			String subject = "Supplier Subscription";
			HashMap<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", name);
			map.put("loginUrl", APP_URL + "/login");
			map.put("supplierName", supplierName);
			notificationService.sendEmail(mailTo, subject, map, Global.SUPPLIER_SUBSCRIPTION_FOR_BUYER);
		} catch (Exception e) {
			LOG.error("Error While Sending  supplier Subscription mail to buyer:" + e.getMessage(), e);
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

	/**
	 * @param suppId
	 * @param timeZone
	 * @return
	 */
	private String getTimeZoneBySupplierSettings(String suppId, String timeZone) {
		try {
			if (StringUtils.checkString(suppId).length() > 0) {
				String time = supplierSettingsService.getSupplierTimeZoneByTenantId(suppId);
				if (time != null) {
					timeZone = time;
					LOG.info("time Zone :" + timeZone);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching supplier setting time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	private void sendCancelSubscriptionMail(PaymentTransaction paymentTransaction) {
		try {
			String name = StringUtils.checkString(paymentTransaction.getCompanyName());
			String mailTo = paymentTransaction.getCommunicationEmail();
			String plan = "";
			String currencyCode = paymentTransaction.getCurrencyCode();
			String amount = paymentTransaction.getAmount() + "";
			if (paymentTransaction.getBuyerPlan() != null) {
				plan = paymentTransaction.getBuyerPlan().getPlanName();
			} else if (paymentTransaction.getSupplierPlan() != null) {
				plan = paymentTransaction.getSupplierPlan().getPlanName();
			}

			String subject = "Payment cancellation";
			HashMap<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			map.put("date", df.format(new Date()));
			map.put("userName", name);
			map.put("loginUrl", APP_URL + "/login");

			map.put("plan", plan);
			map.put("currencyCode", currencyCode);
			map.put("amount", amount);
//			User user = userService.getDetailsOfLoggedinUser(paymentTransaction.getLoginEmail());
//			if(user.getEmailNotifications())
			notificationService.sendEmail(mailTo, subject, map, Global.CANCEL_SUBSCRIPTION);
		} catch (Exception e) {
			LOG.error("Error While Sending cancel Subscription mail :" + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public String initiatePaypalPaymentForRenewSupplier(SupplierSubscription subscription, String planId, String supplierId, HttpSession session, String returnURL, String cancelURL, String promoCode) {
		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);
		BigDecimal tax = BigDecimal.ZERO;
		Supplier supplier = subscription.getSupplier();
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

		int paymentAmount = plan.getPrice();
		if (!StringUtils.checkString(promoCode).isEmpty()) {
			BigDecimal promoDiscountPrice = BigDecimal.ZERO;
			try {
				promoDiscountPrice = getPromoDiscount(promoCode, BigDecimal.valueOf(paymentAmount));
				paymentAmount = (BigDecimal.valueOf(paymentAmount).subtract(promoDiscountPrice).setScale(2, BigDecimal.ROUND_HALF_UP)).intValue();
			} catch (Exception e) {
				LOG.info("error while adding promocode value");
			}
		}
		// The paymentAmount is the total value of the shopping cart

		// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
		String currencyCodeType = plan.getCurrency().getCurrencyCode();
		String paymentType = "Sale";

		// boolean bSandbox = true;
		// if (bSandbox == true) {
		// PAYPAL_URL = "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=";
		// } else {
		// PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
		// }

		// session.setAttribute("currencyCodeType", currencyCodeType);
		// session.setAttribute("paymentAmount", paymentAmount);
		// session.setAttribute("paymentType", paymentType);
		// session.setAttribute("quantity", subscription.getPlanQuantity());

		// Create an instance of PaymentTransaction at the beginning of payment flow.
		PaymentTransaction paymentTransaction = subscription.getPaymentTransaction();
		paymentTransaction.setSupplierPlan(plan);
		paymentTransaction.setCreatedDate(new Date());
		paymentTransaction.setCurrencyCode(currencyCodeType);
		paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
		paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
		paymentTransaction.setType(TransactionType.PAYMENT);
		paymentTransaction.setAmount(new BigDecimal(paymentAmount));
		paymentTransaction.setCompanyRegistrationNumber(subscription.getSupplier().getCompanyRegistrationNumber());
		paymentTransaction.setCompanyName(subscription.getSupplier().getCompanyName());
		paymentTransaction.setSupplier(subscription.getSupplier());
		paymentTransaction.setCommunicationEmail(subscription.getSupplier().getCommunicationEmail());
		paymentTransaction = savePaymentTransaction(paymentTransaction);

		// The returnURL is the location where buyers return to when a payment has been succesfully authorized.
		returnURL += paymentTransaction.getId();

		// The cancelURL is the location buyers are sent to when they hit the cancel button during authorization of
		// payment during the PayPal flow
		cancelURL += paymentTransaction.getId();

		BigDecimal amount = new BigDecimal(paymentAmount);
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal totalTax = (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

		// LOG.info("amount: " + amount );
		// LOG.info("tax: " + tax );
		// LOG.info("amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP) : " + amount.multiply(tax).setScale(2,
		// BigDecimal.ROUND_HALF_UP) );
		// LOG.info("(amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)): " +
		// (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)) );

		// Construct the parameter string that describes the PayPal payment.
		String nvpstr = "&PAYMENTREQUEST_0_AMT=" + amount.add(totalTax) + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType + "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan.";

		// No Shipping required
		nvpstr += "&NOSHIPPING=1";

		// Local code
		// nvpstr += "&LOCALECODE=MY";

		// Business Name Image
		// nvpstr += "&HDRIMG=";

		// Background Color of checkout page
		nvpstr += "&PAYFLOWCOLOR=0095D5";

		// SOLUTIONTYPE
		nvpstr += "&SOLUTIONTYPE=Sole"; // or Mark

		// Cart Border Color
		nvpstr += "&CARTBORDERCOLOR=0095D5";

		// Logo Image
		// nvpstr += "&LOGOIMG=" + URLEncoder.encode(request.getScheme() + "://" + request.getServerName() + ":" +
		// request.getServerPort() + request.getContextPath() + "/resources/images/pro_logo.png");

		// Buyer Email
		nvpstr += "&EMAIL=" + subscription.getPaymentTransaction().getCommunicationEmail();

		// Landing Page - Type of PayPal page to display
		nvpstr += "&LANDINGPAGE=Billing"; // or LANDINGPAGE=Login

		String gstDesc = " (Inclusive of " + tax + "%25 Tax)";

		String note = "You are renewing subscription subscribing for " + plan.getPeriod() + " " + plan.getPeriodUnit() + "S" + gstDesc;

		// Note to Buyer
		nvpstr += "&NOTETOBUYER=" + note;

		/**
		 * Item Details
		 */
		// Item Amount
		nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + amount;
		// Total Tax
		nvpstr += "&PAYMENTREQUEST_0_TAXAMT=" + totalTax;
		// Total Shipping
		nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0";
		// Item Name
		nvpstr += "&L_PAYMENTREQUEST_0_NAME0=" + plan.getPlanName();
		// Item Description
		nvpstr += "&L_PAYMENTREQUEST_0_DESC0=" + plan.getShortDescription();
		// Item Amount
		nvpstr += "&L_PAYMENTREQUEST_0_AMT0=" + amount;
		// Item Qty
		nvpstr += "&L_PAYMENTREQUEST_0_QTY0=1";
		// Item Tax
		nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT0=0";
		// Item Url
		// nvpstr += "&L_PAYMENTREQUEST_0_ITEMURL0=" + URLEncoder.encode(request.getScheme() + "://" +
		// request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +
		// "/subscription/selectPlan/" + planId);

		nvpstr += "&ReturnUrl=" + URLEncoder.encode(returnURL) + "&CANCELURL=" + URLEncoder.encode(cancelURL);

		// Make the call to PayPal to get the Express Checkout token If the API call succeded, then redirect the buyer
		// to PayPal to begin to authorize payment. If an error occured, show the resulting errors
		LOG.info("Sending to payment gateway with data : " + nvpstr);

		// Calls the SetExpressCheckout API call
		LOG.info("Invoking Paypal Service Express Checkout ============ >");
		HashMap<String, String> nvp = invokePaypalService("SetExpressCheckout", nvpstr);
		if (nvp == null) {
			paymentTransaction.setStatus(TransactionStatus.FAILURE);
			paymentTransaction.setErrorCode("-1");
			paymentTransaction.setErrorMessage("Error invoking paypal service");
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			return "redirect:/supplier/supplierSubscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
		}
		LOG.info("Payment gateway response : " + nvp.get("ACK"));

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) {
			session.setAttribute(SubscriptionServiceImpl.TOKEN, nvp.get(SubscriptionServiceImpl.TOKEN).toString());
			LOG.info(" TOKEN : " + nvp.get(SubscriptionServiceImpl.TOKEN));

			// We dont need these for saving the subscription
			if (supplier.getId() == null) {
				subscription.setSupplier(null);
			}

			// subscription.setSupplierPlan(plan);
			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);
			subscription.setPaymentTransaction(paymentTransaction);
			if (subscription.getBuyerLimit() == null) {
				subscription.setBuyerLimit(plan.getBuyerLimit());
			}

			subscription = saveSupplierSubscription(subscription);

			// Save the TOKEN in DB
			paymentTransaction.setSupplierSubscription(subscription);
			paymentTransaction.setPaymentToken(nvp.get(SubscriptionServiceImpl.TOKEN));
			paymentTransaction.setAdditionalTaxDesc("Tax " + tax + "%");
			paymentTransaction.setAdditionalTax(totalTax);

			paymentTransaction.setPriceAmount(amount);
			paymentTransaction.setAmount(amount.add(totalTax));
			paymentTransaction.setTotalPriceAmount(amount.add(totalTax));
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			// Reassign the values
			subscription.setSupplier(supplier);

			session.setAttribute("subscriptionId", subscription.getId());
			session.setAttribute("paymentTransactionId", paymentTransaction.getId());
			session.setAttribute("supplier", supplier);

			String payPalURL = PAYPAL_URL + nvp.get(SubscriptionServiceImpl.TOKEN);
			return "redirect:" + payPalURL;

		} else {
			paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
		}

		return "redirect:/supplier/supplierSubscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
	}

	@Override
	@Transactional(readOnly = false)
	public void confirmRenewSupplierSubscription(String token, String payerId, Model model, String serverName, String paymentTransactionId, boolean existingSupplier) throws ApplicationException {

		String paymentType = "Sale";

		LOG.info("Confirming Payment Transaction Id : " + paymentTransactionId);
		PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId);

		String nvpstr = "&TOKEN=" + token + "&PAYERID=" + payerId + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_AMT=" + paymentTransaction.getAmount();

		if (paymentTransaction.getSupplierPlan() != null) {
			nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" + paymentTransaction.getSupplierPlan().getCurrency().getCurrencyCode() + "&IPADDRESS=" + serverName;
		}

		LOG.info("Invoking PayPal Confirm Payment ===================> ");
		/*
		 * Make the call to PayPal to finalize payment If an error occured, show the resulting errors
		 */
		HashMap<String, String> nvp = invokePaypalService("DoExpressCheckoutPayment", nvpstr);
		model.addAttribute("paypalResponse", nvp);

		SupplierSubscription subscription = null;
		String strAck = nvp.get("ACK").toString();
		try {
			subscription = getSupplierSubscriptionById(paymentTransaction.getSupplierSubscription().getId());
			if (strAck != null && strAck.toLowerCase().startsWith(SubscriptionServiceImpl.SUCCESS.toLowerCase())) {

				// Update the Payment Transaction Details
				LOG.info("Subscription Id : " + paymentTransaction.getSupplierSubscription().getId());
				paymentTransaction.setConfirmationDate(new Date());
				paymentTransaction.setStatus(TransactionStatus.SUCCESS);
				paymentTransaction.setReferenceTransactionId(nvp.get("PAYMENTINFO_0_TRANSACTIONID"));
				paymentTransaction.setPaymentFee(new BigDecimal(nvp.get("PAYMENTINFO_0_FEEAMT")));
				paymentTransaction = updatePaymentTransaction(paymentTransaction);

				Supplier supplier = new Supplier();
				if (existingSupplier) {
					supplier = subscription.getSupplier();
					supplier.setSupplierSubscription(subscription);
					if (paymentTransaction.getBuyer() != null) {
						if (supplier.getAssociatedBuyers() == null) {
							supplier.setAssociatedBuyers(new ArrayList<Buyer>());
						}
						LOG.info("Associating buyer : " + paymentTransaction.getBuyer().getId() + " with supplier : " + supplier.getCompanyName());
						supplier.getAssociatedBuyers().add(paymentTransaction.getBuyer());
					}

					if (subscription != null) {
						// Calculate Start/End, Trial Start Trial End etc...
						SupplierPackage sp = supplier.getSupplierPackage();
						Calendar previousEndDate = Calendar.getInstance();
						if (sp.getEndDate() == null) {
							previousEndDate.setTime(new Date());
						} else {
							// if already expired, then the start date after renewal will be current date
							if (sp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED) {
								previousEndDate.setTime(new Date());
							} else {
								previousEndDate.setTime(sp.getEndDate());
								previousEndDate.add(Calendar.DATE, 1); // 1 day to add
							}
						}
						subscription.setStartDate(previousEndDate.getTime());
						subscription.setActivatedDate(previousEndDate.getTime());
						subscription.setSubscriptionStatus(SubscriptionStatus.FUTURE);

						if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.MONTH) {
							previousEndDate.add(Calendar.MONTH, paymentTransaction.getSupplierPlan().getPeriod());
						} else if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.YEAR) {
							previousEndDate.add(Calendar.YEAR, paymentTransaction.getSupplierPlan().getPeriod());
						}
						subscription.setEndDate(previousEndDate.getTime());

						sp.setEndDate(previousEndDate.getTime());
						subscription = updateSupplierSubscription(subscription);

						supplier.setSupplierPackage(sp);
						LOG.info("Supplier Id : " + supplier.getId());
						supplierService.updateSupplier(supplier);
					}
					// sending payment receipt email to supplier on successful
					String timeZone = "GMT+8:00";
					try {
						timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
						String msg = "Your payment transaction has successfully done";
						String subject = "Subscription renewed Successfully";
//						User user = userService.getDetailsOfLoggedinUser(supplier.getLoginEmail());
//						if(user.getEmailNotifications())
						sendSupplierSubscriptionMail(supplier.getCommunicationEmail(), supplier.getFullName(), subscription, timeZone, msg, subject, true);
					} catch (Exception e) {
						LOG.error("Error While sending success subscription renew mail to supplier :" + e.getMessage(), e);
					}

					// // sending email to buyer on supplier successful subscribed
					// if (paymentTransaction.getBuyer() != null) {
					// try {
					// timeZone = getTimeZoneByBuyerSettings(paymentTransaction.getBuyer().getId(), timeZone);
					// sendSupplierSubscriptionSuccessMailForBuyer(paymentTransaction.getBuyer().getCommunicationEmail(),
					// paymentTransaction.getBuyer().getFullName(), timeZone, supplier.getCompanyName());
					// } catch (Exception e) {
					// LOG.error("Error While sending success supplier subscription mail to buyer :" + e.getMessage(),
					// e);
					// }
					// }

				} else {
					// Create the Supplier account and send out email notification for account activiation.
					supplier.setLoginEmail(paymentTransaction.getLoginEmail());
					supplier.setCommunicationEmail(paymentTransaction.getCommunicationEmail());
					supplier.setCompanyContactNumber(paymentTransaction.getCompanyContactNumber());
					supplier.setCompanyName(paymentTransaction.getCompanyName());
					supplier.setCompanyRegistrationNumber(paymentTransaction.getCompanyRegistrationNumber());
					supplier.setSupplierSubscription(subscription);
					supplier.setFullName(paymentTransaction.getFullName());
					supplier.setPassword(paymentTransaction.getPassword());
					supplier.setRemarks(paymentTransaction.getRemarks());
					supplier.setMobileNumber(paymentTransaction.getMobileNumber());
					// supplier.setPlan(paymentTransaction.getSupplierPlan());
					supplier.setRegistrationOfCountry(paymentTransaction.getCountry());
					supplier.setStatus(SupplierStatus.PENDING);
					supplier.setSubscriptionDate(new Date());
					supplier.setCreatedDate(new Date());
					try {
						supplier = supplierService.saveSupplier(supplier, true);
					} catch (Exception e) {
						LOG.error("Error creating Supplier instance : " + e.getMessage(), e);
					}

					// Update the subscription details
					if (subscription != null) {
						subscription.setSupplier(supplier);
						// Calculate Start/End, Trial Start Trial End etc...
						// doComputeSubscription(subscription, null);

						// BuyerPackage bp = new BuyerPackage(subscription);

						subscription = updateSupplierSubscription(subscription);
						// buyer.setBuyerPackage(bp);
						// buyer = buyerService.findBuyerById(user.getTenantId());
						LOG.info("Supplier Id : " + supplier.getId());
						// buyer = buyerService.updateBuyer(buyer);
					}
					// Update transaction details with buyer info
					paymentTransaction.setSupplier(supplier);
					paymentTransaction = updatePaymentTransaction(paymentTransaction);

				}
			} else {
				paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
				// sending payment receipt email to supplier on failure
				String timeZone = "GMT+8:00";
				try {
					timeZone = getTimeZoneBySupplierSettings(subscription.getSupplier().getId(), timeZone);
					String msg = "Your payment transaction has failed during renew of subscription";
					String subject = "Renew Subscription Failure";
					User user = userService.getDetailsOfLoggedinUser(subscription.getSupplier().getLoginEmail());
					if(user.getEmailNotifications())
					sendSupplierSubscriptionMail(subscription.getSupplier().getCommunicationEmail(), subscription.getSupplier().getFullName(), subscription, timeZone, msg, subject, false);
				} catch (Exception e) {
					LOG.error("Error While sending failure renew subscription mail to supplier :" + e.getMessage(), e);
				}
				throw new ApplicationException("Payment transaction has failed");
			}
		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);

			paymentTransaction.setStatus(TransactionStatus.NEEDS_ATTENTION);
			paymentTransaction.setErrorMessage("Error during payment confirmation : " + e.getMessage());
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
		}

		model.addAttribute("subscription", subscription);
		model.addAttribute("paymentTransaction", paymentTransaction);
	}

	@Transactional(readOnly = false)
	private void cancelOldSupplierSubscription(String id) {

		try {
			Supplier supplier = supplierService.findSuppById(id);
			if (supplier != null) {
				if (supplier.getSupplierSubscription() != null) {
					LOG.info("cancelled old subcription...............");
					SupplierSubscription s = supplier.getSupplierSubscription();
					s.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
					supplierSubscriptionDao.update(s);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while cancelling old subcription:" + e.getMessage(), e);
		}
	}

	@Override
	public boolean checkSupplierExpireSubscription(String loggedInUserTenantId) throws SubscriptionException {
		Supplier supplier = getSupplierWithSupplierPackagePlanByTenantId(loggedInUserTenantId);
		OwnerSettings ownerSettings = ownerSettingsService.getPlainOwnersettings();
		if (ownerSettings != null && (ownerSettings.getSupplierChargeStartDate() != null && new Date().after(ownerSettings.getSupplierChargeStartDate())) || ownerSettings.getSupplierChargeStartDate() == null) {
			if (supplier.getSupplierPackage() != null) {
				if (supplier.getSupplierPackage().getSubscriptionStatus() == SubscriptionStatus.EXPIRED || (supplier.getSupplierPackage().getEndDate() != null && supplier.getSupplierPackage().getEndDate().before(new Date()))) {
					throw new SubscriptionException("Your subscription has expired.");
				}
			} else {
				throw new SubscriptionException("Please subscribe to continue.");
			}
		}
		return true;
	}

	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param request
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	public String initiateSupplierOrderPaypalPayment(SupplierSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL) {
		BigDecimal paymentAmount = subscription.getTotalPriceAmount();
		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);

		BigDecimal tax = BigDecimal.ZERO;

		Supplier supplier = subscription.getSupplier();
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

		// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
		String currencyCodeType = "USD";
		String paymentType = "Order";

		// boolean bSandbox = true;
		// if (bSandbox == true) {
		// PAYPAL_URL = "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=";
		// } else {
		// PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
		// }

		// session.setAttribute("currencyCodeType", currencyCodeType);
		// session.setAttribute("paymentAmount", paymentAmount);
		// session.setAttribute("paymentType", paymentType);
		// session.setAttribute("quantity", subscription.getPlanQuantity());

		// Create an instance of PaymentTransaction at the beginning of payment flow.
		PaymentTransaction paymentTransaction = subscription.getPaymentTransaction();
		// paymentTransaction.setSupplierPlan(plan);
		paymentTransaction.setCreatedDate(new Date());
		paymentTransaction.setCurrencyCode(currencyCodeType);
		paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
		paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
		paymentTransaction.setType(TransactionType.PAYMENT);
		paymentTransaction.setAmount(paymentAmount);

		paymentTransaction.setCompanyRegistrationNumber(subscription.getSupplier().getCompanyRegistrationNumber());
		paymentTransaction.setCompanyName(subscription.getSupplier().getCompanyName());
		paymentTransaction.setCommunicationEmail(subscription.getSupplier().getCommunicationEmail());
		paymentTransaction.setPassword(subscription.getSupplier().getPassword());
		paymentTransaction.setLoginEmail(subscription.getSupplier().getLoginEmail());
		paymentTransaction.setCompanyContactNumber(subscription.getSupplier().getCompanyContactNumber());
		paymentTransaction.setFullName(subscription.getSupplier().getFullName());
		paymentTransaction.setRemarks(subscription.getSupplier().getRemarks());
		paymentTransaction.setMobileNumber(subscription.getSupplier().getMobileNumber());
		paymentTransaction.setCountry(subscription.getSupplier().getRegistrationOfCountry());

		paymentTransaction = savePaymentTransaction(paymentTransaction);

		// The returnURL is the location where buyers return to when a payment has been succesfully authorized.
		returnURL += paymentTransaction.getId();

		// The cancelURL is the location buyers are sent to when they hit the cancel button during authorization of
		// payment during the PayPal flow
		cancelURL += paymentTransaction.getId();
		BigDecimal amount = paymentAmount;
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal totalTax = (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

		// LOG.info("amount: " + amount );
		// LOG.info("tax: " + tax );
		// LOG.info("amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP) : " + amount.multiply(tax).setScale(2,
		// BigDecimal.ROUND_HALF_UP) );
		// LOG.info("(amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)): " +
		// (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)) );

		// Construct the parameter string that describes the PayPal payment.
		String nvpstr = "&PAYMENTREQUEST_0_AMT=" + amount.add(totalTax) + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType + "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan.";

		// No Shipping required
		nvpstr += "&NOSHIPPING=1";

		// Local code
		// nvpstr += "&LOCALECODE=MY";

		// Business Name Image
		// nvpstr += "&HDRIMG=";

		// Background Color of checkout page
		nvpstr += "&PAYFLOWCOLOR=0095D5";

		// SOLUTIONTYPE
		nvpstr += "&SOLUTIONTYPE=Sole"; // or Mark

		// Cart Border Color
		nvpstr += "&CARTBORDERCOLOR=0095D5";

		// Logo Image
		// nvpstr += "&LOGOIMG=" + URLEncoder.encode(request.getScheme() + "://" + request.getServerName() + ":" +
		// request.getServerPort() + request.getContextPath() + "/resources/images/pro_logo.png");

		// Buyer Email
		nvpstr += "&EMAIL=" + subscription.getPaymentTransaction().getCommunicationEmail();

		// Landing Page - Type of PayPal page to display
		nvpstr += "&LANDINGPAGE=Billing"; // or LANDINGPAGE=Login
		String note = "You are subscribing for trial period";

		// Note to Buyer
		nvpstr += "&NOTETOBUYER=" + note;

		/**
		 * Item Details
		 */
		// Item Amount
		nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + amount;
		// Total Tax
		nvpstr += "&PAYMENTREQUEST_0_TAXAMT=" + totalTax;
		// Total Shipping
		nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0";
		// Item Name
		nvpstr += "&L_PAYMENTREQUEST_0_NAME0=" + "FREETRIAL";
		// Item Description
		nvpstr += "&L_PAYMENTREQUEST_0_DESC0=" + "FREETRIAL";
		// Item Amount
		nvpstr += "&L_PAYMENTREQUEST_0_AMT0=" + amount;
		// Item Qty
		nvpstr += "&L_PAYMENTREQUEST_0_QTY0=1";
		// Item Tax
		nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT0=0";
		// Item Url
		// nvpstr += "&L_PAYMENTREQUEST_0_ITEMURL0=" + URLEncoder.encode(request.getScheme() + "://" +
		// request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +
		// "/subscription/selectPlan/" + planId);

		nvpstr += "&ReturnUrl=" + URLEncoder.encode(returnURL) + "&CANCELURL=" + URLEncoder.encode(cancelURL);

		// Make the call to PayPal to get the Express Checkout token If the API call succeded, then redirect the buyer
		// to PayPal to begin to authorize payment. If an error occured, show the resulting errors
		LOG.info("Sending to payment gateway with data : " + nvpstr);

		// Calls the SetExpressCheckout API call
		LOG.info("Invoking Paypal Service Express Checkout ============ >");
		HashMap<String, String> nvp = invokePaypalService("SetExpressCheckout", nvpstr);
		if (nvp == null) {
			paymentTransaction.setStatus(TransactionStatus.FAILURE);
			paymentTransaction.setErrorCode("-1");
			paymentTransaction.setErrorMessage("Error invoking paypal service");
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			// TODO Error URL
			// return "redirect:/supplier/supplierSubscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
		}
		LOG.info("Payment gateway response : " + nvp.get("ACK"));

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) {
			session.setAttribute(SubscriptionServiceImpl.TOKEN, nvp.get(SubscriptionServiceImpl.TOKEN).toString());
			LOG.info(" TOKEN : " + nvp.get(SubscriptionServiceImpl.TOKEN));

			// We dont need these for saving the subscription
			// if (supplier.getId() == null) {
			subscription.setSupplier(null);
			// }

			// subscription.setSupplierPlan(plan);
			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);
			subscription.setPaymentTransaction(paymentTransaction);
			if (subscription.getBuyerLimit() == null) {
				subscription.setBuyerLimit(9999);
			}

			subscription = saveSupplierSubscription(subscription);

			// Save the TOKEN in DB
			paymentTransaction.setSupplierSubscription(subscription);
			paymentTransaction.setPaymentToken(nvp.get(SubscriptionServiceImpl.TOKEN));
			paymentTransaction.setAdditionalTaxDesc("Tax " + tax + "%");
			paymentTransaction.setAdditionalTax(totalTax);
			paymentTransaction.setPriceAmount(amount);
			paymentTransaction.setAmount(amount.add(totalTax));
			paymentTransaction.setTotalPriceAmount(amount.add(totalTax));
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			// Reassign the values
			// subscription.setSupplier(supplier);

			session.setAttribute("subscriptionId", subscription.getId());
			session.setAttribute("paymentTransactionId", paymentTransaction.getId());
			session.setAttribute("supplier", supplier);

			String payPalURL = PAYPAL_URL + nvp.get(SubscriptionServiceImpl.TOKEN);
			return "redirect:" + payPalURL;

		} else {
			paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
		}
		return "redirect:" + cancelURL;
		// return "redirect:/supplier/supplierSubscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
	}

	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param request
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	public String initiateSupplierRegistrationPaypalPayment(SupplierSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL) {
		SupplierPlan plan = subscription.getSupplierPlan();

		if (plan == null) {
			LOG.info("Plan is null");
			return "redirect:supplierCheckout";
		} else {
			LOG.info("Plan " + plan.getPlanName());
			LOG.info("Plan curr code " + plan.getCurrency().getCurrencyCode());
		}
		// The paymentAmount is the total value of the shopping cart
		BigDecimal paymentAmount = subscription.getTotalPriceAmount();

		Supplier supplier = subscription.getSupplier();
		BigDecimal tax = BigDecimal.ZERO;
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

		// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
		String currencyCodeType = plan.getCurrency().getCurrencyCode();
		String paymentType = "Sale";

		// boolean bSandbox = true;
		// if (bSandbox == true) {
		// PAYPAL_URL = "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=";
		// } else {
		// PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
		// }

		// session.setAttribute("currencyCodeType", currencyCodeType);
		// session.setAttribute("paymentAmount", paymentAmount);
		// session.setAttribute("paymentType", paymentType);
		// session.setAttribute("quantity", subscription.getPlanQuantity());

		// Create an instance of PaymentTransaction at the beginning of payment flow.
		PaymentTransaction paymentTransaction = subscription.getPaymentTransaction();
		LOG.info("promocode : " + paymentTransaction.getPromoCode());
		LOG.info("promocode discount : " + subscription.getPromoCodeDiscount());
		if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
			LOG.info("promocode Id: " + paymentTransaction.getPromoCode().getId());
			PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(paymentTransaction.getPromoCode().getId());
			paymentTransaction.setPromoCode(promotionalCode);
		}

		paymentTransaction.setSupplierPlan(plan);
		paymentTransaction.setCreatedDate(new Date());
		paymentTransaction.setCurrencyCode(currencyCodeType);
		paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
		paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
		paymentTransaction.setType(TransactionType.PAYMENT);
		paymentTransaction.setAmount(paymentAmount);
		paymentTransaction.setCompanyRegistrationNumber(subscription.getSupplier().getCompanyRegistrationNumber());
		paymentTransaction.setCompanyName(subscription.getSupplier().getCompanyName());
		paymentTransaction.setCommunicationEmail(subscription.getSupplier().getCommunicationEmail());
		paymentTransaction.setLoginEmail(subscription.getSupplier().getLoginEmail());
		paymentTransaction.setCompanyContactNumber(subscription.getSupplier().getCompanyContactNumber());
		paymentTransaction.setFullName(subscription.getSupplier().getFullName());
		paymentTransaction.setPassword(subscription.getSupplier().getPassword());
		paymentTransaction.setRemarks(subscription.getSupplier().getRemarks());
		paymentTransaction.setMobileNumber(subscription.getSupplier().getMobileNumber());
		paymentTransaction.setCountry(subscription.getSupplier().getRegistrationOfCountry());
		paymentTransaction = savePaymentTransaction(paymentTransaction);

		// The returnURL is the location where buyers return to when a payment has been succesfully authorized.
		returnURL += paymentTransaction.getId();

		// The cancelURL is the location buyers are sent to when they hit the cancel button during authorization of
		// payment during the PayPal flow
		cancelURL += paymentTransaction.getId();
		BigDecimal amount = paymentAmount;
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal totalTax = (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

		// LOG.info("amount: " + amount );
		// LOG.info("tax: " + tax );
		// LOG.info("amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP) : " + amount.multiply(tax).setScale(2,
		// BigDecimal.ROUND_HALF_UP) );
		// LOG.info("(amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)): " +
		// (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)) );

		// Construct the parameter string that describes the PayPal payment.
		String nvpstr = "&PAYMENTREQUEST_0_AMT=" + amount.add(totalTax) + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType + "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan.";

		// No Shipping required
		nvpstr += "&NOSHIPPING=1";

		// Local code
		// nvpstr += "&LOCALECODE=MY";

		// Business Name Image
		// nvpstr += "&HDRIMG=";

		// Background Color of checkout page
		nvpstr += "&PAYFLOWCOLOR=0095D5";

		// SOLUTIONTYPE
		nvpstr += "&SOLUTIONTYPE=Sole"; // or Mark

		// Cart Border Color
		nvpstr += "&CARTBORDERCOLOR=0095D5";

		// Logo Image
		// nvpstr += "&LOGOIMG=" + URLEncoder.encode(request.getScheme() + "://" + request.getServerName() + ":" +
		// request.getServerPort() + request.getContextPath() + "/resources/images/pro_logo.png");

		// Buyer Email
		nvpstr += "&EMAIL=" + subscription.getPaymentTransaction().getCommunicationEmail();

		// Landing Page - Type of PayPal page to display
		nvpstr += "&LANDINGPAGE=Billing"; // or LANDINGPAGE=Login
		String gstDesc = "";
		String note = "You are subscribing for " + plan.getPeriod() + " " + plan.getPeriodUnit() + "S" + gstDesc;

		// Note to Buyer
		nvpstr += "&NOTETOBUYER=" + note;

		/**
		 * Item Details
		 */
		// Item Amount
		nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + subscription.getTotalPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		// Total Tax
		nvpstr += "&PAYMENTREQUEST_0_TAXAMT=" + totalTax;
		// Total Shipping
		nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0";
		// Item Name
		nvpstr += "&L_PAYMENTREQUEST_0_NAME0=" + plan.getPlanName();
		// Item Description
		nvpstr += "&L_PAYMENTREQUEST_0_DESC0=" + plan.getShortDescription();
		// Item Amount
		nvpstr += "&L_PAYMENTREQUEST_0_AMT0=" + subscription.getPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		// Item Qty
		nvpstr += "&L_PAYMENTREQUEST_0_QTY0=1";
		// Item Tax
		nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT0=0";

		if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME1=" + "Promo Discount ";
			String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP) + " OFF" : paymentTransaction.getPromoCode().getPromoDiscount() + "%25 OFF";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC1=" + paymentTransaction.getPromoCode().getPromoName() + "-" + promoDesc;
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT1=" + (subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("promo discount : " + subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY1=1";
			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT1=0";
		}

		// Item Url
		// nvpstr += "&L_PAYMENTREQUEST_0_ITEMURL0=" + URLEncoder.encode(request.getScheme() + "://" +
		// request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +
		// "/subscription/selectPlan/" + planId);

		nvpstr += "&ReturnUrl=" + URLEncoder.encode(returnURL) + "&CANCELURL=" + URLEncoder.encode(cancelURL);

		// Make the call to PayPal to get the Express Checkout token If the API call succeded, then redirect the buyer
		// to PayPal to begin to authorize payment. If an error occured, show the resulting errors
		LOG.info("Sending to payment gateway with data : " + nvpstr);

		// Calls the SetExpressCheckout API call
		LOG.info("Invoking Paypal Service Express Checkout ============ >");
		HashMap<String, String> nvp = invokePaypalService("SetExpressCheckout", nvpstr);
		if (nvp == null) {
			paymentTransaction.setStatus(TransactionStatus.FAILURE);
			paymentTransaction.setErrorCode("-1");
			paymentTransaction.setErrorMessage("Error invoking paypal service");
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			return "redirect:supplierCheckout";
		}
		LOG.info("Payment gateway response : " + nvp.get("ACK"));

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) {
			session.setAttribute(SubscriptionServiceImpl.TOKEN, nvp.get(SubscriptionServiceImpl.TOKEN).toString());
			LOG.info(" TOKEN : " + nvp.get(SubscriptionServiceImpl.TOKEN));

			// We dont need these for saving the subscription
			if (supplier != null && StringUtils.checkString(supplier.getId()).length() > 0) {
				subscription.setSupplier(supplier);
			} else {
				subscription.setSupplier(null);
			}

			// subscription.setSupplierPlan(plan);
			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);
			subscription.setPaymentTransaction(paymentTransaction);
			if (subscription.getBuyerLimit() == null) {
				subscription.setBuyerLimit(plan.getBuyerLimit());
			}

			subscription = saveSupplierSubscription(subscription);

			// Save the TOKEN in DB
			paymentTransaction.setSupplierSubscription(subscription);
			paymentTransaction.setPaymentToken(nvp.get(SubscriptionServiceImpl.TOKEN));
			paymentTransaction.setAdditionalTaxDesc("Tax " + tax + "%");
			paymentTransaction.setAdditionalTax(totalTax);
			paymentTransaction.setPriceAmount(amount);
			paymentTransaction.setAmount(amount.add(totalTax));
			paymentTransaction.setTotalPriceAmount(amount.add(totalTax));
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			// Reassign the values
			// subscription.setSupplier(supplier);

			session.setAttribute("subscriptionId", subscription.getId());
			session.setAttribute("paymentTransactionId", paymentTransaction.getId());
			session.setAttribute("supplier", supplier);

			String payPalURL = PAYPAL_URL + nvp.get(SubscriptionServiceImpl.TOKEN);
			return "redirect:" + payPalURL;

		} else {
			paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
		}
		return "redirect:supplierCheckout";
	}

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param paymentType
	 * @param paymentTransaction
	 * @throws ApplicationException
	 */
	@Override
	@Transactional(readOnly = false)
	public void confirmSupplierPaymentSubscription(String token, String payerId, Model model, String serverName, String paymentTransactionId, String paymentType, Boolean isSale) throws ApplicationException {

		if (isSale) {
			paymentType = "Sale";
		} else {
			paymentType = "Order";
		}

		LOG.info("Confirming Payment Transaction Id : " + paymentTransactionId);
		PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId);

		String nvpstr = "&TOKEN=" + token + "&PAYERID=" + payerId + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_AMT=" + paymentTransaction.getAmount();

		if (paymentTransaction.getSupplierPlan() != null) {
			nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" + paymentTransaction.getSupplierPlan().getCurrency().getCurrencyCode() + "&IPADDRESS=" + serverName;
		}

		LOG.info("Invoking PayPal Confirm Payment ===================> ");

		/* Make the call to PayPal to finalize payment If an error occured, show the resulting errors */

		HashMap<String, String> nvp = invokePaypalService("DoExpressCheckoutPayment", nvpstr);
		model.addAttribute("paypalResponse", nvp);

		SupplierSubscription subscription = null;
		String strAck = nvp.get("ACK").toString();
		try {
			subscription = getSupplierSubscriptionById(paymentTransaction.getSupplierSubscription().getId());
			if (strAck != null && strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) {

				// Update the Payment Transaction Details
				LOG.info("Subscription Id : " + paymentTransaction.getSupplierSubscription().getId());
				paymentTransaction.setConfirmationDate(new Date());
				paymentTransaction.setStatus(TransactionStatus.SUCCESS);
				paymentTransaction.setReferenceTransactionId(nvp.get("PAYMENTINFO_0_TRANSACTIONID"));
				paymentTransaction.setPaymentFee(new BigDecimal(nvp.get("PAYMENTINFO_0_AMT")));
				paymentTransaction = updatePaymentTransaction(paymentTransaction);

				Supplier supplier = new Supplier();
				// Create the Supplier account and send out email notification for account activiation.
				supplier.setLoginEmail(paymentTransaction.getLoginEmail());
				supplier.setCommunicationEmail(paymentTransaction.getCommunicationEmail());
				supplier.setCompanyContactNumber(paymentTransaction.getCompanyContactNumber());
				supplier.setCompanyName(paymentTransaction.getCompanyName());
				supplier.setCompanyRegistrationNumber(paymentTransaction.getCompanyRegistrationNumber());
				supplier.setSupplierSubscription(subscription);
				supplier.setFullName(paymentTransaction.getFullName());
				supplier.setPassword(paymentTransaction.getPassword());
				supplier.setRemarks(paymentTransaction.getRemarks());
				supplier.setMobileNumber(paymentTransaction.getMobileNumber());
				// supplier.setPlan(paymentTransaction.getSupplierPlan());
				supplier.setRegistrationOfCountry(paymentTransaction.getCountry());
				supplier.setStatus(SupplierStatus.PENDING);
				supplier.setSubscriptionDate(new Date());
				supplier.setCreatedDate(new Date());
				supplier.setDesignation(paymentTransaction.getDesignation());
				try {
					supplier = supplierService.saveSupplier(supplier, true);
				} catch (Exception e) {
					LOG.error("Error creating Supplier instance : " + e.getMessage(), e);
				}

				// Update the subscription details
				if (subscription != null) {
					subscription.setSupplier(supplier);

					subscription.setStartDate(new Date());
					subscription.setActivatedDate(new Date());
					subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
					Calendar endDate = Calendar.getInstance();
					if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.MONTH) {
						endDate.add(Calendar.MONTH, paymentTransaction.getSupplierPlan().getPeriod());
					} else if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.YEAR) {
						endDate.add(Calendar.YEAR, paymentTransaction.getSupplierPlan().getPeriod());
					}
					subscription.setEndDate(endDate.getTime());

					SupplierPackage sp = new SupplierPackage(subscription);
					subscription = updateSupplierSubscription(subscription);

					LOG.info("Supplier Id : " + supplier.getId());
					supplier.setSupplierPackage(sp);
					supplierService.updateSupplier(supplier);

				}
				// Update transaction details with buyer info
				paymentTransaction.setSupplier(supplier);
				paymentTransaction = updatePaymentTransaction(paymentTransaction);

			} else {
				paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
				// sending payment receipt email to supplier on failure
				String timeZone = "GMT+8:00";
				try {
					timeZone = getTimeZoneBySupplierSettings(subscription.getSupplier().getId(), timeZone);
					String msg = "Your payment transaction has failed during subscription";
					String subject = "Subscription Failure";
//					User user = userService.getDetailsOfLoggedinUser(subscription.getSupplier().getLoginEmail());
//					if(user.getEmailNotifications())
					sendSupplierSubscriptionMail(subscription.getSupplier().getCommunicationEmail(), subscription.getSupplier().getFullName(), subscription, timeZone, msg, subject, false);
				} catch (Exception e) {
					LOG.error("Error While sending failure subscription mail to supplier :" + e.getMessage(), e);
				}
				throw new ApplicationException("Payment transaction has failed");
			}
		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);

			paymentTransaction.setStatus(TransactionStatus.NEEDS_ATTENTION);
			paymentTransaction.setErrorMessage("Error during payment confirmation : " + e.getMessage());
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
		}

		model.addAttribute("subscription", subscription);
		model.addAttribute("paymentTransaction", paymentTransaction);
	}

	@Override
	@Transactional(readOnly = false)
	public boolean associateBuyerWithSupplier(boolean accepted, String loggedInUserTenantId, String buyerId) {
		boolean allowed = false;
		Supplier supplier = getSupplierWithSupplierPackagePlanByTenantId(loggedInUserTenantId);
		// for PH 211 we change supplier.getSupplierSubscription().getBuyerLimit() < 99

		// Check if this supplier is associated with the event Buyer. If not, add the buyer to association if the
		// subscription permits.
		if (supplier != null && supplier.getSupplierSubscription() != null) {
			if (CollectionUtil.isEmpty(supplier.getAssociatedBuyers())) {
				supplier.setAssociatedBuyers(new ArrayList<Buyer>());
			}

			// If buyer limit has not been reached, only then add the buyer. else dont allow association
			
			if ( supplier.getAssociatedBuyers().size() <= supplier.getSupplierPackage().getBuyerLimit()) {
				for (Buyer buyer : supplier.getAssociatedBuyers()) {
					if (buyer.getId().equals(buyerId)) {
						allowed = true;
						break;
					}
				}
				if (supplier.getSupplierPackage().getSupplierPlan() != null && !allowed && supplier.getAssociatedBuyers().size() < supplier.getSupplierPackage().getBuyerLimit()) {
					LOG.info("Adding Buyer : " + buyerId + " in associated buyer list of supplier : " + loggedInUserTenantId);
					allowed = true;
					supplier.getAssociatedBuyers().add(new Buyer(buyerId));
				}
			}
			supplierService.updateSupplier(supplier);
		}

		return allowed;
	}

	@Override
	@Transactional(readOnly = false)
	public String initiateSupplierRegistrationPaypalPaymentForBuyPlan(SupplierSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL) {
		SupplierPlan plan = subscription.getSupplierPlan();

		if (plan == null) {
			LOG.info("Plan is null");
			return "redirect:supplierPlanCheckout";
		} else {
			LOG.info("Plan " + plan.getPlanName());
			LOG.info("Plan curr code " + plan.getCurrency().getCurrencyCode());
		}
		// The paymentAmount is the total value of the shopping cart
		BigDecimal paymentAmount = subscription.getTotalPriceAmount();
		Supplier supplier = subscription.getSupplier();
		BigDecimal tax = BigDecimal.ZERO;
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

		// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
		String currencyCodeType = plan.getCurrency().getCurrencyCode();
		String paymentType = "Sale";

		// boolean bSandbox = true;
		// if (bSandbox == true) {
		// PAYPAL_URL = "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout&token=";
		// } else {
		// PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
		// }

		// session.setAttribute("currencyCodeType", currencyCodeType);
		// session.setAttribute("paymentAmount", paymentAmount);
		// session.setAttribute("paymentType", paymentType);
		// session.setAttribute("quantity", subscription.getPlanQuantity());

		// Create an instance of PaymentTransaction at the beginning of payment flow.
		PaymentTransaction paymentTransaction = subscription.getPaymentTransaction();
		LOG.info("promocode : " + paymentTransaction.getPromoCode());
		LOG.info("promocode discount : " + subscription.getPromoCodeDiscount());
		if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
			LOG.info("promocode Id: " + paymentTransaction.getPromoCode().getId());
			PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(paymentTransaction.getPromoCode().getId());
			paymentTransaction.setPromoCode(promotionalCode);
		}

		paymentTransaction.setSupplierPlan(plan);
		paymentTransaction.setCreatedDate(new Date());
		paymentTransaction.setCurrencyCode(currencyCodeType);
		paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
		paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
		paymentTransaction.setType(TransactionType.PAYMENT);
		paymentTransaction.setAmount(paymentAmount);
		paymentTransaction.setCompanyRegistrationNumber(subscription.getSupplier().getCompanyRegistrationNumber());
		paymentTransaction.setCompanyName(subscription.getSupplier().getCompanyName());
		paymentTransaction.setCommunicationEmail(subscription.getSupplier().getCommunicationEmail());
		paymentTransaction.setLoginEmail(subscription.getSupplier().getLoginEmail());
		paymentTransaction.setCompanyContactNumber(subscription.getSupplier().getCompanyContactNumber());
		paymentTransaction.setFullName(subscription.getSupplier().getFullName());
		paymentTransaction.setPassword(subscription.getSupplier().getPassword());
		paymentTransaction.setRemarks(subscription.getSupplier().getRemarks());
		paymentTransaction.setMobileNumber(subscription.getSupplier().getMobileNumber());
		paymentTransaction.setCountry(subscription.getSupplier().getRegistrationOfCountry());
		paymentTransaction = savePaymentTransaction(paymentTransaction);

		// The returnURL is the location where buyers return to when a payment has been succesfully authorized.
		returnURL += paymentTransaction.getId();

		// The cancelURL is the location buyers are sent to when they hit the cancel button during authorization of
		// payment during the PayPal flow
		cancelURL += paymentTransaction.getId();
		BigDecimal amount = paymentAmount;
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal totalTax = (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

		// LOG.info("amount: " + amount );
		// LOG.info("tax: " + tax );
		// LOG.info("amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP) : " + amount.multiply(tax).setScale(2,
		// BigDecimal.ROUND_HALF_UP) );
		// LOG.info("(amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)): " +
		// (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)) );

		// Construct the parameter string that describes the PayPal payment.
		String nvpstr = "&PAYMENTREQUEST_0_AMT=" + amount.add(totalTax) + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType + "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan.";

		// No Shipping required
		nvpstr += "&NOSHIPPING=1";

		// Local code
		// nvpstr += "&LOCALECODE=MY";

		// Business Name Image
		// nvpstr += "&HDRIMG=";

		// Background Color of checkout page
		nvpstr += "&PAYFLOWCOLOR=0095D5";

		// SOLUTIONTYPE
		nvpstr += "&SOLUTIONTYPE=Sole"; // or Mark

		// Cart Border Color
		nvpstr += "&CARTBORDERCOLOR=0095D5";

		// Logo Image
		// nvpstr += "&LOGOIMG=" + URLEncoder.encode(request.getScheme() + "://" + request.getServerName() + ":" +
		// request.getServerPort() + request.getContextPath() + "/resources/images/pro_logo.png");

		// Buyer Email
		nvpstr += "&EMAIL=" + subscription.getPaymentTransaction().getCommunicationEmail();

		// Landing Page - Type of PayPal page to display
		nvpstr += "&LANDINGPAGE=Billing"; // or LANDINGPAGE=Login
		String gstDesc = "";
		String note = "You are subscribing for " + plan.getPeriod() + " " + plan.getPeriodUnit() + "S" + gstDesc;

		// Note to Buyer
		nvpstr += "&NOTETOBUYER=" + note;

		/**
		 * Item Details
		 */
		// Item Amount
		nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + subscription.getTotalPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		// Total Tax
		nvpstr += "&PAYMENTREQUEST_0_TAXAMT=" + totalTax;
		// Total Shipping
		nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0";
		// Item Name
		nvpstr += "&L_PAYMENTREQUEST_0_NAME0=" + plan.getPlanName();
		// Item Description
		nvpstr += "&L_PAYMENTREQUEST_0_DESC0=" + plan.getShortDescription();
		// Item Amount
		nvpstr += "&L_PAYMENTREQUEST_0_AMT0=" + subscription.getPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		// Item Qty
		nvpstr += "&L_PAYMENTREQUEST_0_QTY0=1";
		// Item Tax
		nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT0=0";

		if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME1=" + "Promo Discount ";
			String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP) + " OFF" : paymentTransaction.getPromoCode().getPromoDiscount() + "%25 OFF";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC1=" + paymentTransaction.getPromoCode().getPromoName() + "-" + promoDesc;
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT1=" + (subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("promo discount : " + subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY1=1";
			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT1=0";
		}

		// Item Url
		// nvpstr += "&L_PAYMENTREQUEST_0_ITEMURL0=" + URLEncoder.encode(request.getScheme() + "://" +
		// request.getServerName() + ":" + request.getServerPort() + request.getContextPath() +
		// "/subscription/selectPlan/" + planId);

		nvpstr += "&ReturnUrl=" + URLEncoder.encode(returnURL) + "&CANCELURL=" + URLEncoder.encode(cancelURL);

		// Make the call to PayPal to get the Express Checkout token If the API call succeded, then redirect the buyer
		// to PayPal to begin to authorize payment. If an error occured, show the resulting errors
		LOG.info("Sending to payment gateway with data : " + nvpstr);

		// Calls the SetExpressCheckout API call
		LOG.info("Invoking Paypal Service Express Checkout ============ >");
		HashMap<String, String> nvp = invokePaypalService("SetExpressCheckout", nvpstr);
		if (nvp == null) {
			paymentTransaction.setStatus(TransactionStatus.FAILURE);
			paymentTransaction.setErrorCode("-1");
			paymentTransaction.setErrorMessage("Error invoking paypal service");
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			return "redirect:supplierPlanCheckout";
		}
		LOG.info("Payment gateway response : " + nvp.get("ACK"));

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS)) {
			session.setAttribute(SubscriptionServiceImpl.TOKEN, nvp.get(SubscriptionServiceImpl.TOKEN).toString());
			LOG.info(" TOKEN : " + nvp.get(SubscriptionServiceImpl.TOKEN));

			// We dont need these for saving the subscription
			if (supplier != null && StringUtils.checkString(supplier.getId()).length() > 0) {
				subscription.setSupplier(supplier);
			} else {
				subscription.setSupplier(null);
			}

			// subscription.setSupplierPlan(plan);
			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);
			subscription.setPaymentTransaction(paymentTransaction);
			if (subscription.getBuyerLimit() == null) {
				subscription.setBuyerLimit(plan.getBuyerLimit());
			}

			subscription = saveSupplierSubscription(subscription);

			// Save the TOKEN in DB
			paymentTransaction.setSupplierSubscription(subscription);
			paymentTransaction.setPaymentToken(nvp.get(SubscriptionServiceImpl.TOKEN));
			paymentTransaction.setAdditionalTaxDesc("Tax " + tax + "%");
			paymentTransaction.setAdditionalTax(totalTax);
			paymentTransaction.setPriceAmount(amount);
			paymentTransaction.setAmount(amount.add(totalTax));
			paymentTransaction.setTotalPriceAmount(amount.add(totalTax));
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			// Reassign the values
			// subscription.setSupplier(supplier);

			session.setAttribute("subscriptionId", subscription.getId());
			session.setAttribute("paymentTransactionId", paymentTransaction.getId());
			session.setAttribute("supplier", supplier);

			String payPalURL = PAYPAL_URL + nvp.get(SubscriptionServiceImpl.TOKEN);
			return "redirect:" + payPalURL;

		} else {
			paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
		}
		return "redirect:supplierPlanCheckout";
	}

	@Override
	public List<SupplierSubscription> getSupplierSubscriptionValidity(String supplierId) {
		return supplierSubscriptionDao.getSupplierSubscriptionValidityBySupplierId(supplierId);
	}

	private BigDecimal getPromoDiscount(String promoCode, BigDecimal totalPrice) throws Exception {
		BigDecimal promoDiscountPrice = BigDecimal.ZERO;
		if (StringUtils.checkString(promoCode).length() > 0) {
			PromotionalCode promo = promotionalCodeService.getPromotionalCodePromoCode(promoCode);
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

	@Override
	@Transactional(readOnly = false)
	public SupplierPackage updateSupplierPackage(SupplierPackage sp) throws ApplicationException {
		try {
			return supplierPackageDao.update(sp);
		} catch (Exception e) {
			Log.error("Error in updating supplier package ", e);
			throw new ApplicationException(e);
		}
	}

}
