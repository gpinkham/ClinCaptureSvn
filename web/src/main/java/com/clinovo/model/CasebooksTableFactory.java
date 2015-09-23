package com.clinovo.model;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.AbstractTableFactory;
import org.akaza.openclinica.control.DefaultActionsEditor;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.web.table.sdv.SDVSimpleListFilter;
import org.jmesa.facade.TableFacade;
import org.jmesa.view.component.Row;
import org.jmesa.view.editor.BasicCellEditor;
import org.jmesa.view.editor.CellEditor;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Subject casebooks table factory.
 */
@SuppressWarnings("unchecked")
public class CasebooksTableFactory extends AbstractTableFactory {

    public static final String STUDY_CASEBOOKS_TABLE = "studyCasebooksTable";
    public static final String SELECT = "select";
    public static final String STUDY_SUBJECT_ID = "studySubjectId";
    public static final String STUDY_SUBJECT_SECOND_LABEL = "studySubjectSecondLabel";
    public static final String DATE_CREATED = "dateCreated";
    public static final String DATE_OF_BIRTH = "date_of_birth";
    public static final String SUBJECT_STATUS = "subjectStatus";
    public static final String PERSON_ID = "subjectPersonId";
    public static final String SEX = "subjectSex";
    public static final String SITE_NAME = "siteName";

    private SubjectDAO subjectDAO;
    private List<StudySubjectBean> studySubjectBeanList;
    private MessageSource messageSource;
    private boolean secondaryIdRequired;
    private boolean collectDob;
    private boolean genderRequired;
    private boolean personIdRequired;
    private boolean isSite;
    private boolean copyPersonId;

    /**
     * CasebookTableFactory constructor.
     *
     * @param studySubjectBeanList the list of study subject beans.
     */
    public CasebooksTableFactory(List<StudySubjectBean> studySubjectBeanList) {
        this.studySubjectBeanList = studySubjectBeanList;
    }

    @Override
    protected String getTableName() {
        return STUDY_CASEBOOKS_TABLE;
    }

    @Override
    protected void configureColumns(TableFacade tableFacade, Locale locale) {
        tableFacade.setColumnProperties(SELECT, SITE_NAME, STUDY_SUBJECT_ID, DATE_CREATED, SUBJECT_STATUS, SEX,
                STUDY_SUBJECT_SECOND_LABEL, DATE_OF_BIRTH, PERSON_ID);

        Row row = tableFacade.getTable().getRow();
        configureColumn(row.getColumn(SELECT), getMessageSource().getMessage("select", null, locale), new SelectCellEditor(), new DefaultActionsEditor(locale), true, false);
        configureColumn(row.getColumn(SITE_NAME), getMessageSource().getMessage("site_name", null, locale), null, new SDVSimpleListFilter(getSitesOIDsList()), true, true);
        configureColumn(row.getColumn(STUDY_SUBJECT_ID), getMessageSource().getMessage("study_subject_ID", null, locale), null, null, true, true);
        configureColumn(row.getColumn(DATE_CREATED), getMessageSource().getMessage("date_created", null, locale), null, null, true, true);
        configureColumn(row.getColumn(SUBJECT_STATUS), getMessageSource().getMessage("subject_status", null, locale), new BasicCellEditor(), null, true, true);
        configureColumn(row.getColumn(SEX), getMessageSource().getMessage("gender", null, locale), null, null, true, true);
        configureColumn(row.getColumn(STUDY_SUBJECT_SECOND_LABEL), getMessageSource().getMessage("secondary_label", null, locale), null, null, true, true);
        configureColumn(row.getColumn(DATE_OF_BIRTH), getMessageSource().getMessage("date_of_birth", null, locale), null, null, true, true);
        configureColumn(row.getColumn(PERSON_ID), getMessageSource().getMessage("subject_unique_ID", null, locale), null, null, true, true);
    }

