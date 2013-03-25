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

import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.domain.technicaladmin.ConfigurationBean;
import org.akaza.openclinica.templates.HibernateOcDbTestCase;

public class ConfigurationDaoTest extends HibernateOcDbTestCase {

	public ConfigurationDaoTest() {
		super();
	}

	public void testSaveOrUpdate() {
		ConfigurationDao configurationDao = (ConfigurationDao) getContext().getBean("configurationDao");
		ConfigurationBean configurationBean = new ConfigurationBean();
		configurationBean.setKey("user.test");
		configurationBean.setValue("test");
		configurationBean.setDescription("Testing attention please");

		configurationBean = configurationDao.saveOrUpdate(configurationBean);

		assertNotNull("Persistant id is null", configurationBean.getId());
	}

	public void testfindById() {
		ConfigurationDao configurationDao = (ConfigurationDao) getContext().getBean("configurationDao");
		ConfigurationBean configurationBean = configurationDao.findById(-1);

		assertEquals("Key should be test.test", "test.test", configurationBean.getKey());
	}

	public void testfindByKey() {
		ConfigurationDao configurationDao = (ConfigurationDao) getContext().getBean("configurationDao");
		ConfigurationBean configurationBean = configurationDao.findByKey("test.test");

		assertEquals("Key should be test.test", "test.test", configurationBean.getKey());
	}
}
