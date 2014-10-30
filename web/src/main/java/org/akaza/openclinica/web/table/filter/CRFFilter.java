package org.akaza.openclinica.web.table.filter;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.jmesa.view.html.editor.DroplistFilterEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * A select row filter for a table that binds to the CRF names in the database.
 * 
 */
@SuppressWarnings({ "unused" })
public class CRFFilter extends DroplistFilterEditor {

	public static final Logger LOGGER = LoggerFactory.getLogger(StudyEventTableRowFilter.class);

	private DataSource dataSource;
	private StudyBean study;
	private UserAccountBean userAccountBean;

	/**
	 * Constructs an instance of the filter. It assumes that the supplied data source and study are valid and
	 * functional.
	 * 
	 * @param dataSource
	 *            Valid DataBase connection object
	 * @param currentStudy
	 *            The current study the user is working with.
	 * @param user
	 *            The current user account bean.
	 */
	public CRFFilter(DataSource dataSource, StudyBean currentStudy, UserAccountBean user) {
		this.userAccountBean = user;
		this.dataSource = dataSource;
		this.study = currentStudy;
	}

	@Override
	protected List<Option> getOptions() {

		List<CRFBean> crfs;
		if (Role.STUDY_EVALUATOR.equals(userAccountBean.getRoleByStudy(study).getRole())) {
			crfs = getEvaluationCRFs();
		} else {
			crfs = getCRFs();
		}

		List<Option> options = new ArrayList<Option>();
		for (CRFBean crf : crfs) {
			options.add(new Option(crf.getName() + "", crf.getName()));
		}
		return options;
	}

	private List<CRFBean> getCRFs() {
		return new CRFDAO(dataSource).findAllActiveCrfs();
	}

	private List<CRFBean> getEvaluationCRFs() {
		return new CRFDAO(dataSource).findAllEvaluableCrfs(study.getId());
	}
}
