package com.privasia.procurehere.service;

import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;

public interface StripeSubscriptionService {

	/**
	 * @param event
	 * @throws ApplicationException
	 */
	public void handlePaymentWebhookEvent(Event event) throws ApplicationException;

	/**
	 * @param event
	 * @throws ApplicationException
	 */
	public void handleSupplierPaymentRenewalWebhookEvent(Event event, StripeObject stripeObject) throws ApplicationException;

	/**
	 * @param event
	 * @throws ApplicationException
	 */
	void handleSupplierPlanUpgradationWebhookEvent(Event event, StripeObject stripeObject) throws ApplicationException;

	/**
	 * @param paymentToken
	 * @return
	 * @throws ApplicationException
	 * @throws StripeException
	 */
	PaymentIntent getPaymentStatusData(String paymentToken) throws ApplicationException, StripeException;

	/**
	 * @param event
	 * @throws ApplicationException
	 */
	void handleBuyerPaymentRenewalAndUpdationWebhookEvent(Event event, StripeObject stripeObject) throws ApplicationException;
}
