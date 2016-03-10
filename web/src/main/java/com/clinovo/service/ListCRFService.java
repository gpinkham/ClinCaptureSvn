package com.clinovo.service;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.web.bean.ListCRFRow;

import java.util.ArrayList;

/**
 * List CRF Service.
 */
public interface ListCRFService {

	/**
	 * Generate List of ListCRFRows
	 * @param crfsList List of CRFBeans
	 * @return ArrayList
	 */
	ArrayList<ListCRFRow> generateRowsFromBeans(ArrayList<CRFBean> crfsList);
}
