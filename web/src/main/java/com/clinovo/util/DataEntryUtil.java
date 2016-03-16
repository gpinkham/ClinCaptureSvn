package com.clinovo.util;

import com.clinovo.enums.CurrentDataEntryStage;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Data Entry Util.
 */
@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public final class DataEntryUtil {

	private DataEntryUtil() {
	}

	/**
	 * Get group item input name.
	 * @param digb DisplayItemGroupBean
	 * @param ordinal int
	 * @param dib DisplayItemBean
	 * @return String
	 */
	public static String getGroupItemInputName(DisplayItemGroupBean digb, int ordinal, DisplayItemBean dib) {
		return digb.getItemGroupBean().getOid() + "_" + ordinal + getInputName(dib);
	}

	/**
	 * @param dib A DisplayItemBean representing an input on the CRF.
	 * @return The name of the input in the HTML form.
	 */
	public static String getInputName(DisplayItemBean dib) {
		ItemBean ib = dib.getItem();
		return "input" + ib.getId();
	}

	/**
	 *
	 * @param dynamicsMetadataService DynamicsMetadataService
	 * @param dib DisplayItemBean
	 * @param ecb EventCRFBean
	 * @return DisplayItemBean
	 */
	public static DisplayItemBean runDynamicsItemCheck(DynamicsMetadataService dynamicsMetadataService,
													   DisplayItemBean dib, EventCRFBean ecb) {
		if (!dib.getMetadata().isShowItem()) {
			boolean showItem = dynamicsMetadataService.isShown(dib.getItem().getId(), ecb, dib.getData());
			dib.getMetadata().setShowItem(showItem);
		}
		return dib;
	}

	/**
	 *
	 * @param fp FormProcessor
	 * @param oid String
	 * @param i int
	 * @return boolean
	 */
	public static boolean rowPresentInRequest(FormProcessor fp, String oid, int i) {
		return fp.getStartsWith(oid + "_" + i + "input")
				|| !StringUtil.isBlank(fp.getString(oid + "_" + i + ".newRow"));
	}

	/**
	 * Read in form values and write them to a display item bean. Note that this results in the form value being written
	 * to both the response set bean and the item data bean. The ResponseSetBean is used to display preset values on the
	 * form in the event of error, and the ItemDataBean is used to send values to the database.
	 *
	 * @param dib     The DisplayItemBean to write data into.
	 * @param request HttpServletRequest
	 * @return The DisplayItemBean, with form data loaded.
	 */
	public static DisplayItemBean loadFormValue(DisplayItemBean dib, HttpServletRequest request) {
		String inputName = getInputName(dib);
		FormProcessor fp = new FormProcessor(request);
		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();

		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
			dib.loadFormValue(fp.getStringArray(inputName));
		} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CALCULATION)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.GROUP_CALCULATION)) {
			dib.loadFormValue(dib.getData().getValue());
			ResponseOptionBean rob = (ResponseOptionBean) dib.getMetadata().getResponseSet().getOptions().get(0);
		} else {
			dib.loadFormValue(fp.getString(inputName));
		}
		return dib;
	}

	/**
	 * Get current data entry stage from request.
	 * @param request HttpServletRequest
	 * @return CurrentDataEntryStage
	 */
	public static CurrentDataEntryStage getDataEntryStageFromRequest(HttpServletRequest request) {
		return (CurrentDataEntryStage) request.getAttribute("currentDataEntryStage");
	}

	/**
	 * Get section index in table of contents.
	 * @param sb SectionBean
	 * @param toc DisplayTableOfContentsBean
	 * @param sectionIdsInToc LinkedList<Integer>
	 * @return  int
	 */
	public static int getSectionIndexInToc(SectionBean sb, DisplayTableOfContentsBean toc, LinkedList<Integer> sectionIdsInToc) {
		ArrayList sectionBeans = new ArrayList<SectionBean>();
		int index = -1;
		if (toc != null) {
			sectionBeans = toc.getSections();
		}
		if (sectionBeans != null && sectionBeans.size() > 0) {
			for (int i = 0; i < sectionIdsInToc.size(); ++i) {
				if (sb.getId() == sectionIdsInToc.get(i)) {
					index = i;
					break;
				}
			}
		}
		return index;
	}

	/**
	 * Get list of Section Ids in DisplayTableOfContentsBean.
	 * @param toc DisplayTableOfContentsBean
	 * @return LinkedList<Integer>
	 */
	public static LinkedList<Integer> getSectionIdsInToc(DisplayTableOfContentsBean toc) {
		LinkedList<Integer> ids = new LinkedList<Integer>();
		if (toc != null) {
			ArrayList<SectionBean> sectionBeans = toc.getSections();
			if (sectionBeans != null && sectionBeans.size() > 0) {
				for (SectionBean s : sectionBeans) {
					ids.add(s.getId());
				}
			}
		}
		return ids;
	}

	/**
	 * Get previous SectionBean.
	 * @param toc DisplayTableOfContentsBean
	 * @param sbPos int
	 * @return SectionBean
	 */
	public static SectionBean getPrevSection(DisplayTableOfContentsBean toc, int sbPos) {
		SectionBean p = new SectionBean();
		ArrayList<SectionBean> sectionBeans;
		if (toc != null) {
			sectionBeans = toc.getSections();
			if (sbPos > 0) {
				p = sectionBeans.get(sbPos - 1);
			}
		}
		return p != null && p.getId() > 0 ? p : new SectionBean();
	}

	/**
	 * Get next SectionBean.
	 * @param toc DisplayTableOfContentsBean
	 * @param sbPos int
	 * @return SectionBean
	 */
	public static SectionBean getNextSection(DisplayTableOfContentsBean toc, int sbPos) {
		SectionBean n = new SectionBean();
		ArrayList<SectionBean> sectionBeans;
		if (toc != null) {
			sectionBeans = toc.getSections();
			int size = sectionBeans.size();
			if (sbPos >= 0 && size > 1 && sbPos < size - 1) {
				n = sectionBeans.get(sbPos + 1);
			}
		}
		return n != null && n.getId() > 0 ? n : new SectionBean();
	}
}
