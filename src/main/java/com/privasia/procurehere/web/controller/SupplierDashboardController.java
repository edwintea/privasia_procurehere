/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ibm.icu.util.Calendar;
import com.privasia.procurehere.core.dao.PaymentTransactionDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierBoardOfDirectors;
import com.privasia.procurehere.core.entity.SupplierFinanicalDocuments;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionStatus;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;
import com.privasia.procurehere.core.pojo.PoSupplierPojo;
import com.privasia.procurehere.core.pojo.SupplierFormSubmissionPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.NaicsCodesService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.PaymentTransactionService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.StripeSubscriptionService;
import com.privasia.procurehere.service.SubscriptionService;
import com.privasia.procurehere.service.SupplierFormSubmissionService;
import com.privasia.procurehere.service.SupplierPlanService;
import com.privasia.procurehere.service.SupplierSubscriptionStripeService;
import com.privasia.procurehere.service.impl.SubscriptionServiceImpl;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.stripe.model.PaymentIntent;

/**
 * @author Arc
 */
@Controller
@RequestMapping(value = "/supplier")
public class SupplierDashboardController extends DashboardBase implements Serializable {

	private static final long serialVersionUID = 1024473699180824175L;

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	SupplierService supplierService;

	@Value("${paypal.merchant.id}")
	String MERCHANT_ID;

	@Value("${paypal.url}")
	String PAYPAL_URL;

	@Value("${paypal.api.username}")
	String PAYPAL_API_USERNAME;

	@Value("${paypal.api.password}")
	String PAYPAL_API_PASSWORD;

	@Value("${paypal.api.signature}")
	String PAYPAL_API_SIGNATURE;

	@Value("${app.url}")
	String appUrl;

	@Value("${stripe.secret}")
	String secretKey;

	@Value("${stripe.publish}")
	String publishKey;

	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierPlanService supplierPlanService;

	@Autowired
	PaymentTransactionService paymentTransactionService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	NaicsCodesService industryCategoryService;

	@Autowired
	SupplierFormSubmissionService supplierFormService;

	@Value("${paypal.environment}")
	String PAYPAL_ENVIRONMENT;

	@Autowired
	PaymentTransactionDao paymentTransactionDao;

	@Autowired
	StripeSubscriptionService stripeService;

	@Autowired
	SupplierSubscriptionStripeService supplierSubscriptionStripeService;

	@ModelAttribute("rfxList")
	public List<RfxTypes> getStatusList() {
		return Arrays.asList(RfxTypes.values());
	}

	@ModelAttribute("supplierFormStatusList")
	public List<SupplierFormSubmitionStatus> getFormStatusList() {
		return Arrays.asList(SupplierFormSubmitionStatus.SUBMITTED, SupplierFormSubmitionStatus.ACCEPTED);
	}

