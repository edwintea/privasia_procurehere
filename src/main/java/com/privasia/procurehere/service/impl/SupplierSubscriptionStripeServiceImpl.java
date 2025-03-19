package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.PaymentTransactionDao;
import com.privasia.procurehere.core.dao.SupplierSubscriptionDao;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.PromotionalCode;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.enums.PaymentMethod;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.enums.TransactionType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PromotionalCodeService;
import com.privasia.procurehere.service.SupplierPlanService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.SupplierSubscriptionStripeService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentCreateParams.Builder;

@Service
@Transactional(readOnly = true)
public class SupplierSubscriptionStripeServiceImpl implements SupplierSubscriptionStripeService {

	private static Logger LOG = LogManager.getLogger(Global.SUBSCRIPTION_LOG);

	@Autowired
	PromotionalCodeService promotionalCodeService;

	@Autowired
	PaymentTransactionDao paymentTransactionDao;

	@Autowired
	SupplierSubscriptionDao supplierSubscriptionDao;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	SupplierPlanService supplierPlanService;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Value("${app.url}")
	String APP_URL;

	@Value("${stripe.secret}")
	String secretKey;

	@Value("${stripe.publish}")
	String publishKey;

	@Override
	@Transactional(readOnly = false)
	public PaymentIntentPojo initiateSupplierRegistrationStripePayment(SupplierSubscription subscription, String paymentMode) throws ApplicationException {
		PaymentTransaction paymentTransaction = subscription.getPaymentTransaction();
		try {
			SupplierPlan plan = subscription.getSupplierPlan();
			if (plan == null) {
				throw new ApplicationException("No Plan selected");
			}
			// The paymentAmount is the total value of the shopping cart
			BigDecimal paymentAmount = subscription.getTotalPriceAmount();
			BigDecimal planAmount = new BigDecimal(plan.getPrice());
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

			String currencyCodeType = plan.getCurrency().getCurrencyCode();
			String paymentType = paymentMode;

			// if (paymentTransaction.getPromoCode() != null &&
			// StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
			// LOG.info("Subscription has promo code : " + paymentTransaction.getPromoCode() + " with discount: " +
			// subscription.getPromoCodeDiscount() + " and ID " + paymentTransaction.getPromoCode().getId());
			// PromotionalCode promotionalCode =
			// promotionalCodeService.getPromotionalCodeByPromoCodeId(paymentTransaction.getPromoCode().getId());
			// paymentTransaction.setPromoCode(promotionalCode);
			// }

			BigDecimal amount = paymentAmount;
			amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal totalTax = (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
			String gstDesc = " (Inclusive of " + tax + " % SST)";
			String description = "Procurehere Supplier New Plan: " + supplier.getCompanyName() + " is subscribing for " + plan.getPeriod() + " " + plan.getPeriodUnit() + "(S)" + gstDesc;

			if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
				String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP) + " " + (currencyCodeType != null ? currencyCodeType : "") + " OFF" : paymentTransaction.getPromoCode().getPromoDiscount() + "% OFF";
				description += promoDesc;
			}

			paymentTransaction.setSupplierPlan(plan);
			paymentTransaction.setCreatedDate(new Date());
			paymentTransaction.setCurrencyCode(currencyCodeType);
			paymentTransaction.setPaymentMethod((StringUtils.equals("fpx", paymentType) ? PaymentMethod.BANK_TRANSFER : StringUtils.equals("card", paymentType) ? PaymentMethod.CARD : PaymentMethod._UNKNOWN));
			paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
			paymentTransaction.setType(TransactionType.PAYMENT);
			paymentTransaction.setAmount(paymentAmount);
			paymentTransaction.setCompanyRegistrationNumber(subscription.getSupplier().getCompanyRegistrationNumber());
			paymentTransaction.setCompanyName(subscription.getSupplier().getCompanyName());
			paymentTransaction.setCommunicationEmail(subscription.getSupplier().getCommunicationEmail());
			paymentTransaction.setLoginEmail(subscription.getSupplier().getLoginEmail());
			paymentTransaction.setDesignation(supplier.getDesignation());
			paymentTransaction.setCompanyContactNumber(subscription.getSupplier().getCompanyContactNumber());
			paymentTransaction.setFullName(subscription.getSupplier().getFullName());
			paymentTransaction.setPassword(subscription.getSupplier().getPassword());
			paymentTransaction.setRemarks(subscription.getSupplier().getRemarks());
			paymentTransaction.setMobileNumber(subscription.getSupplier().getMobileNumber());
			paymentTransaction.setCountry(subscription.getSupplier().getRegistrationOfCountry());
			paymentTransaction.setPriceAmount(planAmount);
			paymentTransaction.setAmount(amount.add(totalTax).subtract(paymentTransaction.getPromoCodeDiscount()));
			paymentTransaction.setTotalPriceAmount(amount.add(totalTax).subtract(paymentTransaction.getPromoCodeDiscount()));
			paymentTransaction.setAdditionalTaxDesc("SST " + tax + "%");
			paymentTransaction = savePaymentTransaction(paymentTransaction);

			LOG.info("Making payment for " + plan.getPlanName() + " with currency code " + plan.getCurrency().getCurrencyCode() + " for amount " + amount + " with tax " + totalTax + " and description " + description + " with mode " + paymentType);
			PaymentIntentPojo response = invokeStripePayment(paymentType, amount.add(totalTax), description, subscription, paymentTransaction);
			LOG.info("Updating payment token for payment transaction: " + response.getId());
			LOG.info("Got supplier ID as : " + supplier.getId());
			if (supplier != null && StringUtils.checkString(supplier.getId()).length() > 0) {
				subscription.setSupplier(supplier);
			} else {
				subscription.setSupplier(null);
			}

			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);
			subscription.setPaymentTransaction(paymentTransaction);
			if (subscription.getBuyerLimit() == null) {
				subscription.setBuyerLimit(plan.getBuyerLimit());
			}
			subscription = saveSupplierSubscription(subscription);

