package com.privasia.procurehere.web.controller;

import com.privasia.procurehere.core.dao.RfsTemplateDocumentDao;
import com.privasia.procurehere.core.dao.SourcingTemplateDao;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventDocument;
import com.privasia.procurehere.core.entity.RfsTemplateDocument;
import com.privasia.procurehere.core.entity.RfxTemplateDocument;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.SourcingTemplateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/buyer")
public class SourcingTemplateDocumentController {
    private static final Logger LOG = LogManager.getLogger(Global.RFS_LOG);
    @Resource
    MessageSource messageSource;

    @Autowired
    OwnerSettingsService ownerSettingsService;

    @Autowired
    SourcingTemplateService sourcingTemplateService;

    @RequestMapping(value = "/createRfsTemplateDocuments", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfsTemplateDocument>> uploadRfsTemplateDocument(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, @RequestParam("formId") String formId) {
        String fileName = null;
        List<RfsTemplateDocument> docsList = new ArrayList<RfsTemplateDocument>();
        if (!file.isEmpty()) {
            try {
                fileName = file.getOriginalFilename();
                docsList = sourcingTemplateService.findAllTemplateDocsBytemplateId(formId);
                // Check if the number of documents is greater than or equal to 5
                if (docsList.size() >= 5) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("error", "You cannot upload more than 5 documents for this template.");
                    return new ResponseEntity<List<RfsTemplateDocument>>(docsList, headers, HttpStatus.BAD_REQUEST);
                }
                byte[] bytes = file.getBytes();
                LOG.info("FILE CONTENT" + bytes);
                RfsTemplateDocument document = new RfsTemplateDocument();
                document.setCredContentType(file.getContentType());
                document.setDescription(desc);
                document.setFileName(fileName);
                document.setFileData(bytes);
                document.setUploadDate(new Date());
                document.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
                SourcingFormTemplate rfsTemplate = sourcingTemplateService.getSourcingFormbyId(formId);
                document.setSourcingFormTemplate(rfsTemplate);
                document.setUploadBy(SecurityLibrary.getLoggedInUser());
                document.setInternal(true);
                sourcingTemplateService.saveRfsTemplateDocument(document);
                HttpHeaders headers = new HttpHeaders();
                docsList = sourcingTemplateService.findAllTemplateDocsBytemplateId(formId);
                headers.add("success", "File " + fileName + " upload successfully ");
                return new ResponseEntity<List<RfsTemplateDocument>>(docsList, headers, HttpStatus.OK);
            } catch (Exception e) {
                LOG.error("You failed to upload " + fileName + ": " + e.getMessage(), e);
                HttpHeaders headers = new HttpHeaders();
                headers.add("error", "You failed to upload " + fileName);
                return new ResponseEntity<List<RfsTemplateDocument>>(null, headers, HttpStatus.BAD_REQUEST);
            }
        }
        return null;
    }

    @RequestMapping(value = "/downloadRfsTemplateDocument/{docId}", method = RequestMethod.GET)
    public void downloadRfsTemplateFile(@PathVariable String docId, HttpServletResponse response) throws IOException {
        try {
            sourcingTemplateService.downloadRfsTemplateDocument(docId, response);
        } catch (Exception e) {
            LOG.error("Error while downloading RfsTemplateDocument : " + e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/removeRfsTemplateDocument", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<List<RfsTemplateDocument>> removeRfsTemplateDocument(@RequestParam("removeDocId") String removeDocId, @RequestParam("formId") String formId) {
        try {
            if (StringUtils.checkString(removeDocId).length() > 0) {
                sourcingTemplateService.removeDocument(sourcingTemplateService.findDocsById(removeDocId));
                List<RfsTemplateDocument> docsList = sourcingTemplateService.findAllTemplateDocsBytemplateId(formId);
                HttpHeaders headers = new HttpHeaders();
                headers.add("success", messageSource.getMessage("rft.file.remove", new Object[] {}, Global.LOCALE));
                return new ResponseEntity<List<RfsTemplateDocument>>(docsList, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            LOG.error("Error while Error while removing Other Credential : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("rft.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
            return new ResponseEntity<List<RfsTemplateDocument>>(null, headers, HttpStatus.EXPECTATION_FAILED);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("success", messageSource.getMessage("rft.file.remove", new Object[] {}, Global.LOCALE));
        return new ResponseEntity<List<RfsTemplateDocument>>(null, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateDocumentDesc", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfsTemplateDocument>> updateDocumentDesc(@RequestParam("docId") String docId, @RequestParam("docDesc") String docDesc, @RequestParam("formId") String formId) {
        LOG.info("====docId=====" + docId + "======docDesc======" + docDesc);
        try {
            sourcingTemplateService.updateDocument(docId, docDesc, formId);
            List<RfsTemplateDocument> docsList = sourcingTemplateService.findAllTemplateDocsBytemplateId(formId);
            HttpHeaders headers = new HttpHeaders();
            headers.add("success", "File Description updated successfully ");
            return new ResponseEntity<List<RfsTemplateDocument>>(docsList, headers, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("You failed to update File Description :" + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", "You failed to update File Description :" + e.getMessage());
            return new ResponseEntity<List<RfsTemplateDocument>>(null, headers, HttpStatus.BAD_REQUEST);
        }

    }

}
