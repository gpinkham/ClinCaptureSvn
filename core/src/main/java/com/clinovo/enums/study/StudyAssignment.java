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
package com.clinovo.enums.study;

/**
 * StudyAssignment.
 */
public enum StudyAssignment {

	EMPTY_VALUE(0, "", "select"), SINGLE_GROUP(1, "single_group", "single_group"), PARALLEL(2, "parallel", "parallel"), CROSS_OVER(3, "cross_over", "cross_over"),
	FACTORIAL(4, "factorial", "factorial"), EXPANDED_ACCESS(5, "expanded_access", "expanded_access");

	private int id;
	private String code;
	private String value;

	StudyAssignment(int id, String value, String code) {
		this.id = id;
		this.code = code;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public String getCode() {
		return code;
	}

	/**
	 * Finds StudyAssignment by id.
	 *
	 * @param id
	 *            int
	 * @return StudyAssignment
	 */
	public static StudyAssignment get(int id) {
		StudyAssignment result = EMPTY_VALUE;
		for (StudyAssignment studyAssignment : StudyAssignment.values()) {
			if (studyAssignment.getId() == id) {
				result = studyAssignment;
				break;
			}
		}
		return result;
	}

	/**
	 * Finds StudyAssignment by value.
	 *
	 * @param value
	 *            String
	 * @return StudyAssignment
	 */
	public static StudyAssignment get(String value) {
		StudyAssignment result = EMPTY_VALUE;
		for (StudyAssignment studyAssignment : StudyAssignment.values()) {
			if (studyAssignment.getValue().equals(value)) {
				result = studyAssignment;
				break;
			}
		}
		return result;
	}
}