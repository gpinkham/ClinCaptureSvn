package com.clinovo.lib.crf.validator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.exception.CRFReadingException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;

import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;
import com.clinovo.lib.crf.builder.impl.JsonCrfBuilder;
import com.clinovo.lib.crf.enums.CellName;
import com.clinovo.lib.crf.enums.SheetName;
import com.clinovo.lib.crf.factory.CrfBuilderFactory;
import com.clinovo.lib.crf.producer.impl.ExcelErrorMessageProducer;
import com.clinovo.lib.crf.producer.impl.JsonErrorMessageProducer;

public class CommonValidatorTest extends DefaultAppContextTest {

	private StudyBean studyBean;

	private UserAccountBean owner;

	private JsonCrfBuilder jsonCrfBuilder;

	private ExcelCrfBuilder excelCrfBuilder;

	@Autowired
	private CrfBuilderFactory crfBuilderFactory;

	@Before
	public void before() {
		studyBean = (StudyBean) studyDAO.findByPK(1);
		owner = (UserAccountBean) userAccountDAO.findByPK(1);
	}

	@Override
	protected void restoreDb() throws Exception {
		// do not restore db
	}

	private String getJsonData(String fileName) throws Exception {
		InputStream inputStream = null;
		try {
			inputStream = new DefaultResourceLoader().getResource("data/json/".concat(fileName)).getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			return out.toString();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception ex) {
				//
			}
		}
	}

	private Workbook getWorkbook(String fileName) throws Exception {
		InputStream inputStream = null;
		boolean isXlsx = fileName.toLowerCase().endsWith(".xlsx");
		try {
			inputStream = new DefaultResourceLoader().getResource("data/excel/".concat(fileName)).getInputStream();
			return !isXlsx ? new HSSFWorkbook(new POIFSFileSystem(inputStream)) : new XSSFWorkbook(inputStream);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception ex) {
				//
			}
		}
	}

	private String generateString(int length) {
		String result = "";
		for (int i = 0; i <= length - 1; i++) {
			result += "a";
		}
		return result;
	}

	private JsonCrfBuilder getMockedJsonCrfBuilder(String jsonData) throws Exception {
		jsonCrfBuilder = Mockito.spy((JsonCrfBuilder) crfBuilderFactory.getCrfBuilder(jsonData.toString(), studyBean,
				owner, Locale.ENGLISH, messageSource));
		JsonErrorMessageProducer mockedJsonErrorMessageProducer = Mockito
				.spy(new JsonErrorMessageProducer(jsonCrfBuilder));
		Mockito.when(jsonCrfBuilder.getErrorMessageProducer()).thenReturn(mockedJsonErrorMessageProducer);
		return jsonCrfBuilder;
	}

	private ExcelCrfBuilder getMockedExcelCrfBuilder(Workbook workbook) throws Exception {
		excelCrfBuilder = Mockito.spy((ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner,
				Locale.ENGLISH, messageSource));
		ExcelErrorMessageProducer mockedExcelErrorMessageProducer = Mockito
				.spy(new ExcelErrorMessageProducer(excelCrfBuilder));
		Mockito.when(excelCrfBuilder.getErrorMessageProducer()).thenReturn(mockedExcelErrorMessageProducer);
		return excelCrfBuilder;
	}

	@Test
	public void testThatJsonCrfBuilderDoesNotGenerateAnyErrorsForCorrectData() throws Exception {
		jsonCrfBuilder = (JsonCrfBuilder) crfBuilderFactory.getCrfBuilder(getJsonData("testCrf.json"), studyBean, owner,
				Locale.ENGLISH, messageSource);
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().isEmpty());
		assertTrue(jsonCrfBuilder.getErrorsMap().isEmpty());
	}

	@Test(expected = CRFReadingException.class)
	public void testThatJsonCrfBuilderGeneratesErrorsIfCrfVersionIsBlank() throws Exception {
		JSONObject jsonData = new JSONObject(getJsonData("testCrf.json"));
		jsonData.put("version", "");
		jsonCrfBuilder = (JsonCrfBuilder) crfBuilderFactory.getCrfBuilder(jsonData.toString(), studyBean, owner,
				Locale.ENGLISH, messageSource);
		jsonCrfBuilder.build();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfCrfVersionLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("version", generateString(256));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).crfVersionLengthIsExceeded();
	}

	@Test(expected = CRFReadingException.class)
	public void testThatJsonCrfBuilderGeneratesErrorsIfCrfNameIsBlank() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("name", "");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfCrfNameLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("name", generateString(256));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).crfNameLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfSectionLabelIsBlank() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).put("name", "");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).sectionLabelIsBlank();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfSectionLabelLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).put("name", generateString(2001));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).sectionLabelLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfSectionLabelIsDuplicated() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).put("name", "section1");
		jsonObject.getJSONArray("pages").getJSONObject(1).put("name", "section1");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).sectionLabelIsDuplicated();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfSectionTitleLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).put("title", generateString(2001));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).sectionTitleLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfSectionInstructionsLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).put("instructions", generateString(10001));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).sectionInstructionsLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfGroupLabelIsBlank() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(3)
				.getJSONArray("children").getJSONObject(1).put("itemName", "");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).groupLabelIsBlank();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfGroupLabelLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(3)
				.getJSONArray("children").getJSONObject(1).put("itemName", generateString(256));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).groupLabelLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfGroupHeaderLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(3)
				.getJSONArray("children").getJSONObject(1).put("header", generateString(256));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).groupHeaderLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfGroupLabelIsDuplicated() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		JSONObject jsonObjectTable = jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions")
				.getJSONObject(3).getJSONArray("children").getJSONObject(1);
		jsonObjectTable.put("itemName", "table1");
		jsonObjectTable.put("header", "_dup_header1");
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(3)
				.getJSONArray("children").put(2, jsonObjectTable);
		jsonObjectTable = jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(3)
				.getJSONArray("children").getJSONObject(2);
		jsonObjectTable.put("itemName", "table2");
		jsonObjectTable.put("header", "_dup_header1");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).groupLabelIsDuplicated();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfItemNameIsBlank() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("itemName",
				"");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).itemNameIsBlank();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfItemNameLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("itemName",
				generateString(256));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).itemNameLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfItemNameIsDuplicated() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("itemName",
				"_dup_item1");
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(1)
				.getJSONArray("children").getJSONObject(0).put("itemName", "_dup_item1");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).itemNameIsDuplicated();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfItemDescriptionLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("description",
				generateString(4001));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).itemDescriptionLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfItemNameIsWrong() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("itemName",
				"$$###");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).itemNameIsNotMatchingRegexp();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfRepeatNumIsWrong() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(3)
				.getJSONArray("children").getJSONObject(1).put("minRows", "-1");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).repeatNumIsWrong();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfRepeatMaxIsWrong() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(3)
				.getJSONArray("children").getJSONObject(1).put("maxRows", "-1");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).repeatMaxIsWrong();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfLeftItemTextLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("leftText",
				generateString(4001));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).itemLeftTextLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfRightItemTextLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("rightText",
				generateString(2001));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).itemRightTextLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfItemHeaderLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("header",
				generateString(2001));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).itemHeaderLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfItemSubHeaderLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("subheader",
				generateString(241));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).itemSubHeaderLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfItemGroupLabelLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(3)
				.getJSONArray("children").getJSONObject(1).put("itemName", generateString(256));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer(), Mockito.times(4)).itemGroupLabelLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfItemSectionLabelLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).put("name", generateString(2001));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer(), Mockito.times(32)).itemSectionLabelLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfResponseTypeIsBlank() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("type", "");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).responseTypeIsBlank();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfResponseTypeIsNotValid() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("type",
				"WRONG TYPE");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).responseTypeIsNotValid();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfRadioHasItemWithDefaultValue() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(2)
				.getJSONArray("children").getJSONObject(0).put("defaultValue", "1");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).hasRadioWithDefault();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfResponseOptionsTextIsBlank() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(2)
				.getJSONArray("children").getJSONObject(0).put("options", new JSONArray());
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).responseOptionsTextIsBlank();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfResponseOptionsValuesIsBlank() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(2)
				.getJSONArray("children").getJSONObject(0).put("options", new JSONArray());
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).responseOptionsValuesIsBlank();
	}

	@Test(expected = CRFReadingException.class)
	public void testThatJsonCrfBuilderGeneratesErrorsIfCrfDoesNotHaveItems() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).put("questions", new JSONArray());
		jsonObject.getJSONArray("pages").getJSONObject(1).put("questions", new JSONArray());
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfOntologyNameIsNotValid() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(4).put("dictionary", "WRONG VALUE");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).ontologyNameIsNotValid();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfExpressionDoesNotStartWithFunc() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("defaultValue", ": 2+2");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).expressionDoesNotStartWithFunc();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfExpressionIsNotValid() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("defaultValue", "func: $#2*/d;2+2");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).expressionIsNotValid();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfValidationColumnIsNotValid() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("fieldValidation", ":#$afasdg32x10");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).validationColumnIsNotValid();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfRegexpErrorMsgIsBlank() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("fieldValidation", "regexp: /2.*/");
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("validationMessage", "");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).regexpErrorMsgIsBlank();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfRegexpErrorMsgLengthIsExceeded() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("fieldValidation", "regexp: /2.*/");
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("validationMessage", generateString(256));
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).regexpErrorMsgLengthIsExceeded();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfRegexpIsNotValid() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("fieldValidation", "func: #$afasdg32x10");
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("validationMessage", "BLA");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).regexpIsNotValid();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfRegexpIsInvalidRegularExpression() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("fieldValidation", "regexp: #$afasdg32x10");
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("validationMessage", "BLA");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).regexpIsInvalidRegularExpression();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfValidationColumnHasInvalidRegularExpression() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("fieldValidation", "regexp: \\\\#$afasdg32x10");
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(4)
				.getJSONArray("children").getJSONObject(3).put("validationMessage", "BLA");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).validationColumnHasInvalidRegularExpression();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfPhiIsNotValid() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("phiData",
				"WRONG TYPE");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).phiIsNotValid();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfRequiredIsNotValid() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(0).put("required",
				"WRONG TYPE");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).requiredIsNotValid();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfItemDisplayStatusIsNotValid() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(2)
				.getJSONArray("children").getJSONObject(0).put("displayState", "show");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).itemDisplayStatusIsNotValid();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfControlResponseValueIsNotValid() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(2)
				.getJSONArray("children").getJSONObject(0).getJSONArray("SL").put(1, "XF12");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).controlResponseValueIsNotValid();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfControlItemNameIsNotValid() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(2)
				.getJSONArray("children").getJSONObject(0).getJSONArray("SL").put(0, "wrong item");
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).controlItemNameIsNotValid();
	}

	@Test
	public void testThatJsonCrfBuilderGeneratesErrorsIfSCDIsNotValid() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(0, "item2");
		jsonArray.put(1, "t");
		jsonObject.getJSONArray("pages").getJSONObject(0).getJSONArray("questions").getJSONObject(2)
				.getJSONArray("children").getJSONObject(0).put("SL", jsonArray);
		jsonCrfBuilder = getMockedJsonCrfBuilder(jsonObject.toString());
		jsonCrfBuilder.build();
		assertTrue(jsonCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(jsonCrfBuilder.getErrorMessageProducer()).simpleConditionalDisplayIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderDoesNotGenerateAnyErrorsForCorrectData() throws Exception {
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean,
				owner, Locale.ENGLISH, messageSource);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().isEmpty());
		assertTrue(excelCrfBuilder.getErrorsMap().isEmpty());
	}

	@Test(expected = CRFReadingException.class)
	public void testThatExcelCrfBuilderGeneratesErrorsIfCrfVersionIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1).createCell(CellName.CRF_VERSION.getColumnNumber())
				.setCellValue("");
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfCrfVersionLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1).createCell(CellName.CRF_VERSION.getColumnNumber())
				.setCellValue(generateString(256));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).crfVersionLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfCrfVersionDescriptionLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1)
				.createCell(CellName.CRF_VERSION_DESCRIPTION.getColumnNumber()).setCellValue(generateString(4001));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).crfVersionDescriptionLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRevisionNotesIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1)
				.createCell(CellName.CRF_REVISION_NOTES.getColumnNumber()).setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).crfRevisionNotesIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRevisionNotesLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1)
				.createCell(CellName.CRF_REVISION_NOTES.getColumnNumber()).setCellValue(generateString(256));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).crfRevisionNotesLengthIsExceeded();
	}

	@Test(expected = CRFReadingException.class)
	public void testThatExcelCrfBuilderGeneratesErrorsIfCrfNameIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1).createCell(CellName.CRF_NAME.getColumnNumber())
				.setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfCrfNameLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.CRF.getSheetNumber()).getRow(1).createCell(CellName.CRF_NAME.getColumnNumber())
				.setCellValue(generateString(256));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).crfNameLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionLabelIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_LABEL.getColumnNumber()).setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).sectionLabelIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionLabelLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_LABEL.getColumnNumber()).setCellValue(generateString(2001));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).sectionLabelLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionLabelIsDuplicated() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_LABEL.getColumnNumber()).setCellValue("section1");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).createRow(2)
				.createCell(CellName.SECTION_LABEL.getColumnNumber()).setCellValue("section1");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).sectionLabelIsDuplicated();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionTitleIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_TITLE.getColumnNumber()).setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).sectionTitleIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionTitleLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_TITLE.getColumnNumber()).setCellValue(generateString(2001));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).sectionTitleLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionInstructionsLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_INSTRUCTIONS.getColumnNumber()).setCellValue(generateString(10001));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).sectionInstructionsLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSectionPageNumberLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(1)
				.createCell(CellName.SECTION_PAGE_NUMBER.getColumnNumber()).setCellValue(generateString(6));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).sectionPageNumberLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfGroupLabelIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_LABEL.getColumnNumber()).setCellValue("");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_HEADER.getColumnNumber()).setCellValue("GROUP HEADER");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).groupLabelIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfGroupLabelLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_LABEL.getColumnNumber()).setCellValue(generateString(256));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).groupLabelLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfGroupHeaderLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_HEADER.getColumnNumber() - 1).setCellValue(generateString(256));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).groupHeaderLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfGroupLabelIsDuplicated() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_LABEL.getColumnNumber()).setCellValue("group1");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(2)
				.createCell(CellName.GROUP_LABEL.getColumnNumber()).setCellValue("group1");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).groupLabelIsDuplicated();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemNameIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1).createCell(CellName.ITEM_NAME.getColumnNumber())
				.setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemNameIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemNameLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1).createCell(CellName.ITEM_NAME.getColumnNumber())
				.setCellValue(generateString(256));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemNameLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemNameIsDuplicated() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1).createCell(CellName.ITEM_NAME.getColumnNumber())
				.setCellValue("item1");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2).createCell(CellName.ITEM_NAME.getColumnNumber())
				.setCellValue("item1");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemNameIsDuplicated();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDescriptionIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DESCRIPTION_LABEL.getColumnNumber()).setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemDescriptionIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDescriptionLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DESCRIPTION_LABEL.getColumnNumber()).setCellValue(generateString(4001));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemDescriptionLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRepeatNumIsWrong() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_REPEAT_NUMBER.getColumnNumber() - 1).setCellValue("-1");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).repeatNumIsWrong();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRepeatMaxIsWrong() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.GROUPS.getSheetNumber()).getRow(1)
				.createCell(CellName.GROUP_REPEAT_MAX.getColumnNumber() - 1).setCellValue("-1");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).repeatMaxIsWrong();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemNameIsWrong() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1).createCell(CellName.ITEM_NAME.getColumnNumber())
				.setCellValue("$$###");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemNameIsNotMatchingRegexp();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfLeftItemTextLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_LEFT_ITEM_TEXT.getColumnNumber()).setCellValue(generateString(4001));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemLeftTextLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRightItemTextLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RIGHT_ITEM_TEXT.getColumnNumber()).setCellValue(generateString(2001));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemRightTextLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHeaderLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_HEADER.getColumnNumber()).setCellValue(generateString(2001));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemHeaderLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemSubHeaderLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_SUBHEADER.getColumnNumber()).setCellValue(generateString(241));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemSubHeaderLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemGroupLabelIsWrong() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("WRONG GROUP");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemGroupLabelIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemSectionLabelIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_SECTION_LABEL.getColumnNumber()).setCellValue("WRONG SECTION");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemSectionLabelIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemGroupLabelLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue(generateString(256));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemGroupLabelLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemSectionLabelLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_SECTION_LABEL.getColumnNumber()).setCellValue(generateString(2001));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemSectionLabelLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemUnitLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_UNITS.getColumnNumber()).setCellValue(generateString(65));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemUnitsLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHasWrongParentItem() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_PARENT_ITEM.getColumnNumber()).setCellValue("WRONG ITEM");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemParentItemIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseTypeIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).responseTypeIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseTypeIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("WRONG TYPE");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).responseTypeIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfThereAreNestedParentItems() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_PARENT_ITEM.getColumnNumber()).setCellValue("CM001_TXT_INT1");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(3)
				.createCell(CellName.ITEM_PARENT_ITEM.getColumnNumber()).setCellValue("CM001_TXT_INT2");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).hasNestedParentItem();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfThereAreRepeatingGroupsWithParentItems() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_PARENT_ITEM.getColumnNumber()).setCellValue("CM001_TXT_INT1");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).repeatingGroupHasParentItem();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRadioHasItemWithDefaultValue() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("radio");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DEFAULT_VALUE.getColumnNumber()).setCellValue("1");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).hasRadioWithDefault();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseLabelIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_LABEL.getColumnNumber()).setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).responseLabelIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseOptionsTextIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).responseOptionsTextIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseOptionsValuesIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).responseOptionsValuesIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHasIncompleteOptionValuePair() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("a,b,c,d,e");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("0,1,2,3,4,5");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemHasIncompleteOptionValuePair();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHasDifferentNumberOfOptionsValuesForTheSameResponseLabel()
			throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("radio");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_LABEL.getColumnNumber()).setCellValue("yyy");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("a,b,c,d,e");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("0,1,2,3,4,5");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemHasDifferentNumberOfOptionsValues();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHasDifferentValuesForOptionsValuesForTheSameResponseLabel()
			throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("radio");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_LABEL.getColumnNumber()).setCellValue("yyy");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("a,b,c,d,e");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("0,1,7,3,4");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemHasDifferentValuesForOptionsValues();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemHasDifferentNumberOfOptionsTextForTheSameResponseLabel()
			throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("radio");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_LABEL.getColumnNumber()).setCellValue("yyy");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("a,b,c,d,e,z");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("0,1,2,3,4");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemHasDifferentNumberOfOptionsText();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfExpressionDoesNotStartWithFunc() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("calculation");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue(": 2+2");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).expressionDoesNotStartWithFunc();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfExpressionIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("calculation");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("func: $#2*/d;2+2");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).expressionIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemMustBeListedBeforeAnotherItem() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("calculation");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("CM001_TXT_INT3 + CM001_TXT_INT2");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemMustBeListedBeforeAnotherItem();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemMustHaveTheSameGroup() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("calculation");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("CM001_TXT_INT1 + CM001_TXT_INT2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemsMustHaveTheSameGroup();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemsShouldNotHaveTheSameGroup() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("group-calculation");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber())
				.setCellValue("CM001_TXT_INT1 + CM001_TXT_INT2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemsShouldNotHaveTheSameGroup();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDataTypeIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemDataTypeIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDataTypeIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("WRONG DATA TYPE");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemDataTypeIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDataTypeShouldBeFile() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("file");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("ST");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemDataTypeShouldBeFile();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseOptionsValuesShouldBeInteger() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("INT");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("a,1,2,3,4");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).responseOptionsValuesShouldBeInteger();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseOptionsValuesShouldBeReal() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("REAL");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("a,1,2,3,4");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).responseOptionsValuesShouldBeReal();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfWidthDecimalIsNotAvailable() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_WIDTH_DECIMAL.getColumnNumber()).setCellValue("10");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).widthDecimalIsNotAvailable();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfWidthDecimalIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_WIDTH_DECIMAL.getColumnNumber()).setCellValue("x10");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).widthDecimalHasErrors();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfValidationColumnIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue(":#$afasdg32x10");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue("BLA");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).validationColumnIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRegexpErrorMsgIsBlank() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue("regexp: /2.*/");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue("");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).regexpErrorMsgIsBlank();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRegexpErrorMsgLengthIsExceeded() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue("regexp: /2.*/");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue(generateString(256));
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).regexpErrorMsgLengthIsExceeded();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRegexpIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue("func: #$afasdg32x10");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue("BLA");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).regexpIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRegexpIsInvalidRegularExpression() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue("regexp: #$afasdg32x10");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue("BLA");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).regexpIsInvalidRegularExpression();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfValidationColumnHasInvalidRegularExpression() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION.getColumnNumber()).setCellValue("regexp: \\\\#$afasdg32x10");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_VALIDATION_ERROR_MESSAGE.getColumnNumber()).setCellValue("BLA");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).validationColumnHasInvalidRegularExpression();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfPhiIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1).createCell(CellName.ITEM_PHI.getColumnNumber())
				.setCellValue("WRONG VALUE");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).phiIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfRequiredIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_REQUIRED.getColumnNumber()).setCellValue("WRONG VALUE");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).requiredIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemDisplayStatusIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DISPLAY_STATUS.getColumnNumber()).setCellValue("SHOW");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY.getColumnNumber())
				.setCellValue("CM001_TXT_INT1,1,MESSAGE!");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemDisplayStatusIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfControlResponseValueIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DISPLAY_STATUS.getColumnNumber()).setCellValue("HIDE");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY.getColumnNumber())
				.setCellValue("CM001_TXT_INT1,345,MESSAGE!");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).controlResponseValueIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfControlItemNameIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DISPLAY_STATUS.getColumnNumber()).setCellValue("HIDE");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY.getColumnNumber())
				.setCellValue("CM001_TXT_INT345,1,MESSAGE!");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).controlItemNameIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfSCDIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DISPLAY_STATUS.getColumnNumber()).setCellValue("HIDE");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_SIMPLE_CONDITIONAL_DISPLAY.getColumnNumber())
				.setCellValue("CM001_TXT_INT1,1");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).simpleConditionalDisplayIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfOntologyNameIsNotValid() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("CODE");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_CODE_REF.getColumnNumber()).setCellValue("WRONG VALUE");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).ontologyNameIsNotValid();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfNeedToUpdateMedicalCodingReferenceItemType() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_DATA_TYPE.getColumnNumber()).setCellValue("INT");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_CODE_REF.getColumnNumber()).setCellValue("MEDDRA");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).needToUpdateMedicalCodingReferenceItemType();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfResponseLabelHasBeenUsedForAnotherResponseType()
			throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_TYPE.getColumnNumber()).setCellValue("single-select");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_LABEL.getColumnNumber()).setCellValue("yyy");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_OPTIONS_TEXT.getColumnNumber()).setCellValue("a,b,c,d,e");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_RESPONSE_VALUES_OR_CALCULATIONS.getColumnNumber()).setCellValue("0,1,2,3,4");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).responseLabelHasBeenUsedForAnotherResponseType();
	}

	@Test
	public void testThatExcelCrfBuilderGeneratesErrorsIfItemOfOneGroupBelongsToMoreThanOneSection() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).createRow(2)
				.createCell(CellName.SECTION_LABEL.getColumnNumber()).setCellValue("section2");
		workbook.getSheetAt(SheetName.SECTIONS.getSheetNumber()).getRow(2)
				.createCell(CellName.SECTION_TITLE.getColumnNumber()).setCellValue("section2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_SECTION_LABEL.getColumnNumber()).setCellValue("section2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(1)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).getRow(2)
				.createCell(CellName.ITEM_GROUP_LABEL.getColumnNumber()).setCellValue("group2");
		excelCrfBuilder = getMockedExcelCrfBuilder(workbook);
		excelCrfBuilder.build();
		assertTrue(excelCrfBuilder.getErrorsList().size() > 0);
		assertTrue(excelCrfBuilder.getErrorsMap().size() > 0);
		Mockito.verify(excelCrfBuilder.getErrorMessageProducer()).itemOfOneGroupBelongsToMoreThanOneSection();
	}

	@Test(expected = CRFReadingException.class)
	public void testThatExcelCrfBuilderGeneratesErrorsIfCrfDoesNotHaveItems() throws Exception {
		Workbook workbook = getWorkbook("testCrf.xls");
		for (int i = 0; i <= 11; i++) {
			workbook.getSheetAt(SheetName.ITEMS.getSheetNumber()).createRow(i);
		}
		excelCrfBuilder = (ExcelCrfBuilder) crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		excelCrfBuilder.build();
	}
}
