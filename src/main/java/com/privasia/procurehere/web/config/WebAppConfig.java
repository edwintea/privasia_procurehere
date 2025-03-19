/**
 * 
 */
package com.privasia.procurehere.web.config;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.jms.DeliveryMode;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.ui.velocity.VelocityEngineFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.enums.converter.BigDecimalConverter;
import com.privasia.procurehere.core.parsers.PrFileParser;
import com.privasia.procurehere.core.utils.HTMLCharacterEscapes;

/**
 * @author Nitin Otageri
 */
@EnableAsync
@EnableWebMvc
@Configuration
@Import(value = { JpaConfig.class, MailConfig.class, SpringRootConfig.class, ApplicationLocaleResolver.class })
@ComponentScan(basePackages = "com.privasia.procurehere", excludeFilters = { @Filter(type = FilterType.REGEX, pattern = "com.privasia.procurehere.core.entity") })
// @EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebAppConfig extends WebMvcConfigurerAdapter {

	
	private static final Logger LOG = LogManager.getLogger(WebAppConfig.class);
	
	@Value("${activemq.broker-url}")
	private String brokerUrl;

	@Value("${activemq.user}")
	private String userName;

	@Value("${activemq.password}")
	private String password;

	@Value("${environment-key}")
	private String environmentKey;

	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		LOG.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + environmentKey);
		
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		if(!(environmentKey == null || (environmentKey != null && environmentKey.equalsIgnoreCase("demo")) || (environmentKey != null && environmentKey.trim().equalsIgnoreCase("")))) {
			registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		}
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// registry.addViewController("/forbidden").setViewName("forbidden");
		registry.addViewController("/login.jsp").setViewName("login");
		registry.addViewController("/").setViewName("login");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/forceChangePassword").setViewName("redirect:/admin/changePassword");
		// registry.addViewController("/expresscheckout").setViewName("/expresscheckout");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		super.addFormatters(registry);
		registry.addConverter(new BigDecimalConverter());
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		TilesViewResolver viewResolver = new TilesViewResolver();
		registry.viewResolver(viewResolver);
	}

	// TODO pr import removed from screen so no use
	@Bean(name = "prFileParser")
	public PrFileParser<PrItem> getPrFileParser() {
		return new PrFileParser<PrItem>(PrItem.class);
	}

	@Bean
	TilesConfigurer tilesConfigurer() {
		TilesConfigurer tilesConfigurer = new TilesConfigurer();
		tilesConfigurer.setDefinitions("WEB-INF/views/jsp/tiles/tiles.xml");
		tilesConfigurer.setCheckRefresh(true);
		tilesConfigurer.setPreparerFactoryClass(org.springframework.web.servlet.view.tiles3.SpringBeanPreparerFactory.class);
		return tilesConfigurer;
	}

	@Bean(name = "messageSource")
	public ResourceBundleMessageSource resourceBundleMessageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("messages", "ValidationMessages", "application", "uicaptions", "uiplaceholder", "uivalidations", "helpDesk", "uicaptions_ms", "uiplaceholder_ms", "uivalidations_ms");
		return messageSource;
	}

	@Bean(name = "validator")
	@Override
	public org.springframework.validation.Validator getValidator() {
		return new LocalValidatorFactoryBean();
	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver getCommonsMultipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(1073741824); // 1 GB
		multipartResolver.setMaxInMemorySize(1048576); // 1MB
		return multipartResolver;
	}

	/*
	 * Velocity configuration.
	 */
	@Bean
	public VelocityEngine getVelocityEngine() throws VelocityException, IOException {
		VelocityEngineFactory factory = new VelocityEngineFactory();
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		factory.setVelocityProperties(props);
		return factory.createVelocityEngine();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		for (HttpMessageConverter converter : converters) {
			if (converter instanceof AbstractJackson2HttpMessageConverter) {
				AbstractJackson2HttpMessageConverter c = (AbstractJackson2HttpMessageConverter) converter;
				ObjectMapper objectMapper = c.getObjectMapper();
				objectMapper.setSerializationInclusion(Include.NON_NULL);
				objectMapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
				objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
				// objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
				objectMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy HH24:mm"));
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			}
		}
		super.extendMessageConverters(converters);
	}

	@Bean(name = "localeResolver")
	public LocaleResolver localeResolver() {
		return new ApplicationLocaleResolver();
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	/*
	 * FreeMarker configuration.
	 */
	@Bean
	public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() {
		FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
		bean.setTemplateLoaderPath("classpath:templates/");
		return bean;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(150);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("AsyncJobs-");
		executor.initialize();
		return executor;
	}

	@Bean
	public PooledConnectionFactory pooledConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerUrl);
		activeMQConnectionFactory.setUserName(userName);
		activeMQConnectionFactory.setPassword(password);
		PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
		pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);
		return pooledConnectionFactory;
	}

	@Bean
	public JmsTemplate jmsTemplate() {
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(pooledConnectionFactory());
		template.setDeliveryMode(DeliveryMode.PERSISTENT);
		return template;
	}
}
