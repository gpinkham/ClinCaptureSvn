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

package org.akaza.openclinica.ws.logic;

import org.akaza.openclinica.bean.managestudy.SubjectTransferBean;

import java.util.ArrayList;
import java.util.List;

public class DatabaseCctsService extends CctsService {

	@Override
	public boolean isSubjectInQueue() {
		// TODO: Implement this
		return true;
	}

	@Override
	public boolean doesSubjectExit() {
		// TODO: Implement this
		return true;
	}

	@Override
	public void createSubject(SubjectTransferBean subjectTransfer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createSubjectTransfer(SubjectTransferBean subjectTransfer) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<SubjectTransferBean> getAllSubjectsInQueue() {
		// TODO Auto-generated method stub
		return new ArrayList<SubjectTransferBean>();
	}

	@Override
	public void removeSubjectFromQueue(SubjectTransferBean subjectTransfer) {
		// TODO Auto-generated method stub

	}

}
