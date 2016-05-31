/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * <p/>
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 * <p/>
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * <p/>
 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.lib.crf.validator;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.control.form.spreadsheet.OnChangeSheetValidationCell;
import org.akaza.openclinica.control.form.spreadsheet.OnChangeSheetValidationType;
import org.akaza.openclinica.control.form.spreadsheet.SheetCell;
import org.akaza.openclinica.control.form.spreadsheet.SheetValidationType;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.util.ItemGroupCrvVersionUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.logic.score.ScoreValidator;

import com.clinovo.lib.crf.bean.ItemBeanExt;
import com.clinovo.lib.crf.builder.impl.BaseCrfBuilder;
import com.clinovo.lib.crf.enums.OperationType;
import com.clinovo.lib.crf.enums.RealValueKey;
import com.clinovo.util.CodingFieldsUtil;

/**
 * CrfValidator.
 */
public final class CommonValidator {

	public static final int INT_5 = 5;
	public static final int INT_7 = 7;
	public static final int INT_16 = 16;
	public static final int INT_19 = 19;
	public static final int INT_64 = 64;
	public static final int INT_240 = 240;
	public static final int INT_255 = 255;
	public static final int INT_4000 = 4000;
	public static final int INT_2000 = 2000;
	public static final int INT_10000 = 10000;
	private static final int MAX_ITEM_NAME_LENGTH = 255;
	private static final int MAX_SECTION_NAME_LENGTH = 2000;

	public static final String ONE = "1";
	public static final String NO = "no";
	public static final String ZERO = "0";
	public static final String YES = "yes";
	public static final String TRUE = "true";
	public static final String FILE = "file";
	public static final String FUNC = "func";
	public static final String FALSE = "false";
	public static final String UNGROUPED = "Ungrouped";

	private CommonValidator() {
	}

	private static void validateCrf(BaseCrfBuilder crfBuilder) throws Exception {
		CRFDAO crfDao = new CRFDAO(crfBuilder.getDataSource());
		CRFVersionDAO crfVersionDao = new CRFVersionDAO(crfBuilder.getDataSource());
		if (StringUtil.isBlank(crfBuilder.getCrfBean().getName())) {
			crfBuilder.getErrorMessageProducer().crfNameIsBlank();
		}
		if (crfBuilder.getCrfBean().getName().length() > INT_255) {
			crfBuilder.getErrorMessageProducer().crfNameLengthIsExceeded();
		}
		if (crfBuilder.getOperationType() == OperationType.IMPORT_NEW_CRF) {
			CRFBean crfBean = (CRFBean) crfDao.findByNameAndStudy(crfBuilder.getCrfBean().getName(),
					crfBuilder.getStudyBean());
			if (crfBean.getId() > 0) {
				crfBuilder.getErrorMessageProducer().crfNameHasAlreadyBeenUsed();
			}
		}
		if (crfBuilder.getOperationType() == OperationType.IMPORT_NEW_CRF_VERSION) {
			CRFBean crfBean = (CRFBean) crfDao.findByPK(crfBuilder.getCrfBean().getId());
			if (!crfBean.getName().equalsIgnoreCase(crfBuilder.getCrfBean().getName())) {
				crfBuilder.getErrorMessageProducer().didNotMatchCrfName(crfBean.getName());
			} else {
				CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByFullNameAndStudy(
						crfBuilder.getCrfVersionBean().getName(), crfBuilder.getCrfBean().getName(),
						crfBuilder.getStudyBean());
				if (crfVersionBean.getId() > 0) {
					crfBuilder.getErrorMessageProducer().crfVersionHasAlreadyBeenUsed();
				}
			}
		}
		if (StringUtil.isBlank(crfBuilder.getCrfVersionBean().getName())) {
			crfBuilder.getErrorMessageProducer().crfVersionIsBlank();
		}
		if (crfBuilder.getCrfVersionBean().getName() != null
				&& crfBuilder.getCrfVersionBean().getName().length() > INT_255) {
			crfBuilder.getErrorMessageProducer().crfVersionLengthIsExceeded();
		}
		if (crfBuilder.getCrfVersionBean().getDescription() != null
				&& crfBuilder.getCrfVersionBean().getDescription().length() > INT_4000) {
			crfBuilder.getErrorMessageProducer().crfVersionDescriptionLengthIsExceeded();
		}
		if (StringUtil.isBlank(crfBuilder.getCrfVersionBean().getRevisionNotes())) {
			crfBuilder.getErrorMessageProducer().crfRevisionNotesIsBlank();
		}
		if (crfBuilder.getCrfVersionBean().getRevisionNotes() != null
				&& crfBuilder.getCrfVersionBean().getRevisionNotes().length() > INT_255) {
			crfBuilder.getErrorMessageProducer().crfRevisionNotesLengthIsExceeded();
		}
	}

	private static void validateSection(BaseCrfBuilder crfBuilder) throws Exception {
		// validate section label
		SectionBean sectionBean = crfBuilder.getCurrentSection();
		if (StringUtil.isBlank(sectionBean.getLabel())) {
			crfBuilder.getErrorMessageProducer().sectionLabelIsBlank();
		}
		if (sectionBean.getLabel() != null && sectionBean.getLabel().length() > INT_2000) {
			crfBuilder.getErrorMessageProducer().sectionLabelLengthIsExceeded();
		}
		if (crfBuilder.getSectionNames().contains(sectionBean.getLabel())) {
			crfBuilder.getErrorMessageProducer().sectionLabelIsDuplicated();
		} else {
			crfBuilder.getSectionNames().add(sectionBean.getLabel());
		}
		// validate section title
		if (StringUtil.isBlank(sectionBean.getTitle())) {
			crfBuilder.getErrorMessageProducer().sectionTitleIsBlank();
		}
		if (sectionBean.getTitle() != null && sectionBean.getTitle().length() > INT_2000) {
			crfBuilder.getErrorMessageProducer().sectionTitleLengthIsExceeded();
		}
		// validate section instructions
		if (sectionBean.getInstructions() != null && sectionBean.getInstructions().length() > INT_10000) {
			crfBuilder.getErrorMessageProducer().sectionInstructionsLengthIsExceeded();
		}
		// validate section page number
		if (sectionBean.getPageNumberLabel() != null && sectionBean.getPageNumberLabel().length() > INT_5) {
			crfBuilder.getErrorMessageProducer().sectionPageNumberLengthIsExceeded();
		}
	}

