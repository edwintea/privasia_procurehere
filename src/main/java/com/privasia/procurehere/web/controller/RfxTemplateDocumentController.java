package com.privasia.procurehere.web.controller;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.RfxTemplateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/buyer")
public class RfxTemplateDocumentController {
    private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

    @Resource
    MessageSource messageSource;

    @Autowired
    RfxTemplateService rfxTemplateService;

    @Autowired
    OwnerSettingsService ownerSettingsService;

    @RequestMapping(path = "rfxTemplate/rfxTemplateDocument/{formId}", method = RequestMethod.GET)
    public String rfxTemplateDocument(@PathVariable("formId") String formId, Model model) {
        LOG.info("create Rfx template Document GET called Rfs id :" + formId);
        RfxTemplate template = rfxTemplateService.getRfxTemplateById(formId);
        // Get the list of documents and sort by uploadDate in descending order
        List<RfxTemplateDocument> documents = template.getDocuments();
        Collections.sort(documents, (d1, d2) -> d2.getUploadDate().compareTo(d1.getUploadDate()));

        for (RfxTemplateDocument document : template.getDocuments()) {
            String uploadedBy = (document.getUploadBy() != null ? document.getUploadBy().getId() : "");
        }
        template.setCompleteTemplateDetails(true);
        rfxTemplateService.update(template);
        model.addAttribute("template", template);
        model.addAttribute("event", template);
        model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
        RequestAudit audit = new RequestAudit();
        model.addAttribute("audit", audit);
        model.addAttribute("isTemplateUsed", template.getTemplateUsed());
        model.addAttribute("isChecked", true);
        return "rfxTemplateDocuments";
    }

    @RequestMapping(value = "/createRfxTemplateDocuments", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfxTemplateDocument>> uploadRfxTemplateDocument(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, @RequestParam("eventId") String eventId, Boolean internal) {
        String fileName = null;
        List<RfxTemplateDocument> rftDocsList = new ArrayList<RfxTemplateDocument>();
        if (!file.isEmpty()) {
            try {
                 rftDocsList = rfxTemplateService.findAllTemplateDocsBytemplateId(eventId);
                // Check if the number of documents is greater than or equal to 5
                if (rftDocsList.size() >= 5) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("error", "You cannot upload more than 5 documents for this template.");
                    return new ResponseEntity<List<RfxTemplateDocument>>(rftDocsList, headers, HttpStatus.BAD_REQUEST);
                }
                fileName = file.getOriginalFilename();
                byte[] bytes = file.getBytes();
                RfxTemplateDocument document = new RfxTemplateDocument();
                document.setCredContentType(file.getContentType());
                document.setDescription(desc);
                document.setFileName(fileName);
                document.setFileData(bytes);
                document.setUploadDate(new Date());
                document.setInternal(internal);
                document.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
                RfxTemplate template = rfxTemplateService.getRfxTemplateById(eventId);
                document.setRfxTemplate(template);
                document.setUploadBy(SecurityLibrary.getLoggedInUser());
                rfxTemplateService.saveRfxTemplateDocument(document);
                HttpHeaders headers = new HttpHeaders();
                rftDocsList = rfxTemplateService.findAllTemplateDocsBytemplateId(eventId);
                headers.add("success", "File " + fileName + " upload successfully ");
                return new ResponseEntity<List<RfxTemplateDocument>>(rftDocsList, headers, HttpStatus.OK);
            } catch (Exception e) {
                LOG.error("You failed to upload " + fileName + ": " + e.getMessage(), e);
                HttpHeaders headers = new HttpHeaders();
                headers.add("error", "You failed to upload " + fileName);
                return new ResponseEntity<List<RfxTemplateDocument>>(null, headers, HttpStatus.BAD_REQUEST);
            }
        }
        return null;

    }
    @RequestMapping(value = "/downloadRfxTemplateDocument/{docId}", method = RequestMethod.GET)
    public void downloadRfxTemplateFile(@PathVariable String docId, HttpServletResponse response) throws IOException {
        try {
            rfxTemplateService.downloadRfxTemplateDocument(docId, response);
        } catch (Exception e) {
            LOG.error("Error while downloading RfxTemplateDocument : " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/removeRfxTemplateDocument", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<List<RfxTemplateDocument>> removeRfxTemplateDocument(@RequestParam("removeDocId") String removeDocId, @RequestParam("eventId") String formId) {
        try {
            if (StringUtils.checkString(removeDocId).length() > 0) {
                rfxTemplateService.removeDocument(rfxTemplateService.findDocsById(removeDocId));
                List<RfxTemplateDocument> docsList = rfxTemplateService.findAllTemplateDocsBytemplateId(formId);
                HttpHeaders headers = new HttpHeaders();
                headers.add("success", "File removed successfully ");
                return new ResponseEntity<List<RfxTemplateDocument>>(docsList, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            LOG.error("Error while Error while removing Other Credential : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("rft.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
            return new ResponseEntity<List<RfxTemplateDocument>>(null, headers, HttpStatus.EXPECTATION_FAILED);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("success", "File removed successfully ");
        return new ResponseEntity<List<RfxTemplateDocument>>(null, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateRfxTemplateDocumentDesc", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfxTemplateDocument>> updateDocumentDesc(@RequestParam("docId") String docId, @RequestParam("docDesc") String docDesc, @RequestParam("eventId") String formId, @RequestParam("internal") Boolean internal) {
        LOG.info("====docId=====" + docId + "======docDesc======" + docDesc);
        try {
            rfxTemplateService.updateDocument(docId, docDesc, formId, internal);
            List<RfxTemplateDocument> docsList = rfxTemplateService.findAllTemplateDocsBytemplateId(formId);
            HttpHeaders headers = new HttpHeaders();
            headers.add("success", "File Description updated successfully ");
            return new ResponseEntity<List<RfxTemplateDocument>>(docsList, headers, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("You failed to update File Description :" + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", "You failed to update File Description :" + e.getMessage());
            return new ResponseEntity<List<RfxTemplateDocument>>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }
}
