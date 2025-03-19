package com.privasia.procurehere.job;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.AccessRightsDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.NotificationMessageDao;
import com.privasia.procurehere.core.dao.OwnerSettingsDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.dao.UserRoleDao;
import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerPackage;
import com.privasia.procurehere.core.entity.BuyerSubscription;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierPackage;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.entity.UserRole;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSubscriptionService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.SubscriptionService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author Nitin Otageri
 * @author parveen
 */
@Component
public class SubscriptionMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(SubscriptionMonitorJob.class);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	UserDao userDao;

	@Autowired
	OwnerSettingsDao ownerSettingsDao;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	// @Autowired
	// DashboardNotificationService dashboardNotificationService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	SupplierService supplierService;

	@Autowired
	BuyerSubscriptionService buyerSubscriptionService;

	@Autowired
	UserRoleDao userRoleDao;

	@Autowired
	AccessRightsDao accessRightsDao;

	@Autowired
	NotificationMessageDao notificationMessageDao;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		try {
			LOG.info("buyer subscription");
			changeBuyerSubscriptionStatusActiveToExpired();
			buyerExpiryNotificationReminder();
			buyerExtraUserDeactivateExpiryNotification();

			changeSupplierSubscriptionStatusActiveToExpired();
			List<Integer> expiryList = ownerSettingsSupplierBuyerExpiryReminder();
			if (CollectionUtil.isNotEmpty(expiryList)) {
				Integer supplierSubsExpiryReminder = expiryList.get(0);
				if (supplierSubsExpiryReminder != null) {
					supplierExpiryNotificationReminder(supplierSubsExpiryReminder);
				}
			}

		} catch (Exception e) {
			LOG.error("Error in subscription monitor job :" + e.getMessage(), e);
		}

	}

	private void supplierExpiryNotificationReminder(Integer supplierSubsExpiryReminder) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, supplierSubsExpiryReminder);
		Date remindDate = cal.getTime();
		List<Supplier> suppliers = supplierService.findSuppliersForExpiryNotificationReminder(remindDate);
		if (CollectionUtil.isNotEmpty(suppliers)) {
			for (Supplier supplier : suppliers) {
				SupplierSubscription subscription = supplier.getSupplierSubscription();
				User supplierUser = userDao.getPlainUserByLoginId(supplier.getLoginEmail());
				if (supplierUser == null) {
					LOG.error("CANNOT SEND SUBSCRIPTION EXPIRY NOTIFICATION AS NO SUPPLIER USER FOUND BY LOGIN EMAIL : " + supplier.getLoginEmail());
					continue;
				}
				String timeZone = "GMT+8:00";
				timeZone = getTimeZoneBySupplierSettings(supplierUser.getTenantId(), timeZone);
				subscription.setReminderSent(Boolean.TRUE);
				subscriptionService.updateSupplierSubscription(subscription);
				try {
					sendSupplierExpiryNotificationReminderMail(supplierUser, subscription, timeZone, supplierSubsExpiryReminder);
				} catch (Exception e) {
					LOG.error("Error While Sending Supplier Subscription Expiry Reminder mail :" + e.getMessage(), e);
				}
			}
		}
	}

	private List<Integer> ownerSettingsSupplierBuyerExpiryReminder() {
		return ownerSettingsDao.getSupplierBuyerExpiryReminder();
	}

	private void changeBuyerSubscriptionStatusActiveToExpired() {
		try {
			List<Buyer> buyers = buyerService.findAllBuyersWithActiveSubscription();
			if (CollectionUtil.isNotEmpty(buyers)) {
				String timeZone = "GMT+8:00";
				for (Buyer buyer : buyers) {
					BuyerSubscription subscription = buyer.getCurrentSubscription();
					timeZone = getTimeZoneByBuyerSettings(buyer.getId(), timeZone);

					DateTime dt = new DateTime(new Date());
					DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
					DateTime dtus = dt.withZone(dtZone);
					Date nowDateWithTimeZone = dtus.toLocalDateTime().toDate(); // Convert to date with TimeZone

					/*
					 * LOG.info("buyer name :" + buyer.getLoginEmail() + "== current time:" + nowDateWithTimeZone +
					 * "=====subscription.getEndDate() :::" + subscription.getEndDate());
					 * LOG.info("subscription.getEndDate().before(now.getTime()) :" +
					 * subscription.getEndDate().before(nowDateWithTimeZone));
					 * LOG.info("subscription.getEndDate().compareTo(now.getTime()) : " +
					 * subscription.getEndDate().compareTo(nowDateWithTimeZone));
					 */
					if (subscription != null && subscription.getEndDate() != null && subscription.getEndDate().compareTo(nowDateWithTimeZone) < 0) {
						// LOG.info("buyer name :" + buyer.getLoginEmail());
						// Current subscription is expired. Change its status and find next subscription and make it
						// active
						BuyerSubscription nextSubscription = subscription.getNextSubscription();
						User buyerUser = userDao.getPlainUserByLoginId(buyer.getLoginEmail());
						if (nextSubscription != null && nextSubscription.getSubscriptionStatus() == SubscriptionStatus.FUTURE) {
							BuyerPackage bp = buyer.getBuyerPackage();

							PaymentTransaction pt = nextSubscription.getPaymentTransactions().get(0);

							if (subscription.getIsTrial() == Boolean.TRUE && pt != null && pt.getIsCapturePayment() == Boolean.FALSE) {

								// If payment not captured after Trial
								bp.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
								buyer.setBuyerPackage(bp);
								buyerService.updateBuyer(buyer);
							} else {
								nextSubscription.setActivatedDate(new Date());
								nextSubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
								buyer.setCurrentSubscription(nextSubscription);

								bp.setEventLimit(nextSubscription.getEventQuantity());
								bp.setUserLimit(nextSubscription.getUserQuantity());
								bp.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
								if (PlanType.PER_USER == nextSubscription.getPlanType()) {
									// Check Active users for buyer
									long activeUsers = userDao.findTotalRegisteredOrActiveUserForTenant(buyer.getId(), true);
									if (activeUsers > nextSubscription.getUserQuantity()) {
										// Deactivate all users except Admin buyer
										userDao.deactivateAllUsersExceptAdminUser(buyer);
										bp.setNoOfUsers(1);
									}
									bp.setNoOfEvents(0);
								}

								if (PlanType.PER_USER == subscription.getPlanType() && PlanType.PER_EVENT == nextSubscription.getPlanType()) {
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

								bp.setPlan(nextSubscription.getPlan());
								buyer.setBuyerPackage(bp);
								buyer.setPlan(nextSubscription.getPlan());
								buyerService.updateBuyer(buyer);
								buyerSubscriptionService.updateSubscription(nextSubscription);
								try {
									sendBuyerNextSubscriptionMail(buyerUser, nextSubscription, timeZone);
								} catch (Exception e) {
									LOG.error("Error While Sending next Subscription mail :" + e.getMessage(), e);
								}
							}
						} else {
							BuyerPackage bp = buyer.getBuyerPackage();
							bp.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
							buyer.setBuyerPackage(bp);
							buyerService.updateBuyer(buyer);
						}
						if (nextSubscription == null && subscription.getSubscriptionStatus() != SubscriptionStatus.EXPIRED) {
							subscription.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
							buyerSubscriptionService.updateSubscription(subscription);
							try {
								sendBuyerSubscriptionExpiryMail(buyerUser, subscription, timeZone);
							} catch (Exception e) {
								LOG.error("Error While Sending Subscription Expiry mail :" + e.getMessage(), e);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error processing Buyer Subscription Active to Expire. Error : " + e.getMessage(), e);
		}
	}

	private void changeSupplierSubscriptionStatusActiveToExpired() {
		try {
			LOG.info("Supplier Subscription job started....");
			List<Supplier> suppliers = supplierService.findSuppliersForSubscriptionExpireOrExtend();
			if (CollectionUtil.isNotEmpty(suppliers)) {
				String timeZone = "GMT+8:00";
				for (Supplier supplier : suppliers) {
					// LOG.info("Supplier name :" + supplier.getLoginEmail());
					SupplierSubscription subscription = supplier.getSupplierSubscription();
					SupplierPackage pkg = supplier.getSupplierPackage();
					User supplierUser = userDao.getPlainUserByLoginId(supplier.getLoginEmail());
					if (supplierUser != null) {
						LOG.info("Updating supplier from job setting status as expired: " + supplier.getLoginEmail());
						pkg.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
						supplier.setSupplierPackage(pkg);
						subscription.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
						subscriptionService.updateSupplierSubscription(subscription);
						supplierService.updateSupplier(supplier);
						// if (subscription.getSubscriptionStatus() != SubscriptionStatus.FUTURE) {
						try {
							sendSupplierSubscriptionExpiryMail(supplierUser, subscription, timeZone);
						} catch (Exception e) {
							LOG.error("Error While Sending Supplier Subscription Expiry mail :" + e.getMessage(), e);
						}
					}
					// }
				}
			}

			List<Supplier> s = supplierService.findSuppliersForFutureSubscriptionActivation();
			if (CollectionUtil.isNotEmpty(s)) {
				String timeZone = "GMT+8:00";
				for (Supplier supplier : s) {
					User supplierUser = userDao.getPlainUserByLoginId(supplier.getLoginEmail());
					if (supplierUser != null) {
						List<SupplierSubscription> ss = supplierService.getSupplierFutureSubscriptionByCreatedDate(supplier.getId());
						if (CollectionUtil.isNotEmpty(ss)) {
							for (SupplierSubscription currentSubscription : ss) {
								if (currentSubscription.getStartDate().before(new Date()) && currentSubscription.getEndDate().after(new Date())) {
									currentSubscription.setActivatedDate(new Date());
									currentSubscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
									subscriptionService.updateSupplierSubscription(currentSubscription);
									timeZone = getTimeZoneBySupplierSettings(supplierUser.getTenantId(), timeZone);
									LOG.info("Updating supplier setting subscription: " + supplier.getLoginEmail());
									supplier.setSupplierSubscription(currentSubscription);
									SupplierPackage pkg = supplier.getSupplierPackage();
									pkg.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
									supplier.setSupplierPackage(pkg);
									supplierService.updateSupplier(supplier);
									try {
										sendSupplierNextSubscriptionMail(supplierUser, currentSubscription, timeZone);
									} catch (Exception e) {
										LOG.error("Error While Sending Supplier Subscription ACTIVE mail :" + e.getMessage(), e);
									}
									break;
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			LOG.error("Error processing Supplier Subscription Active to Expire. Error : " + e.getMessage(), e);
		}
		LOG.info("Supplier Subscription job started....");
	}

	/**
	 * @param user
	 * @param subscription
	 * @param timeZone
	 */
	private void sendBuyerNextSubscriptionMail(User user, BuyerSubscription subscription, String timeZone) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String mailTo = user.getCommunicationEmail();
			String subject = "Subscription Activated";

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("plan", subscription.getPlan() != null ? subscription.getPlan().getPlanName() : "");
			map.put("planDesc", subscription.getPlan() != null ? subscription.getPlan().getShortDescription() : "");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.BUYER_NEXT_SUBSCRIPTION_ACTIVE);
			}
			try {
				String notificationMessage = messageSource.getMessage("buyer.next.subscription.active.notification", new Object[] {}, Global.LOCALE);
				sendDashboardNotification(user, "", subject, notificationMessage);
			} catch (Exception e) {
				LOG.error("Error While Sending Dashboard next Subscription Active notification :" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending next Subscription Active mail :" + e.getMessage(), e);
		}
	}

	/**
	 * @param user
	 * @param subscription
	 * @param timeZone
	 */
	private void sendSupplierNextSubscriptionMail(User user, SupplierSubscription subscription, String timeZone) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String mailTo = user.getCommunicationEmail();
			String subject = "Subscription Activated";

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("plan", subscription.getSupplierPlan() != null ? subscription.getSupplierPlan().getPlanName() : "");
			map.put("planDesc", subscription.getSupplierPlan() != null ? subscription.getSupplierPlan().getShortDescription() : "");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.SUPPLIER_NEXT_SUBSCRIPTION_ACTIVE);
			}
			try {
				String notificationMessage = messageSource.getMessage("supplier.next.subscription.active.notification", new Object[] {}, Global.LOCALE);
				sendDashboardNotification(user, "", subject, notificationMessage);
			} catch (Exception e) {
				LOG.error("Error While Sending Dashboard next Subscription Active notification :" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending next Subscription Active mail :" + e.getMessage(), e);
		}
	}

	/**
	 * @param user
	 * @param timeZone
	 */
	private void sendBuyerSubscriptionExpiryMail(User user, BuyerSubscription subscription, String timeZone) {
		try {
			if (user != null) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				String mailTo = user.getCommunicationEmail();
				String subject = "Subscription Expired";

				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				df.setTimeZone(TimeZone.getTimeZone(timeZone));
				map.put("date", df.format(new Date()));
				map.put("userName", user.getName());
				map.put("loginUrl", APP_URL + "/login");
				map.put("plan", subscription.getPlan() != null ? subscription.getPlan().getPlanName() : "");
				map.put("planDesc", subscription.getPlan() != null ? subscription.getPlan().getShortDescription() : "");
				if(user.getEmailNotifications()) {
					notificationService.sendEmail(mailTo, subject, map, Global.BUYER_EXPIRY_SUBSCRIPTION);
				}
				try {
					String notificationMessage = messageSource.getMessage("buyer.subscription.expiry.notification", new Object[] {}, Global.LOCALE);
					sendDashboardNotification(user, "", subject, notificationMessage);
				} catch (Exception e) {
					LOG.error("Error While Sending Dashboard Subscription Expiry notification :" + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Sending Subscription Expiry mail :" + e.getMessage(), e);
		}
	}

	/**
	 * @param user
	 * @param timeZone
	 */
	private void sendSupplierSubscriptionExpiryMail(User user, SupplierSubscription subscription, String timeZone) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String mailTo = user.getCommunicationEmail();
			String subject = "Subscription Expired";

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("plan", subscription.getSupplierPlan() != null ? subscription.getSupplierPlan().getPlanName() : "FREE TRIAL");
			map.put("planDesc", subscription.getSupplierPlan() != null ? subscription.getSupplierPlan().getShortDescription() : "FREE TRIAL SUBSCRIPTION");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.SUPPLIER_EXPIRY_SUBSCRIPTION);
			}
			try {
				String notificationMessage = messageSource.getMessage("supplier.subscription.expiry.notification", new Object[] {}, Global.LOCALE);
				sendDashboardNotification(user, "", subject, notificationMessage);
			} catch (Exception e) {
				LOG.error("Error While Sending Dashboard Supplier Subscription Expiry notification :" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending Supplier Subscription Expiry mail :" + e.getMessage(), e);
		}
	}

	/**
	 * @param user
	 * @param timeZone
	 * @return timeZone
	 */
	private String getTimeZoneByBuyerSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = buyerSettingsDao.getBuyerTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer setting time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	/**
	 * @param suppId
	 * @param timeZone
	 * @return
	 */
	private String getTimeZoneBySupplierSettings(String suppId, String timeZone) {
		try {
			if (StringUtils.checkString(suppId).length() > 0) {
				String time = supplierSettingsService.getSupplierTimeZoneByTenantId(suppId);
				if (time != null) {
					timeZone = time;
					LOG.info("time Zone :" + timeZone);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching supplier setting time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	/**
	 * @param messageTo
	 * @param url
	 * @param subject
	 * @param notificationMessage
	 */
	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
		try {
			NotificationMessage message = new NotificationMessage();
			message.setCreatedBy(null);
			message.setCreatedDate(new Date());
			message.setMessage(notificationMessage);
			message.setNotificationType(NotificationType.GENERAL);
			message.setMessageTo(messageTo);
			message.setSubject(subject);
			message.setTenantId(messageTo.getTenantId());
			message.setUrl(url);
			// dashboardNotificationService.save(message);
			notificationMessageDao.saveOrUpdate(message);
		} catch (Exception e) {
			LOG.error("Error while saving dashboard notification :" + e.getMessage(), e);
		}
	}

	/**
	 * @param user
	 * @param timeZone
	 */
	private void sendSupplierExpiryNotificationReminderMail(User user, SupplierSubscription subscription, String timeZone, Integer days) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String mailTo = user.getCommunicationEmail();
			String subject = "Subscription Expiry Reminder";
			LOG.info("sending email to supplier" + mailTo);
			String appUrl = APP_URL + "/supplier/billing/accountOverview";

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("days", days);
			map.put("appUrl", appUrl);
			df = new SimpleDateFormat("dd/MM/yyyy");
			map.put("endDate", df.format(subscription.getEndDate()));
			map.put("plan", subscription.getSupplierPlan() != null ? subscription.getSupplierPlan().getPlanName() : "FREE TRIAL");
			map.put("planDesc", subscription.getSupplierPlan() != null ? subscription.getSupplierPlan().getShortDescription() : "FREE TRIAL SUBSCRIPTION");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.EXPIRY_REMINDER_SUBSCRIPTION);
			}
			try {
				String notificationMessage = messageSource.getMessage("subscription.expiry.reminder.notification", new Object[] { days }, Global.LOCALE);
				sendDashboardNotification(user, "", subject, notificationMessage);
			} catch (Exception e) {
				LOG.error("Error While Sending Dashboard Supplier Subscription Expiry Reminder notification :" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending Supplier Subscription Expiry Reminder mail :" + e.getMessage(), e);
		}
	}

	private void buyerExpiryNotificationReminder() {

		// For Event based subscription
		reminderBefore6Months();
		reminderBefore3Months();

		reminderBefore30Days();

		reminderBefore15Days();
		reminderBefore7Days();
	}

	private void reminderBefore6Months() {
		try {
			// Remind before 6 Months
			Calendar before6Month = Calendar.getInstance();
			before6Month.add(Calendar.MONTH, 6);

			// Remind before 5 months
			Calendar before5Month = Calendar.getInstance();
			before5Month.setTime(before6Month.getTime());
			before5Month.add(Calendar.MONTH, -1);

			List<Buyer> buyers = buyerService.findBuyersForExpiryNotificationReminderBefore6Months();
			if (CollectionUtil.isNotEmpty(buyers)) {
				for (Buyer buyer : buyers) {

					// LOG.info(" buyer Email : " + buyer.getLoginEmail());
					User buyerUser = userDao.getPlainUserByLoginId(buyer.getLoginEmail());
					String timeZone = "GMT+8:00";
					timeZone = getTimeZoneByBuyerSettings(buyerUser.getTenantId(), timeZone);
					BuyerPackage buyerPackage = buyer.getBuyerPackage();

					DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

					DateTime before6MonDT = new DateTime(before6Month.getTime());
					before6MonDT = before6MonDT.withZone(dtZone);
					Date before6MonDate = before6MonDT.toLocalDateTime().toDate(); // Convert to date with TimeZone

					DateTime before5MonDT = new DateTime(before5Month.getTime());
					before5MonDT = before5MonDT.withZone(dtZone);
					Date before5MonDate = before5MonDT.toLocalDateTime().toDate(); // Convert to date with TimeZone

					// LOG.info("before5MonDate : " + before5MonDate + " ==before6MonDate : " + before6MonDate + "====
					// buyerPackage.getEndDate() " +buyerPackage.getEndDate());
					// LOG.info(" Condition :" + (buyerPackage.getEndDate().compareTo(before5MonDate) > 0 &&
					// buyerPackage.getEndDate().compareTo(before6MonDate) < 0));
					if (buyerPackage != null && buyerPackage.getEndDate().compareTo(before5MonDate) > 0 && buyerPackage.getEndDate().compareTo(before6MonDate) < 0) {
						try {
							LOG.info("  buyer Email : " + buyer.getLoginEmail());
							sendBuyerExpiryNotificationReminderMail(buyerUser, buyerPackage, timeZone);
							buyerPackage.setRemBefore6Month(Boolean.TRUE);
							buyer.setBuyerPackage(buyerPackage);
							buyerService.updateBuyer(buyer);
						} catch (Exception e) {
							LOG.error("Error While Sending Buyer Subscription Expiry Reminder mail 6 months before :" + e.getMessage(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while sending mail 6 months before to expiry date : " + e.getMessage(), e);
		}
	}

	private void reminderBefore3Months() {
		try {
			// Remind before 3 months
			Calendar before3Month = Calendar.getInstance();
			before3Month.add(Calendar.MONTH, 3);

			// Remind before 2 months
			Calendar before2Month = Calendar.getInstance();
			before2Month.setTime(before3Month.getTime());
			before2Month.add(Calendar.MONTH, -1);

			List<Buyer> buyers = buyerService.findBuyersForExpiryNotificationReminderBefore3Months();
			if (CollectionUtil.isNotEmpty(buyers)) {
				for (Buyer buyer : buyers) {

					// LOG.info(" buyer Email : " + buyer.getLoginEmail());
					User buyerUser = userDao.getPlainUserByLoginId(buyer.getLoginEmail());
					String timeZone = "GMT+8:00";
					timeZone = getTimeZoneByBuyerSettings(buyerUser.getTenantId(), timeZone);
					BuyerPackage buyerPackage = buyer.getBuyerPackage();

					DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

					DateTime before3MonDT = new DateTime(before3Month.getTime());
					before3MonDT = before3MonDT.withZone(dtZone);
					Date before3MonDate = before3MonDT.toLocalDateTime().toDate(); // Convert to date with TimeZone

					DateTime before2MonDT = new DateTime(before2Month.getTime());
					before2MonDT = before2MonDT.withZone(dtZone);
					Date before2MonDate = before2MonDT.toLocalDateTime().toDate(); // Convert to date with TimeZone

					// LOG.info("before2MonDate : " + before2MonDate + " ==before3MonDate : " + before3MonDate + "====
					// buyerPackage.getEndDate() " +buyerPackage.getEndDate());
					// LOG.info(" Condition :" + (buyerPackage.getEndDate().compareTo(before2MonDate) > 0 &&
					// buyerPackage.getEndDate().compareTo(before3MonDate) < 0));
					if (buyerPackage != null && buyerPackage.getEndDate().compareTo(before2MonDate) > 0 && buyerPackage.getEndDate().compareTo(before3MonDate) < 0) {
						try {
							LOG.info("  buyer Email : " + buyer.getLoginEmail());
							sendBuyerExpiryNotificationReminderMail(buyerUser, buyerPackage, timeZone);
							buyerPackage.setRemBefore3Month(Boolean.TRUE);
							buyer.setBuyerPackage(buyerPackage);
							buyerService.updateBuyer(buyer);
						} catch (Exception e) {
							LOG.error("Error While Sending Buyer Subscription Expiry Reminder mail 3 months before :" + e.getMessage(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while sending mail 3 months before to expiry date : " + e.getMessage(), e);
		}
	}

	private void reminderBefore30Days() {
		try {
			// Remind before 30 days
			Calendar before30Cal = Calendar.getInstance();
			before30Cal.add(Calendar.DATE, 30);

			// Remind before 25 days
			Calendar before25Cal = Calendar.getInstance();
			before25Cal.setTime(before30Cal.getTime());
			before25Cal.add(Calendar.DATE, -5);

			List<Buyer> buyers = buyerService.findBuyersForExpiryNotificationReminderBefore30Days();
			if (CollectionUtil.isNotEmpty(buyers)) {
				for (Buyer buyer : buyers) {

					// LOG.info(" buyer Email : " + buyer.getLoginEmail());
					User buyerUser = userDao.getPlainUserByLoginId(buyer.getLoginEmail());
					if (buyerUser != null) {
						String timeZone = "GMT+8:00";
						timeZone = getTimeZoneByBuyerSettings(buyerUser.getTenantId(), timeZone);
						BuyerPackage buyerPackage = buyer.getBuyerPackage();

						DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

						DateTime before30DT = new DateTime(before30Cal.getTime());
						before30DT = before30DT.withZone(dtZone);
						Date before30Date = before30DT.toLocalDateTime().toDate(); // Convert to date with TimeZone

						DateTime before25DT = new DateTime(before25Cal.getTime());
						before25DT = before25DT.withZone(dtZone);
						Date before25Date = before25DT.toLocalDateTime().toDate(); // Convert to date with TimeZone

						// LOG.info("before25Date : " + before25Date + " ==before30Date : " + before30Date + "====
						// buyerPackage.getEndDate() " +buyerPackage.getEndDate());
						// LOG.info(" Condition :" + (buyerPackage.getEndDate().compareTo(before25Date) > 0 &&
						// buyerPackage.getEndDate().compareTo(before30Date) < 0));
						if (buyerPackage != null && buyerPackage.getEndDate().compareTo(before25Date) > 0 && buyerPackage.getEndDate().compareTo(before30Date) < 0) {
							try {
								LOG.info("  buyer Email : " + buyer.getLoginEmail());
								sendBuyerExpiryNotificationReminderMail(buyerUser, buyerPackage, timeZone);
								buyerPackage.setRemBefore30Day(Boolean.TRUE);
								buyer.setBuyerPackage(buyerPackage);
								buyerService.updateBuyer(buyer);
							} catch (Exception e) {
								LOG.error("Error While Sending Buyer Subscription Expiry Reminder mail 30 days before :" + e.getMessage(), e);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while sending mail 30 days before to expiry date : " + e.getMessage(), e);
		}
	}

	private void reminderBefore15Days() {
		try {
			// Remind before 15 days
			Calendar before15Cal = Calendar.getInstance();
			before15Cal.add(Calendar.DATE, 15);

			// Remind before 10 days
			Calendar before10Cal = Calendar.getInstance();
			before10Cal.setTime(before15Cal.getTime());
			before10Cal.add(Calendar.DATE, -5);

			List<Buyer> buyers = buyerService.findBuyersForExpiryNotificationReminderBefore15Days();
			if (CollectionUtil.isNotEmpty(buyers)) {
				for (Buyer buyer : buyers) {

					String timeZone = "GMT+8:00";
					timeZone = getTimeZoneByBuyerSettings(buyer.getId(), timeZone);
					BuyerPackage buyerPackage = buyer.getBuyerPackage();

					DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

					DateTime before15DT = new DateTime(before15Cal.getTime());
					before15DT = before15DT.withZone(dtZone);
					Date before15Date = before15DT.toLocalDateTime().toDate(); // Convert to date with TimeZone

					DateTime before10DT = new DateTime(before10Cal.getTime());
					before10DT = before10DT.withZone(dtZone);
					Date before10Date = before10DT.toLocalDateTime().toDate(); // Convert to date with TimeZone

					// LOG.info("before10Date : " + before10Date + " ==before15Date : " + before15Date + "====
					// buyerPackage.getEndDate() " + buyerPackage.getEndDate());
					// LOG.info(" Condition :" + (buyerPackage.getEndDate().compareTo(before10Date) > 0 &&
					// buyerPackage.getEndDate().compareTo(before15Date) < 0));
					if (buyerPackage != null && buyerPackage.getEndDate().compareTo(before10Date) > 0 && buyerPackage.getEndDate().compareTo(before15Date) < 0) {
						LOG.info("  buyer Email : " + buyer.getLoginEmail());
						try {
							User buyerUser = userDao.getPlainUserByLoginId(buyer.getLoginEmail());
							sendBuyerExpiryNotificationReminderMail(buyerUser, buyerPackage, timeZone);
							buyerPackage.setRemBefore15Day(Boolean.TRUE);
							buyer.setBuyerPackage(buyerPackage);
							buyerService.updateBuyer(buyer);
						} catch (Exception e) {
							LOG.error("Error While Sending Buyer Subscription Expiry Reminder mail 15 days before :" + e.getMessage(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while sending mail 15 days before to expiry date : " + e.getMessage(), e);
		}
	}

	private void reminderBefore7Days() {
		try {
			// Remind before 7 days
			Calendar before7Cal = Calendar.getInstance();
			before7Cal.add(Calendar.DATE, 7);

			// Remind before 2 days
			Calendar before2Cal = Calendar.getInstance();
			before2Cal.setTime(before7Cal.getTime());
			before2Cal.add(Calendar.DATE, -5);

			List<Buyer> buyers = buyerService.findBuyersForExpiryNotificationReminderBefore7Days();
			if (CollectionUtil.isNotEmpty(buyers)) {
				for (Buyer buyer : buyers) {
					User buyerUser = userDao.getPlainUserByLoginId(buyer.getLoginEmail());
					if (buyerUser != null) {
						String timeZone = "GMT+8:00";
						timeZone = getTimeZoneByBuyerSettings(buyerUser.getTenantId(), timeZone);
						BuyerPackage buyerPackage = buyer.getBuyerPackage();

						DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

						DateTime before7DT = new DateTime(before7Cal.getTime());
						before7DT = before7DT.withZone(dtZone);
						Date before7Date = before7DT.toLocalDateTime().toDate(); // Convert to date with TimeZone

						DateTime before2DT = new DateTime(before2Cal.getTime());
						before2DT = before2DT.withZone(dtZone);
						Date before2Date = before2DT.toLocalDateTime().toDate(); // Convert to date with TimeZone

						// LOG.info("before2Date : " + before2Date + " ==before7Date : " + before7Date + "====
						// buyerPackage.getEndDate() " + buyerPackage.getEndDate());
						// LOG.info(" Condition :" + (buyerPackage.getEndDate().compareTo(before2Date) > 0 &&
						// buyerPackage.getEndDate().compareTo(before7Date) < 0));
						if (buyerPackage != null && buyerPackage.getEndDate().compareTo(before2Date) > 0 && buyerPackage.getEndDate().compareTo(before7Date) < 0) {
							try {
								LOG.info("  buyer Email : " + buyer.getLoginEmail());
								sendBuyerExpiryNotificationReminderMail(buyerUser, buyerPackage, timeZone);
								buyerPackage.setRemBefore7Day(Boolean.TRUE);
								buyer.setBuyerPackage(buyerPackage);
								buyerService.updateBuyer(buyer);
							} catch (Exception e) {
								LOG.error("Error While Sending Buyer Subscription Expiry Reminder mail 7 days before :" + e.getMessage(), e);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while sending mail 7 days before to expiry date : " + e.getMessage(), e);
		}
	}

	private void sendBuyerExpiryNotificationReminderMail(User user, BuyerPackage buyerPackage, String timeZone) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			String mailTo = user.getCommunicationEmail();
			String subject = "Subscription Expiry Reminder";
			String appUrl = APP_URL + "/buyer/billing/accountOverview";
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", appUrl);
			df = new SimpleDateFormat("dd/MM/yyyy");
			map.put("endDate", df.format(buyerPackage.getEndDate()));
			map.put("plan", buyerPackage.getPlan() != null ? buyerPackage.getPlan().getPlanName() : "");
			map.put("planDesc", buyerPackage.getPlan() != null ? buyerPackage.getPlan().getShortDescription() : "");
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.EXPIRY_REMINDER_SUBSCRIPTION);
			}
			try {
				String notificationMessage = messageSource.getMessage("subscription.expiry.reminder.notification", new Object[] { df.format(buyerPackage.getEndDate()) }, Global.LOCALE);
				sendDashboardNotification(user, appUrl, subject, notificationMessage);
			} catch (Exception e) {
				LOG.error("Error While Sending Dashboard buyer Subscription Expiry Reminder notification :" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error While Sending buyer Subscription Expiry Reminder mail :" + e.getMessage(), e);
		}
	}

	private void buyerExtraUserDeactivateExpiryNotification() {

		buyerExtraUserDeactivateExpiryNotificationBefore30Days();
		buyerExtraUserDeactivateExpiryNotificationBefore15Days();
		buyerExtraUserDeactivateExpiryNotificationBefore7Days();
		buyerExtraUserDeactivateExpiryNotificationBefore2Days();
	}

	private void buyerExtraUserDeactivateExpiryNotificationBefore30Days() {

		try {
			// Remind before 30 days
			Calendar before30Cal = Calendar.getInstance();
			before30Cal.add(Calendar.DATE, 30);

			// Remind now
			Calendar now = Calendar.getInstance();

			List<Buyer> buyers = buyerService.findAllBuyersFor30DaysBeforeSubscriptionExpire();
			if (CollectionUtil.isNotEmpty(buyers)) {
				String timeZone = "GMT+8:00";
				for (Buyer buyer : buyers) {
					DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

					DateTime before30DT = new DateTime(before30Cal.getTime());
					before30DT = before30DT.withZone(dtZone);
					Date before7Date = before30DT.toLocalDateTime().toDate(); // Convert to date with TimeZone
					DateTime nowDT = new DateTime(now.getTime());
					nowDT = nowDT.withZone(dtZone);
					Date nowDate = nowDT.toLocalDateTime().toDate(); // Convert to date with TimeZone
					BuyerSubscription subscription = buyer.getCurrentSubscription();
					if (subscription != null && subscription.getEndDate().compareTo(nowDate) > 0 && subscription.getEndDate().compareTo(before7Date) < 0 && subscription.getNextSubscription() != null) {
						BuyerSubscription nextSubscription = subscription.getNextSubscription();

						if (nextSubscription.getSubscriptionStatus() == SubscriptionStatus.FUTURE && PlanType.PER_USER == nextSubscription.getPlanType()) {

							BuyerPackage buyerPackage = buyer.getBuyerPackage();
							// Check Active users for buyer
							long activeUsers = userDao.findTotalRegisteredOrActiveUserForTenant(buyer.getId(), true);
							if (activeUsers > nextSubscription.getUserQuantity()) {
								LOG.info("buyer name :" + buyer.getLoginEmail());
								// Deactivate notification to all users except admin owner buyer
								try {
									// For buyer Admin
									User buyerUser = userDao.getPlainUserByLoginId(buyer.getLoginEmail());
									timeZone = getTimeZoneByBuyerSettings(buyer.getId(), timeZone);
									String msg = "Please manage extra users otherwise all users will be deactivated in 30 days due to active user count mismatch. ";
									sendBuyerUserDeactivateMail(buyerUser, msg, timeZone, subscription, nextSubscription, activeUsers, true);

									// For all users
									// List<User> userList = userDao.fetchAllUsersForTenant(buyer.getId());
									// for (User user : userList) {
									// // Check mail to admin owner and other users
									// if (user.getLoginId().equalsIgnoreCase(buyer.getLoginEmail())) {
									// // Notification to buyer admin owner
									// String msg = "Please manage extra users otherwise all users will be deactivated
									// in 30 days due to active user count mismatch. ";
									// sendBuyerUserDeactivateMail(user, msg, timeZone, subscription, nextSubscription,
									// activeUsers, true);
									// } else {
									// // Notification to buyer admin other users
									// String msg = "your account may be deactivated in 30 days due to changes in
									// subscription, please contact your administrator for more details.";
									// sendBuyerUserDeactivateMail(user, msg, timeZone, subscription, nextSubscription,
									// activeUsers, false);
									// }
									// }
									buyerPackage.setUserDaRemBefore15Day(Boolean.TRUE);
									buyer.setBuyerPackage(buyerPackage);
									buyerService.updateBuyer(buyer);
								} catch (Exception e) {
									LOG.error("Error While Sending mail Buyer Subscription user deactivate before 30 days on next subs  :" + e.getMessage(), e);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error processing Buyer Subscription user deactivate before 30 days on next subs . Error : " + e.getMessage(), e);
		}
	}

	private void buyerExtraUserDeactivateExpiryNotificationBefore15Days() {

		try {
			// Remind before 15 days
			Calendar before15Cal = Calendar.getInstance();
			before15Cal.add(Calendar.DATE, 15);

			// Remind now
			Calendar now = Calendar.getInstance();

			List<Buyer> buyers = buyerService.findAllBuyersFor15DaysBeforeSubscriptionExpire();
			if (CollectionUtil.isNotEmpty(buyers)) {
				String timeZone = "GMT+8:00";
				for (Buyer buyer : buyers) {
					DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

					DateTime before15DT = new DateTime(before15Cal.getTime());
					before15DT = before15DT.withZone(dtZone);
					Date before7Date = before15DT.toLocalDateTime().toDate(); // Convert to date with TimeZone
					DateTime nowDT = new DateTime(now.getTime());
					nowDT = nowDT.withZone(dtZone);
					Date nowDate = nowDT.toLocalDateTime().toDate(); // Convert to date with TimeZone
					BuyerSubscription subscription = buyer.getCurrentSubscription();
					if (subscription != null && subscription.getEndDate().compareTo(nowDate) > 0 && subscription.getEndDate().compareTo(before7Date) < 0 && subscription.getNextSubscription() != null) {
						BuyerSubscription nextSubscription = subscription.getNextSubscription();

						if (nextSubscription.getSubscriptionStatus() == SubscriptionStatus.FUTURE && PlanType.PER_USER == nextSubscription.getPlanType()) {

							BuyerPackage buyerPackage = buyer.getBuyerPackage();
							// Check Active users for buyer
							long activeUsers = userDao.findTotalRegisteredOrActiveUserForTenant(buyer.getId(), true);
							if (activeUsers > nextSubscription.getUserQuantity()) {
								LOG.info("buyer name :" + buyer.getLoginEmail());
								// Deactivate notification to all users except admin owner buyer
								try {
									// For buyer Admin
									User buyerUser = userDao.getPlainUserByLoginId(buyer.getLoginEmail());
									timeZone = getTimeZoneByBuyerSettings(buyer.getId(), timeZone);
									String msg = "Please manage extra users otherwise all users will be deactivated in 15 days due to active user count mismatch. ";
									sendBuyerUserDeactivateMail(buyerUser, msg, timeZone, subscription, nextSubscription, activeUsers, true);

									// For all users
									// List<User> userList = userDao.fetchAllUsersForTenant(buyer.getId());
									// for (User user : userList) {
									// // Check mail to admin owner and other users
									// if (user.getLoginId().equalsIgnoreCase(buyer.getLoginEmail())) {
									// // Notification to buyer admin owner
									// String msg = "Please manage extra users otherwise all users will be deactivated
									// in 15 days due to active user count mismatch. ";
									// sendBuyerUserDeactivateMail(user, msg, timeZone, subscription, nextSubscription,
									// activeUsers, true);
									// } else {
									// // Notification to buyer admin other users
									// String msg = "your account may be deactivated in 15 days due to changes in
									// subscription, please contact your administrator for more details.";
									// sendBuyerUserDeactivateMail(user, msg, timeZone, subscription, nextSubscription,
									// activeUsers, false);
									// }
									// }
									buyerPackage.setUserDaRemBefore15Day(Boolean.TRUE);
									buyer.setBuyerPackage(buyerPackage);
									buyerService.updateBuyer(buyer);
								} catch (Exception e) {
									LOG.error("Error While Sending mail Buyer Subscription user deactivate before 15 days on next subs  :" + e.getMessage(), e);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error processing Buyer Subscription user deactivate before 15 days on next subs . Error : " + e.getMessage(), e);
		}
	}

	private void buyerExtraUserDeactivateExpiryNotificationBefore7Days() {

		try {
			// Remind before 7 days
			Calendar before7Cal = Calendar.getInstance();
			before7Cal.add(Calendar.DATE, 7);

			// Remind now
			Calendar now = Calendar.getInstance();

			List<Buyer> buyers = buyerService.findAllBuyersFor7DaysBeforeSubscriptionExpire();
			if (CollectionUtil.isNotEmpty(buyers)) {
				String timeZone = "GMT+8:00";
				for (Buyer buyer : buyers) {
					DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

					DateTime before7DT = new DateTime(before7Cal.getTime());
					before7DT = before7DT.withZone(dtZone);
					Date before7Date = before7DT.toLocalDateTime().toDate(); // Convert to date with TimeZone
					DateTime nowDT = new DateTime(now.getTime());
					nowDT = nowDT.withZone(dtZone);
					Date nowDate = nowDT.toLocalDateTime().toDate(); // Convert to date with TimeZone
					BuyerSubscription subscription = buyer.getCurrentSubscription();
					if (subscription != null && subscription.getEndDate().compareTo(nowDate) > 0 && subscription.getEndDate().compareTo(before7Date) < 0 && subscription.getNextSubscription() != null) {
						BuyerSubscription nextSubscription = subscription.getNextSubscription();

						if (nextSubscription.getSubscriptionStatus() == SubscriptionStatus.FUTURE && PlanType.PER_USER == nextSubscription.getPlanType()) {

							BuyerPackage buyerPackage = buyer.getBuyerPackage();
							// Check Active users for buyer
							long activeUsers = userDao.findTotalRegisteredOrActiveUserForTenant(buyer.getId(), true);
							if (activeUsers > nextSubscription.getUserQuantity()) {
								LOG.info("buyer name :" + buyer.getLoginEmail());
								// Deactivate notification to all users except admin owner buyer
								try {
									// For buyer Admin
									User buyerUser = userDao.getPlainUserByLoginId(buyer.getLoginEmail());
									timeZone = getTimeZoneByBuyerSettings(buyer.getId(), timeZone);
									String msg = "Please manage extra users otherwise all users will be deactivated in 7 days due to active user count mismatch. ";
									sendBuyerUserDeactivateMail(buyerUser, msg, timeZone, subscription, nextSubscription, activeUsers, true);

									// For All users
									// List<User> userList = userDao.fetchAllUsersForTenant(buyer.getId());
									// for (User user : userList) {
									// // Check mail to admin owner and other users
									// if (user.getLoginId().equalsIgnoreCase(buyer.getLoginEmail())) {
									// // Notification to buyer admin owner
									// String msg = "Please manage extra users otherwise all users will be deactivated
									// in 7 days due to active user count mismatch. ";
									// sendBuyerUserDeactivateMail(user, msg, timeZone, subscription, nextSubscription,
									// activeUsers, true);
									// } else {
									// // Notification to buyer admin other users
									// String msg = "your account may be deactivated in 7 days due to changes in
									// subscription, please contact your administrator for more details.";
									// sendBuyerUserDeactivateMail(user, msg, timeZone, subscription, nextSubscription,
									// activeUsers, false);
									// }
									// }
									buyerPackage.setUserDaRemBefore7Day(Boolean.TRUE);
									buyer.setBuyerPackage(buyerPackage);
									buyerService.updateBuyer(buyer);
								} catch (Exception e) {
									LOG.error("Error While Sending mail Buyer Subscription user deactivate before 7 days on next subs  :" + e.getMessage(), e);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error processing Buyer Subscription user deactivate before 7 days on next subs . Error : " + e.getMessage(), e);
		}
	}

	private void buyerExtraUserDeactivateExpiryNotificationBefore2Days() {

		try {
			// Remind before 2 days
			Calendar before2Cal = Calendar.getInstance();
			before2Cal.add(Calendar.DATE, 2);

			// Remind now
			Calendar now = Calendar.getInstance();

			List<Buyer> buyers = buyerService.findAllBuyersFor2DaysBeforeSubscriptionExpire();
			if (CollectionUtil.isNotEmpty(buyers)) {
				String timeZone = "GMT+8:00";
				for (Buyer buyer : buyers) {
					DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

					DateTime before2DT = new DateTime(before2Cal.getTime());
					before2DT = before2DT.withZone(dtZone);
					Date before2Date = before2DT.toLocalDateTime().toDate(); // Convert to date with TimeZone
					DateTime nowDT = new DateTime(now.getTime());
					nowDT = nowDT.withZone(dtZone);
					Date nowDate = nowDT.toLocalDateTime().toDate(); // Convert to date with TimeZone
					BuyerSubscription subscription = buyer.getCurrentSubscription();
					if (subscription != null && subscription.getEndDate().compareTo(nowDate) > 0 && subscription.getEndDate().compareTo(before2Date) < 0 && subscription.getNextSubscription() != null) {
						BuyerSubscription nextSubscription = subscription.getNextSubscription();

						if (nextSubscription.getSubscriptionStatus() == SubscriptionStatus.FUTURE && PlanType.PER_USER == nextSubscription.getPlanType()) {
							LOG.info("buyer name :" + buyer.getLoginEmail());

							BuyerPackage buyerPackage = buyer.getBuyerPackage();
							// Check Active users for buyer
							long activeUsers = userDao.findTotalRegisteredOrActiveUserForTenant(buyer.getId(), true);
							if (activeUsers > nextSubscription.getUserQuantity()) {
								// Deactivate notification to all users except admin owner buyer
								try {
									List<User> userList = userDao.fetchAllUsersForTenant(buyer.getId());
									timeZone = getTimeZoneByBuyerSettings(buyer.getId(), timeZone);
									for (User user : userList) {
										// Check mail to admin owner and other users
										if (user.getLoginId().equalsIgnoreCase(buyer.getLoginEmail())) {
											// Notification to buyer admin owner
											String msg = "Please manage extra users otherwise all users will be deactivated in 2 days due to active user count mismatch. ";
											sendBuyerUserDeactivateMail(user, msg, timeZone, subscription, nextSubscription, activeUsers, true);
										} else {
											// Notification to buyer admin other users
											String msg = "your account may be deactivated in 2 days due to changes in subscription, please contact your administrator for more details.";
											sendBuyerUserDeactivateMail(user, msg, timeZone, subscription, nextSubscription, activeUsers, false);
										}
									}
									buyerPackage.setUserDaRemBefore2Day(Boolean.TRUE);
									buyer.setBuyerPackage(buyerPackage);
									buyerService.updateBuyer(buyer);
								} catch (Exception e) {
									LOG.error("Error While Sending mail Buyer Subscription user deactivate before 2 days on next subs  :" + e.getMessage(), e);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error processing Buyer Subscription user deactivate before 2 days on next subs . Error : " + e.getMessage(), e);
		}
	}

	private void sendBuyerUserDeactivateMail(User user, String msg, String timeZone, BuyerSubscription currentSubs, BuyerSubscription nextSubs, long activeUsers, boolean adminUser) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String mailTo = user.getCommunicationEmail();
		String subject = "Account Deactive";
		String appUrl = APP_URL + "/buyer/billing/accountOverview";
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("userName", user.getName());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", appUrl);
		map.put("adminUser", adminUser ? "true" : "");
		map.put("currentSubs", currentSubs.getPlan() != null ? currentSubs.getPlan().getPlanName() : "");
		map.put("nextSubs", nextSubs.getPlan() != null ? nextSubs.getPlan().getPlanName() : "");
		df = new SimpleDateFormat("dd/MM/yyyy");
		map.put("nextSubsStartDate", df.format(nextSubs.getStartDate()));
		map.put("activeUsers", activeUsers);
		map.put("revisedUsers", nextSubs.getUserQuantity());

		map.put("msg", msg);
		if(user.getEmailNotifications()) {
			notificationService.sendEmail(mailTo, subject, map, Global.USER_DEACTIVATE_REMINDER);
		}
		try {
			sendDashboardNotification(user, appUrl, subject, msg);
		} catch (Exception e) {
			LOG.error("Error While Sending Dashboard Buyer Subscription user deactivate on next subs notification :" + e.getMessage(), e);
		}
	}
}
