/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.controller;

import com.clinovo.coding.Search;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.model.ClassificationElement;
import com.clinovo.coding.source.impl.BioPortalSearchInterface;
import com.clinovo.dao.MedicalProductDAO;
import com.clinovo.dao.SystemDAO;
import com.clinovo.model.CodedItem;
import com.clinovo.model.CodedItemElement;
import com.clinovo.model.CodedItemsTableFactory;
import com.clinovo.model.Dictionary;
import com.clinovo.model.Ingredient;
import com.clinovo.model.MedicalProduct;
import com.clinovo.model.Status.CodeStatus;
import com.clinovo.model.Term;
import com.clinovo.model.TermElement;
import com.clinovo.model.Therapgroup;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.DictionaryService;
import com.clinovo.service.TermService;
import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The controller for managing coded items. Acts as the glue between the service layer and the UI.
 */
@Controller
public class CodedItemsController {

	private StudyDAO studyDAO;
	@Autowired
	private SystemDAO systemDAO;

	@Autowired
	private DataSource datasource;

	@Autowired
	private TermService termService;

	@Autowired
	private DictionaryService dictionaryService;

	@Autowired
	private CodedItemService codedItemService;

	private Search search = new Search();
	private Logger logger = LoggerFactory.getLogger(getClass().getName());

	private ItemDataDAO itemDataDAO;
	private StudyParameterValueDAO studyParamDAO;

	private static final String BIOONTOLOGY_URL = "http://bioportal.bioontology.org";
	private static final String BIOONTOLOGY_WS_URL = "http://data.bioontology.org";

	/**
	 * Handle for retrieving all the coded items.
	 * 
	 * @param request
	 *            The incoming request
	 * @param response
	 *            The response to redirect to
	 * 
	 * @return Map The map with coded item attributes that will be placed on the UX.
	 * 
	 * @throws Exception
	 *             For all exceptions
	 */
	@RequestMapping("/codedItems")
	public ModelMap codedItemsHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String httpPath = (String) request.getSession().getAttribute("codedItemUrl");
		String queryString = request.getQueryString();

		if (queryString == null && httpPath != null) {

			response.sendRedirect("codedItems?" + httpPath);
		} else {

			request.getSession().setAttribute("codedItemUrl", queryString);
		}

		ModelMap model = new ModelMap();
		ResourceBundleProvider.updateLocale(SessionUtil.getLocale(request));

		String showMoreLink = request.getParameter("showMoreLink");
		String showContext = request.getParameter("showContext");
		String themeColor = (String) request.getSession().getAttribute("newThemeColor");
		showMoreLink = showMoreLink == null ? "false" : showMoreLink;
		showContext = showContext == null ? "false" : showContext;
		themeColor = themeColor == null ? "blue" : themeColor;

		StudyBean study = (StudyBean) request.getSession().getAttribute("study");
		StudyParameterValueBean mcApprovalNeeded = getStudyParameterValueDAO().findByHandleAndStudy(study.getId(),
				"medicalCodingApprovalNeeded");
		StudyParameterValueBean medicalCodingContextNeeded = getStudyParameterValueDAO().findByHandleAndStudy(
				study.getId(), "medicalCodingContextNeeded");
		StudyParameterValueBean configuredDictionary = getStudyParameterValueDAO().findByHandleAndStudy(study.getId(),
				"autoCodeDictionaryName");
		com.clinovo.model.System bioontologyUrl = systemDAO.findByName("defaultBioontologyURL");

		boolean configuredDictionaryIsAvailable = configuredDictionary.getValue() != null
				&& !configuredDictionary.getValue().isEmpty() ? true : false;

		List<CodedItem> items = new ArrayList<CodedItem>();

		// Scope the items
		if (study.isSite(study.getParentStudyId())) {
			items = codedItemService.findByStudyAndSite(study.getParentStudyId(), study.getId());
		} else {
			items = codedItemService.findByStudy(study.getId());
		}

		List<CodedItem> codedItems = getItems(items, CodeStatus.CODED);
		List<CodedItem> unCodedItems = getItems(items, CodeStatus.NOT_CODED);
		List<CodedItem> codeNotFoundItems = getItems(items, CodeStatus.CODE_NOT_FOUND);

