/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.notification.core.batch;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

/**
 * uk.ac.bbsrc.tgac.miso.notification.handler
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 08-Dec-2010
 * @since 0.0.2
 */
public class JobLaunchingHandler {

  private JobLocator jobLocator;
  private JobLauncher jobLauncher;

  public JobLaunchingHandler(JobLocator jobLocator, JobLauncher jobLauncher) {
    super();
    this.jobLocator = jobLocator;
    this.jobLauncher = jobLauncher;
  }

  public JobExecution launch(JobLaunchRequest request) throws JobExecutionAlreadyRunningException, JobRestartException,
      JobInstanceAlreadyCompleteException, JobParametersInvalidException, NoSuchJobException {
    Job job = new SimpleJob(request.getJobName());
    JobParametersBuilder builder = new JobParametersBuilder();
    for (Map.Entry<String, String> entry : request.getJobParameters().entrySet()) {
      builder.addString(entry.getKey(), entry.getValue());
    }
    return jobLauncher.run(job, builder.toJobParameters());
  }
}
