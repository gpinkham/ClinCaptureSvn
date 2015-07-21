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
package com.clinovo.util;

import javax.sql.DataSource;

import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;

/**
 * DAOWrapper class.
 */
public class DAOWrapper {

	private StudyDAO sdao;
	private EventCRFDAO ecdao;
	private CRFVersionDAO cvdao;
	private StudyEventDAO sedao;
	private StudySubjectDAO ssdao;
	private DiscrepancyNoteDAO discDao;
	private EventDefinitionCRFDAO edcdao;
	private StudyEventDefinitionDAO seddao;

	/**
	 * DAOWrapper constructor.
	 * 
	 * @param ds
	 *            DataSource
	 */
	public DAOWrapper(DataSource ds) {
		this.sdao = new StudyDAO(ds);
		this.ecdao = new EventCRFDAO(ds);
		this.cvdao = new CRFVersionDAO(ds);
		this.sedao = new StudyEventDAO(ds);
		this.ssdao = new StudySubjectDAO(ds);
		this.discDao = new DiscrepancyNoteDAO(ds);
		this.edcdao = new EventDefinitionCRFDAO(ds);
		this.seddao = new StudyEventDefinitionDAO(ds);
	}

	public StudyDAO getSdao() {
		return sdao;
	}

	public StudyEventDAO getSedao() {
		return sedao;
	}

	public StudySubjectDAO getSsdao() {
		return ssdao;
	}

	public EventCRFDAO getEcdao() {
		return ecdao;
	}

	public EventDefinitionCRFDAO getEdcdao() {
		return edcdao;
	}

	public StudyEventDefinitionDAO getSeddao() {
		return seddao;
	}

	public DiscrepancyNoteDAO getDiscDao() {
		return discDao;
	}

	public CRFVersionDAO getCvdao() {
		return cvdao;
	}
}
