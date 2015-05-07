package com.clinovo.rest.odm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.cdisc.ns.odm.v130.FileType;
import org.junit.Test;

public class RestOdmContainerTest {

	@Test
	public void testThatDefaultConstructorSetsCorrectValuesForFields() throws Exception {
		RestOdmContainer restOdmContainer = new RestOdmContainer();
		assertNull(restOdmContainer.getRestData());
	}

	@Test
	public void testThatCollectOdmRootSetsValuesCorrectly() throws Exception {
		RestOdmContainer restOdmContainer = new RestOdmContainer();
		restOdmContainer.collectOdmRoot();
		assertNotNull(restOdmContainer.getFileOID());
		assertEquals(restOdmContainer.getDescription(), "REST Data");
		assertNotNull(restOdmContainer.getCreationDateTime());
		assertEquals(restOdmContainer.getODMVersion(), "1.3");
		assertEquals(restOdmContainer.getFileType(), FileType.SNAPSHOT);
	}
}
