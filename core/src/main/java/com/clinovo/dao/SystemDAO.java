/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.dao;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.springframework.stereotype.Repository;

import com.clinovo.model.System;
import com.clinovo.model.SystemGroup;

/**
 * This class is the database interface for system properties.
 * 
 */
@Repository
public class SystemDAO extends AbstractDomainDao<System> {

	@Override
	public Class<System> domainClass() {
		return System.class;
	}

	/**
	 * Retrieves system property by name.
	 * 
	 * @return List of all properties
	 */
	public System findByName(String name) {
		return (System) getCurrentSession().createQuery("from " + getDomainClassName() + " where name = :name")
				.setString("name", name).setCacheable(true).uniqueResult();
	}

	/**
	 * Retrieves all system properties from the database.
	 * 
	 * @return List of all properties
	 */
	@SuppressWarnings("unchecked")
	public List<System> findAll() {
		return getCurrentSession().createQuery("from " + getDomainClassName() + " order by name asc").setCacheable(true)
				.list();
	}

	/**
	 * Retrieves all main groups from the database.
	 * 
	 * @return List of all main groups
	 */
	@SuppressWarnings("unchecked")
	public List<SystemGroup> getAllMainGroups() {
		return getCurrentSession()
				.createQuery("from " + SystemGroup.class.getName() + " where parentId = 0 order by orderId asc")
				.setCacheable(true).list();
	}

	/**
	 * Retrieves all child groups from the database.
	 * 
	 * @return List of all child groups
	 */
	@SuppressWarnings("unchecked")
	public List<SystemGroup> getAllChildGroups(int parentId) {
		return getCurrentSession()
				.createQuery("from " + SystemGroup.class.getName() + " where parentId = :parentId order by orderId asc")
				.setInteger("parentId", parentId).setCacheable(true).list();
	}

	/**
	 * Retrieves all system properties by group id and role.
	 * 
	 * @param groupId
	 *            group id
	 * @param role
	 *            user role
	 * @return List of system properties
	 */
	@SuppressWarnings("unchecked")
	public List<System> getAllProperties(int groupId, Role role) {
		String user = null;
		if (role.equals(Role.SYSTEM_ADMINISTRATOR)) {
			user = "root";
		} else if (role.equals(Role.STUDY_ADMINISTRATOR)) {
			user = "admin";
		} else if (role.equals(Role.STUDY_MONITOR)) {
			user = "monitor";
		} else if (role.equals(Role.INVESTIGATOR)) {
			user = "investigator";
		} else if (role.equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {
			user = "crc";
		}
		return user == null
				? new ArrayList<System>()
				: getCurrentSession()
						.createQuery("from " + getDomainClassName() + " where groupId = :groupId and " + user
								+ " != 'HIDDEN' order by orderId asc")
						.setInteger("groupId", groupId).setCacheable(true).list();
	}
}
