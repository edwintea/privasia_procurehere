package com.privasia.procurehere.web.security.xss;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.AntPathMatcher;

import com.privasia.procurehere.web.filters.RecaptchaResponseFilter;

/**
 * @author Yogesh
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {

	private static final Logger LOG = LogManager.getLogger(RecaptchaResponseFilter.class);

	private final static String[] escapeSearchList = { "\0" };
	private final static String[] escapeReplacementList = { "" };

	private static final Pattern[] patterns = new Pattern[] {
			// Script fragments
			Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
			// src='...'
			Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL), Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// lonely script tags
			Pattern.compile("</script>", Pattern.CASE_INSENSITIVE), Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// eval(...)
			Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// expression(...)
			Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// javascript:...
			Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
			// vbscript:...
			Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
			// onload(...)=...
			Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL) };

	public XSSRequestWrapper(HttpServletRequest servletRequest) {
		super(servletRequest);
	}

	@Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);

		if (values == null) {
			return null;
		}

		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = stripXSS(values[i]);
		}

		return encodedValues;
	}

	@Override
	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		return stripXSS(value);
	}

	@Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		return stripXSS(value);
	}

	private String stripXSS(String value) {
		if (value != null) {
			// NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
			// avoid encoded attacks.
			// value = ESAPI.encoder().canonicalize(value);

			// Avoid null characters
			// this is run faster than string.replaceAll() function
			value = StringUtils.replaceEach(value, escapeSearchList, escapeReplacementList);
			// value = value.replaceAll("\0", "");

			String path = super.getRequestURI();

			AntPathMatcher pmatcher = new AntPathMatcher();
			boolean declarationPathMatch = pmatcher.match("/**/saveDeclaration", path);
			boolean announcementPathMatch = pmatcher.match("/**/createAnnouncement", path);
			Pattern pattern1 = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			Pattern pattern2 = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

			// Remove all sections that match a pattern
			for (Pattern scriptPattern : patterns) {
				if (declarationPathMatch || announcementPathMatch) {
					boolean match1 = scriptPattern.pattern().equals(pattern1.pattern());
					boolean match2 = scriptPattern.pattern().equals(pattern2.pattern());
					if (match1 || match2) {
						value = value;
					} else {
						value = scriptPattern.matcher(value).replaceAll("");
						value = decodeHtmlEntities(value);
					}
				} else {
					value = scriptPattern.matcher(value).replaceAll("");
					value = decodeHtmlEntities(value);
				}
			}

		}
		return value;
	}

	private static String decodeHtmlEntities(String input) {
		return input.replace("‘", "'").replace("’", "'").replace("“", "\"").replace("”", "\"");
	}


}
