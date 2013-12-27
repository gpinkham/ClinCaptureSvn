package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.akaza.openclinica.util.DiscrepancyShortcutsAnalyzer;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings("serial")
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		FormProcessor fp = new FormProcessor(request);

		StudyBean currentStudy = getCurrentStudy(request);
        List<DiscrepancyNoteBean> allNotes = new ArrayList<DiscrepancyNoteBean>();
		DiscrepancyNoteUtil dNoteUtil = new DiscrepancyNoteUtil();
        List<SectionBean> allSections = new ArrayList<SectionBean>();

		SectionDAO sdao = new SectionDAO(getDataSource());
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
			
			allNotes = extractCoderNotes(allNotes, request);
			List<DiscrepancyNoteBean> eventCrfNotes = dndao.findOnlyParentEventCRFDNotesFromEventCRF(ecb);
			if (!eventCrfNotes.isEmpty()) {
				allNotes.addAll(eventCrfNotes);
			}
            allSections = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
		}

		List<DiscrepancyNoteThread> noteThreads = dNoteUtil.createThreadsOfParents(allNotes, getDataSource(),
				currentStudy, null, -1, true);

		DiscrepancyShortcutsAnalyzer.prepareDnShortcutLinks(request, ecb, ifmdao, eventDefinitionCRFId, allSections,
				noteThreads);

		forwardPage(Page.UPDATE_CRF_HEADER_PAGE, request, response);
	}
}
