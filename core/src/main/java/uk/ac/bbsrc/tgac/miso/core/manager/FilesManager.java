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

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Nameable;

/**
 * uk.ac.bbsrc.tgac.miso.core.manager
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface FilesManager {
  File generateTemporaryFile(String prefix, String suffix, File baseDir) throws IOException;
  String getFileStorageDirectory();
  Collection<File> getFiles(Class  type, String qualifier) throws IOException;
  Collection<String> getFileNames(Class  type, String qualifier) throws IOException;
  File getFile(Class  type, String qualifier, String fileName) throws IOException;
  File storeFile(Class type, String qualifier, File file) throws IOException;
  void deleteFile(Class<? extends Nameable> type, String qualifier, String fileName) throws IOException;
}