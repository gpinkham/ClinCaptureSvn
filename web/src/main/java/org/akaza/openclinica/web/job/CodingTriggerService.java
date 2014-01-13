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
    public static final String PREFERRED_NAME = "preferredName";


    public SimpleTriggerImpl generateCodeItemService(String codedItemId, String verbatimTerm, String preferredName, boolean isAlias) {

        JobDataMap codeJobDataMap = new JobDataMap();
        
        codeJobDataMap.put(CODED_ITEM_ID, codedItemId);
        codeJobDataMap.put(VERBATIM_TERM, verbatimTerm);
        codeJobDataMap.put(PREFERRED_NAME, preferredName);
        codeJobDataMap.put(IS_ALIAS, String.valueOf(isAlias));
        
        SimpleTriggerImpl sTrigger = new SimpleTriggerImpl();
        
        sTrigger.setName("coding_item_" + codedItemId + " verbatiumTerm_" + preferredName + " " + System.currentTimeMillis());
        sTrigger.setGroup("CODING");
        sTrigger.setRepeatCount(0);
        sTrigger.setRepeatInterval(1);
        sTrigger.setStartTime(new Date());
        sTrigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        sTrigger.setJobDataMap(codeJobDataMap);
        
        return sTrigger;
    }
}
