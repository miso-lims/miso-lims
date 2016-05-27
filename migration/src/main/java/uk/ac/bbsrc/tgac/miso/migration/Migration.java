package uk.ac.bbsrc.tgac.miso.migration;

import java.io.FileNotFoundException;
import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.migration.destination.DefaultMigrationTarget;
import uk.ac.bbsrc.tgac.miso.migration.destination.MigrationTarget;
import uk.ac.bbsrc.tgac.miso.migration.source.LoadGeneratorSource;
import uk.ac.bbsrc.tgac.miso.migration.source.MigrationSource;

public class Migration {
  
  private static final String OPT_SOURCE = "source";
  
  private static MigrationProperties properties;
  
  public static void main(String[] args) {
    properties = getProperties(args);
    
    MigrationSource source = getMigrationSource();
    MigrationTarget target = null;
    try {
      target = getMigrationTarget();
    } catch (IOException e) {
      System.out.println("\nERROR: " + e.getLocalizedMessage() + "\n");
      showHelpAndExit(1);
    }
    
    try {
      System.out.println("Collecting migration data from source...");
      MigrationData data = source.getMigrationData();
      System.out.println("Data collected.");
      
      System.out.println("Loading data into target...");
      target.migrate(data);
      System.out.println("Migration complete.");
    } catch (IOException e) {
      System.err.println("Error during migration");
      e.printStackTrace();
    }
  }
  
  private static MigrationProperties getProperties(String[] args) {
    if (args.length != 1) {
      showHelpAndExit(1);
    } else {
      try {
        return new MigrationProperties(args[0]);
      } catch (FileNotFoundException e1) {
        System.err.println("Properties file not found: " + args[0]);
      } catch (IOException e1) {
        System.err.println("Error reading properties file: " + args[0]);
      }
    }
    showHelpAndExit(1);
    return null;
  }
  
  private static MigrationSource getMigrationSource() {
    String sourceType = properties.getRequiredString(OPT_SOURCE);
    switch (sourceType) {
    case LoadGeneratorSource.SOURCE_PARAM:
      return new LoadGeneratorSource(properties);
    default:
      System.err.println("Invalid source: '" + sourceType + "'. Valid sources: " + LoadGeneratorSource.SOURCE_PARAM);
      System.exit(1);
      return null;
    }
  }
  
  private static MigrationTarget getMigrationTarget() throws IOException {
    return new DefaultMigrationTarget(properties);
  }
  
  private static void showHelpAndExit(int exitCode) {
    System.out.println("Usage: java -jar <migration-jar.jar> migration.properties");
    System.exit(exitCode);
  }

}
