package org.akaza.openclinica.dao.dataset;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.DatasetItemStatus;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class DatasetDaoTest extends DefaultAppContextTest {

    private DatasetBean createDateset() {
        DatasetBean dataset = new DatasetBean();
        dataset.setStudyId(1);
        dataset.setOwner((UserAccountBean)userAccountDAO.findByPK(1));
        dataset.setStatus(Status.AVAILABLE);
        dataset.setName("xx");
        dataset.setDescription("xx");
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
    public void testCreate() throws OpenClinicaException {
        DatasetBean dataset = createDateset();
        dataset = (DatasetBean)datasetDAO.create(dataset);
        assertTrue(dataset.getId() > 0);
    }

    @Test
    public void testUpdate() throws OpenClinicaException {
        DatasetBean dataset;
        List<DatasetBean> datasets = (List<DatasetBean>)datasetDAO.findAll();
        if (datasets.size() == 0) {
            dataset = createDateset();
            dataset = (DatasetBean)datasetDAO.create(dataset);
        } else {
            dataset = datasets.get(0);
        }
        assertTrue(dataset.getId() > 0);
        String newName = dataset.getName() + "_" + new Date();
        dataset.setName(newName);
        dataset = (DatasetBean)datasetDAO.update(dataset);
        assertEquals(dataset.getName(), newName);
    }

    @Test
    public void testUpdateAll() throws OpenClinicaException {
        DatasetBean dataset;
        List<DatasetBean> datasets = (List<DatasetBean>)datasetDAO.findAll();
        if (datasets.size() == 0) {
            dataset = createDateset();
            dataset = (DatasetBean)datasetDAO.create(dataset);
        } else {
            dataset = datasets.get(0);
        }
        assertTrue(dataset.getId() > 0);
        int numRuns = 99;
        String newName = dataset.getName() + "_" + new Date();
        dataset.setName(newName);
        dataset.setDateLastRun(new Date());
        dataset.setNumRuns(numRuns);
        dataset = (DatasetBean)datasetDAO.updateAll(dataset);
        assertEquals(dataset.getName(), newName);
        assertEquals(dataset.getNumRuns(), numRuns);
    }

    @Test
    public void testInitialDatasetData() throws OpenClinicaException {
        DatasetBean dataset;
        List<DatasetBean> datasets = (List<DatasetBean>)datasetDAO.findAll();
        if (datasets.size() == 0) {
            dataset = createDateset();
            dataset = (DatasetBean)datasetDAO.create(dataset);
        } else {
            dataset = datasets.get(0);
        }
        assertTrue(dataset.getId() > 0);
        dataset = (DatasetBean)datasetDAO.initialDatasetData(dataset.getId());
        assertTrue(dataset.getItemMap().size() > 0);
    }

}
