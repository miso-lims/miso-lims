package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;

public interface TissueOriginDao {

  public List<TissueOrigin> getTissueOrigin();

  public TissueOrigin getTissueOrigin(Long id);

  public Long addTissueOrigin(TissueOrigin tissueOrigin);

  public void deleteTissueOrigin(TissueOrigin tissueOrigin);

  public void update(TissueOrigin tissueOrigin);

}
