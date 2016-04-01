package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;

public interface SampleTissueDao {
  
  /**
   * @return a list of all SampleTissues
   */
  List<SampleTissue> getSampleTissue();
  
  /**
   * Retrieves a single SampleTissue by ID
   * 
   * @param id ID of the SampleTissue to retrieve
   * @return
   */
  SampleTissue getSampleTissue(Long id);
  
  /**
   * Save a new SampleTissue
   * 
   * @param sampleTissue the SampleTissue to save 
   * @return the ID of the newly-saved SampleTissue
   */
  Long addSampleTissue(SampleTissue sampleTissue);
  
  /**
   * Delete an existing SampleTissue
   * 
   * @param sampleTissue the SampleTissue to delete
   */
  void deleteSampleTissue(SampleTissue sampleTissue);
  
  /**
   * Save a modified SampleTissue
   * 
   * @param sampleTissue the SampleTissue to save
   */
  void update(SampleTissue sampleTissue);
  
}
