package com.privasia.procurehere.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonSyntaxException;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.StripeSubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.ApiResource;

@Controller
@RequestMapping("/stripe")
public class StripeWebhookController {

	@Autowired
	StripeSubscriptionService stripeService;

	private static final Logger LOG = LogManager.getLogger(Global.SUBSCRIPTION_LOG);

	@RequestMapping(value = "/paymentEvents", method = RequestMethod.POST)
	public @ResponseBody void paymentWebHooks(@RequestBody String json, HttpServletRequest request) throws SignatureVerificationException, ApplicationException {
		com.stripe.model.Event event = null;
		try {
			event = ApiResource.GSON.fromJson(json, com.stripe.model.Event.class);
				LOG.info("Payment intent is: "+event.getType());
				stripeService.handlePaymentWebhookEvent(event);
		} catch (JsonSyntaxException e) {
			LOG.error("Unable to get stripe event details");
		} catch (Exception e) {
			LOG.error("Unable to get stripe details details " + e);
		}
	}

}
