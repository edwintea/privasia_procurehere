/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.Subscription;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierPackage;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;

/**
 * @author Nitin Otageri
 */
public interface SubscriptionService {

	/**
	 * @param subscription
	 * @return
	 */
	Subscription saveSubscription(Subscription subscription);

	/**
	 * @param subscription
	 * @return
	 */
	Subscription updateSubscription(Subscription subscription);

	/**
	 * @param subscriptionId
	 * @return
	 */
	Subscription getSubscriptionById(String subscriptionId);

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
	 * @param paymentTransactionId
	 * @return
	 */
	PaymentTransaction getPaymentTransactionById(String paymentTransactionId);

	/**
	 * @param subscription
	 */
	// void doComputeSubscription(Subscription subscription, Subscription previousSubscription);

	/**
	 * @param buyerId
	 * @return
	 */
	Subscription getCurrentSubscriptionForBuyer(String buyerId);

	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param returnURL
	 * @param cancelURL
	 * @return
	 */
	// String initiatePaypalPayment(Subscription subscription, String planId, HttpSession session, String returnURL,
	// String cancelURL);

	/**
	 * @param methodName
	 * @param nvpStr
	 * @return
	 */
	HashMap<String, String> invokePaypalService(String methodName, String nvpStr);

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param paymentTransactionId
	 * @return
	 * @throws ApplicationException
	 */
	// User confirmSubscription(String token, String payerId, Model model, String serverName, String
	// paymentTransactionId) throws ApplicationException;

	/**
	 * @param txId
	 * @return
	 */
	PaymentTransaction cancelPaymentTransaction(String txId);

	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param returnURL
	 * @param cancelURL
	 * @return
	 */
	// String initiatePaypalPaymentForTopup(Subscription subscription, String planId, String buyerId, HttpSession
	// session, String returnURL, String cancelURL);

	/**
	 * @param subscription
	 * @param planId
	 * @param buyerId
	 * @param session
	 * @param returnURL
	 * @param cancelURL
	 * @return
	 */
	// String initiatePaypalPaymentForRenew(Subscription subscription, String planId, String buyerId, HttpSession
	// session, String returnURL, String cancelURL);

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param paymentTransactionId
	 * @return
	 */
	// HashMap<String, String> confirmSubscriptionRenew(String token, String payerId, Model model, String serverName,
	// String paymentTransactionId);

	/**
	 * @param subscription
	 * @param planId
	 * @param buyerId
	 * @param session
	 * @param returnURL
	 * @param cancelURL
	 * @return
	 */
	// String initiatePaypalPaymentForChangeOfPlan(Subscription subscription, String planId, String buyerId, HttpSession
	// session, String returnURL, String cancelURL);

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param paymentTransactionId
	 * @return
	 */
	// HashMap<String, String> confirmSubscriptionChange(String token, String payerId, Model model, String serverName,
	// String paymentTransactionId);

	/**
	 * @param subscription
	 * @param planId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	// User doTrialSubscription(Subscription subscription, String planId, Model model) throws Exception;

	/**
	 * @param subscription
	 * @return
	 */
	SupplierSubscription saveSupplierSubscription(SupplierSubscription subscription);

	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param returnURL
	 * @param cancelURL
	 * @return
	 */
	String initiateSupplierPaypalPayment(SupplierSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL);

	/**
	 * @param subscriptionId
	 * @return
	 */
	SupplierSubscription getSupplierSubscriptionById(String subscriptionId);

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param paymentTransactionId
	 * @param existingSupplier TODO
	 * @return
	 * @throws ApplicationException
	 */
	void confirmSupplierSubscription(String token, String payerId, Model model, String serverName, String paymentTransactionId, boolean existingSupplier) throws ApplicationException;

	/**
	 * @param subscription
	 * @return
	 */
	SupplierSubscription updateSupplierSubscription(SupplierSubscription subscription);

	/**
	 * @param planId
	 * @return
	 */
	boolean isPlanInUse(String planId);

	/**
	 * @param planId
	 * @return
	 */
	boolean isSupplierPlanInUse(String planId);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	SupplierSubscription getCurrentSubscriptionForSupplier(String loggedInUserTenantId);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	Supplier getSupplierWithSupplierPackagePlanByTenantId(String loggedInUserTenantId);

	/**
	 * @param subscription
	 * @param planId
	 * @param loggedInUserTenantId
	 * @param session
	 * @param returnURL
	 * @param cancelURL
	 * @param promoCode TODO
	 * @return
	 */
	String initiatePaypalPaymentForRenewSupplier(SupplierSubscription subscription, String planId, String loggedInUserTenantId, HttpSession session, String returnURL, String cancelURL, String promoCode);

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param paymentTransactionId
	 * @param b
	 * @throws ApplicationException
	 */
	void confirmRenewSupplierSubscription(String token, String payerId, Model model, String serverName, String paymentTransactionId, boolean b) throws ApplicationException;

	/**
	 * @param loggedInUserTenantId
	 * @return
	 * @throws SubscriptionException
	 */
	boolean checkSupplierExpireSubscription(String loggedInUserTenantId) throws SubscriptionException;

	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param returnURL
	 * @param cancelURL
	 * @return
	 */
	String initiateSupplierOrderPaypalPayment(SupplierSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL);

	void confirmSupplierPaymentSubscription(String token, String payerId, Model model, String serverName, String paymentTransactionId, String paymentType, Boolean isSale) throws ApplicationException;

	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param returnURL
	 * @param cancelURL
	 * @return
	 */
	String initiateSupplierRegistrationPaypalPayment(SupplierSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL);

	/**
	 * @param accepted
	 * @param loggedInUserTenantId
	 * @param buyerId TODO
	 * @return TODO
	 */
	boolean associateBuyerWithSupplier(boolean accepted, String loggedInUserTenantId, String buyerId);

	String initiateSupplierRegistrationPaypalPaymentForBuyPlan(SupplierSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL);

	Supplier getSupplierWithSupplierPackagePlanByTenantIdforBilling(String loggedInUserTenantId);

	List<SupplierSubscription> getSupplierSubscriptionValidity(String supplierId);

	/**
	 * @param sp
	 * @throws ApplicationException
	 */
	SupplierPackage updateSupplierPackage(SupplierPackage sp) throws ApplicationException;

}