	private static void validateGroup(BaseCrfBuilder crfBuilder) throws Exception {
		ItemGroupBean itemGroupBean = crfBuilder.getCurrentItemGroup();
		// validate group label
		if (StringUtil.isBlank(itemGroupBean.getName())) {
			crfBuilder.getErrorMessageProducer().groupLabelIsBlank();
		}
		if (itemGroupBean.getName() != null && itemGroupBean.getName().length() > INT_255) {
			crfBuilder.getErrorMessageProducer().groupLabelLengthIsExceeded();
		}
		if (itemGroupBean != crfBuilder.getDefaultItemGroupBean()) {
			crfBuilder.getSheetContainer().getRepeatingGroupLabels().add(itemGroupBean.getName());
			if (crfBuilder.getGroupNames().contains(itemGroupBean.getName())) {
				crfBuilder.getErrorMessageProducer().groupLabelIsDuplicated();
			} else {
				crfBuilder.getGroupNames().add(itemGroupBean.getName());
			}
		}
		// validate group header
		if (itemGroupBean.getMeta().getHeader() != null && itemGroupBean.getMeta().getHeader().length() > INT_255) {
			crfBuilder.getErrorMessageProducer().groupHeaderLengthIsExceeded();

		}
		// validate repeat num
		if (itemGroupBean.getMeta().getRepeatNum() < 1) {
			crfBuilder.getErrorMessageProducer().repeatNumIsWrong();
		}
		// validate repeat max
		if (itemGroupBean.getMeta().getRepeatMax() < 1) {
			crfBuilder.getErrorMessageProducer().repeatMaxIsWrong();
		}
	}

