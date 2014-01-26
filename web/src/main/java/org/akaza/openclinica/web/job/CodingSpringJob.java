package org.akaza.openclinica.web.job;

import com.clinovo.coding.Search;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.model.ClassificationElement;
import com.clinovo.coding.source.impl.BioPortalSearchInterface;
import com.clinovo.model.*;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.DictionaryService;
import com.clinovo.service.TermService;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class CodingSpringJob extends QuartzJobBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private CodedItemService codedItemService;
    private DictionaryService dictionaryService;
    private TermService termService;
    private StudyParameterValueDAO studyParameterValueDAO;
    private DataSource datasource;
    private Search search = new Search();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        JobDetailImpl jobDetail = (JobDetailImpl) context.getJobDetail();
        JobDataMap dataMap = jobDetail.getJobDataMap();

        String verbatimTerm = dataMap.getString(CodingTriggerService.VERBATIM_TERM);
        boolean isAlias = dataMap.getBooleanFromString(CodingTriggerService.IS_ALIAS);
        String preferredName = dataMap.getString(CodingTriggerService.PREFERRED_NAME);
        String codeSearchTerm = dataMap.getString((CodingTriggerService.CODE_SEARCH_TERM));
        String bioontologyUrl = dataMap.getString(CodingTriggerService.BIOONTOLOGY_URL);
        String bioontologyApiKey = dataMap.getString(CodingTriggerService.BIOONTOLOGY_API_KEY);
        int codedItemId = Integer.valueOf(dataMap.getString(CodingTriggerService.CODED_ITEM_ID));

        try {

            ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");

            datasource = (DataSource) appContext.getBean("dataSource");
            termService = (TermService) appContext.getBean("termService");
            studyParameterValueDAO = new StudyParameterValueDAO(datasource);
            dictionaryService = (DictionaryService) appContext.getBean("dictionaryService");
            codedItemService = (CodedItemService) appContext.getBean("codedItemServiceImpl");

            CodedItem codedItem = codedItemService.findCodedItem(codedItemId);

            search.setSearchInterface(new BioPortalSearchInterface());

            List<Classification> classificationResultList = search.getClassifications(preferredName, codedItem.getDictionary().replace("_", " "), bioontologyUrl, bioontologyApiKey);

            if (classificationResultList.size() > 0) {

                Classification classificationResult = getCurrentTermClassification(classificationResultList, preferredName);

                //get codes for all verb terms & save it in classification
                search.getClassificationWithCodes(classificationResult, codedItem.getDictionary().replace("_", " "), bioontologyUrl, bioontologyApiKey);
                //replace all terms & codes from classification to coded elements
                generateCodedItemFields(codedItem.getCodedItemElements(), classificationResult.getClassificationElement(), codedItem.getDictionary());

                //if isAlias is true, create term using completed classification
                if (isAlias) {

                    StudyParameterValueBean configuredDictionary = studyParameterValueDAO.findByHandleAndStudy(codedItem.getStudyId(), "autoCodeDictionaryName");
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

                codedItem.setStatus((String.valueOf(Status.CodeStatus.CODED)));
                codedItem.setHttpPath(classificationResult.getHttpPath());

                codedItemService.saveCodedItem(codedItem);
            }

        } catch (Exception e) {

            logger.error(e.getMessage());

            JobExecutionException qe = new JobExecutionException(e);
            qe.refireImmediately();
            throw qe;
        }
    }



    private Classification getCurrentTermClassification(List<Classification> classificationResultList, String verbatimTerm) {

        for (Classification classification : classificationResultList) {
            for (ClassificationElement classificationElement : classification.getClassificationElement()) {

                if (classificationElement.getCodeName().equals(verbatimTerm)) {

                    return classification;
                }
            }
        }

        return classificationResultList.get(0); //not sure that it is a good idea.
    }

    private List<TermElement> generateTermElementList(List<ClassificationElement> classificationElementList) {

        List<TermElement> termElementList = new ArrayList<TermElement>();

        for(ClassificationElement classElement : classificationElementList) {

            TermElement newTermElement = new TermElement(classElement.getCodeName(), classElement.getCodeValue(), classElement.getElementName());
            termElementList.add(newTermElement);
        }

        return termElementList;
    }

	private void generateCodedItemFields(List<CodedItemElement> codedItemElements,
			List<ClassificationElement> classificationElements, String dictionary) {

		String ptCode = "";
		String ptcCode = "";

		for (CodedItemElement codedItemElement : codedItemElements) {

			for (ClassificationElement classificationElement : classificationElements) {

				// code items with values
				String name = codedItemElement.getItemName();

				if (name.equals(classificationElement.getElementName())) {

					if (name.equalsIgnoreCase("pt")) {
						ptCode = classificationElement.getCodeName();
					}

					codedItemElement.setItemCode(classificationElement.getCodeName());
					break;
					// code items with code
				} else if (name.equals(classificationElement.getElementName() + "C")) {

					if (name.equalsIgnoreCase("ptc")) {
						ptcCode = classificationElement.getCodeValue();
					}

					codedItemElement.setItemCode(classificationElement.getCodeValue());
					break;
				}

			}
		}

        if (dictionary.equals("MEDDRA")) {

            // Copy over the PT to LLT
            CodedItemElement lltElement = getClassificationElement("llt", codedItemElements);
            lltElement.setItemCode(ptCode);

            // Copy over the PTC to LLTC
            CodedItemElement lltcElement = getClassificationElement("lltc", codedItemElements);
            lltcElement.setItemCode(ptcCode);
        }
    }
    
    private CodedItemElement getClassificationElement(String name, List<CodedItemElement> codedItemElements) {
    	
    	CodedItemElement element = null;
    	for (CodedItemElement codedItemElement : codedItemElements) {
    		
    		if (codedItemElement.getItemName().equalsIgnoreCase(name)) {
    			
    			element = codedItemElement;
    			break;
    		}
    	}
    	
		return element;
    }
}
