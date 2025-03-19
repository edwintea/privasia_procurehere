/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Notes;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierBoardOfDirectors;
import com.privasia.procurehere.core.entity.SupplierCompanyProfile;
import com.privasia.procurehere.core.entity.SupplierFinanicalDocuments;
import com.privasia.procurehere.core.entity.SupplierNoteDocument;
import com.privasia.procurehere.core.entity.SupplierOtherCredentials;
import com.privasia.procurehere.core.entity.SupplierOtherDocuments;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.Coverage;
import com.privasia.procurehere.core.pojo.ExtentionValidity;
import com.privasia.procurehere.core.pojo.NotesPojo;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.SubscriptionService;
import com.privasia.procurehere.service.SuppNotesDocUploadService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Arc
 */
@Controller
@RequestMapping(value = "/supplierreg")
public class SupplierApprovalController implements Serializable {

	private static final long serialVersionUID = -3511236085076614718L;

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Resource
	MessageSource messageSource;

	@Autowired
	SupplierService supplierService;

	@Autowired
	UserService userService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	SuppNotesDocUploadService suppNotesDocUploadService;

	@Autowired
	SubscriptionService subscriptionService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	@RequestMapping(value = "/supplierSignupList", method = RequestMethod.GET)
	public String supplierPendingList(Model model) {
		try {
			List<SupplierPojo> list = supplierService.findPendingSuppliers();
			// if (CollectionUtil.isNotEmpty(list)) {
			// for (SupplierPojo supplier : list) {
			// supplier.setActionBy(null);
			// supplier.getRegistrationOfCountry().setCreatedBy(null);
			// supplier.setState(null);
			// }
			// }
			LOG.info("LIST SIZE : " + list.size());
			model.addAttribute("supplierPendingList", list);
			model.addAttribute("formSupplier", new Supplier());
			if (model.asMap().get("success") != null) {
				model.addAttribute("success", model.asMap().get("success"));
			}
		} catch (Exception e) {
			LOG.error("Error while getting Supplier list");
		}
		return "supplierSignupList";
	}

