package com.privasia.procurehere.web.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.TransferOwnershipService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.UserEditor;

/**
 * @author Priyanka Singh
 */
@Controller
@RequestMapping("/buyer")
public class TransferOwnershipController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	UserService userService;

	@Autowired
	TransferOwnershipService transferOwnershipService;

	@Resource
	MessageSource messageSource;

	@Autowired
	UserEditor userEditor;

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		binder.registerCustomEditor(User.class, userEditor);
	}

	@RequestMapping(path = "/transferOwnership", method = RequestMethod.GET)
	public ModelAndView transferOwnership(Model model) throws JsonProcessingException {
		LOG.info("transferOwnership create Called");
		List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("userList", userList);
		return new ModelAndView("transferOwnership");
	}

	@RequestMapping(path = "/saveTransferOwnership", method = RequestMethod.POST)
	public String saveTransferOwnership(@RequestParam(name = "fromUser") String fromUserId, @RequestParam(name = "toUser") String toUserId, Model model, RedirectAttributes redir, HttpSession session) {
		User user = userService.findUserById(fromUserId);
		String sourceUsername = user.getName();
		user = userService.findUserById(toUserId);
		String targetUsername = user.getName();
		try {
			transferOwnershipService.saveTransferOwnership(fromUserId, toUserId, session, SecurityLibrary.getLoggedInUser().getId());
			LOG.info("saveTransferOwnership  Called");
			redir.addFlashAttribute("success", messageSource.getMessage("event.audit.transfer", new Object[] {sourceUsername, targetUsername}, Global.LOCALE));
			return "redirect:transferOwnership";
		} catch (Exception e) {
			LOG.error("Error in saving TransferOwnership " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("event.audit.transfer.error", new Object[] {sourceUsername, targetUsername}, Global.LOCALE));
			return "transferOwnership";
		}
	}
}
