package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

import com.clinovo.model.Widget;

public class WidgetServiceTest extends DefaultAppContextTest {

	@Test
	public void testThatFindAllDoesNotReturnsNull() {
		assertNotNull(widgetService.findAll().size());
	}
	
	@Test
	public void testThatFindAllReturnsAllWidgetsFromDb() {
		assertEquals(3, widgetService.findAll().size());
	}

	@Test
	public void testThatFindAllReturnsWidgetsWithCorrectNames() {
		assertEquals("test-widget-1", widgetService.findAll().get(0).getWidgetName());
	}

	@Test
	public void testThatFindByIdDoesNotReturnsNull() {
		assertNotNull(widgetService.findById(1));
	}

	@Test
	public void testThatFindByIdReturnsWidgetWithCorrectDescription() {
		assertEquals("test description 1", widgetService.findById(1).getDescription());
	}
	
	@Test
	public void testThatFindByIdReturnsWidgetWithWidgetsLayout() {
		assertEquals(1, widgetService.findById(1).getWidgetsLayout().size());
	}

	@Test
	public void testThatFindByChildsIdDoesNotReturnsNull() {
		assertNotNull(widgetService.findByChildsId(1));
	}

	@Test
	public void testThatFindByChildsIdReturnsWidgetWithCorrectName() {
		assertEquals("test description 1", widgetService.findByChildsId(1).getDescription());
	}

	@Test
	public void testThatSaveWidgetPresistsNewWidgetToDb() {
		Widget widget = new Widget();
		widget.setWidgetName("test-widget-4");
		widget.setDescription("Test description 4");
		widget.setDisplayAsDefault("1");
		widget.setHaveAccess("1");
		widget.setSiteMetrics(false);
		widget.setStudyMetrics(true);
		widget.setVersion(0);

		widgetService.saveWidget(widget);

		assertEquals(4, widgetService.findAll().size());
	}
}
