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

package org.akaza.openclinica.bean.extract;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ResourceBundleProvider.class)
@SuppressWarnings("unused")
public class DownloadDiscrepancyNoteTest {

	private DownloadDiscrepancyNote downloadDiscrepancyNote;
	private List<DiscrepancyNoteThread> testDNThreadsList;
	private String testExportFileBodyContent;

	@Before
	public void setUp() {

		// first obtaining all the required Resource Bundle instances for tests,
		// then stubbing all the static methods of the ResourceBundleProvider class
		ResourceBundleProvider.updateLocale(Locale.ENGLISH);
		ResourceBundle resTerm = ResourceBundleProvider.getTermsBundle();
		ResourceBundle resWord = ResourceBundleProvider.getWordsBundle();
		PowerMockito.mockStatic(ResourceBundleProvider.class);
		PowerMockito.when(ResourceBundleProvider.getTermsBundle()).thenReturn(resTerm);
		PowerMockito.when(ResourceBundleProvider.getWordsBundle()).thenReturn(resWord);

		downloadDiscrepancyNote = new DownloadDiscrepancyNote(Locale.ENGLISH, DateTimeZone.getDefault().getID());
		testDNThreadsList = getTestDNThreadsList();

		testExportFileBodyContent = getTestExportFileBodyContent();
	}

	private List<DiscrepancyNoteThread> getTestDNThreadsList() {

		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setLabel("SS-001");
		studySubject.setStatus(Status.AVAILABLE);

		StudyBean study = new StudyBean();
		study.setId(1);
		study.setOid("S_TESTSTUDY");

		UserAccountBean assignedUser = new UserAccountBean();
		assignedUser.setName("test_crc");

		// Creating first test DN thread

		DiscrepancyNoteBean dnParent = new DiscrepancyNoteBean();
		dnParent.setId(10);
		dnParent.setStudySub(studySubject);
		dnParent.setStudy(study);
		dnParent.setCreatedDateString("10-Jun-2014");
		dnParent.setUpdatedDateString("10-Jun-2014");
		dnParent.setAge(25);
		dnParent.setDays(25);
		dnParent.setDisType(DiscrepancyNoteType.QUERY);
		dnParent.setResolutionStatusId(ResolutionStatus.OPEN.getId());
		dnParent.setEventName("Screening");
		dnParent.setCrfName("Enrollment");
		dnParent.setCrfStatus(Status.DATA_ENTRY_STARTED.getName());
		dnParent.setEntityName("DSSTDAT");
		dnParent.setEntityValue("10-Jul-2014");
		dnParent.setDescription("Date cannot be in the future. Please update or clarify.");
		dnParent.setAssignedUser(assignedUser);
		dnParent.setStudyId(study.getId());

		DiscrepancyNoteBean dnChild = new DiscrepancyNoteBean();
		dnChild.setId(11);
		dnChild.setStudySub(studySubject);
		dnChild.setStudy(study);
		dnChild.setParentDnId(dnParent.getId());
		dnChild.setCreatedDateString("10-Jun-2014");
		dnChild.setUpdatedDateString("10-Jun-2014");
		dnChild.setDisType(DiscrepancyNoteType.QUERY);
		dnChild.setResolutionStatusId(ResolutionStatus.OPEN.getId());
		dnChild.setEventName("Screening");
		dnChild.setCrfName("Enrollment");
		dnChild.setCrfStatus(Status.DATA_ENTRY_STARTED.getName());
		dnChild.setEntityName("DSSTDAT");
		dnChild.setEntityValue("10-Jul-2014");
		dnChild.setDescription("Date cannot be in the future. Please update or clarify.");
		dnChild.setAssignedUser(assignedUser);
		dnChild.setStudyId(study.getId());

		List<DiscrepancyNoteBean> thread = new LinkedList<DiscrepancyNoteBean>();
		thread.add(dnParent);
		thread.add(dnChild);

		DiscrepancyNoteThread dnThread = new DiscrepancyNoteThread();
		dnThread.setLinkedNoteList((LinkedList<DiscrepancyNoteBean>) thread);

		List<DiscrepancyNoteThread> dnThreadsList = new ArrayList<DiscrepancyNoteThread>();
		dnThreadsList.add(dnThread);

		// Creating second test DN thread

		dnParent = new DiscrepancyNoteBean();
		dnParent.setId(25);
		dnParent.setStudySub(studySubject);
		dnParent.setStudy(study);
		dnParent.setCreatedDateString("10-Jun-2014");
		dnParent.setUpdatedDateString("14-Jun-2014");
		dnParent.setAge(25);
		dnParent.setDays(21);
		dnParent.setDisType(DiscrepancyNoteType.QUERY);
		dnParent.setResolutionStatusId(ResolutionStatus.CLOSED.getId());
		dnParent.setEventName("Screening");
		dnParent.setCrfName("Enrollment");
		dnParent.setCrfStatus(Status.DATA_ENTRY_STARTED.getName());
		dnParent.setEntityName("IE001_RDB_CND1");
		dnParent.setEntityValue("0");
		dnParent.setDescription("Subject does not meet Inclusion Criteria. Please provide an explanation.");
		dnParent.setAssignedUser(assignedUser);
		dnParent.setStudyId(study.getId());

		dnChild = new DiscrepancyNoteBean();
		dnChild.setId(26);
		dnChild.setStudySub(studySubject);
		dnChild.setStudy(study);
		dnChild.setParentDnId(dnParent.getId());
		dnChild.setCreatedDateString("10-Jun-2014");
		dnChild.setUpdatedDateString("10-Jun-2014");
		dnChild.setDisType(DiscrepancyNoteType.QUERY);
		dnChild.setResolutionStatusId(ResolutionStatus.OPEN.getId());
		dnChild.setEventName("Screening");
		dnChild.setCrfName("Enrollment");
		dnChild.setCrfStatus(Status.DATA_ENTRY_STARTED.getName());
		dnChild.setEntityName("IE001_RDB_CND1");
		dnChild.setEntityValue("0");
		dnChild.setDescription("Subject does not meet Inclusion Criteria. Please provide an explanation.");
		dnChild.setAssignedUser(assignedUser);
		dnChild.setStudyId(study.getId());

		thread = new LinkedList<DiscrepancyNoteBean>();
		thread.add(dnParent);
		thread.add(dnChild);

		dnChild = new DiscrepancyNoteBean();
		dnChild.setId(27);
		dnChild.setStudySub(studySubject);
		dnChild.setStudy(study);
		dnChild.setParentDnId(dnParent.getId());
		dnChild.setCreatedDateString("14-Jun-2014");
		dnChild.setUpdatedDateString("14-Jun-2014");
		dnChild.setDisType(DiscrepancyNoteType.QUERY);
		dnChild.setResolutionStatusId(ResolutionStatus.CLOSED.getId());
		dnChild.setEventName("Screening");
		dnChild.setCrfName("Enrollment");
		dnChild.setCrfStatus(Status.DATA_ENTRY_STARTED.getName());
		dnChild.setEntityName("IE001_RDB_CND1");
		dnChild.setEntityValue("0");
		dnChild.setDescription("Query monitored and closed");
		dnChild.setAssignedUser(assignedUser);
		dnChild.setStudyId(study.getId());

		thread.add(dnChild);

		dnThread = new DiscrepancyNoteThread();
		dnThread.setLinkedNoteList((LinkedList<DiscrepancyNoteBean>) thread);
		dnThreadsList.add(dnThread);

		return dnThreadsList;
	}

