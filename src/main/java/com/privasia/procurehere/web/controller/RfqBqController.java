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
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.RfqBqItemResponsePojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfqEventService;

@Controller
@RequestMapping("/buyer/RFQ")
public class RfqBqController extends EventBqBase {

	@Autowired
	RfqEventService rfqEventService;

	public RfqBqController() {
		super(RfxTypes.RFQ);
	}

	/**
	 * @param model
	 * @param eventId
	 * @param redir
	 * @return
	 */
	@RequestMapping(value = "/bqPrevious", method = RequestMethod.POST)
	public String bqPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		RfqEvent rftEvent = rfqBqService.getEventById(eventId);
		if (rftEvent != null) {
			return super.bqPrevious(rftEvent);
		} else {
			LOG.error("Event not found redirecting to login ");
			return "/";
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
	 * @param model
	 * @param eventId
	 * @param redir
	 * @return
	 */
	@RequestMapping(value = "/bqNext", method = RequestMethod.POST)
	public String bqNext(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		RfqEvent event = rfqBqService.getEventById(eventId);
		if (event != null) {
			event.setBqCompleted(Boolean.TRUE);
			rfqEventService.updateEvent(event);
			return super.bqNext(event);
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
		RfqEvent rftEvent = rfqBqService.getEventById(eventId);
		List<RfqEventBq> bqEventList = rfqBqService.getAllBqListByEventIdByOrder(eventId);
		model.addAttribute("event", rftEvent);
		model.addAttribute("bqEventList", bqEventList);
		model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		return "createBQList";
	}

	/**
	 * @param rftBqPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/createBQList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Bq>> saveBQList(@RequestBody BqPojo rftBqPojo) throws JsonProcessingException {
		try {

			if (!rfqBqService.isBqExists(rftBqPojo)) {
				List<RfqEventBq> bqList = rfqBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
				Integer count = 1;
				if (CollectionUtil.isNotEmpty(bqList)) {
					for (RfqEventBq bq : bqList) {
						if (bq.getBqOrder() == null) {
							bq.setBqOrder(count);
							rfqBqService.updateBq(bq);
							count++;
						}
					}
					count = bqList.size();
					count++;
				}
				super.saveBq(rftBqPojo, count);
				List<Bq> bqListNew = rfqBqService.findBqbyEventId(rftBqPojo.getEventId());
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.bq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<Bq>>(bqListNew, headers, HttpStatus.OK);
			} else {
				List<Bq> bqList = rfqBqService.findBqbyEventId(rftBqPojo.getEventId());
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[] { rftBqPojo.getBqName() }, Global.LOCALE));
				return new ResponseEntity<List<Bq>>(bqList, headers, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<Bq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param eventId
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/createEventBQ/{eventId}/{bqId}", method = RequestMethod.GET)
	public String createEventBQ(@PathVariable String eventId, @PathVariable String bqId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0 || StringUtils.checkString(bqId).length() == 0) {
			return "redirect:/400_error";
		}
		Integer start = 0;
		Integer length = 50;
		Integer pageNo = 1;
		LOG.info("BqId : " + bqId);

		RfqEvent rftEvent = rfqBqService.getEventById(eventId);
		RfqEventBq eventBq = rfqBqService.getBqById(bqId);
		List<RfqBqItem> returnList = new ArrayList<>();
		length = SecurityLibrary.getLoggedInUser().getBqPageLength();
		LOG.info(" BQ Page length : " + length);
		List<RfqBqItem> bqList = rfqBqService.getBqItemForSearchFilter(bqId, null, null, null, start, length, pageNo);
		buildBqItemForSearch(returnList, bqList);
		// List<RfqBqItem> bqList = rfqBqService.findBqbyBqId(bqId);
		model.addAttribute("rftBqItem", new RftBqItem());
		model.addAttribute("bqList", returnList);
		super.buildModel(eventId, bqId, model, rftEvent, eventBq);
		List<BqItemPojo> leveLOrderList = rfqBqService.getAllLevelOrderBqItemByBqId(bqId, null);
		model.addAttribute("leveLOrderList", leveLOrderList);
		long totalBqItemCount = rfqBqService.totalBqItemCountByBqId(bqId, null);
		model.addAttribute("eventPermissions", rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("totalBqItemCount", totalBqItemCount);
		model.addAttribute("event", rftEvent);
		LOG.info("totalBqItemCount :" + totalBqItemCount);
		return "createEventBQ";
	}

	/**
	 * @param returnList
	 * @param bqList
	 */
	private void buildBqItemForSearch(List<RfqBqItem> returnList, List<RfqBqItem> bqList) {
		RfqBqItem parent = null;
		for (RfqBqItem item : bqList) {
			if (item.getOrder() != 0) {
				if (parent != null) {
					parent.getChildren().add(item.createSearchShallowCopy());
				}
			} else {
				parent = item.createSearchShallowCopy();
				parent.setChildren(new ArrayList<RfqBqItem>());
				returnList.add(parent);
			}
		}
	}

	/**
	 * @param rftBqItemPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/createEventBQ", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RfqBqItemResponsePojo> saveRftEventBQ(@RequestBody BqItemPojo rftBqItemPojo, Model model) throws JsonProcessingException {

		try {
			if (StringUtils.checkString(rftBqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(rftBqItemPojo.getParent()).length() == 0) {
				HttpHeaders header = new HttpHeaders();
				header.add("error", messageSource.getMessage("event.bqsection.required", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfqBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
			}

			if (rftBqItemPojo.getQuantity() != null && rftBqItemPojo.getUnitPrice() != null) {
				HttpHeaders header = new HttpHeaders();
				java.math.BigDecimal unitPrice = rftBqItemPojo.getUnitPrice();
				java.math.BigDecimal qnty = rftBqItemPojo.getQuantity();
				if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
					LOG.info("VALUE IS " + qnty.multiply(unitPrice));
					header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[] {}, null));
					return new ResponseEntity<RfqBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}
			LOG.info("Crate EVENT BQ ID :: " + rftBqItemPojo.getBq());
			LOG.info(" Filter :" + rftBqItemPojo.getFilterVal() + " Search :" + rftBqItemPojo.getSearchVal());
			RfqBqItem rftBqItem = new RfqBqItem();
			rftBqItem.setId(rftBqItemPojo.getId());
			rftBqItem.setRfxEvent(rfqBqService.getEventById(rftBqItemPojo.getRftEvent()));
			rftBqItem.setBq(rfqBqService.getBqItembyId(rftBqItemPojo.getBq()));
			RfqEvent rfqEvent = rfqEventService.getRfqEventByeventId(rftBqItemPojo.getRftEvent());
			model.addAttribute("event", rfqEvent);
			/*************** To check whether quantity of Bqitem entered should not be 0 ****************/
			if (rftBqItemPojo.getQuantity() != null) {
				LOG.info("rftBqItem.setBq***************" + rftBqItemPojo.getQuantity());
				HttpHeaders header = new HttpHeaders();

				java.math.BigDecimal i = rftBqItemPojo.getQuantity();
				java.math.BigDecimal c = java.math.BigDecimal.ZERO;
				int res = i.compareTo(c);
				if (res == 0) {
					header.add("error", messageSource.getMessage("buyer.rftbq.quantity", new Object[] { rftBqItemPojo.getQuantity() }, Global.LOCALE));
					return new ResponseEntity<RfqBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			constructBqItemValues(rftBqItemPojo, rftBqItem);
			if (StringUtils.checkString(rftBqItemPojo.getParent()).length() > 0) {
				rftBqItem.setParent(rfqBqService.getBqItemsbyBqId(StringUtils.checkString(rftBqItemPojo.getParent())));
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
			RfqBqItemResponsePojo bqItemResponsePojo = new RfqBqItemResponsePojo();

			if (!rfqBqService.isBqItemExists(rftBqItem, rftBqItemPojo.getBq(), rftBqItemPojo.getParent())) {

				rfqBqService.saveBqItem(rftBqItem);
				rftBqItemPojo.setStart(0);
				LOG.info(" Strat :" + rftBqItemPojo.getStart() + " lenth :" + rftBqItemPojo.getPageLength() + " Page Number :" + rftBqItemPojo.getPageNo());
				List<RfqBqItem> bqItemList = rfqBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), itemLevel, itemOrder, rftBqItemPojo.getSearchVal(), rftBqItemPojo.getStart(), rftBqItemPojo.getPageLength(), rftBqItemPojo.getPageNo());

				List<BqItemPojo> leveLOrderList = rfqBqService.getAllLevelOrderBqItemByBqId(rftBqItemPojo.getBq(), rftBqItemPojo.getSearchVal());
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItemList);

				// List<RfqBqItem> bqList = rfqBqService.findBqbyBqId(rftBqItemPojo.getBq());
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("buyer.rftbq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfqBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);

			} else {
				List<RfqBqItem> bqItemList = rfqBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), itemLevel, itemOrder, rftBqItemPojo.getSearchVal(), rftBqItemPojo.getStart(), rftBqItemPojo.getPageLength(), rftBqItemPojo.getPageNo());

				List<BqItemPojo> leveLOrderList = rfqBqService.getAllLevelOrderBqItemByBqId(rftBqItemPojo.getBq(), rftBqItemPojo.getSearchVal());
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItemList);

				// List<RfqBqItem> bqList = rfqBqService.findBqbyBqId(rftBqItemPojo.getBq());
				LOG.info("Validate of BQ LIST :: " + rftBqItemPojo.toString());
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("buyer.rftbq.duplicate", new Object[] { rftBqItemPojo.getItemName() }, Global.LOCALE));
				return new ResponseEntity<RfqBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfqBqItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param bqItemOrder
	 * @return
	 */
	@RequestMapping(path = "/eventBQOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfqBqItem>> eventBQOrder(@RequestBody BqItemPojo bqItemPojo) {
		LOG.info("rftBqItemPojo.getRfqEvent() :" + bqItemPojo.getRftEvent() + " Parent : " + bqItemPojo.getParent() + " Item Id : " + bqItemPojo.getId() + " New Position : " + bqItemPojo.getOrder());
		HttpHeaders headers = new HttpHeaders();
		List<RfqBqItem> bqList = null;
		Integer start = null;
		Integer length = null;
		try {
			if (StringUtils.checkString(bqItemPojo.getId()).length() > 0) {
				start = 0;
				length = bqItemPojo.getPageLength();
				LOG.info("Updating order..........................");
				RfqBqItem bqItem = rfqBqService.getBqItembyBqItemId(bqItemPojo.getId());
				if (bqItem.getOrder() > 0 && StringUtils.checkString(bqItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					List<RfqBqItem> returnList = rfqBqService.getBqItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
					return new ResponseEntity<List<RfqBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
					// return new
					// ResponseEntity<List<RfqBqItem>>(rfqBqService.findBqbyBqId(rfqBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId()),
					// headers, HttpStatus.BAD_REQUEST);
				}
				if (bqItem.getOrder() == 0 && StringUtils.checkString(bqItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					List<RfqBqItem> returnList = rfqBqService.getBqItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
					return new ResponseEntity<List<RfqBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
					// return new
					// ResponseEntity<List<RfqBqItem>>(rfqBqService.findBqbyBqId(rfqBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId()),
					// headers, HttpStatus.BAD_REQUEST);
				}
				RfqBqItem item = new RfqBqItem();
				item.setId(bqItemPojo.getId());
				item.setRfxEvent(rfqBqService.getEventById(bqItemPojo.getRftEvent()));
				item.setItemName(bqItemPojo.getItemName());
				constructBqItemValues(bqItemPojo, item);
				if (bqItem.getParent() != null && !bqItem.getParent().getId().equals(bqItemPojo.getParent()) && rfqBqService.isBqItemExists(item, bqItemPojo.getBq(), bqItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new NotAllowedException("Duplicate BQ Item. BQ Item by that name already exists.");
				}
				rfqBqService.reorderBqItems(bqItemPojo);
				bqList = rfqBqService.getBqItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
				// bqList =
				// rfqBqService.findBqbyBqId(rfqBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqBqItem>>(bqList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException e) {
			LOG.error("Error while moving BQ parent with item as Child : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.drag", new Object[] { e.getMessage() }, Global.LOCALE));
			List<RfqBqItem> returnList = rfqBqService.getBqItemForSearchFilter(bqItemPojo.getBq(), null, null, null, start, length, bqItemPojo.getPageNo());
			return new ResponseEntity<List<RfqBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
			// bqList =
			// rfqBqService.findBqbyBqId(rfqBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId());
			// return new ResponseEntity<List<RfqBqItem>>(bqList, headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("Error while moving BQ Item : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfqBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RfqBqItem>>(bqList, headers, HttpStatus.OK);
	}

	/**
	 * @param model
	 * @param bqId
	 * @param eventId
	 * @param redir
	 * @return
	 */
	@RequestMapping(value = "/showBqItems", method = RequestMethod.POST)
	public String showBqItems(Model model, @RequestParam(name = "bqId", required = true) String bqId, @RequestParam(name = "eventId", required = true) String eventId) {
		RfqEvent rftEvent = rfqBqService.getEventById(eventId);
		if (rftEvent != null) {
			return "redirect:createEventBQ/" + rftEvent.getId() + "/" + bqId;
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
	public @ResponseBody ResponseEntity<RfqBqItemResponsePojo> updateBqItem(@RequestBody BqItemPojo bqItemPojo) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(bqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(bqItemPojo.getParent()).length() == 0) {
				HttpHeaders header = new HttpHeaders();
				header.add("error", messageSource.getMessage("event.bqsection.required", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfqBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
			}

			if (bqItemPojo.getQuantity() != null && bqItemPojo.getUnitPrice() != null) {
				HttpHeaders header = new HttpHeaders();
				java.math.BigDecimal unitPrice = bqItemPojo.getUnitPrice();
				java.math.BigDecimal qnty = bqItemPojo.getQuantity();
				if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
					LOG.info("VALUE IS " + qnty.multiply(unitPrice));
					header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[] {}, null));
					return new ResponseEntity<RfqBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			if (bqItemPojo.getQuantity() != null && bqItemPojo.getUnitPrice() != null) {
				HttpHeaders header = new HttpHeaders();
				java.math.BigDecimal unitPrice = bqItemPojo.getUnitPrice();
				java.math.BigDecimal qnty = bqItemPojo.getQuantity();
				if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
					LOG.info("VALUE IS " + qnty.multiply(unitPrice));
					header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[] {}, null));
					return new ResponseEntity<RfqBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			LOG.info(" :: UPDATE BILL OF QUNATITY ::" + bqItemPojo.toString());
			RfqBqItem rftBq = rfqBqService.getBqItemById(bqItemPojo.getId());

			rftBq.setUom(StringUtils.checkString(bqItemPojo.getUom()).length() > 0 ? uomService.getUombyCode(bqItemPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()) : null);
			super.buildUpdateBqItem(bqItemPojo, rftBq);

			RfqBqItem item = new RfqBqItem();
			item.setId(bqItemPojo.getId());
			item.setRfxEvent(rfqBqService.getEventById(bqItemPojo.getRftEvent()));
			item.setItemName(bqItemPojo.getItemName());

			/*************** To check whether quantity of Bqitem entered should not be 0 ****************/

			if (bqItemPojo.getQuantity() != null) {
				LOG.info("rftBqItem.setBq***************" + bqItemPojo.getQuantity());
				HttpHeaders header = new HttpHeaders();

				java.math.BigDecimal i = bqItemPojo.getQuantity();
				java.math.BigDecimal c = java.math.BigDecimal.ZERO;
				int res = i.compareTo(c);
				if (res == 0) {
					header.add("error", messageSource.getMessage("buyer.rftbq.quantity", new Object[] { bqItemPojo.getQuantity() }, Global.LOCALE));
					return new ResponseEntity<RfqBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			constructBqItemValues(bqItemPojo, item);
			if (rfqBqService.isBqItemExists(item, bqItemPojo.getBq(), bqItemPojo.getParent())) {
				LOG.info("Duplicate....");
				throw new ApplicationException("Duplicate BQ Item. BQ Item by that name already exists.");
			}
			rfqBqService.updateBqItem(rftBq);
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
			List<RfqBqItem> bqItemList = rfqBqService.getBqItemForSearchFilter(bqItemPojo.getBq(), itemLevel, itemOrder, bqItemPojo.getSearchVal(), bqItemPojo.getStart(), bqItemPojo.getPageLength(), bqItemPojo.getPageNo());

			// sending level order list and bq Item list bind in new Pojo class
			RfqBqItemResponsePojo bqItemResponsePojo = new RfqBqItemResponsePojo();

			List<BqItemPojo> leveLOrderList = rfqBqService.getAllLevelOrderBqItemByBqId(bqItemPojo.getBq(), null);
			bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
			bqItemResponsePojo.setBqItemList(bqItemList);

			// LOG.info("EVENT ALL LIST :: " + bqList.toString());
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", messageSource.getMessage("buyer.rftbq.save", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RfqBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error while updating BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfqBqItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param rftBqPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/updateBqList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfqEventBq>> updateBqList(@RequestBody BqPojo rftBqPojo) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("UPDATE RFP BQ :: " + rftBqPojo.toString());
			RfqEventBq rftBq = rfqBqService.getBqById(rftBqPojo.getId());
			rftBq.setName(rftBqPojo.getBqName());
			rftBq.setDescription(rftBqPojo.getBqDesc());
			rftBq.setModifiedDate(new Date());
			if (bqValidate(rftBqPojo)) {
				rfqBqService.updateBq(rftBq);
				List<RfqEventBq> bqList = rfqBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
				headers.add("success", messageSource.getMessage("rft.bq.update", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqEventBq>>(bqList, headers, HttpStatus.OK);
			} else {
				List<RfqEventBq> bqList = rfqBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
				headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[] { rftBqPojo.getBqName() }, Global.LOCALE));
				return new ResponseEntity<List<RfqEventBq>>(bqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("rft.bq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfqEventBq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param rftBqPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/deleteRftBq", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfqEventBq>> deleteRftBq(@RequestBody BqPojo rftBqPojo) throws JsonProcessingException {
		RfqEventBq bqEvent = rfqBqService.getBqById(rftBqPojo.getId());
		try {
			try {
				rfqBqService.isAllowtoDeleteBQ(rftBqPojo.getId(), RfxTypes.RFQ);
			} catch (Exception e) {
				LOG.error("Error while deleting BQ  :" + e.getMessage(), e);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", e.getMessage());
				return new ResponseEntity<List<RfqEventBq>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			}
			// if (bqEvent.getRfxEvent().getStatus() != EventStatus.DRAFT) {
			// HttpHeaders headers = new HttpHeaders();
			// headers.add("info", messageSource.getMessage("rft.bq.info.draft", new Object[] { bqEvent.getName() },
			// Global.LOCALE));
			// return new ResponseEntity<List<RfqEventBq>>(null, headers, HttpStatus.OK);
			// } else {
			rfqBqService.deleteBq(bqEvent.getId());
			LOG.info(" After delete BQ display remaining eventID :: " + rftBqPojo.getEventId());
			List<RfqEventBq> bqList = rfqBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
			Integer count = 1;
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (RfqEventBq bq : bqList) {
					bq.setBqOrder(count);
					rfqBqService.updateBq(bq);
					count++;
				}
			}
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", messageSource.getMessage("rft.bq.success.delete", new Object[] { bqEvent.getName() }, Global.LOCALE));
			return new ResponseEntity<List<RfqEventBq>>(bqList, headers, HttpStatus.OK);
			// }

		} catch (Exception e) {
			LOG.error("Error while deleting BQ  [ " + bqEvent.getName() + " ]" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.error.delete", new Object[] { bqEvent.getName() }, Global.LOCALE));
			return new ResponseEntity<List<RfqEventBq>>(null, headers, HttpStatus.OK);
		}

	}

	/**
	 * @param rftBqItemPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/addNewColumns", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfqBqItem>> addNewColumns(@RequestBody BqPojo rfqBq) throws JsonProcessingException {
		LOG.info("Enter this Block :: " + rfqBq.toString());
		HttpHeaders headers = new HttpHeaders();
		List<RfqBqItem> bqItemList = null;
		if (rfqBq.getField1Label() == null && rfqBq.getField2Label() == null && rfqBq.getField3Label() == null && rfqBq.getField4Label() == null) {
			headers.add("error", "Field cannot be empty");
			return new ResponseEntity<List<RfqBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		} else {
			RfqEventBq rftEventBq = rfqBqService.getBqById(rfqBq.getId());
			if (rftEventBq != null) {
				try {

					List<String> fieldLabelList = new ArrayList<String>();
					if (rfqBq.getField1Label() != null && fieldLabelList.contains(rfqBq.getField1Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField1Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfqBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfqBq.getField1Label() != null) {
						fieldLabelList.add(rfqBq.getField1Label().toLowerCase());
					}
					if (rfqBq.getField2Label() != null && fieldLabelList.contains(rfqBq.getField2Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField2Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfqBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfqBq.getField2Label() != null) {
						fieldLabelList.add(rfqBq.getField2Label().toLowerCase());
					}
					if (rfqBq.getField3Label() != null && fieldLabelList.contains(rfqBq.getField3Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField3Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfqBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfqBq.getField3Label() != null) {
						fieldLabelList.add(rfqBq.getField3Label().toLowerCase());
					}
					if (rfqBq.getField4Label() != null && fieldLabelList.contains(rfqBq.getField4Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField4Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfqBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfqBq.getField4Label() != null) {
						fieldLabelList.add(rfqBq.getField4Label().toLowerCase());
					}

					super.buildAddNewColumns(rfqBq, rftEventBq);
					rfqBqService.updateRfqBqFields(rftEventBq);

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
					bqItemList = rfqBqService.getBqItemForSearchFilter(rftEventBq.getId(), itemLevel, itemOrder, rfqBq.getSearchVal(), start, length, rfqBq.getPageNo());
					for (RfqBqItem bqItem : bqItemList) {
						if (bqItem.getUom() != null) {
							bqItem.getUom().setCreatedBy(null);
							bqItem.getUom().setModifiedBy(null);
						}
					}
					headers.add("success", messageSource.getMessage("rft.bq.newfields.success", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfqBqItem>>(bqItemList, headers, HttpStatus.OK);
				} catch (Exception e) {
					headers.add("error", messageSource.getMessage("buyer.rftbq.newfields.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfqBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
				}
			}
		}
		return new ResponseEntity<List<RfqBqItem>>(bqItemList, headers, HttpStatus.OK);

	}

	/**
	 * @param bqId
	 * @param bqItemId
	 * @return
	 */
	@RequestMapping(path = "/getBqForEdit", method = RequestMethod.GET)
	public ResponseEntity<RfqBqItem> getBqForEdit(@RequestParam String bqId, @RequestParam String bqItemId) {
		RfqBqItem rftBqItem = null;
		LOG.info("BQ EDIT :: bqId" + bqId + " :: bqItemId :: " + bqItemId);
		try {
			rftBqItem = rfqBqService.getBqItembyBqIdAndBqItemId(bqItemId);
			rftBqItem = rftBqItem.createShallowCopy();
		} catch (Exception e) {
			LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfqBqItem>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (rftBqItem != null) {
			return new ResponseEntity<RfqBqItem>(rftBqItem, HttpStatus.OK);
		} else {
			LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RfqBqItem>(null, headers, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @param items
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/deleteBqItems/{bqId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RfqBqItemResponsePojo> deleteBqItems(@RequestParam(name = "items", required = true) String items, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo, @PathVariable("bqId") String bqId, @RequestParam(name = "allDelete", required = false) boolean allDelete) throws JsonProcessingException {
		LOG.info("DELETE BQ ITEMS IDs :: " + items);
		List<RfqBqItem> bqItems = null;
		HttpHeaders headers = new HttpHeaders();

		if (StringUtils.checkString(filterVal).length() > 0) {
			if (filterVal.contains(".")) {
				filterVal = "";
			}
		}
		// sending level order list and bq Item list bind in new Pojo class
		RfqBqItemResponsePojo bqItemResponsePojo = new RfqBqItemResponsePojo();

		if (StringUtils.checkString(items).length() > 0) {
			try {
				String[] bqItemsArr = items.split(",");

				LOG.info(" filterVal  :" + filterVal + " searchVal: " + searchVal + "=====>" + allDelete);

				if (allDelete) {
					rfqBqService.deleteAllBqItems(bqId);
				} else {
					rfqBqService.deleteBqItems(bqItemsArr, bqId);
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

				bqItems = rfqBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
				List<BqItemPojo> leveLOrderList = rfqBqService.getAllLevelOrderBqItemByBqId(bqId, searchVal);
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItems);

			} catch (NotAllowedException e) {
				LOG.error("Not Allowed Exception: " + e.getMessage(), e);
				headers.add("error", e.getMessage());
				return new ResponseEntity<RfqBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (Exception e) {
				LOG.error("Error while delete bq items: " + e.getMessage(), e);
				headers.add("error", "Error during delete : " + messageSource.getMessage("rft.bqitems.error.delete", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfqBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<RfqBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/bqSaveDraft", method = RequestMethod.POST)
	public String bqSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId) {
		RfqEvent rftEvent = rfqBqService.getEventById(eventId);
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
		return "redirect:createBQList/" + eventId;

	}

	/**
	 * @param bqId
	 * @param label
	 * @return
	 */
	@RequestMapping(path = "/deleteBqNewField", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RfqBqItem>> deleteBqNewField(@RequestParam("bqId") String bqId, @RequestParam("label") String label, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
		LOG.info("deleteBqNewField label ::" + label + " bqId :: " + bqId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		HttpHeaders headers = new HttpHeaders();
		List<RfqBqItem> bqItemList = null;
		try {
			if (StringUtils.checkString(label).length() > 0) {
				rfqBqService.deletefieldInBqItems(bqId, label);
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
				bqItemList = rfqBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
				for (RfqBqItem bqItem : bqItemList) {
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
			return new ResponseEntity<List<RfqBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<RfqBqItem>>(bqItemList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getBqForNewFields", method = RequestMethod.GET)
	public ResponseEntity<RfqEventBq> getBqForNewFields(@RequestParam String bqId) {
		RfqEventBq eventBq = null;
		LOG.info("BQ EDIT :: bqId" + bqId);
		try {
			eventBq = rfqBqService.getBqById(bqId);
		} catch (Exception e) {
			LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfqEventBq>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (eventBq != null) {
			return new ResponseEntity<RfqEventBq>(eventBq, HttpStatus.OK);
		} else {
			LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RfqEventBq>(null, headers, HttpStatus.NOT_FOUND);
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

			super.eventDownloader(workbook, bqId, RfxTypes.RFQ);

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
		List<RfqBqItem> bqItemList = null;
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
					totalBqItemCount = super.bqItemsUpload(file, bqId, eventId, RfxTypes.RFQ);
					if (totalBqItemCount == 0) {
						LOG.info("totalBqItemCount " + totalBqItemCount);
						throw new Exception("Please fill data in excel file");
					}

				} catch (Exception e) {
					// e.printStackTrace();
					// bqList = rfqBqService.findBqbyBqId(bqId);
					bqItemList = rfqBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
					for (RfqBqItem bqItem : bqItemList) {
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
			// LOG.info("Upload failed!" + e.getMessage());
			// bqList = rfqBqService.findBqbyBqId(bqId);
			bqItemList = rfqBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
			for (RfqBqItem bqItem : bqItemList) {
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
		bqItemList = rfqBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
		for (RfqBqItem bqItem : bqItemList) {
			if (bqItem.getUom() != null) {
				bqItem.getUom().setCreatedBy(null);
				bqItem.getUom().setModifiedBy(null);
			}
		}
		RfqEventBq bq = rfqBqService.getRfqEventBqByBqId(bqId);
		BqPojo rfqBqColumns = new BqPojo(bq);
		response.put("rftBqColumns", rfqBqColumns);

		headers.add("success", messageSource.getMessage("buyer.rftbq.upload.success", new Object[] {}, Global.LOCALE));
		response.put("list", bqItemList);
		response.put("totalBqItemCount", totalBqItemCount);
		return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getEventBqbyId", method = RequestMethod.POST)
	public ResponseEntity<RfqEventBq> getEventBqbyId(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId) {
		HttpHeaders headers = new HttpHeaders();
		RfqEventBq bq = rfqBqService.getBqById(bqId);
		return new ResponseEntity<RfqEventBq>(bq, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getBqItemForSearchFilter", method = RequestMethod.POST)
	public ResponseEntity<RfqBqItemResponsePojo> getBqItemForSearchFilter(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
		LOG.info(" getEventBqForChoosenFilterValue  : " + bqId + " eventId :" + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		HttpHeaders headers = new HttpHeaders();
		List<RfqBqItem> bqItemList = null;
		// sending total Bq Item count and Bq Item list bind in new Pojo class
		RfqBqItemResponsePojo bqItemResponsePojo = new RfqBqItemResponsePojo();
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
			bqItemList = rfqBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
			bqItemResponsePojo.setBqItemList(bqItemList);
			long totalBqItemCount = rfqBqService.totalBqItemCountByBqId(bqId, searchVal);
			LOG.info("totalBqItemCount :" + totalBqItemCount);
			bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
			List<BqItemPojo> leveLOrderList = rfqBqService.getAllLevelOrderBqItemByBqId(bqId, searchVal);
			bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
			for (RfqBqItem bqItem : bqItemList) {
				if (bqItem.getUom() != null) {
					bqItem.getUom().setCreatedBy(null);
					bqItem.getUom().setModifiedBy(null);
				}
			}
			if (pageLength != SecurityLibrary.getLoggedInUser().getBqPageLength()) {
				userService.updateUserBqPageLength(pageLength, SecurityLibrary.getLoggedInUser().getId());
				super.updateSecurityLibraryUser(pageLength);
			}
			return new ResponseEntity<RfqBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during Search Reset Bill Of Quantity :" + e.getMessage(), e);
			headers.add("error", "Error during Search Bill Of Quantity ");
			return new ResponseEntity<RfqBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/updateEventBqOrder/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfqEventBq>> updateEventBqOrder(@RequestBody String[] cqIds, @PathVariable("eventId") String eventId) {
		try {
			LOG.info("RFQ eventId : " + eventId);
			if (cqIds != null && cqIds.length > 0) {
				Integer count = 1;
				for (String cqId : cqIds) {
					RfqEventBq bq = rfqBqService.getBqById(cqId);
					bq.setBqOrder(count);
					rfqBqService.updateBq(bq);
					count++;
				}
				List<RfqEventBq> bqList = rfqBqService.getAllBqListByEventIdByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.bq.order", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqEventBq>>(bqList, headers, HttpStatus.OK);
			} else {
				List<RfqEventBq> bqList = rfqBqService.getAllBqListByEventIdByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.bq.order.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfqEventBq>>(bqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while Ordering BQ : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfqEventBq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

}
