package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;

public interface LabService extends DeleterService<Lab>, ListService<Lab> {
  
  /**
   * Saves a new Lab
   * 
   * @param lab 
   * @param instituteId of the associated Institute
   * @return the ID of the newly-saved Lab
   * @throws IOException
   */
  Long create(Lab lab, Long instituteId) throws IOException;
  
  /**
   * Updates an existing Lab
   * 
   * @param lab
   * @param instituteId of the associated Institute
   * @throws IOException
   */
  void update(Lab lab, Long instituteId) throws IOException;
  
}