    @Override
    public void setDataAndLimitVariables(TableFacade tableFacade) {
        Collection<HashMap<Object, Object>> tableData = new ArrayList<HashMap<Object, Object>>();
        SubjectDAO subjectDAO = getSubjectDAO();
        for (StudySubjectBean studySubjectBean : studySubjectBeanList) {
            SubjectBean subjectBean = (SubjectBean) subjectDAO.findByPK(studySubjectBean.getSubjectId());
            HashMap<Object, Object> h = new HashMap<Object, Object>();
            h.put(STUDY_SUBJECT_ID, studySubjectBean.getLabel());
            h.put(STUDY_SUBJECT_SECOND_LABEL, studySubjectBean.getSecondaryLabel());
            h.put(DATE_CREATED, studySubjectBean.getCreatedDate());
            h.put(DATE_OF_BIRTH, subjectBean.getDateOfBirth());
            h.put(PERSON_ID, isCopyPersonId() ? studySubjectBean.getId() : subjectBean.getUniqueIdentifier());
            h.put(SUBJECT_STATUS, getColoredStatus(studySubjectBean.getStatus()));
            h.put(SEX, studySubjectBean.getGender());
            h.put(SITE_NAME, studySubjectBean.getStudyName());
            h.put("studySubjectOid", studySubjectBean.getOid());

            tableData.add(h);
        }
        tableFacade.setTotalRows(getStudySubjectBeanList().size());
        tableFacade.setItems(tableData);
    }

    @Override
    public void configureTableFacade(HttpServletResponse response, TableFacade tableFacade) {
        super.configureTableFacade(response, tableFacade);
    }

    @Override
    public void configureTableFacadePostColumnConfiguration(TableFacade tableFacade) {
        tableFacade.setToolbar(new CasebooksTableToolbar(isSecondaryIdRequired(), isDateOfEnrollmentRequired(), isGenderRequired(), isPersonIdRequired(), isSite(), getMessageSource(), getLocale()));
    }

    public void setStudySubjectBeanList(List<StudySubjectBean> studySubjectBeanList) {
        this.studySubjectBeanList = studySubjectBeanList;
    }

	private List<String> getSitesOIDsList() {
		List<String> oidList = new ArrayList<String>();
		if (studySubjectBeanList != null) {
			for (StudySubjectBean subject : studySubjectBeanList) {
				oidList.add(subject.getStudyName());
			}
		}
		return oidList;
	}

    public List<StudySubjectBean> getStudySubjectBeanList() {
        return studySubjectBeanList;
    }

    public boolean isSecondaryIdRequired() {
        return secondaryIdRequired;
    }

    public boolean isDateOfEnrollmentRequired() {
        return collectDob;
    }

    public void setCollectDob(boolean collectDob) {
        this.collectDob = collectDob;
    }

    public boolean isGenderRequired() {
        return genderRequired;
    }

    public void setGenderRequired(boolean genderRequired) {
        this.genderRequired = genderRequired;
    }

    public boolean isPersonIdRequired() {
        return personIdRequired;
    }

    public void setPersonIdRequired(boolean personIdRequired) {
        this.personIdRequired = personIdRequired;
    }

    public void setSecondaryIdRequired(boolean secondaryIdRequired) {
        this.secondaryIdRequired = secondaryIdRequired;
    }

    public boolean isSite() {
        return isSite;
    }

    public void setSite(boolean isSite) {
        this.isSite = isSite;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public SubjectDAO getSubjectDAO() {
        return subjectDAO;
    }

    public void setSubjectDAO(SubjectDAO subjectDAO) {
        this.subjectDAO = subjectDAO;
    }

    public void setCopyPersonId(boolean copyPersonId) {
        this.copyPersonId = copyPersonId;
    }

    public boolean isCopyPersonId() {
        return copyPersonId;
    }

    private class SelectCellEditor implements CellEditor {
		public Object getValue(Object o, String s, int i) {
            String studySubjectOid = (String) ((HashMap<Object, Object>) o).get("studySubjectOid");
			return "<div style=\"width:130px;text-align:center;\"><input name=\"oids\" type=\"checkbox\" ssOid=\"" + studySubjectOid + "\"/></div>";
        }
    }

	private String getColoredStatus(Status status) {
		String className = status == Status.AVAILABLE ? "aka_green_highlight"
				: status == Status.AUTO_DELETED || status == Status.DELETED ? "aka_red_highlight" : "";
		return "<span class=" + className + ">" + status.getName() + "</span>";
	}
}
