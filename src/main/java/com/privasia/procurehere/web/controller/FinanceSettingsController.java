package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.FinanceCompanySettings;
import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.FinanceSettingsService;
import com.privasia.procurehere.service.TimeZoneService;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.TimeZoneEditor;

@Controller
@RequestMapping("/finance")
public class FinanceSettingsController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	FinanceSettingsService financeSettingsService;

	@Autowired
	TimeZoneService timeZoneService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	TimeZoneEditor timeZoneEditors;

	@Resource
	MessageSource messageSource;

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Currency.class, "currency", currencyEditor);
		binder.registerCustomEditor(TimeZone.class, "timeZone", timeZoneEditors);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@RequestMapping(path = "/financeSettings", method = RequestMethod.GET)
	public ModelAndView createFinanceSettings(Model model) throws JsonProcessingException {
		FinanceCompanySettings companySettings = new FinanceCompanySettings();
		LOG.info("finance Settings create Called");
		model.addAttribute("timeZone", timeZoneService.findAllActiveTimeZone());
		LOG.info("financ Called---------------------");
		try {
			LOG.info("financ Called---------------------");
			companySettings = financeSettingsService.getFinanceSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("financ Called--------------3-------");
			if (companySettings != null)
				return new ModelAndView("financeSettings", "financeSettings", companySettings);

		} catch (Exception e) {

			LOG.error("Error in saving financeSettings " + e.getMessage(), e);
		}
		return new ModelAndView("financeSettings", "financeSettings", new FinanceCompanySettings());
	}

	@RequestMapping(path = "/financeSettings", method = RequestMethod.POST)
	public ModelAndView saveFinanceSettings(@Valid @ModelAttribute("financeSettings") FinanceCompanySettings financeCompanySettings, BindingResult result, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("Finance Settings Save Called : ");
		List<String> errMessages = new ArrayList<String>();
		try {

			model.addAttribute("timeZone", timeZoneService.findAllActiveTimeZone());
			if (result.hasErrors()) {
				for (ObjectError oe : result.getAllErrors()) {
					errMessages.add(oe.getDefaultMessage());
				}
				model.addAttribute("errors", errMessages);
				return new ModelAndView("financeSettings");
			} else {
				LOG.info("Do validate called FinanceSetting Called");
				if (StringUtils.checkString(financeCompanySettings.getId()).length() == 0) {

					LOG.info("save fu nction called FinanceSetting Called");
					financeCompanySettings.setCreatedBy(SecurityLibrary.getLoggedInUser());
					financeCompanySettings.setCreatedDate(new Date());
					financeCompanySettings.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					financeSettingsService.saveFinanceSettings(financeCompanySettings, SecurityLibrary.getLoggedInUser());
					redir.addFlashAttribute("success", messageSource.getMessage("financeSettings.create.success", new Object[] {}, Global.LOCALE));
					LOG.info("Create financeSettings Called : " + SecurityLibrary.getLoggedInUserLoginId() + " for finance : " + SecurityLibrary.getLoggedInUserTenantId());
					// Update the timezone settings in the current session
					if (financeCompanySettings.getTimeZone() != null) {
						session.setAttribute(Global.SESSION_TIME_ZONE_KEY, financeCompanySettings.getTimeZone().getTimeZone());
						session.setAttribute(Global.SESSION_TIME_ZONE_LOCATION_KEY, financeCompanySettings.getTimeZone().getTimeZoneDescription());
					}
					return new ModelAndView("redirect:financeSettings");
				} else {
					LOG.info("UPDATE financeSettings Called");
					FinanceCompanySettings persistObj = financeSettingsService.getFinanceSettingsById(financeCompanySettings.getId());
					persistObj.setTimeZone(financeCompanySettings.getTimeZone());
					persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
					persistObj.setModifiedDate(new Date());
					financeSettingsService.updateFinanceSettings(persistObj, SecurityLibrary.getLoggedInUser());
					redir.addFlashAttribute("success", messageSource.getMessage("financeSettings.update.success", new Object[] {}, Global.LOCALE));
					model.addAttribute("btnValue", "Update");
					LOG.info("update financeSettings Called : " + SecurityLibrary.getLoggedInUserLoginId() + " for finance : " + SecurityLibrary.getLoggedInUserTenantId());

					// Update the timezone settings in the current session
					if (financeCompanySettings.getTimeZone() != null) {
						session.setAttribute(Global.SESSION_TIME_ZONE_KEY, financeCompanySettings.getTimeZone().getTimeZone());
						session.setAttribute(Global.SESSION_TIME_ZONE_LOCATION_KEY, financeCompanySettings.getTimeZone().getTimeZoneDescription());
					}

					return new ModelAndView("redirect:financeSettings");
				}
			}
		} catch (Exception e) {
			LOG.error("Error in saving financeSettings " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("financeSettings.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("financeSettings");
		}
	}

}
