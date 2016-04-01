package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.dto.SampleTissueDto;

public interface SampleTissueService {

  SampleTissue get(Long sampleTissueId);

  Long create(SampleTissue sampleTissue) throws IOException;

  void update(SampleTissue sampleTissue) throws IOException;

  Set<SampleTissue> getAll();

  void delete(Long sampleTissueId);

  SampleTissue to(SampleTissueDto sampleTissueDto) throws IOException;
  
  /**
   * copies all the editable properties from one SampleTissue instance to another
   * 
   * @param target the persisted SampleTissue to copy changes into
   * @param source the modified SampleTissue to copy changes from
   */
  public void applyChanges(SampleTissue target, SampleTissue source);

}
