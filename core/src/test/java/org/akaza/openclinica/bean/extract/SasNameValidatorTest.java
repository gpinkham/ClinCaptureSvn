/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2015 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.bean.extract;

import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class SasNameValidatorTest {

	@Test
	public void testThatGetValidNameReturnsOriginalItemNameIfItIsUniqueAcrossCRF() {

		// SETUP
		SasNameValidator sasNameValidator = new SasNameValidator();
		TreeSet<String> uniqueItemNamesSet = new TreeSet<String>();
		uniqueItemNamesSet.add("IT_SAE01");
		uniqueItemNamesSet.add("IT_SAE02");
		sasNameValidator.setUniqueNameTable(uniqueItemNamesSet);

		// TEST
		String resultSasItemName = sasNameValidator.getValidName("IT_SAE0314TEXT");

		// VERIFY
		Assert.assertEquals("IT_SAE03", resultSasItemName);
		Assert.assertTrue(uniqueItemNamesSet.contains(resultSasItemName));
		Assert.assertEquals(1, sasNameValidator.getSequential());
	}

	@Test
	public void testThatGetValidNameReturnsCorrectSasItemNameWhenNextSequentialIsOneCharacterString() {

		// SETUP
		SasNameValidator sasNameValidator = new SasNameValidator();
		TreeSet<String> uniqueItemNamesSet = new TreeSet<String>();
		uniqueItemNamesSet.add("IT_SAE01");
		uniqueItemNamesSet.add("IT_SAE02");
		sasNameValidator.setUniqueNameTable(uniqueItemNamesSet);
		sasNameValidator.setSequential(11);

		// TEST
		String resultSasItemName = sasNameValidator.getValidName("IT_SAE0299TEXT");

		// VERIFY
		Assert.assertEquals("IT_SAE0B", resultSasItemName);
		Assert.assertTrue(uniqueItemNamesSet.contains(resultSasItemName));
		Assert.assertEquals(12, sasNameValidator.getSequential());
	}

	@Test
	public void testThatGetValidNameReturnsCorrectSasItemNameWhenNextSequentialIsTwoCharactersString() {

		// SETUP
		SasNameValidator sasNameValidator = new SasNameValidator();
		TreeSet<String> uniqueItemNamesSet = new TreeSet<String>();
		uniqueItemNamesSet.add("IT_SAE01");
		uniqueItemNamesSet.add("IT_SAE02");
		sasNameValidator.setUniqueNameTable(uniqueItemNamesSet);
		sasNameValidator.setSequential(140);

		// TEST
		String resultSasItemName = sasNameValidator.getValidName("IT_SAE0200TEXT");

		// VERIFY
		Assert.assertEquals("IT_SAE3W", resultSasItemName);
		Assert.assertTrue(uniqueItemNamesSet.contains(resultSasItemName));
		Assert.assertEquals(141, sasNameValidator.getSequential());
	}

	@Test
	public void testThatGetValidNameReturnsCorrectSasItemNameWhenNextSequentialIsThreeCharactersString() {

		// SETUP
		SasNameValidator sasNameValidator = new SasNameValidator();
		TreeSet<String> uniqueItemNamesSet = new TreeSet<String>();
		uniqueItemNamesSet.add("IT_SAE01");
		uniqueItemNamesSet.add("IT_SAE02");
		sasNameValidator.setUniqueNameTable(uniqueItemNamesSet);
		sasNameValidator.setSequential(10056);

		// TEST
		String resultSasItemName = sasNameValidator.getValidName("IT_SAE0226TEXT");

		// VERIFY
		Assert.assertEquals("IT_SA7RC", resultSasItemName);
		Assert.assertTrue(uniqueItemNamesSet.contains(resultSasItemName));
		Assert.assertEquals(10057, sasNameValidator.getSequential());
	}
}
