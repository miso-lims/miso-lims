package uk.ac.bbsrc.tgac.miso.db.migration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class V8520__Attachments implements JdbcMigration {

  private static final Logger log = Logger.getLogger(V8520__Attachments.class.getName());

  private enum EntityType {
    PROJECT("project", "Project", "projectId", "Project_Attachment"),
    SERVICE_RECORD("servicerecord", "ServiceRecord", "recordId", "ServiceRecord_Attachment");
    
    private final String dirName;
    private final String table;
    private final String primaryKey;
    private final String joinTable;
    
    private EntityType(String dirName, String table, String primaryKey, String joinTable) {
      this.dirName = dirName;
      this.table = table;
      this.primaryKey = primaryKey;
      this.joinTable = joinTable;
    }

    public String getDirName() {
      return dirName;
    }

    public String getTable() {
      return table;
    }

    public String getPrimaryKey() {
      return primaryKey;
    }

    public String getJoinTable() {
      return joinTable;
    }
  }

  private Connection connection;
  private String filesDir;
  private Path basePath;

  @Override
  public void migrate(Connection connection) throws Exception {
    log.info("Scanning existing attachments to save in database...");

    this.connection = connection;
    filesDir = getFilesDir();
    if (LimsUtils.isStringEmptyOrNull(filesDir)) {
      throw new IllegalStateException("MISO_FILES_DIR not set");
    }
    if (!filesDir.endsWith(File.separator)) {
      filesDir += File.separator;
    }
    basePath = Paths.get(filesDir);

    log.info(String.format("scanning directory %s", filesDir));
    try (PreparedStatement insertStatement = connection.prepareStatement(
        "INSERT INTO Attachment(filename, path, creator, created)"
            + " VALUES (?, ?, (SELECT userId FROM User WHERE loginName = 'admin'), NOW());",
        Statement.RETURN_GENERATED_KEYS)) {
      for (EntityType type : EntityType.values()) {
        scanTypeDir(type, insertStatement);
      }
    }

    dropTempTable();
  }

  private void scanTypeDir(EntityType type, PreparedStatement insertStatement) throws SQLException {
    File dir = new File(filesDir + type.getDirName());
    if (dir.exists()) {
      log.info(String.format("scanning directory %s", dir.getAbsolutePath()));
      try (PreparedStatement getStatement = connection
          .prepareStatement(String.format("SELECT * FROM %s WHERE %s = ?", type.getTable(), type.getPrimaryKey()));
          PreparedStatement joinStatement = connection.prepareStatement(
              String.format("INSERT INTO %s(%s, attachmentId) VALUES (?, ?);", type.getJoinTable(), type.getPrimaryKey()))) {
        for (File typeFile : dir.listFiles()) {
          processObjectDir(type, typeFile, getStatement, insertStatement, joinStatement);
        }
      }
    }
  }

  private void processObjectDir(EntityType type, File dir, PreparedStatement getStatement, PreparedStatement insertStatement,
      PreparedStatement joinStatement) throws SQLException {
    log.info("scanning directory " + dir.getAbsolutePath());
    if (!dir.isDirectory()) {
      log.warning(String.format("Unexpected non-directory found: %s", dir.getAbsolutePath()));
    } else {
      long entityId = 0L;
      try {
        entityId = Long.parseLong(dir.getName());
      } catch (NumberFormatException e) {
        // ignore error - failure handled below
      }
      if (entityId == 0L) {
        log.warning(String.format("Unexpected directory name: %s", dir.getAbsolutePath()));
      } else {
        getStatement.setLong(1, entityId);
        if (!getStatement.executeQuery().next()) {
          log.warning(String.format("Found files for non-existant %s %d", type.getTable(), entityId));
        } else {
          joinStatement.setLong(1, entityId);
          for (File objectFile : dir.listFiles()) {
            processFile(objectFile, insertStatement, joinStatement);
          }
        }
      }
    }
  }

  private void processFile(File objectFile, PreparedStatement insertStatement, PreparedStatement joinStatement) throws SQLException {
    if (!objectFile.isFile()) {
      log.warning(String.format("Unexpected non-file found: %s", objectFile.getAbsolutePath()));
    } else {
      log.info("processing file " + objectFile.getAbsolutePath());
      Path filePath = Paths.get(objectFile.getAbsolutePath());
      String relativePath = File.separator + basePath.relativize(filePath).toString();

      insertStatement.setString(1, objectFile.getName());
      insertStatement.setString(2, relativePath);
      if (insertStatement.executeUpdate() == 0) {
        throw new RuntimeException("Failed to insert attachment data");
      }

      joinStatement.setLong(2, getGeneratedId(insertStatement));
      if (joinStatement.executeUpdate() == 0) {
        throw new RuntimeException("Failed to insert attachment relationship data");
      }
    }
  }

  private String getFilesDir() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      ResultSet results = stmt.executeQuery("SELECT val FROM TempValues WHERE name = 'filesDir';");
      if (!results.next()) {
        throw new RuntimeException("Error retrieving filesDir");
      }
      return results.getString(1);
    }
  }

  private long getGeneratedId(PreparedStatement stmt) throws SQLException {
    ResultSet keys = stmt.getGeneratedKeys();
    if (!keys.next()) {
      throw new RuntimeException("Error inserting attachment data");
    }
    return keys.getLong(1);
  }

  private void dropTempTable() throws SQLException {
    try (PreparedStatement stmt = connection.prepareStatement("DROP TABLE TempValues;")) {
      stmt.execute();
    }
  }

}
