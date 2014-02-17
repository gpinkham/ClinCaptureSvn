package com.clinovo.service;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;


import com.clinovo.model.WidgetsLayout;

public class WidgetsLayotServiceTest extends DefaultAppContextTest {

	@Test
	public void testThatFindAllByStudyIdAndUserIdDoesNotReturnsNull() {
		assertNotNull(widgetsLayoutService.findAllByStudyIdAndUserId(1, 1));
	}

	@Test
	public void testThatFindAllByStudyIdAndUserIdReturnsAllWidgetsLayoutsFromTheDb() {
		assertEquals(3, widgetsLayoutService.findAllByStudyIdAndUserId(1, 1).size());
	}

	@Test
	public void testThatFindByWidgetIdAndStudyIdAndUserIdDoesNotReturnsNull() {
		assertNotNull(widgetsLayoutService.findByWidgetIdAndStudyIdAndUserId(1, 1, 1));
	}

	@Test
	public void testThatByWidgetIdAndStudyIdAndUserIdReturnsWidgetsLayoutWithCorrectOrdinal() {
		assertEquals(0, widgetsLayoutService.findByWidgetIdAndStudyIdAndUserId(1, 1, 1).getOrdinal());
	}

	@Test
	public void testThatSaveLayoutPersistsAllNewWidgetsLayoutsToDb() {
		WidgetsLayout widgetsLayout1 = new WidgetsLayout();
		WidgetsLayout widgetsLayout2 = new WidgetsLayout();

		widgetsLayout1.setOrdinal(1);
		widgetsLayout2.setOrdinal(2);
		widgetsLayout1.setStudyId(1);
		widgetsLayout2.setStudyId(1);
		widgetsLayout1.setUserId(1);
		widgetsLayout2.setUserId(1);
		widgetsLayout1.setVersion(0);
		widgetsLayout2.setVersion(0);

		List<WidgetsLayout> listOfWidgetsLayout = new ArrayList<WidgetsLayout>();
		listOfWidgetsLayout.add(widgetsLayout1);
		listOfWidgetsLayout.add(widgetsLayout2);

		widgetsLayoutService.saveLayout(listOfWidgetsLayout);
		assertEquals(5, widgetsLayoutService.findAllByStudyIdAndUserId(1, 1).size());
	}
}
