package com.clinovo.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.clinovo.model.Status.CodeStatus;

public class CodedItemTest {

	@Test
	public void testThatNewCodedItemHasNotCodedStatus() {
		assertEquals(CodeStatus.NOT_CODED, new CodedItem().getCodeStatus());
	}

	@Test
	public void testThatCodedItemPersistsAChangedStatus() {
		
		CodedItem item = new CodedItem();
		item.setStatus("CODED");
		
		assertEquals(CodeStatus.CODED, item.getCodeStatus());
	}
	
	@Test
	public void testThatIsCodedIsFalseOnNewCodedItem() {
		assertFalse(new CodedItem().isCoded());
	}
	
	@Test
	public void testThatIsCodedReturnsTrueForCodedItem() {
		
		CodedItem item = new CodedItem();
		item.setStatus("CODED");
		
		assertTrue(item.isCoded());
	}
}
