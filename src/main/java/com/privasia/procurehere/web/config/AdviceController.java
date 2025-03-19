package com.privasia.procurehere.web.config;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.privasia.procurehere.core.utils.Global;

@ControllerAdvice
public class AdviceController {

	private static final Logger LOG = LogManager.getLogger(Global.ERROR_LOG);

	@ExceptionHandler(NoHandlerFoundException.class)
	public String dealWithNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
		LOG.error("Not found resource : " + e.getRequestURL());
		return "404_error";
	}

	@ExceptionHandler(Exception.class)
	public String handleException(Exception e, HttpServletRequest request) {
		LOG.error("Error resource : " + request.getRequestURL());
		LOG.error("Server Error : " + e.getMessage(), e);
		return "500_error";
	}
}
