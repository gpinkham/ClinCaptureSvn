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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.bean.rule;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.rule.RuleDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"rawtypes"})
public class RuleExecutionBusinessObject {

	private SessionManager sm;
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	protected StudyBean currentStudy;
	protected UserAccountBean ub;

	public RuleExecutionBusinessObject(SessionManager sm, StudyBean currentStudy, UserAccountBean ub) {
		this.sm = sm;
		this.currentStudy = currentStudy;
		this.ub = ub;
	}

	public void runRule(int eventCrfId) {
		// int eventCrfId = 11;
		EventCRFBean eventCrfBean = getEventCRFBean(eventCrfId);
		RuleSetBean ruleSetBean = getRuleSetBean(eventCrfBean);
		ArrayList<RuleBean> rules = getRuleBeans(ruleSetBean);
		for (RuleBean rule : rules) {
			initializeRule(rule);
		}
	}

	public void initializeRule(RuleBean rule) {
		ItemDataBean sourceItemDataBean = null;
		ItemDataBean targetItemDataBean = null;
		boolean sourceResult = true;
		boolean targetResult = true;

		if (sourceResult && targetResult) {
			// We are good
		}
		if (sourceResult == true && targetResult == false) {
			// file a descrepancy Note
			createDiscrepancyNote(rule.toString(), targetItemDataBean, sourceItemDataBean);
		}

	}

	private void createDiscrepancyNote(String description, ItemDataBean targetItemDataBean,
			ItemDataBean sourceItemDataBean) {

		DiscrepancyNoteBean note = new DiscrepancyNoteBean();
		note.setDescription(description);
		note.setDetailedNotes("");
		note.setOwner(ub);
		note.setCreatedDate(new Date());
		note.setResolutionStatusId(1);
		note.setDiscrepancyNoteTypeId(1);
		note.setEntityId(targetItemDataBean.getId());
		note.setEntityType("ItemData");
		note.setColumn("value");
		note.setStudyId(currentStudy.getId());

		DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(sm.getDataSource());
		note = (DiscrepancyNoteBean) discrepancyNoteDao.create(note);
		discrepancyNoteDao.createMapping(note);

	}

	// These are dao mostly calls see how to reduce redundancy
	private EventCRFBean getEventCRFBean(int eventCrfBeanId) {
		EventCRFDAO eventCrfDao = new EventCRFDAO(sm.getDataSource());
		return eventCrfBeanId > 0 ? (EventCRFBean) eventCrfDao.findByPK(eventCrfBeanId) : null;
	}

	private RuleSetBean getRuleSetBean(EventCRFBean eventCrfBean) {
		return null;
	}

	private ArrayList<RuleBean> getRuleBeans(RuleSetBean ruleSet) {
		RuleDAO ruleDao = new RuleDAO(sm.getDataSource());
		return ruleSet != null ? ruleDao.findByRuleSet(ruleSet) : new ArrayList<RuleBean>();
	}

}
