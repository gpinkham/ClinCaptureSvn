/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import com.clinovo.util.ValidatorHelper;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.NullValue;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InconsistentStateException;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ssachs
 */
@SuppressWarnings({"rawtypes","unchecked", "serial"})
public class InitialDataEntryServletOld extends Controller {
	// these inputs are used when other servlets redirect you here
	// this is most typically the case when the user enters data and clicks the
	// "Previous" or "Next" button
	public static final String INPUT_EVENT_CRF = "event";
	public static final String INPUT_SECTION = "section";

	// these inputs come from the form or from another jsp, such as the
	// tableOfContents.jsp
	public static final String INPUT_EVENT_CRF_ID = "eventCRFId";
	public static final String INPUT_SECTION_ID = "sectionId";
	public static final String INPUT_IGNORE_PARAMETERS = "ignore";
	public static final String INPUT_CHECK_INPUTS = "checkInputs";

	// this comes from the form
	public static final String RESUME_LATER = "submittedResume";
	public static final String GO_PREVIOUS = "submittedPrev";
	public static final String GO_NEXT = "submittedNext";

	public static final String BEAN_DISPLAY = "section";

    private class ObjectPairs {
        private EventCRFBean ecb;
        private SectionBean sb;
        private EventDefinitionCRFBean edcb;
        public ObjectPairs(EventCRFBean ecb, SectionBean sb) {
            this.ecb = ecb;
            this.sb = sb;
        }
    }

	private ObjectPairs getInputBeans(HttpServletRequest request) {
        FormProcessor fp = new FormProcessor(request);
        EventCRFDAO ecdao = getEventCRFDAO();
        SectionDAO sdao = getSectionDAO();

        EventCRFBean ecb = (EventCRFBean) request.getAttribute(INPUT_EVENT_CRF);
		if (ecb == null) {
			int eventCRFId = fp.getInt(INPUT_EVENT_CRF_ID, true);
			ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
		}

        SectionBean sb = (SectionBean) request.getAttribute(INPUT_SECTION);
		if (sb == null) {
			int sectionId = fp.getInt(INPUT_SECTION_ID, true);
			sb = (SectionBean) sdao.findByPK(sectionId);
		}

		return new ObjectPairs(ecb, sb);
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);

        ObjectPairs objectPairs = getInputBeans(request);

		if (!objectPairs.ecb.isActive()) {
			throw new InconsistentStateException(Page.SUBMIT_DATA, resexception.getString("event_not_exists"));
		}

        SectionDAO sdao = getSectionDAO();
		DisplaySectionBean section = getDisplayBean(request, objectPairs);
		SectionBean previousSec = sdao.findPrevious(objectPairs.ecb, objectPairs.sb);
		SectionBean nextSec = sdao.findNext(objectPairs.ecb, objectPairs.sb);
		section.setFirstSection(!previousSec.isActive());
		section.setLastSection(!nextSec.isActive());

		Boolean b = (Boolean) request.getAttribute(INPUT_IGNORE_PARAMETERS);

        FormProcessor fp = new FormProcessor(request);
		if (!fp.isSubmitted() || b != null) {
			// TODO: prevent data enterer from seeing results of first round of
			// data entry, if this is second round
			request.setAttribute(BEAN_DISPLAY, section);
			forwardPage(Page.INITIAL_DATA_ENTRY, request, response);
		} else {
            HashMap errors = new HashMap();
			ArrayList items = section.getItems();

			if (fp.getBoolean(INPUT_CHECK_INPUTS)) {
				Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));

				// TODO: always validate null values
				for (int i = 0; i < items.size(); i++) {
					DisplayItemBean dib = (DisplayItemBean) items.get(i);
					dib = validateDisplayItemBean(fp, objectPairs.edcb, v, dib);

					ArrayList children = dib.getChildren();
					for (int j = 0; j < children.size(); j++) {
						DisplayItemBean child = (DisplayItemBean) children.get(j);
						child = validateDisplayItemBean(fp, objectPairs.edcb, v, child);
						children.set(j, child);
					}

					dib.setChildren(children);
					items.set(i, dib);
				}

