/**
 * 
 */
package com.privasia.procurehere.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.event.LoggerListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.web.filters.ChangePasswordFilter;

/**
 * @author Nitin Otageri
 */
@Configuration
@Order(1)
public class PasswordEncoderConfig {

	@Bean
	protected BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	public LoggerListener loggerListener() {
		LoggerListener listener = new LoggerListener();
		return listener;
	}
	
	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public ChangePasswordFilter changePasswordFilter() {
		ChangePasswordFilter filter = new ChangePasswordFilter();
		filter.setChangePasswordUrl("/forceChangePassword");
		return filter;
	}


}
