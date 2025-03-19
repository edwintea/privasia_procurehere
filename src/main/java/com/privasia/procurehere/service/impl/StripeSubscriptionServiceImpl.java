package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.dao.PaymentTransactionDao;
import com.privasia.procurehere.core.dao.SupplierSubscriptionDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.dao.UserRoleDao;
import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerPackage;
import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierPackage;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.PeriodUnitType;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.SubscriptionCancelReason;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSubscriptionStripeService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PromotionalCodeService;
import com.privasia.procurehere.service.StripeSubscriptionService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.SupplierSubscriptionStripeService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;

@Service
@Transactional(readOnly = false)
public class StripeSubscriptionServiceImpl implements StripeSubscriptionService {

	private static final Logger LOG = LogManager.getLogger(Global.SUBSCRIPTION_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Value("${stripe.secret}")
	String secretKey;

	@Value("${stripe.publish}")
	String publishKey;

	@Autowired
	PromotionalCodeService promotionalCodeService;

	@Autowired
	PaymentTransactionDao paymentTransactionDao;

	@Autowired
	SupplierSubscriptionDao supplierSubscriptionDao;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierSubscriptionStripeService supplierSubscriptionStripeService;

	@Autowired
	BuyerSubscriptionStripeService buyerSubscriptionStripeService;

	@Autowired
	UserRoleDao userRoleDao;

	@Autowired
	UserDao userDao;

	@Autowired
	AccessRightsDao accessRightsDao;

	@SuppressWarnings("deprecation")
	@Override
	public void handlePaymentWebhookEvent(Event event) throws ApplicationException {
		StripeObject stripeObject = event.getData().getObject();
		LOG.info("Response from stripe: " + stripeObject.toString());

		try {
			PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
			if (paymentIntent != null) {
				LOG.info("Payment intent for: " + (paymentIntent.getDescription() != null ? paymentIntent.getDescription() : paymentIntent.getClientSecret()));
				PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentIntent.getId());
				if (paymentTransaction != null && StringUtils.isBlank(paymentTransaction.getReferenceTransactionId()) && paymentTransaction.getStatus() != TransactionStatus.SUCCESS) {

					if (paymentTransaction.getStatus() != TransactionStatus.IN_PROGRESS) {
						LOG.warn("Receieved intent for: " + paymentTransaction.getStatus() + " for id: " + paymentTransaction.getId());
					}

					if (paymentTransaction.getSupplierPlan() != null) {
						LOG.info("Handling supplier payment....");
						if (paymentTransaction.getSupplier() == null) {
							LOG.info("Creating a new supplier...");
							System.out.println("Creating a new supplier ... ");
							handleSupplierSubscription(event, stripeObject);
						} else if (paymentTransaction.getSupplier() != null) {
							if (paymentTransaction.getSupplier().getSupplierSubscription() != null && paymentTransaction.getSupplierSubscription() != null) {
								if (paymentTransaction.getSupplier().getSupplierPackage() != null && paymentTransaction.getSupplier().getSupplierPackage().getSupplierPlan() != null) {
									LOG.info("Renewing an existing supplier with id ..." + paymentTransaction.getSupplier().getId() + " and subscription id " + paymentTransaction.getSupplierSubscription().getId());
									handleSupplierPaymentRenewalWebhookEvent(event, stripeObject);
								} else {
									LOG.info("Upgrading an existing supplier with id ..." + paymentTransaction.getSupplier().getId());
									handleSupplierPlanUpgradationWebhookEvent(event, stripeObject);
								}
							}

						}
					}

					if (paymentTransaction.getBuyerPlan() != null) {
						LOG.info("Handling buyer payment....");
						if (paymentTransaction.getBuyer() == null) {
							handleBuyerSubscription(event, stripeObject);
						} else if (paymentTransaction.getBuyer() != null) {
							handleBuyerPaymentRenewalAndUpdationWebhookEvent(event, stripeObject);

						}

					}
				}
			}
		} catch (Exception e) {
			LOG.error("Exception in handling payment : " + e.getMessage(), e);
			PaymentIntent exception = (PaymentIntent) stripeObject;
			PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(exception.getId());
			if (paymentTransaction != null) {
				markTransactionAsNeedsAttention(paymentTransaction, e.getMessage(), "500");
			}
		}

	}

