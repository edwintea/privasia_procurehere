package com.privasia.procurehere.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventSor;
import com.privasia.procurehere.core.entity.RfpSorItem;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.RfpSorItemResponsePojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/buyer/RFP")
public class RfpSorController extends EventSorBase {


    public RfpSorController() {
        super(RfxTypes.RFP);
    }


    @RequestMapping(value = "/sorListPrevious", method = RequestMethod.POST)
    public String sorListPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) throws Exception {
        try {
            if (eventId != null) {
                return "redirect:createSorList/" + eventId;
            } else {
                LOG.error("Event not found redirecting to login ");
                return "/";
            }
        } catch (Exception e) {
            LOG.error("Event not found redirecting to login : " + e.getMessage(), e);
            throw new Exception("Event not found redirecting to login : " + e.getMessage());
        }
    }

    @RequestMapping(path = "/createSorList/{eventId}", method = RequestMethod.GET)
    public String createBQList(@PathVariable String eventId, Model model) {
        if (StringUtils.checkString(eventId).length() == 0) {
            return "redirect:/400_error";
        }
        RfpEvent rfpEvent = rfpEventService.getEventById(eventId);
        List<RfpEventSor> sorEventList = rfpSorService.getAllSorListByEventIdByOrder(eventId);
        model.addAttribute("event", rfpEvent);
        model.addAttribute("sorEventList", sorEventList);
        model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
        return "createSorList";
    }

    @RequestMapping(path = "/createSorList", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<Sor>> saveSorList(@RequestBody SorPojo sorPojo) throws JsonProcessingException {
        try {

            if (!rfpSorService.isSorExists(sorPojo)) {
                List<RfpEventSor> sorList = rfpSorService.getAllSorListByEventIdByOrder(sorPojo.getEventId());
                Integer count = 1;
                if (CollectionUtil.isNotEmpty(sorList)) {
                    for (RfpEventSor rfqEventSor : sorList) {
                        if (rfqEventSor.getSorOrder() == null) {
                            rfqEventSor.setSorOrder(count);
                            rfpSorService.updateSor(rfqEventSor);
                            count++;
                        }
                    }
                    count = sorList.size();
                    count++;
                }
                super.saveSor(sorPojo, count);
                List<Sor> sorList1 = rfpSorService.findSorbyEventId(sorPojo.getEventId());
                HttpHeaders headers = new HttpHeaders();
                // Need to change message here
                headers.add("success", messageSource.getMessage("rft.sor.save", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<Sor>>(sorList1, headers, HttpStatus.OK);
            } else {
                List<Sor> sorList = rfpSorService.findSorbyEventId(sorPojo.getEventId());
                HttpHeaders headers = new HttpHeaders();
                // Need to change message here
                headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[]{sorPojo.getSorName()}, Global.LOCALE));
                return new ResponseEntity<List<Sor>>(sorList, headers, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("rft.sor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<List<Sor>>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/showSorItems", method = RequestMethod.POST)
    public String showBqItems(Model model, @RequestParam(name = "bqId", required = true) String bqId, @RequestParam(name = "eventId", required = true) String eventId) {
        RfpEvent rftEvent = rfpEventService.getEventById(eventId);
        if (rftEvent != null) {
            return "redirect:createEventSor/" + rftEvent.getId() + "/" + bqId;
        } else {
            LOG.error("BQ not found redirecting to login ");
            return "/";
        }
    }

    @RequestMapping(path = "/createEventSor/{eventId}/{bqId}", method = RequestMethod.GET)
    public String createEventBQ(@PathVariable String eventId, @PathVariable String bqId, Model model) {
        if (StringUtils.checkString(eventId).length() == 0 || StringUtils.checkString(bqId).length() == 0) {
            return "redirect:/400_error";
        }
        Integer start = 0;
        Integer length = 50;
        Integer pageNo = 1;
        LOG.info("BqId : " + bqId);

        RfpEvent rftEvent = rfpEventService.getEventById(eventId);
        RfpEventSor rfqEventSor = rfpSorService.getSorById(bqId);

        List<RfpSorItem> returnList = new ArrayList<>();
        length = 0;//SecurityLibrary.getLoggedInUser().getBqPageLength();
        LOG.info(" SOR Page length : " + length);
        List<RfpSorItem> bqList = rfpSorService.getSorItemForSearchFilter(bqId, null, null, null, start, length, pageNo);


        buildBqItemForSearch(returnList, bqList);
        // List<RfqBqItem> bqList = rfqBqService.findBqbyBqId(bqId);
        model.addAttribute("rftBqItem", new RfpSorItem());
        model.addAttribute("bqList", returnList);
        super.buildModel(eventId, bqId, model, rftEvent, rfqEventSor);
        List<BqItemPojo> leveLOrderList = rfpSorService.getAllLevelOrderSorItemBySorId(bqId, null);

        model.addAttribute("leveLOrderList", leveLOrderList);
        long totalBqItemCount = rfpSorService.totalSorItemCountByBqId(bqId, null);
        model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
        model.addAttribute("totalBqItemCount", totalBqItemCount);
        model.addAttribute("event", rftEvent);
        LOG.info("totalBqItemCount :" + totalBqItemCount);
        return "createEventSor";
    }


    @RequestMapping(path = "/createEventSor", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<RfpSorItemResponsePojo> saveRftEventSor(@RequestBody BqItemPojo rftBqItemPojo, Model model) throws JsonProcessingException {

        try {
            if (StringUtils.checkString(rftBqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(rftBqItemPojo.getParent()).length() == 0) {
                HttpHeaders header = new HttpHeaders();
                header.add("error", messageSource.getMessage("event.sorsection.required", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<RfpSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
            }

            if (rftBqItemPojo.getQuantity() != null && rftBqItemPojo.getUnitPrice() != null) {
                HttpHeaders header = new HttpHeaders();
                java.math.BigDecimal unitPrice = rftBqItemPojo.getUnitPrice();
                java.math.BigDecimal qnty = rftBqItemPojo.getQuantity();
                if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
                    LOG.info("VALUE IS " + qnty.multiply(unitPrice));
                    header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[]{}, null));
                    return new ResponseEntity<RfpSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
                }
            }
            LOG.info("Crate EVENT BQ ID :: " + rftBqItemPojo.getBq());
            LOG.info(" Filter :" + rftBqItemPojo.getFilterVal() + " Search :" + rftBqItemPojo.getSearchVal());
            RfpSorItem rfqSorItem = new RfpSorItem();
            rfqSorItem.setId(rftBqItemPojo.getId());
            rfqSorItem.setRfxEvent(rfpEventService.getEventById(rftBqItemPojo.getRftEvent()));
            rfqSorItem.setSor(rfpSorService.getSorById(rftBqItemPojo.getBq()));
            RfpEvent rfqEvent = rfpEventService.getRfpEventByeventId(rftBqItemPojo.getRftEvent());
            model.addAttribute("event", rfqEvent);
            /*************** To check whether quantity of Bqitem entered should not be 0 ****************/
            if (rftBqItemPojo.getQuantity() != null) {
                LOG.info("rftBqItem.setBq***************" + rftBqItemPojo.getQuantity());
                HttpHeaders header = new HttpHeaders();

                java.math.BigDecimal i = rftBqItemPojo.getQuantity();
                java.math.BigDecimal c = java.math.BigDecimal.ZERO;
                int res = i.compareTo(c);
                if (res == 0) {
                    header.add("error", messageSource.getMessage("buyer.rftbq.quantity", new Object[]{rftBqItemPojo.getQuantity()}, Global.LOCALE));
                    return new ResponseEntity<RfpSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
                }
            }

            constructSorItemValues(rftBqItemPojo, rfqSorItem);
            if (StringUtils.checkString(rftBqItemPojo.getParent()).length() > 0) {
                rfqSorItem.setParent(rfpSorService.getSorItemsbySorId(StringUtils.checkString(rftBqItemPojo.getParent())));
            }
            Integer itemLevel = null;
            Integer itemOrder = null;
            if (StringUtils.checkString(rftBqItemPojo.getFilterVal()).length() == 1) {
                rftBqItemPojo.setFilterVal("");
            }
            if (StringUtils.checkString(rftBqItemPojo.getFilterVal()).length() > 0) {
                itemLevel = 0;
                itemOrder = 0;
                String[] values = rftBqItemPojo.getFilterVal().split("\\.");
                itemLevel = Integer.parseInt(values[0]);
                itemOrder = Integer.parseInt(values[1]);
            }

            // sending level order list and bq Item list bind in new Pojo class
            RfpSorItemResponsePojo bqItemResponsePojo = new RfpSorItemResponsePojo();

            if (!rfpSorService.isSorItemExists(rfqSorItem, rftBqItemPojo.getBq(), rftBqItemPojo.getParent())) {

                rfpSorService.saveSorItem(rfqSorItem);
                rftBqItemPojo.setStart(0);
                LOG.info(" Strat :" + rftBqItemPojo.getStart() + " lenth :" + rftBqItemPojo.getPageLength() + " Page Number :" + rftBqItemPojo.getPageNo());
                List<RfpSorItem> bqItemList = rfpSorService.getSorItemForSearchFilter(rftBqItemPojo.getBq(), itemLevel, itemOrder, rftBqItemPojo.getSearchVal(), rftBqItemPojo.getStart(), rftBqItemPojo.getPageLength(), rftBqItemPojo.getPageNo());

                List<BqItemPojo> leveLOrderList = rfpSorService.getAllLevelOrderSorItemBySorId(rftBqItemPojo.getBq(), rftBqItemPojo.getSearchVal());
                bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
                bqItemResponsePojo.setBqItemList(bqItemList);

                // List<RfqBqItem> bqList = rfqBqService.findBqbyBqId(rftBqItemPojo.getBq());
                HttpHeaders headers = new HttpHeaders();
                if(rftBqItemPojo != null && rftBqItemPojo.getUom() != null && rftBqItemPojo.getUom().length() > 1) {
                    headers.add("success", messageSource.getMessage("buyer.rftsoritem.save", new Object[]{}, Global.LOCALE));
                } else {
                    headers.add("success", messageSource.getMessage("buyer.rftsor.save", new Object[]{}, Global.LOCALE));
                }                return new ResponseEntity<RfpSorItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);

            } else {
                List<RfpSorItem> bqItemList = rfpSorService.getSorItemForSearchFilter(rftBqItemPojo.getBq(), itemLevel, itemOrder, rftBqItemPojo.getSearchVal(), rftBqItemPojo.getStart(), rftBqItemPojo.getPageLength(), rftBqItemPojo.getPageNo());

                List<BqItemPojo> leveLOrderList = rfpSorService.getAllLevelOrderSorItemBySorId(rftBqItemPojo.getBq(), rftBqItemPojo.getSearchVal());
                bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
                bqItemResponsePojo.setBqItemList(bqItemList);

                // List<RfqBqItem> bqList = rfqBqService.findBqbyBqId(rftBqItemPojo.getBq());
                LOG.info("Validate of BQ LIST :: " + rftBqItemPojo);
                HttpHeaders headers = new HttpHeaders();
                headers.add("error", messageSource.getMessage("buyer.rftbq.duplicate", new Object[]{rftBqItemPojo.getItemName()}, Global.LOCALE));
                return new ResponseEntity<RfpSorItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<RfpSorItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/getSorForEdit", method = RequestMethod.GET)
    public ResponseEntity<RfpSorItem> getSorForEdit(@RequestParam String bqId, @RequestParam String bqItemId) {
        RfpSorItem rftBqItem = null;
        LOG.info("BQ EDIT :: bqId" + bqId + " :: bqItemId :: " + bqItemId);
        try {
            rftBqItem = rfpSorService.getSorItemsbySorId(bqItemId);
            rftBqItem = rftBqItem.createShallowCopy();
        } catch (Exception e) {
            LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<RfpSorItem>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (rftBqItem != null) {
            return new ResponseEntity<RfpSorItem>(rftBqItem, HttpStatus.OK);
        } else {
            LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[]{}, Global.LOCALE));
            return new ResponseEntity<RfpSorItem>(null, headers, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/sorSaveDraft", method = RequestMethod.POST)
    public String saveSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId) {
        RfpEvent rftEvent = rfpEventService.getEventById(eventId);
        redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
        return "redirect:createSorList/" + eventId;
    }


    @RequestMapping(value = "/updateSorItem", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<RfpSorItemResponsePojo> updateBqItem(@RequestBody BqItemPojo bqItemPojo) throws JsonProcessingException {
        try {

            if (StringUtils.checkString(bqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(bqItemPojo.getParent()).length() == 0) {
                HttpHeaders header = new HttpHeaders();
                header.add("error", messageSource.getMessage("event.sorsection.required", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<RfpSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
            }

            if (bqItemPojo.getQuantity() != null && bqItemPojo.getUnitPrice() != null) {
                HttpHeaders header = new HttpHeaders();
                java.math.BigDecimal unitPrice = bqItemPojo.getUnitPrice();
                java.math.BigDecimal qnty = bqItemPojo.getQuantity();
                if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
                    LOG.info("VALUE IS " + qnty.multiply(unitPrice));
                    header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[]{}, null));
                    return new ResponseEntity<RfpSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
                }
            }

            if (bqItemPojo.getQuantity() != null && bqItemPojo.getUnitPrice() != null) {
                HttpHeaders header = new HttpHeaders();
                java.math.BigDecimal unitPrice = bqItemPojo.getUnitPrice();
                java.math.BigDecimal qnty = bqItemPojo.getQuantity();
                if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
                    LOG.info("VALUE IS " + qnty.multiply(unitPrice));
                    header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[]{}, null));
                    return new ResponseEntity<RfpSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
                }
            }

            LOG.info(" :: UPDATE BILL OF QUNATITY ::" + bqItemPojo);
            RfpSorItem rftBq = rfpSorService.getSorItemsbySorId(bqItemPojo.getId());

            rftBq.setUom(StringUtils.checkString(bqItemPojo.getUom()).length() > 0 ? uomService.getUombyCode(bqItemPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()) : null);
            super.buildUpdateBqItem(bqItemPojo, rftBq);

            RfpSorItem item = new RfpSorItem();
            item.setId(bqItemPojo.getId());
            item.setRfxEvent(rfpEventService.getEventById(bqItemPojo.getRftEvent()));
            item.setItemName(bqItemPojo.getItemName());

            /*************** To check whether quantity of Bqitem entered should not be 0 ****************/

            if (bqItemPojo.getQuantity() != null) {
                LOG.info("rftBqItem.setBq***************" + bqItemPojo.getQuantity());
                HttpHeaders header = new HttpHeaders();

                java.math.BigDecimal i = bqItemPojo.getQuantity();
                java.math.BigDecimal c = java.math.BigDecimal.ZERO;
                int res = i.compareTo(c);
                if (res == 0) {
                    header.add("error", messageSource.getMessage("buyer.rftbq.quantity", new Object[]{bqItemPojo.getQuantity()}, Global.LOCALE));
                    return new ResponseEntity<RfpSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
                }
            }

            constructBqItemValues(bqItemPojo, item);
            if (rfpSorService.isSorItemExists(item, bqItemPojo.getBq(), bqItemPojo.getParent())) {
                LOG.info("Duplicate....");
                throw new ApplicationException("Duplicate Schedule of Rate Item. Schedule of Rate Item by that name already exists.");
            }
            rfpSorService.updateSorItem(rftBq);
            // List<RfqBqItem> bqList = rfqBqService.findBqbyBqId(bqItemPojo.getBq());
            Integer itemLevel = null;
            Integer itemOrder = null;
            if (StringUtils.checkString(bqItemPojo.getFilterVal()).length() == 1) {
                bqItemPojo.setFilterVal("");
            }
            if (StringUtils.checkString(bqItemPojo.getFilterVal()).length() > 0) {
                itemLevel = 0;
                itemOrder = 0;
                String[] values = bqItemPojo.getFilterVal().split("\\.");
                itemLevel = Integer.parseInt(values[0]);
                itemOrder = Integer.parseInt(values[1]);
            }
            bqItemPojo.setStart(0);
            LOG.info(" bqItemPojo Page no :" + bqItemPojo.getPageNo());
            List<RfpSorItem> bqItemList = rfpSorService.getSorItemForSearchFilter(bqItemPojo.getBq(), itemLevel, itemOrder, bqItemPojo.getSearchVal(), bqItemPojo.getStart(), bqItemPojo.getPageLength(), bqItemPojo.getPageNo());

            // sending level order list and bq Item list bind in new Pojo class
            RfpSorItemResponsePojo bqItemResponsePojo = new RfpSorItemResponsePojo();

            List<BqItemPojo> leveLOrderList = rfpSorService.getAllLevelOrderSorItemBySorId(bqItemPojo.getBq(), null);
            bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
            bqItemResponsePojo.setBqItemList(bqItemList);

            // LOG.info("EVENT ALL LIST :: " + bqList.toString());
            HttpHeaders headers = new HttpHeaders();
            if(bqItemPojo != null && bqItemPojo.getUom() != null && bqItemPojo.getUom().length() > 1) {
                headers.add("success", messageSource.getMessage("buyer.rftsoritem.save", new Object[]{}, Global.LOCALE));
            } else {
                headers.add("success", messageSource.getMessage("buyer.rftsor.save", new Object[]{}, Global.LOCALE));
            }
            return new ResponseEntity<RfpSorItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Error while updating SOR Items to Buyer : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<RfpSorItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/getSorForNewFields", method = RequestMethod.GET)
    public ResponseEntity<RfpEventSor> getBqForNewFields(@RequestParam String bqId) {
        RfpEventSor eventBq = null;
        LOG.info("BQ EDIT :: bqId" + bqId);
        try {
            eventBq = rfpSorService.getSorById(bqId);
        } catch (Exception e) {
            LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<RfpEventSor>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (eventBq != null) {
            return new ResponseEntity<RfpEventSor>(eventBq, HttpStatus.OK);
        } else {
            LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[]{}, Global.LOCALE));
            return new ResponseEntity<RfpEventSor>(null, headers, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/sorItemTemplate/{bqId}", method = RequestMethod.GET)
    public void downloader(@PathVariable String bqId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            String downloadFolder = context.getRealPath("/WEB-INF/");
            String fileName = "SorItemTemplate.xlsx";
            Path file = Paths.get(downloadFolder, fileName);
            LOG.info("File Path ::" + file);

            super.eventDownloader(workbook, bqId, RfxTypes.RFP);

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

    private void buildBqItemForSearch(List<RfpSorItem> returnList, List<RfpSorItem> bqList) {
        RfpSorItem parent = null;
        for (RfpSorItem item : bqList) {
            if (item.getOrder() != 0) {
                if (parent != null) {
                    parent.getChildren().add(item.createSearchShallowCopy());
                }
            } else {
                parent = item.createSearchShallowCopy();
                parent.setChildren(new ArrayList<RfpSorItem>());
                returnList.add(parent);
            }
        }
    }

    @RequestMapping(value = "/uploadSorItems", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) throws IOException {
        LOG.info("UPLOADING STARTED ...... SOR Id :: " + bqId + "Event Id :: " + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
        List<RfpSorItem> bqItemList = null;
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
                super.validateUploadSorItems(file);
                try {
                    totalBqItemCount = super.sorItemsUpload(file, bqId, eventId, RfxTypes.RFP);
                    if (totalBqItemCount == 0) {
                        LOG.info("totalBqItemCount " + totalBqItemCount);
                        throw new Exception("Please fill data in excel file");
                    }

                } catch (Exception e) {
                    // e.printStackTrace();
                    // bqList = rfqBqService.findBqbyBqId(bqId);
                    bqItemList = rfpSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
                    for (RfpSorItem bqItem : bqItemList) {
                        if (bqItem.getUom() != null) {
                            bqItem.getUom().setCreatedBy(null);
                            bqItem.getUom().setModifiedBy(null);
                        }
                    }
                    headers.add("error", messageSource.getMessage("common.upload.notsupported.fail", new Object[]{e.getMessage()}, Global.LOCALE));
                    response.put("list", bqItemList);
                    response.put("totalBqItemCount", totalBqItemCount);
                    return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (MultipartException e) {
            bqItemList = rfpSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
            for (RfpSorItem bqItem : bqItemList) {
                if (bqItem.getUom() != null) {
                    bqItem.getUom().setCreatedBy(null);
                    bqItem.getUom().setModifiedBy(null);
                }
            }
            headers.add("error", messageSource.getMessage("buyer.rftbq.upload.notsupported.fail", new Object[]{e.getMessage()}, Global.LOCALE));
            response.put("list", bqItemList);
            response.put("totalBqItemCount", totalBqItemCount);
            return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.BAD_REQUEST);
        }
        // return "redirect:createEventBQ/" + eventId + "/" + bqId;
        // bqList = rfqBqService.findBqbyBqId(bqId);
        bqItemList = rfpSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
        for (RfpSorItem bqItem : bqItemList) {
            if (bqItem.getUom() != null) {
                bqItem.getUom().setCreatedBy(null);
                bqItem.getUom().setModifiedBy(null);
            }
        }
        RfpEventSor bq = rfpSorService.getRfpEventSorBySorId(bqId);
        SorPojo rfqBqColumns = new SorPojo(bq);
        response.put("rftBqColumns", rfqBqColumns);

        headers.add("success", messageSource.getMessage("buyer.rftsor.upload.success", new Object[]{}, Global.LOCALE));
        response.put("list", bqItemList);
        response.put("totalBqItemCount", totalBqItemCount);
        return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/sorPrevious", method = RequestMethod.POST)
    public String bqPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
        RfpEvent rftEvent = rfpEventService.getEventById(eventId);
        if (rftEvent != null) {
            model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
            return super.sorPrevious(rftEvent);
        } else {
            LOG.error("Event not found redirecting to login ");
            return "/";
        }
    }

    @RequestMapping(value = "/sorNext", method = RequestMethod.POST)
    public String bqNext(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
        RfpEvent event = rfpEventService.getEventById(eventId);
        if (event != null) {
            event.setSorCompleted(Boolean.TRUE);
            rfpEventService.updateEvent(event);
            return super.sorNext(event);
        } else {
            LOG.error("Event not found redirecting to login ");
            return "/";
        }
    }

    @RequestMapping(path = "/getEventSorbyId", method = RequestMethod.POST)
    public ResponseEntity<RfpEventSor> getEventBqbyId(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId) {
        HttpHeaders headers = new HttpHeaders();
        RfpEventSor bq = rfpSorService.getRfpEventSorBySorId(bqId);
        return new ResponseEntity<RfpEventSor>(bq, headers, HttpStatus.OK);
    }

    @RequestMapping(path = "/eventSorOrder", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfpSorItem>> eventBQOrder(@RequestBody BqItemPojo bqItemPojo) {
        LOG.info("rftBqItemPojo.getRfqEvent() :" + bqItemPojo.getRftEvent() + " Parent : " + bqItemPojo.getParent() + " Item Id : " + bqItemPojo.getId() + " New Position : " + bqItemPojo.getOrder());
        HttpHeaders headers = new HttpHeaders();
        List<RfpSorItem> bqList = null;
        Integer start = null;
        Integer length = null;
        try {
            if (StringUtils.checkString(bqItemPojo.getId()).length() > 0) {
                start = 0;
                length = bqItemPojo.getPageLength();
                LOG.info("Updating order..........................");
                RfpSorItem bqItem = rfpSorService.getSorItemsbySorId(bqItemPojo.getId());
                if (bqItem.getOrder() > 0 && StringUtils.checkString(bqItemPojo.getParent()).length() == 0) {
                    LOG.info("child cannot be a parent");
                    headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[]{}, Global.LOCALE));
                    List<RfpSorItem> returnList = rfpSorService.getSorItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
                    return new ResponseEntity<List<RfpSorItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
                }
                if (bqItem.getOrder() == 0 && StringUtils.checkString(bqItemPojo.getParent()).length() > 0) {
                    LOG.info("parent cannot be a child");
                    headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[]{}, Global.LOCALE));
                    List<RfpSorItem> returnList = rfpSorService.getSorItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
                    return new ResponseEntity<List<RfpSorItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
                }
                RfpSorItem item = new RfpSorItem();
                item.setId(bqItemPojo.getId());
                item.setRfxEvent(rfpEventService.getEventById(bqItemPojo.getRftEvent()));
                item.setItemName(bqItemPojo.getItemName());
                constructSorItemValues(bqItemPojo, item);
                if (bqItem.getParent() != null && !bqItem.getParent().getId().equals(bqItemPojo.getParent()) && rfpSorService.isSorItemExists(item, bqItemPojo.getBq(), bqItemPojo.getParent())) {
                    LOG.info("Duplicate....");
                    throw new NotAllowedException("Duplicate Sor Item. Sor Item by that name already exists.");
                }
                rfpSorService.reorderBqItems(bqItemPojo);
                bqList = rfpSorService.getSorItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
                headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<RfpSorItem>>(bqList, headers, HttpStatus.OK);
            }
        } catch (NotAllowedException e) {
            LOG.error("Error while moving SOR parent with item as Child : " + e.getMessage(), e);
            headers.add("error", messageSource.getMessage("buyer.rftbq.drag", new Object[]{e.getMessage()}, Global.LOCALE));
            List<RfpSorItem> returnList = rfpSorService.getSorItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
            return new ResponseEntity<List<RfpSorItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOG.error("Error while moving SOR Item : " + e.getMessage(), e);
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<List<RfpSorItem>>(null, headers, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<List<RfpSorItem>>(bqList, headers, HttpStatus.OK);
    }

    @RequestMapping(path = "/getSorItemForSearchFilter", method = RequestMethod.POST)
    public ResponseEntity<RfpSorItemResponsePojo> getBqItemForSearchFilter(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
        LOG.info(" getEventSorForChoosenFilterValue  : " + bqId + " eventId :" + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
        HttpHeaders headers = new HttpHeaders();
        List<RfpSorItem> bqItemList = null;
        // sending total SOR Item count and SOR Item list bind in new Pojo class
        RfpSorItemResponsePojo bqItemResponsePojo = new RfpSorItemResponsePojo();
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
            bqItemList = rfpSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
            bqItemResponsePojo.setBqItemList(bqItemList);
            long totalBqItemCount = rfpSorService.totalSorItemCountByBqId(bqId, searchVal);
            LOG.info("totalBqItemCount :" + totalBqItemCount);
            bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
            List<BqItemPojo> leveLOrderList = rfpSorService.getAllLevelOrderSorItemBySorId(bqId, searchVal);
            bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
            for (RfpSorItem bqItem : bqItemList) {
                if (bqItem.getUom() != null) {
                    bqItem.getUom().setCreatedBy(null);
                    bqItem.getUom().setModifiedBy(null);
                }
            }
            // Not sure about this method
//            if (pageLength != SecurityLibrary.getLoggedInUser().getBqPageLength()) {
//                userService.updateUserBqPageLength(pageLength, SecurityLibrary.getLoggedInUser().getId());
//                super.updateSecurityLibraryUser(pageLength);
//            }
            return new ResponseEntity<RfpSorItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error during Search Reset Schedule Of rate :" + e.getMessage(), e);
            headers.add("error", "Error during Search Schedule Of rate  ");
            return new ResponseEntity<RfpSorItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/updateEventSorOrder/{eventId}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfpEventSor>> updateEventBqOrder(@RequestBody String[] sorIds, @PathVariable("eventId") String eventId) {
        try {
            LOG.info("RFQ eventId : " + eventId);
            if (sorIds != null && sorIds.length > 0) {
                Integer count = 1;
                for (String cqId : sorIds) {
                    RfpEventSor rfqEventSor = rfpSorService.getSorById(cqId);
                    rfqEventSor.setSorOrder(count);
                    rfpSorService.saveRfpSor(rfqEventSor);
                    count++;
                }
                List<RfpEventSor> rfqEventSorList = rfpSorService.getAllSorListByEventIdByOrder(eventId);
                HttpHeaders headers = new HttpHeaders();
                // Need to change message here
                headers.add("success", messageSource.getMessage("rft.sor.order", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<RfpEventSor>>(rfqEventSorList, headers, HttpStatus.OK);
            } else {
                List<RfpEventSor> rfqEventSorList = rfpSorService.getAllSorListByEventIdByOrder(eventId);
                HttpHeaders headers = new HttpHeaders();
                // Need to change message here
                headers.add("error", messageSource.getMessage("rft.sor.order.empty", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<RfpEventSor>>(rfqEventSorList, headers, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error while Ordering Sor : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            // Need to change message here
            headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<List<RfpEventSor>>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/deleteRftSor", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfpEventSor>> deleteRftBq(@RequestBody BqPojo rftBqPojo) throws JsonProcessingException {
        RfpEventSor sorEvent = rfpSorService.getSorById(rftBqPojo.getId());
        try {
            try {

                rfpSorService.isAllowtoDeleteBQ(rftBqPojo.getId(), RfxTypes.RFP);
            } catch (Exception e) {
                LOG.error("Error while deleting SOR  :" + e.getMessage(), e);
                HttpHeaders headers = new HttpHeaders();
                headers.add("error", e.getMessage());
                return new ResponseEntity<List<RfpEventSor>>(null, headers, HttpStatus.EXPECTATION_FAILED);
            }

            rfpSorService.deleteRfpSor(sorEvent.getId());
            LOG.info(" After delete SOR display remaining eventID :: " + rftBqPojo.getEventId());
            List<RfpEventSor> bqList = rfpSorService.getAllSorListByEventIdByOrder(rftBqPojo.getEventId());
            Integer count = 1;
            if (CollectionUtil.isNotEmpty(bqList)) {
                for (RfpEventSor bq : bqList) {
                    bq.setSorOrder(count);
                    rfpSorService.updateSor(bq);
                    count++;
                }
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("success", messageSource.getMessage("rft.sor.success.delete", new Object[]{sorEvent.getName()}, Global.LOCALE));
            return new ResponseEntity<List<RfpEventSor>>(bqList, headers, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error while deleting SOR  [ " + sorEvent.getName() + " ]" + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("rft.sor.error.delete", new Object[]{sorEvent.getName()}, Global.LOCALE));
            return new ResponseEntity<List<RfpEventSor>>(null, headers, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/updateSorList", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfpEventSor>> updateBqList(@RequestBody SorPojo rfaBqPojo) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        try {
            LOG.info("UPDATE RFP SOR :: " + rfaBqPojo.toString());
            RfpEventSor rfaBq = rfpSorService.getRfpEventSorBySorId(rfaBqPojo.getId());
            rfaBq.setName(rfaBqPojo.getSorName());
            rfaBq.setDescription(rfaBqPojo.getSorDesc());
            rfaBq.setModifiedDate(new Date());
            if (sorValidate(rfaBqPojo)) {
                rfpSorService.updateSor(rfaBq);
                List<RfpEventSor> bqList = rfpSorService.getAllSorListByEventIdByOrder(rfaBqPojo.getEventId());
                headers.add("success", messageSource.getMessage("rft.sor.update", new Object[] {}, Global.LOCALE));
                return new ResponseEntity<List<RfpEventSor>>(bqList, headers, HttpStatus.OK);
            } else {
                List<RfpEventSor> bqList = rfpSorService.getAllSorListByEventIdByOrder(rfaBqPojo.getEventId());
                headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[] { rfaBqPojo.getSorName() }, Global.LOCALE));
                return new ResponseEntity<List<RfpEventSor>>(bqList, headers, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error while adding SOR Items to Buyer : " + e.getMessage(), e);
            headers.add("error", messageSource.getMessage("rft.sor.error", new Object[] { e.getMessage() }, Global.LOCALE));
            return new ResponseEntity<List<RfpEventSor>>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(path = "/addNewColumnsSor", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfpSorItem>> addNewColumns(@RequestBody SorPojo rfqBq) throws JsonProcessingException {
        LOG.info("Enter this Block :: " + rfqBq.toString());
        HttpHeaders headers = new HttpHeaders();
        List<RfpSorItem> bqItemList = null;
        if (rfqBq.getField1Label() == null && rfqBq.getField2Label() == null && rfqBq.getField3Label() == null && rfqBq.getField4Label() == null) {
            headers.add("error", "Field cannot be empty");
            return new ResponseEntity<List<RfpSorItem>>(null, headers, HttpStatus.BAD_REQUEST);
        } else {
            RfpEventSor rftEventBq = rfpSorService.getSorById(rfqBq.getId());
            if (rftEventBq != null) {
                try {

                    List<String> fieldLabelList = new ArrayList<String>();
                    if (rfqBq.getField1Label() != null && fieldLabelList.contains(rfqBq.getField1Label().toLowerCase())) {
                        headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField1Label() }, Global.LOCALE));
                        return new ResponseEntity<List<RfpSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    } else if (rfqBq.getField1Label() != null) {
                        fieldLabelList.add(rfqBq.getField1Label().toLowerCase());
                    }
                    if (rfqBq.getField2Label() != null && fieldLabelList.contains(rfqBq.getField2Label().toLowerCase())) {
                        headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField2Label() }, Global.LOCALE));
                        return new ResponseEntity<List<RfpSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    } else if (rfqBq.getField2Label() != null) {
                        fieldLabelList.add(rfqBq.getField2Label().toLowerCase());
                    }
                    if (rfqBq.getField3Label() != null && fieldLabelList.contains(rfqBq.getField3Label().toLowerCase())) {
                        headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField3Label() }, Global.LOCALE));
                        return new ResponseEntity<List<RfpSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    } else if (rfqBq.getField3Label() != null) {
                        fieldLabelList.add(rfqBq.getField3Label().toLowerCase());
                    }
                    if (rfqBq.getField4Label() != null && fieldLabelList.contains(rfqBq.getField4Label().toLowerCase())) {
                        headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField4Label() }, Global.LOCALE));
                        return new ResponseEntity<List<RfpSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    } else if (rfqBq.getField4Label() != null) {
                        fieldLabelList.add(rfqBq.getField4Label().toLowerCase());
                    }

                    super.buildAddNewColumns(rfqBq, rftEventBq);
                    rfpSorService.updateRfpSorFields(rftEventBq);

                    Integer itemLevel = null;
                    Integer itemOrder = null;
                    Integer start = null;
                    Integer length = null;
                    if (StringUtils.checkString(rfqBq.getFilterVal()).length() == 1) {
                        rfqBq.setFilterVal("");
                    }
                    if (StringUtils.checkString(rfqBq.getFilterVal()).length() > 0) {
                        itemLevel = 0;
                        itemOrder = 0;
                        String[] values = rfqBq.getFilterVal().split("\\.");
                        itemLevel = Integer.parseInt(values[0]);
                        itemOrder = Integer.parseInt(values[1]);
                    }
                    start = 0;
                    length = rfqBq.getPageLength();

                    // bqList = rfqBqService.findBqbyBqId(rftEventBq.getId());
                    bqItemList = rfpSorService.getSorItemForSearchFilter(rftEventBq.getId(), itemLevel, itemOrder, rfqBq.getSearchVal(), start, length, rfqBq.getPageNo());
                    for (RfpSorItem bqItem : bqItemList) {
                        if (bqItem.getUom() != null) {
                            bqItem.getUom().setCreatedBy(null);
                            bqItem.getUom().setModifiedBy(null);
                        }
                    }
                    headers.add("success", messageSource.getMessage("rft.sor.newfields.success", new Object[] {}, Global.LOCALE));
                    return new ResponseEntity<List<RfpSorItem>>(bqItemList, headers, HttpStatus.OK);
                } catch (Exception e) {
                    headers.add("error", messageSource.getMessage("buyer.rftbq.newfields.error", new Object[] {}, Global.LOCALE));
                    return new ResponseEntity<List<RfpSorItem>>(null, headers, HttpStatus.BAD_REQUEST);
                }
            }
        }
        return new ResponseEntity<List<RfpSorItem>>(bqItemList, headers, HttpStatus.OK);
    }

    @RequestMapping(path = "/deleteSorNewField", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<List<RfpSorItem>> deleteBqNewField(@RequestParam("bqId") String bqId, @RequestParam("label") String label, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
        LOG.info("deleteSorNewField label ::" + label + " bqId :: " + bqId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
        HttpHeaders headers = new HttpHeaders();
        List<RfpSorItem> bqItemList = null;
        try {
            if (StringUtils.checkString(label).length() > 0) {
                rfpSorService.deletefieldInSorItems(bqId, label);
                // bqList = rfqBqService.findBqbyBqId(bqId);

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
                bqItemList = rfpSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
                for (RfpSorItem bqItem : bqItemList) {
                    if (bqItem.getUom() != null) {
                        bqItem.getUom().setCreatedBy(null);
                        bqItem.getUom().setModifiedBy(null);
                    }
                }
                headers.add("success", "Column deleted Successfully");
            }
        } catch (Exception e) {
            LOG.error("Error while deleting SOR New Field " + e.getMessage(), e);
            headers.add("error", "Error while deleting SOR New Field : " + e.getMessage());
            return new ResponseEntity<List<RfpSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<RfpSorItem>>(bqItemList, headers, HttpStatus.OK);
    }

    @RequestMapping(path = "/deleteSorItems/{bqId}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<RfpSorItemResponsePojo> deleteBqItems(@RequestParam(name = "items", required = true) String items, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo, @PathVariable("bqId") String bqId, @RequestParam(name = "allDelete", required = false) boolean allDelete) throws JsonProcessingException {
        LOG.info("DELETE SOR ITEMS IDs :: " + items);
        List<RfpSorItem> bqItems = null;
        HttpHeaders headers = new HttpHeaders();

        if (StringUtils.checkString(filterVal).length() > 0) {
            if (filterVal.contains(".")) {
                filterVal = "";
            }
        }
        // sending level order list and bq Item list bind in new Pojo class
        RfpSorItemResponsePojo bqItemResponsePojo = new RfpSorItemResponsePojo();

        if (StringUtils.checkString(items).length() > 0) {
            try {
                String[] bqItemsArr = items.split(",");

                LOG.info(" filterVal  :" + filterVal + " searchVal: " + searchVal + "=====>" + allDelete);

                if (allDelete) {
                    rfpSorService.deleteAllSorItems(bqId);
                } else {
                    rfpSorService.deleteSorItems(bqItemsArr, bqId);
                }
                headers.add("success", messageSource.getMessage("rft.soritems.success.delete", new Object[] {}, Global.LOCALE));

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

                bqItems = rfpSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
                List<BqItemPojo> leveLOrderList = rfpSorService.getAllLevelOrderSorItemBySorId(bqId, searchVal);
                bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
                bqItemResponsePojo.setBqItemList(bqItems);

            } catch (NotAllowedException e) {
                LOG.error("Not Allowed Exception: " + e.getMessage(), e);
                headers.add("error", e.getMessage());
                return new ResponseEntity<RfpSorItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception e) {
                LOG.error("Error while delete sor items: " + e.getMessage(), e);
                headers.add("error", "Error during delete : " + messageSource.getMessage("rft.soritems.error.delete", new Object[] {}, Global.LOCALE));
                return new ResponseEntity<RfpSorItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<RfpSorItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
    }

}
