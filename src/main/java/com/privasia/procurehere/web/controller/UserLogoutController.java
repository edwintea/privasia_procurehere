/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.utils.Global;

/**
 * @author parveen
 */
@Controller
// @RequestMapping(path = "/")
public class UserLogoutController implements Serializable {

	private static final long serialVersionUID = 487031691048703172L;
	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@RequestMapping(value = { "/Logout" }, method = RequestMethod.GET)
	public String logoutMethod() {
		LOG.info("**** logout successfully");
		return "logout";
	}
}
