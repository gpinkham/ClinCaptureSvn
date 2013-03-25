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

/**
 * 
 */
package org.akaza.openclinica.dao.hibernate;

import java.sql.Timestamp;

import org.akaza.openclinica.domain.OpenClinicaVersionBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author pgawade
 * 
 */
public class OpenClinicaVersionDAO extends AbstractDomainDao<OpenClinicaVersionBean> {

	private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public Class<OpenClinicaVersionBean> domainClass() {
		return OpenClinicaVersionBean.class;
	}

	@Transactional
	public OpenClinicaVersionBean findDefault() {
		String query = "from " + getDomainClassName() + " ocVersion";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		return (OpenClinicaVersionBean) q.uniqueResult();
	}

	@Transactional
	public void saveOCVersionToDB(String OpenClinicaVersion) {
		logger.debug("OpenClinicaVersionDAO -> saveOCVersionToDB");
		logger.debug("OpenClinicaVersion: " + OpenClinicaVersion);
		// Delete the previous entry if exists in the database
		deleteDefault();
		// Insert new entry
		Timestamp currentTimestamp = new Timestamp(new java.util.Date().getTime());
		OpenClinicaVersionBean openClinicaVersionBean = new OpenClinicaVersionBean();
		openClinicaVersionBean.setName(OpenClinicaVersion);
		openClinicaVersionBean.setUpdate_timestamp(currentTimestamp);
		saveOrUpdate(openClinicaVersionBean);

	}

	@Transactional
	public int deleteDefault() {
		String query = "delete from " + getDomainClassName() + " ocVersion";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		return q.executeUpdate();
	}

}
