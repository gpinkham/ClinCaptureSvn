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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides support functionality to help build preview of new CRF/CRF version.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class SpreadsheetPreviewUtil {

	private SpreadsheetPreviewUtil() {
	}

	/**
	 * Removes empty sections (with no items assigned) from CRF sections metadata map, if there are any.
	 *
	 * @param sections
	 *            Map
	 * @param items
	 *            Map
	 * @return Map CRF sections metadata map, cleared of empty sections, if there are any.
	 */
	public static Map<Integer, Map<String, String>> clearOutOfEmptySections(Map<Integer, Map<String, String>> sections,
			Map<Integer, Map<String, String>> items) {

		if (sections == null || items == null || sections.isEmpty() || items.isEmpty()) {
			return new TreeMap<Integer, Map<String, String>>();
		}

		List<Integer> mapKeysOfEmptyCrfSections = new ArrayList<Integer>();
		for (Map.Entry entry : sections.entrySet()) {
			String sectionName = (String) ((Map) entry.getValue()).get("section_label");
			int numberOfItemsInSection = getNumberOfItemsInSection(items, sectionName);
			if (numberOfItemsInSection == 0) {
				mapKeysOfEmptyCrfSections.add((Integer) entry.getKey());
			}
		}

		if (mapKeysOfEmptyCrfSections.isEmpty()) {
			return sections;
		} else {
			for (Integer mapKey : mapKeysOfEmptyCrfSections) {
				sections.remove(mapKey);
			}
			int mapEntryKey = 1;
			Map<Integer, Map<String, String>> resultSectionsMap = new TreeMap<Integer, Map<String, String>>();
			for (Map.Entry entry : sections.entrySet()) {
				resultSectionsMap.put(mapEntryKey++, (Map<String, String>) entry.getValue());
			}
			return resultSectionsMap;
		}
	}

	/**
	 * Determines the number of items associated with specific section.
	 *
	 * @param itemsMap
	 *            Map
	 * @param sectionLabel
	 *            String
	 * @return int the number of items associated with specific section, 0 if there are no items in that section.
	 */
	public static int getNumberOfItemsInSection(Map<Integer, Map<String, String>> itemsMap, String sectionLabel) {

		if (itemsMap == null || sectionLabel == null || itemsMap.isEmpty() || sectionLabel.isEmpty()) {
			return 0;
		}
		int itemCount = 0;
		for (Map.Entry<Integer, Map<String, String>> entryItemMetadata : itemsMap.entrySet()) {
			Map<String, String> itemProperties = entryItemMetadata.getValue();
			for (Map.Entry<String, String> itemProperty : itemProperties.entrySet()) {
				String columnName = itemProperty.getKey();
				String value = itemProperty.getValue();
				if ("section_label".equalsIgnoreCase(columnName) && sectionLabel.equalsIgnoreCase(value)) {
					itemCount++;
				}
			}
		}
		return itemCount;
	}
}
