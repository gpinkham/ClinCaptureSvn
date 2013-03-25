/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.dao;

import org.akaza.openclinica.dao.hibernate.DatabaseChangeLogDao;
import org.akaza.openclinica.domain.technicaladmin.DatabaseChangeLogBean;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;

import java.util.ArrayList;

public class DatabaseChangeLogDaoTest extends HibernateOcDbTestCase {

	private final Integer POSTGRES_COUNT = 833;
	private final Integer ORACLE_COUNT = 833;

	public DatabaseChangeLogDaoTest() {
		super();
	}

	public void testCount() {
		DatabaseChangeLogDao databaseChangeLogDao = (DatabaseChangeLogDao) getContext().getBean("databaseChangeLogDao");
		Long count = databaseChangeLogDao.count();

		if (getDbName().equals("postgres")) {
			assertEquals("Total Count should be", String.valueOf(POSTGRES_COUNT), String.valueOf(count));
		}
		if (getDbName().equals("oracle")) {
			assertEquals("Total Count should be", String.valueOf(ORACLE_COUNT), String.valueOf(count));
		}

	}

	public void testfindAll() {
		DatabaseChangeLogDao databaseChangeLogDao = (DatabaseChangeLogDao) getContext().getBean("databaseChangeLogDao");
		DatabaseChangeLogBean databaseChangeLogBean = null;
		ArrayList<DatabaseChangeLogBean> databaseChangeLogBeans = databaseChangeLogDao.findAll();
		databaseChangeLogBean = databaseChangeLogBeans.get(0);

		if (getDbName().equals("postgres")) {
			assertEquals("Total Count should be", String.valueOf(POSTGRES_COUNT),
					String.valueOf(databaseChangeLogBeans.size()));
		}
		if (getDbName().equals("oracle")) {
			assertEquals("Total Count should be", String.valueOf(ORACLE_COUNT),
					String.valueOf(databaseChangeLogBeans.size()));
		}
		assertNotNull(databaseChangeLogBean);

	}

	public void testfindById() {
		DatabaseChangeLogDao databaseChangeLogDao = (DatabaseChangeLogDao) getContext().getBean("databaseChangeLogDao");
		DatabaseChangeLogBean databaseChangeLogBean = null;
		databaseChangeLogBean = databaseChangeLogDao.findById("1235684743487-1", "pgawade (generated)",
				"migration/2.5/changeLogCreateTables.xml");

		assertNotNull(databaseChangeLogBean);
		assertEquals("Author should be pgawade (generated)", "pgawade (generated)", databaseChangeLogBean.getAuthor());

	}

}
