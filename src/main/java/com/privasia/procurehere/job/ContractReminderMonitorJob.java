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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.NotificationMessageDao;
import com.privasia.procurehere.core.dao.ProductContractDao;
import com.privasia.procurehere.core.dao.ProductContractReminderDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.ProductContract;
import com.privasia.procurehere.core.entity.ProductContractNotifyUsers;
import com.privasia.procurehere.core.entity.ProductContractReminder;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.NotificationService;

/**
 * @author ravi
 */
@Component
public class ContractReminderMonitorJob implements BaseSchedulerJob {

	private static final Logger LOG = LogManager.getLogger(ContractReminderMonitorJob.class);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	ProductContractDao productContractDao;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	UserDao userDao;

	@Autowired
	NotificationService notificationService;

	@Autowired
	NotificationMessageDao notificationMessageDao;

	@Autowired
	ProductContractReminderDao productContractReminderDao;

	@Override
	@Transactional(readOnly = false)
	public void execute(JobExecutionContext ctx) {
		try {
			checkMeetingReminders();
		} catch (Exception e) {
			LOG.error("Error while Executing contract reminder job :" + e.getMessage(), e);
		}
	}

	private void checkMeetingReminders() {

		// Sending Mails and Notifications for default contract reminders
		reminderBefore180Days();
		reminderBefore90Days();
		reminderBefore30Days();

		sendCustomReminders();
	}

