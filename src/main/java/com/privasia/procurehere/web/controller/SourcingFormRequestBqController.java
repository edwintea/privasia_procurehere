package com.privasia.procurehere.web.controller;

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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.SourcingFormRequestBqDao;
import com.privasia.procurehere.core.entity.RequestAudit;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.pojo.RfsBqPojo;
import com.privasia.procurehere.core.pojo.SourcingBqItemPojo;
import com.privasia.procurehere.core.pojo.SourcingBqItemResponsePojo;
import com.privasia.procurehere.core.pojo.SourcingReqBqPojo;
import com.privasia.procurehere.core.pojo.UomPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SourcingFormRequestBqService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.SourcingFormRequestBqEditor;
import com.privasia.procurehere.web.editors.UserEditor;

/**
 * @author pooja
 */
@Controller
@RequestMapping("/buyer")
public class SourcingFormRequestBqController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	UserEditor userEditor;

	@Autowired
	SourcingFormRequestBqEditor sourcingFormRequestBqEditor;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	SourcingFormRequestBqService sourcingFormRequestBqService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	UomService uomService;

	@Autowired
	UserService userService;

	@Autowired
	ServletContext context;

	@Autowired
	SourcingFormRequestBqDao sourcingFormRequestBqDao;

	@ModelAttribute("step")
	public String getStep() {
		return "4";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(SourcingFormRequestBq.class, sourcingFormRequestBqEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@RequestMapping(value = "/sourcingBqPrevious", method = RequestMethod.POST)
	public String sourcingBqPrevious(@RequestParam(name = "formId", required = true) String formId) {
		SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormById(formId);
		if (sourcingFormRequest != null) {
			return "redirect:sourcingFormRequestCqList/" + formId;
		} else {
			LOG.error("Sourcing Request not found redirecting to login");
			return "/";
		}
	}

	@RequestMapping(value = "/sourcingBqListPrevious", method = RequestMethod.POST)
	public String sourcingBqListPrevious(@RequestParam(name = "formId", required = true) String formId) throws Exception {
		try {
			if (formId != null) {
				return "redirect:createSourcingRequestBqList/" + formId;
			} else {
				LOG.error("Sourcing Request not found redirecting to login");
				return "/";
			}
		} catch (Exception e) {
			LOG.error("Sourcing Request not found redirecting to login" + e.getMessage(), e);
			throw new Exception("Sourcing Request not found redirecting to login" + e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "/sourcingBqNext", method = RequestMethod.POST)
	public String bqNext(Model model, @RequestParam(name = "formId", required = true) String formId) {
		SourcingFormRequest sourcingFormRequest = sourcingFormRequestBqService.getSourcingRequestBqByFormId(formId);
		List<RequestAudit> reqAudit = sourcingFormRequestService.getReqAudit(formId);
		LOG.info("formId " + formId);

		if (sourcingFormRequest != null) {
			sourcingFormRequest.setBqCompleted(Boolean.TRUE);
			sourcingFormRequestService.updateSourcingFormRequest(sourcingFormRequest);
			model.addAttribute("sourcingFormRequest", sourcingFormRequest);
			return "redirect:/buyer/createSourcingRequestSorList/" + sourcingFormRequest.getId();
		} else {
			LOG.error("Sourcing Request not found redirecting to login ");
			return "/";
		}
	}

	@RequestMapping(path = "/createSourcingRequestBqList/{formId}", method = RequestMethod.GET)
	public String createSourcingRequestBqList(@PathVariable("formId") String formId, Model model) {
		if (StringUtils.checkString(formId).length() == 0) {
			return "redirect:/400_error";
		}
		try {
			SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormById(formId);
			sourcingFormRequest.setCqCompleted(true);
			sourcingFormRequest = sourcingFormRequestService.update(sourcingFormRequest);
			// List<SourcingFormRequestBq> bqSourcingList = sourcingFormRequestBqService.findBqByFormId(formId);
			List<SourcingFormRequestBq> bqSourcingList = sourcingFormRequestBqService.findBqByFormIdByOrder(formId);
			model.addAttribute("eventPermissions", sourcingFormRequestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), formId));
			model.addAttribute("sourcingFormRequest", sourcingFormRequest);
			model.addAttribute("bqSourcingList", bqSourcingList);
			
			RequestAudit audit = new RequestAudit();
			model.addAttribute("audit", audit);
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			// model.addAttribute("error", "Error while fetching sourcing Bq List : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("error.while.fetching.sourcing.bqlist", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "sourcingFormRequestBqList";
	}

	@RequestMapping(path = "/createSourcingRequestBqList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SourcingFormRequestBq>> saveSourcingBqList(@RequestBody SourcingReqBqPojo sourcingReqBqPojo) {

		try {
			if (!bqValidate(sourcingReqBqPojo.getFormId(), sourcingReqBqPojo.getId(), sourcingReqBqPojo.getBqName())) {
				sourcingFormRequestBqService.saveSourcingBq(sourcingReqBqPojo);
				List<SourcingFormRequestBq> bqList = sourcingFormRequestBqService.findBqByFormIdByOrder(sourcingReqBqPojo.getFormId());
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.bq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingFormRequestBq>>(bqList, headers, HttpStatus.OK);
			} else {
				List<SourcingFormRequestBq> bqList = sourcingFormRequestBqService.findBqByFormIdByOrder(sourcingReqBqPojo.getFormId());
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[] { sourcingReqBqPojo.getBqName() }, Global.LOCALE));
				return new ResponseEntity<List<SourcingFormRequestBq>>(bqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while adding sourcing BQ to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SourcingFormRequestBq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/showSourcingBqItems", method = RequestMethod.POST)
	public String showSourcingBqItems(Model model, @RequestParam(name = "bqId", required = true) String bqId, @RequestParam(name = "formId", required = true) String formId, RedirectAttributes redir) {
		SourcingFormRequest sourcingFormRequest = sourcingFormRequestBqService.getSourcingRequestBqByFormId(formId);
		if (sourcingFormRequest != null) {
			return "redirect:createSourcingBqItem/" + sourcingFormRequest.getId() + "/" + bqId;
		} else {
			LOG.error("BQ not found redirecting to login ");
			return "/";
		}
	}

	@RequestMapping(path = "/createSourcingBqItem/{formId}/{bqId}", method = RequestMethod.GET)
	public String createSourcingBqItem(Model model, @PathVariable(name = "bqId", required = true) String bqId, @PathVariable(name = "formId", required = true) String formId, RedirectAttributes redir) {
		LOG.info("Bq ID............................" + bqId);
		LOG.info("Form Id.........................." + formId);

		if (StringUtils.checkString(formId).length() == 0 || StringUtils.checkString(bqId).length() == 0) {
			return "redirect:/400_error";
		}
		Integer start = 0;
		Integer length = 50;
		Integer pageNo = 1;
		SourcingFormRequest sourcingFormRequest = sourcingFormRequestBqService.getSourcingRequestBqByFormId(formId);

		SourcingFormRequestBq bq = sourcingFormRequestBqService.findBqById(bqId);
		List<SourcingFormRequestBqItem> returnList = new ArrayList<>();
		length = SecurityLibrary.getLoggedInUser().getBqPageLength();
		List<SourcingFormRequestBqItem> bqList = sourcingFormRequestBqService.getBqItemForSearchFilter(bqId, null, null, null, start, length, pageNo);
		buildBqItemForSearch(returnList, bqList);
		model.addAttribute("sourcingFormRequestBqItem", new SourcingFormRequestBqItem());
		model.addAttribute("bqList", returnList);
		model.addAttribute("eventPermissions", sourcingFormRequestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), formId));
		buildModel(formId, bqId, model, sourcingFormRequest, bq);
		List<SourcingBqItemPojo> leveLOrderList = sourcingFormRequestBqService.getAllLevelOrderBqItemByBqId(bqId, null);
		model.addAttribute("leveLOrderList", leveLOrderList);
		long totalBqItemCount = sourcingFormRequestBqService.getTotalBqItemCountByBqId(bqId, null);
		model.addAttribute("totalBqItemCount", totalBqItemCount);
		LOG.info("totalBqItemCount :" + totalBqItemCount);

		return "sourcingFormCreateBQ";
	}

	private void buildBqItemForSearch(List<SourcingFormRequestBqItem> returnList, List<SourcingFormRequestBqItem> bqList) {
		SourcingFormRequestBqItem parent = null;
		for (SourcingFormRequestBqItem item : bqList) {
			if (item.getOrder() != 0) {
				if (parent != null) {
					parent.getChildren().add(item.createSearchShallowCopy());
				}
			} else {
				parent = item.createSearchShallowCopy();
				parent.setChildren(new ArrayList<SourcingFormRequestBqItem>());
				returnList.add(parent);
			}
		}
	}

	protected void buildModel(String formId, String bqId, Model model, SourcingFormRequest form, SourcingFormRequestBq sourcingFormRequestBq) {
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

	@RequestMapping(path = "/getSourcingBqbyId", method = RequestMethod.POST)
	public ResponseEntity<SourcingFormRequestBq> getEventBqbyId(@RequestParam("bqId") String bqId) {
		HttpHeaders headers = new HttpHeaders();
		SourcingFormRequestBq bq = sourcingFormRequestBqService.findBqById(bqId);
		return new ResponseEntity<SourcingFormRequestBq>(bq, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateSourcingBqList", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SourcingFormRequestBq>> updateBqList(@RequestBody SourcingReqBqPojo sourcingReqBqPojo) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("UPDATE SOURCING BQ :: " + sourcingReqBqPojo.toString());
			SourcingFormRequestBq bq = sourcingFormRequestBqService.findBqById(sourcingReqBqPojo.getId());
			bq.setName(sourcingReqBqPojo.getBqName());
			bq.setDescription(sourcingReqBqPojo.getBqDesc());
			bq.setModifiedDate(new Date());
			if (!bqValidate(sourcingReqBqPojo.getFormId(), sourcingReqBqPojo.getId(), sourcingReqBqPojo.getBqName())) {
				sourcingFormRequestBqService.updateSourcingBq(bq);
				List<SourcingFormRequestBq> bqList = sourcingFormRequestBqService.findBqByFormIdByOrder(sourcingReqBqPojo.getFormId());
				headers.add("success", messageSource.getMessage("rft.bq.update", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingFormRequestBq>>(bqList, headers, HttpStatus.OK);
			} else {
				List<SourcingFormRequestBq> bqList = sourcingFormRequestBqService.findBqByFormIdByOrder(sourcingReqBqPojo.getFormId());
				headers.add("error", messageSource.getMessage("rft.bq.dupicate", new Object[] { sourcingReqBqPojo.getBqName() }, Global.LOCALE));
				return new ResponseEntity<List<SourcingFormRequestBq>>(bqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("rft.bq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SourcingFormRequestBq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	private boolean bqValidate(String formId, String bqId, String name) {
		return sourcingFormRequestBqService.isBqExists(formId, bqId, name);
	}

	@RequestMapping(path = "/createSourcingBqItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<SourcingBqItemResponsePojo> saveSourcingFormBqItem(@RequestBody SourcingBqItemPojo sourcingBqItemPojo, Model model) throws JsonProcessingException {
		try {
			LOG.info("Crate Sourcing BQ ID :: " + sourcingBqItemPojo.getBq());
			LOG.info("Unit Price " + sourcingBqItemPojo.getUnitPrice());
			if (StringUtils.checkString(sourcingBqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(sourcingBqItemPojo.getParent()).length() == 0) {
				HttpHeaders header = new HttpHeaders();
				header.add("error", messageSource.getMessage("event.bqsection.required", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<SourcingBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
			}

			SourcingFormRequestBqItem sourcingBqItem = new SourcingFormRequestBqItem();
			sourcingBqItem.setId(sourcingBqItemPojo.getId());
			sourcingBqItem.setSourcingFormRequest(sourcingFormRequestBqService.getSourcingRequestBqByFormId(sourcingBqItemPojo.getFormId()));
			sourcingBqItem.setItemName(sourcingBqItemPojo.getItemName());
			sourcingBqItem.setBq(sourcingFormRequestBqService.getBqById(sourcingBqItemPojo.getBq()));
			constructBqItemValues(sourcingBqItemPojo, sourcingBqItem);
			if (StringUtils.checkString(sourcingBqItemPojo.getParent()).length() > 0) {
				sourcingBqItem.setParent(sourcingFormRequestBqService.getBqItemsbyBqId(StringUtils.checkString(sourcingBqItemPojo.getParent())));
			}
			/*************** To check whether quantity of Bqitem entered should not be 0 ****************/

			if (sourcingBqItemPojo.getQuantity() != null) {
				HttpHeaders header = new HttpHeaders();

				BigDecimal i = sourcingBqItemPojo.getQuantity();
				BigDecimal c = BigDecimal.ZERO;
				int res = i.compareTo(c);
				if (res == 0) {
					header.add("error", messageSource.getMessage("buyer.rftbq.quantity", new Object[] { sourcingBqItemPojo.getQuantity() }, Global.LOCALE));
					return new ResponseEntity<SourcingBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
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
			SourcingBqItemResponsePojo bqItemResponsePojo = new SourcingBqItemResponsePojo();

			if (!sourcingFormRequestBqService.isBqItemExists(sourcingBqItem, sourcingBqItemPojo.getBq(), sourcingBqItemPojo.getParent())) {
				sourcingFormRequestBqService.saveSourcingBqItem(sourcingBqItem);
				sourcingBqItemPojo.setStart(0);
				List<SourcingFormRequestBqItem> bqItemList = sourcingFormRequestBqService.getBqItemForSearchFilter(sourcingBqItemPojo.getBq(), itemLevel, itemOrder, sourcingBqItemPojo.getSearchVal(), sourcingBqItemPojo.getStart(), sourcingBqItemPojo.getPageLength(), sourcingBqItemPojo.getPageNo());
				List<SourcingBqItemPojo> leveLOrderList = sourcingFormRequestBqService.getAllLevelOrderBqItemByBqId(sourcingBqItemPojo.getBq(), sourcingBqItemPojo.getSearchVal());
				bqItemResponsePojo.setBqItemList(bqItemList);
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				HttpHeaders header = new HttpHeaders();
				header.add("success", messageSource.getMessage("buyer.rftbq.save", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<SourcingBqItemResponsePojo>(bqItemResponsePojo, header, HttpStatus.OK);
			} else {
				List<SourcingFormRequestBqItem> bqItemList = sourcingFormRequestBqService.getBqItemForSearchFilter(sourcingBqItemPojo.getBq(), itemLevel, itemOrder, sourcingBqItemPojo.getSearchVal(), sourcingBqItemPojo.getStart(), sourcingBqItemPojo.getPageLength(), sourcingBqItemPojo.getPageNo());
				List<SourcingBqItemPojo> leveLOrderList = sourcingFormRequestBqService.getAllLevelOrderBqItemByBqId(sourcingBqItemPojo.getBq(), sourcingBqItemPojo.getSearchVal());
				bqItemResponsePojo.setBqItemList(bqItemList);
				bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
				HttpHeaders header = new HttpHeaders();
				header.add("error", messageSource.getMessage("buyer.rftbq.duplicate", new Object[] { sourcingBqItemPojo.getItemName() }, Global.LOCALE));
				return new ResponseEntity<SourcingBqItemResponsePojo>(bqItemResponsePojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception e) {
			LOG.error("Error while adding BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<SourcingBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	protected void constructBqItemValues(SourcingBqItemPojo bqPojo, SourcingFormRequestBqItem sourcingBqItem) {
		sourcingBqItem.setItemDescription(bqPojo.getItemDescription());
		sourcingBqItem.setItemName(bqPojo.getItemName());
		sourcingBqItem.setQuantity(bqPojo.getQuantity());
		sourcingBqItem.setUnitPrice(bqPojo.getUnitPrice());
		if (StringUtils.checkString(bqPojo.getUom()).length() > 0) {
			sourcingBqItem.setUom(uomService.getUombyCode(bqPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()));
		}
		sourcingBqItem.setPriceType(bqPojo.getPriceType());
		sourcingBqItem.setUnitBudgetPrice(bqPojo.getUnitBudgetPrice());
		sourcingBqItem.setField1(bqPojo.getField1());
		sourcingBqItem.setField2(bqPojo.getField2());
		sourcingBqItem.setField3(bqPojo.getField3());
		sourcingBqItem.setField4(bqPojo.getField4());
		sourcingBqItem.setField5(bqPojo.getField5());
		sourcingBqItem.setField6(bqPojo.getField6());
		sourcingBqItem.setField7(bqPojo.getField7());
		sourcingBqItem.setField8(bqPojo.getField8());
		sourcingBqItem.setField9(bqPojo.getField9());
		sourcingBqItem.setField10(bqPojo.getField10());
	}

	@RequestMapping(path = "updateSourcingBqItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<SourcingBqItemResponsePojo> updateSourcingBqItem(@RequestBody SourcingBqItemPojo sourcingBqItemPojo) throws JsonProcessingException {
		try {

			if (StringUtils.checkString(sourcingBqItemPojo.getItemName()).length() == 0 && StringUtils.checkString(sourcingBqItemPojo.getParent()).length() == 0) {
				HttpHeaders header = new HttpHeaders();
				header.add("error", messageSource.getMessage("event.bqsection.required", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<SourcingBqItemResponsePojo>(null, header, HttpStatus.BAD_REQUEST);
			}

			SourcingFormRequestBqItem sourcingBqItem = sourcingFormRequestBqService.getBqItemsbyBqId(sourcingBqItemPojo.getId());
			sourcingBqItem.setUom(StringUtils.checkString(sourcingBqItemPojo.getUom()).length() > 0 ? uomService.getUombyCode(sourcingBqItemPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()) : null);
			buildUpdateBqItem(sourcingBqItemPojo, sourcingBqItem);
			SourcingFormRequestBqItem sourcingReqBqItem = new SourcingFormRequestBqItem();
			sourcingReqBqItem.setId(sourcingBqItemPojo.getId());
			sourcingReqBqItem.setSourcingFormRequest(sourcingFormRequestBqService.getSourcingRequestBqByFormId(sourcingBqItemPojo.getFormId()));
			if (sourcingFormRequestBqService.isBqItemExists(sourcingReqBqItem, sourcingBqItemPojo.getBq(), sourcingBqItemPojo.getParent())) {
				throw new ApplicationException("Duplicate BQ Item. BQ Item by that name already exists.");
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

			sourcingFormRequestBqService.updateSourcingBqItem(sourcingBqItem);
			// sending level order list and bq Item list bind in new Pojo class
			SourcingBqItemResponsePojo bqItemResponsePojo = new SourcingBqItemResponsePojo();
			List<SourcingBqItemPojo> levelOrderList = sourcingFormRequestBqService.getAllLevelOrderBqItemByBqId(sourcingBqItemPojo.getBq(), sourcingBqItemPojo.getSearchVal());
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
			List<SourcingFormRequestBqItem> bqItemList = sourcingFormRequestBqService.getBqItemForSearchFilter(sourcingBqItemPojo.getBq(), itemLevel, itemOrder, sourcingBqItemPojo.getSearchVal(), sourcingBqItemPojo.getStart(), sourcingBqItemPojo.getPageLength(), sourcingBqItemPojo.getPageNo());
			bqItemResponsePojo.setBqItemList(bqItemList);
			HttpHeaders header = new HttpHeaders();
			header.add("success", messageSource.getMessage("buyer.rftbq.update", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<SourcingBqItemResponsePojo>(bqItemResponsePojo, header, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while updating BQ Items to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<SourcingBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	protected void buildUpdateBqItem(SourcingBqItemPojo bqPojo, SourcingFormRequestBqItem bqItem) {
		bqItem.setItemName(bqPojo.getItemName());
		bqItem.setUom(bqItem.getUom());
		bqItem.setUnitPrice(bqPojo.getUnitPrice());
		bqItem.setQuantity(bqPojo.getQuantity());
		bqItem.setItemDescription(bqPojo.getItemDescription());
		bqItem.setPriceType(bqPojo.getPriceType());
		bqItem.setUnitBudgetPrice(bqPojo.getUnitBudgetPrice());
		bqItem.setField1(bqPojo.getField1());
		bqItem.setField2(bqPojo.getField2());
		bqItem.setField3(bqPojo.getField3());
		bqItem.setField4(bqPojo.getField4());
		bqItem.setField5(bqPojo.getField5());
		bqItem.setField6(bqPojo.getField6());
		bqItem.setField7(bqPojo.getField7());
		bqItem.setField8(bqPojo.getField8());
		bqItem.setField9(bqPojo.getField9());
		bqItem.setField10(bqPojo.getField10());
	}

	@RequestMapping(path = "/getSourcingBqForNewFields", method = RequestMethod.GET)
	public ResponseEntity<SourcingFormRequestBq> getBqForNewFields(@RequestParam String bqId) {
		SourcingFormRequestBq requestBq = null;
		LOG.info("BQ EDIT :: bqId" + bqId);
		try {
			requestBq = sourcingFormRequestBqService.getBqById(bqId);

		} catch (Exception e) {
			LOG.error("Error while getting Sourcing BQ Item for Edit : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<SourcingFormRequestBq>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (requestBq != null) {
			return new ResponseEntity<SourcingFormRequestBq>(requestBq, HttpStatus.OK);
		} else {
			LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<SourcingFormRequestBq>(null, headers, HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(path = "/sourcingBQOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SourcingFormRequestBqItem>> sourcingBQOrder(@RequestBody SourcingBqItemPojo sourcingBqItemPojo) {
		LOG.info("sourcingBqItemPojo.getBq() : " + sourcingBqItemPojo.getBq() + " Parent : " + sourcingBqItemPojo.getParent() + " Item Id : " + sourcingBqItemPojo.getId() + " New Position : " + sourcingBqItemPojo.getOrder());
		HttpHeaders headers = new HttpHeaders();
		List<SourcingFormRequestBqItem> bqList = null;
		Integer start = null;
		Integer length = null;
		try {
			if (StringUtils.checkString(sourcingBqItemPojo.getId()).length() > 0) {
				start = 0;
				length = sourcingBqItemPojo.getPageLength();
				LOG.info("Updating order.........................." + sourcingBqItemPojo.getBq());
				SourcingFormRequestBqItem bqItem = sourcingFormRequestBqService.getBqItembyBqItemId(sourcingBqItemPojo.getId());
				if (bqItem.getOrder() > 0 && StringUtils.checkString(sourcingBqItemPojo.getParent()).length() == 0) {
					LOG.info("child cannot be a parent");
					headers.add("error", messageSource.getMessage("rfx.child.reOrder.error", new Object[] {}, Global.LOCALE));
					List<SourcingFormRequestBqItem> returnList = sourcingFormRequestBqService.getBqItemForSearchFilter(sourcingBqItemPojo.getBq(), null, null, null, start, length, sourcingBqItemPojo.getPageNo());
					return new ResponseEntity<List<SourcingFormRequestBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
				}
				if (bqItem.getOrder() == 0 && StringUtils.checkString(sourcingBqItemPojo.getParent()).length() > 0) {
					LOG.info("parent cannot be a child");
					headers.add("error", messageSource.getMessage("rfx.parent.reOrder.error", new Object[] {}, Global.LOCALE));
					List<SourcingFormRequestBqItem> returnList = sourcingFormRequestBqService.getBqItemForSearchFilter(sourcingBqItemPojo.getBq(), null, null, null, start, length, sourcingBqItemPojo.getPageNo());
					return new ResponseEntity<List<SourcingFormRequestBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);

				}
				SourcingFormRequestBqItem item = new SourcingFormRequestBqItem();
				item.setId(sourcingBqItemPojo.getId());
				item.setSourcingFormRequest(sourcingFormRequestBqService.getSourcingRequestBqByFormId(sourcingBqItemPojo.getFormId()));
				item.setItemName(sourcingBqItemPojo.getItemName());
				if (bqItem.getParent() != null && !bqItem.getParent().getId().equals(sourcingBqItemPojo.getParent()) && sourcingFormRequestBqService.isBqItemExists(item, sourcingBqItemPojo.getBq(), sourcingBqItemPojo.getParent())) {
					LOG.info("Duplicate....");
					throw new NotAllowedException("Duplicate BQ Item. BQ Item by that name already exists.");
				}
				constructBqItemValues(sourcingBqItemPojo, item);
				sourcingFormRequestBqService.reorderBqItems(sourcingBqItemPojo);
				bqList = sourcingFormRequestBqService.getBqItemForSearchFilter(sourcingBqItemPojo.getBq(), null, null, null, start, length, sourcingBqItemPojo.getPageNo());
				headers.add("success", messageSource.getMessage("buyer.rftbq.success", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingFormRequestBqItem>>(bqList, headers, HttpStatus.OK);
			}
		} catch (NotAllowedException e) {
			LOG.error("Error while moving BQ parent with item as Child : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.drag", new Object[] { e.getMessage() }, Global.LOCALE));
			List<SourcingFormRequestBqItem> returnList = sourcingFormRequestBqService.getBqItemForSearchFilter(sourcingBqItemPojo.getBq(), null, null, null, start, length, sourcingBqItemPojo.getPageNo());
			return new ResponseEntity<List<SourcingFormRequestBqItem>>(returnList, headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("Error while moving BQ Item : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SourcingFormRequestBqItem>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<List<SourcingFormRequestBqItem>>(bqList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteSourcingBq", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SourcingFormRequestBq>> deleteSourcingBq(@RequestBody SourcingReqBqPojo sourcingReqBqPojo) throws JsonProcessingException {
		SourcingFormRequestBq sourcingFormRequestBq = sourcingFormRequestBqService.getBqById(sourcingReqBqPojo.getId());
		try {
			sourcingFormRequestBqService.deleteSourcingBq(sourcingFormRequestBq.getId());
			LOG.info(" After delete BQ display remaining formID :: " + sourcingReqBqPojo.getFormId());
			List<SourcingFormRequestBq> bqList = sourcingFormRequestBqService.findBqByFormIdByOrder(sourcingReqBqPojo.getFormId());
			Integer count = 1;
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (SourcingFormRequestBq bq : bqList) {
					bq.setBqOrder(count);
					sourcingFormRequestBqService.updateSourcingBq(bq);
					count++;
				}
			}
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", messageSource.getMessage("rft.bq.success.delete", new Object[] { sourcingFormRequestBq.getName() }, Global.LOCALE));
			return new ResponseEntity<List<SourcingFormRequestBq>>(bqList, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error while deleting BQ  [ " + sourcingFormRequestBq.getName() + " ]" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.error.delete", new Object[] { sourcingFormRequestBq.getName() }, Global.LOCALE));
			return new ResponseEntity<List<SourcingFormRequestBq>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}

	}

	@RequestMapping(path = "/getSourcingBqForEdit", method = RequestMethod.GET)
	public ResponseEntity<SourcingFormRequestBqItem> getSourcingBqForEdit(@RequestParam String bqId, @RequestParam String bqItemId) {
		LOG.info("  getBqForEdit  : " + bqId + " bqItemId  :" + bqItemId);
		SourcingFormRequestBqItem sourcingBqItem = null;
		LOG.info("BQ EDIT :: bqId" + bqId + " :: bqItemId :: " + bqItemId);
		try {
			sourcingBqItem = sourcingFormRequestBqService.getBqItembyBqItemId(bqItemId);
			sourcingBqItem = sourcingBqItem.createShallowCopy();
		} catch (Exception e) {
			LOG.error("Error while getting BQ Item for Edit : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<SourcingFormRequestBqItem>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (sourcingBqItem != null) {
			return new ResponseEntity<SourcingFormRequestBqItem>(sourcingBqItem, HttpStatus.OK);
		} else {
			LOG.warn("The BQ Item for the specified Event not found. Bad Request.");
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("buyer.rftbq.item.notfound", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<SourcingFormRequestBqItem>(null, headers, HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(path = "/deleteSourcingBqItems/{bqId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<SourcingBqItemResponsePojo> deleteSourcingBqItems(@RequestParam(name = "items", required = true) String items, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo, @PathVariable("bqId") String bqId) throws JsonProcessingException {
		LOG.info("Delete Bq Items......." + items);
		List<SourcingFormRequestBqItem> bqItemList = null;
		HttpHeaders header = new HttpHeaders();

		SourcingBqItemResponsePojo bqItemResponsePojo = new SourcingBqItemResponsePojo();
		if (StringUtils.checkString(items).length() > 0) {
			try {
				String[] bqItemsIds = items.split(",");
				sourcingFormRequestBqService.deleteSourcingBqItems(bqItemsIds, bqId);
				header.add("success", messageSource.getMessage("rft.bqitems.success.delete", new Object[] {}, Global.LOCALE));
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
				bqItemList = sourcingFormRequestBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
				List<SourcingBqItemPojo> levelOrderList = sourcingFormRequestBqService.getAllLevelOrderBqItemByBqId(bqId, searchVal);
				bqItemResponsePojo.setBqItemList(bqItemList);
				bqItemResponsePojo.setLeveLOrderList(levelOrderList);
			} catch (NotAllowedException e) {
				LOG.error("Not Allowed Exception: " + e.getMessage(), e);
				header.add("error", e.getMessage());
				return new ResponseEntity<SourcingBqItemResponsePojo>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (Exception e) {
				LOG.error("Error while delete bq items: " + e.getMessage(), e);
				header.add("error", "Error during delete : " + e.getMessage());
				return new ResponseEntity<SourcingBqItemResponsePojo>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<SourcingBqItemResponsePojo>(bqItemResponsePojo, header, HttpStatus.OK);
	}

	@RequestMapping(value = "/sourcingBqSaveDraft", method = RequestMethod.POST)
	public String bqSaveDraft(RedirectAttributes redir, @RequestParam("formId") String formId) {
		SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.getSourcingRequestById(formId);
		redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (sourcingFormRequest.getSourcingFormName() != null ? sourcingFormRequest.getSourcingFormName() : sourcingFormRequest.getFormId()) }, Global.LOCALE));
		return "redirect:createSourcingBQList/" + formId;

	}

	@RequestMapping(path = "/getSourcingBqItemForSearchFilter", method = RequestMethod.POST)
	public ResponseEntity<SourcingBqItemResponsePojo> getSourcingBqItemForSearchFilter(@RequestParam("bqId") String bqId, @RequestParam("formId") String formId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
		LOG.info(" getBqItemForSearchFilter  : " + bqId + " formId :" + formId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		List<SourcingFormRequestBqItem> bqItemList = null;
		HttpHeaders headers = new HttpHeaders();
		// sending total Bq Item count and Bq Item list bind in new Pojo class
		SourcingBqItemResponsePojo bqItemResponsePojo = new SourcingBqItemResponsePojo();
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
			bqItemList = sourcingFormRequestBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
			bqItemResponsePojo.setBqItemList(bqItemList);
			long totalBqItemCount = sourcingFormRequestBqService.getTotalBqItemCountByBqId(bqId, searchVal);
			LOG.info("totalBqItemCount :" + totalBqItemCount);
			bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);

			List<SourcingBqItemPojo> leveLOrderList = sourcingFormRequestBqService.getAllLevelOrderBqItemByBqId(bqId, searchVal);
			bqItemResponsePojo.setLeveLOrderList(leveLOrderList);
			for (SourcingFormRequestBqItem sourcingBqItem : bqItemList) {
				if (sourcingBqItem.getUom() != null) {
					sourcingBqItem.getUom().setCreatedBy(null);
					sourcingBqItem.getUom().setModifiedBy(null);
				}
			}

			if (pageLength != SecurityLibrary.getLoggedInUser().getBqPageLength()) {
				userService.updateUserBqPageLength(pageLength, SecurityLibrary.getLoggedInUser().getId());
				updateSecurityLibraryUser(pageLength);
			}
			return new ResponseEntity<SourcingBqItemResponsePojo>(bqItemResponsePojo, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error during Search Reset Bill Of Quantity :" + e.getMessage(), e);
			headers.add("error", "Error during Search reset Bill Of Quantity ");
			return new ResponseEntity<SourcingBqItemResponsePojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void updateSecurityLibraryUser(Integer pageLength) {
		/*
		 * UPDATE THE SECURITY CONTEXT AS THE BQ Page Length IS NOW CHANGED.
		 */
		// gonna need this to get user from Acegi
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		// get user obj
		AuthenticatedUser authUser = (AuthenticatedUser) auth.getPrincipal();
		// update the bq Page length on the user obj
		authUser.setBqPageLength(pageLength);
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(authUser, auth.getCredentials(), authUser.getAuthorities());
		upat.setDetails(auth.getDetails());
		ctx.setAuthentication(upat);
	}

	@RequestMapping(path = "/bqItemRfsTemplate/{bqId}", method = RequestMethod.GET)
	public void downloader(@PathVariable String bqId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "RfsItemTemplate.xlsx";
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

	private void eventDownloader(XSSFWorkbook workbook, String bqId) {
		XSSFSheet sheet = workbook.createSheet("BQ Item List");
		int r = 1;
		SourcingFormRequestBq bq = null;

		// Create Heading Row
		bq = sourcingFormRequestBqService.getBqById(bqId);
		buildHeader(bq, workbook, sheet);

		// Create Row
		SourcingFormRequestBq rfsBqList = sourcingFormRequestBqService.getAllbqItemsByBqId(bqId);
		for (SourcingFormRequestBqItem item : rfsBqList.getBqItems()) {
			r = buildRows(sheet, r, item.getBq(), item);
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

	private int buildRows(XSSFSheet sheet, int r, SourcingFormRequestBq sourcingFormRequestBq, SourcingFormRequestBqItem item) {
		Row row = sheet.createRow(r++);
		int cellNum = 0;
		row.createCell(cellNum++).setCellValue(item.getLevel() + "." + item.getOrder());
		row.createCell(cellNum++).setCellValue(item.getItemName());
		row.createCell(cellNum++).setCellValue(item.getItemDescription() != null ? item.getItemDescription() : "");
		if (item.getOrder() == 0) {
			int colNum = 7;

			if (StringUtils.checkString(sourcingFormRequestBq.getField1Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(sourcingFormRequestBq.getField2Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(sourcingFormRequestBq.getField3Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(sourcingFormRequestBq.getField4Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(sourcingFormRequestBq.getField5Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(sourcingFormRequestBq.getField6Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(sourcingFormRequestBq.getField7Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(sourcingFormRequestBq.getField8Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(sourcingFormRequestBq.getField9Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(sourcingFormRequestBq.getField10Label()).length() > 0)
				colNum++;

			sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, cellNum - 1, colNum));
		} else {
			row.createCell(cellNum++).setCellValue(item.getUom() != null ? item.getUom().getUom() : "");
			row.createCell(cellNum++).setCellValue(item.getQuantity() != null ? String.valueOf(item.getQuantity()) : "");
			row.createCell(cellNum++).setCellValue(item.getUnitPrice() != null ? String.valueOf(item.getUnitPrice()) : "");
			row.createCell(cellNum++).setCellValue(item.getPriceType() != null ? item.getPriceType().getValue() : "");
			row.createCell(cellNum++).setCellValue(item.getUnitBudgetPrice() != null ? String.valueOf(item.getUnitBudgetPrice()) : "");
			if (StringUtils.checkString(sourcingFormRequestBq.getField1Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField1());
			if (StringUtils.checkString(sourcingFormRequestBq.getField2Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField2());
			if (StringUtils.checkString(sourcingFormRequestBq.getField3Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField3());
			if (StringUtils.checkString(sourcingFormRequestBq.getField4Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField4());

			if (StringUtils.checkString(sourcingFormRequestBq.getField5Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField5());
			if (StringUtils.checkString(sourcingFormRequestBq.getField6Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField6());
			if (StringUtils.checkString(sourcingFormRequestBq.getField7Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField7());
			if (StringUtils.checkString(sourcingFormRequestBq.getField8Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField8());
			if (StringUtils.checkString(sourcingFormRequestBq.getField9Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField9());
			if (StringUtils.checkString(sourcingFormRequestBq.getField10Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField10());
		}
		// Auto Fit
		for (int i = 0; i < 18; i++) {
			sheet.autoSizeColumn(i, true);
		}
		return r;
	}

	private void buildHeader(SourcingFormRequestBq bq, XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);

		// For style
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.BQ_EXCEL_COLUMNS_TYPE_2) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
		// if (bq.getField1Label() != null) {
		// cell = rowHeading.createCell(7);
		// cell.setCellValue(bq.getField1Label());
		// cell.setCellStyle(styleHeading);
		// }
		// if (bq.getField2Label() != null) {
		// cell = rowHeading.createCell(8);
		// cell.setCellValue(bq.getField2Label());
		// cell.setCellStyle(styleHeading);
		// }
		// if (bq.getField3Label() != null) {
		// cell = rowHeading.createCell(9);
		// cell.setCellValue(bq.getField3Label());
		// cell.setCellStyle(styleHeading);
		// }
		// if (bq.getField4Label() != null) {
		// cell = rowHeading.createCell(10);
		// cell.setCellValue(bq.getField4Label());
		// cell.setCellStyle(styleHeading);
		// }
		// if (bq.getField5Label() != null) {
		// cell = rowHeading.createCell(11);
		// cell.setCellValue(bq.getField5Label());
		// cell.setCellStyle(styleHeading);
		// }
		// if (bq.getField6Label() != null) {
		// cell = rowHeading.createCell(12);
		// cell.setCellValue(bq.getField6Label());
		// cell.setCellStyle(styleHeading);
		// }
		// if (bq.getField7Label() != null) {
		// cell = rowHeading.createCell(13);
		// cell.setCellValue(bq.getField7Label());
		// cell.setCellStyle(styleHeading);
		// }
		// if (bq.getField8Label() != null) {
		// cell = rowHeading.createCell(14);
		// cell.setCellValue(bq.getField8Label());
		// cell.setCellStyle(styleHeading);
		// }
		// if (bq.getField9Label() != null) {
		// cell = rowHeading.createCell(15);
		// cell.setCellValue(bq.getField9Label());
		// cell.setCellStyle(styleHeading);
		// }
		// if (bq.getField10Label() != null) {
		// cell = rowHeading.createCell(16);
		// cell.setCellValue(bq.getField10Label());
		// cell.setCellStyle(styleHeading);
		// }
	}

	@RequestMapping(value = "/uploadRfsBqItems", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> handleFormUpload(@RequestParam("file") MultipartFile file, @RequestParam("bqId") String bqId, @RequestParam("eventId") String eventId, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
		LOG.info("UPLOADING STARTED ...... BQ Id :: " + bqId + "Event Id :: " + eventId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		List<SourcingFormRequestBqItem> bqItemList = null;
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

					totalBqItemCount = sourcingFormRequestBqService.uploadBqFile(bqId, eventId, convFile, SecurityLibrary.getLoggedInUserTenantId());
					if (totalBqItemCount == 0) {
						LOG.info("totalBqItemCount " + totalBqItemCount);
						throw new Exception("Please fill data in excel file");
					}

				} catch (ExcelParseException e) {
					bqItemList = sourcingFormRequestBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
					for (SourcingFormRequestBqItem bqItem : bqItemList) {
						if (bqItem.getUom() != null) {
							bqItem.getUom().setCreatedBy(null);
							bqItem.getUom().setModifiedBy(null);
						}
					}
					headers.add("error", e.getMessage());
					response.put("list", bqItemList);
					response.put("totalBqItemCount", totalBqItemCount);
					return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.BAD_REQUEST);

				} catch (Exception e) {
					// e.printStackTrace();
					// bqList = rfqBqService.findBqbyBqId(bqId);
					bqItemList = sourcingFormRequestBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
					for (SourcingFormRequestBqItem bqItem : bqItemList) {
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
			bqItemList = sourcingFormRequestBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
			for (SourcingFormRequestBqItem bqItem : bqItemList) {
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
		bqItemList = sourcingFormRequestBqService.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
		for (SourcingFormRequestBqItem bqItem : bqItemList) {
			if (bqItem.getUom() != null) {
				bqItem.getUom().setCreatedBy(null);
				bqItem.getUom().setModifiedBy(null);
			}
		}
		SourcingFormRequestBq bq = sourcingFormRequestBqService.getBqById(bqId);
		RfsBqPojo rfsBqColumns = new RfsBqPojo(bq);
		response.put("rfsBqColumns", rfsBqColumns);

		headers.add("success", messageSource.getMessage("buyer.rftbq.upload.success", new Object[] {}, Global.LOCALE));
		response.put("list", bqItemList);
		response.put("totalBqItemCount", totalBqItemCount);
		return new ResponseEntity<Map<String, Object>>(response, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateSourcingBqOrder/{formId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SourcingFormRequestBq>> updateSourcingBqOrder(@RequestBody String[] bqIds, @PathVariable("formId") String formId) {
		try {
			if (bqIds != null && bqIds.length > 0) {
				LOG.info("formId : " + formId);
				Integer count = 1;
				for (String bqId : bqIds) {
					SourcingFormRequestBq bq = sourcingFormRequestBqService.getBqById(bqId);
					bq.setBqOrder(count);
					sourcingFormRequestBqService.updateSourcingBq(bq);
					count++;
				}
				List<SourcingFormRequestBq> bqList = sourcingFormRequestBqService.findBqByFormIdByOrder(formId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("success", messageSource.getMessage("rft.bq.order", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingFormRequestBq>>(bqList, headers, HttpStatus.OK);
			} else {
				List<SourcingFormRequestBq> bqList = sourcingFormRequestBqService.findBqByFormIdByOrder(formId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("rft.bq.order.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SourcingFormRequestBq>>(bqList, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			LOG.error("Error while Ordering sourcing BQ : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("rft.bq.order.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SourcingFormRequestBq>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}
}
