package com.clinovo.servlet;

import java.io.PrintWriter;
import java.util.List;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.coding.Search;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.source.impl.BioPortalSearchInterface;

@SuppressWarnings("serial")
public class MedicalCodingServlet extends SecureController {

	private Search search = new Search();
	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)
				|| currentRole.getRole().equals(Role.INVESTIGATOR)
				|| currentRole.getRole().equals(Role.CLINICAL_RESEARCH_COORDINATOR)) {

			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU,
				resexception.getString("not_allowed_access_extract_data_servlet"), "1");
	}

	@Override
	public void processRequest() throws Exception {

		String term = request.getParameter("term");
		String dictionary = CoreResources.getField("dictionary");
		
		PrintWriter writer = response.getWriter();
		if (term != null && term.length() > 0 && dictionary != null && dictionary.length() > 0) {

			search.setSearchInterface(new BioPortalSearchInterface());

			try {

				List<Classification> classifications = search.getClassifications(term, dictionary);
				for (Classification clazz : classifications) {

					writer.write("==============================================================================================================================================\n");
					writer.write("Code: " + clazz.getCode() + "\n");
					writer.write("Term: " + clazz.getTerm() + "\n");
					writer.write("Dictionary: " + clazz.getDictionary() + "\n");
					writer.write("Url: " + clazz.getId() + "\n");
				}
				
				writer.write("==============================================================================================================================================\n");
				writer.flush();

			} catch (Exception ex) {
				
				log.error(ex.getMessage());
				writer.write(ex.getMessage());
				writer.flush();
			}
		} else {
			
			writer.write("You have to specify both the term to search for and the dictionary to search from");
			writer.flush();
		}
	}
}
