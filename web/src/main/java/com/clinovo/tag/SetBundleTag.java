/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
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
package com.clinovo.tag;

import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import com.clinovo.i18n.LocaleResolver;

/**
 * SetBundleTag.
 */
public class SetBundleTag extends TagSupport {

	private static final String REQUEST = "request";
	private static final String SESSION = "session";
	private static final String APPLICATION = "application";

	private String var;
	protected String basename;
	private int scope = PageContext.PAGE_SCOPE;

	public void setVar(String var) {
		this.var = var;
	}

	public void setBasename(String basename) throws JspTagException {
		this.basename = basename;
	}

	public void setScope(String scope) {
		int result = PageContext.PAGE_SCOPE;
		if (REQUEST.equalsIgnoreCase(scope)) {
			result = PageContext.REQUEST_SCOPE;
		} else if (SESSION.equalsIgnoreCase(scope)) {
			result = PageContext.SESSION_SCOPE;
		} else if (APPLICATION.equalsIgnoreCase(scope)) {
			result = PageContext.APPLICATION_SCOPE;
		}
		this.scope = result;
	}

	private void reset() {
		var = null;
		basename = null;
		scope = PageContext.PAGE_SCOPE;
	}

	public int doEndTag() throws JspException {
		Locale locale = LocaleResolver.getLocale();
		LocalizationContext localizationContext = new LocalizationContext(ResourceBundleProvider.getResBundle(basename,
				locale));
		if (var != null) {
			pageContext.setAttribute(var, localizationContext, scope);
		} else {
			Config.set(pageContext, Config.FMT_LOCALIZATION_CONTEXT, localizationContext, scope);
		}
		reset();
		return EVAL_PAGE;
	}

	public void release() {
		reset();
		super.release();
	}
}