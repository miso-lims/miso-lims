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

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;

/**
 * An interface to describe a print job carried out on a {@link Queue} of printable objects, sent to a MISO {@link MisoPrintService}
 * 
 * @author Rob Davey
 * @date 01-Jul-2011
 * @since 0.0.3
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface PrintJob extends Comparable {
  void setJobId(Long jobId);

  Long getJobId();

  void setPrintDate(Date printDate);

  Date getPrintDate();

  void setPrintUser(User printUser);

  User getPrintUser();

  void setPrintService(MisoPrintService printService);

  MisoPrintService getPrintService();

  void setQueuedElements(Queue<?> elements);

  Queue<?> getQueuedElements();

  void setStatus(String status);

  String getStatus();
}
