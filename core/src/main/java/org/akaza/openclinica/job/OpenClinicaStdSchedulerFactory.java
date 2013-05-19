/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
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

package org.akaza.openclinica.job;

import java.util.Properties;

import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.ThreadPool;

/**
 * Custom {@link SchedulerFactory} adapted to configure a {@link ThreadPool} with zero threads.
 * 
 * @author Doug Rodrigues (douglas.rodrigues@openclinica.com)
 * 
 */
public class OpenClinicaStdSchedulerFactory extends StdSchedulerFactory {

	@Override
	public void initialize(Properties props) throws SchedulerException {
		String threadCount = props.getProperty("org.quartz.threadPool.threadCount");
		if (threadCount.trim().equals("0")) {
			// Replaces the thread pool class used
			props.put("org.quartz.threadPool.class", "org.akaza.openclinica.job.EmptyThreadPool");
			// Removes "org.quartz.threadPool.*" properties not applicable for this class
			props.remove("org.quartz.threadPool.threadCount");
			props.remove("org.quartz.threadPool.threadPriority");
		}
		super.initialize(props);
	}

}
