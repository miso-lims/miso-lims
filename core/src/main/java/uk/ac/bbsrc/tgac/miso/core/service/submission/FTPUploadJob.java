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

import java.io.File;

/**
 * Created by IntelliJ IDEA. User: collesa Date: 26/04/12 Time: 18:11 To change this template use File | Settings | File Templates.
 */
public class FTPUploadJob implements UploadJob {

  private File file;
  private UploadListener uploadListener = new UploadListener();

  public FTPUploadJob(File file) {
    this.file = file;

  }

  @Override
  public void setFile(File file) {
    this.file = file;
  }

  @Override
  public File getFile() {
    return file; // To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public long getBytesTransferred() {
    return uploadListener.getTotalBT(); // To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public int getPercentageTransferred() {
    if (file != null) {
      return 100 * (int) (uploadListener.getTotalBT() / uploadListener.getStreamSize());
    } else
      return 0; // To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean isComplete() {
    if (getPercentageTransferred() == 100)
      return true;
    else
      return false; // To change body of implemented methods use File | Settings | File Templates.
  }

  public UploadListener getListener() {
    return this.uploadListener;
  }
}
