/**
 * 
 */
package com.privasia.procurehere.web.validator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;

import com.privasia.procurehere.core.exceptions.RecaptchaServiceException;
import com.privasia.procurehere.core.pojo.RecaptchaForm;
import com.privasia.procurehere.service.RecaptchaService;

/**
 * @author Nitin Otageri
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RecaptchaFormValidator implements Validator {

	private static final String ERROR_RECAPTCHA_INVALID = "recaptcha.error.invalid";
	private final HttpServletRequest httpServletRequest;
	private final RecaptchaService recaptchaService;

	@Autowired
	public RecaptchaFormValidator(HttpServletRequest httpServletRequest, RecaptchaService recaptchaService) {
		this.httpServletRequest = httpServletRequest;
		this.recaptchaService = recaptchaService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return RecaptchaForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RecaptchaForm form = (RecaptchaForm) target;
		try {
			if ((form.getRecaptchaResponse() == null || (form.getRecaptchaResponse() != null && !form.getRecaptchaResponse().isEmpty())) && !recaptchaService.isResponseValid(httpServletRequest.getRemoteAddr(), form.getRecaptchaResponse())) {
				errors.reject(ERROR_RECAPTCHA_INVALID);
				errors.rejectValue("recaptchaResponse", ERROR_RECAPTCHA_INVALID, "The response to captcha was identified as invalid. Try again.");
			}
		} catch (RecaptchaServiceException e) {
			errors.reject("Captcha service is unavailable at this time, try again later.");
		}
	}

}
