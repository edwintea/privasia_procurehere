package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.privasia.procurehere.core.entity.RftEventSor;
import com.privasia.procurehere.service.RftSorService;
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
import com.privasia.procurehere.core.dao.RftEnvelopDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RftEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.RftUnMaskedUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.EvaluationStatus;
import com.privasia.procurehere.core.enums.EventStatus;
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
import com.privasia.procurehere.service.RftBqService;
import com.privasia.procurehere.service.RftCqService;
import com.privasia.procurehere.service.RftEnvelopService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.web.editors.RftEnvelopeOpenerUserEditor;

@Controller
@RequestMapping("/buyer/RFT")
public class RftEnvelopController extends EventEnvelopBase {

	@Autowired
	RftEventService eventService;

	@Autowired
	RftEnvelopService envelopService;

	@Autowired
	RftCqService rftCqService;

	@Autowired
	RftBqService rftBqService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	RftEnvelopeOpenerUserEditor envelopeOpenerUserEditor;

	@Autowired
	RftEnvelopDao rftEnvelopDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RftSorService rftSorService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		super.initBinder(binder);
		binder.registerCustomEditor(RftEnvelopeOpenerUser.class, envelopeOpenerUserEditor);
	}

	public RftEnvelopController() {
		super(RfxTypes.RFT);
	}

	@RequestMapping(value = "/envelopPrevious", method = RequestMethod.POST)
	public String envelopPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		RftEvent rftEvent = eventService.getRftEventById(eventId);
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			return super.envelopPrevious(rftEvent);
		} else {
			LOG.error("Event not found redirecting to login ");
			return "/";
		}
	}

	@RequestMapping(value = "/envelopNext", method = RequestMethod.POST)
	public String envelopNext(Model model, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes attributes) {
		RftEvent rftEvent = eventService.getRftEventById(eventId);
		if (rftEvent != null) {

			if (Boolean.TRUE == rftEvent.getRfxEnvelopeOpening()) {
				int count = 0;
				int sum = 0;
				int seq = 0;
				List<Integer> list = new ArrayList<Integer>();

				for (RftEnvelop envlope : rftEvent.getRftEnvelop()) {
					if (envlope.getEnvelopType() == EnvelopType.CLOSED && CollectionUtil.isEmpty(envlope.getOpenerUsers())) {
						attributes.addFlashAttribute("error", "Please select Envelope Opener for Envelope : " + envlope.getEnvelopTitle());
						return "redirect:envelopList/" + rftEvent.getId();
					}

					count++;
					if (envlope.getEnvelopSequence() != null) {
						if (envlope.getEnvelopSequence() <= 0) {
							attributes.addFlashAttribute("error", "Sequence must contain numeric values in ascending order");
							return "redirect:envelopList/" + rftEvent.getId();
						}
						seq += envlope.getEnvelopSequence();
						list.add(envlope.getEnvelopSequence());
					}
				}
				for (int s = 1; s <= count; s++) {
					sum += s;
				}

				Set<Integer> set = new HashSet<Integer>(list);
				if (set.size() < list.size()) {
					attributes.addFlashAttribute("error", "Duplicate sequence not allowed");
					return "redirect:envelopList/" + rftEvent.getId();
				}

				if (seq == 0) {
					attributes.addFlashAttribute("error", "Envelope sequence is missing");
					return "redirect:envelopList/" + rftEvent.getId();
				}

				if (seq != sum) {
					attributes.addFlashAttribute("error", "Sequence must contain numeric values in ascending order");
					return "redirect:envelopList/" + rftEvent.getId();
				}
			}

			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			rftEvent.setEnvelopCompleted(Boolean.TRUE);
			eventService.updateRftEvent(rftEvent);
			return "redirect:eventSummary/" + rftEvent.getId();
		} else {
			LOG.error("Event not found redirecting to login ");
			return "/";
		}
	}

	@RequestMapping(path = "/envelopList/{eventId}", method = RequestMethod.GET)
	public String envelopList(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		RftEvent event = eventService.loadRftEventById(eventId);
		Boolean isPreSet = eventService.isDefaultPreSetEnvlope(eventId);

		LOG.info("Allow to close " + event.getCloseEnvelope());
		model.addAttribute("closeEnv", event.getCloseEnvelope());
		model.addAttribute("allowToView", event.getViewSupplerName());
		EventPermissions permission = eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
		List<RftEnvelop> envelopList = envelopService.getAllRftEnvelopByEventId(eventId, SecurityLibrary.getLoggedInUser());
		for (RftEnvelop rftEnvelop : envelopList) {
			EventPermissions envPermission = eventService.getUserPemissionsForEnvelope(SecurityLibrary.getLoggedInUser().getId(), eventId, rftEnvelop.getId());
			if (envPermission.isEvaluator() || envPermission.isLeadEvaluator()) {
				LOG.info("user is evaluator or lead evaluator");
				rftEnvelop.setShowOpen(true);
				if (event.getEnableEvaluationDeclaration()) {
					LOG.info("Enable Declaration of evaluation");
					rftEnvelop.setShowEvaluationDeclaration(envelopService.isAcceptedEvaluationDeclaration(rftEnvelop.getId(), SecurityLibrary.getLoggedInUser().getId(), eventId));
				}
			}
			if (envPermission.isOpener()) {
				rftEnvelop.setPermitToOpenClose(true);
			}
		}
		LOG.info("isPreSet " + isPreSet);

		model.addAttribute("envelopList", envelopList);
		model.addAttribute("preSetEnvlop", isPreSet);
		model.addAttribute("eventPermissions", permission);
		model.addAttribute("isMaskingEnable", event.getViewSupplerName());
		model.addAttribute("event", eventService.loadRftEventById(eventId));
		model.addAttribute("loggedInUserId", SecurityLibrary.getLoggedInUser().getId());
		LOG.info("event.getEventStart() : " + event.getEventStart() + " Status : " + event.getStatus());
		if (event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED) {
			return "envelopList";
		} else {
			model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
			model.addAttribute("showEnvelopTab", true);
			return "ongoingEnvelopList";
		}
	}

	@RequestMapping(path = "/showEnvelop", method = RequestMethod.POST)
	public String showCreateEnvolpe(@RequestParam String eventId, Model model, RedirectAttributes attributes) {
		return "redirect:envelop/" + eventId;
	}

	@RequestMapping(path = "/envelop/{eventId}", method = RequestMethod.GET)
	public ModelAndView createEnvelop(@PathVariable String eventId, @ModelAttribute RftEnvelop rftEnvelop, Model model) {
		LOG.info("Create Envelop called");
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		// List<User> userList =
		// userService.fetchAllActiveUsersForEnvelopForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		// model.addAttribute("eventPermissions",
		// eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

		model.addAttribute("eventPermissions", new EventPermissions());
		RftEnvelop envelope = new RftEnvelop();
		envelope.setEnvelopType(EnvelopType.CLOSED);
		envelope.setPreFix("SUPPLIER");
		envelope.setOpener(SecurityLibrary.getLoggedInUser());
		envelope.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
		model.addAttribute("openers", userList);
		model.addAttribute("evaluationOwner", userList);
		model.addAttribute("event", eventService.loadRftEventById(eventId));
		model.addAttribute("btnValue", "Create");
		model.addAttribute("bqlist", rftBqService.getRftEventBqForEventIdForEnvelop(eventId, null));
		model.addAttribute("sorlist", rftSorService.getEventSorForEventIdForEnvelop(eventId, null));
		model.addAttribute("cqlist", rftCqService.getRftCqByEventId(eventId, null));
		model.addAttribute("envelop", envelope);
		return new ModelAndView("envelop");
	}

	@RequestMapping(path = "/envelop", method = RequestMethod.POST)
	public @ResponseBody ModelAndView saveEnvelop(@ModelAttribute("envelop") RftEnvelop envelop, BindingResult result, Model model, @RequestParam(value = "bqids[]", required = false) String[] bqids, @RequestParam(value = "cqids[]", required = false) String[] cqids,@RequestParam(value = "sorids[]", required = false) String[] sorids , @RequestParam String eventId, RedirectAttributes redir) {
		EventPermissions eventPermissions = null;
		RftEvent rftEvent = eventService.loadRftEventById(eventId);

		String eventOwnerID = rftEvent.getCreatedBy().getId();
		String userControlledFlag = null;
		boolean isUserControl = Boolean.TRUE.equals(SecurityLibrary.getLoggedInUser().getBuyer().getEnableEventUserControle());

		LOG.info("========" + userControlledFlag);
		LOG.info("========" + eventOwnerID);
		LOG.info("========" + isUserControl);

		model.addAttribute("bqlist", rftBqService.getRftEventBqForEventIdForEnvelop(eventId, null));
		model.addAttribute("sorlist", rftSorService.getEventSorForEventIdForEnvelop(eventId, null));
		model.addAttribute("cqlist", rftCqService.getRftCqByEventId(eventId, null));
		model.addAttribute("event", rftEvent);
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);

		List<UserPojo> openerUserList = new ArrayList<UserPojo>();
		for (UserPojo user : userList) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			openerUserList.add(user);
		}

		model.addAttribute("evaluationOwner", userList);
		eventService.getUserPemissionsForEnvelope(SecurityLibrary.getLoggedInUser().getId(), eventId, envelop.getId());
		model.addAttribute("eventPermissions", eventPermissions);

		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
					LOG.info("ERROR : " + err.getDefaultMessage());
				}
				model.addAttribute("btnValue", "Create");
				eventPermissions = eventService.getUserPemissionsForEnvelope(SecurityLibrary.getLoggedInUser().getId(), eventId, envelop.getId());
				model.addAttribute("eventPermissions", eventPermissions);
				model.addAttribute("error", errMessages);
				return new ModelAndView("envelop", "envelop", envelop);

			} else {

				List<RftEnvelopeOpenerUser> openerUsers = envelop.getOpenerUsers();
				if (CollectionUtil.isNotEmpty(openerUsers)) {
					for (RftEnvelopeOpenerUser user : openerUsers) {
						user.setEvent(rftEvent);
						user.setEnvelope(envelop);
						UserPojo u = new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications());
						if (!openerUserList.contains(u)) {
							openerUserList.add(u);
						}
					}
				}

				model.addAttribute("openers", openerUserList);

				if (StringUtils.checkString(envelop.getEnvelopTitle()).length() == 0) {
					model.addAttribute("error", messageSource.getMessage("envelop.title.req", new Object[] {}, Global.LOCALE));
					return new ModelAndView("envelop", "envelop", envelop);
				}
				if (envelop.getEnvelopType() == EnvelopType.CLOSED && CollectionUtil.isEmpty(envelop.getOpenerUsers())) {
					model.addAttribute("error", messageSource.getMessage("error.while.select.opener", new Object[] {}, Global.LOCALE));
					return new ModelAndView("envelop", "envelop", envelop);
				}
				if (envelop.getLeadEvaluater() == null) {
					model.addAttribute("error", messageSource.getMessage("error.while.select.evaluation.owner", new Object[] {}, Global.LOCALE));
					return new ModelAndView("envelop", "envelop", envelop);
				}
				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers()) && envelop.getEnvelopType() == EnvelopType.CLOSED) {
					for (RftEnvelopeOpenerUser user : envelop.getOpenerUsers()) {
						if (isUserControl && user.getUser().getId().equals(eventOwnerID)) {
							throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Envelope Opener" }, Global.LOCALE));
						}
					}
				}

				if (isUserControl && envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(eventOwnerID)) {
					throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Envelope Evaluation Owner" }, Global.LOCALE));
				}

				if (doValidate(envelop, eventId)) {
					if (StringUtils.checkString(envelop.getId()).length() == 0) {
						envelop.setRfxEvent(rftEvent);
						envelop.setEnvelopSequence(envelop.getEnvelopSequence());

						if (envelop.getEnvelopType() == EnvelopType.OPEN) {
							envelop.setOpener(null);
							envelop.setIsOpen(Boolean.TRUE);
							envelop.setOpenerUsers(null);
						}
						envelopService.saveRftEnvelop(envelop, bqids, cqids, sorids);
						redir.addFlashAttribute("success", messageSource.getMessage("envelop.create.success", new Object[] { envelop.getEnvelopTitle() }, Global.LOCALE));
					} else {
						LOG.info("update Envelope Called");
						RftEnvelop persistObj = envelopService.getRftEnvelopById(envelop.getId());
						if (envelop.getLeadEvaluater() != null && envelopService.getCountOfAssignedSupplier(envelop.getLeadEvaluater().getId(), envelop.getId(), eventId)) {
							model.addAttribute("error", messageSource.getMessage("envelope.error.select.leadevaluator", new Object[] {}, Global.LOCALE));
							return editEnvelope(envelop.getId(), eventId, model);
						}
						persistObj.setEnvelopTitle(envelop.getEnvelopTitle());
						persistObj.setPreFix(envelop.getPreFix());
						persistObj.setDescription(envelop.getDescription());
						persistObj.setEnvelopType(envelop.getEnvelopType());
						persistObj.setLeadEvaluater(envelop.getLeadEvaluater());
						persistObj.setEnvelopSequence(envelop.getEnvelopSequence());
						persistObj.setOpenerUsers(envelop.getOpenerUsers());
						if (envelop.getEnvelopType() == EnvelopType.OPEN) {
							persistObj.setOpener(null);
							persistObj.setIsOpen(Boolean.TRUE);
							persistObj.setOpenerUsers(null);
						} else {
							persistObj.setIsOpen(Boolean.FALSE);
						}
						// persistObj.setOpener(envelop.getOpener());
						envelopService.updateRftEnvelop(persistObj, bqids, cqids, sorids);
						redir.addFlashAttribute("success", messageSource.getMessage("envelop.update.success", new Object[] { envelop.getEnvelopTitle() }, Global.LOCALE));

					}
				} else {
					LOG.info("Validation error ...............");
					model.addAttribute("error", messageSource.getMessage("envelop.duplicate.error", new Object[] { envelop.getEnvelopTitle() }, Global.LOCALE));
					return new ModelAndView("envelop", "envelop", envelop);
				}
			}
			eventPermissions = eventService.getUserPemissionsForEnvelope(SecurityLibrary.getLoggedInUser().getId(), eventId, envelop.getId());
			model.addAttribute("eventPermissions", eventPermissions);
		} catch (Exception e) {
			LOG.error("Error While Save the Envelop : " + e.getMessage(), e);
			eventPermissions = eventService.getUserPemissionsForEnvelope(SecurityLibrary.getLoggedInUser().getId(), eventId, envelop.getId());
			model.addAttribute("eventPermissions", eventPermissions);
			model.addAttribute("error", messageSource.getMessage("envelope.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("envelop", "envelop", envelop);
		}

		return new ModelAndView("redirect:envelopList/" + eventId);

	}

	private boolean doValidate(RftEnvelop rftEnvelop, String eventId) {
		boolean validate = true;
		if (envelopService.isExists(rftEnvelop, eventId)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/envelopCancel", method = RequestMethod.POST)
	public String envelopCancel(@RequestParam String eventId, Model model) {
		return "redirect:envelopList/" + eventId;
	}

	@RequestMapping(path = "/editEnvelope", method = RequestMethod.POST)
	public ModelAndView editEnvelope(@RequestParam String envelopId, @RequestParam String eventId, Model model) {

		List<RftEvaluatorUser> assignedEvaluators = null;
		assignedEvaluators = envelopService.findEvaluatorsByEnvelopId(envelopId);
		RftEnvelop envelop = envelopService.getRftEnvelopById(envelopId);
		List<RftEventBq> assignedBqList = envelop.getBqList();
		List<RftEventSor> assignedSorList = envelop.getSorList();
		List<RftCq> assignedCqList = envelop.getCqList();
		RftEvent rftEvent = eventService.loadRftEventById(eventId);

		// List<User> userList =
		// userService.fetchAllActiveUsersForEnvelopForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);

		List<UserPojo> openerUserList = new ArrayList<UserPojo>();

		for (UserPojo user : userList) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			openerUserList.add(user);
		}

		List<RftEnvelopeOpenerUser> openerUSerList = envelop.getOpenerUsers();
		if (CollectionUtil.isNotEmpty(openerUSerList)) {
			for (RftEnvelopeOpenerUser user : openerUSerList) {
				UserPojo u = new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications());
				if (!openerUserList.contains(u)) {
					openerUserList.add(u);
				}
			}
		}

		// setting opener if selected user is approver or inactive
		// List<UserPojo> openerListSelectedUser = new ArrayList<UserPojo>();
		// if (envelop.getOpener() != null) {
		// try {
		// openerListSelectedUser.add(new UserPojo(envelop.getOpener().getId(), envelop.getOpener().getLoginId(),
		// envelop.getOpener().getName(), envelop.getOpener().getTenantId(), envelop.getOpener().isDeleted(),
		// envelop.getOpener().getCommunicationEmail()));
		// } catch (Exception e) {
		// LOG.info("Error while cloning the user :" + e.getMessage());
		// }
		// }
		// for (UserPojo user : userList) {
		// boolean isOpener = false;
		// try {
		// if (envelop.getOpener() != null && envelop.getOpener().getId().equals(user.getId())) {
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
		if (envelop.getLeadEvaluater() != null) {
			try {
				ownerListSelectedUser.add(new UserPojo(envelop.getLeadEvaluater().getId(), envelop.getLeadEvaluater().getLoginId(), envelop.getLeadEvaluater().getName(), envelop.getLeadEvaluater().getTenantId(), envelop.getLeadEvaluater().isDeleted(), envelop.getLeadEvaluater().getCommunicationEmail(), envelop.getLeadEvaluater().getEmailNotifications()));
			} catch (Exception e) {
				LOG.info("Error while cloning the user :" + e.getMessage());
			}
		}
		for (UserPojo user : userList) {
			boolean isLeadEval = false;
			try {
				if (envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(user.getId())) {
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
			for (RftEvaluatorUser evalUser : assignedEvaluators) {
				if (evalUser.getUser().getId().equals(user.getId())) {
					found = true;
					break;
				}
			}

			if (!found && (envelop.getLeadEvaluater() == null || (envelop.getLeadEvaluater() != null && !user.getId().equals(envelop.getLeadEvaluater().getId())))) {
				evaluators.add(user);
			}
		}
		model.addAttribute("Evaluators", evaluators);

		model.addAttribute("envelop", envelop);
		model.addAttribute("bqOfEnvelope", assignedBqList);
		model.addAttribute("sorOfEnvelope", assignedSorList);
		model.addAttribute("cqOfEnvelope", assignedCqList);
		model.addAttribute("event", rftEvent);
		// Get envelope permissions
		EventPermissions eventPermissions = eventService.getUserPemissionsForEnvelope(SecurityLibrary.getLoggedInUser().getId(), eventId, envelopId);
		model.addAttribute("eventPermissions", eventPermissions);

		List<String> bqIds = null;
		if (CollectionUtil.isNotEmpty(assignedBqList)) {
			bqIds = new ArrayList<String>();
			for (RftEventBq bq : assignedBqList) {
				bqIds.add(bq.getId());
			}
		}
		List<RftEventBq> bqList = rftBqService.getRftEventBqForEventIdForEnvelop(eventId, bqIds);
		model.addAttribute("bqlist", bqList);

		List<String> sorIds = null;
		if (CollectionUtil.isNotEmpty(assignedSorList)) {
			sorIds = new ArrayList<String>();
			for (RftEventSor bq : assignedSorList) {
				sorIds.add(bq.getId());
			}
		}

		List<RftEventSor> sorList = rftSorService.getEventSorForEventIdForEnvelop(eventId, sorIds);
		model.addAttribute("sorlist", sorList);

		List<String> cqIds = null;
		if (CollectionUtil.isNotEmpty(assignedCqList)) {
			cqIds = new ArrayList<String>();
			for (RftCq cq : assignedCqList) {
				cqIds.add(cq.getId());
			}
		}
		List<RftCq> cqList = rftCqService.getRftCqByEventId(eventId, cqIds);
		model.addAttribute("cqlist", cqList);

		return new ModelAndView("envelop", "envelop", envelop);
	}

	@RequestMapping(path = "/deleteEnvelope", method = RequestMethod.POST)
	public String deleteEnvelope(@RequestParam String envelopId, @RequestParam String eventId, Model model, RedirectAttributes attributes) {
		RftEnvelop rftEnvelop = null;
		try {
			rftEnvelop = envelopService.getRftEnvelopById(envelopId);
			LOG.info("Delete the envelope :   " + rftEnvelop.getEnvelopTitle());
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			envelopService.deleteRftEnvelop(rftEnvelop);
			attributes.addFlashAttribute("success", messageSource.getMessage("envelop.deleted.success", new Object[] { rftEnvelop.getEnvelopTitle() }, Global.LOCALE));
		} catch (Exception e) {
			EventPermissions eventPermissions = eventService.getUserPemissionsForEnvelope(SecurityLibrary.getLoggedInUser().getId(), eventId, envelopId);
			model.addAttribute("eventPermissions", eventPermissions);
			LOG.error("Error while deleting country [ " + rftEnvelop.getEnvelopTitle() + " ]" + e.getMessage(), e);
			attributes.addFlashAttribute("error", messageSource.getMessage("envelop.deleted.error", new Object[] { rftEnvelop.getEnvelopTitle() }, Global.LOCALE));
		}
		return "redirect:envelopList/" + eventId;
	}

	@RequestMapping(value = "/envelopSaveDraft", method = RequestMethod.POST)
	public String envelopSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId) {
		RftEvent rftEvent = eventService.getRftEventByeventId(eventId);
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
		return "redirect:envelopList/" + eventId;

	}

	@RequestMapping(path = "/bqCqList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<String>> bqCqList(@RequestParam("envelopId") String envelopId) {
		// RftEnvelop rftEnvelop = envelopService.getRftEnvelopById(envelopId);
		List<RftEventBq> rftBq = envelopService.getBqsByEnvelopIdByOrder(envelopId);
		List<RftCq> rftCq = envelopService.getCqsByEnvelopIdByOrder(envelopId);
		List<RftEventSor> rfqSor = envelopService.getSorsByEnvelopIdByOrder(envelopId);
		List<String> returnList = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(rftCq)) {
			for (RftCq cqsOfEnvelop : rftCq) {
				returnList.add(cqsOfEnvelop.getName() + " [Questionnaire]");
			}
		}
		if (CollectionUtil.isNotEmpty(rftBq)) {
			for (RftEventBq bqsOfEnvelop : rftBq) {
				returnList.add(bqsOfEnvelop.getName() + " [Bill of Quantity]");
			}
		}
		if (CollectionUtil.isNotEmpty(rfqSor)) {
			for (RftEventSor bqsOfEnvelop : rfqSor) {
				returnList.add(bqsOfEnvelop.getName() + " [Schedule of Rate]");
			}
		}
		return new ResponseEntity<List<String>>(returnList, HttpStatus.OK);
	}

	@RequestMapping(path = "/addEvaluatorToList", method = RequestMethod.POST)
	public ResponseEntity<List<RftEvaluatorUser>> addEvaluator(@RequestParam("leadEvaluaterid") String leadEvaluaterid, @RequestParam("eventId") String eventId, @RequestParam("envelopeId") String envelopeId, @RequestParam("userId") String userId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("addEvaluatorToList----Evaluator" + " eventId: " + eventId + " envelopeId: " + envelopeId + " userId: " + userId);
		List<RftEvaluatorUser> evaluators = null;
		try {
			List<RftEvaluatorUser> assignedEvaluators = envelopService.findEvaluatorsByEnvelopId(envelopeId);
			for (RftEvaluatorUser rfaEvaluatorUser : assignedEvaluators) {
				if (rfaEvaluatorUser.getUser().getId().equals(userId)) {
					return new ResponseEntity<List<RftEvaluatorUser>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}

			if (StringUtils.checkString(envelopeId).length() > 0) {
				Boolean isUserControl = SecurityLibrary.getLoggedInUser().getBuyer().getEnableEventUserControle();
				LOG.info("isUserControl   : " + isUserControl);
				if (Boolean.TRUE == isUserControl) {
					String eventOwnerID = eventService.getEventOwnerId(eventId);
					if (!StringUtils.checkString(leadEvaluaterid).isEmpty() && leadEvaluaterid.equals(eventOwnerID)) {
						throw new ApplicationException(messageSource.getMessage("rftEvent.error.eveluowner.control", new Object[] {}, Global.LOCALE));
					}

					if (Boolean.TRUE == isUserControl) {
						if (userId.equals(eventOwnerID)) {
							throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Envelope Evaluation User" }, Global.LOCALE));
						}
					}
				}
				LOG.info("envelopeId : " + envelopeId);
				evaluators = envelopService.addEvaluator(eventId, envelopeId, userId);
			}
		} catch (Exception e) {
			LOG.error("Error While adding evaluator user : " + e.getMessage(), e);
			headers.add("error", "Error While adding evaluator user : " + e.getMessage());
			return new ResponseEntity<List<RftEvaluatorUser>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		LOG.info("RftController addEvaluatorToList editors: " + evaluators);
		return new ResponseEntity<List<RftEvaluatorUser>>(evaluators, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/removeEvaluatorToList", method = RequestMethod.POST)
	public ResponseEntity<List<UserPojo>> removeEvaluator(@RequestParam("eventId") String eventId, @RequestParam("envelopeId") String envelopeId, @RequestParam("userId") String userId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("removeEvaluator----Evaluator" + " eventId: " + eventId + " envelopeId: " + envelopeId + " userId: " + userId);
		List<User> evaluators = null;
		RftEnvelop rfaEnvelop = envelopService.getRftEnvelopById(envelopeId);
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

		// Remove all users that are already added as evaluators.
		return new ResponseEntity<List<UserPojo>>(evList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/openEnvelop", method = RequestMethod.POST)
	public ResponseEntity<Boolean> openEnvelop(@RequestParam("envelopId") String envelopId, @RequestParam("eventId") String eventId, @RequestParam("password") String password) {
		HttpHeaders headers = new HttpHeaders();
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		try {
			RftEnvelop envelop = envelopService.getRftEnvelopById(envelopId);
			RftEvent event = eventService.getRftEventByeventId(eventId);

			if (event.getRfxEnvelopeOpening().equals(Boolean.TRUE) && envelop.getEnvelopSequence() != null) {
				if (envelop.getEnvelopSequence() != 1) {

					RftEnvelop envelopS = envelopService.getRftEnvelopBySeqId(envelop.getEnvelopSequence() - 1, eventId);
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
							headers.add("error", "Cannot open this envelope as previous envelope evaluation is still pending");
							return new ResponseEntity<Boolean>(Boolean.FALSE, headers, HttpStatus.BAD_REQUEST);
						}
					}
				}
			}

			User user = SecurityLibrary.getLoggedInUser();
			if (enc.matches(password, user.getPassword())) {
				Boolean isAllOpen = true;
				List<RftEnvelopeOpenerUser> openersList = envelop.getOpenerUsers();
				if (CollectionUtil.isNotEmpty(openersList)) {
					for (RftEnvelopeOpenerUser opener : openersList) {
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
					try {
						envelopService.sendEnvelopOpenNotification(envelop, RfxTypes.RFT, eventId, SecurityLibrary.getLoggedInUser(), isAllOpen);
					} catch (Exception e) {
						LOG.error("Error While sending notification on Event Envelop open : " + e.getMessage(), e);
					}
					headers.add("success", "Envelope \"" + envelop.getEnvelopTitle() + "\" opened successfully");
					try {
						RftEventAudit audit = new RftEventAudit();
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
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.OPEN, "Envelop '" + envelop.getEnvelopTitle() + "' is opened for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
					try {
//						TatReportPojo tatReport = tatReportService.geTatReportFirstEnvelopOpenDateByEventIdAndTenantId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
//						if (tatReport != null) {
							tatReportService.updateTatReportFirstEnvelopOpenDate(event.getId(), SecurityLibrary.getLoggedInUserTenantId(), event.getStatus());
							//						}
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
		List<RftEvaluatorUser> assignedEvaluators = envelopService.findEvaluatorsByEnvelopId(envelopeId);
		for (RftEvaluatorUser rftEvaluatorUser : assignedEvaluators) {
			if (rftEvaluatorUser.getUser().getId().equals(userId)) {
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
		RftEnvelop envelop = envelopService.getRftEnvelopById(envelopId);
		RftEvent event = eventService.getRftEventByeventId(eventId);
		try {
			User user = SecurityLibrary.getLoggedInUser();
			if (enc.matches(password, user.getPassword())) {

				Boolean isAllClosed = true;
				List<RftEnvelopeOpenerUser> openersList = envelop.getOpenerUsers();
				if (CollectionUtil.isNotEmpty(openersList)) {
					for (RftEnvelopeOpenerUser opener : openersList) {
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
						RftEventAudit audit = new RftEventAudit();
						audit.setAction(AuditActionType.Close);
						audit.setActionDate(new Date());
						audit.setDescription("Envelope '" + envelop.getEnvelopTitle() + "' is Closed");
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setEvent(event);
						eventAuditService.save(audit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CLOSED, "Envelope '" + envelop.getEnvelopTitle() + "' is closed for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e);
					}

					try {
						envelopService.sendEnvelopCloseNotification(envelop, RfxTypes.RFT, eventId, SecurityLibrary.getLoggedInUser(), isAllClosed);

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
		RftEvent event = null;
		try {
			User user = SecurityLibrary.getLoggedInUser();
			event = eventService.getRftEventByeventId(eventId);
			BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

			if (enc.matches(unmaskPwd, user.getPassword())) {
				boolean isAllDone = true;
				List<RftUnMaskedUser> list = event.getUnMaskedUsers();
				if (CollectionUtil.isNotEmpty(list)) {
					for (RftUnMaskedUser rfiUnMaskedUser : list) {
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
				event = eventService.updateRftEvent(event);
				try {
					RftEventAudit audit = new RftEventAudit();
					audit.setAction(AuditActionType.Complete);
					audit.setActionDate(new Date());
					if (isAllDone) {
						audit.setDescription("Concluded Evaluation and Event Unmasked");
					} else {
						audit.setDescription("Unmasked done by user");
					}
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setEvent(event);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.info("error whie save audit");
				}

				if (isAllDone) {
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.COMPLETE, "Concluded Evaluation and Event Unmasked for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				} else {
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.COMPLETE, "Unmasked done by user for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				}
				TatReportPojo tatReport = tatReportService.geTatReportFirstEnvelopOpenDateByEventIdAndTenantId(event.getId(), SecurityLibrary.getLoggedInUserTenantId());
				if (tatReport != null && Boolean.TRUE == isAllDone) {
					tatReportService.updateTatReportUnmskingDate(tatReport.getId(), new Date());
				}

				model.addAttribute("success", messageSource.getMessage("rfa.success.mask.updated", new Object[] {}, Global.LOCALE));
				model.addAttribute("event", event);
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
		RftEvent event = null;
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
			event = eventService.getRftEventByeventId(eventId);

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
				List<RftEvaluationConclusionUser> list = event.getEvaluationConclusionUsers();
				if (CollectionUtil.isNotEmpty(list)) {
					for (RftEvaluationConclusionUser conUser : list) {
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

						List<RftEnvelop> envList = envelopService.getAllRftEnvelopByEventId(eventId, user);

						if (CollectionUtil.isNotEmpty(envList)) {
							totalEnvCount = envList.size();
							for (RftEnvelop env : envList) {
								if (EvaluationStatus.COMPLETE == env.getEvaluationStatus()) {
									totalEnvEvalCount++;
								} else {

									// Check if anyone has competed the evaluation. If yes, probably we do not have to
									// flag envelope as CompletedPrematurely - because we need to show reports even if
									// single user has completed evaluation.
									// boolean anyOneCompleted = false;
									// List<RftEvaluatorUser> usrList = env.getEvaluators();
									// if (CollectionUtil.isNotEmpty(usrList)) {
									// for (RftEvaluatorUser usr : usrList) {
									// if (usr.getEvaluationStatus() == EvaluationStatus.COMPLETE) {
									// anyOneCompleted = true;
									// break;
									// }
									// }
									// }

									env.setEvaluationStatus(EvaluationStatus.COMPLETE);
									env.setEvaluationCompletedPrematurely(Boolean.TRUE);
									env.setEvaluationDate(new Date());
									envelopService.openEnvelop(env);
									// envelopService.updateRftEnvelop(env, null, null);
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
							for (RftEventSupplier sup : event.getSuppliers()) {
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
					event = eventService.updateRftEvent(event);

					// Send Notifications
					try {
						eventService.sendEvaluationCompletedPrematurelyNotification(user, event);
					} catch (Exception e) {
						LOG.error("error whie sending notification : " + e.getMessage(), e);
					}

					try {
						RftEventAudit audit = new RftEventAudit();
						audit.setAction(AuditActionType.Complete);
						audit.setActionDate(new Date());
						audit.setDescription("Evaluation Concluded Prematurely");
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setEvent(event);
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error("error whie save audit : " + e.getMessage(), e);
					}
					try {
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.COMPLETE, "Evaluation Concluded Prematurely for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
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
