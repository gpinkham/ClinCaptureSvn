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

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "rawtypes", "serial" })
public class EntityAction extends Term {

	public static final EntityAction VIEW = new EntityAction(1, "view");
	public static final EntityAction EDIT = new EntityAction(2, "edit");
	public static final EntityAction DELETE = new EntityAction(3, "delete");
	public static final EntityAction RESTORE = new EntityAction(4, "restore");
	public static final EntityAction DEPLOY = new EntityAction(5, "deploy");

	private static final EntityAction[] members = { VIEW, EDIT, DELETE, RESTORE, DEPLOY };
	public static final List list = Arrays.asList(members);

	private EntityAction(int id, String name) {
		super(id, name);
	}

	private EntityAction() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static EntityAction get(int id) {
		Term term = Term.get(id, list);
		return term instanceof EntityAction ? (EntityAction) term : null;
	}
}
