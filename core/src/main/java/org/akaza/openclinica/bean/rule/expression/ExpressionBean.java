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
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.bean.rule.expression;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class ExpressionBean extends AuditableEntityBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private Context context;
	private String value;
	private String contextName;

	public ExpressionBean() {
		// TODO Auto-generated constructor stub
	}

	public ExpressionBean(Context context, String value) {
		this.context = context;
		this.value = value;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Do not use this method for getting Context name. Use of this is strictly for import validation.
	 * 
	 * @return String
	 */
	public String getContextName() {
		return this.contextName;
	}

	/**
	 * Do not use this method for setting Context name. Use of this is strictly for import validation.
	 * 
	 * @param contextName
	 */
	public void setContextName(String contextName) {
		this.contextName = contextName;
		this.context = Context.getByName(contextName);
	}
}
