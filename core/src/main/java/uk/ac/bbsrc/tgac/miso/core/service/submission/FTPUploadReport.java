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

package uk.ac.bbsrc.tgac.miso.core.service.submission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA. User: collesa Date: 26/04/12 Time: 17:18 To change this template use File | Settings | File Templates.
 */
public class FTPUploadReport implements UploadReport {
  private List<UploadJob> uploadJobs = new ArrayList<UploadJob>() {
  };

  private String status;
  private String message;
  protected static final Logger log = LoggerFactory.getLogger(FTPUploadReport.class);

  public FTPUploadReport(List<FTPUploadJob> FTPUploadJobs) {
    log.debug("FTPUploadReport has been created for submission:");

    for (FTPUploadJob up : FTPUploadJobs) {
      log.debug("UploadJob: " + up.getFile() + " " + up.getPercentageTransferred() + "% complete");

      uploadJobs.add(up);
    }
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String getStatus() {
    return status; // To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public String getMessage() {
    return message; // To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Map<String, Object> getUploadReport() {
    Map report = new HashMap<String, Object>();
    for (UploadJob up : uploadJobs) {
      report.put(up.getFile().getName(), up.getPercentageTransferred());
      log.debug(up.getFile().getName());
    }

    return report;
  }

  @Override
  public List<UploadJob> getUploadJobs() {
    return uploadJobs;
  }

  @Override
  public UploadJob getUploadJobByFile() {
    return uploadJobs.iterator().next(); // To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public UploadJob getUploadJobByIndex() {
    return null; // To change body of implemented methods use File | Settings | File Templates.
  }

}
