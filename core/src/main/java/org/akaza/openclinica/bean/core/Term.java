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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author ssachs
 * 
 *         Superclass for controlled vocabulary terms like status, role, etc.
 */
public class Term extends EntityBean {

	Locale locale;
	ResourceBundle resterm;
	protected String description;

	public Term() {
		super();
	}

	public Term(int id, String name) {
		setId(id);
		setName(name);
		setDescription("");

	}

	public Term(int id, String name, String description) {
		setId(id);
		setName(name);
		setDescription(description);
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		if (description != null && !"".equals(description)) {
			resterm = ResourceBundleProvider.getTermsBundle();
			return resterm.getString(description).trim();
		} else {
			return null;
		}
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public boolean equals(Term t) {
		return (t != null) && (this.id == t.id);
	}

	@Override
	public int hashCode() {
		return id;
	}

	public static boolean contains(int id, List list) {
		Term t = new Term(id, "");

		for (int i = 0; i < list.size(); i++) {
			Term temp = (Term) list.get(i);
			if (temp.equals(t)) {
				return true;
			}
		}
		return false;
	}

	public static Term get(int id, List list) {
		Term t = new Term(id, "");

		for (int i = 0; i < list.size(); i++) {
			Term temp = (Term) list.get(i);
			if (temp.equals(t)) {
				return temp;
			}
		}

		return new Term();
	}

	@Override
	public String getName() {
		resterm = ResourceBundleProvider.getTermsBundle();
		if (name != null && resterm.containsKey(name)) {
			return resterm.getString(name).trim();
		} else {
			return "";
		}
	}

	// clinovo - start (ticket #50)
	public String getCode() {
		return this.name;
	}
	// clinovo - end

	// TODO
	/*
	 * public String getLocalizedName() { locale = LocaleProvider.getLocale(); resterm=
	 * ResourceBundle.getBundle("org.akaza.openclinica.i18n.terms",locale); return resterm.getString(this.name); }
	 */

}
