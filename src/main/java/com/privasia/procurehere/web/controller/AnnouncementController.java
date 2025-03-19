package com.privasia.procurehere.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
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

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.Announcement;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.AnnouncementService;
import com.privasia.procurehere.service.BuyerSettingsService;

/**
 * @author pooja
 */
@Controller
@RequestMapping(path = "/buyer")
public class AnnouncementController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	AnnouncementService announcementService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	BuyerSettingsService buyerSettingsService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	/**
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		format.setTimeZone(timeZone);
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		timeFormat.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "announcementStart", new CustomDateEditor(format, true));
		binder.registerCustomEditor(Date.class, "announcementEnd", new CustomDateEditor(format, true));
		binder.registerCustomEditor(Date.class, "announcementStartTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "announcementEndTime", new CustomDateEditor(timeFormat, true));
	}

	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}

	@RequestMapping(path = "/announcementList")
	public String announcementList(Model model) {
		LOG.info("getting announcement List view...");
		return "announcementList";
	}

	@RequestMapping(path = "/announcement", method = RequestMethod.GET)
	public ModelAndView createAnnouncement(Model model) {
		model.addAttribute("btnValue", "Create");
		model.addAttribute("readOnlyData", false);
		return new ModelAndView("announcementCreate", "announcementObj", new Announcement());
	}

	@RequestMapping(path = "/createAnnouncement", method = RequestMethod.POST)
	public ModelAndView saveAnnouncement(@Valid @ModelAttribute("announcementObj") Announcement announcement, BindingResult result, Model model, HttpSession session, RedirectAttributes redir) throws ParseException {
		LOG.info("Saving announcement : " + announcement);
		List<String> errMessages = new ArrayList<String>();
		try {
			
			if (StringUtils.checkString(announcement.getId()).length() == 0) {
				model.addAttribute("btnValue", "Create");
			} else {
				model.addAttribute("btnValue", "Update");
			}

			Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
			Set<ConstraintViolation<Announcement>> constraintViolations = validator.validate(announcement, Default.class);
			for (ConstraintViolation<Announcement> cv : constraintViolations) {
				LOG.info("Message : " + cv.getMessage());
				errMessages.add(cv.getMessage());
				model.addAttribute("errors", errMessages);
			}
			if (errMessages.size() > 0) {
				return new ModelAndView("announcementCreate", "announcementObj", announcement);
			}

			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					LOG.info(err.getObjectName() + " - " + err.getDefaultMessage());
					errMessages.add(err.getDefaultMessage());
				}
				model.addAttribute("errors", errMessages);
				return new ModelAndView("announcementCreate", "announcementObj", announcement);
			} else {

				announcement.setPublicOrEmailContent(StringUtils.checkString(announcement.getPublicOrEmailContent()).replaceAll("&Acirc;", "").replaceAll("&#160;", " "));

				if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
					announcement.setBuyer(new Buyer());
					announcement.getBuyer().setId(SecurityLibrary.getLoggedInUserTenantId());
				}
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (StringUtils.checkString(strTimeZone).length() > 0) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
				dateTimeFormatter.setTimeZone(timeZone);

				Date startDateTime = null;
				Date endDateTime = null;

				if (announcement.getAnnouncementStart() != null && announcement.getAnnouncementStartTime() != null) {
					startDateTime = DateUtil.combineDateTime(announcement.getAnnouncementStart(), announcement.getAnnouncementStartTime(), timeZone);
					announcement.setAnnouncementStart(startDateTime);
				}
				if (announcement.getAnnouncementEnd() != null && announcement.getAnnouncementEndTime() != null) {
					endDateTime = DateUtil.combineDateTime(announcement.getAnnouncementEnd(), announcement.getAnnouncementEndTime(), timeZone);
					announcement.setAnnouncementEnd(endDateTime);
				}
				if (announcement.getReadOnlyData()) {
					if (announcement.getAnnouncementEnd() != null && announcement.getAnnouncementEnd().before(new Date())) {
						model.addAttribute("error", messageSource.getMessage("announcement.error.pastEndDate", new Object[] { dateTimeFormatter.format(announcement.getAnnouncementEnd()) }, Global.LOCALE));
						model.addAttribute("readOnlyData", announcement.getReadOnlyData());
						return new ModelAndView("announcementCreate", "announcementObj", announcement);
					}
				}
				if (!announcement.getReadOnlyData()) {
					if (announcement.getAnnouncementStart() != null && announcement.getAnnouncementStart().before(new Date())) {
						model.addAttribute("readOnlyData", announcement.getReadOnlyData());
						model.addAttribute("error", messageSource.getMessage("announcement.error.startDate", new Object[] { dateTimeFormatter.format(announcement.getAnnouncementStart()) }, Global.LOCALE));
						return new ModelAndView("announcementCreate", "announcementObj", announcement);
					}

					if (announcement.getAnnouncementStart() != null && announcement.getAnnouncementEnd() != null && announcement.getAnnouncementStart().after(announcement.getAnnouncementEnd())) {
						model.addAttribute("error", messageSource.getMessage("announcement.error.enddate", new Object[] { dateTimeFormatter.format(announcement.getAnnouncementEnd()) }, Global.LOCALE));
						model.addAttribute("readOnlyData", announcement.getReadOnlyData());
						return new ModelAndView("announcementCreate", "announcementObj", announcement);
					}
					if ((announcement.getPublicAnnouncement() == Boolean.TRUE || announcement.getEmail() == Boolean.TRUE) && StringUtils.checkString(announcement.getPublicOrEmailContent()).length() == 0) {
						model.addAttribute("error", messageSource.getMessage("announcement.publicOrEmailContent.error", new Object[] {}, Global.LOCALE));
						model.addAttribute("readOnlyData", announcement.getReadOnlyData());
						return new ModelAndView("announcementCreate", "announcementObj", announcement);
					}
				}

				if (StringUtils.checkString(announcement.getId()).length() == 0) {
					LOG.info("Creating new announcement for user" + SecurityLibrary.getLoggedInUser().getLoginId());
					announcement.setCreatedBy(SecurityLibrary.getLoggedInUser());
					announcement.setCreatedDate(new Date());
					announcementService.saveAnnouncement(announcement);
					redir.addFlashAttribute("success", messageSource.getMessage("announcement.save.success", new Object[] { announcement.getTitle() }, Global.LOCALE));
					LOG.info("saving announcement successfully for user" + SecurityLibrary.getLoggedInUser().getLoginId());
				} else {
					LOG.info("Updating announcement for user" + SecurityLibrary.getLoggedInUser().getName());
					Announcement persistObj = announcementService.getAnnouncementById(announcement.getId(), SecurityLibrary.getLoggedInUserTenantId());
					persistObj.setTitle(announcement.getTitle());
					persistObj.setFax(announcement.getFax());
					persistObj.setFaxContent(persistObj.getFax() == Boolean.TRUE ? announcement.getFaxContent() : "");
					persistObj.setSms(announcement.getSms());
					persistObj.setSmsContent(persistObj.getSms() == Boolean.TRUE ? announcement.getSmsContent() : "");
					persistObj.setEmail(announcement.getEmail());
					persistObj.setPublicAnnouncement(announcement.getPublicAnnouncement());
					if (StringUtils.checkString(announcement.getPublicOrEmailContent()).length() > 0 && (Boolean.TRUE == announcement.getPublicAnnouncement() || Boolean.TRUE == announcement.getEmail())) {
						persistObj.setPublicOrEmailContent(announcement.getPublicOrEmailContent());
					} else {
						persistObj.setPublicOrEmailContent(null);
					}
					boolean doAuditStatusChange = announcement.getStatus() != persistObj.getStatus();
					persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
					persistObj.setModifiedDate(new Date());
					persistObj.setAnnouncementStart(announcement.getAnnouncementStart());
					persistObj.setAnnouncementEnd(Boolean.TRUE == persistObj.getPublicAnnouncement()? announcement.getAnnouncementEnd() : null);
					persistObj.setStatus(announcement.getStatus());
					announcementService.updateAnnouncement(persistObj);
					if(doAuditStatusChange) {
						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(announcement.getStatus() == Status.ACTIVE ? AuditTypes.ACTIVE : AuditTypes.INACTIVE, "Announcement '" + announcement.getTitle() + "' is " + announcement.getStatus(), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.Announcement);
							buyerAuditTrailDao.save(buyerAuditTrail);
							LOG.info("--------------------------AFTER AUDIT---------------------------------------");
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
					}
					
					redir.addFlashAttribute("success", messageSource.getMessage("announcement.update.success", new Object[] { announcement.getTitle() }, Global.LOCALE));
					LOG.info("updating announcement successfully for user" + SecurityLibrary.getLoggedInUser().getLoginId());

				}
			}
		} catch (Exception e) {
			LOG.error("Error while saving announcement:" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("announcement.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("readOnlyData", announcement.getReadOnlyData());
			return new ModelAndView("announcementCreate", "announcementObj", announcement);
		}
		return new ModelAndView("redirect:announcementList");
	}

	@RequestMapping(path = "/editAnnouncement", method = RequestMethod.GET)
	public ModelAndView editAnnouncement(@RequestParam String id, Model model) {
		LOG.info("Editing announcement for user :" + SecurityLibrary.getLoggedInUserLoginId());
		Announcement announcement = announcementService.getAnnouncementById(id, SecurityLibrary.getLoggedInUserTenantId());
		if (announcement != null) {
			if (announcement.getAnnouncementStart() != null && announcement.getAnnouncementStart().after(new Date())) {
				model.addAttribute("readOnlyData", false);
			} else {
				model.addAttribute("readOnlyData", true);
			}

		}
		model.addAttribute("btnValue", "Update");

		// Remove junk chars...
		announcement.setPublicOrEmailContent(StringUtils.checkString(announcement.getPublicOrEmailContent()).replaceAll("&Acirc;", "").replaceAll("&#160;", " "));

		return new ModelAndView("announcementCreate", "announcementObj", announcement);
	}

	@RequestMapping(path = "/deleteAnnouncement", method = RequestMethod.GET)
	public String deleteAnnouncement(@RequestParam String id, Announcement announcement, Model model) {
		LOG.info("Deleting  annoucement for user :" + SecurityLibrary.getLoggedInUser().getLoginId());
		announcement = announcementService.getAnnouncementById(id, SecurityLibrary.getLoggedInUserTenantId());
		try {
			if (announcement != null) {
				announcementService.deleteAnnouncement(announcement);
				model.addAttribute("success", messageSource.getMessage("announcement.success.delete", new Object[] { announcement.getTitle() }, Global.LOCALE));
				LOG.info("Deleted announcement successfully for user :" + SecurityLibrary.getLoggedInUser().getLoginId());
			}

		} catch (Exception e) {
			LOG.error("Error while deleting announcement :" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("announcement.error.delete", new Object[] { announcement.getTitle() }, Global.LOCALE));
		}
		return "announcementList";

	}

	@RequestMapping(path = "/announcementListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<Announcement>> announcementListData(TableDataInput input) {
		try {
			LOG.info("Getting  annoucement data for user :" + SecurityLibrary.getLoggedInUser().getLoginId());
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			TableData<Announcement> data = new TableData<Announcement>(announcementService.findAnnouncementsByTeanantId(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = announcementService.findTotalFilteredAnnouncementForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = announcementService.findTotalActiveAnnouncementForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<Announcement>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching announcement list:" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching announcement list :" + e.getMessage());
			return new ResponseEntity<TableData<Announcement>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
