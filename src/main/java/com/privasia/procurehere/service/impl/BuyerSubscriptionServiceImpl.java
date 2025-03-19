/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.BuyerSubscriptionDao;
import com.privasia.procurehere.core.dao.PaymentTransactionDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.dao.UserRoleDao;
import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerPackage;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.PlanPeriod;
import com.privasia.procurehere.core.entity.PlanRange;
import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.PaymentMethod;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.enums.TransactionType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.BuyerSubscriptionPojo;
import com.privasia.procurehere.core.pojo.CurrencyConversion;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerPlanService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSubscriptionService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.PlanService;
import com.privasia.procurehere.service.PromotionalCodeService;

/**
 * @author parveen
 */
@Service
@Transactional(readOnly = true)
public class BuyerSubscriptionServiceImpl implements BuyerSubscriptionService {

	private static final Logger LOG = LogManager.getLogger(Global.SUBSCRIPTION_LOG);

	public static final String TOKEN = "TOKEN";
	public static final String SUCCESS = "Success";

	@Autowired
	BuyerService buyerService;

	@Autowired
	PaymentTransactionDao paymentTransactionDao;

	@Autowired
	PlanService planService;

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

	@Value("${paypal.app.id}")
	String PAYPAL_APP_ID;

	@Value("${paypal.adaptive-payments.url}")
	String PAYPAL_ADAPTIVE_PAYMENTS_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	BuyerPlanService buyerPlanService;

	@Autowired
	BuyerSubscriptionDao buyerSubscriptionDao;

	@Autowired
	PromotionalCodeService promotionalCodeService;

	@Autowired
	UserDao userDao;

	@Autowired
	UserRoleDao userRoleDao;

	@Autowired
	AccessRightsDao accessRightsDao;

	@Autowired
	ServletContext context;

	@Override
	public BuyerSubscription getBuyerSubscriptionById(String subscriptionId) {
		BuyerSubscription subs = buyerSubscriptionDao.getBuyerSubscriptionById(subscriptionId);

		if (subs != null) {
			if (subs.getPlan() != null) {
				if (CollectionUtil.isNotEmpty(subs.getPlan().getPlanPeriodList())) {
					for (PlanPeriod per : subs.getPlan().getPlanPeriodList()) {
						per.getPlanDuration();
					}
				}
				if (CollectionUtil.isNotEmpty(subs.getPlan().getRangeList())) {
					for (PlanRange range : subs.getPlan().getRangeList()) {
						range.getRangeStart();
					}
				}

				if (subs.getBuyer() != null) {
					subs.getBuyer().getRegistrationOfCountry();
					subs.getBuyer().getRegistrationOfCountry().getCountryCode();
				}
			}
		}
		return subs;
	}

	@Override
	public BuyerSubscription getBuyerSubscriptionByIdForUpdateSubs(String subscriptionId) {
		BuyerSubscription subs = buyerSubscriptionDao.getBuyerSubscriptionById(subscriptionId);

		if (subs != null) {
			if (subs.getPlan() != null) {
				if (CollectionUtil.isNotEmpty(subs.getPlan().getPlanPeriodList())) {
					for (PlanPeriod per : subs.getPlan().getPlanPeriodList()) {
						per.getPlanDuration();
					}
				}
				if (CollectionUtil.isNotEmpty(subs.getPlan().getRangeList())) {
					for (PlanRange range : subs.getPlan().getRangeList()) {
						range.getRangeStart();
					}
				}

				if (subs.getPlan().getCurrency() != null) {
					subs.getPlan().getCurrency().getCurrencyCode();
					// setting currency into transient due to Json Ignore
					subs.getPlan().setCurrencyCode(subs.getPlan().getCurrency().getCurrencyCode());
				}
			}

			// Calculate Remaining months to charge amount
			if (subs.getPlanType() == PlanType.PER_USER && subs.getPlanPeriod() != null) {
				subs.setChargeMonths(calculateChargeableMonths(subs.getStartDate(), subs.getEndDate()));
			}
		}
		return subs;
	}

