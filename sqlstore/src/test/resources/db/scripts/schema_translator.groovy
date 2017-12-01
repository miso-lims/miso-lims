/**
 * This script reads the src migrations and the built beforeMigrate and afterMigrate sql scripts 
 * (see concate_migrate.groovy).
 * It then makes the changes necessary to work with H2, and saves the migrations as test resources to be used by Flyway
 */

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

final String basedir = "${project.basedir}"
final File productionSchemaDir = new File(basedir + '/src/main/resources/db/migration/')
println('Translating schema files from ' + productionSchemaDir.getAbsolutePath() + '...')

final String productionScriptPattern = '^(V\\d{4}_.*|afterMigrate|beforeMigrate)\\.sql$'
final String testSchemaDir = basedir + '/target/test-classes/db/test_migration/'
final String testDataFile = basedir + '/src/test/resources/db/test_migration/test_data.sql'

Files.createDirectories(Paths.get(testSchemaDir))
// productionSchemaDir is in src, and beforeMigrate and afterMigrate are in the built directory
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
    
    // run test data after everything else
    if (file.getName().matches('^afterMigrate.sql$')) {
      Path testDataPath = Paths.get(testDataFile);
      text += new String(Files.readAllBytes(testDataPath));
    }
    
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
