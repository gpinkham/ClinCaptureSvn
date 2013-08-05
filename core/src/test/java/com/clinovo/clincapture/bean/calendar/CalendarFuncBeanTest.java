package com.clinovo.clincapture.bean.calendar;

import static org.junit.Assert.*;

import java.util.Date;

import org.akaza.openclinica.service.calendar.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * User: Denis Date: 07.12.13
 */

public class CalendarFuncBeanTest {
	
	private CalendarFuncBean function;
	private Date testDate;
	
	@Before
	public void before(){
		function = new CalendarFuncBean();
		testDate = new Date();
	}
	
	@After
	public void after(){
		function = null;
	}
	
	@Test
	public void testCalendarFuncBeanGetEventName(){
		assertEquals("",function.getEventName());		
	}
	
	@Test
	public void testCalendarFuncBeanGetDateMax() {
		assertEquals(testDate,function.getDateMax());
	}
	
	@Test
	public void testCalendarFuncBeanGetDateMin() {
		assertEquals(testDate,function.getDateMin());
	}
	
	@Test
	public void testCalendarFuncBeanGetDateEmail() {
		assertEquals(testDate,function.getDateEmail());
	}
	
	@Test
	public void testCalendarFuncBeanGetDateSchedule() {
		assertEquals(testDate,function.getDateSchedule());
	}
	
	@Test
	public void testCalendarFuncBeanGetEventsRV() {
		assertEquals("",function.getEventsReferenceVisit());
	}
}
