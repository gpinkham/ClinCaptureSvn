package org.akaza.openclinica.web.table.filter;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.jmesa.view.html.editor.DroplistFilterEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A select row filter for a table that binds to the study event names in the database.
 * 
 */
public class StudyEventTableRowFilter extends DroplistFilterEditor {

	private StudyBean study;
	private DataSource dataSource;
	private UserAccountBean user;

	private Logger log = LoggerFactory.getLogger(StudyEventTableRowFilter.class);

	/**
	 * Constructs an instance of the filter. It assumes that the supplied data source and study are valid and
	 * functional.
	 * 
	 * @param dataSource
	 *            Valid DataBase connection object
	 * @param currentStudy
	 *            The current study the user is working with.
	 * @param user
	 *            The current user
	 */
	public StudyEventTableRowFilter(DataSource dataSource, StudyBean currentStudy, UserAccountBean user) {
		this.dataSource = dataSource;
		this.study = currentStudy;
		this.user = user;
	}

	@Override
	protected List<Option> getOptions() {
		List<Option> options = new ArrayList<Option>();
		List<StudyEventDefinitionBean> studyEvents = getStudyEvents();
		for (StudyEventDefinitionBean studyEvent : studyEvents) {
			options.add(new Option(studyEvent.getName() + "", studyEvent.getName()));
		}
		return options;
	}

	@SuppressWarnings({ "unchecked" })
	private List<StudyEventDefinitionBean> getStudyEvents() {
		log.trace("Extracting events for study: " + study.getName());
		StudyEventDefinitionDAO studyEventDAO = new StudyEventDefinitionDAO(dataSource);
		List<StudyEventDefinitionBean> studyEvents;

		Role roleInStudy = StudyUserRoleBean.determineRoleInCurrentStudy(user, study);
		if (roleInStudy.equals(Role.STUDY_EVALUATOR)) {
			studyEvents = studyEventDAO.findAllAvailableWithEvaluableCRFByStudy(study);
		} else {
			studyEvents = studyEventDAO.findAllAvailableByStudy(study);
		}
		return studyEvents;
	}
}
