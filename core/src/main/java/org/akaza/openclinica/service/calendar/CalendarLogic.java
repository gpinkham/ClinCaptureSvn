package org.akaza.openclinica.service.calendar;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.joda.time.DateTime;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class CalendarLogic {

	DataSource ds;
	
	public CalendarLogic(DataSource ds) {
		this.ds = ds;

	}

	public String maxMinDaysValidator(StudyEventBean studyEventBean) {
		
		String messageReturn = "empty";
		
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(ds);
		StudyEventDAO sed = new StudyEventDAO(ds);
		StudySubjectDAO ssdao = new StudySubjectDAO(ds);
		int subjectId = studyEventBean.getStudySubjectId();
		StudyDAO sdao = new StudyDAO(ds);
		StudyBean studyBean = sdao.findByStudySubjectId(subjectId);
		StudySubjectBean ssb = (StudySubjectBean) ssdao.findByPKAndStudy(subjectId, studyBean);
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao.findByPK(studyEventBean.getStudyEventDefinitionId());
		//
		List<StudyEventBean> seb;
		StudyEventBean studyEventBeanRef = new StudyEventBean();
		if (sedb.getReferenceVisit()) {
			List<StudyEventDefinitionBean> studyEventDefinitions = seddao.findReferenceVisitBeans();
			for (StudyEventDefinitionBean studyEventDefinitionBean : studyEventDefinitions) {
				seb = sed.findAllByDefinitionAndSubject(studyEventDefinitionBean, ssb);
				for (StudyEventBean studyEventBeanReferenceVisit : seb) {
					if (studyEventBeanReferenceVisit.getSubjectEventStatus().equals(SubjectEventStatus.COMPLETED)) {
						if (studyEventBeanRef.getDateStarted() == null) {
							studyEventBeanRef = new StudyEventBean(studyEventBeanReferenceVisit);
						} else {
							if (studyEventBeanRef.getDateStarted().before(studyEventBeanReferenceVisit.getDateStarted())) {
								studyEventBeanRef = new StudyEventBean(studyEventBeanReferenceVisit);
							}
						}
					}
				}
			}
			System.out.println("Latest reference visit date for this subject " + studyEventBeanRef.getDateStarted());
			//schedule all other events using this date and their sch_day fields
			List<StudyEventDefinitionBean> sedForSch = seddao.findAllByStudy(studyBean);
			for (StudyEventDefinitionBean sedTmp : sedForSch) {
				if (!sedTmp.getReferenceVisit()) {
					ArrayList<StudyEventBean> eventsForValidation = sed.findAllByStudySubjectAndDefinition(ssb, sedTmp);
					System.out.println("size eventsForValidation "+ eventsForValidation.size());
					if (eventsForValidation.size() == 0) {
						System.out.println("This events is not created - i will create it now and set status scheduled");
						StudyEventBean studyEvent = new StudyEventBean();
						studyEvent.setStudyEventDefinitionId(sedTmp.getId());
						studyEvent.setStudySubjectId(subjectId);
						studyEvent.setStartTimeFlag(false);
						int schDay = sedTmp.getScheduleDay();
						System.out.println("Is not RV events");
						DateTime dateTimeCompleted = new DateTime(studyEventBeanRef.getDateStarted().getTime());
						System.out.println("This date will be set without sch days "+ dateTimeCompleted);
						dateTimeCompleted = dateTimeCompleted.plusDays(schDay);
						System.out.println("Start date will be set (plus sch days) "+ dateTimeCompleted);
						studyEvent.setDateStarted(dateTimeCompleted.toDate());
						studyEvent.setOwner(getUserByEmail(sedb.getEmailAdress()));
						studyEvent.setStatus(Status.AVAILABLE);
						studyEvent.setLocation("");
						studyEvent.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);
						studyEvent.setSampleOrdinal(sed.getMaxSampleOrdinal(sedTmp, ssb) + 1);
						studyEvent = (StudyEventBean) sed.create(studyEvent);
					}
				}
			}
		}
		return messageReturn;
	}
	
	private UserAccountBean getUserByEmail(String userEmail) {
		UserAccountDAO uadao = new UserAccountDAO(ds);
		UserAccountBean userBean = (UserAccountBean) uadao.findByUserEmail(userEmail);
		return userBean;
	}
	
	
}

