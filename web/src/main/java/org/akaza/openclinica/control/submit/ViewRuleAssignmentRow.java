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

/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.web.domain.EntityBeanRow;

import java.util.ArrayList;

/**
 * A help class for ListCRF view to display CRF objects in show table class
 * 
 * @author ywang
 * @author jxu
 * 
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ViewRuleAssignmentRow extends EntityBeanRow {
	// columns:
	public static final int COL_NAME = 0;

	public static final int COL_CRF = 1;

	public static final int COL_GROUP = 2;

	public static final int COL_ITEM = 3;

	@Override
	protected int compareColumn(Object row, int sortingColumn) {
		if (!row.getClass().equals(ViewRuleAssignmentRow.class)) {
			return 0;
		}

		RuleSetBean thisRuleSet = (RuleSetBean) bean;
		RuleSetBean argRuleSet = (RuleSetBean) ((ViewRuleAssignmentRow) row).bean;

		int answer = 0;
		switch (sortingColumn) {
		case COL_CRF:
			answer = thisRuleSet.getCrfWithVersionName().toLowerCase()
					.compareTo(argRuleSet.getCrfWithVersionName().toLowerCase());
			break;
		case COL_GROUP:
			answer = thisRuleSet.getGroupLabel().toLowerCase().compareTo(argRuleSet.getGroupLabel().toLowerCase());
			break;
		case COL_ITEM:
			answer = thisRuleSet.getItemName().toLowerCase().compareTo(argRuleSet.getItemName().toLowerCase());
			break;
		}

		return answer;
	}

	@Override
	public String getSearchString() {
		RuleSetBean thisRuleSet = (RuleSetBean) bean;
		return thisRuleSet.getStudyEventDefinitionName() + " " + thisRuleSet.getGroupLabel() + " "
				+ thisRuleSet.getCrfWithVersionName() + " " + thisRuleSet.getItemName();
	}

	@Override
	public ArrayList generatRowsFromBeans(ArrayList beans) {
		return ViewRuleAssignmentRow.generateRowsFromBeans(beans);
	}

	public static ArrayList generateRowsFromBeans(ArrayList<RuleSetBean> beans) {
		ArrayList answer = new ArrayList();

		for (int i = 0; i < beans.size(); i++) {
			try {
				ViewRuleAssignmentRow row = new ViewRuleAssignmentRow();
				row.setBean(beans.get(i));
				answer.add(row);
			} catch (Exception e) {
			}
		}

		return answer;
	}
}
