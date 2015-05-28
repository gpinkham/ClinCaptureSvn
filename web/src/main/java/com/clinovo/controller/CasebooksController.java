package com.clinovo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.web.print.HtmlToPdfController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.model.CasebooksTableFactory;
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
public class CasebooksController extends Redirection {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MessageSource messageSource;

    public static final String CASEBOOKS_PAGE = "casebooks/studyCasebooks";
    public static final String CRF_CASEBOOK_STORED_URL = "studyCasebooksUrl";
    public static final String JSESSIONID = "JSESSIONID";
    public static final String IS_JOB = "isJob";
    public static final String HTML_CODE = "htmlCode";
    public static final String FILE_NAME = "fileName";
    public static final int WAIT_FOR_JAVA_SCRIPT = 15000;

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

        StudyDAO studyDAO = new StudyDAO(dataSource);
        StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
        SubjectDAO subjectDAO = new SubjectDAO(dataSource);
        StudyParameterValueDAO studyParameterValueDao = new StudyParameterValueDAO(dataSource);

        List<StudySubjectBean> studySubjects = studySubjectDAO.findAllByStudyId(studyBean.getId());
        StudyBean parentStudy = isSite ? (StudyBean) studyDAO.findByPK(studyBean.getParentStudyId()) : studyBean;
        boolean secondaryIdRequired = !studyParameterValueDao.findByHandleAndStudy(parentStudy.getId(), "secondaryIdRequired").getValue().equalsIgnoreCase("not_used") && !studyParameterValueDao.findByHandleAndStudy(parentStudy.getId(), "secondaryIdRequired").getValue().isEmpty();
        boolean collectDob = !studyParameterValueDao.findByHandleAndStudy(parentStudy.getId(), "collectDob").getValue().equalsIgnoreCase("3") && !studyParameterValueDao.findByHandleAndStudy(parentStudy.getId(), "collectDob").getValue().isEmpty();
        boolean genderRequired = studyParameterValueDao.findByHandleAndStudy(parentStudy.getId(), "genderRequired").getValue().equalsIgnoreCase("true");
        boolean personIdRequired = !studyParameterValueDao.findByHandleAndStudy(parentStudy.getId(), "subjectPersonIdRequired").getValue().equalsIgnoreCase("not used") && !studyParameterValueDao.findByHandleAndStudy(parentStudy.getId(), "subjectPersonIdRequired").getValue().isEmpty();
        boolean copyPersonIdFromStudySubId = studyParameterValueDao.findByHandleAndStudy(parentStudy.getId(), "subjectPersonIdRequired").getValue().equalsIgnoreCase("copyFromSSID") && !studyParameterValueDao.findByHandleAndStudy(parentStudy.getId(), "subjectPersonIdRequired").getValue().isEmpty();

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
        request.setAttribute("studyName", parentStudy.getName());
        return CASEBOOKS_PAGE;
    }

    /**
     * Generates subject casebook PDF files.
     *
     * @param request  The request containing the item to code and alias.
     * @param response The response to redirect to.
     * @return the casebooks page.
     * @throws Exception for all exceptions.
     */
	@RequestMapping("/generateCasebooks")
    public String generateCasebooks(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String studySubjectOid = request.getParameter("oids");
        List<String> studySubjectOidList = new ArrayList<String>(Arrays.asList(studySubjectOid.split("\\,")));
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        HtmlToPdfController controller = new HtmlToPdfController();
        StudyBean studyBean = (StudyBean) request.getSession().getAttribute("study");
        StudyDAO studyDAO = new StudyDAO(dataSource);
        studyBean = studyBean.isSite() ? (StudyBean) studyDAO.findByPK(studyBean.getParentStudyId()) : studyBean;

        for (String ssOid : studySubjectOidList) {
            WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
            webClient.setAjaxController(new AjaxController() {
                @Override
                public boolean processSynchron(HtmlPage page, WebRequest request, boolean async) {
                    return true;
                }
            });

            webClient.getCookieManager().addCookie(new Cookie(request.getServerName(), JSESSIONID, sessionId));
            HtmlPage page = webClient.getPage(getCasebookUrl(request, ssOid, studyBean.getOid()));
            while (page.asXml().indexOf("page-header") < 0) {
                if (page.asText().isEmpty()) {
                    break;
                }
                webClient.waitForBackgroundJavaScript(WAIT_FOR_JAVA_SCRIPT);
            }
            request.setAttribute(IS_JOB, "true");
            request.setAttribute(HTML_CODE, page.getBody().asXml());
            request.setAttribute(FILE_NAME, ssOid);
            controller.buildPdf(response, request);
        }
        return CASEBOOKS_PAGE;
    }

	private String getCasebookUrl(HttpServletRequest request, String studySubjectOid, String studyOid) {
		String validUri = request.getRequestURI().substring(0, request.getRequestURI().lastIndexOf("/pages"));
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + validUri
				+ "/print/clinicaldata/html/print/" + studyOid + "/" + studySubjectOid + "/*/*";
	}

    @Override
    public String getUrl() {
        return CASEBOOKS_PAGE;
    }
}
