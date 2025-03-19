package com.privasia.procurehere.web.config;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.privasia.procurehere.core.exceptions.SecurityRuntimeException;
import com.privasia.procurehere.core.utils.SecurityLibrary;

/**
 * @author Sarang
 * @author Madhuri
 */
@Configuration
public class ApplicationLocaleResolver extends SessionLocaleResolver {

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		try {
			return Locale.forLanguageTag(SecurityLibrary.getLoggedInUserLangCode());
		} catch (SecurityRuntimeException e) {
			return Locale.ENGLISH;
		}
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		super.setLocale(request, response, locale);
	}

}
