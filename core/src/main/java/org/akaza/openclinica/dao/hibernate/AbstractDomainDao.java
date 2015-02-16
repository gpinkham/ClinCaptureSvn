/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
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

import java.sql.Connection;

import org.akaza.openclinica.domain.DomainObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class AbstractDomainDao<T extends DomainObject> {

    @Autowired
    private SessionFactory sessionFactory;

    public abstract Class<T> domainClass();

    public String getDomainClassName() {
        return domainClass().getName();
    }

    @SuppressWarnings("unchecked")
    public T findById(Integer id) {
        getSessionFactory().getStatistics().logSummary();
        String query = "from " + getDomainClassName() + " do  where do.id = :id";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setInteger("id", id);
        return (T) q.uniqueResult();
    }

    @Transactional
    public T saveOrUpdate(T domainObject, Connection con) {
        getSessionFactory().getStatistics().logSummary();
        getSessionForTransaction(con).saveOrUpdate(domainObject);
        return domainObject;
    }

    @Transactional
    public T saveOrUpdate(T domainObject) {
        getSessionFactory().getStatistics().logSummary();
        getCurrentSession().saveOrUpdate(domainObject);
        return domainObject;
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
     * @param sessionFactory the sessionFactory to set
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

    protected Session getSessionForTransaction(Connection con) {
        if (con != null) {
            return sessionFactory.openSession(con);
        } else {
            return sessionFactory.getCurrentSession();
        }

    }

    @Transactional
    public T findByColumnName(Object id, String key) {
        String query = "from " + getDomainClassName() + " do where do." + key + "= ?";
        org.hibernate.Query q = getCurrentSession().createQuery(query);
        q.setParameter(0, id);
        return (T) q.uniqueResult();
    }
}
