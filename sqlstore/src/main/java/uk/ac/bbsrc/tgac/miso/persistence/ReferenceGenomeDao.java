package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;

public interface ReferenceGenomeDao {

  Collection<ReferenceGenome> listAllReferenceGenomeTypes();

  ReferenceGenome getReferenceGenome(Long id);

}
