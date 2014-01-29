package org.akaza.openclinica.web.job;


import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;

public class CodingTriggerService {

    public CodingTriggerService() {

    }

    public static final String IS_ALIAS = "isAlias";
    public static final String CODED_ITEM_ID = "codedItem";
    public static final String VERBATIM_TERM = "verbatimTerm";
    public static final String CATEGORY_LIST = "categoryList";
    public static final String CODE_SEARCH_TERM = "codeSearchTerm";
    public static final String BIOONTOLOGY_URL = "bioontologyUrl";
    public static final String BIOONTOLOGY_API_KEY = "bioontologyApiKey";


    public SimpleTriggerImpl generateCodeItemService(String codedItemId, String verbatimTerm, String categoryList, String codeSearchTerm, String bioontologyUrl, String bioontologyApiKey, boolean isAlias) {

        JobDataMap codeJobDataMap = new JobDataMap();
        
        codeJobDataMap.put(CODED_ITEM_ID, codedItemId);
        codeJobDataMap.put(VERBATIM_TERM, verbatimTerm);
        codeJobDataMap.put(CATEGORY_LIST, categoryList);
        codeJobDataMap.put(CODE_SEARCH_TERM, codeSearchTerm);
        codeJobDataMap.put(IS_ALIAS, String.valueOf(isAlias));
        codeJobDataMap.put(BIOONTOLOGY_URL, bioontologyUrl);
        codeJobDataMap.put(BIOONTOLOGY_API_KEY, bioontologyApiKey);

        SimpleTriggerImpl sTrigger = new SimpleTriggerImpl();
        
        sTrigger.setName("coding_item_" + codedItemId + " verbatimTerm_" + verbatimTerm + " " + System.currentTimeMillis());
        sTrigger.setGroup("CODING");
        sTrigger.setRepeatCount(0);
        sTrigger.setRepeatInterval(1);
        sTrigger.setStartTime(new Date());
        sTrigger.setJobDataMap(codeJobDataMap);
        sTrigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        
        return sTrigger;
    }
}
