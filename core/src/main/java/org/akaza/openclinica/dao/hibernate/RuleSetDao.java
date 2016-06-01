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

import java.math.BigInteger;
import java.util.ArrayList;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

/**
 * RuleSetDao.
 */
@SuppressWarnings("unchecked")
public class RuleSetDao extends AbstractDomainDao<RuleSetBean> {

	@Override
	public Class<RuleSetBean> domainClass() {
		return RuleSetBean.class;
	}

	/**
	 * Finds RuleSetBean by id and study.
	 * 
	 * @param id
	 *            Integer
	 * @param study
	 *            StudyBean
	 * @return RuleSetBean
	 */
	public RuleSetBean findById(Integer id, StudyBean study) {
		String query = "from " + getDomainClassName()
				+ " ruleSet  where ruleSet.id = :id and ruleSet.studyId = :studyId ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setInteger("id", id);
		q.setInteger("studyId", study.getId());
		return (RuleSetBean) q.uniqueResult();
	}

	/**
	 * Returns count of RuleSetBean's by study.
	 * 
	 * @param study
	 *            StudyBean
	 * @return Long
	 */
	public Long count(StudyBean study) {
		String query = "select count(*) from " + domainClass().getName() + " ruleSet where ruleSet.studyId = :studyId "
				+ " AND ruleSet.status != :status ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setInteger("studyId", study.getId());
		q.setParameter("status", Status.DELETED);
		return (Long) q.uniqueResult();

	}

	/**
	 * Returns count of RuleSetBean's by filter.
	 * 
	 * @param filter
	 *            ViewRuleAssignmentFilter
	 * @return int
	 */
	public int getCountWithFilter(final ViewRuleAssignmentFilter filter) {
		// Using a sql query because we are referencing objects not managed by hibernate
		String query = "select COUNT(DISTINCT(rs.id)) from rule_set rs "
				+ " left outer join study_event_definition sed on rs.study_event_definition_id = sed.study_event_definition_id "
				+ " left outer join crf_version cv on rs.crf_version_id = cv.crf_version_id "
				+ " left outer join crf c on rs.crf_id = c.crf_id "
				+ " left outer join item i on rs.item_id = i.item_id "
				+ " left outer join item_group ig on rs.item_group_id = ig.item_group_id "
				+ " join rule_expression re on rs.rule_expression_id = re.id "
				+ " join rule_set_rule rsr on rs.id = rsr.rule_set_id  " + " join rule r on r.id = rsr.rule_id "
				+ " join rule_expression rer on r.rule_expression_id = rer.id " + " where ";

		query += filter.execute("");
		org.hibernate.Query q = getCurrentSession().createSQLQuery(query);

		return ((BigInteger) q.uniqueResult()).intValue();
	}

