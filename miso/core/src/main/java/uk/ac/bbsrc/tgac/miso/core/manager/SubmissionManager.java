/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.manager;

import java.util.Map;

import org.apache.http.entity.mime.MultipartEntity;
import org.w3c.dom.Document;

import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.service.submission.UploadReport;

/**
 * Packages data from the data model in a format that can be ingested by an external entity as part of the data release process.
 */
public interface SubmissionManager {
  /**
   * Submit the given submittable output type object to the submission endpoint and return any response
   */
  public Document submit(Submission s) throws SubmissionException;

  public Map<String, Object> parseResponse(Document response);

  public MultipartEntity prepareSubmission(Submission submission) throws SubmissionException;

  public String prettifySubmissionMetadata(Submission submission) throws SubmissionException;

  public String submitSequenceData(Submission submission);

  public UploadReport getUploadProgress(Long submissionId);
}
