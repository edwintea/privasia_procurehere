/**
 * 
 */
package com.privasia.procurehere.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.privasia.procurehere.core.exceptions.ApplicationException;

/**
 * @author Arc
 */
public interface NotificationService {
	/**
	 * Send notification by email
	 * 
	 * @param mailto
	 * @param message
	 */
	void sendEmail(String mailto, String subject, String message) throws Exception;

	/**
	 * Send a plain text notification.
	 * 
	 * @param mailto The receiver id. Separated by comma ','.
	 * @param subject The email Subject
	 * @param message The email message
	 * @throws Exception In case of any errors during mail send.
	 */
	void sendPlainTextEmail(String mailto, String subject, String message) throws Exception;

	/**
	 * @param mailto
	 * @param subject
	 * @param message
	 * @param file
	 * @param attachedFileName
	 * @throws ApplicationException
	 */
	void sendEmailWithAttachment(String mailto, String subject, String message, File file, String attachedFileName) throws ApplicationException;

	/**
	 * @param mailTo
	 * @param subject
	 * @param map
	 * @param template
	 */
	void sendEmail(String mailTo, String subject, Map<String, Object> map, String template);

	/**
	 * @param message
	 * @param messageUrl
	 * @param payload
	 * @param isEnglish
	 * @param targetDeviceIds
	 */
	void pushOneSignalNotification(String message, String messageUrl, Map<String, String> payload, List<String> targetDeviceIds);

	/**
	 * @param mailTo
	 * @param mailBcc
	 * @param subject
	 * @param map
	 * @param template
	 */
	void sendEmailWithBcc(String mailTo, String mailBcc, String subject, Map<String, Object> map, String template);

	/**
	 * @param mailTo
	 * @param subject
	 * @param mailBcc
	 * @param message
	 */
	void sendEmailWithcc(String mailTo, String mailBcc, String subject, String message) throws Exception;
}
