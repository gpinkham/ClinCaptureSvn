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

import java.util.List;

import org.akaza.openclinica.domain.rule.RuleBean;
import org.hibernate.Query;

public class RuleDao extends AbstractDomainDao<RuleBean> {

	@Override
	public Class<RuleBean> domainClass() {
		return RuleBean.class;
	}

	@SuppressWarnings("unchecked")
	public List<RuleBean> findAll() {
		
		String query = "from  " + this.getDomainClassName() + " order by oid asc";
		Query q = this.getCurrentSession().createQuery(query);

		return (List<RuleBean>) q.list();
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
	
	@SuppressWarnings("unchecked")
	public List<String> findRuleOIDs() {
		
		Query q = this.getCurrentSession().createQuery("select oid from " + this.getDomainClassName() + " order by oid desc");

		return (List<String>) q.list();
	}

}
