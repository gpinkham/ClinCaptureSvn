/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.logic.rulerunner;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import com.clinovo.util.EmailUtil;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.hibernate.RuleActionRunLogDao;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.akaza.openclinica.service.rule.expression.ExpressionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * 
 * Contains all basic routines for running rules.
 * 
 */
public class RuleRunner {

	private final Logger logger = LoggerFactory.getLogger(RuleRunner.class);

	private static final int TWENTY = 20;
	public static final String NEW_LINE = "<br/>";

	private final JavaMailSenderImpl mailSender;
	private DynamicsMetadataService dynamicsMetadataService;
	private RuleActionRunLogDao ruleActionRunLogDao;
	private DataSource ds;

	private String requestURLMinusServletPath;
	private String contextPath;

	/**
	 *
	 * Rule Runner Mode Enum.
	 *
	 */
	public enum RuleRunnerMode {
		DATA_ENTRY, CRF_BULK, RULSET_BULK, IMPORT_DATA
	}

	/**
	 *
	 * @param ds
	 *            DataSource
	 * @param requestURLMinusServletPath
	 *            Request URL
	 * @param contextPath
	 *            Context path
	 * @param mailSender
	 *            Mail sender
	 */
	public RuleRunner(DataSource ds, String requestURLMinusServletPath, String contextPath,
			JavaMailSenderImpl mailSender) {
		this.ds = ds;
		this.requestURLMinusServletPath = requestURLMinusServletPath;
		this.contextPath = contextPath;
		this.mailSender = mailSender;
	}

	public DataSource getDataSource() {
		return ds;
	}

	String curateMessage(RuleActionBean ruleAction, RuleSetRuleBean ruleSetRule) {
		String message = ruleAction.getSummary();
		String ruleOid = ruleSetRule.getRuleBean().getOid();
		return ruleOid + " " + message;
	}

	@SuppressWarnings("unchecked")
	HashMap<String, String> prepareEmailContents(RuleSetBean ruleSet,
												 StudyBean currentStudy, RuleActionBean ruleAction) {

		// get the Study Event
		StudyEventBean studyEvent = (StudyEventBean) getStudyEventDao().findByPK(
				Integer.valueOf(getExpressionService().getStudyEventDefenitionOrdninalCurated(
						ruleSet.getTarget().getValue())));
		// get the Study Subject
		StudySubjectBean studySubject = (StudySubjectBean) getStudySubjectDao()
				.findByPK(studyEvent.getStudySubjectId());
		// get Study/Site Associated with Subject
		StudyBean theStudy = (StudyBean) getStudyDao().findByPK(studySubject.getStudyId());
		String theStudyName, theSiteName = "";
		if (theStudy.getParentStudyId() > 0) {
			StudyBean theParentStudy = (StudyBean) getStudyDao().findByPK(theStudy.getParentStudyId());
			theStudyName = theParentStudy.getName() + " / " + theParentStudy.getIdentifier();
			theSiteName = theStudy.getName() + " / " + theStudy.getIdentifier();
		} else {
			theStudyName = theStudy.getName() + " / " + theStudy.getIdentifier();
		}

		CRFBean crf;
		EventCRFBean eventCrf;
		String crfVersionOid = getExpressionService().getCrfOid(ruleSet.getTarget().getValue());
		CRFVersionBean crfVersion = getCrfVersionDao().findByOid(crfVersionOid);
		if (crfVersion != null && crfVersion.getCrfId() > 0) {
			crf = (CRFBean) getCrfDao().findByPK(crfVersion.getCrfId());
		} else {
			crf = getCrfDao().findByOid(crfVersionOid);
		}
		logger.debug("rule checking: " + getExpressionService().getCrfOid(ruleSet.getTarget().getValue()));
		logger.debug("expression checking " + ruleSet.getTarget().getValue());
		List<EventCRFBean> eventCrfList = (List<EventCRFBean>) getEventCrfDao()
				.findAllByStudyEventAndCrfOrCrfVersionOid(studyEvent, crf.getOid());
		if (eventCrfList != null && eventCrfList.size() > 0) {
			eventCrf = eventCrfList.get(0);
		} else {
			return new HashMap<String, String>();
		}

		crfVersion = (CRFVersionBean) getCrfVersionDao().findByPK(eventCrf.getCRFVersionId());
		crf = (CRFBean) getCrfDao().findByPK(crfVersion.getCrfId());

		String studyEventDefinitionName = getExpressionService().getStudyEventDefinitionFromExpression(
				ruleSet.getTarget().getValue(), currentStudy).getName();
		studyEventDefinitionName += " [" + studyEvent.getSampleOrdinal() + "]";

		String itemGroupName = getExpressionService().getItemGroupNameAndOrdinal(ruleSet.getTarget().getValue());
		ItemGroupBean itemGroupBean = getExpressionService().getItemGroupExpression(ruleSet.getTarget().getValue());
		ItemBean itemBean = getExpressionService().getItemExpression(ruleSet.getTarget().getValue(), itemGroupBean);
		String itemName = itemBean.getName();

		SectionBean section = (SectionBean) getSectionDAO().findByPK(
				getItemFormMetadataDAO().findByItemIdAndCRFVersionId(itemBean.getId(), crfVersion.getId())
						.getSectionId());

		Locale locale = CoreResources.getSystemLocale();
		StringBuilder sb = new StringBuilder();
		ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle();

		sb.append(EmailUtil.getEmailBodyStart());
		sb.append(respage.getString("email_header_1"));
		sb.append(" ");
		sb.append(respage.getString("email_header_2"));
		sb.append(" '").append(currentStudy.getName()).append("' ");
		sb.append(respage.getString("email_header_3"));
		sb.append(NEW_LINE);

		sb.append(respage.getString("email_body_1")).append(" ").append(theStudyName).append(NEW_LINE);
		sb.append(respage.getString("email_body_1_a")).append(" ").append(theSiteName).append(NEW_LINE);
		sb.append(respage.getString("email_body_2")).append(" ").append(studySubject.getName()).append(NEW_LINE);
		sb.append(respage.getString("email_body_3")).append(" ").append(studyEventDefinitionName).append(NEW_LINE);
		sb.append(respage.getString("email_body_4")).append(" ").append(crf.getName()).append(" ").append(crfVersion.getName()).append(NEW_LINE);
		sb.append(respage.getString("email_body_5")).append(" ").append(section.getTitle()).append(NEW_LINE);
		sb.append(respage.getString("email_body_6")).append(" ").append(itemGroupName).append(NEW_LINE);
		sb.append(respage.getString("email_body_7")).append(" ").append(itemName).append(NEW_LINE);
		sb.append(respage.getString("email_body_8")).append(" ").append(ruleAction.getCuratedMessage()).append(NEW_LINE);

		sb.append(NEW_LINE);
		sb.append(respage.getString("email_body_9"));
		sb.append(" ").append(contextPath).append(" ");
		sb.append(respage.getString("email_body_10"));
		sb.append(NEW_LINE);

		requestURLMinusServletPath = requestURLMinusServletPath == null ? "" : requestURLMinusServletPath;

		sb.append(requestURLMinusServletPath).append("/ViewSectionDataEntry?eventCRFId=").append(eventCrf.getId()).append("&sectionId=").append(section.getId()).append("&tabId=").append(section.getOrdinal());
		sb.append(NEW_LINE);
		sb.append(EmailUtil.getEmailBodyEnd());
		sb.append(EmailUtil.getEmailFooter(locale));

		String subject = contextPath + " - [" + currentStudy.getName() + "] ";
		String ruleSummary = ruleAction.getSummary() != null ? ruleAction.getSummary() : "";
		String message = ruleSummary.length() < TWENTY ? ruleSummary : ruleSummary.substring(0, TWENTY) + " ... ";
		subject += message;

		HashMap<String, String> emailContents = new HashMap<String, String>();
		emailContents.put("body", sb.toString());
		emailContents.put("subject", subject);

		return emailContents;
	}

