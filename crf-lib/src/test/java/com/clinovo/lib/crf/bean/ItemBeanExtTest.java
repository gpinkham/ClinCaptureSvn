package com.clinovo.lib.crf.bean;

import static org.junit.Assert.assertNull;

import java.util.Locale;

import javax.sql.DataSource;

import com.clinovo.service.ItemRenderMetadataService;
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

import com.clinovo.lib.crf.builder.impl.ExcelCrfBuilder;
import com.clinovo.lib.crf.service.ImportCrfService;

@RunWith(MockitoJUnitRunner.class)
public class ItemBeanExtTest {

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
	@Mock
	private ImportCrfService importCrfService;
	@Mock
	private MessageSource messageSource;
	@Mock
	private ItemRenderMetadataService metadataService;

	@Before
	public void before() {
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		Mockito.when(workbook.getSheetAt(Mockito.anyInt())).thenReturn(sheet);
		Mockito.when(row.getCell(Mockito.anyInt())).thenReturn(cell);
		Mockito.when(sheet.getRow(Mockito.anyInt())).thenReturn(row);
	}

	@Test
	public void testDefaultValues() {
		ItemBeanExt itemBeanExt = new ItemBeanExt(new ExcelCrfBuilder(workbook, owner, studyBean, dataSource,
				Locale.ENGLISH, messageSource, importCrfService, metadataService));
		assertNull(itemBeanExt.getSectionBean());
		assertNull(itemBeanExt.getResponseSet());
		assertNull(itemBeanExt.getParentItemBean());
		assertNull(itemBeanExt.getItemGroupBean());
		assertNull(itemBeanExt.getSimpleConditionalDisplayBean());
	}
}
