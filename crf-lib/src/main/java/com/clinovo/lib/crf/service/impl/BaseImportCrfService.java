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

package com.clinovo.lib.crf.service.impl;

import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.SectionBean;

import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;
import com.clinovo.lib.crf.service.ImportCrfService;

/**
 * BaseImportCrfService.
 */
public abstract class BaseImportCrfService implements ImportCrfService {

	protected org.akaza.openclinica.domain.datamap.ResponseType newResponseType(ResponseType responseType) {
		org.akaza.openclinica.domain.datamap.ResponseType result = new org.akaza.openclinica.domain.datamap.ResponseType();
		result.setName(responseType.getCode());
		result.setResponseTypeId(responseType.getId());
		return result;
	}

	protected SectionBean newSectionBean(ExcelCrfBuilder crfBuilder) {
		SectionBean sectionBean = new SectionBean();
		sectionBean.setIndex(crfBuilder.getIndex());
		sectionBean.setRowNumber(crfBuilder.getRowNumber());
		sectionBean.setSheetNumber(crfBuilder.getCurrentSheetNumber());
		return sectionBean;
	}

	protected ItemGroupBean newItemGroupBean(ExcelCrfBuilder crfBuilder) {
		ItemGroupBean itemGroupBean = new ItemGroupBean();
		itemGroupBean.setIndex(crfBuilder.getIndex());
		itemGroupBean.setRowNumber(crfBuilder.getRowNumber());
		itemGroupBean.setSheetNumber(crfBuilder.getCurrentSheetNumber());
		return itemGroupBean;
	}

}
