package org.akaza.openclinica.util;

import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Util to manage Event Definition CRFs.
 */
public final class EventDefinitionCRFUtil {
	private EventDefinitionCRFUtil() {
	}

	/**
	 * Method that will update default version of CRF if current is assigned to event.
	 * @param ds DataSource
	 * @param deletedCRFVersionId int
	 */
	public static void setDefaultCRFVersionInsteadOfDeleted(DataSource ds, int deletedCRFVersionId) {

		EventDefinitionCRFDAO eventCRFDAO = new EventDefinitionCRFDAO(ds);
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(ds);
		ArrayList<EventDefinitionCRFBean> crfs = eventCRFDAO.findByDefaultVersion(deletedCRFVersionId);

		for (EventDefinitionCRFBean crf : crfs) {
			CRFVersionBean latestVersion = crfVersionDAO.findLatestAfterDeleted(deletedCRFVersionId);
			if (latestVersion != null) {
				crf.setDefaultVersionId(latestVersion.getId());
				crf.setDefaultVersionName(latestVersion.getName());
				eventCRFDAO.update(crf);
			}
		}
	}
}
