package com.privasia.procurehere.web.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.PasswordHistoryDao;
import com.privasia.procurehere.core.entity.PasswordHistory;
import com.privasia.procurehere.core.entity.PasswordSettings;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.pojo.PasswordPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.PasswordSettingService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.ImageResize;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Javed Ahmed
 */
@Controller
@RequestMapping(value = "/")
public class ProfileSettingController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	UserService userService;

	@Resource
	MessageSource messageSource;

	@Autowired
	PasswordHistoryDao passwordHistoryDao;

	@Autowired
	PasswordSettingService passwordSettingService;

	@RequestMapping(path = "/profileSetting", method = RequestMethod.GET)
	public ModelAndView profileSetting(Model model) throws IOException {
		LOG.info("Details of logged in user  :::" + userService.getDetailsOfLoggedinUser(SecurityLibrary.getLoggedInUser().getLoginId()));

		User userObj = userService.getDetailsOfLoggedinUser(SecurityLibrary.getLoggedInUser().getLoginId());

//		LOG.info("inside profile setting" + userObj.toString());
		if (userService.getCurrentLoggedInUser().getBuyer() != null) {
			LOG.info("inside PS BUYER ::" + userObj.getBuyer().getCompanyName());
			model.addAttribute("buyerOrSupp", userObj.getBuyer());
			model.addAttribute("dashboard", "buyer/buyerDashboard");
		} else if (userService.getCurrentLoggedInUser().getSupplier() != null) {
			model.addAttribute("buyerOrSupp", userObj.getSupplier());
			model.addAttribute("dashboard", "supplier/supplierDashboard");
			LOG.info("inside PS SUPP ::" + userObj.getSupplier().getCompanyName());
		} else if (userService.getCurrentLoggedInUser().getBuyer() == null && userService.getCurrentLoggedInUser().getSupplier() == null) {
			model.addAttribute("dashboard", "owner/ownerDashboard");

		}

		try {
			byte[] encodeBase64 = Base64.encodeBase64(userObj.getPrfPicAttatchment());
			String base64Encoded = new String(encodeBase64, "UTF-8");
			model.addAttribute("profPic", base64Encoded);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		model.addAttribute("passwordObj", new PasswordPojo());
		model.addAttribute("regex", passwordSettingService.getPasswordRegex(SecurityLibrary.getLoggedInUserTenantId()));
		return new ModelAndView("profileSetting", "userObj", userObj);
	}

	@RequestMapping(path = "/profileSetting", method = RequestMethod.POST)
	public ModelAndView saveProfileSetting(@Valid @ModelAttribute User user, BindingResult result, HttpServletRequest request, User persistUser, @RequestParam(value = "prfPic", required = false) MultipartFile prfPic, Model model) throws IOException {
		LOG.info("inside profileSetting save and User Profile pic  size " + prfPic != null ? prfPic.getSize() : "");
		List<String> errMessages = new ArrayList<String>();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
				}
				LOG.info("error..");
				model.addAttribute("error", errMessages);
				return new ModelAndView("profileSetting", "userObj", user);
			} else {

//				LOG.info("inside save PS img name :::" + prfPic.getOriginalFilename());
				persistUser = userService.getDetailsOfLoggedinUser(SecurityLibrary.getLoggedInUser().getLoginId());
				LOG.info("inside profileSetting save and User id " + persistUser.getId());
				// send a new file type in argument
				// ImageResize.imageResize(null);

				if (!prfPic.isEmpty()) {
					//validate profile pic file extension
					String fileExtension = FilenameUtils.getExtension(prfPic.getOriginalFilename());
					if(isNotAllowFileExtension(fileExtension)){
						errMessages.add("Invalid file format. Only JPG, PNG, and JPEG files are allowed.");
						model.addAttribute("error", errMessages);
						return new ModelAndView("redirect:profileSetting");
					}
					// convert byte array to BufferedImage
					InputStream in = new ByteArrayInputStream(prfPic.getBytes());
					BufferedImage bImageFromConvert = ImageIO.read(in);

					BufferedImage finalImage = ImageResize.imageResize(bImageFromConvert);
					// ImageIO.write(finalImage, "jpg", new File("/home/javed/profile_img_toChange.jpg"));

					// for image
					persistUser.setPrfPicName(prfPic.getOriginalFilename());
					persistUser.setPrfPiccontentType(prfPic.getContentType());
					LOG.info("prfPic.getBytes()  " + prfPic.getBytes());
					persistUser.setPrfPicAttatchment(prfPic.getBytes());

					LOG.info("after converting byte into jpg");
				}

				persistUser.setName(user.getName());
				persistUser.setDesignation(user.getDesignation());
				persistUser.setPhoneNumber(user.getPhoneNumber());
				persistUser.setCommunicationEmail(user.getCommunicationEmail());
				persistUser.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
				persistUser = userService.updateUser(persistUser);
				TenantType tenantType=persistUser.getTenantType();
				
				switch (tenantType) {
				case BUYER:
					model.addAttribute("success", messageSource.getMessage("profilesetting.update.buyer.profile.msg", new Object[] {}, Global.LOCALE));
					break;
				case SUPPLIER:
					model.addAttribute("success", messageSource.getMessage("profilesetting.update.supplier.profile.msg", new Object[] {}, Global.LOCALE));
					break;
				case OWNER:
					model.addAttribute("success", messageSource.getMessage("profilesetting.update.owner.profile.msg", new Object[] {}, Global.LOCALE));
					break;
   				case FINANCE_COMPANY:
					model.addAttribute("success", messageSource.getMessage("profilesetting.update.financecompany.profile.msg", new Object[] {}, Global.LOCALE));
					break;
				default:
					model.addAttribute("success", messageSource.getMessage("profilesetting.update.profile.msg", new Object[] {}, Global.LOCALE));
					break;
				}
//				if(tenantType==TenantType.BUYER) {
//				   model.addAttribute("success",messageSource.getMessage("profilesetting.update.profile.msg",new Object[] {},Global.LOCALE));
//				}
				
//				request.getSession(true).setAttribute(Global.SESSION_PROFILE_PICTURE_KEY, persistUser.getProfilePicture());
				LOG.info("profile pic Controller " + persistUser.getPrfPicAttatchment());
				LOG.info("inside save PS" + persistUser.toString());
				// String pic = (String) request.getSession(true).getAttribute(Global.SESSION_PROFILE_PICTURE_KEY);
				// if (pic == null) {
				// LOG.info("Setting profile pic into session : " +
				// SecurityLibrary.getLoggedInUserProfilePicture());

				// }
				/*
				 * byte[] encodeBase64 = Base64.encodeBase64(persistUser.getPrfPicAttatchment()); String base64Encoded =
				 * new String(encodeBase64, "UTF-8"); model.addAttribute("profPic", base64Encoded);
				 */
				return new ModelAndView("redirect:profileSetting");
			}
		} catch (Exception e) {
			LOG.error("Error While saving the state" + e.getMessage(), e);
			return new ModelAndView("redirect:profileSetting");

		}
	}

	public boolean isNotAllowFileExtension(String fileExtension) {
		if (StringUtils.isNotBlank(fileExtension)) {
			fileExtension = fileExtension.trim().toUpperCase();
			return !"JPG JPEG PNG".contains(fileExtension);
		}
		return false;
	}

	@RequestMapping(path = "/changeUserPassword", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PasswordPojo> changeUserPassword(@RequestBody PasswordPojo password) throws JsonProcessingException {
		LOG.info("Change Password POST Called in ajax :::" + password.toLogString());
		// List<HttpHeaders> headers = new ArrayList<HttpHeaders>();

		List<String> errors = new ArrayList<String>();
		HttpHeaders headers = new HttpHeaders();

		try {
			PasswordSettings passwordSetting = passwordSettingService.getPasswordRegex(SecurityLibrary.getLoggedInUserTenantId());
			String regex = null;
			if (passwordSetting != null) {
				regex = passwordSetting.getRegx();
			}

			LOG.info("validatePassword");
			if (!password.getNewPassword().equals(password.getConfirmPassword())) {
				// new and confirm do not match
				LOG.info("new password and confirm password mismatch");
				errors.add(messageSource.getMessage("user.changepassword.equalpasswords", new Object[] {}, Global.LOCALE));
			}
			if (!StringUtils.validatePasswordWithRegx(password.getNewPassword(), regex)) {
				// weak password...
				LOG.info("Password Weak");
				/* errors.add(messageSource.getMessage("user.password.week", new Object[] {}, Global.LOCALE)); */
			}

			if (errors.size() > 0) {
				headers.put("error", errors);
				return new ResponseEntity<PasswordPojo>(headers, HttpStatus.BAD_REQUEST);
			}

			BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
			User dbUser = validatePassword(password, errors, enc, Boolean.FALSE);

			// UPDATE PASSWORD INTO DB
			if (CollectionUtil.isEmpty(errors)) {
				// All good.
				LOG.info("password save");
				String newPassword = enc.encode(password.getNewPassword());
				dbUser.setLastPasswordChangedDate(new Date());
				dbUser.setPassword(newPassword);
				userService.updateUserPlain(dbUser);

				if (Global.PASSWORD_HISTORY_ENABLED) {
					PasswordHistory passwordHistory = new PasswordHistory();
					passwordHistory.setPassword(dbUser.getPassword());
					LOG.info("new Password Object get    :   " + dbUser.getPassword());
					passwordHistory.setPasswordChangedDate(new Date());
					passwordHistory.setUser(dbUser);
					passwordHistoryDao.save(passwordHistory);
				}

				/*
				 * UPDATE THE SECURITY CONTEXT AS THE PASSWORD IS NOW CHANGED.
				 */
				// gonna need this to get user from Acegi
				SecurityContext ctx = SecurityContextHolder.getContext();
				Authentication auth = ctx.getAuthentication();
				// get user obj
				AuthenticatedUser authUser = (AuthenticatedUser) auth.getPrincipal();
				// update the password on the user obj
				authUser.setPassword(dbUser.getPassword());
				authUser.setPasswordExpired(false);
				// Tell Acegi about the changes: update the
				// SecurityContextHolder
				// (see
				// org.acegisecurity.providers.dao.AbstractUserDetailsAuthenticationProvider.createSuccessAuthentication())
				UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(authUser, auth.getCredentials(), authUser.getAuthorities());
				upat.setDetails(auth.getDetails());
				ctx.setAuthentication(upat);

			}

		} catch (ApplicationException e) {
			LOG.error("Error While Save the User" + e.getMessage(), e);
			errors.add(e.getMessage());
			headers.add("error", e.getMessage());
			return new ResponseEntity<PasswordPojo>(null, headers, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("Error While Save the User" + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("user.password.change", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<PasswordPojo>(null, headers, HttpStatus.BAD_REQUEST);
		}
		// model.addAttribute("error", errMessages);
		if (CollectionUtil.isEmpty(errors)) {
			headers.add("success", "Password changed successfully");
			return new ResponseEntity<PasswordPojo>(null, headers, HttpStatus.OK);
		}
		headers.add("success", "Password changed successfully");
		return new ResponseEntity<PasswordPojo>(null, headers, HttpStatus.OK);
	}

	/**
	 * @param password
	 * @param errMessages
	 * @param enc
	 * @param bypassOldPasswordValidation
	 * @return
	 * @throws ApplicationException
	 * @throws NoSuchMessageException
	 */
	private User validatePassword(PasswordPojo password, List<String> errMessages, BCryptPasswordEncoder enc, Boolean bypassOldPasswordValidation) throws NoSuchMessageException, ApplicationException {
		LOG.info("validatePassword");

		User dbUser = userService.getUserByLoginIdNoTouch((Boolean.TRUE == bypassOldPasswordValidation ? password.getLoginEmail() : SecurityLibrary.getLoggedInUser().getLoginId()));
		LOG.info("validatePassword" + dbUser);
		if (dbUser == null) {
			LOG.info("dbUser == null)");
			errMessages.add(messageSource.getMessage("user.error.login.email", new Object[] {}, Global.LOCALE));
			return dbUser;
		}

		LOG.info("OLD PASSWORD : " + password.getOldPassword());
		if (Boolean.FALSE == bypassOldPasswordValidation && !enc.matches(password.getOldPassword(), dbUser.getPassword())) {
			// old password does not match
			LOG.info("Password old password wrong");
			errMessages.add(messageSource.getMessage("user.changepassword.old.password.wrong", new Object[] {}, Global.LOCALE));
			throw new ApplicationException(messageSource.getMessage("user.changepassword.old.password.wrong", new Object[] {}, Global.LOCALE));
		}

		if (CollectionUtil.isEmpty(errMessages)) {
			LOG.info("CollectionUtil.isEmpty(errMessages");
			PasswordSettings passwordSetting = passwordSettingService.getPasswordSettingsByTenantId(dbUser.getTenantId());
			boolean enablePasswordReuse = false;
			int numberOfPasswordRemember = Global.PASSWORD_HISTORY_COUNT;
			if (passwordSetting != null) {
				enablePasswordReuse = passwordSetting.getEnableReusePassword() != null ? (!passwordSetting.getEnableReusePassword()) : false;
				numberOfPasswordRemember = passwordSetting.getNumberOfPasswordRemember() != null ? passwordSetting.getNumberOfPasswordRemember() : Global.PASSWORD_HISTORY_COUNT;
			}
			if (enablePasswordReuse) {
				List<PasswordHistory> pasList = new ArrayList<>();
				List<PasswordHistory> passwordHistories = passwordHistoryDao.findPasswordHistoryByUserId(dbUser.getId());
				if (passwordHistories != null && passwordHistories.size() > 0) {
					int count = 1;
					for (PasswordHistory passwordHistory : passwordHistories) {
						if (count <= numberOfPasswordRemember && enc.matches(password.getNewPassword(), passwordHistory.getPassword())) {
							LOG.info("Password matches with history..." + password.getNewPassword());
							errMessages.add(messageSource.getMessage("user.changepassword.password.exist.inhistory", new Object[] {}, Global.LOCALE));
							throw new ApplicationException(messageSource.getMessage("user.changepassword.password.exist.inhistory", new Object[] {}, Global.LOCALE));
						}
						count++;
						/**
						 * If the history count greater than 6 system will identify those records for deletion
						 **/
						if (count > numberOfPasswordRemember)
							pasList.add(passwordHistory);
					}

					if (!pasList.isEmpty() && pasList.size() > 0) {
						for (PasswordHistory pasHistory : pasList) {
							passwordHistoryDao.delete(pasHistory);
							LOG.info("Deleteed from history : " + pasHistory.getPassword());
							LOG.info("Password history deleted for : " + dbUser.getLoginId());
						}
					}
				}
			}
		}
		LOG.info("dbuser :" + dbUser);
		return dbUser;
	}

}