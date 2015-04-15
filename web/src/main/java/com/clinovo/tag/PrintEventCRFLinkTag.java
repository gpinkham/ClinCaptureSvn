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
import org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.CRFMaskingService;

/**
 * Custom tag for building Print Event CRF link.
 */
@SuppressWarnings("serial")
public class PrintEventCRFLinkTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintEventCRFLinkTag.class);

	private DisplayEventDefinitionCRFBean dedc;
	private StudyEventBean studyEvent;
	private String crfVersionOid = "";
	private DisplayEventCRFBean dec;
	private String subjectOid = "";
	private String studyOid = "";
	private String onClick = "";
	private String hspace = "";

	private void reset() {
		dec = null;
		dedc = null;
		hspace = "";
		onClick = "";
		studyOid = "";
		subjectOid = "";
		studyEvent = null;
		crfVersionOid = "";
	}

	@Override
	public int doStartTag() throws JspException {
		WebApplicationContext appContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(((PageContext) pageContext.getAttribute(PageContext.PAGECONTEXT))
						.getServletContext());
		CRFMaskingService maskingService = (CRFMaskingService) appContext.getBean("crfMaskingService");
		MessageSource messageSource = (MessageSource) appContext.getBean("messageSource");
		Locale locale = LocaleResolver.getLocale((HttpServletRequest) pageContext.getRequest());
		UserAccountBean user = (UserAccountBean) pageContext.getSession().getAttribute("userBean");
		hspace = hspace.isEmpty() ? "6" : hspace;
		String link = "<img src=\"images/bt_Transparent.gif\" border=\"0\" align=\"left\" hspace=\"" + hspace + "\">";
		String ending = "onMouseDown=\"javascript:setImage('bt_Print1','images/bt_Print_d.gif');\" "
				+ "onMouseUp=\"javascript:setImage('bt_Print1','images/bt_Print.gif');\" " + "onclick=\"" + onClick
				+ "\">" + "<img name=\"bt_Print1\" src=\"images/bt_Print.gif\" border=\"0\" " + "alt=\""
				+ messageSource.getMessage("print_default", null, locale) + "\" " + "title=\""
				+ messageSource.getMessage("print_default", null, locale) + "\" " + "align=\"left\" hspace=\"" + hspace
				+ "\"/></a>";
		EventDefinitionCRFBean edcBean;
		edcBean = dec == null ? (dedc == null ? null : dedc.getEdc()) : dec.getEventDefinitionCRF();
		boolean decMasked = edcBean != null
				&& maskingService.isEventDefinitionCRFMasked(edcBean.getId(), user.getId(), edcBean.getStudyId());

		if (dec != null && studyEvent != null && !decMasked) {
			link = "<a href=\"javascript:openPrintCRFWindow('print/clinicaldata/html/print/" + studyOid + "/"
					+ subjectOid + "/" + studyEvent.getStudyEventDefinition().getOid();
			if (studyEvent.getStudyEventDefinition().isRepeating()) {
				link = link.concat("[" + studyEvent.getSampleOrdinal() + "]");
			}
			link += "/" + dec.getEventCRF().getCrfVersion().getOid() + "')\" " + ending;
		} else if (!crfVersionOid.isEmpty() && (!decMasked)) {
			link = "<a href=\"javascript:processPrintCRFRequest('print/metadata/html/print/*/*/" + crfVersionOid
					+ "')\" " + ending;
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

	public DisplayEventCRFBean getDec() {
		return dec;
	}

	public void setDec(DisplayEventCRFBean dec) {
		this.dec = dec;
	}

	public StudyEventBean getStudyEvent() {
		return studyEvent;
	}

	public void setStudyEvent(StudyEventBean studyEvent) {
		this.studyEvent = studyEvent;
	}

	public String getSubjectOid() {
		return subjectOid;
	}

	public void setSubjectOid(String subjectOid) {
		this.subjectOid = subjectOid;
	}

	public String getStudyOid() {
		return studyOid;
	}

	public void setStudyOid(String studyOid) {
		this.studyOid = studyOid;
	}

	public String getCrfVersionOid() {
		return crfVersionOid;
	}

	public void setCrfVersionOid(String crfVersionOid) {
		this.crfVersionOid = crfVersionOid;
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

	public DisplayEventDefinitionCRFBean getDedc() {
		return dedc;
	}

	public void setDedc(DisplayEventDefinitionCRFBean dedc) {
		this.dedc = dedc;
	}

	@Override
	public void release() {
		reset();
		super.release();
	}
}
