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

import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Custom tag for adding calendar js scripts.
 */
@SuppressWarnings({ "serial" })
public class CalendarTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarTag.class);

	@Override
	public int doStartTag() throws JspException {
		String language = SessionUtil.getLocale((HttpServletRequest) pageContext.getRequest()).getLanguage();
		language = CoreResources.CALENDAR_LANGS.contains(language) ? language : "en";
		String contextPath = pageContext.getServletContext().getContextPath();
		String html = "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"".concat(contextPath).concat(
				"/includes/new_cal/skins/aqua/theme.css\" title=\"Aqua\" />");
		html = html.concat("<script type=\"text/javascript\" src=\"").concat(contextPath)
				.concat("/includes/new_cal/calendar.js\"></script>");
		html = html.concat("<script type=\"text/javascript\" src=\"").concat(contextPath)
				.concat("/includes/new_cal/lang/calendar-").concat(language).concat(".js\"></script>");
		html = html.concat("<script type=\"text/javascript\" src=\"").concat(contextPath)
				.concat("/includes/new_cal/calendar-setup.js\"></script>");
		JspWriter writer = pageContext.getOut();
		try {
			writer.write(html);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return SKIP_BODY;
	}
}
