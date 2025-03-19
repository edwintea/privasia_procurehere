package com.privasia.procurehere.web.controller;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAuthorizedException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.*;
import com.privasia.procurehere.core.utils.*;
import com.privasia.procurehere.service.*;
import com.privasia.procurehere.web.editors.*;
import freemarker.template.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.*;

/**
 * @author Parveen
 */
@Controller
@RequestMapping("/buyer")
public class PoController extends PrBaseController {

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	CostCenterEditor costCenterEditor;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	BuyerAddressEditor buyerAddressEditor;

	@Autowired
	UserEditor userEditor;

	@Autowired
	PrApprovalUserEditor prApprovalUserEditor;

	@Autowired
	PrTemplateService prTemplateService;

	@Autowired
	BusinessUnitEditor businessUnitEditor;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	BudgetService budgetService;

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	PaymentTermsService paymentTermesService;

	@Autowired
	PaymentTermesEditor paymentTermesEditor;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	PoAuditService poAuditService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(CostCenter.class, costCenterEditor);
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		binder.registerCustomEditor(BuyerAddress.class, buyerAddressEditor);
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(PrApprovalUser.class, prApprovalUserEditor);
		binder.registerCustomEditor(List.class, "approvalUsers", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					User user = userService.findUserById(id);
					return new PrApprovalUser(user);
				}
				return null;
			}
		});
		binder.registerCustomEditor(PaymentTermes.class, paymentTermesEditor);

	}

	@ModelAttribute("step")
	public String getStep() {
		return "1";
	}

	@ModelAttribute("poStatusList")
	public List<PoStatus> getPoStatusList() {
		List<PoStatus> poStatusList = Arrays.asList(PoStatus.APPROVED, PoStatus.DELIVERED, PoStatus.TRANSFERRED, PoStatus.CANCELLED);
		return poStatusList;
	}

	@RequestMapping(path = "/addTeamMemberToPoList", method = RequestMethod.POST)
	public ResponseEntity<List<EventTeamMember>> addTeamMemberToList(@RequestParam("poId") String poId, @RequestParam("userId") String userId, @RequestParam("memberType") TeamMemberType memberType) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("addTeamMemberToPoList:  " + " poId: " + poId + " userId: " + userId);
		List<EventTeamMember> teamMembers = null;
		try {
			if (userId != null) {
				poService.addTeamMemberToList(poId, userId, memberType);
				teamMembers = poService.getPlainTeamMembersForPo(poId);
				Po po = poService.findPoById(poId);
				User userTo = userService.getUsersById(userId);
				String userToEmail = userTo.getCommunicationEmail();
				String userToName = userTo.getName();

				String userInvitorName = SecurityLibrary.getLoggedInUser().getName();

				try {
					;
					sendAddTeamMemberEmailNotificationEmail(po, userToEmail, userToName, userInvitorName, userTo,memberType);
					PoAudit poAudit = new PoAudit();
					poAudit.setAction(PoAuditType.UPDATE);
					poAudit.setActionBy(SecurityLibrary.getLoggedInUser());
					poAudit.setActionDate(new Date());
					poAudit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					if (po.getSupplier() != null && po.getSupplier().getSupplier() != null) {
						poAudit.setSupplier(po.getSupplier().getSupplier());
					}
					poAudit.setDescription(userToName + " has been added from Associate Owner");
					poAudit.setVisibilityType(PoAuditVisibilityType.BUYER);
					poAudit.setPo(po);
					poAuditService.save(poAudit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE,
							userToName + " has been added from Associate Owner",
							SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(),
							ModuleType.PO);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error("Error while saving po audit for add team member:" + e.getMessage(), e);
				}
			} else {
				headers.add("error", "Please Select TeamMember Users");
				LOG.error("Please Select TeamMember Users");
				return new ResponseEntity<List<EventTeamMember>>(null, headers, HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			LOG.error("Error While adding TeamMember users : " + e.getMessage(), e);
			headers.add("error", "Please Select TeamMember Users");
			return new ResponseEntity<List<EventTeamMember>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOG.info(teamMembers.size() + "..................." + teamMembers);
		return new ResponseEntity<List<EventTeamMember>>(teamMembers, headers, HttpStatus.OK);
	}

	private void sendRemoveTeamMemberEmailNotificationEmail(Po po, String userToMail, String userToName, String invitorName,String userinvitorMail,User user, TeamMemberType memberType) {
		// TODO Auto-generated method stub
		try {
			String subject = "You have been Removed as TEAM MEMBER from PR";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", userToName);
			map.put("memberType", memberType.getValue());

			if (memberType == TeamMemberType.Editor)
				map.put("memberMessage", " Allows you to edit the entire draft stage of the PR but not finish the PR");
			else if (memberType == TeamMemberType.Viewer)
				map.put("memberMessage", "Allows you to view entire draft stage of the PR without the ability to edit");
			else
				map.put("memberMessage", "Allows you to perform the same actions as the PR Owner.");
			String eventName = StringUtils.checkString(po.getName()).length() > 0 ? po.getName() : " ";
			map.put("eventName", StringUtils.checkString(po.getName()).length() > 0 ? po.getName() : " ");
			map.put("createdBy", invitorName);
			map.put("createrEmail", userinvitorMail);
			map.put("eventId", po.getId());
			map.put("eventRefNum", StringUtils.checkString(po.getReferenceNumber()).length() > 0 ? po.getReferenceNumber() : " ");

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(SecurityLibrary.getLoggedInUserTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);

			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.REMOVE_TEAM_MEMBER_TEMPLATE_PR), map);
			url = APP_URL + "/buyer/poCreate/" + po.getId();
			notificationService.sendEmail(userToMail, subject, message);

			String notificationMessage = messageSource.getMessage("team.pr.remove", new Object[]{memberType, eventName}, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);

		} catch (Exception e) {
			LOG.error("error in sending team member email " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/removeTeamMemberfromPoList", method = RequestMethod.POST)
	public ResponseEntity<List<User>> removeTeamMemberfromList(@RequestParam(value = "poId") String poId, @RequestParam(value = "userId") String userId, Model model) {
		LOG.info("removeTeamMemberToList:  " + " poId: " + poId + " userId: " + userId);
		HttpHeaders headers = new HttpHeaders();
		LOG.info("userId Call");
		List<User> teamMembers = null;
		List<User> userList = new ArrayList<User>();
		try {
			PoTeamMember poTeamMember = poService.getPoTeamMemberByUserIdAndPoId(poId, userId);

			Po po = poService.findPoById(poId);
			User userTo = userService.getUsersById(userId);
			String userToName = userTo.getName();
			String userToMail = userTo.getCommunicationEmail();

			String userinvitorName = SecurityLibrary.getLoggedInUser().getName();
			String userinvitorMail = SecurityLibrary.getLoggedInUser().getCommunicationEmail();

			sendRemoveTeamMemberEmailNotificationEmail(po, userToMail, userToName, userinvitorName,userinvitorMail,userTo, poTeamMember.getTeamMemberType());

			teamMembers = poService.removeTeamMemberfromList(poId, userId, poTeamMember);

			List<User> activeUserList = userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			for (User user : activeUserList) {
				try {
					userList.add((User) user.clone());
				} catch (Exception e) {
					LOG.error("Error while cloning user List: " + e.getMessage(), e);
					headers.add("error", "Error While removing Team Member users : " + e.getMessage());
					return new ResponseEntity<List<User>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			// Remove all users that are already added as editors.
			userList.removeAll(teamMembers);

			try {
				PoAudit poAudit = new PoAudit();
				poAudit.setAction(PoAuditType.UPDATE);
				poAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				poAudit.setActionDate(new Date());
				poAudit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				if (po.getSupplier() != null && po.getSupplier().getSupplier() != null) {
					poAudit.setSupplier(po.getSupplier().getSupplier());
				}
				poAudit.setDescription(userToName + " has been remove as Associate Owner");
				poAudit.setVisibilityType(PoAuditVisibilityType.BUYER);
				poAudit.setPo(po);
				poAuditService.save(poAudit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE,
						userToName + " has been remove as Associate Owner",
						SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(),
						ModuleType.PO);
				buyerAuditTrailDao.save(buyerAuditTrail);

			} catch (Exception e) {
				LOG.error("Error while saving po audit for add team member:" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error While removing Team Member users : " + e.getMessage(), e);
			headers.add("error", "Error While removing Team Member users : " + e.getMessage());
			return new ResponseEntity<List<User>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<User>>(userList, headers, HttpStatus.OK);
	}

	private void sendAddTeamMemberEmailNotificationEmail(Po po, String userToMail,String userToName, String userInvitorName, User user,TeamMemberType memberType) {
		// TODO Auto-generated method stub
		try {
			String subject = "You have been Invited as TEAM MEMBER In PO";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("userName", userToName);
			map.put("memberType", memberType.getValue());

			if (memberType == TeamMemberType.Editor)
				map.put("memberMessage", " Allows you to edit the entire draft stage of the PO but not finish the PO");
			else if (memberType == TeamMemberType.Viewer)
				map.put("memberMessage", "Allows you to view entire draft stage of the PO without the ability to edit");
			else
				map.put("memberMessage", "Allows you to perform the same actions as the PO Owner.");
			String eventName = StringUtils.checkString(po.getName()).length() > 0 ? po.getName() : " ";
			map.put("eventName", StringUtils.checkString(po.getName()).length() > 0 ? po.getName() : " ");
			map.put("createdBy", userInvitorName);
			map.put("eventId", po.getId());
			map.put("eventRefNum", StringUtils.checkString(po.getReferenceNumber()).length() > 0 ? po.getReferenceNumber() : " ");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(SecurityLibrary.getLoggedInUserTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);

			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.TEAM_MEMBER_TEMPLATE_PO), map);
			notificationService.sendEmail(userToMail, subject, message);
			url = APP_URL + "/buyer/poCreate/" + po.getId();

			String notificationMessage = messageSource.getMessage("team.pr.remove", new Object[]{memberType, eventName}, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);

		} catch (Exception e) {
			LOG.error("error in sending team member email " + e.getMessage(), e);
		}
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(NotificationType.EVENT_MESSAGE);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
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
}