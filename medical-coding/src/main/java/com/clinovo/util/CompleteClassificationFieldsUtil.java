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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

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
