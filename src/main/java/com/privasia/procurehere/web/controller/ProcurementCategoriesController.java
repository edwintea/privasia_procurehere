package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.ProcurementCategoriesService;

/**
 * @author sana
 */
@Controller
@RequestMapping(path = "/buyer")
public class ProcurementCategoriesController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	ProcurementCategoriesService procurementCategoriesService;

	@Autowired
	BuyerService buyerService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Resource
	MessageSource messageSource;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@GetMapping("/procurementCategoriesList")
	public String listPaymentTermes(Model model) {
		return "procurementCategoriesList";
	}

	@GetMapping("/procurementCategoriesData")
	public ResponseEntity<TableData<ProcurementCategories>> procurementCategoriesData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<ProcurementCategories> data = new TableData<ProcurementCategories>(procurementCategoriesService.findProcurementCategoriesForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = procurementCategoriesService.findTotalFilteredProcurementCategoriesForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = procurementCategoriesService.findCountOfProcurementCategoriesForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ProcurementCategories>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Procurement Categories list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Procurement Categories list : " + e.getMessage());
			return new ResponseEntity<TableData<ProcurementCategories>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/procurementCategories")
	public ModelAndView createProcurementCategories(@ModelAttribute ProcurementCategories procurementCategories, Model model) {
		model.addAttribute("Buyer", buyerService.findAllBuyers());
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("procurementCategories", "procurementCategories", new ProcurementCategories());
	}

	@PostMapping("/procurementCategories")
	public ModelAndView saveProcurementCategories(@ModelAttribute ProcurementCategories procurementCategories, BindingResult result, Model model, RedirectAttributes redir) {
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				LOG.info("error..");
				model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				model.addAttribute("errors", errMessages);
				model.addAttribute("Buyer", buyerService.findAllBuyers());
				return new ModelAndView("procurementCategories", "procurementCategories", new ProcurementCategories());
			} else {

				if (doValidate(procurementCategories)) {
					LOG.info("Buyer In Procurement Categories" + SecurityLibrary.getLoggedInUser().getBuyer());
					if (StringUtils.checkString(procurementCategories.getId()).length() == 0) {
						procurementCategories.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
						procurementCategories.setCreatedBy(SecurityLibrary.getLoggedInUser());
						procurementCategories.setCreatedDate(new Date());
						procurementCategoriesService.saveProcurementCategories(procurementCategories);
						redir.addFlashAttribute("success", messageSource.getMessage("procurement.categories.save.success", new Object[] { procurementCategories.getProcurementCategories() }, Global.LOCALE));
					} else {
						ProcurementCategories persistObj = procurementCategoriesService.getProcurementCategoriesById(procurementCategories.getId());
						LOG.info("Procurement Categories :  " + persistObj);
						persistObj.setProcurementCategories(procurementCategories.getProcurementCategories());
						persistObj.setDescription(procurementCategories.getDescription());
						persistObj.setStatus(procurementCategories.getStatus());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						procurementCategoriesService.updateProcurementCategories(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("procurement.categories.update.success", new Object[] { procurementCategories.getProcurementCategories() }, Global.LOCALE));
					}
				} else {
					model.addAttribute("errors", messageSource.getMessage("procurement.categories.error.duplicate", new Object[] { procurementCategories.getProcurementCategories() }, Global.LOCALE));
					model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					model.addAttribute("Buyer", buyerService.findAllBuyers());
					return new ModelAndView("procurementCategories", "procurementCategories", procurementCategories);

				}
			}
		} catch (Exception e) {
			LOG.error("Error While saving the procurement Categories" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("procurement.categories.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("Buyer", buyerService.findAllBuyers());
			model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
			return new ModelAndView("procurementCategories", "procurementCategories", new ProcurementCategories());
		}
		return new ModelAndView("redirect:procurementCategoriesList");

	}

	private boolean doValidate(ProcurementCategories procurementCategories) {
		boolean validate = true;
		if (procurementCategoriesService.isExists(procurementCategories, SecurityLibrary.getLoggedInUserTenantId())) {
			LOG.info("inside validation");
			validate = false;
		}
		return validate;
	}

	@GetMapping("/editProcurementCategories")
	public ModelAndView editProcurementCategories(@RequestParam String id, Model model) {
		LOG.info("Edit Procurement Categories Called  " + id);
		ProcurementCategories procurementCategories = procurementCategoriesService.getProcurementCategoriesById(id);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("procurementCategories", "procurementCategories", procurementCategories);
	}

	@GetMapping("/deleteProcurementCategories")
	public String deleteProcurementCategories(@RequestParam String id, Model model) {
		LOG.info("Delete Procurement Categories Called");
		ProcurementCategories procurementCategories = procurementCategoriesService.getProcurementCategoriesById(id);
		try {
			procurementCategories.setModifiedBy(SecurityLibrary.getLoggedInUser());
			procurementCategories.setModifiedDate(new Date());
			procurementCategoriesService.deleteProcurementCategories(procurementCategories);
			model.addAttribute("success", messageSource.getMessage("procurement.categories.success.delete", new Object[] { procurementCategories.getProcurementCategories() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting Procurement Categories " + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("procurement.categories.error.delete", new Object[] { procurementCategories.getProcurementCategories() }, Global.LOCALE));
		}
		return "procurementCategoriesList";
	}

	@GetMapping("/procurementCategoriesTemplate")
	public void downloadProcurementCategoriesExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			procurementCategoriesService.procurementCategoriesDownloadTemplate(response, SecurityLibrary.getLoggedInUserTenantId());
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Procurement Categories is successfully downloaded" , SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ProcurementCategory);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Error while downloading procurement Categories template :: " + e.getMessage(), e);
		}
	}
}
