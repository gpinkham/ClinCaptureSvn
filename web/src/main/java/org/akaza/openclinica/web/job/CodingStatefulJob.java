package org.akaza.openclinica.web.job;

import org.quartz.StatefulJob;

@SuppressWarnings("deprecation")
public class CodingStatefulJob extends CodingSpringJob implements StatefulJob {

    public CodingStatefulJob() {
        super();
    }
}
