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
import com.clinovo.model.Ingredient;
import com.clinovo.model.LowLevelTerm;
import com.clinovo.model.MedicalHierarchy;
import com.clinovo.model.MedicalProduct;
import com.clinovo.model.Therapgroup;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Util adds additional fields for the coded item elements.
 */
public final class CodingFieldsUtil {


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

		ArrayList<?> dictionaryValuesList;

		if (dictionary.equals(ICD9CM) || dictionary.equals(ICD10)) {
			dictionaryValuesList = new ArrayList<ICD910>(Arrays.asList(ICD910.values()));
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
		if (ICD10.equalsIgnoreCase(termDictionary) || ICD9CM.equalsIgnoreCase(termDictionary)) {
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
	public static List<?> getEnumAsList(String ontologyName) {
		if (ontologyName.contains(MEDDRAC)) {
			return Arrays.asList(MEDDRA.values());
		} else if (ontologyName.equals("ICD_10") || ontologyName.equals("ICD_9CM")) {
			return Arrays.asList(ICD910.values());
		} else if (ontologyName.equals(CTCAEC)) {
			return Arrays.asList(CTCAE.values());
		} else if (ontologyName.contains(WHODC)) {
			return Arrays.asList(WHOD.values());
		}
		return null;
	}

	/**
	 * Returns classification list from medical product list.
	 *
	 * @param mpList the medical product list.
	 * @param locale the system locale.
	 * @return the list of medical products.
	 * @throws ParseException for parsing exception.
	 */
	public static List<Classification> medicalProductListToClassificationList(List<Object> mpList, Locale locale) throws ParseException {

		List<Classification> classifications = new ArrayList<Classification>();
		for (Object mp : mpList) {
			classifications.add(medicalProductToClassification((MedicalProduct) mp, locale));
		}
		Collections.sort(classifications, new ClassificationSortByReference());
		return classifications;
	}

	/**
	 * Returns classification object from medical product object.
	 *
	 * @param mp the medical product bean.
	 * @param locale the system locale.
	 * @return the classification object with element name, code and code name.
	 * @throws ParseException for all parse exceptions.
	 */
	public static Classification medicalProductToClassification(MedicalProduct mp, Locale locale) throws ParseException {

		Classification classification = new Classification();
		classification.setHttpPath(String.valueOf(mp.getMedicinalprodId()));
		classification.addClassificationElement(new ClassificationElement("MPN", mp.getDrugName(), ""));
		classification.addClassificationElement(new ClassificationElement("MPNC", mp.getDrugRecordNumber() + mp.getSequenceNumber1() + mp.getSequenceNumber2(), ""));
		String ingList = String.valueOf(mp.getDrugRecordNumber());
		for (Ingredient ing : mp.getIngList()) {
			ingList = ingList.concat(", ").concat(ing.getSun().getSubstanceName());
		}
		classification.addClassificationElement(new ClassificationElement("CMP", ingList, ""));
		int counter = 1;
		for (Therapgroup thg : mp.getThgList()) {

			classification.addClassificationElement(new ClassificationElement("ATC" + counter, thg.getAtc().getAtcText(), thg.getAtc().getAtcCode()));
			counter++;
		}
		classification.addClassificationElement(new ClassificationElement("CNTR", mp.getSourceCountryBean().getCountryName(), ""));

		ClassificationElement dateCreate = new ClassificationElement();
		dateCreate.setElementName("date_created");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", locale);
		Date d = sdf.parse(mp.getCreateDate());
		sdf.applyPattern("dd-MMM-yyyy");
		String formattedDate = sdf.format(d);
		dateCreate.setCodeName(formattedDate);

		classification.addClassificationElement(new ClassificationElement("date_created", formattedDate, ""));
		classification.addClassificationElement(new ClassificationElement("GENERIC", mp.getGeneric().equals("Y") ? "Yes" : "No", ""));

		ClassificationElement umbrella = new ClassificationElement();
		umbrella.setElementName("UMBRELLA");
		int firstDigit = mp.getDrugRecordNumber();
		final int number = 9;
		final int divider = 10;
		while (firstDigit > number) {
			firstDigit = firstDigit / divider;
		}
		classification.addClassificationElement(new ClassificationElement("UMBRELLA", mp.getDrugName().indexOf(" nos") > 0 || firstDigit == number ? "Yes" : "No", ""));
		classification.addClassificationElement(new ClassificationElement("PREFERRED", Integer.valueOf(mp.getSequenceNumber1()) > 1 && Integer.valueOf(mp.getSequenceNumber2()) == 1 ? "Yes" : "No", ""));

		return classification;
	}

	/**
	 * Comparator class that sort list using preferred value.
	 */
	public static class ClassificationSortByReference implements Comparator<Classification> {
		/**
		 * @param o1 the first object to be compared.
		 * @param o2 the second object to be compared.
		 * @return a negative integer, zero, or a positive integer as the
		 * first argument is less than, equal to, or greater than the
		 * second.
		 */
		public int compare(Classification o1, Classification o2) {
			for (ClassificationElement classification : o1.getClassificationElement()) {
				for (ClassificationElement classification2 : o2.getClassificationElement()) {
					if (classification.getElementName().equals("PREFERRED") && classification.getElementName().equals(classification2.getElementName())) {
						if ((classification.getCodeName().equals("No") && classification2.getCodeName().equals("Yes"))) {
							return 1;
						} else if (classification.getCodeName().equals("Yes") && classification2.getCodeName().equals("No")) {
							return -1;
						}
					}
				}
			}
			return 0;
		}
	}

	/**
	 * Returns classification object from medical hierarchy object.
	 *
	 * @param lowLevelTerm the list of medical hierarchy beans.
	 * @return the list of classifications.
	 */
	public static List<Classification> medicalHierarchyToClassificationList(List<Object> lowLevelTerm) {
		List<Classification> classifications = new ArrayList<Classification>();
		List<LowLevelTerm> updatedLowLevelTermList = getListWithUniqueMedicalHierarchy(lowLevelTerm);
		for (LowLevelTerm llt : updatedLowLevelTermList) {
			classifications.add(medicalHierarchyToClassification(llt));
		}

		return classifications;
	}

	private static List<LowLevelTerm> getListWithUniqueMedicalHierarchy(List<Object> lowLevelTerm) {
		List<LowLevelTerm> finalList = new ArrayList<LowLevelTerm>();
		for (Object object : lowLevelTerm) {
			LowLevelTerm llt = (LowLevelTerm) object;
			if (llt.getMedicalHierarchy().size() > 1) {
				for (MedicalHierarchy medicalHierarchy : llt.getMedicalHierarchy()) {
					LowLevelTerm termTerm = new LowLevelTerm();
					List<MedicalHierarchy> medicalHierarchyList = new ArrayList<MedicalHierarchy>();
					medicalHierarchyList.add(medicalHierarchy);
					termTerm.setMedicalHierarchy(medicalHierarchyList);
					termTerm.setLltCode(llt.getLltCode());
					termTerm.setLltCurrency(llt.getLltCurrency());
					termTerm.setLltName(llt.getLltName());
					termTerm.setPtCode(llt.getPtCode());
					finalList.add(termTerm);
				}
			} else {
				finalList.add(llt);
			}
		}
		return finalList;
	}

	/**
	 * Returns classification object from medical hierarchy object.
	 *
	 * @param lowLevelTerm the medical low level term bean.
	 * @return the classification object.
	 */
	public static Classification medicalHierarchyToClassification(LowLevelTerm lowLevelTerm) {

		Classification classification = new Classification();
		for (MedicalHierarchy medicalHierarchy : lowLevelTerm.getMedicalHierarchy()) {
			classification.setHttpPath(String.valueOf(lowLevelTerm.getPtCode()) + "-" + String.valueOf(lowLevelTerm.getLltCode()));
			classification.addClassificationElement(new ClassificationElement("SOC", medicalHierarchy.getSocName(), String.valueOf(medicalHierarchy.getSocCode())));
			classification.addClassificationElement(new ClassificationElement("HLT", medicalHierarchy.getHltName(), String.valueOf(medicalHierarchy.getHltCode())));
			classification.addClassificationElement(new ClassificationElement("HLGT", medicalHierarchy.getHlgtName(), String.valueOf(medicalHierarchy.getHlgtCode())));
			classification.addClassificationElement(new ClassificationElement("PT", medicalHierarchy.getPtName(), String.valueOf(lowLevelTerm.getPtCode())));
			}
		classification.addClassificationElement(new ClassificationElement("LLT", lowLevelTerm.getLltName(), String.valueOf(lowLevelTerm.getLltCode())));
		return classification;
	}

	/**
	 * Returns valid ontology name for UI.
	 *
	 * @param ontologyName the name of ontology in the db required format.
	 * @return the valid ontology name for UI.
	 */
	public static String getValidUiOntologyName(String ontologyName) {
		final int ontologyNameWithVersionLength = 4;
		final int ontologyMonthS = 1;
		final int ontologyMonthE = 3;
		final int ontologyYearS = 2;
		if (ontologyName.contains(WHODC)) {
			String whod = "WHODrug";
			if (ontologyName.length() > ontologyNameWithVersionLength) {

				String monthNum = ontologyName.substring(ontologyName.indexOf("-") + ontologyMonthS, ontologyName.indexOf("-") + ontologyMonthE);
				String yearNum = ontologyName.substring(ontologyName.length() - ontologyYearS, ontologyName.length());
				String monthName = new DateFormatSymbols().getMonths()[Integer.valueOf(monthNum) - 1];
				return whod + " " + monthName.substring(0, ontologyMonthE) + yearNum;
			} else {
				return whod;
			}

		} else if (ontologyName.contains(MEDDRAC)) {
			final int versionSIndex = 1;
			final int versionEIndex = 3;
			final int simpleNameLength = 6;
			final int nameWithSubversionLength = 9;
			String meddra = "MedDRA";
			if (ontologyName.length() > simpleNameLength) {
				String version = ontologyName.substring(ontologyName.indexOf("-") + versionSIndex, ontologyName.indexOf("-") + versionEIndex);
				String subversion = "";
				if (ontologyName.length() > nameWithSubversionLength) {
					subversion = ontologyName.substring(ontologyName.indexOf("-") + versionEIndex, ontologyName.length());
				}
				subversion = !subversion.isEmpty() ? "." + subversion : "";
				return meddra + " v" + version + subversion;
			} else {
				return meddra;
			}
		}
		return ontologyName;
	}

	/**
	 * Returns the valid ontology name for db connection.
	 *
	 * @param ontologyName the UI ontology name.
	 * @return the ontology name for db connection.
	 * @throws ParseException for all parsing exceptions.
	 */
	public static String getValidDbOntologyName(String ontologyName) throws ParseException {
		String ontologyNumber = ontologyName.replaceAll("\\D+", "");
		if (ontologyName.toUpperCase().contains(WHODC) && !ontologyNumber.isEmpty()) {
			final int monthNumE = 4;
			final int monthCounter = 9;
			Date date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(ontologyName.substring(ontologyName.indexOf(" ") + 1, ontologyName.indexOf(" ") + monthNumE));
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int monthNumber = cal.get(Calendar.MONTH) + 1;
			String monthNumFormatted = monthNumber > monthCounter ? String.valueOf(monthNumber) : "0" + String.valueOf(monthNumber);
			ontologyName = "WHOD-" + monthNumFormatted + ontologyNumber;
		} else if (ontologyName.toUpperCase().contains(MEDDRAC) && !ontologyNumber.isEmpty()) {
			ontologyName = "MEDDRA-" + ontologyNumber;
		} else {
			ontologyName = ontologyName.contains(WHODC) ? "WHOD" : ontologyName.toUpperCase();
		}
		return ontologyName;
	}
}
