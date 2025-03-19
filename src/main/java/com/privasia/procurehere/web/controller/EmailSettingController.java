package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.EmailSettings;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.EmailSettingsService;

/**
 * @author Javed Ahmed
 */

@Controller
@RequestMapping(path = "/admin")
public class EmailSettingController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	EmailSettingsService emailSettingsService;

	@Resource
	MessageSource messageSource;

	@RequestMapping(path = "/emailSettings", method = RequestMethod.GET)
	public ModelAndView emailSettings(@ModelAttribute EmailSettings emailSettings, Model model) {
		LOG.info("Creating Email " + emailSettings);
		EmailSettings emailObject = emailSettingsService.loadEmailSettings();
		LOG.info("Creating Email 2 " + emailObject);
		return new ModelAndView("emailSettings", "emailObject", emailObject);
	}

	@RequestMapping(path = "/saveEmail", method = RequestMethod.POST)
	public ModelAndView saveEmail(@Valid @ModelAttribute("emailObject") EmailSettings emailSettings, BindingResult result, Model model, RedirectAttributes redir) {
		List<String> errMessages = new ArrayList<String>();
		LOG.info("save of email called  :");
		if (result.hasErrors()) {
			for (ObjectError err : result.getAllErrors()) {
				errMessages.add(err.getDefaultMessage());

			}
			LOG.info("ear01 of email  :");
			model.addAttribute("btnValue", "Create");
			model.addAttribute("errors", errMessages);
			return new ModelAndView("emailSettings", "emailObject", new EmailSettings());
		} else {
			String str = emailSettings.getSupplierSignupNotificationEmailAccount();
			String[] freeze;
			String delimiter = ",|;|\\s+";
			freeze = str.split(delimiter);
			for (int i = 0; i < freeze.length; i++) {
				LOG.info("inside for loop");
				LOG.info(freeze[i]);
				boolean exp = StringUtils.isValidAddress(freeze[i]);
				LOG.info("Inside for method n exp value:" + exp);
				if (exp == false) {
					redir.addFlashAttribute("exp", messageSource.getMessage("email.update.error", new Object[] {}, Global.LOCALE));
					return new ModelAndView("redirect:emailSettings");
				}
			}
		}
		try {
			
			if (emailSettings.getId() != null) {
				LOG.info("updating Email " + emailSettings.getId());
				emailSettingsService.updateEmailSettings(emailSettings, SecurityLibrary.getLoggedInUser());
			}

			LOG.info("updated Email in database  :" + emailSettingsService.loadEmailSettings().getSupplierSignupNotificationEmailAccount());

		} catch (Exception e) {
			LOG.error("Error While saving the Email" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("email.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("emailSettings", "emailObject", new EmailSettings());
		}
		redir.addFlashAttribute("success", messageSource.getMessage("email.update.success", new Object[] {}, Global.LOCALE));
		return new ModelAndView("redirect:emailSettings");

	}

}
