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
import com.privasia.procurehere.core.entity.AgreementType;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.AgreementTypeService;

@Controller
@RequestMapping(path = "/buyer/agreementType")
public class AgreementTypeController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	AgreementTypeService agreementTypeService;
	
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

	@RequestMapping(path = "/agreementType", method = RequestMethod.GET)
	public ModelAndView agreementTypeCreate(@ModelAttribute AgreementType agreementType, Model model) {
		LOG.info("agreementTypeView.... called");
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("agreementType", "agreementTypeObject", new AgreementType());
	}

	@RequestMapping(path = "/agreementType", method = RequestMethod.POST)
	public ModelAndView saveAgreementType(@Valid @ModelAttribute AgreementType agreementType, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("Save agreementType Method Called " + agreementType.getId());
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError oe : result.getAllErrors()) {
					LOG.info(oe.getObjectName() + " - " + oe.getDefaultMessage());
					errMessages.add(oe.getDefaultMessage());
				}
				model.addAttribute("errors", errMessages);
				return new ModelAndView("agreementType", "agreementTypeObject", agreementType);
			} else {
				agreementType.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
				if (doValidate(agreementType)) {
					LOG.info("Validation successes..................................");
					if (StringUtils.checkString(agreementType.getId()).length() == 0) {
						agreementType.setCreatedBy(SecurityLibrary.getLoggedInUser());
						agreementType.setCreatedDate(new Date());
						agreementType.setStatus(agreementType.getStatus());
						agreementType.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
						agreementTypeService.createAgreementType(agreementType);
						redir.addFlashAttribute("success", messageSource.getMessage("agreement.type.save.success", new Object[] { agreementType.getAgreementType() }, Global.LOCALE));
						LOG.info("create agreementType Called" + SecurityLibrary.getLoggedInUser());
					} else {

						AgreementType persistObj = agreementTypeService.getAgreementTypeById(agreementType.getId());
						persistObj.setAgreementType(agreementType.getAgreementType());
						persistObj.setDescription(agreementType.getDescription());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setStatus(agreementType.getStatus());
						agreementTypeService.updateAgreementType(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("agreement.type.update.success", new Object[] { agreementType.getAgreementType() }, Global.LOCALE));
						LOG.info("update Agreement Type Called");
					}
				} else {
					model.addAttribute("error", messageSource.getMessage("agreement.type.error.duplicate", new Object[] { agreementType.getAgreementType() }, Global.LOCALE));
					model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					return new ModelAndView("agreementType", "agreementTypeObject", agreementType);

				}
			}

		} catch (Exception e) {
			LOG.error("Error in saving Agreement Type " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("agreement.type.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("agreementType", "agreementTypeObject", agreementType);
		}

		return new ModelAndView("redirect:agreementTypeList");
	}

	private boolean doValidate(AgreementType agreementType) {
		boolean validate = true;
		if (agreementTypeService.isExists(agreementType)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/agreementTypeList")
	public String listAgreementType(Model model) throws JsonProcessingException {
		List<AgreementType> agreementTypeList = agreementTypeService.getAllAgreementType(SecurityLibrary.getLoggedInUserTenantId());
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("agreementTypeList", mapper.writeValueAsString(agreementTypeList));
		return "agreementTypeList";

	}

	@RequestMapping(path = "/editAgreementType", method = RequestMethod.GET)
	public ModelAndView editAgreementType(@RequestParam String id, Model model) throws JsonProcessingException {
		AgreementType agreementType = agreementTypeService.getAgreementTypeById(id);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("agreementType", "agreementTypeObject", agreementType);
	}

	@RequestMapping(path = "/deleteAgreementType", method = RequestMethod.GET)
	public String deleteAgreementType(@RequestParam String id, AgreementType agreementType, Model model) throws JsonProcessingException {
		agreementType = agreementTypeService.getAgreementTypeById(id);
		try {
			if (agreementType != null) {
				agreementType.setModifiedBy(SecurityLibrary.getLoggedInUser());
				agreementType.setModifiedDate(new Date());
				agreementTypeService.deleteAgreementType(agreementType);
				model.addAttribute("success", messageSource.getMessage("agreement.type.success.delete", new Object[] { agreementType.getAgreementType() }, Global.LOCALE));
			}
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting Agreement Type , " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("agreement.type.delete.child.exist", new Object[] { agreementType.getAgreementType() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting Agreement Type , " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("agreement.type.error.delete", new Object[] { agreementType.getAgreementType() }, Global.LOCALE));
		}
		List<AgreementType> agreementTypeList = agreementTypeService.getAllAgreementType(SecurityLibrary.getLoggedInUserTenantId());
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("agreementTypeList", mapper.writeValueAsString(agreementTypeList));
		return "agreementTypeList";
	}

	@RequestMapping(path = "/agreementTypeListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<AgreementType>> agreementTypeListData(TableDataInput input) throws JsonProcessingException {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TableData<AgreementType> data = new TableData<AgreementType>(agreementTypeService.findAgreementTypeForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = agreementTypeService.findTotalFilteredAgreementTypeForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = agreementTypeService.findTotalActiveAgreementTypeForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<AgreementType>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching AgreementType list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching AgreementType list : " + e.getMessage());
			return new ResponseEntity<TableData<AgreementType>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(path = "/agreementTypeTemplate", method = RequestMethod.GET)
	public void downloadAgreementTypeExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			agreementTypeService.agreementTypeDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());

		} catch (Exception e) {
			LOG.error("Error while downloading Agreement Type template :: " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/exportAgreementTypeCsvReport", method = RequestMethod.POST)
	public void downloadAgreementTypeCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("agreementType") AgreementType agreementType) throws IOException {
		try {
			File file = File.createTempFile("AgreementType-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			LOG.info("After if condition controller.........");
			agreementTypeService.downloadCsvFileForAgreementType(response, file, agreementType, SecurityLibrary.getLoggedInUserTenantId());
			
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Agreement Type is successfully downloaded" , SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.AgreementType);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}


			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=AgreementType.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));

		}
	}
}
