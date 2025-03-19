package com.privasia.procurehere.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.RequestAudit;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import com.privasia.procurehere.core.entity.SourcingFormRequestSorItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.RfsBqPojo;
import com.privasia.procurehere.core.pojo.SourcingReqBqPojo;
import com.privasia.procurehere.core.pojo.SourcingReqSorPojo;
import com.privasia.procurehere.core.pojo.SourcingSorItemPojo;
import com.privasia.procurehere.core.pojo.SourcingSorItemResponsePojo;
import com.privasia.procurehere.core.pojo.UomPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.SourcingFormRequestSorService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.web.editors.SourcingFormRequestSorEditor;
import com.privasia.procurehere.web.editors.UserEditor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/buyer")
public class SourcingFormRequestSorController {

    private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);


    @Autowired
    SourcingFormRequestService sourcingFormRequestService;

    @Autowired
    SourcingFormRequestSorService sourcingFormRequestSorService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    UserEditor userEditor;

    @Autowired
    UomService uomService;

    @Autowired
    SourcingFormRequestSorEditor sourcingFormRequestBqEditor;

    @Autowired
    ServletContext context;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(User.class, userEditor);
        binder.registerCustomEditor(SourcingFormRequestBq.class, sourcingFormRequestBqEditor);
        StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringtrimmer);
    }

    @ModelAttribute("step")
    public String getStep() {
        return "5";
    }

    @RequestMapping(path = "/createSourcingRequestSorList/{formId}", method = RequestMethod.GET)
    public String createSourcingRequestSorList(@PathVariable("formId") String formId, Model model) {
        if (StringUtils.checkString(formId).length() == 0) {
            return "redirect:/400_error";
        }
        try {
            SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormById(formId);
//            sourcingFormRequest.setSorCompleted(true);
            sourcingFormRequest = sourcingFormRequestService.update(sourcingFormRequest);
            List<SourcingFormRequestSor> sorSourcingList = sourcingFormRequestSorService.findSorByFormIdByOrder(formId);
            model.addAttribute("eventPermissions", sourcingFormRequestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), formId));
            model.addAttribute("sourcingFormRequest", sourcingFormRequest);
            model.addAttribute("sorSourcingList", sorSourcingList);

            RequestAudit audit = new RequestAudit();
            model.addAttribute("audit", audit);
        } catch (Exception e) {
            LOG.error("Error : " + e.getMessage(), e);
            // model.addAttribute("error", "Error while fetching sourcing Bq List : " + e.getMessage());
            model.addAttribute("error", messageSource.getMessage("error.while.fetching.sourcing.sorlist", new Object[]{e.getMessage()}, Global.LOCALE));
        }
        return "sourcingFormRequestSorList";
    }

    @RequestMapping(value = "/sourcingSorPrevious", method = RequestMethod.POST)
    public String sourcingSorPrevious(@RequestParam(name = "formId", required = true) String formId) {
        SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormById(formId);
        if (sourcingFormRequest != null) {
            return "redirect:createSourcingRequestBqList/" + formId;
        } else {
            LOG.error("Sourcing Request not found redirecting to login");
            return "/";
        }
    }


    @RequestMapping(value = "/sourcingSorListPrevious", method = RequestMethod.POST)
    public String sourcingBqListPrevious(@RequestParam(name = "formId", required = true) String formId) throws Exception {
        try {
            if (formId != null) {
                return "redirect:createSourcingRequestSorList/" + formId;
            } else {
                LOG.error("Sourcing Request not found redirecting to login");
                return "/";
            }
        } catch (Exception e) {
            LOG.error("Sourcing Request not found redirecting to login" + e.getMessage(), e);
            throw new Exception("Sourcing Request not found redirecting to login" + e.getMessage());
        }
    }

    @RequestMapping(path = "/createSourcingRequestSorList", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<SourcingFormRequestSor>> saveSourcingSorList(@RequestBody SourcingReqSorPojo sourcingReqSorPojo) {

        try {
            if (!sorValidate(sourcingReqSorPojo.getFormId(), sourcingReqSorPojo.getId(), sourcingReqSorPojo.getSorName())) {
                sourcingFormRequestSorService.saveSourcingSor(sourcingReqSorPojo);
                List<SourcingFormRequestSor> bqList = sourcingFormRequestSorService.findSorByFormIdByOrder(sourcingReqSorPojo.getFormId());
                HttpHeaders headers = new HttpHeaders();
                headers.add("success", messageSource.getMessage("rft.sor.save", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<SourcingFormRequestSor>>(bqList, headers, HttpStatus.OK);
            } else {
                List<SourcingFormRequestSor> bqList = sourcingFormRequestSorService.findSorByFormIdByOrder(sourcingReqSorPojo.getFormId());
                HttpHeaders headers = new HttpHeaders();
                headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[]{sourcingReqSorPojo.getSorName()}, Global.LOCALE));
                return new ResponseEntity<List<SourcingFormRequestSor>>(bqList, headers, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error while adding sourcing SOR to Buyer : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("rft.sor.errorr", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<List<SourcingFormRequestSor>>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/updateSourcingSorOrder/{formId}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<SourcingFormRequestSor>> updateSourcingBqOrder(@RequestBody String[] bqIds, @PathVariable("formId") String formId) {
        try {
            if (bqIds != null && bqIds.length > 0) {
                LOG.info("formId : " + formId);
                Integer count = 1;
                for (String bqId : bqIds) {
                    SourcingFormRequestSor bq = sourcingFormRequestSorService.getSorById(bqId);
                    bq.setSorOrder(count);
                    sourcingFormRequestSorService.updateSourcingSor(bq);
                    count++;
                }
                List<SourcingFormRequestSor> bqList = sourcingFormRequestSorService.findSorByFormIdByOrder(formId);
                HttpHeaders headers = new HttpHeaders();
                headers.add("success", messageSource.getMessage("rft.sor.order", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<SourcingFormRequestSor>>(bqList, headers, HttpStatus.OK);
            } else {
                List<SourcingFormRequestSor> bqList = sourcingFormRequestSorService.findSorByFormIdByOrder(formId);
                HttpHeaders headers = new HttpHeaders();
                headers.add("error", messageSource.getMessage("rft.sor.order.empty", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<SourcingFormRequestSor>>(bqList, headers, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error while Ordering sourcing SOR : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<List<SourcingFormRequestSor>>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/showSourcingSorItems", method = RequestMethod.POST)
    public String showSourcingBqItems(Model model, @RequestParam(name = "bqId", required = true) String bqId, @RequestParam(name = "formId", required = true) String formId, RedirectAttributes redir) {
        SourcingFormRequest sourcingFormRequest = sourcingFormRequestSorService.getSourcingRequestSorByFormId(formId);
        if (sourcingFormRequest != null) {
            return "redirect:createSourcingSorItem/" + sourcingFormRequest.getId() + "/" + bqId;
        } else {
            LOG.error("BQ not found redirecting to login ");
            return "/";
        }
    }

    @RequestMapping(path = "/createSourcingSorItem/{formId}/{bqId}", method = RequestMethod.GET)
    public String createSourcingBqItem(Model model, @PathVariable(name = "bqId", required = true) String bqId, @PathVariable(name = "formId", required = true) String formId, RedirectAttributes redir) {
        LOG.info("Sor ID............................" + bqId);
        LOG.info("Form Id.........................." + formId);

        if (StringUtils.checkString(formId).length() == 0 || StringUtils.checkString(bqId).length() == 0) {
            return "redirect:/400_error";
        }
        Integer start = 0;
        Integer length = 50;
        Integer pageNo = 1;
        SourcingFormRequest sourcingFormRequest = sourcingFormRequestSorService.getSourcingRequestSorByFormId(formId);

        SourcingFormRequestSor bq = sourcingFormRequestSorService.findSorById(bqId);
        List<SourcingFormRequestSorItem> returnList = new ArrayList<>();
        length = SecurityLibrary.getLoggedInUser().getBqPageLength();
        List<SourcingFormRequestSorItem> bqList = sourcingFormRequestSorService.getSorItemForSearchFilter(bqId, null, null, null, start, length, pageNo);
        buildBqItemForSearch(returnList, bqList);
        model.addAttribute("sourcingFormRequestBqItem", new SourcingFormRequestSorItem());
        model.addAttribute("bqList", returnList);
        model.addAttribute("eventPermissions", sourcingFormRequestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), formId));
        buildModel(formId, bqId, model, sourcingFormRequest, bq);
        List<SourcingSorItemPojo> levelOrderList = sourcingFormRequestSorService.getAllLevelOrderSorItemBySorId(bqId, null);
        model.addAttribute("levelOrderList", levelOrderList);
        long totalBqItemCount = sourcingFormRequestSorService.getTotalSortemCountBySorId(bqId, null);
        model.addAttribute("totalBqItemCount", totalBqItemCount);
        LOG.info("totalBqItemCount :" + totalBqItemCount);

        return "sourcingFormCreateSor";
    }


    @RequestMapping(path = "/createSourcingSorItem", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<SourcingSorItemResponsePojo> saveSourcingFormBqItem(@RequestBody SourcingSorItemPojo sourcingBqItemPojo, Model model) throws JsonProcessingException {
        try {
            LOG.info("Crate Sourcing SOR ID :: " + sourcingBqItemPojo.getSor());
            LOG.info("Unit Price " + sourcingBqItemPojo.getUnitPrice());
            if (StringUtils.checkString(sourcingBqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(sourcingBqItemPojo.getParent()).length() == 0) {
                HttpHeaders header = new HttpHeaders();
                header.add("error", messageSource.getMessage("event.sorsection.required", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<SourcingSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
            }

            SourcingFormRequestSorItem sourcingBqItem = new SourcingFormRequestSorItem();
            sourcingBqItem.setId(sourcingBqItemPojo.getId());
            sourcingBqItem.setSourcingFormRequest(sourcingFormRequestSorService.getSourcingRequestSorByFormId(sourcingBqItemPojo.getFormId()));
            sourcingBqItem.setItemName(sourcingBqItemPojo.getItemName());
            sourcingBqItem.setSor(sourcingFormRequestSorService.getSorById(sourcingBqItemPojo.getSor()));
            constructBqItemValues(sourcingBqItemPojo, sourcingBqItem);
            if (StringUtils.checkString(sourcingBqItemPojo.getParent()).length() > 0) {
                sourcingBqItem.setParent(sourcingFormRequestSorService.getSorItemsbyBqId(StringUtils.checkString(sourcingBqItemPojo.getParent())));
            }

            if (sourcingBqItemPojo.getQuantity() != null) {
                HttpHeaders header = new HttpHeaders();

                BigDecimal i = sourcingBqItemPojo.getQuantity();
                BigDecimal c = BigDecimal.ZERO;
                int res = i.compareTo(c);
                if (res == 0) {
                    header.add("error", messageSource.getMessage("buyer.rftsor.quantity", new Object[]{sourcingBqItemPojo.getQuantity()}, Global.LOCALE));
                    return new ResponseEntity<SourcingSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
                }
            }
            Integer itemLevel = null;
            Integer itemOrder = null;
            if (StringUtils.checkString(sourcingBqItemPojo.getFilterVal()).length() == 1) {
                sourcingBqItemPojo.setFilterVal("");
            }
            if (StringUtils.checkString(sourcingBqItemPojo.getFilterVal()).length() > 0) {
                itemLevel = 0;
                itemOrder = 0;
                String[] values = sourcingBqItemPojo.getFilterVal().split("\\.");
                itemLevel = Integer.parseInt(values[0]);
                itemOrder = Integer.parseInt(values[1]);
            }
            model.addAttribute("eventPermissions", sourcingFormRequestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), sourcingBqItem.getSourcingFormRequest().getId()));
            // sending level order list and bq Item list bind in new Pojo class
            SourcingSorItemResponsePojo bqItemResponsePojo = new SourcingSorItemResponsePojo();

            if (!sourcingFormRequestSorService.isSorItemExists(sourcingBqItem, sourcingBqItemPojo.getSor(), sourcingBqItemPojo.getParent())) {
                sourcingFormRequestSorService.saveSourcingSorItem(sourcingBqItem);
                sourcingBqItemPojo.setStart(0);
                List<SourcingFormRequestSorItem> bqItemList = sourcingFormRequestSorService.getSorItemForSearchFilter(sourcingBqItemPojo.getSor(), itemLevel, itemOrder, sourcingBqItemPojo.getSearchVal(), sourcingBqItemPojo.getStart(), sourcingBqItemPojo.getPageLength(), sourcingBqItemPojo.getPageNo());
                List<SourcingSorItemPojo> leveLOrderList = sourcingFormRequestSorService.getAllLevelOrderSorItemBySorId(sourcingBqItemPojo.getSor(), sourcingBqItemPojo.getSearchVal());
                bqItemResponsePojo.setBqItemList(bqItemList);
                bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
                HttpHeaders header = new HttpHeaders();
                if(sourcingBqItemPojo != null && sourcingBqItemPojo.getUom() != null && sourcingBqItemPojo.getUom().length() > 1) {
                    header.add("success", messageSource.getMessage("buyer.rftsoritem.save", new Object[]{}, Global.LOCALE));
                } else {
                    header.add("success", messageSource.getMessage("buyer.rftsor.save", new Object[]{}, Global.LOCALE));
                }
                return new ResponseEntity<SourcingSorItemResponsePojo>(bqItemResponsePojo, header, HttpStatus.OK);
            } else {
                List<SourcingFormRequestSorItem> bqItemList = sourcingFormRequestSorService.getSorItemForSearchFilter(sourcingBqItemPojo.getSor(), itemLevel, itemOrder, sourcingBqItemPojo.getSearchVal(), sourcingBqItemPojo.getStart(), sourcingBqItemPojo.getPageLength(), sourcingBqItemPojo.getPageNo());
                List<SourcingSorItemPojo> leveLOrderList = sourcingFormRequestSorService.getAllLevelOrderSorItemBySorId(sourcingBqItemPojo.getSor(), sourcingBqItemPojo.getSearchVal());
                bqItemResponsePojo.setBqItemList(bqItemList);
                bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
                HttpHeaders header = new HttpHeaders();
                header.add("error", messageSource.getMessage("buyer.rftbq.duplicate", new Object[]{sourcingBqItemPojo.getItemName()}, Global.LOCALE));
                return new ResponseEntity<SourcingSorItemResponsePojo>(bqItemResponsePojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            LOG.error("Error while adding Sor Items to Buyer : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<SourcingSorItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(value = "/sourcingSorNext", method = RequestMethod.POST)
    public String bqNext(Model model, @RequestParam(name = "formId", required = true) String formId) {
        SourcingFormRequest sourcingFormRequest = sourcingFormRequestSorService.getSourcingRequestSorByFormId(formId);
        List<RequestAudit> reqAudit = sourcingFormRequestService.getReqAudit(formId);
        LOG.info("formId " + formId);

        if (sourcingFormRequest != null) {
            sourcingFormRequest.setSorCompleted(Boolean.TRUE);
            sourcingFormRequestService.updateSourcingFormRequest(sourcingFormRequest);
            model.addAttribute("sourcingFormRequest", sourcingFormRequest);
            return "redirect:/buyer/sourcingRequestSummary/" + sourcingFormRequest.getId();
        } else {
            LOG.error("Sourcing Request not found redirecting to login ");
            return "/";
        }
    }


    @RequestMapping(path = "/getSourcingSorForEdit", method = RequestMethod.GET)
    public ResponseEntity<SourcingFormRequestSorItem> getSourcingBqForEdit(@RequestParam String bqId, @RequestParam String bqItemId) {
        LOG.info("  getSorForEdit  : " + bqId + " bqItemId  :" + bqItemId);
        SourcingFormRequestSorItem sourcingBqItem = null;
        LOG.info("SOR EDIT :: bqId" + bqId + " :: bqItemId :: " + bqItemId);
        try {
            sourcingBqItem = sourcingFormRequestSorService.getSorItemsbyBqId(bqItemId);
            sourcingBqItem = sourcingBqItem.createShallowCopy();
        } catch (Exception e) {
            LOG.error("Error while getting SOR Item for Edit : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<SourcingFormRequestSorItem>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (sourcingBqItem != null) {
            return new ResponseEntity<SourcingFormRequestSorItem>(sourcingBqItem, HttpStatus.OK);
        } else {
            LOG.warn("The SOR Item for the specified Event not found. Bad Request.");
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[]{}, Global.LOCALE));
            return new ResponseEntity<SourcingFormRequestSorItem>(null, headers, HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/deleteSourcingSor", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<SourcingFormRequestSor>> deleteSourcingBq(@RequestBody SourcingReqBqPojo sourcingReqBqPojo) throws JsonProcessingException {
        SourcingFormRequestSor sourcingFormRequestBq = sourcingFormRequestSorService.getSorById(sourcingReqBqPojo.getId());
        try {
            sourcingFormRequestSorService.deleteSourcingSor(sourcingFormRequestBq.getId());
            LOG.info(" After delete SOR display remaining formID :: " + sourcingReqBqPojo.getFormId());
            List<SourcingFormRequestSor> bqList = sourcingFormRequestSorService.findSorByFormIdByOrder(sourcingReqBqPojo.getFormId());
            Integer count = 1;
            if (CollectionUtil.isNotEmpty(bqList)) {
                for (SourcingFormRequestSor bq : bqList) {
                    bq.setSorOrder(count);
                    sourcingFormRequestSorService.updateSourcingSor(bq);
                    count++;
                }
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("success", messageSource.getMessage("rft.sor.success.delete", new Object[]{sourcingFormRequestBq.getName()}, Global.LOCALE));
            return new ResponseEntity<List<SourcingFormRequestSor>>(bqList, headers, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Error while deleting BQ  [ " + sourcingFormRequestBq.getName() + " ]" + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("rft.sor.error.delete", new Object[]{sourcingFormRequestBq.getName()}, Global.LOCALE));
            return new ResponseEntity<List<SourcingFormRequestSor>>(null, headers, HttpStatus.EXPECTATION_FAILED);
        }

    }


    @RequestMapping(value = "/uploadRfsSorItems", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
        LOG.info("UPLOADING STARTED ...... SOR Id :: " + bqId + "Event Id :: " + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
        List<SourcingFormRequestSorItem> sorItemList = null;
        HttpHeaders headers = new HttpHeaders();
        Integer itemLevel = null;
        Integer itemOrder = null;
        Integer start = null;
        Integer length = null;
        int totalBqItemCount = 0;
        Map<String, Object> response = new HashMap<String, Object>();
        try {
            if (StringUtils.checkString(filterVal).length() == 1) {
                filterVal = "";
            }
            if (StringUtils.checkString(filterVal).length() > 0) {
                itemLevel = 0;
                itemOrder = 0;
                String[] values = filterVal.split("\\.");
                itemLevel = Integer.parseInt(values[0]);
                itemOrder = Integer.parseInt(values[1]);
            }
            start = 0;
            length = pageLength;
            LOG.info(" itemOrder : " + itemOrder + " itemLevel :" + itemLevel);
            if (!file.isEmpty()) {
                if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
                    throw new MultipartException("Only excel files accepted!");

                try {
                    File convFile = File.createTempFile(file.getOriginalFilename(), "xlsx");
                    convFile.createNewFile();
                    file.transferTo(convFile);

                    totalBqItemCount = sourcingFormRequestSorService.uploadSorFile(bqId, eventId, convFile, SecurityLibrary.getLoggedInUserTenantId());
                    if (totalBqItemCount == 0) {
                        LOG.info("totalBqItemCount " + totalBqItemCount);
                        throw new Exception("Please fill data in excel file");
                    }

                } catch (ExcelParseException e) {
                    sorItemList = sourcingFormRequestSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
                    for (SourcingFormRequestSorItem bqItem : sorItemList) {
                        if (bqItem.getUom() != null) {
                            bqItem.getUom().setCreatedBy(null);
                            bqItem.getUom().setModifiedBy(null);
                        }
                    }
                    headers.add("error", e.getMessage());
                    response.put("list", sorItemList);
                    response.put("totalBqItemCount", totalBqItemCount);
                    return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.BAD_REQUEST);

                } catch (Exception e) {
                    // e.printStackTrace();
                    // bqList = rfqBqService.findBqbyBqId(bqId);
                    sorItemList = sourcingFormRequestSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
                    for (SourcingFormRequestSorItem bqItem : sorItemList) {
                        if (bqItem.getUom() != null) {
                            bqItem.getUom().setCreatedBy(null);
                            bqItem.getUom().setModifiedBy(null);
                        }
                    }
                    headers.add("error", messageSource.getMessage("common.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
                    response.put("list", sorItemList);
                    response.put("totalBqItemCount", totalBqItemCount);
                    return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (MultipartException e) {
            // LOG.info("Upload failed!" + e.getMessage());
            // bqList = rfqBqService.findBqbyBqId(bqId);
            sorItemList = sourcingFormRequestSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
            for (SourcingFormRequestSorItem bqItem : sorItemList) {
                if (bqItem.getUom() != null) {
                    bqItem.getUom().setCreatedBy(null);
                    bqItem.getUom().setModifiedBy(null);
                }
            }
            headers.add("error", messageSource.getMessage("buyer.rftbq.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
            response.put("list", sorItemList);
            response.put("totalBqItemCount", totalBqItemCount);
            return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.BAD_REQUEST);
        }
        sorItemList = sourcingFormRequestSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
        for (SourcingFormRequestSorItem bqItem : sorItemList) {
            if (bqItem.getUom() != null) {
                bqItem.getUom().setCreatedBy(null);
                bqItem.getUom().setModifiedBy(null);
            }
        }
        SourcingFormRequestSor bq = sourcingFormRequestSorService.getSorById(bqId);
        RfsBqPojo rfsBqColumns = new RfsBqPojo(bq);
        response.put("rfsBqColumns", rfsBqColumns);

        headers.add("success", messageSource.getMessage("buyer.rftsoritems.upload.success", new Object[] {}, Global.LOCALE));
        response.put("list", sorItemList);
        response.put("totalBqItemCount", totalBqItemCount);
        return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.OK);
    }


    @RequestMapping(path = "/updateSourcingSorItem", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<SourcingSorItemResponsePojo> updateSourcingSorItem(@RequestBody SourcingSorItemPojo sourcingBqItemPojo) throws JsonProcessingException {
        try {

            if (StringUtils.checkString(sourcingBqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(sourcingBqItemPojo.getParent()).length() == 0) {
                HttpHeaders header = new HttpHeaders();
                header.add("error", messageSource.getMessage("event.sorsection.required", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<SourcingSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
            }

            SourcingFormRequestSorItem sourcingBqItem = sourcingFormRequestSorService.getSorItemsbyBqId(sourcingBqItemPojo.getId());
            sourcingBqItem.setUom(StringUtils.checkString(sourcingBqItemPojo.getUom()).length() > 0 ? uomService.getUombyCode(sourcingBqItemPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()) : null);
            buildUpdateBqItem(sourcingBqItemPojo, sourcingBqItem);
            SourcingFormRequestSorItem sourcingReqBqItem = new SourcingFormRequestSorItem();
            sourcingReqBqItem.setId(sourcingBqItemPojo.getId());
            sourcingReqBqItem.setSourcingFormRequest(sourcingFormRequestSorService.getSourcingRequestSorByFormId(sourcingBqItemPojo.getFormId()));
            if (sourcingFormRequestSorService.isSorItemExists(sourcingReqBqItem, sourcingBqItemPojo.getSor(), sourcingBqItemPojo.getParent())) {
                throw new ApplicationException("Duplicate SOR Item. SOR Item by that name already exists.");
            }
            /*************** To check whether quantity of Bqitem entered should not be 0 ****************/

            if (sourcingBqItemPojo.getQuantity() != null) {
                BigDecimal i = sourcingBqItemPojo.getQuantity();
                BigDecimal c = BigDecimal.ZERO;
                int res = i.compareTo(c);
                if (res == 0) {
                    throw new ApplicationException("Bill of Quantity Item quantity cannot be 0 ");
                }
            }

            sourcingFormRequestSorService.updateSourcingSorItem(sourcingBqItem);
            // sending level order list and sor Item list bind in new Pojo class
            SourcingSorItemResponsePojo bqItemResponsePojo = new SourcingSorItemResponsePojo();
            List<SourcingSorItemPojo> levelOrderList = sourcingFormRequestSorService.getAllLevelOrderSorItemBySorId(sourcingBqItemPojo.getSor(),
                    sourcingBqItemPojo.getSearchVal());
            bqItemResponsePojo.setLeveLOrderList(levelOrderList);

            Integer itemLevel = null;
            Integer itemOrder = null;
            if (StringUtils.checkString(sourcingBqItemPojo.getFilterVal()).length() == 1) {
                sourcingBqItemPojo.setFilterVal("");
            }
            if (StringUtils.checkString(sourcingBqItemPojo.getFilterVal()).length() > 0) {
                itemLevel = 0;
                itemOrder = 0;
                String[] values = sourcingBqItemPojo.getFilterVal().split("\\.");
                itemLevel = Integer.parseInt(values[0]);
                itemOrder = Integer.parseInt(values[1]);
            }
            sourcingBqItemPojo.setStart(0);
            List<SourcingFormRequestSorItem> bqItemList = sourcingFormRequestSorService.getSorItemForSearchFilter(sourcingBqItemPojo.getSor(),
                    itemLevel, itemOrder, sourcingBqItemPojo.getSearchVal(), sourcingBqItemPojo.getStart(), sourcingBqItemPojo.getPageLength(), sourcingBqItemPojo.getPageNo());
            bqItemResponsePojo.setBqItemList(bqItemList);
            HttpHeaders header = new HttpHeaders();
            if(sourcingBqItemPojo != null && sourcingBqItemPojo.getUom() != null && sourcingBqItemPojo.getUom().length() > 1) {
                header.add("success", messageSource.getMessage("buyer.rftsoritem.save", new Object[]{}, Global.LOCALE));
            } else {
                header.add("success", messageSource.getMessage("buyer.rftsor.save", new Object[]{}, Global.LOCALE));
            }
            return new ResponseEntity<SourcingSorItemResponsePojo>(bqItemResponsePojo, header, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error while updating SOR Items to Buyer : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<SourcingSorItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/getSourcingSorForNewFields", method = RequestMethod.GET)
    public ResponseEntity<SourcingFormRequestSor> getBqForNewFields(@RequestParam String bqId) {
        SourcingFormRequestSor requestBq = null;
        LOG.info("SOR EDIT :: sorId" + bqId);
        try {
            requestBq = sourcingFormRequestSorService.getSorById(bqId);

        } catch (Exception e) {
            LOG.error("Error while getting Sourcing SOR Item for Edit : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<SourcingFormRequestSor>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (requestBq != null) {
            return new ResponseEntity<SourcingFormRequestSor>(requestBq, HttpStatus.OK);
        } else {
            LOG.warn("The SOR Item for the specified Event not found. Bad Request.");
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[]{}, Global.LOCALE));
            return new ResponseEntity<SourcingFormRequestSor>(null, headers, HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(path = "/getSourcingSorItemForSearchFilter", method = RequestMethod.POST)
    public ResponseEntity<SourcingSorItemResponsePojo> getSourcingSorItemForSearchFilter(@RequestParam("bqId") String bqId, @RequestParam("formId") String formId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
        LOG.info(" getSorItemForSearchFilter  : " + bqId + " formId :" + formId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
        List<SourcingFormRequestSorItem> bqItemList = null;
        HttpHeaders headers = new HttpHeaders();
        // sending total Bq Item count and Sor Item list bind in new Pojo class
        SourcingSorItemResponsePojo bqItemResponsePojo = new SourcingSorItemResponsePojo();
        try {
            Integer itemLevel = null;
            Integer itemOrder = null;
            Integer start = null;
            Integer length = null;
            if (StringUtils.checkString(filterVal).length() == 1) {
                filterVal = "";
            }
            if (StringUtils.checkString(filterVal).length() > 0) {
                itemLevel = 0;
                itemOrder = 0;
                String[] values = filterVal.split("\\.");
                itemLevel = Integer.parseInt(values[0]);
                itemOrder = Integer.parseInt(values[1]);
            }
            start = 0;
            length = pageLength;
            LOG.info(" itemOrder : " + itemOrder + " itemLevel :" + itemLevel);
            bqItemList = sourcingFormRequestSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
            bqItemResponsePojo.setBqItemList(bqItemList);
            long totalBqItemCount = sourcingFormRequestSorService.getTotalSortemCountBySorId(bqId, searchVal);
            LOG.info("totalBqItemCount :" + totalBqItemCount);
            bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);

            List<SourcingSorItemPojo> leveLOrderList = sourcingFormRequestSorService.getAllLevelOrderSorItemBySorId(bqId, searchVal);
            bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
            for (SourcingFormRequestSorItem sourcingBqItem : bqItemList) {
                if (sourcingBqItem.getUom() != null) {
                    sourcingBqItem.getUom().setCreatedBy(null);
                    sourcingBqItem.getUom().setModifiedBy(null);
                }
            }

            // Not sure about this method
//            if (pageLength != SecurityLibrary.getLoggedInUser().getBqPageLength()) {
//                userService.updateUserBqPageLength(pageLength, SecurityLibrary.getLoggedInUser().getId());
//                updateSecurityLibraryUser(pageLength);
//            }
            return new ResponseEntity<SourcingSorItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error during Search Reset Bill Of Quantity :" + e.getMessage(), e);
            headers.add("error", "Error during Search reset Bill Of Quantity ");
            return new ResponseEntity<SourcingSorItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(path = "/sourcingSOROrder", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<SourcingFormRequestSorItem>> sourcingBQOrder(@RequestBody SourcingSorItemPojo sourcingBqItemPojo) {
        LOG.info("sourcingBqItemPojo.getBq() : " + sourcingBqItemPojo.getSor() + " Parent : " + sourcingBqItemPojo.getParent() + " Item Id : " + sourcingBqItemPojo.getId() + " New Position : " + sourcingBqItemPojo.getOrder());
        HttpHeaders headers = new HttpHeaders();
        List<SourcingFormRequestSorItem> bqList = null;
        Integer start = null;
        Integer length = null;
        try {
            if (StringUtils.checkString(sourcingBqItemPojo.getId()).length() > 0) {
                start = 0;
                length = sourcingBqItemPojo.getPageLength();
                LOG.info("Updating order.........................." + sourcingBqItemPojo.getSor());
                SourcingFormRequestSorItem bqItem = sourcingFormRequestSorService.getSorItemsbyBqId(sourcingBqItemPojo.getId());
                if (bqItem.getOrder() > 0 && StringUtils.checkString(sourcingBqItemPojo.getParent()).length() == 0) {
                    LOG.info("child cannot be a parent");
                    headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[]{}, Global.LOCALE));
                    List<SourcingFormRequestSorItem> returnList = sourcingFormRequestSorService.getSorItemForSearchFilter(sourcingBqItemPojo.getSor(), null, null, null, start, length, sourcingBqItemPojo.getPageNo());
                    return new ResponseEntity<List<SourcingFormRequestSorItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
                }
                if (bqItem.getOrder() == 0 && StringUtils.checkString(sourcingBqItemPojo.getParent()).length() > 0) {
                    LOG.info("parent cannot be a child");
                    headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[]{}, Global.LOCALE));
                    List<SourcingFormRequestSorItem> returnList = sourcingFormRequestSorService.getSorItemForSearchFilter(sourcingBqItemPojo.getSor(), null, null, null, start, length, sourcingBqItemPojo.getPageNo());
                    return new ResponseEntity<List<SourcingFormRequestSorItem>>(returnList, headers, HttpStatus.BAD_REQUEST);

                }
                SourcingFormRequestSorItem item = new SourcingFormRequestSorItem();
                item.setId(sourcingBqItemPojo.getId());
                item.setSourcingFormRequest(sourcingFormRequestSorService.getSourcingRequestSorByFormId(sourcingBqItemPojo.getFormId()));
                item.setItemName(sourcingBqItemPojo.getItemName());
                if (bqItem.getParent() != null && !bqItem.getParent().getId().equals(sourcingBqItemPojo.getParent()) && sourcingFormRequestSorService.isSorItemExists(item, sourcingBqItemPojo.getSor(), sourcingBqItemPojo.getParent())) {
                    LOG.info("Duplicate....");
                    throw new NotAllowedException("Duplicate SOR Item. SOR Item by that name already exists.");
                }
                constructBqItemValues(sourcingBqItemPojo, item);
                sourcingFormRequestSorService.reorderSorItems(sourcingBqItemPojo);
                bqList = sourcingFormRequestSorService.getSorItemForSearchFilter(sourcingBqItemPojo.getSor(), null, null, null, start, length, sourcingBqItemPojo.getPageNo());
                headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<SourcingFormRequestSorItem>>(bqList, headers, HttpStatus.OK);
            }
        } catch (NotAllowedException e) {
            LOG.error("Error while moving SOR parent with item as Child : " + e.getMessage(), e);
            headers.add("error", messageSource.getMessage("buyer.rftbq.drag", new Object[]{e.getMessage()}, Global.LOCALE));
            List<SourcingFormRequestSorItem> returnList = sourcingFormRequestSorService.getSorItemForSearchFilter(sourcingBqItemPojo.getSor(), null, null, null, start, length, sourcingBqItemPojo.getPageNo());
            return new ResponseEntity<List<SourcingFormRequestSorItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOG.error("Error while moving SOR Item : " + e.getMessage(), e);
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<List<SourcingFormRequestSorItem>>(null, headers, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<List<SourcingFormRequestSorItem>>(bqList, headers, HttpStatus.OK);
    }


    @RequestMapping(path = "/sorItemRfsTemplate/{bqId}", method = RequestMethod.GET)
    public void downloader(@PathVariable String bqId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            String downloadFolder = context.getRealPath("/WEB-INF/");
            String fileName = "RfsSorItemTemplate.xlsx";
            Path file = Paths.get(downloadFolder, fileName);
            LOG.info("File Path ::" + file);

            // Write data in it
            eventDownloader(workbook, bqId);

            // Save Excel File
            FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
            workbook.write(out);
            out.close();
            LOG.info("Successfully written in Excel");

            if (Files.exists(file)) {
                response.setContentType("application/vnd.ms-excel");
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
                try {
                    Files.copy(file, response.getOutputStream());
                    response.getOutputStream().flush();
                } catch (IOException e) {
                    LOG.error("Error :- " + e.getMessage());
                }
            }

        } catch (Exception e) {
            LOG.error("Error while downloading BQ items :: " + e.getMessage(), e);
        }

    }


    @RequestMapping(path = "/getSourcingSorbyId", method = RequestMethod.POST)
    public ResponseEntity<SourcingFormRequestSor> getEventBqbyId(@RequestParam("bqId") String bqId) {
        HttpHeaders headers = new HttpHeaders();
        SourcingFormRequestSor bq = sourcingFormRequestSorService.findSorById(bqId);
        return new ResponseEntity<SourcingFormRequestSor>(bq, headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/updateSourcingSorList", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<SourcingFormRequestSor>> updateBqList(@RequestBody SourcingReqSorPojo sourcingReqSorPojo) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        try {
            LOG.info("UPDATE SOURCING SOR :: " + sourcingReqSorPojo.toString());
            SourcingFormRequestSor bq = sourcingFormRequestSorService.findSorById(sourcingReqSorPojo.getId());
            bq.setName(sourcingReqSorPojo.getSorName());
            bq.setDescription(sourcingReqSorPojo.getSorDesc());
            bq.setModifiedDate(new Date());
            if (!sorValidate(sourcingReqSorPojo.getFormId(), sourcingReqSorPojo.getId(), sourcingReqSorPojo.getSorName())) {
                sourcingFormRequestSorService.updateSourcingSor(bq);
                List<SourcingFormRequestSor> bqList = sourcingFormRequestSorService.findSorByFormIdByOrder(sourcingReqSorPojo.getFormId());
                headers.add("success", messageSource.getMessage("rft.sor.order", new Object[] {}, Global.LOCALE));
                return new ResponseEntity<List<SourcingFormRequestSor>>(bqList, headers, HttpStatus.OK);
            } else {
                List<SourcingFormRequestSor> bqList = sourcingFormRequestSorService.findSorByFormIdByOrder(sourcingReqSorPojo.getFormId());
                headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[] { sourcingReqSorPojo.getSorName() }, Global.LOCALE));
                return new ResponseEntity<List<SourcingFormRequestSor>>(bqList, headers, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error while adding SOR Items to Buyer : " + e.getMessage(), e);
            headers.add("error", messageSource.getMessage("rft.sor.error", new Object[] { e.getMessage() }, Global.LOCALE));
            return new ResponseEntity<List<SourcingFormRequestSor>>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/deleteSourcingSorItems/{bqId}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<SourcingSorItemResponsePojo> deleteSourcingSorItems(@RequestParam(name = "items", required = true) String items, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo, @PathVariable("bqId") String bqId) throws JsonProcessingException {
        LOG.info("Delete Sor Items......." + items);
        List<SourcingFormRequestSorItem> bqItemList = null;
        HttpHeaders header = new HttpHeaders();

        SourcingSorItemResponsePojo bqItemResponsePojo = new SourcingSorItemResponsePojo();
        if (StringUtils.checkString(items).length() > 0) {
            try {
                String[] bqItemsIds = items.split(",");
                sourcingFormRequestSorService.deleteSourcingSorItems(bqItemsIds, bqId);
                header.add("success", messageSource.getMessage("rft.soritems.success.delete", new Object[] {}, Global.LOCALE));
                Integer itemLevel = null;
                Integer itemOrder = null;
                Integer start = null;
                Integer length = null;
                if (StringUtils.checkString(filterVal).length() == 1) {
                    filterVal = "";
                }
                if (StringUtils.checkString(filterVal).length() > 0) {
                    itemLevel = 0;
                    itemOrder = 0;
                    String[] values = filterVal.split("\\.");
                    itemLevel = Integer.parseInt(values[0]);
                    itemOrder = Integer.parseInt(values[1]);
                }
                start = 0;
                length = pageLength;
                bqItemList = sourcingFormRequestSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
                List<SourcingSorItemPojo> levelOrderList = sourcingFormRequestSorService.getAllLevelOrderSorItemBySorId(bqId, searchVal);
                bqItemResponsePojo.setBqItemList(bqItemList);
                bqItemResponsePojo.setLeveLOrderList(levelOrderList);
            } catch (NotAllowedException e) {
                LOG.error("Not Allowed Exception: " + e.getMessage(), e);
                header.add("error", e.getMessage());
                return new ResponseEntity<SourcingSorItemResponsePojo>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception e) {
                LOG.error("Error while delete sor items: " + e.getMessage(), e);
                header.add("error", "Error during delete : " + e.getMessage());
                return new ResponseEntity<SourcingSorItemResponsePojo>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<SourcingSorItemResponsePojo>(bqItemResponsePojo, header, HttpStatus.OK);
    }


    private void eventDownloader(XSSFWorkbook workbook, String bqId) {
        XSSFSheet sheet = workbook.createSheet("SOR Item List");
        int r = 1;
        SourcingFormRequestSor bq = null;

        // Create Heading Row
        bq = sourcingFormRequestSorService.getSorById(bqId);
        buildHeader(bq, workbook, sheet);

        // Create Row
        SourcingFormRequestSor rfsBqList = sourcingFormRequestSorService.getAllsorItemsBySorId(bqId);
        for (SourcingFormRequestSorItem item : rfsBqList.getSorItems()) {
            r = buildRows(sheet, r, item.getSor(), item);
        }

        // to create drop-down
        XSSFSheet lookupSheet1 = workbook.createSheet("LOOKUP1");
        int index1 = 0;
        List<UomPojo> uom = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
        for (UomPojo u : uom) {
            String uomId = u.getId();
            String uomName = u.getUom();
            XSSFRow row = lookupSheet1.createRow(index1++);
            XSSFCell cell1 = row.createCell(0);
            cell1.setCellValue(uomId);
            XSSFCell cell2 = row.createCell(1);
            cell2.setCellValue(uomName);
        }
        XSSFDataValidationHelper uomHelper = new XSSFDataValidationHelper(lookupSheet1);
        XSSFDataValidationConstraint uomConstraint = (XSSFDataValidationConstraint) uomHelper.createFormulaListConstraint("'LOOKUP1'!$B$1:$B$" + (uom.size() + 1));
        CellRangeAddressList uomAddressList = new CellRangeAddressList(1, 1000, 3, 3);
        XSSFDataValidation uomValidation = (XSSFDataValidation) uomHelper.createValidation(uomConstraint, uomAddressList);
        uomValidation.setSuppressDropDownArrow(true);
        uomValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        uomValidation.createErrorBox("Invalid UOM Selected", "Please select UOM from the list");
        uomValidation.createPromptBox("UOM List", "Select UOM from the list provided. It has been exported from your master data.");
        uomValidation.setShowErrorBox(true);
        uomValidation.setShowPromptBox(true);
        sheet.addValidationData(uomValidation);

        XSSFSheet lookupSheet2 = workbook.createSheet("LOOKUP2");
        int index2 = 0;
        PricingTypes[] priceTypeArr = PricingTypes.values();
        for (PricingTypes type : priceTypeArr) {
            String name = type.getValue();
            XSSFRow row = lookupSheet2.createRow(index2++);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(name);
        }
        XSSFDataValidationHelper priceHelper = new XSSFDataValidationHelper(lookupSheet2);
        XSSFDataValidationConstraint priceConstraint = (XSSFDataValidationConstraint) priceHelper.createFormulaListConstraint("'LOOKUP2'!$A$1:$A$" + (priceTypeArr.length + 1));
        CellRangeAddressList priceAddressList = new CellRangeAddressList(1, 1000, 6, 6);
        XSSFDataValidation priceValidation = (XSSFDataValidation) priceHelper.createValidation(priceConstraint, priceAddressList);
        priceValidation.setSuppressDropDownArrow(true);
        priceValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        priceValidation.createErrorBox("Invalid PRICE TYPE Selected", "Please select PRICE TYPE from the list");
        priceValidation.createPromptBox("PRICE TYPE List", "Select PRICE TYPE from the list provided. It has been exported from your master data.");
        priceValidation.setShowErrorBox(true);
        priceValidation.setShowPromptBox(true);
        sheet.addValidationData(priceValidation);
        workbook.setSheetHidden(1, true);
        workbook.setSheetHidden(2, true);

        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i, true);
        }
    }


    private void buildHeader(SourcingFormRequestSor bq, XSSFWorkbook workbook, XSSFSheet sheet) {
        Row rowHeading = sheet.createRow(0);

        // For style
        CellStyle styleHeading = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        styleHeading.setFont(font);
        styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

        int i = 0;
        Cell cell = null;
        for (String column : Global.SOR_EXCEL_COLUMNS) {
            cell = rowHeading.createCell(i++);
            cell.setCellValue(column);
            cell.setCellStyle(styleHeading);
        }
    }


    private int buildRows(XSSFSheet sheet, int r, SourcingFormRequestSor sourcingFormRequestBq, SourcingFormRequestSorItem item) {
        Row row = sheet.createRow(r++);
        int cellNum = 0;
        row.createCell(cellNum++).setCellValue(item.getLevel() + "." + item.getOrder());
        row.createCell(cellNum++).setCellValue(item.getItemName());
        row.createCell(cellNum++).setCellValue(item.getItemDescription() != null ? item.getItemDescription() : "");
        if (item.getOrder() == 0) {
            int colNum = 7;

            if (StringUtils.checkString(sourcingFormRequestBq.getField1Label()).length() > 0)
                colNum++;

            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, cellNum - 1, colNum));
        } else {
            row.createCell(cellNum++).setCellValue(item.getUom() != null ? item.getUom().getUom() : "");
            if (StringUtils.checkString(sourcingFormRequestBq.getField1Label()).length() > 0)
                row.createCell(cellNum++).setCellValue(item.getField1());
        }
        // Auto Fit
        for (int i = 0; i < 18; i++) {
            sheet.autoSizeColumn(i, true);
        }
        return r;
    }


    private boolean sorValidate(String formId, String bqId, String name) {
        return sourcingFormRequestSorService.isSorExists(formId, bqId, name);
    }

    private void buildBqItemForSearch(List<SourcingFormRequestSorItem> returnList, List<SourcingFormRequestSorItem> bqList) {
        SourcingFormRequestSorItem parent = null;
        for (SourcingFormRequestSorItem item : bqList) {
            if (item.getOrder() != 0) {
                if (parent != null) {
                    parent.getChildren().add(item.createSearchShallowCopy());
                }
            } else {
                parent = item.createSearchShallowCopy();
                parent.setChildren(new ArrayList<SourcingFormRequestSorItem>());
                returnList.add(parent);
            }
        }
    }

    protected void buildModel(String formId, String bqId, Model model, SourcingFormRequest form, SourcingFormRequestSor sourcingFormRequestBq) {
        List<UomPojo> uomList = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
        PricingTypes[] pricingTypes = PricingTypes.values();
        BqUserTypes[] BqFilledBy = BqUserTypes.values();
        model.addAttribute("pricingTypes", pricingTypes);
        model.addAttribute("bqFilledBy", BqFilledBy);
        model.addAttribute("formId", formId);
        model.addAttribute("bqId", bqId);
        model.addAttribute("uomList", uomList);
        model.addAttribute("sourcingFormRequest", form);
        model.addAttribute("sourcingFormRequestBq", sourcingFormRequestBq);
        model.addAttribute("bqName", sourcingFormRequestBq.getName());
    }


    protected void constructBqItemValues(SourcingSorItemPojo bqPojo, SourcingFormRequestSorItem sourcingBqItem) {
        sourcingBqItem.setItemDescription(bqPojo.getItemDescription());
        sourcingBqItem.setItemName(bqPojo.getItemName());
        if (StringUtils.checkString(bqPojo.getUom()).length() > 0) {
            sourcingBqItem.setUom(uomService.getUombyCode(bqPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()));
        }
        sourcingBqItem.setField1(bqPojo.getField1());
    }

    protected void buildUpdateBqItem(SourcingSorItemPojo bqPojo, SourcingFormRequestSorItem bqItem) {
        bqItem.setItemName(bqPojo.getItemName());
        bqItem.setUom(bqItem.getUom());
        bqItem.setItemDescription(bqPojo.getItemDescription());
        bqItem.setField1(bqPojo.getField1());
    }
}
