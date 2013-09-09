package org.akaza.openclinica.util;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.akaza.openclinica.bean.managestudy.StudyBean;

public class ImportSummaryInfo {

	public Set<Integer> totalStudySubjectIds = new HashSet<Integer>();
    public Set<Integer> affectedStudySubjectIds = new HashSet<Integer>();
    public Set<Integer> skippedStudySubjectIds = new HashSet<Integer>();
    public Set<String> totalStudyEventIds = new HashSet<String>();
    public Set<String> affectedStudyEventIds = new HashSet<String>();
    public Set<String> skippedStudyEventIds = new HashSet<String>();
    public Set<String> totalItemIds = new HashSet<String>();
    public Set<String> affectedItemIds = new HashSet<String>();
    public Set<String> skippedItemIds = new HashSet<String>();

	public void processStudySubject(Integer studySubjectId, boolean skipped) {
		totalStudySubjectIds.add(studySubjectId);
		if (skipped && !affectedStudySubjectIds.contains(studySubjectId)) {
			skippedStudySubjectIds.add(studySubjectId);
		} else {
			skippedStudySubjectIds.remove(studySubjectId);
			affectedStudySubjectIds.add(studySubjectId);
		}
	}

	public void processStudyEvent(String eventCrfId, boolean skipped) {
		totalStudyEventIds.add(eventCrfId);
		if (skipped && !affectedStudyEventIds.contains(eventCrfId)) {
			skippedStudyEventIds.add(eventCrfId);
		} else {
			skippedStudyEventIds.remove(eventCrfId);
			affectedStudyEventIds.add(eventCrfId);
		}
	}

	public void processItem(String itemId, boolean skipped) {
		totalItemIds.add(itemId);
		if (skipped && !affectedItemIds.contains(itemId)) {
			skippedItemIds.add(itemId);
		} else {
			skippedItemIds.remove(itemId);
			affectedItemIds.add(itemId);
		}
	}

    public String prepareSummaryMessage(StudyBean currentStudy, ResourceBundle resword) {
        String msg = "";
        if (currentStudy.getStudyParameterConfig().getReplaceExisitingDataDuringImport().equals("no")) {
            MessageFormat mf = new MessageFormat("");
            mf.applyPattern(resword.getString("import_summary_subjects_out_of_affected"));
            msg += mf.format(new Object[]{ affectedStudySubjectIds.size(), totalStudySubjectIds.size() }) + "<br/>";

            mf = new MessageFormat("");
            mf.applyPattern(resword.getString("import_summary_events_out_of_affected"));
            msg += mf.format(new Object[]{ affectedStudyEventIds.size(), totalStudyEventIds.size() }) + "<br/>";

            mf = new MessageFormat("");
            mf.applyPattern(resword.getString("import_summary_item_out_of_affected"));
            msg += mf.format(new Object[]{ affectedItemIds.size(), totalItemIds.size() });
        } else {
            MessageFormat mf = new MessageFormat("");
            mf.applyPattern(resword.getString("import_summary_subjects_affected"));
            msg += mf.format(new Object[]{ totalStudySubjectIds.size() }) + "<br/>";

            mf = new MessageFormat("");
            mf.applyPattern(resword.getString("import_summary_events_affected"));
            msg += mf.format(new Object[]{ totalStudyEventIds.size() }) + "<br/>";

            mf = new MessageFormat("");
            mf.applyPattern(resword.getString("import_summary_item_affected"));
            msg += mf.format(new Object[]{ totalItemIds.size() });
        }
        return msg;
    }
}
