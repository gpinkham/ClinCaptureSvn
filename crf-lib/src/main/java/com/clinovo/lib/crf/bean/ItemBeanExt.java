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

package com.clinovo.lib.crf.bean;

import java.util.HashMap;
import java.util.Map;

import org.akaza.openclinica.bean.odmbeans.SimpleConditionalDisplayBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.domain.datamap.ResponseSet;

import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;
import com.clinovo.lib.crf.enums.RealValueKey;

/**
 * ItemBeanExt.
 */
@SuppressWarnings("serial")
public class ItemBeanExt extends ItemBean {

	// main beans
	private SectionBean sectionBean;

	private ResponseSet responseSet;

	private ItemBean parentItemBean;

	private ItemGroupBean itemGroupBean;

	private SimpleConditionalDisplayBean simpleConditionalDisplayBean;

	// real data
	private Map<RealValueKey, String> realValues = new HashMap<RealValueKey, String>();

	/**
	 * Default constructor.
	 */
	public ItemBeanExt() {
		//
	}

	/**
	 * Default constructor.
	 */
	public ItemBeanExt(ItemBean itemBean) {
		itemBean.cloneTo(this);
	}

	/**
	 * Constructor.
	 *
	 * @param crfBuilder
	 *            ExcelCrfBuilder
	 */
	public ItemBeanExt(ExcelCrfBuilder crfBuilder) {
		setIndex(crfBuilder.getIndex());
		setRowNumber(crfBuilder.getRowNumber());
		setSheetNumber(crfBuilder.getCurrentSheetNumber());
	}

	public SectionBean getSectionBean() {
		return sectionBean;
	}

	public void setSectionBean(SectionBean sectionBean) {
		this.sectionBean = sectionBean;
	}

	public ResponseSet getResponseSet() {
		return responseSet;
	}

	public void setResponseSet(ResponseSet responseSet) {
		this.responseSet = responseSet;
	}

	public ItemBean getParentItemBean() {
		return parentItemBean;
	}

	public void setParentItemBean(ItemBean parentItemBean) {
		this.parentItemBean = parentItemBean;
	}

	public ItemGroupBean getItemGroupBean() {
		return itemGroupBean;
	}

	public void setItemGroupBean(ItemGroupBean itemGroupBean) {
		this.itemGroupBean = itemGroupBean;
	}

	public SimpleConditionalDisplayBean getSimpleConditionalDisplayBean() {
		return simpleConditionalDisplayBean;
	}

	public void setSimpleConditionalDisplayBean(SimpleConditionalDisplayBean simpleConditionalDisplayBean) {
		this.simpleConditionalDisplayBean = simpleConditionalDisplayBean;
	}

	/**
	 * Returns real string value for RealValueKey.
	 * 
	 * @param realValueKey
	 *            RealValueKey
	 * @return String
	 */
	public String getRealValue(RealValueKey realValueKey) {
		String value = realValues.get(realValueKey);
		return value == null ? "" : value;
	}

	/**
	 * Sets real string value for RealValueKey.
	 *
	 * @param realValueKey
	 *            RealValueKey
	 * @param value
	 *            String
	 */
	public void setRealValue(RealValueKey realValueKey, String value) {
		realValues.put(realValueKey, value == null ? "" : value);
	}
}
