/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;

import com.clinovo.model.EventCRFSectionBean;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DisplaySectionBean {
	private CRFBean crf;
	private CRFVersionBean crfVersion;
	private EventCRFBean eventCRF;
	private EventDefinitionCRFBean eventDefinitionCRF;
	private SectionBean section;
	private EventCRFSectionBean eventCRFSection;
	private ArrayList<DisplayItemBean> items; // an array of DisplayItemBeans which belong to
	// this section
	private final boolean checkInputs;
	private boolean firstSection;
	private boolean lastSection;
	private Integer ruleEditCheck;
	// collect item_id of simple conditional display items to be shown in this section
	private Set<Integer> showSCDItemIds;
	// The Item groups associated with this section.
	private List<DisplayItemGroupBean> displayFormGroups; // for items in
	// groups

	private List<DisplayItemWithGroupBean> displayItemGroups; // for all items
	
	// including
	// single ones
	// and in group
	// ones

	public DisplaySectionBean() {
		crf = new CRFBean();
		crfVersion = new CRFVersionBean();
		eventCRF = new EventCRFBean();
		section = new SectionBean();
		items = new ArrayList();
		checkInputs = true;
		firstSection = false;
		lastSection = false;
		displayFormGroups = new ArrayList<DisplayItemGroupBean>();
		displayItemGroups = new ArrayList<DisplayItemWithGroupBean>();
		ruleEditCheck = 0;
		showSCDItemIds = new HashSet<Integer>();
	}

	/**
	 * @return the displayItemGroups
	 */
	public List<DisplayItemWithGroupBean> getDisplayItemGroups() {
		return displayItemGroups;
	}

	/**
	 * @param displayItemGroups
	 *            the displayItemGroups to set
	 */
	public void setDisplayItemGroups(List<DisplayItemWithGroupBean> displayItemGroups) {
		this.displayItemGroups = displayItemGroups;
	}

	public List<DisplayItemGroupBean> getDisplayFormGroups() {
		return displayFormGroups;
	}

	public void setDisplayFormGroups(List<DisplayItemGroupBean> displayFormGroups) {
		this.displayFormGroups = displayFormGroups;
	}

	/**
	 * @return Returns the crf.
	 */
	public CRFBean getCrf() {
		return crf;
	}

	/**
	 * @param crf
	 *            The crf to set.
	 */
	public void setCrf(CRFBean crf) {
		this.crf = crf;
	}

	/**
	 * @return Returns the crfVersion.
	 */
	public CRFVersionBean getCrfVersion() {
		return crfVersion;
	}

	/**
	 * @param crfVersion
	 *            The crfVersion to set.
	 */
	public void setCrfVersion(CRFVersionBean crfVersion) {
		this.crfVersion = crfVersion;
	}

	/**
	 * @return Returns the eventCRF.
	 */
	public EventCRFBean getEventCRF() {
		return eventCRF;
	}

	/**
	 * @param eventCRF
	 *            The eventCRF to set.
	 */
	public void setEventCRF(EventCRFBean eventCRF) {
		this.eventCRF = eventCRF;
	}

	/**
	 * @return Returns the items.
	 */
	public ArrayList<DisplayItemBean> getItems() {
		return items;
	}

	/**
	 * @param items
	 *            The items to set.
	 */
	public void setItems(ArrayList<DisplayItemBean> items) {
		this.items = items;
	}

	/**
	 * @return Returns the section.
	 */
	public SectionBean getSection() {
		return section;
	}

	/**
	 * @param section
	 *            The section to set.
	 */
	public void setSection(SectionBean section) {
		this.section = section;
	}

	/**
	 * @return Returns the eventDefinitionCRF.
	 */
	public EventDefinitionCRFBean getEventDefinitionCRF() {
		return eventDefinitionCRF;
	}

	/**
	 * @param eventDefinitionCRF
	 *            The eventDefinitionCRF to set.
	 */
	public void setEventDefinitionCRF(EventDefinitionCRFBean eventDefinitionCRF) {
		this.eventDefinitionCRF = eventDefinitionCRF;
	}

	/**
	 * @return Returns the checkInputs.
	 */
	public boolean isCheckInputs() {
		return checkInputs;
	}

	// /**
	// * @param checkInputs The checkInputs to set.
	// */
	// public void setCheckInputs(boolean checkInputs) {
	// this.checkInputs = checkInputs;
	// }

	/**
	 * @return Returns the firstSection.
	 */
	public boolean isFirstSection() {
		return firstSection;
	}

	/**
	 * @param firstSection
	 *            The firstSection to set.
	 */
	public void setFirstSection(boolean firstSection) {
		this.firstSection = firstSection;
	}

	/**
	 * @return Returns the lastSection.
	 */
	public boolean isLastSection() {
		return lastSection;
	}

	/**
	 * @param lastSection
	 *            The lastSection to set.
	 */
	public void setLastSection(boolean lastSection) {
		this.lastSection = lastSection;
	}

	/**
	 * @return the ruleEditCheck
	 */
	public Integer getRuleEditCheck() {
		return ruleEditCheck;
	}

	/**
	 * @param ruleEditCheck
	 *            the ruleEditCheck to set
	 */
	public void setRuleEditCheck(Integer ruleEditCheck) {
		this.ruleEditCheck = ruleEditCheck;
	}

	public Set<Integer> getShowSCDItemIds() {
		return showSCDItemIds;
	}

	public void setShowSCDItemIds(Set<Integer> showSCDItemIds) {
		this.showSCDItemIds = showSCDItemIds;
	}

	/**
	 * Get clone of the object.
	 * @return DisplaySectionBean
	 */
	public DisplaySectionBean cloneWithoutDisplayItemGroups() {
		DisplaySectionBean newSection = new DisplaySectionBean();
		newSection.setCrf(crf);
		newSection.setCrfVersion(crfVersion);
		newSection.setEventCRF(eventCRF);
		newSection.setEventDefinitionCRF(eventDefinitionCRF);
		newSection.setSection(section);
		newSection.setItems(items);
		newSection.setFirstSection(firstSection);
		newSection.setLastSection(lastSection);
		newSection.setRuleEditCheck(ruleEditCheck);
		newSection.setShowSCDItemIds(showSCDItemIds);
		newSection.setDisplayFormGroups(displayFormGroups);
		return newSection;
	}

	public EventCRFSectionBean getEventCRFSection() {
		return eventCRFSection;
	}

	public void setEventCRFSection(EventCRFSectionBean eventCRFSection) {
		this.eventCRFSection = eventCRFSection;
	}
}
