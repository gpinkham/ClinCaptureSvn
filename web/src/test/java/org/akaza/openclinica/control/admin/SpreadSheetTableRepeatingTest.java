package org.akaza.openclinica.control.admin;

import static org.junit.Assert.assertEquals;

import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SpreadSheetTableRepeatingTest {

	private SpreadSheetTableRepeating spreadSheetTableRepeating;

	@Before
	public void setUp() throws OpenClinicaException {
		spreadSheetTableRepeating = Mockito.mock(SpreadSheetTableRepeating.class);
		Mockito.when(spreadSheetTableRepeating.stripQuotes(Mockito.anyString())).thenCallRealMethod();
		Mockito.when(spreadSheetTableRepeating.stripQuotes(Mockito.anyString(), Mockito.anyBoolean()))
				.thenCallRealMethod();
	}

	@Test
	public void testStripQuotes() throws InsufficientPermissionException {
		String text = "~,123123'\\";
		String expectedResult = "~,123123''\\\\";
		assertEquals(expectedResult, spreadSheetTableRepeating.stripQuotes(text));
	}

	@Test
	public void testHardStripQuotes() throws InsufficientPermissionException {
		String text = "a, 1, this is a\\, test\\, of \\\\\\'#\\$\\\\%\\\\@com\\\\'ma\\\\\"s";
		String expectedResult = "a, 1, this is a\\\\, test\\\\, of \\\\\\\\\\\\''#\\\\$\\\\\\\\%\\\\\\\\@com\\\\\\\\''ma\\\\\\\\\"s";
		assertEquals(expectedResult, spreadSheetTableRepeating.stripQuotes(text));
	}

}
