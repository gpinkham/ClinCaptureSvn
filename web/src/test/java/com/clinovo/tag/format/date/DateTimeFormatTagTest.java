package com.clinovo.tag.format.date;

import com.clinovo.i18n.LocaleResolver;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.util.Date;
import java.util.Locale;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LocaleResolver.class })
public class DateTimeFormatTagTest {

	private DateTimeFormatTag dateTimeFormatTag;

	@Mock
	private PageContext pageContext;

	@Mock
	private JspWriter jspWriter;

	private DateTimeZone jvmTimeZone = DateTimeZone.getDefault();

	@Before
	public void setUp() throws Exception {

		Locale locale = Locale.ENGLISH;
		ResourceBundleProvider.updateLocale(locale);
		dateTimeFormatTag = new DateTimeFormatTag();
		dateTimeFormatTag.setPageContext(pageContext);
		Mockito.when(pageContext.getOut()).thenReturn(jspWriter);

		PowerMockito.mockStatic(LocaleResolver.class);
		PowerMockito.when(LocaleResolver.getLocale()).thenReturn(locale);
	}

	@After
	public void restoreDefault() {
		DateTimeZone.setDefault(jvmTimeZone);
	}

	@Test
	public void testThatDoEndTagMakesCorrectTranslationBetweenTimeZones_1() throws Exception {

		// SETUP
		DateTimeZone.setDefault(DateTimeZone.forID("America/Chihuahua"));
		Date dateToTranslate = new Date(1427284953000L); // 25th March 2015 12:02:33 GMT
		dateTimeFormatTag.setValue(dateToTranslate);
		dateTimeFormatTag.setPattern("dd-MMM-yyyy HH:mm:ss");
		dateTimeFormatTag.setDateTimeZone("Europe/Helsinki");

		// TEST
		dateTimeFormatTag.doEndTag();

		// VERIFY
		// As of 25th March 2015 12:02:33 GMT
		// time zone "America/Chihuahua" has offset -07:00
		// and time zone "Europe/Helsinki" has offset +02:00.
		// result of translation "America/Chihuahua" -> "Europe/Helsinki" must be equal to 25th March 2015 14:02:33
		Mockito.verify(jspWriter).print("25-Mar-2015 14:02:33");
	}

	@Test
	public void testThatDoEndTagMakesCorrectTranslationBetweenTimeZones_2() throws Exception {

		// SETUP
		DateTimeZone.setDefault(DateTimeZone.forID("America/Chihuahua"));
		Date dateToTranslate = new Date(1431348770000L); // 11th May 2015 12:52:50 GMT
		dateTimeFormatTag.setValue(dateToTranslate);
		dateTimeFormatTag.setPattern("dd-MMM-yyyy HH:mm:ss");
		dateTimeFormatTag.setDateTimeZone("Europe/Helsinki");

		// TEST
		dateTimeFormatTag.doEndTag();

		// VERIFY
		// As of 11th May 2015 12:52:50 GMT
		// time zone "America/Chihuahua" has offset -06:00
		// and time zone "Europe/Helsinki" has offset +03:00.
		// result of translation "America/Chihuahua" -> "Europe/Helsinki" must be equal to 11th May 2015 15:52:50
		Mockito.verify(jspWriter).print("11-May-2015 15:52:50");
	}

	@Test
	public void testThatDoEndTagMakesCorrectTranslationBetweenTimeZones_3() throws Exception {

		// SETUP
		DateTimeZone.setDefault(DateTimeZone.forID("America/Chihuahua"));
		Date dateToTranslate = new Date(1427284953000L); // 25th March 2015 12:02:33 GMT
		dateTimeFormatTag.setValue(dateToTranslate);
		dateTimeFormatTag.setPattern("dd-MMM-yyyy HH:mm:ss");
		dateTimeFormatTag.setDateTimeZone("Asia/Muscat");

		// TEST
		dateTimeFormatTag.doEndTag();

		// VERIFY
		// As of 25th March 2015 12:02:33 GMT
		// time zone "America/Chihuahua" has offset -07:00.
		// Time zone "Asia/Muscat" has constant offset +04:00.
		// result of translation "America/Chihuahua" -> "Asia/Muscat" must be equal to 25th March 2015 16:02:33
		Mockito.verify(jspWriter).print("25-Mar-2015 16:02:33");
	}

	@Test
	public void testThatDoEndTagMakesCorrectTranslationBetweenTimeZones_4() throws Exception {

		// SETUP
		DateTimeZone.setDefault(DateTimeZone.forID("America/Chihuahua"));
		Date dateToTranslate = new Date(1431348770000L); // 11th May 2015 12:52:50 GMT
		dateTimeFormatTag.setValue(dateToTranslate);
		dateTimeFormatTag.setPattern("dd-MMM-yyyy HH:mm:ss");
		dateTimeFormatTag.setDateTimeZone("Asia/Muscat");

		// TEST
		dateTimeFormatTag.doEndTag();

		// VERIFY
		// As of 11th May 2015 12:52:50 GMT
		// time zone "America/Chihuahua" has offset -06:00.
		// Time zone "Asia/Muscat" has constant offset +04:00.
		// result of translation "America/Chihuahua" -> "Asia/Muscat" must be equal to 11th May 2015 16:52:50
		Mockito.verify(jspWriter).print("11-May-2015 16:52:50");
	}

	@Test
	public void testThatDoEndTagMakesCorrectTranslationBetweenTimeZones_5() throws Exception {

		// SETUP
		DateTimeZone.setDefault(DateTimeZone.forID("America/Chihuahua"));
		Date dateToTranslate = new Date(1431348770000L); // 11th May 2015 12:52:50 GMT
		dateTimeFormatTag.setValue(dateToTranslate);
		dateTimeFormatTag.setDateTimeZone("Asia/Muscat");

		// TEST
		dateTimeFormatTag.doEndTag();

		// VERIFY
		// As of 11th May 2015 12:52:50 GMT
		// time zone "America/Chihuahua" has offset -06:00.
		// Time zone "Asia/Muscat" has constant offset +04:00.
		// result of translation "America/Chihuahua" -> "Asia/Muscat" must be equal to 11th May 2015 16:52:50
		Mockito.verify(jspWriter).print("11-May-2015");
	}

	@Test
	public void testThatDoEndTagMakesCorrectTranslationBetweenTimeZones_6() throws Exception {

		// SETUP
		DateTimeZone.setDefault(DateTimeZone.forID("America/Chihuahua"));
		Date dateToTranslate = new Date(1431348770000L); // 11th May 2015 12:52:50 GMT
		dateTimeFormatTag.setValue(dateToTranslate);
		dateTimeFormatTag.setPattern("dd-MMM-yyyy HH:mm:ss");

		// TEST
		dateTimeFormatTag.doEndTag();

		// VERIFY
		// As of 11th May 2015 12:52:50 GMT
		// time zone "America/Chihuahua" has offset -06:00.
		// Target time zone was not set, thus date must remain in the JVM time zone
		Mockito.verify(jspWriter).print("11-May-2015 06:52:50");
	}
}
