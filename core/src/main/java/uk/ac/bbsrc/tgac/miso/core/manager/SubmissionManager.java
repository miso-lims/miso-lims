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

package uk.ac.bbsrc.tgac.miso.core.manager;

import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.service.submission.TransferMethod;
import uk.ac.bbsrc.tgac.miso.core.service.submission.UploadReport;

/**
 * An interface that defines a SubmissionManager object that can submit objects to a service defined by an endpoint
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface SubmissionManager<I, O, R> {
  /**
   * Sets the submissionStoragePath of this SubmissionManager object.
   * 
   * @param path
   *          submissionStoragePath.
   */
  public void setSubmissionStoragePath(String path);

  /**
   * Returns the submissionStoragePath of this SubmissionManager object.
   * 
   * @return String submissionStoragePath.
   */
  public String getSubmissionStoragePath();

  /**
   * Sets the submissionEndPoint of this SubmissionManager object.
   * 
   * @param o
   *          submissionEndPoint.
   */
  public void setSubmissionEndPoint(O o);

  /**
   * Returns the submissionEndPoint of this SubmissionManager object.
   * 
   * @return O submissionEndPoint.
   */
  public O getSubmissionEndPoint();

  /**
   * Submit the given submittable output type object to the submission endpoint and return any response
   * 
   * @param i
   *          of type I
   * @return R response from submission service to which the submission has been sent
   * @throws SubmissionException
   *           when
   */
  public R submit(I i) throws SubmissionException;

  public Object parseResponse(R response);

  public String generateSubmissionMetadata(Submission submission) throws SubmissionException;

  public String prettifySubmissionMetadata(Submission submission) throws SubmissionException;

  public String submitSequenceData(Submission submission);

  public void setTransferMethod(TransferMethod transferMethod);

  public UploadReport getUploadProgress(Long submissionId);
}
