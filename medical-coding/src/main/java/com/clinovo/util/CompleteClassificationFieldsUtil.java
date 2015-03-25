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
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.model.ClassificationElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Util adds additional fields for the coded item elements.
 */
public final class CompleteClassificationFieldsUtil {

	private enum ICD910 { CAT, GRP }
	private enum MEDDRA { SOC, HLGT, HLT }
	private enum WHOD { MPN, MPNC, CMP, UMBRELLA, GENERIC, PREFERRED, ATC1, CNTR }
	private enum CTCAE { SOC, AEN }

	private static final String ICD9CM = "ICD9CM";
	private static final String ICD10 = "ICD10";
	private static final String MEDDRAC = "MEDDRA";
	private static final String CTCAEC = "CTCAE";
	private static final String WHODC = "WHOD";

	/**
	 * Sets elements name for term dictionary.
	 *
	 * @param classificationElements the list of classification elements.
	 * @param dictionary the classification elements list.
	 * @throws SearchException the exception if unknown dictionary type specified.
	 */
	public static void completeClassificationNameFields(ArrayList<ClassificationElement> classificationElements, String dictionary) throws SearchException {

		ArrayList dictionaryValuesList;

		if (dictionary.equals(ICD9CM) || dictionary.equals(ICD10)) {
			dictionaryValuesList = new ArrayList<ICD910>(Arrays.asList(ICD910.values()));
		} else if (dictionary.equals(MEDDRAC)) {
			dictionaryValuesList = new ArrayList<MEDDRA>(Arrays.asList(MEDDRA.values()));
		} else if (dictionary.equals(CTCAEC)) {
			dictionaryValuesList = new ArrayList<CTCAE>(Arrays.asList(CTCAE.values()));
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

	/**
	 * Sets basic information about coded item.
	 *
	 * @param classificationResponse the list of classification elements from bioportal web-service.
	 * @param dictionary the coded item dictionary.
	 * @param prefLabel the term preferable name.
	 * @param codeHttpPath the coded item http path.
	 * @return the list of updated classification.
	 * @throws SearchException for all exceptions.
	 */
	public static List<Classification> firstResponse(List<Classification> classificationResponse, String dictionary, String prefLabel, String codeHttpPath) throws SearchException {

			Classification classification = new Classification();
			classification.setHttpPath(codeHttpPath);

			ClassificationElement classificationElement = new ClassificationElement();
			classificationElement.setElementName(getFirstElementName(dictionary));
			classificationElement.setCodeName(prefLabel);

			classification.addClassificationElement(classificationElement);

			classificationResponse.add(classification);

		return classificationResponse;
	}

	private static String getFirstElementName(String termDictionary) throws SearchException {
		if (MEDDRAC.equalsIgnoreCase(termDictionary)) {
			return "LLT";
		} else if (ICD10.equalsIgnoreCase(termDictionary) || ICD9CM.equalsIgnoreCase(termDictionary)) {
			return "EXT";
		} else if (CTCAEC.equalsIgnoreCase(termDictionary)) {
			return "AEG";
		}
		throw new SearchException("Unknown dictionary type specified");
	}

	/**
	 * Returns list of ontology elements.
	 *
	 * @param ontologyName ontology for search.
	 * @return the list with ontology elements or null.
	 */
	public static List getEnumAsList(String ontologyName) {
		if (ontologyName.equals(MEDDRAC)) {
			return Arrays.asList(MEDDRA.values());
		} else if (ontologyName.equals("ICD_10") || ontologyName.equals("ICD_9CM")) {
			return Arrays.asList(ICD910.values());
		} else if (ontologyName.equals(CTCAEC)) {
			return Arrays.asList(CTCAE.values());
		} else if (ontologyName.equals(WHODC)) {
			return Arrays.asList(WHOD.values());
		}
		return null;
	}
}