	private static void validateResponseSet(BaseCrfBuilder crfBuilder) throws Exception {
		// validate response set type
		if (StringUtil.isBlank(crfBuilder.getCurrentItem().getRealValue(RealValueKey.RESPONSE_TYPE))) {
			crfBuilder.getErrorMessageProducer().responseTypeIsBlank();
		} else {
			if ((!ResponseType.findByName(crfBuilder.getCurrentItem().getRealValue(RealValueKey.RESPONSE_TYPE)))) {
				crfBuilder.getErrorMessageProducer().responseTypeIsNotValid();
			}
			if (crfBuilder.getCurrentItem().getResponseSet().getResponseType().getResponseTypeId() == ResponseType.RADIO
					.getId() && !StringUtil.isBlank(crfBuilder.getCurrentItem().getItemMeta().getDefaultValue())) {
				crfBuilder.getErrorMessageProducer().hasRadioWithDefault();
			}
		}
		if (crfBuilder.getCurrentItem().getResponseSet().getResponseType().getResponseTypeId() != ResponseType.TEXT
				.getId()
				&& crfBuilder.getCurrentItem().getResponseSet().getResponseType()
				.getResponseTypeId() != ResponseType.TEXTAREA.getId()
				&& crfBuilder.getCurrentItem().getResponseSet().getResponseType()
				.getResponseTypeId() != ResponseType.INSTANT_CALCULATION.getId()
				&& crfBuilder.getCurrentItem().getResponseSet().getResponseType()
				.getResponseTypeId() != ResponseType.GROUP_CALCULATION.getId()
				&& crfBuilder.getCurrentItem().getResponseSet().getResponseType()
				.getResponseTypeId() != ResponseType.CALCULATION.getId()
				&& crfBuilder.getCurrentItem().getResponseSet().getResponseType()
				.getResponseTypeId() != ResponseType.FILE.getId()) {
			// set the stored options text for the same label & response type if it's is empty
			if (StringUtil.isBlank(crfBuilder.getCurrentItem().getResponseSet().getOptionsText())
					&& crfBuilder.getOptionsTextMap().keySet()
					.contains(crfBuilder.getCurrentItem().getResponseSet().getLabel().toLowerCase() + "_"
							+ crfBuilder.getCurrentItem().getRealValue(RealValueKey.RESPONSE_TYPE))) {
				crfBuilder.getCurrentItem().getResponseSet()
						.setOptionsText(crfBuilder.getOptionsTextMap()
								.get(crfBuilder.getCurrentItem().getResponseSet().getLabel().toLowerCase() + "_"
										+ crfBuilder.getCurrentItem().getRealValue(RealValueKey.RESPONSE_TYPE)));
			}
			// set the stored options values for the same label & response type if it's is empty
			if (StringUtil.isBlank(crfBuilder.getCurrentItem().getResponseSet().getOptionsValues())
					&& crfBuilder.getOptionsValuesMap().keySet()
					.contains(crfBuilder.getCurrentItem().getResponseSet().getLabel().toLowerCase() + "_"
							+ crfBuilder.getCurrentItem().getRealValue(RealValueKey.RESPONSE_TYPE))) {
				crfBuilder.getCurrentItem().getResponseSet()
						.setOptionsValues(crfBuilder.getOptionsValuesMap()
								.get(crfBuilder.getCurrentItem().getResponseSet().getLabel().toLowerCase() + "_"
										+ crfBuilder.getCurrentItem().getRealValue(RealValueKey.RESPONSE_TYPE)));
			}
			// validate response label
			if (StringUtil.isBlank(crfBuilder.getCurrentItem().getResponseSet().getLabel())) {
				crfBuilder.getErrorMessageProducer().responseLabelIsBlank();
			}
			// validate response options text
			int numberOfOptions = 0;
			if (!crfBuilder.getResNames().contains(crfBuilder.getCurrentItem().getResponseSet().getLabel())
					&& StringUtil.isBlank(crfBuilder.getCurrentItem().getResponseSet().getOptionsText())) {
				crfBuilder.getErrorMessageProducer().responseOptionsTextIsBlank();
			}
			String[] resArray = crfBuilder.split(crfBuilder.getCurrentItem().getResponseSet().getOptionsText(), ",");
			if (!crfBuilder.getResNames().contains(crfBuilder.getCurrentItem().getResponseSet().getLabel())
					&& !StringUtil.isBlank(crfBuilder.getCurrentItem().getResponseSet().getOptionsText())) {
				numberOfOptions = resArray.length;
			}
			if (crfBuilder.getLabelWithOptionsText()
					.containsKey(crfBuilder.getCurrentItem().getResponseSet().getLabel())) {
				String[] mapArray = crfBuilder.getLabelWithOptionsText()
						.get(crfBuilder.getCurrentItem().getResponseSet().getLabel());
				if (!StringUtil.isBlank(crfBuilder.getCurrentItem().getResponseSet().getOptionsText())) {
					if (resArray.length != mapArray.length) {
						crfBuilder.getErrorMessageProducer().itemHasDifferentNumberOfOptionsText();
					} else {
						for (int i = 0; i < resArray.length; i++) {
							if (!resArray[i].equals(mapArray[i])) {
								crfBuilder.getErrorMessageProducer().itemHasDifferentValuesForOptionsText();
								break;
							}
						}
					}
				}
			} else {
				crfBuilder.getLabelWithOptionsText().put(crfBuilder.getCurrentItem().getResponseSet().getLabel(),
						resArray);
			}
			// validate response options values
			if (!crfBuilder.getResNames().contains(crfBuilder.getCurrentItem().getResponseSet().getLabel())
					&& StringUtil.isBlank(crfBuilder.getCurrentItem().getResponseSet().getOptionsValues())) {
				crfBuilder.getErrorMessageProducer().responseOptionsValuesIsBlank();
			}
			resArray = crfBuilder.split(crfBuilder.getCurrentItem().getResponseSet().getOptionsValues(), ",");
			if (crfBuilder.getLabelWithOptionsValues()
					.containsKey(crfBuilder.getCurrentItem().getResponseSet().getLabel())) {
				String[] mapArray = crfBuilder.getLabelWithOptionsValues()
						.get(crfBuilder.getCurrentItem().getResponseSet().getLabel());
				if (!StringUtil.isBlank(crfBuilder.getCurrentItem().getResponseSet().getOptionsValues())) {
					if (resArray.length != mapArray.length) {
						crfBuilder.getErrorMessageProducer().itemHasDifferentNumberOfOptionsValues();
					} else {
						for (int i = 0; i < resArray.length; i++) {
							if (!resArray[i].equals(mapArray[i])) {
								crfBuilder.getErrorMessageProducer().itemHasDifferentValuesForOptionsValues();
								break;
							}
						}
					}
				}
				crfBuilder.getControlValues().put(crfBuilder.getCurrentItem().getItemMeta().getSectionName() + "---"
						+ crfBuilder.getCurrentItem().getName(), mapArray);
			} else {
				crfBuilder.getLabelWithOptionsValues().put(crfBuilder.getCurrentItem().getResponseSet().getLabel(),
						resArray);
				crfBuilder.getControlValues().put(crfBuilder.getCurrentItem().getItemMeta().getSectionName() + "---"
						+ crfBuilder.getCurrentItem().getName(), resArray);
			}
			if (numberOfOptions > 0 && crfBuilder.split(crfBuilder.getCurrentItem().getResponseSet().getOptionsValues(),
					",").length != numberOfOptions) {
				crfBuilder.getErrorMessageProducer().itemHasIncompleteOptionValuePair();
			}
			if (!crfBuilder.getResPairs().contains(crfBuilder.getCurrentItem().getResponseSet().getLabel().toLowerCase()
					+ "_" + crfBuilder.getCurrentItem().getRealValue(RealValueKey.RESPONSE_TYPE))) {
				if (!crfBuilder.getResNames()
						.contains(crfBuilder.getCurrentItem().getResponseSet().getLabel().toLowerCase())) {
					crfBuilder.getResNames().add(crfBuilder.getCurrentItem().getResponseSet().getLabel().toLowerCase());
					crfBuilder.getOptionsTextMap()
							.put(crfBuilder.getCurrentItem().getResponseSet().getLabel().toLowerCase() + "_"
											+ crfBuilder.getCurrentItem().getRealValue(RealValueKey.RESPONSE_TYPE),
									crfBuilder.getCurrentItem().getResponseSet().getOptionsText());
					crfBuilder.getOptionsValuesMap()
							.put(crfBuilder.getCurrentItem().getResponseSet().getLabel().toLowerCase() + "_"
											+ crfBuilder.getCurrentItem().getRealValue(RealValueKey.RESPONSE_TYPE),
									crfBuilder.getCurrentItem().getResponseSet().getOptionsValues());
				} else {
					crfBuilder.getErrorMessageProducer().responseLabelHasBeenUsedForAnotherResponseType();
				}
				crfBuilder.getResPairs().add(crfBuilder.getCurrentItem().getResponseSet().getLabel().toLowerCase() + "_"
						+ crfBuilder.getCurrentItem().getRealValue(RealValueKey.RESPONSE_TYPE));
			}
		}
	}

