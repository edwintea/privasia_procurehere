package com.privasia.procurehere.service;

import java.math.BigDecimal;

import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;

public interface SupplierSubscriptionStripeService {

	/**
	 * @param subscription
	 * @param secretKey
	 * @param publishKey
	 * @param paymentMode
	 * @param session
	 * @return
	 * @throws ApplicationException
	 */
	PaymentIntentPojo initiateSupplierRegistrationStripePayment(SupplierSubscription subscription, String paymentMode) throws ApplicationException;

	/**
	 * @param paymentMode
	 * @param key
	 * @param amount
	 * @param description
	 * @param subscription
	 * @param paymentTransaction
	 * @return
	 * @throws ApplicationException
	 */
	PaymentIntentPojo invokeStripePayment(String paymentMode, BigDecimal amount, String description, SupplierSubscription subscription, PaymentTransaction paymentTransaction) throws ApplicationException;

	/**
	 * @param paymentTransaction
	 * @return
	 */
	PaymentTransaction savePaymentTransaction(PaymentTransaction paymentTransaction);

	/**
	 * @param paymentTransaction
	 * @return
	 */
	PaymentTransaction updatePaymentTransaction(PaymentTransaction paymentTransaction);

	/**
	 * @param subscription
	 * @return
	 */
	SupplierSubscription saveSupplierSubscription(SupplierSubscription subscription);

	/**
	 * @param subscriptionId
	 * @return
	 */
	SupplierSubscription getSupplierSubscriptionById(String subscriptionId);

	/**
	 * @param subscription
	 * @return
	 */
	SupplierSubscription updateSupplierSubscription(SupplierSubscription subscription);

	/**
	 * @param subscription
	 * @param planId
	 * @param supplierId
	 * @param promoCode
	 * @param mode
	 * @return
	 * @throws ApplicationException
	 */
	PaymentIntentPojo initiateStripePaymentForRenewSupplier(SupplierSubscription subscription, String planId, String supplierId, String promoCode, String mode, boolean renew) throws ApplicationException;

	/**
	 * @param paymentToken
	 * @return
	 * @throws ApplicationException
	 */
	String getPaymentStatus(String paymentToken) throws ApplicationException;

	/**
	 * @param subscription
	 * @param planId
	 * @param paymentMode
	 * @return
	 * @throws ApplicationException
	 */
	PaymentIntentPojo initiateSupplierStripePayment(SupplierSubscription subscription, String planId, String paymentMode) throws ApplicationException;

	/**
	 * @param suppId
	 * @param timeZone
	 * @return
	 */
	String getTimeZoneBySupplierSettings(String suppId, String timeZone);

	/**
	 * @param mailTo
	 * @param name
	 * @param timeZone
	 * @param supplierName
	 */
	void sendSupplierSubscriptionSuccessMailForBuyer(String mailTo, String name, String timeZone, String supplierName);

	/**
	 * @param tenantId
	 * @param timeZone
	 * @return
	 */
	String getTimeZoneByBuyerSettings(String tenantId, String timeZone);

	/**
	 * @param id
	 */
	void cancelOldSupplierSubscription(String id);

	/**
	 * @param mailTo
	 * @param name
	 * @param subscription
	 * @param timeZone
	 * @param msg
	 * @param subject
	 * @param isSuccess
	 */
	void sendSupplierSubscriptionMail(String mailTo, String name, SupplierSubscription subscription, String timeZone, String msg, String subject, boolean isSuccess);

}
