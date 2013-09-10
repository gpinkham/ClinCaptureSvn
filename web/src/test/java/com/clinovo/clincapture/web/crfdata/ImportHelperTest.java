package com.clinovo.clincapture.web.crfdata;

import com.clinovo.util.ValidatorHelper;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.dao.hibernate.ConfigurationDao;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.crfdata.ImportHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

public class ImportHelperTest {

	protected ImportHelper importHelper;
	protected MockHttpServletRequest request;
    protected ConfigurationDao configurationDao;
	protected FormDiscrepancyNotes formDiscrepancyNotes;
	protected DiscrepancyValidator discrepancyValidator;    

	protected DisplayItemBean displayItemBean;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		request.setPreferredLocales(Arrays.asList(new Locale[] { new Locale("en") }));
		ResourceBundleProvider.updateLocale(request.getLocale());

        configurationDao = Mockito.mock(ConfigurationDao.class);
                
		importHelper = new ImportHelper();
		formDiscrepancyNotes = new FormDiscrepancyNotes();
		discrepancyValidator = new DiscrepancyValidator(new ValidatorHelper(request, configurationDao),
				formDiscrepancyNotes);

		displayItemBean = new DisplayItemBean();

		ItemBean ib = new ItemBean();
		ib.setOid("xxx_oid");
		ib.setId(1);
		ib.setDataType(ItemDataType.INTEGER);

		displayItemBean.setItem(ib);

		ItemDataBean idb = new ItemDataBean();

		displayItemBean.setData(idb);

		ItemFormMetadataBean ifmb = new ItemFormMetadataBean();

		displayItemBean.setMetadata(ifmb);
	}

	@Test
	public void testWrongInteger() {
		displayItemBean.getData().setValue("string");
		request.setParameter(displayItemBean.getItem().getOid(), displayItemBean.getData().getValue());
		importHelper.validateDisplayItemBeanText(discrepancyValidator, displayItemBean, displayItemBean.getItem()
				.getOid());
		HashMap<?, ?> validationErrors = discrepancyValidator.validate("noregex");
		assertEquals(validationErrors.size(), 1);
	}

	@Test
	public void testCorrectInteger() {
		displayItemBean.getData().setValue("1");
		request.setParameter(displayItemBean.getItem().getOid(), displayItemBean.getData().getValue());
		importHelper.validateDisplayItemBeanText(discrepancyValidator, displayItemBean, displayItemBean.getItem()
				.getOid());
		HashMap<?, ?> validationErrors = discrepancyValidator.validate("noregex");
		assertEquals(validationErrors.size(), 0);
	}
}
