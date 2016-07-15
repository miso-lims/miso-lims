package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;

public interface SampleTissueService {

  SampleTissue get(Long sampleTissueId);

  Set<SampleTissue> getAll();

  void delete(Long sampleTissueId);

  void loadMembers(SampleTissue target, SampleTissue source) throws IOException;

  /**
   * copies all the editable properties from one SampleTissue instance to another
   * 
   * @param target
   *          the persisted SampleTissue to copy changes into
   * @param source
   *          the modified SampleTissue to copy changes from
   * @throws IOException
   */
  public void applyChanges(SampleTissue target, SampleTissue source) throws IOException;

}
