package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Institute;

public interface InstituteService {
  
  /**
   * Retrieves a single Institute by ID
   * 
   * @param id ID of the Institute to retrieve
   * @return
   */
  Institute get(Long id);
  
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
  
  /**
   * @return a list of all Institutes
   */
  Set<Institute> getAll();
  
  /**
   * Delete an existing Institute by ID
   * 
   * @param instituteId ID of the Institute to delete
   */
  void delete(Long instituteId);
  
}