	@RequestMapping(value = "confirmSupplier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierPojo>> confirmSupplier(@RequestBody SearchVo searchVo) {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("Confirm Supplier.... " + searchVo.toString());
			Supplier supplier = supplierService.findSupplierById(searchVo.getId());
			supplier.setStatus(SupplierStatus.valueOf(searchVo.getStatus()));
			supplierService.confirmSupplier(supplier, true);
			headers.add("success", "Supplier '" + supplier.getCompanyName() + "' " + searchVo.getStatus());
		} catch (ApplicationException ae) {
			LOG.error("Error while confirming Supplier " + ae.getMessage(), ae);
			headers.add("error", messageSource.getMessage("supplier.error.confirm", new Object[] { ae.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierPojo>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			LOG.error("Error while confirming Supplier " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("supplier.error.confirm", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierPojo>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// List<Supplier> list = supplierService.findPendingSuppliers();

		List<SupplierPojo> list = supplierService.searchSuppliersForPagination(null, "Newest", null, "0");
		// if (CollectionUtil.isNotEmpty(list)) {
		// for (Supplier supplier : list) {
		// supplier.setActionBy(null);
		// supplier.getRegistrationOfCountry().setCreatedBy(null);
		// supplier.setState(null);
		// }
		// }
		return new ResponseEntity<List<SupplierPojo>>(list, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/rejectSupplierDetails", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierPojo>> rejectSupplierDetails(@RequestBody SearchVo searchVo, Model model, RedirectAttributes redirectAttributes) {
		LOG.info("ID : " + searchVo.getId());
		HttpHeaders headers = new HttpHeaders();
		try {
			Supplier supplier = supplierService.findSupplierById(StringUtils.checkString(searchVo.getId()));
			supplier.setStatus(SupplierStatus.REJECTED);
			supplierService.confirmSupplier(supplier, true);
			headers.add("success", "Supplier '" + supplier.getCompanyName() + "' " + SupplierStatus.REJECTED);
		} catch (ApplicationException ae) {
			LOG.error("Error while confirming Supplier " + ae.getMessage(), ae);
			headers.add("error", messageSource.getMessage("supplier.error.confirm", new Object[] { ae.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierPojo>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			LOG.error("Error while confirming Supplier " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("supplier.error.confirm", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierPojo>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<SupplierPojo> list = supplierService.findPendingSuppliers();
		// if (CollectionUtil.isNotEmpty(list)) {
		// for (Supplier supplier : list) {
		// supplier.setActionBy(null);
		// supplier.getRegistrationOfCountry().setCreatedBy(null);
		// supplier.setState(null);
		// }
		// }
		return new ResponseEntity<List<SupplierPojo>>(list, headers, HttpStatus.OK);
	}

	/**
	 * @param status
	 * @return
	 */
	@RequestMapping(value = "searchSupplier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierPojo>> searchSupplierByStatus(@RequestBody SearchVo searchVo) {
		List<SupplierPojo> list = null;
		try {
			LOG.info("Search supplier by status " + SupplierStatus.valueOf(StringUtils.checkString(searchVo.getStatus()).toUpperCase()) + " Order " + searchVo.getOrder() + " Global search : " + searchVo.getGlobalSreach());
			list = supplierService.searchSuppliersForPagination(searchVo.getStatus(), searchVo.getOrder(), searchVo.getGlobalSreach(), "0");
			// if (CollectionUtil.isNotEmpty(list)) {
			// for (Supplier supplier : list) {
			// supplier.setActionBy(null);
			// supplier.getRegistrationOfCountry().setCreatedBy(null);
			// supplier.setState(null);
			// }
			// }
		} catch (Exception e) {
			LOG.error("Error while confirming Supplier " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.error.confirm", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierPojo>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<SupplierPojo>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "/supplierDetails/{supplierId}", method = RequestMethod.GET)
	public String showSupplierDetailsView(@PathVariable(name = "supplierId") String supplierId, Model model) throws JsonProcessingException {
		Supplier supplier = supplierService.findSupplierForAdminProfileById(supplierId);
		if (supplier != null && CollectionUtil.isNotEmpty(supplier.getCountries())) {
			List<Coverage> coverages = new ArrayList<Coverage>();
			for (Country country : supplier.getCountries()) {
				Coverage coverage = new Coverage();
				coverage.setCode(country.getCountryCode());
				coverage.setName(country.getCountryName());
				if (CollectionUtil.isNotEmpty(supplier.getStates())) {
					List<Coverage> childs = new ArrayList<Coverage>();
					for (State state : supplier.getStates()) {
						if (state.getCountry() != null && coverage.getCode().equals(state.getCountry().getCountryCode())) {
							Coverage sta = new Coverage();
							sta.setCode(state.getStateCode());
							sta.setName(state.getStateName());
							childs.add(sta);
						}
					}
					coverage.setChildren(childs);
				}
				coverages.add(coverage);
			}
			supplier.setCoverages(coverages);
		}
		List<SupplierOtherDocuments> otherDocumentList = supplierService.findAllOtherDocumentBySupplierId(supplierId);
		List<SupplierSubscription> subscriptionsList = subscriptionService.getSupplierSubscriptionValidity(supplierId);
		List<SupplierFinanicalDocuments> documents = supplierService.findAllFinancialDocumentsBySupplierID(supplierId);
		List<SupplierBoardOfDirectors> directors = supplierService.findAllDirectorsBySupplierID(supplierId);

		User user = userService.getAdminUserForSupplier(supplier);
		model.addAttribute("adminUser", user);
		model.addAttribute("notesObject", new Notes());
		model.addAttribute("extensionValidity", new ExtentionValidity());
		model.addAttribute("supplier", supplier);
		model.addAttribute("subscriptionsList", subscriptionsList);
		model.addAttribute("otherDocsList", otherDocumentList);
		model.addAttribute("financialDocuments", documents);
		model.addAttribute("organisationalDetails", directors);
		List<NotesPojo> noteList = supplierService.notesForSupplier(supplierId, SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("totalEventInvited", supplierService.countTotalInvitedEventOfSupplier(supplierId));
		model.addAttribute("totalEventParticipated", supplierService.countTotalParticipatedEventOfSupplier(supplierId));
		model.addAttribute("totalEventAwarded", supplierService.totalEventAwardedSupplier(supplierId));
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("noteList", mapper.writeValueAsString(noteList));
		return "supplierDetails";
	}

	@RequestMapping(value = "/toggleAdminAccountLockedStatus", method = RequestMethod.POST)
	public String toggleAdminAccountLockedStatus(@ModelAttribute("adminUser") User adminUser, @RequestParam(name = "supplierId") String supplierId, Model model) {
		Supplier supplier = supplierService.findSupplierById(supplierId);
		User user = userService.getAdminUserForSupplier(supplier);
		if (user.getId().equalsIgnoreCase(adminUser.getId())) {
			LOG.info("Changing Locked Account Status for Admin account for Supplier : " + supplierId + " from : " + user.isLocked() + " to " + (!user.isLocked()) + " - Done By User : " + SecurityLibrary.getLoggedInUserLoginId());
			user.setLocked(!user.isLocked());
			if (!user.isLocked()) {
				user.setActive(true);
			}
			user.setFailedAttempts(null);
			userService.updateUser(user);
		} else {
			// error - someone hacked...
		}
		return "redirect:supplierDetails/" + supplierId;
	}

	@RequestMapping(value = "/confirmDetails", method = RequestMethod.POST)
	public @ResponseBody String confirmSupplierDetails(@RequestBody SearchVo searchVo, Model model) {
		LOG.info("ID : " + searchVo.getId());
		try {
			Supplier supplier = supplierService.findSupplierById(searchVo.getId());
			supplier.setStatus(SupplierStatus.valueOf(searchVo.getStatus()));
			supplier.setActionBy(SecurityLibrary.getLoggedInUser());
			supplier.setActionDate(new Date());
			supplierService.confirmSupplier(supplier, true);
			// model.addAttribute("success", "Supplier '" + supplier.getCompanyName() + "' " + searchVo.getStatus());
			model.addAttribute("success", messageSource.getMessage("rfa.supplier.param2", new Object[] { (supplier.getCompanyName() != null ? supplier.getCompanyName() : ""), (searchVo.getStatus() != null ? searchVo.getStatus() : "") }, Global.LOCALE));
		} catch (ApplicationException ae) {
			LOG.error("Error while confirming Supplier " + ae.getMessage(), ae);
			model.addAttribute("error", messageSource.getMessage("supplier.error.confirm", new Object[] { ae.getMessage() }, Global.LOCALE));
			return "supplierDetails";
		} catch (Exception e) {
			LOG.error("Error while confirming Supplier " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("supplier.error.confirm", new Object[] { e.getMessage() }, Global.LOCALE));
			return "supplierDetails";
		}
		return "supplierSignupList";
	}

	@RequestMapping(value = "/rejectDetails", method = RequestMethod.POST)
	public String rejectSupplier(@RequestParam("rejectId") String id, Model model, RedirectAttributes redirectAttributes) {
		LOG.info("ID : " + id);
		try {
			Supplier supplier = supplierService.findSupplierById(id);
			supplier.setStatus(SupplierStatus.REJECTED);
			supplier.setActionBy(SecurityLibrary.getLoggedInUser());
			supplier.setActionDate(new Date());
			supplierService.confirmSupplier(supplier, true);
			// model.addAttribute("success", "Supplier '" + supplier.getCompanyName() + "' " + supplier.getStatus());
			model.addAttribute("success", messageSource.getMessage("rfa.supplier.param2", new Object[] { (supplier.getCompanyName() != null ? supplier.getCompanyName() : ""), (supplier.getStatus() != null ? supplier.getStatus() : "") }, Global.LOCALE));
			model.addAttribute("formSupplier", supplierService.findSupplierById(id));
		} catch (Exception e) {
			LOG.error("Error while confirming Supplier " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("supplier.error.confirm", new Object[] { e.getMessage() }, Global.LOCALE));
			return "supplierDetails";
		}

		return "redirect:supplierSignupList";
	}

	/**
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/downloadOtherCredential/{id}", method = RequestMethod.GET)
	public void downloadCredFile(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Other Credential Download  :: :: " + id + "::::::");
			SupplierOtherCredentials otherCred = supplierService.findOtherCredentialById(id);
			response.setContentType(otherCred.getCredContentType());
			response.setContentLength(otherCred.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + otherCred.getFileName() + "\"");
			FileCopyUtils.copy(otherCred.getFileData(), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while File downloaded Company Profie : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/downloadCopmanyProfile/{id}", method = RequestMethod.GET)
	public void downloadCopmanyProfile(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Company Profile Download  :: :: " + id + "::::::");
			SupplierCompanyProfile companyProfile = supplierService.findCompanyProfileById(id);
			response.setContentType(companyProfile.getCompanyProfileContentType());
			response.setContentLength(companyProfile.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + companyProfile.getFileName() + "\"");
			FileCopyUtils.copy(companyProfile.getFileData(), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while File downloaded Company Profie : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/saveNote", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<NotesPojo>> saveNote(@RequestBody Notes notes) throws JsonProcessingException {
		LOG.info("incidentType  :: " + notes.getIncidentType() + " :: description  :: " + notes.getDescription() + " :: SupplierId ::" + notes.getId());
		try {
			notes.setSupplier(supplierService.findSuppById(notes.getId()));
			notes.setCreatedBy(SecurityLibrary.getLoggedInUser());
			notes.setCreatedDate(new Date());
			supplierService.saveNotes(notes);
			List<NotesPojo> noteList = supplierService.notesForSupplier(notes.getId(), SecurityLibrary.getLoggedInUserTenantId());
			HttpHeaders headers = new HttpHeaders();
			headers.add("info", messageSource.getMessage("supplier.notes.save", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<NotesPojo>>(noteList, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error while adding Notes to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.notes.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<NotesPojo>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/otherDocumentUpload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierOtherDocuments>> otherDocumentUpload(@RequestParam("file") MultipartFile file, @RequestParam(name = "desc", required = false) String desc, @RequestParam(name = "expiryDate", required = false) String expiryDate, @RequestParam("supplierId") String supplierId, HttpSession session) {
		String fileName = null;
		LOG.info("+++++++++++expiryDate++++++++" + expiryDate);
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				SupplierOtherDocuments otherDoc = new SupplierOtherDocuments();
				otherDoc.setDescription(desc);
				otherDoc.setFileData(bytes);
				otherDoc.setFileName(fileName);
				otherDoc.setUploadDate(new Date());
				otherDoc.setUploadBy(SecurityLibrary.getLoggedInUser());
				otherDoc.setContentType(file.getContentType());
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(expiryDate).length() > 0) {
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
					formatter.setTimeZone(timeZone);
					Date expDate = (Date) formatter.parse(expiryDate);
					otherDoc.setExpiryDate(expDate);
				}
				Supplier supplier = supplierService.findSupplierById(supplierId);
				otherDoc.setSupplier(supplier);
				supplierService.saveSupplierOtherDocuments(otherDoc);

				List<SupplierOtherDocuments> otherDocumentList = supplierService.findAllOtherDocumentBySupplierId(supplierId);

				LOG.info("File upload successfuly : " + otherDoc.toString() + " List " + otherDocumentList.size());
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.otherdocfile.upload", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<SupplierOtherDocuments>>(otherDocumentList, headers, HttpStatus.OK);
			} catch (Exception e) {
				LOG.error("You failed to upload " + fileName + ": " + e.getMessage(), e);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.file.uploaderror", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<SupplierOtherDocuments>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		}
		return null;

	}

	@RequestMapping(value = "/removeOtherDocument", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SupplierOtherDocuments>> removeOtherDocument(@RequestParam("removeOtherDocId") String removeOtherDocId, @RequestParam("otherDocFile") String otherDocFile, @RequestParam("supplierId") String supplierId) {
		try {
			if (StringUtils.checkString(removeOtherDocId).length() > 0) {
				supplierService.removeOtherDocuments(removeOtherDocId);
				List<SupplierOtherDocuments> list = supplierService.findAllOtherDocumentBySupplierId(supplierId);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.file.remove", new Object[] { otherDocFile }, Global.LOCALE));
				return new ResponseEntity<List<SupplierOtherDocuments>>(list, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while Error while removing Other Document : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierOtherDocuments>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("info", messageSource.getMessage("supplier.file.remove", new Object[] { otherDocFile }, Global.LOCALE));
		return new ResponseEntity<List<SupplierOtherDocuments>>(null, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/downloadOtherDocument/{id}", method = RequestMethod.GET)
	public void downloadRftFile(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			SupplierOtherDocuments docs = supplierService.findOtherDocumentById(id);
			response.setContentType(docs.getContentType());
			response.setContentLength(docs.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
			FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while downloaded Other Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/suppNotesUploadDocument", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierNoteDocument>> suppNotesUploadDocument(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, @RequestParam("supplierId") String supplierId, @RequestParam("visible") Boolean visible, Model model, RedirectAttributes redir) {
		LOG.info(" Visible & upload notes Upload Document called For supplierId :" + supplierId);
		String fileName = null;
		HttpHeaders headers = new HttpHeaders();
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				SupplierNoteDocument notesDoc = new SupplierNoteDocument();
				notesDoc.setCredContentType(file.getContentType());
				notesDoc.setDescription(desc);
				notesDoc.setFileName(fileName);
				notesDoc.setFileData(bytes);
				notesDoc.setVisible(visible);
				notesDoc.setUploadDate(new Date());
				notesDoc.setUploadTenantId(SecurityLibrary.getLoggedInUserTenantId());
				notesDoc.setCreatedBy(SecurityLibrary.getLoggedInUser());
				if (SecurityLibrary.getLoggedInUser() != null && SecurityLibrary.getLoggedInUser().getBuyer() != null) {
					notesDoc.setTenantType(TenantType.BUYER);
				} else if (SecurityLibrary.getLoggedInUser() != null && SecurityLibrary.getLoggedInUser().getOwner() != null) {
					notesDoc.setTenantType(TenantType.OWNER);
				}
				notesDoc.setSupplier(supplierService.findSuppById(supplierId));
				notesDoc.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
				suppNotesDocUploadService.saveOrUpdate(notesDoc);

				headers.add("success", messageSource.getMessage("notesDoc.fileUpload.success", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<SupplierNoteDocument>>(null, headers, HttpStatus.OK);
			} catch (Exception e) {
				headers.add("error", messageSource.getMessage("notesDoc.fileUpload.error", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<SupplierNoteDocument>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			headers.add("error", messageSource.getMessage("notesDoc.fileUpload.empty", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<SupplierNoteDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(path = "/suppNotesListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierNoteDocument>> suppNotesListData(TableDataInput input, @RequestParam String suppId) throws JsonProcessingException {
		try {
			TenantType tenantType = TenantType.BUYER;

			if (SecurityLibrary.getLoggedInUser().getBuyer() != null) {
				tenantType = TenantType.BUYER;
			} else if (SecurityLibrary.getLoggedInUser().getOwner() != null) {
				tenantType = TenantType.OWNER;
			}

			TableData<SupplierNoteDocument> data = new TableData<SupplierNoteDocument>(suppNotesDocUploadService.findSuppNotesDocBySuppId(suppId, SecurityLibrary.getLoggedInUserTenantId(), tenantType, input));
			data.setDraw(input.getDraw());
			long totalFilterCount = suppNotesDocUploadService.findTotalFilteredSupNotesDocList(suppId, SecurityLibrary.getLoggedInUserTenantId(), tenantType, input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = suppNotesDocUploadService.findTotalSupNotesDocList(suppId, SecurityLibrary.getLoggedInUserTenantId(), tenantType);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<SupplierNoteDocument>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching SupplierNoteDocument list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching SupplierNoteDocument list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierNoteDocument>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/downloadSuppNotesDoc/{docId}", method = RequestMethod.GET)
	public void downloadSuppNotesDoc(@PathVariable String docId, HttpServletResponse response) throws IOException {
		try {
			LOG.info(" Download Supp NotesDoc Download : " + docId);
			suppNotesDocUploadService.downloadSuppNotesDoc(docId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading Supplier Notes Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/removeSuppNotesDocument/{removeDocId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SupplierNoteDocument>> removeSuppNotesDocument(@PathVariable("removeDocId") String removeDocId, @RequestParam("supplierId") String supplierId) {
		LOG.info(" Remove Supp Notes Document  with documentId :" + removeDocId);

		HttpHeaders headers = new HttpHeaders();
		try {
			if (StringUtils.checkString(removeDocId).length() > 0) {
				SupplierNoteDocument supplierNoteDocument = suppNotesDocUploadService.findSuppNoteDocById(removeDocId);
				suppNotesDocUploadService.removeSuuppNoteDocument(supplierNoteDocument);
				headers.add("success", messageSource.getMessage("notesDoc.file.removedocument", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<List<SupplierNoteDocument>>(null, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			headers.add("error", messageSource.getMessage("notesDoc.file.removedocumenterror", new Object[] { e.getMessage() }, Global.LOCALE));
			e.printStackTrace();
			return new ResponseEntity<List<SupplierNoteDocument>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		headers.add("info", messageSource.getMessage("notesDoc.file.remove", new Object[] {}, Global.LOCALE));
		return new ResponseEntity<List<SupplierNoteDocument>>(null, headers, HttpStatus.OK);

	}

	@RequestMapping(value = "/updateSuppNotesDocumentDesc/{docId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SupplierNoteDocument>> updateSuppNotesDocumentDesc(@PathVariable("docId") String docId, @RequestParam("docDesc") String docDesc, @RequestParam("supplierId") String supplierId) {
		LOG.info(" Update Supp Notes Document Desc called ==============" + docId);
		try {
			suppNotesDocUploadService.updateSuppNotesDocumentDesc(docId, docDesc, supplierId);
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "File Description updated successfully ");
			return new ResponseEntity<List<SupplierNoteDocument>>(null, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("You failed to update File Description :" + e.getMessage(), e);
			e.printStackTrace();
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "You failed to update File Description :" + e.getMessage());
			return new ResponseEntity<List<SupplierNoteDocument>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(path = "/ExportSupplierProfile", method = RequestMethod.POST)
	public void ExportSupplierProfile(HttpServletRequest request, HttpServletResponse response, @RequestParam String supplierId, HttpSession session) throws IOException {
		try {
			String filename = "";
			JasperPrint jasperPrint = null;
			String teanantType="";
			if (SecurityLibrary.getLoggedInUser() != null && SecurityLibrary.getLoggedInUser().getBuyer() != null) {
				teanantType="Buyer";
			} else if (SecurityLibrary.getLoggedInUser() != null && SecurityLibrary.getLoggedInUser().getOwner() != null) {
				teanantType="Owner";
			}

			jasperPrint = favoriteSupplierService.getSupplierProfilePdfForAll(supplierId, session, SecurityLibrary.getLoggedInUserTenantId(), teanantType);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
		} catch (Exception e) {
			LOG.error("Could not generate Profile Report. " + e.getMessage(), e);
		}
	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);

		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();

	}

	@RequestMapping(value = "searchSupplierForPagination", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierPojo>> searchSupplierForPagination(@RequestBody SearchVo searchVo, @RequestParam(required = false) String pageNo) {
		List<SupplierPojo> list = null;
		try {
			LOG.info("Search supplier by status " + SupplierStatus.valueOf(StringUtils.checkString(searchVo.getStatus()).toUpperCase()) + " Order " + searchVo.getOrder() + " Global search : " + searchVo.getGlobalSreach() + " " + searchVo.getPageNo());

			list = supplierService.searchSuppliersForPagination(searchVo.getStatus(), searchVo.getOrder(), searchVo.getGlobalSreach(), searchVo.getPageNo());
			// if (CollectionUtil.isNotEmpty(list)) {
			// for (Supplier supplier : list) {
			// supplier.setActionBy(null);
			// supplier.getRegistrationOfCountry().setCreatedBy(null);
			// supplier.setState(null);
			// }
			// }
		} catch (Exception e) {
			LOG.error("Error while confirming Supplier " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.error.confirm", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierPojo>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<SupplierPojo>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "/resendSupplierActivationLink", method = RequestMethod.POST)
	public String resendSupplierActivationLink(@RequestParam(name = "supplierId") String supplierId, RedirectAttributes redirectAttributes) {
		try {
			Supplier supplier = supplierService.findSupplierById(supplierId);
			if (supplier != null) {
				LOG.info("supplier found");
				UUID uuid = UUID.randomUUID();
				String password = uuid.toString().replaceAll("-", "").toUpperCase();
				password = password.substring(0, 9);
				BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
				supplier.setPassword(enc.encode(password));
				User user = userService.getAdminUserForSupplier(supplier);
				if (user != null) {
					LOG.info("admin user found");
					user.setPassword(enc.encode(password));
					userService.updateUser(user);
				}
				supplierService.updateSupplier(supplier);
				sendEmailToSupplier(user, password, supplier);
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", messageSource.getMessage("flasherror.resending.email", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("Error resending email to supplier about his account : " + e.getMessage(), e);
		}
		redirectAttributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.account.setup", new Object[] {}, Global.LOCALE));
		return "redirect:supplierDetails/" + supplierId;
	}

	private void sendEmailToSupplier(User user, String password, Supplier supplier) {
		try {
			if (user != null) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("userName", user.getName());
				map.put("loginId", user.getLoginId());
				map.put("password", password);
				map.put("appUrl", APP_URL + "/login");
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
				String timeZone = "GMT+8:00";
				df.setTimeZone(TimeZone.getTimeZone(timeZone));
				map.put("date", df.format(new Date()));
				LOG.info("" + supplier.getCommunicationEmail());
				if(user.getEmailNotifications())
				notificationService.sendEmail(supplier.getCommunicationEmail(), "Your Procurehere account is created", map, Global.SUPPLIER_CREATION_TEMPLATE);
			}

		} catch (Exception e) {
			LOG.error("Error sending email to supplier about his account : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/extendSupplierValidity/{supplierId}", method = RequestMethod.POST)
	public String extendSupplierValidity(@ModelAttribute(name = "extensionValidity") ExtentionValidity extentionValidity, @PathVariable("supplierId") String supplierId, RedirectAttributes redirectAttributes) {
		try {
			Supplier supplier = supplierService.findSupplierById(supplierId);
			if (supplier != null) {
				LOG.info(SecurityLibrary.getLoggedInUser().getName() + " is updating subscription validity of " + supplier.getCompanyName() + " to " + extentionValidity.getExtensionDate() + " with reason " + extentionValidity.getExtensionReason());
				supplierService.extendValidity(supplier, extentionValidity);
				redirectAttributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.account.validity.updated", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", messageSource.getMessage("flashsuccess.account.validity.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("Error extending validity : " + e.getMessage(), e);
		}
		return "redirect:/supplierreg/supplierDetails/" + supplierId;
	}

}