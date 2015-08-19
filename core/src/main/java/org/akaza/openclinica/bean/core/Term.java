/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.core;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Superclass for controlled vocabulary terms like status, role, etc.
 * 
 * @author ssachs
 * 
 */
@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class Term extends EntityBean {

	private ResourceBundle resterm;
	private String description;

	/**
	 * Term constructor.
	 */
	public Term() {
		super();
		setDescription("");
	}

	/**
	 * Term constructor.
	 * 
	 * @param id
	 *            int
	 * @param name
	 *            String
	 */
	public Term(int id, String name) {
		setId(id);
		setName(name);
		setDescription("");
	}

	/**
	 * Term constructor.
	 * 
	 * @param id
	 *            int
	 * @param name
	 *            String
	 * @param description
	 *            String
	 */
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

	/**
	 * Method that compares term objects.
	 * 
	 * @param t
	 *            Term
	 * @return boolean
	 */
	public boolean equals(Term t) {
		return (t != null) && (this.id == t.id);
	}

	@Override
	public int hashCode() {
		return id;
	}

	/**
	 * Method that checks that term id is present in the list.
	 * 
	 * @param id
	 *            int
	 * @param list
	 *            List
	 * @return boolean
	 */
	public static boolean contains(int id, List list) {
		Term t = new Term(id, "");

		for (Term temp : (List<Term>) list) {
			if (temp.equals(t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method that returns term by id from the list.
	 * 
	 * @param id
	 *            int
	 * @param list
	 *            List
	 * @return Term
	 */
	public static Term get(int id, List list) {
		Term t = new Term(id, "");

		for (Term temp : (List<Term>) list) {
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

	public String getNormalizedName() {
		return normalizeString(getName());
	}

	/**
	 * Method makes first symbol in upper case for each word in the passed string.
	 * 
	 * @param value
	 *            String
	 * @return String
	 */
	public static String normalizeString(String value) {
		if (value != null) {
			List<String> wordList = Arrays.asList(value.split("\\s"));
			StringBuilder builder = new StringBuilder();
			for (String word : wordList) {
				if (wordList.indexOf(word) > 0) {
					builder.append(" ");
				}
				builder.append(word.isEmpty() ? " " : (Character.toString(word.charAt(0)).toUpperCase() + word
						.substring(1)));
			}
			value = builder.toString();
		}
		return value;
	}

	public String getCode() {
		return this.name;
	}

}
