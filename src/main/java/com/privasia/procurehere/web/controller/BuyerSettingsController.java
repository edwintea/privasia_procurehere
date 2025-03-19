package com.privasia.procurehere.web.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.EventSettings;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.BusinessUnitPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventSettingsService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.TimeZoneService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.TimeZoneEditor;
import com.stripe.Stripe;
import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import com.stripe.model.WebhookEndpoint;
import com.stripe.model.WebhookEndpointCollection;

@Controller
@RequestMapping("/buyer")
public class BuyerSettingsController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String HMAC_SHA512 = "HmacSHA256";
	private static final String SECRET_KEY = "xyz0123456789";
	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	TimeZoneService timeZoneService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	TimeZoneEditor timeZoneEditor;

	@Resource
	MessageSource messageSource;

	@Autowired
	BuyerService buyerService;

	@Autowired
	UserService userService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	EventSettingsService eventSettingsService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	CostCenterService costCenterService;

	@Value("${app.url}")
	String APP_URL;

	@Value("${environment-key}")
	String ENVIRONMENT_KEY;

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Currency.class, "currency", currencyEditor);
		binder.registerCustomEditor(TimeZone.class, "timeZone", timeZoneEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@RequestMapping(path = "/buyerSettings", method = RequestMethod.GET)
	public ModelAndView createBuyerSettings(Model model) throws JsonProcessingException {
		BuyerSettings buyerSettings = new BuyerSettings();
		EventSettings eventSettings = null;

		LOG.info("buyer Settings create Called");
		model.addAttribute("currency", currencyService.getAllCurrency());
		List<com.privasia.procurehere.core.entity.TimeZone> timeZones = timeZoneService.findAllActiveTimeZone();
		model.addAttribute("timeZone", timeZones);
		try {
			buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUserTenantId());
			if (buyer != null) {
				model.addAttribute("enableEventPublishing", buyer.getEnableEventPublishing() != null ? buyer.getEnableEventPublishing() : null);
			}
			if (StringUtils.checkString(buyerSettings.getId()).length() == 0) {
				LOG.info("save fu nction called BuyerSettings Called");
				buyerSettings.setCreatedBy(SecurityLibrary.getLoggedInUser());
				buyerSettings.setCreatedDate(new Date());
				buyerSettings.setTenantId(SecurityLibrary.getLoggedInUserTenantId());

				for (com.privasia.procurehere.core.entity.TimeZone timeZone : timeZones) {
					if (timeZone.getTimeZone().equalsIgnoreCase("GMT+8:00")) {
						buyerSettings.setTimeZone(timeZone);
						break;
					}
				}
				buyerSettingsService.saveBuyerSettings(buyerSettings, SecurityLibrary.getLoggedInUser());
				LOG.info("Create BuyerSettings Called : " + SecurityLibrary.getLoggedInUserLoginId() + " for buyer : " + SecurityLibrary.getLoggedInUserTenantId());
			} else {
				List<BusinessUnitPojo> businessUnitList = businessUnitService.getBusinessUnitIdByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (CollectionUtil.isNotEmpty(businessUnitList)) {
					for (BusinessUnitPojo unit : businessUnitList) {
						boolean flag = true;
						long assignedCostId = businessUnitService.getCountCostCentersByBusinessUnitId(unit.getId(), null);
						model.addAttribute("assignCostCenter", assignedCostId > 0);
						//If any one BU as cost center set a
						if (assignedCostId > 0) {
							break;
						}
					}
				} else {
					model.addAttribute("assignCostCenter", false);
				}
			}

			eventSettings = eventSettingsService.getEventSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			eventSettings.setAutoCreatePo(buyerSettings.getAutoCreatePo());
			eventSettings.setAutoPublishPo(buyerSettings.getAutoPublishPo());
			model.addAttribute("eventSettings", eventSettings);

			List<TimeZone> timeZonesList = new ArrayList<TimeZone>();
			if (buyerSettings.getTimeZone() != null) {
				timeZonesList.add(new TimeZone(buyerSettings.getTimeZone().getId(), buyerSettings.getTimeZone().getTimeZone(), buyerSettings.getTimeZone().getTimeZoneDescription()));
			}
			for (TimeZone timeZone : timeZones) {
				boolean isTZExist = false;
				if (buyerSettings.getTimeZone() != null && buyerSettings.getTimeZone().getId().equals(timeZone.getId())) {
					isTZExist = true;
				}
				if (!isTZExist) {
					timeZonesList.add(timeZone);
				}
			}
			model.addAttribute("timeZone", timeZonesList);

			List<Currency> currencies = currencyService.fetchAllActiveCurrencies(null);
			List<Currency> currencyList = new ArrayList<Currency>();
			if (buyerSettings.getCurrency() != null) {
				currencyList.add(new Currency(buyerSettings.getCurrency().getId(), buyerSettings.getCurrency().getCurrencyName()));
			}
			for (Currency cur : currencies) {
				boolean isCurExist = false;
				if (buyerSettings.getCurrency() != null && buyerSettings.getCurrency().getId().equals(cur.getId())) {
					isCurExist = true;
				}
				if (!isCurExist) {
					currencyList.add(cur);
				}
			}
			model.addAttribute("currency", currencyList);

			return new ModelAndView("buyerSettings", "buyerSettings", buyerSettings);
		} catch (Exception e) {
			return new ModelAndView("buyerSettings", "buyerSettings", buyerSettings);
		}
	}

	@RequestMapping(path = "/buyerSettings", method = RequestMethod.POST)
	public ModelAndView saveBuyerSettings(@Valid @ModelAttribute("buyerSettings") BuyerSettings buyerSettings, BindingResult result, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("Buyer Settings Save Called : ");
		List<String> errMessages = new ArrayList<String>();
		try {
			model.addAttribute("currency", currencyService.getAllCurrency());
			model.addAttribute("timeZone", timeZoneService.findAllActiveTimeZone());
			EventSettings eventSettings = eventSettingsService.getEventSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			eventSettings.setAutoCreatePo(buyerSettings.getAutoCreatePo());
			eventSettings.setAutoPublishPo(buyerSettings.getAutoPublishPo());
			model.addAttribute("eventSettings", eventSettings);
			if (result.hasErrors()) {
				for (ObjectError oe : result.getAllErrors()) {
					errMessages.add(oe.getDefaultMessage());
				}
				model.addAttribute("errors", errMessages);
				throw new ApplicationException("Testing");
				// return new ModelAndView("buyerSettings");
			} else {
				LOG.info("Do validate called BuyerSettings Called");
				if (StringUtils.checkString(buyerSettings.getId()).length() == 0) {

					LOG.info("save fu nction called BuyerSettings Called");
					buyerSettings.setCreatedBy(SecurityLibrary.getLoggedInUser());
					buyerSettings.setCreatedDate(new Date());
					buyerSettings.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					buyerSettingsService.saveBuyerSettings(buyerSettings, SecurityLibrary.getLoggedInUser());
					redir.addFlashAttribute("success", messageSource.getMessage("generalSettings.create.success", new Object[] {}, Global.LOCALE));
					LOG.info("Create BuyerSettings Called : " + SecurityLibrary.getLoggedInUserLoginId() + " for buyer : " + SecurityLibrary.getLoggedInUserTenantId());
					// Update the timezone settings in the current session
					if (buyerSettings.getTimeZone() != null) {
						session.setAttribute(Global.SESSION_TIME_ZONE_KEY, buyerSettings.getTimeZone().getTimeZone());
						session.setAttribute(Global.SESSION_TIME_ZONE_LOCATION_KEY, buyerSettings.getTimeZone().getTimeZoneDescription());
					}
					return new ModelAndView("redirect:buyerSettings");
				} else {
					LOG.info("UPDATE BuyerSettings Called" + buyerSettings.getCurrency());
					BuyerSettings persistObj = buyerSettingsService.getBuyerSettingsById(buyerSettings.getId());
					persistObj.setCurrency(buyerSettings.getCurrency());
					persistObj.setDecimal(buyerSettings.getDecimal());
					persistObj.setTimeZone(buyerSettings.getTimeZone());
					persistObj.setErpNotificationEmails(buyerSettings.getErpNotificationEmails());
					if (StringUtils.checkString(buyerSettings.getRfiPublishUrl()).length() > 0) {
						persistObj.setRfiPublishUrl(buyerSettings.getRfiPublishUrl());
					}
					if (StringUtils.checkString(buyerSettings.getRfqPublishUrl()).length() > 0) {
						persistObj.setRfqPublishUrl(buyerSettings.getRfqPublishUrl());
					}
					if (StringUtils.checkString(buyerSettings.getRftPublishUrl()).length() > 0) {
						persistObj.setRftPublishUrl(buyerSettings.getRftPublishUrl());
					}
					if (StringUtils.checkString(buyerSettings.getRfiUpdatePublishUrl()).length() > 0) {
						persistObj.setRfiUpdatePublishUrl(buyerSettings.getRfiUpdatePublishUrl());
					}
					if (StringUtils.checkString(buyerSettings.getRfqUpdatePublishUrl()).length() > 0) {
						persistObj.setRfqUpdatePublishUrl(buyerSettings.getRfqUpdatePublishUrl());
					}
					if (StringUtils.checkString(buyerSettings.getRftUpdatePublishUrl()).length() > 0) {
						persistObj.setRftUpdatePublishUrl(buyerSettings.getRftUpdatePublishUrl());
					}
					persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
					persistObj.setModifiedDate(new Date());
					// persistObj.setAutoCreatePo(buyerSettings.getAutoCreatePo());
					// persistObj.setAutoPublishPo(buyerSettings.getAutoPublishPo());

					// if (buyerSettings.getEnableUnitAndCostCorrelation() == Boolean.FALSE) {
					// List<BusinessUnitPojo> businessUnitList =
					// businessUnitService.getBusinessUnitIdByTenantId(SecurityLibrary.getLoggedInUserTenantId());
					// if (CollectionUtil.isNotEmpty(businessUnitList)) {
					// for (BusinessUnitPojo unit : businessUnitList) {
					// costCenterService.deleteAssignedCostCenter(unit.getId());
					// }
					// }
					// }
					persistObj.setEnableUnitAndCostCorrelation(buyerSettings.getEnableUnitAndCostCorrelation());
					persistObj.setEnableUnitAndGrpCodeCorrelation(buyerSettings.getEnableUnitAndGrpCodeCorrelation());

					LOG.info("Environment key is: " + ENVIRONMENT_KEY);
					if (StringUtils.isBlank(ENVIRONMENT_KEY)) {
						if (buyerSettings.getStripePublishKey() != null && buyerSettings.getStripePublishKey().indexOf("live") == -1) {
							LOG.error("Error in saving BuyerSettings " + "Publush key is not for live mode");
							model.addAttribute("error", "Publish key cannot be in test mode.");
							return new ModelAndView("buyerSettings");
						}

						if (buyerSettings.getStripeSecretKey() != null && buyerSettings.getStripeSecretKey().indexOf("live") == -1) {
							LOG.error("Error in saving BuyerSettings " + "Secret key is not for live mode");
							model.addAttribute("error", "Secret key cannot be in test mode.");
							return new ModelAndView("buyerSettings");
						}
					}

					persistObj.setStripePublishKey(buyerSettings.getStripePublishKey());
					persistObj.setStripeSecretKey(buyerSettings.getStripeSecretKey());

					// Configure webhook endpoint for stripe

					try {
						if (StringUtils.isNotBlank(buyerSettings.getStripeSecretKey()) && StringUtils.isNotBlank(buyerSettings.getStripePublishKey())) {
							Stripe.apiKey = buyerSettings.getStripeSecretKey();
							Map<String, Object> param = new HashMap<>();
							param.put("limit", 10);
							WebhookEndpointCollection webhookEndpoints = WebhookEndpoint.list(param);

							if (webhookEndpoints.getData() != null && webhookEndpoints.getData().size() == 0) {
								List<Object> enabledEvents = new ArrayList<>();
								enabledEvents.add("charge.succeeded");
								enabledEvents.add("payment_intent.succeeded");
								enabledEvents.add("charge.failed");
								enabledEvents.add("payment_intent.payment_failed");
								enabledEvents.add("payment_intent.canceled");
								Map<String, Object> params = new HashMap<>();
								params.put("url", APP_URL + "/supplier/paymentEvents");
								params.put("enabled_events", enabledEvents);
								WebhookEndpoint webhookEndpoint = WebhookEndpoint.create(params);
								LOG.info("Webhook endpoint updated : " + webhookEndpoint.getId());
							} else {
								LOG.info("Webhook endpoint already exists for this buyer. Number of endpoints are: " + webhookEndpoints.getData().size());
							}
						}
					} catch (StripeException e) {
						String message = null;
						if (e instanceof ApiException) {
							message = "Stripe API Exception, Please check the Stripe secret key";
						} else {
							message = e.getLocalizedMessage();
						}
						LOG.error("Stripe exception: " + e.getLocalizedMessage());
						model.addAttribute("error", message);
						return new ModelAndView("buyerSettings");
					} catch (Exception e) {
						LOG.error("Error setting up webhook endpoint: " + e.getMessage());
						model.addAttribute("error", e.getLocalizedMessage());
						return new ModelAndView("buyerSettings");
					}

					buyerSettingsService.updateBuyerSettings(persistObj, SecurityLibrary.getLoggedInUser());
					redir.addFlashAttribute("success", messageSource.getMessage("generalSettings.update.success", new Object[] {}, Global.LOCALE));
					model.addAttribute("btnValue", "Update");
					LOG.info("update BuyerSettings Called : " + SecurityLibrary.getLoggedInUserLoginId() + " for buyer : " + SecurityLibrary.getLoggedInUserTenantId());

					// Update the timezone settings in the current session
					if (buyerSettings.getTimeZone() != null) {
						session.setAttribute(Global.SESSION_TIME_ZONE_KEY, buyerSettings.getTimeZone().getTimeZone());
						session.setAttribute(Global.SESSION_TIME_ZONE_LOCATION_KEY, buyerSettings.getTimeZone().getTimeZoneDescription());
					}

					return new ModelAndView("redirect:buyerSettings?view=general");
				}
			}
		} catch (Exception e) {
			LOG.error("Error in saving BuyerSettings " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("buyerSettings.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("page", "general");
			return new ModelAndView("buyerSettings");
		}
	}

	@RequestMapping(value = "/closeAccount")
	public ModelAndView closeAccount(Model model, RedirectAttributes redir) {

		LOG.info("buyer closing account Called");
		model.addAttribute("currency", currencyService.getAllCurrency());
		model.addAttribute("timeZone", timeZoneService.findAllActiveTimeZone());
		try {
			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			String msg = "User requested to Export Data & Close Account";
			if (buyerSettings != null) {

				User owner = userService.getAdminUser();
				List<User> users = userService.getAllAdminUsersForBuyer(SecurityLibrary.getLoggedInUserTenantId());
				if (buyerSettings.getIsClose() == Boolean.FALSE) {
					LOG.info("buyer close request");
					buyerSettings.setIsClose(Boolean.TRUE);
					buyerSettings.setCloseRequestDate(new Date());
					buyerSettings.setRequestedBy(SecurityLibrary.getLoggedInUser());
					for (User user : users) {
						LOG.info("================buyer close======================" + user.getCommunicationEmail());
						sendNotificationMailToAdmin(user, SecurityLibrary.getLoggedInUser());
					}
					sendNotificationMailToOwner(owner, SecurityLibrary.getLoggedInUser());
					redir.addFlashAttribute("success", messageSource.getMessage("colse.account.request.placed", new Object[] {}, Global.LOCALE));
				} else {
					LOG.info("buyer cancal request");
					buyerSettings.setIsClose(Boolean.FALSE);
					buyerSettings.setCancalRequestDate(new Date());

					for (User user : users) {
						LOG.info("================buyer close======================" + user.getCommunicationEmail());
						cancelRequestNotificationMailToAdmin(user, SecurityLibrary.getLoggedInUser());
					}
					msg = "User requested to Cancel Close Account";
					cancelRequestNotificationMailToOwner(owner, SecurityLibrary.getLoggedInUser());
					redir.addFlashAttribute("success", messageSource.getMessage("colse.account.request.canceled", new Object[] {}, Global.LOCALE));
				}
				buyerSettingsService.updateBuyerSettingsWithAudit(buyerSettings, SecurityLibrary.getLoggedInUser(), msg);

			}
		} catch (Exception e) {
			LOG.info("error while close Account " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("colse.account.request.error", new Object[] {}, Global.LOCALE));
		}

		return new ModelAndView("redirect:/buyer/buyerSettings");

	}

	private void cancelRequestNotificationMailToOwner(User mailTo, User user) {
		LOG.info("Sending acount closing cancel request email to owner (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

		String subject = "Request to Close Account by Buyer Cancellation";
		String url = APP_URL;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("message", "Request for closing account is canceled by " + user.getBuyer().getCompanyName());
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications() ) {
			sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.CLOSE_ACCOUNT_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = "Request for closing account cancelled";
		sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.GENERAL);

	}

	private void sendNotificationMailToOwner(User mailTo, User user) {
		LOG.info("Sending acount closing to Owner request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

		String subject = "Request to Close Account by Buyer";
		String url = APP_URL;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("message", "Request for closing account is placed by " + user.getBuyer().getCompanyName());
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
			sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.CLOSE_ACCOUNT_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = "Request for closing account cancelled";
		sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.GENERAL);

	}

	private void sendNotificationMailToAdmin(User mailTo, User user) {

		LOG.info("Sending acount closing request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

		String subject = "Request to Close Account";
		String url = APP_URL + "/buyer/buyerSettings";
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());

		map.put("message", "Request by " + user.getName() + " for closing account is successfully placed.");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
			sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.CLOSE_ACCOUNT_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = "Request for closing account placed";
		sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.GENERAL);

	}

	private void cancelRequestNotificationMailToAdmin(User mailTo, User user) {

		LOG.info("Sending acount closing cancel request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

		String subject = "Request to Close Account Cancellation";
		String url = APP_URL + "/buyer/buyerSettings";
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("message", "Request for closing account is successfully canceled by " + user.getName());
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
			sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.CLOSE_ACCOUNT_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = "Request for closing account cancelled";
		sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.GENERAL);

	}

	private String getTimeZoneByBuyerSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = buyerSettingsService.getBuyerTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage, NotificationType notificationType) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(notificationType);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				notificationService.sendEmail(mailTo, subject, map, template);
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	@RequestMapping(value = "/exportAccount")
	public ModelAndView exportAccount(Model model, RedirectAttributes redir) {

		LOG.info("buyer export account Called");
		model.addAttribute("currency", currencyService.getAllCurrency());
		model.addAttribute("timeZone", timeZoneService.findAllActiveTimeZone());
		try {
			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());

			if (buyerSettings != null) {

				// User owner = userService.getAdminUser();
				// List<User> users = userService.getAllAdminUsersForBuyer(SecurityLibrary.getLoggedInUserTenantId());
				LOG.info("buyer export request");
				buyerSettings.setIsBackup(Boolean.TRUE);
				buyerSettings.setIsExport(Boolean.FALSE);
				buyerSettings.setExportURL(null);
				/*
				 * buyerSettings.setCloseRequestDate(new Date());
				 * buyerSettings.setRequestedBy(SecurityLibrary.getLoggedInUser()); for (User user : users) {
				 * sendNotificationMailToAdmin(user, SecurityLibrary.getLoggedInUser()); }
				 * sendNotificationMailToOwner(owner, SecurityLibrary.getLoggedInUser());
				 */
				redir.addFlashAttribute("success", messageSource.getMessage("export.account.request.placed", new Object[] {}, Global.LOCALE));
				/*
				 * else { LOG.info("buyer cancal request"); buyerSettings.setIsClose(Boolean.FALSE);
				 * buyerSettings.setCancalRequestDate(new Date()); for (User user : users) {
				 * cancelRequestNotificationMailToAdmin(user, SecurityLibrary.getLoggedInUser()); }
				 * cancelRequestNotificationMailToOwner(owner, SecurityLibrary.getLoggedInUser());
				 * redir.addFlashAttribute("success", messageSource.getMessage("colse.account.request.canceled", new
				 * Object[] {}, Global.LOCALE)); }
				 */

				buyerSettingsService.updateBuyerSettingsWithAudit(buyerSettings, SecurityLibrary.getLoggedInUser(), "User requested to Export Data");

			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("export.account.request.error", new Object[] {}, Global.LOCALE));
		}

		return new ModelAndView("redirect:/buyer/buyerSettings");

	}

	@RequestMapping(value = "/keyGenerateForBuyer", method = RequestMethod.POST)
	public ResponseEntity<String> keyGenerateForBuyer() {
		char[] chararray = new char[10];
		for (int i = 0; i < 10; i++) {
			chararray[i] = genrateRandomNumber();
		}

		String key = hmacSha512(chararray.toString());
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<String>(key, headers, HttpStatus.OK);
	}

	public static String hmacSha512(String value) {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(DEFAULT_ENCODING), HMAC_SHA512);

			Mac mac = Mac.getInstance(HMAC_SHA512);
			mac.init(keySpec);
			return toHexString(mac.doFinal(value.getBytes(DEFAULT_ENCODING)));

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static char genrateRandomNumber() {

		String tempString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 62) { // length of the random string.
			int index = (int) (rnd.nextFloat() * tempString.length());
			salt.append(tempString.charAt(index));
		}
		// String saltStr = salt.toString();
		return salt.charAt(0);
	}

	private static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String returnStr = formatter.toString();
		if (formatter != null) {
			formatter.close();
			formatter = null;
		}
		return returnStr;
	}

	@RequestMapping(path = "/apiSettings", method = RequestMethod.GET)
	public String createBuyerApiSettings(Model model) throws JsonProcessingException {
		try {
			LOG.info("apiSettings create Called");
			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("buyerSettings", buyerSettings);
		} catch (Exception e) {
			LOG.error(e);
			model.addAttribute("error", e.getMessage());
		}
		return "apiSettings";
	}

	@RequestMapping(path = "/apiSettings", method = RequestMethod.POST)
	public String saveBuyerApiSetting(@ModelAttribute BuyerSettings buyerSettings, Model model) {
		try {
			User user = SecurityLibrary.getLoggedInUser();
			BuyerSettings persistObject = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			persistObject.setBuyerKey(buyerSettings.getBuyerKey());
			persistObject.setModifiedBy(user);
			persistObject.setModifiedDate(new Date());
			persistObject = buyerSettingsService.updateBuyerSettings(persistObject, user);
			model.addAttribute("success", messageSource.getMessage("api.setting.updated", new Object[] {}, Global.LOCALE));
			model.addAttribute("buyerSettings", persistObject);
			LOG.info("buyerSetting updated");
		} catch (Exception e) {
			LOG.error(e);
			model.addAttribute("error", "Error while updating buyer setting");
		}
		return "apiSettings";
	}

	@RequestMapping(path = "/saveEventSettings", method = RequestMethod.POST)
	public ModelAndView saveEventSettings(@Valid @ModelAttribute("eventSettings") EventSettings eventSettings, Model model, RedirectAttributes redir, HttpSession session) {

		LOG.info("Save Event Settings called .........................: " + eventSettings.getId());
		try {
			eventSettings.setBuyer(new Buyer());
			eventSettings.getBuyer().setId(SecurityLibrary.getLoggedInUserTenantId());
			eventSettingsService.updateEventSettings(eventSettings, SecurityLibrary.getLoggedInUser());

			redir.addFlashAttribute("success", messageSource.getMessage("eventSettings.update.success", new Object[] {}, Global.LOCALE));

			// return new ModelAndView("redirect:/buyer/buyerSettings?view=event");

		} catch (Exception e) {
			LOG.error("Error is ..........: " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("eventSettings.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return new ModelAndView("redirect:/buyer/buyerSettings?view=event");
	}

	@PostMapping("/searchTimeZoneFromList")
	public @ResponseBody ResponseEntity<List<TimeZone>> searchTimeZoneFromList(@RequestParam("timeZone") String timeZone) {
		List<TimeZone> TimeZoneList = timeZoneService.findTimeZonesForTenantId(SecurityLibrary.getLoggedInUserTenantId(), timeZone);
		return new ResponseEntity<List<TimeZone>>(TimeZoneList, HttpStatus.OK);
	}

	@PostMapping("/searchCurrencyFromList")
	public @ResponseBody ResponseEntity<List<Currency>> searchCurrencyFromList(@RequestParam("currencyName") String currencyName) {
		List<Currency> currencyList = currencyService.fetchAllActiveCurrencies(currencyName);
		return new ResponseEntity<List<Currency>>(currencyList, HttpStatus.OK);
	}

	@PostMapping("/checkForAssignedCCForBU")
	public @ResponseBody ResponseEntity<Boolean> checkForAssignedCCForBU() {
		LOG.info("Checking CCs for BU...............");
		Boolean presentForAll = true;
		try {
			List<BusinessUnit> bUnits = businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId());
			for (BusinessUnit unit : bUnits) {
				List<String> ccIds = costCenterService.getListOfAssignedCostCenterIdsForBusinessUnit(unit.getId());
				if (CollectionUtil.isEmpty(ccIds)) {
					presentForAll = false;
					break;
				}
			}
			return new ResponseEntity<Boolean>(presentForAll, HttpStatus.OK);
		} catch (Exception e) {
			LOG.info("Error is .. :" + e.getMessage(), e);
			return new ResponseEntity<Boolean>(presentForAll, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
