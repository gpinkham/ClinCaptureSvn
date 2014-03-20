package com.clinovo.util;

import com.clinovo.coding.SearchException;
import com.clinovo.coding.model.ClassificationElement;

import java.util.ArrayList;
import java.util.Arrays;

public class CompleteClassificationFieldsUtil {

	enum ICD910 {CAT, GRP}

	enum MEDDRA {SOC, HLGT, HLT}

	@SuppressWarnings("rawtypes")
	public static void completeClassificationNameFields(ArrayList<ClassificationElement> classificationElements, String dictionary) throws SearchException {

		ArrayList dictionaryValuesList;

		if (dictionary.equals("ICD9CM") || dictionary.equals("ICD10")) {

			dictionaryValuesList = new ArrayList<ICD910>(Arrays.asList(ICD910.values()));

		} else if (dictionary.equals("MEDDRA")) {

			dictionaryValuesList = new ArrayList<MEDDRA>(Arrays.asList(MEDDRA.values()));

		} else {

			throw new SearchException("Unknown dictionary type specified");
		}

		if (classificationElements.size() != 0) {

			if (dictionaryValuesList.size() < classificationElements.size()) {

				int numberElementsToRemove = classificationElements.size() - dictionaryValuesList.size();

				for (int removeIndex = 0; removeIndex < numberElementsToRemove; removeIndex++) {
					classificationElements.remove(removeIndex);
				}
			}

			for (int i = 0; i < dictionaryValuesList.size(); i++) {

				if (i < classificationElements.size()) {

					classificationElements.get(i).setElementName(dictionaryValuesList.get(i).toString());
				}
			}
		}
	}
}
