package org.akaza.openclinica.dao.extract;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.DatasetItemStatus;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.junit.Before;
import org.junit.Test;

public class OdmExtractDAOTest extends DefaultAppContextTest {

	private String sedIds;
	private String itemIds;
	private String studyIds;
	private String odmVersion;
	private String parentStudyIds;
	private String dateConstraint;
	private String studySubjectIds;
	private int datasetItemStatusId;

	private DatasetBean createDateset() {
		DatasetBean dataset = new DatasetBean();
		dataset.setStudyId(1);
		dataset.setOwner((UserAccountBean) userAccountDAO.findByPK(1));
		dataset.setStatus(Status.AVAILABLE);
		dataset.setName("OdmExtractDAOTest");
		dataset.setDescription("OdmExtractDAOTest");
		dataset.setDatasetItemStatus(DatasetItemStatus.COMPLETED);
		dataset.setSQLStatement("select distinct * from extract_data_table where study_event_definition_id in (1, 2, 3, 4) and item_id in (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34) and (date(date_created) >= date('2000-01-01')) and (date(date_created) <= date('2100-01-31')) order by date_start asc");
		Date currentDate = new Date();
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.YEAR, 2);
		dataset.setDateStart(currentDate);
		dataset.setDateEnd(calendar.getTime());
		dataset.setCreatedDate(currentDate);
		dataset.setUpdatedDate(currentDate);
		dataset.setOdmMetaDataVersionName("1.0.1");
		dataset.setOdmMetaDataVersionOid("1.0.1");
		dataset.setOdmPriorStudyOid("1");
		dataset.setOdmPriorMetaDataVersionOid("1.0.0");
		return dataset;
	}

	@Before
	public void setUp() throws Exception {
		StudyBean study = new StudyBean();
		study.setId(1);

		studySubjectIds = "1";

		odmVersion = "oc1.3";

		studyIds = study.getId() + "";
		parentStudyIds = study.getParentStudyId() > 0 ? study.getParentStudyId() + "" : studyIds;

		DatasetBean dataset = (DatasetBean) datasetDAO.findByNameAndStudy("OdmExtractDAOTest", study);
        if (dataset.getId() == 0) {
            dataset = createDateset();
        }

		datasetItemStatusId = 3;
		String sql = dataset.getSQLStatement().split("order by")[0].trim();
		sql = sql.split("study_event_definition_id in")[1];
		String[] ss = sql.split("and item_id in");
		sedIds = ss[0];
		String[] sss = ss[1].split("and");
		itemIds = sss[0];

		dateConstraint = "";
		if (dbDriverClassName.toLowerCase().contains("postgresql")) {
			dateConstraint = "and " + sss[1] + " and " + sss[2];
			dateConstraint = dateConstraint.replace("date_created", "ss.enrollment_date");
		} else if (dbDriverClassName.toLowerCase().contains("oracle")) {
			String[] os = (sss[1] + sss[2]).split("'");
			dateConstraint = "and trunc(ss.enrollment_date) >= to_date('" + os[1]
					+ "') and trunc(ss.enrollment_date) <= to_date('" + os[3] + "')";
		}
	}

	@Test
	public void testThatGetOCSubjectEventFormSqlSSReturnsCorrectAmountOfRecords() throws Exception {
		odmExtractDAO.setSubjectEventFormDataTypesExpected(odmVersion);
		assertEquals(
				odmExtractDAO.select(
						odmExtractDAO.getOCSubjectEventFormSqlSS(studyIds, sedIds, itemIds, dateConstraint,
								datasetItemStatusId, studySubjectIds, parentStudyIds)).size(), 3);
	}
}
