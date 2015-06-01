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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.akaza.openclinica.bean.managestudy.StudySubjectBean;

/**
 * OdmExtractUtil.
 */
@SuppressWarnings("unchecked")
public final class OdmExtractUtil {

	private OdmExtractUtil() {
	}

	public static class StudySubjectsHolder {

		private String studySubjectIds = "";

		private List<StudySubjectBean> studySubjectList = new ArrayList<StudySubjectBean>();

		public String getStudySubjectIds() {
			return studySubjectIds;
		}

		public List<StudySubjectBean> getStudySubjectList() {
			return studySubjectList;
		}

		/**
		 * Adds study subject.
		 * 
		 * @param studySubjectBean
		 *            StudySubjectBean
		 */
		public void addStudySubject(StudySubjectBean studySubjectBean) {
			studySubjectList.add(studySubjectBean);
			studySubjectIds = studySubjectIds.concat(studySubjectIds.isEmpty() ? "" : ",").concat(
					Integer.toString(studySubjectBean.getId()));
		}
	}

	/**
	 * Returns map.
	 * 
	 * @param key
	 *            int
	 * @param value
	 *            int
	 * @return Map
	 */
	public static Map<Integer, Integer> pair(int key, int value) {
		Map<Integer, Integer> pair = new HashMap<Integer, Integer>();
		pair.put(key, value);
		return pair;
	}

	/**
	 * Returns pair list.
	 *
	 * @param key
	 *            int
	 * @param value
	 *            int
	 * @return List
	 */
	public static List<Map<Integer, Integer>> pairList(int key, int value) {
		List<Map<Integer, Integer>> pairList = new ArrayList<Map<Integer, Integer>>();
		pairList.add(pair(key, value));
		return pairList;
	}

	/**
	 * Returns keys as sql.
	 * 
	 * @param pairList
	 *            List
	 * @return String
	 */
	public static String keysAsSql(List<Map<Integer, Integer>> pairList) {
		Set<Integer> set = new HashSet<Integer>();
		for (Map pair : pairList) {
			set.addAll(pair.keySet());
		}
		return set.toString().replace("]", ")").replace("[", "(");
	}

	/**
	 * Returns keys as sql.
	 * 
	 * @param pairList
	 *            List
	 * @return String
	 */
	public static String valuesAsSql(List<Map<Integer, Integer>> pairList) {
		Set<Integer> set = new HashSet<Integer>();
		for (Map pair : pairList) {
			set.addAll(pair.values());
		}
		return set.toString().replace("]", ")").replace("[", "(");
	}

	/**
	 * Returns all study subjects.
	 * 
	 * @param mapOfStudySubjectsHolderList
	 *            Map
	 * @return ArrayList
	 */
	public static ArrayList getAllStudySubjects(
			Map<Integer, List<OdmExtractUtil.StudySubjectsHolder>> mapOfStudySubjectsHolderList) {
		ArrayList studySubjectsList = new ArrayList<StudySubjectBean>();
		for (List<OdmExtractUtil.StudySubjectsHolder> studySubjectsHolderList : mapOfStudySubjectsHolderList.values()) {
			for (OdmExtractUtil.StudySubjectsHolder studySubjectsHolder : studySubjectsHolderList) {
				studySubjectsList.addAll(studySubjectsHolder.getStudySubjectList());
			}
		}
		return studySubjectsList;
	}
}