		CodedItemsTableFactory factory = new CodedItemsTableFactory(medicalCodingContextNeeded.getValue(),
				showMoreLink, showContext);

		factory.setStudyId(study.getId());
		factory.setCodedItems(items);
		factory.setDataSource(datasource);
		factory.setThemeColor(themeColor);
		factory.setStudyDAO(getStudyDAO());
		factory.setTerms(termService.findAll());
		factory.setCrfDAO(new CRFDAO(datasource));
		factory.setEventCRFDAO(new EventCRFDAO(datasource));
		factory.setCrfVersionDAO(new CRFVersionDAO(datasource));
		factory.setItemDataDAO(new ItemDataDAO(datasource));
		factory.setStudyEventDAO(new StudyEventDAO(datasource));
		factory.setStudySubjectDAO(new StudySubjectDAO(datasource));
		factory.setEventDefinitionCRFDAO(new EventDefinitionCRFDAO(datasource));
		factory.setStudyEventDefinitionDAO(new StudyEventDefinitionDAO(datasource));
		factory.setBioontologyUrl(bioontologyUrl.getValue());

		String codedItemsTable = factory.createTable(request, response).render();

		StudyInfoPanel panel = new StudyInfoPanel();
		panel.reset();

		model.addAttribute("panel", panel);
		model.addAttribute("allItems", items);
		model.addAttribute("codedItems", codedItems.size());
		model.addAttribute("codedItemsTable", codedItemsTable);
		model.addAttribute("unCodedItems", unCodedItems.size());
		model.addAttribute("codeNotFoundItems", codeNotFoundItems.size());
		model.addAttribute("mcApprovalNeeded", mcApprovalNeeded.getValue().equals("yes"));
		model.addAttribute("configuredDictionaryIsAvailable", configuredDictionaryIsAvailable);

		// After auto coding attempt
		model.addAttribute("skippedItems", request.getAttribute("skippedItems"));
		model.addAttribute("autoCodedItems", request.getAttribute("autoCodedItems"));