	private static void validateExpression(BaseCrfBuilder crfBuilder) throws Exception {
		// validate expression
		if (crfBuilder.getCurrentItem().getResponseSet().getResponseType()
				.getResponseTypeId() == ResponseType.CALCULATION.getId()
				|| crfBuilder.getCurrentItem().getResponseSet().getResponseType()
				.getResponseTypeId() == ResponseType.GROUP_CALCULATION.getId()) {
			if (crfBuilder.getCurrentItem().getResponseSet().getOptionsValues().contains(":")) {
				String[] s = crfBuilder.getCurrentItem().getResponseSet().getOptionsValues().split(":");
				if (s.length > 0 && !FUNC.equalsIgnoreCase(s[0].trim())) {
					crfBuilder.getErrorMessageProducer().expressionDoesNotStartWithFunc();
				}
			}
			String exp = crfBuilder.getCurrentItem().getResponseSet().getOptionsValues();
			crfBuilder.setCurrentScoreValidatorErrorsBuffer(new StringBuffer());
			ArrayList<String> variables = new ArrayList<String>();
			ScoreValidator scoreValidator = new ScoreValidator(crfBuilder.getLocale());
			if (!scoreValidator.isValidExpression(exp.replace("\\\\,", ","),
					crfBuilder.getCurrentScoreValidatorErrorsBuffer(), variables)) {
				crfBuilder.getErrorMessageProducer().expressionIsNotValid();
			}
			String groupLabel = crfBuilder.getCurrentItem().getItemMeta().getGroupLabel();
			for (String variable : variables) {
				crfBuilder.setCurrentMessage(new StringBuffer(variable));
				if (!crfBuilder.getSheetContainer().getAllItems().containsKey(variable)) {
					crfBuilder.getErrorMessageProducer().itemMustBeListedBeforeAnotherItem();
				} else {
					if (crfBuilder.getCurrentItem().getResponseSet().getResponseType()
							.getResponseTypeId() == ResponseType.CALCULATION.getId()
							&& !crfBuilder.getSheetContainer().getAllItems().get(variable)
							.equalsIgnoreCase(groupLabel)) {
						crfBuilder.getErrorMessageProducer().itemsMustHaveTheSameGroup();
					} else if (crfBuilder.getCurrentItem().getResponseSet().getResponseType()
							.getResponseTypeId() == ResponseType.GROUP_CALCULATION.getId()) {
						String g = crfBuilder.getSheetContainer().getAllItems().get(variable);
						if (!g.equalsIgnoreCase(UNGROUPED) && g.equalsIgnoreCase(groupLabel)) {
							crfBuilder.getErrorMessageProducer().itemsShouldNotHaveTheSameGroup();
						}
					}
				}
			}
		} else if (crfBuilder.getCurrentItem().getResponseSet().getResponseType()
				.getResponseTypeId() == ResponseType.INSTANT_CALCULATION.getId()) {
			int row = crfBuilder.getCurrentItem().getRowNumber();
			int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
			OnChangeSheetValidationCell onChangeCell = new OnChangeSheetValidationCell(OnChangeSheetValidationType.ALL,
					new SheetCell.Builder().rowName(crfBuilder.getCurrentItem().getName())
							.colTitle("RESPONSE_VALUES_column")
							.colValue(crfBuilder.getCurrentItem().getResponseSet().getOptionsValues())
							.forWhich("instant_calculation").sheetNum(sheetNumber).rowNum(row).colNum(INT_16).build());
			crfBuilder.getInstantValidator().addValidationCells(onChangeCell);
		}
	}

	private static void validateDataType(BaseCrfBuilder crfBuilder) throws Exception {
		// valdiate data type
		if (StringUtil.isBlank(crfBuilder.getCurrentItem().getRealValue(RealValueKey.ITEM_DATA_TYPE))) {
			crfBuilder.getErrorMessageProducer().itemDataTypeIsBlank();
		} else {
			if (!ItemDataType.findByName(crfBuilder.getCurrentItem().getRealValue(RealValueKey.ITEM_DATA_TYPE))) {
				crfBuilder.getErrorMessageProducer().itemDataTypeIsNotValid();
			}
			if (crfBuilder.getCurrentItem().getResponseSet().getResponseType().getResponseTypeId() == ResponseType.FILE
					.getId() && crfBuilder.getCurrentItem().getItemDataTypeId() != ItemDataType.FILE.getId()) {
				crfBuilder.getErrorMessageProducer().itemDataTypeShouldBeFile();
			} else if (crfBuilder.getCurrentItem().getResponseSet().getResponseType()
					.getResponseTypeId() == ResponseType.INSTANT_CALCULATION.getId()) {
				int row = crfBuilder.getCurrentItem().getRowNumber();
				int sheetNumber = crfBuilder.getCurrentItem().getSheetNumber();
				OnChangeSheetValidationCell onChangeCell = new OnChangeSheetValidationCell(
						OnChangeSheetValidationType.NONE, SheetValidationType.SHOULD_BE_ST,
						new SheetCell.Builder().rowName(crfBuilder.getCurrentItem().getName())
								.colTitle("DATA_TYPE_column")
								.colValue(crfBuilder.getCurrentItem().getRealValue(RealValueKey.ITEM_DATA_TYPE))
								.forWhich("instant_calculation").sheetNum(sheetNumber).rowNum(row).colNum(INT_19)
								.build());
				crfBuilder.getInstantValidator().addValidationCells(onChangeCell);
			}
		}
	}

