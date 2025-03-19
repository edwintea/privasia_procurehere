/**
 * 
 */
package com.privasia.procurehere.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.api.payments.Address;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.CreditCard;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.FundingInstrument;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Sale;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

/**
 * @author Nitin Otageri
 */
public class PayPalTest {

	private static final String CLIENT_ID = "ATUbW9GJ8YbMnMrPhw35fsyhO5TQIyH9Gv-PHDe6ZrwkJNv-jvhJPHdFhQNy_4GcviqkEU6jYq5EEyqU";
	private static final String SECRET = "EL9E3nXvbMJL8QCmMoZWKd4h7X3aq4Z84p4TpVGbqudxRBocsiHxZ2l1--iiljPxoosk_WK3BK4LnG2q";
	private static final String MODE = "sandbox";
	private static final String MERCHANT_ID = "WRYAX4U7DY3SU";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Replace these values with your clientId and secret. You can use these to get started right now.
		// String clientId = "AYSq3RDGsmBLJE-otTkBtM-jBRd1TCQwFf9RGfwddNXWz0uFU9ztymylOhRS";
		// String clientSecret = "EGnHDxD_qRPdaLdZz8iCr8N7_MzF-YHPTkjs6NKYQvQSBngp4PTTVWkPZRbL";

		String clientId = PayPalTest.CLIENT_ID;
		String clientSecret = SECRET;

		// Create a Credit Card
		CreditCard card = new CreditCard().setType("visa").setNumber("4417119669820331").setExpireMonth(11).setExpireYear(2019).setCvv2(012).setFirstName("Joe").setLastName("Shopper");

		try {
			APIContext context = new APIContext(clientId, clientSecret, MODE);
			card.create(context);
			System.out.println(card);
		} catch (PayPalRESTException e) {
			System.err.println(e.getDetails());
		}

