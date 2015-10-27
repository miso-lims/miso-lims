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

import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;

/**
 * Created by IntelliJ IDEA. User: collesa Date: 26/04/12 Time: 16:31 To change this template use File | Settings | File Templates.
 */
public class UploadListener implements CopyStreamListener {
  private int calls;
  private long streamSZ;
  private long totalBT;

  @Override
  public void bytesTransferred(CopyStreamEvent event) {
    bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());

  }

  @Override
  public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
    streamSZ = streamSize;
    totalBT = totalBytesTransferred;
    calls++;
  }

  public long getStreamSize() {
    return (streamSZ);
  }

  public long getTotalBT() {
    return (totalBT);
  }
}
