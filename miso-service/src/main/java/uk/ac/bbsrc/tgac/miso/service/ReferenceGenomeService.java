package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface ReferenceGenomeService extends DeleterService<ReferenceGenome>, SaveService<ReferenceGenome> {

  public Collection<ReferenceGenome> list() throws IOException;

}