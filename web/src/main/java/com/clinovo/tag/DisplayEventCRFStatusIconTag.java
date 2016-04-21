/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.tag;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.EventCRFUtil;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.sql.DataSource;

import java.util.Locale;

/**
 * Custom tag for calculation of current status of event CRF.
 */
@SuppressWarnings("serial")
public class DisplayEventCRFStatusIconTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(DisplayEventCRFStatusIconTag.class);

	private StudySubjectBean studySubject;

	private StudyEventBean studyEvent;

	private EventDefinitionCRFBean eventDefinitionCRF;

	private EventCRFBean eventCrf;

	public StudySubjectBean getStudySubject() {
		return studySubject;
	}

	public void setStudySubject(StudySubjectBean studySubject) {
		this.studySubject = studySubject;
	}

	public StudyEventBean getStudyEvent() {
		return studyEvent;
	}

	public void setStudyEvent(StudyEventBean studyEvent) {
		this.studyEvent = studyEvent;
	}

	public EventDefinitionCRFBean getEventDefinitionCRF() {
		return eventDefinitionCRF;
	}

	public void setEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRF) {
		this.eventDefinitionCRF = eventDefinitionCRF;
	}

	public EventCRFBean getEventCrf() {
		return eventCrf;
	}

	public void setEventCrf(EventCRFBean eventCrf) {
		this.eventCrf = eventCrf;
	}

	@Override
	public int doStartTag() throws JspException {

		JspWriter writer = pageContext.getOut();
		try {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			HttpSession session = request.getSession();
			WebApplicationContext appContext = WebApplicationContextUtils
					.getRequiredWebApplicationContext(session.getServletContext());
			DataSource dataSource = (DataSource) appContext.getBean("dataSource");
			CRFVersionDAO crfVersionDAO = new CRFVersionDAO(dataSource);
			EventDefinitionCRFDAO eventDefCRFDAO = new EventDefinitionCRFDAO(dataSource);
			MessageSource messageSource = (MessageSource) appContext.getBean("messageSource");
			Locale locale = LocaleResolver.getLocale(request);

			Status eventCRFStatus = EventCRFUtil.getEventCRFCurrentStatus(studySubject, studyEvent,
					eventDefinitionCRF, eventCrf, crfVersionDAO, eventDefCRFDAO);
			String eventCRFStatusIconPath = EventCRFUtil.getEventCRFStatusIconPath(eventCRFStatus);
			String eventCRFStatusIconHintHandle = EventCRFUtil.getStatusIconHintHandle(eventCRFStatus, eventDefinitionCRF);
			String eventCRFStatusIconHint = messageSource.getMessage(eventCRFStatusIconHintHandle, null, locale);
			String eventCRFStatusImg = new StringBuilder("").append("<img src=\"").append(eventCRFStatusIconPath)
					.append("\" alt=\"").append(eventCRFStatusIconHint).append("\" title=\"")
					.append(eventCRFStatusIconHint).append("\">").toString();
			writer.write(eventCRFStatusImg);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		reset();
		return SKIP_BODY;
	}

	private void reset() {
		studySubject = null;
		studyEvent = null;
		eventDefinitionCRF = null;
		eventCrf = null;
	}

	@Override
	public void release() {
		reset();
		super.release();
	}
}

