/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PoShare;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.SuppNotesDocUploadService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author Arc
 */
@Controller
@RequestMapping(value = "/finance")
public class FinanceSupplierController implements Serializable {

	private static final long serialVersionUID = -3511236085076614718L;

	private static final Logger LOG = LogManager.getLogger(Global.FINANCE_COMPANY_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	SupplierService supplierService;

	@Autowired
	UserService userService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	SuppNotesDocUploadService suppNotesDocUploadService;

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	@RequestMapping(value = "/financeSupplierList", method = RequestMethod.GET)
	public String financeSupplierList(Model model) {
		List<FinancePo> list = supplierService.findFinanceSuppliers(SecurityLibrary.getLoggedInUser().getTenantId());

		LOG.info("LIST SIZE : " + list.size());
		model.addAttribute("supplierList", list);
		model.addAttribute("formSupplier", new Supplier());
		if (model.asMap().get("success") != null) {
			model.addAttribute("success", model.asMap().get("success"));
		}
		return "financeSupplierList";
	}

	@RequestMapping(value = "/financeSupplierDetails/{supplierId}", method = RequestMethod.GET)
	public String showSupplierDetailsView(@PathVariable(name = "supplierId") String supplierId, Model model) throws JsonProcessingException {
		Supplier supplier = supplierService.findSupplierById(supplierId);
		if (supplier != null && CollectionUtil.isNotEmpty(supplier.getCountries())) {
			SupplierSettings supplierSettings = supplierSettingsService.getSupplierSettingsByTenantId(supplierId);

			if (supplierSettings != null) {
				if (supplierSettings.getPoShare() == PoShare.ALL) {
					model.addAttribute("poShareSetting", "Sharing All PO");
				} else if (supplierSettings.getPoShare() == PoShare.BUYER) {
					model.addAttribute("poShareSetting", "Sharing By Buyer PO");
				} else {
					model.addAttribute("poShareSetting", "Sharing Individually Po");
				}
			}else{
				model.addAttribute("poShareSetting", "Sharing Individually Po");
			}

			//List<SupplierOtherDocuments> otherDocumentList = supplierService.findAllOtherDocumentBySupplierId(supplierId);
			User user = userService.getAdminUserForSupplier(supplier);
			model.addAttribute("adminUser", user);

			model.addAttribute("supplier", supplier);
			//model.addAttribute("otherDocsList", otherDocumentList);
		}

		// LOG.info("NoteList : " + noteList);
		return "financeSupplierDetails";
	}

	@RequestMapping(value = "searchFinanceSupplier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<FinancePo>> searchSupplierByStatus(@RequestBody SearchVo searchVo) {
		List<FinancePo> list = null;
		try {
			LOG.info("-------------------------------------" + searchVo.getOrder());
			list = supplierService.searchFinanceSuppliers("", searchVo.getOrder(), searchVo.getGlobalSreach(), SecurityLibrary.getLoggedInUser().getTenantId());
			if (CollectionUtil.isNotEmpty(list)) {
				/*
				 * for (FinancePo supplier : list) { supplier.setActionBy(null);
				 * supplier.getRegistrationOfCountry().setCreatedBy(null); supplier.setState(null); }
				 */
			}
		} catch (Exception e) {
			LOG.error("Error while confirming Supplier " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.error.confirm", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<FinancePo>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<FinancePo>>(list, HttpStatus.OK);
	}

}