/**
 * This script reads the output of the concat_migrate.groovy script that has been placed in the sqlstore build directory,
 * which gets copied by Maven into the IT test build directory. It then makes the changes necessary to work with H2, 
 * and saves the migrations as test resources to be used by Flyway.
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

final String productionScriptPattern = '^(V\\d{4}_.*|afterMigrate|beforeMigrate)\\.sql$'
final String testSchemaDir = basedir + '/target/test-classes/db/migration/'
final String testStoredProceduresFile = basedir + '/src/it/resources/db/migration/afterAfterMigrate.sql'

Files.createDirectories(Paths.get(testSchemaDir))
for (File file : productionSchemaDir.listFiles()) {
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
    
    if (file.getName().matches('^afterMigrate.sql$')) {
     // append the 'test stored procedures' file so that the test runs during migration
     Path aamPath = Paths.get(testStoredProceduresFile)
     
     text += new String(Files.readAllBytes(aamPath))
    }
     
    Files.write(dstPath, text.getBytes(), StandardOpenOption.CREATE)
    println("Wrote translated schema file: " + dstPath.toAbsolutePath().toString())
  }
}
