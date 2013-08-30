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

import org.akaza.openclinica.domain.technicaladmin.AuditUserLoginBean;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository("auditUserLoginDao")
public class AuditUserLoginDao extends AbstractDomainDao<AuditUserLoginBean> {

	@Override
	public Class<AuditUserLoginBean> domainClass() {
		return AuditUserLoginBean.class;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<AuditUserLoginBean> findAll() {
		String query = "from " + getDomainClassName() + " aul order by aul.loginAttemptDate desc ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		return (ArrayList<AuditUserLoginBean>) q.list();
	}

	public int getCountWithFilter(final AuditUserLoginFilter filter) {
		Criteria criteria = getCurrentSession().createCriteria(domainClass());
		criteria = filter.execute(criteria);
		criteria.setProjection(Projections.rowCount()).uniqueResult();
		return ((Long) criteria.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<AuditUserLoginBean> getWithFilterAndSort(final AuditUserLoginFilter filter,
			final AuditUserLoginSort sort, final int rowStart, final int rowEnd) {
		Criteria criteria = getCurrentSession().createCriteria(domainClass());
		criteria = filter.execute(criteria);
		criteria = sort.execute(criteria);
		criteria.setFirstResult(rowStart);
		criteria.setMaxResults(rowEnd - rowStart);
		return (ArrayList<AuditUserLoginBean>) criteria.list();
	}

}