	private void handleSupplierSubscriptionFailure(PaymentIntent paymentFailIntent) throws ApplicationException {
		PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentFailIntent.getId());
		if (paymentTransaction != null) {
			paymentTransaction = markTransactionFailed(paymentTransaction, paymentFailIntent.getLastPaymentError().getMessage(), paymentFailIntent.getLastPaymentError().getCode());
			SupplierSubscription subscription = paymentTransaction.getSupplierSubscription();

			if (subscription != null) {
				// sending payment receipt email to supplier on failure
				String timeZone = "GMT+8:00";
				try {
					timeZone = supplierSubscriptionStripeService.getTimeZoneBySupplierSettings((subscription.getSupplier() != null ? subscription.getSupplier().getId() : null), timeZone);
					String msg = "Your payment transaction has failed during subscription";
					String subject = "Subscription Failure";
//					User user = userDao.getDetailsOfLoggedinUser(paymentTransaction.getLoginEmail());
//					if(user.getEmailNotifications())
					supplierSubscriptionStripeService.sendSupplierSubscriptionMail(paymentTransaction.getCommunicationEmail(), paymentTransaction.getFullName(), subscription, timeZone, msg, subject, false);
				} catch (Exception e) {
					LOG.error("Error While sending failure subscription mail to supplier :" + e.getMessage(), e);
				}
				// throw new ApplicationException("Payment transaction has failed");
			}
		}
	}

	private void handleSupplierSubscription(Event event, StripeObject stripeObject) {
		PaymentIntent paymentIntentt = (PaymentIntent) stripeObject;
		LOG.info("Payment intent for: " + paymentIntentt);
		try {
			switch (event.getType()) {
			case "charge.succeeded":
			case "payment_intent.succeeded":
				PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
				if (paymentIntent != null) {
					LOG.info("Payment succeeded for: " + paymentIntent.getDescription() != null ? paymentIntent.getDescription() : paymentIntent.getClientSecret());
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentIntent.getId());
					LOG.info("Creating a new supplier...");
					if (paymentTransaction != null) {
						SupplierSubscription subscription = paymentTransaction.getSupplierSubscription();
						if (subscription != null) {
							try {
								subscription = supplierSubscriptionStripeService.getSupplierSubscriptionById(paymentTransaction.getSupplierSubscription().getId());
								// Update the Payment Transaction Details
								LOG.info("Subscription Id : " + paymentTransaction.getSupplierSubscription().getId());
								paymentTransaction.setConfirmationDate(new Date());
								paymentTransaction.setStatus(TransactionStatus.SUCCESS);
								paymentTransaction.setReferenceTransactionId(paymentIntent.getId());
								paymentTransaction.setPaymentFee(new BigDecimal(paymentIntent.getAmount()));
								paymentTransaction = supplierSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);

								Supplier supplier = new Supplier();
								// Create the Supplier account and send out email notification for account
								// activiation.
								supplier.setLoginEmail(paymentTransaction.getLoginEmail());
								supplier.setCommunicationEmail(paymentTransaction.getCommunicationEmail());
								supplier.setCompanyContactNumber(paymentTransaction.getCompanyContactNumber());
								supplier.setCompanyName(paymentTransaction.getCompanyName());
								supplier.setCompanyRegistrationNumber(paymentTransaction.getCompanyRegistrationNumber());
								supplier.setSupplierSubscription(subscription);
								supplier.setFullName(paymentTransaction.getFullName());
								supplier.setPassword(paymentTransaction.getPassword());
								supplier.setRemarks(paymentTransaction.getRemarks());
								supplier.setMobileNumber(paymentTransaction.getMobileNumber());
								// supplier.setPlan(paymentTransaction.getSupplierPlan());
								supplier.setRegistrationOfCountry(paymentTransaction.getCountry());
								supplier.setStatus(SupplierStatus.PENDING);
								supplier.setSubscriptionDate(new Date());
								supplier.setCreatedDate(new Date());
								supplier.setDesignation(paymentTransaction.getDesignation());
								LOG.info("Designation is : " + paymentTransaction.getDesignation());
								try {
									supplier = supplierService.saveSupplier(supplier, true);
								} catch (Exception e) {
									LOG.error("Error creating Supplier instance : " + e.getMessage(), e);
								}

								// Update the subscription details
								if (subscription != null) {
									subscription.setSupplier(supplier);

									subscription.setStartDate(new Date());
									subscription.setActivatedDate(new Date());
									subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
									Calendar endDate = Calendar.getInstance();
									endDate.set(Calendar.HOUR_OF_DAY, 23);
									endDate.set(Calendar.MINUTE, 59);
									endDate.set(Calendar.SECOND, 59);
									if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.MONTH) {
										endDate.add(Calendar.MONTH, paymentTransaction.getSupplierPlan().getPeriod());
									} else if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.YEAR) {
										endDate.add(Calendar.YEAR, paymentTransaction.getSupplierPlan().getPeriod());
									}
									subscription.setEndDate(endDate.getTime());

									SupplierPackage sp = new SupplierPackage(subscription);

									if (paymentTransaction.getPromoCode() != null) {
										subscription.setPromoCode(paymentTransaction.getPromoCode().getPromoName());
									}

									subscription = supplierSubscriptionStripeService.updateSupplierSubscription(subscription);

									LOG.info("Supplier Id : " + supplier.getId());
									supplier.setSupplierPackage(sp);
									supplier.setRegistrationDate(new Date());
									supplierService.updateSupplier(supplier);

								}
								// Update transaction details with buyer info
								paymentTransaction.setSupplier(supplier);
								paymentTransaction = supplierSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);
								LOG.info("Finished creating supplier and update payment transaction, subscription ..  " + supplier.getDesignation());
							} catch (Exception e) {
								LOG.error("Error during payment confirmation : " + e.getMessage(), e);

								paymentTransaction.setStatus(TransactionStatus.NEEDS_ATTENTION);
								paymentTransaction.setErrorMessage("Error during payment confirmation : " + e.getMessage());
								paymentTransaction = supplierSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);
							}

							LOG.info("Successfully handled payment subscription..." + paymentIntent);

						}

					}
				}
				break;
			case "charge.failed":
			case "payment_intent.payment_failed":
				PaymentIntent paymentFailIntent = (PaymentIntent) stripeObject;
				if (paymentFailIntent != null) {
					LOG.error("Payment failed for: " + paymentFailIntent.getId());
					handleSupplierSubscriptionFailure(paymentFailIntent);
					LOG.info("Successfully handled payment failure....");
				}
				break;
			case "payment_intent.canceled":
				PaymentIntent paymentCancelled = (PaymentIntent) stripeObject;
				if (paymentCancelled != null) {
					LOG.error("Payment failed for: " + paymentCancelled.getDescription() != null ? paymentCancelled.getDescription() : paymentCancelled.getId() + " having ID: " + paymentCancelled.getId());
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentCancelled.getId());
					if (paymentTransaction != null) {
						paymentTransaction = markTransactionFailed(paymentTransaction, paymentCancelled.getLastPaymentError().getMessage(), paymentCancelled.getLastPaymentError().getCode());
						LOG.info("Successfully handled payment cancelation....");
					}
					// throw new ApplicationException("Payment transaction has failed");
				}
			default:
				LOG.info("Unhandled Event " + event.getType());
			}
		} catch (Exception e) {
			LOG.info("Exception in handling payment : " + e.getMessage(), e);
			PaymentIntent exception = (PaymentIntent) stripeObject;
			PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(exception.getId());
			if (paymentTransaction != null) {
				markTransactionAsNeedsAttention(paymentTransaction, e.getMessage(), "500");
			}
		}

	}

	@Override
	public void handleSupplierPaymentRenewalWebhookEvent(Event event, StripeObject stripeObject) throws ApplicationException {
		try {
			switch (event.getType()) {
			case "charge.succeeded":
			case "payment_intent.succeeded":
				PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
				if (paymentIntent != null) {
					SupplierSubscription subscription = null;
					LOG.info("Payment succeeded for: " + paymentIntent.getDescription() != null ? paymentIntent.getDescription() : paymentIntent.getClientSecret());
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentIntent.getId());
					subscription = supplierSubscriptionStripeService.getSupplierSubscriptionById(paymentTransaction.getSupplierSubscription().getId());
					if (subscription != null) {
						LOG.info("Subscription Id : " + paymentTransaction.getSupplierSubscription().getId());
						paymentTransaction.setConfirmationDate(new Date());
						paymentTransaction.setStatus(TransactionStatus.SUCCESS);
						paymentTransaction.setReferenceTransactionId(paymentIntent.getId());
						paymentTransaction.setPaymentFee(new BigDecimal(paymentIntent.getAmount()));
						paymentTransaction = supplierSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);

						Supplier supplier = new Supplier();
						supplier = subscription.getSupplier();

						LOG.info("Will enter while loop....");
						SupplierSubscription endSubscription = supplier.getSupplierSubscription();
						while (endSubscription.getNextSubscription() != null) {
							endSubscription = endSubscription.getNextSubscription();
						}
						LOG.info("Finished executing while loop...." + endSubscription.getId());
						// endSubscription.setNextSubscription(subscription);
						// supplier.setSupplierSubscription(endSubscription);

						if (endSubscription.getSupplierPlan() == null || endSubscription.getSupplierPlan().getBuyerLimit() == 1) {
							endSubscription.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
							endSubscription.setCancelledDate(new Date());
							endSubscription.setSubscriptionCancelReason(SubscriptionCancelReason.PLAN_CHANGE);
							// endSubscription.setNextSubscription(subscription);
							supplier.setSupplierSubscription(subscription);
						} else {
							endSubscription.setNextSubscription(subscription);
							supplier.setSupplierSubscription(endSubscription);
						}

						endSubscription = supplierSubscriptionStripeService.updateSupplierSubscription(endSubscription);

						// This block may be unnecessary. Please check and delete
						if (paymentTransaction.getBuyer() != null) {
							if (supplier.getAssociatedBuyers() == null) {
								supplier.setAssociatedBuyers(new ArrayList<Buyer>());
							}
							LOG.info("Associating buyer : " + paymentTransaction.getBuyer().getId() + " with supplier : " + supplier.getCompanyName());
							supplier.getAssociatedBuyers().add(paymentTransaction.getBuyer());
						}

						LOG.info("Got subscription as " + subscription.getId() + "");
						if (subscription != null) {

							subscription.setSubscriptionStatus(SubscriptionStatus.FUTURE);

							// Calculate Start/End, Trial Start Trial End etc...
							SupplierPackage sp = supplier.getSupplierPackage();
							Calendar previousEndDate = Calendar.getInstance();
							if (sp.getEndDate() == null) {
								previousEndDate.setTime(new Date());
							} else {
								// if already expired, then the start date after renewal will be current date
								if (sp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || endSubscription.getSubscriptionStatus() == SubscriptionStatus.CANCELLED) {
									previousEndDate.setTime(new Date());
									subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
									sp.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
									sp.setSupplierPlan(subscription.getSupplierPlan());
									sp.setBuyerLimit(subscription.getSupplierPlan().getBuyerLimit());
								} else {
									previousEndDate.setTime(sp.getEndDate());
									//previousEndDate.add(Calendar.DATE, 1); // 1 day to add
								}
							}
							subscription.setStartDate(previousEndDate.getTime());
							subscription.setActivatedDate(previousEndDate.getTime());

							if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.MONTH) {
								previousEndDate.add(Calendar.MONTH, paymentTransaction.getSupplierPlan().getPeriod());
							} else if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.YEAR) {
								previousEndDate.add(Calendar.YEAR, paymentTransaction.getSupplierPlan().getPeriod());
							}

							previousEndDate.set(Calendar.HOUR_OF_DAY, 23);
							previousEndDate.set(Calendar.MINUTE, 59);
							previousEndDate.set(Calendar.SECOND, 59);
							LOG.info("Setting end date as : " + previousEndDate.getTime());
							subscription.setEndDate(previousEndDate.getTime());
							sp.setEndDate(previousEndDate.getTime());
							if (paymentTransaction.getPromoCode() != null) {
								subscription.setPromoCode(paymentTransaction.getPromoCode().getPromoName());
							}
							subscription = supplierSubscriptionStripeService.updateSupplierSubscription(subscription);
							supplier.setSupplierPackage(sp);
							LOG.info("Supplier Id : " + supplier.getId());
							supplierService.updateSupplier(supplier);
							String timeZone = "GMT+8:00";
							try {
								timeZone = supplierSubscriptionStripeService.getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
								String msg = "Your payment transaction has successfully done";
								String subject = "Subscription renewed Successfully";
						    User user = userDao.getUserDetailsBySupplier(supplier.getId(), supplier.getLoginEmail());
							if(user.getEmailNotifications())
								supplierSubscriptionStripeService.sendSupplierSubscriptionMail(supplier.getCommunicationEmail(), supplier.getFullName(), subscription, timeZone, msg, subject, true);
							} catch (Exception e) {
								LOG.error("Error While sending success subscription renew mail to supplier :" + e.getMessage(), e);
							}
							LOG.info("Successfully handled payment renewal....");
						}
					}

				}
				break;
			case "charge.failed":
			case "payment_intent.payment_failed":
				PaymentIntent paymentFailIntent = (PaymentIntent) stripeObject;
				if (paymentFailIntent != null) {
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentFailIntent.getId());
					paymentTransaction = markTransactionFailed(paymentTransaction, paymentFailIntent.getLastPaymentError().getMessage(), paymentFailIntent.getLastPaymentError().getCode());
					LOG.info("Successfully handled payment renewal failure....");
				}
				break;
			case "payment_intent.canceled":
				PaymentIntent paymentCancelled = (PaymentIntent) stripeObject;
				if (paymentCancelled != null) {
					LOG.error("Payment failed for: " + paymentCancelled.getDescription() != null ? paymentCancelled.getDescription() : paymentCancelled.getId() + " having ID: " + paymentCancelled.getId());
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentCancelled.getId());
					if (paymentTransaction != null) {
						paymentTransaction = markTransactionFailed(paymentTransaction, paymentCancelled.getLastPaymentError().getMessage(), paymentCancelled.getLastPaymentError().getCode());
						LOG.info("Successfully handled payment renewal cancelation....");
					}
				}
			default:
				LOG.info("Unhandled Event " + event.getType());
			}
		} catch (Exception e) {
			LOG.info("Exception in handling payment : " + e.getMessage(), e);
			PaymentIntent exception = (PaymentIntent) stripeObject;
			PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(exception.getId());
			if (paymentTransaction != null) {
				markTransactionAsNeedsAttention(paymentTransaction, e.getMessage(), "500");
			}
			LOG.info("Exception in handling payment : " + e.getMessage(), e);
		}

	}

	@Override
	public void handleSupplierPlanUpgradationWebhookEvent(Event event, StripeObject stripeObject) throws ApplicationException {
		try {
			switch (event.getType()) {
			case "charge.succeeded":
			case "payment_intent.succeeded":
				PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
				if (paymentIntent != null) {
					SupplierSubscription subscription = null;
					LOG.info("Payment succeeded for: " + paymentIntent.getDescription() != null ? paymentIntent.getDescription() : paymentIntent.getClientSecret());
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentIntent.getId());
					subscription = supplierSubscriptionStripeService.getSupplierSubscriptionById(paymentTransaction.getSupplierSubscription().getId());
					if (subscription != null) {

						// Update the Payment Transaction Details
						LOG.info("Subscription Id : " + paymentTransaction.getSupplierSubscription().getId());
						paymentTransaction.setConfirmationDate(new Date());
						paymentTransaction.setStatus(TransactionStatus.SUCCESS);
						paymentTransaction.setReferenceTransactionId(paymentIntent.getId());
						paymentTransaction.setPaymentFee(new BigDecimal(paymentIntent.getAmount()));
						paymentTransaction = supplierSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);

						Supplier supplier = new Supplier();
						supplier = subscription.getSupplier();

						if (supplier.getSupplierSubscription().getSubscriptionStatus() != SubscriptionStatus.EXPIRED) {
							supplierSubscriptionStripeService.cancelOldSupplierSubscription(supplier.getId());
						}
						supplier.setSupplierSubscription(subscription);
						// This block may be unnecessary. Please check and delete
						if (paymentTransaction.getBuyer() != null) {
							if (supplier.getAssociatedBuyers() == null) {
								supplier.setAssociatedBuyers(new ArrayList<Buyer>());
							}
							LOG.info("Associating buyer : " + paymentTransaction.getBuyer().getId() + " with supplier : " + supplier.getCompanyName());
							supplier.getAssociatedBuyers().add(paymentTransaction.getBuyer());
						}

						LOG.info("Got subscription as " + subscription.getId());
						if (subscription != null) {
							// Calculate Start/End, Trial Start Trial End etc...
							subscription.setSubscriptionStatus(SubscriptionStatus.FUTURE);

							SupplierPackage sp = supplier.getSupplierPackage();

							Calendar previousEndDate = Calendar.getInstance();

							if (sp == null || (sp != null && sp.getEndDate() == null)) {
								previousEndDate.setTime(new Date());
								subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
							} else {
								// if already expired, then the start date after renewal will be current date
								if (sp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED) {
									subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
									previousEndDate.setTime(new Date());
								} else {
									previousEndDate.setTime(new Date());
									subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
									// previousEndDate.add(Calendar.DATE, 1); // 1 day to add
								}
							}

							subscription.setStartDate(previousEndDate.getTime());
							subscription.setActivatedDate(previousEndDate.getTime());
							Calendar endDate = Calendar.getInstance();
							endDate.setTime(subscription.getStartDate());
							if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.MONTH) {
								endDate.add(Calendar.MONTH, paymentTransaction.getSupplierPlan().getPeriod());
							} else if (paymentTransaction.getSupplierPlan().getPeriodUnit() == PeriodUnitType.YEAR) {
								endDate.add(Calendar.YEAR, paymentTransaction.getSupplierPlan().getPeriod());
							}

							endDate.set(Calendar.HOUR_OF_DAY, 23);
							endDate.set(Calendar.MINUTE, 59);
							endDate.set(Calendar.SECOND, 59);
							LOG.info("Setting end date as : " + endDate.getTime());
							subscription.setEndDate(endDate.getTime());

							// sp = new SupplierPackage(subscription);

							LOG.info("Existing package is: " + sp.getId());

							sp.setStartDate(subscription.getStartDate());
							sp.setEndDate(endDate.getTime());
							sp.setSupplierPlan(subscription.getSupplierPlan());
							sp.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
							sp.setBuyerLimit(subscription.getSupplierPlan().getBuyerLimit());
							supplier.setSupplierPackage(sp);
							supplier.setSupplierSubscription(subscription);
							LOG.info("Supplier Id : " + supplier.getId());
							supplier = supplierService.updateSupplier(supplier);

							if (paymentTransaction.getPromoCode() != null) {
								subscription.setPromoCode(paymentTransaction.getPromoCode().getPromoName());
							}

							subscription = supplierSubscriptionStripeService.updateSupplierSubscription(subscription);

							LOG.info("Supplier Id : " + supplier.getId());
							supplierService.updateSupplier(supplier);

						}
						// sending payment receipt email to supplier on successful
						String timeZone = "GMT+8:00";
						try {
							timeZone = supplierSubscriptionStripeService.getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
							String msg = "Your payment transaction has successfully done";
							String subject = "Subscribed Successfully";
//							User user = userDao.getUserDetailsBySupplier(supplier.getId(), supplier.getLoginEmail());
//							if(user.getEmailNotifications())
							supplierSubscriptionStripeService.sendSupplierSubscriptionMail(supplier.getCommunicationEmail(), supplier.getFullName(), subscription, timeZone, msg, subject, true);
						} catch (Exception e) {
							LOG.error("Error While sending success subscription mail to supplier :" + e.getMessage(), e);
						}

						// sending email to buyer on supplier successful subscribed
						if (paymentTransaction.getBuyer() != null) {
							try {
								timeZone = supplierSubscriptionStripeService.getTimeZoneByBuyerSettings(paymentTransaction.getBuyer().getId(), timeZone);
								User user = userDao.getUserDetailsByBuyer(paymentTransaction.getBuyer().getId(), paymentTransaction.getBuyer().getLoginEmail());
								if(user.getEmailNotifications())
								supplierSubscriptionStripeService.sendSupplierSubscriptionSuccessMailForBuyer(paymentTransaction.getBuyer().getCommunicationEmail(), paymentTransaction.getBuyer().getFullName(), timeZone, supplier.getCompanyName());
							} catch (Exception e) {
								LOG.error("Error While sending success supplier subscription mail to buyer :" + e.getMessage(), e);
							}
						}
					}
				}
				break;
			case "charge.failed":
			case "payment_intent.payment_failed":
			case "payment_intent.canceled":
				PaymentIntent paymentCancelled = (PaymentIntent) stripeObject;
				if (paymentCancelled != null) {
					LOG.error("Payment failed for: " + paymentCancelled.getDescription() != null ? paymentCancelled.getDescription() : paymentCancelled.getId() + " having ID: " + paymentCancelled.getId());
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentCancelled.getId());
					if (paymentTransaction != null) {
						paymentTransaction = markTransactionFailed(paymentTransaction, paymentCancelled.getLastPaymentError().getMessage(), paymentCancelled.getLastPaymentError().getCode());
						LOG.info("Successfully handled payment renewal cancelation....");
					}
				}
			default:
				LOG.info("Unhandled Event " + event.getType());
			}
		} catch (Exception e) {
			LOG.info("Exception in handling payment : " + e.getMessage(), e);
			PaymentIntent exception = (PaymentIntent) stripeObject;
			PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(exception.getId());
			if (paymentTransaction != null) {
				markTransactionAsNeedsAttention(paymentTransaction, e.getMessage(), "500");
			}
		}

	}

	private void handleBuyerSubscription(Event event, StripeObject stripeObject) {
		try {
			switch (event.getType()) {
			case "charge.succeeded":
			case "payment_intent.succeeded":
				LOG.info("Creating a new buyer...");
				PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
				if (paymentIntent != null) {
					boolean isSale = true;
					User user = null;
					BuyerSubscription subscription = null;
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentIntent.getId());
					subscription = buyerSubscriptionStripeService.getBuyerSubscriptionById(paymentTransaction.getBuyerSubscription().getId());

					// Update the Payment Transaction Details
					LOG.info("Subscription Id : " + paymentTransaction.getBuyerSubscription().getId());
					paymentTransaction.setConfirmationDate(new Date());
					paymentTransaction.setStatus(TransactionStatus.SUCCESS);
					paymentTransaction.setReferenceTransactionId(paymentIntent.getId());
					paymentTransaction.setPaymentFee(new BigDecimal(paymentIntent.getAmount()));
					paymentTransaction = buyerSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);

					Buyer buyer = new Buyer();
					try {
						// Create the Buyer account and send out email notification for account activiation.
						buyer.setLoginEmail(paymentTransaction.getLoginEmail());
						buyer.setCommunicationEmail(paymentTransaction.getCommunicationEmail());
						buyer.setCompanyContactNumber(paymentTransaction.getCompanyContactNumber());
						buyer.setCompanyName(paymentTransaction.getCompanyName());
						buyer.setCompanyRegistrationNumber(paymentTransaction.getCompanyRegistrationNumber());
						// buyer.setCurrentSubscription(subscription); this is set later below
						buyer.setFullName(paymentTransaction.getFullName());
						buyer.setPlan(paymentTransaction.getBuyerPlan());
						buyer.setRegistrationOfCountry(paymentTransaction.getCountry());
						buyer.setStatus(BuyerStatus.PENDING);
						buyer.setSubscriptionDate(new Date());
						buyer.setCreatedDate(new Date());
						user = buyerService.saveBuyer(buyer);
					} catch (Exception e) {
						LOG.error("Error creating Buyer instance : " + e.getMessage(), e);
					}

					// Update the subscription details
					if (subscription != null) {
						subscription.setBuyer(user.getBuyer());

						BuyerSubscription previous = null;

						if (!isSale) {
							previous = buyerSubscriptionStripeService.saveTrialSubscription(user, user.getBuyer());
							previous.setBuyer(user.getBuyer());
							buyerSubscriptionStripeService.updateSubscription(previous);
						}

						// Calculate Start/End Date etc...
						buyerSubscriptionStripeService.doComputeSubscription(subscription, previous);

						// If previous exists then use that to prepare the Buyer Package else the new one.
						BuyerPackage bp = new BuyerPackage(previous == null ? subscription : previous);
						buyer.setCurrentSubscription(previous == null ? subscription : previous);
						subscription = buyerSubscriptionStripeService.updateSubscription(subscription);
						if (!isSale) {
							previous.setNextSubscription(subscription);
							bp.setEndDate(subscription.getEndDate());
							buyerSubscriptionStripeService.updateSubscription(previous);
						}
						buyer.setBuyerPackage(bp);
						// buyer = buyerService.findBuyerById(user.getTenantId());
						LOG.info("Buyer Id : " + buyer.getId());
						buyer = buyerService.updateBuyer(buyer);
					}

					// Update transaction details with buyer info
					paymentTransaction.setBuyer(buyer);
					paymentTransaction.setCompanyName(buyer.getCompanyName());
					paymentTransaction = buyerSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);
					if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
						promotionalCodeService.updatePromoCode(paymentTransaction.getPromoCode().getId());
					}

					try {
						buyerService.sentBuyerCreationMail(user.getBuyer(), user);
					} catch (Exception e) {
						LOG.error("Error Sending Buyer Creation Email : " + e.getMessage(), e);
					}

				}
				break;
			case "charge.failed":
			case "payment_intent.payment_failed":
				PaymentIntent paymentFailIntent = (PaymentIntent) stripeObject;
				if (paymentFailIntent != null) {
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentFailIntent.getId());
					paymentTransaction = markTransactionFailed(paymentTransaction, paymentFailIntent.getLastPaymentError().getMessage(), paymentFailIntent.getLastPaymentError().getCode());

					// sending mail to buyer on payment failure
					try {
						String msg = "Your payment transaction has failed during subscription";
						String subject = "Subscription Failure";
//						User user = userDao.getDetailsOfLoggedinUser(paymentTransaction.getLoginEmail());
//						if(user.getEmailNotifications())
						buyerSubscriptionStripeService.sendBuyerSubscriptionMail(paymentTransaction.getCommunicationEmail(), paymentTransaction.getFullName(), paymentTransaction.getBuyerSubscription(), msg, subject, false, paymentTransaction);
					} catch (Exception e) {
						LOG.error("Error While sending failure subscription mail to buyer :" + e.getMessage(), e);
					}
					// throw new ApplicationException("Payment transaction has failed");
				}
				break;
			case "payment_intent.canceled":
				PaymentIntent paymentCancelled = (PaymentIntent) stripeObject;
				if (paymentCancelled != null) {
					LOG.error("Payment failed for: " + paymentCancelled.getDescription() != null ? paymentCancelled.getDescription() : paymentCancelled.getId() + " having ID: " + paymentCancelled.getId());
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentCancelled.getId());
					if (paymentTransaction != null) {
						paymentTransaction = markTransactionFailed(paymentTransaction, paymentCancelled.getLastPaymentError().getMessage(), paymentCancelled.getLastPaymentError().getCode());
					}

				}
			default:
				LOG.info("Unhandled Event " + event.getType());
			}
		} catch (Exception e) {
			LOG.info("Exception in handling payment : " + e.getMessage(), e);
			PaymentIntent exception = (PaymentIntent) stripeObject;
			PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(exception.getId());
			if (paymentTransaction != null) {
				markTransactionAsNeedsAttention(paymentTransaction, e.getMessage(), "500");
			}
		}
	}

	@Override
	public void handleBuyerPaymentRenewalAndUpdationWebhookEvent(Event event, StripeObject stripeObject) throws ApplicationException {
		try {
			switch (event.getType()) {
			case "charge.succeeded":
			case "payment_intent.succeeded":
				// Charge
				PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
				if (paymentIntent != null) {
					LOG.info("Payment succeeded for: " + (paymentIntent.getDescription() != null ? paymentIntent.getDescription() : paymentIntent.getClientSecret()) + " and ID " + paymentIntent.getId());
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentIntent.getId());
					Buyer buyer = paymentTransaction.getBuyer();
					LOG.info("Package..." + buyer.getBuyerPackage().getUserLimit() + "-" + buyer.getBuyerPackage().getEventLimit());
					LOG.info("Existing buyer subscription is " + paymentTransaction.getBuyer().getCurrentSubscription().getId() + " and subscription for current transaction is " + paymentTransaction.getBuyerSubscription().getId());
					LOG.info("Renewing an existing buyer with id ..." + paymentTransaction.getBuyer().getId());
					// Update the Payment Transaction Details
					LOG.info("Subscription Id : " + paymentTransaction.getBuyerSubscription().getId());
					paymentTransaction.setConfirmationDate(new Date());
					paymentTransaction.setStatus(TransactionStatus.SUCCESS);
					paymentTransaction.setReferenceTransactionId(paymentIntent.getId());
					paymentTransaction.setPaymentFee(new BigDecimal(paymentIntent.getAmount()));
					paymentTransaction = buyerSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);
					BuyerSubscription subscription = paymentTransaction.getBuyerSubscription();

					LOG.info("Updated payment transaction : " + paymentTransaction.getId() + " - " + paymentTransaction.getStatus() + " user quantity " + subscription.getUserQuantity() + " event quantity " + subscription.getEventQuantity());

					subscription.setPriceAmount(paymentTransaction.getPriceAmount());
					subscription.setTotalPriceAmount(paymentTransaction.getTotalPriceAmount());
					subscription.setPriceDiscount(paymentTransaction.getPriceDiscount());
					subscription.setPromoCodeDiscount(paymentTransaction.getPromoCodeDiscount());

					Boolean updateSubscriptionFlag = false;
					BuyerSubscription currentSubscription = buyer.getCurrentSubscription();
					if (currentSubscription != null) {
						while (currentSubscription.getNextSubscription() != null) {
							LOG.info("currentSubscription.getNextSubscription().getId() : " + currentSubscription.getNextSubscription().getId());
							LOG.info("subscription.getId() : " + subscription.getId());
							if (currentSubscription.getNextSubscription().getId().equals(subscription.getId())) {
								updateSubscriptionFlag = true;
								break;
							}
							currentSubscription = currentSubscription.getNextSubscription();
						}
					}

					LOG.info("updateSubscriptionFlag : " + updateSubscriptionFlag);

					// Update the subscription details for a plan
					if ((subscription != null && buyer.getCurrentSubscription() != null && subscription.getId().equals(buyer.getCurrentSubscription().getId()))) {
						LOG.info("User is updating current subscription");
						BuyerPackage bp = buyer.getBuyerPackage();
						if (subscription.getPlanType() == PlanType.PER_USER) {
							subscription.setUserQuantity(subscription.getUserQuantity() + paymentTransaction.getUserQuantity());
						} else if (subscription.getPlanType() == PlanType.PER_EVENT) {
							subscription.setEventQuantity(subscription.getEventQuantity() + paymentTransaction.getEventQuantity());
						}
						bp.setEventLimit(subscription.getEventQuantity());
						bp.setUserLimit(subscription.getUserQuantity());
						buyer.setBuyerPackage(bp);
						LOG.info("Buyer Id : " + buyer.getId());
						buyer = buyerService.updateBuyer(buyer);

						// Update transaction details with buyer info
						paymentTransaction.setBuyer(buyer);
						subscription = buyerSubscriptionStripeService.updateSubscription(subscription);

						LOG.info("Updating subscription with user quantity " + subscription.getUserQuantity() + " and event quantity " + subscription.getEventQuantity());

						paymentTransaction = buyerSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);
						if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
							promotionalCodeService.updatePromoCode(paymentTransaction.getPromoCode().getId());
						}

						// cancel recurring payment on change plan
						if (subscription != null && Boolean.TRUE == subscription.getAutoChargeSubscription()) {
						}

					} else if (updateSubscriptionFlag == true) {
						LOG.info("User is updating existing subscription");
						if (subscription.getPlanType() == PlanType.PER_USER) {
							subscription.setUserQuantity(subscription.getUserQuantity() + paymentTransaction.getUserQuantity());
							subscription.setEventQuantity(9999); // set default 9999 max event on user based
						} else if (subscription.getPlanType() == PlanType.PER_EVENT) {
							subscription.setEventQuantity(subscription.getEventQuantity() + paymentTransaction.getEventQuantity());
							subscription.setUserQuantity(9999); // set default 999 max user on event based
						}

						buyerSubscriptionStripeService.updateSubscription(subscription);
					} else {
						LOG.info("User is updating renewing subscription");
						if (subscription != null) {
							if (subscription.getPlanType() == PlanType.PER_USER) {
								subscription.setUserQuantity(paymentTransaction.getUserQuantity());
							} else if (subscription.getPlanType() == PlanType.PER_EVENT) {
								subscription.setEventQuantity(paymentTransaction.getEventQuantity());
							}
							// Chain the Subscriptions to know which one is next.
							LOG.info("Package..." + buyer.getBuyerPackage().getUserLimit() + "-" + buyer.getBuyerPackage().getEventLimit());
							BuyerSubscription currentChainEnd = buyer.getCurrentSubscription();
							if (currentChainEnd != null) {
								while (currentChainEnd.getNextSubscription() != null) {
									LOG.info("currentChainEnd.getNextSubscription() : " + currentChainEnd.getNextSubscription().getId());
									currentChainEnd = currentChainEnd.getNextSubscription();
								}
								LOG.info("Setting next subscription as: " + subscription.getId());
								currentChainEnd.setNextSubscription(subscription);
								buyerSubscriptionStripeService.updateSubscription(currentChainEnd);
							}
							LOG.info("Package..." + buyer.getBuyerPackage().getUserLimit() + "-" + buyer.getBuyerPackage().getEventLimit());
							LOG.info("Finished current chain end.....");

							if (Boolean.TRUE == subscription.getImmediateEffect()) {

								BuyerSubscription current = buyer.getCurrentSubscription();
								current.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
								current.setSubscriptionCancelReason(SubscriptionCancelReason.PLAN_CHANGE);
								while (current.getNextSubscription() != null) {
									current.getNextSubscription().setSubscriptionStatus(SubscriptionStatus.CANCELLED);
									current.getNextSubscription().setSubscriptionCancelReason(SubscriptionCancelReason.PLAN_CHANGE);
									buyerSubscriptionStripeService.updateSubscription(current.getNextSubscription());
									LOG.info("Inside while loop.....");
								}

								// currentChainEnd.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
								buyerSubscriptionStripeService.updateSubscription(current);
								buyerSubscriptionStripeService.doComputeSubscriptionChangePlan(subscription, buyer.getCurrentSubscription());
								if (subscription.getPlanType() == PlanType.PER_USER) {
									// Check Active users for buyer
									long activeUsers = userDao.findTotalRegisteredOrActiveUserForTenant(buyer.getId(), true);
									if (activeUsers > subscription.getUserQuantity()) {
										// Deactivate all users except Admin buyer
										userDao.deactivateAllUsersExceptAdminUser(buyer);
									}
								}
							} else {
								buyerSubscriptionStripeService.doComputeSubscription(subscription, currentChainEnd);
							}

							LOG.info("Package..." + buyer.getBuyerPackage().getUserLimit() + "-" + buyer.getBuyerPackage().getEventLimit());

							LOG.info("Finished current chain end.....");

							if (SubscriptionStatus.EXPIRED == currentChainEnd.getSubscriptionStatus() && PlanType.PER_USER == currentChainEnd.getPlanType() && subscription.getPlanType() == PlanType.PER_EVENT) {
								// get the ACL matching %PR....
								List<AccessRights> prAcl = accessRightsDao.getPrAccessControlListForBuyer();
								// get all roles for the tenant id
								List<UserRole> userRoleList = userRoleDao.findAllUserRolesForTenant(buyer.getId());
								if (CollectionUtil.isNotEmpty(userRoleList) && CollectionUtil.isNotEmpty(prAcl))
									for (UserRole role : userRoleList) {
										role.getAccessControlList().removeAll(prAcl);
										userRoleDao.update(role);
									}
							}

							LOG.info("Package..." + buyer.getBuyerPackage().getUserLimit() + "-" + buyer.getBuyerPackage().getEventLimit());
							LOG.info("Finished ACL.....");
							LOG.info("Updating subscription with user quantity " + subscription.getUserQuantity() + " and event quantity " + subscription.getEventQuantity());
							BuyerPackage bp = buyer.getBuyerPackage();
							subscription = buyerSubscriptionStripeService.updateSubscription(subscription);

							LOG.info("Package..." + bp.getUserLimit() + "-" + bp.getEventLimit());
							bp.setEndDate(subscription.getEndDate());
							buyer.setBuyerPackage(bp);
							buyer = buyerService.updateBuyer(buyer);

							LOG.info("Finished updating buyer .....");

							// Update transaction details with buyer info
							paymentTransaction.setBuyer(buyer);
							paymentTransaction.setCompanyName(buyer.getCompanyName());
							paymentTransaction = buyerSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);
							if (paymentTransaction.getPromoCode() != null && StringUtils.checkString(paymentTransaction.getPromoCode().getId()).length() > 0) {
								promotionalCodeService.updatePromoCode(paymentTransaction.getPromoCode().getId());
							}

							LOG.info("Finished updating promo code .....");

							// first cancel recurring payment
							if (currentChainEnd != null && Boolean.TRUE == currentChainEnd.getAutoChargeSubscription()) {
								// Do nothing
							}

							// creating new recurring payment after change plan
							if (currentChainEnd != null && Boolean.TRUE == currentChainEnd.getAutoChargeSubscription()) {
								// Do nothing
							}

						}

					}

				}
				break;
			case "charge.failed":
			case "payment_intent.payment_failed":
				PaymentIntent paymentFailIntent = (PaymentIntent) stripeObject;
				if (paymentFailIntent != null) {
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentFailIntent.getId());
					paymentTransaction = markTransactionFailed(paymentTransaction, paymentFailIntent.getLastPaymentError().getMessage(), paymentFailIntent.getLastPaymentError().getCode());

					// sending mail to buyer on payment failure
					try {
						String msg = "Your payment transaction has failed during subscription";
						String subject = "Subscription Failure";
//						User user = userDao.getDetailsOfLoggedinUser(paymentTransaction.getLoginEmail());
//						if(user.getEmailNotifications())
						buyerSubscriptionStripeService.sendBuyerSubscriptionMail(paymentTransaction.getCommunicationEmail(), paymentTransaction.getFullName(), paymentTransaction.getBuyerSubscription(), msg, subject, false, paymentTransaction);
					} catch (Exception e) {
						LOG.error("Error While sending failure subscription mail to buyer :" + e.getMessage(), e);
					}
					// throw new ApplicationException("Payment transaction has failed");
				}
				break;
			case "payment_intent.canceled":
				PaymentIntent paymentCancelled = (PaymentIntent) stripeObject;
				if (paymentCancelled != null) {
					LOG.error("Payment failed for: " + paymentCancelled.getDescription() != null ? paymentCancelled.getDescription() : paymentCancelled.getId() + " having ID: " + paymentCancelled.getId());
					PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(paymentCancelled.getId());
					if (paymentTransaction != null) {
						paymentTransaction = markTransactionFailed(paymentTransaction, paymentCancelled.getLastPaymentError().getMessage(), paymentCancelled.getLastPaymentError().getCode());
					}

				}
			default:
				LOG.info("Unhandled Event " + event.getType());
			}
		} catch (Exception e) {
			LOG.error("Exception in handling payment : " + e.getMessage(), e);
			PaymentIntent exception = (PaymentIntent) stripeObject;
			PaymentTransaction paymentTransaction = paymentTransactionDao.findPaymentTransactionByPaymentTokenId(exception.getId());
			if (paymentTransaction != null) {
				markTransactionAsNeedsAttention(paymentTransaction, e.getMessage(), "500");
			}
		}

	}

	@Override
	public PaymentIntent getPaymentStatusData(String paymentToken) throws ApplicationException, StripeException {
		Stripe.apiKey = secretKey;
		LOG.info("Getting Payment status for : " + paymentToken);
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentToken);
		return paymentIntent;

	}

	private PaymentTransaction markTransactionFailed(PaymentTransaction paymentTransaction, String errorMessage, String errorCode) {
		LOG.error("Error during payment. Error code : " + errorCode + ", Error Message : " + errorMessage);
		// Save the TOKEN in DB
		paymentTransaction.setStatus(TransactionStatus.FAILURE);
		paymentTransaction.setErrorCode(errorCode);
		paymentTransaction.setErrorMessage(errorMessage);
		paymentTransaction = supplierSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);
		return paymentTransaction;
	}

	private PaymentTransaction markTransactionAsNeedsAttention(PaymentTransaction paymentTransaction, String errorMessage, String errorCode) {
		LOG.error("Error during payment. Error code : " + errorCode + ", Error Message : " + errorMessage);
		// Save the TOKEN in DB
		paymentTransaction.setStatus(TransactionStatus.NEEDS_ATTENTION);
		paymentTransaction.setErrorCode(errorCode);
		paymentTransaction.setErrorMessage(errorMessage);
		paymentTransaction = supplierSubscriptionStripeService.updatePaymentTransaction(paymentTransaction);
		return paymentTransaction;
	}
}
