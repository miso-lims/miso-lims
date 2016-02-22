package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;

public interface LabService {
  
  /**
   * Retrieves a single Lab by ID
   * 
   * @param id ID of the Lab to retrieve
   * @return
   * @throws IOException
   */
  Lab get(Long id) throws IOException;
  
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
  
  /**
   * @return a Set of all Labs
   * @throws IOException
   */
  Set<Lab> getAll() throws IOException;
  
  /**
   * Deletes an existing Lab by ID
   * 
   * @param labId ID of the Lab to delete
   * @throws IOException
   */
  void delete(Long labId) throws IOException;
  
}
