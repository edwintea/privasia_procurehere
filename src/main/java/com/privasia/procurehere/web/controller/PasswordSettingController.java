package com.privasia.procurehere.web.controller;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.FinanceAuditTrail;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.PasswordSettings;
import com.privasia.procurehere.core.entity.SupplierAuditTrail;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.BuyerAuditTrailService;
import com.privasia.procurehere.service.FinanceAuditTrailService;
import com.privasia.procurehere.service.OwnerAuditTrailService;
import com.privasia.procurehere.service.PasswordSettingService;
import com.privasia.procurehere.service.SupplierAuditTrailService;

/**
 * @author sarang
 */
@Controller
public class PasswordSettingController {

	private static final Logger LOG = LogManager.getLogger(PasswordSettingController.class);
	
	@Autowired
	BuyerAuditTrailService buyerAuditTrialService;

	@Autowired
	SupplierAuditTrailService supplierAuditTrialService;

	@Autowired
	FinanceAuditTrailService financeAuditTrialService;

	@Autowired
	OwnerAuditTrailService ownerAuditTrialService;

	@Autowired
	FinanceAuditTrailService financeAuditTrailService;

	@Autowired
	PasswordSettingService passwordSettingService;

	@RequestMapping(path = "/passwordSetting", method = RequestMethod.GET)
	public String passwordSetting(Model model) {
		PasswordSettings passwordSetting = passwordSettingService.getPasswordSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		passwordSetting.setEnableReusePassword((!passwordSetting.getEnableReusePassword()));
		
		model.addAttribute("passwordSetting", passwordSetting);
		return "passwordSetting";
	}

	@RequestMapping(path = "/passwordSetting", method = RequestMethod.POST)
	public String passwordSettingDetails(@ModelAttribute("PasswordSettings") PasswordSettings passwordSetting, Model model) {
		try {
			PasswordSettings dbPasswordSetting = passwordSettingService.getPasswordSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (dbPasswordSetting != null) {
				dbPasswordSetting.setContainNonAlphanumeric(passwordSetting.getContainNonAlphanumeric());
				dbPasswordSetting.setContainOneLowerCaseLetters(passwordSetting.getContainOneLowerCaseLetters());
				dbPasswordSetting.setContainOneNumbers(passwordSetting.getContainOneNumbers());
				dbPasswordSetting.setContainOneUpperCaseLetters(passwordSetting.getContainOneUpperCaseLetters());
				dbPasswordSetting.setEnableExpiration(passwordSetting.getEnableExpiration());
				dbPasswordSetting.setEnableFailedAttempts(passwordSetting.getEnableFailedAttempts());
				dbPasswordSetting.setEnableReusePassword((!passwordSetting.getEnableReusePassword()));
				dbPasswordSetting.setFailedAttempts(passwordSetting.getFailedAttempts());
				dbPasswordSetting.setNumberOfPasswordRemember(passwordSetting.getNumberOfPasswordRemember());
				dbPasswordSetting.setPasswordLength(passwordSetting.getPasswordLength());
				dbPasswordSetting.setPasswordExpiryInDays(passwordSetting.getPasswordExpiryInDays());
				dbPasswordSetting.setModifiedDate(new Date());
				dbPasswordSetting.setModifiedBy(SecurityLibrary.getLoggedInUser());
				passwordSetting = passwordSettingService.saveOrUpdate(dbPasswordSetting);
				model.addAttribute("success", "Password setting updated successfully");
				model.addAttribute("passwordSetting", passwordSetting);
			}
		} catch (Exception e) {
			model.addAttribute("passwordSetting", passwordSetting);
			model.addAttribute("error", "Error while updating password setting");
			return "redirect:passwordSetting";
		}
		try {
			switch (SecurityLibrary.getLoggedInUser().getTenantType()) {
			case BUYER:
				BuyerAuditTrail buyerAudit = new BuyerAuditTrail();
				buyerAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				buyerAudit.setActionDate(passwordSetting.getModifiedDate());
				buyerAudit.setActivity(AuditTypes.UPDATE);
				buyerAudit.setDescription("Password setting updated");
				buyerAudit.setTenantId(passwordSetting.getTenantId());
				buyerAudit.setModuleType(ModuleType.PasswordSetting);
				buyerAudit.setModuleTypeName("Buyer Settings");
				buyerAuditTrialService.save(buyerAudit);
				break;
			case OWNER:
				OwnerAuditTrail ownerAudit = new OwnerAuditTrail();
				ownerAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				ownerAudit.setActionDate(passwordSetting.getModifiedDate());
				ownerAudit.setActivity(AuditTypes.UPDATE);
				ownerAudit.setDescription("Password setting updated");
				ownerAudit.setTenantId(passwordSetting.getTenantId());
				ownerAudit.setModuleType(ModuleType.PasswordSetting);
				ownerAudit.setModuleTypeName("Owner Settings");
				ownerAuditTrialService.save(ownerAudit);

				break;
			case SUPPLIER:
				SupplierAuditTrail supplierAudit = new SupplierAuditTrail();
				supplierAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				supplierAudit.setActionDate(passwordSetting.getModifiedDate());
				supplierAudit.setActivity(AuditTypes.UPDATE);
				supplierAudit.setDescription("Password setting updated");
				supplierAudit.setTenantId(passwordSetting.getTenantId());
				supplierAudit.setModuleType(ModuleType.PasswordSetting);
				supplierAudit.setModuleTypeName("Supplier Settings");
				supplierAuditTrialService.save(supplierAudit);
				break;

			case FINANCE_COMPANY:
				FinanceAuditTrail FinanceAuditTrail = new FinanceAuditTrail();
				FinanceAuditTrail.setActionBy(SecurityLibrary.getLoggedInUser());
				FinanceAuditTrail.setActionDate(passwordSetting.getModifiedDate());
				FinanceAuditTrail.setActivity(AuditTypes.UPDATE);
				FinanceAuditTrail.setDescription("Password setting updated");
				FinanceAuditTrail.setTenantId(passwordSetting.getTenantId());
				FinanceAuditTrail.setModuleType(ModuleType.PasswordSetting);
				FinanceAuditTrail.setModuleTypeName("Finance Company Settings");
				financeAuditTrailService.save(FinanceAuditTrail);
				break;

			default:
				break;

			}
		} catch (Exception e) {
			model.addAttribute("passwordSetting", passwordSetting);
			model.addAttribute("error", "Error while  saving Audit Trail");
			return "redirect:passwordSetting";
		}

		return "redirect:passwordSetting";
	}
}
