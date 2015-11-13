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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * uk.ac.bbsrc.tgac.miso.notification.handler
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 08-Dec-2010
 * @since 0.0.2
 */
public class JobLaunchRequest {

  private String jobName;

  private Map<String, String> jobParameters;

  public JobLaunchRequest(String jobName) {
    this(jobName, Collections.EMPTY_MAP);
  }

  public JobLaunchRequest(String jobName, Map<String, String> jobParameters) {
    super();
    this.jobName = jobName;
    this.jobParameters = jobParameters;
  }

  public JobLaunchRequest(String jobName, Properties jobParametersAsProps) {
    this(jobName);
    this.jobParameters = new HashMap<String, String>();
    for (Map.Entry<?, ?> entry : jobParametersAsProps.entrySet()) {
      this.jobParameters.put(entry.getKey().toString(), entry.getValue().toString());
    }
  }

  public String getJobName() {
    return jobName;
  }

  public Map<String, String> getJobParameters() {
    return jobParameters == null ? Collections.EMPTY_MAP : Collections.unmodifiableMap(jobParameters);
  }
}
