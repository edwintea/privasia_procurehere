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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.ScoreRating;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.ScoreRatingPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ScoreRatingService;

/**
 * @author priyanka
 */
@Controller
@RequestMapping(path = "/admin/scoreRating")
public class ScoreRatingController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	ScoreRatingService scoreRatingService;

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

	@PostMapping("/exportScoreRatingCsvReport")
	public void downloadScoreRatingCSVRepost(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("scoreRatingPojo") ScoreRatingPojo scoreRatingPojo) throws IOException {
		try {
			File file = File.createTempFile("ScoreRating-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			scoreRatingService.downloadCsvFileForSCoreRating(response, file, scoreRatingPojo, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getTenantType());

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=ScoreRating.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));

		}
	}

	@RequestMapping(path = "/scoreRatingList", method = RequestMethod.GET)
	public String scoreRating(Model model) throws JsonProcessingException {
		return "scoreRatingList";

	}

	@GetMapping("/scoreRatingListData")
	public ResponseEntity<TableData<ScoreRating>> scoreRatingListData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());

			TableData<ScoreRating> data = new TableData<ScoreRating>(scoreRatingService.findScoreRatingForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, TenantType.BUYER));
			data.setDraw(input.getDraw());
			long totalFilterCount = scoreRatingService.findTotalFilteredScoreRatingForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, TenantType.BUYER);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = scoreRatingService.findTotalActiveScoreRatingForTenant(SecurityLibrary.getLoggedInUserTenantId(), TenantType.BUYER);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<ScoreRating>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching score rating list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching score rating list : " + e.getMessage());
			return new ResponseEntity<TableData<ScoreRating>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/deleteScoreRating", method = RequestMethod.GET)
	public String deleteScoreRating(@RequestParam("id") String id, Model model) {
		ScoreRating sr = scoreRatingService.editScoreRating(id);
		try {
			if (sr != null) {
				sr.setModifiedBy(SecurityLibrary.getLoggedInUser());
				sr.setModifiedDate(new Date());
				scoreRatingService.deleteScoreRating(id);
				model.addAttribute("success", messageSource.getMessage("score.rating.success.delete", new Object[] {}, Global.LOCALE));
			}
		} catch (DataIntegrityViolationException e) {
			LOG.error("Error while deleting Score Rating , " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("score.rating.error.delete.child.exist", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting Score Rating , " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("score.rating.error.delete", new Object[] {e.getMessage()}, Global.LOCALE));
		}
		return "scoreRatingList";

	}

	@GetMapping("/editScoreRating")
	public ModelAndView editScoreRating(@RequestParam("id") String id, Model model) {
		ScoreRating sr = scoreRatingService.editScoreRating(id);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("scoreRating", "scoreRatingPojo", sr);
	}

	@GetMapping("/createScoreRating")
	public ModelAndView createScoreRating(Model model) {
		LOG.info("create score .... called");
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("scoreRating", "scoreRatingPojo", new ScoreRating());
	}

	@RequestMapping(path = "/createScoreRating", method = RequestMethod.POST)
	public ModelAndView saveScoreRating(@Valid @ModelAttribute ScoreRating scoreRating, BindingResult result, Model model, RedirectAttributes redir) {
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, Global.LOCALE));
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError oe : result.getAllErrors()) {
					LOG.info(oe.getObjectName() + " - " + oe.getDefaultMessage());
					errMessages.add(oe.getDefaultMessage());
				}
				model.addAttribute("errors", errMessages);
				return new ModelAndView("scoreRating", "scoreRatingPojo", scoreRating);
			} else {
				if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
					scoreRating.setBuyer(new Buyer());
					scoreRating.getBuyer().setId(SecurityLibrary.getLoggedInUserTenantId());
				}

				if (!doValidate(scoreRating)) {
					if (StringUtils.checkString(scoreRating.getId()).length() == 0) {
						scoreRating.setCreatedBy(SecurityLibrary.getLoggedInUser());
						scoreRating.setCreatedDate(new Date());
						scoreRating.setStatus(scoreRating.getStatus());
						scoreRatingService.createScoreRating(scoreRating);
						redir.addFlashAttribute("success", messageSource.getMessage("score.rating.save.success", new Object[] {}, Global.LOCALE));
						LOG.info("create score rating Called" + SecurityLibrary.getLoggedInUser());
					} else {
						ScoreRating persistObj = scoreRatingService.findById(scoreRating.getId());
						persistObj.setMinScore(scoreRating.getMinScore());
						persistObj.setMaxScore(scoreRating.getMaxScore());
						persistObj.setRating(scoreRating.getRating());
						persistObj.setDescription(scoreRating.getDescription());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						persistObj.setStatus(scoreRating.getStatus());
						scoreRatingService.updateScoreRating(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("score.rating.update.success", new Object[] {}, Global.LOCALE));
					}
				} else {
					model.addAttribute("error", messageSource.getMessage("score.rating.error.duplicate", new Object[] {}, Global.LOCALE));
					model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					return new ModelAndView("scoreRating", "scoreRatingPojo", scoreRating);
				}
			}
		} catch (Exception e) {
			LOG.error("Error in saving Score Rating " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("score.rating.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("scoreRating", "scoreRatingPojo", scoreRating);
		}
		return new ModelAndView("redirect:scoreRatingList");
	}

	private boolean doValidate(ScoreRating scoreRating) {
		if(Status.INACTIVE.equals(scoreRating.getStatus())) {
			return false;
		}
		return scoreRatingService.checkScoreRangeOverlap(scoreRating, SecurityLibrary.getLoggedInUserTenantId());
	}
}
