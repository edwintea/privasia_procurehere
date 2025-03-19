/**
 * 
 */
package com.privasia.procurehere.web.config;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author nitin
 *
 */
public class SwaggerConfigurationBasedOnEnvironment implements Condition {

	private static final Logger LOG = LogManager.getLogger(SwaggerConfigurationBasedOnEnvironment.class);
	
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String environmentKey = null;
		
		Resource resource = new ClassPathResource("/application.properties");
		Properties props;
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
			environmentKey = props.getProperty("environment-key", null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOG.info(" $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ " + environmentKey);
	    return !(environmentKey == null || (environmentKey != null && environmentKey.equalsIgnoreCase("demo")) || (environmentKey != null && environmentKey.trim().equalsIgnoreCase("")));
	}

}
