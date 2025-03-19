package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.RfiEventDocument;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.RfxTemplateDocument;
import com.privasia.procurehere.service.RfxTemplateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventDocument;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfpEventService;

@Controller
@RequestMapping("/buyer/RFP")
public class RfpDocumentsController extends EventDocumentBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfxTemplateService rfxTemplateService;

	public RfpDocumentsController() {
		super(RfxTypes.RFP);
	}

	@RequestMapping(path = "/eventDescriptionPrevious", method = RequestMethod.POST)
	public String eventDescriptionPrevious(@ModelAttribute RfpEvent rftEvent, Model model, BindingResult result) {
		LOG.info("Previous called in Documents : " + rftEvent.getId());
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));

		rftEvent = rfpDocumentService.getEventById(rftEvent.getId());
		return "redirect:eventDescription/" + rftEvent.getId();
	}

	/**
	 * @param eventId
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/createEventDocuments/{eventId}", method = RequestMethod.GET)
	public String uploadRftDocuments(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		RfpEvent rftEvent = rfpEventService.getEventById(eventId);
		if(rftEvent.getTemplate() != null && rftEvent.getDocumentReq()) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateById(rftEvent.getTemplate().getId());
			if (rfxTemplate.getDocuments() != null && !rftEvent.getDocumentCompleted()) {
				for (RfxTemplateDocument templateDocument : rfxTemplate.getDocuments()) {
					RfpEventDocument doc = new RfpEventDocument();
					doc.setCredContentType(templateDocument.getCredContentType());
					doc.setDescription(templateDocument.getDescription());
					doc.setFileName(templateDocument.getFileName());
					doc.setFileData(templateDocument.getFileData());
					doc.setUploadDate(templateDocument.getUploadDate());
					doc.setInternal(templateDocument.getInternal());
					doc.setUploadBy(templateDocument.getUploadBy());
					doc.setFileSizeInKb(templateDocument.getFileSizeInKb());
					doc.setRfxEvent(rftEvent);
					rfpDocumentService.saveDocuments(doc);
				}
				rftEvent.setDocumentCompleted(true);
				rfpEventService.updateEvent(rftEvent);
			}
		}
		model.addAttribute("event", rftEvent);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

		List<RfpEventDocument> rftDocsList = rfpDocumentService.findAllEventdocsbyEventId(eventId);
		model.addAttribute("rftDocuments", rftDocsList);
		return "createEventDocuments";
	}

	/**
	 * @param file
	 * @param desc
	 * @return
	 */
	@RequestMapping(value = "/createEventDocuments", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpEventDocument>> uploadRftDocument(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, @RequestParam("eventId") String eventId, Boolean internal) {
		String fileName = null;
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				LOG.info("FILE CONTENT" + bytes);
				RfpEventDocument rfpDocument = new RfpEventDocument();
				rfpDocument.setCredContentType(file.getContentType());
				rfpDocument.setDescription(desc);
				rfpDocument.setFileName(fileName);
				rfpDocument.setFileData(bytes);
				rfpDocument.setUploadDate(new Date());
				rfpDocument.setInternal(internal);
				rfpDocument.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
				rfpDocument.setUploadBy(SecurityLibrary.getLoggedInUser());
				RfpEvent rftEvents = rfpDocumentService.getEventById(eventId);
				rfpDocument.setRfxEvent(rftEvents);

				rfpDocumentService.saveDocuments(rfpDocument);

				List<RfpEventDocument> rftDocsList = rfpDocumentService.findAllEventdocsbyEventId(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", "File " + fileName + " upload successfully ");
				return new ResponseEntity<List<RfpEventDocument>>(rftDocsList, headers, HttpStatus.OK);
			} catch (Exception e) {
				LOG.error("You failed to upload " + fileName + ": " + e.getMessage(), e);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "You failed to upload " + fileName);
				return new ResponseEntity<List<RfpEventDocument>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		}
		return null;

	}

	@RequestMapping(value = "/updateEventDocumentDesc", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpEventDocument>> updateEventDocumentDesc(@RequestParam("docId") String docId, @RequestParam("docDesc") String docDesc, @RequestParam("eventId") String eventId, @RequestParam("internal") Boolean internal) {
		LOG.info("====docId=====" + docId + "======docDesc======" + docDesc);
		try {
			rfpDocumentService.updateEventDocumentDesc(docId, docDesc, eventId, internal);
			List<RfpEventDocument> rfpDocsList = rfpDocumentService.findAllEventdocsbyEventId(eventId);
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "File Description updated successfully ");
			return new ResponseEntity<List<RfpEventDocument>>(rfpDocsList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("You failed to update File Description :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "You failed to update File Description :" + e.getMessage());
			return new ResponseEntity<List<RfpEventDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/eventDocumentNext", method = RequestMethod.POST)
	public ModelAndView eventDocumentNext(@RequestParam("eventId") String eventId, Model model) {
		LOG.info("EVENT ID IN DOCUMENT NEXT :" + eventId);
		RfpEvent event = rfpDocumentService.getEventById(eventId);
		if (event != null) {
			event.setDocumentCompleted(Boolean.TRUE);
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

			rfpEventService.updateEvent(event);
			return eventDocumentNext(event);
		} else {
			return new ModelAndView("redirect:login");
		}
	}

	@RequestMapping(value = "/removeRftDocument", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RfpEventDocument>> removeRftDocument(@RequestParam("removeDocId") String removeDocId, @RequestParam("eventId") String eventId) {
		try {
			if (StringUtils.checkString(removeDocId).length() > 0) {
				rfpDocumentService.removeDocument(removeDocId);
				List<RfpEventDocument> rftDocsList = rfpDocumentService.findAllEventdocsbyEventId(eventId);

				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpEventDocument>>(rftDocsList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while Error while removing Other Credential : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfpEventDocument>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("success", messageSource.getMessage("rft.file.remove", new Object[] {}, Global.LOCALE));
		return new ResponseEntity<List<RfpEventDocument>>(null, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/downloadRftDocument/{id}", method = RequestMethod.GET)
	public void downloadRftFile(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("RFT Event Download  :: :: " + id + "::::::");
			rfpEventService.downloadEventDocument(id, response);
		} catch (Exception e) {
			LOG.error("Error while downloaded RFP event Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/documentSaveDraft", method = RequestMethod.POST)
	public String documentSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId, Model model) {
		RfpEvent rftEvent = rfpDocumentService.getEventById(eventId);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
		return "redirect:createEventDocuments/" + rftEvent.getId();

	}

}