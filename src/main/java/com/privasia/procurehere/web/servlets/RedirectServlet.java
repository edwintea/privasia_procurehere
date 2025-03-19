/**
 * 
 */
package com.privasia.procurehere.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This one just does a redirect if it identifies "url=" into the query string. Used mostly in case of error page
 * handlers. e.g. 404, 403 etc.
 * 
 * @author Nitin Otageri
 */
public class RedirectServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1185051562479704036L;

	private static final String URL_PREFIX = "url=";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String query = req.getQueryString();
		if (query.contains(URL_PREFIX)) {
			String url = query.replace(URL_PREFIX, "");
			if (!url.startsWith(req.getContextPath())) {
				url = req.getContextPath() + url;
			}

			resp.sendRedirect(url);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
