package org.akaza.openclinica.web.print;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;


import com.clinovo.util.SessionUtil;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import org.akaza.openclinica.bean.extract.odm.FullReportBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
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

/**
 * * Rest service for ODM clinical data usage.
 */
@Controller
@RequestMapping("/clinicaldata")
@Scope("prototype")
public class ODMClinicaDataResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ODMClinicaDataResource.class);
    private static final int INDENT_LEVEL = 2;

    @Autowired
    private ClinicalDataCollectorResource clinicalDataCollectorResource;

    @Autowired
    private MetadataCollectorResource metadataCollectorResource;

    @Autowired
    private DataSource dataSource;

    public MetadataCollectorResource getMetadataCollectorResource() {
        return metadataCollectorResource;
    }

    public void setMetadataCollectorResource(
            MetadataCollectorResource metadataCollectorResource) {
        this.metadataCollectorResource = metadataCollectorResource;
    }

    public ClinicalDataCollectorResource getClinicalDataCollectorResource() {
        return clinicalDataCollectorResource;
    }

    public void setClinicalDataCollectorResource(
            ClinicalDataCollectorResource clinicalDataCollectorResource) {
        this.clinicalDataCollectorResource = clinicalDataCollectorResource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns json object with data.
     *
     * @param studyOID the study oid.
     * @param formVersionOID ecrf version oid.
     * @param studyEventOID study event oid.
     * @param studySubjectIdentifier study subject oid.
     * @param includeDns include discrepancy notes flag
     * @param includeAudits include audit logs flag
     * @param request the request object.
     * @return the string with json object.
     */
    @RequestMapping(value = "/json/view/{studyOID}/{studySubjectIdentifier}/{studyEventOID}/{formVersionOID}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String getODMClinicaldata(@PathVariable("studyOID") String studyOID,
                                     @PathVariable("formVersionOID") String formVersionOID,
                                     @PathVariable("studyEventOID") String studyEventOID,
                                     @PathVariable("studySubjectIdentifier") String studySubjectIdentifier,
                                     @RequestParam(value = "includeDNs", defaultValue = "n") String includeDns,
                                     @RequestParam(value = "includeAudits", defaultValue = "n") String includeAudits,
                                     HttpServletRequest request) {
        LOGGER.debug("Requesting clinical data resource");
        boolean includeDN = false;
        boolean includeAudit = false;
        if (includeDns.equalsIgnoreCase("no") || includeDns.equalsIgnoreCase("n")) {
            includeDN = false;
        }
        if (includeAudits.equalsIgnoreCase("no") || includeAudits.equalsIgnoreCase("n")) {
            includeAudit = false;
        }
        if (includeDns.equalsIgnoreCase("yes") || includeDns.equalsIgnoreCase("y")) {
            includeDN = true;
        }
        if (includeAudits.equalsIgnoreCase("yes") || includeAudits.equalsIgnoreCase("y")) {
            includeAudit = true;
        }
        int userId = ((UserAccountBean) request.getSession().getAttribute("userBean")).getId();
        XMLSerializer xmlSerializer = new XMLSerializer();
        FullReportBean report = getMetadataCollectorResource().collectODMMetadataForClinicalData(studyOID,
                formVersionOID,
                getClinicalDataCollectorResource()
                        .generateClinicalData(studyOID, getStudySubjectOID(studySubjectIdentifier, studyOID),
                                studyEventOID, formVersionOID, includeDN, includeAudit, SessionUtil.getLocale(request), userId));
        report.createOdmXml(true);
        xmlSerializer.setTypeHintsEnabled(true);
        JSON json = xmlSerializer.read(report.getXmlOutput().toString().trim());

        JSONClinicalDataPostProcessor processor = new JSONClinicalDataPostProcessor(SessionUtil.getLocale(request));
        processor.process((JSONObject) json);

        return json.toString(INDENT_LEVEL);
    }

    /**
     *Returns print page
     *
     * @param request the request object.
     * @param studyOID the current study oid.
     * @param studySubjectIdentifier the study subject identifier
     * @param eventOID the event oid.
     * @param formVersionOID the form version oid.
     * @param includeDns the flag for include discrepancy notes.
     * @param includeAudits the flag for include audit notes.
     * @return the page with print fields.
     * @throws Exception for all exceptions.
     */
    @RequestMapping(value = "/html/print/{studyOID}/{studySubjectIdentifier}/{eventOID}/{formVersionOID}", method = RequestMethod.GET)
    public String getPrintCRFController(HttpServletRequest request,
                                        @PathVariable("studyOID") String studyOID,
                                        @PathVariable("studySubjectIdentifier") String studySubjectIdentifier,
                                        @PathVariable("eventOID") String eventOID,
                                        @PathVariable("formVersionOID") String formVersionOID,
                                        @RequestParam(value = "includeDNs", defaultValue = "n") String includeDns,
                                        @RequestParam(value = "includeAudits", defaultValue = "n") String includeAudits) throws Exception {
        request.setAttribute("studyOID", studyOID);
        request.setAttribute("studySubjectOID", getStudySubjectOID(studySubjectIdentifier, studyOID));
        request.setAttribute("eventOID", eventOID);
        request.setAttribute("formVersionOID", formVersionOID);
        request.setAttribute("includeAudits", includeAudits);
        request.setAttribute("includeDNs", includeDns);
        return "printcrf";
    }

    @RequestMapping(value = "/xml/view/{studyOID}/{studySubjectIdentifier}/{studyEventOID}/{formVersionOID}", method = RequestMethod.GET,
            produces = MediaType.TEXT_XML_VALUE + ";charset=UTF-8")
    @ResponseBody
    public String getODMMetadata(HttpServletRequest request,
                                 @PathVariable("studyOID") String studyOID,
                                 @PathVariable("formVersionOID") String formVersionOID,
                                 @PathVariable("studySubjectIdentifier") String studySubjectIdentifier,
                                 @PathVariable("studyEventOID") String studyEventOID,
                                 @RequestParam(value = "includeDNs", defaultValue = "n") String includeDns,
                                 @RequestParam(value = "includeAudits", defaultValue = "n") String includeAudits
                                 ) {
        LOGGER.debug("Requesting clinical data resource");
        boolean includeDN = false;
        boolean includeAudit = false;
        int userId = ((UserAccountBean) request.getSession().getAttribute("userBean")).getId();

        if (includeDns.equalsIgnoreCase("no") || includeDns.equalsIgnoreCase("n")) includeDN = false;
        if (includeAudits.equalsIgnoreCase("no") || includeAudits.equalsIgnoreCase("n")) includeAudit = false;
        if (includeDns.equalsIgnoreCase("yes") || includeDns.equalsIgnoreCase("y")) includeDN = true;
        if (includeAudits.equalsIgnoreCase("yes") || includeAudits.equalsIgnoreCase("y")) includeAudit = true;
        FullReportBean report = getMetadataCollectorResource()
                .collectODMMetadataForClinicalData(
                        studyOID, formVersionOID, getClinicalDataCollectorResource()
                                .generateClinicalData(studyOID, getStudySubjectOID(studySubjectIdentifier, studyOID),
                                        studyEventOID, formVersionOID, includeDN, includeAudit, SessionUtil.getLocale(request), userId));

        report.createOdmXml(true);
        LOGGER.debug(report.getXmlOutput().toString().trim());

        return report.getXmlOutput().toString().trim();
    }

    private String getStudySubjectOID(String subjectIdentifier, String studyOID) {
        StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
        StudySubjectBean studySubject = studySubjectDAO.findByOid(subjectIdentifier);
        if (subjectIdentifier.equals("*") || (studySubject != null && studySubject.getOid() != null)) {
            return subjectIdentifier;
        } else {
            StudyDAO studyDAO = new StudyDAO(dataSource);
            StudyBean study = studyDAO.findByOid(studyOID);
            studySubject = studySubjectDAO.findByLabelAndStudy(subjectIdentifier, study);
            if (studySubject != null && studySubject.getOid() != null) {
                return studySubject.getOid();
            } else {
                return subjectIdentifier;
            }
        }
    }

}
