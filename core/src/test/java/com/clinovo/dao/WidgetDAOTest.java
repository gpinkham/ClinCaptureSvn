package com.clinovo.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

import com.clinovo.model.Widget;

public class WidgetDAOTest extends DefaultAppContextTest {

	@Test
	public void testThatFindAllDoesNotReturnNull() {
		assertNotNull(widgetDAO.findAll());
	}

	@Test
	public void testThatFindAllReturnsAllTheWidgetsFromTheDb() {
		assertEquals(3, widgetDAO.findAll().size());
	}

	@Test
	public void testThatFindByIdDoesNotReturnNull() {
		assertNotNull(widgetDAO.findById(1));
	}

	@Test
	public void testThatFindByIdReturnsWidgetWithAllWidgetLayout() {
		assertEquals(1, widgetDAO.findById(1).getWidgetsLayout().size());
	}

	@Test
	public void testThatFindByIdReturnsWidgetWithCorrectName() {
		assertEquals("test-widget-1", widgetDAO.findById(1).getWidgetName());
	}

	@Test
	public void testThatFindByIdReturnsWidgetWithCorrectDescription() {
		assertEquals("test description 1", widgetDAO.findById(1).getDescription());
	}

	@Test
	public void testThatFindByChildDoesNotReturnsNull() {
		assertNotNull(widgetDAO.findByChildsId(1));
	}

	@Test
	public void testThatFindByChildIdReturnsWidgetWithCorrectDescription() {
		assertEquals("test description 2", widgetDAO.findByChildsId(2).getDescription());
	}

	@Test
	public void testThatFindByChildIdReturnsWidgetWithAllWidgetLayout() {
		assertEquals(1, widgetDAO.findByChildsId(1).getWidgetsLayout().size());
	}

	@Test
	public void testThatSaveOrUpdatePersistsANewWidget() {

        Widget widget = new Widget();

		widget.setId(4);
		widget.setWidgetName("test-widget-4");
		widget.setDescription("test desription 4");
		widget.setSiteMetrics(false);
		widget.setStudyMetrics(true);
		widget.setDisplayAsDefault("1");
		widget.setHaveAccess("1");

		widgetDAO.saveOrUpdate(widget);

		assertEquals(4, widgetDAO.findAll().size());
	}
}
