package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.PoDocument;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrDocument;
import com.privasia.procurehere.core.enums.PoDocumentType;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.PrService;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PrDocumentController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	PrService prService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

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

	@RequestMapping(value = "/documentNext/{prId}", method = RequestMethod.GET)
	public String documentNext(@PathVariable String prId) {
		LOG.info("prId : " + prId);
		Pr pr = prService.getPrById(prId);
		if (pr != null) {
			pr.setDocumentCompleted(Boolean.TRUE);
			prService.updatePr(pr);
			LOG.info("pr documentCompleted updated");
			return "redirect:/buyer/prDelivery/" + prId;
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(path = "/prDocument/{prId}", method = RequestMethod.GET)
	public String prDocument(@PathVariable String prId, Model model) {
		LOG.info("create pr Document GET called pr id :" + prId);
		Pr pr = prService.getLoadedPrById(prId);
		model.addAttribute("pr", pr);
		model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId));
		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		return "prDocument";
	}

	/**
	 * @param file
	 * @param desc
	 * @return
	 */
	@RequestMapping(value = "/prDocument", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PrDocument>> uploadprDocuments(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, @RequestParam("prId") String prId) {
		String fileName = null;
		HttpHeaders headers = new HttpHeaders();
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				LOG.info("FILE CONTENT" + bytes);
				PrDocument prDocument = new PrDocument();
				prDocument.setCredContentType(file.getContentType());
				String decodedDesc2 = "";
				if(desc != null && !desc.isEmpty()) {
					decodedDesc2 = new String(desc.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
				}
				prDocument.setDescription(desc != null ? prService.replaceSmartQuotes(decodedDesc2) : decodedDesc2);
				prDocument.setFileName(fileName);
				prDocument.setFileData(bytes);
				prDocument.setUploadDate(new Date());
				prDocument.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
				Pr pr = prService.getLoadedPrById(prId);
				prDocument.setPr(pr);
				prService.savePrDocument(prDocument);
				List<PrDocument> prDocsList = prService.findAllPlainPrDocsbyPrId(prId);
				headers.add("success", messageSource.getMessage("pr.fileUpload.success", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<PrDocument>>(prDocsList, headers, HttpStatus.OK);
			} catch (Exception e) {
				headers.add("error", messageSource.getMessage("pr.fileUpload.error", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<PrDocument>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			headers.add("error", messageSource.getMessage("pr.fileUpload.empty", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<PrDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/updateEventDocumentDesc", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PrDocument>> updateEventDocumentDesc(@RequestParam("docId") String docId, @RequestParam("docDesc") String docDesc, @RequestParam("prId") String prId) {
		LOG.info("====docId=====" + docId + "======docDesc======" + docDesc);
		try {
			prService.updateEventDocumentDesc(docId, docDesc, prId);
			List<PrDocument> rftDocsList = prService.findAllPlainPrDocsbyPrId(prId);
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "File Description updated successfully ");
			return new ResponseEntity<List<PrDocument>>(rftDocsList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("You failed to update File Description :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "You failed to update File Description :" + e.getMessage());
			return new ResponseEntity<List<PrDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/downloadPrDocument/{docId}", method = RequestMethod.GET)
	public void downloadPrFile(@PathVariable String docId, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Pr Download  :: :: " + docId + "::::::");
			prService.downloadPrDocument(docId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading Pr Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/removePrDocument", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<PrDocument>> removePrDocument(@RequestParam("removeDocId") String removeDocId, @RequestParam("prId") String prId) {
		LOG.info("Pr remove Doc Id  :: :: " + removeDocId);
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeDocId).length() > 0) {
				PrDocument prDocument = prService.findPrDocById(removeDocId);
				prService.removePrDocument(prDocument);
				List<PrDocument> prDocsList = prService.findAllPlainPrDocsbyPrId(prId);
				headers.add("success", messageSource.getMessage("pr.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<PrDocument>>(prDocsList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			headers.add("error", messageSource.getMessage("pr.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<PrDocument>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		headers.add("info", messageSource.getMessage("pr.file.remove", new Object[] {}, Global.LOCALE));
		return new ResponseEntity<List<PrDocument>>(null, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/savePrDraft/{prId}", method = RequestMethod.GET)
	public ModelAndView savePrDraft(@PathVariable String prId, RedirectAttributes redir) {
		LOG.info("pr savePrDraft called..");
		try {
			if (StringUtils.checkString(prId).length() > 0) {
				Pr persistObj = prService.getLoadedPrById(prId);
				persistObj.setStatus(PrStatus.DRAFT);
				prService.updatePr(persistObj);
				LOG.info("pr status Updated succesfully");
				redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[] { persistObj.getPrId() }, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while save draft :" + e.getMessage(), e);
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	/**
	 * @param file
	 * @param desc
	 * @return
	 */
	@RequestMapping(value = "/poDocument", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PoDocument>> uploadPoDocuments(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, @RequestParam("prId") String prId, @RequestParam("docType") String docType) {
		LOG.info("upload poDocument called prId :" + prId);
		String fileName = null;
		HttpHeaders headers = new HttpHeaders();
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				// LOG.info("FILE CONTENT" + bytes);
				PoDocument poDocument = new PoDocument();
				poDocument.setCredContentType(file.getContentType());
				poDocument.setDescription(desc);
				poDocument.setFileName(fileName);
				poDocument.setFileData(bytes);
				poDocument.setUploadDate(new Date());

				if (StringUtils.checkString(docType).length() > 0) {
					poDocument.setDocType(PoDocumentType.valueOf(docType));
				} else {
					poDocument.setDocType(PoDocumentType.OTHER);
				}
				poDocument.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
				Pr pr = new Pr();
				pr.setId(prId);
				poDocument.setPr(pr);
				prService.savePoDocument(poDocument);
				List<PoDocument> poDocsList = prService.findAllPlainPoDocsbyPrId(prId);
				headers.add("success", messageSource.getMessage("pr.fileUpload.success", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<PoDocument>>(poDocsList, headers, HttpStatus.OK);
			} catch (Exception e) {
				headers.add("error", messageSource.getMessage("pr.fileUpload.error", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<PoDocument>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			headers.add("error", messageSource.getMessage("pr.fileUpload.empty", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<PoDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/removePoDocument", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<PoDocument>> removePoDocument(@RequestParam("removeDocId") String removeDocId, @RequestParam("prId") String prId) {
		LOG.info("Po remove Doc Id  :: :: " + removeDocId);
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeDocId).length() > 0) {
				PoDocument poDocument = prService.findPoDocById(removeDocId);
				prService.removePoDocument(poDocument);
				List<PoDocument> poDocsList = prService.findAllPlainPoDocsbyPrId(prId);
				headers.add("success", messageSource.getMessage("pr.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<PoDocument>>(poDocsList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			headers.add("error", messageSource.getMessage("pr.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<PoDocument>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		headers.add("info", messageSource.getMessage("pr.file.remove", new Object[] {}, Global.LOCALE));
		return new ResponseEntity<List<PoDocument>>(null, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/downloadPoDocument/{docId}", method = RequestMethod.GET)
	public void downloadPoDocument(@PathVariable String docId, HttpServletResponse response) throws IOException {
		try {
			LOG.info("PO Download  :: :: " + docId + "::::::");
			prService.downloadPoDocument(docId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading PO Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/updatePoDocumentDesc", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PoDocument>> updatePoDocumentDesc(@RequestParam("docId") String docId, @RequestParam("docDesc") String docDesc, @RequestParam("prId") String prId, @RequestParam("docType") String docType) {
		LOG.info("====docId=====" + docId + "======docDesc======" + docDesc + "======docType======" + docType);
		try {
			prService.updatePoDocumentDesc(docId, docDesc, prId, docType);
			List<PoDocument> poDocsList = prService.findAllPlainPoDocsbyPrId(prId);
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "File Description updated successfully ");
			return new ResponseEntity<List<PoDocument>>(poDocsList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("You failed to update File Description :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "You failed to update File Description :" + e.getMessage());
			return new ResponseEntity<List<PoDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}

	}
}