package org.akaza.openclinica.web.table.filter;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

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

	private Logger log = LoggerFactory.getLogger(StudyEventTableRowFilter.class);

	/**
	 * Constructs an instance of the filter. It assumes that the supplied data source and study are valid and
	 * functional.
	 * 
	 * @param dataSource
	 *            Valid DataBase connection object
	 * @param currentStudy
	 *            The current study the user is working with.
	 */
	public StudyEventTableRowFilter(DataSource dataSource, StudyBean currentStudy) {

		this.dataSource = dataSource;
		this.study = currentStudy;
	}

	@Override
	protected List<Option> getOptions() {

		List<Option> options = new ArrayList<Option>();
		List<StudyEventDefinitionBean> studyEvents = getStudyEvents();

		for (StudyEventDefinitionBean studyEvent : studyEvents) {

			// Build with id and name
			options.add(new Option(studyEvent.getName() + "", studyEvent.getName()));
		}

		return options;
	}

	@SuppressWarnings({ "unchecked" })
	private List<StudyEventDefinitionBean> getStudyEvents() {

		log.trace("Extracting events for study: " + study.getName());

		StudyEventDefinitionDAO studyEventDAO = new StudyEventDefinitionDAO(dataSource);
		List<StudyEventDefinitionBean> studyEvents = studyEventDAO.findAllAvailableByStudy(study);

		return studyEvents;
	}
}
