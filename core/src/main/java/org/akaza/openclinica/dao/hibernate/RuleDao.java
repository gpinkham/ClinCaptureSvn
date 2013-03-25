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

package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.domain.rule.RuleBean;

public class RuleDao extends AbstractDomainDao<RuleBean> {

	@Override
	public Class<RuleBean> domainClass() {
		return RuleBean.class;
	}

	public RuleBean findByOid(RuleBean ruleBean) {
		String query = "from " + getDomainClassName() + " rule  where rule.oid = :oid and  rule.studyId = :studyId ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setString("oid", ruleBean.getOid());
		q.setInteger("studyId", ruleBean.getStudyId());
		return (RuleBean) q.uniqueResult();
	}

	public RuleBean findByOid(String oid, Integer studyId) {
		String query = "from " + getDomainClassName() + " rule  where rule.oid = :oid and  rule.studyId = :studyId ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setString("oid", oid);
		q.setInteger("studyId", studyId);
		return (RuleBean) q.uniqueResult();
	}

}
