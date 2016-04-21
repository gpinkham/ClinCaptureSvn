/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.util;

import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DRUtil {
	public static String getTextFromHTML(String htmlText) {
		// remove tags and returns text
		return htmlText.replaceAll("<[^>]*>", "");
	}

	public static String getValueFromRadio(DisplayItemBean dib) {
		StringBuilder defaultResult = new StringBuilder("");
		for (int i = 0, size = dib.getMetadata().getResponseSet().getOptions().size(); i < size; i++) {
			ResponseOptionBean rob = (ResponseOptionBean) dib.getMetadata().getResponseSet().getOptions().get(i);
			if (rob.isSelected())
				return rob.getText();
			defaultResult.append(defaultResult.length() > 0? ", " : "").append(rob.getText());
		}
		
		return defaultResult.toString();
	}

	public static String getValueFromSelect(DisplayItemBean dib) {
		StringBuilder result = new StringBuilder("");
		for (int i = 0, size = dib.getMetadata().getResponseSet().getOptions().size(); i < size; i++) {
			ResponseOptionBean rob = (ResponseOptionBean) dib.getMetadata().getResponseSet().getOptions().get(i);
			if (rob.isSelected()) 
				result.append(result.length() > 0? ", " : "").append(rob.getText());
		}
		
		return result.toString();
	}

	public static String getTextFromHeader(String header) {
		return header.replaceAll("</br>", "\n");
	}

	public static String[] getRepeatingColumnNames(List<DisplayItemBean> repeatingGroupRow) {
		List<String> columnNames = new ArrayList<String>();
		for (DisplayItemBean firstRowElement : repeatingGroupRow) {
			String header = getTextFromHTML(firstRowElement.getMetadata().getHeader()).isEmpty()
					? getTextFromHTML(firstRowElement.getMetadata().getLeftItemText()).isEmpty()
					? firstRowElement.getMetadata().getGroupLabel() + firstRowElement.getMetadata().getOrdinal()
					: firstRowElement.getMetadata().getLeftItemText()
					: firstRowElement.getMetadata().getHeader();
			columnNames.add(DRUtil.getTextFromHTML(header));
		}
		String[] stockArr = new String[columnNames.size()];
		return  columnNames.toArray(stockArr);
	}

	public static class RepeatingRowComparator implements Comparator<DisplayItemBean> {
		/**
		 * {@inheritDoc}
		 */
		public int compare(DisplayItemBean o1, DisplayItemBean o2) {
			return o1.getMetadata().getOrdinal() - o2.getMetadata().getOrdinal();
		}
	}
}
