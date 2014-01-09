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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.clinovo.coding.Search;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.model.ClassificationElement;
import com.clinovo.coding.source.impl.BioPortalSearchInterface;
import com.clinovo.model.*;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.job.OpenClinicaSchedulerFactoryBean;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.job.CodingTriggerService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.clinovo.model.Status.CodeStatus;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.TermService;


/**
 * The controller for managing coded items. Acts as the glue between the service layer and the UI -
 * 
 */
@Controller
@SuppressWarnings("rawtypes")
public class CodedItemsController {

	private StudyDAO studyDAO;
	
	@Autowired
    private DataSource datasource;

    @Autowired
    private OpenClinicaSchedulerFactoryBean scheduler;

	private Search search = new Search();

	@Autowired
	private TermService termService;
	
	@Autowired
	private CodedItemService codedItemService;

	private StudyParameterValueDAO studyParamDAO;

	/**
	 * Handle for retrieving all the coded items.
	 * 
	 * @param request The incoming request
	 * @param response The response to redirect to
	 * 
	 * @return Map The map with coded item attributes that will be placed on the UX.
	 * 
	 * @throws Exception For all exceptions
	 */
	@RequestMapping("/codedItems")
	public ModelMap codedItemsHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelMap model = new ModelMap();
		ResourceBundleProvider.updateLocale(request.getLocale());
		
		String studyId = request.getParameter("study");
        String showMoreLink = request.getParameter("showMoreLink");
        showMoreLink = showMoreLink == null ? "false" : showMoreLink;

		StudyBean study = (StudyBean) getStudyDAO().findByPK(Integer.parseInt(studyId));
        StudyParameterValueBean medicalCodingContextNeeded = getStudyParameterValueDAO().findByHandleAndStudy(study.getId(), "medicalCodingContextNeeded");

		List<CodedItem> items = new ArrayList<CodedItem>();
		
		// Scope the items
		if (study.isSite(study.getParentStudyId())) {
			items = codedItemService.findByStudyAndSite(study.getParentStudyId(), Integer.parseInt(studyId));
		} else {
			items = codedItemService.findByStudy(Integer.parseInt(studyId));
		}
		
		List<CodedItem> codedItems = getItems(items, CodeStatus.CODED);
		List<CodedItem> unCodedItems = getItems(items, CodeStatus.NOT_CODED);
		
		CodedItemsTableFactory factory = new CodedItemsTableFactory(medicalCodingContextNeeded.getValue(), showMoreLink);

		factory.setStudyId(studyId);
		factory.setCodedItems(items);
        factory.setTerms(termService.findAll());
		factory.setDataSource(datasource);
		factory.setStudyDAO(getStudyDAO());
		factory.setEventCRFDAO(new EventCRFDAO(datasource));
		factory.setEventDefinitionCRFDAO(new EventDefinitionCRFDAO(datasource));
		factory.setStudySubjectDAO(new StudySubjectDAO(datasource));
		factory.setStudyEventDefinitionDAO(new StudyEventDefinitionDAO(datasource));
        factory.setItemDataDAO(new ItemDataDAO(datasource));
        factory.setCrfDAO(new CRFDAO(datasource));

		String codedItemsTable = factory.createTable(request, response).render();

		StudyInfoPanel panel = new StudyInfoPanel();
		panel.reset();

		model.addAttribute("panel", panel);
		model.addAttribute("allItems", items);
		model.addAttribute("codedItems", codedItems.size());
		model.addAttribute("codedItemsTable", codedItemsTable);
		model.addAttribute("unCodedItems", unCodedItems.size());
		model.addAttribute("studyId", Integer.valueOf(studyId));

