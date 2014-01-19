package com.clinovo.util;

import com.clinovo.coding.SearchException;
import com.clinovo.coding.model.ClassificationElement;

import java.util.ArrayList;
import java.util.Arrays;

public class CompleteClassificationFieldsUtil {

    enum ICD910 {CAT, GRP, EXT}

    enum MEDDRA {SOC, HLGT, HLT, PT, LTT}

    @SuppressWarnings("rawtypes")
	public static void completeClassificationNameFields(ArrayList<ClassificationElement> classificationElements, String dictionary) throws SearchException {

        ArrayList dictionaryValuesList;

        if (dictionary.equals("ICD9CM") || dictionary.equals("ICD10")) {

            dictionaryValuesList = new ArrayList<ICD910>(Arrays.asList(ICD910.values()));

        } else if (dictionary.equals("MEDDRA")) {

            if (classificationElements.size() == 4) {
                ClassificationElement lttElement = new ClassificationElement();
                lttElement.setCodeName("");
                classificationElements.add(lttElement);
            }
            dictionaryValuesList = new ArrayList<MEDDRA>(Arrays.asList(MEDDRA.values()));

        } else {

            throw new SearchException("Unknown dictionary type specified");
        }

        if (dictionaryValuesList.size() == classificationElements.size()) {
            for (int i = 0; i < classificationElements.size(); i++) {
                classificationElements.get(i).setElementName(dictionaryValuesList.get(i).toString());
            }
        }
        
        // Copy over PT to LTT
        if (dictionary.equals("MEDDRA")) {
        	
        	if (classificationElements.get(4).getCodeName().isEmpty()) {
        		classificationElements.get(4).setCodeName(classificationElements.get(3).getCodeName());
        	}
        }
    }
}
