package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;

public interface ReferenceGenomeService {

  public Collection<ReferenceGenome> listAllReferenceGenomeTypes() throws IOException;

  public ReferenceGenome get(Long id) throws IOException;

}