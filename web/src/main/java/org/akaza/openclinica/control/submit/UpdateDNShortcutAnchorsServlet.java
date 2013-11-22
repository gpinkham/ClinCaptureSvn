package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.util.DiscrepancyShortcutsAnalyzer;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
@Component
public class UpdateDNShortcutAnchorsServlet extends Controller {

	public static final String DISCREPANCY_SHORTCUTS_ANALYZER = "discrepancyShortcutsAnalyzer";
	public static final String DISPLAY_ITEM_BEAN = "displayItemBean";
	public static final String EVENT_CRF_ID = "eventCRFId";
	public static final String ROW_COUNT = "rowCount";
	public static final String ITEM_ID = "itemId";
	public static final String FIELD = "field";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		//
	}

	@SuppressWarnings("unchecked")
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);

		List<DiscrepancyNoteBean> allNotes = new ArrayList<DiscrepancyNoteBean>();
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute(STUDY);

		ItemDataDAO iddao = new ItemDataDAO(getDataSource());
		DiscrepancyNoteUtil dNoteUtil = new DiscrepancyNoteUtil();
		DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(getDataSource());
        
		int itemId = fp.getInt(ITEM_ID);
		String field = fp.getString(FIELD);
		int rowCount = fp.getInt(ROW_COUNT);
		int eventCRFId = fp.getInt(EVENT_CRF_ID);
		FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) request.getSession().getAttribute(
				AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
		if (fdn.getFieldNotes() != null) {
			for (DiscrepancyNoteBean discrepancyNoteBean : (List<DiscrepancyNoteBean>) fdn.getNotes(field)) {
				if (discrepancyNoteBean.getId() == 0) {
					allNotes.add(discrepancyNoteBean);
					if (eventCRFId == 0) {
						eventCRFId = discrepancyNoteBean.getEventCRFId();
					}
				}
			}
		}

		if (eventCRFId > 0 && itemId > 0) {
			List<ItemDataBean> itemDataList = iddao.findAllByEventCRFIdAndItemId(eventCRFId, itemId);
			if (itemDataList != null) {
				for (ItemDataBean itemDataBean : itemDataList) {
					if (itemDataBean.getOrdinal() == rowCount + 1) {
						allNotes.addAll(dndao.findExistingNotesForItemData(itemDataBean.getId()));
						break;
					}
				}
			}
		}

		ItemDataBean itemDataBean = new ItemDataBean();
		itemDataBean.setItemId(itemId);
		itemDataBean.setOrdinal(fp.getString(ROW_COUNT).isEmpty() ? -1 : rowCount);
		DisplayItemBean dib = new DisplayItemBean();
		dib.setDbData(itemDataBean);
		request.setAttribute(DISPLAY_ITEM_BEAN, dib);
		DiscrepancyShortcutsAnalyzer discrepancyShortcutsAnalyzer = new DiscrepancyShortcutsAnalyzer();
		request.setAttribute(DISCREPANCY_SHORTCUTS_ANALYZER, discrepancyShortcutsAnalyzer);
		List<DiscrepancyNoteThread> noteThreads = dNoteUtil.createThreadsOfParents(allNotes, getDataSource(),
				currentStudy, null, -1, true);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, dib, noteThreads);

		forwardPage(Page.UPDATE_DN_SHORTCUT_ANCHORS_PAGE, request, response);
	}
}
