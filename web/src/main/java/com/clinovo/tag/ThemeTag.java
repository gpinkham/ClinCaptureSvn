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

package com.clinovo.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Custom tag for adding hide stuff class.
 */
@SuppressWarnings({ "serial" })
public class ThemeTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThemeTag.class);

	@Override
	public int doStartTag() throws JspException {
		String newThemeColor = (String) pageContext.getSession().getAttribute("newThemeColor");
		newThemeColor = newThemeColor == null || newThemeColor.trim().isEmpty() ? "blue" : newThemeColor;
		String html = "<script type=\"text/JavaScript\" language=\"JavaScript\" src=\"".concat(
				pageContext.getServletContext().getContextPath()).concat("/includes/theme.js\"></script>");
		if (!newThemeColor.equalsIgnoreCase("blue")) {
			html = html.concat("<style class=\"hideStuff\" type=\"text/css\">body {visibility:hidden;}</style>");
		}
		html = html.concat("<link rel=\"stylesheet\" href=\"").concat(pageContext.getServletContext().getContextPath())
				.concat("/includes/css/charts_").concat(newThemeColor).concat(".css\" type=\"text/css\"/>");
		html = html.concat("<link rel=\"stylesheet\" href=\"").concat(pageContext.getServletContext().getContextPath())
				.concat("/includes/css/styles_").concat(newThemeColor).concat(".css\" type=\"text/css\"/>");
		JspWriter writer = pageContext.getOut();
		try {
			writer.write(html);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return SKIP_BODY;
	}
}