			paymentTransaction.setSupplierSubscription(subscription);
			paymentTransaction.setPaymentToken(response.getId());
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
	public PaymentTransaction savePaymentTransaction(PaymentTransaction paymentTransaction) {
		return paymentTransactionDao.saveOrUpdate(paymentTransaction);
	}

	@Override
	@Transactional(readOnly = false)
	public PaymentTransaction updatePaymentTransaction(PaymentTransaction paymentTransaction) {
		return paymentTransactionDao.update(paymentTransaction);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierSubscription saveSupplierSubscription(SupplierSubscription subscription) {
		return supplierSubscriptionDao.saveOrUpdate(subscription);
	}

	@Override
	public PaymentIntentPojo invokeStripePayment(String paymentMode, BigDecimal amount, String description, SupplierSubscription subscription, PaymentTransaction paymentTransaction) throws ApplicationException {
		try {
			if (StringUtils.isBlank(secretKey)) {
				throw new ApplicationException("No Stripe key found. Please provide a valid stripe secret key.");
			}
			Stripe.apiKey = secretKey;
			PaymentIntentCreateParams params = null;
			if (StringUtils.equals(paymentMode, "fpx")) {
				if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
					amount = amount.subtract(subscription.getPromoCodeDiscount());
				}
				Builder b = PaymentIntentCreateParams.builder().setAmount(amount.multiply(new BigDecimal(100)).longValue()).setCurrency("myr").setDescription(description).addPaymentMethodType("fpx").setReceiptEmail(subscription.getPaymentTransaction().getCommunicationEmail());
				params = b.build();
			} else if (StringUtils.equals(paymentMode, "card")) {
				if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
					LOG.info("Subscrating amount....." + subscription.getPromoCodeDiscount());
					amount = amount.subtract(subscription.getPromoCodeDiscount());
				}

				LOG.info("Amount is...." + amount);
				Builder b = PaymentIntentCreateParams.builder().setCurrency(paymentTransaction.getCurrencyCode()).setAmount(amount.multiply(new BigDecimal(100)).longValue()).setDescription(description).addPaymentMethodType("card").setReceiptEmail(subscription.getPaymentTransaction().getCommunicationEmail());
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
			LOG.info("Error during Stripe Service Call : " + e.getMessage(), e);
			throw new ApplicationException(e.getLocalizedMessage());
		}
	}