	/**
	 * Returns list of RuleSetBean's by filter and sort.
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
	public ArrayList<RuleSetBean> getWithFilterAndSort(final ViewRuleAssignmentFilter filter,
			final ViewRuleAssignmentSort sort, final int rowStart, final int rowEnd) {

		String query = "select DISTINCT(rs.*) from rule_set rs "
				+ " left outer join study_event_definition sed on rs.study_event_definition_id = sed.study_event_definition_id "
				+ " left outer join crf_version cv on rs.crf_version_id = cv.crf_version_id "
				+ " left outer join crf c on rs.crf_id = c.crf_id "
				+ " left outer join item i on rs.item_id = i.item_id "
				+ " left outer join item_group ig on rs.item_group_id = ig.item_group_id "
				+ " join rule_expression re on rs.rule_expression_id = re.id "
				+ " join rule_set_rule rsr on rs.id = rsr.rule_set_id " + " join rule r on r.id = rsr.rule_id "
				+ " join rule_expression rer on r.rule_expression_id = rer.id " + " where ";

		query += filter.execute("");
		org.hibernate.Query q = getCurrentSession().createSQLQuery(query).addEntity(domainClass());
		q.setFirstResult(rowStart);
		q.setMaxResults(rowEnd - rowStart);
		return (ArrayList<RuleSetBean>) q.list();
	}

	/**
	 * Returns list of RuleSetBean's by crf version or crf and study and study event definition.
	 * 
	 * @param crfVersion
	 *            CRFVersionBean
	 * @param crfBean
	 *            CRFBean
	 * @param currentStudy
	 *            StudyBean
	 * @param sed
	 *            StudyEventDefinitionBean
	 * @return ArrayList
	 */
	@Transactional
	public ArrayList<RuleSetBean> findByCrfVersionOrCrfAndStudyAndStudyEventDefinition(CRFVersionBean crfVersion,
			CRFBean crfBean, StudyBean currentStudy, StudyEventDefinitionBean sed) {
		// Using a sql query because we are referencing objects not managed by hibernate
		String query = " select rs.* from rule_set rs where rs.study_id = :studyId "
				+ " AND (rs.crf_version_id is not null or rs.crf_id is not null)"
				+ " AND (( rs.study_event_definition_id = :studyEventDefinitionId "
				+ " AND (( rs.crf_version_id = :crfVersionId AND rs.crf_id = :crfId ) "
				+ " OR (rs.crf_version_id is null AND rs.crf_id = :crfId ))) OR ( rs.study_event_definition_id is null "
				+ " and rs.item_id in (select item_id from item_form_metadata where crf_version_id = :crfVersionId)  ))";
		Session s = getSessionFactory().openSession();
		org.hibernate.Query q = s.createSQLQuery(query).addEntity(domainClass());

		q.setInteger("crfVersionId", crfVersion.getId());
		q.setInteger("crfId", crfBean.getId());
		q.setInteger("studyId",
				currentStudy.getParentStudyId() != 0 ? currentStudy.getParentStudyId() : currentStudy.getId());
		q.setInteger("studyEventDefinitionId", sed.getId());

		ArrayList<RuleSetBean> results = (ArrayList<RuleSetBean>) q.list();
		s.close();

		return results;

	}

	/**
	 * Returns list of RuleSetBean's by study.
	 * 
	 * @param currentStudy
	 *            StudyBean
	 * @return ArrayList
	 */
	public ArrayList<RuleSetBean> findAllByStudy(StudyBean currentStudy) {
		String query = "from " + getDomainClassName() + " ruleSet  where ruleSet.studyId = :studyId  ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setInteger("studyId", currentStudy.getId());
		return (ArrayList<RuleSetBean>) q.list();
	}

	/**
	 * Returns list of RuleSetBean's by crf.
	 * 
	 * @param crfBean
	 *            CRFBean
	 * @param currentStudy
	 *            StudyBean
	 * @return ArrayList
	 */
	public ArrayList<RuleSetBean> findByCrf(CRFBean crfBean, StudyBean currentStudy) {
		String query = " select rs.* from rule_set rs where rs.study_id = :studyId "
				+ " AND rs.item_id in ( select distinct(item_id) from item_form_metadata ifm,crf_version cv "
				+ " where ifm.crf_version_id = cv.crf_version_id and cv.crf_id = :crfId) ";
		// Using a sql query because we are referencing objects not managed by hibernate
		org.hibernate.Query q = getCurrentSession().createSQLQuery(query).addEntity(domainClass());
		q.setInteger("crfId", crfBean.getId());
		q.setInteger("studyId", currentStudy.getId());
		return (ArrayList<RuleSetBean>) q.list();
	}

	/**
	 * Returns list of RuleSetBean's by crf id and crf oid.
	 * 
	 * @param crfBean
	 *            CRFBean
	 * @return ArrayList
	 */
	public ArrayList<RuleSetBean> findByCrfIdAndCrfOid(CRFBean crfBean) {
		String query = "select distinct rs.* from rule_set rs, rule r, rule_expression re, rule_set_rule rsr left OUTER join rule_action ra on ra.rule_set_rule_id = rsr.id left outer join rule_action_property rap on rap.rule_action_id = ra.id where rsr.rule_set_id = rs.id and rsr.rule_id = r.id and (rs.rule_expression_id = re.id or r.rule_expression_id = re.id)\n"
				+ "and (rs.item_id in (select distinct(item_id) from item_form_metadata ifm, crf_version cv where ifm.crf_version_id = cv.crf_version_id and cv.crf_id = :crfId) or re.value like '%"
				+ crfBean.getOid() + ".%' or rap.oc_oid like '%" + crfBean.getOid()
				+ ".%' or re.target_version_oid in (select cv.oc_oid from crf_version cv where cv.crf_id = :crfId))";
		// Using a sql query because we are referencing objects not managed by hibernate
		org.hibernate.Query q = getCurrentSession().createSQLQuery(query).addEntity(domainClass());
		q.setInteger("crfId", crfBean.getId());
		q.setInteger("crfId", crfBean.getId());
		return (ArrayList<RuleSetBean>) q.list();
	}

