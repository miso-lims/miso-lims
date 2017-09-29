package uk.ac.bbsrc.tgac.miso.migration.destination;

public interface MigrationCompleteListener {

  /**
   * Called by a MigrationTarget after migration completes, and is useful for verifying results
   * 
   * @param lookup provides complete sets of valid attribute values for the migration target. This is helpful for verification when
   *          a source value was modified due to being invalid in the target
   */
  public void onMigrationComplete(ValueTypeLookup lookup);

}