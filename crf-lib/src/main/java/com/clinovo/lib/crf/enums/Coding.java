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

package com.clinovo.lib.crf.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Coding.
 */
public enum Coding {

	CTCAE_GR("GR", "Vertical", "coding_radio", "1,2,3,4,5", true),

	CTCAE_SOC("SOC"), CTCAE_SOCC("SOCC"), CTCAE_AEN("AEN"), CTCAE_AENC("AENC"), CTCAE_AEG("AEG"), CTCAE_AEGC("AEGC"),

	ICD_CAT("CAT"), ICD_CATC("CATC"), ICD_GRP("GRP"), ICD_GRPC("GRPC"), ICD_EXT("EXT"), ICD_EXTC("EXTC"),

	MEDDRA_SOC("SOC"), MEDDRA_SOCC("SOCC"), MEDDRA_HLGT("HLGT"), MEDDRA_HLGTC("HLGTC"), MEDDRA_HLT("HLT"), MEDDRA_HLTC("HLTC"),

	MEDDRA_PT("PT"), MEDDRA_PTC("PTC"), MEDDRA_LLT("LLT"), MEDDRA_LLTC("LLTC"),

	WHODRUG_MPN("MPN"), WHODRUG_MPNC("MPNC"), WHODRUG_CMP("CMP"), WHODRUG_ATC1("ATC1"), WHODRUG_ATC1C("ATC1C"), WHODRUG_ATC2("ATC2"), WHODRUG_ATC2C("ATC2C"),

	WHODRUG_ATC3("ATC3"), WHODRUG_ATC3C("ATC3C"), WHODRUG_ATC4("ATC4"), WHODRUG_ATC4C("ATC4C"), WHODRUG_ATC5("ATC5"), WHODRUG_ATC5C("ATC5C"),

	WHODRUG_ATC6("ATC6"), WHODRUG_ATC6C("ATC6C"), WHODRUG_ATC7("ATC7"), WHODRUG_ATC7C("ATC7C"), WHODRUG_CNTR("CNTR");

	private String type;

	private String layout;

	private String postfix;

	private boolean visible;

	private List<String> optionsValues;

	Coding(String postfix) {
		this.layout = "";
		this.visible = false;
		this.postfix = postfix;
		this.type = "coding_system";
		optionsValues = new ArrayList<String>();
	}

	Coding(String postfix, String layout, String type, String optionsValues, boolean visible) {
		this.type = type;
		this.layout = layout;
		this.postfix = postfix;
		this.visible = visible;
		this.optionsValues = Arrays.asList(optionsValues.split(","));
	}

	public String getType() {
		return type;
	}

	public String getLayout() {
		return layout;
	}

	public String getPostfix() {
		return postfix;
	}

	public boolean isVisible() {
		return visible;
	}

	public List<String> getOptionsValues() {
		return optionsValues;
	}
}
