package com.clinovo.util;

import com.clinovo.enums.eventdefenition.ReminderEmailRecipient;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

public class CollectionUtilTest {

	@Test
	public void testThatCollectionToStringReturnsCorrectStringForCollectionOfEnumConstants() {

		// SETUP
		Set<ReminderEmailRecipient> enumSet = new LinkedHashSet<ReminderEmailRecipient>();
		enumSet.add(ReminderEmailRecipient.DATA_ENTRY_USER);
		enumSet.add(ReminderEmailRecipient.OTHER_STUDY_USERS);
		enumSet.add(ReminderEmailRecipient.FACILITY_CONTACT);

		// TEST
		String serialized = CollectionUtil.collectionToString(enumSet);

		// VERIFY
		Assert.assertEquals("DATA_ENTRY_USER,OTHER_STUDY_USERS,FACILITY_CONTACT", serialized);
	}

	@Test
	public void testThatCollectionToStringReturnsCorrectStringForCollectionOfStrings() {

		// SETUP
		Set<String> stringSet = new LinkedHashSet<String>();
		stringSet.add("admin");
		stringSet.add("monitor");
		stringSet.add("coder");

		// TEST
		String serialized = CollectionUtil.collectionToString(stringSet);

		// VERIFY
		Assert.assertEquals("admin,monitor,coder", serialized);
	}

	@Test
	public void testThatStringToEnumSetReturnsCorrectSetOfEnumConstantsParsedFromInputString() {

		// SETUP
		String serialized = " DATA_ENTRY_USER, OTHER_STUDY_USERS, FACILITY_CONTACT ";

		// TEST
		Set<ReminderEmailRecipient> enumSet = CollectionUtil.stringToEnumSet(ReminderEmailRecipient.class, serialized);

		// VERIFY
		Assert.assertEquals(3, enumSet.size());
		Assert.assertTrue(enumSet.contains(ReminderEmailRecipient.DATA_ENTRY_USER));
		Assert.assertTrue(enumSet.contains(ReminderEmailRecipient.FACILITY_CONTACT));
		Assert.assertTrue(enumSet.contains(ReminderEmailRecipient.OTHER_STUDY_USERS));
	}

	@Test
	public void testThatStringToEnumSetReturnsCorrectSetOfEnumConstantsParsedFromInputStringExcludingEmptyItems() {

		// SETUP
		String serialized = "DATA_ENTRY_USER,   , FACILITY_CONTACT, ,";

		// TEST
		Set<ReminderEmailRecipient> enumSet = CollectionUtil.stringToEnumSet(ReminderEmailRecipient.class, serialized);

		// VERIFY
		Assert.assertEquals(2, enumSet.size());
		Assert.assertTrue(enumSet.contains(ReminderEmailRecipient.DATA_ENTRY_USER));
		Assert.assertTrue(enumSet.contains(ReminderEmailRecipient.FACILITY_CONTACT));
	}

	@Test
	public void testThatStringToEnumSetReturnsEmptySetIfInputStringIsEmpty() {

		// TEST
		Set<ReminderEmailRecipient> enumSet = CollectionUtil.stringToEnumSet(ReminderEmailRecipient.class, "    ");

		// VERIFY
		Assert.assertTrue(enumSet != null);
		Assert.assertEquals(0, enumSet.size());
	}

	@Test
	public void testThatStringToEnumSetReturnsEmptySetIfInputStringIsNull() {

		// TEST
		Set<ReminderEmailRecipient> enumSet = CollectionUtil.stringToEnumSet(ReminderEmailRecipient.class, null);

		// VERIFY
		Assert.assertTrue(enumSet != null);
		Assert.assertEquals(0, enumSet.size());
	}

	@Test
	public void testThatStringToStringSetReturnsCorrectSetOfStringsParsedFromInputString() {

		// SETUP
		String serialized = " admin, coder, site_monitor ";

		// TEST
		Set<String> stringSet = CollectionUtil.stringToStringSet(serialized);

		// VERIFY
		Assert.assertEquals(3, stringSet.size());
		Assert.assertTrue(stringSet.contains("admin"));
		Assert.assertTrue(stringSet.contains("coder"));
		Assert.assertTrue(stringSet.contains("site_monitor"));
	}

	@Test
	public void testThatStringToStringSetReturnsCorrectSetOfStringsParsedFromInputStringExcludingEmptyItems() {

		// SETUP
		String serialized = ",admin, coder, , site_monitor";

		// TEST
		Set<String> stringSet = CollectionUtil.stringToStringSet(serialized);

		// VERIFY
		Assert.assertEquals(3, stringSet.size());
		Assert.assertTrue(stringSet.contains("admin"));
		Assert.assertTrue(stringSet.contains("coder"));
		Assert.assertTrue(stringSet.contains("site_monitor"));
	}

	@Test
	public void testThatStringToStringSetReturnsEmptySetIfInputStringIsEmpty() {

		// TEST
		Set<String> stringSet = CollectionUtil.stringToStringSet("    ");

		// VERIFY
		Assert.assertTrue(stringSet != null);
		Assert.assertEquals(0, stringSet.size());
	}

	@Test
	public void testThatStringToStringSetReturnsEmptySetIfInputStringIsNull() {

		// TEST
		Set<String> stringSet = CollectionUtil.stringToStringSet(null);

		// VERIFY
		Assert.assertTrue(stringSet != null);
		Assert.assertEquals(0, stringSet.size());
	}
}