	ExpressionService getExpressionService() {
		return dynamicsMetadataService.getExpressionService();
	}

	CRFDAO getCrfDao() {
		return new CRFDAO(ds);
	}

	StudyEventDAO getStudyEventDao() {
		return new StudyEventDAO(ds);
	}

	ItemDataDAO getItemDataDao() {
		return new ItemDataDAO(ds);
	}

	EventCRFDAO getEventCrfDao() {
		return new EventCRFDAO(ds);
	}

	CRFVersionDAO getCrfVersionDao() {
		return new CRFVersionDAO(ds);
	}

	StudySubjectDAO getStudySubjectDao() {
		return new StudySubjectDAO(ds);
	}

	ItemFormMetadataDAO getItemFormMetadataDAO() {
		return new ItemFormMetadataDAO(ds);
	}

	SectionDAO getSectionDAO() {
		return new SectionDAO(ds);
	}

	StudyDAO getStudyDao() {
		return new StudyDAO(ds);
	}

	public JavaMailSenderImpl getMailSender() {
		return mailSender;
	}

	public DynamicsMetadataService getDynamicsMetadataService() {
		return dynamicsMetadataService;
	}

	public void setDynamicsMetadataService(DynamicsMetadataService dynamicsMetadataService) {
		this.dynamicsMetadataService = dynamicsMetadataService;
	}

	public RuleActionRunLogDao getRuleActionRunLogDao() {
		return ruleActionRunLogDao;
	}

	public void setRuleActionRunLogDao(RuleActionRunLogDao ruleActionRunLogDao) {
		this.ruleActionRunLogDao = ruleActionRunLogDao;
	}

	protected boolean ruleActionContainerAlreadyExistsInList(RuleActionContainer ruleActionContainer,
			List<RuleActionContainer> ruleActionContainerList) {
		for (RuleActionContainer rac : ruleActionContainerList) {
			if (rac.getRuleAction().getId().equals(ruleActionContainer.getRuleAction().getId())
					&& rac.getExpressionBean().equals(ruleActionContainer.getExpressionBean())
					&& ((rac.getItemDataBean() == null && ruleActionContainer.getItemDataBean() == null) || (rac
							.getItemDataBean().getId() == ruleActionContainer.getItemDataBean().getId()))
					&& rac.getRuleSetBean().getId().equals(ruleActionContainer.getRuleSetBean().getId())) {
				return true;
			}
		}
		return false;
	}
}
