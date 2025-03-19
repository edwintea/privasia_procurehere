package com.privasia.procurehere.web.controller;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.RfaBqItemResponsePojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaSupplierBqItemService;
import com.privasia.procurehere.service.RfaSupplierBqService;
import com.privasia.procurehere.service.supplier.SupplierService;

@Controller
@RequestMapping("/buyer/RFA")
public class RfaBqController extends EventBqBase {

	public RfaBqController() {
		super(RfxTypes.RFA);
	}

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfaSupplierBqItemDao supplierBqItemDao;

	@Autowired
	RfaSupplierBqItemService rfaSupplierBqItemService;

	@Autowired
	SupplierService supplierService;

	/**
	 * @param eventId
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/createEventBQ/{eventId}/{bqId}", method = RequestMethod.GET)
	public String createRfaEventBQ(@PathVariable String eventId, @PathVariable String bqId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0 || StringUtils.checkString(bqId).length() == 0) {
			return "redirect:/400_error";
		}
		Integer start = 0;
		Integer length = 50;
		Integer pageNo = 1;
		LOG.info("BqId : " + bqId);
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		RfaEventBq eventBq = rfaBqService.getRfaBqById(bqId);
		List<RfaBqItem> returnList = new ArrayList<>();
		length = SecurityLibrary.getLoggedInUser().getBqPageLength();
		LOG.info(" BQ Page length : " + length);
		List<RfaBqItem> bqList = rfaBqService.getBqItemForSearchFilter(bqId, null, null, null, start, length, pageNo);
		buildBqItemForSearch(returnList, bqList);
		// List<RfaBqItem> bqList = rfaBqService.findRfaBqbyBqId(bqId);
		model.addAttribute("rftBqItem", new RfaBqItem());
		model.addAttribute("bqList", returnList);
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

		model.addAttribute("supplierBqCount", (supplierBqItemDao.getCountOfSupplierBqItem(eventId) > 0 ? true : false));
		buildModel(eventId, bqId, model, rfaEvent, eventBq);
		List<BqItemPojo> leveLOrderList = rfaBqService.getAllLevelOrderBqItemByBqId(bqId, null);
		model.addAttribute("leveLOrderList", leveLOrderList);
		long totalBqItemCount = rfaBqService.totalBqItemCountByBqId(bqId, null);
		model.addAttribute("totalBqItemCount", totalBqItemCount);
		model.addAttribute("event", rfaEvent);
		LOG.info("totalBqItemCount :" + totalBqItemCount);
		return "createEventBQ";
	}

	/**
	 * @param returnList
	 * @param bqList
	 */
	private void buildBqItemForSearch(List<RfaBqItem> returnList, List<RfaBqItem> bqList) {
		RfaBqItem parent = null;
		for (RfaBqItem item : bqList) {
			if (item.getOrder() != 0) {
				if (parent != null) {
					parent.getChildren().add(item.createSearchShallowCopy());
				}
			} else {
				parent = item.createSearchShallowCopy();
				parent.setChildren(new ArrayList<RfaBqItem>());
				returnList.add(parent);
			}
		}
	}

	/**
	 * @param model
	 * @param eventId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/bqListPrevious", method = RequestMethod.POST)
	public String bqListPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) throws Exception {
		try {
			if (eventId != null) {
				model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				return "redirect:createBQList/" + eventId;
			} else {
				LOG.error("Event not found redirecting to login ");
				return "/";
			}
		} catch (Exception e) {
			LOG.error("Event not found redirecting to login : " + e.getMessage(), e);
			throw new Exception("Event not found redirecting to login : " + e.getMessage());
		}
	}

	/**
	 * @param rfaBqItemPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/createEventBQ", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RfaBqItemResponsePojo> saveRfaEventBQ(@RequestBody BqItemPojo rfaBqItemPojo, Model model) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {

			if (StringUtils.checkString(rfaBqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(rfaBqItemPojo.getParent()).length() == 0) {
				HttpHeaders header = new HttpHeaders();
				header.add("error", messageSource.getMessage("event.bqsection.required", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfaBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
			}

			if (rfaBqItemPojo.getQuantity() != null && rfaBqItemPojo.getUnitPrice() != null) {
				java.math.BigDecimal unitPrice = rfaBqItemPojo.getUnitPrice();
				java.math.BigDecimal qnty = rfaBqItemPojo.getQuantity();
				if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
					LOG.info("VALUE IS " + qnty.multiply(unitPrice));
					headers.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[] {}, null));
					return new ResponseEntity<RfaBqItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
				}
			}
			LOG.info("Crate EVENT BQ ID :: " + rfaBqItemPojo.getBq());
			LOG.info(" Filter :" + rfaBqItemPojo.getFilterVal() + " Search :" + rfaBqItemPojo.getSearchVal());
			RfaBqItem rfaBqItem = new RfaBqItem();
			rfaBqItem.setId(rfaBqItemPojo.getId());
			rfaBqItem.setRfxEvent(rfaEventService.getRfaEventByeventId(rfaBqItemPojo.getRftEvent()));
			rfaBqItem.setBq(rfaBqService.getBqItembyId(rfaBqItemPojo.getBq()));

			RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(rfaBqItemPojo.getRftEvent());
			model.addAttribute("event", rfaEvent);

			/*************** To check whether quantity of Bqitem entered should not be 0 ****************/