		// new PayPalTest().createCreditCardPayment(null, null);
		new PayPalTest().getPaymentDetails();
		new PayPalTest().getSaleDetails();
	}

	public Payment createCreditCardPayment(HttpServletRequest req, HttpServletResponse resp) {

		System.out.println("Doing payment !!!...");

		// ###Address
		// Base Address object used as shipping or billing
		// address in a payment. [Optional]
		Address billingAddress = new Address();
		billingAddress.setCity("Johnstown");
		billingAddress.setCountryCode("US");
		billingAddress.setLine1("52 N Main ST");
		billingAddress.setPostalCode("43210");
		billingAddress.setState("OH");

		// ###CreditCard
		// A resource representing a credit card that can be
		// used to fund a payment.
		CreditCard creditCard = new CreditCard();
		creditCard.setBillingAddress(billingAddress);
		creditCard.setCvv2(012);
		creditCard.setExpireMonth(11);
		creditCard.setExpireYear(2021);
		creditCard.setFirstName("Nitin");
		creditCard.setLastName("Otageri");
		creditCard.setNumber("4293122327407870");
		creditCard.setType("visa");

		// ###Details
		// Let's you specify details of a payment amount.
		Details details = new Details();
		details.setShipping("1");
		details.setSubtotal("5");
		details.setTax("1");

		// ###Amount
		// Let's you specify a payment amount.
		Amount amount = new Amount();
		amount.setCurrency("USD");
		// Total must be equal to sum of shipping, tax and subtotal.
		amount.setTotal("7");
		amount.setDetails(details);

		// ###Transaction
		// A transaction defines the contract of a
		// payment - what is the payment for and who
		// is fulfilling it. PaymentTransaction is created with
		// a `Payee` and `Amount` types
		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction.setDescription("This is the payment transaction description.");

		// The Payment creation API requires a list of
		// PaymentTransaction; add the created `PaymentTransaction`
		// to a List
		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);

		// ###FundingInstrument
		// A resource representing a Payeer's funding instrument.
		// Use a Payer ID (A unique identifier of the payer generated
		// and provided by the facilitator. This is required when
		// creating or using a tokenized funding instrument)
		// and the `CreditCardDetails`
		FundingInstrument fundingInstrument = new FundingInstrument();
		fundingInstrument.setCreditCard(creditCard);

		// The Payment creation API requires a list of
		// FundingInstrument; add the created `FundingInstrument`
		// to a List
		List<FundingInstrument> fundingInstrumentList = new ArrayList<FundingInstrument>();
		fundingInstrumentList.add(fundingInstrument);

		// ###Payer
		// A resource representing a Payer that funds a payment
		// Use the List of `FundingInstrument` and the Payment Method
		// as 'credit_card'
		Payer payer = new Payer();
		payer.setFundingInstruments(fundingInstrumentList);
		payer.setPaymentMethod("credit_card");

		// ###Payment
		// A Payment Resource; create one using
		// the above types and intent as 'sale'
		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		Payment createdPayment = null;
		try {
			// ### Api Context
			// Pass in a `ApiContext` object to authenticate
			// the call and to send a unique request id
			// (that ensures idempotency). The SDK generates
			// a request id if you do not pass one explicitly.
			APIContext apiContext = new APIContext(PayPalTest.CLIENT_ID, PayPalTest.SECRET, PayPalTest.MODE);

			// Create a payment by posting to the APIService
			// using a valid AccessToken
			// The return object contains the status;
			createdPayment = payment.create(apiContext);

			System.out.println("Created payment with id = " + createdPayment.getId() + " and status = " + createdPayment.getState());
			System.out.println("Last Request : " + Payment.getLastRequest());
			System.out.println("Last Response : " + Payment.getLastResponse());

			// ResultPrinter.addResult(req, resp, "Payment with Credit Card", Payment.getLastRequest(),
			// Payment.getLastResponse(), null);
		} catch (PayPalRESTException e) {
			// ResultPrinter.addResult(req, resp, "Payment with Credit Card", Payment.getLastRequest(), null,
			// e.getMessage());
			System.out.println("Error : " + e.getMessage());
			e.printStackTrace();
		}
		return createdPayment;

	}

	public Payment createPayPalPayment(HttpServletRequest req, HttpServletResponse resp) {
		Payment createdPayment = null;

		Map<String, String> map = new HashMap<String, String>();

		// ### Api Context
		// Pass in a `ApiContext` object to authenticate
		// the call and to send a unique request id
		// (that ensures idempotency). The SDK generates
		// a request id if you do not pass one explicitly.
		APIContext apiContext = new APIContext(PayPalTest.CLIENT_ID, PayPalTest.SECRET, PayPalTest.MODE);
		if (req.getParameter("PayerID") != null) {
			Payment payment = new Payment();
			if (req.getParameter("guid") != null) {
				payment.setId(map.get(req.getParameter("guid")));
			}

			PaymentExecution paymentExecution = new PaymentExecution();
			paymentExecution.setPayerId(req.getParameter("PayerID"));
			try {

				createdPayment = payment.execute(apiContext, paymentExecution);
				System.out.println("Last Request : " + Payment.getLastRequest());
				System.out.println("Last Response : " + Payment.getLastResponse());
				// ResultPrinter.addResult(req, resp, "Executed The Payment", Payment.getLastRequest(),
				// Payment.getLastResponse(), null);
			} catch (PayPalRESTException e) {
				// ResultPrinter.addResult(req, resp, "Executed The Payment", Payment.getLastRequest(), null,
				// e.getMessage());
				System.out.println("Error : " + e.getMessage());
				e.printStackTrace();
			}
		} else {

			// ###Details
			// Let's you specify details of a payment amount.
			Details details = new Details();
			details.setShipping("1");
			details.setSubtotal("5");
			details.setTax("1");

			// ###Amount
			// Let's you specify a payment amount.
			Amount amount = new Amount();
			amount.setCurrency("USD");
			// Total must be equal to sum of shipping, tax and subtotal.
			amount.setTotal("7");
			amount.setDetails(details);

			// ###Transaction
			// A transaction defines the contract of a
			// payment - what is the payment for and who
			// is fulfilling it. PaymentTransaction is created with
			// a `Payee` and `Amount` types
			Transaction transaction = new Transaction();
			transaction.setAmount(amount);
			transaction.setDescription("This is the payment transaction description.");

			// ### Items
			Item item = new Item();
			item.setName("Ground Coffee 40 oz").setQuantity("1").setCurrency("USD").setPrice("5");
			ItemList itemList = new ItemList();
			List<Item> items = new ArrayList<Item>();
			items.add(item);
			itemList.setItems(items);

			transaction.setItemList(itemList);

			// The Payment creation API requires a list of
			// PaymentTransaction; add the created `PaymentTransaction`
			// to a List
			List<Transaction> transactions = new ArrayList<Transaction>();
			transactions.add(transaction);

			// ###Payer
			// A resource representing a Payer that funds a payment
			// Payment Method
			// as 'paypal'
			Payer payer = new Payer();
			payer.setPaymentMethod("paypal");

			// ###Payment
			// A Payment Resource; create one using
			// the above types and intent as 'sale'
			Payment payment = new Payment();
			payment.setIntent("sale");
			payment.setPayer(payer);
			payment.setTransactions(transactions);

			// ###Redirect URLs
			RedirectUrls redirectUrls = new RedirectUrls();
			String guid = UUID.randomUUID().toString().replaceAll("-", "");
			redirectUrls.setCancelUrl(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/paymentwithpaypal?guid=" + guid);
			redirectUrls.setReturnUrl(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/paymentwithpaypal?guid=" + guid);
			payment.setRedirectUrls(redirectUrls);

			// Create a payment by posting to the APIService
			// using a valid AccessToken
			// The return object contains the status;
			try {
				createdPayment = payment.create(apiContext);
				System.out.println("Created payment with id = " + createdPayment.getId() + " and status = " + createdPayment.getState());
				// ###Payment Approval Url
				Iterator<Links> links = createdPayment.getLinks().iterator();
				while (links.hasNext()) {
					Links link = links.next();
					if (link.getRel().equalsIgnoreCase("approval_url")) {
						req.setAttribute("redirectURL", link.getHref());
					}
				}
				System.out.println("Last Request : " + Payment.getLastRequest());
				System.out.println("Last Response : " + Payment.getLastResponse());
				// ResultPrinter.addResult(req, resp, "Payment with PayPal", Payment.getLastRequest(),
				// Payment.getLastResponse(), null);
				map.put(guid, createdPayment.getId());
			} catch (PayPalRESTException e) {
				// ResultPrinter.addResult(req, resp, "Payment with PayPal", Payment.getLastRequest(), null,
				// e.getMessage());
				System.out.println("Error : " + e.getMessage());
				e.printStackTrace();
			}
		}
		return createdPayment;
	}

	public void getPaymentDetails() {
		try {
			System.out.println("\nGetting Payment Details....");
			// ### Api Context
			// Pass in a `ApiContext` object to authenticate
			// the call and to send a unique request id
			// (that ensures idempotency). The SDK generates
			// a request id if you do not pass one explicitly.
			APIContext apiContext = new APIContext(PayPalTest.CLIENT_ID, PayPalTest.SECRET, PayPalTest.MODE);

			// Retrieve the payment object by calling the
			// static `get` method
			// on the Payment class by passing a valid
			// AccessToken and Payment ID
			Payment payment = Payment.get(apiContext, "PAY-91575986AH279013VLAI73WY");
			System.out.println("Payment retrieved ID = " + payment.getId() + ", status = " + payment.getState());
			System.out.println("Last Request : " + Payment.getLastRequest());
			System.out.println("Last Response : " + Payment.getLastResponse());
			// ResultPrinter.addResult(req, resp, "Get Payment", Payment.getLastRequest(), Payment.getLastResponse(),
			// null);
		} catch (PayPalRESTException e) {
			// ResultPrinter.addResult(req, resp, "Get Payment", Payment.getLastRequest(), null, e.getMessage());
			System.out.println("Error : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void getSaleDetails() {
		try {
			System.out.println("\nGetting Sale Details....");
			// ### Api Context
			// Pass in a `ApiContext` object to authenticate
			// the call and to send a unique request id
			// (that ensures idempotency). The SDK generates
			// a request id if you do not pass one explicitly.
			APIContext apiContext = new APIContext(PayPalTest.CLIENT_ID, PayPalTest.SECRET, PayPalTest.MODE);

			// Pass an AccessToken and the ID of the sale
			// transaction from your payment resource.
			Sale sale = Sale.get(apiContext, "5VW87419H5327413S");
			System.out.println("Sale amount : " + sale.getAmount() + " for saleID : " + sale.getId());
			System.out.println("Last Request : " + Payment.getLastRequest());
			System.out.println("Last Response : " + Payment.getLastResponse());
//			ResultPrinter.addResult(req, resp, "Get Sale", Sale.getLastRequest(), Sale.getLastResponse(), null);
		} catch (PayPalRESTException e) {
			// ResultPrinter.addResult(req, resp, "Get Sale", Sale.getLastRequest(), null, e.getMessage());
			System.out.println("Error : " + e.getMessage());
			e.printStackTrace();
		}
	}
}
