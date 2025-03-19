package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.pojo.PaymentTransactionPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Nitin Otageri
 */
public interface PaymentTransactionDao extends GenericDao<PaymentTransaction, String> {

	/**
	 * @param paymentTransactionId
	 * @return
	 */
	PaymentTransaction getPaymentTransactionById(String paymentTransactionId);

	/**
	 * @param tableParams
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	List<PaymentTransaction> findPaymentTransactions(TableDataInput tableParams, Date startDate, Date endDate);

	/**
	 * @return
	 */
	long findTotalPaymentTransactions();

	/**
	 * @param tableParams
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	long findTotalFilteredPaymentTransactions(TableDataInput tableParams, Date startDate, Date endDate);

	/**
	 * @param buyerId
	 * @return
	 */
	long findTotalPaymentTransactionsForBuyer(String buyerId);

	/**
	 * @param buyerId
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams);

	/**
	 * @param buyerId
	 * @param tableParams
	 * @return
	 */
	List<PaymentTransaction> findPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams);

	/**
	 * @param buyerId
	 * @param tableParams
	 * @return
	 */
	List<PaymentTransaction> findSuccessfulPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams);

	/**
	 * @param buyerId
	 * @return
	 */
	long findTotalSuccessfulPaymentTransactionsForBuyer(String buyerId);

	/**
	 * @param buyerId
	 * @param tableParams
	 * @return
	 */
	long findTotalSuccessfulFilteredPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	List<PaymentTransaction> findSuccessfulPaymentTransactionsForSupplier(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalSuccessfulFilteredPaymentTransactionsForSupplier(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalSuccessfulPaymentTransactionForSupplier(String loggedInUserTenantId);

	/**
	 * @param paymentTransactionId
	 * @return
	 */
	PaymentTransaction getPaymentTransactionWithSupplierPlanByPaymentTransactionId(String paymentTransactionId);

	/**
	 * @param paymentTransactionId
	 * @return
	 */
	PaymentTransaction getPaymentTransactionWithBuyerPlanById(String paymentTransactionId);

	/**
	 * @param subscriptionId
	 * @return
	 */
	PaymentTransaction findPaymentTransactionBySubscriptionId(String subscriptionId);

	List<PaymentTransaction> getAllPaymentTransactionForBuyerForExcel(String buyerId, TransactionStatus status);

	List<PaymentTransaction> getAllPaymentTransactionForSupplierForExcel(String loggedInUserTenantId, TransactionStatus success);

	// PH-1251-1 Stripe payment

	/**
	 * @param token
	 * @return
	 */
	PaymentTransaction findPaymentTransactionByPaymentTokenId(String token);

	/**
	 * @param paymentIds
	 * @param paymentTransactionPojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param object
	 * @return
	 */
	List<PaymentTransactionPojo> getAllPaymentTransactionsForExcelReport(String[] paymentIds, PaymentTransactionPojo paymentTransactionPojo, boolean select_all, Date startDate, Date endDate, Object object);

}
