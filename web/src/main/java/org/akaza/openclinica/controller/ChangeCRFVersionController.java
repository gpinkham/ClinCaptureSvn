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

package org.akaza.openclinica.controller;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.navigation.Navigation;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.util.DAOWrapper;
import com.clinovo.util.SubjectEventStatusUtil;

/**
 * Controller handles requests for item data migration between two CRF versions for specific event CRF.
 */
@Controller("changeCRFVersionController")
@SuppressWarnings({"unchecked"})
public class ChangeCRFVersionController extends SpringController {
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int SIX = 6;
	public static final int SEVEN = 7;
	public static final int EIGHT = 8;
	public static final int ZERO = 0;

	/**
	 * Public constructor for controller.
	 */
	public ChangeCRFVersionController() {
	}

	/**
	 * Allows user to select new CRF version.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param crfId
	 *            int
	 * @param crfName
	 *            String
	 * @param crfVersionId
	 *            int
	 * @param crfVersionName
	 *            String
	 * @param studySubjectLabel
	 *            String
	 * @param studySubjectId
	 *            int
	 * @param eventCRFId
	 *            int
	 * @param eventDefinitionCRFId
	 *            int
	 * @return ModelMap
	 */
	@RequestMapping(value = "/managestudy/chooseCRFVersion", method = RequestMethod.GET)
	public ModelMap chooseCRFVersion(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("crfId") int crfId, @RequestParam("crfName") String crfName,
			@RequestParam("crfversionId") int crfVersionId, @RequestParam("crfVersionName") String crfVersionName,
			@RequestParam("studySubjectLabel") String studySubjectLabel,
			@RequestParam("studySubjectId") int studySubjectId, @RequestParam("eventCRFId") int eventCRFId,
			@RequestParam("eventDefinitionCRFId") int eventDefinitionCRFId) {

		// to be removed for aquamarine
		if (!mayProceed(request)) {
			try {
				response.sendRedirect(request.getContextPath() + "/MainMenu?message=authentication_failed");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		resetPanel(request);
		ModelMap gridMap = new ModelMap();

		request.setAttribute("eventCRFId", eventCRFId);
		request.setAttribute("studySubjectLabel", studySubjectLabel);
		request.setAttribute("eventDefinitionCRFId", eventDefinitionCRFId);
		request.setAttribute("studySubjectId", studySubjectId);
		request.setAttribute("crfId", crfId);
		request.setAttribute("crfName", crfName);
		request.setAttribute("crfversionId", crfVersionId);
		request.setAttribute("crfVersionName", crfVersionName.trim());

		ArrayList<String> pageMessages = (ArrayList<String>) request.getAttribute("pageMessages");
		if (pageMessages == null) {
			pageMessages = new ArrayList<String>();
		}

		request.setAttribute("pageMessages", pageMessages);
		Object errorMessage = request.getParameter("errorMessage");
		if (errorMessage != null) {
			pageMessages.add((String) errorMessage);
		}
		// get CRF by ID with all versions
		// create List of all versions (label + value)
		// set default CRF version label

		// from event_crf get
		StudyBean study = (StudyBean) request.getSession().getAttribute("study");

		CRFDAO cdao = new CRFDAO(dataSource);
		CRFBean crfBean = (CRFBean) cdao.findByPK(crfId);
		CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
		ArrayList<CRFVersionBean> versions = (ArrayList<CRFVersionBean>) crfVersionDao.findAllActiveByCRF(crfId);
		StudyEventDefinitionDAO sfed = new StudyEventDefinitionDAO(dataSource);
		StudyEventDefinitionBean sedb = sfed.findByEventDefinitionCRFId(eventDefinitionCRFId);
		request.setAttribute("eventName", sedb.getName());

		EventCRFDAO ecdao = new EventCRFDAO(dataSource);
		EventCRFBean ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);

		StudyEventDAO sedao = new StudyEventDAO(dataSource);
		StudyEventBean seb = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
		request.setAttribute("eventCreateDate", formatDate(seb.getCreatedDate()));
		if (sedb.isRepeating()) {
			request.setAttribute("eventOrdinal", seb.getSampleOrdinal());
		}
		if (study.getParentStudyId() > 0) {
			EventDefinitionCRFDAO edfdao = new EventDefinitionCRFDAO(dataSource);
			EventDefinitionCRFBean edf = (EventDefinitionCRFBean) edfdao.findByPK(eventDefinitionCRFId);

			if (!edf.getSelectedVersionIds().equals("")) {
				String[] versionIds = edf.getSelectedVersionIds().split(",");
				HashMap<String, String> tmp = new HashMap<String, String>(versionIds.length);
				for (String vs : versionIds) {
					tmp.put(vs, vs);
				}
				ArrayList<CRFVersionBean> siteVersions = new ArrayList<CRFVersionBean>(versions.size());

				for (CRFVersionBean vs : versions) {
					if (tmp.get(String.valueOf(vs.getId())) != null) {
						siteVersions.add(vs);
					}
				}
				versions = siteVersions;
			}

		}
		crfBean.setVersions(versions);
		gridMap.addAttribute("numberOfVersions", crfBean.getVersions().size() + 1);
		gridMap.addAttribute("crfBean", crfBean);

		return gridMap;
	}

	/**
	 * Displays two set of columns for user to confirm his decision to switch to a new version of CRF field name | OID |
	 * field value.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param crfId
	 *            int
	 * @param crfName
	 *            String
	 * @param crfVersionId
	 *            int
	 * @param crfVersionName
	 *            String
	 * @param studySubjectLabel
	 *            String
	 * @param studySubjectId
	 *            int
	 * @param eventCRFId
	 *            int
	 * @param eventDefinitionCRFId
	 *            int
	 * @param selectedVersionId
	 *            int
	 * @param selectedVersionName
	 *            String
	 * @param eventName
	 *            String
	 * @param eventCreateDate
	 *            String
	 * @param eventOrdinal
	 *            String
	 * @return ModelMap
	 */
	@RequestMapping(value = "/managestudy/confirmCRFVersionChange", method = RequestMethod.POST)
	public ModelMap confirmCRFVersionChange(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "crfId", required = false) int crfId,
			@RequestParam(value = "crfName", required = false) String crfName,
			@RequestParam(value = "crfversionId", required = false) int crfVersionId,
			@RequestParam(value = "crfVersionName", required = false) String crfVersionName,
			@RequestParam(value = "studySubjectLabel", required = false) String studySubjectLabel,
			@RequestParam(value = "studySubjectId", required = false) int studySubjectId,
			@RequestParam(value = "eventCRFId", required = false) int eventCRFId,
			@RequestParam(value = "eventDefinitionCRFId", required = false) int eventDefinitionCRFId,
			@RequestParam(value = "selectedVersionId", required = false) int selectedVersionId,
			@RequestParam(value = "selectedVersionName", required = false) String selectedVersionName,
			@RequestParam(value = "eventName", required = false) String eventName,
			@RequestParam(value = "eventCreateDate", required = false) String eventCreateDate,
			@RequestParam(value = "eventOrdinal", required = false) String eventOrdinal) {

		// add here error handling for post with no data and redirect from OC error page
		// to be removed for aquamarine
		if (!mayProceed(request)) {
			if (redirect(request, response, "/MainMenu?message=authentication_failed") == null) {
				return null;
			}
		}
		resetPanel(request);
		request.setAttribute("eventCRFId", eventCRFId);
		request.setAttribute("studySubjectLabel", studySubjectLabel);
		request.setAttribute("eventDefinitionCRFId", eventDefinitionCRFId);
		request.setAttribute("studySubjectId", studySubjectId);
		request.setAttribute("crfId", crfId);
		request.setAttribute("crfName", crfName);
		request.setAttribute("crfversionId", crfVersionId);
		request.setAttribute("crfVersionName", crfVersionName.trim());
		request.setAttribute("selectedVersionId", selectedVersionId);
		if (selectedVersionName != null) {
			selectedVersionName = selectedVersionName.trim();
		}
		request.setAttribute("selectedVersionName", selectedVersionName);
		request.setAttribute("eventName", eventName);
		request.setAttribute("eventCreateDate", eventCreateDate);
		request.setAttribute("eventOrdinal", eventOrdinal);

		ResourceBundle resword = ResourceBundleProvider.getWordsBundle();

		ModelMap gridMap = new ModelMap();
		ArrayList<String> pageMessages = (ArrayList<String>) request.getAttribute("pageMessages");
		if (pageMessages == null) {
			pageMessages = new ArrayList<String>();
		}
		if (selectedVersionId == -1) {
			String errorMessage = resword.getString("confirm_crf_version_em_select_version"); // "Please select CRF version";
			StringBuilder params = new StringBuilder();
			params.append("/pages/managestudy/chooseCRFVersion?crfId=").append(crfId).append("&crfName=")
					.append(crfName).append("&crfversionId=").append(crfVersionId).append("&crfVersionName=")
					.append(crfVersionName).append("&studySubjectLabel=").append(studySubjectLabel)
					.append("&studySubjectId=").append(studySubjectId).append("&eventCRFId=").append(eventCRFId)
					.append("&eventDefinitionCRFId=").append(eventDefinitionCRFId).append("&errorMessage=")
					.append(errorMessage);

			if (redirect(request, response, params.toString()) == null) {
				return null;
			}
		}

		request.getSession().removeAttribute("pageMessages");
		// get data for current crf version display
		// select name, ordinal, oc_oid, item_data_id, i.item_id, value from item_data id, item i
		// where id.item_id=i.item_id and event_crf_id = 171 order by i.item_id,ordinal;
		ArrayList<String[]> rows = new ArrayList<String[]>();
		int currentCrfVersionCounter = 0;
		int newCrfVersionCounter = 0;

		try {
			ItemDAO itemDAO = new ItemDAO(dataSource);
			// get metadata to find repeat group or not
			ItemGroupMetadataDAO itemGroupMetadataDAO = new ItemGroupMetadataDAO(dataSource);
			List<ItemGroupMetadataBean> itemFormMetaDataBeansForCurrentCrfVersion = itemGroupMetadataDAO
					.findByCrfVersion(crfVersionId);
			logger.debug("Size of beans found by version: " + itemFormMetaDataBeansForCurrentCrfVersion.size());
			HashMap<Integer, ItemGroupMetadataBean> hashItemFormMetaDataForCurrentVersion = new HashMap<Integer, ItemGroupMetadataBean>(
					itemFormMetaDataBeansForCurrentCrfVersion.size());

			for (ItemGroupMetadataBean bn : itemFormMetaDataBeansForCurrentCrfVersion) {
				hashItemFormMetaDataForCurrentVersion.put(bn.getItemId(), bn);
			}
			logger.debug("Current Metadata Size " + hashItemFormMetaDataForCurrentVersion.size());

			List<ItemGroupMetadataBean> itemFormMetaDataBeansForNewCrfVersion = itemGroupMetadataDAO
					.findByCrfVersion(selectedVersionId);
			HashMap<Integer, ItemGroupMetadataBean> hashItemFormMetaDataForNewVersion = new HashMap<Integer, ItemGroupMetadataBean>(
					itemFormMetaDataBeansForNewCrfVersion.size());

			for (ItemGroupMetadataBean bn : itemFormMetaDataBeansForNewCrfVersion) {
				hashItemFormMetaDataForNewVersion.put(bn.getItemId(), bn);
			}
			logger.debug("Future Metadata Size " + hashItemFormMetaDataForNewVersion.size());

			// get items description
			ArrayList<ItemBean> itemsInCurrentCrfVersion = itemDAO.findAllWithItemDataByCRFVersionId(crfVersionId,
					eventCRFId);
			logger.debug("Found number of current items " + itemsInCurrentCrfVersion.size());

			ArrayList<ItemBean> itemsInNewCrfVersion = itemDAO.findAllWithItemDataByCRFVersionId(selectedVersionId,
					eventCRFId);
			logger.debug("Found number of new items " + itemsInNewCrfVersion.size());

			ItemBean itemFromCurrentCrfVersion;
			ItemBean itemFromNewCrfVersion;
			ItemGroupMetadataBean groupMetaDataBeanForItemFromCurrentCrfVersion;
			ItemGroupMetadataBean groupMetaDataBeanForItemFromNewCrfVersion;
			while (true) {
				// break out of the while loop here
				if (currentCrfVersionCounter >= (itemsInCurrentCrfVersion.size() - 1)
						&& newCrfVersionCounter >= (itemsInNewCrfVersion.size() - 1)) {
					break;
				}
				itemFromCurrentCrfVersion = itemsInCurrentCrfVersion.get(currentCrfVersionCounter);
				groupMetaDataBeanForItemFromCurrentCrfVersion = hashItemFormMetaDataForCurrentVersion.get(new Integer(
						itemFromCurrentCrfVersion.getId()));
				itemFromNewCrfVersion = itemsInNewCrfVersion.get(newCrfVersionCounter);
				groupMetaDataBeanForItemFromNewCrfVersion = hashItemFormMetaDataForNewVersion.get(new Integer(
						itemFromNewCrfVersion.getId()));

				if (itemFromNewCrfVersion.getId() == itemFromCurrentCrfVersion.getId()) {
					buildRecord(itemFromCurrentCrfVersion, itemFromNewCrfVersion,
							groupMetaDataBeanForItemFromCurrentCrfVersion, groupMetaDataBeanForItemFromNewCrfVersion,
							rows);
				} else if (itemFromNewCrfVersion.getId() < itemFromCurrentCrfVersion.getId()) {
					buildRecord(null, itemFromNewCrfVersion, null, groupMetaDataBeanForItemFromNewCrfVersion, rows);
				} else if (itemFromNewCrfVersion.getId() > itemFromCurrentCrfVersion.getId()) {
					buildRecord(itemFromCurrentCrfVersion, null, groupMetaDataBeanForItemFromCurrentCrfVersion, null,
							rows);
				}

				if (currentCrfVersionCounter >= (itemsInCurrentCrfVersion.size() - 1)
						&& newCrfVersionCounter < (itemsInNewCrfVersion.size() - 1)) {
					while (newCrfVersionCounter < itemsInNewCrfVersion.size() - 1) {
						newCrfVersionCounter++;
						itemFromNewCrfVersion = itemsInNewCrfVersion.get(newCrfVersionCounter);
						groupMetaDataBeanForItemFromNewCrfVersion = hashItemFormMetaDataForNewVersion.get(new Integer(
								itemFromNewCrfVersion.getId()));
						buildRecord(null, itemFromNewCrfVersion, null, groupMetaDataBeanForItemFromNewCrfVersion, rows);
					}
					break;
				}
				if (currentCrfVersionCounter < (itemsInCurrentCrfVersion.size() - 1)
						&& newCrfVersionCounter >= (itemsInNewCrfVersion.size() - 1)) {
					while (currentCrfVersionCounter < itemsInCurrentCrfVersion.size() - 1) {
						currentCrfVersionCounter++;
						itemFromCurrentCrfVersion = itemsInCurrentCrfVersion.get(currentCrfVersionCounter);
						groupMetaDataBeanForItemFromCurrentCrfVersion = hashItemFormMetaDataForCurrentVersion
								.get(new Integer(itemFromCurrentCrfVersion.getId()));
						buildRecord(itemFromCurrentCrfVersion, null, groupMetaDataBeanForItemFromCurrentCrfVersion,
								null, rows);
					}
					break;
				}
				if (itemFromNewCrfVersion.getId() == itemFromCurrentCrfVersion.getId()) {
					currentCrfVersionCounter++;
					newCrfVersionCounter++;
				} else if (itemFromNewCrfVersion.getId() < itemFromCurrentCrfVersion.getId()) {
					newCrfVersionCounter++;
				} else if (itemFromNewCrfVersion.getId() > itemFromCurrentCrfVersion.getId()) {
					currentCrfVersionCounter++;
				}
			}

		} catch (Exception e) {
			logger.error(currentCrfVersionCounter + " " + newCrfVersionCounter);
			pageMessages.add(resword.getString("confirm_crf_version_em_dataextraction"));
		}
		request.setAttribute("pageMessages", pageMessages);
		gridMap.addAttribute("rows", rows);

		return gridMap;
	}

