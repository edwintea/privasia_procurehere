/**
 * 
 */
package com.privasia.procurehere.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Nitin Otageri
 */
@Controller
@RequestMapping("/")
public class ErrorController {

	@RequestMapping(value = "/404_error", method = { RequestMethod.GET, RequestMethod.POST })
	public String get404Error(Model model) {
		return "404_error";
	}

	@RequestMapping(value = "/500_error", method = { RequestMethod.GET, RequestMethod.POST })
	public String get500Error(Model model) {
		return "500_error";
	}

	@RequestMapping(value = "/403_error", method = { RequestMethod.GET, RequestMethod.POST })
	public String get403Error(Model model) {
		return "403_error";
	}

	@RequestMapping(value = "/400_error", method = { RequestMethod.GET, RequestMethod.POST })
	public String get400Error(Model model) {
		return "400_error";
	}
	
	@RequestMapping(value = "/invalid_event", method = { RequestMethod.GET, RequestMethod.POST })
	public String getInvalidEvent(Model model) {
		return "invalid_event";
	}
}
