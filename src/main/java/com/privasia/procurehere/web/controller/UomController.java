package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UomPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.UomService;

@Controller
@RequestMapping(path = "/admin/uom")
public class UomController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	UomService uomService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	MessageSource messageSource;

	/**
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/uom", method = RequestMethod.GET)
	public ModelAndView uomCreate(@ModelAttribute Uom uom, Model model) {
		LOG.info("uomView.... called");
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("uom", "uomObject", new Uom());
	}

	@RequestMapping(path = "/uom", method = RequestMethod.POST)
	public ModelAndView saveUom(@Valid @ModelAttribute Uom uom, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("Save uom Method Called " + uom.getId());
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError oe : result.getAllErrors()) {
					LOG.info(oe.getObjectName() + " - " + oe.getDefaultMessage());
					errMessages.add(oe.getDefaultMessage());
				}
				model.addAttribute("errors", errMessages);
				return new ModelAndView("uom", "uomObject", uom);
			} else {
				if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
					uom.setBuyer(new Buyer());
					uom.getBuyer().setId(SecurityLibrary.getLoggedInUserTenantId());
				} else if (SecurityLibrary.getLoggedInUser().getOwner() != null) {
					uom.setOwner(new Owner());
					uom.getOwner().setId(SecurityLibrary.getLoggedInUserTenantId());
				}
				if (doValidate(uom)) {
					LOG.info("Validation successes..................................");
					if (StringUtils.checkString(uom.getId()).length() == 0) {
						uom.setCreatedBy(SecurityLibrary.getLoggedInUser());
						uom.setCreatedDate(new Date());
						uom.setStatus(uom.getStatus());
						uomService.createUom(uom);
						redir.addFlashAttribute("success", messageSource.getMessage("uom.save.success", new Object[] { uom.getUom() }, Global.LOCALE));
						LOG.info("create uom Called" + SecurityLibrary.getLoggedInUser());
					} else {

						Uom persistObj = uomService.getUomById(uom.getId());
						persistObj.setUom(uom.getUom());
						persistObj.setUomDescription(uom.getUomDescription());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setStatus(uom.getStatus());
						uomService.updateUom(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("uom.update.success", new Object[] { uom.getUom() }, Global.LOCALE));
						LOG.info("update Uom Called");
					}
				} else {
					model.addAttribute("error", messageSource.getMessage("uom.error.duplicate", new Object[] { uom.getUom() }, Global.LOCALE));
					model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					return new ModelAndView("uom", "uomObject", uom);

				}
			}

		} catch (Exception e) {
			LOG.error("Error in saving Uom " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("uom.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("uom", "uomObject", uom);
		}

		return new ModelAndView("redirect:uomList");
	}

	private boolean doValidate(Uom uom) {
		boolean validate = true;
		TenantType tenantType = TenantType.BUYER;
		if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
			tenantType = TenantType.BUYER;
		} else if (SecurityLibrary.getLoggedInUser().getOwner() != null) {
			tenantType = TenantType.OWNER;
		}
		if (uomService.isExists(uom, tenantType)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/uomList")
	public String listUom(Model model) throws JsonProcessingException {
		List<UomPojo> uomList = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("uomList", mapper.writeValueAsString(uomList));
		return "uomList";

	}

	@RequestMapping(path = "/editUom", method = RequestMethod.GET)
	public ModelAndView editUom(@RequestParam String id, Model model) throws JsonProcessingException {
		Uom uom = uomService.getUomById(id);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("uom", "uomObject", uom);
	}

	@RequestMapping(path = "/deleteUom", method = RequestMethod.GET)
	public String deleteUom(@RequestParam String id, Uom uom, Model model) throws JsonProcessingException {
		uom = uomService.getUomById(id);
		try {
			if (uom != null) {
				uom.setModifiedBy(SecurityLibrary.getLoggedInUser());
				uom.setModifiedDate(new Date());
				uomService.deleteUom(uom);
				model.addAttribute("success", messageSource.getMessage("uom.success.delete", new Object[] { uom.getUom() }, Global.LOCALE));
			}
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting UOM , " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("uom.error.delete.child.exist", new Object[] { uom.getUom() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting UOM , " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("uom.error.delete", new Object[] { uom.getUom() }, Global.LOCALE));
		}
		List<UomPojo> uomList = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("uomList", mapper.writeValueAsString(uomList));
		return "uomList";
	}

	@RequestMapping(path = "/uomListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Uom>> uomListData(TableDataInput input) throws JsonProcessingException {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TenantType tenantType = TenantType.BUYER;

			if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
				tenantType = TenantType.BUYER;
			} else if (SecurityLibrary.getLoggedInUser().getOwner() != null) {
				tenantType = TenantType.OWNER;
			}

			TableData<Uom> data = new TableData<Uom>(uomService.findUomForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, tenantType));
			data.setDraw(input.getDraw());
			long totalFilterCount = uomService.findTotalFilteredUomForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, tenantType);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = uomService.findTotalActiveUomForTenant(SecurityLibrary.getLoggedInUserTenantId(), tenantType);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<Uom>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching UOM list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching UOM list : " + e.getMessage());
			return new ResponseEntity<TableData<Uom>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(path = "/uomTemplate", method = RequestMethod.GET)
	public void downloadUomExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			uomService.uomDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());

		} catch (Exception e) {
			LOG.error("Error while downloading uom  template :: " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/exportUomCsvReport", method = RequestMethod.POST)
	public void downloadUomCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("uomPojo") UomPojo uomPojo) throws IOException {
		try {
			File file = File.createTempFile("Uom-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			LOG.info("After if condition controller........." + uomPojo.getEventIds());
			uomService.downloadCsvFileForUom(response, file, uomPojo, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getTenantType());
			
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "UOM is successfully downloaded" , SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.Uom);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}


			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Uom.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));

		}
	}
}