	private void buildRecord(ItemBean itemFromCurrentCrfVersion, ItemBean itemFromNewCrfVersion,
			ItemGroupMetadataBean groupMetaDataBeanForItemFromCurrentCrfVersion,
			ItemGroupMetadataBean groupMetaDataBeanForItemFromNewCrfVersion, ArrayList<String[]> rows) {

		String[] row;
		int cycleCount = 0;

		if (itemFromCurrentCrfVersion == null && itemFromNewCrfVersion != null) {
			for (ItemDataBean itemData : itemFromNewCrfVersion.getItemDataElements()) {
				row = new String[EIGHT];
				row[ZERO] = "";
				row[ONE] = "";
				row[TWO] = "";
				row[THREE] = "";
				row[FOUR] = (groupMetaDataBeanForItemFromNewCrfVersion.isRepeatingGroup()) ? itemFromNewCrfVersion
						.getName() + "(1)" : itemFromNewCrfVersion.getName();
				row[FIVE] = itemFromNewCrfVersion.getOid();
				row[SIX] = String.valueOf(itemFromNewCrfVersion.getId());
				row[SEVEN] = itemData.getValue();
				rows.add(row);
				cycleCount++;
				if (cycleCount > 0 && !groupMetaDataBeanForItemFromNewCrfVersion.isRepeatingGroup()) {
					break;
				}
			}
		} else if (itemFromCurrentCrfVersion != null && itemFromNewCrfVersion == null) {

			for (ItemDataBean itemData : itemFromCurrentCrfVersion.getItemDataElements()) {
				row = new String[EIGHT];
				row[ZERO] = (groupMetaDataBeanForItemFromCurrentCrfVersion.isRepeatingGroup())
						? itemFromCurrentCrfVersion.getName() + " (" + itemData.getOrdinal() + ")"
						: itemFromCurrentCrfVersion.getName();
				row[ONE] = itemFromCurrentCrfVersion.getOid();
				row[TWO] = String.valueOf(itemFromCurrentCrfVersion.getId());
				row[THREE] = itemData.getValue();
				row[FOUR] = "";
				row[SIX] = "";
				row[SEVEN] = "";
				row[FIVE] = "";
				rows.add(row);
				cycleCount++;
				if (cycleCount > 0 && !groupMetaDataBeanForItemFromCurrentCrfVersion.isRepeatingGroup()) {
					break;
				}
			}
		} else if (itemFromCurrentCrfVersion != null) {
			// for repeating groups: 3 cases
			// one cycle: repeating group item -> none-repeating group item
			// second cycle -> back none-repeating to prev repeating
			for (ItemDataBean itemData : itemFromCurrentCrfVersion.getItemDataElements()) {
				row = new String[EIGHT];
				if (!groupMetaDataBeanForItemFromCurrentCrfVersion.isRepeatingGroup() && cycleCount > 0) {
					row[ZERO] = "";
					row[ONE] = "";
					row[TWO] = "";
					row[THREE] = "";
				} else {
					row[ZERO] = (groupMetaDataBeanForItemFromCurrentCrfVersion.isRepeatingGroup())
							? itemFromCurrentCrfVersion.getName() + " (" + itemData.getOrdinal() + ")"
							: itemFromCurrentCrfVersion.getName();
					row[ONE] = itemFromCurrentCrfVersion.getOid();
					row[TWO] = String.valueOf(itemFromCurrentCrfVersion.getId());
					row[THREE] = itemData.getValue();
				}
				if (groupMetaDataBeanForItemFromNewCrfVersion.isRepeatingGroup()) {
					// case when new one is a repeating group and has data from some previous entry while current does
					// not have a repeating group
					if (!groupMetaDataBeanForItemFromCurrentCrfVersion.isRepeatingGroup()) {
						row[FOUR] = itemFromCurrentCrfVersion.getName() + " (" + itemData.getOrdinal() + ")";
					}

					// new one is repeating & cur is repeating
					if (groupMetaDataBeanForItemFromCurrentCrfVersion.isRepeatingGroup()) {
						row[FOUR] = row[ZERO];
					}
					row[FIVE] = itemFromNewCrfVersion.getOid();
					row[SIX] = String.valueOf(itemFromNewCrfVersion.getId());
					row[SEVEN] = itemData.getValue();
				} else {
					if (cycleCount == 0) {

						row[FOUR] = row[ZERO];
						row[FIVE] = itemFromNewCrfVersion.getOid();
						row[SIX] = String.valueOf(itemFromNewCrfVersion.getId());
						row[SEVEN] = itemData.getValue();
					} else {
						row[FOUR] = "";
						row[FIVE] = "";
						row[SIX] = "";
						row[SEVEN] = "";
					}
				}
				// do not add row if all items empty -> from data of repeat group to none-rep
				if (!(row[ZERO].equals("") && row[FOUR].equals(""))) {
					rows.add(row);
				}
				cycleCount++;
			}
		}
	}

