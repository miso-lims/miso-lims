/**
 * This script reads the production schema sql scripts, makes the changes necessary to work with H2, and saves them
 * as test resources to be used by Flyway
 */

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

final String basedir = "${project.basedir}"
final File productionSchemaDir = new File(basedir + '/target/test-classes/sqlstore/db/migration/')
println('Translating schema files from ' + productionSchemaDir.getAbsolutePath() + '...')

final String productionScriptPattern = '^(V\\d{4}_.*|afterMigrate)\\.sql$'
final String testSchemaDir = basedir + '/target/test-classes/db/migration/'

Files.createDirectories(Paths.get(testSchemaDir))
for (File file : productionSchemaDir.listFiles() + new File(basedir + '/target/classes/db/migration/afterMigrate.sql') + new File(basedir + '/target/classes/db/migration/beforeMigrate.sql')) {
  if (!file.isFile()) {
    continue
  }
  if (file.getName().contains(" ")) {
    println("File name has spaces : " + file.getName())
    System.exit(1)
  }
  if (file.getName().matches(productionScriptPattern)) {
    println('Translating file: ' + file.getAbsolutePath())
    Path srcPath = file.toPath()
    Path dstPath = Paths.get(testSchemaDir + file.getName().replaceFirst('\\.sql$', '.test.sql'))
    String text = new String(Files.readAllBytes(srcPath))
    
    String translated = text
        .replaceAll('(?s)-- ?StartNoTest(.*?)-- ?EndNoTest', '--') // Delete blocks containing non-standard delimiters
        .replaceAll('b\'0\'', '0') // bit representation
        .replaceAll('b\'1\'', '1') // bit representation
        .replaceAll('DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', 'AS CURRENT_TIMESTAMP') // syntax difference, same result
        .replaceAll('\\\\\'', '\'\'') // escape single quotes as '' instead of '/
        .replaceAll('(\'ROLE_[^\']*\')', 'RAWTOHEX($1)') // RAWTOHEX function to write BLOB fields
        .replaceAll('(?s)CREATE TRIGGER.*;', '') // Delete triggers
        
    Files.write(dstPath, translated.getBytes(), StandardOpenOption.CREATE)
    println("Wrote translated schema file: " + dstPath.toAbsolutePath().toString())
  }
}