			if (rfaBqItemPojo.getQuantity() != null) {
				LOG.info("rftBqItem.setBq***************" + rfaBqItemPojo.getQuantity());
				HttpHeaders header = new HttpHeaders();

				java.math.BigDecimal i = rfaBqItemPojo.getQuantity();
				java.math.BigDecimal c = java.math.BigDecimal.ZERO;
				int res = i.compareTo(c);
				if (res == 0) {
					header.add("error", messageSource.getMessage("buyer.rftbq.quantity", new Object[] { rfaBqItemPojo.getQuantity() }, Global.LOCALE));
					return new ResponseEntity<RfaBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			constructBqItemValues(rfaBqItemPojo, rfaBqItem);
			if (StringUtils.checkString(rfaBqItemPojo.getParent()).length() > 0) {
				rfaBqItem.setParent(rfaBqService.getBqItemsbyBqId(StringUtils.checkString(rfaBqItemPojo.getParent())));
			}
			Integer itemLevel = null;
			Integer itemOrder = null;
			if (StringUtils.checkString(rfaBqItemPojo.getFilterVal()).length() == 1) {
				rfaBqItemPojo.setFilterVal("");
			}
			if (StringUtils.checkString(rfaBqItemPojo.getFilterVal()).length() > 0) {
				itemLevel = 0;
				itemOrder = 0;
				String[] values = rfaBqItemPojo.getFilterVal().split("\\.");
				itemLevel = Integer.parseInt(values[0]);
				itemOrder = Integer.parseInt(values[1]);
			}
			// sending level order list and bq Item list bind in new Pojo class
			RfaBqItemResponsePojo bqItemResponsePojo = new RfaBqItemResponsePojo();

			if (!rfaBqService.isBqItemExists(rfaBqItem, rfaBqItemPojo.getBq(), rfaBqItemPojo.getParent())) {
				rfaBqService.saveRfaBqItem(rfaBqItem);
				rfaBqItemPojo.setStart(0);
				LOG.info(" Strat :" + rfaBqItemPojo.getStart() + " lenth :" + rfaBqItemPojo.getPageLength() + " Page Number :" + rfaBqItemPojo.getPageNo());
				List<RfaBqItem> bqItemList = rfaBqService.getBqItemForSearchFilter(rfaBqItemPojo.getBq(), itemLevel, itemOrder, rfaBqItemPojo.getSearchVal(), rfaBqItemPojo.getStart(), rfaBqItemPojo.getPageLength(), rfaBqItemPojo.getPageNo());
				List<BqItemPojo> leveLOrderList = rfaBqService.getAllLevelOrderBqItemByBqId(rfaBqItemPojo.getBq(), rfaBqItemPojo.getSearchVal());
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItemList);

				// List<RfaBqItem> bqList = rfaBqService.findRfaBqbyBqId(rfaBqItemPojo.getBq());
				headers.add("success", messageSource.getMessage("buyer.rftbq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfaBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);

			} else {
				List<RfaBqItem> bqItemList = rfaBqService.getBqItemForSearchFilter(rfaBqItemPojo.getBq(), itemLevel, itemOrder, rfaBqItemPojo.getSearchVal(), rfaBqItemPojo.getStart(), rfaBqItemPojo.getPageLength(), rfaBqItemPojo.getPageNo());
				List<BqItemPojo> leveLOrderList = rfaBqService.getAllLevelOrderBqItemByBqId(rfaBqItemPojo.getBq(), rfaBqItemPojo.getSearchVal());
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItemList);
				// List<RfaBqItem> bqList = rfaBqService.findRfaBqbyBqId(rfaBqItemPojo.getBq());
				LOG.info("Validate of BQ LIST :: " + rfaBqItemPojo.toString());
				headers.add("error", messageSource.getMessage("buyer.rftbq.duplicate", new Object[] { rfaBqItemPojo.getItemName() }, Global.LOCALE));
				return new ResponseEntity<RfaBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfaBqItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param bqItemOrder
	 * @return
	 */
	@RequestMapping(path = "/eventBQOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaBqItem>> eventBQOrder(@RequestBody BqItemPojo rfaBqItemPojo, Model model) {
		LOG.info("Parent : " + rfaBqItemPojo.getParent() + " Item Id : " + rfaBqItemPojo.getId() + " New Position : " + rfaBqItemPojo.getOrder());
		HttpHeaders headers = new HttpHeaders();
		List<RfaBqItem> bqList = null;
		Integer start = null;
		Integer length = null;
		try {
			if (StringUtils.checkString(rfaBqItemPojo.getId()).length() > 0) {
				start = 0;
				length = rfaBqItemPojo.getPageLength();
				LOG.info("Updating order..........................");
				RfaBqItem bqItem = rfaBqService.getBqItembyBqItemId(rfaBqItemPojo.getId());
				model.addAttribute("supplierBqCount", (supplierBqItemDao.getCountOfSupplierBqItem(rfaBqItemPojo.getId()) > 0 ? true : false));
				if (bqItem.getOrder() > 0 && StringUtils.checkString(rfaBqItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					List<RfaBqItem> returnList = rfaBqService.getBqItemForSearchFilter(rfaBqItemPojo.getBq(), null, null, null, start, length, rfaBqItemPojo.getPageNo());
					return new ResponseEntity<List<RfaBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
					// return new
					// ResponseEntity<List<RfaBqItem>>(rfaBqService.findRfaBqbyBqId(rfaBqService.getBqItembyBqItemId(rfaBqItemPojo.getId()).getBq().getId()),
					// headers, HttpStatus.BAD_REQUEST);
				}
				if (bqItem.getOrder() == 0 && StringUtils.checkString(rfaBqItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					List<RfaBqItem> returnList = rfaBqService.getBqItemForSearchFilter(rfaBqItemPojo.getBq(), null, null, null, start, length, rfaBqItemPojo.getPageNo());
					return new ResponseEntity<List<RfaBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
					// return new
					// ResponseEntity<List<RfaBqItem>>(rfaBqService.findRfaBqbyBqId(rfaBqService.getBqItembyBqItemId(rfaBqItemPojo.getId()).getBq().getId()),
					// headers, HttpStatus.BAD_REQUEST);
				}
				RfaBqItem item = new RfaBqItem();
				item.setId(rfaBqItemPojo.getId());
				item.setRfxEvent(rfaEventService.getRfaEventByeventId(rfaBqItemPojo.getRftEvent()));
				item.setItemName(rfaBqItemPojo.getItemName());
				constructBqItemValues(rfaBqItemPojo, item);
				if (bqItem.getParent() != null && !bqItem.getParent().getId().equals(rfaBqItemPojo.getParent()) && rfaBqService.isBqItemExists(item, rfaBqItemPojo.getBq(), rfaBqItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new NotAllowedException("Duplicate BQ Item. BQ Item by that name already exists.");
				}
				rfaBqService.reorderBqItems(rfaBqItemPojo);
				// bqList =
				// rfaBqService.findRfaBqbyBqId(rfaBqService.getBqItembyBqItemId(rfaBqItemPojo.getId()).getBq().getId());
				bqList = rfaBqService.getBqItemForSearchFilter(rfaBqItemPojo.getBq(), null, null, null, start, length, rfaBqItemPojo.getPageNo());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfaBqItem>>(bqList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException e) {
			LOG.error("Error while moving BQ parent with item as Child : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.drag", new Object[] { e.getMessage() }, Global.LOCALE));
			bqList = rfaBqService.findRfaBqbyBqId(rfaBqService.getBqItembyBqItemId(rfaBqItemPojo.getId()).getBq().getId());
			List<RfaBqItem> returnList = rfaBqService.getBqItemForSearchFilter(rfaBqItemPojo.getBq(), null, null, null, start, length, rfaBqItemPojo.getPageNo());
			return new ResponseEntity<List<RfaBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
			// return new ResponseEntity<List<RfaBqItem>>(bqList, headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("Error while moving BQ Item : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfaBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RfaBqItem>>(bqList, headers, HttpStatus.OK);
	}

	/**
	 * @param model
	 * @param eventId
	 * @param redir
	 * @return
	 */
	@RequestMapping(value = "/bqPrevious", method = RequestMethod.POST)
	public String bqPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes redir) {
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		if (rfaEvent != null) {
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			return rfaBqPrevious(rfaEvent);
		} else {
			LOG.error("Event not found redirecting to login ");
			return "/";
		}
	}

	/**
	 * @param model
	 * @param eventId
	 * @param redir
	 * @return
	 */
	@RequestMapping(value = "/bqNext", method = RequestMethod.POST)
	public String bqNext(Model model, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes redir) {
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		if (rfaEvent != null) {
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			rfaEvent.setBqCompleted(Boolean.TRUE);
			rfaEventService.updateRfaEvent(rfaEvent);
			return super.bqNext(rfaEvent);
		} else {
			LOG.error("Event not found redirecting to login ");
			return "/";
		}
	}

	/**
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/createBQList/{eventId}", method = RequestMethod.GET)
	public String createBQList(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		List<RfaEventBq> bqEventList = rfaBqService.getAllBqListByEventIdByOrder(eventId);
		model.addAttribute("event", rfaEvent);

		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("bqEventList", bqEventList);
		AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
		model.addAttribute("auctionRules", auctionRules);
		model.addAttribute("supplierBqCount", (supplierBqItemDao.getCountOfSupplierBqItem(eventId) > 0 ? true : false));
		return "createBQList";
	}

	/**
	 * @param rfaBqPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/createBQList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Bq>> saveBQList(@RequestBody BqPojo rfaBqPojo) throws JsonProcessingException {
		try {
			HttpHeaders headers = new HttpHeaders();
			List<Bq> bqList = rfaBqService.findRfaBqbyEventId(rfaBqPojo.getEventId());
			if (CollectionUtil.isEmpty(bqList)) {
				if (bqValidate(rfaBqPojo)) {
					Integer count = 1;
					bqList = super.saveBq(rfaBqPojo, count);
					headers.add("success", messageSource.getMessage("rft.bq.save", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<Bq>>(bqList, headers, HttpStatus.OK);
				} else {
					bqList = rfaBqService.findRfaBqbyEventId(rfaBqPojo.getEventId());
					headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[] { rfaBqPojo.getBqName() }, Global.LOCALE));
					return new ResponseEntity<List<Bq>>(bqList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				headers.add("error", messageSource.getMessage("rft.bq.limt", new Object[] { rfaBqPojo.getBqName() }, Global.LOCALE));
				return new ResponseEntity<List<Bq>>(bqList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<Bq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param model
	 * @param bqId
	 * @param eventId
	 * @param redir
	 * @return
	 */
	@RequestMapping(value = "/showBqItems", method = RequestMethod.POST)
	public String showBqItems(Model model, @RequestParam(name = "bqId", required = true) String bqId, @RequestParam(name = "eventId", required = true) String eventId, RedirectAttributes redir) {
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		if (rfaEvent != null) {
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			return "redirect:createEventBQ/" + rfaEvent.getId() + "/" + bqId;
		} else {
			LOG.error("BQ not found redirecting to login ");
			return "/";
		}
	}

	/**
	 * @param rftBqItem
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/updateBqItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RfaBqItemResponsePojo> updateBqItem(@RequestBody BqItemPojo rfaBqItem) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(rfaBqItem.getItemName()).length() == 0 && StringUtils.checkString(rfaBqItem.getParent()).length() == 0) {
				HttpHeaders header = new HttpHeaders();
				header.add("error", messageSource.getMessage("event.bqsection.required", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfaBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
			}

			if (rfaBqItem.getQuantity() != null && rfaBqItem.getUnitPrice() != null) {
				HttpHeaders header = new HttpHeaders();
				java.math.BigDecimal unitPrice = rfaBqItem.getUnitPrice();
				java.math.BigDecimal qnty = rfaBqItem.getQuantity();
				if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
					LOG.info("VALUE IS " + qnty.multiply(unitPrice));
					header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[] {}, null));
					return new ResponseEntity<RfaBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			LOG.info(" :: UPDATE BILL OF QUNATITY ::" + rfaBqItem.toString());
			RfaBqItem rfaBq = rfaBqService.getRfaBqItemById(rfaBqItem.getId());
			rfaBq.setUom(StringUtils.checkString(rfaBqItem.getUom()).length() > 0 ? uomService.getUombyCode(rfaBqItem.getUom(), SecurityLibrary.getLoggedInUserTenantId()) : null);

			super.buildUpdateBqItem(rfaBqItem, rfaBq);

			/*************** To check whether quantity of Bqitem entered should not be 0 ****************/

			RfaBqItem item = new RfaBqItem();
			item.setId(rfaBqItem.getId());
			item.setRfxEvent(rfaEventService.getRfaEventByeventId(rfaBqItem.getRftEvent()));
			item.setItemName(rfaBqItem.getItemName());
			if (rfaBqItem.getQuantity() != null) {
				LOG.info("rftBqItem.setBq***************" + rfaBqItem.getQuantity());
				HttpHeaders header = new HttpHeaders();

				java.math.BigDecimal i = rfaBqItem.getQuantity();
				java.math.BigDecimal c = java.math.BigDecimal.ZERO;
				int res = i.compareTo(c);
				if (res == 0) {
					header.add("error", messageSource.getMessage("buyer.rftbq.quantity", new Object[] { rfaBqItem.getQuantity() }, Global.LOCALE));
					return new ResponseEntity<RfaBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}
			// constructBqItemValues(rfaBqItem, item);
			if (rfaBqService.isBqItemExists(item, rfaBqItem.getBq(), rfaBqItem.getParent())) {
				LOG.info("Duplicate....");
				throw new ApplicationException("Duplicate BQ Item. BQ Item by that name already exists.");
			}
			rfaBqService.updateBqItem(rfaBq);
			// List<RfaBqItem> bqList = rfaBqService.findRfaBqbyBqId(rfaBqItem.getBq());
			Integer itemLevel = null;
			Integer itemOrder = null;
			if (StringUtils.checkString(rfaBqItem.getFilterVal()).length() == 1) {
				rfaBqItem.setFilterVal("");
			}
			if (StringUtils.checkString(rfaBqItem.getFilterVal()).length() > 0) {
				itemLevel = 0;
				itemOrder = 0;
				String[] values = rfaBqItem.getFilterVal().split("\\.");
				itemLevel = Integer.parseInt(values[0]);
				itemOrder = Integer.parseInt(values[1]);
			}
			rfaBqItem.setStart(0);
			LOG.info(" bqItemPojo Page no :" + rfaBqItem.getPageNo());
			List<RfaBqItem> bqItemList = rfaBqService.getBqItemForSearchFilter(rfaBqItem.getBq(), itemLevel, itemOrder, rfaBqItem.getSearchVal(), rfaBqItem.getStart(), rfaBqItem.getPageLength(), rfaBqItem.getPageNo());
			// sending level order list and bq Item list bind in new Pojo class
			RfaBqItemResponsePojo bqItemResponsePojo = new RfaBqItemResponsePojo();
			List<BqItemPojo> leveLOrderList = rfaBqService.getAllLevelOrderBqItemByBqId(rfaBqItem.getBq(), null);
			bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
			bqItemResponsePojo.setBqItemList(bqItemList);

			HttpHeaders headers = new HttpHeaders();
			headers.add("success", messageSource.getMessage("buyer.rftbq.save", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RfaBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while updating BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfaBqItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param rfaBqPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/updateBqList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaEventBq>> updateBqList(@RequestBody BqPojo rfaBqPojo) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("UPDATE RFT BQ :: " + rfaBqPojo.toString());
			RfaEventBq rfaBq = rfaBqService.getRfaBqById(rfaBqPojo.getId());
			rfaBq.setName(rfaBqPojo.getBqName());
			rfaBq.setDescription(rfaBqPojo.getBqDesc());
			rfaBq.setModifiedDate(new Date());
			if (bqValidate(rfaBqPojo)) {
				rfaBqService.updateRfaBq(rfaBq);
				List<RfaEventBq> bqList = rfaBqService.getAllBqListByEventIdByOrder(rfaBqPojo.getEventId());
				headers.add("success", messageSource.getMessage("rft.bq.update", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfaEventBq>>(bqList, headers, HttpStatus.OK);
			} else {
				List<RfaEventBq> bqList = rfaBqService.getAllBqListByEventIdByOrder(rfaBqPojo.getEventId());
				headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[] { rfaBqPojo.getBqName() }, Global.LOCALE));
				return new ResponseEntity<List<RfaEventBq>>(bqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("rft.bq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfaEventBq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param rftBqPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/deleteRftBq", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaEventBq>> deleteRfaBq(@RequestBody BqPojo rfaBqPojo) throws JsonProcessingException {
		RfaEventBq bqEvent = rfaBqService.getRfaBqById(rfaBqPojo.getId());
		try {
			try {
				rfaBqService.isAllowtoDeleteBQ(rfaBqPojo.getId(), RfxTypes.RFA);
			} catch (Exception e) {
				LOG.error("Error while deleting BQ  :" + e.getMessage(), e);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", e.getMessage());
				return new ResponseEntity<List<RfaEventBq>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			}
			// if (bqEvent.getRfxEvent().getStatus() != EventStatus.DRAFT) {
			// HttpHeaders headers = new HttpHeaders();
			// headers.add("error", messageSource.getMessage("rft.bq.info.draft", new Object[] { bqEvent.getName() },
			// Global.LOCALE));
			// return new ResponseEntity<List<RfaEventBq>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			// } else {
			rfaBqService.deleteRfaBq(bqEvent.getId());
			LOG.info(" After delete BQ display remaining eventID :: " + rfaBqPojo.getEventId());
			List<RfaEventBq> bqList = rfaBqService.getAllBqListByEventIdByOrder(rfaBqPojo.getEventId());
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", messageSource.getMessage("rft.bq.success.delete", new Object[] { bqEvent.getName() }, Global.LOCALE));
			return new ResponseEntity<List<RfaEventBq>>((bqList == null ? new ArrayList<RfaEventBq>() : bqList), headers, HttpStatus.OK);
			// }

		} catch (Exception e) {
			LOG.error("Error while deleting BQ  [ " + bqEvent.getName() + " ]" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.error.delete", new Object[] { bqEvent.getName() }, Global.LOCALE));
			return new ResponseEntity<List<RfaEventBq>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}

	}

	/**
	 * @param rftBqItemPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/addNewColumns", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaBqItem>> addNewColumns(@RequestBody BqPojo rfaBq) throws JsonProcessingException {
		LOG.info("Enter this Block :: " + rfaBq.toString());
		HttpHeaders headers = new HttpHeaders();
		List<RfaBqItem> bqItemList = null;
		if (rfaBq.getField1Label() == null && rfaBq.getField2Label() == null && rfaBq.getField3Label() == null && rfaBq.getField4Label() == null) {
			headers.add("error", "Field cannot be empty");

			return new ResponseEntity<List<RfaBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		} else {
			RfaEventBq rfaEventBq = rfaBqService.getRfaBqById(rfaBq.getId());
			if (rfaEventBq != null) {
				try {
					List<String> fieldLabelList = new ArrayList<String>();
					if (rfaBq.getField1Label() != null && fieldLabelList.contains(rfaBq.getField1Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfaBq.getField1Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfaBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfaBq.getField1Label() != null) {
						fieldLabelList.add(rfaBq.getField1Label().toLowerCase());
					}
					if (rfaBq.getField2Label() != null && fieldLabelList.contains(rfaBq.getField2Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfaBq.getField2Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfaBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfaBq.getField2Label() != null) {
						fieldLabelList.add(rfaBq.getField2Label().toLowerCase());
					}
					if (rfaBq.getField3Label() != null && fieldLabelList.contains(rfaBq.getField3Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfaBq.getField3Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfaBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfaBq.getField3Label() != null) {
						fieldLabelList.add(rfaBq.getField3Label().toLowerCase());
					}
					if (rfaBq.getField4Label() != null && fieldLabelList.contains(rfaBq.getField4Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfaBq.getField4Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfaBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfaBq.getField4Label() != null) {
						fieldLabelList.add(rfaBq.getField4Label().toLowerCase());
					}

					super.buildAddNewColumns(rfaBq, rfaEventBq);

					rfaBqService.updateRfaBqFields(rfaEventBq);
					Integer itemLevel = null;
					Integer itemOrder = null;
					Integer start = null;
					Integer length = null;
					if (StringUtils.checkString(rfaBq.getFilterVal()).length() == 1) {
						rfaBq.setFilterVal("");
					}
					if (StringUtils.checkString(rfaBq.getFilterVal()).length() > 0) {
						itemLevel = 0;
						itemOrder = 0;
						String[] values = rfaBq.getFilterVal().split("\\.");
						itemLevel = Integer.parseInt(values[0]);
						itemOrder = Integer.parseInt(values[1]);
					}
					start = 0;
					length = rfaBq.getPageLength();

					// bqList = rfaBqService.findRfaBqbyBqId(rfaEventBq.getId());
					bqItemList = rfaBqService.getBqItemForSearchFilter(rfaEventBq.getId(), itemLevel, itemOrder, rfaBq.getSearchVal(), start, length, rfaBq.getPageNo());
					for (RfaBqItem bqItem : bqItemList) {
						if (bqItem.getUom() != null) {
							bqItem.getUom().setCreatedBy(null);
							bqItem.getUom().setModifiedBy(null);
						}
					}

					headers.add("success", messageSource.getMessage("rft.bq.newfields.success", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfaBqItem>>(bqItemList, headers, HttpStatus.OK);
				} catch (Exception e) {
					headers.add("error", messageSource.getMessage("buyer.rftbq.newfields.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfaBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
				}
			}
		}
		return new ResponseEntity<List<RfaBqItem>>(bqItemList, headers, HttpStatus.OK);

	}

	/**
	 * @param bqId
	 * @param bqItemId
	 * @return
	 */
	@RequestMapping(path = "/getBqForEdit", method = RequestMethod.GET)
	public ResponseEntity<RfaBqItem> getBqForEdit(@RequestParam String bqId, @RequestParam String bqItemId) {
		RfaBqItem rfaBqItem = null;
		LOG.info("BQ EDIT :: bqId" + bqId + " :: bqItemId :: " + bqItemId);
		try {
			rfaBqItem = rfaBqService.getBqItembyBqIdAndBqItemId(bqItemId);
			rfaBqItem = rfaBqItem.createShallowCopy();
		} catch (Exception e) {
			LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfaBqItem>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (rfaBqItem != null) {
			return new ResponseEntity<RfaBqItem>(rfaBqItem, HttpStatus.OK);
		} else {
			LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RfaBqItem>(null, headers, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @param items
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/deleteBqItems/{bqId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RfaBqItemResponsePojo> deleteBqItems(@RequestParam(name = "items", required = true) String items, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo, @PathVariable("bqId") String bqId, @RequestParam(name = "allDelete", required = false) boolean allDelete) throws JsonProcessingException {
		LOG.info("DELETE BQ ITEMS IDs :: " + items);
		List<RfaBqItem> bqItems = null;
		HttpHeaders headers = new HttpHeaders();

		if (StringUtils.checkString(filterVal).length() > 0) {
			if (filterVal.contains(".")) {
				filterVal = "";
			}
		}

		// sending level order list and bq Item list bind in new Pojo class
		RfaBqItemResponsePojo bqItemResponsePojo = new RfaBqItemResponsePojo();
		if (StringUtils.checkString(items).length() > 0) {
			try {
				String[] bqItemsArr = items.split(",");
				LOG.info(" filterVal  :" + filterVal + " searchVal: " + searchVal);

				if (allDelete) {
					rfaBqService.deleteAllBqItems(bqId);
				} else {
					rfaBqService.deleteBqItems(bqItemsArr, bqId);
				}
				headers.add("success", messageSource.getMessage("rft.bqitems.success.delete", new Object[] {}, Global.LOCALE));

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

				bqItems = rfaBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
				List<BqItemPojo> leveLOrderList = rfaBqService.getAllLevelOrderBqItemByBqId(bqId, searchVal);
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItems);
			} catch (NotAllowedException e) {
				LOG.error("Not Allowed Exception: " + e.getMessage(), e);
				headers.add("error", e.getMessage());
				return new ResponseEntity<RfaBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (Exception e) {
				LOG.error("Error while delete bq items: " + e.getMessage(), e);
				headers.add("error", "Error during delete : " + messageSource.getMessage("rft.bqitems.error.delete", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfaBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<RfaBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/bqSaveDraft", method = RequestMethod.POST)
	public String bqSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId) {
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rfaEvent.getEventName() != null ? rfaEvent.getEventName() : rfaEvent.getEventId()) }, Global.LOCALE));
		return "redirect:createBQList/" + eventId;

	}

	/**
	 * @param bqId
	 * @param label
	 * @return
	 */
	@RequestMapping(path = "/deleteBqNewField", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RfaBqItem>> deleteBqNewField(@RequestParam("bqId") String bqId, @RequestParam("label") String label, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
		LOG.info("deleteBqNewField label ::" + label + " bqId :: " + bqId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		HttpHeaders headers = new HttpHeaders();
		List<RfaBqItem> bqItemList = null;
		try {
			if (StringUtils.checkString(label).length() > 0) {
				rfaBqService.deletefieldInBqItems(bqId, label);
				// bqList = rfaBqService.findRfaBqbyBqId(bqId);
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
				bqItemList = rfaBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
				for (RfaBqItem bqItem : bqItemList) {
					if (bqItem.getUom() != null) {
						bqItem.getUom().setCreatedBy(null);
						bqItem.getUom().setModifiedBy(null);
					}
				}
				headers.add("success", "Column deleted Successfully");
			}
		} catch (Exception e) {
			LOG.error("Error while deleting BQ New Field " + e.getMessage(), e);
			headers.add("error", "Error while deleting BQ New Field : " + e.getMessage());
			return new ResponseEntity<List<RfaBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<RfaBqItem>>(bqItemList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getBqForNewFields", method = RequestMethod.GET)
	public ResponseEntity<RfaEventBq> getBqForNewFields(@RequestParam String bqId) {
		RfaEventBq eventBq = null;
		LOG.info("BQ EDIT :: bqId" + bqId);
		try {
			eventBq = rfaBqService.getRfaBqById(bqId);

		} catch (Exception e) {
			LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfaEventBq>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (eventBq != null) {
			return new ResponseEntity<RfaEventBq>(eventBq, HttpStatus.OK);
		} else {
			LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RfaEventBq>(null, headers, HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(path = "/bqItemTemplate/{bqId}", method = RequestMethod.GET)
	public void downloader(@PathVariable String bqId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "BqItemTemplate.xlsx";
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

	@RequestMapping(value = "/uploadBqItems", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) throws IOException {
		LOG.info("UPLOADING STARTED ...... BQ Id :: " + bqId + "Event Id :: " + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		List<RfaBqItem> bqItemList = null;
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
				super.validateUploadBQItems(file);
				try {
					totalBqItemCount = super.bqItemsUpload(file, bqId, eventId, RfxTypes.RFA);
					if (totalBqItemCount == 0) {
						LOG.info("totalBqItemCount " + totalBqItemCount);
						throw new Exception("Please fill data in excel file");
					}
				} catch (Exception e) {
					// e.printStackTrace();
					// bqList = rfaBqService.findRfaBqbyBqId(bqId);
					bqItemList = rfaBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
					for (RfaBqItem bqItem : bqItemList) {
						if (bqItem.getUom() != null) {
							bqItem.getUom().setCreatedBy(null);
							bqItem.getUom().setModifiedBy(null);
						}
					}
					headers.add("error", messageSource.getMessage("common.upload.fail", new Object[] { e.getMessage() }, Global.LOCALE));
					response.put("list", bqItemList);
					response.put("totalBqItemCount", totalBqItemCount);
					return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.BAD_REQUEST);
				}
			}
		} catch (MultipartException e) {
			// LOG.info("Upload failed!" + e.getMessage());
			// bqList = rfaBqService.findRfaBqbyBqId(bqId);
			bqItemList = rfaBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
			for (RfaBqItem bqItem : bqItemList) {
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
		// return "redirect:createEventBQ/" + eventId + "/" + bqId;
		// bqList = rfaBqService.findRfaBqbyBqId(bqId);
		bqItemList = rfaBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
		for (RfaBqItem bqItem : bqItemList) {
			if (bqItem.getUom() != null) {
				bqItem.getUom().setCreatedBy(null);
				bqItem.getUom().setModifiedBy(null);
			}
		}
		RfaEventBq bq = rfaBqService.getRfaEventBqByBqId(bqId);
		BqPojo rfaBqColumns = new BqPojo(bq);
		response.put("rftBqColumns", rfaBqColumns);

		headers.add("success", messageSource.getMessage("common.upload.success", new Object[] { "RFA BQ Items" }, Global.LOCALE));
		response.put("list", bqItemList);
		response.put("totalBqItemCount", totalBqItemCount);
		return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.OK);
	}

	protected String rfaBqPrevious(RfaEvent rfaEvent) {
		if (Boolean.TRUE == rfaEvent.getQuestionnaires()) {
			return "redirect:eventCqList/" + rfaEvent.getId();
		} else if (Boolean.TRUE == rfaEvent.getMeetingReq()) {
			return "redirect:meetingList/" + rfaEvent.getId();
		} else if (rfaEvent.getEventVisibility() != EventVisibilityType.PUBLIC) {
			return "redirect:addSupplier/" + rfaEvent.getId();
		} else if (Boolean.TRUE == rfaEvent.getDocumentReq()) {
			return "redirect:createEventDocuments/" + rfaEvent.getId();
		} else {
			return "redirect:auctionRules/" + rfaEvent.getId();
		}
	}

	@RequestMapping(path = "/eventBqForSupplier/{eventId}", method = RequestMethod.GET)
	public String eventFirstBqForSupplier(@PathVariable String eventId, Model model, RedirectAttributes redir) {
		try {
			RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
			model.addAttribute("event", event);

			RfaSupplierBq rfaSupplierBq = new RfaSupplierBq();
			List<RfaEventBq> rfaEventBqs = rfaBqService.findBqAndBqItemsByEventIdByOrder(eventId);
			boolean isBqItemExists = rfaBqService.checkIfBqItemExists(eventId);
			Boolean isPresetPreBidForAllSuppliers = rfaBqService.findPreSetPredBidAuctionRulesWithEventId(eventId);
			model.addAttribute("isPresetPreBidForAllSuppliers", isPresetPreBidForAllSuppliers);
			List<RfaEventSupplier> eventSuppliers = rfaEventSupplierService.getAllRfaEventSuppliersListByEventId(eventId);
			if (isPresetPreBidForAllSuppliers) {
				String bqId = null;
				if (CollectionUtil.isEmpty(rfaEventBqs)) {
					redir.addFlashAttribute("error", messageSource.getMessage("create.bq.before.assign", new Object[] {}, Global.LOCALE));
					return "redirect:/buyer/RFA/createBQList/" + eventId;
				}
				if (CollectionUtil.isEmpty(eventSuppliers)) {
					redir.addFlashAttribute("error", messageSource.getMessage("add.supplier.bq.before.assign", new Object[] {}, Global.LOCALE));
					return "redirect:/buyer/RFA/createBQList/" + eventId;
				}
				for (RfaEventBq rfaEventBq : rfaEventBqs) {
					bqId = rfaEventBq.getId();
					if (CollectionUtil.isEmpty(rfaEventBq.getBqItems())) {
						redir.addFlashAttribute("error", messageSource.getMessage("add.bq.item.before.assign.prebid.price", new Object[] {}, Global.LOCALE));
						return "redirect:/buyer/RFA/createBQList/" + eventId;
					}
				}
				List<RfaSupplierBq> supplierBqList = rfaSupplierBqService.findRfaSummarySupplierBqbyEventId(eventId);
				if (CollectionUtil.isEmpty(supplierBqList)) {
					rfaSupplierBqItemService.saveSupplierBqDetails(bqId, eventSuppliers);
				} else {
					if (eventSuppliers.size() != supplierBqList.size()) {
						for (RfaEventSupplier rfaEventSupplier : eventSuppliers) {
							List<RfaSupplierBqItem> supplierBqItem = null;
							supplierBqItem = rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqId, rfaEventSupplier.getSupplier().getId());
							if (CollectionUtil.isEmpty(supplierBqItem)) {
								rfaSupplierBqItemService.saveSupplierEventBqByBuyer(bqId, rfaEventSupplier.getSupplier().getId());
							}
						}
					}
				}
				List<RfaSupplierBq> supplierBqsList = rfaSupplierBqService.findRfaSummarySupplierBqbyEventId(eventId);
				RfaEventSupplier currentSupplier = null;
				for (RfaEventSupplier rfaEventSupplier : eventSuppliers) {
					if (rfaEventSupplier.getSupplier().getId().equals(supplierBqsList.get(0).getSupplier().getId())) {
						currentSupplier = rfaEventSupplier;
						break;
					}
				}
				rfaSupplierBq = rfaSupplierBqService.getSupplierBqByBqAndSupplierId(bqId, supplierBqsList.get(0).getSupplier().getId());
				model.addAttribute("event", rfaEventService.getRfaEventByeventId(eventId));
				model.addAttribute("auctionRules", rfaEventService.getLeanAuctionRulesByEventId(eventId));
				model.addAttribute("currentSupplier", currentSupplier);
				model.addAttribute("showSave", true);
				model.addAttribute("supplierId", rfaSupplierBq.getSupplier().getId());
				model.addAttribute("taxTypeList", TaxType.values());
			}

			if (isBqItemExists) {
				model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				model.addAttribute("rfaSupplierBq", rfaSupplierBq);
				model.addAttribute("eventSuppliers", eventSuppliers);
				return "eventBqForSupplier";

			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("rfabq.add.bq.item", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/RFA/createBQList/" + eventId;
			}
		} catch (Exception e) {
			LOG.error("Error while getting event bq supplier pre bid:" + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
			return "redirect:/buyer/RFA/createBQList/" + eventId;

		}
	}

	@RequestMapping(value = "/getRfaSupplierBqItem/{eventId}", method = RequestMethod.POST)
	public String getRfaSupplierBqItem(@PathVariable("eventId") String eventId, @RequestParam("supplierId") String supplierId, Model model, RedirectAttributes redir) {
		List<RfaSupplierBqItem> supplierBqItem = null;
		String bqId = null;

		RfaSupplierBq rfaSupplierBq = null;
		try {
			if (StringUtils.checkString(supplierId).length() == 0) {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.select.supplier", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/RFA/eventBqForSupplier/" + eventId;
			}
			List<RfaEventBq> rfaEventBqs = rfaBqService.getAllBqListByEventIdByOrder(eventId);
			if (CollectionUtil.isEmpty(rfaEventBqs)) {
				redir.addFlashAttribute("error", messageSource.getMessage("create.bq.before.assign", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/RFA/eventBqForSupplier/" + eventId;
			}
			for (RfaEventBq rfaEventBq : rfaEventBqs) {
				bqId = rfaEventBq.getId();
			}
			supplierBqItem = rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqId, supplierId);
			if (CollectionUtil.isEmpty(supplierBqItem)) {
				rfaSupplierBqItemService.saveSupplierEventBqByBuyer(bqId, supplierId);
			}
			RfaEventSupplier currentSupplier = null;
			List<RfaEventSupplier> eventSuppliers = rfaEventSupplierService.getAllRfaEventSuppliersListByEventId(eventId);
			for (RfaEventSupplier rfaEventSupplier : eventSuppliers) {
				if (rfaEventSupplier.getSupplier().getId().equals(supplierId)) {
					currentSupplier = rfaEventSupplier;
					break;
				}
			}
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			rfaSupplierBq = rfaSupplierBqService.getSupplierBqByBqAndSupplierId(bqId, supplierId);
			model.addAttribute("event", rfaEventService.getRfaEventByeventId(eventId));
			model.addAttribute("auctionRules", rfaEventService.getLeanAuctionRulesByEventId(eventId));
			model.addAttribute("eventSuppliers", eventSuppliers);
			model.addAttribute("currentSupplier", currentSupplier);
			model.addAttribute("showSave", true);
		} catch (Exception e) {
			LOG.info("Error : " + e.getMessage(), e);
		}
		model.addAttribute("rfaSupplierBq", rfaSupplierBq);
		model.addAttribute("supplierId", supplierId);
		model.addAttribute("taxTypeList", TaxType.values());

		// return "redirect:/buyer/RFA/eventBqForSupplier/" + eventId + "/" + supplierId + "/" + bqId;
		return "eventBqForSupplier";
	}

	@RequestMapping(path = "submitSupplierBq/{eventId}", method = RequestMethod.POST)
	public String submitEnglishAuction(@PathVariable String eventId, @RequestParam String supplierId, @ModelAttribute("rfaSupplierBq") RfaSupplierBq rfaSupplierBq, Model model, RedirectAttributes redir) {
		try {
			RfaEvent event = rfaEventService.getRfaEventById(eventId);
			if (rfaSupplierBq != null) {

				rfaSupplierBqItemService.updateBqForSupplier(rfaSupplierBq, supplierId, event.getDecimal(), eventId);
				rfaSupplierBq = rfaSupplierBqService.getSupplierBqByBqAndSupplierId(rfaSupplierBq.getBq().getId(), supplierId);
				model.addAttribute("rfaSupplierBq", rfaSupplierBq);
			}
			model.addAttribute("event", event);
			model.addAttribute("eventSuppliers", rfaEventSupplierService.getAllRfaEventSuppliersListByEventId(eventId));
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.submitted.initial.price", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("rfabq.error.while.submitting", new Object[] {}, Global.LOCALE));
			return "eventBqForSupplier";
		}
		LOG.info("Sucessfully Submited ");

		model.addAttribute("supplierId", supplierId);
		model.addAttribute("taxTypeList", TaxType.values());

		return "redirect:/buyer/RFA/createBQList/" + eventId;
	}

	@RequestMapping(path = "discaredSuppliersInitialPrice/{eventId}", method = RequestMethod.POST)
	public String discaredSuppliersInitialPrice(@PathVariable String eventId, RedirectAttributes redir) {
		try {
			rfaSupplierBqService.discardSupplierBqforEventId(eventId);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.discarded.intital.price", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while discarding supplier initial price " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.discarding.supplier.initialprice", new Object[] {}, Global.LOCALE));
		}
		return "redirect:/buyer/RFA/createBQList/" + eventId;
	}

	@RequestMapping(path = "discaredSuppliersPreBidInitialPrice/{eventId}", method = RequestMethod.POST)
	public ResponseEntity<String> discaredSuppliersPreBidInitialPrice(@PathVariable String eventId) {
		HttpHeaders headers = new HttpHeaders();
		try {
			rfaSupplierBqService.discardSupplierBqforEventId(eventId);
			headers.add("success", messageSource.getMessage("flashsuccess.discarded.intital.price", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<String>("success", headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error while discarding supplier initial price " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("flasherror.discarding.supplier.initialprice", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@RequestMapping(path = "/getEventBqbyId", method = RequestMethod.POST)
	public ResponseEntity<RfaEventBq> getEventBqbyId(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId) {
		HttpHeaders headers = new HttpHeaders();
		RfaEventBq bq = rfaBqService.getRfaBqById(bqId);
		return new ResponseEntity<RfaEventBq>(bq, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getBqItemForSearchFilter", method = RequestMethod.POST)
	public ResponseEntity<RfaBqItemResponsePojo> getBqItemForSearchFilter(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
		LOG.info(" getEventBqForChoosenFilterValue  : " + bqId + " eventId :" + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		HttpHeaders headers = new HttpHeaders();
		List<RfaBqItem> bqItemList = null;
		// sending total Bq Item count and Bq Item list bind in new Pojo class
		RfaBqItemResponsePojo bqItemResponsePojo = new RfaBqItemResponsePojo();
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
			bqItemList = rfaBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
			bqItemResponsePojo.setBqItemList(bqItemList);
			long totalBqItemCount = rfaBqService.totalBqItemCountByBqId(bqId, searchVal);
			LOG.info("totalBqItemCount :" + totalBqItemCount);
			bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
			List<BqItemPojo> leveLOrderList = rfaBqService.getAllLevelOrderBqItemByBqId(bqId, searchVal);
			bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
			for (RfaBqItem bqItem : bqItemList) {
				if (bqItem.getUom() != null) {
					bqItem.getUom().setCreatedBy(null);
					bqItem.getUom().setModifiedBy(null);
				}
			}
			if (pageLength != SecurityLibrary.getLoggedInUser().getBqPageLength()) {
				userService.updateUserBqPageLength(pageLength, SecurityLibrary.getLoggedInUser().getId());
				super.updateSecurityLibraryUser(pageLength);
			}
			return new ResponseEntity<RfaBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during Search Reset Bill Of Quantity :" + e.getMessage(), e);
			headers.add("error", "Error during Search Bill Of Quantity ");
			return new ResponseEntity<RfaBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}