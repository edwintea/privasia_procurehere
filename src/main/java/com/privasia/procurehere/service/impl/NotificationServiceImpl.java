/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.NotificationService;

import freemarker.template.Configuration;

/**
 * @author Arc
 */
@Service("smtpNotificationService")
public class NotificationServiceImpl implements NotificationService {

	private static final Logger LOG = LogManager.getLogger(NotificationServiceImpl.class);

	@Autowired(required = true)
	MailSender simpleMailSender;

	@Autowired(required = true)
	JavaMailSenderImpl mailSender;
	
	@Autowired
	Configuration freemarkerConfiguration;

	/*
	 * (non-Javadoc)
	 * @see com.privasia.ipms.service.NotificationService#sendEmail(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void sendEmail(String mailto, String subject, String message) throws Exception {
		try {
			MimeMessage mimeMessag = mailSender.createMimeMessage();
			try {
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessag, true);
				mailto = mailto.replaceAll(" ", "");
				String[] mailList = null;
				if (mailto != null && (mailto.contains(",") || mailto.contains(";")))
					mailList = StringUtils.checkString(mailto).split("\\,|\\;");

				if (mailList != null)
					helper.setTo(mailList);
				else
					helper.setTo(mailto);

				helper.setFrom(mailSender.getUsername());
				helper.setSubject(subject);
				helper.setText(message, true);
				mailSender.send(mimeMessag);

			} catch (MailSendException e) {
				e.printStackTrace();
				Exception[] exceptions = e.getMessageExceptions();
				if (exceptions != null) {
					reSendFaildEmails(exceptions, subject, message);
				}

			}
		} catch (Exception e) {
			LOG.error("Error sending email -> " + e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 
	 */
	private void reSendFaildEmails(Exception[] exceptions, String subject, String message) throws Exception {

		MimeMessage mimeMessag = null;
		MimeMessageHelper helper = null;
		String notValid = "";
		String valid = "";
		for (Exception exception : exceptions) {
			if (exception instanceof SendFailedException) {
				SendFailedException sfex = (SendFailedException) exception;
				Address[] invalid = sfex.getInvalidAddresses();
				if (invalid != null) {
					if (invalid != null) {
						for (int i = 0; i < invalid.length; i++)
							notValid = notValid + invalid[i] + ",";
					}
				}
				if (notValid != null && notValid.length() > 0)
					notValid = notValid.substring(0, notValid.length() - 1);
				LOG.error("Not Valid Mail Ids : " + notValid);

				Address[] validUnsent = sfex.getValidUnsentAddresses();
				if (validUnsent != null) {
					if (validUnsent != null) {
						for (int i = 0; i < validUnsent.length; i++)
							valid = valid + validUnsent[i] + ",";
					}
				}
				if (valid != null && valid.length() > 0) {
					valid = valid.substring(0, valid.length() - 1);
					LOG.info("Valid Mail Ids : " + valid);

					try {
						mimeMessag = mailSender.createMimeMessage();
						helper = new MimeMessageHelper(mimeMessag, true);
						helper.setFrom(mailSender.getUsername());
						helper.setSubject(subject);
						helper.setText(message, true);
						String[] mailList = null;
						if (valid != null && (valid.contains(",") || valid.contains(";")))
							mailList = StringUtils.checkString(valid).split("\\,|\\;");
						if (mailList != null && mailList.length > 0)
							helper.setTo(mailList);
						else
							helper.setTo(valid);
						mailSender.send(mimeMessag);
					} catch (Exception ex) {
						LOG.error("Error re-sending email : " + ex.getMessage(), ex);
					}
				}

			}

			if (notValid != null && notValid.length() > 0) {
				throw new Exception("Error occured while sending email to this Email Ids :- " + notValid);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * @see com.privasia.ipms.service.NotificationService#sendPlainTextEmail(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void sendPlainTextEmail(String mailto, String subject, String message) throws Exception {
		try {

			SimpleMailMessage msg = new SimpleMailMessage();
			try {
				String[] mailList = null;
				if (mailto != null && (mailto.contains(",") || mailto.contains(";")))
					mailList = StringUtils.checkString(mailto).split("\\,|\\;");

				if (mailList != null)
					msg.setTo(mailList);
				else
					msg.setTo(mailto);

				msg.setSubject(subject);
				msg.setText(message);
/*				System.out.println("sab kuch set kiya..." + mailSender.getHost() +" : "+ mailSender.getPort() +" : "+ mailSender.getUsername() +" -----  ");
				
				Session session=  mailSender.getSession();
				session.setDebug(true);
				*/
				mailSender.send(msg);
			} catch (MailSendException e) {
				e.printStackTrace();
				Exception[] exceptions = e.getMessageExceptions();
				if (exceptions != null) {
					reSendFailedPlainTextEmails(exceptions, subject, message);
				}

			}

		} catch (Exception e) {
			LOG.error("Error ->", e);
		}
	}

	private void reSendFailedPlainTextEmails(Exception[] exceptions, String subject, String message) {
		String notValid = "";
		String valid = "";

		for (Exception exception : exceptions) {
			if (exception instanceof SendFailedException) {
				SendFailedException sfex = (SendFailedException) exception;
				Address[] invalid = sfex.getInvalidAddresses();
				if (invalid != null) {
					if (invalid != null) {
						for (int i = 0; i < invalid.length; i++)
							notValid = notValid + invalid[i] + ",";
					}
				}

				if (notValid != null && notValid.length() > 0) {
					notValid = notValid.substring(0, notValid.length() - 1);
					LOG.info("Not Valid Mail Ids : " + notValid);
				}

				Address[] validUnsent = sfex.getValidUnsentAddresses();
				if (validUnsent != null) {
					if (validUnsent != null) {
						for (int i = 0; i < validUnsent.length; i++)
							valid = valid + validUnsent[i] + ",";
					}
				}
				if (valid != null && valid.length() > 0) {
					valid = valid.substring(0, valid.length() - 1);
					LOG.info("Valid Mail Ids : " + valid);

					try {
						SimpleMailMessage msg = new SimpleMailMessage();
						msg.setFrom(mailSender.getUsername());
						msg.setSubject(subject);
						msg.setText(message);
						String[] mailList = null;
						if (valid != null && (valid.contains(",") || valid.contains(";")))
							mailList = StringUtils.checkString(valid).split("\\,|\\;");
						if (mailList != null && mailList.length > 0)
							msg.setTo(mailList);
						else
							msg.setTo(valid);
						mailSender.send(msg);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			}

			if (notValid != null && notValid.length() > 0) {
				LOG.error("---------------------------------------------------------------------");
				LOG.error("Error occured while sending email to this Email Ids :- " + notValid);
				LOG.error("---------------------------------------------------------------------");
			}
		}

	}

	@Override
	public void sendEmailWithAttachment(String mailto, String subject, String message, File file, String attachedFileName) throws ApplicationException {

		MimeMessage mimeMessag = mailSender.createMimeMessage();
		try {
			mailto = mailto.replaceAll(" ", "");
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessag, true);
			String[] mailList = null;
			if (mailto != null && (mailto.contains(";") || mailto.contains(";")))
				mailList = StringUtils.checkString(mailto).split("\\;|\\,");

			if (mailList != null)
				helper.setTo(mailList);
			else
				helper.setTo(mailto);

			helper.setFrom(mailSender.getUsername());
			helper.setSubject(subject);
			helper.setText(message, true);

			if (file != null) {
				helper.addAttachment(attachedFileName, file);
			}
			mailSender.send(mimeMessag);

		} catch (MessagingException e) {
			LOG.error("Mail Notification error : " + e.getMessage(), e);
			throw new ApplicationException("Error occured while sending email :- " + e.getMessage());
		} catch (MailSendException e) {
			LOG.error("Mail Notification error : " + e.getMessage(), e);
			if (e.getRootCause() != null && e.getRootCause().getMessage().contains("Connection refused")) {
				LOG.error("Error sending email message : " + e.getMessage(), e);
				throw new ApplicationException("Connection error occured while sending email :- " + e.getRootCause().getMessage());
			}
			throw new ApplicationException("Error occured while sending email :- " + e.getMessage());

		} catch (Exception e) {
			LOG.error("Mail Notification error : " + e.getMessage(), e);
			if (e instanceof ApplicationException) {
				throw (ApplicationException) e;
			} else {
				LOG.error("Error sending email message for billing pdf : " + e.getMessage(), e);
				throw new ApplicationException(e.getMessage());
			}
		}
	}
	
	@Override
	public void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				LOG.info("Sending request email to : " + mailTo);
				String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(template), map);
				sendEmail(mailTo, subject, message);
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	@Override
	public void pushOneSignalNotification(String message, String messageUrl, Map<String, String> payload, List<String> targetDeviceIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEmailWithBcc(String mailTo, String mailBcc, String subject, Map<String, Object> map, String template) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEmailWithcc(String mailTo, String mailBcc, String subject, String message) throws Exception {
		// TODO Auto-generated method stub
		
	}


}
