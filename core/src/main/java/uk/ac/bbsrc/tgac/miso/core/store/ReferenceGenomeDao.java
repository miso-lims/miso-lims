package uk.ac.bbsrc.tgac.miso.core.store;

import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;

public interface ReferenceGenomeDao {

  public Collection<ReferenceGenome> list();

  public ReferenceGenome get(long id);

  public ReferenceGenome getByAlias(String alias);

  public long create(ReferenceGenome reference);

  public long update(ReferenceGenome reference);

  public long getUsage(ReferenceGenome reference);

}
