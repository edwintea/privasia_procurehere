/**
 * 
 */
package com.privasia.procurehere.service;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.pojo.PaymentTransactionPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Nitin Otageri
 */
public interface PaymentTransactionService {

	/**
	 * @param tableParams
	 * @param startDate TODO
	 * @param endDate TODO
	 * @return
	 */
	List<PaymentTransaction> findPaymentTransactions(TableDataInput tableParams, Date startDate, Date endDate);

	/**
	 * @param paymentTransaction
	 * @return
	 */
	PaymentTransaction save(PaymentTransaction paymentTransaction);

	/**
	 * @param paymentTransaction
	 */
	PaymentTransaction update(PaymentTransaction paymentTransaction);

	/**
	 * @param id
	 * @return
	 */
	PaymentTransaction getPaymentTransactionById(String id);

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
	long findTotalPaymentTransactionForBuyer(String buyerId);

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
	 * @return
	 */
	long findTotalSuccessfulPaymentTransactionForBuyer(String buyerId);

	/**
	 * @param buyerId
	 * @param tableParams
	 * @return
	 */
	long findTotalSuccessfulFilteredPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams);

	/**
	 * @param buyerId
	 * @param tableParams
	 * @return
	 */
	List<PaymentTransaction> findSuccessfulPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams);

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
	 * @param response
	 * @param file
	 * @param paymentTransactionPojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param paymentIds
	 * @param formatter
	 */
	void downloadCsvFileForPaymentTransactionList(HttpServletResponse response, File file, PaymentTransactionPojo paymentTransactionPojo, boolean select_all, Date startDate, Date endDate, String[] paymentIds, DateFormat formatter);

}