	private static void validateResponseOptionsValuesForMatchingDataType(BaseCrfBuilder crfBuilder) throws Exception {
		if (crfBuilder.getCurrentItem().getResponseSet().getResponseType().getResponseTypeId() == ResponseType.CHECKBOX
				.getId()
				|| crfBuilder.getCurrentItem().getResponseSet().getResponseType()
				.getResponseTypeId() == ResponseType.RADIO.getId()
				|| crfBuilder.getCurrentItem().getResponseSet().getResponseType()
				.getResponseTypeId() == ResponseType.SELECT.getId()
				|| crfBuilder.getCurrentItem().getResponseSet().getResponseType()
				.getResponseTypeId() == ResponseType.SELECTMULTI.getId()) {
			if (crfBuilder.getLabelWithType().containsKey(crfBuilder.getCurrentItem().getResponseSet().getLabel())) {
				// make sure same responseLabels have same data type
				if (!crfBuilder.getCurrentItem().getRealValue(RealValueKey.ITEM_DATA_TYPE).equalsIgnoreCase(
						crfBuilder.getLabelWithType().get(crfBuilder.getCurrentItem().getResponseSet().getLabel()))) {
					crfBuilder.getErrorMessageProducer().doesNotMatchDataTypeOfItemWithSameResponseLabel();
				}
			} else {
				crfBuilder.getLabelWithType().put(crfBuilder.getCurrentItem().getResponseSet().getLabel(),
						crfBuilder.getCurrentItem().getRealValue(RealValueKey.ITEM_DATA_TYPE));
				// make sure response values matching data type
				String[] resValArray = crfBuilder.split(crfBuilder.getCurrentItem().getResponseSet().getOptionsValues(),
						",");
				if (resValArray.length > 0) {
					boolean wrongType = false;
					if (crfBuilder.getCurrentItem().getItemDataTypeId() == ItemDataType.INTEGER.getId()) {
						for (String s : resValArray) {
							String st = s != null && s.length() > 0 ? s.trim() : "";
							if (st.length() > 0) {
								try {
									if (st.contains(".")) {
										wrongType = true;
										break;
									} else {
										Integer.parseInt(st);
									}
								} catch (Exception e) {
									wrongType = true;
								}
							}
						}
						if (wrongType) {
							crfBuilder.getErrorMessageProducer().responseOptionsValuesShouldBeInteger();
						}
					} else if (crfBuilder.getCurrentItem().getItemDataTypeId() == ItemDataType.REAL.getId()) {
						for (String s : resValArray) {
							String st = s != null && s.length() > 0 ? s.trim() : "";
							if (st.length() > 0) {
								try {
									Double.parseDouble(st);
								} catch (Exception e) {
									wrongType = true;
									break;
								}
							}
						}
						if (wrongType) {
							crfBuilder.getErrorMessageProducer().responseOptionsValuesShouldBeReal();
						}
					}
				}
			}
		}
	}

	// verifies section group placement for items.
	private static void verifySectionGroupPlacementForItem(BaseCrfBuilder crfBuilder) {
		String groupLabel = crfBuilder.getCurrentItem().getItemMeta().getGroupLabel();
		if (groupLabel != null && !groupLabel.isEmpty()) {
			// verify that this is repeating group
			ItemGroupBean itemGroup = crfBuilder.getItemGroupLabelMap().get(groupLabel);
			boolean isRepeatingGroup = false;
			if (itemGroup != null) {
				isRepeatingGroup = itemGroup.getMeta().isRepeatingGroup();
			}
			if (isRepeatingGroup) {
				String sectionLabel = crfBuilder.getGroupSectionMap().get(groupLabel);
				if (sectionLabel != null) {
					if (!sectionLabel.equals(crfBuilder.getCurrentItem().getItemMeta().getSectionName())) {
						// items of one group belong to more than one section
						crfBuilder.getErrorMessageProducer().itemOfOneGroupBelongsToMoreThanOneSection();
					}
				} else {
					crfBuilder.getGroupSectionMap().put(groupLabel,
							crfBuilder.getCurrentItem().getItemMeta().getSectionName());
				}
			}
		}
	}

	// verifies unique item placement in groups.
	private static void verifyUniqueItemPlacementInGroups(BaseCrfBuilder crfBuilder) {
		crfBuilder.setCurrentMessage(new StringBuffer());
		for (ItemGroupCrvVersionUtil itemGroupCrvVersion : crfBuilder.getItemGroupCrfRecords()) {
			// we expect no more than one hit
			if (itemGroupCrvVersion.getItemName().equals(crfBuilder.getCurrentItem().getName())
					&& !(crfBuilder.getCurrentItem().getItemMeta().getGroupLabel().isEmpty()
					&& itemGroupCrvVersion.getGroupName().equalsIgnoreCase(UNGROUPED))) {
				if (!crfBuilder.getCurrentItem().getItemMeta().getGroupLabel()
						.equals(itemGroupCrvVersion.getGroupName()) && itemGroupCrvVersion.getCrfVersionStatus() == 1) {
					crfBuilder.getCurrentMessage().append(crfBuilder.getMessage("verifyUniqueItemPlacementInGroups_4"))
							.append(itemGroupCrvVersion.getGroupName());
					crfBuilder.getCurrentMessage().append(crfBuilder.getMessage("verifyUniqueItemPlacementInGroups_5"));
					crfBuilder.getCurrentMessage().append(itemGroupCrvVersion.getCrfVersionName());
					if (crfBuilder.getItemGroupCrfRecords()
							.indexOf(itemGroupCrvVersion) != crfBuilder.getItemGroupCrfRecords().size() - 1) {
						crfBuilder.getCurrentMessage().append("', ");
					}
				}
			}
		}
		if (crfBuilder.getCurrentMessage().length() > 0) {
			crfBuilder.getErrorMessageProducer().notUniqueItemPlacementInGroups();
		}

	}

