package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.service.PrService;
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

import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PurchaseOrderDocument;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.PoService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author ravi
 */
@Controller
@RequestMapping("/buyer/po")
public class PoDocumentController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	PoService poService;

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

	@RequestMapping(value = "/poDocumentNext/{poId}", method = RequestMethod.GET)
	public String poDocumentNext(@PathVariable String poId,@RequestParam("prId") String prId) {
		LOG.info("poId : " + poId);
		Po po = poService.findPoById(poId);
		if (po != null) {
			po.setDocumentCompleted(Boolean.TRUE);
			poService.updatePo(po);
			LOG.info("po documentCompleted updated");
			return "redirect:/buyer/poDelivery/" + poId+"?prId="+prId;
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(path = "/poDocument/{poId}", method = RequestMethod.GET)
	public String prDocument(@PathVariable String poId, Model model) {
		LOG.info("create po Document GET called po id :" + poId);
		Po po = poService.getLoadedPoById(poId);
		model.addAttribute("po", po);
		model.addAttribute("eventPermissions", poService.getUserPemissionsForPo(SecurityLibrary.getLoggedInUser().getId(), poId));
		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		return "purchaseOrderDocument";
	}

	@RequestMapping(path = "/poDocumentView/{poId}", method = RequestMethod.GET)
	public String poDocumentView(@PathVariable String poId, @RequestParam(required=false) String prId,Model model) {
		LOG.info("create po Document GET called po id :" + poId+" from prId : "+prId);
		Po po = poService.getLoadedPoById(poId);
		model.addAttribute("po", po);

		Pr pr = prService.getLoadedPrById(prId);
		model.addAttribute("pr", pr);

		List<PurchaseOrderDocument> poDocsList = poService.findAllPlainPoDocsbyPoId(poId);
		model.addAttribute("poDocsList", poDocsList);

		model.addAttribute("eventPermissions", poService.getUserPemissionsForPo(SecurityLibrary.getLoggedInUser().getId(), poId));
		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		return "poDocumentView";
	}

	/**
	 * @param file
	 * @param desc
	 * @return
	 */
	@RequestMapping(value = "/poDocument", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PurchaseOrderDocument>> uploadpoDocuments(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, @RequestParam("poId") String poId, @RequestParam("internal") Boolean internal) {
		String fileName = null;
		HttpHeaders headers = new HttpHeaders();
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				LOG.info("FILE CONTENT" + bytes);
				PurchaseOrderDocument prDocument = new PurchaseOrderDocument();
				prDocument.setCredContentType(file.getContentType());
				prDocument.setDescription(desc);
				prDocument.setFileName(fileName);
				prDocument.setFileData(bytes);
				prDocument.setUploadDate(new Date());
				prDocument.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
				prDocument.setInternal(internal);
				Po pr = poService.getLoadedPoById(poId);
				prDocument.setPo(pr);
				poService.savePoDocument(prDocument);
				List<PurchaseOrderDocument> prDocsList = poService.findAllPlainPoDocsbyPoId(poId);
				headers.add("success", messageSource.getMessage("pr.fileUpload.success", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<PurchaseOrderDocument>>(prDocsList, headers, HttpStatus.OK);
			} catch (Exception e) {
				headers.add("error", messageSource.getMessage("pr.fileUpload.error", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<PurchaseOrderDocument>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			headers.add("error", messageSource.getMessage("pr.fileUpload.empty", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<PurchaseOrderDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/updatePoDocumentDesc", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PurchaseOrderDocument>> updatePoDocumentDesc(@RequestParam("docId") String docId, @RequestParam("docDesc") String docDesc, @RequestParam("poId") String poId, @RequestParam("internal") boolean internal) {
		try {
			LOG.info("internal  :  " + internal);
			poService.updatePoDocumentDesc(docId, docDesc, poId, internal);
			List<PurchaseOrderDocument> rftDocsList = poService.findAllPlainPoDocsbyPoId(poId);
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "File Description updated successfully ");
			return new ResponseEntity<List<PurchaseOrderDocument>>(rftDocsList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("You failed to update File Description :" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "You failed to update File Description :" + e.getMessage());
			return new ResponseEntity<List<PurchaseOrderDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/downloadPoDocument/{docId}", method = RequestMethod.GET)
	public void downloadPoFile(@PathVariable String docId, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Po Document Download  :: :: " + docId + "::::::");
			poService.downloadPoDocument(docId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading Pr Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/removePoDocument", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<PurchaseOrderDocument>> removePrDocument(@RequestParam("removeDocId") String removeDocId, @RequestParam("poId") String poId) {
		LOG.info("Po remove Doc Id  :: :: " + removeDocId);
		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeDocId).length() > 0) {
				poService.removePoDocument(removeDocId, poId);
				List<PurchaseOrderDocument> prDocsList = poService.findAllPlainPoDocsbyPoId(poId);
				headers.add("success", messageSource.getMessage("pr.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<PurchaseOrderDocument>>(prDocsList, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			headers.add("error", messageSource.getMessage("pr.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<PurchaseOrderDocument>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		headers.add("info", messageSource.getMessage("pr.file.remove", new Object[] {}, Global.LOCALE));
		return new ResponseEntity<List<PurchaseOrderDocument>>(null, headers, HttpStatus.OK);
	}

	//PH-4113
	@RequestMapping(path = "/savePoDraft/{poId}", method = RequestMethod.GET)
	public ModelAndView savePoDraft(@PathVariable String poId,@RequestParam(required=false) String prId, RedirectAttributes redir) {
		LOG.info("po savePoDraft called..");
		try {
			if (StringUtils.checkString(poId).length() > 0) {
				Po persistObj = poService.getLoadedPoById(poId);
				persistObj.setStatus(PoStatus.DRAFT);
				poService.updatePo(persistObj);
				LOG.info("po status Updated succesfully");
				redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[] { persistObj.getPoNumber() }, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while save draft :" + e.getMessage(), e);
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

}