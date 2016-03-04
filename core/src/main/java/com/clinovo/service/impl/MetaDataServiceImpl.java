package com.clinovo.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.sql.DataSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.akaza.openclinica.bean.extract.odm.FullReportBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.print.ClinicalDataCollectorResource;
import org.akaza.openclinica.print.MetadataCollectorResource;
import org.akaza.openclinica.service.subject.SubjectServiceInterface;
import org.apache.commons.io.IOUtils;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinovo.bean.ConverterHelper;
import com.clinovo.service.MetaDataService;

/**
 * MetaDataServiceImpl.
 */
@Service
@Transactional
public class MetaDataServiceImpl implements MetaDataService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataServiceImpl.class);

	public static final String XSLT = "xslt";
	public static final String UTF_8 = "UTF-8";
	public static final String FILE_PATH = "filePath";
	public static final String ODM_TO_CASEBOOK_HTML_XSL = "odm_to_casebook_html.xsl";

	@Autowired
	private ClinicalDataCollectorResource clinicalDataCollectorResource;

	@Autowired
	private MetadataCollectorResource metadataCollectorResource;

	@Autowired
	private SubjectServiceInterface studySubjectService;

	@Autowired
	private StudyConfigService studyConfigService;

	@Autowired
	private DataSource dataSource;

	/**
	 * {@inheritDoc}
	 */
	public String getXML(UserAccountBean currentUser, String studyOID, String subjectIdentifier, String studyEventOID,
			String formVersionOID, boolean includeDN, boolean includeAudit, boolean localizeDatesToUserTZ,
			Locale locale) {
		String studySubjectOID = studySubjectService.getStudySubjectOID(subjectIdentifier, studyOID);
		FullReportBean report = metadataCollectorResource.collectODMMetadataForClinicalData(studyOID, formVersionOID,
				clinicalDataCollectorResource.generateClinicalData(studyOID, studySubjectOID, studyEventOID,
						formVersionOID, includeDN, includeAudit, locale, currentUser.getId()));
		if (localizeDatesToUserTZ) {
			report.setTargetTimeZoneId(currentUser.getUserTimeZoneId());
		}
		report.createOdmXml(true);
		return report.getXmlOutput().toString().trim();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getHTML(String contextPath, UserAccountBean currentUser, String studyOID, String subjectIdentifier,
			String studyEventOID, String formVersionOID, boolean includeDNs, boolean includeAudits,
			boolean localizeDatesToUserTZ, Locale locale) {
		StringWriter writer = new StringWriter();
		FileInputStream xslFileInputStream = null;
		try {
			ResourceBundleProvider.updateLocale(locale);
			StudyBean studyBean = new StudyDAO(dataSource).findByOid(studyOID);
			String studySubjectOID = studySubjectService.getStudySubjectOID(subjectIdentifier, studyOID);
			ResourceBundle formatResourceBundle = ResourceBundleProvider.getFormatBundle();

			String odmXML = getXML(currentUser, studyOID, studySubjectOID, studyEventOID, formVersionOID, includeDNs,
					includeAudits, localizeDatesToUserTZ, locale);

			xslFileInputStream = new FileInputStream(CoreResources.getField(FILE_PATH).concat(File.separator)
					.concat(XSLT).concat(File.separator).concat(ODM_TO_CASEBOOK_HTML_XSL));

			studyConfigService.populateStudyBean(studyBean);

			TransformerFactory tFactory = new TransformerFactoryImpl();
			Transformer transformer = tFactory.newTransformer(new StreamSource(xslFileInputStream));
			transformer.setOutputProperty(OutputKeys.ENCODING, UTF_8);
			transformer.setParameter("locale", locale);
			transformer.setParameter("includeDNs", includeDNs);
			transformer.setParameter("casebookDate", new Date());
			transformer.setParameter("includeAudits", includeAudits);
			transformer.setParameter("formVersionOID", formVersionOID);
			transformer.setParameter("language", locale.getLanguage());
			transformer.setParameter("converterHelper", new ConverterHelper());
			transformer.setParameter("userTimeZoneId", currentUser.getUserTimeZoneId());
			transformer.setParameter("studyParameters", studyBean.getStudyParameterConfig());
			transformer.setParameter("resourceBundleWords", ResourceBundleProvider.getWordsBundle());
			transformer.setParameter("resourceBundleTerms", ResourceBundleProvider.getTermsBundle());
			transformer.setParameter("dateFormat", formatResourceBundle.getString("date_format_string"));
			transformer.setParameter("fileDownloadUrl", contextPath.concat("/DownloadAttachedFile?fileName="));
			transformer.setParameter("dateTimeFormat", formatResourceBundle.getString("date_time_format_string"));
			transformer.transform(new StreamSource(IOUtils.toInputStream(odmXML, UTF_8)), new StreamResult(writer));
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		} finally {
			try {
				if (xslFileInputStream != null) {
					xslFileInputStream.close();
				}
			} catch (Exception ex) {
				LOGGER.error("Error has occurred.", ex);
			}
		}
		return writer.toString();
	}
}
