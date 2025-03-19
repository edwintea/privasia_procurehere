/**
 * 
 */
package com.privasia.procurehere.web.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author nitin
 *
 */
@Controller
public class LoginController {

	private static final Logger LOG = LogManager.getLogger(LoginController.class);
	
	@RequestMapping(value = "/login", method = { RequestMethod.GET})
	public ModelAndView login(@RequestParam(required = false) String error, @RequestParam(required = false) String logout) {

		LOG.info("Login url called...." + logout + " - " + error);
		ModelAndView model = new ModelAndView();

		if (error != null) {
			LOG.info("Login Error >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			model.addObject("message", "Invalid Credentials.");
			model.setViewName("login");
		}

		if (logout != null) {
			LOG.info("LOGOUT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			model.addObject("message", "You have been successfully logged out.");
			model.setViewName("login");
		}

		return model;
	}

}
