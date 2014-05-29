/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.navigation;

import java.io.PrintWriter;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author igor
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
@SuppressWarnings({"unchecked", "serial"})
public class HelpNavigationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        Stack<String> visitedURLs;
        String defaultUrl = "/MainMenu";
        if (request.getSession().getAttribute("visitedURLs")!=null) {
    		visitedURLs = (Stack<String>)request.getSession().getAttribute("visitedURLs");
    		visitedURLs.pop();
    		if (visitedURLs.isEmpty()){
    			visitedURLs.push(defaultUrl);
    		} 
    		request.getSession().setAttribute("skipURL", "true");
    		response.setContentType("text/xml");
    		response.setHeader("Cache-Control", "no-cache");
    		PrintWriter pw = response.getWriter();
    		pw.write(request.getContextPath()+visitedURLs.peek());
    		pw.flush();
        }
    }
}
