package com.privasia.procurehere.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.TimeZoneService;
import com.privasia.procurehere.web.editors.TimeZoneEditor;

@Controller
@RequestMapping(path = "/owner")
public class OwnerSettingsController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);
	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	TimeZoneService timeZoneService;

	@Autowired
	TimeZoneEditor timeZoneEditor;
	
	@Resource
	MessageSource messageSource;

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(TimeZone.class, timeZoneEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
//		java.util.TimeZone timeZone = java.util.TimeZone.getDefault();
//		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
//		if (strTimeZone != null) {
//			timeZone = java.util.TimeZone.getTimeZone(strTimeZone);
//		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//		format.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "supplierChargeStartDate", new CustomDateEditor(format, true));
	}

	@RequestMapping(path = "/ownerSettings", method = RequestMethod.GET)
	public ModelAndView createOwnerSettings(Model model) {
		LOG.info("owner Settings create Called");
		model.addAttribute("timeZone", timeZoneService.findAllActiveTimeZone());
		try {
			OwnerSettings ownerSettings = ownerSettingsService.getOwnerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (ownerSettings == null) {
				ownerSettings = new OwnerSettings();
				ownerSettings.setOwner(SecurityLibrary.getLoggedInUser().getOwner());
			}
			return new ModelAndView("ownerSettings", "ownerSettings", ownerSettings);
		} catch (Exception e) {
			LOG.error("Error fetching Owner Settings : " + e.getMessage(), e);
		}
		return new ModelAndView("ownerSettings");
	}

	@RequestMapping(path = "/ownerSettings", method = RequestMethod.POST)
	public ModelAndView createOwnerSettings(@Valid @ModelAttribute(name = "ownerSettings") OwnerSettings ownerSettings, BindingResult result, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("owner Settings create Called");
		model.addAttribute("timeZone", timeZoneService.findAllActiveTimeZone());
		List<String> errMessages = new ArrayList<String>();
		OwnerSettings dbOwnerSettings = null;
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					LOG.info("errorrrrrrr....." + err.getDefaultMessage());
					errMessages.add(err.getDefaultMessage());
				}
				model.addAttribute("error", errMessages);
			} else {
				dbOwnerSettings = ownerSettingsService.getOwnerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (dbOwnerSettings == null) {
					dbOwnerSettings = new OwnerSettings();
					dbOwnerSettings.setOwner(SecurityLibrary.getLoggedInUser().getOwner());
				}
				dbOwnerSettings.setTimeZone(ownerSettings.getTimeZone());
				dbOwnerSettings.setModifiedDate(new Date());
				dbOwnerSettings.setModifiedBy(SecurityLibrary.getLoggedInUser());
				dbOwnerSettings.setSupplierSignupNotificationEmailAccount(ownerSettings.getSupplierSignupNotificationEmailAccount());
				dbOwnerSettings.setFileSizeLimit(ownerSettings.getFileSizeLimit());
				dbOwnerSettings.setFileTypes(ownerSettings.getFileTypes());
				dbOwnerSettings.setSupplierChargeStartDate(ownerSettings.getSupplierChargeStartDate());
				dbOwnerSettings.setBuyerSubsExpiryReminder(ownerSettings.getBuyerSubsExpiryReminder());
				dbOwnerSettings.setSupplierSubsExpiryReminder(ownerSettings.getSupplierSubsExpiryReminder());
				dbOwnerSettings.setBuyerSubscriptionNotificationEmail(ownerSettings.getBuyerSubscriptionNotificationEmail());
				ownerSettingsService.updateSettings(dbOwnerSettings);
				dbOwnerSettings = ownerSettingsService.getOwnerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());

				if (dbOwnerSettings.getTimeZone() != null) {
					session.setAttribute(Global.SESSION_TIME_ZONE_KEY, dbOwnerSettings.getTimeZone().getTimeZone());
					session.setAttribute(Global.SESSION_TIME_ZONE_LOCATION_KEY, dbOwnerSettings.getTimeZone().getTimeZoneDescription());
				}
				LOG.info("owner Settings update Called");
			}
			model.addAttribute("success", messageSource.getMessage("owner.success.settings.updated", new Object[] {}, Global.LOCALE));
			// redir.addFlashAttribute("success", "Settings updated successfully");
			return new ModelAndView("ownerSettings", "ownerSettings", dbOwnerSettings);
		} catch (Exception e) {
			LOG.error("Error Updating Owner Settings : " + e.getMessage(), e);
//			model.addAttribute("error", "Error while saving the RfxTemplate : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("owner.error.saving.rfxtemplate", new Object[] {e.getMessage()}, Global.LOCALE));
		}
		return new ModelAndView("ownerSettings", "ownerSettings", dbOwnerSettings);
	}

}
