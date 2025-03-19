package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerSubscriptionDao;
import com.privasia.procurehere.core.dao.PaymentTransactionDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerPackage;
import com.privasia.procurehere.core.entity.BuyerPlan;
import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.PlanPeriod;
import com.privasia.procurehere.core.entity.PlanRange;
import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PaymentMethod;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.enums.TransactionType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerPlanService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSubscriptionStripeService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PromotionalCodeService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentCreateParams.Builder;

@Service
@Transactional(readOnly = false)
public class BuyerSubscriptionStripeServiceImpl implements BuyerSubscriptionStripeService {

	private static final Logger LOG = LogManager.getLogger(Global.SUBSCRIPTION_LOG);

	@Autowired
	PaymentTransactionDao paymentTransactionDao;

	@Autowired
	BuyerSubscriptionDao buyerSubscriptionDao;

	@Autowired
	BuyerService buyerService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	BuyerPlanService buyerPlanService;

	@Autowired
	PromotionalCodeService promotionalCodeService;

	@Value("${app.url}")
	String APP_URL;

	@Value("${stripe.secret}")
	String secretKey;

	@Value("${stripe.publish}")
	String publishKey;

	@Override
	@Transactional(readOnly = false)
	public void doComputeSubscription(BuyerSubscription subscription, BuyerSubscription previousSubscription) {
		if (subscription.getPlan() == null) {
			LOG.info("Plan is null!");
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
	public void createRecurringPaymentsSubscription(String token, BuyerSubscription subscription, PaymentTransaction paymentTransaction) {
	}

	@Override
	public void cancelRecurringPaymentsSubscription(String token, BuyerSubscription previousSubscription, PaymentTransaction paymentTransaction) {
	}

	@Override
	public void sendBuyerSubscriptionMail(String mailTo, String name, BuyerSubscription subscription, String msg, String subject, boolean isSuccess, PaymentTransaction paymentTransaction) {
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
			}
		}
		return subs;
	}

