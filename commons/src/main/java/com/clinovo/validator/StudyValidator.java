package com.clinovo.validator;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.managestudy.StudyDAO;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * StudyBean validator.
 */
public class StudyValidator {

	public static boolean checkIfStudyFieldsAreUnique(FormProcessor fp, HashMap errors, StudyDAO studyDAO,
													  ResourceBundle respage, ResourceBundle resexception) {
		return checkIfStudyFieldsAreUnique(fp, errors, studyDAO, respage, resexception, null);
	}

	public static boolean checkIfStudyFieldsAreUnique(FormProcessor fp, HashMap errors, StudyDAO studyDAO,
													  ResourceBundle respage, ResourceBundle resexception,
													  StudyBean currentStudy) {
		ArrayList<StudyBean> allStudies = (ArrayList<StudyBean>) studyDAO.findAll();
		boolean result = true;

		for (StudyBean thisBean : allStudies) {
			if (fp.getString("name").trim().equals(thisBean.getName()) && isNotTheSameStudy(currentStudy, thisBean)) {
				result = false;
				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(respage.getString("brief_title_existed"));
				Object[] arguments = {fp.getString("name").trim()};
				Validator.addError(errors, "name", mf.format(arguments));
			}
			if (fp.getString("uniqueProId").trim().equals(thisBean.getIdentifier()) && isNotTheSameStudy(currentStudy, thisBean)) {
				result = false;
				Validator.addError(errors, "uniqueProId", resexception.getString("unique_protocol_id_existed"));
			}
		}
		return result;
	}

	private static boolean isNotTheSameStudy(StudyBean currentStudy, StudyBean anotherStudy) {
		return currentStudy == null || currentStudy.getId() != anotherStudy.getId();
	}
}
