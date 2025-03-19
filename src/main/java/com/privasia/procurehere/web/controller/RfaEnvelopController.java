package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.service.RfaSorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfaEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaUnMaskedUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.EvaluationStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfaEnvelopService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.web.editors.RfaEnvelopeOpenerUserEditor;

@Controller
@RequestMapping("/buyer/RFA")
public class RfaEnvelopController extends EventEnvelopBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RfaEnvelopService envelopService;

	@Autowired
	RfaEventService eventService;

	@Autowired
	RfaBqService rfaBqService;

	@Autowired
	RfaCqService rfaCqService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	RfaEnvelopeOpenerUserEditor envelopeOpenerUserEditor;

	@Autowired
	RfaEnvelopDao rfaEnvelopDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RfaSorService rfaSorService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		super.initBinder(binder);
		binder.registerCustomEditor(RfaEnvelopeOpenerUser.class, envelopeOpenerUserEditor);
	}

	public RfaEnvelopController() {
		super(RfxTypes.RFA);
	}

	@RequestMapping(path = "/envelopList/{eventId}", method = RequestMethod.GET)
	public String envelopList(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		Boolean isPreSet = eventService.isDefaultPreSetEnvlope(eventId);

		List<RfaEnvelop> rfaEnvelopList = envelopService.getAllEnvelopByEventId(eventId, SecurityLibrary.getLoggedInUser());
		LOG.info("Rfa Envelop List Count : " + rfaEnvelopList.size());
		model.addAttribute("envelopList", rfaEnvelopList);
		// model.addAttribute("eventPermissions",
		// eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		EventPermissions eventPermissions = eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
		User loggedInUser = userService.findById(SecurityLibrary.getLoggedInUser().getId());
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			eventPermissions.setApproverUser(true);
		}

		RfaEvent event = eventService.loadRfaEventById(eventId);
		for (RfaEnvelop rfaEnvelop : rfaEnvelopList) {
			EventPermissions envPermission = eventService.getUserPemissionsForEnvelope(SecurityLibrary.getLoggedInUser().getId(), eventId, rfaEnvelop.getId());
			if (envPermission.isEvaluator() || envPermission.isLeadEvaluator()) {
				LOG.info("user is evaluator or lead evaluator");
				rfaEnvelop.setShowOpen(true);
				if (event.getEnableEvaluationDeclaration()) {
					LOG.info("Enable Declaration of evaluation");
					rfaEnvelop.setShowEvaluationDeclaration(envelopService.isAcceptedEvaluationDeclaration(rfaEnvelop.getId(), SecurityLibrary.getLoggedInUser().getId(), eventId));
				}
			}
			if (envPermission.isOpener()) {
				rfaEnvelop.setPermitToOpenClose(true);
			}

		}

		LOG.info("isPreSet " + isPreSet);
		model.addAttribute("preSetEnvlop", isPreSet);

		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("eventId", eventId);
		model.addAttribute("allowToView", event.getViewSupplerName());
		model.addAttribute("closeEnv", event.getCloseEnvelope());
		model.addAttribute("isMaskingEnable", event.getViewSupplerName());
		model.addAttribute("event", event);
		model.addAttribute("rfxType", RfxTypes.RFA);
		model.addAttribute("loggedInUserId", SecurityLibrary.getLoggedInUser().getId());
		if (event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED) {
			return "envelopList";
		} else {
			model.addAttribute("showEnvelopTab", true);
			model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
			return "ongoingEnvelopList";
		}
	}

	@RequestMapping(path = "/showEnvelop", method = RequestMethod.POST)
	public String showCreateEnvolpe(@RequestParam String eventId, Model model, RedirectAttributes attributes) {
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		return "redirect:envelop/" + eventId;
	}

	@RequestMapping(path = "/envelop/{eventId}", method = RequestMethod.GET)
	public ModelAndView createEnvelop(@PathVariable String eventId, @ModelAttribute RfaEnvelop rfaEnvelop, Model model) {
		LOG.info("Create Envelop called");
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		RfaEvent rfaEvent = eventService.loadRfaEventById(eventId);
		// List<User> userList =
		// userService.fetchAllActiveUsersForEnvelopForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);

		model.addAttribute("openers", userList);
		model.addAttribute("evaluationOwner", userList);

		model.addAttribute("event", rfaEvent);
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("btnValue", "Create");
		List<RfaEventBq> bqList = rfaBqService.getRfaEventBqForEventIdForEnvelop(eventId, null);
		model.addAttribute("bqlist", bqList);

		// TODO: after CQ this code need to be alive
		List<RfaCq> cqList = rfaCqService.getRfaCqByEventId(eventId, null);
		model.addAttribute("cqlist", cqList);

		model.addAttribute("sorlist", rfaSorService.getEventSorForEventIdForEnvelop(eventId, null));

		RfaEnvelop envelope = new RfaEnvelop();
		envelope.setEnvelopType(EnvelopType.CLOSED);
		envelope.setOpener(SecurityLibrary.getLoggedInUser());
		envelope.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
		model.addAttribute("envelop", envelope);
		return new ModelAndView("envelop");
	}

	@RequestMapping(path = "/envelop", method = RequestMethod.POST)
	public @ResponseBody ModelAndView saveEnvelop(@ModelAttribute("envelop") RfaEnvelop envelop, BindingResult result, Model model, @RequestParam(value = "bqids[]", required = false) String[] bqids, @RequestParam(value = "sorids[]", required = false) String[] sorids, @RequestParam(value = "cqids[]", required = false) String[] cqids, @RequestParam String eventId, RedirectAttributes redir) {

		List<RfaEventBq> bqList = rfaBqService.getRfaEventBqForEventIdForEnvelop(eventId, null);
		model.addAttribute("bqlist", bqList);
		// load Rft event CQ on error

		// TODO: after CQ this code need to be alive
		List<RfaCq> cqList = rfaCqService.getRfaCqByEventId(eventId, null);
		model.addAttribute("cqlist", cqList);

		model.addAttribute("sorlist", rfaSorService.getEventSorForEventIdForEnvelop(eventId, null));

		// load Rft event on error
		RfaEvent rfaEvent = eventService.loadRfaEventById(eventId);
		model.addAttribute("event", rfaEvent);

		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);

		List<UserPojo> openerUserList = new ArrayList<UserPojo>();
		for (UserPojo user : userList) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(),user.getTenantId(), user.isDeleted());
			openerUserList.add(user);
		}
		model.addAttribute("evaluationOwner", userList);

		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
					LOG.info("ERROR : " + err.getDefaultMessage());
				}
				model.addAttribute("btnValue", "Create");
				model.addAttribute("error", errMessages);
				return new ModelAndView("envelop", "envelop", envelop);

			} else {
				if (envelop.getEnvelopType() == EnvelopType.CLOSED && CollectionUtil.isEmpty(envelop.getOpenerUsers())) {
					model.addAttribute("error", "Please select Envelope Opener");
					model.addAttribute("error", messageSource.getMessage("error.while.select.opener", new Object[] {}, Global.LOCALE));
					return new ModelAndView("envelop", "envelop", envelop);
				}
				List<RfaEnvelopeOpenerUser> openerUsers = envelop.getOpenerUsers();
				if (CollectionUtil.isNotEmpty(openerUsers)) {
					for (RfaEnvelopeOpenerUser user : openerUsers) {
						user.setEvent(rfaEvent);
						user.setEnvelope(envelop);
						UserPojo u = new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications());
						if (!openerUserList.contains(u)) {
							openerUserList.add(u);
						}
					}
				}

				model.addAttribute("openers", openerUserList);

				if (envelop.getLeadEvaluater() == null) {
					model.addAttribute("error", messageSource.getMessage("error.while.select.evaluation.owner", new Object[] {}, Global.LOCALE));
					return new ModelAndView("envelop", "envelop", envelop);
				}
				if (doValidate(envelop, eventId)) {
					if (StringUtils.checkString(envelop.getId()).length() == 0) {
						envelop.setRfxEvent(rfaEvent);
						envelop.setEnvelopSequence(envelop.getEnvelopSequence());

						if (envelop.getEnvelopType() == EnvelopType.OPEN) {
							envelop.setOpener(null);
							envelop.setIsOpen(Boolean.TRUE);
							envelop.setOpenerUsers(null);
						}
						envelopService.saveRfaEnvelop(envelop, bqids, cqids, sorids);
						redir.addFlashAttribute("success", messageSource.getMessage("envelop.create.success", new Object[] { envelop.getEnvelopTitle() }, Global.LOCALE));
					} else {
						LOG.info("update Envelope Called");
						RfaEnvelop persistObj = envelopService.getRfaEnvelopById(envelop.getId());
						if (envelop.getLeadEvaluater() != null && envelopService.getCountOfAssignedSupplier(envelop.getLeadEvaluater().getId(), envelop.getId(), eventId)) {
							model.addAttribute("error", messageSource.getMessage("envelope.error.select.leadevaluator", new Object[] {}, Global.LOCALE));
							return editEnvelope(envelop.getId(), eventId, model, redir);
						}
						persistObj.setEnvelopSequence(envelop.getEnvelopSequence());

						persistObj.setPreFix(envelop.getPreFix());
						persistObj.setEnvelopTitle(envelop.getEnvelopTitle());
						persistObj.setDescription(envelop.getDescription());
						persistObj.setEnvelopType(envelop.getEnvelopType());
						persistObj.setLeadEvaluater(envelop.getLeadEvaluater());
						persistObj.setOpenerUsers(envelop.getOpenerUsers());
						// persistObj.setOpener(envelop.getOpener());
						if (envelop.getEnvelopType() == EnvelopType.OPEN) {
							persistObj.setIsOpen(Boolean.TRUE);
							persistObj.setOpener(null);
							persistObj.setOpenerUsers(null);
						} else {
							persistObj.setIsOpen(Boolean.FALSE);
						}
						envelopService.updateRfaEnvelop(persistObj, bqids, cqids, sorids);
						redir.addFlashAttribute("success", messageSource.getMessage("envelop.update.success", new Object[] { envelop.getEnvelopTitle() }, Global.LOCALE));
					}
				} else {
					LOG.info("Validation error ...............");
					model.addAttribute("error", messageSource.getMessage("envelop.duplicate.error", new Object[] { envelop.getEnvelopTitle() }, Global.LOCALE));
					return new ModelAndView("envelop", "envelop", envelop);
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Envelop : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("envelope.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("envelop", "envelop", envelop);
		}
		return new ModelAndView("redirect:envelopList/" + eventId);

	}

	private boolean doValidate(RfaEnvelop rfaEnvelop, String eventId) {
		boolean validate = true;
		if (envelopService.isExists(rfaEnvelop, eventId)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/bqCqList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<String>> bqCqList(@RequestParam("envelopId") String envelopId) {
		RfaEnvelop rfaEnvelop = envelopService.getRfaEnvelopById(envelopId);
		List<RfaCq> rfaCq = envelopService.getCqsByEnvelopIdByOrder(envelopId);
		List<RfaEventSor> rfqSor = envelopService.getSorsByEnvelopIdByOrder(envelopId);
		List<String> returnList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rfaCq)) {
			for (RfaCq cqsOfEnvelop : rfaCq) {
				returnList.add(cqsOfEnvelop.getName() + " [Questionnaire]");
			}
		}
		if (CollectionUtil.isNotEmpty(rfaEnvelop.getBqList())) {
			for (RfaEventBq bqsOfEnvelop : rfaEnvelop.getBqList()) {
				returnList.add(bqsOfEnvelop.getName() + " [Bill of Quantity]");
			}
		}
		// TODO: after CQ this code need to be alive
		/*
		 * List<RfaCq> rfaCqList = rfaEnvelop.getCqList(); for (RfaCq cqsOfEnvelop : rfaCqList) {
		 * returnList.add(cqsOfEnvelop.getName()); }
		 */
		if (CollectionUtil.isNotEmpty(rfqSor)) {
			for (RfaEventSor bqsOfEnvelop : rfqSor) {
				returnList.add(bqsOfEnvelop.getName() + " [Schedule of Rate]");
			}
		}
		return new ResponseEntity<List<String>>(returnList, HttpStatus.OK);
	}

	@RequestMapping(value = "/envelopPrevious", method = RequestMethod.POST)
	public String envelopPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		RfaEvent rfaEvent = eventService.getRfaEventByeventId(eventId);
		if (rfaEvent != null) {
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			if(Boolean.TRUE == rfaEvent.getScheduleOfRate()) {
				return "redirect:createSorList/" + rfaEvent.getId();
			} else if (Boolean.TRUE == rfaEvent.getBillOfQuantity()) {
				return "redirect:createBQList/" + rfaEvent.getId();
			} else if (Boolean.TRUE == rfaEvent.getQuestionnaires()) {
				return "redirect:eventCqList/" + rfaEvent.getId();
			} else if (Boolean.TRUE == rfaEvent.getMeetingReq()) {
				return "redirect:meetingList/" + rfaEvent.getId();
			} else if (rfaEvent.getEventVisibility() != EventVisibilityType.PUBLIC) {
				return "redirect:addSupplier/" + rfaEvent.getId();
			} else if (Boolean.TRUE == rfaEvent.getDocumentReq()) {
				return "redirect:createEventDocuments/" + rfaEvent.getId();
			} else {
				return "redirect:auctionRules/" + rfaEvent.getId();
			}
		} else {
			LOG.error("Event not found redirecting to login ");
			return "/";
		}
	}

	@RequestMapping(path = "/envelopCancel", method = RequestMethod.POST)
	public String envelopCancel(@RequestParam String eventId, Model model) {
		return "redirect:envelopList/" + eventId;
	}

	@RequestMapping(path = "/editEnvelope", method = RequestMethod.POST)
	public ModelAndView editEnvelope(@RequestParam String envelopId, @RequestParam String eventId, Model model, RedirectAttributes attributes) {

		List<RfaEvaluatorUser> assignedEvaluators = null;
		assignedEvaluators = envelopService.findEvaluatorsByEnvelopId(envelopId);

		RfaEnvelop rfaEnvelop = envelopService.getRfaEnvelopById(envelopId);
		LOG.info("Rfa Envelop  For editing : " + rfaEnvelop.getEnvelopTitle());
		model.addAttribute("envelop", rfaEnvelop);
		List<RfaEventBq> assignedBqList = rfaEnvelop.getBqList();
		model.addAttribute("bqOfEnvelope", assignedBqList);
		model.addAttribute("sorOfEnvelope", rfaEnvelop.getSorList());
		List<RfaEventSor> assignedSorList = rfaEnvelop.getSorList();
		// Get envelope permissions
		EventPermissions eventPermissions = eventService.getUserPemissionsForEnvelope(SecurityLibrary.getLoggedInUser().getId(), eventId, envelopId);
		model.addAttribute("eventPermissions", eventPermissions);

		List<RfaCq> assignedCqList = rfaEnvelop.getCqList();
		model.addAttribute("cqOfEnvelope", assignedCqList);

		// List<User> userList =
		// userService.fetchAllActiveUsersForEnvelopForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		// setting opener if selected user is approver or inactive

		List<UserPojo> openerUserList = new ArrayList<UserPojo>();

		for (UserPojo user : userList) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			openerUserList.add(user);
		}

		List<RfaEnvelopeOpenerUser> openerUSerList = rfaEnvelop.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(openerUSerList)) {
			for (RfaEnvelopeOpenerUser user : openerUSerList) {
				UserPojo u = new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications());
				if (!openerUserList.contains(u)) {
					openerUserList.add(u);
				}
			}
		}

		// if (rfaEnvelop.getOpener() != null) {
		// try {
		// openerListSelectedUser.add(new UserPojo(rfaEnvelop.getOpener().getId(), rfaEnvelop.getOpener().getLoginId(),
		// rfaEnvelop.getOpener().getName(), rfaEnvelop.getOpener().getTenantId(), rfaEnvelop.getOpener().isDeleted(),
		// rfaEnvelop.getOpener().getCommunicationEmail()));
		// } catch (Exception e) {
		// LOG.info("Error while cloning the user :" + e.getMessage());
		// }
		// }
		// for (UserPojo user : userList) {
		// boolean isOpener = false;
		// try {
		// if (rfaEnvelop.getOpener() != null && rfaEnvelop.getOpener().getId().equals(user.getId())) {
		// isOpener = true;
		// }
		// if (!isOpener) {
		// openerListSelectedUser.add(user);
		// }
		// } catch (Exception e) {
		// LOG.info("Error while cloning the user List :" + e.getMessage());
		// }
		// }

		// setting owner if selected user is approver or inactive
		List<UserPojo> ownerListSelectedUser = new ArrayList<UserPojo>();
		if (rfaEnvelop.getLeadEvaluater() != null) {
			try {
				ownerListSelectedUser.add(new UserPojo(rfaEnvelop.getLeadEvaluater().getId(), rfaEnvelop.getLeadEvaluater().getLoginId(), rfaEnvelop.getLeadEvaluater().getName(), rfaEnvelop.getLeadEvaluater().getTenantId(), rfaEnvelop.getLeadEvaluater().isDeleted(), rfaEnvelop.getLeadEvaluater().getCommunicationEmail(), rfaEnvelop.getLeadEvaluater().getEmailNotifications()));

			} catch (Exception e) {
				LOG.info("Error while cloning the user :" + e.getMessage());
			}
		}
		for (UserPojo user : userList) {
			boolean isLeadEval = false;
			try {
				if (rfaEnvelop.getLeadEvaluater() != null && rfaEnvelop.getLeadEvaluater().getId().equals(user.getId())) {
					isLeadEval = true;
				}
				if (!isLeadEval) {
					ownerListSelectedUser.add(user);
				}
			} catch (Exception e) {
				LOG.info("Error while cloning the user List :" + e.getMessage());
			}
		}

		model.addAttribute("openers", openerUserList);
		model.addAttribute("assignedEvaluators", assignedEvaluators);
		model.addAttribute("evaluationOwner", ownerListSelectedUser);

		List<UserPojo> evaluators = new ArrayList<UserPojo>();
		// Remove all users that are already added as evaluators.

		for (UserPojo user : userList) {
			boolean found = false;
			for (RfaEvaluatorUser evalUser : assignedEvaluators) {
				if (evalUser.getUser().getId().equals(user.getId())) {
					found = true;
					break;
				}
			}
			if (!found && (rfaEnvelop.getLeadEvaluater() == null || (rfaEnvelop.getLeadEvaluater() != null && !user.getId().equals(rfaEnvelop.getLeadEvaluater().getId())))) {
				evaluators.add(user);
			}
		}
		model.addAttribute("Evaluators", evaluators);

		// TODO: after CQ this code need to be alive
		/*
		 * List<RftCq> assignedCqList = rfaEnvelop.getCqList(); model.addAttribute("cqOfEnvelope", assignedCqList);
		 */
		RfaEvent rfaEvent = eventService.loadRfaEventById(eventId);
		model.addAttribute("event", rfaEvent);
		LOG.info("rft event in edit envelop : " + rfaEvent.getEventName());
		List<String> bqIds = null;
		if (CollectionUtil.isNotEmpty(assignedBqList)) {
			bqIds = new ArrayList<String>();
			for (RfaEventBq bq : assignedBqList) {
				bqIds.add(bq.getId());
			}
		}
		List<String> sorIds = null;
		if (CollectionUtil.isNotEmpty(assignedSorList)) {
			sorIds = new ArrayList<String>();
			for (RfaEventSor bq : assignedSorList) {
				sorIds.add(bq.getId());
			}
		}

		List<RfaEventSor> sorList = rfaSorService.getEventSorForEventIdForEnvelop(eventId, sorIds);
		model.addAttribute("sorlist", sorList);

		List<RfaEventBq> bqList = rfaBqService.getRfaEventBqForEventIdForEnvelop(eventId, bqIds);
		model.addAttribute("bqlist", bqList);

		// TODO: after CQ this code need to be alive
		List<String> cqIds = null;
		if (CollectionUtil.isNotEmpty(assignedCqList)) {
			cqIds = new ArrayList<String>();
			for (RfaCq cq : assignedCqList) {
				cqIds.add(cq.getId());
			}
		}
		List<RfaCq> cqList = rfaCqService.getRfaCqByEventId(eventId, cqIds);
		model.addAttribute("cqlist", cqList);

		return new ModelAndView("envelop", "envelop", rfaEnvelop);
	}

	@RequestMapping(value = "/envelopNext", method = RequestMethod.POST)
	public String bqNext(Model model, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes attributes) {
		RfaEvent rfaEvent = eventService.getRfaEventByeventId(eventId);
		if (rfaEvent != null) {
			if (Boolean.TRUE == rfaEvent.getRfxEnvelopeOpening()) {
				int count = 0;
				int sum = 0;
				int seq = 0;
				List<Integer> list = new ArrayList<Integer>();

				for (RfaEnvelop envlope : rfaEvent.getRfaEnvelop()) {
					if (envlope.getEnvelopType() == EnvelopType.CLOSED && CollectionUtil.isEmpty(envlope.getOpenerUsers())) {
						attributes.addFlashAttribute("error", "Please select Envelope Opener for Envelope : " + envlope.getEnvelopTitle());
						return "redirect:envelopList/" + rfaEvent.getId();
					}
					count++;
					if (envlope.getEnvelopSequence() != null) {
						if (envlope.getEnvelopSequence() <= 0) {
							attributes.addFlashAttribute("error", "Sequence must contain numeric values in ascending order");
							return "redirect:envelopList/" + rfaEvent.getId();
						}

						list.add(envlope.getEnvelopSequence());
						seq += envlope.getEnvelopSequence();
					}
				}
				for (int s = 1; s <= count; s++) {
					sum += s;
				}

				Set<Integer> set = new HashSet<Integer>(list);
				if (set.size() < list.size()) {
					attributes.addFlashAttribute("error", "Duplicate sequence not allowed");
					return "redirect:envelopList/" + rfaEvent.getId();
				}

				if (seq == 0) {
					attributes.addFlashAttribute("error", "Envelope sequence is missing");
					return "redirect:envelopList/" + rfaEvent.getId();
				}

				if (seq != sum) {
					attributes.addFlashAttribute("error", "Sequence must contain numeric values in ascending order");
					return "redirect:envelopList/" + rfaEvent.getId();
				}
			}

			rfaEvent.setEnvelopCompleted(Boolean.TRUE);
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			eventService.updateRfaEvent(rfaEvent);
			return "redirect:eventSummary/" + rfaEvent.getId();
		} else {
			LOG.error("Event not found redirecting to login ");
			return "/";
		}
	}

	@RequestMapping(path = "/deleteEnvelope", method = RequestMethod.POST)
	public String deleteEnvelope(@RequestParam String envelopId, @RequestParam String eventId, Model model, RedirectAttributes attributes) {

		RfaEnvelop rfaEnvelop = envelopService.getRfaEnvelopById(envelopId);
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		try {
			LOG.info("Delete the envelope :   " + rfaEnvelop.getEnvelopTitle());
			envelopService.deleteRfaEnvelop(rfaEnvelop);
			attributes.addFlashAttribute("success", messageSource.getMessage("envelop.deleted.success", new Object[] { rfaEnvelop.getEnvelopTitle() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting country [ " + rfaEnvelop.getEnvelopTitle() + " ]" + e.getMessage(), e);
			attributes.addFlashAttribute("error", messageSource.getMessage("envelop.deleted.error", new Object[] { rfaEnvelop.getEnvelopTitle() }, Global.LOCALE));
		}
		// attributes.addFlashAttribute("eventId", eventId);
		return "redirect:envelopList/" + eventId;
	}

	@RequestMapping(value = "/envelopSaveDraft", method = RequestMethod.POST)
	public String envelopSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId) {
		RfaEvent rfaEvent = eventService.getRfaEventByeventId(eventId);
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rfaEvent.getEventName() != null ? rfaEvent.getEventName() : rfaEvent.getEventId()) }, Global.LOCALE));
		return "redirect:envelopList/" + eventId;

	}

	@RequestMapping(path = "/addEvaluatorToList", method = RequestMethod.POST)
	public ResponseEntity<List<RfaEvaluatorUser>> addEvaluator(@RequestParam("eventId") String eventId, @RequestParam("envelopeId") String envelopeId, @RequestParam("userId") String userId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("addEvaluatorToList----Editor" + " eventId: " + eventId + " envelopeId: " + envelopeId + " userId: " + userId);
		List<RfaEvaluatorUser> evaluators = null;
		try {
			List<RfaEvaluatorUser> assignedEvaluators = envelopService.findEvaluatorsByEnvelopId(envelopeId);
			for (RfaEvaluatorUser rfaEvaluatorUser : assignedEvaluators) {
				if (rfaEvaluatorUser.getUser().getId().equals(userId)) {
					return new ResponseEntity<List<RfaEvaluatorUser>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			if (envelopeId != null && userId != null) {
				LOG.info("envelopeId : " + envelopeId);
				evaluators = envelopService.addEvaluator(eventId, envelopeId, userId);
			} else {
				headers.add("error", "Please Select Evaluator Users");
				LOG.error("Please Select User Evaluator");
				return new ResponseEntity<List<RfaEvaluatorUser>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error While adding evaluator user : " + e.getMessage(), e);
			headers.add("error", "Error While adding evaluator user : " + e.getMessage());
			return new ResponseEntity<List<RfaEvaluatorUser>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		LOG.info("RfaController addEvaluatorToList editors: " + evaluators);
		return new ResponseEntity<List<RfaEvaluatorUser>>(evaluators, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/removeEvaluatorToList", method = RequestMethod.POST)
	public ResponseEntity<List<UserPojo>> removeEvaluator(@RequestParam("eventId") String eventId, @RequestParam("envelopeId") String envelopeId, @RequestParam("userId") String userId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("removeEvaluator----Evaluator" + " eventId: " + eventId + " envelopeId: " + envelopeId + " userId: " + userId);
		List<User> evaluators = null;
		RfaEnvelop rfaEnvelop = envelopService.getRfaEnvelopById(envelopeId);
		try {
			evaluators = envelopService.removeEvaluator(eventId, envelopeId, userId);
		} catch (Exception e) {
			LOG.error("Error While removing evaluator user : " + e.getMessage(), e);
			headers.add("error", "Error While removing evaluator user : " + e.getMessage());
			return new ResponseEntity<List<UserPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<UserPojo> evList = new ArrayList<UserPojo>();
		// List<User> evaluatorList =
		// userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> evaluatorList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		for (UserPojo user : evaluatorList) {
			if (!user.getId().equals(rfaEnvelop.getLeadEvaluater().getId())) {
				boolean found = false;
				// dont add event evalustors into list
				for (User euser : evaluators) {
					if (euser.getId().equals(user.getId())) {
						found = true;
						break;
					}
				}
				try {
					if (!found) {
						evList.add(user);
					}
				} catch (Exception e) {
					LOG.error("Error during clone : " + e.getMessage(), e);
				}
			}
		}

		return new ResponseEntity<List<UserPojo>>(evList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/openEnvelop", method = RequestMethod.POST)
	public ResponseEntity<Boolean> openEnvelop(@RequestParam("envelopId") String envelopId, @RequestParam("eventId") String eventId, @RequestParam("password") String password) {
		HttpHeaders headers = new HttpHeaders();
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

		try {
			RfaEvent event = eventService.getRfaEventByeventId(eventId);
			RfaEnvelop envelop = envelopService.getRfaEnvelopById(envelopId);

			if (event.getRfxEnvelopeOpening().equals(Boolean.TRUE) && envelop.getEnvelopSequence() != null) {
				if (envelop.getEnvelopSequence() != 1) {

					RfaEnvelop envelopS = envelopService.getRfaEnvelopBySeq(envelop.getEnvelopSequence() - 1, eventId);

					if (event.getRfxEnvOpeningAfter().equals("OPENING")) {
						LOG.info("OPENING .....");
						if (envelopS.getIsOpen().equals(Boolean.FALSE)) {
							LOG.error("opening envlope is not in a sequence .....");
							headers.add("error", "Cannot open this envelope as previous envelope is not opened yet");
							return new ResponseEntity<Boolean>(Boolean.FALSE, headers, HttpStatus.BAD_REQUEST);
						}

					}

					if (event.getRfxEnvOpeningAfter().equals("EVALUATION")) {
						LOG.info("EVALUATION .....");

						if (envelopS.getEvaluationStatus().equals(EvaluationStatus.PENDING)) {
							LOG.error("opeing envlope is not in a sequence .....");
							headers.add("error", "Cannot open this envelope as previous envelope evaluation is still pending ");
							return new ResponseEntity<Boolean>(Boolean.FALSE, headers, HttpStatus.BAD_REQUEST);
						}
					}
				}
			}

			User user = SecurityLibrary.getLoggedInUser();

			if (enc.matches(password, user.getPassword())) {
				Boolean isAllOpen = true;
				List<RfaEnvelopeOpenerUser> openersList = envelop.getOpenerUsers();
				if (CollectionUtil.isNotEmpty(openersList)) {
					for (RfaEnvelopeOpenerUser opener : openersList) {
						if (opener.getUser().getId().equals(user.getId())) {
							opener.setIsOpen(Boolean.TRUE);
							opener.setOpenDate(new Date());
						}
						if (opener.getIsOpen() == null || Boolean.FALSE == opener.getIsOpen()) {
							isAllOpen = false;
						}
					}
					if (isAllOpen) {
						envelop.setIsOpen(Boolean.TRUE);
						envelop.setOpenDate(new Date());
					}

					envelop.setOpenerUsers(openersList);
					envelopService.openEnvelop(envelop);
					headers.add("success", "Envelope \"" + envelop.getEnvelopTitle() + "\" opened successfully");
					try {
						RfaEventAudit audit = new RfaEventAudit();
						audit.setAction(AuditActionType.Open);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setDescription("Envelope '" + envelop.getEnvelopTitle() + "' is opened");
						audit.setActionDate(new Date());
						audit.setEvent(event);
						eventAuditService.save(audit);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

					try {
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.OPEN, "Envelop" + envelop.getEnvelopTitle() + "' is opened successfully", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
					try {
						envelopService.sendEnvelopOpenNotification(envelop, RfxTypes.RFA, eventId, SecurityLibrary.getLoggedInUser(), isAllOpen);
					} catch (Exception e) {
						LOG.error("Error While sending notification on Event Envelop open : " + e.getMessage(), e);
					}

					try {
						// TatReportPojo tatReport =
						// tatReportService.geTatReportFirstEnvelopOpenDateByEventIdAndTenantId(event.getId(),
						// SecurityLibrary.getLoggedInUserTenantId());
						// if (tatReport != null) {
						// TODO::
						tatReportService.updateTatReportFirstEnvelopOpenDate(event.getId(), SecurityLibrary.getLoggedInUserTenantId(), event.getStatus());
						// }

					} catch (Exception e) {
						LOG.info("Error while updting tat report : " + e.getMessage(), e);
					}

				} else {
					throw new ApplicationException("Invalid action by user.");
				}
				return new ResponseEntity<Boolean>(Boolean.TRUE, headers, HttpStatus.OK);
			} else {
				LOG.error("Password mismatch.....");
				headers.add("error", "Password Mismatch");
				return new ResponseEntity<Boolean>(Boolean.FALSE, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (ApplicationException e) {
			LOG.error("Error while opening envelop : " + e.getMessage(), e);
			headers.add("error", "Error while opening envelop : " + e.getMessage());
			return new ResponseEntity<Boolean>(Boolean.FALSE, headers, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			LOG.error("Error while opening envelop : " + e.getMessage(), e);
			return new ResponseEntity<Boolean>(Boolean.FALSE, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(path = "/removeEvaluatorFromList", method = RequestMethod.POST)
	public ResponseEntity<List<UserPojo>> removeEvaluatorFromList(@RequestParam("eventId") String eventId, @RequestParam("envelopeId") String envelopeId, @RequestParam("values") String userId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("removeEvaluator----Evaluator" + " eventId: " + eventId + " envelopeId: " + envelopeId + "value to be remove" + userId);
		List<UserPojo> evaluators = new ArrayList<UserPojo>();
		List<RfaEvaluatorUser> assignedEvaluators = envelopService.findEvaluatorsByEnvelopId(envelopeId);
		for (RfaEvaluatorUser rfaEvaluatorUser : assignedEvaluators) {
			if (rfaEvaluatorUser.getUser().getId().equals(userId)) {
				return new ResponseEntity<List<UserPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		for (UserPojo user : userList) {
			if (!user.getId().equals(userId)) {
				evaluators.add(user);
			}
		}
		return new ResponseEntity<List<UserPojo>>(evaluators, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/closeEnvelope", method = RequestMethod.POST)
	public ResponseEntity<Boolean> closeEnvelope(@RequestParam("envelopId") String envelopId, @RequestParam("eventId") String eventId, @RequestParam("password") String password) {
		LOG.info("POST METHOD");
		HttpHeaders headers = new HttpHeaders();
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		RfaEnvelop envelop = envelopService.getRfaEnvelopById(envelopId);
		RfaEvent event = eventService.getRfaEventByeventId(eventId);
		try {
			User user = SecurityLibrary.getLoggedInUser();
			if (enc.matches(password, user.getPassword())) {

				Boolean isAllClosed = true;
				List<RfaEnvelopeOpenerUser> openersList = envelop.getOpenerUsers();
				if (CollectionUtil.isNotEmpty(openersList)) {
					for (RfaEnvelopeOpenerUser opener : openersList) {
						if (opener.getUser().getId().equals(user.getId())) {
							opener.setIsOpen(Boolean.FALSE);
							opener.setCloseDate(new Date());
						}
						if (Boolean.TRUE == opener.getIsOpen()) {
							isAllClosed = false;
						}
					}
					if (isAllClosed) {
						envelop.setIsOpen(Boolean.FALSE);
						envelop.setCloseDate(new Date());
					}

					envelop.setOpenerUsers(openersList);
					envelopService.closeEnvelop(envelop);

					headers.add("success", "Envelope \"" + envelop.getEnvelopTitle() + "\" closed successfully");
					try {
						RfaEventAudit audit = new RfaEventAudit();
						audit.setAction(AuditActionType.Close);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setDescription("Envelope '" + envelop.getEnvelopTitle() + "' is closed");
						audit.setActionDate(new Date());
						audit.setEvent(event);
						eventAuditService.save(audit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CLOSED, "Envelope '" + envelop.getEnvelopTitle() + "' is closed for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

					try {
						envelopService.sendEnvelopCloseNotification(envelop, RfxTypes.RFA, eventId, SecurityLibrary.getLoggedInUser(), isAllClosed);

					} catch (Exception e) {
						LOG.error("Error While sending notification on Event Envelop close : " + e.getMessage(), e);
					}
				} else {
					throw new ApplicationException("Invalid action by user.");
				}
			} else {
				LOG.error("Password Mismatch.....");
				headers.add("error", "Password Mismatch");
				return new ResponseEntity<Boolean>(Boolean.TRUE, headers, HttpStatus.BAD_REQUEST);

			}
		} catch (ApplicationException e) {
			LOG.error("Error while closeing envelop : " + e.getMessage(), e);
			headers.add("error", "Error while closeing envelop : " + e.getMessage());
			return new ResponseEntity<Boolean>(Boolean.FALSE, headers, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			headers.add("error", "Error while closeing envelop");
			LOG.error("Error while closeing  envelop : " + e.getMessage(), e);
			return new ResponseEntity<Boolean>(Boolean.TRUE, headers, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Boolean>(Boolean.FALSE, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/disableMasking/{eventId}", method = RequestMethod.GET)
	public String disableMasking(@PathVariable("eventId") String eventId, @RequestParam("unmaskPwd") String unmaskPwd, Model model) {
		RfaEvent event = null;
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		try {
			User user = SecurityLibrary.getLoggedInUser();
			event = eventService.getRfaEventByeventId(eventId);

			if (enc.matches(unmaskPwd, user.getPassword())) {
				boolean isAllDone = true;
				List<RfaUnMaskedUser> list = event.getUnMaskedUsers();
				if (CollectionUtil.isNotEmpty(list)) {
					for (RfaUnMaskedUser rfiUnMaskedUser : list) {
						if (rfiUnMaskedUser.getUser().getId().equals(user.getId())) {
							LOG.info("yes mached : " + user.getId());
							rfiUnMaskedUser.setUserUnmasked(true);
							rfiUnMaskedUser.setUserUnmaskedTime(new Date());
						}
						if (rfiUnMaskedUser.getUserUnmasked() == null || Boolean.FALSE == rfiUnMaskedUser.getUserUnmasked()) {
							isAllDone = false;
						}
					}
					if (isAllDone) {
						event.setDisableMasking(true);
					}
					event.setUnMaskedUsers(list);
				} else if (event.getUnMaskedUser() != null) {
					// Old version has only one unmask user stored in event
					event.setDisableMasking(true);
				}
				event = eventService.updateRfaEvent(event);
				try {
					RfaEventAudit audit = new RfaEventAudit();
					audit.setAction(AuditActionType.Complete);
					audit.setActionDate(new Date());
					if (isAllDone) {
						audit.setDescription("Concluded Evaluation and Event Unmasked");
						try {
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.COMPLETE, "Concluded Evaluation and Event Unmasked for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
							buyerAuditTrailDao.save(buyerAuditTrail);
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
					} else {
						audit.setDescription("Unmasked done by user");
						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.COMPLETE, "Unmasked done by user for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
							buyerAuditTrailDao.save(buyerAuditTrail);
							LOG.info("--------------------------AFTER AUDIT---------------------------------------");
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
					}
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setEvent(event);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("error whie save audit");
				}

				TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
				if (tatReport != null && Boolean.TRUE == isAllDone) {
					tatReportService.updateTatReportUnmskingDate(tatReport.getId(), new Date());
				}

				model.addAttribute("event", event);
				model.addAttribute("success", messageSource.getMessage("rfa.success.mask.updated", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/" + getEventType().name() + "/envelopList/" + event.getId();
			} else {
				throw new ApplicationException("Password mismatch");
			}
		} catch (ApplicationException e) {
			model.addAttribute("event", event);
			// model.addAttribute("error", "Password mismatch");
			model.addAttribute("error", messageSource.getMessage("error.password.mismatch", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/" + getEventType().name() + "/envelopList/" + event.getId();
		} catch (Exception e) {
			model.addAttribute("event", event);
			// model.addAttribute("error", "error while updating Mask setting");
			model.addAttribute("error", messageSource.getMessage("error.while.updating.masksetting", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/" + getEventType().name() + "/envelopList/" + event.getId();
		}
	}

	@RequestMapping(path = "/concludeEvaluationPrematurely/{eventId}", method = RequestMethod.POST)
	public ResponseEntity<Void> concludeEvaluationPrematurely(@PathVariable("eventId") String eventId, @RequestParam(name = "concludeEvalDocument", required = false) Optional<MultipartFile> fileOp, @RequestParam(name = "desc", required = false) Optional<String> descOp, @RequestParam("concludeEvaluationPwd") String concludeEvaluationPwd, @RequestParam("concludeEvaluationRemarks") String concludeEvaluationRemarks) {
		HttpHeaders headers = new HttpHeaders();
		RfaEvent event = null;
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		try {
			MultipartFile file = null;
			String desc = null;
			if (fileOp.isPresent()) {
				file = fileOp.get();

				if (descOp.isPresent()) {
					desc = descOp.get();
				}
			}

			User user = SecurityLibrary.getLoggedInUser();
			event = eventService.getRfaEventByeventId(eventId);

			if (StringUtils.checkString(concludeEvaluationRemarks).length() == 0) {
				throw new ApplicationException("Remarks is required.");
			}

			if (enc.matches(concludeEvaluationPwd, user.getPassword())) {

				String fileName = null;
				byte[] fileData = null;
				if (file != null && !file.isEmpty()) {
					fileName = file.getOriginalFilename();
					fileData = file.getBytes();
				}

				boolean isAllDone = true;
				List<RfaEvaluationConclusionUser> list = event.getEvaluationConclusionUsers();
				if (CollectionUtil.isNotEmpty(list)) {
					for (RfaEvaluationConclusionUser conUser : list) {
						if (conUser.getUser().getId().equals(user.getId())) {
							LOG.info("yes mached : " + user.getId());
							conUser.setConcluded(Boolean.TRUE);
							conUser.setConcludedTime(new Date());
							conUser.setRemarks(StringUtils.checkString(concludeEvaluationRemarks));
							conUser.setFileName(fileName);
							conUser.setFileData(fileData);
							conUser.setFileDesc(desc);
						}
						if (conUser.getConcluded() == null || Boolean.FALSE == conUser.getConcluded()) {
							isAllDone = false;
						}
					}
					if (isAllDone) {
						// All is done... do necessary audit logs and change envelope statuses to COMPLETE.
						int totalEnvEvalCount = 0;
						int totalEnvCount = 0;

						List<RfaEnvelop> envList = envelopService.getAllEnvelopByEventId(eventId, user);

						if (CollectionUtil.isNotEmpty(envList)) {
							totalEnvCount = envList.size();
							for (RfaEnvelop env : envList) {
								if (EvaluationStatus.COMPLETE == env.getEvaluationStatus()) {
									totalEnvEvalCount++;
								} else {
									env.setEvaluationStatus(EvaluationStatus.COMPLETE);
									env.setEvaluationCompletedPrematurely(Boolean.TRUE);
									env.setEvaluationDate(new Date());
									envelopService.openEnvelop(env);
									// envelopService.updateRfaEnvelop(env, null, null);
								}
							}
						}
						// Set evaluation completed
						event.setStatus(EventStatus.COMPLETE);
						event.setEvaluationConclusionEnvelopeEvaluatedCount(totalEnvEvalCount);
						event.setEvaluationConclusionEnvelopeNonEvaluatedCount(totalEnvCount - totalEnvEvalCount);

						int disqualifiedSupCount = 0;
						int remainingSupCount = 0;

						if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
							for (RfaEventSupplier sup : event.getSuppliers()) {
								if (Boolean.TRUE == sup.getDisqualify()) {
									disqualifiedSupCount++;
								} else if (SubmissionStatusType.COMPLETED == sup.getSubmissionStatus()) {
									remainingSupCount++;
								}
							}
						}

						event.setEvaluationConclusionDisqualifiedSupplierCount(disqualifiedSupCount);
						event.setEvaluationConclusionRemainingSupplierCount(remainingSupCount);
					}

					event.setEvaluationConclusionUsers(list);
					event = eventService.updateRfaEvent(event);

					// Send Notifications
					try {
						eventService.sendEvaluationCompletedPrematurelyNotification(user, event);
					} catch (Exception e) {
						LOG.error("error whie sending notification : " + e.getMessage(), e);
					}

					try {
						RfaEventAudit audit = new RfaEventAudit();
						audit.setAction(AuditActionType.Complete);
						audit.setActionDate(new Date());
						audit.setDescription("Evaluation Concluded Prematurely");
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setEvent(event);
						eventAuditService.save(audit);

						RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.COMPLETE, "Evaluation Concluded Prematurely for Event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error("error whie save audit : " + e.getMessage(), e);
					}

				} else {
					throw new ApplicationException("Invalid action by user.");
				}

				try {
					if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
						tatReportService.updateTatReportEvaluationCompleted(event.getId(), SecurityLibrary.getLoggedInUserTenantId(), new Date(), EventStatus.COMPLETE);
					}
				} catch (Exception e) {
					LOG.info("Error while updting tat report : " + e.getMessage(), e);
				}

				headers.add("success", "Premature conclusion of evaluation is successful");
			} else {
				throw new ApplicationException("Password mismatch");
			}
		} catch (ApplicationException e) {
			LOG.error("Error during premature evaluation conclusion : " + e.getMessage(), e);
			// model.addAttribute("error", "Password mismatch");
			headers.add("error", "Error during premature evaluation conclusion : " + e.getMessage());
			return new ResponseEntity<Void>(headers, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			LOG.error("Error during premature evaluation conclusion : " + e.getMessage(), e);
			// model.addAttribute("error", "error while updating Mask setting");
			headers.add("error", "Error during premature evaluation conclusion : " + e.getMessage());
			return new ResponseEntity<Void>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

}
