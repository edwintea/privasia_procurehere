/**
 * 
 */
package com.privasia.procurehere.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import com.privasia.procurehere.web.config.JpaConfig;
import com.privasia.procurehere.web.config.MailConfig;
import com.privasia.procurehere.web.config.SpringRootConfig;

/**
 * @author arc
 *
 */
@Configuration
@Import({MailConfig.class, JpaConfig.class, SpringRootConfig.class})
@ComponentScan(basePackages = {"com.privasia.procurehere.core.dao.impl", "com.privasia.procurehere.core.dao", "com.privasia.procurehere.supplier.dao.impl", "com.privasia.procurehere.supplier.dao", "com.privasia.procurehere.service.impl", "com.privasia.procurehere.service", "com.privasia.procurehere.service.supplier"}, excludeFilters = { @Filter(type = FilterType.REGEX, pattern = "com.privasia.procurehere.core.entity") })
public class TestConfig {

}