	private static void validateItem(BaseCrfBuilder crfBuilder) throws Exception {
		crfBuilder.getSheetContainer().getItemSectionNameMap().put(crfBuilder.getCurrentItem().getName(),
				crfBuilder.getCurrentItem().getItemMeta().getSectionName());
		crfBuilder.getSheetContainer().collectRepGrpItemNameMap(crfBuilder.getCurrentItem().getName(),
				crfBuilder.getCurrentItem().getItemMeta().getGroupLabel());
		// validate item name
		if (StringUtil.isBlank(crfBuilder.getCurrentItem().getName())) {
			crfBuilder.getErrorMessageProducer().itemNameIsBlank();
		}
		if (!Utils.isMatchingRegexp(crfBuilder.getCurrentItem().getName(), "\\w+")) {
			crfBuilder.getErrorMessageProducer().itemNameIsNotMatchingRegexp();
		}
		if (crfBuilder.getCurrentItem().getName().length() > MAX_ITEM_NAME_LENGTH) {
			crfBuilder.getErrorMessageProducer().itemNameLengthIsExceeded();
		}
		if (crfBuilder.getItemNames().contains(crfBuilder.getCurrentItem().getName())) {
			crfBuilder.getErrorMessageProducer().itemNameIsDuplicated();
		} else {
			crfBuilder.getItemNames().add(crfBuilder.getCurrentItem().getName());
		}
		// validate item description
		if (crfBuilder.isExcelCrfBuilder() && StringUtil.isBlank(crfBuilder.getCurrentItem().getDescription())) {
			crfBuilder.getErrorMessageProducer().itemDescriptionIsBlank();
		}
		if (crfBuilder.getCurrentItem().getDescription() != null
				&& crfBuilder.getCurrentItem().getDescription().length() > INT_4000) {
			crfBuilder.getErrorMessageProducer().itemDescriptionLengthIsExceeded();
		}
		// validate left item text
		if (crfBuilder.getCurrentItem().getItemMeta().getLeftItemText() != null
				&& crfBuilder.getCurrentItem().getItemMeta().getLeftItemText().length() > INT_4000) {
			crfBuilder.getErrorMessageProducer().itemLeftTextLengthIsExceeded();
		}
		// validate right item text
		if (crfBuilder.getCurrentItem().getItemMeta().getRightItemText() != null
				&& crfBuilder.getCurrentItem().getItemMeta().getRightItemText().length() > INT_2000) {
			crfBuilder.getErrorMessageProducer().itemRightTextLengthIsExceeded();
		}
		// validate item header
		if (crfBuilder.getCurrentItem().getItemMeta().getHeader() != null
				&& crfBuilder.getCurrentItem().getItemMeta().getHeader().length() > INT_2000) {
			crfBuilder.getErrorMessageProducer().itemHeaderLengthIsExceeded();
		}
		// validate item sub header
		if (crfBuilder.getCurrentItem().getItemMeta().getSubHeader() != null
				&& crfBuilder.getCurrentItem().getItemMeta().getSubHeader().length() > INT_240) {
			crfBuilder.getErrorMessageProducer().itemSubHeaderLengthIsExceeded();
		}
		// validate item section label
		if (crfBuilder.getCurrentItem().getItemMeta().getSectionName().trim().isEmpty()) {
			crfBuilder.getErrorMessageProducer().itemSectionLabelIsNotValid();
		} else {
			if (crfBuilder.getCurrentItem().getItemMeta().getSectionName().length() > MAX_SECTION_NAME_LENGTH) {
				crfBuilder.getErrorMessageProducer().itemSectionLabelLengthIsExceeded();
			}
			if (!crfBuilder.getSectionLabelMap().keySet()
					.contains(crfBuilder.getCurrentItem().getItemMeta().getSectionName())) {
				crfBuilder.getErrorMessageProducer().itemSectionLabelIsNotValid();
			}
		}
		// validate item group label
		if (crfBuilder.getCurrentItem().getItemMeta().getGroupLabel().trim().isEmpty()) {
			crfBuilder.getErrorMessageProducer().itemGroupLabelIsNotValid();
		} else {
			if (crfBuilder.getCurrentItem().getItemMeta().getGroupLabel().length() > INT_255) {
				crfBuilder.getErrorMessageProducer().itemGroupLabelLengthIsExceeded();
			}
			if (!crfBuilder.getItemGroupLabelMap().keySet()
					.contains(crfBuilder.getCurrentItem().getItemMeta().getGroupLabel())) {
				crfBuilder.getErrorMessageProducer().itemGroupLabelIsNotValid();
			}
		}
		// validate units
		if (crfBuilder.getCurrentItem().getUnits().length() > INT_64) {
			crfBuilder.getErrorMessageProducer().itemUnitsLengthIsExceeded();
		}
		// validate parent item
		if (!crfBuilder.getCurrentItem().getItemMeta().getParentLabel().isEmpty()) {
			ItemBeanExt parentItemBean = crfBuilder.getItemNameToItemMap()
					.get(crfBuilder.getCurrentItem().getItemMeta().getParentLabel());
			if (parentItemBean == null) {
				crfBuilder.getErrorMessageProducer().itemParentItemIsNotValid();
			}
			if (parentItemBean != null && parentItemBean.getParentItemBean() != null) {
				crfBuilder.getErrorMessageProducer().hasNestedParentItem();
			}
			if (parentItemBean != null
					&& !crfBuilder.getCurrentItem().getItemMeta().getGroupLabel().equalsIgnoreCase(UNGROUPED)) {
				crfBuilder.getErrorMessageProducer().repeatingGroupHasParentItem();
			}
		}
		crfBuilder.getSheetContainer().getAllItems().put(crfBuilder.getCurrentItem().getName(),
				crfBuilder.getCurrentItem().getItemMeta().getGroupLabel());
	}

