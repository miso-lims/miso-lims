package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

/**
 * This script reads the production schema sql scripts, makes the changes necessary to work with H2, and saves them
 * as test resources to be used by Flyway
 */
class SchemaTranslator {
  
  public static void main(String[] args) {
    final File productionSchemaDir = new File('src/main/resources/db/migration/')
    final String productionScriptPattern = '^V\\d{4}_.*\\.sql$'
    final String testSchemaDir = 'target/test-classes/db/migration/'
    
    Files.createDirectories(Paths.get(testSchemaDir))
    for (File file : productionSchemaDir.listFiles()) {
      if (file.isFile() && file.getName().matches(productionScriptPattern)) {
        Path srcPath = file.toPath()
        Path dstPath = Paths.get(testSchemaDir + file.getName().replaceFirst('\\.sql$', '.test.sql'))
        String text = new String(Files.readAllBytes(srcPath))
        
        Files.write(dstPath, translateScript(text).getBytes(), StandardOpenOption.CREATE)
      }
    }
  }
  
  public static String translateScript(String text) {
    return text.replaceAll('b\'0\'', '0') // bit representation
        .replaceAll('b\'1\'', '1') // bit representation
        .replaceAll('DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', 'AS CURRENT_TIMESTAMP') // syntax difference, same result
        .replaceAll('\\sCOMMENT=\'.*\'', '') // remove table comments
        .replaceAll('CHARSET=latin1', 'CHARSET=utf8')
        .replaceAll('\\\\\'', '\'\'') // escape single quotes as '' instead of '/
        .replaceAll('(\'ROLE_[^\']*\')', 'RAWTOHEX($1)') // RAWTOHEX function to write BLOB fields
  }
  
}