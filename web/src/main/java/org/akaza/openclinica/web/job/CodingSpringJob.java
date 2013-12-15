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
import org.apache.commons.lang3.StringUtils;
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

    public static final String CODED_ITEM = "codedItem";
    public static final String VERB_TERM = "verbatimTerm";
    public static final String IS_ALIAS = "isAlias";
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

        int codedItemId = Integer.valueOf(dataMap.getString(CODED_ITEM));
        String verbatimTerm = dataMap.getString(VERB_TERM);
        boolean isAlias = dataMap.getBooleanFromString(IS_ALIAS);

        try {

            ApplicationContext appContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");

            codedItemService = (CodedItemService) appContext.getBean("codedItemServiceImpl");
            dictionaryService = (DictionaryService) appContext.getBean("dictionaryService");
            termService = (TermService) appContext.getBean("termService");
            datasource = (DataSource) appContext.getBean("dataSource");

            studyParameterValueDAO = new StudyParameterValueDAO(datasource);

            CodedItem codedItem = codedItemService.findCodedItem(codedItemId);

            search.setSearchInterface(new BioPortalSearchInterface());

            List<Classification> classificationResultList = search.getClassifications(verbatimTerm, codedItem.getDictionary().replace("_", " "));

            if (classificationResultList.size() > 0) {

                Classification classificationResult = getCurrentTermClassification(classificationResultList, verbatimTerm);

                //get codes for all verb terms & save it in classification
                search.getClassificationWithCodes(classificationResult, codedItem.getDictionary().replace("_", " "));
                //replace all terms & codes from classification to coded element
                generateCodedItemFields(codedItem.getCodedItemElements(), classificationResult.getClassificationElement());

                //if isAlias true, create term using completed classification
                if (isAlias) {

                    StudyParameterValueBean configuredDictionary = studyParameterValueDAO.findByHandleAndStudy(codedItem.getStudyId(), "autoCodeDictionaryName");
                    Dictionary dictionary = dictionaryService.findDictionary(configuredDictionary.getValue());

                    Term term = new Term();

                    term.setDictionary(dictionary);
                    term.setPreferredName(verbatimTerm.toLowerCase());
                    term.setExternalDictionaryName(codedItem.getDictionary());
                    term.setTermElementList(generateTermElementList(classificationResult.getClassificationElement()));
                    term.setHttpPath(classificationResult.getHttpPath());

                    termService.saveTerm(term);
                }

                codedItem.setStatus("CODED");
                codedItemService.saveCodedItem(codedItem);
            }

        } catch (Exception e) {

            logger.error(e.getMessage());
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

        return classificationResultList.get(0); //not sure that it is good idea.
    }

    private List<TermElement> generateTermElementList(List<ClassificationElement> classificationElementList) {

        List<TermElement> termElementList = new ArrayList<TermElement>();

        for(ClassificationElement classElement : classificationElementList) {

            TermElement newTermElement = new TermElement(classElement.getCodeName(), classElement.getCodeValue(), classElement.getElementName());
            termElementList.add(newTermElement);
        }

        return termElementList;
    }

    private void generateCodedItemFields(List<CodedItemElement> codedItemElements, List<ClassificationElement> classificationElements) {
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
}
