package uk.ac.bbsrc.tgac.miso.migration.source;

import uk.ac.bbsrc.tgac.miso.migration.MigrationData;

public interface MigrationSource {
  
  /**
   * @return all data to be migrated. Implementors should not include IDs, but should fully construct
   * the object graph. Consumers are expected to generate all IDs according to the graph
   */
  public MigrationData getMigrationData();
  
}