				// we have to do this since we loaded all the form values into
				// the display item beans above
				section.setItems(items);

				errors = v.validate();
			} else {
				for (int i = 0; i < items.size(); i++) {
					DisplayItemBean dib = (DisplayItemBean) items.get(i);
					dib = loadFormValue(fp, dib);

					ArrayList children = dib.getChildren();
					for (int j = 0; j < children.size(); j++) {
						DisplayItemBean child = (DisplayItemBean) children.get(j);
						child = loadFormValue(fp, child);
						children.set(j, child);
					}

					dib.setChildren(children);
					items.set(i, dib);
				}
				// we have to do this since we loaded all the form values into
				// the display item beans above
				section.setItems(items);

			}

			if (!errors.isEmpty()) {

				request.setAttribute(BEAN_DISPLAY, section);
				setInputMessages(errors, request);
				addPageMessage(respage.getString("errors_in_submission_see_below_details"), request);
				addPageMessage(respage.getString("to_override_these_errors"), request);
				forwardPage(Page.INITIAL_DATA_ENTRY, request, response);
			} else {
				ItemDataDAO iddao = getItemDataDAO();
				boolean success = true;
				boolean temp = true;

				items = section.getItems();
				for (int i = 0; i < items.size(); i++) {
					DisplayItemBean dib = (DisplayItemBean) items.get(i);
					temp = writeToDB(ub, dib, iddao, objectPairs);
					success = success && temp;

					ArrayList childItems = dib.getChildren();
					for (int j = 0; j < childItems.size(); j++) {
						DisplayItemBean child = (DisplayItemBean) childItems.get(j);
						temp = writeToDB(ub, child, iddao, objectPairs);
						success = success && temp;
					}
				}

				request.setAttribute(INPUT_IGNORE_PARAMETERS, Boolean.TRUE);
				if (!success) {
					addPageMessage(resexception.getString("database_error"), request);
					request.setAttribute(BEAN_DISPLAY, section);
					forwardPage(Page.TABLE_OF_CONTENTS_SERVLET, request, response);
				} else {
					boolean forwardingSucceeded = false;

					if (!fp.getString(GO_PREVIOUS).equals("")) {
						if (previousSec.isActive()) {
							forwardingSucceeded = true;
							request.setAttribute(INPUT_EVENT_CRF, objectPairs.ecb);
							request.setAttribute(INPUT_SECTION, previousSec);
							forwardPage(Page.INITIAL_DATA_ENTRY_SERVLET, request, response);
						}
					} else if (!fp.getString(GO_NEXT).equals("")) {
						if (nextSec.isActive()) {
							forwardingSucceeded = true;
							request.setAttribute(INPUT_EVENT_CRF, objectPairs.ecb);
							request.setAttribute(INPUT_SECTION, nextSec);
							forwardPage(Page.INITIAL_DATA_ENTRY_SERVLET, request, response);
						}
					}

					if (!forwardingSucceeded) {
						request.setAttribute(TableOfContentsServlet.INPUT_EVENT_CRF_BEAN, objectPairs.ecb);
						addPageMessage(respage.getString("data_saved_continue_later"), request);
						forwardPage(Page.TABLE_OF_CONTENTS_SERVLET, request, response);
					}
				}
			}
		}
	}

	private DisplayItemBean loadFormValue(FormProcessor fp, DisplayItemBean dib) {
		String inputName = "input" + dib.getItem().getId();
		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();

		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
			dib.loadFormValue(fp.getStringArray(inputName));
		} else {
			dib.loadFormValue(fp.getString(inputName));
		}

		return dib;
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		ObjectPairs objectPairs = getInputBeans(request);

		DataEntryStage stage = objectPairs.ecb.getStage();
		Role r = currentRole.getRole();

		if (stage.equals(DataEntryStage.UNCOMPLETED)) {
			if (!SubmitDataServlet.maySubmitData(ub, currentRole)) {
				String exceptionName = resexception.getString("no_permission_to_perform_data_entry");
				String noAccessMessage = respage.getString("you_may_not_perform_data_entry_on_a_CRF") + " "
						+ respage.getString("change_study_contact_study_coordinator");

				addPageMessage(noAccessMessage, request);
				throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
			}
		} else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY)) {
			if (ub.getId() != objectPairs.ecb.getOwnerId() && !r.equals(Role.STUDY_DIRECTOR) && !r.equals(Role.STUDY_ADMINISTRATOR)
					&& !r.equals(Role.SYSTEM_ADMINISTRATOR)) {
				UserAccountDAO udao = getUserAccountDAO();
				String ownerName = ((UserAccountBean) udao.findByPK(objectPairs.ecb.getOwnerId())).getName();
				MessageFormat mf = new MessageFormat("");
				mf.applyPattern(respage.getString("you_may_not_perform_data_entry_on_event_CRF_because_not_owner"));
				Object[] arguments = { ownerName };
				addPageMessage(mf.format(arguments), request);

				throw new InsufficientPermissionException(Page.SUBMIT_DATA,
						resexception.getString("non_owner_attempting_DE_on_event"), "1");
			}
		} else {
			addPageMessage(respage.getString("you_not_enter_data_initial_DE_completed"), request);
			throw new InsufficientPermissionException(Page.SUBMIT_DATA,
					resexception.getString("using_IDE_event_CRF_completed"), "1");
		}

		return;
	}

	private DisplaySectionBean getDisplayBean(HttpServletRequest request, ObjectPairs objectPairs) throws Exception {
		DisplaySectionBean section = new DisplaySectionBean();

		section.setEventCRF(objectPairs.ecb);

        SectionDAO sdao = getSectionDAO();
		if (objectPairs.sb.getParentId() > 0) {
			SectionBean parent = (SectionBean) sdao.findByPK(objectPairs.sb.getParentId());
            objectPairs.sb.setParent(parent);
		}

		section.setSection(objectPairs.sb);

		CRFVersionDAO cvdao = getCRFVersionDAO();
		CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(objectPairs.ecb.getCRFVersionId());
		section.setCrfVersion(cvb);

		CRFDAO cdao = getCRFDAO();
		CRFBean cb = (CRFBean) cdao.findByPK(cvb.getCrfId());
		section.setCrf(cb);

        EventDefinitionCRFDAO edcdao = getEventDefinitionCRFDAO();
		StudyBean study = (StudyBean) request.getSession().getAttribute("study");
        objectPairs.edcb = edcdao.findByStudyEventIdAndCRFVersionId(study, objectPairs.ecb.getStudyEventId(), cvb.getId());
		section.setEventDefinitionCRF(objectPairs.edcb);

		// setup DAO's here to avoid creating too many objects
        ItemDAO idao = getItemDAO();
        ItemFormMetadataDAO ifmdao = getItemFormMetadataDAO();
        ItemDataDAO iddao = getItemDataDAO();

		// get all the display item beans
		ArrayList displayItems = getParentDisplayItems(objectPairs, idao, ifmdao, iddao);

		// now sort them by ordinal
		Collections.sort(displayItems);

		// now get the child DisplayItemBeans
		for (int i = 0; i < displayItems.size(); i++) {
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(i);
			dib.setChildren(getChildrenDisplayItems(dib, objectPairs));
			dib.loadDBValue();
			displayItems.set(i, dib);
		}

		section.setItems(displayItems);

		return section;
	}

	/**
	 * For each item in this section which is a parent, get a DisplayItemBean corresponding to that item. Note that an
	 * item is a parent iff its parentId == 0.
	 * 
	 * @param objectPairs
	 *            The section whose items we are retrieving.
	 * @return An array of DisplayItemBean objects, one per parent item in the section. Note that there is no guarantee
	 *         on the ordering of the objects.
	 * @throws Exception
	 */
	private ArrayList getParentDisplayItems(ObjectPairs objectPairs, ItemDAO idao,
			ItemFormMetadataDAO ifmdao, ItemDataDAO iddao) throws Exception {
		ArrayList answer = new ArrayList();

		// DisplayItemBean objects are composed of an ItemBean, ItemDataBean and
		// ItemFormDataBean.
		// However the DAOs only provide methods to retrieve one type of bean at
		// a time (per section)
		// the displayItems hashmap allows us to compose these beans into
		// DisplayItemBean objects,
		// while hitting the database only three times
		HashMap displayItems = new HashMap();

		ArrayList items = idao.findAllParentsBySectionId(objectPairs.sb.getId());
		for (int i = 0; i < items.size(); i++) {
			DisplayItemBean dib = new DisplayItemBean();
			dib.setEventDefinitionCRF(objectPairs.edcb);
			ItemBean ib = (ItemBean) items.get(i);
			dib.setItem(ib);
			displayItems.put(new Integer(dib.getItem().getId()), dib);
		}

		ArrayList metadata = ifmdao.findAllBySectionId(objectPairs.sb.getId());
		for (int i = 0; i < metadata.size(); i++) {
			ItemFormMetadataBean ifmb = (ItemFormMetadataBean) metadata.get(i);
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(new Integer(ifmb.getItemId()));
			if (dib != null) {
				dib.setMetadata(ifmb);
				displayItems.put(new Integer(ifmb.getItemId()), dib);
			}
		}

		ArrayList data = iddao.findAllBySectionIdAndEventCRFId(objectPairs.sb.getId(), objectPairs.ecb.getId());
		for (int i = 0; i < data.size(); i++) {
			ItemDataBean idb = (ItemDataBean) data.get(i);
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(new Integer(idb.getItemId()));
			if (dib != null) {
				dib.setData(idb);
				displayItems.put(new Integer(idb.getItemId()), dib);
			}
		}

		Iterator hmIt = displayItems.keySet().iterator();
		while (hmIt.hasNext()) {
			Integer key = (Integer) hmIt.next();
			DisplayItemBean dib = (DisplayItemBean) displayItems.get(key);
			answer.add(dib);
		}

		return answer;
	}

	/**
	 * Get the DisplayItemBean objects corresponding to the items which are children of the specified parent.
	 * 
	 * @param parent
	 *            The item whose children are to be retrieved.
	 * @return An array of DisplayItemBean objects corresponding to the items which are children of parent, and are
	 *         sorted by column number (ascending), then ordinal (ascending).
	 */
	private ArrayList getChildrenDisplayItems(DisplayItemBean parent, ObjectPairs objectPairs) {
		ArrayList answer = new ArrayList();

        ItemDAO idao = getItemDAO();
        ItemDataDAO iddao = getItemDataDAO();
        ItemFormMetadataDAO ifmdao = getItemFormMetadataDAO();

		int parentId = parent.getItem().getId();
		ArrayList childItemBeans = idao.findAllByParentIdAndCRFVersionId(parentId, objectPairs.ecb.getCRFVersionId());

		for (int i = 0; i < childItemBeans.size(); i++) {
			ItemBean child = (ItemBean) childItemBeans.get(i);
			ItemDataBean data = iddao.findByItemIdAndEventCRFId(child.getId(), objectPairs.ecb.getId());
			ItemFormMetadataBean metadata = ifmdao.findByItemIdAndCRFVersionId(child.getId(), objectPairs.ecb.getCRFVersionId());

			// DisplayItemBean dib = new DisplayItemBean(objectPairs.edcb);
			DisplayItemBean dib = new DisplayItemBean();
			dib.setEventDefinitionCRF(objectPairs.edcb);
			dib.setItem(child);
			dib.setData(data);
			dib.setMetadata(metadata);
			dib.loadDBValue();

			answer.add(dib);
		}

		// this is a pretty slow and memory intensive way to sort... see if we
		// can have the db do this instead
		// ChildDisplayItemBeanComparator childSorter =
		// ChildDisplayItemBeanComparator.getInstance();
		// Collections.sort(answer, childSorter);
		Collections.sort(answer);

		return answer;
	}

	private DisplayItemBean validateDisplayItemBean(FormProcessor fp, EventDefinitionCRFBean edcb, Validator v, DisplayItemBean dib) {
		ItemBean ib = dib.getItem();
		ItemDataType idt = ib.getDataType();
		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();

		String inputName = "input" + ib.getId();

		// note that this step sets us up both for
		// displaying the data on the form again, in the event of an error
		// and sending the data to the database, in the event of no error
		dib = loadFormValue(fp, dib);

		// types TEL and ED are not supported yet
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.CALCULATION)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.GROUP_CALCULATION)) {
			boolean isNull = false;
			ArrayList nullValues = edcb.getNullValuesList();
			for (int i = 0; i < nullValues.size(); i++) {
				NullValue nv = (NullValue) nullValues.get(i);
				if (nv.getName().equals(fp.getString(inputName))) {
					isNull = true;
				}
			}

			if (!isNull) {
				if (idt.equals(ItemDataType.ST)) {
					v.addValidation(inputName, Validator.NO_BLANKS);
				} else if (idt.equals(ItemDataType.INTEGER)) {
					v.addValidation(inputName, Validator.NO_BLANKS);
					v.addValidation(inputName, Validator.IS_AN_INTEGER);
				} else if (idt.equals(ItemDataType.REAL)) {
					v.addValidation(inputName, Validator.NO_BLANKS);
					v.addValidation(inputName, Validator.IS_A_NUMBER);
				} else if (idt.equals(ItemDataType.BL)) {
					// there is no validation here since this data type is
					// explicitly allowed to be null
					// if the string input for this field parses to a non-zero
					// number, the value will be true; otherwise, 0
				} else if (idt.equals(ItemDataType.BN)) {
					v.addValidation(inputName, Validator.NO_BLANKS);
				} else if (idt.equals(ItemDataType.SET)) {
					v.addValidation(inputName, Validator.NO_BLANKS_SET);
					v.addValidation(inputName, Validator.IN_RESPONSE_SET_SINGLE_VALUE, dib.getMetadata()
							.getResponseSet());
				}
			}
		} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
			v.addValidation(inputName, Validator.NO_BLANKS_SET);
			v.addValidation(inputName, Validator.IN_RESPONSE_SET_SINGLE_VALUE, dib.getMetadata().getResponseSet());
		} else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.CHECKBOX)
				|| rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECTMULTI)) {
			v.addValidation(inputName, Validator.NO_BLANKS_SET);
			v.addValidation(inputName, Validator.IN_RESPONSE_SET, dib.getMetadata().getResponseSet());
		}

		return dib;
	}

	private boolean writeToDB(UserAccountBean ub, DisplayItemBean dib, ItemDataDAO iddao, ObjectPairs objectPairs) {
		ItemDataBean idb = dib.getData();

		if (idb.getValue().equals("")) {
			idb.setStatus(Status.AVAILABLE);
		} else {
			Status newStatus;
			newStatus = objectPairs.edcb.isDoubleEntry() ? Status.PENDING : Status.UNAVAILABLE;
			idb.setStatus(newStatus);
		}

		if (!idb.isActive()) {
			idb.setCreatedDate(new Date());
			idb.setOwner(ub);
			idb.setItemId(dib.getItem().getId());
			idb.setEventCRFId(objectPairs.ecb.getId());

			idb = (ItemDataBean) iddao.create(idb);
		} else {
			idb = (ItemDataBean) iddao.update(idb);
		}

		return idb.isActive();
	}

}
