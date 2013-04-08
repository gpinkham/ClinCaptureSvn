package org.akaza.openclinica.control.login;

import java.io.PrintWriter;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.dao.core.CoreResources;

@SuppressWarnings({ "unchecked", "serial" })
public class HelpThemeServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
		String newThemeColor = CoreResources.getField("themeColor");
		PrintWriter pw = response.getWriter();
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		pw.write(newThemeColor);
		pw.flush();
	}
}