	/**
	 * Returns list of RuleSetBean's by crf version id and crf version oid.
	 * 
	 * @param crfVersionBean
	 *            CRFVersionBean
	 * @return ArrayList
	 */
	public ArrayList<RuleSetBean> findByCrfVersionIdAndCrfVersionOid(CRFVersionBean crfVersionBean) {
		String query = "select distinct rs.* from rule_set_rule rsr, rule_set rs, rule r, rule_expression re "
				+ "where rsr.rule_set_id = rs.id " + "and rsr.rule_id = r.id "
				+ "and (rs.rule_expression_id = re.id or r.rule_expression_id = re.id) " + "and (re.value like '%"
				+ crfVersionBean.getOid() + ".%'" + "or ((re.target_version_oid = '" + crfVersionBean.getOid() + "') "
				+ "and (select count(*) from item_form_metadata ifm, rule_set rs where rs.item_id = ifm.item_id) = 1));";
		// Using a sql query because we are referencing objects not managed by hibernate
		org.hibernate.Query q = getCurrentSession().createSQLQuery(query).addEntity(domainClass());
		return (ArrayList<RuleSetBean>) q.list();
	}

	/**
	 * Finds RuleSetBean by expression.
	 * 
	 * @param ruleSet
	 *            RuleSetBean
	 * @return RuleSetBean
	 */
	public RuleSetBean findByExpression(RuleSetBean ruleSet) {
		String query = "from " + getDomainClassName()
				+ " ruleSet  where ruleSet.originalTarget.value = :value AND ruleSet.originalTarget.context = :context ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setString("value", ruleSet.getTarget().getValue());
		q.setParameter("context", ruleSet.getTarget().getContext());
		return (RuleSetBean) q.uniqueResult();
	}

	/**
	 * Finds RuleSetBean by expression and study.
	 * 
	 * @param ruleSet
	 *            RuleSetBean
	 * @param studyId
	 *            Integer
	 * @return RuleSetBean
	 */
	public RuleSetBean findByExpressionAndStudy(RuleSetBean ruleSet, Integer studyId) {
		String query = "from " + getDomainClassName() + " ruleSet  where ruleSet.originalTarget.value = :value "
				+ "AND ruleSet.originalTarget.context = :context " + "AND ruleSet.studyId = :studyId ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setString("value", ruleSet.getTarget().getValue());
		q.setParameter("context", ruleSet.getTarget().getContext());
		q.setInteger("studyId", studyId);
		return (RuleSetBean) q.uniqueResult();
	}

	/**
	 * Unbinds rules from CRFVersion.
	 *
	 * @param crfVersionBean
	 *            CRFVersionBean
	 */
	@Transactional
	public void unbindRulesFromCrfVersion(CRFVersionBean crfVersionBean) {
		String sql = "UPDATE rule_expression SET target_version_oid = NULL WHERE target_version_oid = '"
				.concat(crfVersionBean.getOid()).concat("'");
		Query query = getCurrentSession().createSQLQuery(sql);
		query.executeUpdate();

		sql = "UPDATE rule_set SET crf_id = ".concat(Integer.toString(crfVersionBean.getCrfId()))
				.concat(", crf_version_id = null WHERE crf_version_id = ")
				.concat(Integer.toString(crfVersionBean.getId()));
		query = getCurrentSession().createSQLQuery(sql);
		query.executeUpdate();
	}
}
