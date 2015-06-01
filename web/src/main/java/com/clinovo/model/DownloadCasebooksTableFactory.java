package com.clinovo.model;

import com.clinovo.entity.DownloadCasebooksBean;
import com.clinovo.entity.DownloadCasebooksRow;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Download casebook table factory.
 */
public class DownloadCasebooksTableFactory {

    public static final String EBL_PAGE = "ebl_page";
    public static final String EBL_SORT_COLUMN = "ebl_sortColumnInd";
    public static final String EBL_SORT_ORDER = "ebl_sortAscending";
    public static final String EBL_FILTERED = "ebl_filtered";
    public static final String EBL_FILTER_KEYWORD = "ebl_filterKeyword";
    public static final String EBL_PAGINATED = "ebl_paginated";
    private String sortingColumn = "";
    private Locale locale;
    private List<StudySubjectBean> studySubjectBeanList;

    private StudyDAO studyDao;

    /**
     * Download casebook table constructor.
     * @param sortingColumn the sorting column parameter.
     * @param locale the current system locale.
     */
    public DownloadCasebooksTableFactory(String sortingColumn, Locale locale) {
        this.sortingColumn = sortingColumn;
        this.locale = locale;
    }

    /**
     * Returns download casebook table.
     * @return the download casebook table entity bean.
     */
    public EntityBeanTable buildTable() {
        EntityBeanTable table = getEntityBeanTable();
        ResourceBundle resword = ResourceBundleProvider.getWordsBundle(getLocale());
        String[] columns = {resword.getString("study_subject_ID"), resword.getString("study_name"), resword.getString("action")};
        ArrayList rows = DownloadCasebooksRow.generateRowsFromBeans((ArrayList) getDownloadCasebooksBean(getStudySubjectBeanList()));
        table.setColumns(new ArrayList(Arrays.asList(columns)));
        table.hideColumnLink(2);
        table.setQuery("/pages/downloadCasebooks", new HashMap());
        table.setSortingIfNotExplicitlySet(DownloadCasebooksRow.COL_SUBJECT_LABEL, true);
        table.setRows(rows);
        table.computeDisplay();
        return table;
    }

    private List<DownloadCasebooksBean> getDownloadCasebooksBean(List<StudySubjectBean> studySubjectBeanList) {
        List<DownloadCasebooksBean> downloadCasebooksBeans = new ArrayList<DownloadCasebooksBean>();
        for (StudySubjectBean studySubjectBean : studySubjectBeanList) {
            StudyBean studyBean = getStudyDao().findByStudySubjectId(studySubjectBean.getId());
            DownloadCasebooksBean downloadCasebooksBean = new DownloadCasebooksBean();
            downloadCasebooksBean.setStudySubjectLabel(studySubjectBean.getLabel());
            downloadCasebooksBean.setStudyName(studyBean.getName());
            studyBean = studyBean.isSite() ? (StudyBean) getStudyDao().findByPK(studyBean.getParentStudyId()) : studyBean;
            downloadCasebooksBean.setDownloadLink(getDownloadLink(studyBean.getOid(), studySubjectBean.getOid()));
            downloadCasebooksBean.setDeleteLink(getDeleteLink(studyBean.getOid(), studySubjectBean.getOid()));
            downloadCasebooksBeans.add(downloadCasebooksBean);
        }
        return downloadCasebooksBeans;
    }

    private String getDeleteLink(String studyBeanOid, String studySubjectBeanOid) {
        return "/pages/deleteCasebookFromStorage?studyOid=" + studyBeanOid + "&" + "studySubjectOid=" + studySubjectBeanOid;
    }

    private String getDownloadLink(String studyBeanOid, String studySubjectBeanOid) {
        return "/pages/downloadCasebookFromStorage?studyOid=" + studyBeanOid + "&" + "studySubjectOid=" + studySubjectBeanOid;
    }

    private EntityBeanTable getEntityBeanTable() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        FormProcessor fp = new FormProcessor(request);
        EntityBeanTable answer = new EntityBeanTable();
        answer.setLocale(getLocale());

        if (getSortingColumn() != null && !"".equals(getSortingColumn())) {
            answer.setSortingColumnExplicitlySet(true);
        }

        answer.setCurrPageNumber(fp.getInt(EBL_PAGE));
        answer.setSortingColumnInd(fp.getInt(EBL_SORT_COLUMN));
        answer.setKeywordFilter(fp.getString(EBL_FILTER_KEYWORD));
        String[] blnFields = {EBL_SORT_ORDER, EBL_FILTERED, EBL_PAGINATED};

        for (int i = 0; i < blnFields.length; i++) {
            String value = fp.getString(blnFields[i]);
            boolean b = fp.getBoolean(blnFields[i]);
            if (!"".equals(value)) {
                if (i == 0) {
                    answer.setAscendingSort(b);
                } else if (i == 1) {
                    answer.setFiltered(b);
                } else {
                    answer.setPaginated(b);
                }
            }
        }

        return answer;
    }

    public String getSortingColumn() {
        return sortingColumn;
    }

    public void setSortingColumn(String sortingColumn) {
        this.sortingColumn = sortingColumn;
    }

    public List<StudySubjectBean> getStudySubjectBeanList() {
        return studySubjectBeanList;
    }

    public void setStudySubjectBeanList(List<StudySubjectBean> studySubjectBeanList) {
        this.studySubjectBeanList = studySubjectBeanList;
    }

    public void setStudyDao(StudyDAO studyDao) {
        this.studyDao = studyDao;
    }

    public StudyDAO getStudyDao() {
        return studyDao;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }
}
