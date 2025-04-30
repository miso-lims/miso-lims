package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * uk.ac.bbsrc.tgac.miso.core.manager
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class MisoFilesManager implements FilesManager {
  private static final Logger log = LoggerFactory.getLogger(MisoFilesManager.class);

  private String fileStorageDirectory;

  public void setFileStorageDirectory(String fileStorageDirectory) {
    this.fileStorageDirectory = fileStorageDirectory;
  }

  @Override
  public String getFileStorageDirectory() {
    return this.fileStorageDirectory;
  }

  @Override
  public Collection<String> getFileNames(Class<?> type, String qualifier) throws IOException {
    final File path = new File(fileStorageDirectory + "/" + type.getSimpleName().toLowerCase() + "/" + qualifier + "/");
    if (path.exists()) {
      if (path.canRead()) {
        final File[] files = path.listFiles();
        if (files != null) {
          final List<String> names = new ArrayList<>();
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

  protected File getFile(Class<?> type, String qualifier, String fileName, boolean createIfNotExist)
      throws IOException {
    final File path = new File(fileStorageDirectory + "/" + type.getSimpleName().toLowerCase() + "/" + qualifier + "/");
    final File file = new File(path, fileName);
    log.info("Looking up {}", file);
    if (path.exists()) {
      if (file.exists()) {
        if (file.canRead()) {
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

  public File getNewFile(Class<?> type, String qualifier, String fileName) throws IOException {
    return getFile(type, qualifier, fileName, true);
  }

  @Override
  public File getFile(Class<?> type, String qualifier, String fileName) throws IOException {
    return getFile(type, qualifier, fileName, false);
  }
}
