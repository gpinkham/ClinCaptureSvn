package com.clinovo.lib.crf.producer.impl;

import java.util.Locale;

import javax.sql.DataSource;

import com.clinovo.service.ItemRenderMetadataService;
import org.akaza.openclinica.DefaultAppContextTest;
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
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.clinovo.lib.crf.bean.ItemBeanExt;
import com.clinovo.lib.crf.builder.impl.JsonCrfBuilder;
import com.clinovo.lib.crf.service.ImportCrfService;

public class JsonErrorMessageProducerTest extends DefaultAppContextTest {

	@Mock
	private JSONObject jsonObject;
	@Mock
	private StudyBean studyBean;
	@Mock
	private DataSource dataSource;
	@Mock
	private UserAccountBean owner;

	private JsonCrfBuilder jsonCrfBuilder;

	private JsonErrorMessageProducer jsonErrorMessageProducer;

	@Mock
	private ImportCrfService importCrfService;
	@Mock
	private ItemRenderMetadataService metadataService;

	@Override
	protected void restoreDb() throws Exception {
		// do not restore db
	}

	@Before
	public void before() {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		jsonCrfBuilder = new JsonCrfBuilder(jsonObject, owner, studyBean, dataSource, Locale.ENGLISH, messageSource,
				importCrfService, metadataService);
		jsonErrorMessageProducer = new JsonErrorMessageProducer(jsonCrfBuilder);
		jsonCrfBuilder.setCurrentMessage(new StringBuffer());
		jsonCrfBuilder.setCurrentItemGroup(new ItemGroupBean());
		jsonCrfBuilder.setCurrentSection(new SectionBean());
		jsonCrfBuilder.setCurrentItem(new ItemBeanExt());
		jsonCrfBuilder.getCurrentItem().setResponseSet(new ResponseSet());
		jsonCrfBuilder.getCurrentItem().setItemMeta(new ItemFormMetadataBean());
		jsonCrfBuilder.getCurrentItem().setSimpleConditionalDisplayBean(new SimpleConditionalDisplayBean());
		jsonCrfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setMessage("");
		jsonCrfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setOptionValue("");
		jsonCrfBuilder.getCurrentItem().getSimpleConditionalDisplayBean().setControlItemName("");
		jsonCrfBuilder.getItemGroupLabelToMetaMap().put(jsonCrfBuilder.getCurrentItemGroup().getName(),
				new ItemGroupMetadataBean());
	}

