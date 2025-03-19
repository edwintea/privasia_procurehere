package com.privasia.procurehere.web.controller;

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
import com.privasia.procurehere.core.dao.RftBqItemDao;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.RftBqItemResponsePojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;

@Controller
@RequestMapping("/buyer/RFT")
public class RftBqController extends EventBqBase {

	public RftBqController() {
		super(RfxTypes.RFT);
	}

	@Autowired
	RftBqItemDao rftBqItemDao;

	/**
	 * @param model
	 * @param eventId
	 * @param redir
	 * @return
	 */
	@RequestMapping(value = "/bqPrevious", method = RequestMethod.POST)
	public String bqPrevious(Model model, @RequestParam(name = "eventId", required = true) String eventId) {
		RftEvent rftEvent = rftBqService.getRftEventById(eventId);
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
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
				model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
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
		RftEvent rftEvent = rftBqService.getRftEventById(eventId);
		if (rftEvent != null) {
			rftEvent.setBqCompleted(Boolean.TRUE);
			rftEventService.updateRftEvent(rftEvent);
			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			return super.bqNext(rftEvent);
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
		RftEvent event = rftBqService.getRftEventById(eventId);
		List<RftEventBq> bqEventList = rftBqService.getAllBqListByEventIdByOrder(eventId);
		model.addAttribute("event", event);
		model.addAttribute("bqEventList", bqEventList);
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		return "createBQList";
	}

	/**
	 * @param rftBqPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/createBQList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Bq>> saveBQList(@RequestBody BqPojo rftBqPojo) throws JsonProcessingException {
		LOG.info("Crate EVENT BQ ID :: " + rftBqPojo.getId());
		try {
			if (bqValidate(rftBqPojo)) {
				List<RftEventBq> bqList = rftBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
				Integer count = 1;
				if (CollectionUtil.isNotEmpty(bqList)) {
					for (RftEventBq bq : bqList) {
						if (bq.getBqOrder() == null) {
							bq.setBqOrder(count);
							rftBqService.updateRftBq(bq);
							count++;
						}
					}
					count = bqList.size();
					count++;
				}
				super.saveBq(rftBqPojo, count);
				List<Bq> bqListNew = rftBqService.findRftBqbyEventId(rftBqPojo.getEventId());
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.bq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<Bq>>(bqListNew, headers, HttpStatus.OK);
			} else {
				List<Bq> bqList = rftBqService.findRftBqbyEventId(rftBqPojo.getEventId());
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
	public String createRftEventBQ(@PathVariable String eventId, @PathVariable String bqId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0 || StringUtils.checkString(bqId).length() == 0) {
			return "redirect:/400_error";
		}
		Integer start = 0;
		Integer length = 50;
		Integer pageNo = 1;
		LOG.info("BqId : " + bqId);
		RftEvent rftEvent = rftBqService.getRftEventById(eventId);
		RftEventBq eventBq = rftBqService.getRftBqById(bqId);
		List<RftBqItem> returnList = new ArrayList<>();
		length = SecurityLibrary.getLoggedInUser().getBqPageLength();
		LOG.info(" BQ Page length : " + length);
		List<RftBqItem> bqList = rftBqService.getBqItemForSearchFilter(bqId, null, null, null, start, length, pageNo);
		buildBqItemForSearch(returnList, bqList);
		// rftBqService.getBqItemForSearchFilter(bqId, null, null, null, start, length);
		// LOG.info(" bqList :"+bqList.size());

		// List<RftBqItem> bqList = rftBqService.findRftBqbyBqId(bqId);
		model.addAttribute("rftBqItem", new RftBqItem());
		model.addAttribute("bqList", returnList);
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		buildModel(eventId, bqId, model, rftEvent, eventBq);
		List<BqItemPojo> leveLOrderList = rftBqService.getAllLevelOrderBqItemByBqId(bqId, null);
		model.addAttribute("leveLOrderList", leveLOrderList);
		long totalBqItemCount = rftBqService.totalBqItemCountByBqId(bqId, null);
		model.addAttribute("totalBqItemCount", totalBqItemCount);
		model.addAttribute("event", rftEvent);
		LOG.info("totalBqItemCount :" + totalBqItemCount);
		return "createEventBQ";
	}

	private void buildBqItemForSearch(List<RftBqItem> returnList, List<RftBqItem> bqList) {
		RftBqItem parent = null;
		for (RftBqItem item : bqList) {
			if (item.getOrder() != 0) {
				if (parent != null) {
					parent.getChildren().add(item.createSearchShallowCopy());
				}
			} else {
				parent = item.createSearchShallowCopy();
				parent.setChildren(new ArrayList<RftBqItem>());
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
	public @ResponseBody ResponseEntity<RftBqItemResponsePojo> saveRftEventBQ(@RequestBody BqItemPojo rftBqItemPojo, Model model) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(rftBqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(rftBqItemPojo.getParent()).length() == 0) {
				HttpHeaders header = new HttpHeaders();
				header.add("error", messageSource.getMessage("event.bqsection.required", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RftBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
			}

			if (rftBqItemPojo.getQuantity() != null && rftBqItemPojo.getUnitPrice() != null) {
				HttpHeaders header = new HttpHeaders();
				java.math.BigDecimal unitPrice = rftBqItemPojo.getUnitPrice();
				java.math.BigDecimal qnty = rftBqItemPojo.getQuantity();
				if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
					LOG.info("VALUE IS " + qnty.multiply(unitPrice));
					header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[] {}, null));
					return new ResponseEntity<RftBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			LOG.info("Crate EVENT BQ ID :: " + rftBqItemPojo.getBq());
			LOG.info(" Filter :" + rftBqItemPojo.getFilterVal() + " Search :" + rftBqItemPojo.getSearchVal());
			RftBqItem rftBqItem = new RftBqItem();
			rftBqItem.setId(rftBqItemPojo.getId());
			rftBqItem.setRfxEvent(rftBqService.getRftEventById(rftBqItemPojo.getRftEvent()));
			rftBqItem.setItemName(rftBqItemPojo.getItemName());
			rftBqItem.setBq(rftBqService.getBqItembyId(rftBqItemPojo.getBq()));
			RftEvent rfaEvent = rftEventService.getRftEventByeventId(rftBqItemPojo.getRftEvent());
			model.addAttribute("event", rfaEvent);

			/*************** To check whether quantity of Bqitem entered should not be 0 ****************/

			if (rftBqItemPojo.getQuantity() != null) {
				LOG.info("rftBqItem.setBq***************" + rftBqItemPojo.getQuantity());
				HttpHeaders header = new HttpHeaders();

				BigDecimal i = rftBqItemPojo.getQuantity();
				BigDecimal c = BigDecimal.ZERO;
				int res = i.compareTo(c);
				if (res == 0) {
					header.add("error", messageSource.getMessage("buyer.rftbq.quantity", new Object[] { rftBqItemPojo.getQuantity() }, Global.LOCALE));
					return new ResponseEntity<RftBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			constructBqItemValues(rftBqItemPojo, rftBqItem);
			if (StringUtils.checkString(rftBqItemPojo.getParent()).length() > 0) {
				rftBqItem.setParent(rftBqService.getBqItemsbyBqId(StringUtils.checkString(rftBqItemPojo.getParent())));
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
			RftBqItemResponsePojo bqItemResponsePojo = new RftBqItemResponsePojo();

			if (!rftBqService.isBqItemExists(rftBqItem, rftBqItemPojo.getBq(), rftBqItemPojo.getParent())) {
				rftBqService.saveRftBqItem(rftBqItem);
				rftBqItemPojo.setStart(0);
				LOG.info(" Strat :" + rftBqItemPojo.getStart() + " lenth :" + rftBqItemPojo.getPageLength() + " Page Number :" + rftBqItemPojo.getPageNo());
				List<RftBqItem> bqItemList = rftBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), itemLevel, itemOrder, rftBqItemPojo.getSearchVal(), rftBqItemPojo.getStart(), rftBqItemPojo.getPageLength(), rftBqItemPojo.getPageNo());
				List<BqItemPojo> leveLOrderList = rftBqService.getAllLevelOrderBqItemByBqId(rftBqItemPojo.getBq(), rftBqItemPojo.getSearchVal());
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItemList);
				// List<RftBqItem> bqList = rftBqService.findRftBqbyBqId(rftBqItemPojo.getBq());
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("buyer.rftbq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RftBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
			} else {
				List<RftBqItem> bqItemList = rftBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), itemLevel, itemOrder, rftBqItemPojo.getSearchVal(), rftBqItemPojo.getStart(), rftBqItemPojo.getPageLength(), rftBqItemPojo.getPageNo());
				List<BqItemPojo> leveLOrderList = rftBqService.getAllLevelOrderBqItemByBqId(rftBqItemPojo.getBq(), rftBqItemPojo.getSearchVal());
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItemList);
				// List<RftBqItem> bqList = rftBqService.findRftBqbyBqId(rftBqItemPojo.getBq());
				LOG.info("Validate of BQ LIST :: " + rftBqItemPojo.toString());
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("buyer.rftbq.duplicate", new Object[] { rftBqItemPojo.getItemName() }, Global.LOCALE));
				return new ResponseEntity<RftBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RftBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @param bqItemOrder
	 * @return
	 */
	@RequestMapping(path = "/eventBQOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RftBqItem>> eventBQOrder(@RequestBody BqItemPojo rftBqItemPojo) {
		LOG.info("rftBqItemPojo.getBq() : " + rftBqItemPojo.getBq() + " Parent : " + rftBqItemPojo.getParent() + " Item Id : " + rftBqItemPojo.getId() + " New Position : " + rftBqItemPojo.getOrder());
		HttpHeaders headers = new HttpHeaders();
		List<RftBqItem> bqList = null;
		Integer start = null;
		Integer length = null;
		try {
			if (StringUtils.checkString(rftBqItemPojo.getId()).length() > 0) {
				start = 0;
				length = rftBqItemPojo.getPageLength();
				LOG.info("Updating order.........................." + rftBqItemPojo.getBq());
				RftBqItem bqItem = rftBqService.getBqItembyBqItemId(rftBqItemPojo.getId());
				if (bqItem.getOrder() > 0 && StringUtils.checkString(rftBqItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					List<RftBqItem> returnList = rftBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), null, null, null, start, length, rftBqItemPojo.getPageNo());
					return new ResponseEntity<List<RftBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
					// return new
					// ResponseEntity<List<RftBqItem>>(rftBqService.findRftBqbyBqId(rftBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId()),
					// headers, HttpStatus.BAD_REQUEST);

				}
				if (bqItem.getOrder() == 0 && StringUtils.checkString(rftBqItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					List<RftBqItem> returnList = rftBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), null, null, null, start, length, rftBqItemPojo.getPageNo());
					return new ResponseEntity<List<RftBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);

					// return new
					// ResponseEntity<List<RftBqItem>>(rftBqService.findRftBqbyBqId(rftBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId()),
					// headers, HttpStatus.BAD_REQUEST);
				}
				RftBqItem item = new RftBqItem();
				item.setId(rftBqItemPojo.getId());
				item.setRfxEvent(rftBqService.getRftEventById(rftBqItemPojo.getRftEvent()));
				item.setItemName(rftBqItemPojo.getItemName());
				if (bqItem.getParent() != null && !bqItem.getParent().getId().equals(rftBqItemPojo.getParent()) && rftBqService.isBqItemExists(item, rftBqItemPojo.getBq(), rftBqItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new NotAllowedException("Duplicate BQ Item. BQ Item by that name already exists.");
				}
				constructBqItemValues(rftBqItemPojo, item);
				rftBqService.reorderBqItems(rftBqItemPojo);
				// bqList =
				// rftBqService.findRftBqbyBqId(rftBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId());
				bqList = rftBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), null, null, null, start, length, rftBqItemPojo.getPageNo());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RftBqItem>>(bqList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException e) {
			LOG.error("Error while moving BQ parent with item as Child : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.drag", new Object[] { e.getMessage() }, Global.LOCALE));
			List<RftBqItem> returnList = rftBqService.getBqItemForSearchFilter(rftBqItemPojo.getBq(), null, null, null, start, length, rftBqItemPojo.getPageNo());
			return new ResponseEntity<List<RftBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);

			// return new
			// ResponseEntity<List<RftBqItem>>(rftBqService.findRftBqbyBqId(rftBqService.getBqItembyBqItemId(rftBqItemPojo.getId()).getBq().getId()),
			// headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("Error while moving BQ Item : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RftBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<RftBqItem>>(bqList, headers, HttpStatus.OK);
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
		RftEvent rftEvent = rftBqService.getRftEventById(eventId);
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
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
	public @ResponseBody ResponseEntity<RftBqItemResponsePojo> updateBqItem(@RequestBody BqItemPojo bqItemPojo) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(bqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(bqItemPojo.getParent()).length() == 0) {
				HttpHeaders header = new HttpHeaders();
				header.add("error", messageSource.getMessage("event.bqsection.required", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RftBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
			}

			if (bqItemPojo.getQuantity() != null && bqItemPojo.getUnitPrice() != null) {
				HttpHeaders header = new HttpHeaders();
				java.math.BigDecimal unitPrice = bqItemPojo.getUnitPrice();
				java.math.BigDecimal qnty = bqItemPojo.getQuantity();
				if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
					LOG.info("VALUE IS " + qnty.multiply(unitPrice));
					header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[] {}, null));
					return new ResponseEntity<RftBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}

			LOG.info(" :: Filter Value ::" + bqItemPojo.getFilterVal() + " : " + bqItemPojo.getSearchVal());
			LOG.info(" :: UPDATE BILL OF QUNATITY ::" + bqItemPojo.toString());
			RftBqItem rftBq = rftBqService.getRftBqItemById(bqItemPojo.getId());
			rftBq.setUom(StringUtils.checkString(bqItemPojo.getUom()).length() > 0 ? uomService.getUombyCode(bqItemPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()) : null);
			super.buildUpdateBqItem(bqItemPojo, rftBq);
			RftBqItem item = new RftBqItem();
			item.setId(bqItemPojo.getId());
			item.setRfxEvent(rftBqService.getRftEventById(bqItemPojo.getRftEvent()));
			item.setItemName(bqItemPojo.getItemName());

			/*************** To check whether quantity of Bqitem entered should not be 0 ****************/

			if (bqItemPojo.getQuantity() != null) {
				LOG.info("rftBqItem.setBq***************" + bqItemPojo.getQuantity());
				HttpHeaders header = new HttpHeaders();

				BigDecimal i = bqItemPojo.getQuantity();
				BigDecimal c = BigDecimal.ZERO;
				int res = i.compareTo(c);
				if (res == 0) {
					header.add("error", messageSource.getMessage("buyer.rftbq.quantity", new Object[] { bqItemPojo.getQuantity() }, Global.LOCALE));
					return new ResponseEntity<RftBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
				}
			}
			// constructBqItemValues(bqItemPojo, item);

			if (rftBqService.isBqItemExists(item, bqItemPojo.getBq(), bqItemPojo.getParent())) {
				LOG.info("Duplicate....");
				throw new ApplicationException("Duplicate BQ Item. BQ Item by that name already exists.");
			}
			rftBqService.updateBqItem(rftBq);

			// sending level order list and bq Item list bind in new Pojo class
			RftBqItemResponsePojo bqItemResponsePojo = new RftBqItemResponsePojo();
			List<BqItemPojo> leveLOrderList = rftBqService.getAllLevelOrderBqItemByBqId(bqItemPojo.getBq(), null);
			bqItemResponsePojo.setLeveLOrderList(leveLOrderList);

			// List<RftBqItem> bqList = rftBqService.findRftBqbyBqId(bqItemPojo.getBq());

			// bqItemPojo.getSearchVal(), bqItemPojo.getFilterVal());
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
			List<RftBqItem> bqItemList = rftBqService.getBqItemForSearchFilter(bqItemPojo.getBq(), itemLevel, itemOrder, bqItemPojo.getSearchVal(), bqItemPojo.getStart(), bqItemPojo.getPageLength(), bqItemPojo.getPageNo());
			bqItemResponsePojo.setBqItemList(bqItemList);
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", messageSource.getMessage("buyer.rftbq.update", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RftBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while updating BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RftBqItemResponsePojo>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param rftBqPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/updateBqList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RftEventBq>> updateBqList(@RequestBody BqPojo rftBqPojo) throws JsonProcessingException {
		try {
			RftEventBq rftBq = rftBqService.getRftBqById(rftBqPojo.getId());
			rftBq.setName(rftBqPojo.getBqName());
			rftBq.setDescription(rftBqPojo.getBqDesc());
			rftBq.setModifiedDate(new Date());
			if (bqValidate(rftBqPojo)) {
				rftBqService.updateRftBq(rftBq);
				List<RftEventBq> bqList = rftBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.bq.update", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RftEventBq>>(bqList, headers, HttpStatus.OK);
			} else {
				List<RftEventBq> bqList = rftBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[] { rftBqPojo.getBqName() }, Global.LOCALE));
				return new ResponseEntity<List<RftEventBq>>(bqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RftEventBq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param rftBqPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "/deleteRftBq", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RftEventBq>> deleteRftBq(@RequestBody BqPojo rftBqPojo) throws JsonProcessingException {
		RftEventBq bqEvent = rftBqService.getRftBqById(rftBqPojo.getId());
		try {
			try {

				rftBqService.isAllowtoDeleteBQ(rftBqPojo.getId(), RfxTypes.RFT);
			} catch (Exception e) {
				LOG.error("Error while deleting BQ  :" + e.getMessage(), e);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", e.getMessage());
				return new ResponseEntity<List<RftEventBq>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			}
			// if (bqEvent.getRfxEvent().getStatus() != EventStatus.DRAFT) {
			// HttpHeaders headers = new HttpHeaders();
			// headers.add("info", messageSource.getMessage("rft.bq.info.draft", new Object[] { bqEvent.getName() },
			// Global.LOCALE));
			// return new ResponseEntity<List<RftEventBq>>(null, headers, HttpStatus.OK);
			// } else {
			rftBqService.deleteRftBq(bqEvent.getId());
			LOG.info(" After delete BQ display remaining eventID :: " + rftBqPojo.getEventId());
			List<RftEventBq> bqList = rftBqService.getAllBqListByEventIdByOrder(rftBqPojo.getEventId());
			Integer count = 1;
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (RftEventBq bq : bqList) {
					bq.setBqOrder(count);
					rftBqService.updateRftBq(bq);
					count++;
				}
			}
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", messageSource.getMessage("rft.bq.success.delete", new Object[] { bqEvent.getName() }, Global.LOCALE));
			return new ResponseEntity<List<RftEventBq>>(bqList, headers, HttpStatus.OK);
			// }

		} catch (Exception e) {
			LOG.error("Error while deleting BQ  [ " + bqEvent.getName() + " ]" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.error.delete", new Object[] { bqEvent.getName() }, Global.LOCALE));
			return new ResponseEntity<List<RftEventBq>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}

	}

	/**
	 * @param rftBqItemPojo
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/addNewColumns", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RftBqItem>> addNewColumns(@RequestBody BqPojo rftBq) throws JsonProcessingException {
		LOG.info("Enter this Block :: " + rftBq.toLogString());
		HttpHeaders headers = new HttpHeaders();
		List<RftBqItem> bqItemList = null;
		if (rftBq.getField1Label() == null && rftBq.getField2Label() == null && rftBq.getField3Label() == null && rftBq.getField4Label() == null && rftBq.getField5Label() == null && rftBq.getField6Label() == null && rftBq.getField7Label() == null && rftBq.getField8Label() == null && rftBq.getField9Label() == null && rftBq.getField10Label() == null) {
			headers.add("error", "Field cannot be empty");
			return new ResponseEntity<List<RftBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		} else {
			RftEventBq rftEventBq = rftBqService.getRftBqById(rftBq.getId());
			if (rftEventBq != null) {
				try {
					List<String> fieldLabelList = new ArrayList<String>();
					if (rftBq.getField1Label() != null && fieldLabelList.contains(rftBq.getField1Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rftBq.getField1Label() }, Global.LOCALE));
						return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rftBq.getField1Label() != null) {
						fieldLabelList.add(rftBq.getField1Label().toLowerCase());
					}
					if (rftBq.getField2Label() != null && fieldLabelList.contains(rftBq.getField2Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rftBq.getField2Label() }, Global.LOCALE));
						return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rftBq.getField2Label() != null) {
						fieldLabelList.add(rftBq.getField2Label().toLowerCase());
					}
					if (rftBq.getField3Label() != null && fieldLabelList.contains(rftBq.getField3Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rftBq.getField3Label() }, Global.LOCALE));
						return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rftBq.getField3Label() != null) {
						fieldLabelList.add(rftBq.getField3Label().toLowerCase());
					}
					if (rftBq.getField4Label() != null && fieldLabelList.contains(rftBq.getField4Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rftBq.getField4Label() }, Global.LOCALE));
						return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rftBq.getField4Label() != null) {
						fieldLabelList.add(rftBq.getField4Label().toLowerCase());
					}

					if (rftBq.getField5Label() != null && fieldLabelList.contains(rftBq.getField5Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rftBq.getField5Label() }, Global.LOCALE));
						return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rftBq.getField5Label() != null) {
						fieldLabelList.add(rftBq.getField5Label().toLowerCase());
					}
					if (rftBq.getField6Label() != null && fieldLabelList.contains(rftBq.getField6Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rftBq.getField6Label() }, Global.LOCALE));
						return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rftBq.getField6Label() != null) {
						fieldLabelList.add(rftBq.getField6Label().toLowerCase());
					}
					if (rftBq.getField7Label() != null && fieldLabelList.contains(rftBq.getField7Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rftBq.getField7Label() }, Global.LOCALE));
						return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rftBq.getField7Label() != null) {
						fieldLabelList.add(rftBq.getField7Label().toLowerCase());
					}

					if (rftBq.getField8Label() != null && fieldLabelList.contains(rftBq.getField8Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rftBq.getField8Label() }, Global.LOCALE));
						return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rftBq.getField8Label() != null) {
						fieldLabelList.add(rftBq.getField8Label().toLowerCase());
					}
					if (rftBq.getField9Label() != null && fieldLabelList.contains(rftBq.getField9Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rftBq.getField9Label() }, Global.LOCALE));
						return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rftBq.getField9Label() != null) {
						fieldLabelList.add(rftBq.getField9Label().toLowerCase());
					}
					if (rftBq.getField10Label() != null && fieldLabelList.contains(rftBq.getField10Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rftBq.getField10Label() }, Global.LOCALE));
						return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rftBq.getField10Label() != null) {
						fieldLabelList.add(rftBq.getField10Label().toLowerCase());
					}

					super.buildAddNewColumns(rftBq, rftEventBq);
					rftBqService.updateRftBqFields(rftEventBq);

					Integer itemLevel = null;
					Integer itemOrder = null;
					Integer start = null;
					Integer length = null;
					if (StringUtils.checkString(rftBq.getFilterVal()).length() == 1) {
						rftBq.setFilterVal("");
					}
					if (StringUtils.checkString(rftBq.getFilterVal()).length() > 0) {
						itemLevel = 0;
						itemOrder = 0;
						String[] values = rftBq.getFilterVal().split("\\.");
						itemLevel = Integer.parseInt(values[0]);
						itemOrder = Integer.parseInt(values[1]);
					}
					start = 0;
					length = rftBq.getPageLength();

					// bqList = rftBqService.findRftBqbyBqId(rftEventBq.getId());
					bqItemList = rftBqService.getBqItemForSearchFilter(rftEventBq.getId(), itemLevel, itemOrder, rftBq.getSearchVal(), start, length, rftBq.getPageNo());
					for (RftBqItem rftBqItem : bqItemList) {
						if (rftBqItem.getUom() != null) {
							rftBqItem.getUom().setCreatedBy(null);
							rftBqItem.getUom().setModifiedBy(null);
						}
					}
					headers.add("success", messageSource.getMessage("rft.bq.newfields.success", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.OK);

				} catch (Exception e) {
					headers.add("error", messageSource.getMessage("buyer.rftbq.newfields.error", new Object[] {}, "Error while save New Field", Global.LOCALE));
					return new ResponseEntity<List<RftBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
				}
			}
		}
		return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.OK);

	}

	/**
	 * @param bqId
	 * @param bqItemId
	 * @return
	 */
	@RequestMapping(path = "/getBqForEdit", method = RequestMethod.GET)
	public ResponseEntity<RftBqItem> getBqForEdit(@RequestParam String bqId, @RequestParam String bqItemId) {
		LOG.info("  getBqForEdit  : " + bqId + " bqItemId  :" + bqItemId);
		RftBqItem rftBqItem = null;
		LOG.info("BQ EDIT :: bqId" + bqId + " :: bqItemId :: " + bqItemId);
		try {
			rftBqItem = rftBqService.getBqItembyBqIdAndBqItemId(bqItemId);
			rftBqItem = rftBqItem.createShallowCopy();
		} catch (Exception e) {
			LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RftBqItem>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (rftBqItem != null) {
			return new ResponseEntity<RftBqItem>(rftBqItem, HttpStatus.OK);
		} else {
			LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RftBqItem>(null, headers, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @param items
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(path = "/deleteBqItems/{bqId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RftBqItemResponsePojo> deleteBqItems(@RequestParam(name = "items", required = true) String items, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo, @PathVariable("bqId") String bqId, @RequestParam(name = "allDelete", required = false) boolean allDelete) throws JsonProcessingException {
		LOG.info("DELETE BQ ITEMS IDs :: " + items);
		List<RftBqItem> bqItems = null;
		HttpHeaders headers = new HttpHeaders();
		if (StringUtils.checkString(filterVal).length() > 0) {
			if (filterVal.contains(".")) {
				filterVal = "";
			}
		}
		// sending level order list and bq Item list bind in new Pojo class
		RftBqItemResponsePojo bqItemResponsePojo = new RftBqItemResponsePojo();

		if (StringUtils.checkString(items).length() > 0) {
			try {
				String[] bqItemsArr = items.split(",");
				LOG.info(" filterVal  :" + filterVal + " searchVal: " + searchVal);

				if (allDelete) {
					rftBqService.deleteAllBqItems(bqId);
				} else {
					rftBqService.deleteBqItems(bqItemsArr, bqId);
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

				bqItems = rftBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
				List<BqItemPojo> leveLOrderList = rftBqService.getAllLevelOrderBqItemByBqId(bqId, searchVal);
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				bqItemResponsePojo.setBqItemList(bqItems);
				// bqItems = rftBqService.findRftBqbyBqId(bqId);
			} catch (NotAllowedException e) {
				LOG.error("Not Allowed Exception: " + e.getMessage(), e);
				headers.add("error", e.getMessage());
				return new ResponseEntity<RftBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (Exception e) {
				LOG.error("Error while delete bq items: " + e.getMessage(), e);
				headers.add("error", "Error during delete : " + messageSource.getMessage("rft.bqitems.error.delete", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<RftBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<RftBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/bqSaveDraft", method = RequestMethod.POST)
	public String bqSaveDraft(RedirectAttributes redir, @RequestParam("eventId") String eventId) {
		RftEvent rftEvent = rftBqService.getRftEventById(eventId);
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId()) }, Global.LOCALE));
		return "redirect:createBQList/" + eventId;

	}

	/**
	 * @param bqId
	 * @param label
	 * @return
	 */
	@RequestMapping(path = "/deleteBqNewField", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RftBqItem>> deleteBqNewField(@RequestParam("bqId") String bqId, @RequestParam("label") String label, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
		LOG.info("deleteBqNewField label ::" + label + " bqId :: " + bqId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		HttpHeaders headers = new HttpHeaders();
		List<RftBqItem> bqItemList = null;
		try {
			if (StringUtils.checkString(label).length() > 0) {
				rftBqService.deletefieldInBqItems(bqId, label);
				// bqList = rftBqService.findRftBqbyBqId(bqId);
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
				bqItemList = rftBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
				for (RftBqItem rftBqItem : bqItemList) {
					if (rftBqItem.getUom() != null) {
						rftBqItem.getUom().setCreatedBy(null);
						rftBqItem.getUom().setModifiedBy(null);
					}
				}
				headers.add("success", "Column deleted Successfully");
			}
		} catch (Exception e) {
			LOG.error("Error while deleting BQ New Field " + e.getMessage(), e);
			headers.add("error", "Error while deleting BQ New Field : " + e.getMessage());
			return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<RftBqItem>>(bqItemList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getBqForNewFields", method = RequestMethod.GET)
	public ResponseEntity<RftEventBq> getBqForNewFields(@RequestParam String bqId) {
		RftEventBq eventBq = null;
		LOG.info("BQ EDIT :: bqId" + bqId);
		try {
			eventBq = rftBqService.getRftBqById(bqId);

		} catch (Exception e) {
			LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<RftEventBq>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (eventBq != null) {
			return new ResponseEntity<RftEventBq>(eventBq, HttpStatus.OK);
		} else {
			LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<RftEventBq>(null, headers, HttpStatus.NOT_FOUND);
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

			super.eventDownloader(workbook, bqId, RfxTypes.RFT);

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
		List<RftBqItem> bqItemList = null;
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
					totalBqItemCount = super.bqItemsUpload(file, bqId, eventId, RfxTypes.RFT);
					if (totalBqItemCount == 0) {
						LOG.info("totalBqItemCount " + totalBqItemCount);
						throw new Exception("Please fill data in excel file");
					}

				} catch (Exception e) {
					// e.printStackTrace();
					// bqList = rftBqService.findRftBqbyBqId(bqId);
					bqItemList = rftBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
					for (RftBqItem rftBqItem : bqItemList) {
						if (rftBqItem.getUom() != null) {
							rftBqItem.getUom().setCreatedBy(null);
							rftBqItem.getUom().setModifiedBy(null);
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
			// bqList = rftBqService.findRftBqbyBqId(bqId);
			bqItemList = rftBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
			for (RftBqItem rftBqItem : bqItemList) {
				if (rftBqItem.getUom() != null) {
					rftBqItem.getUom().setCreatedBy(null);
					rftBqItem.getUom().setModifiedBy(null);
				}
			}
			headers.add("error", messageSource.getMessage("common.upload.notsupported.fail", new Object[] { e.getMessage() }, Global.LOCALE));
			response.put("list", bqItemList);
			response.put("totalBqItemCount", totalBqItemCount);
			return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.BAD_REQUEST);
		}
		// return "redirect:createEventBQ/" + eventId + "/" + bqId;
		// bqList = rftBqService.findRftBqbyBqId(bqId);
		bqItemList = rftBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
		for (RftBqItem rftBqItem : bqItemList) {
			if (rftBqItem.getUom() != null) {
				rftBqItem.getUom().setCreatedBy(null);
				rftBqItem.getUom().setModifiedBy(null);
			}
		}
		RftEventBq bq = rftBqService.getRftEventBqByBqId(bqId);
		BqPojo rftBqColumns = new BqPojo(bq, eventId);
		response.put("rftBqColumns", rftBqColumns);

		headers.add("success", messageSource.getMessage("common.upload.success", new Object[] { "RFT BQ Items" }, Global.LOCALE));
		response.put("list", bqItemList);
		response.put("totalBqItemCount", totalBqItemCount);
		return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getEventBqbyId", method = RequestMethod.POST)
	public ResponseEntity<RftEventBq> getEventBqbyId(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId) {
		HttpHeaders headers = new HttpHeaders();
		RftEventBq bq = rftBqService.getRftBqById(bqId);
		return new ResponseEntity<RftEventBq>(bq, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getBqItemForSearchFilter", method = RequestMethod.POST)
	public ResponseEntity<RftBqItemResponsePojo> getBqItemForSearchFilter(@RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
		LOG.info(" getBqItemForSearchFilter  : " + bqId + " eventId :" + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		List<RftBqItem> bqItemList = null;
		HttpHeaders headers = new HttpHeaders();
		// sending total Bq Item count and Bq Item list bind in new Pojo class
		RftBqItemResponsePojo bqItemResponsePojo = new RftBqItemResponsePojo();
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
			bqItemList = rftBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
			bqItemResponsePojo.setBqItemList(bqItemList);
			long totalBqItemCount = rftBqService.totalBqItemCountByBqId(bqId, searchVal);
			LOG.info("totalBqItemCount :" + totalBqItemCount);
			bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);

			List<BqItemPojo> leveLOrderList = rftBqService.getAllLevelOrderBqItemByBqId(bqId, searchVal);
			bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
			for (RftBqItem rftBqItem : bqItemList) {
				if (rftBqItem.getUom() != null) {
					rftBqItem.getUom().setCreatedBy(null);
					rftBqItem.getUom().setModifiedBy(null);
				}
			}

			if (pageLength != SecurityLibrary.getLoggedInUser().getBqPageLength()) {
				userService.updateUserBqPageLength(pageLength, SecurityLibrary.getLoggedInUser().getId());
				super.updateSecurityLibraryUser(pageLength);
			}
			return new ResponseEntity<RftBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during Search Reset Bill Of Quantity :" + e.getMessage(), e);
			headers.add("error", "Error during Search reset Bill Of Quantity ");
			return new ResponseEntity<RftBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/updateEventBqOrder/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RftEventBq>> updateEventBqOrder(@RequestBody String[] bqIds, @PathVariable("eventId") String eventId) {
		try {
			LOG.info("RFT eventId : " + eventId);
			if (bqIds != null && bqIds.length > 0) {
				Integer count = 1;
				for (String cqId : bqIds) {
					RftEventBq bq = rftBqService.getRftBqById(cqId);
					bq.setBqOrder(count);
					rftBqService.updateRftBq(bq);
					count++;
				}
				List<RftEventBq> bqList = rftBqService.getAllBqListByEventIdByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.bq.order", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RftEventBq>>(bqList, headers, HttpStatus.OK);
			} else {
				List<RftEventBq> bqList = rftBqService.getAllBqListByEventIdByOrder(eventId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.bq.order.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<RftEventBq>>(bqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while Ordering BQ : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<RftEventBq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

}