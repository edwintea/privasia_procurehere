package com.privasia.procurehere.service;

import java.math.BigDecimal;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;

public interface BuyerSubscriptionStripeService {

	/**
	 * @param paymentTransaction
	 * @return
	 */
	PaymentTransaction updatePaymentTransaction(PaymentTransaction paymentTransaction);

	/**
	 * @param subscription
	 * @return
	 */
	BuyerSubscription updateSubscription(BuyerSubscription subscription);

	/**
	 * @param subscription
	 * @param previousSubscription
	 */
	void doComputeSubscription(BuyerSubscription subscription, BuyerSubscription previousSubscription);

	/**
	 * @param token
	 * @param subscription
	 * @param paymentTransaction
	 */
	void createRecurringPaymentsSubscription(String token, BuyerSubscription subscription, PaymentTransaction paymentTransaction);

	/**
	 * @param mailTo
	 * @param name
	 * @param subscription
	 * @param msg
	 * @param subject
	 * @param isSuccess
	 * @param paymentTransaction
	 */
	void sendBuyerSubscriptionMail(String mailTo, String name, BuyerSubscription subscription, String msg, String subject, boolean isSuccess, PaymentTransaction paymentTransaction);

	/**
	 * @param subscriptionId
	 * @return
	 */
	BuyerSubscription getBuyerSubscriptionById(String subscriptionId);

	/**
	 * @param token
	 * @param previousSubscription
	 * @param paymentTransaction
	 */
	void cancelRecurringPaymentsSubscription(String token, BuyerSubscription previousSubscription, PaymentTransaction paymentTransaction);

	/**
	 * @param paymentToken
	 * @return
	 * @throws ApplicationException
	 */
	String getPaymentStatus(String paymentToken) throws ApplicationException;

	/**
	 * @param buyer
	 * @param id
	 * @param paymentMode
	 * @return
	 * @throws ApplicationException
	 */
	PaymentIntentPojo initiateStripePaymentForBuyerSubscription(BuyerSubscription buyer, String id, String paymentMode, boolean renew) throws ApplicationException;

	/**
	 * @param subscription
	 * @return
	 */
	BuyerSubscription saveBuyerSubscription(BuyerSubscription subscription);

	/**
	 * @param paymentTransaction
	 * @return
	 */
	PaymentTransaction savePaymentTransaction(PaymentTransaction paymentTransaction);

	/**
	 * @param paymentTransactionId
	 * @return
	 */
	PaymentTransaction getPaymentTransactionById(String paymentTransactionId);

	/**
	 * @param paymentMode
	 * @param amount
	 * @param description
	 * @param subscription
	 * @param paymentTransaction
	 * @return
	 * @throws ApplicationException
	 */
	PaymentIntentPojo invokeStripePayment(String paymentMode, BigDecimal amount, String description, BuyerSubscription subscription, PaymentTransaction paymentTransaction) throws ApplicationException;

	/**
	 * @param buyerSubscription
	 * @param currentSubscription
	 */
	void doComputeSubscriptionChangePlan(BuyerSubscription buyerSubscription, BuyerSubscription currentSubscription);

	/**
	 * @param subscription
	 * @param id
	 * @param paymentTransaction
	 * @param integer
	 * @param promocode
	 * @param paymentMode
	 * @return
	 * @throws ApplicationException
	 */
	PaymentIntentPojo initiateStripePaymentForUpdateBuyerSubscription(BuyerSubscription subscription, String id, PaymentTransaction paymentTransaction, Integer integer, String promocode, String paymentMode) throws ApplicationException;

	/**
	 * @param paymentTransactionId
	 * @return
	 */
	PaymentTransaction getPaymentTransactionByToken(String paymentTransactionId);

	/**
	 * @param subscription
	 * @param planId
	 * @param mode
	 * @return
	 * @throws ApplicationException
	 */
	PaymentIntentPojo initiateStripePaymentForBuyerSubscriptionChangePlan(BuyerSubscription subscription, String planId, String mode) throws ApplicationException;

	/**
	 * @param user
	 * @param buyer
	 * @return
	 * @throws Exception
	 */
	BuyerSubscription saveTrialSubscription(User user, Buyer buyer) throws Exception;

}
