package com.privasia.procurehere.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.PoSharingBuyer;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.FinanceCompanyStatus;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PoShare;
import com.privasia.procurehere.core.pojo.PoShareBuyerPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.FinanceCompanyService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PoSharingBuyerService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.TimeZoneService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.BuyerEditorFinance;
import com.privasia.procurehere.web.editors.FinanceCompanyEditor;
import com.privasia.procurehere.web.editors.TimeZoneEditor;

@Controller
@RequestMapping(path = "/supplier")
public class SupplierSettingsController {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	TimeZoneService timeZoneService;

	@Autowired
	TimeZoneEditor timeZoneEditor;

	@Resource
	MessageSource messageSource;

	@Autowired
	UserService userService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	FinanceCompanyEditor financeCompanyEditor;

	@Autowired
	PoSharingBuyerService poSharingBuyerService;

	@Autowired
	FinanceCompanyService financeCompanyService;
	@Autowired
	BuyerEditorFinance buyerEditor;

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(TimeZone.class, timeZoneEditor);
		binder.registerCustomEditor(Buyer.class, buyerEditor);

		binder.registerCustomEditor(FinanceCompany.class, financeCompanyEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@RequestMapping(path = "/supplierSettings", method = RequestMethod.GET)
	public ModelAndView createSupplierSettings(Model model) {
		SupplierSettings supplierSettings = new SupplierSettings();
		try {
			LOG.info("Inside supplier Settings.......");
			List<com.privasia.procurehere.core.entity.TimeZone> timeZones = timeZoneService.findAllActiveTimeZone();
			model.addAttribute("timeZone", timeZones);
			List<FinanceCompany> financeCompanies = financeCompanyService.searchFinanceCompany(FinanceCompanyStatus.ACTIVE.toString(), "Newest", null);
			model.addAttribute("financeCompanies", financeCompanies);
			List<Buyer> buyers = supplierSettingsService.getBuyersForPoSharing(SecurityLibrary.getLoggedInUserTenantId());
			List<Buyer> buyersList = new ArrayList<>();
			List<PoShareBuyerPojo> poSharingBuyers = supplierSettingsService.getPoSharingBuyers(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("buyers" + buyers.size());
			LOG.info("PoShareBuyerPojo" + poSharingBuyers.size());
			
			if (CollectionUtil.isNotEmpty(buyers)) {
				for (Buyer buyer : buyers) {
					if (CollectionUtil.isNotEmpty(poSharingBuyers)) {
						for (PoShareBuyerPojo poShareBuyerPojo : poSharingBuyers) {
							LOG.info(buyer.getId() + "=========" + poShareBuyerPojo.getBuyerId());
							if (buyer.getId().equalsIgnoreCase(poShareBuyerPojo.getBuyerId())) {
								break;
							} else {
								buyersList.add(buyer);
							}

						}

					} else {
						buyersList.add(buyer);
					}
				}
			}

			model.addAttribute("buyers", buyersList);

			model.addAttribute("poSharingBuyer", new PoSharingBuyer());
			ObjectMapper mapper = new ObjectMapper();
			try {
				model.addAttribute("poSharingBuyers", mapper.writeValueAsString(poSharingBuyers));
			} catch (JsonProcessingException e1) {
				LOG.error("Error fetching Supplier Settings : " + e1.getMessage(), e1);
			}

			supplierSettings = supplierSettingsService.getSupplierSettingsByTenantIdWithFinance(SecurityLibrary.getLoggedInUserTenantId());
			if (supplierSettings == null) {
				supplierSettings = new SupplierSettings();
				supplierSettings.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				supplierSettings.setPoShare(PoShare.NONE);
				for (com.privasia.procurehere.core.entity.TimeZone timeZone : timeZones) {
					if (timeZone.getTimeZone().equalsIgnoreCase("GMT+8:00")) {
						supplierSettings.setTimeZone(timeZone);
						break;
					}
				}
				supplierSettings.setModifiedDate(new Date());
				supplierSettings.setModifiedBy(SecurityLibrary.getLoggedInUser());
				
				supplierSettingsService.saveSettings(supplierSettings);

			}
			
			try {
				if (supplierSettings.getFileAttatchment() != null) {
					byte[] encodeBase64 = Base64.encodeBase64(supplierSettings.getFileAttatchment());
					String base64Encoded = new String(encodeBase64, "UTF-8");
					model.addAttribute("logoImg", base64Encoded);
				}
			} catch (Exception e) {
				LOG.error("Error while encoding logo :" + e.getMessage(), e);
			}
			
			return new ModelAndView("supplierSettings", "supplierSettings", supplierSettings);
		} catch (Exception e) {
			LOG.error("Error fetching Supplier Settings : " + e.getMessage(), e);
		}
		return new ModelAndView("supplierSettings", "supplierSettings", supplierSettings);
	}

	@RequestMapping(path = "/supplierSettings", method = RequestMethod.POST)
	public ModelAndView createSupplierSettings(@Valid @ModelAttribute(name = "supplierSettings") SupplierSettings supplierSettings, @RequestParam(value = "logoImg", required = false) MultipartFile logoImg, @RequestParam(value = "removeFile") boolean removeFile, BindingResult result, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("Save supplier Settings Called");
		model.addAttribute("timeZone", timeZoneService.findAllActiveTimeZone());
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {

					errMessages.add(err.getDefaultMessage());
				}
				LOG.info(errMessages);
				model.addAttribute("error", errMessages);
			} else {
				SupplierSettings dbSupplierSettings = supplierSettingsService.getSupplierSettingsByTenantIdWithFinance(SecurityLibrary.getLoggedInUserTenantId());
				if (dbSupplierSettings == null) {
					dbSupplierSettings = new SupplierSettings();
					supplierSettings.setPoShare(PoShare.NONE);
					dbSupplierSettings.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				}
				dbSupplierSettings.setTimeZone(supplierSettings.getTimeZone());
				dbSupplierSettings.setModifiedDate(new Date());
				dbSupplierSettings.setModifiedBy(SecurityLibrary.getLoggedInUser());
				
				String fileName = null;
				if (logoImg != null && !logoImg.isEmpty()) {
					fileName = logoImg.getOriginalFilename();
					LOG.info("fileName ---------- :" + fileName);
					byte[] bytes = logoImg.getBytes();
					dbSupplierSettings.setContentType(logoImg.getContentType());
					dbSupplierSettings.setFileName(fileName);
					dbSupplierSettings.setFileAttatchment(bytes);
					dbSupplierSettings.setFileSizeKb(bytes.length > 0 ? bytes.length / 1024 : 0);
				}
				if (removeFile) {
					dbSupplierSettings.setContentType(null);
					dbSupplierSettings.setFileName(null);
					dbSupplierSettings.setFileAttatchment(null);
					dbSupplierSettings.setFileSizeKb(null);
				}
				
				supplierSettings = supplierSettingsService.updateSettings(dbSupplierSettings);

				if (supplierSettings.getTimeZone() != null) {
					session.setAttribute(Global.SESSION_TIME_ZONE_KEY, supplierSettings.getTimeZone().getTimeZone());
					session.setAttribute(Global.SESSION_TIME_ZONE_LOCATION_KEY, supplierSettings.getTimeZone().getTimeZoneDescription());
				}

			}
			List<FinanceCompany> financeCompanies = financeCompanyService.searchFinanceCompany(FinanceCompanyStatus.ACTIVE.toString(), "Newest", null);
			model.addAttribute("financeCompanies", financeCompanies);

			model.addAttribute("supplierSettings", supplierSettings);
			redir.addFlashAttribute("success", messageSource.getMessage("owner.success.settings.updated", new Object[] {}, Global.LOCALE));

			// return new ModelAndView("supplierSettings", "supplierSettings", supplierSettings);
		} catch (Exception e) {
			LOG.error("Error Updating Supplier Settings : " + e.getMessage(), e);
			// model.addAttribute("error", "Error Updating Supplier Settings : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("error.while.updating.supplierplan", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return new ModelAndView("redirect:/supplier/supplierSettings");
	}

	@RequestMapping(value = "/closeAccount")
	public ModelAndView closeAccount(Model model, RedirectAttributes redir) {

		LOG.info("supplier close Account Called");
		// model.addAttribute("timeZone", timeZoneService.findAllActiveTimeZone());
		try {
			String msg = "User requested to Export Data & Close Account";
			SupplierSettings supplierSettings = supplierSettingsService.getSupplierSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (supplierSettings != null) {

				User owner = userService.getAdminUser();
				List<User> users = userService.getAllAdminUsersForSupplier(SecurityLibrary.getLoggedInUserTenantId());
				if (supplierSettings.getIsClose() == Boolean.FALSE) {
					LOG.info("supplier close request");
					supplierSettings.setIsClose(Boolean.TRUE);
					supplierSettings.setCloseRequestDate(new Date());

					for (User user : users) {
						LOG.info("====================supplier close=======================================" + user.getCommunicationEmail());
						sendNotificationMailToAdmin(user, SecurityLibrary.getLoggedInUser());
					}
					sendNotificationMailToOwner(owner, SecurityLibrary.getLoggedInUser());
					redir.addFlashAttribute("success", messageSource.getMessage("colse.account.request.placed", new Object[] {}, Global.LOCALE));
				} else {
					LOG.info("Supplier cancal request");
					msg = "User requested to Cancel Close Account";
					supplierSettings.setIsClose(Boolean.FALSE);
					supplierSettings.setCancalRequestDate(new Date());
					supplierSettings.setRequestedBy(SecurityLibrary.getLoggedInUser());
					for (User user : users) {
						LOG.info("=====================supplier close======================================" + user.getCommunicationEmail());
						cancelRequestNotificationMailToAdmin(user, SecurityLibrary.getLoggedInUser());
					}

					cancelRequestNotificationMailToOwner(owner, SecurityLibrary.getLoggedInUser());
					redir.addFlashAttribute("success", messageSource.getMessage("colse.account.request.canceled", new Object[] {}, Global.LOCALE));
				}
				supplierSettingsService.updateSupplierSettingsWithAudit(supplierSettings, SecurityLibrary.getLoggedInUser(), msg);

				// return new ModelAndView("supplierSettings", "supplierSettings", supplierSettings);
			} else {
				LOG.error("Error fetching supplier settings");
			}

		} catch (Exception e) {
			LOG.error("Error fetching Supplier Settings : " + e.getMessage(), e);
		}
		return new ModelAndView("redirect:/supplier/supplierSettings");

	}

	private void cancelRequestNotificationMailToOwner(User mailTo, User user) {
		LOG.info("Sending acount closing cancel request email to owner (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

		String subject = "Request to Close Account by Supplier Cancellation";
		String url = APP_URL;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("message", "Request for closing account is canceled by " + user.getSupplier().getCompanyName());
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0) {
			sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.CLOSE_ACCOUNT_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = "Request for closing account cancelled";
		sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.GENERAL);

	}

	private void sendNotificationMailToOwner(User mailTo, User user) {
		LOG.info("Sending acount closing cancel request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

		String subject = "Request to Close Account by Supplier";
		String url = APP_URL;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("message", "Request for closing account is placed by " + user.getSupplier().getCompanyName());
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0) {
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
		String url = APP_URL + "/supplier/supplierSettings";
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("message", "Request by " + user.getName() + " for closing account is successfully placed.");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if ((StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0) && (mailTo.getEmailNotifications())) {
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
		String url = APP_URL + "/supplier/supplierSettings";
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("message", "Request for closing account is successfully cancelled by " + user.getName());
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(java.util.TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if ((StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0) && (mailTo.getEmailNotifications())) {
			sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.CLOSE_ACCOUNT_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = "Request for closing account cancelled";
		sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.GENERAL);

	}

	private String getTimeZoneBySupplierSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = supplierSettingsService.getSupplierTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching supplier time zone :" + e.getMessage(), e);
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

		LOG.info("supplier export Account Called");
		// model.addAttribute("timeZone", timeZoneService.findAllActiveTimeZone());
		try {
			SupplierSettings supplierSettings = supplierSettingsService.getSupplierSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (supplierSettings != null) {

				// User owner = userService.getAdminUser();
				// ist<User> users = userService.getAllAdminUsersForSupplier(SecurityLibrary.getLoggedInUserTenantId());
				LOG.info("supplier export request");
				supplierSettings.setIsBackup(Boolean.TRUE);
				supplierSettings.setIsExport(Boolean.FALSE);
				supplierSettings.setExportURL(null);
				/*
				 * supplierSettings.setIsClose(Boolean.TRUE); supplierSettings.setCloseRequestDate(new Date()); for
				 * (User user : users) { sendNotificationMailToAdmin(user, SecurityLibrary.getLoggedInUser()); }
				 * sendNotificationMailToOwner(owner, SecurityLibrary.getLoggedInUser());
				 */ redir.addFlashAttribute("success", messageSource.getMessage("export.account.request.placed", new Object[] {}, Global.LOCALE));
				/*
				 * } else { LOG.info("Supplier cancal request"); supplierSettings.setIsClose(Boolean.FALSE);
				 * supplierSettings.setCancalRequestDate(new Date());
				 * supplierSettings.setRequestedBy(SecurityLibrary.getLoggedInUser()); for (User user : users) {
				 * cancelRequestNotificationMailToAdmin(user, SecurityLibrary.getLoggedInUser()); }
				 * cancelRequestNotificationMailToOwner(owner, SecurityLibrary.getLoggedInUser());ss
				 * redir.addFlashAttribute("success", messageSource.getMessage("colse.account.request.canceled", new
				 * Object[] {}, Global.LOCALE));
				 */

				supplierSettingsService.updateSupplierSettingsWithAudit(supplierSettings, SecurityLibrary.getLoggedInUser(), "User requested to Export Data");
				;

				// return new ModelAndView("supplierSettings", "supplierSettings", supplierSettings);
			} else {
				LOG.error("Error fetching supplier settings");
			}

		} catch (Exception e) {
			LOG.info("error while close Account " + e.getMessage(), e);
			LOG.error("Error fetching Supplier Settings : " + e.getMessage(), e);
		}
		return new ModelAndView("redirect:/supplier/supplierSettings");

	}

	@RequestMapping(path = "/saveBuyerForSharing", method = RequestMethod.POST)
	public String saveSharingBuyer(@ModelAttribute(name = "poSharingBuyer") PoSharingBuyer poSharingBuyer, Model model, RedirectAttributes redir) {
		try {

			if (poSharingBuyer.getBuyer() == null) {
				redir.addFlashAttribute("error", messageSource.getMessage("please.select.buyer", new Object[] {}, Global.LOCALE));
				return "redirect:supplierSettings";
			}
			if (poSharingBuyer.getFinanceCompany() == null) {
				redir.addFlashAttribute("error", messageSource.getMessage("please.select.finance.company", new Object[] {}, Global.LOCALE));
				return "redirect:supplierSettings";
			}

			poSharingBuyer.setCreatedBy(SecurityLibrary.getLoggedInUser());
			poSharingBuyer.setCreatedDate(new Date());
			poSharingBuyer.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());

			// LOG.info("saving........." + poSharingBuyer.getBuyer().getCompanyName());
			supplierSettingsService.saveSharingBuyer(poSharingBuyer);

			shareAllPotoFinaceCompanyForBuyer(SecurityLibrary.getLoggedInUser().getSupplier(), poSharingBuyer.getFinanceCompany(), poSharingBuyer.getBuyer());
			SupplierSettings dbSupplierSettings = supplierSettingsService.getSupplierSettingsByTenantIdWithFinance(SecurityLibrary.getLoggedInUserTenantId());
			if (dbSupplierSettings == null) {
				dbSupplierSettings = new SupplierSettings();
				dbSupplierSettings.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			}
			model.addAttribute("poSharingBuyer", new PoSharingBuyer());
			dbSupplierSettings.setModifiedDate(new Date());
			dbSupplierSettings.setModifiedBy(SecurityLibrary.getLoggedInUser());
			dbSupplierSettings.setFinanceCompany(null);
			dbSupplierSettings.setPoShare(PoShare.BUYER);
			supplierSettingsService.updateSettings(dbSupplierSettings);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.buyer.added.polist", new Object[] {}, Global.LOCALE));

		} catch (Exception e) {
			LOG.error("error while save buyer po share ....." + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.adding.buyer", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:supplierSettings";
	}

	@RequestMapping(path = "/deleteBuyerForSharing", method = RequestMethod.GET)
	public String deleteSharingBuyer(@ModelAttribute(name = "id") String id, Model model, RedirectAttributes redir) {
		try {
			PoSharingBuyer poSharingBuyer = new PoSharingBuyer();
			poSharingBuyer.setId(id);
			supplierSettingsService.deleteSharingBuyer(poSharingBuyer);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.buyer.remove", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.removing", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:supplierSettings";
	}

	protected List<PoSharingBuyer> setNullPoshareBuyerObject(List<PoSharingBuyer> eventSupplierList) {
		List<PoSharingBuyer> returnList = null;
		if (CollectionUtil.isNotEmpty(eventSupplierList)) {
			returnList = new ArrayList<PoSharingBuyer>();
			for (PoSharingBuyer poSharingBuyer : eventSupplierList) {
				returnList.add(poSharingBuyer.createShallowCopy());
			}
		}
		return returnList;
	}

	@RequestMapping(path = "/supplierAllPoSettings", method = RequestMethod.POST)
	public String supplierAllPoSettings(@ModelAttribute(name = "dbSupplierSettings") SupplierSettings supplierSettings, Model model, RedirectAttributes redir) {
		try {

			SupplierSettings dbSupplierSettings = supplierSettingsService.getSupplierSettingsByTenantIdWithFinance(SecurityLibrary.getLoggedInUserTenantId());
			if (dbSupplierSettings == null) {
				dbSupplierSettings = new SupplierSettings();
				dbSupplierSettings.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			}
			model.addAttribute("poSharingBuyer", new PoSharingBuyer());
			dbSupplierSettings.setModifiedDate(new Date());
			dbSupplierSettings.setModifiedBy(SecurityLibrary.getLoggedInUser());
			dbSupplierSettings.setFinanceCompany(supplierSettings.getFinanceCompany());
			dbSupplierSettings.setPoShare(PoShare.ALL);
			shareAllPotoFinaceCompany(SecurityLibrary.getLoggedInUser().getSupplier(), supplierSettings.getFinanceCompany());
			supplierSettingsService.updateSettings(dbSupplierSettings);
			poSharingBuyerService.clearBuyerSetting(SecurityLibrary.getLoggedInUser().getSupplier().getId());
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.all.po.updated", new Object[] {}, Global.LOCALE));

		} catch (Exception e) {
			LOG.error("erroe wile po setting update for all " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.po.setting", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:supplierSettings";
	}

	private void shareAllPotoFinaceCompany(Supplier supplier, FinanceCompany financeCompany) {

		if (supplier != null && financeCompany != null)
			poSharingBuyerService.shareAllPotoFinaceCompany(supplier, financeCompany, null, SecurityLibrary.getLoggedInUser());
		else
			LOG.info("someone is null");
	}

	private void shareAllPotoFinaceCompanyForBuyer(Supplier supplier, FinanceCompany financeCompany, Buyer buyer) {

		if (supplier != null && financeCompany != null && buyer != null) {
			poSharingBuyerService.shareAllPotoFinaceCompany(supplier, financeCompany, buyer, SecurityLibrary.getLoggedInUser());
		} else {
			LOG.info("someone is null");
		}

	}

	@RequestMapping(path = "/buyerPoShare", method = RequestMethod.GET)
	public String buyerPoShare(Model model, RedirectAttributes redir) {
		try {

			SupplierSettings dbSupplierSettings = supplierSettingsService.getSupplierSettingsByTenantIdWithFinance(SecurityLibrary.getLoggedInUserTenantId());
			if (dbSupplierSettings == null) {
				dbSupplierSettings = new SupplierSettings();
				dbSupplierSettings.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			}

			model.addAttribute("poSharingBuyer", new PoSharingBuyer());
			dbSupplierSettings.setModifiedDate(new Date());
			dbSupplierSettings.setModifiedBy(SecurityLibrary.getLoggedInUser());
			dbSupplierSettings.setFinanceCompany(null);
			dbSupplierSettings.setPoShare(PoShare.BUYER);
			supplierSettingsService.updateSettings(dbSupplierSettings);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.po.buyersetting.update", new Object[] {}, Global.LOCALE));

		} catch (Exception e) {
			LOG.error("erroe wile po setting update for all " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.po.setting", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:supplierSettings";
	}

	@RequestMapping(path = "/disablePoShare", method = RequestMethod.POST)
	public ResponseEntity<String> disablePoShare(RedirectAttributes redir) {
		HttpHeaders headers = new HttpHeaders();
		try {

			SupplierSettings dbSupplierSettings = supplierSettingsService.getSupplierSettingsByTenantIdWithFinance(SecurityLibrary.getLoggedInUserTenantId());
			if (dbSupplierSettings == null) {
				dbSupplierSettings = new SupplierSettings();
				dbSupplierSettings.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			}
			dbSupplierSettings.setModifiedDate(new Date());
			dbSupplierSettings.setFinanceCompany(null);
			dbSupplierSettings.setModifiedBy(SecurityLibrary.getLoggedInUser());
			dbSupplierSettings.setPoShare(PoShare.NONE);
			poSharingBuyerService.clearBuyerSetting(SecurityLibrary.getLoggedInUser().getSupplier().getId());
			supplierSettingsService.updateSettings(dbSupplierSettings);
			headers.add("success", "Individual PO share setting update successfuly");
		} catch (Exception e) {
			HttpHeaders header = new HttpHeaders();
			LOG.error("erroe wile po setting update for all " + e.getMessage(), e);
			headers.add("error", "error while PO share setting update:" + e.getMessage());
			return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>("{\"msg\":\"All is good\"}", headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/buyerDetailsData", method = RequestMethod.GET)
	public ResponseEntity<TableData<PoShareBuyerPojo>> buyerDetailsData(TableDataInput input) throws JsonProcessingException {
		try {

			List<PoShareBuyerPojo> poSharingBuyers = supplierSettingsService.getPoSharingBuyers(SecurityLibrary.getLoggedInUserTenantId());
			TableData<PoShareBuyerPojo> data = new TableData<PoShareBuyerPojo>(poSharingBuyers);
			data.setDraw(input.getDraw());
			return new ResponseEntity<TableData<PoShareBuyerPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching SupplierNoteDocument list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Sharing buyer list : " + e.getMessage());
			return new ResponseEntity<TableData<PoShareBuyerPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}