package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Institute;

public interface InstituteService extends DeleterService<Institute>, ListService<Institute> {
  
  /**
   * Saves a new Institute
   * 
   * @param institute
   * @return the ID of the newly-saved Institute
   * @throws IOException
   */
  Long create(Institute institute) throws IOException;
  
  /**
   * Updates an existing institute
   * 
   * @param institute the institute with updates
   * @throws IOException
   */
  void update(Institute institute) throws IOException;
  
}