	@Override
	public String getPaymentStatus(String paymentToken) throws ApplicationException {
		Stripe.apiKey = secretKey;
		try {
			LOG.info("Getting Payment status for : " + paymentToken);
			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentToken);
			if (Global.STRIPE_STATUS_SUCCESS.equals(paymentIntent.getStatus())) {
				String message = "Successfully made a payment of " + (paymentIntent.getCurrency()).toUpperCase() + " " + new BigDecimal(paymentIntent.getAmount()).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				return message;
			} else if (Global.STRIPE_STATUS_PROCESSING.equals(paymentIntent.getStatus())) {
				String message = "Processing a payment of " + (paymentIntent.getCurrency()).toUpperCase() + " " + new BigDecimal(paymentIntent.getAmount()).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
				return message;
			} else {
				throw new ApplicationException(paymentIntent.getLastPaymentError() != null ? paymentIntent.getLastPaymentError().getMessage() : "Payment Not completed.");
			}
		} catch (Exception e) {
			LOG.error("Error getting payment status application exception: " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = false)
	public PaymentIntentPojo initiateStripePaymentForBuyerSubscription(BuyerSubscription subscription, String planId, String paymentMode, boolean renew) throws ApplicationException {
		PaymentTransaction paymentTransaction = new PaymentTransaction();
		try {
			BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
			if (plan == null) {
				throw new ApplicationException("Plan cannot be null");
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

			// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
			String currencyCodeType = plan.getCurrency().getCurrencyCode();
			String paymentType = paymentMode;

			BigDecimal paymentAmount = new BigDecimal(0);
			// Create an instance of PaymentTransaction at the beginning of payment flow.
			if (CollectionUtil.isNotEmpty(subscription.getPaymentTransactions())) {
				paymentTransaction = subscription.getPaymentTransactions().get(0);
				// The paymentAmount is the total value of the shopping cart
				if (paymentTransaction.getTotalPriceAmount() != null) {
					paymentAmount = paymentTransaction.getTotalPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
				}
				LOG.info("Payment amount for subscription is: " + paymentAmount + " with currency code: " + plan.getCurrency().getCurrencyCode());
				if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
					PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(paymentTransaction.getPromoCode().getId());
					paymentTransaction.setPromoCode(promotionalCode);
				}
				paymentTransaction.setBuyerPlan(plan);
				paymentTransaction.setCreatedDate(new Date());
				LOG.info("currencyCodeType " + currencyCodeType);
				paymentTransaction.setCurrencyCode(currencyCodeType);
				paymentTransaction.setPaymentMethod((StringUtils.equals("fpx", paymentType) ? PaymentMethod.BANK_TRANSFER : StringUtils.equals("card", paymentType) ? PaymentMethod.CARD : PaymentMethod._UNKNOWN));
				paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
				paymentTransaction.setType(TransactionType.PAYMENT);
				if (subscription.getBuyer() != null) {
					paymentTransaction.setCompanyName(subscription.getBuyer().getCompanyName());
					paymentTransaction.setCountry(subscription.getBuyer().getRegistrationOfCountry());
				}
				paymentTransaction = savePaymentTransaction(paymentTransaction);
			}

			// For free trial
			String planDesc = (renew ? "Renew Plan: " : "");

			if (StringUtils.isNotBlank(buyer.getId())) {
				Buyer b = buyerService.findBuyerByIdWithBuyerPackage(buyer.getId());
				if (b != null && b.getBuyerPackage() != null && b.getBuyerPackage().getPlan() != null) {
					if (b.getBuyerPackage().getPlan().getPlanName().equals("FREETRIAL")) {
						planDesc = "Change Plan: ";
					}
				}
			}

			String note = planDesc + buyer.getCompanyName() + " is subscribing for ";
			String paymentRemarks = "Payment for ";
			int userEventQuantity = 0;
			int quantity = 1;
			String gstDescription = "";
			String gstDesc = "";
			if (tax != null && tax.floatValue() > 0) {
				gstDescription = " (Inclusive of " + tax + " % SST)";
				gstDesc = " (Inclusive of " + tax + " % SST)";
			} else {
				gstDescription = " (Inclusive of " + 0 + " % SST)";
				gstDesc = " (Inclusive of " + 0 + " % SST)";
			}

			if (plan.getPlanType() == PlanType.PER_USER) {
				if (plan.getBasePrice() != null && plan.getBaseUsers() > paymentTransaction.getUserQuantity()) {
					LOG.info("UserQuantity: " + userEventQuantity + "=====Base Users: " + plan.getBaseUsers());
					paymentTransaction.setUserQuantity(plan.getBaseUsers());
				}
				note += paymentTransaction.getUserQuantity() + " users for ";
				paymentRemarks += paymentTransaction.getUserQuantity() + " users for ";
				if (subscription.getPlanPeriod().getPlanDuration() == 12) {
					note += " 1 Year " + gstDesc;
					paymentRemarks += " 1 Year " + gstDescription;
				} else {
					note += subscription.getPlanPeriod().getPlanDuration() + " months " + gstDesc;
					paymentRemarks += subscription.getPlanPeriod().getPlanDuration() + " months " + gstDescription;
				}
				userEventQuantity = paymentTransaction.getUserQuantity();
				quantity = subscription.getPlanPeriod().getPlanDuration();
			} else {
				note += paymentTransaction.getEventQuantity() + " events for unlimited users " + gstDesc;
				paymentRemarks += paymentTransaction.getEventQuantity() + " events for unlimited users " + gstDescription;
				userEventQuantity = paymentTransaction.getEventQuantity();
			}

			if (paymentTransaction != null && paymentTransaction.getPromoCode() != null) {
				String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? paymentTransaction.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP) + " " + (currencyCodeType != null ? currencyCodeType : "") + " OFF" : paymentTransaction.getPromoCode().getPromoDiscount() + "% OFF";
				note += " " + promoDesc;
			}

			// GST
			// BigDecimal totalTax = new BigDecimal(0);
			// totalTax = (paymentAmount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new
			// BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

			// paymentTransaction.setAdditionalTax(totalTax);

			if (Boolean.TRUE == subscription.getAutoChargeSubscription()) {
				// Do nothing
			}

			// Note to Buyer
			BigDecimal rangePrice = subscription.getRange().getPrice();
			rangePrice = rangePrice.setScale(2, BigDecimal.ROUND_HALF_UP);

			BigDecimal itemAmount = BigDecimal.ZERO;
			if (plan.getBasePrice() != null) {
				itemAmount = plan.getBasePrice().setScale(2, BigDecimal.ROUND_HALF_UP);
				if (plan.getBaseUsers() < userEventQuantity) {
					userEventQuantity = userEventQuantity - plan.getBaseUsers();
					itemAmount = itemAmount.add(((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
			} else {
				itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP);
			}

			itemAmount.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP);

			if (subscription.getPlanPeriod() != null && paymentTransaction.getPriceDiscount() != null) {
				itemAmount.subtract(paymentTransaction.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
				LOG.info("Subscription discount : " + paymentTransaction.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			}

			if (paymentTransaction != null && paymentTransaction.getPromoCode() != null) {
				itemAmount.subtract(paymentTransaction.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
				LOG.info("Promo discount : " + paymentTransaction.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			}

			LOG.info("Making payment for " + plan.getPlanName() + " with currency code " + plan.getCurrency().getCurrencyCode() + " for amount " + itemAmount + " and description " + note + " with mode " + paymentType);
			PaymentIntentPojo response = invokeStripePayment(paymentType, paymentAmount, note, subscription, paymentTransaction);

			if (buyer != null && StringUtils.checkString(buyer.getId()).length() > 0) {
				subscription.setBuyer(buyer);
			} else {
				subscription.setBuyer(null);
			}

			subscription.setPlan(plan);
			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);

			// subscription.setPriceAmount(((itemAmount).multiply(new BigDecimal(quantity))).setScale(2,
			// BigDecimal.ROUND_HALF_UP));
			subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
			subscription = saveBuyerSubscription(subscription);

			LOG.info("Saved subscription with ID " + subscription.getId());

			// Save the TOKEN in DB
			paymentTransaction.setBuyerSubscription(subscription);
			paymentTransaction.setPaymentToken(response.getId());
			paymentTransaction.setRemarks(paymentRemarks);
			paymentTransaction.setAdditionalTaxDesc("SST " + tax + "%");
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
			return response;

		} catch (Exception e) {
			LOG.error("Error in payment: " + e.getLocalizedMessage(), e);
			paymentTransaction.setStatus(TransactionStatus.FAILURE);
			paymentTransaction.setErrorCode("-1");
			paymentTransaction.setErrorMessage("Error Initiating Stripe payment");
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
			throw new ApplicationException(e.getLocalizedMessage());
		}
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
	public PaymentIntentPojo invokeStripePayment(String paymentMode, BigDecimal amount, String description, BuyerSubscription subscription, PaymentTransaction paymentTransaction) throws ApplicationException {
		try {
			if (StringUtils.isBlank(secretKey)) {
				throw new ApplicationException("No Stripe key found. Please provide a valid stripe secret key.");
			}
			Stripe.apiKey = secretKey;
			PaymentIntentCreateParams params = null;
			if (StringUtils.equals(paymentMode, "fpx")) {
				if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
					amount.subtract(subscription.getPromoCodeDiscount());
				}
				Builder b = PaymentIntentCreateParams.builder().setAmount(amount.multiply(new BigDecimal(100)).longValue()).setCurrency("myr").setDescription(description).addPaymentMethodType("fpx").setReceiptEmail((StringUtils.isNotBlank(subscription.getBuyer().getCommunicationEmail()) ? subscription.getBuyer().getCommunicationEmail() : null));
				params = b.build();
			} else if (StringUtils.equals(paymentMode, "card")) {
				if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
					amount.subtract(subscription.getPromoCodeDiscount());
				}
				Builder b = PaymentIntentCreateParams.builder().setCurrency(paymentTransaction.getCurrencyCode()).setAmount(amount.multiply(new BigDecimal(100)).longValue()).setDescription(description).addPaymentMethodType("card").setReceiptEmail((StringUtils.isNotBlank(subscription.getBuyer().getCommunicationEmail()) ? subscription.getBuyer().getCommunicationEmail() : null));
				params = b.build();
			}
			PaymentIntent paymentIntent = PaymentIntent.create(params);
			PaymentIntentPojo response = new PaymentIntentPojo();
			response.setId(paymentIntent.getId());
			response.setClientSecret(paymentIntent.getClientSecret());
			response.setAmount(paymentIntent.getAmount());
			response.setCreatedAt(paymentIntent.getCreated());
			return response;

		} catch (Exception e) {
			LOG.error("Error during Stripe Service Call : " + e.getMessage(), e);
			throw new ApplicationException(e.getLocalizedMessage());
		}
	}

	@Override
	@Transactional(readOnly = false)
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
	@Transactional(readOnly = false)
	public PaymentIntentPojo initiateStripePaymentForUpdateBuyerSubscription(BuyerSubscription subscription, String planId, PaymentTransaction paymentTransaction, Integer userEventQuantity, String promoCode, String paymentMode) throws ApplicationException {
		try {
			BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
			if (plan == null) {
				throw new ApplicationException("No Plan available.");
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
			if (paymentTransaction.getTotalPriceAmount() != null) {
				paymentAmount = paymentTransaction.getTotalPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
			}

			LOG.info("paymentAmount : " + paymentAmount);
			// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
			String currencyCodeType = plan.getCurrency().getCurrencyCode();
			String paymentType = paymentMode;

			// Create an instance of PaymentTransaction at the beginning of payment flow.
			if (CollectionUtil.isNotEmpty(subscription.getPaymentTransactions())) {
				if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
					PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(paymentTransaction.getPromoCode().getId());
					paymentTransaction.setPromoCode(promotionalCode);
				}
				paymentTransaction.setBuyerPlan(plan);
				paymentTransaction.setCreatedDate(new Date());
				paymentTransaction.setCurrencyCode(currencyCodeType);
				paymentTransaction.setPaymentMethod((StringUtils.equals("fpx", paymentType) ? PaymentMethod.BANK_TRANSFER : StringUtils.equals("card", paymentType) ? PaymentMethod.CARD : PaymentMethod._UNKNOWN));
				paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
				paymentTransaction.setType(TransactionType.PAYMENT);
				paymentTransaction = savePaymentTransaction(paymentTransaction);
			}

			String note = "Update Plan: " + buyer.getCompanyName() + " is subscribing for ";
			String paymentRemarks = "Payment for ";
			int quantity = 1;

			String gstDescription = "";
			String gstDesc = "";
			if (tax != null && tax.floatValue() > 0) {
				gstDescription = " (Inclusive of " + tax + "% SST)";
				gstDesc = " (Inclusive of " + tax + "% SST)";
			} else {
				gstDescription = " (Inclusive of " + 0 + "% SST)";
				gstDesc = " (Inclusive of " + 0 + "% SST)";
			}

			if (plan.getPlanType() == PlanType.PER_USER) {
				note += userEventQuantity + " users for ";
				paymentRemarks += userEventQuantity + " users for ";
				if (subscription.getPlanPeriod().getPlanDuration() == 12) {
					note += " 1 Year " + gstDesc;
					paymentRemarks += " 1 Year " + gstDescription;
				} else {
					note += subscription.getPlanPeriod().getPlanDuration() + " months " + gstDesc;
					paymentRemarks += subscription.getPlanPeriod().getPlanDuration() + " months " + gstDescription;
				}
				quantity = subscription.getPlanPeriod().getPlanDuration();
			} else {
				note += userEventQuantity + " events for unlimited users " + gstDesc;
				paymentRemarks += userEventQuantity + " events for unlimited users " + gstDescription;
			}

			if (paymentTransaction != null && paymentTransaction.getPromoCode() != null) {
				LOG.info("Adding promo description.....");
				String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? paymentTransaction.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP) + " " + (currencyCodeType != null ? currencyCodeType : "") + " OFF" : paymentTransaction.getPromoCode().getPromoDiscount() + "% OFF";
				note += " " + promoDesc;
			}

			BigDecimal totalTax = new BigDecimal(0);

			BigDecimal a = paymentTransaction.getPriceAmount().subtract(paymentTransaction.getPromoCodeDiscount());
			totalTax = (a.multiply(tax)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
			LOG.info("totalTax :" + totalTax);
			BigDecimal rangePrice = subscription.getRange().getPrice();
			rangePrice = rangePrice.setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal itemAmount = BigDecimal.ZERO;
			if (plan.getBasePrice() != null) {
				itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP);
			}

			itemAmount.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP);
			itemAmount.add(totalTax);
			if (subscription.getPlanPeriod() != null && paymentTransaction.getPriceDiscount() != null) {
				LOG.info("subscription discount : " + paymentTransaction.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
				itemAmount.subtract(paymentTransaction.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			}
			if (paymentTransaction != null && paymentTransaction.getPromoCode() != null) {
				LOG.info("promo discount : " + paymentTransaction.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
				itemAmount.subtract(paymentTransaction.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			}

			LOG.info("Making payment with type " + paymentType + " for amount " + itemAmount + " with note " + note);
			PaymentIntentPojo response = invokeStripePayment(paymentType, paymentAmount, note, subscription, paymentTransaction);
			paymentTransaction.setTotalPriceAmount(paymentAmount);
			paymentTransaction.setAmount(paymentAmount);
			subscription = saveBuyerSubscription(subscription);
			LOG.info("Subscription ID is " + subscription.getId() + " with user quantity " + subscription.getUserQuantity());
			// Save the TOKEN in DB
			paymentTransaction.setBuyerSubscription(subscription);
			paymentTransaction.setPaymentToken(response.getId());
			paymentTransaction.setRemarks(paymentRemarks);
			paymentTransaction.setAdditionalTaxDesc("SST " + tax + "%");
			paymentTransaction.setAdditionalTax(totalTax);
			if (subscription.getBuyer() != null) {
				paymentTransaction.setCompanyName(subscription.getBuyer().getCompanyName());
				paymentTransaction.setCountry(subscription.getBuyer().getRegistrationOfCountry());
			}
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
			return response;
		} catch (Exception e) {
			LOG.error("Error Initiating Stripe payment: ", e);
			paymentTransaction.setStatus(TransactionStatus.FAILURE);
			paymentTransaction.setErrorCode("-1");
			paymentTransaction.setErrorMessage("Error Initiating Stripe payment");
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
			throw new ApplicationException(e.getLocalizedMessage());
		}
	}

	@Override
	public PaymentTransaction getPaymentTransactionByToken(String paymentTransactionId) {
		return paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentTransactionId);
	}

	@Override
	@Transactional(readOnly = false)
	public PaymentIntentPojo initiateStripePaymentForBuyerSubscriptionChangePlan(BuyerSubscription subscription, String planId, String mode) throws ApplicationException {
		PaymentTransaction paymentTransaction = new PaymentTransaction();
		try {
			BuyerPlan plan = buyerPlanService.getBuyerPlanForEditById(planId);
			if (plan == null) {
				throw new ApplicationException("Plan cannot be null");
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
			// The currencyCodeType and paymentType are set to the selections made on the Integration Assistant
			String currencyCodeType = plan.getCurrency().getCurrencyCode();
			String paymentType = mode;

			// Create an instance of PaymentTransaction at the beginning of payment flow.
			if (CollectionUtil.isNotEmpty(subscription.getPaymentTransactions())) {
				paymentTransaction = subscription.getPaymentTransactions().get(0);

				if (paymentTransaction.getTotalPriceAmount() != null) {
					paymentAmount = paymentTransaction.getTotalPriceAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
				}

				LOG.info("paymentAmount : " + paymentAmount);

				if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
					PromotionalCode promotionalCode = promotionalCodeService.getPromotionalCodeByPromoCodeId(paymentTransaction.getPromoCode().getId());
					paymentTransaction.setPromoCode(promotionalCode);
				}
				paymentTransaction.setBuyerPlan(plan);
				paymentTransaction.setCreatedDate(new Date());
				LOG.info("currencyCodeType " + currencyCodeType);
				paymentTransaction.setCurrencyCode(currencyCodeType);
				paymentTransaction.setPaymentMethod((StringUtils.equals("fpx", paymentType) ? PaymentMethod.BANK_TRANSFER : StringUtils.equals("card", paymentType) ? PaymentMethod.CARD : PaymentMethod._UNKNOWN));
				paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
				paymentTransaction.setType(TransactionType.PAYMENT);

				if (subscription.getBuyer() != null) {
					paymentTransaction.setCompanyName(subscription.getBuyer().getCompanyName());
					paymentTransaction.setCountry(subscription.getBuyer().getRegistrationOfCountry());
				}
				paymentTransaction = savePaymentTransaction(paymentTransaction);
			}
			String note = "Change Plan: " + buyer.getCompanyName() + " is subscribing for ";
			String paymentRemarks = "Payment for ";
			int userEventQuantity = 0;
			int quantity = 1;

			String gstDescription = "";
			String gstDesc = "";
			if (tax != null && tax.floatValue() > 0) {
				gstDescription = " (Inclusive of " + tax + "% SST)";
				gstDesc = " (Inclusive of " + tax + "% SST)";
			} else {
				gstDescription = " (Inclusive of " + 0 + "% SST)";
				gstDesc = " (Inclusive of " + 0 + "% SST)";
			}
			if (plan.getPlanType() == PlanType.PER_USER) {

				if (plan.getBasePrice() != null && plan.getBaseUsers() > paymentTransaction.getUserQuantity()) {
					LOG.info("UserQuantity: " + userEventQuantity + "=====Base Users: " + plan.getBaseUsers());
					paymentTransaction.setUserQuantity(plan.getBaseUsers());
				}

				note += paymentTransaction.getUserQuantity() + " users for ";
				paymentRemarks += paymentTransaction.getUserQuantity() + " users for ";

				if (subscription.getPlanPeriod().getPlanDuration() == 12) {
					note += " 1 Year " + gstDesc;
					paymentRemarks += " 1 Year " + gstDescription;
				} else {
					note += subscription.getPlanPeriod().getPlanDuration() + " months " + gstDesc;
					paymentRemarks += subscription.getPlanPeriod().getPlanDuration() + " months " + gstDescription;
				}
				userEventQuantity = paymentTransaction.getUserQuantity();
				quantity = subscription.getPlanPeriod().getPlanDuration();
			} else {
				note += paymentTransaction.getEventQuantity() + " events for unlimited users " + gstDesc;
				paymentRemarks += paymentTransaction.getEventQuantity() + " events for unlimited users " + gstDescription;
				userEventQuantity = paymentTransaction.getEventQuantity();
			}

			if (paymentTransaction != null && paymentTransaction.getPromoCode() != null) {
				String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? paymentTransaction.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP) + " " + (currencyCodeType != null ? currencyCodeType : "") + " OFF" : paymentTransaction.getPromoCode().getPromoDiscount() + "% OFF";
				note += " " + promoDesc;
			}

			// BigDecimal totalTax = new BigDecimal(0);
			// totalTax = (paymentAmount.multiply(tax)).divide(new BigDecimal(100)).setScale(2,
			// BigDecimal.ROUND_HALF_UP);
			// LOG.info("totalTax :" + totalTax);
			// paymentAmount.add(totalTax)
			// Construct the parameter string that describes the PayPal payment.
			if (Boolean.TRUE == subscription.getAutoChargeSubscription()) {
				// Billing type
				// nvpstr += "&L_BILLINGTYPE0=RecurringPayments";
				// Billing Agreement Desc
				// nvpstr += "&L_BILLINGAGREEMENTDESCRIPTION0=" + "Payment for " + plan.getPlanName() + " plan at
				// procurehere.com ";
			}

			LOG.info("paymentAmount :" + paymentAmount);
			BigDecimal rangePrice = subscription.getRange().getPrice();
			rangePrice = rangePrice.setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal itemAmount = BigDecimal.ZERO;
			if (plan.getBasePrice() != null) {
				itemAmount = plan.getBasePrice().setScale(2, BigDecimal.ROUND_HALF_UP);
				if (plan.getBaseUsers() < userEventQuantity) {
					userEventQuantity = userEventQuantity - plan.getBaseUsers();
					itemAmount = itemAmount.add(((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
				LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);
			} else {
				itemAmount = ((rangePrice).multiply(new BigDecimal(userEventQuantity))).setScale(2, BigDecimal.ROUND_HALF_UP);
			}

			LOG.info("itemAmount : " + itemAmount + " quantity :" + quantity);
			// itemAmount.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP)
			// LOG.info("tax on individual totalTax :" + totalTax);

			if (subscription.getPlanPeriod() != null && paymentTransaction.getPriceDiscount() != null) {
				itemAmount.subtract(paymentTransaction.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
				LOG.info("subscription discount : " + paymentTransaction.getPriceDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			}
			if (paymentTransaction != null && paymentTransaction.getPromoCode() != null) {
				itemAmount.subtract((paymentTransaction.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate()));
				LOG.info("promo discount : " + paymentTransaction.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP).negate());
			}

			LOG.info("Making payment for " + plan.getPlanName() + " with currency code " + plan.getCurrency().getCurrencyCode() + " for amount " + itemAmount + " and description " + note + " with mode " + paymentType);
			PaymentIntentPojo response = invokeStripePayment(paymentType, paymentAmount, note, subscription, paymentTransaction);
			subscription.setBuyer(null);
			subscription.setPlan(plan);
			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);

			// subscription.setPriceAmount(((itemAmount).multiply(new BigDecimal(quantity))).setScale(2,
			// BigDecimal.ROUND_HALF_UP));
			subscription.setPaymentTransactions(Arrays.asList(paymentTransaction));
			subscription = saveBuyerSubscription(subscription);

			LOG.info("Saved subscription with ID " + subscription.getId());

			paymentTransaction.setTotalPriceAmount(paymentAmount);
			paymentTransaction.setAmount(paymentAmount);
			paymentTransaction.setBuyerSubscription(subscription);
			paymentTransaction.setPaymentToken(response.getId());
			paymentTransaction.setRemarks(paymentRemarks);
			paymentTransaction.setAdditionalTaxDesc("SST " + tax + "%");
			// paymentTransaction.setAdditionalTax(totalTax);
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
			subscription.setBuyer(buyer);

			return response;
		} catch (Exception e) {
			LOG.error("Error in payment: " + e.getLocalizedMessage(), e);
			paymentTransaction.setStatus(TransactionStatus.FAILURE);
			paymentTransaction.setErrorCode("-1");
			paymentTransaction.setErrorMessage("Error Initiating Stripe payment");
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
			throw new ApplicationException(e.getLocalizedMessage());
		}
	}

	@Override
	@Transactional(readOnly = false)
	public BuyerSubscription saveTrialSubscription(User user, Buyer buyer) throws Exception {

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

}