	private String getTestExportFileBodyContent() {

		StringBuilder fileContent = new StringBuilder("");
		fileContent
				.append("Study Subject ID,Subject Status,Study/Site OID,Thread ID,Note ID,Parent Note ID,Date Created,")
				.append("Date Update,Days Open,Days Since Updated,Discrepancy Type,Resolution Status,Event Name,CRF Name,")
				.append("CRF Status,Entity name,Entity value,Description,Detailed Notes,Assigned User,Study Id\n");

		int threadCount = 1;

		for (DiscrepancyNoteThread discrepancyNoteThread : testDNThreadsList) {
			for (DiscrepancyNoteBean discNoteBean : discrepancyNoteThread.getLinkedNoteList()) {

				fileContent.append(discNoteBean.getStudySub().getLabel()).append(",")
						.append(discNoteBean.getStudySub().getStatus().getName()).append(",")
						.append(discNoteBean.getStudy().getOid()).append(",").append(threadCount).append(",")
						.append(discNoteBean.getId()).append(",")
						.append(discNoteBean.getParentDnId() > 0 ? discNoteBean.getParentDnId() : "").append(",")
						.append(discNoteBean.getCreatedDateString()).append(",")
						.append(discNoteBean.getUpdatedDateString()).append(",");

				if (discNoteBean.getParentDnId() == 0) {

					fileContent.append(discNoteBean.getAge()).append(",");
					fileContent.append(discNoteBean.getDays() == 0 ? "" : discNoteBean.getDays()).append(",");
				} else {
					fileContent.append(",,");
				}

				fileContent.append(discNoteBean.getDisType().getName()).append(",")
						.append(ResolutionStatus.get(discNoteBean.getResolutionStatusId()).getName()).append(",")
						.append(discNoteBean.getEventName()).append(",").append(discNoteBean.getCrfName()).append(",")
						.append(discNoteBean.getCrfStatus()).append(",").append(discNoteBean.getEntityName())
						.append(",").append(discNoteBean.getEntityValue()).append(",")
						.append(discNoteBean.getDescription()).append(",").append(discNoteBean.getDetailedNotes())
						.append(",").append(discNoteBean.getAssignedUser().getName()).append(",")
						.append(discNoteBean.getStudyId()).append("\n");
			}

			threadCount++;
		}

		return fileContent.toString();
	}

	@Test
	public void testThatGetThreadListContentLengthReturnsCorrectNumberOfBytes() throws IOException {

		Assert.assertEquals(1475, downloadDiscrepancyNote.getThreadListContentLength(testDNThreadsList));
	}
}
