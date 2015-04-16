package com.clinovo.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.clinovo.util.CrfShortcutsAnalyzer;

public class ItemSDVServiceTest extends DefaultAppContextTest {

	private StudyBean study;
	private UserAccountBean ub;
	private Map<Integer, Boolean> metadata;
	private CrfShortcutsAnalyzer crfShortcutsAnalyzer;

	@Before
	public void setUp() {
		study = new StudyBean();
		study.setId(1);
		ub = new UserAccountBean();
		ub.setId(1);
		metadata = new HashMap<Integer, Boolean>();
		metadata.put(1, true);
		crfShortcutsAnalyzer = Mockito.mock(CrfShortcutsAnalyzer.class);
	}

	@Test
	public void testThatGetCountOfItemsToSDVReturnsTrue() {
		assertEquals(itemSDVService.getCountOfItemsToSDV(1), 0);
	}

	@Test
	public void testThatGetListOfItemsToSDVReturnsCorrectSize() {
		assertEquals(itemSDVService.getListOfItemsToSDV(1).size(), 0);
	}

	@Test
	public void testThatTransactionalSDVCrfItemsDoesNotThrowAnException() throws Exception {
		Connection con = getDataSource().getConnection();
		con.setAutoCommit(false);
		itemSDVService.sdvCrfItems(1, ub.getId(), true, con);
		con.commit();
	}

	@Test
	public void testThatSDVCrfItemsReturnsTrue() {
		assertTrue(itemSDVService.sdvCrfItems(1, ub.getId(), true));
	}

	@Test
	public void testThatSDVItemsReturnsNotEmptyValue() throws Exception {
		String jsonObject = itemSDVService.sdvItem(1, 1, 1, "sdv", ub, crfShortcutsAnalyzer);
		assertFalse(jsonObject.isEmpty());
	}

	@Test
	public void testThatHasItemsToSDVReturnsFalse() throws Exception {
		assertFalse(itemSDVService.hasItemsToSDV(1));
	}

	@Test
	public void testThatResetSDVForItemsDoesNotThrowAnException() throws Exception {
		itemSDVService.resetSDVForItems(new ArrayList<DisplayItemBean>(), ub);
	}

	@Test
	public void testThatProcessChangedCrfVersionMetadataDoesNotThrowAnException() throws Exception {
		itemSDVService.processChangedCrfVersionMetadata(study, ub, 1, metadata);
	}

	@Test
	public void testThatCopySettingsFromPreviousVersionMovesItemLevelSDVSettingsCorrectly() throws Exception {
		ItemFormMetadataBean imfBean = imfdao.findByItemIdAndCRFVersionId(25, 1);
		imfBean.setSdvRequired(true);
		imfdao.update(imfBean);
		imfBean = imfdao.findByItemIdAndCRFVersionId(33, 2);
		imfBean.setSdvRequired(false);
		imfdao.update(imfBean);
		itemSDVService.copySettingsFromPreviousVersion(1, 2);
		imfBean = imfdao.findByItemIdAndCRFVersionId(33, 2);
		assertTrue(imfBean.isSdvRequired());
	}
}
