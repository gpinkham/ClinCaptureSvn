package com.clinovo.controller;

import com.clinovo.bean.display.DisplayItemLevelSDVRow;
import com.clinovo.model.EDCItemMetadata;
import com.clinovo.service.EDCItemMetadataService;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This controller is available from UpdateEventDefinition page.
 */
@Controller
@RequestMapping("/configureItemLevelSDV")
@SuppressWarnings("rawtypes")
public class ItemLevelSDVController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EDCItemMetadataService edcItemMetadataService;

	public static final String ITEM_PREFIX = "_i";

	/**
	 * Main get method - will be called when page is opened.
	 *
	 * @param model Model.
	 * @param edcId int
	 * @return page name.
	 */
	@RequestMapping(method = RequestMethod.GET, params = "edcId")
	public String initConfigurePage(Model model, @RequestParam("edcId") int edcId) {
		String page = "managestudy/itemLevelSDV";
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		StudyEventDefinitionBean studyEventDefinition = studyEventDefinitionDAO.findByEventDefinitionCRFId(edcId);
		model.addAttribute("studyEventDefinition", studyEventDefinition);

		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		EventDefinitionCRFBean eventDefinitionCRFBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(edcId);
		CRFDAO crfdao = new CRFDAO(dataSource);
		CRFBean crfBean = (CRFBean) crfdao.findByPK(eventDefinitionCRFBean.getCrfId());
		model.addAttribute("crf", crfBean);
		model.addAttribute("edcId", edcId);

		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(dataSource);
		ArrayList crfVersionBeanList = crfVersionDAO.findAllByCRFId(crfBean.getId());
		model.addAttribute("versionsList", crfVersionBeanList);

		return page;
	}

	/**
	 * This method is called via AJAX, and will return JSP filled with
	 * data about SDV required items per sections for some specific CRF version.
	 *
	 * @param model        Model
	 * @param edcId        int
	 * @param crfVersionId int
	 * @param request      HttpServletRequest
	 * @return String
	 */
	@RequestMapping(method = RequestMethod.POST, params = "crfVersionId")
	public String getPageContentForVersion(Model model, @RequestParam("edcId") int edcId,
										   @RequestParam("crfVersionId") int crfVersionId, HttpServletRequest request) {
		String page = "include/itemLevelSDVCrfVersionTable";
		HashMap<SectionBean, ArrayList<DisplayItemLevelSDVRow>> sdvMetadataBySection
				= new HashMap<SectionBean, ArrayList<DisplayItemLevelSDVRow>>();
		SectionDAO sectionDAO = new SectionDAO(dataSource);
		EventDefinitionCRFDAO eventDefinitionCRFDAO = new EventDefinitionCRFDAO(dataSource);
		EventDefinitionCRFBean edcBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(edcId);
		ArrayList sectionBeans = sectionDAO.findAllByCRFVersionId(crfVersionId);
		ItemDAO itemDAO = new ItemDAO(dataSource);
		ItemFormMetadataDAO itemFormMetadataDAO = new ItemFormMetadataDAO(dataSource);

		for (Object sectionObject : sectionBeans) {
			SectionBean sectionBean = (SectionBean) sectionObject;
			ArrayList itemBeans = itemDAO.findAllBySectionId(sectionBean.getId());
			ArrayList<DisplayItemLevelSDVRow> rows = new ArrayList<DisplayItemLevelSDVRow>();

			for (Object itemObject : itemBeans) {
				ItemBean itemBean = (ItemBean) itemObject;
				ItemFormMetadataBean itemFormMetadataBean = itemFormMetadataDAO
						.findByItemIdAndCRFVersionId(itemBean.getId(), sectionBean.getCRFVersionId());

				EDCItemMetadata edcItemMetadata = getMetadataFromSessionOrDatabase(request.getSession(), edcId,
						sectionBean.getCRFVersionId(), itemBean.getId(), edcBean.getStudyEventDefinitionId());

				edcItemMetadata = edcItemMetadata == null ? new EDCItemMetadata() : edcItemMetadata;
				DisplayItemLevelSDVRow row = new DisplayItemLevelSDVRow();
				row.setItemBean(itemBean);
				row.setItemFormMetadataBean(itemFormMetadataBean);
				row.setEdcItemMetadata(edcItemMetadata);
				rows.add(row);
			}
			sdvMetadataBySection.put(sectionBean, rows);
		}
		model.addAttribute("sdvMetadataBySection", sdvMetadataBySection);
		model.addAttribute("crfVersionId", crfVersionId);
		return page;
	}

	/**
	 * Submit all input values into the database.
	 *
	 * @param request   HttpServletRequest
	 * @param response  HttpServletResponse
	 * @param edcId     int
	 * @param versionId int
	 * @throws IOException   if data from request is incorrect or database contains corrupted data.
	 * @throws JSONException in case if json object was not parsed correctly
	 */
	@RequestMapping(method = RequestMethod.POST, params = "submit")
	@SuppressWarnings("unchecked")
	public void saveConfiguration(HttpServletRequest request, HttpServletResponse response,
										 @RequestParam("edcId") int edcId,
										 @RequestParam("versionId") int versionId) throws IOException, JSONException {
		String result = "success";
		String jsonData = request.getParameter("jsonData");
		StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);
		StudyEventDefinitionBean studyEventDefinitionBean = studyEventDefinitionDAO.findByEventDefinitionCRFId(edcId);
		JSONObject jsonObject = new JSONObject(jsonData);
		JSONArray jsonArray = jsonObject.getJSONArray("inputsData");

		HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap = (HashMap<Integer, ArrayList<EDCItemMetadata>>)
				request.getSession().getAttribute("edcItemMetadataMap");
		edcItemMetadataMap = edcItemMetadataMap == null ? new HashMap<Integer, ArrayList<EDCItemMetadata>>() : edcItemMetadataMap;
		ArrayList<EDCItemMetadata> edcItemMetadataList = edcItemMetadataMap.get(edcId);
		edcItemMetadataList = edcItemMetadataList == null ? new ArrayList<EDCItemMetadata>() : edcItemMetadataList;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonEntity = jsonArray.getJSONObject(i);
			String inputName = jsonEntity.getString("inputName");
			String inputValue = jsonEntity.getString("inputValue");
			String itemIdString = inputName.substring(inputName.indexOf(ITEM_PREFIX)).replace(ITEM_PREFIX, "");
			int itemId = Integer.parseInt(itemIdString);
			EDCItemMetadata edcItemMetadata = edcItemMetadataService.findByCRFVersionIDEventDefinitionCRFIDAndItemID(versionId, edcId, itemId);
			if (edcItemMetadata == null) {
				edcItemMetadata = new EDCItemMetadata();
				edcItemMetadata.setItemId(itemId);
				edcItemMetadata.setEventDefinitionCrfId(edcId);
				edcItemMetadata.setCrfVersionId(versionId);
				edcItemMetadata.setStudyEventDefinitionId(studyEventDefinitionBean.getId());
				edcItemMetadata.setSdvRequired(inputValue);
				int index = getIndexOfMetadataInArray(edcItemMetadata, edcItemMetadataList);
				if (index > -1) {
					edcItemMetadataList.set(index, edcItemMetadata);
				} else {
					edcItemMetadataList.add(edcItemMetadata);
				}

			} else {
				EDCItemMetadata detachedEdcItemMetadata = new EDCItemMetadata(edcItemMetadata);
				detachedEdcItemMetadata.setSdvRequired(inputValue);
				int index = getIndexOfMetadataInArray(detachedEdcItemMetadata, edcItemMetadataList);
				if (index > -1) {
					edcItemMetadataList.set(index, detachedEdcItemMetadata);
				} else {
					edcItemMetadataList.add(detachedEdcItemMetadata);
				}
			}
		}
		edcItemMetadataMap.put(edcId, edcItemMetadataList);
		request.getSession().setAttribute("edcItemMetadataMap", edcItemMetadataMap);
		response.getWriter().write(result);
	}

	private int getIndexOfMetadataInArray(EDCItemMetadata edcItemMetadata, ArrayList<EDCItemMetadata> edcItemMetadataList) {
		EDCItemMetadata reverseMetadata = new EDCItemMetadata(edcItemMetadata);
		reverseMetadata.setBoolSdvRequired(!edcItemMetadata.sdvRequired());
		if (edcItemMetadataList.indexOf(edcItemMetadata) > -1) {
			return edcItemMetadataList.indexOf(edcItemMetadata);
		} else if (edcItemMetadataList.indexOf(reverseMetadata) > -1) {
			return edcItemMetadataList.indexOf(reverseMetadata);
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	private EDCItemMetadata getMetadataFromSessionOrDatabase(HttpSession session, int edcId, int crfVersionId, int itemBeanId, int sedId) {

		EDCItemMetadata metadata = edcItemMetadataService.findByCRFVersionIDEventDefinitionCRFIDAndItemID(crfVersionId, edcId, itemBeanId);
		if (metadata == null) {
			metadata = new EDCItemMetadata();
			metadata.setCrfVersionId(crfVersionId);
			metadata.setItemId(itemBeanId);
			metadata.setEventDefinitionCrfId(edcId);
			metadata.setStudyEventDefinitionId(sedId);
		}

		HashMap<Integer, ArrayList<EDCItemMetadata>> edcItemMetadataMap = (HashMap<Integer, ArrayList<EDCItemMetadata>>)
				session.getAttribute("edcItemMetadataMap");
		if (edcItemMetadataMap == null) {
			return metadata;
		}

		ArrayList<EDCItemMetadata> edcItemMetadataList = edcItemMetadataMap.get(edcId);
		if (edcItemMetadataList == null || edcItemMetadataList.size() == 0) {
			return metadata;
		}

		int index = getIndexOfMetadataInArray(metadata, edcItemMetadataList);
		if (index > -1) {
			return edcItemMetadataList.get(index);
		} else {
			return metadata;
		}
	}
}
