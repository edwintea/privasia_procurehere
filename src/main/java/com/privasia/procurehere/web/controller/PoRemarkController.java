package com.privasia.procurehere.web.controller;

import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.PrTemplateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PoRemarkController extends PrBaseController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	PrTemplateService prTemplateService;
	
	@ModelAttribute("step")
	public String getStep() {
		return "6";
	}

	@RequestMapping(path = "/poRemark/{poId}", method = RequestMethod.GET)
	public String poRemark(@PathVariable String poId,@RequestParam String prId, Model model) {
		LOG.info("create po Remark GET called po id :" + poId);
		Pr pr = prService.getLoadedPrById(prId);
		Po po = poService.getLoadedPoById(poId);
		model.addAttribute("pr", pr);
		model.addAttribute("po", po);
		model.addAttribute("eventPermissions", poService.getUserPemissionsForPo(SecurityLibrary.getLoggedInUser().getId(), poId));
		if (pr.getTemplate() != null && pr.getTemplate().getId() != null) {
			PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(pr.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("templateFields", prTemplate.getFields());
		}
		return "poRemark";
	}

	@RequestMapping(path = "/poRemark", method = RequestMethod.POST)
	public ModelAndView savePoRemark(@ModelAttribute Po po,@RequestParam(required=false) String prId, Model model, RedirectAttributes redir, boolean isDraft) {
		LOG.info("create po Remark POST called po id :" + po.getId()+" from prID:"+prId);
		try {
			Po persistObj = poService.getLoadedPoById(po.getId());
			LOG.info("PO REMARKS UPDATE : >>>>>>>>>> "+po.getRemarks());
			persistObj.setRemarks(po.getRemarks() != null? prService.replaceSmartQuotes(new String(po.getRemarks().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)) : po.getRemarks());
			persistObj.setTermsAndConditions(po.getTermsAndConditions() != null ? prService.replaceSmartQuotes(new String(po.getTermsAndConditions().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)): po.getTermsAndConditions());
			persistObj.setRemarkCompleted(Boolean.TRUE);
			// save As Draft
			if (isDraft) {
				persistObj.setStatus(PoStatus.DRAFT);
				poService.updatePo(persistObj);
				redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[] { persistObj.getPoNumber() }, Global.LOCALE));
				return new ModelAndView("redirect:/buyer/buyerDashboard");
			}
			poService.updatePo(persistObj);
		} catch (Exception e) {
			LOG.error("Error in saving Po Remark " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("pr.remark.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), po.getId()));
			return new ModelAndView("poRemark", "po", po);
		}
		return new ModelAndView("redirect:poView/" + po.getId()+"?prId="+prId);
	}

	/**
	 * @param po
	 * @param model
	 * @param redir
	 * @return
	 */
	@RequestMapping(path = "/savePoRemarkDraft", method = RequestMethod.POST)
	public ModelAndView savePoDraft(@ModelAttribute Po po,@RequestParam String prId, Model model, RedirectAttributes redir) {
		LOG.info("SaveDraft PO Remark Called ....");
		savePoRemark(po, prId,model, redir, Boolean.TRUE);
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}
}