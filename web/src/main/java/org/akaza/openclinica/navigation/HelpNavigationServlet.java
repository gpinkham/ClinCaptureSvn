package org.akaza.openclinica.navigation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Stack;

/**
 * @author igor
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
@SuppressWarnings({ "unchecked", "serial" })
public class HelpNavigationServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			java.io.IOException {
		Stack<String> visitedURLs;
		String defaultUrl = "/MainMenu";
		String backToRulesUrl = "/ViewRuleAssignment";
		if (request.getSession().getAttribute("visitedURLs") != null) {
			System.out.println("inside  delete block");
			visitedURLs = (Stack<String>) request.getSession().getAttribute("visitedURLs");
			visitedURLs.pop();
			if (visitedURLs.isEmpty()) {
				visitedURLs.push(defaultUrl);
			} else if (visitedURLs.peek().contains("ViewRuleAssignment")) {
				visitedURLs.push(backToRulesUrl);
			}
			request.getSession().setAttribute("skipURL", "true");
			response.setContentType("text/xml");
			response.setHeader("Cache-Control", "no-cache");
			PrintWriter pw = response.getWriter();
			pw.write(request.getContextPath() + visitedURLs.peek());
			pw.flush();
		}
	}
}
