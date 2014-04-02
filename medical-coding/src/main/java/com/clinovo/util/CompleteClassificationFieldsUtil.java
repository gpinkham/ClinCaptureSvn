package com.clinovo.util;

import com.clinovo.coding.SearchException;
import com.clinovo.coding.model.ClassificationElement;

import java.util.ArrayList;
import java.util.Arrays;

public class CompleteClassificationFieldsUtil {

	enum ICD910 {CAT, GRP}

	enum MEDDRA {SOC, HLGT, HLT}

	enum WHOD {ATC7, ATC6, ATC5, ATC4, ATC3, ATC2, ATC1, CMP, CNTR}

	@SuppressWarnings("rawtypes")
	public static void completeClassificationNameFields(ArrayList<ClassificationElement> classificationElements, String dictionary) throws SearchException {

		ArrayList dictionaryValuesList;

		if (dictionary.equals("ICD9CM") || dictionary.equals("ICD10")) {

			dictionaryValuesList = new ArrayList<ICD910>(Arrays.asList(ICD910.values()));

		} else if (dictionary.equals("MEDDRA")) {

			dictionaryValuesList = new ArrayList<MEDDRA>(Arrays.asList(MEDDRA.values()));

		} else if (dictionary.equals("WHOD")) {

			//remove VA uniq key
			classificationElements.remove(0);
			dictionaryValuesList = new ArrayList<WHOD>(Arrays.asList(WHOD.values()));

			for (ClassificationElement whodClassificationElement : classificationElements) {
				String classificationElement = whodClassificationElement.getCodeName().replaceAll("_", " ").replaceAll(" and ", " & ");
				whodClassificationElement.setCodeName(classificationElement.substring(0, classificationElement.indexOf("@")));
			}

		} else {

			throw new SearchException("Unknown dictionary type specified");
		}

		if (classificationElements.size() != 0) {

			if (dictionaryValuesList.size() < classificationElements.size()) {

				int numberElementsToRemove = classificationElements.size() - dictionaryValuesList.size();

				for (int removeIndex = 0; removeIndex < numberElementsToRemove; removeIndex++) {
					classificationElements.remove(0);
				}
			} else if (dictionaryValuesList.size() > classificationElements.size()) {

				int numberElementsToRemove = dictionaryValuesList.size() - classificationElements.size();

				for (int removeIndex = 0; removeIndex < numberElementsToRemove; removeIndex++) {
					dictionaryValuesList.remove(0);
				}
			}

			for (int i = 0; i < dictionaryValuesList.size(); i++) {

				classificationElements.get(i).setElementName(dictionaryValuesList.get(i).toString());
			}
		}
	}
}
