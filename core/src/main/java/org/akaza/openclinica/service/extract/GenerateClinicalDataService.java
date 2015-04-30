package org.akaza.openclinica.service.extract;

import java.util.LinkedHashMap;
import java.util.Locale;

import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;

/**
 * Service that is used to generate clinical data in ODM format.
 */
public interface GenerateClinicalDataService {

	/**
	 * This is a generic method where the control enters first. Regardless what URL is being used. Depending upon the combination of URL parameters, further course is determined.
	 * @param  studyOID String
	 * @param studySubjectOID String
	 * @param studyEventOID String
	 * @param formVersionOID String
	 * @param collectAudit Boolean
	 * @param locale Locale;
	 * @param userId int
	 * @param collectDNS bollean
	 * @return LinkedHashMap <String, OdmClinicalDataBean>
	 */
    LinkedHashMap<String, OdmClinicalDataBean> getClinicalData(String studyOID, String studySubjectOID, String studyEventOID, String formVersionOID, Boolean collectDNS, Boolean collectAudit, Locale locale, int userId);
}
