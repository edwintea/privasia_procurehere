/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.BuyerSubscriptionPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author parveen
 */
public interface BuyerSubscriptionService {

	String initiatePaypalPaymentForBuyerSubscription(BuyerSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL) throws ApplicationException;

	/**
	 * @param paymentTransaction
	 * @return
	 */
	PaymentTransaction savePaymentTransaction(PaymentTransaction paymentTransaction);

	/**
	 * @param methodName
	 * @param nvpStr
	 * @return
	 */
	HashMap<String, String> invokePaypalService(String methodName, String nvpStr);

	/**
	 * @param paymentTransaction
	 * @return
	 */
	PaymentTransaction updatePaymentTransaction(PaymentTransaction paymentTransaction);

	/**
	 * @param subscription
	 * @return
	 */
	BuyerSubscription saveBuyerSubscription(BuyerSubscription subscription);

	/**
	 * @param subscriptionId
	 * @return
	 */
	BuyerSubscription getBuyerSubscriptionById(String subscriptionId);

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param paymentTransactionId
	 * @param isSale TODO
	 * @return
	 * @throws ApplicationException
	 */
	User confirmSubscription(String token, String payerId, Model model, String serverName, String paymentTransactionId, boolean isSale) throws ApplicationException;

	/**
	 * @param paymentTransactionId
	 * @return
	 */
	PaymentTransaction getPaymentTransactionById(String paymentTransactionId);

	/**
	 * @param txId
	 * @return
	 */
	PaymentTransaction cancelPaymentTransaction(String txId);

	/**
	 * @param subscription
	 * @param previousSubscription
	 * @throws Exception
	 */
	void doComputeSubscription(BuyerSubscription subscription, BuyerSubscription previousSubscription) throws Exception;

	/**
	 * @param subscription
	 * @return
	 */
	BuyerSubscription updateSubscription(BuyerSubscription subscription);

	/**
	 * @param buyerId
	 * @return
	 */
	PlanType getBuyerSubscriptionPlanTypeByBuyerID(String buyerId);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<BuyerSubscription> findBuyerSubscriptionHistoryForBuyer(String tenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredBuyerSubscriptionHistoryForBuyer(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalBuyerSubscriptionHistoryForBuyer(String tenantId);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	BuyerSubscription getCurrentBuyerSubscriptionForBuyer(String loggedInUserTenantId);

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param txId
	 * @return
	 * @throws Exception
	 */
	HashMap<String, String> confirmSubscriptionRenew(String token, String payerId, Model model, String serverName, String txId) throws Exception;

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	BuyerSubscription getLastBuyerSubscriptionForBuyer(String loggedInUserTenantId);

	/**
	 * @param subscriptionId
	 * @return
	 */
	BuyerSubscription getBuyerSubscriptionByIdForUpdateSubs(String subscriptionId);

	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param returnURL
	 * @param cancelURL
	 * @param paymentTransaction
	 * @param quantity TODO
	 * @return
	 */
	String initiatePaypalPaymentForUpdateBuyerSubscription(BuyerSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL, PaymentTransaction paymentTransaction, int quantity);

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param paymentTransactionId
	 * @param subscription TODO
	 * @return
	 * @throws Exception
	 */
	HashMap<String, String> confirmSubscriptionUpdate(String token, String payerId, Model model, String serverName, String paymentTransactionId, BuyerSubscription subscription, PaymentTransaction paymentTransaction) throws Exception;

	/**
	 * @param subscription
	 * @param planId
	 * @param session
	 * @param returnURL
	 * @param cancelURL
	 * @return
	 */
	String initiatePaypalPaymentForBuyerSubscriptionChangePlan(BuyerSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL);

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param txId
	 * @return
	 * @throws Exception
	 */
	HashMap<String, String> confirmSubscriptionChangePlan(String token, String payerId, Model model, String serverName, String txId) throws Exception;

	/**
	 * @param subscription
	 * @param previousSubscription
	 */
	void doComputeSubscriptionChangePlan(BuyerSubscription subscription, BuyerSubscription previousSubscription);

	/**
	 * @param token
	 * @param payerId
	 * @param model
	 * @param serverName
	 * @param paymentTransactionId
	 * @param subscription
	 * @param paymentTransaction
	 */
	void createRecurringPaymentsSubscription(String token, BuyerSubscription subscription, PaymentTransaction paymentTransaction);

	/**
	 * @param profileId
	 * @return
	 */
	BuyerSubscription getActiveBuyerSubscriptionByPaymentProfileId(String profileId);

	/**
	 * @param txId
	 * @return
	 */
	PaymentTransaction errorPaymentTransaction(String txId);

	/**
	 * @param buyer
	 * @return
	 * @throws ApplicationException 
	 */
	User saveTrialBuyer(Buyer buyer) throws ApplicationException;

	/**
	 * @param buyer
	 * @param planId
	 * @param session
	 * @param returnURL
	 * @param cancelURL
	 * @return
	 */
	String initiatePaypalPaymentForBuyerTrialSubscription(BuyerSubscription buyerSubscription, HttpSession session, String returnURL, String cancelURL);

	/**
	 * @param paymentTransactionId
	 * @return
	 * @throws ApplicationException
	 *//*
	HashMap<String, String> capturePayment(String paymentTransactionId) throws ApplicationException;
*/
	/**
	 * @param paymentTransactionId
	 * @param payerId
	 * @param serverName
	 * @return
	 * @throws ApplicationException
	 */
	HashMap<String, String> authorizeOrderPayment(String paymentTransactionId, String payerId, String serverName) throws ApplicationException;

	void doCapturePayment(String subscriptionId) throws ApplicationException;

	/**
	 * 
	 * @param response
	 * @param loggedInUserTenantId
	 */
	void downloadSubscriptionHistoryExcel(HttpServletResponse response, String loggedInUserTenantId);

	/**
	 * 
	 * @param response
	 * @param loggedInUserTenantId
	 */
	void downloadPaymentTransactionExcel(HttpServletResponse response, String loggedInUserTenantId);

	String initiatePaypalPaymentForBuyBuyerSubscription(BuyerSubscription subscription, String planId, HttpSession session, String returnURL, String cancelURL) throws ApplicationException;

	List<BuyerSubscriptionPojo> getBuyerSubscriptionFutureplan(String tenantId);

}
