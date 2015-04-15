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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.CRFMaskingService;

/**
 * Custom tag for building RestoreEventCRF link.
 */
@SuppressWarnings("serial")
public class RestoreEventCRFLinkTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestoreEventCRFLinkTag.class);

	private Object object;
	private String hspace = "";
	private String onClick = "";
	private String subjectId = "";

	@Override
	public int doStartTag() throws JspException {
		WebApplicationContext appContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(((PageContext) pageContext.getAttribute(PageContext.PAGECONTEXT))
						.getServletContext());
		CRFMaskingService crfMaskingService = (CRFMaskingService) appContext.getBean("crfMaskingService");
		MessageSource messageSource = (MessageSource) appContext.getBean("messageSource");
		Locale locale = LocaleResolver.getLocale((HttpServletRequest) pageContext.getRequest());
		UserAccountBean user = (UserAccountBean) pageContext.getSession().getAttribute("userBean");
		EventDefinitionCRFBean edcBean;
		hspace = hspace.isEmpty() ? "4" : hspace;
		String link = "<img src=\"images/bt_Transparent.gif\" border=\"0\" align=\"left\" hspace=\"" + hspace + "\">";

		if (object != null && object instanceof DisplayEventCRFBean && !subjectId.isEmpty()) {
			DisplayEventCRFBean dec = (DisplayEventCRFBean) object;
			edcBean = dec.getEventDefinitionCRF();

			if (!crfMaskingService.isEventDefinitionCRFMasked(edcBean.getId(), user.getId(), edcBean.getStudyId())) {
				link = "<a href=\"RestoreEventCRF?action=confirm&id=" + dec.getEventCRF().getId() + "&studySubId="
						+ subjectId + "\" "
						+ "onMouseDown=\"javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');\" "
						+ "onMouseUp=\"javascript:setImage('bt_Restore3','images/bt_Restore.gif');\" " + "onclick=\""
						+ onClick + "\">" + "<img name=\"bt_Restore3\" "
						+ "src=\"images/bt_Restore.gif\" border=\"0\" " + "alt=\""
						+ messageSource.getMessage("restore", null, locale) + "\" " + "title=\""
						+ messageSource.getMessage("restore", null, locale) + "\" " + "align=\"left\" " + "hspace=\""
						+ hspace + "\"></a>";
			}
		}
		JspWriter writer = pageContext.getOut();
		try {
			writer.write(link);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		reset();
		return SKIP_BODY;
	}

	private void reset() {
		hspace = "";
		onClick = "";
		subjectId = "";
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getHspace() {
		return hspace;
	}

	public void setHspace(String hspace) {
		this.hspace = hspace;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	@Override
	public void release() {
		reset();
		super.release();
	}
}