	@Test(expected = CRFReadingException.class)
	public void testThatCrfVersionIsBlankMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.crfVersionIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatCrfVersionLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.crfVersionLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatCrfVersionDescriptionLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.crfVersionDescriptionLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatCrfRevisionNotesIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.crfRevisionNotesIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatCrfRevisionNotesLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.crfRevisionNotesLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test(expected = CRFReadingException.class)
	public void testThatCrfNameIsBlankMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.crfNameIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatCrfNameLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.crfNameLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test(expected = CRFReadingException.class)
	public void testThatCrfVersionHasAlreadyBeenUsedMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.crfVersionHasAlreadyBeenUsed();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test(expected = CRFReadingException.class)
	public void testThatDidNotMatchCrfNameMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.didNotMatchCrfName("Test Name");
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatSectionLabelIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.sectionLabelIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatSectionLabelLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.sectionLabelLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatSectionLabelIsDuplicatedMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.sectionLabelIsDuplicated();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatSectionTitleIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.sectionTitleIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatSectionTitleLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.sectionTitleLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatSectionInstructionsLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.sectionInstructionsLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatSectionPageNumberLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.sectionPageNumberLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatGroupLabelLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.groupLabelLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatGroupLabelIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.groupLabelIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatGroupHeaderLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.groupHeaderLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatGroupLabelIsDuplicatedMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.groupLabelIsDuplicated();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatRepeatNumIsWrongMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.repeatNumIsWrong();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatRepeatMaxIsWrongMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.repeatMaxIsWrong();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemNameIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemNameIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemNameLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.itemNameLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemNameIsDuplicatedMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemNameIsDuplicated();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemNameIsNotMatchingRegexpMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemNameIsNotMatchingRegexp();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemDescriptionIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemDescriptionIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemDescriptionLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.itemDescriptionLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemLeftTextLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.itemLeftTextLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemRightTextLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.itemRightTextLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemHeaderLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.itemHeaderLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemSubHeaderLengthIsExceededMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemSubHeaderLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemSectionLabelIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemSectionLabelIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemSectionLabelLengthIsExceededMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemSectionLabelLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemGroupLabelIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemGroupLabelIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemGroupLabelLengthIsExceededMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemGroupLabelLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemUnitsLengthIsExceededMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemUnitsLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemParentItemIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemParentItemIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatHasNestedParentItemMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.hasNestedParentItem();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatRepeatingGroupHasParentItemMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.repeatingGroupHasParentItem();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatResponseTypeIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.responseTypeIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatResponseTypeIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.responseTypeIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatHasRadioWithDefaultMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.hasRadioWithDefault();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatResponseLabelIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.responseLabelIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatResponseOptionsTextIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.responseOptionsTextIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemHasDifferentNumberOfOptionsTextMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.responseOptionsTextIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemHasDifferentValuesForOptionsTextMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemHasDifferentValuesForOptionsText();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatResponseOptionsValuesIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.responseOptionsValuesIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemHasDifferentNumberOfOptionsValuesMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemHasDifferentNumberOfOptionsValues();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemHasDifferentValuesForOptionsValuesMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemHasDifferentValuesForOptionsValues();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatExpressionDoesNotStartWithFuncMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.expressionDoesNotStartWithFunc();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatExpressionIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.expressionIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemHasIncompleteOptionValuePairMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemHasIncompleteOptionValuePair();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemMustBeListedBeforeAnotherItemMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemMustBeListedBeforeAnotherItem();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemsMustHaveTheSameGroupMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemsMustHaveTheSameGroup();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemsShouldNotHaveTheSameGroupMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemsShouldNotHaveTheSameGroup();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemDataTypeIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemDataTypeIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemDataTypeIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemDataTypeIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemDataTypeShouldBeFileMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemDataTypeShouldBeFile();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatResponseOptionsValuesShouldBeIntegerMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.responseOptionsValuesShouldBeInteger();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatResponseOptionsValuesShouldBeRealMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.responseOptionsValuesShouldBeReal();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatDoesNotMatchDataTypeOfItemWithSameResponseLabelMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.doesNotMatchDataTypeOfItemWithSameResponseLabel();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatWidthDecimalIsNotAvailableMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.widthDecimalIsNotAvailable();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatWidthDecimalHasErrorsMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.widthDecimalHasErrors();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatRegexpIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.regexpIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatValidationColumnIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.validationColumnIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatRegexpErrorMsgIsBlankMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.regexpErrorMsgIsBlank();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatRegexpErrorMsgLengthIsExceededMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.regexpErrorMsgLengthIsExceeded();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatRegexpIsInvalidRegularExpressionMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.regexpIsInvalidRegularExpression();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatValidationColumnHasInvalidRegularExpressionMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.validationColumnHasInvalidRegularExpression();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatPhiIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.phiIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatRequiredIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.requiredIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatItemDisplayStatusIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.itemDisplayStatusIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatControlResponseValueIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.controlResponseValueIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatControlItemNameIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.controlItemNameIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatSimpleConditionalDisplayIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.simpleConditionalDisplayIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatOntologyNameIsNotValidMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.ontologyNameIsNotValid();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatNeedToUpdateCodingItemTypeToCodeMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.needToUpdateCodingItemTypeToCode();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatNeedToUpdateMedicalCodingReferenceItemTypeMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.needToUpdateMedicalCodingReferenceItemType();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test
	public void testThatResponseLabelHasBeenUsedForAnotherResponseTypeMethodGeneratesErrorMessages() throws Exception {
		jsonErrorMessageProducer.responseLabelHasBeenUsedForAnotherResponseType();
		assertEquals(jsonCrfBuilder.getErrorsList().size(), 1);
	}

	@Test(expected = CRFReadingException.class)
	public void testThatCrfShouldHaveAtLeastOneItemMethodGeneratesErrorMessage() throws Exception {
		jsonErrorMessageProducer.crfShouldHaveAtLeastOneItem();
	}
}