	private static void validateWidthDecimal(BaseCrfBuilder crfBuilder) throws Exception {
		if (!StringUtil.isBlank(crfBuilder.getCurrentItem().getItemMeta().getWidthDecimal())) {
			if (crfBuilder.getCurrentItem().getResponseSet().getResponseType()
					.getResponseTypeId() == ResponseType.CHECKBOX.getId()
					|| crfBuilder.getCurrentItem().getResponseSet().getResponseType()
					.getResponseTypeId() == ResponseType.RADIO.getId()
					|| crfBuilder.getCurrentItem().getResponseSet().getResponseType()
					.getResponseTypeId() == ResponseType.SELECT.getId()
					|| crfBuilder.getCurrentItem().getResponseSet().getResponseType()
					.getResponseTypeId() == ResponseType.SELECTMULTI.getId()) {
				crfBuilder.getErrorMessageProducer().widthDecimalIsNotAvailable();
			} else {
				boolean isCalc = crfBuilder.getCurrentItem().getResponseSet().getResponseType()
						.getResponseTypeId() == ResponseType.GROUP_CALCULATION.getId()
						|| crfBuilder.getCurrentItem().getResponseSet().getResponseType()
						.getResponseTypeId() == ResponseType.CALCULATION.getId();
				crfBuilder.setCurrentMessage(Validator.validateWidthDecimalSetting(
						crfBuilder.getCurrentItem().getItemMeta().getWidthDecimal(),
						crfBuilder.getCurrentItem().getRealValue(RealValueKey.ITEM_DATA_TYPE), isCalc,
						crfBuilder.getLocale()));
				if (crfBuilder.getCurrentMessage().length() > 0) {
					crfBuilder.getErrorMessageProducer().widthDecimalHasErrors();
				}
			}
		}
	}

	private static void validateRegexp(BaseCrfBuilder crfBuilder) throws Exception {
		if (!StringUtil.isBlank(crfBuilder.getCurrentItem().getItemMeta().getRegexp())) {
			// parse the string and get reg exp eg. regexp: /[0-9]*/
			String regexp = crfBuilder.getCurrentItem().getItemMeta().getRegexp().trim();
			if (regexp.startsWith("regexp:")) {
				String finalRegexp = regexp.substring(INT_7).trim();
				if (finalRegexp.contains("\\\\")) {
					crfBuilder.getErrorMessageProducer().validationColumnHasInvalidRegularExpression();
				} else {
					if (finalRegexp.startsWith("/") && finalRegexp.endsWith("/")) {
						finalRegexp = finalRegexp.substring(1, finalRegexp.length() - 1);
						try {
							Pattern.compile(finalRegexp);
						} catch (PatternSyntaxException pse) {
							crfBuilder.getErrorMessageProducer().regexpIsInvalidRegularExpression();
						}
					} else {
						crfBuilder.getErrorMessageProducer().regexpIsInvalidRegularExpression();
					}
				}

			} else if (regexp.startsWith("func:")) {
				try {
					Validator.processCRFValidationFunction(regexp);
				} catch (Exception ex) {
					crfBuilder.setCurrentMessage(new StringBuffer(ex.getMessage()));
					crfBuilder.getErrorMessageProducer().regexpIsNotValid();
				}
			} else {
				crfBuilder.getErrorMessageProducer().validationColumnIsNotValid();
			}
		}
		if (!StringUtil.isBlank(crfBuilder.getCurrentItem().getItemMeta().getRegexp())
				&& StringUtil.isBlank(crfBuilder.getCurrentItem().getItemMeta().getRegexpErrorMsg())) {
			crfBuilder.getErrorMessageProducer().regexpErrorMsgIsBlank();
		}
		if (crfBuilder.getCurrentItem().getItemMeta().getRegexpErrorMsg() != null
				&& crfBuilder.getCurrentItem().getItemMeta().getRegexpErrorMsg().length() > INT_255) {
			crfBuilder.getErrorMessageProducer().regexpErrorMsgLengthIsExceeded();
		}
	}

	private static void validatePhiAndRequired(BaseCrfBuilder crfBuilder) throws Exception {
		if (crfBuilder.isExcelCrfBuilder()) {
			if (!(crfBuilder.getCurrentItem().getRealValue(RealValueKey.PHI).isEmpty()
					|| ZERO.equals(crfBuilder.getCurrentItem().getRealValue(RealValueKey.PHI))
					|| ONE.equals(crfBuilder.getCurrentItem().getRealValue(RealValueKey.PHI)))) {
				crfBuilder.getErrorMessageProducer().phiIsNotValid();
			}
			if (!ZERO.equals(crfBuilder.getCurrentItem().getRealValue(RealValueKey.REQUIRED))
					&& !ONE.equals(crfBuilder.getCurrentItem().getRealValue(RealValueKey.REQUIRED))) {
				crfBuilder.getErrorMessageProducer().requiredIsNotValid();
			}
		} else if (crfBuilder.isJsonCrfBuilder()
				&& crfBuilder.getCurrentItem().getItemDataTypeId() != ItemDataType.LABEL.getId()
				&& crfBuilder.getCurrentItem().getItemDataTypeId() != ItemDataType.DIVIDER.getId()) {
			if (!(crfBuilder.getCurrentItem().getRealValue(RealValueKey.PHI).isEmpty()
					|| TRUE.equals(crfBuilder.getCurrentItem().getRealValue(RealValueKey.PHI))
					|| FALSE.equals(crfBuilder.getCurrentItem().getRealValue(RealValueKey.PHI)))) {
				crfBuilder.getErrorMessageProducer().phiIsNotValid();
			}
			if (!YES.equals(crfBuilder.getCurrentItem().getRealValue(RealValueKey.REQUIRED))
					&& !NO.equals(crfBuilder.getCurrentItem().getRealValue(RealValueKey.REQUIRED))) {
				crfBuilder.getErrorMessageProducer().requiredIsNotValid();
			}
		}
	}

