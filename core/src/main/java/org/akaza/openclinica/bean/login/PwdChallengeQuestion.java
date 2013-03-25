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
package org.akaza.openclinica.bean.login;

import org.akaza.openclinica.bean.core.Term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jxu
 * @version CVS: $Id: PwdChallengeQuestion.java 9771 2007-08-28 15:26:26Z thickerson $
 * 
 */

// Internationalized name and description in Term.getName and
// Term.getDescription()
public class PwdChallengeQuestion extends Term {
	
	private static final long serialVersionUID = -3257460938361987325L;

	public static final PwdChallengeQuestion MAIDEN_NAME = new PwdChallengeQuestion(1, "Mother_Maiden_Name");

	public static final PwdChallengeQuestion FARORITE_PET = new PwdChallengeQuestion(2, "Favorite_Pet");

	public static final PwdChallengeQuestion CITY_OF_BIRTH = new PwdChallengeQuestion(3, "City_of_Birth");

	private static final PwdChallengeQuestion[] members = { MAIDEN_NAME, FARORITE_PET, CITY_OF_BIRTH };

	public static final List<PwdChallengeQuestion> list = Arrays.asList(members);

	private PwdChallengeQuestion(int id, String name) {
		super(id, name);
	}

	private PwdChallengeQuestion() {
	}

	public static boolean contains(int id) {
		return Term.contains(id, list);
	}

	public static PwdChallengeQuestion get(int id) {
		return (PwdChallengeQuestion) Term.get(id, list);
	}

	public static ArrayList<PwdChallengeQuestion> toArrayList() {
		return new ArrayList<PwdChallengeQuestion>(list);
	}

}
