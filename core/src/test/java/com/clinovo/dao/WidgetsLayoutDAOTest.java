package com.clinovo.dao;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

import com.clinovo.model.WidgetsLayout;

public class WidgetsLayoutDAOTest extends DefaultAppContextTest{
	
	@Test
	public void testThatFindAllByStudyIdAndUserIdDoesNotReturnsNull() {
		assertNotNull(widgetsLayoutDAO.findAllByStudyIdAndUserId(1, 1));
	}

	@Test
	public void testThatFindAllByStudyIdAndUserIdReturnsAllWidgetsLayoutsFromDb() {
		assertEquals(3, widgetsLayoutDAO.findAllByStudyIdAndUserId(1, 1).size());
	}

	@Test
	public void testThatFindByWidgetIdAndStudyIdAndUserIdDoesNotReturnsNull() {
		assertNotNull(widgetsLayoutDAO.findByWidgetIdAndStudyIdAndUserId(1, 1, 1));
	}

	@Test
	public void testThatFindByWidgetIdAndStudyIdAndUserIdDoesNotReturnsWidgetLayoutWithCorrectOrdinal() {
		assertEquals(0, widgetsLayoutDAO.findByWidgetIdAndStudyIdAndUserId(1, 1, 1).getOrdinal());
	}

	@Test
	public void testThatFindByIdDoesNotReturnNull() {
		assertNotNull(widgetsLayoutDAO.findById(1));
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

		widgetsLayoutDAO.saveLayout(listOfWidgetsLayout);

		assertEquals(5, widgetsLayoutDAO.findAllByStudyIdAndUserId(1, 1).size());
	}
}
