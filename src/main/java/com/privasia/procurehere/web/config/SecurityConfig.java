/**
 * 
 */
package com.privasia.procurehere.web.config;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.web.filters.AjaxLoginProcessingFilter;
import com.privasia.procurehere.web.filters.ChangePasswordFilter;
import com.privasia.procurehere.web.security.AjaxAuthenticationProvider;
import com.privasia.procurehere.web.security.JwtAuthenticationProvider;
import com.privasia.procurehere.web.security.JwtTokenAuthenticationProcessingFilter;
import com.privasia.procurehere.web.security.RestAuthenticationEntryPoint;
import com.privasia.procurehere.web.security.SkipPathRequestMatcher;
import com.privasia.procurehere.web.security.TokenExtractor;
import com.privasia.procurehere.web.servlets.AppAccessDeniedHandler;
import com.privasia.procurehere.web.servlets.AppAuthenticationFailureHandler;
import com.privasia.procurehere.web.servlets.AppAuthenticationSuccessHandler;
import com.privasia.procurehere.web.servlets.AppLogoutSuccessHandler;

/**
 * @author Nitin Otageri
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	public static final String JWT_TOKEN_HEADER_PARAM = "X-Authorization";
	public static final String FORM_BASED_LOGIN_ENTRY_POINT = "/api/auth/login";
	public static final String TOKEN_BASED_AUTH_ENTRY_POINT = "/api/**";
	public static final String TOKEN_REFRESH_ENTRY_POINT = "/api/auth/token";

	private static final Logger LOG = LogManager.getLogger(SecurityConfig.class);

	// Web Security
	@Configuration
	@Order(2)
	public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		@Qualifier("customUserDetailsService")
		UserDetailsService userDetailsService;

		@Autowired
		PersistentTokenRepository tokenRepository;

		@Autowired
		@Qualifier("appAuthenticationSuccessHandler")
		AppAuthenticationSuccessHandler authenticationSuccessHandler;

		@Autowired
		@Qualifier("appAuthenticationFailureHandler")
		AppAuthenticationFailureHandler authenticationFailureHandler;

		@Autowired
		@Qualifier("usernamePasswordAuthenticationManager")
		AuthenticationManager authenticationManager;

		@Autowired
		BCryptPasswordEncoder passwordEncoder;

		@Autowired
		ChangePasswordFilter changePasswordFilter;

		@Autowired
		AppLogoutSuccessHandler appLogoutSuccessHandler;

		@Override
		public void configure(WebSecurity web) throws Exception {
			// Spring Security should completely ignore URLs starting with /resources/
			web.ignoring().antMatchers("/resources/**", "/favicon.ico");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/favicon.ico", "/resources/**", "/integration/**", "/supplierSignup*", "/supplierSignup/*", "/confirmation", "/admin/resetPassword", "/admin/recoverPassword", "/admin/forgetPassword", "/admin/forgetPasswordThankYou", "/400_error", "/403_error", "/404_error", "/500_error", "/", "/owner/checkBuyerCompanyName", "/owner/checkLoginEmail", "/owner/checkBuyerRegistrationNumber", "/checkRegistrationNumber", "/checkCompanyNameExis", "/downloadOtherCredential", "/buyer/buyerProfileSetup", "/buyer/buyerTermsOfUse", "/buyer/countryStates", "/countryStates", "/index.jsp", "/index", "/", "/checkLoginEmail", "/forceChangePassword", "/buyerSubscription/**", "/suppliersubscription/**/*.*", "/suppliersubscription/**", "/publicEvents", "/erpApi/**", "/registration/saveEnquiry", "/Logout", "/finance/financeCompanyTermsOfUse", "/finance/financeCompanyProfileSetup", "/finance/financeCountryStates", "/integration/buyerApi/**", "/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/swagger-resources", "/swagger-resources/configuration/ui", "/swagger-resources/configuration/security", "/integration/supplierApi/**", "/myApi/**","/publicEvents/**", "/publicEventsListData/**","/viewPublicEventSummary/**","/downloadPublicEventSummary/**","/supplier/paymentEvents","/stripe/paymentEvents", "/**/register", "/**/buyerSupplierCreationThankyou", "/**/pcSearch", "/integration/userRevocationApi/**", "/maintenance-notification/**").permitAll()
					// all users
					.antMatchers("/changePassword").hasRole("USER")
					// supplier
					.antMatchers("/supplier/**", "/supplierDashboard", "/supplierProfile", "/supplierTrackRecord", "/saveSupplierInSession").hasRole("SUPPLIER")
					// .antMatchers("/", "/list").access("hasRole('USER') or hasRole('ADMIN') or hasRole('DBA')")
					.antMatchers("/buyer/**").hasRole("BUYER")
					// uom
					.antMatchers("/admin/uom/**").hasAnyRole("BUYER", "OWNER")
					.antMatchers("/supplierreg/**").hasAnyRole("BUYER", "OWNER", "SUPPLIER")
					// owners
					.antMatchers("/owner/**", "/admin/plan/**", "/admin/supplierplan/**", "/admin/editPromotionalCode*", "/admin/promotionalCodeList*").hasRole("OWNER")
					// login page access should be anonymous else the security headers will not be set for login page
					.antMatchers("/login").permitAll()
					.anyRequest().authenticated()
					// .and().exceptionHandling().accessDeniedPage("/forbidden")
					.and().exceptionHandling().accessDeniedHandler(new AppAccessDeniedHandler("/403_error")).and().formLogin().loginPage("/login").permitAll().failureUrl("/login?error=true")
					// .failureHandler(new SimpleUrlAuthenticationFailureHandler())
					.successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler)
					// Bypass CSRF for login page
					.and().csrf().ignoringAntMatchers("/supplier/payment**", "/buyerSubscription/**", "/suppliersubscription/**", "/registration/**", "/auctions/**", "/erpApi/**", "/integration/buyerApi/**", "/integration/supplierApi/**", "/myApi/**","/stripe/**", "/integration/userRevocationApi/**").and() //
					.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")) //
					.logoutSuccessHandler(appLogoutSuccessHandler).deleteCookies("JSESSIONID").and() //
					.sessionManagement().maximumSessions(1).expiredUrl("/logout?expired=true");

			/*
			 * .requireCsrfProtectionMatcher(new RequestMatcher() { // .ignoringAntMatchers("/login",
			 * "/supplier/payment**", "/buyerSubscription/**", "/suppliersubscription/**","/registration/**"
			 * ,"/erpApi/**") .requireCsrfProtectionMatcher(new RequestMatcher() { private Pattern allowedMethods =
			 * Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$"); // private RegexRequestMatcher unprotectedMatcher = new
			 * RegexRequestMatcher("/login", null); private AntPathRequestMatcher[] requestMatchers = { new
			 * AntPathRequestMatcher("/login"), new AntPathRequestMatcher("/supplier/payment**") };
			 * @Override public boolean matches(HttpServletRequest request) { if
			 * (allowedMethods.matcher(request.getMethod()).matches()) { return false; } for (AntPathRequestMatcher rm :
			 * requestMatchers) { if (rm.matches(request)) { return true; } } return false; // return
			 * !unprotectedMatcher.matches(request); } }) // .and().requestCache().requestCache(new NullRequestCache())
			 * -- use this if you want the original user // requested url to be stored into something else instead of
			 * HttpSessionRequestCache // in production
			 */
			http.addFilterAfter(changePasswordFilter, SwitchUserFilter.class);
		}

		@Bean("usernamePasswordAuthenticationManager")
		public AuthenticationManager authenticationManager() {
			List<AuthenticationProvider> providers = new LinkedList<AuthenticationProvider>();
			providers.add(authenticationProvider());
			ProviderManager pm = new ProviderManager(providers);
			return pm;
		}

		@Bean
		public UsernamePasswordAuthenticationFilter getUsernamePasswordAuthenticationFilter() {
			UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
			filter.setAuthenticationManager(authenticationManager());
			filter.setUsernameParameter("username");
			filter.setPasswordParameter("password");
			filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
			return filter;
		}

		@Autowired
		@Override
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService);
			auth.authenticationProvider(authenticationProvider());
		}

		@Bean
		public DaoAuthenticationProvider authenticationProvider() {
			DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
			authenticationProvider.setUserDetailsService(userDetailsService);
			authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
			return authenticationProvider;
		}

		@Bean
		public PersistentTokenBasedRememberMeServices getPersistentTokenBasedRememberMeServices() {
			PersistentTokenBasedRememberMeServices tokenBasedservice = new PersistentTokenBasedRememberMeServices("remember-me", userDetailsService, tokenRepository);
			return tokenBasedservice;
		}

	}

	@Configuration
	@Order(1)
	public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Autowired
		RestAuthenticationEntryPoint authenticationEntryPoint;

		@Autowired
		@Qualifier("ajaxAwareAuthenticationSuccessHandler")
		AuthenticationSuccessHandler successHandler;

		@Autowired
		@Qualifier("ajaxAwareAuthenticationFailureHandler")
		AuthenticationFailureHandler failureHandler;

		@Autowired
		AjaxAuthenticationProvider ajaxAuthenticationProvider;

		@Autowired
		JwtAuthenticationProvider jwtAuthenticationProvider;

		@Autowired
		private TokenExtractor tokenExtractor;

		@Autowired
		@Qualifier("restAuthenticationManager")
		private AuthenticationManager authenticationManager;

		@Autowired
		private ObjectMapper objectMapper;

		protected AjaxLoginProcessingFilter buildAjaxLoginProcessingFilter() throws Exception {
			AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter(FORM_BASED_LOGIN_ENTRY_POINT, successHandler, failureHandler, objectMapper);
			filter.setAuthenticationManager(this.authenticationManager);
			return filter;
		}

		protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter() throws Exception {
			List<String> pathsToSkip = Arrays.asList(TOKEN_REFRESH_ENTRY_POINT, FORM_BASED_LOGIN_ENTRY_POINT);
			SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, TOKEN_BASED_AUTH_ENTRY_POINT);
			JwtTokenAuthenticationProcessingFilter filter = new JwtTokenAuthenticationProcessingFilter(failureHandler, tokenExtractor, matcher);
			filter.setAuthenticationManager(this.authenticationManager);
			return filter;
		}

		@Bean("restAuthenticationManager")
		@Override
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) {
			LOG.info("Setting authentication providers.... : " + jwtAuthenticationProvider);
			auth.authenticationProvider(ajaxAuthenticationProvider);
			auth.authenticationProvider(jwtAuthenticationProvider);
		}

		protected void configure(HttpSecurity http) throws Exception {

			http.csrf().disable().exceptionHandling()//
			.authenticationEntryPoint(this.authenticationEntryPoint)//
			.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//
			.and().authorizeRequests().antMatchers(FORM_BASED_LOGIN_ENTRY_POINT).permitAll() // Login end-point
			.antMatchers(TOKEN_REFRESH_ENTRY_POINT).permitAll()//
			.and().antMatcher(TOKEN_BASED_AUTH_ENTRY_POINT).authorizeRequests().anyRequest().authenticated()//
			.and().addFilterBefore(buildAjaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class) //
			.addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
			// .and().antMatcher("/api/**")
			// .authorizeRequests()
			// .anyRequest().hasRole("ADMIN")
			// .and()
			// .httpBasic();
		}
	}
	
}