	@Override
	public SupplierSubscription getSupplierSubscriptionById(String subscriptionId) {
		return supplierSubscriptionDao.getSubscriptionById(subscriptionId);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierSubscription updateSupplierSubscription(SupplierSubscription subscription) {
		return supplierSubscriptionDao.update(subscription);
	}

	@Override
	@Transactional(readOnly = false)
	public PaymentIntentPojo initiateStripePaymentForRenewSupplier(SupplierSubscription subscription, String planId, String supplierId, String promoCode, String mode, boolean renew) throws ApplicationException {
		PaymentTransaction paymentTransaction = null;
		try {
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

			paymentTransaction = subscription.getPaymentTransaction();
			int paymentAmount = plan.getPrice();
			int planAmount = plan.getPrice();
			BigDecimal promoDiscountPrice = BigDecimal.ZERO;
			PromotionalCode promo = null;
			BigDecimal amount = new BigDecimal(paymentAmount);
			amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
			String currencyCodeType = plan.getCurrency().getCurrencyCode();
			String paymentType = mode;
			BigDecimal totalTax = (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

			paymentTransaction.setSupplierPlan(plan);
			paymentTransaction.setCreatedDate(new Date());
			paymentTransaction.setCurrencyCode(currencyCodeType);
			paymentTransaction.setPaymentMethod((StringUtils.equals("fpx", paymentType) ? PaymentMethod.BANK_TRANSFER : StringUtils.equals("card", paymentType) ? PaymentMethod.CARD : PaymentMethod._UNKNOWN));
			paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
			paymentTransaction.setType(TransactionType.PAYMENT);
			paymentTransaction.setCompanyRegistrationNumber(subscription.getSupplier().getCompanyRegistrationNumber());
			paymentTransaction.setCompanyName(subscription.getSupplier().getCompanyName());
			paymentTransaction.setCountry(subscription.getSupplier() != null ? subscription.getSupplier().getRegistrationOfCountry() : null);
			paymentTransaction.setSupplier(subscription.getSupplier());
			paymentTransaction.setCommunicationEmail(subscription.getSupplier().getCommunicationEmail());
			paymentTransaction.setLoginEmail(subscription.getSupplier().getLoginEmail());
			paymentTransaction.setAdditionalTaxDesc("SST " + tax + "%");
			paymentTransaction.setPriceAmount(new BigDecimal(planAmount));
			paymentTransaction.setAmount(amount.add(totalTax));
			paymentTransaction.setAdditionalTax(totalTax);
			paymentTransaction.setTotalPriceAmount(amount.add(totalTax));
			paymentTransaction = savePaymentTransaction(paymentTransaction);

			BigDecimal amountWithTax = amount.add(totalTax);
			if (!StringUtils.checkString(promoCode).isEmpty()) {
				try {
					promotionalCodeService.validatePromoCode(promoCode, plan, null, amountWithTax, TenantType.SUPPLIER, SecurityLibrary.getLoggedInUserTenantId());
					promo = promotionalCodeService.getPromotionalCodePromoCode(promoCode);
					promoDiscountPrice = getPromoDiscount(promo, amountWithTax, plan);
					paymentAmount = (BigDecimal.valueOf(paymentAmount).subtract(promoDiscountPrice).setScale(2, BigDecimal.ROUND_HALF_UP)).intValue();
					subscription.setPromoCode(promoCode);
					subscription.setPromoCodeDiscount(promoDiscountPrice);
					subscription.setTotalPriceAmount(amountWithTax.subtract(promoDiscountPrice));
					paymentTransaction.setPromoCodeDiscount(promoDiscountPrice);
					paymentTransaction.setPromoCode(promo);
				} catch (Exception e) {
					LOG.error("error while adding promocode value ", e);
					throw new ApplicationException(e.getMessage());
				}
			}

			String gstDesc = " (Inclusive of " + tax + "% SST)";

			Supplier s = supplierService.findSupplierSubscriptionDetailsBySupplierId(supplier.getId());

			if (s != null) {
				if (s.getSupplierSubscription() == null || s.getSupplierSubscription().getSupplierPlan() == null) {
					renew = false;
				} else if (s.getSupplierSubscription() != null && s.getSupplierSubscription().getSupplierPlan() != null) {
					renew = true;
				}
			}

			String description = (renew ? "Procurehere Supplier Renew Plan: " : "Procurehere Supplier New Plan: ") + supplier.getCompanyName() + " is subscribing for " + plan.getPeriod() + " " + plan.getPeriodUnit() + "(S)" + gstDesc;

			if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
				String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP) + " " + (currencyCodeType != null ? currencyCodeType : "") + " OFF" : paymentTransaction.getPromoCode().getPromoDiscount() + "% OFF";
				description += " " + promoDesc;
			}

			paymentTransaction.setPriceAmount(new BigDecimal(planAmount));
			paymentTransaction.setAmount(amount.add(totalTax).subtract(promoDiscountPrice));
			paymentTransaction.setAdditionalTax(totalTax);
			paymentTransaction.setTotalPriceAmount(amount.add(totalTax).subtract(promoDiscountPrice));
			paymentTransaction = savePaymentTransaction(paymentTransaction);

			LOG.info("Initiating payment: " + description);
			PaymentIntentPojo response = invokeStripePayment(paymentType, amount.add(totalTax), description, subscription, paymentTransaction);
			LOG.info("Updating payment token for payment transaction: " + response.getId());

			if (supplier.getId() == null) {
				subscription.setSupplier(null);
			}

			subscription.setCreatedDate(new Date());
			subscription.setCurrencyCode(currencyCodeType);
			subscription.setPaymentTransaction(paymentTransaction);
			if (subscription.getBuyerLimit() == null) {
				subscription.setBuyerLimit(plan.getBuyerLimit());
			}

			subscription = saveSupplierSubscription(subscription);
			paymentTransaction.setSupplierSubscription(subscription);
			paymentTransaction.setPaymentToken(response.getId());
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
			subscription.setSupplier(supplier);
			return response;
		} catch (Exception e) {
			if (paymentTransaction != null) {
				paymentTransaction.setStatus(TransactionStatus.FAILURE);
				paymentTransaction.setErrorCode("-1");
				paymentTransaction.setErrorMessage("Error Initiating Stripe payment");
				paymentTransaction = updatePaymentTransaction(paymentTransaction);
			}
			throw new ApplicationException(e.getLocalizedMessage());
		}
	}

