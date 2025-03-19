package com.privasia.procurehere.web.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.RfaEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfiEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfpEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfqEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RftEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.UserEditor;

public class EventEnvelopBase {

	protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	UserService userService;

	@Autowired
	UserEditor userEditor;

	@Resource
	MessageSource messageSource;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfaEventService rfaEventService;
	
	@Autowired
	TatReportService tatReportService;
	
	@Autowired
	RfaEventSupplierService rfaEventSupplierService;
	
	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	private RfxTypes eventType;

	public EventEnvelopBase(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("eventType")
	public RfxTypes getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("step")
	public String getStep() {
		return "8";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(User.class, userEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	/**
	 * @param event
	 * @return
	 */
	protected String envelopPrevious(Event event) {
		if (Boolean.TRUE == event.getScheduleOfRate()) {
			return "redirect:createSorList/" + event.getId();
		} else if (Boolean.TRUE == event.getBillOfQuantity()) {
			return "redirect:createBQList/" + event.getId();
		} else if (Boolean.TRUE == event.getQuestionnaires()) {
			return "redirect:eventCqList/" + event.getId();
		} else if (Boolean.TRUE == event.getMeetingReq()) {
			return "redirect:meetingList/" + event.getId();
		} else if (event.getEventVisibility() != EventVisibilityType.PUBLIC) {
			return "redirect:addSupplier/" + event.getId();
		} else if (Boolean.TRUE == event.getDocumentReq()) {
			return "redirect:createEventDocuments/" + event.getId();
		} else {
			return "redirect:eventDescription/" + event.getId();
		}
	}

	@RequestMapping(path = "/validatePassword/{eventId}", method = RequestMethod.POST)
	public ResponseEntity<Void> validatePassword(@PathVariable("eventId") String eventId, @RequestParam("userData") String userPassword, Model model) {
		HttpHeaders headers = new HttpHeaders();
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
		try {
			User user = SecurityLibrary.getLoggedInUser();
			if (!enc.matches(userPassword, user.getPassword())) {
				throw new ApplicationException("Password mismatch");
			}
			headers.add("success", "Password Matched");
		} catch (ApplicationException e) {
			LOG.error("Error during password Validation : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("error.password.mismatch", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<Void>(headers, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			LOG.error("Error during password Validation : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("error.while.validating.password", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Void>(headers, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/downloadEvalConclusionAttachment/{eventId}/{evalConUserId}", method = RequestMethod.GET)
	public void downloadFile(@PathVariable String eventId, @PathVariable String evalConUserId, HttpServletResponse response) throws IOException {
		try {

			byte[] data = null;
			String fileName = null;

			switch (eventType) {
			case RFA: {
				RfaEvaluationConclusionUser usr = rfaEventService.getEvaluationConclusionUserAttachment(eventId, evalConUserId);
				if (usr != null) {
					data = usr.getFileData();
					fileName = usr.getFileName();
				}
				break;
			}
			case RFI: {
				RfiEvaluationConclusionUser usr = rfiEventService.getEvaluationConclusionUserAttachment(eventId, evalConUserId);
				if (usr != null) {
					data = usr.getFileData();
					fileName = usr.getFileName();
				}
				break;
			}
			case RFP: {
				RfpEvaluationConclusionUser usr = rfpEventService.getEvaluationConclusionUserAttachment(eventId, evalConUserId);
				if (usr != null) {
					data = usr.getFileData();
					fileName = usr.getFileName();
				}
				break;
			}
			case RFQ: {
				RfqEvaluationConclusionUser usr = rfqEventService.getEvaluationConclusionUserAttachment(eventId, evalConUserId);
				if (usr != null) {
					data = usr.getFileData();
					fileName = usr.getFileName();
				}
				break;
			}
			case RFT: {
				RftEvaluationConclusionUser usr = rftEventService.getEvaluationConclusionUserAttachment(eventId, evalConUserId);
				if (usr != null) {
					data = usr.getFileData();
					fileName = usr.getFileName();
				}
				break;
			}
			default:
				break;
			}
			if (data != null) {
				// response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
				FileCopyUtils.copy(data, response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			LOG.error("Error while downloading Premature Evaluation Conclusion user attachment : " + e.getMessage(), e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}