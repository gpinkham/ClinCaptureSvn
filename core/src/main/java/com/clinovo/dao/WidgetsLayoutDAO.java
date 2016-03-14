/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.dao;

import java.util.List;

import org.akaza.openclinica.dao.hibernate.AbstractDomainDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.clinovo.model.WidgetsLayout;

/**
 * This class is the database interface for widgets displayed on the home page.
 */

@Repository
@SuppressWarnings("unchecked")
public class WidgetsLayoutDAO extends AbstractDomainDao<WidgetsLayout> {

	@Override
	public Class<WidgetsLayout> domainClass() {
		return WidgetsLayout.class;
	}

	/**
	 * Find all widgets layouts for specific user and study
	 * 
	 * @param studyId
	 *            The ID of Study to filter on.
	 * @param userId
	 *            The ID of User dictionary to filter on.
	 * 
	 * @return List of WidgetsLayouts
	 */
	public List<WidgetsLayout> findAllByStudyIdAndUserId(int studyId, int userId) {
		String query = "from " + getDomainClassName()
				+ " where studyId = :studyId and userId = :userId order by ordinal";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("studyId", studyId);
		q.setInteger("userId", userId);

		return (List<WidgetsLayout>) q.setCacheable(true).list();
	}

	/**
	 * Find widgets layout by widget ID for specific user and study
	 * 
	 * @param widgetId
	 *            The ID of widget to filter on.
	 * @param studyId
	 *            The ID of Study to filter on.
	 * @param userId
	 *            The ID of User dictionary to filter on.
	 * 
	 * @return WidgetsLayout
	 */
	public WidgetsLayout findByWidgetIdAndStudyIdAndUserId(int widgetId, int studyId, int userId) {
		String query = "from " + getDomainClassName()
				+ " where studyId = :studyId and userId = :userId  and widget_id = :widgetId order by ordinal";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("studyId", studyId);
		q.setInteger("userId", userId);
		q.setInteger("widgetId", widgetId);

		return (WidgetsLayout) q.setCacheable(true).uniqueResult();
	}

	/**
	 * Save layout of all widgets for specific user and study
	 * 
	 * @param widgetsLayout
	 *            The list of widgetsLayots that should be saved to database
	 */
	public void saveLayout(List<WidgetsLayout> widgetsLayout) {
		for (int i = 0; i < widgetsLayout.size(); i++) {
			super.saveOrUpdate(widgetsLayout.get(i));
		}
	}
}
