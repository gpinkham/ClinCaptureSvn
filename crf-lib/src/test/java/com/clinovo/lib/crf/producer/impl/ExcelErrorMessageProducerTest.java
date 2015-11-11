package com.clinovo.lib.crf.producer.impl;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import javax.sql.DataSource;

import com.clinovo.service.ItemRenderMetadataService;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.odmbeans.SimpleConditionalDisplayBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.domain.datamap.ResponseSet;
import org.akaza.openclinica.exception.CRFReadingException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import com.clinovo.lib.crf.bean.ItemBeanExt;
import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;
import com.clinovo.lib.crf.service.ImportCrfService;

@RunWith(MockitoJUnitRunner.class)
public class ExcelErrorMessageProducerTest {

	@Mock
	private Row row;
	@Mock
	private Cell cell;
	@Mock
	private Sheet sheet;
	@Mock
	private Workbook workbook;
	@Mock
	private StudyBean studyBean;
	@Mock
	private DataSource dataSource;
	@Mock
	private UserAccountBean owner;

	private ExcelCrfBuilder excelCrfBuilder;

	private ExcelErrorMessageProducer excelErrorMessageProducer;

	@Mock
	private ImportCrfService importCrfService;
	@Mock
	private ItemRenderMetadataService metadataService;

	@Mock
	private MessageSource messageSource;

	@Before
	public void before() {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		Mockito.when(workbook.getSheetAt(Mockito.anyInt())).thenReturn(sheet);
		Mockito.when(row.getCell(Mockito.anyInt())).thenReturn(cell);
		Mockito.when(sheet.getRow(Mockito.anyInt())).thenReturn(row);
		Mockito.when(sheet.getRow(Mockito.anyInt())).thenReturn(row);
		excelCrfBuilder = new ExcelCrfBuilder(workbook, owner, studyBean, dataSource, Locale.ENGLISH, messageSource,
				importCrfService, metadataService);
		excelErrorMessageProducer = new ExcelErrorMessageProducer(excelCrfBuilder);
		excelCrfBuilder.setCurrentMessage(new StringBuffer());
		excelCrfBuilder.setCurrentItemGroup(new ItemGroupBean());
		excelCrfBuilder.setCurrentSection(new SectionBean());
		excelCrfBuilder.setCurrentItem(new ItemBeanExt());
		excelCrfBuilder.getCurrentItem().setResponseSet(new ResponseSet());
		excelCrfBuilder.getCurrentItem().setItemMeta(new ItemFormMetadataBean());
		excelCrfBuilder.getCurrentItem().setSimpleConditionalDisplayBean(new SimpleConditionalDisplayBean());
		excelCrfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setMessage("");
		excelCrfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setOptionValue("");
		excelCrfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setControlItemName("");
		excelCrfBuilder.getItemGroupLabelToMetaMap().put(excelCrfBuilder.getCurrentItemGroup().getName(),
				new ItemGroupMetadataBean());
	}

