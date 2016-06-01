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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.HideActionBean;
import org.akaza.openclinica.domain.rule.action.InsertActionBean;
import org.akaza.openclinica.domain.rule.action.RuleActionBean;
import org.akaza.openclinica.domain.rule.action.ShowActionBean;
import org.hibernate.SQLQuery;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.clinovo.util.SQLUtil;

/**
 * RuleSetRuleDao.
 */
@Repository
@Transactional
@SuppressWarnings("unchecked")
public class RuleSetRuleDao extends AbstractDomainDao<RuleSetRuleBean> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RuleSetRuleDao.class);

	@Autowired
	private DataSource dataSource;

	private CoreResources coreResources;

	public CoreResources getCoreResources() {
		return coreResources;
	}

	public void setCoreResources(CoreResources coreResources) {
		this.coreResources = coreResources;
	}

	@Override
	public Class<RuleSetRuleBean> domainClass() {
		return RuleSetRuleBean.class;
	}

	/**
	 * Returns list of RuleSetRuleBean's by RuleSetBean and RuleBean.
	 * 
	 * @param ruleSetBean
	 *            RuleSetBean
	 * @param ruleBean
	 *            RuleBean
	 * @return ArrayList
	 */
	public ArrayList<RuleSetRuleBean> findByRuleSetBeanAndRuleBean(RuleSetBean ruleSetBean, RuleBean ruleBean) {
		String query = "from " + getDomainClassName() + " ruleSetRule  where ruleSetRule.ruleSetBean = :ruleSetBean"
				+ " AND ruleSetRule.ruleBean = :ruleBean ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setParameter("ruleSetBean", ruleSetBean);
		q.setParameter("ruleBean", ruleBean);
		return (ArrayList<RuleSetRuleBean>) q.list();
	}

	/**
	 * Delete all unused Rule Set Rule Beans, where rule_id is not in the list of rules.
	 */
	public void deleteAllUnused() {
		org.hibernate.Query q = getCurrentSession()
				.createSQLQuery("DELETE FROM rule_set_rule WHERE rule_id NOT IN " + "(SELECT id FROM rule)");
		q.executeUpdate();
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
		LOGGER.trace("EntityRuleSet" + stats.getEntityInsertCount());
		LOGGER.trace(stats.getQueryExecutionMaxTimeQueryString());
		LOGGER.trace("hit count" + stats.getSecondLevelCacheHitCount());
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

	/**
	 * Returns count by filter.
	 * 
	 * @param filter
	 *            ViewRuleAssignmentFilter
	 * @return int
	 */
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

	/**
	 * Returns list of RuleSetRuleBean by filter and sort.
	 * 
	 * @param filter
	 *            ViewRuleAssignmentFilter
	 * @param sort
	 *            ViewRuleAssignmentSort
	 * @param rowStart
	 *            int
	 * @param rowEnd
	 *            int
	 * @return ArrayList
	 */
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
	 * 
	 * @param eventOid
	 *            String
	 * @param crfOIDs
	 *            list of CRF oids.
	 * @return List of RuleSetRuleBeans
	 */
	public List<RuleSetRuleBean> findAllRulesForEventDefinitionCRF(String eventOid, ArrayList<String> crfOIDs) {
		StringBuilder queryBuilder = new StringBuilder(getFindAllRuleSetRulesQuery());
		queryBuilder.append(" ((re.target_event_oid = '").append(eventOid).append("' OR re.value LIKE '%")
				.append(eventOid).append(".%') AND (");
		String splitter = "";
		for (String oid : crfOIDs) {
			queryBuilder.append(splitter).append("re.target_version_oid = '").append(oid)
					.append("' OR re.value LIKE '%").append(oid).append("%'");
			splitter = " OR ";
		}
		queryBuilder.append("))");
		org.hibernate.Query q = getCurrentSession().createSQLQuery(queryBuilder.toString()).addEntity(domainClass());
		return (ArrayList<RuleSetRuleBean>) q.list();
	}

	/**
	 * Returns list of RuleSetRuleBean's by crf.
	 * 
	 * @param crfBean
	 *            CRFBean
	 * @return List
	 */
	public List<RuleSetRuleBean> findAllByCrf(CRFBean crfBean) {
		List<CRFVersionBean> crfVersionBeanList = (List<CRFVersionBean>) new CRFVersionDAO(dataSource)
				.findAllByCRFId(crfBean.getId());
		StringBuilder sqlBuilder = new StringBuilder("");
		sqlBuilder.append("select distinct rsr.* from rule_set_rule rsr ")
				.append("join rule r on r.id =  rsr.rule_id ")
				.append("join rule_set rs on rs.id = rsr.rule_set_id ")
				.append("join rule_expression re1 on re1.id = rs.rule_expression_id ")
				.append("join rule_expression re2 on re2.id = r.rule_expression_id ")
				.append("left join rule_action ra on ra.rule_set_rule_id = rsr.id ")
				.append("left join rule_action_property rap on rap.rule_action_id = ra.id ")
				.append("left join crf_version cv1 on cv1.oc_oid = re1.target_version_oid ")
				.append("left join crf_version cv2 on cv2.crf_version_id = rs.crf_version_id ")
				.append("where cv1.crf_id = :crfId or cv2.crf_id = :crfId or rs.crf_id = :crfId ");
		sqlBuilder.append(" or rap.oc_oid like ").append("'%".concat(crfBean.getOid()).concat(".%'"));
		sqlBuilder.append(" or re2.value like ").append("'%".concat(crfBean.getOid()).concat(".%'"));
		for (CRFVersionBean crfVersionBean : crfVersionBeanList) {
			sqlBuilder.append(" or re2.value like ").append("'%".concat(crfVersionBean.getOid()).concat(".%'"));
		}
		SQLQuery query = getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameter("crfId", crfBean.getId());
		query.addEntity(RuleSetRuleBean.class);
		return (List<RuleSetRuleBean>) query.list();
	}

	/**
	 * Returns list of RuleSetRuleBean's by crf version.
	 *
	 * @param crfVersionBean
	 *            CRFVersionBean
	 * @return List
	 */
	public List<RuleSetRuleBean> findAllByCrfVersion(CRFVersionBean crfVersionBean) {
		StringBuilder sqlBuilder = new StringBuilder("");
		sqlBuilder.append("select distinct rsr.* from rule_set_rule rsr ")
				.append("join rule r on r.id =  rsr.rule_id ")
				.append("join rule_set rs on rs.id = rsr.rule_set_id ")
				.append("join rule_expression re1 on re1.id = rs.rule_expression_id ")
				.append("join rule_expression re2 on re2.id = r.rule_expression_id ")
				.append("where re1.value like ").append("'%".concat(crfVersionBean.getOid()).concat(".%'"))
				.append(" or re2.value like ").append("'%".concat(crfVersionBean.getOid()).concat(".%'"));
		SQLQuery query = getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.addEntity(RuleSetRuleBean.class);
		return (List<RuleSetRuleBean>) query.list();
	}

	/**
	 * Deletes all rules by crf.
	 * 
	 * @param crfBean
	 *            CRFBean
	 */
	@Transactional
	public void deleteAllRulesByCrf(CRFBean crfBean) {
		deleteAllRules(findAllByCrf(crfBean));
	}

	/**
	 * Deletes all rules by crf version.
	 *
	 * @param crfVersionBean
	 *            CRFVersionBean
	 */
	@Transactional
	public void deleteAllRulesByCrfVersion(CRFVersionBean crfVersionBean) {
		deleteAllRules(findAllByCrfVersion(crfVersionBean));
	}

	private void deleteAllRules(List<RuleSetRuleBean> ruleSetRuleBeanList) {
		if (ruleSetRuleBeanList.size() > 0) {
			Set<Integer> ruleIds = new HashSet<Integer>();
			Set<Integer> ruleSetIds = new HashSet<Integer>();
			Set<Integer> ruleSetRuleIds = new HashSet<Integer>();
			Set<Integer> ruleExpressionIds = new HashSet<Integer>();
			
			for (RuleSetRuleBean ruleSetRuleBean : ruleSetRuleBeanList) {
				ruleSetRuleIds.add(ruleSetRuleBean.getId());
				ruleIds.add(ruleSetRuleBean.getRuleBean().getId());
				ruleExpressionIds.add(ruleSetRuleBean.getRuleBean().getExpression().getId());
			}

			for (RuleSetRuleBean ruleSetRuleBean : ruleSetRuleBeanList) {
				boolean deleteRuleSet = true;
				for (RuleSetRuleBean rsrBean : ruleSetRuleBean.getRuleSetBean().getRuleSetRules()) {
					if (!ruleSetRuleIds.contains(rsrBean.getId())) {
						deleteRuleSet = false;
						break;
					}
				}
				if (deleteRuleSet) {
					ruleSetIds.add(ruleSetRuleBean.getRuleSetBean().getId());
					ruleExpressionIds.add(ruleSetRuleBean.getRuleSetBean().getOriginalTarget().getId());
				}
			}

			String sql = "delete from rule_action_run_log where rule_oc_oid in (select distinct r.oc_oid from rule_set_rule rsr "
					+ "join rule r on r.id = rsr.rule_id "
					+ "where rsr.id in :ruleSetRuleIds); "

					+ "delete from rule_action_property where id in (select distinct rap.id from rule_set_rule rsr "
					+ "join rule_action ra on ra.rule_set_rule_id = rsr.id "
					+ "join rule_action_property rap on rap.rule_action_id = ra.id "
					+ "where rsr.id in :ruleSetRuleIds); "

					+ "delete from rule_action_run where id in (select distinct ra.rule_action_run_id from rule_set_rule rsr "
					+ "join rule_action ra on ra.rule_set_rule_id = rsr.id "
					+ "where rsr.id in :ruleSetRuleIds); "

					+ "delete from rule_action where id in (select distinct ra.id from rule_set_rule rsr "
					+ "join rule_action ra on ra.rule_set_rule_id = rsr.id "
					+ "where rsr.id in :ruleSetRuleIds); "

					+ "delete from rule_set_audit where id in (select distinct rsa.id from rule_set_rule rsr "
					+ "join rule_set_audit rsa on rsa.rule_set_id = rsr.rule_set_id "
					+ "where rsr.id in :ruleSetRuleIds); "

					+ "delete from rule_set_rule_audit where id in (select distinct rsra.id from rule_set_rule rsr "
					+ "join rule_set_rule_audit rsra on rsra.rule_set_rule_id = rsr.id "
					+ "where rsr.id in :ruleSetRuleIds); "

					+ "delete from rule_set_rule where id in :ruleSetRuleIds; "

					+ "delete from rule_set where id in :ruleSetIds; "

					+ "delete from rule where id in :ruleIds; "

					+ "delete from rule_expression where id in :ruleExpressionIds;";

			sql = sql.replaceAll(":ruleIds", SQLUtil.asInClause(ruleIds));
			sql = sql.replaceAll(":ruleSetIds", SQLUtil.asInClause(ruleSetIds));
			sql = sql.replaceAll(":ruleSetRuleIds", SQLUtil.asInClause(ruleSetRuleIds));
			sql = sql.replaceAll(":ruleExpressionIds", SQLUtil.asInClause(ruleExpressionIds));

			getCurrentSession().createSQLQuery(sql).executeUpdate();
		}
	}

	/**
	 * Returns count by study.
	 * 
	 * @param study
	 *            StudyBean
	 * @return int
	 */
	public int getCountByStudy(StudyBean study) {
		int studyId = study.getParentStudyId() > 0 ? study.getParentStudyId() : study.getId();
		String query = "select COUNT(*) from rule_set_rule rsr " + " join rule_set rs on rs.id = rsr.rule_set_id "
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

	private String getFindAllRuleSetRulesQuery() {
		String select = "select DISTINCT(rsr.id),rsr.rule_set_id,rsr.rule_id,rsr.owner_id,rsr.date_created, rsr.date_updated, rsr.update_id, rsr.status_id,rsr.version,i.name as iname,re.value as revalue,sed.name as sedname,c.name as cname,cv.name as cvname,ig.name as igname,rer.value as rervalue,r.oc_oid as rocoid,r.description as rdescription,r.name as rname from rule_set_rule rsr ";
		if ("oracle".equalsIgnoreCase(CoreResources.getDBType())) {
			select = "select DISTINCT(rsr.id),rsr.rule_set_id,rsr.rule_id,rsr.owner_id,rsr.date_created, rsr.date_updated, rsr.update_id, rsr.status_id,rsr.version,i.name iname,re.value revalue,sed.name sedname,c.name cname,cv.name cvname,ig.name igname,rer.value rervalue,r.oc_oid rocoid,r.description rdescription,r.name rname from rule_set_rule rsr ";
		}
		return select + " join rule_set rs on rs.id = rsr.rule_set_id "
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
