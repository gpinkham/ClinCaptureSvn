/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVOâ€™S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.tag.format.date;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.clinovo.i18n.LocaleResolver;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * The date and time formatting tag.
 */
@SuppressWarnings("serial")
public class DateTimeFormatTag extends TagSupport {

	private static final String REQUEST = "request";

	private static final String SESSION = "session";

	private static final String APPLICATION = "application";

	private Object value;

	private String pattern;

	private String style;

	private DateTimeZone dateTimeZone;

	private String var;

	private int scope;

	public DateTimeFormatTag() {
		super();
		init();
	}

	private void init() {
		var = null;
		value = null;
		pattern = null;
		style = null;
		dateTimeZone = null;
		scope = PageContext.PAGE_SCOPE;
	}

	public void setValue(Object value) throws JspTagException {
		this.value = value;
	}

	public void setStyle(String style) throws JspTagException {
		this.style = style;
	}

	public void setPattern(String pattern) throws JspTagException {
		this.pattern = pattern;
	}

	public void setDateTimeZone(Object tz) throws JspTagException {

		if (tz == null || tz instanceof String && ((String) tz).trim().isEmpty()) {
			this.dateTimeZone = DateTimeZone.getDefault();
		} else if (tz instanceof DateTimeZone) {
			this.dateTimeZone = (DateTimeZone) tz;
		} else {
			try {
				this.dateTimeZone = DateTimeZone.forID((String) tz);
			} catch (Exception exception) {
				throw new JspTagException(exception.toString(), exception);
			}
		}
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setScope(String scope) {

		this.scope = PageContext.PAGE_SCOPE;
		if (REQUEST.equalsIgnoreCase(scope)) {
			this.scope = PageContext.REQUEST_SCOPE;
		} else if (SESSION.equalsIgnoreCase(scope)) {
			this.scope = PageContext.SESSION_SCOPE;
		} else if (APPLICATION.equalsIgnoreCase(scope)) {
			this.scope = PageContext.APPLICATION_SCOPE;
		}
	}

	@Override
	public int doEndTag() throws JspException {

		if (value == null) {
			if (var != null) {
				pageContext.removeAttribute(var, scope);
			}
			return EVAL_PAGE;
		}

		// setup formatter
		DateTimeFormatter formatter;
		if (pattern != null) {
			formatter = DateTimeFormat.forPattern(pattern);
		} else if (style != null) {
			formatter = DateTimeFormat.forStyle(style);
		} else {
			formatter = DateTimeFormat.forPattern(ResourceBundleProvider.getResFormat("date_format_string"));
		}
		formatter = formatter.withLocale(LocaleResolver.getLocale()).withZone(dateTimeZone);

		// format value
		String formatted;
		if (value instanceof ReadableInstant) {
			formatted = formatter.print((ReadableInstant) value);
		} else if (value instanceof ReadablePartial) {
			formatted = formatter.print((ReadablePartial) value);
		} else if (value instanceof java.util.Date) {
			formatted = formatter.print(((java.util.Date) value).getTime());
		} else {
			throw new JspException("value attribute of format tag must be a ReadableInstant, ReadablePartial or java.util.Date,"
					+ " was: " + value.getClass().getName());
		}

		if (var != null) {
			pageContext.setAttribute(var, formatted, scope);
		} else {
			try {
				pageContext.getOut().print(formatted);
			} catch (IOException ioe) {
				throw new JspTagException(ioe.toString(), ioe);
			}
		}
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		init();
	}
}


