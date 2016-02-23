/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.CasebooksTableFactory;
import com.clinovo.model.DownloadCasebooksTableFactory;
import com.clinovo.service.EmailService;
import com.clinovo.util.EmailUtil;
import com.clinovo.util.HtmlToPdfUtil;
import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

/**
 * Controller for print subject casebooks page.
 */
@Controller
@SuppressWarnings("serial")
public class CasebooksController extends SpringController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MessageSource messageSource;

	@Autowired
	private EmailService emailService;

	private ArrayList<String> subjectOIDs;

    public static final String CASEBOOKS_PAGE = "casebooks/studyCasebooks";
    public static final String DOWNLOADCASEBOOKS_PAGE = "casebooks/downloadCasebooks";
    public static final String CRF_CASEBOOK_STORED_URL = "studyCasebooksUrl";
    public static final String JSESSIONID = "JSESSIONID";
    public static final String DIR_NAME = "print" + File.separator + "Casebooks";
    public static final int WAIT_FOR_JAVA_SCRIPT = 5000;

    /**
     * Casebook table handler.
     *
     * @param request  The request containing the item to code and alias.
     * @param response The response to redirect to.
     * @return the casebooks page.
     * @throws Exception for all exceptions.
     */
    @RequestMapping("/casebooks")
    public String changeDefinitionOrdinalHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String httpPath = (String) request.getSession().getAttribute(CRF_CASEBOOK_STORED_URL);
        String queryString = request.getQueryString();
        StudyBean studyBean = (StudyBean) request.getSession().getAttribute("study");
        boolean isSite = studyBean.isSite();
        if (queryString == null && httpPath != null) {
            if (isSite) {
                httpPath = httpPath.replaceAll("\\&studyCasebooksTable_f_siteName(=[^&]*)?" , "");
            }
            response.sendRedirect("casebooks?" + httpPath);
        } else {
            request.getSession().setAttribute(CRF_CASEBOOK_STORED_URL, queryString);
        }

        StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
        SubjectDAO subjectDAO = new SubjectDAO(dataSource);
        StudyParameterValueDAO studyParameterValueDao = new StudyParameterValueDAO(dataSource);

        List<StudySubjectBean> studySubjects = studySubjectDAO.findAllByStudyId(studyBean.getId());
        boolean secondaryIdRequired = !studyParameterValueDao.findByHandleAndStudy(studyBean.getId(), "secondaryIdRequired").getValue().equalsIgnoreCase("not_used") && !studyParameterValueDao.findByHandleAndStudy(studyBean.getId(), "secondaryIdRequired").getValue().isEmpty();
        boolean collectDob = !studyParameterValueDao.findByHandleAndStudy(studyBean.getId(), "collectDob").getValue().equalsIgnoreCase("3") && !studyParameterValueDao.findByHandleAndStudy(studyBean.getId(), "collectDob").getValue().isEmpty();
        boolean genderRequired = studyParameterValueDao.findByHandleAndStudy(studyBean.getId(), "genderRequired").getValue().equalsIgnoreCase("true");
        boolean personIdRequired = !studyParameterValueDao.findByHandleAndStudy(studyBean.getId(), "subjectPersonIdRequired").getValue().equalsIgnoreCase("not used") && !studyParameterValueDao.findByHandleAndStudy(studyBean.getId(), "subjectPersonIdRequired").getValue().isEmpty();
        boolean copyPersonIdFromStudySubId = studyParameterValueDao.findByHandleAndStudy(studyBean.getId(), "subjectPersonIdRequired").getValue().equalsIgnoreCase("copyFromSSID") && !studyParameterValueDao.findByHandleAndStudy(studyBean.getId(), "subjectPersonIdRequired").getValue().isEmpty();

        CasebooksTableFactory casebooksTableFactory = new CasebooksTableFactory(studySubjects);
        casebooksTableFactory.setSecondaryIdRequired(secondaryIdRequired);
        casebooksTableFactory.setCollectDob(collectDob);
        casebooksTableFactory.setCopyPersonId(copyPersonIdFromStudySubId);
        casebooksTableFactory.setGenderRequired(genderRequired);
        casebooksTableFactory.setPersonIdRequired(personIdRequired);
        casebooksTableFactory.setSite(isSite);
        casebooksTableFactory.setSubjectDAO(subjectDAO);
        casebooksTableFactory.setMessageSource(messageSource);
        casebooksTableFactory.setLocale(LocaleResolver.getLocale(request));

        request.setAttribute("crfEvaluationTable", casebooksTableFactory.createTable(request, response).render());
        request.setAttribute("studyName", studyBean.getName());
		request.setAttribute("generatingOIDs", getSubjectOIDs());
        return CASEBOOKS_PAGE;
    }

	/**
	 * Generate Site casebooks.
	 * @param request HttpServletRequest
	 * @throws Exception in case of error
	 */
    @RequestMapping("/generateSiteCasebooks")
    public void generateSiteCasebooks(HttpServletRequest request) throws Exception {
        String siteId = request.getParameter("siteId");
        if (siteId != null) {
            StudyDAO studyDAO = new StudyDAO(dataSource);
            StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
            StudyBean siteBean = (StudyBean) studyDAO.findByPK(Integer.valueOf(siteId));
            List<StudySubjectBean> studySubjectBeans = studySubjectDAO.findAllByStudyId(Integer.valueOf(siteId));
            StudyBean parentStudyBean = siteBean.isSite() ? (StudyBean) studyDAO.findByPK(siteBean.getParentStudyId()) : siteBean;
            for (StudySubjectBean studySubjectBean : studySubjectBeans) {
                String reportXml = getSubjectCasebookXml(request, true, true, studySubjectBean.getOid());
                OutputStream os = new FileOutputStream(getFile(studySubjectBean.getOid(), DIR_NAME + File.separator + parentStudyBean.getOid()));
                HtmlToPdfUtil.buildPdf(reportXml, os);
            }
        }
    }

	/**
	 * Generate one casebook.
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws Exception in case of error
	 */
    @RequestMapping("/generateCasebook")
    public void generateCasebook(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String subjectOid = request.getParameter("studySubjectOid");
        String includeAudit = request.getParameter("includeAudits");
        String includeDNs = request.getParameter("includeDNs");
        boolean audit = includeAudit != null && !includeAudit.isEmpty() && (includeAudit.equals("y"));
        boolean notes = includeDNs != null && !includeDNs.isEmpty() && (includeDNs.equals("y"));

        StudyBean studyBean = (StudyBean) request.getSession().getAttribute("study");
        StudyDAO studyDAO = new StudyDAO(dataSource);
        studyBean = studyBean.isSite() ? (StudyBean) studyDAO.findByPK(studyBean.getParentStudyId()) : studyBean;

        response.setHeader("Content-Disposition", "inline; filename=" + subjectOid + "_casebook.pdf");
        response.setContentType("application/pdf;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String reportXml = getSubjectCasebookXml(request, audit, notes, subjectOid);
        OutputStream os = new FileOutputStream(getFile(subjectOid, DIR_NAME + File.separator + studyBean.getOid()));
        HtmlToPdfUtil.buildPdf(reportXml, os);
        HtmlToPdfUtil.buildPdf(reportXml, response.getOutputStream());
    }

    /**
     * Generates subject casebook PDF files.
     *
     * @param request  The request containing the item to code and alias.
     * @return the casebooks page.
     * @throws Exception for all exceptions.
     */
	@RequestMapping("/generateCasebooks")
    public String generateCasebooks(HttpServletRequest request) throws Exception {
        String studySubjectOidString = request.getParameter("oids");
        StudyBean studyBean = (StudyBean) request.getSession().getAttribute("study");
        StudyDAO studyDAO = new StudyDAO(dataSource);
        List<String> studySubjectOidList = new ArrayList<String>(Arrays.asList(studySubjectOidString.split("\\,")));
		setSubjectOIDs(studySubjectOidList);
        StudyBean parentStudyBean = studyBean.isSite() ? (StudyBean) studyDAO.findByPK(studyBean.getParentStudyId()) : studyBean;
        for (String ssOid : studySubjectOidList) {
            String reportXml = getSubjectCasebookXml(request, true, true, ssOid);
            OutputStream os = new FileOutputStream(getFile(ssOid, DIR_NAME + File.separator + parentStudyBean.getOid()));
            HtmlToPdfUtil.buildPdf(reportXml, os);
        }
		cleanupOIDs(studySubjectOidList);
		sendCompletionEmail(request, studySubjectOidString);
		HashMap<String, Object> storedAttributes = new HashMap<String, Object>();
		ArrayList<String> messages = new ArrayList<String>();
		String message = messageSource.getMessage("casebooks_generated", null, LocaleResolver.getLocale()).replace("{0}", request.getContextPath() + "/pages");
		messages.add(message);
		storedAttributes.put("pageMessages", messages);
		request.getSession().setAttribute("RememberLastPage_storedAttributes", storedAttributes);
        return CASEBOOKS_PAGE;
    }

    private String getSubjectCasebookXml(HttpServletRequest request, boolean includeAudit, boolean includeNotes, String ssOid) throws IOException {

        StudyBean studyBean = (StudyBean) request.getSession().getAttribute("study");
        StudyDAO studyDAO = new StudyDAO(dataSource);
        StudyBean parentStudyBean = studyBean.isSite() ? (StudyBean) studyDAO.findByPK(studyBean.getParentStudyId()) : studyBean;
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
        webClient.setAjaxController(new AjaxController() {
            @Override
            public boolean processSynchron(HtmlPage page, WebRequest request, boolean async) {
                return true;
            }
        });

        webClient.getCookieManager().addCookie(new Cookie(request.getServerName(), JSESSIONID, sessionId));
        HtmlPage page = webClient.getPage(getCasebookUrl(request, includeAudit, includeNotes, ssOid, parentStudyBean.getOid()));
		while (!page.asXml().contains("document_end")) {
			if (page.asText().isEmpty()) {
				break;
			}
			webClient.waitForBackgroundJavaScript(WAIT_FOR_JAVA_SCRIPT);
		}
        String result = page.getBody().asXml();
        webClient.closeAllWindows();
        return result;
    }

    /**
     * Main method for download study casebook table.
     *
     * @param request the http request object.
     * @return the download casebooks page path.
     */
    @RequestMapping("/downloadCasebooks")
    public String downloadCasebookPageHandler(HttpServletRequest request) {
        StudyDAO studyDAO = new StudyDAO(dataSource);
        StudyBean studyBean = (StudyBean) request.getSession().getAttribute("study");
        StudyBean parentStudyBean = studyBean.isSite() ? (StudyBean) studyDAO.findByPK(studyBean.getParentStudyId()) : studyBean;

        List<String> studySubjectOid = getStudySubjectOidList(parentStudyBean.getOid());
        List<StudySubjectBean> studySubjects = getStudySubjectBeanList(studySubjectOid, studyBean.getId(), studyBean.isSite());

        String sortingColumn = request.getParameter(DownloadCasebooksTableFactory.EBL_SORT_COLUMN);
        DownloadCasebooksTableFactory downloadCasebooksTableFactory = new DownloadCasebooksTableFactory(sortingColumn, LocaleResolver.getLocale(request));
        downloadCasebooksTableFactory.setStudySubjectBeanList(studySubjects);
        downloadCasebooksTableFactory.setStudyDao(studyDAO);

        EntityBeanTable table = downloadCasebooksTableFactory.buildTable();
        request.setAttribute("table", table);
        request.setAttribute("studyName", studyBean.getName());

        return DOWNLOADCASEBOOKS_PAGE;
    }

    /**
     * Downloads study subject casebook by study oid and study subject oid.
     *
     * @param request  the http request object.
     * @param response the http response object.
     * @return the download casebook page.
     * @throws IOException for all IO exceptions.
     */
    @RequestMapping("/downloadCasebookFromStorage")
    public String downloadCasebookFromStorage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String studyOid = request.getParameter("studyOid");
        String studySubjectOid = request.getParameter("studySubjectOid");

        if (studyOid != null && !studyOid.isEmpty() && studySubjectOid != null && !studySubjectOid.isEmpty()) {
            String casebookPath = getCasebookFilePath(studyOid, studySubjectOid);
            File file = new File(casebookPath);
            if (file.isFile()) {
                downloadFile(file, "text/xml", response);
            }
        }
        return DOWNLOADCASEBOOKS_PAGE;
    }

    private String getCasebookFilePath(String studyOid, String studySubjectOid) {
        return SQLInitServlet.getField("filePath") + "print" + File.separator + "Casebooks" + File.separator + studyOid + File.separator + studySubjectOid + ".pdf";
    }

    /**
     * Deletes study subject casebook by study oid and study subject oid.
     *
     * @param request the request object.
     * @return the download casebook page.
     * @throws IOException for all IO exceptions.
     */
    @RequestMapping("/deleteCasebookFromStorage")
    public String deleteCasebookFromStorage(HttpServletRequest request) throws IOException {
        String studyOid = request.getParameter("studyOid");
        String studySubjectOid = request.getParameter("studySubjectOid");
        if (studyOid != null && !studyOid.isEmpty() && studySubjectOid != null && !studySubjectOid.isEmpty()) {
            String casebookPath = getCasebookFilePath(studyOid, studySubjectOid);
            File file = new File(casebookPath);
            if (file.isFile()) {
                file.delete();
            }
        }

        return downloadCasebookPageHandler(request);
    }

    private List<StudySubjectBean> getStudySubjectBeanList(List<String> studySubjectOid, int studyId, boolean isSite) {
        StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
        List<StudySubjectBean> studySubjectBeans = new ArrayList<StudySubjectBean>();
        for (String ssOid : studySubjectOid) {
            StudySubjectBean studySubjectBean = studySubjectDAO.findByOid(ssOid);
            if (studySubjectBean != null) {
                if (isSite) {
                    if (studySubjectBean.getStudyId() == studyId) {
                        studySubjectBeans.add(studySubjectBean);
                    }
                } else {
                    studySubjectBeans.add(studySubjectBean);
                }
            }
        }
        return studySubjectBeans;
    }

    private List<String> getStudySubjectOidList(String oid) {
        String datasetFilePath = SQLInitServlet.getField("filePath") + "print" + File.separator + "Casebooks" + File.separator + oid;
        File folder = new File(datasetFilePath);
        List<String> studySubjectOid = new ArrayList<String>();
        if (folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
			if (listOfFiles != null) {
				for (File file : listOfFiles) {
					if (file.isFile()) {
						studySubjectOid.add(FilenameUtils.removeExtension(file.getName()));
					}
				}
			}
        }
        return studySubjectOid;
    }

    private String getCasebookUrl(HttpServletRequest request, boolean includeAudit, boolean includeNotes, String studySubjectOid, String studyOid) {
        String validUri = request.getRequestURI().substring(0, request.getRequestURI().lastIndexOf("/pages"));
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + validUri
                + "/print/clinicaldata/html/print/" + studyOid + "/" + studySubjectOid + "/*/*?includeDNs="
                + (includeNotes ? "y" : "n") + "&includeAudits=" + (includeAudit ? "y" : "n");
    }


    private File getFile(String pdfName, String folderName) throws IOException {
        String datasetFilePath = SQLInitServlet.getField("filePath") + folderName + File.separator + pdfName + ".pdf";
        File file = new File(datasetFilePath);
        if (!file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();

        return file;
    }

	private void sendCompletionEmail(HttpServletRequest request, String subjectOID) {
		UserAccountBean user = (UserAccountBean) request.getSession().getAttribute("userBean");
		Locale locale = LocaleResolver.getLocale(request);
		int listSize = subjectOID.split(",").length;
		String subject = messageSource.getMessage("casebooks_were_generated_for", null, locale) + " " + listSize
				+ " " + messageSource.getMessage("_subjects", null, locale) + ".";
		StudyBean study = (StudyBean) request.getSession().getAttribute("study");
		String message = EmailUtil.getEmailBodyStart() + subject + "<br/><ul>"
				+ messageSource.getMessage("job_error_mail.serverUrl", null, locale) + " " + SQLInitServlet.getSystemURL() + "</li>"
				+ messageSource.getMessage("job_error_mail.studyName", null, locale) + " " + study.getName() + "</li></ul><br/>"
				+ messageSource.getMessage("casebooks_download_link", null, locale) + ": <a href=\"" + SQLInitServlet.getSystemURL() + "pages/casebooks\">"
				+ messageSource.getMessage("download_casebooks", null, locale) + "</a><br/>"
				+ EmailUtil.getEmailBodyEnd() + EmailUtil.getEmailFooter(locale);
		try {
			emailService.sendEmail(user.getEmail(), subject, message, true, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> getSubjectOIDs() {
		if (subjectOIDs == null) {
			subjectOIDs = new ArrayList<String>();
		}
		return subjectOIDs;
	}

	private void setSubjectOIDs(List<String> list) {
		for (String oid : list) {
			if (getSubjectOIDs().contains(oid)) {
				continue;
			}
			getSubjectOIDs().add(oid);
		}
	}

	private void cleanupOIDs(List<String> list) {
		getSubjectOIDs().removeAll(list);
	}
}
