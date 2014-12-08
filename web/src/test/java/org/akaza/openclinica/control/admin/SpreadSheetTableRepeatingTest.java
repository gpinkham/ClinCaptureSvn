package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class SpreadSheetTableRepeatingTest {

	private SpreadSheetTableRepeating spreadSheetTableRepeating;

	@Before
	public void setUp() throws OpenClinicaException {
		spreadSheetTableRepeating = Mockito.mock(SpreadSheetTableRepeating.class);
		Mockito.when(spreadSheetTableRepeating.stripQuotes(Mockito.anyString())).thenCallRealMethod();
	}

	@Test
	public void testStripQuotes() throws InsufficientPermissionException {
		String text = "~,123123'\\";
		String expectedResult = "~,123123''\\\\";
		assertEquals(expectedResult, spreadSheetTableRepeating.stripQuotes(text));
	}

}
