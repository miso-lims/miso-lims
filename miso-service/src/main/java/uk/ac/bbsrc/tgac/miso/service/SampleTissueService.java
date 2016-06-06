package uk.ac.bbsrc.tgac.miso.service;

import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;

public interface SampleTissueService {

  SampleTissue get(Long sampleTissueId);

  Set<SampleTissue> getAll();

  void delete(Long sampleTissueId);
  
  /**
   * copies all the editable properties from one SampleTissue instance to another
   * 
   * @param target the persisted SampleTissue to copy changes into
   * @param source the modified SampleTissue to copy changes from
   */
  public void applyChanges(SampleTissue target, SampleTissue source);

}
