package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

import com.privasia.procurehere.core.entity.AdditionalDocument;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.enums.DocumentReferenceType;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.EventAdditionalDocumentService;
import com.privasia.procurehere.service.RftEventService;
import org.springframework.ui.Model;

/**
 * @author sudesha
 */
@Controller
@RequestMapping("/buyer")
public class EventAdditionalDocumentsController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	EventAdditionalDocumentService eventAdditionlDocumentService;

	@Autowired
	BuyerService buyerService;

	@ModelAttribute("step")
	public String getStep() {
		return "2";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());

	}

	@RequestMapping(value = "/eventAdditionalDocument", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<AdditionalDocument>> uploadAdditionalDocuments(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, @RequestParam("eventId") String eventId, Model model) {
		String fileName = null;
		HttpHeaders headers = new HttpHeaders();
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				AdditionalDocument rftDocument = new AdditionalDocument();
				rftDocument.setCredContentType(file.getContentType());
				rftDocument.setDescription(desc);
				rftDocument.setFileName(fileName);
				rftDocument.setFileData(bytes);
				rftDocument.setUploadDate(new Date());
				rftDocument.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
				rftDocument.setDocumentReferenceType(DocumentReferenceType.SUMMARY);
				rftDocument.setRftEvent(new RftEvent(eventId));

				eventAdditionlDocumentService.saveEventAdditionalDocuments(rftDocument);
			
				List<AdditionalDocument> rftDocsList = eventAdditionlDocumentService.findAllRftEventdocsbyEventId(eventId);
				headers.add("success", "File " + fileName + " upload successfully ");
				return new ResponseEntity<List<AdditionalDocument>>(rftDocsList, headers, HttpStatus.OK);
			} catch (Exception e) {
				LOG.error("You failed to upload " + fileName + ": " + e.getMessage(), e);
				headers.add("error", "You failed to upload " + fileName);
				return new ResponseEntity<List<AdditionalDocument>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			headers.add("error", messageSource.getMessage("event.fileUpload.empty", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<AdditionalDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/updateEventAdditionalDocumentDesc", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<AdditionalDocument>> updateEventAdditionalDocumentDesc(@RequestParam("docId") String docId, @RequestParam("docDesc") String docDesc, @RequestParam("eventId") String eventId) {
		try {
			eventAdditionlDocumentService.updateEventAdditionalDocumentDesc(docId, docDesc, eventId);
			List<AdditionalDocument> approvalDocsList = eventAdditionlDocumentService.findAllRftEventdocsbyEventId(eventId);
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "File Description updated successfully ");
			return new ResponseEntity<List<AdditionalDocument>>(approvalDocsList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("You failed to update File Description :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "You failed to update File Description :" + e.getMessage());
			return new ResponseEntity<List<AdditionalDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/downloadEventAdditionalDocument/{docId}", method = RequestMethod.GET)
	public void downloadEventAdditionalDocuFile(@PathVariable String docId, HttpServletResponse response) throws IOException {
		try {
			eventAdditionlDocumentService.downloadAdditionalDocument(docId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading Approval Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/removeEventAdditionalDocument", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<AdditionalDocument>> removeApprovalDocument(@RequestParam("removeDocId") String removeDocId, @RequestParam("eventId") String eventId) {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeDocId).length() > 0) {
				AdditionalDocument approvalDocument = eventAdditionlDocumentService.findAdditionalDocById(removeDocId);
				eventAdditionlDocumentService.removeAdditionalDocument(approvalDocument);
				List<AdditionalDocument> approvalDocsList = eventAdditionlDocumentService.findAllRftEventdocsbyEventId(eventId);
				headers.add("success", messageSource.getMessage("event.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<AdditionalDocument>>(approvalDocsList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			headers.add("error", messageSource.getMessage("event.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<AdditionalDocument>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		headers.add("info", messageSource.getMessage("event.file.remove", new Object[] {}, Global.LOCALE));
		return new ResponseEntity<List<AdditionalDocument>>(null, headers, HttpStatus.OK);
	}

}