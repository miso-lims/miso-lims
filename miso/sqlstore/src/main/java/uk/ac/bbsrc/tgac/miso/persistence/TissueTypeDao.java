package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;

public interface TissueTypeDao {

  public List<TissueType> getTissueType();

  public TissueType getTissueType(Long id);

  public Long addTissueType(TissueType tissueType);

  public void deleteTissueType(TissueType tissueType);

  public void update(TissueType tissueType);

}