	/**
	 * Change CRF Version action request handler.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param eventCRFId
	 *            int
	 * @param newCRFVersionId
	 *            int
	 * @return ModelMap
	 */
	@RequestMapping("/managestudy/changeCRFVersion")
	public ModelMap changeCRFVersionAction(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("eventCRFId") int eventCRFId,
			@RequestParam(value = "newCRFVersionId", required = true) int newCRFVersionId) {

		// to be removed for aquamarine
		if (!mayProceed(request)) {
			if (redirect(request, response, "/MainMenu?message=authentication_failed") == null) {
				return null;
			}
		}

		ResourceBundle resword = ResourceBundleProvider.getWordsBundle();

		ArrayList<String> pageMessages = (ArrayList<String>) request.getAttribute("pageMessages");
		if (pageMessages == null) {
			pageMessages = new ArrayList<String>();
		}
		request.setAttribute("pageMessages", pageMessages);
		// update event_crf_id table
		try {
			EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
			StudyEventDAO sedao = new StudyEventDAO(dataSource);

			EventCRFBean evBean = (EventCRFBean) eventCRFDAO.findByPK(eventCRFId);
			StudyEventBean studyEventBean = (StudyEventBean) sedao.findByPK(evBean.getStudyEventId());

			Connection con = dataSource.getConnection();
			con.setAutoCommit(false);
			eventCRFDAO.updateCRFVersionID(eventCRFId, newCRFVersionId, getCurrentUser(request).getId(), con);

			String statusBeforeUpdate;
			SubjectEventStatus eventStatus;
			Status subjectStatus;
			AuditDAO auditDao = new AuditDAO(dataSource);

			// event signed, check if subject is signed as well
			StudySubjectDAO studySubDao = new StudySubjectDAO(dataSource);
			StudySubjectBean studySubBean = (StudySubjectBean) studySubDao.findByPK(studyEventBean.getStudySubjectId());
			if (studySubBean.getStatus().isSigned()) {
				statusBeforeUpdate = auditDao.findLastStatus("study_subject", studySubBean.getId(), "8");
				if (statusBeforeUpdate != null && statusBeforeUpdate.length() == 1) {
					int subjectStatusId = Integer.parseInt(statusBeforeUpdate);
					subjectStatus = Status.get(subjectStatusId);
					studySubBean.setStatus(subjectStatus);
				}
				studySubBean.setUpdater(getCurrentUser(request));
				studySubDao.update(studySubBean, con);
			}
			studyEventBean.setUpdater(getCurrentUser(request));
			studyEventBean.setUpdatedDate(new Date());

			statusBeforeUpdate = auditDao.findLastStatus("study_event", studyEventBean.getId(), "8");
			if (statusBeforeUpdate != null && statusBeforeUpdate.length() == 1) {
				int status = Integer.parseInt(statusBeforeUpdate);
				eventStatus = SubjectEventStatus.get(status);
				studyEventBean.setSubjectEventStatus(eventStatus);
			}
			sedao.update(studyEventBean, con);

			con.commit();
			con.setAutoCommit(true);
			con.close();
			pageMessages.add(resword.getString("confirm_crf_version_ms"));
			String msg = resword.getString("confirm_crf_version_ms");
			addPageMessage(msg, request, logger);
			storePageMessages(request);
			StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(dataSource);
			StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean) seddao.findByPK(studyEventBean
					.getStudyEventDefinitionId());
			SubjectEventStatusUtil.determineSubjectEventStates(sedBean, studySubBean, getCurrentUser(request),
					new DAOWrapper(dataSource));
			response.sendRedirect(Navigation.getSavedUrl(request));
		} catch (Exception e) {

			pageMessages.add(resword.getString("error_message_cannot_update_crf_version"));

		}
		return null;
	}

	/**
	 * ExceptionHandler for exceptions of class <code>HttpSessionRequiredException</code>.
	 * 
	 * @return String
	 */
	@ExceptionHandler(HttpSessionRequiredException.class)
	public String handleSessionRequiredException() {
		return "redirect:/MainMenu";
	}

	/**
	 * ExceptionHandler for exceptions of class <code>NullPointerException</code>.
	 *
	 * @param ex
	 *            NullPointerException
	 * @param request
	 *            HttpServletRequest
	 * @return String
	 */
	@ExceptionHandler(NullPointerException.class)
	public String handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
		StudyBean currentStudy = (StudyBean) request.getSession().getAttribute("study");
		if (currentStudy == null) {
			return "redirect:/MainMenu";
		}
		throw ex;
	}

	// to be depricated in aquamarine
	private boolean mayProceed(HttpServletRequest request) {

		StudyUserRoleBean currentRole = (StudyUserRoleBean) request.getSession().getAttribute("userRole");
		Role r = currentRole.getRole();

		return r.equals(Role.SYSTEM_ADMINISTRATOR) || r.equals(Role.STUDY_DIRECTOR)
				|| r.equals(Role.STUDY_ADMINISTRATOR);
	}

	private UserAccountBean getCurrentUser(HttpServletRequest request) {
		return (UserAccountBean) request.getSession().getAttribute("userBean");
	}

	private Object redirect(HttpServletRequest request, HttpServletResponse response, String location) {
		try {
			response.sendRedirect(request.getContextPath() + location);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void resetPanel(HttpServletRequest request) {
		StudyInfoPanel panel = new StudyInfoPanel();
		panel.reset();
		panel.setIconInfoShown(false);
		request.getSession().setAttribute("panel", panel);
	}

	private String formatDate(Date date) {
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
		String dateFormat = resformat.getString("date_format_string");
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, LocaleResolver.getLocale());
		return formatter.format(date);
	}

}
