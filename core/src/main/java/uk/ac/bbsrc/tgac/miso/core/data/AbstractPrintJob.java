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

package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;
import java.util.Queue;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;

/**
 * Skeleton implementation of a PrintJob
 * 
 * @author Rob Davey
 * @date 01-Jul-2011
 * @since 0.0.3
 */
public class AbstractPrintJob implements PrintJob {
  public static final Long UNSAVED_ID = 0L;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long jobId = AbstractPrintJob.UNSAVED_ID;
  private Date printDate;
  private User printUser;
  private Queue<?> queuedElements;
  private String status;
  private MisoPrintService printService;

  @Override
  public void setJobId(Long jobId) {
    this.jobId = jobId;
  }

  @Override
  public Long getJobId() {
    return jobId;
  }

  @Override
  public void setPrintDate(Date printDate) {
    this.printDate = printDate;
  }

  @Override
  public Date getPrintDate() {
    return printDate;
  }

  @Override
  public void setPrintUser(User printUser) {
    this.printUser = printUser;
  }

  @Override
  public User getPrintUser() {
    return printUser;
  }

  @Override
  public void setPrintService(MisoPrintService printService) {
    this.printService = printService;
  }

  @Override
  public MisoPrintService getPrintService() {
    return printService;
  }

  @Override
  public void setQueuedElements(Queue<?> queuedElements) {
    this.queuedElements = queuedElements;
  }

  @Override
  public Queue<?> getQueuedElements() {
    return queuedElements;
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public int compareTo(Object o) {
    PrintJob t = (PrintJob) o;
    if (getJobId() < t.getJobId()) return -1;
    if (getJobId() > t.getJobId()) return 1;
    return 0;
  }
}
