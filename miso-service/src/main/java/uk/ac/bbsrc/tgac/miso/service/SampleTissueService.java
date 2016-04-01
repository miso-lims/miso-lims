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

}
