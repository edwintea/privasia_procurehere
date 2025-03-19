package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;

public interface SupplierEventDetailService {

	List<RftBqItem> getAllBqItemsbyBqId(String bqId);

	/**
	 * @param event
	 * @param rft
	 * @param supplierCompanyName
	 * @param accepted
	 * @param loggedInUser TODO
	 */
	void sendAcceptOrRejectNotifications(Event event, RfxTypes rft, String supplierCompanyName, boolean accepted, User loggedInUser);

	/**
	 * @param event
	 * @param eventType
	 * @param supplierCompanyName
	 * @param isRevisedBQ
	 * @param loggedInUser TODO
	 */
	void sendSubmissionNotifications(Event event, RfxTypes eventType, String supplierCompanyName, boolean isRevisedBQ, User loggedInUser);

	// PH-1567

	/**
	 * @param paymentType
	 * @param eventId
	 * @param eventTypes
	 * @param email
	 * @return
	 * @throws Exception
	 */
	PaymentIntentPojo initiateStripePayment(String paymentType, String eventId, RfxTypes eventTypes, String email) throws Exception;

	/**
	 * @param paymentId
	 * @param emailFlag
	 * @param recipientEmail
	 * @return
	 * @throws ApplicationException
	 */
	PaymentIntentPojo confirmStripePayment(String paymentId, boolean emailFlag, String recipientEmail) throws ApplicationException;

	/**
	 * @param event
	 * @param emailFlag
	 * @throws ApplicationException
	 */
	void handlePaymentWebhookEvent(com.stripe.model.Event event, boolean emailFlag) throws ApplicationException;

	/**
	 * @param feeReference
	 * @param tenantId
	 * @return
	 * @throws ApplicationException
	 */
	String getPaymentStatus(String feeReference, String tenantId) throws ApplicationException;

	/**
	 * @param feeReference
	 * @param tenantId
	 * @return
	 */
	Boolean checkForPendingPayments(String feeReference, String tenantId);
}
