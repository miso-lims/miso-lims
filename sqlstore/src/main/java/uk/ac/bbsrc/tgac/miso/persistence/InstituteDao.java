package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Institute;

public interface InstituteDao {
  
  /**
   * @return a list of all Institutes
   */
  List<Institute> getInstitute();
  
  /**
   * Retrieve a single Institute by ID
   * 
   * @param id ID of the Institute to retrieve
   * @return
   */
  Institute getInstitute(Long id);
  
  /**
   * Save a new Institute
   * 
   * @param institute the institute to save
   * @return the ID of the newly-saved Institute
   */
  Long addInstitute(Institute institute);
  
  /**
   * Delete an Institute
   * 
   * @param institute the Institute to delete
   */
  void deleteInstitute(Institute institute);
  
  /**
   * Save a modified Institute
   * 
   * @param institute the Institute to save
   */
  void update(Institute institute);
  
}
