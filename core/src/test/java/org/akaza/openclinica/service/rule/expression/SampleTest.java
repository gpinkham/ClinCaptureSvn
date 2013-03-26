/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
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

package org.akaza.openclinica.service.rule.expression;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;

public class SampleTest extends HibernateOcDbTestCase {

	public SampleTest() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public void testStatement() {
		StudyDAO studyDao = new StudyDAO(getDataSource());
		StudyBean study = (StudyBean) studyDao.findByPK(1);
		assertNotNull(study);
	}
}
