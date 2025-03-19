/**
 * 
 */
package com.privasia.procurehere.web.servlets;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.privasia.procurehere.web.config.WebAppConfig;

/**
 * @author Nitin Otageri
 *
 */
public class WebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		//TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.register(WebAppConfig.class);
		ctx.setServletContext(servletContext);

		DispatcherServlet dp =  new DispatcherServlet(ctx);
        dp.setThrowExceptionIfNoHandlerFound(true);
        
		Dynamic dynamic = servletContext.addServlet("dispatcher", dp);
		dynamic.addMapping("/");
		dynamic.setLoadOnStartup(1);
		dynamic.setAsyncSupported(true);
		
		servletContext.addListener(HttpSessionEventPublisher.class);
		
		ResourceBundle bundle = ResourceBundle.getBundle("application");
        String googleAnalTrackId = bundle.getString("google.analytics.tracking.id");
        String clickStreamAnalTrackNam = bundle.getString("clickstream.analytics.tracker.name");
        String clickStreamAnalAppId = bundle.getString("clickstream.analytics.app.id");
        
		
		servletContext.setInitParameter("GOOGLE_ANALYTICS_TRACKING_ID", googleAnalTrackId);
		servletContext.setInitParameter("CLICKSTREAM_ANALYTICS_TRACKER_NAME", clickStreamAnalTrackNam);
		servletContext.setInitParameter("CLICKSTREAM_ANALYTICS_APP_ID", clickStreamAnalAppId);
		
	}

}
