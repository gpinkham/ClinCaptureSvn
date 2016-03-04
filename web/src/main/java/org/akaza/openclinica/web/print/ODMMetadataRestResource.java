package org.akaza.openclinica.web.print;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.print.MetadataCollectorResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Rest service for ODM metadata.
 */
@Controller
@RequestMapping("/metadata")
@Scope("prototype")
public class ODMMetadataRestResource extends SpringController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ODMMetadataRestResource.class);

	@Autowired
	private MetadataCollectorResource metadataCollectorResource;

	@RequestMapping(value = "/xml/view/{studyOID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE
			+ ";charset=UTF-8")
	@ResponseBody
	public String getODMMetadata(@PathVariable("studyOID") String studyOID) {
		LOGGER.debug("returning form study OID: " + studyOID);
		return metadataCollectorResource.collectODMMetadata(studyOID);
	}

	@RequestMapping(value = "/json/view/{study}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
			+ ";charset=UTF-8")
	@ResponseBody
	public String getODMMetadataJson(@PathVariable("study") String studyOID) {
		LOGGER.debug("returning form study OID: " + studyOID);
		return metadataCollectorResource.collectODMMetadataJson(studyOID);
	}

	@RequestMapping(value = "/html/print/{studyOID}/{eventOID}/{formVersionOID}", method = RequestMethod.GET)
	public String getPrintCRFController(HttpServletRequest request, @PathVariable("studyOID") String studyOID,
			@PathVariable("eventOID") String eventOID, @PathVariable("formVersionOID") String formVersionOID)
					throws Exception {
		request.setAttribute("studyOID", studyOID);
		request.setAttribute("eventOID", eventOID);
		request.setAttribute("formVersionOID", formVersionOID);
		return "printcrf";
	}

	@RequestMapping(value = "/xml/view/{studyOID}/{studyEventDefinitionOId}/{formVersionOID}", method = RequestMethod.GET, produces = MediaType.TEXT_XML_VALUE
			+ ";charset=UTF-8")
	@ResponseBody
	public String getODMMetadataWithFormVersionOID(@PathVariable("studyOID") String studyOID,
			@PathVariable("formVersionOID") String formVersionOID) {
		LOGGER.debug("returning form version OID: " + formVersionOID);
		return metadataCollectorResource.collectODMMetadataForForm(studyOID, formVersionOID);
	}

	@RequestMapping(value = "/json/view/{studyOid}/{studyEventDefinitionOId}/{formVersionOID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
			+ ";charset=UTF-8")
	@ResponseBody
	public String getODMMetadataJson(@PathVariable("studyOid") String studyOID,
			@PathVariable("formVersionOID") String formVersionOID) {
		LOGGER.debug("returning form version OID: " + formVersionOID);
		return metadataCollectorResource.collectODMMetadataJsonString(studyOID, formVersionOID);
	}
}
