/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.control.admin;

import com.clinovo.util.CompleteCRFDeleteUtil;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
@Component
public class CompleteCRFDeleteServlet extends Controller {

	private static final String CRF_ID = "crfId";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {

		UserAccountBean ub = getUserAccountBean(request);

		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		FormProcessor fp = new FormProcessor(request);
		int crfId = fp.getInt(CRF_ID);

		CRFDAO crfDao = getCRFDAO();
		CRFBean crfBean = (CRFBean) crfDao.findByPK(crfId);

		CompleteCRFDeleteUtil.setRuleSetDao(getRuleSetDao());
		CompleteCRFDeleteUtil.setSessionManager(getSessionManager(request));
		CompleteCRFDeleteUtil.validateCRF(crfBean);

		if (crfBean.isDeletable()) {

			crfDao.deleteCrfById(crfId);

			addPageMessage(respage.getString("the_crf_has_been_removed"), request);
			forwardPage(Page.CRF_LIST_SERVLET, request, response);

		} else {

			String keyValue = (String) request.getSession().getAttribute("savedListCRFsUrl");

			if (keyValue != null) {
				Map storedAttributes = new HashMap();
				storedAttributes.put(Controller.PAGE_MESSAGE, request.getAttribute(Controller.PAGE_MESSAGE));
				request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
				try {
					response.sendRedirect(response.encodeRedirectURL(keyValue));
				} catch (IOException e) {
					logger.error("Redirect: " + e.getMessage());
				}
			} else {

				forwardPage(Page.CRF_LIST_SERVLET, request, response);
			}
		}
	}
}
