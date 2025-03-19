/**
 * 
 */
package com.privasia.procurehere.core.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author Nitin Otageri
 */
@Component
public class CustomDateDeserializer extends StdDeserializer<Date> {

	private static final long serialVersionUID = -3981406392594419421L;

	private static final Logger LOG = LogManager.getLogger(CustomDateDeserializer.class);

	@Autowired
	HttpSession httpSession;

	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	public CustomDateDeserializer() {
		this(null);
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	@SuppressWarnings("rawtypes")
	public CustomDateDeserializer(Class t) {
		super(t);
	}

	@Override
	public Date deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String value = p.getText();
		try {
			if (StringUtils.checkString(value).length() > 0) {
				String timeZone = "GMT+8:00";
				if (httpSession != null) {
					timeZone = (String) httpSession.getAttribute(Global.SESSION_TIME_ZONE_KEY);
					if (timeZone == null) {
						timeZone = "GMT+8:00";
					}
					if (LOG.isDebugEnabled()) {
						LOG.debug("Time Zone in formatter : " + timeZone + ", Date : " + value + " TZ : " + TimeZone.getTimeZone(timeZone));
					}
				}

				formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
				return formatter.parse(value);
			}
		} catch (Exception e) {
			LOG.error("Error during JSON date formatting : " + e.getMessage(), e);
		}
		// some error or no value
		return null;
	}

}
