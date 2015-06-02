package com.clinovo.entity;

import org.akaza.openclinica.bean.core.EntityBean;

/**
 * Download casebook object bean for UX.
 */
@SuppressWarnings("serial")
public class DownloadCasebooksBean extends EntityBean {

    private String studySubjectLabel = "";
    private String studyName = "";
    private String downloadLink = "";
    private String deleteLink = "";

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getDeleteLink() {
        return deleteLink;
    }

    public void setDeleteLink(String deleteLink) {
        this.deleteLink = deleteLink;
    }

    public String getStudySubjectLabel() {
        return studySubjectLabel;
    }

    public void setStudySubjectLabel(String studySubjectLabel) {
        this.studySubjectLabel = studySubjectLabel;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }
}
