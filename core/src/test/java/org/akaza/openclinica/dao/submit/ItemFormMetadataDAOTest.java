package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

public class ItemFormMetadataDAOTest extends DefaultAppContextTest {

    @Test
    public void testTotalStudySubjects() {
        assertNotNull(imfdao.findAllByCRFVersionIdAndItemId(2, 5));
    }
}
