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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.util.Assert;

/**
 * uk.ac.bbsrc.tgac.miso.notification.handler
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 08-Dec-2010
 * @since version
 */
public class NotificationJobLauncher implements JobLauncher, InitializingBean {
  protected static final Logger log = LoggerFactory.getLogger(NotificationJobLauncher.class);

  Map<String, String> resources;

  JobRepository repository;
  TaskExecutor taskExecutor;

  public NotificationJobLauncher() {
    super();
    this.resources = new HashMap<String, String>();
  }

  public void setJobRepository(JobRepository repository) {
    this.repository = repository;
  }

  public void setTaskExecutor(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  @Override
  public JobExecution run(final Job job, final JobParameters jobParameters)
      throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
    Assert.notNull(job, "The Job must not be null.");
    Assert.notNull(jobParameters, "The JobParameters must not be null.");

    final JobExecution jobExecution;
    JobExecution lastExecution = repository.getLastJobExecution(job.getName(), jobParameters);
    if (lastExecution != null) {
      if (!job.isRestartable()) {
        throw new JobRestartException("JobInstance already exists and is not restartable");
      }
    }

    // Check the validity of the parameters before doing creating anything
    // in the repository...
    job.getJobParametersValidator().validate(jobParameters);

    /*
     * There is a very small probability that a non-restartable job can be restarted, but only if another process or thread manages to
     * launch <i>and</i> fail a job execution for this instance between the last assertion and the next method returning successfully.
     */
    jobExecution = repository.createJobExecution(job.getName(), jobParameters);

    try {
      for (String key : resources.keySet()) {
        JobParameter param = new JobParameter(resources.get(key));
        jobParameters.getParameters().put(key, param);
        taskExecutor.execute(new NotificationRunnable(job, jobParameters, jobExecution));
      }
    } catch (TaskRejectedException e) {
      jobExecution.upgradeStatus(BatchStatus.FAILED);
      if (jobExecution.getExitStatus().equals(ExitStatus.UNKNOWN)) {
        jobExecution.setExitStatus(ExitStatus.FAILED.addExitDescription(e));
      }
      repository.update(jobExecution);
    }

    return jobExecution;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.state(repository != null, "A JobRepository has not been set.");
    if (taskExecutor == null) {
      log.info("No TaskExecutor has been set, defaulting to synchronous executor.");
      taskExecutor = new SyncTaskExecutor();
    }
  }

  private class NotificationRunnable implements Runnable {
    Job job;
    JobParameters jobParameters;
    JobExecution jobExecution;

    public NotificationRunnable(Job job, JobParameters jobParameters, JobExecution jobExecution) {
      this.job = job;
      this.jobParameters = jobParameters;
      this.jobExecution = jobExecution;
    }

    @Override
    public void run() {
      try {
        log.info("Job: [" + job + "] launched with the following parameters: [" + jobParameters + "]");
        job.execute(jobExecution);
        log.info("Job: [" + job + "] completed with the following parameters: [" + jobParameters + "] and the following status: ["
            + jobExecution.getStatus() + "]");
      } catch (Throwable t) {
        log.info("Job: [" + job + "] failed unexpectedly and fatally with the following parameters: [" + jobParameters + "]", t);
        rethrow(t);
      }
    }

    private void rethrow(Throwable t) {
      if (t instanceof RuntimeException) {
        throw (RuntimeException) t;
      } else if (t instanceof Error) {
        throw (Error) t;
      }
      throw new IllegalStateException(t);
    }
  }
}
