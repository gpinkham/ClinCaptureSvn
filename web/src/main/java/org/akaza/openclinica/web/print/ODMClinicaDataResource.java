package org.akaza.openclinica.web.print;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;

import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.print.JSONClinicalDataPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.service.MetaDataService;

/**
 * * Rest service for ODM clinical data usage.
 */
@Controller
@RequestMapping("/clinicaldata")
@Scope("prototype")
public class ODMClinicaDataResource extends SpringController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ODMClinicaDataResource.class);

	private static final int INDENT_LEVEL = 2;

	@Autowired
	private MetaDataService metaDataService;

	/**
	 * Returns json object with data.
	 *
	 * @param studyOID
	 *            the study oid.
	 * @param formVersionOID
	 *            ecrf version oid.
	 * @param studyEventOID
	 *            study event oid.
	 * @param studySubjectIdentifier
	 *            study subject oid.
	 * @param includeDns
	 *            include discrepancy notes flag
	 * @param includeAudits
	 *            include audit logs flag
	 * @param localizeDatesToUserTZ
	 *            flag, that determines, if dates in report should be printed in user time zone
	 * @param request
	 *            the request object.
	 * @return the string with json object.
	 */
	@RequestMapping(value = "/json/view/{studyOID}/{studySubjectIdentifier}/{studyEventOID}/{formVersionOID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
			+ ";charset=UTF-8")
	@ResponseBody
	public String getODMClinicaldata(@PathVariable("studyOID") String studyOID,
			@PathVariable("formVersionOID") String formVersionOID, @PathVariable("studyEventOID") String studyEventOID,
			@PathVariable("studySubjectIdentifier") String studySubjectIdentifier,
			@RequestParam(value = "includeDNs", defaultValue = "n") String includeDns,
			@RequestParam(value = "includeAudits", defaultValue = "n") String includeAudits,
			@RequestParam(value = "localizeDatesToUserTZ", defaultValue = "no") String localizeDatesToUserTZ,
			HttpServletRequest request) {
		LOGGER.debug("Requesting clinical data resource");
		boolean addDNs = includeDns.trim().equalsIgnoreCase("yes") || includeDns.trim().equalsIgnoreCase("y");
		boolean addAudits = includeAudits.trim().equalsIgnoreCase("yes") || includeAudits.trim().equalsIgnoreCase("y");
		String odmXML = metaDataService.getXML(getUserAccountBean(), studyOID, studySubjectIdentifier, studyEventOID,
				formVersionOID, addDNs, addAudits, "yes".equalsIgnoreCase(localizeDatesToUserTZ),
				LocaleResolver.getLocale());
		XMLSerializer xmlSerializer = new XMLSerializer();
		xmlSerializer.setTypeHintsEnabled(true);
		JSON json = xmlSerializer.read(odmXML);
		JSONClinicalDataPostProcessor processor = new JSONClinicalDataPostProcessor(LocaleResolver.getLocale(request));
		processor.process(json);
		return json.toString(INDENT_LEVEL);
	}

	/**
	 * Returns print page
	 *
	 * @param request
	 *            the request object.
	 * @param studyOID
	 *            the current study oid.
	 * @param studySubjectIdentifier
	 *            the study subject identifier
	 * @param eventOID
	 *            the event oid.
	 * @param formVersionOID
	 *            the form version oid.
	 * @param includeDns
	 *            the flag for include discrepancy notes.
	 * @param includeAudits
	 *            the flag for include audit notes.
	 * @return the page with print fields.
	 * @throws Exception
	 *             for all exceptions.
	 */
	@RequestMapping(value = "/html/print/{studyOID}/{studySubjectIdentifier}/{eventOID}/{formVersionOID}", method = RequestMethod.GET)
	@ResponseBody
	public String getPrintCRFController(HttpServletRequest request, @PathVariable("studyOID") String studyOID,
			@PathVariable("studySubjectIdentifier") String studySubjectIdentifier,
			@PathVariable("eventOID") String eventOID, @PathVariable("formVersionOID") String formVersionOID,
			@RequestParam(value = "includeDNs", defaultValue = "n") String includeDns,
			@RequestParam(value = "includeAudits", defaultValue = "n") String includeAudits) throws Exception {
		boolean addDNs = includeDns.trim().equalsIgnoreCase("yes") || includeDns.trim().equalsIgnoreCase("y");
		boolean addAudits = includeAudits.trim().equalsIgnoreCase("yes") || includeAudits.trim().equalsIgnoreCase("y");
		return metaDataService.getHTML(request.getContextPath(), getUserAccountBean(), studyOID, studySubjectIdentifier,
				eventOID, formVersionOID, addDNs, addAudits, true, LocaleResolver.getLocale());
	}

	@RequestMapping(value = "/xml/view/{studyOID}/{studySubjectIdentifier}/{studyEventOID}/{formVersionOID}", method = RequestMethod.GET, produces = MediaType.TEXT_XML_VALUE
			+ ";charset=UTF-8")
	@ResponseBody
	public String getODMMetadata(HttpServletRequest request, @PathVariable("studyOID") String studyOID,
			@PathVariable("formVersionOID") String formVersionOID,
			@PathVariable("studySubjectIdentifier") String studySubjectIdentifier,
			@PathVariable("studyEventOID") String studyEventOID,
			@RequestParam(value = "includeDNs", defaultValue = "n") String includeDns,
			@RequestParam(value = "includeAudits", defaultValue = "n") String includeAudits) {
		LOGGER.debug("Requesting clinical data resource");
		boolean addDNs = includeDns.trim().equalsIgnoreCase("yes") || includeDns.trim().equalsIgnoreCase("y");
		boolean addAudits = includeAudits.trim().equalsIgnoreCase("yes") || includeAudits.trim().equalsIgnoreCase("y");
		String odmXML = metaDataService.getXML(getUserAccountBean(), studyOID, studySubjectIdentifier, studyEventOID,
				formVersionOID, addDNs, addAudits, true, LocaleResolver.getLocale());
		LOGGER.debug(odmXML);
		return odmXML;
	}
}
