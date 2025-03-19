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

import com.privasia.procurehere.core.entity.ApprovalDocument;
import com.privasia.procurehere.core.entity.RfsDocument;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SourcingFormRequestService;

/**
 * @author sudesha
 */
@Controller
@RequestMapping("/buyer")
public class ApprovalDocumentController {

	private static final Logger LOG = LogManager.getLogger(Global.RFS_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	SourcingFormRequestService requestService;

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

	@RequestMapping(value = "/approvalDocument", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<ApprovalDocument>> uploadApprovalDocuments(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, @RequestParam("formId") String formId) {
		LOG.info("APPROVALDOCUMENT");
		String fileName = null;
		HttpHeaders headers = new HttpHeaders();
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				LOG.info("FILE CONTENT" + bytes);
				ApprovalDocument approvalDocument = new ApprovalDocument();
				approvalDocument.setCredContentType(file.getContentType());
				approvalDocument.setDescription(desc);
				approvalDocument.setFileName(fileName);
				approvalDocument.setFileData(bytes);
				approvalDocument.setUploadDate(new Date());
				approvalDocument.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
				SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadApprovaldocuemntFormIdById(formId);
				approvalDocument.setSourcingFormRequest(sourcingFormRequest);
				sourcingFormRequestService.saveApprovalDocument(approvalDocument);
				List<ApprovalDocument> approvalDocsList = sourcingFormRequestService.findAllPlainApprovalDocsbyRfsId(formId);
				headers.add("success", messageSource.getMessage("rfs.fileUpload.success", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<ApprovalDocument>>(approvalDocsList, headers, HttpStatus.OK);
			} catch (Exception e) {
				headers.add("error", messageSource.getMessage("rfs.fileUpload.error", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<ApprovalDocument>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			headers.add("error", messageSource.getMessage("rfs.fileUpload.empty", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<ApprovalDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/updateApprovalDocumentDesc", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<ApprovalDocument>> updateApprovalDocumentDesc(@RequestParam("docId") String docId, @RequestParam("docDesc") String docDesc, @RequestParam("formId") String formId) {
		try {
			sourcingFormRequestService.updateApprovalDocumentDesc(docId, docDesc, formId);
			List<ApprovalDocument> approvalDocsList = sourcingFormRequestService.findAllPlainApprovalDocsbyRfsId(formId);
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "File Description updated successfully ");
			return new ResponseEntity<List<ApprovalDocument>>(approvalDocsList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("You failed to update File Description :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "You failed to update File Description :" + e.getMessage());
			return new ResponseEntity<List<ApprovalDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/downloadApprovalDocument/{docId}", method = RequestMethod.GET)
	public void downloadApprovalFile(@PathVariable String docId, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Approval Download  :: :: " + docId + "::::::");
			sourcingFormRequestService.downloadApprovalDocument(docId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading Approval Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/removeApprovalDocument", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<ApprovalDocument>> removeApprovalDocument(@RequestParam("removeDocId") String removeDocId, @RequestParam("formId") String formId) {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeDocId).length() > 0) {
				ApprovalDocument approvalDocument = sourcingFormRequestService.findApprovalDocById(removeDocId);
				sourcingFormRequestService.removeApprovalDocument(approvalDocument);
				List<ApprovalDocument> approvalDocsList = sourcingFormRequestService.findAllPlainApprovalDocsbyRfsId(formId);
				headers.add("success", messageSource.getMessage("rfs.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<ApprovalDocument>>(approvalDocsList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			headers.add("error", messageSource.getMessage("rfs.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<ApprovalDocument>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		headers.add("info", messageSource.getMessage("rfs.file.remove", new Object[] {}, Global.LOCALE));
		return new ResponseEntity<List<ApprovalDocument>>(null, headers, HttpStatus.OK);
	}

}