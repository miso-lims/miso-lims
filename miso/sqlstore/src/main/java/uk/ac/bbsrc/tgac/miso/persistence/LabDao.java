package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;

public interface LabDao {
  
  /**
   * @return a list of all Labs
   */
  List<Lab> getLabs();
  
  /**
   * Retrieve a single Lab by ID
   * 
   * @param id ID of the Lab to retrieve
   * @return the Lab, or null if no Lab exists with the specified ID
   */
  Lab getLab(Long id);
  
  /**
   * Save a new Lab
   * 
   * @param lab the Lab to save
   * @return the ID of the newly-saved Lab
   */
  Long addLab(Lab lab);
  
  /**
   * Delete an existing Lab
   * 
   * @param lab the Lab to delete
   */
  void deleteLab(Lab lab);
  
  /**
   * Save a modified Lab
   * 
   * @param lab the Lab to save
   */
  void update(Lab lab);
  
}