	@RequestMapping(value = "/supplierDashboard", method = RequestMethod.GET)
	public String supplierAdminDashboard(Model model) {
		try {
			LOG.info(" Get Supplier Dashboard  : ");

			long eventAccseptCount = rftEventService.findCountOfAllAcceptedEventForSupplier(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			long eventActiveCount = rftEventService.findCountOfAllActiveEventForSupplier(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			long eventPendingCount = rftEventService.findCountOfAllPendingEventForSupplier(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			// long eventPendingCount =
			// rftEventService.findTotalPendingEventForForSupplier(SecurityLibrary.getLoggedInUserTenantId(),
			// null, SecurityLibrary.getLoggedInUser().getId());
			long eventSuspendedCount = rftEventService.findCountOfAllSuspendedEventForSupplier(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			long eventCompletedCount = rftEventService.findCountOfAllCompletedEventForSupplier(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			long eventClosedCount = rftEventService.findCountOfAllClosedEventForSupplier(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			long eventRejectedCount = rftEventService.findCountOfAllRejectedEventForSupplier(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			long orderedPoCount = supplierService.findCountOfPoForSupplierBasedOnStatus(SecurityLibrary.getLoggedInUserTenantId(), PoStatus.ORDERED);
			long acceptedPoCount = supplierService.findCountOfPoForSupplierBasedOnStatus(SecurityLibrary.getLoggedInUserTenantId(), PoStatus.ACCEPTED);
			// List<RfxView> eventList =
			// rftEventService.getAllEventForSupplier(SecurityLibrary.getLoggedInUserTenantId(),
			// SecurityLibrary.getLoggedInUser().getId());

			// remaining change status
			long pendingFormCount = supplierFormService.pendingBuyerFormCount(SecurityLibrary.getLoggedInUserTenantId(), Arrays.asList(SupplierFormSubmitionStatus.PENDING));
			long submittedFormCount = supplierFormService.pendingBuyerFormCount(SecurityLibrary.getLoggedInUserTenantId(), Arrays.asList(SupplierFormSubmitionStatus.SUBMITTED, SupplierFormSubmitionStatus.ACCEPTED));
			
			//PH-1496
			long eventActivePendingCount = rftEventService.findCountOfAllActivePendingEventCountForSupplier(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			long eventActiveSubmittedCount = rftEventService.findCountOfAllActiveSubmittedEventCountForSupplier(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());

			Supplier supplier = supplierService.findSupplierSubscriptionDetailsBySupplierIdExcludedExpiredBuyers(SecurityLibrary.getLoggedInUserTenantId());

			model.addAttribute("supplier", supplier);
			// model.addAttribute("publicEventList", eventList);
			OwnerSettings ownerSettings = ownerSettingsService.getPlainOwnersettings();
			if (ownerSettings != null) {
				model.addAttribute("supplierChargeStartDate", ownerSettings.getSupplierChargeStartDate());
			}
			model.addAttribute("eventActiveCount", eventActiveCount);
			model.addAttribute("eventAccseptCount", eventAccseptCount);
			model.addAttribute("eventSuspendedCount", eventSuspendedCount);
			model.addAttribute("eventClosedCount", eventClosedCount);
			model.addAttribute("eventRejectedCount", eventRejectedCount);
			model.addAttribute("eventPendingCount", eventPendingCount);
			model.addAttribute("eventCompletedCount", eventCompletedCount);
			model.addAttribute("orderedPoCount", orderedPoCount);
			model.addAttribute("acceptedPoCount", acceptedPoCount);
			model.addAttribute("pendingFormCount", pendingFormCount);
			model.addAttribute("submittedFormCount", submittedFormCount);
			model.addAttribute("eventActivePendingCount", eventActivePendingCount);
			model.addAttribute("eventActiveSubmittedCount", eventActiveSubmittedCount);

			Supplier loggedInSupplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());

			// PH-1098 check if the supplier has selected more than 25 sub categories.

			List<SupplierBoardOfDirectors> directorList = loggedInSupplier.getSupplierBoardOfDirectors();
			List<SupplierFinanicalDocuments> documents = loggedInSupplier.getSupplierFinancialDocuments();
			List<NaicsCodes> codes = industryCategoryService.searchForCategories(Global.SUPPLIER_COVERAGE, loggedInSupplier.getId(), null, null, null);
			int selectedCount = 0;
			if (CollectionUtil.isNotEmpty(codes)) {
				for (NaicsCodes level1 : codes) {
					if (CollectionUtil.isNotEmpty(level1.getChildren())) {
						for (NaicsCodes level2 : level1.getChildren()) {
							if (level2.isChecked() == true) {
								if (CollectionUtil.isNotEmpty(level2.getChildren())) {
									for (NaicsCodes level3 : level2.getChildren()) {
										if (level3.isChecked() == true) {
											if (CollectionUtil.isNotEmpty(level3.getChildren())) {
												for (NaicsCodes level4 : level3.getChildren()) {
													if (level4.isChecked() == true) {
														if (CollectionUtil.isNotEmpty(level4.getChildren())) {
															for (NaicsCodes level5 : level4.getChildren()) {
																if (level5.isChecked() == true) {
																	selectedCount++;
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			if (selectedCount > 25 || (directorList != null ? directorList.size() : 0) == 0 || (documents != null ? documents.size() : 0) == 0 || loggedInSupplier.getPaidUpCapital() == null || loggedInSupplier.getTaxRegistrationNumber() == null || loggedInSupplier.getCurrency() == null) {
				model.addAttribute("registrationIncompleteFlag", true);
			} else {
				model.addAttribute("registrationIncompleteFlag", false);
			}
			model.addAttribute("isFilter", false);
		} catch (Exception e) {
			LOG.error("Error while fetching supplier dashboard details :" + e.getMessage(), e);
		}
		return "supplierDashboard";
	}

	@RequestMapping(value = "/pendingEventList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfxView>> supplierPendingEventList(TableDataInput input) {
		TableData<RfxView> data = null;
		LOG.info(" Invited Event List");
		try {
			data = new TableData<RfxView>(rftEventService.getOnlyAllPendingEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalPendingEventForForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());

			LOG.info("Total Count : " + totalCount);

			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

		} catch (Exception e) {
			LOG.error("Error while loading Pending List For Supplier : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RfxView>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/suspendedEventList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfxView>> supplierSuspendedEventList(TableDataInput input) {
		TableData<RfxView> data = null;
		LOG.info("suspended Event List ");
		try {
			data = new TableData<RfxView>(rftEventService.getOnlyAllSuspendedEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalOnlyAllSuspendedEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

		} catch (Exception e) {
			LOG.error("Error while loading Suspended List For Supplier : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RfxView>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/activeEventList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfxView>> supplierActiveEventList(TableDataInput input) {
		TableData<RfxView> data = null;
		LOG.info("Active Event List ");
		try {
			data = new TableData<RfxView>(rftEventService.getOnlyAllActiveEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalOnlyAllActiveEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

		} catch (Exception e) {
			LOG.error("Error while loading Active List For Supplier : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RfxView>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/closedEventList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfxView>> supplierClosedEventList(TableDataInput input) {
		TableData<RfxView> data = null;
		LOG.info("Closed Event List ");
		try {
			data = new TableData<RfxView>(rftEventService.getOnlyAllClosedEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalOnlyAllClosedEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

		} catch (Exception e) {
			LOG.error("Error while loading Closed List For Supplier : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RfxView>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/completedEventList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfxView>> supplierCompletedEventList(TableDataInput input) {
		TableData<RfxView> data = null;
		LOG.info("Completed Event List ");
		try {
			data = new TableData<RfxView>(rftEventService.getOnlyAllCompletedEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalOnlyAllCompletedEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

		} catch (Exception e) {
			LOG.error("Error while loading Completed List For Supplier : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RfxView>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/rejectedEventList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfxView>> supplierRejectedEventList(TableDataInput input) {
		TableData<RfxView> data = null;
		LOG.info(" Rejected Event List ");
		try {
			data = new TableData<RfxView>(rftEventService.getOnlyAllRejectedEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalOnlyAllRejectedEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

		} catch (Exception e) {
			LOG.error("Error while loading Rejected  List For Supplier : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RfxView>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/subscribePlan", method = RequestMethod.GET)
	public String planList(Model model, @RequestParam(name = "payment_intent", required = false) String paymentStatus) {
		List<SupplierPlan> planList = supplierPlanService.findAllPlansByStatus(PlanStatus.ACTIVE);
		model.addAttribute("planList", planList);
		model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
		model.addAttribute("merchantId", this.MERCHANT_ID);
		model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
		model.addAttribute("publicKey", publishKey);
		Supplier supplier = supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("supplier", supplier);
		model.addAttribute("supplierCountry", (supplier.getRegistrationOfCountry() != null ? supplier.getRegistrationOfCountry().getCountryCode() : ""));

		if (StringUtils.checkString(paymentStatus).length() > 0) {
			PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentStatus);
			try {
				String msg = supplierSubscriptionStripeService.getPaymentStatus(paymentStatus);
				if (msg.indexOf("Processing") != -1) {
					model.addAttribute("info", msg);
				} else {
					paymentTransaction.setStatus(TransactionStatus.SUCCESS);
					model.addAttribute("subscription", paymentTransaction.getSupplierSubscription());
					model.addAttribute("paymentTransaction", paymentTransaction);
					model.addAttribute("renewal", true);
					return "showSupplierSubscriptionSuccess";
				}
			} catch (Exception e) {
				LOG.error("Error in payment " + e.getLocalizedMessage());
				return "supplierSubscriptionError/" + supplier.getSupplierSubscription().getSupplierPlan().getId() + "/" + paymentTransaction.getId();
			}

		}

		return "subscribePlan";
	}

	@RequestMapping(path = "/upgradePlan/{planId}", method = RequestMethod.GET)
	public String upgradePlanList(Model model, @PathVariable String planId, @RequestParam(name = "payment_intent", required = false) String paymentStatus) {
		List<SupplierPlan> planList = supplierPlanService.findAllPlansForUpgradeByStatus(PlanStatus.ACTIVE, planId);
		model.addAttribute("planList", planList);
		model.addAttribute("buyerList", buyerService.findAllActiveBuyers());
		model.addAttribute("merchantId", this.MERCHANT_ID);
		model.addAttribute("paypalEnvironment", this.PAYPAL_ENVIRONMENT);
		model.addAttribute("publicKey", publishKey);
		Supplier supplier = supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("supplier", supplier);
		model.addAttribute("supplierCountry", (supplier.getRegistrationOfCountry() != null ? supplier.getRegistrationOfCountry().getCountryCode() : ""));

		if (StringUtils.checkString(paymentStatus).length() > 0) {
			PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentStatus);
			try {
				String msg = supplierSubscriptionStripeService.getPaymentStatus(paymentStatus);
				if (msg.indexOf("Processing") != -1) {
					model.addAttribute("info", msg);
				} else {
					paymentTransaction.setStatus(TransactionStatus.SUCCESS);
					model.addAttribute("subscription", paymentTransaction.getSupplierSubscription());
					model.addAttribute("paymentTransaction", paymentTransaction);
					model.addAttribute("renewal", true);
					return "showSupplierSubscriptionSuccess";
				}
			} catch (Exception e) {
				LOG.error("Error in payment " + e.getLocalizedMessage());
				return "supplierSubscriptionError/" + supplier.getSupplierSubscription().getSupplierPlan().getId() + "/" + paymentTransaction.getId();
			}
		}

		return "subscribePlan";
	}

	// @RequestMapping(path = "/payment/{planId}", method = RequestMethod.POST)
	// public String doPayment(@PathVariable(name = "planId") String planId, Model model, @RequestParam(name =
	// "buyerId", required = false) String buyerId, HttpSession session, HttpServletRequest request) {
	//
	// Supplier supplier =
	// supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId());
	// SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);
	//
	// SupplierSubscription subscription = new SupplierSubscription();
	// subscription.setSupplierPlan(plan);
	// subscription.setPaymentTransaction(new PaymentTransaction());
	// subscription.getPaymentTransaction().setCountry(supplier.getRegistrationOfCountry());
	// subscription.setSupplier(supplier);
	//
	// if (StringUtils.checkString(buyerId).length() > 0) {
	// LOG.info("User selected the Buyer : " + buyerId);
	// subscription.getPaymentTransaction().setBuyer(new Buyer());
	// subscription.getPaymentTransaction().getBuyer().setId(buyerId);
	// }
	//
	// String returnURL = appUrl + "/supplier/confirm?planId=" + planId + "&txId=";
	// String cancelURL = appUrl + "/supplier/cancel?planId=" + planId + "&txId=";
	// // String returnURL = request.getScheme() + "://" + request.getServerName() +
	// // ":" + request.getServerPort()
	// // + request.getContextPath() + "/subscription/confirm?planId=" + planId +
	// // "&txId=";
	// // String cancelURL = request.getScheme() + "://" + request.getServerName() +
	// // ":" + request.getServerPort()
	// // + request.getContextPath() + "/subscription/cancel?planId=" + planId +
	// // "&txId=";
	// return subscriptionService.initiateSupplierPaypalPayment(subscription, planId, session, returnURL, cancelURL);
	//
	// }

	@RequestMapping(path = "/buyPlanpayment/{planId}", method = RequestMethod.POST)
	public String buyPlanpayment(@PathVariable(name = "planId") String planId, Model model, @RequestParam(name = "buyerId", required = false) String buyerId, HttpSession session, HttpServletRequest request) {

		Supplier supplier = supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId());
		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);

		SupplierSubscription subscription = new SupplierSubscription();
		subscription.setSupplierPlan(plan);
		subscription.setPaymentTransaction(new PaymentTransaction());
		subscription.getPaymentTransaction().setCountry(supplier.getRegistrationOfCountry());
		subscription.setSupplier(supplier);

		if (StringUtils.checkString(buyerId).length() > 0) {
			LOG.info("User selected the Buyer : " + buyerId);
			subscription.getPaymentTransaction().setBuyer(new Buyer());
			subscription.getPaymentTransaction().getBuyer().setId(buyerId);
		}

		String returnURL = appUrl + "/supplier/confirm?planId=" + planId + "&txId=";
		String cancelURL = appUrl + "/supplier/cancelPlan?planId=" + planId + "&txId=";
		// String returnURL = request.getScheme() + "://" + request.getServerName() +
		// ":" + request.getServerPort()
		// + request.getContextPath() + "/subscription/confirm?planId=" + planId +
		// "&txId=";
		// String cancelURL = request.getScheme() + "://" + request.getServerName() +
		// ":" + request.getServerPort()
		// + request.getContextPath() + "/subscription/cancel?planId=" + planId +
		// "&txId=";
		return subscriptionService.initiateSupplierPaypalPayment(subscription, planId, session, returnURL, cancelURL);

	}

	@RequestMapping(path = "/confirm", method = RequestMethod.GET)
	public String showConfirmPayment(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, @RequestParam(name = "token") String token, @RequestParam(name = "PayerID") String payerID, Model model, HttpServletRequest request, HttpSession session) {

		// Not required in model as the Subscription object already has the Plan details
		// String planId = (String) session.getAttribute("planId");
		// Plan plan = planService.getPlanForEditById(planId);
		// model.addAttribute("plan", plan);

		String subscriptionId = (String) session.getAttribute("subscriptionId");
		LOG.info("Subscription Id : " + subscriptionId);
		SupplierSubscription subscription = subscriptionService.getSupplierSubscriptionById(subscriptionId);
		if (subscription != null) {
			Supplier supplier = (Supplier) session.getAttribute("supplier");
			subscription.setSupplier(supplier);
		}
		model.addAttribute("subscription", subscription);

		PaymentTransaction paymentTransaction = subscriptionService.getPaymentTransactionById(txId);
		model.addAttribute("paymentTransaction", paymentTransaction);

		/*
		 * Build a second API request to PayPal, using the token as the ID to get the details on the payment
		 * authorization
		 */
		String nvpstr = "&TOKEN=" + token;

		LOG.info("Invoking PayPal Checkout Details ===================> ");
		/*
		 * Make the API call and store the results in an array. If the call was a success, show the authorization
		 * details, and provide an action to complete the payment. If failed, show the error
		 */
		HashMap<String, String> nvp = subscriptionService.invokePaypalService("GetExpressCheckoutDetails", nvpstr);
		String strAck = nvp.get("ACK").toString();
		if (strAck != null && (strAck.equalsIgnoreCase(SubscriptionServiceImpl.SUCCESS) || strAck.equalsIgnoreCase("SuccessWithWarning"))) {
			session.setAttribute("PAYERID", nvp.get("PAYERID").toString());
		}

		// The map will contain values as documented at
		// https://developer.paypal.com/docs/classic/api/merchant/GetExpressCheckoutDetails_API_Operation_NVP/
		model.addAttribute("paypalResponse", nvp);
		return "showSupplierConfirmSubscription";
	}

	@RequestMapping(path = "/confirm", method = RequestMethod.POST)
	public String doConfirmPayment(@RequestParam(name = "paymentTransactionId") String paymentTransactionId, @RequestParam(name = "token") String token, @RequestParam(name = "payerId") String payerId, Model model, HttpServletRequest request, HttpSession session) {
		try {
			String serverName = request.getServerName();
			subscriptionService.confirmSupplierSubscription(token, payerId, model, serverName, paymentTransactionId, true);
			// buyerService.sentBuyerCreationMail(user.getBuyer(), user);

		} catch (ApplicationException e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);
			PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionWithSupplierPlanByPaymentTransactionId(paymentTransactionId);
			return "redirect:/supplier/supplierSubscriptionError/" + paymentTransaction.getSupplierPlan().getId() + "/" + paymentTransactionId;
		} catch (Exception e) {
			LOG.error("Error during payment confirmation : " + e.getMessage(), e);
		}
		return "showSupplierSubscriptionSuccess";
	}

	@RequestMapping(path = "/supplierSubscriptionError/{planId}/{paymentTransactionId}", method = RequestMethod.GET)
	public String showPaymentError(@PathVariable(name = "planId") String planId, @PathVariable(name = "paymentTransactionId") String paymentTransactionId, Model model) {
		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);
		PaymentTransaction paymentTransaction = subscriptionService.getPaymentTransactionById(paymentTransactionId);
		Supplier supplier = subscriptionService.getSupplierWithSupplierPackagePlanByTenantIdforBilling(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("supplier", supplier);
		if (paymentTransaction == null) {
			try {
				paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentTransactionId);
				SupplierSubscription subscription = subscriptionService.getSupplierSubscriptionById(paymentTransaction.getSupplierSubscription().getId());
				model.addAttribute("subscription", subscription);
				PaymentIntent status = stripeService.getPaymentStatusData(paymentTransactionId);
				LOG.info("Got payment status as " + status.getStatus());
				if (Global.STRIPE_STATUS_REQUIRES_ATTENTION.equals(status.getStatus())) {
					if (status.getLastPaymentError() != null) {
						paymentTransaction.setStatus(TransactionStatus.FAILURE);
						paymentTransaction.setErrorCode(status.getLastPaymentError().getCode());
						paymentTransaction.setErrorMessage(status.getLastPaymentError().getMessage());
					}
				}
			} catch (Exception e) {
				LOG.error("Error in getting payment object " + e.getMessage());
			}
		}
		model.addAttribute("paymentTransaction", paymentTransaction);
		model.addAttribute("plan", plan);
		return "showSupplierSubscriptionError";
	}

	@RequestMapping(path = "/cancel", method = RequestMethod.GET)
	public String doCancel(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, Model model) {
		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);
		model.addAttribute("plan", plan);
		PaymentTransaction paymentTransaction = subscriptionService.cancelPaymentTransaction(txId);
		model.addAttribute("paymentTransaction", paymentTransaction);
		return "redirect:subscribePlan";
	}

	@RequestMapping(path = "/cancelPlan", method = RequestMethod.GET)
	public String cancelbuyPlan(@RequestParam(name = "planId") String planId, @RequestParam(name = "txId") String txId, Model model) {
		SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);
		model.addAttribute("plan", plan);
		PaymentTransaction paymentTransaction = subscriptionService.cancelPaymentTransaction(txId);
		model.addAttribute("paymentTransaction", paymentTransaction);
		return "redirect:billing/buyPlan";
	}

	@RequestMapping(value = "/extendSession", method = RequestMethod.GET)
	public ResponseEntity<String> extendSession(Model model) {
		try {
			LOG.debug("Extend Session requested by : " + SecurityLibrary.getLoggedInUserLoginId());
		} catch (Exception e) {
			LOG.error("Error in session extension : " + e.getMessage(), e);
		}
		return new ResponseEntity<String>("{\"id\" : \"Success\"}", HttpStatus.OK);
	}

	/*
	 * @RequestMapping(value = "/searchValue", method = RequestMethod.POST) public String searchValue(String opVal,
	 * String array, Model model, RedirectAttributes redirectAttrs) { LOG.info("  searchValue     " + opVal +
	 * "paramValues " + array); redirectAttrs.addFlashAttribute("opVal", opVal);
	 * redirectAttrs.addFlashAttribute("array", array); return "redirect:searchSupplierEvent"; }
	 */

	@RequestMapping(value = "/searchSupplierEvent", method = RequestMethod.GET)
	public ModelAndView searchSupplierEvent(@ModelAttribute("opVal") String opVal, @RequestParam(value = "Status", required = false) String status, @RequestParam(value = "Type", required = false) String type, @RequestParam(value = "daterangepicker-date", required = false) String daterange, Model model) {
		LOG.info("  searchSupplierEvent     " + opVal + "  Status " + status + "  type  :" + type + "  daterangepicker-date  :" + daterange);
		Date startDate = null;
		Date endDate = null;
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String[] dateValue = daterange.split("-");
		try {
			if (StringUtils.checkString(daterange).length() > 0) {
				startDate = (Date) formatter.parse(dateValue[0]);
				endDate = (Date) formatter.parse(dateValue[1]);

				Calendar startCal = Calendar.getInstance();
				startCal.setTime(startDate);
				startCal.set(Calendar.HOUR, 0);
				startCal.set(Calendar.MINUTE, 0);
				startCal.set(Calendar.SECOND, 0);
				startDate = startCal.getTime();

				Calendar endCal = Calendar.getInstance();
				endCal.setTime(endDate);
				endCal.set(Calendar.HOUR, 23);
				endCal.set(Calendar.MINUTE, 59);
				endCal.set(Calendar.SECOND, 59);
				endDate = endCal.getTime();

			}
		} catch (Exception e) {
			LOG.info("Error In Parsing Date Format" + e.getMessage(), e);
		}
		List<RfxView> publicEventList = rftEventService.getAllEventBasedOnSearchvalue(SecurityLibrary.getLoggedInUserTenantId(), opVal, status, type, startDate, endDate, SecurityLibrary.ifAnyGranted("ROLE_ADMIN") ? null : SecurityLibrary.getLoggedInUser().getId());
		model.addAttribute("publicEventList", publicEventList);
		model.addAttribute("opVal", opVal);
		model.addAttribute("isFilter", true);
		return new ModelAndView("supplierDashboard");
	}

	@RequestMapping(value = "/acceptedEventList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfxView>> supplierAcceptedEventList(TableDataInput input) {
		TableData<RfxView> data = null;
		LOG.info(" pending Event List");
		try {
			data = new TableData<RfxView>(rftEventService.getOnlyAllAcceptedEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalAcceptedEventForForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

		} catch (Exception e) {
			LOG.error("Error while loading Pending List For Supplier : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RfxView>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/orderedPoListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<PoSupplierPojo>> orderedPoListData(TableDataInput input) {
		try {
			List<PoSupplierPojo> poList = supplierService.findAllSearchFilterPoForSupplierByStatus(SecurityLibrary.getLoggedInUserTenantId(), input, PoStatus.ORDERED);
			TableData<PoSupplierPojo> data = new TableData<PoSupplierPojo>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = supplierService.findTotalSearchFilterPoForSupplierByStatus(SecurityLibrary.getLoggedInUserTenantId(), input, PoStatus.ORDERED);
			LOG.info(" totalCount : " + recordFiltered);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
			return new ResponseEntity<TableData<PoSupplierPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching new Po List For Supplier: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching  new Po List For Supplier : " + e.getMessage());
			return new ResponseEntity<TableData<PoSupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/acceptedPoListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<PoSupplierPojo>> acceptedPoListData(TableDataInput input) {
		try {
			List<PoSupplierPojo> poList = supplierService.findAllSearchFilterPoForSupplierByStatus(SecurityLibrary.getLoggedInUserTenantId(), input, PoStatus.ACCEPTED);
			TableData<PoSupplierPojo> data = new TableData<PoSupplierPojo>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = supplierService.findTotalSearchFilterPoForSupplierByStatus(SecurityLibrary.getLoggedInUserTenantId(), input, PoStatus.ACCEPTED);
			LOG.info(" totalCount : " + recordFiltered);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
			return new ResponseEntity<TableData<PoSupplierPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching accepted Po List For Supplier: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching accepted Po List For Supplier : " + e.getMessage());
			return new ResponseEntity<TableData<PoSupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/pendingFormListData")
	public ResponseEntity<TableData<SupplierFormSubmissionPojo>> pendingFormListData(TableDataInput input) {
		try {
			List<SupplierFormSubmissionPojo> pendingList = supplierFormService.findAllSearchFilterPendingFormByStatus(SecurityLibrary.getLoggedInUserTenantId(), input, SupplierFormSubmitionStatus.PENDING);
			TableData<SupplierFormSubmissionPojo> data = new TableData<SupplierFormSubmissionPojo>(pendingList);
			data.setDraw(input.getDraw());
			long recordFiltered = supplierFormService.findTotalPendingFormByStatus(SecurityLibrary.getLoggedInUserTenantId(), input, SupplierFormSubmitionStatus.PENDING);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
			return new ResponseEntity<TableData<SupplierFormSubmissionPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while fetching Pending Form List :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while fetching Pending Form List :" + e.getMessage());
			return new ResponseEntity<TableData<SupplierFormSubmissionPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/submittedFormListData")
	public ResponseEntity<TableData<SupplierFormSubmissionPojo>> submittedFormListData(TableDataInput input) {
		try {
			List<SupplierFormSubmissionPojo> pendingList = supplierFormService.findAllSearchFilterPendingFormByStatus(SecurityLibrary.getLoggedInUserTenantId(), input, null);
			TableData<SupplierFormSubmissionPojo> data = new TableData<SupplierFormSubmissionPojo>(pendingList);
			data.setDraw(input.getDraw());
			long recordFiltered = supplierFormService.findTotalPendingFormByStatus(SecurityLibrary.getLoggedInUserTenantId(), input, null);
			// long totalCount = supplierFormService.findTotalFormOfSupplier(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
			return new ResponseEntity<TableData<SupplierFormSubmissionPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while fetching Submitted Form List :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while fetching Submitted Form List :" + e.getMessage());
			return new ResponseEntity<TableData<SupplierFormSubmissionPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/activePendingData", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfxView>> supplierActivePendingEventList(TableDataInput input) {
		TableData<RfxView> data = null;
		LOG.info("Active Event List ");
		try {
			data = new TableData<RfxView>(rftEventService.getOnlyActivePendingEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalCountOfActivePendingEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

		} catch (Exception e) {
			LOG.error("Error while loading Active List For Supplier : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RfxView>>(data, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/activeSubmittedList", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfxView>> supplierActiveSubmittedEventList(TableDataInput input) {
		TableData<RfxView> data = null;
		LOG.info("Active Event List ");
		try {
			data = new TableData<RfxView>(rftEventService.getOnlyActiveSubmittedEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId()));
			data.setDraw(input.getDraw());

			long totalCount = rftEventService.findTotalCountOfActiveSubmittedEventsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(totalCount);

		} catch (Exception e) {
			LOG.error("Error while loading Active List For Supplier : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RfxView>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/payment/{planId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PaymentIntentPojo> doPayment(@PathVariable(name = "planId") String planId, Model model, @RequestParam(name = "buyerId", required = false) String buyerId, @RequestParam(name = "paymentMode") String paymentMode) {
		HttpHeaders headers = new HttpHeaders();
		try {
			Supplier supplier = supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId());
			SupplierPlan plan = supplierPlanService.getPlanForEditById(planId);
			SupplierSubscription subscription = new SupplierSubscription();
			subscription.setSupplierPlan(plan);
			subscription.setPaymentTransaction(new PaymentTransaction());
			subscription.getPaymentTransaction().setCountry(supplier.getRegistrationOfCountry());
			subscription.setSupplier(supplier);
			if (StringUtils.checkString(buyerId).length() > 0) {
				LOG.info("User selected the Buyer : " + buyerId);
				subscription.getPaymentTransaction().setBuyer(new Buyer());
				subscription.getPaymentTransaction().getBuyer().setId(buyerId);
			}
			return new ResponseEntity<PaymentIntentPojo>(supplierSubscriptionStripeService.initiateSupplierStripePayment(subscription, planId, paymentMode), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while storing Supplier : " + e.getMessage(), e);
			headers.add("error", e.getLocalizedMessage());
			return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
