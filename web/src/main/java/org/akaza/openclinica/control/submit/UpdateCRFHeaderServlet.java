package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.util.DiscrepancyShortcutsAnalyzer;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * UpdateCRFHeaderServlet.
 */
@SuppressWarnings({ "serial", "unchecked", "unused" })
@Component
public class UpdateCRFHeaderServlet extends Controller {

	public static final String EVENT_DEFINITION_CRF_ID = "eventDefinitionCRFId";
	public static final String EVENT_CRF_ID = "eventCRFId";
	public static final String TRUE = "true";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);

		StudyBean currentStudy = getCurrentStudy(request);
		List<DiscrepancyNoteBean> allNotes = new ArrayList<DiscrepancyNoteBean>();
		DiscrepancyNoteUtil dNoteUtil = new DiscrepancyNoteUtil();
		List<SectionBean> allSections = new ArrayList<SectionBean>();

		SectionDAO sdao = new SectionDAO(getDataSource());
		ItemDataDAO iddao = new ItemDataDAO(getDataSource());
		EventCRFDAO ecdao = new EventCRFDAO(getDataSource());
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
		ItemFormMetadataDAO ifmdao = new ItemFormMetadataDAO(getDataSource());

		EventCRFBean ecb = new EventCRFBean();
		int eventCRFId = fp.getInt(EVENT_CRF_ID);
		int eventDefinitionCRFId = fp.getInt(EVENT_DEFINITION_CRF_ID);
		FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession().getAttribute(
				AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
		if (fdn.getFieldNotes() != null) {
			for (Object list : fdn.getFieldNotes().values()) {
				for (DiscrepancyNoteBean discrepancyNoteBean : (List<DiscrepancyNoteBean>) list) {
					if (discrepancyNoteBean.getId() == 0) {
						allNotes.add(discrepancyNoteBean);
						if (eventCRFId == 0) {
							eventCRFId = discrepancyNoteBean.getEventCRFId();
						}
					}
				}
			}
		}

		if (eventCRFId > 0) {
			ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
			allNotes.addAll(dndao.findAllTopNotesByEventCRF(eventCRFId));
			allNotes = filterNotesByUserRole(allNotes, request);
			allSections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
		}

		List<DiscrepancyNoteThread> noteThreads = dNoteUtil.createThreadsOfParents(allNotes, getDataSource(),
				currentStudy, null, -1, true);

		DiscrepancyShortcutsAnalyzer.prepareDnShortcutLinks(request, ecb, ifmdao, eventDefinitionCRFId, allSections,
				noteThreads);
		DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer = (DiscrepancyShortcutsAnalyzer) request
				.getAttribute(DiscrepancyShortcutsAnalyzer.DISCREPANCY_SHORTCUTS_ANALYZER);

		JSONArray jsonArray = new JSONArray();
		int totalItems = fp.getInt("totalItems");
		for (int i = 1; i <= totalItems; i++) {
			String rowCountAttrName = "rowCount_".concat(Integer.toString(i));
			int rowCount = fp.getInt(rowCountAttrName);
			String rowCountValue = fp.getString(rowCountAttrName);
			int itemId = fp.getInt("itemId_".concat(Integer.toString(i)));
			String field = fp.getString("field_".concat(Integer.toString(i)));

			ItemDataBean itemDataBean = new ItemDataBean();
			itemDataBean.setItemId(itemId);
			itemDataBean.setOrdinal(rowCount + 1);
			itemDataBean.setEventCRFId(eventCRFId);
			DisplayItemBean dib = new DisplayItemBean();
			dib.setField(field);
			dib.setDbData(itemDataBean);

			DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, dib, noteThreads, true);

			JSONObject jsonObj = new JSONObject();
			jsonObj.put("rowCount", rowCountValue);
			jsonObj.put("itemId", itemDataBean.getItemId());
			jsonObj.put("field", field);
			jsonObj.put("newDn", dib.getNewDn());
			jsonObj.put("updatedDn", dib.getUpdatedDn());
			jsonObj.put("resolutionProposedDn", dib.getResolutionProposedDn());
			jsonObj.put("closedDn", dib.getClosedDn());
			jsonObj.put("annotationDn", dib.getAnnotationDn());
			jsonArray.put(jsonObj);
		}

		JSONObject jsonObject = new JSONObject();

		jsonObject.put("totalNew", discrepancyShortcutsAnalyzer.getTotalNew());
		jsonObject.put("totalUpdated", discrepancyShortcutsAnalyzer.getTotalUpdated());
		jsonObject.put("totalResolutionProposed", discrepancyShortcutsAnalyzer.getTotalResolutionProposed());
		jsonObject.put("totalClosed", discrepancyShortcutsAnalyzer.getTotalClosed());
		jsonObject.put("totalAnnotations", discrepancyShortcutsAnalyzer.getTotalAnnotations());

		jsonObject.put("sectionTotalNew", discrepancyShortcutsAnalyzer.getSectionTotalNew());
		jsonObject.put("firstNewDnLink", discrepancyShortcutsAnalyzer.getFirstNewDnLink());
		jsonObject.put("nextNewDnLink", discrepancyShortcutsAnalyzer.getNextNewDnLink());

		jsonObject.put("sectionTotalUpdated", discrepancyShortcutsAnalyzer.getSectionTotalUpdated());
		jsonObject.put("firstUpdatedDnLink", discrepancyShortcutsAnalyzer.getFirstUpdatedDnLink());
		jsonObject.put("nextUpdatedDnLink", discrepancyShortcutsAnalyzer.getNextUpdatedDnLink());

		jsonObject.put("sectionTotalResolutionProposed",
				discrepancyShortcutsAnalyzer.getSectionTotalResolutionProposed());
		jsonObject.put("firstResolutionProposedDnLink", discrepancyShortcutsAnalyzer.getFirstResolutionProposedLink());
		jsonObject.put("nextResolutionProposedDnLink", discrepancyShortcutsAnalyzer.getNextResolutionProposedLink());

		jsonObject.put("sectionTotalClosed", discrepancyShortcutsAnalyzer.getSectionTotalClosed());
		jsonObject.put("firstClosedDnLink", discrepancyShortcutsAnalyzer.getFirstClosedDnLink());
		jsonObject.put("nextClosedDnLink", discrepancyShortcutsAnalyzer.getNextClosedDnLink());

		jsonObject.put("sectionTotalAnnotations", discrepancyShortcutsAnalyzer.getSectionTotalAnnotations());
		jsonObject.put("firstAnnotationDnLink", discrepancyShortcutsAnalyzer.getFirstAnnotationLink());
		jsonObject.put("nextAnnotationDnLink", discrepancyShortcutsAnalyzer.getNextAnnotationLink());

		jsonObject.put("items", jsonArray);

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.getWriter().write(jsonObject.toString());
	}
}
