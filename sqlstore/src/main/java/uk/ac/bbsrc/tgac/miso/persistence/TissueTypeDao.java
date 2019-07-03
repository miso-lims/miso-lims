package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;

public interface TissueTypeDao {

  public List<TissueType> list();

  public TissueType get(Long id);

  public TissueType getByAlias(String alias);

  public Long create(TissueType tissueType);

  public long update(TissueType tissueType);

  public long getUsage(TissueType tissueType);

}
