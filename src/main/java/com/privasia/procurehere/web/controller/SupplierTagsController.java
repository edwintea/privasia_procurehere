package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.SupplierTags;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.SupplierTagsService;

/**
 * @author RT-Kapil
 */
@Controller
@RequestMapping(path = "/buyer")
public class SupplierTagsController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	SupplierTagsService supplierTagsService;

	@Autowired
	BuyerService buyerService;

	@Resource
	MessageSource messageSource;

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/supplierTags", method = RequestMethod.GET)
	public ModelAndView createSupplierTags(@ModelAttribute SupplierTags supplierTags, Model model) {
		model.addAttribute("Buyer", buyerService.findAllBuyers());
		model.addAttribute("btnValue", "Create");
		return new ModelAndView("supplierTags", "supplierTags", new SupplierTags());
	}

	@RequestMapping(path = "/supplierTags", method = RequestMethod.POST)
	public ModelAndView saveSuppliertags(@ModelAttribute SupplierTags supplierTags, BindingResult result, Model model, RedirectAttributes redir) {
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());

				}
				model.addAttribute("btnValue", "Create");
				model.addAttribute("errors", errMessages);
				model.addAttribute("Buyer", buyerService.findAllBuyers());
				return new ModelAndView("supplierTags", "supplierTags", new SupplierTags());
			} else {

				if (doValidate(supplierTags)) {
					if (StringUtils.checkString(supplierTags.getId()).length() == 0) {
						supplierTags.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
						supplierTags.setCreatedBy(SecurityLibrary.getLoggedInUser());
						supplierTags.setCreatedDate(new Date());
						supplierTagsService.saveSupplierTags(supplierTags);
						redir.addFlashAttribute("success", messageSource.getMessage("suppliertags.save.success", new Object[] { supplierTags.getSupplierTags() }, Global.LOCALE));
					} else {
						SupplierTags persistObj = supplierTagsService.getSupplierTagsById(supplierTags.getId());
						persistObj.setSupplierTags(supplierTags.getSupplierTags());
						persistObj.setDescription(supplierTags.getDescription());
						persistObj.setStatus(supplierTags.getStatus());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						supplierTagsService.updateSupplierTags(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("suppliertags.update.success", new Object[] { supplierTags.getSupplierTags() }, Global.LOCALE));
					}
				} else {

					model.addAttribute("errors", messageSource.getMessage("suppliertags.error.duplicate", new Object[] { supplierTags.getSupplierTags() }, Global.LOCALE));
					model.addAttribute("btnValue", "Create");
					model.addAttribute("Buyer", buyerService.findAllBuyers());
					return new ModelAndView("supplierTags", "supplierTags", supplierTags);

				}
			}
		} catch (Exception e) {
			LOG.error("Error While saving the supplier Tags" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("suppliertags.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("Buyer", buyerService.findAllBuyers());
			return new ModelAndView("supplierTags", "supplierTags", new SupplierTags());
		}

		return new ModelAndView("redirect:listSupplierTags");

	}

	private boolean doValidate(SupplierTags supplierTags) {
		boolean validate = true;
		if (supplierTagsService.isExists(supplierTags, SecurityLibrary.getLoggedInUserTenantId())) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/listSupplierTags", method = RequestMethod.GET)
	public String listSupplierTags(Model model) {
		return "listSupplierTags";
	}

	@RequestMapping(path = "/supplierTagsData", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierTags>> supplierTagsData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			TableData<SupplierTags> data = new TableData<SupplierTags>(supplierTagsService.findSupplierTagsForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = supplierTagsService.findTotalFilteredSupplierTagsForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = supplierTagsService.findTotalSupplierTagsForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<SupplierTags>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching supplier Tags list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching supplier Tags list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierTags>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/editSupplierTags", method = RequestMethod.GET)
	public ModelAndView editSupplierTags(@RequestParam String id, Model model) {
		SupplierTags supplierTags = supplierTagsService.getSupplierTagsById(id);
		model.addAttribute("btnValue", "Update");
		return new ModelAndView("supplierTags", "supplierTags", supplierTags);
	}

	@RequestMapping(path = "/deleteSupplierTags", method = RequestMethod.GET)
	public String deleteSupplierTags(@RequestParam String id, Model model,RedirectAttributes redir) {
		SupplierTags supplierTags = supplierTagsService.getSupplierTagsById(id);
		try {
			supplierTags.setModifiedBy(SecurityLibrary.getLoggedInUser());
			supplierTags.setModifiedDate(new Date());
			supplierTagsService.deleteSupplierTags(supplierTags);
			redir.addFlashAttribute("success", messageSource.getMessage("suppliertags.success.delete", new Object[] { supplierTags.getSupplierTags() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting Supplier Tags , " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("suppliertags.error.delete", new Object[] { supplierTags.getSupplierTags() }, Global.LOCALE));
		}
		return "redirect:listSupplierTags";
	}

}
