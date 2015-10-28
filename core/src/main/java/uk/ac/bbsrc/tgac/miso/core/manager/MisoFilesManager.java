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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.core.manager
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class MisoFilesManager implements FilesManager {
   protected static final Logger log = LoggerFactory.getLogger(MisoFilesManager.class);

   private String fileStorageDirectory;

   public void setFileStorageDirectory(String fileStorageDirectory) {
      this.fileStorageDirectory = fileStorageDirectory;
   }

   @Override
   public String getFileStorageDirectory() {
      return this.fileStorageDirectory;
   }

   /*
    * public File createFile(Class type, String qualifier, String name) throws IOException { File dir = new
    * File(fileStorageDirectory+"/"+type.getSimpleName().toLowerCase()+"/"+qualifier); if (LimsUtils.checkDirectory(dir, true)) { File
    * newFile = new File(dir, name); log.info("Attempting to store " + newFile.getAbsolutePath()); if ((newFile.exists() && newFile.length()
    * != file.length()) || !newFile.exists()) { FileOutputStream fout = null; try { byte[] fileData = new byte[(int)file.length()];
    * FileInputStream fis = null;
    *
    * try { fis = new FileInputStream(file); fis.read(fileData); } catch (IOException e) { fileData = null; } finally { if (fis != null) {
    * try { fis.close(); } catch (IOException e) { // ignore } } } fout = new FileOutputStream(newFile); fout.write(fileData); } finally {
    * if (fout != null) { fout.close(); } } return newFile; } else { log.info("File already exists - not overwriting."); return newFile; } }
    * return null; }
    */
   @Override
   public File storeFile(Class type, String qualifier, File file) throws IOException {
      final File dir = new File(fileStorageDirectory + "/" + type.getSimpleName().toLowerCase() + "/" + qualifier);
      if (LimsUtils.checkDirectory(dir, true)) {
         final File newFile = new File(dir, file.getName().replace(" ", "_"));
         log.info("Attempting to store " + newFile.getAbsolutePath());
         if ((newFile.exists() && newFile.length() != file.length()) || !newFile.exists()) {
            FileOutputStream fout = null;
            try {
               byte[] fileData = new byte[(int) file.length()];
               FileInputStream fis = null;

               try {
                  fis = new FileInputStream(file);
                  fis.read(fileData);
               } catch (final IOException e) {
                  fileData = null;
               } finally {
                  if (fis != null) {
                     try {
                        fis.close();
                     } catch (final IOException e) {
                        // ignore
                     }
                  }
               }
               fout = new FileOutputStream(newFile);
               fout.write(fileData);
            } finally {
               if (fout != null) {
                  fout.close();
               }
            }
            return newFile;
         } else {
            log.info("File already exists - not overwriting.");
            return newFile;
         }
      }
      return null;
   }

   public File storeFile(Object type, String qualifier, File file) throws IOException {
      return storeFile(type.getClass(), qualifier, file);
   }

   @Override
   public File generateTemporaryFile(String prefix, String suffix, File baseDir) throws IOException {
      if (baseDir.exists()) {
         return File.createTempFile(prefix, suffix, baseDir);
      } else {
         log.warn("MISO temporary files directory doesn't seem to exist. Trying to create it...");
         if (baseDir.mkdirs()) {
            log.warn("MISO temporary files directory created.. retrying file generation...");
            return File.createTempFile(prefix, suffix, baseDir);
         } else {
            throw new IOException("Could not create MISO temporary file directory (" + baseDir
                  + "). Please create this directory or allow the parent to be writable to MISO.");
         }
      }
   }

   public File generateTemporaryFile(String prefix, String suffix) throws IOException {
      final File tempPath = new File(fileStorageDirectory, "/temp/");
      if (tempPath.exists()) {
         return File.createTempFile(prefix, suffix, tempPath);
      } else {
         log.warn("MISO temporary files directory doesn't seem to exist. Trying to create it...");
         if (tempPath.mkdirs()) {
            log.warn("MISO temporary files directory created.. retrying file generation...");
            return File.createTempFile(prefix, suffix, tempPath);
         } else {
            throw new IOException("Could not create MISO temporary file directory (" + tempPath
                  + "). Please create this directory or allow the parent to be writable to MISO.");
         }
      }
   }

   @Override
   public Collection<File> getFiles(Class type, String qualifier) throws IOException {
      // log.debug("getFiles called for qualifier:..." + qualifier);
      final File path = new File(fileStorageDirectory + "/" + type.getSimpleName().toLowerCase() + "/" + qualifier + "/");
      // log.debug("filepath:..." + path.getAbsolutePath());

      if (path.exists()) {
         if (path.canRead()) {
            final File[] files = path.listFiles();
            if (files != null) return Arrays.asList(files);
         }
      }
      return Collections.emptyList();
   }

   public Collection<File> getFiles(Object type, String qualifier) throws IOException {
      return getFiles(type.getClass(), qualifier);
   }

   @Override
   public Collection<String> getFileNames(Class type, String qualifier) throws IOException {
      final File path = new File(fileStorageDirectory + "/" + type.getSimpleName().toLowerCase() + "/" + qualifier + "/");
      if (path.exists()) {
         if (path.canRead()) {
            // SecurityProfile profile = type.getSecurityProfile();
            // if (profile.userCanRead(user)) {
            final File[] files = path.listFiles();
            if (files != null) {
               final List<String> names = new ArrayList<String>();
               for (final File f : Arrays.asList(files)) {
                  names.add(f.getName());
               }
               Collections.sort(names);
               return names;
            }
         } else {
            throw new IOException("It seems that the MISO file directory is not readable. Check the Spring configuration.");
         }
      } else {
         log.warn("MISO files directory doesn't seem to exist. Trying to create it...");
         if (path.mkdirs()) {
            log.warn("MISO files directory created.. retrying file listing...");
            return getFileNames(type, qualifier);
         } else {
            throw new IOException("Could not create LIMS file directory (" + path
                  + "). Please create this directory or allow the parent to be writable to MISO.");
         }
      }
      return Collections.emptyList();
   }

   public Collection<String> getFileNames(Object type, String qualifier) throws IOException {
      return getFileNames(type.getClass(), qualifier);
   }

   protected File getFile(Class type, String qualifier, String fileName, boolean createIfNotExist) throws IOException {
      // SecurityProfile profile = type.getSecurityProfile();
      final File path = new File(fileStorageDirectory + "/" + type.getSimpleName().toLowerCase() + "/" + qualifier + "/");
      final File file = new File(path, fileName);
      log.info("Looking up " + file);
      if (path.exists()) {
         if (file.exists()) {
            if (file.canRead()) {
               // if (profile.userCanRead(user)) {
               log.info("OK");
               return file;
            } else {
               throw new IOException("Access denied. Please check file permissions.");
            }
         } else {
            if (createIfNotExist && file.createNewFile()) {
               log.info("OK");
               return file;
            }
            throw new IOException("No such file.");
         }
      } else {
         log.warn("MISO files directory doesn't seem to exist. Trying to create it...");
         if (path.mkdirs()) {
            log.warn("MISO files directory created.. retrying file listing...");
            return getFile(type, qualifier, fileName, createIfNotExist);
         } else {
            throw new IOException("Could not create MISO file directory (" + path
                  + "). Please create this directory or allow the parent to be writable to MISO.");
         }
      }
   }

   public File getNewFile(Class type, String qualifier, String fileName) throws IOException {
      return getFile(type, qualifier, fileName, true);
   }

   @Override
   public File getFile(Class type, String qualifier, String fileName) throws IOException {
      return getFile(type, qualifier, fileName, false);
   }

   public File getNewFile(Object type, String qualifier, String fileName) throws IOException {
      return getFile(type.getClass(), qualifier, fileName, true);
   }

   public File getFile(Object type, String qualifier, String fileName) throws IOException {
      return getFile(type.getClass(), qualifier, fileName, false);
   }

   @Override
   public void deleteFile(Class<? extends Nameable> type, String qualifier, String fileName) throws IOException {
      final File remove = getFile(type, qualifier, fileName, false);
      remove.delete();
      log.info(MessageFormat.format("{0} {1} deleted.", qualifier, fileName));
   }
}