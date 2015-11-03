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

package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.HideActionBean;
import org.akaza.openclinica.domain.rule.action.InsertActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.ShowActionBean;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class RuleSetRuleDao extends AbstractDomainDao<RuleSetRuleBean> {

	private CoreResources coreResources;
	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	@Override
	public Class<RuleSetRuleBean> domainClass() {
		return RuleSetRuleBean.class;
	}

	public ArrayList<RuleSetRuleBean> findByRuleSetBeanAndRuleBean(RuleSetBean ruleSetBean, RuleBean ruleBean) {
		String query = "from " + getDomainClassName() + " ruleSetRule  where ruleSetRule.ruleSetBean = :ruleSetBean"
				+ " AND ruleSetRule.ruleBean = :ruleBean ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setParameter("ruleSetBean", ruleSetBean);
		q.setParameter("ruleBean", ruleBean);
		return (ArrayList<RuleSetRuleBean>) q.list();
	}

	/**
	 * Use this method carefully as we force an eager fetch. It is also annotated with Transactional so it can be called
	 * from Quartz threads.
	 * 
	 * @param studyId
	 *            Integer
	 * @return List of RuleSetRuleBeans
	 */
	public ArrayList<RuleSetRuleBean> findByRuleSetStudyIdAndStatusAvail(Integer studyId) {
		String query = "from " + getDomainClassName()
				+ " ruleSetRule  where ruleSetRule.ruleSetBean.studyId = :studyId and status = :status ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);

		q.setInteger("studyId", studyId);
		q.setParameter("status", org.akaza.openclinica.domain.Status.AVAILABLE);

		q.setCacheable(true);
		q.setCacheRegion(getDomainClassName());
		// JN: enabling statistics for hibernate queries etc... to monitor the performance

		Statistics stats = getSessionFactory().getStatistics();
		logger.trace("EntityRuleSet" + stats.getEntityInsertCount());
		logger.trace(stats.getQueryExecutionMaxTimeQueryString());
		logger.trace("hit count" + stats.getSecondLevelCacheHitCount());
		stats.logSummary();

		ArrayList<RuleSetRuleBean> ruleSetRules = (ArrayList<RuleSetRuleBean>) q.list();
		// Forcing eager fetch of actions & their properties
		for (RuleSetRuleBean ruleSetRuleBean : ruleSetRules) {
			for (RuleActionBean action : ruleSetRuleBean.getActions()) {
				if (action instanceof InsertActionBean) {
					((InsertActionBean) action).getProperties().size();
				}
				if (action instanceof ShowActionBean) {
					((ShowActionBean) action).getProperties().size();
				}
				if (action instanceof HideActionBean) {
					((HideActionBean) action).getProperties().size();
				}
			}
		}
		return ruleSetRules;
	}

	public int getCountWithFilter(final ViewRuleAssignmentFilter filter) {

		// Using a sql query because we are referencing objects not managed by hibernate
		String query = "select COUNT(DISTINCT(rsr.id)) from rule_set_rule rsr "
				+ " join rule_set rs on rs.id = rsr.rule_set_id "
				+ " left outer join study_event_definition sed on rs.study_event_definition_id = sed.study_event_definition_id "
				+ " left outer join crf_version cv on rs.crf_version_id = cv.crf_version_id and cv.status_id = 1 "
				+ " left outer join (SELECT c2.* FROM crf_version cv2 join crf c2 on c2.crf_id = cv2.crf_id where cv2.status_id = 1) c on rs.crf_id = c.crf_id and c.status_id = 1 "
				+ " left outer join item i on rs.item_id = i.item_id "
				+ " left outer join item_group ig on rs.item_group_id = ig.item_group_id "
				+ " join rule_expression re on rs.rule_expression_id = re.id " + " join rule r on r.id = rsr.rule_id "
				+ " join rule_expression rer on r.rule_expression_id = rer.id "
				+ " join rule_action ra on ra.rule_set_rule_id = rsr.id "
				+ " left join event_definition_crf related_edc on related_edc.parent_id is null and related_edc.crf_id = c.crf_id and related_edc.study_event_definition_id = rs.study_event_definition_id and related_edc.status_id = 1 and related_edc.study_id = rs.study_id "
				+ " left join event_definition_crf actual_edc on actual_edc.parent_id is null and actual_edc.crf_id = c.crf_id and actual_edc.status_id = 1 and actual_edc.study_id = rs.study_id "
				+ " where ";

		query += filter.execute("");
		query += " and ((rs.study_event_definition_id is null and actual_edc.event_definition_crf_id is not null) or (rs.study_event_definition_id is not null and related_edc.event_definition_crf_id is not null)) ";
		org.hibernate.Query q = getCurrentSession().createSQLQuery(query);

		return ((Number) q.uniqueResult()).intValue();
	}

	public ArrayList<RuleSetRuleBean> getWithFilterAndSort(final ViewRuleAssignmentFilter filter,
			final ViewRuleAssignmentSort sort, final int rowStart, final int rowEnd) {

		String query = getFindAllRuleSetRulesQuery();
		query += filter.execute("");
		query += " and ((rs.study_event_definition_id is null and actual_edc.event_definition_crf_id is not null) or (rs.study_event_definition_id is not null and related_edc.event_definition_crf_id is not null)) ";
		query += sort.execute("");
		org.hibernate.Query q = getCurrentSession().createSQLQuery(query).addEntity(domainClass());
		q.setFirstResult(rowStart);
		q.setMaxResults(rowEnd - rowStart);
		return (ArrayList<RuleSetRuleBean>) q.list();
	}

	/**
	 * Find all ruleSetRules for some EventDefinitionCRF by event oid and CRF Id.
	 * @param eventOid String
	 * @param crfOIDs list of CRF oids.
	 * @return List of RuleSetRuleBeans
	 */
	public List<RuleSetRuleBean> findAllRulesForEventDefinitionCRF(String eventOid, ArrayList<String> crfOIDs) {
		String query = getFindAllRuleSetRulesQuery();
		query += " ((re.target_event_oid = '" + eventOid + "' OR re.value LIKE '%" + eventOid + ".%') AND (";
		int index = 0;
		for (String oid : crfOIDs) {
			query += (index == 0 ? "" : " OR ") + "re.target_version_oid = '" + oid + "' OR re.value LIKE '%" + oid + "%'";
			index++;
		}
		query += "))";
		org.hibernate.Query q = getCurrentSession().createSQLQuery(query).addEntity(domainClass());
		return (ArrayList<RuleSetRuleBean>) q.list();
	}

	public int getCountByStudy(StudyBean study) {
		int studyId = study.getParentStudyId() > 0 ? study.getParentStudyId() : study.getId();
		String query = "select COUNT(*) from rule_set_rule rsr "
				+ " join rule_set rs on rs.id = rsr.rule_set_id "
				+ " left outer join study_event_definition sed on rs.study_event_definition_id = sed.study_event_definition_id "
				+ " left outer join crf_version cv on rs.crf_version_id = cv.crf_version_id "
				+ " left outer join crf c on rs.crf_id = c.crf_id "
				+ " left outer join item i on rs.item_id = i.item_id "
				+ " left outer join item_group ig on rs.item_group_id = ig.item_group_id "
				+ " join rule_expression re on rs.rule_expression_id = re.id " + " join rule r on r.id = rsr.rule_id "
				+ " join rule_expression rer on r.rule_expression_id = rer.id "
				+ " join rule_action ra on ra.rule_set_rule_id = rsr.id " + " where rs.study_id = " + studyId
				+ "  AND  rsr.status_id = 1";
		// adding parent study, probably should be a separate method?

		org.hibernate.Query q = getCurrentSession().createSQLQuery(query);
		return ((Number) q.uniqueResult()).intValue();
	}

	public CoreResources getCoreResources() {
		return coreResources;
	}

	public void setCoreResources(CoreResources coreResources) {
		this.coreResources = coreResources;
	}

	private String getFindAllRuleSetRulesQuery() {
		String select = "select DISTINCT(rsr.id),rsr.rule_set_id,rsr.rule_id,rsr.owner_id,rsr.date_created, rsr.date_updated, rsr.update_id, rsr.status_id,rsr.version,i.name as iname,re.value as revalue,sed.name as sedname,c.name as cname,cv.name as cvname,ig.name as igname,rer.value as rervalue,r.oc_oid as rocoid,r.description as rdescription,r.name as rname from rule_set_rule rsr ";
		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			select = "select DISTINCT(rsr.id),rsr.rule_set_id,rsr.rule_id,rsr.owner_id,rsr.date_created, rsr.date_updated, rsr.update_id, rsr.status_id,rsr.version,i.name iname,re.value revalue,sed.name sedname,c.name cname,cv.name cvname,ig.name igname,rer.value rervalue,r.oc_oid rocoid,r.description rdescription,r.name rname from rule_set_rule rsr ";
		}
		return select
				+ " join rule_set rs on rs.id = rsr.rule_set_id "
				+ " left outer join study_event_definition sed on rs.study_event_definition_id = sed.study_event_definition_id "
				+ " left outer join crf_version cv on rs.crf_version_id = cv.crf_version_id and cv.status_id = 1 "
				+ " left outer join (SELECT c2.* FROM crf_version cv2 join crf c2 on c2.crf_id = cv2.crf_id where cv2.status_id = 1) c on rs.crf_id = c.crf_id and c.status_id = 1 "
				+ " left outer join item i on rs.item_id = i.item_id "
				+ " left outer join item_group ig on rs.item_group_id = ig.item_group_id "
				+ " join rule_expression re on rs.rule_expression_id = re.id " + " join rule r on r.id = rsr.rule_id "
				+ " join rule_expression rer on r.rule_expression_id = rer.id "
				+ " join rule_action ra on ra.rule_set_rule_id = rsr.id "
				+ " left join event_definition_crf related_edc on related_edc.parent_id is null and related_edc.crf_id = c.crf_id and related_edc.study_event_definition_id = rs.study_event_definition_id and related_edc.status_id = 1 and related_edc.study_id = rs.study_id "
				+ " left join event_definition_crf actual_edc on actual_edc.parent_id is null and actual_edc.crf_id = c.crf_id and actual_edc.status_id = 1 and actual_edc.study_id = rs.study_id "
				+ " where ";
	}
}
