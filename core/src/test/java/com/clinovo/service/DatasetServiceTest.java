package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.DatasetItemStatus;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@SuppressWarnings("unchecked")
public class DatasetServiceTest extends DefaultAppContextTest {

	private DatasetBean createDateset() {
		DatasetBean dataset = new DatasetBean();
		dataset.setStudyId(1);
		dataset.setOwner((UserAccountBean) userAccountDAO.findByPK(1));
		dataset.setStatus(Status.AVAILABLE);
		dataset.setName("test_name");
		dataset.setDescription("test_name");
		dataset.setDatasetItemStatus(DatasetItemStatus.COMPLETED);
		dataset.setSQLStatement("select distinct * from extract_data_table where study_event_definition_id in (1) and item_id in (1, 2, 3) and (date(date_created) >= date('2000-01-01')) and (date(date_created) <= date('2100-01-31')) order by date_start asc");
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

	@Test
	public void testInitialDatasetData() {
		DatasetBean dataset;
		List<DatasetBean> datasets = (List<DatasetBean>) datasetDAO.findAll();
		if (datasets.size() == 0) {
			dataset = createDateset();
			dataset = datasetService.create(dataset);
		} else {
			dataset = datasets.get(0);
		}
		assertTrue(dataset.getId() > 0);
		dataset = datasetService.initialDatasetData(dataset.getId());
		assertTrue(dataset.getItemIds().size() > 0);
	}
}