	private BigDecimal getPromoDiscount(PromotionalCode promo, BigDecimal totalPrice, SupplierPlan plan) throws Exception {
		BigDecimal promoDiscountPrice = BigDecimal.ZERO;
		LOG.info("Getting discount for amount : " + totalPrice);
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

		} else {
			throw new ApplicationException("Invalid Promo Code");
		}
		return promoDiscountPrice;
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
	public PaymentIntentPojo initiateSupplierStripePayment(SupplierSubscription subscription, String planId, String paymentMode) throws ApplicationException {
		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);
		// The paymentAmount is the total value of the shopping cart
		int paymentAmount = plan.getPrice();
		int planAmount = plan.getPrice();

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
		String currencyCodeType = plan.getCurrency().getCurrencyCode();
		String paymentType = paymentMode;
		// Create an instance of PaymentTransaction at the beginning of payment flow.
		PaymentTransaction paymentTransaction = subscription.getPaymentTransaction();

		BigDecimal amount = new BigDecimal(paymentAmount);
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal totalTax = (amount.multiply(tax).setScale(2, BigDecimal.ROUND_HALF_UP)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
		String gstDesc = " (Inclusive of " + tax + "% SST)";
		String description = "Procurehere Supplier New Plan: " + supplier.getCompanyName() + " is subscribing for " + plan.getPeriod() + " " + plan.getPeriodUnit() + "(S)" + gstDesc;

		if (paymentTransaction != null && paymentTransaction.getPromoCode() != null && subscription.getPromoCodeDiscount() != null) {
			String promoDesc = paymentTransaction.getPromoCode().getDiscountType() == ValueType.VALUE ? subscription.getPromoCodeDiscount().setScale(2, BigDecimal.ROUND_HALF_UP) + " " + (currencyCodeType != null ? currencyCodeType : "") + " OFF" : paymentTransaction.getPromoCode().getPromoDiscount() + "% OFF";
			description += " " + promoDesc;
		}

		paymentTransaction.setSupplierPlan(plan);
		paymentTransaction.setCreatedDate(new Date());
		paymentTransaction.setCurrencyCode(currencyCodeType);
		paymentTransaction.setPaymentMethod((StringUtils.equals("fpx", paymentType) ? PaymentMethod.BANK_TRANSFER : StringUtils.equals("card", paymentType) ? PaymentMethod.CARD : PaymentMethod._UNKNOWN));
		paymentTransaction.setStatus(TransactionStatus.IN_PROGRESS);
		paymentTransaction.setType(TransactionType.PAYMENT);
		paymentTransaction.setCompanyRegistrationNumber(subscription.getSupplier().getCompanyRegistrationNumber());
		paymentTransaction.setCompanyName(subscription.getSupplier().getCompanyName());
		paymentTransaction.setCommunicationEmail(subscription.getSupplier().getCommunicationEmail());
		paymentTransaction.setSupplier(subscription.getSupplier());
		paymentTransaction.setAdditionalTaxDesc("SST " + tax + "%");
		paymentTransaction.setAdditionalTax(totalTax);
		paymentTransaction.setPriceAmount(new BigDecimal(planAmount));
		paymentTransaction.setTotalPriceAmount(amount.add(totalTax));
		paymentTransaction.setAmount(amount.add(totalTax));
		paymentTransaction = savePaymentTransaction(paymentTransaction);

		LOG.info("Making payment for " + plan.getPlanName() + " with currency code " + plan.getCurrency().getCurrencyCode() + " for amount " + amount + " with tax " + totalTax + " and description " + description + " with mode" + paymentType);
		try {
			PaymentIntentPojo response = invokeStripePayment(paymentType, amount.add(totalTax), description, subscription, paymentTransaction);
			LOG.info("Updating payment token for payment transaction: " + response.getId());

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
			paymentTransaction.setPaymentToken(response.getId());
			paymentTransaction = updatePaymentTransaction(paymentTransaction);

			// Reassign the values
			subscription.setSupplier(supplier);

			paymentTransaction.setPaymentToken(response.getId());
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
			return response;
		} catch (Exception e) {
			paymentTransaction.setStatus(TransactionStatus.FAILURE);
			paymentTransaction.setErrorCode("-1");
			paymentTransaction.setErrorMessage("Error Initiating Stripe payment");
			paymentTransaction = updatePaymentTransaction(paymentTransaction);
			throw new ApplicationException(e.getLocalizedMessage());
		}

	}

	@Override
	public String getTimeZoneBySupplierSettings(String suppId, String timeZone) {
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

	@Override
	public void sendSupplierSubscriptionSuccessMailForBuyer(String mailTo, String name, String timeZone, String supplierName) {
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

	@Override
	public String getTimeZoneByBuyerSettings(String tenantId, String timeZone) {
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

	@Override
	@Transactional(readOnly = false)
	public void cancelOldSupplierSubscription(String id) {

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
	public void sendSupplierSubscriptionMail(String mailTo, String name, SupplierSubscription subscription, String timeZone, String msg, String subject, boolean isSuccess) {
		try {
			LOG.info("Got timezone as " + timeZone);
			HashMap<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("msg", msg);
			map.put("userName", name);
			map.put("loginUrl", APP_URL + "/login");

			SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
			// Removing timezone to match date
			// date.setTimeZone(TimeZone.getTimeZone(timeZone));
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

}
