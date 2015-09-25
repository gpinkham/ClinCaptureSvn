package com.clinovo.service;

import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.DisplayItemWithGroupBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.view.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * This service was created in order to remove duplication of code from DataEntryServlet.
 */
public interface DisplayItemService {

	/**
	 * This methods will create an array of DisplayItemGroupBean, which contains multiple rows for an item group on the
	 * data entry form.
	 *
	 * @param displayItemGroupBean       The Item group which has multiple data rows
	 * @param dataBaseGroups   The original array got from DB which contains multiple data rows
	 * @param formGroups Array from front end which contains multiple data rows
	 * @param request    HttpServletRequest
	 * @return new constructed formGroups, compare to dbGroups, some rows are update, some new ones are added and some
	 * are removed
	 */
	List<DisplayItemGroupBean> loadFormValueForItemGroup(DisplayItemGroupBean displayItemGroupBean,
																 List<DisplayItemGroupBean> dataBaseGroups,
																 List<DisplayItemGroupBean> formGroups,
																 HttpServletRequest request);

	/**
	 *
	 * @param itemWithGroup  DisplayItemWithGroupBean
	 * @param sb SectionBean
	 * @param edcb EventDefinitionCRFBean
	 * @param ecb EventCRFBean
	 * @param request HttpServletRequest
	 */
	void loadItemsWithGroupRows(DisplayItemWithGroupBean itemWithGroup, SectionBean sb,
										  EventDefinitionCRFBean edcb, EventCRFBean ecb, HttpServletRequest request);

	/**
	 * Constructs a list of DisplayItemWithGroupBean, which is used for display a section of items on the UI.
	 *
	 * @param dsb           DisplaySectionBean
	 * @param hasItemGroup  boolean
	 * @param eventCRFDefId int
	 * @param request       HttpServletRequest
	 * @return List<DisplayItemWithGroupBean>
	 */
	List<DisplayItemWithGroupBean> createItemWithGroups(DisplaySectionBean dsb, boolean hasItemGroup,
																  int eventCRFDefId, HttpServletRequest request);
}
