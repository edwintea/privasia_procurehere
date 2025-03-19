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
import org.springframework.ui.Model;
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

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.RequestAudit;
import com.privasia.procurehere.core.entity.RfsDocument;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormTeamMember;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.RequestAuditType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.RfsDocumentPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author sudesha
 */
@Controller
@RequestMapping("/buyer")
public class RfsDocumentController {

	private static final Logger LOG = LogManager.getLogger(Global.RFS_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	SourcingFormRequestService requestService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

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

	@RequestMapping(value = "/rfsDocumentNext/{formId}", method = RequestMethod.GET)
	public String documentNext(@PathVariable String formId) {
		SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.getSourcingRequestById(formId);
		if (sourcingFormRequest != null) {
			sourcingFormRequest.setDocumentCompleted(Boolean.TRUE);
			sourcingFormRequestService.update(sourcingFormRequest);
			LOG.info("RFS documentCompleted updated");
			return "redirect:/buyer/sourcingFormRequestCqList/" + formId;
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(path = "/rfsDocument/{formId}", method = RequestMethod.GET)
	public String rfsDocument(@PathVariable String formId, Model model) {
		LOG.info("create RFS Document GET called Rfs id :" + formId);

		SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormIdById(formId);
		for (RfsDocument document : sourcingFormRequest.getRfsDocuments()) {
			SourcingFormTeamMember sourcingFormTeamMember = sourcingFormRequestService.getTeamMemberByUserIdAndFormId(formId, document.getUploadBy().getId());
			if (sourcingFormTeamMember != null) {
				String uploadedBy = (document.getUploadBy() != null ? document.getUploadBy().getId() : "");
				if (sourcingFormTeamMember.getTeamMemberType() == TeamMemberType.Editor && uploadedBy.equals(SecurityLibrary.getLoggedInUser().getId())) {
					document.setEditorMember(true);
				}
			}
		}
		model.addAttribute("sourcingFormRequest", sourcingFormRequest);
		model.addAttribute("eventPermissions", requestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), formId));
		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		RequestAudit audit = new RequestAudit();
		model.addAttribute("audit", audit); 
		return "rfsDocument";
	}

	@RequestMapping(value = "/rfsDocument", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfsDocumentPojo>> uploadRfsDocuments(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, @RequestParam("formId") String formId,  Boolean internal) {
		String fileName = null;
		HttpHeaders headers = new HttpHeaders();
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				LOG.info("FILE CONTENT" + bytes);
				RfsDocument rfsDocument = new RfsDocument();
				rfsDocument.setCredContentType(file.getContentType());
				rfsDocument.setDescription(desc);
				rfsDocument.setFileName(fileName);
				rfsDocument.setFileData(bytes);
				rfsDocument.setUploadDate(new Date());
				rfsDocument.setInternal(internal);
				rfsDocument.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
				SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormIdById(formId);
				rfsDocument.setSourcingFormRequest(sourcingFormRequest);
				rfsDocument.setUploadBy(SecurityLibrary.getLoggedInUser());
				sourcingFormRequestService.saveRfsDocument(rfsDocument);

				EventPermissions permission = requestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), formId);
				List<RfsDocumentPojo> rfsDocsList = sourcingFormRequestService.findAllPlainRfsDocsbyRfsIdAndUploadBy(formId);

				for (RfsDocumentPojo document : rfsDocsList) {
					if (permission.isApprover()) {
						document.setApproverMember(true);
					}
					if (permission.isEditor()) {
						document.setLoggedInMember(true);
						SourcingFormTeamMember sourcingFormTeamMember = sourcingFormRequestService.getTeamMemberByUserIdAndFormId(formId, document.getUploadById());
						if (sourcingFormTeamMember != null) {
							String uploadedBy = (document.getUploadById() != null ? document.getUploadById() : "");
							if (sourcingFormTeamMember.getTeamMemberType() == TeamMemberType.Editor && uploadedBy.equals(SecurityLibrary.getLoggedInUser().getId())) {
								document.setEditorMember(true);
							}
						}
					}
				}

				headers.add("success", messageSource.getMessage("rfs.fileUpload.success", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<RfsDocumentPojo>>(rfsDocsList, headers, HttpStatus.OK);
			} catch (Exception e) {
				headers.add("error", messageSource.getMessage("rfs.fileUpload.error", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<RfsDocumentPojo>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			headers.add("error", messageSource.getMessage("rfs.fileUpload.empty", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<RfsDocumentPojo>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/updateRfsDocumentDesc", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfsDocumentPojo>> updateRfsDocumentDesc(@RequestParam("docId") String docId, @RequestParam("docDesc") String docDesc, @RequestParam("formId") String formId, @RequestParam("internal") Boolean internal) {
		try {
			sourcingFormRequestService.updateRfsDocumentDesc(docId, docDesc, formId, internal);

			String fileName = sourcingFormRequestService.findUploadFileName(docId);
			SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.findById(formId);

			if (sourcingFormRequest != null) {
				try {
					RequestAudit audit = new RequestAudit();
					audit.setActionDate(new Date());
					audit.setAction(RequestAuditType.UPDATE);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					audit.setReq(sourcingFormRequest);
					audit.setDescription("Document " + "\"" + fileName + "\"" + " has been updated");
					audit = sourcingFormRequestService.saveAudit(audit);
				} catch (Exception e) {
					LOG.info("Error saving audit details: " + e.getMessage());
				}
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Document " + "\"" + fileName + "\"" + " has been updated", sourcingFormRequest.getCreatedBy().getTenantId(), sourcingFormRequest.getCreatedBy(), new Date(), ModuleType.RFS);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			}

			EventPermissions permission = requestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), formId);
			List<RfsDocumentPojo> rfsDocsList = sourcingFormRequestService.findAllPlainRfsDocsbyRfsIdAndUploadBy(formId);
			for (RfsDocumentPojo document : rfsDocsList) {
				if (permission.isEditor()) {
					document.setLoggedInMember(true);
					SourcingFormTeamMember sourcingFormTeamMember = sourcingFormRequestService.getTeamMemberByUserIdAndFormId(formId, document.getUploadById());
					if (sourcingFormTeamMember != null) {
						String uploadedBy = (document.getUploadById() != null ? document.getUploadById() : "");
						if (sourcingFormTeamMember.getTeamMemberType() == TeamMemberType.Editor && uploadedBy.equals(SecurityLibrary.getLoggedInUser().getId())) {
							document.setEditorMember(true);
						}
					}
				}
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "File Description updated successfully ");
			return new ResponseEntity<List<RfsDocumentPojo>>(rfsDocsList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("You failed to update File Description :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "You failed to update File Description :" + e.getMessage());
			return new ResponseEntity<List<RfsDocumentPojo>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/downloadRfsDocument/{docId}", method = RequestMethod.GET)
	public void downloadRfsFile(@PathVariable String docId, HttpServletResponse response) throws IOException {
		try {
			sourcingFormRequestService.downloadRfsDocument(docId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading Rfs Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/removeRfsDocument", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RfsDocumentPojo>> removeRfsDocument(@RequestParam("removeDocId") String removeDocId, @RequestParam("formId") String formId, RedirectAttributes redirectAttributes) {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeDocId).length() > 0) {
				RfsDocument rfsDocument = sourcingFormRequestService.findRfsDocById(removeDocId);
				String fileName = sourcingFormRequestService.findUploadFileName(removeDocId);
				sourcingFormRequestService.removeRfsDocument(rfsDocument);

				SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.findById(formId);
				if (sourcingFormRequest != null) {
					try {
						RequestAudit audit = new RequestAudit();
						audit.setActionDate(new Date());
						audit.setAction(RequestAuditType.DELETE);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
						audit.setReq(sourcingFormRequest);
						audit.setDescription("Document " + "\"" + fileName + "\"" + " has been deleted");
						audit = sourcingFormRequestService.saveAudit(audit);
					} catch (Exception e) {
						LOG.info("Error saving audit details: " + e.getMessage());
					}
				}

				EventPermissions permission = requestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), formId);
				List<RfsDocumentPojo> rfsDocsList = sourcingFormRequestService.findAllPlainRfsDocsbyRfsIdAndUploadBy(formId);
				for (RfsDocumentPojo document : rfsDocsList) {
					if (permission.isEditor()) {
						document.setLoggedInMember(true);
						SourcingFormTeamMember sourcingFormTeamMember = sourcingFormRequestService.getTeamMemberByUserIdAndFormId(formId, document.getUploadById());
						if (sourcingFormTeamMember != null) {
							String uploadedBy = (document.getUploadById() != null ? document.getUploadById() : "");
							if (sourcingFormTeamMember.getTeamMemberType() == TeamMemberType.Editor && uploadedBy.equals(SecurityLibrary.getLoggedInUser().getId())) {
								document.setEditorMember(true);
							}
						}
					}
				}

				headers.add("success", messageSource.getMessage("rfs.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfsDocumentPojo>>(rfsDocsList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			headers.add("error", messageSource.getMessage("rfs.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfsDocumentPojo>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		headers.add("info", messageSource.getMessage("rfs.file.remove", new Object[] {}, Global.LOCALE));
		return new ResponseEntity<List<RfsDocumentPojo>>(null, headers, HttpStatus.OK);
	}

}