		return model;
	}

	/**
	 * Handle for coding a specified item.
	 *
	 * @param request The request containing the item to code.
	 * @param response The response to redirect to.
	 * @return Map with attributes to be used on the UX.
	 * @throws Exception For all exceptions.
	 */
	@RequestMapping("/codeItem")
	public ModelMap codeItemHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Term term = null;
		ModelMap model = new ModelMap();
		ResourceBundleProvider.updateLocale(SessionUtil.getLocale(request));

		String itemId = request.getParameter("item");
		String dictionary = request.getParameter("dictionary");
		String prefLabel = request.getParameter("prefLabel");

		List<Classification> classifications = new ArrayList<Classification>();
		CodedItem codedItem = codedItemService.findCodedItem(Integer.parseInt(itemId));
		StudyParameterValueBean configuredDictionary = getStudyParameterValueDAO().findByHandleAndStudy(
				codedItem.getStudyId(), "autoCodeDictionaryName");

		boolean configuredDictionaryIsAvailable = configuredDictionary.getValue() != null
				&& !configuredDictionary.getValue().isEmpty() ? true : false;

		com.clinovo.model.System bioontologyUrl = systemDAO.findByName("defaultBioontologyURL");
		com.clinovo.model.System bioontologyApiKey = systemDAO.findByName("medicalCodingApiKey");

		if (configuredDictionaryIsAvailable) {
			ItemDataDAO itemDataDAO = new ItemDataDAO(datasource);
			ItemDataBean data = (ItemDataBean) itemDataDAO.findByPK(codedItem.getItemId());
			String dataValue = "";
			if (codedItem.getCodedItemElementByItemName("GR").getItemDataId() > 0) {
				dataValue = data.getValue() + " (Grade " + codedItem.getCodedItemElementByItemName("GR").getItemCode() + ")";
			} else {
				dataValue = data.getValue();
			}

			term = termService.findByAliasAndExternalDictionary(dataValue.toLowerCase(),
					codedItem.getDictionary());
		}

		if (term != null) {
			Classification classification = new Classification();
			classification.setHttpPath(term.getHttpPath());
			classification.setClassificationElement(generateClassificationElements(term.getTermElementList()));

			generateCodedItemFields(codedItem.getCodedItemElements(), classification.getClassificationElement());

			codedItem.setAutoCoded(true);
			codedItem.setPreferredTerm(term.getPreferredName());
			codedItem.setHttpPath(term.getHttpPath());
			codedItem.setStatus((String.valueOf(CodeStatus.CODED)));

			codedItemService.saveCodedItem(codedItem);
			classifications.add(classification);
			model.addAttribute("autoCoded", true);

		} else {
			if (!codedItem.isCoded()) {
				search.setSearchInterface(new BioPortalSearchInterface());
				try {
					if (dictionary.equalsIgnoreCase("WHOD")) {
						final int minCodeLength = 5;
						final int firstCodeLength = 3;
						if (prefLabel.matches("\\d+") && prefLabel.length() > minCodeLength) {
							String drugRecordNum = prefLabel.substring(0, prefLabel.length() - minCodeLength);
							String seq1 = prefLabel.substring(prefLabel.length() - minCodeLength, prefLabel.length() - firstCodeLength);
							String seq2 = prefLabel.substring(prefLabel.length() - firstCodeLength, prefLabel.length());
							classifications = medicalProductListToClassificationList((getMedicalProductDAO().findByMedicalProductUniqueKeys(drugRecordNum, seq1, seq2)), SessionUtil.getLocale(request));
						} else {
							classifications = medicalProductListToClassificationList(getMedicalProductDAO().findByMedicalProductName(prefLabel), SessionUtil.getLocale(request));
						}
					} else {
						classifications = search.getClassifications(prefLabel.toLowerCase(), dictionary, bioontologyUrl.getValue(),
								bioontologyApiKey.getValue());
					}
					if (classifications.size() == 0) {
						codedItem.setStatus(String.valueOf(CodeStatus.CODE_NOT_FOUND));
						codedItemService.saveCodedItem(codedItem);
						model.addAttribute("notCoded", true);
					}
				} catch (Exception ex) {
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY);
					return model;
				}
			}
		}

		model.addAttribute("bioontologyUrl", normalizeUrl(bioontologyUrl.getValue(), codedItem.getDictionary()));
		model.addAttribute("itemDictionary", dictionary);
		model.addAttribute("itemDataId", codedItem.getItemId());
		model.addAttribute("codedElementList", classifications);
		model.addAttribute("configuredDictionaryIsAvailable", configuredDictionaryIsAvailable);

		return model;
	}

	private String normalizeUrl(String bioontologyUrl, String dictionary) throws MalformedURLException {

		if (bioontologyUrl.equals(BIOONTOLOGY_WS_URL) || "MEDDRA".equals(dictionary)) {
			return BIOONTOLOGY_URL;
		} else {
			URL url = new URL(bioontologyUrl);
			return url.getProtocol() + "://" + url.getHost();
		}
	}

	/**
	 * Handle for auto-coding using terms from database.
	 *
	 * @param request The request containing the item to code.
	 * @param response The response to redirect to.
	 * @throws Exception The exception for all exceptions.
	 */
	@RequestMapping("/autoCode")
	public void autoCodeItemsHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<CodedItem> items = new ArrayList<CodedItem>();
		ItemDataDAO itemDataDAO = new ItemDataDAO(datasource);
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(datasource);
		StudyEventDAO studyEventDAO = new StudyEventDAO(datasource);
		List<CodedItem> skippedItems = new ArrayList<CodedItem>();

		List<CodedItem> uncodedItems = codedItemService.findCodedItemsByStatus(CodeStatus.NOT_CODED);

		for (CodedItem item : uncodedItems) {

			StudyEventBean studyEventBean = (StudyEventBean) studyEventDAO.findByPK(item.getEventCrfId());
			CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDAO.findByPK(item.getCrfVersionId());

			boolean crfIsLocked = studyEventBean.getSubjectEventStatus().isLocked()
					|| studyEventBean.getSubjectEventStatus().isStopped()
					|| studyEventBean.getSubjectEventStatus().isSkipped()
					|| !crfVersionBean.getStatus().equals(Status.AVAILABLE);

			if (!crfIsLocked) {
				ItemDataBean data = (ItemDataBean) itemDataDAO.findByPK(item.getItemId());
				String dataValue = "";
				if (item.getCodedItemElementByItemName("GR").getItemDataId() > 0) {
					dataValue = data.getValue() + " (Grade " + item.getCodedItemElementByItemName("GR").getItemCode() + ")";
				} else {
					dataValue = data.getValue();
				}

				Term term = termService.findByAliasAndExternalDictionary(dataValue.toLowerCase(), item.getDictionary());
				if (term != null) {
					Classification classification = new Classification();
					classification.setHttpPath(term.getHttpPath());
					classification.setClassificationElement(generateClassificationElements(term.getTermElementList()));
					generateCodedItemFields(item.getCodedItemElements(), classification.getClassificationElement());
					item.setPreferredTerm(term.getPreferredName());
					item.setHttpPath(term.getHttpPath());
					item.setAutoCoded(true);
					item.setStatus((String.valueOf(CodeStatus.CODED)));

					codedItemService.saveCodedItem(item);
					items.add(item);
				} else {
					skippedItems.add(item);
				}
			}
		}

		request.setAttribute("autoCodedItems", items);
		request.setAttribute("skippedItems", skippedItems);

		codedItemsHandler(request, response);
	}

	/**
	 * Handle for saving a coded item.
	 *
	 * @param request The request containing the coded item to save
	 * @param response The response to redirect to.
	 * @return Redirects to the coded items handler.
	 * @throws IOException For all exception
	 */

	@RequestMapping("/saveCodedItem")
	public String saveCodedItemHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {

		ResourceBundleProvider.updateLocale(SessionUtil.getLocale(request));
		String itemId = request.getParameter("item");
		String categoryList = request.getParameter("categoryList");
		String verbatimTerm = request.getParameter("verbatimTerm");
		String codeSearchTerm = request.getParameter("coderSearchTerm");

		CodedItem codedItem = codedItemService.findCodedItem(Integer.valueOf(itemId));
		com.clinovo.model.System bioontologyUrl = systemDAO.findByName("defaultBioontologyURL");
		com.clinovo.model.System bioontologyApiKey = systemDAO.findByName("medicalCodingApiKey");

		try {
			provideCoding(verbatimTerm, false, categoryList, codeSearchTerm, bioontologyUrl.getValue(),
					bioontologyApiKey.getValue(), codedItem.getItemId(), SessionUtil.getLocale(request));
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY);
		}
		return "codedItems";
	}

	/**
	 * Handle for uncoding a given coded item.
	 *
	 * @param request The request containing the item to uncode.
	 * @return Redirects to coded items.
	 * @throws Exception For all exceptions
	 */
	@RequestMapping("/uncodeCodedItem")
	public String unCodeCodedItemHandler(HttpServletRequest request) throws Exception {

		ResourceBundleProvider.updateLocale(SessionUtil.getLocale(request));

		String codedItemItemDataId = request.getParameter("item");

		CodedItem codedItem = codedItemService.findCodedItem(Integer.parseInt(codedItemItemDataId));
		ItemDataBean itemData = (ItemDataBean) getItemDataDAO().findByPK(codedItem.getItemId());
		boolean isGradeTerm = codedItem.getCodedItemElementByItemName("GR").getItemDataId() > 0 ? true : false;
		for (CodedItemElement codedItemElement : codedItem.getCodedItemElements()) {
			if (isGradeTerm) {
				if (!codedItemElement.getItemName().equals("GR")) {
					codedItemElement.setItemCode("");
				}
			} else {
				codedItemElement.setItemCode("");
			}
		}

		if (codedItem.isAutoCoded()) {
			codedItem.setAutoCoded(false);
		}

		codedItem.setHttpPath("");
		codedItem.setStatus(String.valueOf(CodeStatus.NOT_CODED));
		codedItem.setPreferredTerm(itemData.getValue());

		codedItemService.saveCodedItem(codedItem);

		return "codedItems";
	}

	/**
	 * Handle for getting specified item additional fields.
	 *
	 * @param request The request containing the term url to getting additional fields.
	 * @return Map with attributes to be used on the UX-
	 * @throws Exception For all exceptions
	 */
	@RequestMapping("/codeItemFields")
	public ModelMap termAdditionalFieldsHandler(HttpServletRequest request) throws Exception {

		ResourceBundleProvider.updateLocale(SessionUtil.getLocale(request));

		String codedItemUrl = request.getParameter("codedItemUrl");
		String term = request.getParameter("term");

		search.setSearchInterface(new BioPortalSearchInterface());

		com.clinovo.model.System bioontologyUrl = systemDAO.findByName("defaultBioontologyURL");
		com.clinovo.model.System bioontologyApiKey = systemDAO.findByName("medicalCodingApiKey");

		Classification classificationWithTerms = search.getClassificationWithTerms(codedItemUrl,
				bioontologyUrl.getValue(), bioontologyApiKey.getValue());

		if (codedItemUrl.indexOf("MEDDRA") > 0 || codedItemUrl.indexOf("MDR") > 0) {
			ClassificationElement ptElement = new ClassificationElement();
			ptElement.setCodeName(term);
			ptElement.setElementName(ResourceBundleProvider.getResWord("pt"));
			classificationWithTerms.addClassificationElement(ptElement);
		}

		ModelMap model = new ModelMap();
		model.addAttribute("codedElement", classificationWithTerms);
		return model;
	}

	/**
	 * Handle for coding and aliasing a given coded item.
	 *
	 * @param request The request containing the item to code and alias.
	 * @param response The response to redirect to.
	 * @return Redirects to coded items.
	 * @throws Exception For all exceptions
	 */
	@RequestMapping("/codeAndAlias")
	public String codeAndAliasHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ResourceBundleProvider.updateLocale(SessionUtil.getLocale(request));

		boolean isAlias = false;
		String item = request.getParameter("item");
		String categoryList = request.getParameter("categoryList");
		String verbatimTerm = request.getParameter("verbatimTerm");
		String codeSearchTerm = request.getParameter("coderSearchTerm");

		StudyBean study = (StudyBean) request.getSession().getAttribute("study");

		StudyParameterValueBean configuredDictionary = getStudyParameterValueDAO().findByHandleAndStudy(study.getId(),
				"autoCodeDictionaryName");
		com.clinovo.model.System bioontologyUrl = systemDAO.findByName("defaultBioontologyURL");
		com.clinovo.model.System bioontologyApiKey = systemDAO.findByName("medicalCodingApiKey");

		CodedItem codedItem = codedItemService.findCodedItem(Integer.parseInt(item));

		if (configuredDictionary.getValue() != null && !configuredDictionary.getValue().isEmpty()) {

			isAlias = true;
		}

		if (codedItem != null) {
			try {

				provideCoding(verbatimTerm, isAlias, categoryList, codeSearchTerm, bioontologyUrl.getValue(),
						bioontologyApiKey.getValue(), codedItem.getItemId(), SessionUtil.getLocale(request));
			} catch (Exception ex) {

				logger.error(ex.getMessage());
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY);
			}
		}

		return "codedItems";
	}

	private List<CodedItem> getItems(List<CodedItem> items, CodeStatus status) {

		List<CodedItem> matchingItems = new ArrayList<CodedItem>();

		for (CodedItem item : items) {

			if (item.getStatus().equals(String.valueOf(status))) {
				matchingItems.add(item);
			}
		}

		return matchingItems;
	}

	private StudyDAO getStudyDAO() {

		if (studyDAO == null) {
			studyDAO = new StudyDAO(datasource);
		}

		return studyDAO;
	}

	private StudyParameterValueDAO getStudyParameterValueDAO() {
		if (studyParamDAO == null) {
			studyParamDAO = new StudyParameterValueDAO(datasource);
		}

		return studyParamDAO;
	}

	private ItemDataDAO getItemDataDAO() {

		if (itemDataDAO == null) {
			itemDataDAO = new ItemDataDAO(datasource);
		}

		return itemDataDAO;
	}

	private ArrayList<ClassificationElement> generateClassificationElements(List<TermElement> termElementList) {

		ArrayList<ClassificationElement> classElementList = new ArrayList<ClassificationElement>();

		for (TermElement termElement : termElementList) {

			ClassificationElement classElement = new ClassificationElement();
			classElement.setElementName(termElement.getElementName());
			classElement.setCodeName(termElement.getTermName());
			classElement.setCodeValue(termElement.getTermCode());

			classElementList.add(classElement);

		}

		return classElementList;
	}

	private CodedItem provideCoding(String verbatimTerm, boolean isAlias, String categoryList, String codeSearchTerm,
			String bioontologyUrl, String bioontologyApiKey, int codedItemId, Locale locale) throws Exception {

		StudyParameterValueDAO studyParameterValueDAO = new StudyParameterValueDAO(datasource);

		CodedItem codedItem = codedItemService.findCodedItem(codedItemId);
		codedItem.setPreferredTerm(codeSearchTerm);

		Classification classificationResult = getClassificationFromCategoryString(categoryList);
		if (codedItem.getDictionary().equals("WHOD")) {
			MedicalProduct mpBean = getMedicalProductDAO().findByPk(Integer.valueOf(classificationResult.getHttpPath()));
			classificationResult = medicalProductToClassification(mpBean, locale);
		} else {
			search.setSearchInterface(new BioPortalSearchInterface());
			search.getClassificationWithCodes(classificationResult, codedItem.getDictionary().replace("_", " "),
					bioontologyUrl, bioontologyApiKey);
		}

		// replace all terms & codes from classification to coded elements
		generateCodedItemFields(codedItem.getCodedItemElements(), classificationResult.getClassificationElement());

		// if isAlias is true, create term using completed classification
		if (isAlias) {

			StudyParameterValueBean configuredDictionary = studyParameterValueDAO.findByHandleAndStudy(
					codedItem.getStudyId(), "autoCodeDictionaryName");
			Dictionary dictionary = dictionaryService.findDictionary(configuredDictionary.getValue());

			Term term = new Term();

			term.setDictionary(dictionary);
			term.setLocalAlias(verbatimTerm.toLowerCase());
			term.setPreferredName(codeSearchTerm.toLowerCase());
			term.setHttpPath(classificationResult.getHttpPath());
			term.setExternalDictionaryName(codedItem.getDictionary());
			term.setTermElementList(generateTermElementList(classificationResult.getClassificationElement()));

			termService.saveTerm(term);
		}

		codedItem.setStatus((String.valueOf(CodeStatus.CODED)));
		codedItem.setHttpPath(classificationResult.getHttpPath());

		codedItemService.saveCodedItem(codedItem);

		return codedItem;
	}

	private Classification getClassificationFromCategoryString(String categoryList) {

		Classification classification = new Classification();
		List<String> list = new ArrayList<String>(Arrays.asList(categoryList.split("\\|")));

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals("HTTP")) {
				classification.setHttpPath(list.get(i + 1));
				i++;
			} else if (!list.get(i).isEmpty()) {
				ClassificationElement classificationElement = new ClassificationElement();
				classificationElement.setElementName(list.get(i));
				classificationElement.setCodeName(list.get(i + 1));
				classification.addClassificationElement(classificationElement);
				i++;
			}
		}

		return classification;
	}

	private List<TermElement> generateTermElementList(List<ClassificationElement> classificationElementList) {

		List<TermElement> termElementList = new ArrayList<TermElement>();
		for (ClassificationElement classElement : classificationElementList) {
			TermElement newTermElement = new TermElement(classElement.getCodeName(), classElement.getCodeValue(),
					classElement.getElementName());
			termElementList.add(newTermElement);
		}

		return termElementList;
	}

	private void generateCodedItemFields(List<CodedItemElement> codedItemElements,
			List<ClassificationElement> classificationElements) {

		for (CodedItemElement codedItemElement : codedItemElements) {
			for (ClassificationElement classificationElement : classificationElements) {
				String name = codedItemElement.getItemName();
				if (name.equals(classificationElement.getElementName())) {
					codedItemElement.setItemCode(classificationElement.getCodeName().replaceAll(
							classificationElement.getCodeValue(), ""));
				} else if (name.equals(classificationElement.getElementName() + "C")) {
					codedItemElement.setItemCode(classificationElement.getCodeValue());
				}
			}
		}
	}

	private List<Classification> medicalProductListToClassificationList(List<MedicalProduct> mpList, Locale locale) throws ParseException {
		List<Classification> classifications = new ArrayList<Classification>();
		for (MedicalProduct mp : mpList) {
			classifications.add(medicalProductToClassification(mp, locale));
		}
		Collections.sort(classifications, new ClassificationSortByReference());
		return classifications;
	}

	private Classification medicalProductToClassification(MedicalProduct mp, Locale locale) throws ParseException {

		Classification classification = new Classification();
		classification.setHttpPath(String.valueOf(mp.getMedicinalprodId()));
		ClassificationElement medicalProductName = new ClassificationElement();
		medicalProductName.setElementName("MPN");
		medicalProductName.setCodeName(mp.getDrugName());
		classification.addClassificationElement(medicalProductName);
		ClassificationElement medicalProductCode = new ClassificationElement();
		medicalProductCode.setElementName("MPNC");
		medicalProductCode.setCodeName(mp.getDrugRecordNumber() + mp.getSequenceNumber1() + mp.getSequenceNumber2());
		classification.addClassificationElement(medicalProductCode);
		ClassificationElement component = new ClassificationElement();
		component.setElementName("CMP");
		String ingList = String.valueOf(mp.getDrugRecordNumber());
		for (Ingredient ing : mp.getIngList()) {
			ingList = ingList.concat(", ").concat(ing.getSun().getSubstanceName());
		}
		component.setCodeName(ingList);
		classification.addClassificationElement(component);
		int counter = 1;
		for (Therapgroup thg : mp.getThgList()) {
			ClassificationElement atcElement = new ClassificationElement();
			atcElement.setElementName("ATC" + counter);
			atcElement.setCodeName(thg.getAtc().getAtcText());
			atcElement.setCodeValue(thg.getAtc().getAtcCode());
			classification.addClassificationElement(atcElement);
			counter++;
		}
		ClassificationElement country = new ClassificationElement();
		country.setElementName("CNTR");
		country.setCodeName(mp.getSourceCountryBean().getCountryName());
		classification.addClassificationElement(country);

		ClassificationElement dateCreate = new ClassificationElement();
		dateCreate.setElementName("date_created");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", locale);
		Date d = sdf.parse(mp.getCreateDate());
		sdf.applyPattern("dd-MMM-yyyy");
		String formattedDate = sdf.format(d);
		dateCreate.setCodeName(formattedDate);
		classification.addClassificationElement(dateCreate);

		ClassificationElement generic = new ClassificationElement();
		generic.setElementName("GENERIC");
		generic.setCodeName(mp.getGeneric().equals("Y") ? "Yes" : "No");
		classification.addClassificationElement(generic);
		ClassificationElement umbrella = new ClassificationElement();
		umbrella.setElementName("UMBRELLA");
		int firstDigit = mp.getDrugRecordNumber();
		final int number = 9;
		final int divider = 10;
		while (firstDigit > number) {
			firstDigit = firstDigit / divider;
		}
		umbrella.setCodeName(mp.getDrugName().indexOf(" nos") > 0 || firstDigit == number ? "Yes" : "No");
		classification.addClassificationElement(umbrella);
		ClassificationElement preferred = new ClassificationElement();
		preferred.setElementName("PREFERRED");
		preferred.setCodeName(Integer.valueOf(mp.getSequenceNumber1()) > 1 && Integer.valueOf(mp.getSequenceNumber2()) == 1 ? "Yes" : "No");
		classification.addClassificationElement(preferred);

		return classification;
	}

	private class ClassificationSortByReference implements Comparator<Classification> {
		public int compare(Classification o1, Classification o2) {
			for (ClassificationElement classification : o1.getClassificationElement()) {
				for (ClassificationElement classification2 : o2.getClassificationElement()) {
					if (classification.getElementName().equals("PREFERRED") && classification.getElementName().equals(classification2.getElementName())) {
						if ((classification.getCodeName().equals("No") && classification2.getCodeName().equals("Yes"))) {
							return 1;
						} else if (classification.getCodeName().equals("Yes") && classification2.getCodeName().equals("No")) {
							return -1;
						}
					}
				}
			}
			return 0;
		}
	}

	private MedicalProductDAO getMedicalProductDAO() {
		return new MedicalProductDAO();
	}
}
