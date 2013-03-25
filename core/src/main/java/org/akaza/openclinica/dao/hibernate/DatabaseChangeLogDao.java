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

import org.akaza.openclinica.domain.technicaladmin.DatabaseChangeLogBean;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

public class DatabaseChangeLogDao {

	private SessionFactory sessionFactory;

	public String getDomainClassName() {
		return domainClass().getName();
	}

	public Class<DatabaseChangeLogBean> domainClass() {
		return DatabaseChangeLogBean.class;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<DatabaseChangeLogBean> findAll() {
		String query = "from " + getDomainClassName() + " dcl order by dcl.id desc ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		return (ArrayList<DatabaseChangeLogBean>) q.list();
	}

	public DatabaseChangeLogBean findById(String id, String author, String fileName) {
		String query = "from " + getDomainClassName()
				+ " do  where do.id = :id and do.author = :author and do.fileName = :fileName ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setString("id", id);
		q.setString("author", author);
		q.setString("fileName", fileName);
		return (DatabaseChangeLogBean) q.uniqueResult();
	}

	public Long count() {
		return (Long) getCurrentSession().createQuery("select count(*) from " + domainClass().getName()).uniqueResult();
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @return Session Object
	 */
	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
}
