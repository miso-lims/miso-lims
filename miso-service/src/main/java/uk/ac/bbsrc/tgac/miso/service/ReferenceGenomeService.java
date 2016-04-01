package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;

public interface ReferenceGenomeService {

  public Collection<ReferenceGenome> listAllReferenceGenomeTypes() throws IOException;
}