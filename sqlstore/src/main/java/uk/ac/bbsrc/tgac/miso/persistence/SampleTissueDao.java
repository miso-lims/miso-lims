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
   * Delete an existing SampleTissue
   * 
   * @param sampleTissue the SampleTissue to delete
   */
  void deleteSampleTissue(SampleTissue sampleTissue);
  
}
