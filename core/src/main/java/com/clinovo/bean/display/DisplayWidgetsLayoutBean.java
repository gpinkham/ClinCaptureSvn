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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.bean.display;

import java.util.Comparator;

import org.akaza.openclinica.bean.core.EntityBean;

@SuppressWarnings("serial")
public class DisplayWidgetsLayoutBean extends EntityBean {

	private String widgetName;
	private int widgetId;
	private int ordinal;
	private boolean twoColumnWidget;

	public static ComparatorForDisplayWidgetsLayout comparatorForDisplayWidgetsLayout = new ComparatorForDisplayWidgetsLayout();

	public String getWidgetName() {
		return widgetName;
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(int widgetId) {
		this.widgetId = widgetId;
	}

	public boolean isTwoColumnWidget() {
		return twoColumnWidget;
	}

	public void setTwoColumnWidget(boolean twoColumnWidget) {
		this.twoColumnWidget = twoColumnWidget;
	}
}

class ComparatorForDisplayWidgetsLayout implements Comparator<DisplayWidgetsLayoutBean> {

	public int compare(DisplayWidgetsLayoutBean display1, DisplayWidgetsLayoutBean display2) {

        if (display1.getOrdinal() < display2.getOrdinal()) {

            return -1;
		}
		if (display1.getOrdinal() > display2.getOrdinal()) {

            return 1;
		}

        return 0;
	}
}
