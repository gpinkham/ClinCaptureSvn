package com.clinovo.lib.crf.factory.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
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

import com.clinovo.lib.crf.builder.CrfBuilder;
import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;
import com.clinovo.lib.crf.builder.impl.JsonCrfBuilder;
import com.clinovo.lib.crf.factory.CrfBuilderFactory;
import com.clinovo.lib.crf.service.ImportCrfService;

@RunWith(MockitoJUnitRunner.class)
public class CrfBuilderFactoryImplTest {

	@Mock
	private Row row;
	@Mock
	private Cell cell;
	@Mock
	private Sheet sheet;
	@Mock
	private StudyBean study;
	@Mock
	private Workbook workbook;
	@Mock
	private StudyBean studyBean;
	@Mock
	private DataSource dataSource;
	@Mock
	private UserAccountBean owner;
	@Mock
	private ImportCrfService importCrfService;

	private CrfBuilderFactory crfBuilderFactory;

	@Mock
	private MessageSource messageSource;

	@Before
	public void before() {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		crfBuilderFactory = new CrfBuilderFactoryImpl();
		Mockito.when(workbook.getSheetAt(Mockito.anyInt())).thenReturn(sheet);
		Mockito.when(row.getCell(Mockito.anyInt())).thenReturn(cell);
		Mockito.when(sheet.getRow(Mockito.anyInt())).thenReturn(row);
	}

	@Test
	public void testThatCrfBuilderFactoryCreatesExcelCrfBuilderCorrectly() throws Exception {
		CrfBuilder crfBuilder = crfBuilderFactory.getCrfBuilder(workbook, studyBean, owner, Locale.ENGLISH,
				messageSource);
		assertNotNull(crfBuilder);
		assertTrue(crfBuilder instanceof ExcelCrfBuilder);
	}

	@Test
	public void testThatCrfBuilderFactoryCreatesJsonCrfBuilderCorrectly() throws Exception {
		CrfBuilder crfBuilder = crfBuilderFactory.getCrfBuilder("{}", studyBean, owner, Locale.ENGLISH, messageSource);
		assertNotNull(crfBuilder);
		assertTrue(crfBuilder instanceof JsonCrfBuilder);
	}
}
