package org.akaza.openclinica.web.table.filter;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.jmesa.view.html.editor.DroplistFilterEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A select row filter for a table that binds to the CRF names in the database.
 * 
 */
public class CRFFilter extends DroplistFilterEditor {

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
	public CRFFilter(DataSource dataSource, StudyBean currentStudy) {

		this.dataSource = dataSource;
		this.study = currentStudy;
	}

	@Override
	protected List<Option> getOptions() {

		List<CRFBean> crfs = getCRFs();
		List<Option> options = new ArrayList<Option>();

		for (CRFBean crf : crfs) {

			// Build with id and name
			options.add(new Option(crf.getName() + "", crf.getName()));
		}

		return options;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<CRFBean> getCRFs() {

		log.trace("Extracting crfs");

		CRFDAO crfDAO = new CRFDAO(dataSource);
		List<CRFBean> crfs = (List<CRFBean>) crfDAO.findAllByStudy(study.getId());

		return crfs;
	}
}
