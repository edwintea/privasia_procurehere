package com.privasia.procurehere.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@Conditional(SwaggerConfigurationBasedOnEnvironment.class)
@PropertySource(value="classpath:application.properties")
@EnableSwagger2
public class SwaggerConfig {
	// @formatter:off
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)//
				.groupName("ERP") //
				.select()//
				.apis(RequestHandlerSelectors.any())// RequestHandlerSelectors.basePackage("com.user.controller")
				.paths(PathSelectors.regex("/erpApi/.*"))//
				.build()//
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("PROCUREHERE").description("Online Eproc").version("1.33.0")
				/*
				 * .termsOfServiceUrl("http://terms-of-services.url") .license("LICENSE")
				 * .licenseUrl("http://url-to-license.com")
				 */
				.build();
	}

	@Bean
	public Docket supplierIntegrationApi() {
		return new Docket(DocumentationType.SWAGGER_2)//
				.groupName("Supplier") //
				.select()//
				.apis(RequestHandlerSelectors.any())//
				.paths(PathSelectors.regex("/integration/.*"))//
				.build()//
				.apiInfo(apiInfo());
	}

	// @formatter:on
}
