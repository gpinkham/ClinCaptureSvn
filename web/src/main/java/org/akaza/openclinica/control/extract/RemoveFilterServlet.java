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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.extract;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.FilterBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.extract.FilterDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.akaza.openclinica.web.bean.FilterRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * <P>
 * The goal here is to provide a small servlet which will change a status from 'available' to 'unavailable' so that it
 * cannot be accessed.
 * 
 * <P>
 * TODO define who can or can't remove a filter; creator only? anyone in the project?
 * 
 * @author thickerson
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class RemoveFilterServlet extends SecureController {

	Locale locale;

	public static final String PATH = "RemoveFilter";
	public static final String ARG_FILTER_ID = "filterId";

	public static String getLink(int filterId) {
		return PATH + '?' + ARG_FILTER_ID + '=' + filterId;
	}

	@Override
	public void processRequest() throws Exception {
		FormProcessor fp = new FormProcessor(request);
		int filterId = fp.getInt("filterId");
		FilterDAO fDAO = new FilterDAO(sm.getDataSource());
		FilterBean filter = (FilterBean) fDAO.findByPK(filterId);

		String action = request.getParameter("action");
		if (resword.getString("remove_this_filter").equalsIgnoreCase(action)) {
			filter.setStatus(Status.DELETED);
			fDAO.update(filter);
			addPageMessage(respage.getString("filter_removed_admin_can_access_and_reverse"));
			EntityBeanTable table = getFilterTable();
			request.setAttribute("table", table);

			forwardPage(Page.CREATE_FILTER_SCREEN_1);
		} else if (resword.getString("cancel").equalsIgnoreCase(action)) {
			EntityBeanTable table = getFilterTable();
			request.setAttribute("table", table);

			forwardPage(Page.CREATE_FILTER_SCREEN_1);
		} else {
			request.setAttribute("filter", filter);
			forwardPage(Page.REMOVE_FILTER);
		}
	}

	@Override
	public void mayProceed() throws InsufficientPermissionException {

		locale = request.getLocale();

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");

	}

	private EntityBeanTable getFilterTable() {
		FormProcessor fp = new FormProcessor(request);
		FilterDAO fdao = new FilterDAO(sm.getDataSource());
		EntityBeanTable table = fp.getEntityBeanTable();

		ArrayList filters = (ArrayList) fdao.findAll();
		// TODO make findAllByProject
		ArrayList filterRows = FilterRow.generateRowsFromBeans(filters);

		String[] columns = { resword.getString("filter_name"), resword.getString("description"),
				resword.getString("created_by"), resword.getString("created_date"), resword.getString("status"),
				resword.getString("actions") };

		table.setColumns(new ArrayList(Arrays.asList(columns)));
		table.hideColumnLink(5);
		table.setQuery("CreateFiltersOne", new HashMap());
		table.setRows(filterRows);
		table.computeDisplay();
		return table;
	}

}
