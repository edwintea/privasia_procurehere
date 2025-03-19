package com.privasia.procurehere.web.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.PrTemplateService;

import java.nio.charset.StandardCharsets;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PrRemarkController extends PrBaseController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	PrTemplateService prTemplateService;
	
	@ModelAttribute("step")
	public String getStep() {
		return "6";
	}

	@RequestMapping(path = "/prRemark/{prId}", method = RequestMethod.GET)
	public String prRemark(@PathVariable String prId, Model model) {
		LOG.info("create pr Remark GET called pr id :" + prId);
		Pr pr = prService.getLoadedPrById(prId);
		model.addAttribute("pr", pr);
		model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId));
		if (pr.getTemplate() != null && pr.getTemplate().getId() != null) {
			PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(pr.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("templateFields", prTemplate.getFields());
		}
		return "prRemark";
	}

	@RequestMapping(path = "/prRemark", method = RequestMethod.POST)
	public ModelAndView savePrRemark(@ModelAttribute Pr pr, Model model, RedirectAttributes redir, boolean isDraft) {
		LOG.info("create pr Remark POST called pr id :" + pr.getId());
		try {

			if (!isDraft && super.validatePr(pr, model, Pr.PrRemark.class)) {
				return new ModelAndView("prRemark", "pr", pr);
			}
			Pr persistObj = prService.getLoadedPrById(pr.getId());
			persistObj.setRemarks(pr.getRemarks() != null? prService.replaceSmartQuotes(new String(pr.getRemarks().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)) : pr.getRemarks());
			persistObj.setTermsAndConditions(pr.getTermsAndConditions() != null ? prService.replaceSmartQuotes(new String(pr.getTermsAndConditions().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)): pr.getTermsAndConditions());
			persistObj.setRemarkCompleted(Boolean.TRUE);
			// save As Draft
			if (isDraft) {
				persistObj.setStatus(PrStatus.DRAFT);
				prService.updatePr(persistObj);
				redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[] { persistObj.getPrId() }, Global.LOCALE));
				return new ModelAndView("redirect:/buyer/buyerDashboard");
			}
			prService.updatePr(persistObj);
		} catch (Exception e) {
			LOG.error("Error in saving Pr Remark " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("pr.remark.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), pr.getId()));
			return new ModelAndView("prRemark", "pr", pr);
		}
		return new ModelAndView("redirect:prSummary/" + pr.getId());
	}

	/**
	 * @param pr
	 * @param model
	 * @param redir
	 * @return
	 */
	@RequestMapping(path = "/savePrRemarkDraft", method = RequestMethod.POST)
	public ModelAndView savePrDraft(@ModelAttribute Pr pr, Model model, RedirectAttributes redir) {
		savePrRemark(pr, model, redir, Boolean.TRUE);
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}
}