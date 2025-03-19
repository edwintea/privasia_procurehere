/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.GroupCodeService;

/**
 * @author jayshree
 *
 */
@Controller
@RequestMapping(path = "/buyer")
public class GroupCodeController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);
	
	@Autowired
	GroupCodeService groupCodeService;
	
	@Autowired
	BuyerService buyerService;

	@Resource
	MessageSource messageSource;
	
	@Autowired
	ServletContext context;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;
	
	@ModelAttribute("statusList")
	public List<Status> getStatusList() {
		return Arrays.asList(Status.values());
	}
	
	@RequestMapping(path = "/groupCodeList", method = RequestMethod.GET)
	public String listGroupCode(Model model) {
		return "groupCodeList";
	}
	
	@RequestMapping(path = "/groupCodeListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<GroupCode>> groupCodeListData(TableDataInput input) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
			TableData<GroupCode> data = new TableData<GroupCode>(groupCodeService.findGroupCodesForTenant(SecurityLibrary.getLoggedInUserTenantId(), input));
			data.setDraw(input.getDraw());
			long totalFilterCount = groupCodeService.findTotalFilteredGroupCodesForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = groupCodeService.findTotalGroupCodesForTenant(SecurityLibrary.getLoggedInUserTenantId());
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<GroupCode>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Group Code list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Group Code list : " + e.getMessage());
			return new ResponseEntity<TableData<GroupCode>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(path = "/groupCode", method = RequestMethod.GET)
	public ModelAndView createGroupCode(@ModelAttribute GroupCode groupCode, Model model) {
		model.addAttribute("Buyer", buyerService.findAllBuyers());
		model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("groupCode", "groupCode", new GroupCode());
	}
	
	@RequestMapping(path = "/saveGroupCode", method = RequestMethod.POST)
	public ModelAndView saveGroupCode(@ModelAttribute GroupCode groupCode, BindingResult result, Model model, RedirectAttributes redir) {
		List<String> errMessages = new ArrayList<String>();
		try {
			LOG.info("Group Code SAve called");
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				LOG.info("error..");
				model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
				model.addAttribute("errors", errMessages);
				model.addAttribute("Buyer", buyerService.findAllBuyers());
				return new ModelAndView("costCenter", "costCenter", new CostCenter());
			} else {
				if (doValidate(groupCode)) {
					LOG.info("Buyer In Group Code" + SecurityLibrary.getLoggedInUser().getBuyer());
					if (StringUtils.checkString(groupCode.getId()).length() == 0) {
						groupCode.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
						groupCode.setCreatedBy(SecurityLibrary.getLoggedInUser());
						groupCode.setCreatedDate(new Date());
						groupCodeService.saveGroupCode(groupCode);
						redir.addFlashAttribute("success", messageSource.getMessage("groupCode.save.success", new Object[] { groupCode.getGroupCode() }, Global.LOCALE));
					} else {
						GroupCode persistObj = groupCodeService.getGroupCodeById(groupCode.getId());
						LOG.info("Group Code :  " + persistObj);
						persistObj.setGroupCode(groupCode.getGroupCode());
						persistObj.setDescription(groupCode.getDescription());
						persistObj.setStatus(groupCode.getStatus());
						persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
						persistObj.setModifiedDate(new Date());
						groupCodeService.updateGroupCode(persistObj);
						redir.addFlashAttribute("success", messageSource.getMessage("groupCode.update.success", new Object[] { groupCode.getGroupCode() }, Global.LOCALE));
						LOG.info("Group Code :  " + persistObj);
					}
				} else {
					model.addAttribute("errors", messageSource.getMessage("groupCode.error.duplicate", new Object[] { groupCode.getGroupCode() }, Global.LOCALE));
					model.addAttribute("btnValue", messageSource.getMessage("application.create", new Object[] {}, LocaleContextHolder.getLocale()));
					model.addAttribute("Buyer", buyerService.findAllBuyers());
					return new ModelAndView("groupCode", "groupCode", groupCode);
				}
			}
		} catch (Exception e) {
			LOG.error("Error While saving the Group Code" + e.getMessage(), e);
			model.addAttribute("errors", messageSource.getMessage("groupCode.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			model.addAttribute("Buyer", buyerService.findAllBuyers());
			return new ModelAndView("groupCode", "groupCode", groupCode);
		}
		return new ModelAndView("redirect:groupCodeList");
	}
	
	private boolean doValidate(GroupCode groupCode) {
		boolean validate = true;
		if (groupCodeService.isExists(groupCode, SecurityLibrary.getLoggedInUserTenantId())) {
			LOG.info("inside validation");
			validate = false;
		}
		return validate;
	}
	
	@RequestMapping(path = "/editGroupCode", method = RequestMethod.GET)
	public ModelAndView editGroupCode(@RequestParam String id, Model model) {
		LOG.info("Edit Group Code Called  " + id);
		GroupCode groupCode = groupCodeService.getGroupCodeById(id);
		model.addAttribute("btnValue", messageSource.getMessage("application.update", new Object[] {}, LocaleContextHolder.getLocale()));
		return new ModelAndView("groupCode", "groupCode", groupCode);
	}

	@RequestMapping(path = "/groupCodeCsv", method = RequestMethod.GET)
	public void downloadGroupCodeListCsv(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			File file = File.createTempFile("GroupCode", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());
			
			groupCodeService.downloadGroupCodeCsvFile(response, file,  SecurityLibrary.getLoggedInUserTenantId());
			
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Group Code is successfully downloaded" , SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.GroupCode);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=GroupCode.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();
		} catch (Exception e) {
			LOG.error("Error while downloading GroupCode template :: " + e.getMessage(), e);
		}
	}
	
	@RequestMapping(path = "/downloadGCTemplate", method = RequestMethod.GET)
	public void downloadGroupCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "GroupCodeTemplate.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			workbook = groupCodeService.templateDownloader(workbook);

			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");

			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.getOutputStream().flush();
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage());
				}
			}
		} catch (Exception e) {
			LOG.error("Error while downloading Group Code Template :: " + e.getMessage(), e);
		}
	}
	
	@RequestMapping(path = "/uploadGroupCode", method = RequestMethod.POST)
	public ResponseEntity<String> uploadGroupCodeExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		try {
			validateUploadGroupCode(file);
			LOG.info("uploadGroupCodeExcel method called" + file.getOriginalFilename());
			groupCodeService.groupCodeUploadFile(file, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser());
			headers.add("success", "Group Code list uploaded successfully");
			return new ResponseEntity<String>(headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while Uploading Group Code  template :: " + e.getMessage(), e);
			headers.add("error", e.getMessage());
			return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	public void validateUploadGroupCode(MultipartFile file) {
		LOG.info("++++++++++++file.getContentType()++++++++++++++" + file.getContentType());
		if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
			throw new MultipartException("Only excel files accepted!");
	}

	@RequestMapping(path = "/deleteGroupCode", method = RequestMethod.GET)
	public String deleteCostCenter(@RequestParam String id, Model model, RedirectAttributes redir) {
		LOG.info("Delete Group Code Called");
		GroupCode groupCode = groupCodeService.getGroupCodeById(id);
		try {
			groupCode.setModifiedBy(SecurityLibrary.getLoggedInUser());
			groupCode.setModifiedDate(new Date());
			groupCodeService.deleteGroupCode(groupCode);
//			model.addAttribute("success", messageSource.getMessage("groupCode.success.delete", new Object[] { groupCode.getGroupCode() }, Global.LOCALE));
			redir.addFlashAttribute("success", messageSource.getMessage("groupCode.success.delete", new Object[] { groupCode.getGroupCode() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while deleting Group Code  " + e.getMessage(), e);
//			model.addAttribute("errors", messageSource.getMessage("groupCode.error.delete", new Object[] { groupCode.getGroupCode()}, Global.LOCALE));
			redir.addFlashAttribute("errors", messageSource.getMessage("groupCode.error.delete", new Object[] { groupCode.getGroupCode()}, Global.LOCALE));
			return "redirect:groupCodeList";
		}
		return "redirect:groupCodeList";
	}
	
}