	@Test(expected = CRFReadingException.class)
	public void testThatCrfVersionIsBlankMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.crfVersionIsBlank();
	}

	@Test
	public void testThatCrfVersionLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.crfVersionLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatCrfVersionDescriptionLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.crfVersionDescriptionLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatCrfRevisionNotesIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.crfRevisionNotesIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatCrfRevisionNotesLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.crfRevisionNotesLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test(expected = CRFReadingException.class)
	public void testThatCrfNameIsBlankMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.crfNameIsBlank();
	}

	@Test
	public void testThatCrfNameLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.crfNameLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test(expected = CRFReadingException.class)
	public void testThatCrfNameHasAlreadyBeenUsedMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.crfNameHasAlreadyBeenUsed();
	}

	@Test(expected = CRFReadingException.class)
	public void testThatCrfVersionHasAlreadyBeenUsedMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.crfVersionHasAlreadyBeenUsed();
	}

	@Test(expected = CRFReadingException.class)
	public void testThatDidNotMatchCrfNameMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.didNotMatchCrfName("Test Name");
	}

	@Test
	public void testThatSectionLabelIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.sectionLabelIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatSectionLabelLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.sectionLabelLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatSectionLabelIsDuplicatedMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.sectionLabelIsDuplicated();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatSectionTitleIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.sectionTitleIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatSectionTitleLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.sectionTitleLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatSectionInstructionsLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.sectionInstructionsLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatSectionPageNumberLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.sectionPageNumberLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatGroupLabelLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.groupLabelLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatGroupLabelIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.groupLabelIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatGroupHeaderLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.groupHeaderLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatGroupLabelIsDuplicatedMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.groupLabelIsDuplicated();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatRepeatNumIsWrongMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.repeatNumIsWrong();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatRepeatMaxIsWrongMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.repeatMaxIsWrong();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemNameIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemNameIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemNameLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.itemNameLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemNameIsDuplicatedMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemNameIsDuplicated();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemNameIsNotMatchingRegexpMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemNameIsNotMatchingRegexp();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemDescriptionIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemDescriptionIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemDescriptionLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.itemDescriptionLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemLeftTextLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.itemLeftTextLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemRightTextLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.itemRightTextLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemHeaderLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.itemHeaderLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemSubHeaderLengthIsExceededMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemSubHeaderLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemSectionLabelIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemSectionLabelIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemSectionLabelLengthIsExceededMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemSectionLabelLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemGroupLabelIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemGroupLabelIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemGroupLabelLengthIsExceededMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemGroupLabelLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemUnitsLengthIsExceededMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemUnitsLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemParentItemIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemParentItemIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatHasNestedParentItemMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.hasNestedParentItem();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatRepeatingGroupHasParentItemMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.repeatingGroupHasParentItem();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatResponseTypeIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.responseTypeIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatResponseTypeIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.responseTypeIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatHasRadioWithDefaultMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.hasRadioWithDefault();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatResponseLabelIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.responseLabelIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatResponseOptionsTextIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.responseOptionsTextIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemHasDifferentNumberOfOptionsTextMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.responseOptionsTextIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemHasDifferentValuesForOptionsTextMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemHasDifferentValuesForOptionsText();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatResponseOptionsValuesIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.responseOptionsValuesIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemHasDifferentNumberOfOptionsValuesMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemHasDifferentNumberOfOptionsValues();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemHasDifferentValuesForOptionsValuesMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemHasDifferentValuesForOptionsValues();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatExpressionDoesNotStartWithFuncMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.expressionDoesNotStartWithFunc();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatExpressionIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.expressionIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemHasIncompleteOptionValuePairMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemHasIncompleteOptionValuePair();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 2);
	}

	@Test
	public void testThatItemMustBeListedBeforeAnotherItemMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemMustBeListedBeforeAnotherItem();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemsMustHaveTheSameGroupMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemsMustHaveTheSameGroup();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemsShouldNotHaveTheSameGroupMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemsShouldNotHaveTheSameGroup();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemDataTypeIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemDataTypeIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemDataTypeIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemDataTypeIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemDataTypeShouldBeFileMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemDataTypeShouldBeFile();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatResponseOptionsValuesShouldBeIntegerMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.responseOptionsValuesShouldBeInteger();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatResponseOptionsValuesShouldBeRealMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.responseOptionsValuesShouldBeReal();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatDoesNotMatchDataTypeOfItemWithSameResponseLabelMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.doesNotMatchDataTypeOfItemWithSameResponseLabel();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatWidthDecimalIsNotAvailableMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.widthDecimalIsNotAvailable();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatWidthDecimalHasErrorsMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.widthDecimalHasErrors();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatRegexpIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.regexpIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatValidationColumnIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.validationColumnIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatRegexpErrorMsgIsBlankMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.regexpErrorMsgIsBlank();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatRegexpErrorMsgLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.regexpErrorMsgLengthIsExceeded();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatRegexpIsInvalidRegularExpressionMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.regexpIsInvalidRegularExpression();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatValidationColumnHasInvalidRegularExpressionMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.validationColumnHasInvalidRegularExpression();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatPhiIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.phiIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatRequiredIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.requiredIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatItemDisplayStatusIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.itemDisplayStatusIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatControlResponseValueIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.controlResponseValueIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatControlItemNameIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.controlItemNameIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatSimpleConditionalDisplayIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.simpleConditionalDisplayIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatOntologyNameIsNotValidMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.ontologyNameIsNotValid();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatNeedToUpdateCodingItemTypeToCodeMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.needToUpdateCodingItemTypeToCode();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatNeedToUpdateMedicalCodingReferenceItemTypeMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.needToUpdateMedicalCodingReferenceItemType();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test
	public void testThatResponseLabelHasBeenUsedForAnotherResponseTypeMethodGeneratesErrorMessages() throws Exception {
		excelErrorMessageProducer.responseLabelHasBeenUsedForAnotherResponseType();
		assertEquals(excelCrfBuilder.getErrorsList().size(), 1);
		assertEquals(excelCrfBuilder.getErrorsMap().size(), 1);
	}

	@Test(expected = CRFReadingException.class)
	public void testThatCrfShouldHaveAtLeastOneItemMethodGeneratesErrorMessage() throws Exception {
		excelErrorMessageProducer.crfShouldHaveAtLeastOneItem();
	}
}
