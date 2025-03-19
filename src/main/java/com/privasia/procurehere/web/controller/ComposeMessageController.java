package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;


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

import com.privasia.procurehere.core.entity.ComposeMessage;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.ComposeMessageService;
import com.privasia.procurehere.service.UserService;

@Controller
@RequestMapping("/admin")
public class ComposeMessageController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	ComposeMessageService composeMessageService;

	@Autowired
	UserService userService;
	
	@Resource
	MessageSource messageSource;

	@RequestMapping(path = "/composeMessage", method = RequestMethod.GET)
	public ModelAndView createComposeMessage(@ModelAttribute ComposeMessage composeMessage, Model model) {
		LOG.info("Create composeMessage called");
		model.addAttribute("composeMessage", new ComposeMessage());
		model.addAttribute("btnValue", "Create");
		List<UserPojo> userList = userService.getAllUserPojo();
		List<String> loginIds = null;
		if(CollectionUtil.isNotEmpty(userList)){
			loginIds = new ArrayList<String>();
			for(UserPojo userPojo : userList){
				loginIds.add(userPojo.getLoginId());
			}
		}
		model.addAttribute("user1",loginIds);
		return new ModelAndView("composeMessage", "composeObj", new ComposeMessage());
	}

	@RequestMapping(path = "/composeMessage1", method = RequestMethod.POST)
	public String saveComposeMessage(@ModelAttribute("composeObj") ComposeMessage composeMessage, Model model, BindingResult result) {
		LOG.info("error For user" +composeMessage);
		LOG.info("user emails cmtrollrr" );
		try {
			List<String> errMessages = new ArrayList<String>();
			if (result.hasErrors()) {
				LOG.info("error For user1111" +composeMessage.getUserList());
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				LOG.info("error 1");
				model.addAttribute("btnValue", "Create");
				model.addAttribute("errors", errMessages);
				return "composeMessage";
			} else {
				LOG.info("save For user" +composeMessage);
				composeMessage.setFromUser(SecurityLibrary.getLoggedInUser());
				composeMessage.setCreatedDate(new Date());
				composeMessageService.saveComposedMessage(composeMessage);
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Country" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("user.save.country", new Object[] {  e.getMessage() }, Global.LOCALE));
			return "composeMessage";
		}
		return "composeMessage";

	}
	
}
