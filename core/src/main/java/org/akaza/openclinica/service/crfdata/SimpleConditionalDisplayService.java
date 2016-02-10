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
 * copyright 2003-2010 Akaza Research
 */
package org.akaza.openclinica.service.crfdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.dao.hibernate.SCDItemMetadataDao;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.domain.crfdata.SCDItemMetadataBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For simple conditional display
 * 
 * @author ywang
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SimpleConditionalDisplayService {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	DataSource dataSource;
	SCDItemMetadataDao scdItemMetadataDao;

	public SimpleConditionalDisplayService(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Method converts SectionBean list to DisplaySectionBean list with scd info.
	 * 
	 * @param sections
	 *            List<SectionBean
	 * @param versionMap
	 *            HashMap
	 * @return List<DisplaySectionBean>
	 */
	public List<DisplaySectionBean> convertSectionBeansToDisplaySectionBeansWithSCDInfo(List<SectionBean> sections,
			HashMap versionMap) {
		List<DisplaySectionBean> displaySectionBeanList = new ArrayList<DisplaySectionBean>();
		for (SectionBean section : sections) {
			ArrayList<DisplayItemBean> displayItemBeanList = new ArrayList<DisplayItemBean>();
			DisplaySectionBean displaySectionBean = new DisplaySectionBean();
			for (ItemBean itemBean : (ArrayList<ItemBean>) versionMap.get(new Integer(section.getId()))) {
				DisplayItemBean displayItemBean = new DisplayItemBean();
				displayItemBean.setItem(itemBean);
				displayItemBean.setMetadata(itemBean.getItemMeta());
				displayItemBeanList.add(displayItemBean);
			}
			displaySectionBean.setItems(displayItemBeanList);
			displaySectionBean.setSection(section);
			initConditionalDisplays(displaySectionBean);
			displaySectionBeanList.add(displaySectionBean);
		}
		return displaySectionBeanList;
	}

	/**
	 * Initialize ItemFormMetadataBean in DisplaySectionBean for simple conditional display
	 * 
	 * @param displaySection
	 * @return
	 */
	public DisplaySectionBean initConditionalDisplays(DisplaySectionBean displaySection) {
		int sectionId = displaySection.getSection().getId();
		Set<Integer> showSCDItemIds = displaySection.getShowSCDItemIds();
		ArrayList<SCDItemMetadataBean> cds = scdItemMetadataDao.findAllBySectionId(sectionId);
		if (cds == null) {
			logger.info("SCDItemMetadataDao.findAllBySectionId with sectionId=" + sectionId + " returned null.");
		} else if (cds.size() > 0) {
			ArrayList<DisplayItemBean> displayItems = initSCDItems(displaySection.getItems(), cds, showSCDItemIds);
			HashMap<Integer, ArrayList<SCDItemMetadataBean>> scdPairMap = getControlMetaIdAndSCDSetMap(sectionId, cds);
			if (scdPairMap == null) {
				logger.info("SimpleConditionalDisplayService.getControlMetaIdAndSCDSetMap returned null.");
			} else {
				for (DisplayItemBean displayItem : displayItems) {
					if (scdPairMap.containsKey(displayItem.getMetadata().getId())) {
						// displayItem is control item
						ArrayList<SCDItemMetadataBean> sets = scdPairMap.get(displayItem.getMetadata().getId());
						displayItem.getScdData().setScdSetsForControl(sets);
						for (SCDItemMetadataBean scd : sets) {
							if (initConditionalDisplayToBeShown(displayItem, scd)) {
								showSCDItemIds.add(scd.getScdItemId());
							}
						}
					}
					// control item is ahead of its scd item(s)
					if (displayItem.getScdData().getScdItemMetadataBean().getScdItemFormMetadataId() > 0) {
						// displayItem is scd item
						displayItem.setIsSCDtoBeShown(showSCDItemIds.contains(displayItem.getMetadata().getItemId()));
					}

					if (displayItem.getChildren().size() > 0) {
						ArrayList<DisplayItemBean> cs = displayItem.getChildren();
						for (DisplayItemBean c : cs) {
							if (scdPairMap.containsKey(c.getMetadata().getId())) {
								// c is control item
								ArrayList<SCDItemMetadataBean> sets = scdPairMap.get(c.getMetadata().getId());
								c.getScdData().setScdSetsForControl(sets);
								for (SCDItemMetadataBean scd : sets) {
									if (initConditionalDisplayToBeShown(c, scd)) {
										showSCDItemIds.add(scd.getScdItemId());
									}
								}
							}
							// control item is ahead of its scd item(s)
							if (c.getScdData().getScdItemMetadataBean().getScdItemFormMetadataId() > 0) {
								// c is scd item
								c.setIsSCDtoBeShown(showSCDItemIds.contains(c.getMetadata().getItemId()));
							}
						}
					}
				}
			}
		}
		return displaySection;
	}

	public ArrayList<DisplayItemBean> initSCDItems(ArrayList<DisplayItemBean> displayItems,
			ArrayList<SCDItemMetadataBean> cds, Set<Integer> showSCDItemIds) {
		ArrayList<DisplayItemBean> dis = displayItems;
		HashMap<Integer, SCDItemMetadataBean> scds = (HashMap<Integer, SCDItemMetadataBean>) this
				.getSCDMetaIdAndSCDSetMap(cds);
		for (DisplayItemBean displayItem : dis) {
			ItemFormMetadataBean meta = displayItem.getMetadata();
			if (scds.containsKey(meta.getId())) {
				SCDItemMetadataBean scdItemMetadataBean = scds.get(meta.getId());
				scdItemMetadataBean.setScdItemId(meta.getItemId());
				displayItem.getScdData().setScdItemMetadataBean(scdItemMetadataBean);
			}
			if (meta.getParentId() < 1) {
				ArrayList<DisplayItemBean> cs = displayItem.getChildren();
				for (DisplayItemBean c : cs) {
					ItemFormMetadataBean cmeta = c.getMetadata();
					if (scds.containsKey(cmeta.getId())) {
						SCDItemMetadataBean scdItemMetadataBean = scds.get(cmeta.getId());
						scdItemMetadataBean.setScdItemId(cmeta.getItemId());
						c.getScdData().setScdItemMetadataBean(scdItemMetadataBean);
					}
				}
			}
		}
		return dis;
	}

	public HashMap<Integer, ArrayList<SCDItemMetadataBean>> getControlMetaIdAndSCDSetMap(int sectionId,
			ArrayList<SCDItemMetadataBean> scdSets) {
		HashMap<Integer, ArrayList<SCDItemMetadataBean>> cdPairMap = new HashMap<Integer, ArrayList<SCDItemMetadataBean>>();
		if (scdSets == null) {
			logger.info("SimpleConditionalDisplayService.getControlMetaIdAndSCDSetMap() ArrayList<SCDItemMetadataBean> parameter is null.");
		} else {
			for (SCDItemMetadataBean scd : scdSets) {
				Integer conId = scd.getControlItemFormMetadataId();
				ArrayList<SCDItemMetadataBean> conScds = cdPairMap.containsKey(conId)
						? cdPairMap.get(conId)
						: new ArrayList<SCDItemMetadataBean>();
				conScds.add(scd);
				cdPairMap.put(conId, conScds);
			}
		}
		return cdPairMap;
	}

	public Map<Integer, SCDItemMetadataBean> getSCDMetaIdAndSCDSetMap(ArrayList<SCDItemMetadataBean> scdSets) {
		Map<Integer, SCDItemMetadataBean> map = new HashMap<Integer, SCDItemMetadataBean>();
		if (scdSets == null) {
			logger.info("SimpleConditionalDisplayService.getSCDMetaIdAndSCDSetMap() ArrayList<SCDItemMetadataBean> parameter is null.");
		} else {
			for (SCDItemMetadataBean scd : scdSets) {
				map.put(scd.getScdItemFormMetadataId(), scd);
			}
		}
		return map;
	}

	/**
	 * Base on chosen option of a control item. scdItemId has to be initialized for SCDItemMetadataBean.
	 * 
	 * @param dib
	 * @param showSCDItemIds
	 *            .
	 * @return
	 */
	public static Set<Integer> conditionalDisplayToBeShown(DisplayItemBean dib, Set<Integer> showSCDItemIds) {
		Set<Integer> showIds = showSCDItemIds;
		// a conditional display item will be always after its control item.
		ArrayList<SCDItemMetadataBean> cds = dib.getScdData().getScdSetsForControl();
		if (cds.size() > 0) {
			for (SCDItemMetadataBean cd : cds) {
				Integer scdItemId = cd.getScdItemId();
				if (scdItemId > 0) {
					if (conditionalDisplayToBeShown(dib.getData().getValue(), cd)) {
						showIds.add(scdItemId);
					} else if (showIds.contains(scdItemId)) {
						showIds.remove(scdItemId);
					}
				}
			}
		}
		return showIds;
	}

	/**
	 * Return true if a SCDItemMetadataBean has a chosen optionValue
	 * 
	 * @param chosenOption
	 * @param cd
	 * @return
	 */
	public static boolean conditionalDisplayToBeShown(String chosenOption, SCDItemMetadataBean cd) {
		if (chosenOption != null && chosenOption.length() > 0) {
			chosenOption = chosenOption.replaceAll("\\\\,", "##");
			if (chosenOption.contains(",")) {
				String[] ss = chosenOption.split(",");
				for (int i = 0; i < ss.length; ++i) {
					if (ss[i].replaceAll("##", "\\\\,").trim().equalsIgnoreCase(cd.getOptionValue())) {
						return true;
					}
				}
			} else {
				chosenOption = chosenOption.replaceAll("##", "\\,");
				if (chosenOption.equalsIgnoreCase(cd.getOptionValue())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return true, if a SCDItemMetadataBean to show initially.
	 * 
	 * @param controlItem DisplayItemBean
	 * @param cd SCDItemMetadataBean
	 * @return boolean
	 */
	private boolean initConditionalDisplayToBeShown(DisplayItemBean controlItem, SCDItemMetadataBean cd) {

		if (controlItem.getData().isActive() && notStartedDoubleDataEntryStage(controlItem)) {
			if (scdOptionValueSelected(controlItem, cd, controlItem.getData().getValue())) {
				return true;
			}
		} else {
			if (scdOptionValueSelected(controlItem, cd, controlItem.getMetadata().getDefaultValue())) {
				return true;
			} else if (scdOptionValueSelected(controlItem, cd, getDefaultValue(controlItem))) {
				return true;
			} else {
				if (ResponseType.SELECT.equals(controlItem.getMetadata().getResponseSet().getResponseType())) {
					String firstOptionValue = ((ResponseOptionBean) controlItem.getMetadata().getResponseSet()
							.getOptions().get(0)).getValue();
					if (scdOptionValueSelected(controlItem, cd, firstOptionValue)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean scdOptionValueSelected(DisplayItemBean controlItem, SCDItemMetadataBean cd,
			String selectedOptionValue) {

		if (selectedOptionValue != null && selectedOptionValue.length() > 0) {
			ResponseType ctrlResponseType = controlItem.getMetadata().getResponseSet().getResponseType();
			if (ResponseType.CHECKBOX.equals(ctrlResponseType) || ResponseType.SELECTMULTI.equals(ctrlResponseType)) {
				String[] value = selectedOptionValue.split(",");
				for (String option : value) {
					if (option.equals(cd.getOptionValue())) {
						return true;
					}
				}
			} else if (selectedOptionValue.equals(cd.getOptionValue())) {
				return true;
			}
		}
		return false;
	}

	private String getDefaultValue(DisplayItemBean controlItem) {

		String defaultValue = "";
		if (!StringUtils.isEmpty(controlItem.getMetadata().getDefaultValue())) {

			List<String> optionTexts = Arrays.asList(controlItem.getMetadata().getDefaultValue().split(","));
			List<ResponseOptionBean> responseOptions = controlItem.getMetadata().getResponseSet().getOptions();
			for (String text: optionTexts) {
				for (ResponseOptionBean response: responseOptions) {
					if (text.equals(response.getText())) {
						defaultValue += response.getValue() + ",";
						break;
					}
				}
			}
			defaultValue = defaultValue.substring(0, defaultValue.length() - 1);
		}
		return defaultValue;
	}

	private boolean notStartedDoubleDataEntryStage(DisplayItemBean controlItem) {

		EventCRFDAO eventCRFDAO = new EventCRFDAO(getDataSource());
		int eventCRFID = controlItem.getData().getEventCRFId();
		EventCRFBean eventCRF = (EventCRFBean) eventCRFDAO.findByPK(eventCRFID);
		EventDefinitionCRFBean eventDefCRF = controlItem.getEventDefinitionCRF();
		return !(eventDefCRF.isDoubleEntry() && DataEntryStage.INITIAL_DATA_ENTRY_COMPLETE.equals(eventCRF.getStage()));
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public SCDItemMetadataDao getScdItemMetadataDao() {
		return scdItemMetadataDao;
	}

	public void setScdItemMetadataDao(SCDItemMetadataDao scdItemMetadataDao) {
		this.scdItemMetadataDao = scdItemMetadataDao;
	}

}
