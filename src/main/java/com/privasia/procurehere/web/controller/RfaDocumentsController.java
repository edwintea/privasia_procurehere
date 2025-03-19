package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.RfiEventDocument;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.RfxTemplateDocument;
import com.privasia.procurehere.service.RfxTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventDocument;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaDocumentService;
import com.privasia.procurehere.service.RfaEventService;

@Controller
@RequestMapping("/buyer/RFA")
public class RfaDocumentsController extends EventDocumentBase {

	@Resource
	MessageSource messageSource;

	@Autowired
	RfaDocumentService rfaDocumentService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());

	}

	public RfaDocumentsController() {
		super(RfxTypes.RFA);
	}

	@RequestMapping(path = "/eventDescriptionPrevious", method = RequestMethod.POST)
	public String auctionRulesPrevious(@ModelAttribute("event") RfaEvent rfaEvent, Model model, BindingResult result) {
		rfaEvent = rfaEventService.getRfaEventByeventId(rfaEvent.getId());
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
		return "redirect:auctionRules/" + rfaEvent.getId();
	}

	/**
	 * @param eventId
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/createEventDocuments/{eventId}", method = RequestMethod.GET)
	public String uploadRfaDocuments(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		if(rfaEvent.getTemplate() != null && rfaEvent.getDocumentReq()) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateById(rfaEvent.getTemplate().getId());
			if (rfxTemplate.getDocuments() != null && !rfaEvent.getDocumentCompleted()) {
				for (RfxTemplateDocument templateDocument : rfxTemplate.getDocuments()) {
					RfaEventDocument doc = new RfaEventDocument();
					doc.setCredContentType(templateDocument.getCredContentType());
					doc.setDescription(templateDocument.getDescription());
					doc.setFileName(templateDocument.getFileName());
					doc.setFileData(templateDocument.getFileData());
					doc.setUploadDate(templateDocument.getUploadDate());
					doc.setInternal(templateDocument.getInternal());
					doc.setUploadBy(templateDocument.getUploadBy());
					doc.setFileSizeInKb(templateDocument.getFileSizeInKb());
					doc.setRfxEvent(rfaEvent);
					rfaDocumentService.saveRfaDocuments(doc);
				}
				rfaEvent.setDocumentCompleted(true);
				rfaEventService.updateRfaEvent(rfaEvent);
			}
		}
		model.addAttribute("event", rfaEvent);
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		List<RfaEventDocument> rfaDocsList = rfaDocumentService.findAllRfaEventdocsbyEventId(eventId);
		model.addAttribute("rftDocuments", rfaDocsList);
		return "createEventDocuments";
	}

	/**
	 * @param file
	 * @param desc
	 * @return
	 */
	@RequestMapping(value = "/createEventDocuments", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaEventDocument>> uploadRfaDocument(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, @RequestParam("eventId") String eventId, Boolean internal) {
		String fileName = null;
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				if (bytes != null) {
					LOG.info("FILE CONTENT Size : " + bytes.length);
				}
				RfaEventDocument rfaDocument = new RfaEventDocument();
				rfaDocument.setCredContentType(file.getContentType());
				rfaDocument.setDescription(desc);
				rfaDocument.setFileName(fileName);
				rfaDocument.setFileData(bytes);
				rfaDocument.setUploadDate(new Date());
				rfaDocument.setInternal(internal);
				rfaDocument.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
				rfaDocument.setUploadBy(SecurityLibrary.getLoggedInUser());
				RfaEvent rfaEvents = rfaEventService.getRfaEventByeventId(eventId);
				rfaDocument.setRfxEvent(rfaEvents);
				LOG.info("Saving document...");
				rfaDocumentService.saveRfaDocuments(rfaDocument);
				LOG.info("Document saved...");
				List<RfaEventDocument> rftDocsList = rfaDocumentService.findAllRfaEventdocsbyEventId(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", "File " + fileName + " upload successfully ");
				return new ResponseEntity<List<RfaEventDocument>>(rftDocsList, headers, HttpStatus.OK);
			} catch (Exception e) {
				LOG.error("You failed to upload " + fileName + ": " + e.getMessage(), e);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "You failed to upload " + fileName);
				return new ResponseEntity<List<RfaEventDocument>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		}
		return null;

	}

	@RequestMapping(value = "/updateEventDocumentDesc", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaEventDocument>> updateEventDocumentDesc(@RequestParam("docId") String docId, @RequestParam("docDesc") String docDesc, @RequestParam("eventId") String eventId, @RequestParam("internal") Boolean internal) {
		LOG.info("====docId=====" + docId + "======docDesc======" + docDesc);
		try {
			rfaDocumentService.updateEventDocumentDesc(docId, docDesc, eventId, internal);
			List<RfaEventDocument> rfaDocsList = rfaDocumentService.findAllRfaEventdocsbyEventId(eventId);
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "File Description updated successfully ");
			return new ResponseEntity<List<RfaEventDocument>>(rfaDocsList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("You failed to update File Description :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "You failed to update File Description :" + e.getMessage());
			return new ResponseEntity<List<RfaEventDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/eventDocumentNext", method = RequestMethod.POST)
	public ModelAndView eventDocumentNext(@RequestParam("eventId") String eventId, RedirectAttributes redir, Model model) {
		RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
		if (event != null) {
			event.setDocumentCompleted(Boolean.TRUE);
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			rfaEventService.updateRfaEvent(event);
			return eventDocumentNext(event);
		} else {
			return new ModelAndView("redirect:login");
		}

	}

	@RequestMapping(value = "/removeRftDocument", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RfaEventDocument>> removeRfaDocument(@RequestParam("removeDocId") String removeDocId, @RequestParam("eventId") String eventId) {
		try {
			if (StringUtils.checkString(removeDocId).length() > 0) {
				rfaDocumentService.removeRfaDocument(removeDocId);
				List<RfaEventDocument> rftDocsList = rfaDocumentService.findAllRfaEventdocsbyEventId(eventId);

				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfaEventDocument>>(rftDocsList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while Error while removing Other Credential : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfaEventDocument>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("success", messageSource.getMessage("rft.file.remove", new Object[] {}, Global.LOCALE));
		return new ResponseEntity<List<RfaEventDocument>>(null, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/downloadRftDocument/{id}", method = RequestMethod.GET)
	public void downloadRftFile(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("RFA Event Download  :: :: " + id + "::::::");
			rfaEventService.downloadEventDocument(id, response);
		} catch (Exception e) {
			LOG.error("Error while downloaded RFA event Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/documentSaveDraft", method = RequestMethod.POST)
	public String documentSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId, Model model) {
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rfaEvent.getEventName() != null ? rfaEvent.getEventName() : rfaEvent.getEventId()) }, Global.LOCALE));
		return "redirect:createEventDocuments/" + rfaEvent.getId();

	}

}