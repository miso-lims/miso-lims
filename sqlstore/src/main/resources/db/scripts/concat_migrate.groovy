/**
 * This script reads combines all the before and after migrate scripts to the
 * site and main ones can be kept separate
 */

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

final String basedir = "${project.basedir}"
for (String type : ["afterMigrate", "beforeMigrate"]) {
  StringBuilder builder = new StringBuilder()
  final File sources = new File(basedir + '/src/main/resources/db/migration_' + type)
  println('Concatenating schema files from ' + sources.absolutePath + '...')

  for (File file : sources.listFiles().sort{a,b -> a.name <=> b.name}) {
    if (!file.isFile() || !file.name.endsWith(".sql")) {
      continue
    }
    builder.append(new String(Files.readAllBytes(file.toPath())))
    if (builder.charAt(builder.length() - 1) != '\n') builder.append('\n')
  }
  File destination = new File(basedir + '/target/classes/db/migration/' + type + '.sql')
  destination.getParentFile().mkdirs()
  Files.write(destination.toPath(), builder.toString().bytes, StandardOpenOption.CREATE)
  println("Wrote concatenated schema file: " + destination.absolutePath)
}
