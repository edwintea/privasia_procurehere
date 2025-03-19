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
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.RfpBqItemResponsePojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;

@Controller
@RequestMapping("/buyer/RFP")
public class RfpBqController extends EventBqBase {

	public RfpBqController() {
		super(RfxTypes.RFP);
	}

	/**
	 * @param model
	 * @param eventId
	 * @param redir
	 * @return
	 */
	@RequestMapping(value = "/bqPrevious", method = RequestMethod.POST)
	public String bqPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		RfpEvent rftEvent = rfpBqService.getRfpEventById(eventId);
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
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
				model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
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
		RfpEvent event = rfpBqService.getRfpEventById(eventId);
		if (event != null) {
			event.setBqCompleted(Boolean.TRUE);
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

			rfpEventService.updateEvent(event);
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
		RfpEvent rftEvent = rfpEventService.getEventById(eventId);
		List<RfpEventBq> bqEventList = rfpBqService.getAllBqListByEventIdByOrder(eventId);
		model.addAttribute("event", rftEvent);
		model.addAttribute("bqEventList", bqEventList);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

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

			if (!rfpBqService.isBqExists(rftBqPojo)) {
				List<RfpEventBq> bqList = rfpBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
				Integer count = 1;
				if (CollectionUtil.isNotEmpty(bqList)) {
					for (RfpEventBq bq : bqList) {
						if (bq.getBqOrder() == null) {
							bq.setBqOrder(count);
							rfpBqService.updateBq(bq);
							count++;
						}
					}
					count = bqList.size();
					count++;
				}
				super.saveBq(rftBqPojo, count);
				List<Bq> bqListNew = rfpBqService.findBqbyEventId(rftBqPojo.getEventId());
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.bq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<Bq>>(bqListNew, headers, HttpStatus.OK);
			} else {
				List<Bq> bqList = rfpBqService.findBqbyEventId(rftBqPojo.getEventId());
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

		RfpEvent rftEvent = rfpEventService.getEventById(eventId);
		RfpEventBq eventBq = rfpBqService.getBqById(bqId);
		List<RfpBqItem> returnList = new ArrayList<>();
		length = SecurityLibrary.getLoggedInUser().getBqPageLength();
		LOG.info(" BQ Page length : " + length);
		List<RfpBqItem> bqList = rfpBqService.getBqItemForSearchFilter(bqId, null, null, null, start, length, pageNo);
		buildBqItemForSearch(returnList, bqList);
		// List<RfpBqItem> bqList = rfpBqService.findRfpBqbyBqId(bqId);
		model.addAttribute("rftBqItem", new RftBqItem());
		model.addAttribute("bqList", returnList);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		super.buildModel(eventId, bqId, model, rftEvent, eventBq);
		List<BqItemPojo> leveLOrderList = rfpBqService.getAllLevelOrderBqItemByBqId(bqId, null);
		model.addAttribute("leveLOrderList", leveLOrderList);
		long totalBqItemCount = rfpBqService.totalBqItemCountByBqId(bqId, null);
		model.addAttribute("event", rftEvent);
		model.addAttribute("totalBqItemCount", totalBqItemCount);
		LOG.info("totalBqItemCount :" + totalBqItemCount);
		return "createEventBQ";
	}

	/**
	 * @param returnList
	 * @param bqList
	 */
	private void buildBqItemForSearch(List<RfpBqItem> returnList, List<RfpBqItem> bqList) {
		RfpBqItem parent = null;
		for (RfpBqItem item : bqList) {
			if (item.getOrder() != 0) {
				if (parent != null) {
					parent.getChildren().add(item.createSearchShallowCopy());
				}
			} else {
				parent = item.createSearchShallowCopy();
				parent.setChildren(new ArrayList<RfpBqItem>());
				returnList.add(parent);
			}
		}
	}

	/**
	 * @param bqItemPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/createEventBQ", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RfpBqItemResponsePojo> saveRftEventBQ(@RequestBody BqItemPojo bqItemPojo, Model model) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(bqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(bqItemPojo.getParent()).length() == 0) {
				HttpHeaders header = new HttpHeaders();
				header.add("error", messageSource.getMessage("event.bqsection.required", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfpBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
			}

			if (bqItemPojo.getQuantity() != null && bqItemPojo.getUnitPrice() != null) {
				HttpHeaders header = new HttpHeaders();
				java.math.BigDecimal unitPrice = bqItemPojo.getUnitPrice();
				java.math.BigDecimal qnty = bqItemPojo.getQuantity();
				if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
					LOG.info("VALUE IS " + qnty.multiply(unitPrice));
					header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[] {}, null));
					return new ResponseEntity<RfpBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			LOG.info("Crate EVENT BQ ID :: " + bqItemPojo.getBq());
			LOG.info(" Filter :" + bqItemPojo.getFilterVal() + " Search :" + bqItemPojo.getSearchVal());
			RfpBqItem rftBqItem = new RfpBqItem();
			rftBqItem.setId(bqItemPojo.getId());
			rftBqItem.setRfxEvent(rfpBqService.getRfpEventById(bqItemPojo.getRftEvent()));
			rftBqItem.setBq(rfpBqService.getBqItembyId(bqItemPojo.getBq()));
			RfpEvent rfpEvent = rfpEventService.getRfpEventByeventId(bqItemPojo.getRftEvent());
			model.addAttribute("event", rfpEvent);
			/*************** To check whether quantity of Bqitem entered should not be 0 ****************/

			if (bqItemPojo.getQuantity() != null) {
				LOG.info("rftBqItem.setBq***************" + bqItemPojo.getQuantity());
				HttpHeaders header = new HttpHeaders();

				java.math.BigDecimal i = bqItemPojo.getQuantity();
				java.math.BigDecimal c = java.math.BigDecimal.ZERO;
				int res = i.compareTo(c);
				if (res == 0) {
					header.add("error", messageSource.getMessage("buyer.rftbq.quantity", new Object[] { bqItemPojo.getQuantity() }, Global.LOCALE));
					return new ResponseEntity<RfpBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			constructBqItemValues(bqItemPojo, rftBqItem);
			if (StringUtils.checkString(bqItemPojo.getParent()).length() > 0) {
				rftBqItem.setParent(rfpBqService.getBqItemsbyBqId(StringUtils.checkString(bqItemPojo.getParent())));
			}
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

			// sending level order list and bq Item list bind in new Pojo class
			RfpBqItemResponsePojo bqItemResponsePojo = new RfpBqItemResponsePojo();

			if (!rfpBqService.isBqItemExists(rftBqItem, bqItemPojo.getBq(), bqItemPojo.getParent())) {
				rfpBqService.saveBqItem(rftBqItem);
				bqItemPojo.setStart(0);
				LOG.info(" Strat :" + bqItemPojo.getStart() + " lenth :" + bqItemPojo.getPageLength() + " Page Number :" + bqItemPojo.getPageNo());
				List<RfpBqItem> bqItemList = rfpBqService.getBqItemForSearchFilter(bqItemPojo.getBq(), itemLevel, itemOrder, bqItemPojo.getSearchVal(), bqItemPojo.getStart(), bqItemPojo.getPageLength(), bqItemPojo.getPageNo());
				List<BqItemPojo> leveLOrderList = rfpBqService.getAllLevelOrderBqItemByBqId(bqItemPojo.getBq(), bqItemPojo.getSearchVal());
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItemList);

				// List<RfpBqItem> bqList = rfpBqService.findRfpBqbyBqId(rftBqItemPojo.getBq());
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("buyer.rftbq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfpBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);

			} else {
				List<RfpBqItem> bqItemList = rfpBqService.getBqItemForSearchFilter(bqItemPojo.getBq(), itemLevel, itemOrder, bqItemPojo.getSearchVal(), bqItemPojo.getStart(), bqItemPojo.getPageLength(), bqItemPojo.getPageNo());
				List<BqItemPojo> leveLOrderList = rfpBqService.getAllLevelOrderBqItemByBqId(bqItemPojo.getBq(), bqItemPojo.getSearchVal());
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItemList);

				// List<RfpBqItem> bqList = rfpBqService.findRfpBqbyBqId(bqItemPojo.getBq());
				LOG.info("Validate of BQ LIST :: " + bqItemPojo.toString());
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("buyer.rftbq.duplicate", new Object[] { bqItemPojo.getItemName() }, Global.LOCALE));
				return new ResponseEntity<RfpBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfpBqItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param bqItemOrder
	 * @return
	 */
	@RequestMapping(path = "/eventBQOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpBqItem>> eventBQOrder(@RequestBody BqItemPojo rftBqItemPojo) {
		LOG.info("rftBqItemPojo.getRftEvent() :" + rftBqItemPojo.getRftEvent() + " Parent : " + rftBqItemPojo.getParent() + " Item Id : " + rftBqItemPojo.getId() + " New Position : " + rftBqItemPojo.getOrder());
		HttpHeaders headers = new HttpHeaders();
		List<RfpBqItem> bqList = null;
		Integer start = null;
		Integer length = null;
		try {
			if (StringUtils.checkString(rftBqItemPojo.getId()).length() > 0) {
				start = 0;
				length = rftBqItemPojo.getPageLength();
				LOG.info("Updating order..........................");
				RfpBqItem bqItem = rfpBqService.getBqItembyBqItemId(rftBqItemPojo.getId());
				if (bqItem.getOrder() > 0 && StringUtils.checkString(rftBqItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					List<RfpBqItem> returnList = rfpBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), null, null, null, start, length, rftBqItemPojo.getPageNo());
					return new ResponseEntity<List<RfpBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
					// return new
					// ResponseEntity<List<RfpBqItem>>(rfpBqService.findRfpBqbyBqId(rfpBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId()),
					// headers, HttpStatus.BAD_REQUEST);
				}
				if (bqItem.getOrder() == 0 && StringUtils.checkString(rftBqItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					List<RfpBqItem> returnList = rfpBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), null, null, null, start, length, rftBqItemPojo.getPageNo());
					return new ResponseEntity<List<RfpBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
					// return new
					// ResponseEntity<List<RfpBqItem>>(rfpBqService.findRfpBqbyBqId(rfpBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId()),
					// headers, HttpStatus.BAD_REQUEST);
				}
				RfpBqItem item = new RfpBqItem();
				item.setId(rftBqItemPojo.getId());
				item.setRfxEvent(rfpBqService.getRfpEventById(rftBqItemPojo.getRftEvent()));
				item.setItemName(rftBqItemPojo.getItemName());
				constructBqItemValues(rftBqItemPojo, item);
				if (bqItem.getParent() != null && !bqItem.getParent().getId().equals(rftBqItemPojo.getParent()) && rfpBqService.isBqItemExists(item, rftBqItemPojo.getBq(), rftBqItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new NotAllowedException("Duplicate BQ Item. BQ Item by that name already exists.");
				}
				rfpBqService.reorderBqItems(rftBqItemPojo);
				bqList = rfpBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), null, null, null, start, length, rftBqItemPojo.getPageNo());
				// bqList =
				// rfpBqService.findRfpBqbyBqId(rfpBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpBqItem>>(bqList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException e) {
			LOG.error("Error while moving BQ parent with item as Child : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.drag", new Object[] { e.getMessage() }, Global.LOCALE));
			List<RfpBqItem> returnList = rfpBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), null, null, null, start, length, rftBqItemPojo.getPageNo());
			return new ResponseEntity<List<RfpBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
			// bqList =
			// rfpBqService.findRfpBqbyBqId(rfpBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId());
			// return new ResponseEntity<List<RfpBqItem>>(bqList, headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("Error while moving BQ Item : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfpBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RfpBqItem>>(bqList, headers, HttpStatus.OK);
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
		RfpEvent rftEvent = rfpBqService.getRfpEventById(eventId);
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
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
	public @ResponseBody ResponseEntity<RfpBqItemResponsePojo> updateBqItem(@RequestBody BqItemPojo bqItemPojo) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(bqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(bqItemPojo.getParent()).length() == 0) {
				HttpHeaders header = new HttpHeaders();
				header.add("error", messageSource.getMessage("event.bqsection.required", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfpBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
			}

			if (bqItemPojo.getQuantity() != null && bqItemPojo.getUnitPrice() != null) {
				HttpHeaders header = new HttpHeaders();
				java.math.BigDecimal unitPrice = bqItemPojo.getUnitPrice();
				java.math.BigDecimal qnty = bqItemPojo.getQuantity();
				if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
					LOG.info("VALUE IS " + qnty.multiply(unitPrice));
					header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[] {}, null));
					return new ResponseEntity<RfpBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			LOG.info(" :: UPDATE BILL OF QUNATITY ::" + bqItemPojo.toString());
			RfpBqItem rfpBq = rfpBqService.getBqItemById(bqItemPojo.getId());
			rfpBq.setUom(StringUtils.checkString(bqItemPojo.getUom()).length() > 0 ? uomService.getUombyCode(bqItemPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()) : null);
			super.buildUpdateBqItem(bqItemPojo, rfpBq);
			RfpBqItem item = new RfpBqItem();
			item.setId(bqItemPojo.getId());
			item.setRfxEvent(rfpBqService.getRfpEventById(bqItemPojo.getRftEvent()));
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
					return new ResponseEntity<RfpBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}
			// constructBqItemValues(bqItemPojo, item);
			if (rfpBqService.isBqItemExists(item, bqItemPojo.getBq(), bqItemPojo.getParent())) {
				LOG.info("Duplicate....");
				throw new ApplicationException("Duplicate BQ Item. BQ Item by that name already exists.");
			}
			rfpBqService.updateBqItem(rfpBq);
			// List<RfpBqItem> bqList = rfpBqService.findRfpBqbyBqId(bqItemPojo.getBq());
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
			List<RfpBqItem> bqItemList = rfpBqService.getBqItemForSearchFilter(bqItemPojo.getBq(), itemLevel, itemOrder, bqItemPojo.getSearchVal(), bqItemPojo.getStart(), bqItemPojo.getPageLength(), bqItemPojo.getPageNo());
			List<BqItemPojo> leveLOrderList = rfpBqService.getAllLevelOrderBqItemByBqId(bqItemPojo.getBq(), null);

			// sending level order list and bq Item list bind in new Pojo class
			RfpBqItemResponsePojo bqItemResponsePojo = new RfpBqItemResponsePojo();

			bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
			bqItemResponsePojo.setBqItemList(bqItemList);

			// LOG.info("EVENT ALL LIST :: " + bqList.toString());
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", messageSource.getMessage("buyer.rftbq.save", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RfpBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error while updating BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while adding Notes to Buyer " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfpBqItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param rftBqPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/updateBqList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpEventBq>> updateBqList(@RequestBody BqPojo rftBqPojo) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("UPDATE RFP BQ :: " + rftBqPojo.toString());
			RfpEventBq rftBq = rfpBqService.getBqById(rftBqPojo.getId());
			rftBq.setName(rftBqPojo.getBqName());
			rftBq.setDescription(rftBqPojo.getBqDesc());
			rftBq.setModifiedDate(new Date());
			if (bqValidate(rftBqPojo)) {
				rfpBqService.updateBq(rftBq);
				List<RfpEventBq> bqList = rfpBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
				headers.add("success", messageSource.getMessage("rft.bq.update", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpEventBq>>(bqList, headers, HttpStatus.OK);
			} else {
				List<RfpEventBq> bqList = rfpBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
				headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[] { rftBqPojo.getBqName() }, Global.LOCALE));
				return new ResponseEntity<List<RfpEventBq>>(bqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("rft.bq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfpEventBq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param rftBqPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/deleteRftBq", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpEventBq>> deleteRftBq(@RequestBody BqPojo rftBqPojo) throws JsonProcessingException {
		RfpEventBq bqEvent = rfpBqService.getBqById(rftBqPojo.getId());
		try {
			try {
				rfpBqService.isAllowtoDeleteBQ(rftBqPojo.getId(), RfxTypes.RFP);
			} catch (Exception e) {
				LOG.error("Error while deleting BQ  :" + e.getMessage(), e);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", e.getMessage());
				return new ResponseEntity<List<RfpEventBq>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			}
			// if (bqEvent.getRfxEvent().getStatus() != EventStatus.DRAFT) {
			// HttpHeaders headers = new HttpHeaders();
			// headers.add("info", messageSource.getMessage("rft.bq.info.draft", new Object[] { bqEvent.getName() },
			// Global.LOCALE));
			// return new ResponseEntity<List<RfpEventBq>>(null, headers, HttpStatus.OK);
			// } else {
			rfpBqService.deleteBq(bqEvent.getId());
			LOG.info(" After delete BQ display remaining eventID :: " + rftBqPojo.getEventId());
			List<RfpEventBq> bqList = rfpBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
			Integer count = 1;
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (RfpEventBq bq : bqList) {
					bq.setBqOrder(count);
					rfpBqService.updateBq(bq);
					count++;
				}
			}
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", messageSource.getMessage("rft.bq.success.delete", new Object[] { bqEvent.getName() }, Global.LOCALE));
			return new ResponseEntity<List<RfpEventBq>>(bqList, headers, HttpStatus.OK);
			// }

		} catch (Exception e) {
			LOG.error("Error while deleting BQ  [ " + bqEvent.getName() + " ]" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.error.delete", new Object[] { bqEvent.getName() }, Global.LOCALE));
			return new ResponseEntity<List<RfpEventBq>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}

	}

	/**
	 * @param rftBqItemPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/addNewColumns", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpBqItem>> addNewColumns(@RequestBody BqPojo rfpBq) throws JsonProcessingException {
		LOG.info("Enter this Block :: " + rfpBq.toString());
		HttpHeaders headers = new HttpHeaders();
		List<RfpBqItem> bqItemList = null;
		if (rfpBq.getField1Label() == null && rfpBq.getField2Label() == null && rfpBq.getField3Label() == null && rfpBq.getField4Label() == null) {
			headers.add("error", "Field cannot be empty");
			return new ResponseEntity<List<RfpBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		} else {
			RfpEventBq rftEventBq = rfpBqService.getBqById(rfpBq.getId());
			if (rftEventBq != null) {
				try {

					List<String> fieldLabelList = new ArrayList<String>();
					if (rfpBq.getField1Label() != null && fieldLabelList.contains(rfpBq.getField1Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfpBq.getField1Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfpBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfpBq.getField1Label() != null) {
						fieldLabelList.add(rfpBq.getField1Label().toLowerCase());
					}
					if (rfpBq.getField2Label() != null && fieldLabelList.contains(rfpBq.getField2Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfpBq.getField2Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfpBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfpBq.getField2Label() != null) {
						fieldLabelList.add(rfpBq.getField2Label().toLowerCase());
					}
					if (rfpBq.getField3Label() != null && fieldLabelList.contains(rfpBq.getField3Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfpBq.getField3Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfpBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfpBq.getField3Label() != null) {
						fieldLabelList.add(rfpBq.getField3Label().toLowerCase());
					}
					if (rfpBq.getField4Label() != null && fieldLabelList.contains(rfpBq.getField4Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfpBq.getField4Label() }, Global.LOCALE));
						return new ResponseEntity<List<RfpBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfpBq.getField4Label() != null) {
						fieldLabelList.add(rfpBq.getField4Label().toLowerCase());
					}

					super.buildAddNewColumns(rfpBq, rftEventBq);
					rfpBqService.updateRfpBqFields(rftEventBq);
					Integer itemLevel = null;
					Integer itemOrder = null;
					Integer start = null;
					Integer length = null;
					if (StringUtils.checkString(rfpBq.getFilterVal()).length() == 1) {
						rfpBq.setFilterVal("");
					}
					if (StringUtils.checkString(rfpBq.getFilterVal()).length() > 0) {
						itemLevel = 0;
						itemOrder = 0;
						String[] values = rfpBq.getFilterVal().split("\\.");
						itemLevel = Integer.parseInt(values[0]);
						itemOrder = Integer.parseInt(values[1]);
					}
					start = 0;
					length = rfpBq.getPageLength();

					// bqList = rfpBqService.findRfpBqbyBqId(rftEventBq.getId());
					bqItemList = rfpBqService.getBqItemForSearchFilter(rftEventBq.getId(), itemLevel, itemOrder, rfpBq.getSearchVal(), start, length, rfpBq.getPageNo());
					for (RfpBqItem bqItem : bqItemList) {
						if (bqItem.getUom() != null) {
							bqItem.getUom().setCreatedBy(null);
							bqItem.getUom().setModifiedBy(null);
						}
					}

					headers.add("success", messageSource.getMessage("rft.bq.newfields.success", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfpBqItem>>(bqItemList, headers, HttpStatus.OK);
				} catch (Exception e) {
					headers.add("error", messageSource.getMessage("buyer.rftbq.newfields.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RfpBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
				}
			}
		}
		return new ResponseEntity<List<RfpBqItem>>(bqItemList, headers, HttpStatus.OK);

	}

	/**
	 * @param bqId
	 * @param bqItemId
	 * @return
	 */
	@RequestMapping(path = "/getBqForEdit", method = RequestMethod.GET)
	public ResponseEntity<RfpBqItem> getBqForEdit(@RequestParam String bqId, @RequestParam String bqItemId) {
		RfpBqItem rftBqItem = null;
		LOG.info("BQ EDIT :: bqId" + bqId + " :: bqItemId :: " + bqItemId);
		try {
			rftBqItem = rfpBqService.getBqItembyBqIdAndBqItemId(bqItemId);
			rftBqItem = rftBqItem.createShallowCopy();
		} catch (Exception e) {
			LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfpBqItem>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (rftBqItem != null) {
			return new ResponseEntity<RfpBqItem>(rftBqItem, HttpStatus.OK);
		} else {
			LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RfpBqItem>(null, headers, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @param items
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/deleteBqItems/{bqId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RfpBqItemResponsePojo> deleteBqItems(@RequestParam(name = "items", required = true) String items, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo, @PathVariable("bqId") String bqId, @RequestParam(name = "allDelete", required = false) boolean allDelete) throws JsonProcessingException {
		LOG.info("DELETE BQ ITEMS IDs :: " + items);
		List<RfpBqItem> bqItems = null;
		HttpHeaders headers = new HttpHeaders();
		// sending level order list and bq Item list bind in new Pojo class
		RfpBqItemResponsePojo bqItemResponsePojo = new RfpBqItemResponsePojo();
		if (StringUtils.checkString(filterVal).length() > 0) {
			if (filterVal.contains(".")) {
				filterVal = "";
			}
		}

		if (StringUtils.checkString(items).length() > 0) {
			try {
				String[] bqItemsArr = items.split(",");
				LOG.info(" filterVal  :" + filterVal + " searchVal: " + searchVal);

				if (allDelete) {
					rfpBqService.deleteAllBqItems(bqId);
				} else {
					rfpBqService.deleteBqItems(bqItemsArr, bqId);
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

				bqItems = rfpBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
				List<BqItemPojo> leveLOrderList = rfpBqService.getAllLevelOrderBqItemByBqId(bqId, searchVal);

				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItems);

			} catch (NotAllowedException e) {
				LOG.error("Not Allowed Exception: " + e.getMessage(), e);
				headers.add("error", e.getMessage());
				return new ResponseEntity<RfpBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (Exception e) {
				LOG.error("Error while delete bq items: " + e.getMessage(), e);
				headers.add("error", "Error during delete : " + messageSource.getMessage("rft.bqitems.error.delete", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RfpBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<RfpBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/bqSaveDraft", method = RequestMethod.POST)
	public String bqSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId, Model model) {
		RfpEvent rftEvent = rfpBqService.getRfpEventById(eventId);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
		return "redirect:createBQList/" + eventId;

	}

	/**
	 * @param bqId
	 * @param label
	 * @return
	 */
	@RequestMapping(path = "/deleteBqNewField", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RfpBqItem>> deleteBqNewField(@RequestParam("bqId") String bqId, @RequestParam("label") String label, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
		LOG.info("deleteBqNewField label ::" + label + " bqId :: " + bqId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		HttpHeaders headers = new HttpHeaders();
		List<RfpBqItem> bqItemList = null;
		try {
			if (StringUtils.checkString(label).length() > 0) {
				rfpBqService.deletefieldInBqItems(bqId, label);
				// bqList = rfpBqService.findRfpBqbyBqId(bqId);
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
				bqItemList = rfpBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
				for (RfpBqItem bqItem : bqItemList) {
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
			return new ResponseEntity<List<RfpBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<RfpBqItem>>(bqItemList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getBqForNewFields", method = RequestMethod.GET)
	public ResponseEntity<RfpEventBq> getBqForNewFields(@RequestParam String bqId) {
		RfpEventBq eventBq = null;
		LOG.info("BQ EDIT :: bqId" + bqId);
		try {
			eventBq = rfpBqService.getBqById(bqId);
		} catch (Exception e) {
			LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RfpEventBq>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (eventBq != null) {
			return new ResponseEntity<RfpEventBq>(eventBq, HttpStatus.OK);
		} else {
			LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RfpEventBq>(null, headers, HttpStatus.NOT_FOUND);
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

	@RequestMapping(value = "/uploadBqItems", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) throws IOException {
		LOG.info("UPLOADING STARTED ...... BQ Id :: " + bqId + "Event Id :: " + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		List<RfpBqItem> bqItemList = null;
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
					totalBqItemCount = super.bqItemsUpload(file, bqId, eventId, RfxTypes.RFP);
					if (totalBqItemCount == 0) {
						LOG.info("totalBqItemCount " + totalBqItemCount);
						throw new Exception("Please fill data in excel file");
					}
				} catch (Exception e) {
					// e.printStackTrace();
					// bqList = rfpBqService.findRfpBqbyBqId(bqId);
					bqItemList = rfpBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
					for (RfpBqItem bqItem : bqItemList) {
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
			// bqList = rfpBqService.findRfpBqbyBqId(bqId);
			bqItemList = rfpBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
			for (RfpBqItem bqItem : bqItemList) {
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
		// bqList = rfpBqService.findRfpBqbyBqId(bqId);
		bqItemList = rfpBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
		for (RfpBqItem bqItem : bqItemList) {
			if (bqItem.getUom() != null) {
				bqItem.getUom().setCreatedBy(null);
				bqItem.getUom().setModifiedBy(null);
			}
		}
		RfpEventBq bq = rfpBqService.getRfpEventBqByBqId(bqId);
		BqPojo rfpBqColumns = new BqPojo(bq);
		response.put("rftBqColumns", rfpBqColumns);

		headers.add("success", messageSource.getMessage("common.upload.success", new Object[] { "RFP BQ Items" }, Global.LOCALE));
		response.put("list", bqItemList);
		response.put("totalBqItemCount", totalBqItemCount);
		return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getEventBqbyId", method = RequestMethod.POST)
	public ResponseEntity<RfpEventBq> getEventBqbyId(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId) {
		HttpHeaders headers = new HttpHeaders();
		RfpEventBq bq = rfpBqService.getBqById(bqId);
		return new ResponseEntity<RfpEventBq>(bq, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getBqItemForSearchFilter", method = RequestMethod.POST)
	public ResponseEntity<RfpBqItemResponsePojo> getBqItemForSearchFilter(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
		LOG.info(" getEventBqForChoosenFilterValue  : " + bqId + " eventId :" + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		HttpHeaders headers = new HttpHeaders();
		List<RfpBqItem> bqItemList = null;
		// sending total Bq Item count and Bq Item list bind in new Pojo class
		RfpBqItemResponsePojo bqItemResponsePojo = new RfpBqItemResponsePojo();
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
			bqItemList = rfpBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
			bqItemResponsePojo.setBqItemList(bqItemList);
			long totalBqItemCount = rfpBqService.totalBqItemCountByBqId(bqId, searchVal);
			LOG.info("totalBqItemCount :" + totalBqItemCount);
			bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
			List<BqItemPojo> leveLOrderList = rfpBqService.getAllLevelOrderBqItemByBqId(bqId, searchVal);
			bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
			for (RfpBqItem bqItem : bqItemList) {
				if (bqItem.getUom() != null) {
					bqItem.getUom().setCreatedBy(null);
					bqItem.getUom().setModifiedBy(null);
				}
			}
			if (pageLength != SecurityLibrary.getLoggedInUser().getBqPageLength()) {
				userService.updateUserBqPageLength(pageLength, SecurityLibrary.getLoggedInUser().getId());
				super.updateSecurityLibraryUser(pageLength);
			}
			return new ResponseEntity<RfpBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during Search Reset Bill Of Quantity :" + e.getMessage(), e);
			headers.add("error", "Error during Search Bill Of Quantity ");
			return new ResponseEntity<RfpBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/updateEventBqOrder/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfpEventBq>> updateEventBqOrder(@RequestBody String[] bqIds, @PathVariable("eventId") String eventId) {
		try {
			LOG.info("RFP eventId : " + eventId);
			if (bqIds != null && bqIds.length > 0) {
				Integer count = 1;
				for (String cqId : bqIds) {
					RfpEventBq bq = rfpBqService.getBqById(cqId);
					bq.setBqOrder(count);
					rfpBqService.updateBq(bq);
					count++;
				}
				List<RfpEventBq> bqList = rfpBqService.getAllBqListByEventIdByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.bq.order", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpEventBq>>(bqList, headers, HttpStatus.OK);
			} else {
				List<RfpEventBq> bqList = rfpBqService.getAllBqListByEventIdByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.bq.order.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RfpEventBq>>(bqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while Ordering BQ : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RfpEventBq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

}