		return model;
	}

	/**
	 * Handle for coding a specified item
	 * 
	 * @param request The request containing the item to code
	 * 
	 * @return Map with attributes to be used on the UX-
	 * 
	 * @throws Exception For all exceptions
	 */
	@RequestMapping("/codeItem")
	public ModelMap codeItemHandler(HttpServletRequest request) throws Exception {

		ModelMap model = new ModelMap();
		ResourceBundleProvider.updateLocale(request.getLocale());

        String dictionary = request.getParameter("dictionary");
        String codedItemItemDataId = request.getParameter("item");
        String verbatimTerm = request.getParameter("verbatimTerm");

        CodedItem codedItem = codedItemService.findCodedItem(Integer.parseInt(codedItemItemDataId));
        StudyParameterValueBean configuredDictionary = getStudyParameterValueDAO().findByHandleAndStudy(codedItem.getStudyId(), "autoCodeDictionaryName");
        Term term = null;
        List<Classification> classifications = new ArrayList<Classification>();

        if (configuredDictionary.getValue() != null && !configuredDictionary.getValue().isEmpty()) {

            // Ignore case - (until Marc changes his mind)
            term = termService.findByNonUniqueTermAndExternalDictionary(verbatimTerm, codedItem.getDictionary());
        }

        if (term != null) {

            Classification classification = new Classification();

            classification.setHttpPath(term.getHttpPath());
            classification.setClassificationElement(generateClassificationElements(term.getTermElementList()));

            generateCodedItemElements(codedItem.getCodedItemElements(), classification.getClassificationElement());

            codedItem.setStatus((String.valueOf(CodeStatus.CODED)));
            codedItem.setAutoCoded(true);
            codedItem.setVerbatimTerm(verbatimTerm);

            codedItemService.saveCodedItem(codedItem);

            classifications.add(classification);

            model.addAttribute("autoCoded", true);

        } else {

            search.setSearchInterface(new BioPortalSearchInterface());
            classifications = search.getClassifications(verbatimTerm, dictionary);
        }

        model.addAttribute("itemDataId", codedItem.getItemId());
        model.addAttribute("itemDictionary", dictionary);
        model.addAttribute("codedElementList", classifications);

        return model;

    }
	
	/**
	 * Handle for saving a coded item
	 * 
	 * @param request The request containing the coded item to save
	 * 
	 * @return Redirects to the coded items handler.
	 * 
	 * @throws Exception For all exception
	 */

    @RequestMapping("/saveCodedItem")
    public String saveCodedItemHandler(HttpServletRequest request) throws Exception {

        ResourceBundleProvider.updateLocale(request.getLocale());

        String codedItemItemDataId = request.getParameter("item");
        String codedItemVerbatimTerm = request.getParameter("code");
        CodedItem codedItem = codedItemService.findCodedItem(Integer.valueOf(codedItemItemDataId));

        codedItem.setStatus((String.valueOf(CodeStatus.IN_PROGRESS)));
        codedItem.setVerbatimTerm(codedItemVerbatimTerm);

        codedItemService.saveCodedItem(codedItem);

        createCodeItemJob(codedItemItemDataId, codedItemVerbatimTerm, false);

        // Redirect to main
        return "codedItems";
    }

	/**
	 * Handle for uncoding a given coded item.
	 * 
	 * @param request The request containing the item to uncode.
	 * 
	 * @return Redirects to coded items.
	 * 
	 * @throws Exception For all exceptions
	 */
    @RequestMapping("/uncodeCodedItem")
    public String unCodeCodedItemHandler(HttpServletRequest request) throws Exception {

        ResourceBundleProvider.updateLocale(request.getLocale());

        String codedItemItemDataId = request.getParameter("item");

        CodedItem codedItem = codedItemService.findCodedItem(Integer.parseInt(codedItemItemDataId));


        for (CodedItemElement codedItemElement : codedItem.getCodedItemElements()) {

            codedItemElement.setItemCode("");
        }

        if(codedItem.isAutoCoded()) {

            codedItem.setAutoCoded(false);
        }

        codedItem.setStatus(String.valueOf(CodeStatus.NOT_CODED));

        codedItemService.saveCodedItem(codedItem);

        return "codedItems";
    }
    
	/**
	 * Handle for coding and aliasing a given coded item.
	 * 
	 * @param request
	 *            The request containing the item to code and alias.
	 * 
	 * @return Redirects to coded items.
	 * 
	 * @throws Exception
	 *             For all exceptions
	 */
	@RequestMapping("/codeAndAlias")
	public String codeAndAliasHandler(HttpServletRequest request) throws Exception {

		ResourceBundleProvider.updateLocale(request.getLocale());

		String code = request.getParameter("code");
		String studyId = request.getParameter("study");
		String codedItemItemDataId = request.getParameter("item");
        boolean isAlias = false;
		
		StudyParameterValueBean configuredDictionary = getStudyParameterValueDAO().findByHandleAndStudy(Integer.parseInt(studyId), "autoCodeDictionaryName");
		
		CodedItem codedItem = codedItemService.findCodedItem(Integer.parseInt(codedItemItemDataId));

        if(configuredDictionary.getValue() != null && !configuredDictionary.getValue().isEmpty()) {

            isAlias = true;
        }

        if(codedItem != null) {

            codedItem.setStatus((String.valueOf(CodeStatus.IN_PROGRESS)));
            codedItem.setVerbatimTerm(code);
            codedItemService.saveCodedItem(codedItem);

            createCodeItemJob(codedItemItemDataId, code, isAlias);
        }

		return "codedItems";
	}
	
	private List<CodedItem> getItems(List<CodedItem> items, CodeStatus status) {
		
		List<CodedItem> matchingItems = new ArrayList<CodedItem>();
		
		for(CodedItem item : items) {
			
			if(item.getStatus().equals(String.valueOf(status))) {
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

    private void createCodeItemJob(String codedItemItemDataId, String codedItemVerbatimTerm, boolean isAlias) throws SchedulerException {

        CodingTriggerService codingTriggerService = new CodingTriggerService();
        SimpleTriggerImpl trigger = codingTriggerService.generateCodeItemService(codedItemItemDataId, codedItemVerbatimTerm, isAlias);
        trigger.setDescription(codedItemItemDataId + " " + codedItemVerbatimTerm);

        JobDetailImpl jobDetailBean = new JobDetailImpl();

        jobDetailBean.setGroup(trigger.getGroup());
        jobDetailBean.setName(trigger.getName());
        jobDetailBean.setJobClass(org.akaza.openclinica.web.job.CodingStatefulJob.class);
        jobDetailBean.setJobDataMap(trigger.getJobDataMap());
        jobDetailBean.setDurability(true);

        getStdScheduler().scheduleJob(jobDetailBean, trigger);
    }

    private ArrayList<ClassificationElement> generateClassificationElements(List<TermElement> termElementList) {

        ArrayList<ClassificationElement> classElementList = new ArrayList<ClassificationElement>();

        for(TermElement termElement : termElementList) {

            ClassificationElement classElement = new ClassificationElement();
            classElement.setElementName(termElement.getElementName());
            classElement.setCodeName(termElement.getTermName());
            classElement.setCodeValue(termElement.getTermCode());

            classElementList.add(classElement);

        }

        return classElementList;
    }

    private void generateCodedItemElements(List<CodedItemElement> codedItemElements, List<ClassificationElement> classificationElements) {
        for (CodedItemElement codedItemElement : codedItemElements) {

            for (ClassificationElement classificationElement : classificationElements) {
                //code items with values
                if (StringUtils.substringAfter(codedItemElement.getItemName(), "_").equals(classificationElement.getElementName())) {

                    codedItemElement.setItemCode(classificationElement.getCodeName());
                    break;
                    //code items with code
                } else if (StringUtils.substringAfter(codedItemElement.getItemName(), "_").equals(classificationElement.getElementName() + "C")) {

                    codedItemElement.setItemCode(classificationElement.getCodeValue());
                    break;
                }
            }
        }
    }

    public StdScheduler getStdScheduler() {
        return (StdScheduler) scheduler.getScheduler();
    }
}
