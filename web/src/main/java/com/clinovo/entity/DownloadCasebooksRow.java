package com.clinovo.entity;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.web.bean.EntityBeanRow;

import java.util.ArrayList;

/**
 * Row object for download casebooks table.
 */
public class DownloadCasebooksRow extends EntityBeanRow {

    public static final int COL_SUBJECT_LABEL = 0;
    public static final int COL_STUDY_LABEL = 1;


    @Override
    protected int compareColumn(Object row, int sortingColumn) {
        if (!row.getClass().equals(DownloadCasebooksRow.class)) {
            return 0;
        }

        DownloadCasebooksBean thisEvent = (DownloadCasebooksBean) bean;
        DownloadCasebooksBean argEvent = (DownloadCasebooksBean) ((DownloadCasebooksRow) row).bean;

        int answer = 0;
        switch (sortingColumn) {
            case COL_SUBJECT_LABEL:
                answer = thisEvent.getStudySubjectLabel().toLowerCase().compareTo(argEvent.getStudySubjectLabel().toLowerCase());
                break;
            case COL_STUDY_LABEL:
                answer = thisEvent.getStudyName().toLowerCase().compareTo(argEvent.getStudyName().toLowerCase());
                break;
            default:
        }

        return answer;
    }

    @Override
    public String getSearchString() {
        DownloadCasebooksBean thisElement = (DownloadCasebooksBean) bean;
        return thisElement.getStudyName() + " " + thisElement.getStudySubjectLabel();
    }

    @Override
    public ArrayList generatRowsFromBeans(ArrayList beans) {
        return DownloadCasebooksRow.generateRowsFromBeans(beans);
    }

    /**
     * Converts container beans to the row objects.
     * @param beans input beans.
     * @return the list of rows.
     */
    public static ArrayList generateRowsFromBeans(ArrayList beans) {
        ArrayList answer = new ArrayList();

        for (int i = 0; i < beans.size(); i++) {
            try {
                DownloadCasebooksRow row = new DownloadCasebooksRow();
                row.setBean((DownloadCasebooksBean) beans.get(i));
                answer.add(row);
            } catch (Exception e) {
            }
        }
        return answer;
    }
}
