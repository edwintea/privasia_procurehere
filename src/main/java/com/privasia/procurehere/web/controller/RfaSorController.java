package com.privasia.procurehere.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.core.entity.RfaSorItem;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.RfaSorItemResponsePojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.UserService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletContext;
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
@RequestMapping("/buyer/RFA")
public class RfaSorController extends EventSorBase {

    public RfaSorController() {
        super(RfxTypes.RFA);
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
        RfaEvent rfaEvent = rfaEventService.getRfaEventById(eventId);
        List<RfaEventSor> sorEventList = rfaSorService.getAllSorListByEventIdByOrder(eventId);
        model.addAttribute("event", rfaEvent);
        model.addAttribute("sorEventList", sorEventList);
        model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
        return "createSorList";
    }

    @RequestMapping(value = "/sorSaveDraft", method = RequestMethod.POST)
    public String saveSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId) {
        RfaEvent rftEvent = rfaEventService.getRfaEventById(eventId);
        redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
        return "redirect:createSorList/" + eventId;
    }


    @RequestMapping(path = "/createSorList", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<Sor>> saveSorList(@RequestBody SorPojo sorPojo) throws JsonProcessingException {
        try {

            if (!rfaSorService.isSorExists(sorPojo)) {
                List<RfaEventSor> sorList = rfaSorService.getAllSorListByEventIdByOrder(sorPojo.getEventId());
                Integer count = 1;
                if (CollectionUtil.isNotEmpty(sorList)) {
                    for (RfaEventSor rfqEventSor : sorList) {
                        if (rfqEventSor.getSorOrder() == null) {
                            rfqEventSor.setSorOrder(count);
                            rfaSorService.updateSor(rfqEventSor);
                            count++;
                        }
                    }
                    count = sorList.size();
                    count++;
                }
                super.saveSor(sorPojo, count);
                List<Sor> sorList1 = rfaSorService.findSorbyEventId(sorPojo.getEventId());
                HttpHeaders headers = new HttpHeaders();
                // Need to change message here
                headers.add("success", messageSource.getMessage("rft.sor.save", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<Sor>>(sorList1, headers, HttpStatus.OK);
            } else {
                List<Sor> sorList = rfaSorService.findSorbyEventId(sorPojo.getEventId());
                HttpHeaders headers = new HttpHeaders();
                // Need to change message here
                headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[]{sorPojo.getSorName()}, Global.LOCALE));
                return new ResponseEntity<List<Sor>>(sorList, headers, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            LOG.error("Error while adding SOR Items to Buyer : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("rft.sor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<List<Sor>>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/showSorItems", method = RequestMethod.POST)
    public String showBqItems(Model model, @RequestParam(name = "bqId", required = true) String bqId, @RequestParam(name = "eventId", required = true) String eventId) {
        RfaEvent rftEvent = rfaEventService.getRfaEventById(eventId);
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

        RfaEvent rftEvent = rfaEventService.getRfaEventById(eventId);
        RfaEventSor rfqEventSor = rfaSorService.getSorById(bqId);

        List<RfaSorItem> returnList = new ArrayList<>();
        length = 0;//SecurityLibrary.getLoggedInUser().getBqPageLength();
        LOG.info(" SOR Page length : " + length);
        List<RfaSorItem> bqList = rfaSorService.getSorItemForSearchFilter(bqId, null, null, null, start, length, pageNo);


        buildBqItemForSearch(returnList, bqList);
        // List<RfqBqItem> bqList = rfqBqService.findBqbyBqId(bqId);
        model.addAttribute("rftBqItem", new RfaSorItem());
        model.addAttribute("bqList", returnList);
        super.buildModel(eventId, bqId, model, rftEvent, rfqEventSor);
        List<BqItemPojo> leveLOrderList = rfaSorService.getAllLevelOrderSorItemBySorId(bqId, null);

        model.addAttribute("leveLOrderList", leveLOrderList);
        long totalBqItemCount = rfaSorService.totalSorItemCountByBqId(bqId, null);
        model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
        model.addAttribute("totalBqItemCount", totalBqItemCount);
        model.addAttribute("event", rftEvent);
        LOG.info("totalBqItemCount :" + totalBqItemCount);
        return "createEventSor";
    }

    @RequestMapping(path = "/createEventSor", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<RfaSorItemResponsePojo> saveRftEventSor(@RequestBody BqItemPojo rftBqItemPojo, Model model) throws JsonProcessingException {

        try {
            if (StringUtils.checkString(rftBqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(rftBqItemPojo.getParent()).length() == 0) {
                HttpHeaders header = new HttpHeaders();
                header.add("error", messageSource.getMessage("event.sorsection.required", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<RfaSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
            }

            if (rftBqItemPojo.getQuantity() != null && rftBqItemPojo.getUnitPrice() != null) {
                HttpHeaders header = new HttpHeaders();
                java.math.BigDecimal unitPrice = rftBqItemPojo.getUnitPrice();
                java.math.BigDecimal qnty = rftBqItemPojo.getQuantity();
                if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
                    LOG.info("VALUE IS " + qnty.multiply(unitPrice));
                    header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[]{}, null));
                    return new ResponseEntity<RfaSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
                }
            }
            LOG.info("Crate EVENT SOR ID :: " + rftBqItemPojo.getBq());
            LOG.info(" Filter :" + rftBqItemPojo.getFilterVal() + " Search :" + rftBqItemPojo.getSearchVal());
            RfaSorItem rfqSorItem = new RfaSorItem();
            rfqSorItem.setId(rftBqItemPojo.getId());
            rfqSorItem.setRfxEvent(rfaEventService.getRfaEventById(rftBqItemPojo.getRftEvent()));
            rfqSorItem.setSor(rfaSorService.getSorById(rftBqItemPojo.getBq()));
            RfaEvent rfqEvent = rfaEventService.getRfaEventById(rftBqItemPojo.getRftEvent());
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
                    return new ResponseEntity<RfaSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
                }
            }

            constructSorItemValues(rftBqItemPojo, rfqSorItem);
            if (StringUtils.checkString(rftBqItemPojo.getParent()).length() > 0) {
                rfqSorItem.setParent(rfaSorService.getSorItemsbySorId(StringUtils.checkString(rftBqItemPojo.getParent())));
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
            RfaSorItemResponsePojo bqItemResponsePojo = new RfaSorItemResponsePojo();

            if (!rfaSorService.isSorItemExists(rfqSorItem, rftBqItemPojo.getBq(), rftBqItemPojo.getParent())) {

                rfaSorService.saveSorItem(rfqSorItem);
                rftBqItemPojo.setStart(0);
                LOG.info(" Strat :" + rftBqItemPojo.getStart() + " lenth :" + rftBqItemPojo.getPageLength() + " Page Number :" + rftBqItemPojo.getPageNo());
                List<RfaSorItem> bqItemList = rfaSorService.getSorItemForSearchFilter(rftBqItemPojo.getBq(), itemLevel, itemOrder, rftBqItemPojo.getSearchVal(), rftBqItemPojo.getStart(), rftBqItemPojo.getPageLength(), rftBqItemPojo.getPageNo());

                List<BqItemPojo> leveLOrderList = rfaSorService.getAllLevelOrderSorItemBySorId(rftBqItemPojo.getBq(), rftBqItemPojo.getSearchVal());
                bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
                bqItemResponsePojo.setBqItemList(bqItemList);

                // List<RfqBqItem> bqList = rfqBqService.findBqbyBqId(rftBqItemPojo.getBq());
                HttpHeaders headers = new HttpHeaders();
                if(rftBqItemPojo != null && rftBqItemPojo.getUom() != null && rftBqItemPojo.getUom().length() > 1) {
                    headers.add("success", messageSource.getMessage("buyer.rftsoritem.save", new Object[]{}, Global.LOCALE));
                } else {
                    headers.add("success", messageSource.getMessage("buyer.rftsor.save", new Object[]{}, Global.LOCALE));
                }                return new ResponseEntity<RfaSorItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);

            } else {
                List<RfaSorItem> bqItemList = rfaSorService.getSorItemForSearchFilter(rftBqItemPojo.getBq(), itemLevel, itemOrder, rftBqItemPojo.getSearchVal(), rftBqItemPojo.getStart(), rftBqItemPojo.getPageLength(), rftBqItemPojo.getPageNo());

                List<BqItemPojo> leveLOrderList = rfaSorService.getAllLevelOrderSorItemBySorId(rftBqItemPojo.getBq(), rftBqItemPojo.getSearchVal());
                bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
                bqItemResponsePojo.setBqItemList(bqItemList);

                // List<RfqBqItem> bqList = rfqBqService.findBqbyBqId(rftBqItemPojo.getBq());
                LOG.info("Validate of SOR LIST :: " + rftBqItemPojo);
                HttpHeaders headers = new HttpHeaders();
                headers.add("error", messageSource.getMessage("buyer.rftbq.duplicate", new Object[]{rftBqItemPojo.getItemName()}, Global.LOCALE));
                return new ResponseEntity<RfaSorItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<RfaSorItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/getSorItemForSearchFilter", method = RequestMethod.POST)
    public ResponseEntity<RfaSorItemResponsePojo> getBqItemForSearchFilter(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
        LOG.info(" getEventSorForChoosenFilterValue  : " + bqId + " eventId :" + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
        HttpHeaders headers = new HttpHeaders();
        List<RfaSorItem> bqItemList = null;
        // sending total SOR Item count and SOR Item list bind in new Pojo class
        RfaSorItemResponsePojo bqItemResponsePojo = new RfaSorItemResponsePojo();
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
            bqItemList = rfaSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
            bqItemResponsePojo.setBqItemList(bqItemList);
            long totalBqItemCount = rfaSorService.totalSorItemCountByBqId(bqId, searchVal);
            LOG.info("totalSorItemCount :" + totalBqItemCount);
            bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
            List<BqItemPojo> leveLOrderList = rfaSorService.getAllLevelOrderSorItemBySorId(bqId, searchVal);
            bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
            for (RfaSorItem bqItem : bqItemList) {
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
            return new ResponseEntity<RfaSorItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error during Search Reset Schedule Of rate :" + e.getMessage(), e);
            headers.add("error", "Error during Search Schedule Of rate  ");
            return new ResponseEntity<RfaSorItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/sorNext", method = RequestMethod.POST)
    public String bqNext(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
        RfaEvent event = rfaEventService.getRfaEventById(eventId);
        if (event != null) {
            event.setSorCompleted(Boolean.TRUE);
            rfaEventService.updateRfaEvent(event);
            return super.sorNext(event);
        } else {
            LOG.error("Event not found redirecting to login ");
            return "/";
        }
    }

    @RequestMapping(value = "/sorPrevious", method = RequestMethod.POST)
    public String bqPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
        RfaEvent rftEvent = rfaEventService.getRfaEventById(eventId);
        if (rftEvent != null) {
            model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
            return super.sorPrevious(rftEvent);
        } else {
            LOG.error("Event not found redirecting to login ");
            return "/";
        }
    }

    @RequestMapping(path = "/getSorForNewFields", method = RequestMethod.GET)
    public ResponseEntity<RfaEventSor> getSorForNewFields(@RequestParam String bqId) {
        RfaEventSor eventBq = null;
        LOG.info("BQ EDIT :: bqId" + bqId);
        try {
            eventBq = rfaSorService.getSorById(bqId);
        } catch (Exception e) {
            LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<RfaEventSor>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (eventBq != null) {
            return new ResponseEntity<RfaEventSor>(eventBq, HttpStatus.OK);
        } else {
            LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[]{}, Global.LOCALE));
            return new ResponseEntity<RfaEventSor>(null, headers, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(path = "/getSorForEdit", method = RequestMethod.GET)
    public ResponseEntity<RfaSorItem> getSorForEdit(@RequestParam String bqId, @RequestParam String bqItemId) {
        RfaSorItem rftBqItem = null;
        LOG.info("BQ EDIT :: bqId" + bqId + " :: bqItemId :: " + bqItemId);
        try {
            rftBqItem = rfaSorService.getSorItemsbySorId(bqItemId);
            rftBqItem = rftBqItem.createShallowCopy();
        } catch (Exception e) {
            LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<RfaSorItem>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (rftBqItem != null) {
            return new ResponseEntity<RfaSorItem>(rftBqItem, HttpStatus.OK);
        } else {
            LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[]{}, Global.LOCALE));
            return new ResponseEntity<RfaSorItem>(null, headers, HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/updateSorItem", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<RfaSorItemResponsePojo> updateBqItem(@RequestBody BqItemPojo bqItemPojo) throws JsonProcessingException {
        try {

            if (StringUtils.checkString(bqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(bqItemPojo.getParent()).length() == 0) {
                HttpHeaders header = new HttpHeaders();
                header.add("error", messageSource.getMessage("event.sorsection.required", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<RfaSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
            }

            if (bqItemPojo.getQuantity() != null && bqItemPojo.getUnitPrice() != null) {
                HttpHeaders header = new HttpHeaders();
                java.math.BigDecimal unitPrice = bqItemPojo.getUnitPrice();
                java.math.BigDecimal qnty = bqItemPojo.getQuantity();
                if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
                    LOG.info("VALUE IS " + qnty.multiply(unitPrice));
                    header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[]{}, null));
                    return new ResponseEntity<RfaSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
                }
            }

            if (bqItemPojo.getQuantity() != null && bqItemPojo.getUnitPrice() != null) {
                HttpHeaders header = new HttpHeaders();
                java.math.BigDecimal unitPrice = bqItemPojo.getUnitPrice();
                java.math.BigDecimal qnty = bqItemPojo.getQuantity();
                if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
                    LOG.info("VALUE IS " + qnty.multiply(unitPrice));
                    header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[]{}, null));
                    return new ResponseEntity<RfaSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
                }
            }

            LOG.info(" :: UPDATE SCHEDULE OF RATE ::" + bqItemPojo);
            RfaSorItem rftBq = rfaSorService.getSorItemsbySorId(bqItemPojo.getId());

            rftBq.setUom(StringUtils.checkString(bqItemPojo.getUom()).length() > 0 ? uomService.getUombyCode(bqItemPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()) : null);
            super.buildUpdateBqItem(bqItemPojo, rftBq);

            RfaSorItem item = new RfaSorItem();
            item.setId(bqItemPojo.getId());
            item.setRfxEvent(rfaEventService.getRfaEventById(bqItemPojo.getRftEvent()));
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
                    return new ResponseEntity<RfaSorItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
                }
            }

            constructBqItemValues(bqItemPojo, item);
            if (rfaSorService.isSorItemExists(item, bqItemPojo.getBq(), bqItemPojo.getParent())) {
                LOG.info("Duplicate....");
                throw new ApplicationException("Duplicate SOR Item. SOR Item by that name already exists.");
            }
            rfaSorService.updateSorItem(rftBq);
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
            List<RfaSorItem> bqItemList = rfaSorService.getSorItemForSearchFilter(bqItemPojo.getBq(), itemLevel, itemOrder, bqItemPojo.getSearchVal(), bqItemPojo.getStart(), bqItemPojo.getPageLength(), bqItemPojo.getPageNo());

            // sending level order list and bq Item list bind in new Pojo class
            RfaSorItemResponsePojo bqItemResponsePojo = new RfaSorItemResponsePojo();

            List<BqItemPojo> leveLOrderList = rfaSorService.getAllLevelOrderSorItemBySorId(bqItemPojo.getBq(), null);
            bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
            bqItemResponsePojo.setBqItemList(bqItemList);

            // LOG.info("EVENT ALL LIST :: " + bqList.toString());
            HttpHeaders headers = new HttpHeaders();
            if(bqItemPojo != null && bqItemPojo.getUom() != null && bqItemPojo.getUom().length() > 1) {
                headers.add("success", messageSource.getMessage("buyer.rftsoritem.save", new Object[]{}, Global.LOCALE));
            } else {
                headers.add("success", messageSource.getMessage("buyer.rftsor.save", new Object[]{}, Global.LOCALE));
            }
            return new ResponseEntity<RfaSorItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Error while updating SOR Items to Buyer : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<RfaSorItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(path = "/eventSorOrder", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfaSorItem>> eventBQOrder(@RequestBody BqItemPojo bqItemPojo) {
        LOG.info("rftBqItemPojo.getRfqEvent() :" + bqItemPojo.getRftEvent() + " Parent : " + bqItemPojo.getParent() + " Item Id : " + bqItemPojo.getId() + " New Position : " + bqItemPojo.getOrder());
        HttpHeaders headers = new HttpHeaders();
        List<RfaSorItem> bqList = null;
        Integer start = null;
        Integer length = null;
        try {
            if (StringUtils.checkString(bqItemPojo.getId()).length() > 0) {
                start = 0;
                length = bqItemPojo.getPageLength();
                LOG.info("Updating order..........................");
                RfaSorItem bqItem = rfaSorService.getSorItemsbySorId(bqItemPojo.getId());
                if (bqItem.getOrder() > 0 && StringUtils.checkString(bqItemPojo.getParent()).length() == 0) {
                    LOG.info("child cannot be a parent");
                    headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[]{}, Global.LOCALE));
                    List<RfaSorItem> returnList = rfaSorService.getSorItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
                    return new ResponseEntity<List<RfaSorItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
                }
                if (bqItem.getOrder() == 0 && StringUtils.checkString(bqItemPojo.getParent()).length() > 0) {
                    LOG.info("parent cannot be a child");
                    headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[]{}, Global.LOCALE));
                    List<RfaSorItem> returnList = rfaSorService.getSorItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
                    return new ResponseEntity<List<RfaSorItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
                }
                RfaSorItem item = new RfaSorItem();
                item.setId(bqItemPojo.getId());
                item.setRfxEvent(rfaEventService.getRfaEventById(bqItemPojo.getRftEvent()));
                item.setItemName(bqItemPojo.getItemName());
                constructSorItemValues(bqItemPojo, item);
                if (bqItem.getParent() != null && !bqItem.getParent().getId().equals(bqItemPojo.getParent()) && rfaSorService.isSorItemExists(item, bqItemPojo.getBq(), bqItemPojo.getParent())) {
                    LOG.info("Duplicate....");
                    throw new NotAllowedException("Duplicate Sor Item. Sor Item by that name already exists.");
                }
                rfaSorService.reorderBqItems(bqItemPojo);
                bqList = rfaSorService.getSorItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
                headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<RfaSorItem>>(bqList, headers, HttpStatus.OK);
            }
        } catch (NotAllowedException e) {
            LOG.error("Error while moving SOR parent with item as Child : " + e.getMessage(), e);
            headers.add("error", messageSource.getMessage("buyer.rftbq.drag", new Object[]{e.getMessage()}, Global.LOCALE));
            List<RfaSorItem> returnList = rfaSorService.getSorItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
            return new ResponseEntity<List<RfaSorItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOG.error("Error while moving SOR Item : " + e.getMessage(), e);
            headers.add("error", messageSource.getMessage("buyer.rftsor.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<List<RfaSorItem>>(null, headers, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<List<RfaSorItem>>(bqList, headers, HttpStatus.OK);
    }


    @RequestMapping(path = "/sorItemTemplate/{bqId}", method = RequestMethod.GET)
    public void downloader(@PathVariable String bqId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            String downloadFolder = context.getRealPath("/WEB-INF/");
            String fileName = "SorItemTemplate.xlsx";
            Path file = Paths.get(downloadFolder, fileName);
            LOG.info("File Path ::" + file);

            super.eventDownloader(workbook, bqId, RfxTypes.RFA);

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


    @RequestMapping(value = "/uploadSorItems", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) throws IOException {
        LOG.info("UPLOADING STARTED ...... SOR Id :: " + bqId + "Event Id :: " + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
        List<RfaSorItem> bqItemList = null;
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
                    totalBqItemCount = super.sorItemsUpload(file, bqId, eventId, RfxTypes.RFA);
                    if (totalBqItemCount == 0) {
                        LOG.info("totalSorItemCount " + totalBqItemCount);
                        throw new Exception("Please fill data in excel file");
                    }

                } catch (Exception e) {
                    // e.printStackTrace();
                    // bqList = rfqBqService.findBqbyBqId(bqId);
                    bqItemList = rfaSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
                    for (RfaSorItem bqItem : bqItemList) {
                        if (bqItem.getUom() != null) {
                            bqItem.getUom().setCreatedBy(null);
                            bqItem.getUom().setModifiedBy(null);
                        }
                    }
                    headers.add("error", messageSource.getMessage("common.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
                    response.put("list", bqItemList);
                    response.put("totalBqItemCount", totalBqItemCount);
                    return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (MultipartException e) {
            bqItemList = rfaSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
            for (RfaSorItem bqItem : bqItemList) {
                if (bqItem.getUom() != null) {
                    bqItem.getUom().setCreatedBy(null);
                    bqItem.getUom().setModifiedBy(null);
                }
            }
            headers.add("error", messageSource.getMessage("buyer.rftbq.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
            response.put("list", bqItemList);
            response.put("totalBqItemCount", totalBqItemCount);
            return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.BAD_REQUEST);
        }
        // return "redirect:createEventBQ/" + eventId + "/" + bqId;
        // bqList = rfqBqService.findBqbyBqId(bqId);
        bqItemList = rfaSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
        for (RfaSorItem bqItem : bqItemList) {
            if (bqItem.getUom() != null) {
                bqItem.getUom().setCreatedBy(null);
                bqItem.getUom().setModifiedBy(null);
            }
        }
        RfaEventSor bq = rfaSorService.getRfaEventSorBySorId(bqId);
        SorPojo rfqBqColumns = new SorPojo(bq);
        response.put("rftBqColumns", rfqBqColumns);

        headers.add("success", messageSource.getMessage("buyer.rftsor.upload.success", new Object[] {}, Global.LOCALE));
        response.put("list", bqItemList);
        response.put("totalBqItemCount", totalBqItemCount);
        return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.OK);
    }

    @RequestMapping(path = "/getEventSorbyId", method = RequestMethod.POST)
    public ResponseEntity<RfaEventSor> getEventBqbyId(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId) {
        HttpHeaders headers = new HttpHeaders();
        RfaEventSor bq = rfaSorService.getRfaEventSorBySorId(bqId);
        return new ResponseEntity<RfaEventSor>(bq, headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/updateEventSorOrder/{eventId}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfaEventSor>> updateEventBqOrder(@RequestBody String[] sorIds, @PathVariable("eventId") String eventId) {
        try {
            LOG.info("RFQ eventId : " + eventId);
            if (sorIds != null && sorIds.length > 0) {
                Integer count = 1;
                for (String cqId : sorIds) {
                    RfaEventSor rfqEventSor = rfaSorService.getSorById(cqId);
                    rfqEventSor.setSorOrder(count);
                    rfaSorService.saveRfaSor(rfqEventSor);
                    count++;
                }
                List<RfaEventSor> rfqEventSorList = rfaSorService.getAllSorListByEventIdByOrder(eventId);
                HttpHeaders headers = new HttpHeaders();
                // Need to change message here
                headers.add("success", messageSource.getMessage("rft.sor.order", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<RfaEventSor>>(rfqEventSorList, headers, HttpStatus.OK);
            } else {
                List<RfaEventSor> rfqEventSorList = rfaSorService.getAllSorListByEventIdByOrder(eventId);
                HttpHeaders headers = new HttpHeaders();
                // Need to change message here
                headers.add("error", messageSource.getMessage("rft.sor.order.empty", new Object[]{}, Global.LOCALE));
                return new ResponseEntity<List<RfaEventSor>>(rfqEventSorList, headers, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error while Ordering Sor : " + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            // Need to change message here
            headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[]{e.getMessage()}, Global.LOCALE));
            return new ResponseEntity<List<RfaEventSor>>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/deleteRftSor", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfaEventSor>> deleteRftBq(@RequestBody BqPojo rftBqPojo) throws JsonProcessingException {
        RfaEventSor sorEvent = rfaSorService.getSorById(rftBqPojo.getId());
        try {
            try {

                rfaSorService.isAllowtoDeleteBQ(rftBqPojo.getId(), RfxTypes.RFA);
            } catch (Exception e) {
                LOG.error("Error while deleting SOR  :" + e.getMessage(), e);
                HttpHeaders headers = new HttpHeaders();
                headers.add("error", e.getMessage());
                return new ResponseEntity<List<RfaEventSor>>(null, headers, HttpStatus.EXPECTATION_FAILED);
            }

            rfaSorService.deleteRfaSor(sorEvent.getId());
            LOG.info(" After delete SOR display remaining eventID :: " + rftBqPojo.getEventId());
            List<RfaEventSor> bqList = rfaSorService.getAllSorListByEventIdByOrder(rftBqPojo.getEventId());
            Integer count = 1;
            if (CollectionUtil.isNotEmpty(bqList)) {
                for (RfaEventSor bq : bqList) {
                    bq.setSorOrder(count);
                    rfaSorService.updateSor(bq);
                    count++;
                }
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("success", messageSource.getMessage("rft.sor.success.delete", new Object[]{sorEvent.getName()}, Global.LOCALE));
            return new ResponseEntity<List<RfaEventSor>>(bqList, headers, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error while deleting SOR  [ " + sorEvent.getName() + " ]" + e.getMessage(), e);
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", messageSource.getMessage("rft.sor.error.delete", new Object[]{sorEvent.getName()}, Global.LOCALE));
            return new ResponseEntity<List<RfaEventSor>>(null, headers, HttpStatus.EXPECTATION_FAILED);
        }
    }


    /**
     * @param rfaBqPojo
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/updateSorList", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfaEventSor>> updateBqList(@RequestBody SorPojo rfaBqPojo) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        try {
            LOG.info("UPDATE RFA SOR :: " + rfaBqPojo.toString());
            RfaEventSor rfaBq = rfaSorService.getRfaEventSorBySorId(rfaBqPojo.getId());
            rfaBq.setName(rfaBqPojo.getSorName());
            rfaBq.setDescription(rfaBqPojo.getSorDesc());
            rfaBq.setModifiedDate(new Date());
            if (sorValidate(rfaBqPojo)) {
                rfaSorService.updateSor(rfaBq);
                List<RfaEventSor> bqList = rfaSorService.getAllSorListByEventIdByOrder(rfaBqPojo.getEventId());
                headers.add("success", messageSource.getMessage("rft.sor.update", new Object[] {}, Global.LOCALE));
                return new ResponseEntity<List<RfaEventSor>>(bqList, headers, HttpStatus.OK);
            } else {
                List<RfaEventSor> bqList = rfaSorService.getAllSorListByEventIdByOrder(rfaBqPojo.getEventId());
                headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[] { rfaBqPojo.getSorName() }, Global.LOCALE));
                return new ResponseEntity<List<RfaEventSor>>(bqList, headers, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error while adding SOR Items to Buyer : " + e.getMessage(), e);
            headers.add("error", messageSource.getMessage("rft.sor.error", new Object[] { e.getMessage() }, Global.LOCALE));
            return new ResponseEntity<List<RfaEventSor>>(null, headers, HttpStatus.BAD_REQUEST);
        }
    }

    private void buildBqItemForSearch(List<RfaSorItem> returnList, List<RfaSorItem> bqList) {
        RfaSorItem parent = null;
        for (RfaSorItem item : bqList) {
            if (item.getOrder() != 0) {
                if (parent != null) {
                    parent.getChildren().add(item.createSearchShallowCopy());
                }
            } else {
                parent = item.createSearchShallowCopy();
                parent.setChildren(new ArrayList<RfaSorItem>());
                returnList.add(parent);
            }
        }
    }

    @RequestMapping(path = "/addNewColumnsSor", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<List<RfaSorItem>> addNewColumns(@RequestBody SorPojo rfqBq) throws JsonProcessingException {
        LOG.info("Enter this Block :: " + rfqBq.toString());
        HttpHeaders headers = new HttpHeaders();
        List<RfaSorItem> bqItemList = null;
        if (rfqBq.getField1Label() == null && rfqBq.getField2Label() == null && rfqBq.getField3Label() == null && rfqBq.getField4Label() == null) {
            headers.add("error", "Field cannot be empty");
            return new ResponseEntity<List<RfaSorItem>>(null, headers, HttpStatus.BAD_REQUEST);
        } else {
            RfaEventSor rftEventBq = rfaSorService.getSorById(rfqBq.getId());
            if (rftEventBq != null) {
                try {

                    List<String> fieldLabelList = new ArrayList<String>();
                    if (rfqBq.getField1Label() != null && fieldLabelList.contains(rfqBq.getField1Label().toLowerCase())) {
                        headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField1Label() }, Global.LOCALE));
                        return new ResponseEntity<List<RfaSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    } else if (rfqBq.getField1Label() != null) {
                        fieldLabelList.add(rfqBq.getField1Label().toLowerCase());
                    }
                    if (rfqBq.getField2Label() != null && fieldLabelList.contains(rfqBq.getField2Label().toLowerCase())) {
                        headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField2Label() }, Global.LOCALE));
                        return new ResponseEntity<List<RfaSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    } else if (rfqBq.getField2Label() != null) {
                        fieldLabelList.add(rfqBq.getField2Label().toLowerCase());
                    }
                    if (rfqBq.getField3Label() != null && fieldLabelList.contains(rfqBq.getField3Label().toLowerCase())) {
                        headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField3Label() }, Global.LOCALE));
                        return new ResponseEntity<List<RfaSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    } else if (rfqBq.getField3Label() != null) {
                        fieldLabelList.add(rfqBq.getField3Label().toLowerCase());
                    }
                    if (rfqBq.getField4Label() != null && fieldLabelList.contains(rfqBq.getField4Label().toLowerCase())) {
                        headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField4Label() }, Global.LOCALE));
                        return new ResponseEntity<List<RfaSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    } else if (rfqBq.getField4Label() != null) {
                        fieldLabelList.add(rfqBq.getField4Label().toLowerCase());
                    }

                    super.buildAddNewColumns(rfqBq, rftEventBq);
                    rfaSorService.updateRfaSorFields(rftEventBq);

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
                    bqItemList = rfaSorService.getSorItemForSearchFilter(rftEventBq.getId(), itemLevel, itemOrder, rfqBq.getSearchVal(), start, length, rfqBq.getPageNo());
                    for (RfaSorItem bqItem : bqItemList) {
                        if (bqItem.getUom() != null) {
                            bqItem.getUom().setCreatedBy(null);
                            bqItem.getUom().setModifiedBy(null);
                        }
                    }
                    headers.add("success", messageSource.getMessage("rft.sor.newfields.success", new Object[] {}, Global.LOCALE));
                    return new ResponseEntity<List<RfaSorItem>>(bqItemList, headers, HttpStatus.OK);
                } catch (Exception e) {
                    headers.add("error", messageSource.getMessage("buyer.rftbq.newfields.error", new Object[] {}, Global.LOCALE));
                    return new ResponseEntity<List<RfaSorItem>>(null, headers, HttpStatus.BAD_REQUEST);
                }
            }
        }
        return new ResponseEntity<List<RfaSorItem>>(bqItemList, headers, HttpStatus.OK);
    }


    @RequestMapping(path = "/deleteSorNewField", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<List<RfaSorItem>> deleteBqNewField(@RequestParam("bqId") String bqId, @RequestParam("label") String label, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
        LOG.info("deleteSorNewField label ::" + label + " bqId :: " + bqId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
        HttpHeaders headers = new HttpHeaders();
        List<RfaSorItem> bqItemList = null;
        try {
            if (StringUtils.checkString(label).length() > 0) {
                rfaSorService.deletefieldInSorItems(bqId, label);
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
                bqItemList = rfaSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
                for (RfaSorItem bqItem : bqItemList) {
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
            return new ResponseEntity<List<RfaSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<RfaSorItem>>(bqItemList, headers, HttpStatus.OK);
    }

    @RequestMapping(path = "/deleteSorItems/{bqId}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<RfaSorItemResponsePojo> deleteBqItems(@RequestParam(name = "items", required = true) String items, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo, @PathVariable("bqId") String bqId, @RequestParam(name = "allDelete", required = false) boolean allDelete) throws JsonProcessingException {
        LOG.info("DELETE SOR ITEMS IDs :: " + items);
        List<RfaSorItem> bqItems = null;
        HttpHeaders headers = new HttpHeaders();

        if (StringUtils.checkString(filterVal).length() > 0) {
            if (filterVal.contains(".")) {
                filterVal = "";
            }
        }
        // sending level order list and bq Item list bind in new Pojo class
        RfaSorItemResponsePojo bqItemResponsePojo = new RfaSorItemResponsePojo();

        if (StringUtils.checkString(items).length() > 0) {
            try {
                String[] bqItemsArr = items.split(",");

                LOG.info(" filterVal  :" + filterVal + " searchVal: " + searchVal + "=====>" + allDelete);

                if (allDelete) {
                    rfaSorService.deleteAllSorItems(bqId);
                } else {
                    rfaSorService.deleteSorItems(bqItemsArr, bqId);
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

                bqItems = rfaSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
                List<BqItemPojo> leveLOrderList = rfaSorService.getAllLevelOrderSorItemBySorId(bqId, searchVal);
                bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
                bqItemResponsePojo.setBqItemList(bqItems);

            } catch (NotAllowedException e) {
                LOG.error("Not Allowed Exception: " + e.getMessage(), e);
                headers.add("error", e.getMessage());
                return new ResponseEntity<RfaSorItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception e) {
                LOG.error("Error while delete sor items: " + e.getMessage(), e);
                headers.add("error", "Error during delete : " + messageSource.getMessage("rft.soritems.error.delete", new Object[] {}, Global.LOCALE));
                return new ResponseEntity<RfaSorItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<RfaSorItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
    }

}
