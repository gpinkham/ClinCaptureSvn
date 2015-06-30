/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.tag;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SDVUtil;

import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.sql.DataSource;

import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Custom tag for building SDV Study Subject link.
 */
@SuppressWarnings("serial")
public class SDVStudySubjectLinkTag extends TagSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(SDVStudySubjectLinkTag.class);

	private StudySubjectBean studySubject;

	private boolean studySubjectHasUnclosedDNs;

	private Page page;

	private HttpServletRequest request;

	public StudySubjectBean getStudySubject() {
		return studySubject;
	}

	public void setStudySubject(StudySubjectBean studySubject) {
		this.studySubject = studySubject;
	}

	public boolean isStudySubjectHasUnclosedDNs() {
		return studySubjectHasUnclosedDNs;
	}

	public void setStudySubjectHasUnclosedDNs(boolean studySubjectHasUnclosedDNs) {
		this.studySubjectHasUnclosedDNs = studySubjectHasUnclosedDNs;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override public int doStartTag() throws JspException {

		JspWriter writer = pageContext.getOut();
		try {
			writer.write(buildLink());
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		reset();
		return SKIP_BODY;
	}

	/**
	 * Returns HTML href to SDV page for a specific study subject,
	 * if study subject is ready for SDV and current user has permissions to perform SDV;
	 * Returns transparent icon-gag otherwise.
	 *
	 * @return String
	 */
	public String buildLink() {

		HttpServletRequest request = pageContext != null ? (HttpServletRequest) pageContext.getRequest() : getRequest();
		HttpSession session = request.getSession();
		StudyBean currentStudy = (StudyBean) session.getAttribute(BaseController.STUDY);
		StudyUserRoleBean currentRole = (StudyUserRoleBean) session.getAttribute(BaseController.USER_ROLE);
		WebApplicationContext appContext =
				WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());
		DataSource dataSource = (DataSource) appContext.getBean("dataSource");
		MessageSource messageSource = (MessageSource) appContext.getBean("messageSource");
		Locale locale = LocaleResolver.getLocale(request);

		DAOWrapper daoWrapper = new DAOWrapper(new StudyDAO(dataSource), new StudyEventDAO(dataSource),
				new StudySubjectDAO(dataSource), new EventCRFDAO(dataSource), new EventDefinitionCRFDAO(dataSource),
				new StudyEventDefinitionDAO(dataSource), new DiscrepancyNoteDAO(dataSource));
		boolean isStudySubjectReadyForSDV = SDVUtil.permitSDV(getStudySubject(), daoWrapper);
		boolean isStudyStatusAllowsSDV = !currentStudy.getStatus().isFrozen() && !currentStudy.getStatus().isLocked();
		String allowSdvWithOpenQueries = currentStudy.getStudyParameterConfig().getAllowSdvWithOpenQueries();
		boolean isSDVAllowedIfSubjectHasUnclosedDNs = (allowSdvWithOpenQueries.equalsIgnoreCase("yes"))
				|| (allowSdvWithOpenQueries.equalsIgnoreCase("no") && !isStudySubjectHasUnclosedDNs());
		StringBuilder sdvLink = new StringBuilder("");

		if (isStudySubjectReadyForSDV && !getStudySubject().getStatus().isDeleted()
				&& isStudyStatusAllowsSDV && currentRole.isCanSDV() && isSDVAllowedIfSubjectHasUnclosedDNs) {

			if (Page.CRF_LIST_FOR_STUDY_EVENT.getFileName().equals(page.getFileName())) {
				sdvLink.append("<c:set var=\"hideCol5\" value=\"false\"/>\n");
			}

			// SDV href opening tag >> start
			sdvLink.append("<a class=\"sdvLink\" href=\"pages/viewSubjectAggregate?sbb=true&studyId=")
					.append(currentStudy.getId())
					.append("&studySubjectId=&theStudySubjectId=0&redirection=viewSubjectAggregate&maxRows=15")
					.append("&showMoreLink=true&s_sdv_tr_=true&s_sdv_p_=1&s_sdv_mr_=15&s_sdv_f_studySubjectId=")
					.append(getStudySubject().getLabel()).append("\"");
			if (Page.LIST_STUDY_SUBJECTS.getFileName().equals(page.getFileName())) {
				sdvLink.append(" onClick=\"javascript:setAccessedObjected(this);\"");
			}
			sdvLink.append(">");
			// SDV href opening tag >> end

			// adding SDV icon
			String performSDVmsg = messageSource.getMessage("perform_sdv", null, locale);
			sdvLink.append("<img src=\"images/icon_DoubleCheck_Action.gif\" border=\"0\" align=\"left\" alt=\"")
					.append(performSDVmsg).append("\" title=\"").append(performSDVmsg).append("\" hspace=\"4\"/>");

			// SDV href closing tag
			sdvLink.append("</a>");
		} else {
			// adding Transparent icon, if SDV is not allowed
			sdvLink.append("<img src=\"images/bt_Transparent.gif\" border=\"0\" align=\"left\" hspace=\"4\"/>");
		}
		return sdvLink.toString();
	}

	private void reset() {
		studySubject = null;
		studySubjectHasUnclosedDNs = false;
		page = null;
		request = null;
	}

	@Override public void release() {
		reset();
		super.release();
	}
}


