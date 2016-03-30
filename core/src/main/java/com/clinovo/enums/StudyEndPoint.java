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
package com.clinovo.enums;

/**
 * StudyEndPoint.
 */
public enum StudyEndPoint {

	EMPTY_VALUE(0, "", "select"), SAFETY(1, "safety", "safety"), EFFICACY(2, "efficacy", "efficacy"), SAFETY_EFFICACY(3, "safety_efficacy", "safety_efficacy"),
	BIO_EQUIVALENCE(4, "bio_equivalence", "bio_equivalence"), BIO_AVAILABILITY(5, "bio_availability", "bio_availability"), PHARMACOKINETICS(6, "pharmacokinetics", "pharmacokinetics"),
	PHARMACODYNAMICS(7, "pharmacodynamics", "pharmacodynamics"), PHARMACOKINETICS_PHARMACODYNAMICS(8, "pharmacokinetics_pharmacodynamics", "pharmacokinetics_pharmacodynamics");

	private int id;
	private String code;
	private String value;

	StudyEndPoint(int id, String value, String code) {
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

	public static StudyEndPoint get(int id) {
		StudyEndPoint result = EMPTY_VALUE;
		for (StudyEndPoint studyEndPoint : StudyEndPoint.values()) {
			if (studyEndPoint.getId() == id) {
				result = studyEndPoint;
				break;
			}
		}
		return result;
	}

	public static StudyEndPoint get(String value) {
		StudyEndPoint result = EMPTY_VALUE;
		for (StudyEndPoint studyEndPoint : StudyEndPoint.values()) {
			if (studyEndPoint.getValue().equals(value)) {
				result = studyEndPoint;
				break;
			}
		}
		return result;
	}
}
