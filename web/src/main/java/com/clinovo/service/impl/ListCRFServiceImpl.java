package com.clinovo.service.impl;

import com.clinovo.service.ListCRFService;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.web.bean.ListCRFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Implementation of the ListCRFService.
 */
@Service("listCRFService")
public class ListCRFServiceImpl implements ListCRFService {

	@Autowired
	private DataSource dataSource;

	/**
	 * {@inheritDoc}
	 */
	public ArrayList<ListCRFRow> generateRowsFromBeans(ArrayList<CRFBean> crfsList) {
		ArrayList<ListCRFRow> answer = new ArrayList<ListCRFRow>();
		StudyDAO studyDAO = new StudyDAO(dataSource);

		for (CRFBean crf : crfsList) {
			ArrayList<StudyBean> studyBeans = studyDAO.findAllActiveWhereCRFIsUsed(crf.getId());
			crf.setStudiesWhereUsed(studyBeans);
			ListCRFRow row = new ListCRFRow();
			row.setBean(crf);
			answer.add(row);
		}
		return answer;
	}
}
