package org.akaza.openclinica.job;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobTerminationMonitor implements Serializable {

    private static final long serialVersionUID = 361394087982395855L;

    private static final Logger LOG = LoggerFactory.getLogger(JobTerminationMonitor.class);

    private static ThreadLocal<JobTerminationMonitor> instance = new ThreadLocal<JobTerminationMonitor>() {
        @Override
        protected JobTerminationMonitor initialValue() {
            return new JobTerminationMonitor();
        }
    };

    private String jobName = "<untitled>";

    private JobTerminationMonitor() {
    }

    private JobTerminationMonitor(String jobName) {
        this.jobName = jobName;
    }

    public static JobTerminationMonitor createInstance(String jobName) {
        instance.remove();
        instance.set(new JobTerminationMonitor(jobName));
        return instance.get();
    }

    private boolean running = true;

    /**
     * Verifies if the termination of a job was requested.
     *
     */
    public static void check() {
        JobTerminationMonitor monitor = instance.get();
        if (!monitor.running) {
            LOG.info("Raising termination exception for job " + monitor.jobName);
        }
    }

    /**
     * Request the monitor to throw an interruption exception in the next checkpoint
     */
    public void terminate() {
        running = false;
    }

    public String getJobName() {
        return jobName;
    }

}