	private void reminderBefore180Days() {
		try {
			LOG.info("Sending contract reminder notification before 180 days");
			// Remind before 180 Days
			Calendar before180Days = Calendar.getInstance();
			before180Days.add(Calendar.DATE, 180);

			// Remind before 170 Days
			Calendar before170Days = Calendar.getInstance();
			before170Days.setTime(before180Days.getTime());
			before170Days.add(Calendar.DATE, -10);

			List<ProductContract> contacts = productContractDao.contractExpiryNotificationReminderBefore180Days();
			if (CollectionUtil.isNotEmpty(contacts)) {
				for (ProductContract contract : contacts) {

					String timeZone = "GMT+8:00";
					timeZone = getTimeZoneByBuyerSettings(contract.getBuyer().getId(), timeZone);

					DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

					DateTime before180DaysDT = new DateTime(before180Days.getTime());
					before180DaysDT = before180DaysDT.withZone(dtZone);
					Date before120DaysDate = before180DaysDT.toLocalDateTime().toDate(); // Convert to date with
																							// TimeZone

					DateTime before170DaysDT = new DateTime(before170Days.getTime());
					before170DaysDT = before170DaysDT.withZone(dtZone);
					Date before110DaysDate = before170DaysDT.toLocalDateTime().toDate(); // Convert to date with
																							// TimeZone

					if (contract != null && contract.getContractEndDate() != null &&  contract.getContractEndDate().compareTo(before110DaysDate) > 0 && contract.getContractEndDate().compareTo(before120DaysDate) < 0) {
						try {
							List<User> adminUsers = userDao.getAllAdminUsersForBuyer(contract.getBuyer().getId());
							List<ProductContractNotifyUsers> notifiUsers = contract.getNotifyUsers();
							String msg = "Contract " + contract.getContractReferenceNumber() + " is Expirying in 180 days ";
							for (User au : adminUsers) {
								sendContractExpiryMail(au, msg, timeZone, contract.getContractEndDate(), contract.getContractReferenceNumber(), contract.getSupplierName(), contract.getGroupCodeStr(), (contract.getBusinessUnit() != null ? contract.getBusinessUnit().getDisplayName() : ""));
							}
							for (ProductContractNotifyUsers nu : notifiUsers) {
								sendContractExpiryMail(nu.getUser(), msg, timeZone, contract.getContractEndDate(), contract.getContractReferenceNumber(), contract.getSupplierName(), contract.getGroupCodeStr(), (contract.getBusinessUnit() != null ? contract.getBusinessUnit().getDisplayName() : ""));
							}
							contract.setRemBefore180Day(Boolean.TRUE);
							productContractDao.update(contract);
						} catch (Exception e) {
							LOG.error("Error While Sending Contract Expiry Reminder mail 120 days before :" + e.getMessage(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while sending mail 120 days before to expiry date : " + e.getMessage(), e);
		}
	}

	private void reminderBefore90Days() {
		try {
			LOG.info("Sending contract reminder notification before 90 days");
			// Remind before 90 Days
			Calendar before90Days = Calendar.getInstance();
			before90Days.add(Calendar.DATE, 90);

			// Remind before 80 Days
			Calendar before80Days = Calendar.getInstance();
			before80Days.setTime(before90Days.getTime());
			before80Days.add(Calendar.DATE, -10);

			List<ProductContract> contacts = productContractDao.contractExpiryNotificationReminderBefore90Days();
			if (CollectionUtil.isNotEmpty(contacts)) {
				for (ProductContract contract : contacts) {

					String timeZone = "GMT+8:00";
					timeZone = getTimeZoneByBuyerSettings(contract.getBuyer().getId(), timeZone);

					DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

					DateTime before90DaysDT = new DateTime(before90Days.getTime());
					before90DaysDT = before90DaysDT.withZone(dtZone);
					Date before90DaysDate = before90DaysDT.toLocalDateTime().toDate(); // Convert to date with TimeZone

					DateTime before80DaysDT = new DateTime(before80Days.getTime());
					before80DaysDT = before80DaysDT.withZone(dtZone);
					Date before80DaysDate = before80DaysDT.toLocalDateTime().toDate(); // Convert to date with TimeZone

					if (before80DaysDate != null && contract != null && contract.getContractEndDate() != null && contract.getContractEndDate().compareTo(before80DaysDate) > 0 && contract.getContractEndDate().compareTo(before90DaysDate) < 0) {
						try {
							User buyerUser = userDao.getPlainUserByLoginId(contract.getBuyer().getLoginEmail());

							List<ProductContractNotifyUsers> notifiUsers = contract.getNotifyUsers();
							String msg = "Contract " + contract.getContractReferenceNumber() + " is Expirying in 90 days ";
							// Buyer Admin
							sendContractExpiryMail(buyerUser, msg, timeZone, contract.getContractEndDate(), contract.getContractReferenceNumber(), contract.getSupplierName(), contract.getGroupCodeStr(), (contract.getBusinessUnit() != null ? contract.getBusinessUnit().getDisplayName() : ""));
							for (ProductContractNotifyUsers nu : notifiUsers) {
								sendContractExpiryMail(nu.getUser(), msg, timeZone, contract.getContractEndDate(), contract.getContractReferenceNumber(), contract.getSupplierName(), contract.getGroupCodeStr(), (contract.getBusinessUnit() != null ? contract.getBusinessUnit().getDisplayName() : ""));
							}

							contract.setRemBefore90Day(Boolean.TRUE);
							productContractDao.update(contract);
						} catch (Exception e) {
							LOG.error("Error While Sending Contract Expiry Reminder mail 90 days before :" + e.getMessage(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while sending mail 90 days before to expiry date : " + e.getMessage(), e);
		}
	}

	private void reminderBefore30Days() {
		try {
			LOG.info("Sending contract reminder notification before 30 days");
			// Remind before 30 Days
			Calendar before30Days = Calendar.getInstance();
			before30Days.add(Calendar.DATE, 30);

			// Remind before 25 Days
			Calendar before25Days = Calendar.getInstance();
			before25Days.setTime(before30Days.getTime());
			before25Days.add(Calendar.DATE, -5);

			List<ProductContract> contacts = productContractDao.contractExpiryNotificationReminderBefore30Days();
			if (CollectionUtil.isNotEmpty(contacts)) {
				for (ProductContract contract : contacts) {

					String timeZone = "GMT+8:00";
					timeZone = getTimeZoneByBuyerSettings(contract.getBuyer().getId(), timeZone);

					DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));

					DateTime before30DaysDT = new DateTime(before30Days.getTime());
					before30DaysDT = before30DaysDT.withZone(dtZone);
					Date before30DaysDate = before30DaysDT.toLocalDateTime().toDate(); // Convert to date with TimeZone

					DateTime before25DaysDT = new DateTime(before25Days.getTime());
					before25DaysDT = before25DaysDT.withZone(dtZone);
					Date before25DaysDate = before25DaysDT.toLocalDateTime().toDate(); // Convert to date with TimeZone

					if (before25DaysDate != null && contract != null && contract.getContractEndDate() != null && contract.getContractEndDate().compareTo(before25DaysDate) > 0 && contract.getContractEndDate().compareTo(before30DaysDate) < 0) {
						try {
							User buyerUser = userDao.getPlainUserByLoginId(contract.getBuyer().getLoginEmail());
							List<ProductContractNotifyUsers> notifiUsers = contract.getNotifyUsers();
							String msg = "Contract " + contract.getContractReferenceNumber() + " is Expirying in 30 days ";
							// Buyer Admin
							sendContractExpiryMail(buyerUser, msg, timeZone, contract.getContractEndDate(), contract.getContractReferenceNumber(), contract.getSupplierName(), contract.getGroupCodeStr(), (contract.getBusinessUnit() != null ? contract.getBusinessUnit().getDisplayName() : ""));
							for (ProductContractNotifyUsers nu : notifiUsers) {
								sendContractExpiryMail(nu.getUser(), msg, timeZone, contract.getContractEndDate(), contract.getContractReferenceNumber(), contract.getSupplierName(), contract.getGroupCodeStr(), (contract.getBusinessUnit() != null ? contract.getBusinessUnit().getDisplayName() : ""));
							}

							contract.setRemBefore30Day(Boolean.TRUE);
							productContractDao.update(contract);
						} catch (Exception e) {
							LOG.error("Error While Sending contract Expiry Reminder mail 30 days before :" + e.getMessage(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while sending mail 30 days before to expiry date : " + e.getMessage(), e);
		}
	}

	private void sendCustomReminders() {
		List<ProductContractReminder> reminders = productContractReminderDao.getContractRemindersForNotification();
		if (CollectionUtil.isNotEmpty(reminders)) {
			LOG.info("Sending custom contract reminder notification for expiry");
			for (ProductContractReminder reminder : reminders) {
				ProductContract contract = reminder.getProductContract();
				try {
					String timeZone = "GMT+8:00";
					timeZone = getTimeZoneByBuyerSettings(contract.getBuyer().getId(), timeZone);

					User buyerUser = userDao.getPlainUserByLoginId(contract.getBuyer().getLoginEmail());
					List<ProductContractNotifyUsers> notifiUsers = contract.getNotifyUsers();
					String msg = "Contract " + contract.getContractReferenceNumber() + " is Expirying in " + reminder.getInterval() + "days ";
					// Buyer Admin
					sendContractExpiryMail(buyerUser, msg, timeZone, contract.getContractEndDate(), contract.getContractReferenceNumber(), contract.getSupplierName(), contract.getGroupCodeStr(), (contract.getBusinessUnit() != null ? contract.getBusinessUnit().getDisplayName() : ""));

					for (ProductContractNotifyUsers nu : notifiUsers) {
						sendContractExpiryMail(nu.getUser(), msg, timeZone, contract.getContractEndDate(), contract.getContractReferenceNumber(), contract.getSupplierName(), contract.getGroupCodeStr(), (contract.getBusinessUnit() != null ? contract.getBusinessUnit().getDisplayName() : ""));
					}

					reminder.setReminderSent(Boolean.TRUE);
					productContractReminderDao.update(reminder);
				} catch (Exception e) {
					LOG.error("Error While Sending contract Expiry custom Reminder mail :" + e.getMessage(), e);
				}

			}
		}
	}

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

	private void sendContractExpiryMail(User user, String msg, String timeZone, Date endDate, String reference, String supplierCompany, String groupCode, String bu) {
		LOG.info("Sending contract reminder notification for expiry for user :" + user.getCommunicationEmail());
		HashMap<String, Object> map = new HashMap<String, Object>();
		String mailTo = user.getCommunicationEmail();
		String subject = "Contract Expiry Reminder";
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("userName", user.getName());
		map.put("loginUrl", APP_URL + "/login");
		map.put("reference", reference);
		df = new SimpleDateFormat("dd/MM/yyyy");
		map.put("endDate", df.format(endDate));
		map.put("supplierCompany", supplierCompany);
		map.put("groupCode", StringUtils.checkString(groupCode));
		map.put("bu", StringUtils.checkString(bu));
		if(user.getEmailNotifications()) {
			notificationService.sendEmail(mailTo, subject, map, Global.CONTRACT_EXPIRY_REMINDER_TEMPLATE);
		}
		try {
			sendDashboardNotification(user, null, subject, msg);
		} catch (Exception e) {
			LOG.error("Error While Sending Dashboard Buyer Subscription user deactivate on next subs notification :" + e.getMessage(), e);
		}
	}

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
			notificationMessageDao.saveOrUpdate(message);
		} catch (Exception e) {
			LOG.error("Error while saving dashboard notification :" + e.getMessage(), e);
		}
	}

}