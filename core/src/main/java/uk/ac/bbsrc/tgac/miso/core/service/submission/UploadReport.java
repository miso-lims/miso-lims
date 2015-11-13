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

import java.util.List;
import java.util.Map;

/**
 * An interface that defines datafile upload progress report
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface UploadReport {
  /**
   *
   */
  public void setStatus(String status);

  public void setMessage(String message);

  public String getStatus();

  public String getMessage();

  public Map<String, Object> getUploadReport();

  public List<UploadJob> getUploadJobs();

  public UploadJob getUploadJobByFile();

  public UploadJob getUploadJobByIndex();

}