	public static int calculateChargeableMonths(Date startDate, Date endDate) {
		int chargeMonths = 0;
		Calendar start = Calendar.getInstance();
		// if future plan
		if (startDate.after(new Date())) {
			start.setTime(startDate);
		}
		start.set(Calendar.AM_PM, Calendar.AM);
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);

		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		end.set(Calendar.AM_PM, Calendar.AM);
		end.set(Calendar.HOUR, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);
		LOG.info("Start date :" + start.getTime() + " ===end :" + end.getTime());
		while (start.compareTo(end) < 0) {
			LOG.info("start.compareTo(end) :" + start.compareTo(end));
			chargeMonths++;
			start.add(Calendar.MONTH, 1);
			LOG.info("Start date :" + start.getTime());
		}
		// if user buying last day of subs
		if (chargeMonths == 0) {
			chargeMonths = 1;
		}
		return chargeMonths;
	}

	@Override
	@Transactional(readOnly = false)
	public BuyerSubscription saveBuyerSubscription(BuyerSubscription subscription) {
		return buyerSubscriptionDao.saveOrUpdate(subscription);
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
	@Transactional(readOnly = false)
	public BuyerSubscription updateSubscription(BuyerSubscription subscription) {
		return buyerSubscriptionDao.update(subscription);
	}

	@Override
	public PlanType getBuyerSubscriptionPlanTypeByBuyerID(String buyerId) {
		return buyerSubscriptionDao.getBuyerSubscriptionPlanTypeByBuyerID(buyerId);
	}

	/**
	 * @param txId
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	public PaymentTransaction cancelPaymentTransaction(String txId) {
		PaymentTransaction paymentTransaction = getPaymentTransactionById(txId);
		if (paymentTransaction == null) {
			paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(txId);
		}

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

	@Override
	@Transactional(readOnly = false)
	public String initiatePaypalPaymentForBuyerSubscription(BuyerSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL) throws ApplicationException {
		BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
		if (plan == null) {
			throw new ApplicationException("Plane is null ");
			// return "redirect:/buyerSubscription/selectPlan";
		}
		Buyer buyer = subscription.getBuyer();

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

		// The paymentAmount is the total value of the shopping cart
		BigDecimal paymentAmount = new BigDecimal(0);
		if (subscription.getTotalPriceAmount() != null) {
			paymentAmount = subscription.getTotalPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		LOG.info("paymentAmount : " + paymentAmount);
		// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
		String currencyCodeType = plan.getCurrency().getCurrencyCode();
		String paymentType = "Sale";
		// Create an instance of PaymentTransaction at the beginning of payment flow.
		PaymentTransaction paymentTransaction = null;
		if (CollectionUtil.isNotEmpty(subscription.getPaymentTransactions())) {
			paymentTransaction = subscription.getPaymentTransactions().get(0);
			if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(paymentTransaction.getPromoCode().getId());
				paymentTransaction.setPromoCode(promotionalCode);
			}
			paymentTransaction.setBuyerPlan(plan);
			paymentTransaction.setCreatedDate(new Date());
			// Convert currency if user select Malaysia country
			// if (paymentTransaction.getCountry() != null &&
			// paymentTransaction.getCountry().getCountryCode().equalsIgnoreCase("MY")) {
			// currencyCodeType = "MYR"; // paymentTransaction.getCountry().getCountryCode();
			// try {
			// // call currency change Api here
			// paymentAmount = convertUsdToMyr(paymentAmount, paymentTransaction);
			// if (paymentAmount != null) {
			// paymentAmount = paymentAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
			// }
			// BigDecimal priceAmount = convertUsdToMyr(subscription.getPriceAmount(), null);
			// if (priceAmount != null) {
			// subscription.setPriceAmount(priceAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal priceDiscount = convertUsdToMyr(subscription.getPriceDiscount(), null);
			// if (priceDiscount != null) {
			// subscription.setPriceDiscount(priceDiscount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal promoCodeDiscount = convertUsdToMyr(subscription.getPromoCodeDiscount(), null);
			// if (promoCodeDiscount != null) {
			// subscription.setPromoCodeDiscount(promoCodeDiscount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal totalPriceAmount = convertUsdToMyr(subscription.getTotalPriceAmount(), null);
			// if (totalPriceAmount != null) {
			// subscription.setTotalPriceAmount(totalPriceAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// } catch (Exception e) {
			// LOG.error("Error while calling currency change Api :" + e.getMessage(), e);
			// }
			// }
			LOG.info("currencyCodeType " + currencyCodeType);
			paymentTransaction.setCurrencyCode(currencyCodeType);

			paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
			paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
			paymentTransaction.setType(TransactionType.PAYMENT);
			paymentTransaction.setAmount(paymentAmount);
			paymentTransaction.setPriceAmount(subscription.getPriceAmount());
			paymentTransaction.setPriceDiscount(subscription.getPriceDiscount());
			paymentTransaction.setPromoCodeDiscount(subscription.getPromoCodeDiscount());
			paymentTransaction.setTotalPriceAmount(subscription.getTotalPriceAmount());
			paymentTransaction = savePaymentTransaction(paymentTransaction);
		}
		// The returnURL is the location where buyers return to when a payment has been succesfully authorized.
		returnURL += paymentTransaction.getId();

		// The cancelURL is the location buyers are sent to when they hit the cancel button during authorization of
		// payment during the PayPal flow
		cancelURL += paymentTransaction.getId();

		String note = "You are subscribing for ";
		String paymentRemarks = "Payment for ";
		int userEventQuantity = 0;
		int quantity = 1;
		String gstDescription = "";
		String gstDesc = "";
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		if (tax != null && tax.floatValue() > 0) {
			gstDescription = " (Inclusive of " + tax + "% Tax)";
			gstDesc = " (Inclusive of " + tax + "%25 Tax)";
		}
		// }

		if (plan.getPlanType() == PlanType.PER_USER) {

			if (plan.getBasePrice() != null && plan.getBaseUsers() > subscription.getUserQuantity()) {
				LOG.info("UserQuantity: " + userEventQuantity + "=====Base Users: " + plan.getBaseUsers());
				subscription.setUserQuantity(plan.getBaseUsers());
			}

			note += subscription.getUserQuantity() + " users for " + subscription.getPlanPeriod().getPlanDuration() + " months " + gstDesc;
			paymentRemarks += subscription.getUserQuantity() + " users for " + subscription.getPlanPeriod().getPlanDuration() + " months " + gstDescription;
			userEventQuantity = subscription.getUserQuantity();
			quantity = subscription.getPlanPeriod().getPlanDuration();
		} else {
			note += subscription.getEventQuantity() + " events for unlimited users " + gstDesc;
			paymentRemarks += subscription.getEventQuantity() + " events for unlimited users " + gstDescription;

			userEventQuantity = subscription.getEventQuantity();
		}

		// GST
		BigDecimal totalTax = new BigDecimal(0);
		BigDecimal itemTax = new BigDecimal(0);
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {

		LOG.info("paymentAmount: " + paymentAmount);
		LOG.info("tax: " + tax);
		LOG.info("paymentAmount.multiply(tax): " + paymentAmount.multiply(tax));

		totalTax = (paymentAmount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
		LOG.info("totalTax0 :" + totalTax);
		// itemTax = (totalTax.divide(new BigDecimal(quantity), 2, RoundingMode.HALF_UP));
		// itemTax = itemTax.setScale(2, BigDecimal.ROUND_HALF_UP);
		// totalTax = itemTax.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
		// }
		LOG.info("totalTax1 :" + totalTax);
		// Construct the parameter string that describes the PayPal payment.
		String nvpstr = "&PAYMENTREQUEST_0_AMT=" + paymentAmount.add(totalTax) + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType + "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan.";

		LOG.info("paymentAmount + totalTax = " + paymentAmount + " + " + totalTax + " = " + paymentAmount.add(totalTax));

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
		nvpstr += "&EMAIL=" + paymentTransaction.getCommunicationEmail();

		// Landing Page - Type of PayPal page to display

		nvpstr += "&LANDINGPAGE=Billing"; // or nvpstr += "&LANDINGPAGE=Login";

		/**
		 * Recurring Payments
		 */
		if (Boolean.TRUE == subscription.getAutoChargeSubscription()) {
			// Billing type
			nvpstr += "&L_BILLINGTYPE0=RecurringPayments";
			// Billing Agreement Desc
			nvpstr += "&L_BILLINGAGREEMENTDESCRIPTION0=" + "Payment for " + plan.getPlanName() + " plan at procurehere.com ";
		}
		// Note to Buyer
		nvpstr += "&NOTETOBUYER=" + note;

		/**
		 * Item Details
		 */
		// Item Amount
		nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + paymentAmount;
		LOG.info("paymentAmount :" + paymentAmount);

		// Total Tax
		nvpstr += "&PAYMENTREQUEST_0_TAXAMT=" + totalTax;
		// Total Shipping
		nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0";
		int item = 0;
		BigDecimal rangePrice = subscription.getRange().getPrice();
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		// rangePrice = convertUsdToMyr(subscription.getRange().getPrice(), null).setScale(2, BigDecimal.ROUND_HALF_UP);
		// } else {
		rangePrice = rangePrice.setScale(2, BigDecimal.ROUND_HALF_UP);
		// }

		// Item Name
		nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + plan.getPlanName();
		// Item Description
		nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + plan.getShortDescription();

		BigDecimal itemAmount = BigDecimal.ZERO;
		if (plan.getBasePrice() != null) {
			itemAmount = plan.getBasePrice().setScale(2, BigDecimal.ROUND_HALF_UP);
			// if (currencyCodeType.equalsIgnoreCase("MYR")) {
			// itemAmount = convertUsdToMyr(plan.getBasePrice(), null).setScale(2, BigDecimal.ROUND_HALF_UP);
			// }

			if (plan.getBaseUsers() < userEventQuantity) {
				userEventQuantity = userEventQuantity - plan.getBaseUsers();
				LOG.info("itemAmount : " + itemAmount + " rangePrice :" + rangePrice + " userEventQuantity: " + userEventQuantity);
				itemAmount = itemAmount.add(((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);
		} else {
			itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);

		// Item Amount
		nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + itemAmount.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP);

		// Item Qty
		nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";

		// Item Tax
		nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=" + totalTax;
		LOG.info("tax on total item :" + totalTax);
		item++;
		if (subscription.getPlanPeriod() != null && subscription.getPriceDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + "Subscription Discount";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + subscription.getPlanPeriod().getPlanDiscount() + " %25";
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + (subscription.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("subscription discount : " + subscription.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());

			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";

			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=0";
			item++;
		}
		if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + "Promo Discount ";
			String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP) + " OFF" : paymentTransaction.getPromoCode().getPromoDiscount() + "%25 OFF";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + paymentTransaction.getPromoCode().getPromoName() + "-" + promoDesc;
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + (subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("promo discount : " + subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";
			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=0";
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
		LOG.info("Payment gateway response : " + nvp.get("ACK"));

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {
			session.setAttribute(BuyerSubscriptionServiceImpl.TOKEN, nvp.get(BuyerSubscriptionServiceImpl.TOKEN).toString());
			LOG.info(" TOKEN : " + nvp.get(BuyerSubscriptionServiceImpl.TOKEN));

			// We dont need these for saving the subscription
			LOG.info("buyer ID: " + buyer.getId());
			if (buyer != null && StringUtils.checkString(buyer.getId()).length() > 0) {
				subscription.setBuyer(buyer);
			} else {
				subscription.setBuyer(null);
			}

			subscription.setPlan(plan);
			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);

			subscription.setPriceAmount(((itemAmount).multiply(new BigDecimal(quantity))).setScale(2, BigDecimal.ROUND_HALF_UP));
			subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
			subscription.setTotalPriceAmount(paymentAmount.add(totalTax));
			subscription = saveBuyerSubscription(subscription);

			// Save the TOKEN in DB
			paymentTransaction.setBuyerSubscription(subscription);
			paymentTransaction.setPaymentToken(nvp.get(BuyerSubscriptionServiceImpl.TOKEN));

			paymentTransaction.setPriceAmount(subscription.getPriceAmount());
			paymentTransaction.setRemarks(paymentRemarks);
			paymentTransaction.setAdditionalTaxDesc("Tax " + tax + "%");
			paymentTransaction.setAdditionalTax(totalTax);
			paymentTransaction.setAmount(subscription.getTotalPriceAmount());
			paymentTransaction.setTotalPriceAmount(subscription.getTotalPriceAmount());
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
			// Reassign the values
			// subscription.setBuyer(buyer);

			session.setAttribute("subscriptionId", subscription.getId());
			session.setAttribute("paymentTransactionId", paymentTransaction.getId());
			session.setAttribute("buyer", buyer);

			String payPalURL = PAYPAL_URL + nvp.get(BuyerSubscriptionServiceImpl.TOKEN);
			return "redirect:" + payPalURL;

		} else {
			paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
		}

		return "redirect:../subscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
	}

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

	private BigDecimal convertUsdToMyr(BigDecimal amount, PaymentTransaction paymentTransaction) {
		BigDecimal convertedAmount = null;
		try {
			if (amount != null) {
				RestTemplate restTemplate = new RestTemplate();
				CurrencyConversion currencyConversion = restTemplate.getForObject("https://api.fixer.io/latest?base=USD&symbols=MYR", CurrencyConversion.class);
				BigDecimal rate = null;
				if (currencyConversion != null && currencyConversion.getRates() != null && !currencyConversion.getRates().values().isEmpty()) {
					rate = currencyConversion.getRates().values().iterator().next();
					if (rate != null) {
						if (paymentTransaction != null) {
							paymentTransaction.setExchangeRate(rate.setScale(2, BigDecimal.ROUND_HALF_UP) + "");
						}
						convertedAmount = (amount.setScale(2, BigDecimal.ROUND_HALF_UP)).multiply(rate.setScale(2, BigDecimal.ROUND_HALF_UP));
					}
				}
				LOG.info("currencyConversion :" + currencyConversion.toString() + "==USD amount ==" + amount + " == converted amount ==" + convertedAmount);
			} else {
				LOG.info("Amount is null");
			}
		} catch (Exception e) {
			LOG.error("Error while converting USD to MYR :" + e.getMessage(), e);
		}
		return convertedAmount;
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

	private PaymentTransaction markTransactionFailed(PaymentTransaction paymentTransaction, HashMap<String, String> nvp) {
		// Display a user friendly Error on the page using any of the following error information returned by PayPal
		String errorCode = nvp.get("L_ERRORCODE0") != null ? nvp.get("L_ERRORCODE0").toString() : "";
		String errorShortMsg = nvp.get("L_SHORTMESSAGE0") != null ? nvp.get("L_SHORTMESSAGE0").toString() : "";
		String errorLongMsg = nvp.get("L_LONGMESSAGE0") != null ? nvp.get("L_LONGMESSAGE0").toString() : "";
		String errorSeverityCode = nvp.get("L_SEVERITYCODE0") != null ? nvp.get("L_SEVERITYCODE0").toString() : "";

		LOG.error("Error during payment. Error code : " + errorCode + ", Short Message : " + errorShortMsg + ", Severity : " + errorSeverityCode + ", Error Message : " + errorLongMsg);

		// Save the TOKEN in DB
		paymentTransaction.setStatus(TransactionStatus.FAILURE);
		paymentTransaction.setErrorCode(errorCode);
		paymentTransaction.setErrorMessage(errorLongMsg);
		paymentTransaction = updatePaymentTransaction(paymentTransaction);
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
	@Override
	@Transactional(readOnly = false)
	public User confirmSubscription(String token, String payerId, Model model, String serverName, String paymentTransactionId, boolean isSale) throws ApplicationException {

		User user = null;
		String paymentType = "Sale";
		if (!isSale) {
			paymentType = "Order";
		}
		PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId);

		String nvpstr = "&TOKEN=" + token + "&PAYERID=" + payerId + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_AMT=" + paymentTransaction.getAmount();
		nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" + paymentTransaction.getCurrencyCode() + "&IPADDRESS=" + serverName;

		// INSTANT Payment Notification URL
		// nvpstr += "&NOTIFYURL=" + APP_URL + "/buyerSubscription/instantPaymentNotification";

		LOG.info("Invoking PayPal Confirm Payment ===================> ");
		LOG.info("Confirm nvp ===================> " + nvpstr);

		/*
		 * Make the call to PayPal to finalize payment If an error occured, show the resulting errors
		 */
		HashMap<String, String> nvp = invokePaypalService("DoExpressCheckoutPayment", nvpstr);
		model.addAttribute("paypalResponse", nvp);

		BuyerSubscription subscription = null;
		String strAck = nvp.get("ACK").toString();
		try {
			subscription = getBuyerSubscriptionById(paymentTransaction.getBuyerSubscription().getId());
			if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {

				// Update the Payment Transaction Details
				LOG.info("Subscription Id : " + paymentTransaction.getBuyerSubscription().getId());
				paymentTransaction.setConfirmationDate(new Date());
				paymentTransaction.setStatus(TransactionStatus.SUCCESS);
				paymentTransaction.setReferenceTransactionId(nvp.get("PAYMENTINFO_0_TRANSACTIONID"));
				paymentTransaction.setPaymentFee(new BigDecimal(nvp.get("PAYMENTINFO_0_AMT")));
				paymentTransaction = updatePaymentTransaction(paymentTransaction);

				// Create the Buyer account and send out email notification for account activiation.
				Buyer buyer = new Buyer();
				buyer.setLoginEmail(paymentTransaction.getLoginEmail());
				buyer.setCommunicationEmail(paymentTransaction.getCommunicationEmail());
				buyer.setCompanyContactNumber(paymentTransaction.getCompanyContactNumber());
				buyer.setCompanyName(paymentTransaction.getCompanyName());
				buyer.setCompanyRegistrationNumber(paymentTransaction.getCompanyRegistrationNumber());
				// buyer.setCurrentSubscription(subscription); this is set later below
				buyer.setFullName(paymentTransaction.getFullName());
				buyer.setPlan(paymentTransaction.getBuyerPlan());
				buyer.setRegistrationOfCountry(paymentTransaction.getCountry());
				buyer.setStatus(BuyerStatus.PENDING);
				buyer.setSubscriptionDate(new Date());
				buyer.setCreatedDate(new Date());
				try {
					user = buyerService.saveBuyer(buyer);
					user.getBuyer().getCommunicationEmail();
				} catch (Exception e) {
					LOG.error("Error creating Buyer instance : " + e.getMessage(), e);
				}

				// Update the subscription details
				if (subscription != null) {
					subscription.setBuyer(user.getBuyer());

					BuyerSubscription previous = null;

					if (!isSale) {
						previous = saveTrialSubscription(user, user.getBuyer());
						previous.setBuyer(user.getBuyer());
						updateSubscription(previous);
					}

					// Calculate Start/End Date etc...
					doComputeSubscription(subscription, previous);

					// If previous exists then use that to prepare the Buyer Package else the new one.
					BuyerPackage bp = new BuyerPackage(previous == null ? subscription : previous);
					buyer.setCurrentSubscription(previous == null ? subscription : previous);

					subscription = updateSubscription(subscription);
					if (!isSale) {
						previous.setNextSubscription(subscription);
						bp.setEndDate(subscription.getEndDate());
						updateSubscription(previous);
					}
					buyer.setBuyerPackage(bp);
					// buyer = buyerService.findBuyerById(user.getTenantId());
					LOG.info("Buyer Id : " + buyer.getId());
					buyer = buyerService.updateBuyer(buyer);
				}

				// Update transaction details with buyer info
				paymentTransaction.setBuyer(buyer);
				paymentTransaction = updatePaymentTransaction(paymentTransaction);
				if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
					promotionalCodeService.updatePromoCode(paymentTransaction.getPromoCode().getId());
				}
			} else {
				paymentTransaction = markTransactionFailed(paymentTransaction, nvp);

				// sending mail to buyer on payment failure
				try {
					String msg = "Your payment transaction has failed during subscription";
					String subject = "Subscription Failure";
					sendBuyerSubscriptionMail(paymentTransaction.getCommunicationEmail(), paymentTransaction.getFullName(), subscription, msg, subject, false, paymentTransaction);
				} catch (Exception e) {
					LOG.error("Error While sending failure subscription mail to buyer :" + e.getMessage(), e);
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

		if (subscription != null && Boolean.TRUE == subscription.getAutoChargeSubscription()) {
			try {
				createRecurringPaymentsSubscription(token, subscription, paymentTransaction);
			} catch (Exception e1) {
				LOG.error("Error while recurring payment for '" + paymentTransactionId + "' transaction id :" + e1.getMessage(), e1);
			}
		}
		model.addAttribute("subscription", subscription);
		model.addAttribute("paymentTransaction", paymentTransaction);
		return user;
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

			notificationService.sendEmail(mailTo, subject, map, Global.CANCEL_SUBSCRIPTION);
		} catch (Exception e) {
			LOG.error("Error While Sending cancel Subscription mail :" + e.getMessage(), e);
		}
	}

	private void sendBuyerSubscriptionMail(String mailTo, String name, BuyerSubscription subscription, String msg, String subject, boolean isSuccess, PaymentTransaction paymentTransaction) {
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
			map.put("currencyCode", subscription != null ? subscription.getCurrencyCode() : "Not Available");
			map.put("amount", subscription != null ? subscription.getTotalPriceAmount() : "");
			if (isSuccess) {
				notificationService.sendEmail(mailTo, subject, map, Global.BUYER_SUBSCRIPTION);
			} else {
				map.put("errorCode", paymentTransaction != null ? paymentTransaction.getErrorCode() : "");
				map.put("errorMsg", paymentTransaction != null ? paymentTransaction.getErrorMessage() : "");
				notificationService.sendEmail(mailTo, subject, map, Global.BUYER_FAILURE_SUBSCRIPTION);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending Subscription mail :" + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public void doComputeSubscription(BuyerSubscription subscription, BuyerSubscription previousSubscription) {
		if (subscription.getPlan() == null) {
			return;
		}

		// SET default for start date
		Calendar newDate = Calendar.getInstance();
		newDate.set(Calendar.AM_PM, Calendar.AM);
		newDate.set(Calendar.HOUR, 0);
		newDate.set(Calendar.MINUTE, 0);
		newDate.set(Calendar.SECOND, 0);
		newDate.set(Calendar.MILLISECOND, 0);
		// Go to the end of subscription chain
		if (previousSubscription != null) {
			while (previousSubscription.getNextSubscription() != null && previousSubscription.getNextSubscription().getStartDate() != null) {
				previousSubscription = previousSubscription.getNextSubscription();
			}
		}
		if (previousSubscription != null) {
			// Set the start date as Today if previous subscription has expired
			if (previousSubscription.getSubscriptionStatus() == SubscriptionStatus.EXPIRED) {
				subscription.setStartDate(newDate.getTime());
				subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
				Buyer buyer = previousSubscription.getBuyer();
				if (buyer != null) {
					buyer.setCurrentSubscription(subscription);
					BuyerPackage bp = buyer.getBuyerPackage();
					bp.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
					buyer.setBuyerPackage(bp);
					buyerService.updateBuyer(buyer);
				}
			} else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(previousSubscription.getEndDate());
				cal.add(Calendar.DATE, 1);
				cal.set(Calendar.AM_PM, Calendar.AM);
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				Date perviousendDate = cal.getTime();
				subscription.setStartDate(perviousendDate);
				subscription.setSubscriptionStatus(SubscriptionStatus.FUTURE);
			}
		} else {
			subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
			subscription.setActivatedDate(new Date());
			subscription.setStartDate(newDate.getTime());
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(subscription.getStartDate());
		cal.set(Calendar.AM_PM, Calendar.PM);
		cal.set(Calendar.HOUR, 11);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 59);

		// set End Date based on plan type
		if (subscription.getPlanType() == PlanType.PER_USER) {
			cal.add(Calendar.MONTH, subscription.getPlanPeriod().getPlanDuration());
			cal.add(Calendar.DATE, -1);
			Date endDate = cal.getTime();
			LOG.info("Plan Type :" + subscription.getPlanType() + " Start date :" + subscription.getStartDate() + " duration period : " + subscription.getPlanPeriod().getPlanDuration() + " End Date :" + endDate);
			subscription.setEndDate(endDate);
		} else {
			if (subscription.getPlanPeriod() != null) {
				cal.add(Calendar.MONTH, subscription.getPlanPeriod().getPlanDuration());
				LOG.info("Plan Type :" + subscription.getPlanType() + " Start date :" + subscription.getStartDate() + " duration period : " + subscription.getPlanPeriod().getPlanDuration());
			} else {
				// Default set Time 3 year for Event based
				cal.add(Calendar.MONTH, 36);
			}
			cal.add(Calendar.DATE, -1);
			Date endDate = cal.getTime();
			LOG.info(" End Date :" + endDate);
			subscription.setEndDate(endDate);
		}
	}

	@Override
	public List<BuyerSubscription> findBuyerSubscriptionHistoryForBuyer(String tenantId, TableDataInput input) {
		List<BuyerSubscription> subscriptionList = buyerSubscriptionDao.findBuyerSubscriptionHistoryForBuyer(tenantId, input);
		if (CollectionUtil.isNotEmpty(subscriptionList)) {
			for (BuyerSubscription buyerSubscription : subscriptionList) {
				if (buyerSubscription.getPlan() != null) {
					buyerSubscription.getPlan().setCreatedBy(null);
					buyerSubscription.getPlan().setModifiedBy(null);
					buyerSubscription.getPlan().setRangeList(null);
					buyerSubscription.getPlan().setPlanPeriodList(null);
				}
			}
		}
		return subscriptionList;
	}

	@Override
	public long findTotalFilteredBuyerSubscriptionHistoryForBuyer(String tenantId, TableDataInput input) {
		return buyerSubscriptionDao.findTotalFilteredBuyerSubscriptionHistoryForBuyer(tenantId, input);
	}

	@Override
	public long findTotalBuyerSubscriptionHistoryForBuyer(String tenantId) {
		return buyerSubscriptionDao.findTotalBuyerSubscriptionHistoryForBuyer(tenantId);
	}

	@Override
	public BuyerSubscription getCurrentBuyerSubscriptionForBuyer(String tenantId) {
		BuyerSubscription subs = null;
		Buyer buyer = buyerService.findBuyerByIdWithBuyerPackage(tenantId);
		// BuyerSubscription subs = buyer.getCurrentSubscription();
		if (buyer.getCurrentSubscription() != null) {
			subs = buyerSubscriptionDao.getBuyerSubscriptionBySubscriptionId(buyer.getCurrentSubscription().getId());
			if (subs != null) {
				if (subs.getPlan() != null) {
					if (CollectionUtil.isNotEmpty(subs.getPlan().getRangeList())) {
						for (PlanRange range : subs.getPlan().getRangeList()) {
							range.getPrice();
						}
					}
					if (CollectionUtil.isNotEmpty(subs.getPlan().getPlanPeriodList())) {
						for (PlanPeriod period : subs.getPlan().getPlanPeriodList()) {
							period.getPlanDuration();
						}
					}
				}
				if (subs.getNextSubscription() != null) {
					BuyerSubscription nxt = subs.getNextSubscription();
					while (nxt != null) {
						if (nxt.getPlan() != null) {
							LOG.info("next plan :" + nxt.getPlan().getPlanName());
						}
						nxt.getActivatedDate();
						nxt.getPlan().getCurrency();
						if (CollectionUtil.isNotEmpty(nxt.getPlan().getRangeList())) {
							for (PlanRange range : nxt.getPlan().getRangeList()) {
								range.getPrice();
							}
						}
						if (CollectionUtil.isNotEmpty(nxt.getPlan().getPlanPeriodList())) {
							for (PlanPeriod period : nxt.getPlan().getPlanPeriodList()) {
								period.getPlanDuration();
							}
						}
						if (CollectionUtil.isNotEmpty(nxt.getPaymentTransactions())) {
							for (PaymentTransaction trans : nxt.getPaymentTransactions()) {
								trans.getAmount();
								if (trans.getPromoCode() != null) {
									trans.getPromoCode().getPromoCode();
								}
							}
						}
						nxt = nxt.getNextSubscription();

					}
				}
			}
		}
		return subs;
	}

	@Override
	public BuyerSubscription getLastBuyerSubscriptionForBuyer(String tenantId) {
		BuyerSubscription subs = buyerSubscriptionDao.getLastBuyerSubscriptionForBuyer(tenantId);
		if (subs != null) {
			if (subs.getPlan() != null) {
				if (CollectionUtil.isNotEmpty(subs.getPlan().getRangeList())) {
					for (PlanRange range : subs.getPlan().getRangeList()) {
						range.getPrice();
					}
				}
				if (CollectionUtil.isNotEmpty(subs.getPlan().getPlanPeriodList())) {
					for (PlanPeriod period : subs.getPlan().getPlanPeriodList()) {
						period.getPlanDuration();
					}
				}

				if (subs.getPlan().getCurrency() != null) {
					subs.getPlan().getCurrency().getCurrencyCode();
				}

			}
		}
		return subs;
	}

	@Override
	@Transactional(readOnly = false)
	public HashMap<String, String> confirmSubscriptionRenew(String token, String payerId, Model model, String serverName, String paymentTransactionId) throws Exception {

		String paymentType = "Sale";
		PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId);

		String nvpstr = "&TOKEN=" + token + "&PAYERID=" + payerId + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_AMT=" + paymentTransaction.getAmount();
		nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" + paymentTransaction.getCurrencyCode() + "&IPADDRESS=" + serverName;

		LOG.info("Invoking PayPal Confirm Payment ===================> ");
		/*
		 * Make the call to PayPal to finalize payment If an error occured, show the resulting errors
		 */
		HashMap<String, String> nvp = invokePaypalService("DoExpressCheckoutPayment", nvpstr);
		model.addAttribute("paypalResponse", nvp);

		BuyerSubscription subscription = null;
		String strAck = nvp.get("ACK").toString();
		try {
			subscription = getBuyerSubscriptionById(paymentTransaction.getBuyerSubscription().getId());
			if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {

				// Update the Payment Transaction Details
				LOG.info("Subscription Id : " + paymentTransaction.getBuyerSubscription().getId());
				paymentTransaction.setConfirmationDate(new Date());
				paymentTransaction.setStatus(TransactionStatus.SUCCESS);
				paymentTransaction.setReferenceTransactionId(nvp.get("PAYMENTINFO_0_TRANSACTIONID"));
				paymentTransaction.setPaymentFee(new BigDecimal(nvp.get("PAYMENTINFO_0_AMT")));
				paymentTransaction = updatePaymentTransaction(paymentTransaction);

				// Chain the Subscriptions to know which one is next.
				Buyer buyer = buyerService.findBuyerById(subscription.getBuyer().getId());
				BuyerSubscription currentChainEnd = buyer.getCurrentSubscription();
				if (currentChainEnd != null) {
					while (currentChainEnd.getNextSubscription() != null) {
						currentChainEnd = currentChainEnd.getNextSubscription();
					}
					currentChainEnd.setNextSubscription(subscription);
					updateSubscription(currentChainEnd);
				}

				// Update the subscription details
				if (subscription != null) {
					// Calculate Start/End Date etc...
					doComputeSubscription(subscription, buyer.getCurrentSubscription());

					BuyerPackage bp = buyer.getBuyerPackage();
					subscription = updateSubscription(subscription);
					bp.setEndDate(subscription.getEndDate());

					buyer.setBuyerPackage(bp);
					// buyer = buyerService.findBuyerById(user.getTenantId());
					LOG.info("Buyer Id : " + buyer.getId());
					buyer = buyerService.updateBuyer(buyer);
				}

				// Update transaction details with buyer info
				paymentTransaction.setBuyer(buyer);
				paymentTransaction = updatePaymentTransaction(paymentTransaction);
				if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
					promotionalCodeService.updatePromoCode(paymentTransaction.getPromoCode().getId());
				}

				// creating new recurring payment after change plan
				if (subscription != null && Boolean.TRUE == subscription.getAutoChargeSubscription()) {
					try {
						createRecurringPaymentsSubscription(token, subscription, paymentTransaction);
					} catch (Exception e1) {
						LOG.error("Error while recurring payment for '" + paymentTransactionId + "' transaction id :" + e1.getMessage(), e1);
					}
				}
			} else {
				paymentTransaction = markTransactionFailed(paymentTransaction, nvp);

				// sending mail to buyer on payment failure
				try {
					String msg = "Your payment transaction has failed during subscription";
					String subject = "Subscription Failure";
					sendBuyerSubscriptionMail(paymentTransaction.getCommunicationEmail(), paymentTransaction.getFullName(), subscription, msg, subject, false, paymentTransaction);
				} catch (Exception e) {
					LOG.error("Error While sending failure subscription mail to buyer :" + e.getMessage(), e);
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
		return nvp;
	}

	@Override
	@Transactional(readOnly = false)
	public String initiatePaypalPaymentForUpdateBuyerSubscription(BuyerSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL, PaymentTransaction paymentTransaction, int userEventQuantity) {
		BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
		if (plan == null) {
			return "redirect:/buyer/billing/accountOverview";
		}
		Buyer buyer = subscription.getBuyer();
		BigDecimal tax = BigDecimal.ZERO;
		if (tax == null) {
			tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			if (buyer.getRegistrationOfCountry() != null && "MY".equalsIgnoreCase(buyer.getRegistrationOfCountry().getCountryCode())) {
				tax = plan.getTax();
				tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}

		// The paymentAmount is the total value of the shopping cart
		BigDecimal paymentAmount = new BigDecimal(0);
		if (subscription.getTotalPriceAmount() != null) {
			paymentAmount = subscription.getTotalPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		LOG.info("paymentAmount : " + paymentAmount);
		// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
		String currencyCodeType = plan.getCurrency().getCurrencyCode();
		String paymentType = "Sale";

		// Create an instance of PaymentTransaction at the beginning of payment flow.
		if (CollectionUtil.isNotEmpty(subscription.getPaymentTransactions())) {
			if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(paymentTransaction.getPromoCode().getId());
				paymentTransaction.setPromoCode(promotionalCode);
			}
			paymentTransaction.setBuyerPlan(plan);
			paymentTransaction.setCreatedDate(new Date());
			// Convert currency if user select Malaysia country
			// if (paymentTransaction.getCountry() != null &&
			// paymentTransaction.getCountry().getCountryCode().equalsIgnoreCase("MY")) {
			// currencyCodeType = "MYR"; // paymentTransaction.getCountry().getCountryCode();
			// try {
			// // call currency change Api here
			// paymentAmount = convertUsdToMyr(paymentAmount, paymentTransaction);
			// if (paymentAmount != null) {
			// paymentAmount = paymentAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
			// }
			// BigDecimal priceAmount = convertUsdToMyr(subscription.getPriceAmount(), null);
			// if (priceAmount != null) {
			// subscription.setPriceAmount(priceAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal priceDiscount = convertUsdToMyr(subscription.getPriceDiscount(), null);
			// if (priceDiscount != null) {
			// subscription.setPriceDiscount(priceDiscount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal promoCodeDiscount = convertUsdToMyr(subscription.getPromoCodeDiscount(), null);
			// if (promoCodeDiscount != null) {
			// subscription.setPromoCodeDiscount(promoCodeDiscount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal totalPriceAmount = convertUsdToMyr(subscription.getTotalPriceAmount(), null);
			// if (totalPriceAmount != null) {
			// subscription.setTotalPriceAmount(totalPriceAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// } catch (Exception e) {
			// LOG.error("Error while calling currency change Api :" + e.getMessage(), e);
			// }
			// }
			LOG.info("currencyCodeType " + currencyCodeType);
			paymentTransaction.setCurrencyCode(currencyCodeType);
			paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
			paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
			paymentTransaction.setType(TransactionType.PAYMENT);
			paymentTransaction.setAmount(paymentAmount);
			paymentTransaction = savePaymentTransaction(paymentTransaction);
		}
		// The returnURL is the location where buyers return to when a payment has been succesfully authorized.
		returnURL += paymentTransaction.getId();

		// The cancelURL is the location buyers are sent to when they hit the cancel button during authorization of
		// payment during the PayPal flow
		cancelURL += paymentTransaction.getId();

		String note = "You are subscribing for ";
		String paymentRemarks = "Payment for ";
		int quantity = 1;

		String gstDescription = "";
		String gstDesc = "";
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		if (tax != null && tax.floatValue() > 0) {
			gstDescription = " (Inclusive of " + tax + "% Tax)";
			gstDesc = " (Inclusive of " + tax + "%25 Tax)";
		}
		// }

		if (plan.getPlanType() == PlanType.PER_USER) {
			note += userEventQuantity + " users for " + subscription.getPlanPeriod().getPlanDuration() + " months " + gstDesc;
			paymentRemarks += userEventQuantity + " users for " + subscription.getPlanPeriod().getPlanDuration() + " months " + gstDescription;
			quantity = subscription.getPlanPeriod().getPlanDuration();
		} else {
			note += subscription.getEventQuantity() + " events for unlimited users " + gstDesc;
			paymentRemarks += userEventQuantity + " events for unlimited users " + gstDescription;
		}

		// 6 % GST FOR MALAYSIA
		BigDecimal totalTax = new BigDecimal(0);
		BigDecimal itemTax = new BigDecimal(0);
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		totalTax = (paymentAmount.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
		// itemTax = (totalTax.divide(new BigDecimal(quantity), 2, RoundingMode.HALF_UP));
		// itemTax = itemTax.setScale(2, BigDecimal.ROUND_HALF_UP);
		// totalTax = itemTax.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
		// }
		LOG.info("totalTax :" + totalTax);

		// Construct the parameter string that describes the PayPal payment.
		String nvpstr = "&PAYMENTREQUEST_0_AMT=" + paymentAmount.add(totalTax) + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType + "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan.";

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
		nvpstr += "&EMAIL=" + paymentTransaction.getCommunicationEmail();

		// Landing Page - Type of PayPal page to display

		nvpstr += "&LANDINGPAGE=Billing"; // or LANDINGPAGE=Login

		// Note to Buyer
		nvpstr += "&NOTETOBUYER=" + note;

		/**
		 * Item Details
		 */
		// Item Amount
		nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + paymentAmount;
		LOG.info("paymentAmount :" + paymentAmount);

		// Total Tax
		nvpstr += "&PAYMENTREQUEST_0_TAXAMT=" + totalTax;
		// Total Shipping
		nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0";
		int item = 0;
		BigDecimal rangePrice = subscription.getRange().getPrice();
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		// rangePrice = convertUsdToMyr(subscription.getRange().getPrice(), null);
		// } else {
		rangePrice = rangePrice.setScale(2, BigDecimal.ROUND_HALF_UP);
		// }

		// Item Name
		nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + plan.getPlanName();
		// Item Description
		nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + plan.getShortDescription();

		// BigDecimal itemAmount = (subscription.getRange().getPrice().setScale(2,
		// BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP);
		// BigDecimal itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2,
		// BigDecimal.ROUND_HALF_UP);

		BigDecimal itemAmount = BigDecimal.ZERO;
		if (plan.getBasePrice() != null) {
			// itemAmount = plan.getBasePrice().setScale(2, BigDecimal.ROUND_HALF_UP);
			// if (currencyCodeType.equalsIgnoreCase("MYR")) {
			// itemAmount = convertUsdToMyr(plan.getBasePrice(), null).setScale(2, BigDecimal.ROUND_HALF_UP);
			// }
			LOG.info("userEventQuantity :" + userEventQuantity);
			// if (plan.getBaseUsers() < subscription.getUserQuantity()) {
			// userEventQuantity = subscription.getUserQuantity() - plan.getBaseUsers();
			LOG.info("userEventQuantity: " + userEventQuantity + " == subscription.getUserQuantity(): " + subscription.getUserQuantity() + " == rangePrice: " + rangePrice + " == itemAmount: " + itemAmount);
			// itemAmount = itemAmount.add(((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2,
			// BigDecimal.ROUND_HALF_UP));
			// }

			itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP);
			LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);
		} else {
			itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);

		// Item Amount
		nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + itemAmount.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP);

		// Item Qty
		nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";

		// Item Tax
		nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=" + totalTax;
		LOG.info("tax on item :" + totalTax);

		item++;
		if (subscription.getPlanPeriod() != null && subscription.getPriceDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + "Subscription Discount";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + subscription.getPlanPeriod().getPlanDiscount() + " %25";
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + (subscription.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("subscription discount : " + subscription.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());

			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";

			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=0";
			item++;
		}

		if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + "Promo Discount ";
			String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? " OFF" : "%25 OFF";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + paymentTransaction.getPromoCode().getPromoName() + "-" + paymentTransaction.getPromoCode().getPromoDiscount() + promoDesc;
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + (subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("promo discount : " + subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";
			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=0";
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
		LOG.info("Payment gateway response : " + nvp.get("ACK"));

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {
			session.setAttribute(BuyerSubscriptionServiceImpl.TOKEN, nvp.get(BuyerSubscriptionServiceImpl.TOKEN).toString());
			LOG.info(" TOKEN : " + nvp.get(BuyerSubscriptionServiceImpl.TOKEN));

			subscription.setPriceAmount(((itemAmount).multiply(new BigDecimal(quantity))).setScale(2, BigDecimal.ROUND_HALF_UP));
			subscription.setTotalPriceAmount(paymentAmount.add(totalTax));
			// subscription = updateSubscription(subscription);

			// Save the TOKEN in DB
			paymentTransaction.setBuyerSubscription(subscription);
			paymentTransaction.setPaymentToken(nvp.get(BuyerSubscriptionServiceImpl.TOKEN));
			paymentTransaction.setRemarks(paymentRemarks);
			paymentTransaction.setPriceAmount(subscription.getPriceAmount());
			paymentTransaction.setPriceDiscount(subscription.getPriceDiscount());
			paymentTransaction.setPromoCodeDiscount(subscription.getPromoCodeDiscount());
			paymentTransaction.setAdditionalTaxDesc("Tax " + tax + "%");
			paymentTransaction.setAdditionalTax(totalTax);
			paymentTransaction.setAmount(subscription.getTotalPriceAmount());
			paymentTransaction.setTotalPriceAmount(subscription.getTotalPriceAmount());
			// paymentTransaction = updatePaymentTransaction(paymentTransaction);

			session.setAttribute("subscription", subscription);
			session.setAttribute("paymentTransaction", paymentTransaction);
			session.setAttribute("subscriptionId", subscription.getId());
			session.setAttribute("paymentTransactionId", paymentTransaction.getId());
			session.setAttribute("buyer", subscription.getBuyer());

			String payPalURL = PAYPAL_URL + nvp.get(BuyerSubscriptionServiceImpl.TOKEN);
			return "redirect:" + payPalURL;

		} else {
			paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
		}

		return "redirect:../subscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public HashMap<String, String> confirmSubscriptionUpdate(String token, String payerId, Model model, String serverName, String paymentTransactionId, BuyerSubscription subscription, PaymentTransaction paymentTransaction) throws Exception {

		String paymentType = "Sale";
		// PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId);

		String nvpstr = "&TOKEN=" + token + "&PAYERID=" + payerId + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_AMT=" + paymentTransaction.getAmount();
		nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" + paymentTransaction.getCurrencyCode() + "&IPADDRESS=" + serverName;

		LOG.info("Invoking PayPal Confirm Payment ===================> ");
		/*
		 * Make the call to PayPal to finalize payment If an error occured, show the resulting errors
		 */
		HashMap<String, String> nvp = invokePaypalService("DoExpressCheckoutPayment", nvpstr);
		model.addAttribute("paypalResponse", nvp);

		String strAck = nvp.get("ACK").toString();
		try {
			// subscription = getBuyerSubscriptionById(paymentTransaction.getBuyerSubscription().getId());
			if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {

				// Update the Payment Transaction Details
				LOG.info("Subscription Id : " + subscription.getId());
				paymentTransaction.setConfirmationDate(new Date());
				paymentTransaction.setStatus(TransactionStatus.SUCCESS);
				paymentTransaction.setReferenceTransactionId(nvp.get("PAYMENTINFO_0_TRANSACTIONID"));
				paymentTransaction.setPaymentFee(new BigDecimal(nvp.get("PAYMENTINFO_0_AMT")));
				paymentTransaction = updatePaymentTransaction(paymentTransaction);

				Buyer buyer = buyerService.findBuyerById(subscription.getBuyer().getId());

				// Update the subscription details
				if (subscription != null && buyer.getCurrentSubscription() != null && subscription.getId().equals(buyer.getCurrentSubscription().getId())) {

					BuyerPackage bp = buyer.getBuyerPackage();
					bp.setEventLimit(subscription.getEventQuantity());
					bp.setUserLimit(subscription.getUserQuantity());
					buyer.setBuyerPackage(bp);
					LOG.info("Buyer Id : " + buyer.getId());
					buyer = buyerService.updateBuyer(buyer);
				}

				// Update transaction details with buyer info
				paymentTransaction.setBuyer(buyer);
				subscription = updateSubscription(subscription);
				paymentTransaction = updatePaymentTransaction(paymentTransaction);
				if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
					promotionalCodeService.updatePromoCode(paymentTransaction.getPromoCode().getId());
				}

				// cancel recurring payment on change plan
				if (subscription != null && Boolean.TRUE == subscription.getAutoChargeSubscription()) {
					try {
						cancelRecurringPaymentsSubscription(token, subscription, paymentTransaction);
					} catch (Exception e1) {
						LOG.error("Error while cancel recurring payment for '" + buyer.getLoginEmail() + "' login email and '" + subscription.getPaymentProfileId() + "' ProfileId id :" + e1.getMessage(), e1);
					}
				}

			} else {
				paymentTransaction = markTransactionFailed(paymentTransaction, nvp);

				// sending mail to buyer on payment failure
				try {
					String msg = "Your payment transaction has failed during subscription";
					String subject = "Subscription Failure";
					sendBuyerSubscriptionMail(paymentTransaction.getCommunicationEmail(), paymentTransaction.getFullName(), subscription, msg, subject, false, paymentTransaction);
				} catch (Exception e) {
					LOG.error("Error While sending failure subscription mail to buyer :" + e.getMessage(), e);
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
		return nvp;
	}

	@Override
	@Transactional(readOnly = false)
	public String initiatePaypalPaymentForBuyerSubscriptionChangePlan(BuyerSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL) {
		BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
		if (plan == null) {
			LOG.error("Plan can't be null");
			return "redirect:/buyer/billing/accountOverview";
		}

		Buyer buyer = subscription.getBuyer();
		BigDecimal tax = BigDecimal.ZERO;
		if (tax == null) {
			tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			if (buyer.getRegistrationOfCountry() != null && "MY".equalsIgnoreCase(buyer.getRegistrationOfCountry().getCountryCode())) {
				tax = plan.getTax();
				tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}

		// The paymentAmount is the total value of the shopping cart
		BigDecimal paymentAmount = new BigDecimal(0);
		if (subscription.getTotalPriceAmount() != null) {
			paymentAmount = subscription.getTotalPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		LOG.info("paymentAmount : " + paymentAmount);
		// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
		String currencyCodeType = plan.getCurrency().getCurrencyCode();
		String paymentType = "Sale";

		// Create an instance of PaymentTransaction at the beginning of payment flow.
		PaymentTransaction paymentTransaction = null;
		if (CollectionUtil.isNotEmpty(subscription.getPaymentTransactions())) {
			paymentTransaction = subscription.getPaymentTransactions().get(0);
			if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(paymentTransaction.getPromoCode().getId());
				paymentTransaction.setPromoCode(promotionalCode);
			}
			paymentTransaction.setBuyerPlan(plan);
			paymentTransaction.setCreatedDate(new Date());
			// Convert currency if user select Malaysia country
			// if (paymentTransaction.getCountry() != null &&
			// paymentTransaction.getCountry().getCountryCode().equalsIgnoreCase("MY")) {
			// currencyCodeType = "MYR"; // paymentTransaction.getCountry().getCountryCode();
			// try {
			// // call currency change Api here
			// paymentAmount = convertUsdToMyr(paymentAmount, paymentTransaction);
			// if (paymentAmount != null) {
			// paymentAmount = paymentAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
			// }
			// BigDecimal priceAmount = convertUsdToMyr(subscription.getPriceAmount(), null);
			// if (priceAmount != null) {
			// subscription.setPriceAmount(priceAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal priceDiscount = convertUsdToMyr(subscription.getPriceDiscount(), null);
			// if (priceDiscount != null) {
			// subscription.setPriceDiscount(priceDiscount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal promoCodeDiscount = convertUsdToMyr(subscription.getPromoCodeDiscount(), null);
			// if (promoCodeDiscount != null) {
			// subscription.setPromoCodeDiscount(promoCodeDiscount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal totalPriceAmount = convertUsdToMyr(subscription.getTotalPriceAmount(), null);
			// if (totalPriceAmount != null) {
			// subscription.setTotalPriceAmount(totalPriceAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// } catch (Exception e) {
			// LOG.error("Error while calling currency change Api :" + e.getMessage(), e);
			// }
			// }
			LOG.info("currencyCodeType " + currencyCodeType);

			paymentTransaction.setCurrencyCode(currencyCodeType);
			paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
			paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
			paymentTransaction.setType(TransactionType.PAYMENT);
			paymentTransaction.setAmount(paymentAmount);
			paymentTransaction.setPriceAmount(subscription.getPriceAmount());
			paymentTransaction.setPriceDiscount(subscription.getPriceDiscount());
			paymentTransaction.setPromoCodeDiscount(subscription.getPromoCodeDiscount());
			paymentTransaction.setTotalPriceAmount(subscription.getTotalPriceAmount());
			paymentTransaction = savePaymentTransaction(paymentTransaction);
		}
		// The returnURL is the location where buyers return to when a payment has been succesfully authorized.
		returnURL += paymentTransaction.getId();

		// The cancelURL is the location buyers are sent to when they hit the cancel button during authorization of
		// payment during the PayPal flow
		cancelURL += paymentTransaction.getId();

		String note = "You are subscribing for ";
		String paymentRemarks = "Payment for ";
		int userEventQuantity = 0;
		int quantity = 1;

		String gstDescription = "";
		String gstDesc = "";
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		if (tax != null && tax.floatValue() > 0) {
			gstDescription = " (Inclusive of " + tax + "% Tax)";
			gstDesc = " (Inclusive of " + tax + "%25 Tax)";
		}
		// }

		if (plan.getPlanType() == PlanType.PER_USER) {

			if (plan.getBasePrice() != null && plan.getBaseUsers() > subscription.getUserQuantity()) {
				LOG.info("UserQuantity: " + userEventQuantity + "=====Base Users: " + plan.getBaseUsers());
				subscription.setUserQuantity(plan.getBaseUsers());
			}

			note += subscription.getUserQuantity() + " users for " + subscription.getPlanPeriod().getPlanDuration() + " months " + gstDesc;
			paymentRemarks += subscription.getUserQuantity() + " users for " + subscription.getPlanPeriod().getPlanDuration() + " months " + gstDescription;
			userEventQuantity = subscription.getUserQuantity();
			quantity = subscription.getPlanPeriod().getPlanDuration();
		} else {
			note += subscription.getEventQuantity() + " events for unlimited users " + gstDesc;
			paymentRemarks += subscription.getEventQuantity() + " events for unlimited users " + gstDescription;
			userEventQuantity = subscription.getEventQuantity();
		}

		// 6 % GST FOR MALAYSIA
		BigDecimal totalTax = new BigDecimal(0);
		BigDecimal itemTax = new BigDecimal(0);
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		totalTax = (paymentAmount.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
		// itemTax = (totalTax.divide(new BigDecimal(quantity), 2, RoundingMode.HALF_UP));
		// itemTax = itemTax.setScale(2, BigDecimal.ROUND_HALF_UP);
		// totalTax = itemTax.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
		// }
		LOG.info("totalTax :" + totalTax);

		// Construct the parameter string that describes the PayPal payment.
		String nvpstr = "&PAYMENTREQUEST_0_AMT=" + paymentAmount.add(totalTax) + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType + "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan.";

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
		nvpstr += "&EMAIL=" + paymentTransaction.getCommunicationEmail();

		// Landing Page - Type of PayPal page to display

		nvpstr += "&LANDINGPAGE=Billing"; // or LANDINGPAGE=Login

		/**
		 * Recurring Payments
		 */
		if (Boolean.TRUE == subscription.getAutoChargeSubscription()) {
			// Billing type
			nvpstr += "&L_BILLINGTYPE0=RecurringPayments";
			// Billing Agreement Desc
			nvpstr += "&L_BILLINGAGREEMENTDESCRIPTION0=" + "Payment for " + plan.getPlanName() + " plan at procurehere.com ";
		}

		// Note to Buyer
		nvpstr += "&NOTETOBUYER=" + note;

		/**
		 * Item Details
		 */
		// Item Amount
		nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + paymentAmount;
		LOG.info("paymentAmount :" + paymentAmount);

		// Total Tax
		nvpstr += "&PAYMENTREQUEST_0_TAXAMT=" + totalTax;
		;
		// Total Shipping
		nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0";
		int item = 0;
		// Item Name
		nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + plan.getPlanName();
		// Item Description
		nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + plan.getShortDescription();

		BigDecimal rangePrice = subscription.getRange().getPrice();
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		// rangePrice = convertUsdToMyr(subscription.getRange().getPrice(), null);
		// } else {
		rangePrice = rangePrice.setScale(2, BigDecimal.ROUND_HALF_UP);
		// }

		// BigDecimal itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2,
		// BigDecimal.ROUND_HALF_UP);

		BigDecimal itemAmount = BigDecimal.ZERO;
		if (plan.getBasePrice() != null) {
			itemAmount = plan.getBasePrice().setScale(2, BigDecimal.ROUND_HALF_UP);
			// if (currencyCodeType.equalsIgnoreCase("MYR")) {
			// itemAmount = convertUsdToMyr(plan.getBasePrice(), null).setScale(2, BigDecimal.ROUND_HALF_UP);
			// }

			if (plan.getBaseUsers() < userEventQuantity) {
				userEventQuantity = userEventQuantity - plan.getBaseUsers();
				itemAmount = itemAmount.add(((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);
		} else {
			itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);

		// Item Amount
		nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + itemAmount.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP);

		// Item Qty
		nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";

		// Item Tax
		nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=" + totalTax;
		LOG.info("tax on individual totalTax :" + totalTax);

		item++;
		if (subscription.getPlanPeriod() != null && subscription.getPriceDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + "Subscription Discount";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + subscription.getPlanPeriod().getPlanDiscount() + " %25";
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + (subscription.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("subscription discount : " + subscription.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());

			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";

			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=0";
			item++;
		}
		if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + "Promo Discount ";
			String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? " OFF" : "%25 OFF";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + paymentTransaction.getPromoCode().getPromoName() + "-" + paymentTransaction.getPromoCode().getPromoDiscount() + promoDesc;
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + (subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("promo discount : " + subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";
			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=0";
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
		LOG.info("Payment gateway response : " + nvp.get("ACK"));

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {
			session.setAttribute(BuyerSubscriptionServiceImpl.TOKEN, nvp.get(BuyerSubscriptionServiceImpl.TOKEN).toString());
			LOG.info(" TOKEN : " + nvp.get(BuyerSubscriptionServiceImpl.TOKEN));

			// We dont need these for saving the subscription
			subscription.setBuyer(null);

			subscription.setPlan(plan);
			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);

			subscription.setPriceAmount(((itemAmount).multiply(new BigDecimal(quantity))).setScale(2, BigDecimal.ROUND_HALF_UP));
			subscription.setTotalPriceAmount(paymentAmount.add(totalTax));
			subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
			subscription = saveBuyerSubscription(subscription);

			// Save the TOKEN in DB
			paymentTransaction.setBuyerSubscription(subscription);
			paymentTransaction.setPaymentToken(nvp.get(BuyerSubscriptionServiceImpl.TOKEN));
			paymentTransaction.setRemarks(paymentRemarks);
			// if (currencyCodeType.equalsIgnoreCase("MYR")) {
			paymentTransaction.setAdditionalTaxDesc("Tax " + tax + "%");
			paymentTransaction.setAdditionalTax(totalTax);
			// }
			paymentTransaction.setPriceAmount(subscription.getPriceAmount());
			paymentTransaction.setAmount(subscription.getTotalPriceAmount());
			paymentTransaction.setTotalPriceAmount(subscription.getTotalPriceAmount());
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			// Reassign the values
			subscription.setBuyer(buyer);

			session.setAttribute("subscriptionId", subscription.getId());
			session.setAttribute("paymentTransactionId", paymentTransaction.getId());
			session.setAttribute("buyer", buyer);

			String payPalURL = PAYPAL_URL + nvp.get(BuyerSubscriptionServiceImpl.TOKEN);
			LOG.info("payPalURL: " + payPalURL);
			return "redirect:" + payPalURL;

		} else {
			paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
		}

		return "redirect:../subscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
	}

	@Override
	@Transactional(readOnly = false)
	public HashMap<String, String> confirmSubscriptionChangePlan(String token, String payerId, Model model, String serverName, String paymentTransactionId) throws Exception {

		String paymentType = "Sale";
		PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId);

		String nvpstr = "&TOKEN=" + token + "&PAYERID=" + payerId + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_AMT=" + paymentTransaction.getAmount();
		nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" + paymentTransaction.getCurrencyCode() + "&IPADDRESS=" + serverName;

		LOG.info("Invoking PayPal Confirm Payment ===================> ");
		/*
		 * Make the call to PayPal to finalize payment If an error occured, show the resulting errors
		 */
		HashMap<String, String> nvp = invokePaypalService("DoExpressCheckoutPayment", nvpstr);
		model.addAttribute("paypalResponse", nvp);

		BuyerSubscription subscription = null;
		String strAck = nvp.get("ACK").toString();
		try {
			subscription = getBuyerSubscriptionById(paymentTransaction.getBuyerSubscription().getId());
			if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {

				// Update the Payment Transaction Details
				LOG.info("Subscription Id : " + paymentTransaction.getBuyerSubscription().getId());
				paymentTransaction.setConfirmationDate(new Date());
				paymentTransaction.setStatus(TransactionStatus.SUCCESS);
				paymentTransaction.setReferenceTransactionId(nvp.get("PAYMENTINFO_0_TRANSACTIONID"));
				paymentTransaction.setPaymentFee(new BigDecimal(nvp.get("PAYMENTINFO_0_AMT")));
				paymentTransaction = updatePaymentTransaction(paymentTransaction);

				// Chain the Subscriptions to know which one is next.
				Buyer buyer = buyerService.findBuyerById(subscription.getBuyer().getId());
				if (Boolean.TRUE != subscription.getImmediateEffect()) {
					BuyerSubscription currentChainEnd = buyer.getCurrentSubscription();
					if (currentChainEnd != null) {
						while (currentChainEnd.getNextSubscription() != null) {
							currentChainEnd = currentChainEnd.getNextSubscription();
						}
						currentChainEnd.setNextSubscription(subscription);
						updateSubscription(currentChainEnd);
					}
				}
				BuyerSubscription currentSubs = buyer.getCurrentSubscription();
				// Update the subscription details
				if (subscription != null) {

					// Calculate Start/End Date etc...
					if (Boolean.TRUE == subscription.getImmediateEffect()) {
						currentSubs.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
						updateSubscription(currentSubs);
						doComputeSubscriptionChangePlan(subscription, buyer.getCurrentSubscription());
						if (subscription.getPlanType() == PlanType.PER_USER) {
							// Check Active users for buyer
							long activeUsers = userDao.findTotalRegisteredOrActiveUserForTenant(buyer.getId(), true);
							if (activeUsers > subscription.getUserQuantity()) {
								// Deactivate all users except Admin buyer
								userDao.deactivateAllUsersExceptAdminUser(buyer);
							}
						}
					} else {
						doComputeSubscription(subscription, buyer.getCurrentSubscription());
					}
					if (SubscriptionStatus.EXPIRED == currentSubs.getSubscriptionStatus() && PlanType.PER_USER == currentSubs.getPlanType() && subscription.getPlanType() == PlanType.PER_EVENT) {
						// get the ACL matching %PR....
						List<AccessRights> prAcl = accessRightsDao.getPrAccessControlListForBuyer();
						// get all roles for the tenant id
						List<UserRole> userRoleList = userRoleDao.findAllUserRolesForTenant(buyer.getId());
						if (CollectionUtil.isNotEmpty(userRoleList) && CollectionUtil.isNotEmpty(prAcl))
							for (UserRole role : userRoleList) {
								role.getAccessControlList().removeAll(prAcl);
								userRoleDao.update(role);
							}
					}

					BuyerPackage bp = buyer.getBuyerPackage();
					subscription = updateSubscription(subscription);
					bp.setEndDate(subscription.getEndDate());

					buyer.setBuyerPackage(bp);
					// buyer = buyerService.findBuyerById(user.getTenantId());
					LOG.info("Buyer Id : " + buyer.getId());
					buyer = buyerService.updateBuyer(buyer);
				}

				// Update transaction details with buyer info
				paymentTransaction.setBuyer(buyer);
				paymentTransaction = updatePaymentTransaction(paymentTransaction);
				if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
					promotionalCodeService.updatePromoCode(paymentTransaction.getPromoCode().getId());
				}

				// first cancel recurring payment
				if (currentSubs != null && Boolean.TRUE == currentSubs.getAutoChargeSubscription()) {
					try {
						cancelRecurringPaymentsSubscription(token, currentSubs, paymentTransaction);
					} catch (Exception e1) {
						LOG.error("Error while cancel recurring payment for '" + buyer.getLoginEmail() + "' login email and '" + currentSubs.getPaymentProfileId() + "' ProfileId id :" + e1.getMessage(), e1);
					}
				}

				// creating new recurring payment after change plan
				if (subscription != null && Boolean.TRUE == subscription.getAutoChargeSubscription()) {
					try {
						createRecurringPaymentsSubscription(token, subscription, paymentTransaction);
					} catch (Exception e1) {
						LOG.error("Error while recurring payment for '" + paymentTransactionId + "' transaction id :" + e1.getMessage(), e1);
					}
				}

			} else {
				paymentTransaction = markTransactionFailed(paymentTransaction, nvp);

				// sending mail to buyer on payment failure
				try {
					String msg = "Your payment transaction has failed during subscription";
					String subject = "Subscription Failure";
					sendBuyerSubscriptionMail(paymentTransaction.getCommunicationEmail(), paymentTransaction.getFullName(), subscription, msg, subject, false, paymentTransaction);
				} catch (Exception e) {
					LOG.error("Error While sending failure subscription mail to buyer :" + e.getMessage(), e);
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
		return nvp;
	}

	@Override
	@Transactional(readOnly = true)
	public void doComputeSubscriptionChangePlan(BuyerSubscription subscription, BuyerSubscription currentSubscription) {
		if (subscription.getPlan() == null) {
			return;
		}
		subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

		// SET default for start date
		Calendar newDate = Calendar.getInstance();
		newDate.set(Calendar.AM_PM, Calendar.AM);
		newDate.set(Calendar.HOUR, 0);
		newDate.set(Calendar.MINUTE, 0);
		newDate.set(Calendar.SECOND, 0);
		newDate.set(Calendar.MILLISECOND, 0);

		subscription.setActivatedDate(new Date());
		subscription.setStartDate(newDate.getTime());

		Buyer buyer = subscription.getBuyer();
		if (buyer != null) {
			buyer.setCurrentSubscription(subscription);
			BuyerPackage bp = buyer.getBuyerPackage();
			bp.setPlan(subscription.getPlan());
			bp.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
			bp.setEventLimit(subscription.getEventQuantity());
			bp.setUserLimit(subscription.getUserQuantity());
			bp.setNoOfUsers(1);
			bp.setNoOfEvents(0);
			buyer.setBuyerPackage(bp);
			buyer.setPlan(subscription.getPlan());
			buyerService.updateBuyer(buyer);
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(subscription.getStartDate());
		cal.set(Calendar.AM_PM, Calendar.PM);
		cal.set(Calendar.HOUR, 11);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 59);

		// set End Date based on plan type
		if (subscription.getPlanType() == PlanType.PER_USER) {
			cal.add(Calendar.MONTH, subscription.getPlanPeriod().getPlanDuration());
			Date endDate = cal.getTime();
			LOG.info("Plan Type :" + subscription.getPlanType() + " Start date :" + subscription.getStartDate() + " duration period : " + subscription.getPlanPeriod().getPlanDuration() + " End Date :" + endDate);
			subscription.setEndDate(endDate);
		} else {
			if (subscription.getPlanPeriod() != null) {
				cal.add(Calendar.MONTH, subscription.getPlanPeriod().getPlanDuration());
				LOG.info("Plan Type :" + subscription.getPlanType() + " Start date :" + subscription.getStartDate() + " duration period : " + subscription.getPlanPeriod().getPlanDuration());
			} else {
				// Default set Time 3 year for Event based
				cal.add(Calendar.MONTH, 36);
			}
			Date endDate = cal.getTime();
			LOG.info(" End Date :" + endDate);
			subscription.setEndDate(endDate);
		}
	}

	@Override
	public void createRecurringPaymentsSubscription(String token, BuyerSubscription subscription, PaymentTransaction paymentTransaction) {

		// PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId);

		String nvpstr = "&TOKEN=" + token;
		SimpleDateFormat profileStartDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00+08:00");

		// setting date 7 days before the end date of subscription
		Calendar cal = Calendar.getInstance();
		cal.setTime(subscription.getEndDate());
		cal.add(Calendar.DATE, -7);

		nvpstr += "&PROFILESTARTDATE=" + profileStartDate.format(cal.getTime()).replaceAll(" ", "T");
		// nvpstr += "&PROFILESTARTDATE=" + profileStartDate.format(new Date()).replaceAll(" ", "T");

		// Desc should be same as the BILLING AGREEMENT DESCRIPTION sent for Set Express Checkout
		nvpstr += "&DESC=" + "Payment for " + paymentTransaction.getBuyerPlan().getPlanName() + " plan at procurehere.com ";
		// nvpstr += "&BILLINGPERIOD=" + "Day";

		nvpstr += "&BILLINGPERIOD=" + "Month";
		int billingFrequency = 1;
		if (paymentTransaction.getBuyerSubscription() != null && paymentTransaction.getBuyerSubscription().getPlanPeriod() != null) {
			billingFrequency = paymentTransaction.getBuyerSubscription().getPlanPeriod().getPlanDuration();
		}
		nvpstr += "&BILLINGFREQUENCY=" + billingFrequency;
		nvpstr += "&AMT=" + paymentTransaction.getAmount();
		nvpstr += "&CURRENCYCODE=" + paymentTransaction.getCurrencyCode();
		nvpstr += "&EMAIL=" + paymentTransaction.getCommunicationEmail();
		nvpstr += "&L_PAYMENTREQUEST_0_ITEMCATEGORY0=Physical";
		nvpstr += "&L_PAYMENTREQUEST_0_NAME0=" + paymentTransaction.getBuyerPlan().getPlanName();
		nvpstr += "&L_PAYMENTREQUEST_0_AMT0=" + paymentTransaction.getAmount();
		nvpstr += "&L_PAYMENTREQUEST_0_QTY0=1";
		LOG.info("Invoking PayPal Create Recurring Payments Profile Payment ===================> ");
		LOG.info("Create Recurring Payments nvp ===================> " + nvpstr);
		/*
		 * Make the call to PayPal to finalize payment If an error occured, show the resulting errors
		 */
		HashMap<String, String> nvp = invokePaypalService("CreateRecurringPaymentsProfile", nvpstr);

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {
			if (paymentTransaction.getBuyerSubscription() != null) {
				BuyerSubscription subs = paymentTransaction.getBuyerSubscription();
				subs.setPaymentProfileId(nvp.get("PROFILEID"));
				updateSubscription(subs);
			}
		} else {
			LOG.error("Error while recurring payment for :" + nvp.get("ACK").toString() + "==L_LONGMESSAGE0 = " + nvp.get("L_LONGMESSAGE0") + "==L_ERRORCODE0 = " + nvp.get("L_ERRORCODE0"));
		}
	}

	public void cancelRecurringPaymentsSubscription(String token, BuyerSubscription previousSubscription, PaymentTransaction paymentTransaction) {

		if (StringUtils.checkString(previousSubscription.getPaymentProfileId()).length() > 0) {
			String nvpstr = "&TOKEN=" + token;
			nvpstr += "&PROFILEID=" + previousSubscription.getPaymentProfileId();
			nvpstr += "&ACTION=Cancel";
			LOG.info("Invoking PayPal cancel Recurring Payments Profile Payment ===================> ");
			LOG.info("cancel Recurring Payments nvp ===================> " + nvpstr);
			/*
			 * Make the call to PayPal to finalize payment If an error occured, show the resulting errors
			 */
			HashMap<String, String> nvp = invokePaypalService("ManageRecurringPaymentsProfileStatus", nvpstr);
			String strAck = nvp.get("ACK").toString();
			if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {

				// write update cancel logic here
				previousSubscription.setRecurringCancelDate(new Date());
				previousSubscription.setRecurringCancelRemarks("Profile '" + previousSubscription.getPaymentProfileId() + "' cancel due to change in plan.");
				previousSubscription.setAutoChargeSubscription(false);
				updateSubscription(previousSubscription);
				LOG.info("Recurring payment successfully canceled for profile ID :" + previousSubscription.getPaymentProfileId());
			} else {
				LOG.error("Error while cancel recurring payment for :" + nvp.get("ACK").toString() + "==L_LONGMESSAGE0 = " + nvp.get("L_LONGMESSAGE0") + "==L_ERRORCODE0 = " + nvp.get("L_ERRORCODE0"));
			}
		} else {
			LOG.error("Something went wrong Profile id is null for subscription :" + previousSubscription.getId());
		}
	}

	@Override
	public BuyerSubscription getActiveBuyerSubscriptionByPaymentProfileId(String profileId) {
		return buyerSubscriptionDao.getActiveBuyerSubscriptionByPaymentProfileId(profileId);
	}

	@Override
	@Transactional(readOnly = false)
	public PaymentTransaction errorPaymentTransaction(String txId) {
		PaymentTransaction paymentTransaction = getPaymentTransactionById(txId);

		try {
			sendBuyerSubscriptionErroMail(paymentTransaction);
		} catch (Exception e) {
			LOG.error("Error while sending mail on error subscription :" + e.getMessage(), e);
		}
		return paymentTransaction;
	}

	private void sendBuyerSubscriptionErroMail(PaymentTransaction paymentTransaction) {
		String mailTo = paymentTransaction.getCommunicationEmail();
		String subject = "Subscription Failed";
		HashMap<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		map.put("date", df.format(new Date()) + " (UTC)");
		map.put("msg", "Error While Payment Transaction");
		map.put("userName", paymentTransaction.getCompanyName());
		map.put("loginUrl", APP_URL + "/login");
		map.put("errorCode", paymentTransaction != null ? paymentTransaction.getErrorCode() : "");
		map.put("errorMsg", paymentTransaction != null ? paymentTransaction.getErrorMessage() : "");
		notificationService.sendEmail(mailTo, subject, map, Global.BUYER_FAILURE_PAYMENT);
	}

	@Override
	@Transactional(readOnly = false)
	public User saveTrialBuyer(Buyer buyer) throws ApplicationException {
		User user = null;
		try {
			BuyerSubscription subscription = saveTrialSubscription(user, buyer);

			buyer.setStatus(BuyerStatus.PENDING);
			buyer.setCreatedDate(new Date());
			user = buyerService.saveBuyer(buyer);

			user.getBuyer().getCommunicationEmail();
			subscription.setBuyer(buyer);
			subscription = updateSubscription(subscription);
			if (user != null) {
				user.setShowWizardTutorial(Boolean.TRUE);
				LOG.info("**saveTrialBuyer**");
				userDao.update(user);
			}
		} catch (ApplicationException e) {
			LOG.error("Error creating Buyer instance : " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOG.error("Error creating Buyer instance : " + e.getMessage(), e);
			throw new ApplicationException("Error creating Buyer instance : " + e.getMessage(), e);
		}

		return user;
	}

	private BuyerSubscription saveTrialSubscription(User user, Buyer buyer) throws Exception {

		BuyerSubscription subscription = new BuyerSubscription();
		subscription.setCreatedDate(new Date());
		subscription.setActivatedDate(new Date());
		BuyerPlan plan = buyerPlanService.getPlanByPlanName("FREETRIAL", PlanStatus.HIDDEN);
		if (plan == null) {
			// TODO send mail to super admin
			LOG.error("Error Trial hidden plan is not configured");
		}
		// default lowest period duration for manual buyer
		if (CollectionUtil.isNotEmpty(plan.getPlanPeriodList())) {
			subscription.setPlanPeriod(plan.getPlanPeriodList().get(0));
		}
		subscription.setPlan(plan);
		subscription.setPlanType(plan.getPlanType());

		// default highest range price for manual buyer
		if (CollectionUtil.isNotEmpty(plan.getRangeList())) {
			subscription.setRange(plan.getRangeList().get(0));
		}
		subscription.setCurrencyCode(plan.getCurrency() != null ? plan.getCurrency().getCurrencyCode() : null);
		subscription.setPriceAmount(new BigDecimal(0));
		subscription.setTotalPriceAmount(new BigDecimal(0));
		subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
		subscription.setActivatedDate(new Date());

		// SET default for start date
		Calendar newDate = Calendar.getInstance();
		newDate.set(Calendar.AM_PM, Calendar.AM);
		newDate.set(Calendar.HOUR, 0);
		newDate.set(Calendar.MINUTE, 0);
		newDate.set(Calendar.SECOND, 0);
		newDate.set(Calendar.MILLISECOND, 0);
		subscription.setStartDate(newDate.getTime());

		Calendar endDate = Calendar.getInstance();
		endDate.setTime(subscription.getStartDate());
		endDate.set(Calendar.AM_PM, Calendar.PM);
		endDate.set(Calendar.HOUR, 11);
		endDate.set(Calendar.MINUTE, 59);
		endDate.set(Calendar.SECOND, 59);
		endDate.set(Calendar.MILLISECOND, 59);
		endDate.add(Calendar.DATE, 30);
		subscription.setEndDate(endDate.getTime());
		subscription.setUserQuantity(9999);
		subscription.setEventQuantity(9999);

		// Construct the Buyer Package that will hold the total sum of all active subscriptions
		BuyerPackage buyerPackage = new BuyerPackage(subscription);
		buyerPackage.setBuyer(buyer);

		// We will save subscription later after saving the Buyer.
		buyer.setCurrentSubscription(null);

		subscription = saveBuyerSubscription(subscription);
		buyer.setCurrentSubscription(subscription);
		buyer.setPlan(plan);
		buyer.setBuyerPackage(buyerPackage);
		buyer.setSubscriptionDate(new Date());
		return subscription;
	}

	@Override
	@Transactional(readOnly = false)
	public String initiatePaypalPaymentForBuyerTrialSubscription(BuyerSubscription subscription, HttpSession session, String returnURL, String cancelURL) {
		BuyerPlan plan = subscription.getPlan();
		if (plan == null) {
			return "redirect:/buyerSubscription/selectPlan";
		}
		Buyer buyer = subscription.getBuyer();
		BigDecimal tax = BigDecimal.ZERO;
		if (tax == null) {
			tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			if (buyer.getRegistrationOfCountry() != null && "MY".equalsIgnoreCase(buyer.getRegistrationOfCountry().getCountryCode())) {
				tax = plan.getTax();
				tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}

		// The paymentAmount is the total value of the shopping cart
		BigDecimal paymentAmount = new BigDecimal(0);
		if (subscription.getTotalPriceAmount() != null) {
			paymentAmount = subscription.getTotalPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		LOG.info("paymentAmount : " + paymentAmount);
		// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
		String currencyCodeType = plan.getCurrency().getCurrencyCode();
		String paymentType = "Order";

		// Create an instance of PaymentTransaction at the beginning of payment flow.
		PaymentTransaction paymentTransaction = null;
		if (CollectionUtil.isNotEmpty(subscription.getPaymentTransactions())) {
			paymentTransaction = subscription.getPaymentTransactions().get(0);
			if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(paymentTransaction.getPromoCode().getId());
				paymentTransaction.setPromoCode(promotionalCode);
			}
			paymentTransaction.setBuyerPlan(plan);
			paymentTransaction.setCreatedDate(new Date());

			LOG.info("currencyCodeType " + currencyCodeType);
			paymentTransaction.setCurrencyCode(currencyCodeType);

			paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
			paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
			paymentTransaction.setType(TransactionType.PAYMENT);
			paymentTransaction.setAmount(paymentAmount);
			paymentTransaction.setPriceAmount(subscription.getPriceAmount());
			paymentTransaction.setPriceDiscount(subscription.getPriceDiscount());
			paymentTransaction.setPromoCodeDiscount(subscription.getPromoCodeDiscount());
			paymentTransaction.setTotalPriceAmount(subscription.getTotalPriceAmount());
			paymentTransaction.setIsCapturePayment(Boolean.FALSE);
			paymentTransaction = savePaymentTransaction(paymentTransaction);
		}
		// The returnURL is the location where buyers return to when a payment has been succesfully authorized.
		returnURL += paymentTransaction.getId();

		// The cancelURL is the location buyers are sent to when they hit the cancel button during authorization of
		// payment during the PayPal flow
		cancelURL += paymentTransaction.getId();

		String note = "You are subscribing for ";
		String paymentRemarks = "Payment to be charged for ";
		int userEventQuantity = 0;
		int quantity = 1;
		String gstDescription = "";
		String gstDesc = "";
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		if (tax != null && tax.floatValue() > 0) {
			gstDescription = " (Inclusive of " + tax + "% Tax)";
			gstDesc = " (Inclusive of " + tax + "%25 Tax)";
		}
		// }

		if (plan.getPlanType() == PlanType.PER_USER) {
			if (plan.getBasePrice() != null && plan.getBaseUsers() > subscription.getUserQuantity()) {
				LOG.info("UserQuantity: " + userEventQuantity + "=====Base Users: " + plan.getBaseUsers());
				subscription.setUserQuantity(plan.getBaseUsers());
			}

			note += subscription.getUserQuantity() + " users for " + subscription.getPlanPeriod().getPlanDuration() + " months " + gstDesc;
			paymentRemarks += subscription.getUserQuantity() + " users for " + subscription.getPlanPeriod().getPlanDuration() + " months " + gstDescription;
			userEventQuantity = subscription.getUserQuantity();
			quantity = subscription.getPlanPeriod().getPlanDuration();
		} else {
			note += subscription.getEventQuantity() + " events for unlimited users " + gstDesc;
			paymentRemarks += subscription.getEventQuantity() + " events for unlimited users " + gstDescription;
			userEventQuantity = subscription.getEventQuantity();
		}

		note += ". You will be charged after expiry of the trial period.";
		paymentRemarks += " after expiry of the trial period.";

		// GST
		BigDecimal totalTax = new BigDecimal(0);
		BigDecimal itemTax = new BigDecimal(0);
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {

		LOG.info("paymentAmount: " + paymentAmount);
		LOG.info("tax: " + tax);
		LOG.info("paymentAmount.multiply(tax): " + paymentAmount.multiply(tax));

		totalTax = (paymentAmount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
		LOG.info("totalTax0 :" + totalTax);
		// itemTax = (totalTax.divide(new BigDecimal(quantity), 2, RoundingMode.HALF_UP));
		// itemTax = itemTax.setScale(2, BigDecimal.ROUND_HALF_UP);
		// totalTax = itemTax.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
		// }
		LOG.info("totalTax1 :" + totalTax);
		// Construct the parameter string that describes the PayPal payment.
		String nvpstr = "&PAYMENTREQUEST_0_AMT=" + paymentAmount.add(totalTax) + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType + "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan.";

		LOG.info("paymentAmount + totalTax = " + paymentAmount + " + " + totalTax + " = " + paymentAmount.add(totalTax));

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
		nvpstr += "&EMAIL=" + paymentTransaction.getCommunicationEmail();

		// Landing Page - Type of PayPal page to display

		nvpstr += "&LANDINGPAGE=Billing"; // or nvpstr += "&LANDINGPAGE=Login";

		/**
		 * Recurring Payments
		 */
		if (Boolean.TRUE == subscription.getAutoChargeSubscription()) {
			// Billing type
			nvpstr += "&L_BILLINGTYPE0=RecurringPayments";
			// Billing Agreement Desc
			nvpstr += "&L_BILLINGAGREEMENTDESCRIPTION0=" + "Payment for " + plan.getPlanName() + " plan at procurehere.com ";
		}
		// Note to Buyer
		nvpstr += "&NOTETOBUYER=" + note;

		/**
		 * Item Details
		 */
		// Item Amount
		nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + paymentAmount;
		LOG.info("paymentAmount :" + paymentAmount);

		// Total Tax
		nvpstr += "&PAYMENTREQUEST_0_TAXAMT=" + totalTax;
		// Total Shipping
		nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0";
		int item = 0;
		BigDecimal rangePrice = subscription.getRange().getPrice();
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		// rangePrice = convertUsdToMyr(subscription.getRange().getPrice(), null).setScale(2, BigDecimal.ROUND_HALF_UP);
		// } else {
		rangePrice = rangePrice.setScale(2, BigDecimal.ROUND_HALF_UP);
		// }

		// Item Name
		nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + plan.getPlanName();
		// Item Description
		nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + plan.getShortDescription();

		BigDecimal itemAmount = BigDecimal.ZERO;
		if (plan.getBasePrice() != null) {
			itemAmount = plan.getBasePrice().setScale(2, BigDecimal.ROUND_HALF_UP);
			// if (currencyCodeType.equalsIgnoreCase("MYR")) {
			// itemAmount = convertUsdToMyr(plan.getBasePrice(), null).setScale(2, BigDecimal.ROUND_HALF_UP);
			// }

			if (plan.getBaseUsers() < userEventQuantity) {
				userEventQuantity = userEventQuantity - plan.getBaseUsers();
				LOG.info("itemAmount : " + itemAmount + " rangePrice :" + rangePrice + " userEventQuantity: " + userEventQuantity);
				itemAmount = itemAmount.add(((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);
		} else {
			itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);

		// Item Amount
		nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + itemAmount.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP);

		// Item Qty
		nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";

		// Item Tax
		nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=" + totalTax;
		LOG.info("tax on total item :" + totalTax);
		item++;
		if (subscription.getPlanPeriod() != null && subscription.getPriceDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + "Subscription Discount";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + subscription.getPlanPeriod().getPlanDiscount() + " %25";
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + (subscription.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("subscription discount : " + subscription.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());

			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";

			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=0";
			item++;
		}
		if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + "Promo Discount ";
			String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP) + " OFF" : paymentTransaction.getPromoCode().getPromoDiscount() + "%25 OFF";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + paymentTransaction.getPromoCode().getPromoName() + "-" + promoDesc;
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + (subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("promo discount : " + subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";
			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=0";
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
		LOG.info("Payment gateway response : " + nvp.get("ACK"));

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {
			session.setAttribute(BuyerSubscriptionServiceImpl.TOKEN, nvp.get(BuyerSubscriptionServiceImpl.TOKEN).toString());
			LOG.info(" TOKEN : " + nvp.get(BuyerSubscriptionServiceImpl.TOKEN));

			// We dont need these for saving the subscription so add it to session
			session.setAttribute("buyer", buyer);

			subscription.setBuyer(null);

			subscription.setPlan(plan);
			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);

			subscription.setPriceAmount(((itemAmount).multiply(new BigDecimal(quantity))).setScale(2, BigDecimal.ROUND_HALF_UP));
			subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
			subscription.setTotalPriceAmount(paymentAmount.add(totalTax));
			subscription = saveBuyerSubscription(subscription);

			// Save the TOKEN in DB
			paymentTransaction.setBuyerSubscription(subscription);
			paymentTransaction.setPaymentToken(nvp.get(BuyerSubscriptionServiceImpl.TOKEN));

			paymentTransaction.setPriceAmount(subscription.getPriceAmount());
			paymentTransaction.setRemarks(paymentRemarks);
			paymentTransaction.setAdditionalTaxDesc("Tax " + tax + "%");
			paymentTransaction.setAdditionalTax(totalTax);
			paymentTransaction.setAmount(subscription.getTotalPriceAmount());
			paymentTransaction.setTotalPriceAmount(subscription.getTotalPriceAmount());
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			// Reassign the values
			// subscription.setBuyer(buyer);

			session.setAttribute("subscriptionId", subscription.getId());
			session.setAttribute("paymentTransactionId", paymentTransaction.getId());

			String payPalURL = PAYPAL_URL + nvp.get(BuyerSubscriptionServiceImpl.TOKEN);
			return "redirect:" + payPalURL;

		} else {
			paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
		}

		return "redirect:../subscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
	}

	@Override
	@Transactional(readOnly = false)
	public HashMap<String, String> authorizeOrderPayment(String paymentTransactionId, String payerId, String serverName) throws ApplicationException {

		User user = null;
		String paymentType = "Authorization";
		PaymentTransaction paymentTransaction = getPaymentTransactionById(paymentTransactionId);

		String nvpstr = "&TOKEN=" + paymentTransaction.getPaymentToken() + "&PAYERID=" + payerId + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_AMT=" + paymentTransaction.getAmount();
		nvpstr = nvpstr + "&PAYMENTREQUEST_0_CURRENCYCODE=" + paymentTransaction.getCurrencyCode() + "&IPADDRESS=" + serverName;

		// INSTANT Payment Notification URL
		// nvpstr += "&NOTIFYURL=" + APP_URL + "/buyerSubscription/instantPaymentNotification";

		LOG.info("Invoking PayPal Confirm Payment ===================> ");
		LOG.info("Confirm nvp ===================> " + nvpstr);

		/*
		 * Make the call to PayPal to finalize payment If an error occured, show the resulting errors
		 */
		HashMap<String, String> nvp = invokePaypalService("DoExpressCheckoutPayment", nvpstr);

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {
			LOG.info("Authorize Order payment Success!!!!!!!!!!!!!");

		} else {
			LOG.error("Authorize Order payment failed !!!!!!!!!!!!!");
		}

		return nvp;

	}

	/*
	 * @Override
	 * @Transactional(readOnly = false) public HashMap<String, String> capturePayment(String paymentTransactionId)
	 * throws ApplicationException { PaymentTransaction paymentTransaction =
	 * getPaymentTransactionById(paymentTransactionId); String nvpstr = "&AUTHORIZATIONID=" +
	 * paymentTransaction.getReferenceTransactionId() + "&AMT=" + paymentTransaction.getAmount() + "&CURRENCYCODE=" +
	 * paymentTransaction.getCurrencyCode() + "&COMPLETETYPE=Complete"; // INSTANT Payment Notification URL // nvpstr +=
	 * "&NOTIFYURL=" + APP_URL + "/buyerSubscription/instantPaymentNotification";
	 * LOG.info("Invoking PayPal Capture Payment ===================> "); LOG.info("Confirm nvp ===================> " +
	 * nvpstr); Make the call to PayPal to finalize payment If an error occured, show the resulting errors
	 * HashMap<String, String> nvp = invokePaypalService("DoCapture", nvpstr); String strAck =
	 * nvp.get("ACK").toString(); if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {
	 * LOG.info("Capture payment Success!!!!!!!!!!!!!"); } else { LOG.error("Capture payment failed !!!!!!!!!!!!!"); }
	 * return nvp; }
	 */

	@Override
	@Transactional(readOnly = false)
	public void doCapturePayment(String subscriptionId) throws ApplicationException {
		PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionBySubscriptionId(subscriptionId);
		if (paymentTransaction != null) {

			String nvpstr = "&AUTHORIZATIONID=" + paymentTransaction.getReferenceTransactionId() + "&AMT=" + paymentTransaction.getAmount() + "&CURRENCYCODE=" + paymentTransaction.getCurrencyCode() + "&COMPLETETYPE=Complete";

			LOG.info("Invoking PayPal Capture Payment ===================> ");
			LOG.info("Confirm nvp ===================> " + nvpstr);

			/*
			 * Make the call to PayPal to finalize payment If an error occured, show the resulting errors
			 */
			HashMap<String, String> nvp = invokePaypalService("DoCapture", nvpstr);
			String strAck = nvp.get("ACK").toString();
			if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {
				LOG.info("Capture payment Success!!!!!!!!!!!!!");
				paymentTransaction.setIsCapturePayment(Boolean.TRUE);
				paymentTransaction = updatePaymentTransaction(paymentTransaction);
			} else {
				LOG.error("Capture payment failed !!!!!!!!!!!!!");
				throw new ApplicationException("Error while confirm payment");
			}
		}

	}

	@Override
	public void downloadSubscriptionHistoryExcel(HttpServletResponse response, String loggedInUserTenantId) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "SubscriptionHistory.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			workbook = getExcelWorkBook(loggedInUserTenantId);
			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");

			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.getOutputStream().flush();
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("Error :- " + e.getMessage());
		}

	}

	private XSSFWorkbook getExcelWorkBook(String loggedInUserTenantId) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("User List");
		// Creating Headings
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.SUBSCRIPTION_HISTORY_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}

		List<BuyerSubscription> buyerSubscriptionList = buyerSubscriptionDao.getAllBuyerSubscriptionForBuyerForExcel(loggedInUserTenantId);

		int r = 1;
		if (CollectionUtil.isNotEmpty(buyerSubscriptionList)) {
			for (BuyerSubscription buyerSubscription : buyerSubscriptionList) {

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(buyerSubscription.getPlan() != null ? buyerSubscription.getPlan().getPlanName() : "");
				row.createCell(cellNum++).setCellValue(buyerSubscription.getUserQuantity() != null ? buyerSubscription.getUserQuantity().toString() : "");
				row.createCell(cellNum++).setCellValue(buyerSubscription.getEventQuantity() != null ? buyerSubscription.getEventQuantity().toString() : "");
				row.createCell(cellNum++).setCellValue(buyerSubscription.getStartDate() != null ? buyerSubscription.getStartDate().toString() : "");
				row.createCell(cellNum++).setCellValue(buyerSubscription.getEndDate() != null ? buyerSubscription.getEndDate().toString() : "");
				row.createCell(cellNum++).setCellValue(buyerSubscription.getTotalPriceAmount() != null ? buyerSubscription.getTotalPriceAmount().toString() : "");
				row.createCell(cellNum++).setCellValue(buyerSubscription.getSubscriptionStatus() != null ? buyerSubscription.getSubscriptionStatus().toString() : "");
				row.createCell(cellNum++).setCellValue(buyerSubscription.getPlanType() != null ? buyerSubscription.getPlanType().toString() : "");
			}
		}
		for (int k = 0; k < 15; k++) {
			sheet.autoSizeColumn(k, true);
		}
		return workbook;
	}

	@Override
	public void downloadPaymentTransactionExcel(HttpServletResponse response, String loggedInUserTenantId) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "SubscriptionHistory.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			workbook = getExcelWorkBookForPaymentTransaction(loggedInUserTenantId);
			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");

			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.getOutputStream().flush();
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("Error :- " + e.getMessage());
		}

	}

	private XSSFWorkbook getExcelWorkBookForPaymentTransaction(String loggedInUserTenantId) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("User List");
		// Creating Headings
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.PAYMENT_TRANSACTION_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}

		List<PaymentTransaction> paymentTransactionList = paymentTransactionDao.getAllPaymentTransactionForBuyerForExcel(loggedInUserTenantId, TransactionStatus.SUCCESS);

		int r = 1;
		if (CollectionUtil.isNotEmpty(paymentTransactionList)) {
			for (PaymentTransaction paymentTransaction : paymentTransactionList) {

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(paymentTransaction.getBuyerPlan() != null ? paymentTransaction.getBuyerPlan().getPlanName() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getCreatedDate() != null ? paymentTransaction.getCreatedDate().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getRemarks() != null ? paymentTransaction.getRemarks() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getReferenceTransactionId() != null ? paymentTransaction.getReferenceTransactionId() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getPriceAmount() != null ? paymentTransaction.getPriceAmount().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getPriceDiscount() != null ? paymentTransaction.getPriceDiscount().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getPromoCodeDiscount() != null ? paymentTransaction.getPromoCodeDiscount().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getAdditionalTax() != null ? paymentTransaction.getAdditionalTax().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getTotalPriceAmount() != null ? paymentTransaction.getTotalPriceAmount().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getCurrencyCode() != null ? paymentTransaction.getCurrencyCode() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getPromoCode() != null ? paymentTransaction.getPromoCode().getPromoCode() : "N/A");
			}
		}
		for (int k = 0; k < 15; k++) {
			sheet.autoSizeColumn(k, true);
		}
		return workbook;
	}

	@Override
	@Transactional(readOnly = false)
	public String initiatePaypalPaymentForBuyBuyerSubscription(BuyerSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL) throws ApplicationException {
		BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
		if (plan == null) {
			throw new ApplicationException("Plane is null ");
			// return "redirect:/buyerSubscription/selectPlan";
		}
		Buyer buyer = subscription.getBuyer();

		BigDecimal tax = BigDecimal.ZERO;
		if (tax == null) {
			tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			if (buyer.getRegistrationOfCountry() != null && "MY".equalsIgnoreCase(buyer.getRegistrationOfCountry().getCountryCode())) {
				tax = plan.getTax();
				tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				tax = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}

		// The paymentAmount is the total value of the shopping cart
		BigDecimal paymentAmount = new BigDecimal(0);
		if (subscription.getTotalPriceAmount() != null) {
			paymentAmount = subscription.getTotalPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		LOG.info("paymentAmount : " + paymentAmount);
		// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
		String currencyCodeType = plan.getCurrency().getCurrencyCode();
		String paymentType = "Sale";
		// Create an instance of PaymentTransaction at the beginning of payment flow.
		PaymentTransaction paymentTransaction = null;
		if (CollectionUtil.isNotEmpty(subscription.getPaymentTransactions())) {
			paymentTransaction = subscription.getPaymentTransactions().get(0);
			if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
				PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(paymentTransaction.getPromoCode().getId());
				paymentTransaction.setPromoCode(promotionalCode);
			}
			paymentTransaction.setBuyerPlan(plan);
			paymentTransaction.setCreatedDate(new Date());
			// Convert currency if user select Malaysia country
			// if (paymentTransaction.getCountry() != null &&
			// paymentTransaction.getCountry().getCountryCode().equalsIgnoreCase("MY")) {
			// currencyCodeType = "MYR"; // paymentTransaction.getCountry().getCountryCode();
			// try {
			// // call currency change Api here
			// paymentAmount = convertUsdToMyr(paymentAmount, paymentTransaction);
			// if (paymentAmount != null) {
			// paymentAmount = paymentAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
			// }
			// BigDecimal priceAmount = convertUsdToMyr(subscription.getPriceAmount(), null);
			// if (priceAmount != null) {
			// subscription.setPriceAmount(priceAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal priceDiscount = convertUsdToMyr(subscription.getPriceDiscount(), null);
			// if (priceDiscount != null) {
			// subscription.setPriceDiscount(priceDiscount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal promoCodeDiscount = convertUsdToMyr(subscription.getPromoCodeDiscount(), null);
			// if (promoCodeDiscount != null) {
			// subscription.setPromoCodeDiscount(promoCodeDiscount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// BigDecimal totalPriceAmount = convertUsdToMyr(subscription.getTotalPriceAmount(), null);
			// if (totalPriceAmount != null) {
			// subscription.setTotalPriceAmount(totalPriceAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			// }
			// } catch (Exception e) {
			// LOG.error("Error while calling currency change Api :" + e.getMessage(), e);
			// }
			// }
			LOG.info("currencyCodeType " + currencyCodeType);
			paymentTransaction.setCurrencyCode(currencyCodeType);

			paymentTransaction.setPaymentMethod(PaymentMethod.PAYPAL_EXPRESS_CHECKOUT);
			paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
			paymentTransaction.setType(TransactionType.PAYMENT);
			paymentTransaction.setAmount(paymentAmount);
			paymentTransaction.setPriceAmount(subscription.getPriceAmount());
			paymentTransaction.setPriceDiscount(subscription.getPriceDiscount());
			paymentTransaction.setPromoCodeDiscount(subscription.getPromoCodeDiscount());
			paymentTransaction.setTotalPriceAmount(subscription.getTotalPriceAmount());
			paymentTransaction = savePaymentTransaction(paymentTransaction);
		}
		// The returnURL is the location where buyers return to when a payment has been succesfully authorized.
		returnURL += paymentTransaction.getId();

		// The cancelURL is the location buyers are sent to when they hit the cancel button during authorization of
		// payment during the PayPal flow
		cancelURL += paymentTransaction.getId();

		String note = "You are subscribing for ";
		String paymentRemarks = "Payment for ";
		int userEventQuantity = 0;
		int quantity = 1;
		String gstDescription = "";
		String gstDesc = "";
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		if (tax != null && tax.floatValue() > 0) {
			gstDescription = " (Inclusive of " + tax + "% Tax)";
			gstDesc = " (Inclusive of " + tax + "%25 Tax)";
		}
		// }

		if (plan.getPlanType() == PlanType.PER_USER) {

			if (plan.getBasePrice() != null && plan.getBaseUsers() > subscription.getUserQuantity()) {
				LOG.info("UserQuantity: " + userEventQuantity + "=====Base Users: " + plan.getBaseUsers());
				subscription.setUserQuantity(plan.getBaseUsers());
			}

			note += subscription.getUserQuantity() + " users for " + subscription.getPlanPeriod().getPlanDuration() + " months " + gstDesc;
			paymentRemarks += subscription.getUserQuantity() + " users for " + subscription.getPlanPeriod().getPlanDuration() + " months " + gstDescription;
			userEventQuantity = subscription.getUserQuantity();
			quantity = subscription.getPlanPeriod().getPlanDuration();
		} else {
			note += subscription.getEventQuantity() + " events for unlimited users " + gstDesc;
			paymentRemarks += subscription.getEventQuantity() + " events for unlimited users " + gstDescription;

			userEventQuantity = subscription.getEventQuantity();
		}

		// GST
		BigDecimal totalTax = new BigDecimal(0);
		BigDecimal itemTax = new BigDecimal(0);
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {

		LOG.info("paymentAmount: " + paymentAmount);
		LOG.info("tax: " + tax);
		LOG.info("paymentAmount.multiply(tax): " + paymentAmount.multiply(tax));

		totalTax = (paymentAmount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
		LOG.info("totalTax0 :" + totalTax);
		// itemTax = (totalTax.divide(new BigDecimal(quantity), 2, RoundingMode.HALF_UP));
		// itemTax = itemTax.setScale(2, BigDecimal.ROUND_HALF_UP);
		// totalTax = itemTax.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
		// }
		LOG.info("totalTax1 :" + totalTax);
		// Construct the parameter string that describes the PayPal payment.
		String nvpstr = "&PAYMENTREQUEST_0_AMT=" + paymentAmount.add(totalTax) + "&PAYMENTREQUEST_0_PAYMENTACTION=" + paymentType + "&PAYMENTREQUEST_0_CURRENCYCODE=" + currencyCodeType + "&PAYMENTREQUEST_0_DESC=Subscription of procurehere.com plan.";

		LOG.info("paymentAmount + totalTax = " + paymentAmount + " + " + totalTax + " = " + paymentAmount.add(totalTax));

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
		nvpstr += "&EMAIL=" + paymentTransaction.getCommunicationEmail();

		// Landing Page - Type of PayPal page to display

		nvpstr += "&LANDINGPAGE=Billing"; // or nvpstr += "&LANDINGPAGE=Login";

		/**
		 * Recurring Payments
		 */
		if (Boolean.TRUE == subscription.getAutoChargeSubscription()) {
			// Billing type
			nvpstr += "&L_BILLINGTYPE0=RecurringPayments";
			// Billing Agreement Desc
			nvpstr += "&L_BILLINGAGREEMENTDESCRIPTION0=" + "Payment for " + plan.getPlanName() + " plan at procurehere.com ";
		}
		// Note to Buyer
		nvpstr += "&NOTETOBUYER=" + note;

		/**
		 * Item Details
		 */
		// Item Amount
		nvpstr += "&PAYMENTREQUEST_0_ITEMAMT=" + paymentAmount;
		LOG.info("paymentAmount :" + paymentAmount);

		// Total Tax
		nvpstr += "&PAYMENTREQUEST_0_TAXAMT=" + totalTax;
		// Total Shipping
		nvpstr += "&PAYMENTREQUEST_0_SHIPPINGAMT=0";
		int item = 0;
		BigDecimal rangePrice = subscription.getRange().getPrice();
		// if (currencyCodeType.equalsIgnoreCase("MYR")) {
		// rangePrice = convertUsdToMyr(subscription.getRange().getPrice(), null).setScale(2, BigDecimal.ROUND_HALF_UP);
		// } else {
		rangePrice = rangePrice.setScale(2, BigDecimal.ROUND_HALF_UP);
		// }

		// Item Name
		nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + plan.getPlanName();
		// Item Description
		nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + plan.getShortDescription();

		BigDecimal itemAmount = BigDecimal.ZERO;
		if (plan.getBasePrice() != null) {
			itemAmount = plan.getBasePrice().setScale(2, BigDecimal.ROUND_HALF_UP);
			// if (currencyCodeType.equalsIgnoreCase("MYR")) {
			// itemAmount = convertUsdToMyr(plan.getBasePrice(), null).setScale(2, BigDecimal.ROUND_HALF_UP);
			// }
			LOG.info("plan.getBaseUsers() : " + plan.getBaseUsers() + " userEventQuantity :" + userEventQuantity);
			if (plan.getBaseUsers() < userEventQuantity) {
				userEventQuantity = userEventQuantity - plan.getBaseUsers();
				LOG.info("itemAmount : " + itemAmount + " rangePrice :" + rangePrice + " userEventQuantity: " + userEventQuantity);
				itemAmount = itemAmount.add(((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP));
			}
			LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);
		} else {
			itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);

		// Item Amount
		nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + itemAmount.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP);

		// Item Qty
		nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";

		// Item Tax
		nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=" + totalTax;
		LOG.info("tax on total item :" + totalTax);
		item++;
		if (subscription.getPlanPeriod() != null && subscription.getPriceDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + "Subscription Discount";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + subscription.getPlanPeriod().getPlanDiscount() + " %25";
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + (subscription.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("subscription discount : " + subscription.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());

			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";

			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=0";
			item++;
		}
		if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
			// Item Name
			nvpstr += "&L_PAYMENTREQUEST_0_NAME" + item + "=" + "Promo Discount ";
			String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP) + " OFF" : paymentTransaction.getPromoCode().getPromoDiscount() + "%25 OFF";
			// Item Description
			nvpstr += "&L_PAYMENTREQUEST_0_DESC" + item + "=" + paymentTransaction.getPromoCode().getPromoName() + "-" + promoDesc;
			// Item Amount
			nvpstr += "&L_PAYMENTREQUEST_0_AMT" + item + "=" + (subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			LOG.info("promo discount : " + subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			// Item Qty
			nvpstr += "&L_PAYMENTREQUEST_0_QTY" + item + "=1";
			// Item Tax
			nvpstr += "&L_PAYMENTREQUEST_0_TAXAMT" + item + "=0";
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
		LOG.info("Payment gateway response : " + nvp.get("ACK"));

		String strAck = nvp.get("ACK").toString();
		if (strAck != null && strAck.equalsIgnoreCase(BuyerSubscriptionServiceImpl.SUCCESS)) {
			session.setAttribute(BuyerSubscriptionServiceImpl.TOKEN, nvp.get(BuyerSubscriptionServiceImpl.TOKEN).toString());
			LOG.info(" TOKEN : " + nvp.get(BuyerSubscriptionServiceImpl.TOKEN));

			// We dont need these for saving the subscription
			LOG.info("buyer ID: " + buyer.getId());
			if (buyer != null && StringUtils.checkString(buyer.getId()).length() > 0) {
				subscription.setBuyer(buyer);
			} else {
				subscription.setBuyer(null);
			}

			subscription.setPlan(plan);
			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);

			subscription.setPriceAmount(((itemAmount).multiply(new BigDecimal(quantity))).setScale(2, BigDecimal.ROUND_HALF_UP));
			subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
			subscription.setTotalPriceAmount(paymentAmount.add(totalTax));
			subscription = saveBuyerSubscription(subscription);

			// Save the TOKEN in DB
			paymentTransaction.setBuyerSubscription(subscription);
			paymentTransaction.setPaymentToken(nvp.get(BuyerSubscriptionServiceImpl.TOKEN));

			paymentTransaction.setPriceAmount(subscription.getPriceAmount());
			paymentTransaction.setRemarks(paymentRemarks);
			paymentTransaction.setAdditionalTaxDesc("Tax " + tax + "%");
			paymentTransaction.setAdditionalTax(totalTax);
			paymentTransaction.setAmount(subscription.getTotalPriceAmount());
			paymentTransaction.setTotalPriceAmount(subscription.getTotalPriceAmount());
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
			// Reassign the values
			// subscription.setBuyer(buyer);

			session.setAttribute("subscriptionId", subscription.getId());
			session.setAttribute("paymentTransactionId", paymentTransaction.getId());
			session.setAttribute("buyer", buyer);

			String payPalURL = PAYPAL_URL + nvp.get(BuyerSubscriptionServiceImpl.TOKEN);
			return "redirect:" + payPalURL;

		} else {
			paymentTransaction = markTransactionFailed(paymentTransaction, nvp);
		}

		return "redirect:../subscriptionError/" + plan.getId() + "/" + paymentTransaction.getId();
	}

	@Override
	public List<BuyerSubscriptionPojo> getBuyerSubscriptionFutureplan(String tenantId) {

		List<BuyerSubscriptionPojo> pojolist = new ArrayList<BuyerSubscriptionPojo>();
		List<BuyerSubscription> buyerSubscriptions = buyerSubscriptionDao.getNonActiveBuyerSubscriptionList(tenantId);
		for (BuyerSubscription buyerSubscription : buyerSubscriptions) {
			BuyerSubscriptionPojo pojo = new BuyerSubscriptionPojo();
			pojo.setPromoCode(getComaSeprated(buyerSubscription.getPaymentTransactions()));
			pojo.setStartDate(buyerSubscription.getStartDate());
			pojo.setEndDate(buyerSubscription.getEndDate());
			pojo.setCreatedDate(buyerSubscription.getCreatedDate());
			pojo.setPlan(buyerSubscription.getPlan().getPlanName());
			pojo.setSubscriptionStatus(buyerSubscription.getSubscriptionStatus());
			pojolist.add(pojo);
		}
		return pojolist;
	}

	private String getComaSeprated(List<PaymentTransaction> list) {
		String promoCodes = "";
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getPromoCode() != null) {
				if (StringUtils.checkString(list.get(i).getPromoCode().getPromoCode()).length() > 0) {
					if (i == 0) {
						promoCodes += list.get(i).getPromoCode().getPromoCode();
					} else {
						promoCodes += (promoCodes.length() > 0 ? "," : "") + list.get(i).getPromoCode().getPromoCode();
					}
				}
			}
		}
		LOG.info("promoCodes&&&&&&&&&&&&" + promoCodes);
		return StringUtils.checkString(promoCodes).length() > 0 ? promoCodes : "N/A";
	}
}
