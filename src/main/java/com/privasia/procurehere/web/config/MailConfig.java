/**
 * 
 */
package com.privasia.procurehere.web.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author arc
 */
@Configuration
@PropertySource(value = { "classpath:application.properties" })
public class MailConfig {

	@Autowired
	private Environment env;

	@Bean
	public JavaMailSender getMailSender() {
		Properties javaMailProperties = new Properties();
		javaMailProperties.setProperty("mail.smtp.auth", "true");
		javaMailProperties.setProperty("mail.user", env.getRequiredProperty("email.user"));
		javaMailProperties.setProperty("mail.from", env.getRequiredProperty("email.user"));
		javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
		javaMailProperties.setProperty("mail.smtp.ssl.trust", "*");

		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(env.getRequiredProperty("email.host"));
		javaMailSender.setPort(Integer.parseInt(env.getRequiredProperty("email.port")));
		javaMailSender.setUsername(env.getRequiredProperty("email.user"));
		javaMailSender.setPassword(env.getRequiredProperty("email.password"));
		javaMailSender.setProtocol("smtp");
		javaMailSender.setJavaMailProperties(javaMailProperties);
		return javaMailSender;
	}

	@Bean
	public SimpleMailMessage getSimpleMailSender() {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(env.getRequiredProperty("email.user"));

		// simpleMailMessage.setSubject(subject);
		return simpleMailMessage;
	}
}
