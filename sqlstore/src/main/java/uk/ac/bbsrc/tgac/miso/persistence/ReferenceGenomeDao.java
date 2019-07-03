package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;

public interface ReferenceGenomeDao {

  public List<ReferenceGenome> list();

  public ReferenceGenome get(long id);

  public ReferenceGenome getByAlias(String alias);

  public long create(ReferenceGenome reference);

  public long update(ReferenceGenome reference);

  public long getUsage(ReferenceGenome reference);

}