	private static void validateMedicalCodingRow(BaseCrfBuilder crfBuilder) throws Exception {
		String codeRef = crfBuilder.getCurrentItem().getItemMeta().getCodeRef();
		String itemType = crfBuilder.getCurrentItem().getRealValue(RealValueKey.ITEM_DATA_TYPE);
		String itemName = crfBuilder.getCurrentItem().getName();
		if (!codeRef.isEmpty()) {
			if (CodingFieldsUtil.getEnumAsList(codeRef) == null
					&& !crfBuilder.getCodingRefItemNames().contains(codeRef)) {
				crfBuilder.getErrorMessageProducer().ontologyNameIsNotValid();
			} else {
				if (CodingFieldsUtil.getEnumAsList(codeRef) == null && !itemType.equalsIgnoreCase("CODE")
						&& !itemName.contains("_GR")) {
					crfBuilder.getErrorMessageProducer().needToUpdateCodingItemTypeToCode();
				}
				if (CodingFieldsUtil.getEnumAsList(codeRef) != null && !itemType.equalsIgnoreCase("ST")) {
					crfBuilder.getErrorMessageProducer().needToUpdateMedicalCodingReferenceItemType();
				}
			}
		}
	}

	private static void validateItemDisplayStatusAndSCD(BaseCrfBuilder crfBuilder) throws Exception {
		if (!StringUtil.isBlank(crfBuilder.getCurrentItem().getRealValue(RealValueKey.SCD_DATA))) {
			if (crfBuilder.getCurrentItem().getItemMeta().isShowItem()) {
				crfBuilder.getErrorMessageProducer().itemDisplayStatusIsNotValid();
			}
			// validate availability of item_label
			if (crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean() != null) {
				if (crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().getControlItemName().length() > 0
						&& crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().getOptionValue().length() > 0
						&& crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().getMessage().length() > 0) {
					if (crfBuilder.getItemNames().contains(
							crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().getControlItemName())) {
						String pvKey = crfBuilder.getCurrentItem().getItemMeta().getSectionName() + "---"
								+ crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().getControlItemName();
						ItemBean controlItemBean = crfBuilder.getItemNameToItemMap().get(
								crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().getControlItemName());
						if (controlItemBean != null && crfBuilder.getControlValues().containsKey(pvKey)) {
							String[] pvs = crfBuilder.getControlValues().get(pvKey);
							boolean existing = false;
							for (String s : pvs) {
								if (controlItemBean.getItemDataTypeId() == ItemDataType.INTEGER.getId()) {
									try {
										if (Integer.parseInt(s.trim()) == Integer.parseInt(crfBuilder.getCurrentItem()
												.getSimpleConditionalDisplayBean().getOptionValue().trim())) {
											existing = true;
											break;
										}
									} catch (Exception ex) {
										//
									}
								} else if (controlItemBean.getItemDataTypeId() == ItemDataType.REAL.getId()) {
									try {
										if (Double.parseDouble(s.trim()) == Double
												.parseDouble(crfBuilder.getCurrentItem()
														.getSimpleConditionalDisplayBean().getOptionValue().trim())) {
											existing = true;
											break;
										}
									} catch (Exception ex) {
										//
									}
								} else {
									if (s.trim().equals(crfBuilder.getCurrentItem().getSimpleConditionalDisplayBean()
											.getOptionValue().trim())) {
										existing = true;
										break;
									}
								}
							}
							if (!existing) {
								crfBuilder.getErrorMessageProducer().controlResponseValueIsNotValid();
							}
						}
					} else {
						crfBuilder.getErrorMessageProducer().controlItemNameIsNotValid();
					}
				} else {
					crfBuilder.getErrorMessageProducer().simpleConditionalDisplayIsNotValid();
				}
			} else {
				crfBuilder.getErrorMessageProducer().simpleConditionalDisplayIsNotValid();
			}
		}
	}

	private static void instantValidation(BaseCrfBuilder crfBuilder) {
		crfBuilder.getInstantValidator().validate();
		crfBuilder.getInstantValidator().getSheetErrors().addErrorsToSheet(crfBuilder.getErrorsList());
		crfBuilder.getInstantValidator().getSheetErrors().putHtmlErrorsToSheet(crfBuilder.getErrorsMap());

	}

	/**
	 * Validates CrfBuilder data.
	 *
	 * @param crfBuilder
	 *            BaseCrfBuilder
	 * @throws Exception
	 *             an Exception
	 */
	public static void validate(BaseCrfBuilder crfBuilder) throws Exception {
		validateCrf(crfBuilder);
		for (SectionBean sectionBean : crfBuilder.getSections()) {
			crfBuilder.setCurrentSection(sectionBean);
			validateSection(crfBuilder);
		}
		for (ItemGroupBean itemGroupBean : crfBuilder.getItemGroups()) {
			crfBuilder.setCurrentItemGroup(itemGroupBean);
			validateGroup(crfBuilder);
		}
		crfBuilder.prepareItemGroupCrfRecords();
		if (crfBuilder.getItems().size() == 0) {
			crfBuilder.getErrorMessageProducer().crfShouldHaveAtLeastOneItem();
		}
		for (ItemBean itemBean : crfBuilder.getItems()) {
			crfBuilder.setCurrentItem((ItemBeanExt) itemBean);
			validateItem(crfBuilder);
			verifySectionGroupPlacementForItem(crfBuilder);
			verifyUniqueItemPlacementInGroups(crfBuilder);
			validateResponseSet(crfBuilder);
			validateExpression(crfBuilder);
			validateDataType(crfBuilder);
			validateResponseOptionsValuesForMatchingDataType(crfBuilder);
			validateWidthDecimal(crfBuilder);
			validateRegexp(crfBuilder);
			validatePhiAndRequired(crfBuilder);
			validateMedicalCodingRow(crfBuilder);
			validateItemDisplayStatusAndSCD(crfBuilder);
		}
		instantValidation(crfBuilder);
	}
}
