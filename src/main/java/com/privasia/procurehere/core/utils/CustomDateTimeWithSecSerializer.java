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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author kapil
 * 
 * NOTE :- This Serializer For some place we need date in sec format
 * 			Exp:- auction bids for buyer
 */
@Component
public class CustomDateTimeWithSecSerializer extends StdSerializer<Date> {

	private static final long serialVersionUID = -3579456964766125334L;

	private static final Logger LOG = LogManager.getLogger(CustomDateTimeWithSecSerializer.class);

	@Autowired
	HttpSession httpSession;

	private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
	
	//private SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");

	public CustomDateTimeWithSecSerializer() {
		this(null);
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CustomDateTimeWithSecSerializer(Class t) {
		super(t);
	}

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {
		try {
			if (value != null) {
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
				gen.writeString(formatter.format(value));
			}
		} catch (Exception e) {
			LOG.error("Error during JSON date formatting : " + e.getMessage(), e);
		}
	}
}
